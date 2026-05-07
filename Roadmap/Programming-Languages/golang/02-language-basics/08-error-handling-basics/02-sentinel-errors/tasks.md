# Go Sentinel Errors — Tasks

## Instructions

Each task includes a description, starter code, expected output, and an evaluation checklist. Use sentinels idiomatically: declare with `errors.New`, name `Err<Reason>`, check with `errors.Is`, wrap with `%w`. Do not mutate after init.

---

## Task 1 — Declare and detect a basic sentinel

**Difficulty**: Beginner
**Topic**: Sentinel declaration, `==` and `errors.Is` checks

**Description**: Declare a sentinel `ErrNotFound` for a tiny key-value store. Implement `Lookup` so a miss returns the bare sentinel. Show that both `==` and `errors.Is` detect it.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

// TODO: declare ErrNotFound here.

var data = map[string]int{"a": 1, "b": 2}

func Lookup(k string) (int, error) {
    // TODO: return ErrNotFound on a miss.
    return 0, nil
}

func main() {
    _, err := Lookup("missing")
    fmt.Println("Is:", errors.Is(err, ErrNotFound)) // true
    fmt.Println("==:", err == ErrNotFound)           // true (no wrapping yet)
}
```

**Expected Output**:
```
Is: true
==: true
```

**Evaluation Checklist**:
- [ ] `ErrNotFound` declared with `errors.New`
- [ ] Message prefixed with a package-like prefix
- [ ] `Lookup` returns `ErrNotFound` directly on a miss
- [ ] Both checks return true (since the bare sentinel is returned)

---

## Task 2 — Wrap a sentinel with context

**Difficulty**: Beginner
**Topic**: `fmt.Errorf("%w")` and identity preservation

**Description**: Modify `Lookup` to wrap `ErrNotFound` with the missing key in the message. Show that `==` now fails but `errors.Is` still succeeds.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("store: not found")
var data = map[string]int{"a": 1, "b": 2}

func Lookup(k string) (int, error) {
    // TODO: wrap ErrNotFound with the key on a miss.
    return 0, nil
}

func main() {
    _, err := Lookup("missing")
    fmt.Println("err:", err)
    fmt.Println("Is:", errors.Is(err, ErrNotFound)) // true
    fmt.Println("==:", err == ErrNotFound)           // false now
}
```

**Expected Output**:
```
err: lookup "missing": store: not found
Is: true
==: false
```

**Evaluation Checklist**:
- [ ] Wrap with `fmt.Errorf("...: %w", ErrNotFound)`
- [ ] The key appears in the message
- [ ] `errors.Is` still detects `ErrNotFound`
- [ ] `==` no longer matches (demonstrating the trap)

---

## Task 3 — Translate `sql.ErrNoRows` to your own sentinel

**Difficulty**: Beginner
**Topic**: Boundary translation

**Description**: Write a `repo.GetUser` that calls a fake database, gets back `sql.ErrNoRows`, and translates it to `repo.ErrNotFound`.

**Starter Code**:
```go
package main

import (
    "database/sql"
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("repo: not found")

func fakeQuery(id int) error {
    if id < 0 {
        return sql.ErrNoRows
    }
    return nil
}

func GetUser(id int) error {
    err := fakeQuery(id)
    // TODO: translate sql.ErrNoRows → ErrNotFound (wrap with id in the message).
    return err
}

func main() {
    err := GetUser(-1)
    fmt.Println("err:", err)
    fmt.Println("Is ErrNotFound:", errors.Is(err, ErrNotFound)) // true
    fmt.Println("Is sql.ErrNoRows:", errors.Is(err, sql.ErrNoRows)) // false (translated away)
}
```

**Expected Output**:
```
err: get user -1: repo: not found
Is ErrNotFound: true
Is sql.ErrNoRows: false
```

**Evaluation Checklist**:
- [ ] `errors.Is(err, sql.ErrNoRows)` triggers the translation
- [ ] The wrapper carries `ErrNotFound`, not `sql.ErrNoRows`
- [ ] `errors.Is(err, ErrNotFound)` succeeds; `sql.ErrNoRows` does not match

---

## Task 4 — Read until EOF

**Difficulty**: Beginner
**Topic**: `io.EOF` loop pattern

**Description**: Implement `readLines` that reads from a `*bufio.Reader` line-by-line until EOF, returning all lines and any non-EOF error.

**Starter Code**:
```go
package main

import (
    "bufio"
    "errors"
    "fmt"
    "io"
    "strings"
)

func readLines(r *bufio.Reader) ([]string, error) {
    var lines []string
    // TODO: loop calling r.ReadString('\n'). Append the line if non-empty.
    // Stop on io.EOF (return lines, nil). On other errors, return lines, err.
    return lines, nil
}

func main() {
    r := bufio.NewReader(strings.NewReader("a\nb\nc\n"))
    lines, err := readLines(r)
    fmt.Println("lines:", lines)
    fmt.Println("err:", err)
}
```

**Expected Output**:
```
lines: [a b c]
err: <nil>
```

**Evaluation Checklist**:
- [ ] Uses `errors.Is(err, io.EOF)` to detect EOF
- [ ] Trims newlines or stores raw lines consistently
- [ ] Non-EOF errors propagate
- [ ] Empty input returns empty slice and nil error

---

## Task 5 — Multiple sentinels in a switch

**Difficulty**: Intermediate
**Topic**: Sentinel-switch idiom

**Description**: Implement a service operation that may fail with `ErrNotFound`, `ErrConflict`, or `context.Canceled`. Write a `classify(err)` function that returns a category string for each.

**Starter Code**:
```go
package main

import (
    "context"
    "errors"
    "fmt"
)

var (
    ErrNotFound = errors.New("svc: not found")
    ErrConflict = errors.New("svc: conflict")
)

func classify(err error) string {
    // TODO: switch over errors.Is checks.
    return "other"
}

func main() {
    ctx, cancel := context.WithCancel(context.Background())
    cancel()

    fmt.Println(classify(nil))
    fmt.Println(classify(fmt.Errorf("op: %w", ErrNotFound)))
    fmt.Println(classify(fmt.Errorf("op: %w", ErrConflict)))
    fmt.Println(classify(ctx.Err()))
    fmt.Println(classify(errors.New("random")))
}
```

**Expected Output**:
```
ok
not_found
conflict
canceled
other
```

**Evaluation Checklist**:
- [ ] `nil` returns `"ok"`
- [ ] `errors.Is` cascade with appropriate ordering
- [ ] Each sentinel has a distinct category
- [ ] Default returns `"other"`

---

## Task 6 — Sentinel + structured error

**Difficulty**: Intermediate
**Topic**: Structured error wrapping a sentinel

**Description**: Define a `*ConflictError` carrying the conflicting key. It should `Unwrap` to a sentinel `ErrConflict`. Show that `errors.Is` detects the sentinel and `errors.As` extracts the key.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

var ErrConflict = errors.New("repo: conflict")

type ConflictError struct {
    // TODO: key field.
}

// TODO: implement Error() and Unwrap()

func Insert(k string) error {
    // TODO: return a *ConflictError with the key.
    return nil
}

func main() {
    err := Insert("alice")
    fmt.Println("err:", err)
    fmt.Println("Is conflict:", errors.Is(err, ErrConflict))

    var ce *ConflictError
    if errors.As(err, &ce) {
        fmt.Println("conflicting key:", ce.Key)
    }
}
```

**Expected Output**:
```
err: conflict on key "alice"
Is conflict: true
conflicting key: alice
```

**Evaluation Checklist**:
- [ ] `*ConflictError` has `Key` field
- [ ] `Error()` includes the key in a human-friendly message
- [ ] `Unwrap()` returns `ErrConflict`
- [ ] `errors.Is(err, ErrConflict)` is true
- [ ] `errors.As(err, &ce)` populates the key

---

## Task 7 — Sentinel-as-category via `Is` method

**Difficulty**: Intermediate
**Topic**: `Is` method on a structured error

**Description**: Define `*HTTPError` with a `StatusCode` field. Implement an `Is` method so `errors.Is(httpErr, ErrRetryable)` returns true for status 429 and 5xx. Use a sentinel `ErrRetryable`.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

var ErrRetryable = errors.New("retryable")

type HTTPError struct {
    // TODO: StatusCode field.
}

// TODO: Error() and Is(target).

func main() {
    e1 := &HTTPError{StatusCode: 200}
    e2 := &HTTPError{StatusCode: 429}
    e3 := &HTTPError{StatusCode: 503}
    e4 := &HTTPError{StatusCode: 400}

    for _, e := range []*HTTPError{e1, e2, e3, e4} {
        fmt.Printf("status=%d retryable=%v\n", e.StatusCode, errors.Is(e, ErrRetryable))
    }
}
```

**Expected Output**:
```
status=200 retryable=false
status=429 retryable=true
status=503 retryable=true
status=400 retryable=false
```

**Evaluation Checklist**:
- [ ] `Is(target error) bool` method on `*HTTPError`
- [ ] Returns true for `target == ErrRetryable` and the right status codes
- [ ] Returns false otherwise (don't accidentally match other targets)
- [ ] `errors.Is` cooperates correctly

---

## Task 8 — Detect a wrapped sentinel three layers deep

**Difficulty**: Intermediate
**Topic**: Chain walking

**Description**: Build a 3-layer call stack where each layer wraps the inner error. Write a top-level handler that uses `errors.Is` to detect a sentinel deep in the chain.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

var ErrInner = errors.New("inner")

func deep() error {
    return ErrInner
}

func mid() error {
    // TODO: wrap deep()
    return nil
}

func top() error {
    // TODO: wrap mid()
    return nil
}

func main() {
    err := top()
    fmt.Println("err:", err)
    fmt.Println("Is ErrInner:", errors.Is(err, ErrInner))
    fmt.Println("== ErrInner:", err == ErrInner)
}
```

**Expected Output**:
```
err: top: mid: inner
Is ErrInner: true
== ErrInner: false
```

**Evaluation Checklist**:
- [ ] Each layer wraps with `%w`
- [ ] The message reads as a colon-separated chain
- [ ] `errors.Is` detects the sentinel
- [ ] `==` does not (showing why `errors.Is` is needed)

---

## Task 9 — Aliased sentinel

**Difficulty**: Intermediate
**Topic**: Re-exporting via alias vs re-declaration

**Description**: Define a sentinel in package `inner`, then re-export it from package `outer` two ways: once via alias and once via fresh `errors.New`. Demonstrate the identity difference.

**Starter Code** (single file with a fake "two packages" via comments):
```go
package main

import (
    "errors"
    "fmt"
)

// "package inner"
var InnerErr = errors.New("inner: err")

// "package outer (alias version)"
// TODO: var OuterAlias = ???

// "package outer (redeclared version)"
// TODO: var OuterRedeclared = ???

func produce() error {
    // returns the inner sentinel directly
    return InnerErr
}

func main() {
    err := produce()
    fmt.Println("Is OuterAlias:", errors.Is(err, OuterAlias))
    fmt.Println("Is OuterRedeclared:", errors.Is(err, OuterRedeclared))
}
```

**Expected Output**:
```
Is OuterAlias: true
Is OuterRedeclared: false
```

**Evaluation Checklist**:
- [ ] `OuterAlias = InnerErr` (same identity)
- [ ] `OuterRedeclared = errors.New("...")` (new identity)
- [ ] The alias check matches; the re-declaration does not
- [ ] Comment explains why redeclaration is a common bug

---

## Task 10 — Sentinel as a metric label

**Difficulty**: Advanced
**Topic**: Sentinel-driven classification for observability

**Description**: Implement an `errorLabel` function that takes an `error` and returns a Prometheus-friendly label string from a fixed set: `"ok"`, `"not_found"`, `"conflict"`, `"canceled"`, `"deadline"`, `"internal"`. Use sentinels as the dispatch axis.

**Starter Code**:
```go
package main

import (
    "context"
    "errors"
    "fmt"
)

var (
    ErrNotFound = errors.New("svc: not found")
    ErrConflict = errors.New("svc: conflict")
)

func errorLabel(err error) string {
    // TODO: switch over errors.Is checks; default to "internal".
    return "internal"
}

func main() {
    cases := []error{
        nil,
        ErrNotFound,
        fmt.Errorf("op: %w", ErrConflict),
        context.Canceled,
        context.DeadlineExceeded,
        errors.New("random"),
    }
    for _, c := range cases {
        fmt.Println(errorLabel(c))
    }
}
```

**Expected Output**:
```
ok
not_found
conflict
canceled
deadline
internal
```

**Evaluation Checklist**:
- [ ] Returns `"ok"` for nil
- [ ] Each sentinel maps to a distinct label
- [ ] Cancellation and deadline are distinguished
- [ ] Unknown errors map to `"internal"`
- [ ] Cardinality is fixed (good for Prometheus)

---

## Task 11 — Retry policy keyed by `ErrRetryable`

**Difficulty**: Advanced
**Topic**: Sentinel-driven retry

**Description**: Implement `retry(fn func() error, n int) error` that retries `fn` up to `n` times if the returned error matches `ErrRetryable`. Otherwise it returns immediately.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

var ErrRetryable = errors.New("retryable")
var ErrFatal = errors.New("fatal")

var attempts int

func flakey() error {
    attempts++
    if attempts < 3 {
        return fmt.Errorf("attempt %d: %w", attempts, ErrRetryable)
    }
    return nil
}

func explode() error {
    return ErrFatal
}

func retry(fn func() error, n int) error {
    // TODO: loop up to n times. Return early if err is nil or not ErrRetryable.
    return nil
}

func main() {
    attempts = 0
    err := retry(flakey, 5)
    fmt.Println("flakey err:", err, "attempts:", attempts)

    attempts = 0
    err = retry(explode, 5)
    fmt.Println("explode err:", err, "attempts:", attempts)
}
```

**Expected Output**:
```
flakey err: <nil> attempts: 3
explode err: fatal attempts: 1
```

**Evaluation Checklist**:
- [ ] Retries while `errors.Is(err, ErrRetryable)`
- [ ] Stops on `nil` or non-retryable errors
- [ ] Respects the `n` cap
- [ ] Returns the last error if all attempts failed

---

## Task 12 — Bug to fix: `==` against a wrapped sentinel

**Difficulty**: Advanced
**Topic**: Recognising and fixing the `==` bug

**Description**: Read the buggy code below, identify the bug, and fix it. The fix should be minimal and idiomatic.

**Starter Code (buggy)**:
```go
package main

import (
    "fmt"
    "io"
)

func mayWrap() error {
    return fmt.Errorf("decode: %w", io.EOF)
}

func main() {
    err := mayWrap()
    if err == io.EOF {
        fmt.Println("clean EOF")
    } else if err != nil {
        fmt.Println("error:", err)
    }
}
```

**Bug**: The wrapped error is not `==` to `io.EOF`. The "clean EOF" branch never fires.

**Fix**: Replace `err == io.EOF` with `errors.Is(err, io.EOF)`. Add the `errors` import.

**Expected Output (after fix)**:
```
clean EOF
```

**Evaluation Checklist**:
- [ ] `errors.Is` replaces `==`
- [ ] Import added
- [ ] No other unrelated changes

---

## Task 13 — Multi-wrap with `errors.Join`

**Difficulty**: Advanced
**Topic**: `errors.Join` and multi-target `errors.Is`

**Description**: Build an error that combines `ErrA` and `ErrB` via `errors.Join`. Show that `errors.Is` detects both.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

var (
    ErrA = errors.New("a")
    ErrB = errors.New("b")
)

func combined() error {
    // TODO: return errors.Join(ErrA, ErrB)
    return nil
}

func main() {
    err := combined()
    fmt.Println("err:", err)
    fmt.Println("Is A:", errors.Is(err, ErrA))
    fmt.Println("Is B:", errors.Is(err, ErrB))
}
```

**Expected Output (Go 1.20+)**:
```
err: a
b
Is A: true
Is B: true
```

**Evaluation Checklist**:
- [ ] Uses `errors.Join`
- [ ] Both sentinels are detected
- [ ] Output shows both messages joined

---

## Task 14 — Sentinel doc comments

**Difficulty**: Advanced
**Topic**: Documenting return conditions

**Description**: Take the package below and add proper doc comments explaining when each sentinel is returned. The goal is that a reader of `go doc` understands the contract without reading the implementation.

**Starter Code**:
```go
package store

import "errors"

var ErrNotFound = errors.New("store: not found")
var ErrConflict = errors.New("store: conflict")
var ErrReadOnly = errors.New("store: read-only")

// TODO: add comments above each sentinel describing the condition.

// Lookup retrieves a value by key.
func Lookup(k string) (any, error) { return nil, nil }

// Insert adds a value with the given key.
func Insert(k string, v any) error { return nil }

// Delete removes the value with the given key.
func Delete(k string) error { return nil }
```

**Expected outcome**: each sentinel has a one-line doc comment in the form "ErrXxx is returned by ... when ...".

**Evaluation Checklist**:
- [ ] Every sentinel has a doc comment
- [ ] Each comment names the function(s) that return it
- [ ] Each comment names the condition that triggers it
- [ ] The function doc comments mention which sentinels they may return
- [ ] `go doc` output reads as a complete contract

---

## Task 15 — Build a tiny error-classifier library

**Difficulty**: Advanced
**Topic**: Sentinel-driven library design

**Description**: Build a small package `errcat` exposing a `Category` enum and a `Classify(err)` function. Map sentinels from `context`, your own package, and `os` to categories. Provide a test that locks in the mapping.

**Starter Code (package errcat)**:
```go
package errcat

import (
    "context"
    "errors"
    "io/fs"
)

type Category int

const (
    CatOK Category = iota
    CatNotFound
    CatPermission
    CatCanceled
    CatDeadline
    CatRetryable
    CatInternal
)

var (
    // ErrRetryable is matched by Classify to return CatRetryable.
    // Use it as a sentinel wrap target in your producers.
    ErrRetryable = errors.New("errcat: retryable")
)

func Classify(err error) Category {
    // TODO: switch over errors.Is checks
    return CatInternal
}
```

**Test Code**:
```go
package errcat_test

import (
    "context"
    "errors"
    "fmt"
    "io/fs"
    "testing"

    "example.com/errcat"
)

func TestClassify(t *testing.T) {
    cases := []struct {
        name string
        err  error
        want errcat.Category
    }{
        {"nil", nil, errcat.CatOK},
        {"not exist", fs.ErrNotExist, errcat.CatNotFound},
        {"permission", fs.ErrPermission, errcat.CatPermission},
        {"canceled", context.Canceled, errcat.CatCanceled},
        {"deadline", context.DeadlineExceeded, errcat.CatDeadline},
        {"retryable", fmt.Errorf("op: %w", errcat.ErrRetryable), errcat.CatRetryable},
        {"random", errors.New("random"), errcat.CatInternal},
    }
    for _, c := range cases {
        got := errcat.Classify(c.err)
        if got != c.want {
            t.Errorf("%s: got %v, want %v", c.name, got, c.want)
        }
    }
}
```

**Evaluation Checklist**:
- [ ] `Classify` covers all listed sentinels
- [ ] Cases ordered with cancellation/deadline before retryable
- [ ] `nil` → `CatOK`, default → `CatInternal`
- [ ] Test passes
- [ ] `ErrRetryable` exposed for callers to use in their wraps

---

## Bonus Task — Sentinel evolution

**Difficulty**: Advanced
**Topic**: Backward-compatible API evolution

**Description**: Take a package that exports `var ErrConflict = errors.New(...)` and evolve it to also expose a `*ConflictError` carrying the conflicting key. Existing callers using `errors.Is(err, ErrConflict)` must continue to work; new callers can use `errors.As(err, &ce)` to extract the key.

**Starter Code**:
```go
package store

import "errors"

var ErrConflict = errors.New("store: conflict")

// Insert returns ErrConflict if the key already exists.
func Insert(k string, v any) error {
    if _, exists := data[k]; exists {
        return ErrConflict
    }
    data[k] = v
    return nil
}

var data = map[string]any{}
```

**Goal after evolution**:
- Insert returns a `*ConflictError` containing the key.
- `errors.Is(err, ErrConflict)` still returns true (the structured error wraps the sentinel).
- `errors.As(err, &ce)` populates the conflicting key.

**Evaluation Checklist**:
- [ ] `*ConflictError` defined with `Key` field
- [ ] `Error()` includes the key
- [ ] `Unwrap()` returns `ErrConflict`
- [ ] Existing callers continue to compile and pass `errors.Is`
- [ ] New callers can extract `Key` via `errors.As`
- [ ] No breaking changes
