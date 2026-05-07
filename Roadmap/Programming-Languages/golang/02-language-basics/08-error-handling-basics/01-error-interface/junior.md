# The error Interface — Junior Level

## 1. Introduction

### What is it?
In Go there are no exceptions. Functions that can fail return an extra `error` value as the last return result. The caller checks it and decides what to do. The contract is captured by a tiny built-in interface:

```go
type error interface {
    Error() string
}
```

That is the entire definition. Any type that has a method `Error() string` automatically satisfies the `error` interface. There is no class hierarchy, no `throws` keyword, no `try` block. Every error is just a value that flows through normal control flow.

### How to use it?

```go
package main

import (
    "errors"
    "fmt"
)

func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

func main() {
    result, err := divide(10, 0)
    if err != nil {
        fmt.Println("error:", err)
        return
    }
    fmt.Println("result:", result)
}
```

Two things to notice:

1. The error is the LAST return value (Go convention).
2. The caller checks `err != nil` immediately. If it is non-nil, the other return values are typically zero or undefined and must be ignored.

---

## 2. Prerequisites

Before reading this topic, you should be comfortable with:

- **Functions and multiple return values** (2.6.1, 2.6.2). Errors ride alongside normal results.
- **Methods on user-defined types** (struct methods). A custom error is a type with one method.
- **Interfaces and interface satisfaction** (any type with `Error() string` IS an `error`).
- **Pointers and pointer receivers** (matters for the famous nil-interface bug below).
- **`fmt.Println` and string formatting** (`fmt.Errorf` is just `Sprintf` plus a wrapper).

If interfaces feel hazy, re-read 2.7 (Interfaces) first. Errors are the simplest interface in the entire standard library and a great way to understand interface satisfaction in practice.

---

## 3. Glossary

| Term | Definition |
|------|------------|
| `error` | The built-in interface `interface { Error() string }`. Predeclared identifier. |
| `Error()` | The single method that any error value must implement. Must return a `string`. |
| sentinel error | A package-level error variable used as a stable, comparable identity, e.g. `io.EOF`. |
| custom error | A user-defined type (usually a struct) that implements `Error() string`. |
| `errors.New` | Constructor that returns a basic error wrapping a string. |
| `fmt.Errorf` | `Printf`-style error constructor. Without `%w` it produces a plain string error. |
| `errorString` | The unexported struct used internally by `errors.New`. |
| interface satisfaction | A type satisfies `error` automatically when it has the `Error() string` method. |
| nil interface | An interface value whose type tag AND data are both nil. Different from "an interface holding a typed nil pointer". |
| iface tuple | The two-word `(type, data)` representation of an interface value at runtime. |
| value receiver | `func (e MyError) Error() string`. Both `MyError` and `*MyError` satisfy `error`. |
| pointer receiver | `func (e *MyError) Error() string`. Only `*MyError` satisfies `error`. |
| zero value | The default value (`nil` for `error`). |
| short error string | The text returned by `Error()`. Convention: lowercase, no trailing punctuation. |

---

## 4. Core Concepts

### 4.1 The Built-in `error` Type

`error` is one of Go's predeclared identifiers, just like `int`, `string`, or `len`. It lives in the `builtin` pseudo-package:

```go
// src/builtin/builtin.go
type error interface {
    Error() string
}
```

The actual definition is in the runtime, but `builtin.go` is the documentation source. Three crucial facts:

1. `error` is an interface, so `var e error` defaults to `nil`.
2. The only required method is `Error() string`. No `Code()`, no `Cause()`, no kitchen sink.
3. Any concrete type with that method automatically satisfies the interface — no `implements` keyword.

The smallest possible custom error type is a single line:

```go
type myErr struct{}
func (myErr) Error() string { return "boom" }
```

That's it. `myErr{}` now satisfies `error`.

### 4.2 `errors.New` — The Simplest Constructor

```go
package main

import (
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("not found")

func find(id int) error {
    if id < 0 {
        return ErrNotFound
    }
    return nil
}

func main() {
    if err := find(-1); err != nil {
        fmt.Println(err) // not found
    }
}
```

Under the hood `errors.New` is two lines from `src/errors/errors.go`:

```go
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

Three things to remember:

1. `errors.New` returns a POINTER (`*errorString`). Two separate calls produce two different pointers, even with the same text — they are NOT equal under `==`.
2. The text should be lowercase and without trailing punctuation, by Go convention.
3. Sentinel error variables are usually declared at package scope as `var ErrXxx = errors.New("...")`. The `Err` prefix is idiomatic.

### 4.3 `fmt.Errorf` Without `%w` — Just String Formatting

`fmt.Errorf` is the second go-to constructor when you want interpolated values:

```go
package main

import (
    "fmt"
    "os"
)

func openConfig(path string) error {
    if _, err := os.Stat(path); err != nil {
        return fmt.Errorf("config file %q not readable: %v", path, err)
    }
    return nil
}
```

Without the `%w` verb, `fmt.Errorf` is exactly equivalent to `errors.New(fmt.Sprintf(...))`. The resulting error has only the formatted text — the inner `err` is lost as a value (only its `%v` rendering remains).

When you want to preserve the inner error so callers can later inspect it with `errors.Is` or `errors.As`, you use `%w` — that is the next topic. For now, just use `%v` and treat the result as a flat string error.

### 4.4 Defining a Custom Error Type

A custom error type lets you attach extra data: an operation name, a path, an HTTP status code, a numeric code, retry hints, anything.

```go
package main

import "fmt"

type FileError struct {
    Op   string
    Path string
    Err  error
}

func (e *FileError) Error() string {
    return fmt.Sprintf("%s %s: %v", e.Op, e.Path, e.Err)
}

func openFile(path string) error {
    return &FileError{Op: "open", Path: path, Err: fmt.Errorf("permission denied")}
}

func main() {
    err := openFile("/etc/shadow")
    fmt.Println(err) // open /etc/shadow: permission denied
}
```

Key points:

- `*FileError` satisfies `error` because `Error()` is defined on the pointer receiver.
- Returning `&FileError{...}` is the common style — it lets the caller mutate (rare) and avoids copying the struct.
- The struct fields are exported so callers can type-assert and inspect them.

This is exactly the shape used by the standard library: `os.PathError`, `net.OpError`, `*url.Error`, and `json.UnmarshalTypeError` all follow this pattern.

### 4.5 Pointer Receiver vs Value Receiver

The choice of receiver controls which type satisfies `error`:

```go
type V struct{ s string }
func (v V) Error() string { return v.s }  // value receiver: BOTH V and *V satisfy error

type P struct{ s string }
func (p *P) Error() string { return p.s } // pointer receiver: ONLY *P satisfies error
```

If you choose pointer receiver, `P{s: "x"}` (value) does NOT satisfy `error`. You must always use `&P{...}` or take the address. Most standard-library error types use pointer receivers because they hold meaningful state and you want a single canonical address per error.

Convention:

- Use **pointer receiver** for non-trivial error structs (multi-field, holding `error` causes).
- Use **value receiver** only for tiny immutable error types (like an `enum` of error codes).

### 4.6 The Famous Nil-Interface Bug

This bites every Go newcomer at least once.

```go
package main

import "fmt"

type MyErr struct{ msg string }

func (e *MyErr) Error() string { return e.msg }

func work() error {
    var err *MyErr // typed nil pointer
    return err     // returns an interface containing (type=*MyErr, data=nil)
}

func main() {
    if err := work(); err != nil {
        fmt.Println("got error:", err)
    } else {
        fmt.Println("no error")
    }
}
```

Output:

```
got error: <nil>
```

Even though `err` was a nil pointer, the returned `error` interface is NOT nil — because an interface value is `nil` only when BOTH its type tag and data pointer are nil. Returning a typed nil pointer fills the type slot, so `err != nil` is true.

The fix: never return a typed nil pointer as an `error`. Either return the untyped `nil` directly, or use a plain `error` variable:

```go
func work() error {
    var err error // interface, default nil
    return err    // truly nil
}
```

We will explore this in depth in `find-bug.md` and `interview.md`.

---

## 5. Real-World Analogies

### 5.1 Errors as Parcels

Think of an `error` as a parcel handed back to the caller. The label (the `Error()` string) is what's printed on the outside. The contents (the underlying type with its fields) is what's inside, accessible by opening (type assertion or `errors.As`).

A `nil` error is "no parcel". A typed nil pointer wrapped in an interface is "an empty box with a label" — the caller still sees a parcel.

### 5.2 Error as a Receipt

When a function fails it gives you a receipt that describes what happened. The receipt is just a string by default. If you need structured fields (transaction ID, retry hint), use a custom error type — a richer receipt.

---

## 6. Common Mistakes

### 6.1 Comparing Errors with `==`

```go
err1 := errors.New("oops")
err2 := errors.New("oops")
fmt.Println(err1 == err2) // false, different pointers!
```

Two `errors.New` calls always produce distinct values. Use sentinel variables (a single `var ErrX = errors.New("x")`) or `errors.Is` (next topic).

### 6.2 Capitalizing Error Strings

`errors.New("File Not Found")` violates the convention. Errors get embedded in larger messages — `"open foo: File Not Found"` reads badly. Always lowercase, no trailing punctuation:

```go
errors.New("file not found") // good
errors.New("File not found.") // bad
```

### 6.3 Returning a Typed Nil Pointer

Already covered above — the nil-interface bug.

### 6.4 Putting `Error()` on a Pointer Receiver but Returning the Value Type

```go
type E struct{}
func (e *E) Error() string { return "x" }

func bad() error {
    return E{} // compile error: E does not implement error (Error has pointer receiver)
}
```

Either return `&E{}` or move the method to a value receiver.

### 6.5 Ignoring Errors

```go
result, _ := riskyOp() // dangerous
```

The blank identifier silences the error. In production code you almost never want this. Lint rules like `errcheck` exist exactly to catch it.

### 6.6 Using Panic Instead of Returning an Error

`panic` is for programmer mistakes (nil dereference, out-of-bounds, impossible state). For expected failures (file missing, network down, bad input) ALWAYS return an `error`. We cover panic vs error trade-offs in topic 5 of this section.

### 6.7 Mixing String Concatenation Into `Error()`

```go
func (e *MyErr) Error() string {
    return "operation " + e.Op + " failed: " + e.Cause.Error()
}
```

Allocates twice on each call. Prefer `fmt.Sprintf` once, or build with `strings.Builder` if hot. We cover this in `optimize.md`.

### 6.8 Forgetting That `errors.New` Returns A Pointer

The fact that `errors.New` returns `*errorString` matters for two things: (a) you can compare a sentinel error to itself with `==` because the pointer is stable; (b) you cannot compare two independent constructions even with the same string.

---

## 7. Mini Exercises

Each exercise is self-contained. Solutions appear in `tasks.md` and on the file index.

### Exercise 1 — Smallest Error

Write the smallest possible custom error type that returns the string `"boom"` from `Error()`. Then use it from `main`.

<details>
<summary>Hint</summary>

You don't need any fields. An empty struct is enough.

</details>

### Exercise 2 — divide

Write `divide(a, b float64) (float64, error)` returning an error with text `"division by zero"` when `b == 0`.

### Exercise 3 — A `LineColumnError`

Define a struct with `Line` and `Column` ints and a `Msg` string. Implement `Error()` returning `"<line>:<col>: <msg>"`. Use it from a function `parse(input string) error` that returns a `&LineColumnError{...}` whenever input is empty.

### Exercise 4 — Sentinel Error

Define `var ErrNotFound = errors.New("not found")`. Write a function `find(id int) error` that returns `ErrNotFound` when `id < 0`. In `main`, compare the returned error to `ErrNotFound` using `==`.

### Exercise 5 — Reproduce the Nil-Interface Bug

Write a function that returns a `*MyErr` typed nil and shows in `main` that `err != nil` is unexpectedly true. Then fix it.

### Exercise 6 — Format vs Wrap

Use `fmt.Errorf("read %s: %v", path, ioErr)` to format a non-wrapping error. Print the result and convince yourself the inner error is gone as a value (only its text remains).

---

## 8. Cheat Sheet

| Task | Code |
|------|------|
| Return a simple error | `return errors.New("not found")` |
| Format an error | `return fmt.Errorf("open %s: %v", path, err)` |
| Define a sentinel | `var ErrX = errors.New("x")` |
| Smallest custom error | `type e struct{}; func (e) Error() string { return "x" }` |
| Custom error with fields | `type FileErr struct{Op, Path string; Err error}` |
| Return no error | `return nil` |
| Check for an error | `if err != nil { ... }` |
| The "no error" sentinel value | The literal `nil` |

### Quick Reference: Error Conventions

- Lowercase, no trailing punctuation. `"not found"`, never `"Not found."`.
- The error is the LAST return value.
- Sentinel error names start with `Err`: `ErrNotFound`, `io.EOF`.
- Custom error type names end with `Error`: `PathError`, `OpError`, `UnmarshalTypeError`.
- Don't ignore errors with `_`. If you really must, leave a comment explaining why.

### Quick Reference: The Five Most Common Errors at This Level

1. Returning a typed nil pointer through an `error` interface.
2. Comparing two `errors.New("x")` results with `==`.
3. Capitalizing or punctuating the error string.
4. Putting `Error()` on a pointer receiver and returning the value type.
5. Forgetting that `fmt.Errorf` without `%w` loses the inner error as a value.

---

## 9. Visual Summary

```
+----------------+
| error          |  <- predeclared interface
| ----------     |
| Error() string |
+----------------+
        ^
        |  satisfied by ANY type with Error() string
        |
+----------------+  +-------------------+  +----------------+
| *errorString   |  | *FileError        |  | MyValueErr     |
| (errors.New)   |  | (custom struct)   |  | (value rcv)    |
+----------------+  +-------------------+  +----------------+
```

The interface is at the top. The concrete types underneath all live below it and substitute for `error` wherever it is expected.

---

## 10. Walkthrough: Reading A Realistic Error Handling Loop

Let's walk through the most common shape of error-handling code you will write or read every day:

```go
func loadUser(id int) (*User, error) {
    row, err := db.QueryRow("SELECT id, name FROM users WHERE id = ?", id)
    if err != nil {
        return nil, fmt.Errorf("loadUser %d: query: %v", id, err)
    }

    var u User
    if err := row.Scan(&u.ID, &u.Name); err != nil {
        return nil, fmt.Errorf("loadUser %d: scan: %v", id, err)
    }

    return &u, nil
}

func handler(w http.ResponseWriter, r *http.Request) {
    id, err := strconv.Atoi(r.URL.Query().Get("id"))
    if err != nil {
        http.Error(w, "bad id", http.StatusBadRequest)
        return
    }

    u, err := loadUser(id)
    if err != nil {
        log.Printf("handler: %v", err)
        http.Error(w, "internal error", http.StatusInternalServerError)
        return
    }

    fmt.Fprintf(w, "user: %s\n", u.Name)
}
```

Break it down:

1. `db.QueryRow` returns an error. We check it.
2. We FORMAT a new error with `fmt.Errorf`, prefixing context (`"loadUser <id>: query: ..."`).
3. We return early on each failure. The remaining code only runs on the happy path.
4. The handler builds its OWN context (`"handler: ..."`) before logging.
5. The handler decides how to expose the error to the user (a generic 500). It never returns the raw internal error to the client.

This pattern is universal in Go production code:

- Each layer adds context on the way out.
- Errors propagate up; logs happen at the boundary.
- Internal errors do not leak to external interfaces (HTTP, gRPC, CLI).

---

## 11. Idiomatic Patterns

A few idioms you will see frequently:

### 11.1 Early-Return Style

```go
v, err := step1()
if err != nil {
    return err
}
w, err := step2(v)
if err != nil {
    return err
}
return finish(w)
```

Clear, linear, testable. Avoid nesting — every error returns immediately.

### 11.2 Wrapping With Context

```go
if err := step(); err != nil {
    return fmt.Errorf("step: %w", err)
}
```

We will cover `%w` in topic 04. For now, know that `%w` preserves the inner error so callers can still match against sentinels, while adding a human-readable prefix.

### 11.3 Sentinel Comparison

```go
if err == io.EOF {
    break
}
```

For UN-wrapped errors. Note: `io.EOF` is a sentinel, hence comparable by `==`. Most other production code uses `errors.Is`.

### 11.4 Returning Both Result And Error

```go
func parse(s string) (int, error) {
    n, err := strconv.Atoi(s)
    if err != nil {
        return 0, fmt.Errorf("parse %q: %v", s, err)
    }
    return n, nil
}
```

When the function fails, the result is zero. Callers ignore the result on error.

### 11.5 Defer For Cleanup

```go
f, err := os.Open(path)
if err != nil {
    return err
}
defer f.Close()
// use f
```

The `Close` happens regardless of how the function exits — including when subsequent calls return an error.

---

## 12. Frequently Asked Beginner Questions

**Why doesn't Go have exceptions?** Because explicit error returns make every failure path visible at the call site. Exceptions hide them. Go optimizes for code that humans (and tools) can audit linearly.

**Is `panic` the same as throwing?** No. `panic` exists for programmer mistakes and unrecoverable invariant violations. Routine failures (file missing, network down) are NEVER panics — they are `error` returns.

**Why are error strings lowercase?** Because errors compose into chains. `"step1: step2: not found"` reads better than `"step1: step2: Not found."`.

**Why does the interface have only one method?** To keep the bar low. Every type that already implements `Error() string` automatically becomes an error. Adding methods would break this.

**Why is the error returned LAST?** Because it lets `if err != nil` follow the result naturally. `result, err := fn()` reads "give me result, then the error".

**Can I return multiple errors at once?** Yes — return them as a slice or use `errors.Join` (Go 1.20+). We cover this in middle level.

**What happens if I forget to check an error?** Nothing at compile time. The result variable will be zero/invalid and your program may misbehave silently. The `errcheck` linter exists to catch this.

**What if I don't care about the error?** Use the blank identifier: `result, _ := fn()`. But understand: this is rarely correct. Document why if you must.

**What if I want a stack trace?** The standard error has none. Use a third-party library (`github.com/cockroachdb/errors`) or capture `runtime/debug.Stack()` manually. We discuss in middle level.

**Should I create a custom error type for every failure?** No. Most failures are fine as `errors.New("...")` or `fmt.Errorf("...")`. Create a custom type only when callers need to branch on the error or read structured fields.

---

## 13. Practice: Reading A Standard-Library Error

Open `src/errors/errors.go` in the Go source. The whole file is shorter than this section. Notice:

```go
// Package errors implements functions to manipulate errors.
package errors

// New returns an error that formats as the given text.
// Each call to New returns a distinct error value even if the text is identical.
func New(text string) error {
    return &errorString{text}
}

// errorString is a trivial implementation of error.
type errorString struct {
    s string
}

func (e *errorString) Error() string {
    return e.s
}
```

That's the entire core of `errors.New`. Five lines of real code. Read this file once, slowly. Most of Go's error machinery is just as simple.

Next, open `src/io/fs/fs.go` and find `PathError`. Read its `Error()`, `Unwrap()`, and `Timeout()` methods. This is the canonical wrapper shape and you will copy it for many of your own error types.

Reading the standard library is the fastest way to internalize idiomatic Go. The error code is a great starting point because it is so small.

---

## 14. Summary Table

| Concept | Code | Notes |
|---------|------|-------|
| Built-in interface | `type error interface { Error() string }` | Predeclared, no import |
| Simplest constructor | `errors.New("text")` | Returns `*errorString` |
| Formatted error | `fmt.Errorf("ctx: %v", err)` | Without `%w` no chain |
| Wrapping | `fmt.Errorf("ctx: %w", err)` | Preserves chain (topic 04) |
| Sentinel | `var ErrX = errors.New("x")` | Stable identity |
| Custom type | `type FErr struct{...}; func (e *FErr) Error() string {...}` | Pointer receiver default |
| Smallest type | `type T struct{}; func (T) Error() string { return "x" }` | Value receiver, no fields |
| Nil error | `return nil` | Untyped nil through interface |
| Check error | `if err != nil { ... }` | Always check |
| Compare sentinel | `if err == ErrX` (no wrap) or `errors.Is(err, ErrX)` | Topic 03 |
| Extract type | `errors.As(err, &target)` | Topic 03 |

---

## 15. Next Steps

You now know:

- Why Go's error model is just an interface.
- How `errors.New`, `fmt.Errorf`, and custom error types fit together.
- The pointer-vs-value receiver trade-off.
- The famous nil-interface bug and how to avoid it.
- The idiomatic patterns for layered error handling.

Next, in topic **02-sentinel-errors**, you will learn how to build packages around stable error identities (`io.EOF`, `os.ErrNotExist`, etc.) and the trade-offs of exposing them as part of a public API.

Then **03-errors-is-as** introduces the proper way to compare and unwrap errors — replacing naive `==` comparisons that, as you have already seen, are full of foot-guns.

After that, **04-error-wrapping-percent-w** formalizes the `%w` verb, and **05-panic-and-recover** explains when (rarely) you should panic instead of returning an error.

By the end of this section, you will be writing error handling code that fits cleanly into any Go codebase.
