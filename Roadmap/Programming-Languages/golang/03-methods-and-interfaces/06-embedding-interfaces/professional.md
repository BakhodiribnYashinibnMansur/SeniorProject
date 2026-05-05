# Embedding Interfaces — Professional Level

## Library API Design

### Standard library uslubi

```go
package io

type Reader interface { Read([]byte) (int, error) }
type Writer interface { Write([]byte) (int, error) }
type Closer interface { Close() error }

// Convenience compositions
type ReadCloser interface { Reader; Closer }
type WriteCloser interface { Writer; Closer }
type ReadWriter interface { Reader; Writer }
type ReadWriteCloser interface { Reader; Writer; Closer }
```

Bu — eng yaxshi misol. Atomic interfaces + convenience composition.

### Versioning

| Change | Breaking? |
|--------|-----------|
| Add method to atomic interface | BREAKING (all implementations) |
| Add embed to composition interface | BREAKING |
| Create new composition interface | Non-breaking |
| Remove atomic interface | BREAKING |

### Soft migration

```go
// v1
type Reader interface { Read(...) ... }

// v1.5 — yangi optional capability
type AvailableReader interface {
    Reader
    Available() int
}

// If a caller asks for AvailableReader, the new capability is required
// Otherwise, falls back to Reader
```

---

## DDD Layer Composition

### Repository layer

```go
type UserReader interface {
    Find(ctx context.Context, id UserID) (*User, error)
    FindByEmail(ctx context.Context, email Email) (*User, error)
}

type UserWriter interface {
    Save(ctx context.Context, u *User) error
    Delete(ctx context.Context, id UserID) error
}

type UserRepository interface {
    UserReader
    UserWriter
}
```

Use cases request only the capability they need:

```go
type ListUsersUseCase struct{ reader UserReader }
type CreateUserUseCase struct{ writer UserWriter }
type ManageUserUseCase struct{ repo UserRepository }
```

Read-only use case — `UserReader` yetarli (mock yozish ham oson).

### Service composition

```go
type Notifier interface { Notify(to, msg string) error }
type Tracker interface { Track(event string) }

type NotificationService interface {
    Notifier
    Tracker
}
```

Service has multiple concerns — composition via embedding.

---

## Mocking with Embedding

### Partial mock

```go
type Repo interface {
    Find(id string) (*User, error)
    Save(u *User) error
    Delete(id string) error
}

type PartialMock struct{ Repo }   // embed real or no-op
func (m *PartialMock) Find(id string) (*User, error) {
    return &User{ID: id}, nil  // override
}
// Save, Delete delegate
```

### No-op base + selective override

```go
type NoOpRepo struct{}
func (NoOpRepo) Find(id string) (*User, error) { return nil, nil }
func (NoOpRepo) Save(u *User) error            { return nil }
func (NoOpRepo) Delete(id string) error        { return nil }

type FindMock struct{ NoOpRepo }
func (m *FindMock) Find(id string) (*User, error) {
    return &User{ID: id}, nil
}
```

---

## Production Patterns

### Pattern 1: Pluggable middleware

```go
type Handler interface { Handle(req Request) Response }

type Middleware interface {
    Wrap(next Handler) Handler
}

// Middleware decorate qilishi mumkin
type LoggingMiddleware struct{}
func (LoggingMiddleware) Wrap(next Handler) Handler { ... }
```

### Pattern 2: Capability-based access

```go
type Readable interface { Read(id string) (Item, error) }
type Writable interface { Write(item Item) error }
type Deletable interface { Delete(id string) error }

func GuardedAccess[T Readable](store T, allowed []string) Readable { ... }
```

User capability-iga ko'ra interface granularity orqali tanlash.

### Pattern 3: Phased migration

```go
type V1Service interface { OldMethod() }
type V2Service interface {
    V1Service
    NewMethod()
}

// Callers use V2Service; V1Service implementations remain OK
```

---

## Documentation Standards

### Interface kontrakti

```go
// Reader is the interface that wraps the basic Read method.
//
// Read reads up to len(p) bytes into p. It returns the number
// of bytes read (0 <= n <= len(p)) and any error encountered.
type Reader interface { ... }

// Closer is the interface that wraps the basic Close method.
//
// The behavior of Close after the first call is undefined.
// Specific implementations may document their own behavior.
type Closer interface { ... }

// ReadCloser is the interface that groups the basic Read
// and Close methods.
type ReadCloser interface {
    Reader
    Closer
}
```

### Why-comment

```go
// AuditedRepo combines repository operations with audit logging.
//
// Embedding ensures that all repo capabilities are exposed,
// while the audit middleware adds compliance logging.
type AuditedRepo struct {
    Repo                // embed
    auditLog AuditLog
}
```

---

## Linter Rules

### `revive`
- **interface-naming** — `-er` suffix tavsiya
- **embedding** — finds direct embeds

### `staticcheck`
- Implicit method conflict warnings

### `gocritic`
- **interfaceUsage** — overuse warnings

### Custom
- Unrelated embed warning (project-specific)

---

## Cheat Sheet

```
LIBRARY DESIGN
─────────────────────────
Atomic interface birinchi
Convenience composition
-er suffix
Documentation kontrakti

DDD COMPOSITION
─────────────────────────
Reader + Writer = Repository
Use case minimal interface so'rasin
Read-only mock oson

MOCKING
─────────────────────────
NoOp base + override
Partial via embed
Real + selective override

VERSIONING
─────────────────────────
Adding a method to an atomic interface → BREAKING
Yangi composition → soft
Soft migration: yangi optional interface

DOCUMENTATION
─────────────────────────
Kontrakt
Concurrency safety
Sentinel errors
Embed sababi
```

---

## Summary

Professional embedding:
- Library: atomic + convenience composition
- DDD: layered repository, capability-based
- Mocking: NoOp + override, partial mock
- Versioning: yangi optional interface, soft migration
- Documentation: kontrakt + concurrency safety
- Linters: -er suffix, overuse warning

Embedding is Go's powerful composition mechanism. The standard library style (`io`) is a clear model. Granular atomic interfaces combined with convenience composition produce a stable, extensible design.
