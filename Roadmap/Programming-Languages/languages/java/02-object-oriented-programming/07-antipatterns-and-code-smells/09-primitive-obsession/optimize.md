# Primitive Obsession — Optimize

The honest cost of choosing typed value objects over raw primitives, what the JVM already does for you, and where (and only where) primitives are still the right answer.

---

## 1. The real overhead of a record

A Java record holds:

- An object header (12 or 16 bytes depending on compressed oops).
- The declared fields, aligned to 8 bytes.
- A reference, when stored in another object or a collection.

`record UserId(long value) {}` therefore takes 24 bytes on the heap (16-byte header + 8-byte long), and an extra 4–8 bytes per *reference* to it. A primitive `long` is 8 bytes inline — no reference, no header.

In raw numbers, the typed version is 3–4x the memory cost. In practice, this cost vanishes in three ways:

1. **Escape Analysis (EA) + scalar replacement** — the JIT eliminates the allocation entirely for non-escaping records.
2. **GC throughput** — short-lived records die in the young generation; modern GCs (G1, ZGC, Shenandoah) handle them efficiently.
3. **Cache locality of references** — irrelevant when the record holds one or two scalars; relevant in tight numeric loops.

---

## 2. Escape Analysis in detail

EA proves that an object's reference does not escape the method that allocates it. If the proof succeeds, the JIT performs **scalar replacement**: the object is decomposed into its fields, which live in registers or on the stack. There is no heap allocation, no GC pressure, no indirection.

```java
public Money totalWithTax(Money base, BigDecimal taxRate) {
    Money tax = base.multiply(taxRate); // candidate for scalar replacement
    return base.add(tax);
}
```

`tax` here is allocated, used once, and then absorbed into the return value. C2 detects this and scalar-replaces `tax`. The `multiply` and `add` calls inline, the `BigDecimal` work dominates the cost, and the record wrapper costs zero.

### When EA fails

EA fails (and the record allocates) when the record:

- Is stored in a field of another object.
- Is returned from the method (unless the caller also inlines).
- Is passed to a non-inlined method.
- Is stored in a `List`, `Map`, or array.
- Crosses a virtual call that the JIT cannot devirtualise.

Use `-XX:+UnlockDiagnosticVMOptions -XX:+PrintEscapeAnalysis` to see what EA discovered. JFR's `jdk.ObjectAllocationSample` event reveals which sites actually allocate at runtime.

---

## 3. Project Valhalla — the long-term answer

JEP 401 (Value Classes and Objects, Preview) introduces **identity-less value classes**. A value class:

- Has no `==`-as-reference semantics.
- Cannot be synchronised on.
- Cannot be null in a `value`-typed field (with the strictest variant).
- Can be flattened by the JVM into containers and method frames.

```java
public value class UserId {
    private final long value;
    public UserId(long value) { this.value = value; }
    public long value() { return value; }
}
```

A `List<UserId>` backed by an array becomes a flat layout — no per-element header, no indirection. A `UserId` in a method parameter is passed in registers like a `long`. The runtime cost equals raw `long`; the only thing that survives is the type safety.

Migration from `record` to `value record` is a one-keyword change. **Designing with records today is the migration path**.

---

## 4. Autoboxing — the silent allocator

Boxed primitives (`Long`, `Integer`, `Boolean`) appear "for free" via autoboxing:

```java
Map<Long, User> usersById = ...;
usersById.get(42L); // autoboxes to Long
```

Each `42L` boxed is a `Long` allocation (16 bytes + 8 bytes payload). `Integer` caches values in `-128..127`; `Long` does the same — anything outside is a fresh allocation. In hot paths over wide ID ranges this is a measurable cost.

The pragmatic fix: typed IDs hide this from the caller. Inside `record UserId(long value) {}`, the field is a primitive `long`, not a boxed `Long`. Equality and hashing use the primitive directly. The boxing penalty disappears.

```java
public record UserId(long value) {} // value is a primitive long, no boxing
```

If you need `Map<UserId, User>`, the *key* is the record (still a heap object), but the *long inside* it is unboxed. Eclipse Collections, Koloboke, or fastutil provide primitive-keyed maps for the tightest cases.

---

## 5. The numbers — when it matters

A JMH benchmark of "lookup by ID, 100 million iterations, no GC" gives a representative profile:

| Variant | ns/op | Allocations/op |
|---|---|---|
| `long` primitive | 11.2 | 0 B |
| `record UserId(long)` — EA scalar-replaces | 11.8 | 0 B |
| `record UserId(long)` — EA fails (escapes) | 19.4 | 24 B |
| `Long` boxed (autoboxing on `Map.get`) | 22.7 | 32 B |
| Future Valhalla `value class UserId` | ~11.2 | 0 B |

Takeaways:

- When EA succeeds, records cost essentially nothing.
- When EA fails, records cost about 2x the primitive — still cheaper than autoboxing.
- Valhalla closes the gap entirely.

For business logic running at thousands of QPS, the difference is invisible. For inner loops processing millions of elements per second, profile first.

---

## 6. Where primitives still win

There are three legitimate uses of raw primitives:

### 6.1 Numerical kernels

Image processing, FFT, matrix multiplication, ML inference. These loops cross hundreds of millions of elements per call; allocation and boxing penalties dominate. Use primitive arrays (`int[]`, `double[]`) and primitive locals. Wrap the kernel in a typed API and keep the typed/untyped boundary at the kernel entry.

### 6.2 Low-level protocol parsing

When parsing a wire protocol byte-by-byte, the loop body sees raw `byte` and `int`. Wrap the parser entry point with typed inputs (`ByteBuffer buf` → `ParsedMessage`); the loop interior stays primitive.

### 6.3 Truly identity-free counters

A loop index, an array length, an iteration count. These represent no domain concept and wrapping them in a type adds noise.

---

## 7. Avoid these anti-optimisations

- **Caching value-object instances in a static map** — adds memory pressure, hurts cache locality, and prevents EA. Just construct.
- **`equals` short-circuit on `this == other`** — already done by the synthesised record `equals`. Don't override.
- **Using `String.intern()` for value-object backing strings** — `intern` table is global, slow, and rarely worth it. Records compare by value; interning provides no algorithmic benefit.
- **Custom `hashCode` with magic primes** — the synthesised record `hashCode` is sufficient. Diverging from it requires a benchmark to justify.

---

## 8. Quick rules

1. **Default to records.** Reach for primitives only with a profile in hand.
2. **Trust escape analysis** for non-escaping records. Verify with JFR.
3. **Keep typed APIs at boundaries**; primitives at inner loops only if measurement justifies it.
4. **Plan for Valhalla** — write records, switch to value classes when JEP 401 ships.
5. **Avoid autoboxing in hot maps** — use primitive-keyed maps from Eclipse Collections / fastutil.
6. **Profile before optimising back.** The 2x cost is irrelevant at business-logic QPS; bug cost is permanent.
7. **Single hot path, single optimisation.** Don't undo typed APIs across the whole codebase to "speed it up"; isolate.

---

## 9. Measuring in production

- **JFR allocation profiling:** `-XX:StartFlightRecording=settings=profile,filename=app.jfr`. Open in JDK Mission Control; sort by allocation size. Records that allocate frequently surface immediately.
- **Async-profiler in `alloc` mode:** flame graphs of allocation hot paths. Pinpoints the call site, not just the type.
- **`-XX:+PrintInlining`:** confirms whether the method holding the record was inlined. Inlined methods are where EA applies.
- **`-XX:+TraceDeoptimization`:** if records suddenly become slower, a deopt may have undone scalar replacement; this surfaces it.

---

## 10. Worked optimisation — when records *do* cost too much

Profile shows that `record Pixel(int r, int g, int b)` allocates 200 million times per image render and dominates the CPU.

**Wrong fix:** delete the record and pass three `int`s around — loses type safety across the whole rendering pipeline.

**Right fix:** keep `Pixel` as the public API of the renderer; internally, the hot loop uses three `int[]` arrays (one per channel) and the `Pixel` is constructed only at the boundary (when reading from / writing to disk). The typed API is preserved; the inner loop is primitive.

```java
public final class Image {
    private final int[] r, g, b;
    public Pixel pixel(int x, int y) { return new Pixel(r[idx(x,y)], g[idx(x,y)], b[idx(x,y)]); }
    public void setPixel(int x, int y, Pixel p) { r[idx] = p.r(); g[idx] = p.g(); b[idx] = p.b(); }
    // hot inner loops operate on r/g/b arrays directly
}
```

This is the canonical pattern: **typed outside, primitive inside, conversion at the wall**. It is the same pattern as the controller/service boundary, scaled to nanoseconds.

---

**Memorize this:** Records are almost always free. When they aren't, the JIT tells you, JFR proves it, and you can keep the typed API while flattening the hot loop. Optimising by removing types is a last resort; optimising by isolating the hot loop is the first move.
