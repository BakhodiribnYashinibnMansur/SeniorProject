# break Statement — Tasks

## Task 1: Linear Search (Easy)

Search a slice for the first occurrence of a target value. Return its index, or -1 if not found. Use `break` to stop early.

```go
package main

import "fmt"

// TODO: implement LinearSearch using break
func LinearSearch(s []int, target int) int {
    // Iterate with for range
    // When found: set result and break
    // Return -1 if not found
}

func main() {
    fmt.Println(LinearSearch([]int{3, 7, 2, 9, 1}, 9))  // 3
    fmt.Println(LinearSearch([]int{3, 7, 2, 9, 1}, 5))  // -1
    fmt.Println(LinearSearch([]int{5}, 5))               // 0
}
```

**Requirements:**
- Use `break` to exit as soon as the target is found
- Return the index (not the value)

---

## Task 2: Read Until Sentinel (Easy)

Process values from a slice until you encounter a sentinel value (-1).

```go
package main

import "fmt"

// TODO: implement ProcessUntilSentinel
// Process all values until -1 is encountered; return sum of processed values
func ProcessUntilSentinel(data []int) int {
    sum := 0
    // Use for range and break when sentinel found
    return sum
}

func main() {
    fmt.Println(ProcessUntilSentinel([]int{3, 5, 7, -1, 2, 4})) // 15 (3+5+7)
    fmt.Println(ProcessUntilSentinel([]int{1, 2, 3}))            // 6 (no sentinel)
    fmt.Println(ProcessUntilSentinel([]int{-1, 5, 6}))           // 0 (immediate)
}
```

---

## Task 3: break in switch-for (Common Pitfall) (Easy)

Fix the buggy code below so it correctly stops the loop when `i == 3`.

```go
package main

import "fmt"

// TODO: fix this function — break should exit the for loop, not just the switch
func stopAtThree() {
    for i := 0; i < 6; i++ {
        switch i {
        case 3:
            fmt.Println("Stopping at 3")
            break // BUG: this only exits the switch!
        }
        fmt.Println("i =", i)
    }
    // Current output: i=0,1,2, Stopping at 3, i=3, i=4, i=5
    // Expected: i=0,1,2, Stopping at 3
}

func main() {
    stopAtThree()
}
```

**Hint:** Use a labeled break targeting the for loop.

---

## Task 4: Find First Duplicate (Medium)

Given a slice, find the first element that appears more than once. Return it and `true`, or 0 and `false` if no duplicates.

```go
package main

import "fmt"

// TODO: implement FirstDuplicate
// Use a map to track seen values
// Break when a duplicate is found
func FirstDuplicate(s []int) (int, bool) {
    seen := make(map[int]bool)
    // your code here
}

func main() {
    fmt.Println(FirstDuplicate([]int{1, 2, 3, 2, 5}))    // 2, true
    fmt.Println(FirstDuplicate([]int{1, 2, 3, 4, 5}))    // 0, false
    fmt.Println(FirstDuplicate([]int{7, 7, 8, 9}))       // 7, true
}
```

---

## Task 5: Word Count with Limit (Medium)

Count words in a string, but stop counting after reaching a maximum count limit.

```go
package main

import (
    "fmt"
    "strings"
)

// TODO: implement CountWordsUpTo
// Count words in s, but stop after maxWords words are counted
// Return the count and whether the limit was reached
func CountWordsUpTo(s string, maxWords int) (count int, limitReached bool) {
    words := strings.Fields(s)
    // Use for range and break when count reaches maxWords
    return
}

func main() {
    c, lim := CountWordsUpTo("the quick brown fox jumps over the lazy dog", 5)
    fmt.Println(c, lim) // 5, true

    c, lim = CountWordsUpTo("hello world", 10)
    fmt.Println(c, lim) // 2, false
}
```

---

## Task 6: Matrix Search with Labeled break (Medium)

Find a value in a 2D slice using a labeled break to exit both loops.

```go
package main

import "fmt"

// TODO: implement FindInMatrix using labeled break
// Return (row, col, true) if found, or (-1, -1, false) if not found
// Must use a labeled break to exit both loops when found
func FindInMatrix(matrix [][]int, target int) (row, col int, found bool) {
    // Use labeled break (not return) to exit the outer loop
    // your code here
}

func main() {
    m := [][]int{
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9},
    }
    fmt.Println(FindInMatrix(m, 5)) // 1, 1, true
    fmt.Println(FindInMatrix(m, 0)) // -1, -1, false
}
```

**Constraint:** Must use a labeled `break` (not `return`) to exit the outer loop when found.

---

## Task 7: Retry with break (Medium)

Implement a retry mechanism that stops on success or after max attempts.

```go
package main

import (
    "fmt"
    "math/rand"
)

// TODO: implement Retry
// Call fn up to maxAttempts times
// Break immediately on success (nil error)
// Return last error if all attempts fail, nil if success
func Retry(maxAttempts int, fn func() error) error {
    var lastErr error
    // Use for i := 0; i < maxAttempts; i++ with break on success
    return lastErr
}

func main() {
    attempts := 0
    err := Retry(5, func() error {
        attempts++
        if attempts >= 3 {
            return nil // succeed on 3rd attempt
        }
        return fmt.Errorf("attempt %d failed", attempts)
    })
    fmt.Printf("Result: err=%v, attempts=%d\n", err, attempts)
    // Result: err=<nil>, attempts=3
}
```

---

## Task 8: Pipeline with Early Termination (Hard)

Build a pipeline that processes items but stops on the first error using `break`.

```go
package main

import "fmt"

type ProcessResult struct {
    Input  int
    Output int
    Error  error
}

// TODO: implement ProcessPipeline
// For each item, call transform(item)
// If transform returns an error, add it to results and break
// Return all results up to and including the first error
func ProcessPipeline(items []int, transform func(int) (int, error)) []ProcessResult {
    var results []ProcessResult
    // Use for range with break on error
    return results
}

func main() {
    results := ProcessPipeline(
        []int{1, 2, 3, -4, 5},
        func(n int) (int, error) {
            if n < 0 { return 0, fmt.Errorf("negative: %d", n) }
            return n * 2, nil
        },
    )
    for _, r := range results {
        fmt.Printf("in=%d out=%d err=%v\n", r.Input, r.Output, r.Error)
    }
    // in=1 out=2 err=<nil>
    // in=2 out=4 err=<nil>
    // in=3 out=6 err=<nil>
    // in=-4 out=0 err=negative: -4
}
```

---

## Task 9: Break from Channel Range with Context (Hard)

Implement a function that reads from a channel and stops when either the channel closes, a count limit is reached, or the context is cancelled.

```go
package main

import (
    "context"
    "fmt"
)

// TODO: implement ReadUntil
// Read from ch until:
// 1. Channel is closed
// 2. maxItems items have been read (use break)
// 3. ctx is cancelled
// Return collected items
func ReadUntil(ctx context.Context, ch <-chan int, maxItems int) []int {
    var items []int
    // Use for { select { ... } } with labeled break
    return items
}

func main() {
    ctx := context.Background()
    ch := make(chan int, 10)
    for i := 1; i <= 8; i++ { ch <- i }
    close(ch)

    items := ReadUntil(ctx, ch, 5)
    fmt.Println(items) // [1 2 3 4 5]
}
```

---

## Task 10: Batch Processor with Timeout (Hard)

Process items in batches. Break from the current batch when a timeout occurs or the batch is full.

```go
package main

import (
    "fmt"
    "time"
)

// TODO: implement BatchProcess
// Collect items into batches of size batchSize
// Process each complete batch
// If timeout occurs before batch is full, process partial batch and stop
func BatchProcess(items []int, batchSize int, timeout time.Duration) {
    var batch []int
    start := time.Now()

    for _, item := range items {
        if time.Since(start) >= timeout {
            fmt.Println("Timeout! Processing partial batch:", batch)
            break
        }
        batch = append(batch, item)
        if len(batch) == batchSize {
            fmt.Println("Processing full batch:", batch)
            batch = nil // reset batch
        }
    }
    if len(batch) > 0 && time.Since(start) < timeout {
        fmt.Println("Processing final batch:", batch)
    }
}

func main() {
    items := make([]int, 100)
    for i := range items { items[i] = i + 1 }
    BatchProcess(items, 5, 1*time.Second)
}
```

---

## Task 11: Labeled break Search in 3D Slice (Expert)

Search for a value in a 3D slice using labeled break statements.

```go
package main

import "fmt"

// TODO: implement Search3D
// Use labeled break to exit all three loops when found
// Must use labeled break (not return) for the outer two loops
func Search3D(cube [][][]int, target int) (x, y, z int, found bool) {
    x, y, z = -1, -1, -1

    // Label the outermost for loop
    // Use labeled break when target is found
    // your code here

    return
}

func main() {
    cube := [][][]int{
        {{1, 2}, {3, 4}},
        {{5, 6}, {7, 8}},
        {{9, 10}, {11, 12}},
    }
    fmt.Println(Search3D(cube, 7))  // 1 1 0 true
    fmt.Println(Search3D(cube, 99)) // -1 -1 -1 false
}
```

---

## Task 12: Event Loop with Break Conditions (Expert)

Implement a simple event loop that breaks under multiple conditions.

```go
package main

import (
    "context"
    "fmt"
    "time"
)

type Event struct {
    Type    string
    Payload interface{}
}

// TODO: implement EventLoop
// Process events until:
// 1. "shutdown" event received (break)
// 2. maxEvents events processed (break)
// 3. ctx cancelled (return)
// Return number of events processed and reason for stopping
func EventLoop(ctx context.Context, events <-chan Event, maxEvents int) (int, string) {
    count := 0
    reason := ""
    // Use for { select { ... } } with labeled break
    _ = reason
    return count, reason
}

func main() {
    ctx := context.Background()
    events := make(chan Event, 10)

    go func() {
        for i := 0; i < 5; i++ {
            events <- Event{"data", i}
        }
        events <- Event{"shutdown", nil}
        events <- Event{"data", 999}
    }()

    time.Sleep(10 * time.Millisecond)
    n, reason := EventLoop(ctx, events, 10)
    fmt.Printf("Processed %d events, stopped: %s\n", n, reason)
    // Processed 5 events, stopped: shutdown
}
```

---

## Solutions Reference

| Task | Key break Pattern |
|---|---|
| 1 | Plain `break` in `for range` after setting result |
| 2 | `break` when sentinel value found |
| 3 | Labeled `break` targeting `for` loop from inside `switch` |
| 4 | `break` in `for range` after duplicate detection |
| 5 | `break` when count reaches limit |
| 6 | Labeled `break` (not return) for 2D search |
| 7 | `break` on success in retry loop |
| 8 | `break` on first error in pipeline |
| 9 | Labeled `break` in `for { select { ... } }` |
| 10 | `break` on timeout in range loop |
| 11 | Labeled break across 3 nested loops |
| 12 | Labeled break in event loop with multiple exit conditions |
