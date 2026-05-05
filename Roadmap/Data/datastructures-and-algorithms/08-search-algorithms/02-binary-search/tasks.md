# Binary Search — Practice Tasks

> 15 graded practice problems with full Go / Java / Python solutions. Each task starts with a **problem statement**, gives **constraints**, then provides three implementations. Difficulty grows from "warmup" to "this is hard".

---

## Task 1 — Classic Binary Search

**Problem.** Given a sorted array `nums[]` and a target `t`, return the index of `t`, or `-1` if absent.

**Constraints.** `1 <= n <= 10^5`, distinct values.

**Go:**
```go
func bsearch(nums []int, t int) int {
    lo, hi := 0, len(nums)-1
    for lo <= hi {
        m := lo + (hi-lo)/2
        if nums[m] == t { return m }
        if nums[m] < t  { lo = m + 1 } else { hi = m - 1 }
    }
    return -1
}
```

**Java:**
```java
int bsearch(int[] nums, int t) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int m = lo + (hi - lo) / 2;
        if (nums[m] == t) return m;
        if (nums[m] < t) lo = m + 1; else hi = m - 1;
    }
    return -1;
}
```

**Python:**
```python
def bsearch(nums, t):
    lo, hi = 0, len(nums) - 1
    while lo <= hi:
        m = (lo + hi) // 2
        if nums[m] == t: return m
        if nums[m] < t: lo = m + 1
        else: hi = m - 1
    return -1
```

---

## Task 2 — Recursive Variant

**Problem.** Same as Task 1, but written recursively.

**Go:**
```go
func bsearchRec(nums []int, t int) int { return rec(nums, t, 0, len(nums)-1) }
func rec(a []int, t, lo, hi int) int {
    if lo > hi { return -1 }
    m := lo + (hi-lo)/2
    if a[m] == t { return m }
    if a[m] < t  { return rec(a, t, m+1, hi) }
    return rec(a, t, lo, m-1)
}
```

**Java:**
```java
int bsearchRec(int[] a, int t) { return rec(a, t, 0, a.length - 1); }
int rec(int[] a, int t, int lo, int hi) {
    if (lo > hi) return -1;
    int m = lo + (hi - lo) / 2;
    if (a[m] == t) return m;
    if (a[m] < t)  return rec(a, t, m + 1, hi);
    return rec(a, t, lo, m - 1);
}
```

**Python:**
```python
def bsearch_rec(a, t):
    def rec(lo, hi):
        if lo > hi: return -1
        m = (lo + hi) // 2
        if a[m] == t: return m
        if a[m] < t:  return rec(m + 1, hi)
        return rec(lo, m - 1)
    return rec(0, len(a) - 1)
```

---

## Task 3 — Find First Occurrence

**Problem.** Sorted array with duplicates. Return the smallest index `i` where `a[i] == t`, or `-1`.

**Go:**
```go
func findFirst(a []int, t int) int {
    lo, hi, ans := 0, len(a)-1, -1
    for lo <= hi {
        m := lo + (hi-lo)/2
        if a[m] == t { ans = m; hi = m - 1 } else
        if a[m] < t  { lo = m + 1 } else { hi = m - 1 }
    }
    return ans
}
```

**Java:**
```java
int findFirst(int[] a, int t) {
    int lo = 0, hi = a.length - 1, ans = -1;
    while (lo <= hi) {
        int m = lo + (hi - lo) / 2;
        if (a[m] == t) { ans = m; hi = m - 1; }
        else if (a[m] < t) lo = m + 1;
        else               hi = m - 1;
    }
    return ans;
}
```

**Python:**
```python
def find_first(a, t):
    lo, hi, ans = 0, len(a) - 1, -1
    while lo <= hi:
        m = (lo + hi) // 2
        if a[m] == t: ans = m; hi = m - 1
        elif a[m] < t: lo = m + 1
        else: hi = m - 1
    return ans
```

---

## Task 4 — Find Last Occurrence

**Problem.** Symmetric to Task 3 — return the largest index `i` where `a[i] == t`.

**Go:**
```go
func findLast(a []int, t int) int {
    lo, hi, ans := 0, len(a)-1, -1
    for lo <= hi {
        m := lo + (hi-lo)/2
        if a[m] == t { ans = m; lo = m + 1 } else
        if a[m] < t  { lo = m + 1 } else { hi = m - 1 }
    }
    return ans
}
```

**Java:**
```java
int findLast(int[] a, int t) {
    int lo = 0, hi = a.length - 1, ans = -1;
    while (lo <= hi) {
        int m = lo + (hi - lo) / 2;
        if (a[m] == t) { ans = m; lo = m + 1; }
        else if (a[m] < t) lo = m + 1;
        else               hi = m - 1;
    }
    return ans;
}
```

**Python:**
```python
def find_last(a, t):
    lo, hi, ans = 0, len(a) - 1, -1
    while lo <= hi:
        m = (lo + hi) // 2
        if a[m] == t: ans = m; lo = m + 1
        elif a[m] < t: lo = m + 1
        else: hi = m - 1
    return ans
```

---

## Task 5 — `lower_bound` and `upper_bound`

**Problem.** Implement both `lower_bound(a, t)` (smallest `i` with `a[i] >= t`) and `upper_bound(a, t)` (smallest `i` with `a[i] > t`).

**Go:**
```go
func lowerBound(a []int, t int) int {
    lo, hi := 0, len(a)
    for lo < hi {
        m := lo + (hi-lo)/2
        if a[m] < t { lo = m + 1 } else { hi = m }
    }
    return lo
}
func upperBound(a []int, t int) int {
    lo, hi := 0, len(a)
    for lo < hi {
        m := lo + (hi-lo)/2
        if a[m] <= t { lo = m + 1 } else { hi = m }
    }
    return lo
}
```

**Java:**
```java
int lowerBound(int[] a, int t) {
    int lo = 0, hi = a.length;
    while (lo < hi) { int m = lo + (hi - lo) / 2; if (a[m] < t) lo = m + 1; else hi = m; }
    return lo;
}
int upperBound(int[] a, int t) {
    int lo = 0, hi = a.length;
    while (lo < hi) { int m = lo + (hi - lo) / 2; if (a[m] <= t) lo = m + 1; else hi = m; }
    return lo;
}
```

**Python:**
```python
from bisect import bisect_left, bisect_right
def lower_bound(a, t): return bisect_left(a, t)
def upper_bound(a, t): return bisect_right(a, t)
```

---

## Task 6 — Search in Rotated Sorted Array

**Problem.** A sorted array was rotated at an unknown pivot (e.g., `[4,5,6,7,0,1,2]`). Find target in O(log n).

**Go:**
```go
func searchRotated(a []int, t int) int {
    lo, hi := 0, len(a)-1
    for lo <= hi {
        m := lo + (hi-lo)/2
        if a[m] == t { return m }
        if a[lo] <= a[m] {
            if a[lo] <= t && t < a[m] { hi = m - 1 } else { lo = m + 1 }
        } else {
            if a[m] < t && t <= a[hi] { lo = m + 1 } else { hi = m - 1 }
        }
    }
    return -1
}
```

**Java:** identical structure to Go (see `interview.md` Challenge 4).

**Python:**
```python
def search_rotated(a, t):
    lo, hi = 0, len(a) - 1
    while lo <= hi:
        m = (lo + hi) // 2
        if a[m] == t: return m
        if a[lo] <= a[m]:
            if a[lo] <= t < a[m]: hi = m - 1
            else: lo = m + 1
        else:
            if a[m] < t <= a[hi]: lo = m + 1
            else: hi = m - 1
    return -1
```

---

## Task 7 — Find Peak Element

**Problem.** Find any index `i` where `a[i] > a[i-1]` and `a[i] > a[i+1]` (treat out-of-bounds as `-∞`). Array may be unsorted.

**Go:**
```go
func findPeak(a []int) int {
    lo, hi := 0, len(a)-1
    for lo < hi {
        m := lo + (hi-lo)/2
        if a[m] > a[m+1] { hi = m } else { lo = m + 1 }
    }
    return lo
}
```

**Java/Python:** mirror.

```java
int findPeak(int[] a) {
    int lo = 0, hi = a.length - 1;
    while (lo < hi) { int m = lo + (hi - lo) / 2; if (a[m] > a[m+1]) hi = m; else lo = m + 1; }
    return lo;
}
```
```python
def find_peak(a):
    lo, hi = 0, len(a) - 1
    while lo < hi:
        m = (lo + hi) // 2
        if a[m] > a[m+1]: hi = m
        else: lo = m + 1
    return lo
```

---

## Task 8 — Integer Square Root via Binary Search

**Problem.** Given `x >= 0`, return `⌊√x⌋` (without using `math.sqrt`).

**Go:**
```go
func isqrt(x int) int {
    if x < 2 { return x }
    lo, hi := 1, x/2 + 1
    for lo < hi {
        m := lo + (hi-lo)/2
        if m*m > x { hi = m } else { lo = m + 1 }
    }
    return lo - 1
}
```

**Java:**
```java
int isqrt(int x) {
    if (x < 2) return x;
    long lo = 1, hi = x / 2 + 1;
    while (lo < hi) {
        long m = lo + (hi - lo) / 2;
        if (m * m > x) hi = m; else lo = m + 1;
    }
    return (int)(lo - 1);
}
```

**Python:**
```python
def isqrt(x):
    if x < 2: return x
    lo, hi = 1, x // 2 + 1
    while lo < hi:
        m = (lo + hi) // 2
        if m * m > x: hi = m
        else: lo = m + 1
    return lo - 1
```

---

## Task 9 — Binary Search on Float Range

**Problem.** Find `r` such that `r * r * r = x` (cube root) with precision `1e-9`.

**Python:**
```python
def cbrt(x, eps=1e-9):
    sign = 1 if x >= 0 else -1
    x = abs(x)
    lo, hi = 0.0, max(1.0, x)
    for _ in range(100):
        m = (lo + hi) / 2
        if m * m * m > x:
            hi = m
        else:
            lo = m
    return sign * (lo + hi) / 2
```

**Go:**
```go
func cbrt(x float64) float64 {
    sign := 1.0
    if x < 0 { sign = -1.0; x = -x }
    lo, hi := 0.0, math.Max(1.0, x)
    for i := 0; i < 100; i++ {
        m := (lo + hi) / 2
        if m*m*m > x { hi = m } else { lo = m }
    }
    return sign * (lo + hi) / 2
}
```

**Java:**
```java
double cbrt(double x) {
    double sign = x < 0 ? -1 : 1; x = Math.abs(x);
    double lo = 0, hi = Math.max(1, x);
    for (int i = 0; i < 100; i++) {
        double m = (lo + hi) / 2;
        if (m * m * m > x) hi = m; else lo = m;
    }
    return sign * (lo + hi) / 2;
}
```

---

## Task 10 — Capacity to Ship Packages Within D Days (LeetCode 1011)

**Problem.** Given `weights[]` (must be shipped in given order) and `D` days, find the minimum truck capacity that allows shipping in `D` days.

**Python:**
```python
def shipWithinDays(weights, D):
    def days_needed(cap):
        days, load = 1, 0
        for w in weights:
            if load + w > cap:
                days += 1; load = 0
            load += w
        return days
    lo, hi = max(weights), sum(weights)
    while lo < hi:
        m = (lo + hi) // 2
        if days_needed(m) <= D: hi = m
        else: lo = m + 1
    return lo
```

**Go:**
```go
func shipWithinDays(weights []int, D int) int {
    daysNeeded := func(cap int) int {
        days, load := 1, 0
        for _, w := range weights {
            if load+w > cap { days++; load = 0 }
            load += w
        }
        return days
    }
    lo, hi := 0, 0
    for _, w := range weights {
        if w > lo { lo = w }
        hi += w
    }
    for lo < hi {
        m := lo + (hi-lo)/2
        if daysNeeded(m) <= D { hi = m } else { lo = m + 1 }
    }
    return lo
}
```

**Java:**
```java
public int shipWithinDays(int[] weights, int D) {
    int lo = 0, hi = 0;
    for (int w : weights) { lo = Math.max(lo, w); hi += w; }
    while (lo < hi) {
        int m = lo + (hi - lo) / 2;
        if (daysNeeded(weights, m) <= D) hi = m;
        else                             lo = m + 1;
    }
    return lo;
}
private int daysNeeded(int[] w, int cap) {
    int days = 1, load = 0;
    for (int x : w) {
        if (load + x > cap) { days++; load = 0; }
        load += x;
    }
    return days;
}
```

---

## Task 11 — Split Array Largest Sum (LeetCode 410)

**Problem.** Split `nums[]` into `m` non-empty contiguous subarrays minimizing the largest subarray sum. Return that minimized maximum.

**Python:**
```python
def splitArray(nums, m):
    def can_split(max_sum):
        parts, cur = 1, 0
        for x in nums:
            if cur + x > max_sum:
                parts += 1; cur = 0
            cur += x
        return parts <= m
    lo, hi = max(nums), sum(nums)
    while lo < hi:
        mid = (lo + hi) // 2
        if can_split(mid): hi = mid
        else: lo = mid + 1
    return lo
```

**Go/Java:** structurally identical to Task 10 with `parts <= m` predicate.

---

## Task 12 — Search in 2D Sorted Matrix (LeetCode 74)

**Problem.** Matrix where each row is sorted left-to-right and each row's first value > previous row's last value. Find target.

**Strategy:** treat the matrix as a flat array of length `m*n`, binary-search by index, decompose to `(row, col) = (idx / n, idx % n)`.

**Python:**
```python
def searchMatrix(mat, t):
    m, n = len(mat), len(mat[0])
    lo, hi = 0, m * n - 1
    while lo <= hi:
        idx = (lo + hi) // 2
        v = mat[idx // n][idx % n]
        if v == t: return True
        if v < t: lo = idx + 1
        else: hi = idx - 1
    return False
```

**Go:**
```go
func searchMatrix(mat [][]int, t int) bool {
    m, n := len(mat), len(mat[0])
    lo, hi := 0, m*n-1
    for lo <= hi {
        idx := (lo + hi) / 2
        v := mat[idx/n][idx%n]
        if v == t { return true }
        if v < t { lo = idx + 1 } else { hi = idx - 1 }
    }
    return false
}
```

**Java:**
```java
public boolean searchMatrix(int[][] mat, int t) {
    int m = mat.length, n = mat[0].length;
    int lo = 0, hi = m * n - 1;
    while (lo <= hi) {
        int idx = lo + (hi - lo) / 2;
        int v = mat[idx / n][idx % n];
        if (v == t) return true;
        if (v < t) lo = idx + 1; else hi = idx - 1;
    }
    return false;
}
```

---

## Task 13 — Exponential Search

**Problem.** Sorted array of unknown length (or known but the target is near the front). Implement exponential + binary.

**Python:**
```python
def exponential_search(a, t):
    if not a: return -1
    if a[0] == t: return 0
    n, b = len(a), 1
    while b < n and a[b] < t:
        b *= 2
    lo, hi = b // 2, min(b, n - 1)
    while lo <= hi:
        m = (lo + hi) // 2
        if a[m] == t: return m
        if a[m] < t: lo = m + 1
        else: hi = m - 1
    return -1
```

**Go:**
```go
func expSearch(a []int, t int) int {
    if len(a) == 0 { return -1 }
    if a[0] == t { return 0 }
    n, b := len(a), 1
    for b < n && a[b] < t { b *= 2 }
    lo, hi := b/2, b
    if hi >= n { hi = n - 1 }
    for lo <= hi {
        m := (lo + hi) / 2
        if a[m] == t { return m }
        if a[m] < t { lo = m + 1 } else { hi = m - 1 }
    }
    return -1
}
```

**Java:**
```java
int expSearch(int[] a, int t) {
    if (a.length == 0) return -1;
    if (a[0] == t) return 0;
    int n = a.length, b = 1;
    while (b < n && a[b] < t) b *= 2;
    int lo = b / 2, hi = Math.min(b, n - 1);
    while (lo <= hi) {
        int m = lo + (hi - lo) / 2;
        if (a[m] == t) return m;
        if (a[m] < t) lo = m + 1; else hi = m - 1;
    }
    return -1;
}
```

---

## Task 14 — Ternary Search for Unimodal Function Maximum

**Problem.** Given a unimodal function `f` on `[lo, hi]` (integer domain), find the index of the maximum.

**Python:**
```python
def ternary_max(lo, hi, f):
    while hi - lo > 2:
        m1 = lo + (hi - lo) // 3
        m2 = hi - (hi - lo) // 3
        if f(m1) < f(m2): lo = m1 + 1
        else: hi = m2 - 1
    return max(range(lo, hi + 1), key=f)
```

**Go:**
```go
func ternaryMax(lo, hi int, f func(int) int) int {
    for hi-lo > 2 {
        m1 := lo + (hi-lo)/3
        m2 := hi - (hi-lo)/3
        if f(m1) < f(m2) { lo = m1 + 1 } else { hi = m2 - 1 }
    }
    best := lo
    for i := lo + 1; i <= hi; i++ { if f(i) > f(best) { best = i } }
    return best
}
```

**Java:**
```java
int ternaryMax(int lo, int hi, java.util.function.IntUnaryOperator f) {
    while (hi - lo > 2) {
        int m1 = lo + (hi - lo) / 3;
        int m2 = hi - (hi - lo) / 3;
        if (f.applyAsInt(m1) < f.applyAsInt(m2)) lo = m1 + 1;
        else                                     hi = m2 - 1;
    }
    int best = lo;
    for (int i = lo + 1; i <= hi; i++) if (f.applyAsInt(i) > f.applyAsInt(best)) best = i;
    return best;
}
```

---

## Task 15 — Parallel Binary Search Across Multiple Keys

**Problem.** Given a sorted array `a[]` and a list of `q` queries `targets[]`, return all answers efficiently. Naive: `q * log(n)`. Use a sort + merge approach for total `O((n + q) log q)`.

**Idea:** sort the queries, then walk both arrays once with a two-pointer merge. For each query in sorted order, advance the array pointer until you cross the target, then record the answer. This is the **batched binary search** trick.

**Python:**
```python
def batch_search(a, targets):
    """For each target, return its lower_bound index in a."""
    n, q = len(a), len(targets)
    order = sorted(range(q), key=lambda i: targets[i])
    answers = [0] * q
    ai = 0
    for qi in order:
        t = targets[qi]
        while ai < n and a[ai] < t:
            ai += 1
        answers[qi] = ai
    return answers
```

**Go:**
```go
func batchSearch(a, targets []int) []int {
    n, q := len(a), len(targets)
    order := make([]int, q)
    for i := range order { order[i] = i }
    sort.Slice(order, func(i, j int) bool { return targets[order[i]] < targets[order[j]] })
    answers := make([]int, q)
    ai := 0
    for _, qi := range order {
        for ai < n && a[ai] < targets[qi] { ai++ }
        answers[qi] = ai
    }
    return answers
}
```

**Java:**
```java
int[] batchSearch(int[] a, int[] targets) {
    int n = a.length, q = targets.length;
    Integer[] order = new Integer[q];
    for (int i = 0; i < q; i++) order[i] = i;
    java.util.Arrays.sort(order, (x, y) -> targets[x] - targets[y]);
    int[] answers = new int[q];
    int ai = 0;
    for (int qi : order) {
        while (ai < n && a[ai] < targets[qi]) ai++;
        answers[qi] = ai;
    }
    return answers;
}
```

**Complexity:** O((n + q) log q). Beats `O(q log n)` when `q >> log n`.

---

## Self-Check

After completing these tasks, you should be able to:

- [ ] Write classic binary search from memory in any of Go / Java / Python.
- [ ] Choose between inclusive `[lo, hi]` and half-open `[lo, hi)` based on the problem.
- [ ] Implement `lower_bound` / `upper_bound` and explain the `<` vs `<=` difference.
- [ ] Recognize parametric search problems and write the predicate.
- [ ] Apply binary search to floats with a fixed iteration count.
- [ ] Use exponential search for unbounded ranges.
- [ ] Use ternary search for unimodal functions.
- [ ] Identify when binary search is the wrong tool (linked lists, unsorted data, exact-match-only).

If any of these feel uncertain, revisit the relevant section of `junior.md` or `middle.md` and redo the task without looking at the solution.
