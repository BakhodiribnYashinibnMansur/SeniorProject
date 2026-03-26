# Go Roadmap

- Roadmap: https://roadmap.sh/golang
- PDF: [golang.pdf](./golang.pdf)

## 1. Introduction to Go
- 1.1 Why use Go
- 1.2 History of Go
- 1.3 Setting up the Environment
- 1.4 Hello World in Go
- 1.5 `go` command

## 2. Language Basics

### 2.1 Variables & Constants
- 2.1.1 `var` vs `:=`
- 2.1.2 Zero Values
- 2.1.3 `const` and `iota`
- 2.1.4 Scope and Shadowing

### 2.2 Data Types
- 2.2.1 Boolean
- 2.2.2 Numeric Types
  - 2.2.2.1 Integers (Signed, Unsigned)
  - 2.2.2.2 Floating Points
  - 2.2.2.3 Complex Numbers
- 2.2.3 Runes
- 2.2.4 Strings
  - 2.2.4.1 Raw String Literals
  - 2.2.4.2 Interpreted String Literals
- 2.2.5 Type Conversion
- 2.2.6 Commands & Docs

### 2.3 Composite Types
- 2.3.1 Arrays
- 2.3.2 Slices
  - 2.3.2.1 Capacity and Growth
  - 2.3.2.2 `make()`
  - 2.3.2.3 Slice to Array Conversion
  - 2.3.2.4 Array to Slice Conversion
- 2.3.3 Strings
- 2.3.4 Maps
  - 2.3.4.1 Comma-Ok Idiom
- 2.3.5 Structs
  - 2.3.5.1 Struct Tags & JSON
  - 2.3.5.2 Embedding Structs

### 2.4 Conditionals
- 2.4.1 `if`
- 2.4.2 `if-else`
- 2.4.3 `switch`

### 2.5 Loops
- 2.5.1 `for` loop
- 2.5.2 `for range`
  - 2.5.2.1 Iterating Maps
  - 2.5.2.2 Iterating Strings
- 2.5.3 `break`
- 2.5.4 `continue`
- 2.5.5 `goto` (discouraged)

### 2.6 Functions
- 2.6.1 Functions Basics
- 2.6.2 Variadic Functions
- 2.6.3 Multiple Return Values
- 2.6.4 Anonymous Functions
- 2.6.5 Closures
- 2.6.6 Named Return Values
- 2.6.7 Call by Value

### 2.7 Pointers
- 2.7.1 Pointers Basics
- 2.7.2 Pointers with Structs
- 2.7.3 With Maps & Slices
- 2.7.4 Memory Management
  - 2.7.4.1 Garbage Collection

## 3. Methods and Interfaces
- 3.1 Methods vs Functions
- 3.2 Pointer Receivers
- 3.3 Value Receivers
- 3.4 Interfaces Basics
  - 3.4.1 Empty Interfaces
  - 3.4.2 Embedding Interfaces
  - 3.4.3 Type Assertions
  - 3.4.4 Type Switch

## 4. Generics
- 4.1 Why Generics?
- 4.2 Generic Functions
- 4.3 Generic Types / Interfaces
- 4.4 Type Constraints
- 4.5 Type Inference

## 5. Error Handling
- 5.1 Error Handling Basics
- 5.2 `error` interface
- 5.3 `errors.New`
- 5.4 `fmt.Errorf`
- 5.5 Wrapping/Unwrapping Errors
- 5.6 Sentinel Errors
- 5.7 `panic` and `recover`
- 5.8 Stack Traces & Debugging

## 6. Code Organization

### 6.1 Modules & Dependencies
- 6.1.1 `go mod init`
- 6.1.2 `go mod tidy`
- 6.1.3 `go mod vendor`

### 6.2 Packages
- 6.2.1 Package Import Rules
- 6.2.2 Using 3rd Party Packages
- 6.2.3 Publishing Modules

## 7. Concurrency
- 7.1 Goroutines

### 7.2 Channels
- 7.2.1 Buffered vs Unbuffered
- 7.2.2 Select Statement
- 7.2.3 Worker Pools

### 7.3 `sync` Package
- 7.3.1 Mutexes
- 7.3.2 WaitGroups

### 7.4 `context` Package
- 7.4.1 Deadlines & Cancellations
- 7.4.2 Common Usecases

### 7.5 Concurrency Patterns
- 7.5.1 fan-in
- 7.5.2 fan-out
- 7.5.3 pipeline
- 7.5.4 Race Detection

## 8. Standard Library
- 8.1 I/O & File Handling
- 8.2 `flag`
- 8.3 `time`
- 8.4 `encoding/json`
- 8.5 `os`
- 8.6 `bufio`
- 8.7 `slog`
- 8.8 `regexp`
- 8.9 `go:embed` for embedding

## 9. Testing & Benchmarking
- 9.1 `testing` package basics
- 9.2 Table-driven Tests
- 9.3 Mocks and Stubs
- 9.4 `httptest` for HTTP Tests
- 9.5 Benchmarks
- 9.6 Coverage

## 10. Ecosystem & Popular Libraries

### 10.1 Building CLIs
- 10.1.1 Cobra
- 10.1.2 urfave/cli
- 10.1.3 bubbletea

### 10.2 Web Development
- 10.2.1 `net/http` (standard)
- 10.2.2 Frameworks (Optional)
  - 10.2.2.1 gin
  - 10.2.2.2 echo
  - 10.2.2.3 fiber
  - 10.2.2.4 beego
- 10.2.3 gRPC & Protocol Buffers

### 10.3 ORMs & DB Access
- 10.3.1 pgx
- 10.3.2 GORM

### 10.4 Logging
- 10.4.1 Zerolog
- 10.4.2 Zap

### 10.5 Realtime Communication
- 10.5.1 Melody
- 10.5.2 Centrifugo

## 11. Go Toolchain and Tools

### 11.1 Core Go Commands
- 11.1.1 `go run`
- 11.1.2 `go build`
- 11.1.3 `go install`
- 11.1.4 `go fmt`
- 11.1.5 `go mod`
- 11.1.6 `go test`
- 11.1.7 `go clean`
- 11.1.8 `go doc`
- 11.1.9 `go version`

### 11.2 Code Generation / Build Tags
- 11.2.1 `go generate`
- 11.2.2 Build Tags

### 11.3 Code Quality and Analysis
- 11.3.1 `go vet`
- 11.3.2 `goimports`
- 11.3.3 Linters
  - 11.3.3.1 revive
  - 11.3.3.2 staticcheck
  - 11.3.3.3 golangci-lint

### 11.4 Security
- 11.4.1 govulncheck

### 11.5 Performance and Debugging
- 11.5.1 pprof
- 11.5.2 trace
- 11.5.3 Race Detector

### 11.6 Deployment & Tooling
- 11.6.1 Cross-compilation
- 11.6.2 Building Executables

## 12. Advanced Topics
- 12.1 Memory Mgmt. in Depth
- 12.2 Escape Analysis
- 12.3 Reflection
- 12.4 Unsafe Package
- 12.5 Build Constraints & Tags
- 12.6 CGO Basics
- 12.7 Compiler & Linker Flags
- 12.8 Plugins & Dynamic Loading
