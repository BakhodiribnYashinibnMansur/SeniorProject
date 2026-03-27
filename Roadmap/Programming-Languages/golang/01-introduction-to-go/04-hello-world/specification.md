# Hello World — Specification

> **Official Specification Reference**
> Source: [Go Language Specification](https://go.dev/ref/spec) — §Source file organization, §Program execution
> Also: §Package clause | §Import declarations | §Function declarations | §Predeclared identifiers

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

A minimal Hello World program encodes nearly every top-level rule of the Go specification. The spec defines the exact structure every Go executable must follow.

From §Program execution:
> "A complete program is created by linking a single, unimported package called the command package with all the packages it imports, transitively. The command package must have package name main and declare a function main that takes no arguments and returns no value."

From §Source file organization:
> "Each source file consists of a package clause defining the package to which it belongs, followed by a possibly empty set of import declarations that declare packages whose contents it wishes to use, followed by a possibly empty set of declarations."

### The three mandatory elements of an executable Go program

1. `package main` — declares this file belongs to the main package
2. `func main()` — the program entry point
3. Exactly one package named `main` per executable binary

The `fmt` package (used in the canonical Hello World) is part of the Go standard library. Its `Println` function signature is:

```go
func Println(a ...any) (n int, err error)
```

---

## 2. Formal Grammar

### Complete source file grammar

```ebnf
SourceFile    = PackageClause ";" { ImportDecl ";" } { TopLevelDecl ";" } .

PackageClause = "package" PackageName .
PackageName   = identifier .

ImportDecl    = "import" ( ImportSpec | "(" { ImportSpec ";" } ")" ) .
ImportSpec    = [ "." | PackageName ] ImportPath .
ImportPath    = string_lit .

TopLevelDecl  = Declaration | FunctionDecl | MethodDecl .
Declaration   = ConstDecl | TypeDecl | VarDecl .

FunctionDecl  = "func" FunctionName [ TypeParameters ] Signature [ FunctionBody ] .
FunctionName  = identifier .
FunctionBody  = Block .
Block         = "{" StatementList "}" .
StatementList = { Statement ";" } .
```

### Function declaration grammar

```ebnf
FunctionDecl  = "func" FunctionName Signature [ FunctionBody ] .
Signature     = Parameters [ Result ] .
Parameters    = "(" [ ParameterList [ "," ] ] ")" .
ParameterList = ParameterDecl { "," ParameterDecl } .
ParameterDecl = [ IdentifierList ] [ "..." ] Type .
Result        = Parameters | Type .
```

For `func main()`:
- `FunctionName` = `main`
- `Parameters` = `()` (empty)
- `Result` = absent (no return value)
- `FunctionBody` = the statement block `{ ... }`

### Call expression grammar (how `fmt.Println(...)` is parsed)

```ebnf
CallExpr         = PrimaryExpr Arguments .
Arguments        = "(" [ ( ExpressionList | Type [ "," ExpressionList ] ) [ "..." ] [ "," ] ] ")" .
PrimaryExpr      = Operand | PrimaryExpr Selector | PrimaryExpr Index | ... .
Selector         = "." identifier .
```

`fmt.Println("Hello, World!")` breaks down as:
- `fmt` — package name operand
- `.Println` — selector
- `("Hello, World!")` — arguments (one untyped string constant)

---

## 3. Core Rules & Constraints

### Rule 1: The `main` package must have exactly one `func main()`

From the spec §Program execution:
> "Program execution begins by initializing the main package and then invoking the function main. When that function invocation returns, the program exits. It does not wait for other (non-main) goroutines to complete."

```go
// VALID: correct minimal executable
package main

func main() {}
```

```go
// INVALID: package main without func main — linker error:
// runtime.main_main·f: function main is undeclared in the main package
package main
```

```go
// INVALID: func main cannot have parameters
// compile error: func main must have no arguments and no return values
package main

func main(args []string) {} // wrong
```

```go
// INVALID: func main cannot have return values
package main

func main() int { return 0 } // compile error
```

### Rule 2: Import paths are string literals; the last element is the default package name

From the spec §Import declarations:
> "If the PackageName is omitted, it defaults to the identifier specified in the package clause of the imported package."

```go
package main

import (
    "fmt"           // package name: fmt (matches directory name)
    "math/rand"     // package name: rand (last path element)
    "encoding/json" // package name: json
)

func main() {
    fmt.Println(rand.Intn(100))
    _ = json.Marshal
}
```

### Rule 3: Semicolons are inserted automatically by the lexer

From the spec §Semicolons:
> "The formal grammar uses semicolons ';' as terminators in a number of productions. Go programs may omit most of these semicolons using the following two rules: When the input is broken into tokens, a semicolon is automatically inserted into the token stream immediately after a line's final token if that token is: an identifier; an integer, floating-point, imaginary, rune, or string literal; one of the keywords break, continue, fallthrough, or return; one of the operators and punctuation ++, --, ), ], or }."

This is why the opening brace `{` must be on the same line as `func main()`:

```go
// VALID
func main() {
    fmt.Println("Hello")
}

// INVALID: semicolon is inserted after "main()" on the first line,
// turning the next line's "{" into a syntax error
// func main()
// {
//     fmt.Println("Hello")
// }
```

### Rule 4: String literals in Go are UTF-8 encoded

From the spec §String literals:
> "String literals represent string constants obtained from concatenating a sequence of characters. There are two forms: raw string literals and interpreted string literals."

```go
package main

import "fmt"

func main() {
    // interpreted string literal: escape sequences processed
    fmt.Println("Hello, World!\n")     // newline at end
    fmt.Println("Tab:\there")          // tab character

    // raw string literal: no escape processing, can span lines
    fmt.Println(`Hello,
World!`)                               // literal newline in output

    // Unicode string literal
    fmt.Println("Hello, 世界")         // valid UTF-8
    fmt.Println("Hello, \u4e16\u754c") // same, using escapes
}
```

---

## 4. Type Rules

### String type specification

From the spec §String types:
> "A string type represents the set of string values. A string value is a (possibly empty) sequence of bytes. The number of bytes is called the length of the string and is never negative. Strings are immutable: once created, it is impossible to change the contents of a string."

| Property | Value |
|----------|-------|
| Underlying type | sequence of bytes (uint8) |
| Mutability | immutable |
| Encoding | arbitrary bytes (UTF-8 by convention) |
| Length | `len(s)` returns number of bytes, not characters |
| Index | `s[i]` returns byte at index `i`, type `byte` (uint8) |
| Zero value | `""` (empty string) |

### Untyped string constants

From the spec §Constants:
> "An untyped constant has a default type which is the type to which the constant is implicitly converted in contexts where a typed value is needed."

```go
package main

import "fmt"

func main() {
    const greeting = "Hello, World!" // untyped string constant

    // Used in different contexts:
    var s string = greeting     // assigned to string variable
    fmt.Println(greeting)       // passed where string expected
    fmt.Printf("%s\n", greeting) // passed as interface{} (any)

    // len of a constant string is a compile-time constant
    const n = len(greeting) // n = 13
    fmt.Println(n)
}
```

### The `fmt.Println` function type

```go
// Signature from the fmt package source:
func Println(a ...any) (n int, err error)
// 'any' is an alias for 'interface{}' since Go 1.18
// The variadic parameter accepts any number of arguments of any type
```

`fmt.Println` returns `(int, error)` — but in the Hello World example, these are typically ignored. The return values are the number of bytes written and any write error.

---

## 5. Behavioral Specification

### Program initialization sequence

From the spec §Program initialization and execution:

1. All package-level variables are initialized, in dependency order.
2. All `init` functions in imported packages run (in import order, then within a package in source order).
3. The `main` package's package-level variables are initialized.
4. The `main` package's `init` functions run.
5. `func main()` is called.
6. When `main()` returns (or `os.Exit` is called), the program terminates.

```go
package main

import "fmt"

var message = initMessage() // runs before main()

func initMessage() string {
    fmt.Println("1: initMessage() called")
    return "Hello, World!"
}

func init() {
    fmt.Println("2: init() called, message =", message)
}

func main() {
    fmt.Println("3: main() called")
    fmt.Println(message)
}
// Output:
// 1: initMessage() called
// 2: init() called, message = Hello, World!
// 3: main() called
// Hello, World!
```

### Exit behavior

From the spec:
> "If the main function returns or calls os.Exit, the program terminates. Unlike Java's System.exit or C's exit(), deferred functions are NOT run when os.Exit is called."

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    defer fmt.Println("This WILL run — normal return")
    fmt.Println("Hello, World!")
    // os.Exit(0) would NOT run the deferred Println
}
```

### Standard I/O

`fmt.Println` writes to `os.Stdout`. The underlying file descriptor is 1 (standard output). The program also has:
- `os.Stdin` (file descriptor 0)
- `os.Stderr` (file descriptor 2)

---

## 6. Defined vs Undefined Behavior

### Defined behavior in Hello World context

| Scenario | Spec-defined outcome |
|----------|---------------------|
| `fmt.Println()` with no args | Prints a single newline |
| `fmt.Println(a, b)` | Prints `a` and `b` separated by a space, followed by newline |
| Writing to `os.Stdout` | Writes to file descriptor 1 |
| Program returns from `main()` | Exit code 0 |
| `os.Exit(n)` called | Exit code n, no deferred functions run |
| Panic not recovered in `main()` | Prints stack trace to stderr, exits with non-zero code |

### String immutability (defined)

```go
package main

func main() {
    s := "Hello"
    // s[0] = 'h' // compile error: cannot assign to s[0] (value of type byte)

    // Strings ARE indexable (read-only):
    b := s[0] // b == 'H' (byte value 72)
    _ = b
}
```

### `fmt.Println` return values

Most Hello World examples ignore the return values of `fmt.Println`. This is legal — Go does not require you to use return values from a function call (unlike unused variables).

```go
package main

import "fmt"

func main() {
    n, err := fmt.Println("Hello, World!")
    if err != nil {
        panic(err)
    }
    _ = n // n = 14 (13 chars + newline byte)
}
```

---

## 7. Edge Cases from Spec

### Edge Case 1: `package main` files cannot be imported

From the spec:
> "Package main is special. The program starts running in package main."

A file with `package main` is never imported by other packages. It can only be built into an executable. If you attempt to import a `main` package, the build fails:

```
// This is not possible:
// import "github.com/example/myapp" // error if that package is "package main"
```

### Edge Case 2: Multiple `init()` functions in one file

From the spec:
> "A package can contain multiple init functions. Within a package, the package-level variable initialization and the call of init functions happen in a single goroutine, sequentially, one package at a time."

```go
package main

import "fmt"

func init() {
    fmt.Println("init #1")
}

func init() {
    fmt.Println("init #2")
}

func main() {
    fmt.Println("main")
}
// Output:
// init #1
// init #2
// main
```

Multiple `init` functions per file are valid. They run in the order they appear. This is unique to `init` — you cannot have two functions with the same name otherwise.

### Edge Case 3: `init` cannot be called or referenced explicitly

From the spec:
> "The name init is special: init functions cannot be referred to from anywhere in a program."

```go
package main

func init() {}

func main() {
    // init()  // compile error: undefined: init
    // f := init  // compile error: undefined: init
}
```

### Edge Case 4: Dot import makes all exported names visible without qualification

```go
package main

import (
    . "fmt" // dot import: all fmt exports are in scope directly
)

func main() {
    Println("Hello, World!") // no "fmt." prefix needed
}
```

From the spec:
> "If an explicit period (.) appears instead of a name, all the package's exported identifiers declared in that package's package block will be declared in the current file's file block and can be accessed without a qualifier."

Dot imports are generally discouraged in production code because they obscure which package a name comes from.

### Edge Case 5: Blank import for side effects

```go
package main

import (
    "fmt"
    _ "net/http/pprof" // imported for side effects: registers pprof HTTP handlers
)

func main() {
    fmt.Println("Hello, World!")
}
```

The `_` import causes the package's `init()` to run (registering pprof endpoints) without making any names visible.

---

## 8. Version History

| Go Version | Change Relevant to Source File Structure |
|------------|------------------------------------------|
| Go 1.0 (2012) | `package main` + `func main()` pattern established. Stability guaranteed. |
| Go 1.4 (2014) | `//go:generate` directive added to source files. `go generate` command. |
| Go 1.5 (2015) | Internal packages enforced by toolchain. |
| Go 1.9 (2017) | `type Alias = Type` syntax added; aliases can be used in package declarations. |
| Go 1.11 (2018) | `go.mod` file format; import paths now relative to module root. |
| Go 1.16 (2021) | `//go:embed` directive added for embedding files into binary. |
| Go 1.17 (2021) | `//go:build` constraint syntax (replaces `// +build`). |
| Go 1.18 (2022) | `any` as alias for `interface{}` predeclared. Visible in function signatures. |
| Go 1.21 (2023) | `min`, `max`, `clear` added as predeclared built-in functions. |
| Go 1.22 (2024) | `for range N` (iterate over integer) formalized in spec. |

### How `fmt.Println` works with `any` (since Go 1.18)

Before Go 1.18, the signature was:
```go
func Println(a ...interface{}) (n int, err error)
```
After Go 1.18:
```go
func Println(a ...any) (n int, err error)
```
`any` is a predeclared type alias for `interface{}`. They are 100% interchangeable.

---

## 9. Implementation-Specific Behavior

### Compilation of `package main`

When `go build` processes `package main`:

1. **Parsing** — the source is tokenized and parsed into an AST (Abstract Syntax Tree).
2. **Type checking** — all types are resolved; unused imports and variables are caught here.
3. **Compilation** — the AST is lowered to SSA (Static Single Assignment) form.
4. **Linking** — the runtime, standard library packages, and user code are linked into a single executable.

```bash
# Compile and run
go run main.go

# Compile to binary
go build -o hello main.go

# Examine the binary
file hello               # ELF 64-bit / Mach-O (platform-dependent)
go version hello         # show Go version used to build
strings hello | grep "Hello"  # verify string is embedded
```

### The Go runtime overhead in Hello World

Even a minimal Hello World binary includes:
- The Go runtime (goroutine scheduler, GC, stack management)
- The `fmt` and `os` packages and their dependencies
- Reflection support (used by `fmt`)

Typical binary sizes:
| Build option | Approximate size |
|---|---|
| `go build` (default) | 1.5–2 MB |
| `go build -ldflags="-s -w"` (stripped) | 1.0–1.5 MB |
| `CGO_ENABLED=0 go build -ldflags="-s -w"` | ~1 MB |
| With UPX compression | ~400 KB |

### `go run` vs `go build`

| Behavior | `go run main.go` | `go build -o hello` |
|----------|-----------------|---------------------|
| Compiles | Yes (to temp dir) | Yes (to current dir) |
| Runs | Yes | No |
| Artifact kept | No | Yes |
| Suitable for | Development/scripting | Distribution/deployment |

---

## 10. Spec Compliance Checklist

For a correct, spec-compliant Hello World (and any Go executable):

- [ ] File starts with `package main`
- [ ] `func main()` has no parameters and no return values
- [ ] Only one `func main()` exists in the entire `main` package
- [ ] All import paths are valid string literals
- [ ] All imported packages are used (or blanked with `_`)
- [ ] Opening brace `{` is on the same line as the function signature (semicolon insertion rule)
- [ ] String literals are valid UTF-8
- [ ] No unused local variables
- [ ] `init` functions (if present) are not called explicitly
- [ ] Return values from `fmt.Println` are either used or explicitly discarded with `_`
- [ ] `os.Exit` is only called when deferred functions do not need to run

---

## 11. Official Examples

### The canonical Hello World (from go.dev/tour)

```go
package main

import "fmt"

func main() {
    fmt.Println("Hello, World!")
}
```

### Hello World with explicit error handling

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    _, err := fmt.Fprintln(os.Stdout, "Hello, World!")
    if err != nil {
        fmt.Fprintln(os.Stderr, "write error:", err)
        os.Exit(1)
    }
}
```

### Hello World with command-line arguments

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    if len(os.Args) > 1 {
        fmt.Printf("Hello, %s!\n", os.Args[1])
    } else {
        fmt.Println("Hello, World!")
    }
}
// Run: go run main.go Alice
// Output: Hello, Alice!
```

### Hello World demonstrating initialization order

```go
package main

import "fmt"

var (
    who  = "World"
    msg  = fmt.Sprintf("Hello, %s!", who)
)

func main() {
    fmt.Println(msg) // Output: Hello, World!
}
```

### Hello World with `//go:embed` (Go 1.16+)

```go
package main

import (
    _ "embed"
    "fmt"
)

//go:embed greeting.txt
var greeting string

func main() {
    fmt.Print(greeting) // content of greeting.txt
}
// Requires: greeting.txt containing "Hello, World!\n"
```

### Hello World to stderr

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    fmt.Fprintln(os.Stderr, "Hello, World!") // writes to file descriptor 2
}
```

---

## 12. Related Spec Sections

| Spec Section | URL | Relevance |
|---|---|---|
| Source file organization | https://go.dev/ref/spec#Source_file_organization | `SourceFile` grammar |
| Package clause | https://go.dev/ref/spec#Package_clause | `package main` rule |
| Import declarations | https://go.dev/ref/spec#Import_declarations | `import "fmt"` semantics |
| Program initialization and execution | https://go.dev/ref/spec#Program_initialization_and_execution | `main()` and `init()` order |
| Function declarations | https://go.dev/ref/spec#Function_declarations | `func main()` grammar |
| String literals | https://go.dev/ref/spec#String_literals | String constant rules |
| String types | https://go.dev/ref/spec#String_types | Immutability, byte semantics |
| Semicolons | https://go.dev/ref/spec#Semicolons | Why `{` must be on same line |
| Blank identifier | https://go.dev/ref/spec#Blank_identifier | `_` in imports and assignments |
| Exported identifiers | https://go.dev/ref/spec#Exported_identifiers | `Println` starts with uppercase |
| fmt package | https://pkg.go.dev/fmt | `Println`, `Printf`, `Fprintf` |
| A Tour of Go | https://go.dev/tour/welcome/1 | Interactive Hello World tutorial |
