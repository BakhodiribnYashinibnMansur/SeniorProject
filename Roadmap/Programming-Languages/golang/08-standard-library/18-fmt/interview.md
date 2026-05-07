# Go fmt — Interview Questions

## Table of Contents

1. [Junior Level Questions](#junior-level-questions)
2. [Middle Level Questions](#middle-level-questions)
3. [Senior Level Questions](#senior-level-questions)
4. [Scenario-Based Questions](#scenario-based-questions)
5. [FAQ](#faq)

---

## Junior Level Questions

**Q1: What's the difference between `Print`, `Println`, and `Printf`?**

**Answer**:
- `Print(args...)` writes the arguments to stdout with no trailing
  newline. It puts a space between two adjacent non-string
  arguments and nothing between strings.
- `Println(args...)` always puts a space between arguments and adds
  a trailing newline.
- `Printf(format, args...)` parses a format string with verbs (`%s`,
  `%d`, ...) and writes the result. No automatic newline.

```go
fmt.Print("a", "b")     // ab
fmt.Print(1, 2)         // 1 2
fmt.Println("a", "b")   // a b\n
fmt.Printf("%s-%d", "x", 9) // x-9
```

---

**Q2: What's the difference between `Print`, `Sprint`, and `Fprint`?**

**Answer**:
- `Print*` writes to stdout (`os.Stdout`).
- `Sprint*` returns the formatted string instead of writing it.
- `Fprint*` writes to any `io.Writer` (file, network, buffer,
  stderr).

`Print` is sugar for `Fprint(os.Stdout, ...)`. All three families
have `*ln` and `*f` variants.

```go
fmt.Print("hi")                  // → stdout
s := fmt.Sprint("hi")            // → returns "hi"
fmt.Fprint(os.Stderr, "warn")    // → stderr
```

---

**Q3: What's the difference between `%v`, `%+v`, and `%#v`?**

**Answer**:
- `%v` — default representation. For structs: `{field1 field2}`.
- `%+v` — like `%v` but adds field names: `{Name:field1 Age:32}`.
- `%#v` — Go-syntax representation, paste-able into source:
  `main.User{Name:"field1", Age:32}`.

```go
type U struct{ Name string; Age int }
u := U{"Ada", 36}
fmt.Printf("%v\n", u)   // {Ada 36}
fmt.Printf("%+v\n", u)  // {Name:Ada Age:36}
fmt.Printf("%#v\n", u)  // main.U{Name:"Ada", Age:36}
```

`%#v` calls `GoString()` if defined; the others call `String()`.

---

**Q4: How do you wrap an error with `fmt.Errorf`?**

**Answer**: Use the `%w` verb:

```go
if _, err := os.Open(path); err != nil {
    return fmt.Errorf("load %s: %w", path, err)
}
```

The wrapped error participates in the chain — `errors.Is(err,
os.ErrNotExist)` walks through it. `%w` works **only** inside
`fmt.Errorf`; in `Sprintf` it falls back to a malformed-verb
placeholder.

Since Go 1.20 you can use multiple `%w` per call:
```go
fmt.Errorf("step: %w; cleanup: %w", err1, err2)
```

---

**Q5: How does `fmt` know to call `String()` on a type? When does
it NOT?**

**Answer**: For verbs `%s`, `%v` (and a few others), `fmt` checks
in order:

1. Does the value implement `Formatter` (`Format(State, rune)`)?
   Use it.
2. Does the value implement `error`? Use `Error()`.
3. Does the value implement `Stringer` (`String() string`)? Use it.
4. Otherwise, fall back to reflection-based default formatting.

`String()` is **not** called when:
- The verb is `%T` (which always prints the type name).
- The verb is `%#v` and `GoString()` is defined.
- The type has a pointer receiver `String()` and you format the
  value (not the pointer).
- A `Formatter` method exists and overrides everything.
- The type also implements `error` — `Error()` wins for `%v`/`%s`.

---

**Q6: What does `%T` print?**

**Answer**: The Go type name of the argument, including package
path:

```go
fmt.Printf("%T\n", 42)         // int
fmt.Printf("%T\n", "x")        // string
fmt.Printf("%T\n", []int{})    // []int
fmt.Printf("%T\n", &User{})    // *main.User
fmt.Printf("%T\n", nil)        // <nil>
```

Useful for debugging interface values.

---

**Q7: How do you print a percent sign?**

**Answer**: Double it: `%%`.

```go
fmt.Printf("100%%\n") // 100%
```

A single `%` followed by a non-verb character produces a `%!(NOVERB)`
error placeholder.

---

**Q8: What's wrong with `fmt.Printf(userInput)`?**

**Answer**: It treats user input as a format string. Verbs in
`userInput` (e.g. `%s %d`) are interpreted, looking for arguments
that don't exist; you get `%!s(MISSING)` placeholders. It also
makes log lines unpredictable.

**Fix**:
```go
fmt.Print(userInput)        // literal
fmt.Printf("%s", userInput) // literal via %s
```

`staticcheck` SA1006 catches this.

---

## Middle Level Questions

**Q9: Why is `fmt.Sprintf` slower than direct `strconv.Itoa`?**

**Answer**: `Sprintf` does several things `strconv.Itoa` skips:

1. **Format string parsing**. Walk the bytes, identify each verb,
   apply width/precision rules.
2. **Argument boxing**. The variadic `...any` boxes each argument
   into an interface, possibly allocating.
3. **Reflection / type switch**. `fmt` checks the dynamic type,
   sometimes using `reflect.Value`.
4. **Method dispatch**. Check for `Formatter`, `error`, `Stringer`.

`strconv.Itoa(n)` is a typed function with a hand-rolled fast path
(table lookup for small ints, division-by-10 loop otherwise),
producing a string with one allocation.

Benchmarks (Go 1.22):
```
BenchmarkSprintfInt-8   30000000   45 ns/op   16 B/op   2 allocs/op
BenchmarkItoa-8        200000000    7 ns/op    0 B/op   0 allocs/op
```

About 6x faster, zero allocs.

---

**Q10: When should you use `slog` instead of `fmt.Println`?**

**Answer**: Always, for service logs. Specifically, switch when:

- The log is consumed by a machine (Splunk, Datadog, ELK).
- You want structured key-value pairs.
- You want leveled filtering (`Debug`, `Info`, `Warn`, `Error`).
- You want low/zero allocation per log line.
- You want context propagation (`slog.With(...)`).

`fmt.Println` stays for: CLI tools, tests, error wrapping (via
`fmt.Errorf`), and human-readable one-shot output.

---

**Q11: Show how to implement `fmt.Formatter` for custom verb
handling.**

**Answer**:

```go
type Color struct{ R, G, B uint8 }

func (c Color) Format(f fmt.State, verb rune) {
    switch verb {
    case 'h': // hex
        fmt.Fprintf(f, "#%02x%02x%02x", c.R, c.G, c.B)
    case 'r': // rgb()
        fmt.Fprintf(f, "rgb(%d,%d,%d)", c.R, c.G, c.B)
    case 'v':
        if f.Flag('+') {
            fmt.Fprintf(f, "Color{R:%d G:%d B:%d}", c.R, c.G, c.B)
            return
        }
        fmt.Fprintf(f, "(%d,%d,%d)", c.R, c.G, c.B)
    default:
        fmt.Fprintf(f, "%%!%c(Color=%v)", verb, c)
    }
}

fmt.Printf("%h\n", Color{255, 128, 0})  // #ff8000
fmt.Printf("%r\n", Color{255, 128, 0})  // rgb(255,128,0)
fmt.Printf("%+v\n", Color{255, 128, 0}) // Color{R:255 G:128 B:0}
```

Three rules:
1. Switch on `verb` and handle each.
2. Use `f.Flag('+')`, `f.Width()`, `f.Precision()` to honour
   modifiers.
3. Always have a default branch; otherwise unsupported verbs print
   nothing.

---

**Q12: What's the difference between `Stringer` and `GoStringer`?**

**Answer**:
- `Stringer.String() string` — natural, human-readable form.
  Called by `%s` and `%v`. Used in logs, UI output.
- `GoStringer.GoString() string` — Go-syntax form that compiles
  back to the value. Called by `%#v`. Used in debugging dumps.

```go
type ID [16]byte

func (id ID) String() string {
    return hex.EncodeToString(id[:])
}

func (id ID) GoString() string {
    return fmt.Sprintf("ID{%#x}", id[:])
}

fmt.Printf("%s\n", id)  // 0123456789abcdef...
fmt.Printf("%v\n", id)  // 0123456789abcdef...
fmt.Printf("%#v\n", id) // ID{0x0123...}
```

---

**Q13: What does `%[N]verb` do?**

**Answer**: It's an indexed argument reference. `%[1]d` means "use
argument 1 (1-indexed)". The next non-indexed verb continues from
`N+1`.

```go
fmt.Printf("%[1]d in hex is %[1]x\n", 255)
// 255 in hex is ff

fmt.Printf("%[2]s %[1]s!\n", "World", "Hello")
// Hello World!
```

Useful for printing the same value with multiple representations
and for translation files where word order varies.

---

**Q14: When does `Sprintf` outperform `strings.Builder`?**

**Answer**: It usually doesn't. `Builder` is faster for repeated
string assembly because it reuses a `[]byte` buffer.

`Sprintf` is competitive when:
- The format is complex (many verbs).
- The call is one-off (no reuse possible).
- Readability matters more than nanoseconds.

For loops, builders, or bulk assembly: `Builder` + `strconv` wins
by 3-5x.

---

**Q15: How does `%w` interact with `errors.Is` and `errors.As`?**

**Answer**: `%w` makes the resulting error implement `Unwrap()
error` (single `%w`) or `Unwrap() []error` (multiple, Go 1.20+).
`errors.Is/As` walk the chain by repeatedly calling `Unwrap`.

```go
var ErrNotFound = errors.New("not found")
err := fmt.Errorf("user %d: %w", 42, ErrNotFound)

errors.Is(err, ErrNotFound)  // true
```

For typed errors:
```go
var pe *fs.PathError
if errors.As(err, &pe) { ... }
```

Without `%w`, the wrapped error is just text; `errors.Is/As` cannot
see it.

---

**Q16: Why does `fmt.Errorf("a: %w", nil)` panic in Go 1.20+?**

**Answer**: Because `nil` cannot be unwrapped. Earlier versions
silently produced a non-wrapping error, which confused `errors.Is`
checks. Go 1.20 added a runtime panic to make the bug obvious.

Filter `nil` before calling, or use `errors.Join` which silently
ignores nils:

```go
err := errors.Join(primary, cleanupOrNil) // cleanupOrNil may be nil
```

---

## Senior Level Questions

**Q17: Describe the `pp` printer state and its role.**

**Answer**: `pp` is `fmt`'s internal printer struct (defined in
`src/fmt/print.go`). It holds:

- The output buffer (`buf []byte`).
- The current argument and its `reflect.Value`.
- A formatting state (`fmt fmtState`) with width/precision/flags.
- A list of wrapped errors (`wrappedErrs`) for `Errorf`.

Each `Sprintf`/`Printf` call grabs a `pp` from a `sync.Pool`, fills
the buffer, and returns it to the pool (unless the buffer grew past
~64 KiB, in which case it's discarded to avoid pinning memory).

The pool eliminates ~80% of the per-call allocations. The remaining
allocations are:
- The `[]any` for variadics (sometimes).
- The result string (`Sprintf` only).
- Boxes for non-cached integers.

---

**Q18: How does `vet`'s printf analyzer work?**

**Answer**: The analyzer (`golang.org/x/tools/go/analysis/passes/
printf`) walks the AST, identifies calls to `Printf`-like
functions, parses the literal format string, and type-checks each
verb against the corresponding argument's type.

It reports:
- Wrong type: `Printf format %d has arg "x" of wrong type string`.
- Missing argument: `Printf format %s reads arg #2, but call has 1 arg`.
- Extra argument: `Printf call has arguments but no formatting directives`.
- `%w` outside `Errorf`.
- Unknown verbs.

It recognises stdlib `Printf`-likes by signature; for custom
helpers add a `// vet:printffunc` annotation or use staticcheck's
configurable list.

---

**Q19: Explain the dispatch order: Formatter, error, Stringer.**

**Answer**: When `fmt` formats a value (verb `%s` or `%v`), it
checks methods in this order:

1. `Formatter` interface (`Format(State, rune)`) — overrides
   everything.
2. `error` interface (`Error() string`) — for `%s` and `%v`,
   takes precedence over `Stringer`.
3. `Stringer` interface (`String() string`) — last.
4. Reflection-based default — fallback.

Consequence: a type that implements both `error` and `Stringer`
shows `Error()` for `%v`. To force `Stringer`, define `Format` and
explicitly call `String()`.

---

**Q20: How does `stringer` codegen work?**

**Answer**: `golang.org/x/tools/cmd/stringer` reads a Go file,
finds the constant block of a named integer type, and emits a
sibling file with a `String()` method.

For dense, contiguous values (like `iota`-generated enums) it
emits a packed name string and an offset table:

```go
const _Status_name = "PendingRunningDone"
var _Status_index = [...]uint8{0, 7, 14, 18}

func (i Status) String() string {
    if i < 0 || i >= Status(len(_Status_index)-1) {
        return "Status(" + strconv.FormatInt(int64(i), 10) + ")"
    }
    return _Status_name[_Status_index[i]:_Status_index[i+1]]
}
```

For sparse values it falls back to a `map[T]string` lookup.

Used across the standard library (`time.Weekday`, `time.Month`)
and in major OSS (Kubernetes, Prometheus, etcd).

---

**Q21: A type implements `Stringer`, `error`, and `Formatter`. What
gets called?**

**Answer**: `Format`. `Formatter` is the highest-precedence
interface and overrides both `error` and `Stringer`.

If the `Format` method itself wants to delegate (say, to `Error()`
for `%s` and a richer view for `%+v`), it must call them
explicitly.

---

**Q22: How do you avoid the `Stringer` recursion bug?**

**Answer**: Don't use `%v` of `self` inside `String()`. Two safe
patterns:

1. Explicit field references:
```go
func (t T) String() string {
    return fmt.Sprintf("T{X:%d, Y:%d}", t.X, t.Y)
}
```

2. Alias type that strips the method:
```go
func (t T) String() string {
    type alias T
    return fmt.Sprintf("%+v", alias(t))
}
```

`vet` does NOT catch this; add a unit test that calls `String()`
and asserts it returns a non-empty string.

---

**Q23: What allocations happen in `fmt.Println("hello", n)` where
n is an int?**

**Answer**:
1. The variadic `...any` packs into an `[]any`. This is a `[2]any`
   allocation (~32 B).
2. `n` is boxed into an `any`. For small ints (e.g. -256 to 255 on
   most builds), Go interns them and no allocation happens. For
   larger ints, one allocation.
3. The `[]any` is passed to `Fprintln`.
4. `Fprintln` writes to `os.Stdout`. No string allocation; bytes
   go directly.

So roughly 1-2 allocations per call. For hot loops, this is the
dominant cost.

---

## Scenario-Based Questions

**Q24: A code review shows `return fmt.Errorf("read %s: %v", path,
err)`. What's wrong?**

**Answer**: The `%v` should be `%w`. With `%v` the wrapped error
becomes plain text; the chain is broken; `errors.Is` returns false
even when the cause matches.

Fix: `return fmt.Errorf("read %s: %w", path, err)`.

`errorlint` flags this; treat its warnings as build errors.

---

**Q25: A service prints `fmt.Println` for every request. Profile
shows 30% CPU in `fmt.(*pp).doPrint`. What do you do?**

**Answer**: Switch to structured logging (`slog` with JSONHandler).
`slog.Info` is allocation-free in Go 1.22+ and produces parsable
output.

If `slog` is not available (older Go), buffer stdout with
`bufio.Writer` and use `Fprintln` to it; remember to `Flush` on
shutdown.

---

**Q26: A custom error type prints as `&{op:load err:...}` instead
of `load: file not found`. Why?**

**Answer**: The type is being printed via the default struct format,
not via `Error()`. Either:
1. The variable is `&customError`, but the `Error()` method has a
   value receiver that doesn't get called on the pointer (rare —
   pointers DO inherit value methods). More likely:
2. The type doesn't implement `error` — perhaps the method is
   `Err()` instead of `Error()`.

Check: `fmt.Printf("%T\n", err)` to see the type, then verify the
method is `func (e MyError) Error() string`.

---

**Q27: A team has a custom logger `func Logf(format string, args
...any)`. `vet` doesn't catch verb mismatches. How do you fix?**

**Answer**: Add a `printfunc` annotation (in newer `vet`):

```go
// Logf logs a message.
//
//go:printf
func (l *Logger) Logf(format string, args ...any) { ... }
```

Or in older `vet`:
```bash
go vet -printfuncs=Logf ./...
```

Or with `staticcheck.conf`:
```
checks = ["all"]
[printf]
funcs = ["(*Logger).Logf"]
```

After this, `vet` treats `Logf` like `Printf` and checks verbs.

---

## FAQ

**Q: Should I prefer `Sprint` or `Sprintf` for one-off
concatenation?**

A: `Sprint` for "print these values with default format separated
by spaces". `Sprintf` when you have a template. They have similar
cost; choose for clarity.

**Q: Is `fmt.Println(err)` enough, or should I use `Error()`
explicitly?**

A: `Println(err)` calls `Error()` automatically (because `error`
beats `Stringer` in dispatch). Either is fine; `Println(err)` is
shorter.

**Q: How do I print floats without scientific notation?**

A: Use `%f`. `%g` switches between `%e` and `%f` based on magnitude;
`%f` always uses decimal.

```go
fmt.Printf("%f\n", 1234567.89) // 1234567.890000
fmt.Printf("%g\n", 1234567.89) // 1.23456789e+06
```

**Q: How do I align hex output?**

A: Width plus `0` flag:
```go
fmt.Printf("%08x\n", 255)  // 000000ff
fmt.Printf("%#08x\n", 255) // 0x0000ff
```

**Q: Can I print binary?**

A: `%b`:
```go
fmt.Printf("%b\n", 10) // 1010
fmt.Printf("%08b\n", 10) // 00001010
```

**Q: How do I dump a `[]byte` as a string?**

A: `%s`:
```go
b := []byte("hello")
fmt.Printf("%s\n", b) // hello
fmt.Printf("%v\n", b) // [104 101 108 108 111]
```

**Q: How do I escape user-provided text?**

A: `%q` Go-quotes it:
```go
fmt.Printf("%q\n", "hello\nworld") // "hello\nworld"
```

**Q: What's the difference between `%x` on bytes and on strings?**

A: None — both produce hex. `%x` on numbers produces lowercase hex
of the integer.

**Q: Does `fmt.Print` flush?**

A: It writes to `os.Stdout`, which is unbuffered for terminals
(line-buffered in some shells, block-buffered when piped). For
deterministic flushing, wrap with `bufio.Writer` and call `Flush`.

**Q: Can `fmt.Sprintf` ever fail?**

A: It can produce `%!verb(...)` placeholders, but it doesn't
return an error. Use `vet` to catch problems at compile time.
