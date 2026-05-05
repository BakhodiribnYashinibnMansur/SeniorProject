# Linear Search — Practice Tasks

15 tasks of increasing difficulty. Each task has Go, Java, and Python starter code. Solve the task, then check against the reference behavior.

## Table of Contents

1. [Task 1 — Basic Linear Search](#task-1--basic-linear-search)
2. [Task 2 — Find All Occurrences](#task-2--find-all-occurrences)
3. [Task 3 — Count Occurrences](#task-3--count-occurrences)
4. [Task 4 — Find First and Last Index](#task-4--find-first-and-last-index)
5. [Task 5 — Substring Search (Naive)](#task-5--substring-search-naive)
6. [Task 6 — 2D Matrix Search](#task-6--2d-matrix-search)
7. [Task 7 — Find Majority Element](#task-7--find-majority-element)
8. [Task 8 — Search in Rotated Array](#task-8--search-in-rotated-array)
9. [Task 9 — Parallel Linear Search](#task-9--parallel-linear-search)
10. [Task 10 — SIMD Linear Search](#task-10--simd-linear-search)
11. [Task 11 — Sentinel Linear Search](#task-11--sentinel-linear-search)
12. [Task 12 — Recursive Linear Search](#task-12--recursive-linear-search)
13. [Task 13 — Search Linked List](#task-13--search-linked-list)
14. [Task 14 — Search with Custom Predicate](#task-14--search-with-custom-predicate)
15. [Task 15 — Benchmark vs Binary Search](#task-15--benchmark-vs-binary-search)

---

## Task 1 — Basic Linear Search

Implement linear search returning the index of the first occurrence, or -1 if absent.

### Starter — Go
```go
func LinearSearch(arr []int, target int) int {
    // TODO
    return -1
}
```

### Starter — Java
```java
public static int linearSearch(int[] arr, int target) {
    // TODO
    return -1;
}
```

### Starter — Python
```python
def linear_search(arr, target):
    # TODO
    return -1
```

### Expected
- `linear_search([3, 7, 1, 4], 1)` → `2`
- `linear_search([], 5)` → `-1`
- `linear_search([1, 2, 3], 9)` → `-1`

---

## Task 2 — Find All Occurrences

Return a list of all indices where target appears.

### Starter — Go
```go
func FindAll(arr []int, target int) []int {
    // TODO
    return nil
}
```

### Starter — Java
```java
public static java.util.List<Integer> findAll(int[] arr, int target) {
    // TODO
    return java.util.Collections.emptyList();
}
```

### Starter — Python
```python
def find_all(arr, target):
    # TODO
    return []
```

### Expected
- `find_all([1, 2, 1, 3, 1], 1)` → `[0, 2, 4]`
- `find_all([1, 2, 3], 5)` → `[]`
- `find_all([], 5)` → `[]`

---

## Task 3 — Count Occurrences

Return the count of occurrences (without storing indices).

### Starter — Go
```go
func Count(arr []int, target int) int {
    // TODO
    return 0
}
```

### Starter — Java
```java
public static int count(int[] arr, int target) {
    // TODO
    return 0;
}
```

### Starter — Python
```python
def count(arr, target):
    # TODO
    return 0
```

### Expected
- `count([1, 2, 1, 3, 1], 1)` → `3`
- `count([5, 5, 5], 5)` → `3`
- `count([1, 2, 3], 9)` → `0`

---

## Task 4 — Find First and Last Index

Return a tuple `(first_index, last_index)`. If not found, return `(-1, -1)`.

### Starter — Go
```go
func FirstAndLast(arr []int, target int) (int, int) {
    // TODO
    return -1, -1
}
```

### Starter — Java
```java
public static int[] firstAndLast(int[] arr, int target) {
    // TODO
    return new int[]{-1, -1};
}
```

### Starter — Python
```python
def first_and_last(arr, target):
    # TODO
    return (-1, -1)
```

### Expected
- `first_and_last([1, 2, 1, 3, 1], 1)` → `(0, 4)`
- `first_and_last([5], 5)` → `(0, 0)`
- `first_and_last([1, 2, 3], 9)` → `(-1, -1)`

**Hint:** Single pass; track `first` and `last` separately.

---

## Task 5 — Substring Search (Naive)

Find the first occurrence of `pattern` in `text`. Return the start index or -1.

### Starter — Go
```go
func SubstringSearch(text, pattern string) int {
    // TODO  (do NOT use strings.Index)
    return -1
}
```

### Starter — Java
```java
public static int substringSearch(String text, String pattern) {
    // TODO  (do NOT use indexOf)
    return -1;
}
```

### Starter — Python
```python
def substring_search(text, pattern):
    # TODO  (do NOT use 'in' or .find())
    return -1
```

### Expected
- `substring_search("hello world", "world")` → `6`
- `substring_search("aaab", "aab")` → `1`
- `substring_search("abc", "")` → `0` (empty pattern matches at index 0)

**Hint:** For each `i` in `[0, len(text) - len(pattern)]`, check if `text[i:i+len(pattern)] == pattern`. O(n*m).

---

## Task 6 — 2D Matrix Search

Search a 2D matrix for a target value. Return `(row, col)` or `(-1, -1)`.

### Starter — Go
```go
func MatrixSearch(matrix [][]int, target int) (int, int) {
    // TODO
    return -1, -1
}
```

### Starter — Java
```java
public static int[] matrixSearch(int[][] matrix, int target) {
    // TODO
    return new int[]{-1, -1};
}
```

### Starter — Python
```python
def matrix_search(matrix, target):
    # TODO
    return (-1, -1)
```

### Expected
- `matrix_search([[1,2],[3,4]], 4)` → `(1, 1)`
- `matrix_search([[1,2],[3,4]], 9)` → `(-1, -1)`
- `matrix_search([], 1)` → `(-1, -1)`

**Hint:** Two nested loops. Iterate row-major (outer = row, inner = col) for cache friendliness.

---

## Task 7 — Find Majority Element

A majority element appears **more than n/2** times in an array of length n. Return it, or `-1` if none.

### Starter — Go
```go
func FindMajority(arr []int) int {
    // TODO
    return -1
}
```

### Starter — Java
```java
public static int findMajority(int[] arr) {
    // TODO
    return -1;
}
```

### Starter — Python
```python
def find_majority(arr):
    # TODO
    return -1
```

### Expected
- `find_majority([3, 3, 4, 2, 4, 4, 2, 4, 4])` → `4`
- `find_majority([1, 2, 3])` → `-1`
- `find_majority([5, 5])` → `5`

**Hint:** Brute O(n²) — for each element, count its occurrences via linear search. Optimization: Boyer-Moore Voting in O(n) / O(1).

---

## Task 8 — Search in Rotated Array

A sorted array has been rotated at an unknown pivot, e.g., `[4, 5, 6, 7, 0, 1, 2]`. Find the target via linear search. Return index or -1.

### Starter — Go
```go
func SearchRotated(arr []int, target int) int {
    // TODO  (linear scan; binary-search variant is harder)
    return -1
}
```

### Starter — Java
```java
public static int searchRotated(int[] arr, int target) {
    // TODO
    return -1;
}
```

### Starter — Python
```python
def search_rotated(arr, target):
    # TODO
    return -1
```

### Expected
- `search_rotated([4,5,6,7,0,1,2], 0)` → `4`
- `search_rotated([4,5,6,7,0,1,2], 3)` → `-1`

**Hint:** Linear search ignores rotation — just scan. The challenge becomes the O(log n) version with modified binary search.

---

## Task 9 — Parallel Linear Search

Implement linear search using multiple threads / goroutines. Split the array into chunks; first match wins.

### Starter — Go
```go
func ParallelSearch(arr []int, target, workers int) int {
    // TODO  (use goroutines + context for cancellation)
    return -1
}
```

### Starter — Java
```java
public static int parallelSearch(int[] arr, int target, int workers)
    throws InterruptedException {
    // TODO  (use ExecutorService + AtomicInteger for found index)
    return -1;
}
```

### Starter — Python
```python
def parallel_search(arr, target, workers=4):
    # TODO  (use concurrent.futures.ThreadPoolExecutor)
    return -1
```

### Expected
- For arrays of millions of elements, runs ~`workers`× faster than serial.
- Returns a valid index (any matching one is acceptable; or specify "first").

**Hint:** Coordinate via a shared atomic / channel. Cancel other workers on first match.

---

## Task 10 — SIMD Linear Search

Implement linear search of a `[]int32` array using SIMD (8-wide, AVX2-equivalent). For Java/Python, simulate by unrolling 8x.

### Starter — Go
```go
import "github.com/klauspost/cpuid/v2"

func SIMDSearch(arr []int32, target int32) int {
    // TODO (use unrolled loop or call into assembly)
    return -1
}
```

### Starter — Java
```java
import jdk.incubator.vector.*;

public static int simdSearch(int[] arr, int target) {
    // TODO use VectorAPI from jdk.incubator.vector
    return -1;
}
```

### Starter — Python
```python
import numpy as np

def simd_search(arr_np, target):
    # TODO use np.where for vectorized comparison
    return -1
```

### Expected
- Same result as serial linear search, but ~4-8× faster on supported hardware.

**Hint:** In Python, `np.where(arr == target)[0]` returns all matching indices in vectorized C code.

---

## Task 11 — Sentinel Linear Search

Implement sentinel linear search: append target at the end, drop the bounds check inside the loop, restore the array at the end.

### Starter — Go
```go
func SentinelSearch(arr []int, target int) int {
    // TODO
    return -1
}
```

### Starter — Java
```java
public static int sentinelSearch(int[] arr, int target) {
    // TODO
    return -1;
}
```

### Starter — Python
```python
def sentinel_search(arr, target):
    # TODO
    return -1
```

### Expected
- Same result as basic linear search.
- Inner loop has only one comparison per iteration.

**Hint:** Save `arr[n-1]`, write `target` to `arr[n-1]`, scan with single condition, restore.

---

## Task 12 — Recursive Linear Search

Implement linear search recursively. Document the stack-overflow risk for large n.

### Starter — Go
```go
func RecursiveSearch(arr []int, target int, i int) int {
    // TODO
    return -1
}
```

### Starter — Java
```java
public static int recursiveSearch(int[] arr, int target, int i) {
    // TODO
    return -1;
}
```

### Starter — Python
```python
import sys

def recursive_search(arr, target, i=0):
    # TODO
    return -1
```

### Expected
- Works for small arrays.
- **Stack overflow** for n > ~1000 in Python (default recursion limit), n > ~10000 in Java/Go.

**Hint:** Base cases: `i >= len(arr)` → -1, `arr[i] == target` → i. Recurse on `i + 1`.

---

## Task 13 — Search Linked List

Search a singly-linked list for a target value. Return the **node** (or null) — not an index, since linked lists don't naturally support indexing.

### Starter — Go
```go
type Node struct {
    Value int
    Next  *Node
}

func SearchList(head *Node, target int) *Node {
    // TODO
    return nil
}
```

### Starter — Java
```java
class Node { int value; Node next; }

public static Node searchList(Node head, int target) {
    // TODO
    return null;
}
```

### Starter — Python
```python
class Node:
    def __init__(self, value, next=None):
        self.value = value
        self.next = next

def search_list(head, target):
    # TODO
    return None
```

### Expected
- Returns the first node whose value equals target.
- Returns null/None if not found.
- Cache-unfriendly: pointer chasing — slower than array linear search.

---

## Task 14 — Search with Custom Predicate

Find the first element matching a predicate function. Return the index, or -1.

### Starter — Go
```go
func FindFirstMatching(arr []int, pred func(int) bool) int {
    // TODO
    return -1
}
```

### Starter — Java
```java
import java.util.function.IntPredicate;

public static int findFirstMatching(int[] arr, IntPredicate pred) {
    // TODO
    return -1;
}
```

### Starter — Python
```python
from typing import Callable

def find_first_matching(arr, pred: Callable[[int], bool]) -> int:
    # TODO
    return -1
```

### Expected
- `find_first_matching([1, 4, 7, 10], lambda x: x > 5)` → `2`
- `find_first_matching([1, 2, 3], lambda x: x > 100)` → `-1`

---

## Task 15 — Benchmark vs Binary Search

Implement both linear and binary search; benchmark over array sizes 8, 32, 128, 1024, 10000, 1000000. Report the crossover point.

### Starter — Go
```go
import (
    "sort"
    "testing"
    "time"
)

func BenchmarkSearch() {
    sizes := []int{8, 32, 128, 1024, 10000, 1000000}
    for _, n := range sizes {
        arr := make([]int, n)
        for i := range arr { arr[i] = i }
        target := n - 1  // worst case for linear

        // Time linear search
        t0 := time.Now()
        for k := 0; k < 100000; k++ {
            _ = LinearSearch(arr, target)
        }
        linearTime := time.Since(t0)

        // Time binary search
        t1 := time.Now()
        for k := 0; k < 100000; k++ {
            _ = sort.SearchInts(arr, target)
        }
        binaryTime := time.Since(t1)

        // TODO: print results
    }
}
```

### Starter — Java
```java
public static void benchmark() {
    int[] sizes = {8, 32, 128, 1024, 10000, 1000000};
    for (int n : sizes) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        int target = n / 2;

        long t0 = System.nanoTime();
        for (int k = 0; k < 100000; k++) {
            linearSearch(arr, target);
        }
        long linearNs = System.nanoTime() - t0;

        long t1 = System.nanoTime();
        for (int k = 0; k < 100000; k++) {
            java.util.Arrays.binarySearch(arr, target);
        }
        long binaryNs = System.nanoTime() - t1;

        // TODO print
    }
}
```

### Starter — Python
```python
import bisect, time

def benchmark():
    for n in [8, 32, 128, 1024, 10000, 1000000]:
        arr = list(range(n))
        target = n // 2

        t0 = time.perf_counter()
        for _ in range(100_000):
            linear_search(arr, target)
        linear_time = time.perf_counter() - t0

        t1 = time.perf_counter()
        for _ in range(100_000):
            bisect.bisect_left(arr, target)
        binary_time = time.perf_counter() - t1

        # TODO print
```

### Expected
- For small n (8, 32), linear may be **faster** than binary due to cache effects and prediction.
- For large n (10000+), binary is dramatically faster.
- The crossover is typically n ≈ 32-64 on modern CPUs.

**Bonus:** Repeat with a `target` that is **absent** — this is linear's true worst case.

---

## Reflection Questions

After completing the tasks:

1. Which tasks felt mechanical, and which required more thought?
2. In Task 11 (sentinel), did you measure a real speedup? Why or why not?
3. In Task 15 (benchmark), where was your crossover? Did it match the expected ~32?
4. In Task 9 (parallel), how did you avoid the data race on the result variable?
5. For Task 12 (recursive), what was the largest n you could handle without stack overflow?

These questions are deliberately open. Linear search is "trivial" only at the algorithm level — the real complexity emerges in the systems context.
