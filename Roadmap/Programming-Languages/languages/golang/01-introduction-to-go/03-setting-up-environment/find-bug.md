# Setting up the Go Environment — Find the Bug

> **Practice finding and fixing bugs in Go code related to Setting up the Go Environment.**
> Each exercise contains buggy code — your job is to find the bug, explain why it happens, and fix it.

---

## How to Use

1. Read the buggy code carefully
2. Try to find the bug **without** looking at the hint
3. Write the fix yourself before checking the solution
4. Understand **why** the bug happens — not just how to fix it

### Difficulty Levels

| Level | Description |
|:-----:|:-----------|
| 🟢 | **Easy** — Common beginner mistakes, syntax-level bugs |
| 🟡 | **Medium** — Logic errors, subtle behavior, concurrency issues |
| 🔴 | **Hard** — Race conditions, memory issues, compiler/runtime edge cases |

---

## Bug 1: Wrong Module Path in go.mod 🟢

**What the code should do:** Initialize a Go module and run a simple program that prints a greeting.

```
// go.mod
module github.com/myuser/myproject

go 1.21
```

```go
// main.go (located in directory: myproject/)
package main

import "fmt"

func main() {
    fmt.Println("Hello from myproject!")
}
```

```bash
# Running from the myproject/ directory:
cd myproject/
go run main.go
```

**Expected output:**
```
Hello from myproject!
```

**Actual output:**
```
Hello from myproject!
```

But when another module tries to import it:

```go
// In another project's main.go
package main

import (
    "fmt"
    "github.com/myuser/myproject/utils"
)

func main() {
    fmt.Println(utils.Greet())
}
```

```
// utils/utils.go inside myproject/
package utils

func Greet() string {
    return "Hello from utils!"
}
```

**Expected output:**
```
Hello from utils!
```

**Actual output:**
```
go: github.com/myuser/myproject/utils: module github.com/myuser/myproject: git ls-remote -q origin in /home/user/go/pkg/mod/cache/vcs/...: exit status 128:
    fatal: repository 'https://github.com/myuser/myproject/' not found
```

<details>
<summary>💡 Hint</summary>

Look at the module path in `go.mod` — does `github.com/myuser/myproject` actually exist as a remote repository? What happens when Go tries to fetch it?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The module path `github.com/myuser/myproject` does not correspond to an actual published repository on GitHub.
**Why it happens:** When another module tries to import packages from this module, Go uses the module path to resolve and download the dependency. If the repository does not exist at that URL, the download fails.
**Impact:** The module works fine locally in isolation, but cannot be imported by any other module. This is a very common beginner mistake — using a placeholder module path that does not match a real repository.

</details>

<details>
<summary>✅ Fixed Code</summary>

```
// go.mod — use the ACTUAL repository URL that matches your Git remote
module github.com/actualuser/myproject

go 1.21
```

Or for local-only development, use a non-importable path:

```
// go.mod — local-only module (cannot be imported remotely)
module myproject

go 1.21
```

**What changed:** The module path now matches the actual Git repository URL where the code is hosted, or uses a simple local path if the module is not intended to be imported externally.

</details>

---

## Bug 2: Missing go.sum Causes Build Failure 🟢

**What the code should do:** Use an external package (`github.com/fatih/color`) to print colored text.

```
// go.mod
module myapp

go 1.21

require github.com/fatih/color v1.16.0
```

```go
// main.go
package main

import "github.com/fatih/color"

func main() {
    color.Green("Success: environment is configured!")
}
```

```bash
# Someone cloned the repo but go.sum was in .gitignore
git clone https://github.com/team/myapp.git
cd myapp
go build .
```

**Expected output:**
```
(builds successfully, then prints green text)
Success: environment is configured!
```

**Actual output:**
```
go: github.com/fatih/color@v1.16.0: missing go.sum entry for go.sum file; to add:
        go mod download github.com/fatih/color
```

<details>
<summary>💡 Hint</summary>

Look at the `.gitignore` file — should `go.sum` be tracked by version control or ignored?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The `go.sum` file was added to `.gitignore`, so it is not committed to the repository.
**Why it happens:** Some developers mistakenly treat `go.sum` like `node_modules/` or `vendor/` and add it to `.gitignore`. However, `go.sum` contains cryptographic checksums of dependencies and is required by Go for security verification. Without it, `go build` refuses to proceed.
**Impact:** Every developer who clones the repository must manually run `go mod download` or `go mod tidy` before they can build. In CI/CD pipelines, this can cause unexpected failures.

</details>

<details>
<summary>✅ Fixed Code</summary>

```bash
# Remove go.sum from .gitignore
# .gitignore should NOT contain go.sum

# Regenerate go.sum
go mod tidy

# Commit go.sum to version control
git add go.sum
git commit -m "Add go.sum to version control"
```

```
// .gitignore (correct version)
# Binaries
*.exe
*.exe~
*.dll
*.so
*.dylib

# Test binary
*.test

# Output of go coverage
*.out

# Dependency directories (NOT go.sum!)
vendor/
```

**What changed:** Removed `go.sum` from `.gitignore` and committed it to the repository. The `go.sum` file must always be checked into version control.

</details>

---

## Bug 3: Importing the Wrong Package Path 🟢

**What the code should do:** Import and use a local package named `helpers` from within the same module.

```
// go.mod
module company.com/tools

go 1.21
```

```
// Project structure:
// tools/
// ├── go.mod
// ├── main.go
// └── helpers/
//     └── helpers.go
```

```go
// helpers/helpers.go
package helpers

import "runtime"

func GoVersion() string {
    return runtime.Version()
}
```

```go
// main.go
package main

import (
    "fmt"
    "helpers"
)

func main() {
    fmt.Printf("Running Go version: %s\n", helpers.GoVersion())
}
```

**Expected output:**
```
Running Go version: go1.21.0
```

**Actual output:**
```
main.go:5:2: package helpers is not in std (/usr/local/go/src/helpers)
```

<details>
<summary>💡 Hint</summary>

Look at the import path for the `helpers` package — how does Go resolve import paths in module mode? Does it look in the standard library or in your module?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The import path `"helpers"` is treated as a standard library package. In module mode, local packages must be imported using the full module path prefix.
**Why it happens:** Go resolves import paths by first checking the standard library, then looking in the module's dependency tree. A bare import like `"helpers"` does not tell Go to look inside the current module. The correct path must be `"company.com/tools/helpers"`.
**Impact:** The program fails to compile. This is one of the most common mistakes when transitioning from GOPATH mode to module mode.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
// main.go
package main

import (
    "fmt"
    "company.com/tools/helpers" // Use full module path + package path
)

func main() {
    fmt.Printf("Running Go version: %s\n", helpers.GoVersion())
}
```

**What changed:** Changed the import from `"helpers"` to `"company.com/tools/helpers"` — the full module path prefix followed by the package directory.

</details>

---

## Bug 4: Replace Directive with Wrong Path 🟡

**What the code should do:** Use a local replacement for a dependency during development.

```
// go.mod
module myapp

go 1.21

require github.com/myorg/shared-lib v1.2.0

replace github.com/myorg/shared-lib v1.2.0 => ../shared-lib
```

```
// Directory structure:
// workspace/
// ├── myapp/
// │   ├── go.mod
// │   └── main.go
// └── shared-library/     <-- Note the directory name!
//     ├── go.mod
//     └── lib.go
```

```go
// main.go
package main

import (
    "fmt"
    lib "github.com/myorg/shared-lib"
)

func main() {
    fmt.Println(lib.Version())
}
```

**Expected output:**
```
v1.2.0-local
```

**Actual output:**
```
go: github.com/myorg/shared-lib@v1.2.0 (replaced by ../shared-lib): reading ../shared-lib/go.mod: open /home/user/workspace/shared-lib/go.mod: no such file or directory
```

<details>
<summary>💡 Hint</summary>

Compare the `replace` directive path `../shared-lib` with the actual directory name on disk. Are they the same?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The `replace` directive points to `../shared-lib` but the actual directory is named `../shared-library` (with a `-library` suffix).
**Why it happens:** The `replace` directive uses a filesystem path, and that path must exactly match the directory name. Unlike module paths, there is no resolution mechanism — it is a literal path. A mismatch between the directory name and the path in `replace` causes Go to fail when trying to read `go.mod` from the non-existent directory.
**Impact:** The build fails with a confusing "no such file or directory" error. This is especially common in teams where directory naming conventions differ from module paths.

</details>

<details>
<summary>✅ Fixed Code</summary>

```
// go.mod — fix the replace path to match the actual directory name
module myapp

go 1.21

require github.com/myorg/shared-lib v1.2.0

replace github.com/myorg/shared-lib v1.2.0 => ../shared-library
```

**What changed:** Changed `../shared-lib` to `../shared-library` to match the actual directory name on the filesystem.

</details>

---

## Bug 5: Vendor Mode Conflict with go run 🟡

**What the code should do:** Run a program using vendored dependencies.

```
// go.mod
module myapp

go 1.21

require github.com/google/uuid v1.6.0
```

```go
// main.go
package main

import (
    "fmt"
    "github.com/google/uuid"
)

func main() {
    id := uuid.New()
    fmt.Printf("Generated UUID: %s\n", id.String())
}
```

```bash
# Developer vendored dependencies
go mod vendor

# Then deleted the module cache to save disk space
go clean -modcache

# Now trying to run
go run main.go
```

**Expected output:**
```
Generated UUID: 550e8400-e29b-41d4-a716-446655440000
```

**Actual output:**
```
main.go:5:2: no required module provides package github.com/google/uuid: go.sum file is incomplete
```

<details>
<summary>💡 Hint</summary>

After running `go mod vendor`, how does Go know to look in the `vendor/` directory instead of the module cache? Does `go run` automatically use vendor mode?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** After vendoring and deleting the module cache, `go run main.go` does not automatically use the `vendor/` directory. By default, Go commands use the module cache, not the vendor directory.
**Why it happens:** Go does not implicitly switch to vendor mode just because a `vendor/` directory exists. You must explicitly tell Go to use it with the `-mod=vendor` flag or by setting `GOFLAGS=-mod=vendor`. Without this, Go tries to use the module cache, which was deleted.
**Impact:** The build fails even though all dependencies are present in the `vendor/` directory. This is a common issue in CI/CD environments and air-gapped systems.

</details>

<details>
<summary>✅ Fixed Code</summary>

```bash
# Option 1: Use -mod=vendor flag explicitly
go run -mod=vendor main.go

# Option 2: Set GOFLAGS environment variable
export GOFLAGS=-mod=vendor
go run main.go

# Option 3: Set in go.env (persistent per-user)
go env -w GOFLAGS=-mod=vendor
```

**What changed:** Added `-mod=vendor` flag to tell Go to resolve dependencies from the `vendor/` directory instead of the module cache.

</details>

---

## Bug 6: Build Tag Syntax Mistake 🟡

**What the code should do:** Conditionally compile a file only on Linux.

```
// go.mod
module myapp

go 1.21
```

```go
// server_linux.go
//go:build linux,
package main

import "fmt"

func platformInfo() string {
    return fmt.Sprintf("Running on Linux")
}
```

```go
// server_default.go
//go:build !linux

package main

import "fmt"

func platformInfo() string {
    return fmt.Sprintf("Running on non-Linux platform")
}
```

```go
// main.go
package main

import "fmt"

func main() {
    fmt.Println(platformInfo())
}
```

```bash
GOOS=linux go build -o myapp .
```

**Expected output:**
```
(builds successfully)
Running on Linux
```

**Actual output:**
```
# myapp
server_linux.go:2:1: invalid //go:build constraint: unexpected comma at end
```

<details>
<summary>💡 Hint</summary>

Look carefully at the `//go:build` directive in `server_linux.go`. Is the syntax valid? Pay attention to trailing characters.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The build constraint `//go:build linux,` has a trailing comma after `linux`, which is invalid syntax.
**Why it happens:** The `//go:build` directive uses boolean expressions. A trailing comma is not valid Go build constraint syntax. The comma is likely a leftover from editing or confusion with the old `// +build` format where comma meant AND.
**Impact:** The file is completely excluded from compilation on all platforms because the build constraint is invalid. The compiler reports an error, and neither `server_linux.go` nor any platform-specific file will work correctly.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
// server_linux.go
//go:build linux

package main

import "fmt"

func platformInfo() string {
    return fmt.Sprintf("Running on Linux")
}
```

**What changed:** Removed the trailing comma from `//go:build linux,` to make it `//go:build linux`.

</details>

---

## Bug 7: GOPATH vs Module Mode Confusion 🟡

**What the code should do:** A developer has two projects — one using GOPATH mode, one using modules — and both should build correctly.

```bash
# Environment setup
export GOPATH=/home/dev/gopath
export GO111MODULE=off   # Set globally to support legacy project

# Legacy project (GOPATH mode) — works fine
cd /home/dev/gopath/src/legacy-app
go build .   # OK

# New project (module mode) — should also work
cd /home/dev/projects/new-app
```

```
// /home/dev/projects/new-app/go.mod
module new-app

go 1.21

require github.com/sirupsen/logrus v1.9.3
```

```go
// /home/dev/projects/new-app/main.go
package main

import (
    "github.com/sirupsen/logrus"
)

func main() {
    logrus.Info("Application started")
}
```

```bash
cd /home/dev/projects/new-app
go build .
```

**Expected output:**
```
(builds successfully)
```

**Actual output:**
```
main.go:4:2: cannot find package "github.com/sirupsen/logrus" in any of:
    /usr/local/go/src/github.com/sirupsen/logrus (from $GOROOT)
    /home/dev/gopath/src/github.com/sirupsen/logrus (from $GOPATH)
```

<details>
<summary>💡 Hint</summary>

Look at the `GO111MODULE` environment variable. What does `off` mean? Does Go respect the `go.mod` file when module mode is disabled?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `GO111MODULE=off` is set globally, which disables module mode entirely — even for projects that have a `go.mod` file.
**Why it happens:** When `GO111MODULE` is set to `off`, Go ignores `go.mod` files and resolves all imports using GOPATH. The new project uses modules and has dependencies declared in `go.mod`, but Go does not read that file. Instead, it looks for packages in `$GOPATH/src/`, where they do not exist.
**Impact:** Any module-based project fails to build. The `go.mod` and `go.sum` files are completely ignored.

</details>

<details>
<summary>✅ Fixed Code</summary>

```bash
# Option 1: Set GO111MODULE to "auto" (default since Go 1.16)
# Go will use module mode when go.mod is present, GOPATH mode otherwise
export GO111MODULE=auto

# Option 2: Override per-project using go env
cd /home/dev/projects/new-app
GO111MODULE=on go build .

# Option 3 (recommended): Remove the global override entirely
unset GO111MODULE
# Since Go 1.16+, module mode is the default when go.mod is present
```

**What changed:** Changed `GO111MODULE` from `off` to `auto` (or `on` for the specific project), allowing Go to use module mode when a `go.mod` file is present.

</details>

---

## Bug 8: Private Module Proxy Misconfiguration 🔴

**What the code should do:** Fetch a private module from a company's internal Git server.

```
// go.mod
module company.com/team/service

go 1.21

require company.com/internal/auth v0.5.0
```

```go
// main.go
package main

import (
    "fmt"
    "company.com/internal/auth"
)

func main() {
    token := auth.GenerateToken("user123")
    fmt.Printf("Token: %s\n", token)
}
```

```bash
# Environment setup
export GOPROXY=https://proxy.golang.org,direct
export GONOSUMCHECK=company.com/internal/*

go mod download
```

**Expected output:**
```
go: downloading company.com/internal/auth v0.5.0
```

**Actual output:**
```
go: company.com/internal/auth@v0.5.0: reading https://proxy.golang.org/company.com/internal/auth/@v/v0.5.0.info: 404 Not Found
go: company.com/internal/auth@v0.5.0: reading https://proxy.golang.org/company.com/internal/auth/@v/v0.5.0.info: 410 Gone
    server response: not found: company.com/internal/auth@v0.5.0: unrecognized import path "company.com/internal/auth": https fetch: Get "https://company.com/internal/auth?go-get=1": dial tcp: lookup company.com: no such host
```

<details>
<summary>💡 Hint</summary>

The `GOPROXY` sends all requests to the public proxy first. Private modules are not available on the public proxy. What environment variable tells Go to skip the proxy for certain module paths?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `GOPRIVATE` is not set. Go sends all module requests — including private ones — to `proxy.golang.org`, which does not have access to `company.com/internal/*`.
**Why it happens:** The `GOPROXY` variable is set to use the public proxy first, then fall back to `direct`. However, the public proxy returns 404/410 for private modules it cannot access. While `GONOSUMCHECK` prevents checksum verification, it does not prevent the proxy from being queried. The `GOPRIVATE` variable (or `GONOSUMDB`/`GONOPROXY`) is needed to tell Go to bypass the proxy entirely for matching module paths.
**Impact:** Private modules cannot be downloaded. The error messages can be misleading because they show both the proxy failure and the direct fetch failure (which may fail for different reasons like missing Git credentials).
**How to detect:** Run `go env GOPRIVATE` — if it is empty and you use private modules, this is the problem.

</details>

<details>
<summary>✅ Fixed Code</summary>

```bash
# Set GOPRIVATE to bypass proxy AND checksum DB for private modules
export GOPRIVATE=company.com/internal/*

# GOPROXY can remain as-is — GOPRIVATE overrides it for matching paths
export GOPROXY=https://proxy.golang.org,direct

# Now Go will fetch company.com/internal/* directly via Git
go mod download

# To make it persistent:
go env -w GOPRIVATE=company.com/internal/*
```

```bash
# Also ensure Git is configured for private access:
git config --global url."git@company.com:".insteadOf "https://company.com/"
```

**What changed:** Added `GOPRIVATE=company.com/internal/*` so Go bypasses the public proxy and checksum database for all modules under `company.com/internal/`.
**Alternative fix:** Use `GONOPROXY` and `GONOSUMDB` separately if you want finer-grained control over which modules skip the proxy vs. the checksum database.

</details>

---

## Bug 9: Module Version Conflict with Indirect Dependency 🔴

**What the code should do:** Build a project that uses two libraries which both depend on `golang.org/x/text` but at different versions.

```
// go.mod
module myapp

go 1.21

require (
    github.com/example/libA v1.0.0
    github.com/example/libB v2.0.0
)

require golang.org/x/text v0.3.0 // indirect — manually pinned to old version
```

```go
// main.go
package main

import (
    "fmt"
    "github.com/example/libA"
    "github.com/example/libB"
)

func main() {
    fmt.Println(libA.Process("hello"))
    fmt.Println(libB.Transform("world"))
}
```

```
// libA's go.mod requires: golang.org/x/text v0.9.0
// libB's go.mod requires: golang.org/x/text v0.14.0
```

```bash
go mod tidy
go build .
```

**Expected output:**
```
(builds successfully using golang.org/x/text v0.14.0 — the highest required version)
```

**Actual output (after manual editing of go.sum to force v0.3.0):**
```
go: inconsistent versions: golang.org/x/text@v0.3.0 used by myapp, but golang.org/x/text@v0.14.0 required by github.com/example/libB@v2.0.0
```

Or at runtime, if the developer managed to build with stale cache:
```
panic: runtime error: invalid memory address or nil pointer dereference
```

<details>
<summary>💡 Hint</summary>

Look at the pinned version of `golang.org/x/text` in `go.mod`. Is `v0.3.0` compatible with what `libA` and `libB` require? What does Go's Minimum Version Selection (MVS) algorithm say?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The developer manually pinned `golang.org/x/text` to `v0.3.0` in `go.mod`, but `libA` requires `v0.9.0` and `libB` requires `v0.14.0`. Go's Minimum Version Selection (MVS) should select `v0.14.0` (the maximum of all minimums), but the manual pin to `v0.3.0` creates an inconsistency.
**Why it happens:** Developers sometimes manually edit `go.mod` to pin indirect dependencies to older versions, thinking it will reduce binary size or avoid changes. However, if a direct dependency requires a higher version, Go cannot satisfy both constraints. Editing `go.sum` to force the old version can lead to checksum mismatches or runtime panics due to API incompatibilities.
**Impact:** Build failures with "inconsistent versions" errors, or worse, runtime panics if the build somehow succeeds with an incompatible version (e.g., from a stale module cache).
**How to detect:** Run `go mod tidy` — it will automatically resolve versions using MVS and update `go.mod` accordingly.

</details>

<details>
<summary>✅ Fixed Code</summary>

```bash
# Let Go resolve the correct version automatically
go mod tidy
```

```
// go.mod — after go mod tidy (versions resolved by MVS)
module myapp

go 1.21

require (
    github.com/example/libA v1.0.0
    github.com/example/libB v2.0.0
)

require golang.org/x/text v0.14.0 // indirect — MVS selects highest minimum
```

**What changed:** Removed the manual version pin and let `go mod tidy` resolve `golang.org/x/text` to `v0.14.0` using Minimum Version Selection. Never manually downgrade indirect dependencies below what your direct dependencies require.

</details>

---

## Bug 10: CGO_ENABLED Cross-Compilation Failure 🔴

**What the code should do:** Cross-compile a Go program that uses `net` package for Linux from a macOS development machine.

```
// go.mod
module myapp

go 1.21
```

```go
// main.go
package main

import (
    "fmt"
    "net"
    "os"
    "runtime"
)

func main() {
    fmt.Printf("OS: %s, Arch: %s\n", runtime.GOOS, runtime.GOARCH)

    addrs, err := net.LookupHost("localhost")
    if err != nil {
        fmt.Fprintf(os.Stderr, "DNS lookup failed: %v\n", err)
        os.Exit(1)
    }
    for _, addr := range addrs {
        fmt.Printf("localhost resolves to: %s\n", addr)
    }
}
```

```bash
# Cross-compile for Linux from macOS
GOOS=linux GOARCH=amd64 go build -o myapp-linux main.go
```

**Expected output:**
```
(builds a Linux binary successfully)
```

**Actual output:**
```
# runtime/cgo
gcc: error: unrecognized command-line option '-marm64'
# or on some systems:
/usr/bin/x86_64-linux-gnu-gcc: not found
```

<details>
<summary>💡 Hint</summary>

When you cross-compile, does the `net` package use CGO by default? What happens when CGO is enabled but there is no cross-compiler for the target platform? Check what `CGO_ENABLED` defaults to.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** CGO is enabled by default when the build target matches the host (and sometimes even during cross-compilation if a C compiler is detected). The `net` package has both a CGO and a pure-Go implementation. When CGO is enabled during cross-compilation, Go tries to use the system's C compiler, which cannot produce binaries for the target platform without a proper cross-compilation toolchain.
**Why it happens:** On macOS, the default C compiler (`clang`) targets macOS/ARM64 or macOS/AMD64. When cross-compiling to `GOOS=linux`, Go still tries to invoke the C compiler for CGO-dependent packages like `net` (which uses CGO for DNS resolution by default). Without a Linux-targeting cross-compiler installed, this fails.
**Impact:** Cross-compilation fails with cryptic GCC/Clang errors. The developer might not realize that `net` uses CGO at all.
**Go spec reference:** https://pkg.go.dev/net — "On Unix systems, the resolver has two options for resolving names... cgo-based resolver or pure Go resolver"

</details>

<details>
<summary>✅ Fixed Code</summary>

```bash
# Option 1: Disable CGO entirely (recommended for cross-compilation)
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o myapp-linux main.go

# Option 2: Force pure-Go DNS resolver while keeping CGO
GOOS=linux GOARCH=amd64 go build -tags netgo -o myapp-linux main.go

# Option 3: Install a cross-compiler (for when CGO is truly needed)
# On macOS with Homebrew:
brew install FiloSottile/musl-cross/musl-cross
CC=x86_64-linux-musl-gcc CGO_ENABLED=1 GOOS=linux GOARCH=amd64 go build -o myapp-linux main.go
```

**What changed:** Set `CGO_ENABLED=0` to disable CGO, forcing Go to use the pure-Go implementation of all packages including `net`. This allows cross-compilation without a C cross-compiler.
**Alternative fix:** Use the `-tags netgo` build tag to force the pure-Go DNS resolver while keeping CGO enabled for other packages, or install a proper cross-compilation toolchain.

</details>

---

## Score Card

Track your progress:

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

### Rating:
- **10/10 without hints** → Senior-level debugging skills
- **7-9/10** → Solid middle-level understanding
- **4-6/10** → Good junior, keep practicing
- **< 4/10** → Review the topic fundamentals first
