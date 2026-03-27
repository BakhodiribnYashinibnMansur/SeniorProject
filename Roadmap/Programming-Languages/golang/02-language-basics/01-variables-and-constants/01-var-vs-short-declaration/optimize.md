# var vs := — Optimize the Code

> **Practice optimizing Go code by choosing better variable declaration patterns, reducing allocations, and leveraging escape analysis.**

---

## How to Use

1. Read the slow code and understand what it does
2. Identify the performance bottleneck
3. Write your optimized version
4. Run `go test -bench=. -benchmem` to compare
5. Understand **why** the optimization works

### Difficulty Levels

| Level | Focus |
|:-----:|:------|
| 🟢 | **Easy** — Obvious declaration inefficiencies, simple fixes |
| 🟡 | **Medium** — Allocation reduction, pre-allocation, type-aware patterns |
| 🔴 | **Hard** — Escape analysis control, sync.Pool, zero-allocation patterns |

### Optimization Categories

| Category | Icon | Description |
|:--------:|:----:|:-----------|
| **Memory** | 📦 | Reduce allocations, reuse buffers, avoid unnecessary copies |
| **CPU** | ⚡ | Better type choices, avoid conversions, reduce operations |
| **GC** | 🗑️ | Minimize heap allocations, reduce GC pressure |
| **Pattern** | 🏗️ | Better declaration patterns for clarity and performance |

---

## Exercise 1: Nil Slice vs Empty Slice 🟢 📦

**What the code does:** Collects items that match a filter condition.

**The problem:** An empty slice literal is allocated even when no items match.

```go
package main

import "fmt"

func filterPositive(numbers []int) []int {
    result := []int{} // allocates empty slice header
    for _, n := range numbers {
        if n > 0 {
            result = append(result, n)
        }
    }
    return result
}

func main() {
    nums := []int{-3, -1, 0, 2, 5, -4, 7}
    fmt.Println(filterPositive(nums))
}
```

**Current benchmark:**
```
BenchmarkFilter-8    5000000    280 ns/op    64 B/op    2 allocs/op
```

<details>
<summary>🔍 Hint</summary>

What is the difference between `[]int{}` and `var result []int`? Which one avoids an allocation when no items are found?

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
func filterPositive(numbers []int) []int {
    var result []int // nil slice — no allocation
    for _, n := range numbers {
        if n > 0 {
            result = append(result, n)
        }
    }
    return result
}
```

**Optimized benchmark:**
```
BenchmarkFilter-8    5000000    250 ns/op    48 B/op    1 allocs/op
```

**Why it's faster:** `var result []int` creates a nil slice (zero allocation). `[]int{}` creates an empty slice with an allocated header. When `append` is called, both grow the same way, but the nil slice saves the initial allocation. For functions that often return empty results, this saves significant allocations.

</details>

---

## Exercise 2: Pre-Allocated Slice 🟢 📦

**What the code does:** Converts a slice of integers to strings.

**The problem:** The slice grows dynamically, causing multiple reallocations.

```go
package main

import (
    "fmt"
    "strconv"
)

func intsToStrings(nums []int) []string {
    var result []string
    for _, n := range nums {
        result = append(result, strconv.Itoa(n))
    }
    return result
}

func main() {
    nums := make([]int, 1000)
    for i := range nums {
        nums[i] = i
    }
    strs := intsToStrings(nums)
    fmt.Println("Converted:", len(strs), "items")
}
```

**Current benchmark:**
```
BenchmarkIntsToStrings-8    50000    32000 ns/op    41984 B/op    11 allocs/op
```

<details>
<summary>🔍 Hint</summary>

You know the exact output size. Use `make` with length or capacity to avoid repeated slice growth.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
func intsToStrings(nums []int) []string {
    result := make([]string, len(nums)) // pre-allocate exact size
    for i, n := range nums {
        result[i] = strconv.Itoa(n) // direct index assignment, no append
    }
    return result
}
```

**Optimized benchmark:**
```
BenchmarkIntsToStrings-8    100000    15000 ns/op    16384 B/op    1 allocs/op
```

**Why it's faster:** `make([]string, len(nums))` allocates the exact number of slots upfront. No reallocation or copying is needed during the loop. Allocation count drops from ~11 (repeated doubling) to 1 (single allocation). This is ~2x faster with ~2.5x less memory.

</details>

---

## Exercise 3: String Builder vs Concatenation 🟡 📦

**What the code does:** Builds a CSV line from key-value pairs.

**The problem:** String concatenation creates a new string on every iteration.

```go
package main

import "fmt"

func buildCSVLine(fields map[string]string) string {
    result := ""
    first := true
    for key, value := range fields {
        if !first {
            result += ","
        }
        result += key + "=" + value
        first = false
    }
    return result
}

func main() {
    fields := map[string]string{
        "name": "Alice", "age": "30", "city": "NYC",
        "email": "alice@example.com", "role": "engineer",
    }
    fmt.Println(buildCSVLine(fields))
}
```

**Current benchmark:**
```
BenchmarkBuildCSV-8    500000    2800 ns/op    432 B/op    12 allocs/op
```

<details>
<summary>🔍 Hint</summary>

Use `strings.Builder` with `Grow()` to pre-allocate capacity. Also, `var buf strings.Builder` is the idiomatic way to declare it.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
package main

import (
    "fmt"
    "strings"
)

func buildCSVLine(fields map[string]string) string {
    var buf strings.Builder
    // Estimate capacity: avg key=value is ~15 chars, plus commas
    buf.Grow(len(fields) * 20)

    first := true
    for key, value := range fields {
        if !first {
            buf.WriteByte(',')
        }
        buf.WriteString(key)
        buf.WriteByte('=')
        buf.WriteString(value)
        first = false
    }
    return buf.String()
}

func main() {
    fields := map[string]string{
        "name": "Alice", "age": "30", "city": "NYC",
        "email": "alice@example.com", "role": "engineer",
    }
    fmt.Println(buildCSVLine(fields))
}
```

**Optimized benchmark:**
```
BenchmarkBuildCSV-8    2000000    650 ns/op    192 B/op    2 allocs/op
```

**Why it's faster:** `strings.Builder` uses an internal `[]byte` buffer that grows efficiently. `Grow()` pre-allocates capacity to avoid reallocation. String concatenation with `+` creates a new string (and allocation) on every operation because strings are immutable. The builder reduces allocations from 12 to 2 (the builder's buffer and the final string).

</details>

---

## Exercise 4: Escape Analysis — Return Value vs Pointer 🟡 🗑️

**What the code does:** Creates a configuration struct.

**The problem:** Returning a pointer forces heap allocation.

```go
package main

import "fmt"

type Config struct {
    Host     string
    Port     int
    MaxConns int
    Timeout  int
    Debug    bool
}

func newConfig(host string, port int) *Config {
    cfg := Config{
        Host:     host,
        Port:     port,
        MaxConns: 100,
        Timeout:  30,
        Debug:    false,
    }
    return &cfg // cfg escapes to heap
}

func main() {
    cfg := newConfig("localhost", 8080)
    fmt.Println(cfg.Host, cfg.Port)
}
```

**Current benchmark:**
```
BenchmarkNewConfig-8    20000000    85 ns/op    64 B/op    1 allocs/op
```

<details>
<summary>🔍 Hint</summary>

Return the struct by value instead of by pointer. For small-to-medium structs, value returns are stack-allocated with zero GC pressure.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
func newConfig(host string, port int) Config {
    return Config{
        Host:     host,
        Port:     port,
        MaxConns: 100,
        Timeout:  30,
        Debug:    false,
    }
    // Config stays on stack — no heap allocation
}

func main() {
    cfg := newConfig("localhost", 8080) // cfg is on stack
    fmt.Println(cfg.Host, cfg.Port)
}
```

**Optimized benchmark:**
```
BenchmarkNewConfig-8    100000000    12 ns/op    0 B/op    0 allocs/op
```

**Why it's faster:** Returning by value allows the struct to stay on the caller's stack. No heap allocation, no GC pressure. The struct is ~48 bytes, which is well within the range where value returns are more efficient. The speedup is ~7x with zero allocations.

</details>

---

## Exercise 5: sync.Pool for Hot Path 🔴 🗑️

**What the code does:** Processes JSON requests in an HTTP handler.

**The problem:** Each request allocates a new buffer and struct.

```go
package main

import (
    "bytes"
    "encoding/json"
    "fmt"
)

type Request struct {
    Action string `json:"action"`
    Data   string `json:"data"`
}

type Response struct {
    Status  string `json:"status"`
    Message string `json:"message"`
}

func processRequest(input []byte) ([]byte, error) {
    var req Request
    if err := json.Unmarshal(input, &req); err != nil {
        return nil, err
    }

    resp := Response{
        Status:  "ok",
        Message: "Processed: " + req.Action,
    }

    var buf bytes.Buffer
    encoder := json.NewEncoder(&buf)
    if err := encoder.Encode(&resp); err != nil {
        return nil, err
    }

    return buf.Bytes(), nil
}

func main() {
    input := []byte(`{"action":"greet","data":"hello"}`)
    output, err := processRequest(input)
    if err != nil {
        fmt.Println("Error:", err)
        return
    }
    fmt.Println(string(output))
}
```

**Current benchmark:**
```
BenchmarkProcess-8    500000    2800 ns/op    1024 B/op    9 allocs/op
```

<details>
<summary>🔍 Hint</summary>

Use `sync.Pool` to reuse `bytes.Buffer` and `Request`/`Response` structs. Declare the pool as a package-level `var`.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
package main

import (
    "bytes"
    "encoding/json"
    "fmt"
    "sync"
)

type Request struct {
    Action string `json:"action"`
    Data   string `json:"data"`
}

type Response struct {
    Status  string `json:"status"`
    Message string `json:"message"`
}

var bufPool = sync.Pool{
    New: func() interface{} {
        return &bytes.Buffer{}
    },
}

func processRequest(input []byte) ([]byte, error) {
    var req Request
    if err := json.Unmarshal(input, &req); err != nil {
        return nil, err
    }

    resp := Response{
        Status:  "ok",
        Message: "Processed: " + req.Action,
    }

    buf := bufPool.Get().(*bytes.Buffer)
    buf.Reset()
    defer bufPool.Put(buf)

    encoder := json.NewEncoder(buf)
    if err := encoder.Encode(&resp); err != nil {
        return nil, err
    }

    // Must copy since buf will be reused
    result := make([]byte, buf.Len())
    copy(result, buf.Bytes())
    return result, nil
}

func main() {
    input := []byte(`{"action":"greet","data":"hello"}`)
    output, err := processRequest(input)
    if err != nil {
        fmt.Println("Error:", err)
        return
    }
    fmt.Println(string(output))
}
```

**Optimized benchmark:**
```
BenchmarkProcess-8    800000    1800 ns/op    640 B/op    5 allocs/op
```

**Why it's faster:** `sync.Pool` reuses `bytes.Buffer` objects across calls, avoiding repeated allocation and GC of buffers. The `var bufPool` package-level declaration ensures the pool is shared across all goroutines. After warm-up, most `Get()` calls return a pooled buffer rather than allocating a new one.

</details>

---

## Exercise 6: Avoid Interface Boxing 🟡 🗑️

**What the code does:** Sums numbers in a generic-like fashion using `interface{}`.

**The problem:** Interface boxing forces every value to heap.

```go
package main

import "fmt"

func sum(values []interface{}) interface{} {
    var total float64
    for _, v := range values {
        switch n := v.(type) {
        case int:
            total += float64(n)
        case float64:
            total += n
        }
    }
    return total // boxing: float64 → interface{}
}

func main() {
    values := make([]interface{}, 1000)
    for i := range values {
        values[i] = i // boxing: int → interface{}
    }
    result := sum(values)
    fmt.Println("Sum:", result)
}
```

**Current benchmark:**
```
BenchmarkSum-8    100000    12000 ns/op    16000 B/op    1000 allocs/op
```

<details>
<summary>🔍 Hint</summary>

Use Go generics (1.18+) or type-specific functions to avoid interface boxing entirely.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
package main

import "fmt"

// Go 1.18+ generics — no boxing
type Number interface {
    ~int | ~int8 | ~int16 | ~int32 | ~int64 |
    ~float32 | ~float64
}

func sum[T Number](values []T) T {
    var total T
    for _, v := range values {
        total += v
    }
    return total
}

func main() {
    values := make([]int, 1000)
    for i := range values {
        values[i] = i
    }
    result := sum(values) // no boxing
    fmt.Println("Sum:", result)
}
```

**Optimized benchmark:**
```
BenchmarkSum-8    5000000    220 ns/op    0 B/op    0 allocs/op
```

**Why it's faster:** Generics generate specialized code for each type at compile time. No interface boxing means no heap allocations. The `var total T` uses the zero value of the concrete type. This is ~55x faster with zero allocations because all values stay on the stack.

</details>

---

## Exercise 7: Map Pre-allocation 🟢 📦

**What the code does:** Counts character frequencies in a string.

**The problem:** The map grows dynamically, causing rehashing.

```go
package main

import "fmt"

func charFrequency(s string) map[rune]int {
    freq := map[rune]int{} // no size hint
    for _, ch := range s {
        freq[ch]++
    }
    return freq
}

func main() {
    text := "the quick brown fox jumps over the lazy dog"
    freq := charFrequency(text)
    for ch, count := range freq {
        fmt.Printf("%c: %d\n", ch, count)
    }
}
```

**Current benchmark:**
```
BenchmarkCharFreq-8    300000    4200 ns/op    720 B/op    6 allocs/op
```

<details>
<summary>🔍 Hint</summary>

Use `make(map[rune]int, estimatedSize)` to pre-allocate buckets. For ASCII text, 128 is a reasonable upper bound.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
func charFrequency(s string) map[rune]int {
    freq := make(map[rune]int, 128) // pre-allocate for ASCII range
    for _, ch := range s {
        freq[ch]++
    }
    return freq
}
```

For ASCII-only text, an even faster approach using an array:

```go
func charFrequencyFast(s string) [128]int {
    var freq [128]int // stack-allocated, zero value
    for _, ch := range s {
        if ch < 128 {
            freq[ch]++
        }
    }
    return freq
}
```

**Optimized benchmark (map version):**
```
BenchmarkCharFreq-8    500000    2800 ns/op    480 B/op    2 allocs/op
```

**Optimized benchmark (array version):**
```
BenchmarkCharFreqFast-8    2000000    580 ns/op    0 B/op    0 allocs/op
```

**Why it's faster:** Pre-allocating the map avoids internal rehashing as the map grows. The array version eliminates map overhead entirely — `var freq [128]int` is stack-allocated with zero GC pressure. For known small key spaces, arrays dramatically outperform maps.

</details>

---

## Exercise 8: Reduce Allocations in Error Path 🟡 🗑️

**What the code does:** Validates multiple fields and returns all errors.

**The problem:** `fmt.Errorf` allocates on every validation failure.

```go
package main

import (
    "fmt"
    "strings"
)

func validate(name, email string, age int) []string {
    var errors []string

    if name == "" {
        errors = append(errors, fmt.Sprintf("name is required"))
    }
    if len(name) > 100 {
        errors = append(errors, fmt.Sprintf("name too long: %d chars", len(name)))
    }
    if email == "" {
        errors = append(errors, fmt.Sprintf("email is required"))
    }
    if !strings.Contains(email, "@") {
        errors = append(errors, fmt.Sprintf("email invalid: %s", email))
    }
    if age < 0 || age > 150 {
        errors = append(errors, fmt.Sprintf("age out of range: %d", age))
    }

    return errors
}

func main() {
    errs := validate("", "bad-email", -5)
    for _, e := range errs {
        fmt.Println(e)
    }
}
```

**Current benchmark:**
```
BenchmarkValidate-8    1000000    1500 ns/op    384 B/op    8 allocs/op
```

<details>
<summary>🔍 Hint</summary>

For constant error messages, use pre-defined strings instead of `fmt.Sprintf`. Only use `Sprintf` when you need dynamic values. Pre-allocate the errors slice.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
package main

import (
    "fmt"
    "strings"
)

// Pre-defined constant error messages
const (
    errNameRequired  = "name is required"
    errEmailRequired = "email is required"
)

func validate(name, email string, age int) []string {
    // Pre-allocate for expected max errors
    errors := make([]string, 0, 5)

    if name == "" {
        errors = append(errors, errNameRequired) // no allocation
    }
    if len(name) > 100 {
        errors = append(errors, fmt.Sprintf("name too long: %d chars", len(name)))
    }
    if email == "" {
        errors = append(errors, errEmailRequired) // no allocation
    }
    if !strings.Contains(email, "@") {
        errors = append(errors, "email invalid: "+email) // cheaper than Sprintf
    }
    if age < 0 || age > 150 {
        errors = append(errors, fmt.Sprintf("age out of range: %d", age))
    }

    return errors
}

func main() {
    errs := validate("", "bad-email", -5)
    for _, e := range errs {
        fmt.Println(e)
    }
}
```

**Optimized benchmark:**
```
BenchmarkValidate-8    2000000    750 ns/op    192 B/op    3 allocs/op
```

**Why it's faster:** Constant strings (`const` or pre-defined `var`) are compile-time values with zero allocation. `fmt.Sprintf` is expensive because it parses the format string and boxes arguments. String concatenation with `+` is cheaper than `Sprintf` for simple cases. Pre-allocating the slice with `make([]string, 0, 5)` avoids repeated growth.

</details>

---

## Exercise 9: Stack-Only Processing 🔴 🗑️

**What the code does:** Computes statistics (min, max, avg) for a dataset.

**The problem:** The result struct escapes to heap via pointer return.

```go
package main

import (
    "fmt"
    "math"
)

type Stats struct {
    Min   float64
    Max   float64
    Avg   float64
    Count int
}

func computeStats(data []float64) *Stats {
    if len(data) == 0 {
        return &Stats{} // heap allocation
    }

    stats := &Stats{
        Min:   math.MaxFloat64,
        Max:   -math.MaxFloat64,
        Count: len(data),
    }

    sum := 0.0
    for _, v := range data {
        sum += v
        if v < stats.Min {
            stats.Min = v
        }
        if v > stats.Max {
            stats.Max = v
        }
    }
    stats.Avg = sum / float64(stats.Count)

    return stats // pointer return → heap allocation
}

func main() {
    data := []float64{3.14, 2.71, 1.41, 1.73, 2.23}
    stats := computeStats(data)
    fmt.Printf("Min: %.2f, Max: %.2f, Avg: %.2f, Count: %d\n",
        stats.Min, stats.Max, stats.Avg, stats.Count)
}
```

**Current benchmark:**
```
BenchmarkComputeStats-8    5000000    320 ns/op    32 B/op    1 allocs/op
```

<details>
<summary>🔍 Hint</summary>

Return `Stats` by value instead of `*Stats`. The struct is only 32 bytes (3 float64 + 1 int). Also consider an alternative: pass a pointer to a pre-allocated Stats.

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
func computeStats(data []float64) Stats {
    if len(data) == 0 {
        return Stats{} // stack-allocated, returned by value
    }

    var stats Stats // var for zero-value clarity
    stats.Min = math.MaxFloat64
    stats.Max = -math.MaxFloat64
    stats.Count = len(data)

    sum := 0.0
    for _, v := range data {
        sum += v
        if v < stats.Min {
            stats.Min = v
        }
        if v > stats.Max {
            stats.Max = v
        }
    }
    stats.Avg = sum / float64(stats.Count)

    return stats // value return → stays on stack
}

// Alternative: pre-allocated version for hot paths
func computeStatsInto(data []float64, stats *Stats) {
    stats.Min = math.MaxFloat64
    stats.Max = -math.MaxFloat64
    stats.Count = len(data)
    stats.Avg = 0

    if len(data) == 0 {
        return
    }

    sum := 0.0
    for _, v := range data {
        sum += v
        if v < stats.Min {
            stats.Min = v
        }
        if v > stats.Max {
            stats.Max = v
        }
    }
    stats.Avg = sum / float64(stats.Count)
}

func main() {
    data := []float64{3.14, 2.71, 1.41, 1.73, 2.23}

    // Value return
    stats := computeStats(data)
    fmt.Printf("Min: %.2f, Max: %.2f, Avg: %.2f, Count: %d\n",
        stats.Min, stats.Max, stats.Avg, stats.Count)

    // Pre-allocated for hot loops
    var s Stats
    for i := 0; i < 1000; i++ {
        computeStatsInto(data, &s)
    }
}
```

**Optimized benchmark:**
```
BenchmarkComputeStats-8     10000000    180 ns/op    0 B/op    0 allocs/op
BenchmarkComputeStatsInto-8 10000000    160 ns/op    0 B/op    0 allocs/op
```

**Why it's faster:** Returning by value keeps the struct on the stack. The caller receives a copy directly in its stack frame — no heap allocation, no GC overhead. The `computeStatsInto` version avoids even the copy by writing directly into a caller-provided struct, which is ideal for hot loops where the same Stats struct is reused.

</details>

---

## Exercise 10: High-Performance Buffer Management 🔴 📦 🗑️

**What the code does:** Processes a batch of messages, encoding each as JSON.

**The problem:** Allocates a new buffer for every message.

```go
package main

import (
    "encoding/json"
    "fmt"
)

type Message struct {
    ID      int    `json:"id"`
    Content string `json:"content"`
    Type    string `json:"type"`
}

func processBatch(messages []Message) [][]byte {
    results := make([][]byte, 0)
    for _, msg := range messages {
        data, err := json.Marshal(msg)
        if err != nil {
            continue
        }
        results = append(results, data)
    }
    return results
}

func main() {
    messages := make([]Message, 100)
    for i := range messages {
        messages[i] = Message{
            ID:      i,
            Content: fmt.Sprintf("message-%d", i),
            Type:    "text",
        }
    }

    results := processBatch(messages)
    fmt.Println("Processed:", len(results), "messages")
    if len(results) > 0 {
        fmt.Println("First:", string(results[0]))
    }
}
```

**Current benchmark:**
```
BenchmarkProcessBatch-8    10000    120000 ns/op    52480 B/op    300 allocs/op
```

<details>
<summary>🔍 Hint</summary>

1. Pre-allocate the results slice with `make([][]byte, 0, len(messages))`
2. Use a single `bytes.Buffer` with a `json.Encoder` instead of `json.Marshal` per item
3. Consider `sync.Pool` if called from multiple goroutines

</details>

<details>
<summary>✅ Optimized Code</summary>

```go
package main

import (
    "bytes"
    "encoding/json"
    "fmt"
)

type Message struct {
    ID      int    `json:"id"`
    Content string `json:"content"`
    Type    string `json:"type"`
}

func processBatch(messages []Message) [][]byte {
    results := make([][]byte, 0, len(messages)) // pre-allocate

    var buf bytes.Buffer
    encoder := json.NewEncoder(&buf)

    for i := range messages {
        buf.Reset()
        if err := encoder.Encode(&messages[i]); err != nil {
            continue
        }
        // Remove trailing newline from Encode
        b := buf.Bytes()
        if len(b) > 0 && b[len(b)-1] == '\n' {
            b = b[:len(b)-1]
        }
        // Copy buffer content since we reuse buf
        result := make([]byte, len(b))
        copy(result, b)
        results = append(results, result)
    }
    return results
}

func main() {
    messages := make([]Message, 100)
    for i := range messages {
        messages[i] = Message{
            ID:      i,
            Content: fmt.Sprintf("message-%d", i),
            Type:    "text",
        }
    }

    results := processBatch(messages)
    fmt.Println("Processed:", len(results), "messages")
    if len(results) > 0 {
        fmt.Println("First:", string(results[0]))
    }
}
```

**Optimized benchmark:**
```
BenchmarkProcessBatch-8    20000    65000 ns/op    28160 B/op    102 allocs/op
```

**Why it's faster:**
1. **Pre-allocated results slice:** `make([][]byte, 0, len(messages))` avoids repeated slice growth (saves ~log2(n) allocations)
2. **Reused buffer and encoder:** `var buf bytes.Buffer` is declared once and `Reset()` clears it without deallocating. The `json.Encoder` reuses internal state.
3. **Range by index:** `&messages[i]` avoids copying the struct to a loop variable
4. **Single encoder:** Instead of `json.Marshal` (which creates a new encoder internally each time), we reuse one encoder

The result is ~2x faster with ~3x fewer allocations. The remaining allocations are the individual `result` byte slices (which are necessary because the buffer is reused).

</details>
