# Slice Capacity and Growth — Find the Bug

> **Format:** Each bug includes difficulty, description, buggy code, expected vs actual behavior, a hint, and a full explanation with fix.

---

## Difficulty Key

- 🟢 Easy — common beginner mistake
- 🟡 Medium — subtle slice semantics
- 🔴 Hard — low-level or composite bug

---

## Bug 1 — Modifying Shared Backing Array 🟢

**Description:** Two slices share a backing array. The programmer assumes they are independent, but modifying one affects the other.

```go
package main

import "fmt"

func main() {
    original := []int{1, 2, 3, 4, 5}
    copy := original[1:4]

    copy[0] = 99

    fmt.Println(original) // programmer expects: [1 2 3 4 5]
    fmt.Println(copy)     // programmer expects: [99 3 4]
}
```

**Expected behavior:** `original` remains `[1 2 3 4 5]`

**Actual behavior:** `original` becomes `[1 99 3 4 5]`

<details>
<summary>Hint</summary>

`copy` is not a deep copy — it is a slice expression. Both `original` and `copy` point to the same underlying array. Check how slicing works in Go.
</details>

<details>
<summary>Explanation & Fix</summary>

`original[1:4]` creates a new slice header with `ptr = &original[1]`, but the backing array is shared. Writing to `copy[0]` writes to `original[1]`.

**Fix 1:** Use the `copy` built-in function (note: variable named `copy` shadows the built-in — rename it):

```go
package main

import "fmt"

func main() {
    original := []int{1, 2, 3, 4, 5}
    independent := make([]int, 3)
    copy(independent, original[1:4]) // deep copy

    independent[0] = 99

    fmt.Println(original)     // [1 2 3 4 5] — unchanged
    fmt.Println(independent)  // [99 3 4]
}
```

**Fix 2:** Use `append` idiom:

```go
independent := append([]int(nil), original[1:4]...)
```
</details>

---

## Bug 2 — Assuming Append Does Not Reallocate 🟢

**Description:** The programmer appends to a slice, then modifies the original slice expecting the appended slice to reflect the change. But `append` triggered a reallocation.

```go
package main

import "fmt"

func main() {
    base := make([]int, 3, 3) // cap = len = 3, FULL
    base[0], base[1], base[2] = 1, 2, 3

    extended := append(base, 4) // reallocates because cap is full

    base[0] = 99

    fmt.Println(extended[0]) // programmer expects 99
}
```

**Expected behavior (programmer's assumption):** `extended[0]` is `99`

**Actual behavior:** `extended[0]` is `1` — `extended` has its own backing array

<details>
<summary>Hint</summary>

When `append` reallocates, the new slice's backing array is completely separate from the original. What was the capacity of `base` before appending?
</details>

<details>
<summary>Explanation & Fix</summary>

`base` has `cap=3, len=3` — it is full. `append` must allocate a new backing array, copy existing elements, then add the new element. The two slices now point to different memory.

```go
package main

import "fmt"

func main() {
    // Case 1: reallocation (cap full) — separate arrays
    base := make([]int, 3, 3)
    base[0], base[1], base[2] = 1, 2, 3
    extended := append(base, 4)
    base[0] = 99
    fmt.Println(extended[0]) // 1 — they are independent

    // Case 2: no reallocation (cap has room) — shared array
    base2 := make([]int, 3, 6)
    base2[0], base2[1], base2[2] = 1, 2, 3
    extended2 := append(base2, 4)
    base2[0] = 99
    fmt.Println(extended2[0]) // 99 — they share the array!
}
```

**Lesson:** Never assume whether `append` reallocates or not. If you need independent slices, always use `copy`.
</details>

---

## Bug 3 — Memory Leak: Keeping Large Backing Array Alive 🟡

**Description:** A function returns a small subslice of a very large buffer, accidentally keeping the entire large buffer alive in memory.

```go
package main

import (
    "fmt"
    "runtime"
)

func loadConfig() []byte {
    // Simulate loading a 10MB config file
    fullFile := make([]byte, 10*1024*1024)
    // ... parse and fill fullFile ...
    fullFile[0] = 'G'
    fullFile[1] = 'O'

    // Return only the first 10 bytes (the "magic header")
    return fullFile[:10] // BUG: keeps 10MB alive!
}

func main() {
    header := loadConfig()
    fmt.Println(string(header[:2]))

    // Even after loadConfig returns, the 10MB array is NOT collected
    // because 'header' holds a reference into it
    runtime.GC()
    var m runtime.MemStats
    runtime.ReadMemStats(&m)
    fmt.Printf("Heap in use: %.2f MB\n", float64(m.HeapInuse)/1024/1024)
}
```

**Expected behavior:** After `loadConfig` returns, only 10 bytes should be retained.

**Actual behavior:** 10MB of memory is retained.

<details>
<summary>Hint</summary>

The slice header stores a pointer into the middle of the backing array. The GC sees this pointer and cannot collect the backing array. How can you copy only the bytes you need?
</details>

<details>
<summary>Explanation & Fix</summary>

A slice header `{ptr, len, cap}` keeps the entire backing array alive as long as the slice exists. Even though `len=10`, the GC traces `ptr` to the 10MB array and cannot collect it.

```go
package main

import (
    "fmt"
    "runtime"
)

func loadConfig() []byte {
    fullFile := make([]byte, 10*1024*1024)
    fullFile[0] = 'G'
    fullFile[1] = 'O'

    // FIX: copy only what we need into a new, small backing array
    header := make([]byte, 10)
    copy(header, fullFile[:10])
    return header // fullFile is now eligible for GC
}

func main() {
    header := loadConfig()
    fmt.Println(string(header[:2]))
    runtime.GC()
    var m runtime.MemStats
    runtime.ReadMemStats(&m)
    fmt.Printf("Heap in use: %.2f MB\n", float64(m.HeapInuse)/1024/1024)
    // Now shows ~0.00 MB instead of ~10 MB
}
```

**One-liner fix:**
```go
return append([]byte(nil), fullFile[:10]...)
```
</details>

---

## Bug 4 — Off-By-One in Capacity Check 🟡

**Description:** The programmer manually checks if there is capacity before appending, but gets the comparison wrong.

```go
package main

import "fmt"

func safeAppend(s []int, v int) []int {
    if len(s) <= cap(s) { // BUG: should be <, not <=
        s = s[:len(s)+1]
        s[len(s)-1] = v
    }
    return s
}

func main() {
    s := make([]int, 3, 3) // completely full
    s = safeAppend(s, 42)
    fmt.Println(s)
}
```

**Expected behavior:** The function recognizes the slice is full and does not corrupt memory.

**Actual behavior:** When `len == cap`, the condition `len <= cap` is true, so `s[:len+1]` is executed — this panics with "slice bounds out of range".

<details>
<summary>Hint</summary>

When `len(s) == cap(s)`, the slice is full. The check `len <= cap` is true even when full. What should the condition be?
</details>

<details>
<summary>Explanation & Fix</summary>

The correct check is `len(s) < cap(s)` — strictly less than. When `len == cap`, there is no room.

```go
package main

import "fmt"

func safeAppend(s []int, v int) []int {
    if len(s) < cap(s) { // FIX: strict less than
        s = s[:len(s)+1]
        s[len(s)-1] = v
        return s
    }
    // Slice is full — use append to let Go handle reallocation
    return append(s, v)
}

func main() {
    s := make([]int, 3, 3)
    s = safeAppend(s, 42)
    fmt.Println(s) // [0 0 0 42]
    fmt.Println(len(s), cap(s)) // 4, 6 (or similar)
}
```

**Better approach:** Don't reimplement `append` — just use it. This manual pattern is almost always unnecessary.
</details>

---

## Bug 5 — Wrong Full Slice Expression Order 🟡

**Description:** The programmer uses the full slice expression but gets the `max` parameter wrong, causing an immediate panic.

```go
package main

import "fmt"

func main() {
    a := make([]int, 10)
    for i := range a {
        a[i] = i
    }

    // Programmer wants subslice [2:5] with cap limited to 6 elements
    b := a[2:5:8] // BUG: max=8 means cap = 8-2 = 6, which is correct...
                  // BUT: what if they wrote a[5:2:8]?

    // Common mistake: swap low and high
    // c := a[5:2:8] // panic: slice bounds out of range [5:2]

    // Another common mistake: max less than high
    d := a[2:7:5] // BUG: max=5 < high=7 — panic!
    fmt.Println(b, d)
}
```

**Expected behavior:** No panic.

**Actual behavior:** `a[2:7:5]` panics — `max` must be `>= high`.

<details>
<summary>Hint</summary>

The full slice expression requires `low <= high <= max <= cap(a)`. Which constraint is violated?
</details>

<details>
<summary>Explanation & Fix</summary>

The invariant for `a[low:high:max]` is:

```
0 <= low <= high <= max <= cap(a)
```

`a[2:7:5]` violates `high <= max` since `7 > 5`.

```go
package main

import "fmt"

func main() {
    a := make([]int, 10)
    for i := range a {
        a[i] = i
    }

    // CORRECT: 0 <= 2 <= 7 <= 9 <= 10
    b := a[2:7:9]
    fmt.Println(b)       // [2 3 4 5 6]
    fmt.Println(cap(b))  // 7 (= 9 - 2)

    // CORRECT: limit cap to exactly high
    c := a[2:7:7]
    fmt.Println(cap(c))  // 5 — append to c forces immediate reallocation
}
```
</details>

---

## Bug 6 — Append Inside a Loop Does Not Update the Outer Slice 🟡

**Description:** The programmer appends inside a goroutine/function thinking the outer slice will be updated, but the slice header is passed by value.

```go
package main

import "fmt"

func addItems(s []int, items []int) {
    for _, item := range items {
        s = append(s, item) // BUG: modifies local copy of header
    }
    // s here has new elements, but the caller's s does not
}

func main() {
    s := make([]int, 0, 10)
    addItems(s, []int{1, 2, 3})
    fmt.Println(s) // programmer expects [1 2 3], gets []
}
```

**Expected behavior:** `s` in `main` contains `[1 2 3]`

**Actual behavior:** `s` in `main` is still empty `[]`

<details>
<summary>Hint</summary>

Slices are passed by value in Go — the function receives a copy of the header. Changes to `len` and `cap` inside the function are not visible to the caller. How can you propagate the change back?
</details>

<details>
<summary>Explanation & Fix</summary>

The slice header `{ptr, len, cap}` is a value. Passing `s []int` copies the header. `s = append(s, ...)` inside the function updates the local copy only.

**Fix 1:** Return the new slice:

```go
package main

import "fmt"

func addItems(s []int, items []int) []int {
    for _, item := range items {
        s = append(s, item)
    }
    return s
}

func main() {
    s := make([]int, 0, 10)
    s = addItems(s, []int{1, 2, 3})
    fmt.Println(s) // [1 2 3]
}
```

**Fix 2:** Pass a pointer to the slice:

```go
func addItems(s *[]int, items []int) {
    for _, item := range items {
        *s = append(*s, item)
    }
}
```
</details>

---

## Bug 7 — `nil` Out Pointers When Shrinking 🔴

**Description:** A stack implementation pops elements by reducing `len`, but pointer elements remain in the backing array, causing a memory leak.

```go
package main

import "fmt"

type Node struct {
    Value string
    Data  [1024]byte // large struct
}

type Stack struct {
    items []*Node
}

func (s *Stack) Push(n *Node) {
    s.items = append(s.items, n)
}

func (s *Stack) Pop() *Node {
    if len(s.items) == 0 {
        return nil
    }
    n := len(s.items) - 1
    item := s.items[n]
    s.items = s.items[:n] // BUG: pointer at index n still in backing array!
    return item
}

func main() {
    s := &Stack{}
    s.Push(&Node{Value: "first"})
    s.Push(&Node{Value: "second"})
    popped := s.Pop()
    fmt.Println(popped.Value)
    // The backing array at index 1 still holds a *Node pointer
    // The GC cannot collect the Node struct
}
```

**Expected behavior:** After `Pop`, the `Node` at that position is eligible for GC.

**Actual behavior:** The backing array at the popped index still holds the pointer — the `Node` cannot be collected.

<details>
<summary>Hint</summary>

Reducing `len` does not zero out the memory at the old position. The backing array still holds the pointer, keeping the `Node` reachable by the GC. What should you do before shrinking?
</details>

<details>
<summary>Explanation & Fix</summary>

When you do `s.items = s.items[:n]`, the pointer at `s.items[n]` is still in the backing array at index `n`. The GC traces this pointer and keeps the `*Node` alive. This is a classic Go memory leak for pointer slices.

```go
package main

import "fmt"

type Node struct {
    Value string
    Data  [1024]byte
}

type Stack struct {
    items []*Node
}

func (s *Stack) Push(n *Node) {
    s.items = append(s.items, n)
}

func (s *Stack) Pop() *Node {
    if len(s.items) == 0 {
        return nil
    }
    n := len(s.items) - 1
    item := s.items[n]
    s.items[n] = nil   // FIX: nil out the pointer before shrinking
    s.items = s.items[:n]
    return item
}

func main() {
    s := &Stack{}
    s.Push(&Node{Value: "first"})
    s.Push(&Node{Value: "second"})
    popped := s.Pop()
    fmt.Println(popped.Value)
    // Now the Node struct is eligible for GC
}
```

**Rule:** Always nil out pointer (or interface) elements when removing them from a slice that may outlive the elements.
</details>

---

## Bug 8 — Concurrent Append Without Synchronization 🔴

**Description:** Two goroutines append to the same slice concurrently. The programmer thinks slices are goroutine-safe because "Go is concurrent."

```go
package main

import (
    "fmt"
    "sync"
)

func main() {
    var s []int
    var wg sync.WaitGroup

    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func(n int) {
            defer wg.Done()
            s = append(s, n) // BUG: data race!
        }(i)
    }

    wg.Wait()
    fmt.Println(len(s)) // likely not 1000, may crash
}
```

**Expected behavior:** `s` contains all 1000 elements.

**Actual behavior:** Data race — undefined behavior, crashes, or lost elements.

<details>
<summary>Hint</summary>

Go slices are NOT goroutine-safe. Multiple goroutines reading/writing the slice header and backing array simultaneously is a data race. What Go primitive synchronizes access?
</details>

<details>
<summary>Explanation & Fix</summary>

`append` reads `len` and `cap`, potentially allocates a new array, and writes the new header — none of this is atomic. Concurrent appends race on all of these operations.

**Fix 1:** Use a mutex:

```go
package main

import (
    "fmt"
    "sync"
)

func main() {
    var s []int
    var mu sync.Mutex
    var wg sync.WaitGroup

    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func(n int) {
            defer wg.Done()
            mu.Lock()
            s = append(s, n)
            mu.Unlock()
        }(i)
    }

    wg.Wait()
    fmt.Println(len(s)) // 1000
}
```

**Fix 2:** Use a channel to collect results:

```go
results := make(chan int, 1000)
for i := 0; i < 1000; i++ {
    go func(n int) { results <- n }(i)
}
s := make([]int, 0, 1000)
for i := 0; i < 1000; i++ {
    s = append(s, <-results)
}
```
</details>

---

## Bug 9 — Reusing Slice After Pool Return 🔴

**Description:** The programmer returns a slice to a `sync.Pool`, then continues using it — a use-after-free bug.

```go
package main

import (
    "fmt"
    "sync"
)

var pool = sync.Pool{
    New: func() interface{} {
        b := make([]byte, 0, 512)
        return &b
    },
}

func process(data string) string {
    bp := pool.Get().(*[]byte)
    buf := (*bp)[:0]

    buf = append(buf, data...)
    buf = append(buf, "!"...)

    *bp = buf
    pool.Put(bp) // return to pool

    return string(buf) // BUG: buf may be reused by another goroutine!
}

func main() {
    result := process("hello")
    fmt.Println(result)
}
```

**Expected behavior:** Safe return of the result string.

**Actual behavior (in concurrent scenario):** `buf` can be overwritten by another goroutine after `pool.Put(bp)` but before `string(buf)` completes.

<details>
<summary>Hint</summary>

`string(buf)` must complete before `buf` can be reused. But `pool.Put` may immediately make the buffer available to another goroutine. What is the safe ordering?
</details>

<details>
<summary>Explanation & Fix</summary>

`string(buf)` creates a new string by copying the bytes — this copy must happen **before** the buffer is returned to the pool.

```go
package main

import (
    "fmt"
    "sync"
)

var pool = sync.Pool{
    New: func() interface{} {
        b := make([]byte, 0, 512)
        return &b
    },
}

func process(data string) string {
    bp := pool.Get().(*[]byte)
    buf := (*bp)[:0]

    buf = append(buf, data...)
    buf = append(buf, "!"...)

    result := string(buf) // FIX: copy BEFORE returning to pool

    *bp = buf
    pool.Put(bp)

    return result // safe — result is an independent string
}

func main() {
    result := process("hello")
    fmt.Println(result) // "hello!"
}
```
</details>

---

## Bug 10 — Growing Slice in Goroutine Does Not Update Caller 🔴

**Description:** A goroutine receives a slice, appends to it, but the caller never sees the appended elements because the slice header was copied.

```go
package main

import (
    "fmt"
    "sync"
)

func fillConcurrently(s []int, start, end int, wg *sync.WaitGroup) {
    defer wg.Done()
    for i := start; i < end; i++ {
        s = append(s, i) // modifies local header copy
    }
}

func main() {
    result := make([]int, 0, 100)
    var wg sync.WaitGroup

    wg.Add(2)
    go fillConcurrently(result, 0, 50, &wg)
    go fillConcurrently(result, 50, 100, &wg)
    wg.Wait()

    fmt.Println(len(result)) // programmer expects 100, gets 0
}
```

**Expected behavior:** `result` contains 100 elements.

**Actual behavior:** `result` is empty — goroutines modified their local copies.

<details>
<summary>Hint</summary>

Each goroutine gets a copy of the slice header. Appending updates the local copy's `len`, but the caller's `result` variable is unchanged. How do you collect results from goroutines?
</details>

<details>
<summary>Explanation & Fix</summary>

Pass `*[]int` or use channels to collect results. Also note this version has a data race even after fixing the header issue.

```go
package main

import (
    "fmt"
    "sort"
    "sync"
)

func fillRange(start, end int, out chan<- int, wg *sync.WaitGroup) {
    defer wg.Done()
    for i := start; i < end; i++ {
        out <- i
    }
}

func main() {
    ch := make(chan int, 100)
    var wg sync.WaitGroup

    wg.Add(2)
    go fillRange(0, 50, ch, &wg)
    go fillRange(50, 100, ch, &wg)

    go func() {
        wg.Wait()
        close(ch)
    }()

    result := make([]int, 0, 100)
    for v := range ch {
        result = append(result, v)
    }

    sort.Ints(result)
    fmt.Println(len(result)) // 100
    fmt.Println(result[0], result[99]) // 0 99
}
```
</details>

---

## Bug 11 — Slice Literal vs `make` Gotcha 🟡

**Description:** The programmer creates a slice and immediately assigns indices, not realizing the slice has zero length.

```go
package main

import "fmt"

func main() {
    // Programmer wants a slice of 5 zeros, then sets index 3
    s := []int{}
    s[3] = 42 // BUG: panic — index out of range
    fmt.Println(s)
}
```

**Expected behavior:** `s[3]` is set to 42.

**Actual behavior:** Panic — `s` has `len=0, cap=0`.

<details>
<summary>Hint</summary>

`[]int{}` creates a slice with `len=0`. You cannot index into positions that don't exist. How do you create a slice with a specific length?
</details>

<details>
<summary>Explanation & Fix</summary>

There are two distinct patterns:

```go
package main

import "fmt"

func main() {
    // FIX 1: make with length — all elements initialized to zero
    s1 := make([]int, 5)
    s1[3] = 42
    fmt.Println(s1) // [0 0 0 42 0]

    // FIX 2: slice literal with explicit values
    s2 := []int{0, 0, 0, 42, 0}
    fmt.Println(s2) // [0 0 0 42 0]

    // FIX 3: if you want to grow dynamically, use append
    s3 := make([]int, 0, 5)
    for i := 0; i < 4; i++ {
        s3 = append(s3, 0)
    }
    s3 = append(s3, 42) // index 4
    fmt.Println(s3) // [0 0 0 0 42]
}
```

`make([]T, n)` creates a slice of length `n` with all elements zero-initialized. `make([]T, 0, n)` creates a slice with length `0` and capacity `n` — you cannot index into it until you append elements.
</details>
