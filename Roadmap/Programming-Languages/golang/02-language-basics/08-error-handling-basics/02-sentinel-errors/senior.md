# Go Sentinel Errors — Senior Level

## 1. Overview

Senior-level mastery of sentinel errors is about being precise: knowing the exact runtime representation, the cost model, the API-evolution constraints, and the design tradeoffs versus structured errors. A sentinel is a tiny piece of mechanism — a `*errorString` allocated at package init — but it is wrapped in heavy convention. Treat it like any other piece of public API.

---

## 2. Runtime Representation

### 2.1 `*errorString` as the underlying type

`errors.New` is a four-line function in `src/errors/errors.go`:

```go
// New returns an error that formats as the given text.
// Each call to New returns a distinct error value even if the text is identical.
func New(text string) error {
    return &errorString{text}
}

type errorString struct {
    s string
}

func (e *errorString) Error() string {
    return e.s
}
```

A sentinel is therefore a pointer to a heap-allocated `errorString` containing a single `string` field. The variable `io.EOF` (or any sentinel) stores this pointer.

The `error` interface itself is two words: a `*itab` for the dynamic type and a `unsafe.Pointer` to the data. So a sentinel comparison via `==` reduces to:

1. Compare the type pointers (always `*errorString` for sentinels).
2. Compare the data pointers — i.e. the addresses of the underlying `errorString` allocations.

Two sentinels are equal iff they reference the same heap allocation. This is the foundation of identity-based equality.

### 2.2 Identity, not value, equality

```go
a := errors.New("oops")
b := errors.New("oops")
fmt.Println(a == b) // false — different *errorString allocations
fmt.Println(a.Error() == b.Error()) // true — same message
```

Sentinels rely on identity. Two values with the same message are *not* the same sentinel; they are unrelated errors that happen to print the same way. This is why `errors.New` is the idiomatic choice — each call produces a fresh allocation, but you call it exactly once at package init for the sentinel name, and every comparison thereafter is against that allocation.

### 2.3 Why `var`, not `const`

Go's `const` system supports only basic types (numbers, strings, booleans). It cannot express interface values or pointers. A sentinel needs to be:
- A pointer (so identity is meaningful and comparable).
- Of interface type `error`.

That requires `var`. Convention says you do not reassign the variable after init; the language does not enforce it.

`internal/abi.RuntimeError` and a few other internal sentinels are similar — package-level `var` of error type.

### 2.4 Init ordering

Sentinels are initialized during the package init phase, in declaration order, before `init()` runs and before any user code in importing packages executes. Two consequences:

1. Importing packages can safely reference the sentinel from their own `init()`s.
2. If you initialise a sentinel using another package's identifier (`var ErrFoo = pkg.ErrFoo`), Go's import-graph topological sort guarantees `pkg`'s sentinel is initialised first.

A pathological case: cyclic init through reflection or `runtime.SetFinalizer` could observe a sentinel as a typed nil mid-init. In practice this never happens — sentinels are bound to package-level vars, which are initialised before any code runs.

---

## 3. Identity-Based Equality in Detail

### 3.1 What `==` does on errors

For two `error` values:
- If their dynamic types differ, `==` is false.
- If their dynamic types match and the type is **comparable** (most pointer types qualify), Go compares the data words.

For `*errorString`, the data word is the pointer to the heap allocation. Two distinct allocations are unequal even with identical content. The single-allocation discipline of sentinels makes them comparable across the program.

### 3.2 What `errors.Is` does

`errors.Is` from `src/errors/wrap.go`:

```go
func Is(err, target error) bool {
    if target == nil {
        return err == target
    }
    isComparable := reflectlite.TypeOf(target).Comparable()
    for {
        if isComparable && err == target {
            return true
        }
        if x, ok := err.(interface{ Is(error) bool }); ok && x.Is(target) {
            return true
        }
        switch x := err.(type) {
        case interface{ Unwrap() error }:
            err = x.Unwrap()
            if err == nil {
                return false
            }
        case interface{ Unwrap() []error }:
            for _, err := range x.Unwrap() {
                if Is(err, target) {
                    return true
                }
            }
            return false
        default:
            return false
        }
    }
}
```

Two paths:
- **Direct equality** at every level of the chain (when `target` is comparable).
- **`Is` method dispatch** — if the current link has an `Is(error) bool` method, the runtime delegates to it.

So `errors.Is` against a sentinel walks the chain, comparing each link to the sentinel. The cost is `O(chain depth)` pointer comparisons plus a few interface assertions.

### 3.3 Comparability constraint

A sentinel target must be a comparable error. `*errorString` is a pointer — comparable. A user-defined error type containing a slice or map is **not** comparable; using it as a sentinel target would panic in older Go releases. Modern `errors.Is` (Go 1.20+) gracefully handles non-comparable types by skipping the `==` step.

For all stdlib sentinels and 99% of custom sentinels, the underlying type is `*errorString` or a tiny struct with comparable fields. Comparability is not a practical concern.

---

## 4. Cost Model

### 4.1 Allocation cost

A sentinel costs exactly one allocation, performed once at package init:

```
errors.New("io: EOF") → *errorString { s: "io: EOF" } on the heap
```

Approximately 24 bytes (16-byte string header + alignment). Negligible relative to any other package init work.

### 4.2 Comparison cost

`==` against a sentinel: one pointer comparison, ~1 ns. Inlined and branch-predictable.

`errors.Is` against a 3-link chain: 3 pointer comparisons + a couple of interface assertions, ~5-10 ns. Still negligible.

### 4.3 Wrapping cost

Each `fmt.Errorf("...: %w", err)` allocates one `*fmt.wrapError`:

```go
type wrapError struct {
    msg string
    err error
}

func (e *wrapError) Error() string  { return e.msg }
func (e *wrapError) Unwrap() error  { return e.err }
```

About 32-64 bytes per wrap. Wrapping a sentinel three times produces three small allocations and a chain of `Unwrap` pointers. In normal request-handling code this is fine; in microbenchmarks of error-heavy hot paths it can show up.

### 4.4 GC cost

Sentinel `*errorString` allocations are pinned by package globals — they live for the program's lifetime and never enter GC's reachable scan beyond root marking. They are essentially free at runtime.

Wrapped errors allocated per call follow normal GC rules.

---

## 5. Init-Order Considerations

### 5.1 Sentinel declaration ordering

Within a package, vars are initialised in declaration order (with cycle-breaking by topological dependency). Sentinels are independent of each other, so order does not matter.

### 5.2 Sentinels referencing other sentinels

```go
package mine

import "io"

var ErrEnd = io.EOF // alias
```

`mine.ErrEnd` and `io.EOF` are the same `*errorString` allocation. Both `errors.Is(err, mine.ErrEnd)` and `errors.Is(err, io.EOF)` succeed identically.

But:

```go
package mine

import "io"

var ErrEnd = errors.New("end") // distinct allocation
```

Even though the message is similar, this is a fresh sentinel with no relationship to `io.EOF`. `errors.Is(err, mine.ErrEnd)` does not match `io.EOF`, and vice versa.

### 5.3 Sentinels inside `init()`

Avoid this:

```go
var ErrFoo error

func init() {
    ErrFoo = errors.New("foo")
}
```

It works, but loses the convention-driven readability of a top-level `var ErrFoo = errors.New("foo")`. It also means downstream packages that read `ErrFoo` from their own init code see whatever ordering the import graph produces. Stick with `var`.

### 5.4 Generated sentinels

Some code generators (gRPC, OpenAPI clients) emit sentinels per error code. Same rules apply: declared at package level, initialised before user code runs.

```go
// generated
var (
    ErrCodeNotFound       = errors.New("api: 404")
    ErrCodeUnauthorized   = errors.New("api: 401")
    ErrCodeInternal       = errors.New("api: 500")
)
```

---

## 6. Sentinels vs Structured Errors — Senior-Level Decision

### 6.1 The design test

Ask: *"What does the caller need to do?"*

- **Just classify:** sentinel.
- **Classify + extract data (path, key, code):** structured error with `Unwrap` to a sentinel for classification.
- **Classify into categories:** sentinel + `Is` method on structured types.

### 6.2 The os.PathError pattern

```go
type PathError struct {
    Op   string
    Path string
    Err  error // wrapped sentinel like syscall.ENOENT
}

func (e *PathError) Error() string { return e.Op + " " + e.Path + ": " + e.Err.Error() }
func (e *PathError) Unwrap() error { return e.Err }
```

Plus:

```go
// in os/error.go
var ErrNotExist = errPermissionDenied // ... approximately
```

`*os.PathError`'s `Unwrap` returns the syscall error. `os.ErrNotExist` is a sentinel that aliases / wraps the relevant syscall errors. `errors.Is` walks from the `*PathError` wrapper to the inner `syscall.Errno` and matches against `os.ErrNotExist` via a custom `Is` method.

This is the canonical "sentinel + structured" combination. Callers get both: identity (`errors.Is(err, os.ErrNotExist)`) and data (`var pe *PathError; errors.As(err, &pe); _ = pe.Path`).

### 6.3 When to add an `Is` method

A custom `Is` method on a structured error type is appropriate when:
- The type wraps multiple underlying errors that *should all* match a single sentinel.
- The type expresses a category (`StatusError` matching `ErrRetryable` for retry-worthy status codes).
- You want to maintain a stable sentinel surface while internals evolve.

```go
type DBError struct {
    Code int
    Msg  string
}

var ErrTransient = errors.New("db: transient")

func (e *DBError) Is(target error) bool {
    if target == ErrTransient {
        return e.Code == 53300 || e.Code == 57P03 // representative codes
    }
    return false
}
```

Callers say `errors.Is(err, ErrTransient)`. The set of "transient" codes can grow without changing the public sentinel.

---

## 7. API Evolution Constraints

### 7.1 What you can change

- The message text (no caller depends on it for identity).
- The internal type backing the sentinel (as long as identity comparison still works — which it always does for a `var`).
- The number of internal call sites returning the sentinel (more or fewer is fine, *as long as the documented condition still produces it*).

### 7.2 What you cannot change

- The variable name (importers reference it).
- The exact set of conditions documented to produce it.
- The fact that it implements `error`.
- Its identity across versions (don't replace `var X = errors.New(...)` with a fresh `errors.New` call elsewhere).

### 7.3 Adding new sentinels

Always backward-compatible — new exports cannot break existing imports.

### 7.4 Removing sentinels

Always a breaking change. Reserve for major-version bumps. The migration story for callers is annoying because every `errors.Is(err, pkg.ErrFoo)` site needs an alternative.

### 7.5 Migrating from sentinel to structured

If a sentinel was overloaded and you need data attached:

```go
// v1: bare sentinel
var ErrConflict = errors.New("repo: conflict")
return ErrConflict

// v2: keep the sentinel, add a structured type that wraps it
type ConflictError struct{ Key string }
func (e *ConflictError) Error() string { return "conflict on " + e.Key }
func (e *ConflictError) Unwrap() error { return ErrConflict }

return &ConflictError{Key: key}
```

`errors.Is(err, ErrConflict)` continues to work for old callers. New callers can `errors.As(err, &ce)` for the key. Backward-compatible evolution.

---

## 8. Sentinels in `go/types` and Reflection

### 8.1 Type-checker view

To `go/types`, a sentinel is a `*types.Var` of type `error`. Nothing distinguishes it from any other error-typed variable.

### 8.2 Doc-comment heuristics

`go vet` and `staticcheck` apply heuristics: any package-level `error` var named `Err...` is treated as a sentinel for some lints (e.g. `errorlint`). The convention is enforced socially, not by the type system.

### 8.3 Reflection

```go
import "reflect"

v := reflect.ValueOf(io.EOF)
fmt.Println(v.Type()) // *errors.errorString
fmt.Println(v.Pointer()) // address of the underlying *errorString
```

Reflection sees the `*errorString` directly. Useful for introspection in test helpers, but not part of normal use.

---

## 9. Cross-Module / Cross-Build Concerns

### 9.1 Same package path, two builds

Sentinels are package globals. If two binaries build the same package independently (e.g., a shared library plugin loaded into a host), each has its own `*errorString`. Comparisons across the boundary fail.

This rarely matters: Go statically links by default, so sentinels are unique within a build.

### 9.2 Vendored copies

If your build vendors `github.com/foo/bar` directly *and* indirectly through `github.com/baz/qux`, the Go toolchain ensures you get one copy of `bar` per build (via `go.mod`'s minimum-version selection). Sentinels are unique.

If you mix `vendor/` directory and module-mode imports incorrectly, you can end up with two `bar` copies. This is a build-system bug, not a sentinel design flaw.

### 9.3 Shared libraries (`-buildmode=shared`)

Each shared library has its own sentinels. Comparing across shared-library boundaries is unreliable. In practice, almost no one uses Go's shared-library mode — this is an exotic concern.

---

## 10. Concurrency

A sentinel is a read-only pointer to a read-only struct. Concurrent reads need no synchronisation. The data race detector does not flag them.

Wrapping is safe: `fmt.Errorf` does not mutate the wrapped error. `errors.Is` does not mutate. The sentinel and any wrap chain are effectively immutable once constructed.

---

## 11. Production Patterns

### 11.1 The boundary translator

Every public API layer defines its own sentinels and translates inner-layer ones:

```go
// repo/repo.go
var ErrNotFound = errors.New("repo: not found")

func (r *Repo) Get(...) (..., error) {
    ... err := r.db.QueryRowContext(...).Scan(...)
    if errors.Is(err, sql.ErrNoRows) {
        return ..., fmt.Errorf("...: %w", ErrNotFound)
    }
    ...
}
```

Inner-layer sentinels become implementation details.

### 11.2 The classifier

```go
type Class int

const (
    ClassUnknown Class = iota
    ClassNotFound
    ClassConflict
    ClassCancelled
    ClassDeadline
    ClassTransient
    ClassPermanent
)

func Classify(err error) Class {
    switch {
    case err == nil:                            return ClassUnknown // or "ok"
    case errors.Is(err, ErrNotFound):           return ClassNotFound
    case errors.Is(err, ErrConflict):           return ClassConflict
    case errors.Is(err, context.Canceled):      return ClassCancelled
    case errors.Is(err, context.DeadlineExceeded): return ClassDeadline
    case errors.Is(err, ErrTransient):          return ClassTransient
    default:                                    return ClassPermanent
    }
}
```

Used for retry policies, metrics labels, and HTTP status mapping. The sentinel set defines the classifier's vocabulary.

### 11.3 Sentinel-backed retry policies

```go
type Policy struct {
    Max    int
    Sleep  time.Duration
}

func Do(ctx context.Context, fn func() error, p Policy) error {
    var err error
    for i := 0; i < p.Max; i++ {
        err = fn()
        if err == nil {
            return nil
        }
        if !errors.Is(err, ErrTransient) {
            return err
        }
        select {
        case <-ctx.Done():
            return ctx.Err()
        case <-time.After(p.Sleep):
        }
    }
    return fmt.Errorf("retried %d: %w", p.Max, err)
}
```

The retry decision is a single `errors.Is`. New retryable conditions: extend the `Is` method on the underlying structured error or expand `ErrTransient`'s aliasing.

### 11.4 Sentinels in tests

```go
err := svc.Do(...)
if !errors.Is(err, ErrInvalidInput) {
    t.Fatalf("got %v, want ErrInvalidInput", err)
}
```

Tests pin the sentinel-level contract. Refactors of internal error messages do not break them.

---

## 12. Production Incidents

### 12.1 The "wrapped == comparison" silent break

A team upgraded their repository to wrap database errors:

```go
// before
return ErrNotFound

// after
return fmt.Errorf("get user %d: %w", id, ErrNotFound)
```

Clients had been using `if err == ErrNotFound`. Suddenly, the check returned false for every miss; every "not found" turned into an `Internal Server Error`. Discovered in production after a deploy.

Lesson: enforce `errors.Is` via lint (`errorlint`). Treat `==` against an exported sentinel as a code smell.

### 12.2 The accidental wrap in declaration

```go
var ErrInternal = fmt.Errorf("svc: internal: %w", io.EOF) // typo, should have been errors.New
```

The maintainer copy-pasted the format from a wrap site. `errors.Is(svc.ErrInternal, io.EOF)` returned true, and the retry policy wrongly retried "internal" failures because `io.EOF` was on the retryable list.

Lesson: prefer `errors.New` for sentinels; reach for `fmt.Errorf` only when you genuinely need formatting.

### 12.3 The sentinel re-export trap

A wrapper package re-declared an upstream sentinel:

```go
// upstream/store
var ErrNotFound = errors.New("store: not found")

// wrapper
var ErrNotFound = errors.New("wrapper: not found") // distinct identity
```

Some code paths returned the upstream sentinel (`store.ErrNotFound`); others returned the wrapper's. Callers checked the wrapper's. Half their "not found" cases fell through to the default branch.

Fix: alias instead of redeclare:
```go
var ErrNotFound = upstream.ErrNotFound
```

Or, better, define a new sentinel and convert at boundary.

### 12.4 The sentinel that grew fields

A repo defined `var ErrConflict = errors.New("...")`. Later, callers asked which key conflicted. The team turned `ErrConflict` into a function:

```go
// before
var ErrConflict = errors.New("repo: conflict")

// after — broken
type ConflictError struct{ Key string }
func ErrConflict(k string) *ConflictError { return &ConflictError{Key: k} }
```

This silently broke every `errors.Is(err, ErrConflict)` check (now a function, not an error). The right approach is to keep the sentinel as-is and add a structured type that wraps it:

```go
var ErrConflict = errors.New("repo: conflict")

type ConflictError struct{ Key string }
func (e *ConflictError) Error() string { return "conflict on " + e.Key }
func (e *ConflictError) Unwrap() error { return ErrConflict }
```

`errors.Is(err, ErrConflict)` continues to work; new callers can `errors.As`.

---

## 13. Best Practices

1. Treat sentinels as part of your stable public API.
2. Use `errors.New`, not `fmt.Errorf`.
3. Document return conditions.
4. Translate at boundaries.
5. Use `errors.Is`; reserve `==` for tightly-controlled local hot paths.
6. Pair sentinels with structured errors when data matters.
7. Keep the set small.
8. Use the `Is` method to express categorical predicates (`ErrRetryable`).
9. Lint with `errorlint`.
10. Test sentinel returns explicitly.

---

## 14. Self-Assessment Checklist

- [ ] I can describe `*errorString` and identity-based equality.
- [ ] I can read `errors.Is` and trace the chain walk.
- [ ] I know the cost: ~1 alloc at init, ~1 ns per `==`, ~5-10 ns per `errors.Is`.
- [ ] I can translate inner-layer sentinels at API boundaries.
- [ ] I can decide between sentinel, structured, and combined.
- [ ] I know how to use the `Is` method for derived predicates.
- [ ] I can articulate the API-evolution rules for sentinels.
- [ ] I know the common production failure modes.

---

## 15. Summary

A sentinel is a single `*errorString` allocated at package init and exposed as a package-level `var`. Identity equality (`==` or the chain walk in `errors.Is`) is the foundation. The runtime cost is negligible; the design cost — public-API surface, coupling, evolution constraints — is real. Use sentinels for a small, closed set of well-known conditions, translate them at layer boundaries, and pair them with structured errors when data is needed. Lint with `errorlint`, prefer `errors.Is`, document return conditions.

---

## 16. Further Reading

- [`errors` source](https://cs.opensource.google/go/go/+/refs/tags/go1.22.0:src/errors/errors.go)
- [`errors.Is` source](https://cs.opensource.google/go/go/+/refs/tags/go1.22.0:src/errors/wrap.go)
- [`io` package vars](https://cs.opensource.google/go/go/+/refs/tags/go1.22.0:src/io/io.go)
- [`database/sql` errors](https://cs.opensource.google/go/go/+/refs/tags/go1.22.0:src/database/sql/sql.go)
- [`os` errors](https://cs.opensource.google/go/go/+/refs/tags/go1.22.0:src/os/error.go)
- [Go blog — Working with Errors in Go 1.13](https://go.dev/blog/go1.13-errors)
- [errorlint](https://github.com/polyfloyd/go-errorlint)
- 2.8.4 Error wrapping
- 2.8.5 `errors.Is` / `errors.As`
