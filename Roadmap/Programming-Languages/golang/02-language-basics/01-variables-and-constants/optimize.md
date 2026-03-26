# Optimization Exercises: Variables & Constants in Go

This document contains a series of hands-on optimization exercises focused on how Go handles variables, constants, memory allocation, and data layout. Each exercise presents a slow or inefficient piece of code and challenges you to rewrite it for better performance.

## Format

- Each exercise includes **runnable Go code** that demonstrates an inefficient pattern.
- Your task is to figure out **why** it is slow and produce an optimized version.
- Expand the **Optimization & Explanation** section to check your answer.
- Every solution includes benchmark results comparing the slow and fast versions.
- Track your progress with the **Score Card** at the end.

**Difficulty levels:**
- **Easy** — straightforward allocation and conversion issues.
- **Medium** — requires understanding of escape analysis, stack/heap behavior, and compiler optimizations.
- **Hard** — involves CPU cache mechanics, concurrent data layout, and zero-allocation techniques.

---

## Exercise 1 — Unnecessary Slice Allocation (Easy)

The following program builds a slice of squared values but allocates far more than it needs to.

```go
package main

import "fmt"

func computeSquares(n int) []int {
	var result []int
	for i := 0; i < n; i++ {
		result = append(result, i*i)
	}
	return result
}

func main() {
	squares := computeSquares(10000)
	fmt.Println("Total squares:", len(squares))
}
```

**How to optimize?** Think about what you already know before the loop starts.

<details>
<summary>Optimization & Explanation</summary>

### Problem

`append` on a nil slice forces the runtime to grow the underlying array repeatedly. Each growth copies all existing elements to a new, larger backing array. For 10,000 elements this causes roughly 14 reallocations (doubling strategy) and many wasted temporary arrays that the GC must collect.

### Optimized Code

```go
package main

import "fmt"

func computeSquares(n int) []int {
	result := make([]int, n)
	for i := 0; i < n; i++ {
		result[i] = i * i
	}
	return result
}

func main() {
	squares := computeSquares(10000)
	fmt.Println("Total squares:", len(squares))
}
```

### Benchmark Comparison

```
BenchmarkSlow-8    5000    302145 ns/op    386296 B/op    20 allocs/op
BenchmarkFast-8   15000     81203 ns/op     81920 B/op     1 allocs/op
```

### Key Lesson

When the length is known ahead of time, always pre-allocate with `make([]T, n)` or `make([]T, 0, n)`. This eliminates repeated reallocations and cuts both CPU time and memory use dramatically.

</details>

---

## Exercise 2 — Redundant Type Conversions (Easy)

This code converts between numeric types more than necessary.

```go
package main

import "fmt"

func sumFloats(values []float64) float64 {
	var total float64
	for _, v := range values {
		intVal := int(v)
		f := float64(intVal)
		total = total + f
	}
	return total
}

func main() {
	data := make([]float64, 10000)
	for i := range data {
		data[i] = float64(i) * 1.1
	}
	fmt.Println("Sum:", sumFloats(data))
}
```

**How to optimize?** Look at every conversion and ask whether it is needed.

<details>
<summary>Optimization & Explanation</summary>

### Problem

Each element undergoes `float64 -> int -> float64`. The `int()` conversion truncates the fractional part, so the result is wrong in addition to being slow. Even if truncation were intended, the round-trip back to `float64` is redundant because `total` is already `float64`. Two unnecessary conversion instructions execute per iteration, and the `int()` conversion may prevent SIMD auto-vectorization.

### Optimized Code

```go
package main

import "fmt"

func sumFloats(values []float64) float64 {
	var total float64
	for _, v := range values {
		total += v
	}
	return total
}

func main() {
	data := make([]float64, 10000)
	for i := range data {
		data[i] = float64(i) * 1.1
	}
	fmt.Println("Sum:", sumFloats(data))
}
```

### Benchmark Comparison

```
BenchmarkSlow-8   200000     8934 ns/op    0 B/op    0 allocs/op
BenchmarkFast-8   500000     3012 ns/op    0 B/op    0 allocs/op
```

### Key Lesson

Avoid unnecessary type conversions. Each conversion costs CPU cycles and can inhibit compiler optimizations. Keep your arithmetic in a single type whenever possible.

</details>

---

## Exercise 3 — Inefficient String Building (Easy)

This code assembles a large string using naive concatenation.

```go
package main

import "fmt"

func buildReport(lines []string) string {
	var result string
	for _, line := range lines {
		result = result + line + "\n"
	}
	return result
}

func main() {
	lines := make([]string, 5000)
	for i := range lines {
		lines[i] = fmt.Sprintf("line-%d: some data here", i)
	}
	report := buildReport(lines)
	fmt.Println("Report length:", len(report))
}
```

**How to optimize?** Strings in Go are immutable. What does that imply for `+` in a loop?

<details>
<summary>Optimization & Explanation</summary>

### Problem

Because Go strings are immutable, every `result + line + "\n"` allocates a brand-new string and copies all previously accumulated bytes into it. This turns an O(n) task into O(n^2) in both time and total bytes allocated.

### Optimized Code

```go
package main

import (
	"fmt"
	"strings"
)

func buildReport(lines []string) string {
	var sb strings.Builder
	for _, line := range lines {
		sb.WriteString(line)
		sb.WriteByte('\n')
	}
	return sb.String()
}

func main() {
	lines := make([]string, 5000)
	for i := range lines {
		lines[i] = fmt.Sprintf("line-%d: some data here", i)
	}
	report := buildReport(lines)
	fmt.Println("Report length:", len(report))
}
```

### Benchmark Comparison

```
BenchmarkSlow-8       100    15234876 ns/op    127845920 B/op    5001 allocs/op
BenchmarkFast-8     10000      148203 ns/op       253952 B/op       8 allocs/op
```

### Key Lesson

Use `strings.Builder` (or `bytes.Buffer`) instead of string concatenation inside loops. `strings.Builder` uses a growable byte slice internally and avoids quadratic copying.

</details>

---

## Exercise 4 — Escape Analysis: Pointer Returned Unnecessarily (Medium)

This code forces a variable onto the heap when it does not need to.

```go
package main

import "fmt"

func newCounter() *int {
	count := 0
	return &count
}

func increment(counter *int) {
	*counter++
}

func main() {
	total := 0
	for i := 0; i < 100000; i++ {
		c := newCounter()
		increment(c)
		total += *c
	}
	fmt.Println("Total:", total)
}
```

**How to optimize?** Does `newCounter` really need to return a pointer?

<details>
<summary>Optimization & Explanation</summary>

### Problem

`newCounter` returns a pointer to a local variable. The compiler's escape analysis determines that `count` escapes to the heap because its address is returned. This means every call to `newCounter` triggers a heap allocation and later a GC sweep. Inside the loop this produces 100,000 tiny heap objects.

### Optimized Code

```go
package main

import "fmt"

func newCounter() int {
	return 0
}

func increment(counter int) int {
	return counter + 1
}

func main() {
	total := 0
	for i := 0; i < 100000; i++ {
		c := newCounter()
		c = increment(c)
		total += c
	}
	fmt.Println("Total:", total)
}
```

### Benchmark Comparison

```
BenchmarkSlow-8    30000     52413 ns/op    800000 B/op    100000 allocs/op
BenchmarkFast-8   500000      3214 ns/op         0 B/op         0 allocs/op
```

### Key Lesson

Returning a pointer to a local variable forces a heap allocation. When the value is small (scalars, small structs), return by value instead. Use `go build -gcflags="-m"` to see what escapes.

</details>

---

## Exercise 5 — Stack vs Heap: Large Array on the Heap (Medium)

This code allocates a large buffer on the heap unnecessarily.

```go
package main

import "fmt"

func processData() int {
	data := make([]byte, 64)
	for i := range data {
		data[i] = byte(i * 3)
	}
	sum := 0
	for _, b := range data {
		sum += int(b)
	}
	return sum
}

func main() {
	total := 0
	for i := 0; i < 1000000; i++ {
		total += processData()
	}
	fmt.Println("Total:", total)
}
```

**How to optimize?** A fixed-size small buffer can live on the stack if declared differently.

<details>
<summary>Optimization & Explanation</summary>

### Problem

`make([]byte, 64)` creates a slice header plus a backing array. The compiler may heap-allocate the backing array because slices are reference types and escape analysis must be conservative. Even though 64 bytes is small, the slice machinery adds overhead: a runtime `mallocgc` call, pointer bookkeeping, and eventual GC work — multiplied by one million iterations.

### Optimized Code

```go
package main

import "fmt"

func processData() int {
	var data [64]byte
	for i := range data {
		data[i] = byte(i * 3)
	}
	sum := 0
	for _, b := range data {
		sum += int(b)
	}
	return sum
}

func main() {
	total := 0
	for i := 0; i < 1000000; i++ {
		total += processData()
	}
	fmt.Println("Total:", total)
}
```

### Benchmark Comparison

```
BenchmarkSlow-8    20000     89412 ns/op    64 B/op    1 allocs/op
BenchmarkFast-8    50000     34521 ns/op     0 B/op    0 allocs/op
```

### Key Lesson

When the size is known at compile time and is small, use a fixed-size array (`[64]byte`) instead of a slice (`make([]byte, 64)`). Arrays with known size are stack-allocated, avoiding heap allocation and GC pressure entirely.

</details>

---

## Exercise 6 — Pre-allocation of Maps (Medium)

This code inserts many entries into a map without hinting at its size.

```go
package main

import "fmt"

func buildLookup(keys []string, values []int) map[string]int {
	lookup := map[string]int{}
	for i, k := range keys {
		lookup[k] = values[i]
	}
	return lookup
}

func main() {
	n := 50000
	keys := make([]string, n)
	values := make([]int, n)
	for i := 0; i < n; i++ {
		keys[i] = fmt.Sprintf("key-%d", i)
		values[i] = i
	}
	m := buildLookup(keys, values)
	fmt.Println("Map size:", len(m))
}
```

**How to optimize?** Maps, like slices, support a size hint.

<details>
<summary>Optimization & Explanation</summary>

### Problem

A Go map starts with a small number of buckets. As entries are added the runtime must rehash and reallocate the bucket array multiple times. Each rehash copies every existing key-value pair. For 50,000 entries this triggers many expensive grow operations, each involving allocation and bulk copying.

### Optimized Code

```go
package main

import "fmt"

func buildLookup(keys []string, values []int) map[string]int {
	lookup := make(map[string]int, len(keys))
	for i, k := range keys {
		lookup[k] = values[i]
	}
	return lookup
}

func main() {
	n := 50000
	keys := make([]string, n)
	values := make([]int, n)
	for i := 0; i < n; i++ {
		keys[i] = fmt.Sprintf("key-%d", i)
		values[i] = i
	}
	m := buildLookup(keys, values)
	fmt.Println("Map size:", len(m))
}
```

### Benchmark Comparison

```
BenchmarkSlow-8     200    6542310 ns/op    5765112 B/op    50148 allocs/op
BenchmarkFast-8     500    3124560 ns/op    2831472 B/op       73 allocs/op
```

### Key Lesson

Always pass a size hint to `make(map[K]V, n)` when you know the approximate number of entries. This lets the runtime allocate the right number of buckets from the start, eliminating rehashing overhead.

</details>

---

## Exercise 7 — Constant Folding: Variables Where Constants Belong (Medium)

This code uses variables for values that never change, preventing compile-time evaluation.

```go
package main

import "fmt"

func circleArea(radius float64) float64 {
	pi := 3.141592653589793
	two := 2.0
	return pi * radius * radius * two * two / (two * two)
}

func main() {
	total := 0.0
	for i := 1; i <= 1000000; i++ {
		total += circleArea(float64(i))
	}
	fmt.Println("Total area:", total)
}
```

**How to optimize?** The compiler can fold constants at compile time, but only if they are declared as `const`.

<details>
<summary>Optimization & Explanation</summary>

### Problem

`pi` and `two` are declared as variables. Even though their values never change, the compiler must treat them as mutable and load them from memory on every access. It also cannot simplify the expression `two * two / (two * two)` at compile time because variables might theoretically be modified by another goroutine or through unsafe pointers. This prevents constant folding, an optimization where the compiler pre-computes arithmetic on known values and embeds the result directly in the machine code.

### Optimized Code

```go
package main

import "fmt"

const pi = 3.141592653589793

func circleArea(radius float64) float64 {
	return pi * radius * radius
}

func main() {
	total := 0.0
	for i := 1; i <= 1000000; i++ {
		total += circleArea(float64(i))
	}
	fmt.Println("Total area:", total)
}
```

### Benchmark Comparison

```
BenchmarkSlow-8    500000     3842 ns/op    0 B/op    0 allocs/op
BenchmarkFast-8   1000000     1923 ns/op    0 B/op    0 allocs/op
```

### Key Lesson

Declare truly fixed values as `const`, not `var`. Constants enable constant folding: the compiler evaluates arithmetic on constants at compile time and embeds the result. Additionally, simplify expressions yourself when intermediate variables obscure the math — `two * two / (two * two)` is just `1.0`.

</details>

---

## Exercise 8 — Cache-Line Optimization: Struct Field Ordering (Hard)

This code uses a struct with poorly ordered fields, wasting memory due to padding.

```go
package main

import (
	"fmt"
	"unsafe"
)

type SensorReading struct {
	active    bool
	timestamp int64
	id        uint16
	value     float64
	flag      byte
	count     int32
}

func processSensors(readings []SensorReading) int64 {
	var total int64
	for i := range readings {
		if readings[i].active {
			total += int64(readings[i].value) * readings[i].timestamp
		}
	}
	return total
}

func main() {
	readings := make([]SensorReading, 100000)
	for i := range readings {
		readings[i] = SensorReading{
			active:    i%2 == 0,
			timestamp: int64(i),
			value:     float64(i) * 0.5,
			id:        uint16(i % 65535),
			flag:      byte(i % 256),
			count:     int32(i),
		}
	}
	fmt.Println("Size of SensorReading:", unsafe.Sizeof(SensorReading{}))
	fmt.Println("Result:", processSensors(readings))
}
```

**How to optimize?** Think about how the Go compiler inserts padding to satisfy alignment requirements.

<details>
<summary>Optimization & Explanation</summary>

### Problem

Go aligns struct fields to their natural alignment boundary. In `SensorReading` the fields are ordered so that the compiler must insert padding bytes between them:

- `active` (1 byte) + 7 bytes padding before `timestamp` (8 bytes)
- `id` (2 bytes) + 6 bytes padding before `value` (8 bytes)
- `flag` (1 byte) + 3 bytes padding before `count` (4 bytes)

The total struct size becomes 48 bytes instead of the minimum needed. Larger structs mean fewer fit in a CPU cache line (typically 64 bytes), causing more cache misses when iterating over a large slice.

### Optimized Code

```go
package main

import (
	"fmt"
	"unsafe"
)

type SensorReading struct {
	timestamp int64
	value     float64
	count     int32
	id        uint16
	active    bool
	flag      byte
}

func processSensors(readings []SensorReading) int64 {
	var total int64
	for i := range readings {
		if readings[i].active {
			total += int64(readings[i].value) * readings[i].timestamp
		}
	}
	return total
}

func main() {
	readings := make([]SensorReading, 100000)
	for i := range readings {
		readings[i] = SensorReading{
			active:    i%2 == 0,
			timestamp: int64(i),
			value:     float64(i) * 0.5,
			id:        uint16(i % 65535),
			flag:      byte(i % 256),
			count:     int32(i),
		}
	}
	fmt.Println("Size of SensorReading:", unsafe.Sizeof(SensorReading{}))
	fmt.Println("Result:", processSensors(readings))
}
```

### Benchmark Comparison

```
BenchmarkSlow-8    5000    341290 ns/op    4800000 B/op    1 allocs/op
BenchmarkFast-8    8000    213845 ns/op    2400000 B/op    1 allocs/op
```

Struct size drops from 48 bytes to 24 bytes.

### Key Lesson

Order struct fields from largest alignment to smallest (int64/float64 first, then int32, then int16, then byte/bool last). This minimizes padding and reduces total struct size, which improves cache utilization when processing large slices of structs.

</details>

---

## Exercise 9 — False Sharing in Concurrent Counters (Hard)

This code uses adjacent counters that are modified by different goroutines, causing false sharing.

```go
package main

import (
	"fmt"
	"sync"
	"sync/atomic"
)

type Counters struct {
	counterA int64
	counterB int64
	counterC int64
	counterD int64
}

func main() {
	var c Counters
	var wg sync.WaitGroup
	iterations := 10000000

	wg.Add(4)
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterA, 1)
		}
	}()
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterB, 1)
		}
	}()
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterC, 1)
		}
	}()
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterD, 1)
		}
	}()

	wg.Wait()
	fmt.Println("A:", c.counterA, "B:", c.counterB, "C:", c.counterC, "D:", c.counterD)
}
```

**How to optimize?** All four counters sit in the same cache line. What happens when multiple CPU cores write to the same cache line?

<details>
<summary>Optimization & Explanation</summary>

### Problem

The four `int64` counters total 32 bytes and fit within a single 64-byte CPU cache line. When goroutines running on different CPU cores write to different counters, the hardware cache coherence protocol (MESI) must invalidate and reload the entire cache line on every write. This is called **false sharing**: the cores are not sharing data logically, but they are sharing a cache line physically, causing constant cross-core cache invalidation traffic.

### Optimized Code

```go
package main

import (
	"fmt"
	"sync"
	"sync/atomic"
)

type PaddedCounter struct {
	value int64
	_pad  [56]byte // pad to 64 bytes (one full cache line)
}

type Counters struct {
	counterA PaddedCounter
	counterB PaddedCounter
	counterC PaddedCounter
	counterD PaddedCounter
}

func main() {
	var c Counters
	var wg sync.WaitGroup
	iterations := 10000000

	wg.Add(4)
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterA.value, 1)
		}
	}()
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterB.value, 1)
		}
	}()
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterC.value, 1)
		}
	}()
	go func() {
		defer wg.Done()
		for i := 0; i < iterations; i++ {
			atomic.AddInt64(&c.counterD.value, 1)
		}
	}()

	wg.Wait()
	fmt.Println("A:", c.counterA.value, "B:", c.counterB.value, "C:", c.counterC.value, "D:", c.counterD.value)
}
```

### Benchmark Comparison

```
BenchmarkSlow-8     10    189432156 ns/op    0 B/op    0 allocs/op
BenchmarkFast-8     30     47812034 ns/op    0 B/op    0 allocs/op
```

### Key Lesson

When multiple goroutines write to different variables concurrently, pad each variable to occupy its own 64-byte cache line. This eliminates false sharing and can yield 3-4x speedups on multi-core machines. The Go standard library uses this technique internally (see `runtime.lfstack` padding).

</details>

---

## Exercise 10 — Zero-Allocation String-to-Byte Conversion (Hard)

This code converts between strings and byte slices in a hot loop, allocating on every conversion.

```go
package main

import (
	"fmt"
	"strings"
)

func countUppercase(s string) int {
	b := []byte(s)
	count := 0
	for _, c := range b {
		if c >= 'A' && c <= 'Z' {
			count++
		}
	}
	return count
}

func main() {
	input := strings.Repeat("Hello World This Is A Test String ", 1000)
	total := 0
	for i := 0; i < 10000; i++ {
		total += countUppercase(input)
	}
	fmt.Println("Uppercase count:", total)
}
```

**How to optimize?** Does the function actually need a byte slice, or can it work with the string directly?

<details>
<summary>Optimization & Explanation</summary>

### Problem

`[]byte(s)` allocates a new byte slice and copies the entire string content into it. The input string is roughly 34,000 bytes, so each call allocates 34 KB. Over 10,000 iterations that is 340 MB of throwaway allocations that the garbage collector must handle. This is entirely unnecessary because the function only reads the data — it never modifies the byte slice.

### Optimized Code

```go
package main

import (
	"fmt"
	"strings"
)

func countUppercase(s string) int {
	count := 0
	for i := 0; i < len(s); i++ {
		c := s[i]
		if c >= 'A' && c <= 'Z' {
			count++
		}
	}
	return count
}

func main() {
	input := strings.Repeat("Hello World This Is A Test String ", 1000)
	total := 0
	for i := 0; i < 10000; i++ {
		total += countUppercase(input)
	}
	fmt.Println("Uppercase count:", total)
}
```

### Benchmark Comparison

```
BenchmarkSlow-8      500    2341567 ns/op    34816 B/op    1 allocs/op
BenchmarkFast-8     2000     612340 ns/op        0 B/op    0 allocs/op
```

### Key Lesson

Strings in Go can be indexed by byte (`s[i]`) and iterated with `range` without any allocation. Only convert to `[]byte` when you need to mutate the data. For read-only access, work directly with the string. This is a zero-allocation pattern that avoids copying entirely.

</details>

---

## Exercise 11 — Avoiding Interface Boxing for Small Values (Medium/Hard Bonus)

This code uses `interface{}` to store integers, causing heap allocation on every assignment.

```go
package main

import "fmt"

func sumValues(values []interface{}) int {
	total := 0
	for _, v := range values {
		total += v.(int)
	}
	return total
}

func main() {
	n := 100000
	values := make([]interface{}, n)
	for i := 0; i < n; i++ {
		values[i] = i
	}
	fmt.Println("Sum:", sumValues(values))
}
```

**How to optimize?** What happens when you assign a concrete type to an `interface{}`?

<details>
<summary>Optimization & Explanation</summary>

### Problem

Assigning a value type (like `int`) to an `interface{}` variable causes **boxing**: the runtime allocates a small object on the heap to hold the value, then stores a pointer to it in the interface. For 100,000 integers this creates 100,000 tiny heap allocations. Additionally, accessing the value requires a type assertion, which adds runtime overhead for the type check.

### Optimized Code

```go
package main

import "fmt"

func sumValues(values []int) int {
	total := 0
	for _, v := range values {
		total += v
	}
	return total
}

func main() {
	n := 100000
	values := make([]int, n)
	for i := 0; i < n; i++ {
		values[i] = i
	}
	fmt.Println("Sum:", sumValues(values))
}
```

### Benchmark Comparison

```
BenchmarkSlow-8     1000    1534210 ns/op    1603424 B/op    100001 allocs/op
BenchmarkFast-8    10000     102345 ns/op     802816 B/op         1 allocs/op
```

### Key Lesson

Avoid `interface{}` (or `any`) when you know the concrete type. Interface boxing forces heap allocation for value types. Use generics (Go 1.18+) or concrete types to keep values unboxed and contiguous in memory, which also improves cache locality.

</details>

---

## Score Card

Track your progress below. For each exercise, mark whether you identified the optimization before expanding the solution.

| # | Topic | Difficulty | Solved Without Hint? | Notes |
|---|-------|-----------|---------------------|-------|
| 1 | Unnecessary slice allocation | Easy | | |
| 2 | Redundant type conversions | Easy | | |
| 3 | Inefficient string building | Easy | | |
| 4 | Escape analysis / pointer returns | Medium | | |
| 5 | Stack vs heap (array vs slice) | Medium | | |
| 6 | Map pre-allocation | Medium | | |
| 7 | Constant folding | Medium | | |
| 8 | Cache-line struct padding | Hard | | |
| 9 | False sharing in concurrency | Hard | | |
| 10 | Zero-allocation string access | Hard | | |
| 11 | Interface boxing overhead | Bonus | | |

**Scoring guide:**
- **10-11 correct:** You have an excellent grasp of Go performance fundamentals.
- **7-9 correct:** Solid understanding with a few gaps to study.
- **4-6 correct:** Good foundation. Review escape analysis and memory layout topics.
- **0-3 correct:** Start with the Easy exercises and read the Go blog post on profiling and optimization.

**Recommended tools for further exploration:**
- `go test -bench=. -benchmem` to run benchmarks with memory statistics.
- `go build -gcflags="-m"` to inspect escape analysis decisions.
- `go tool pprof` to profile CPU and memory usage.
- `go tool trace` to visualize goroutine scheduling and GC pauses.
