# Go Specification: The error Interface

**Source:** https://go.dev/ref/spec#Errors
**Package docs:** https://pkg.go.dev/errors
**Sections:** Errors (predeclared identifier), Interface types, Method sets, Predeclared identifiers

---

## 1. Spec Reference

| Field | Value |
|-------|-------|
| **Official Spec** | https://go.dev/ref/spec#Errors |
| **errors package** | https://pkg.go.dev/errors |
| **Predeclared identifiers** | https://go.dev/ref/spec#Predeclared_identifiers |
| **Interface types** | https://go.dev/ref/spec#Interface_types |
| **Method sets** | https://go.dev/ref/spec#Method_sets |
| **Go Version** | Go 1.0+ (predeclared `error`); Go 1.13 added `Unwrap`/`Is`/`As`; Go 1.20 added `Join` |

The Go spec's "Errors" section is unusually short. It declares the interface and points the reader at the `errors` package. The verbatim text:

> The predeclared type `error` is defined as
>
> ```go
> type error interface {
>     Error() string
> }
> ```
>
> It is the conventional interface for representing an error condition, with the nil value representing no error. For instance, a function to read data from a file might be defined:
>
> ```go
> func Read(f *File, b []byte) (n int, err error)
> ```

Three takeaways from the spec text:

1. `error` is **predeclared** — like `int`, `string`, `len`. No import required.
2. The interface is exactly one method: `Error() string`.
3. The convention is: nil = success; non-nil = failure.

---

## 2. Definition

An **error** in Go is any value of a type that implements the `error` interface — that is, any type with a method `Error() string`. The interface is built into the language; no `errors` import is required to use the interface itself.

The `error` interface, like all interfaces with one or more methods, is represented at runtime as an iface — a two-word tuple of type tag and data pointer. Comparing two error values with `==` compares both halves: same dynamic type AND same data.

Errors are propagated by **return values**, not by stack unwinding. Go has no try/catch. Functions that may fail return an `error` as their last result; the caller checks with `if err != nil`.

---

## 3. Core Rules

### 3.1 Rule: Any Type With `Error() string` Implements `error`

```go
type myErr struct{}
func (myErr) Error() string { return "x" }

var e error = myErr{} // automatic interface satisfaction
```

No `implements` keyword. The compiler verifies the method set at the assignment site.

### 3.2 Rule: Pointer Receiver Restricts The Method Set

If `Error()` is defined on `*T`, only `*T` satisfies `error`. `T{}` does NOT.

```go
type P struct{}
func (p *P) Error() string { return "p" }

var _ error = &P{} // ok
var _ error = P{}  // compile error: P does not implement error
```

If `Error()` is defined on `T`, BOTH `T` and `*T` satisfy `error`. (Because the method set of `*T` always contains the method set of `T`.)

### 3.3 Rule: The `nil` Value Of `error` Means "No Error"

By convention, every Go function returning `(T, error)` returns `(zero, nil)` on success and `(undefined, non-nil)` on failure. Callers must check the error before using the other return values.

```go
result, err := op()
if err != nil {
    // result is undefined; do not use it
    return err
}
// result is meaningful
```

### 3.4 Rule: A Nil Interface Has BOTH Halves Nil

The error interface is `(type, data)`. It is nil only when both halves are nil.

```go
var err error // (nil, nil) -> err == nil true

var p *MyErr // typed nil pointer
err = p      // (*MyErr, nil) -> err == nil FALSE
```

This is the famous nil-interface bug. It is a direct consequence of the iface representation, not a Go quirk.

### 3.5 Rule: `errors.New` Returns A Pointer; Each Call Allocates

```go
e1 := errors.New("x")
e2 := errors.New("x")
// e1 and e2 are different *errorString pointers; e1 == e2 is FALSE
```

The interface stores the pointer in `iface.data`. Two calls produce two distinct interfaces.

### 3.6 Rule: `fmt.Errorf` Without `%w` Produces A Flat Error

```go
inner := errors.New("inner")
outer := fmt.Errorf("ctx: %v", inner) // %v
// errors.Is(outer, inner) is FALSE — no wrap
```

Use `%w` to wrap (next topic).

### 3.7 Rule: An Error Type May Define `Unwrap() error`

```go
type Wrap struct {
    Cause error
}
func (w *Wrap) Error() string { return "wrap" }
func (w *Wrap) Unwrap() error { return w.Cause }
```

`errors.Is`, `errors.As`, and `errors.Unwrap` honor this method to traverse the chain.

### 3.8 Rule: An Error Type May Define `Unwrap() []error` (Go 1.20+)

For multi-error wrappers (`errors.Join`-style):

```go
type Multi struct{ errs []error }
func (m *Multi) Error() string { return "multi" }
func (m *Multi) Unwrap() []error { return m.errs }
```

`errors.Is` and `errors.As` walk all branches.

### 3.9 Rule: The Custom `Is(target error) bool` Method Is Honored

```go
type CodeErr struct{ Code int }
func (e *CodeErr) Error() string { return "code" }
func (e *CodeErr) Is(target error) bool {
    var t *CodeErr
    return errors.As(target, &t) && t.Code == e.Code
}
```

`errors.Is(myCodeErr, &CodeErr{Code: 42})` calls `Is` for structural matching beyond simple `==`.

### 3.10 Rule: The Custom `As(target any) bool` Method Is Honored

```go
func (e *CodeErr) As(target any) bool {
    if t, ok := target.(**CodeErr); ok {
        *t = e
        return true
    }
    return false
}
```

Rare in practice — the default reflection-based `As` handles most cases.

### 3.11 Rule: Error Strings Are Lowercase Without Trailing Punctuation

A convention, not enforced by the spec. Codified by `golint`/`revive`. Standard library follows it everywhere: `"file does not exist"`, `"sql: no rows in result set"`.

### 3.12 Rule: `panic` Is Not An Error

A panic carries a value of type `any`, not `error`. You can `panic(err)`, but the recovered value is `any`. Conventionally, panics are reserved for programmer mistakes, not the kind of failure errors handle.

---

## 4. Edge Cases

### 4.1 The Nil-Interface Bug

Already covered in `3.4` and in `find-bug.md` (Bug 4). Restated for completeness:

```go
func bad() error {
    var p *MyErr
    return p // returns iface (*MyErr, nil) — NOT nil
}

if err := bad(); err != nil { // TRUE
    fmt.Println(err) // may panic if Error() dereferences
}
```

Always return untyped `nil` or use `var err error` as the slot.

### 4.2 Wrapped Errors Compared With `==`

```go
var ErrX = errors.New("x")
err := fmt.Errorf("ctx: %w", ErrX)
err == ErrX // FALSE — err is the wrapper, not ErrX
errors.Is(err, ErrX) // TRUE — walks the chain
```

After Go 1.13 introduced `%w`, `==` is fragile. Use `errors.Is`.

### 4.3 Comparable Struct Errors

```go
type Code int
func (c Code) Error() string { return "code" }
const NotFound = Code(404)

err := NotFound
err == NotFound // TRUE — value type, comparable, same value
```

If the struct contains slices/maps/funcs, it is no longer comparable and `==` is a runtime panic.

### 4.4 The Interface Holds A Copy Of The Value (Sometimes)

For value-typed dynamic types, the interface either (a) stores the value directly in `data` if it fits in a word, or (b) heap-allocates a copy and stores the pointer. The compiler decides based on size.

For pointer-typed dynamic types, the interface stores the pointer directly.

This is invisible to most code but matters for performance: large value-typed errors trigger heap copies on every interface conversion. Prefer pointer types for non-trivial errors.

### 4.5 `Error()` Called On A Nil Receiver

```go
type P struct{}
func (p *P) Error() string {
    if p == nil {
        return "<nil P>"
    }
    return "p"
}
```

A method on a pointer receiver CAN be called on a nil pointer — the method runs with `p == nil`. If the body dereferences `p`, it panics. Defensive bodies handle nil explicitly. (Most Go style guides advise against relying on nil-receiver methods.)

### 4.6 Empty `Error()` String

```go
type emptyErr struct{}
func (emptyErr) Error() string { return "" }

var err error = emptyErr{}
err.Error() // ""
fmt.Println(err) // prints empty line
```

The error is non-nil but its message is empty. Legal, but confusing. Avoid.

---

## 5. Related Specs

The error interface is a thin layer over several deeper specifications:

### 5.1 Interface Types

> https://go.dev/ref/spec#Interface_types
>
> An interface type defines a type set. A variable of interface type can store a value of any type that is in the type set of the interface. Such a type is said to implement the interface.

`error`'s type set is "every type with method `Error() string`". The compiler infers membership; you do not declare it.

### 5.2 Method Sets

> https://go.dev/ref/spec#Method_sets
>
> The method set of a type determines the interfaces that the type implements. The method set of a defined type T consists of all methods declared with receiver type T. The method set of a pointer to a defined type T (where T is neither a pointer nor an interface) is the set of all methods declared with receiver *T or T.

This rule decides whether `T` or `*T` (or both) satisfies `error`.

### 5.3 Predeclared Identifiers

> https://go.dev/ref/spec#Predeclared_identifiers
>
> Types: any, bool, byte, comparable, complex64, complex128, error, float32, float64, int, int8, int16, int32, int64, rune, string, uint, uint8, uint16, uint32, uint64, uintptr

`error` is a type, declared in the universe block. You can shadow it in your own scope (don't), but you cannot avoid it being available without an import.

### 5.4 Constants And Variables

The standard library declares many error sentinels. Their identifiers are exported `var`s, not `const`s, because Go does not allow non-string/non-numeric constants. A sentinel error variable is the standard pattern:

```go
var ErrNotFound = errors.New("not found")
```

### 5.5 The `errors` Package

`https://pkg.go.dev/errors` — declared functions:

```go
func New(text string) error
func Unwrap(err error) error
func Is(err, target error) bool
func As(err error, target any) bool
func Join(errs ...error) error
```

These are not part of the language spec — they are part of the `errors` package's API. Together with the predeclared `error` interface, they define the modern Go error idiom.

### 5.6 The `fmt` Package And `%w`

`https://pkg.go.dev/fmt` — `fmt.Errorf` is documented to recognize the `%w` verb (one and only one per format string) and produce a wrapping error. The wrapping error implements `Unwrap()` so the resulting chain is traversable.

---

## 6. Version History

| Go Version | Release | Change |
|------------|---------|--------|
| 1.0 | 2012-03 | `error` predeclared interface; `errors.New`. |
| 1.13 | 2019-09 | `errors.Is`, `errors.As`, `errors.Unwrap`; `%w` in `fmt.Errorf`. |
| 1.16 | 2021-02 | `os.PathError` aliased to `fs.PathError`; reorganization of fs errors. |
| 1.20 | 2023-02 | `errors.Join` and `Unwrap() []error`; `errors.Is`/`As` traverse multi-unwrap. |
| 1.21 | 2023-08 | Minor stdlib clean-up; behavioral interfaces stable. |
| 1.22 | 2024-02 | No error-related changes. |

The `error` interface itself is unchanged from Go 1.0. All subsequent additions are in the `errors` package and the `fmt` package — non-breaking augmentations to the modeling toolkit.

This stability is intentional. Changing the `error` interface would break every Go program ever written. The Go team has explicitly chosen to extend through helpers and conventions rather than touch the interface.

### 6.1 Pre-Go-1.13 Era

Before `%w` and `errors.Is/As`, the idiom was:

- Sentinel `var ErrX = errors.New("x")` and `err == ErrX`.
- Type assertion `if pe, ok := err.(*PathError); ok { ... }`.
- Third-party libraries (`pkg/errors`, `xerrors`) for wrapping with stack traces.

This idiom still works, but `==` against wrapped errors silently breaks. Modern code uses `errors.Is` everywhere.

### 6.2 Pre-Go-1.20 Era

Multi-error aggregation required hand-rolled types (`hashicorp/go-multierror`, custom `MultiError` slices). `errors.Join` consolidated this into the standard library.

### 6.3 The `error` Future

There is no proposed change to the `error` interface in any Go major-version plan. The community has settled on the iface model with `Unwrap`-based chains. Future additions, if any, will likely be:

- More behavioral interfaces (e.g., a standard `Retryer`).
- More helpers in `errors` for common patterns.
- Better tooling around stack-trace capture without third-party libraries.

---

## 7. Specification Of Surrounding Packages

### 7.1 `errors.New`

```go
// Package errors implements functions to manipulate errors.
//
// New returns an error that formats as the given text.
// Each call to New returns a distinct error value even if the text is identical.
func New(text string) error {
    return &errorString{text}
}
```

The `errorString` struct is unexported. Each `New` call allocates one.

### 7.2 `errors.Unwrap`

```go
// Unwrap returns the result of calling the Unwrap method on err, if err's
// type contains an Unwrap method returning error. Otherwise, Unwrap returns nil.
//
// Unwrap only calls a method of the form "Unwrap() error". In particular Unwrap
// does not unwrap errors returned by [Join].
func Unwrap(err error) error {
    u, ok := err.(interface{ Unwrap() error })
    if !ok {
        return nil
    }
    return u.Unwrap()
}
```

Single-step traversal. To walk the whole chain, loop until nil.

### 7.3 `errors.Is`

```go
// Is reports whether any error in err's chain matches target.
//
// The chain consists of err itself followed by the sequence of errors obtained by
// repeatedly calling Unwrap.
//
// An error is considered to match a target if it is equal to that target or if
// it implements a method Is(error) bool such that Is(target) returns true.
func Is(err, target error) bool {
    // ...
}
```

### 7.4 `errors.As`

```go
// As finds the first error in err's chain that matches target, and if one is found,
// sets target to that error value and returns true. Otherwise, it returns false.
func As(err error, target any) bool {
    // ...
}
```

### 7.5 `errors.Join`

```go
// Join returns an error that wraps the given errors. Any nil error values are
// discarded. Join returns nil if every value in errs is nil.
func Join(errs ...error) error {
    // ...
}
```

---

## 8. Summary

The `error` interface is one of Go's most stable promises:

```go
type error interface {
    Error() string
}
```

That single line has not changed since Go 1.0 and is unlikely to change. Every Go program ever written depends on this exact shape.

Around it, the `errors` package has grown a small toolkit (`Unwrap`, `Is`, `As`, `Join`) for traversing error chains. The `fmt.Errorf` function gained `%w` for wrapping. The standard library's error types (`*PathError`, `*OpError`, `*UnmarshalTypeError`) demonstrate the canonical struct shapes.

When you write Go, you are inheriting the entire history of this design. Following the conventions — lowercase messages, sentinel `Err` variables, custom types ending in `Error`, pointer receivers, `Unwrap()` for wrappers, `errors.Is`/`As` for matching — produces code that fits seamlessly into the broader ecosystem. Deviating from them produces friction at every interface boundary.

---

## 9. Cross-References

- `junior.md` — Introduction, glossary, code examples.
- `middle.md` — Real-world patterns and standard-lib design.
- `senior.md` — Runtime representation and allocation cost.
- `professional.md` — Standard-library cases and lint config.
- `optimize.md` — Allocation-aware error patterns.
- `find-bug.md` — The five mandatory bugs, plus seven more.
- `interview.md` — Twenty-five questions including the five mandated.
- `tasks.md` — Graded exercises with hints and solutions.

Read in order: `junior` → `middle` → `senior` → `professional` → the rest as reference.
