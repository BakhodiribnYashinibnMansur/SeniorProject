# Go fmt — Tasks

## Instructions

Each task includes a description, starter code, expected output,
and an evaluation checklist. Use `fmt` idiomatically; prefer
`strconv` and `strings.Builder` when performance is asked for;
implement `Stringer`/`Formatter` when a type needs a custom
representation.

---

## Task 1 — Greeting Printer

**Difficulty**: Beginner
**Topic**: Print family

**Description**: Write a function `Greet(name string) string` that
returns `"Hello, <name>!"`.

**Starter Code**:
```go
package main

import "fmt"

func Greet(name string) string {
    // TODO
    return ""
}

func main() {
    fmt.Println(Greet("Ada"))
}
```

**Expected Output**:
```
Hello, Ada!
```

**Checklist**:
- [ ] Uses `fmt.Sprintf` (not string concatenation).
- [ ] Returns the string; `main` prints it.
- [ ] No trailing newline in the returned string.

---

## Task 2 — Aligned Table

**Difficulty**: Beginner
**Topic**: Width and precision

**Description**: Print this slice as a 3-column aligned table.
Names right-padded to 12 characters, counts right-aligned in 5
characters, percentages with two decimal places in 7 characters.

```go
items := []struct {
    Name  string
    Count int
    Share float64
}{
    {"alpha", 12, 0.04},
    {"beta-prime", 240, 0.83},
    {"gamma", 1, 0.001},
}
```

**Expected Output**:
```
alpha            12   4.00%
beta-prime      240  83.00%
gamma             1   0.10%
```

**Checklist**:
- [ ] Uses `Printf` with width and precision verbs.
- [ ] Columns line up regardless of name length.
- [ ] Uses `%%` for the literal percent sign.

---

## Task 3 — Error Wrapper

**Difficulty**: Beginner
**Topic**: `%w` and `errors.Is`

**Description**: Write `LoadConfig(path string) error` that calls
`os.Open(path)` and wraps any error with `fmt.Errorf("load
config: %w", err)`. Add a test that verifies
`errors.Is(LoadConfig("/no/such"), os.ErrNotExist)` is true.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
    "os"
)

func LoadConfig(path string) error {
    // TODO
    return nil
}

func main() {
    err := LoadConfig("/no/such")
    fmt.Println(err)
    fmt.Println("is not-exist:", errors.Is(err, os.ErrNotExist))
}
```

**Expected Output**:
```
load config: open /no/such: no such file or directory
is not-exist: true
```

**Checklist**:
- [ ] Uses `%w` (not `%v`).
- [ ] `errors.Is` returns true for the wrapped error.
- [ ] Returns nil on success.

---

## Task 4 — Stringer for Direction

**Difficulty**: Beginner
**Topic**: `Stringer` interface

**Description**: Define `type Direction int` with constants
`North`, `East`, `South`, `West`. Implement `String()` so
`fmt.Println(North)` prints `N`.

**Starter Code**:
```go
package main

import "fmt"

type Direction int

const (
    North Direction = iota
    East
    South
    West
)

// TODO: String()

func main() {
    for _, d := range []Direction{North, East, South, West} {
        fmt.Println(d)
    }
}
```

**Expected Output**:
```
N
E
S
W
```

**Checklist**:
- [ ] `String() string` defined on `Direction` (value receiver).
- [ ] Returns one of `N`, `E`, `S`, `W`.
- [ ] Out-of-range values return `Direction(N)` (e.g. via
      `strconv.Itoa`).

---

## Task 5 — Money Type

**Difficulty**: Intermediate
**Topic**: `Stringer` with formatting

**Description**: Implement `type Money struct{ Cents int64; Code
string }` with a `String()` method that returns
`"<dollars>.<cents> <code>"`. Cents must always be 2 digits, even
when zero.

**Starter Code**:
```go
package main

import "fmt"

type Money struct {
    Cents int64
    Code  string
}

// TODO: String()

func main() {
    fmt.Println(Money{1234, "USD"}) // 12.34 USD
    fmt.Println(Money{900, "USD"})  // 9.00 USD
    fmt.Println(Money{5, "USD"})    // 0.05 USD
}
```

**Expected Output**:
```
12.34 USD
9.00 USD
0.05 USD
```

**Checklist**:
- [ ] Uses `fmt.Sprintf("%d.%02d %s", ...)` or equivalent.
- [ ] Handles cent values < 10 (e.g. `9.05`, not `9.5`).
- [ ] Value receiver (so both `Money` and `*Money` print the same).

---

## Task 6 — Custom Error With Unwrap

**Difficulty**: Intermediate
**Topic**: `error`, `Unwrap`, `errors.Is`

**Description**: Define `type AppError struct{ Op string; Err error
}` that:
1. Implements `error` with `Error() = "<op>: <inner>"`.
2. Implements `Unwrap() error`.
3. Wraps a base sentinel `ErrNotFound` so that `errors.Is(appErr,
   ErrNotFound)` returns true.

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("not found")

type AppError struct {
    Op  string
    Err error
}

// TODO: Error, Unwrap

func main() {
    err := &AppError{Op: "load", Err: ErrNotFound}
    fmt.Println(err)
    fmt.Println(errors.Is(err, ErrNotFound))
}
```

**Expected Output**:
```
load: not found
true
```

**Checklist**:
- [ ] `Error()` returns `Op + ": " + Err.Error()`.
- [ ] `Unwrap()` returns `e.Err`.
- [ ] `errors.Is` walks through.

---

## Task 7 — Format Method With Verbose Mode

**Difficulty**: Intermediate
**Topic**: `Formatter` interface

**Description**: Add a `Format(fmt.State, rune)` method to
`AppError` from Task 6 such that `%v` prints `Op: Inner`, and `%+v`
prints
```
Op
  cause: <inner.Error()>
```

**Starter Code**:
```go
package main

import (
    "errors"
    "fmt"
)

type AppError struct {
    Op  string
    Err error
}

func (e *AppError) Error() string  { return e.Op + ": " + e.Err.Error() }
func (e *AppError) Unwrap() error  { return e.Err }

// TODO: Format

func main() {
    err := &AppError{Op: "load", Err: errors.New("disk full")}
    fmt.Printf("%v\n", err)
    fmt.Printf("%+v\n", err)
}
```

**Expected Output**:
```
load: disk full
load
  cause: disk full
```

**Checklist**:
- [ ] `Format` switches on `verb`.
- [ ] Honours `f.Flag('+')`.
- [ ] Uses `fmt.Fprintf(f, ...)` to write through the State.

---

## Task 8 — Sprintf vs Builder Benchmark

**Difficulty**: Intermediate
**Topic**: Performance comparison

**Description**: Write benchmarks that compare:
1. `fmt.Sprintf("user-%d-%s", id, kind)`
2. `strings.Builder` with `Grow` and `strconv.Itoa`.

Run on 10000 iterations of `id = i`, `kind = "profile"`.

**Starter Code**:
```go
package fmtbench

import (
    "fmt"
    "strconv"
    "strings"
    "testing"
)

func BenchmarkSprintf(b *testing.B) {
    for i := 0; i < b.N; i++ {
        _ = fmt.Sprintf("user-%d-%s", i, "profile")
    }
}

func BenchmarkBuilder(b *testing.B) {
    for i := 0; i < b.N; i++ {
        var sb strings.Builder
        sb.Grow(20)
        sb.WriteString("user-")
        sb.WriteString(strconv.Itoa(i))
        sb.WriteByte('-')
        sb.WriteString("profile")
        _ = sb.String()
    }
}
```

**Expected Outcome**:
- `BenchmarkBuilder` is at least 2x faster than `BenchmarkSprintf`.
- Builder uses fewer allocations per iteration.

**Checklist**:
- [ ] Run `go test -bench . -benchmem`.
- [ ] Compare ns/op and allocs/op.
- [ ] Document results in a comment.

---

## Task 9 — Color Formatter With Custom Verbs

**Difficulty**: Advanced
**Topic**: `Formatter`, custom verbs

**Description**: Define `type Color struct{ R, G, B uint8 }` with a
`Format` method supporting:
- `%v` and `%s` → `(R,G,B)`
- `%h` → `#rrggbb`
- `%r` → `rgb(R,G,B)`

**Starter Code**:
```go
package main

import "fmt"

type Color struct{ R, G, B uint8 }

// TODO: Format

func main() {
    c := Color{255, 128, 0}
    fmt.Printf("%v %s %h %r\n", c, c, c, c)
}
```

**Expected Output**:
```
(255,128,0) (255,128,0) #ff8000 rgb(255,128,0)
```

**Checklist**:
- [ ] Switch on `verb` rune.
- [ ] Default branch handles unsupported verbs.
- [ ] No call to `fmt.Sprintf("%v", c)` inside `Format` (recursion).

---

## Task 10 — Stringer Codegen

**Difficulty**: Intermediate
**Topic**: `golang.org/x/tools/cmd/stringer`

**Description**: Define an enum type `type Status int` with
`StatusPending`, `StatusRunning`, `StatusDone`. Use `go generate`
with `stringer -type=Status -trimprefix=Status` to generate a
`String()` method. Verify that `fmt.Println(StatusRunning)` prints
`Running`.

**Starter Code**:
```go
package main

//go:generate stringer -type=Status -trimprefix=Status

type Status int

const (
    StatusPending Status = iota
    StatusRunning
    StatusDone
)
```

**Steps**:
1. Install: `go install golang.org/x/tools/cmd/stringer@latest`
2. Run: `go generate ./...`
3. A file `status_string.go` should appear.
4. Add `func main() { fmt.Println(StatusRunning) }`.

**Expected Output**:
```
Running
```

**Checklist**:
- [ ] `stringer` is installed.
- [ ] Generated file exists.
- [ ] `Println` prints the trimmed name.

---

## Task 11 — Hex Dumper

**Difficulty**: Intermediate
**Topic**: `%x` and `%X`

**Description**: Write `Hex(b []byte) string` that returns a hex
dump like `de ad be ef`. Lowercase, space-separated.

**Starter Code**:
```go
package main

import "fmt"

func Hex(b []byte) string {
    // TODO
    return ""
}

func main() {
    fmt.Println(Hex([]byte{0xde, 0xad, 0xbe, 0xef}))
}
```

**Expected Output**:
```
de ad be ef
```

**Checklist**:
- [ ] Uses `%x` (or `% x` for built-in space separation).
- [ ] Trims any trailing space if you build manually.
- [ ] Works for empty slices (returns `""`).

---

## Task 12 — Sscanf Parser

**Difficulty**: Intermediate
**Topic**: `Sscanf`

**Description**: Parse the line `"user=42 ip=10.0.0.1"` into `id
int` and `ip string`. Use `fmt.Sscanf`. Return an error if parsing
fails.

**Starter Code**:
```go
package main

import "fmt"

func Parse(line string) (int, string, error) {
    var id int
    var ip string
    // TODO
    return id, ip, nil
}

func main() {
    id, ip, err := Parse("user=42 ip=10.0.0.1")
    fmt.Println(id, ip, err)
}
```

**Expected Output**:
```
42 10.0.0.1 <nil>
```

**Checklist**:
- [ ] Uses `Sscanf`.
- [ ] Format string `"user=%d ip=%s"`.
- [ ] Returns the error from `Sscanf` on mismatch.

---

## Task 13 — Multi-Line Logger

**Difficulty**: Advanced
**Topic**: `Fprintln` to a buffered writer

**Description**: Implement `func Logger(out io.Writer) *Log` with
methods:
- `Infof(format string, args ...any)` — writes `"INFO: <fmt> + \n"`.
- `Errorf(format string, args ...any)` — writes `"ERROR: <fmt> +
  \n"`.
- `Flush() error`.

Internally wrap `out` with `bufio.Writer`. Demonstrate usage.

**Starter Code**:
```go
package main

import (
    "bufio"
    "fmt"
    "io"
    "os"
)

type Log struct {
    w *bufio.Writer
}

func Logger(out io.Writer) *Log { /* TODO */ return nil }
func (l *Log) Infof(format string, args ...any) { /* TODO */ }
func (l *Log) Errorf(format string, args ...any) { /* TODO */ }
func (l *Log) Flush() error { /* TODO */ return nil }

func main() {
    l := Logger(os.Stdout)
    l.Infof("started %s", "service")
    l.Errorf("disk %d%%", 92)
    l.Flush()
}
```

**Expected Output**:
```
INFO: started service
ERROR: disk 92%
```

**Checklist**:
- [ ] Wraps `out` with `bufio.NewWriter`.
- [ ] `Infof`/`Errorf` use `fmt.Fprintf`.
- [ ] `Flush` returns the writer's flush error.
- [ ] No `fmt.Println`.

---

## Task 14 — Optimised Tabular Output

**Difficulty**: Advanced
**Topic**: Performance, `bufio.Writer`, `strconv.AppendInt`

**Description**: Print 1M rows `"<id> <name>\n"` to stdout. Beat
`fmt.Printf` by 5x.

**Starter Code**:
```go
package main

import (
    "bufio"
    "os"
    "strconv"
)

func main() {
    w := bufio.NewWriterSize(os.Stdout, 1<<20)
    defer w.Flush()
    var line []byte
    for i := 0; i < 1_000_000; i++ {
        line = strconv.AppendInt(line[:0], int64(i), 10)
        line = append(line, ' ')
        line = append(line, "name"...)
        line = append(line, '\n')
        w.Write(line)
    }
}
```

**Goal**:
- Run with `time ./prog > /dev/null` and compare to a `fmt.Printf`
  baseline.
- Expect 5x or better speedup.

**Checklist**:
- [ ] Uses `bufio.Writer` (1 MB buffer).
- [ ] Uses `strconv.AppendInt` (no `Sprintf`).
- [ ] Reuses `line` slice (no per-iteration alloc).
- [ ] Flushes on exit.

---

## Task 15 — Test the Stringer Recursion

**Difficulty**: Intermediate
**Topic**: Defensive testing

**Description**: Given a buggy `String()` that recurses:

```go
type T struct{ X int }
func (t T) String() string { return fmt.Sprintf("%v", t) } // BUG
```

Write a test that detects the recursion in finite time (without
stack-overflowing the test process). Hints: use a goroutine with a
timeout, or detect via `runtime.Stack` after a deferred recover.

**Starter Code**:
```go
package fmtrecover_test

import (
    "fmt"
    "testing"
    "time"
)

type T struct{ X int }

func (t T) String() string { return fmt.Sprintf("%v", t) } // BUG

func TestStringerTerminates(t *testing.T) {
    done := make(chan struct{})
    var got string
    go func() {
        defer func() {
            if r := recover(); r != nil { /* expected on overflow */ }
            close(done)
        }()
        got = fmt.Sprintf("%v", T{1})
    }()
    select {
    case <-done:
        t.Logf("returned: %q", got)
    case <-time.After(2 * time.Second):
        t.Fatal("String() did not terminate; recursion suspected")
    }
}
```

**Checklist**:
- [ ] Goroutine isolates the panicking work.
- [ ] Timeout detects infinite recursion.
- [ ] Test fails for the buggy version, passes once the bug is
      fixed.

---

## Summary

These 15 tasks cover:
- The Print/Sprint/Fprint families (Tasks 1, 11, 13, 14).
- Verbs, width, precision (Tasks 2, 11).
- Error wrapping (Tasks 3, 6).
- `Stringer` (Tasks 4, 5, 10).
- `Formatter` (Tasks 7, 9).
- Codegen with `stringer` (Task 10).
- Performance optimisation (Tasks 8, 14).
- Scanning (Task 12).
- Defensive testing (Task 15).

Work through them in order; later tasks build on earlier ones (Task
7 extends Task 6; Task 14 builds on Task 13). Run `go vet` and
`golangci-lint run` on each to catch verb mismatches and
`%w`-discipline issues.
