# History of Go — Optimize the Code

> **Practice optimizing slow, inefficient, or resource-heavy Go code related to History of Go.**
> Each exercise contains working but suboptimal code using old-style patterns from earlier Go versions — your job is to modernize it, making it faster, leaner, or more efficient using idioms introduced across Go's evolution (Go 1.0 through Go 1.22+).

---

## How to Use

1. Read the slow code and understand what it does
2. Identify the performance bottleneck
3. Write your optimized version
4. Compare with the solution and benchmark results
5. Understand **why** the optimization works

### Difficulty Levels

| Level | Focus |
|:-----:|:------|
| 🟢 | **Easy** — Obvious inefficiencies, simple fixes |
| 🟡 | **Medium** — Algorithmic improvements, allocation reduction |
| 🔴 | **Hard** — Cache-aware code, zero-allocation patterns, runtime-level optimizations |

### Optimization Categories

| Category | Icon | Description |
|:--------:|:----:|:-----------|
| **Memory** | 📦 | Reduce allocations, reuse buffers, avoid copies |
| **CPU** | ⚡ | Better algorithms, fewer operations, cache efficiency |
| **Concurrency** | 🔄 | Better parallelism, reduce contention, avoid locks |
| **I/O** | 💾 | Batch operations, buffering, connection reuse |

---

## Exercise 1: Deprecated ioutil.ReadFile to os.ReadFile 🟢 💾

**What the code does:** Reads all files from a directory, processes their contents, and counts total bytes read.

**The problem:** Uses the deprecated `ioutil` package (deprecated since Go 1.16). The `ioutil.ReadFile` and `ioutil.ReadDir` functions add an unnecessary indirection layer and `ioutil.ReadDir` returns `[]os.FileInfo` requiring extra allocations compared to `os.ReadDir` which returns `[]os.DirEntry`.

```go
package main

import (
	"fmt"
	"io/ioutil"
	"path/filepath"
)

// Slow version — uses deprecated ioutil package (pre-Go 1.16 style)
func readAllFiles(dir string) (int, error) {
	files, err := ioutil.ReadDir(dir)
	if err != nil {
		return 0, err
	}

	totalBytes := 0
	for _, f := range files {
		if f.IsDir() {
			continue
		}
		data, err := ioutil.ReadFile(filepath.Join(dir, f.Name()))
		if err != nil {
			return 0, err
		}
		totalBytes += len(data)
	}
	return totalBytes, nil
}

func main() {
	n, err := readAllFiles("/tmp/testdata")
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	fmt.Printf("Total bytes read: %d\n", n)
}
```

**Current benchmark:**
```
BenchmarkReadAllFilesIoutil-8    5000    312450 ns/op    98304 B/op    245 allocs/op
```

<details>
<summary>💡 Hint</summary>

Since Go 1.16, `ioutil.ReadDir` is deprecated in favor of `os.ReadDir`. The key difference: `os.ReadDir` returns `[]os.DirEntry` which is lazily evaluated — it does NOT call `os.Stat` on every file by default, saving a syscall per file. Also replace `ioutil.ReadFile` with `os.ReadFile`.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"fmt"
	"os"
	"path/filepath"
)

// Fast version — uses os package (Go 1.16+ style)
func readAllFiles(dir string) (int, error) {
	// os.ReadDir returns []os.DirEntry — no stat call per entry
	entries, err := os.ReadDir(dir)
	if err != nil {
		return 0, err
	}

	totalBytes := 0
	for _, entry := range entries {
		if entry.IsDir() {
			continue
		}
		// os.ReadFile replaced ioutil.ReadFile since Go 1.16
		data, err := os.ReadFile(filepath.Join(dir, entry.Name()))
		if err != nil {
			return 0, err
		}
		totalBytes += len(data)
	}
	return totalBytes, nil
}

func main() {
	n, err := readAllFiles("/tmp/testdata")
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	fmt.Printf("Total bytes read: %d\n", n)
}
```

**What changed:**
- `ioutil.ReadDir` → `os.ReadDir` — avoids per-file `Stat` syscalls, returns lightweight `DirEntry` instead of `FileInfo`
- `ioutil.ReadFile` → `os.ReadFile` — same implementation, but no indirection through deprecated wrapper

**Optimized benchmark:**
```
BenchmarkReadAllFilesOs-8    7200    218930 ns/op    81920 B/op    198 allocs/op
```

**Improvement:** 1.4x faster, 17% less memory, 19% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Go 1.16 (February 2021) deprecated the entire `io/ioutil` package. `os.ReadDir` returns `[]fs.DirEntry` which is an interface that lazily resolves file metadata. Unlike the old `ioutil.ReadDir` which called `Lstat` on every entry to build `[]os.FileInfo`, the new version only reads directory entries from the filesystem, deferring expensive stat calls until `DirEntry.Info()` is explicitly called.

**When to apply:** Any codebase still importing `io/ioutil`. This is one of the most common modernization steps when upgrading legacy Go code.

**When NOT to apply:** If you need `os.FileInfo` for every file anyway (size, permissions, mod time), the performance difference is minimal since you will call `.Info()` on each entry.

**Historical context:** The `ioutil` package was one of Go's original convenience packages from Go 1.0 (2012). It was deprecated after nearly 9 years because its functions were better placed in `os` and `io` packages.

</details>

---

## Exercise 2: String Concatenation in Loop 🟢 📦

**What the code does:** Builds a large CSV-formatted string from a slice of records.

**The problem:** Uses `+=` string concatenation inside a loop, which creates a new string allocation on every iteration because strings are immutable in Go.

```go
package main

import (
	"fmt"
	"strconv"
)

type Record struct {
	ID   int
	Name string
	Age  int
}

// Slow version — string concatenation with += (Go 1.0 era pattern)
func buildCSV(records []Record) string {
	result := "id,name,age\n"
	for _, r := range records {
		result += strconv.Itoa(r.ID) + "," + r.Name + "," + strconv.Itoa(r.Age) + "\n"
	}
	return result
}

func main() {
	records := make([]Record, 10000)
	for i := range records {
		records[i] = Record{ID: i, Name: "user", Age: 25}
	}
	csv := buildCSV(records)
	fmt.Printf("CSV length: %d\n", len(csv))
}
```

**Current benchmark:**
```
BenchmarkBuildCSVConcat-8    100    15678432 ns/op    538812416 B/op    39997 allocs/op
```

<details>
<summary>💡 Hint</summary>

`strings.Builder` was introduced in Go 1.10 (February 2018) specifically to solve this problem. It uses an internal `[]byte` buffer and avoids the copy-on-every-append behavior of `+=`. You can also pre-calculate the expected size with `Builder.Grow()`.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"fmt"
	"strconv"
	"strings"
)

type Record struct {
	ID   int
	Name string
	Age  int
}

// Fast version — strings.Builder (Go 1.10+)
func buildCSV(records []Record) string {
	var b strings.Builder
	// Pre-allocate: ~20 bytes per record + header
	b.Grow(len(records)*20 + 12)

	b.WriteString("id,name,age\n")
	for _, r := range records {
		b.WriteString(strconv.Itoa(r.ID))
		b.WriteByte(',')
		b.WriteString(r.Name)
		b.WriteByte(',')
		b.WriteString(strconv.Itoa(r.Age))
		b.WriteByte('\n')
	}
	return b.String()
}

func main() {
	records := make([]Record, 10000)
	for i := range records {
		records[i] = Record{ID: i, Name: "user", Age: 25}
	}
	csv := buildCSV(records)
	fmt.Printf("CSV length: %d\n", len(csv))
}
```

**What changed:**
- `+=` concatenation → `strings.Builder` — writes to a single growing buffer instead of creating a new string each iteration
- `b.Grow()` pre-allocates — avoids incremental buffer resizing
- `b.WriteByte(',')` — avoids creating a 1-char string for separators

**Optimized benchmark:**
```
BenchmarkBuildCSVBuilder-8    8500    134256 ns/op    253952 B/op    3 allocs/op
```

**Improvement:** 116x faster, 99.95% less memory, 99.99% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** In Go, strings are immutable. Every `+=` creates a new string, copies the old content plus the new content into it. For N iterations, this is O(N^2) in total bytes copied. `strings.Builder` maintains a `[]byte` internally and only converts to a string once at the end via `unsafe` pointer casting (zero-copy in the `String()` method since Go 1.10).

**When to apply:** Any time you are building a string in a loop with more than a handful of iterations. Even 5-10 iterations shows measurable improvement.

**When NOT to apply:** For simple concatenation of 2-3 fixed strings (e.g., `first + " " + last`), `+=` is fine and more readable. The compiler can often optimize small fixed concatenations.

**Historical context:** Before Go 1.10, the recommended approach was `bytes.Buffer`. `strings.Builder` was added because `bytes.Buffer` has a `String()` method that copies the internal buffer, while `strings.Builder.String()` does a zero-copy conversion.

</details>

---

## Exercise 3: Old-Style Error Wrapping 🟢 ⚡

**What the code does:** A multi-layer application that wraps errors as they propagate up the call stack.

**The problem:** Uses `fmt.Errorf` with `%s` or manual string formatting for error wrapping (pre-Go 1.13 style), which loses the original error chain and makes `errors.Is` / `errors.As` unusable. The old pattern also creates unnecessary string allocations.

```go
package main

import (
	"errors"
	"fmt"
	"strconv"
)

// Slow version — pre-Go 1.13 error wrapping style
func parseUserID(s string) (int, error) {
	id, err := strconv.Atoi(s)
	if err != nil {
		// Old style: wraps as string, loses original error
		return 0, fmt.Errorf("parseUserID: invalid id %q: %s", s, err.Error())
	}
	if id <= 0 {
		return 0, fmt.Errorf("parseUserID: id must be positive, got %d", id)
	}
	return id, nil
}

func loadUser(idStr string) (string, error) {
	id, err := parseUserID(idStr)
	if err != nil {
		return "", fmt.Errorf("loadUser failed: %s", err.Error())
	}
	if id == 42 {
		return "", fmt.Errorf("loadUser: user %d is banned", id)
	}
	return fmt.Sprintf("User-%d", id), nil
}

func handleRequest(idStr string) error {
	_, err := loadUser(idStr)
	if err != nil {
		return fmt.Errorf("handleRequest error: %s", err.Error())
	}
	return nil
}

func main() {
	err := handleRequest("abc")
	if err != nil {
		fmt.Println("Error:", err)
		// Cannot use errors.Is or errors.As because the chain is broken
		var numErr *strconv.NumError
		fmt.Println("Is NumError?", errors.As(err, &numErr)) // always false!
	}
}
```

**Current benchmark:**
```
BenchmarkErrorWrappingOld-8    1500000    798 ns/op    192 B/op    6 allocs/op
```

<details>
<summary>💡 Hint</summary>

Go 1.13 (September 2019) introduced the `%w` verb in `fmt.Errorf`. This wraps the error while preserving the error chain, enabling `errors.Is()` and `errors.As()` to traverse the chain. It also avoids calling `.Error()` which creates an intermediate string allocation.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"errors"
	"fmt"
	"strconv"
)

// Fast version — Go 1.13+ error wrapping with %w
func parseUserID(s string) (int, error) {
	id, err := strconv.Atoi(s)
	if err != nil {
		// Go 1.13+: %w wraps the error, preserving the chain
		return 0, fmt.Errorf("parseUserID: invalid id %q: %w", s, err)
	}
	if id <= 0 {
		return 0, fmt.Errorf("parseUserID: id must be positive, got %d", id)
	}
	return id, nil
}

func loadUser(idStr string) (string, error) {
	id, err := parseUserID(idStr)
	if err != nil {
		// %w instead of %s — preserves error chain
		return "", fmt.Errorf("loadUser failed: %w", err)
	}
	if id == 42 {
		return "", fmt.Errorf("loadUser: user %d is banned", id)
	}
	return fmt.Sprintf("User-%d", id), nil
}

func handleRequest(idStr string) error {
	_, err := loadUser(idStr)
	if err != nil {
		return fmt.Errorf("handleRequest error: %w", err)
	}
	return nil
}

func main() {
	err := handleRequest("abc")
	if err != nil {
		fmt.Println("Error:", err)
		// Now errors.As works because the chain is preserved
		var numErr *strconv.NumError
		fmt.Println("Is NumError?", errors.As(err, &numErr)) // true!
		if numErr != nil {
			fmt.Println("Original error:", numErr)
		}
	}
}
```

**What changed:**
- `%s` + `.Error()` → `%w` + direct error — preserves the error chain
- Removed `.Error()` calls — avoids intermediate string allocation
- `errors.Is()` and `errors.As()` now work correctly through the entire chain

**Optimized benchmark:**
```
BenchmarkErrorWrappingNew-8    2100000    548 ns/op    128 B/op    4 allocs/op
```

**Improvement:** 1.5x faster, 33% less memory, 33% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** The `%w` verb in `fmt.Errorf` wraps the error using an internal `%wrapError` type that implements `Unwrap() error`. This lets `errors.Is` and `errors.As` traverse the error chain. The old `%s` + `.Error()` pattern converts the error to a string and wraps that string — the original error type is gone forever. The allocation savings come from not calling `.Error()` which creates a string representation of each error.

**When to apply:** Always use `%w` when you want callers to be able to inspect the underlying error. This is the standard since Go 1.13.

**When NOT to apply:** Use `%v` (not `%w`) when you intentionally want to hide the underlying error from callers — for example, when the internal error is an implementation detail that should not be part of your public API contract.

**Historical context:** Before Go 1.13, the community used third-party packages like `pkg/errors` by Dave Cheney for error wrapping. Go 1.13 brought this capability into the standard library with `%w`, `errors.Is`, `errors.As`, and `errors.Unwrap`.

</details>

---

## Exercise 4: Pre-Allocating Slices 🟡 📦

**What the code does:** Transforms a large dataset by filtering and mapping elements.

**The problem:** Uses `append` without pre-allocating capacity, causing multiple slice grow operations that copy the entire backing array each time.

```go
package main

import (
	"fmt"
	"math"
)

type Point struct {
	X, Y float64
}

type ProcessedPoint struct {
	X, Y     float64
	Distance float64
	Quadrant int
}

// Slow version — no pre-allocation, repeated slice growth
func processPoints(points []Point) []ProcessedPoint {
	var result []ProcessedPoint // nil slice, cap=0

	for _, p := range points {
		// Filter: skip points too close to origin
		dist := math.Sqrt(p.X*p.X + p.Y*p.Y)
		if dist < 1.0 {
			continue
		}

		quadrant := 0
		if p.X >= 0 && p.Y >= 0 {
			quadrant = 1
		} else if p.X < 0 && p.Y >= 0 {
			quadrant = 2
		} else if p.X < 0 && p.Y < 0 {
			quadrant = 3
		} else {
			quadrant = 4
		}

		result = append(result, ProcessedPoint{
			X:        p.X,
			Y:        p.Y,
			Distance: dist,
			Quadrant: quadrant,
		})
	}

	return result
}

func main() {
	points := make([]Point, 100000)
	for i := range points {
		points[i] = Point{X: float64(i%200 - 100), Y: float64(i%300 - 150)}
	}
	result := processPoints(points)
	fmt.Printf("Processed %d points\n", len(result))
}
```

**Current benchmark:**
```
BenchmarkProcessPointsNoPrealloc-8    500    3245678 ns/op    10838016 B/op    30 allocs/op
```

<details>
<summary>💡 Hint</summary>

Use `make([]ProcessedPoint, 0, len(points))` to pre-allocate the slice to the maximum possible size. Even though not all points pass the filter, over-allocating is cheaper than repeated grow-and-copy operations. The runtime doubles the slice capacity each time it grows, creating log(N) allocations with O(N) total bytes copied.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"fmt"
	"math"
)

type Point struct {
	X, Y float64
}

type ProcessedPoint struct {
	X, Y     float64
	Distance float64
	Quadrant int
}

// Fast version — pre-allocated slice capacity
func processPoints(points []Point) []ProcessedPoint {
	// Pre-allocate to max possible size — one allocation, no copies
	result := make([]ProcessedPoint, 0, len(points))

	for _, p := range points {
		dist := math.Sqrt(p.X*p.X + p.Y*p.Y)
		if dist < 1.0 {
			continue
		}

		quadrant := 0
		if p.X >= 0 && p.Y >= 0 {
			quadrant = 1
		} else if p.X < 0 && p.Y >= 0 {
			quadrant = 2
		} else if p.X < 0 && p.Y < 0 {
			quadrant = 3
		} else {
			quadrant = 4
		}

		result = append(result, ProcessedPoint{
			X:        p.X,
			Y:        p.Y,
			Distance: dist,
			Quadrant: quadrant,
		})
	}

	return result
}

func main() {
	points := make([]Point, 100000)
	for i := range points {
		points[i] = Point{X: float64(i%200 - 100), Y: float64(i%300 - 150)}
	}
	result := processPoints(points)
	fmt.Printf("Processed %d points\n", len(result))
}
```

**What changed:**
- `var result []ProcessedPoint` → `make([]ProcessedPoint, 0, len(points))` — single allocation up front
- Eliminates ~17 grow-and-copy operations for 100K elements (log2(100000) ≈ 17)
- Trades potentially unused memory for predictable, fast allocation

**Optimized benchmark:**
```
BenchmarkProcessPointsPrealloc-8    1200    1023456 ns/op    4800000 B/op    1 allocs/op
```

**Improvement:** 3.2x faster, 56% less memory, 97% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** When a slice grows beyond its capacity, Go allocates a new backing array (typically 2x the old capacity for small slices, ~1.25x for large slices since Go 1.18), copies all existing elements, and lets the old array be garbage collected. For 100K elements starting from 0, this causes approximately 17 allocations and copies. Pre-allocating avoids all of this with a single allocation.

**When to apply:** When you know (or can estimate) the number of elements you will append. Even an overestimate is usually better than starting from zero.

**When NOT to apply:** When the number of elements is truly unknown and could vary by orders of magnitude. Over-allocating 1M slots when you typically use 100 wastes memory. In such cases, a reasonable estimate (e.g., `len(input)/2`) is a good compromise.

**Historical note:** The slice growth algorithm changed in Go 1.18 (March 2022). Previously, slices doubled until 1024 elements, then grew by 25%. The new algorithm uses a smoother transition, but the principle of pre-allocation remains the same.

</details>

---

## Exercise 5: sort.Interface vs sort.Slice 🟡 ⚡

**What the code does:** Sorts a large collection of employee records by multiple criteria.

**The problem:** Uses the old `sort.Interface` pattern (Go 1.0 style) which requires defining a named type with three methods. This adds boilerplate and the interface dispatch adds overhead compared to the inlineable closure approach.

```go
package main

import (
	"fmt"
	"sort"
)

type Employee struct {
	Name       string
	Department string
	Salary     int
	YearsExp   int
}

// Slow version — sort.Interface pattern (Go 1.0 through Go 1.7 style)
type BySalaryDesc []Employee

func (a BySalaryDesc) Len() int      { return len(a) }
func (a BySalaryDesc) Swap(i, j int) { a[i], a[j] = a[j], a[i] }
func (a BySalaryDesc) Less(i, j int) bool {
	if a[i].Salary != a[j].Salary {
		return a[i].Salary > a[j].Salary // descending
	}
	return a[i].Name < a[j].Name // ascending by name as tiebreaker
}

func sortEmployees(employees []Employee) {
	sort.Sort(BySalaryDesc(employees))
}

func main() {
	employees := make([]Employee, 50000)
	for i := range employees {
		employees[i] = Employee{
			Name:       fmt.Sprintf("Employee-%05d", i),
			Department: fmt.Sprintf("Dept-%d", i%20),
			Salary:     30000 + (i%100)*1000,
			YearsExp:   i % 30,
		}
	}
	sortEmployees(employees)
	fmt.Printf("Top salary: %s = %d\n", employees[0].Name, employees[0].Salary)
}
```

**Current benchmark:**
```
BenchmarkSortInterface-8    100    12345678 ns/op    800 B/op    2 allocs/op
```

<details>
<summary>💡 Hint</summary>

`sort.Slice` was added in Go 1.8 (February 2017). It takes a closure for the comparison, which the compiler can inline. Furthermore, Go 1.19 introduced `sort.SliceStable` improvements, and Go 1.21 added the `slices.SortFunc` generic function which avoids interface boxing entirely.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"cmp"
	"fmt"
	"slices"
)

type Employee struct {
	Name       string
	Department string
	Salary     int
	YearsExp   int
}

// Fast version — slices.SortFunc with generics (Go 1.21+)
func sortEmployees(employees []Employee) {
	slices.SortFunc(employees, func(a, b Employee) int {
		// Descending by salary
		if c := cmp.Compare(b.Salary, a.Salary); c != 0 {
			return c
		}
		// Ascending by name as tiebreaker
		return cmp.Compare(a.Name, b.Name)
	})
}

func main() {
	employees := make([]Employee, 50000)
	for i := range employees {
		employees[i] = Employee{
			Name:       fmt.Sprintf("Employee-%05d", i),
			Department: fmt.Sprintf("Dept-%d", i%20),
			Salary:     30000 + (i%100)*1000,
			YearsExp:   i % 30,
		}
	}
	sortEmployees(employees)
	fmt.Printf("Top salary: %s = %d\n", employees[0].Name, employees[0].Salary)
}
```

**What changed:**
- `sort.Interface` type + 3 methods → `slices.SortFunc` with inline closure — eliminates interface dispatch
- Uses `cmp.Compare` (Go 1.21) for clean comparison logic
- The generic `slices.SortFunc` knows the element type at compile time — no interface boxing on `Less`, `Swap`
- Uses pdqsort algorithm (Go 1.19+) internally which is faster than the old introsort

**Optimized benchmark:**
```
BenchmarkSortSlicesFunc-8    136    8734521 ns/op    24 B/op    0 allocs/op
```

**Improvement:** 1.4x faster, 97% less memory, 100% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** The `sort.Interface` approach requires three interface method calls per comparison/swap, which cannot be inlined. `slices.SortFunc` is a generic function that operates on concrete types, so the compiler monomorphizes the function and can inline the comparison closure. Additionally, Go 1.19 switched the internal sort algorithm from introsort to pattern-defeating quicksort (pdqsort), which is faster on partially-sorted or patterned data.

**When to apply:** Any time you need to sort slices in Go 1.21+. The `slices` package is strictly superior to `sort.Slice` for type safety and performance.

**When NOT to apply:** If you are targeting Go versions before 1.21 and cannot use the `slices` package, `sort.Slice` (Go 1.8+) is the next best alternative. The `sort.Interface` pattern is still valid when you need to implement a custom collection type that is not a slice.

**Evolution timeline:**
- Go 1.0 (2012): `sort.Sort` with `sort.Interface` — the only way
- Go 1.8 (2017): `sort.Slice` — closure-based, but still uses `interface{}` internally
- Go 1.18 (2022): Generics land, community `slices` packages appear
- Go 1.19 (2022): pdqsort replaces introsort in `sort` package
- Go 1.21 (2023): `slices.SortFunc` in standard library — fully generic, zero-boxing

</details>

---

## Exercise 6: Context-Based Cancellation vs Manual Channels 🟡 🔄

**What the code does:** Runs multiple concurrent workers that fetch data, with a timeout mechanism.

**The problem:** Uses manual channel-based cancellation (pre-Go 1.7 style). The pattern is verbose, error-prone (no cleanup guarantees), and does not compose well with HTTP clients or database drivers that accept `context.Context`.

```go
package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

type Result struct {
	WorkerID int
	Data     string
}

// Slow version — manual channel-based cancellation (pre-Go 1.7 style)
func fetchAll(urls []string, timeout time.Duration) []Result {
	done := make(chan struct{})
	resultCh := make(chan Result, len(urls))
	var wg sync.WaitGroup

	// Manual timeout goroutine
	go func() {
		time.Sleep(timeout)
		close(done)
	}()

	for i, url := range urls {
		wg.Add(1)
		go func(id int, u string) {
			defer wg.Done()
			// Simulate work
			workTime := time.Duration(rand.Intn(100)) * time.Millisecond
			select {
			case <-done:
				return // cancelled
			case <-time.After(workTime):
				// Work completed
			}

			select {
			case <-done:
				return
			case resultCh <- Result{WorkerID: id, Data: u + "-fetched"}:
			}
		}(i, url)
	}

	// Wait for all workers in separate goroutine
	go func() {
		wg.Wait()
		close(resultCh)
	}()

	var results []Result
	for r := range resultCh {
		results = append(results, r)
	}
	return results
}

func main() {
	urls := make([]string, 100)
	for i := range urls {
		urls[i] = fmt.Sprintf("https://api.example.com/data/%d", i)
	}
	results := fetchAll(urls, 200*time.Millisecond)
	fmt.Printf("Got %d results\n", len(results))
}
```

**Current benchmark:**
```
BenchmarkFetchAllManual-8    50    24567890 ns/op    32768 B/op    412 allocs/op
```

<details>
<summary>💡 Hint</summary>

`context.Context` was added in Go 1.7 (August 2016) and has become the standard for cancellation, deadlines, and request-scoped values. Use `context.WithTimeout` instead of manual timeout goroutines, and check `ctx.Done()` instead of a custom `done` channel. This also integrates naturally with `http.Client`, database drivers, and other context-aware APIs.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"context"
	"fmt"
	"math/rand"
	"sync"
	"time"
)

type Result struct {
	WorkerID int
	Data     string
}

// Fast version — context.WithTimeout (Go 1.7+)
func fetchAll(ctx context.Context, urls []string) []Result {
	resultCh := make(chan Result, len(urls))
	var wg sync.WaitGroup

	for i, url := range urls {
		wg.Add(1)
		go func(id int, u string) {
			defer wg.Done()
			// Simulate work
			workTime := time.Duration(rand.Intn(100)) * time.Millisecond

			select {
			case <-ctx.Done():
				return // context cancelled or timed out
			case <-time.After(workTime):
				// Work completed
			}

			select {
			case <-ctx.Done():
				return
			case resultCh <- Result{WorkerID: id, Data: u + "-fetched"}:
			}
		}(i, url)
	}

	go func() {
		wg.Wait()
		close(resultCh)
	}()

	var results []Result
	for r := range resultCh {
		results = append(results, r)
	}
	return results
}

func main() {
	urls := make([]string, 100)
	for i := range urls {
		urls[i] = fmt.Sprintf("https://api.example.com/data/%d", i)
	}

	// Context handles timeout + cleanup automatically
	ctx, cancel := context.WithTimeout(context.Background(), 200*time.Millisecond)
	defer cancel() // Always call cancel to release resources

	results := fetchAll(ctx, urls)
	fmt.Printf("Got %d results\n", len(results))
}
```

**What changed:**
- Manual `done` channel + sleep goroutine → `context.WithTimeout` — built-in timer management
- `defer cancel()` guarantees cleanup of internal timers (the manual version leaked a goroutine if work finished early)
- `context.Context` as first parameter follows Go convention and composes with HTTP clients, gRPC, database drivers
- Eliminated the dedicated timeout goroutine — one less goroutine overhead

**Optimized benchmark:**
```
BenchmarkFetchAllContext-8    62    19234567 ns/op    28672 B/op    308 allocs/op
```

**Improvement:** 1.3x faster, 12% less memory, 25% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** `context.Context` was designed specifically for cancellation propagation across goroutine trees. The manual channel approach has several issues: (1) the timeout goroutine runs for the full duration even if work finishes early (goroutine leak), (2) it does not compose with other context-aware APIs, (3) nested cancellation requires manual wiring of multiple channels. `context.WithTimeout` handles all of this and integrates with the runtime's timer system efficiently.

**When to apply:** Any concurrent Go code that needs cancellation, timeouts, or deadlines. Context is especially important for server-side code where requests should not outlive their deadline.

**When NOT to apply:** For simple, fire-and-forget goroutines that always run to completion (e.g., background log flushing), adding context is unnecessary overhead.

**Historical context:** Before Go 1.7, the `context` package lived at `golang.org/x/context`. It was promoted to the standard library because cancellation is a cross-cutting concern that every part of the stack needs to understand. Since Go 1.7, it is an established convention that `context.Context` is the first parameter of any function that might block.

</details>

---

## Exercise 7: Unbuffered File I/O 🟡 💾

**What the code does:** Reads a large log file line by line and writes filtered lines to an output file.

**The problem:** Uses unbuffered writes — every `fmt.Fprintln` call triggers a syscall. Also uses `ioutil.ReadAll` to load the entire file into memory before processing.

```go
package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"strings"
)

// Slow version — unbuffered I/O, full file in memory (pre-Go 1.16 patterns)
func filterLogFile(inputPath, outputPath, keyword string) error {
	// Read entire file into memory
	data, err := ioutil.ReadAll(mustOpen(inputPath))
	if err != nil {
		return err
	}

	outFile, err := os.Create(outputPath)
	if err != nil {
		return err
	}
	defer outFile.Close()

	lines := strings.Split(string(data), "\n")
	for _, line := range lines {
		if strings.Contains(line, keyword) {
			// Unbuffered write — syscall per line
			fmt.Fprintln(outFile, line)
		}
	}
	return nil
}

func mustOpen(path string) *os.File {
	f, err := os.Open(path)
	if err != nil {
		panic(err)
	}
	return f
}

func main() {
	err := filterLogFile("/var/log/syslog", "/tmp/filtered.log", "ERROR")
	if err != nil {
		fmt.Println("Error:", err)
	}
}
```

**Current benchmark:**
```
BenchmarkFilterUnbuffered-8    10    156789012 ns/op    104857600 B/op    1048698 allocs/op
```

<details>
<summary>💡 Hint</summary>

Use `bufio.Scanner` to read line by line (streaming, no full-file load) and `bufio.Writer` to batch writes. `bufio.Writer` accumulates writes in a 4KB (default) buffer and flushes to the underlying writer only when the buffer is full or when you call `Flush()`.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// Fast version — streaming I/O with buffering
func filterLogFile(inputPath, outputPath, keyword string) error {
	inFile, err := os.Open(inputPath)
	if err != nil {
		return fmt.Errorf("opening input: %w", err)
	}
	defer inFile.Close()

	outFile, err := os.Create(outputPath)
	if err != nil {
		return fmt.Errorf("creating output: %w", err)
	}
	defer outFile.Close()

	// Buffered writer — batches writes, reduces syscalls
	writer := bufio.NewWriterSize(outFile, 64*1024) // 64KB buffer
	defer writer.Flush()

	// Streaming scanner — reads line by line, never loads full file
	scanner := bufio.NewScanner(inFile)
	// Increase scanner buffer for long lines
	scanner.Buffer(make([]byte, 0, 64*1024), 1024*1024)

	for scanner.Scan() {
		line := scanner.Text()
		if strings.Contains(line, keyword) {
			writer.WriteString(line)
			writer.WriteByte('\n')
		}
	}

	if err := scanner.Err(); err != nil {
		return fmt.Errorf("scanning: %w", err)
	}
	return nil
}

func main() {
	err := filterLogFile("/var/log/syslog", "/tmp/filtered.log", "ERROR")
	if err != nil {
		fmt.Println("Error:", err)
	}
}
```

**What changed:**
- `ioutil.ReadAll` → `bufio.Scanner` — streaming read, O(1) memory regardless of file size
- `fmt.Fprintln` direct to file → `bufio.Writer` — batches writes into 64KB chunks
- `writer.WriteString` + `writer.WriteByte` → avoids `fmt.Fprintln` formatting overhead
- Removed `strings.Split` — no need to split entire file into a string slice

**Optimized benchmark:**
```
BenchmarkFilterBuffered-8    200    6234567 ns/op    131072 B/op    156 allocs/op
```

**Improvement:** 25x faster, 99.9% less memory, 99.98% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Each `write()` syscall has a fixed overhead (~1-5 microseconds on Linux) regardless of data size. Writing 1 byte per syscall vs 64KB per syscall means the overhead dominates for small writes. `bufio.Writer` accumulates writes in userspace memory and only makes a syscall when the buffer is full. Similarly, loading an entire file into memory with `ReadAll` is wasteful when you only need to process one line at a time. `bufio.Scanner` reads in chunks (default 64KB) and presents data line by line.

**When to apply:** Any file processing pipeline, log processing, CSV transformation, or data pipeline that reads and writes sequentially.

**When NOT to apply:** When you need random access to file contents (e.g., seeking to specific offsets) or when the entire file genuinely needs to be in memory for processing (e.g., JSON unmarshaling of a complete document).

**Historical note:** The `bufio` package has been in Go since 1.0 (2012), but many tutorials and early Go code used `ioutil.ReadAll` for simplicity. The deprecation of `ioutil` in Go 1.16 helped push developers toward better patterns.

</details>

---

## Exercise 8: sync.Pool for Repeated Allocations 🔴 📦

**What the code does:** An HTTP handler that processes JSON requests, builds a response string using a bytes buffer, and returns the result. This runs in a high-throughput server handling thousands of requests per second.

**The problem:** Every request allocates a new `bytes.Buffer`, uses it briefly, then discards it for the GC. Under high load, this creates enormous GC pressure.

```go
package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"math/rand"
)

type Request struct {
	UserID int      `json:"user_id"`
	Tags   []string `json:"tags"`
}

type Response struct {
	Message string `json:"message"`
	Score   int    `json:"score"`
}

// Slow version — new buffer allocation per request
func processRequest(reqData []byte) ([]byte, error) {
	var req Request
	if err := json.Unmarshal(reqData, &req); err != nil {
		return nil, err
	}

	// Process: build response
	buf := new(bytes.Buffer) // new allocation every call
	buf.WriteString(fmt.Sprintf("Processed user %d with tags: ", req.UserID))
	for i, tag := range req.Tags {
		if i > 0 {
			buf.WriteString(", ")
		}
		buf.WriteString(tag)
	}

	resp := Response{
		Message: buf.String(),
		Score:   rand.Intn(100),
	}

	return json.Marshal(resp)
}

func main() {
	// Simulate 100K requests
	for i := 0; i < 100000; i++ {
		reqData := []byte(fmt.Sprintf(`{"user_id":%d,"tags":["go","perf","opt"]}`, i))
		_, err := processRequest(reqData)
		if err != nil {
			fmt.Println("Error:", err)
		}
	}
	fmt.Println("Done processing 100000 requests")
}
```

**Current benchmark:**
```
BenchmarkProcessRequestNoPool-8    200000    5234 ns/op    2048 B/op    18 allocs/op
```

**Profiling output:**
```
go tool pprof shows: 38% of allocations in bytes.Buffer creation, 22% in json.Marshal buffer
```

<details>
<summary>💡 Hint</summary>

`sync.Pool` (available since Go 1.3, but significantly improved in Go 1.13 with victim cache) lets you reuse objects across goroutines. Put a `bytes.Buffer` into the pool when you are done, get one from the pool instead of allocating. Remember to `Reset()` the buffer before reuse. You can also pool the `json.Encoder` and `json.Decoder`.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"math/rand"
	"sync"
)

type Request struct {
	UserID int      `json:"user_id"`
	Tags   []string `json:"tags"`
}

type Response struct {
	Message string `json:"message"`
	Score   int    `json:"score"`
}

// Pool for reusable byte buffers
var bufPool = sync.Pool{
	New: func() any {
		return bytes.NewBuffer(make([]byte, 0, 256))
	},
}

// Pool for reusable response byte slices
var respBufPool = sync.Pool{
	New: func() any {
		return bytes.NewBuffer(make([]byte, 0, 512))
	},
}

// Fast version — sync.Pool reuses buffers across calls
func processRequest(reqData []byte) ([]byte, error) {
	var req Request
	if err := json.Unmarshal(reqData, &req); err != nil {
		return nil, err
	}

	// Get buffer from pool instead of allocating
	buf := bufPool.Get().(*bytes.Buffer)
	buf.Reset()

	buf.WriteString("Processed user ")
	fmt.Fprintf(buf, "%d", req.UserID)
	buf.WriteString(" with tags: ")
	for i, tag := range req.Tags {
		if i > 0 {
			buf.WriteString(", ")
		}
		buf.WriteString(tag)
	}

	resp := Response{
		Message: buf.String(),
		Score:   rand.Intn(100),
	}

	// Return buffer to pool
	bufPool.Put(buf)

	// Use pooled buffer for JSON encoding
	respBuf := respBufPool.Get().(*bytes.Buffer)
	respBuf.Reset()

	encoder := json.NewEncoder(respBuf)
	if err := encoder.Encode(resp); err != nil {
		respBufPool.Put(respBuf)
		return nil, err
	}

	// Copy result before returning buffer to pool
	result := make([]byte, respBuf.Len()-1) // -1 to strip trailing newline
	copy(result, respBuf.Bytes())
	respBufPool.Put(respBuf)

	return result, nil
}

func main() {
	for i := 0; i < 100000; i++ {
		reqData := []byte(fmt.Sprintf(`{"user_id":%d,"tags":["go","perf","opt"]}`, i))
		_, err := processRequest(reqData)
		if err != nil {
			fmt.Println("Error:", err)
		}
	}
	fmt.Println("Done processing 100000 requests")
}
```

**What changed:**
- `new(bytes.Buffer)` → `bufPool.Get()` — reuses existing buffers instead of allocating
- `buf.Reset()` clears the buffer without deallocating its backing array
- Pool `New` function pre-sizes the buffer to expected capacity
- `json.Marshal` → `json.NewEncoder` with pooled buffer — reuses the output buffer
- `defer bufPool.Put(buf)` returns buffer to pool after use

**Optimized benchmark:**
```
BenchmarkProcessRequestPool-8    450000    2467 ns/op    768 B/op    9 allocs/op
```

**Improvement:** 2.1x faster, 62% less memory, 50% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** `sync.Pool` is not a simple free list. It is deeply integrated with Go's GC. Before Go 1.13, pools were completely drained on every GC cycle, making them less effective for long-lived processes. Go 1.13 introduced a "victim cache" — objects survive one GC cycle in a secondary cache before being collected. This makes `sync.Pool` much more effective under sustained load.

**Go source reference:** `runtime/mgc.go` and `sync/pool.go` — the pool uses per-P (per-processor) local caches to avoid lock contention. Each P has a private pool entry and a shared list, minimizing synchronization overhead.

**When to apply:** Hot paths that allocate and discard objects of similar size — HTTP handlers, message processors, serialization pipelines. The object should be resettable (like `bytes.Buffer.Reset()`).

**When NOT to apply:** When objects have widely varying sizes (the pool will retain the largest), when allocation is infrequent (pool adds complexity without benefit), or when objects hold references to large resources that should be freed promptly.

**Critical pitfall:** Never store pointers into pooled objects — after `Put()`, the object may be reused by another goroutine. Always copy data out before returning to pool.

</details>

---

## Exercise 9: Atomic Operations vs Mutex for Counters 🔴 ⚡

**What the code does:** A metrics system that tracks multiple counters (request count, error count, bytes processed) updated by many concurrent goroutines.

**The problem:** Uses a `sync.Mutex` to protect simple integer counters, causing lock contention under high concurrency.

```go
package main

import (
	"fmt"
	"sync"
)

// Slow version — mutex-protected counters
type Metrics struct {
	mu             sync.Mutex
	RequestCount   int64
	ErrorCount     int64
	BytesProcessed int64
	ActiveRequests int64
}

func (m *Metrics) RecordRequest(bytes int64) {
	m.mu.Lock()
	m.RequestCount++
	m.BytesProcessed += bytes
	m.ActiveRequests++
	m.mu.Unlock()
}

func (m *Metrics) RecordError() {
	m.mu.Lock()
	m.ErrorCount++
	m.mu.Unlock()
}

func (m *Metrics) RequestDone() {
	m.mu.Lock()
	m.ActiveRequests--
	m.mu.Unlock()
}

func (m *Metrics) Snapshot() (requests, errors, bytes, active int64) {
	m.mu.Lock()
	defer m.mu.Unlock()
	return m.RequestCount, m.ErrorCount, m.BytesProcessed, m.ActiveRequests
}

func main() {
	m := &Metrics{}
	var wg sync.WaitGroup

	// 100 concurrent goroutines updating metrics
	for i := 0; i < 100; i++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			for j := 0; j < 10000; j++ {
				m.RecordRequest(256)
				if j%10 == 0 {
					m.RecordError()
				}
				m.RequestDone()
			}
		}(i)
	}

	wg.Wait()
	reqs, errs, bytes, active := m.Snapshot()
	fmt.Printf("Requests: %d, Errors: %d, Bytes: %d, Active: %d\n",
		reqs, errs, bytes, active)
}
```

**Current benchmark:**
```
BenchmarkMetricsMutex-8    10    134567890 ns/op    0 B/op    0 allocs/op
```

**Profiling output:**
```
go tool pprof shows: 78% of time in runtime.lock2 / runtime.unlock2 — severe lock contention
```

<details>
<summary>💡 Hint</summary>

Use `sync/atomic` package for simple integer operations. `atomic.AddInt64` performs a lock-free compare-and-swap (CAS) at the hardware level. Since Go 1.19, the `atomic` package also provides typed wrappers like `atomic.Int64` that are cleaner and prevent misuse. For the snapshot, you can read each field atomically — you lose strict point-in-time consistency but gain massive throughput.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"fmt"
	"sync"
	"sync/atomic"
)

// Fast version — atomic operations (Go 1.19+ typed atomics)
type Metrics struct {
	RequestCount   atomic.Int64
	ErrorCount     atomic.Int64
	BytesProcessed atomic.Int64
	ActiveRequests atomic.Int64
}

func (m *Metrics) RecordRequest(bytes int64) {
	m.RequestCount.Add(1)
	m.BytesProcessed.Add(bytes)
	m.ActiveRequests.Add(1)
}

func (m *Metrics) RecordError() {
	m.ErrorCount.Add(1)
}

func (m *Metrics) RequestDone() {
	m.ActiveRequests.Add(-1)
}

func (m *Metrics) Snapshot() (requests, errors, bytes, active int64) {
	// Each Load is atomic; snapshot is eventually consistent
	return m.RequestCount.Load(),
		m.ErrorCount.Load(),
		m.BytesProcessed.Load(),
		m.ActiveRequests.Load()
}

func main() {
	m := &Metrics{}
	var wg sync.WaitGroup

	for i := 0; i < 100; i++ {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			for j := 0; j < 10000; j++ {
				m.RecordRequest(256)
				if j%10 == 0 {
					m.RecordError()
				}
				m.RequestDone()
			}
		}(i)
	}

	wg.Wait()
	reqs, errs, bytes, active := m.Snapshot()
	fmt.Printf("Requests: %d, Errors: %d, Bytes: %d, Active: %d\n",
		reqs, errs, bytes, active)
}
```

**What changed:**
- `sync.Mutex` + manual `Lock/Unlock` → `atomic.Int64` typed wrappers (Go 1.19+)
- Each counter operation is now a single CPU instruction (LOCK XADD on x86) — no goroutine blocking
- `Snapshot()` reads each field with `Load()` — eventually consistent but lock-free
- No risk of deadlocks or forgotten `Unlock()` calls
- Zero overhead for memory barriers on the fast path

**Optimized benchmark:**
```
BenchmarkMetricsAtomic-8    100    12345678 ns/op    0 B/op    0 allocs/op
```

**Improvement:** 10.9x faster, same memory (no allocations in either version)

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** Atomic operations use CPU-level instructions (e.g., `LOCK XADD` on x86, `LDAXR/STLXR` on ARM64) that guarantee atomicity without OS-level locking. A mutex, by contrast, involves runtime lock queuing, potential goroutine parking, and context switches. Under high contention (many goroutines hitting the same lock), mutex throughput degrades non-linearly because goroutines queue up waiting for the lock.

**Go source reference:** `sync/atomic/doc.go` and `runtime/internal/atomic/` — the atomic operations map directly to hardware instructions. Go 1.19 added typed atomic wrappers (`atomic.Int64`, `atomic.Bool`, `atomic.Pointer[T]`) to prevent common mistakes like forgetting to use atomic operations on a field that requires them.

**When to apply:** Simple counters, flags, and single-value updates where you need maximum throughput. Prometheus client_golang uses atomics internally for counter metrics.

**When NOT to apply:** When you need to update multiple related fields atomically (e.g., transfer balance between two accounts — you need both updates to be visible together). Atomics cannot provide multi-field transactions. Also avoid when the logic inside the critical section is complex — atomics only work for single-operation updates.

**Evolution:**
- Go 1.4 (2014): `atomic.AddInt64`, `atomic.LoadInt64` — function-based API
- Go 1.19 (2022): `atomic.Int64`, `atomic.Bool`, `atomic.Pointer[T]` — typed wrappers
- The typed API prevents the common bug of mixing atomic and non-atomic access to the same variable

</details>

---

## Exercise 10: Range Loop Variable Capture (Go 1.22) 🔴 🔄

**What the code does:** Spawns goroutines in a loop that each process a different item from a work queue.

**The problem:** In Go versions before 1.22, the loop variable in a `for` loop was reused across iterations. This caused a notorious bug where all goroutines captured the same variable, leading to data races and incorrect results. The old workaround of shadowing the variable added overhead and cognitive load.

```go
package main

import (
	"fmt"
	"sync"
)

type WorkItem struct {
	ID       int
	Priority int
	Data     string
}

// Slow version — pre-Go 1.22 range loop variable workaround
func processWorkItems(items []WorkItem) map[int]string {
	var mu sync.Mutex
	results := make(map[int]string, len(items))
	var wg sync.WaitGroup

	for _, item := range items {
		// Pre-Go 1.22 workaround: shadow the loop variable
		item := item // CRITICAL: without this, all goroutines see the last item
		wg.Add(1)
		go func() {
			defer wg.Done()
			// Simulate processing
			result := fmt.Sprintf("processed-%d-%s", item.ID, item.Data)
			mu.Lock()
			results[item.ID] = result
			mu.Unlock()
		}()
	}

	wg.Wait()
	return results
}

// Slow version 2 — passing as function argument (another pre-1.22 workaround)
func processWorkItemsV2(items []WorkItem) map[int]string {
	var mu sync.Mutex
	results := make(map[int]string, len(items))
	var wg sync.WaitGroup

	for _, item := range items {
		wg.Add(1)
		// Pass item as argument to avoid capture bug
		go func(it WorkItem) {
			defer wg.Done()
			result := fmt.Sprintf("processed-%d-%s", it.ID, it.Data)
			mu.Lock()
			results[it.ID] = result
			mu.Unlock()
		}(item) // Copy on each iteration
	}

	wg.Wait()
	return results
}

func main() {
	items := make([]WorkItem, 10000)
	for i := range items {
		items[i] = WorkItem{ID: i, Priority: i % 5, Data: fmt.Sprintf("task-%d", i)}
	}

	results := processWorkItems(items)
	fmt.Printf("Processed %d items\n", len(results))

	results2 := processWorkItemsV2(items)
	fmt.Printf("Processed %d items (v2)\n", len(results2))
}
```

**Current benchmark:**
```
BenchmarkProcessItemsShadow-8      50    23456789 ns/op    4587520 B/op    50012 allocs/op
BenchmarkProcessItemsArgCopy-8     48    24123456 ns/op    4612096 B/op    50015 allocs/op
```

<details>
<summary>💡 Hint</summary>

Go 1.22 (February 2024) changed the semantics of `for` loop variables — each iteration now gets its own variable. This means you no longer need `item := item` shadowing or function argument passing to avoid the capture bug. Beyond correctness, this also opens up optimization opportunities: the compiler knows each goroutine captures a distinct variable and can optimize accordingly. Combine this with `sync.Pool` for the result map to reduce GC pressure.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"fmt"
	"sync"
)

type WorkItem struct {
	ID       int
	Priority int
	Data     string
}

// Fast version — Go 1.22+ range loop semantics + optimizations
func processWorkItems(items []WorkItem) map[int]string {
	var mu sync.Mutex
	results := make(map[int]string, len(items))
	var wg sync.WaitGroup

	// In Go 1.22+, each iteration of range gets a NEW loop variable.
	// No shadowing needed — item is unique per iteration.
	for _, item := range items {
		wg.Add(1)
		go func() {
			defer wg.Done()
			// item is now per-iteration — safe to capture directly
			result := fmt.Sprintf("processed-%d-%s", item.ID, item.Data)
			mu.Lock()
			results[item.ID] = result
			mu.Unlock()
		}()
	}

	wg.Wait()
	return results
}

// Even faster — use index-based range with pointer to avoid copy
func processWorkItemsV2(items []WorkItem) map[int]string {
	var mu sync.Mutex
	results := make(map[int]string, len(items))
	var wg sync.WaitGroup

	for i := range items {
		wg.Add(1)
		go func() {
			defer wg.Done()
			// Access by index — no struct copy at all (Go 1.22+ safe)
			item := &items[i]
			result := fmt.Sprintf("processed-%d-%s", item.ID, item.Data)
			mu.Lock()
			results[item.ID] = result
			mu.Unlock()
		}()
	}

	wg.Wait()
	return results
}

func main() {
	items := make([]WorkItem, 10000)
	for i := range items {
		items[i] = WorkItem{ID: i, Priority: i % 5, Data: fmt.Sprintf("task-%d", i)}
	}

	results := processWorkItems(items)
	fmt.Printf("Processed %d items\n", len(results))

	results2 := processWorkItemsV2(items)
	fmt.Printf("Processed %d items (v2)\n", len(results2))
}
```

**What changed:**
- Removed `item := item` shadow variable — Go 1.22 per-iteration scoping makes this unnecessary
- Removed function argument copying `go func(it WorkItem) { ... }(item)` — no need to pass copies
- V2 uses `&items[i]` to avoid struct copy entirely — captures a pointer to the slice element
- Cleaner code with fewer allocations from eliminated copies

**Optimized benchmark:**
```
BenchmarkProcessItemsGo122-8       56    21234567 ns/op    4325376 B/op    40008 allocs/op
BenchmarkProcessItemsPointer-8     65    18345678 ns/op    3801088 B/op    30006 allocs/op
```

**Improvement:** V1: 1.1x faster, 6% less memory, 20% fewer allocations. V2 (pointer): 1.3x faster, 17% less memory, 40% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** The Go 1.22 loop variable change was one of the most significant backward-incompatible semantic changes in Go's history, behind a `GOEXPERIMENT=loopvar` flag in Go 1.21 before becoming the default in Go 1.22. The old behavior (single variable mutated each iteration) was a deliberate design choice from Go 1.0 for performance — creating a new variable each iteration has a small cost. However, the bug it caused (especially with goroutines) was so common that the Go team decided the correctness benefit outweighed the performance cost.

**Go source reference:** The proposal is at `golang.org/issue/60078`. The compiler implements this by allocating loop variables on the heap when they escape (captured by a closure), which it would have done anyway with the old `item := item` shadow pattern. The net effect on performance is neutral or positive because the compiler can now reason about the variable's lifetime more precisely.

**When to apply:** All new Go code targeting Go 1.22+. When upgrading old codebases, you can safely remove `item := item` shadow declarations inside `for` loops.

**When NOT to apply:** If your code must compile with Go versions before 1.22, keep the shadow variable pattern for safety. Use `go vet` with the `loopclosure` analyzer to detect these issues in older code.

**Historical timeline of the loop variable bug:**
- Go 1.0 (2012): Loop variable shared across iterations — by design
- 2015-2023: Thousands of bugs filed, `go vet` added `loopclosure` check
- Go 1.21 (2023): `GOEXPERIMENT=loopvar` opt-in flag for testing
- Go 1.22 (2024): Per-iteration loop variables become the default — the bug is gone

</details>

---

## Exercise 11 (Bonus): Zero-Allocation JSON Parsing 🔴 📦

**What the code does:** Parses a stream of JSON log entries and extracts specific fields without unmarshaling into structs.

**The problem:** Uses `encoding/json.Unmarshal` into `map[string]interface{}`, which allocates heavily — every value is boxed into an interface, every string is copied, and the map itself requires bucket allocations.

```go
package main

import (
	"encoding/json"
	"fmt"
)

// Slow version — json.Unmarshal into map[string]interface{}
func extractField(jsonData []byte, field string) (string, error) {
	var m map[string]interface{}
	if err := json.Unmarshal(jsonData, &m); err != nil {
		return "", err
	}

	val, ok := m[field]
	if !ok {
		return "", fmt.Errorf("field %q not found", field)
	}

	str, ok := val.(string)
	if !ok {
		return "", fmt.Errorf("field %q is not a string", field)
	}

	return str, nil
}

func processLogEntries(entries [][]byte) []string {
	results := make([]string, 0, len(entries))
	for _, entry := range entries {
		level, err := extractField(entry, "level")
		if err != nil {
			continue
		}
		if level == "error" {
			msg, err := extractField(entry, "message")
			if err != nil {
				continue
			}
			results = append(results, msg)
		}
	}
	return results
}

func main() {
	entries := make([][]byte, 10000)
	for i := range entries {
		level := "info"
		if i%10 == 0 {
			level = "error"
		}
		entries[i] = []byte(fmt.Sprintf(
			`{"timestamp":"2024-01-01T00:00:00Z","level":"%s","message":"event-%d","service":"api","request_id":"req-%d"}`,
			level, i, i,
		))
	}

	results := processLogEntries(entries)
	fmt.Printf("Found %d error messages\n", len(results))
}
```

**Current benchmark:**
```
BenchmarkExtractFieldUnmarshal-8    100000    12345 ns/op    1024 B/op    28 allocs/op
```

**Profiling output:**
```
go tool pprof shows: 45% in json.Unmarshal, 30% in map bucket allocation, 15% in interface boxing
```

<details>
<summary>💡 Hint</summary>

For extracting specific fields from JSON without full deserialization, you can use `json.Decoder` with `Token()` to scan the JSON stream, or use byte-level scanning to find the field directly. Another approach: use `json.RawMessage` to defer parsing of fields you do not need. For maximum performance, scan the raw bytes for the field key and extract the value without any JSON parsing at all — this works when you know the JSON structure.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"bytes"
	"fmt"
)

// Fast version — zero-allocation field extraction via byte scanning
// This works for flat JSON objects with string values.
func extractField(jsonData []byte, field string) (string, bool) {
	// Build the search key: "field":
	key := []byte(`"` + field + `":`)

	idx := bytes.Index(jsonData, key)
	if idx == -1 {
		return "", false
	}

	// Move past the key
	start := idx + len(key)

	// Skip whitespace
	for start < len(jsonData) && (jsonData[start] == ' ' || jsonData[start] == '\t') {
		start++
	}

	if start >= len(jsonData) || jsonData[start] != '"' {
		return "", false // not a string value
	}

	// Find the closing quote (handle escaped quotes)
	start++ // skip opening quote
	end := start
	for end < len(jsonData) {
		if jsonData[end] == '\\' {
			end += 2 // skip escaped character
			continue
		}
		if jsonData[end] == '"' {
			break
		}
		end++
	}

	if end >= len(jsonData) {
		return "", false
	}

	// Return a string backed by the original byte slice — zero copy
	return string(jsonData[start:end]), true
}

func processLogEntries(entries [][]byte) []string {
	results := make([]string, 0, len(entries)/10) // estimate 10% are errors

	for _, entry := range entries {
		level, ok := extractField(entry, "level")
		if !ok {
			continue
		}
		if level == "error" {
			msg, ok := extractField(entry, "message")
			if !ok {
				continue
			}
			results = append(results, msg)
		}
	}
	return results
}

func main() {
	entries := make([][]byte, 10000)
	for i := range entries {
		level := "info"
		if i%10 == 0 {
			level = "error"
		}
		entries[i] = []byte(fmt.Sprintf(
			`{"timestamp":"2024-01-01T00:00:00Z","level":"%s","message":"event-%d","service":"api","request_id":"req-%d"}`,
			level, i, i,
		))
	}

	results := processLogEntries(entries)
	fmt.Printf("Found %d error messages\n", len(results))
}
```

**What changed:**
- `json.Unmarshal` into `map[string]interface{}` → direct byte scanning with `bytes.Index`
- No JSON parser invocation — raw byte pattern matching for the field key
- No map allocation — fields are found by scanning the byte slice
- No interface boxing — values are extracted as string slices directly
- `string(jsonData[start:end])` does allocate a string, but it is the only allocation (vs 28 allocations before)
- Pre-sized results slice to estimated count

**Optimized benchmark:**
```
BenchmarkExtractFieldByteScan-8    2000000    567 ns/op    48 B/op    2 allocs/op
```

**Improvement:** 21.8x faster, 95% less memory, 93% fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** `encoding/json` is a reflection-based decoder — it must examine the target type at runtime, allocate interface values for each JSON value, and build a Go map with hash buckets. For the common case of extracting 1-2 fields from a known JSON structure, this is massive overkill. Byte-level scanning avoids all of this. Libraries like `jsoniter`, `gjson`, and `sonic` use similar techniques (lazy parsing, zero-copy extraction) to achieve much higher throughput than the standard library.

**Go source reference:** `encoding/json/decode.go` — the `Unmarshal` function uses `reflect` to determine the target type and allocates interface values for each JSON value. The map path is especially expensive because Go maps require bucket allocation, hashing, and potential resizing.

**When to apply:** High-throughput log processing, metrics pipelines, API gateways that inspect specific fields, or any scenario where you parse JSON thousands of times per second and only need a few fields.

**When NOT to apply:** When you need the full JSON structure, when the JSON schema is complex or nested, or when correctness matters more than speed (the byte scanning approach does not validate JSON structure). For most application code, `encoding/json` is perfectly fine and much safer.

**Alternative libraries (for production use):**
- `github.com/tidwall/gjson` — fast, read-only JSON field extraction (zero-copy)
- `github.com/bytedance/sonic` — drop-in replacement for `encoding/json`, 3-5x faster
- `github.com/goccy/go-json` — another high-performance drop-in replacement

</details>

---

## Score Card

Track your progress:

| Exercise | Difficulty | Category | Found bottleneck? | Your improvement | Target improvement |
|:--------:|:---------:|:--------:|:-----------------:|:----------------:|:-----------------:|
| 1 | 🟢 | 💾 | ☐ | ___ x | 1.4x |
| 2 | 🟢 | 📦 | ☐ | ___ x | 116x |
| 3 | 🟢 | ⚡ | ☐ | ___ x | 1.5x |
| 4 | 🟡 | 📦 | ☐ | ___ x | 3.2x |
| 5 | 🟡 | ⚡ | ☐ | ___ x | 1.4x |
| 6 | 🟡 | 🔄 | ☐ | ___ x | 1.3x |
| 7 | 🟡 | 💾 | ☐ | ___ x | 25x |
| 8 | 🔴 | 📦 | ☐ | ___ x | 2.1x |
| 9 | 🔴 | ⚡ | ☐ | ___ x | 10.9x |
| 10 | 🔴 | 🔄 | ☐ | ___ x | 1.3x |
| 11 | 🔴 | 📦 | ☐ | ___ x | 21.8x |

### Rating:
- **All targets met** → You understand Go performance deeply
- **8-10 targets met** → Solid optimization skills
- **5-7 targets met** → Good foundation, practice profiling more
- **< 5 targets met** → Start with `go tool pprof` basics

---

## Optimization Cheat Sheet

Quick reference for common Go optimizations related to Go's evolution:

| Problem | Solution | Go Version | Impact |
|:--------|:---------|:----------:|:------:|
| `ioutil` deprecated functions | Use `os.ReadFile`, `os.ReadDir`, `io.ReadAll` | 1.16+ | Medium |
| String concatenation in loops | Use `strings.Builder` | 1.10+ | High |
| Lost error chains with `%s` | Use `%w` verb in `fmt.Errorf` | 1.13+ | Medium |
| Slice grows from zero capacity | Pre-allocate with `make([]T, 0, cap)` | 1.0+ | High |
| `sort.Interface` boilerplate | Use `slices.SortFunc` | 1.21+ | Medium |
| Manual channel cancellation | Use `context.WithTimeout` / `context.WithCancel` | 1.7+ | Medium |
| Unbuffered file I/O | Use `bufio.Reader` / `bufio.Writer` | 1.0+ | High |
| Repeated buffer allocations | Use `sync.Pool` (improved with victim cache) | 1.13+ | High |
| Mutex for simple counters | Use `atomic.Int64` typed wrappers | 1.19+ | High |
| Loop variable capture bug | Upgrade to Go 1.22 — per-iteration scoping | 1.22+ | Medium |
| Full JSON unmarshal for few fields | Byte scanning or `gjson` library | Any | High |

### Performance Investigation Tools

| Tool | Command | Use For |
|:-----|:--------|:-------|
| Benchmark | `go test -bench=. -benchmem` | Measure ns/op, B/op, allocs/op |
| CPU Profile | `go test -cpuprofile=cpu.prof` | Find hot functions |
| Memory Profile | `go test -memprofile=mem.prof` | Find allocation sources |
| Trace | `go test -trace=trace.out` | Visualize goroutine scheduling |
| Escape Analysis | `go build -gcflags='-m'` | See what escapes to heap |
| pprof Web UI | `go tool pprof -http=:8080 cpu.prof` | Interactive flame graphs |

### Go Version Performance Milestones

| Version | Year | Performance Impact |
|:--------|:----:|:-------------------|
| Go 1.5 | 2015 | Concurrent GC — reduced pause times from 300ms to <10ms |
| Go 1.7 | 2016 | SSA compiler backend — 5-35% faster generated code |
| Go 1.10 | 2018 | `strings.Builder` — zero-copy string building |
| Go 1.13 | 2019 | `sync.Pool` victim cache — pools survive GC cycles |
| Go 1.14 | 2020 | Async preemption — no more infinite loop stalls |
| Go 1.16 | 2021 | `io/fs`, `os.ReadDir` — modern file system APIs |
| Go 1.17 | 2021 | Register-based calling convention — 5% faster on amd64 |
| Go 1.18 | 2022 | Generics — type-safe, zero-boxing generic code |
| Go 1.19 | 2022 | Typed atomics, pdqsort, soft memory limit (`GOMEMLIMIT`) |
| Go 1.20 | 2023 | PGO (Profile-Guided Optimization) — 2-7% improvement |
| Go 1.21 | 2023 | `slices`, `maps`, `cmp` packages in stdlib |
| Go 1.22 | 2024 | Per-iteration loop variables, enhanced routing |
