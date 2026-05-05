# Type Switch — Interview Questions

## Junior

### Q1: Type switch syntax?
**Answer:** `switch v := i.(type) { case T1: ...; case T2: ...; default: ... }`.

### Q2: What type does `v` have in a multi-case?
**Answer:** `any` (no common type inferred).

### Q3: When does `case nil` match?
**Answer:** When the interface value is truly nil.

### Q4: Does `fallthrough` work in a type switch?
**Answer:** No — compile error.

### Q5: Difference between type switch and type assertion?
**Answer:**
- Assertion — single type
- Switch — multiple types + optimized

---

## Middle

### Q6: Are interface cases allowed?
**Answer:** Yes. The case matches when the dynamic type satisfies the interface.

### Q7: Type of `v` in `case T1, T2`?
**Answer:** Static type of i (typically `any`).

### Q8: Type switch performance?
**Answer:** ~1-2 ns. Compiler-generated jump table.

### Q9: Are wrapped errors found by a type switch?
**Answer:** No — direct match only. Use `errors.As`.

### Q10: When is choosing an interface preferable to a type switch?
**Answer:** Closed-set polymorphism → interface. Open-set or boundary code → switch.

---

## Senior

### Q11: Type switch vs assertion-chain performance?
**Answer:** Switch ~1-2 ns, chain ~3-4 ns (3 cases). The switch uses an optimized jump table.

### Q12: With a nil concrete value inside an interface, does `case nil` match?
**Answer:** No. Type info is present — the `case T` (concrete type) matches.

### Q13: Type switch with generics?
**Answer:** `any(v).(type)` — convert the generic value to `any` first.

### Q14: When to use polymorphism instead of a type switch?
**Answer:** In domain logic — prefer interfaces. At boundaries (HTTP, JSON) — switch is fine.

### Q15: Does order matter?
**Answer:** For most cases — no. But when an interface case is involved, list specific cases first and general ones afterward.

---

## Tricky

### Q16: What does this print?
```go
var p *int = nil
var i any = p
switch i.(type) {
case nil:    fmt.Println("nil")
case *int:   fmt.Println("*int")
}
```
**Answer:** `*int`. Type info is present.

### Q17: What does this print?
```go
var i any = struct{}{}
switch i.(type) {
case nil: ...
case struct{}: ...
default: ...
}
```
**Answer:** `case struct{}` matches.

### Q18: Does this compile?
```go
switch v := i.(type) {
case int, int64:
    return v + 1
}
```
**Answer:** No. `v` is `any` in a multi-case. `+ 1` does not work.

### Q19: What if we swap the order of `case io.Reader` and `case *os.File`?
**Answer:** `*os.File` satisfies Reader. If Reader comes first, it always matches that case. List specific cases first.

### Q20: Switch inside a type switch?
**Answer:** Allowed, but the flow becomes complex. Refactor.

---

## Coding Tasks

### Task 1: Describe

```go
func describe(i any) string {
    switch v := i.(type) {
    case nil:    return "nil"
    case int:    return fmt.Sprintf("int:%d", v)
    case string: return fmt.Sprintf("str:%s", v)
    case bool:   return fmt.Sprintf("bool:%v", v)
    default:     return fmt.Sprintf("%T", v)
    }
}
```

### Task 2: JSON walker

```go
func walk(v any) {
    switch v := v.(type) {
    case map[string]any:
        for k, val := range v { fmt.Println(k); walk(val) }
    case []any:
        for _, val := range v { walk(val) }
    default:
        fmt.Println(v)
    }
}
```

### Task 3: Visitor

```go
type Expr interface { isExpr() }
type Num struct{ val int }
type Add struct{ a, b Expr }

func (Num) isExpr() {}
func (Add) isExpr() {}

func eval(e Expr) int {
    switch e := e.(type) {
    case Num: return e.val
    case Add: return eval(e.a) + eval(e.b)
    }
    return 0
}
```

### Task 4: Error category

```go
func category(err error) string {
    switch err.(type) {
    case nil: return "ok"
    case *NotFound: return "404"
    case *Validation: return "400"
    default: return "500"
    }
}
```

### Task 5: Polymorphism refactor

```go
// Type switch
switch e := entity.(type) {
case *User: e.Save()
case *Order: e.Save()
}

// Interface
type Saveable interface { Save() error }
saveable.Save()
```
