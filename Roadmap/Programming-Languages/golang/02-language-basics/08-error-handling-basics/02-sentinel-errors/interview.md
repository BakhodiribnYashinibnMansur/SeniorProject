# Go Sentinel Errors — Interview Questions

## Table of Contents
1. [Junior Level Questions](#junior-level-questions)
2. [Middle Level Questions](#middle-level-questions)
3. [Senior Level Questions](#senior-level-questions)
4. [Scenario-Based Questions](#scenario-based-questions)
5. [FAQ](#faq)

---

## Junior Level Questions

**Q1: What is a sentinel error in Go and when would you use one?**

**Answer**: A sentinel error is a single, exported, package-level error value used as a stable identifier for a specific failure condition. You declare it once with `errors.New`, name it `Err<Reason>`, and let callers compare against it with `errors.Is`.

```go
package store

import "errors"

var ErrNotFound = errors.New("store: not found")
```

Use a sentinel when:
- The failure mode is well-known and won't carry extra data.
- You expect callers to detect the condition explicitly.
- The set of conditions is small (3-5 sentinels per package).

The standard library is full of them: `io.EOF`, `sql.ErrNoRows`, `context.Canceled`, `os.ErrNotExist`, `http.ErrServerClosed`.

---

**Q2: Should you compare errors with `==` or `errors.Is`? Why?**

**Answer**: Use `errors.Is`. It walks the unwrap chain, so it works even when the producer wrapped the sentinel with `fmt.Errorf("...: %w", sentinel)`. `==` only compares the outermost value and silently fails against wrapped errors.

```go
err := fmt.Errorf("read failed: %w", io.EOF)

err == io.EOF             // false — wrapper is not io.EOF
errors.Is(err, io.EOF)    // true — walks chain to find io.EOF
```

The `==` form is fine in tight cases where you control both producer and caller and you forbid wrapping. But the safer default is always `errors.Is`. Lint rules like `errorlint` flag bare `==` against errors.

---

**Q3: Name 5 sentinel errors from the standard library and the package they live in.**

**Answer**:

| Sentinel | Package | Meaning |
|---|---|---|
| `io.EOF` | `io` | End of input stream |
| `sql.ErrNoRows` | `database/sql` | Query returned zero rows |
| `context.Canceled` | `context` | Context was cancelled |
| `context.DeadlineExceeded` | `context` | Context's deadline passed |
| `os.ErrNotExist` | `os` (alias of `fs.ErrNotExist`) | File or directory missing |

Other common ones: `io.ErrUnexpectedEOF`, `io.ErrShortBuffer`, `sql.ErrConnDone`, `sql.ErrTxDone`, `os.ErrExist`, `os.ErrPermission`, `bufio.ErrBufferFull`, `http.ErrServerClosed`. Third-party clients have similar conventions: `redis.Nil`, `mongo.ErrNoDocuments`.

---

**Q4: What are the downsides of sentinel errors?**

**Answer**:

1. **Tight coupling**: every consumer that does `errors.Is(err, pkg.ErrFoo)` depends on the producer's exported variable. Removing or renaming it is a breaking change.
2. **No data**: the sentinel is just an identifier. If the caller needs to know which key, path, or ID failed, you need a structured error type alongside.
3. **Hard to evolve**: adding fields means abandoning the sentinel and migrating to a structured type.
4. **Public API surface grows**: each sentinel is part of the package's compatibility promise.
5. **Easy to misuse**: `==` against a wrapped sentinel silently fails; declaring with `fmt.Errorf("...: %w", X)` accidentally creates a chain.
6. **Cross-process/cross-vendor identity is fragile**: identity depends on the same allocation.

For a small, fixed set of well-known failure modes, sentinels are great. For evolving APIs with structured failure data, structured errors are better.

---

**Q5: How does a sentinel interact with `fmt.Errorf("...: %w", err)` wrapping?**

**Answer**: `fmt.Errorf` with `%w` returns a new error whose `Unwrap` method points to the wrapped error. The chain is:

```
fmt.Errorf wrapper → wrapped sentinel
```

`errors.Is` walks this chain by repeatedly calling `Unwrap`. So:

```go
err := fmt.Errorf("read at offset %d: %w", offset, io.EOF)
errors.Is(err, io.EOF)  // true
err == io.EOF           // false (the outer wrapper is not io.EOF)
```

Wrapping preserves the sentinel for `errors.Is` while letting you attach context (offset, key, path) in the message. This is the standard pattern: wrap to add context, check with `errors.Is`. As long as every layer uses `%w`, the sentinel remains detectable at the top.

If anyone uses `%v` or `%s` instead of `%w`, the chain is broken and `errors.Is` no longer finds the sentinel. Always wrap with `%w` when you want the sentinel detectable downstream.

---

**Q6: How do you declare a sentinel?**

**Answer**:

```go
package store

import "errors"

var ErrNotFound = errors.New("store: not found")
```

Conventions:
- Use `errors.New`, not `fmt.Errorf`.
- Name it `Err<Reason>` — `ErrNotFound`, `ErrConflict`, `ErrTimeout`.
- Prefix the message with the package name.
- Group multiple sentinels in a single `var (...)` block, often in `errors.go`.
- Document each one with a doc comment listing the conditions that produce it.

```go
// ErrNotFound is returned by Lookup when key is absent.
var ErrNotFound = errors.New("store: not found")

// ErrConflict is returned by Insert when the key already exists.
var ErrConflict = errors.New("store: conflict")
```

---

**Q7: What's the difference between `errors.Is` and `errors.As`?**

**Answer**:

- `errors.Is(err, target)` answers "is `target` somewhere in the chain?" — identity-based, used for sentinels.
- `errors.As(err, &target)` answers "is there a value of `target`'s type in the chain?" — type-based, used for structured errors. It populates `target` with the matching value.

```go
// Sentinel check
if errors.Is(err, ErrNotFound) { ... }

// Type extraction
var pe *fs.PathError
if errors.As(err, &pe) {
    fmt.Println(pe.Path)
}
```

Both walk the chain. `Is` checks identity (or delegates to a custom `Is` method). `As` checks type (and assigns the matching value to the target).

---

## Middle Level Questions

**Q8: When should you prefer a structured error over a sentinel?**

**Answer**: When the caller needs to extract data from the error. A sentinel is just an identifier; if you need the failed path, key, ID, or status code, the caller cannot get it from a bare sentinel.

```go
// Sentinel: identifier only
var ErrNotFound = errors.New("not found")

// Structured: identifier + data
type NotFoundError struct{ Resource string; ID string }
func (e *NotFoundError) Error() string { return e.Resource + " " + e.ID + " not found" }
```

Often you want both:
```go
type NotFoundError struct{ Resource string; ID string }
func (e *NotFoundError) Error() string { ... }
func (e *NotFoundError) Unwrap() error { return ErrNotFound } // category
```

Now `errors.Is(err, ErrNotFound)` works for classification, and `errors.As(err, &nfe)` works for data extraction. This is the `os.PathError` pattern.

---

**Q9: What's a sentinel "boundary translation" and why do it?**

**Answer**: When an error crosses a public-API boundary (e.g., from a database driver up to a repository, or from a repository up to an HTTP handler), you translate inner-layer sentinels to outer-layer sentinels. The point is to keep inner-layer details out of the outer-layer's public contract.

```go
// repo translates sql.ErrNoRows into its own ErrNotFound
func (r *Repo) Get(...) (User, error) {
    err := r.db.QueryRowContext(...).Scan(...)
    if errors.Is(err, sql.ErrNoRows) {
        return User{}, fmt.Errorf("...: %w", ErrNotFound)
    }
    if err != nil {
        return User{}, fmt.Errorf("...: %w", err)
    }
    return ..., nil
}
```

Callers of the repo depend on `repo.ErrNotFound`, not `sql.ErrNoRows`. The repo can switch databases, change drivers, or reorganise without affecting consumers.

---

**Q10: Why is `var ErrFoo = fmt.Errorf("foo: %w", io.EOF)` usually a bug?**

**Answer**: It silently makes `ErrFoo` wrap `io.EOF`. Any `errors.Is(ErrFoo, io.EOF)` returns true because the chain contains `io.EOF`. Code that checks `errors.Is(err, io.EOF)` for retry, classification, or stream termination gets triggered for every `ErrFoo` return — usually unintentional.

The maintainer probably copy-pasted the `fmt.Errorf` pattern from a wrap site without realising they were baking a permanent chain into the sentinel.

Fix: declare with `errors.New`. It accepts only a message string and cannot accidentally produce a chain:

```go
var ErrFoo = errors.New("foo")
```

If you want a per-call wrap, do it at the call site:
```go
return fmt.Errorf("operation: %w", err)
```

---

**Q11: How do you declare a sentinel that's "retryable"?**

**Answer**: Two complementary patterns.

**Pattern 1 — bare sentinel as a category**:
```go
var ErrRetryable = errors.New("retryable")

// At the producer:
if isTransient(err) {
    return fmt.Errorf("net: %w", ErrRetryable)
}
```

The producer wraps `ErrRetryable` whenever the failure is transient. Callers check `errors.Is(err, ErrRetryable)`.

**Pattern 2 — `Is` method on a structured error**:
```go
type NetError struct{ Code int }

func (e *NetError) Error() string { return "..." }

func (e *NetError) Is(target error) bool {
    if target == ErrRetryable {
        return e.Code == 429 || (e.Code >= 500 && e.Code < 600)
    }
    return false
}
```

Now `errors.Is(myNetErr, ErrRetryable)` returns true for retryable status codes without an explicit wrap. The `Is` method bridges structured errors and sentinel-style identity.

Both patterns let callers write `if errors.Is(err, ErrRetryable) { backoff(); retry() }`.

---

**Q12: Can you mutate a sentinel after declaration?**

**Answer**: Technically yes (it's a `var`), but doing so silently breaks every comparison. A sentinel's identity is its allocation address. Reassigning the variable to a fresh `errors.New(...)` gives it a new address; existing return values still point at the old address; `errors.Is` between them fails.

```go
var ErrFoo = errors.New("foo")

// Some producer captured this at startup.
saved := ErrFoo

// Later:
ErrFoo = errors.New("changed") // BAD

errors.Is(saved, ErrFoo) // false — different allocations
```

Treat sentinels as effectively immutable. If you need localisation, do it at the presentation layer (translate based on sentinel identity, don't mutate the sentinel).

---

**Q13: How does `errors.Is` work internally?**

**Answer**: `errors.Is(err, target)` walks the unwrap chain of `err` and at each level:

1. Compares the current link with `target` via `==` (if the type is comparable).
2. If the link has an `Is(error) bool` method, calls it; if it returns true, match.
3. Calls `Unwrap()` to advance.
4. Stops when `Unwrap` returns nil or there's no `Unwrap` method.

In Go 1.20+, errors can also have `Unwrap() []error` for multi-wrap; `errors.Is` recursively checks each branch.

The `Is` method on a type lets you express custom equality — e.g., `*os.PathError`'s `Is` method delegates to its embedded syscall error's match against `os.ErrNotExist`.

---

**Q14: What's the cost of `errors.Is`?**

**Answer**: Roughly O(chain depth) pointer comparisons plus a couple of interface-method assertions per level. For a typical chain of depth 2-3, ~5-15 ns. Negligible relative to any I/O or non-trivial computation.

The cost grows linearly with chain depth. Wrapping at every layer of a 5-layer call stack produces a chain of depth 5; `errors.Is` walks it all. This is rarely a hot-path issue but can be worth considering in extremely error-heavy services. The fix is to wrap at fewer boundaries (1-2 layers), not to replace `errors.Is` with `==`.

---

## Senior Level Questions

**Q15: Walk through what `errors.New("foo")` actually does.**

**Answer**:

```go
func New(text string) error {
    return &errorString{text}
}

type errorString struct {
    s string
}

func (e *errorString) Error() string { return e.s }
```

It allocates a new `*errorString` on the heap (one allocation, ~24 bytes including string header), copies the text pointer into the struct, and returns it as an `error` interface value.

Two key consequences:
1. Each call to `errors.New` produces a *distinct* allocation. Two `errors.New("foo")` calls return non-equal values.
2. A package-level `var X = errors.New(...)` produces a single allocation per package per build, used by all comparisons.

This single-allocation discipline is what makes sentinels comparable across the program: every reference to the variable resolves to the same heap address.

---

**Q16: How do `os.PathError` and `os.ErrNotExist` cooperate?**

**Answer**:

`*fs.PathError` (the type returned by `os.Open`, `os.Stat`, etc.) is a structured error:
```go
type PathError struct {
    Op   string
    Path string
    Err  error // typically a syscall.Errno
}
```

It implements:
- `Error() string` — produces the message.
- `Unwrap() error` — returns the inner syscall error.

`os.ErrNotExist` is a sentinel:
```go
var ErrNotExist = fs.ErrNotExist // alias
```

The bridge is the `Is` method on `syscall.Errno` (and on `*PathError` via its `Unwrap`):
- `errors.Is(pathErr, os.ErrNotExist)` walks `*PathError → Unwrap → syscall.Errno`.
- `syscall.Errno.Is(target)` checks if the errno is `ENOENT` (and target is `os.ErrNotExist`); returns true if so.

Result: callers get both data (`pathErr.Path`) via `errors.As` and identity (`os.ErrNotExist`) via `errors.Is`.

This is the canonical "structured-error wraps a sentinel" pattern. The structured error carries data; the sentinel carries identity.

---

**Q17: What changes about sentinels in Go 1.20+?**

**Answer**: Two things:

1. **Multi-error wrapping**: `errors.Join(errs...)` and `fmt.Errorf("...: %w: %w", e1, e2)` create errors with multiple branches. `errors.Is` now recursively checks each branch. A sentinel can match through any branch.

   ```go
   err := fmt.Errorf("ctx: %w; app: %w", context.Canceled, ErrAppFailed)
   errors.Is(err, context.Canceled)  // true
   errors.Is(err, ErrAppFailed)      // true
   ```

2. **`context.Cause(ctx)`**: returns the cancellation reason, which may be a custom sentinel rather than `context.Canceled`. Callers can check `errors.Is(context.Cause(ctx), MyAppErr)`.

The sentinel concept itself is unchanged. The walk algorithm grew to support multi-branch chains.

---

**Q18: Compare sentinels in Go vs error codes in C and exceptions in Java.**

**Answer**:

| Aspect | Go sentinel | C errno | Java exception |
|---|---|---|---|
| Identity | Pointer to `*errorString` | `int` constant | Class type |
| Carry data | Via wrapping or structured types | Via `errno` global only | Via fields |
| Compare cost | Pointer compare or chain walk | Integer compare | `instanceof` |
| Cross-process | Doesn't survive serialisation | Numeric, easy | Class-name + JSON |
| Stack trace | Optional, no built-in | None | Built-in |
| Public API impact | Each sentinel is exported var | Each errno is constant | Each exception is class |

Go sits between C and Java: more structured than `errno`, lighter than full exceptions. The combination of a small sentinel set with `%w`-wrapping context and structured types via `errors.As` covers most use cases.

For service APIs over the wire, Go services typically convert sentinels to gRPC `Status` codes or HTTP status codes — like C-style numeric codes, with a structured Status message attached.

---

**Q19: How would you migrate a sentinel-heavy package to a typed-error design?**

**Answer**: Plan a backward-compatible evolution.

**Step 1**: Keep the existing sentinels.
**Step 2**: Introduce a structured type that wraps the sentinel:

```go
// existing
var ErrNotFound = errors.New("repo: not found")

// new
type NotFoundError struct{ Resource string }
func (e *NotFoundError) Error() string { return e.Resource + " not found" }
func (e *NotFoundError) Unwrap() error { return ErrNotFound }
```

**Step 3**: Producers return the structured type:
```go
return &NotFoundError{Resource: "user"}
```

**Step 4**: Existing callers continue to work because `errors.Is(err, ErrNotFound)` still matches via `Unwrap`. New callers use `errors.As(err, &nfe)` to get the resource.

**Step 5**: Deprecate the bare sentinel returns over time; new producers use the structured type exclusively.

The key trick: the structured type's `Unwrap` keeps the sentinel reachable. Old callers continue to work; new callers gain access to data.

This is the standard backward-compatible migration path.

---

**Q20: How does sentinel matching cross goroutine boundaries?**

**Answer**: It doesn't need to. Sentinels are immutable globals; goroutines simply read them. `errors.Is(err, ErrFoo)` from goroutine A and goroutine B both compare against the same allocation. No synchronisation needed.

The interesting question is about *transmitting errors* across boundaries:
- Channels: an `error` sent on a channel is just a value transfer; identity preserved.
- Mutex-protected stores: same — identity preserved.
- Across an RPC boundary: identity is *not* preserved; you need a code-based protocol (gRPC `Status`).

So: sentinels survive goroutine and process boundaries within a single binary; they do not survive RPC, file serialisation, or shared-library boundaries. Design accordingly.

---

## Scenario-Based Questions

**Q21: A service uses `if err == sql.ErrNoRows` everywhere. After upgrading the database driver, the check fails for some queries but not others. Why?**

**Answer**: The new driver wraps errors in some paths. `==` only matches the bare sentinel; for wrapped versions, it fails. The fix is to migrate every check to `errors.Is(err, sql.ErrNoRows)`, which walks the chain.

This is an extremely common production incident. The fix is mechanical (`errors.Is` replaces `==`) but easy to miss without lint enforcement (`errorlint` configured with `comparison: true` flags every offending site).

---

**Q22: Your team's repository package exports 30 sentinels. A new engineer asks if this is too many. How do you respond?**

**Answer**:

30 is a code smell. A sentinel is part of the public API contract, and 30 means 30 documented conditions, 30 stable identifiers, 30 things callers might check.

Investigate:
1. **Are they truly distinct categories?** Often there are 4-5 real categories with sub-sentinels for variations. Consolidate by promoting the categories to sentinels and using structured fields for variations.
2. **Are some only used internally?** Unexport them.
3. **Could a code-based design be better?** A single `*RepoError` type with a `Code` field is friendlier than 30 sentinels for wide error spaces.
4. **Do callers actually distinguish all 30?** Probably not. The handler likely classifies into 5-6 buckets.

Plan a backward-compatible migration: introduce a `*RepoError` type, alias the old sentinels via its `Unwrap`/`Is` methods, deprecate the bare sentinel returns over time.

---

**Q23: A retry decorator says `if errors.Is(err, ErrRetryable) { retry() }`. The retry fires on `context.Canceled` errors, looping until the context expires. Why?**

**Answer**: Likely a wrapping mistake somewhere. Either:

1. `ErrRetryable` is itself declared as `fmt.Errorf("...: %w", context.Canceled)` — a sentinel that wraps cancellation. `errors.Is(retryable, context.Canceled)` is true; `errors.Is(canceled, retryable)` is also true through the chain.
2. A producer wraps cancellation via `ErrRetryable`: `return fmt.Errorf("...: %w", ErrRetryable)` even when the underlying cause is cancellation.
3. An `Is` method on a structured error returns true for both `ErrRetryable` and `context.Canceled`.

**Fix priority**: cancellation must always abort. Reorder the retry decorator:

```go
if errors.Is(err, context.Canceled) || errors.Is(err, context.DeadlineExceeded) {
    return err // never retry cancellation
}
if errors.Is(err, ErrRetryable) {
    return retry()
}
return err
```

Cancellation checks come first. Then audit the chain construction to find why retryable matched cancellation in the first place.

---

**Q24: You're designing a new package. How do you decide between sentinels, structured errors, and a code enum?**

**Answer**: Use a decision tree.

1. **Does the failure carry per-call data (path, key, ID)?**
   - Yes: structured error.
   - No: continue.
2. **Is the set of failure modes small (3-5) and stable?**
   - Yes: sentinels.
   - No: continue.
3. **Are errors transmitted across processes (RPC, JSON, etc.)?**
   - Yes: code enum (gRPC `Status`, HTTP status, custom code field).
   - No: structured error with a category enum.
4. **Do callers need to extract data AND classify?**
   - Both: structured error wrapping a sentinel, with `Unwrap` for classification and field access for data.

For most local Go libraries, sentinels + structured errors that wrap them is the right combination. For service APIs over the wire, codes are necessary.

---

**Q25: The `errors.Is(err, ErrFoo)` check passes in tests but fails in production. Why?**

**Answer**: A few possibilities:

1. **Different module versions**: tests vendor one version, production vendors another. Each has a distinct `*errorString` allocation. Confirm with `go mod why`.
2. **Build tags or generated code**: a code generator or build-time substitution may produce different sentinels in different builds.
3. **Plugin/shared-library boundaries**: each shared library has its own globals; identity does not cross.
4. **Race condition during init**: extremely rare, but a sentinel reassigned in `init()` (anti-pattern) could be observed pre-mutation in tests and post-mutation in production.

Investigate via:
- `go list -m all` to compare versions.
- A test-prod toggle for build tags.
- `runtime.SetMutexProfileFraction` and similar to detect init races (rare).

Fix: ensure single-version dependency resolution. Avoid sentinel mutation. Avoid plugin boundaries unless absolutely required.

---

## FAQ

**Are sentinels constants?**

No. Go has no constant errors of interface type. They are package-level `var`. By convention you don't reassign them.

---

**Why is `errors.New` preferred over `fmt.Errorf` for sentinels?**

`errors.New` accepts only a string and cannot accidentally create a wrap chain. `fmt.Errorf` can be misused with `%w` to silently produce a sentinel that wraps another error.

---

**Can sentinels carry data?**

Not directly. The sentinel itself is an identifier. Pair it with a structured error type that wraps the sentinel (via `Unwrap`), and put the data on the structured type's fields.

---

**Why use `errors.Is` instead of `==`?**

`errors.Is` walks the unwrap chain. It works against wrapped errors. `==` only matches the outermost value, silently failing when any layer wraps with `%w`. `errorlint` flags `==` against errors as a code smell.

---

**Can you compare two errors created by `errors.New("same text")`?**

No (in the sense of equality). Each `errors.New` call allocates a new value. They are not equal via `==`, even if their messages match. This is why sentinels are *single* allocations exported as variables.

---

**What's the difference between `os.ErrNotExist` and `fs.ErrNotExist`?**

`os.ErrNotExist` is an alias of `fs.ErrNotExist` — they are the same allocation. Either name resolves to the same sentinel. The `os` package re-exports `fs` sentinels for ergonomic reasons.

---

**Why does `errors.Is(err, io.EOF)` return false for my wrapped error?**

Most likely the producer used `%v` or `%s` instead of `%w` somewhere in the chain. `%v` prints the error's text but does NOT preserve the chain. `%w` is required for `errors.Is` to walk through.

---

**Should I document my sentinels?**

Yes. Each sentinel's doc comment should list the conditions under which it is returned. The doc comment is part of the contract; callers depend on those conditions.

---

**What's the relationship between sentinels and `panic`?**

None directly. Sentinels are normal error values returned from functions. `panic` is for unrecoverable conditions; you shouldn't return a sentinel from a recovered panic unless the caller is supposed to handle it via the normal error path.

---

**Should I expose a sentinel for every failure mode?**

No. Reserve sentinels for failure modes the caller will explicitly detect and handle. A "miscellaneous internal" failure doesn't need a sentinel — it's just `error`. Three to five sentinels per package is typical; more than ten is a smell.

---

**Can two sentinels be declared with the same message?**

Yes — they are distinct allocations with distinct identities. The message is for human display; identity is what `errors.Is` compares. So `var ErrA = errors.New("oops")` and `var ErrB = errors.New("oops")` are different sentinels.

---

**Why does the Go community prefer `errors.Is` over `errors.As` when both could work?**

`errors.Is` answers "is this a known category of failure?" with a simple bool. `errors.As` is needed when you want to *extract* data from a structured error. For pure classification, `Is` is faster and simpler.

---

**What's `context.Cause(ctx)` for?**

In Go 1.20+, `context.WithCancelCause(parent)` lets the canceller attach a custom reason. `context.Cause(ctx)` returns that reason, which may be a custom sentinel rather than `context.Canceled`. Useful for distinguishing different sources of cancellation in a fan-out goroutine tree.
