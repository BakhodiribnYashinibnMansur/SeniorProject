# Go Command — Specification

> **Official Specification Reference**
> Source: [cmd/go documentation](https://pkg.go.dev/cmd/go)
> Also: [Go Module Reference](https://go.dev/ref/mod) | [go.dev/ref/spec#Compilation_units](https://go.dev/ref/spec)

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

The `go` command is the official build tool for Go programs. It is the single entry point for compiling, testing, formatting, and managing dependencies. Its behavior is normatively defined in the `cmd/go` documentation.

From the official documentation:
> "Go is a tool for managing Go source code. Usage: go <command> [arguments]"

### Primary subcommands

| Command | Purpose |
|---------|---------|
| `go build` | Compile packages and dependencies |
| `go run` | Compile and run a Go program |
| `go test` | Test packages |
| `go mod` | Module maintenance |
| `go get` | Add/upgrade module dependencies |
| `go install` | Compile and install packages and dependencies |
| `go fmt` | Run `gofmt` on package sources |
| `go vet` | Report likely mistakes in packages |
| `go clean` | Remove object files and cached files |
| `go doc` | Show documentation for a package or symbol |
| `go list` | List packages or modules |
| `go generate` | Generate Go files by running commands |
| `go env` | Print Go environment information |
| `go version` | Print Go version |

### Relationship to the language specification

The `go` command implements the compilation model specified in the language spec. From §Compilation units:
> "A Go compilation unit is a set of source files. The unit compiles to a single package object. The dependencies of a compilation unit are the imported packages."

---

## 2. Formal Grammar

### Command-line invocation grammar

```ebnf
GoCommand     = "go" SubCommand { Flag } { Argument } .
SubCommand    = "build" | "run" | "test" | "mod" | "get" | "install"
              | "fmt" | "vet" | "clean" | "doc" | "list" | "generate"
              | "env" | "version" | "work" | "tool" .
Flag          = "-" FlagName [ "=" FlagValue | FlagValue ] .
FlagName      = identifier .
FlagValue     = string .
Argument      = PackagePattern | GoFile | ModulePath .
PackagePattern = "." | "./..." | PackagePath | PackagePath "/..." .
```

### Package pattern grammar

```ebnf
PackagePattern  = "." | "./..." | PackagePath | PackagePath "/..." .
PackagePath     = identifier { "/" identifier } .
```

Special patterns recognized by all `go` subcommands:
- `.` — the package in the current directory
- `./...` — the package in the current directory and all subdirectories
- `std` — all packages in the Go standard library
- `all` — all packages in the module plus their test dependencies
- `cmd` — the Go compiler toolchain packages

### `go test` flag grammar

```ebnf
TestFlags    = { TestFlag } .
TestFlag     = "-bench" Regexp
             | "-benchmem"
             | "-benchtime" Duration
             | "-count" n
             | "-cover"
             | "-coverprofile" file
             | "-cpu" CPUList
             | "-failfast"
             | "-fuzz" Regexp
             | "-json"
             | "-list" Regexp
             | "-parallel" n
             | "-race"
             | "-run" Regexp
             | "-short"
             | "-shuffle" "off" | "on" | seed
             | "-timeout" Duration
             | "-v"
             .
```

---

## 3. Core Rules & Constraints

### Rule 1: `go build` produces no output for non-main packages unless `-o` is given

From the `go build` documentation:
> "When compiling packages, build ignores files that end in '_test.go'. When compiling a single main package, build writes the resulting executable to an output file named after the first source file ('go build ed.go rx.go' writes 'ed' or 'ed.exe') or the source code directory ('go build unix/sam' writes 'sam' or 'sam.exe'). The '.exe' suffix is added when writing a Windows executable."

```bash
# Library package: no output file, just type-checks and compiles
go build ./mypackage

# Main package: creates executable
go build .          # creates: ./myapp (or myapp.exe on Windows)
go build -o server  # creates: ./server
go build ./cmd/app  # creates: ./app

# Cross-platform output
GOOS=linux GOARCH=amd64 go build -o myapp-linux ./cmd/app
GOOS=windows GOARCH=amd64 go build -o myapp.exe ./cmd/app
```

### Rule 2: `go test` requires test functions to follow naming conventions

From the `go test` documentation:
> "Test files that declare a package with the suffix '_test' will be compiled as a separate package, and then linked and run with the main test binary. Test functions must have the signature: func TestXxx(*testing.T)"

```go
// VALID test function signatures
func TestAdd(t *testing.T) { ... }
func TestAdd_overflow(t *testing.T) { ... }
func TestAdd_negative(t *testing.T) { ... }

// VALID benchmark function
func BenchmarkAdd(b *testing.B) { ... }

// VALID fuzz test (Go 1.18+)
func FuzzAdd(f *testing.F) { ... }

// VALID example function (used by godoc)
func ExampleAdd() {
    fmt.Println(Add(1, 2))
    // Output: 3
}

// INVALID: lowercase after 'Test' — ignored by go test
func Testadd(t *testing.T) { ... } // not run
```

### Rule 3: `go mod tidy` synchronizes go.mod with actual imports

From the module reference:
> "The go mod tidy command adds any missing module requirements necessary to build the current module's packages and dependencies, and removes requirements on modules that don't provide any relevant packages. It also adds any missing entries to go.sum and removes unnecessary entries."

```bash
# After adding a new import in source code:
go mod tidy

# What go mod tidy does:
# 1. Scans all .go files in the module
# 2. Adds required modules not in go.mod
# 3. Removes modules from go.mod not imported by any .go file
# 4. Updates go.sum with new checksums
```

### Rule 4: `go vet` runs static analysis checks built into the toolchain

From the documentation:
> "Vet examines Go source code and reports suspicious constructs, such as Printf calls whose arguments do not align with the format string."

`go vet` checks that are guaranteed to run (from the `cmd/vet` source):
- `asmdecl` — mismatches between assembly and Go declarations
- `assign` — useless assignments
- `atomic` — common misuses of sync/atomic
- `bools` — common mistakes involving boolean operators
- `buildtag` — malformed `//go:build` constraints
- `cgocall` — CGo pointer passing rule violations
- `composites` — composite literals that use unkeyed fields
- `copylocks` — lock values passed by value
- `directive` — invalid compiler directives
- `errorsas` — incorrect use of `errors.As`
- `httpresponse` — common mistakes using HTTP responses
- `ifaceassert` — impossible interface-to-interface type assertions
- `loopclosure` — references to loop variables from within nested functions
- `lostcancel` — context.CancelFunc not called on all paths
- `nilfunc` — comparisons of functions to nil
- `printf` — printf-style function misuses
- `shift` — shifts that equal or exceed the width of the integer type
- `stdmethods` — misspellings in the signatures of common methods
- `stringintconv` — string(int) conversions
- `structtag` — malformed struct tags
- `testinggoroutine` — t.Fatal called from goroutines not started by the test
- `tests` — common mistakes in tests
- `timeformat` — incorrect time format strings
- `unmarshal` — invalid JSON/XML unmarshal targets
- `unreachable` — unreachable code
- `unsafeptr` — invalid conversions of uintptr to unsafe.Pointer
- `unusedresult` — calls to certain functions where the result is discarded

---

## 4. Type Rules

### Build constraints and type-gated compilation

Build constraints allow conditional compilation based on OS, architecture, or Go version. This directly affects which types and functions are available:

```go
//go:build linux

package syscall

// LinuxSigset is only defined on Linux
type LinuxSigset [2]uint32
```

```go
//go:build windows

package syscall

// SecurityAttributes is only defined on Windows
type SecurityAttributes struct {
    Length             uint32
    SecurityDescriptor uintptr
    InheritHandle      uint32
}
```

### Test binary types

The `go test` command defines special types in the `testing` package:

| Type | Used in | Purpose |
|------|---------|---------|
| `*testing.T` | `func TestXxx(t *testing.T)` | Test state and logging |
| `*testing.B` | `func BenchmarkXxx(b *testing.B)` | Benchmark state and timer control |
| `*testing.F` | `func FuzzXxx(f *testing.F)` | Fuzz test seed corpus management |
| `*testing.M` | `func TestMain(m *testing.M)` | Test binary lifecycle |

### Flag types for `go build`

Build flags that affect output type:
- `-buildmode=exe` — Build main package as executable (default)
- `-buildmode=c-shared` — Build as C shared library (`.so`/`.dll`)
- `-buildmode=c-archive` — Build as C archive (`.a`)
- `-buildmode=plugin` — Build as Go plugin (`.so`)
- `-buildmode=pie` — Build as Position-Independent Executable

---

## 5. Behavioral Specification

### `go build` pipeline

The build pipeline for a single package:

```
Source files (.go)
    |
    v
[Lexer + Parser] --> AST (Abstract Syntax Tree)
    |
    v
[Type checker] --> Typed AST
    |
    v
[SSA lowering] --> SSA IR
    |
    v
[Backend (architecture-specific)] --> Object file (.o)
    |
    v
[Linker] --> Executable binary
```

Flags controlling the pipeline:

```bash
go build -gcflags="-N -l"  # disable optimizations and inlining (for debuggers)
go build -gcflags="-S"     # print assembly to stdout
go build -ldflags="-s -w"  # strip symbol table and DWARF debug info
go build -ldflags="-X main.version=1.0.0"  # set variable at link time
```

### `go test` execution model

From the documentation:
> "Go test recompiles each package along with any files with names matching the file pattern '*_test.go'. These additional files can contain test functions, benchmark functions, fuzz tests and example functions."

Test binary execution order:
1. `TestMain` (if defined) — controls overall test lifecycle
2. Each `TestXxx` function in the order they appear in source
3. `BenchmarkXxx` functions only if `-bench` flag is given
4. `FuzzXxx` functions only if `-fuzz` flag is given

### Build caching behavior

From the documentation:
> "The go command caches build results to reuse in future builds. The default location for cache data is a subdirectory named 'go-build' in the standard user cache directory."

Cache invalidation triggers:
- Source file content change
- Build flags change
- Environment variable change (`GOOS`, `GOARCH`, `CGO_ENABLED`, etc.)
- Go toolchain version change
- Any imported package's cache entry is invalidated

```bash
go clean -cache   # clear the build cache
go clean -testcache  # clear only test result cache
go env GOCACHE    # show cache location
```

---

## 6. Defined vs Undefined Behavior

### Defined behavior

| Scenario | Defined outcome |
|----------|----------------|
| `go build ./...` on a package with errors | Compile errors printed, no binary produced, exit code 1 |
| `go test` passes all tests | Exit code 0 |
| `go test` has a failing test | Exit code 1; failing test names printed |
| `go test -race` detects data race | Exit code 1; race report printed to stderr |
| `go vet` finds an issue | Exit code 1; issue description printed |
| `go mod tidy` with no internet | Uses cached modules; fails if module not cached |
| `go build -o /dev/null` | Compiles but discards output (useful for type-checking) |

### Defined exit codes

| Exit code | Meaning |
|-----------|---------|
| 0 | Success |
| 1 | Build/test failure |
| 2 | `go` command usage error |

### Undefined / tool-specific behavior

| Scenario | Status |
|----------|--------|
| Order of packages compiled in `./...` | Implementation-defined (parallelized) |
| Exact binary size | Implementation-defined (depends on linker) |
| Inlining decisions | Implementation-defined (controlled by `-gcflags="-l"` to disable) |
| Escape analysis results | Implementation-defined (controlled by `-gcflags="-m"` to print) |

---

## 7. Edge Cases from Spec

### Edge Case 1: `go test -run` uses regexp matching, not exact names

```bash
# Run only tests whose name matches "TestAdd"
go test -run TestAdd ./...

# Run tests matching "TestAdd" OR "TestSub"
go test -run "TestAdd|TestSub" ./...

# Run subtests: matches TestUser/create
go test -run "TestUser/create" ./...

# Run ALL tests (match any name)
go test -run . ./...
```

The regexp is anchored to the full test function name with `^` and `$` implicitly.

### Edge Case 2: `go run` only accepts `package main` files

```bash
# VALID: main package files
go run main.go
go run main.go helper.go  # multiple files from same package
go run .                  # all files in current directory

# INVALID: cannot run a library package directly
# go run ./mylib  # error: cannot run non-main package
```

### Edge Case 3: `go generate` runs arbitrary commands, not `go build`

From the documentation:
> "Generate runs commands described by directives within existing files. Those commands can run any process but the intent is to create or update Go source files."

```go
//go:generate stringer -type=Direction

package main

type Direction int

const (
    North Direction = iota
    East
    South
    West
)
// Running 'go generate ./...' will call:
// stringer -type=Direction
// which generates direction_string.go
```

`go generate` is NOT run automatically by `go build`. You must call it explicitly.

### Edge Case 4: `go install` installs to `$GOBIN`, `go build` does not

```bash
# go build: binary in current directory
go build ./cmd/myapp
ls ./myapp  # found here

# go install: binary in $GOBIN
go install ./cmd/myapp
ls $(go env GOBIN)/myapp  # found here

# Install a specific version from the internet
go install github.com/user/tool@v1.2.3
go install github.com/user/tool@latest
```

### Edge Case 5: `go mod download` vs `go mod tidy`

| Command | What it does |
|---------|-------------|
| `go mod download` | Downloads all modules listed in `go.mod` to the cache |
| `go mod tidy` | Adds missing modules, removes unused modules, updates go.sum |
| `go mod verify` | Verifies downloaded modules match go.sum hashes |
| `go mod graph` | Prints the module dependency graph |
| `go mod why` | Explains why a module is needed |
| `go mod vendor` | Copies dependencies into `vendor/` directory |
| `go mod edit` | Edit go.mod programmatically |

---

## 8. Version History

| Go Version | `go` command change |
|------------|---------------------|
| Go 1.0 (2012) | `go build`, `go run`, `go test`, `go fmt`, `go vet`, `go install` established |
| Go 1.1 (2013) | `go test -cover` coverage analysis added |
| Go 1.2 (2013) | `go test -cover` gains `-coverprofile` |
| Go 1.4 (2014) | `go generate` command added |
| Go 1.5 (2015) | `go tool trace` for execution tracing |
| Go 1.7 (2016) | `go test` subtests: `t.Run("name", func)` |
| Go 1.10 (2018) | Build cache introduced (significant build speedup) |
| Go 1.11 (2018) | `go mod` subcommands introduced |
| Go 1.12 (2019) | `go mod download` and `go mod graph` improved |
| Go 1.13 (2019) | `GOPROXY` and `GONOSUMDB` default values set; `go env -w` added |
| Go 1.14 (2020) | `go test` adds `-shuffle` flag preview |
| Go 1.16 (2021) | Module mode default; `go install pkg@version` added |
| Go 1.17 (2021) | `go mod tidy` improved for lazy loading; `go get` no longer installs |
| Go 1.18 (2022) | `go test -fuzz` fuzzing support. `go work` workspace commands. |
| Go 1.19 (2022) | `go doc` improved; `go generate` improvements |
| Go 1.20 (2023) | `go test -shuffle` stabilized; `go build -cover` for whole-program coverage |
| Go 1.21 (2023) | `go test -fullpath`; `go build -pgo` for profile-guided optimization |
| Go 1.22 (2024) | `go telemetry` command added; `go test` timing improvements |
| Go 1.23 (2024) | `go tool` improvements; iterator support in `go test` |

---

## 9. Implementation-Specific Behavior

### Parallel compilation

`go build` compiles packages in parallel, limited by:
- `GOMAXPROCS` (for compilation goroutines)
- `-p n` flag (number of parallel compilations, default = number of CPUs)

```bash
# Limit to sequential compilation (useful for debugging)
go build -p 1 ./...
```

### Linker flags

The `go` linker (`cmd/link`) accepts flags via `-ldflags`:

```bash
# Strip debug info and symbol table (smaller binary)
go build -ldflags="-s -w" .

# Embed version string
go build -ldflags="-X main.version=$(git describe --tags)" .

# Force external linker (useful for CGO)
go build -ldflags="-linkmode=external" .
```

### Compiler flags via `-gcflags`

```bash
# Disable inlining (easier to debug)
go build -gcflags="-l" .

# Disable inlining AND optimizations (for delve debugger)
go build -gcflags="all=-N -l" .

# Print inlining decisions
go build -gcflags="-m" .

# Print escape analysis decisions
go build -gcflags="-m=2" .

# Print SSA output (very verbose)
go build -gcflags="-S" .
```

### `go test` output formats

```bash
# Default: minimal output (dot for pass, F for fail)
go test ./...

# Verbose: print each test name and result
go test -v ./...

# JSON output (machine-readable)
go test -json ./...

# Generate HTML coverage report
go test -coverprofile=cover.out ./...
go tool cover -html=cover.out -o coverage.html
```

---

## 10. Spec Compliance Checklist

For correct use of the `go` command:

- [ ] `go build ./...` completes without errors before committing
- [ ] `go vet ./...` completes without issues
- [ ] `go test ./...` passes all tests
- [ ] `go test -race ./...` passes with no data races
- [ ] `go mod tidy` has been run and `go.mod`/`go.sum` are up to date
- [ ] `go mod verify` passes (module checksums match)
- [ ] Build tags (`//go:build`) are correct for platform-specific files
- [ ] `go generate` is documented and generates correct code
- [ ] `-ldflags` used to embed version information in production builds
- [ ] `go build -gcflags="all=-N -l"` only used for debug builds, not production
- [ ] Coverage is measured with `go test -cover ./...`
- [ ] Benchmarks use `b.ResetTimer()` after setup code

---

## 11. Official Examples

### Complete test file example

```go
package math_test

import (
    "testing"
)

func Add(a, b int) int { return a + b }

func TestAdd(t *testing.T) {
    tests := []struct {
        name     string
        a, b     int
        expected int
    }{
        {"positive", 1, 2, 3},
        {"negative", -1, -2, -3},
        {"zero", 0, 0, 0},
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            got := Add(tt.a, tt.b)
            if got != tt.expected {
                t.Errorf("Add(%d, %d) = %d, want %d", tt.a, tt.b, got, tt.expected)
            }
        })
    }
}

func BenchmarkAdd(b *testing.B) {
    for range b.N {
        Add(1, 2)
    }
}
```

Run with:
```bash
go test -v -run TestAdd ./...
go test -bench=BenchmarkAdd -benchmem ./...
```

### Fuzz test (Go 1.18+)

```go
package fuzz_test

import (
    "testing"
    "unicode/utf8"
)

func FuzzReverse(f *testing.F) {
    // Seed corpus
    f.Add("Hello, World!")
    f.Add("")
    f.Add("a")

    f.Fuzz(func(t *testing.T, input string) {
        reversed := reverseString(input)
        // Property: reversing twice yields original
        if reverseString(reversed) != input {
            t.Errorf("double reverse of %q != original", input)
        }
        // Property: output is valid UTF-8
        if !utf8.ValidString(reversed) {
            t.Errorf("reversed %q is not valid UTF-8", input)
        }
    })
}

func reverseString(s string) string {
    runes := []rune(s)
    for i, j := 0, len(runes)-1; i < j; i, j = i+1, j-1 {
        runes[i], runes[j] = runes[j], runes[i]
    }
    return string(runes)
}
```

Run with:
```bash
go test -fuzz=FuzzReverse -fuzztime=30s ./...
```

### Link-time variable injection

```go
package main

import "fmt"

// Set by -ldflags at build time
var (
    version   = "dev"
    buildTime = "unknown"
    gitCommit = "none"
)

func main() {
    fmt.Printf("Version:    %s\n", version)
    fmt.Printf("Build Time: %s\n", buildTime)
    fmt.Printf("Git Commit: %s\n", gitCommit)
}
```

```bash
go build \
  -ldflags="-X main.version=1.2.3 \
            -X main.buildTime=$(date -u +%Y-%m-%dT%H:%M:%SZ) \
            -X main.gitCommit=$(git rev-parse --short HEAD)" \
  -o myapp .
```

### TestMain for setup/teardown

```go
package integration_test

import (
    "fmt"
    "os"
    "testing"
)

func TestMain(m *testing.M) {
    // Setup: runs before any test
    fmt.Println("Setting up test database...")
    setupDB()

    // Run tests
    exitCode := m.Run()

    // Teardown: runs after all tests
    fmt.Println("Tearing down test database...")
    teardownDB()

    os.Exit(exitCode)
}

func setupDB() {}
func teardownDB() {}

func TestSomething(t *testing.T) {
    t.Log("uses the database set up in TestMain")
}
```

### go:generate example for code generation

```go
//go:generate go run ./gen/main.go -output routes_gen.go

package main

// Running 'go generate ./...' will execute:
// go run ./gen/main.go -output routes_gen.go
// which might scan handler functions and produce routing boilerplate

func main() {}
```

---

## 12. Related Spec Sections

| Resource | URL | Relevance |
|----------|-----|-----------|
| cmd/go documentation | https://pkg.go.dev/cmd/go | Complete `go` command reference |
| go build flags | https://pkg.go.dev/cmd/go#hdr-Compile_packages_and_dependencies | Build flags |
| go test flags | https://pkg.go.dev/cmd/go#hdr-Testing_flags | Test flags |
| go mod subcommands | https://pkg.go.dev/cmd/go#hdr-Module_maintenance | Module commands |
| testing package | https://pkg.go.dev/testing | Test function signatures |
| Build constraints | https://pkg.go.dev/cmd/go#hdr-Build_constraints | `//go:build` syntax |
| go generate | https://pkg.go.dev/cmd/go#hdr-Generate_Go_files_by_running_commands | Code generation |
| cmd/compile flags | https://pkg.go.dev/cmd/compile | `-gcflags` options |
| cmd/link flags | https://pkg.go.dev/cmd/link | `-ldflags` options |
| Go module reference | https://go.dev/ref/mod | Module system specification |
| Fuzzing documentation | https://go.dev/doc/fuzz/ | `go test -fuzz` guide |
| Profile-guided optimization | https://go.dev/doc/pgo | `-pgo` flag usage |
