# Slice Capacity and Growth — Performance Optimization

> **Format:** Each exercise includes difficulty, description, slow code, a benchmark, a hint, and the optimized solution with explanation.

---

## Difficulty Key

- 🟢 Easy — straightforward pre-allocation
- 🟡 Medium — structural changes required
- 🔴 Hard — advanced techniques (pooling, zero-alloc, SIMD-friendly layout)

---

## Exercise 1 — Pre-allocate Before a Loop 🟢

**Description:** The function builds a result slice inside a loop but starts with no capacity, causing repeated reallocations.

**Slow Code:**

```go
package main

func squaresSlowly(n int) []int {
    var result []int          // cap=0, will reallocate ~log2(n) times
    for i := 0; i < n; i++ {
        result = append(result, i*i)
    }
    return result
}
```

**Benchmark:**

```go
package main_test

import "testing"

func BenchmarkSquaresSlow(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        _ = squaresSlowly(10000)
    }
}

func BenchmarkSquaresFast(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        _ = squaresFast(10000)
    }
}
```

**Expected results:**
```
BenchmarkSquaresSlow-8    5000    320000 ns/op    386105 B/op    14 allocs/op
BenchmarkSquaresFast-8   20000     72000 ns/op     81920 B/op     1 allocs/op
```

<details>
<summary>Hint</summary>

You know `n` before the loop starts. Use `make([]int, 0, n)` to allocate exactly enough capacity upfront. The only allocation needed is the one `make` call.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

func squaresFast(n int) []int {
    result := make([]int, 0, n) // pre-allocate exactly n slots
    for i := 0; i < n; i++ {
        result = append(result, i*i) // never reallocates
    }
    return result
}

// Even better: use make with length and direct index assignment
func squaresFastest(n int) []int {
    result := make([]int, n) // len=n, no append needed
    for i := 0; i < n; i++ {
        result[i] = i * i
    }
    return result
}
```

**Why it's faster:**
- `make([]int, 0, n)`: 1 allocation instead of ~14.
- `make([]int, n)` + index assignment: same 1 allocation, slightly faster because it avoids the `append` bounds check overhead.
- Total bytes copied: 0 (no reallocation means no memmove).
</details>

---

## Exercise 2 — Use `copy` to Break Backing Array Sharing 🟢

**Description:** The function returns a subslice of a large buffer. It's fast but keeps the entire large buffer alive in memory, increasing GC pause time.

**Slow Code:**

```go
package main

func extractHeader(data []byte) []byte {
    // Fast but leaks 1MB backing array
    return data[:64]
}

var globalCache [][]byte

func cacheHeader(data []byte) {
    header := extractHeader(data) // 64 bytes visible, 1MB held
    globalCache = append(globalCache, header)
}
```

**Benchmark:**

```go
package main_test

import (
    "runtime"
    "testing"
)

func BenchmarkCacheLeaky(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        data := make([]byte, 1_000_000)
        cacheHeaderLeaky(data)
    }
    runtime.GC()
    b.ReportMetric(float64(len(globalCacheLeaky))*1e6, "bytes-held")
}
```

<details>
<summary>Hint</summary>

Instead of returning `data[:64]`, copy those 64 bytes into a fresh allocation. The original 1MB buffer will then be eligible for GC after the function returns.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

func extractHeaderFast(data []byte) []byte {
    // Allocates only 64 bytes; original data becomes GC-eligible
    header := make([]byte, 64)
    copy(header, data[:64])
    return header
}

// One-liner alternative:
func extractHeaderOneliner(data []byte) []byte {
    return append([]byte(nil), data[:64]...)
}

var globalCacheFast [][]byte

func cacheHeaderFast(data []byte) {
    header := extractHeaderFast(data)
    globalCacheFast = append(globalCacheFast, header)
}
```

**Why it's better:**
- Each cached entry holds only 64 bytes instead of 1MB.
- After `extractHeaderFast` returns, the 1MB `data` buffer is unreachable and GC-collectible.
- Trade-off: one small allocation per call (64 bytes) vs keeping 1MB alive indefinitely.
- For 1000 cached entries: 64KB held vs 1GB held. Dramatic GC improvement.
</details>

---

## Exercise 3 — Avoid Growing a Slice in the Hot Path 🟡

**Description:** A function is called thousands of times per second. It builds a temporary result slice on each call, causing excessive heap allocation.

**Slow Code:**

```go
package main

import "strings"

// Called ~100,000 times per second
func parseFields(line string) []string {
    var fields []string            // allocation on every call
    for _, f := range strings.Split(line, "\t") {
        f = strings.TrimSpace(f)
        if f != "" {
            fields = append(fields, f) // possible reallocation
        }
    }
    return fields
}
```

**Benchmark:**

```go
package main_test

import "testing"

const testLine = "  go  \t  fast  \t  simple  \t  concurrent  "

func BenchmarkParseFieldsSlow(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        _ = parseFields(testLine)
    }
}

func BenchmarkParseFieldsFast(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        _ = parseFieldsFast(testLine)
    }
}
```

**Expected improvement:**
```
BenchmarkParseFieldsSlow-8   500000   2400 ns/op   320 B/op   4 allocs/op
BenchmarkParseFieldsFast-8  2000000    600 ns/op    64 B/op   1 allocs/op
```

<details>
<summary>Hint</summary>

Two improvements: (1) Accept a `[]string` buffer parameter so the caller can reuse it across calls. (2) Pre-allocate using a capacity hint from `strings.Count` or a fixed reasonable size.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

import "strings"

// Caller provides a buffer for reuse — zero allocs when buffer is large enough
func parseFieldsFast(line string) []string {
    // Count separators for capacity hint (one pass, no allocation)
    n := strings.Count(line, "\t") + 1
    fields := make([]string, 0, n)

    start := 0
    for i := 0; i <= len(line); i++ {
        if i == len(line) || line[i] == '\t' {
            f := strings.TrimSpace(line[start:i])
            if f != "" {
                fields = append(fields, f)
            }
            start = i + 1
        }
    }
    return fields
}

// Zero-alloc version: caller provides and reuses the buffer
func parseFieldsZeroAlloc(line string, buf []string) []string {
    buf = buf[:0] // reset length, keep capacity
    start := 0
    for i := 0; i <= len(line); i++ {
        if i == len(line) || line[i] == '\t' {
            f := strings.TrimSpace(line[start:i])
            if f != "" {
                buf = append(buf, f)
            }
            start = i + 1
        }
    }
    return buf
}

// Usage of zero-alloc version:
// buf := make([]string, 0, 16)
// for _, line := range lines {
//     result := parseFieldsZeroAlloc(line, buf)
//     process(result)
// }
```

**Why it's faster:**
- `strings.Split` allocates a new `[]string` on every call — eliminated.
- Buffer reuse (`buf[:0]`) zeroes out `len` but keeps the backing array — zero allocation when buffer is large enough.
- Capacity hint from `strings.Count` avoids reallocation inside the function.
</details>

---

## Exercise 4 — Pool of Slices with `sync.Pool` 🟡

**Description:** A JSON encoder allocates a `[]byte` buffer on every call. Under high load, this floods the GC. Implement a buffer pool.

**Slow Code:**

```go
package main

import (
    "encoding/json"
    "fmt"
)

type Event struct {
    ID   int
    Name string
}

func encodeEventSlow(e Event) ([]byte, error) {
    return json.Marshal(e) // allocates every call
}

func main() {
    e := Event{ID: 1, Name: "login"}
    data, _ := encodeEventSlow(e)
    fmt.Println(string(data))
}
```

**Benchmark:**

```go
package main_test

import "testing"

func BenchmarkEncodeSlow(b *testing.B) {
    b.ReportAllocs()
    e := Event{ID: 1, Name: "login"}
    for i := 0; i < b.N; i++ {
        data, _ := encodeEventSlow(e)
        _ = data
    }
}

func BenchmarkEncodeFast(b *testing.B) {
    b.ReportAllocs()
    e := Event{ID: 1, Name: "login"}
    for i := 0; i < b.N; i++ {
        data, _ := encodeEventFast(e)
        _ = data
    }
}
```

<details>
<summary>Hint</summary>

Use `sync.Pool` to maintain a pool of `*bytes.Buffer` objects. Get a buffer from the pool, encode into it, copy out the result, then return the buffer to the pool. Reset the buffer (not the backing array) before returning.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

import (
    "bytes"
    "encoding/json"
    "fmt"
    "sync"
)

type Event struct {
    ID   int
    Name string
}

var bufPool = sync.Pool{
    New: func() interface{} {
        return bytes.NewBuffer(make([]byte, 0, 256))
    },
}

func encodeEventFast(e Event) ([]byte, error) {
    buf := bufPool.Get().(*bytes.Buffer)
    buf.Reset() // resets len to 0, keeps backing array

    if err := json.NewEncoder(buf).Encode(e); err != nil {
        bufPool.Put(buf)
        return nil, err
    }

    // Copy result BEFORE returning buffer to pool
    result := make([]byte, buf.Len())
    copy(result, buf.Bytes())

    bufPool.Put(buf)
    return result, nil
}

func main() {
    e := Event{ID: 1, Name: "login"}
    data, _ := encodeEventFast(e)
    fmt.Println(string(data))
}
```

**Why it's better:**
- The `bytes.Buffer` backing array is reused across calls — no repeated heap allocation for the encode buffer.
- Only the final result (small, exact-size) is allocated fresh.
- Under high concurrency, `sync.Pool` keeps per-P free lists, reducing lock contention.
- Note: `sync.Pool` entries are cleared between GC cycles — pool is for temporary reuse, not permanent caching.
</details>

---

## Exercise 5 — Two-Pass Algorithm to Avoid Reallocation 🟡

**Description:** The function builds a result slice by appending, but the final size is deterministic. A two-pass approach avoids any reallocation.

**Slow Code:**

```go
package main

func filterAndDouble(s []int) []int {
    var result []int // unknown size → reallocates during append
    for _, v := range s {
        if v > 0 {
            result = append(result, v*2)
        }
    }
    return result
}
```

**Benchmark:**

```go
package main_test

import "testing"

func BenchmarkFilterSlow(b *testing.B) {
    b.ReportAllocs()
    data := make([]int, 10000)
    for i := range data {
        data[i] = i - 5000 // half negative, half positive
    }
    for i := 0; i < b.N; i++ {
        _ = filterAndDouble(data)
    }
}
```

<details>
<summary>Hint</summary>

Do two passes over the input: first count how many elements pass the filter to get the exact result size, then allocate and fill. Two O(n) passes is still O(n) total — and eliminates all reallocations.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

// Two-pass: count, then allocate and fill
func filterAndDoubleFast(s []int) []int {
    // Pass 1: count matching elements
    count := 0
    for _, v := range s {
        if v > 0 {
            count++
        }
    }

    // Single allocation with exact size
    result := make([]int, 0, count)

    // Pass 2: fill result
    for _, v := range s {
        if v > 0 {
            result = append(result, v*2)
        }
    }
    return result
}

// Alternative: in-place if you can modify the input
func filterAndDoubleInPlace(s []int) []int {
    w := 0
    for _, v := range s {
        if v > 0 {
            s[w] = v * 2
            w++
        }
    }
    return s[:w] // no allocation at all
}
```

**Why it's better:**
- Two-pass: 1 allocation (exact size) vs ~log2(n/2) allocations.
- In-place: 0 allocations — modifies original slice, appropriate when the input is no longer needed.
- Trade-off for two-pass: double the iteration time vs fewer allocations. For large n or expensive elements, the allocation savings usually win.
</details>

---

## Exercise 6 — Capacity Hints for Maps Built from Slices 🟡

**Description:** Building a map from a slice is slow because the map starts small and resizes repeatedly.

**Slow Code:**

```go
package main

import "fmt"

func buildIndex(users []string) map[string]int {
    index := make(map[string]int) // starts small, grows ~8x
    for i, u := range users {
        index[u] = i
    }
    return index
}

func main() {
    users := make([]string, 10000)
    for i := range users {
        users[i] = fmt.Sprintf("user%d", i)
    }
    idx := buildIndex(users)
    fmt.Println(len(idx))
}
```

**Benchmark:**

```go
package main_test

import (
    "fmt"
    "testing"
)

func BenchmarkBuildIndexSlow(b *testing.B) {
    b.ReportAllocs()
    users := make([]string, 10000)
    for i := range users {
        users[i] = fmt.Sprintf("user%d", i)
    }
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        _ = buildIndex(users)
    }
}
```

<details>
<summary>Hint</summary>

`make(map[K]V, hint)` accepts a size hint. Pass `len(users)` as the hint to pre-allocate the map's internal hash table, avoiding rehashing during insertion.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

import "fmt"

func buildIndexFast(users []string) map[string]int {
    index := make(map[string]int, len(users)) // pre-allocate with hint
    for i, u := range users {
        index[u] = i
    }
    return index
}

func main() {
    users := make([]string, 10000)
    for i := range users {
        users[i] = fmt.Sprintf("user%d", i)
    }
    idx := buildIndexFast(users)
    fmt.Println(len(idx)) // 10000
}
```

**Why it's better:**
- `make(map[K]V, n)` pre-allocates the internal hash buckets for `n` elements.
- Without the hint, the map starts with 8 buckets and rehashes (realloc + rehash all keys) ~log2(n/8) times.
- With `n=10000`: ~10 rehashes without hint → 0 rehashes with hint.
- Benchmark improvement: typically 30–50% fewer allocations, 20–40% faster for large maps.
- The hint is a lower bound — Go may allocate slightly more to avoid immediate collisions.
</details>

---

## Exercise 7 — Avoid Slice Growth in Recursive Functions 🔴

**Description:** A recursive tree walker builds a path slice by appending, creating a new allocation at each level of recursion.

**Slow Code:**

```go
package main

import "fmt"

type TreeNode struct {
    Val   int
    Left  *TreeNode
    Right *TreeNode
}

func findPaths(node *TreeNode, target int) [][]int {
    if node == nil {
        return nil
    }
    var results [][]int
    var dfs func(n *TreeNode, path []int)
    dfs = func(n *TreeNode, path []int) {
        path = append(path, n.Val) // allocates new backing array if needed
        if n.Left == nil && n.Right == nil {
            sum := 0
            for _, v := range path {
                sum += v
            }
            if sum == target {
                result := make([]int, len(path))
                copy(result, path)
                results = append(results, result)
            }
            return
        }
        if n.Left != nil {
            dfs(n.Left, path)
        }
        if n.Right != nil {
            dfs(n.Right, path)
        }
    }
    dfs(node, nil)
    return results
}

func main() {
    root := &TreeNode{Val: 5,
        Left:  &TreeNode{Val: 4, Left: &TreeNode{Val: 11, Left: &TreeNode{Val: 7}, Right: &TreeNode{Val: 2}}},
        Right: &TreeNode{Val: 8, Left: &TreeNode{Val: 13}, Right: &TreeNode{Val: 4, Right: &TreeNode{Val: 1}}},
    }
    fmt.Println(findPaths(root, 22))
}
```

<details>
<summary>Hint</summary>

Pre-allocate the path slice with the maximum possible depth (height of the tree). Pass it as a slice with its current length tracked via a depth parameter, then truncate it on backtrack (`path = path[:depth]`). This reuses the single backing array across all recursion levels.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

import "fmt"

type TreeNode struct {
    Val   int
    Left  *TreeNode
    Right *TreeNode
}

func treeHeight(n *TreeNode) int {
    if n == nil {
        return 0
    }
    l, r := treeHeight(n.Left), treeHeight(n.Right)
    if l > r {
        return l + 1
    }
    return r + 1
}

func findPathsFast(node *TreeNode, target int) [][]int {
    if node == nil {
        return nil
    }
    // Pre-allocate path with max depth — reused across all recursion levels
    maxDepth := treeHeight(node)
    path := make([]int, 0, maxDepth) // single allocation for path

    var results [][]int
    var dfs func(n *TreeNode, depth int, sum int)

    dfs = func(n *TreeNode, depth int, sum int) {
        // Extend path at current depth (reuses backing array up to maxDepth)
        path = path[:depth+1]
        path[depth] = n.Val
        sum += n.Val

        if n.Left == nil && n.Right == nil {
            if sum == target {
                result := make([]int, depth+1)
                copy(result, path[:depth+1])
                results = append(results, result)
            }
            return
        }
        if n.Left != nil {
            dfs(n.Left, depth+1, sum)
        }
        if n.Right != nil {
            dfs(n.Right, depth+1, sum)
        }
        // Backtrack: restore depth (path[:depth] implicitly on next call)
    }

    dfs(node, 0, 0)
    return results
}

func main() {
    root := &TreeNode{Val: 5,
        Left:  &TreeNode{Val: 4, Left: &TreeNode{Val: 11, Left: &TreeNode{Val: 7}, Right: &TreeNode{Val: 2}}},
        Right: &TreeNode{Val: 8, Left: &TreeNode{Val: 13}, Right: &TreeNode{Val: 4, Right: &TreeNode{Val: 1}}},
    }
    fmt.Println(findPathsFast(root, 22)) // [[5 4 11 2] [5 8 4 1]]
}
```

**Why it's better:**
- Original: `append(path, n.Val)` may allocate a new backing array at each level (O(height) allocations per path, O(height * leaves) total).
- Optimized: `path[:depth+1]` reuses the pre-allocated backing array — no allocation per recursion level.
- The `path[depth] = n.Val` assignment is a single array write — no bounds check failure because `depth < maxDepth = cap(path)`.
</details>

---

## Exercise 8 — Replace `append` with Index Assignment in Known-Size Loops 🟢

**Description:** The function uses `append` in a loop where the final size is already known, adding unnecessary overhead per iteration.

**Slow Code:**

```go
package main

func multiply(a, b []float64) []float64 {
    if len(a) != len(b) {
        return nil
    }
    result := make([]float64, 0, len(a))
    for i := range a {
        result = append(result, a[i]*b[i]) // append overhead per iteration
    }
    return result
}
```

<details>
<summary>Hint</summary>

When you pre-allocate with `make([]T, n)` (not `make([]T, 0, n)`), you can use direct index assignment `result[i] = ...` which is faster than `append` because it skips the length/capacity check.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

import "fmt"

func multiplyFast(a, b []float64) []float64 {
    if len(a) != len(b) {
        return nil
    }
    result := make([]float64, len(a)) // len=n, not 0
    for i := range a {
        result[i] = a[i] * b[i] // direct assignment — no bounds or cap check
    }
    return result
}

func main() {
    a := []float64{1, 2, 3, 4, 5}
    b := []float64{2, 3, 4, 5, 6}
    fmt.Println(multiplyFast(a, b)) // [2 6 12 20 30]
}
```

**Why it's faster:**
- `append(result, x)` must check: is `len < cap`? increment len. Write value. (3 operations + potential cap check branch)
- `result[i] = x` is a single bounds-checked write.
- The compiler can often eliminate the bounds check for `result[i]` when iterating `for i := range a` and `len(result) == len(a)`.
- For SIMD auto-vectorization: direct index loops are much more likely to be vectorized by the compiler than append-based loops.

**Benchmark:**
```go
func BenchmarkMultiplySlow(b *testing.B) {
    b.ReportAllocs()
    a := make([]float64, 10000)
    bv := make([]float64, 10000)
    for i := range a { a[i] = float64(i); bv[i] = float64(i) }
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        _ = multiply(a, bv)
    }
}
// vs BenchmarkMultiplyFast: ~15-20% faster, same 1 alloc/op
```
</details>

---

## Exercise 9 — Batch Processing to Reduce Per-Item Allocation 🔴

**Description:** A streaming processor calls a function per record, each call allocating a small result slice. Batching records reduces allocation overhead dramatically.

**Slow Code:**

```go
package main

import "fmt"

type Record struct {
    Fields []string
}

func processRecord(r Record) []int {
    result := make([]int, len(r.Fields)) // 1 alloc per record
    for i, f := range r.Fields {
        result[i] = len(f)
    }
    return result
}

func processStreamSlow(records []Record) [][]int {
    results := make([][]int, len(records))
    for i, r := range records {
        results[i] = processRecord(r)
    }
    return results
}

func main() {
    records := []Record{
        {Fields: []string{"go", "is", "fast"}},
        {Fields: []string{"slices", "are", "efficient"}},
    }
    fmt.Println(processStreamSlow(records))
}
```

<details>
<summary>Hint</summary>

Use a single large backing array for all results and hand out subslices of it. First compute the total number of fields across all records. Then allocate one flat `[]int` of that size, and use `flat[offset:offset+len(r.Fields)]` for each record's result.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

import "fmt"

type Record struct {
    Fields []string
}

// processStreamFast uses a single backing array for all results.
func processStreamFast(records []Record) [][]int {
    // Pass 1: count total fields
    total := 0
    for _, r := range records {
        total += len(r.Fields)
    }

    // Single allocation for all field lengths
    flat := make([]int, total)
    results := make([][]int, len(records))

    offset := 0
    for i, r := range records {
        n := len(r.Fields)
        // Assign a slice of flat to this record — no extra allocation
        results[i] = flat[offset : offset+n]
        for j, f := range r.Fields {
            flat[offset+j] = len(f)
        }
        offset += n
    }

    return results
}

func main() {
    records := []Record{
        {Fields: []string{"go", "is", "fast"}},
        {Fields: []string{"slices", "are", "efficient"}},
    }
    fmt.Println(processStreamFast(records))
    // [[2 2 4] [6 3 9]]
}
```

**Why it's better:**
- Original: 1 allocation per record → O(n) allocations for n records.
- Optimized: 2 allocations total (one for `flat`, one for `results` slice of slices) regardless of n.
- For 100,000 records: ~100,000 allocations → 2 allocations. Massive GC pressure reduction.
- All field-length data is contiguous in `flat` — cache-friendly access pattern.
</details>

---

## Exercise 10 — Use Stack-Allocated Array as Slice Backing 🔴

**Description:** A function that processes at most 8 items uses a heap-allocated slice. Replace it with a stack-allocated array to eliminate the heap allocation entirely.

**Slow Code:**

```go
package main

import (
    "fmt"
    "strings"
)

func splitPath(path string) []string {
    parts := strings.Split(path, "/") // heap allocation
    var result []string               // heap allocation
    for _, p := range parts {
        if p != "" {
            result = append(result, p)
        }
    }
    return result
}

func main() {
    fmt.Println(splitPath("/usr/local/bin/go"))
}
```

<details>
<summary>Hint</summary>

Declare a fixed-size array on the stack: `var buf [8]string`. Take a slice of it: `result := buf[:0]`. Append into the slice — as long as you never exceed 8 elements, no heap allocation occurs. For safety, return a copy if the caller retains the result.
</details>

<details>
<summary>Optimized Solution</summary>

```go
package main

import (
    "fmt"
    "strings"
)

// splitPathFast uses a stack-allocated backing array for paths with <= 8 components.
func splitPathFast(path string) []string {
    var buf [8]string              // stack-allocated array — zero heap cost
    result := buf[:0]              // slice backed by the stack array

    start := 0
    if len(path) > 0 && path[0] == '/' {
        start = 1
    }
    for i := start; i <= len(path); i++ {
        if i == len(path) || path[i] == '/' {
            if i > start {
                if len(result) < len(buf) {
                    result = append(result, path[start:i])
                }
            }
            start = i + 1
        }
    }

    // If caller retains result beyond this function, copy to heap.
    // For immediate use (no escape), this is zero-alloc.
    return result
}

// For callers that need to store the result:
func splitPathSafe(path string) []string {
    // Use fast version for parsing, copy to heap for storage
    fast := splitPathFast(path)
    out := make([]string, len(fast))
    copy(out, fast)
    return out
}

func main() {
    // Direct use — zero alloc (if compiler determines result doesn't escape)
    parts := splitPathFast("/usr/local/bin/go")
    fmt.Println(parts) // [usr local bin go]

    // Stored use — one alloc for the output
    stored := splitPathSafe("/etc/ssl/certs")
    fmt.Println(stored) // [etc ssl certs]

    _ = strings.Split // suppress import
}
```

**Why it's better:**
- Stack allocation is essentially free — just decrement the stack pointer.
- No GC involvement for stack-allocated objects.
- For hot paths processing many short paths, this eliminates millions of small allocations per second.
- Caveat: if the slice escapes to the heap (e.g., stored in a global, passed to an interface), the compiler will heap-allocate `buf` anyway. Use `go build -gcflags="-m"` to check.

**Benchmark:**
```go
func BenchmarkSplitSlow(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        _ = splitPath("/usr/local/bin/go")
    }
}
func BenchmarkSplitFast(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ {
        _ = splitPathFast("/usr/local/bin/go")
    }
}
// Expected: slow=3 allocs/op, fast=0 allocs/op
```
</details>

---

## Summary Table

| Exercise | Technique | Alloc Reduction |
|----------|-----------|-----------------|
| 1 | Pre-allocate before loop | 14 → 1 allocs |
| 2 | `copy` to break backing array sharing | Prevents MB-scale leaks |
| 3 | Buffer reuse + capacity hint | 4 → 0 allocs (with reuse) |
| 4 | `sync.Pool` for buffer reuse | Near-zero allocs at scale |
| 5 | Two-pass count + allocate | log2(n) → 1 alloc |
| 6 | Map capacity hint | ~10 rehashes → 0 rehashes |
| 7 | Pre-allocated path + backtrack | O(height) → 1 alloc |
| 8 | Direct index vs `append` | Same allocs, faster access |
| 9 | Single flat backing array | O(n) → 2 allocs |
| 10 | Stack-allocated backing array | heap alloc → 0 allocs |

**General rules:**
1. If you know the size upfront, use `make([]T, n)` or `make([]T, 0, n)`.
2. If the function is called frequently, accept a `buf []T` parameter and reset with `buf[:0]`.
3. If allocations are measured in the hot path, consider `sync.Pool`.
4. Never return a subslice of a large buffer — always `copy` to a new slice.
5. Use `go test -bench=. -benchmem` to measure; never optimize without measuring.
