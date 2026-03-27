# Slice Capacity and Growth — Interview Q&A

> **Format:** Questions grouped by seniority level, each with a full answer and code example.

---

## Table of Contents

- [Junior Level](#junior-level)
- [Middle Level](#middle-level)
- [Senior Level](#senior-level)
- [Scenario-Based Questions](#scenario-based-questions)
- [FAQ — Common Misconceptions](#faq--common-misconceptions)

---

## Junior Level

### Q1. What is the difference between `len` and `cap` in a slice?

**Answer:**
- `len(s)` — the number of elements currently in the slice (accessible via index).
- `cap(s)` — the total number of elements the slice can hold before a new allocation is needed.

`len <= cap` is always true. Elements at index `len` through `cap-1` are allocated but not accessible via normal indexing.

```go
package main

import "fmt"

func main() {
    s := make([]int, 3, 7)
    fmt.Println(len(s)) // 3 — three elements accessible
    fmt.Println(cap(s)) // 7 — seven slots allocated

    // s[3] would panic: index out of range
    // but append can use those 4 extra slots without reallocating
    s = append(s, 10)
    fmt.Println(len(s), cap(s)) // 4, 7 — no reallocation
}
```

---

### Q2. What is a nil slice and how does it differ from an empty slice?

**Answer:**

| | `nil` slice | Empty slice |
|---|---|---|
| Declaration | `var s []int` | `s := []int{}` or `make([]int, 0)` |
| `s == nil` | `true` | `false` |
| `len(s)` | `0` | `0` |
| `cap(s)` | `0` | `0` |
| Safe to `append` | Yes | Yes |
| Safe to `range` | Yes | Yes |

```go
package main

import "fmt"

func main() {
    var nilSlice []int
    emptySlice := []int{}

    fmt.Println(nilSlice == nil)   // true
    fmt.Println(emptySlice == nil) // false
    fmt.Println(len(nilSlice), len(emptySlice)) // 0 0

    // Both are safe to append to
    nilSlice = append(nilSlice, 1)
    emptySlice = append(emptySlice, 1)
    fmt.Println(nilSlice, emptySlice) // [1] [1]
}
```

**Tip:** Prefer returning `nil` slices over `[]int{}` from functions when the result is empty — `nil` is idiomatic in Go.

---

### Q3. What happens when you `append` to a slice that is already at full capacity?

**Answer:**

Go allocates a new, larger backing array, copies all existing elements into it, appends the new element, and returns a new slice header pointing to the new array. The original backing array is abandoned and eventually garbage-collected.

```go
package main

import "fmt"

func main() {
    s := make([]int, 3, 3) // len=3, cap=3 — full
    s[0], s[1], s[2] = 1, 2, 3

    fmt.Printf("Before: ptr=%p  cap=%d\n", &s[0], cap(s))

    s = append(s, 4) // triggers reallocation

    fmt.Printf("After:  ptr=%p  cap=%d\n", &s[0], cap(s))
    // ptr changed — new backing array was allocated
    // cap is now 6 (doubled)
}
```

---

### Q4. How do you create a slice with a pre-allocated capacity?

**Answer:**

Use `make([]T, length, capacity)`:

```go
package main

import "fmt"

func main() {
    // Create a slice with 0 elements but room for 100
    s := make([]int, 0, 100)
    fmt.Println(len(s), cap(s)) // 0 100

    for i := 0; i < 100; i++ {
        s = append(s, i) // no reallocation occurs
    }
    fmt.Println(len(s), cap(s)) // 100 100
}
```

Use this when you know the maximum size in advance. It eliminates repeated reallocations during `append`.

---

### Q5. What is the `cap()` built-in function?

**Answer:**

`cap(s)` returns the capacity of slice `s` — the number of elements the slice can hold without reallocating. It works on slices, arrays (returns the array length), and channels (returns the buffer size).

```go
package main

import "fmt"

func main() {
    s := make([]string, 2, 5)
    fmt.Println(cap(s)) // 5

    arr := [4]int{1, 2, 3, 4}
    fmt.Println(cap(arr)) // 4 (same as len for arrays)

    ch := make(chan int, 10)
    fmt.Println(cap(ch)) // 10
}
```

---

### Q6. Can you access elements beyond `len` but within `cap`?

**Answer:**

No — accessing `s[i]` where `i >= len(s)` causes a runtime panic, even if `i < cap(s)`. The capacity is an implementation detail; only `len` determines valid indices.

```go
package main

func main() {
    s := make([]int, 3, 10)
    _ = s[2] // OK: index 2 < len 3
    _ = s[5] // PANIC: index 5 >= len 3
}
```

You can "extend" the slice up to `cap` using a reslice: `s = s[:5]` is safe if `cap(s) >= 5`.

---

## Middle Level

### Q7. How does Go's slice growth strategy work in Go 1.18+?

**Answer:**

Go uses a **two-phase growth strategy** based on a threshold of 256:

- **Small slices (cap < 256):** Capacity doubles.
- **Large slices (cap >= 256):** Capacity grows by approximately 1.25x per step, using the formula `newcap += (newcap + 3*256) / 4`.
- **Batch append:** If the requested new length exceeds twice the old capacity, the capacity jumps directly to the new length.

After the mathematical growth, the result is rounded up to the nearest memory allocator size class.

```go
package main

import "fmt"

func main() {
    s := make([]int, 0)
    prev := 0
    for i := 0; i < 600; i++ {
        s = append(s, i)
        if cap(s) != prev {
            fmt.Printf("len=%3d  cap=%3d\n", len(s), cap(s))
            prev = cap(s)
        }
    }
    // Small: 1,2,4,8,16,32,64,128,256
    // Large: 320, 416, 544, 704, ... (roughly 1.3x, converging to 1.25x)
}
```

---

### Q8. What is the full slice expression and why is it useful?

**Answer:**

The full slice expression `a[low:high:max]` creates a slice where:
- `ptr = &a[low]`
- `len = high - low`
- `cap = max - low`

It is useful to **prevent a subslice from growing into and overwriting elements beyond `high`** in the original backing array.

```go
package main

import "fmt"

func main() {
    original := []int{1, 2, 3, 4, 5, 6}

    // Without full slice expression:
    unsafe := original[1:3]          // cap=5, can grow into original[3..5]
    unsafe = append(unsafe, 99)      // overwrites original[3]!
    fmt.Println(original)            // [1 2 3 99 5 6] — surprise!

    // Reset
    original = []int{1, 2, 3, 4, 5, 6}

    // With full slice expression:
    safe := original[1:3:3]          // cap=2, cannot grow into original[3..5]
    safe = append(safe, 99)          // forces reallocation
    fmt.Println(original)            // [1 2 3 4 5 6] — unchanged!
}
```

---

### Q9. How do you avoid memory leaks when working with large slices?

**Answer:**

A subslice keeps the entire backing array alive in memory, even if the subslice only uses a few elements.

```go
package main

import "fmt"

func leaky() []byte {
    huge := make([]byte, 1_000_000)
    // ... fill huge ...
    return huge[:10] // LEAK: 1M array stays alive
}

func safe() []byte {
    huge := make([]byte, 1_000_000)
    // ... fill huge ...
    result := make([]byte, 10)
    copy(result, huge[:10])
    return result // only 10 bytes kept alive
}

func main() {
    leak := leaky()
    noLeak := safe()
    fmt.Println(len(leak), len(noLeak)) // both 10
    // but leak keeps a 1MB array alive; noLeak does not
}
```

The idiomatic one-liner to copy and break the reference:

```go
result := append([]byte(nil), huge[:10]...)
```

---

### Q10. What is the difference between `copy` and `append` for duplicating a slice?

**Answer:**

Both can duplicate a slice, but they behave differently:

```go
package main

import "fmt"

func main() {
    original := []int{1, 2, 3, 4, 5}

    // Using copy (requires pre-allocation)
    copy1 := make([]int, len(original))
    copy(copy1, original)

    // Using append (idiomatic one-liner)
    copy2 := append([]int(nil), original...)

    // Verify independence
    copy1[0] = 99
    copy2[1] = 88
    fmt.Println(original) // [1 2 3 4 5] — unchanged
    fmt.Println(copy1)    // [99 2 3 4 5]
    fmt.Println(copy2)    // [1 88 3 4 5]
}
```

- `copy` is explicit and slightly more efficient for known sizes.
- `append([]T(nil), src...)` is a common idiom but may over-allocate due to size-class rounding.

---

### Q11. What happens to the original slice after `append` causes a reallocation?

**Answer:**

The original slice header is **unchanged** — it still points to the old backing array with the old `len` and `cap`. Only the **returned** slice header reflects the new allocation.

```go
package main

import "fmt"

func main() {
    original := make([]int, 3, 3)
    original[0], original[1], original[2] = 1, 2, 3

    // append returns a NEW slice header
    newSlice := append(original, 4)

    original[0] = 99 // modifies old backing array
    fmt.Println(original)  // [99 2 3]
    fmt.Println(newSlice)  // [1 2 3 4] — has its own backing array
}
```

This is a common source of bugs: assuming `append` modifies in place.

---

### Q12. How do you shrink a slice's capacity?

**Answer:**

Capacity cannot shrink automatically. You must explicitly create a new slice:

```go
package main

import "fmt"

func shrink(s []int, newLen int) []int {
    // Method 1: copy to new allocation
    result := make([]int, newLen)
    copy(result, s[:newLen])
    return result
}

func main() {
    s := make([]int, 10, 100)
    for i := range s {
        s[i] = i
    }
    fmt.Println(len(s), cap(s)) // 10 100

    trimmed := shrink(s, 5)
    fmt.Println(len(trimmed), cap(trimmed)) // 5 5
    // The 100-element backing array is now eligible for GC
}
```

---

## Senior Level

### Q13. Explain the performance implications of slice growth in a hot path.

**Answer:**

In a hot path (tight loop, high-frequency function), slice growth causes:
1. **Heap allocation** via `mallocgc` — expensive, bypasses the stack.
2. **memmove** — O(n) copy of existing data.
3. **GC pressure** — old arrays become garbage, triggering more frequent GC cycles.
4. **Write barrier overhead** — for slices containing pointers.

Mitigation strategies:

```go
package main

import (
    "fmt"
    "sync"
)

// Strategy 1: Pre-allocate with known size
func processKnown(inputs []string) []int {
    results := make([]int, 0, len(inputs)) // no reallocation
    for _, s := range inputs {
        results = append(results, len(s))
    }
    return results
}

// Strategy 2: Use sync.Pool for reusable slices
var slicePool = sync.Pool{
    New: func() interface{} {
        s := make([]int, 0, 256)
        return &s
    },
}

func processPooled(inputs []string) []int {
    sp := slicePool.Get().(*[]int)
    s := (*sp)[:0] // reset len, keep cap
    defer func() {
        *sp = s
        slicePool.Put(sp)
    }()
    for _, input := range inputs {
        s = append(s, len(input))
    }
    result := make([]int, len(s))
    copy(result, s)
    return result
}

func main() {
    data := []string{"hello", "world", "go"}
    fmt.Println(processKnown(data))
    fmt.Println(processPooled(data))
}
```

---

### Q14. What changed in Go 1.18 regarding slice growth?

**Answer:**

Before Go 1.18, the growth threshold was **1024** and the formula was:
- Double below 1024.
- Grow by 25% (`newcap += newcap / 4`) above 1024.

Go 1.18 changed to:
- Threshold reduced to **256**.
- Smoother transition formula: `newcap += (newcap + 3*256) / 4`.

**Why the change?** The old 1.25x formula caused a sudden, discontinuous jump in growth rate at exactly 1024 elements. The new formula creates a smooth curve from 2x (for very small slices) converging to 1.25x (for very large slices), reducing memory waste for medium-sized slices (256–1024 elements).

```go
// Old behavior (pre-1.18): grew from 512 to 1024 to 1280 to 1600...
// New behavior (1.18+):   grew from 512 to 848 to 1024 (after rounding)...
// The transition is smoother and wastes less memory
```

---

### Q15. How does the garbage collector interact with slices?

**Answer:**

The GC scans the slice header to find the backing array pointer. Behavior depends on element type:

- **`[]int`, `[]float64`, etc.** — elements contain no pointers. The GC only follows the one pointer in the slice header to the backing array, then does **no further scanning** of the array contents.
- **`[]string`, `[]*T`, `[]interface{}`** — elements contain pointers. The GC must **scan every element** in the backing array.

```go
// Cheap for GC: no pointer scanning of elements
bigInts := make([]int64, 1_000_000)

// Expensive for GC: must scan all 1M pointers
bigPtrs := make([]*MyStruct, 1_000_000)
```

**Memory leak via subslice:** The GC cannot collect a backing array as long as any slice header points into it — even if the subslice only accesses 2 of 1,000,000 elements.

---

### Q16. How would you implement a stack using a slice efficiently?

**Answer:**

```go
package main

import "fmt"

type Stack[T any] struct {
    data []T
}

func NewStack[T any](initialCap int) *Stack[T] {
    return &Stack[T]{data: make([]T, 0, initialCap)}
}

func (s *Stack[T]) Push(v T) {
    s.data = append(s.data, v)
}

func (s *Stack[T]) Pop() (T, bool) {
    if len(s.data) == 0 {
        var zero T
        return zero, false
    }
    n := len(s.data) - 1
    v := s.data[n]
    // Nil out the element to avoid memory leaks (for pointer types)
    var zero T
    s.data[n] = zero
    s.data = s.data[:n]
    return v, true
}

func (s *Stack[T]) Len() int { return len(s.data) }

func main() {
    st := NewStack[int](16)
    st.Push(1)
    st.Push(2)
    st.Push(3)

    for st.Len() > 0 {
        v, _ := st.Pop()
        fmt.Println(v) // 3, 2, 1
    }
}
```

Key points: pre-allocate with expected capacity; nil out popped pointer elements to prevent memory leaks.

---

## Scenario-Based Questions

### Q17. You have a function that builds a result slice inside a loop. It runs 10,000 times per second and is showing up in heap profiles. How do you optimize it?

**Answer:**

```go
package main

import "sync"

// BEFORE: allocates on every call
func buildResultSlow(data []int) []int {
    result := []int{} // allocation #1
    for _, v := range data {
        if v > 0 {
            result = append(result, v*2) // possible allocations #2..N
        }
    }
    return result
}

// AFTER: pre-allocate with capacity hint
func buildResultFast(data []int) []int {
    result := make([]int, 0, len(data)) // one allocation, correct capacity
    for _, v := range data {
        if v > 0 {
            result = append(result, v*2) // never reallocates
        }
    }
    return result
}

// BEST: pool for extreme hot paths
var resultPool = sync.Pool{
    New: func() interface{} {
        s := make([]int, 0, 64)
        return &s
    },
}

func buildResultPooled(data []int) []int {
    sp := resultPool.Get().(*[]int)
    result := (*sp)[:0]
    for _, v := range data {
        if v > 0 {
            result = append(result, v*2)
        }
    }
    out := make([]int, len(result))
    copy(out, result)
    *sp = result
    resultPool.Put(sp)
    return out
}
```

---

### Q18. A colleague claims "slices in Go are passed by reference." Is this correct?

**Answer:**

**Partially correct, but misleading.** The slice header `{ptr, len, cap}` is passed **by value**. This means:

- **Modifying elements** via the passed slice: visible to the caller (same backing array).
- **Changing `len` or `cap`** (via `append`): NOT visible to the caller (different header copy).

```go
package main

import "fmt"

func modifyElement(s []int) {
    s[0] = 99 // visible to caller — same backing array
}

func appendElement(s []int) {
    s = append(s, 100) // NOT visible to caller — s is a copy of the header
}

func appendElementFixed(s *[]int) {
    *s = append(*s, 100) // visible — we pass the header pointer
}

func main() {
    s := []int{1, 2, 3}

    modifyElement(s)
    fmt.Println(s) // [99 2 3] — element modified

    appendElement(s)
    fmt.Println(s) // [99 2 3] — NO change, append wasn't visible

    appendElementFixed(&s)
    fmt.Println(s) // [99 2 3 100] — visible via pointer
}
```

---

### Q19. What is the output of this code?

```go
package main

import "fmt"

func main() {
    a := []int{1, 2, 3, 4, 5}
    b := a[1:4]
    b = append(b, 10)
    fmt.Println(a)
    fmt.Println(b)
}
```

**Answer:**

```
[1 2 3 4 10]
[2 3 4 10]
```

Explanation: `b = a[1:4]` creates a subslice with `len=3, cap=4`. Since `len(b) < cap(b)`, `append(b, 10)` does NOT reallocate — it writes `10` into `a[4]` (the element at index 3 of `b`). Both `a` and `b` share the backing array.

---

### Q20. How would you implement a sliding window that avoids re-allocating for each window?

**Answer:**

```go
package main

import "fmt"

func slidingWindowSum(data []int, windowSize int) []int {
    if len(data) < windowSize {
        return nil
    }
    resultLen := len(data) - windowSize + 1
    results := make([]int, 0, resultLen) // pre-allocate exact size

    // Compute first window
    sum := 0
    for i := 0; i < windowSize; i++ {
        sum += data[i]
    }
    results = append(results, sum)

    // Slide the window
    for i := windowSize; i < len(data); i++ {
        sum += data[i] - data[i-windowSize]
        results = append(results, sum)
    }
    return results
}

func main() {
    data := []int{1, 3, 5, 2, 8, 4, 6}
    fmt.Println(slidingWindowSum(data, 3))
    // [9 10 15 14 18] — no reallocations
}
```

---

## FAQ — Common Misconceptions

### FAQ1. "I appended to a slice, so the original slice changed."

**Not necessarily.** If `append` reallocated (cap was full), the original is unaffected. If it didn't reallocate (cap had room), elements in the shared range may change.

```go
s1 := make([]int, 3, 6) // has room
s2 := s1[:3]
s2 = append(s2, 99) // no realloc — writes to s1's backing array
fmt.Println(s1[:4]) // [0 0 0 99] — s1 sees the change via reslice
```

### FAQ2. "Capacity is always a power of 2."

**False.** Capacity is rounded to allocator size classes, not necessarily powers of 2. Size classes include 48, 80, 96, 112, etc.

### FAQ3. "A slice and its subslice always share memory."

**False after append.** They share memory until an `append` to the subslice causes reallocation (if the subslice is at capacity or uses a full slice expression).

### FAQ4. "Using `s = s[:0]` frees memory."

**False.** `s = s[:0]` resets `len` to 0 but keeps the backing array allocated. It is useful for reusing the buffer, but does not release memory to the GC.

```go
s := make([]int, 1000)
s = s[:0]
fmt.Println(len(s), cap(s)) // 0 1000 — memory still held
```

### FAQ5. "Pre-allocating is always better."

**Not always.** If you pre-allocate `make([]T, 0, 1000)` but only append 5 elements, you waste 995 slots. Use pre-allocation when you have a good estimate of the final size.
