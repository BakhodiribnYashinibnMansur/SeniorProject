# Logarithmic Time O(log n) — Senior Level

## Table of Contents

- [Introduction](#introduction)
- [O(log n) in Databases — B+ Tree Indexes](#olog-n-in-databases--b-tree-indexes)
- [Binary Search in Production Systems](#binary-search-in-production-systems)
- [Distributed Consensus — Raft Log](#distributed-consensus--raft-log)
- [Log-Structured Merge Trees (LSM Trees)](#log-structured-merge-trees-lsm-trees)
- [Skip Lists in Production](#skip-lists-in-production)
- [Practical Considerations](#practical-considerations)
- [Key Takeaways](#key-takeaways)
- [References](#references)

---

## Introduction

At the senior level, O(log n) stops being a textbook concept and becomes a design tool. You will
see how logarithmic complexity shapes the architecture of databases, distributed systems, and
high-throughput services. The focus shifts from implementing algorithms to choosing and configuring
the right logarithmic data structures for production workloads.

---

## O(log n) in Databases — B+ Tree Indexes

### How B+ Trees Power Database Queries

Every major relational database (PostgreSQL, MySQL/InnoDB, SQL Server, Oracle) uses **B+ trees**
as the default index structure. A B+ tree lookup traverses O(log_B n) levels, where B is the
branching factor (typically 100-500 for 8KB pages).

For a table with 1 billion rows and B = 200:

```
height = ⌈log₂₀₀(1,000,000,000)⌉ = ⌈9,000,000,000 / log₂(200)⌉ ≈ 4 levels
```

This means **4 disk reads** to locate any row among a billion. The root and first internal levels
are typically cached in RAM, reducing it to **1-2 disk reads** in practice.

### Index Design Implications

Understanding the logarithmic nature of B+ trees informs index design:

1. **Composite indexes** — A composite index on (a, b, c) supports O(log n) lookups on (a),
   (a, b), and (a, b, c), but NOT on (b) alone. The B+ tree is sorted lexicographically.

2. **Index selectivity** — An index on a boolean column (2 values) is nearly useless because the
   B+ tree cannot eliminate much of the search space. High-cardinality columns benefit most.

3. **Covering indexes** — If all queried columns are in the index, the database can answer the
   query from the B+ tree alone without touching the table data, keeping the operation purely
   O(log n).

### PostgreSQL EXPLAIN Example

```sql
-- Create a table with 10M rows and a B-tree index
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- This uses the B-tree index: O(log n) lookup
EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'user@example.com';

-- Output:
-- Index Scan using users_email_key on users
--   Index Cond: (email = 'user@example.com')
--   Planning Time: 0.1 ms
--   Execution Time: 0.05 ms    <-- logarithmic: barely changes with table size
```

### When B+ Trees Are Not Enough

| Scenario                    | B+ Tree Performance | Alternative           |
|-----------------------------|--------------------|-----------------------|
| Point lookups               | O(log n) — great   | Hash index: O(1)     |
| Range scans                 | O(log n + k) — great | —                  |
| Write-heavy workload        | O(log n) — mediocre | LSM tree: O(1) amortized |
| Full-text search            | Poor               | Inverted index        |
| High-dimensional similarity | Poor               | HNSW, IVF             |

---

## Binary Search in Production Systems

### Rate Limiters — Token Bucket with Sorted Timestamps

A rate limiter often needs to find "how many requests occurred in the last T seconds." By
maintaining a sorted list of timestamps, binary search finds the boundary in O(log n):

```go
package main

import (
    "fmt"
    "sort"
    "time"
)

type SlidingWindowLimiter struct {
    timestamps []int64 // sorted Unix timestamps in milliseconds
    windowMs   int64
    maxReqs    int
}

func NewLimiter(windowMs int64, maxReqs int) *SlidingWindowLimiter {
    return &SlidingWindowLimiter{
        windowMs: windowMs,
        maxReqs:  maxReqs,
    }
}

func (l *SlidingWindowLimiter) Allow(nowMs int64) bool {
    // Binary search for the start of the current window
    cutoff := nowMs - l.windowMs
    // Find first index where timestamp > cutoff
    idx := sort.Search(len(l.timestamps), func(i int) bool {
        return l.timestamps[i] > cutoff
    })

    // Remove expired timestamps
    l.timestamps = l.timestamps[idx:]

    if len(l.timestamps) >= l.maxReqs {
        return false
    }

    l.timestamps = append(l.timestamps, nowMs)
    return true
}

func main() {
    limiter := NewLimiter(1000, 5) // 5 requests per second
    now := time.Now().UnixMilli()

    for i := 0; i < 7; i++ {
        allowed := limiter.Allow(now + int64(i*100))
        fmt.Printf("Request %d at +%dms: %v\n", i+1, i*100, allowed)
    }
    // First 5 allowed, 6th and 7th denied
}
```

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingWindowLimiter {
    private List<Long> timestamps = new ArrayList<>();
    private final long windowMs;
    private final int maxReqs;

    public SlidingWindowLimiter(long windowMs, int maxReqs) {
        this.windowMs = windowMs;
        this.maxReqs = maxReqs;
    }

    public boolean allow(long nowMs) {
        long cutoff = nowMs - windowMs;

        // Binary search for the first timestamp after cutoff
        int idx = Collections.binarySearch(timestamps, cutoff);
        if (idx < 0) idx = -(idx + 1);
        // Advance to first element strictly > cutoff
        while (idx < timestamps.size() && timestamps.get(idx) <= cutoff) idx++;

        timestamps = new ArrayList<>(timestamps.subList(idx, timestamps.size()));

        if (timestamps.size() >= maxReqs) return false;

        timestamps.add(nowMs);
        return true;
    }

    public static void main(String[] args) {
        SlidingWindowLimiter limiter = new SlidingWindowLimiter(1000, 5);
        long now = System.currentTimeMillis();

        for (int i = 0; i < 7; i++) {
            boolean allowed = limiter.allow(now + i * 100L);
            System.out.printf("Request %d at +%dms: %b%n", i + 1, i * 100, allowed);
        }
    }
}
```

```python
import bisect
import time


class SlidingWindowLimiter:
    def __init__(self, window_ms: int, max_reqs: int):
        self.timestamps: list[int] = []
        self.window_ms = window_ms
        self.max_reqs = max_reqs

    def allow(self, now_ms: int) -> bool:
        cutoff = now_ms - self.window_ms
        # Binary search for the first timestamp after cutoff
        idx = bisect.bisect_right(self.timestamps, cutoff)

        # Remove expired timestamps
        self.timestamps = self.timestamps[idx:]

        if len(self.timestamps) >= self.max_reqs:
            return False

        self.timestamps.append(now_ms)
        return True


if __name__ == "__main__":
    limiter = SlidingWindowLimiter(1000, 5)
    now = int(time.time() * 1000)

    for i in range(7):
        allowed = limiter.allow(now + i * 100)
        print(f"Request {i+1} at +{i*100}ms: {allowed}")
```

### Load Balancers — Weighted Routing with Binary Search

Weighted load balancing with a cumulative distribution can use binary search to select a backend
in O(log n) time:

```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
)

type Backend struct {
    Address string
    Weight  int
}

type WeightedBalancer struct {
    backends   []Backend
    cumWeights []int
    totalWeight int
}

func NewWeightedBalancer(backends []Backend) *WeightedBalancer {
    wb := &WeightedBalancer{backends: backends}
    wb.cumWeights = make([]int, len(backends))

    cumulative := 0
    for i, b := range backends {
        cumulative += b.Weight
        wb.cumWeights[i] = cumulative
    }
    wb.totalWeight = cumulative
    return wb
}

// Pick selects a backend in O(log n) using binary search on cumulative weights.
func (wb *WeightedBalancer) Pick() string {
    r := rand.Intn(wb.totalWeight)
    idx := sort.SearchInts(wb.cumWeights, r+1)
    return wb.backends[idx].Address
}

func main() {
    backends := []Backend{
        {"server-a:8080", 5},
        {"server-b:8080", 3},
        {"server-c:8080", 2},
    }

    balancer := NewWeightedBalancer(backends)

    counts := map[string]int{}
    for i := 0; i < 10000; i++ {
        counts[balancer.Pick()]++
    }

    for addr, count := range counts {
        fmt.Printf("%s: %d (%.1f%%)\n", addr, count, float64(count)/100.0)
    }
}
```

---

## Distributed Consensus — Raft Log

The **Raft consensus algorithm** maintains a replicated log across cluster nodes. Key operations
that leverage O(log n) structures:

1. **Log indexing** — Finding a specific log entry by index uses the underlying storage's index
   structure (typically a B-tree or skip list), giving O(log n) access.

2. **Binary search for commit point** — The leader determines the highest index replicated to a
   majority by sorting `matchIndex` values and taking the median. With binary search on a sorted
   view, this is O(log k) where k is the cluster size.

3. **Log compaction via snapshots** — When the log grows too large, Raft takes a snapshot and
   truncates. Finding the snapshot boundary involves binary search on the log.

### Practical Impact

In production systems like **etcd** (used by Kubernetes), the Raft log is backed by **BoltDB**,
which uses a B+ tree. Every key-value operation in Kubernetes ultimately passes through an O(log n)
B+ tree lookup.

---

## Log-Structured Merge Trees (LSM Trees)

LSM trees power write-optimized databases like **RocksDB**, **LevelDB**, **Cassandra**, and
**HBase**. They achieve O(1) amortized writes by buffering in memory, then flushing sorted runs
to disk.

### Read Path and O(log n)

A point read in an LSM tree checks multiple levels:

1. **Memtable** (in-memory skip list or red-black tree): O(log n₁)
2. **Level 0 SSTables**: Binary search within each file + bloom filter check
3. **Level 1-L SSTables**: Binary search to find the right file, then binary search within it

The total read cost is **O(L * log n)** where L is the number of levels. Since L = O(log n)
for a leveled compaction strategy, worst-case reads are O(log² n).

**Bloom filters** reduce this dramatically: if the target is not in a level, the bloom filter
rejects it in O(1) with high probability.

### Write Amplification vs. Read Amplification

| Structure  | Write      | Point Read | Range Read   |
|-----------|------------|-----------|-------------|
| B+ tree    | O(log n)   | O(log n)  | O(log n + k)|
| LSM tree   | O(1) amort | O(log² n) | O(log n + k)|
| Hash table | O(1)       | O(1)      | O(n)        |

LSM trees trade read performance for write performance. This is why Cassandra excels at
write-heavy workloads while PostgreSQL (B+ tree) excels at read-heavy workloads.

---

## Skip Lists in Production

A **skip list** is a probabilistic alternative to balanced BSTs that provides O(log n) expected
time for search, insert, and delete. Skip lists are used in:

- **Redis** sorted sets (ZSET)
- **LevelDB / RocksDB** memtable
- **Java ConcurrentSkipListMap**

### Why Skip Lists Over Balanced Trees?

1. **Simpler implementation** — No rotation logic, easier to debug.
2. **Lock-free variants** — Skip lists can be made lock-free more easily than balanced trees,
   which is crucial for concurrent access.
3. **Cache-friendly traversal** — Forward pointers enable efficient range scans.

### Redis ZRANGEBYSCORE

When you run `ZRANGEBYSCORE myset 100 200`, Redis:

1. Uses the skip list to find the first element with score >= 100 in **O(log n)**.
2. Traverses forward at the bottom level until score > 200 in **O(k)** where k is the result size.

Total: **O(log n + k)** — the same as a B+ tree range scan.

---

## Practical Considerations

### When O(log n) Is Not Fast Enough

Even O(log n) can be a bottleneck:

1. **Hot-path latency** — At 1 billion elements, O(log n) ≈ 30 comparisons. With cache misses
   on each comparison (B-tree node on disk), this could mean 30 * 10ms = 300ms. Solution: cache
   upper levels of the tree.

2. **High QPS** — At 1 million queries per second, each doing O(log n) work, the aggregate CPU
   cost matters. Consider hash tables for point lookups.

3. **Embedded systems** — Recursion depth of O(log n) may exceed small stack limits. Use
   iterative implementations.

### Amortized vs. Worst-Case O(log n)

| Structure     | Worst-case guarantee? | Notes                           |
|---------------|----------------------|---------------------------------|
| AVL tree      | Yes                  | Strictly balanced               |
| Red-Black tree| Yes                  | Relaxed balance                 |
| Splay tree    | No (amortized)       | O(n) single operation possible  |
| Skip list     | No (expected)        | Probabilistic, O(n) unlikely    |
| B-tree        | Yes                  | Guaranteed by structure          |

For real-time systems (robotics, trading), prefer worst-case O(log n) structures like AVL or
B-trees over amortized ones like splay trees.

---

## Key Takeaways

1. **B+ trees** in databases provide O(log_B n) lookups with very small constants, making them
   practical for billions of rows with only 3-4 disk reads.

2. **Binary search** appears in production systems beyond simple array lookups — rate limiters,
   load balancers, time-series databases.

3. **Raft and other consensus protocols** rely on O(log n) data structures for log indexing and
   commit point calculation.

4. **LSM trees** trade O(log n) reads for near-O(1) writes, making them ideal for write-heavy
   workloads.

5. **Skip lists** offer O(log n) with simpler implementation and better concurrency properties
   than balanced BSTs.

6. **Choose the right logarithmic structure** based on your workload: B+ tree for reads, LSM
   for writes, skip list for concurrent access, hash table when O(log n) is not fast enough.

---

## References

1. Comer, D. "The Ubiquitous B-Tree," *ACM Computing Surveys*, 1979.
2. Ongaro, D., Ousterhout, J. "In Search of an Understandable Consensus Algorithm," *USENIX ATC*, 2014.
3. O'Neil, P., et al. "The Log-Structured Merge-Tree (LSM-Tree)," *Acta Informatica*, 1996.
4. Pugh, W. "Skip Lists: A Probabilistic Alternative to Balanced Trees," *CACM*, 1990.
5. Graefe, G. "Modern B-Tree Techniques," *Foundations and Trends in Databases*, 2011.
