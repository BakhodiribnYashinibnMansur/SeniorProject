# Go Sentinel Errors — Find the Bug

## Instructions

Each exercise contains buggy code involving sentinel errors. Identify the bug, explain why, and provide the fix. Difficulty: Easy, Medium, Hard.

---

## Bug 1 (Easy) — `==` against a wrapped sentinel

```go
package main

import (
    "fmt"
    "io"
)

func mayWrap() error {
    return fmt.Errorf("read failed: %w", io.EOF)
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

What does it print?

<details>
<summary>Solution</summary>

**Bug**: `fmt.Errorf("...: %w", io.EOF)` returns a new `*fmt.wrapError` wrapping `io.EOF`. The wrapper is not equal to `io.EOF`. `==` comparison fails; the code follows the "real error" branch.

Output:
```
error: read failed: EOF
```

**Fix** — use `errors.Is`, which walks the chain:

```go
import "errors"

if errors.Is(err, io.EOF) {
    fmt.Println("clean EOF")
} else if err != nil {
    fmt.Println("error:", err)
}
```

`errors.Is` calls `Unwrap` on the wrapper, gets `io.EOF`, matches against the target. Returns true.

**Key lesson**: As soon as any layer wraps with `%w`, `==` against the bare sentinel breaks. Use `errors.Is` by default. This is the single most common sentinel bug in Go code, and the reason `errorlint` flags `err == ErrFoo` patterns.
</details>

---

## Bug 2 (Easy) — Declaring a sentinel with `fmt.Errorf("foo")`

```go
package store

import "fmt"

var ErrNotFound = fmt.Errorf("store: not found")

func Lookup(k string) (any, error) {
    return nil, ErrNotFound
}
```

A consumer test:
```go
err := store.Lookup("k")
if !errors.Is(err, store.ErrNotFound) {
    t.Fail()
}
```

The test passes. So what's the bug?

<details>
<summary>Solution</summary>

**Discussion**: It works for now. `fmt.Errorf("store: not found")` (no `%w` directive) returns a plain `*fmt.wrapError` (or, in older Go, a `*errors.errorString`-like value) with no chain. Identity-based comparison still works.

**The bug is latent**. Suppose a maintainer later adds context to the message:

```go
var ErrNotFound = fmt.Errorf("store: not found in %q", "default") // still fine
```

Or, by mistake:

```go
var ErrNotFound = fmt.Errorf("store: not found: %w", io.EOF) // BAD
```

The second form silently makes `ErrNotFound` wrap `io.EOF`. Now:

```go
errors.Is(store.ErrNotFound, io.EOF) // true!
```

Any retry policy or classifier that checks `io.EOF` will treat all `ErrNotFound` returns as EOF.

**Fix** — declare with `errors.New`. It cannot accidentally produce a wrap:

```go
import "errors"

var ErrNotFound = errors.New("store: not found")
```

`errors.New` accepts only a string; it has no formatting verbs and cannot create a chain.

**Key lesson**: Use `errors.New` for sentinels. The function's signature prevents accidental wrapping. Reaching for `fmt.Errorf` is non-idiomatic and opens the door to a subtle later mistake.
</details>

---

## Bug 3 (Easy) — Mutating a sentinel at runtime

```go
package store

import "errors"

var ErrNotFound = errors.New("store: not found")

func InitWithLocale(locale string) {
    if locale == "es" {
        ErrNotFound = errors.New("store: no encontrado") // mutate at init
    }
}
```

Caller code:

```go
err := store.Lookup("k")
if errors.Is(err, store.ErrNotFound) {
    fmt.Println("missing")
}
```

Some calls match; some don't. Why?

<details>
<summary>Solution</summary>

**Bug**: `InitWithLocale` reassigns `ErrNotFound` to a fresh `errors.New(...)` — a new allocation with a new identity. Any error value already returned (and pointing at the old allocation) no longer matches the new `ErrNotFound`.

If the timing is:
1. Producer calls `errors.New("store: not found")` and uses it as the return.
2. `InitWithLocale("es")` runs, replacing `ErrNotFound` with a different allocation.
3. Caller calls `errors.Is(returnedErr, ErrNotFound)`.

The returned error points at the *original* allocation; `ErrNotFound` now points at the new one. `errors.Is` walks the chain and never finds the new one. Returns false.

In addition, any future return after step 2 will use the new `ErrNotFound` and match correctly. Mixed timing produces flaky behavior.

**Fix** — never mutate a sentinel after declaration. Localisation belongs at the presentation layer:

```go
var ErrNotFound = errors.New("store: not found") // identifier, not localised

// in handler/presentation:
func translate(err error, locale string) string {
    switch {
    case errors.Is(err, store.ErrNotFound) && locale == "es":
        return "no encontrado"
    case errors.Is(err, store.ErrNotFound):
        return "not found"
    }
    return err.Error()
}
```

Sentinel identity is the language-neutral key; localisation is a presentation concern.

**Key lesson**: A sentinel's address must be stable for the program's lifetime. Mutating it silently breaks every existing comparison. Treat it as effectively immutable.
</details>

---

## Bug 4 (Medium) — Sentinel from another package, wrong import

```go
package consumer

import (
    "errors"
    "fmt"

    "example.com/v1/store"
)

func handle(err error) {
    if errors.Is(err, store.ErrNotFound) {
        fmt.Println("missing")
        return
    }
    fmt.Println("other:", err)
}
```

The producer:
```go
package producer

import "example.com/v2/store" // different major version

func find(k string) error {
    return store.ErrNotFound // v2's sentinel
}
```

`handle(producer.find("k"))` prints `other: store: not found`. Why?

<details>
<summary>Solution</summary>

**Bug**: The consumer imports `example.com/v1/store`; the producer imports `example.com/v2/store`. They are different packages (different module versions). Each version's `ErrNotFound` is a separate `errors.New(...)` allocation.

The producer returns `v2.ErrNotFound`. The consumer compares against `v1.ErrNotFound`. The pointers differ. `errors.Is` does not match.

The error message `"store: not found"` happens to be identical, masking the bug.

**Fix** — make sure all participants of a sentinel comparison share the same module version. In a single binary, `go.mod`'s minimum-version selection ensures one copy per module path. The bug here is using two different paths (`v1` and `v2`) for the same logical package.

If you must support both versions in the same build, expose your own re-classifying boundary:

```go
package consumer

import (
    v1 "example.com/v1/store"
    v2 "example.com/v2/store"
)

func IsNotFound(err error) bool {
    return errors.Is(err, v1.ErrNotFound) || errors.Is(err, v2.ErrNotFound)
}
```

But the right fix is usually to pick one version.

**Subtlety**: this bug also appears with copy-pasted comparisons. A developer copying `errors.Is(err, store.ErrNotFound)` from a v2 example into a v1 file may get a compile error if the symbol name moved, or worse, may accidentally import the wrong version via auto-import.

**Key lesson**: Sentinel identity is per-package per-build. Cross-version comparisons silently fail. Audit imports when sentinel checks misbehave.
</details>

---

## Bug 5 (Medium) — Re-exporting a sentinel via a fresh `errors.New`

```go
package shim

import (
    "errors"

    "example.com/store"
)

// Re-export for convenience.
var ErrNotFound = errors.New("not found")

func Find(k string) (any, error) {
    return store.Lookup(k) // returns store.ErrNotFound
}
```

A consumer:

```go
err := shim.Find("k")
if errors.Is(err, shim.ErrNotFound) {
    fmt.Println("missing")
}
```

The check fails. Why?

<details>
<summary>Solution</summary>

**Bug**: `shim.ErrNotFound` is a *new* sentinel created by `errors.New("not found")` — a different allocation from `store.ErrNotFound`. `Find` returns the underlying `store.ErrNotFound`, which has a different identity than `shim.ErrNotFound`. `errors.Is` does not match.

The shim author meant "re-export for convenience" but accidentally created a parallel sentinel that nobody ever returns.

**Fix** — alias, not redeclare:

```go
package shim

import (
    "example.com/store"
)

// Alias: same identity as store.ErrNotFound.
var ErrNotFound = store.ErrNotFound
```

Now `shim.ErrNotFound == store.ErrNotFound` is true (same allocation), and `errors.Is` works against either name.

**Or**, translate at the boundary:

```go
package shim

import (
    "errors"
    "fmt"

    "example.com/store"
)

var ErrNotFound = errors.New("shim: not found")

func Find(k string) (any, error) {
    v, err := store.Lookup(k)
    if errors.Is(err, store.ErrNotFound) {
        return nil, fmt.Errorf("find %q: %w", k, ErrNotFound)
    }
    return v, err
}
```

Now `shim.ErrNotFound` is independent, but `Find` actively converts the upstream sentinel to it. Both designs are valid; which one fits depends on whether `shim` wants to expose its own contract or pass through `store`'s.

**Key lesson**: Re-exporting via `var X = errors.New(...)` looks like an alias but creates a fresh identity. Use `var X = pkg.X` for true aliases, or do active boundary translation. Never mix the two styles for the same name.
</details>

---

## Bug 6 (Medium) — Sentinel in a switch, wrong order

```go
package main

import (
    "context"
    "errors"
    "fmt"
    "time"
)

var ErrAppFailed = errors.New("app: failed")

func work(ctx context.Context) error {
    select {
    case <-time.After(time.Second):
        return ErrAppFailed
    case <-ctx.Done():
        return fmt.Errorf("work: %w", ctx.Err())
    }
}

func handle(err error) {
    switch {
    case err == nil:
        return
    case errors.Is(err, ErrAppFailed):
        fmt.Println("retry app failure")
    case errors.Is(err, context.Canceled),
         errors.Is(err, context.DeadlineExceeded):
        fmt.Println("clean shutdown")
    default:
        fmt.Println("other:", err)
    }
}
```

What's the issue?

<details>
<summary>Solution</summary>

**Bug**: The order is fine in isolation, but consider when both conditions could match. If `work` ever wraps both — e.g.:

```go
return fmt.Errorf("work after deadline: %w: %w", ErrAppFailed, ctx.Err()) // multi-wrap
```

— then `errors.Is(err, ErrAppFailed)` matches and `errors.Is(err, context.Canceled)` would also match. The switch picks the first matching case (`ErrAppFailed`), classifies it as a retryable failure, and triggers retries on a cancelled context. The whole point of context cancellation is to *stop* work; retrying defeats it.

**Fix** — check cancellation/deadline FIRST, since they should always abort regardless of inner failure:

```go
switch {
case err == nil:
    return
case errors.Is(err, context.Canceled),
     errors.Is(err, context.DeadlineExceeded):
    fmt.Println("clean shutdown")
case errors.Is(err, ErrAppFailed):
    fmt.Println("retry app failure")
default:
    fmt.Println("other:", err)
}
```

Cancellation is a top-priority signal; it should override any application-level classification.

**Key lesson**: When a sentinel switch involves cancellation or fatal conditions, put them FIRST. Multi-wrap or composite errors can match multiple cases; the order determines which one wins.
</details>

---

## Bug 7 (Medium) — `errors.Is` vs `errors.As` confusion

```go
package main

import (
    "errors"
    "fmt"
    "os"
)

func main() {
    _, err := os.Open("/nope")
    if err == os.ErrNotExist {
        fmt.Println("missing")
    } else if err != nil {
        fmt.Println("other:", err)
    }
}
```

Output: `other: open /nope: no such file or directory`. Why doesn't the first branch fire?

<details>
<summary>Solution</summary>

**Bug**: `os.Open` returns a `*fs.PathError` containing the underlying syscall error. `os.ErrNotExist` is the sentinel that the path error wraps (via its `Is` method delegating to `syscall.ENOENT`). The returned `error` is *not* `os.ErrNotExist` directly; it's a wrapping struct.

`==` compares the outermost value, which is the `*fs.PathError`, not `os.ErrNotExist`. Hence the `else` branch.

**Fix** — use `errors.Is`:

```go
if errors.Is(err, os.ErrNotExist) {
    fmt.Println("missing")
}
```

`errors.Is` walks the chain: outer `*fs.PathError` → its `Is` method → matches `os.ErrNotExist`. Returns true.

**For accessing the path** (from the `*fs.PathError`):

```go
var pe *fs.PathError
if errors.As(err, &pe) {
    fmt.Println("missing path:", pe.Path)
}
```

**Key lesson**: For sentinels exposed via structured errors (the stdlib's `os` package, network errors, etc.), `==` is broken from the start; `errors.Is` is the only correct check. `errors.As` is needed for data extraction.
</details>

---

## Bug 8 (Medium) — Wrap with `%v` instead of `%w`

```go
package repo

import (
    "database/sql"
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("repo: not found")

func (r *Repo) Get(id int) (User, error) {
    var u User
    err := r.db.QueryRow("...").Scan(&u.ID)
    if errors.Is(err, sql.ErrNoRows) {
        return User{}, fmt.Errorf("get %d: %v: not found", id, sql.ErrNoRows) // BUG
    }
    if err != nil {
        return User{}, fmt.Errorf("get %d: %v", id, err)
    }
    return u, nil
}
```

Caller:

```go
u, err := repo.Get(42)
if errors.Is(err, repo.ErrNotFound) {
    fmt.Println("not found")
}
```

The check fails. Why?

<details>
<summary>Solution</summary>

**Bug**: The `Get` function wraps with `%v` instead of `%w`, *and* it never references `repo.ErrNotFound`. Two separate problems:

1. `%v` formats the error's message but does NOT preserve the chain. The returned error has no `Unwrap` method; `errors.Is` cannot walk it.
2. The function never returns `ErrNotFound`. It only mentions "not found" in the message text.

`errors.Is(err, ErrNotFound)` therefore fails because:
- The outer error is a `*fmt.wrapError`-like value (from `%v` it's actually `*errors.errorString` with no chain).
- Its message contains "not found" but the chain doesn't contain `ErrNotFound`.
- Identity comparison fails.

**Fix** — use `%w` and reference the right sentinel:

```go
if errors.Is(err, sql.ErrNoRows) {
    return User{}, fmt.Errorf("get %d: %w", id, ErrNotFound)
}
if err != nil {
    return User{}, fmt.Errorf("get %d: %w", id, err)
}
```

Now the chain is `wrapper → ErrNotFound`. `errors.Is(err, ErrNotFound)` walks it and returns true.

**Key lesson**: `%v` and `%w` look similar but behave differently. `%v` is for display only; the chain is broken. `%w` preserves the chain. For sentinels to be detectable downstream, you must use `%w` at every wrap site, and you must reference an actual exported sentinel — not just "include the words" in the message.
</details>

---

## Bug 9 (Hard) — Sentinel that wraps its own producer's error

```go
package svc

import (
    "errors"
    "fmt"
    "io"
)

// Defined once for clarity.
var ErrInternal = fmt.Errorf("svc: internal: %w", io.ErrUnexpectedEOF) // BUG

func Process(r io.Reader) error {
    if _, err := r.Read(nil); err != nil {
        return ErrInternal // bare return
    }
    return nil
}
```

Retry policy:

```go
err := svc.Process(r)
if errors.Is(err, io.ErrUnexpectedEOF) {
    backoff()
    retry()
}
```

The retry fires for every `Process` failure, even ones that have nothing to do with EOF. Why?

<details>
<summary>Solution</summary>

**Bug**: `var ErrInternal = fmt.Errorf("svc: internal: %w", io.ErrUnexpectedEOF)` is a sentinel that *wraps* `io.ErrUnexpectedEOF`. The `%w` directive establishes the chain at declaration. Now any `errors.Is(svc.ErrInternal, io.ErrUnexpectedEOF)` returns true. The retry policy thinks every `ErrInternal` is an unexpected EOF and retries.

This is mandated find-bug case 2 + a real-world retry incident. The maintainer copy-pasted a wrap-pattern from another file ("everything wraps the cause") and didn't notice that they baked a permanent chain into the sentinel.

**Fix** — declare with `errors.New`:

```go
var ErrInternal = errors.New("svc: internal")
```

Now `ErrInternal` has no chain. `errors.Is(svc.ErrInternal, io.ErrUnexpectedEOF)` is false. The retry policy fires only for real EOF cases.

If you want the wrap dynamically (per call) — e.g., to attach the underlying cause — wrap at the *call site*, not at declaration:

```go
return fmt.Errorf("process: %w", err) // wraps the actual underlying error
```

Now the sentinel is the identity; the wrap chain reflects the real cause of this specific failure.

**Key lesson**: A sentinel declared with `fmt.Errorf("...: %w", X)` permanently wraps `X`. Every `errors.Is` against `X` reaches the sentinel through the chain. Declare sentinels with `errors.New`. Reserve `fmt.Errorf` for *per-call* wrapping with dynamic context.
</details>

---

## Bug 10 (Hard) — Two re-exports diverging silently

```go
package a

import "errors"

var ErrFoo = errors.New("a: foo")
```

```go
package b

import "errors"

// Author copy-pastes "a: foo" instead of importing.
var ErrFoo = errors.New("a: foo")
```

```go
package consumer

import (
    "errors"
    "fmt"

    "example.com/a"
    "example.com/b"
)

func handle(err error) {
    if errors.Is(err, a.ErrFoo) || errors.Is(err, b.ErrFoo) {
        fmt.Println("foo")
        return
    }
    fmt.Println("other:", err)
}
```

Producers in `a` return `a.ErrFoo`. Producers in `b` return `b.ErrFoo`. They look identical. Why is it still a bug?

<details>
<summary>Solution</summary>

**Discussion**: `a.ErrFoo` and `b.ErrFoo` are different `*errorString` allocations. Their messages are identical, but their identities are not. The `OR` in `handle` papers over the bug.

The bug is **API-design**: `b` should not have its own `ErrFoo`. It should either:
1. **Alias**: `var ErrFoo = a.ErrFoo` — same identity, transparent.
2. **Translate**: when crossing the `b` boundary, convert `a.ErrFoo` to a `b`-specific sentinel via `fmt.Errorf("...: %w", b.ErrFoo)`.

The current state is the worst of both: two distinct values that look identical, callers must check both, and the producer is never sure which one to return.

Practical failure modes:

- A developer assumes `b` exports `a`'s sentinel. Returns `a.ErrFoo` from a `b` function. Caller checks `errors.Is(err, b.ErrFoo)` — false. Silent miss.
- Maintainer of `b` changes the message ("b: foo"). Comparisons no longer "look identical" but the bug is the same.
- A monitoring rule based on `errors.Is(err, b.ErrFoo)` misses errors that come through `a`.

**Fix** — choose one sentinel design:
```go
package b
import "example.com/a"

var ErrFoo = a.ErrFoo // alias
```

Or:
```go
package b
import (
    "errors"
    "fmt"

    "example.com/a"
)

var ErrFoo = errors.New("b: foo")

func Wrap(err error) error {
    if errors.Is(err, a.ErrFoo) {
        return fmt.Errorf("b wrap: %w", ErrFoo)
    }
    return err
}
```

**Key lesson**: Re-declaring a sentinel by copy-pasting the message creates two parallel identities. The bug looks invisible because messages match. Either alias the original or translate at the boundary. Never both.

This is the mandated re-export bug case (find-bug #5).
</details>

---

## Bug 11 (Hard) — Capturing a sentinel in a closure that reassigns

```go
package main

import (
    "errors"
    "fmt"
)

func main() {
    err := errors.New("not found")
    isMissing := func(e error) bool {
        return errors.Is(e, err)
    }
    
    err = errors.New("changed") // reassigning the local
    
    target := errors.New("not found")
    fmt.Println(isMissing(target))
}
```

Output: `false`. Why does the test for "not found" miss?

<details>
<summary>Solution</summary>

**Bug**: Two issues compounded:

1. The closure `isMissing` captures the *variable* `err` by reference. Reassigning `err` changes what the closure compares against.
2. `target` is a fresh `errors.New("not found")` — a different allocation than the original `err`. Even before reassignment, the comparison was based on identity, not message.

After `err = errors.New("changed")`, `isMissing(target)` calls `errors.Is(target, err)` where `err` now points to the second allocation. `target` is yet a third allocation. No match.

**Fix** — capture by value (a snapshot) and avoid the runtime confusion:

```go
sentinel := errors.New("not found") // declared once, never reassigned
isMissing := func(e error) bool {
    return errors.Is(e, sentinel)
}
```

Or treat sentinels properly — declare them at package level:

```go
var ErrNotFound = errors.New("not found")

func main() {
    isMissing := func(e error) bool {
        return errors.Is(e, ErrNotFound)
    }
    target := someProducer()
    fmt.Println(isMissing(target))
}
```

And ensure `someProducer` returns the actual `ErrNotFound`, not a fresh `errors.New`.

**Key lesson**: Sentinels must be stable identities — declared once, never reassigned. Capturing them in closures is fine *if* the sentinel itself is stable. Reassigning a captured sentinel breaks every comparison the closure performs.
</details>

---

## Bug 12 (Hard) — Sentinel comparison with a typed-nil error

```go
package main

import (
    "errors"
    "fmt"
)

type myErr struct{ kind string }

func (m *myErr) Error() string { return "my: " + m.kind }

var ErrFoo = errors.New("foo")

func mayReturnNil() error {
    var e *myErr
    if false {
        e = &myErr{kind: "real"}
    }
    return e // typed nil
}

func main() {
    err := mayReturnNil()
    fmt.Println("err == nil:", err == nil)
    fmt.Println("Is ErrFoo:", errors.Is(err, ErrFoo))
    if err != nil {
        fmt.Println("entered error branch:", err)
    }
}
```

What does it print?

<details>
<summary>Solution</summary>

**Bug**: `mayReturnNil` returns a typed nil — an interface value with type `*myErr` and data pointer nil. The interface value is not `== nil` because the type pointer is non-nil. This is the classic typed-nil trap.

Output:
```
err == nil: false
Is ErrFoo: false
entered error branch: <nil>... (or actually panics if Error() dereferences nil)
```

`err == nil` is false. `errors.Is(err, ErrFoo)` is false (correct — nothing matches `ErrFoo`). The `if err != nil` branch fires; printing `err` calls `err.Error()` on a nil receiver, which panics if `Error()` dereferences the receiver.

**Sentinel-specific issue**: `errors.Is` itself behaves correctly here (it returns false). But the *outer* `if err != nil` is wrong, leading to spurious "error" handling. Sentinel checks downstream of a typed-nil-error trap inherit the brokenness.

**Fix** — never return a typed nil. Convert to `error` only when the value is actually non-nil:

```go
func mayReturnNil() error {
    var e *myErr
    if false {
        e = &myErr{kind: "real"}
    }
    if e == nil {
        return nil
    }
    return e
}
```

Or design the function to return `error` directly:

```go
func mayReturnNil() error {
    if condition {
        return &myErr{kind: "real"}
    }
    return nil
}
```

**Key lesson**: Sentinel comparisons via `errors.Is` are robust to typed nils. The bug is one level out — in `if err != nil` checks. Always return an untyped nil from functions whose return type is `error`. The interaction with sentinels is incidental but real: a typed-nil error sneaking into a sentinel-based classifier produces baffling "no sentinel matched, but err is non-nil" behavior.
</details>

---

## Bonus Bug (Hard) — Wrong wrap depth in a switch

```go
package main

import (
    "context"
    "errors"
    "fmt"
)

var ErrAppFailed = errors.New("app: failed")

func deep(ctx context.Context) error {
    return ctx.Err()
}

func mid(ctx context.Context) error {
    if err := deep(ctx); err != nil {
        return fmt.Errorf("mid wrap: %w", err) // wrap once
    }
    return nil
}

func top(ctx context.Context) error {
    if err := mid(ctx); err != nil {
        return fmt.Errorf("top: %s: %w", err.Error(), ErrAppFailed) // BUG
    }
    return nil
}

func main() {
    ctx, cancel := context.WithCancel(context.Background())
    cancel()
    err := top(ctx)
    
    fmt.Println("Is Canceled:", errors.Is(err, context.Canceled))
    fmt.Println("Is AppFailed:", errors.Is(err, ErrAppFailed))
}
```

Output:
```
Is Canceled: false
Is AppFailed: true
```

The top-level wrap loses cancellation context. Why?

<details>
<summary>Solution</summary>

**Bug**: `top` wraps with `%w` but `%w` is bound to `ErrAppFailed`, not to the inner `err`. The actual underlying chain is *broken*:

- The wrap chain from `top` is: `wrapper(ErrAppFailed) → ErrAppFailed`.
- The inner `err` (containing `mid → context.Canceled`) is rendered into the message via `%s` but is no longer in the chain.

`errors.Is(err, context.Canceled)` cannot reach `context.Canceled` — it's stringified, not chained. `errors.Is(err, ErrAppFailed)` succeeds.

The intent was probably to *both* wrap the cause AND tag it as `ErrAppFailed`. The pattern `fmt.Errorf("...: %s: %w", err.Error(), Tag)` is a frequent mistake.

**Fix options**:

Option 1 — wrap only the cause; tag via an `Is` method or a category sentinel:
```go
return fmt.Errorf("top: %w", err) // preserves the chain, including context.Canceled
```
And classify via a separate dimension. Lose the explicit `ErrAppFailed` tag.

Option 2 — multi-wrap (Go 1.20+):
```go
return fmt.Errorf("top: %w: %w", err, ErrAppFailed)
```
Both `errors.Is(err, context.Canceled)` and `errors.Is(err, ErrAppFailed)` return true. Use cautiously — multi-wrap chains can be confusing.

Option 3 — structured error containing both:
```go
type AppError struct {
    Cause error
}
func (a *AppError) Error() string { return "app: failed: " + a.Cause.Error() }
func (a *AppError) Unwrap() error { return a.Cause }
func (a *AppError) Is(target error) bool { return target == ErrAppFailed }

return &AppError{Cause: err}
```
Now `errors.Is` matches both `context.Canceled` (via `Unwrap`) and `ErrAppFailed` (via the `Is` method).

**Key lesson**: `%w` binds to one argument. If you want to express both "the cause is X" and "the category is Y", use multi-`%w` (Go 1.20+) or a structured error. Stringifying the inner error with `%s` and wrapping a tag with `%w` silently discards the original chain.
</details>
