# Go fmt — Middle Level

## 1. Introduction

At the middle level, `fmt` becomes a tool you use deliberately:
formatting goes through `Sprintf` only when the cost is acceptable;
error wrapping uses `%w` and never `%v`; structured logs go to
`slog`, not `Printf`; and you know the verb table well enough to
read a format string at a glance.

This leaf covers the patterns that distinguish day-one users from
people who own production code: choosing between `Sprintf` and
`strings.Builder`, designing custom error chains, formatting tabular
data with `text/tabwriter`, and recognising the cases where `fmt` is
the wrong package and `slog`, `strconv`, or hand-rolled bytes
belong instead.

---

## 2. Prerequisites

- Junior-level `fmt` content.
- `io.Writer`, `bytes.Buffer`, `strings.Builder`.
- Go errors (`errors.Is`, `errors.As`, `errors.Unwrap`).
- A reading-level acquaintance with
  [`slog`](../07-slog/) and
  [`strconv`](https://pkg.go.dev/strconv).

---

## 3. Glossary

| Term | Definition |
|------|-----------|
| Wrapping | Embedding an error inside another so `errors.Is/As` walks the chain |
| Sentinel | A package-level error compared with `errors.Is` |
| Format directive | `%[flags][width][.precision]<verb>` |
| Indexed argument | `%[N]v` — refer to the Nth argument |
| Stringer | Method `String() string`; called by `%s` and `%v` |
| Tabwriter | `text/tabwriter` aligns `\t`-separated columns |
| pp | `fmt`'s internal printer state, pooled in `sync.Pool` |

---

## 4. Core Concepts

### 4.1 The Verb Table You Need by Heart

| Verb | Type | Notes |
|------|------|-------|
| `%v` | any | Default; uses `String()` if defined |
| `%+v` | struct | Adds field names |
| `%#v` | any | Go-syntax representation; uses `GoString()` if defined |
| `%T` | any | The type, e.g. `*main.User` |
| `%d` | int | Decimal |
| `%b` | int | Binary |
| `%o` | int | Octal |
| `%x` `%X` | int / []byte / string | Hex (lower / upper) |
| `%c` | rune | Character |
| `%U` | rune | Unicode `U+1234` |
| `%f` | float | Decimal, no exponent |
| `%e` `%E` | float | Scientific |
| `%g` `%G` | float | Choose `%e` or `%f` based on magnitude |
| `%s` | string | Plain |
| `%q` | string / rune | Go-quoted |
| `%p` | pointer / slice / map / chan / func | `0xAddress` |
| `%t` | bool | `true` / `false` |
| `%w` | error | **Errorf-only** — wrap |

### 4.2 Width, Precision, Flags

The full grammar is:

```
% [flags] [width] [.precision] [argument index] verb
```

Flags:

| Flag | Meaning |
|------|---------|
| `-` | Left-align in the width |
| `+` | Always print sign on numbers |
| `#` | Alternate form (e.g. `0x` prefix on `%#x`) |
| `0` | Pad with zeros instead of spaces |
| ` ` (space) | Leave a space for the sign of a number |

Examples:
```go
fmt.Printf("%+d %+d\n", 5, -5)      // +5 -5
fmt.Printf("%#x %#o\n", 255, 8)      // 0xff 010
fmt.Printf("%5d|%-5d|\n", 42, 42)    //    42|42   |
fmt.Printf("%.3f\n", 1.0/7.0)        // 0.143
fmt.Printf("%6.2f\n", 3.14)          //   3.14
```

### 4.3 Indexed Arguments

`%[N]verb` references the Nth argument. Useful for printing the
same value with two verbs and for translation:

```go
fmt.Printf("%[1]d in hex is %[1]x\n", 255) // 255 in hex is ff
```

After an explicit `[N]`, the next non-indexed verb refers to the
argument at `N+1`.

### 4.4 Sprintf vs strings.Builder vs strconv

```go
// One-shot, readable, allocates.
key := fmt.Sprintf("u:%d:%s", id, kind)

// Hot loop, low allocations.
var b strings.Builder
b.Grow(32)
b.WriteString("u:")
b.WriteString(strconv.Itoa(id))
b.WriteByte(':')
b.WriteString(kind)
key := b.String()

// Single conversion, fastest.
s := strconv.Itoa(id)
```

Rule of thumb: until profiling says otherwise, use `Sprintf`. When a
benchmark shows `Sprintf` in the top, switch to `Builder` +
`strconv`.

### 4.5 Errorf and the %w Verb

`fmt.Errorf` with `%w` returns an error that wraps another:

```go
return fmt.Errorf("load %s: %w", path, err)
```

Why this matters:

- `errors.Is(returned, os.ErrNotExist)` walks the chain.
- `errors.As(returned, &netErr)` finds `*net.OpError` within.
- The string representation includes both messages, separated by
  `: `.

Multiple `%w` (Go 1.20+):
```go
return fmt.Errorf("step failed: %w; cleanup: %w", primary, cleanup)
// Both errors join the chain; errors.Is matches either.
```

Without `%w`, the wrapped error is just text — `errors.Is` cannot
see it.

### 4.6 Fprint into an io.Writer

`Fprintf` is your bridge between `fmt`'s formatting and any
destination:

```go
// HTTP handler
fmt.Fprintf(w, "Hello, %s\n", user)

// Log to a file
fmt.Fprintf(logFile, "[%s] %s\n", time.Now().Format(time.RFC3339), msg)

// Build a buffer
var buf bytes.Buffer
fmt.Fprintf(&buf, "page %d of %d", n, total)
```

### 4.7 The Stringer Interface (Preview)

Any type with `String() string` is automatically formatted by `%v`
and `%s`:

```go
type Direction int

const (
    North Direction = iota
    East
    South
    West
)

func (d Direction) String() string {
    return [...]string{"N", "E", "S", "W"}[d]
}

fmt.Println(North) // N
```

Senior-level material covers `Stringer`, `GoStringer`, and
`Formatter` in depth.

### 4.8 When NOT to Use fmt

| Situation | Use instead |
|-----------|-------------|
| Long-running service logs | [`slog`](../07-slog/) |
| Building a large string | `strings.Builder` |
| Single int → string | `strconv.Itoa` |
| Single float → string | `strconv.FormatFloat` |
| Building bytes | `bytes.Buffer` or `append` |
| User-facing translations | `golang.org/x/text/message` |
| Tabular CLI output | `text/tabwriter` |
| Pretty-printing structs | `encoding/json` with `MarshalIndent` |

`fmt` is the right answer for one-shot formatting and for `Stringer`
support. It is the wrong answer for hot paths and structured logs.

### 4.9 Tabular Output with text/tabwriter

```go
package main

import (
    "fmt"
    "os"
    "text/tabwriter"
)

func main() {
    w := tabwriter.NewWriter(os.Stdout, 0, 0, 2, ' ', 0)
    fmt.Fprintln(w, "name\tqty\tcost")
    fmt.Fprintln(w, "apple\t3\t$0.50")
    fmt.Fprintln(w, "watermelon\t1\t$4.00")
    w.Flush()
}
```

Output:
```
name        qty  cost
apple       3    $0.50
watermelon  1    $4.00
```

`tabwriter` aligns `\t`-separated columns. Use it instead of
hand-tuned widths for variable-length data.

### 4.10 Scan and Sscan

```go
var name string
var age int

// From stdin
fmt.Scan(&name, &age)

// From a string
fmt.Sscan("Ada 36", &name, &age)

// With a format
fmt.Sscanf("name=Ada age=36", "name=%s age=%d", &name, &age)

// From any io.Reader
fmt.Fscan(r, &name, &age)
```

Whitespace separates tokens. For non-trivial input parsing, reach
for `bufio.Scanner` or `encoding/json`.

### 4.11 Print Spacing Rules in Detail

`Print(a, b)` adds a space between `a` and `b` only if neither is a
string.

```go
fmt.Print("a", "b")  // ab
fmt.Print(1, 2)      // 1 2
fmt.Print("a", 1)    // a1   (NO space — one is a string)
```

`Println(a, b)` always adds a space:

```go
fmt.Println("a", 1)  // a 1
```

### 4.12 The String/Error Promotion Rule

When a verb expects a string (`%s`, `%q`, `%x`, `%X`, `%v` of a
non-numeric, etc.) `fmt` checks, in order:

1. Does the value implement `Formatter`? Use it.
2. (For `%v`) does it implement `GoStringer` and the verb is `%#v`? Use it.
3. Does it implement `error`? Use `Error()`.
4. Does it implement `Stringer`? Use `String()`.
5. Otherwise, fall back to reflection.

`error` is checked **before** `Stringer`, so a type implementing
both will use `Error()` for `%s`/`%v`.

---

## 5. Real-World Analogies

**A tax form**. The format string is the form's printed text. The
verbs are the boxes you fill in. Width and precision are the size of
the boxes. `%w` is "see schedule attached": the wrapped error is the
schedule that auditors (i.e. `errors.Is`) can walk.

**A typesetter**. `Sprintf` is hand-typesetting one line at a time.
`Builder` is a typewriter. `tabwriter` is a column-laying tool. For
a whole book, you'd reach for a typesetting system (`text/template`,
`html/template`).

**A receipt printer**. `Println` is the kitchen ticket printer:
quick and unstructured. `slog` is the structured POS log: every
record has known fields and goes to a database. Use the right tool
for the consumer.

---

## 6. Mental Models

```
fmt.Errorf("read %s: %w", path, ioErr)
        │     │     │
        │     │     └── %w wraps ioErr (only in Errorf)
        │     └── %s prints path (string)
        └── format string
```

`%w` does two things: it formats the inner error (like `%v`) and
links it via `Unwrap` so `errors.Is/As` walk the chain.

```
errors chain:
    "read /etc/x: open /etc/x: permission denied"
       │             │
       └── outer ────┴── wrapped by %w
                          ↑
                     errors.Is(err, fs.ErrPermission) → true
```

---

## 7. Pros & Cons of fmt at Middle Scale

### Pros

- The verb table is stable and well documented.
- `%w` makes wrapping a one-liner.
- `Sprintf` builds keys, paths, and SQL fragments quickly.
- `Fprintf` works with anything that implements `io.Writer`.

### Cons

- Allocates per call; bad in tight loops.
- Format strings are checked only by `vet`, never by the type
  system.
- Reflection-based; type errors surface at runtime as `%!d(...)`.
- For multiline structured data, `text/template` or `slog` is
  cleaner.

---

## 8. Use Cases

1. Wrapping errors in a service layer.
2. Building cache keys and Redis keys.
3. Producing CLI tables with `tabwriter`.
4. Writing HTTP handler responses for small sites.
5. Generating SQL fragments **without** user input.
6. Pretty-printing config dumps with `%+v`.
7. Producing fixture strings in tests.
8. Implementing `String()` methods.
9. Hex dumping bytes with `%x`.

---

## 9. Code Examples (Worked Examples)

### Example 1 — Layered error wrapping

```go
package main

import (
    "errors"
    "fmt"
    "os"
)

type AppError struct {
    Op   string
    Err  error
}

func (e *AppError) Error() string { return fmt.Sprintf("%s: %v", e.Op, e.Err) }
func (e *AppError) Unwrap() error { return e.Err }

func loadUser(id int) error {
    _, err := os.Open(fmt.Sprintf("users/%d.json", id))
    if err != nil {
        return &AppError{Op: "loadUser", Err: fmt.Errorf("open: %w", err)}
    }
    return nil
}

func main() {
    err := loadUser(42)
    fmt.Println(err)
    fmt.Println("is not exist:", errors.Is(err, os.ErrNotExist))
}
```

Output:
```
loadUser: open: open users/42.json: no such file or directory
is not exist: true
```

The `%w` inside both layers preserves the chain.

### Example 2 — Aligned table without tabwriter

```go
package main

import "fmt"

type row struct {
    name string
    cnt  int
    pct  float64
}

func main() {
    rows := []row{
        {"alpha", 12, 0.04},
        {"beta-prime", 240, 0.83},
        {"gamma", 1, 0.001},
    }
    fmt.Printf("%-12s %6s %8s\n", "name", "count", "share")
    for _, r := range rows {
        fmt.Printf("%-12s %6d %7.2f%%\n", r.name, r.cnt, r.pct*100)
    }
}
```

### Example 3 — Sprintf vs Builder benchmark

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
        _ = fmt.Sprintf("u:%d:%s", i, "profile")
    }
}

func BenchmarkBuilder(b *testing.B) {
    for i := 0; i < b.N; i++ {
        var sb strings.Builder
        sb.Grow(20)
        sb.WriteString("u:")
        sb.WriteString(strconv.Itoa(i))
        sb.WriteString(":profile")
        _ = sb.String()
    }
}
```

Typical results on Go 1.22 / amd64:
```
BenchmarkSprintf-8   18000000   65 ns/op   24 B/op   2 allocs/op
BenchmarkBuilder-8   60000000   17 ns/op    8 B/op   1 allocs/op
```

`Builder` wins by ~4x. Use `Sprintf` for clarity and `Builder` when
the bench says so.

### Example 4 — Custom Stringer

```go
package main

import "fmt"

type Money struct {
    Cents int64
    Code  string
}

func (m Money) String() string {
    return fmt.Sprintf("%d.%02d %s", m.Cents/100, m.Cents%100, m.Code)
}

func main() {
    m := Money{Cents: 1234, Code: "USD"}
    fmt.Println(m)              // 12.34 USD
    fmt.Printf("%s\n", m)       // 12.34 USD
    fmt.Printf("%v\n", m)       // 12.34 USD
    fmt.Printf("%+v\n", m)      // 12.34 USD  (Stringer wins over %+v)
    fmt.Printf("%#v\n", m)      // main.Money{Cents:1234, Code:"USD"}
}
```

`%+v` and `%#v` mostly bypass `Stringer` only for `%#v` (which
prefers `GoStringer`).

### Example 5 — Fprintf to http.ResponseWriter

```go
package main

import (
    "fmt"
    "net/http"
)

func health(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "text/plain; charset=utf-8")
    fmt.Fprintf(w, "ok %s %d\n", r.Method, r.ContentLength)
}

func main() {
    http.HandleFunc("/health", health)
    http.ListenAndServe(":8080", nil)
}
```

### Example 6 — Hex dump

```go
package main

import "fmt"

func main() {
    b := []byte{0xde, 0xad, 0xbe, 0xef}
    fmt.Printf("%x\n", b)   // deadbeef
    fmt.Printf("%X\n", b)   // DEADBEEF
    fmt.Printf("% x\n", b)  // de ad be ef
    fmt.Printf("%#x\n", b)  // 0xdeadbeef
}
```

### Example 7 — Sscanf for fixed parsing

```go
package main

import "fmt"

func main() {
    line := "user=42 ip=10.0.0.1"
    var id int
    var ip string
    n, err := fmt.Sscanf(line, "user=%d ip=%s", &id, &ip)
    fmt.Println(n, err, id, ip) // 2 <nil> 42 10.0.0.1
}
```

`Sscanf` is fragile for variable input — use a real parser when the
shape is not fixed.

---

## 10. Coding Patterns

### Pattern 1 — Wrap-then-check

```go
if err := step(); err != nil {
    return fmt.Errorf("step %d: %w", n, err)
}
// caller side
if errors.Is(retErr, sentinel) { ... }
```

### Pattern 2 — Builder for keys in hot loops

```go
func userKey(id int, kind string) string {
    var b strings.Builder
    b.Grow(8 + len(kind))
    b.WriteString("u:")
    b.WriteString(strconv.Itoa(id))
    b.WriteByte(':')
    b.WriteString(kind)
    return b.String()
}
```

### Pattern 3 — Indexed args for translations

```go
fmt.Printf("user %[1]s logged in (%[1]s id=%[2]d)\n", "ada", 7)
```

### Pattern 4 — Fprintln for log lines

```go
fmt.Fprintf(os.Stderr, "[%s] %s: %v\n",
    time.Now().Format(time.RFC3339), op, err)
```

### Pattern 5 — Sprintf for SQL parameters list

```go
placeholders := make([]string, len(ids))
for i := range ids {
    placeholders[i] = fmt.Sprintf("$%d", i+1)
}
query := "SELECT * FROM t WHERE id IN (" +
    strings.Join(placeholders, ",") + ")"
```

### Pattern 6 — Stringer for enums

```go
type Status int

const (
    StatusPending Status = iota
    StatusRunning
    StatusDone
)

func (s Status) String() string {
    switch s {
    case StatusPending:
        return "pending"
    case StatusRunning:
        return "running"
    case StatusDone:
        return "done"
    }
    return fmt.Sprintf("Status(%d)", int(s))
}
```

---

## 11. Clean Code Guidelines

1. Wrap with `%w` whenever the caller might want to use
   `errors.Is/As`.
2. Keep format strings as constants; never build them at runtime.
3. Use `%v` for one-off debugging; pick a specific verb for
   user-visible output.
4. Implement `String()` once per type instead of writing the same
   `Sprintf` everywhere.
5. Run `go vet` in CI; treat its `printf` warnings as errors.

```go
// Good
const tmpl = "user=%d action=%s"
return fmt.Errorf(tmpl+": %w", id, action, err)

// Bad
tmpl := "user=" + actionName + "=%d" // dynamic format string
fmt.Errorf(tmpl, id)
```

---

## 12. Product Use / Feature Example

**A request logger middleware** that uses `fmt` for the line and
`Fprintln` for the destination. Production code would use `slog`,
but this is a clean illustration:

```go
package main

import (
    "fmt"
    "io"
    "net/http"
    "time"
)

type lwriter struct {
    http.ResponseWriter
    status int
    bytes  int
}

func (w *lwriter) WriteHeader(code int) {
    w.status = code
    w.ResponseWriter.WriteHeader(code)
}

func (w *lwriter) Write(b []byte) (int, error) {
    n, err := w.ResponseWriter.Write(b)
    w.bytes += n
    return n, err
}

func logger(out io.Writer, h http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        lw := &lwriter{ResponseWriter: w, status: 200}
        start := time.Now()
        h.ServeHTTP(lw, r)
        fmt.Fprintf(out, "%s %d %dB %v %s %s\n",
            r.Method, lw.status, lw.bytes,
            time.Since(start).Round(time.Microsecond),
            r.RemoteAddr, r.URL.Path)
    })
}
```

For real services, swap the `Fprintf` line for an `slog.Info` call.

---

## 13. Error Handling

### 13.1 Wrapping vs Stringing

```go
// Just text — errors.Is can't see the cause
return fmt.Errorf("load: %v", err)

// Wrapped — errors.Is walks the chain
return fmt.Errorf("load: %w", err)
```

Use `%w` whenever the caller might want to inspect.

### 13.2 Multiple %w (Go 1.20+)

```go
return fmt.Errorf("step: %w; cleanup: %w", err1, err2)
```

`errors.Is(err, x)` returns true if either chain contains `x`.

### 13.3 Custom errors with Format

A type that implements both `error` and `fmt.Formatter` controls its
own representation:

```go
type Q struct{ Op, Key string; Err error }

func (q *Q) Error() string  { return q.Op + " " + q.Key + ": " + q.Err.Error() }
func (q *Q) Unwrap() error  { return q.Err }
func (q *Q) Format(s fmt.State, verb rune) {
    switch verb {
    case 'v':
        if s.Flag('+') {
            fmt.Fprintf(s, "%s %s\n  caused by: %+v", q.Op, q.Key, q.Err)
            return
        }
        fmt.Fprint(s, q.Error())
    case 's':
        fmt.Fprint(s, q.Error())
    case 'q':
        fmt.Fprintf(s, "%q", q.Error())
    }
}
```

`pkg/errors` and modern `cockroachdb/errors` use this pattern to
produce stack traces under `%+v`.

---

## 14. Security Considerations

1. **Format strings must be constants.** A user-controlled format
   string is a verb-injection bug. `staticcheck SA1006` catches
   `fmt.Printf(userInput)`.
2. **Avoid printing whole structs that contain secrets.** Implement
   a `String()` that redacts:
   ```go
   func (c Credentials) String() string {
       return fmt.Sprintf("Credentials{User:%q, Pass:[redacted]}", c.User)
   }
   ```
3. **Don't build SQL with `Sprintf`** — use parameterised queries.
4. **Don't use `fmt.Sscanf` on attacker input** — its parsing rules
   are surprising; use `encoding/json` or a real parser.

---

## 15. Performance Tips

1. `Sprintf` allocates ~2 allocs per call (one for the buffer, one
   for the result string). For 1k calls/sec it is invisible; for
   1M/sec it is the top hot spot.
2. `Println` allocates an `[]any` for the variadic args.
3. `Builder` plus `strconv` is the fastest portable option for
   numbers.
4. `bytes.Buffer` is fine for `[]byte` building; cheaper than
   `strings.Builder` only when you already have one.
5. The `fmt` package pools its printer state (`pp`) in
   `sync.Pool`; reuse is automatic.

---

## 16. Metrics & Analytics

- `pprof` heap: `fmt.Sprintf` showing > 5% of allocations is a
  signal to switch hot paths to `Builder` + `strconv`.
- `pprof` cpu: `fmt.(*pp).doPrintf` near the top suggests the same.
- For service logs, `slog` plus `slogs` benchmarks (zero-alloc
  `JSONHandler`) outperforms `Printf` lines by a wide margin.

---

## 17. Best Practices

1. Use `%w` for wrapping — never `%v` for that purpose.
2. Keep format strings constant; let `vet` check them.
3. Use `Println` for human-readable output, `Errorf` for errors,
   `Sprintf` for IDs and keys.
4. Prefer `slog` over `Printf` in long-running services.
5. Implement `String()` for types you print or log frequently.
6. Use `text/tabwriter` for variable-width columns.
7. In hot loops, profile before micro-optimising; only switch to
   `Builder`/`strconv` if it shows up.

---

## 18. Edge Cases & Pitfalls

### Pitfall 1 — Stringer Recursion

```go
type T struct{ X int }
func (t T) String() string { return fmt.Sprintf("%v", t) } // infinite!
```

`%v` re-enters `String()`. Use `%+v` of an alias type, or hand-build:

```go
func (t T) String() string { return fmt.Sprintf("T{X:%d}", t.X) }
```

### Pitfall 2 — Pointer Receiver vs Value

```go
type T struct{ X int }
func (t *T) String() string { return "T!" }

var t T
fmt.Println(t)  // {0}      — value, no String() called
fmt.Println(&t) // T!       — pointer, String() called
```

Fix: define on the value receiver if you want both to work:
```go
func (t T) String() string { return "T!" }
```

### Pitfall 3 — %w in Sprintf

```go
s := fmt.Sprintf("err: %w", err) // "err: %!w(*errors.errorString=...)"
```

Use `%v` in `Sprintf` and `%w` only in `Errorf`.

### Pitfall 4 — Width on Strings

```go
fmt.Printf("%5s|\n", "hi")  //    hi|
fmt.Printf("%-5s|\n", "hi") // hi   |
fmt.Printf("%.3s|\n", "hello") // hel|  (precision = max width for strings)
```

Precision on a string truncates.

### Pitfall 5 — %v on nil interface

```go
var e error
fmt.Printf("%v\n", e) // <nil>
```

`%v` of a nil interface is `<nil>`, not blank. To detect a real
error, use `if err != nil` first.

### Pitfall 6 — Print spacing rules

```go
fmt.Print("a", 1, "b") // a1b — strings on each side bracket the int
fmt.Print(1, 2)        // 1 2 — both non-strings; space inserted
```

`Println` always spaces; `Print` only between non-strings.

---

## 19. Common Mistakes

| Mistake | Fix |
|---------|-----|
| `fmt.Errorf("...: %v", err)` for wrapping | Use `%w` |
| Building format strings at runtime | Keep them constant |
| `Sprintf("%d", x)` in tight loop | `strconv.Itoa(x)` |
| `Println` for service logs | `slog.Info` |
| Pointer-receiver `String()` then printing the value | Use value receiver |
| `Sprintf("%w", err)` | Use `Errorf` |

---

## 20. Common Misconceptions

**Misconception 1**: "`%v` always calls `String()`."
**Truth**: It calls `String()` if defined, but only on values whose
type implements `Stringer`. For a struct printed via `%+v`, field
values still go through their own formatting rules.

**Misconception 2**: "`%w` makes the wrapping invisible."
**Truth**: `%w` formats the inner error like `%v` AND makes
`errors.Is/As` walk it. The text is the same as `%v` — the
difference is the unwrap chain.

**Misconception 3**: "`fmt.Sprintf` is the canonical way to convert
an int to a string."
**Truth**: `strconv.Itoa(n)` is canonical, faster, and zero-alloc
for small ints.

**Misconception 4**: "`Println` is faster than `Printf` because it
has no format parsing."
**Truth**: `Println` parses every argument by reflection and adds
spacing logic; benchmarks usually find it on par or slower than a
`Printf("...\n")` with a fixed format.

**Misconception 5**: "Width and precision do nothing on strings."
**Truth**: Width pads; precision truncates.

---

## 21. Tricky Points

1. The order `fmt` checks interfaces: `Formatter` → `error` →
   `Stringer`. `error` beats `Stringer`.
2. `%v` of a struct uses `String()` if defined, even when the
   struct's fields are also Stringers.
3. `%[N]v` reuses an argument; the next non-indexed verb continues
   from `N+1`.
4. `%w` only inside `Errorf`; elsewhere it falls back to a `%!w`
   placeholder.
5. `%T` on `nil` prints `<nil>`; on a typed nil it prints the type.

---

## 22. Test

```go
package fmt_test

import (
    "errors"
    "fmt"
    "io/fs"
    "strings"
    "testing"
)

func TestErrorChain(t *testing.T) {
    inner := fs.ErrNotExist
    outer := fmt.Errorf("load: %w", inner)
    if !errors.Is(outer, fs.ErrNotExist) {
        t.Fatal("expected chain to contain fs.ErrNotExist")
    }
    if !strings.Contains(outer.Error(), "load: ") {
        t.Fatal("expected prefix")
    }
}

func TestStringerVsErrorf(t *testing.T) {
    s := fmt.Sprintf("v=%v", customError{})
    if s != "v=boom" {
        t.Fatalf("got %q", s)
    }
}

type customError struct{}

func (customError) Error() string  { return "boom" }
func (customError) String() string { return "stringer" }
```

---

## 23. Tricky Questions

**Q1**: A type implements both `Stringer` and `error`. Which method
does `%v` call?
**A**: `Error()`. `error` beats `Stringer`.

**Q2**: What does `fmt.Printf("%[2]d %[1]s\n", "x", 7)` print?
**A**: `7 x` — indexed args reorder.

**Q3**: What does `fmt.Println(1, 2, 3)` print, character by
character?
**A**: `1`, space, `2`, space, `3`, newline. Always spaced.

**Q4**: After `fmt.Errorf("x: %w; y: %w", a, b)`, does
`errors.Is(err, a)` return true? `errors.Is(err, b)`?
**A**: Both true (Go 1.20+).

---

## 24. Cheat Sheet

```go
// Wrap an error
fmt.Errorf("op: %w", err)

// Default format with field names
fmt.Printf("%+v\n", obj)

// Go-syntax form
fmt.Printf("%#v\n", obj)

// Indexed
fmt.Printf("%[1]s = %[1]q\n", s)

// Width / precision
fmt.Printf("%-10s %5.2f\n", name, val)

// Hex dump
fmt.Printf("% x\n", b)

// Build a key
key := fmt.Sprintf("u:%d:%s", id, kind)

// Hot loop?
var sb strings.Builder
sb.WriteString("u:")
sb.WriteString(strconv.Itoa(id))
key := sb.String()
```

---

## 25. Self-Assessment Checklist

- [ ] I know all the verbs in the table.
- [ ] I use `%w` only inside `Errorf`.
- [ ] I keep format strings constant.
- [ ] I implement `String()` on types I log frequently.
- [ ] I know when to swap `Sprintf` for `Builder` + `strconv`.
- [ ] I recognise the cases where `slog` beats `Printf`.
- [ ] I know the order: `Formatter` → `error` → `Stringer`.
- [ ] I run `go vet` and read its `printf` warnings.

---

## 26. Summary

At the middle level you treat `fmt` as a tool with sharp edges:
fluent for one-shot formatting, costly in hot paths, perfect for
error wrapping, mediocre for structured logs. You wrap with `%w`,
keep format strings constant, lean on `Stringer`, and reach for
`slog`, `strconv`, or `Builder` whenever the situation calls for it.
You read `vet` warnings and pre-empt the runtime placeholders that
otherwise leak into production.

---

## 27. What You Can Build

- A logging middleware with `Fprintf`.
- A CLI tool with aligned columns via `tabwriter`.
- An error type with `Format` for stack traces.
- A small DSL parser using `Sscanf` (for fixed input).
- A REPL where `Print` and `Println` build the loop.
- A health-check handler with `Fprintf(w, ...)`.

---

## 28. Further Reading

- [pkg.go.dev/fmt](https://pkg.go.dev/fmt) — full docs.
- [Working with Errors in Go 1.13](https://go.dev/blog/go1.13-errors)
  — original `%w` writeup.
- [staticcheck SA1006 / SA9006](https://staticcheck.dev/docs/checks/) —
  printf-style checks.
- [`text/tabwriter`](https://pkg.go.dev/text/tabwriter) — tabular
  output companion.

---

## 29. Related Topics

- 8.7 `log/slog` — structured logging.
- 5.4 `fmt.Errorf` — focused deep dive on `%w`.
- 8.1 `io` and file handling — every `Fprintf` writes to a writer.
- 8.16 `sort/slices/maps` — companion ergonomic stdlib helpers.

---

## 30. Diagrams & Visual Aids

### Verb dispatch with interfaces

```mermaid
flowchart TD
    A[fmt sees a value] --> B{implements Formatter?}
    B -- yes --> C[call Format]
    B -- no --> D{verb is %#v}
    D -- yes --> E{implements GoStringer?}
    E -- yes --> F[call GoString]
    E -- no --> G[default Go-syntax]
    D -- no --> H{implements error?}
    H -- yes --> I[call Error]
    H -- no --> J{implements Stringer?}
    J -- yes --> K[call String]
    J -- no --> L[reflect-based default]
```

### Wrapping chain

```
fmt.Errorf("a: %w", fmt.Errorf("b: %w", io.EOF))

err.Error()  →  "a: b: EOF"

Unwrap chain:
  err  →  inner1  →  inner2 (= io.EOF)

errors.Is(err, io.EOF)  →  true
```
