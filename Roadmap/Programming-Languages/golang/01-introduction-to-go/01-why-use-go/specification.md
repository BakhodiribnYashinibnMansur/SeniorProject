# Why Use Go — Specification

> **Official Specification Reference**
> Source: [Go Language Specification](https://go.dev/ref/spec) — §Introduction
> Also: [Go FAQ](https://go.dev/doc/faq) | [Go at Google](https://go.dev/talks/2012/splash.article)

---

## Table of Contents

1. [Spec Reference](#spec-reference)
2. [Formal Grammar](#formal-grammar)
3. [Core Rules & Constraints](#core-rules--constraints)
4. [Type Rules](#type-rules)
5. [Behavioral Specification](#behavioral-specification)
6. [Defined vs Undefined Behavior](#defined-vs-undefined-behavior)
7. [Edge Cases from Spec](#edge-cases-from-spec)
8. [Version History](#version-history)
9. [Implementation-Specific Behavior](#implementation-specific-behavior)
10. [Spec Compliance Checklist](#spec-compliance-checklist)
11. [Official Examples](#official-examples)
12. [Related Spec Sections](#related-spec-sections)

---

## 1. Spec Reference

The Go specification opens with a formal statement of scope:

> "This is the reference manual for the Go programming language. The pre-Go1.18 version, without generics, can be found [here](https://tip.golang.org/ref/spec). For more information and other documents, see [go.dev](https://go.dev)."

**Design goals stated in the FAQ:**

| Goal | Specification Statement |
|------|------------------------|
| Simplicity | The language is designed to be small enough that most programmers can hold the entire specification in their heads. |
| Safety | Go is a statically typed, garbage-collected language. Type errors are caught at compile time. |
| Concurrency | First-class goroutines and channels based on CSP (Communicating Sequential Processes). |
| Fast compilation | Single-pass compilation; no circular imports allowed. |
| Readability | `gofmt` enforces a single canonical style; the spec defines formatting rules. |

**Normative reference documents:**

- [The Go Programming Language Specification](https://go.dev/ref/spec) — the authoritative language definition
- [The Go Memory Model](https://go.dev/ref/mem) — defines the conditions under which reads of a memory location in one goroutine can be guaranteed to observe values produced by writes to the same location in a different goroutine
- [Go Module Reference](https://go.dev/ref/mod) — defines module-aware build semantics

---

## 2. Formal Grammar

The Go specification uses Extended Backus-Naur Form (EBNF) throughout. The meta-syntax is:

```ebnf
Syntax      = { Production } .
Production  = production_name "=" [ Expression ] "." .
Expression  = Term { "|" Term } .
Term        = Factor { Factor } .
Factor      = production_name
            | token [ "…" token ]
            | Group
            | Option
            | Repetition .
Group       = "(" Expression ")" .
Option      = "[" Expression "]" .
Repetition  = "{" Expression "}" .
```

**Source file top-level grammar** (the entry point for every Go program):

```ebnf
SourceFile       = PackageClause ";" { ImportDecl ";" } { TopLevelDecl ";" } .
PackageClause    = "package" PackageName .
PackageName      = identifier .
ImportDecl       = "import" ( ImportSpec | "(" { ImportSpec ";" } ")" ) .
ImportSpec       = [ "." | PackageName ] ImportPath .
ImportPath       = string_lit .
TopLevelDecl     = Declaration | FunctionDecl | MethodDecl .
```

**Identifier grammar** (core to every Go name):

```ebnf
identifier     = letter { letter | unicode_digit } .
letter         = unicode_letter | "_" .
unicode_letter = /* a Unicode code point categorized as "Letter" */ .
unicode_digit  = /* a Unicode code point categorized as "Number, decimal digit" */ .
```

**Keyword list** (reserved; cannot be used as identifiers):

```
break        default      func         interface    select
case         defer        go           map          struct
chan         else         goto         package      switch
const        fallthrough  if           range        type
continue     for          import       return       var
```

---

## 3. Core Rules & Constraints

### Rule 1: Every Go source file must declare exactly one package

From the spec:
> "A package clause begins each source file and defines the package to which the file belongs."

```go
// VALID: correct package declaration
package main

func main() {}
```

```go
// INVALID: missing package clause — compiler error:
// expected 'package', found 'func'
func main() {}
```

```go
// INVALID: two package clauses in one file — compiler error:
// non-declaration statement outside function body
package main
package util
```

### Rule 2: The blank identifier `_` suppresses unused-variable and unused-import errors

From the spec:
> "The blank identifier provides a way to ignore right-hand side values in an assignment... It may be used like any other identifier in a declaration, but it does not introduce a binding and thus is not declared."

```go
package main

import (
    "fmt"
    _ "net/http" // imported for side effects only; blank identifier suppresses "imported and not used"
)

func divide(a, b int) (int, int) {
    return a / b, a % b
}

func main() {
    quotient, _ := divide(10, 3) // remainder discarded via blank identifier
    fmt.Println(quotient)        // Output: 3
}
```

### Rule 3: Go enforces strict unused-variable and unused-import rules at compile time

From the spec:
> "Implementation restriction: A compiler may make it illegal to declare a variable inside a function body if the variable is never used."

```go
package main

// INVALID: unused import causes compile error
// import "fmt"

// INVALID: unused local variable causes compile error
func bad() {
    // x := 5 // error: x declared and not used
}

// VALID: package-level variables do not trigger this restriction
var packageLevel = 42

func main() {
    _ = packageLevel
}
```

### Rule 4: Exported identifiers begin with a Unicode upper-case letter

From the spec:
> "An identifier may be exported to permit access to it from another package. An identifier is exported if both: the first character of the identifier's name is a Unicode upper-case letter (Unicode character category Lu); and the identifier is declared in the package block, or it is a field name or a method name."

```go
package geometry

// Exported — accessible from other packages
type Rectangle struct {
    Width  float64
    Height float64
}

// Exported function
func Area(r Rectangle) float64 {
    return r.Width * r.Height
}

// Unexported — only accessible within package geometry
type point struct {
    x, y float64
}

func distance(p point) float64 {
    return p.x*p.x + p.y*p.y
}
```

---

## 4. Type Rules

Go is a **statically typed** language. Every expression has a type known at compile time.

### Built-in predeclared types

| Category | Types |
|----------|-------|
| Boolean | `bool` |
| Integer | `int`, `int8`, `int16`, `int32`, `int64`, `uint`, `uint8`, `uint16`, `uint32`, `uint64`, `uintptr` |
| Float | `float32`, `float64` |
| Complex | `complex64`, `complex128` |
| String | `string` |
| Byte alias | `byte` (alias for `uint8`) |
| Rune alias | `rune` (alias for `int32`) |

### Type identity rules

From the spec:
> "Two types are either identical or different. A named type is always different from any other type."

| Expression | Type | Notes |
|------------|------|-------|
| `var x int` | `int` | predeclared type |
| `type MyInt int` | `MyInt` | new named type, distinct from `int` |
| `var y MyInt = x` | compile error | `int` and `MyInt` are different types |
| `var y MyInt = MyInt(x)` | `MyInt` | explicit conversion required |

### Type assignability rules (summary from spec §Assignability)

A value `x` of type `V` is assignable to a variable of type `T` if any of these conditions hold:

1. `V` and `T` have identical underlying types and at least one of them is not a named type.
2. `T` is an interface type and `V` implements `T`.
3. `x` is the predeclared identifier `nil` and `T` is a pointer, function, slice, map, channel, or interface type.
4. `x` is an untyped constant representable by a value of type `T`.

---

## 5. Behavioral Specification

### Compile-time behavior

Go's compiler enforces these properties before any code runs:

1. **Type checking** — all type mismatches are detected at compile time.
2. **Package initialization order** — within a package, variables are initialized in the order they appear, but dependency order is respected.
3. **Unused symbols** — unused local variables and unused imports are compile errors (not warnings).
4. **Circular imports** — the Go specification forbids cycles in the import graph. If package A imports B and B imports A, compilation fails.

From the spec:
> "An import declaration declares a dependency relation between the containing source file and the imported package. The import declaration names an identifier (PackageName) to be used for access and an ImportPath that specifies the package to be imported."

### Runtime behavior

1. **Goroutine scheduling** — the Go runtime multiplexes goroutines onto OS threads using an M:N scheduler. The spec guarantees goroutines are scheduled cooperatively at function calls, channel operations, and system calls.
2. **Garbage collection** — Go uses a concurrent tri-color mark-and-sweep garbage collector. Memory is automatically reclaimed; there is no `free()`.
3. **Stack growth** — goroutine stacks start small (typically 2–8 KB) and grow dynamically as needed (up to 1 GB by default on 64-bit systems).
4. **Panic and recover** — `panic` unwinds the call stack, running deferred functions. `recover` can catch a panic if called directly inside a deferred function.

---

## 6. Defined vs Undefined Behavior

Unlike C, Go has very few sources of undefined behavior. The specification explicitly defines the behavior of most edge cases.

### Defined behavior

| Scenario | Spec-defined outcome |
|----------|---------------------|
| Integer overflow | Wraps around using two's complement arithmetic (spec §Arithmetic operators) |
| Nil pointer dereference | Runtime panic: `nil pointer dereference` |
| Division by zero (integer) | Runtime panic: `integer divide by zero` |
| Division by zero (float) | Returns `+Inf`, `-Inf`, or `NaN` per IEEE 754 |
| Out-of-bounds slice index | Runtime panic: `index out of range` |
| Closing a closed channel | Runtime panic: `close of closed channel` |
| Sending to a closed channel | Runtime panic: `send on closed channel` |

### Defined but "undefined" in memory model terms

From the Go Memory Model:
> "If the effects of an atomic operation A are observed by atomic operation B, then A is synchronized before B. All the atomic operations executed in a program behave as though executed in some sequentially consistent order."

The memory model explicitly states:
> "Programs with data races have undefined behavior: they may crash or, on hardware with weak memory ordering, may produce results inconsistent with any sequentially consistent execution."

```go
// UNDEFINED BEHAVIOR: data race
package main

import "fmt"

var counter int

func increment() {
    counter++ // read-modify-write: not atomic
}

func main() {
    go increment()
    go increment()
    fmt.Println(counter) // result is undefined: race condition
}
```

### Undefined behavior (spec-acknowledged gaps)

| Scenario | Status |
|----------|--------|
| Goroutine scheduling order | Undefined; do not rely on it |
| Map iteration order | Deliberately randomized since Go 1.0 |
| Finalizer execution timing | Not guaranteed to run |
| `unsafe` package operations | Behavior outside spec guarantees |

---

## 7. Edge Cases from Spec

### Edge Case 1: `init` functions and initialization order

From the spec:
> "A package with no imports is initialized by assigning initial values to all its package-level variables and then calling all init functions in the order they appear in the source, as presented to the compiler."

```go
package main

import "fmt"

var (
    a = b + 1    // a depends on b; b is initialized first
    b = 2
)

func init() {
    fmt.Println("init called, a =", a, "b =", b)
}

func main() {
    fmt.Println("main called, a =", a, "b =", b)
}
// Output:
// init called, a = 3 b = 2
// main called, a = 3 b = 2
```

Multiple `init` functions are allowed in the same file or package; they all run in order.

### Edge Case 2: Named return values and naked returns

From the spec:
> "The return value or values may be explicitly listed in the result type... If the result type specifies names for its results, a return statement without arguments ('naked return') returns the current values of the result variables."

```go
package main

import "fmt"

func minMax(arr []int) (min, max int) { // named return values
    min, max = arr[0], arr[0]
    for _, v := range arr[1:] {
        if v < min {
            min = v
        }
        if v > max {
            max = v
        }
    }
    return // naked return: returns current min and max
}

func main() {
    lo, hi := minMax([]int{3, 1, 4, 1, 5, 9, 2, 6})
    fmt.Println(lo, hi) // Output: 1 9
}
```

### Edge Case 3: Constants and iota

From the spec:
> "Within a constant declaration, the predeclared identifier iota represents successive untyped integer constants. Its value is the index of the respective ConstSpec in that constant declaration."

```go
package main

import "fmt"

type Direction int

const (
    North Direction = iota // 0
    East                   // 1
    South                  // 2
    West                   // 3
)

// iota resets to 0 in every new const block
const (
    _  = iota             // 0, skipped with blank identifier
    KB = 1 << (10 * iota) // 1 << 10 = 1024
    MB                    // 1 << 20 = 1048576
    GB                    // 1 << 30 = 1073741824
)

func main() {
    fmt.Println(North, East, South, West) // 0 1 2 3
    fmt.Println(KB, MB, GB)               // 1024 1048576 1073741824
}
```

### Edge Case 4: Interface satisfaction is implicit

From the spec:
> "A type implements any interface comprising any subset of its methods and may therefore implement several distinct interfaces."

```go
package main

import (
    "fmt"
    "math"
)

type Shape interface {
    Area() float64
}

type Circle struct {
    Radius float64
}

// Circle implicitly implements Shape — no "implements" keyword needed
func (c Circle) Area() float64 {
    return math.Pi * c.Radius * c.Radius
}

func printArea(s Shape) {
    fmt.Printf("Area: %.2f\n", s.Area())
}

func main() {
    c := Circle{Radius: 5}
    printArea(c) // Output: Area: 78.54
}
```

---

## 8. Version History

| Go Version | Release Date | Relevant Changes to Language Philosophy |
|------------|-------------|----------------------------------------|
| Go 1.0 | March 2012 | First stable release; compatibility promise established. The spec was frozen as the normative document. |
| Go 1.1 | May 2013 | Integer division rules clarified; method sets for pointer receivers revised. |
| Go 1.4 | December 2014 | `for range` over a channel formalized. `go generate` tool added. |
| Go 1.5 | August 2015 | Compiler fully rewritten in Go (was C). GC latency improved dramatically. |
| Go 1.7 | August 2016 | `context` package added to standard library; subtests in `testing`. |
| Go 1.11 | August 2018 | Go Modules introduced (`go.mod`/`go.sum`). GOPATH-based workflow deprecated. |
| Go 1.13 | September 2019 | `errors.Is`, `errors.As`, `fmt.Errorf` with `%w` wrapping added. |
| Go 1.14 | February 2020 | Overlapping interface method sets allowed (addressed in spec). |
| Go 1.17 | August 2021 | Module graph pruning; `//go:build` constraint syntax added. |
| Go 1.18 | March 2022 | **Generics** — type parameters added to spec. Largest spec change since Go 1.0. |
| Go 1.21 | August 2023 | `min`, `max`, `clear` built-in functions added. `log/slog` structured logging. |
| Go 1.22 | February 2024 | Loop variable capture semantics changed: each iteration gets its own variable. |

### Go Compatibility Promise (since Go 1.0)

> "It is intended that programs written to the Go 1 specification will continue to compile and run correctly, unchanged, over the lifetime of that specification." — [go.dev/doc/go1compat](https://go.dev/doc/go1compat)

This promise is a core design principle: upgrading the Go toolchain should never break existing Go 1.x programs.

---

## 9. Implementation-Specific Behavior

### gc (the standard compiler)

The `gc` compiler (used by `go build`) is the reference implementation maintained by the Go team. It targets all major platforms:

| Platform | Architecture |
|----------|-------------|
| `linux` | `amd64`, `arm64`, `386`, `arm`, `mips`, `riscv64`, `s390x` |
| `darwin` | `amd64`, `arm64` |
| `windows` | `amd64`, `arm64`, `386` |
| `freebsd` | `amd64`, `arm64` |
| `wasm` | `wasm` (via `GOARCH=wasm GOOS=js`) |

### gccgo

An alternative compiler built on GCC. It follows the same spec but may differ in:
- Stack growth strategy
- Escape analysis results
- Garbage collector behavior

### Platform-dependent sizes

From the spec:
> "The size of int and uint are implementation-specific, either 32 or 64 bits."

| Type | Size (32-bit platform) | Size (64-bit platform) |
|------|----------------------|----------------------|
| `int` | 32 bits | 64 bits |
| `uint` | 32 bits | 64 bits |
| `uintptr` | 32 bits | 64 bits |
| `float32` | 32 bits | 32 bits |
| `float64` | 64 bits | 64 bits |

---

## 10. Spec Compliance Checklist

Use this checklist when evaluating whether Go code is written to specification:

- [ ] Every source file begins with a `package` clause
- [ ] `package main` with a `func main()` is present in executable programs
- [ ] No circular imports exist in the dependency graph
- [ ] All imported packages are used (or blanked with `_`)
- [ ] All declared local variables are used
- [ ] Exported identifiers start with an uppercase letter
- [ ] No use of `unsafe` package without documented justification
- [ ] Integer arithmetic overflow is intentional if relied upon (two's complement)
- [ ] No data races (verify with `go test -race` / `go run -race`)
- [ ] `init` functions do not depend on goroutine scheduling order
- [ ] Interface implementations are verified (use compile-time assertion: `var _ MyInterface = MyType{}`)
- [ ] Constants fit within their declared type (untyped constants checked at use site)

---

## 11. Official Examples

### Hello World (canonical entry point per the spec)

```go
package main

import "fmt"

func main() {
    fmt.Println("Hello, World!")
}
```

### Demonstrating Go's concurrency model (goroutines + channels)

```go
package main

import (
    "fmt"
    "sync"
)

func worker(id int, wg *sync.WaitGroup) {
    defer wg.Done()
    fmt.Printf("Worker %d starting\n", id)
    // ... do work ...
    fmt.Printf("Worker %d done\n", id)
}

func main() {
    var wg sync.WaitGroup

    for i := 1; i <= 3; i++ {
        wg.Add(1)
        go worker(i, &wg)
    }

    wg.Wait()
    fmt.Println("All workers done")
}
```

### Demonstrating interfaces and type assertions

```go
package main

import (
    "fmt"
    "math"
)

type Shape interface {
    Area() float64
    Perimeter() float64
}

type Circle struct{ Radius float64 }
type Rectangle struct{ Width, Height float64 }

func (c Circle) Area() float64      { return math.Pi * c.Radius * c.Radius }
func (c Circle) Perimeter() float64 { return 2 * math.Pi * c.Radius }

func (r Rectangle) Area() float64      { return r.Width * r.Height }
func (r Rectangle) Perimeter() float64 { return 2 * (r.Width + r.Height) }

func describe(s Shape) {
    fmt.Printf("Area: %.2f  Perimeter: %.2f\n", s.Area(), s.Perimeter())
}

func main() {
    shapes := []Shape{
        Circle{Radius: 3},
        Rectangle{Width: 4, Height: 5},
    }
    for _, s := range shapes {
        describe(s)
    }
}
// Output:
// Area: 28.27  Perimeter: 18.85
// Area: 20.00  Perimeter: 18.00
```

---

## 12. Related Spec Sections

| Spec Section | URL | Relevance |
|---|---|---|
| Introduction | https://go.dev/ref/spec#Introduction | Language overview and design goals |
| Source file organization | https://go.dev/ref/spec#Source_file_organization | Package and import structure |
| Declarations and scope | https://go.dev/ref/spec#Declarations_and_scope | Identifier visibility rules |
| Exported identifiers | https://go.dev/ref/spec#Exported_identifiers | What names are visible outside the package |
| Package clause | https://go.dev/ref/spec#Package_clause | Package declaration grammar |
| Import declarations | https://go.dev/ref/spec#Import_declarations | How packages are imported |
| Program initialization and execution | https://go.dev/ref/spec#Program_initialization_and_execution | `init` functions, `main` entry point |
| Type identity | https://go.dev/ref/spec#Type_identity | When two types are the same |
| Assignability | https://go.dev/ref/spec#Assignability | When a value can be assigned to a variable |
| Go Memory Model | https://go.dev/ref/mem | Concurrency and data race rules |
