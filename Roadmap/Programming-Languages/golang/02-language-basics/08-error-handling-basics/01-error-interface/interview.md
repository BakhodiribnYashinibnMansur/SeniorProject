# The error Interface — Interview Questions

## Table of Contents

1. [Junior Level Questions](#junior-level-questions)
2. [Middle Level Questions](#middle-level-questions)
3. [Senior Level Questions](#senior-level-questions)
4. [Trap Questions](#trap-questions)
5. [FAQ](#faq)

---

## Junior Level Questions

### Q1: What is the `error` interface in Go?

**Answer**: It is a built-in interface with a single method:

```go
type error interface {
    Error() string
}
```

Any type that has a method `Error() string` automatically satisfies `error`. There is no `implements` keyword, no class hierarchy, and no exception machinery — errors are just values returned alongside results.

---

### Q2: What does `errors.New` actually do?

**Answer**: It returns a `*errorString`, which is an unexported struct with a single string field. The implementation is two lines:

```go
// src/errors/errors.go
type errorString struct{ s string }
func (e *errorString) Error() string { return e.s }
func New(text string) error { return &errorString{text} }
```

Two important consequences:
1. Each call returns a new heap pointer, so two `errors.New("x")` values are never `==` even with the same string.
2. The result is suitable as a sentinel when assigned to a package-level variable.

---

### Q3: What's the difference between `errors.New` and `fmt.Errorf`?

**Answer**: 

- `errors.New(text)` returns a flat error whose message is the literal `text`. No formatting verbs are interpreted.
- `fmt.Errorf(format, args...)` is `errors.New(fmt.Sprintf(format, args...))` PLUS the special `%w` verb that wraps an inner error.

Without `%w`, `fmt.Errorf` is a heavier `errors.New` (it allocates a varargs slice and runs the format machinery). With `%w`, it produces an error that satisfies `Unwrap()`, enabling `errors.Is` and `errors.As`.

Use `errors.New` for constant-string errors, `fmt.Errorf` for formatted ones, and `%w` whenever you wrap a cause.

---

### Q4: What's the smallest possible custom error type?

**Answer**: Three lines:

```go
type myErr struct{}
func (myErr) Error() string { return "boom" }
```

`myErr{}` satisfies `error`. No fields, no constructor, no allocation beyond the iface boxing.

In practice you write this whenever you want a one-off error type that does not need to carry data.

---

### Q5: How do you check whether a function returned an error?

**Answer**:

```go
result, err := fn()
if err != nil {
    // handle err
    return err
}
// use result
```

Convention:
- The error is the LAST return value.
- The check is the FIRST thing the caller does.
- `if err != nil` is the canonical idiom; some linters reject anything else.

---

### Q6: Can `Error()` be defined with a value receiver?

**Answer**: Yes, but it has consequences. With a value receiver:

```go
func (e MyErr) Error() string { return e.msg }
```

both `MyErr` and `*MyErr` satisfy `error`. With a pointer receiver:

```go
func (e *MyErr) Error() string { return e.msg }
```

only `*MyErr` satisfies `error`; passing `MyErr{}` is a compile error.

Use **pointer receiver** for non-trivial structs (multi-field, holding causes). Use **value receiver** for tiny immutable error tags (e.g., numeric error codes).

---

### Q7: What does `var err error` initialize to?

**Answer**: `nil`. The zero value of any interface type is nil. Specifically, the iface struct is `{tab: nil, data: nil}`, which is the only state where `err == nil` is true.

---

### Q8: What's wrong with this code?

```go
result, _ := someFunction()
fmt.Println(result)
```

**Answer**: It silently discards any error. If `someFunction` returns a non-nil error, `result` is typically zero/invalid and printing it produces nonsense. In production code, every error should be either returned, handled, or explicitly logged with a comment justifying the silence.

The `errcheck` linter flags this pattern.

---

## Middle Level Questions

### Q9: Why does `var err *MyErr = nil; return err` produce a non-nil error in the caller?

**Answer**: Because the `error` interface value is `(type, data) = (*MyErr, nil)`, not `(nil, nil)`. An interface is nil only when BOTH parts are nil.

#### Diagram: `iface = (type, data)`

```
Returning a typed nil pointer:
+----------------+-----------+
| type = *MyErr  | data = nil|
+----------------+-----------+
This is NOT nil. err == nil is false.

True nil interface:
+----------------+-----------+
| type = nil     | data = nil|
+----------------+-----------+
This is nil. err == nil is true.
```

The rule: never return a typed nil pointer through an `error` interface. Either return untyped `nil` or use a plain `error` variable as the return slot.

```go
// BAD
func bad() error {
    var e *MyErr
    return e // nil pointer wrapped in non-nil interface
}

// GOOD
func good() error {
    return nil // untyped nil
}

// ALSO GOOD
func good2() error {
    var err error // interface, default nil
    return err
}
```

This is the most asked Go interview question. It catches every newcomer once.

---

### Q10: What's the difference between sentinel errors and custom error types?

**Answer**:

| Aspect | Sentinel | Custom Type |
|--------|----------|-------------|
| Form | `var ErrX = errors.New("x")` | `type XErr struct{...}; func (x *XErr) Error() string {...}` |
| Identity | Single shared value | One per construction |
| Data | None | Arbitrary fields |
| Comparison | `==` (or `errors.Is` if wrapped) | `errors.As` for type extraction |
| API surface | Heavy: a public variable | Heavy: a public type |

Use **sentinels** when callers branch on identity but no extra data is needed (e.g., `io.EOF`).

Use **custom types** when callers need structured data (e.g., `*os.PathError` exposing `Path` and `Op`).

You can combine: a sentinel for identity, a custom type for data, where the type wraps the sentinel.

---

### Q11: Does Go provide a stack trace by default? If not, how do you get one?

**Answer**: No. The standard `error` interface has no stack trace. `errors.New("x")` gives you a string and that's it.

Options:

1. **Add a stack at the wrap site manually:**
   ```go
   import "runtime/debug"
   return fmt.Errorf("op: %w\n%s", err, debug.Stack())
   ```
   Heavy and inelegant.

2. **Use a third-party error library:**
   - `github.com/pkg/errors` (now archived but still widely used). `errors.Wrap(err, "msg")` captures a stack.
   - `github.com/cockroachdb/errors`. Production-grade, Postgres-style errors with stack and structured fields.
   - `golang.org/x/xerrors` (deprecated since Go 1.13 stdlib has the basic features).

3. **Use Go 1.13+ stdlib without stacks:**
   `errors.Is`, `errors.As`, `errors.Unwrap`, and `errors.Join` are great for matching but they do NOT add stacks. If you need stacks, you still need a third-party library.

Why no stacks in the stdlib? Two reasons:
- Stack capture is expensive (microseconds per error).
- Go's design philosophy: errors are values, not exceptions. A handled error often does not deserve a stack — you've already decided what to do with it.

Most production teams adopt one stack-capturing error library (`cockroachdb/errors` or similar) and use it consistently.

---

### Q12: When would you use a value receiver vs pointer receiver for `Error()`?

**Answer**:

**Value receiver** (`func (e MyErr) Error() string`):
- The error has no fields, or only a few small immutable ones.
- You want both `MyErr{}` and `&MyErr{}` to satisfy `error`.
- Multiple instances with the same field values should compare equal under `==`.
- Examples: a numeric error code wrapper, an enum-like error tag.

```go
type Code int
func (c Code) Error() string { return fmt.Sprintf("code %d", int(c)) }
const (
    NotFound = Code(404)
    Internal = Code(500)
)
```

**Pointer receiver** (`func (e *MyErr) Error() string`):
- The struct has multiple fields, especially with a wrapped `Err`.
- You want a single canonical address per error (reduces accidental duplication).
- Memoizing the formatted message inside the struct.
- Standard library convention: `*PathError`, `*OpError`, `*UnmarshalTypeError` all use pointer receivers.

```go
type PathError struct{ Op, Path string; Err error }
func (e *PathError) Error() string { return e.Op + " " + e.Path + ": " + e.Err.Error() }
```

Default: pointer receiver for any struct with two or more fields.

---

### Q13: What does this print, and why?

```go
e1 := errors.New("oops")
e2 := errors.New("oops")
fmt.Println(e1 == e2)
```

**Answer**: `false`. Each `errors.New` allocates a new `*errorString`. The two iface values have the same TYPE (`*errorString`) but different DATA pointers. `==` on iface compares both, and the data pointers differ — so `false`.

Same lesson: text-content equality is NOT identity. Use a sentinel.

---

### Q14: How would you implement `errors.Unwrap` for your custom error type?

**Answer**: Add an `Unwrap() error` method that returns the inner error:

```go
type MyErr struct {
    Op  string
    Err error
}

func (e *MyErr) Error() string  { return e.Op + ": " + e.Err.Error() }
func (e *MyErr) Unwrap() error  { return e.Err }
```

`errors.Unwrap` calls this method via type assertion. Without it, the wrap chain stops at `MyErr` and `errors.Is`/`errors.As` cannot traverse past.

For a multi-error type (Go 1.20+ `errors.Join`), implement `Unwrap() []error`:

```go
type Multi struct{ errs []error }
func (m *Multi) Unwrap() []error { return m.errs }
```

---

### Q15: How do exceptions in Java/Python differ from Go errors?

**Answer**:

| | Exceptions | Go errors |
|---|---|---|
| Channel | Separate (stack unwinding) | Normal return values |
| Caller awareness | Often hidden (RuntimeException) | Always explicit |
| Cost of throwing | High (capture stack, unwind) | Low (return a pointer) |
| Cost of catching | Low (table-based unwind) | Low (`if err != nil`) |
| Hierarchy | Class-based | Interface + Unwrap chain |
| Stack trace | Built-in | Not built-in (library-provided) |

Go's design forces every failure to be visible at the call site, at the cost of more code. Exceptions hide errors but tend to centralize handling. The Go style scales better in long-running services where you want every failure path visible to humans (and tools).

---

### Q16: Why is the convention to write error messages in lowercase without trailing punctuation?

**Answer**: Because errors compose. A wrapper like `fmt.Errorf("step1: %v", inner)` produces a chained message: `"step1: step2: not found"`. If `inner` were `"Not found."`, the chain would read `"step1: step2: Not found."` — visually awkward.

Lowercase + no period = composable. Capital + period = breaks composition.

Standard library precedent: `io.EOF` is `"EOF"` (acronyms are fine), `os.ErrNotExist` is `"file does not exist"`, `sql.ErrNoRows` is `"sql: no rows in result set"`. None capitalize random words; none end with periods.

---

## Senior Level Questions

### Q17: How is `error` represented at runtime?

**Answer**: As an `iface`, the two-word interface representation:

```go
type iface struct {
    tab  *itab          // type-and-method-table for the dynamic type
    data unsafe.Pointer // pointer to the value (or the value itself if word-sized)
}
```

The `itab` carries:
- A pointer to the interface type descriptor (`error`'s descriptor).
- A pointer to the dynamic type descriptor (e.g., `*errorString`).
- The method function pointers — for `error`, just `Error()`.

`itab` instances are cached. Calling `err.Error()` is one indirect call through `itab.fun[0]`.

This explains `err == nil`: it checks both `tab == nil` AND `data == nil`. Returning a typed nil pointer fills `tab` while `data` stays nil — the interface is non-nil, hence the famous bug.

---

### Q18: How many allocations does `errors.New("x")` cost?

**Answer**: Typically one heap allocation per call: the `errorString` struct (16 bytes on 64-bit: a string header). The interface conversion that boxes `*errorString` into `error` is free because the pointer fits in the iface `data` slot.

Benchmark:

```
BenchmarkErrorsNew-12   500000000   2.3 ns/op   16 B/op   1 allocs/op
```

Implication: thousands of `errors.New` calls per request show up as garbage. The standard fix: package-level sentinels declared once.

`fmt.Errorf` is heavier (~3 allocs: varargs slice + buffer + the resulting struct). Avoid in hot paths.

---

### Q19: What happens when you call `errors.Is` on a wrapped error chain?

**Answer**: `errors.Is(err, target)` walks the chain via `Unwrap()` and compares each link. Logic:

1. If `err == target` (interface equality), return true.
2. If `err` has an `Is(target) bool` method, call it. If true, return true.
3. If `err` has `Unwrap() error`, walk to the next; goto 1.
4. If `err` has `Unwrap() []error` (Go 1.20+), recurse into each.
5. Reached the leaf. Return false.

Cost: `O(depth)` of the chain. For deep chains (5+) and many checks per error, consider categorizing once and switching on the category — see `optimize.md`.

The optional `Is(error) bool` method lets a custom type opt into structural matching: e.g., a `*FileError` with `Path = "/tmp/x"` could declare itself `==` to any other `*FileError` with the same path.

---

### Q20: Explain method-set semantics for `error` satisfaction.

**Answer**: Go's method set rules:

- The method set of type `T` contains methods declared with receiver type `T`.
- The method set of `*T` contains methods declared with receiver type `T` AND `*T`.

Consequence:

```go
type V struct{}
func (V) Error() string { return "v" } // value receiver
// Both V and *V satisfy error.

type P struct{}
func (*P) Error() string { return "p" } // pointer receiver
// Only *P satisfies error.
```

The compiler enforces this at compile time. If you accidentally write `return P{}` where an `error` is expected and `Error()` is on `*P`, you get a clear compile error.

The rule for receiver choice on errors: pointer for non-trivial structs (multi-field), value for tiny immutable types.

---

### Q21: Why are `*errorString` values comparable via `==`?

**Answer**: Because the iface stores a pointer to the heap-allocated `errorString`. Two iface values for the same `errors.New` sentinel hold the same pointer in `data`. Comparing iface values compares both the type tag and the data pointer — both equal, so `==` returns true.

For two SEPARATE `errors.New("x")` calls, the data pointers differ, so `==` returns false.

This is why a single `var ErrX = errors.New("x")` is the canonical sentinel pattern: every reference to `ErrX` shares the same iface value.

---

### Q22: How does Go 1.20's `errors.Join` work?

**Answer**: It returns a `*joinError` that holds a slice of wrapped errors:

```go
func Join(errs ...error) error {
    n := 0
    for _, err := range errs {
        if err != nil {
            n++
        }
    }
    if n == 0 {
        return nil
    }
    e := &joinError{errs: make([]error, 0, n)}
    for _, err := range errs {
        if err != nil {
            e.errs = append(e.errs, err)
        }
    }
    return e
}
```

`joinError` implements `Error() string` (concatenating with newlines) and `Unwrap() []error` (returning the slice). `errors.Is` and `errors.As` both honor the multi-unwrap.

Useful for accumulating errors during validation, batch operations, or fan-out goroutine results. Replaces hand-rolled `multiError` types from before Go 1.20.

---

### Q23: When is `panic` the right response to an error?

**Answer**: Almost never in production code. Reserve `panic` for:

1. **Programmer errors**: an `init()` function configuration that violates an invariant (e.g., a malformed regex literal). The program cannot run, so panic.
2. **Impossible state**: unreachable code branches. If you reach them, your assumptions are wrong; `panic("unreachable")` is the right answer.
3. **Library boundaries** with documented panic-on-misuse: e.g., `regexp.MustCompile`, `template.Must`. Convention: `Must*` constructors panic; the non-Must variants return errors.

NEVER use `panic` for:
- Network failures.
- File-not-found.
- Bad user input.
- Database errors.
- Anything the caller might recover from.

A server that panics on a network glitch is a bug, no matter how convenient the panic was at the time of writing.

---

### Q24: Walk me through how `errors.As` works internally.

**Answer**: `errors.As(err, &target)` finds the first error in the chain whose type is assignable to `*target`. It returns a bool indicating whether a match was found.

Implementation outline:

1. Validate `target` is a non-nil pointer to a type that either implements `error` or is an interface.
2. Loop:
   - If `err == nil`, return false.
   - Try the type assertion: if `err`'s dynamic type is assignable to `*target`, set `*target = err` and return true.
   - If `err` has an `As(target any) bool` method, call it.
   - Else, call `err.Unwrap()` (single or slice) and continue.
3. Reached the end of the chain without a match. Return false.

The reflection cost is paid once per call, not per chain element. For deep chains it can be measurable but rarely shows up in profiles unless `errors.As` is in a tight loop.

---

### Q25: How would you design a public error API for a new package?

**Answer**:

1. **List the failures callers MUST distinguish.** These become sentinels or custom error types.
2. **Decide sentinel vs type per failure**:
   - Identity-only failure: `var ErrXxx = errors.New("...")`.
   - With data: a struct with `Error()` and `Unwrap()`.
3. **Use the Op/Path/Err triplet** if your package operates on resources (files, URLs, keys).
4. **Pick pointer receivers** for non-trivial structs.
5. **Wrap internal errors with `%w`** when crossing the API boundary.
6. **Document every sentinel and type** in package docs.
7. **Test with `errors.Is`/`errors.As`**, not `==`.
8. **Add lint rules** (`errcheck`, `errorlint`, `wrapcheck`) to CI.

The result: a small, stable, documented error API that callers can branch on without parsing strings.

---

## Trap Questions

### T1: Does `if err == nil` always mean "no error"?

**Trap**: A typed nil pointer wrapped in `error` is non-nil. So `err == nil` is FALSE even when the underlying pointer is nil.

```go
var p *MyErr
var err error = p
err == nil // false! type tag is non-nil
```

The fix is to NEVER assign a typed nil pointer to an `error` variable in the first place.

---

### T2: If two errors have the same `Error()` string, are they equal under `==`?

**Trap**: No. `errors.New("x") == errors.New("x")` is false because they are different pointers. Text content is not identity.

The exception: comparable struct errors with value receivers. `MyEnumErr(1) == MyEnumErr(1)` is true.

---

### T3: Can I return `nil` from a function with return type `*MyErr`?

**Trap**: Yes — but if the caller stores it in an `error` variable, the resulting interface is NOT nil (see T1 and Bug 4 in find-bug.md). To avoid this, make the return type `error`, not `*MyErr`, when the caller will adapt it.

```go
// dangerous if caller does: var err error = makeErr()
func makeErr() *MyErr { return nil }

// safer
func makeErr() error { return nil } // untyped nil through interface
```

---

### T4: Does `fmt.Errorf("...: %v", err)` preserve the cause for `errors.Is`?

**Trap**: No. `%v` only formats the inner error's string. Use `%w` to preserve the cause for `errors.Is`/`errors.As`.

```go
fmt.Errorf("ctx: %v", err) // string only, NO chain
fmt.Errorf("ctx: %w", err) // wraps, chain available
```

---

### T5: If my custom error has `Error() string` but no `Unwrap()`, can callers traverse to the wrapped cause?

**Trap**: No. Without `Unwrap()`, the chain stops at your error. `errors.Is(err, sentinel)` only checks the outermost error. Always implement `Unwrap()` if your error wraps another.

---

## FAQ

### F1: Why is the `error` interface predeclared, like `int` and `string`?

Because errors are universal in Go. Putting them in a package would force every program to import that package. Predeclaring them keeps the interface visible from anywhere, with no import. The interface is so simple — one method — that it deserves builtin status.

### F2: Why one method instead of two (e.g., a `Code()` method)?

Because every additional method becomes a forced contract on every error type ever written. With one method, the bar is low; with two, every existing type breaks. Go's deliberate minimalism here means the standard `error` interface is forever stable.

Specialized contracts (`Timeouter`, `Temporary`, `Coder`) are introduced as separate behavioral interfaces that callers can probe with type assertion or `errors.As`. This keeps the universal interface small while letting domains add their own.

### F3: How big is the `error` interface in memory?

16 bytes on 64-bit (two pointer-sized fields: `tab` and `data`). The underlying value can be any size, but the interface header itself is fixed at two words.

### F4: Are `error` values goroutine-safe?

`error` itself is just an interface. The underlying type may or may not be safe for concurrent use. `errors.New` produces an immutable `*errorString`, which is safe. `*PathError` is immutable after construction, so safe. A custom error with mutable state (like a counter in `Error()`) is NOT safe.

Default assumption: errors are immutable and safe for concurrent reads. Don't mutate after construction.

### F5: What about Go 2 / generics for errors?

There has been discussion of typed errors via generics (see proposals on go.dev). As of Go 1.22 and 1.23 there is no generic error type or change to the interface. The community settled on `errors.Is`/`As`/`Join` plus the existing iface model.

For now, write idiomatic Go 1.13+ code: `%w`, `errors.Is`, `errors.As`, sentinels, and Op/Path/Err structs.

---

## Summary

If you can answer Q9 (the nil-interface bug) clearly with a diagram, you have shown the interviewer you understand the iface representation. If you can answer Q11 (stack traces) without confidently asserting that "Go has built-in stacks", you've shown you have actually used Go in production. The rest of the questions are vocabulary checks: sentinels, custom types, `%w`, `errors.Is/As`, receiver choice.

Reread the find-bug document next; the bugs there are the ones interviewers most commonly ask you to spot in pasted code.
