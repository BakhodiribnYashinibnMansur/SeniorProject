# var vs := — Practical Tasks

> Hands-on exercises to practice Go's variable declaration mechanisms. Each task includes starter code and expected output.

---

## 1. Junior Tasks

### Task 1: Variable Declaration Practice

**Goal:** Declare variables using all possible forms of `var` and `:=`.

**Requirements:**
1. Declare a `string` variable using full `var` syntax
2. Declare an `int` variable using `var` with zero value
3. Declare a `float64` variable using `var` with type inference
4. Declare a `bool` variable using `:=`
5. Print all variables with their types using `%T`

```go
package main

import "fmt"

func main() {
    // TODO: Declare variables using different forms

    // 1. Full var declaration
    // var name string = ???

    // 2. Zero value var
    // var count ???

    // 3. var with type inference
    // var price = ???

    // 4. Short declaration
    // active := ???

    // Print all variables with types
    // fmt.Printf("name: %v (%T)\n", name, name)
    // fmt.Printf("count: %v (%T)\n", count, count)
    // fmt.Printf("price: %v (%T)\n", price, price)
    // fmt.Printf("active: %v (%T)\n", active, active)
}
```

**Expected output:**
```
name: Alice (string)
count: 0 (int)
price: 19.99 (float64)
active: true (bool)
```

---

### Task 2: Swap Variables

**Goal:** Swap two variables using Go's multiple assignment.

```go
package main

import "fmt"

func main() {
    a := 10
    b := 20

    fmt.Println("Before swap:", a, b)

    // TODO: Swap a and b using a single line (no temp variable)

    fmt.Println("After swap:", a, b)
}
```

**Expected output:**
```
Before swap: 10 20
After swap: 20 10
```

---

### Task 3: Grouped Variable Declaration

**Goal:** Create a `var` block for a student profile and print it.

```go
package main

import "fmt"

// TODO: Create a var block with these student variables:
// - name (string): "John Doe"
// - age (int): 20
// - gpa (float64): 3.75
// - enrolled (bool): true
// - university (string): "MIT"

func main() {
    // TODO: Print each variable in a formatted way
}
```

---

### Task 4: Type Inference Detective

**Goal:** Predict and verify the inferred types for different literals.

```go
package main

import "fmt"

func main() {
    a := 42
    b := 3.14
    c := "hello"
    d := true
    e := 'A'
    f := 2 + 3i

    // TODO: For each variable, print with %T to verify the type

    _ = a; _ = b; _ = c; _ = d; _ = e; _ = f
}
```

**Expected output:**
```
a = 42, type: int
b = 3.14, type: float64
c = hello, type: string
d = true, type: bool
e = 65, type: int32
f = (2+3i), type: complex128
```

---

### Task 5: Error Handling with `:=`

**Goal:** Practice the `result, err :=` pattern with multiple steps.

```go
package main

import (
    "fmt"
    "strconv"
)

func main() {
    inputs := []string{"42", "hello", "100"}

    for _, input := range inputs {
        // TODO: Use strconv.Atoi to convert input to int
        // Handle the error properly
        // Print "Converted: <value>" on success
        // Print "Error converting '<input>': <error>" on failure

        _ = input // remove this line
    }
}
```

**Expected output:**
```
Converted: 42
Error converting 'hello': strconv.Atoi: parsing "hello": invalid syntax
Converted: 100
```

---

## 2. Middle Tasks

### Task 1: Shadowing Detector

**Goal:** Identify and fix all variable shadowing issues in the code.

```go
package main

import (
    "fmt"
    "strconv"
)

func processInput(input string) (int, error) {
    result := 0
    err := error(nil)

    if len(input) > 0 {
        result, err := strconv.Atoi(input)
        if err != nil {
            fmt.Println("Parse error:", err)
            return 0, err
        }
        fmt.Println("Parsed:", result)
    }

    // BUG: result and err here are still the original values
    return result, err
}

func main() {
    val, err := processInput("42")
    fmt.Printf("Value: %d, Error: %v\n", val, err)
    // Expected: Value: 42, Error: <nil>
    // Actual: Value: 0, Error: <nil> — BUG!
}
```

**Task:** Fix the function so it correctly returns the parsed value.

---

### Task 2: Multi-Return Chain

**Goal:** Write a function that processes data through 3 steps, properly chaining `:=` with error handling.

```go
package main

import (
    "encoding/json"
    "fmt"
    "strings"
)

type User struct {
    Name  string `json:"name"`
    Email string `json:"email"`
    Age   int    `json:"age"`
}

// TODO: Implement this function that:
// 1. Trims whitespace from input
// 2. Parses JSON into a User struct
// 3. Validates that Name is not empty, Email contains "@", Age > 0
// Use proper := and = for error handling chain
func parseAndValidateUser(input string) (User, error) {
    return User{}, nil
}

func main() {
    inputs := []string{
        `  {"name": "Alice", "email": "alice@example.com", "age": 30}  `,
        `{"name": "", "email": "bad", "age": 0}`,
        `invalid json`,
    }

    for _, input := range inputs {
        user, err := parseAndValidateUser(input)
        if err != nil {
            fmt.Println("Error:", err)
        } else {
            fmt.Printf("Valid user: %+v\n", user)
        }
    }
}
```

---

### Task 3: Configuration Builder

**Goal:** Build a configuration system using `var` blocks and functional options.

```go
package main

import (
    "fmt"
    "time"
)

type ServerConfig struct {
    Host         string
    Port         int
    ReadTimeout  time.Duration
    WriteTimeout time.Duration
    MaxConns     int
    TLS          bool
}

// TODO: Define default values using a var block
// TODO: Implement Option type and option functions
// TODO: Implement NewServerConfig

func main() {
    cfg1 := NewServerConfig()
    fmt.Printf("Default: %+v\n", cfg1)

    cfg2 := NewServerConfig(
        WithHost("api.example.com"),
        WithPort(443),
        WithTLS(true),
        WithMaxConns(1000),
    )
    fmt.Printf("Custom: %+v\n", cfg2)
}
```

---

### Task 4: Type-Safe Environment Parser

**Goal:** Write functions that read environment variables and convert to proper types.

```go
package main

import (
    "fmt"
    "os"
    "strconv"
)

// TODO: Implement these functions
func getEnvString(key, defaultVal string) string { return "" }
func getEnvInt(key string, defaultVal int) int { return 0 }
func getEnvBool(key string, defaultVal bool) bool { return false }

func main() {
    os.Setenv("APP_NAME", "MyApp")
    os.Setenv("APP_PORT", "8080")
    os.Setenv("APP_DEBUG", "true")

    name := getEnvString("APP_NAME", "DefaultApp")
    port := getEnvInt("APP_PORT", 3000)
    debug := getEnvBool("APP_DEBUG", false)
    missing := getEnvString("MISSING_VAR", "fallback")

    fmt.Printf("Name: %s\nPort: %d\nDebug: %t\nMissing: %s\n",
        name, port, debug, missing)
}
```

**Expected output:**
```
Name: MyApp
Port: 8080
Debug: true
Missing: fallback
```

---

### Task 5: Concurrent Counter

**Goal:** Fix a race condition on a package-level variable.

```go
package main

import (
    "fmt"
    "sync"
)

// BUG: This is not thread-safe
var counter int

func increment(wg *sync.WaitGroup) {
    defer wg.Done()
    for i := 0; i < 1000; i++ {
        counter++ // RACE CONDITION
    }
}

func main() {
    var wg sync.WaitGroup
    for i := 0; i < 10; i++ {
        wg.Add(1)
        go increment(&wg)
    }
    wg.Wait()
    fmt.Println("Counter:", counter)
    // Expected: 10000
}

// TODO: Fix using three approaches:
// 1. sync.Mutex
// 2. sync/atomic
// 3. Channel-based
```

---

## 3. Senior Tasks

### Task 1: Zero-Allocation String Processing

**Goal:** Process strings without heap allocations using proper declaration patterns.

```go
package main

import (
    "fmt"
    "strings"
    "testing"
)

// SLOW: Multiple allocations per call
func processStringSlow(items []string) string {
    result := ""
    for i, item := range items {
        if i > 0 {
            result += ","
        }
        result += strings.ToUpper(item)
    }
    return result
}

// TODO: Implement fast version using:
// - var for pre-allocated builder
// - Grow() to avoid reallocation
// - Minimize allocations to 1 (the final string)
func processStringFast(items []string) string {
    return ""
}

func main() {
    items := []string{"hello", "world", "foo", "bar"}
    fmt.Println(processStringSlow(items))
    fmt.Println(processStringFast(items))
}
```

---

### Task 2: Object Pool Implementation

**Goal:** Implement a type-safe object pool that reduces GC pressure.

```go
package main

import (
    "bytes"
    "fmt"
    "sync"
    "sync/atomic"
)

// TODO: Implement buffer pool with stats tracking
// var bufPool = sync.Pool{...}
// var poolHits, poolMisses atomic.Int64

func getBuffer() *bytes.Buffer { return &bytes.Buffer{} }
func putBuffer(buf *bytes.Buffer) {}
func poolStats() (hits, misses int64) { return 0, 0 }

func main() {
    for i := 0; i < 1000; i++ {
        buf := getBuffer()
        fmt.Fprintf(buf, "request-%d", i)
        _ = buf.String()
        putBuffer(buf)
    }
    hits, misses := poolStats()
    fmt.Printf("Pool stats: hits=%d, misses=%d, ratio=%.2f%%\n",
        hits, misses, float64(hits)/float64(hits+misses)*100)
}
```

---

### Task 3: Escape Analysis Optimization

**Goal:** Rewrite functions to prevent unnecessary heap allocations.

```go
package main

import "fmt"

type Point struct{ X, Y float64 }
type Line struct{ Start, End Point }

// BAD: All Points escape to heap
func createLine(x1, y1, x2, y2 float64) *Line {
    start := &Point{X: x1, Y: y1}
    end := &Point{X: x2, Y: y2}
    line := &Line{Start: *start, End: *end}
    return line
}

// TODO: Rewrite to minimize heap allocations
// 1. Return by value
// 2. Accept pre-allocated pointer
// 3. Write benchmarks comparing all versions
// Run with: go test -bench=. -benchmem -gcflags="-m"

func main() {
    line := createLine(0, 0, 10, 10)
    fmt.Println(line)
}
```

---

### Task 4: Compile-Time Interface Registry

**Goal:** Build a plugin registry using compile-time interface checks.

```go
package main

import "fmt"

type Plugin interface {
    Name() string
    Execute(input string) (string, error)
}

// TODO: Implement 3 plugins (Upper, Reverse, Count)
// TODO: Add compile-time checks: var _ Plugin = (*UpperPlugin)(nil)
// TODO: Implement registry using var block
// TODO: Implement GetPlugin and ListPlugins functions

func main() {
    fmt.Println("Available plugins:", ListPlugins())
    for _, name := range ListPlugins() {
        plugin, _ := GetPlugin(name)
        result, _ := plugin.Execute("Hello, World!")
        fmt.Printf("%s: %s\n", name, result)
    }
}
```

---

### Task 5: Benchmark-Driven Optimization

**Goal:** Profile and optimize a data processing function.

```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
)

type Record struct {
    ID    int
    Name  string
    Score float64
    Tags  []string
}

// SLOW: Many allocations
func filterAndSortSlow(records []Record, minScore float64) []Record {
    var result []Record
    for _, r := range records {
        if r.Score >= minScore {
            result = append(result, r)
        }
    }
    sort.Slice(result, func(i, j int) bool {
        return result[i].Score > result[j].Score
    })
    return result
}

// TODO: Implement optimized version using:
// 1. Pre-allocated slice with make([]Record, 0, estimatedCap)
// 2. Minimal allocations
func filterAndSortFast(records []Record, minScore float64) []Record {
    return nil
}

func main() {
    records := make([]Record, 10000)
    for i := range records {
        records[i] = Record{
            ID: i, Name: fmt.Sprintf("r-%d", i),
            Score: rand.Float64() * 100,
        }
    }
    result := filterAndSortSlow(records, 50.0)
    fmt.Printf("Filtered: %d records\n", len(result))
}
```

---

## 4. Questions

1. Why does Go require all declared variables to be used? What bugs does this prevent?
2. When should you use `var x Type` instead of `x := zeroValue`?
3. How does the `:=` redeclaration rule help with error handling patterns?
4. What is the relationship between `var` declarations and Go's zero-value philosophy?
5. How would you explain shadowing vs redeclaration to a teammate?
6. Why is `:=` not allowed at the package level?
7. How does variable declaration affect escape analysis and garbage collection?
8. When would you choose `var x interface{} = value` over `x := value`?

---

## 5. Mini Projects

### Mini Project 1: Key-Value Store

Build a thread-safe in-memory key-value store:

**Requirements:**
- Package-level `var` for store (with mutex)
- `:=` for function-local variables
- `var (...)` block for configuration
- Thread-safe read/write operations

```go
// Implement: Set, Get, Delete, Keys, Size
```

### Mini Project 2: Simple Expression Evaluator

Build a calculator that evaluates math expressions:

**Requirements:**
- `var` for parser state
- `:=` for local computation
- Error handling chain with `:=`
- Support +, -, *, / operators

### Mini Project 3: HTTP Request Logger

Build a request logger middleware:

**Requirements:**
- `var` block for shared metrics (atomic)
- `:=` for request-scoped variables
- `sync.Pool` for reusable log buffers
- Proper declaration patterns in middleware chain

---

## 6. Challenge

### The Ultimate Declaration Challenge

Create a mini-application demonstrating every aspect of `var` vs `:=`:

**Checklist (18 points possible):**

- [ ] Package-level `var` block with configuration (1 pt)
- [ ] Error sentinels with `var ErrX = errors.New(...)` (1 pt)
- [ ] Compile-time interface check `var _ I = (*T)(nil)` (1 pt)
- [ ] Zero-value struct design (1 pt)
- [ ] Short declaration `:=` for function locals (1 pt)
- [ ] `:=` redeclaration in error chain (1 pt)
- [ ] `if` init statement with `:=` (1 pt)
- [ ] `switch` init statement with `:=` (1 pt)
- [ ] `for` loop with `:=` (1 pt)
- [ ] `range` loop with `:=` (1 pt)
- [ ] Type assertion with `:=` (1 pt)
- [ ] `sync.Pool` with `var` (1 pt)
- [ ] `sync.Once` with `var` (1 pt)
- [ ] `atomic` counter with `var` (1 pt)
- [ ] No variable shadowing bugs (2 pts)
- [ ] Clean code style (2 pts)

**Pass threshold: 14 points**
