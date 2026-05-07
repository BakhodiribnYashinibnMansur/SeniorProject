# The error Interface — Tasks

## Instructions

Each task includes a description, starter code, expected output, and an evaluation checklist. Difficulty: Easy, Medium, Hard, Extra-hard. Hints and full solutions are inside `<details>` blocks so you can attempt without spoilers.

Treat each task as a small independent program (`go run task.go`). Where benchmarks are mentioned, use `go test -bench=. -benchmem`.

---

## Task 1 — Smallest Custom Error (Easy)

**Topic**: Custom error type with no fields.

**Description**: Define the smallest possible error type that returns the string `"boom"` from `Error()`. Use it from `main` and print it.

**Starter Code**:

```go
package main

import "fmt"

// TODO: define type and its Error() method.

func main() {
    var err error // assign your error to this
    // err = ?
    fmt.Println(err)
}
```

**Expected Output**:

```
boom
```

**Evaluation Checklist**:

- [ ] Type implements `Error() string`.
- [ ] No fields needed.
- [ ] Compiles without warnings.

<details>
<summary>Hint</summary>

An empty struct works. `func (T) Error() string { return "boom" }` is enough.

</details>

<details>
<summary>Solution</summary>

```go
package main

import "fmt"

type boom struct{}
func (boom) Error() string { return "boom" }

func main() {
    var err error = boom{}
    fmt.Println(err)
}
```

</details>

---

## Task 2 — `divide` Function (Easy)

**Topic**: Returning errors from a function.

**Description**: Implement `divide(a, b float64) (float64, error)`. Return an error with text `"division by zero"` when `b == 0`. Otherwise return the quotient and nil.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
)

func divide(a, b float64) (float64, error) {
    // TODO
    return 0, nil
}

func main() {
    if r, err := divide(10, 2); err != nil {
        fmt.Println(err)
    } else {
        fmt.Println(r)
    }
    if _, err := divide(1, 0); err != nil {
        fmt.Println(err)
    }
}
```

**Expected Output**:

```
5
division by zero
```

**Evaluation Checklist**:

- [ ] Error string is exactly `"division by zero"`.
- [ ] Successful path returns `(quotient, nil)`.
- [ ] No `panic`.

<details>
<summary>Solution</summary>

```go
func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}
```

</details>

---

## Task 3 — `LineColumnError` (Easy)

**Topic**: Custom error type with fields.

**Description**: Define `LineColumnError` with int fields `Line`, `Column` and string field `Msg`. Implement `Error()` returning `"<line>:<col>: <msg>"`. Use it from `parse(input string) error` that returns `&LineColumnError{Line:1, Column:1, Msg:"empty input"}` when input is empty.

**Starter Code**:

```go
package main

import "fmt"

// TODO: type LineColumnError struct {...}
// TODO: func (e *LineColumnError) Error() string

func parse(input string) error {
    // TODO
    return nil
}

func main() {
    if err := parse(""); err != nil {
        fmt.Println(err)
    }
    if err := parse("ok"); err == nil {
        fmt.Println("ok")
    }
}
```

**Expected Output**:

```
1:1: empty input
ok
```

**Evaluation Checklist**:

- [ ] Pointer receiver on `Error()`.
- [ ] `Line`, `Column`, `Msg` are exported fields.
- [ ] Format matches exactly.

<details>
<summary>Solution</summary>

```go
type LineColumnError struct {
    Line, Column int
    Msg          string
}

func (e *LineColumnError) Error() string {
    return fmt.Sprintf("%d:%d: %s", e.Line, e.Column, e.Msg)
}

func parse(input string) error {
    if input == "" {
        return &LineColumnError{Line: 1, Column: 1, Msg: "empty input"}
    }
    return nil
}
```

</details>

---

## Task 4 — Sentinel Error (Easy)

**Topic**: Package-level sentinel comparison.

**Description**: Declare `var ErrNotFound = errors.New("not found")`. Implement `find(id int) error` that returns `ErrNotFound` when `id < 0`. In `main`, compare the returned error to `ErrNotFound` using `==`.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("not found")

func find(id int) error {
    // TODO
    return nil
}

func main() {
    err := find(-5)
    if err == ErrNotFound {
        fmt.Println("matched")
    } else {
        fmt.Println("did NOT match")
    }
}
```

**Expected Output**:

```
matched
```

**Evaluation Checklist**:

- [ ] `find` returns the SENTINEL, not a fresh `errors.New`.
- [ ] `==` succeeds.

<details>
<summary>Solution</summary>

```go
func find(id int) error {
    if id < 0 {
        return ErrNotFound
    }
    return nil
}
```

</details>

---

## Task 5 — Reproduce And Fix The Nil-Interface Bug (Medium)

**Topic**: The famous interface-nil trap.

**Description**: Write `process()` that returns a `*MyErr` typed nil through an `error` return type. In `main`, demonstrate `err != nil` is unexpectedly true. Then fix it.

**Starter Code**:

```go
package main

import "fmt"

type MyErr struct{ msg string }
func (e *MyErr) Error() string { return e.msg }

func processBad() error {
    var err *MyErr
    return err
}

// TODO: write processGood that does the same logic but returns truly-nil error.

func main() {
    if err := processBad(); err != nil {
        fmt.Println("bad: got error:", err)
    } else {
        fmt.Println("bad: no error")
    }
    if err := processGood(); err != nil {
        fmt.Println("good: got error:", err)
    } else {
        fmt.Println("good: no error")
    }
}
```

**Expected Output**:

```
bad: got error: <nil>
good: no error
```

**Evaluation Checklist**:

- [ ] `processBad` reproduces the bug as written.
- [ ] `processGood` returns truly-nil interface.
- [ ] Outputs match exactly.

<details>
<summary>Hint</summary>

`processGood` should return `nil` directly (untyped nil) or use `var err error` (interface) as its slot.

</details>

<details>
<summary>Solution</summary>

```go
func processGood() error {
    var err *MyErr // nil pointer
    if err != nil {
        return err
    }
    return nil // untyped nil through interface
}

// or
func processGood() error {
    var err error // interface, default nil
    return err
}
```

</details>

---

## Task 6 — `Op/Err/Path` Triplet (Medium)

**Topic**: Standard-library shape replication.

**Description**: Build `*FSError` matching `*os.PathError`'s shape. Implement `Error()`, `Unwrap()`. Use it from `readFile(path string) ([]byte, error)` that wraps `os.Open` errors. Verify `errors.Is(err, fs.ErrNotExist)` works.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
    "io/fs"
    "os"
)

type FSError struct {
    // TODO: Op, Path, Err fields
}

// TODO: Error() and Unwrap()

func readFile(path string) ([]byte, error) {
    f, err := os.Open(path)
    if err != nil {
        // TODO: wrap in FSError
        return nil, nil
    }
    defer f.Close()
    return nil, nil
}

func main() {
    _, err := readFile("/this/does/not/exist")
    if errors.Is(err, fs.ErrNotExist) {
        fmt.Println("file missing")
    } else {
        fmt.Println("other:", err)
    }
}
```

**Expected Output** (when path missing):

```
file missing
```

**Evaluation Checklist**:

- [ ] `Error()` formats as `"open /path: <inner>"`.
- [ ] `Unwrap()` returns the inner error.
- [ ] `errors.Is` traverses the chain.

<details>
<summary>Solution</summary>

```go
type FSError struct {
    Op, Path string
    Err      error
}

func (e *FSError) Error() string {
    return e.Op + " " + e.Path + ": " + e.Err.Error()
}
func (e *FSError) Unwrap() error { return e.Err }

func readFile(path string) ([]byte, error) {
    f, err := os.Open(path)
    if err != nil {
        return nil, &FSError{Op: "open", Path: path, Err: err}
    }
    defer f.Close()
    // ... read body omitted
    return []byte("ok"), nil
}
```

</details>

---

## Task 7 — Validation Errors With Aggregation (Medium)

**Topic**: Multi-error type and aggregation.

**Description**: Define `FieldError` with `Field` and `Msg`. Define `ValidationErrors []*FieldError` with `Error()` method joining its elements. In `validateUser`, accumulate per-field errors and return either nil or the aggregator. Return at least two field errors when both `Name` is empty and `Age` is negative.

**Starter Code**:

```go
package main

import (
    "fmt"
    "strings"
)

type FieldError struct {
    Field, Msg string
}
// TODO: Error() on *FieldError

type ValidationErrors []*FieldError
// TODO: Error() on ValidationErrors

type User struct {
    Name string
    Age  int
}

func validateUser(u *User) error {
    // TODO
    return nil
}

func main() {
    err := validateUser(&User{Name: "", Age: -1})
    if err != nil {
        fmt.Println(err)
    }
}
```

**Expected Output**:

```
validation failed: name: required; age: must be non-negative
```

**Evaluation Checklist**:

- [ ] Both errors collected.
- [ ] Output format matches.
- [ ] Returns nil for valid input.

<details>
<summary>Solution</summary>

```go
func (e *FieldError) Error() string {
    return e.Field + ": " + e.Msg
}

func (v ValidationErrors) Error() string {
    parts := make([]string, len(v))
    for i, f := range v {
        parts[i] = f.Error()
    }
    return "validation failed: " + strings.Join(parts, "; ")
}

func validateUser(u *User) error {
    var errs ValidationErrors
    if u.Name == "" {
        errs = append(errs, &FieldError{Field: "name", Msg: "required"})
    }
    if u.Age < 0 {
        errs = append(errs, &FieldError{Field: "age", Msg: "must be non-negative"})
    }
    if len(errs) > 0 {
        return errs
    }
    return nil
}
```

</details>

---

## Task 8 — Categorized Error With Behavioral Method (Hard)

**Topic**: Combining a category enum with a behavioral interface.

**Description**: Define `Category` enum (`Transient`, `Permanent`, `Timeout`). Define `*OpError{Op string, Cat Category, Err error}` with `Error()`, `Unwrap()`, and a `Retry() bool` method (`true` for `Transient`). Implement `withRetry(op func() error, n int) error` that retries up to `n` times on retryable errors with a 10ms sleep, otherwise fails fast.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
    "time"
)

type Category int
const (
    Transient Category = iota
    Permanent
    Timeout
)

type OpError struct {
    Op  string
    Cat Category
    Err error
}
// TODO: Error, Unwrap, Retry

func withRetry(op func() error, n int) error {
    // TODO
    return nil
}

func main() {
    attempts := 0
    err := withRetry(func() error {
        attempts++
        if attempts < 3 {
            return &OpError{Op: "load", Cat: Transient, Err: errors.New("temp glitch")}
        }
        return nil
    }, 5)
    fmt.Println("attempts:", attempts, "err:", err)
}
```

**Expected Output**:

```
attempts: 3 err: <nil>
```

**Evaluation Checklist**:

- [ ] `Retry()` returns true only for `Transient`.
- [ ] `withRetry` stops after success.
- [ ] `withRetry` fails fast on non-retryable error.

<details>
<summary>Solution</summary>

```go
func (e *OpError) Error() string {
    return e.Op + ": " + e.Err.Error()
}
func (e *OpError) Unwrap() error { return e.Err }
func (e *OpError) Retry() bool   { return e.Cat == Transient }

type retryable interface {
    Retry() bool
}

func withRetry(op func() error, n int) error {
    var err error
    for i := 0; i < n; i++ {
        err = op()
        if err == nil {
            return nil
        }
        var r retryable
        if !errors.As(err, &r) || !r.Retry() {
            return err
        }
        time.Sleep(10 * time.Millisecond)
    }
    return err
}
```

</details>

---

## Task 9 — Sentinel + Wrap For SQL Layer (Hard)

**Topic**: Sentinel translation and `%w` wrapping.

**Description**: Define `var ErrUserNotFound = errors.New("user not found")`. Implement `getUser(id int) (string, error)` that simulates a database call, returns `(name, nil)` on success, and on failure returns `fmt.Errorf("getUser %d: %w", id, ErrUserNotFound)`. In `main`, demonstrate `errors.Is(err, ErrUserNotFound)` succeeds even on a wrapped error.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
)

var ErrUserNotFound = errors.New("user not found")

func getUser(id int) (string, error) {
    // TODO
    return "", nil
}

func main() {
    _, err := getUser(-1)
    if errors.Is(err, ErrUserNotFound) {
        fmt.Println("user missing")
    }
    name, err := getUser(42)
    if err == nil {
        fmt.Println("found:", name)
    }
}
```

**Expected Output**:

```
user missing
found: alice
```

**Evaluation Checklist**:

- [ ] `errors.Is` finds the sentinel through the wrapper.
- [ ] Successful path returns a name.

<details>
<summary>Solution</summary>

```go
func getUser(id int) (string, error) {
    if id < 0 {
        return "", fmt.Errorf("getUser %d: %w", id, ErrUserNotFound)
    }
    return "alice", nil
}
```

</details>

---

## Task 10 — Type Extraction With `errors.As` (Hard)

**Topic**: Using `errors.As` to read structured data from a wrapped chain.

**Description**: Reuse `LineColumnError` from Task 3. Wrap it with a parent error: `fmt.Errorf("parse failed: %w", &LineColumnError{...})`. In `main`, use `errors.As` to extract the `*LineColumnError` and print the line+column.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
)

type LineColumnError struct {
    Line, Column int
    Msg          string
}
func (e *LineColumnError) Error() string {
    return fmt.Sprintf("%d:%d: %s", e.Line, e.Column, e.Msg)
}

func parseProgram(src string) error {
    return fmt.Errorf("parse failed: %w", &LineColumnError{Line: 3, Column: 7, Msg: "syntax"})
}

func main() {
    err := parseProgram("...")
    // TODO: use errors.As to extract *LineColumnError and print "<line>:<col>"
}
```

**Expected Output**:

```
3:7
```

**Evaluation Checklist**:

- [ ] `errors.As` returns true.
- [ ] Extracted struct's fields are accessible.

<details>
<summary>Solution</summary>

```go
func main() {
    err := parseProgram("...")
    var lcErr *LineColumnError
    if errors.As(err, &lcErr) {
        fmt.Printf("%d:%d\n", lcErr.Line, lcErr.Column)
    }
}
```

</details>

---

## Task 11 — Behavioral Interface Probe (Extra-Hard)

**Topic**: Probing for a behavior with `errors.As` against an interface.

**Description**: Define `type Timeouter interface { Timeout() bool }`. Define `*NetErr{Cause error, IsTimeout bool}` implementing `Error`, `Unwrap`, and `Timeout() bool`. Implement `isTimeout(err error) bool` that uses `errors.As` against the `Timeouter` interface (not against `*NetErr`). Demonstrate it works when the `*NetErr` is wrapped by `fmt.Errorf("...: %w", ...)`.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
)

type Timeouter interface {
    Timeout() bool
}

type NetErr struct {
    Cause     error
    IsTimeout bool
}
// TODO: Error, Unwrap, Timeout

func isTimeout(err error) bool {
    // TODO: use errors.As against Timeouter
    return false
}

func main() {
    inner := &NetErr{Cause: errors.New("conn"), IsTimeout: true}
    outer := fmt.Errorf("op: %w", inner)
    fmt.Println(isTimeout(outer))   // true
    fmt.Println(isTimeout(errors.New("plain"))) // false
}
```

**Expected Output**:

```
true
false
```

**Evaluation Checklist**:

- [ ] `errors.As(err, &t)` where `t` is `Timeouter` works.
- [ ] Wrapping is transparent to the probe.

<details>
<summary>Solution</summary>

```go
func (e *NetErr) Error() string  { return e.Cause.Error() }
func (e *NetErr) Unwrap() error  { return e.Cause }
func (e *NetErr) Timeout() bool  { return e.IsTimeout }

func isTimeout(err error) bool {
    var t Timeouter
    return errors.As(err, &t) && t.Timeout()
}
```

</details>

---

## Task 12 — Memoized `Error()` (Extra-Hard)

**Topic**: Performance — caching the formatted string.

**Description**: Define `*BigError{Op, Path string, Err error, msg string}` where `Error()` builds the message lazily on first call and reuses it after. Write a benchmark comparing it to a non-memoized version. Show with `go test -bench=. -benchmem` that the memoized version has zero allocations after the first call.

**Starter Code (`bigerror.go`)**:

```go
package main

import "fmt"

type BigError struct {
    Op, Path string
    Err      error
    msg      string
}

func NewBigError(op, path string, err error) *BigError {
    return &BigError{Op: op, Path: path, Err: err}
}

func (e *BigError) Error() string {
    // TODO: memoize
    return ""
}

type SimpleError struct {
    Op, Path string
    Err      error
}

func (e *SimpleError) Error() string {
    return fmt.Sprintf("%s %s: %v", e.Op, e.Path, e.Err)
}
```

**Starter Code (`bigerror_test.go`)**:

```go
package main

import (
    "errors"
    "testing"
)

var sink string

func BenchmarkSimple(b *testing.B) {
    e := &SimpleError{Op: "open", Path: "/x", Err: errors.New("inner")}
    for i := 0; i < b.N; i++ {
        sink = e.Error()
    }
}

func BenchmarkMemoized(b *testing.B) {
    e := NewBigError("open", "/x", errors.New("inner"))
    for i := 0; i < b.N; i++ {
        sink = e.Error()
    }
}
```

**Expected Output (benchmark)**:

Roughly:

```
BenchmarkSimple-12   30000000   42 ns/op   24 B/op   2 allocs/op
BenchmarkMemoized-12 1000000000  1.5 ns/op  0 B/op   0 allocs/op
```

**Evaluation Checklist**:

- [ ] `Error()` builds once.
- [ ] Subsequent calls reuse cached string.
- [ ] Benchmark shows zero allocations after warmup.

<details>
<summary>Solution</summary>

```go
func (e *BigError) Error() string {
    if e.msg == "" {
        e.msg = e.Op + " " + e.Path + ": " + e.Err.Error()
    }
    return e.msg
}
```

Note: this lazy memoization is NOT goroutine-safe. If the error escapes to multiple goroutines before first `Error()` call, build the string in the constructor instead:

```go
func NewBigError(op, path string, err error) *BigError {
    return &BigError{
        Op:   op,
        Path: path,
        Err:  err,
        msg:  op + " " + path + ": " + err.Error(),
    }
}
```

</details>

---

## Task 13 — Custom `Is(error) bool` Method (Extra-Hard)

**Topic**: Structural equality for matching errors.

**Description**: Define `*PathErr{Path string}` with `Error()` and a custom `Is(target error) bool` that matches when `target` is also `*PathErr` with the same `Path`. Use it with `errors.Is` to match an error to a sample regardless of pointer identity.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
)

type PathErr struct {
    Path string
}
func (e *PathErr) Error() string { return "path: " + e.Path }
// TODO: Is(target error) bool

func main() {
    e1 := &PathErr{Path: "/x"}
    e2 := &PathErr{Path: "/x"}
    fmt.Println(errors.Is(e1, e2)) // expected: true (different pointers, same Path)
    e3 := &PathErr{Path: "/y"}
    fmt.Println(errors.Is(e1, e3)) // expected: false
}
```

**Expected Output**:

```
true
false
```

**Evaluation Checklist**:

- [ ] `Is` compares struct content, not pointer identity.
- [ ] `errors.Is` calls the custom method.

<details>
<summary>Solution</summary>

```go
func (e *PathErr) Is(target error) bool {
    var t *PathErr
    if !errors.As(target, &t) {
        return false
    }
    return e.Path == t.Path
}
```

</details>

---

## Task 14 — Multi-Error With `Unwrap() []error` (Extra-Hard)

**Topic**: Go 1.20+ multi-unwrap.

**Description**: Define `*Multi{errs []error}` with `Error()` joining the messages with `; ` and `Unwrap() []error` returning the slice. Use it in `runAll` that runs three operations and aggregates their errors. Demonstrate `errors.Is(multi, ErrTransient)` works when one of the inner errors is `ErrTransient`.

**Starter Code**:

```go
package main

import (
    "errors"
    "fmt"
    "strings"
)

type Multi struct{ errs []error }
// TODO: Error, Unwrap

var ErrTransient = errors.New("transient")
var ErrPermanent = errors.New("permanent")

func runAll() error {
    return &Multi{errs: []error{ErrTransient, ErrPermanent}}
}

func main() {
    err := runAll()
    fmt.Println(err)
    fmt.Println(errors.Is(err, ErrTransient)) // expected: true
    fmt.Println(errors.Is(err, ErrPermanent)) // expected: true
}
```

**Expected Output**:

```
transient; permanent
true
true
```

**Evaluation Checklist**:

- [ ] `Unwrap() []error` returns the slice.
- [ ] `errors.Is` finds both inner errors.

<details>
<summary>Solution</summary>

```go
func (m *Multi) Error() string {
    parts := make([]string, len(m.errs))
    for i, e := range m.errs {
        parts[i] = e.Error()
    }
    return strings.Join(parts, "; ")
}
func (m *Multi) Unwrap() []error { return m.errs }
```

</details>

---

## Task 15 — Public Error API Design Review (Extra-Hard)

**Topic**: Designing the error surface of a small package.

**Description**: You are writing package `userdb`. The public API must support the following caller behaviors:

1. Detect "user not found" identity.
2. Detect "email already registered" identity.
3. Read the User ID that caused a conflict (when conflict).
4. Wrap a low-level SQL error transparently (`errors.Is(err, sql.ErrNoRows)` should still work).

Design the public error API: which sentinels, which custom types, which fields. Sketch the package's documented surface in 30 lines or less. No tests required.

**Expected Output**:

A markdown sketch of:

- Sentinel variables.
- Custom error types with fields.
- A small `errors.go` file content with proper doc comments.

**Evaluation Checklist**:

- [ ] At most three sentinels.
- [ ] One custom type with fields, with `Unwrap()` for SQL transparency.
- [ ] Each public symbol documented.

<details>
<summary>Reference Solution</summary>

```go
// Package userdb provides typed user-record access.
//
// Failures are reported via sentinel errors (matched by errors.Is) and
// the *Conflict type for callers that need the offending ID.
package userdb

import (
    "errors"
)

// ErrNotFound is returned when no user matches the lookup criteria.
var ErrNotFound = errors.New("user not found")

// ErrEmailDuplicate is returned when inserting a user whose email
// already exists. Use *Conflict to read the offending UserID.
var ErrEmailDuplicate = errors.New("email already registered")

// Conflict is returned alongside ErrEmailDuplicate to expose the
// existing UserID that caused the collision.
type Conflict struct {
    UserID int64
    Err    error
}

func (c *Conflict) Error() string {
    return c.Err.Error()
}

func (c *Conflict) Unwrap() error {
    return c.Err
}

// Sample usage:
//
//   _, err := db.Insert(u)
//   if errors.Is(err, userdb.ErrEmailDuplicate) {
//       var cf *userdb.Conflict
//       if errors.As(err, &cf) {
//           // cf.UserID is the existing user's id
//       }
//   }
```

Notes:

- Two sentinels (`ErrNotFound`, `ErrEmailDuplicate`) are stable, comparable.
- `*Conflict` carries the data and wraps the inner SQL/sentinel error.
- `errors.Is(err, sql.ErrNoRows)` still works because `Conflict.Unwrap()` returns the inner error chain.
- All public symbols documented.

</details>

---

## Self-Check Rubric

After completing the tasks, ask yourself:

| Question | Where it was tested |
|----------|---------------------|
| Can I define a custom error type from memory? | Task 1, Task 3 |
| Do I know when `==` works and when it doesn't? | Task 4, Task 9 |
| Can I avoid the nil-interface bug? | Task 5 |
| Can I design a public error API? | Task 6, Task 9, Task 15 |
| Can I aggregate errors? | Task 7, Task 14 |
| Do I understand `errors.Is`/`errors.As`? | Task 9, Task 10, Task 13 |
| Can I attach behavioral methods? | Task 8, Task 11 |
| Can I optimize hot error paths? | Task 12 |

If you can answer "yes" to all eight, you have mastered the error interface at a level appropriate for production Go work. The next topic, `02-sentinel-errors`, builds on this foundation by formalizing sentinel design and versioning.

---

## Tips

- Run `go vet ./...` after each task.
- Use `golangci-lint run --enable errorlint --enable errcheck` to catch common mistakes.
- For Tasks 8 and 11, run `go test -race` to confirm goroutine safety (or expose problems).
- For Task 12, profile with `pprof` to confirm the allocation reduction in a longer-running benchmark.

Good luck. Errors are deceptively simple in Go — these tasks are the difference between "knows the syntax" and "writes idiomatic code".
