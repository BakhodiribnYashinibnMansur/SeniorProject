# Type Switch — Specification

> Source: [Go Language Specification](https://go.dev/ref/spec) — §Type_switches

---

## 1. Spec Reference

> A type switch compares types rather than values. It is otherwise similar to
> an expression switch. It is marked by a special switch expression that has
> the form of a type assertion using the keyword `type` rather than an actual
> type:
>
> ```go
> switch i.(type) {
> case T1:
>     // i's dynamic type is T1
> case T2:
>     ...
> default:
>     // i's dynamic type is none of T1, T2, ...
> }
> ```

Source: https://go.dev/ref/spec#Type_switches

---

## 2. Formal Grammar

```ebnf
TypeSwitchStmt  = "switch" [ SimpleStmt ";" ] TypeSwitchGuard "{" { TypeCaseClause } "}" .
TypeSwitchGuard = [ identifier ":=" ] PrimaryExpr "." "(" "type" ")" .
TypeCaseClause  = TypeSwitchCase ":" StatementList .
TypeSwitchCase  = "case" TypeList | "default" .
TypeList        = Type { "," Type } .
```

---

## 3. Core Rules

### Rule 1: Operand must be interface

```go
var i any = 42
switch i.(type) { ... }   // OK

var n int = 42
// switch n.(type) { ... }  // illegal — not interface
```

### Rule 2: Optional binding

```go
switch v := i.(type) { ... }   // v binding
switch i.(type) { ... }        // no binding
```

If `v` declared:
- In single-type case — `v` is that type
- In multi-type case — `v` retains static type of i (any)
- In default — `v` retains static type of i

### Rule 3: nil case

```go
switch i.(type) {
case nil: // i is nil interface
}
```

`nil` is valid case label only in type switches.

### Rule 4: No fallthrough

```go
switch i.(type) {
case int:
    // ...
    fallthrough  // ILLEGAL
}
```

Type switches do not allow `fallthrough`.

### Rule 5: Default optional

`default` clause is optional. If no case matches and no default — no action.

### Rule 6: Type list (multi-type case)

```go
case T1, T2, T3:
    // v retains type any (or interface type of switch operand)
```

When multiple types listed, `v` keeps the static type.

---

## 4. Type Comparison

For each case, the dynamic type of i is compared with the listed type(s).
Match condition:
- Same concrete type
- Or i's dynamic type satisfies the case's interface type

---

## 5. Defined vs Undefined Behavior

### Defined

| Operation | Behavior |
|-----------|---------|
| `switch v := i.(type)` | Type switch with binding |
| `case T:` | Match if dynamic type is T |
| `case T1, T2:` | Match if dynamic type is T1 or T2 |
| `case nil:` | Match if i is nil interface |
| `default:` | Match if no other case |

### Illegal

| Operation | Result |
|-----------|--------|
| `fallthrough` in type switch | Compile error |
| Type switch on non-interface | Compile error |
| Duplicate case types | Compile error |

---

## 6. Edge Cases

### Edge Case 1: nil concrete in interface

```go
var p *T = nil
var i any = p

switch i.(type) {
case nil:    // NOT MATCHED — i has type *T
case *T:     // MATCHED
}
```

### Edge Case 2: Interface case

```go
type Stringer interface { String() string }

switch v := i.(type) {
case Stringer:
    // v is Stringer (interface)
    fmt.Println(v.String())
}
```

Interface case is allowed. Match if dynamic type satisfies interface.

### Edge Case 3: Multiple matching types

```go
type S struct{}
func (S) String() string { return "S" }

var i any = S{}
switch i.(type) {
case fmt.Stringer:
    // matched first
case S:
    // never matched (Stringer above)
}
```

First matching case wins. Order matters for interface cases.

### Edge Case 4: Generics constraint

```go
func Process[T any](v T) {
    switch any(v).(type) {
    case int: ...
    case string: ...
    }
}
```

Convert generic type parameter to `any` for type switch.

---

## 7. Version History

| Version | Change |
|---------|--------|
| Go 1.0 | Type switches introduced. |
| Go 1.18 | Generics — type switch on generic types via `any()` conversion. |

---

## 8. Compliance Checklist

- [ ] Operand is interface type.
- [ ] Optional `v` binding declared if used in body.
- [ ] No `fallthrough` in type switch.
- [ ] No duplicate case types.
- [ ] `nil` case for true nil interface only.
- [ ] Interface cases ordered correctly (specific before general).

---

## 9. Official Examples

### Basic

```go
switch v := i.(type) {
case int:
    fmt.Printf("int: %d\n", v)
case string:
    fmt.Printf("string: %s\n", v)
case nil:
    fmt.Println("nil")
default:
    fmt.Printf("type: %T\n", v)
}
```

### Multi-type

```go
switch v := i.(type) {
case int, int64:
    // v is interface{}
    fmt.Println("integer-like:", v)
}
```

### Interface case

```go
switch v := i.(type) {
case io.Reader:
    p := make([]byte, 10)
    v.Read(p)
case io.Writer:
    v.Write([]byte("hi"))
}
```

---

## 10. Related Spec Sections

| Section | URL |
|---------|-----|
| Type switches | https://go.dev/ref/spec#Type_switches |
| Type assertions | https://go.dev/ref/spec#Type_assertions |
| Switch statements | https://go.dev/ref/spec#Switch_statements |
| Interface types | https://go.dev/ref/spec#Interface_types |
| Method sets | https://go.dev/ref/spec#Method_sets |
