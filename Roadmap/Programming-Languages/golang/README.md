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

### 7.0 Introduction
- 7.0.1 What is Concurrency (Concurrency vs Parallelism)
- 7.0.2 CSP Model (Communicating Sequential Processes)
- 7.0.3 Go Runtime & GMP Scheduler
- 7.0.4 Go Memory Model (happens-before)
- 7.0.5 When to Use Concurrency (and when not to)

### 7.1 Goroutines
- 7.1.1 Overview (creation & syntax)
- 7.1.2 Goroutines vs OS Threads
- 7.1.3 Stack Growth
- 7.1.4 Runtime Management
- 7.1.5 Best Practices
- 7.1.6 Common Pitfalls

### 7.2 Channels
- 7.2.1 Buffered vs Unbuffered
- 7.2.2 Select Statement
- 7.2.3 Worker Pools
- 7.2.4 Channel Direction
- 7.2.5 Nil Channels
- 7.2.6 Closing Channels
- 7.2.7 Range over Channels

### 7.3 `sync` Package
- 7.3.1 Mutexes
- 7.3.2 WaitGroups
- 7.3.3 Once
- 7.3.4 Cond
- 7.3.5 Pool
- 7.3.6 Map
- 7.3.7 Atomic (`sync/atomic`)

### 7.4 `context` Package
- 7.4.1 Deadlines & Cancellations
- 7.4.2 Common Usecases
- 7.4.3 Context Values
- 7.4.4 Context Tree
- 7.4.5 Context Internals

### 7.5 Concurrency Patterns
- 7.5.1 fan-in
- 7.5.2 fan-out
- 7.5.3 pipeline
- 7.5.4 Race Detection
- 7.5.5 Future / Promise
- 7.5.6 Broadcast Pattern

### 7.6 Errgroup & `x/sync`
- 7.6.1 `errgroup.Group`
- 7.6.2 `semaphore.Weighted`
- 7.6.3 `singleflight.Group`

### 7.7 Goroutine Lifecycle & Leaks
- 7.7.1 Goroutine Lifecycle
- 7.7.2 Detecting Leaks (`runtime.NumGoroutine`, `pprof`)
- 7.7.3 Preventing Leaks
- 7.7.4 pprof Tools

### 7.8 Deadlock, Livelock, Starvation
- 7.8.1 Deadlock
- 7.8.2 Livelock
- 7.8.3 Starvation

### 7.9 Channel Internals
- 7.9.1 `hchan` Struct
- 7.9.2 Runtime Behavior
- 7.9.3 Buffer Mechanics
- 7.9.4 Send / Receive Flow

### 7.10 Scheduler Deep-Dive
- 7.10.1 GMP Model
- 7.10.2 Preemption (Go 1.14+)
- 7.10.3 `GOMAXPROCS` Tuning
- 7.10.4 Work Stealing
- 7.10.5 Syscall Handling

### 7.11 Advanced Channel Patterns
- 7.11.1 or-done-channel
- 7.11.2 tee-channel
- 7.11.3 bridge-channel
- 7.11.4 Generator
- 7.11.5 Rate Limiter

### 7.12 Lock-Free Programming
- 7.12.1 CAS-based Algorithms
- 7.12.2 ABA Problem
- 7.12.3 Lock-Free Data Structures (queue, stack)
- 7.12.4 Memory Fences
- 7.12.5 Lock-Free vs Wait-Free

### 7.13 Testing Concurrent Code
- 7.13.1 Race Detector Deep-Dive
- 7.13.2 Deterministic Testing
- 7.13.3 `sync.WaitGroup` in Tests
- 7.13.4 Mocking Time
- 7.13.5 Concurrent Fuzzing

### 7.14 Performance Tuning
- 7.14.1 `GOMAXPROCS`
- 7.14.2 `GOGC`
- 7.14.3 `runtime.LockOSThread`
- 7.14.4 Profiling Concurrent Code
- 7.14.5 Scheduler Tracing

### 7.15 Concurrency Anti-Patterns
- 7.15.1 Unlimited Goroutines
- 7.15.2 Mutex Copying
- 7.15.3 Channel Close Violations
- 7.15.4 Premature Optimization
- 7.15.5 Wait-for-Empty-Channel
- 7.15.6 Sleep-for-Sync

### 7.16 Time-based Concurrency
- 7.16.1 `time.Ticker`
- 7.16.2 `time.AfterFunc`
- 7.16.3 Timer Leaks
- 7.16.4 Exponential Backoff
- 7.16.5 Debounce / Throttle

### 7.17 Goroutine Pools (3rd-party)
- 7.17.1 `ants`
- 7.17.2 `tunny`
- 7.17.3 `workerpool`
- 7.17.4 When to Use Pools

### 7.18 Production Patterns
- 7.18.1 Backpressure
- 7.18.2 Dynamic Worker Scaling
- 7.18.3 Batching
- 7.18.4 Graceful Shutdown
- 7.18.5 Drain Pattern

### 7.19 Pipeline Production Patterns
- 7.19.1 Error Propagation
- 7.19.2 Cancellation Propagation
- 7.19.3 Fan-out within Pipeline
- 7.19.4 Batching Stages
- 7.19.5 Fan-in/Fan-out within Pipeline

### 7.20 Cancellation Deep
- 7.20.1 Cooperative vs Force Cancellation
- 7.20.2 Partial Cancellation
- 7.20.3 Cleanup Ordering

### 7.21 Concurrent Data Structures
- 7.21.1 TTL Caches
- 7.21.2 LRU Concurrent
- 7.21.3 Concurrent Skip List
- 7.21.4 Concurrent Trees
- 7.21.5 Copy-on-Write
- 7.21.6 Concurrent Counters
- 7.21.7 Concurrent Bloom Filter

### 7.22 Memory Ordering & Barriers
- 7.22.1 Hardware Memory Barriers
- 7.22.2 Acquire / Release Semantics
- 7.22.3 Sequential Consistency
- 7.22.4 Cache Coherence
- 7.22.5 False Sharing

### 7.23 Concurrency in stdlib
- 7.23.1 `net/http` Server Concurrency
- 7.23.2 `database/sql` Connection Pool
- 7.23.3 `sync.Pool` Internals
- 7.23.4 Runtime Internals
- 7.23.5 `time` Package Concurrency

### 7.24 Primitives Decision Guide
- 7.24.1 Channel vs Mutex
- 7.24.2 Mutex vs Atomic
- 7.24.3 When to Use `sync.Cond`
- 7.24.4 Decision Tree

### 7.25 Famous Bugs & Postmortems
- 7.25.1 Cloudflare Incidents
- 7.25.2 Uber Incidents
- 7.25.3 Dropbox Incidents
- 7.25.4 GitHub Incidents
- 7.25.5 Twitter Incidents

### 7.26 Modern Features
- 7.26.1 `sync.OnceFunc` (Go 1.21+)
- 7.26.2 Structured Concurrency
- 7.26.3 Future Proposals

### 7.27 Real-World Case Studies
- 7.27.1 Kubernetes Scheduler
- 7.27.2 etcd Raft
- 7.27.3 gRPC Stream
- 7.27.4 Docker Concurrency
- 7.27.5 Prometheus Concurrency
- 7.27.6 Postgres Driver

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
