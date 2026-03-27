# Slice Capacity and Growth — Find the Bug

---

## Overview

10 buggy code snippets. Each has a hidden bug related to slice capacity and growth. Try to identify the bug before expanding the solution.

---

## Bug 1: Lost Append

```go
package main

import "fmt"

func addDefault(nums []int) {
    nums = append(nums, 0)
    fmt.Println("inside:", nums)
}

func main() {
    s := []int{1, 2, 3}
    addDefault(s)
    fmt.Println("outside:", s)  // expected [1 2 3 0], got [1 2 3]
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: `addDefault` receives a copy of the slice header. `append` may or may not allocate a new backing array, but in either case the caller's header (`s`) is never updated because Go passes by value.

**Fix**: Return the new slice from the function:
```go
func addDefault(nums []int) []int {
    return append(nums, 0)
}

func main() {
    s := []int{1, 2, 3}
    s = addDefault(s)
    fmt.Println("outside:", s)  // [1 2 3 0]
}
```
</details>

---

## Bug 2: Aliasing Filter

```go
package main

import "fmt"

func filterPositive(s []int) []int {
    result := s[:0]  // reuse backing array
    for _, v := range s {
        if v > 0 {
            result = append(result, v)
        }
    }
    return result
}

func main() {
    nums := []int{-1, 2, -3, 4, -5, 6}
    positive := filterPositive(nums)
    fmt.Println(positive)  // want [2 4 6], may get wrong answer
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: `result := s[:0]` shares the backing array with `s`. As `result` grows via `append`, it overwrites elements of `s` that the `range` loop hasn't processed yet, corrupting the iteration.

For example:
- After appending 2: `s` backing = `[2, 2, -3, 4, -5, 6]`, but next `range` element is index 2 (`-3`) — OK here
- For different data, the corruption can alter which elements pass the filter

**Fix**: Use an independent result slice:
```go
func filterPositive(s []int) []int {
    result := make([]int, 0, len(s))
    for _, v := range s {
        if v > 0 {
            result = append(result, v)
        }
    }
    return result
}
```
</details>

---

## Bug 3: Double Append Aliasing

```go
package main

import "fmt"

func main() {
    base := make([]int, 0, 4)
    a := append(base, 1, 2)
    b := append(base, 3, 4)
    fmt.Println("a:", a)  // expected [1 2], got [3 4]
    fmt.Println("b:", b)  // expected [3 4], got [3 4]
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: Both `append(base, 1, 2)` and `append(base, 3, 4)` start from the same `base` header (ptr, len=0, cap=4). Both write to the same backing array starting at index 0. The second append overwrites what the first wrote. `a` and `b` share the same backing array and `a` sees the value written by `b`.

**Fix**: If you need independent slices, either pre-allocate each independently or copy before the second append:
```go
a := append(base, 1, 2)
// Clone base before second append:
baseCopy := base[:len(base):len(base)]
b := append(baseCopy, 3, 4)
// Or simply:
a := []int{1, 2}
b := []int{3, 4}
```
</details>

---

## Bug 4: Sub-slice Memory Leak

```go
package main

import (
    "fmt"
    "runtime"
)

var stored [][]byte

func storeHeader(data []byte) {
    // Store just the first 8 bytes of each large buffer
    stored = append(stored, data[:8])
}

func main() {
    for i := 0; i < 1000; i++ {
        big := make([]byte, 1<<20)  // 1MB each
        storeHeader(big)
    }
    runtime.GC()
    var m runtime.MemStats
    runtime.ReadMemStats(&m)
    fmt.Printf("HeapAlloc: %d MB\n", m.HeapAlloc/(1<<20))
    // Expected: ~0 MB (only 8KB total), Actual: ~1000 MB!
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: `data[:8]` creates a sub-slice that shares the backing array of `big` (1MB). Even though each `big` variable goes out of scope, the stored sub-slices retain a reference to each 1MB backing array. GC cannot free them. After 1000 iterations, ~1GB stays live.

**Fix**: Copy the first 8 bytes into an independent slice:
```go
func storeHeader(data []byte) {
    header := make([]byte, 8)
    copy(header, data[:8])
    stored = append(stored, header)
}
// Now each stored slice is 8 bytes, and the 1MB buffers can be GC'd
```
</details>

---

## Bug 5: Panic on Index Assignment to Short Slice

```go
package main

import "fmt"

func populateSlice(n int) []int {
    s := make([]int, 0, n)  // len=0, cap=n
    for i := 0; i < n; i++ {
        s[i] = i * i  // direct index assignment
    }
    return s
}

func main() {
    result := populateSlice(5)
    fmt.Println(result)
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: `make([]int, 0, n)` creates a slice with `len=0` and `cap=n`. Directly writing to `s[i]` accesses indices 0..n-1, but the valid indices for `len=0` are none. This panics with `index out of range [0] with length 0`.

**Fix**: Either set the length to `n` at creation, or use `append`:
```go
// Option 1: set length
s := make([]int, n)  // len=n, cap=n
for i := range s {
    s[i] = i * i
}

// Option 2: use append
s := make([]int, 0, n)
for i := 0; i < n; i++ {
    s = append(s, i*i)
}
```
</details>

---

## Bug 6: Goroutine Slice Mutation Race

```go
package main

import (
    "fmt"
    "sync"
)

func processAll(items []string) []string {
    var wg sync.WaitGroup
    results := make([]string, len(items))
    for i, item := range items {
        wg.Add(1)
        go func(idx int, v string) {
            defer wg.Done()
            results = append(results, "processed:"+v)  // BUG
        }(i, item)
    }
    wg.Wait()
    return results
}

func main() {
    items := []string{"a", "b", "c", "d", "e"}
    out := processAll(items)
    fmt.Println(len(out))  // expect 5+5=10? or just 5?
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: Multiple goroutines concurrently call `append` on `results`. If `append` triggers reallocation, the pointer changes, and goroutines reading the old pointer may have a race. Even without reallocation, multiple goroutines writing to the same backing array indices is a data race. This is detected by `go test -race`.

Additionally, the code appends to a pre-sized slice instead of assigning to the correct index, so results end up appended beyond the first 5 zeroed elements.

**Fix**: Use index assignment, which is safe when each goroutine writes to a distinct index:
```go
func processAll(items []string) []string {
    var wg sync.WaitGroup
    results := make([]string, len(items))
    for i, item := range items {
        wg.Add(1)
        go func(idx int, v string) {
            defer wg.Done()
            results[idx] = "processed:" + v  // safe: distinct indices
        }(i, item)
    }
    wg.Wait()
    return results
}
```
</details>

---

## Bug 7: Slice Bounds on Re-slicing

```go
package main

import "fmt"

func getWindow(data []int, start, size int) []int {
    return data[start : start+size]
}

func main() {
    data := []int{1, 2, 3, 4, 5}
    // Get last 3 elements
    window := getWindow(data, 3, 3)  // wants [4, 5, ???]
    fmt.Println(window)
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: `getWindow(data, 3, 3)` computes `data[3:6]`, but `len(data)=5`, so index 6 is out of range. This panics with `slice bounds out of range [3:6] with length 5`.

**Fix**: Add bounds validation:
```go
func getWindow(data []int, start, size int) ([]int, error) {
    if start < 0 || size < 0 || start+size > len(data) {
        return nil, fmt.Errorf("window [%d:%d] out of range for len=%d",
            start, start+size, len(data))
    }
    return data[start : start+size], nil
}
```
</details>

---

## Bug 8: Infinite Growth via Append

```go
package main

import (
    "fmt"
    "time"
)

var eventLog []string

func logEvent(msg string) {
    eventLog = append(eventLog, msg)
}

func startLogger() {
    ticker := time.NewTicker(time.Millisecond)
    go func() {
        for range ticker.C {
            logEvent(fmt.Sprintf("tick at %v", time.Now()))
        }
    }()
}

func main() {
    startLogger()
    time.Sleep(5 * time.Second)
    fmt.Println("total events:", len(eventLog))
    // Runs fine for 5 seconds, but in production runs for days...
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: `eventLog` grows without bound. Every millisecond a new string is appended. After 1 day: 86,400,000 entries × ~100 bytes = ~8.6 GB. The slice will eventually exhaust memory.

Additionally, the goroutine writes to the global `eventLog` without synchronization — this is a data race if anything reads `eventLog` concurrently.

**Fix**: Use a circular/bounded log with synchronization:
```go
const maxEvents = 10000

var (
    eventLog []string
    logMu    sync.Mutex
)

func logEvent(msg string) {
    logMu.Lock()
    defer logMu.Unlock()
    if len(eventLog) >= maxEvents {
        // Rotate: drop oldest half
        copy(eventLog, eventLog[maxEvents/2:])
        eventLog = eventLog[:maxEvents/2]
    }
    eventLog = append(eventLog, msg)
}
```
</details>

---

## Bug 9: Copy Undercount

```go
package main

import "fmt"

func safeCopy(dst, src []int) {
    dst = make([]int, len(src))
    copy(dst, src)
}

func main() {
    src := []int{1, 2, 3, 4, 5}
    var dst []int
    safeCopy(dst, src)
    fmt.Println(dst)  // expected [1 2 3 4 5], got []
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: `dst = make([]int, len(src))` inside `safeCopy` only modifies the local `dst` parameter (a copy of the header). The caller's `dst` is never updated. After the function returns, the caller's `dst` is still nil.

**Fix**: Return the new slice:
```go
func safeCopy(src []int) []int {
    dst := make([]int, len(src))
    copy(dst, src)
    return dst
}

func main() {
    src := []int{1, 2, 3, 4, 5}
    dst := safeCopy(src)
    fmt.Println(dst)  // [1 2 3 4 5]
}
```
</details>

---

## Bug 10: Three-Index Cap Miscalculation

```go
package main

import "fmt"

func limitedSlice(a []int, start, end int) []int {
    // Intention: return a[start:end] with cap limited to end-start
    return a[start:end:start]  // WRONG cap limit
}

func main() {
    a := []int{1, 2, 3, 4, 5, 6, 7, 8}
    s := limitedSlice(a, 2, 5)
    fmt.Println(len(s), cap(s))  // want 3, 3 — got panic or wrong result
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: The three-index expression is `a[low:high:max]` where:
- `low=start=2`, `high=end=5`, `max=start=2`

This requires `low <= high <= max`, i.e., `2 <= 5 <= 2` — which is false. This causes a panic: `slice bounds out of range [2:5:2]`.

The intention was `a[start:end:end]` to set cap = `end - start`:

**Fix**:
```go
func limitedSlice(a []int, start, end int) []int {
    return a[start:end:end]  // cap = end - start
}

func main() {
    a := []int{1, 2, 3, 4, 5, 6, 7, 8}
    s := limitedSlice(a, 2, 5)
    fmt.Println(len(s), cap(s))  // 3, 3
    fmt.Println(s)               // [3 4 5]
}
```
</details>

---

## Bug 11: Wrong `copy` Direction

```go
package main

import "fmt"

func reverseInPlace(s []int) []int {
    reversed := make([]int, len(s))
    for i, v := range s {
        reversed[len(s)-1-i] = v
    }
    copy(s, reversed)  // copy reversed into s — or did we mean the other way?
    return s
}

func main() {
    data := []int{1, 2, 3, 4, 5}
    result := reverseInPlace(data)
    fmt.Println(result)  // [5 4 3 2 1] ✓ seems fine
    fmt.Println(data)    // [5 4 3 2 1] — is this intentional?
}
```

**What is the bug?**

<details>
<summary>Solution</summary>

**Bug**: This is a subtle **design bug**: `reverseInPlace` modifies the caller's slice (`data`) because `copy(s, reversed)` writes into `s`'s backing array (which is `data`'s backing array). The function returns `s`, which is the same backing array.

If the caller did not intend to mutate their original slice, this is a bug. Additionally, if the slice was obtained as a sub-slice of a larger array, the original array is also mutated.

**Fix**: Decide whether mutation is intended and document it clearly. If a non-mutating version is needed:
```go
func reverse(s []int) []int {
    result := make([]int, len(s))
    for i, v := range s {
        result[len(s)-1-i] = v
    }
    return result  // s is not modified
}
```
</details>

---

## Bug 12: Slice of Interfaces Holding Large Values

```go
package main

import (
    "fmt"
    "runtime"
)

type LargeStruct struct {
    data [1024]byte  // 1KB
}

func main() {
    items := make([]interface{}, 1000)
    for i := range items {
        ls := LargeStruct{}
        items[i] = ls  // stores a COPY inside interface
    }
    items = nil
    runtime.GC()
    var m runtime.MemStats
    runtime.ReadMemStats(&m)
    fmt.Printf("HeapAlloc: %dKB\n", m.HeapAlloc/1024)
    // Why is heap not fully freed?
}
```

**What is the bug? (Conceptual)**

<details>
<summary>Solution</summary>

**Bug**: Each `interface{}` that holds a value larger than a machine word causes a heap allocation for the value itself (interface boxing). The `[]interface{}` of 1000 items causes 1000 heap allocations of `LargeStruct` (1KB each = 1MB total), plus the backing array for the slice itself. Even after `items = nil` and GC, the timing of GC collection means memory may not be immediately reclaimed.

More importantly, using `[]interface{}` for large structs is a design smell — it forces heap allocation of each element.

**Fix**: Use a concrete slice type:
```go
items := make([]LargeStruct, 1000)  // one allocation for all 1000 structs
// No per-element heap allocation
```
This is significantly more memory-efficient and cache-friendly.
</details>

---

## Summary Table

| Bug # | Type | Key Lesson |
|-------|------|-----------|
| 1 | Lost append | Always return slice from functions that append |
| 2 | Aliasing filter | Don't reuse input backing array as output |
| 3 | Double-append alias | Appending from same header writes same indices |
| 4 | Memory leak | Sub-slices retain entire backing array |
| 5 | Index on zero-length | `make([]T, 0, n)` has len=0, can't index |
| 6 | Goroutine race | Concurrent append is a data race |
| 7 | Out-of-bounds | Always validate slice bounds before re-slicing |
| 8 | Infinite growth | Unbounded append in long-running goroutine |
| 9 | Copy undercount | `copy` into local var doesn't update caller |
| 10 | Three-index order | `a[low:high:max]` requires low ≤ high ≤ max |
| 11 | Unexpected mutation | `copy` into sub-slice mutates original |
| 12 | Interface boxing | `[]interface{}` of large values causes per-element alloc |
