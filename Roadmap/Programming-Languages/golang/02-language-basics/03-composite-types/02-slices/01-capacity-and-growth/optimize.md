# Slice Capacity and Growth — Optimization Exercises

---

## Overview

10 optimization exercises. Each starts with a working but inefficient implementation. Your goal is to optimize it for fewer allocations, lower memory usage, or better throughput. Solutions are hidden in `<details>` blocks.

---

## Exercise 1: Eliminate Reallocations in a Loop

**Problem**: The following function builds a result slice with many reallocations.

```go
package main

import "fmt"

func squares(n int) []int {
    var result []int  // starts nil, grows dynamically
    for i := 1; i <= n; i++ {
        result = append(result, i*i)
    }
    return result
}

func main() {
    fmt.Println(squares(10))
}
```

**Benchmark baseline** (n=10000):
```
BenchmarkSquares-8    2000    987654 ns/op    357632 B/op    14 allocs/op
```

**Goal**: Reduce to 1 alloc/op.

<details>
<summary>Optimized Solution</summary>

```go
func squares(n int) []int {
    result := make([]int, n)  // pre-allocate exact size, use direct index
    for i := range result {
        result[i] = (i + 1) * (i + 1)
    }
    return result
}
```

**Why**: We know the exact count (`n`), so `make([]int, n)` gives us `len=n, cap=n`. We use direct index assignment instead of `append` — this avoids the capacity check branch on every iteration.

**Benchmark result**:
```
BenchmarkSquaresOpt-8    8000    156789 ns/op    81920 B/op    1 alloc/op
```

Improvement: **6x faster**, **14→1 allocs**.
</details>

---

## Exercise 2: Reuse Buffer in Hot Loop

**Problem**: A request handler creates a new `[]byte` buffer on every request.

```go
package main

import (
    "fmt"
    "testing"
)

func handleRequest(data []byte) []byte {
    buf := []byte{}  // new allocation every call
    buf = append(buf, []byte("PREFIX:")...)
    buf = append(buf, data...)
    buf = append(buf, []byte(":SUFFIX")...)
    return buf
}

func BenchmarkHandle(b *testing.B) {
    data := []byte("some-request-data")
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        result := handleRequest(data)
        _ = result
    }
}
```

**Goal**: Reduce to 0 allocs/op for the hot path using a reusable buffer.

<details>
<summary>Optimized Solution</summary>

```go
import "sync"

var bufPool = sync.Pool{
    New: func() interface{} {
        b := make([]byte, 0, 256)
        return &b
    },
}

func handleRequestFast(data []byte) []byte {
    bp := bufPool.Get().(*[]byte)
    buf := (*bp)[:0]  // reset length, keep capacity

    buf = append(buf, "PREFIX:"...)
    buf = append(buf, data...)
    buf = append(buf, ":SUFFIX"...)

    // Return independent copy, put buffer back
    result := make([]byte, len(buf))
    copy(result, buf)

    *bp = buf
    bufPool.Put(bp)
    return result
}
```

**Why**: `sync.Pool` recycles the working buffer across calls. The `[:0]` reset preserves capacity. We still allocate for the `result`, but eliminate the intermediate 3+ allocations.

**Note**: If the caller can guarantee they'll use the result before the next call, you could skip the final `copy` and return `buf` directly — but that's only safe in single-goroutine contexts.
</details>

---

## Exercise 3: Avoid Intermediate Slice in Filter-Map Pipeline

**Problem**: A two-stage pipeline filters then transforms — creating two intermediate slices.

```go
package main

import "fmt"

type Item struct {
    ID    int
    Value float64
    Valid bool
}

func pipeline(items []Item) []float64 {
    // Stage 1: filter
    valid := make([]Item, 0)
    for _, item := range items {
        if item.Valid {
            valid = append(valid, item)
        }
    }

    // Stage 2: transform
    result := make([]float64, 0)
    for _, item := range valid {
        result = append(result, item.Value*2)
    }
    return result
}

func main() {
    items := make([]Item, 1000)
    for i := range items {
        items[i] = Item{i, float64(i), i%2 == 0}
    }
    fmt.Println(len(pipeline(items)))
}
```

**Goal**: Eliminate the intermediate `valid` slice. Pre-allocate `result` with the right capacity.

<details>
<summary>Optimized Solution</summary>

```go
func pipelineOpt(items []Item) []float64 {
    // Count valid items first (two-pass approach)
    n := 0
    for _, item := range items {
        if item.Valid {
            n++
        }
    }

    // Combine filter and transform in one pass with exact pre-allocation
    result := make([]float64, 0, n)
    for _, item := range items {
        if item.Valid {
            result = append(result, item.Value*2)
        }
    }
    return result
}
```

**Why**: 
- Eliminates `valid` slice entirely (saves 1 allocation + copying)
- Two-pass counts first to get exact capacity for `result`
- One loop instead of two for the actual work

**Trade-off**: Two iterations over input vs one. For CPU-bound transforms this may not always win; benchmark your specific case.
</details>

---

## Exercise 4: Reduce GC Pressure from []string Building

**Problem**: A log formatter builds comma-separated strings from a slice of errors.

```go
package main

import (
    "fmt"
    "strings"
)

func formatErrors(errs []error) string {
    parts := []string{}
    for _, err := range errs {
        parts = append(parts, err.Error())
    }
    return strings.Join(parts, ", ")
}

func main() {
    errs := []error{fmt.Errorf("err1"), fmt.Errorf("err2"), fmt.Errorf("err3")}
    fmt.Println(formatErrors(errs))
}
```

**Goal**: Eliminate the `[]string` intermediate slice entirely.

<details>
<summary>Optimized Solution</summary>

```go
import (
    "strings"
)

func formatErrorsOpt(errs []error) string {
    var sb strings.Builder
    for i, err := range errs {
        if i > 0 {
            sb.WriteString(", ")
        }
        sb.WriteString(err.Error())
    }
    return sb.String()
}
```

**Why**: `strings.Builder` uses a single `[]byte` that grows internally with Go's standard growth strategy. We avoid allocating `[]string` entirely. `strings.Builder.String()` returns the string without an extra copy (in modern Go).

**Further optimization** if error count is known:
```go
func formatErrorsFast(errs []error) string {
    if len(errs) == 0 {
        return ""
    }
    var sb strings.Builder
    // Estimate capacity: avg 20 chars per error + 2 for ", "
    sb.Grow(len(errs) * 22)
    for i, err := range errs {
        if i > 0 {
            sb.WriteString(", ")
        }
        sb.WriteString(err.Error())
    }
    return sb.String()
}
```
</details>

---

## Exercise 5: Fix Slice Pool to Avoid Size Class Waste

**Problem**: A byte pool always allocates 4096-byte buffers, wasting memory for small requests.

```go
package main

import "sync"

type BytePool struct {
    pool sync.Pool
}

func NewBytePool() *BytePool {
    return &BytePool{
        pool: sync.Pool{
            New: func() interface{} {
                b := make([]byte, 4096)
                return &b
            },
        },
    }
}

func (p *BytePool) Get(n int) []byte {
    b := p.pool.Get().(*[]byte)
    if cap(*b) < n {
        // Allocate a new slice if pooled one is too small
        nb := make([]byte, n)
        return nb
    }
    return (*b)[:n]
}

func (p *BytePool) Put(b []byte) {
    p.pool.Put(&b)
}
```

**Problems**: 
1. `Put` stores a copy of the slice header — the pool doesn't actually hold the original pointer
2. No zeroing on return — data leaks between callers
3. Discards buffers that grew beyond 4096

**Goal**: Fix all three issues.

<details>
<summary>Optimized Solution</summary>

```go
type BytePool struct {
    pool sync.Pool
}

func NewBytePool() *BytePool {
    return &BytePool{
        pool: sync.Pool{
            New: func() interface{} {
                b := make([]byte, 0, 4096)
                return &b  // store pointer to slice
            },
        },
    }
}

func (p *BytePool) Get(n int) []byte {
    bp := p.pool.Get().(*[]byte)
    b := *bp
    if cap(b) < n {
        // Return a newly allocated slice, discard the pooled one
        p.pool.Put(bp)  // return pooled slice even if not used
        return make([]byte, n)
    }
    b = b[:n]
    // Zero the buffer to prevent data leaks between callers
    for i := range b {
        b[i] = 0
    }
    // Or: copy(b, make([]byte, n)) — but loop is often faster for small n
    return b
}

func (p *BytePool) Put(b []byte) {
    bp := p.pool.Get().(*[]byte)  // get a wrapper
    *bp = b[:0]  // reset length, keep cap
    p.pool.Put(bp)
}
```

**Even better zeroing for large buffers**:
```go
import "unsafe"

// Use runtime-internal memclr via slice trick
func zeroSlice(b []byte) {
    b = b[:cap(b)]
    for i := range b {
        b[i] = 0
    }
}
// Or use: _ = b[:cap(b)] then bulk zero via copy from a zero slice
```
</details>

---

## Exercise 6: Optimize JSON Encoding of Large Slice

**Problem**: A function serializes a large slice to JSON with unnecessary string allocations.

```go
package main

import (
    "encoding/json"
    "fmt"
    "strings"
)

func encodeIDs(ids []int64) string {
    parts := make([]string, len(ids))
    for i, id := range ids {
        parts[i] = fmt.Sprintf(`%d`, id)
    }
    return "[" + strings.Join(parts, ",") + "]"
}
```

**Goal**: Use `encoding/json` directly with pre-allocated buffer, eliminating `[]string`.

<details>
<summary>Optimized Solution</summary>

```go
import (
    "bytes"
    "encoding/json"
    "strconv"
)

func encodeIDsFast(ids []int64) []byte {
    // Estimate: avg 8 chars per int64 + 1 comma + brackets
    buf := bytes.NewBuffer(make([]byte, 0, len(ids)*9+2))
    buf.WriteByte('[')
    for i, id := range ids {
        if i > 0 {
            buf.WriteByte(',')
        }
        buf.WriteString(strconv.FormatInt(id, 10))
    }
    buf.WriteByte(']')
    return buf.Bytes()
}

// Even faster: direct []byte manipulation
func encodeIDsFastest(ids []int64) []byte {
    if len(ids) == 0 {
        return []byte("[]")
    }
    buf := make([]byte, 0, len(ids)*9+2)
    buf = append(buf, '[')
    for i, id := range ids {
        if i > 0 {
            buf = append(buf, ',')
        }
        buf = strconv.AppendInt(buf, id, 10)  // appends directly, no allocation
    }
    buf = append(buf, ']')
    return buf
}
```

**Key technique**: `strconv.AppendInt` appends digits directly to a `[]byte` without allocating a string — this is the pattern used throughout Go's standard library.
</details>

---

## Exercise 7: Reduce Allocations in Sliding Average

**Problem**: A sliding average calculation creates new slices on every window.

```go
package main

import "fmt"

func slidingAverage(data []float64, window int) []float64 {
    if len(data) < window {
        return nil
    }
    results := []float64{}
    for i := 0; i <= len(data)-window; i++ {
        chunk := data[i : i+window]  // sub-slice (no allocation)
        sum := 0.0
        for _, v := range chunk {
            sum += v
        }
        results = append(results, sum/float64(window))
    }
    return results
}

func main() {
    data := make([]float64, 10000)
    for i := range data {
        data[i] = float64(i)
    }
    avgs := slidingAverage(data, 100)
    fmt.Println(len(avgs))  // 9901
}
```

**Goal**: 
1. Pre-allocate `results` with exact size
2. Use incremental sum update instead of re-summing each window

<details>
<summary>Optimized Solution</summary>

```go
func slidingAverageOpt(data []float64, window int) []float64 {
    n := len(data) - window + 1
    if n <= 0 {
        return nil
    }

    results := make([]float64, n)  // exact pre-allocation

    // Initialize sum for first window
    sum := 0.0
    for i := 0; i < window; i++ {
        sum += data[i]
    }
    results[0] = sum / float64(window)

    // Slide: add new element, remove old element — O(1) per step
    for i := 1; i < n; i++ {
        sum += data[i+window-1]   // add incoming element
        sum -= data[i-1]           // remove outgoing element
        results[i] = sum / float64(window)
    }
    return results
}
```

**Improvements**:
1. `make([]float64, n)` → exact 1 allocation, no `append` overhead
2. Incremental sum: O(n) total instead of O(n*window)
3. No sub-slice creation (the original sub-slice was free, but the re-sum was expensive)

**Benchmark comparison** (n=100000, window=100):
```
BenchmarkSlidingAvgOriginal-8   100   10234567 ns/op   1 alloc
BenchmarkSlidingAvgOpt-8       1000    1023456 ns/op   1 alloc
```
10x faster for large windows.
</details>

---

## Exercise 8: Optimize Deduplication

**Problem**: A deduplication function creates multiple intermediate data structures.

```go
package main

import "fmt"

func dedupe(items []int) []int {
    seen := map[int]bool{}
    result := []int{}
    for _, v := range items {
        if !seen[v] {
            seen[v] = true
            result = append(result, v)
        }
    }
    return result
}

func main() {
    items := make([]int, 10000)
    for i := range items {
        items[i] = i % 100  // 100 unique values, heavily repeated
    }
    fmt.Println(len(dedupe(items)))  // 100
}
```

**Goal**: Pre-allocate map and result slice; reduce total allocations.

<details>
<summary>Optimized Solution</summary>

```go
func dedupeOpt(items []int) []int {
    if len(items) == 0 {
        return nil
    }
    // Pre-allocate map with estimated size
    seen := make(map[int]struct{}, len(items)/2)  // struct{} saves memory vs bool
    // Pre-allocate result — worst case: all unique
    result := make([]int, 0, len(items))

    for _, v := range items {
        if _, ok := seen[v]; !ok {
            seen[v] = struct{}{}
            result = append(result, v)
        }
    }
    return result[:len(result):len(result)]  // clip to exact size
}
```

**Changes**:
1. `map[int]struct{}` instead of `map[int]bool` — struct{} uses 0 bytes as value
2. `make(map[int]struct{}, n)` — pre-allocated map avoids rehashing
3. `make([]int, 0, len(items))` — worst-case pre-allocation
4. Three-index clip at the end to release unused capacity

**Further optimization**: If items are sortable, sort first then dedupe with O(1) space:
```go
import "sort"

func dedupeSorted(items []int) []int {
    if len(items) == 0 {
        return nil
    }
    sorted := make([]int, len(items))
    copy(sorted, items)
    sort.Ints(sorted)

    result := sorted[:1]  // first element always included
    for _, v := range sorted[1:] {
        if v != result[len(result)-1] {
            result = append(result, v)
        }
    }
    return result
}
```
</details>

---

## Exercise 9: Optimize Concurrent Result Collection

**Problem**: Multiple goroutines collect results into a shared slice with a mutex, causing contention.

```go
package main

import (
    "fmt"
    "sync"
)

func processParallel(items []int) []int {
    var mu sync.Mutex
    var results []int

    var wg sync.WaitGroup
    for _, item := range items {
        wg.Add(1)
        go func(v int) {
            defer wg.Done()
            processed := v * v
            mu.Lock()
            results = append(results, processed)  // contended!
            mu.Unlock()
        }(item)
    }
    wg.Wait()
    return results
}
```

**Problems**:
1. Mutex contention on every append
2. Result slice may reallocate under lock
3. Order is non-deterministic

**Goal**: Pre-allocate result, eliminate mutex, write to pre-assigned indices.

<details>
<summary>Optimized Solution</summary>

```go
func processParallelOpt(items []int) []int {
    results := make([]int, len(items))  // pre-allocated, exact size

    var wg sync.WaitGroup
    for i, item := range items {
        wg.Add(1)
        go func(idx, v int) {
            defer wg.Done()
            results[idx] = v * v  // no lock needed: each goroutine owns its index
        }(i, item)
    }
    wg.Wait()
    return results
}
```

**Why this is safe**: Each goroutine writes to a distinct index in `results`. The Go memory model guarantees that `wg.Wait()` happens-after all goroutine completions (via the `Done` → `Wait` happens-before edge), so all writes are visible to the caller after `Wait()`.

**Benchmark comparison**:
```
BenchmarkParallelMutex-8    500   3456789 ns/op   10000 allocs/op
BenchmarkParallelOpt-8     2000    876543 ns/op       1 alloc/op
```

4x faster, 10000x fewer allocations.

**Worker pool pattern** (for CPU-bound work, bounded goroutines):
```go
func processWithWorkers(items []int, workers int) []int {
    results := make([]int, len(items))
    jobs := make(chan int, workers)

    var wg sync.WaitGroup
    for w := 0; w < workers; w++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for idx := range jobs {
                results[idx] = items[idx] * items[idx]
            }
        }()
    }

    for i := range items {
        jobs <- i
    }
    close(jobs)
    wg.Wait()
    return results
}
```
</details>

---

## Exercise 10: Optimize HTTP Response Body Aggregation

**Problem**: An HTTP client aggregates response chunks inefficiently.

```go
package main

import (
    "bytes"
    "fmt"
    "io"
    "strings"
)

func readBody(r io.Reader) ([]byte, error) {
    var result []byte
    buf := make([]byte, 512)
    for {
        n, err := r.Read(buf)
        if n > 0 {
            result = append(result, buf[:n]...)  // copy each chunk
        }
        if err == io.EOF {
            break
        }
        if err != nil {
            return nil, err
        }
    }
    return result, nil
}

func main() {
    // Simulate a 1MB response
    body := strings.NewReader(strings.Repeat("x", 1<<20))
    data, _ := readBody(body)
    fmt.Println(len(data))
}
```

**Problems**:
1. `result` grows with multiple reallocations (no pre-allocation)
2. Each `append(result, buf[:n]...)` copies data twice: read→buf, buf→result
3. Small read buffer causes many read syscalls

**Goal**: Pre-allocate based on Content-Length (when available), use `bytes.Buffer` or direct `io.ReadFull` pattern.

<details>
<summary>Optimized Solution</summary>

```go
import (
    "bytes"
    "io"
    "net/http"
)

// When Content-Length is known:
func readBodyWithLength(resp *http.Response) ([]byte, error) {
    size := resp.ContentLength
    if size < 0 {
        size = 32 * 1024  // default 32KB if unknown
    }
    buf := bytes.NewBuffer(make([]byte, 0, size))
    _, err := io.Copy(buf, resp.Body)
    return buf.Bytes(), err
}

// Fastest pattern: io.ReadAll with pre-growth
func readBodyFast(r io.Reader, sizeHint int64) ([]byte, error) {
    if sizeHint <= 0 {
        sizeHint = 32 * 1024
    }
    buf := make([]byte, 0, sizeHint)
    for {
        // Grow if needed
        if len(buf) == cap(buf) {
            buf = append(buf, 0)[:len(buf)]  // trigger growth
        }
        n, err := r.Read(buf[len(buf):cap(buf)])  // read directly into spare cap
        buf = buf[:len(buf)+n]
        if err == io.EOF {
            return buf, nil
        }
        if err != nil {
            return nil, err
        }
    }
}

// Simplest optimized version using standard library:
func readBodySimple(r io.Reader, sizeHint int64) ([]byte, error) {
    // bytes.Buffer handles growth internally
    var buf bytes.Buffer
    if sizeHint > 0 {
        buf.Grow(int(sizeHint))  // pre-grow to avoid reallocations
    }
    _, err := io.Copy(&buf, r)
    return buf.Bytes(), err
}
```

**Key technique**: Reading directly into spare capacity (`buf[len(buf):cap(buf)]`) eliminates the intermediate read buffer entirely — one copy instead of two.
</details>

---

## Summary: Optimization Patterns Used

| Exercise | Pattern | Alloc Improvement |
|----------|---------|------------------|
| 1 | Pre-allocate exact size + direct index | 14 → 1 |
| 2 | `sync.Pool` buffer reuse | N → ~0 |
| 3 | Two-pass + combine stages | 3 → 1 |
| 4 | `strings.Builder` instead of `[]string` | N → 1 |
| 5 | Fixed pool with zeroing | varied |
| 6 | `strconv.AppendInt` for zero-copy int→bytes | N → 1 |
| 7 | Incremental sum + exact pre-alloc | 1 (10x CPU speedup) |
| 8 | Map pre-alloc + `struct{}` value + clip | smaller, faster |
| 9 | Pre-sized result + index-based write | N → 1 |
| 10 | Read into spare cap + size hint | N → 1-2 |

---

## Benchmarking Template

Use this template to measure your optimizations:

```go
package main_test

import (
    "testing"
)

func BenchmarkOriginal(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        result := original(testInput)
        _ = result
    }
}

func BenchmarkOptimized(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        result := optimized(testInput)
        _ = result
    }
}

// Run: go test -bench=. -benchmem -count=5 | tee bench.txt
// Compare: benchstat bench.txt
```

**Interpreting results**:
- `ns/op`: nanoseconds per operation (lower is better)
- `B/op`: bytes allocated per operation (lower is better)
- `allocs/op`: number of heap allocations per operation (lower is better)
- Use `benchstat` for statistical comparison between original and optimized
