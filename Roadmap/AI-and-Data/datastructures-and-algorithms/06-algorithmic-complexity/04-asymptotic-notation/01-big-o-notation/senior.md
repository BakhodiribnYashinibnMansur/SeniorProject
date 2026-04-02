# Big-O Notation -- Senior Level

## Table of Contents

1. [Big-O in System Design](#big-o-in-system-design)
2. [API Latency and SLA Budgets](#api-latency-and-sla-budgets)
3. [Database Query Planning and Big-O](#database-query-planning-and-big-o)
4. [Profiling to Validate Big-O Claims](#profiling-to-validate-big-o-claims)
5. [When Big-O Misleads](#when-big-o-misleads)
6. [Big-O in Distributed Systems](#big-o-in-distributed-systems)
7. [Capacity Planning with Big-O](#capacity-planning-with-big-o)
8. [Architecture Decision Records and Complexity](#architecture-decision-records-and-complexity)
9. [Key Takeaways](#key-takeaways)

---

## Big-O in System Design

At the senior level, Big-O is not an academic exercise -- it is a tool for making architecture decisions. The question shifts from "what is the complexity?" to "will this design meet our requirements at scale?"

### Translating Big-O to Real Constraints

When designing a system expected to handle 10 million users:

| Component             | O(n) with n=10M  | O(n log n) with n=10M | O(n^2) with n=10M |
|-----------------------|-------------------|-----------------------|--------------------|
| In-memory operations  | ~10ms             | ~230ms                | ~27 hours          |
| Disk I/O operations   | ~100 sec          | ~2,300 sec            | ~3.17 years        |
| Network calls         | Infeasible inline | Infeasible inline     | Infeasible         |

This table demonstrates why O(n^2) algorithms are architectural dead ends at scale, and why even O(n) might be too slow if n is large and each operation involves I/O.

### Real System Design Patterns

**Pagination (converting O(n) to O(k) per request):**
Instead of returning all n records, return k records per page. Each request is O(k) where k is the page size (typically 20-100), regardless of total dataset size.

**Indexing (converting O(n) to O(log n)):**
Database indexes turn full table scans into B-tree lookups. A table with 100 million rows needs ~27 B-tree levels at most (log2(100M) ~ 27).

**Caching (converting O(f(n)) to O(1) for repeated queries):**
The first lookup is O(f(n)), but subsequent lookups for the same key are O(1) from cache.

**Pre-computation (moving O(n) from request time to build time):**
Materialized views, pre-aggregated metrics, and denormalized tables trade write-time complexity for read-time O(1).

---

## API Latency and SLA Budgets

### Decomposing Latency by Complexity

A typical API request involves multiple steps, each with its own complexity:

```
Total latency = auth_check + query_db + process_data + serialize_response

Example for a "list user orders" endpoint:
  auth_check:      O(1) via JWT validation            ~1ms
  query_db:        O(log n) via indexed lookup         ~5ms
  process_data:    O(k) where k = page size            ~2ms
  serialize_response: O(k) JSON marshaling             ~1ms
  Total:           O(log n + k)                        ~9ms
```

If someone changes `process_data` to include an O(k^2) nested comparison:
```
  process_data:    O(k^2) with k=100                   ~50ms
  Total now:       ~57ms per request
```

This might still meet a 200ms SLA, but at k=1000 it becomes 5 seconds.

### Setting SLA Budgets Based on Complexity

A senior engineer's responsibility is to set complexity budgets:

```
P99 latency target: 200ms
Breakdown:
  - Authentication:     10ms budget   -> must be O(1)
  - Database queries:   100ms budget  -> must be O(log n) or better
  - Business logic:     50ms budget   -> must be O(n) where n <= 1000
  - Serialization:      20ms budget   -> must be O(k) where k = page size
  - Network overhead:   20ms budget   -> infrastructure concern
```

---

## Database Query Planning and Big-O

### Index Types and Their Complexities

| Index Type     | Lookup    | Range Query | Insert    | Space    |
|----------------|-----------|-------------|-----------|----------|
| B-Tree         | O(log n)  | O(log n + k)| O(log n) | O(n)     |
| Hash           | O(1) avg  | O(n)        | O(1) avg | O(n)     |
| Bitmap         | O(1)      | O(n/word)   | O(n)     | O(n)     |
| GIN (inverted) | O(log n)  | O(log n + k)| O(log n) | O(n * m) |

### Query Plan Analysis

Understanding EXPLAIN output in terms of Big-O:

```sql
-- Sequential Scan: O(n) -- reads every row
EXPLAIN SELECT * FROM orders WHERE status = 'pending';
-- If no index on status: Seq Scan, cost proportional to table size

-- Index Scan: O(log n + k) -- B-tree descent + fetch matching rows
EXPLAIN SELECT * FROM orders WHERE user_id = 42;
-- With index on user_id: Index Scan, cost proportional to log(n) + matches

-- Nested Loop Join: O(n * m) or O(n * log m) with index
EXPLAIN SELECT * FROM orders o JOIN users u ON o.user_id = u.id;
-- Without index: O(n * m), With index on u.id: O(n * log m)

-- Hash Join: O(n + m)
-- Build hash table from smaller relation: O(m)
-- Probe with larger relation: O(n)

-- Merge Join on sorted inputs: O(n + m)
-- Both inputs must be sorted (or use index for sort)
```

### Query Optimization Decisions

**Go -- demonstrating the impact of query design:**
```go
// BAD: O(n) per call, called m times = O(n * m)
func getOrdersForUsers(db *sql.DB, userIDs []int) ([]Order, error) {
    var allOrders []Order
    for _, uid := range userIDs {
        rows, err := db.Query("SELECT * FROM orders WHERE user_id = ?", uid)
        if err != nil {
            return nil, err
        }
        // ... process rows
    }
    return allOrders, nil
}

// GOOD: O(n + m) with a single query using IN clause
func getOrdersForUsersBatch(db *sql.DB, userIDs []int) ([]Order, error) {
    query := "SELECT * FROM orders WHERE user_id IN (" + placeholders(len(userIDs)) + ")"
    rows, err := db.Query(query, toInterface(userIDs)...)
    if err != nil {
        return nil, err
    }
    // ... process rows
    return allOrders, nil
}
```

**Java:**
```java
// BAD: N+1 query problem -- O(n * log m) with index
public List<Order> getOrdersForUsers(List<Integer> userIds) {
    List<Order> allOrders = new ArrayList<>();
    for (int uid : userIds) {
        allOrders.addAll(orderRepository.findByUserId(uid)); // 1 query per user
    }
    return allOrders;
}

// GOOD: Single query -- O(log m + k) total
public List<Order> getOrdersForUsersBatch(List<Integer> userIds) {
    return orderRepository.findByUserIdIn(userIds); // 1 query total
}
```

**Python:**
```python
# BAD: N+1 query problem
def get_orders_for_users(session, user_ids):
    all_orders = []
    for uid in user_ids:
        orders = session.query(Order).filter(Order.user_id == uid).all()
        all_orders.extend(orders)
    return all_orders

# GOOD: Single query
def get_orders_for_users_batch(session, user_ids):
    return session.query(Order).filter(Order.user_id.in_(user_ids)).all()
```

---

## Profiling to Validate Big-O Claims

Theoretical Big-O must be validated against reality. Here is how to empirically verify complexity.

### Benchmarking Methodology

**Go:**
```go
package bigO_test

import (
    "testing"
    "math/rand"
)

func generateData(n int) []int {
    data := make([]int, n)
    for i := range data {
        data[i] = rand.Intn(n * 10)
    }
    return data
}

// Run: go test -bench=BenchmarkLinearSearch -benchmem
func BenchmarkLinearSearch(b *testing.B) {
    sizes := []int{1000, 10000, 100000, 1000000}
    for _, size := range sizes {
        data := generateData(size)
        target := -1 // worst case: not found
        b.Run(fmt.Sprintf("n=%d", size), func(b *testing.B) {
            for i := 0; i < b.N; i++ {
                linearSearch(data, target)
            }
        })
    }
}
// Expected: 10x input -> ~10x time (O(n))
// If 10x input -> ~100x time, actual complexity is O(n^2)
```

**Python:**
```python
import time
import random

def benchmark(func, sizes, trials=5):
    """Empirically measure complexity by comparing runtimes at different sizes."""
    results = {}
    for n in sizes:
        data = [random.randint(0, n * 10) for _ in range(n)]
        times = []
        for _ in range(trials):
            start = time.perf_counter()
            func(data)
            elapsed = time.perf_counter() - start
            times.append(elapsed)
        avg_time = sum(times) / len(times)
        results[n] = avg_time
        print(f"n={n:>10}: {avg_time:.6f}s")

    # Calculate growth ratios
    sorted_sizes = sorted(results.keys())
    for i in range(1, len(sorted_sizes)):
        prev, curr = sorted_sizes[i-1], sorted_sizes[i]
        size_ratio = curr / prev
        time_ratio = results[curr] / results[prev]
        print(f"Size x{size_ratio:.0f} -> Time x{time_ratio:.1f}")
        # O(n): time_ratio ~ size_ratio
        # O(n^2): time_ratio ~ size_ratio^2
        # O(n log n): time_ratio ~ size_ratio * log(curr)/log(prev)
```

### Interpreting Benchmark Results

| Observed ratio (10x input) | Likely complexity |
|-----------------------------|-------------------|
| ~1x time                    | O(1) or O(log n)  |
| ~10x time                   | O(n)              |
| ~13-15x time                | O(n log n)        |
| ~100x time                  | O(n^2)            |
| ~1000x time                 | O(n^3)            |

---

## When Big-O Misleads

### Constant Factors Matter

Radix sort is O(n * k) where k is the number of digits. For 32-bit integers, k = 32. Comparison sort is O(n log n). For n < 2^32 (~4 billion), log n < 32, so in practice radix sort's "better" Big-O may not translate to faster execution due to cache misses and memory allocation overhead.

### Cache Effects

Modern CPUs have multi-level caches. Algorithms that access memory sequentially (cache-friendly) can be 10-100x faster than algorithms with random access patterns, even with the same Big-O.

**Example:** Array traversal (O(n)) vs linked list traversal (O(n)) -- same Big-O, but the array version is typically 5-20x faster due to cache locality.

### Small Input Sizes

For small n (say n < 50), an O(n^2) algorithm with small constants often beats an O(n log n) algorithm with large constants. This is why most standard library sort implementations switch from quicksort/mergesort to insertion sort for small subarrays.

### Worst Case vs Average Case

Quicksort is O(n^2) worst case but O(n log n) average case. In practice with randomized pivots, the worst case almost never occurs, making it faster than merge sort (O(n log n) worst case) due to better cache performance and lower constant factors.

### Hidden Complexity in Language Features

**Go:**
```go
// Looks like O(n) but string concatenation in a loop is O(n^2)
func concatBad(words []string) string {
    result := ""
    for _, w := range words {
        result += w  // Each += copies the entire string: O(n) per iteration
    }
    return result  // Total: O(n^2) where n = total characters
}

// Actually O(n) using strings.Builder
func concatGood(words []string) string {
    var sb strings.Builder
    for _, w := range words {
        sb.WriteString(w)  // Amortized O(1) per write
    }
    return sb.String()
}
```

---

## Big-O in Distributed Systems

### Network Calls as the Dominant Factor

In distributed systems, a single network call (~1-100ms) dwarfs millions of in-memory operations (~1ns each). Big-O analysis must account for the number of network round trips.

**Fan-out pattern complexity:**
```
Service A calls Service B for each item: O(n) network calls
Service A batches items and calls Service B once: O(1) network calls

Even though both do O(n) total "work," the network-call version
is ~1,000,000x slower due to latency per call.
```

### Consensus Algorithms

| Algorithm | Message Complexity | Round Complexity |
|-----------|--------------------|------------------|
| 2PC       | O(n)               | O(1)             |
| Paxos     | O(n)               | O(1) typical     |
| Raft      | O(n)               | O(1) typical     |
| PBFT      | O(n^2)             | O(1)             |

This is why PBFT-based blockchains struggle to scale beyond a few hundred nodes: the quadratic message complexity becomes prohibitive.

### Data Partitioning and Big-O

Sharding a database across k nodes changes complexity:
- Full table scan: O(n) becomes O(n/k) per shard, but O(k) fan-out -> O(n/k + k)
- Point lookup with routing: O(log n) becomes O(log(n/k)) per shard + O(1) routing -> O(log n)
- Scatter-gather query: O(n/k) per shard * k shards = O(n), no improvement

---

## Capacity Planning with Big-O

### Estimating Resource Requirements

Given an O(n log n) algorithm processing n events per second:

```
Current: n = 10,000 events/sec, CPU usage = 40%
Question: Can we handle 100,000 events/sec?

Growth factor: 100,000 / 10,000 = 10x
O(n log n) growth: 10 * log(100,000) / log(10,000) ~= 10 * 1.25 = 12.5x
Projected CPU: 40% * 12.5 = 500%

Conclusion: Need ~5x more CPU capacity (5 instances instead of 1)
```

### Cost Implications

| Algorithm | n=1M cost | n=10M cost | n=100M cost | Growth pattern |
|-----------|-----------|------------|-------------|----------------|
| O(n)      | $100/mo   | $1,000/mo  | $10,000/mo  | Linear         |
| O(n log n)| $100/mo   | $1,250/mo  | $15,000/mo  | Near-linear    |
| O(n^2)    | $100/mo   | $10,000/mo | $1,000,000/mo| Unsustainable |

---

## Architecture Decision Records and Complexity

When proposing architecture changes, document the complexity implications:

```markdown
## ADR-042: Switch from Nested Loop Join to Hash Join for Report Generation

### Context
Report generation joins user_activity (10M rows) with user_profiles (1M rows).
Current: Nested loop join -> O(n * m) = 10^13 operations -> ~8 hours.

### Decision
Switch to hash join: O(n + m) = 11M operations -> ~2 seconds.

### Consequences
- Memory usage increases by ~100MB (hash table for user_profiles).
- Report generation time drops from 8 hours to under 10 seconds.
- Acceptable tradeoff given server has 16GB available RAM.
```

---

## Key Takeaways

- At the senior level, Big-O is a design tool, not an academic exercise. It drives architecture decisions, SLA budgets, and capacity planning.
- Database query planning is fundamentally about choosing between different Big-O tradeoffs (sequential scan vs index scan vs hash join).
- Always validate theoretical Big-O with benchmarks -- constant factors, cache effects, and real-world data distributions can invalidate theoretical analysis.
- In distributed systems, network call count often dominates; optimize for fewer round trips, not fewer CPU operations.
- Document complexity implications in architecture decision records so future engineers understand why choices were made.
- Big-O misleads when constant factors are large, inputs are small, cache effects dominate, or worst cases are astronomically unlikely.
