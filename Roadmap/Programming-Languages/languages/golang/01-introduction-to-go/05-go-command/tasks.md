# Go Command — Practical Tasks

## Table of Contents

1. [Junior Tasks](#junior-tasks)
2. [Middle Tasks](#middle-tasks)
3. [Senior Tasks](#senior-tasks)
4. [Questions](#questions)
5. [Mini Projects](#mini-projects)
6. [Challenge](#challenge)

---

## Junior Tasks

### Task 1: Initialize, Build, and Run a Go Project

**Type:** Code

**Goal:** Practice the full workflow: `go mod init`, create a file, `go build`, and run.

**Instructions:**

1. Create a new directory called `greeter`
2. Initialize a Go module
3. Create `main.go` with the code below
4. Build a binary called `greeter`
5. Run the binary with your name as an argument

**Starter code:**

```go
package main

import (
    "fmt"
    "os"
    "strings"
)

func main() {
    if len(os.Args) < 2 {
        fmt.Println("Usage: greeter <name>")
        os.Exit(1)
    }
    name := strings.Join(os.Args[1:], " ")
    // TODO: Print a greeting message using the name
    fmt.Println("TODO")
}
```

**Expected output:**
```
$ ./greeter Alice
Hello, Alice! Welcome to Go.
```

**Evaluation criteria:**
- [ ] `go mod init` creates a valid `go.mod`
- [ ] `go build -o greeter` produces a binary
- [ ] Binary runs correctly with arguments
- [ ] Code passes `go vet`

---

### Task 2: Write and Run Tests

**Type:** Code

**Goal:** Practice writing test files and running `go test`.

**Starter code:**

```go
// Save as calculator.go
package main

func Add(a, b int) int      { return a + b }
func Subtract(a, b int) int { return a - b }
func Multiply(a, b int) int { return a * b }

func Divide(a, b int) (int, error) {
    if b == 0 {
        return 0, fmt.Errorf("division by zero")
    }
    return a / b, nil
}

func main() {}
```

```go
// Save as calculator_test.go
package main

import "testing"

// TODO: Write table-driven tests for all four functions
// Use this structure:
func TestAdd(t *testing.T) {
    tests := []struct {
        a, b, want int
    }{
        // TODO: Add test cases
    }
    for _, tt := range tests {
        got := Add(tt.a, tt.b)
        if got != tt.want {
            t.Errorf("Add(%d, %d) = %d; want %d", tt.a, tt.b, got, tt.want)
        }
    }
}
```

**Expected output:**
```
$ go test -v
=== RUN   TestAdd
--- PASS: TestAdd (0.00s)
=== RUN   TestSubtract
--- PASS: TestSubtract (0.00s)
...
PASS
```

**Evaluation criteria:**
- [ ] All four functions have tests
- [ ] `Divide` tests include the division-by-zero case
- [ ] `go test -v` passes all tests
- [ ] At least 3 test cases per function

---

### Task 3: Format and Vet a Messy File

**Type:** Code

**Goal:** Practice `go fmt` and `go vet` to fix code quality issues.

**Starter code (intentionally messy):**

```go
package main
import "fmt"
import "os"
func main(){
fmt.Printf("%d\n",  "not a number")
    x:=42
    fmt.Println(x)
    os.Exit(0)
    fmt.Println("unreachable")
}
```

**Instructions:**

1. Save the file as `messy.go`
2. Run `go fmt messy.go` — observe what changes
3. Run `go vet messy.go` — observe the warnings
4. Fix all issues found by `go vet`
5. Run `go vet` again to confirm no issues remain

**Evaluation criteria:**
- [ ] `go fmt` was run successfully
- [ ] `go vet` issues were identified and fixed
- [ ] Printf format string matches argument types
- [ ] Unreachable code is removed or fixed

---

### Task 4: Add an External Dependency

**Type:** Code

**Goal:** Practice `go get` and `go mod tidy`.

**Instructions:**

1. Create a new module: `go mod init github.com/user/colorapp`
2. Write a program that uses `github.com/fatih/color` to print colored output
3. Run `go mod tidy` to download the dependency
4. Verify `go.mod` and `go.sum` are created with correct entries
5. Build and run the program

**Starter code:**

```go
package main

import (
    "github.com/fatih/color"
)

func main() {
    // TODO: Print "Success!" in green
    // TODO: Print "Warning!" in yellow
    // TODO: Print "Error!" in red
    color.Green("TODO")
}
```

**Evaluation criteria:**
- [ ] `go.mod` contains `github.com/fatih/color` dependency
- [ ] `go.sum` exists with checksums
- [ ] Program compiles and runs
- [ ] Colored output is visible in terminal

---

## Middle Tasks

### Task 5: Build with Version Injection

**Type:** Code

**Goal:** Practice `-ldflags` to inject build-time values.

**Requirements:**

1. Create a program with `version`, `commit`, and `buildTime` variables
2. Write a Makefile that injects these values using `git describe`, `git rev-parse`, and `date`
3. Add a `--version` flag that prints build info
4. Build and verify the output

**Starter code:**

```go
package main

import (
    "flag"
    "fmt"
    "os"
)

var (
    version   = "dev"
    commit    = "none"
    buildTime = "unknown"
)

func main() {
    showVersion := flag.Bool("version", false, "Show version info")
    flag.Parse()

    if *showVersion {
        // TODO: Print version, commit, and buildTime
        fmt.Println("TODO")
        os.Exit(0)
    }

    fmt.Println("Application is running...")
}
```

**Expected output:**
```
$ make build
$ ./server --version
Version:    v1.2.3
Commit:     a1b2c3d
Build Time: 2024-01-15T10:30:00Z
```

**Evaluation criteria:**
- [ ] Makefile correctly injects all three values
- [ ] `--version` flag works
- [ ] Values are not hardcoded in source
- [ ] Handle errors in Makefile (e.g., no git tags)
- [ ] Write tests for version display logic

---

### Task 6: Multi-Module Workspace

**Type:** Code + Design

**Goal:** Practice `go work` for multi-module development.

**Requirements:**

1. Create three modules: `shared`, `api`, `worker`
2. `shared` exports a `Config` struct and a `LoadConfig` function
3. `api` imports `shared` and starts an HTTP server
4. `worker` imports `shared` and processes jobs
5. Use `go work` to develop all three simultaneously
6. Verify that changes to `shared` are immediately reflected

**Directory structure:**

```
workspace/
├── go.work
├── shared/
│   ├── go.mod
│   └── config.go
├── api/
│   ├── go.mod
│   └── main.go
└── worker/
    ├── go.mod
    └── main.go
```

**Evaluation criteria:**
- [ ] `go work init ./shared ./api ./worker` creates a valid workspace
- [ ] All three modules compile with `go build ./...`
- [ ] Changes to `shared` are visible in `api` and `worker` without `go get`
- [ ] `go.work` is in `.gitignore`

---

### Task 7: CI Pipeline Configuration

**Type:** Design + Code

**Goal:** Create a complete CI configuration for a Go project.

**Requirements:**

Create a GitHub Actions workflow (`.github/workflows/ci.yml`) that:

1. Runs `go fmt ./...` and checks for uncommitted formatting changes
2. Runs `go vet ./...`
3. Runs `go test -race -count=1 -coverprofile=coverage.out ./...`
4. Checks test coverage is above 70%
5. Runs `go mod tidy` and checks for uncommitted changes
6. Builds the binary with `-trimpath -ldflags="-s -w"`
7. Caches Go modules and build cache

**Evaluation criteria:**
- [ ] All 7 steps are implemented
- [ ] Build cache is properly configured
- [ ] Coverage threshold is enforced
- [ ] Pipeline fails fast on first error

---

## Senior Tasks

### Task 8: Cross-Compilation Pipeline

**Type:** Code

**Goal:** Build a cross-compilation system that produces binaries for 5 platforms.

**Provided code to extend:**

```go
// cmd/build/main.go — extend this cross-compilation script
package main

import (
    "fmt"
    "os"
    "os/exec"
    "runtime"
    "sync"
    "time"
)

type Target struct {
    GOOS   string
    GOARCH string
}

func main() {
    targets := []Target{
        {"linux", "amd64"},
        {"linux", "arm64"},
        {"darwin", "amd64"},
        {"darwin", "arm64"},
        {"windows", "amd64"},
    }

    // TODO: Implement parallel cross-compilation with:
    // - Semaphore limiting concurrency to runtime.NumCPU()
    // - Version injection via -ldflags
    // - Binary size reporting
    // - SHA256 checksum generation for each binary
    // - Total build time measurement
    // - Error handling per target (don't fail all if one fails)
    _ = targets
    fmt.Println("TODO: implement cross-compilation")
}
```

**Requirements:**
- [ ] Parallel builds with bounded concurrency
- [ ] SHA256 checksums file generated (like `checksums.txt`)
- [ ] Build time reported per target and total
- [ ] Graceful error handling (one failure does not stop others)
- [ ] Version injected from git tags
- [ ] Benchmark your solution: total time vs sequential builds

---

### Task 9: Build Tag Feature System

**Type:** Code + Architecture

**Goal:** Design a feature flag system using build tags.

**Requirements:**

Create a project with these features controlled by build tags:

1. `debug` tag: Enables pprof endpoints, verbose logging, request dumping
2. `metrics` tag: Enables Prometheus metrics endpoint
3. `trace` tag: Enables OpenTelemetry tracing

Each feature should have:
- A `_debug.go`, `_metrics.go`, `_trace.go` file with the implementation
- A corresponding `_no_debug.go`, `_no_metrics.go`, `_no_trace.go` with no-op stubs

```bash
# Development build with all features
go build -tags "debug,metrics,trace" -o server-dev

# Production build with only metrics
go build -tags "metrics" -o server-prod

# Minimal build
go build -o server-minimal
```

**Evaluation criteria:**
- [ ] Each feature can be independently toggled
- [ ] No-op stubs have zero runtime cost
- [ ] `go build ./...` compiles without any tags
- [ ] Binary size differs between configurations
- [ ] Document which tags are used in production vs development

---

### Task 10: Embedded Application

**Type:** Code

**Goal:** Build a self-contained application using `go:embed`.

**Requirements:**

Build a web server that embeds:
- HTML templates (`templates/*.html`)
- Static assets (`static/css/*.css`, `static/js/*.js`)
- Database migrations (`migrations/*.sql`)
- A default configuration file (`config/defaults.yaml`)

The server should:
1. Serve static files from embedded filesystem
2. Render embedded templates
3. Run embedded migrations on startup
4. Fall back to embedded config if no external config is provided

**Evaluation criteria:**
- [ ] Single binary runs without any external files
- [ ] Binary size is reasonable (under 10 MB for small assets)
- [ ] Embedded files are accessible via `embed.FS`
- [ ] Benchmark comparison: embedded vs file-system serving

---

## Questions

### 1. What is the purpose of `go.sum` and why should it be committed to version control?

**Answer:**
`go.sum` contains cryptographic checksums (SHA-256 hashes) of each module version's content. It serves two purposes:
1. **Integrity verification:** Ensures downloaded modules have not been tampered with
2. **Reproducibility:** Guarantees the same bytes are used on every machine

It should be committed because without it, `go mod verify` cannot verify module integrity, and different developers might get different module contents.

---

### 2. What is `GOPROXY` and what is its default value?

**Answer:**
`GOPROXY` specifies the module proxy URL used to download dependencies. The default is `https://proxy.golang.org,direct`, meaning:
1. First try the Go module proxy (cached, fast, available even if origin is down)
2. Fall back to direct download from the source repository

For private modules, set `GOPRIVATE=github.com/mycompany/*` to bypass the proxy.

---

### 3. How does `go test` caching work?

**Answer:**
Go caches test results based on a hash of:
- The test binary
- Command-line flags
- Environment variables that affect test execution
- Test files and their dependencies

If nothing changed, the second run prints `(cached)` instead of re-executing. Use `-count=1` to force re-execution.

---

### 4. What is the difference between `go build ./...` and `go build -o /dev/null ./...`?

**Answer:**
`go build ./...` compiles all packages and produces binaries for any `main` packages. `go build -o /dev/null ./...` compiles all packages but discards the output — useful for checking compilation without producing artifacts.

Note: `go build ./...` for non-main packages produces no output by default (no binary), it only checks compilation.

---

### 5. How do you check for known vulnerabilities in your dependencies?

**Answer:**
```bash
# Install govulncheck
go install golang.org/x/vuln/cmd/govulncheck@latest

# Check for vulnerabilities
govulncheck ./...
```

`govulncheck` analyzes your code's call graph and reports only vulnerabilities in functions your code actually calls (not all functions in the dependency).

---

### 6. What does `go list -m -json all` show?

**Answer:**
It lists all modules in the dependency graph in JSON format, including:
- Module path and version
- Whether it is the main module or a dependency
- The directory path in the module cache
- Replace directives if any

Useful for auditing dependencies and building custom tooling.

---

### 7. How do you profile a Go program's build time?

**Answer:**
```bash
# Verbose build showing each package
go build -v ./... 2>&1

# Show actual commands executed (with timing)
go build -x ./... 2>&1

# Use toolexec for per-tool timing
go build -toolexec="/usr/bin/time" ./... 2>&1

# Profile compilation of a specific function
GOSSAFUNC=hotFunction go build main.go
```

---

## Mini Projects

### Project 1: Go Project Scaffolder

**Requirements:**

Build a CLI tool (`go-scaffold`) that creates a new Go project with:

- [ ] `go mod init` with the provided module path
- [ ] Standard directory layout (`cmd/`, `internal/`, `pkg/`)
- [ ] `Makefile` with `build`, `test`, `lint`, `generate` targets
- [ ] `.gitignore` with Go-specific entries
- [ ] `Dockerfile` with multi-stage build
- [ ] `.github/workflows/ci.yml` with proper Go CI
- [ ] `README.md` with build instructions

```bash
go-scaffold github.com/user/myproject
# Creates:
# myproject/
# ├── cmd/myproject/main.go
# ├── internal/
# ├── pkg/
# ├── Makefile
# ├── Dockerfile
# ├── .gitignore
# ├── .github/workflows/ci.yml
# ├── go.mod
# └── README.md
```

**Difficulty:** Middle
**Estimated time:** 4-6 hours

---

## Challenge

### Build Time Analyzer

Build a tool that analyzes Go project build times and suggests optimizations.

**Requirements:**

1. Run `go build -v -x ./...` and parse output to measure per-package compile times
2. Identify the slowest packages (compilation bottleneck)
3. Check for unnecessary CGO usage
4. Measure binary size and suggest `-ldflags="-s -w"` if debug info is present
5. Check if build cache is being utilized effectively
6. Generate a report with actionable recommendations

**Constraints:**
- Must run in under 60 seconds for a project with 50 packages
- Memory usage under 50 MB
- No external libraries (stdlib only)
- Must work on Linux, macOS, and Windows

**Scoring:**
- Correctness: 50% — accurate timing and recommendations
- Performance (benchmarks): 30% — fast analysis
- Code quality (go vet, readability): 20% — clean, testable code

**Expected output:**
```
Build Analysis Report
=====================
Total build time: 12.3s
Packages compiled: 47
Cache hit rate: 85%

Slowest packages:
  1. internal/parser    3.2s  (26%)
  2. internal/codegen   2.1s  (17%)
  3. pkg/api            1.8s  (15%)

Recommendations:
  - Binary contains debug info (25 MB). Use -ldflags="-s -w" to reduce to ~17 MB
  - CGO is enabled but no C code found. Set CGO_ENABLED=0 for faster builds
  - internal/parser has 15 files. Consider splitting into sub-packages for parallel compilation
```
