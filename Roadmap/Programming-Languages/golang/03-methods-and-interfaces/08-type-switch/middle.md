# Type Switch — Middle Level

## Common Patterns

### Pattern 1: Visitor

```go
type Expr interface { isExpr() }

type Num struct { val int }
type Add struct { left, right Expr }
type Mul struct { left, right Expr }

func (Num) isExpr() {}
func (Add) isExpr() {}
func (Mul) isExpr() {}

func eval(e Expr) int {
    switch e := e.(type) {
    case Num:
        return e.val
    case Add:
        return eval(e.left) + eval(e.right)
    case Mul:
        return eval(e.left) * eval(e.right)
    }
    return 0
}
```

### Pattern 2: AST traversal

```go
type Node any

func walk(n Node, fn func(Node)) {
    fn(n)
    switch n := n.(type) {
    case *Block:
        for _, child := range n.statements { walk(child, fn) }
    case *If:
        walk(n.cond, fn)
        walk(n.then, fn)
        if n.else_ != nil { walk(n.else_, fn) }
    }
}
```

### Pattern 3: Error dispatcher

```go
type AppError interface { error; Code() int }

type NotFound struct{ ID string }
func (NotFound) Code() int { return 404 }
func (e *NotFound) Error() string { return "not found: " + e.ID }

type Validation struct{ Field string }
func (Validation) Code() int { return 400 }
func (e *Validation) Error() string { return "invalid: " + e.Field }

func handle(err error) int {
    switch e := err.(type) {
    case *NotFound:
        return e.Code()
    case *Validation:
        return e.Code()
    case nil:
        return 200
    default:
        return 500
    }
}
```

### Pattern 4: JSON value walker

```go
func walk(v any) {
    switch v := v.(type) {
    case map[string]any:
        for k, val := range v {
            fmt.Printf("%s: ", k)
            walk(val)
        }
    case []any:
        for _, val := range v { walk(val) }
    case string:
        fmt.Println(strconv.Quote(v))
    case float64:
        fmt.Println(v)
    case bool:
        fmt.Println(v)
    case nil:
        fmt.Println("null")
    }
}
```

### Pattern 5: Reflection alternative

```go
// Faster than reflect for known types
func size(v any) int {
    switch v := v.(type) {
    case int, int64: return 8
    case int32:      return 4
    case string:     return len(v)
    case []byte:     return len(v)
    default:         return -1
    }
}
```

---

## Multi-case Behavior

### `v` retains `any`

```go
switch v := i.(type) {
case int, int64:
    // v is any
    // v + 1  // compile error
}
```

If you need a type-specific operation, use a separate case or re-assert:

```go
switch v := i.(type) {
case int:
    return v + 1
case int64:
    return int(v) + 1
}
```

### No fallthrough

```go
switch i.(type) {
case int:
    fmt.Println("int")
    fallthrough  // COMPILE ERROR
case string:
    fmt.Println("string")
}
```

Type switch does not allow fallthrough. Compose with multi-case instead.

---

## Type Switch vs Polymorphism

### Bad — type switch in domain

```go
func processEntity(e any) {
    switch e := e.(type) {
    case *User:    saveUser(e)
    case *Order:   saveOrder(e)
    case *Product: saveProduct(e)
    }
}
```

### Good — interface

```go
type Saveable interface { Save() error }
func processEntity(s Saveable) error { return s.Save() }
```

Type switch is an escape hatch (used at boundaries). Polymorphism belongs to interfaces.

---

## Type Switch with Generics

```go
func Process[T any](v T) string {
    switch any(v).(type) {
    case int: return "int"
    case string: return "string"
    default: return fmt.Sprintf("%T", v)
    }
}
```

`any(v)` converts a generic value to `any` so it can be used in a type switch.

(In a Go 1.18+ generics context.)

---

## Edge Cases

### nil concrete in interface

```go
var p *T = nil
var i any = p

switch i.(type) {
case nil:
    fmt.Println("nil interface")  // NOT this one
case *T:
    fmt.Println("*T")              // this one
}
```

### Interface assertion in case

```go
switch v := i.(type) {
case io.Reader:
    // v is io.Reader (interface)
    v.Read(p)
}
```

A type switch also accepts interface types in cases.

### Nested type switch

```go
switch v := outer.(type) {
case *Container:
    switch inner := v.value.(type) {
    case int: ...
    }
}
```

Allowed, but the flow becomes harder to follow.

---

## Best Practices

### 1. Add a default

```go
switch v := i.(type) {
case int: ...
case string: ...
default:
    return fmt.Errorf("unexpected type: %T", v)
}
```

### 2. Does order matter?

No — each case in a type switch is compared against its own type independently. Order has no performance impact, but a sensible order helps readability.

### 3. Refactor: domain → interface

```go
// Type switch in domain logic → interface
type Saveable interface { Save() error }
```

### 4. Type switch at the boundary is fine

```go
// Result of JSON unmarshal
var data any
json.Unmarshal(payload, &data)
walk(data)   // type switch at the boundary
```

---

## Cheat Sheet

```
PATTERNS
────────────────────────
Visitor (AST eval)
Error dispatcher
JSON walker
Reflection alternative
Multi-case (no operation, just match)

MULTI-CASE
────────────────────────
v is any
For type-specific op — use a separate case

POLYMORPHISM
────────────────────────
Domain → prefer interface
Boundary → type switch is fine

EDGE CASES
────────────────────────
nil concrete in interface → matches case T (not nil)
fallthrough — not allowed
Interface case — OK
Nested switch — OK
```

---

## Summary

Type switch at middle level:
- Patterns: visitor, error dispatcher, JSON walker
- Multi-case — `v` is `any`
- For domain polymorphism — prefer interfaces
- At boundaries — type switch is fine
- With generics — `any(v).(type)`
