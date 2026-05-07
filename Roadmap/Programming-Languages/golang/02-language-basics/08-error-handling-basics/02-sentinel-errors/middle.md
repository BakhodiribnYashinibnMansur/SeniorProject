# Go Sentinel Errors — Middle Level

## 1. Introduction

At the middle level, sentinel errors stop being a curiosity ("oh, that's how `io.EOF` works") and become a deliberate API design decision. You ask: *do I want this failure mode to be part of my package's contract?* If yes, declare a sentinel and document it. If the failure carries data — a path, an offset, a key — reach for a structured error type instead. Mature Go code mixes both.

Mid-level concerns:
- When to design a new sentinel vs a structured error vs both.
- The coupling tradeoff: every sentinel is a public-API promise.
- How to keep producer and caller in step across wrapping boundaries.
- The five or six recurring patterns where sentinels actually shine.
- The interaction with `errors.Is`, `errors.As`, and `Unwrap`.

---

## 2. Prerequisites

- Junior-level sentinel material.
- The `error` interface and `Unwrap` (2.8.1, 2.8.4).
- `errors.Is` and `errors.As` (2.8.5).
- `fmt.Errorf("...: %w", err)` semantics.
- Familiarity with stdlib `io`, `database/sql`, `context`, `os`.

---

## 3. Glossary

| Term | Definition |
|---|---|
| Sentinel | Exported, package-level error value used as an identifier |
| Structured error | An error type with fields, optionally implementing `Is` / `Unwrap` |
| `Is` method | A type method `Is(error) bool` enabling custom equality logic |
| Unwrap chain | Sequence reachable from an error via repeated `Unwrap` |
| Public-API surface | The set of exported identifiers a package guarantees stable |
| Coupling | Caller depending on a specific producer's exported sentinel |
| Sentinel re-export | Aliasing another package's sentinel under a new name |

---

## 4. Core Concepts

### 4.1 When to Design a Sentinel

A sentinel is appropriate when **all** of the following are true:

1. The failure mode is **well-known** to all consumers. ("not found", "EOF", "cancelled".)
2. The condition can be expressed without **per-call data**. ("permission denied" — yes; "permission denied for /etc/x at 0x7f..." — no, this wants a structured error).
3. The set of distinct sentinels is **small** and **closed**. (3-5 per package; not 50.)
4. You are willing to **promise stability**: removing or renaming a sentinel is a breaking change.

If any of those break down, prefer a structured type.

```go
// Good candidate for a sentinel: small, named, no fields needed.
var ErrAlreadyExists = errors.New("store: already exists")

// Bad candidate: the caller wants the colliding key.
type ConflictError struct{ Key string }
func (c *ConflictError) Error() string { return "conflict on " + c.Key }
```

### 4.2 When to Design a Structured Error Instead

If the caller needs to extract data from the error — a path, an offset, a status code — you want a struct:

```go
type PathError struct {
    Op   string
    Path string
    Err  error
}
func (e *PathError) Error() string { return e.Op + " " + e.Path + ": " + e.Err.Error() }
func (e *PathError) Unwrap() error { return e.Err }
```

This is the `os.PathError` design. The caller can `errors.As(err, &pathErr)` to access fields, and `errors.Is(err, os.ErrNotExist)` still works because `Unwrap` exposes the inner sentinel.

The combination is powerful: the structured error carries the data, the sentinel inside carries the identity, and `errors.Is`/`errors.As` queries them independently.

### 4.3 The Coupling Tradeoff

Every sentinel is a contract. Once you publish `var ErrNotFound = errors.New("...")`, every existing caller can depend on it via `errors.Is`. Removing it is a breaking change. Renaming the variable is a breaking change. Even *changing the message* is a behavioral change observable in logs.

```go
// v1.0.0
var ErrNotFound = errors.New("store: not found")

// v2.0.0 — silently rename: every caller importing the old name fails to build.
var ErrMissing = errors.New("store: missing")
```

So:
- Add sentinels conservatively. The first version of a package should have very few.
- When in doubt, return a structured error and add `Is` later.
- Document each sentinel's exact return conditions.

### 4.4 Wrapping Sentinels Across Layers

A typical service has 3-4 layers (HTTP handler, application logic, repository, driver). Each layer wraps the underlying error so the final message says *what failed* at every level. Sentinels survive wrapping because `errors.Is` walks the chain:

```go
// Driver layer
func (d *driver) Read(id int) ([]byte, error) {
    rows, err := d.db.QueryContext(ctx, sel, id)
    if err != nil {
        return nil, fmt.Errorf("driver: %w", err)
    }
    if !rows.Next() {
        return nil, fmt.Errorf("driver read %d: %w", id, sql.ErrNoRows)
    }
    ...
}

// Repository layer
func (r *repo) Find(id int) (User, error) {
    b, err := r.driver.Read(id)
    if errors.Is(err, sql.ErrNoRows) {
        return User{}, fmt.Errorf("repo: %w", ErrNotFound) // re-cast
    }
    if err != nil {
        return User{}, fmt.Errorf("repo find %d: %w", id, err)
    }
    return parse(b), nil
}

// Handler layer
func (h *handler) GetUser(w http.ResponseWriter, r *http.Request) {
    u, err := h.repo.Find(...)
    switch {
    case errors.Is(err, ErrNotFound):
        http.Error(w, "not found", http.StatusNotFound)
    case err != nil:
        http.Error(w, "internal", http.StatusInternalServerError)
    default:
        json.NewEncoder(w).Encode(u)
    }
}
```

Notice the **boundary translation** at the repo layer: it converts the driver-specific `sql.ErrNoRows` to its own `ErrNotFound`. This is good API hygiene — callers of the repo do not need to import `database/sql` to handle "not found".

### 4.5 The Producer/Caller Contract

Every exported sentinel comes with three implicit contracts:

1. **Identity**: callers may compare with `errors.Is`. The variable must remain the same allocation across the package's lifetime.
2. **Conditions**: producers must return it for exactly the conditions they document — no more, no less.
3. **Stability**: producers cannot drop or rename the sentinel without a major-version bump.

Breaking any of these is a silent behavior change for callers.

### 4.6 The `Is` Method Hook

A custom error type can opt into `errors.Is` matching with a method:

```go
type netError struct{ retryable bool }

func (n *netError) Error() string { return "network error" }

var ErrRetryable = errors.New("retryable")

func (n *netError) Is(target error) bool {
    if target == ErrRetryable && n.retryable {
        return true
    }
    return false
}
```

Now `errors.Is(myNetErr, ErrRetryable)` returns true when `myNetErr.retryable` is true. The `Is` method bridges structured errors and sentinel-style identity.

This is exactly how `os` makes its sentinels work across `*os.PathError`, `*os.LinkError`, and `*os.SyscallError`.

---

## 5. Real-World Patterns

### Pattern A — Read until EOF

The single most common sentinel pattern. Every Go program that reads a stream uses it:

```go
func readAll(r io.Reader) ([]byte, error) {
    buf := make([]byte, 0, 1024)
    chunk := make([]byte, 1024)
    for {
        n, err := r.Read(chunk)
        if n > 0 {
            buf = append(buf, chunk[:n]...)
        }
        if errors.Is(err, io.EOF) {
            return buf, nil
        }
        if err != nil {
            return buf, fmt.Errorf("read: %w", err)
        }
    }
}
```

`io.EOF` is the exit signal, not an error. The function turns it into a clean return.

### Pattern B — `sql.ErrNoRows` becomes "not found"

`QueryRow().Scan()` returns `sql.ErrNoRows` when there is no result. The repository converts it to its own sentinel:

```go
func (r *Repo) FindUser(ctx context.Context, id int) (User, error) {
    var u User
    err := r.db.QueryRowContext(ctx,
        "SELECT id, name FROM users WHERE id=$1", id,
    ).Scan(&u.ID, &u.Name)
    if errors.Is(err, sql.ErrNoRows) {
        return User{}, fmt.Errorf("user %d: %w", id, ErrNotFound)
    }
    if err != nil {
        return User{}, fmt.Errorf("user %d: %w", id, err)
    }
    return u, nil
}
```

The repo's `ErrNotFound` is the public face; `sql.ErrNoRows` is an implementation detail.

### Pattern C — `context.Canceled` fast-path

A worker checks for cancellation and exits immediately, treating it as expected (not a failure):

```go
func (w *Worker) run(ctx context.Context, jobs <-chan Job) error {
    for {
        select {
        case <-ctx.Done():
            return ctx.Err() // Canceled or DeadlineExceeded
        case job := <-jobs:
            if err := w.process(ctx, job); err != nil {
                if errors.Is(err, context.Canceled) {
                    return ctx.Err()
                }
                w.logger.Error("job failed", "err", err)
            }
        }
    }
}

// At the caller:
if err := worker.run(ctx, jobs); err != nil {
    if errors.Is(err, context.Canceled) {
        return nil // clean shutdown
    }
    return err
}
```

`context.Canceled` and `context.DeadlineExceeded` are sentinels. The worker propagates them; the caller suppresses `Canceled` and reports the rest.

### Pattern D — File existence check

The classic `os.IsNotExist`/`errors.Is(err, os.ErrNotExist)` idiom:

```go
func ensureConfig(path string) error {
    _, err := os.Stat(path)
    if errors.Is(err, os.ErrNotExist) {
        return writeDefaultConfig(path)
    }
    if err != nil {
        return fmt.Errorf("stat %q: %w", path, err)
    }
    return nil
}
```

`os.Stat` returns a `*PathError` whose inner error is `syscall.ENOENT`, which `errors.Is` matches against `os.ErrNotExist` via the `Is` method on `*os.PathError`.

### Pattern E — `http.ErrServerClosed` as a clean shutdown signal

```go
srv := &http.Server{Addr: ":8080", Handler: mux}

go func() {
    <-shutdownSignal
    srv.Shutdown(context.Background())
}()

if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
    log.Fatal(err)
}
```

`ListenAndServe` returns `http.ErrServerClosed` when `Shutdown` or `Close` is called intentionally. Treating it as success is the right pattern.

---

## 6. Five Worked Examples

### Example 1 — Library With a Tiny Sentinel Surface

```go
package cache

import "errors"

// ErrNotFound indicates the requested key is absent.
var ErrNotFound = errors.New("cache: not found")

// ErrExpired indicates the key existed but its TTL has elapsed.
var ErrExpired = errors.New("cache: expired")

type Cache struct {
    items map[string]item
}

type item struct {
    value    []byte
    expireAt time.Time
}

// Get returns the value for key, or ErrNotFound, or ErrExpired.
func (c *Cache) Get(key string) ([]byte, error) {
    it, ok := c.items[key]
    if !ok {
        return nil, ErrNotFound
    }
    if time.Now().After(it.expireAt) {
        return nil, ErrExpired
    }
    return it.value, nil
}
```

Two sentinels, both documented. Caller code is straightforward:

```go
v, err := c.Get(k)
switch {
case errors.Is(err, cache.ErrNotFound), errors.Is(err, cache.ErrExpired):
    return loadFromOrigin(k)
case err != nil:
    return nil, fmt.Errorf("cache get: %w", err)
}
return v, nil
```

### Example 2 — Repository Translating Driver Errors

```go
package repo

import (
    "context"
    "database/sql"
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("repo: not found")
var ErrConflict = errors.New("repo: conflict")

func (r *Repo) Insert(ctx context.Context, u User) error {
    _, err := r.db.ExecContext(ctx,
        "INSERT INTO users(id, name) VALUES($1, $2)", u.ID, u.Name,
    )
    if err == nil {
        return nil
    }

    // Convert driver-specific errors to repo-level sentinels.
    if isUniqueViolation(err) {
        return fmt.Errorf("user %d: %w", u.ID, ErrConflict)
    }
    return fmt.Errorf("insert user %d: %w", u.ID, err)
}

func (r *Repo) Get(ctx context.Context, id int) (User, error) {
    var u User
    err := r.db.QueryRowContext(ctx,
        "SELECT id, name FROM users WHERE id=$1", id,
    ).Scan(&u.ID, &u.Name)
    if errors.Is(err, sql.ErrNoRows) {
        return User{}, fmt.Errorf("user %d: %w", id, ErrNotFound)
    }
    if err != nil {
        return User{}, fmt.Errorf("get user %d: %w", id, err)
    }
    return u, nil
}
```

Callers depend only on `repo.ErrNotFound` and `repo.ErrConflict`. The driver and dialect can change behind the scenes.

### Example 3 — A Reader That Re-raises EOF

```go
package frame

import (
    "errors"
    "fmt"
    "io"
)

var ErrShortFrame = errors.New("frame: short")

type Frame struct {
    Type byte
    Data []byte
}

func ReadFrame(r io.Reader) (Frame, error) {
    var hdr [5]byte
    n, err := io.ReadFull(r, hdr[:])
    switch {
    case errors.Is(err, io.EOF) && n == 0:
        return Frame{}, io.EOF // pass through cleanly
    case errors.Is(err, io.ErrUnexpectedEOF):
        return Frame{}, fmt.Errorf("read header: %w", ErrShortFrame)
    case err != nil:
        return Frame{}, fmt.Errorf("read header: %w", err)
    }

    typ := hdr[0]
    size := binary.BigEndian.Uint32(hdr[1:5])

    body := make([]byte, size)
    if _, err := io.ReadFull(r, body); err != nil {
        if errors.Is(err, io.EOF) || errors.Is(err, io.ErrUnexpectedEOF) {
            return Frame{}, fmt.Errorf("read body: %w", ErrShortFrame)
        }
        return Frame{}, fmt.Errorf("read body: %w", err)
    }
    return Frame{Type: typ, Data: body}, nil
}
```

`io.EOF` at offset zero means "stream is over" — propagate it. `io.ErrUnexpectedEOF` mid-frame means "truncated" — convert to the package's own `ErrShortFrame`.

### Example 4 — `errors.Is` Walking Several Wrap Levels

```go
package main

import (
    "context"
    "errors"
    "fmt"
)

var ErrInternal = errors.New("svc: internal")

func deep(ctx context.Context) error {
    return ctx.Err() // returns context.Canceled if cancelled
}

func mid(ctx context.Context) error {
    if err := deep(ctx); err != nil {
        return fmt.Errorf("mid: %w", err)
    }
    return nil
}

func top(ctx context.Context) error {
    if err := mid(ctx); err != nil {
        return fmt.Errorf("top: %w", err)
    }
    return nil
}

func main() {
    ctx, cancel := context.WithCancel(context.Background())
    cancel()

    err := top(ctx)
    fmt.Println("err:", err)
    fmt.Println("Is Canceled?", errors.Is(err, context.Canceled))
    fmt.Println("== Canceled?", err == context.Canceled)
}
```

Output:
```
err: top: mid: context canceled
Is Canceled? true
== Canceled? false
```

`errors.Is` walks `top → mid → deep → context.Canceled` and matches. `==` only inspects the outermost wrapper.

### Example 5 — Sentinel With an `Is` Method

```go
package httpclient

import (
    "errors"
    "fmt"
    "net/http"
)

var ErrRetryable = errors.New("httpclient: retryable")

type StatusError struct {
    Status int
    URL    string
}

func (e *StatusError) Error() string {
    return fmt.Sprintf("status %d for %s", e.Status, e.URL)
}

// Is reports whether this status counts as retryable.
func (e *StatusError) Is(target error) bool {
    if target == ErrRetryable {
        return e.Status == 429 || (e.Status >= 500 && e.Status < 600)
    }
    return false
}
```

Use:

```go
err := client.Do(req)
if errors.Is(err, ErrRetryable) {
    backoff()
    continue
}
```

The structured error carries the status; the sentinel-via-`Is`-method gives callers a clean retry predicate.

---

## 7. Pros & Cons

### Pros
- Stable, named identifier for a known failure.
- Simple to use and reason about.
- Plays well with `errors.Is` and `%w`.
- Common stdlib idiom — every Go developer recognises the shape.

### Cons
- Couples caller to producer.
- Carries no per-call data.
- Adds public-API surface; harder to evolve.
- Easy to misuse with `==` against a wrapped error.
- Can multiply if you reach for one per failure mode (anti-pattern).

---

## 8. Use Cases

1. End-of-input markers (`io.EOF`).
2. Lookup misses (`ErrNotFound`).
3. Conflicts and idempotency (`ErrAlreadyExists`).
4. Cancellation/deadlines (`context.Canceled`, `context.DeadlineExceeded`).
5. State guards (`http.ErrServerClosed`, `sql.ErrTxDone`).
6. Permissions and existence (`os.ErrPermission`, `os.ErrNotExist`).
7. Retryable signals (`ErrRetryable` exposed via `Is` method).

---

## 9. Code Patterns at the Mid Level

### Pattern 1 — Single sentinels file

```go
// pkg/errors.go
package pkg

import "errors"

var (
    ErrNotFound  = errors.New("pkg: not found")
    ErrConflict  = errors.New("pkg: conflict")
    ErrReadOnly  = errors.New("pkg: read-only")
)
```

One file, sorted alphabetically, each with a doc comment.

### Pattern 2 — Boundary translation

Every layer's public errors are its own sentinels. Inner-layer sentinels are translated, not exposed.

### Pattern 3 — Switch over `errors.Is`

```go
switch {
case err == nil:
    ...
case errors.Is(err, ErrNotFound):
    ...
case errors.Is(err, ErrConflict):
    ...
case errors.Is(err, context.Canceled):
    ...
default:
    return fmt.Errorf("op: %w", err)
}
```

### Pattern 4 — `errors.Is` plus `errors.As`

```go
var pe *os.PathError
switch {
case errors.Is(err, os.ErrNotExist):
    return nil // expected
case errors.As(err, &pe):
    return fmt.Errorf("path %q: %w", pe.Path, err)
default:
    return err
}
```

`Is` for identity, `As` for data extraction.

### Pattern 5 — Sentinel + `Is` method for derived predicates

Define a sentinel like `ErrRetryable`, then implement `Is` on your structured types so callers can ask the simple question `errors.Is(err, ErrRetryable)`.

---

## 10. Clean Code Guidelines

1. **Document each sentinel** with the exact return conditions.
2. **Keep the set small** — sentinel inflation is a code smell.
3. **Translate at boundaries** — inner-layer sentinels rarely belong in the outer-layer's public surface.
4. **Use `errors.Is`** in checks; only use `==` when you control both ends and forbid wrapping.
5. **Wrap with `%w` plus context** when propagating.
6. **Name them after the condition, not the producer** — `ErrNotFound`, not `ErrUserMissingFromDB`.

---

## 11. Product Use / Feature Example

A multi-tenant API service. Every layer has a small sentinel set:

```go
// repo
var ErrNotFound = errors.New("repo: not found")
var ErrConflict = errors.New("repo: conflict")

// app
var ErrUnauthorized = errors.New("app: unauthorized")
var ErrInvalidInput = errors.New("app: invalid input")

// http
type response struct{ Code int; Body string }

func toResponse(err error) response {
    switch {
    case err == nil:
        return response{200, ""}
    case errors.Is(err, app.ErrInvalidInput):
        return response{400, "bad input"}
    case errors.Is(err, app.ErrUnauthorized):
        return response{401, "unauthorized"}
    case errors.Is(err, repo.ErrNotFound):
        return response{404, "not found"}
    case errors.Is(err, repo.ErrConflict):
        return response{409, "conflict"}
    case errors.Is(err, context.Canceled):
        return response{499, "client closed"}
    case errors.Is(err, context.DeadlineExceeded):
        return response{504, "deadline"}
    default:
        return response{500, "internal"}
    }
}
```

Adding a new well-known failure means: declare a sentinel, document, add a case. No type assertions, no tag fields, no enum drift.

---

## 12. Concurrency Considerations

Sentinels are read-only after init. Concurrent reads are safe by definition (no mutation, no synchronization).

Concerns appear elsewhere:
- A goroutine that returns `context.Canceled` to many waiters: each `errors.Is` is independent.
- A handler that wraps an error with request context (`fmt.Errorf("req %s: %w", id, err)`): each call gets its own wrapper allocation.

The sentinel itself is shared, immutable, and lock-free.

---

## 13. Error Handling at Boundaries

A common rule: each public function clearly states which sentinels it can return. The doc comment is the contract:

```go
// Find returns the user, or ErrNotFound if no user has the given id,
// or context.Canceled / context.DeadlineExceeded if ctx is cancelled,
// or another error wrapping the underlying database failure.
func (r *Repo) Find(ctx context.Context, id int) (User, error)
```

Callers can write:

```go
u, err := repo.Find(ctx, 42)
switch {
case errors.Is(err, repo.ErrNotFound):
    ...
case errors.Is(err, context.Canceled), errors.Is(err, context.DeadlineExceeded):
    ...
case err != nil:
    return fmt.Errorf("load user: %w", err)
}
```

---

## 14. Security Considerations

1. **Avoid leaking auth-related distinctions.** A "user not found" sentinel and a "wrong password" sentinel let attackers enumerate accounts. Use a single `ErrInvalidCredentials` for both.
2. **Sentinels carry no PII** by definition (the message is constant), so logging them freely is safe.
3. **Be wary of cross-package sentinel mixing.** If you re-cast `os.ErrPermission` as your own `ErrForbidden`, ensure callers don't accidentally treat both as the same condition.

---

## 15. Performance Tips

1. The cost of `errors.Is(err, ErrFoo)` on a 3-level chain: about 3 pointer comparisons. Negligible.
2. Wrapping with `%w` allocates one error per call. In hot loops, prefer to pass the underlying error untouched and wrap once at the boundary.
3. `errors.Is(nil, X)` is `false` for any non-nil `X` — no need to nil-check first.

---

## 16. Metrics & Analytics

Sentinels are perfect labels for error metrics. Define a small mapping:

```go
func category(err error) string {
    switch {
    case err == nil:                          return "ok"
    case errors.Is(err, ErrNotFound):         return "not_found"
    case errors.Is(err, ErrConflict):         return "conflict"
    case errors.Is(err, context.Canceled):    return "canceled"
    case errors.Is(err, context.DeadlineExceeded): return "deadline"
    default:                                   return "internal"
    }
}

errorsTotal.WithLabelValues(category(err)).Inc()
```

The label cardinality is bounded by the number of sentinels — Prometheus-friendly.

---

## 17. Best Practices

1. Keep the set small.
2. Document return conditions.
3. Translate at boundaries.
4. Always check with `errors.Is`.
5. Wrap with `%w` plus context.
6. Pair sentinels with structured types when data is needed.
7. Use the `Is` method to express derived predicates (`ErrRetryable`).
8. Treat sentinels as a stable public API.

---

## 18. Edge Cases & Pitfalls

### Pitfall 1 — Double wrap inside the sentinel itself
```go
var ErrFoo = fmt.Errorf("foo: %w", io.EOF) // ErrFoo Is io.EOF — usually unintended
```

### Pitfall 2 — Comparing different vendored versions
```go
if errors.Is(err, vendor.ErrFoo) // OK if both producer and caller built with the same vendor copy
```
Two builds with different vendored copies may have distinct `*errorString` allocations.

### Pitfall 3 — Re-exporting via copy
```go
package shim
var ErrNotFound = errors.New("not found") // NEW identity, not the original sentinel
```
Use `var ErrNotFound = original.ErrNotFound` for an alias.

### Pitfall 4 — Forgetting to `%w` and using `%v`
```go
return fmt.Errorf("op: %v", io.EOF) // chain is broken; errors.Is can't find io.EOF
```
Always `%w` when you want the sentinel still detectable downstream.

### Pitfall 5 — Treating cancellation as a sentinel-style failure to log
```go
if err := work(ctx); err != nil {
    log.Error("work failed", "err", err) // logs context.Canceled as a "failure"
}
```
Filter out cancellation/deadline cases first; they are usually expected.

---

## 19. Common Mistakes

| Mistake | Fix |
|---|---|
| `==` against possibly-wrapped errors | `errors.Is` |
| Sentinel with `fmt.Errorf` containing `%w` | `errors.New` |
| Re-declaring instead of aliasing | `var X = pkg.X` |
| Returning naked `errors.New("not found")` ad-hoc | Export a sentinel |
| Adding sentinels for every failure mode | Use structured errors when data is needed |

---

## 20. Common Misconceptions

**Misconception 1**: "Sentinels are always better than structured errors."
**Truth**: They are simpler but limited. Use structured errors when callers need data.

**Misconception 2**: "Adding a sentinel is a free change."
**Truth**: It expands the public API surface. You commit to keeping it stable.

**Misconception 3**: "Wrapping changes the sentinel."
**Truth**: Wrapping creates a new error whose chain contains the sentinel. The sentinel is unchanged.

**Misconception 4**: "I should re-export upstream sentinels under my own names."
**Truth**: Translate by value (return your own sentinel) when crossing API boundaries. Aliasing is a coupling smell.

**Misconception 5**: "`errors.Is` is for sentinels; `errors.As` is for types."
**Truth**: Largely true, but `errors.Is` also matches via the `Is` method on a custom type. Both tools cooperate.

---

## 21. Tricky Points

1. `var X = fmt.Errorf("...: %w", Y)` makes `X` wrap `Y`. Read carefully.
2. `errors.Is` walks `Unwrap`; if you implement custom error types, decide whether to expose `Unwrap`.
3. The `Is` method on a type can override identity — useful for groups (`ErrRetryable`).
4. Cross-vendored builds: same package path, same name, different allocation. `errors.Is` works only within one build.
5. `context.Cause` (Go 1.20+) returns the underlying cancellation reason — not always equal to `context.Canceled`.

---

## 22. Tests

```go
package store

import (
    "errors"
    "fmt"
    "testing"
)

func TestLookup_NotFound(t *testing.T) {
    s := New()
    _, err := s.Lookup("missing")
    if !errors.Is(err, ErrNotFound) {
        t.Fatalf("got %v, want ErrNotFound", err)
    }
}

func TestLookup_NotFoundIsWrapped(t *testing.T) {
    s := New()
    _, err := s.Lookup("missing")
    if err == ErrNotFound {
        t.Logf("returned bare sentinel; that's fine")
    }
    if !errors.Is(err, ErrNotFound) {
        t.Fatalf("Is should always work, got %v", err)
    }
}

func TestLookup_OtherErrorsNotMistakenForNotFound(t *testing.T) {
    s := New()
    _, err := s.LookupBroken()
    if errors.Is(err, ErrNotFound) {
        t.Fatalf("ErrNotFound matched unexpectedly: %v", err)
    }
    if err == nil {
        t.Fatal("expected an error")
    }
}

func TestWrapping(t *testing.T) {
    err := fmt.Errorf("ctx: %w", ErrNotFound)
    if !errors.Is(err, ErrNotFound) {
        t.Fatalf("wrapped sentinel not detected")
    }
    if err == ErrNotFound {
        t.Fatalf("== matched a wrapped sentinel; should never happen")
    }
}
```

---

## 23. Tricky Questions

**Q1**: Why does `var ErrFoo = fmt.Errorf("...: %w", io.EOF)` cause subtle bugs?
**A**: `ErrFoo` becomes a wrapper for `io.EOF`. `errors.Is(ErrFoo, io.EOF)` returns true. Code that says "if it's `ErrFoo`, do X; if it's `io.EOF`, do Y" hits both branches.

**Q2**: When does `==` against a sentinel fail?
**A**: When the producer wrapped it (with `%w`), or when re-exported as a fresh `errors.New`, or across two separately-vendored builds of the same package.

**Q3**: What's the difference between aliasing and re-declaring a sentinel?
**A**: `var ErrFoo = pkg.ErrFoo` is an alias — same allocation, `errors.Is` against either succeeds. `var ErrFoo = errors.New("foo")` is a re-declaration — new allocation, distinct identity.

---

## 24. Cheat Sheet

```go
// Declare
var (
    ErrNotFound  = errors.New("pkg: not found")
    ErrConflict  = errors.New("pkg: conflict")
)

// Return
return fmt.Errorf("op: %w", ErrNotFound)

// Detect
errors.Is(err, ErrNotFound)

// Translate at boundary
if errors.Is(err, sql.ErrNoRows) {
    return fmt.Errorf("...: %w", repo.ErrNotFound)
}

// Group via Is method
type StatusError struct{ Code int }
func (e *StatusError) Is(t error) bool {
    return t == ErrRetryable && (e.Code == 429 || e.Code >= 500)
}

// Switch
switch {
case errors.Is(err, ErrNotFound): ...
case errors.Is(err, context.Canceled): ...
case err != nil: return fmt.Errorf("op: %w", err)
}
```

---

## 25. Self-Assessment Checklist

- [ ] I know when a sentinel is the right tool vs a structured error
- [ ] I document return conditions for each sentinel
- [ ] I translate driver errors to my package's sentinels at boundaries
- [ ] I always wrap with `%w` when adding context
- [ ] I always check with `errors.Is` (rarely `==`)
- [ ] I understand the `Is` method for derived predicates
- [ ] I can explain the public-API impact of adding a sentinel
- [ ] I do not re-declare another package's sentinel

---

## 26. Summary

Sentinels are the simplest tool for "this specific failure mode." Use them for a small, closed set of well-known conditions: not-found, conflict, end-of-stream, cancelled, deadline, closed. Translate at layer boundaries — inner-layer sentinels rarely belong in your outer-layer's public surface. When data is part of the failure, prefer a structured error and let `errors.Is`/`Unwrap` connect them. Always wrap with `%w` to preserve the chain; always check with `errors.Is`. Keep the set small; every sentinel is a public-API promise.

---

## 27. What You Can Build

- Repositories with stable `ErrNotFound` / `ErrConflict`.
- Caches with `ErrExpired`.
- Stream parsers with `ErrShortFrame` reusing `io.EOF`.
- HTTP error mappers using `errors.Is` cascades.
- Worker pools that exit cleanly on `context.Canceled`.
- Retry decorators using a sentinel-via-`Is`-method `ErrRetryable`.

---

## 28. Further Reading

- [`errors` package](https://pkg.go.dev/errors)
- [`io` package variables](https://pkg.go.dev/io#pkg-variables)
- [`database/sql` package variables](https://pkg.go.dev/database/sql#pkg-variables)
- [`context` package variables](https://pkg.go.dev/context#pkg-variables)
- [Go blog — Working with Errors](https://go.dev/blog/go1.13-errors)
- [Dave Cheney — Don't just check errors, handle them gracefully](https://dave.cheney.net/2016/04/27/dont-just-check-errors-handle-them-gracefully)

---

## 29. Related Topics

- 2.8.1 The `error` interface
- 2.8.3 Custom error types
- 2.8.4 Error wrapping (`%w`)
- 2.8.5 `errors.Is` and `errors.As`
- 2.8.6 Idiomatic error handling
- 2.8.7 Panics

---

## 30. Diagrams & Visual Aids

### Layer translation

```
driver           repo                 handler
┌──────────────┐ ┌──────────────────┐ ┌─────────────────┐
│ sql.ErrNoRows│→│ repo.ErrNotFound │→│ HTTP 404         │
│ pq error 23..│→│ repo.ErrConflict │→│ HTTP 409         │
│ ctx.Err()    │→│ ctx.Err()        │→│ HTTP 499/504     │
└──────────────┘ └──────────────────┘ └─────────────────┘
```

### `errors.Is` chain walk

```
err ────Unwrap()────► wrapper2 ────Unwrap()────► sql.ErrNoRows
 │                       │                          │
 └── ==target? ✗         └── ==target? ✗            └── ==target? ✓
                                                       (target=sql.ErrNoRows)
```

### Sentinel vs structured

```
Sentinel: identifier only
   ErrNotFound  (name + message; no fields)

Structured: identifier + data
   *PathError { Op string; Path string; Err error }
       Unwrap() → inner error (often a sentinel)
       Is(target) → identity check (delegates)

Combination: structured error wrapping a sentinel
   pathErr.Err == os.ErrNotExist
   errors.Is(pathErr, os.ErrNotExist) // true
   errors.As(pathErr, &pe)            // accesses Path
```
