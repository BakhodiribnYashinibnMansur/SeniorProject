# Type Switch — Optimize

## 1. Performance

```
Type switch (jump table): ~1-2 ns
Assertion chain (3): ~3-4 ns
If-chain bilan: sekinroq
```

## 2. Type switch vs assertion chain

```go
// Yomon
if s, ok := i.(string); ok { ... }
if n, ok := i.(int); ok    { ... }

// Yaxshi
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

Multi-million chaqiruvda type switch ham overhead. Concrete tip-ga refactor.

```go
// Hot path
for _, x := range items {
    switch v := x.(type) {  // har iteratsiyada
    case int: process(v)
    case string: process(v)
    }
}

// Yaxshiroq — homogeneous
items := []int{...}
for _, n := range items { process(n) }
```

## 5. Generics afzal

```go
// Yomon
func Sum(xs []any) int {
    total := 0
    for _, x := range xs {
        switch v := x.(type) {
        case int: total += v
        }
    }
    return total
}

// Yaxshi
func Sum[T int | float64](xs []T) T {
    var total T
    for _, x := range xs { total += x }
    return total
}
```

## 6. Refactor → interface

```go
// Type switch domain-da
switch e := entity.(type) {
case *User: e.Save()
case *Order: e.Save()
}

// Interface afzal
type Saveable interface { Save() error }
saveable.Save()
```

## 7. Cleaner code

### Default qoldiring
```go
default:
    log.Warn("unexpected type", "type", fmt.Sprintf("%T", v))
```

### `case nil` aniq
```go
case nil:
    return ErrNoData
```

### Multi-case mantiqiy
```go
case int, int64, int32:
    // numeric handling
```

### errors.As + type switch aralash
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
Hot path → concrete tip
Generics > switch (boxing yo'q)

ORDER
─────────────────────
Specific case birinchi
Interface case keyin
Default oxirida

DESIGN
─────────────────────
Domain → interface
Boundary → switch
Closed-set → switch OK
Open-set → interface

CLEANER CODE
─────────────────────
Default qoldiring
case nil aniq
Multi-case mantiqiy
errors.As aralash
```

## 9. Summary

Performance:
- Type switch ~1-2 ns — preferred for multi-type cases
- Assertion chain sekinroq
- Hot path → concrete tip
- Generics > switch (boxing yo'q)

Cleaner code:
- Specific birinchi
- Default qoldiring
- Interface domain logikada
- `errors.As` aralash

Type switch — Go-ning closed-set polymorphism vositasi. Boundary, plugin, AST — switch OK. Domain entity-lar — interface afzal.
