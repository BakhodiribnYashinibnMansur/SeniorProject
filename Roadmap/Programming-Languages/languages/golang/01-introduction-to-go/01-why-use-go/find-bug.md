# Why Use Go — Find the Bug

> **Practice finding and fixing bugs in Go code related to Go's core features and common beginner/intermediate pitfalls.**

---

## How to Use

1. Read the buggy code carefully
2. Try to find the bug **without** looking at the hint
3. Write the fix yourself before checking the solution
4. Understand **why** the bug happens — not just how to fix it

### Difficulty Levels

| Level | Description |
|:-----:|:-----------|
| 🟢 | **Easy** — Common beginner Go mistakes, syntax-level bugs |
| 🟡 | **Medium** — Logic errors, subtle Go behavior, concurrency issues |
| 🔴 | **Hard** — Race conditions, memory issues, Go compiler/runtime edge cases |

---

## Bug 1: The Missing Error Check 🟢

**What the code should do:** Read a file and print its contents.

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    file, _ := os.Open("config.txt")
    data := make([]byte, 100)
    n, _ := file.Read(data)
    fmt.Println(string(data[:n]))
}
```

**Expected output:**
```
(contents of config.txt)
```

**Actual output:**
```
panic: runtime error: invalid memory address or nil pointer dereference
```

<details>
<summary>Hint</summary>
What happens to `file` when `os.Open` fails? What is the zero value of a pointer?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The error from `os.Open` is ignored using `_`. If the file does not exist, `file` is `nil`, and calling `file.Read()` on a nil pointer causes a panic.
**Why it happens:** Go returns errors as values, not exceptions. Using `_` silently discards the error.
**Impact:** Runtime panic — program crashes.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "os"
)

func main() {
    file, err := os.Open("config.txt")
    if err != nil {
        fmt.Println("Error opening file:", err)
        return
    }
    defer file.Close()

    data := make([]byte, 100)
    n, err := file.Read(data)
    if err != nil {
        fmt.Println("Error reading file:", err)
        return
    }
    fmt.Println(string(data[:n]))
}
```

**What changed:** Added error checking for both `os.Open` and `file.Read`, and added `defer file.Close()` for proper cleanup.

</details>

---

## Bug 2: The Unused Import 🟢

**What the code should do:** Print the current time.

```go
package main

import (
    "fmt"
    "os"
    "time"
)

func main() {
    now := time.Now()
    fmt.Println("Current time:", now)
}
```

**Expected output:**
```
Current time: 2024-01-01 12:00:00 +0000 UTC
```

**Actual output:**
```
./main.go:5:2: "os" imported and not used
```

<details>
<summary>Hint</summary>
Go does not allow unused imports. Look at the import list.
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The `"os"` package is imported but never used. Go treats unused imports as compilation errors.
**Why it happens:** Go enforces this to keep code clean and prevent dead imports from slowing down compilation.
**Impact:** Code does not compile.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    now := time.Now()
    fmt.Println("Current time:", now)
}
```

**What changed:** Removed the unused `"os"` import.

</details>

---

## Bug 3: Short Variable Declaration Scope 🟢

**What the code should do:** Read a value from a function and print it.

```go
package main

import "fmt"

func getValue() (int, error) {
    return 42, nil
}

func main() {
    var result int

    if true {
        result, err := getValue()
        if err != nil {
            fmt.Println("Error:", err)
            return
        }
        fmt.Println("Inside if:", result)
    }

    fmt.Println("Outside if:", result)
}
```

**Expected output:**
```
Inside if: 42
Outside if: 42
```

**Actual output:**
```
Inside if: 42
Outside if: 0
```

<details>
<summary>Hint</summary>
Look carefully at `:=` inside the `if` block. Does it assign to the outer `result` or create a new one?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The `:=` inside the `if` block creates a **new** `result` variable that shadows the outer `result`. The outer `result` remains at its zero value (0).
**Why it happens:** In Go, `:=` creates new variables. Since `err` is a new variable, `:=` also creates a new `result` in the inner scope.
**Impact:** The outer `result` is never assigned — logic error.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import "fmt"

func getValue() (int, error) {
    return 42, nil
}

func main() {
    var result int

    if true {
        var err error
        result, err = getValue() // Use = not :=
        if err != nil {
            fmt.Println("Error:", err)
            return
        }
        fmt.Println("Inside if:", result)
    }

    fmt.Println("Outside if:", result)
}
```

**What changed:** Used `=` instead of `:=` to assign to the outer `result` variable. Declared `err` separately with `var`.

</details>

---

## Bug 4: Goroutine Loop Variable Capture 🟡

**What the code should do:** Print numbers 0 through 4 using goroutines.

```go
package main

import (
    "fmt"
    "sync"
)

func main() {
    var wg sync.WaitGroup

    for i := 0; i < 5; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            fmt.Println(i)
        }()
    }

    wg.Wait()
}
```

**Expected output:**
```
0
1
2
3
4
(in any order)
```

**Actual output (Go < 1.22):**
```
5
5
5
5
5
```

<details>
<summary>Hint</summary>
The goroutine closure captures the variable `i` by reference, not by value. By the time goroutines execute, the loop has finished.
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** In Go versions before 1.22, the closure captures the loop variable `i` by reference. By the time the goroutines execute, the loop has completed and `i` equals 5.
**Why it happens:** Go closures capture variables from the enclosing scope. The loop variable `i` is a single variable that is mutated each iteration.
**Impact:** All goroutines print the same value (5) instead of 0-4. Note: Go 1.22+ fixed this with per-iteration loop variable scoping.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "sync"
)

func main() {
    var wg sync.WaitGroup

    for i := 0; i < 5; i++ {
        wg.Add(1)
        go func(n int) { // Pass i as parameter
            defer wg.Done()
            fmt.Println(n)
        }(i) // Capture current value of i
    }

    wg.Wait()
}
```

**What changed:** Pass `i` as a function argument to create a copy for each goroutine.

</details>

---

## Bug 5: Nil Map Write 🟡

**What the code should do:** Count word frequencies in a sentence.

```go
package main

import (
    "fmt"
    "strings"
)

func wordCount(sentence string) map[string]int {
    var counts map[string]int // Declared but not initialized

    words := strings.Fields(sentence)
    for _, word := range words {
        counts[word]++ // This will panic!
    }

    return counts
}

func main() {
    result := wordCount("go is great go is fast go is simple")
    fmt.Println(result)
}
```

**Expected output:**
```
map[fast:1 go:3 great:1 is:3 simple:1]
```

**Actual output:**
```
panic: assignment to entry in nil map
```

<details>
<summary>Hint</summary>
What is the zero value of a map in Go? Can you write to a nil map?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The map `counts` is declared but never initialized. Its zero value is `nil`. Writing to a nil map causes a panic.
**Why it happens:** In Go, `var m map[K]V` creates a nil map. You can read from a nil map (returns zero value), but writing panics. You must use `make(map[K]V)` to initialize it.
**Impact:** Runtime panic — program crashes.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "strings"
)

func wordCount(sentence string) map[string]int {
    counts := make(map[string]int) // Initialize the map!

    words := strings.Fields(sentence)
    for _, word := range words {
        counts[word]++
    }

    return counts
}

func main() {
    result := wordCount("go is great go is fast go is simple")
    fmt.Println(result)
}
```

**What changed:** Used `make(map[string]int)` to initialize the map before writing to it.

</details>

---

## Bug 6: Nil Interface Trap 🟡

**What the code should do:** Return nil error when processing succeeds.

```go
package main

import "fmt"

type AppError struct {
    Code    int
    Message string
}

func (e *AppError) Error() string {
    return fmt.Sprintf("error %d: %s", e.Code, e.Message)
}

func process(input string) error {
    var appErr *AppError

    if input == "" {
        appErr = &AppError{Code: 400, Message: "empty input"}
    }

    return appErr // Bug: returns non-nil interface even when appErr is nil!
}

func main() {
    err := process("valid input")
    if err != nil {
        fmt.Println("ERROR:", err) // This executes even though processing succeeded!
    } else {
        fmt.Println("Success!")
    }
}
```

**Expected output:**
```
Success!
```

**Actual output:**
```
ERROR: <nil>
```

<details>
<summary>Hint</summary>
A Go interface is a (type, value) pair. What is the difference between a nil interface and an interface holding a nil pointer?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** The function returns `appErr` (type `*AppError`, value `nil`). When assigned to the `error` interface, this becomes `(*AppError, nil)` — which is NOT a nil interface. A nil interface requires both type and value to be nil: `(nil, nil)`.
**Why it happens:** Go's interface type is a pair of (type descriptor, data pointer). When you return a typed nil, the type descriptor is non-nil.
**Impact:** The error check `err != nil` is always true, even when no error occurred.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import "fmt"

type AppError struct {
    Code    int
    Message string
}

func (e *AppError) Error() string {
    return fmt.Sprintf("error %d: %s", e.Code, e.Message)
}

func process(input string) error {
    if input == "" {
        return &AppError{Code: 400, Message: "empty input"}
    }
    return nil // Return untyped nil directly
}

func main() {
    err := process("valid input")
    if err != nil {
        fmt.Println("ERROR:", err)
    } else {
        fmt.Println("Success!")
    }
}
```

**What changed:** Return `nil` directly instead of a typed nil pointer. Never return a typed nil pointer as an interface.

</details>

---

## Bug 7: Deferred Call Argument Evaluation 🟡

**What the code should do:** Log the elapsed time of an operation.

```go
package main

import (
    "fmt"
    "time"
)

func doWork() {
    start := time.Now()
    defer fmt.Printf("Elapsed: %v\n", time.Since(start)) // Bug!

    // Simulate work
    time.Sleep(2 * time.Second)
    fmt.Println("Work done")
}

func main() {
    doWork()
}
```

**Expected output:**
```
Work done
Elapsed: 2.000123456s
```

**Actual output:**
```
Work done
Elapsed: 0s (or very close to 0)
```

<details>
<summary>Hint</summary>
When are the arguments to a deferred function call evaluated — at the `defer` statement or when the function returns?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** Deferred function arguments are evaluated **immediately** when the `defer` statement executes, not when the deferred function runs. `time.Since(start)` is evaluated at the time of `defer`, which is right after `start` is set — so the elapsed time is nearly 0.
**Why it happens:** Go spec: "Each time a defer statement executes, the function value and parameters to the call are evaluated as usual and saved anew."
**Impact:** The logged elapsed time is always ~0, not the actual execution time.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "time"
)

func doWork() {
    start := time.Now()
    defer func() {
        // Wrapped in closure — time.Since(start) evaluated when closure runs
        fmt.Printf("Elapsed: %v\n", time.Since(start))
    }()

    // Simulate work
    time.Sleep(2 * time.Second)
    fmt.Println("Work done")
}

func main() {
    doWork()
}
```

**What changed:** Wrapped the deferred call in a closure. The closure captures `start` by reference, and `time.Since(start)` is evaluated when the closure actually executes (at function return).

</details>

---

## Bug 8: Data Race in Concurrent Counter 🔴

**What the code should do:** Safely increment a counter from multiple goroutines.

```go
package main

import (
    "fmt"
    "sync"
)

type Counter struct {
    value int
}

func (c *Counter) Increment() {
    c.value++ // Not thread-safe!
}

func (c *Counter) Value() int {
    return c.value
}

func main() {
    counter := &Counter{}
    var wg sync.WaitGroup

    for i := 0; i < 10000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            counter.Increment()
        }()
    }

    wg.Wait()
    fmt.Println("Count:", counter.Value())
    // Expected: 10000, Actual: less than 10000 (non-deterministic)
}
```

**Expected output:**
```
Count: 10000
```

**Actual output:**
```
Count: 9847 (or some number less than 10000, non-deterministic)
```

<details>
<summary>Hint</summary>
Run with `go run -race main.go`. What does the race detector report? What happens when two goroutines read and write `c.value` simultaneously?
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** Multiple goroutines concurrently increment `c.value` without synchronization. The `++` operation is not atomic — it is read, increment, write. Two goroutines can read the same value, both increment it, and write back the same result, losing one increment.
**Why it happens:** Go's memory model requires explicit synchronization for concurrent access to shared variables.
**Impact:** Data race — incorrect count, non-deterministic behavior. Detected by `go run -race`.
**Go spec reference:** "If a variable is accessed from multiple goroutines, the accesses must be synchronized."

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "sync"
    "sync/atomic"
)

type Counter struct {
    value int64
}

func (c *Counter) Increment() {
    atomic.AddInt64(&c.value, 1) // Atomic operation — thread-safe
}

func (c *Counter) Value() int64 {
    return atomic.LoadInt64(&c.value)
}

func main() {
    counter := &Counter{}
    var wg sync.WaitGroup

    for i := 0; i < 10000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            counter.Increment()
        }()
    }

    wg.Wait()
    fmt.Println("Count:", counter.Value()) // Always 10000
}
```

**What changed:** Used `sync/atomic.AddInt64` for thread-safe atomic increment.
**Alternative fix:** Use `sync.Mutex` to protect the increment operation.

</details>

---

## Bug 9: Goroutine Leak from Unbuffered Channel 🔴

**What the code should do:** Fetch data with a timeout.

```go
package main

import (
    "fmt"
    "time"
)

func fetchData() string {
    time.Sleep(5 * time.Second) // Simulate slow service
    return "data"
}

func fetchWithTimeout(timeout time.Duration) (string, error) {
    ch := make(chan string) // Unbuffered channel

    go func() {
        result := fetchData()
        ch <- result // This goroutine is STUCK if nobody reads from ch!
    }()

    select {
    case result := <-ch:
        return result, nil
    case <-time.After(timeout):
        return "", fmt.Errorf("timeout after %v", timeout)
    }
}

func main() {
    result, err := fetchWithTimeout(1 * time.Second)
    if err != nil {
        fmt.Println("Error:", err)
        // The goroutine is still running and will leak!
    } else {
        fmt.Println("Result:", result)
    }

    // In production, this goroutine leak accumulates over time
    time.Sleep(100 * time.Millisecond)
    fmt.Println("Program exiting (goroutine still stuck in background)")
}
```

**Expected output:**
```
Error: timeout after 1s
(goroutine cleaned up properly)
```

**Actual output:**
```
Error: timeout after 1s
Program exiting (goroutine still stuck in background)
```

<details>
<summary>Hint</summary>
When the timeout fires, `fetchWithTimeout` returns. But the goroutine is still trying to send on `ch`. Since nobody will ever read from `ch`, the goroutine is stuck forever. Use a buffered channel.
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** When the timeout fires, `fetchWithTimeout` returns without reading from `ch`. The goroutine that runs `fetchData()` will eventually try to send the result on the unbuffered channel, but no one is listening. The goroutine is blocked forever — a goroutine leak.
**Why it happens:** Unbuffered channels block both sender and receiver. If the receiver gives up (timeout), the sender is stuck.
**Impact:** Each leaked goroutine consumes ~2KB+ of memory. Over time in production, this accumulates and can cause OOM.
**How to detect:** Monitor `runtime.NumGoroutine()` over time — an upward trend indicates leaks.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import (
    "fmt"
    "time"
)

func fetchData() string {
    time.Sleep(5 * time.Second)
    return "data"
}

func fetchWithTimeout(timeout time.Duration) (string, error) {
    ch := make(chan string, 1) // Buffered channel — sender won't block!

    go func() {
        result := fetchData()
        ch <- result // Even if nobody reads, the goroutine can finish
    }()

    select {
    case result := <-ch:
        return result, nil
    case <-time.After(timeout):
        return "", fmt.Errorf("timeout after %v", timeout)
    }
}

func main() {
    result, err := fetchWithTimeout(1 * time.Second)
    if err != nil {
        fmt.Println("Error:", err)
    } else {
        fmt.Println("Result:", result)
    }
}
```

**What changed:** Changed `make(chan string)` to `make(chan string, 1)`. The buffer allows the goroutine to send its result even if nobody reads it, preventing the goroutine from being stuck.

</details>

---

## Bug 10: Slice Append Side Effect 🔴

**What the code should do:** Create two different variations of a base configuration.

```go
package main

import "fmt"

func main() {
    base := make([]string, 0, 10) // Capacity 10!
    base = append(base, "host=localhost")
    base = append(base, "port=5432")

    // Create two configurations from the same base
    configA := append(base, "db=users")
    configB := append(base, "db=orders")

    fmt.Println("Config A:", configA)
    fmt.Println("Config B:", configB)
}
```

**Expected output:**
```
Config A: [host=localhost port=5432 db=users]
Config B: [host=localhost port=5432 db=orders]
```

**Actual output:**
```
Config A: [host=localhost port=5432 db=orders]
Config B: [host=localhost port=5432 db=orders]
```

<details>
<summary>Hint</summary>
Look at the capacity of `base`. When `append` does not need to grow the underlying array, it reuses the same memory. Both `configA` and `configB` share the same underlying array.
</details>

<details>
<summary>Bug Explanation</summary>

**Bug:** `base` has capacity 10 but length 2. `append(base, "db=users")` writes to index 2 of the underlying array without creating a new one (capacity is sufficient). Then `append(base, "db=orders")` overwrites the same index 2. Both `configA` and `configB` point to the same underlying array.
**Why it happens:** Go's `append` reuses the underlying array if there is enough capacity. Only when capacity is exceeded does it allocate a new array.
**Impact:** Unexpected data corruption — both configs end up with the same value. This is a subtle bug that often appears in production code.

</details>

<details>
<summary>Fixed Code</summary>

```go
package main

import "fmt"

func main() {
    base := []string{"host=localhost", "port=5432"}

    // Create independent copies before appending
    configA := make([]string, len(base), len(base)+1)
    copy(configA, base)
    configA = append(configA, "db=users")

    configB := make([]string, len(base), len(base)+1)
    copy(configB, base)
    configB = append(configB, "db=orders")

    fmt.Println("Config A:", configA)
    fmt.Println("Config B:", configB)
}
```

**What changed:** Created explicit copies of the base slice before appending. This ensures each config has its own underlying array.
**Alternative fix:** Use `append(base[:len(base):len(base)], "db=users")` — the three-index slice expression limits the capacity, forcing a new allocation.

</details>

---

## Score Card

| Bug | Difficulty | Found without hint? | Understood why? | Fixed correctly? |
|:---:|:---------:|:-------------------:|:---------------:|:----------------:|
| 1 | 🟢 | ☐ | ☐ | ☐ |
| 2 | 🟢 | ☐ | ☐ | ☐ |
| 3 | 🟢 | ☐ | ☐ | ☐ |
| 4 | 🟡 | ☐ | ☐ | ☐ |
| 5 | 🟡 | ☐ | ☐ | ☐ |
| 6 | 🟡 | ☐ | ☐ | ☐ |
| 7 | 🟡 | ☐ | ☐ | ☐ |
| 8 | 🔴 | ☐ | ☐ | ☐ |
| 9 | 🔴 | ☐ | ☐ | ☐ |
| 10 | 🔴 | ☐ | ☐ | ☐ |

### Rating:
- **10/10 without hints** — Senior-level Go debugging skills
- **7-9/10** — Solid Go middle-level understanding
- **4-6/10** — Good junior, keep practicing Go
- **< 4/10** — Review the topic fundamentals first
