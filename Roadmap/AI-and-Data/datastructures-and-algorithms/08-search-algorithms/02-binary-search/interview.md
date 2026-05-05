# Binary Search — Interview Questions & Coding Challenges

> **Audience:** Anyone preparing for a software engineering interview at companies that ask DSA questions. Includes conceptual questions across levels and 7 coding challenges with full Go / Java / Python solutions.

---

## Section A — Conceptual Questions

### Junior level

**Q1. What does binary search require of its input?**
A sorted sequence with O(1) random access. The sort order must be consistent with the comparator used to navigate.

**Q2. What's the time complexity of binary search?**
O(log n) average and worst case. O(1) best case (target at first probed `mid`).

**Q3. What's the space complexity?**
O(1) iterative; O(log n) recursive due to call stack.

**Q4. Why do we write `mid = lo + (hi - lo) / 2` instead of `(lo + hi) / 2`?**
To avoid integer overflow when `lo + hi` exceeds the maximum signed integer value. The bug famously existed in `java.util.Arrays.binarySearch` until 2006.

**Q5. What's the difference between `lo <= hi` and `lo < hi` as loop conditions?**
`lo <= hi` is correct for **inclusive** bounds (`hi = n - 1`). `lo < hi` is correct for **half-open** bounds (`hi = n`). Mixing produces off-by-one bugs.

**Q6. How many comparisons does binary search make in the worst case?**
`⌈log₂(n + 1)⌉`. For `n = 1,000,000`, that's 20.

**Q7. What does binary search return when the target is not present?**
By convention, `-1` (Java, Go, C++ via lower_bound + check). Python's `bisect_left` returns the insertion point. Java's `Arrays.binarySearch` returns `-(insertion_point) - 1`.

### Middle level

**Q8. What's the difference between `bisect_left` and `bisect_right`?**
`bisect_left(a, x)` returns the leftmost insertion point — before any equal elements. `bisect_right(a, x)` returns the rightmost — after any equals. Their difference equals the count of equal elements.

**Q9. How do you find the first and last occurrence of a target?**
Two binary searches:
- First: when `arr[mid] == target`, record `mid` and continue searching left (`hi = mid - 1`).
- Last: when `arr[mid] == target`, record `mid` and continue searching right (`lo = mid + 1`).

Or use `bisect_left(a, t)` and `bisect_right(a, t) - 1`.

**Q10. What is "binary search on the answer" / parametric search?**
Instead of searching over array indices, you binary-search over the **space of possible answers**. The predicate `feasible(x)` must be monotonic. Examples: minimum eating speed, maximum capacity, square root, median.

**Q11. When do you use exponential search?**
When the data is sorted but its length is unknown or unbounded (streams, very large files). Find the upper bound by doubling, then binary-search the resulting range. Total: O(log k) where `k` is the target's position.

**Q12. What's ternary search and when do you use it?**
For finding the maximum (or minimum) of a **unimodal** function. Each step probes two points dividing the range into thirds. O(log n) but with a slightly worse constant than binary search.

**Q13. Can binary search be applied to a linked list?**
Technically yes, but each `mid` access costs O(n), making the total O(n log n) — worse than linear search. Don't.

### Senior level

**Q14. Why do databases use B-trees instead of sorted arrays for indexes?**
B-trees have higher branching factor (100–1000), so depth is `O(log_B n) ≈ 4–5`. Each level corresponds to one disk page read. Sorted arrays would require `O(log₂ n) ≈ 30` cache misses for billion-row data.

**Q15. How does `git bisect` work?**
It binary-searches the commit DAG between a known good and known bad commit, asking the user (or a script) at each step whether the midpoint commit is good or bad. Finds the introducing commit in O(log n) tests.

**Q16. Why might binary search beat a hash table in production?**
Range queries, ordered iteration, predecessor/successor, k-th order statistics, predictable worst case, cache compactness, persistent storage. Hash tables only beat binary search at exact-match O(1) lookup.

**Q17. What's the Eytzinger layout?**
A reordering of a sorted array into BFS order of its implicit balanced BST. Cache-friendly: each probe accesses adjacent memory, so the hardware prefetcher helps. 2–3× faster than classical binary search on cache-busting input sizes.

### Professional level

**Q18. Prove binary search correctness.**
Loop invariant: at the top of every iteration, the answer (if it exists) lies in `[lo, hi]`. Base: `lo = 0, hi = n - 1` covers the whole array. Inductive step: each branch shrinks to the half guaranteed to still contain the answer. Termination: `hi - lo` strictly decreases each iteration.

**Q19. What's the information-theoretic lower bound for comparison-based search on a sorted array?**
`Ω(log n)` comparisons. There are `n + 1` possible outcomes (n indices + "not found"), and a binary decision tree has depth `≥ ⌈log₂(n + 1)⌉`.

**Q20. Why does interpolation search achieve O(log log n)?**
On uniformly distributed data, the linear-interpolation guess for `mid` lands within `√n` of the answer in O(1), and the recurrence `T(n) = T(√n) + O(1)` solves to O(log log n).

---

## Section B — Coding Challenges

### Challenge 1 — Classic Binary Search (LeetCode 704)

> Given a sorted array of integers and a target, return its index, or `-1` if absent.

**Go:**
```go
func search(nums []int, target int) int {
    lo, hi := 0, len(nums)-1
    for lo <= hi {
        mid := lo + (hi-lo)/2
        switch {
        case nums[mid] == target:
            return mid
        case nums[mid] < target:
            lo = mid + 1
        default:
            hi = mid - 1
        }
    }
    return -1
}
```

**Java:**
```java
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) lo = mid + 1;
        else                    hi = mid - 1;
    }
    return -1;
}
```

**Python:**
```python
def search(nums: list[int], target: int) -> int:
    lo, hi = 0, len(nums) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if nums[mid] == target:
            return mid
        if nums[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1
```

**Complexity:** O(log n) time, O(1) space.

---

### Challenge 2 — Find First and Last Position (LeetCode 34)

> Given a sorted array with possible duplicates, return `[firstIndex, lastIndex]` of the target. Return `[-1, -1]` if absent.

**Go:**
```go
func searchRange(nums []int, target int) []int {
    first := lowerBound(nums, target)
    if first == len(nums) || nums[first] != target {
        return []int{-1, -1}
    }
    last := lowerBound(nums, target+1) - 1
    return []int{first, last}
}
func lowerBound(a []int, t int) int {
    lo, hi := 0, len(a)
    for lo < hi {
        mid := lo + (hi-lo)/2
        if a[mid] < t { lo = mid + 1 } else { hi = mid }
    }
    return lo
}
```

**Java:**
```java
public int[] searchRange(int[] nums, int target) {
    int first = lowerBound(nums, target);
    if (first == nums.length || nums[first] != target) return new int[]{-1, -1};
    int last = lowerBound(nums, target + 1) - 1;
    return new int[]{first, last};
}
private int lowerBound(int[] a, int t) {
    int lo = 0, hi = a.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (a[mid] < t) lo = mid + 1;
        else            hi = mid;
    }
    return lo;
}
```

**Python:**
```python
from bisect import bisect_left, bisect_right
def searchRange(nums, target):
    lo = bisect_left(nums, target)
    hi = bisect_right(nums, target) - 1
    return [lo, hi] if lo <= hi else [-1, -1]
```

**Complexity:** O(log n) time, O(1) space.

---

### Challenge 3 — Search Insert Position (LeetCode 35)

> Given a sorted array and a target, return the index where it would be inserted (or its existing index if found).

**Go:**
```go
func searchInsert(nums []int, target int) int {
    lo, hi := 0, len(nums)
    for lo < hi {
        mid := lo + (hi-lo)/2
        if nums[mid] < target { lo = mid + 1 } else { hi = mid }
    }
    return lo
}
```

**Java:**
```java
public int searchInsert(int[] nums, int target) {
    int lo = 0, hi = nums.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] < target) lo = mid + 1;
        else                    hi = mid;
    }
    return lo;
}
```

**Python:**
```python
from bisect import bisect_left
def searchInsert(nums, target):
    return bisect_left(nums, target)
```

**Complexity:** O(log n) time, O(1) space.

---

### Challenge 4 — Search in Rotated Sorted Array (LeetCode 33)

> A sorted array has been rotated at an unknown pivot. Find target in O(log n).

**Strategy:** at each step, exactly one half (`[lo, mid]` or `[mid, hi]`) is sorted. Decide which, then check whether the target lies in the sorted half.

**Go:**
```go
func searchRotated(nums []int, target int) int {
    lo, hi := 0, len(nums)-1
    for lo <= hi {
        mid := lo + (hi-lo)/2
        if nums[mid] == target {
            return mid
        }
        if nums[lo] <= nums[mid] {              // left half is sorted
            if nums[lo] <= target && target < nums[mid] {
                hi = mid - 1
            } else {
                lo = mid + 1
            }
        } else {                                 // right half is sorted
            if nums[mid] < target && target <= nums[hi] {
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
    }
    return -1
}
```

**Java:**
```java
public int search(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;
        if (nums[lo] <= nums[mid]) {
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
            else                                          lo = mid + 1;
        } else {
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
            else                                          hi = mid - 1;
        }
    }
    return -1;
}
```

**Python:**
```python
def searchRotated(nums, target):
    lo, hi = 0, len(nums) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if nums[mid] == target:
            return mid
        if nums[lo] <= nums[mid]:
            if nums[lo] <= target < nums[mid]:
                hi = mid - 1
            else:
                lo = mid + 1
        else:
            if nums[mid] < target <= nums[hi]:
                lo = mid + 1
            else:
                hi = mid - 1
    return -1
```

**Complexity:** O(log n) time, O(1) space. Caveat: with duplicates (LC 81), worst case degrades to O(n).

---

### Challenge 5 — Find Peak Element (LeetCode 162)

> Find any index `i` where `nums[i] > nums[i-1]` and `nums[i] > nums[i+1]`. Treat out-of-bounds as `-∞`. The array may not be sorted.

**Strategy:** even though unsorted, the array has a "peak property" — comparing `nums[mid]` with `nums[mid+1]` tells you which half guarantees a peak.

**Go:**
```go
func findPeakElement(nums []int) int {
    lo, hi := 0, len(nums)-1
    for lo < hi {
        mid := lo + (hi-lo)/2
        if nums[mid] > nums[mid+1] {
            hi = mid
        } else {
            lo = mid + 1
        }
    }
    return lo
}
```

**Java:**
```java
public int findPeakElement(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[mid + 1]) hi = mid;
        else                           lo = mid + 1;
    }
    return lo;
}
```

**Python:**
```python
def findPeakElement(nums):
    lo, hi = 0, len(nums) - 1
    while lo < hi:
        mid = (lo + hi) // 2
        if nums[mid] > nums[mid + 1]:
            hi = mid
        else:
            lo = mid + 1
    return lo
```

**Complexity:** O(log n) time, O(1) space. Why it works: the array starts ascending from `-∞` and ends descending to `-∞`, so somewhere a peak must exist; the comparison tells you which half contains one.

---

### Challenge 6 — Median of Two Sorted Arrays (LeetCode 4)

> Two sorted arrays of sizes `m` and `n`. Find the median in O(log(min(m, n))).

**Strategy:** binary-search the partition point in the smaller array such that everything left of the combined partition is ≤ everything right.

**Python:**
```python
def findMedianSortedArrays(a, b):
    if len(a) > len(b):
        a, b = b, a
    m, n = len(a), len(b)
    half = (m + n + 1) // 2
    lo, hi = 0, m
    while lo <= hi:
        i = (lo + hi) // 2
        j = half - i
        a_left  = a[i - 1] if i > 0 else float('-inf')
        a_right = a[i]     if i < m else float('inf')
        b_left  = b[j - 1] if j > 0 else float('-inf')
        b_right = b[j]     if j < n else float('inf')
        if a_left <= b_right and b_left <= a_right:
            if (m + n) % 2:
                return max(a_left, b_left)
            return (max(a_left, b_left) + min(a_right, b_right)) / 2
        elif a_left > b_right:
            hi = i - 1
        else:
            lo = i + 1
```

**Go:**
```go
func findMedianSortedArrays(a, b []int) float64 {
    if len(a) > len(b) { a, b = b, a }
    m, n := len(a), len(b)
    half := (m + n + 1) / 2
    lo, hi := 0, m
    inf := int(1<<31 - 1); ninf := -inf
    for lo <= hi {
        i := (lo + hi) / 2
        j := half - i
        aL, aR := ninf, inf
        if i > 0 { aL = a[i-1] }
        if i < m { aR = a[i] }
        bL, bR := ninf, inf
        if j > 0 { bL = b[j-1] }
        if j < n { bR = b[j] }
        if aL <= bR && bL <= aR {
            if (m+n)%2 == 1 { return float64(max(aL, bL)) }
            return float64(max(aL, bL) + min(aR, bR)) / 2.0
        } else if aL > bR { hi = i - 1 } else { lo = i + 1 }
    }
    return 0
}
func max(a, b int) int { if a > b { return a }; return b }
func min(a, b int) int { if a < b { return a }; return b }
```

**Java:**
```java
public double findMedianSortedArrays(int[] a, int[] b) {
    if (a.length > b.length) { int[] t = a; a = b; b = t; }
    int m = a.length, n = b.length, half = (m + n + 1) / 2;
    int lo = 0, hi = m;
    while (lo <= hi) {
        int i = (lo + hi) / 2;
        int j = half - i;
        int aL = i == 0     ? Integer.MIN_VALUE : a[i - 1];
        int aR = i == m     ? Integer.MAX_VALUE : a[i];
        int bL = j == 0     ? Integer.MIN_VALUE : b[j - 1];
        int bR = j == n     ? Integer.MAX_VALUE : b[j];
        if (aL <= bR && bL <= aR) {
            if (((m + n) & 1) == 1) return Math.max(aL, bL);
            return (Math.max(aL, bL) + Math.min(aR, bR)) / 2.0;
        } else if (aL > bR) hi = i - 1;
        else                lo = i + 1;
    }
    return 0;
}
```

**Complexity:** O(log(min(m, n))) time, O(1) space. The "hard one" — most candidates struggle with the partition correctness proof.

---

### Challenge 7 — Koko Eating Bananas (LeetCode 875)

> Koko has `piles[]` of bananas and `h` hours. Each hour she eats `min(pile, k)` from one pile. Find the minimum integer `k` such that she finishes in ≤ `h` hours.

**Strategy:** parametric binary search. The predicate `canFinish(k)` is monotonic in `k`.

**Go:**
```go
func minEatingSpeed(piles []int, h int) int {
    hi := 0
    for _, p := range piles {
        if p > hi { hi = p }
    }
    lo := 1
    for lo < hi {
        mid := lo + (hi-lo)/2
        if hoursNeeded(piles, mid) <= h {
            hi = mid
        } else {
            lo = mid + 1
        }
    }
    return lo
}
func hoursNeeded(piles []int, k int) int {
    h := 0
    for _, p := range piles {
        h += (p + k - 1) / k
    }
    return h
}
```

**Java:**
```java
public int minEatingSpeed(int[] piles, int h) {
    int lo = 1, hi = 0;
    for (int p : piles) hi = Math.max(hi, p);
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (hoursNeeded(piles, mid) <= h) hi = mid;
        else                              lo = mid + 1;
    }
    return lo;
}
private long hoursNeeded(int[] piles, int k) {
    long total = 0;
    for (int p : piles) total += (p + k - 1L) / k;
    return total;
}
```

**Python:**
```python
def minEatingSpeed(piles, h):
    def hours(k):
        return sum((p + k - 1) // k for p in piles)
    lo, hi = 1, max(piles)
    while lo < hi:
        mid = (lo + hi) // 2
        if hours(mid) <= h:
            hi = mid
        else:
            lo = mid + 1
    return lo
```

**Complexity:** O(n log(max(piles))) time, O(1) space. The poster child for "binary search on the answer".

---

## Section C — Common Interview Pitfalls

### Pitfall 1 — Off-by-one on `hi`
Mixing inclusive and half-open: writing `hi = nums.length` (half-open) but then the loop `while (lo <= hi)` (inclusive). Picks one style and locks in.

### Pitfall 2 — Forgetting integer overflow
`(lo + hi) / 2` in Java/C/C++ on huge inputs overflows to negative. Always `lo + (hi - lo) / 2`. Recruiters look for this.

### Pitfall 3 — Wrong direction on equality (find first / last)
For "find first", on equality you must continue searching **left** (`hi = mid - 1`), not return immediately. Many candidates write the exact-search version and try to "patch" duplicates.

### Pitfall 4 — Rotated array with duplicates
If duplicates are allowed (LC 81), `nums[lo] == nums[mid]` can occur, and you cannot tell which half is sorted. Worst-case degrades to O(n) — degrade gracefully by `lo++` and continue.

### Pitfall 5 — Median of two sorted: wrong half size on odd total
Using `half = (m + n) // 2` instead of `(m + n + 1) // 2` for odd total length. The `+1` ensures the left partition is the larger one when total is odd, so the median is `max(left)`.

### Pitfall 6 — Parametric search: predicate not monotonic
Always sanity-check that `feasible(x) → feasible(x + 1)`. If not monotonic, binary search is invalid. Sketch the predicate values for small inputs.

### Pitfall 7 — Returning `-1` vs returning insertion point
Java returns `-(ip) - 1`; Python returns `ip` directly. If you confuse them, your code "works" on found cases and silently breaks on not-found. Always test both.

### Pitfall 8 — Recursion stack overflow
For `n = 10^9`, recursion depth is ~30 — fine. But if your bounds are wrong (e.g., `lo = mid` instead of `lo = mid + 1`), you can recurse forever. Iterative is safer.

### Pitfall 9 — Float precision
For float ranges, `lo == hi` may never hold. Use a fixed iteration count (~100) or a precision threshold (`hi - lo < 1e-9`).

### Pitfall 10 — Using built-in incorrectly
`Arrays.binarySearch` does not guarantee returning the **first** match for duplicates. If the question requires first/last, write the loop yourself.

---

## Section D — Mock Interview Drill (15 minutes)

A typical 45-minute interview will combine:
1. **Easy warmup** (5 min): classic binary search.
2. **Medium variation** (15 min): rotated array, find peak, or first/last position.
3. **Harder twist** (15 min): parametric search (Koko, ship packages) or median of two sorted.
4. **Discussion** (10 min): complexity, edge cases, what would change for streaming data, B-tree comparison.

**Drill questions to practice aloud:**
- *"Walk me through binary search step by step on `[1, 3, 5, 7, 9]` searching for 7."*
- *"What if the array can have duplicates? How do you find the first occurrence?"*
- *"How would you binary-search a 100 GB sorted file?"* (Answer: seek-based, `look`-style.)
- *"Why does Java's binary search return a negative number?"* (Encodes insertion point.)
- *"Could you use binary search on a linked list?"* (No — O(n) per probe.)

Memorize the "find first true" template, the integer overflow fix, and the bisect_left/bisect_right semantics. With those three, 80% of binary-search interview questions become straightforward.
