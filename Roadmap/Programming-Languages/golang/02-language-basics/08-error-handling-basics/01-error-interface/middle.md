# The error Interface — Middle Level

## 1. Introduction

At the middle level you stop thinking of errors as "strings the function returns when it fails" and start thinking of them as **values** that carry domain meaning, that are **routed** through your code, and that have an explicit **public contract** with the caller. You decide which errors are part of your package's API, which are internal, which are recoverable, and which signal a programmer mistake. You design error TYPES the same way you design data types.

This document covers practical patterns: domain-specific error structs, the `Op/Err/Path` triplet, error categorization (transient vs permanent), exposing sentinel variables, the comparison with exceptions in other languages, and five worked examples drawn from real production code.

---

## 2. Prerequisites

- Junior-level material on `error`, `errors.New`, `fmt.Errorf`, and custom error types.
- Methods, embedding, interfaces.
- Familiarity with `os`, `io`, and `net` packages so the standard-library examples make sense.
- Awareness that `errors.Is` and `errors.As` exist (we will use them lightly here; the next topic covers them in depth).

---

## 3. Glossary

| Term | Definition |
|------|------------|
| Sentinel error | A package-level error variable used as a stable identity, e.g. `io.EOF`. |
| Domain error type | A struct that carries domain-specific fields (operation, resource, cause). |
| Op | The string label for the operation that failed. Field convention from `os.PathError`. |
| Categorization | Tagging an error as transient/permanent/timeout/fatal so callers can route it. |
| Error wrapping | Embedding a cause inside an outer error. Done idiomatically with `%w` (next topic). |
| `Unwrap()` | The optional method that exposes the wrapped cause. |
| Panic | A runtime mechanism for programmer-mistake errors. NOT for expected failures. |
| Public error API | The set of error types and sentinel variables your package promises to keep stable. |
| Error chain | The chain of `Unwrap()` results from outer to inner. |
| Behavioral error | An error matched by interface (`net.Error.Timeout()`), not by identity. |

---

## 4. Core Concepts

### 4.1 Domain-Specific Error Types

A real package rarely returns just `errors.New("...")`. It returns a struct that callers can inspect. Compare:

```go
// weak: only a string
return errors.New("user not found")

// strong: callers can pull out the user id
type UserNotFoundError struct {
    ID int64
}
func (e *UserNotFoundError) Error() string {
    return fmt.Sprintf("user %d not found", e.ID)
}
return &UserNotFoundError{ID: id}
```

A caller that wants to render a 404 page with the missing ID can `errors.As(err, &nfErr)` and read `nfErr.ID`. With the string version, the only path to the ID is parsing the message — a fragile anti-pattern.

### 4.2 The `Op / Err / Path` Triplet

The standard library and many large Go codebases (Upspin, Docker, Kubernetes) use a struct shaped like:

```go
type Error struct {
    Op   string // operation that failed: "open", "read", "lookup"
    Path string // resource: file path, URL, key, ...
    Err  error  // underlying cause
}

func (e *Error) Error() string {
    if e.Path == "" {
        return e.Op + ": " + e.Err.Error()
    }
    return e.Op + " " + e.Path + ": " + e.Err.Error()
}

func (e *Error) Unwrap() error { return e.Err }
```

This is the exact shape of `os.PathError`:

```go
// src/os/error.go (effectively)
type PathError struct {
    Op   string
    Path string
    Err  error
}
```

Why this shape works:

- **Op** explains the verb. "open foo.txt" is more useful than "foo.txt: bad".
- **Path** anchors the resource so the message is locatable.
- **Err** preserves the cause for `errors.Is` / `errors.As` traversal.
- The triplet composes: when `os.Open` fails it returns `*PathError{Op:"open", Path:..., Err: syscall.ENOENT}`. The caller can compare with `errors.Is(err, fs.ErrNotExist)` or unpack the path.

When you design a package, ask: what is the operation? what is the resource? what is the cause? If you have all three, use this shape.

### 4.3 Sentinel Errors

A sentinel is a single, package-level error value used as an identity:

```go
var ErrNotFound = errors.New("not found")

func Find(id int) (*Item, error) {
    if id < 0 {
        return nil, ErrNotFound
    }
    ...
}

// caller
if errors.Is(err, ErrNotFound) {
    // ...
}
```

Sentinels are useful when:

- The error has no extra payload — just identity.
- Callers across packages must compare against it.
- You want to make the error part of your public API.

Sentinels are problematic when:

- They flow through wrapping. Callers can no longer use `==`; they must use `errors.Is`.
- You change them later (silent API break for any caller doing `==`).
- You expose too many. Three is fine; thirty is a sign of weak design.

Famous sentinels: `io.EOF`, `io.ErrUnexpectedEOF`, `sql.ErrNoRows`, `os.ErrNotExist`, `context.Canceled`, `context.DeadlineExceeded`.

### 4.4 Categorization: Transient vs Permanent

For network code and any retry loop, you want to know: is this error worth retrying?

Two common approaches:

**Approach A: Behavioral interface.**

```go
type Temporary interface {
    Temporary() bool
}

func isTemporary(err error) bool {
    var t Temporary
    return errors.As(err, &t) && t.Temporary()
}
```

The `net.Error` interface has `Timeout() bool` and historically `Temporary() bool` (deprecated in modern Go). Callers can probe behavior without knowing the concrete type.

**Approach B: Category enum on a struct.**

```go
type Category int

const (
    CatUnknown Category = iota
    CatTransient
    CatPermanent
    CatBadRequest
)

type APIError struct {
    Cat Category
    Msg string
}
func (e *APIError) Error() string { return e.Msg }

func isRetryable(err error) bool {
    var ae *APIError
    return errors.As(err, &ae) && ae.Cat == CatTransient
}
```

The category is part of the data. The caller's retry loop becomes one branch.

Modern Go style leans on (B): explicit categories on a struct, plus `errors.Is` / `errors.As`. Behavioral interfaces remain useful for cross-package contracts.

### 4.5 Comparison With Exceptions in Other Languages

| Concern | Java/Python/C++ exceptions | Go errors |
|---------|----------------------------|-----------|
| Where they live | Separate control-flow channel (stack unwinding) | Normal return values |
| How you handle | `try { ... } catch(...) { ... }` | `if err != nil { ... }` |
| Caller awareness | Sometimes implicit (uncatchable RuntimeException) | Always explicit (errors are returned) |
| Performance cost | Throwing is expensive; catching is cheap | Returning is cheap; checking is cheap |
| Stack traces | Built in by default | Not built in (must be added by libraries or via `runtime.Caller`) |
| Hierarchy | Class-based (catch by parent) | Interface-based + `errors.Is/As` |
| Panic equivalent | `Error`, `RuntimeException` (uncatchable in normal code) | `panic` (for unrecoverable programmer mistakes only) |

Go errors are values, exceptions are control flow. The Go style trades verbosity ("`if err != nil`" everywhere) for clarity (every failure path is visible at the call site). Most large Go codebases settle into a small number of error types and a clear policy on when to panic.

### 4.6 When To Use What

| Situation | Tool |
|-----------|------|
| Simple error with no payload | `errors.New("...")` |
| Error with formatted data, no wrapping | `fmt.Errorf("...: %v", x)` |
| Error wrapping a cause | `fmt.Errorf("...: %w", cause)` (next topic) |
| Stable identity for callers | sentinel `var ErrX = errors.New("x")` |
| Rich domain error with fields | custom struct + `Error()` method |
| Programmer mistake (impossible state) | `panic("...")` |
| Many error categories | category enum + a single struct |
| Behavioral check across packages | behavioral interface (e.g. `Timeouter`) |

### 4.7 Should You Expose The Error Type?

A package's exported error types and sentinel variables are part of its API. Anything callers can `errors.As` or compare against is a contract you cannot break. Three rules of thumb:

1. **Expose sparingly.** Each exposed sentinel/type is a long-term commitment.
2. **Keep struct fields stable.** Renaming `Path` to `Resource` is a breaking change.
3. **Document them.** Every exposed error variable or type belongs in the package docs.

The `os` package is the gold standard here: `*PathError` is a stable, documented type used by every file operation, and `os.ErrNotExist` is a stable sentinel callers can match against.

---

## 5. Worked Examples

### Example 1 — Configuration Loader

```go
package config

import (
    "errors"
    "fmt"
    "os"
)

// Public errors
var (
    ErrFileMissing = errors.New("config file missing")
    ErrParseFailed = errors.New("config parse failed")
)

type LoadError struct {
    Path string
    Err  error
}

func (e *LoadError) Error() string {
    return fmt.Sprintf("load %q: %v", e.Path, e.Err)
}
func (e *LoadError) Unwrap() error { return e.Err }

func Load(path string) (*Config, error) {
    f, err := os.Open(path)
    if err != nil {
        if errors.Is(err, os.ErrNotExist) {
            return nil, &LoadError{Path: path, Err: ErrFileMissing}
        }
        return nil, &LoadError{Path: path, Err: err}
    }
    defer f.Close()
    cfg, err := parse(f)
    if err != nil {
        return nil, &LoadError{Path: path, Err: ErrParseFailed}
    }
    return cfg, nil
}
```

Notes:

- Two stable sentinels (`ErrFileMissing`, `ErrParseFailed`). Callers can `errors.Is(err, config.ErrFileMissing)`.
- A `LoadError` wrapping the cause and exposing the path. Callers can `errors.As(err, &le)` to read `le.Path`.
- Internal parser errors are categorized down to one of the two sentinels; the package surface stays small.

### Example 2 — HTTP Handler Error Routing

```go
type httpError struct {
    Status int
    Msg    string
    Err    error
}

func (e *httpError) Error() string { return e.Msg }
func (e *httpError) Unwrap() error { return e.Err }

func writeError(w http.ResponseWriter, err error) {
    var he *httpError
    if errors.As(err, &he) {
        http.Error(w, he.Msg, he.Status)
        return
    }
    http.Error(w, "internal error", http.StatusInternalServerError)
}
```

The handler returns `&httpError{Status: 404, Msg: "user not found", Err: domainErr}` and the writer routes by `Status`. Other code paths can still return plain errors — they get the catch-all 500.

### Example 3 — Retry Loop

```go
type Retryable interface {
    Retry() bool
}

type netError struct {
    Op   string
    Temp bool
    Err  error
}

func (e *netError) Error() string { return e.Op + ": " + e.Err.Error() }
func (e *netError) Unwrap() error { return e.Err }
func (e *netError) Retry() bool   { return e.Temp }

func withRetry(op func() error, n int) error {
    var err error
    for i := 0; i < n; i++ {
        err = op()
        if err == nil {
            return nil
        }
        var r Retryable
        if !errors.As(err, &r) || !r.Retry() {
            return err // non-retryable; fail fast
        }
        time.Sleep(backoff(i))
    }
    return err
}
```

The retry decision is a behavior, not a sentinel. Any error type that defines `Retry() bool` participates without coupling.

### Example 4 — Validation Errors With Multi-Field

```go
type FieldError struct {
    Field string
    Msg   string
}
func (e *FieldError) Error() string { return e.Field + ": " + e.Msg }

type ValidationErrors []*FieldError

func (v ValidationErrors) Error() string {
    if len(v) == 0 {
        return ""
    }
    parts := make([]string, len(v))
    for i, f := range v {
        parts[i] = f.Error()
    }
    return "validation failed: " + strings.Join(parts, "; ")
}

func validate(u *User) error {
    var out ValidationErrors
    if u.Name == "" {
        out = append(out, &FieldError{Field: "name", Msg: "required"})
    }
    if u.Age < 0 {
        out = append(out, &FieldError{Field: "age", Msg: "must be non-negative"})
    }
    if len(out) > 0 {
        return out
    }
    return nil
}
```

`ValidationErrors` is itself an error AND a slice of errors. Callers can range over it for per-field rendering. (Go 1.20+ `errors.Join` formalizes this pattern.)

### Example 5 — Database Layer With Sentinel Translation

```go
package userdb

import (
    "database/sql"
    "errors"
    "fmt"
)

var ErrUserNotFound = errors.New("user not found")

type DB struct{ sql *sql.DB }

func (d *DB) GetByID(id int64) (*User, error) {
    var u User
    err := d.sql.QueryRow("SELECT ... WHERE id = ?", id).Scan(&u.ID, &u.Name)
    if err != nil {
        if errors.Is(err, sql.ErrNoRows) {
            return nil, fmt.Errorf("getbyid %d: %w", id, ErrUserNotFound)
        }
        return nil, fmt.Errorf("getbyid %d: %w", id, err)
    }
    return &u, nil
}
```

The package exposes a domain sentinel (`ErrUserNotFound`) that hides the SQL detail. Callers no longer depend on `database/sql` at all. The `%w` wrap (covered in topic 04) preserves the cause for diagnostics.

---

## 6. Common Pitfalls at This Level

### 6.1 String Matching on `Error()` Output

```go
if strings.Contains(err.Error(), "not found") { ... } // brittle
```

Anti-pattern. Use sentinels and `errors.Is`. The exact message is not a public contract.

### 6.2 Returning Different Concrete Types From The Same Function

```go
func Lookup(id int) (*User, error) {
    if id < 0 {
        return nil, ErrBadID
    }
    if !exists(id) {
        return nil, &UserNotFoundError{ID: id}
    }
    return nil, &dbErr{...}
}
```

This is fine but stress-test it against your callers: do they need to distinguish all three? If yes, document each. If no, collapse to fewer types.

### 6.3 Building A Deep Hierarchy

```go
type BaseError struct{ ... }
type APIError struct{ BaseError; ... }
type DatabaseError struct{ BaseError; ... }
```

Resist. Go errors are not Java exceptions. Categorization belongs in fields, not types.

### 6.4 Confusing Nil Interface And Typed-Nil Pointer

Already covered in junior-level material; review the bug if you forget. At the middle level you will see this most often when a helper returns `*MyErr` and a caller adapts it to `error`:

```go
func helper() *MyErr { ... }
func caller() error  { return helper() } // BUG when helper returns nil
```

The fix is to check explicitly:

```go
func caller() error {
    if e := helper(); e != nil {
        return e
    }
    return nil
}
```

### 6.5 Using Panic For Expected Failures

```go
if err != nil {
    panic(err) // wrong, in 99% of cases
}
```

Reserve panic for impossible state ("this code is wrong, fix me"). Production server code that panics on a network error is a bug.

### 6.6 Wrapping And Then Comparing With `==`

```go
err := fmt.Errorf("ctx: %w", ErrNotFound)
if err == ErrNotFound { // false! err is the wrapper
    ...
}
```

Use `errors.Is(err, ErrNotFound)`. We will revisit in topic 03.

### 6.7 Forgetting `Unwrap()` On Custom Error Types

If you wrap an inner error, expose it:

```go
func (e *MyError) Unwrap() error { return e.Cause }
```

Otherwise `errors.Is` and `errors.As` cannot traverse past your error.

### 6.8 Logging The Same Error Twice

```go
log.Printf("op failed: %v", err)
return err
```

The caller now logs it again. Pick one place to log (usually the top of the call stack) and propagate everywhere else. We will discuss this in `professional.md`.

---

## 7. Mini Exercises

### Exercise 1 — Op/Err/Path Triplet

Build a `*FSError` that satisfies the `Op/Err/Path` shape and use it in a `readFile(path string) ([]byte, error)` function. Make sure `errors.Is(err, fs.ErrNotExist)` works through your wrapper.

### Exercise 2 — Behavioral Interface

Define a `type Timeouter interface { Timeout() bool }`. Define an `*OpError` with a `Timeout` field. Write a `withRetryOnTimeout(op func() error, n int) error` helper.

### Exercise 3 — Replace String Matching

Find or write a function that does `strings.Contains(err.Error(), "not found")`. Replace it with `errors.Is(err, ErrNotFound)` and a sentinel.

### Exercise 4 — Validation Error Aggregator

Implement `ValidationErrors` from Example 4. Add a `Has(field string) bool` method. Use it in a `validateUser` function that returns either nil or the aggregator.

### Exercise 5 — Two Senates Of Categorization

Pick a familiar package (HTTP client, file handler, SQL gateway). Sketch the category enum that would replace its current scatter of error types. Justify each category.

---

## 8. Cheat Sheet

| Pattern | When to use |
|---------|-------------|
| `errors.New("x")` (sentinel) | Identity-only error, public API |
| `fmt.Errorf("...: %v", x)` | Inline formatted error, no preserved cause |
| `fmt.Errorf("...: %w", err)` | Wrap a cause for `errors.Is`/`errors.As` |
| `Op/Err/Path` struct | Resource-oriented packages (filesystem, net, db) |
| Category enum on struct | Many distinct cases callers must branch on |
| Behavioral interface (`Timeouter`) | Cross-package contracts |
| Aggregate error type (`Errors`) | Validation, batch operations |

### Decision Tree: Which Error Pattern?

```
Is the error caller-relevant (do they branch on it)?
|
+- No  -> errors.New / fmt.Errorf, log, move on
|
+- Yes -> Does it carry data (path, id, status)?
         |
         +- No  -> sentinel: var ErrX = errors.New("x")
         |
         +- Yes -> custom struct with Error() and Unwrap()
                  Does it represent multiple resources?
                  |
                  +- Yes -> aggregate slice ErrorS or errors.Join
```

---

## 9. Visual Summary

```
+-------------------+
|  Caller code      |
|  err := pkg.Do()  |
|  if err != nil    |
+-------------------+
        |
        |  err is one of:
        v
+----------------------------------------------+
|  pkg's public error API                      |
|  - sentinel:   ErrNotFound, ErrBad, ...      |
|  - typed:      *PathError{Op,Path,Err}, ...  |
+----------------------------------------------+
        |
        v
+----------------------------------------------+
|  pkg's internal errors (wrapped or hidden)   |
+----------------------------------------------+
```

The middle-level skill is drawing this line precisely: what is in the public API, what is hidden behind it, and how the public surface remains stable.

---

## 10. Deeper Patterns

### 10.1 Hiding Internal Errors From Public APIs

A package's public API should not leak internal types. If your `userdb` package internally uses `database/sql` errors, those should not bubble up to callers as `*pq.Error` (Postgres driver type). Callers would couple to your driver choice.

```go
// internal: query the database
row, err := d.sql.QueryRow(...)
if err != nil {
    if errors.Is(err, sql.ErrNoRows) {
        return nil, fmt.Errorf("getbyid %d: %w", id, ErrUserNotFound)
    }
    // unknown error — don't expose driver internals; wrap with package context
    return nil, fmt.Errorf("getbyid %d: %w", id, ErrDBFailure)
}
```

Two sentinels (`ErrUserNotFound`, `ErrDBFailure`) hide the SQL implementation. Callers depend on `userdb`'s API only.

### 10.2 Error Translation At Boundaries

Every public-API boundary is a translation point: internal error types in, package errors out.

```
+------------------+
|  HTTP handler    |
+------------------+
        |
        v   *userdb.NotFound, *userdb.DBFailure (package types)
+------------------+
|  userdb package  |
+------------------+
        |
        v   *pq.Error, sql.ErrNoRows (driver types)
+------------------+
| database/sql     |
+------------------+
```

Each layer translates: `pq` errors become `userdb` errors, which become HTTP statuses. The handler does NOT see `pq.Error`. Callers do not see `sql.ErrNoRows`.

### 10.3 Error Identity Across Process Boundaries

If your error must travel across a network (REST/gRPC), it cannot be a Go pointer. The identity must be encoded in a serializable field — typically a string code or numeric code.

```go
type APIError struct {
    Code    string // "USER_NOT_FOUND", "RATE_LIMITED", ...
    Message string
}

func (e *APIError) Error() string { return e.Code + ": " + e.Message }
```

The receiving end reconstructs the error by string code. Callers compare with `err.Code == "USER_NOT_FOUND"` or with helper functions.

This is the opposite of the in-process sentinel pattern — across processes, only data round-trips, not pointer identity.

### 10.4 Logging Strategy

A common mistake is logging at every wrap site:

```go
// BAD: each layer logs the same error
func a() error {
    if err := b(); err != nil {
        log.Printf("a: %v", err)
        return fmt.Errorf("a: %w", err)
    }
    return nil
}
```

Triple-logged errors are noise. The convention:

- **Wrap with context** at each layer (adds info, no logging).
- **Log once** at the OUTER boundary — typically the HTTP handler, the goroutine root, or the CLI top-level.
- **Use structured logging**: `slog.Error("op failed", "err", err, "user", id)` rather than `Printf("op failed for user %d: %v", id, err)`.

### 10.5 Sentinel Versus Type Decision

A common dilemma: should this failure be a sentinel or a typed error?

| Use sentinel when | Use typed error when |
|-------------------|----------------------|
| No data per failure | Each failure carries unique fields |
| Caller branches by identity only | Caller reads structured fields |
| The error is ubiquitous (`io.EOF`) | The error is local to one operation |
| You want minimal API surface | You want to expose context |

A package can have both: a sentinel for identity, a typed error for data. The typed error wraps the sentinel:

```go
var ErrNotFound = errors.New("not found")

type LookupError struct {
    Key string
    Err error // typically wraps ErrNotFound
}
func (e *LookupError) Error() string { return ... }
func (e *LookupError) Unwrap() error { return e.Err }

return &LookupError{Key: k, Err: ErrNotFound}
```

Callers do `errors.Is(err, ErrNotFound)` for identity and `errors.As(err, &le)` for the key.

### 10.6 Stack Traces

The standard library's errors do NOT include stack traces. If you need them:

**Option A — third-party library**:
- `github.com/cockroachdb/errors`. Production-grade, used at CockroachDB and others.
- `github.com/pkg/errors` (archived but widely used). `errors.Wrap(err, "msg")` captures.

**Option B — manual capture**:
```go
import "runtime/debug"

func wrap(err error) error {
    return fmt.Errorf("%v\n%s", err, debug.Stack())
}
```

Heavy, ugly, but works.

**Option C — at the log site only**:
```go
log.Printf("op failed: %v\n%s", err, debug.Stack())
```

Capture the stack only when actually logging, not in every error.

In most production codebases, the choice is "Option A everywhere" or "no stacks anywhere". Mixed approaches lead to inconsistent diagnostics.

### 10.7 Errors Carrying Domain IDs

A specific pattern useful for distributed tracing:

```go
type RequestError struct {
    RequestID string
    Op        string
    Err       error
}
func (e *RequestError) Error() string { return ... }
func (e *RequestError) Unwrap() error { return e.Err }
```

Every error from a request carries the request ID. Logs and traces can correlate.

In hot paths, watch for the allocation cost (each request constructs a new error). For services at extreme scale, a context-based approach (request ID lives in `context.Context`, not the error) is sometimes better.

---

## 11. Worked Code: A Small `userdb` Package End-To-End

Putting the patterns together:

```go
// Package userdb provides typed user-record access.
package userdb

import (
    "context"
    "database/sql"
    "errors"
    "fmt"
)

// Public errors
var (
    ErrUserNotFound   = errors.New("user not found")
    ErrEmailDuplicate = errors.New("email already registered")
    ErrInvalidUser    = errors.New("invalid user")
)

// Conflict carries the offending UserID for callers that need it.
type Conflict struct {
    UserID int64
    Err    error
}

func (c *Conflict) Error() string {
    return fmt.Sprintf("user %d: %v", c.UserID, c.Err)
}

func (c *Conflict) Unwrap() error { return c.Err }

type DB struct{ sql *sql.DB }

func New(db *sql.DB) *DB { return &DB{sql: db} }

// GetByID returns the user with the given id, or ErrUserNotFound.
func (d *DB) GetByID(ctx context.Context, id int64) (*User, error) {
    var u User
    err := d.sql.QueryRowContext(ctx,
        "SELECT id, email, name FROM users WHERE id = ?", id,
    ).Scan(&u.ID, &u.Email, &u.Name)
    if err != nil {
        if errors.Is(err, sql.ErrNoRows) {
            return nil, fmt.Errorf("getbyid %d: %w", id, ErrUserNotFound)
        }
        return nil, fmt.Errorf("getbyid %d: %w", id, err)
    }
    return &u, nil
}

// Insert adds a user; returns *Conflict (wrapping ErrEmailDuplicate)
// when the email is already taken.
func (d *DB) Insert(ctx context.Context, u *User) error {
    if u.Email == "" {
        return fmt.Errorf("insert: %w", ErrInvalidUser)
    }
    res, err := d.sql.ExecContext(ctx,
        "INSERT INTO users (email, name) VALUES (?, ?)", u.Email, u.Name,
    )
    if err != nil {
        if existingID, ok := isDuplicateKey(err); ok {
            return &Conflict{UserID: existingID, Err: ErrEmailDuplicate}
        }
        return fmt.Errorf("insert %s: %w", u.Email, err)
    }
    id, _ := res.LastInsertId()
    u.ID = id
    return nil
}

func isDuplicateKey(err error) (int64, bool) {
    // driver-specific check, returns the colliding row's id if knowable
    return 0, false
}

type User struct {
    ID    int64
    Email string
    Name  string
}
```

Caller code:

```go
u, err := userdb.GetByID(ctx, 42)
if errors.Is(err, userdb.ErrUserNotFound) {
    http.Error(w, "user missing", 404)
    return
}
if err != nil {
    http.Error(w, "internal error", 500)
    return
}

err = userdb.Insert(ctx, &userdb.User{Email: "x@y.z"})
if errors.Is(err, userdb.ErrEmailDuplicate) {
    var cf *userdb.Conflict
    if errors.As(err, &cf) {
        http.Error(w, fmt.Sprintf("email taken (existing user %d)", cf.UserID), 409)
        return
    }
}
```

This is a complete, idiomatic Go package error API. Three sentinels, one typed error, all wrapping correctly.

---

## 12. Anti-Patterns Recap

| Anti-pattern | Why it's bad |
|--------------|-------------|
| `strings.Contains(err.Error(), "x")` | The exact string is not API |
| Returning typed nil pointer through `error` | Nil-interface bug |
| `panic` for routine failures | Loses visibility, breaks recovery semantics |
| Hierarchical error types (Java-style) | Doesn't fit Go's model |
| Logging at every wrap site | Triple-logs in production |
| `==` against wrapped errors | Wrapper is not the inner |
| Capitalized error strings | Composes badly |
| Many sentinels (>5-6 per package) | Tells you the design is fragmented |
| No `Unwrap()` on wrapper errors | Breaks `errors.Is`/`errors.As` |
| Custom error type with no fields | Just use `errors.New(...)` |

---

## 13. Next Steps

You now know how to design error types like a library author. The next topics drill into:

- **02-sentinel-errors**: how to design and version stable sentinel APIs.
- **03-errors-is-as**: how callers should match errors when wrapping is in play.
- **04-error-wrapping-percent-w**: the `%w` verb and the `Unwrap()` contract.
- **05-panic-and-recover**: when a panic is the right answer (rarely), and when it is a bug.

After that, you will be ready for the senior view: how the runtime represents `error` as an iface, what allocations errors cause, and how performance-sensitive code structures its error paths.
