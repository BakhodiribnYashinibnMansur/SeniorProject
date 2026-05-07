# The error Interface — Professional / Internals Level

## 1. Overview

This document studies real-world Go error types in the standard library, the conventions large Go codebases use to keep errors a tractable part of the public API, and the lint and review machinery that keeps these conventions honored at scale.

We trace four representative error types from their definitions in the Go source tree, then derive a set of team conventions, a review checklist, and a lint configuration drawn from production setups.

Source files cited (read along):

- `src/os/error.go` and `src/io/fs/fs.go` for `*PathError`.
- `src/net/net.go` for `*OpError`.
- `src/net/url/url.go` for `*url.Error`.
- `src/encoding/json/decode.go` for `*UnmarshalTypeError`.
- `src/errors/errors.go` and `src/errors/wrap.go` for the standard helpers.

---

## 2. `os.PathError` — The Triplet Pattern

Definition (in `src/io/fs/fs.go` since Go 1.16; `os.PathError` is now an alias to `fs.PathError`):

```go
type PathError struct {
    Op   string
    Path string
    Err  error
}

func (e *PathError) Error() string {
    return e.Op + " " + e.Path + ": " + e.Err.Error()
}

func (e *PathError) Unwrap() error { return e.Err }

func (e *PathError) Timeout() bool {
    t, ok := e.Err.(interface{ Timeout() bool })
    return ok && t.Timeout()
}
```

Lessons:

1. **Three fields and only three.** `Op`, `Path`, `Err`. Every filesystem error in the standard library uses exactly this shape.
2. **`Unwrap()` exposes the cause.** `errors.Is(err, fs.ErrNotExist)` traverses through this wrapper and matches against the inner `syscall.ENOENT`-derived sentinel.
3. **Behavioral delegation via type assertion.** `Timeout()` checks the inner error for the same method, exposing the behavior at the wrapper level. This is how `*net.OpError` propagates timeout-ness.

Real call site (in `src/os/file_unix.go`):

```go
func Open(name string) (*File, error) {
    return OpenFile(name, O_RDONLY, 0)
}

// OpenFile uses fixCount and wraps:
return nil, &PathError{Op: "open", Path: name, Err: e}
```

Caller code:

```go
f, err := os.Open("missing")
if errors.Is(err, fs.ErrNotExist) {
    // works because of Unwrap and the inner sentinel
}
var pe *fs.PathError
if errors.As(err, &pe) {
    fmt.Println(pe.Path) // "missing"
}
```

The contract: every `os.Open` failure returns a `*PathError`. Tests can rely on it. Tools can probe it.

---

## 3. `net.OpError` — Network Errors With Structured Context

From `src/net/net.go`:

```go
type OpError struct {
    Op     string  // "dial", "read", "write", "accept", ...
    Net    string  // "tcp", "udp", "unix", ...
    Source Addr    // local address (may be nil)
    Addr   Addr    // remote address (may be nil)
    Err    error   // underlying cause
}

func (e *OpError) Error() string {
    // builds: "<Op> <Net> <Source>-><Addr>: <Err>"
    // exact code in net.go
}

func (e *OpError) Unwrap() error { return e.Err }

func (e *OpError) Timeout() bool {
    if ne, ok := e.Err.(*os.SyscallError); ok {
        // ...
    }
    t, ok := e.Err.(interface{ Timeout() bool })
    return ok && t.Timeout()
}

func (e *OpError) Temporary() bool { ... }
```

Notes:

1. **Five fields.** Two more than `PathError` because network errors need source/dest addresses for diagnostics.
2. **Behavioral methods (`Timeout`, `Temporary`) front the cause.** Callers can check `if oe.Timeout()` without unwrapping.
3. **Layered wrapping.** A typical `Dial` failure produces `*net.OpError -> *os.SyscallError -> syscall.Errno`. Three layers, each with its own role: `OpError` for net context, `SyscallError` for syscall name, `Errno` for the OS code. `errors.Is(err, syscall.ECONNREFUSED)` works through all three.

Caller pattern:

```go
conn, err := net.Dial("tcp", addr)
if err != nil {
    var oe *net.OpError
    if errors.As(err, &oe) && oe.Timeout() {
        // retry with backoff
    }
}
```

---

## 4. `url.Error` — Wrapping HTTP/URL Failures

From `src/net/url/url.go`:

```go
type Error struct {
    Op  string
    URL string
    Err error
}

func (e *Error) Error() string {
    return e.Op + " " + e.URL + ": " + e.Err.Error()
}

func (e *Error) Unwrap() error { return e.Err }

func (e *Error) Timeout() bool {
    t, ok := e.Err.(interface{ Timeout() bool })
    return ok && t.Timeout()
}
```

`*url.Error` is what `http.Client.Do` returns on transport-level failures. Three fields again — `Op`, `URL`, `Err`. Note how three different parts of the standard library converged on essentially the same shape: an operation label, the resource identifier, and the cause.

Take-away for your own packages: when in doubt, copy this shape.

---

## 5. `json.UnmarshalTypeError` — A Domain-Specific Shape

From `src/encoding/json/decode.go`:

```go
type UnmarshalTypeError struct {
    Value  string       // description of JSON value
    Type   reflect.Type // type of Go value
    Offset int64        // byte offset where the error happened
    Struct string       // name of the struct type (if any)
    Field  string       // path to the field (if any)
}

func (e *UnmarshalTypeError) Error() string {
    if e.Struct != "" || e.Field != "" {
        return "json: cannot unmarshal " + e.Value + " into Go struct field " + e.Struct + "." + e.Field + " of type " + e.Type.String()
    }
    return "json: cannot unmarshal " + e.Value + " into Go value of type " + e.Type.String()
}
```

Lessons:

1. **No `Op`/`Path` triplet.** This error doesn't fit the triplet because it has different domain fields.
2. **Five domain-specific fields.** Each is exposed for callers who want structured diagnostics.
3. **No `Unwrap`.** There is no inner cause — the error is a leaf.
4. **Pointer receiver, returned as `*UnmarshalTypeError`.** Always use `&...{}` to construct.

Take-away: if your error is a leaf with rich domain data, design fields freely. The triplet is a starting point, not a law.

---

## 6. Other Notable Standard-Library Error Types

Quick survey:

| Type | File | Shape |
|------|------|-------|
| `*os.SyscallError` | `src/os/error.go` | `{Syscall string; Err error}`; wraps `syscall.Errno` |
| `*os.LinkError` | `src/os/error.go` | `{Op, Old, New string; Err error}` for hardlink/symlink ops |
| `*exec.ExitError` | `src/os/exec/exec.go` | embeds `*os.ProcessState`, includes `Stderr []byte` |
| `*tls.RecordHeaderError` | `src/crypto/tls/conn.go` | `{Msg string; RecordHeader [5]byte; Conn net.Conn}` |
| `*strconv.NumError` | `src/strconv/atoi.go` | `{Func, Num string; Err error}`; canonical "parse uint64" pattern |
| `*regexp.Error` | `src/regexp/syntax/parse.go` | `{Code ErrorCode; Expr string}`; uses an enum |
| `*time.ParseError` | `src/time/format.go` | five fields including `Layout`, `Value`, `LayoutElem` |
| `*flag.ParseError` | (package internal) | enum-of-cases used via `errors.Is` |

Pattern recap:

- Operation/resource/cause is the most common shape.
- Leaf errors (no `Unwrap`) carry rich domain data.
- Wrapper errors expose `Unwrap`, an `Op` label, and behavioral helpers (`Timeout`, `Temporary`).

---

## 7. Team Conventions

Distilled from large Go codebases (Kubernetes, etcd, Docker, Caddy, Hashicorp tools):

### 7.1 Naming

- Sentinel: `var ErrSomething = errors.New("...")` at package scope. Comment each one.
- Custom error type: ends in `Error`. `PathError`, `LookupError`, `ValidationError`.
- Constructor: `NewXxxError(...)` if the fields need validation; otherwise use the literal `&XxxError{...}`.
- Error message: lowercase, no trailing punctuation, no terminal newline.

### 7.2 Construction Style

- Always return pointer types for non-trivial errors. `return &MyError{...}`.
- Never return a typed nil. Either return untyped `nil` or use a plain `error` variable.

```go
// good
return nil

// good
return &MyError{...}

// catastrophic — typed nil
var e *MyError
return e
```

### 7.3 Wrapping Policy

- Wrap with `%w` when crossing a layer (db -> service -> handler).
- Don't wrap when the inner error is already informative AND the layer adds no context.
- Don't double-log: log at the OUTER boundary (handler), not at every wrap site.

### 7.4 Comparison Policy

- Sentinel comparisons: `errors.Is(err, pkg.ErrSomething)`.
- Type extraction: `errors.As(err, &targetType)`.
- `==` only on locally-scoped, never-wrapped errors.

### 7.5 When To `panic`

- Never on user input.
- Never on a network error.
- Yes on `init()` configuration that the program cannot run without.
- Yes inside parsers when a programmer has misused the API in a way that should be caught at test time.

### 7.6 Error Logging

- Log with `%+v` if you have a stack-trace-bearing error library; otherwise `%v`.
- Log key=value: `slog.Error("op failed", "err", err, "user", userID, "path", path)`.
- Don't log the same error twice; the receiver of the error is responsible for it.

### 7.7 Error Aggregation

- Use `errors.Join(errs...)` for collecting multiple errors (Go 1.20+).
- Or define a custom `MultiError` type with a stable `Error()` rendering.

---

## 8. Review Checklist

When reviewing a Go pull request that introduces or modifies an error type, check:

- [ ] **Naming.** Does the type end in `Error`? Does each sentinel start with `Err`?
- [ ] **Pointer receiver.** Is `Error()` defined on a pointer receiver for non-trivial structs?
- [ ] **No typed nil returns.** Search the diff for `return e` where `e` is a typed pointer that might be nil.
- [ ] **`Unwrap()` defined when wrapping.** If the new error embeds a cause, expose it.
- [ ] **Lowercased message.** "not found", not "Not found." or "NOT_FOUND".
- [ ] **No string matching on `Error()`.** Reject any `strings.Contains(err.Error(), ...)` in the diff.
- [ ] **No `==` against errors that may be wrapped.** Replace with `errors.Is`.
- [ ] **Sentinel exposure.** New `var ErrXxx` deserves a doc comment and a public-API note.
- [ ] **No global mutable state.** The error fields should be set at construction; don't mutate after the fact.
- [ ] **No `panic` for expected failures.** A network glitch is not a panic.
- [ ] **`errcheck` clean.** No silently ignored errors.
- [ ] **`errorlint` clean.** No raw `==` comparisons against wrapped errors.
- [ ] **Test coverage.** Each new error path is tested (happy path AND failure path).

---

## 9. Lint Rules

The Go ecosystem has three popular linters that enforce error conventions. Use them in CI.

### 9.1 `errcheck`

Detects ignored error returns:

```go
f, _ := os.Open(path) // errcheck flags this
```

Configuration in `.errcheck.yaml`:

```yaml
errcheck:
  check-type-assertions: true
  check-blank: true
```

### 9.2 `errorlint`

Detects misuse of `==` and incorrect `%v`/`%w` choices:

```go
if err == io.EOF { ... }              // flagged: should be errors.Is
fmt.Errorf("%v", inner)                // flagged: should be %w if you want wrapping
```

Configuration in `.golangci.yaml`:

```yaml
linters-settings:
  errorlint:
    errorf: true
    asserts: true
    comparison: true
```

### 9.3 `wrapcheck`

Ensures errors crossing API boundaries are wrapped:

```go
func service() error {
    return repo.Find() // wrapcheck flags: should add context
}
```

```yaml
linters-settings:
  wrapcheck:
    ignoreSigs:
      - .Errorf(
      - errors.New(
      - errors.Wrap(
```

### 9.4 `staticcheck`

Includes the `SA9003` ("empty branch") and several error-related rules — install it and let it run on every commit.

### 9.5 `golangci-lint` Profile

Sample `.golangci.yaml` excerpt:

```yaml
linters:
  enable:
    - errcheck
    - errorlint
    - wrapcheck
    - staticcheck
    - gocritic
    - revive

linters-settings:
  errorlint:
    errorf: true
    asserts: true
    comparison: true
```

Run on every PR via `golangci-lint run`.

---

## 10. Production Patterns From Real Codebases

### 10.1 Kubernetes — `apimachinery/pkg/api/errors`

Kubernetes wraps every API error in a `*StatusError` carrying an embedded `metav1.Status` with `Code`, `Reason`, `Message`, `Details`. Callers branch on the `Reason` enum. Sentinels exist (`IsNotFound(err)`, `IsConflict(err)`) as helper functions.

Lesson: helper functions (`IsNotFound`) are clearer at call sites than naked `errors.Is(err, NotFound)`. Define them when callers branch frequently.

### 10.2 Hashicorp `multierror`

Predecessor of `errors.Join`. Holds a slice of errors and renders as a multi-line string. Implements a custom `Unwrap()` that returns the next error in the slice, supporting incremental traversal.

Lesson: aggregate errors deserve their own type. Most teams now use `errors.Join` (Go 1.20+) directly.

### 10.3 Upspin — Original Inspiration For The Triplet

Rob Pike's Upspin filesystem introduced the `errors.E(...)` constructor and the `Op/Kind/Path/User/Err` shape. See https://commandcenter.blogspot.com/2017/12/error-handling-in-upspin.html.

Lesson: a richer (but still flat) struct of fields scales well even for very large codebases. Five fields is plenty.

### 10.4 Google's `xerrors` (precursor of Go 1.13 errors)

Brought stack frames and `%w` to Go before they were standardized. Now superseded by `fmt.Errorf("...: %w", err)` and the `errors` package, but the codebase shows the design history clearly.

### 10.5 Caddy — Sentinel-Heavy Configuration Errors

Caddy declares dozens of `var ErrXxx` at package scope because configuration errors are matched repeatedly during validation. Each sentinel has a one-line comment.

Lesson: when the error space is small and finite, sentinels are perfectly fine even if there are many of them. Document each one.

---

## 11. Designing A New Error Type — Workflow

Use this checklist when introducing an error type into a package:

1. **Audit callers.** Will anyone branch on this error? If no — return a plain string error.
2. **Name it.** `XyzError` for the type, `ErrXyz` for the sentinel.
3. **Choose fields.** Op? Resource? Cause? Or domain-specific fields (offset, line, column)?
4. **Define `Error()`.** Lowercase, prefix with operation, end with cause. No trailing punctuation.
5. **Define `Unwrap()`** if you wrap.
6. **Define behavioral methods** (`Timeout`, `Temporary`, `Retry`) if callers need them.
7. **Document.** Add a comment to the type and to each public sentinel.
8. **Test.** Cover the happy path AND the error path. Use `errors.Is`/`errors.As` in tests so you exercise the same API callers will.
9. **Update changelog.** New exposed errors are an API change.
10. **Add to lint config** if any new patterns need enforcement.

---

## 12. Visual: Standard Library Error Hierarchy

```
                         error (interface)
                              |
                              |
         +--------------------+---------------------+
         |                    |                     |
   *errorString         *PathError              *OpError
   (errors.New)         {Op,Path,Err}           {Op,Net,...,Err}
                              |                     |
                              v                     v
                          (Unwrap)              (Unwrap)
                              |                     |
                       syscall.Errno     *os.SyscallError -> Errno
```

The hierarchy is a chain through `Unwrap()`, not a class tree. `errors.Is/As` walks down the chain.

---

## 13. Summary

| Take-away | Source |
|-----------|--------|
| The Op/Path/Err triplet is the canonical wrapper shape | `os.PathError`, `url.Error` |
| Network errors layer wrappers: OpError -> SyscallError -> Errno | `net.OpError` |
| Leaf errors carry rich domain fields | `json.UnmarshalTypeError` |
| Always pointer receivers for non-trivial errors | every standard-lib type |
| Behavioral methods (Timeout, Temporary) propagate via type assertions | `net.OpError`, `url.Error` |
| Document every exposed sentinel and type | standard-lib precedent |
| Lint rules: errcheck, errorlint, wrapcheck | community standard |
| Helper functions (IsNotFound) clarify call sites | Kubernetes |

When you author or review a Go package's error API, hold it next to the standard library. If your shape diverges, you should have a clear reason. Most of the time, copying `*PathError` or `*UnmarshalTypeError` verbatim is the right answer.

---

## 14. Further Reading

- https://go.dev/blog/error-handling-and-go
- https://go.dev/blog/go1.13-errors
- https://commandcenter.blogspot.com/2017/12/error-handling-in-upspin.html
- https://dave.cheney.net/2016/04/27/dont-just-check-errors-handle-them-gracefully
- https://pkg.go.dev/errors

These articles, paired with the standard library files cited above, cover essentially every error-design decision you will face in production Go code.
