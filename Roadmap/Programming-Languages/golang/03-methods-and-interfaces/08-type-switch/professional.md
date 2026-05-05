# Type Switch — Professional Level

## Production Patterns

### Pattern 1: Plugin dispatch

```go
type Request any

func (s *Server) handle(req Request) Response {
    switch r := req.(type) {
    case *LoginRequest:
        return s.login(r)
    case *LogoutRequest:
        return s.logout(r)
    case *RefreshRequest:
        return s.refresh(r)
    default:
        return BadRequest("unknown request")
    }
}
```

A closed set of requests — type switch is fine.

### Pattern 2: Event handler

```go
type Event any

func (h *Handler) Process(e Event) error {
    switch e := e.(type) {
    case OrderPlaced:
        return h.onOrderPlaced(e)
    case PaymentReceived:
        return h.onPaymentReceived(e)
    case OrderCancelled:
        return h.onOrderCancelled(e)
    default:
        h.logger.Warn("unknown event", "type", fmt.Sprintf("%T", e))
        return nil
    }
}
```

### Pattern 3: AST evaluation

```go
type Expr interface { isExpr() }

func eval(e Expr, env Env) (Value, error) {
    switch e := e.(type) {
    case *Lit:    return e.val, nil
    case *Var:    return env.Lookup(e.name)
    case *Binary: return evalBinary(e, env)
    case *Call:   return evalCall(e, env)
    default:      return nil, fmt.Errorf("unknown expr: %T", e)
    }
}
```

### Pattern 4: Error category

```go
func errorCategory(err error) Category {
    var net net.Error
    var perm *fs.PathError

    switch {
    case err == nil:
        return CategoryNone
    case errors.Is(err, context.Canceled):
        return CategoryCancelled
    case errors.As(err, &net) && net.Timeout():
        return CategoryTimeout
    case errors.As(err, &perm):
        return CategoryFileSystem
    default:
        return CategoryUnknown
    }
}
```

The `switch{}` form combined with `errors.As` — wrapped + type switch.

### Pattern 5: Migration helper

```go
// Migrate V1 to V2
func migrate(data any) (V2Format, error) {
    switch v := data.(type) {
    case V1Format:
        return convertV1(v), nil
    case V2Format:
        return v, nil
    default:
        return V2Format{}, fmt.Errorf("unsupported version: %T", v)
    }
}
```

---

## Anti-patterns

### 1. Type switch in domain

```go
// Bad
func processEntity(e any) error {
    switch e := e.(type) {
    case *User:    return e.Save()
    case *Order:   return e.Save()
    case *Product: return e.Save()
    }
    return errors.New("unknown")
}

// Good
type Saveable interface { Save() error }
func processEntity(s Saveable) error { return s.Save() }
```

### 2. Open-set extension

```go
// Bad — every new type forces an update to the switch
func handle(plugin any) {
    switch p := plugin.(type) {
    case *PluginA: ...
    case *PluginB: ...
    // Adding a new plugin requires updating this code
    }
}

// Good
type Plugin interface { Run() error }
func handle(p Plugin) error { return p.Run() }
```

### 3. Type switch instead of `errors.Is`

```go
// Bad — direct
switch err {
case sql.ErrNoRows: return NotFound
}

// Good — wrapped-aware
if errors.Is(err, sql.ErrNoRows) { return NotFound }
```

---

## Library API Design

### Hide the type switch

```go
// Public API — typed
type Status int
const ( OK Status = iota; NotFound; Forbidden; Error )

func GetStatus(err error) Status {
    switch err.(type) {
    case nil: return OK
    case *NotFoundError: return NotFound
    case *ForbiddenError: return Forbidden
    default: return Error
    }
}

// The caller only sees Status; the type switch stays internal
```

### Document accepted types

```go
// Process handles one of: *LoginRequest, *LogoutRequest, *RefreshRequest.
// Returns ErrUnknownRequest if request type is not recognized.
func (s *Server) Process(req any) (Response, error) { ... }
```

---

## Testing

### Test all branches

```go
func TestHandle(t *testing.T) {
    cases := []struct {
        name string
        req  Request
        want int
    }{
        {"login", &LoginRequest{}, 200},
        {"logout", &LogoutRequest{}, 200},
        {"unknown", &UnknownRequest{}, 400},
    }
    for _, c := range cases {
        t.Run(c.name, func(t *testing.T) {
            got := handle(c.req).Status
            if got != c.want { t.Errorf("got %d, want %d", got, c.want) }
        })
    }
}
```

Cover every case of the type switch.

### Default case warning

```go
default:
    log.Warn("unexpected type", "type", fmt.Sprintf("%T", v))
```

In production, log unexpected types and provide a fallback.

---

## Cheat Sheet

```
PRODUCTION PATTERNS
────────────────────────
Plugin / request dispatch
Event handler
AST evaluation
Error categorization
Migration helper

ANTI-PATTERNS
────────────────────────
Type switch on domain entities
Open-set extension
Type switch instead of errors.Is

LIBRARY DESIGN
────────────────────────
Hide type switch behind typed return
Document accepted types
errors.As wrapped-aware

TESTING
────────────────────────
All branches
Default case logging
```

---

## Summary

Professional type switch:
- Production patterns — plugin dispatch, event handler, AST, error category
- Anti-patterns — in domain logic, for open-set extension
- Library design — hide behind a typed return
- Testing — all branches + default
- Use `errors.As` for wrapped errors

Type switch fits closed-set polymorphism. For open sets, use an interface. Fine at boundaries, not in domain code.
