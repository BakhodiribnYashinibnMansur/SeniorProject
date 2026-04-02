# Time vs Space Complexity — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Deeper Concepts](#deeper-concepts)
3. [Comparison with Alternatives](#comparison-with-alternatives)
4. [Advanced Patterns](#advanced-patterns)
5. [Graph and Tree Applications](#graph-and-tree-applications)
6. [Dynamic Programming Integration](#dynamic-programming-integration)
7. [Code Examples](#code-examples)
8. [Error Handling](#error-handling)
9. [Performance Analysis](#performance-analysis)
10. [Best Practices](#best-practices)
11. [Visual Animation](#visual-animation)
12. [Summary](#summary)

---

## Introduction

> Focus: "Why does it work?" and "When should I choose this?"

At the middle level, you move beyond simply categorizing algorithms by Big-O. You learn to analyze **why** certain trade-offs exist, understand amortized complexity, recognize when average-case analysis matters more than worst-case, and make principled decisions about time-space trade-offs based on real system constraints — memory budgets, latency SLAs, and input distributions.

---

## Deeper Concepts

### Amortized Complexity

Not every operation in a data structure costs the same. A dynamic array's `append` is usually O(1), but occasionally triggers an O(n) resize. **Amortized analysis** averages the cost over a sequence of operations. For a dynamic array that doubles its capacity, the amortized cost per append is O(1) — the occasional expensive resize is "paid for" by the many cheap appends.

```text
n appends to a dynamic array (doubling strategy):
  Total copies: 1 + 2 + 4 + 8 + ... + n = 2n - 1
  Amortized cost per append: (2n - 1) / n ≈ O(1)
```

### Worst-Case vs Average-Case vs Expected-Case

| Analysis Type | What It Measures | When to Use |
|--------------|-----------------|-------------|
| Worst-case | Maximum cost over all inputs of size n | Security-critical, real-time systems |
| Average-case | Expected cost assuming uniform random input | General-purpose applications |
| Expected-case | Expected cost for randomized algorithms (over random choices, not inputs) | Randomized quicksort, skip lists |

Quicksort is O(n²) worst-case but O(n log n) average-case. With random pivot selection, the **expected-case** is O(n log n) regardless of input — this is why randomized quicksort is preferred in practice.

### Space Complexity: Stack vs Heap

Memory in most programs lives in two places:

| Property | Stack | Heap |
|----------|-------|------|
| **Allocation** | Automatic (function calls) | Manual (`new`, `make`, `malloc`) |
| **Speed** | Very fast (pointer bump) | Slower (allocator overhead) |
| **Size** | Limited (1-8 MB typical) | Large (limited by RAM) |
| **Lifetime** | Function scope | Until freed/GC'd |
| **Fragmentation** | None | Can fragment over time |

Recursive algorithms use stack space; iterative algorithms with explicit data structures use heap space. Both count toward space complexity, but stack space is more limited.

### Cache Locality and Practical Performance

Big-O ignores constant factors, but in practice **cache locality** matters enormously. Arrays are stored contiguously in memory — sequential access hits the CPU cache. Linked lists scatter nodes across the heap — each access may cause a cache miss.

```text
Array traversal:  [1][2][3][4][5][6][7][8]  → sequential memory → cache-friendly
                   ↑ ↑ ↑ ↑ ↑ ↑ ↑ ↑
                   All in same cache line

Linked list:      [1]→  [2]→  [3]→  [4]→   → scattered memory → cache-unfriendly
                   ↑      ↑      ↑      ↑
                   Different cache lines
```

Result: Even though both are O(n) for traversal, arrays can be 10-100x faster due to cache effects.

---

## Comparison with Alternatives

### Time-Space Trade-Off Strategies

| Strategy | Time | Space | Example |
|----------|------|-------|---------|
| Brute force | O(n²) or worse | O(1) | Nested loops for pair search |
| Hash-based | O(n) | O(n) | Hash set for duplicate detection |
| Sort-based | O(n log n) | O(1) to O(n) | Sort then binary search |
| Precomputation | O(1) lookup | O(n) to O(n²) build | Prefix sums, lookup tables |
| Streaming | O(n) | O(1) or O(k) | Running median, sliding window |
| Memoization | O(states) | O(states) | DP with hash map cache |

**Choose brute force when:** n is small (< 1000), correctness matters more than speed
**Choose hash-based when:** n is large, memory is available, need O(1) lookups
**Choose sort-based when:** data is nearly sorted, or multiple queries on same data
**Choose precomputation when:** many queries on static data (prefix sums, sparse tables)

### Complexity Class Comparison

| Class | n=10 | n=100 | n=1,000 | n=10,000 | n=100,000 |
|-------|------|-------|---------|----------|-----------|
| O(1) | 1 | 1 | 1 | 1 | 1 |
| O(log n) | 3 | 7 | 10 | 13 | 17 |
| O(n) | 10 | 100 | 1,000 | 10,000 | 100,000 |
| O(n log n) | 33 | 664 | 9,966 | 132,877 | 1,660,964 |
| O(n²) | 100 | 10,000 | 1,000,000 | 10⁸ | 10¹⁰ |
| O(2ⁿ) | 1,024 | 10³⁰ | ∞ | ∞ | ∞ |

---

## Advanced Patterns

### Pattern: Prefix Sum — O(n) Precompute, O(1) Query

Precompute cumulative sums so any range sum can be answered in O(1) time at the cost of O(n) space.

#### Go

```go
package main

import "fmt"

type PrefixSum struct {
    prefix []int
}

func NewPrefixSum(arr []int) *PrefixSum {
    prefix := make([]int, len(arr)+1)
    for i, v := range arr {
        prefix[i+1] = prefix[i] + v
    }
    return &PrefixSum{prefix: prefix}
}

// O(1) range sum query
func (ps *PrefixSum) RangeSum(left, right int) int {
    return ps.prefix[right+1] - ps.prefix[left]
}

func main() {
    arr := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    ps := NewPrefixSum(arr) // O(n) time, O(n) space
    fmt.Println("Sum [2..5]:", ps.RangeSum(2, 5)) // 3+4+5+6 = 18
    fmt.Println("Sum [0..9]:", ps.RangeSum(0, 9)) // 55
}
```

#### Java

```java
public class PrefixSum {
    private int[] prefix;

    public PrefixSum(int[] arr) {
        prefix = new int[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            prefix[i + 1] = prefix[i] + arr[i];
        }
    }

    // O(1) range sum query
    public int rangeSum(int left, int right) {
        return prefix[right + 1] - prefix[left];
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        PrefixSum ps = new PrefixSum(arr);
        System.out.println("Sum [2..5]: " + ps.rangeSum(2, 5)); // 18
        System.out.println("Sum [0..9]: " + ps.rangeSum(0, 9)); // 55
    }
}
```

#### Python

```python
class PrefixSum:
    def __init__(self, arr):
        self.prefix = [0] * (len(arr) + 1)
        for i, v in enumerate(arr):
            self.prefix[i + 1] = self.prefix[i] + v

    # O(1) range sum query
    def range_sum(self, left, right):
        return self.prefix[right + 1] - self.prefix[left]

arr = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
ps = PrefixSum(arr)  # O(n) time, O(n) space
print("Sum [2..5]:", ps.range_sum(2, 5))  # 18
print("Sum [0..9]:", ps.range_sum(0, 9))  # 55
```

### Pattern: Two Pointers — O(n) Time, O(1) Space

#### Go

```go
func twoSum(arr []int, target int) (int, int) {
    left, right := 0, len(arr)-1
    for left < right {
        sum := arr[left] + arr[right]
        if sum == target {
            return left, right
        } else if sum < target {
            left++
        } else {
            right--
        }
    }
    return -1, -1
}
```

#### Java

```java
public static int[] twoSum(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) return new int[]{left, right};
        else if (sum < target) left++;
        else right--;
    }
    return new int[]{-1, -1};
}
```

#### Python

```python
def two_sum(arr, target):
    left, right = 0, len(arr) - 1
    while left < right:
        s = arr[left] + arr[right]
        if s == target:
            return left, right
        elif s < target:
            left += 1
        else:
            right -= 1
    return -1, -1
```

### Pattern: Sliding Window — O(n) Time, O(1) Space

#### Go

```go
func maxSumSubarray(arr []int, k int) int {
    windowSum := 0
    for i := 0; i < k; i++ {
        windowSum += arr[i]
    }
    maxSum := windowSum
    for i := k; i < len(arr); i++ {
        windowSum += arr[i] - arr[i-k] // slide the window
        if windowSum > maxSum {
            maxSum = windowSum
        }
    }
    return maxSum
}
```

#### Java

```java
public static int maxSumSubarray(int[] arr, int k) {
    int windowSum = 0;
    for (int i = 0; i < k; i++) windowSum += arr[i];
    int maxSum = windowSum;
    for (int i = k; i < arr.length; i++) {
        windowSum += arr[i] - arr[i - k];
        maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
}
```

#### Python

```python
def max_sum_subarray(arr, k):
    window_sum = sum(arr[:k])
    max_sum = window_sum
    for i in range(k, len(arr)):
        window_sum += arr[i] - arr[i - k]
        max_sum = max(max_sum, window_sum)
    return max_sum
```

---

## Graph and Tree Applications

```mermaid
graph TD
    A[Time-Space Trade-offs] --> B[BFS — O(V+E) time, O(V) space]
    A --> C[DFS — O(V+E) time, O(V) space]
    A --> D[Dijkstra — O((V+E)log V) time, O(V) space]
    A --> E[Floyd-Warshall — O(V³) time, O(V²) space]
```

### BFS: O(V+E) Time, O(V) Space

#### Go

```go
func bfs(graph map[int][]int, start int) []int {
    visited := map[int]bool{start: true}
    queue := []int{start}
    order := []int{}
    for len(queue) > 0 {
        node := queue[0]
        queue = queue[1:]
        order = append(order, node)
        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                visited[neighbor] = true
                queue = append(queue, neighbor)
            }
        }
    }
    return order // Space: O(V) for visited + queue
}
```

#### Java

```java
import java.util.*;

public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
    Set<Integer> visited = new HashSet<>(Set.of(start));
    Queue<Integer> queue = new LinkedList<>(List.of(start));
    List<Integer> order = new ArrayList<>();
    while (!queue.isEmpty()) {
        int node = queue.poll();
        order.add(node);
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (visited.add(neighbor)) {
                queue.add(neighbor);
            }
        }
    }
    return order; // Space: O(V) for visited + queue
}
```

#### Python

```python
from collections import deque

def bfs(graph, start):
    visited = {start}
    queue = deque([start])
    order = []
    while queue:
        node = queue.popleft()
        order.append(node)
        for neighbor in graph.get(node, []):
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(neighbor)
    return order  # Space: O(V) for visited + queue
```

### DFS: O(V+E) Time, O(V) Space (Recursive = Stack Space)

#### Go

```go
func dfs(graph map[int][]int, start int) []int {
    visited := map[int]bool{}
    order := []int{}
    var helper func(int)
    helper = func(node int) {
        visited[node] = true
        order = append(order, node)
        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                helper(neighbor) // O(V) call stack depth in worst case
            }
        }
    }
    helper(start)
    return order
}
```

#### Java

```java
import java.util.*;

public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int start) {
    Set<Integer> visited = new HashSet<>();
    List<Integer> order = new ArrayList<>();
    dfsHelper(graph, start, visited, order);
    return order;
}

private static void dfsHelper(Map<Integer, List<Integer>> graph, int node,
                               Set<Integer> visited, List<Integer> order) {
    visited.add(node);
    order.add(node);
    for (int neighbor : graph.getOrDefault(node, List.of())) {
        if (!visited.contains(neighbor)) {
            dfsHelper(graph, neighbor, visited, order);
        }
    }
}
```

#### Python

```python
def dfs(graph, start):
    visited = set()
    order = []

    def helper(node):
        visited.add(node)
        order.append(node)
        for neighbor in graph.get(node, []):
            if neighbor not in visited:
                helper(neighbor)

    helper(start)
    return order
```

---

## Dynamic Programming Integration

DP is the quintessential time-space trade-off: store computed results (O(states) space) to avoid recomputation (reduce exponential time to polynomial).

#### Go

```go
// O(2^n) time, O(n) space — naive recursion
func fibNaive(n int) int {
    if n <= 1 {
        return n
    }
    return fibNaive(n-1) + fibNaive(n-2)
}

// O(n) time, O(n) space — memoization (top-down DP)
func fibMemo(n int, memo map[int]int) int {
    if n <= 1 {
        return n
    }
    if val, ok := memo[n]; ok {
        return val
    }
    memo[n] = fibMemo(n-1, memo) + fibMemo(n-2, memo)
    return memo[n]
}

// O(n) time, O(1) space — tabulation with space optimization
func fibOptimal(n int) int {
    if n <= 1 {
        return n
    }
    prev2, prev1 := 0, 1
    for i := 2; i <= n; i++ {
        curr := prev1 + prev2
        prev2 = prev1
        prev1 = curr
    }
    return prev1
}
```

#### Java

```java
import java.util.HashMap;

public class Fibonacci {
    // O(2^n) time, O(n) space
    public static int fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    // O(n) time, O(n) space
    static HashMap<Integer, Integer> memo = new HashMap<>();
    public static int fibMemo(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        int result = fibMemo(n - 1) + fibMemo(n - 2);
        memo.put(n, result);
        return result;
    }

    // O(n) time, O(1) space
    public static int fibOptimal(int n) {
        if (n <= 1) return n;
        int prev2 = 0, prev1 = 1;
        for (int i = 2; i <= n; i++) {
            int curr = prev1 + prev2;
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }
}
```

#### Python

```python
from functools import lru_cache

# O(2^n) time, O(n) space
def fib_naive(n):
    if n <= 1:
        return n
    return fib_naive(n - 1) + fib_naive(n - 2)

# O(n) time, O(n) space
@lru_cache(maxsize=None)
def fib_memo(n):
    if n <= 1:
        return n
    return fib_memo(n - 1) + fib_memo(n - 2)

# O(n) time, O(1) space
def fib_optimal(n):
    if n <= 1:
        return n
    prev2, prev1 = 0, 1
    for _ in range(2, n + 1):
        curr = prev1 + prev2
        prev2 = prev1
        prev1 = curr
    return prev1
```

---

## Code Examples

### Full Benchmark: Time vs Space Across Strategies

#### Go

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    sizes := []int{100, 1000, 10000, 100000}
    for _, n := range sizes {
        arr := make([]int, n)
        for i := range arr {
            arr[i] = i
        }

        // O(n²) time, O(1) space
        start := time.Now()
        hasDupBrute(arr)
        brute := time.Since(start)

        // O(n) time, O(n) space
        start = time.Now()
        hasDupHash(arr)
        hash := time.Since(start)

        fmt.Printf("n=%6d | Brute: %10v | Hash: %10v | Speedup: %.1fx\n",
            n, brute, hash, float64(brute)/float64(hash))
    }
}

func hasDupBrute(arr []int) bool {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] == arr[j] {
                return true
            }
        }
    }
    return false
}

func hasDupHash(arr []int) bool {
    seen := make(map[int]bool, len(arr))
    for _, v := range arr {
        if seen[v] {
            return true
        }
        seen[v] = true
    }
    return false
}
```

#### Java

```java
import java.util.HashSet;

public class Benchmark {
    public static void main(String[] args) {
        int[] sizes = {100, 1000, 10000, 100000};
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;

            long start = System.nanoTime();
            hasDupBrute(arr);
            double brute = (System.nanoTime() - start) / 1_000_000.0;

            start = System.nanoTime();
            hasDupHash(arr);
            double hash = (System.nanoTime() - start) / 1_000_000.0;

            System.out.printf("n=%6d | Brute: %8.3f ms | Hash: %8.3f ms | Speedup: %.1fx%n",
                n, brute, hash, brute / hash);
        }
    }

    static boolean hasDupBrute(int[] arr) {
        for (int i = 0; i < arr.length; i++)
            for (int j = i + 1; j < arr.length; j++)
                if (arr[i] == arr[j]) return true;
        return false;
    }

    static boolean hasDupHash(int[] arr) {
        HashSet<Integer> seen = new HashSet<>(arr.length);
        for (int v : arr) {
            if (!seen.add(v)) return true;
        }
        return false;
    }
}
```

#### Python

```python
import timeit

def has_dup_brute(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False

def has_dup_hash(arr):
    seen = set()
    for v in arr:
        if v in seen:
            return True
        seen.add(v)
    return False

sizes = [100, 1000, 10000]
for n in sizes:
    arr = list(range(n))
    brute = timeit.timeit(lambda: has_dup_brute(arr), number=5) / 5
    hash_t = timeit.timeit(lambda: has_dup_hash(arr), number=5) / 5
    speedup = brute / hash_t if hash_t > 0 else float('inf')
    print(f"n={n:>6} | Brute: {brute*1000:>8.3f} ms | Hash: {hash_t*1000:>8.3f} ms | Speedup: {speedup:.1f}x")
```

---

## Error Handling

| Scenario | What Goes Wrong | Correct Approach |
|----------|----------------|-----------------|
| Memoization without bound | Unbounded memory growth for large state space | Use LRU cache with size limit or bottom-up DP |
| Recursive DFS on large graph | Stack overflow (O(V) depth) | Convert to iterative DFS with explicit stack |
| Hash map with poor hash function | Degenerate to O(n) per lookup → O(n²) total | Use language-standard hash or verify distribution |
| Pre-allocating too much | OutOfMemoryError for O(n²) space structures | Check available memory, consider streaming |

---

## Performance Analysis

#### Go

```go
import (
    "fmt"
    "time"
    "runtime"
)

func benchmarkMemory(n int) {
    var m runtime.MemStats
    runtime.GC()
    runtime.ReadMemStats(&m)
    before := m.Alloc

    data := make([]int, n)
    for i := range data { data[i] = i }

    runtime.ReadMemStats(&m)
    after := m.Alloc

    fmt.Printf("n=%d: allocated ~%d KB\n", n, (after-before)/1024)
}
```

#### Java

```java
public static void benchmarkMemory(int n) {
    Runtime rt = Runtime.getRuntime();
    rt.gc();
    long before = rt.totalMemory() - rt.freeMemory();
    int[] data = new int[n];
    long after = rt.totalMemory() - rt.freeMemory();
    System.out.printf("n=%d: allocated ~%d KB%n", n, (after - before) / 1024);
}
```

#### Python

```python
import sys

def benchmark_memory(n):
    data = list(range(n))
    size_bytes = sys.getsizeof(data) + sum(sys.getsizeof(x) for x in data)
    print(f"n={n}: ~{size_bytes // 1024} KB")
```

---

## Best Practices

- Profile before optimizing — `go test -bench`, Java JMH, Python `timeit`
- For competitive programming: n ≤ 10⁶ usually means O(n) or O(n log n) is required
- Prefer O(n) time + O(n) space over O(n²) time + O(1) space in most real-world applications — memory is cheap, latency isn't
- When space is critical (embedded, mobile), prefer in-place algorithms and streaming
- Document your complexity analysis: `// Time: O(n log n), Space: O(n)`
- Consider cache locality — arrays beat linked lists in practice for sequential access even at the same Big-O

---

## Visual Animation

> See [`animation.html`](./animation.html) for interactive visualization.
>
> Middle-level animation includes:
> - Comparison of brute force vs hash-based approaches with live operation counters
> - Memory usage visualization as allocations happen
> - Side-by-side runtime comparison across input sizes
> - Cache locality demonstration (array vs linked list traversal)

---

## Summary

At the middle level, time-space analysis goes beyond simple Big-O. You understand amortized complexity, worst vs average vs expected case, and the practical impact of cache locality. You use patterns like prefix sums, two pointers, and sliding windows to achieve optimal trade-offs. You know when to trade space for time (hash maps, memoization) and when to preserve space (in-place algorithms, streaming). Master benchmarking in all three languages to validate your theoretical analysis.
