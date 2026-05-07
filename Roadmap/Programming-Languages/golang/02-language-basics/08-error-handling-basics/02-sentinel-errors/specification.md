# Go Specification: Sentinel Errors

**Source primary**: https://pkg.go.dev/errors
**Source related**: https://go.dev/ref/spec, https://pkg.go.dev/io, https://pkg.go.dev/database/sql, https://pkg.go.dev/context, https://pkg.go.dev/os

---

## 1. Spec Reference

| Field | Value |
|---|---|
| Convention | Go 1.0+ (sentinel pattern) |
| Tooling | `errors.New` (Go 1.0+), `fmt.Errorf("%w", err)` (Go 1.13), `errors.Is`/`errors.As` (Go 1.13), multi-wrap `errors.Join` (Go 1.20) |
| Specification | None — sentinels are a convention, not a language feature |

The sentinel concept is documented across the standard library's package-variable sections. The `errors` package documents `Is` and `As` with the canonical phrasing on chain walking.

Verbatim from `pkg.go.dev/errors#Is`:

> "Is reports whether any error in err's tree matches target.
>
> The tree consists of err itself, followed by the errors obtained by repeatedly calling Unwrap. When err wraps multiple errors, Is examines err followed by a depth-first traversal of its children.
>
> An error is considered to match a target if it is equal to that target or if it implements a method Is(error) bool such that Is(target) returns true."

This is the formal definition of how `errors.Is` cooperates with sentinels.

---

## 2. Definition

A **sentinel error** in Go is an exported, package-level variable of interface type `error`, declared by convention with `errors.New(msg)`, used as a stable identifier for a specific failure condition. The sentinel's identity (the address of its underlying allocation) is constant for the program's lifetime; callers detect the sentinel using `errors.Is` (which compares identity through an unwrap chain) or, less commonly, via `==`.

Sentinels are convention, not a language construct. The Go specification has no notion of "sentinel"; the term names a *pattern*: package-level error variables compared against by callers.

---

## 3. Core Rules

### 3.1 Identity-based equality

A sentinel is a single allocation referenced by a package-level `var`. Equality is identity-based: two values are equal iff they point to the same allocation.

```go
var ErrFoo = errors.New("pkg: foo")

var fresh = errors.New("pkg: foo") // distinct allocation, even with same text

ErrFoo == fresh // false
ErrFoo == ErrFoo // true (same allocation)
```

Two `errors.New(s)` calls produce *distinct* values even with identical strings. This is documented in `pkg.go.dev/errors#New`:

> "New returns an error that formats as the given text. Each call to New returns a distinct error value even if the text is identical."

### 3.2 `errors.Is` walks the chain

`errors.Is(err, target)` returns true if `target` is anywhere in `err`'s unwrap tree. Mechanically:

1. If `target` is comparable and `err == target`, return true.
2. If `err` has an `Is(error) bool` method and it returns true for `target`, return true.
3. If `err` has `Unwrap() error`, set `err = err.Unwrap()`; if non-nil, repeat.
4. If `err` has `Unwrap() []error`, recurse on each child.
5. Otherwise, return false.

Quoting `pkg.go.dev/errors#Is`:

> "An error is considered to match a target if it is equal to that target or if it implements a method Is(error) bool such that Is(target) returns true."

### 3.3 `==` only matches the outermost value

`==` against an error compares the interface values directly. It does not walk the chain. A wrapped sentinel does not match the bare sentinel via `==`.

```go
err := fmt.Errorf("ctx: %w", io.EOF)
err == io.EOF              // false
errors.Is(err, io.EOF)     // true
```

### 3.4 Wrapping with `%w` preserves identity

`fmt.Errorf("...: %w", err)` returns a new error whose `Unwrap` returns `err`. The wrapper has its own identity; the wrapped error's identity is preserved and reachable via the chain. Multi-wrap (`%w: %w`) and `errors.Join` produce trees with multiple branches.

### 3.5 Wrapping with `%v` or `%s` breaks the chain

`fmt.Errorf("...: %v", err)` formats the error's text into the message but does NOT add `Unwrap`. The chain is broken; downstream `errors.Is` cannot reach the sentinel.

### 3.6 Convention: declare with `errors.New`

By convention, sentinels are declared with `errors.New(msg)`, not `fmt.Errorf(msg)`. `errors.New` cannot accidentally create a chain (it accepts only a string). `fmt.Errorf` allows `%w`, which can silently introduce an unwanted chain.

### 3.7 Convention: name `Err<Reason>`

Universal convention since Go 1.0. Examples: `io.EOF` (the prefix is dropped because EOF is a recognised acronym), `os.ErrNotExist`, `sql.ErrNoRows`, `context.Canceled` (the prefix is dropped to read like a noun).

### 3.8 Convention: prefix message with the package name

Sentinel messages typically start with the package name: `"sql: no rows in result set"`, `"http: Server closed"`. This makes errors readable in logs without additional context.

### 3.9 Convention: do not mutate after init

A sentinel is `var`, but reassigning it after init silently breaks every existing comparison. Treat sentinels as effectively immutable.

---

## 4. Type Rules

### 4.1 Sentinels are values of interface type `error`

```go
var ErrFoo error = errors.New("foo") // explicit type, redundant
var ErrFoo = errors.New("foo")       // idiomatic, type inferred as error
```

The compiler infers `error` because `errors.New` returns `error`.

### 4.2 The underlying type is `*errorString`

`errors.New` returns a pointer to a tiny struct:
```go
type errorString struct {
    s string
}
func (e *errorString) Error() string { return e.s }
```

Two distinct allocations of `*errorString` are unequal even with the same text.

### 4.3 Typed-value sentinels

Some sentinels are zero-sized typed values rather than `*errorString`:

```go
type deadlineExceededError struct{}
func (deadlineExceededError) Error() string   { return "context deadline exceeded" }
func (deadlineExceededError) Timeout() bool   { return true }
func (deadlineExceededError) Temporary() bool { return true }

var DeadlineExceeded error = deadlineExceededError{}
```

`context.DeadlineExceeded` is declared this way to expose the `Timeout()` and `Temporary()` methods. Identity comparison still works because the type's zero value is unique.

### 4.4 Comparability

A sentinel target passed to `errors.Is` should be comparable (i.e., its concrete type allows `==`). `*errorString` is a pointer — comparable. A struct sentinel is comparable iff all its fields are comparable. Slices, maps, and functions are not comparable; using them as sentinel underlying types causes `errors.Is` to skip the `==` step (and rely on `Is` methods).

---

## 5. Behavioral Specification

### 5.1 Sentinel lifetime

A sentinel allocated at package init lives for the program's lifetime. The package's global `var` keeps it reachable; the GC never collects it.

### 5.2 Concurrent access

Sentinels are read-only after init. Concurrent reads from any number of goroutines need no synchronisation. The race detector does not flag them.

### 5.3 `errors.Is` against `nil`

```go
errors.Is(nil, ErrFoo) // false (when ErrFoo != nil)
errors.Is(err, nil)    // true iff err == nil
```

### 5.4 Sentinel + structured error combination

```go
type PathError struct {
    Op   string
    Path string
    Err  error // typically a sentinel or wrapped error
}

func (e *PathError) Error() string { ... }
func (e *PathError) Unwrap() error { return e.Err }
```

`errors.Is(pe, sentinel)` walks `pe → Unwrap → e.Err` and matches `sentinel`. `errors.As(pe, &target)` extracts the structured fields. Both work simultaneously.

### 5.5 Multi-wrap (Go 1.20+)

```go
err := errors.Join(io.EOF, context.Canceled)
errors.Is(err, io.EOF)            // true
errors.Is(err, context.Canceled)  // true
```

`errors.Join` produces an error whose `Unwrap() []error` returns the joined list. `errors.Is` recursively descends each child.

```go
err := fmt.Errorf("a: %w; b: %w", io.EOF, context.Canceled)
errors.Is(err, io.EOF)            // true
errors.Is(err, context.Canceled)  // true
```

`fmt.Errorf` with multiple `%w` is supported in Go 1.20+.

---

## 6. Defined vs Undefined Behavior

| Situation | Behavior |
|---|---|
| `errors.New(s)` called once at package init, exported as `var X` | Defined: stable sentinel, identity equal across the program |
| Two `errors.New(s)` with identical `s` | Defined: distinct, non-equal values |
| `errors.Is(err, target)` against an unwrap chain | Defined: walks chain, matches via `==` or custom `Is` method |
| `==` against a wrapped sentinel | Defined: false (only matches outermost) |
| Reassigning a sentinel after init | Allowed but breaks identity; unsupported convention |
| Mutating a sentinel's underlying struct fields | Implementation-defined; for `*errorString`, mutates the message but not identity |
| Sentinel comparison across separate builds (plugins, shared libraries) | Undefined: identities differ between builds |
| Sentinel comparison across the wire (RPC, JSON) | Undefined: identity does not survive serialisation |
| Wrapping a sentinel at declaration via `%w` | Defined: produces a sentinel that itself wraps; `errors.Is` matches both |

---

## 7. Edge Cases

### 7.1 Typed-nil sentinel

```go
type myErr struct{}
func (*myErr) Error() string { return "my" }

var e *myErr // typed nil
var s error = e // interface value with non-nil type, nil data

s == nil // false (typed-nil trap)
errors.Is(s, e) // panics or returns false depending on Go version
```

Sentinel checks via `errors.Is` are not directly affected, but the surrounding `if err != nil` patterns are. Functions returning `error` should never return a typed nil.

### 7.2 Multiple init paths

A package's variables are initialised in declaration order, with cycle-breaking by topological dependency. If a sentinel is declared as `var ErrX = pkg.ErrX`, Go's import-graph init order ensures `pkg.ErrX` is initialised first. Cycles between two packages each declaring a sentinel via the other's identifier produce a build error.

### 7.3 Sentinels in `init()`

```go
var ErrFoo error // declared without value

func init() {
    ErrFoo = errors.New("foo")
}
```

Works, but discouraged. Top-level `var ErrFoo = errors.New("foo")` is preferred for readability.

### 7.4 Sentinel as a typed value with non-zero fields

```go
type myErr struct{ code int }
func (m *myErr) Error() string { return ... }

var ErrFoo = &myErr{code: 42}
```

This is a sentinel with attached data. Comparison via `==` still works (pointer equality). Mutating `ErrFoo.code` would silently change behaviour for all observers — a bad pattern. Use `errors.New` for pure sentinels.

### 7.5 Cross-version sentinels

```go
import "example.com/v1/pkg"
import "example.com/v2/pkg"

errors.Is(err, v1.ErrX) // matches errors from v1 producers
errors.Is(err, v2.ErrX) // matches errors from v2 producers
```

Two versions = two allocations = two identities. Producers using one version, consumers using another, will silently miss matches.

### 7.6 Wrapping a sentinel inside its own declaration

```go
var ErrFoo = fmt.Errorf("foo: %w", io.EOF)
```

`ErrFoo` is a sentinel that wraps `io.EOF`. `errors.Is(ErrFoo, io.EOF)` returns true. Almost always unintentional — see the find-bug document.

### 7.7 `errors.Join` with a sentinel

```go
err := errors.Join(ErrA, ErrB)
errors.Is(err, ErrA) // true
errors.Is(err, ErrB) // true
```

`errors.Join(nil, ErrA)` is equal to `errors.Join(ErrA)` (nils are filtered). `errors.Join()` with no args returns nil.

### 7.8 Sentinel re-export via copy-paste

```go
package shim
var ErrFoo = errors.New("foo") // distinct from upstream pkg.ErrFoo
```

This produces a parallel identity. Consumers of `shim.ErrFoo` will not match errors from `pkg`.

### 7.9 Sentinel re-export via alias

```go
package shim
import "example.com/pkg"

var ErrFoo = pkg.ErrFoo // same identity, transparent
```

Both names refer to the same allocation. `errors.Is(err, shim.ErrFoo)` and `errors.Is(err, pkg.ErrFoo)` are equivalent.

---

## 8. Version History

| Go Version | Change |
|---|---|
| Go 1.0 | Sentinel convention established. `errors.New` available. Equality via `==`. |
| Go 1.13 | `errors.Is`, `errors.As`, `fmt.Errorf("%w")` introduced. The wrapping-aware comparison formalised; `errors.Is` becomes the canonical check. |
| Go 1.20 | Multi-wrap support: `errors.Join` and `fmt.Errorf` with multiple `%w` directives. `errors.Is` recursively descends multi-wrap trees. `context.WithCancelCause` and `context.Cause` for richer cancellation reasons. |
| Go 1.21+ | No structural changes; lint and tooling continue to evolve. `errorlint` widely adopted. |

The sentinel pattern itself has been stable since Go 1.0. Go 1.13 added the wrapping-aware equality (`errors.Is`) that made it robust against `%w`. Go 1.20 generalised to multi-wrap.

---

## 9. Implementation-Specific Behavior

### 9.1 `*errorString` allocation

`errors.New` allocates a `*errorString` on the heap. Approximately 24 bytes (pointer + string header + alignment). One allocation per call. For a package-level sentinel, this is one-time at init.

### 9.2 `errors.Is` walking algorithm

Implemented in `src/errors/wrap.go`:
1. Check comparability of the target type (via `reflectlite`).
2. Loop:
   - If comparable and `err == target`, return true.
   - If `err` has `Is(error) bool`, call it; if true, return true.
   - Switch on `Unwrap` interface: `Unwrap() error` advances; `Unwrap() []error` recurses.
   - If neither, return false.

The cost is `O(depth)` for linear chains; `O(total nodes)` for trees.

### 9.3 `errors.As` walking algorithm

Similar to `errors.Is`, but instead of identity it checks if the current error is assignable to the target type. Uses reflection to assign on success.

### 9.4 `runtime` view

A sentinel `*errorString` is a heap allocation pinned by a package global. It is a GC root via the global. The GC never collects it.

### 9.5 Comparability fallback

Pre-Go 1.20, passing a non-comparable error type as the target to `errors.Is` would panic on the `==` step. Go 1.20+ checks comparability upfront and falls back to `Is` method dispatch. Stdlib sentinels are all comparable (`*errorString` and tiny structs); the issue only affected user-defined types.

---

## 10. Spec Compliance Checklist

- [ ] Sentinel declared with `errors.New(...)` (or a typed value with intentional methods).
- [ ] Named `Err<Reason>` (or recognised idiom like `EOF`, `Canceled`).
- [ ] Message prefixed with the package name.
- [ ] Documented with the conditions that produce it.
- [ ] Detected with `errors.Is`, not `==`.
- [ ] Wrapped with `%w` when adding context.
- [ ] Translated at API boundaries.
- [ ] Not re-declared in another package (only aliased if intentional).
- [ ] Not mutated after init.
- [ ] Treated as part of the package's stable public API.

---

## 11. Official Examples

### Example 1 — Stdlib `io.EOF`

`src/io/io.go`:
```go
var EOF = errors.New("EOF")
```

Idiomatic use:
```go
for {
    n, err := r.Read(buf)
    if err == io.EOF { // OK because Read promises bare EOF
        break
    }
    if err != nil {
        return err
    }
    process(buf[:n])
}
```

### Example 2 — Stdlib `sql.ErrNoRows`

`src/database/sql/sql.go`:
```go
var ErrNoRows = errors.New("sql: no rows in result set")
```

Idiomatic use:
```go
err := db.QueryRow(...).Scan(&v)
switch {
case errors.Is(err, sql.ErrNoRows):
    return Default, nil
case err != nil:
    return Default, fmt.Errorf("query: %w", err)
}
```

### Example 3 — Stdlib `context.Canceled` and `context.DeadlineExceeded`

`src/context/context.go`:
```go
var Canceled = errors.New("context canceled")

type deadlineExceededError struct{}
func (deadlineExceededError) Error() string   { return "context deadline exceeded" }
func (deadlineExceededError) Timeout() bool   { return true }
func (deadlineExceededError) Temporary() bool { return true }

var DeadlineExceeded error = deadlineExceededError{}
```

Idiomatic use:
```go
if err := work(ctx); err != nil {
    if errors.Is(err, context.Canceled) || errors.Is(err, context.DeadlineExceeded) {
        return nil // expected
    }
    return err
}
```

### Example 4 — Stdlib `os.ErrNotExist`

`src/os/error.go` (alias):
```go
var ErrNotExist = fs.ErrNotExist
```

`src/io/fs/fs.go`:
```go
var ErrNotExist = errors.New("file does not exist")
```

Idiomatic use:
```go
_, err := os.Open(path)
if errors.Is(err, os.ErrNotExist) {
    return writeDefault(path)
}
```

### Example 5 — User package with documented sentinels

```go
// Package store provides a key-value store.
package store

import "errors"

// ErrNotFound is returned by Lookup when the key is absent.
var ErrNotFound = errors.New("store: not found")

// ErrConflict is returned by Insert when a value already exists for the key.
var ErrConflict = errors.New("store: conflict")
```

### Example 6 — Custom `Is` method

```go
type StatusError struct{ Code int }

func (e *StatusError) Error() string { return fmt.Sprintf("status %d", e.Code) }

var ErrRetryable = errors.New("retryable")

func (e *StatusError) Is(target error) bool {
    if target == ErrRetryable {
        return e.Code == 429 || (e.Code >= 500 && e.Code < 600)
    }
    return false
}
```

---

## 12. Related Spec Sections

| Section | URL | Relevance |
|---|---|---|
| `errors` package | https://pkg.go.dev/errors | Definitions of `New`, `Is`, `As`, `Join`, `Unwrap` |
| Predeclared `error` interface | https://go.dev/ref/spec#Errors | `error` as a built-in interface |
| Variable declarations | https://go.dev/ref/spec#Variable_declarations | `var` semantics for sentinels |
| `fmt.Errorf` and `%w` | https://pkg.go.dev/fmt#Errorf | Error wrapping syntax |
| `io` package vars | https://pkg.go.dev/io#pkg-variables | Stdlib sentinels |
| `database/sql` package vars | https://pkg.go.dev/database/sql#pkg-variables | Stdlib sentinels |
| `context` package vars | https://pkg.go.dev/context#pkg-variables | Stdlib sentinels |
| `os` package vars | https://pkg.go.dev/os#pkg-variables | Stdlib sentinels |
| `io/fs` package vars | https://pkg.go.dev/io/fs#pkg-variables | Filesystem sentinels |
| Memory model | https://go.dev/ref/mem | Concurrent reads of immutable globals |
