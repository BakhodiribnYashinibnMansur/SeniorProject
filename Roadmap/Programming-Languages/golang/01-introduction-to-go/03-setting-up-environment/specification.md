# Setting Up Environment — Specification

> **Official Specification Reference**
> Source: [Go Language Specification](https://go.dev/ref/spec) — §Source file organization
> Also: [Go Module Reference](https://go.dev/ref/mod) | [go.dev/doc/install](https://go.dev/doc/install)

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

The Go language specification defines how source files are organized and how packages and modules relate to each other. The `go` toolchain interprets these rules during the build process.

From the spec §Source file organization:
> "Each source file consists of a package clause defining the package to which it belongs, followed by a possibly empty set of import declarations that declare packages whose contents it wishes to use, followed by a possibly empty set of declarations."

From the Go Module Reference:
> "A module is a collection of packages that are released, versioned, and distributed together. Modules may be downloaded directly from version control repositories or from module proxy servers."

### Environment model

The Go toolchain uses a set of environment variables to determine where to find source code, where to write compiled artifacts, and how to handle module resolution:

| Variable | Default | Description |
|----------|---------|-------------|
| `GOROOT` | Install dir | Location of the Go standard library and toolchain |
| `GOPATH` | `~/go` | Root for Go workspace (downloaded modules, built binaries) |
| `GOMODCACHE` | `$GOPATH/pkg/mod` | Cache directory for downloaded module source code |
| `GOCACHE` | OS-specific | Build cache (compiled artifacts) |
| `GOBIN` | `$GOPATH/bin` | Where `go install` puts compiled executables |
| `GOPROXY` | `https://proxy.golang.org,direct` | Module proxy URL list |
| `GONOSUMCHECK` | `` | Patterns of modules to skip checksum verification |
| `GONOSUMDB` | `` | Patterns of modules not to look up in sum database |
| `GOFLAGS` | `` | Default flags for `go` commands |
| `CGO_ENABLED` | `1` | Whether to enable C interop (cgo) |
| `GOOS` | host OS | Target operating system for cross-compilation |
| `GOARCH` | host arch | Target CPU architecture for cross-compilation |

---

## 2. Formal Grammar

### Module file grammar (go.mod)

The `go.mod` file has a formally specified grammar in the Go Module Reference:

```ebnf
GoMod          = { Directive } .
Directive      = ModuleDirective
               | GoDirective
               | ToolchainDirective
               | RequireDirective
               | ReplaceDirective
               | ExcludeDirective
               | RetractDirective
               .
ModuleDirective  = "module" ModulePath [ GoVersion ] newline .
GoDirective      = "go" GoVersion newline .
ToolchainDirective = "toolchain" ToolchainName newline .
RequireDirective = "require" ( RequireSpec | "(" newline { RequireSpec } ")" newline ) .
RequireSpec    = ModulePath Version newline .
ReplaceDirective = "replace" ( ReplaceSpec | "(" newline { ReplaceSpec } ")" newline ) .
ReplaceSpec    = ModulePath [ Version ] "=>" FilePath newline
               | ModulePath [ Version ] "=>" ModulePath Version newline .
ExcludeDirective = "exclude" ( ExcludeSpec | "(" newline { ExcludeSpec } ")" newline ) .
ExcludeSpec    = ModulePath Version newline .
RetractDirective = "retract" ( RetractSpec | "(" newline { RetractSpec } ")" newline ) .
RetractSpec    = Version newline | "[" Version "," Version "]" newline .

ModulePath     = { ModulePathElement "/" } ModulePathElement .
GoVersion      = "1." DecimalDigit+ ( "." DecimalDigit+ )? .
Version        = "v" SemVer .
```

### Package path grammar

```ebnf
ImportPath    = string_lit .
PackageClause = "package" PackageName .
PackageName   = identifier .
```

### Source file organization grammar

```ebnf
SourceFile    = PackageClause ";" { ImportDecl ";" } { TopLevelDecl ";" } .
ImportDecl    = "import" ( ImportSpec | "(" { ImportSpec ";" } ")" ) .
ImportSpec    = [ "." | PackageName ] ImportPath .
```

---

## 3. Core Rules & Constraints

### Rule 1: Module path determines the import path prefix for all packages in the module

From the Go Module Reference:
> "A module path is the canonical name for a module, declared with the module directive in the module's go.mod file."

```
// go.mod
module github.com/example/myapp

go 1.21
```

This means:
- A file at `./server/http.go` has import path `github.com/example/myapp/server`
- Another module imports it with `import "github.com/example/myapp/server"`

```go
// File: github.com/example/myapp/server/http.go
package server

// HTTPServer handles HTTP connections
type HTTPServer struct {
    Port int
}
```

```go
// File: github.com/example/myapp/main.go
package main

import (
    "fmt"
    "github.com/example/myapp/server"
)

func main() {
    s := server.HTTPServer{Port: 8080}
    fmt.Println(s.Port)
}
```

### Rule 2: A Go workspace directory contains src, bin, and pkg subdirectories (GOPATH mode, pre-modules)

This is the legacy layout. Module mode is now the default. From the official docs:
> "The GOPATH environment variable lists places to look for Go code. On Unix, the value is a colon-separated string. On Windows, the value is a semicolon-separated string."

Pre-modules layout (historical reference):

```
$GOPATH/
    src/                  # source code
        github.com/user/
            myproject/
                main.go
    pkg/                  # compiled package archives
        linux_amd64/
    bin/                  # compiled executables
        myproject
```

### Rule 3: go.sum provides cryptographic verification of module content

From the Go Module Reference:
> "Each line in go.sum has three fields separated by spaces: a module path, a version, and a hash."

```
github.com/pkg/errors v0.9.1 h1:FEBLx1zS214owpjy7qsBeixbURkuhQAwrK5UwLGTwt38=
github.com/pkg/errors v0.9.1/go.mod h1:bwawxfHBFNV+L2hUp1rHADufV3IMtnDRdf1r5NINEl0=
```

The hash format is `h1:` followed by a base64-encoded SHA-256 hash of a hash tree of the module zip file.

---

## 4. Type Rules

### GOPATH and module path resolution rules

The Go toolchain resolves import paths using these rules (in priority order):

| Import path type | Resolution |
|-----------------|------------|
| Standard library (`fmt`, `os`, etc.) | Found in `$GOROOT/src` |
| Local package (starts with `./` or `../`) | Resolved relative to current directory |
| Module dependency | Found in `$GOMODCACHE` after download |
| `replace` directive target | Redirected to local path or alternative version |

### Versioning semantics

From the Go Module Reference:
> "Module versions are written as semver strings. A major version greater than 1 must be added to the module path as a suffix."

| Module path | Meaning |
|-------------|---------|
| `github.com/user/pkg` | v0.x.x or v1.x.x |
| `github.com/user/pkg/v2` | v2.x.x |
| `github.com/user/pkg/v3` | v3.x.x |

```go
import (
    "github.com/user/pkg"    // v1 or v0
    pkgv2 "github.com/user/pkg/v2"  // v2, given alias to avoid collision
)
```

---

## 5. Behavioral Specification

### Module resolution algorithm

When the `go` toolchain needs to resolve an import:

1. Check if the import path is a standard library package (`$GOROOT/src`).
2. If in module mode, read `go.mod` to find the module containing the package.
3. Download the module if not present in `$GOMODCACHE`.
4. Verify the download hash against `go.sum`.
5. Build the package from the cached source.

### GOPATH mode vs Module mode

| Behavior | GOPATH mode (legacy) | Module mode (current) |
|----------|---------------------|----------------------|
| Dependency versioning | None (uses latest code in `src/`) | Semantic versioning via `go.mod` |
| Reproducible builds | No | Yes (go.sum locks hashes) |
| Code location | Must be under `$GOPATH/src` | Anywhere on the filesystem |
| Activation | No `go.mod` in parent directories | `go.mod` exists in module root |

Module mode has been the default since Go 1.16. GOPATH mode is still supported but discouraged.

### `go env` — querying the environment

```bash
$ go env GOPATH
/Users/username/go

$ go env GOMODCACHE
/Users/username/go/pkg/mod

$ go env GOPROXY
https://proxy.golang.org,direct

$ go env -json
{
    "GOARCH": "arm64",
    "GOBIN": "",
    "GOCACHE": "/Users/username/Library/Caches/go-build",
    ...
}
```

---

## 6. Defined vs Undefined Behavior

### Defined behavior in module resolution

| Scenario | Defined outcome |
|----------|----------------|
| Two dependencies require different versions of same module | Minimum version selection (MVS): highest required version wins |
| `replace` directive replaces a module | Local path or alternate version used unconditionally |
| Hash mismatch with go.sum | Build fails: `verifying module: checksum mismatch` |
| Network unavailable, module in cache | Build succeeds using cached copy |
| `GONOSUMCHECK` matches a module | Checksum verification skipped for that module |

### Minimum Version Selection (MVS)

From the module reference:
> "Unlike most dependency management systems, the go command always selects the minimum (oldest) version of each module consistent with requirements. This ensures that builds are always reproducible and that any change to requirements must be explicit."

```
Module A requires:
  B v1.2
  C v1.0

Module B v1.2 requires:
  D v1.3

Module C v1.0 requires:
  D v1.1

Result: D v1.3 is selected (maximum of v1.3 and v1.1)
```

### Undefined behavior

| Scenario | Status |
|----------|--------|
| Modifying files in `$GOMODCACHE` | Undefined; cache may be corrupted |
| Hand-editing `go.sum` | Undefined; verification will fail |
| Building with mismatched `go.mod` and `go.sum` | Build error |
| Using `replace` with a non-existent local path | Build error |

---

## 7. Edge Cases from Spec

### Edge Case 1: The `internal` directory restricts import access

From the Go toolchain documentation:
> "An import of a path containing the element 'internal' is disallowed if the importing code is outside the tree rooted at the parent of the 'internal' directory."

```
mymodule/
    internal/
        helper/
            helper.go  // import path: mymodule/internal/helper
    cmd/
        main.go        // CAN import mymodule/internal/helper
    otherpkg/
        util.go        // CAN import mymodule/internal/helper

// External module CANNOT import mymodule/internal/helper
// Compile error: use of internal package mymodule/internal/helper not allowed
```

### Edge Case 2: The `vendor` directory overrides module downloads

```
project/
    go.mod
    go.sum
    vendor/
        modules.txt
        github.com/pkg/errors/
            errors.go
    main.go
```

From the module reference:
> "When vendoring is enabled, build commands like go build and go test will load packages from the vendor directory instead of downloading modules from their sources into the module cache."

Activate with:
```bash
go mod vendor        # populate vendor directory
go build -mod=vendor # build using vendor
```

### Edge Case 3: `GONOSUMCHECK` and private modules

Private modules (internal company code) cannot be verified against the public sum database. Configure:

```bash
export GONOSUMCHECK="gitlab.internal.company.com/*"
export GOPROXY="off"  # no proxy for private modules
export GONOSUMDB="gitlab.internal.company.com/*"
```

Or in `go env -w` (persisted to `$GOENV`):
```bash
go env -w GONOSUMCHECK=gitlab.internal.company.com/*
go env -w GOPROXY=https://proxy.golang.org,direct
```

### Edge Case 4: `replace` directives do not propagate to dependents

From the module reference:
> "Replace directives only apply in the main module's go.mod file; replace directives in other modules are ignored. See Minimal version selection for details."

This means: if your library uses a `replace` directive for local development, consumers of your library will NOT inherit that replacement.

---

## 8. Version History

| Go Version | Environment / Module Change |
|------------|-----------------------------|
| Pre-Go 1.11 | GOPATH mode only. All code in `$GOPATH/src`. |
| Go 1.11 (2018) | Go Modules introduced as opt-in (`GO111MODULE=on`). `go.mod` and `go.sum` added. |
| Go 1.12 (2019) | Module mode on when `go.mod` exists in the directory tree. |
| Go 1.13 (2019) | `GOPROXY` default set to `proxy.golang.org`. `GONOSUMDB` added. |
| Go 1.14 (2020) | `go mod vendor` and `-mod=vendor` support improved. `go.sum` required. |
| Go 1.15 (2020) | `GOMODCACHE` variable introduced (was always `$GOPATH/pkg/mod`). |
| Go 1.16 (2021) | Module mode is **default** (`GO111MODULE=on` by default). `go install pkg@version` added. |
| Go 1.17 (2021) | All dependencies listed in `go.mod` (not just direct). Module graph pruning. |
| Go 1.18 (2022) | Workspaces: `go.work` file for multi-module development. |
| Go 1.21 (2023) | `toolchain` directive in `go.mod`. Automatic toolchain download. |

### Go Workspaces (since Go 1.18)

```ebnf
GoWork      = { Directive } .
UseDirective = "use" FilePath newline .
ReplaceDirective = "replace" ReplaceSpec newline .
GoDirective  = "go" GoVersion newline .
```

```
// go.work
go 1.21

use ./myapp
use ./mylib
```

This allows working with multiple local modules simultaneously without editing `go.mod` replace directives.

---

## 9. Implementation-Specific Behavior

### The `gc` toolchain directory layout

After installing Go (e.g., to `/usr/local/go`):

```
/usr/local/go/
    bin/
        go          # the go command
        gofmt       # the formatter
    src/            # standard library source
        fmt/
        os/
        net/
        ...
    pkg/
        tool/
            linux_amd64/
                compile   # compiler
                link      # linker
                vet       # static analysis
    lib/
        time/
            zoneinfo.zip
```

### Build cache location by OS

| OS | Default build cache |
|----|-------------------|
| Linux | `~/.cache/go-build` |
| macOS | `~/Library/Caches/go-build` |
| Windows | `%LocalAppData%\go-build` |

The build cache stores compiled packages and test results. It is automatically managed and pruned by the toolchain.

### CGO and cross-compilation

When `CGO_ENABLED=0`, Go produces fully static binaries with no C library dependency:

```bash
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o myapp ./...
```

When `CGO_ENABLED=1` (default), Go may link against the system C library (`libc`), which means the binary may not be portable across different Linux distributions.

---

## 10. Spec Compliance Checklist

- [ ] `go.mod` exists at the root of every module
- [ ] Module path in `go.mod` matches the intended import path prefix
- [ ] `go.sum` is committed to version control alongside `go.mod`
- [ ] `go mod tidy` has been run (removes unused dependencies, adds missing ones)
- [ ] No hand-editing of `go.sum`
- [ ] Major version suffix added to module path for v2+ (`/v2`, `/v3`, etc.)
- [ ] `internal` packages used to restrict access to implementation details
- [ ] `GOPROXY` and `GONOSUMCHECK` configured correctly for private modules
- [ ] `vendor` directory (if present) was populated with `go mod vendor` and is up to date
- [ ] `go.work` used for multi-module local development (not `replace` directives in production modules)
- [ ] `CGO_ENABLED=0` set for containerized deployments requiring static binaries
- [ ] `GOFLAGS` not set to values that would break CI reproducibility

---

## 11. Official Examples

### Initializing a new module

```bash
mkdir myapp && cd myapp
go mod init github.com/example/myapp
# Creates:
# go.mod:
#   module github.com/example/myapp
#   go 1.21
```

### Complete go.mod example

```
module github.com/example/myapp

go 1.21.0

toolchain go1.21.3

require (
    github.com/gin-gonic/gin v1.9.1
    golang.org/x/crypto v0.17.0
)

require (
    // indirect dependencies (populated by go mod tidy)
    github.com/bytedance/sonic v1.9.1 // indirect
    github.com/go-playground/validator/v10 v10.15.5 // indirect
)
```

### Querying and setting environment variables

```bash
# View all Go environment variables
go env

# View a specific variable
go env GOPATH

# Set a persistent variable (stored in $GOENV file)
go env -w GOPROXY=https://goproxy.cn,direct

# Unset a persistent variable
go env -u GOPROXY

# View where the GOENV file is
go env GOENV
```

### Setting up a workspace (go 1.18+)

```bash
mkdir workspace && cd workspace
git clone https://github.com/example/app
git clone https://github.com/example/lib

go work init ./app ./lib
# Creates go.work:
#   go 1.21
#
#   use ./app
#   use ./lib
```

Now changes to `lib` are immediately reflected when building `app`, without publishing.

### Verifying module integrity

```bash
# Verify all modules in the module cache
go mod verify

# Download all dependencies
go mod download

# List all direct and indirect dependencies
go list -m all

# Show why a dependency is needed
go mod why github.com/pkg/errors
```

---

## 12. Related Spec Sections

| Resource | URL | Relevance |
|----------|-----|-----------|
| Source file organization | https://go.dev/ref/spec#Source_file_organization | Package/import grammar |
| Go Module Reference | https://go.dev/ref/mod | Complete module specification |
| go.mod file reference | https://go.dev/ref/mod#go-mod-file | `go.mod` grammar and directives |
| Minimum Version Selection | https://go.dev/ref/mod#minimal-version-selection | Dependency resolution algorithm |
| Module proxy protocol | https://go.dev/ref/mod#module-proxy | How `GOPROXY` works |
| GOPATH reference | https://pkg.go.dev/cmd/go#hdr-GOPATH_environment_variable | Legacy GOPATH semantics |
| go command documentation | https://pkg.go.dev/cmd/go | Full `go` tool reference |
| Installing Go | https://go.dev/doc/install | Official installation guide |
| Getting started tutorial | https://go.dev/doc/tutorial/getting-started | First module walkthrough |
