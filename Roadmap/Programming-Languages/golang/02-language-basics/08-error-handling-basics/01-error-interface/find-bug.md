# The error Interface — Find the Bug

## Instructions

Each exercise contains buggy Go code involving the `error` interface. Identify the bug, explain why it happens, and provide the corrected code. Difficulty: E (Easy), M (Medium), H (Hard).

The five mandated bugs (sentinel comparison, value-receiver copy, pointer-vs-value method-set mismatch, nil-interface bug, and `==` against a wrapped error) are present below as Bugs 1, 2, 3, 4, and 5.

---

## Bug 1 (E) — Two `errors.New` Calls Are Not Equal

```go
package main

import (
    "errors"
    "fmt"
)

var pkgErr = errors.New("not found")

func find() error {
    return errors.New("not found")
}

func main() {
    err := find()
    if err == pkgErr {
        fmt.Println("matched")
    } else {
        fmt.Println("did NOT match")
    }
}
```

The author expected "matched". What prints?

<details>
<summary>Solution</summary>

**Bug**: `find` calls `errors.New("not found")` fresh, returning a new `*errorString` pointer. `pkgErr` is a different `*errorString` pointer. Two pointer values, never equal under `==`, even though their string contents match.

Output:

```
did NOT match
```

**Why**: `errors.New` returns `&errorString{text}`. Each call allocates a new struct on the heap. The interface value `error` is `(type=*errorString, data=heap-pointer)`. Two heap pointers differ. `==` on iface compares the data pointer for pointer-typed values.

**Fix** — return the sentinel:

```go
func find() error {
    return pkgErr
}
```

Now `err` and `pkgErr` are the same iface value. `==` returns true.

**Alternative fix** if you must construct: use `errors.Is` (next topic):

```go
if errors.Is(err, pkgErr) { ... }
```

But for a flat (un-wrapped) error, this still compares with `==` internally and would still fail. Always declare a single sentinel and reuse it.

**Key lesson**: `errors.New` produces distinct values per call. Use a single package-level sentinel for stable identity.
</details>

---

## Bug 2 (M) — Custom Error With Value Receiver Stores Mutable State

```go
package main

import "fmt"

type Counter struct {
    Count int
    Op    string
}

func (c Counter) Error() string {
    c.Count++ // intent: track how many times Error() is called
    return fmt.Sprintf("%s called %d times", c.Op, c.Count)
}

func main() {
    e := Counter{Op: "open"}
    fmt.Println(e.Error())
    fmt.Println(e.Error())
    fmt.Println(e.Error())
    fmt.Println("final count:", e.Count)
}
```

The author expects the count to increase across calls. What prints?

<details>
<summary>Solution</summary>

**Bug**: `Error()` has a value receiver, so the receiver is a COPY of `Counter`. `c.Count++` mutates the copy, never the original.

Output:

```
open called 1 times
open called 1 times
open called 1 times
final count: 0
```

**Why**: `func (c Counter)` copies the entire struct on each call. The body mutates a local copy that is discarded. The original `e.Count` never changes.

**Fix** — switch to pointer receiver:

```go
func (c *Counter) Error() string {
    c.Count++
    return fmt.Sprintf("%s called %d times", c.Op, c.Count)
}

func main() {
    e := &Counter{Op: "open"}
    fmt.Println(e.Error())
    fmt.Println(e.Error())
    fmt.Println(e.Error())
    fmt.Println("final count:", e.Count)
}
// open called 1 times
// open called 2 times
// open called 3 times
// final count: 3
```

**Side note**: mutating in `Error()` is itself a code smell — `Error()` should be idempotent and ideally side-effect-free. Use this only if you genuinely need it (rare).

**Key lesson**: Value receiver = copy. Pointer receiver = shared. Choose based on whether `Error()` should see/touch the live struct.
</details>

---

## Bug 3 (M) — Pointer Receiver, Returning A Value

```go
package main

import "fmt"

type MyErr struct{ msg string }

func (e *MyErr) Error() string { return e.msg }

func makeErr() error {
    return MyErr{msg: "boom"}
}

func main() {
    err := makeErr()
    fmt.Println(err)
}
```

What does the compiler do?

<details>
<summary>Solution</summary>

**Bug**: `Error()` is defined on `*MyErr` (pointer receiver). The method set of `MyErr` (value) does NOT include `Error()`. Only `*MyErr` satisfies the `error` interface.

`return MyErr{msg: "boom"}` is `MyErr` (a value), not `*MyErr` (a pointer). This produces a compile error:

```
./prog.go:11:9: cannot use MyErr{...} (value of type MyErr) as type error in return argument:
        MyErr does not implement error (Error method has pointer receiver)
```

**Fix** — return a pointer:

```go
func makeErr() error {
    return &MyErr{msg: "boom"}
}
```

Or move the method to a value receiver:

```go
func (e MyErr) Error() string { return e.msg }

// then both work:
return MyErr{msg: "boom"} // ok
return &MyErr{msg: "boom"} // also ok (method set of *T contains methods of T)
```

**Key lesson**: Pointer receiver = ONLY `*T` satisfies the interface. Decide receiver type with this in mind.
</details>

---

## Bug 4 (H) — The Famous Nil-Interface Bug

```go
package main

import "fmt"

type MyErr struct{ msg string }

func (e *MyErr) Error() string { return e.msg }

func process() error {
    var err *MyErr // typed nil pointer
    // ... some logic, err remains nil ...
    return err     // returns through error interface
}

func main() {
    if err := process(); err != nil {
        fmt.Println("got error:", err)
    } else {
        fmt.Println("no error")
    }
}
```

The author expects "no error" because `err` was nil inside `process`. What prints?

<details>
<summary>Solution</summary>

**Bug**: `process` returns a typed nil pointer (`*MyErr` = nil) wrapped in an `error` interface. The returned interface value has:

```
interface = (type tag = *MyErr, data = nil)
```

The interface is NOT nil — `err != nil` is **true** because the type tag is non-nil. The `Error()` method is then called on a nil receiver and panics OR returns a zero value depending on the method body.

Output:

```
got error: <nil>
```

(Sometimes `Error()` will deref a nil pointer and panic; the example above accesses `e.msg` on a nil pointer which panics. The exact behavior depends on what `Error()` does.)

### Diagram: iface = (type, data)

```
+----------------+----------------+
| type pointer   | data pointer   |
+----------------+----------------+
        |                |
        v                v
   *MyErr type      nil (typed nil)

This interface is NON-NIL because the type slot is filled.
err == nil is FALSE.
```

vs. a true nil interface:

```
+----------------+----------------+
|       nil      |       nil      |
+----------------+----------------+

Both slots empty. err == nil is TRUE.
```

**Fix A** — return untyped nil:

```go
func process() error {
    var err *MyErr
    if cond {
        err = &MyErr{msg: "boom"}
    }
    if err != nil {
        return err
    }
    return nil // untyped nil -> truly nil interface
}
```

**Fix B** — use an `error` variable as the return:

```go
func process() error {
    var err error // interface, not pointer
    if cond {
        err = &MyErr{msg: "boom"}
    }
    return err
}
```

**Key lesson**: NEVER return a typed nil pointer through an `error` interface. The interface will not be nil at the call site. Always either return untyped `nil` or check before returning.

This bug is so common that it is the single most asked Go interview question. Learn it once and never write code that has it.
</details>

---

## Bug 5 (M) — `==` Against A Wrapped Error

```go
package main

import (
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("not found")

func lookup(id int) error {
    return fmt.Errorf("lookup %d: %w", id, ErrNotFound)
}

func main() {
    err := lookup(42)
    if err == ErrNotFound {
        fmt.Println("matched")
    } else {
        fmt.Println("did NOT match")
    }
}
```

What prints?

<details>
<summary>Solution</summary>

**Bug**: `lookup` wraps `ErrNotFound` with `fmt.Errorf("...: %w", ...)`. The returned `err` is a `*fmt.wrapError`, NOT `ErrNotFound` itself. `err == ErrNotFound` compares two different iface values — false.

Output:

```
did NOT match
```

**Fix** — use `errors.Is`, which walks the chain via `Unwrap()`:

```go
if errors.Is(err, ErrNotFound) {
    fmt.Println("matched")
}
```

**Why this is so common**: pre-Go-1.13 codebases used `==` everywhere. After wrapping was introduced, `==` silently became wrong for any caller that wraps. The `errorlint` linter exists to flag exactly this pattern.

**Key lesson**: Once any code in your stack uses `%w`, every comparison must use `errors.Is`. Migrate immediately when you introduce wrapping.
</details>

---

## Bug 6 (E) — Capitalized Error Strings

```go
var ErrInvalid = errors.New("Invalid input.")

func parse() error {
    return ErrInvalid
}

func handler() {
    if err := parse(); err != nil {
        fmt.Printf("parse failed: %v\n", err)
        // Output: "parse failed: Invalid input."
    }
}
```

<details>
<summary>Solution</summary>

**Bug**: Error strings should be lowercase and have no trailing punctuation. The above produces:

```
parse failed: Invalid input.
```

The capital "I" and the period look out of place when embedded in a larger message. Compare with the corrected:

```
parse failed: invalid input
```

**Fix**:

```go
var ErrInvalid = errors.New("invalid input")
```

The convention exists because errors compose: `fmt.Errorf("step1: %v", innerErr)` produces a chain like `"step1: step2: invalid input"`. Capitalized inner errors and trailing periods break the visual flow.

**Key lesson**: Error messages are lowercase, no trailing punctuation. Even if it offends English-prose instincts, follow Go convention.
</details>

---

## Bug 7 (M) — Missing `Unwrap()` On A Wrapper Error

```go
package main

import (
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("not found")

type RepoError struct {
    Op  string
    Err error
}

func (e *RepoError) Error() string {
    return e.Op + ": " + e.Err.Error()
}

func get(id int) error {
    return &RepoError{Op: "get", Err: ErrNotFound}
}

func main() {
    err := get(42)
    if errors.Is(err, ErrNotFound) {
        fmt.Println("matched")
    } else {
        fmt.Println("did NOT match")
    }
}
```

The author expects `errors.Is` to find `ErrNotFound` inside the wrapper. What prints?

<details>
<summary>Solution</summary>

**Bug**: `RepoError` has no `Unwrap()` method. `errors.Is` cannot traverse past `RepoError` to find `ErrNotFound`. `errors.Is(err, ErrNotFound)` compares only the outer wrapper to the target and returns false.

Output:

```
did NOT match
```

**Fix** — add `Unwrap()`:

```go
func (e *RepoError) Unwrap() error { return e.Err }
```

Now `errors.Is(err, ErrNotFound)` walks `RepoError -> ErrNotFound` and matches.

**Key lesson**: Any custom wrapper error must implement `Unwrap()` for `errors.Is` and `errors.As` to traverse the chain.
</details>

---

## Bug 8 (E) — Returning A Pointer-To-Pointer Through `error`

```go
type MyErr struct{ msg string }
func (e *MyErr) Error() string { return e.msg }

func make() error {
    e := &MyErr{msg: "x"}
    return &e
}
```

<details>
<summary>Solution</summary>

**Bug**: `&e` is a `**MyErr`, not `*MyErr`. `**MyErr` does not have an `Error()` method (the method is on `*MyErr`). The compiler refuses:

```
./prog.go:6:9: cannot use &e (value of type **MyErr) as type error in return argument:
        **MyErr does not implement error (missing Error method)
```

**Fix**:

```go
func make() error {
    return &MyErr{msg: "x"}
}
```

Just return the pointer, not a pointer to a pointer.

**Key lesson**: Mind the levels of indirection. Errors are returned as `*T`, never `**T`.
</details>

---

## Bug 9 (M) — Calling `Error()` On A Possibly-Nil Error

```go
func handle(err error) {
    fmt.Println("got:", err.Error())
}

func main() {
    handle(nil)
}
```

<details>
<summary>Solution</summary>

**Bug**: Calling `err.Error()` when `err` is nil panics with a nil-interface dereference. The interface has no concrete value to dispatch on.

Output:

```
panic: runtime error: invalid memory address or nil pointer dereference
```

**Fix** — guard before dereferencing, or use `%v` formatting which is nil-safe:

```go
func handle(err error) {
    if err == nil {
        fmt.Println("got: <nil>")
        return
    }
    fmt.Println("got:", err.Error())
}
```

Or just:

```go
fmt.Println("got:", err) // %v on nil prints "<nil>" safely
```

**Key lesson**: `err.Error()` is unsafe on nil. Use `%v` formatting or check before calling.
</details>

---

## Bug 10 (M) — Method On A Value But Comparison Through Pointer

```go
type EOFErr struct{}

func (e EOFErr) Error() string { return "eof" }

var ErrEOF = EOFErr{}

func read() error {
    return EOFErr{}
}

func main() {
    err := read()
    if err == ErrEOF {
        fmt.Println("matched")
    } else {
        fmt.Println("did NOT match")
    }
}
```

<details>
<summary>Solution</summary>

**Result**: prints "matched". This one is actually correct because:

- `EOFErr{}` is a comparable struct value (no slice/map/func fields).
- The interface stores the value directly (or in a copy), and `==` on iface compares both type AND value.
- Two empty `EOFErr{}` values are equal.

But beware: if you switch to a pointer receiver:

```go
func (e *EOFErr) Error() string { return "eof" }

var ErrEOF = &EOFErr{}

func read() error {
    return &EOFErr{} // different pointer than ErrEOF
}
```

Now `read()`'s `&EOFErr{}` and `ErrEOF` are different pointers — `==` returns false.

**Why this matters**: comparable struct errors (with value receivers) are a legitimate pattern (e.g., numeric error codes). They allow `==` comparisons without the heap-pointer trap. But the moment you introduce slice/map/func fields the struct is no longer comparable and `==` panics at runtime. Test carefully.

**Fix for the pointer-receiver case**: always use the global sentinel (`return ErrEOF`), or use `errors.Is`.

**Key lesson**: Comparable error structs with value receivers behave well under `==`. Pointer-receiver errors do not — use the sentinel directly or `errors.Is`.
</details>

---

## Bug 11 (H) — Storing An Error In A Struct Field, Then Comparing

```go
type Result struct {
    Err error
}

var done = errors.New("done")

func produce() Result {
    return Result{Err: done}
}

func main() {
    r1 := produce()
    r2 := produce()
    if r1 == r2 {
        fmt.Println("equal")
    } else {
        fmt.Println("not equal")
    }
}
```

<details>
<summary>Solution</summary>

**Behavior**: This compiles AND prints "equal" because `Result` is a comparable struct (its only field is an `error` interface) and both copies hold the same `done` iface value (same type, same data pointer).

**Sub-bug** if you later add a slice field:

```go
type Result struct {
    Err   error
    Items []string
}
```

Now `Result` is NOT comparable. `r1 == r2` is a compile error. Hidden API break for any caller using `==`.

**Fix**: avoid relying on `==` on aggregate types. If equality matters, write a custom `Equal` method.

**Key lesson**: Errors stored in struct fields can make the struct comparable or non-comparable depending on neighbors. Avoid relying on struct `==` when an error field is present.
</details>

---

## Bug 12 (M) — Forgetting That `fmt.Errorf` Without `%w` Loses The Cause

```go
var ErrTimeout = errors.New("timeout")

func attempt() error {
    return ErrTimeout
}

func op() error {
    if err := attempt(); err != nil {
        return fmt.Errorf("op failed: %v", err) // %v, not %w
    }
    return nil
}

func main() {
    err := op()
    if errors.Is(err, ErrTimeout) {
        fmt.Println("retrying")
    } else {
        fmt.Println("giving up")
    }
}
```

<details>
<summary>Solution</summary>

**Bug**: `%v` formats the inner error's text into the message but does NOT call `Unwrap()`. The returned error is a flat string error with no chain. `errors.Is(err, ErrTimeout)` cannot find `ErrTimeout` and returns false.

Output:

```
giving up
```

**Fix** — use `%w` to preserve the cause:

```go
return fmt.Errorf("op failed: %w", err)
```

Now `errors.Is(err, ErrTimeout)` walks `wrapError -> ErrTimeout` and matches.

**Key lesson**: `%v` is a stringification verb. `%w` is a wrapping verb. They look similar but have very different semantics. Modern code almost always wants `%w`.
</details>

---

## Bug 13 (E) — Discarding Errors With `_`

```go
func main() {
    f, _ := os.Open("config.toml")
    defer f.Close()
    io.Copy(os.Stdout, f)
}
```

<details>
<summary>Solution</summary>

**Bug**: If `os.Open` fails, `f` is nil. `f.Close()` panics. `io.Copy` reads from a nil reader and panics again.

**Fix** — handle the error:

```go
func main() {
    f, err := os.Open("config.toml")
    if err != nil {
        log.Fatalf("open: %v", err)
    }
    defer f.Close()
    io.Copy(os.Stdout, f)
}
```

**Lint**: `errcheck` flags any ignored error. Run it in CI.

**Key lesson**: Never discard errors with `_` unless you have a specific, documented reason — and even then, prefer a comment explaining why.
</details>

---

## Cheat Sheet — The Five Mandated Bugs

| # | Bug | Symptom |
|---|-----|---------|
| 1 | Two `errors.New("x")` are not `==` | Sentinel comparisons unexpectedly fail |
| 2 | Value-receiver `Error()` mutates a copy | State changes vanish |
| 3 | Pointer receiver but returning the value type | Compile error: doesn't implement error |
| 4 | Typed nil pointer returned through `error` | `err != nil` is unexpectedly true |
| 5 | `==` against a `%w`-wrapped error | Wrapped chain not detected |

Print this list. Read it before every code review of error-handling code. Each one bites a fresh Go developer roughly every other week.

---

## Summary

The error interface is small but its interactions with pointer/value receivers, interface boxing, and the wrap chain produce a surprisingly rich set of foot-guns. The five mandated bugs above account for the vast majority of error-related Go bugs in the wild. The rest of the bugs in this document round out the full set you should be ready to recognize in a code review or interview setting.

The next topic, `02-sentinel-errors`, gives you the design vocabulary for declaring stable error identities. After that, `03-errors-is-as` and `04-error-wrapping-percent-w` formalize the modern Go idiom that resolves bugs 5 and 12 once and for all.
