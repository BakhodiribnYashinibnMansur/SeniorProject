# Type Switch — Junior Level

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Glossary](#glossary)
4. [Core Concepts](#core-concepts)
5. [Real-World Analogies](#real-world-analogies)
6. [Mental Models](#mental-models)
7. [Pros & Cons](#pros--cons)
8. [Use Cases](#use-cases)
9. [Code Examples](#code-examples)
10. [Coding Patterns](#coding-patterns)
11. [Clean Code](#clean-code)
12. [Best Practices](#best-practices)
13. [Edge Cases & Pitfalls](#edge-cases--pitfalls)
14. [Common Mistakes](#common-mistakes)
15. [Common Misconceptions](#common-misconceptions)
16. [Test](#test)
17. [Tricky Questions](#tricky-questions)
18. [Cheat Sheet](#cheat-sheet)
19. [Summary](#summary)
20. [Diagrams](#diagrams)

---

## Introduction

**Type switch** is a tool for checking the concrete type of an interface value across multiple alternatives. Syntax:

```go
switch v := i.(type) {
case T1:
    // v is T1
case T2:
    // v is T2
default:
    // v has i's dynamic type
}
```

A type switch is a special form of the `switch` statement. The `i.(type)` form is allowed only inside a type switch (it is not a regular interface assertion syntax).

```go
func describe(i any) {
    switch v := i.(type) {
    case int:
        fmt.Println("int:", v)
    case string:
        fmt.Println("string:", v)
    case bool:
        fmt.Println("bool:", v)
    case nil:
        fmt.Println("nil")
    default:
        fmt.Printf("unknown: %T\n", v)
    }
}
```

A type switch is faster and more readable than several chained type assertions.

After this file you will:
- Know the type switch syntax
- Know what type `v` has in each case
- Understand the `nil` case
- Know when to choose type switch vs type assertion

---

## Prerequisites
- Type assertion basics
- Interface basics
- `switch` statement

---

## Glossary

| Term | Definition |
|--------|--------|
| **Type switch** | `switch v := i.(type) { ... }` — switch by type |
| **Type assertion** | `i.(T)` — extract concrete type from interface |
| **`case nil`** | Checks whether the interface value is nil |
| **`default`** | Runs when no case matches |
| **`v`** | Inside the type switch — typed according to the dynamic type |

---

## Core Concepts

### 1. Syntax

```go
switch v := i.(type) {
case T1:    // v is T1
case T2:    // v is T2
case nil:   // i is nil interface
default:    // v is i's dynamic type
}
```

### 2. `v` has a specific type in each case

```go
var i any = 42

switch v := i.(type) {
case int:
    fmt.Println(v + 1)   // v is int, arithmetic OK
case string:
    fmt.Println(v + "!")  // v is string, concat OK
}
```

### 3. Multi-case

```go
switch v := i.(type) {
case int, int64:
    fmt.Println(v)   // v is any (no common type inferred)
case string, []byte:
    fmt.Println(v)
}
```

In a multi-case clause `v` has type `any`. If you need a specific type, put each type in its own case.

### 4. `case nil`

```go
switch i.(type) {
case nil:
    fmt.Println("nil interface")
case int:
    fmt.Println("int")
}
```

The `nil` case fires when the interface value is truly nil.

### 5. `default`

```go
switch v := i.(type) {
case int: ...
case string: ...
default:
    fmt.Printf("type: %T, value: %v\n", v, v)
}
```

`default` runs when no case matches. `v` holds i's dynamic type.

---

## Real-World Analogies

**Analogy 1 — Sports car catalog**

Asking a sports car "what kind of engine?" Options: V8, V12, hybrid. Each gets its own description.

**Analogy 2 — Mail label**

Opening an envelope: "Is this a book, document, or package?" Each is handled differently.

**Analogy 3 — Restaurant pickup**

Restaurant orders: "Pizza, salad, soup, or dessert?" Each has its own pickup flow.

---

## Mental Models

### Model 1: Switch with type query

```
i — interface value
↓
switch i.(type) — "what is inside i?"
↓
case T1: ...
case T2: ...
default: ...
```

### Model 2: `v` is adaptive

```
case int:    v is int
case string: v is string
default:     v is i's dynamic type (any)
```

### Model 3: Compiler optimization

A type switch compiles to a jump table. Fast hash-based dispatch.

---

## Pros & Cons

| Pros | Cons |
|------|------|
| Fast checking of multiple types | Syntax is tricky for beginners |
| Compiler-optimized | A switch with many cases often signals a design flaw |
| `v` has a specific type in each case | A `nil` case may be required |
| Type-safe | Multi-case keeps `v` as `any` |

---

## Use Cases

### Use case 1: Heterogeneous data

```go
items := []any{1, "two", 3.0, true}
for _, item := range items {
    switch v := item.(type) {
    case int:    fmt.Println("int:", v)
    case string: fmt.Println("string:", v)
    case float64: fmt.Println("float:", v)
    case bool:   fmt.Println("bool:", v)
    }
}
```

### Use case 2: JSON dynamic exploration

```go
func explore(v any) {
    switch v := v.(type) {
    case map[string]any:
        for k, val := range v { fmt.Println(k); explore(val) }
    case []any:
        for _, val := range v { explore(val) }
    case string, float64, bool, nil:
        fmt.Println(v)
    }
}
```

### Use case 3: AST traversal

```go
type Node any

func eval(n Node) int {
    switch n := n.(type) {
    case Num:
        return n.value
    case BinaryOp:
        return eval(n.left) + eval(n.right)  // example
    }
    return 0
}
```

### Use case 4: Error inspection

```go
switch e := err.(type) {
case *NotFoundError:
    return Status404
case *ValidationError:
    return Status400
case nil:
    return Status200
default:
    return Status500
}
```

---

## Code Examples

### Example 1: Simple

```go
package main

import "fmt"

func describe(i any) {
    switch v := i.(type) {
    case int:    fmt.Println("int:", v)
    case string: fmt.Println("string:", v)
    case bool:   fmt.Println("bool:", v)
    case nil:    fmt.Println("nil")
    default:     fmt.Printf("unknown: %T\n", v)
    }
}

func main() {
    describe(42)        // int: 42
    describe("hello")   // string: hello
    describe(true)      // bool: true
    describe(nil)       // nil
    describe(3.14)      // unknown: float64
}
```

### Example 2: Multi-case

```go
package main

import "fmt"

func describe(i any) {
    switch v := i.(type) {
    case int, int64:
        // v is any (common type not deduced)
        fmt.Println("integer-like:", v)
    case string, []byte:
        fmt.Println("text-like:", v)
    default:
        fmt.Println("other")
    }
}
```

### Example 3: JSON traversal

```go
package main

import (
    "encoding/json"
    "fmt"
)

func walk(v any, depth int) {
    switch v := v.(type) {
    case map[string]any:
        for k, val := range v {
            fmt.Printf("%*s%s:\n", depth*2, "", k)
            walk(val, depth+1)
        }
    case []any:
        for i, val := range v {
            fmt.Printf("%*s[%d]:\n", depth*2, "", i)
            walk(val, depth+1)
        }
    default:
        fmt.Printf("%*s%v\n", depth*2, "", v)
    }
}

func main() {
    var data any
    json.Unmarshal([]byte(`{"name":"Alice","tags":["a","b"]}`), &data)
    walk(data, 0)
}
```

### Example 4: Custom error

```go
package main

import "fmt"

type NotFound struct{ ID string }
type Validation struct{ Field string }

func (e *NotFound) Error() string   { return "not found" }
func (e *Validation) Error() string { return "validation" }

func handle(err error) string {
    switch e := err.(type) {
    case *NotFound:
        return "NF: " + e.ID
    case *Validation:
        return "VAL: " + e.Field
    case nil:
        return "OK"
    default:
        return "ERR: " + err.Error()
    }
}

func main() {
    fmt.Println(handle(&NotFound{ID: "u1"}))
    fmt.Println(handle(&Validation{Field: "email"}))
    fmt.Println(handle(nil))
}
```

### Example 5: Pointer vs value

```go
type T struct{ v int }

var i any = &T{v: 5}

switch x := i.(type) {
case *T:
    fmt.Println("pointer:", x.v)
case T:
    fmt.Println("value:", x.v)
}
```

---

## Coding Patterns

### Pattern 1: Heterogeneous list

```go
for _, item := range items {
    switch v := item.(type) {
    case int: ...
    case string: ...
    }
}
```

### Pattern 2: Visitor pattern

```go
type Node any

func visit(n Node) {
    switch n := n.(type) {
    case *Add: visitAdd(n)
    case *Mul: visitMul(n)
    case *Num: visitNum(n)
    }
}
```

### Pattern 3: Error dispatching

```go
switch err := err.(type) {
case *Network: handleNet(err)
case *Storage: handleStore(err)
case nil:      handleSuccess()
default:       handleUnknown(err)
}
```

---

## Clean Code

### Rule 1: Use type switch for multiple types

```go
// Bad
if s, ok := i.(string); ok { ... }
if n, ok := i.(int); ok { ... }
if b, ok := i.(bool); ok { ... }

// Good
switch v := i.(type) {
case string: ...
case int: ...
case bool: ...
}
```

### Rule 2: Keep a `default`

```go
switch v := i.(type) {
case int: ...
case string: ...
default:
    log.Printf("unexpected type: %T", v)
}
```

### Rule 3: Refactor — prefer interface

```go
// Bad — type switch in domain code
switch e := entity.(type) {
case *User: e.Save()
case *Order: e.Save()
}

// Good — interface
type Saver interface { Save() error }
saver.Save()
```

---

## Best Practices

1. **Use type switch for multiple types** (3+)
2. **Keep a default** — for unexpected types
3. **`case nil`** — check for nil interface
4. **Refactor to interfaces** in domain logic
5. **Multi-case** — `v` stays `any`, be careful

---

## Edge Cases & Pitfalls

### Pitfall 1: Type of `v` in multi-case

```go
switch v := i.(type) {
case int, int64:
    // v is any, int operations don't work
    // v + 1  // compile error
}
```

### Pitfall 2: nil interface vs nil concrete

```go
var p *T = nil
var i any = p

switch i.(type) {
case nil:
    // this case does NOT match — i is not nil (type info present)
case *T:
    // this case matches
}
```

### Pitfall 3: No fallthrough

```go
switch i.(type) {
case int:
    fmt.Println("int")
    // fallthrough  // COMPILE ERROR — not allowed in type switch
case string:
    fmt.Println("string")
}
```

### Pitfall 4: Type switch in a method

```go
func (t T) M(i any) {
    switch i.(type) {
    case T:    // OK
    case *T:   // OK
    }
}
```

OK — assertions work as usual.

---

## Common Mistakes

| Mistake | Solution |
|------|--------|
| Switch with a single case | Use type assertion |
| Using `v` in a multi-case as a specific type | Put each type in its own case, or work with any |
| Using `fallthrough` | Not allowed — type switch forbids it |
| Forgetting the nil case | Add `case nil:` |

---

## Common Misconceptions

**1. "Type switch is just a shorthand for assertion"**
No — it is a separate statement with optimized dispatch.

**2. "In a multi-case `v` has the common type"**
No — `v` stays `any`.

**3. "`default` is required"**
No — it is optional. But keeping one is best practice.

---

## Test

### 1. Type switch syntax?
**Answer:** `switch v := i.(type) { case T1: ... }`.

### 2. Type of `v` in a multi-case?
**Answer:** `any` (no specific type inferred).

### 3. When does `case nil` match?
**Answer:** When the interface value is truly nil.

### 4. Does `fallthrough` work in type switch?
**Answer:** No — compile error.

### 5. Is `default` required?
**Answer:** No. Best practice is to keep one.

---

## Tricky Questions

**Q1: Does `var i any; switch i.(type) { case nil: ... }` match?**
Yes — i is a nil interface, `case nil` matches.

**Q2: Given `var p *int = nil; var i any = p; switch i.(type) { case nil: ... case *int: ... }`, which case matches?**
`*int` — type info is present, so it is not nil.

**Q3: Difference between type switch and type assertion?**
Switch handles multiple types and is optimized. Assertion handles one type.

**Q4: Can a type switch be nested inside another type switch?**
Yes, nesting is fine.

**Q5: Can you perform a type-specific operation inside a multi-case?**
Only via methods of a common interface (or via any).

---

## Cheat Sheet

```
SYNTAX
─────────────────
switch v := i.(type) {
case T1: ...      // v is T1
case T2, T3: ...  // v is any
case nil: ...
default: ...      // v has i's dynamic type
}

WHEN TO USE
─────────────────
✓ Checking 3+ types
✓ Heterogeneous data
✓ JSON dynamic
✓ AST/visitor
✓ Error dispatching

RULES
─────────────────
NO fallthrough
case nil — true nil interface
default — best practice
multi-case — v = any

REFACTORING
─────────────────
In domain logic → prefer interface
At boundaries → type switch is fine
```

---

## Self-Assessment Checklist

- [ ] I can write the type switch syntax
- [ ] I know that `v` is `any` in a multi-case
- [ ] I use `case nil` correctly
- [ ] I know `fallthrough` does not work
- [ ] I know when to use type switch vs type assertion

---

## Summary

A type switch checks the concrete type of an interface value across several alternatives:
- Syntax: `switch v := i.(type) { ... }`
- `v` has a specific type in each case
- Multi-case: `v` is `any`
- `case nil` — nil interface
- `default` — best practice
- `fallthrough` does not work

A type switch is faster and more readable than several chained type assertions. But in domain logic, prefer interfaces.

---

## Diagrams

### Type switch flow

```mermaid
graph TD
    A[i.(type)] --> B{Dynamic type}
    B -->|T1| C[case T1<br/>v is T1]
    B -->|T2| D[case T2<br/>v is T2]
    B -->|nil| E[case nil]
    B -->|other| F[default<br/>v is i's type]
```
