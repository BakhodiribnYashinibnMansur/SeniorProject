# Type Switch — Senior Level

## Internals

### Compile-time

The compiler turns a type switch into a jump table (or a case-by-case if-chain, depending on the number of cases).

```go
switch v := i.(type) {
case int: ...
case string: ...
}
```

Compiled (simplified):
```
1. Read itab type from i
2. Hash → case index
3. Jump to handler
```

### Runtime

The itab type points to the concrete type. Comparison is a pointer compare (fast).

### Performance

```go
func BenchmarkTypeSwitch(b *testing.B) {
    var i any = 42
    for n := 0; n < b.N; n++ {
        switch i.(type) {
        case int: _ = "int"
        case string: _ = "string"
        case bool: _ = "bool"
        }
    }
}
```

Typical: ~1-2 ns/op (compiler-optimized).

### Type switch vs assertion chain

| Method | Speed |
|--------|---------|
| Single assertion | ~1 ns |
| Type switch (3 cases) | ~1-2 ns |
| If-chain (3 assertions) | ~3-4 ns |

For multiple cases the type switch (jump table) is preferred.

---

## Refactoring

### Type switch → interface

```go
// Before
func cost(transport any) float64 {
    switch t := transport.(type) {
    case Car:    return t.Distance * t.FuelPerKM * 1.5
    case Bike:   return 0
    case Train:  return t.Distance * 0.5
    }
    return 0
}

// After
type Transport interface { Cost() float64 }

func (c Car) Cost() float64   { return c.Distance * c.FuelPerKM * 1.5 }
func (Bike) Cost() float64    { return 0 }
func (t Train) Cost() float64 { return t.Distance * 0.5 }

func cost(t Transport) float64 { return t.Cost() }
```

### Type switch as an extension point

```go
// Plugin system
func handle(req any) Response {
    switch r := req.(type) {
    case *LoginReq:    return login(r)
    case *LogoutReq:   return logout(r)
    case *RefreshReq:  return refresh(r)
    default:           return badRequest()
    }
}
```

Adding a new request type means adding a new case to the switch. With a closed set this is fine.

---

## Multi-case Specific Operation

In a multi-case `v` is `any`. If you need a type-specific operation:

### Pattern: Re-assertion

```go
switch v := i.(type) {
case int, int64:
    n, _ := v.(int64)   // ?
    fmt.Println(n)
}
```

Since `v` is `any`, re-assertion works:

```go
switch v := i.(type) {
case int, int64:
    var n int64
    if x, ok := v.(int); ok { n = int64(x) } else { n = v.(int64) }
}
```

The typical solution is to write separate cases:

```go
switch v := i.(type) {
case int:    process(int64(v))
case int64:  process(v)
}
```

---

## When Type Switch is Wrong

### Closed-set polymorphism

```go
type Shape interface { Area() float64 }

func (Circle) Area() float64 { ... }
func (Square) Area() float64 { ... }
```

Use an interface instead of a type switch.

### Open-set extension

When extension is unknown ahead of time, type switch is bad. Adding a new type forces every switch to be updated.

```go
// Bad
switch e := entity.(type) {
case *User: ...
case *Order: ...
}
// If User and Order live outside, the code won't compile until updated
```

---

## Error Handling

```go
err := someOp()
switch e := err.(type) {
case nil:
    return Success
case *NotFoundError:
    return Status404
case *ValidationError:
    return Status400
case net.Error:
    if e.Timeout() { return StatusTimeout }
    return StatusServiceUnavailable
default:
    return StatusInternalError
}
```

`net.Error` is an interface case. Add such cases thoughtfully.

### Wrapped error

A type switch performs a direct match. For wrapped errors use `errors.As`:

```go
var nf *NotFoundError
if errors.As(err, &nf) { ... }
```

Mixing type switch and `errors.As`:

```go
var nf *NotFoundError
if errors.As(err, &nf) {
    return Status404
}

switch err {
case sql.ErrNoRows:    return Status404
case context.Canceled: return StatusCancelled
}
```

---

## Cheat Sheet

```
PERFORMANCE
────────────────────────
Type switch (jump table): ~1-2 ns
Assertion chain (3): ~3-4 ns
If-chain — slower

REFACTORING
────────────────────────
Closed polymorphism → interface
Plugin / extension → switch OK
Open-set extension → interface

MULTI-CASE
────────────────────────
v is any
Type-specific op → separate case

ERROR HANDLING
────────────────────────
Direct error → type switch OK
Wrapped → errors.As
Mixed — pick the right tool

ANTI-PATTERNS
────────────────────────
Type switch on domain entities
Open-set types
Trying to use fallthrough
```

---

## Summary

Type switch at senior level:
- Internals — jump table, ~1-2 ns
- Refactor closed-set → interface
- Plugin / boundary — switch is fine
- Multi-case — `v` is `any`, use a separate case for type-specific ops
- Wrapped error — combine with `errors.As`

A type switch is faster and more consistent than several chained type assertions. But for domain polymorphism, prefer interfaces.
