# Slice Capacity and Growth — Practical Tasks

> **Format:** Each task includes type, goal, starter code with `// TODO`, expected output, and evaluation checklist.

---

## Table of Contents

- [Junior Tasks](#junior-tasks)
- [Middle Tasks](#middle-tasks)
- [Senior Tasks](#senior-tasks)
- [Questions to Answer](#questions-to-answer)
- [Mini Projects](#mini-projects)
- [Challenge](#challenge)

---

## Junior Tasks

---

### Task 1 — Observe Slice Growth

**Type:** Code
**Difficulty:** 🟢 Easy
**Goal:** Print len and cap each time the capacity changes during sequential appends. Observe the doubling behavior.

```go
package main

import "fmt"

func main() {
    s := make([]int, 0)
    prevCap := -1

    for i := 0; i < 33; i++ {
        s = append(s, i)
        // TODO: if cap(s) changed from prevCap,
        //       print: "append #%d: len=%d cap=%d\n"
        //       then update prevCap
    }
}
```

**Expected Output:**
```
append #1:  len=1  cap=1
append #2:  len=2  cap=2
append #3:  len=3  cap=4
append #5:  len=5  cap=8
append #9:  len=9  cap=16
append #17: len=17 cap=32
append #33: len=33 cap=64
```

**Evaluation Checklist:**
- [ ] Correctly detects capacity change
- [ ] Prints len and cap at each growth point
- [ ] Observes doubling pattern for small slices
- [ ] Does not print every iteration — only on capacity change

---

### Task 2 — Pre-allocate vs Dynamic Append

**Type:** Code + Compare
**Difficulty:** 🟢 Easy
**Goal:** Implement both a dynamic and pre-allocated version of building a slice of 1000 squares. Count allocations using `runtime.MemStats`.

```go
package main

import (
    "fmt"
    "runtime"
)

func dynamic() []int {
    // TODO: start with var s []int
    // append i*i for i in 0..999
    // return s
    return nil
}

func preAllocated() []int {
    // TODO: make with capacity 1000
    // append i*i for i in 0..999
    // return s
    return nil
}

func mallocs(f func() []int) uint64 {
    runtime.GC()
    var before runtime.MemStats
    runtime.ReadMemStats(&before)
    f()
    var after runtime.MemStats
    runtime.ReadMemStats(&after)
    return after.Mallocs - before.Mallocs
}

func main() {
    fmt.Printf("dynamic allocs:      %d\n", mallocs(dynamic))
    fmt.Printf("pre-allocated allocs: %d\n", mallocs(preAllocated))
}
```

**Expected Output:**
```
dynamic allocs:       11
pre-allocated allocs:  1
```

**Evaluation Checklist:**
- [ ] `dynamic()` uses no initial capacity
- [ ] `preAllocated()` uses `make([]int, 0, 1000)`
- [ ] Both return identical elements
- [ ] Allocation count for pre-allocated is exactly 1
- [ ] Student can explain the difference in allocation counts

---

### Task 3 — Reslice Without Reallocation

**Type:** Code
**Difficulty:** 🟢 Easy
**Goal:** Demonstrate that reslicing within capacity does not allocate.

```go
package main

import "fmt"

func main() {
    s := make([]int, 3, 10)
    s[0], s[1], s[2] = 10, 20, 30

    // TODO: extend s to length 7 without append (use reslice)
    // Set s[3]=40, s[4]=50, s[5]=60, s[6]=70
    // Hint: s = s[:newLen]

    fmt.Println(s)
    fmt.Println(len(s), cap(s))
}
```

**Expected Output:**
```
[10 20 30 40 50 60 70]
7 10
```

**Evaluation Checklist:**
- [ ] Uses reslice syntax, not append
- [ ] No new allocation occurs
- [ ] `cap` remains 10 after reslice
- [ ] All 7 elements have correct values

---

### Task 4 — The Full Slice Expression

**Type:** Code
**Difficulty:** 🟢 Easy
**Goal:** Use the full slice expression `a[low:high:max]` to safely take a subslice and prevent it from growing into the original array.

```go
package main

import "fmt"

func main() {
    data := []int{1, 2, 3, 4, 5, 6, 7, 8}

    // TODO: create a subslice 'sub' from data[2:5]
    //       using the full slice expression so that cap(sub) == 3
    //       (max = 5)

    fmt.Println(sub)           // [3 4 5]
    fmt.Println(cap(sub))      // 3

    // TODO: append 99 to sub
    //       verify that data[5] is still 6 (not 99)
    sub = append(sub, 99)
    fmt.Println(data[5])       // 6 (unchanged)
    fmt.Println(sub)           // [3 4 5 99]
}
```

**Expected Output:**
```
[3 4 5]
3
6
[3 4 5 99]
```

**Evaluation Checklist:**
- [ ] Uses full slice expression with three indices
- [ ] `cap(sub)` is correctly 3
- [ ] After appending to `sub`, `data[5]` is unchanged
- [ ] Student understands why reallocation occurred

---

## Middle Tasks

---

### Task 5 — Word Frequency Counter with Pre-allocation

**Type:** Code + Design
**Difficulty:** 🟡 Medium
**Goal:** Build a word frequency counter. Pre-allocate the map with a capacity hint. Then collect frequent words into a pre-allocated slice.

```go
package main

import (
    "fmt"
    "strings"
)

func wordFrequency(text string) map[string]int {
    words := strings.Fields(text)
    // TODO: create freq map with capacity hint len(words)
    // count each word
    // return freq
    return nil
}

func frequentWords(freq map[string]int, minCount int) []string {
    // TODO: pre-allocate result slice with capacity len(freq)
    // append words where freq[word] >= minCount
    // return result
    return nil
}

func main() {
    text := "go is fast go is simple go is concurrent go is fun"
    freq := wordFrequency(text)
    fmt.Println(freq)
    words := frequentWords(freq, 3)
    fmt.Println(words) // should contain "go" and "is"
}
```

**Expected Output (order may vary):**
```
map[concurrent:1 fast:1 fun:1 go:4 is:4 simple:1]
[go is]
```

**Evaluation Checklist:**
- [ ] Map created with `make(map[string]int, len(words))`
- [ ] Slice created with `make([]string, 0, len(freq))`
- [ ] Results are correct
- [ ] No unnecessary reallocations in hot path

---

### Task 6 — Implement `Filter` Without Reallocation

**Type:** Code
**Difficulty:** 🟡 Medium
**Goal:** Implement a generic-style `Filter` function that reuses the input slice's backing array when possible (in-place filtering).

```go
package main

import "fmt"

// filterInPlace removes elements not matching predicate.
// It modifies the input slice in place to avoid allocation.
func filterInPlace(s []int, keep func(int) bool) []int {
    // TODO: use a write index approach
    // iterate over s, copy matching elements to front
    // return s[:writeIdx]
    return nil
}

func main() {
    data := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    evens := filterInPlace(data, func(n int) bool { return n%2 == 0 })
    fmt.Println(evens)       // [2 4 6 8 10]
    fmt.Println(cap(evens))  // 10 — same backing array, no allocation
}
```

**Expected Output:**
```
[2 4 6 8 10]
10
```

**Evaluation Checklist:**
- [ ] Uses write-index technique (no allocations)
- [ ] Returns correct subset
- [ ] `cap(result)` equals `cap(original)` — same backing array
- [ ] Works correctly with various predicates
- [ ] Student can explain the trade-off (original data is modified)

---

### Task 7 — Circular Buffer Using a Slice

**Type:** Code + Design
**Difficulty:** 🟡 Medium
**Goal:** Implement a fixed-capacity circular buffer using a slice. The buffer should not grow beyond the initial capacity.

```go
package main

import "fmt"

type CircularBuffer struct {
    data  []int
    head  int
    tail  int
    size  int
    cap   int
}

func NewCircularBuffer(capacity int) *CircularBuffer {
    // TODO: initialize with make([]int, capacity)
    return nil
}

func (cb *CircularBuffer) Push(v int) bool {
    // TODO: return false if full
    // write v at tail, advance tail with wrap-around
    // increment size
    return false
}

func (cb *CircularBuffer) Pop() (int, bool) {
    // TODO: return 0, false if empty
    // read from head, advance head with wrap-around
    // decrement size
    return 0, false
}

func (cb *CircularBuffer) Len() int { return cb.size }

func main() {
    cb := NewCircularBuffer(4)
    cb.Push(1)
    cb.Push(2)
    cb.Push(3)
    cb.Push(4)
    ok := cb.Push(5) // should return false — full
    fmt.Println("Push 5 succeeded:", ok) // false

    v, _ := cb.Pop()
    fmt.Println("Popped:", v) // 1

    cb.Push(5) // now there's room
    for cb.Len() > 0 {
        v, _ := cb.Pop()
        fmt.Print(v, " ")
    }
    fmt.Println()
}
```

**Expected Output:**
```
Push 5 succeeded: false
Popped: 1
2 3 4 5
```

**Evaluation Checklist:**
- [ ] Uses exactly one allocation (`make` in constructor)
- [ ] Never exceeds initial capacity
- [ ] Wrap-around logic is correct
- [ ] Push/Pop return correct bool indicators
- [ ] All elements in correct FIFO order

---

### Task 8 — Benchmark Pre-allocated vs Dynamic Slice

**Type:** Benchmark
**Difficulty:** 🟡 Medium
**Goal:** Write a Go benchmark comparing dynamic growth vs pre-allocated slice for collecting results of processing 10,000 integers.

```go
package bench_test

import "testing"

func processItem(n int) int { return n*n + n }

// TODO: implement BenchmarkDynamic
// - use []int{} or var s []int
// - append processItem(i) for i in 0..9999
// - call b.ReportAllocs()

// TODO: implement BenchmarkPreAllocated
// - use make([]int, 0, 10000)
// - append processItem(i) for i in 0..9999
// - call b.ReportAllocs()

// TODO: implement BenchmarkExactLength
// - use make([]int, 10000) with index assignment s[i] = ...
// - call b.ReportAllocs()
```

Run with: `go test -bench=. -benchmem`

**Expected Results (approximate):**
```
BenchmarkDynamic-8          50000    25000 ns/op    386097 B/op    14 allocs/op
BenchmarkPreAllocated-8    200000     6000 ns/op     81920 B/op     1 allocs/op
BenchmarkExactLength-8     250000     5500 ns/op     81920 B/op     1 allocs/op
```

**Evaluation Checklist:**
- [ ] All three benchmarks implemented correctly
- [ ] `b.ReportAllocs()` called in each
- [ ] Pre-allocated and exact-length have 1 alloc/op
- [ ] Dynamic has ~14 allocs/op (log2(10000) + initial)
- [ ] Student can explain the ns/op difference

---

## Senior Tasks

---

### Task 9 — Detect and Fix Memory Leak

**Type:** Debug + Fix
**Difficulty:** 🔴 Hard
**Goal:** The function below processes log lines, extracts the first 50 bytes of each, and caches them. Find and fix the memory leak.

```go
package main

import (
    "fmt"
)

var cache [][]byte

func processLogs(logs [][]byte) {
    for _, log := range logs {
        // TODO: this code has a memory leak — find it and fix it
        entry := log[:50]
        cache = append(cache, entry)
    }
}

func main() {
    // Simulate 1000 large log entries (1MB each)
    logs := make([][]byte, 100)
    for i := range logs {
        logs[i] = make([]byte, 1_000_000)
        logs[i][0] = byte(i)
    }

    processLogs(logs)
    fmt.Printf("Cache len: %d\n", len(cache))
    // After this, 'logs' goes out of scope but
    // 100MB is still alive due to the leak
}
```

**Expected Fix:** Each `cache` entry should only hold 50 bytes, not reference a 1MB backing array.

**Evaluation Checklist:**
- [ ] Correctly identifies that `log[:50]` keeps full backing array alive
- [ ] Fix uses `copy` or `append([]byte(nil), log[:50]...)` 
- [ ] After fix, only 50 bytes per entry are retained
- [ ] Can describe the impact on GC behavior
- [ ] Knows how to verify with `runtime.ReadMemStats`

---

### Task 10 — Pool of Reusable Slices

**Type:** Design + Code
**Difficulty:** 🔴 Hard
**Goal:** Implement a `BufferPool` that reuses byte slices to reduce GC pressure in a high-throughput system.

```go
package main

import (
    "fmt"
    "sync"
)

type BufferPool struct {
    // TODO: embed sync.Pool
    // pool should store *[]byte
    // New function should create make([]byte, 0, 4096)
}

func NewBufferPool() *BufferPool {
    // TODO: initialize and return
    return nil
}

func (bp *BufferPool) Get() []byte {
    // TODO: get from pool
    // return slice reset to len=0 (keep capacity)
    return nil
}

func (bp *BufferPool) Put(b []byte) {
    // TODO: reset and return to pool
    // only return if cap is not too large (e.g., cap <= 64*1024)
}

func main() {
    pool := NewBufferPool()

    // Simulate processing 1000 requests
    var wg sync.WaitGroup
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func(n int) {
            defer wg.Done()
            buf := pool.Get()
            // simulate writing data
            buf = append(buf, fmt.Sprintf("request-%d", n)...)
            pool.Put(buf)
        }(i)
    }
    wg.Wait()
    fmt.Println("Done")
}
```

**Evaluation Checklist:**
- [ ] Uses `sync.Pool` correctly
- [ ] `Get()` returns `s[:0]` (resets len, keeps cap)
- [ ] `Put()` guards against oversized buffers
- [ ] Thread-safe (sync.Pool handles this)
- [ ] Student can explain why this reduces GC pressure
- [ ] Student knows that sync.Pool entries may be collected between GC cycles

---

### Task 11 — Avoid Growing Slice in Hot Path

**Type:** Refactor
**Difficulty:** 🔴 Hard
**Goal:** The function below is called 1M times per second and allocates in the hot path. Refactor it to zero allocations.

```go
package main

import (
    "fmt"
    "strings"
)

// SLOW: allocates on every call
func parseTagsSlow(input string) []string {
    parts := strings.Split(input, ",")
    var tags []string
    for _, p := range parts {
        p = strings.TrimSpace(p)
        if p != "" {
            tags = append(tags, p)
        }
    }
    return tags
}

// TODO: implement parseTagsFast that:
// 1. Accepts a []string buffer as parameter (caller provides)
// 2. Resets and reuses the buffer (buf = buf[:0])
// 3. Returns buf with results written in
// This avoids ALL allocations when the caller reuses the buffer

func main() {
    input := "go, python, rust, , java"
    fmt.Println(parseTagsSlow(input))

    // TODO: demonstrate parseTagsFast with a reused buffer
}
```

**Expected Output:**
```
[go python rust java]
[go python rust java]
```

**Evaluation Checklist:**
- [ ] `parseTagsFast` takes a `buf []string` parameter
- [ ] Resets buf with `buf = buf[:0]`
- [ ] Returns `buf` with results appended
- [ ] No allocations when buffer has sufficient capacity
- [ ] Caller demonstrates reuse across multiple calls

---

## Questions to Answer

Answer these in writing (no code required):

**Q1.** Why does `append` need to return a value? What would go wrong if it modified the slice in place?

**Q2.** What is the "amortized O(1)" claim for append, and why does geometric growth (doubling) guarantee it?

**Q3.** Explain the trade-off between `make([]T, n)` and `make([]T, 0, n)`. When would you use each?

**Q4.** A colleague says "I never need `cap()` because the runtime handles growth automatically." In what situations is knowing `cap()` important?

**Q5.** Why did Go 1.18 lower the growth threshold from 1024 to 256? What problem did the old threshold cause?

---

## Mini Projects

---

### Mini Project 1 — Capacity-Aware Builder

**Goal:** Implement a `StringBuilder`-like type that pre-allocates and tracks its own capacity growth.

```go
type StringBuilder struct {
    buf []byte
    // TODO: add a field to track reallocations
}

func (sb *StringBuilder) WriteString(s string) *StringBuilder
func (sb *StringBuilder) WriteByte(b byte) *StringBuilder
func (sb *StringBuilder) String() string
func (sb *StringBuilder) Reallocations() int
func (sb *StringBuilder) Len() int
func (sb *StringBuilder) Cap() int
```

Requirements:
- Start with `make([]byte, 0, 64)`
- Track how many times the backing array was reallocated
- All methods chainable (return `*StringBuilder`)

---

### Mini Project 2 — Slice Growth Visualizer

**Goal:** Write a CLI tool that prints an ASCII histogram of capacity growth steps for different element types and starting sizes.

```
go run main.go --type int --start 1 --appends 1000

Capacity Growth for []int (1000 appends from cap=1):
  cap=1    ██
  cap=2    ████
  cap=4    ████████
  cap=8    ████████████████
  ...
```

Requirements:
- Shows actual capacity at each growth point
- Marks the 256 threshold with a divider
- Accepts command-line flags

---

## Challenge

### Challenge — Zero-Allocation JSON Tag Parser

**Goal:** Parse JSON field tags like `json:"name,omitempty"` into a struct with zero heap allocations. Use a fixed-size backing array on the stack.

```go
package main

import "fmt"

type TagOptions struct {
    Name      string
    OmitEmpty bool
    String    bool
    // up to 4 options
}

// parseJSONTag must have 0 allocs/op in benchmarks.
// Hint: use [4]string on the stack, take a slice of it.
func parseJSONTag(tag string) TagOptions {
    // TODO
    return TagOptions{}
}

func main() {
    fmt.Println(parseJSONTag(`name,omitempty`))
    // {Name:name OmitEmpty:true String:false}
    fmt.Println(parseJSONTag(`id,string`))
    // {Name:id OmitEmpty:false String:true}
}
```

**Constraint:** `go test -bench=BenchmarkParseJSONTag -benchmem` must report `0 allocs/op`.

**Evaluation Checklist:**
- [ ] Zero allocations confirmed by benchmark
- [ ] Correct parsing of name, omitempty, string options
- [ ] Uses stack-allocated array as slice backing
- [ ] Handles edge cases (empty tag, `-` skip tag)
