# Go Sentinel Errors — Professional / Internals Level

## 1. Overview

Sentinel errors are a thirty-year-old C convention adapted to Go: a stable, exported value the caller can compare against to identify a specific failure. The standard library uses them everywhere, modern Go community libraries use them selectively, and large services build classification, retry, and observability systems on top of them. This document catalogues the real OSS landscape, contrasts sentinels with structured-error designs you'll meet in the wild (gRPC status codes, kubernetes apimachinery), and lays out lint rules and idiomatic conventions.

---

## 2. Standard Library Sentinel Catalogue

Verified against `go1.22.0` source. Each entry: where the sentinel is declared, its message text, and its semantic role.

### 2.1 `io` (file: `src/io/io.go`)

```go
var EOF = errors.New("EOF")
var ErrUnexpectedEOF = errors.New("unexpected EOF")
var ErrShortBuffer = errors.New("short buffer")
var ErrShortWrite = errors.New("short write")
var ErrClosedPipe = errors.New("io: read/write on closed pipe")
var ErrNoProgress = errors.New("multiple Read calls return no data or error")
```

| Sentinel | Returned by | Meaning |
|---|---|---|
| `io.EOF` | All `Reader`s | Stream is over; not an error |
| `io.ErrUnexpectedEOF` | `io.ReadFull`, framed readers | Stream ended mid-message |
| `io.ErrShortBuffer` | Buffer too small for a record |
| `io.ErrShortWrite` | Writer wrote fewer bytes than requested |
| `io.ErrClosedPipe` | `io.PipeReader/Writer` after `Close` |
| `io.ErrNoProgress` | Pathological readers in tight loops |

`io.EOF` is the most-used sentinel in all of Go.

### 2.2 `database/sql` (file: `src/database/sql/sql.go`)

```go
var ErrNoRows = errors.New("sql: no rows in result set")
var ErrConnDone = errors.New("sql: connection is already closed")
var ErrTxDone = errors.New("sql: transaction has already been committed or rolled back")
```

| Sentinel | Returned by | Meaning |
|---|---|---|
| `sql.ErrNoRows` | `Row.Scan` | Single-row query returned zero rows |
| `sql.ErrConnDone` | Methods on a closed `*Conn` | Connection returned to the pool |
| `sql.ErrTxDone` | Methods on a finalised `*Tx` | Transaction already committed/rolled back |

These define the recoverable failure modes of `database/sql`. Driver-specific errors (PostgreSQL `pq`, MySQL, SQLite) are returned directly and identified through their structured types.

### 2.3 `context` (file: `src/context/context.go`)

```go
var Canceled = errors.New("context canceled")
var DeadlineExceeded = deadlineExceededError{}
```

`Canceled` is a classic `errors.New` sentinel. `DeadlineExceeded` is a typed value (a zero-sized struct implementing `error`, `Timeout() bool`, `Temporary() bool`). Both are sentinels by usage even though their underlying types differ.

| Sentinel | Returned by | Meaning |
|---|---|---|
| `context.Canceled` | `ctx.Err()` after `cancel()` | Caller-initiated cancellation |
| `context.DeadlineExceeded` | `ctx.Err()` after deadline | Timeout |

Go 1.20+ added `context.Cause(ctx)` which returns the underlying cancellation reason — possibly equal to one of the sentinels, possibly something more specific.

### 2.4 `os` (file: `src/os/error.go`)

```go
var (
    ErrInvalid    = fs.ErrInvalid    // "invalid argument"
    ErrPermission = fs.ErrPermission // "permission denied"
    ErrExist      = fs.ErrExist      // "file already exists"
    ErrNotExist   = fs.ErrNotExist   // "file does not exist"
    ErrClosed     = fs.ErrClosed     // "file already closed"
    ErrNoDeadline = errNoDeadline()  // "file type does not support deadline"
)
```

These are aliases of `io/fs` sentinels, declared in `src/io/fs/fs.go`:

```go
var (
    ErrInvalid    = errors.New("invalid argument")
    ErrPermission = errors.New("permission denied")
    ErrExist      = errors.New("file already exists")
    ErrNotExist   = errors.New("file does not exist")
    ErrClosed     = errors.New("file already closed")
)
```

The aliasing means `os.ErrNotExist == fs.ErrNotExist` is `true`. They are the same allocation. Idiomatic.

`os.PathError` (now `*fs.PathError`) wraps these via `Unwrap`, so `errors.Is(pathErr, os.ErrNotExist)` walks the chain into the syscall error and matches via the structured error's `Is` method.

### 2.5 `bufio` (file: `src/bufio/bufio.go`)

```go
var (
    ErrInvalidUnreadByte = errors.New("bufio: invalid use of UnreadByte")
    ErrInvalidUnreadRune = errors.New("bufio: invalid use of UnreadRune")
    ErrBufferFull        = errors.New("bufio: buffer full")
    ErrNegativeCount     = errors.New("bufio: negative count")
)
```

`ErrBufferFull` is the one most often checked: it means the next call to `ReadSlice` (or related) cannot fit a delimiter into the internal buffer.

### 2.6 `net/http` (file: `src/net/http/server.go` and others)

```go
var ErrServerClosed = errors.New("http: Server closed")
```

In `src/net/http/request.go`:

```go
var ErrNoCookie = errors.New("http: named cookie not present")
var ErrNoLocation = errors.New("http: no Location header in response")
var ErrMissingFile = errors.New("http: no such file")
var ErrBodyReadAfterClose = errors.New("http: invalid Read on closed Body")
```

In `src/net/http/transport.go`:

```go
var ErrUseLastResponse = errors.New("net/http: use last response")
```

`http.ErrServerClosed` is the canonical "Server.Close was called intentionally" signal — every long-running HTTP server treats it as success.

### 2.7 Other Stdlib Sentinels

| File | Sentinel(s) |
|---|---|
| `src/crypto/rsa/rsa.go` | `ErrMessageTooLong`, `ErrDecryption`, `ErrVerification` |
| `src/encoding/json/decode.go` | `errPhase` (internal) |
| `src/path/filepath/path.go` | `ErrBadPattern`, `SkipDir`, `SkipAll` |
| `src/regexp/syntax/parse.go` | many `Err*` sentinels for regex parse errors |
| `src/syscall/zerrors_*.go` | `Errno` constants — typed sentinels per errno |

`syscall.Errno` is interesting: a typed `uintptr` with `Error()`, `Is()`, and `Temporary()` methods. Each errno is effectively a sentinel, but their type is `Errno`, not `*errorString`.

---

## 3. Stylistic Variants

### 3.1 The pure sentinel
```go
var ErrFoo = errors.New("pkg: foo")
```
The default. ~70% of stdlib sentinels.

### 3.2 The typed-value sentinel
```go
type fooError struct{}
func (fooError) Error() string { return "pkg: foo" }
var ErrFoo = fooError{}
```
Used by `context.DeadlineExceeded`. Allows custom methods (`Timeout()`, `Temporary()`).

### 3.3 The aliased sentinel
```go
import "io/fs"

var ErrNotExist = fs.ErrNotExist
```
Same identity, two names. Useful when one package's sentinels live in two packages by historical or compatibility reasons.

### 3.4 The wrapping sentinel (uncommon, often a bug)
```go
var ErrFoo = fmt.Errorf("pkg: foo: %w", io.EOF) // ErrFoo wraps io.EOF
```
Almost always unintentional. Discussed at length in the find-bug document.

---

## 4. Naming Convention: `Err<Reason>`

Universal convention since Go 1.0. Variants:
- `ErrNotFound`, `ErrNoRows`, `ErrInvalidInput` — descriptive.
- `ErrServerClosed`, `ErrConnDone`, `ErrTxDone` — state-based.
- `ErrShortBuffer`, `ErrShortWrite` — protocol-based.

The `Err` prefix is a strong cue: `gopls`, `errorlint`, and human reviewers all expect it. Deviations are treated as code smell.

A sister convention: skip values like `path/filepath.SkipDir` and `path/filepath.SkipAll`. These are sentinels used as *control flow signals* (returned to instruct `Walk`/`WalkDir` to skip), not as failures. They follow the `Err`-less pattern by intent.

---

## 5. Beyond Sentinels: Other Idiomatic Designs

### 5.1 gRPC Status Codes

`google.golang.org/grpc/status` and `google.golang.org/grpc/codes` express failures via a fixed enumeration of codes plus a message:

```go
import (
    "google.golang.org/grpc/codes"
    "google.golang.org/grpc/status"
)

return nil, status.Errorf(codes.NotFound, "user %d not found", id)
```

The caller extracts:

```go
if s, ok := status.FromError(err); ok && s.Code() == codes.NotFound {
    ...
}
```

Codes are constants of type `codes.Code` (a `uint32`). They are sentinel-like but carry a structured Status with details. This design is a sentinel-of-codes (the integer) inside a structured error (the Status with message and details).

For service-API errors crossing language and process boundaries, this is the dominant design. Sentinels via `errors.Is` work poorly across the wire (the value isn't transmitted), so a numeric code is mandatory.

### 5.2 Kubernetes apimachinery

`k8s.io/apimachinery/pkg/api/errors` defines a `StatusError` type and helper functions:

```go
import apierrors "k8s.io/apimachinery/pkg/api/errors"

if apierrors.IsNotFound(err) { ... }
if apierrors.IsConflict(err) { ... }
if apierrors.IsTimeout(err) { ... }
if apierrors.IsAlreadyExists(err) { ... }
if apierrors.IsServerTimeout(err) { ... }
if apierrors.IsTooManyRequests(err) { ... }
```

These predicates wrap an embedded `metav1.Status` with a `Reason` and `Code`. The predicates are *named functions* rather than sentinels, but the design intent is identical: classify an error into a closed set of categories.

For Kubernetes-style APIs, function predicates are preferred because the underlying `Status` carries a lot of data and the user shouldn't compare against a bare value.

### 5.3 Stdlib structured errors with sentinels inside

`os.PathError`, `os.LinkError`, `os.SyscallError`, `net.OpError`, `url.Error`, `exec.Error`, `tls.RecordHeaderError` — all structured types. Most expose `Unwrap()` so the inner sentinel is reachable through the chain.

The pattern: structured outer for data, sentinel inner for identity, `errors.Is` and `errors.As` work together.

### 5.4 Redis / NoSQL conventions

Most Redis clients export `redis.Nil`:

```go
var Nil = errors.New("redis: nil")
```

Returned when a key is missing. Naming deviates from `Err<Reason>` because it predates the convention; the value still works as a sentinel.

`go-redis` (`github.com/redis/go-redis/v9`) at `internal/proto/reader.go` declares it. Code: `errors.Is(err, redis.Nil)` is the universal "key not found" check.

MongoDB drivers expose `mongo.ErrNoDocuments`. Cassandra clients expose `gocql.ErrNotFound`. Each is a sentinel of the same shape.

### 5.5 Cloud SDK conventions

AWS SDK v2 uses structured types per service (e.g., `*types.NoSuchEntityException`) instead of sentinels. The classification is via `errors.As` to a specific type.

```go
var nse *types.NoSuchEntityException
if errors.As(err, &nse) { ... }
```

This is sentinel-equivalent for the caller — a closed set of expected types — but uses `As` for the dispatch.

---

## 6. Lint Rules

### 6.1 `errorlint`

`github.com/polyfloyd/go-errorlint` enforces:

- `err == ErrFoo` → use `errors.Is(err, ErrFoo)`. (Configurable; `comparison: true`.)
- `if e, ok := err.(*MyErr); ok` → use `errors.As(err, ...)`. (`asserts: true`.)
- `fmt.Errorf("...: %s", err)` → use `%w`. (`errorf: true`.)

Recommended in any project using sentinels and wrapping. Add to `.golangci.yml`:

```yaml
linters:
  enable:
    - errorlint
linters-settings:
  errorlint:
    errorf: true
    asserts: true
    comparison: true
```

### 6.2 `staticcheck`

- `SA4006`, `SA4011` and friends pick up dead-error patterns.
- `S1009`, `S1020` for redundant nil checks before `errors.Is`.

### 6.3 `gocritic`

- `wrapperFunc` flags some sentinel anti-patterns.
- `appendAssign` and friends are unrelated, but the linter's broader checks help.

### 6.4 `nilerr`

Flags `return nil` after `if err != nil`, an easy way to silently swallow sentinels.

---

## 7. When to Design APIs With Structured Errors Instead

### 7.1 Per-call data

If callers need to know *which* path, key, ID, or offset failed, return a structured error.

```go
type NotFoundError struct{ Resource string; ID string }
func (e *NotFoundError) Error() string { return e.Resource + " " + e.ID + " not found" }
```

Classification still works:
```go
var pe *NotFoundError
errors.As(err, &pe) // get fields
```

Combine with a sentinel for the category:
```go
var ErrNotFound = errors.New("not found")
func (e *NotFoundError) Unwrap() error { return ErrNotFound }
```

### 7.2 Open-ended categories

When the failure-mode set will grow (a third-party API returning new error codes monthly), a code field in a structured error scales better than dozens of sentinels.

### 7.3 Cross-process boundaries

Sentinels lose their identity when serialised. Use codes (numeric or string) plus structured fields, then map back to local sentinels at the deserialiser layer.

---

## 8. Code Catalogue From Real OSS

### 8.1 `go-redis/redis`

```go
// internal/proto/reader.go (representative)
var Nil = errors.New("redis: nil")
```

### 8.2 `etcd`

`go.etcd.io/etcd/client/v3` exposes:
```go
var ErrNoAvailableEndpoints = errors.New("etcdclient: no available endpoints")
var ErrOldCluster = errors.New("etcdclient: old cluster version")
```

### 8.3 `kubernetes`

Many `Err*` vars in `staging/src/k8s.io/apimachinery/pkg/api/errors`:
```go
var ErrIPv6FieldsForbidden = errors.New("...")
```
But the classification API is the predicate functions described in §5.2.

### 8.4 `Docker / moby`

`github.com/docker/docker/errdefs` defines a closed set of *interfaces* (one per error category): `ErrNotFound`, `ErrConflict`, `ErrUnauthorized`. Functions like `errdefs.IsNotFound(err)` check via `errors.As` to the interface.

This is a sentinel-by-interface design — closed set, programmatic classification, but no value comparison.

### 8.5 `gRPC`

Already covered: codes-of-an-enum inside a structured Status.

### 8.6 `prometheus/client_golang`

```go
// in prometheus/registry.go
var (
    AlreadyRegisteredError = errors.New("...")
)
```

`AlreadyRegistered` is interesting because it's a structured error type with a sentinel-like role:
```go
type AlreadyRegisteredError struct { ExistingCollector, NewCollector Collector }
func (err AlreadyRegisteredError) Error() string { ... }
```

Caller code: `var ar prometheus.AlreadyRegisteredError; errors.As(err, &ar)`.

### 8.7 `aws-sdk-go-v2`

No sentinels. Every service-specific error is a struct type. Classification via `errors.As(err, &specificType)`.

The contrast with stdlib's sentinel-heavy style reflects the maturity gradient: small, simple packages prefer sentinels; large, evolving SDK surfaces prefer structured types per category.

---

## 9. Tooling — Generating Sentinels

### 9.1 Code generators

Tools like `protoc-gen-go-grpc`, `oapi-codegen`, `go-bindata` emit sentinels for each enumerated error condition. The pattern:

```go
// generated, do not edit
var (
    ErrCodeNotFound       = errors.New("api: not found")
    ErrCodeUnauthorized   = errors.New("api: unauthorized")
    ErrCodeInternal       = errors.New("api: internal")
)
```

Treat these as part of the API surface. Re-running the generator must keep the variable names stable.

### 9.2 Doc generators

`go doc` lists package vars; sentinels appear as their own section. Document each with a one-line comment:

```go
// ErrNotFound is returned by Find when the resource does not exist.
var ErrNotFound = errors.New("repo: not found")
```

`go doc -all` shows the comment, making the sentinel's contract discoverable.

---

## 10. Sentinels in Tests

### 10.1 Asserting a sentinel return

```go
err := svc.Do(ctx)
if !errors.Is(err, ErrFoo) {
    t.Fatalf("got %v, want ErrFoo", err)
}
```

Standard pattern. Robust to wrapping changes inside `svc.Do`.

### 10.2 Asserting absence

```go
if errors.Is(err, ErrFoo) {
    t.Fatalf("ErrFoo matched unexpectedly")
}
```

### 10.3 Negative tests

Verify that a different error does *not* satisfy the sentinel:
```go
err := errors.New("totally unrelated")
if errors.Is(err, ErrFoo) {
    t.Fatal("unrelated error matched sentinel; identity is broken")
}
```

This catches the "I made `ErrFoo = fmt.Errorf("...: %w", X)`" trap.

### 10.4 Fuzz / property tests

For libraries returning sentinels, a fuzz test that walks the public API and checks that every exposed error is reachable via at least one documented sentinel can catch undocumented failure modes.

---

## 11. API Compatibility

### 11.1 Adding a sentinel

Backward-compatible. New importers can opt in; old importers are unaffected.

### 11.2 Removing or renaming a sentinel

Major-version bump (Go's import-path versioning).

### 11.3 Changing return conditions

If `Foo` previously returned `ErrA` for condition X and now returns `ErrB`, that's a behavior break. Existing callers checking `errors.Is(err, ErrA)` silently miss the new behavior.

### 11.4 Changing internal type

You can change `var ErrFoo = errors.New(...)` to `var ErrFoo = myErrType{}` only if:
- The new type is comparable.
- The variable's identity is preserved (the same `var` stays exported with the same name).
- All consumers using `errors.Is` continue to match.

The catch: `==` may behave differently if the new type is a value rather than a pointer. Sentinels exposed via `==` should keep their original allocation type.

---

## 12. Internationalisation

Sentinel messages are English. There's no Go convention for translated error messages — you'd produce a translation at the presentation layer, keyed by sentinel identity:

```go
var translations = map[error]map[string]string{
    ErrNotFound: {
        "en": "not found",
        "es": "no encontrado",
        "de": "nicht gefunden",
    },
}

func translate(err error, lang string) string {
    for s, t := range translations {
        if errors.Is(err, s) {
            if msg, ok := t[lang]; ok {
                return msg
            }
        }
    }
    return err.Error()
}
```

The sentinel is the language-independent identity; presentation maps to a localised string.

---

## 13. Best Practices Summary

1. Use `errors.New` for sentinels.
2. Name `Err<Reason>`.
3. Prefix message with the package name.
4. Keep the set small; document each.
5. Translate at API boundaries.
6. Always check with `errors.Is`.
7. Wrap with `%w` plus context.
8. Pair with structured errors when data matters.
9. Lint with `errorlint`.
10. Treat sentinels as a stable API contract.

---

## 14. Self-Assessment Checklist

- [ ] I can list 10+ stdlib sentinels and which packages they live in.
- [ ] I know the difference between a sentinel and a code-based design.
- [ ] I know the gRPC status-codes alternative.
- [ ] I know the kubernetes predicates alternative.
- [ ] I have `errorlint` enabled in my project.
- [ ] I document the return conditions of every sentinel I export.
- [ ] I translate inner-layer sentinels at API boundaries.
- [ ] I avoid the `var ErrFoo = fmt.Errorf("...: %w", ...)` trap.

---

## 15. Summary

Sentinels are a pervasive Go idiom: thirty stdlib packages export them, every Go service consumes them via `errors.Is`. Real OSS shows a spectrum: pure sentinels (`io.EOF`, `redis.Nil`), typed-value sentinels (`context.DeadlineExceeded`), aliased sentinels (`os.ErrNotExist == fs.ErrNotExist`), structured errors that wrap sentinels (`os.PathError`), code-based designs (gRPC `Status`, kubernetes predicates), and SDK-style structured-only designs (AWS SDK v2). The right choice depends on data needs, evolution speed, and process boundaries. For local Go libraries and small services, sentinels remain the simplest and clearest tool.

---

## 16. Further Reading

- Standard library files
  - `src/io/io.go`
  - `src/database/sql/sql.go`
  - `src/context/context.go`
  - `src/os/error.go`, `src/io/fs/fs.go`
  - `src/net/http/server.go`, `src/net/http/request.go`
  - `src/bufio/bufio.go`
  - `src/syscall/zerrors_*.go`
- [Go blog — Working with Errors in Go 1.13](https://go.dev/blog/go1.13-errors)
- [errorlint](https://github.com/polyfloyd/go-errorlint)
- [gRPC status codes](https://pkg.go.dev/google.golang.org/grpc/codes)
- [kubernetes apimachinery errors](https://pkg.go.dev/k8s.io/apimachinery/pkg/api/errors)
- [docker errdefs](https://pkg.go.dev/github.com/docker/docker/errdefs)
- [redis/go-redis Nil](https://pkg.go.dev/github.com/redis/go-redis/v9#pkg-variables)
- 2.8.3 Custom error types
- 2.8.4 Error wrapping
- 2.8.5 `errors.Is` / `errors.As`
