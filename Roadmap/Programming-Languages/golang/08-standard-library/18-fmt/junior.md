# Go fmt — Junior Level

## 1. Introduction

### What is it?

The `fmt` package is Go's standard formatted-I/O package, modelled
after C's `printf` and `scanf`. It is the package you reach for when
you want to:

- Print a value to the screen.
- Build a string from a template (`"user=%s id=%d"`).
- Wrap an error with extra context.
- Read user input from stdin.

Almost every Go program imports `fmt`. The package lives at
[`pkg.go.dev/fmt`](https://pkg.go.dev/fmt) and has been part of the
standard library since Go 1.0.

### How to use it?

```go
package main

import "fmt"

func main() {
    name := "Ada"
    age := 36
    fmt.Println("Hello,", name)            // Hello, Ada
    fmt.Printf("name=%s age=%d\n", name, age) // name=Ada age=36
    s := fmt.Sprintf("user-%d", age)       // returns "user-36"
    fmt.Println(s)
}
```

Three things to notice:

1. `Println` adds a newline and puts spaces between arguments.
2. `Printf` does not add a newline; you write `\n` yourself.
3. `Sprintf` returns the string instead of writing it.

---

## 2. Prerequisites

- Variables, basic types (`int`, `string`, `bool`, `float64`).
- Functions and the `error` type.
- Slices, maps, and structs at a reading level.
- A rough idea of `io.Writer` (it has a `Write([]byte) (int, error)`
  method) — needed for the `Fprint*` family.

---

## 3. Glossary

| Term | Definition |
|------|-----------|
| Verb | A `%`-prefixed token in a format string (`%d`, `%s`, ...) |
| Argument | A value supplied after the format string, consumed by a verb |
| Format string | The first parameter of any `Printf`-style call |
| Width | Minimum column width, e.g. `%5d` |
| Precision | Digits after the decimal, e.g. `%.2f` |
| Stringer | Any type with a `String() string` method |
| io.Writer | Anything with `Write([]byte) (int, error)`; receives `Fprint*` output |
| stdout | The default destination for `Print*`; OS file descriptor 1 |
| stderr | OS file descriptor 2; `fmt.Fprintln(os.Stderr, ...)` writes there |

---

## 4. Core Concepts

### 4.1 The Three Families

`fmt` has three families of formatting functions, distinguished by
where the result goes.

```go
fmt.Print("hello")            // → stdout
s := fmt.Sprint("hello")      // → returns string s
fmt.Fprint(os.Stderr, "hi")   // → any io.Writer
```

Within each family there are three variants:

| Variant | Behavior |
|---------|----------|
| `Print` | concatenate, default formatting |
| `Println` | like `Print` plus a trailing newline; spaces between args |
| `Printf` | uses a format string with verbs |

So you have nine functions: `Print`, `Println`, `Printf`, `Sprint`,
`Sprintln`, `Sprintf`, `Fprint`, `Fprintln`, `Fprintf`, plus
`Errorf` (returns `error`) and the `Scan*` family (read from input).

### 4.2 The First Five Verbs

These five verbs cover 80% of day-one needs:

| Verb | Meaning | Example |
|------|---------|---------|
| `%v` | default format | `fmt.Printf("%v", 42)` → `42` |
| `%s` | string | `fmt.Printf("%s", "hi")` → `hi` |
| `%d` | integer (decimal) | `fmt.Printf("%d", 42)` → `42` |
| `%t` | boolean | `fmt.Printf("%t", true)` → `true` |
| `%f` | float | `fmt.Printf("%f", 1.5)` → `1.500000` |

`%v` works on **any** type. When in doubt, reach for `%v` first.

### 4.3 Newlines

`Println` adds a newline; `Printf` does not.

```go
fmt.Println("a", "b")  // a b\n
fmt.Printf("a b")      // a b   (no newline; cursor stays on the line)
fmt.Printf("a b\n")    // a b\n
```

Forgetting `\n` in `Printf` is the most common day-one bug — output
appears glued to the next line.

### 4.4 Many Arguments at Once

A format string consumes one argument per verb, left to right.

```go
fmt.Printf("%s is %d years old\n", "Ada", 36)
// Ada is 36 years old
```

If you supply too few arguments, you get a placeholder error:
`%!d(MISSING)`. Too many arguments are reported as
`%!(EXTRA <type>=<value>)`.

### 4.5 Sprintf Returns a String

`Sprintf` is the same as `Printf` but it returns the result instead
of printing it. Use it whenever you need a string built from parts.

```go
key := fmt.Sprintf("user:%d:name", 42) // "user:42:name"
```

This is the most-used member of the family inside library code.

### 4.6 Errorf Returns an error

`fmt.Errorf` is `Sprintf` plus `errors.New`: it formats and returns
an `error`.

```go
n := -1
err := fmt.Errorf("bad count: %d", n)
fmt.Println(err) // bad count: -1
```

The special verb `%w` (we'll see it in Section 4.10) lets you wrap
an existing error.

### 4.7 Fprintf Writes Anywhere

`Fprintf` takes any `io.Writer` as its first argument. Every place
you can write bytes — stdout, stderr, a file, an HTTP response, a
buffer — works.

```go
fmt.Fprintln(os.Stderr, "warning: low disk")
fmt.Fprintf(w, "Hello %s\n", name) // w is, say, http.ResponseWriter
```

`Print` is just `Fprint(os.Stdout, ...)` under the hood.

### 4.8 Default Format with %v

`%v` picks a sensible default for any type:

```go
fmt.Printf("%v\n", 42)            // 42
fmt.Printf("%v\n", "hi")          // hi
fmt.Printf("%v\n", true)          // true
fmt.Printf("%v\n", []int{1,2,3})  // [1 2 3]
fmt.Printf("%v\n", map[string]int{"a":1}) // map[a:1]
fmt.Printf("%v\n", struct{X int}{1}) // {1}
```

For structs, `%v` prints field values without names. `%+v` adds
field names; `%#v` prints Go syntax that you can paste back into
code.

```go
p := struct{ X, Y int }{1, 2}
fmt.Printf("%v\n", p)   // {1 2}
fmt.Printf("%+v\n", p)  // {X:1 Y:2}
fmt.Printf("%#v\n", p)  // struct { X int; Y int }{X:1, Y:2}
```

### 4.9 String Format with %s and %q

`%s` prints a string as is. `%q` quotes it (Go-syntax escaping).

```go
fmt.Printf("%s\n", "hello\nworld")  // hello
                                    // world
fmt.Printf("%q\n", "hello\nworld")  // "hello\nworld"
```

`%q` is great for log lines because it makes whitespace and
non-printable bytes visible.

### 4.10 The %w Verb (Error Wrapping)

In Go 1.13+, `fmt.Errorf` recognises `%w` to **wrap** an underlying
error. The result implements `Unwrap()` so `errors.Is`/`As` can walk
the chain.

```go
src, err := os.Open("config.toml")
if err != nil {
    return fmt.Errorf("load config: %w", err)
}
```

`%w` only works inside `fmt.Errorf`. Using it in `Sprintf` silently
falls back to `%v` (we cover this in `find-bug.md`).

### 4.11 Width and Precision

Numbers between `%` and the verb specify width (minimum columns) and
precision (digits or significant figures).

```go
fmt.Printf("%5d|\n", 42)     //    42|   (right-aligned in 5 cols)
fmt.Printf("%-5d|\n", 42)    // 42   |   (left-aligned with `-`)
fmt.Printf("%05d|\n", 42)    // 00042|   (zero-padded)
fmt.Printf("%.2f\n", 3.14159) // 3.14
fmt.Printf("%8.2f\n", 3.14)  //     3.14
```

Width and precision can be passed as arguments using `*`:

```go
fmt.Printf("%*d\n", 5, 42) //    42
```

### 4.12 Reading Input with Scan

`Scan*` reads from stdin into pointers.

```go
var name string
var age int
fmt.Print("name age: ")
fmt.Scan(&name, &age)
fmt.Printf("%s is %d\n", name, age)
```

For text-mode applications you usually want
[`bufio.Scanner`](../06-bufio/) instead — `fmt.Scan` is whitespace
tokenized and easy to misuse.

---

## 5. Real-World Analogies

**A photo-printer**. `Println` is the auto mode: take any picture,
print it at the default size. `Printf` is manual mode: you choose the
paper, the orientation, and the borders. `Sprintf` is "send to PDF
instead of paper": you get a digital file you can pass around.

**A mail-merge template**. The format string is the letter template.
The verbs are the placeholders (`{name}`, `{date}`). The arguments
fill them in. If you forget a placeholder, the recipient sees `Hi
%!s(MISSING)` instead of `Hi Ada`.

**A receipt printer at a store**. `Print*` writes to the till. `Fprint`
lets you redirect to any printer (including a "dump to file" printer
for record-keeping). `Sprint` is the screen preview before you print.

---

## 6. Mental Models

```
fmt.Printf("%s=%d\n", k, v)
       │     │  │
       │     │  └── \n: literal newline
       │     │
       │     └── %d: integer verb, consumes v
       │
       └── %s: string verb, consumes k
```

For each verb, `fmt` uses reflection to read the matching argument
and writes formatted bytes. For `Print` and `Sprint`, no format
string is parsed — values are printed with their default format
separated by spaces (or no separator for `Print`).

```
Print family
┌──────────────────────────────┐
│  Print*  → os.Stdout         │
│  Sprint* → returns string    │
│  Fprint* → io.Writer arg     │
│  Errorf  → error             │
│  Scan*   → reads stdin       │
└──────────────────────────────┘
```

---

## 7. Pros & Cons

### Pros

- One package covers stdout, stderr, files, strings, and errors.
- `%v` "just works" on any type.
- The `Stringer` interface lets your types print themselves.
- Familiar to anyone who has used `printf` in C, Python, or shell.

### Cons

- Reflection-based; slower than `strconv` for hot paths.
- Easy to misuse: wrong verb, missing argument, forgotten `\n`.
- `Println` adds spaces between arguments; people forget.
- For structured logging you want
  [`slog`](../07-slog/), not `fmt`.
- Format strings are not type-checked at compile time (only `vet`
  catches them).

---

## 8. Use Cases

1. CLI output and progress messages.
2. Error wrapping with `fmt.Errorf("...: %w", err)`.
3. Building keys, paths, and SQL fragments with `Sprintf`.
4. Implementing `String()` on your own types (covered in
   `senior.md`).
5. Quick debugging with `%+v` and `%#v`.
6. Writing to `bytes.Buffer` or `strings.Builder` for in-memory
   strings.
7. Writing to `http.ResponseWriter` in toy HTTP handlers.
8. Producing test failure messages in `t.Errorf`.

---

## 9. Code Examples

### Example 1 — Print, Println, Printf

```go
package main

import "fmt"

func main() {
    fmt.Print("a", "b", "\n")     // ab
    fmt.Println("a", "b")         // a b
    fmt.Printf("a%sb\n", "-")     // a-b
}
```

`Print` does not add separators between arguments unless neither is
a string. `Println` adds spaces and a final newline.

### Example 2 — Sprintf for keys

```go
package main

import "fmt"

func cacheKey(userID int, kind string) string {
    return fmt.Sprintf("user:%d:%s", userID, kind)
}

func main() {
    fmt.Println(cacheKey(42, "profile")) // user:42:profile
}
```

### Example 3 — Errorf and wrapping

```go
package main

import (
    "errors"
    "fmt"
    "os"
)

func loadConfig(path string) error {
    if _, err := os.Stat(path); err != nil {
        return fmt.Errorf("load %s: %w", path, err)
    }
    return nil
}

func main() {
    err := loadConfig("/no/such/file")
    fmt.Println(err)                          // load /no/such/file: stat ...: no such file
    fmt.Println(errors.Is(err, os.ErrNotExist)) // true
}
```

### Example 4 — Width and precision

```go
package main

import "fmt"

func main() {
    items := []struct {
        name string
        qty  int
        cost float64
    }{
        {"apple", 3, 0.5},
        {"plum", 12, 0.25},
        {"watermelon", 1, 4.0},
    }
    for _, it := range items {
        fmt.Printf("%-12s %3d  $%6.2f\n", it.name, it.qty, it.cost)
    }
}
```

Output:
```
apple          3  $  0.50
plum          12  $  0.25
watermelon     1  $  4.00
```

### Example 5 — Fprintf to stderr

```go
package main

import (
    "fmt"
    "os"
)

func warn(format string, args ...any) {
    fmt.Fprintf(os.Stderr, "warning: "+format+"\n", args...)
}

func main() {
    warn("disk usage at %d%%", 92)
}
```

### Example 6 — Default vs %+v vs %#v

```go
package main

import "fmt"

type Point struct{ X, Y int }

func main() {
    p := Point{1, 2}
    fmt.Printf("%v\n", p)   // {1 2}
    fmt.Printf("%+v\n", p)  // {X:1 Y:2}
    fmt.Printf("%#v\n", p)  // main.Point{X:1, Y:2}
}
```

### Example 7 — Scan

```go
package main

import "fmt"

func main() {
    var name string
    var age int
    fmt.Print("Enter name and age: ")
    n, err := fmt.Scan(&name, &age)
    if err != nil {
        fmt.Println("read error:", err)
        return
    }
    fmt.Printf("read %d values: %s, %d\n", n, name, age)
}
```

---

## 10. Coding Patterns

### Pattern 1 — Build a key/path

```go
url := fmt.Sprintf("/users/%d/posts/%d", userID, postID)
```

### Pattern 2 — Wrap an error with context

```go
if err := step(); err != nil {
    return fmt.Errorf("step %d: %w", n, err)
}
```

### Pattern 3 — Print a struct for debugging

```go
fmt.Printf("debug: %+v\n", req)
```

### Pattern 4 — Print to stderr

```go
fmt.Fprintln(os.Stderr, "fatal: missing config")
```

### Pattern 5 — Tabular output

```go
for _, row := range rows {
    fmt.Printf("%-20s %5d\n", row.Name, row.Count)
}
```

---

## 11. Clean Code Guidelines

1. Use `Println` when you don't need a format string. It is shorter
   and harder to misuse.
2. Use `%v` when you don't care about the exact representation; use
   `%d`, `%s`, etc. when you do.
3. Always include `\n` in the format string of `Printf`, never at
   the end of an argument.
4. Prefer `%w` for error wrapping; it preserves the chain.
5. Write to `os.Stderr` for warnings and errors, `os.Stdout` for
   normal output. Pipelines depend on this split.

```go
// Good
fmt.Fprintln(os.Stderr, "warn: retrying")
return fmt.Errorf("dial %s: %w", addr, err)

// Bad
fmt.Println("warn: retrying")             // pollutes stdout
return fmt.Errorf("dial %s: %v", addr, err) // no wrapping
```

---

## 12. Product Use / Feature Example

**A CLI that summarises a directory.** It prints a header, one row
per file, and a total — with width-aligned columns.

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    if len(os.Args) < 2 {
        fmt.Fprintln(os.Stderr, "usage: dirsum <path>")
        os.Exit(2)
    }
    path := os.Args[1]
    entries, err := os.ReadDir(path)
    if err != nil {
        fmt.Fprintf(os.Stderr, "read %s: %v\n", path, err)
        os.Exit(1)
    }
    fmt.Printf("%-30s %10s\n", "name", "size")
    var total int64
    for _, e := range entries {
        info, err := e.Info()
        if err != nil { continue }
        fmt.Printf("%-30s %10d\n", e.Name(), info.Size())
        total += info.Size()
    }
    fmt.Printf("%-30s %10d\n", "TOTAL", total)
}
```

The whole UI is `fmt`. No third-party formatter needed.

---

## 13. Error Handling

`fmt.Print*` ignores the return value most of the time. The full
signature is:

```go
func Println(a ...any) (n int, err error)
```

Errors come from the underlying writer (e.g. a closed pipe). For
stdout this is rare. For `Fprintf` to a network connection or a
file you may want to check.

```go
if _, err := fmt.Fprintln(w, "ok"); err != nil {
    return fmt.Errorf("write response: %w", err)
}
```

`fmt.Errorf` itself never fails; it returns an `error` value
unconditionally.

---

## 14. Security Considerations

1. **Never use user input as the format string.** This is the Go
   equivalent of `printf(user_input)` in C.
   ```go
   // BAD: user can inject %s and %d to read uninitialized memory
   //      slots from your stack of args... ish.
   fmt.Printf(userInput)
   // Good
   fmt.Print(userInput) // prints literally
   // Or
   fmt.Printf("%s", userInput)
   ```
2. **Avoid logging secrets.** `%v` and `%+v` print all struct fields,
   including `Password` if it is exported. Implement `String()` to
   redact, or tag the field and use a custom logger.
3. **Sprintf-built SQL is a SQL-injection vector.** Use
   parameterised queries.
   ```go
   // BAD
   q := fmt.Sprintf("SELECT * FROM users WHERE id=%s", id)
   // Good
   db.Query("SELECT * FROM users WHERE id=$1", id)
   ```

---

## 15. Performance Tips

1. `fmt.Sprintf` allocates. For one-off code this is fine.
2. In hot loops, `strconv.Itoa(n)` is ~3x faster than
   `fmt.Sprintf("%d", n)`.
3. For building strings from many parts, use `strings.Builder` or
   `bytes.Buffer` and call `b.WriteString` / `fmt.Fprintf(&b, ...)`.
4. `Println` allocates an `[]any` for the arguments — fine for
   logs, painful in tight loops.
5. For long-lived services, prefer
   [`slog`](../07-slog/) over `fmt.Println` — slog batches and
   structures.

---

## 16. Metrics & Analytics

- `runtime.MemStats` and `pprof` show `fmt.Sprintf` near the top of
  many Go services' heap profiles. It's not always wrong, but it's
  worth measuring.
- `go test -bench . -benchmem` makes it visible:
  ```
  BenchmarkSprintfInt-8   30000000   45 ns/op   16 B/op   2 allocs/op
  BenchmarkItoa-8        100000000   12 ns/op    0 B/op   0 allocs/op
  ```

---

## 17. Best Practices

1. Use `%v` until you have a reason to use a specific verb.
2. Use `%w` for wrapping errors, never `%v` for that purpose.
3. Use `Sprintf` for keys and identifiers; use `Builder` for hot
   loops.
4. Always include `\n` in `Printf` format strings.
5. Write user-visible output to `Stdout`, diagnostics to `Stderr`.
6. Implement `String() string` for types you print often.
7. Run `go vet` — its `printf` analyzer catches almost every
   verb/argument mismatch.

---

## 18. Edge Cases & Pitfalls

### Pitfall 1 — Wrong Verb For The Type

```go
fmt.Printf("%d\n", "hi") // %!d(string=hi)
```

`vet` warns:
```
go vet: Printf format %d has arg "hi" of wrong type string
```

Fix: use `%s`.

### Pitfall 2 — Missing Argument

```go
fmt.Printf("%s %d\n", "hi") // hi %!d(MISSING)
```

Fix: supply the argument.

### Pitfall 3 — Extra Argument

```go
fmt.Printf("%s\n", "hi", 99) // hi\n%!(EXTRA int=99)
```

Fix: remove the extra argument or add a verb for it.

### Pitfall 4 — Forgotten \n

```go
fmt.Printf("ready") // no newline; next line glues on
fmt.Printf(" go\n")
// Output: ready go
```

Fix: include `\n` whenever the line should end.

### Pitfall 5 — Println Adds Spaces

```go
fmt.Println("price=", price) // price= 99   (space before 99)
```

Fix:
```go
fmt.Printf("price=%d\n", price)
// or
fmt.Println("price=" + strconv.Itoa(price))
```

### Pitfall 6 — User Input as Format String

```go
fmt.Printf(input) // arbitrary verb interpretation
```

Fix:
```go
fmt.Print(input)        // literal
fmt.Printf("%s", input) // literal via %s
```

### Pitfall 7 — %w in Sprintf

```go
s := fmt.Sprintf("err: %w", err) // %w is treated as %v silently
```

Fix: only use `%w` inside `fmt.Errorf`.

### Pitfall 8 — Forgetting to Escape %

```go
fmt.Printf("100%\n") // %!\n(MISSING)
fmt.Printf("100%%\n") // 100%
```

Fix: double the `%`.

---

## 19. Common Mistakes

| Mistake | Fix |
|---------|-----|
| `%d` on a string | Use `%s` (or `%v`) |
| `%w` in `Sprintf` | Use `%v`, or move to `Errorf` |
| Missing `\n` in `Printf` | Add it |
| User input as format | `fmt.Print(input)` or `%s` |
| Forgetting to double `%` | `100%%` |
| Using `Sprintf` in a tight loop | `strconv` or `Builder` |

---

## 20. Common Misconceptions

**Misconception 1**: "`Println` and `Printf` are the same."
**Truth**: `Println` adds spaces between arguments and a trailing
newline; `Printf` neither.

**Misconception 2**: "`%v` and `%s` are interchangeable."
**Truth**: `%v` calls `String()` if defined and falls back to a
default format. `%s` requires the value be a string or implement
`String()`/`Error()`/`MarshalText()`.

**Misconception 3**: "`fmt.Sprintf` is fast."
**Truth**: It allocates, reflects, and is ~3x slower than `strconv`
for primitives.

**Misconception 4**: "`%w` is a real verb that prints something."
**Truth**: `%w` is a marker recognised only by `fmt.Errorf`. In other
contexts it falls back to `%v`.

**Misconception 5**: "`Print` and `Println` add spaces the same
way."
**Truth**: `Print` adds spaces only between two non-string
arguments. `Println` always adds spaces.

---

## 21. Tricky Points

1. `Print` and `Println` differ in spacing rules.
2. `%v` calls `String()`; `%s` does too — but not on integers.
3. `%w` only works in `Errorf`.
4. `%T` prints the type of the argument, not its value.
5. `%v` of a `nil` interface is `<nil>`.

---

## 22. Test

```go
package main

import (
    "fmt"
    "strings"
    "testing"
)

func TestSprintf(t *testing.T) {
    s := fmt.Sprintf("user-%d", 42)
    if s != "user-42" {
        t.Errorf("got %q, want %q", s, "user-42")
    }
}

func TestErrorfWrap(t *testing.T) {
    inner := fmt.Errorf("inner")
    outer := fmt.Errorf("outer: %w", inner)
    if !strings.Contains(outer.Error(), "outer: inner") {
        t.Errorf("got %q, missing wrap", outer.Error())
    }
}
```

---

## 23. Tricky Questions

**Q1**: What is printed?
```go
fmt.Println("a", "b")
fmt.Print("a", "b")
fmt.Print("a", 1)
```
**A**:
```
a b
ab
a 1
```
`Print` adds a space only between non-string args.

**Q2**: What is printed?
```go
err := fmt.Errorf("count=%d", 3)
s := fmt.Sprintf("count=%w", err)
fmt.Println(s)
```
**A**: `count=%!w(*errors.errorString=&{count=3})`. `%w` outside
`Errorf` is treated as a malformed verb.

**Q3**: What is printed?
```go
type T struct{ A, B int }
t := T{1, 2}
fmt.Printf("%v %+v %#v\n", t, t, t)
```
**A**: `{1 2} {A:1 B:2} main.T{A:1, B:2}`

---

## 24. Cheat Sheet

```go
// Print to stdout
fmt.Print("hello")           // no newline, no spaces
fmt.Println("hello", "you")  // newline + spaces
fmt.Printf("x=%d\n", 42)     // formatted, manual newline

// Build a string
s := fmt.Sprintf("user-%d", id)

// Wrap an error
err := fmt.Errorf("read %s: %w", path, ioErr)

// Write to any io.Writer
fmt.Fprintln(os.Stderr, "warn")
fmt.Fprintf(w, "line %d\n", n)

// Verbs
%v   default
%s   string
%d   integer
%t   bool
%f   float
%T   type
%+v  struct with field names
%#v  Go-syntax representation
%w   wrap error (Errorf only)

// Width / precision
%5d   width 5, right-aligned
%-5d  width 5, left-aligned
%05d  width 5, zero-padded
%.2f  precision 2 (digits after .)
%5.2f width 5, precision 2
```

---

## 25. Self-Assessment Checklist

- [ ] I can choose between `Print`, `Println`, and `Printf`.
- [ ] I know the five day-one verbs (`%v %s %d %t %f`).
- [ ] I know that `Sprintf` returns a string.
- [ ] I know to put `\n` in `Printf` format strings.
- [ ] I use `%w` for error wrapping, only inside `Errorf`.
- [ ] I write user output to `Stdout`, diagnostics to `Stderr`.
- [ ] I never use user input as a format string.
- [ ] I run `go vet` and read its `printf` warnings.

---

## 26. Summary

The `fmt` package gives you three families — `Print`, `Sprint`,
`Fprint` — each with three variants: zero suffix, `ln`, and `f`. Use
`Printf`/`Sprintf`/`Fprintf` when you have a format string with
verbs; use the others for default formatting. Wrap errors with
`fmt.Errorf("...: %w", err)`. Use `%v` until a specific verb is
needed. Mind the spacing differences between `Print` and `Println`,
the missing `\n` in `Printf`, and the `%w`-only-in-`Errorf` rule. Run
`go vet` and trust its `printf` warnings.

---

## 27. What You Can Build

- CLIs with aligned tabular output.
- Error wrappers that preserve cause chains.
- Cache keys, URL paths, and SQL fragments.
- Toy HTTP handlers using `Fprintf(w, ...)`.
- Test failure messages that pinpoint mismatches.
- Quick debug dumps of structs with `%+v`.

---

## 28. Further Reading

- [pkg.go.dev/fmt](https://pkg.go.dev/fmt) — full docs and verb
  reference.
- [Effective Go — Printing](https://go.dev/doc/effective_go#printing)
- [Go Blog — Errors are values](https://go.dev/blog/errors-are-values)
- [Go 1.13 release notes — `%w`](https://go.dev/doc/go1.13#error_wrapping)

---

## 29. Related Topics

- 8.1 `io` and file handling — every `Fprintf` writes to a writer.
- 8.7 `log/slog` — structured logging for services.
- 5.4 `fmt.Errorf` — focused deep dive on `%w`.
- 8.6 `bufio` — buffered I/O over the same writers.

---

## 30. Diagrams & Visual Aids

### The Print families

```
                 ┌───────────┐
                 │ arguments │
                 └─────┬─────┘
                       ▼
   ┌────────────┬─────────────┬────────────┐
   │  Print*    │   Sprint*   │  Fprint*   │
   │  → stdout  │ → returns s │ → writer   │
   └─────┬──────┴──────┬──────┴─────┬──────┘
         ▼             ▼            ▼
       screen       string      file/net
```

### Verb dispatch

```mermaid
flowchart TD
    A[format string] --> B{token?}
    B -- literal --> C[copy to output]
    B -- %v --> D[default by reflect.Kind]
    B -- %d/%s/%f --> E[type-specific encoder]
    B -- %w --> F[Errorf only — wrap]
    D --> G[String\(\) if implemented]
    E --> G
    G --> H[bytes appended]
```
