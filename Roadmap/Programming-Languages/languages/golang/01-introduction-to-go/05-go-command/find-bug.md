# Go Command — Find the Bug

> **Practice finding and fixing bugs in Go code related to the `go` command, build process, module management, and common toolchain issues.**

---

## How to Use

1. Read the buggy code carefully
2. Try to find the bug **without** looking at the hint
3. Write the fix yourself before checking the solution
4. Understand **why** the bug happens — not just how to fix it

### Difficulty Levels

| Level | Description |
|:-----:|:-----------|
| 🟢 | **Easy** — Common beginner Go mistakes, wrong flags, basic module issues |
| 🟡 | **Medium** — Logic errors, subtle build behavior, module dependency problems |
| 🔴 | **Hard** — Race conditions, CGO issues, cross-compilation edge cases, linker problems |

---

## Bug 1: Missing module initialization 🟢

**What the code should do:** Compile and print "Hello, World!"

```go
// main.go — in a new directory with no go.mod
package main

import "fmt"

func main() {
    fmt.Println("Hello, World!")
}
```

```bash
go build -o hello main.go
```

**Expected output:**
```
$ ./hello
Hello, World!
```

**Actual output:**
```
go: cannot find main module; see 'go help modules'
```

<details>
<summary>Hint</summary>
What file does every Go project need before you can build?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** No `go.mod` file exists in the directory.
**Why it happens:** Since Go 1.16, module-aware mode is the default. Without `go.mod`, Go does not know the module path and refuses to compile.
**Impact:** Build fails completely.

</details>

<details>
<summary>Fixed Code</summary>

```bash
# Fix: initialize the module first
go mod init hello
go build -o hello main.go
./hello
```

**What changed:** Added `go mod init` before building.

</details>

---

## Bug 2: Running a multi-file package incorrectly 🟢

**What the code should do:** Print "Sum: 15"

```go
// main.go
package main

import "fmt"

func main() {
    result := Sum(1, 2, 3, 4, 5)
    fmt.Printf("Sum: %d\n", result)
}
```

```go
// math.go
package main

func Sum(nums ...int) int {
    total := 0
    for _, n := range nums {
        total += n
    }
    return total
}
```

```bash
go run main.go
```

**Expected output:**
```
Sum: 15
```

**Actual output:**
```
./main.go:6:13: undefined: Sum
```

<details>
<summary>Hint</summary>
How many files did you pass to `go run`? Does it know about `math.go`?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** `go run main.go` only compiles `main.go`, not `math.go`.
**Why it happens:** When you specify individual files, `go run` only compiles those files. The `Sum` function is in `math.go` which was not included.
**Impact:** Compilation error — `undefined: Sum`.

</details>

<details>
<summary>Fixed Code</summary>

```bash
# Option 1: Run the whole package
go run .

# Option 2: Specify all files
go run main.go math.go
```

**What changed:** Used `go run .` to compile all `.go` files in the package.

</details>

---

## Bug 3: Wrong `go get` usage for installing tools 🟢

**What the code should do:** Install `golangci-lint` as a CLI tool.

```bash
# Inside a project directory with go.mod
go get github.com/golangci/golangci-lint/cmd/golangci-lint@latest
```

**Expected result:** `golangci-lint` binary available in PATH.

**Actual result:** The dependency is added to `go.mod` but no binary is installed. Running `golangci-lint` gives "command not found."

<details>
<summary>Hint</summary>
Since Go 1.17, how should you install CLI tools?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** `go get` in module-aware mode only updates `go.mod`. It does not install binaries.
**Why it happens:** Since Go 1.17, `go get` is for managing dependencies, not installing tools. Using `go get` pollutes your project's `go.mod` with a tool dependency.
**Impact:** Tool not installed; `go.mod` unnecessarily modified.

</details>

<details>
<summary>Fixed Code</summary>

```bash
# Correct: use go install for CLI tools
go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest

# Make sure $GOPATH/bin or $GOBIN is in your PATH
export PATH=$PATH:$(go env GOPATH)/bin
```

**What changed:** Replaced `go get` with `go install` for tool installation.

</details>

---

## Bug 4: Version injection with const instead of var 🟡

**What the code should do:** Print the injected version when built with `-ldflags`.

```go
package main

import "fmt"

const version = "dev"

func main() {
    fmt.Printf("Version: %s\n", version)
}
```

```bash
go build -ldflags="-X main.version=1.2.3" -o server
./server
```

**Expected output:**
```
Version: 1.2.3
```

**Actual output:**
```
Version: dev
```

<details>
<summary>Hint</summary>
What is the difference between `const` and `var` in Go? Can the linker modify constants?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** `version` is declared as `const`, but `-ldflags -X` can only modify `var` declarations.
**Why it happens:** Constants in Go are resolved at compile time and baked into the binary. The linker's `-X` flag modifies package-level string *variables* at link time, after compilation. It cannot change constants.
**Impact:** Version is always "dev" regardless of `-ldflags`.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import "fmt"

// Must be var, not const, for -ldflags -X to work
var version = "dev"

func main() {
    fmt.Printf("Version: %s\n", version)
}
```

**What changed:** Changed `const` to `var`.

</details>

---

## Bug 5: Test cache hiding a broken test 🟡

**What the code should do:** Test that always passes.

```go
// main_test.go
package main

import (
    "os"
    "testing"
)

func TestFileExists(t *testing.T) {
    _, err := os.Stat("/tmp/test-data.txt")
    if err != nil {
        t.Fatal("test data file not found")
    }
}
```

```bash
# First, create the file
echo "data" > /tmp/test-data.txt

# Run test — PASS
go test -v ./...

# Delete the file
rm /tmp/test-data.txt

# Run test again — still PASS!
go test -v ./...
```

**Expected output after file deletion:**
```
FAIL — test data file not found
```

**Actual output after file deletion:**
```
ok  mypackage  (cached)
```

<details>
<summary>Hint</summary>
What does `(cached)` mean? Does Go re-run the test?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** Go caches test results. The second run uses the cached PASS result without re-executing the test, even though the external file was deleted.
**Why it happens:** Go's test cache is based on the test binary, flags, and Go source files — NOT external files. Since nothing in the Go code changed, the cache hit returns the old result.
**Impact:** Test appears to pass but the actual condition is broken. This is dangerous for tests that depend on external state.

</details>

<details>
<summary>Fixed Code</summary>

```bash
# Option 1: Disable test cache
go test -count=1 -v ./...

# Option 2: The test itself should create its own data (better design)
```

```go
func TestFileExists(t *testing.T) {
    // Create test data as part of the test — don't rely on external state
    tmpFile := t.TempDir() + "/test-data.txt"
    if err := os.WriteFile(tmpFile, []byte("data"), 0644); err != nil {
        t.Fatal(err)
    }

    _, err := os.Stat(tmpFile)
    if err != nil {
        t.Fatal("test data file not found")
    }
}
```

**What changed:** Either use `-count=1` to bypass cache, or (better) make the test self-contained.

</details>

---

## Bug 6: Build tag syntax error (silent failure) 🟡

**What the code should do:** Only compile `debug.go` when the `debug` tag is provided.

```go
// debug.go
// +build debug

package main

import "log"

func init() {
    log.Println("DEBUG MODE ENABLED")
}
```

```bash
go build -tags debug -o server
./server
# Expected: "DEBUG MODE ENABLED" appears in output
# Actual: No debug message — file was silently excluded
```

<details>
<summary>Hint</summary>
Look at the build constraint syntax carefully. Is there a required blank line?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The `// +build debug` comment must be followed by a blank line before the `package` declaration. Without the blank line, Go treats it as a regular comment, not a build constraint.
**Why it happens:** The old-style `// +build` constraint requires a blank line separator. Without it, the directive is ignored silently — no error, no warning.
**Impact:** The file is always excluded (or always included, depending on placement), defeating the purpose of the build tag.

</details>

<details>
<summary>Fixed Code</summary>

```go
// debug.go
//go:build debug

package main

import "log"

func init() {
    log.Println("DEBUG MODE ENABLED")
}
```

**What changed:** Used the new `//go:build` syntax (Go 1.17+) which does NOT require a blank line and gives a compile error if malformed. This is the recommended syntax going forward.

</details>

---

## Bug 7: `go mod tidy` removing needed test dependency 🟡

**What the code should do:** Tests import a testing utility, but `go mod tidy` removes it.

```go
// main.go
package main

func main() {}
```

```go
// main_test.go
package main

import (
    "testing"
    "github.com/stretchr/testify/assert"
)

func TestSomething(t *testing.T) {
    assert.Equal(t, 42, 42)
}
```

```bash
go mod tidy
go test ./...
# Error: cannot find module providing package github.com/stretchr/testify/assert
```

<details>
<summary>Hint</summary>
Did you add the dependency before running `go mod tidy`? Or did you run `go mod tidy` before writing the test file?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The test file was created AFTER running `go mod tidy`, or `go mod tidy` was run when the test file had a syntax error or was not saved.
**Why it happens:** `go mod tidy` scans all `.go` files including `_test.go` files to find imports. If the test file was not present or had errors when `go mod tidy` ran, the dependency is treated as unused and removed.
**Impact:** Tests fail because the dependency is missing from `go.mod`.

</details>

<details>
<summary>Fixed Code</summary>

```bash
# Fix: run go mod tidy AFTER writing all code
# Step 1: Write all code including tests
# Step 2: Then tidy
go mod tidy

# Or explicitly add the dependency
go get github.com/stretchr/testify

# Then verify
go test ./...
```

**What changed:** Ensure all source files (including tests) exist before running `go mod tidy`.

</details>

---

## Bug 8: Data race not caught without -race flag 🔴

**What the code should do:** Safely increment a counter from multiple goroutines.

```go
package main

import (
    "fmt"
    "sync"
)

var counter int

func main() {
    var wg sync.WaitGroup
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            counter++
        }()
    }
    wg.Wait()
    fmt.Println("Counter:", counter)
}
```

```bash
go run main.go
# Output: Counter: 987 (or some number close to 1000, appears to work)
```

**Expected output:**
```
Counter: 1000
```

**Actual output:**
```
Counter: 987 (varies, sometimes 1000, sometimes less)
```

<details>
<summary>Hint</summary>
Run with `go run -race main.go`. What does it report?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** `counter++` is a data race — multiple goroutines read and write `counter` simultaneously without synchronization.
**Why it happens:** `counter++` is not atomic. It is actually three operations: read, increment, write. When two goroutines execute simultaneously, one goroutine's increment can be lost.
**Impact:** Incorrect counter value. Without `-race`, the program "appears" to work but gives wrong results. The bug is intermittent and hard to reproduce.
**Go spec reference:** The Go memory model states that concurrent access to shared variables must be synchronized.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "sync"
    "sync/atomic"
)

var counter atomic.Int64

func main() {
    var wg sync.WaitGroup
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            counter.Add(1) // atomic increment
        }()
    }
    wg.Wait()
    fmt.Println("Counter:", counter.Load())
}
```

**What changed:** Replaced `int` with `atomic.Int64` and used `Add(1)` for atomic increment.
**Alternative fix:** Use `sync.Mutex` to protect the counter, or use a channel-based approach.

</details>

---

## Bug 9: CGO binary fails in scratch container 🔴

**What the code should do:** Run a Go HTTP server in a Docker scratch container.

```dockerfile
FROM golang:1.22 AS builder
WORKDIR /app
COPY . .
RUN go build -o server ./cmd/server

FROM scratch
COPY --from=builder /app/server /server
ENTRYPOINT ["/server"]
```

```bash
docker build -t myserver .
docker run myserver
```

**Expected output:**
```
Server listening on :8080
```

**Actual output:**
```
standard_init_linux.go:228: exec user process caused: no such file or directory
```

<details>
<summary>Hint</summary>
Check if the binary is statically linked. What does `CGO_ENABLED` default to in the golang Docker image?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The Go binary is dynamically linked against glibc because `CGO_ENABLED=1` is the default in the `golang` Docker image. The `scratch` container has no shared libraries.
**Why it happens:** When CGO is enabled, the Go net package uses the system DNS resolver (`libc`), which requires `libnss_dns.so`, `libresolv.so`, etc. These libraries exist in the builder stage but not in `scratch`.
**Impact:** Container crashes immediately with a confusing "no such file or directory" error (it is looking for the dynamic linker `ld-linux-x86-64.so.2`).
**How to detect:** `file server` shows "dynamically linked" instead of "statically linked". Or `ldd server` shows shared library dependencies.

</details>

<details>
<summary>Fixed Code</summary>

```dockerfile
FROM golang:1.22 AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -trimpath -ldflags="-s -w" -o server ./cmd/server

FROM scratch
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /app/server /server
ENTRYPOINT ["/server"]
```

**What changed:**
1. Added `CGO_ENABLED=0` to produce a static binary
2. Added `-trimpath -ldflags="-s -w"` for security and size
3. Copied CA certificates for HTTPS support
4. Added separate `go mod download` layer for Docker cache efficiency

</details>

---

## Bug 10: Cross-compilation silently uses wrong architecture 🔴

**What the code should do:** Build a Linux ARM64 binary on an AMD64 machine.

```bash
GOOS=linux GOARCH=arm64 go build -o server-arm64 ./cmd/server
file server-arm64
# Expected: ELF 64-bit LSB executable, ARM aarch64
```

```go
// cmd/server/main.go
package main

/*
#include <stdio.h>
void greet() { printf("Hello from C!\n"); }
*/
import "C"

import "fmt"

func main() {
    C.greet()
    fmt.Println("Server starting...")
}
```

**Expected output:**
```
ELF 64-bit LSB executable, ARM aarch64
```

**Actual output:**
```
# command-line-arguments
/usr/bin/ld: cannot find -lc
collect2: error: ld returned 1 exit status
```

<details>
<summary>Hint</summary>
The code uses CGO (`import "C"`). What does cross-compilation with CGO require?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The code uses CGO (`import "C"`), but cross-compiling with CGO requires a C cross-compiler for the target architecture. The system's default `gcc` targets AMD64, not ARM64.
**Why it happens:** When `import "C"` is present, Go automatically enables CGO. Cross-compiling then tries to use the host's C compiler, which produces code for the wrong architecture.
**Impact:** Build fails with linker errors, or worse — produces a binary that crashes on the target platform.

</details>

<details>
<summary>Fixed Code</summary>

```bash
# Option 1: Remove CGO dependency (preferred)
# Remove the import "C" and C code, use pure Go alternatives

# Option 2: Provide a cross-compiler
CGO_ENABLED=1 CC=aarch64-linux-gnu-gcc \
    GOOS=linux GOARCH=arm64 \
    go build -o server-arm64 ./cmd/server

# Option 3: Build inside a Docker container matching the target
docker buildx build --platform linux/arm64 -t myserver .
```

**What changed:** Either removed CGO or provided the correct cross-compiler via `CC` environment variable.
**Alternative fix:** Use Docker multi-platform builds with `docker buildx`, which uses QEMU to emulate the target architecture.

</details>

---

## Bug 11: go:embed path not found 🔴

**What the code should do:** Embed all HTML templates.

```go
package main

import (
    "embed"
    "fmt"
)

//go:embed ../templates/*.html
var templates embed.FS

func main() {
    entries, _ := templates.ReadDir("templates")
    for _, e := range entries {
        fmt.Println(e.Name())
    }
}
```

```bash
go build
# pattern ../templates/*.html: invalid pattern syntax
```

<details>
<summary>Hint</summary>
Can `go:embed` paths go outside the package directory?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** `go:embed` does not allow paths that go above the package directory (no `..` paths).
**Why it happens:** For security reasons, `go:embed` only allows embedding files within the package's directory tree. Paths with `..` or absolute paths are rejected.
**Impact:** Build fails with a pattern syntax error.

</details>

<details>
<summary>Fixed Code</summary>

```go
// Move the embed directive to a package at or above the templates directory
// Or restructure your project so templates are within the package:
//
// cmd/server/
// ├── main.go
// └── templates/
//     ├── index.html
//     └── about.html

//go:embed templates/*.html
var templates embed.FS

func main() {
    entries, _ := templates.ReadDir("templates")
    for _, e := range entries {
        fmt.Println(e.Name())
    }
}
```

**What changed:** Moved templates into the package directory so the embed path does not need `..`.

</details>

---

## Score Card

| Bug | Difficulty | Found without hint? | Understood why? | Fixed correctly? |
|:---:|:---------:|:-------------------:|:---------------:|:----------------:|
| 1 | 🟢 | ☐ | ☐ | ☐ |
| 2 | 🟢 | ☐ | ☐ | ☐ |
| 3 | 🟢 | ☐ | ☐ | ☐ |
| 4 | 🟡 | ☐ | ☐ | ☐ |
| 5 | 🟡 | ☐ | ☐ | ☐ |
| 6 | 🟡 | ☐ | ☐ | ☐ |
| 7 | 🟡 | ☐ | ☐ | ☐ |
| 8 | 🔴 | ☐ | ☐ | ☐ |
| 9 | 🔴 | ☐ | ☐ | ☐ |
| 10 | 🔴 | ☐ | ☐ | ☐ |
| 11 | 🔴 | ☐ | ☐ | ☐ |

### Rating:
- **11/11 without hints** → Senior-level Go toolchain expertise
- **8-10/11** → Solid Go middle-level understanding
- **5-7/11** → Good junior, keep practicing
- **< 5/11** → Review the topic fundamentals first
