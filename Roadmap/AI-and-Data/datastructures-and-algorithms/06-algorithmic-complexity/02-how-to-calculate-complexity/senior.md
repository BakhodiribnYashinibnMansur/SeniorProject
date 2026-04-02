# How to Calculate Complexity? -- Senior Level

## Prerequisites

- [Junior Level](junior.md) -- counting operations, loop analysis
- [Middle Level](middle.md) -- recurrence relations, Master Theorem, amortized analysis

## Table of Contents

1. [From Theory to Practice](#from-theory-to-practice)
2. [Profiling in Production](#profiling-in-production)
   - [Go: pprof](#go-pprof)
   - [Java: JFR and Async Profiler](#java-jfr-and-async-profiler)
   - [Python: cProfile and py-spy](#python-cprofile-and-py-spy)
3. [Benchmarking Tools](#benchmarking-tools)
   - [Go: testing.B](#go-testingb)
   - [Java: JMH](#java-jmh)
   - [Python: timeit](#python-timeit)
4. [Empirical Complexity Verification](#empirical-complexity-verification)
5. [Complexity at System Scale](#complexity-at-system-scale)
6. [Distributed Algorithm Complexity](#distributed-algorithm-complexity)
7. [Key Takeaways](#key-takeaways)

---

## From Theory to Practice

Theoretical complexity analysis tells you the growth rate of an algorithm. But in production:

- **Constants matter**: An O(n) algorithm with a constant factor of 1000 is slower than O(n log n) with a constant of 1 for n < 2^1000.
- **Cache effects**: Memory access patterns dominate wall-clock time for many workloads.
- **Garbage collection**: Languages with GC (Go, Java, Python) have unpredictable pauses.
- **I/O and network**: In distributed systems, computation is often negligible compared to latency.

The senior engineer must **verify** theoretical analysis with **empirical measurement**.

---

## Profiling in Production

### Go: pprof

Go has built-in profiling via `net/http/pprof` for live services and `runtime/pprof` for CLI tools.

```go
package main

import (
    "log"
    "net/http"
    _ "net/http/pprof"  // register pprof handlers
    "runtime"
)

func main() {
    // Enable blocking and mutex profiling
    runtime.SetBlockProfileRate(1)
    runtime.SetMutexProfileFraction(1)

    // Start pprof server on a separate port
    go func() {
        log.Println(http.ListenAndServe("localhost:6060", nil))
    }()

    // Your application logic here
    runApplication()
}

func runApplication() {
    // Simulate work
    data := make([]int, 1_000_000)
    for i := range data {
        data[i] = i
    }
    // ... more processing
}
```

**Collecting profiles**:

```bash
# CPU profile (30 seconds)
go tool pprof http://localhost:6060/debug/pprof/profile?seconds=30

# Memory (heap) profile
go tool pprof http://localhost:6060/debug/pprof/heap

# Goroutine profile (detect leaks)
go tool pprof http://localhost:6060/debug/pprof/goroutine

# Inside pprof interactive mode:
# top10          -- top 10 functions by CPU
# list funcName  -- annotated source for a function
# web            -- open flame graph in browser
```

### Java: JFR and Async Profiler

**Java Flight Recorder (JFR)** is built into the JDK since Java 11:

```java
// Start your application with JFR enabled:
// java -XX:StartFlightRecording=duration=60s,filename=recording.jfr MyApp

import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Sort Operation")
class SortEvent extends Event {
    @Label("Array Size")
    int arraySize;

    @Label("Algorithm")
    String algorithm;
}

public class ProfilingExample {
    public static void sortWithProfiling(int[] arr) {
        SortEvent event = new SortEvent();
        event.arraySize = arr.length;
        event.algorithm = "mergesort";
        event.begin();

        Arrays.sort(arr);  // actual sort

        event.commit();
    }
}
```

**async-profiler** for low-overhead CPU and allocation profiling:

```bash
# Attach to running JVM
./asprof -d 30 -f flamegraph.html <pid>

# Profile allocations
./asprof -e alloc -d 30 -f alloc.html <pid>
```

### Python: cProfile and py-spy

**cProfile** is built into Python:

```python
import cProfile
import pstats
from io import StringIO

def profile_function(func, *args, **kwargs):
    """Profile a function and print sorted stats."""
    profiler = cProfile.Profile()
    profiler.enable()

    result = func(*args, **kwargs)

    profiler.disable()
    stream = StringIO()
    stats = pstats.Stats(profiler, stream=stream)
    stats.sort_stats('cumulative')
    stats.print_stats(20)  # top 20 functions
    print(stream.getvalue())
    return result


def expensive_computation(n):
    """Example function to profile."""
    result = []
    for i in range(n):
        result.append(sum(range(i)))  # O(n^2) total
    return result


if __name__ == "__main__":
    profile_function(expensive_computation, 5000)
```

**py-spy** for production profiling without code changes:

```bash
# Record a flame graph
py-spy record -o profile.svg --pid <pid>

# Top-like live view
py-spy top --pid <pid>
```

---

## Benchmarking Tools

### Go: testing.B

```go
package complexity

import "testing"

// BenchmarkLinearSearch benchmarks O(n) linear search
func BenchmarkLinearSearch(b *testing.B) {
    sizes := []int{100, 1000, 10000, 100000}
    for _, size := range sizes {
        data := make([]int, size)
        for i := range data {
            data[i] = i
        }
        target := -1 // worst case: not found

        b.Run(fmt.Sprintf("size=%d", size), func(b *testing.B) {
            for i := 0; i < b.N; i++ {
                linearSearch(data, target)
            }
        })
    }
}

// BenchmarkBinarySearch benchmarks O(log n) binary search
func BenchmarkBinarySearch(b *testing.B) {
    sizes := []int{100, 1000, 10000, 100000}
    for _, size := range sizes {
        data := make([]int, size)
        for i := range data {
            data[i] = i
        }
        target := -1

        b.Run(fmt.Sprintf("size=%d", size), func(b *testing.B) {
            for i := 0; i < b.N; i++ {
                binarySearch(data, target)
            }
        })
    }
}

// Run: go test -bench=. -benchmem
// Expected output shows linear growth for LinearSearch,
// near-constant for BinarySearch as size increases.
```

### Java: JMH

```java
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class ComplexityBenchmark {

    @Param({"100", "1000", "10000", "100000"})
    private int size;

    private int[] data;

    @Setup
    public void setup() {
        data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = i;
        }
    }

    @Benchmark
    public int benchmarkLinearSearch() {
        return linearSearch(data, -1);
    }

    @Benchmark
    public int benchmarkBinarySearch() {
        return binarySearch(data, -1);
    }

    private int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    private int binarySearch(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] == target) return mid;
            else if (arr[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }
}
```

### Python: timeit

```python
import timeit
import random

def linear_search(arr, target):
    for i, x in enumerate(arr):
        if x == target:
            return i
    return -1

def binary_search(arr, target):
    lo, hi = 0, len(arr) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1

def benchmark():
    sizes = [100, 1000, 10_000, 100_000]
    for size in sizes:
        data = list(range(size))
        target = -1  # worst case

        linear_time = timeit.timeit(
            lambda: linear_search(data, target),
            number=100
        )
        binary_time = timeit.timeit(
            lambda: binary_search(data, target),
            number=100
        )

        print(f"n={size:>7d} | linear: {linear_time:.4f}s | "
              f"binary: {binary_time:.6f}s | "
              f"ratio: {linear_time/binary_time:.1f}x")

if __name__ == "__main__":
    benchmark()
    # Expected output shows linear_time growing ~10x per 10x size increase
    # while binary_time grows only slightly (log factor)
```

---

## Empirical Complexity Verification

To **verify** your theoretical analysis, run benchmarks at multiple input sizes and check the growth rate.

### The Ratio Method

If T(n) = O(n^k), then T(2n)/T(n) should approach 2^k.

| Algorithm | Expected Ratio T(2n)/T(n) |
|---|---|
| O(1) | 1.0 |
| O(log n) | ~1.0 (slight increase) |
| O(n) | 2.0 |
| O(n log n) | ~2.0 (slightly above) |
| O(n^2) | 4.0 |
| O(n^3) | 8.0 |
| O(2^n) | T(n)^2 (exponential) |

### Go Implementation

```go
func verifyComplexity(algorithm func(int), sizes []int) {
    var prevTime float64
    for _, n := range sizes {
        start := time.Now()
        algorithm(n)
        elapsed := time.Since(start).Seconds()

        if prevTime > 0 {
            ratio := elapsed / prevTime
            fmt.Printf("n=%8d  time=%.4fs  ratio=%.2f\n", n, elapsed, ratio)
        } else {
            fmt.Printf("n=%8d  time=%.4fs\n", n, elapsed)
        }
        prevTime = elapsed
    }
}
```

### Python Implementation

```python
import time

def verify_complexity(algorithm, sizes):
    prev_time = None
    for n in sizes:
        start = time.perf_counter()
        algorithm(n)
        elapsed = time.perf_counter() - start

        if prev_time is not None and prev_time > 0:
            ratio = elapsed / prev_time
            print(f"n={n:>8d}  time={elapsed:.4f}s  ratio={ratio:.2f}")
        else:
            print(f"n={n:>8d}  time={elapsed:.4f}s")
        prev_time = elapsed

# Usage:
# verify_complexity(lambda n: sum(range(n)), [10000, 20000, 40000, 80000])
# Expect ratio ≈ 2.0 for O(n)
```

---

## Complexity at System Scale

In production systems, algorithmic complexity interacts with infrastructure concerns.

### Memory Hierarchy Effects

| Level | Latency | Implication |
|---|---|---|
| L1 cache | ~1 ns | Array traversal is fast (sequential access) |
| L2 cache | ~4 ns | Still fast for moderate data |
| L3 cache | ~10 ns | Noticeable for large datasets |
| Main memory | ~100 ns | Random access patterns hurt here |
| SSD | ~100 us | I/O-bound algorithms dominate |
| Network | ~1 ms | Distributed systems bottleneck |

An O(n) algorithm with poor cache locality can be slower than O(n log n) with good locality.

### Database Query Complexity

```sql
-- O(n): full table scan
SELECT * FROM users WHERE email = 'user@example.com';

-- O(log n): indexed lookup
-- (after CREATE INDEX idx_email ON users(email))
SELECT * FROM users WHERE email = 'user@example.com';

-- O(n*m): nested loop join without index
SELECT * FROM orders o JOIN products p ON o.product_id = p.id;

-- O(n + m): hash join
-- (database optimizer may choose this automatically)
```

### API and Service Complexity

Consider a REST endpoint that returns paginated results:

```
GET /api/users?page=1&limit=50

Without index:   O(n) per request -- scan all users
With index:      O(log n + k) per request -- where k = page size
With cursor:     O(log n + k) per request -- stable pagination
```

---

## Distributed Algorithm Complexity

In distributed systems, complexity has additional dimensions.

### Communication Complexity

- **Message complexity**: How many messages are exchanged?
- **Round complexity**: How many synchronous rounds are needed?
- **Bandwidth complexity**: How many bits are transmitted?

### Common Distributed Patterns

| Pattern | Message Complexity | Round Complexity |
|---|---|---|
| Broadcast | O(n) | O(1) with multicast |
| Gather/Reduce | O(n) | O(log n) with tree |
| All-to-all | O(n^2) | O(1) |
| Consensus (Raft) | O(n) per decision | O(1) amortized |
| MapReduce sort | O(n log n) total work | O(log n) rounds |

### Example: Distributed Aggregation

```
Problem: Sum n values across k machines, each holding n/k values.

Approach 1: Send all to one machine
  - Communication: O(n) messages
  - Computation: O(n) at the coordinator
  - Bottleneck: single machine

Approach 2: Tree reduction
  - Communication: O(k) messages total
  - Computation: O(n/k) per machine + O(k) at root
  - Rounds: O(log k)
  - Much better at scale
```

---

## Key Takeaways

1. **Profile before optimizing** -- use pprof (Go), JFR/async-profiler (Java), cProfile/py-spy (Python).
2. **Benchmark systematically** -- use testing.B (Go), JMH (Java), timeit (Python) with multiple input sizes.
3. **Verify empirically** -- the ratio method confirms your theoretical analysis against real data.
4. **Constants and cache effects** matter in production; pure Big-O is not the whole story.
5. **Database indexes** change query complexity from O(n) to O(log n) -- always analyze your queries.
6. **Distributed complexity** adds communication cost, round complexity, and bandwidth to the analysis.
7. **Measure at system scale** -- the bottleneck is often I/O or network, not CPU.

---

> **Next**: [Professional Level](professional.md) -- Formal proofs, Akra-Bazzi, substitution method, decision tree bounds.
