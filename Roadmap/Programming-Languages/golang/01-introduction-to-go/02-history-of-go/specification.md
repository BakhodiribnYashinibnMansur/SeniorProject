# History of Go — Specification

> **Official Specification Reference**
> Source: [Go Language Specification](https://go.dev/ref/spec) — §Introduction
> Also: [Go Release History](https://go.dev/doc/devel/release) | [Go 1 Compatibility Promise](https://go.dev/doc/go1compat)

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

The Go specification itself was a product of the language's design process. It serves as the normative document for the language. From the introduction:

> "Go is a general-purpose language designed with systems programming in mind. It is strongly typed and garbage-collected and has explicit support for concurrent programming. Programs are constructed from packages, whose properties allow efficient management of dependencies."

The specification was authored primarily by **Robert Griesemer**, **Rob Pike**, and **Ken Thompson** at Google, beginning in 2007. The first public release of the specification accompanied Go's open-source announcement on **November 10, 2009**.

### Key historical milestones in the spec

| Date | Milestone |
|------|-----------|
| September 2007 | Design begins at Google. Initial whiteboard spec. |
| November 10, 2009 | Go open-sourced. First public specification published alongside the `gc` compiler. |
| March 28, 2012 | **Go 1.0** released. Spec frozen with compatibility guarantees. |
| March 2022 | **Go 1.18** — largest spec revision since 1.0: type parameters (generics) added. |

### The Go 1 Compatibility Promise

Introduced with Go 1.0, this is a binding commitment:

> "It is intended that programs written to the Go 1 specification will continue to compile and run correctly, unchanged, over the lifetime of that specification. Go programs that work today should continue to work even as future releases of Go 1 arise."
> — [go.dev/doc/go1compat](https://go.dev/doc/go1compat)

This promise governs the entire release history after Go 1.0. Breaking changes require a major version increment.

---

## 2. Formal Grammar

The specification uses EBNF notation. The grammar for build constraints (relevant to version history) is:

```ebnf
BuildConstraint = "//go:build" Expr .
Expr            = OrExpr .
OrExpr          = AndExpr { "||" AndExpr } .
AndExpr         = UnaryExpr { "&&" UnaryExpr } .
UnaryExpr       = "!" UnaryExpr | "(" Expr ")" | tag .
tag             = TagName | OSName | ArchName | GoVersion .
TagName         = identifier .
GoVersion       = "go" DecimalDigit+ ( "." DecimalDigit+ )* .
```

This grammar (introduced in Go 1.17) replaced the older `// +build` comment form. Both syntaxes are recognized during the transition period.

**Go version string format** (per `go.mod` specification):

```ebnf
GoVersion = "go" decimal_digits "." decimal_digits [ "." decimal_digits ] .
```

Examples: `go1.21`, `go1.21.0`, `go1.18.3`.

**Module declaration grammar** (tracks language version per file):

```ebnf
GoDirective  = "go" GoVersion newline .
ToolDirective = "toolchain" ToolchainName newline .
ToolchainName = "go" GoVersion [ "-" Suffix ] .
```

---

## 3. Core Rules & Constraints

### Rule 1: Language version declared in go.mod controls available features

From the Go module reference:
> "The go directive declares the expected language version used by source files within the module."

```go
// go.mod — declares the minimum Go version for the module
module example.com/myapp

go 1.21  // enables features up to Go 1.21

require (
    golang.org/x/text v0.14.0
)
```

Code using features from a later version (e.g., generics from Go 1.18) will fail to compile when `go.mod` specifies an earlier version.

### Rule 2: The `//go:build` constraint syntax replaced `// +build` in Go 1.17

```go
//go:build linux && amd64

// Before Go 1.17, this was written as:
// +build linux,amd64

package platform

// This file is only compiled on Linux/amd64
```

From the spec change notes:
> "In Go 1.17 a new //go:build form with more readable syntax was introduced as the preferred format."

### Rule 3: Go 1.22 changed loop variable semantics

Before Go 1.22, loop variables were shared across iterations — a common source of bugs when closures captured them. From the Go 1.22 release notes:

> "In Go 1.22, the variable declared by a 'for' statement is created once per iteration of the loop, rather than once per loop."

```go
package main

import "fmt"

func main() {
    // Go 1.22+: each iteration creates a new variable
    // Before Go 1.22: all goroutines captured the same variable
    funcs := make([]func(), 3)
    for i := range 3 {
        funcs[i] = func() { fmt.Println(i) }
    }
    for _, f := range funcs {
        f()
    }
    // Go 1.22 output: 0 1 2
    // Pre-Go 1.22 output: 2 2 2
}
```

---

## 4. Type Rules

### Evolution of the type system across versions

| Go Version | Type System Change |
|------------|-------------------|
| Go 1.0 | Core type system established: bool, numeric, string, array, slice, struct, pointer, function, interface, map, channel |
| Go 1.9 | `type alias` syntax added: `type Alias = ExistingType` |
| Go 1.14 | Overlapping interface method sets: two interface types can both embed a third interface |
| Go 1.18 | **Type parameters**: `func Map[T, U any](s []T, f func(T) U) []U` |
| Go 1.21 | `comparable` constraint behavior clarified in spec |

### Type alias vs type definition (since Go 1.9)

```go
package main

import "fmt"

// Type definition: MyInt is a NEW type with underlying type int
type MyInt int

// Type alias: Alias is another name for int (identical type)
type Alias = int

func main() {
    var a MyInt = 5
    var b Alias = 5

    // a = b        // compile error: cannot use b (type int) as type MyInt
    // a = int(b)   // compile error: cannot use int(b) as type MyInt
    a = MyInt(b)    // OK: explicit conversion
    fmt.Println(a)  // 5

    var c int = b   // OK: Alias IS int, no conversion needed
    fmt.Println(c)  // 5
}
```

### Generic type parameters (Go 1.18+)

```ebnf
TypeParameters  = "[" TypeParamList [ "," ] "]" .
TypeParamList   = TypeParamDecl { "," TypeParamDecl } .
TypeParamDecl   = IdentifierList TypeConstraint .
TypeConstraint  = TypeElem .
TypeElem        = TypeTerm { "|" TypeTerm } .
TypeTerm        = Type | UnderlyingType .
UnderlyingType  = "~" Type .
```

```go
package main

import "fmt"

// Generic function: works with any ordered type
func Min[T int | float64 | string](a, b T) T {
    if a < b {
        return a
    }
    return b
}

func main() {
    fmt.Println(Min(3, 5))       // 3
    fmt.Println(Min(3.14, 2.71)) // 2.71
    fmt.Println(Min("go", "c"))  // c
}
```

---

## 5. Behavioral Specification

### How the spec has evolved at the behavioral level

**Before Go 1.5 (pre-2015):** The Go compiler (`gc`) was written in C. The runtime was partially C, partially assembly.

**Go 1.5 (August 2015):** Full self-hosting. The compiler and runtime were rewritten in Go. This brought:
- Concurrent garbage collector (sub-millisecond GC pauses)
- Default `GOMAXPROCS` set to number of CPU cores (was 1 before)

From the Go 1.5 release notes:
> "The garbage collector has been substantially improved. By using a concurrent sweep algorithm and allocating in parallel with marking, pause times have been reduced significantly."

**Go 1.14 (February 2020):** Asynchronous preemption of goroutines:
> "Goroutines are now asynchronously preemptible. As a result, loops without function calls no longer potentially deadlock the scheduler or significantly delay garbage collection."

```go
// Before Go 1.14: this loop could starve the scheduler
// After Go 1.14: safely preempted by the runtime
package main

func compute() {
    for {
        // tight loop — pre-1.14 this could block GC
        _ = 1 + 1
    }
}
```

**Go 1.18 (March 2022):** Generics added. The spec was extended with:
- Type parameters
- Type constraints
- Type inference
- The `any` predeclared constraint (alias for `interface{}`)

**Go 1.21 (August 2023):** Built-in functions `min`, `max`, `clear` added to the spec.

---

## 6. Defined vs Undefined Behavior

### How spec-definiteness has changed over time

| Feature | Pre-1.0 behavior | Post-1.0 behavior |
|---------|-----------------|-------------------|
| Map iteration order | Implementation-defined (sometimes sorted) | Explicitly randomized — spec forbids relying on order |
| Integer overflow | Not formally specified | Defined: two's complement wrap-around |
| Goroutine scheduling | Undefined | Still undefined ordering, but preemption guaranteed since 1.14 |
| `for` loop variable | Per-loop variable | Per-iteration variable since Go 1.22 |

### Map iteration order is deliberately undefined

```go
package main

import "fmt"

func main() {
    m := map[string]int{
        "a": 1,
        "b": 2,
        "c": 3,
    }

    // The spec says: "The iteration order over maps is not specified
    // and is not guaranteed to be the same from one iteration to the next."
    for k, v := range m {
        fmt.Printf("%s: %d\n", k, v) // order varies each run
    }
}
```

### The `unsafe` package: permanently outside the spec

From the spec:
> "Package unsafe contains operations that step around the type safety of Go programs. Packages that import unsafe may be non-portable and are not protected by the Go 1 compatibility guidelines."

This has never changed since Go 1.0 — `unsafe` is deliberately excluded from compatibility guarantees.

---

## 7. Edge Cases from Spec

### Edge Case 1: The `go1` build tag targets pre-generics code

```go
//go:build go1.18

package main

// This file is only compiled with Go 1.18 or later (has generics)
func Keys[K comparable, V any](m map[K]V) []K {
    keys := make([]K, 0, len(m))
    for k := range m {
        keys = append(keys, k)
    }
    return keys
}
```

### Edge Case 2: `//go:build ignore` excludes a file from normal builds

```go
//go:build ignore

// This file is never compiled normally.
// Run with: go run this_file.go
package main

import "fmt"

func main() {
    fmt.Println("This is a manually-run example")
}
```

### Edge Case 3: Toolchain directive in go.mod (Go 1.21+)

From the Go 1.21 release notes:
> "The go line in go.mod now declares a minimum required version. The toolchain line (new in Go 1.21) declares the specific Go toolchain to use."

```
module example.com/myapp

go 1.21.0
toolchain go1.21.3
```

If the local toolchain is older than the `go` line requires, the Go toolchain will attempt to download the required version automatically (controlled by `GOTOOLCHAIN` environment variable).

### Edge Case 4: Pre-Go 1.0 compatibility break — the `os.Error` removal

Before Go 1.0, errors were represented by `os.Error`. This was changed to the built-in `error` interface in Go 1.0. This is one of the few intentional backward-incompatible changes allowed before the 1.0 stability promise.

```go
// Pre-Go 1.0 (historical, does not compile today)
// f, err := os.Open("file.txt")
// if err != os.EOF { ... }

// Go 1.0+ (current spec)
package main

import (
    "errors"
    "fmt"
    "io"
    "os"
)

func main() {
    f, err := os.Open("file.txt")
    if err != nil {
        fmt.Println(err)
        return
    }
    defer f.Close()

    buf := make([]byte, 512)
    _, err = f.Read(buf)
    if errors.Is(err, io.EOF) {
        fmt.Println("end of file")
    }
}
```

---

## 8. Version History

### Complete Go release timeline with spec-relevant changes

| Version | Date | Key Spec / Language Changes |
|---------|------|-----------------------------|
| Go 1.0 | March 28, 2012 | Spec frozen. Compatibility promise. `error` built-in type. |
| Go 1.1 | May 13, 2013 | Integer division truncation toward zero formally specified. Method sets revised. |
| Go 1.2 | December 1, 2013 | Three-index slices: `s[a:b:c]` (capacity limit). Goroutine preemption improved. |
| Go 1.3 | June 18, 2014 | Stack copying (contiguous stacks replace segmented stacks). |
| Go 1.4 | December 10, 2014 | `for range` over nil maps and channels defined. `//go:generate` directive. |
| Go 1.5 | August 19, 2015 | Compiler rewritten in Go. Concurrent GC. `GOMAXPROCS` defaults to CPU count. |
| Go 1.6 | February 17, 2016 | Concurrent map access panic formalized. |
| Go 1.7 | August 15, 2016 | `context` package in stdlib. Compiler backend uses SSA form. |
| Go 1.8 | February 16, 2017 | Struct tags ignored during type conversion. GC pauses < 100μs. |
| Go 1.9 | August 24, 2017 | **Type aliases** (`type A = B`). `sync.Map`. |
| Go 1.10 | February 16, 2018 | Build cache introduced. `strings.Builder`. |
| Go 1.11 | August 24, 2018 | **Go Modules** (experimental). WebAssembly target. |
| Go 1.12 | February 25, 2019 | Modules on by default when `go.mod` present. |
| Go 1.13 | September 3, 2019 | Error wrapping: `%w`, `errors.Is`, `errors.As`. Numeric literals: `0b`, `0o`, `_` separators. |
| Go 1.14 | February 25, 2020 | Module mode default. Async goroutine preemption. Overlapping interface embeds allowed. |
| Go 1.15 | August 11, 2020 | `time.Duration` formatting improved. `tzdata` embedded. |
| Go 1.16 | February 16, 2021 | Module mode on by default. `//go:embed` directive. `io/fs`. |
| Go 1.17 | August 16, 2021 | `//go:build` constraint syntax. Module graph pruning. |
| Go 1.18 | March 15, 2022 | **Generics** (type parameters). `any` alias. `comparable` constraint. Fuzzing. |
| Go 1.19 | August 2, 2022 | Memory model formalized (atomic operations). Doc comments standardized. |
| Go 1.20 | February 1, 2023 | `comparable` usable anywhere `any` is used. `errors.Join`. Slice-to-array conversion. |
| Go 1.21 | August 8, 2023 | `min`, `max`, `clear` built-ins. `log/slog`. `slices`, `maps`, `cmp` packages. `toolchain` directive. |
| Go 1.22 | February 6, 2024 | **Loop variable semantics**: per-iteration variables. `for range N` integer range. `math/rand/v2`. |
| Go 1.23 | August 2024 | Iterator functions for `range` (`iter` package). `unique` package. Timer changes. |

---

## 9. Implementation-Specific Behavior

### Compiler evolution milestones

| Milestone | Version | Impact |
|-----------|---------|--------|
| `gc` compiler ported from C to Go | 1.5 | Enabled self-hosting; faster iteration on compiler |
| SSA (Static Single Assignment) backend | 1.7 | Better optimizations; ~20–30% performance improvement |
| Register-based calling convention | 1.17 | ~5% CPU improvement; changed ABI on amd64/arm64 |
| Profile-guided optimization (PGO) | 1.20 | Up to 3–4% CPU improvement using production profiles |
| Range-over-func (iterators) | 1.23 | New calling convention for iterator functions |

### Cross-compilation (unchanged since Go 1.0)

Go has supported cross-compilation since its first release, controlled by environment variables:

```bash
# Build for Linux on ARM64 from any host OS
GOOS=linux GOARCH=arm64 go build ./...

# Build for Windows on amd64 from macOS
GOOS=windows GOARCH=amd64 go build ./...

# Available targets
go tool dist list
```

---

## 10. Spec Compliance Checklist

When writing version-aware Go code, verify:

- [ ] `go.mod` specifies the correct minimum Go version for the features used
- [ ] Generics (type parameters) only used when `go 1.18` or later in `go.mod`
- [ ] Type aliases (`type A = B`) only used when `go 1.9` or later
- [ ] `//go:build` syntax used instead of `// +build` for new code (`go 1.17`+)
- [ ] `errors.Is` / `errors.As` used instead of direct `==` comparison (since `go 1.13`)
- [ ] `for i := range n` (integer range) only used with `go 1.22` or later
- [ ] Loop variable capture behavior understood for the target Go version
- [ ] `min` / `max` / `clear` built-ins only used when `go 1.21` or later
- [ ] `unsafe.SliceData`, `unsafe.StringData`, `unsafe.String` only with `go 1.17`+
- [ ] Module-aware mode is used (`go.mod` exists in the project root)

---

## 11. Official Examples

### Build constraint example (Go 1.17+ syntax)

```go
//go:build (linux || darwin) && amd64

package main

import "fmt"

func main() {
    fmt.Println("This runs only on Linux/amd64 or macOS/amd64")
}
```

### Generics example (Go 1.18+)

```go
package main

import "fmt"

// Ordered is a type constraint for types that support < > operators
type Ordered interface {
    ~int | ~int8 | ~int16 | ~int32 | ~int64 |
        ~uint | ~uint8 | ~uint16 | ~uint32 | ~uint64 |
        ~float32 | ~float64 | ~string
}

func Max[T Ordered](a, b T) T {
    if a > b {
        return a
    }
    return b
}

func main() {
    fmt.Println(Max(3, 5))         // 5
    fmt.Println(Max(3.14, 2.71))   // 3.14
    fmt.Println(Max("hello", "hi")) // hi
}
```

### Error wrapping (Go 1.13+)

```go
package main

import (
    "errors"
    "fmt"
)

var ErrNotFound = errors.New("not found")

func findUser(id int) error {
    if id != 1 {
        return fmt.Errorf("findUser(%d): %w", id, ErrNotFound)
    }
    return nil
}

func main() {
    err := findUser(42)
    if errors.Is(err, ErrNotFound) {
        fmt.Println("User does not exist:", err)
    }
}
// Output: User does not exist: findUser(42): not found
```

### Loop variable semantics (Go 1.22+)

```go
package main

import (
    "fmt"
    "sync"
)

func main() {
    var wg sync.WaitGroup

    // Go 1.22: i is a new variable per iteration
    // each goroutine captures its own copy of i
    for i := range 5 {
        wg.Add(1)
        go func() {
            defer wg.Done()
            fmt.Println(i) // always prints the captured value, not a race
        }()
    }

    wg.Wait()
}
// Output (order may vary): 0 1 2 3 4
```

---

## 12. Related Spec Sections

| Resource | URL | Relevance |
|----------|-----|-----------|
| Go Release History | https://go.dev/doc/devel/release | Authoritative changelog per version |
| Go 1 Compatibility Promise | https://go.dev/doc/go1compat | What can and cannot change |
| Go Module Reference | https://go.dev/ref/mod | `go.mod` version declarations |
| Go Language Specification — Introduction | https://go.dev/ref/spec#Introduction | Spec overview |
| Build Constraints | https://pkg.go.dev/cmd/go#hdr-Build_constraints | `//go:build` syntax |
| Go 1.18 Release Notes (Generics) | https://go.dev/blog/go1.18 | Type parameter introduction |
| Go 1.22 Release Notes (Loop vars) | https://go.dev/blog/loopvar-preview | Loop variable change |
| GOARCH/GOOS values | https://go.dev/doc/install/source#environment | Cross-compilation targets |
