# Linear Time O(n) — Senior Level

## Table of Contents

1. [Overview](#overview)
2. [Streaming Algorithms](#streaming-algorithms)
   - [Approximate Counting (Morris Counter)](#approximate-counting)
   - [Reservoir Sampling](#reservoir-sampling)
   - [Bloom Filters and Linear Scanning](#bloom-filters-and-linear-scanning)
3. [O(n) in MapReduce and Distributed Systems](#on-in-mapreduce)
4. [Linear Scan in Databases](#linear-scan-in-databases)
   - [Full Table Scan](#full-table-scan)
   - [When Full Scans Are Acceptable](#when-full-scans-are-acceptable)
   - [Columnar Storage Advantage](#columnar-storage-advantage)
5. [When O(n) is Acceptable at Scale](#when-on-is-acceptable-at-scale)
6. [Thread-Safe Linear Processing](#thread-safe-linear-processing)
   - [Partitioned Parallel Scan](#partitioned-parallel-scan)
   - [Lock-Free Single-Pass Aggregation](#lock-free-single-pass-aggregation)
   - [Concurrent Pipeline Processing](#concurrent-pipeline-processing)
7. [System Design Considerations](#system-design-considerations)
8. [Key Takeaways](#key-takeaways)
9. [References](#references)

---

## Overview

At the senior level, linear time algorithms are evaluated in the context of **production systems** operating on large datasets — millions to billions of records. The questions become: When is O(n) acceptable? How do we parallelize it? What are the system-level implications of a full data scan?

---

## Streaming Algorithms

Streaming algorithms process data in a single pass with limited memory — they see each element once and cannot revisit it. This is inherently O(n) in time but typically O(1) or O(log n) in space.

### Approximate Counting

The Morris counter uses probabilistic increments to estimate counts using O(log log n) bits instead of O(log n).

**Go:**

```go
package main

import (
    "fmt"
    "math"
    "math/rand"
)

// MorrisCounter estimates counts using O(log log n) space.
type MorrisCounter struct {
    exponent int
}

func NewMorrisCounter() *MorrisCounter {
    return &MorrisCounter{exponent: 0}
}

func (mc *MorrisCounter) Increment() {
    // Increment with probability 1 / 2^exponent
    if rand.Float64() < 1.0/math.Pow(2.0, float64(mc.exponent)) {
        mc.exponent++
    }
}

func (mc *MorrisCounter) Estimate() int {
    return int(math.Pow(2.0, float64(mc.exponent))) - 1
}

func main() {
    counter := NewMorrisCounter()
    n := 1000000
    for i := 0; i < n; i++ {
        counter.Increment()
    }
    fmt.Printf("Actual count: %d\n", n)
    fmt.Printf("Morris estimate: %d\n", counter.Estimate())
    fmt.Printf("Exponent stored: %d (only %d bits needed)\n",
        counter.exponent, counter.exponent)
}
```

**Java:**

```java
import java.util.Random;

public class MorrisCounter {
    private int exponent = 0;
    private final Random random = new Random();

    public void increment() {
        if (random.nextDouble() < 1.0 / Math.pow(2, exponent)) {
            exponent++;
        }
    }

    public long estimate() {
        return (long) Math.pow(2, exponent) - 1;
    }

    public static void main(String[] args) {
        MorrisCounter counter = new MorrisCounter();
        int n = 1_000_000;
        for (int i = 0; i < n; i++) {
            counter.increment();
        }
        System.out.printf("Actual: %d, Estimate: %d%n", n, counter.estimate());
    }
}
```

**Python:**

```python
import math
import random

class MorrisCounter:
    """Approximate counter using O(log log n) space."""

    def __init__(self):
        self.exponent = 0

    def increment(self):
        if random.random() < 1.0 / (2 ** self.exponent):
            self.exponent += 1

    def estimate(self) -> int:
        return 2 ** self.exponent - 1


if __name__ == "__main__":
    counter = MorrisCounter()
    n = 1_000_000
    for _ in range(n):
        counter.increment()
    print(f"Actual: {n}, Estimate: {counter.estimate()}")
```

### Reservoir Sampling

Select k random items from a stream of unknown length in one pass with O(k) memory.

**Go:**

```go
package main

import (
    "fmt"
    "math/rand"
)

// ReservoirSample selects k items uniformly at random from a stream.
func ReservoirSample(stream []int, k int) []int {
    reservoir := make([]int, k)
    copy(reservoir, stream[:k])

    for i := k; i < len(stream); i++ {
        j := rand.Intn(i + 1)
        if j < k {
            reservoir[j] = stream[i]
        }
    }
    return reservoir
}

func main() {
    stream := make([]int, 1000)
    for i := range stream {
        stream[i] = i
    }
    sample := ReservoirSample(stream, 5)
    fmt.Printf("5 random samples from 0..999: %v\n", sample)
}
```

**Java:**

```java
import java.util.Arrays;
import java.util.Random;

public class ReservoirSampling {
    private static final Random random = new Random();

    public static int[] reservoirSample(int[] stream, int k) {
        int[] reservoir = Arrays.copyOf(stream, k);
        for (int i = k; i < stream.length; i++) {
            int j = random.nextInt(i + 1);
            if (j < k) {
                reservoir[j] = stream[i];
            }
        }
        return reservoir;
    }

    public static void main(String[] args) {
        int[] stream = new int[1000];
        for (int i = 0; i < 1000; i++) stream[i] = i;
        System.out.println(Arrays.toString(reservoirSample(stream, 5)));
    }
}
```

**Python:**

```python
import random

def reservoir_sample(stream, k: int) -> list:
    """Select k items uniformly from a stream. O(n) time, O(k) space."""
    reservoir = list(stream[:k])
    for i in range(k, len(stream)):
        j = random.randint(0, i)
        if j < k:
            reservoir[j] = stream[i]
    return reservoir


if __name__ == "__main__":
    stream = list(range(1000))
    print(f"5 random samples: {reservoir_sample(stream, 5)}")
```

### Bloom Filters and Linear Scanning

When checking membership in a large dataset streamed once, a Bloom filter provides O(1) per-element checks with a small false-positive rate, making the overall scan O(n).

---

## O(n) in MapReduce

In distributed computing, O(n) operations naturally parallelize. A dataset of size n distributed across m machines becomes O(n/m) per machine.

**Map Phase (O(n/m) per mapper):**

```
Input: partition of data (n/m records)
For each record: emit (key, value)
```

**Reduce Phase (O(n/m) per reducer with good partitioning):**

```
For each key: aggregate values
```

**Example: Distributed word count — each mapper processes its chunk in O(n/m), reducers sum counts in O(n/m) with balanced keys.**

**Go — Simulated MapReduce word count:**

```go
package main

import (
    "fmt"
    "strings"
    "sync"
)

func mapPhase(chunk string) map[string]int {
    counts := make(map[string]int)
    for _, word := range strings.Fields(chunk) {
        counts[strings.ToLower(word)]++
    }
    return counts
}

func reducePhase(results []map[string]int) map[string]int {
    final := make(map[string]int)
    for _, partial := range results {
        for word, count := range partial {
            final[word] += count
        }
    }
    return final
}

func main() {
    chunks := []string{
        "the quick brown fox jumps over the lazy dog",
        "the dog barked at the fox and the fox ran",
        "quick fox quick fox lazy lazy dog",
    }

    var mu sync.Mutex
    var results []map[string]int
    var wg sync.WaitGroup

    for _, chunk := range chunks {
        wg.Add(1)
        go func(c string) {
            defer wg.Done()
            partial := mapPhase(c)
            mu.Lock()
            results = append(results, partial)
            mu.Unlock()
        }(chunk)
    }
    wg.Wait()

    final := reducePhase(results)
    for word, count := range final {
        fmt.Printf("%-10s: %d\n", word, count)
    }
}
```

**Python:**

```python
from collections import Counter
from concurrent.futures import ProcessPoolExecutor

def map_phase(chunk: str) -> Counter:
    """Map phase: count words in a chunk. O(n/m)."""
    return Counter(chunk.lower().split())

def reduce_phase(counters: list[Counter]) -> Counter:
    """Reduce phase: merge all counters. O(n)."""
    total = Counter()
    for c in counters:
        total += c
    return total

if __name__ == "__main__":
    chunks = [
        "the quick brown fox jumps over the lazy dog",
        "the dog barked at the fox and the fox ran",
        "quick fox quick fox lazy lazy dog",
    ]
    with ProcessPoolExecutor() as executor:
        partial_counts = list(executor.map(map_phase, chunks))
    final = reduce_phase(partial_counts)
    for word, count in final.most_common():
        print(f"{word:10s}: {count}")
```

---

## Linear Scan in Databases

### Full Table Scan

A full table scan reads every row in a table — O(n) where n is the row count. This happens when:

- No suitable index exists for the query predicate.
- The query returns a large fraction of the table (optimizer chooses scan over index).
- Aggregations require touching all rows (SUM, COUNT, AVG).

```sql
-- Full table scan: O(n)
SELECT AVG(salary) FROM employees;

-- Full table scan even with WHERE if no index on department
SELECT * FROM employees WHERE department = 'Engineering';

-- Index scan: O(log n + k) where k is matching rows
SELECT * FROM employees WHERE id = 12345;
```

### When Full Scans Are Acceptable

1. **Analytics/OLAP workloads:** Aggregating over entire columns is inherently O(n). Columnar databases optimize this with compression and vectorized execution.

2. **Small tables:** A table with 10,000 rows scans in microseconds. Adding an index would waste space for negligible time savings.

3. **High selectivity inversion:** When a query matches > 20-30% of rows, a full scan can be faster than an index scan due to sequential I/O vs. random I/O.

4. **Batch processing:** ETL jobs that transform every row are inherently O(n).

### Columnar Storage Advantage

Columnar databases (Parquet, ClickHouse, BigQuery) make O(n) scans efficient by:

- Reading only the columns needed (projection pushdown).
- Compressing similar values together (run-length encoding, dictionary encoding).
- Processing data in vectorized batches (SIMD operations on column chunks).

A full scan of 1 billion rows in a columnar database can complete in seconds when only a few columns are needed.

---

## When O(n) is Acceptable at Scale

| Data Size         | O(n) Time (at 1GB/s read) | Acceptable?          |
|-------------------|----------------------------|----------------------|
| 1 MB              | 1 ms                       | Always               |
| 100 MB            | 100 ms                     | Almost always        |
| 1 GB              | 1 second                   | For batch/analytics  |
| 100 GB            | 100 seconds                | Needs parallelism    |
| 1 TB              | ~17 minutes                | Must distribute      |
| 1 PB              | ~12 days                   | Requires large cluster |

**Guidelines:**

- **< 1 GB:** O(n) is fine for interactive queries.
- **1-100 GB:** O(n) is acceptable for batch; use indexes/caching for interactive.
- **> 100 GB:** Distribute the O(n) scan across many machines.

---

## Thread-Safe Linear Processing

### Partitioned Parallel Scan

Divide the array into chunks and process each chunk in a separate goroutine/thread.

**Go:**

```go
package main

import (
    "fmt"
    "sync"
)

// parallelSum computes the sum using multiple goroutines.
func parallelSum(arr []int, numWorkers int) int {
    n := len(arr)
    chunkSize := (n + numWorkers - 1) / numWorkers
    results := make([]int, numWorkers)
    var wg sync.WaitGroup

    for w := 0; w < numWorkers; w++ {
        wg.Add(1)
        go func(workerID int) {
            defer wg.Done()
            start := workerID * chunkSize
            end := start + chunkSize
            if end > n {
                end = n
            }
            localSum := 0
            for i := start; i < end; i++ {
                localSum += arr[i]
            }
            results[workerID] = localSum
        }(w)
    }
    wg.Wait()

    total := 0
    for _, s := range results {
        total += s
    }
    return total
}

func main() {
    arr := make([]int, 10_000_000)
    for i := range arr {
        arr[i] = i + 1
    }
    sum := parallelSum(arr, 8)
    fmt.Printf("Parallel sum: %d\n", sum)
}
```

**Java:**

```java
import java.util.concurrent.*;

public class ParallelSum {

    public static long parallelSum(int[] arr, int numWorkers)
            throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);
        int chunkSize = (arr.length + numWorkers - 1) / numWorkers;
        Future<Long>[] futures = new Future[numWorkers];

        for (int w = 0; w < numWorkers; w++) {
            final int start = w * chunkSize;
            final int end = Math.min(start + chunkSize, arr.length);
            futures[w] = executor.submit(() -> {
                long localSum = 0;
                for (int i = start; i < end; i++) {
                    localSum += arr[i];
                }
                return localSum;
            });
        }

        long total = 0;
        for (Future<Long> f : futures) {
            total += f.get();
        }
        executor.shutdown();
        return total;
    }

    public static void main(String[] args) throws Exception {
        int[] arr = new int[10_000_000];
        for (int i = 0; i < arr.length; i++) arr[i] = i + 1;
        System.out.printf("Parallel sum: %d%n", parallelSum(arr, 8));
    }
}
```

**Python:**

```python
from concurrent.futures import ThreadPoolExecutor

def parallel_sum(arr: list[int], num_workers: int = 4) -> int:
    """Sum array in parallel using thread pool. O(n/w) per thread."""
    n = len(arr)
    chunk_size = (n + num_workers - 1) // num_workers

    def chunk_sum(start: int) -> int:
        end = min(start + chunk_size, n)
        return sum(arr[start:end])

    starts = range(0, n, chunk_size)
    with ThreadPoolExecutor(max_workers=num_workers) as executor:
        partial_sums = list(executor.map(chunk_sum, starts))

    return sum(partial_sums)


if __name__ == "__main__":
    arr = list(range(1, 10_000_001))
    print(f"Parallel sum: {parallel_sum(arr, 8)}")
```

### Lock-Free Single-Pass Aggregation

Use atomic operations for thread-safe counters without locks.

**Go:**

```go
package main

import (
    "fmt"
    "sync"
    "sync/atomic"
)

func atomicCount(arr []int, predicate func(int) bool, numWorkers int) int64 {
    var count int64
    n := len(arr)
    chunkSize := (n + numWorkers - 1) / numWorkers
    var wg sync.WaitGroup

    for w := 0; w < numWorkers; w++ {
        wg.Add(1)
        go func(workerID int) {
            defer wg.Done()
            start := workerID * chunkSize
            end := start + chunkSize
            if end > n {
                end = n
            }
            localCount := int64(0)
            for i := start; i < end; i++ {
                if predicate(arr[i]) {
                    localCount++
                }
            }
            atomic.AddInt64(&count, localCount)
        }(w)
    }
    wg.Wait()
    return count
}

func main() {
    arr := make([]int, 10_000_000)
    for i := range arr {
        arr[i] = i
    }
    evenCount := atomicCount(arr, func(x int) bool { return x%2 == 0 }, 8)
    fmt.Printf("Even numbers: %d\n", evenCount)
}
```

### Concurrent Pipeline Processing

Process data through stages where each stage is O(n) but stages run concurrently via channels/queues.

**Go:**

```go
package main

import "fmt"

// Pipeline: generate -> filter -> transform -> collect
func pipeline() {
    // Stage 1: Generate numbers
    generate := func(n int) <-chan int {
        out := make(chan int)
        go func() {
            for i := 0; i < n; i++ {
                out <- i
            }
            close(out)
        }()
        return out
    }

    // Stage 2: Filter even numbers
    filter := func(in <-chan int) <-chan int {
        out := make(chan int)
        go func() {
            for v := range in {
                if v%2 == 0 {
                    out <- v
                }
            }
            close(out)
        }()
        return out
    }

    // Stage 3: Square
    transform := func(in <-chan int) <-chan int {
        out := make(chan int)
        go func() {
            for v := range in {
                out <- v * v
            }
            close(out)
        }()
        return out
    }

    // Run pipeline
    results := transform(filter(generate(20)))
    for v := range results {
        fmt.Printf("%d ", v)
    }
    fmt.Println()
}

func main() {
    pipeline()
}
```

---

## System Design Considerations

| Concern                 | Mitigation for O(n) scans                          |
|-------------------------|-----------------------------------------------------|
| Memory pressure         | Stream data; do not load all n elements into memory  |
| Latency sensitivity     | Pre-compute results; use caching for repeated queries|
| Disk I/O bottleneck     | Use sequential reads; columnar formats; SSD/NVMe     |
| Network bandwidth       | Process data locally; send aggregates, not raw data  |
| Fault tolerance         | Checkpoint progress; enable resumption from last pos |
| Multi-tenancy           | Rate-limit and prioritize scans; avoid starvation    |

---

## Key Takeaways

1. **Streaming algorithms** process O(n) data in one pass with sub-linear memory — essential for data that does not fit in RAM.

2. **MapReduce** naturally parallelizes O(n) workloads to O(n/m) per machine, making linear scans tractable at petabyte scale.

3. **Full table scans** are not inherently bad — they are the correct choice for analytics, small tables, and high-selectivity queries.

4. **Thread-safe linear processing** uses partitioning (no contention) or atomics (minimal contention) to parallelize O(n) work.

5. **Pipeline concurrency** overlaps stages so that the total wall-clock time approaches the slowest stage, not the sum.

6. **O(n) at scale** requires careful system design: streaming, compression, sequential I/O, and distributed execution.

---

## References

- Muthukrishnan, S. (2005). *Data Streams: Algorithms and Applications*. Now Publishers.
- Dean, J., & Ghemawat, S. (2004). "MapReduce: Simplified Data Processing on Large Clusters." OSDI.
- Abadi, D. J., et al. (2013). "The Design and Implementation of Modern Column-Oriented Database Systems." Foundations and Trends in Databases.
- Cormen, T. H., et al. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press. Chapter 27: Multithreaded Algorithms.
