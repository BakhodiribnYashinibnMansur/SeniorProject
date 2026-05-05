# Polynomial Time O(n^2), O(n^3) -- Middle Level

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [When Is O(n^2) Acceptable?](#when-is-on2-acceptable)
3. [Comparison of Quadratic Sorting Algorithms](#comparison-of-quadratic-sorting-algorithms)
4. [Floyd-Warshall: A Classic O(V^3) Algorithm](#floyd-warshall-a-classic-ov3-algorithm)
5. [Optimizing O(n^2) to O(n log n)](#optimizing-on2-to-on-log-n)
6. [Strassen Matrix Multiplication](#strassen-matrix-multiplication)
7. [Amortized Analysis of Polynomial Algorithms](#amortized-analysis-of-polynomial-algorithms)
8. [Space Complexity Considerations](#space-complexity-considerations)
9. [Practical Benchmarking](#practical-benchmarking)
10. [Key Takeaways](#key-takeaways)

---

## Prerequisites

Before reading this document, you should be comfortable with:
- Basic Big-O notation (O(1), O(n), O(n log n), O(n^2))
- Bubble sort, selection sort, and insertion sort implementations
- Nested loop analysis
- Basic divide-and-conquer concepts

---

## When Is O(n^2) Acceptable?

Not every O(n^2) algorithm needs to be optimized. Here are guidelines for when
quadratic time is perfectly fine:

### Input Size Thresholds

| Input Size n | O(n^2) Operations | Time at 10^8 ops/sec | Verdict        |
|-------------|-------------------|----------------------|----------------|
| 100         | 10,000            | 0.0001s              | Always fine     |
| 1,000       | 1,000,000         | 0.01s                | Fine            |
| 5,000       | 25,000,000        | 0.25s                | Usually fine    |
| 10,000      | 100,000,000       | 1s                   | Borderline      |
| 50,000      | 2,500,000,000     | 25s                  | Too slow        |
| 100,000     | 10,000,000,000    | 100s                 | Unacceptable    |

### When to Keep O(n^2)

1. **Small input size guaranteed** -- If the problem constraints ensure n < 5000,
   an O(n^2) solution is straightforward and correct. Do not over-engineer.

2. **Simpler code, fewer bugs** -- An O(n^2) solution that is easy to understand,
   test, and maintain can be better than a complex O(n log n) one in non-critical
   paths.

3. **Constant factor matters** -- An O(n log n) algorithm with a large constant
   factor can be slower than O(n^2) for small inputs. For example, merge sort
   is slower than insertion sort for arrays with fewer than ~20 elements.

4. **One-time computation** -- If the computation runs once during initialization
   and n is moderate, the quadratic cost is paid once.

5. **Inner loop is very cheap** -- If each operation in the inner loop is a simple
   comparison or addition (not I/O or memory allocation), the constant factor
   is tiny.

### When to Optimize Away O(n^2)

1. **Hot path in production** -- Code that runs on every request or user action.
2. **n can grow unbounded** -- User-controlled input sizes.
3. **Latency-sensitive** -- Real-time systems, interactive applications.
4. **Competitive programming** -- Contest constraints often demand O(n log n).

---

## Comparison of Quadratic Sorting Algorithms

All three classic sorts are O(n^2), but they differ in important ways.

### Side-by-Side Comparison

| Property            | Bubble Sort   | Selection Sort | Insertion Sort |
|--------------------|---------------|----------------|----------------|
| Best case          | O(n)          | O(n^2)         | O(n)           |
| Average case       | O(n^2)        | O(n^2)         | O(n^2)         |
| Worst case         | O(n^2)        | O(n^2)         | O(n^2)         |
| Space              | O(1)          | O(1)           | O(1)           |
| Stable             | Yes           | No             | Yes            |
| Adaptive           | Yes           | No             | Yes            |
| Comparisons (avg)  | n^2/2         | n^2/2          | n^2/4          |
| Swaps (avg)        | n^2/4         | n              | n^2/4          |
| Online             | No            | No             | Yes            |

### What These Properties Mean

**Stable:** A stable sort preserves the relative order of equal elements. This
matters when sorting objects by multiple keys.

**Adaptive:** An adaptive sort runs faster when the input is partially sorted.
Bubble sort and insertion sort detect sorted runs and skip work.

**Online:** An online algorithm can process input as it arrives. Insertion sort
can sort a stream because it maintains a sorted prefix.

### Selection Sort's Hidden Advantage

Selection sort makes the fewest swaps: exactly n-1 swaps regardless of input.
When swapping is expensive (e.g., large objects without move semantics), selection
sort can outperform the others.

```go
// Go -- Selection sort: always exactly n-1 swaps
func selectionSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        minIdx := i
        for j := i + 1; j < n; j++ {
            if arr[j] < arr[minIdx] {
                minIdx = j
            }
        }
        if minIdx != i {
            arr[i], arr[minIdx] = arr[minIdx], arr[i]
        }
    }
}
```

```java
// Java -- Selection sort: always exactly n-1 swaps
void selectionSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        int minIdx = i;
        for (int j = i + 1; j < n; j++) {
            if (arr[j] < arr[minIdx]) {
                minIdx = j;
            }
        }
        if (minIdx != i) {
            int temp = arr[i];
            arr[i] = arr[minIdx];
            arr[minIdx] = temp;
        }
    }
}
```

```python
# Python -- Selection sort: always exactly n-1 swaps
def selection_sort(arr):
    n = len(arr)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            if arr[j] < arr[min_idx]:
                min_idx = j
        if min_idx != i:
            arr[i], arr[min_idx] = arr[min_idx], arr[i]
```

### Insertion Sort for Nearly Sorted Data

When data is "almost sorted" (each element is at most k positions from its correct
place), insertion sort runs in O(nk) time. For small k, this is essentially O(n).

```go
// Go -- Insertion sort: O(nk) for k-nearly-sorted data
func insertionSort(arr []int) {
    for i := 1; i < len(arr); i++ {
        key := arr[i]
        j := i - 1
        // Inner loop runs at most k times per element
        for j >= 0 && arr[j] > key {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = key
    }
}
```

```java
// Java
void insertionSort(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```

```python
# Python
def insertion_sort(arr):
    for i in range(1, len(arr)):
        key = arr[i]
        j = i - 1
        while j >= 0 and arr[j] > key:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = key
```

---

## Floyd-Warshall: A Classic O(V^3) Algorithm

Floyd-Warshall finds shortest paths between **all pairs** of vertices in a
weighted graph. It runs in O(V^3) time and O(V^2) space.

### Algorithm

For every possible intermediate vertex k, for every pair (i, j), check if going
through k gives a shorter path:

```
dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])
```

### Implementation

```go
// Go -- Floyd-Warshall O(V^3)
const INF = math.MaxInt32 / 2

func floydWarshall(graph [][]int) [][]int {
    n := len(graph)
    dist := make([][]int, n)
    for i := 0; i < n; i++ {
        dist[i] = make([]int, n)
        copy(dist[i], graph[i])
    }

    for k := 0; k < n; k++ {
        for i := 0; i < n; i++ {
            for j := 0; j < n; j++ {
                if dist[i][k]+dist[k][j] < dist[i][j] {
                    dist[i][j] = dist[i][k] + dist[k][j]
                }
            }
        }
    }
    return dist
}
```

```java
// Java -- Floyd-Warshall O(V^3)
int[][] floydWarshall(int[][] graph) {
    int n = graph.length;
    int[][] dist = new int[n][n];
    int INF = Integer.MAX_VALUE / 2;

    for (int i = 0; i < n; i++) {
        System.arraycopy(graph[i], 0, dist[i], 0, n);
    }

    for (int k = 0; k < n; k++) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][k] + dist[k][j] < dist[i][j]) {
                    dist[i][j] = dist[i][k] + dist[k][j];
                }
            }
        }
    }
    return dist;
}
```

```python
# Python -- Floyd-Warshall O(V^3)
def floyd_warshall(graph):
    n = len(graph)
    INF = float('inf')
    dist = [row[:] for row in graph]

    for k in range(n):
        for i in range(n):
            for j in range(n):
                if dist[i][k] + dist[k][j] < dist[i][j]:
                    dist[i][j] = dist[i][k] + dist[k][j]

    return dist
```

### When to Use Floyd-Warshall

- **Dense graphs** where E is close to V^2 (otherwise Dijkstra from each vertex
  is better: O(V * E log V))
- **Negative edge weights** (but no negative cycles)
- **Small V** (V <= 500 is comfortable; V <= 1000 is borderline)
- When you need **all pairs** shortest paths, not just single source

---

## Optimizing O(n^2) to O(n log n)

The most important skill at this level is recognizing when an O(n^2) solution
can be improved to O(n log n) or O(n).

### Pattern 1: Sorting + Two Pointers

**Problem:** Find if any two elements in an array sum to a target.

O(n^2) brute force checks all pairs. But if we sort first and use two pointers:

```go
// Go -- Two Sum: O(n^2) -> O(n log n)
import "sort"

func twoSumOptimized(arr []int, target int) bool {
    sort.Ints(arr) // O(n log n)
    left, right := 0, len(arr)-1
    for left < right { // O(n)
        sum := arr[left] + arr[right]
        if sum == target {
            return true
        } else if sum < target {
            left++
        } else {
            right--
        }
    }
    return false
}
```

```java
// Java -- Two Sum: O(n^2) -> O(n log n)
boolean twoSumOptimized(int[] arr, int target) {
    Arrays.sort(arr); // O(n log n)
    int left = 0, right = arr.length - 1;
    while (left < right) { // O(n)
        int sum = arr[left] + arr[right];
        if (sum == target) return true;
        else if (sum < target) left++;
        else right--;
    }
    return false;
}
```

```python
# Python -- Two Sum: O(n^2) -> O(n log n)
def two_sum_optimized(arr, target):
    arr.sort()  # O(n log n)
    left, right = 0, len(arr) - 1
    while left < right:  # O(n)
        s = arr[left] + arr[right]
        if s == target:
            return True
        elif s < target:
            left += 1
        else:
            right -= 1
    return False
```

### Pattern 2: Hash Map Lookup

Replace the inner loop with a hash map lookup (O(1) average):

```go
// Go -- Two Sum: O(n^2) -> O(n) with hash map
func twoSumHash(arr []int, target int) bool {
    seen := make(map[int]bool)
    for _, num := range arr {
        complement := target - num
        if seen[complement] {
            return true
        }
        seen[num] = true
    }
    return false
}
```

```java
// Java
boolean twoSumHash(int[] arr, int target) {
    Set<Integer> seen = new HashSet<>();
    for (int num : arr) {
        if (seen.contains(target - num)) return true;
        seen.add(num);
    }
    return false;
}
```

```python
# Python
def two_sum_hash(arr, target):
    seen = set()
    for num in arr:
        if target - num in seen:
            return True
        seen.add(num)
    return False
```

### Pattern 3: Divide and Conquer

Replace O(n^2) merge with O(n log n) divide and conquer. Classic example:
counting inversions.

```go
// Go -- Count inversions: O(n^2) -> O(n log n)
func countInversions(arr []int) int {
    if len(arr) <= 1 {
        return 0
    }
    mid := len(arr) / 2
    left := make([]int, mid)
    right := make([]int, len(arr)-mid)
    copy(left, arr[:mid])
    copy(right, arr[mid:])

    count := countInversions(left) + countInversions(right)
    count += mergeCount(arr, left, right)
    return count
}

func mergeCount(arr, left, right []int) int {
    i, j, k, count := 0, 0, 0, 0
    for i < len(left) && j < len(right) {
        if left[i] <= right[j] {
            arr[k] = left[i]
            i++
        } else {
            arr[k] = right[j]
            count += len(left) - i // All remaining in left are inversions
            j++
        }
        k++
    }
    for i < len(left) {
        arr[k] = left[i]
        i++
        k++
    }
    for j < len(right) {
        arr[k] = right[j]
        j++
        k++
    }
    return count
}
```

```java
// Java -- Count inversions: O(n^2) -> O(n log n)
int countInversions(int[] arr, int left, int right) {
    if (left >= right) return 0;
    int mid = left + (right - left) / 2;
    int count = countInversions(arr, left, mid)
              + countInversions(arr, mid + 1, right);
    count += mergeCount(arr, left, mid, right);
    return count;
}

int mergeCount(int[] arr, int left, int mid, int right) {
    int[] temp = new int[right - left + 1];
    int i = left, j = mid + 1, k = 0, count = 0;
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) {
            temp[k++] = arr[i++];
        } else {
            count += mid - i + 1;
            temp[k++] = arr[j++];
        }
    }
    while (i <= mid) temp[k++] = arr[i++];
    while (j <= right) temp[k++] = arr[j++];
    System.arraycopy(temp, 0, arr, left, temp.length);
    return count;
}
```

```python
# Python -- Count inversions: O(n^2) -> O(n log n)
def count_inversions(arr):
    if len(arr) <= 1:
        return arr, 0
    mid = len(arr) // 2
    left, left_inv = count_inversions(arr[:mid])
    right, right_inv = count_inversions(arr[mid:])
    merged, split_inv = merge_count(left, right)
    return merged, left_inv + right_inv + split_inv

def merge_count(left, right):
    result = []
    i = j = count = 0
    while i < len(left) and j < len(right):
        if left[i] <= right[j]:
            result.append(left[i])
            i += 1
        else:
            result.append(right[j])
            count += len(left) - i
            j += 1
    result.extend(left[i:])
    result.extend(right[j:])
    return result, count
```

### Pattern 4: Precomputation with Prefix Sums

Replace O(n^2) range queries with O(1) using prefix sums:

```go
// Go -- Subarray sum queries: O(n^2) per query -> O(1) per query
func buildPrefixSum(arr []int) []int {
    prefix := make([]int, len(arr)+1)
    for i, v := range arr {
        prefix[i+1] = prefix[i] + v
    }
    return prefix
}

func rangeSum(prefix []int, left, right int) int {
    return prefix[right+1] - prefix[left]
}
```

```java
// Java
int[] buildPrefixSum(int[] arr) {
    int[] prefix = new int[arr.length + 1];
    for (int i = 0; i < arr.length; i++) {
        prefix[i + 1] = prefix[i] + arr[i];
    }
    return prefix;
}

int rangeSum(int[] prefix, int left, int right) {
    return prefix[right + 1] - prefix[left];
}
```

```python
# Python
def build_prefix_sum(arr):
    prefix = [0] * (len(arr) + 1)
    for i, v in enumerate(arr):
        prefix[i + 1] = prefix[i] + v
    return prefix

def range_sum(prefix, left, right):
    return prefix[right + 1] - prefix[left]
```

---

## Strassen Matrix Multiplication

The naive matrix multiplication is O(n^3). Strassen's algorithm (1969) was the
first to break this barrier, achieving O(n^2.807).

### Key Idea

Instead of 8 recursive multiplications of n/2 x n/2 matrices (which gives
T(n) = 8T(n/2) + O(n^2) = O(n^3)), Strassen uses clever algebraic identities
to need only **7 multiplications** at the cost of more additions:

T(n) = 7T(n/2) + O(n^2) = O(n^log2(7)) = O(n^2.807)

### The 7 Products

For 2x2 block matrices A and B:

```
M1 = (A11 + A22)(B11 + B22)
M2 = (A21 + A22) * B11
M3 = A11 * (B12 - B22)
M4 = A22 * (B21 - B11)
M5 = (A11 + A12) * B22
M6 = (A21 - A11)(B11 + B12)
M7 = (A12 - A22)(B21 + B22)
```

Then:
```
C11 = M1 + M4 - M5 + M7
C12 = M3 + M5
C21 = M2 + M4
C22 = M1 - M2 + M3 + M6
```

### Practical Considerations

- Strassen is faster only for **large matrices** (typically n > 64-128)
- The constant factor is larger than naive multiplication
- Numerical stability can be worse for floating-point arithmetic
- In practice, libraries like BLAS use a hybrid: Strassen for large blocks,
  naive for small blocks

---

## Amortized Analysis of Polynomial Algorithms

Some algorithms appear O(n^2) but are actually better in amortized terms.

### Example: Building a Sorted Array via Repeated Insertion

Inserting n elements one by one into a sorted array:
- Each insertion is O(n) in the worst case (shifting elements)
- Total: O(n^2)

But with a balanced BST or skip list, each insertion is O(log n):
- Total: O(n log n)

### Example: Dynamic Array Resizing

Appending n elements to a dynamic array with doubling:
- Some appends trigger O(n) copy
- But amortized cost per append is O(1)
- Total: O(n), not O(n^2)

Understanding amortization helps you distinguish truly quadratic algorithms from
those that merely have occasional expensive operations.

---

## Space Complexity Considerations

| Algorithm           | Time     | Space  | Notes                         |
|--------------------|---------:|-------:|-------------------------------|
| Bubble Sort        | O(n^2)   | O(1)   | In-place                      |
| Selection Sort     | O(n^2)   | O(1)   | In-place                      |
| Insertion Sort     | O(n^2)   | O(1)   | In-place                      |
| Floyd-Warshall     | O(V^3)   | O(V^2) | Distance matrix               |
| Naive MatMul       | O(n^3)   | O(n^2) | Result matrix                 |
| Count Inversions   | O(n lg n)| O(n)   | Merge sort auxiliary           |

Sometimes an O(n^2) in-place algorithm is preferable to an O(n log n) algorithm
that requires O(n) extra space, especially in memory-constrained environments.

---

## Practical Benchmarking

Theory says O(n^2) should be slower than O(n log n) for large n. Let us verify.

```go
// Go -- Benchmark: Insertion Sort vs sort.Ints
package main

import (
    "fmt"
    "math/rand"
    "sort"
    "time"
)

func insertionSort(arr []int) {
    for i := 1; i < len(arr); i++ {
        key := arr[i]
        j := i - 1
        for j >= 0 && arr[j] > key {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = key
    }
}

func main() {
    for _, n := range []int{100, 1000, 5000, 10000, 50000} {
        arr1 := make([]int, n)
        arr2 := make([]int, n)
        for i := range arr1 {
            arr1[i] = rand.Intn(n * 10)
        }
        copy(arr2, arr1)

        start := time.Now()
        insertionSort(arr1)
        t1 := time.Since(start)

        start = time.Now()
        sort.Ints(arr2)
        t2 := time.Since(start)

        fmt.Printf("n=%6d  InsertionSort: %12v  sort.Ints: %12v  ratio: %.1fx\n",
            n, t1, t2, float64(t1)/float64(t2))
    }
}
```

```java
// Java -- Benchmark: Insertion Sort vs Arrays.sort
import java.util.Arrays;
import java.util.Random;

public class SortBenchmark {
    static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        Random rng = new Random(42);
        int[] sizes = {100, 1000, 5000, 10000, 50000};
        for (int n : sizes) {
            int[] arr1 = new int[n];
            for (int i = 0; i < n; i++) arr1[i] = rng.nextInt(n * 10);
            int[] arr2 = arr1.clone();

            long start = System.nanoTime();
            insertionSort(arr1);
            long t1 = System.nanoTime() - start;

            start = System.nanoTime();
            Arrays.sort(arr2);
            long t2 = System.nanoTime() - start;

            System.out.printf("n=%6d  InsertionSort: %10d ns  Arrays.sort: %10d ns  ratio: %.1fx%n",
                n, t1, t2, (double) t1 / t2);
        }
    }
}
```

```python
# Python -- Benchmark: Insertion Sort vs sorted()
import random
import time

def insertion_sort(arr):
    for i in range(1, len(arr)):
        key = arr[i]
        j = i - 1
        while j >= 0 and arr[j] > key:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = key

for n in [100, 1000, 5000, 10000, 50000]:
    arr1 = [random.randint(0, n * 10) for _ in range(n)]
    arr2 = arr1[:]

    start = time.perf_counter()
    insertion_sort(arr1)
    t1 = time.perf_counter() - start

    start = time.perf_counter()
    sorted(arr2)
    t2 = time.perf_counter() - start

    ratio = t1 / t2 if t2 > 0 else float('inf')
    print(f"n={n:6d}  InsertionSort: {t1:10.6f}s  sorted(): {t2:10.6f}s  ratio: {ratio:.1f}x")
```

Expected output pattern:
```
n=   100  InsertionSort:    0.000ms  sorted():    0.000ms  ratio: 2.0x
n=  1000  InsertionSort:    3.000ms  sorted():    0.100ms  ratio: 30.0x
n=  5000  InsertionSort:   70.000ms  sorted():    0.600ms  ratio: 117.0x
n= 10000  InsertionSort:  280.000ms  sorted():    1.400ms  ratio: 200.0x
n= 50000  InsertionSort: 7000.000ms  sorted():    8.000ms  ratio: 875.0x
```

The gap grows dramatically because O(n^2) / O(n log n) = O(n / log n), which
increases as n grows.

---

## Key Takeaways

1. **O(n^2) is acceptable for n < ~5000** in most contexts. Do not prematurely
   optimize if the input is guaranteed small.

2. **Insertion sort wins for small/nearly-sorted data.** Many production sorting
   libraries (Java's TimSort, Go's pdqsort) use insertion sort for small partitions.

3. **Selection sort minimizes swaps.** Use it when swap cost dominates comparison
   cost.

4. **Floyd-Warshall is the simplest all-pairs shortest path algorithm** but is
   limited to V <= ~500 in practice due to O(V^3).

5. **Four main optimization patterns** to break O(n^2):
   - Sort + two pointers
   - Hash map for O(1) lookup
   - Divide and conquer
   - Prefix sum precomputation

6. **Strassen's O(n^2.807) matrix multiplication** shows that the naive O(n^3)
   is not the best possible, but practical gains require large matrices.

7. **Always benchmark** -- theoretical complexity predicts trends, but constants
   and cache behavior determine real performance.

---

> **Next:** See [senior.md](senior.md) for polynomial time in real production systems.
