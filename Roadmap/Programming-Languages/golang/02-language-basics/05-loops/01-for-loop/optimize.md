# Go for Loop (C-style) — Optimize

Each exercise provides slow or suboptimal code. Your task is to identify the performance issue and rewrite the code to be faster, more idiomatic, or both.

**Difficulty ratings:**
- 🟢 Easy — Obvious fix, beginner-level optimization
- 🟡 Medium — Requires understanding Go internals or idiomatic patterns
- 🔴 Hard — Requires deep knowledge of compiler behavior, memory layout, or concurrency

---

## Exercise 1 — Eliminate Redundant Length Computation 🟢

**Slow code:**
```go
func countVowels(s string) int {
    count := 0
    for i := 0; i < len(s); i++ {
        switch s[i] {
        case 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U':
            count++
        }
    }
    return count
}
```

**Question**: Is `len(s)` recomputed on every iteration? How can you make the intent clearer and avoid any potential repeated computation for more complex bounds?

<details>
<summary>Solution</summary>

**Analysis**: For a simple string `len(s)`, the compiler typically hoists this out of the loop since it's a read of the string header's length field. However, for clarity and to handle more complex bounds (e.g., function calls), caching is idiomatic:

```go
func countVowels(s string) int {
    count := 0
    n := len(s) // explicit cache — intent is clear: length won't change
    for i := 0; i < n; i++ {
        switch s[i] {
        case 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U':
            count++
        }
    }
    return count
}
```

**Benchmark improvement**: On complex bounds expressions (function calls), this can be a 5-20% win. For simple `len(s)`, the compiler already optimizes it — but explicit caching improves readability.

</details>

---

## Exercise 2 — Replace Manual Index Loop with Two-Pointer 🟢

**Slow code:**
```go
func reverseSlice(s []int) []int {
    result := make([]int, len(s))
    for i := 0; i < len(s); i++ {
        result[len(s)-1-i] = s[i]
    }
    return result
}
```

**Question**: This creates a new allocation. If in-place reversal is acceptable, how can you reduce allocations to zero using a two-pointer loop?

<details>
<summary>Solution</summary>

**Optimized** (in-place, zero allocation):
```go
func reverseInPlace(s []int) {
    for lo, hi := 0, len(s)-1; lo < hi; lo, hi = lo+1, hi-1 {
        s[lo], s[hi] = s[hi], s[lo]
    }
}
```

**If a copy is required** (caller needs a new slice):
```go
func reverseSlice(s []int) []int {
    result := make([]int, len(s))
    for lo, hi := 0, len(s)-1; lo <= hi; lo, hi = lo+1, hi-1 {
        result[lo] = s[hi]
        result[hi] = s[lo]
    }
    return result
}
```

**Improvement**: In-place reduces memory allocation from O(n) to O(1). The two-pointer form also iterates n/2 times instead of n.

</details>

---

## Exercise 3 — Pre-Allocate Result Slice 🟢

**Slow code:**
```go
func doubleAll(nums []int) []int {
    var result []int
    for i := 0; i < len(nums); i++ {
        result = append(result, nums[i]*2)
    }
    return result
}
```

**Question**: What is the performance problem? How many allocations can occur?

<details>
<summary>Solution</summary>

**Problem**: `var result []int` starts with a nil slice (capacity 0). Each `append` when capacity is full triggers a reallocation: capacities grow as 0→1→2→4→8→16... For 1000 elements, this causes ~10 allocations and multiple copy operations.

**Fix**: Pre-allocate with `make`:
```go
func doubleAll(nums []int) []int {
    result := make([]int, 0, len(nums)) // pre-allocate exact capacity
    for i := 0; i < len(nums); i++ {
        result = append(result, nums[i]*2)
    }
    return result
}
```

Or use a pre-sized slice:
```go
func doubleAll(nums []int) []int {
    result := make([]int, len(nums)) // pre-sized, use direct assignment
    for i := 0; i < len(nums); i++ {
        result[i] = nums[i] * 2
    }
    return result
}
```

**Benchmark improvement**: 2-5x for large slices, due to eliminated reallocations and copies.

</details>

---

## Exercise 4 — Avoid Heap Allocation in Loop Body 🟡

**Slow code:**
```go
func processAll(items []string) []string {
    result := make([]string, len(items))
    for i := 0; i < len(items); i++ {
        buf := make([]byte, 0, 256)  // new allocation per iteration!
        buf = append(buf, items[i]...)
        buf = append(buf, '_', 'p', 'r', 'o', 'c')
        result[i] = string(buf)
    }
    return result
}
```

**Question**: Where is the unnecessary allocation? How do you reuse the buffer?

<details>
<summary>Solution</summary>

**Problem**: `make([]byte, 0, 256)` allocates a new 256-byte buffer on every iteration. For 10,000 items, this creates 10,000 allocations — significant GC pressure.

**Fix**: Reuse the buffer by declaring it outside the loop:
```go
func processAll(items []string) []string {
    result := make([]string, len(items))
    buf := make([]byte, 0, 256) // allocated once outside the loop
    for i := 0; i < len(items); i++ {
        buf = buf[:0]                    // reset length to 0 (keep capacity)
        buf = append(buf, items[i]...)
        buf = append(buf, '_', 'p', 'r', 'o', 'c')
        result[i] = string(buf)          // string() copies buf — safe to reuse
    }
    return result
}
```

**Key insight**: `buf = buf[:0]` resets the slice length without freeing the underlying array. The buffer is reused each iteration. `string(buf)` copies the bytes, so each `result[i]` owns independent data.

**Benchmark improvement**: 5-10x for many small-to-medium string operations.

</details>

---

## Exercise 5 — Cache-Friendly Matrix Traversal 🟡

**Slow code:**
```go
func sumMatrix(rows, cols int, m [][]float64) float64 {
    var sum float64
    for j := 0; j < cols; j++ {     // outer: columns
        for i := 0; i < rows; i++ { // inner: rows
            sum += m[i][j]           // column-major: cache miss per access
        }
    }
    return sum
}
```

**Question**: Why is this loop order slow? How should it be reordered?

<details>
<summary>Solution</summary>

**Problem**: Go's `[][]float64` is row-major — each `m[i]` is a separate slice in memory. Column-major access (`m[0][j], m[1][j], m[2][j]...`) jumps between different memory regions (different row slices), causing a cache miss on every access.

**Fix**: Row-major traversal:
```go
func sumMatrix(rows, cols int, m [][]float64) float64 {
    var sum float64
    for i := 0; i < rows; i++ {    // outer: rows
        row := m[i]                 // load row pointer once per row
        for j := 0; j < cols; j++ {
            sum += row[j]           // sequential access within row — cache-friendly
        }
    }
    return sum
}
```

**Benchmark improvement**: 3-10x for large matrices (e.g., 1024×1024 float64). The hardware prefetcher predicts sequential access and loads cache lines proactively.

</details>

---

## Exercise 6 — Avoid Repeated Function Call in Loop Condition 🟡

**Slow code:**
```go
func findExpiredTokens(tokens []Token) []Token {
    var expired []Token
    for i := 0; i < len(tokens); i++ {
        if tokens[i].ExpiresAt.Before(time.Now()) { // time.Now() called N times!
            expired = append(expired, tokens[i])
        }
    }
    return expired
}
```

**Question**: What is the performance problem with calling `time.Now()` inside the loop condition check?

<details>
<summary>Solution</summary>

**Problem**: `time.Now()` involves a syscall (or VDSO call) and returns a new `time.Time` value on every iteration. For 10,000 tokens, this is 10,000 syscalls. Additionally, different iterations may get slightly different "now" times — potentially leading to inconsistent results (a token that expires during the loop may or may not be included depending on when its iteration runs).

**Fix**: Capture the current time once before the loop:
```go
func findExpiredTokens(tokens []Token) []Token {
    now := time.Now()               // captured once
    expired := make([]Token, 0)
    for i := 0; i < len(tokens); i++ {
        if tokens[i].ExpiresAt.Before(now) {
            expired = append(expired, tokens[i])
        }
    }
    return expired
}
```

**Benefits**:
1. `time.Now()` called once — no repeated syscalls
2. Consistent "now" across all iterations — semantically correct
3. Compiler may inline the comparison since `now` is a constant across iterations

</details>

---

## Exercise 7 — Replace Nested Loop with Precomputed Map 🟡

**Slow code:**
```go
// O(n²): for each item in a, search all of b
func intersection(a, b []int) []int {
    var result []int
    for i := 0; i < len(a); i++ {
        for j := 0; j < len(b); j++ {
            if a[i] == b[j] {
                result = append(result, a[i])
                break
            }
        }
    }
    return result
}
```

**Question**: This is O(n²). How can you reduce it to O(n) using a map?

<details>
<summary>Solution</summary>

**Optimized** (O(n)):
```go
func intersection(a, b []int) []int {
    // Build lookup set from b
    bSet := make(map[int]struct{}, len(b))
    for i := 0; i < len(b); i++ {
        bSet[b[i]] = struct{}{}
    }
    // Check each element of a against the set
    result := make([]int, 0)
    for i := 0; i < len(a); i++ {
        if _, ok := bSet[a[i]]; ok {
            result = append(result, a[i])
        }
    }
    return result
}
```

**Complexity**: O(|a| + |b|) instead of O(|a| × |b|).

**Benchmark**: For |a| = |b| = 10,000: nested loop ~100M operations vs map approach ~20,000 operations — 5,000x faster.

**Tradeoff**: Map approach uses O(|b|) extra memory. For tiny slices (< 10 elements), the nested loop may be faster due to map overhead.

</details>

---

## Exercise 8 — Reduce Loop Count with Unrolling 🔴

**Slow code:**
```go
func dotProduct(a, b []float64) float64 {
    sum := 0.0
    for i := 0; i < len(a); i++ {
        sum += a[i] * b[i]
    }
    return sum
}
```

**Question**: This has maximum loop overhead for a compute-intensive operation. How can manual unrolling improve throughput for large vectors?

<details>
<summary>Solution</summary>

**Optimized** (4x unrolled):
```go
func dotProduct(a, b []float64) float64 {
    n := len(a)
    if n != len(b) {
        panic("length mismatch")
    }

    var s0, s1, s2, s3 float64
    i := 0

    // Process 4 elements per iteration
    for ; i <= n-4; i += 4 {
        s0 += a[i] * b[i]
        s1 += a[i+1] * b[i+1]
        s2 += a[i+2] * b[i+2]
        s3 += a[i+3] * b[i+3]
    }

    // Handle remainder
    sum := s0 + s1 + s2 + s3
    for ; i < n; i++ {
        sum += a[i] * b[i]
    }
    return sum
}
```

**Why it's faster**:
1. Loop overhead (INCQ + CMPQ + JL) runs N/4 times instead of N times
2. Using 4 accumulators (s0-s3) hides FMA latency — the CPU can compute all 4 independently in parallel (4 execution units)
3. BCE still applies: `i <= n-4` proves `i`, `i+1`, `i+2`, `i+3` are all in bounds

**Benchmark** (N=1024 float64):
```
BenchmarkDotProduct:         ~500 ns/op
BenchmarkDotProductUnrolled: ~180 ns/op  (2.8x faster)
```

**Note**: For production code, benchmark before unrolling — the compiler sometimes achieves similar results automatically.

</details>

---

## Exercise 9 — Parallel Loop with Worker Pool 🔴

**Slow code:**
```go
func processAll(items []Work) []Result {
    results := make([]Result, len(items))
    for i := 0; i < len(items); i++ {
        results[i] = expensiveCompute(items[i]) // sequential — uses 1 CPU core
    }
    return results
}
```

**Question**: This runs entirely sequentially. How do you parallelize it safely using goroutines from a for loop?

<details>
<summary>Solution</summary>

**Optimized** (parallel with goroutines):
```go
func processAll(items []Work) []Result {
    results := make([]Result, len(items))
    var wg sync.WaitGroup
    for i := 0; i < len(items); i++ {
        wg.Add(1)
        go func(idx int) {
            defer wg.Done()
            results[idx] = expensiveCompute(items[idx])
            // Safe: each goroutine writes a different index
        }(i)
    }
    wg.Wait()
    return results
}
```

**For CPU-intensive work, limit to GOMAXPROCS goroutines:**
```go
func processAllBounded(items []Work, maxWorkers int) []Result {
    results := make([]Result, len(items))
    sem := make(chan struct{}, maxWorkers)
    var wg sync.WaitGroup
    for i := 0; i < len(items); i++ {
        wg.Add(1)
        sem <- struct{}{}
        go func(idx int) {
            defer wg.Done()
            defer func() { <-sem }()
            results[idx] = expensiveCompute(items[idx])
        }(i)
    }
    wg.Wait()
    return results
}
```

**Benchmark** (100 items, 1ms each, 8 CPUs):
```
Sequential:    ~100ms
Parallel (8):  ~13ms  (~7.5x speedup)
```

**Key points**:
- Pre-allocate `results` slice — each goroutine writes a different index, no race condition
- Pass `i` as a function argument, not captured (or use Go 1.22)
- Use semaphore to avoid spawning too many goroutines (memory pressure)

</details>

---

## Exercise 10 — Eliminate Bounds Checks with BCE Hint 🔴

**Slow code:**
```go
func normalize(src, dst []float32, scale float32) {
    for i := 0; i < len(src) && i < len(dst); i++ {
        dst[i] = src[i] * scale  // two bounds checks per iteration
    }
}
```

**Question**: How can you restructure this to eliminate bounds checks and make the BCE pass apply?

<details>
<summary>Solution</summary>

**Problem**: The condition `i < len(src) && i < len(dst)` doesn't give the compiler enough information to eliminate bounds checks for both `src[i]` and `dst[i]` separately.

**Fix 1**: Pre-check the lengths, then loop over the minimum:
```go
func normalize(src, dst []float32, scale float32) {
    n := len(src)
    if len(dst) < n {
        n = len(dst)
    }
    // BCE hint: explicitly pre-check both slices
    dst = dst[:n]
    src = src[:n]
    for i := 0; i < n; i++ {
        dst[i] = src[i] * scale  // BCE: both checks eliminated
    }
}
```

**Fix 2**: Use index tricks to force BCE:
```go
func normalize(src, dst []float32, scale float32) {
    n := len(src)
    if len(dst) < n {
        n = len(dst)
    }
    src = src[:n:n]  // reslice to n — compiler knows bounds
    dst = dst[:n:n]
    for i := 0; i < n; i++ {
        dst[i] = src[i] * scale
    }
}
```

**Verify BCE is eliminated:**
```bash
go build -gcflags="-d=ssa/check_bce/debug=1" main.go
# Look for "Removed IsInBounds" messages
```

**Benchmark improvement**: BCE can give 10-30% improvement in tight float loops due to removed branch instructions.

</details>

---

## Exercise 11 — Replace Loop with `copy` Builtin 🟢

**Slow code:**
```go
func copySlice(src []byte) []byte {
    dst := make([]byte, len(src))
    for i := 0; i < len(src); i++ {
        dst[i] = src[i]
    }
    return dst
}
```

**Question**: Is there a faster, more idiomatic way to do this in Go?

<details>
<summary>Solution</summary>

**Optimized**:
```go
func copySlice(src []byte) []byte {
    dst := make([]byte, len(src))
    copy(dst, src)
    return dst
}
```

The built-in `copy` uses `memmove` under the hood, which is implemented with SIMD instructions on all major platforms. It is significantly faster than a manual loop for large slices.

**Benchmark** (N=1024 bytes):
```
BenchmarkManualCopy: ~300 ns/op
BenchmarkBuiltinCopy: ~15 ns/op  (20x faster)
```

Similarly, use `copy` instead of loops for any byte/string copying:
```go
// Don't:
for i := 0; i < len(src); i++ { dst[i] = src[i] }

// Do:
copy(dst, src)
```

</details>

---

## Exercise 12 — Use `strings.Builder` Instead of String Concatenation in Loop 🟡

**Slow code:**
```go
func buildReport(lines []string) string {
    result := ""
    for i := 0; i < len(lines); i++ {
        result += lines[i] + "\n"  // O(n²) allocations!
    }
    return result
}
```

**Question**: Why is string concatenation in a loop quadratic? What is the correct fix?

<details>
<summary>Solution</summary>

**Problem**: Go strings are immutable. Each `result += s` creates a new string by copying all previous content plus the new content. For N lines of average length L:
- Iteration 1: copy 0+L bytes
- Iteration 2: copy L+L bytes
- Iteration 3: copy 2L+L bytes
- ...
- Total: O(N²×L) byte copies

**Fix 1**: Use `strings.Builder`:
```go
import "strings"

func buildReport(lines []string) string {
    var b strings.Builder
    b.Grow(len(lines) * 80) // estimate capacity
    for i := 0; i < len(lines); i++ {
        b.WriteString(lines[i])
        b.WriteByte('\n')
    }
    return b.String()
}
```

**Fix 2**: Use `strings.Join`:
```go
import "strings"

func buildReport(lines []string) string {
    return strings.Join(lines, "\n") + "\n"
}
```

**Benchmark** (1000 lines):
```
BenchmarkConcatenation: ~2ms/op   (O(n²))
BenchmarkBuilder:       ~15µs/op  (100x faster, O(n))
```

</details>
