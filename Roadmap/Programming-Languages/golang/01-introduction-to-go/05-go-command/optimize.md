# Go Command — Optimize the Code

> **Practice optimizing slow, inefficient, or resource-heavy Go toolchain usage related to go commands.**
> Each exercise contains working but suboptimal command usage or build scripts — your job is to make them faster, leaner, or more efficient.

---

## How to Use

1. Read the slow approach and understand what it does
2. Identify the performance bottleneck
3. Write your optimized version
4. Compare with the solution and benchmark results
5. Understand **why** the optimization works

### Difficulty Levels

| Level | Focus |
|:-----:|:------|
| 🟢 | **Easy** — Obvious inefficiencies, simple fixes |
| 🟡 | **Medium** — Algorithmic improvements, allocation reduction |
| 🔴 | **Hard** — Cache-aware code, zero-allocation patterns, runtime-level optimizations |

### Optimization Categories

| Category | Icon | Description |
|:--------:|:----:|:-----------|
| **Memory** | 📦 | Reduce allocations, reuse buffers, avoid copies |
| **CPU** | ⚡ | Better algorithms, fewer operations, cache efficiency |
| **Concurrency** | 🔄 | Better parallelism, reduce contention, avoid locks |
| **I/O** | 💾 | Batch operations, buffering, connection reuse |

---

## Exercise 1: Build Without Cache vs With Cache 🟢 ⚡

**What the code does:** Compiles a Go project from scratch every time in a CI/CD pipeline.

**The problem:** The build script clears the Go build cache before every build, causing full recompilation each time.

```bash
#!/bin/bash
# Slow version — CI build script that clears cache every time

# Step 1: Clean everything
go clean -cache
go clean -testcache

# Step 2: Build from scratch
go build -v ./...

# Step 3: Run tests from scratch
go test -count=1 ./...
```

**Current benchmark:**
```
$ time ./build.sh
go build -v ./...
# rebuilds ALL packages from scratch

real    1m42.318s
user    2m15.440s
sys     0m12.830s
```

<details>
<summary>💡 Hint</summary>

Go has a built-in build cache (`$GOPATH/pkg` and `$HOME/.cache/go-build`). Clearing it forces recompilation of every package, including standard library packages. Only clear the cache when you genuinely need a clean build (e.g., debugging build issues).

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — leverage Go's build cache

# Use a persistent cache directory in CI
export GOCACHE="/tmp/go-build-cache"

# Build with cache (only recompiles changed packages)
go build ./...

# Run tests (use cache for unchanged tests)
go test ./...
```

**What changed:**
- Removed `go clean -cache` — the build cache is your friend, not your enemy
- Removed `-count=1` from tests — allows test result caching
- Removed `-v` flag — verbose output slows down builds with many packages
- Set persistent `GOCACHE` directory — survives between CI runs

**Optimized benchmark:**
```
$ time ./build.sh
# Only recompiles changed packages

real    0m08.214s      (first run: 1m42s, subsequent: 8s)
user    0m06.120s
sys     0m02.340s
```

**Improvement:** 12.5x faster on subsequent builds, ~95% reduction in build time

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Go's build cache stores compiled packages indexed by their source content hash. When source files haven't changed, the compiler reuses the cached object files instead of recompiling. The cache handles invalidation automatically — if any dependency changes, affected packages are recompiled.

**When to apply:** Always in CI/CD pipelines, development workflows, and anywhere builds run repeatedly. Most CI systems (GitHub Actions, GitLab CI) support caching `$GOCACHE` and `$GOMODCACHE` between runs.

**When NOT to apply:** When debugging compiler bugs, investigating non-deterministic build issues, or when you need to verify that the build works from a completely clean state (e.g., release verification builds).

</details>

---

## Exercise 2: Test Cache Invalidation Abuse 🟢 💾

**What the code does:** Runs the full test suite in a Go project with 200+ test functions.

**The problem:** Using `-count=1` on every test run to "ensure fresh results" — even during local development iteration.

```bash
#!/bin/bash
# Slow version — always bypasses test cache

# Run all tests, never use cache
go test -count=1 -v ./...

# Run specific package tests
go test -count=1 -v ./internal/parser/...
go test -count=1 -v ./internal/lexer/...
go test -count=1 -v ./internal/codegen/...
```

**Current benchmark:**
```
$ time go test -count=1 -v ./...
ok   myproject/internal/parser    12.340s
ok   myproject/internal/lexer      4.210s
ok   myproject/internal/codegen    8.770s
ok   myproject/pkg/utils           2.130s
... (15 more packages)

real    0m52.480s
user    1m38.220s
sys     0m08.640s
```

<details>
<summary>💡 Hint</summary>

The `-count=1` flag is the idiomatic way to bypass test caching, but using it during development means you re-run ALL tests even when nothing changed. Go's test cache is content-addressed — it knows when source files change.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — let the test cache work for you

# Run all tests with caching (only re-runs tests for changed packages)
go test ./...

# Only use -count=1 when you specifically need uncached results
# e.g., tests that depend on external services or time
go test -count=1 ./internal/integration/...

# For flaky test investigation only:
# go test -count=3 ./internal/parser/... -run TestFlakyFunction
```

**What changed:**
- Removed `-count=1` from regular test runs — allows caching of passing tests
- Removed `-v` from default runs — verbose output adds I/O overhead and prevents caching
- Only use `-count=1` for integration tests that depend on external state
- Use targeted `-run` flag when investigating specific test issues

**Optimized benchmark:**
```
$ time go test ./...
ok   myproject/internal/parser    (cached)
ok   myproject/internal/lexer     (cached)
ok   myproject/internal/codegen    8.770s    # only this package changed
ok   myproject/pkg/utils          (cached)
... (15 more packages, 12 cached)

real    0m11.230s
user    0m14.440s
sys     0m03.120s
```

**Improvement:** 4.7x faster, only re-runs tests for packages with actual changes

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Go caches test results based on the content hash of the test binary, its inputs (source files, environment variables listed in the test), and command-line flags. If none of these change, the cached result is valid. The `-v` flag actually prevents caching because its output might differ between runs.

**When to apply:** During local development when iterating on code changes. Let the cache handle unchanged packages while you focus on the packages you're modifying.

**When NOT to apply:** For integration tests that depend on external services (databases, APIs), time-sensitive tests, or when you need to detect flaky tests. In these cases, `-count=1` is appropriate.

</details>

---

## Exercise 3: Module Dependency Bloat 🟢 📦

**What the code does:** Manages dependencies in a Go project's `go.mod` file.

**The problem:** The `go.mod` file has accumulated unused dependencies over months of development, increasing download time and build graph complexity.

```bash
#!/bin/bash
# Slow version — bloated dependency management

# go.mod has 87 direct dependencies, but only 52 are actually used
# go.sum has 340 entries

# Download all dependencies (including unused ones)
go mod download

# Build the project (compiler still processes unused module metadata)
go build ./...
```

```
# go.mod (excerpt — 35 unused dependencies remain)
require (
    github.com/gin-gonic/gin v1.9.1
    github.com/stretchr/testify v1.8.4
    github.com/sirupsen/logrus v1.9.3        // unused — switched to slog
    github.com/pkg/errors v0.9.1             // unused — switched to fmt.Errorf
    github.com/go-redis/redis/v8 v8.11.5     // unused — removed redis feature
    github.com/spf13/viper v1.16.0           // unused — switched to env vars
    // ... 31 more unused dependencies
)
```

**Current benchmark:**
```
$ time go mod download
real    0m28.410s    # downloads 87 direct + 253 indirect deps

$ du -sh $GOMODCACHE
1.2G    /home/user/go/pkg/mod

$ wc -l go.sum
340 go.sum
```

<details>
<summary>💡 Hint</summary>

`go mod tidy` removes unused dependencies from `go.mod` and `go.sum`. It also adds any missing dependencies. This reduces download time, build graph complexity, and potential security surface area.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — clean dependency management

# Step 1: Remove unused dependencies and add missing ones
go mod tidy

# Step 2: Verify the module graph is consistent
go mod verify

# Step 3: Download only what's needed
go mod download

# Step 4: Check for any remaining issues
go mod graph | wc -l    # should show fewer nodes

# Optional: vendor dependencies for reproducible builds
# go mod vendor
```

**What changed:**
- Added `go mod tidy` — removes 35 unused direct dependencies and their transitive deps
- Added `go mod verify` — ensures downloaded modules match expected checksums
- Reduced module graph by ~40% — fewer packages to resolve and download

**Optimized benchmark:**
```
$ go mod tidy
# Removed 35 direct dependencies, 89 indirect dependencies

$ time go mod download
real    0m14.220s    # downloads 52 direct + 164 indirect deps

$ du -sh $GOMODCACHE
680M    /home/user/go/pkg/mod

$ wc -l go.sum
216 go.sum
```

**Improvement:** 2x faster dependency download, 43% less disk usage, 36% fewer go.sum entries

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Every dependency in `go.mod` contributes to the module graph that Go must resolve. Unused dependencies still get downloaded, checksummed, and their module metadata is processed. Removing them reduces network I/O, disk usage, and build initialization time.

**When to apply:** Run `go mod tidy` regularly, especially after removing imports, refactoring packages, or upgrading dependencies. Add it to your CI pipeline as a lint check: `go mod tidy && git diff --exit-code go.mod go.sum`.

**When NOT to apply:** Be cautious in multi-module workspaces where dependencies might be shared. Always run tests after `go mod tidy` to ensure nothing was removed that's needed at runtime (e.g., blank imports for side effects like database drivers).

</details>

---

## Exercise 4: Test Parallelism Configuration 🟡 🔄

**What the code does:** Runs a test suite with 150 test functions across 20 packages.

**The problem:** Tests run with default parallelism settings, not taking advantage of available CPU cores for independent test packages.

```bash
#!/bin/bash
# Slow version — suboptimal parallelism settings

# Default: packages run in parallel, but tests within a package are sequential
# On a 16-core machine, this leaves many cores idle

# Run tests with default settings
go test ./...

# Each package's tests run sequentially with t.Parallel() not used
# No -parallel flag specified (defaults to GOMAXPROCS)
# No -p flag specified (defaults to GOMAXPROCS for package parallelism)
```

```go
// internal/parser/parser_test.go
package parser

import "testing"

// Slow version — all tests run sequentially within the package
func TestParseExpression(t *testing.T) {
    // Takes 2.1s — CPU-bound parsing
    result := Parse("complex expression")
    if result == nil { t.Fatal("expected result") }
}

func TestParseStatement(t *testing.T) {
    // Takes 1.8s — CPU-bound parsing
    result := Parse("complex statement")
    if result == nil { t.Fatal("expected result") }
}

func TestParseFunctionDecl(t *testing.T) {
    // Takes 3.2s — CPU-bound parsing
    result := Parse("func decl")
    if result == nil { t.Fatal("expected result") }
}

// ... 12 more independent test functions, total ~25s sequential
```

**Current benchmark:**
```
$ time go test ./...
ok   myproject/internal/parser     25.340s
ok   myproject/internal/lexer      12.180s
ok   myproject/internal/codegen    18.920s
... (17 more packages)

real    1m48.220s
user    1m52.110s
sys     0m06.340s
```

<details>
<summary>💡 Hint</summary>

Go has two levels of test parallelism: (1) `-p` controls how many packages are tested simultaneously, and (2) `-parallel` controls how many `t.Parallel()` tests run concurrently within a single package. You need to use `t.Parallel()` in your test code AND tune the flags.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — maximize parallelism for independent tests

# Use -p to control package-level parallelism (default is GOMAXPROCS)
# Use -parallel to control test-level parallelism within each package
go test -p 8 -parallel 4 ./...

# For CI with known core count:
# go test -p $(nproc) -parallel $(( $(nproc) / 2 )) ./...
```

```go
// internal/parser/parser_test.go
package parser

import "testing"

// Fast version — independent tests run in parallel
func TestParseExpression(t *testing.T) {
    t.Parallel() // Mark as safe for parallel execution
    result := Parse("complex expression")
    if result == nil { t.Fatal("expected result") }
}

func TestParseStatement(t *testing.T) {
    t.Parallel() // Mark as safe for parallel execution
    result := Parse("complex statement")
    if result == nil { t.Fatal("expected result") }
}

func TestParseFunctionDecl(t *testing.T) {
    t.Parallel() // Mark as safe for parallel execution
    result := Parse("func decl")
    if result == nil { t.Fatal("expected result") }
}

// ... all independent tests marked with t.Parallel()
```

**What changed:**
- Added `t.Parallel()` to all independent test functions — allows concurrent execution within a package
- Set `-p 8` — runs 8 packages simultaneously (tuned for 16-core machine)
- Set `-parallel 4` — allows 4 tests per package to run concurrently
- Total concurrent tests: up to 32 (8 packages x 4 tests each)

**Optimized benchmark:**
```
$ time go test -p 8 -parallel 4 ./...
ok   myproject/internal/parser      7.120s    # was 25.3s
ok   myproject/internal/lexer       4.830s    # was 12.2s
ok   myproject/internal/codegen     6.410s    # was 18.9s
... (17 more packages)

real    0m22.180s
user    2m01.340s     # more user time (parallel CPU usage)
sys     0m07.120s
```

**Improvement:** 4.9x faster wall-clock time, CPU utilization increased from ~25% to ~90%

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** By default, Go runs test packages in parallel (up to GOMAXPROCS packages at once), but tests within each package run sequentially unless explicitly marked with `t.Parallel()`. Adding `t.Parallel()` enables intra-package parallelism. The `-parallel` flag limits how many parallel tests run at once per package to prevent resource exhaustion.

**When to apply:** For CPU-bound tests that are independent of each other (no shared mutable state). Particularly effective in projects with many small, isolated test functions.

**When NOT to apply:** Tests that share global state, write to the same files, use the same database tables, or depend on execution order. Also be cautious with memory-heavy tests — running too many in parallel can cause OOM. For I/O-bound tests hitting the same service, excessive parallelism may cause rate limiting.

</details>

---

## Exercise 5: Build with Debug Information 🟡 📦

**What the code does:** Builds a Go binary for production deployment.

**The problem:** The default `go build` includes debug information, symbol tables, and DWARF data that inflates binary size.

```bash
#!/bin/bash
# Slow version — production build with unnecessary debug info

# Default build includes everything
go build -o myapp ./cmd/myapp

# Check the binary size
ls -lh myapp
# -rwxr-xr-x  1 user  staff  28M  myapp

# Deploy to 50 containers
docker build -t myapp:latest .
# Each container ships a 28MB binary
# Total registry storage: 50 x 28MB image layers = 1.4GB
```

**Current benchmark:**
```
$ go build -o myapp ./cmd/myapp
$ ls -lh myapp
-rwxr-xr-x  1 user  staff  28M  myapp

$ file myapp
myapp: ELF 64-bit LSB executable, x86-64, ..., not stripped

$ go tool nm myapp | wc -l
142387    # 142K symbols in binary

$ time docker push myapp:latest
real    0m34.210s
```

<details>
<summary>💡 Hint</summary>

The `-ldflags` option passes flags to the Go linker. The `-s` flag strips the symbol table, and `-w` strips DWARF debugging information. Combined with `-trimpath`, you can also remove local filesystem paths from the binary.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — lean production binary

# Strip debug info, symbols, and filesystem paths
go build -trimpath -ldflags="-s -w" -o myapp ./cmd/myapp

# Check the binary size
ls -lh myapp
# -rwxr-xr-x  1 user  staff  19M  myapp

# Optional: compress with UPX for even smaller binaries
# upx --best myapp
# -rwxr-xr-x  1 user  staff  6.8M  myapp

# Deploy to 50 containers
docker build -t myapp:latest .
# Each container ships a 19MB binary (or 6.8MB with UPX)
```

**What changed:**
- Added `-ldflags="-s -w"` — strips symbol table (-s) and DWARF info (-w), saving ~32% binary size
- Added `-trimpath` — removes local filesystem paths from the binary (security + reproducibility)
- Optional UPX compression — further reduces binary size by ~64% (trade-off: slower startup)

**Optimized benchmark:**
```
$ go build -trimpath -ldflags="-s -w" -o myapp ./cmd/myapp
$ ls -lh myapp
-rwxr-xr-x  1 user  staff  19M  myapp

$ file myapp
myapp: ELF 64-bit LSB executable, x86-64, ..., stripped

$ go tool nm myapp | wc -l
0    # no exported symbols

$ time docker push myapp:latest
real    0m22.140s
```

**Improvement:** 32% smaller binary (28MB to 19MB), 35% faster container push, improved security (no path leakage)

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Go binaries include debugging information (DWARF) and a symbol table by default, which is useful during development but unnecessary in production. The DWARF data alone can account for 20-30% of binary size. Stripping it reduces binary size, docker image layers, network transfer times, and cold start latency.

**When to apply:** All production builds, container images, and deployed binaries. The `-trimpath` flag is especially important for security (prevents leaking developer filesystem paths) and reproducible builds (same source produces identical binary regardless of build machine).

**When NOT to apply:** Development builds where you need `go tool pprof`, `dlv` (Delve debugger), or stack traces with full file paths. Never strip debug info from binaries you might need to debug in production — keep unstripped binaries in your artifact store alongside stripped ones.

</details>

---

## Exercise 6: Sequential Build Tags for Multiple Platforms 🟡 ⚡

**What the code does:** Cross-compiles a Go application for multiple OS/architecture combinations.

**The problem:** Each platform build runs sequentially, and the build cache is not effectively shared between GOOS/GOARCH combinations.

```bash
#!/bin/bash
# Slow version — sequential cross-compilation

PLATFORMS=(
    "linux/amd64"
    "linux/arm64"
    "darwin/amd64"
    "darwin/arm64"
    "windows/amd64"
    "windows/arm64"
)

for platform in "${PLATFORMS[@]}"; do
    IFS='/' read -r GOOS GOARCH <<< "$platform"
    echo "Building for $GOOS/$GOARCH..."

    # Each build starts from scratch, runs sequentially
    GOOS=$GOOS GOARCH=$GOARCH go build -o "dist/myapp-${GOOS}-${GOARCH}" ./cmd/myapp
done
```

**Current benchmark:**
```
$ time ./build-all.sh
Building for linux/amd64...    (18.2s)
Building for linux/arm64...    (22.1s)
Building for darwin/amd64...   (19.8s)
Building for darwin/arm64...   (21.3s)
Building for windows/amd64...  (20.4s)
Building for windows/arm64...  (23.7s)

real    2m05.500s
user    2m12.340s
sys     0m14.220s
```

<details>
<summary>💡 Hint</summary>

Cross-compilation jobs are independent of each other. They can run in parallel using shell background processes or `xargs`. Additionally, pure Go packages (no cgo) share compiled output across architectures at the AST/type-checking level in the build cache.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — parallel cross-compilation with shared cache

PLATFORMS=(
    "linux/amd64"
    "linux/arm64"
    "darwin/amd64"
    "darwin/arm64"
    "windows/amd64"
    "windows/arm64"
)

# Ensure output directory exists
mkdir -p dist

# Pre-compile shared dependencies (platform-independent analysis)
go build -v ./cmd/myapp 2>/dev/null

# Run all cross-compilations in parallel
PIDS=()
for platform in "${PLATFORMS[@]}"; do
    IFS='/' read -r GOOS GOARCH <<< "$platform"
    (
        GOOS=$GOOS GOARCH=$GOARCH CGO_ENABLED=0 \
        go build -trimpath -ldflags="-s -w" \
            -o "dist/myapp-${GOOS}-${GOARCH}" ./cmd/myapp
    ) &
    PIDS+=($!)
done

# Wait for all builds to complete
FAILED=0
for pid in "${PIDS[@]}"; do
    wait "$pid" || FAILED=$((FAILED + 1))
done

if [ $FAILED -gt 0 ]; then
    echo "ERROR: $FAILED build(s) failed"
    exit 1
fi

echo "All builds completed successfully"
ls -lh dist/
```

**What changed:**
- Parallelized builds using background processes (`&`) — all 6 platforms build concurrently
- Added `CGO_ENABLED=0` — disables cgo for pure Go cross-compilation (faster, no C toolchain needed)
- Added `-trimpath -ldflags="-s -w"` — smaller binaries, reproducible builds
- Pre-compile step warms the build cache for shared Go source analysis
- Proper error handling with PID tracking

**Optimized benchmark:**
```
$ time ./build-all.sh
# All 6 platforms build concurrently

real    0m26.340s
user    2m18.440s     # total CPU time similar, but wall-clock much lower
sys     0m16.120s
```

**Improvement:** 4.7x faster wall-clock time (2m05s to 26s), same CPU usage distributed across cores

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Cross-compilation for different GOOS/GOARCH targets is embarrassingly parallel — each build is independent. By running them concurrently, the total wall-clock time approaches the time of the single slowest build rather than the sum of all builds. Setting `CGO_ENABLED=0` avoids requiring platform-specific C toolchains and enables pure Go compilation.

**When to apply:** Any CI/CD pipeline that builds for multiple platforms. Works best on machines with sufficient CPU cores and memory (each build uses ~1-2 cores and 200-500MB RAM).

**When NOT to apply:** When builds require cgo (e.g., SQLite bindings, system libraries) — you'll need platform-specific cross-compilation toolchains. Also be cautious with memory on constrained CI runners — 6 concurrent builds may need 3GB+ RAM. Reduce parallelism with `xargs -P 4` if memory is limited.

</details>

---

## Exercise 7: Inefficient Test Coverage Collection 🟡 💾

**What the code does:** Collects code coverage data for a Go project with 20+ packages.

**The problem:** Running coverage for each package separately and merging results creates excessive I/O and redundant test execution.

```bash
#!/bin/bash
# Slow version — per-package coverage with manual merging

mkdir -p coverage

# Run coverage for each package individually
PACKAGES=$(go list ./...)
for pkg in $PACKAGES; do
    PKG_NAME=$(echo "$pkg" | tr '/' '-')

    # Each package runs its own coverage profile
    go test -coverprofile="coverage/${PKG_NAME}.out" \
            -covermode=atomic \
            "$pkg"
done

# Merge all coverage files manually
echo "mode: atomic" > coverage/total.out
for f in coverage/*.out; do
    tail -n +2 "$f" >> coverage/total.out
done

# Generate HTML report
go tool cover -html=coverage/total.out -o coverage/report.html

# Generate function coverage
go tool cover -func=coverage/total.out
```

**Current benchmark:**
```
$ time ./coverage.sh
ok   myproject/internal/parser     12.340s   coverage: 78.2%
ok   myproject/internal/lexer       4.210s   coverage: 92.1%
ok   myproject/internal/codegen     8.770s   coverage: 65.4%
... (18 more packages)

# File I/O: 21 individual .out files written and read
# Total coverage files: 2.4MB across 21 files

real    1m38.220s
user    1m45.110s
sys     0m12.340s
```

<details>
<summary>💡 Hint</summary>

Since Go 1.20, `go test` supports the `-coverpkg` flag with `./...` and can collect coverage across all packages in a single invocation. Also, the `go tool covdata` command can merge binary coverage data more efficiently than text processing.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — single-pass coverage collection

mkdir -p coverage

# Collect coverage across ALL packages in a single invocation
# -coverpkg=./... ensures coverage is tracked across package boundaries
go test -coverprofile=coverage/total.out \
        -covermode=atomic \
        -coverpkg=./... \
        -p 4 \
        ./...

# Generate HTML report directly
go tool cover -html=coverage/total.out -o coverage/report.html

# Generate function coverage summary
go tool cover -func=coverage/total.out | tail -1

# Alternative: Use Go 1.20+ binary coverage format for even faster processing
# go test -cover -covermode=atomic -coverpkg=./... \
#         -args -test.gocoverdir=coverage/binary ./...
# go tool covdata textfmt -i=coverage/binary -o=coverage/total.out
```

**What changed:**
- Single `go test` invocation with `-coverprofile` — replaces per-package loop
- Added `-coverpkg=./...` — tracks cross-package coverage (function in package A tested by package B)
- Eliminated manual file merging — single output file, no shell scripting needed
- Added `-p 4` — parallel package testing during coverage collection
- Reduced I/O from 21 files to 1 file

**Optimized benchmark:**
```
$ time ./coverage.sh
ok   myproject/internal/parser     12.340s
ok   myproject/internal/lexer       4.210s
... (packages run in parallel with -p 4)

# Single coverage file: 180KB (vs 2.4MB across 21 files)
# Cross-package coverage captured (higher accuracy)

real    0m34.120s
user    1m42.440s
sys     0m04.220s
```

**Improvement:** 2.9x faster, 93% less I/O (21 files to 1), more accurate cross-package coverage

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Running coverage per-package in a loop has three problems: (1) it serializes execution, (2) each invocation starts a new `go test` process with its own overhead, and (3) it misses cross-package coverage (when package A's tests exercise code in package B). A single `go test -coverpkg=./... ./...` invocation runs all packages with shared coverage tracking.

**When to apply:** Any project that needs full coverage reports — CI/CD pipelines, pre-merge checks, coverage badge generation. The `-coverpkg=./...` flag is especially important for projects with integration tests in separate packages.

**When NOT to apply:** When you need per-package coverage thresholds (e.g., "package X must have >80% coverage"), you may still need individual coverage runs. Also, very large monorepos with 500+ packages may hit memory limits with `-coverpkg=./...` — in that case, use coverage groups.

</details>

---

## Exercise 8: Unoptimized go test -short for CI Pipelines 🟡 ⚡

**What the code does:** Runs the full test suite including slow integration tests in a CI fast-feedback pipeline.

**The problem:** The CI pipeline runs ALL tests (unit + integration + e2e) on every commit, delaying feedback to developers.

```go
// internal/database/db_test.go
package database

import (
    "testing"
    "time"
)

// Slow version — no separation between fast and slow tests

func TestDBConnection(t *testing.T) {
    // Unit test — fast (10ms)
    db := NewMockDB()
    if err := db.Ping(); err != nil {
        t.Fatal(err)
    }
}

func TestDBMigration(t *testing.T) {
    // Integration test — slow (15s, needs real database)
    db := ConnectToTestDB()
    defer db.Close()
    if err := RunMigrations(db); err != nil {
        t.Fatal(err)
    }
}

func TestDBLoadTest(t *testing.T) {
    // Load test — very slow (60s)
    db := ConnectToTestDB()
    defer db.Close()
    for i := 0; i < 10000; i++ {
        db.Insert(generateRecord())
    }
    time.Sleep(5 * time.Second) // wait for async processing
}

func TestDBBackupRestore(t *testing.T) {
    // E2E test — extremely slow (120s)
    db := ConnectToTestDB()
    defer db.Close()
    backup := db.CreateBackup()
    db.DropAll()
    db.RestoreBackup(backup)
}
```

```bash
#!/bin/bash
# Slow version — CI runs everything
go test -v ./...
```

**Current benchmark:**
```
$ time go test -v ./...
=== RUN   TestDBConnection          (0.01s)
=== RUN   TestDBMigration           (15.23s)
=== RUN   TestDBLoadTest            (62.18s)
=== RUN   TestDBBackupRestore       (118.44s)
... (more packages)

real    4m12.340s
user    3m48.220s
sys     0m18.110s
```

<details>
<summary>💡 Hint</summary>

Go has a built-in convention for skipping slow tests: `testing.Short()`. Tests can check if `-short` flag is set and skip themselves. This lets you have two CI stages: fast feedback (unit tests only) and full validation (all tests).

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
// internal/database/db_test.go
package database

import (
    "testing"
    "time"
)

// Fast version — tests self-classify using testing.Short()

func TestDBConnection(t *testing.T) {
    // Unit test — always runs (fast)
    db := NewMockDB()
    if err := db.Ping(); err != nil {
        t.Fatal(err)
    }
}

func TestDBMigration(t *testing.T) {
    if testing.Short() {
        t.Skip("skipping integration test in short mode")
    }
    // Integration test — only runs in full mode
    db := ConnectToTestDB()
    defer db.Close()
    if err := RunMigrations(db); err != nil {
        t.Fatal(err)
    }
}

func TestDBLoadTest(t *testing.T) {
    if testing.Short() {
        t.Skip("skipping load test in short mode")
    }
    // Load test — only runs in full mode
    db := ConnectToTestDB()
    defer db.Close()
    for i := 0; i < 10000; i++ {
        db.Insert(generateRecord())
    }
    time.Sleep(5 * time.Second)
}

func TestDBBackupRestore(t *testing.T) {
    if testing.Short() {
        t.Skip("skipping e2e test in short mode")
    }
    // E2E test — only runs in full mode
    db := ConnectToTestDB()
    defer db.Close()
    backup := db.CreateBackup()
    db.DropAll()
    db.RestoreBackup(backup)
}
```

```bash
#!/bin/bash
# Fast CI stage — runs on every commit (< 30 seconds)
go test -short ./...

# Full CI stage — runs on PR merge or nightly
# go test -v ./...
```

**What changed:**
- Added `testing.Short()` guard to all slow tests — they skip in short mode
- CI fast-feedback stage uses `-short` flag — runs only unit tests
- Full test suite still runs on PR merge or nightly schedule
- Developers get feedback in seconds, not minutes

**Optimized benchmark:**
```
$ time go test -short ./...
=== RUN   TestDBConnection          (0.01s)
--- SKIP: TestDBMigration           (0.00s)
--- SKIP: TestDBLoadTest            (0.00s)
--- SKIP: TestDBBackupRestore       (0.00s)
... (more packages, integration tests skipped)

real    0m08.340s
user    0m12.110s
sys     0m03.220s
```

**Improvement:** 30x faster CI feedback (4m12s to 8s), full coverage still runs on merge

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** The `-short` flag is a built-in Go testing convention. When `testing.Short()` returns true, tests that call `t.Skip()` are skipped but still reported. This creates a natural two-tier testing strategy: fast unit tests for immediate feedback, and full integration/e2e tests for thorough validation.

**When to apply:** Any project with mixed test types (unit, integration, e2e). Implement a CI pipeline with two stages: (1) fast feedback on every push using `-short`, (2) full validation on PR merge or nightly. This dramatically improves developer experience.

**When NOT to apply:** If all your tests are fast (< 1s each), the overhead of `-short` classification is unnecessary. Also, some teams prefer build tags (`//go:build integration`) over `-short` for more granular control over test categories.

</details>

---

## Exercise 8: GOGC Tuning for Large Builds 🔴 📦

**What the code does:** Compiles a large Go monorepo with 500+ packages in CI.

**The problem:** The Go garbage collector runs frequently during compilation, consuming up to 30% of build time on large projects. Default GOGC=100 triggers GC too aggressively when the compiler allocates large amounts of memory.

```bash
#!/bin/bash
# Slow version — default GC settings for large build

# Default GOGC=100 means GC runs when heap doubles
# For a large project, the compiler allocates 2-4GB and GC runs hundreds of times

# Build the entire monorepo
go build ./...

# Run all tests
go test ./...
```

**Current benchmark:**
```
$ GODEBUG=gctrace=1 go build ./... 2>&1 | grep -c "gc "
347    # GC ran 347 times during compilation

$ time go build ./...
real    3m42.180s
user    12m18.440s    # high user time due to GC across multiple cores
sys     0m28.340s

$ /usr/bin/time -v go build ./... 2>&1 | grep "Maximum resident"
Maximum resident set size (kbytes): 2841620    # 2.8GB peak memory
```

**Profiling output:**
```
$ GODEBUG=gctrace=1 go build ./... 2>&1 | tail -5
gc 343 @198.234s 4%: 0.12+45.23+0.084 ms clock, 1.9+180.9/89.2/12.1+1.3 ms cpu, 2412->2487->1284 MB, 2568 MB goal
gc 344 @199.112s 4%: 0.11+42.18+0.076 ms clock, 1.8+168.7/84.1/11.4+1.2 ms cpu, 2389->2461->1271 MB, 2568 MB goal
gc 345 @199.987s 4%: 0.13+44.87+0.081 ms clock, 2.1+179.5/89.7/12.3+1.3 ms cpu, 2401->2478->1279 MB, 2568 MB goal
# 4% of total time spent in GC, with 40-45ms pauses
```

<details>
<summary>💡 Hint</summary>

`GOGC` controls how aggressively the GC runs. `GOGC=100` (default) means GC triggers when the heap grows to 2x the live data. Setting `GOGC=200` or higher reduces GC frequency at the cost of higher memory usage. Go 1.19+ also supports `GOMEMLIMIT` for a memory-based GC trigger. For build processes, memory is usually plentiful and CPU is the bottleneck.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — tuned GC for large builds

# Option 1: Increase GOGC to reduce GC frequency
# GOGC=300 means GC triggers when heap grows to 4x live data
export GOGC=300

# Option 2 (Go 1.19+): Use GOMEMLIMIT for memory-based GC control
# Set to 80% of available memory to prevent OOM while reducing GC
export GOMEMLIMIT=6GiB    # on an 8GB CI runner

# Option 3: For maximum build speed with plenty of RAM
# GOGC=off disables GC entirely (use only with GOMEMLIMIT!)
# export GOGC=off
# export GOMEMLIMIT=12GiB

# Build with tuned GC
go build ./...

# Tests also benefit from GOGC tuning
go test ./...

# Reset for normal operation
unset GOGC
unset GOMEMLIMIT
```

**What changed:**
- Set `GOGC=300` — reduces GC frequency by 3x (triggers at 4x live data instead of 2x)
- Added `GOMEMLIMIT=6GiB` — provides a memory ceiling so GC still runs if needed
- Trade-off: uses ~40% more peak memory but runs GC 70% less frequently
- GC pauses reduced from 347 to ~115 cycles

**Optimized benchmark:**
```
$ GOGC=300 GOMEMLIMIT=6GiB GODEBUG=gctrace=1 go build ./... 2>&1 | grep -c "gc "
115    # GC ran 115 times (was 347)

$ time GOGC=300 GOMEMLIMIT=6GiB go build ./...
real    2m51.220s
user    9m42.110s     # 21% less CPU time
sys     0m24.180s

$ GOGC=300 GOMEMLIMIT=6GiB /usr/bin/time -v go build ./... 2>&1 | grep "Maximum resident"
Maximum resident set size (kbytes): 3945840    # 3.9GB peak (was 2.8GB)
```

**Improvement:** 1.3x faster build (3m42s to 2m51s), 67% fewer GC cycles, trade-off: 39% more peak memory

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** The Go garbage collector uses a concurrent, tri-color mark-and-sweep algorithm. Each GC cycle has three phases: (1) mark setup (STW), (2) concurrent marking, and (3) mark termination (STW). While concurrent marking runs alongside your code, it still consumes CPU cores that could be used for compilation. By increasing `GOGC`, you reduce the number of GC cycles, freeing those CPU cores for actual work.

**Go source reference:** The `GOGC` and `GOMEMLIMIT` interaction is defined in `runtime/mgc.go`. The soft memory limit in Go 1.19+ uses `runtime/debug.SetMemoryLimit()` to trigger GC only when approaching the memory limit, even with `GOGC=off`.

**When to apply:** Large builds on CI runners with ample memory (8GB+). Also effective for `go generate`, `go vet`, and other toolchain commands that process many packages. Test with `GODEBUG=gctrace=1` to measure actual GC overhead before tuning.

**When NOT to apply:** Memory-constrained environments (e.g., 2GB CI runners), production applications where memory predictability matters, or when running alongside other memory-hungry processes. Never use `GOGC=off` without `GOMEMLIMIT` — it can cause OOM kills.

</details>

---

## Exercise 9: Unoptimized go generate Pipeline 🔴 ⚡

**What the code does:** Runs code generation for a project that uses protobuf, mock generation, and stringer across 30+ packages.

**The problem:** Each `go generate` directive runs sequentially, and generators are invoked per-file instead of batched. The pipeline re-generates everything even when source files haven't changed.

```go
// api/proto/user.go
//go:generate protoc --go_out=. --go-grpc_out=. user.proto
//go:generate protoc --go_out=. --go-grpc_out=. order.proto
//go:generate protoc --go_out=. --go-grpc_out=. product.proto
//go:generate protoc --go_out=. --go-grpc_out=. payment.proto
//go:generate protoc --go_out=. --go-grpc_out=. shipping.proto
```

```go
// internal/service/user_service.go
//go:generate mockgen -source=user_service.go -destination=mock_user_service.go -package=service
```

```go
// internal/model/status.go
//go:generate stringer -type=Status
//go:generate stringer -type=OrderStatus
//go:generate stringer -type=PaymentStatus
```

```bash
#!/bin/bash
# Slow version — regenerate everything every time

# Clean all generated files first (wasteful!)
find . -name "*_mock.go" -delete
find . -name "*.pb.go" -delete
find . -name "*_string.go" -delete

# Regenerate everything
go generate ./...
```

**Current benchmark:**
```
$ time go generate ./...
# protoc invoked 5 times (one per .proto file)
# mockgen invoked 12 times (one per interface)
# stringer invoked 8 times (one per type)
# Total: 25 generator invocations

real    1m18.440s
user    0m52.110s
sys     0m14.220s
```

<details>
<summary>💡 Hint</summary>

Batch protoc calls to process multiple `.proto` files at once. Use `mockgen` in reflect mode with multiple interfaces. Add a Makefile-style change detection to skip generation when source files haven't changed. Consider using `buf` instead of raw `protoc` for proto generation.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — batched, cached, parallel code generation

set -euo pipefail

CACHE_DIR=".generate-cache"
mkdir -p "$CACHE_DIR"

# Function: check if source file changed since last generation
needs_regen() {
    local src="$1"
    local cache_file="$CACHE_DIR/$(echo "$src" | tr '/' '_').hash"
    local current_hash=$(sha256sum "$src" | cut -d' ' -f1)

    if [ -f "$cache_file" ] && [ "$(cat "$cache_file")" = "$current_hash" ]; then
        return 1  # no regeneration needed
    fi
    echo "$current_hash" > "$cache_file"
    return 0  # needs regeneration
}

# Step 1: Batch protobuf generation (single protoc invocation)
PROTO_FILES=()
for proto in api/proto/*.proto; do
    if needs_regen "$proto"; then
        PROTO_FILES+=("$proto")
    fi
done

if [ ${#PROTO_FILES[@]} -gt 0 ]; then
    echo "Generating protobuf for ${#PROTO_FILES[@]} files..."
    protoc --go_out=. --go-grpc_out=. "${PROTO_FILES[@]}" &
    PROTO_PID=$!
else
    echo "Protobuf: no changes detected, skipping"
    PROTO_PID=""
fi

# Step 2: Batch mock generation
MOCK_CHANGED=false
for src in internal/service/*_service.go; do
    if needs_regen "$src"; then
        MOCK_CHANGED=true
        break
    fi
done

if [ "$MOCK_CHANGED" = true ]; then
    echo "Generating mocks..."
    # Use mockgen with multiple source files
    go run go.uber.org/mock/mockgen@latest \
        -source=internal/service/user_service.go \
        -destination=internal/service/mock_service.go \
        -package=service &
    MOCK_PID=$!
else
    echo "Mocks: no changes detected, skipping"
    MOCK_PID=""
fi

# Step 3: Batch stringer generation
STRINGER_CHANGED=false
for src in internal/model/*.go; do
    if needs_regen "$src"; then
        STRINGER_CHANGED=true
        break
    fi
done

if [ "$STRINGER_CHANGED" = true ]; then
    echo "Generating stringers..."
    go run golang.org/x/tools/cmd/stringer@latest \
        -type=Status,OrderStatus,PaymentStatus \
        ./internal/model/ &
    STRINGER_PID=$!
else
    echo "Stringers: no changes detected, skipping"
    STRINGER_PID=""
fi

# Wait for all parallel generators
for pid in $PROTO_PID $MOCK_PID $STRINGER_PID; do
    [ -n "$pid" ] && wait "$pid"
done

echo "Code generation complete"
```

**What changed:**
- Batched protoc — single invocation processes all `.proto` files (5 invocations reduced to 1)
- Batched stringer — single invocation with `-type=A,B,C` processes all types (8 reduced to 1)
- Added change detection — SHA-256 hashing skips unchanged files
- Parallelized generators — protoc, mockgen, stringer run concurrently
- Total invocations: 25 reduced to 3 (or 0 when nothing changed)

**Optimized benchmark:**
```
# First run (all files changed):
$ time ./generate.sh
Generating protobuf for 5 files...
Generating mocks...
Generating stringers...
Code generation complete

real    0m12.340s     # was 1m18s
user    0m28.110s
sys     0m05.220s

# Subsequent run (no changes):
$ time ./generate.sh
Protobuf: no changes detected, skipping
Mocks: no changes detected, skipping
Stringers: no changes detected, skipping
Code generation complete

real    0m00.340s     # instant!
```

**Improvement:** 6.3x faster (first run), instant when no changes, 88% fewer tool invocations

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** `go generate` is intentionally simple — it just runs commands found in `//go:generate` comments. It has no built-in caching, dependency tracking, or parallelism. For production projects, a build script or Makefile that adds these features provides dramatically better performance. Tools like `buf` (for protobuf) and `go run` (for pinned tool versions) also improve reproducibility.

**Go source reference:** The `go generate` implementation is in `cmd/go/internal/generate/generate.go`. It processes files sequentially within each package and packages sequentially by default.

**When to apply:** Any project with more than 5 `//go:generate` directives, especially those using protoc, mockgen, or other slow generators. The change detection pattern is particularly valuable in CI where most commits only change a few files.

**When NOT to apply:** Small projects with 1-2 simple generators where the overhead of the build script exceeds the time saved. Also, be cautious with change detection when generators depend on each other — if mock generation depends on protobuf output, they must run sequentially.

</details>

---

## Exercise 10: Build Tag Matrix Optimization 🔴 🔄

**What the code does:** Tests a library that supports multiple build configurations using Go build tags (e.g., different storage backends, encryption modes, platform features).

**The problem:** The CI pipeline tests every possible combination of build tags, resulting in a combinatorial explosion of test runs.

```bash
#!/bin/bash
# Slow version — test every tag combination (combinatorial explosion)

STORAGE_TAGS=("sqlite" "postgres" "mysql")
CACHE_TAGS=("redis" "memcached" "inmemory")
CRYPTO_TAGS=("openssl" "boringcrypto" "standard")

# Test ALL combinations: 3 x 3 x 3 = 27 test runs!
for storage in "${STORAGE_TAGS[@]}"; do
    for cache in "${CACHE_TAGS[@]}"; do
        for crypto in "${CRYPTO_TAGS[@]}"; do
            echo "Testing: storage=$storage cache=$cache crypto=$crypto"

            go test -tags "$storage,$cache,$crypto" -count=1 ./...
        done
    done
done
```

```go
// internal/storage/store.go

//go:build sqlite

package storage

// SQLite implementation...

// internal/storage/store_postgres.go

//go:build postgres

package storage

// PostgreSQL implementation...
```

**Current benchmark:**
```
$ time ./test-matrix.sh
Testing: storage=sqlite cache=redis crypto=openssl         (48.2s)
Testing: storage=sqlite cache=redis crypto=boringcrypto    (47.8s)
Testing: storage=sqlite cache=redis crypto=standard        (46.1s)
Testing: storage=sqlite cache=memcached crypto=openssl     (49.3s)
... (23 more combinations)

# 27 full test runs, each taking ~48 seconds
real    21m34.180s
user    28m12.440s
sys     4m08.220s
```

<details>
<summary>💡 Hint</summary>

Not all tag combinations interact — storage, cache, and crypto are independent subsystems. Instead of testing all 27 combinations, you can test each tag independently (3+3+3 = 9 runs) and only test critical cross-cutting combinations. This is called "pairwise testing" or "orthogonal array testing." Additionally, use `-run` to only run tests relevant to each tag.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
#!/bin/bash
# Fast version — smart tag matrix with pairwise coverage

set -euo pipefail

RESULTS_DIR="test-results"
mkdir -p "$RESULTS_DIR"
PIDS=()
FAILED=0

run_test() {
    local name="$1"
    local tags="$2"
    local run_filter="${3:-}"

    local args=(-tags "$tags" -count=1)
    [ -n "$run_filter" ] && args+=(-run "$run_filter")

    echo "Testing: $name (tags: $tags)"
    if go test "${args[@]}" ./... > "$RESULTS_DIR/$name.log" 2>&1; then
        echo "  PASS: $name"
    else
        echo "  FAIL: $name"
        return 1
    fi
}

# Phase 1: Test each tag independently (runs in parallel)
# This catches single-tag bugs: 3 + 3 + 3 = 9 runs (not 27)
echo "=== Phase 1: Independent tag testing ==="

# Storage backends (test only storage-related tests)
for tag in sqlite postgres mysql; do
    run_test "storage-$tag" "$tag" "TestStorage|TestDB|TestStore" &
    PIDS+=($!)
done

# Cache backends (test only cache-related tests)
for tag in redis memcached inmemory; do
    run_test "cache-$tag" "$tag" "TestCache|TestSession" &
    PIDS+=($!)
done

# Crypto backends (test only crypto-related tests)
for tag in openssl boringcrypto standard; do
    run_test "crypto-$tag" "$tag" "TestCrypto|TestEncrypt|TestHash" &
    PIDS+=($!)
done

# Wait for Phase 1
for pid in "${PIDS[@]}"; do
    wait "$pid" || FAILED=$((FAILED + 1))
done
PIDS=()

echo ""
echo "=== Phase 2: Critical cross-cutting combinations ==="

# Phase 2: Test only combinations that are known to interact
# Based on pairwise testing — cover all pairs with minimum combinations
CRITICAL_COMBOS=(
    "sqlite,redis,standard"          # default/common combo
    "postgres,memcached,openssl"     # production combo
    "mysql,inmemory,boringcrypto"    # alternative combo
    "postgres,redis,boringcrypto"    # high-security production
)

for combo in "${CRITICAL_COMBOS[@]}"; do
    combo_name=$(echo "$combo" | tr ',' '-')
    run_test "combo-$combo_name" "$combo" &
    PIDS+=($!)
done

# Wait for Phase 2
for pid in "${PIDS[@]}"; do
    wait "$pid" || FAILED=$((FAILED + 1))
done

echo ""
echo "=== Results ==="
echo "Total test configurations: 13 (was 27)"
echo "Failed: $FAILED"

if [ $FAILED -gt 0 ]; then
    echo "Check logs in $RESULTS_DIR/ for details"
    exit 1
fi
```

**What changed:**
- Replaced 27 combinatorial runs with 13 strategic runs (9 independent + 4 critical combos)
- Phase 1: Independent testing with `-run` filters — only runs relevant tests per tag
- Phase 2: Pairwise critical combinations — catches interaction bugs between subsystems
- All test runs execute in parallel within each phase — maximizes CPU utilization
- Added structured logging and result tracking

**Optimized benchmark:**
```
$ time ./test-matrix.sh
=== Phase 1: Independent tag testing ===
Testing: storage-sqlite    (tags: sqlite)       # 12.1s (filtered to storage tests)
Testing: storage-postgres  (tags: postgres)      # 14.3s
Testing: storage-mysql     (tags: mysql)         # 13.8s
Testing: cache-redis       (tags: redis)         # 8.2s
Testing: cache-memcached   (tags: memcached)     # 7.9s
Testing: cache-inmemory    (tags: inmemory)       # 5.1s
Testing: crypto-openssl    (tags: openssl)        # 6.4s
Testing: crypto-boringcrypto (tags: boringcrypto) # 6.8s
Testing: crypto-standard   (tags: standard)      # 5.9s
# Phase 1 wall-clock: ~14.3s (all 9 run in parallel)

=== Phase 2: Critical cross-cutting combinations ===
Testing: combo-sqlite-redis-standard              # 48.2s
Testing: combo-postgres-memcached-openssl          # 51.3s
Testing: combo-mysql-inmemory-boringcrypto          # 44.7s
Testing: combo-postgres-redis-boringcrypto          # 49.8s
# Phase 2 wall-clock: ~51.3s (all 4 run in parallel)

Total test configurations: 13 (was 27)
Failed: 0

real    1m08.220s     # was 21m34s
user    8m42.110s
sys     1m12.340s
```

**Improvement:** 19x faster (21m34s to 1m08s), 52% fewer test configurations, maintains >95% bug detection coverage through pairwise testing

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** Pairwise testing (also called "all-pairs testing") is a combinatorial test design technique based on the observation that most software bugs are triggered by interactions between at most two factors. By ensuring every pair of tag values appears in at least one test configuration, you achieve high defect detection rates with far fewer tests. Research shows pairwise testing catches 70-90% of interaction bugs with only a fraction of the full combinatorial matrix.

**Go source reference:** Build tags are processed in `cmd/go/internal/load/pkg.go`. The `//go:build` constraint syntax (Go 1.17+) uses boolean expressions that the build system evaluates to determine which files to include. Understanding this helps design efficient tag combinations.

**When to apply:** Any project with multiple independent build tag dimensions (backends, platforms, feature flags). The savings grow exponentially with more dimensions: 4 dimensions with 3 values each = 81 combinations full vs ~16 pairwise.

**When NOT to apply:** When tag dimensions are NOT independent (e.g., certain cache backends only work with certain storage backends). In such cases, you need to test the specific valid combinations rather than assuming independence. Also, for safety-critical software, full combinatorial coverage may be required by regulations.

</details>

---

## Score Card

Track your progress:

| Exercise | Difficulty | Category | Found bottleneck? | Your improvement | Target improvement |
|:--------:|:---------:|:--------:|:-----------------:|:----------------:|:-----------------:|
| 1 | 🟢 | ⚡ | ☐ | ___ x | 12.5x |
| 2 | 🟢 | 💾 | ☐ | ___ x | 4.7x |
| 3 | 🟢 | 📦 | ☐ | ___ x | 2.0x |
| 4 | 🟡 | 🔄 | ☐ | ___ x | 4.9x |
| 5 | 🟡 | 📦 | ☐ | ___ x | 1.5x |
| 6 | 🟡 | ⚡ | ☐ | ___ x | 4.7x |
| 7 | 🟡 | 💾 | ☐ | ___ x | 2.9x |
| 8 | 🟡 | ⚡ | ☐ | ___ x | 30x |
| 9 | 🔴 | 📦 | ☐ | ___ x | 1.3x |
| 10 | 🔴 | ⚡ | ☐ | ___ x | 6.3x |
| 11 | 🔴 | 🔄 | ☐ | ___ x | 19x |

### Rating:
- **All targets met** → You understand Go toolchain performance deeply
- **8-10 targets met** → Solid optimization skills with go commands
- **5-7 targets met** → Good foundation, practice CI/CD optimization more
- **< 5 targets met** → Start with `go help build` and build cache basics

---

## Optimization Cheat Sheet

Quick reference for common Go toolchain optimizations:

| Problem | Solution | Impact |
|:--------|:---------|:------:|
| Full rebuild every time | Preserve `GOCACHE` between CI runs | High |
| Tests re-run when nothing changed | Remove `-count=1` from dev workflows | High |
| Bloated `go.mod` with unused deps | Run `go mod tidy` regularly | Medium |
| Sequential tests on multi-core | Use `t.Parallel()` + `-parallel` flag | High |
| Large production binary | Use `-ldflags="-s -w"` to strip debug info | Medium |
| Sequential cross-compilation | Parallelize with background processes | High |
| Per-package coverage collection | Single `go test -coverpkg=./...` invocation | Medium-High |
| Slow CI feedback loop | Use `go test -short` for fast feedback stage | High |
| GC overhead during large builds | Set `GOGC=300` + `GOMEMLIMIT` | Medium |
| Sequential code generation | Batch generators + change detection + parallelism | High |
| Combinatorial build tag testing | Pairwise testing + parallel execution | High |
| Verbose test output in CI | Remove `-v` flag (it prevents caching) | Medium |
| Downloading deps every CI run | Cache `$GOMODCACHE` between runs | High |
| Slow `go vet` on large codebase | Run only on changed packages: `go vet ./changed/...` | Medium |
