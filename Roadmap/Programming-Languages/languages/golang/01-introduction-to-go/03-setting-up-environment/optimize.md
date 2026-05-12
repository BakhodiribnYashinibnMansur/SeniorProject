# Setting up the Go Environment — Optimize the Code

> **Practice optimizing slow, inefficient, or resource-heavy Go build pipelines, Docker images, and CI/CD configurations related to Setting up the Go Environment.**
> Each exercise contains working but suboptimal code — your job is to make it faster, leaner, or more efficient.

---

## How to Use

1. Read the slow code and understand what it does
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

## Exercise 1: Leveraging the Go Build Cache 🟢 ⚡

**What the code does:** A Makefile target that builds a Go binary from scratch every time.

**The problem:** The build runs `go clean -cache` before every build, discarding all cached compilation artifacts. Every single package is recompiled from scratch on every invocation.

```makefile
# Slow version — Makefile that clears cache before every build
.PHONY: build

build:
	go clean -cache
	go build -o bin/myapp ./cmd/myapp

# Typical project: 45 internal packages, 120 dependencies
# Build time measured with: time make build
```

**Current benchmark:**
```
$ time make build
real    0m38.4s
user    1m12.7s
sys     0m8.2s

# Every build takes ~38 seconds regardless of changes
```

<details>
<summary>💡 Hint</summary>

Go has a built-in build cache at `$GOPATH/pkg` and `$HOME/.cache/go-build`. If you stop clearing it, only changed packages get recompiled. Check what `go env GOCACHE` returns.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```makefile
# Fast version — leverage the build cache
.PHONY: build

build:
	go build -o bin/myapp ./cmd/myapp

# Optional: verify cache is being used
cache-stats:
	go env GOCACHE
	du -sh $$(go env GOCACHE)
```

**What changed:**
- Removed `go clean -cache` — the build cache now persists between builds
- Go automatically detects which packages changed and only recompiles those

**Optimized benchmark:**
```
# First build (cold cache):
$ time make build
real    0m37.9s
user    1m11.3s
sys     0m7.8s

# Second build (no changes, warm cache):
$ time make build
real    0m1.2s
user    0m1.8s
sys     0m0.4s

# Third build (1 file changed, warm cache):
$ time make build
real    0m3.6s
user    0m5.1s
sys     0m1.0s
```

**Improvement:** 10.5x faster on no-change rebuilds, 31x faster on warm cache with minor changes

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Go's build cache (`$HOME/.cache/go-build` on Linux) stores compiled package objects keyed by the source file content hash. When source files haven't changed, Go skips compilation entirely and reuses the cached `.a` files. The cache is content-addressable — it doesn't rely on timestamps.

**When to apply:** Always. There is almost never a reason to clear the build cache in normal development. Even in CI, preserving the cache between runs saves significant time.

**When NOT to apply:** Only clear the cache if you suspect cache corruption (extremely rare), or when debugging compiler bugs where you need to rule out stale artifacts. Use `go clean -cache` only as a diagnostic tool, never as part of a standard build.

</details>

---

## Exercise 2: Cleaning Up Unused Dependencies with go mod tidy 🟢 📦

**What the code does:** A Go project with a bloated `go.mod` file containing unused dependencies accumulated over months of development.

**The problem:** The `go.mod` has 85 direct dependencies, but only 52 are actually used. Every `go build` and `go mod download` pulls unnecessary packages, increasing download time, disk usage, and the attack surface.

```bash
# Slow version — bloated go.mod with unused dependencies

# go.mod has accumulated unused deps:
# module github.com/myorg/myapp
# go 1.22
# require (
#     github.com/gin-gonic/gin v1.9.1          // used
#     github.com/stretchr/testify v1.8.4        // used
#     github.com/sirupsen/logrus v1.9.3         // UNUSED - switched to slog
#     github.com/pkg/errors v0.9.1              // UNUSED - using fmt.Errorf
#     github.com/go-redis/redis/v9 v9.0.5       // UNUSED - removed Redis
#     ... 30+ more unused dependencies
# )

# Build process:
go mod download    # downloads everything including unused deps
go build -o bin/myapp ./cmd/myapp
```

**Current benchmark:**
```
$ time go mod download
real    0m24.6s

$ du -sh $GOPATH/pkg/mod
1.8G    /home/user/go/pkg/mod

# go.sum file: 847 lines
# go.mod direct dependencies: 85
```

<details>
<summary>💡 Hint</summary>

Run `go mod tidy` to automatically remove unused dependencies from `go.mod` and `go.sum`. Then check the difference in file sizes and download times.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
# Fast version — clean up unused dependencies

# Step 1: Remove unused dependencies
go mod tidy

# Step 2: Verify the cleanup
go mod verify

# Step 3: Build as normal
go mod download
go build -o bin/myapp ./cmd/myapp

# Optional: check for why a specific module is required
# go mod why github.com/sirupsen/logrus
```

**What changed:**
- `go mod tidy` removed 33 unused direct dependencies and their transitive deps
- `go.sum` shrank from 847 lines to 512 lines
- Module download pulls only what's actually imported

**Optimized benchmark:**
```
$ time go mod download
real    0m14.2s

$ du -sh $GOPATH/pkg/mod
1.1G    /home/user/go/pkg/mod

# go.sum file: 512 lines
# go.mod direct dependencies: 52
```

**Improvement:** 1.7x faster downloads, 39% less disk usage, 335 fewer lines in go.sum

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** `go mod tidy` walks the entire import graph of your project, determines which modules are actually needed, and removes everything else from `go.mod` and `go.sum`. Fewer modules mean fewer downloads, less disk I/O, and faster `go mod download` in CI pipelines.

**When to apply:** Run `go mod tidy` regularly — after removing imports, switching libraries, or before releasing. Add it to your CI pipeline as a check: `go mod tidy && git diff --exit-code go.mod go.sum`.

**When NOT to apply:** Be careful in monorepos where different build tags or platforms might import different packages. Use `go mod tidy -e` to tolerate errors, and test all platforms after tidying.

</details>

---

## Exercise 3: Stripping Debug Info from Binaries 🟢 📦

**What the code does:** Builds a production Go binary with default compiler flags, including all debug symbols and DWARF information.

**The problem:** The default `go build` includes symbol tables and DWARF debug info, making binaries significantly larger than necessary for production deployments. Larger binaries mean slower Docker image pulls, more storage, and longer deployment times.

```bash
# Slow version — default build with full debug info
go build -o bin/myapp ./cmd/myapp

# Check the binary size
ls -lh bin/myapp
# -rwxr-xr-x 1 user user 24M Jun 15 10:30 bin/myapp

file bin/myapp
# bin/myapp: ELF 64-bit LSB executable, x86-64, ..., not stripped
```

**Current benchmark:**
```
$ ls -lh bin/myapp
24M     bin/myapp

$ file bin/myapp
bin/myapp: ELF 64-bit LSB executable, x86-64, ..., not stripped

# Contains: symbol table (2.1M), DWARF debug info (5.8M), Go type info
```

<details>
<summary>💡 Hint</summary>

The `-ldflags` flag passes options to the Go linker. The `-s` flag strips the symbol table, and `-w` strips DWARF debug information. These are not needed in production.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
# Fast version — strip debug info for production builds
go build -ldflags="-s -w" -o bin/myapp ./cmd/myapp

# Verify the result
ls -lh bin/myapp
file bin/myapp
```

**What changed:**
- `-s` removes the symbol table and related debugging information
- `-w` removes the DWARF debugging information
- The binary is functionally identical but much smaller

**Optimized benchmark:**
```
$ ls -lh bin/myapp
16M     bin/myapp

$ file bin/myapp
bin/myapp: ELF 64-bit LSB executable, x86-64, ..., stripped

# Removed: symbol table (2.1M), DWARF debug info (5.8M)
# Binary is 33% smaller
```

**Improvement:** 33% smaller binary (24M -> 16M), identical runtime performance, faster deployment

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Debug symbols and DWARF data are only used by debuggers (like `dlv`) and profilers. Production binaries don't need them. The linker flags tell the Go toolchain to omit this metadata, producing a leaner executable.

**When to apply:** Always for production builds, Docker images, and deployed binaries. Keep debug symbols in development builds for debugging with Delve.

**When NOT to apply:** Never strip debug info from development builds — you'll lose the ability to use `dlv debug`, `go tool pprof` with source annotations, and meaningful stack traces with full symbol names. Stack traces still work after stripping, but they lose some detail.

</details>

---

## Exercise 4: Multi-Stage Docker Builds 🟡 📦

**What the code does:** A Dockerfile that builds and runs a Go application in a single stage using the full Go SDK image.

**The problem:** The final Docker image includes the entire Go toolchain, source code, build cache, and all dependencies — none of which are needed at runtime. The resulting image is massive.

```dockerfile
# Slow version — single-stage Dockerfile
FROM golang:1.22

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build -o myapp ./cmd/myapp

EXPOSE 8080
CMD ["./myapp"]
```

**Current benchmark:**
```
$ docker build -t myapp:single .
$ docker images myapp:single
REPOSITORY    TAG       SIZE
myapp         single    1.24GB

# Breakdown:
# golang:1.22 base image:  814MB
# Source code + deps:       187MB
# Build cache:             ~200MB
# Binary:                    24MB
# Runtime needs:             24MB (just the binary)

$ time docker push myapp:single
real    2m18.4s
```

<details>
<summary>💡 Hint</summary>

Use a multi-stage build: compile in `golang:1.22`, then copy only the binary into a minimal base image like `alpine:3.19` or `scratch`. You'll need to handle TLS certificates and timezone data if using `scratch`.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```dockerfile
# Fast version — multi-stage Docker build
# Stage 1: Build
FROM golang:1.22-alpine AS builder

WORKDIR /app

# Cache dependency downloads
COPY go.mod go.sum ./
RUN go mod download

COPY . .

# Build static binary with stripped debug info
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -ldflags="-s -w" -o myapp ./cmd/myapp

# Stage 2: Runtime
FROM alpine:3.19

# Add CA certificates for HTTPS and timezone data
RUN apk --no-cache add ca-certificates tzdata

WORKDIR /app

# Copy only the binary from builder
COPY --from=builder /app/myapp .

EXPOSE 8080
CMD ["./myapp"]
```

**What changed:**
- Two-stage build: build in `golang:1.22-alpine`, run in `alpine:3.19`
- `CGO_ENABLED=0` produces a fully static binary (no libc dependency)
- `-ldflags="-s -w"` strips debug info for smaller binary
- Only the binary, CA certs, and tzdata are in the final image

**Optimized benchmark:**
```
$ docker build -t myapp:multi .
$ docker images myapp:multi
REPOSITORY    TAG       SIZE
myapp         multi     24.8MB

$ time docker push myapp:multi
real    0m12.3s
```

**Improvement:** 50x smaller image (1.24GB -> 24.8MB), 11x faster push, dramatically faster pulls in Kubernetes

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Docker multi-stage builds let you use a full development environment for compilation but ship only the artifacts. Go's static compilation (`CGO_ENABLED=0`) means the binary has zero runtime dependencies — it doesn't even need libc. The Alpine base adds only ~7MB for essential certificates and timezone data.

**When to apply:** Every Go production Docker image should use multi-stage builds. There is no legitimate reason to ship the Go SDK in a production container.

**When NOT to apply:** During local development, you might want the full image for debugging. Also, if your application uses CGO (e.g., SQLite bindings), you'll need a different approach — use `alpine` with `musl-dev` in the build stage and ensure the runtime image has the needed shared libraries.

</details>

---

## Exercise 5: Parallel Test Execution 🟡 ⚡

**What the code does:** A CI script that runs all tests in a Go project sequentially.

**The problem:** Tests run one package at a time with no parallelism. On a 4-core CI runner, 75% of CPU capacity sits idle during test execution.

```bash
# Slow version — sequential test execution in CI
#!/bin/bash
set -e

echo "Running tests..."

# Run tests sequentially, one package at a time
for pkg in $(go list ./...); do
    echo "Testing $pkg..."
    go test -v -count=1 "$pkg"
done

echo "All tests passed!"
```

**Current benchmark:**
```
$ time ./run-tests.sh
Testing github.com/myorg/myapp/internal/auth...
ok      github.com/myorg/myapp/internal/auth    4.2s
Testing github.com/myorg/myapp/internal/database...
ok      github.com/myorg/myapp/internal/database 6.8s
Testing github.com/myorg/myapp/internal/handlers...
ok      github.com/myorg/myapp/internal/handlers 3.1s
... (22 more packages)

real    1m52.4s
user    1m28.3s
sys     0m14.1s

# 25 test packages total, average 4.5s each
# CPU utilization during run: ~25% (1 core out of 4)
```

<details>
<summary>💡 Hint</summary>

`go test ./...` already parallelizes test execution across packages. The `-p` flag controls how many packages build and test in parallel (default is `GOMAXPROCS`). The `-parallel` flag controls parallelism within a single package's tests.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
# Fast version — parallel test execution
#!/bin/bash
set -e

echo "Running tests..."

# Run all packages in parallel (default: GOMAXPROCS packages at once)
# -p 4      = build/test up to 4 packages simultaneously
# -parallel 4 = run up to 4 tests in parallel within each package
# -count=1  = disable test caching for CI
go test -v -p 4 -parallel 4 -count=1 ./...

echo "All tests passed!"
```

**What changed:**
- Replaced the sequential `for` loop with `go test ./...` which runs packages in parallel
- `-p 4` allows 4 packages to compile and test simultaneously
- `-parallel 4` allows individual `t.Parallel()` tests to run concurrently within a package
- Single `go test` invocation avoids repeated toolchain startup overhead

**Optimized benchmark:**
```
$ time ./run-tests.sh
ok      github.com/myorg/myapp/internal/auth        4.2s
ok      github.com/myorg/myapp/internal/database     6.8s
ok      github.com/myorg/myapp/internal/handlers     3.1s
... (22 more packages)

real    0m34.7s
user    1m42.1s
sys     0m16.8s

# CPU utilization during run: ~92% (all 4 cores)
# Wall time reduced from 1m52s to 34.7s
```

**Improvement:** 3.2x faster wall-clock time, 92% CPU utilization vs 25%

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** `go test ./...` uses Go's built-in parallel test scheduler. It compiles and tests multiple packages simultaneously, bounded by `-p`. Within each package, tests marked with `t.Parallel()` run concurrently, bounded by `-parallel`. This keeps all CPU cores busy instead of wasting 75% of available compute.

**When to apply:** Always in CI. For local development, `go test ./...` already uses `GOMAXPROCS` parallelism by default. Increase `-p` on machines with many cores.

**When NOT to apply:** If tests have shared global state (databases, files, ports), parallel execution can cause flaky tests. Fix the tests first by using unique resources per test (random ports, test-specific DB schemas), then enable parallelism.

</details>

---

## Exercise 6: Vendoring Dependencies for CI Speed 🟡 🔄

**What the code does:** A CI pipeline that downloads all Go modules from the internet on every build.

**The problem:** Every CI run calls `go mod download`, hitting the Go module proxy and downloading hundreds of modules. Network latency and proxy rate limits slow down builds, and transient network failures cause flaky CI.

```yaml
# Slow version — .github/workflows/build.yml
name: Build
on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-go@v5
        with:
          go-version: '1.22'

      - name: Download dependencies
        run: go mod download

      - name: Build
        run: go build -o bin/myapp ./cmd/myapp

      - name: Test
        run: go test ./...
```

**Current benchmark:**
```
# CI run times (averaged over 10 runs):
Download dependencies:    45s (varies: 28s - 92s depending on proxy load)
Build:                    38s
Test:                     34s
Total:                    1m57s

# Failure rate due to network issues: ~3% of builds
# 120 transitive dependencies downloaded each time
```

<details>
<summary>💡 Hint</summary>

Consider two approaches: (1) `go mod vendor` to commit dependencies into the repo, eliminating network calls entirely, or (2) use GitHub Actions cache to persist the Go module cache between runs.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```yaml
# Fast version — .github/workflows/build.yml with module caching
name: Build
on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-go@v5
        with:
          go-version: '1.22'
          cache: true                    # Built-in Go module caching
          cache-dependency-path: go.sum  # Cache key based on go.sum hash

      - name: Build
        run: go build -o bin/myapp ./cmd/myapp

      - name: Test
        run: go test ./...
```

```bash
# Alternative: vendor approach (zero network dependency)
# Run once locally:
go mod vendor
git add vendor/
git commit -m "vendor: add dependency vendoring"

# Then in CI, use -mod=vendor:
# go build -mod=vendor -o bin/myapp ./cmd/myapp
# go test -mod=vendor ./...
```

**What changed:**
- `actions/setup-go` with `cache: true` persists `$GOPATH/pkg/mod` between runs
- Cache key is based on `go.sum` hash — only re-downloads when dependencies change
- Alternative vendoring approach commits deps to git, eliminating network dependency entirely

**Optimized benchmark:**
```
# CI run times with caching (averaged over 10 runs):
Setup + cache restore:    8s (cache hit)
Build:                    12s (build cache also restored)
Test:                     34s
Total:                    54s

# With vendoring approach:
Setup:                    3s
Build:                    14s
Test:                     34s
Total:                    51s

# Failure rate due to network issues: 0%
```

**Improvement:** 2.2x faster CI pipeline (1m57s -> 54s), 0% network-related failures

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Module caching avoids redundant downloads — `go.sum` is a content-addressable lock file, so the cache is perfectly invalidated when dependencies change. Vendoring goes further by eliminating the network dependency entirely, making builds reproducible and immune to proxy outages or module retraction.

**When to apply:** Always cache modules in CI. Consider vendoring for security-critical projects (you audit every dependency change via code review) or air-gapped environments.

**When NOT to apply:** Vendoring adds hundreds of MB to your Git repo, which slows down `git clone`. For large projects with many dependencies, caching is usually the better tradeoff. Also, `vendor/` makes diffs noisy when updating dependencies.

</details>

---

## Exercise 7: Static Binary with CGO_ENABLED=0 🟡 💾

**What the code does:** Builds a Go binary on a system with CGO enabled by default, producing a dynamically linked executable.

**The problem:** The binary links against system libc (`glibc`), creating runtime dependency issues. The binary fails when deployed to Alpine containers (which use `musl`) or minimal Docker images. It also can't run on `scratch` or `distroless` images without additional shared libraries.

```bash
# Slow version — default build with CGO enabled
go build -o bin/myapp ./cmd/myapp

# Check what it links to
ldd bin/myapp
#   linux-vdso.so.1 (0x00007ffd...)
#   libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6
#   libpthread.so.0 => /lib/x86_64-linux-gnu/libpthread.so.0
#   /lib64/ld-linux-x86-64.so.2

file bin/myapp
# ELF 64-bit LSB executable, x86-64, dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2
```

**Current benchmark:**
```
$ CGO_ENABLED=1 go build -o bin/myapp ./cmd/myapp
$ ls -lh bin/myapp
24M     bin/myapp

$ file bin/myapp
ELF 64-bit LSB executable, x86-64, dynamically linked

$ ldd bin/myapp | wc -l
4

# Deploy to Alpine container:
$ docker run --rm -v $(pwd)/bin:/app alpine /app/myapp
/app/myapp: error while loading shared libraries: libc.so.6: cannot open shared object file

# Deploy to scratch container:
$ docker run --rm -v $(pwd)/bin:/app scratch /app/myapp
standard_init_linux.go: exec format error
```

<details>
<summary>💡 Hint</summary>

Set `CGO_ENABLED=0` to tell the Go compiler to use pure Go implementations of everything (including `net` and `os/user`). The result is a fully static binary that runs anywhere — even on `scratch`.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
# Fast version — fully static binary
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -ldflags="-s -w" -o bin/myapp ./cmd/myapp

# Verify it's static
ldd bin/myapp
# not a dynamic executable

file bin/myapp
# ELF 64-bit LSB executable, x86-64, statically linked
```

```dockerfile
# Now you can use the smallest possible base image
FROM scratch
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /app/myapp /myapp
ENTRYPOINT ["/myapp"]
```

**What changed:**
- `CGO_ENABLED=0` forces pure Go implementations (no C library dependency)
- `-ldflags="-s -w"` strips debug info for smaller binary
- Binary is fully self-contained and runs on any Linux kernel
- Can use `scratch` as base image (0 bytes overhead)

**Optimized benchmark:**
```
$ CGO_ENABLED=0 go build -ldflags="-s -w" -o bin/myapp ./cmd/myapp
$ ls -lh bin/myapp
15M     bin/myapp

$ file bin/myapp
ELF 64-bit LSB executable, x86-64, statically linked

$ ldd bin/myapp
not a dynamic executable

# Docker image with scratch base:
$ docker images myapp:scratch
REPOSITORY    TAG       SIZE
myapp         scratch   15.2MB

# Works everywhere:
$ docker run --rm myapp:scratch
Server started on :8080
```

**Improvement:** 37.5% smaller binary, runs on any image including scratch, 15.2MB Docker image vs 1.24GB

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** When `CGO_ENABLED=0`, Go uses its own pure-Go implementations of the `net` package (DNS resolution), `os/user` (user lookup), and other packages that normally call into C. The resulting binary contains everything it needs to run — no external dependencies. This is one of Go's killer features for containerized deployments.

**When to apply:** For any container deployment, cloud function, or cross-compiled binary. Static binaries are the standard for production Go services.

**When NOT to apply:** When you genuinely need CGO — for example, if you use `go-sqlite3`, image processing libraries wrapping C code, or platform-specific C APIs. In those cases, you must match the libc version between build and runtime environments.

</details>

---

## Exercise 8: UPX Binary Compression 🔴 📦

**What the code does:** Deploys a stripped Go binary to a resource-constrained environment (IoT devices, Lambda functions, or minimal containers where every MB counts).

**The problem:** Even after stripping debug info with `-ldflags="-s -w"`, the binary is still large because it contains the full Go runtime, GC, scheduler, and all compiled application code in uncompressed form. For environments with strict size limits (AWS Lambda 50MB limit, embedded devices), further compression is needed.

```bash
# Slow version — stripped but uncompressed binary
CGO_ENABLED=0 go build -ldflags="-s -w" -o bin/myapp ./cmd/myapp

ls -lh bin/myapp
# 16M    bin/myapp

# AWS Lambda deployment package:
zip -j deployment.zip bin/myapp
ls -lh deployment.zip
# 6.2M   deployment.zip (zip compresses well, but the binary in memory is still 16M)

# Docker scratch image:
# 16MB uncompressed in the image layer
```

**Current benchmark:**
```
$ CGO_ENABLED=0 go build -ldflags="-s -w" -o bin/myapp ./cmd/myapp
$ ls -lh bin/myapp
16M     bin/myapp

$ file bin/myapp
ELF 64-bit LSB executable, x86-64, statically linked, stripped

# Memory footprint at startup:
$ /usr/bin/time -v ./bin/myapp &
Maximum resident set size: 18432 kB

# Cold start time:
$ time ./bin/myapp --health-check
real    0m0.042s
```

**Profiling output:**
```
$ bloaty bin/myapp
    FILE SIZE        VM SIZE
 --------------  --------------
  38.2%  6.12M   38.8%  6.12M    .text (executable code)
  23.1%  3.70M   23.5%  3.70M    .rodata (read-only data)
  18.7%  2.99M   19.0%  2.99M    .gopclntab (Go pcline table)
  10.4%  1.66M   10.6%  1.66M    .go.buildinfo + type data
   9.6%  1.53M    8.1%  1.28M    other sections
```

<details>
<summary>💡 Hint</summary>

UPX (Ultimate Packer for eXecutables) compresses the binary and adds a tiny decompression stub. At startup, the binary decompresses itself into memory. Trade slightly longer startup for much smaller on-disk and transfer size. Use `upx --best` for maximum compression or `upx --brute` for extreme cases.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```bash
# Fast version — UPX-compressed binary
# Step 1: Build stripped static binary
CGO_ENABLED=0 go build -ldflags="-s -w" -o bin/myapp ./cmd/myapp

# Step 2: Compress with UPX
# Install UPX: apt-get install upx-ucl  OR  brew install upx
upx --best --lzma -o bin/myapp-compressed bin/myapp

# Verify it still works
./bin/myapp-compressed --health-check

ls -lh bin/myapp bin/myapp-compressed
```

```dockerfile
# Minimal Docker image with UPX-compressed binary
FROM golang:1.22-alpine AS builder
RUN apk add --no-cache upx

WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .

RUN CGO_ENABLED=0 go build -ldflags="-s -w" -o myapp ./cmd/myapp && \
    upx --best --lzma myapp

FROM scratch
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /app/myapp /myapp
ENTRYPOINT ["/myapp"]
```

**What changed:**
- Added UPX compression step after building the stripped binary
- `--best` enables best compression ratio
- `--lzma` uses LZMA algorithm for superior compression on large binaries
- Binary self-decompresses at startup with minimal overhead

**Optimized benchmark:**
```
$ ls -lh bin/myapp bin/myapp-compressed
16M     bin/myapp
4.8M    bin/myapp-compressed

$ file bin/myapp-compressed
ELF 64-bit LSB executable, x86-64, statically linked, stripped
# UPX packed

# Docker image size:
$ docker images myapp:upx
REPOSITORY    TAG       SIZE
myapp         upx       5.0MB

# Cold start time (with decompression):
$ time ./bin/myapp-compressed --health-check
real    0m0.089s

# Memory footprint (decompressed in RAM):
$ /usr/bin/time -v ./bin/myapp-compressed &
Maximum resident set size: 22528 kB
```

**Improvement:** 70% smaller on disk (16M -> 4.8M), 5MB Docker image, tradeoff: +47ms startup, +4MB RAM

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** UPX embeds a decompression stub at the start of the binary. When the OS loads the executable, the stub runs first, decompresses the payload into memory, and then jumps to the original entry point. The compression ratio is excellent because compiled code has high redundancy (repeated instruction patterns, padding, alignment bytes).

**Go source reference:** The Go runtime starts in `runtime.rt0_go` (in `runtime/asm_amd64.s`). UPX ensures this entry point is called correctly after decompression by rewriting the ELF headers.

**When to apply:** Lambda functions (50MB limit), IoT/embedded deployments, container images where registry bandwidth is expensive, and edge computing scenarios.

**When NOT to apply:** High-frequency cold starts (serverless with aggressive scaling) where the 40-80ms decompression overhead matters. Also, some security scanners flag UPX-packed binaries as suspicious because malware commonly uses UPX. In production Kubernetes clusters with warm pods, the larger unpacked binary is fine.

</details>

---

## Exercise 9: Build Cache in CI with GitHub Actions 🔴 ⚡

**What the code does:** A GitHub Actions workflow that builds and tests a Go project on every push.

**The problem:** Every CI run starts from a cold cache — no build cache, no module cache. The Go compiler recompiles every single package (including the standard library in some cases) from scratch. This wastes 60-70% of build time on unchanged code.

```yaml
# Slow version — .github/workflows/ci.yml (no caching)
name: CI
on: [push, pull_request]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Setup Go
        uses: actions/setup-go@v5
        with:
          go-version: '1.22'
          cache: false  # Caching explicitly disabled

      - name: Download modules
        run: go mod download

      - name: Build
        run: go build ./...

      - name: Test
        run: go test -race -count=1 ./...

      - name: Lint
        run: |
          go install github.com/golangci/golangci-lint/cmd/golangci-lint@v1.56.2
          golangci-lint run ./...
```

**Current benchmark:**
```
# Average CI run times (over 50 runs):
Setup Go:           12s
Download modules:   48s
Build:              42s
Test:               1m24s
Lint (install):     35s
Lint (run):         28s
Total:              4m09s

# Cache stats:
# Build cache hits: 0% (always cold)
# Module cache hits: 0% (always downloads)
# Lint tool: re-downloaded and compiled every run
```

**Profiling output:**
```
# Build cache analysis:
$ go env GOCACHE
/home/runner/.cache/go-build

$ du -sh /home/runner/.cache/go-build
0       /home/runner/.cache/go-build     # Empty — no cache

# Time breakdown showing redundant work:
# - 65% of build time: recompiling unchanged packages
# - 20% of build time: recompiling standard library with -race
# - 15% of build time: compiling changed code (actual work)
```

<details>
<summary>💡 Hint</summary>

Cache three things: (1) Go module cache (`~/go/pkg/mod`), (2) Go build cache (`~/.cache/go-build`), and (3) pre-built lint tools. Use `actions/setup-go@v5` built-in caching and `actions/cache` for build artifacts. Key caches on `go.sum` for modules and source file hashes for build cache.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```yaml
# Fast version — .github/workflows/ci.yml (fully cached)
name: CI
on: [push, pull_request]

env:
  GO_VERSION: '1.22'
  GOLANGCI_LINT_VERSION: 'v1.56.2'

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Setup Go with module cache
        uses: actions/setup-go@v5
        with:
          go-version: ${{ env.GO_VERSION }}
          cache: true                       # Cache ~/go/pkg/mod and ~/.cache/go-build
          cache-dependency-path: go.sum

      - name: Cache Go build artifacts
        uses: actions/cache@v4
        with:
          path: |
            ~/.cache/go-build
          key: go-build-${{ runner.os }}-${{ hashFiles('**/*.go') }}
          restore-keys: |
            go-build-${{ runner.os }}-

      - name: Cache golangci-lint
        uses: actions/cache@v4
        with:
          path: |
            ~/.cache/golangci-lint
          key: golangci-lint-${{ runner.os }}-${{ env.GOLANGCI_LINT_VERSION }}

      - name: Build
        run: go build ./...

      - name: Test
        run: go test -race -count=1 -p 4 ./...

      - name: Lint
        uses: golangci/golangci-lint-action@v4
        with:
          version: ${{ env.GOLANGCI_LINT_VERSION }}
          args: --timeout=5m

  # Separate job: build cache is shared via cache action
  integration-test:
    runs-on: ubuntu-latest
    needs: build-and-test
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-go@v5
        with:
          go-version: ${{ env.GO_VERSION }}
          cache: true
          cache-dependency-path: go.sum

      - name: Restore build cache
        uses: actions/cache@v4
        with:
          path: ~/.cache/go-build
          key: go-build-${{ runner.os }}-${{ hashFiles('**/*.go') }}
          restore-keys: go-build-${{ runner.os }}-

      - name: Integration tests
        run: go test -race -tags=integration -count=1 ./...
```

**What changed:**
- `actions/setup-go` with `cache: true` handles module cache automatically
- Separate `actions/cache` for build artifacts with content-based key (`hashFiles('**/*.go')`)
- `restore-keys` with prefix enables partial cache hits (reuses most of the build cache even when some files change)
- golangci-lint uses the official action with its own cache
- Build cache is shared across jobs via the cache action
- Added `-p 4` for parallel testing

**Optimized benchmark:**
```
# Average CI run times with warm cache (over 50 runs):
Setup Go + restore:  8s (cache hit)
Build:               6s (95% cache hit rate)
Test:                52s (race detector cache partially reused)
Lint:                12s (cached binary, incremental analysis)
Total:               1m18s

# Cache stats:
# Build cache hits: 95% (only changed packages recompile)
# Module cache hits: 100% (until go.sum changes)
# Lint cache hits: 100% (until version changes)

# Cold cache (first run after go.sum change):
# Total: 2m45s (modules download, but build cache still partially valid)
```

**Improvement:** 3.2x faster CI (4m09s -> 1m18s), 95% build cache hit rate, lint 2.3x faster

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** Go's build cache is content-addressable — each compiled package is keyed by the hash of its source files, import configuration, and compiler flags. `actions/cache` with `restore-keys` enables "partial cache hits": even when the exact key doesn't match, the most recent cache with the matching prefix is restored. This means changing one file only invalidates that package's cache entry, not the entire cache. The `-race` flag changes the compiler output (adds instrumentation), so race-enabled builds use separate cache entries.

**Go source reference:** Build cache logic is in `cmd/go/internal/cache/cache.go`. The cache key computation is in `cmd/go/internal/work/exec.go` (`actionID` function). Understanding these helps you predict cache behavior.

**When to apply:** Every Go project in CI should cache both modules and build artifacts. The ROI is massive — typically 2-5x faster CI with zero maintenance after initial setup.

**When NOT to apply:** If your CI requires hermetic builds (e.g., for security auditing or reproducibility certification), caching introduces a variable. In that case, use vendoring for modules and accept the build cache miss. Also, very large caches (>5GB) may exceed GitHub Actions cache limits (10GB per repo).

</details>

---

## Exercise 10: Cross-Compilation Matrix Optimization 🔴 🔄

**What the code does:** A CI pipeline that builds Go binaries for multiple OS/architecture combinations for a release.

**The problem:** Each target platform is built sequentially in a single job. The build matrix doesn't share any work between targets — shared packages (which are platform-independent) are recompiled for each GOOS/GOARCH combination. A 6-target release build takes 6x the time of a single build.

```yaml
# Slow version — sequential cross-compilation
name: Release
on:
  push:
    tags: ['v*']

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-go@v5
        with:
          go-version: '1.22'
          cache: false

      - name: Build all platforms
        run: |
          VERSION=${GITHUB_REF#refs/tags/}

          # Sequential builds — each one compiles everything from scratch
          GOOS=linux   GOARCH=amd64 go build -ldflags="-s -w -X main.version=$VERSION" -o dist/myapp-linux-amd64       ./cmd/myapp
          GOOS=linux   GOARCH=arm64 go build -ldflags="-s -w -X main.version=$VERSION" -o dist/myapp-linux-arm64       ./cmd/myapp
          GOOS=darwin  GOARCH=amd64 go build -ldflags="-s -w -X main.version=$VERSION" -o dist/myapp-darwin-amd64      ./cmd/myapp
          GOOS=darwin  GOARCH=arm64 go build -ldflags="-s -w -X main.version=$VERSION" -o dist/myapp-darwin-arm64      ./cmd/myapp
          GOOS=windows GOARCH=amd64 go build -ldflags="-s -w -X main.version=$VERSION" -o dist/myapp-windows-amd64.exe ./cmd/myapp
          GOOS=windows GOARCH=arm64 go build -ldflags="-s -w -X main.version=$VERSION" -o dist/myapp-windows-arm64.exe ./cmd/myapp

      - name: Create checksums
        run: cd dist && sha256sum * > checksums.txt

      - name: Upload release
        uses: softprops/action-gh-release@v1
        with:
          files: dist/*
```

**Current benchmark:**
```
# Build times (sequential on single runner):
linux/amd64:     38s
linux/arm64:     41s
darwin/amd64:    39s
darwin/arm64:    40s
windows/amd64:   42s
windows/arm64:   44s
Checksums:        2s
Upload:          15s
Total:          4m21s

# Each build recompiles ALL packages from scratch
# Zero sharing between builds — even platform-independent code is recompiled
```

<details>
<summary>💡 Hint</summary>

Use GitHub Actions matrix strategy to build all targets in parallel on separate runners. Combine with build caching and `trimpath` for reproducible builds. Use `actions/upload-artifact` to collect binaries, then a final job to create the release.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```yaml
# Fast version — parallel cross-compilation matrix
name: Release
on:
  push:
    tags: ['v*']

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        include:
          - goos: linux
            goarch: amd64
          - goos: linux
            goarch: arm64
          - goos: darwin
            goarch: amd64
          - goos: darwin
            goarch: arm64
          - goos: windows
            goarch: amd64
            ext: .exe
          - goos: windows
            goarch: arm64
            ext: .exe

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-go@v5
        with:
          go-version: '1.22'
          cache: true
          cache-dependency-path: go.sum

      - name: Build
        env:
          GOOS: ${{ matrix.goos }}
          GOARCH: ${{ matrix.goarch }}
          CGO_ENABLED: '0'
        run: |
          VERSION=${GITHUB_REF#refs/tags/}
          BINARY=myapp-${{ matrix.goos }}-${{ matrix.goarch }}${{ matrix.ext }}
          go build \
            -trimpath \
            -ldflags="-s -w -X main.version=$VERSION" \
            -o dist/$BINARY \
            ./cmd/myapp

      - name: Generate checksum
        run: |
          cd dist
          sha256sum * > checksums-${{ matrix.goos }}-${{ matrix.goarch }}.txt

      - name: Upload artifact
        uses: actions/upload-artifact@v4
        with:
          name: binary-${{ matrix.goos }}-${{ matrix.goarch }}
          path: dist/

  release:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Download all artifacts
        uses: actions/download-artifact@v4
        with:
          path: dist/
          merge-multiple: true

      - name: Merge checksums
        run: |
          cd dist
          cat checksums-*.txt | sort > checksums.txt
          rm checksums-*.txt

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          files: dist/*
          generate_release_notes: true
```

**What changed:**
- Matrix strategy runs all 6 builds in parallel on separate runners
- Each runner has its own module cache (fast restore from shared cache key)
- `-trimpath` removes local filesystem paths for reproducible builds
- `CGO_ENABLED=0` ensures static binaries across all platforms
- Artifacts are uploaded separately, then merged in a final release job
- `generate_release_notes: true` auto-generates changelog from commits

**Optimized benchmark:**
```
# Build times (parallel matrix — all run simultaneously):
linux/amd64:     38s  ─┐
linux/arm64:     41s   │
darwin/amd64:    39s   ├── All run in parallel
darwin/arm64:    40s   │
windows/amd64:   42s   │
windows/arm64:   44s  ─┘
                       Wall time: 44s (longest build)

# Release job:
Download artifacts:   8s
Merge checksums:      1s
Create release:      12s
                     21s

Total wall time:    1m05s

# With warm cache (subsequent releases):
Longest build:       14s
Release job:         21s
Total:               35s
```

**Improvement:** 4x faster releases (4m21s -> 1m05s), reproducible builds with -trimpath, parallelism scales to any number of targets

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** GitHub Actions matrix creates independent jobs that run on separate runners simultaneously. This is embarrassingly parallel — each target has no dependency on the others. The `-trimpath` flag removes all filesystem paths from the compiled binary, replacing them with module paths. This makes builds reproducible regardless of where they run (different runner IDs, different filesystem layouts).

**Go source reference:** Cross-compilation works because the Go compiler is written in Go and can generate code for any supported target. The `GOOS` and `GOARCH` environment variables control the target platform. With `CGO_ENABLED=0`, no platform-specific C compiler is needed, making cross-compilation trivial.

**When to apply:** Any project that releases binaries for multiple platforms. The matrix approach scales linearly — adding a new platform just adds one more parallel job with no impact on total build time (assuming runners are available).

**When NOT to apply:** If you have a very small number of targets (1-2), the overhead of separate jobs (checkout, setup, cache restore) may exceed the time saved by parallelism. Also, if your project requires CGO for each platform, you'll need platform-specific runners (or cross-compilation toolchains like `zig cc`), which adds complexity.

</details>

---

## Score Card

Track your progress:

| Exercise | Difficulty | Category | Found bottleneck? | Your improvement | Target improvement |
|:--------:|:---------:|:--------:|:-----------------:|:----------------:|:-----------------:|
| 1 | 🟢 | ⚡ | ☐ | ___ x | 10.5x |
| 2 | 🟢 | 📦 | ☐ | ___ x | 1.7x |
| 3 | 🟢 | 📦 | ☐ | ___ % | 33% smaller |
| 4 | 🟡 | 📦 | ☐ | ___ x | 50x smaller |
| 5 | 🟡 | ⚡ | ☐ | ___ x | 3.2x |
| 6 | 🟡 | 🔄 | ☐ | ___ x | 2.2x |
| 7 | 🟡 | 💾 | ☐ | ___ x | 82x smaller image |
| 8 | 🔴 | 📦 | ☐ | ___ % | 70% smaller |
| 9 | 🔴 | ⚡ | ☐ | ___ x | 3.2x |
| 10 | 🔴 | 🔄 | ☐ | ___ x | 4x |

### Rating:
- **All targets met** → You understand Go build optimization deeply
- **7-9 targets met** → Solid build pipeline skills
- **4-6 targets met** → Good foundation, practice CI/CD optimization more
- **< 4 targets met** → Start with basic `go build` flags and Docker multi-stage builds

---

## Optimization Cheat Sheet

Quick reference for common Go environment and build optimizations:

| Problem | Solution | Impact |
|:--------|:---------|:------:|
| Slow rebuilds | Stop clearing build cache, let `go build` use `$GOCACHE` | High |
| Bloated go.mod | Run `go mod tidy` to remove unused dependencies | Medium |
| Large production binary | Use `-ldflags="-s -w"` to strip debug symbols | Medium |
| Huge Docker images | Multi-stage build: compile in `golang`, run in `alpine`/`scratch` | High |
| Slow sequential tests | Use `go test ./...` with `-p` and `-parallel` flags | High |
| Slow CI module downloads | Cache `~/go/pkg/mod` or use `go mod vendor` | High |
| Binary won't run on Alpine | Use `CGO_ENABLED=0` for fully static binaries | High |
| Binary too large for Lambda | Compress with `upx --best --lzma` | Medium |
| Cold CI build cache | Cache `~/.cache/go-build` with content-based keys | High |
| Slow multi-platform releases | Use CI matrix strategy for parallel cross-compilation | High |
| Non-reproducible builds | Add `-trimpath` to remove local filesystem paths | Medium |
| Version not embedded | Use `-ldflags="-X main.version=$VERSION"` | Low |
