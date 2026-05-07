# The error Interface — Optimize

## Instructions

Each exercise presents an inefficient or wasteful error-handling pattern. Identify the issue, write an optimized version, and explain. Difficulty markers: Easy (E), Medium (M), Hard (H).

For every benchmark snippet shown, the numbers are typical on a modern x86-64 machine (Go 1.21+). Run them yourself with `go test -bench=. -benchmem` to confirm — the relative ordering is what matters, not the absolute numbers.

---

## Exercise 1 (E) — `errors.New` In A Hot Path

**Problem**:

```go
func process(req Request) error {
    if req.UserID == 0 {
        return errors.New("user id required")
    }
    if req.Action == "" {
        return errors.New("action required")
    }
    return nil
}
```

The function is called millions of times per second under load.

<details>
<summary>Solution</summary>

**Issue**: Every error path allocates a fresh `*errorString` (16 B per allocation, plus the iface header). Under load this is measurable in profiles.

**Optimization**: hoist sentinels.

```go
var (
    errMissingUserID = errors.New("user id required")
    errMissingAction = errors.New("action required")
)

func process(req Request) error {
    if req.UserID == 0 {
        return errMissingUserID
    }
    if req.Action == "" {
        return errMissingAction
    }
    return nil
}
```

**Benchmark**:

```
BenchmarkErrorsNewLocal-12       50000000   24 ns/op   16 B/op   1 allocs/op
BenchmarkErrorsNewSentinel-12   500000000    2 ns/op    0 B/op   0 allocs/op
```

Roughly 12x faster, zero allocations. Bonus: callers can now `errors.Is(err, ErrMissingUserID)`.

**Key insight**: Constant-content errors should be sentinels, not call-time constructions.
</details>

---

## Exercise 2 (E) — `fmt.Errorf` With Format-Free Strings

**Problem**:

```go
return fmt.Errorf("config invalid")
```

<details>
<summary>Solution</summary>

**Issue**: `fmt.Errorf` does the full Printf machinery (varargs slice allocation, format-string scan, output buffer). For a string with no verbs, this is pure waste.

**Optimization**:

```go
return errors.New("config invalid")
```

Or, even better, a sentinel.

**Benchmark**:

```
BenchmarkFmtErrorfPlain-12       20000000   88 ns/op   48 B/op   2 allocs/op
BenchmarkErrorsNewPlain-12       50000000   24 ns/op   16 B/op   1 allocs/op
```

**Key insight**: `fmt.Errorf` is for format strings. Use `errors.New` (or a sentinel) when there is nothing to format.
</details>

---

## Exercise 3 (E) — Custom Error Builds Message Each Call

**Problem**:

```go
type FileError struct {
    Op, Path string
    Err      error
}

func (e *FileError) Error() string {
    return fmt.Sprintf("%s %s: %v", e.Op, e.Path, e.Err)
}
```

The error gets logged twice and inspected by `errors.Is` four times per request.

<details>
<summary>Solution</summary>

**Issue**: `Error()` rebuilds the formatted string on every call. With six calls per request, that's six `fmt.Sprintf`s per error.

**Optimization A** — memoize at construction:

```go
type FileError struct {
    Op, Path string
    Err      error
    msg      string
}

func NewFileError(op, path string, err error) *FileError {
    msg := op + " " + path + ": " + err.Error()
    return &FileError{Op: op, Path: path, Err: err, msg: msg}
}

func (e *FileError) Error() string { return e.msg }
```

**Optimization B** — lazy memoize (be careful with goroutine safety):

```go
func (e *FileError) Error() string {
    if e.msg == "" {
        e.msg = e.Op + " " + e.Path + ": " + e.Err.Error()
    }
    return e.msg
}
```

If the error never escapes the originating goroutine before `Error()` is first called, lazy memoization is safe. Otherwise prefer eager (constructor-time) building.

**Optimization C** — replace `fmt.Sprintf` with concatenation. Go's `+` for strings is efficient when count is small:

```go
func (e *FileError) Error() string {
    return e.Op + " " + e.Path + ": " + e.Err.Error()
}
```

Roughly 30% faster than the `Sprintf` form, with one less allocation.

**Key insight**: `Error()` may be called many times. Make it cheap.
</details>

---

## Exercise 4 (M) — Per-Request Wrapped Error

**Problem**:

```go
func handler(w http.ResponseWriter, r *http.Request) {
    if err := svc.Do(r); err != nil {
        http.Error(w, fmt.Errorf("handler: %w", err).Error(), 500)
    }
}
```

<details>
<summary>Solution</summary>

**Issue**: Three things: (1) `fmt.Errorf` allocates a wrapper just to immediately call `.Error()` on it; (2) `%w` allocation is wasted because we discard the wrapped error; (3) `http.Error` already accepts a string.

**Optimization**:

```go
func handler(w http.ResponseWriter, r *http.Request) {
    if err := svc.Do(r); err != nil {
        http.Error(w, "handler: "+err.Error(), 500)
    }
}
```

If you also need to log the error structurally, log first (with the wrap intact for the logger), then send a clean message to the client.

**Key insight**: Don't wrap errors you immediately stringify and discard.
</details>

---

## Exercise 5 (M) — Allocating In `Error()` For Numeric Codes

**Problem**:

```go
type CodeError struct{ Code int }

func (e *CodeError) Error() string {
    return fmt.Sprintf("error %d", e.Code)
}
```

<details>
<summary>Solution</summary>

**Issue**: Every `Error()` call allocates because `fmt.Sprintf` produces a new string. With a finite code space (say, 0..63), pre-render once.

**Optimization** — pre-render table:

```go
type CodeError struct{ Code int }

var codeStrings = func() [64]string {
    var a [64]string
    for i := range a {
        a[i] = "error " + strconv.Itoa(i)
    }
    return a
}()

func (e *CodeError) Error() string {
    if e.Code < 0 || e.Code >= len(codeStrings) {
        return "error " + strconv.Itoa(e.Code)
    }
    return codeStrings[e.Code]
}
```

`strconv.Itoa` allocates too, but only on the rare out-of-range path.

**Benchmark**:

```
BenchmarkCodeErrorSprintf-12   30000000   42 ns/op   24 B/op   2 allocs/op
BenchmarkCodeErrorTable-12    1000000000  1.0 ns/op    0 B/op   0 allocs/op
```

40x faster on the hot path, zero allocations.

**Key insight**: Finite, enumerable error spaces deserve precomputed tables.
</details>

---

## Exercise 6 (M) — Returning A Pointer Where A Value Would Do

**Problem**:

```go
type ParseError struct{ Pos int }

func (e *ParseError) Error() string { return fmt.Sprintf("parse @%d", e.Pos) }

func parse(input []byte) error {
    if len(input) == 0 {
        return &ParseError{Pos: 0}
    }
    return nil
}
```

The error is constructed thousands of times per second when input is empty.

<details>
<summary>Solution</summary>

**Issue**: `&ParseError{...}` heap-allocates. For a tiny struct with no internal references, you can use a value receiver and (sometimes) a sentinel to avoid the alloc:

**Optimization** — sentinel for the only-Pos-zero common case:

```go
var ErrEmptyInput = &ParseError{Pos: 0}

func parse(input []byte) error {
    if len(input) == 0 {
        return ErrEmptyInput
    }
    ...
}
```

For other positions, the heap allocation is unavoidable (each position needs a distinct value), but the empty-input case is now zero-alloc.

**Alternative** — value-receiver design when the struct is comparable and small:

```go
type ParseError struct{ Pos int }
func (e ParseError) Error() string { ... }

func parse(input []byte) error {
    if len(input) == 0 {
        return ParseError{Pos: 0}
    }
}
```

But beware: returning a value through `error` requires interface boxing. The compiler may still allocate. Profile to confirm.

**Key insight**: Sentinels are valid even for struct-shaped errors when the common case is parameter-free.
</details>

---

## Exercise 7 (M) — Logging The Same Error Twice

**Problem**:

```go
func op() error {
    if err := step1(); err != nil {
        log.Printf("step1: %v", err)
        return fmt.Errorf("op: %w", err)
    }
    return nil
}

func handler() {
    if err := op(); err != nil {
        log.Printf("handler: %v", err)
    }
}
```

<details>
<summary>Solution</summary>

**Issue**: Every failure logs twice — at `op` and at `handler`. Logs are expensive (lock contention, formatted output, syscall). And the duplicate noise hides real signal.

**Optimization** — log at one place only.

```go
func op() error {
    if err := step1(); err != nil {
        return fmt.Errorf("op: %w", err)
    }
    return nil
}

func handler() {
    if err := op(); err != nil {
        log.Printf("handler: %v", err)
    }
}
```

Whoever owns the top-level decision logs.

**Key insight**: Wrapping and logging are different concerns. Wrap to add context; log once at the boundary.
</details>

---

## Exercise 8 (H) — Pre-Built Error Hierarchies For HTTP Status Codes

**Problem**:

```go
func toHTTPError(err error) (status int, msg string) {
    switch {
    case errors.Is(err, ErrNotFound):     return 404, "not found"
    case errors.Is(err, ErrUnauthorized): return 401, "unauthorized"
    case errors.Is(err, ErrBadInput):     return 400, "bad input"
    default:                              return 500, fmt.Sprintf("internal: %v", err)
    }
}
```

The function is called for every request, and the default case allocates.

<details>
<summary>Solution</summary>

**Issue**: The default branch allocates with `fmt.Sprintf`, even though the result is just sent to the client and never reused. Also, the switch is a chain of `errors.Is` walks — for a deep error chain, that traversal cost adds up.

**Optimization** — design with categorization on the error type:

```go
type HTTPError struct {
    Status int
    Msg    string
    Err    error
}
func (e *HTTPError) Error() string { return e.Msg }
func (e *HTTPError) Unwrap() error { return e.Err }

// Sentinels of the canonical statuses:
var (
    httpNotFound     = &HTTPError{Status: 404, Msg: "not found"}
    httpUnauthorized = &HTTPError{Status: 401, Msg: "unauthorized"}
    httpBadInput    = &HTTPError{Status: 400, Msg: "bad input"}
)

func toHTTPError(err error) *HTTPError {
    var he *HTTPError
    if errors.As(err, &he) {
        return he
    }
    // wrap unknown error in a generic 500
    return &HTTPError{Status: 500, Msg: "internal error", Err: err}
}
```

Most errors now match in one `errors.As` call. The default 500 path still allocates one wrapper, but the message is constant.

**Key insight**: When error categorization is part of the hot path, encode the category as a field on the error type, not as a separate switch.
</details>

---

## Exercise 9 (H) — Avoiding Allocation In Tight Retry Loops

**Problem**:

```go
func fetchWithRetry(url string) ([]byte, error) {
    var lastErr error
    for i := 0; i < 5; i++ {
        body, err := fetch(url)
        if err == nil {
            return body, nil
        }
        lastErr = fmt.Errorf("attempt %d: %w", i, err) // allocates each iteration
        time.Sleep(backoff(i))
    }
    return nil, lastErr
}
```

<details>
<summary>Solution</summary>

**Issue**: The `fmt.Errorf` allocates a fresh wrapper every iteration, but only the LAST one is ever returned. The intermediate wrappers are GC garbage.

**Optimization** — wrap once, on exit:

```go
func fetchWithRetry(url string) ([]byte, error) {
    var lastErr error
    for i := 0; i < 5; i++ {
        body, err := fetch(url)
        if err == nil {
            return body, nil
        }
        lastErr = err
        time.Sleep(backoff(i))
    }
    return nil, fmt.Errorf("after 5 attempts: %w", lastErr)
}
```

One allocation total, regardless of retry count.

**Key insight**: Don't construct wrapper errors you might throw away. Build them at the value's end-of-life only.
</details>

---

## Exercise 10 (H) — Per-Request Error Pool

**Problem**: A high-QPS handler returns errors with per-request data:

```go
type ReqError struct {
    ReqID string
    Code  int
    Msg   string
}

func (e *ReqError) Error() string {
    return fmt.Sprintf("[%s] %d %s", e.ReqID, e.Code, e.Msg)
}

func handle(reqID string) error {
    return &ReqError{ReqID: reqID, Code: 500, Msg: "internal"}
}
```

<details>
<summary>Solution</summary>

**Issue**: Every error path allocates a `ReqError` per request. The struct has three fields, totalling ~48 bytes — modest, but at 100k QPS that's 4.8 MB/s of GC pressure.

**Optimization** — be careful here. `sync.Pool` for errors is generally a footgun: an error is captured by callers (logging, returning to clients) and may outlive the pool's expectations. Pooling errors is correct only if you can guarantee their lifetime.

**Better optimizations** in priority order:

1. Replace per-request errors with sentinels where the message is constant.
2. Reduce error-struct size (drop optional fields).
3. Avoid unnecessary wrapping (Exercise 9).
4. Only after profiling proves errors dominate allocation, consider a pool — and even then, design with care.

```go
// Sample sync.Pool usage IF you can guarantee lifetime:
var reqErrPool = sync.Pool{
    New: func() any { return new(ReqError) },
}

func putReqError(e *ReqError) {
    *e = ReqError{} // clear fields
    reqErrPool.Put(e)
}

// caller: defer putReqError(err) where the lifetime is known.
```

This is rarely worth it. The standard library does not pool errors anywhere — even `*os.PathError` is built fresh for each call.

**Key insight**: Pool errors only after profiling shows the allocation is the bottleneck. Sentinel + reduce + don't-wrap is almost always a bigger win.
</details>

---

## Exercise 11 (H) — `errors.Is` Walking Costs

**Problem**:

```go
for _, err := range errs {
    if errors.Is(err, ErrTransient) {
        retry()
    } else if errors.Is(err, ErrPermanent) {
        fail()
    } else if errors.Is(err, ErrTimeout) {
        backoff()
    }
}
```

The error chain is sometimes 5+ wrappers deep. `errors.Is` walks the entire chain for each check.

<details>
<summary>Solution</summary>

**Issue**: Each `errors.Is` call walks `Unwrap()` from the outermost wrapper to the leaf. With three checks and a 5-deep chain, that's up to 15 `Unwrap` calls per error.

**Optimization** — single walk, store the category once:

```go
type Category int
const (
    CatUnknown Category = iota
    CatTransient
    CatPermanent
    CatTimeout
)

func categorize(err error) Category {
    switch {
    case errors.Is(err, ErrTransient):
        return CatTransient
    case errors.Is(err, ErrPermanent):
        return CatPermanent
    case errors.Is(err, ErrTimeout):
        return CatTimeout
    }
    return CatUnknown
}

for _, err := range errs {
    switch categorize(err) {
    case CatTransient:
        retry()
    case CatPermanent:
        fail()
    case CatTimeout:
        backoff()
    }
}
```

But the better optimization is to attach the category to the error at construction time:

```go
type CategorizedError struct {
    Cat  Category
    Err  error
}
func (e *CategorizedError) Error() string { return e.Err.Error() }
func (e *CategorizedError) Unwrap() error { return e.Err }

func categorize(err error) Category {
    var ce *CategorizedError
    if errors.As(err, &ce) {
        return ce.Cat
    }
    return CatUnknown
}
```

One walk via `errors.As`, the category is read directly from the struct.

**Key insight**: When you need to branch on multiple error categories, encode them as data on a single struct rather than as separate sentinels.
</details>

---

## Exercise 12 (H) — Reducing Memory Pinning Through Errors

**Problem**:

```go
type ReadError struct {
    Buffer []byte // 4 KB scratch buffer
    Err    error
}

func (e *ReadError) Error() string { return fmt.Sprintf("read failed: %v", e.Err) }
```

The buffer is for diagnostic context but is rarely consulted.

<details>
<summary>Solution</summary>

**Issue**: The 4 KB buffer pins memory until the error is GC'd. If the error escapes to a long-lived structure (e.g., a circuit-breaker history), every entry holds 4 KB.

**Optimization** — extract only the diagnostic snippet you need:

```go
type ReadError struct {
    Snippet [64]byte // first 64 bytes only
    Length  int
    Err     error
}

func NewReadError(buf []byte, err error) *ReadError {
    re := &ReadError{Length: len(buf), Err: err}
    n := copy(re.Snippet[:], buf)
    re.Length = n
    return re
}
```

Now each error pins 64 bytes, not 4 KB. The original buffer is free to be GC'd.

**Key insight**: Errors hold pointers to their data. If your error captures large slices/maps, it holds them alive. Capture minimal data.
</details>

---

## Cheat Sheet — Optimizations By Frequency

| Pattern | When to use |
|---------|-------------|
| Sentinel `var Err... = errors.New("...")` | Constant-content errors (most common case) |
| `errors.New` over `fmt.Errorf` for format-free strings | Always |
| Memoized `Error()` | Errors logged or stringified multiple times |
| Wrap once at end-of-life | Retry loops, multi-step operations |
| Category enum on error struct | Many distinct cases callers branch on |
| Pre-rendered code-to-string table | Finite enumerable error space |
| Capture minimal data | Errors holding large slices, buffers, or whole responses |
| Single log site (top of stack) | Always, to avoid duplicate noise |
| `errors.Is` once, store Category | Multiple checks against the same error chain |

---

## When NOT To Optimize

The above techniques have a real cost: more code, more discipline, more invariants. Don't apply them unless:

1. Profiling shows error paths in the top 5% of allocations.
2. The QPS is high enough that sub-microsecond differences add up.
3. The team understands the trade-offs (memoization, sentinel discipline).

For an internal tool processing 100 requests per minute, an extra 100 ns per error is invisible. Keep the code simple.

The single optimization worth applying everywhere, regardless of profile: **constant-string errors should be sentinels, not call-time `errors.New`.** Cost: zero. Benefit: a stable, comparable error identity. There is no reason not to.

---

## Summary

Optimization for errors is not about exotic tricks — it is about not wasting allocations the language hands you for free. Sentinels for constant errors, structures with cached strings for hot custom errors, and careful wrapping policy together cover 95% of practical needs. The remaining 5% comes from code-specific data: pre-rendered tables, snippet-based error data capture, and category enums.

Profile first. Optimize where it matters. The rest of the time, write clear errors and move on.
