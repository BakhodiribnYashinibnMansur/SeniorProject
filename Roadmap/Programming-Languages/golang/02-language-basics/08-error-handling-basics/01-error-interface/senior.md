# The error Interface — Senior Level

## 1. Overview

Senior-level mastery of `error` means a precise mental model of how the interface is laid out at runtime, what it costs to construct and propagate, and how the compiler decides where the underlying error value lives. You also understand the three subtle lifecycle issues every error abstraction inherits: identity (`==`), wrapping (`Unwrap`), and method-set satisfaction (pointer vs value receivers).

This document explains:

1. The runtime representation of `error` as an `iface`.
2. The role of the `itab` (interface table) for an error's concrete type.
3. The allocation cost of `errors.New("...")` calls and when the compiler can elide them.
4. Stable-identity patterns (compile-time error registration).
5. The interaction between `error` interfaces and escape analysis.

---

## 2. Runtime Representation: `error` Is An `iface`

In Go's runtime, every interface value with non-empty method set is represented by a two-word `iface` struct:

```go
// runtime/runtime2.go (simplified)
type iface struct {
    tab  *itab          // type metadata for the concrete type
    data unsafe.Pointer // pointer to (or value of, for word-sized) the underlying value
}
```

For the empty interface (`interface{}` / `any`) there is `eface`:

```go
type eface struct {
    _type *_type
    data  unsafe.Pointer
}
```

Because `error` has one method (`Error() string`), it uses `iface`. The `itab` carries:

- The interface type descriptor (`*interfacetype`, here `error`).
- The concrete dynamic type descriptor (`*_type`, e.g. `*errorString`).
- A small array of method function pointers, here just one pointer to the concrete `Error()` implementation.

When you write `var err error = errors.New("x")`, the compiler:

1. Allocates an `errorString` on the heap (`errors.New` always heap-allocates because the result escapes).
2. Constructs (or reuses) an `itab` mapping `*errorString` to `error`.
3. Stores the `itab` pointer in `iface.tab` and the heap pointer in `iface.data`.

`itab` instances are cached. A program with thousands of distinct error types still has only one `itab` per (concrete type, interface) pair.

### 2.1 Why `err == nil` And The Famous Bug Are Now Obvious

The condition `err == nil` is true only when BOTH `tab == nil` AND `data == nil`.

```go
var err error // {tab: nil, data: nil} -> nil interface
err == nil // true

var p *MyErr // *MyErr typed nil pointer
err = p      // {tab: itab(*MyErr,error), data: nil}
err == nil   // false! tab is non-nil
```

Returning a typed nil pointer through `error` fills `tab`, leaving `data` nil. The interface is no longer nil. This is not a Go bug — it is a direct consequence of the iface representation. We exploit and break this in `find-bug.md`.

### 2.2 Method Dispatch Through The `itab`

A call `err.Error()` compiles roughly to:

```
load itab from err.tab
load function pointer from itab.fun[0]
load data from err.data
call fn(data)
```

This is a single indirect call. The CPU's branch predictor handles it well; in tight loops the cost is dominated by the called function, not the dispatch. But the indirection means small concrete error types do not get inlined when called through the interface — important when designing hot paths (see `optimize.md`).

---

## 3. Allocation Cost Of `errors.New`

The critical question for hot paths: how many heap allocations does each `errors.New("x")` cost?

```go
// src/errors/errors.go
func New(text string) error {
    return &errorString{text}
}
```

Naively, two allocations per call:

1. The `errorString` struct (16 bytes: pointer + length for the string field).
2. The interface boxing — wrapping `*errorString` into the `error` iface.

In modern Go (1.16+) the second is typically not a separate allocation: the pointer is stored directly in `iface.data`, so the interface conversion is free at runtime. The `errorString` itself, however, escapes to the heap because `errors.New` returns a pointer that outlives the function.

A canonical benchmark:

```go
func BenchmarkErrorsNew(b *testing.B) {
    for i := 0; i < b.N; i++ {
        _ = errors.New("oops")
    }
}
// 1 alloc/op, 16 B/op typical
```

Implication: hot retry loops or per-request error returns that call `errors.New` thousands of times allocate thousands of `errorString` values. The standard fix is sentinel variables — declared once, reused forever:

```go
var ErrNotFound = errors.New("not found") // 1 alloc at package init
```

Compare:

```go
// hot path, costly:
return errors.New("not found") // alloc per call

// hot path, cheap:
return ErrNotFound // no alloc, identity stable
```

This is the foundation of the optimization guidance in `optimize.md`.

### 3.1 String Interning Is Not Automatic

Two `errors.New("x")` calls allocate two distinct `errorString` structs even though the strings are identical. There is no string interning. The pointers differ, so `==` returns false. This is one of the five mandated find-bug entries.

### 3.2 `fmt.Errorf` Without `%w` Is Heavier

```go
fmt.Errorf("read %s: %v", path, ioErr)
```

This allocates:

1. A `[]any` slice for varargs (escape).
2. The internal `fmt.pp` buffer (often pooled, but the formatted string escapes).
3. The final `errorString` (or `wrapError` if `%w` is used).

Net cost is several allocations per call. Hot paths that call `fmt.Errorf` per request can show measurably in profiles. The optimization is precomputed sentinels or pre-formatted error templates.

### 3.3 Compile-Time Allocation Elision

The compiler can sometimes prove a closure or interface value does not escape, and place it on the stack. For `errors.New` the call boundary always escapes (it returns the pointer). But for inline error structs that you yourself control:

```go
type localErr struct{ code int }
func (e *localErr) Error() string { ... }

func f() error {
    e := localErr{code: 1}
    if cond {
        return &e // escape to heap; can NOT be elided
    }
    return nil
}
```

If you take `&e` and return it, the struct must live on the heap. The `-gcflags=-m` flag prints `e escapes to heap` for these cases. There is no general-purpose way to "stack-allocate an error" the caller can see — by definition the caller outlives the callee.

---

## 4. Compile-Time Error Registration

Some packages declare ALL their public errors at package scope, registering them once at init time. This pattern has three big benefits:

1. **One allocation total** for the lifetime of the program per error.
2. **Stable identity**: `==` works because callers compare pointer values from the same global.
3. **Discoverability**: `grep '^var Err' pkg/*.go` lists the entire error API.

```go
package userdb

import "errors"

var (
    ErrUserNotFound      = errors.New("user not found")
    ErrEmailDuplicate    = errors.New("email already registered")
    ErrPasswordTooShort  = errors.New("password too short")
    ErrAccountSuspended  = errors.New("account suspended")
)
```

The trade-off is breaking changes: once `ErrUserNotFound` is exported, callers may compare against it and rely on the exact identity. Renaming or splitting it later is a major-version change.

### 4.1 Errors With Data Cannot Be Sentinels

A sentinel must be a single shared value. If your error needs per-call data (a path, an id), it cannot be a sentinel — it must be a struct constructed each call. The trick is: you can still use a sentinel for the IDENTITY (so callers can `errors.Is`) and a struct wrapper for the DATA:

```go
var ErrNotFound = errors.New("not found")

type LookupError struct {
    Key string
    Err error // typically wraps ErrNotFound or other
}
func (e *LookupError) Error() string { return fmt.Sprintf("lookup %q: %v", e.Key, e.Err) }
func (e *LookupError) Unwrap() error { return e.Err }

return &LookupError{Key: k, Err: ErrNotFound}
```

The wrapper allocates per call, but the inner sentinel is shared. Callers do `errors.Is(err, ErrNotFound)` to check the identity and `errors.As(err, &le)` to get the key.

### 4.2 Comparable Concrete Error Types

A struct error that is **comparable** (no slice/map/func fields) can itself act as a sentinel-like value:

```go
type Code struct{ N int }
func (c Code) Error() string { return fmt.Sprintf("code %d", c.N) }

var (
    ErrCode1 = Code{N: 1}
    ErrCode2 = Code{N: 2}
)

err := ErrCode1
err == ErrCode1 // true! struct equality
```

Because `Code` is comparable and uses a value receiver, the interface holds the value (or a pointer to a copy) and equality works. This pattern is rare in production but useful for tightly-scoped numeric error codes.

---

## 5. Method Sets And Receiver Choice

The choice of receiver decides which type satisfies `error`:

| Method on | Satisfied by `T` | Satisfied by `*T` |
|-----------|------------------|-------------------|
| `func (T)`  | yes | yes |
| `func (*T)` | no  | yes |

Senior consequence:

```go
type MyErr struct{}
func (e *MyErr) Error() string { return "x" }

// In callers:
var err error
err = MyErr{}    // compile error: MyErr does not implement error
err = &MyErr{}   // ok
```

If your custom error type holds non-trivial state, use pointer receivers AND always return `&MyErr{...}`. If you accidentally write `MyErr{}` somewhere, you get a clear compile error — much better than a runtime mystery.

### 5.1 Embedding Errors

Embedding lets you compose:

```go
type Base struct{ Op string }
func (b Base) Error() string { return b.Op + ": ?" }

type Detailed struct {
    Base
    Cause error
}
func (d Detailed) Error() string { return d.Op + ": " + d.Cause.Error() }
```

Inner `Error()` is shadowed by outer `Error()` because Go resolves promoted methods by the most-derived type. This is occasionally useful for layered error packages but is uncommon — most teams prefer composition by field over embedding.

### 5.2 The Stringer Trap

If you define `String() string` instead of `Error() string`, your type satisfies `fmt.Stringer` but NOT `error`. The compiler will silently let you use it with `fmt.Println` (because `fmt.Println` calls `String()` on stringers) but will NOT accept it as an `error`. Always implement `Error()`, not `String()`, for error types.

---

## 6. The `iface` And `errors.Is` / `errors.As` Internals

Although the next topic covers `errors.Is`/`As` in depth, here's the senior view of why they are necessary:

- `==` compares iface tuples directly: same `tab` AND same `data`. For two pointer-receiver errors with the same fields, `==` returns false because `data` is different pointers. (The exception: comparable struct values returned by value, like `io.EOF`'s `*errorString`, share `tab` and `data` because the variable is the SAME.)
- `errors.Is(err, target)` walks the chain via `Unwrap()` and checks each link with `==` (or with the target's optional `Is(error) bool` method).
- `errors.As(err, &target)` walks the chain checking if any link is type-assertable to `*target`.

Both tools rely on the iface metadata: `Is` reads `tab` for the `Is(error) bool` method check; `As` reads `tab` to compare types.

The senior takeaway: comparing error values directly with `==` is fragile in any code where wrapping is possible. Use `errors.Is` everywhere except when comparing to local-only, never-wrapped sentinels.

---

## 7. Performance Patterns At Scale

A few patterns repeated across high-QPS services:

### 7.1 Prebuilt Error Pool

```go
var (
    errBadInput     = errors.New("bad input")
    errRateLimited  = errors.New("rate limited")
    errInternal     = errors.New("internal")
)

func handle(req Request) error {
    if !valid(req)  { return errBadInput }
    if !accept(req) { return errRateLimited }
    if err := do(req); err != nil {
        log.Printf("op failed: %v", err)
        return errInternal
    }
    return nil
}
```

Zero allocations on the error path. The downside is information loss — the caller cannot pull out structured fields. Use this when error semantics are coarse-grained and the call site is hot.

### 7.2 Cached Per-Code Error Constructors

```go
type code int
var codeErrs = map[code]error{
    101: errors.New("code 101: bad request"),
    102: errors.New("code 102: timeout"),
    ...
}
func errFor(c code) error { return codeErrs[c] }
```

Trade memory for runtime cost. Suitable when the set of errors is finite and known.

### 7.3 Avoid `fmt.Errorf` In Hot Paths

`fmt.Errorf` does a printf-style format with allocations. Replace with `errors.New(constString)` where possible.

### 7.4 Avoid `Error()` Allocations

A custom error type that builds its message in `Error()`:

```go
func (e *MyErr) Error() string {
    return fmt.Sprintf("%s %s: %v", e.Op, e.Path, e.Err) // allocates
}
```

If the same error is logged multiple times (or stringified inside `errors.Is` traversals), `Error()` runs each time. Cache it:

```go
type MyErr struct {
    Op, Path string
    Err      error
    msg      string // memoized
}

func (e *MyErr) Error() string {
    if e.msg == "" {
        e.msg = e.Op + " " + e.Path + ": " + e.Err.Error()
    }
    return e.msg
}
```

(Note: this is not goroutine-safe; if errors cross goroutine boundaries before `Error()` is first called, build the string in the constructor instead.)

---

## 8. Interaction With Escape Analysis

A custom error escapes to the heap if and only if the interface conversion captures its address. Almost every real error type does this because:

- `Error()` is on a pointer receiver.
- Returning the error means returning a pointer.
- The pointer escapes the function's frame.

Escape analysis output (`go build -gcflags=-m=2`) typically shows:

```
./x.go:12:6: &MyErr{...} escapes to heap
./x.go:12:6: &MyErr{...} flow: err = &MyErr{...}
```

There is no realistic way to keep an error on the stack across the function boundary. The escape is fundamental, not a missed optimization.

The cost is measurable in microbenchmarks but rarely matters at the application level. Where it does matter: ultra-low-latency code (e.g., serdes, tight retry loops). The fix is sentinels (pre-allocated globals), not stack allocation.

---

## 9. Worked Investigation: Why Two `errors.New` Calls Differ

Concrete code:

```go
e1 := errors.New("oops")
e2 := errors.New("oops")
fmt.Println(e1 == e2) // false
```

What the runtime sees:

| Step | e1 iface | e2 iface |
|------|----------|----------|
| 1. New() called | e1.data = ptr1 (heap addr A), e1.tab = T | e2.data = ptr2 (heap addr B), e2.tab = T |
| 2. compare e1 == e2 | tabs equal, data pointers differ -> false | |

Even though the strings are the same byte content, `e1.data` and `e2.data` are different `*errorString` heap pointers. The runtime equality check on iface compares `data` directly when the dynamic type is a pointer type. So: false.

Senior consequence: never rely on text-content equality for error identity. Use a single sentinel.

---

## 10. Summary

| Concept | Senior insight |
|---------|----------------|
| `error` runtime layout | iface = (tab, data); 16 bytes |
| nil-interface bug | typed-nil pointer fills tab; `== nil` is false |
| `errors.New` cost | 1 alloc per call; share with sentinels |
| `fmt.Errorf` cost | several allocs per call; avoid in hot loops |
| Method receiver | pointer receiver -> only `*T` satisfies error |
| `==` on errors | compares iface tuples; fragile when wrapped |
| `errors.Is/As` | walks `Unwrap()` chain; preferred over `==` |
| Compile-time registration | sentinels at package scope; once-allocated, stable identity |
| Escape behavior | error structs always escape; sentinels avoid per-call cost |

The senior-level toolkit for error design boils down to: choose stable identities (sentinels), pick pointer receivers for non-trivial types, use `errors.Is/As` for matching, avoid `fmt.Errorf` in hot paths, and document every exported error as part of the public API.

---

## 11. Where To Read The Source

| File | What you'll see |
|------|------------------|
| `src/builtin/builtin.go` | Documentation declaration of `error` |
| `src/errors/errors.go` | `New`, `errorString`, `Unwrap`, `Is`, `As`, `Join` |
| `src/errors/wrap.go` | The wrapping helpers used by `errors.Is/As` |
| `src/runtime/iface.go` | `getitab`, `convT2I`, `assertI2T` — interface conversion |
| `src/runtime/runtime2.go` | The `iface` and `eface` struct declarations |
| `src/fmt/errors.go` | `fmt.Errorf` and the `wrapError` / `wrapErrors` types for `%w` |

Read these in order. The runtime mechanics are short; the `errors` package is shorter still. Together they explain every behavior described above.

---

## 12. Next Steps

`professional.md` widens the lens to real-world OSS error types in `os`, `net`, `encoding/json`, and `net/url`. `optimize.md` quantifies the allocation costs we discussed and shows pooling and elision techniques. `find-bug.md` packages all the foot-guns into 8–12 buggy snippets you can review in a coding interview.
