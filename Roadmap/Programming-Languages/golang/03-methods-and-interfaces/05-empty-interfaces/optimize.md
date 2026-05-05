# Empty Interfaces — Optimize

## 1. Boxing — heap allocation

```go
var x int = 42
var i any = x   // 42 escapes to the heap
```

`go build -gcflags='-m'` will show this.

## 2. Generics preferred (Go 1.18+)

```go
// any
func Sum(xs []any) int {
    total := 0
    for _, x := range xs { total += x.(int) }
    return total
}

// generics — tezroq
func Sum[T int | float64](xs []T) T {
    var total T
    for _, x := range xs { total += x }
    return total
}
```

Typical speed difference: 2-5x.

## 3. Type assertion overhead

```go
v := i.(int)        // ~1-2 ns
v, ok := i.(int)    // ~1-2 ns (ok bilan)
```

Aksariyat hollarda OK. Hot loop-da concrete tip afzal.

## 4. Reflect — sekin

```go
import "reflect"

t := reflect.TypeOf(x)   // ~10-50 ns
v := reflect.ValueOf(x).Field(0)   // ~50-100 ns
```

Hot path-da ishlatmang. Cache `reflect.Type` agar mumkin bo'lsa.

## 5. Map[any]any vs concrete

```go
// Yomon — har put/get-da boxing
m := map[any]any{}

// Yaxshi — concrete tip
m := map[int]string{}
```

## 6. `[]any` vs `[]T`

```go
// Yomon
items := []any{1, 2, 3}

// Yaxshi (homogeneous)
items := []int{1, 2, 3}
```

## 7. Type switch — multi-assertion-dan tezroq

```go
// Yaxshi
switch v := i.(type) {
case int:    process(v)
case string: process(v)
}

// Yomon (slow)
if v, ok := i.(int); ok { process(v) }
if v, ok := i.(string); ok { process(v) }
```

## 8. Pointer in any — no boxing

```go
var x int = 42
var p *int = &x
var i any = p   // no allocation
```

## 9. Cleaner code

### Boundary `any` OK

```go
func Marshal(v any) ([]byte, error) { ... }
```

### Internal — concrete

```go
func processUser(u User) error { ... }   // not any
```

### Don't `any` everywhere

```go
// Yomon
func Pipeline(input any) any { ... }

// Yaxshi
func Pipeline(input Input) (Output, error) { ... }
```

## 10. Cheat Sheet

```
PERFORMANCE
─────────────────────
Boxing: ~5-10 ns + alloc
Type assertion: ~1-2 ns
Reflect: ~10-100+ ns
Generics: ~0 (no boxing)

CHOICE
─────────────────────
Same algo + types → generics
Heterogeneous → any
Hot path → concrete
JSON dynamic → any (boundary)

CLEANER CODE
─────────────────────
any boundary OK
any domain bad
Type switch > multi-assertion
Pointer in any → no boxing

LINTERS
─────────────────────
forcetypeassert — single-value warning
gocritic interfaceUsage — overuse
```

## 11. Summary

`any` performance:
- Boxing — heap alloc
- Generics tezroq (2-5x)
- Type assertion — ~1-2 ns OK
- Reflect is slow — cache it

Cleaner code:
- Boundary OK, domain core concrete
- Type switch multi-assertion-dan afzal
- Pointer in any — no boxing
- Migration any → generics (selektivno)

`any` is a powerful Go feature, but use it selectively to preserve type-safety and performance.
