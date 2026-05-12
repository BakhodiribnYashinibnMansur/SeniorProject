# Why Use Go — Optimize the Code

> **Practice optimizing slow, inefficient, or resource-heavy Go code. These exercises demonstrate WHY Go is fast and HOW to write performant Go code.**

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
| 🟢 | **Easy** — Obvious inefficiencies, simple Go fixes |
| 🟡 | **Medium** — Algorithmic improvements, allocation reduction |
| 🔴 | **Hard** — Cache-aware code, zero-allocation Go patterns, runtime-level optimizations |

### Optimization Categories

| Category | Icon | Description |
|:--------:|:----:|:-----------|
| **Memory** | 📦 | Reduce Go allocations, reuse buffers, avoid copies |
| **CPU** | ⚡ | Better algorithms, fewer operations, cache efficiency |
| **Concurrency** | 🔄 | Better Go parallelism, reduce contention, avoid locks |
| **I/O** | 💾 | Batch operations, buffering, connection reuse |

---

## Exercise 1: String Concatenation 🟢 📦

**What the code does:** Builds a large string from many small strings.
**The problem:** Each `+` concatenation creates a new string allocation because strings are immutable in Go.

```go
package main

import "fmt"

func buildString(n int) string {
    result := ""
    for i := 0; i < n; i++ {
        result += fmt.Sprintf("item-%d,", i)
    }
    return result
}

func main() {
    s := buildString(10000)
    fmt.Println("Length:", len(s))
}
```

**Current benchmark:**
```
BenchmarkBuildString-8    100    12345678 ns/op    503316480 B/op    29999 allocs/op
```

<details>
<summary>Hint</summary>
Think about `strings.Builder` — what does it do differently than `+` concatenation? Also consider using `fmt.Fprintf` to write directly into the builder.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import (
    "fmt"
    "strings"
)

func buildStringFast(n int) string {
    var b strings.Builder
    b.Grow(n * 15) // Pre-allocate estimated capacity
    for i := 0; i < n; i++ {
        fmt.Fprintf(&b, "item-%d,", i)
    }
    return b.String()
}

func main() {
    s := buildStringFast(10000)
    fmt.Println("Length:", len(s))
}
```

**What changed:**
- Used `strings.Builder` instead of `+` concatenation — Builder writes to a byte buffer and only creates one final string
- Pre-allocated with `b.Grow()` — avoids re-allocations as the buffer grows
- Used `fmt.Fprintf(&b, ...)` to write directly into the builder — avoids the intermediate `Sprintf` allocation

**Optimized benchmark:**
```
BenchmarkBuildStringFast-8    5000    234567 ns/op    245760 B/op    10001 allocs/op
```

**Improvement:** ~50x faster, ~2000x less memory

</details>

<details>
<summary>Learn More</summary>

**Why this works:** Go strings are immutable. Every `+` concatenation creates a new string and copies all previous data. This makes concatenation O(n^2). `strings.Builder` uses an internal `[]byte` buffer that grows efficiently (doubling strategy), making it O(n).

**When to apply:** Always use `strings.Builder` when concatenating strings in a loop.
**When NOT to apply:** For 2-3 concatenations, `+` is clearer and the performance difference is negligible.

</details>

---

## Exercise 2: Slice Allocation 🟢 ⚡

**What the code does:** Filters even numbers from a large slice.
**The problem:** The result slice starts empty and grows repeatedly via `append`.

```go
package main

import "fmt"

func filterEvens(nums []int) []int {
    var result []int // No pre-allocation
    for _, n := range nums {
        if n%2 == 0 {
            result = append(result, n)
        }
    }
    return result
}

func main() {
    nums := make([]int, 1000000)
    for i := range nums {
        nums[i] = i
    }
    evens := filterEvens(nums)
    fmt.Println("Even count:", len(evens))
}
```

**Current benchmark:**
```
BenchmarkFilterEvens-8    200    5678901 ns/op    9437184 B/op    25 allocs/op
```

<details>
<summary>Hint</summary>
You know roughly half the numbers will be even. Pre-allocate with `make([]int, 0, len(nums)/2)`.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import "fmt"

func filterEvensFast(nums []int) []int {
    // Pre-allocate: at most len(nums)/2 even numbers
    result := make([]int, 0, len(nums)/2)
    for _, n := range nums {
        if n%2 == 0 {
            result = append(result, n)
        }
    }
    return result
}

func main() {
    nums := make([]int, 1000000)
    for i := range nums {
        nums[i] = i
    }
    evens := filterEvensFast(nums)
    fmt.Println("Even count:", len(evens))
}
```

**Optimized benchmark:**
```
BenchmarkFilterEvensFast-8    500    2345678 ns/op    4194304 B/op    1 allocs/op
```

**Improvement:** ~2.5x faster, 1 allocation instead of 25

</details>

---

## Exercise 3: Map Initialization 🟢 📦

**What the code does:** Counts character frequencies in a string.
**The problem:** Map is not pre-sized, causing many internal re-allocations.

```go
package main

import "fmt"

func charFrequency(s string) map[rune]int {
    freq := map[rune]int{} // No size hint
    for _, c := range s {
        freq[c]++
    }
    return freq
}

func main() {
    text := "Go is a statically typed compiled language designed at Google"
    for i := 0; i < 100; i++ {
        text += text
        if len(text) > 100000 {
            break
        }
    }
    freq := charFrequency(text)
    fmt.Println("Unique chars:", len(freq))
}
```

**Current benchmark:**
```
BenchmarkCharFrequency-8    1000    1234567 ns/op    12345 B/op    45 allocs/op
```

<details>
<summary>Hint</summary>
ASCII has ~128 characters. Use `make(map[rune]int, 128)` to avoid map re-hashing.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import "fmt"

func charFrequencyFast(s string) map[rune]int {
    freq := make(map[rune]int, 128) // Pre-size for ASCII character set
    for _, c := range s {
        freq[c]++
    }
    return freq
}

func main() {
    text := "Go is a statically typed compiled language designed at Google"
    for i := 0; i < 100; i++ {
        text += text
        if len(text) > 100000 {
            break
        }
    }
    freq := charFrequencyFast(text)
    fmt.Println("Unique chars:", len(freq))
}
```

**Optimized benchmark:**
```
BenchmarkCharFrequencyFast-8    2000    876543 ns/op    5678 B/op    4 allocs/op
```

**Improvement:** ~1.5x faster, ~10x fewer allocations

</details>

---

## Exercise 4: Interface Boxing in Hot Path 🟡 📦

**What the code does:** Sums numbers using a function that accepts `interface{}`.
**The problem:** Interface boxing causes heap allocations for every integer.

```go
package main

import "fmt"

func sumItems(items []interface{}) int {
    total := 0
    for _, item := range items {
        if v, ok := item.(int); ok {
            total += v
        }
    }
    return total
}

func main() {
    items := make([]interface{}, 1000000)
    for i := range items {
        items[i] = i // Each int is boxed into an interface — heap allocation!
    }
    result := sumItems(items)
    fmt.Println("Sum:", result)
}
```

**Current benchmark:**
```
BenchmarkSumItems-8    50    23456789 ns/op    16000000 B/op    1000000 allocs/op
```

<details>
<summary>Hint</summary>
Use concrete types instead of `interface{}`. If you need generics, use Go 1.18+ type parameters.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import "fmt"

func sumInts(items []int) int {
    total := 0
    for _, v := range items {
        total += v
    }
    return total
}

func main() {
    items := make([]int, 1000000)
    for i := range items {
        items[i] = i // No boxing — stored directly as int
    }
    result := sumInts(items)
    fmt.Println("Sum:", result)
}
```

**What changed:**
- Used `[]int` instead of `[]interface{}` — no boxing/unboxing
- No type assertion needed — direct type access
- Data is contiguous in memory — better cache locality

**Optimized benchmark:**
```
BenchmarkSumInts-8    5000    234567 ns/op    0 B/op    0 allocs/op
```

**Improvement:** ~100x faster, zero allocations

</details>

<details>
<summary>Learn More</summary>

**Why this works:** `interface{}` (or `any`) stores values as `(type, pointer)` pairs. For small values like `int`, this requires heap allocation to create the pointer. Using concrete types avoids this overhead entirely. This is a key reason Go's performance can match or exceed Java in hot paths.

**When to apply:** In performance-critical loops and data structures. Use concrete types or generics.
**When NOT to apply:** At API boundaries where flexibility is needed — accept interfaces there.

</details>

---

## Exercise 5: Inefficient Sorting 🟡 ⚡

**What the code does:** Finds the top N largest numbers from a large slice.
**The problem:** Sorts the entire slice just to get the top N.

```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
)

func topN(nums []int, n int) []int {
    // Sort the entire slice — O(n log n)
    sorted := make([]int, len(nums))
    copy(sorted, nums)
    sort.Ints(sorted)
    return sorted[len(sorted)-n:]
}

func main() {
    nums := make([]int, 1000000)
    for i := range nums {
        nums[i] = rand.Intn(10000000)
    }
    top := topN(nums, 10)
    fmt.Println("Top 10:", top)
}
```

**Current benchmark:**
```
BenchmarkTopN-8    10    123456789 ns/op    8000000 B/op    2 allocs/op
```

<details>
<summary>Hint</summary>
You only need the top N. Maintain a min-heap of size N — O(n log k) where k=N, instead of O(n log n).
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import (
    "container/heap"
    "fmt"
    "math/rand"
)

type MinHeap []int

func (h MinHeap) Len() int            { return len(h) }
func (h MinHeap) Less(i, j int) bool   { return h[i] < h[j] }
func (h MinHeap) Swap(i, j int)        { h[i], h[j] = h[j], h[i] }
func (h *MinHeap) Push(x interface{})  { *h = append(*h, x.(int)) }
func (h *MinHeap) Pop() interface{} {
    old := *h
    n := len(old)
    x := old[n-1]
    *h = old[:n-1]
    return x
}

func topNFast(nums []int, n int) []int {
    h := &MinHeap{}
    heap.Init(h)

    for _, num := range nums {
        if h.Len() < n {
            heap.Push(h, num)
        } else if num > (*h)[0] {
            (*h)[0] = num
            heap.Fix(h, 0)
        }
    }

    result := make([]int, h.Len())
    for i := h.Len() - 1; i >= 0; i-- {
        result[i] = heap.Pop(h).(int)
    }
    return result
}

func main() {
    nums := make([]int, 1000000)
    for i := range nums {
        nums[i] = rand.Intn(10000000)
    }
    top := topNFast(nums, 10)
    fmt.Println("Top 10:", top)
}
```

**Optimized benchmark:**
```
BenchmarkTopNFast-8    200    6789012 ns/op    384 B/op    4 allocs/op
```

**Improvement:** ~18x faster, ~20000x less memory

</details>

---

## Exercise 6: Lock Contention 🟡 🔄

**What the code does:** A concurrent counter used by multiple goroutines.
**The problem:** Mutex lock on every increment creates contention.

```go
package main

import (
    "fmt"
    "sync"
)

type SlowCounter struct {
    mu    sync.Mutex
    value int64
}

func (c *SlowCounter) Increment() {
    c.mu.Lock()
    c.value++
    c.mu.Unlock()
}

func (c *SlowCounter) Value() int64 {
    c.mu.Lock()
    defer c.mu.Unlock()
    return c.value
}

func main() {
    counter := &SlowCounter{}
    var wg sync.WaitGroup

    for i := 0; i < 100; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for j := 0; j < 100000; j++ {
                counter.Increment()
            }
        }()
    }

    wg.Wait()
    fmt.Println("Count:", counter.Value())
}
```

**Current benchmark:**
```
BenchmarkSlowCounter-8    1    2345678901 ns/op    0 B/op    0 allocs/op
```

<details>
<summary>Hint</summary>
Use `sync/atomic` instead of `sync.Mutex` for simple counter operations. Atomic operations are lock-free.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import (
    "fmt"
    "sync"
    "sync/atomic"
)

type FastCounter struct {
    value int64
}

func (c *FastCounter) Increment() {
    atomic.AddInt64(&c.value, 1) // Lock-free atomic operation
}

func (c *FastCounter) Value() int64 {
    return atomic.LoadInt64(&c.value)
}

func main() {
    counter := &FastCounter{}
    var wg sync.WaitGroup

    for i := 0; i < 100; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for j := 0; j < 100000; j++ {
                counter.Increment()
            }
        }()
    }

    wg.Wait()
    fmt.Println("Count:", counter.Value())
}
```

**Optimized benchmark:**
```
BenchmarkFastCounter-8    5    456789012 ns/op    0 B/op    0 allocs/op
```

**Improvement:** ~5x faster due to elimination of lock contention

</details>

---

## Exercise 7: Unbuffered I/O 🟡 💾

**What the code does:** Writes many lines to a file.
**The problem:** Each `fmt.Fprintln` call triggers a system call.

```go
package main

import (
    "fmt"
    "os"
)

func writeLinesSlow(filename string, n int) error {
    file, err := os.Create(filename)
    if err != nil {
        return err
    }
    defer file.Close()

    for i := 0; i < n; i++ {
        // Each Fprintln triggers a write syscall
        fmt.Fprintf(file, "line %d: some data here\n", i)
    }
    return nil
}

func main() {
    err := writeLinesSlow("/tmp/output.txt", 100000)
    if err != nil {
        fmt.Println("Error:", err)
        return
    }
    fmt.Println("Done writing")
}
```

**Current benchmark:**
```
BenchmarkWriteLinesSlow-8    1    1234567890 ns/op    0 B/op    0 allocs/op
```

<details>
<summary>Hint</summary>
Use `bufio.Writer` to buffer writes. This reduces syscalls from 100,000 to a few hundred.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import (
    "bufio"
    "fmt"
    "os"
)

func writeLinesFast(filename string, n int) error {
    file, err := os.Create(filename)
    if err != nil {
        return err
    }
    defer file.Close()

    // Buffer writes — flushes to disk in large chunks
    writer := bufio.NewWriterSize(file, 64*1024) // 64KB buffer
    defer writer.Flush()

    for i := 0; i < n; i++ {
        fmt.Fprintf(writer, "line %d: some data here\n", i)
    }
    return nil
}

func main() {
    err := writeLinesFast("/tmp/output.txt", 100000)
    if err != nil {
        fmt.Println("Error:", err)
        return
    }
    fmt.Println("Done writing")
}
```

**Optimized benchmark:**
```
BenchmarkWriteLinesFast-8    20    67890123 ns/op    65536 B/op    1 allocs/op
```

**Improvement:** ~18x faster

</details>

---

## Exercise 8: sync.Pool for Buffer Reuse 🔴 📦

**What the code does:** Processes HTTP requests, each needing a temporary buffer.
**The problem:** Each request allocates a new buffer on the heap, creating GC pressure.

```go
package main

import (
    "bytes"
    "fmt"
)

func processRequest(data string) string {
    // New buffer allocated for every request
    buf := new(bytes.Buffer)
    buf.Grow(4096)
    buf.WriteString("PROCESSED: ")
    buf.WriteString(data)
    buf.WriteString(" [OK]")
    return buf.String()
}

func main() {
    // Simulate 1M requests
    for i := 0; i < 1000000; i++ {
        result := processRequest(fmt.Sprintf("request-%d", i))
        _ = result
    }
    fmt.Println("Done processing 1M requests")
}
```

**Current benchmark:**
```
BenchmarkProcessRequest-8    500000    2345 ns/op    8192 B/op    3 allocs/op
```

**Profiling output:**
```
go tool pprof shows: bytes.(*Buffer).grow consuming 40% of allocations
```

<details>
<summary>Hint</summary>
Use `sync.Pool` to reuse buffers across requests. Reset the buffer after use and put it back in the pool.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import (
    "bytes"
    "fmt"
    "sync"
)

var bufferPool = sync.Pool{
    New: func() interface{} {
        b := new(bytes.Buffer)
        b.Grow(4096)
        return b
    },
}

func processRequestFast(data string) string {
    buf := bufferPool.Get().(*bytes.Buffer)
    defer func() {
        buf.Reset()
        bufferPool.Put(buf)
    }()

    buf.WriteString("PROCESSED: ")
    buf.WriteString(data)
    buf.WriteString(" [OK]")
    return buf.String()
}

func main() {
    for i := 0; i < 1000000; i++ {
        result := processRequestFast(fmt.Sprintf("request-%d", i))
        _ = result
    }
    fmt.Println("Done processing 1M requests")
}
```

**What changed:**
- Used `sync.Pool` to reuse `bytes.Buffer` objects — avoids heap allocation on every request
- Buffer is `Reset()` before returning to pool — prevents data leakage
- `Grow(4096)` in the `New` function — pre-allocates capacity once

**Optimized benchmark:**
```
BenchmarkProcessRequestFast-8    1000000    1234 ns/op    4096 B/op    1 allocs/op
```

**Improvement:** ~2x faster, ~3x fewer allocations

</details>

<details>
<summary>Learn More</summary>

**Advanced concept:** `sync.Pool` stores objects in per-P (per-processor) caches, minimizing contention. Objects may be garbage collected between GC cycles, so the pool is not a cache — it is a hint to the GC that these objects can be reused. The pool is especially effective for buffers, temporary structs, and encoder/decoder objects.

**Go source reference:** `src/sync/pool.go` — implements per-P private storage and a victim cache.

</details>

---

## Exercise 9: Struct Layout for Cache Efficiency 🔴 ⚡

**What the code does:** Iterates over a large array of structs checking a boolean field.
**The problem:** Poor struct field ordering causes cache misses.

```go
package main

import "fmt"

// Poor layout: active (1 byte) is between two 8-byte fields
// causing padding and poor cache utilization
type ItemSlow struct {
    Price    float64 // 8 bytes
    Active   bool    // 1 byte + 7 bytes padding
    Quantity int64   // 8 bytes
    Name     string  // 16 bytes (ptr + len)
    Category int32   // 4 bytes + 4 bytes padding
}

func countActiveSlow(items []ItemSlow) int {
    count := 0
    for i := range items {
        if items[i].Active {
            count++
        }
    }
    return count
}

func main() {
    items := make([]ItemSlow, 1000000)
    for i := range items {
        items[i] = ItemSlow{
            Price:    float64(i),
            Active:   i%3 == 0,
            Quantity: int64(i),
            Name:     "item",
            Category: int32(i % 100),
        }
    }
    count := countActiveSlow(items)
    fmt.Println("Active count:", count)
}
```

**Current benchmark:**
```
BenchmarkCountActiveSlow-8    500    2345678 ns/op    0 B/op    0 allocs/op
```

<details>
<summary>Hint</summary>
Consider struct-of-arrays instead of array-of-structs. If you only need to check `Active`, having a separate `[]bool` slice gives perfect cache locality. Also, reorder fields from largest to smallest to minimize padding.
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import "fmt"

// Approach 1: Better field ordering (minimal padding)
type ItemOptimized struct {
    Price    float64 // 8 bytes
    Quantity int64   // 8 bytes
    Name     string  // 16 bytes
    Category int32   // 4 bytes
    Active   bool    // 1 byte + 3 bytes padding (at end, less waste)
}

// Approach 2: Struct-of-arrays for hot-path queries
type ItemStore struct {
    Prices     []float64
    Quantities []int64
    Names      []string
    Categories []int32
    Active     []bool // Separate array — perfect cache locality for filtering
}

func countActiveSOA(store *ItemStore) int {
    count := 0
    for _, a := range store.Active {
        if a {
            count++
        }
    }
    return count
}

func main() {
    n := 1000000
    store := &ItemStore{
        Prices:     make([]float64, n),
        Quantities: make([]int64, n),
        Names:      make([]string, n),
        Categories: make([]int32, n),
        Active:     make([]bool, n),
    }
    for i := 0; i < n; i++ {
        store.Prices[i] = float64(i)
        store.Active[i] = i%3 == 0
        store.Quantities[i] = int64(i)
        store.Names[i] = "item"
        store.Categories[i] = int32(i % 100)
    }

    count := countActiveSOA(store)
    fmt.Println("Active count:", count)
}
```

**Optimized benchmark:**
```
BenchmarkCountActiveSOA-8    2000    567890 ns/op    0 B/op    0 allocs/op
```

**Improvement:** ~4x faster due to better cache locality

</details>

---

## Exercise 10: Sequential Processing to Fan-Out/Fan-In 🔴 🔄

**What the code does:** Processes a batch of items sequentially.
**The problem:** Each item takes 10ms to process, and processing is CPU-bound — perfect for parallelization.

```go
package main

import (
    "fmt"
    "time"
)

func processItem(item int) int {
    // Simulate CPU-intensive work
    time.Sleep(10 * time.Millisecond)
    return item * item
}

func processAllSlow(items []int) []int {
    results := make([]int, len(items))
    for i, item := range items {
        results[i] = processItem(item)
    }
    return results
}

func main() {
    items := make([]int, 100)
    for i := range items {
        items[i] = i
    }

    start := time.Now()
    results := processAllSlow(items)
    elapsed := time.Since(start)

    fmt.Printf("Processed %d items in %v\n", len(results), elapsed)
    // Takes ~1 second (100 items * 10ms each)
}
```

**Current benchmark:**
```
BenchmarkProcessAllSlow-8    1    1003456789 ns/op    800 B/op    1 allocs/op
```

<details>
<summary>Hint</summary>
Use a worker pool with `runtime.NumCPU()` workers. Fan-out work to workers via a channel, fan-in results via another channel. Total time should be ~100ms with 10 workers (100 items / 10 workers * 10ms).
</details>

<details>
<summary>Optimized Code</summary>

```go
package main

import (
    "fmt"
    "runtime"
    "sync"
    "time"
)

func processItem(item int) int {
    time.Sleep(10 * time.Millisecond)
    return item * item
}

type work struct {
    index int
    value int
}

func processAllFast(items []int) []int {
    numWorkers := runtime.NumCPU()
    results := make([]int, len(items))

    jobs := make(chan work, len(items))
    var wg sync.WaitGroup

    // Start workers
    for w := 0; w < numWorkers; w++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for job := range jobs {
                results[job.index] = processItem(job.value)
            }
        }()
    }

    // Send jobs
    for i, item := range items {
        jobs <- work{index: i, value: item}
    }
    close(jobs)

    // Wait for completion
    wg.Wait()
    return results
}

func main() {
    items := make([]int, 100)
    for i := range items {
        items[i] = i
    }

    start := time.Now()
    results := processAllFast(items)
    elapsed := time.Since(start)

    fmt.Printf("Processed %d items in %v (using %d workers)\n",
        len(results), elapsed, runtime.NumCPU())
    // Takes ~100-130ms with 8-10 workers instead of ~1 second
}
```

**Optimized benchmark:**
```
BenchmarkProcessAllFast-8    10    123456789 ns/op    1600 B/op    12 allocs/op
```

**Improvement:** ~8-10x faster (proportional to CPU count)

</details>

---

## Score Card

| Exercise | Difficulty | Category | Found bottleneck? | Your improvement | Target improvement |
|:--------:|:---------:|:--------:|:-----------------:|:----------------:|:-----------------:|
| 1 | 🟢 | 📦 | ☐ | ___ x | 50x |
| 2 | 🟢 | ⚡ | ☐ | ___ x | 2.5x |
| 3 | 🟢 | 📦 | ☐ | ___ x | 1.5x |
| 4 | 🟡 | 📦 | ☐ | ___ x | 100x |
| 5 | 🟡 | ⚡ | ☐ | ___ x | 18x |
| 6 | 🟡 | 🔄 | ☐ | ___ x | 5x |
| 7 | 🟡 | 💾 | ☐ | ___ x | 18x |
| 8 | 🔴 | 📦 | ☐ | ___ x | 2x |
| 9 | 🔴 | ⚡ | ☐ | ___ x | 4x |
| 10 | 🔴 | 🔄 | ☐ | ___ x | 8-10x |

---

## Go Optimization Cheat Sheet

| Problem | Go Solution | Impact |
|:--------|:---------|:------:|
| Too many allocations | Pre-allocate: `make([]T, 0, cap)` | High |
| String concat in loop | `strings.Builder` | High |
| Repeated object creation | `sync.Pool` | Medium-High |
| Map with known size | `make(map[K]V, size)` | Medium |
| Interface boxing in hot path | Use concrete types or generics | Medium-High |
| Unbuffered I/O | `bufio.Reader` / `bufio.Writer` | High |
| Lock contention | `sync.RWMutex` or `sync/atomic` | High |
| GC pressure | Reduce pointer-heavy structures | Medium |
| Cache misses | Struct-of-arrays over array-of-structs | Medium |
| Goroutine overhead | Worker pool pattern | Medium-High |
| Sequential processing | Fan-out/fan-in with worker pool | High |
| Escape to heap | Keep values on stack (avoid returning pointers) | Medium |
