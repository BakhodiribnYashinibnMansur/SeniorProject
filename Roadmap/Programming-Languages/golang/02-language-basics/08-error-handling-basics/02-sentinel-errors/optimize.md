# Go Sentinel Errors — Optimize

## Instructions

Each exercise probes a sentinel-related cost: comparison, allocation, wrapping depth, classifier dispatch. Identify the cost, write a faster or leaner version where it matters, and explain when the optimization is worth doing. Difficulty: Easy, Medium, Hard.

---

## Exercise 1 (Easy) — `==` vs `errors.Is` cost

**Problem**: A hot path checks for `io.EOF` once per record:

```go
for {
    rec, err := next()
    if err == io.EOF {
        return nil
    }
    if err != nil {
        return err
    }
    process(rec)
}
```

**Question**: Is `errors.Is` slower? When is the difference worth caring about?

<details>
<summary>Solution</summary>

`==` compiles to a single pointer comparison plus an interface-table check. About 1 ns.

`errors.Is` walks the unwrap chain. For a chain of length 1 (no wrapping), it does:
1. A `==` comparison.
2. An assertion for the `Is(error) bool` method (negative).
3. An assertion for `Unwrap() error` (negative).
4. Returns false (or true at step 1).

Roughly 5-10 ns on modern hardware.

The 5x difference is real but absolute cost is negligible until you do >10⁸ checks/sec. In a typical record reader processing 10⁶ records/sec, the entire `errors.Is` budget is 5-10 ms — invisible.

**When to prefer `==`**: only when you control the producer, you know it never wraps, and benchmarks demonstrate a hot-path issue. Otherwise prefer `errors.Is` for correctness against future wrapping.

**Benchmark sketch**:
```go
func BenchmarkEqEOF(b *testing.B) {
    err := io.EOF
    for i := 0; i < b.N; i++ {
        if err == io.EOF { _ = i }
    }
}

func BenchmarkIsEOF(b *testing.B) {
    err := io.EOF
    for i := 0; i < b.N; i++ {
        if errors.Is(err, io.EOF) { _ = i }
    }
}
```

Typical (Go 1.22, amd64):
- BenchmarkEqEOF: 0.6 ns/op
- BenchmarkIsEOF: 4.5 ns/op

**Key insight**: `==` is faster but fragile. `errors.Is` is the right default; only optimise away from it when profiling demands it.
</details>

---

## Exercise 2 (Easy) — Sentinel allocation: once vs every call

**Problem**: A package exports a "not found" failure two ways:

```go
// Way A — single allocation at init
var ErrNotFound = errors.New("not found")
func findA(k string) error {
    if !exists(k) { return ErrNotFound }
    return nil
}

// Way B — fresh allocation per call
func findB(k string) error {
    if !exists(k) { return errors.New("not found") }
    return nil
}
```

**Question**: How do they differ at runtime?

<details>
<summary>Solution</summary>

Way A:
- One allocation at package init.
- Every miss returns the same pointer.
- `errors.Is(err, ErrNotFound)` works.
- `err == ErrNotFound` works.

Way B:
- One allocation per miss (~24 bytes for the `*errorString`).
- Every miss returns a different pointer.
- `errors.Is(err, ?)` cannot match — there is no exported sentinel to target.
- `err == anything` is meaningless.

Way B is both slower (one alloc per miss in a hot path) and broken (no way for callers to detect it).

**Benchmark** (1M miss calls):
- Way A: 0 B/op, 0 allocs/op (excluding the init alloc).
- Way B: 24 B/op, 1 alloc/op.

For a service handling 10⁵ misses/sec, Way B costs ~2.4 MB/s of allocator pressure. Way A is free.

**Key insight**: Sentinels are a one-time allocation. Returning bare `errors.New` per call is both slower and useless for callers.
</details>

---

## Exercise 3 (Easy) — Wrapping cost in a hot path

**Problem**: A repository wraps every database error:

```go
func (r *Repo) Get(id int) (User, error) {
    err := r.db.QueryRow(...).Scan(...)
    if err != nil {
        return User{}, fmt.Errorf("repo get %d: %w", id, err)
    }
    return ..., nil
}
```

**Question**: When does the wrapping allocation matter?

<details>
<summary>Solution</summary>

`fmt.Errorf("...: %w", err)` allocates one `*fmt.wrapError` (~32 bytes) plus the message string (~30-50 bytes) per call. Total: 1 allocation + 1 string in the error path.

For a service where 99.9% of calls succeed, the allocation cost is dominated by successful paths — wrapping cost is invisible.

For a service where errors are common (a probe endpoint, a validator) and you're at 10⁵ errors/sec, the cost is ~3 MB/sec of error-path allocation. Worth measuring.

**Optimization** — wrap once at the boundary, not at every layer:
```go
// BAD: wrap at every layer
func a() error { return fmt.Errorf("a: %w", b()) }
func b() error { return fmt.Errorf("b: %w", c()) }
func c() error { return ErrFoo }

// GOOD: wrap once at the public API boundary
func Public() error {
    if err := internal(); err != nil {
        return fmt.Errorf("public: %w", err)
    }
    return nil
}
func internal() error { return ErrFoo } // bare
```

Three wraps cost 3 allocations and a 3-deep chain. One wrap costs 1 allocation and a 1-deep chain. `errors.Is` is slightly faster on shorter chains.

**Benchmark** (1M error returns):
- 3-layer wrap: ~150 ns/op, 96 B/op, 3 allocs/op
- 1-layer wrap: ~50 ns/op, 32 B/op, 1 alloc/op
- No wrap: ~1 ns/op, 0 allocs/op

**Key insight**: Wrap with intent. Each layer should add real context, not echo the caller's. Wrap at API boundaries where context becomes useful.
</details>

---

## Exercise 4 (Medium) — Chain depth and `errors.Is`

**Problem**: A service stacks five wraps before returning:

```go
return fmt.Errorf("a: %w",
    fmt.Errorf("b: %w",
        fmt.Errorf("c: %w",
            fmt.Errorf("d: %w",
                fmt.Errorf("e: %w", ErrFoo)))))
```

A caller does `errors.Is(err, ErrFoo)` 10⁶ times.

**Question**: What's the cost? How would you speed this up?

<details>
<summary>Solution</summary>

`errors.Is` walks the chain top-down. Five wraps + the sentinel = six levels. Each level: `==` against target, `Is` method check, `Unwrap` call. Roughly 25-30 ns per call total.

10⁶ calls = 25-30 ms total. Almost certainly invisible.

Optimisations:
1. **Hoist the check** — compute it once, store the boolean:
   ```go
   isFoo := errors.Is(err, ErrFoo)
   for i := 0; i < 1e6; i++ { if isFoo { ... } }
   ```
   The chain walk happens once.

2. **Reduce wrap depth** — most layers don't add useful context. Wrap once at the public API boundary.

3. **Cache classification** — if you always classify the same error in many places, classify it once and pass the category:
   ```go
   class := classify(err)
   for ... { switch class { ... } }
   ```

**Key insight**: Chain depth is rarely a real performance issue. It does affect readability (`err.Error()` produces a long colon-separated string). Aim for depth 2-3, not 5+.
</details>

---

## Exercise 5 (Medium) — Classifier dispatch

**Problem**: An HTTP handler maps errors to status codes:

```go
func status(err error) int {
    switch {
    case err == nil:                              return 200
    case errors.Is(err, ErrNotFound):             return 404
    case errors.Is(err, ErrConflict):             return 409
    case errors.Is(err, ErrInvalidInput):         return 400
    case errors.Is(err, ErrUnauthorized):         return 401
    case errors.Is(err, ErrForbidden):            return 403
    case errors.Is(err, context.Canceled):        return 499
    case errors.Is(err, context.DeadlineExceeded): return 504
    case errors.Is(err, ErrTransient):            return 503
    default:                                       return 500
    }
}
```

**Question**: 10 sentinels, called per request. Does it matter?

<details>
<summary>Solution</summary>

Worst case (default branch): 10 calls to `errors.Is`, each walking the chain. With chain depth 2 and 10 sentinels: ~50-100 ns per call.

For a service handling 10⁴ requests/sec, total classifier cost: ~1 ms/sec. Negligible.

**Optimisation 1** — order by frequency:
```go
switch {
case err == nil:                  return 200       // 99.9% of calls
case errors.Is(err, ErrNotFound): return 404       // most common error
... // less common cases later
}
```

The `err == nil` check is a single pointer comparison — short-circuit early.

**Optimisation 2** — class enum:
```go
type Class int
const (
    ClassOK Class = iota
    ClassNotFound
    ClassConflict
    ...
)

func classify(err error) Class { /* once */ }
func statusOf(c Class) int     { /* table lookup */ }

c := classify(err)
status := statusOf(c)
```

Classify once, dispatch via a small `switch` or array lookup.

**Optimisation 3** — `errors.As` for typed errors:
For services where errors are often structured (`*MyErr` containing an enum field), `errors.As` plus a tag dispatch can be faster than 10 `errors.Is` calls.

**Benchmark** (default branch, 10⁶ calls):
- Sequential `errors.Is`: ~80 ns/op
- Classify-once + table lookup: ~20 ns/op

**Key insight**: For sentinel-heavy classifiers, order by frequency and consider classifying once if the same error is mapped multiple times in a request.
</details>

---

## Exercise 6 (Medium) — Wrapping inside the sentinel definition

**Problem**: A maintainer adds context to a sentinel at declaration time:

```go
var ErrTransient = fmt.Errorf("svc: transient: %w", io.ErrUnexpectedEOF)
```

**Question**: What's the cost, and what's the actual problem?

<details>
<summary>Solution</summary>

Cost: an additional `*fmt.wrapError` allocation at package init. Negligible (~32 bytes once).

The actual problem is correctness, not performance. `errors.Is(svc.ErrTransient, io.ErrUnexpectedEOF)` is now true. Code that says "if it's `ErrUnexpectedEOF`, log it" gets triggered for any caller checking against `ErrTransient`.

**Fix** — declare with `errors.New`:
```go
var ErrTransient = errors.New("svc: transient")
```

If you want a structured relationship between sentinels (e.g., `ErrTransient` should match a category that includes `io.ErrUnexpectedEOF`), express it via an `Is` method on a structured error type, not by wrapping at declaration.

**Key insight**: Sentinels are identifiers. Wrapping at declaration gives them an unintended chain. Use `errors.New`, always.
</details>

---

## Exercise 7 (Medium) — Cache classification result per error

**Problem**: A retry decorator classifies the same error many times:

```go
for i := 0; i < maxRetries; i++ {
    err := fn()
    if err == nil { return nil }
    if !errors.Is(err, ErrTransient) {
        return err
    }
    backoff()
}
```

**Question**: Is there an issue?

<details>
<summary>Solution</summary>

`errors.Is` is called once per retry iteration. The error rarely changes between iterations of the same retry (it's typically a fresh error per `fn()` call). So caching the classification within one iteration buys nothing.

The check is once per failure: ~5 ns. With 10 retries × 10⁴ failed operations/sec: 500 ns × 10⁴ = 5 ms/sec total. Invisible.

**Real fix is qualitative**: ensure `ErrTransient` is correctly matched by all transient conditions. If it's matched via an `Is` method on a structured error type (DBError, NetError), the dispatch cost is the same and the design is cleaner.

```go
type DBError struct{ Code int }

func (e *DBError) Error() string { return "..." }

func (e *DBError) Is(target error) bool {
    if target == ErrTransient {
        return e.Code == 53300 || e.Code == 57P03
    }
    return false
}
```

Now any DBError carrying a transient code matches `ErrTransient` without explicit wrapping.

**Key insight**: For retry classifiers, structured-error `Is` methods are usually clearer than wrapping. Caching is rarely needed; the dispatch cost is small.
</details>

---

## Exercise 8 (Hard) — Sentinel comparison through a typed-nil

**Problem**:

```go
type myErr struct{}
func (m *myErr) Error() string { return "my" }

func mayFail() error {
    var e *myErr
    return e // typed nil
}

err := mayFail()
fmt.Println(err == nil)              // false!
fmt.Println(errors.Is(err, ErrFoo))  // false (correct)
```

**Question**: How does this interact with sentinel checks?

<details>
<summary>Solution</summary>

The typed-nil trap is well-known: an interface value with a non-nil type pointer and a nil data pointer is not `== nil`. Sentinel checks via `errors.Is` are immune (they compare against a target, not nil).

The trap is in the *outer* nil check:

```go
err := mayFail()
if err != nil { // matches; err is typed-nil
    if errors.Is(err, ErrFoo) {
        // Won't be true, but we already entered the error branch
    }
    return err // returns a typed-nil; caller has the same trap
}
```

**Fix** — never return a typed nil:
```go
func mayFail() error {
    var e *myErr
    if e == nil { return nil } // explicit conversion to untyped nil
    return e
}
```

Or design `mayFail` to return `error` directly:
```go
func mayFail() error {
    if condition { return &myErr{} }
    return nil
}
```

**Performance angle**: typed-nil errors are functionally a bug, not a perf issue. The comparison cost is unchanged; the bug just produces wrong control flow.

**Key insight**: Sentinel comparisons via `errors.Is` are robust to typed-nil. The bug surface is in `if err != nil` checks and in returning typed-nil to a generic `error`-typed caller.
</details>

---

## Exercise 9 (Hard) — Sentinel mass and import-cost

**Problem**: A code-generated client emits 200 sentinels:

```go
var (
    ErrCode001 = errors.New("api: 001")
    ErrCode002 = errors.New("api: 002")
    ...
    ErrCode200 = errors.New("api: 200")
)
```

**Question**: What's the cost, and is it worth caring about?

<details>
<summary>Solution</summary>

Allocation cost: 200 × ~24 bytes = ~4.8 KB at package init. One-time cost. Negligible.

Symbol-table cost: 200 exported names × ~40 bytes per symbol-table entry = ~8 KB in the binary. Trivial relative to total binary size.

`gopls` indexing time: linear in the number of exported names. 200 sentinels add negligible time.

**The real cost**: API surface area. 200 sentinels mean 200 documented contracts, 200 stable identifiers, 200 things callers might check. Code review and documentation cost dominate runtime cost.

**Optimisation**: if the codes are numeric and the design is "decode the code from a wire format and dispatch", a single typed-error-with-code is simpler:

```go
type APIError struct{ Code int; Msg string }
func (e *APIError) Error() string { return fmt.Sprintf("api: %d: %s", e.Code, e.Msg) }
```

Caller checks `if e, ok := err.(*APIError); ok && e.Code == 42`. One type, one comparison. Or, with `errors.As`:

```go
var ae *APIError
if errors.As(err, &ae) && ae.Code == 42 { ... }
```

This is the gRPC `Status` design.

**Key insight**: For wide error spaces (>20 distinct codes), prefer a single structured type with a code field over many sentinels. The runtime cost is similar; the API surface is dramatically smaller.
</details>

---

## Exercise 10 (Hard) — Bench `errors.Is` vs typed predicate

**Problem**: A library exposes both:

```go
// Sentinel-based
var ErrNotFound = errors.New("not found")

// Typed-predicate based
type NotFoundError struct{ Resource string }
func (e *NotFoundError) Error() string { return e.Resource + ": not found" }

// Combined: structured error wrapping the sentinel
func (e *NotFoundError) Unwrap() error { return ErrNotFound }
```

Two callers:

```go
// A — sentinel check
if errors.Is(err, ErrNotFound) { ... }

// B — type assertion
var nfe *NotFoundError
if errors.As(err, &nfe) { ... }
```

**Question**: Which is faster? Which is correct?

<details>
<summary>Solution</summary>

Both are correct. They answer different questions.

A asks: "is this a not-found, of any flavour?" — fast, identity-based, ~5 ns per call.
B asks: "is this a not-found, and what resource?" — slightly slower, ~10-20 ns per call.

`errors.As` walks the chain like `errors.Is` but checks for a type assertion at each level. Cost is similar to `errors.Is` for small chains but dispatches through reflection on first call.

**Benchmark** (10⁶ calls, chain depth 2):
- `errors.Is(err, ErrNotFound)`: ~5 ns/op
- `errors.As(err, &nfe)`: ~15 ns/op

The `As` version is 3x slower because of reflection on the target type.

**When to use which**:
- `Is` for classification: "is this not-found?"
- `As` for data extraction: "what resource was not found?"

For a hot path that *just classifies*, `Is` is the right choice. For code that needs the data anyway, `As` is unavoidable.

**Key insight**: `errors.Is` and `errors.As` answer different questions. `Is` is faster for classification; `As` is necessary for data extraction. Don't replace `Is` with `As` for performance; replace it only when you need the data.
</details>

---

## Bonus Exercise (Hard) — Profile a real error path

**Problem**: A service's `pprof` shows 5% of CPU in `errors.Is`. Investigate.

<details>
<summary>Solution</summary>

5% in `errors.Is` is *high*. Possible causes:

1. **Very deep chains.** Each request wraps 5+ times, each `errors.Is` walks them all.
2. **Many sentinels checked per call.** A classifier with 20+ `errors.Is` calls per request.
3. **Errors that match late.** The default branch always runs all checks.
4. **Hot path with frequent errors.** A high-error-rate endpoint.

Investigation steps:

1. **Profile**: `go tool pprof -focus errors.Is cpu.prof`. Confirm it's in `errors.Is` itself, not in callee `Unwrap` chains.

2. **Inspect chain depth**:
   ```go
   func chainDepth(err error) int {
       n := 0
       for err != nil {
           n++
           u, ok := err.(interface{ Unwrap() error })
           if !ok { break }
           err = u.Unwrap()
       }
       return n
   }
   ```
   Log this for a sample of errors.

3. **Reduce wrapping**: keep wrap depth at 2-3.

4. **Reorder classifier**: most common cases first.

5. **Classify once**: convert error to a `Class` enum once at the boundary, dispatch on the enum elsewhere.

6. **Switch to typed errors with codes** if the sentinel set is large and the error is the dominant return path.

**Realistic outcome**: 5% becomes 1-2%, dominated by the error path's actual work (logging, metrics).

**Key insight**: `errors.Is` performance issues are usually a symptom of over-wrapping or over-classification. Fix the design, not the call.
</details>

---

## Summary

Sentinel-related performance is rarely the bottleneck. The interesting numbers:

| Operation | Cost |
|---|---|
| `==` against a sentinel | ~1 ns |
| `errors.Is` (depth 1) | ~5 ns |
| `errors.Is` (depth 5) | ~25 ns |
| `errors.As` | ~10-20 ns |
| `fmt.Errorf("...: %w", ...)` | ~30-50 ns + 1 alloc |
| `errors.New("...")` | ~10 ns + 1 alloc (init only) |

Optimisations that matter:

1. Reduce wrap depth (1-2 layers).
2. Wrap at API boundaries, not at every internal call.
3. Order classifiers by frequency.
4. Classify once if the same error is mapped multiple times.
5. Use `errors.Is` for identity, `errors.As` only when you need data.

Optimisations that rarely matter:

1. Replacing `errors.Is` with `==`.
2. Avoiding sentinels in favor of error codes.
3. Caching classification results across calls.

Sentinels are cheap. Most performance improvements in error handling are about reducing the number of errors generated, not making each error faster.
