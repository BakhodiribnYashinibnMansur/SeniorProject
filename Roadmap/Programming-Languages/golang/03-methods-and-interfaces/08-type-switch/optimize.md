# Type Switch — Optimize

## 1. Performance

```
Type switch (jump table): ~1-2 ns
Assertion chain (3): ~3-4 ns
With an if-chain: slower
```

## 2. Type switch vs assertion chain

```go
// Bad
if s, ok := i.(string); ok { ... }
if n, ok := i.(int); ok    { ... }

// Good
switch v := i.(type) {
case string: ...
case int: ...
}
```

Type switch — compiler jump table.

## 3. Interface case order

```go
switch v := i.(type) {
case fmt.Stringer:   // general case first — matches everything
case *MyType:        // never reached
}
```

Place specific cases first.

## 4. Hot path optimization

Across multi-million calls even a type switch adds overhead. Refactor to a concrete type.

```go
// Hot path
for _, x := range items {
    switch v := x.(type) {  // on every iteration
    case int: process(v)
    case string: process(v)
    }
}

// Better — homogeneous
items := []int{...}
for _, n := range items { process(n) }
```

## 5. Generics are preferred

```go
// Bad
func Sum(xs []any) int {
    total := 0
    for _, x := range xs {
        switch v := x.(type) {
        case int: total += v
        }
    }
    return total
}

// Good
func Sum[T int | float64](xs []T) T {
    var total T
    for _, x := range xs { total += x }
    return total
}
```

## 6. Refactor → interface

```go
// Type switch in the domain
switch e := entity.(type) {
case *User: e.Save()
case *Order: e.Save()
}

// Interface is preferred
type Saveable interface { Save() error }
saveable.Save()
```

## 7. Cleaner code

### Always include `default`
```go
default:
    log.Warn("unexpected type", "type", fmt.Sprintf("%T", v))
```

### Make `case nil` explicit
```go
case nil:
    return ErrNoData
```

### Multi-case when logical
```go
case int, int64, int32:
    // numeric handling
```

### Mix `errors.As` with a type switch
```go
var nf *NotFound
if errors.As(err, &nf) { return Status404 }

switch err.(type) {
case nil: return Status200
case context.Canceled: return StatusCancelled
}
```

## 8. Cheat Sheet

```
PERFORMANCE
─────────────────────
Type switch: ~1-2 ns
Assertion chain: ~3-4 ns (3 case)
Hot path → concrete type
Generics > switch (no boxing)

ORDER
─────────────────────
Specific case first
Interface case after
Default last

DESIGN
─────────────────────
Domain → interface
Boundary → switch
Closed-set → switch OK
Open-set → interface

CLEANER CODE
─────────────────────
Always include default
Make case nil explicit
Multi-case when logical
Mix with errors.As
```

## 9. Summary

Performance:
- Type switch ~1-2 ns — preferred for multi-type cases
- Assertion chain is slower
- Hot path → concrete type
- Generics > switch (no boxing)

Cleaner code:
- Specific cases first
- Always include default
- Use interfaces in domain logic
- Mix with `errors.As`

A type switch is Go's tool for closed-set polymorphism. Boundary, plugin, AST — switch is fine. For domain entities, interfaces are preferred.
