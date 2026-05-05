# Linear Search — Interview

## Table of Contents

1. [Junior Questions](#junior-questions)
2. [Mid-Level Questions](#mid-level-questions)
3. [Senior Questions](#senior-questions)
4. [System Design Variants](#system-design-variants)
5. [Coding Challenges](#coding-challenges)
6. [Common Pitfalls](#common-pitfalls)

---

## Junior Questions

**Q1. What is linear search and how does it work?**

A: Linear search is the simplest search algorithm. You walk through the array from left to right, comparing each element to the target. If you find a match, return the index. If you reach the end without finding it, return -1. Time is O(n), space is O(1).

**Q2. What's the time complexity of linear search?**

A:
- Best case: O(1) — target is at index 0.
- Average case: O(n) — typically n/2 comparisons.
- Worst case: O(n) — target at the last index, or absent.
- Space: O(1) — just a loop counter.

**Q3. What's the difference between linear search and binary search?**

A: Linear search works on any iterable (sorted or not), in O(n) time. Binary search requires a **sorted** array and runs in O(log n). Linear is simpler and often faster for small arrays (n < ~20) due to cache effects and lower constant factors.

**Q4. Does linear search require the array to be sorted?**

A: No. That's its main advantage. It works on completely unsorted data, which is why it's the default search for unindexed collections.

**Q5. What happens if the array is empty?**

A: The loop body never executes, and the function returns -1 immediately. Always handle this case — don't assume the array has at least one element.

**Q6. How would you handle "find all occurrences" instead of "find first"?**

A: Don't return on first match. Append the index to a list and continue scanning. Return the list at the end.

```python
def find_all(arr, target):
    return [i for i, v in enumerate(arr) if v == target]
```

**Q7. Why would you ever use linear search over binary search?**

A: Several reasons:
- Data is **unsorted** and you don't want to sort it (sorting is O(n log n)).
- Data is **streaming** — you can't random-access it.
- Array is **small** (n < ~20) — linear is faster due to cache effects.
- You only need **one query** — sorting first to enable binary search isn't worth it.
- Data structure doesn't support random access (e.g., a singly-linked list).

---

## Mid-Level Questions

**Q8. What's a "sentinel" linear search and why is it faster?**

A: Place a copy of the target at the end of the array as a guarantee the loop terminates. This lets you drop the bounds check (`i < n`) inside the loop, leaving only the equality check. Each iteration does one comparison instead of two. Speedup is ~5-15% on tight loops.

**Q9. How does the CPU make linear search fast?**

A:
1. **Prefetcher** detects sequential access and pre-loads cache lines.
2. **Branch predictor** correctly predicts the "not equal" branch nearly every iteration.
3. **SIMD** instructions (AVX2/512) compare 8-16 elements per cycle.
4. **Loop unrolling** processes multiple elements per loop iteration.

For small `n`, these effects make linear search faster than binary search despite worse asymptotic complexity.

**Q10. How would you parallelize linear search?**

A: Split the array into chunks (one per worker thread). Each worker scans its chunk; the first to find the target signals the others to stop. The work-coordination via a shared atomic flag is the main overhead. Speedup is roughly equal to the number of cores for large arrays.

**Q11. When does linear search beat binary search empirically?**

A: For n ≤ ~32 on modern x86. The constant-factor cost of binary search (mid calculation, branch misprediction, cache jumps) outweighs its log-n savings until the array is large enough for the asymptotic difference to matter.

**Q12. What's the difference between `indexOf` and `includes` in JavaScript?**

A: `indexOf` uses `===` for comparison and returns `-1` for absent; it can't find `NaN` because `NaN === NaN` is false. `includes` uses **SameValueZero** equality, which treats `NaN` as equal to itself, so it can find `NaN`.

```javascript
[NaN].indexOf(NaN);    // -1
[NaN].includes(NaN);   // true
```

**Q13. How do you find the second occurrence of a value?**

A: Track how many times you've seen the target; return the index when count reaches 2.

```python
def find_nth(arr, target, n):
    count = 0
    for i, v in enumerate(arr):
        if v == target:
            count += 1
            if count == n:
                return i
    return -1
```

**Q14. What's the cache-line cost of linear search vs binary search?**

A: Linear: O(n / B) cache lines (one read per B contiguous elements, where B is the line size). Binary: O(log n) cache lines, but each is essentially a random read at the top of the tree, defeating the prefetcher. Linear is more **bandwidth-friendly**; binary is more **operation-count-friendly**.

---

## Senior Questions

**Q15. When should you NOT add an index for a query?**

A:
- Small tables (< 1000 rows fit in 1-2 pages).
- Low-selectivity predicates (>30% of rows match).
- Predicates the index can't use (`LIKE '%pat%'`, `func(col) = ...`).
- Write-heavy tables where index maintenance dominates.
- One-off analytical queries.

In all these cases, a sequential scan (linear search at the row level) is the right tool.

**Q16. How does ripgrep beat grep?**

A: SIMD-accelerated literal search via `memchr`, parallel file scanning, mmap, and `.gitignore` filtering. The core is still linear search — just **vectorized** linear search at memory bandwidth.

**Q17. Why is array-of-structs slower than struct-of-arrays for linear search?**

A: With AoS, each element occupies a full cache line (or more). Searching by one field touches all the other fields uselessly, wasting cache. With SoA, the field you're searching on is contiguous, so 16 elements fit in one cache line — 16× more cache-efficient.

**Q18. Can you describe an O(1) linear search?**

A: O(1) is the **best case** of linear search — when the target is at index 0. The average case is O(n/2) and worst is O(n). You can't have an algorithm that is O(1) for arbitrary input on unordered data — that's information-theoretically impossible (lower bound Ω(n)). An "O(1) linear search" only makes sense when paired with a self-organizing list (move-to-front), where hot keys converge to the front.

**Q19. Prove linear search is asymptotically optimal for unordered data.**

A: Adversary argument. Any algorithm makes at most k comparisons. The adversary answers "no" to all of them. After k < n comparisons, at least one position is unexamined; the adversary places the target there. The algorithm cannot have correctly identified that position. So any correct algorithm must make at least n comparisons — Ω(n).

**Q20. How would you design a search system for a billion-row table where seq scans take 2 minutes?**

A: Multi-layered:
1. **Bloom filter** to short-circuit likely-absent queries in microseconds.
2. **Inverted index** for full-text or attribute search.
3. **Columnar storage** with min/max statistics per chunk for block-skipping.
4. **Materialized views** for common aggregations.
5. **Cache layer** (Redis/memcached) for hot queries.
6. **Approximate search** (ANN, MinHash) where exact answers aren't required.
7. **Partitioning / sharding** to parallelize across machines.

Linear search of 1B rows is the **fallback**, not the primary path. But for cold or rare queries, it's still the right escape hatch — let it run async via a job queue, deliver results when ready.

---

## System Design Variants

**Q21. Design a real-time blocklist check for a CDN edge.**

Constraints: 100M IPs in blocklist, 10K queries/sec per edge, <1ms latency.

A:
- **Bloom filter** in memory (10 bits/IP → 125 MB) for fast probable-membership.
- On Bloom hit, fall through to a **hash set** for exact verification.
- Linear search would be 100M ops per query → unacceptable.
- Update via gossip / pub-sub from a central control plane.

Linear search is **not** the answer here, but the question forces the candidate to articulate why.

**Q22. Design a "find-the-typo" feature in a 200-line essay.**

A:
- 200 lines × ~10 words = 2000 words.
- Linear scan, comparing each word to a dictionary (also linear scan? No — use a trie or hash set).
- For each word: O(1) hash-set lookup. Total: O(n) where n = word count.
- Linear search of words against the dictionary would be O(n × m) where m = dictionary size — way too slow.

This shows when linear-on-the-outside, indexed-on-the-inside is the right pattern.

---

## Coding Challenges

### Challenge 1: Basic Linear Search

**Problem:** Implement linear search returning the index of the first occurrence, or -1 if absent.

**Go:**
```go
func LinearSearch(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i
        }
    }
    return -1
}
```

**Java:**
```java
public static int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) return i;
    }
    return -1;
}
```

**Python:**
```python
def linear_search(arr: list[int], target: int) -> int:
    for i, v in enumerate(arr):
        if v == target:
            return i
    return -1
```

### Challenge 2: Find All Indices

**Problem:** Return a list of all indices where target appears.

**Go:**
```go
func FindAll(arr []int, target int) []int {
    var out []int
    for i, v := range arr {
        if v == target {
            out = append(out, i)
        }
    }
    return out
}
```

**Java:**
```java
public static List<Integer> findAll(int[] arr, int target) {
    List<Integer> out = new ArrayList<>();
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) out.add(i);
    }
    return out;
}
```

**Python:**
```python
def find_all(arr, target):
    return [i for i, v in enumerate(arr) if v == target]
```

### Challenge 3: Find Min and Max in Unsorted Array

**Problem:** Single pass, find both min and max.

**Go:**
```go
func MinMax(arr []int) (min, max int, ok bool) {
    if len(arr) == 0 { return 0, 0, false }
    min, max = arr[0], arr[0]
    for _, v := range arr[1:] {
        if v < min { min = v }
        if v > max { max = v }
    }
    return min, max, true
}
```

**Java:**
```java
public static int[] minMax(int[] arr) {
    if (arr.length == 0) throw new IllegalArgumentException("empty");
    int min = arr[0], max = arr[0];
    for (int i = 1; i < arr.length; i++) {
        if (arr[i] < min) min = arr[i];
        if (arr[i] > max) max = arr[i];
    }
    return new int[]{min, max};
}
```

**Python:**
```python
def min_max(arr):
    if not arr:
        return None
    lo = hi = arr[0]
    for v in arr[1:]:
        if v < lo: lo = v
        elif v > hi: hi = v
    return lo, hi
```

### Challenge 4: Two-Sum (Brute Force O(n²))

**Problem:** Find indices `i, j` such that `arr[i] + arr[j] == target`.

**Go:**
```go
func TwoSumBrute(arr []int, target int) (int, int, bool) {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] + arr[j] == target {
                return i, j, true
            }
        }
    }
    return 0, 0, false
}
```

**Java:**
```java
public static int[] twoSumBrute(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[i] + arr[j] == target) return new int[]{i, j};
        }
    }
    return new int[]{-1, -1};
}
```

**Python:**
```python
def two_sum_brute(arr, target):
    n = len(arr)
    for i in range(n):
        for j in range(i + 1, n):
            if arr[i] + arr[j] == target:
                return (i, j)
    return None
```

**Optimization note:** O(n) using a hash map — store seen values; for each new value, check if `target - value` is in the map. The brute version is the linear-search "starting point."

### Challenge 5: Find a Peak Element

**Problem:** A peak is an element greater than both neighbors. Find any peak. (Edges count if greater than their single neighbor.)

**Go:**
```go
func FindPeak(arr []int) int {
    n := len(arr)
    if n == 0 { return -1 }
    if n == 1 { return 0 }
    if arr[0] >= arr[1] { return 0 }
    if arr[n-1] >= arr[n-2] { return n - 1 }
    for i := 1; i < n-1; i++ {
        if arr[i] >= arr[i-1] && arr[i] >= arr[i+1] {
            return i
        }
    }
    return -1
}
```

**Java:**
```java
public static int findPeak(int[] arr) {
    int n = arr.length;
    if (n == 0) return -1;
    if (n == 1) return 0;
    if (arr[0] >= arr[1]) return 0;
    if (arr[n-1] >= arr[n-2]) return n - 1;
    for (int i = 1; i < n - 1; i++) {
        if (arr[i] >= arr[i-1] && arr[i] >= arr[i+1]) return i;
    }
    return -1;
}
```

**Python:**
```python
def find_peak(arr):
    n = len(arr)
    if n == 0: return -1
    if n == 1: return 0
    if arr[0] >= arr[1]: return 0
    if arr[-1] >= arr[-2]: return n - 1
    for i in range(1, n - 1):
        if arr[i] >= arr[i-1] and arr[i] >= arr[i+1]:
            return i
    return -1
```

**Optimization note:** This O(n) linear-scan version is the baseline. There's an O(log n) binary-search variant if you can compare neighbors — a great follow-up question.

---

## Common Pitfalls

| Pitfall | Symptom | Fix |
|---------|---------|-----|
| Off-by-one in loop bound | Misses last element or out-of-bounds | Use `range(len(arr))` or `i < n`, never `i <= n` |
| Returning bool instead of index | Caller can't read the element | Return `int`; let caller derive bool with `idx >= 0` |
| Forgetting "not found" return | Function returns `None` / undefined behavior | Always have a `return -1` after the loop |
| Continuing after match | Returns last instead of first | `return` immediately or `break` |
| Mutating array during iteration | Skipped elements / `ConcurrentModificationException` | Iterate over a copy or in reverse |
| Using `is` for equality (Python) | Misses equal-value-but-different-object | Use `==` |
| Comparing floats with `==` | Misses values off by 1e-15 | Use tolerance |
| Linear search inside a loop (quadratic) | Slow for large n | Pre-build a hash set |
| Not stopping at first match | Wasted work on found-early cases | Early return |
| Recursing instead of iterating | Stack overflow on large n | Use a loop |

If you make any of these mistakes in an interview, you've signaled a junior-level miss. Double-check the loop, the return value, and the early-exit before submitting.
