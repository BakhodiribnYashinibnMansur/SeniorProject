# Go fmt — Find the Bug

## Instructions

Each exercise contains buggy Go code involving `fmt`. Identify the
bug, explain why, and provide the corrected code. Difficulty:
🟢 Easy, 🟡 Medium, 🔴 Hard.

---

## Bug 1 🟢 — %d on a String

```go
package main

import "fmt"

func main() {
    name := "Ada"
    fmt.Printf("Hello, %d\n", name)
}
```

What's the output?

<details>
<summary>Solution</summary>

**Bug**: `%d` is the integer verb but `name` is a `string`. `fmt`
substitutes a runtime placeholder:

```
Hello, %!d(string=Ada)
```

`go vet` warns:
```
./main.go:7:13: Printf format %d has arg name of wrong type string
```

**Fix**: use `%s` (or `%v`):
```go
fmt.Printf("Hello, %s\n", name)
```

**Key lesson**: `vet` catches verb/argument mismatches at build
time. Run it in CI; treat warnings as errors.
</details>

---

## Bug 2 🟢 — %s on a Struct With No Stringer

```go
package main

import "fmt"

type User struct {
    Name string
    Age  int
}

func main() {
    u := User{Name: "Ada", Age: 36}
    fmt.Printf("%s\n", u)
}
```

What's the output?

<details>
<summary>Solution</summary>

**Bug**: `%s` on a struct without `String()` falls back to a default
format that is **not** the struct's natural `%v` output:

```
{Ada 36}
```

(With Go ≥ 1.20 you may also see `{%!s(string=Ada) %!s(int=36)}`
depending on the formatter version; the visible behaviour is the
"no field names" struct dump.)

**Fix** — choose your representation:
```go
// Default
fmt.Printf("%v\n", u)   // {Ada 36}
// With field names
fmt.Printf("%+v\n", u)  // {Name:Ada Age:36}
// Or implement Stringer
func (u User) String() string { return u.Name }
fmt.Printf("%s\n", u)   // Ada
```

**Key lesson**: `%s` and `%v` both call `String()` if defined; for
types you print or log, define `String()`.
</details>

---

## Bug 3 🟢 — %v on a Nil Interface

```go
package main

import "fmt"

func main() {
    var err error
    fmt.Printf("error: %v\n", err)
}
```

What's the output?

<details>
<summary>Solution</summary>

**Output**: `error: <nil>`

**Subtle point**: This is **not** the same as a nil pointer holding
a concrete type. Compare:

```go
var p *MyError      // typed nil
var e error = p      // wraps typed nil

fmt.Printf("%v\n", e)        // <nil>  (or panics if (*MyError).Error() doesn't nil-check)
fmt.Println(e == nil)         // false! the interface has a type.
```

**Fix**: always check `err != nil` before formatting an error:

```go
if err != nil {
    fmt.Printf("error: %v\n", err)
}
```

**Key lesson**: `%v` on a nil interface prints `<nil>`. A typed nil
prints whatever the type's `String()` (or `Error()`) returns — which
might panic if the method dereferences without a nil guard.
</details>

---

## Bug 4 🟢 — %w in Sprintf

```go
package main

import (
    "errors"
    "fmt"
)

func main() {
    inner := errors.New("inner")
    msg := fmt.Sprintf("outer: %w", inner)
    fmt.Println(msg)
}
```

What's the output, and is the chain queryable?

<details>
<summary>Solution</summary>

**Output**:
```
outer: %!w(*errors.errorString=&{inner})
```

**Bug**: `%w` is recognised **only** by `fmt.Errorf`. In `Sprintf`
it falls back to a malformed-verb placeholder.

The "chain" doesn't exist: `Sprintf` returns a string, not an
error.

**Fix**: use `Errorf` instead:
```go
err := fmt.Errorf("outer: %w", inner)
fmt.Println(err)             // outer: inner
fmt.Println(errors.Unwrap(err)) // inner
```

If you genuinely want a string with `%v` formatting:
```go
msg := fmt.Sprintf("outer: %v", inner)
```

`go vet` catches this:
```
./main.go:9:24: Sprintf call has error-wrapping directive %w
```

`errorlint` warns even more aggressively.

**Key lesson**: `%w` is the wrap marker; only `Errorf` understands
it. Anywhere else it is a bug.
</details>

---

## Bug 5 🟡 — Stringer Infinite Recursion

```go
package main

import "fmt"

type M struct {
    X, Y int
}

func (m M) String() string {
    return fmt.Sprintf("%v", m)
}

func main() {
    fmt.Println(M{1, 2})
}
```

What happens?

<details>
<summary>Solution</summary>

**Bug**: `%v` of an `M` value calls `M.String()` (because `M`
implements `Stringer`). `String()` calls `fmt.Sprintf("%v", m)`,
which calls `String()` again, ... infinite recursion.

The Go runtime detects stack growth past a limit and crashes with:
```
runtime: goroutine stack exceeds 1000000000-byte limit
runtime: sp=0x... stack=[0x..., 0x...]
fatal error: stack overflow
```

**Fix 1** — use explicit field references:
```go
func (m M) String() string {
    return fmt.Sprintf("M{X:%d, Y:%d}", m.X, m.Y)
}
```

**Fix 2** — alias trick (strips the method from the type):
```go
func (m M) String() string {
    type alias M
    return fmt.Sprintf("%+v", alias(m))
}
```

**Key lesson**: `vet` does NOT catch this. Add a unit test that
calls `String()` and verifies it returns. Or always use explicit
field references inside `String()`.
</details>

---

## Bug 6 🟡 — Pointer Receiver Stringer on Value

```go
package main

import "fmt"

type T struct{ V int }

func (t *T) String() string {
    return fmt.Sprintf("T(%d)", t.V)
}

func main() {
    t := T{V: 42}
    fmt.Println(t)
    fmt.Println(&t)
}
```

What's printed?

<details>
<summary>Solution</summary>

**Output**:
```
{42}
T(42)
```

**Bug**: `String()` has a pointer receiver. `T` (a value) does not
implement `Stringer`; only `*T` does. So `fmt.Println(t)` falls
back to the default struct format `{42}`.

**Fix** — value receiver:
```go
func (t T) String() string {
    return fmt.Sprintf("T(%d)", t.V)
}
```

Or, if `T` is large and copying is expensive, always pass the
pointer:
```go
fmt.Println(&t)
```

**Key lesson**: Define `String()` on the value receiver unless the
type is meant to be used only by pointer (rare for small structs).
Same rule applies to `Format` and `GoString`.
</details>

---

## Bug 7 🟡 — Width Modifier Misuse

```go
package main

import "fmt"

func main() {
    fmt.Printf("%5d\n", "hello")
}
```

What's printed?

<details>
<summary>Solution</summary>

**Output**:
```
%!d(string=hello)
```

The width `5` is parsed but the verb mismatches the type. Width
applies only after type-checking succeeds.

**Subtler case** — width on a string:
```go
fmt.Printf("%5s\n", "hi")    //    hi
fmt.Printf("%.3s\n", "hello") // hel
fmt.Printf("%5.3s\n", "hello") //   hel
```

For strings: width = minimum chars; precision = maximum chars.

**Fix** — choose a verb that matches:
```go
fmt.Printf("%5s\n", "hello")
```

**Key lesson**: Width and precision have different meanings per
verb. Read the verb table.
</details>

---

## Bug 8 🟢 — Forgetting to Escape %

```go
package main

import "fmt"

func main() {
    fmt.Printf("100%\n")
}
```

What's printed?

<details>
<summary>Solution</summary>

**Output**:
```
100%!(NOVERB)
```

**Bug**: `%\n` is parsed as a malformed verb. The `%` is not
escaped.

**Fix**: double the `%`:
```go
fmt.Printf("100%%\n") // 100%
```

`vet` catches this:
```
./main.go:6:13: Printf format %\n has unknown verb \n
```

**Key lesson**: Inside a `Printf` format string, `%` is special.
Use `%%` for a literal percent sign.
</details>

---

## Bug 9 🟡 — Println in a Hot Loop

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    items := make([]int, 1_000_000)
    start := time.Now()
    for _, v := range items {
        fmt.Println("item:", v)
    }
    fmt.Println("elapsed:", time.Since(start))
}
```

What's the cost? What's the fix?

<details>
<summary>Solution</summary>

**Issue**: `fmt.Println` has three hidden costs in a tight loop:
1. Allocating an `[]any` for the variadic args.
2. Boxing `v` into an `any` (allocation if `v` falls outside the
   small-int interned range, free if inside).
3. The `os.Stdout` mutex lock per call.
4. The kernel write per line (no buffering by default).

For 1M iterations: ~10 seconds, ~3M allocations, ~200 MB of GC
pressure.

**Profile**:
```bash
go test -bench BenchmarkPrintln -benchmem -run=^$
go tool pprof cpu.out
# Top samples in fmt.(*pp).doPrint, runtime.mallocgc, syscall.write.
```

**Fix 1** — buffer the writer:
```go
bw := bufio.NewWriterSize(os.Stdout, 1<<20)
defer bw.Flush()
for _, v := range items {
    fmt.Fprintln(bw, "item:", v)
}
```

**Fix 2** — switch to `slog`:
```go
for _, v := range items {
    slog.Debug("item", "value", v)
}
```

**Fix 3** — don't log in a hot loop. Aggregate first.

**Key lesson**: `fmt.Println` is interactive-output speed.
1M calls/sec is wrong tooling.
</details>

---

## Bug 10 🟡 — %q on Bytes vs Strings

```go
package main

import "fmt"

func main() {
    s := "hello\nworld"
    b := []byte("hello\nworld")
    fmt.Printf("%q\n", s)
    fmt.Printf("%q\n", b)
}
```

What's printed?

<details>
<summary>Solution</summary>

**Output**:
```
"hello\nworld"
"hello\nworld"
```

Both produce the **same** output — `fmt` treats `%q` on `[]byte`
and `string` identically (both go through `strconv.Quote`).

**Subtle gotcha**: `%x` on the two **does** differ in style but not
in content:

```go
fmt.Printf("%x\n", "Go")     // 476f
fmt.Printf("%x\n", []byte("Go")) // 476f  (same)
fmt.Printf("% x\n", "Go")    // 47 6f
fmt.Printf("%X\n", []byte{0xde, 0xad}) // DEAD
```

**Subtler gotcha**: `%v` on `[]byte`:
```go
b := []byte("Go")
fmt.Printf("%v\n", b)  // [71 111]   ← decimal byte values, NOT the string "Go"
fmt.Printf("%s\n", b)  // Go         ← string view
```

**Bug** in code that does `%v` on `[]byte` expecting the string is
common. Use `%s` or convert with `string(b)`.

**Key lesson**: For `[]byte`, default `%v` is decimal-ints; use
`%s` for the string view.
</details>

---

## Bug 11 🟡 — Custom Error That Loses %w

```go
package main

import (
    "errors"
    "fmt"
    "io/fs"
    "os"
)

type AppError struct {
    Op  string
    Err error
}

func (e *AppError) Error() string {
    return fmt.Sprintf("%s: %v", e.Op, e.Err)
}

func main() {
    _, ioErr := os.Open("/no/such")
    err := &AppError{Op: "load", Err: ioErr}
    fmt.Println(err)
    fmt.Println(errors.Is(err, fs.ErrNotExist))
}
```

What's printed?

<details>
<summary>Solution</summary>

**Output**:
```
load: open /no/such: no such file or directory
false
```

**Bug**: `AppError` does not implement `Unwrap()`. `errors.Is` walks
the chain by calling `Unwrap`; without it, the chain ends at
`AppError` and `fs.ErrNotExist` is not found.

**Fix** — add `Unwrap`:
```go
func (e *AppError) Unwrap() error { return e.Err }
```

After:
```
load: open /no/such: no such file or directory
true
```

`errorlint`'s `wrap` rule and `staticcheck` SA9003 catch this.

**Key lesson**: A custom error that wraps another **must**
implement `Unwrap()` (or use `fmt.Errorf("...: %w", inner)`).
Otherwise `errors.Is/As` is broken.
</details>

---

## Bug 12 🔴 — User Input as Format String

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    msg := os.Args[1]
    fmt.Printf(msg)
}
```

What's the bug?

<details>
<summary>Solution</summary>

**Bug**: `msg` is user-controlled. If the user passes `%s %d %x %v`,
`fmt` interprets these as verbs and looks for arguments. Since
there are none, it prints `%!s(MISSING)` etc.

In Go this is mostly a *correctness* bug, not a memory-safety bug
(unlike C's `printf`), but it can:
- Leak verbose output the developer didn't intend.
- Cause user-controlled formatting in logs.
- Confuse log parsers.

**Fix** — use `Print` for literal output, or `%s` to constrain the
verb:
```go
fmt.Print(msg)
// or
fmt.Printf("%s", msg)
```

`staticcheck SA1006` catches this:
```
SA1006: Printf with non-constant format string
```

`go vet` with `-printf` also flags non-constant formats when given
the right configuration.

**Key lesson**: Format strings must be constants. Always.
</details>

---

## Bug 13 🟡 — Missing Argument Silent

```go
package main

import "fmt"

func main() {
    fmt.Printf("user=%s id=%d\n", "ada")
}
```

What's printed?

<details>
<summary>Solution</summary>

**Output**:
```
user=ada id=%!d(MISSING)
```

`fmt` does not error — it inserts a `MISSING` placeholder and
continues. This may end up in logs unnoticed.

`vet` catches this at compile time:
```
./main.go:6:13: Printf format %d reads arg #2, but call has 1 arg
```

**Fix**:
```go
fmt.Printf("user=%s id=%d\n", "ada", 7)
```

**Key lesson**: Always run `vet`. Always fix its `printf` warnings.
</details>

---

## Bug 14 🔴 — fmt.Errorf With Multiple %w and Nil

```go
package main

import (
    "errors"
    "fmt"
)

func main() {
    var cleanupErr error // nil
    primary := errors.New("primary")
    err := fmt.Errorf("step: %w; cleanup: %w", primary, cleanupErr)
    fmt.Println(err)
}
```

What happens?

<details>
<summary>Solution</summary>

**Bug**: Since Go 1.20, `fmt.Errorf` panics if any argument bound
to `%w` is `nil`:

```
panic: %w error operand cannot be nil

goroutine 1 [running]:
fmt.errorBufp.printf...
```

**Fix** — guard against nil:
```go
if cleanupErr != nil {
    err = fmt.Errorf("step: %w; cleanup: %w", primary, cleanupErr)
} else {
    err = fmt.Errorf("step: %w", primary)
}
```

Or use `errors.Join`, which silently ignores `nil`:
```go
err = errors.Join(primary, cleanupErr)
```

**Key lesson**: Multiple `%w` requires non-nil errors. `errors.Join`
is safer for collected errors.
</details>

---

## Bug 15 🟡 — Println Adds Spaces Where You Don't Want

```go
package main

import "fmt"

func main() {
    fmt.Println("price=", 99)
}
```

What's printed?

<details>
<summary>Solution</summary>

**Output**:
```
price= 99
```

Note the space between `=` and `99`. `Println` always adds a space
between operands.

**Fix 1** — use `Printf`:
```go
fmt.Printf("price=%d\n", 99)
```

**Fix 2** — use `Print` with concatenation:
```go
fmt.Print("price=", 99, "\n")
// price=99   (Print adds space only between non-strings)
```

Wait — that prints `price=99` because `"price="` is a string and
`99` is not, so no space.

**Subtle**: `Print(1, 2)` prints `1 2` (both non-strings). Test it:
```go
fmt.Print("a", 1)   // a1
fmt.Print(1, 2)     // 1 2
fmt.Print("a", "b") // ab
```

**Key lesson**: `Println` always adds spaces. `Print` adds them
only between two non-string args. Use `Printf` when you want
control.
</details>

---

## Bug 16 🔴 — Format That Panics on nil

```go
package main

import "fmt"

type N struct{ V int }

func (n *N) String() string {
    return fmt.Sprintf("N(%d)", n.V)
}

func main() {
    var p *N
    fmt.Println(p)
}
```

What happens?

<details>
<summary>Solution</summary>

**Output**: panics inside `String()`:
```
panic: runtime error: invalid memory address or nil pointer dereference
```

**Bug**: `String()` dereferences `n.V` without checking if `n` is
nil. `fmt.Println(p)` with `p == nil` calls `(*N)(nil).String()`,
which then nil-deref's.

`fmt` itself recovers from panics inside `String()` and prints
something like:
```
%!v(PANIC=String method: runtime error: invalid memory address or nil pointer dereference)
```

(but only because `fmt` defends against this — relying on it is
poor form.)

**Fix** — nil-check inside `String()`:
```go
func (n *N) String() string {
    if n == nil { return "<nil N>" }
    return fmt.Sprintf("N(%d)", n.V)
}
```

**Key lesson**: Pointer-receiver methods that may be called on a
nil receiver must nil-check. Otherwise `fmt.Println(nilP)` panics.
</details>

---

## Summary: 10 Mandated Bugs Coverage

| # | Bug | Where covered |
|---|-----|---------------|
| 1 | `%d` on string | Bug 1 |
| 2 | `%s` on non-Stringer struct | Bug 2 |
| 3 | `%v` on nil interface | Bug 3 |
| 4 | `%w` in Sprintf | Bug 4 |
| 5 | Stringer infinite recursion | Bug 5 |
| 6 | Pointer-receiver Stringer on value | Bug 6 |
| 7 | Width modifier misuse | Bug 7 |
| 8 | Forgetting to escape `%` | Bug 8 |
| 9 | Println in hot loop | Bug 9 |
| 10 | `%q` on bytes vs strings | Bug 10 |

Plus 6 bonus production traps: Unwrap missing, format-string
injection, missing argument, multiple `%w` with nil, Println
spacing, nil-receiver String panic.

The first line of defense is `go vet`. The second is `errorlint`
and `staticcheck`. The third is a habit of running every new
`String()` method through a `fmt.Println` test before shipping.
