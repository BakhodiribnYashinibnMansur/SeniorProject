# History of Go — Find the Bug

> **Practice finding and fixing bugs in Go code related to History of Go.**
> Each exercise contains buggy code — your job is to find the bug, explain why it happens, and fix it.
> These bugs are specifically tied to Go's evolution: version-specific behaviors, deprecated packages, module system changes, and breaking changes across releases.

---

## How to Use

1. Read the buggy code carefully
2. Try to find the bug **without** looking at the hint
3. Write the fix yourself before checking the solution
4. Understand **why** the bug happens — not just how to fix it

### Difficulty Levels

| Level | Description |
|:-----:|:-----------|
| 🟢 | **Easy** — Common beginner mistakes, deprecated API usage |
| 🟡 | **Medium** — Subtle version-specific behavior, logic errors from old idioms |
| 🔴 | **Hard** — Race conditions from pre-1.22 semantics, module system edge cases, runtime behavioral changes |

---

## Bug 1: Using Deprecated `ioutil.ReadAll` 🟢

**What the code should do:** Read the contents of a string reader and print the result.

```go
package main

import (
	"fmt"
	"io/ioutil"
	"strings"
)

func main() {
	r := strings.NewReader("Hello from Go history!")
	data, err := ioutil.ReadAll(r)
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	fmt.Println(string(data))
}
```

**Expected output:**
```
Hello from Go history!
```

**Actual output:**
```
Hello from Go history!
```

*(Output is correct, but the code uses a deprecated package — this will cause warnings, linter failures, and eventual removal.)*

<details>
<summary>💡 Hint</summary>

The `io/ioutil` package was deprecated in Go 1.16 (released February 2021). All its functions were moved to `io` and `os` packages. Check which package now provides `ReadAll`.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The code uses `ioutil.ReadAll` from the deprecated `io/ioutil` package.
**Why it happens:** In Go 1.16, the Go team deprecated the entire `io/ioutil` package. `ioutil.ReadAll` was moved to `io.ReadAll`, `ioutil.ReadFile` to `os.ReadFile`, `ioutil.WriteFile` to `os.WriteFile`, `ioutil.TempDir` to `os.MkdirTemp`, and `ioutil.TempFile` to `os.CreateTemp`. The old functions still work (they are thin wrappers) but are officially deprecated.
**Impact:** Code compiles and runs correctly today, but using deprecated APIs means: linters like `staticcheck` flag it (SA1019), future Go versions may remove it, and it signals outdated code to reviewers.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"fmt"
	"io"
	"strings"
)

func main() {
	r := strings.NewReader("Hello from Go history!")
	// io.ReadAll replaced ioutil.ReadAll in Go 1.16
	data, err := io.ReadAll(r)
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	fmt.Println(string(data))
}
```

**What changed:** Replaced `ioutil.ReadAll` with `io.ReadAll` and removed the deprecated `io/ioutil` import.

</details>

---

## Bug 2: Using Deprecated `ioutil.TempDir` 🟢

**What the code should do:** Create a temporary directory, print its path, then clean up.

```go
package main

import (
	"fmt"
	"io/ioutil"
	"os"
)

func main() {
	dir, err := ioutil.TempDir("", "myapp-")
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	defer os.RemoveAll(dir)
	fmt.Println("Temp dir created:", dir)
}
```

**Expected output:**
```
Temp dir created: /tmp/myapp-123456789
```

**Actual output:**
```
Temp dir created: /tmp/myapp-123456789
```

*(Output is correct, but uses deprecated API from pre-Go 1.16 era. The replacement also has a different name.)*

<details>
<summary>💡 Hint</summary>

In Go 1.16, `ioutil.TempDir` was not just moved — it was **renamed** to `os.MkdirTemp`. Similarly, `ioutil.TempFile` became `os.CreateTemp`. The new names follow Go naming conventions better.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The code uses `ioutil.TempDir` which was deprecated in Go 1.16.
**Why it happens:** The Go team renamed temp-related functions during the deprecation: `ioutil.TempDir` -> `os.MkdirTemp` and `ioutil.TempFile` -> `os.CreateTemp`. Unlike `ReadAll` (which kept the same name in `io`), these got new names to better follow Go conventions (`MkdirTemp` reads as "make directory, temporary").
**Impact:** Linter warnings, signals outdated codebase, and the old function is a wrapper that may be removed in a future major change.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"fmt"
	"os"
)

func main() {
	// os.MkdirTemp replaced ioutil.TempDir in Go 1.16
	dir, err := os.MkdirTemp("", "myapp-")
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	defer os.RemoveAll(dir)
	fmt.Println("Temp dir created:", dir)
}
```

**What changed:** Replaced `ioutil.TempDir` with `os.MkdirTemp` and removed the `io/ioutil` import entirely.

</details>

---

## Bug 3: Old-Style String to Byte Slice Conversion 🟢

**What the code should do:** Convert a string to a byte slice using the modern approach and print its length.

```go
package main

import (
	"fmt"
	"reflect"
	"unsafe"
)

func main() {
	s := "Go was created in 2007"

	// "Efficient" zero-copy string to []byte conversion
	// (old trick from pre-Go 1.17 era)
	sh := (*reflect.StringHeader)(unsafe.Pointer(&s))
	bh := reflect.SliceHeader{
		Data: sh.Data,
		Len:  sh.Len,
		Cap:  sh.Len,
	}
	b := *(*[]byte)(unsafe.Pointer(&bh))

	fmt.Println("Length:", len(b))
	fmt.Println("Content:", string(b))
}
```

**Expected output:**
```
Length: 22
Content: Go was created in 2007
```

**Actual output:**
```
Length: 22
Content: Go was created in 2007
```

*(Appears to work, but uses `reflect.StringHeader` and `reflect.SliceHeader` which are deprecated since Go 1.20 and can cause GC-related crashes.)*

<details>
<summary>💡 Hint</summary>

`reflect.StringHeader` and `reflect.SliceHeader` were deprecated in Go 1.20. The `unsafe.StringData`, `unsafe.SliceData`, and `unsafe.Slice` functions were added in Go 1.17+. But even simpler: do you really need unsafe at all?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The code uses deprecated `reflect.StringHeader` and `reflect.SliceHeader` for string-to-byte conversion.
**Why it happens:** Before Go 1.17, developers used `reflect.StringHeader`/`reflect.SliceHeader` with `unsafe.Pointer` for zero-copy conversions. In Go 1.20, these types were deprecated because they do not properly keep references alive for the garbage collector — the GC can collect the underlying data while the header still references it, causing use-after-free bugs.
**Impact:** Can cause silent memory corruption or crashes under GC pressure. The GC may collect the original string's data because the `reflect.SliceHeader` does not create a proper reference.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"fmt"
)

func main() {
	s := "Go was created in 2007"

	// Simple, safe conversion — Go compiler optimizes this well
	b := []byte(s)

	fmt.Println("Length:", len(b))
	fmt.Println("Content:", string(b))
}
```

**What changed:** Replaced the unsafe `reflect.StringHeader`/`reflect.SliceHeader` trick with a simple `[]byte(s)` conversion. If zero-copy is truly needed in Go 1.20+, use `unsafe.Slice(unsafe.StringData(s), len(s))` — but for most cases, the simple conversion is fast enough and GC-safe.

</details>

---

## Bug 4: Loop Variable Capture in Goroutines (Pre-Go 1.22 Bug) 🟡

**What the code should do:** Launch 5 goroutines, each printing its own index (0 through 4).

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
			fmt.Println("goroutine:", i)
		}()
	}

	wg.Wait()
}
```

**Expected output:**
```
goroutine: 0
goroutine: 1
goroutine: 2
goroutine: 3
goroutine: 4
```

**Actual output (Go < 1.22):**
```
goroutine: 5
goroutine: 5
goroutine: 5
goroutine: 5
goroutine: 5
```

<details>
<summary>💡 Hint</summary>

Before Go 1.22, the loop variable `i` was shared across all iterations — it was declared once and mutated. By the time goroutines execute, the loop has already finished and `i` is 5. Go 1.22 changed this behavior so each iteration gets its own variable.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** All goroutines capture the same loop variable `i` by reference, not by value.
**Why it happens:** In Go versions before 1.22, the `for` loop declared the variable `i` once and reused it across iterations. Each closure captured a reference to the same variable. Since goroutines are scheduled asynchronously, by the time they execute, the loop has completed and `i` holds the final value (5). This was one of the most infamous Go gotchas, discussed since the language's earliest days.
**Impact:** All goroutines print the same (wrong) value. In Go 1.22+, this was fixed — each loop iteration gets a fresh variable. But code targeting Go < 1.22 (or with `go 1.21` in go.mod) still has this bug.

</details>

<details>
<summary>✅ Fixed Code</summary>

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
		// Fix 1: Pass i as a parameter to the closure (works in ALL Go versions)
		go func(n int) {
			defer wg.Done()
			fmt.Println("goroutine:", n)
		}(i)
	}

	wg.Wait()
}
```

**What changed:** Pass `i` as a function argument to the goroutine closure. This copies the value at each iteration. Alternative fix (Go < 1.22): `i := i` inside the loop body to shadow the variable.

</details>

---

## Bug 5: Range Loop Variable Capture with Pointers (Pre-Go 1.22) 🟡

**What the code should do:** Create a slice of pointers to each element's value from a struct slice.

```go
package main

import "fmt"

type Version struct {
	Number string
	Year   int
}

func main() {
	versions := []Version{
		{"Go 1.0", 2012},
		{"Go 1.11", 2018},
		{"Go 1.22", 2024},
	}

	var ptrs []*Version
	for _, v := range versions {
		ptrs = append(ptrs, &v)
	}

	for _, p := range ptrs {
		fmt.Printf("%s (%d)\n", p.Number, p.Year)
	}
}
```

**Expected output:**
```
Go 1.0 (2012)
Go 1.11 (2018)
Go 1.22 (2024)
```

**Actual output (Go < 1.22):**
```
Go 1.22 (2024)
Go 1.22 (2024)
Go 1.22 (2024)
```

<details>
<summary>💡 Hint</summary>

This is the pointer variant of the classic loop variable capture bug. Before Go 1.22, `v` in `for _, v := range` was a single variable reused across iterations. All pointers `&v` point to the same memory location, which holds the last value after the loop finishes.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** All pointers in `ptrs` point to the same loop variable `v`, which holds the last element after the loop.
**Why it happens:** Before Go 1.22, the range loop created one `v` variable and assigned each element to it in sequence. Taking `&v` gives the address of that single variable, so all pointers are identical. After the loop, `v` holds `{"Go 1.22", 2024}`, so all pointers dereference to the same value.
**Impact:** All elements in the pointer slice reference the same (last) value. This bug was so common it had its own Go wiki entry. Go 1.22 fixes this by creating a new variable per iteration.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

type Version struct {
	Number string
	Year   int
}

func main() {
	versions := []Version{
		{"Go 1.0", 2012},
		{"Go 1.11", 2018},
		{"Go 1.22", 2024},
	}

	var ptrs []*Version
	for i := range versions {
		// Take pointer to the slice element directly, not the loop variable
		ptrs = append(ptrs, &versions[i])
	}

	for _, p := range ptrs {
		fmt.Printf("%s (%d)\n", p.Number, p.Year)
	}
}
```

**What changed:** Instead of taking `&v` (address of loop variable), use `&versions[i]` to reference the actual slice element. Alternative: `v := v` to shadow the variable before taking its address.

</details>

---

## Bug 6: Using `os.IsNotExist` Instead of `errors.Is` 🟡

**What the code should do:** Check if a file exists using modern error handling idioms.

```go
package main

import (
	"fmt"
	"os"
)

type wrappedError struct {
	msg string
	err error
}

func (e *wrappedError) Error() string { return e.msg + ": " + e.err.Error() }
func (e *wrappedError) Unwrap() error { return e.err }

func openConfig() error {
	_, err := os.Open("/nonexistent/config.yaml")
	if err != nil {
		return &wrappedError{msg: "failed to open config", err: err}
	}
	return nil
}

func main() {
	err := openConfig()
	if err != nil {
		// Pre-Go 1.13 style error checking
		if os.IsNotExist(err) {
			fmt.Println("Config file not found, using defaults")
		} else {
			fmt.Println("Unexpected error:", err)
		}
	}
}
```

**Expected output:**
```
Config file not found, using defaults
```

**Actual output:**
```
Unexpected error: failed to open config: open /nonexistent/config.yaml: no such file or directory
```

<details>
<summary>💡 Hint</summary>

`os.IsNotExist` does not unwrap errors. It was designed before Go 1.13 introduced error wrapping with `fmt.Errorf("%w", err)` and `errors.Is`/`errors.As`. When the original error is wrapped, `os.IsNotExist` cannot see through the wrapper.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `os.IsNotExist(err)` returns `false` because the error is wrapped in a custom type.
**Why it happens:** `os.IsNotExist` predates Go 1.13's error wrapping conventions. It checks the top-level error directly — it does not call `Unwrap()` to traverse the error chain. When `openConfig()` wraps the `*os.PathError` inside `wrappedError`, `os.IsNotExist` cannot find the underlying `syscall.ENOENT`. Go 1.13 introduced `errors.Is` which traverses the full error chain.
**Impact:** The "file not found" condition is never detected, causing the program to treat a missing config file as an unexpected error instead of falling back to defaults.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"errors"
	"fmt"
	"os"
)

type wrappedError struct {
	msg string
	err error
}

func (e *wrappedError) Error() string { return e.msg + ": " + e.err.Error() }
func (e *wrappedError) Unwrap() error { return e.err }

func openConfig() error {
	_, err := os.Open("/nonexistent/config.yaml")
	if err != nil {
		return &wrappedError{msg: "failed to open config", err: err}
	}
	return nil
}

func main() {
	err := openConfig()
	if err != nil {
		// Go 1.13+ style: errors.Is traverses the error chain via Unwrap()
		if errors.Is(err, os.ErrNotExist) {
			fmt.Println("Config file not found, using defaults")
		} else {
			fmt.Println("Unexpected error:", err)
		}
	}
}
```

**What changed:** Replaced `os.IsNotExist(err)` with `errors.Is(err, os.ErrNotExist)`. The `errors.Is` function (added in Go 1.13) calls `Unwrap()` recursively to check the entire error chain.

</details>

---

## Bug 7: Assuming `any` Type Alias Exists in Older Go 🟡

**What the code should do:** Create a generic-like function that accepts any value and prints its type (for Go versions before 1.18).

```go
package main

import "fmt"

// This code assumes "any" is available
func printType(v any) {
	fmt.Printf("Type: %T, Value: %v\n", v, v)
}

func main() {
	printType(42)
	printType("Go 1.18 introduced generics")
	printType(true)
}
```

**Expected output:**
```
Type: int, Value: 42
Type: string, Value: Go 1.18 introduced generics
Type: bool, Value: true
```

**Actual output (Go < 1.18):**
```
./main.go:5:17: undefined: any
```

<details>
<summary>💡 Hint</summary>

The `any` type alias was introduced in Go 1.18 (March 2022) as part of the generics feature. It is simply an alias for `interface{}`. If your `go.mod` specifies `go 1.17` or earlier, `any` is not recognized by the compiler.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The code uses `any` which is only available in Go 1.18+.
**Why it happens:** `any` is a predeclared type alias for `interface{}` introduced in Go 1.18. Before that version, `any` is an undefined identifier. Projects with `go 1.17` or earlier in their `go.mod` cannot use `any`. This is a common issue when copying modern Go code into older codebases or when maintaining backwards compatibility.
**Impact:** Compilation failure on Go < 1.18. This catches developers who update their code style but forget that their module's minimum Go version is older.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

// Use interface{} for compatibility with Go versions before 1.18
func printType(v interface{}) {
	fmt.Printf("Type: %T, Value: %v\n", v, v)
}

func main() {
	printType(42)
	printType("Go 1.18 introduced generics")
	printType(true)
}
```

**What changed:** Replaced `any` with `interface{}` for backward compatibility. If targeting Go 1.18+, `any` is preferred as it is more readable. Best practice: check the `go` directive in your `go.mod` before using version-specific features.

</details>

---

## Bug 8: Loop Variable Reuse in Deferred Function Calls 🔴

**What the code should do:** Defer cleanup for each opened resource, logging which resource is being closed.

```go
package main

import "fmt"

type Resource struct {
	Name string
}

func (r *Resource) Close() {
	fmt.Printf("Closing resource: %s\n", r.Name)
}

func openResource(name string) *Resource {
	fmt.Printf("Opening resource: %s\n", name)
	return &Resource{Name: name}
}

func main() {
	names := []string{"database", "cache", "queue"}

	for _, name := range names {
		r := openResource(name)
		// Bug: deferred closure captures loop variable
		defer func() {
			r.Close()
		}()
	}
}
```

**Expected output:**
```
Opening resource: database
Opening resource: cache
Opening resource: queue
Closing resource: queue
Closing resource: cache
Closing resource: database
```

**Actual output (Go < 1.22):**
```
Opening resource: database
Opening resource: cache
Opening resource: queue
Closing resource: queue
Closing resource: queue
Closing resource: queue
```

<details>
<summary>💡 Hint</summary>

This is a subtle interaction between `defer` and loop variable semantics (pre-Go 1.22). The variable `r` is redeclared in each iteration with `:=`, but the closure captures the variable, not the value. However, the real issue here is that `defer` inside a loop defers until the enclosing function returns — combined with the closure, only the last value of `r` is seen. Think about when deferred functions actually execute and what `r` points to at that time.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** In Go < 1.22, the `r` variable declared with `:=` inside the `for range` loop is actually the same variable being reassigned each iteration (this is a subtle compiler behavior with `:=` in loops pre-1.22). The deferred closures all capture the same `r`, which by the time `main` returns points to the last resource.
**Why it happens:** Before Go 1.22, even though `:=` appears to create a new variable, the loop semantics meant the variable could be reused across iterations. The deferred closures capture the variable by reference. By the time they execute (when `main` returns), `r` holds a pointer to the last resource created. Go 1.22's per-iteration variable scoping fixes this.
**Impact:** Only the last resource gets properly closed (three times), while the first two resources are never closed — causing resource leaks.
**Go spec reference:** Go 1.22 release notes: "the loop variable is created anew with each iteration"

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

type Resource struct {
	Name string
}

func (r *Resource) Close() {
	fmt.Printf("Closing resource: %s\n", r.Name)
}

func openResource(name string) *Resource {
	fmt.Printf("Opening resource: %s\n", name)
	return &Resource{Name: name}
}

func main() {
	names := []string{"database", "cache", "queue"}

	for _, name := range names {
		r := openResource(name)
		// Fix: call Close directly without a closure, passing r as a bound argument
		defer r.Close()
	}
}
```

**What changed:** Replaced the closure `defer func() { r.Close() }()` with a direct method value `defer r.Close()`. When you use a method value, Go evaluates `r` at the point of the `defer` statement and binds it, so each deferred call gets the correct resource. Alternatively, pass `r` as a parameter: `defer func(res *Resource) { res.Close() }(r)`.
**Alternative fix:** Use Go 1.22+ where loop variables are scoped per iteration, making the original closure approach work correctly.

</details>

---

## Bug 9: Misusing `context.TODO` in Production Code 🔴

**What the code should do:** Implement an HTTP handler that respects client cancellation via context, simulating a Go evolution pattern from pre-context era to modern Go.

```go
package main

import (
	"context"
	"fmt"
	"time"
)

// Simulates a legacy function from pre-Go 1.7 era (before context was in stdlib)
// that was "updated" to accept context but doesn't actually use it
func fetchFromDatabase(ctx context.Context, query string) (string, error) {
	// Bug: ignores the context completely — old pattern from when
	// context didn't exist and was bolted on later
	time.Sleep(3 * time.Second) // Simulate slow query
	return "result for: " + query, nil
}

func handleRequest(timeout time.Duration) {
	ctx, cancel := context.WithTimeout(context.Background(), timeout)
	defer cancel()

	// This should respect the 500ms timeout
	result, err := fetchFromDatabase(ctx, "SELECT * FROM versions")
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	fmt.Println("Got:", result)
}

func main() {
	start := time.Now()
	handleRequest(500 * time.Millisecond)
	fmt.Printf("Took: %v\n", time.Since(start).Round(time.Millisecond))
}
```

**Expected output:**
```
Error: context deadline exceeded
Took: 500ms
```

**Actual output:**
```
Got: result for: SELECT * FROM versions
Took: 3000ms
```

<details>
<summary>💡 Hint</summary>

The `context.Context` parameter is accepted but never checked. This is a common pattern in codebases that migrated from pre-Go 1.7 (when `context` was in `golang.org/x/net/context`) — functions were updated to accept `context.Context` as a parameter but the implementation was never updated to actually respect cancellation.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `fetchFromDatabase` accepts a `context.Context` but completely ignores it — the `time.Sleep` blocks for the full duration regardless of context cancellation.
**Why it happens:** This is a historical migration artifact. Before Go 1.7, `context` was an external package (`golang.org/x/net/context`). When it was moved to the standard library, many codebases added `ctx context.Context` as a first parameter to match the new convention, but never implemented the actual cancellation logic. The function signature looks modern but the implementation is pre-context era.
**Impact:** Timeouts and cancellations are silently ignored, causing request handlers to hang far beyond their deadlines. This leads to goroutine accumulation, resource exhaustion, and cascading failures in production.
**How to detect:** Use `go vet` with context-checking analyzers, or search for functions that accept `context.Context` but never reference `ctx` in the body.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"context"
	"fmt"
	"time"
)

// Properly context-aware function
func fetchFromDatabase(ctx context.Context, query string) (string, error) {
	// Use select to respect context cancellation
	select {
	case <-time.After(3 * time.Second): // Simulate slow query
		return "result for: " + query, nil
	case <-ctx.Done():
		return "", ctx.Err()
	}
}

func handleRequest(timeout time.Duration) {
	ctx, cancel := context.WithTimeout(context.Background(), timeout)
	defer cancel()

	result, err := fetchFromDatabase(ctx, "SELECT * FROM versions")
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	fmt.Println("Got:", result)
}

func main() {
	start := time.Now()
	handleRequest(500 * time.Millisecond)
	fmt.Printf("Took: %v\n", time.Since(start).Round(time.Millisecond))
}
```

**What changed:** Replaced `time.Sleep` with a `select` statement that races between the work completing (`time.After`) and the context being cancelled (`ctx.Done()`). Now the function respects the caller's timeout/cancellation.

</details>

---

## Bug 10: Goroutine Leak from Old-Style Channel Patterns 🔴

**What the code should do:** Fetch data from multiple "Go version" sources concurrently with a timeout, returning the first successful result.

```go
package main

import (
	"fmt"
	"time"
)

func fetchVersion(source string, delay time.Duration) string {
	time.Sleep(delay)
	return fmt.Sprintf("Go 1.22 (from %s)", source)
}

func getFirstResult() string {
	// Old-style pattern: unbuffered channel, multiple goroutines
	ch := make(chan string)

	go func() {
		ch <- fetchVersion("mirror-1", 2*time.Second)
	}()
	go func() {
		ch <- fetchVersion("mirror-2", 100*time.Millisecond)
	}()
	go func() {
		ch <- fetchVersion("mirror-3", 5*time.Second)
	}()

	// Only read the first result
	select {
	case result := <-ch:
		return result
	case <-time.After(3 * time.Second):
		return "timeout"
	}
}

func main() {
	result := getFirstResult()
	fmt.Println("Result:", result)

	// Give goroutines time to show the leak
	time.Sleep(6 * time.Second)
	fmt.Println("Done — but leaked goroutines are still blocked!")
}
```

**Expected output:**
```
Result: Go 1.22 (from mirror-2)
Done — but leaked goroutines are still blocked!
```

**Actual output:**
```
Result: Go 1.22 (from mirror-2)
Done — but leaked goroutines are still blocked!
```

*(Output looks correct, but 2 goroutines are permanently leaked — they are blocked trying to send on the unbuffered channel that nobody is reading from.)*

<details>
<summary>💡 Hint</summary>

The channel `ch` is unbuffered (`make(chan string)`). After the first result is read, the other two goroutines are permanently blocked on `ch <-` because there is no receiver. This is a goroutine leak — a pattern that was extremely common in early Go code before best practices around structured concurrency and `context` were established.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** Two goroutines leak permanently because they are blocked sending on an unbuffered channel with no receiver.
**Why it happens:** This is a pre-context era concurrency pattern. The unbuffered channel `make(chan string)` requires a receiver for each send. After `getFirstResult` reads one value and returns, the other two goroutines are stuck on `ch <-` forever. They cannot be garbage collected because the goroutine stack holds a reference. This pattern was common in Go's early days (2009-2014) before `context.Context`, `errgroup`, and structured concurrency patterns were established.
**Impact:** Each call to `getFirstResult` leaks N-1 goroutines (where N is the number of concurrent fetchers). In a long-running server, this causes unbounded goroutine growth, memory exhaustion, and eventual OOM crash.
**How to detect:** Monitor `runtime.NumGoroutine()`, use `pprof` goroutine profile, or use `goleak` in tests.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"context"
	"fmt"
	"time"
)

func fetchVersion(ctx context.Context, source string, delay time.Duration) (string, error) {
	select {
	case <-time.After(delay):
		return fmt.Sprintf("Go 1.22 (from %s)", source), nil
	case <-ctx.Done():
		return "", ctx.Err()
	}
}

func getFirstResult() string {
	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel() // Cancels context, causing all goroutines to exit

	// Buffered channel: goroutines can send even if nobody reads
	ch := make(chan string, 3)

	sources := []struct {
		name  string
		delay time.Duration
	}{
		{"mirror-1", 2 * time.Second},
		{"mirror-2", 100 * time.Millisecond},
		{"mirror-3", 5 * time.Second},
	}

	for _, s := range sources {
		go func(name string, d time.Duration) {
			result, err := fetchVersion(ctx, name, d)
			if err != nil {
				return // Context cancelled, exit goroutine cleanly
			}
			ch <- result
		}(s.name, s.delay)
	}

	return <-ch
}

func main() {
	result := getFirstResult()
	fmt.Println("Result:", result)

	time.Sleep(1 * time.Second)
	fmt.Println("Done — no leaked goroutines!")
}
```

**What changed:** (1) Used a **buffered channel** `make(chan string, 3)` so goroutines can send without blocking even if no one reads. (2) Added **context cancellation** so all goroutines exit cleanly when the first result is received (`defer cancel()`). (3) Made `fetchVersion` context-aware so it can be interrupted. This is the modern Go pattern for fan-out/fan-in concurrency.

</details>

---

## Score Card

Track your progress:

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
- **10/10 without hints** — Senior-level debugging skills, deep knowledge of Go's evolution
- **7-9/10** — Solid middle-level understanding of Go's history and version changes
- **4-6/10** — Good junior, keep exploring Go's release notes and changelogs
- **< 4/10** — Review the History of Go fundamentals and read Go release notes

### Key Go Evolution Timeline Covered in These Bugs:
| Version | Year | Feature/Change |
|:--------|:-----|:---------------|
| Go 1.7 | 2016 | `context` package added to stdlib |
| Go 1.13 | 2019 | `errors.Is`, `errors.As`, error wrapping with `%w` |
| Go 1.16 | 2021 | `io/ioutil` deprecated, `go:embed`, module-aware mode default |
| Go 1.17 | 2021 | `unsafe.Slice`, `unsafe.Add` |
| Go 1.18 | 2022 | Generics, `any` type alias, fuzzing |
| Go 1.20 | 2023 | `reflect.StringHeader`/`SliceHeader` deprecated |
| Go 1.22 | 2024 | Loop variable per-iteration scoping (fixes the classic capture bug) |
