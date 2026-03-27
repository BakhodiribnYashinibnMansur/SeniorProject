# Go for Loop (C-style) — Find the Bug

Each exercise contains a buggy Go program. Identify the bug and fix it.

**Difficulty ratings:**
- 🟢 Easy — Beginner-level bug, obvious once spotted
- 🟡 Medium — Requires understanding of Go semantics
- 🔴 Hard — Subtle, requires deep knowledge of Go behavior

---

## Bug 1 — Off-by-One: Last Element Skipped 🟢

```go
package main

import "fmt"

func printAll(s []int) {
    for i := 0; i < len(s)-1; i++ {
        fmt.Println(s[i])
    }
}

func main() {
    printAll([]int{1, 2, 3, 4, 5})
    // Expected: 1 2 3 4 5
    // Got: 1 2 3 4
}
```

**Question**: What is the bug? Why is the last element skipped?

<details>
<summary>Solution</summary>

**Bug**: The condition `i < len(s)-1` stops before the last element. For a 5-element slice, it stops at index 3 (skipping index 4).

**Fix**: Change `len(s)-1` to `len(s)`:
```go
for i := 0; i < len(s); i++ {
    fmt.Println(s[i])
}
```

**Why it happens**: `len(s)-1` is the index of the last element. Using it as the loop bound means the last iteration is `i == len(s)-2`, which skips index `len(s)-1`.

</details>

---

## Bug 2 — Off-by-One: Out-of-Bounds Panic 🟢

```go
package main

import "fmt"

func sum(s []int) int {
    total := 0
    for i := 0; i <= len(s); i++ {
        total += s[i]
    }
    return total
}

func main() {
    fmt.Println(sum([]int{1, 2, 3}))
}
```

**Question**: What panic does this produce and why?

<details>
<summary>Solution</summary>

**Bug**: `i <= len(s)` causes the loop to execute when `i == len(s)`. For a 3-element slice, this tries to access `s[3]` — a valid index would be 0, 1, or 2 only.

**Error**: `runtime error: index out of range [3] with length 3`

**Fix**:
```go
for i := 0; i < len(s); i++ {
    total += s[i]
}
```

**Rule**: For 0-indexed access, always use `i < len(s)`, not `i <= len(s)`.

</details>

---

## Bug 3 — Infinite Loop: Missing Post Statement 🟢

```go
package main

import "fmt"

func printEven(n int) {
    for i := 0; i <= n; {
        if i%2 == 0 {
            fmt.Println(i)
        }
        // Forgot to increment when odd
        if i%2 == 0 {
            i++
        }
    }
}

func main() {
    printEven(10)
}
```

**Question**: What happens when `i` is odd? What does this cause?

<details>
<summary>Solution</summary>

**Bug**: When `i` is odd, neither `if` block increments `i`. For example, `i = 1` (odd): the first `if` is false (not printed), the second `if` is false (not incremented) — `i` stays at 1 forever.

**Result**: Infinite loop printing nothing after the first even number.

**Fix**: Always increment:
```go
for i := 0; i <= n; i++ {
    if i%2 == 0 {
        fmt.Println(i)
    }
}
```

</details>

---

## Bug 4 — Break Only Exits Inner Loop 🟡

```go
package main

import "fmt"

func findTarget(matrix [][]int, target int) (int, int) {
    for i := 0; i < len(matrix); i++ {
        for j := 0; j < len(matrix[i]); j++ {
            if matrix[i][j] == target {
                fmt.Printf("Found at (%d,%d)\n", i, j)
                break // Bug: doesn't stop the outer loop
            }
        }
    }
    return -1, -1 // always returns -1,-1
}

func main() {
    m := [][]int{{1, 2}, {3, 4}, {5, 6}}
    findTarget(m, 3)
    // Expected: Found at (1,0), returns (1,0)
    // Got: Found at (1,0), continues searching, returns (-1,-1)
}
```

<details>
<summary>Solution</summary>

**Bug**: `break` only exits the inner `for j` loop. The outer `for i` loop continues, printing "Found at..." and then continuing to search — and always returning `-1, -1`.

**Fix**: Use a labeled break:
```go
func findTarget(matrix [][]int, target int) (int, int) {
outer:
    for i := 0; i < len(matrix); i++ {
        for j := 0; j < len(matrix[i]); j++ {
            if matrix[i][j] == target {
                fmt.Printf("Found at (%d,%d)\n", i, j)
                break outer
            }
        }
    }
    return -1, -1
}
```

Or return directly:
```go
func findTarget(matrix [][]int, target int) (int, int) {
    for i := 0; i < len(matrix); i++ {
        for j := 0; j < len(matrix[i]); j++ {
            if matrix[i][j] == target {
                return i, j  // immediate return — no label needed
            }
        }
    }
    return -1, -1
}
```

</details>

---

## Bug 5 — Goroutine Captures Loop Variable 🟡

```go
package main

import (
    "fmt"
    "sync"
)

func main() {
    var wg sync.WaitGroup
    results := make([]int, 5)
    for i := 0; i < 5; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            results[i] = i * i  // BUG
        }()
    }
    wg.Wait()
    fmt.Println(results)
    // Expected: [0 1 4 9 16]
    // Got: panic or wrong values
}
```

**Question**: What is the bug? What are the two problems this code has?

<details>
<summary>Solution</summary>

**Bugs**:
1. **Data race on `i`**: All goroutines read `i` which is being modified by the main goroutine's loop — undefined behavior.
2. **Wrong index**: By the time goroutines run, `i` may be 5 (final value), causing `results[5]` — out of bounds panic, or all goroutines writing to the same index.

**Fix**: Pass `i` as an argument (evaluates at call time):
```go
for i := 0; i < 5; i++ {
    wg.Add(1)
    go func(idx int) {
        defer wg.Done()
        results[idx] = idx * idx
    }(i)  // i is passed by value here
}
```

Note: In Go 1.22+, each iteration of a `for range` loop gets its own variable, but for C-style `for i := 0; i < n; i++`, you still need to pass by value.

</details>

---

## Bug 6 — Appending to Slice While Iterating 🟡

```go
package main

import "fmt"

func doubleUntilBig(s []int) []int {
    for i := 0; i < len(s); i++ {
        if s[i] < 100 {
            s = append(s, s[i]*2)  // BUG: modifies slice during iteration
        }
    }
    return s
}

func main() {
    result := doubleUntilBig([]int{5, 10, 20})
    fmt.Println(result)
    // This may run for a very long time or forever!
}
```

<details>
<summary>Solution</summary>

**Bug**: The loop uses `len(s)` in the condition, but `s` grows inside the loop body. Every appended element `< 100` causes another append, potentially running forever.

**Fix**: Cache the length before the loop, or use a different approach:
```go
func doubleUntilBig(s []int) []int {
    result := make([]int, 0, len(s))
    for i := 0; i < len(s); i++ {  // only iterates original elements
        result = append(result, s[i])
        if s[i] < 100 {
            result = append(result, s[i]*2)
        }
    }
    return result
}
```

Or cache length:
```go
n := len(s)
for i := 0; i < n; i++ {  // fixed number of iterations
    if s[i] < 100 {
        s = append(s, s[i]*2)
    }
}
```

</details>

---

## Bug 7 — Continue Skips Error Handling 🟡

```go
package main

import (
    "errors"
    "fmt"
)

func processItems(items []int) error {
    var firstErr error
    for i := 0; i < len(items); i++ {
        if items[i] < 0 {
            continue  // BUG: skip setting firstErr
        }
        if err := validate(items[i]); err != nil {
            firstErr = err
        }
    }
    return firstErr
}

func validate(x int) error {
    if x > 100 {
        return errors.New("too large")
    }
    return nil
}

func main() {
    err := processItems([]int{1, -5, 200, 3})
    fmt.Println(err) // should report that -5 is invalid
}
```

**Question**: What critical logic does the `continue` bypass?

<details>
<summary>Solution</summary>

**Bug**: The `continue` bypasses the `validate` call for negative numbers. The requirement is presumably to collect all errors — but negative items are silently skipped instead of being flagged as invalid.

**Fix**: Validate all items, including negative ones:
```go
func processItems(items []int) error {
    var firstErr error
    for i := 0; i < len(items); i++ {
        if items[i] < 0 {
            if firstErr == nil {
                firstErr = fmt.Errorf("item[%d]: negative value %d", i, items[i])
            }
            continue  // skip further processing, but record the error
        }
        if err := validate(items[i]); err != nil {
            if firstErr == nil {
                firstErr = fmt.Errorf("item[%d]: %w", i, err)
            }
        }
    }
    return firstErr
}
```

</details>

---

## Bug 8 — Post Statement Not Executed on Panic Recovery 🔴

```go
package main

import "fmt"

func safeProcess(items []int) {
    for i := 0; i < len(items); i++ {
        func() {
            defer func() {
                if r := recover(); r != nil {
                    fmt.Printf("recovered at i=%d: %v\n", i, r)
                    // Developer intended i++ to happen after recovery
                    // but believes it happens automatically
                }
            }()
            if items[i] == 0 {
                panic("zero found")
            }
            fmt.Printf("processed %d\n", items[i])
        }()
    }
}

func main() {
    safeProcess([]int{1, 0, 2, 3})
}
```

**Question**: Does this code have a bug? What does it actually output?

<details>
<summary>Solution</summary>

**Analysis**: This code is actually correct — the `panic` is contained within the inner anonymous function. The `defer recover()` catches it, the inner function returns normally, and then the `for` loop's post statement `i++` runs as expected.

**Output**:
```
processed 1
recovered at i=1: zero found
processed 2
processed 3
```

The subtle point: `i++` in the `for` post statement runs after the inner function call returns (whether normally or via recover). The loop continues correctly.

**The real bug risk**: If the panic were in the outer scope (not wrapped in an inner function), the post statement would NOT run — the deferred recover would be on the outer function's stack, not the loop's.

```go
// BROKEN: panic in loop body, recover in outer function
func safeProcessBroken(items []int) {
    defer func() {
        if r := recover(); r != nil {
            fmt.Println("recovered:", r)
            // Loop is gone — execution continues AFTER safeProcessBroken returns
        }
    }()
    for i := 0; i < len(items); i++ {
        if items[i] == 0 {
            panic("zero") // post i++ never runs; function unwinds
        }
    }
}
```

</details>

---

## Bug 9 — Integer Overflow in Loop Bound 🔴

```go
package main

import (
    "fmt"
    "math"
)

func countUp() {
    for i := 0; i <= math.MaxInt64; i++ {
        if i%1000000000 == 0 {
            fmt.Printf("i = %d\n", i)
        }
    }
}

func main() {
    countUp()
}
```

**Question**: Will this loop ever terminate? What happens to `i` when it reaches `math.MaxInt64`?

<details>
<summary>Solution</summary>

**Bug**: On a 64-bit system, `i` is of type `int` which is 64-bit. When `i == math.MaxInt64`, `i++` causes signed integer overflow — undefined behavior in C, but in Go it wraps to `math.MinInt64` (a very large negative number). The condition `i <= math.MaxInt64` is then true again (since MinInt64 ≤ MaxInt64), and the loop continues from a very large negative value, effectively running forever.

**This loop never terminates.**

**Fix 1**: Use `for i := 0; i < math.MaxInt64; i++` (but still runs for ~9 quintillion iterations — impractical).

**Fix 2**: Design the algorithm differently. Don't loop to `MaxInt64`. If you need to count up to a large value, use a different approach.

**Fix 3**: For bounds, use `uint64` carefully — but overflow to 0 would still cause an infinite loop.

```go
// Practical fix: add a maximum iteration guard
const maxIter = 1_000_000_000
for i := 0; i < maxIter; i++ {
    // body
}
```

</details>

---

## Bug 10 — Two-Pointer Off-by-One in Palindrome Check 🟡

```go
package main

import "fmt"

func isPalindrome(s string) bool {
    for lo, hi := 0, len(s); lo < hi; lo, hi = lo+1, hi-1 {
        if s[lo] != s[hi] {
            return false
        }
    }
    return true
}

func main() {
    fmt.Println(isPalindrome("racecar")) // expected: true
    fmt.Println(isPalindrome("hello"))   // expected: false
}
```

**Question**: What is the bug? What panic does this produce?

<details>
<summary>Solution</summary>

**Bug**: `hi` is initialized to `len(s)`, not `len(s)-1`. On the first iteration, `s[hi]` accesses `s[len(s)]` which is out of bounds.

**Error**: `runtime error: index out of range [7] with length 7` (for "racecar")

**Fix**: Initialize `hi` to `len(s)-1`:
```go
func isPalindrome(s string) bool {
    for lo, hi := 0, len(s)-1; lo < hi; lo, hi = lo+1, hi-1 {
        if s[lo] != s[hi] {
            return false
        }
    }
    return true
}
```

**Edge case**: Also handle empty string (`len(s) == 0`) — `len(s)-1 == -1`, so `lo < hi` is `0 < -1 == false`, loop doesn't run, returns `true` — correct.

</details>

---

## Bug 11 — Misuse of `i` After Loop Scope 🟡

```go
package main

import "fmt"

func findFirst(s []int, target int) int {
    for i := 0; i < len(s); i++ {
        if s[i] == target {
            break
        }
    }
    return i  // BUG: i is not in scope here
}

func main() {
    fmt.Println(findFirst([]int{1, 2, 3, 4}, 3))
}
```

**Question**: What compile error does this produce?

<details>
<summary>Solution</summary>

**Bug**: `i` is declared in the init statement of the `for` loop and is scoped to the loop block. After the loop, `i` is undefined. The compiler error is: `undefined: i`.

**Fix 1**: Declare `i` outside the loop:
```go
func findFirst(s []int, target int) int {
    i := 0
    for ; i < len(s); i++ {
        if s[i] == target {
            break
        }
    }
    if i == len(s) {
        return -1  // not found
    }
    return i
}
```

**Fix 2**: Return directly from inside the loop:
```go
func findFirst(s []int, target int) int {
    for i := 0; i < len(s); i++ {
        if s[i] == target {
            return i
        }
    }
    return -1
}
```

Fix 2 is the idiomatic Go approach.

</details>

---

## Bug 12 — Batch Processing Skips Last Batch 🔴

```go
package main

import "fmt"

func processBatches(items []string, batchSize int) {
    for start := 0; start+batchSize <= len(items); start += batchSize {
        batch := items[start : start+batchSize]
        fmt.Printf("Processing batch: %v\n", batch)
    }
}

func main() {
    items := []string{"a", "b", "c", "d", "e"}
    processBatches(items, 2)
    // Expected:
    // Processing batch: [a b]
    // Processing batch: [c d]
    // Processing batch: [e]     <- MISSING
}
```

**Question**: Why is the last incomplete batch skipped?

<details>
<summary>Solution</summary>

**Bug**: The condition `start+batchSize <= len(items)` requires a full batch to exist. When `start = 4` and `batchSize = 2`: `4+2 = 6 <= 5` is false — so the loop stops before processing the last item `"e"`.

**Fix**: Use `start < len(items)` as the condition, and compute `end` safely:
```go
func processBatches(items []string, batchSize int) {
    for start := 0; start < len(items); start += batchSize {
        end := start + batchSize
        if end > len(items) {
            end = len(items)
        }
        batch := items[start:end]
        fmt.Printf("Processing batch: %v\n", batch)
    }
}
```

**Output**:
```
Processing batch: [a b]
Processing batch: [c d]
Processing batch: [e]
```

This is a very common production bug in pagination, batch processing, and chunked I/O.

</details>
