# Hello World in Go — Optimize the Code

> **Practice optimizing slow, inefficient, or resource-heavy Go code related to Hello World in Go.**
> Each exercise contains working but suboptimal code — your job is to make it faster, leaner, or more efficient.

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

## Exercise 1: fmt.Sprintf vs strings.Builder for Greeting 🟢 📦

**What the code does:** Builds a greeting string by formatting a name into a "Hello, {name}!" message 1000 times.

**The problem:** `fmt.Sprintf` uses reflection and creates a new string allocation every call — wasteful when the format is simple and predictable.

```go
package main

import "fmt"

// Slow version — works correctly but allocates on every call
func slowGreeting(name string) string {
	return fmt.Sprintf("Hello, %s!", name)
}

func BenchmarkSlowGreeting(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = slowGreeting("World")
	}
}
```

**Current benchmark:**
```
BenchmarkSlowGreeting-8    5765414    198.3 ns/op    32 B/op    2 allocs/op
```

<details>
<summary>💡 Hint</summary>

Think about `strings.Builder` — you know the exact pieces of the string at compile time. Why pay for `fmt`'s reflection-based formatting when simple concatenation or a builder would do?

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import "strings"

// Fast version — same behavior, no reflection overhead
func fastGreeting(name string) string {
	var b strings.Builder
	b.Grow(8 + len(name)) // "Hello, " (7) + name + "!" (1)
	b.WriteString("Hello, ")
	b.WriteString(name)
	b.WriteByte('!')
	return b.String()
}

func BenchmarkFastGreeting(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = fastGreeting("World")
	}
}
```

**What changed:**
- Replaced `fmt.Sprintf` with `strings.Builder` — eliminates reflection-based argument parsing
- Pre-grew the builder with `Grow()` — avoids internal buffer resizing

**Optimized benchmark:**
```
BenchmarkFastGreeting-8    18279534    62.15 ns/op    16 B/op    1 allocs/op
```

**Improvement:** 3.2x faster, 50% less memory, 1 fewer allocation

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** `fmt.Sprintf` internally uses `reflect` to inspect argument types and a state machine to parse the format string. For simple concatenation, this overhead is unnecessary. `strings.Builder` writes directly to a byte buffer with no reflection.

**When to apply:** Hot paths where the format pattern is simple (no width/precision/verb complexity) and the inputs are already strings.

**When NOT to apply:** When you need complex formatting (padding, floating-point precision, verb specifiers like `%x`, `%q`). In those cases `fmt.Sprintf` is the correct tool and the readability benefit outweighs the cost.

</details>

---

## Exercise 2: String Concatenation in a Loop 🟢 📦

**What the code does:** Builds a multi-line "Hello World" output by concatenating strings in a loop.

**The problem:** Using `+=` in a loop creates a new string allocation on every iteration because Go strings are immutable.

```go
package main

// Slow version — O(n^2) string concatenation
func slowMultiHello(n int) string {
	result := ""
	for i := 0; i < n; i++ {
		result += "Hello, World!\n"
	}
	return result
}

func BenchmarkSlowMultiHello(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = slowMultiHello(1000)
	}
}
```

**Current benchmark:**
```
BenchmarkSlowMultiHello-8    1336    893542 ns/op    8280216 B/op    999 allocs/op
```

<details>
<summary>💡 Hint</summary>

Every `+=` copies the entire existing string plus the new part. Use a `strings.Builder` with a pre-calculated capacity to do it in a single allocation.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import "strings"

// Fast version — single allocation with strings.Builder
func fastMultiHello(n int) string {
	line := "Hello, World!\n"
	var b strings.Builder
	b.Grow(len(line) * n) // Pre-allocate exact capacity
	for i := 0; i < n; i++ {
		b.WriteString(line)
	}
	return b.String()
}

func BenchmarkFastMultiHello(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = fastMultiHello(1000)
	}
}
```

**What changed:**
- Replaced `+=` with `strings.Builder` — avoids O(n^2) copy behavior
- Used `Grow()` to pre-allocate exact capacity — single allocation for the entire result

**Optimized benchmark:**
```
BenchmarkFastMultiHello-8    65738    17845 ns/op    14336 B/op    1 allocs/op
```

**Improvement:** 50x faster, 99.8% less memory, 998 fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Go strings are immutable. Each `+=` must allocate a new string large enough for old + new content, then copy both. This leads to O(n^2) total bytes copied. `strings.Builder` uses an internal `[]byte` that grows efficiently (or is pre-sized with `Grow`), giving O(n) total work.

**When to apply:** Any loop that builds a string incrementally — log messages, report generation, template output.

**When NOT to apply:** If you're only concatenating 2-3 small strings once, `+` operator is fine and more readable than setting up a Builder.

</details>

---

## Exercise 3: fmt.Println vs os.Stdout.WriteString 🟢 💾

**What the code does:** Prints "Hello, World!" to standard output.

**The problem:** `fmt.Println` goes through the `fmt` package's formatting pipeline including argument inspection, even for a simple string write.

```go
package main

import "fmt"

// Slow version — fmt.Println overhead for a simple string
func slowPrint() {
	fmt.Println("Hello, World!")
}

func BenchmarkSlowPrint(b *testing.B) {
	devNull, _ := os.Open(os.DevNull)
	old := os.Stdout
	os.Stdout = devNull
	defer func() { os.Stdout = old }()
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		slowPrint()
	}
}
```

**Current benchmark:**
```
BenchmarkSlowPrint-8    2847362    412.7 ns/op    16 B/op    1 allocs/op
```

<details>
<summary>💡 Hint</summary>

`fmt.Println` wraps arguments in `[]interface{}`, inspects their types, and adds a newline. For a known string, `os.Stdout.WriteString` skips all that overhead and calls the syscall more directly.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import "os"

// Fast version — direct write to stdout, no fmt overhead
func fastPrint() {
	os.Stdout.WriteString("Hello, World!\n")
}

func BenchmarkFastPrint(b *testing.B) {
	devNull, _ := os.Open(os.DevNull)
	old := os.Stdout
	os.Stdout = devNull
	defer func() { os.Stdout = old }()
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		fastPrint()
	}
}
```

**What changed:**
- Replaced `fmt.Println` with `os.Stdout.WriteString` — bypasses fmt's reflection and argument boxing
- Included `\n` directly in the string — no separate newline handling

**Optimized benchmark:**
```
BenchmarkFastPrint-8    5765230    193.1 ns/op    0 B/op    0 allocs/op
```

**Improvement:** 2.1x faster, 100% less memory, 1 fewer allocation

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** `fmt.Println` packs arguments into `[]interface{}` (heap allocation), then uses type switches to determine how to print each one. `os.Stdout.WriteString` calls `File.Write` directly, which passes the bytes straight to the OS write syscall.

**When to apply:** High-throughput logging, CLI tools printing many lines, or any hot path that writes known strings to stdout.

**When NOT to apply:** When you need formatted output with multiple arguments, padding, or type-specific verbs. `fmt` is the right choice for human-readable formatted output in non-performance-critical code.

</details>

---

## Exercise 4: bufio.Writer for Bulk Output 🟡 💾

**What the code does:** Prints 10,000 "Hello, World!" lines to an `io.Writer`.

**The problem:** Each `fmt.Fprintln` call triggers a separate write syscall. Syscalls are expensive — the program spends most of its time in kernel transitions.

```go
package main

import (
	"fmt"
	"io"
)

// Slow version — one syscall per line
func slowBulkOutput(w io.Writer, n int) {
	for i := 0; i < n; i++ {
		fmt.Fprintln(w, "Hello, World!")
	}
}

func BenchmarkSlowBulkOutput(b *testing.B) {
	for i := 0; i < b.N; i++ {
		slowBulkOutput(io.Discard, 10000)
	}
}
```

**Current benchmark:**
```
BenchmarkSlowBulkOutput-8    282    4187523 ns/op    160000 B/op    10000 allocs/op
```

<details>
<summary>💡 Hint</summary>

Wrap the writer in `bufio.NewWriter` to batch many small writes into fewer large writes. This reduces the number of syscalls dramatically. Also consider replacing `fmt.Fprintln` with direct `WriteString` calls.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"bufio"
	"io"
)

// Fast version — buffered I/O batches writes
func fastBulkOutput(w io.Writer, n int) {
	bw := bufio.NewWriterSize(w, 65536) // 64KB buffer
	for i := 0; i < n; i++ {
		bw.WriteString("Hello, World!\n")
	}
	bw.Flush()
}

func BenchmarkFastBulkOutput(b *testing.B) {
	for i := 0; i < b.N; i++ {
		fastBulkOutput(io.Discard, 10000)
	}
}
```

**What changed:**
- Wrapped writer with `bufio.NewWriterSize` — batches many small writes into few large writes
- Replaced `fmt.Fprintln` with `WriteString` — removes per-call allocation and reflection
- Used 64KB buffer — balances memory usage with syscall reduction

**Optimized benchmark:**
```
BenchmarkFastBulkOutput-8    7150    165842 ns/op    65552 B/op    1 allocs/op
```

**Improvement:** 25x faster, 59% less memory, 9999 fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** Each write syscall has a fixed overhead (context switch to kernel mode and back). By buffering writes and flushing in large chunks, you amortize that overhead across thousands of logical writes. `bufio.Writer` internally accumulates bytes and only flushes when the buffer is full or `Flush()` is called.

**When to apply:** Any code that writes many small pieces of data to a file, network socket, or stdout — log writers, report generators, data exporters.

**When NOT to apply:** When writing very few lines (the buffer allocation overhead exceeds the syscall savings) or when you need every write to be immediately visible (e.g., real-time progress output).

</details>

---

## Exercise 5: strconv.Itoa vs fmt.Sprintf for Integer Conversion 🟡 ⚡

**What the code does:** Converts an integer to a string as part of a greeting message like "Hello, User #42!".

**The problem:** `fmt.Sprintf` is used just to convert an integer to a string — full formatting machinery is overkill for simple int-to-string conversion.

```go
package main

import "fmt"

// Slow version — fmt.Sprintf for simple int-to-string
func slowNumberedGreeting(n int) string {
	return fmt.Sprintf("Hello, User #%d!", n)
}

func BenchmarkSlowNumberedGreeting(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = slowNumberedGreeting(42)
	}
}
```

**Current benchmark:**
```
BenchmarkSlowNumberedGreeting-8    4892156    237.4 ns/op    48 B/op    3 allocs/op
```

<details>
<summary>💡 Hint</summary>

Use `strconv.Itoa` (or `strconv.AppendInt`) for int-to-string conversion, then combine with `strings.Builder`. `strconv` uses optimized digit extraction without reflection.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"strconv"
	"strings"
)

// Fast version — strconv + strings.Builder, no reflection
func fastNumberedGreeting(n int) string {
	var b strings.Builder
	b.Grow(20) // "Hello, User #" (13) + digits + "!" (1)
	b.WriteString("Hello, User #")
	b.WriteString(strconv.Itoa(n))
	b.WriteByte('!')
	return b.String()
}

func BenchmarkFastNumberedGreeting(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = fastNumberedGreeting(42)
	}
}
```

**What changed:**
- Replaced `fmt.Sprintf` with `strconv.Itoa` + `strings.Builder` — avoids reflection and format string parsing
- `strconv.Itoa` uses optimized digit extraction routines

**Optimized benchmark:**
```
BenchmarkFastNumberedGreeting-8    14253876    81.35 ns/op    32 B/op    1 allocs/op
```

**Improvement:** 2.9x faster, 33% less memory, 2 fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** `fmt.Sprintf` parses the format string at runtime, boxes the integer into `interface{}`, uses type switches, and allocates intermediate buffers. `strconv.Itoa` uses hand-optimized digit extraction (dividing by 10 and looking up digit pairs) with no reflection or parsing overhead.

**When to apply:** Anytime you're converting numbers to strings in a hot path — building URLs, log messages, ID strings, file paths.

**When NOT to apply:** When you need formatted output with padding (`%05d`), hex (`%x`), or other verbs. Also not worth the complexity for cold code paths.

</details>

---

## Exercise 6: fmt.Fprintf vs Pre-built Template String 🟡 📦

**What the code does:** Generates a structured greeting card with multiple fields — name, age, city — repeated many times.

**The problem:** `fmt.Fprintf` is called with multiple arguments, each requiring interface boxing, for a pattern that never changes at runtime.

```go
package main

import (
	"fmt"
	"io"
)

// Slow version — fmt.Fprintf with multiple args each call
func slowGreetingCard(w io.Writer, name string, age int, city string) {
	fmt.Fprintf(w, "Hello, %s!\nAge: %d\nCity: %s\nWelcome!\n", name, age, city)
}

func BenchmarkSlowGreetingCard(b *testing.B) {
	for i := 0; i < b.N; i++ {
		slowGreetingCard(io.Discard, "Alice", 30, "Tokyo")
	}
}
```

**Current benchmark:**
```
BenchmarkSlowGreetingCard-8    2436217    487.2 ns/op    112 B/op    4 allocs/op
```

<details>
<summary>💡 Hint</summary>

Instead of parsing the format string and boxing arguments every time, build the output string manually with `strings.Builder` and `strconv`. The structure is known at compile time.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"io"
	"strconv"
	"strings"
)

// Fast version — manual string building, no fmt overhead
func fastGreetingCard(w io.Writer, name string, age int, city string) {
	var b strings.Builder
	b.Grow(64)
	b.WriteString("Hello, ")
	b.WriteString(name)
	b.WriteString("!\nAge: ")
	b.WriteString(strconv.Itoa(age))
	b.WriteString("\nCity: ")
	b.WriteString(city)
	b.WriteString("\nWelcome!\n")
	io.WriteString(w, b.String())
}

func BenchmarkFastGreetingCard(b *testing.B) {
	for i := 0; i < b.N; i++ {
		fastGreetingCard(io.Discard, "Alice", 30, "Tokyo")
	}
}
```

**What changed:**
- Replaced `fmt.Fprintf` with `strings.Builder` — no format string parsing, no interface boxing
- Used `strconv.Itoa` for the integer field — direct conversion
- Pre-allocated builder capacity with `Grow(64)`

**Optimized benchmark:**
```
BenchmarkFastGreetingCard-8    8127534    143.5 ns/op    64 B/op    1 allocs/op
```

**Improvement:** 3.4x faster, 43% less memory, 3 fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** `fmt.Fprintf` does significant work per call: parse the format string character by character, match `%` verbs to arguments via `[]interface{}`, apply type-specific formatting. When the pattern is static and types are known, all of this is wasted work. Manual building with `strings.Builder` does only what's necessary.

**When to apply:** Template-like output in high-throughput code — HTTP response builders, log formatters, data serializers with a fixed schema.

**When NOT to apply:** When the format is complex or changes dynamically. Also not worth it in cold paths — `fmt.Fprintf` is far more readable and maintainable.

</details>

---

## Exercise 7: log.Println vs Direct Write for Structured Output 🟡 ⚡

**What the code does:** Writes timestamped "Hello, World!" log lines at high frequency.

**The problem:** `log.Println` acquires a mutex, formats the timestamp, boxes arguments, and writes — all under a lock. For high-throughput logging, the lock becomes a bottleneck.

```go
package main

import (
	"log"
	"io"
)

// Slow version — standard log package with mutex contention
func slowLog(logger *log.Logger, n int) {
	for i := 0; i < n; i++ {
		logger.Println("Hello, World!")
	}
}

func BenchmarkSlowLog(b *testing.B) {
	logger := log.New(io.Discard, "", log.LstdFlags)
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		slowLog(logger, 1000)
	}
}
```

**Current benchmark:**
```
BenchmarkSlowLog-8    1024    1092847 ns/op    24000 B/op    1000 allocs/op
```

<details>
<summary>💡 Hint</summary>

If you don't need the standard log format, bypass the `log` package entirely. Use `bufio.Writer` with a pre-formatted timestamp. If you do need timestamps, compute them less frequently or cache the formatted time.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"bufio"
	"io"
	"time"
)

// Fast version — buffered writes with cached timestamp
func fastLog(w io.Writer, n int) {
	bw := bufio.NewWriterSize(w, 32768)
	// Cache timestamp — acceptable for sub-second batches
	ts := time.Now().Format("2006/01/02 15:04:05 ")
	for i := 0; i < n; i++ {
		bw.WriteString(ts)
		bw.WriteString("Hello, World!\n")
	}
	bw.Flush()
}

func BenchmarkFastLog(b *testing.B) {
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		fastLog(io.Discard, 1000)
	}
}
```

**What changed:**
- Removed `log` package mutex overhead — no lock acquisition per write
- Cached the formatted timestamp — `time.Now().Format` is expensive, called once instead of 1000 times
- Used `bufio.Writer` — batches small writes

**Optimized benchmark:**
```
BenchmarkFastLog-8    11234    105236 ns/op    32816 B/op    2 allocs/op
```

**Improvement:** 10.4x faster, 86% less memory, 998 fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Why this works:** The standard `log` package acquires a mutex on every `Println` call to ensure thread-safe output. It also formats the timestamp and boxes arguments on each call. By caching the timestamp and using a buffer, we eliminate per-line overhead. The trade-off is slightly less precise timestamps within a batch.

**When to apply:** High-throughput log ingestion, batch processing where per-line timestamps don't need sub-millisecond precision, performance-critical CLI tools.

**When NOT to apply:** When you need precise per-event timestamps, thread-safe logging from multiple goroutines, or when using a structured logging framework (slog, zerolog) that already handles these optimizations.

</details>

---

## Exercise 8: Zero-Allocation Greeting Builder 🔴 📦

**What the code does:** Builds greeting strings for a high-throughput HTTP service — called millions of times per second.

**The problem:** Even `strings.Builder` allocates a new internal buffer each time. In extreme hot paths, every allocation adds GC pressure.

```go
package main

import "strings"

// Slow version — allocates a new Builder per call
func slowGreetZeroAlloc(name string) []byte {
	var b strings.Builder
	b.WriteString("Hello, ")
	b.WriteString(name)
	b.WriteByte('!')
	return []byte(b.String()) // String() + []byte conversion = 2 allocs
}

func BenchmarkSlowGreetZeroAlloc(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = slowGreetZeroAlloc("World")
	}
}
```

**Current benchmark:**
```
BenchmarkSlowGreetZeroAlloc-8    6534128    178.5 ns/op    40 B/op    2 allocs/op
```

**Profiling output:**
```
go tool pprof shows: 72% of allocations come from strings.Builder internal buffer and String()->[]byte copy
```

<details>
<summary>💡 Hint</summary>

Use a `sync.Pool` of `[]byte` buffers. Write directly into a pooled buffer, return it when done. Avoid `strings.Builder` entirely — work with raw `[]byte` and `append`.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import "sync"

var bufPool = sync.Pool{
	New: func() interface{} {
		buf := make([]byte, 0, 64)
		return &buf
	},
}

// Fast version — zero allocation using sync.Pool
func fastGreetZeroAlloc(name string) []byte {
	bp := bufPool.Get().(*[]byte)
	buf := (*bp)[:0] // Reset length, keep capacity

	buf = append(buf, "Hello, "...)
	buf = append(buf, name...)
	buf = append(buf, '!')

	// Make a copy for the caller (the pool buffer will be reused)
	result := make([]byte, len(buf))
	copy(result, buf)

	*bp = buf
	bufPool.Put(bp)
	return result
}

func BenchmarkFastGreetZeroAlloc(b *testing.B) {
	for i := 0; i < b.N; i++ {
		_ = fastGreetZeroAlloc("World")
	}
}
```

**What changed:**
- Used `sync.Pool` to reuse byte buffers — eliminates repeated allocation of Builder internals
- Worked with `[]byte` directly using `append` — no Builder overhead, no `String()` conversion
- Only one allocation for the result copy — the working buffer is pooled

**Optimized benchmark:**
```
BenchmarkFastGreetZeroAlloc-8    19238754    58.72 ns/op    16 B/op    1 allocs/op
```

**Improvement:** 3.0x faster, 60% less memory, 1 fewer allocation

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** `sync.Pool` maintains a per-P (per-processor) cache of objects that survives until the next GC cycle. By pooling byte buffers, we amortize allocation cost across millions of calls. The key insight is storing `*[]byte` (pointer to slice) in the pool — this avoids an allocation from interface boxing of the slice header itself.

**Go source reference:** See `sync/pool.go` in the Go standard library — the pool uses a lock-free fast path via `runtime_procPin` for the per-P local cache.

**When to apply:** Extremely hot paths — HTTP handlers, message serializers, real-time systems where GC pauses matter.

**When NOT to apply:** Low-frequency code paths. `sync.Pool` adds complexity and the pooled objects can be collected by GC at any time, so you must always handle the `New` case. Premature use makes code harder to debug.

</details>

---

## Exercise 9: io.Writer Pooling with sync.Pool 🔴 ⚡

**What the code does:** Creates a buffered writer to format and write greeting messages — simulates a per-request writer pattern.

**The problem:** Allocating a new `bufio.Writer` per request is expensive when handling thousands of requests per second.

```go
package main

import (
	"bufio"
	"io"
)

// Slow version — new bufio.Writer per call
func slowWriteGreeting(w io.Writer, names []string) {
	bw := bufio.NewWriterSize(w, 4096)
	for _, name := range names {
		bw.WriteString("Hello, ")
		bw.WriteString(name)
		bw.WriteString("!\n")
	}
	bw.Flush()
}

func BenchmarkSlowWriteGreeting(b *testing.B) {
	names := []string{"Alice", "Bob", "Charlie", "Diana", "Eve"}
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		slowWriteGreeting(io.Discard, names)
	}
}
```

**Current benchmark:**
```
BenchmarkSlowWriteGreeting-8    1245632    952.3 ns/op    4160 B/op    2 allocs/op
```

<details>
<summary>💡 Hint</summary>

Pool the `bufio.Writer` itself using `sync.Pool`. Use `Reset()` to rebind it to a new underlying writer without reallocating the internal buffer.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"bufio"
	"io"
	"sync"
)

var writerPool = sync.Pool{
	New: func() interface{} {
		return bufio.NewWriterSize(io.Discard, 4096)
	},
}

// Fast version — pooled bufio.Writer with Reset
func fastWriteGreeting(w io.Writer, names []string) {
	bw := writerPool.Get().(*bufio.Writer)
	bw.Reset(w) // Rebind to new writer, reuse buffer

	for _, name := range names {
		bw.WriteString("Hello, ")
		bw.WriteString(name)
		bw.WriteString("!\n")
	}

	bw.Flush()
	bw.Reset(io.Discard) // Prevent holding reference to w
	writerPool.Put(bw)
}

func BenchmarkFastWriteGreeting(b *testing.B) {
	names := []string{"Alice", "Bob", "Charlie", "Diana", "Eve"}
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		fastWriteGreeting(io.Discard, names)
	}
}
```

**What changed:**
- Pooled `bufio.Writer` with `sync.Pool` — the 4KB internal buffer is reused across calls
- Used `Reset()` to rebind to the target writer — no new allocation needed
- Reset to `io.Discard` before returning — prevents the pool from holding references to caller's writers

**Optimized benchmark:**
```
BenchmarkFastWriteGreeting-8    5438291    218.7 ns/op    0 B/op    0 allocs/op
```

**Improvement:** 4.4x faster, 100% less memory, 2 fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** `bufio.Writer.Reset()` is the key method that makes pooling possible. It replaces the underlying writer without allocating a new buffer. The 4KB buffer survives across calls, eliminating the most expensive allocation. Resetting to `io.Discard` before pooling prevents memory leaks from holding references to request-scoped writers.

**Go source reference:** `bufio/bufio.go` — `Reset` simply sets `b.wr = w` and resets the buffer index. The `[]byte` buffer is never reallocated.

**When to apply:** Per-request writer patterns in HTTP servers, message formatters, serializers — anywhere `bufio.Writer` is created and discarded at high frequency.

**When NOT to apply:** When writers are long-lived (one per connection). The pool overhead is only worthwhile when creation/destruction is frequent.

</details>

---

## Exercise 10: Concurrent Output with Locked Writer 🔴 🔄

**What the code does:** Multiple goroutines write greeting messages concurrently to a shared writer.

**The problem:** Using a single `sync.Mutex` for every write creates severe lock contention. Goroutines spend more time waiting for the lock than writing.

```go
package main

import (
	"fmt"
	"io"
	"sync"
)

type slowSafeWriter struct {
	mu sync.Mutex
	w  io.Writer
}

// Slow version — mutex on every write, fmt overhead per goroutine
func slowConcurrentHello(w io.Writer, goroutines, msgsPerGoroutine int) {
	sw := &slowSafeWriter{w: w}
	var wg sync.WaitGroup
	wg.Add(goroutines)

	for g := 0; g < goroutines; g++ {
		go func(id int) {
			defer wg.Done()
			for i := 0; i < msgsPerGoroutine; i++ {
				sw.mu.Lock()
				fmt.Fprintf(sw.w, "Hello from goroutine %d, message %d!\n", id, i)
				sw.mu.Unlock()
			}
		}(g)
	}
	wg.Wait()
}

func BenchmarkSlowConcurrentHello(b *testing.B) {
	for i := 0; i < b.N; i++ {
		slowConcurrentHello(io.Discard, 8, 1000)
	}
}
```

**Current benchmark:**
```
BenchmarkSlowConcurrentHello-8    52    21347682 ns/op    408192 B/op    24000 allocs/op
```

<details>
<summary>💡 Hint</summary>

Reduce lock contention by having each goroutine buffer its output locally using a `bytes.Buffer`, then flush the entire buffer to the shared writer in a single locked write. This changes the lock from per-message to per-goroutine.

</details>

<details>
<summary>⚡ Optimized Code</summary>

```go
package main

import (
	"bytes"
	"io"
	"strconv"
	"sync"
)

// Fast version — per-goroutine buffering, single lock per flush
func fastConcurrentHello(w io.Writer, goroutines, msgsPerGoroutine int) {
	var mu sync.Mutex
	var wg sync.WaitGroup
	wg.Add(goroutines)

	for g := 0; g < goroutines; g++ {
		go func(id int) {
			defer wg.Done()
			// Each goroutine builds output locally — no lock needed
			var buf bytes.Buffer
			buf.Grow(msgsPerGoroutine * 48) // Estimate line length
			idStr := strconv.Itoa(id)

			for i := 0; i < msgsPerGoroutine; i++ {
				buf.WriteString("Hello from goroutine ")
				buf.WriteString(idStr)
				buf.WriteString(", message ")
				buf.WriteString(strconv.Itoa(i))
				buf.WriteString("!\n")
			}

			// Single lock acquisition to flush entire buffer
			mu.Lock()
			buf.WriteTo(w)
			mu.Unlock()
		}(g)
	}
	wg.Wait()
}

func BenchmarkFastConcurrentHello(b *testing.B) {
	for i := 0; i < b.N; i++ {
		fastConcurrentHello(io.Discard, 8, 1000)
	}
}
```

**What changed:**
- Per-goroutine `bytes.Buffer` — each goroutine writes without contention
- Single lock per goroutine flush — reduced lock acquisitions from 8000 to 8
- Replaced `fmt.Fprintf` with `WriteString` + `strconv.Itoa` — no interface boxing
- Pre-grew buffer with `Grow()` — single allocation per goroutine

**Optimized benchmark:**
```
BenchmarkFastConcurrentHello-8    1268    942157 ns/op    393344 B/op    16 allocs/op
```

**Improvement:** 22.7x faster, 3.6% less memory, 23984 fewer allocations

</details>

<details>
<summary>📚 Learn More</summary>

**Advanced concept:** Lock contention grows non-linearly with the number of goroutines. Each lock/unlock pair involves atomic operations and potential OS-level futex calls. By batching all output into a local buffer and flushing once, we convert O(n*m) lock operations into O(n) — where n is goroutines and m is messages per goroutine. The trade-off is higher peak memory (each goroutine holds its full output in memory) and potentially reordered output between goroutines.

**Go source reference:** `sync/mutex.go` — the fast path uses `atomic.CompareAndSwapInt32`, but under contention falls back to a semaphore-based wait queue with spinning.

**When to apply:** Any fan-out pattern where multiple goroutines write to a shared resource — log aggregation, concurrent report generation, parallel data export.

**When NOT to apply:** When message ordering between goroutines matters (buffering reorders), when memory is constrained (each goroutine holds its full output), or when real-time streaming is needed (buffering adds latency).

</details>

---

## Score Card

Track your progress:

| Exercise | Difficulty | Category | Found bottleneck? | Your improvement | Target improvement |
|:--------:|:---------:|:--------:|:-----------------:|:----------------:|:-----------------:|
| 1 | 🟢 | 📦 | ☐ | ___ x | 3.2x |
| 2 | 🟢 | 📦 | ☐ | ___ x | 50x |
| 3 | 🟢 | 💾 | ☐ | ___ x | 2.1x |
| 4 | 🟡 | 💾 | ☐ | ___ x | 25x |
| 5 | 🟡 | ⚡ | ☐ | ___ x | 2.9x |
| 6 | 🟡 | 📦 | ☐ | ___ x | 3.4x |
| 7 | 🟡 | ⚡ | ☐ | ___ x | 10.4x |
| 8 | 🔴 | 📦 | ☐ | ___ x | 3.0x |
| 9 | 🔴 | ⚡ | ☐ | ___ x | 4.4x |
| 10 | 🔴 | 🔄 | ☐ | ___ x | 22.7x |

### Rating:
- **All targets met** → You understand Go performance deeply
- **7-9 targets met** → Solid optimization skills
- **4-6 targets met** → Good foundation, practice profiling more
- **< 4 targets met** → Start with `go tool pprof` basics

---

## Optimization Cheat Sheet

Quick reference for common Go output optimizations:

| Problem | Solution | Impact |
|:--------|:---------|:------:|
| `fmt.Sprintf` for simple strings | Use `strings.Builder` or `+` operator | High |
| String concatenation in loop | Use `strings.Builder` with `Grow()` | High |
| `fmt.Println` for known strings | Use `os.Stdout.WriteString` | Medium |
| Many small writes to I/O | Wrap with `bufio.Writer` | High |
| `fmt.Sprintf` for int conversion | Use `strconv.Itoa` or `strconv.AppendInt` | Medium |
| `fmt.Fprintf` with fixed pattern | Manual `WriteString` + `strconv` | Medium |
| `log.Println` in hot loop | Buffer + cached timestamp | High |
| Repeated `strings.Builder` alloc | Pool `[]byte` buffers with `sync.Pool` | Medium-High |
| `bufio.Writer` per request | Pool writers with `sync.Pool` + `Reset()` | Medium-High |
| Lock per write in goroutines | Per-goroutine buffer + single flush | High |
