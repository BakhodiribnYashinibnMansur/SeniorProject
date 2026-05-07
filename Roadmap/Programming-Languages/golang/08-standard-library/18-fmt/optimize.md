# Go fmt — Optimize

## Instructions

Each exercise presents inefficient or wasteful `fmt` usage. Identify
the issue, write an optimized version, and explain. Difficulty:
🟢 Easy, 🟡 Medium, 🔴 Hard.

---

## Exercise 1 🟢 — Sprintf for an Integer Conversion

**Problem**:
```go
func userKey(id int) string {
    return fmt.Sprintf("%d", id)
}
```

**Question**: How many allocations? How do you fix?

<details>
<summary>Solution</summary>

**Issue**: `Sprintf` calls go through the verb parser, the `pp`
state, and a final `string` allocation. ~2 allocations and ~45
ns/op.

**Optimization**:
```go
func userKey(id int) string {
    return strconv.Itoa(id)
}
```

**Benchmark** (Go 1.22 / amd64):
```
BenchmarkSprintf-8   30000000   45 ns/op   16 B/op   2 allocs/op
BenchmarkItoa-8     200000000    7 ns/op    0 B/op   0 allocs/op
```

`strconv.Itoa` has a fast path for small ints (caches strings for
0–99 in many implementations) and avoids the verb parser.

**Key insight**: Single-value conversions belong in `strconv`, not
`fmt`.
</details>

---

## Exercise 2 🟢 — Sprintf in a Tight Loop for a Key

**Problem**:
```go
for _, id := range ids {
    key := fmt.Sprintf("user:%d:profile", id)
    cache.Set(key, profileFor(id))
}
```

**Question**: How do you cut allocations?

<details>
<summary>Solution</summary>

**Issue**: Each iteration allocates the format buffer and the result
string. For 1k ids: 2k allocations.

**Optimization**:
```go
var b strings.Builder
b.Grow(20)
for _, id := range ids {
    b.Reset()
    b.WriteString("user:")
    b.WriteString(strconv.Itoa(id))
    b.WriteString(":profile")
    cache.Set(b.String(), profileFor(id))
}
```

`b.String()` does allocate the final string (because `Builder`
returns a copy from `[]byte`); but the buffer reuse halves the cost.
For 1k ids: ~1k allocations, ~3x faster overall.

**Better**: if `cache.Set` accepts `[]byte`, pass the bytes
directly and skip the final `String()`.

**Key insight**: `Builder` reuses a `[]byte`; `Sprintf` does not.
</details>

---

## Exercise 3 🟡 — Println in a Hot Loop

**Problem**:
```go
for _, item := range items {
    fmt.Println("processing:", item.Name)
}
```

**Question**: What's the cost? What's a good replacement?

<details>
<summary>Solution</summary>

**Issue**:
- `Println` packs the args into an `[]any` (one alloc per call).
- Each `string` arg formats via reflection.
- Output is line-buffered; the syscall is per call.
- Plus the `os.Stdout` mutex lock.

For 100k iterations: ~200k allocations, ~10ms of overhead.

**Optimizations** in order of preference:

1. Move the log out of the hot loop:
   ```go
   slog.Info("starting batch", "count", len(items))
   for _, item := range items {
       process(item)
   }
   slog.Info("batch done")
   ```

2. Use `slog` with a `JSONHandler` (allocation-free in Go 1.22+):
   ```go
   for _, item := range items {
       slog.Debug("processing", "name", item.Name)
   }
   ```

3. Manual `bufio.Writer` if you really want text:
   ```go
   bw := bufio.NewWriter(os.Stdout)
   defer bw.Flush()
   for _, item := range items {
       bw.WriteString("processing: ")
       bw.WriteString(item.Name)
       bw.WriteByte('\n')
   }
   ```

**Benchmark** (1M iterations):
```
BenchmarkPrintln-8       2  680ms/op  ~2M allocs
BenchmarkSlogDebug-8     6  170ms/op  0 allocs
BenchmarkBufioWrite-8   12   85ms/op  0 allocs
```

**Key insight**: Hot-loop logs need either zero-allocation
structured logging or no logging at all.
</details>

---

## Exercise 4 🟡 — Sprintf for a Composite Key

**Problem**:
```go
func tenantKey(tenant, region, kind string) string {
    return fmt.Sprintf("%s/%s/%s", tenant, region, kind)
}
```

**Question**: Is this worth optimizing?

<details>
<summary>Solution</summary>

**Profile first**. If the call is rare, leave it. If
`runtime/pprof` shows it in the top, consider:

```go
func tenantKey(tenant, region, kind string) string {
    n := len(tenant) + len(region) + len(kind) + 2
    var b strings.Builder
    b.Grow(n)
    b.WriteString(tenant)
    b.WriteByte('/')
    b.WriteString(region)
    b.WriteByte('/')
    b.WriteString(kind)
    return b.String()
}
```

**Benchmark**:
```
BenchmarkSprintf-8    20000000   80 ns/op   32 B/op   2 allocs/op
BenchmarkBuilder-8    50000000   24 ns/op   16 B/op   1 allocs/op
```

The Builder version is ~3x faster and one fewer allocation. For
keys built per request, the savings add up.

**Pragmatism**: a one-off "build a key once at startup" use of
Sprintf is fine. Per-request paths benefit from Builder.

**Key insight**: `Grow(n)` upfront avoids buffer resize
re-allocations.
</details>

---

## Exercise 5 🟡 — Errorf in a Cold Path

**Problem**:
```go
if err != nil {
    return fmt.Errorf("call %s: %w", op, err)
}
```

**Question**: Is this worth optimizing?

<details>
<summary>Solution</summary>

**No.** Error paths are cold. The cost (~200 ns and 2 allocations)
is invisible compared to the work that triggered the error.

`Errorf` with `%w` is the canonical way to wrap. Optimizing it
would mean writing a custom error type, losing readability and
breaking `errors.Is` for casual readers.

**Anti-optimization**:
```go
// DON'T
return &myErr{op: op, err: err} // saves 30 ns, costs maintainability
```

**Key insight**: Optimize hot paths only. Errors are by definition
cold.
</details>

---

## Exercise 6 🟡 — Sprintf for a Float in a Hot Path

**Problem**:
```go
func price(cents int64) string {
    return fmt.Sprintf("%.2f", float64(cents)/100.0)
}
```

**Question**: Profile shows this in the top. What do you do?

<details>
<summary>Solution</summary>

**Issue**: `Sprintf("%.2f", ...)` parses the verb, calls
`strconv.AppendFloat` internally, allocates the result string. ~70
ns.

**Optimization** — direct `strconv` and integer math:
```go
func price(cents int64) string {
    var b [16]byte
    n := len(b)
    n--; b[n] = '0' + byte(cents%10)
    cents /= 10
    n--; b[n] = '0' + byte(cents%10)
    cents /= 10
    n--; b[n] = '.'
    if cents == 0 {
        n--; b[n] = '0'
    }
    for cents > 0 {
        n--; b[n] = '0' + byte(cents%10)
        cents /= 10
    }
    return string(b[n:])
}
```

This is ugly but ~5x faster — about 14 ns and zero
intermediate-buffer allocations.

**Benchmark**:
```
BenchmarkSprintf-8   20000000   75 ns/op   24 B/op   2 allocs/op
BenchmarkManual-8   100000000   14 ns/op    8 B/op   1 allocs/op
```

**Trade-off**: Manual code is brittle. Use only when profile demands.

**Key insight**: Currency is integer. Avoid `float64` if you can.
</details>

---

## Exercise 7 🔴 — pp Pool Awareness

**Problem**: A high-throughput service (50k Sprintf/sec) shows
`fmt.newPrinter` allocations in the heap profile. Why? It should be
pooled.

<details>
<summary>Solution</summary>

**Issue**: The `sync.Pool` is per-P (per OS thread). Under bursty
load, threads grab a `pp` and the pool warms; under steady load,
the pool stays warm too. **But**: the pool can drop entries during
GC. After a GC, the next call allocates a `pp` again.

Also: if your `Sprintf` calls produce buffers > 64 KiB, the pool
deliberately drops them (see `pp.free`):

```go
func (p *pp) free() {
    if cap(p.buf) > 64<<10 {
        return // don't pool huge buffers
    }
    ...
    ppFree.Put(p)
}
```

So one large format dump effectively "loses" a pp.

**Optimization** for steady-state: increase GOGC so GC is less
frequent, or move the hot path off `fmt` entirely (use Builder).

**Verification**:
```bash
go test -bench BenchmarkSprintf -benchmem -count=10
go test -bench BenchmarkSprintf -benchmem -gcflags="-m=2"
GODEBUG=allocfreetrace=1 ./yourbinary 2>&1 | grep "fmt.newPrinter"
```

**Key insight**: Pools mitigate but don't eliminate allocation. For
true zero-alloc, format directly into a caller-provided buffer.
</details>

---

## Exercise 8 🔴 — Allocation From Interface Boxing

**Problem**:
```go
fmt.Printf("count=%d\n", n) // n is int
```

**Question**: Is there a hidden allocation?

<details>
<summary>Solution</summary>

**Yes — sometimes.** `Printf("...", n)` packs `n` into an `any`
(`interface{}`). Boxing an `int` into an interface is normally an
allocation, but Go has a fast path: small integers (`-256` to `255`
on most builds, varying with version) are interned and don't
allocate.

For `n` outside that range:
```go
fmt.Printf("count=%d\n", 1_000_000) // 1 alloc for the int box
```

For `n` inside:
```go
fmt.Printf("count=%d\n", 5) // 0 allocs for the box
```

**Optimization** — avoid the box entirely:
```go
buf = strconv.AppendInt(buf, int64(n), 10) // 0 allocs
buf = append(buf, '\n')
os.Stdout.Write(buf)
```

**Verification**:
```bash
go test -bench BenchmarkPrintfInt -benchmem
# BenchmarkPrintfInt-8   ...   45 ns/op   8 B/op   1 allocs/op  (large int)
# BenchmarkPrintfInt-8   ...   42 ns/op   0 B/op   0 allocs/op  (small int)
```

**Key insight**: The variadic `...any` is the hidden cost in
benchmarks of `Printf`-likes. Type-specific functions
(`io.WriteString`, `bufio.WriteByte`, `strconv.AppendInt`) avoid
it.
</details>

---

## Exercise 9 🔴 — Stringer Recursion at Profile Top

**Problem**: pprof shows `fmt.(*pp).doPrintf` calling
`(*pp).handleMethods` calling `Stringer.String` calling
`fmt.Sprintf` calling `(*pp).doPrintf`. Stack depth: 200.

<details>
<summary>Solution</summary>

**Issue**: A `String()` method calls `fmt.Sprintf("%v", t)` on its
own type. Each call recurses; the stack grows until the goroutine
runs out of memory or the program panics.

**Identification**:
```bash
go test -run XXX -cpuprofile cpu.out ./...
go tool pprof -list 'String' cpu.out
# look for self-referential frames
```

**Fix**:
```go
// Bad
func (t T) String() string { return fmt.Sprintf("%v", t) }

// Good — explicit verbs
func (t T) String() string { return fmt.Sprintf("T{X:%d}", t.X) }

// Good — alias type
func (t T) String() string {
    type alias T
    return fmt.Sprintf("%+v", alias(t))
}
```

**Key insight**: Recursive `Stringer` is the most common
infinite-loop bug in `fmt`-using code; `vet` does NOT catch it.
Add a unit test that calls `String()` and asserts it returns.
</details>

---

## Exercise 10 🔴 — Aligning a Million Rows

**Problem**: A CLI prints a 1M-row table with width-aligned columns
using `fmt.Printf("%-30s %10d\n", name, count)`. It takes 8s.

<details>
<summary>Solution</summary>

**Profile**:
- 80% in `fmt.(*pp).doPrintf`.
- 15% in `os.Stdout.Write`.

**Optimization 1** — buffer stdout:
```go
bw := bufio.NewWriterSize(os.Stdout, 1<<20)
defer bw.Flush()
for _, r := range rows {
    fmt.Fprintf(bw, "%-30s %10d\n", r.name, r.count)
}
```

This brings it to ~3s — the syscall cost dominated.

**Optimization 2** — avoid `fmt`:
```go
bw := bufio.NewWriterSize(os.Stdout, 1<<20)
defer bw.Flush()
var lineBuf [64]byte
for _, r := range rows {
    line := lineBuf[:0]
    line = append(line, r.name...)
    for i := len(r.name); i < 30; i++ {
        line = append(line, ' ')
    }
    line = append(line, ' ')
    line = strconv.AppendInt(line, int64(r.count), 10)
    line = append(line, '\n')
    bw.Write(line)
}
```

This brings it to ~1s — `fmt` is gone, `strconv` and direct bytes
do the work.

**Key insight**: Tabular CLIs benefit from `bufio.Writer` first;
removing `fmt` is the second step when you really care.
</details>

---

## Exercise 11 🟡 — strings.Builder vs bytes.Buffer

**Problem**: A function builds a big string. Author chose
`bytes.Buffer`, calls `b.String()` at the end.

```go
func render() string {
    var b bytes.Buffer
    fmt.Fprintf(&b, "header: %s\n", title)
    for _, line := range body {
        fmt.Fprintf(&b, "  %s\n", line)
    }
    return b.String()
}
```

**Question**: Worth switching to `strings.Builder`?

<details>
<summary>Solution</summary>

**Issue**: `bytes.Buffer.String()` allocates a new string from the
buffer's bytes. `strings.Builder.String()` does the same conceptually
but works directly on `[]byte` that becomes the string with no copy.

**Optimization**:
```go
func render() string {
    var b strings.Builder
    fmt.Fprintf(&b, "header: %s\n", title)
    for _, line := range body {
        fmt.Fprintf(&b, "  %s\n", line)
    }
    return b.String()
}
```

**Benchmark** for a 100-line render:
```
BenchmarkBytesBuffer-8   1000000  1300 ns/op  2400 B/op  4 allocs/op
BenchmarkBuilder-8       1500000   900 ns/op  1024 B/op  2 allocs/op
```

`strings.Builder` is ~30% faster for the final string conversion.

**Caveat**: `bytes.Buffer` is more flexible — it implements
`io.Reader` too. If you need to read back what you wrote, stick
with it.

**Key insight**: For "build a string then return it", `Builder` is
the right tool.
</details>

---

## Exercise 12 🔴 — PGO Inlining Through Closures

**Problem**: A logger wraps `fmt.Fprintf`:
```go
func (l *Logger) Logf(format string, args ...any) {
    if !l.enabled { return }
    fmt.Fprintf(l.w, format, args...)
}
```

Profile shows the call site is hot. PGO is enabled. Logger is
disabled in production.

<details>
<summary>Solution</summary>

**Issue**: Even with `enabled = false`, the variadic `args...any`
still boxes each argument before the function returns. Each call
costs an interface-conversion allocation per arg.

**Optimization** — early bail at the call site:
```go
if l.enabled {
    fmt.Fprintf(l.w, "user=%d action=%s\n", uid, action)
}
```

The `if` short-circuits the boxing. Inlining doesn't help because
the boxing happens at the call, not inside the function.

**With PGO**: the `if l.enabled { return }` check inlines, but the
arg boxing in the caller still happens. PGO can sometimes
devirtualize / inline the Fprintf path when the writer is always
the same concrete type (good), but boxing remains.

**Better** — return a closure to defer the formatting:
```go
type lazyMsg func() string

func (l *Logger) LogLazy(get lazyMsg) {
    if !l.enabled { return }
    fmt.Fprintln(l.w, get())
}

l.LogLazy(func() string { return fmt.Sprintf("user=%d", uid) })
```

The closure is created cheaply (one alloc); the formatting only
runs if `enabled`. This is the
[`zap.SugaredLogger`](https://pkg.go.dev/go.uber.org/zap)
optimization in spirit.

**Even better** — use leveled APIs (`Info`, `Debug`) that check the
level on the caller side, like `slog`:
```go
if logger.Enabled(ctx, slog.LevelDebug) {
    logger.Debug("user", "id", uid)
}
```

**Key insight**: Variadic `...any` boxes args at the call site, not
inside the function. Move the level check before the call.
</details>

---

## Exercise 13 🔴 — Format Method Allocation

**Problem**: A custom `Format` method:
```go
func (e *AppErr) Format(s fmt.State, verb rune) {
    fmt.Fprintf(s, "%s: %s", e.Op, e.Err.Error())
}
```

Profile shows it allocating ~40 B per call.

<details>
<summary>Solution</summary>

**Issue**: `Fprintf` re-enters the formatter, parses the verbs, and
allocates internally. For a hot error type, this is overkill.

**Optimization** — write directly through `s`:
```go
func (e *AppErr) Format(s fmt.State, verb rune) {
    s.Write([]byte(e.Op))
    s.Write([]byte(": "))
    s.Write([]byte(e.Err.Error()))
}
```

`s.Write([]byte(string))` is a known compiler-optimized conversion
(no heap alloc for the slice header in Go 1.20+).

**Even better** — avoid `[]byte(string)` conversions by precomputing:
```go
var sep = []byte(": ")

func (e *AppErr) Format(s fmt.State, verb rune) {
    io.WriteString(s, e.Op)
    s.Write(sep)
    io.WriteString(s, e.Err.Error())
}
```

`io.WriteString` uses the `WriteString` method if `s` implements it
(it does in `fmt.pp`).

**Benchmark**:
```
BenchmarkFormatFprintf-8    10000000  150 ns/op  48 B/op  3 allocs/op
BenchmarkFormatDirect-8     30000000   45 ns/op   0 B/op  0 allocs/op
```

**Key insight**: `Format` runs on the hot path of every `%v` call.
Make it allocation-free.
</details>

---

## Exercise 14 🔴 — Reusable Sprintf via byte slice

**Problem**: A serializer calls `fmt.Sprintf` for each field,
producing many short strings that immediately go into a `[]byte`.

```go
out := []byte("[")
for i, x := range xs {
    if i > 0 { out = append(out, ',') }
    out = append(out, fmt.Sprintf("%d", x)...)
}
out = append(out, ']')
```

<details>
<summary>Solution</summary>

**Issue**: Each `fmt.Sprintf("%d", x)` allocates a string, only to
have it appended to `out`. Round-trip from `[]byte` → `string` →
`[]byte`.

**Optimization** — `strconv.AppendInt`:
```go
out := []byte("[")
for i, x := range xs {
    if i > 0 { out = append(out, ',') }
    out = strconv.AppendInt(out, int64(x), 10)
}
out = append(out, ']')
```

`AppendInt` writes directly into `out`. Zero intermediate
allocations.

**Benchmark** (10k ints):
```
BenchmarkSprintf-8     5000   320µs/op  140 KB/op  10000 allocs
BenchmarkAppend-8     50000    25µs/op    8 KB/op      1 alloc
```

12x faster, 10000x fewer allocations.

**Key insight**: Anywhere you `append([]byte, fmt.Sprintf(...)...)`
you really want `strconv.AppendXxx`.
</details>

---

## Exercise 15 🟡 — Println vs Printf for a Constant

**Problem**:
```go
fmt.Printf("ready\n")
```

**Question**: Anything wrong?

<details>
<summary>Solution</summary>

**Issue**: `Printf` parses the format string for verbs even when it
contains none. Slightly slower than `Println`.

**Optimization**:
```go
fmt.Println("ready")
```

Or, for the absolute fastest:
```go
io.WriteString(os.Stdout, "ready\n")
```

`io.WriteString` does no formatting at all.

**Benchmark**:
```
BenchmarkPrintf-8       30000000   55 ns/op
BenchmarkPrintln-8      40000000   42 ns/op
BenchmarkWriteString-8 100000000   12 ns/op
```

**Key insight**: `Printf` parses the format string every call;
`Println` and `WriteString` do not. For a constant message, skip
the parse.
</details>

---

## Summary: Optimization Hierarchy

When `fmt` shows up in a profile, walk this list:

1. **Profile first.** Don't optimize on suspicion.
2. **Single-value conversion?** Switch to `strconv.Itoa`,
   `strconv.FormatFloat`, etc.
3. **Building a string?** Use `strings.Builder` with `Grow`.
4. **Building bytes?** Use `strconv.AppendInt` / `append`.
5. **Writing many lines?** Wrap stdout with `bufio.Writer`.
6. **Service log?** Switch to `slog` with a structured handler.
7. **Hot Format method?** Write directly through the `State`,
   skip nested `Fprintf`.
8. **Stringer recursion?** Fix immediately — it's a bug, not a
   perf issue.
9. **Variadic boxing?** Move level checks to the caller, or use
   typed APIs.
10. **Still hot?** Drop down to a hand-rolled `[]byte` builder.

`fmt` is correct, readable, and slow. Default to it; replace it
where benchmarks demand.
