# Array -- Middle Level

## Table of Contents

- [Prerequisites](#prerequisites)
- [Dynamic Array Internals](#dynamic-array-internals)
  - [Growth Strategy: Doubling](#growth-strategy-doubling)
  - [Amortized O(1) Append](#amortized-o1-append)
  - [Shrinking Strategy](#shrinking-strategy)
- [2D Arrays and Matrices](#2d-arrays-and-matrices)
  - [Row-Major vs Column-Major Order](#row-major-vs-column-major-order)
  - [Matrix Operations](#matrix-operations)
- [Array-Based Algorithms](#array-based-algorithms)
  - [Two Pointers](#two-pointers)
  - [Sliding Window](#sliding-window)
  - [Prefix Sum](#prefix-sum)
  - [Kadane's Algorithm](#kadanes-algorithm)
- [Array vs Linked List](#array-vs-linked-list)
- [When to Use Arrays vs Other Data Structures](#when-to-use-arrays-vs-other-data-structures)
- [Cache Locality Advantage](#cache-locality-advantage)
- [Summary](#summary)

---

## Prerequisites

You should be comfortable with:
- Array basics (indexing, CRUD operations, time complexities)
- Big-O notation
- Basic loop and recursion patterns

---

## Dynamic Array Internals

### Growth Strategy: Doubling

When a dynamic array runs out of capacity, it allocates a new array (typically 2x the current capacity), copies all existing elements to the new array, and frees the old one.

```
Initial:     capacity=4, length=4  -> [A][B][C][D]
Append E:    capacity=8, length=5  -> [A][B][C][D][E][ ][ ][ ]
             (allocated new array of size 8, copied 4 elements)
Append F:    capacity=8, length=6  -> [A][B][C][D][E][F][ ][ ]
             (no reallocation needed)
```

The growth factor varies by language:
- **Go**: starts at 2x, reduces to ~1.25x for large slices (since Go 1.18)
- **Java ArrayList**: 1.5x (new capacity = old capacity + old capacity / 2)
- **Python list**: roughly 1.125x (overallocates by ~12.5%)

### Amortized O(1) Append

Although individual resizes cost O(n) (copying n elements), the **average cost** of n appends is O(n), giving each append an **amortized O(1)** cost.

Intuition: after a resize to capacity 2k, you can do k more appends before the next resize. The cost of those k appends is k (one each) plus the resize cost of 2k (copying). Total: 3k operations for k appends = O(1) amortized per append.

**Go -- observing capacity growth:**

```go
package main

import "fmt"

func main() {
    var s []int
    prevCap := 0
    for i := 0; i < 20; i++ {
        s = append(s, i)
        if cap(s) != prevCap {
            fmt.Printf("len=%2d  cap=%2d  (grew from %d)\n", len(s), cap(s), prevCap)
            prevCap = cap(s)
        }
    }
}
// Output shows capacity roughly doubling:
// len= 1  cap= 1  (grew from 0)
// len= 2  cap= 2  (grew from 1)
// len= 3  cap= 4  (grew from 2)
// len= 5  cap= 8  (grew from 4)
// len= 9  cap=16  (grew from 8)
// len=17  cap=32  (grew from 16)
```

**Java -- observing ArrayList internal capacity:**

```java
import java.util.ArrayList;
import java.lang.reflect.Field;

public class CapacityGrowth {
    public static void main(String[] args) throws Exception {
        ArrayList<Integer> list = new ArrayList<>();
        Field field = ArrayList.class.getDeclaredField("elementData");
        field.setAccessible(true);

        int prevCap = 0;
        for (int i = 0; i < 20; i++) {
            list.add(i);
            int cap = ((Object[]) field.get(list)).length;
            if (cap != prevCap) {
                System.out.printf("size=%2d  capacity=%2d  (grew from %d)%n",
                    list.size(), cap, prevCap);
                prevCap = cap;
            }
        }
    }
    // Capacity grows by 1.5x: 10 -> 15 -> 22 -> 33 -> ...
}
```

**Python -- using sys.getsizeof to observe growth:**

```python
import sys

arr = []
prev_size = sys.getsizeof(arr)
for i in range(30):
    arr.append(i)
    new_size = sys.getsizeof(arr)
    if new_size != prev_size:
        print(f"len={len(arr):2d}  bytes={new_size}  (grew from {prev_size})")
        prev_size = new_size
```

### Shrinking Strategy

Most implementations do **not** automatically shrink. Java's `ArrayList.trimToSize()` and Go's re-slicing with `copy` can reclaim memory manually. Python lists may shrink when enough elements are removed.

---

## 2D Arrays and Matrices

A 2D array is an array of arrays, used to represent grids, matrices, and tables.

### Row-Major vs Column-Major Order

In **row-major** order (C, Go, Java, Python), elements of each row are stored contiguously:

```
Matrix:       [[1, 2, 3],
               [4, 5, 6],
               [7, 8, 9]]

Memory (row-major): 1, 2, 3, 4, 5, 6, 7, 8, 9
```

In **column-major** order (Fortran, MATLAB, Julia), elements of each column are stored contiguously:

```
Memory (column-major): 1, 4, 7, 2, 5, 8, 3, 6, 9
```

This matters for performance: iterating in the wrong order causes cache misses.

### Matrix Operations

**Go:**

```go
package main

import "fmt"

func main() {
    // Create 3x3 matrix
    matrix := [3][3]int{
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9},
    }

    // Iterate row-by-row (cache-friendly)
    for i := 0; i < 3; i++ {
        for j := 0; j < 3; j++ {
            fmt.Printf("%d ", matrix[i][j])
        }
        fmt.Println()
    }

    // Transpose
    var transposed [3][3]int
    for i := 0; i < 3; i++ {
        for j := 0; j < 3; j++ {
            transposed[j][i] = matrix[i][j]
        }
    }
    fmt.Println("Transposed:", transposed)
}
```

**Java:**

```java
public class Matrix {
    public static void main(String[] args) {
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        // Print matrix
        for (int[] row : matrix) {
            for (int val : row) {
                System.out.printf("%d ", val);
            }
            System.out.println();
        }

        // Transpose
        int rows = matrix.length, cols = matrix[0].length;
        int[][] transposed = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
    }
}
```

**Python:**

```python
matrix = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9],
]

# Print matrix
for row in matrix:
    print(" ".join(str(x) for x in row))

# Transpose using zip
transposed = [list(row) for row in zip(*matrix)]
print("Transposed:", transposed)

# Transpose using list comprehension
transposed2 = [[matrix[j][i] for j in range(len(matrix))] for i in range(len(matrix[0]))]
```

---

## Array-Based Algorithms

### Two Pointers

Use two indices moving toward each other (or in the same direction) to solve problems efficiently.

**Problem: Check if a sorted array has a pair that sums to a target.**

**Go:**

```go
func twoSumSorted(arr []int, target int) (int, int, bool) {
    left, right := 0, len(arr)-1
    for left < right {
        sum := arr[left] + arr[right]
        if sum == target {
            return left, right, true
        } else if sum < target {
            left++
        } else {
            right--
        }
    }
    return -1, -1, false
}
```

**Java:**

```java
public static int[] twoSumSorted(int[] arr, int target) {
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

**Python:**

```python
def two_sum_sorted(arr, target):
    left, right = 0, len(arr) - 1
    while left < right:
        s = arr[left] + arr[right]
        if s == target:
            return (left, right)
        elif s < target:
            left += 1
        else:
            right -= 1
    return (-1, -1)
```

Time: O(n), Space: O(1).

### Sliding Window

Maintain a window [left, right] that slides across the array to compute rolling aggregates.

**Problem: Maximum sum of a subarray of size k.**

**Go:**

```go
func maxSumSubarray(arr []int, k int) int {
    n := len(arr)
    if n < k {
        return 0
    }
    // Compute sum of first window
    windowSum := 0
    for i := 0; i < k; i++ {
        windowSum += arr[i]
    }
    maxSum := windowSum

    // Slide the window
    for i := k; i < n; i++ {
        windowSum += arr[i] - arr[i-k]
        if windowSum > maxSum {
            maxSum = windowSum
        }
    }
    return maxSum
}
```

**Java:**

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

**Python:**

```python
def max_sum_subarray(arr, k):
    window_sum = sum(arr[:k])
    max_sum = window_sum
    for i in range(k, len(arr)):
        window_sum += arr[i] - arr[i - k]
        max_sum = max(max_sum, window_sum)
    return max_sum
```

Time: O(n), Space: O(1).

### Prefix Sum

Precompute cumulative sums so that any subarray sum can be answered in O(1).

**Go:**

```go
func buildPrefixSum(arr []int) []int {
    prefix := make([]int, len(arr)+1)
    for i, v := range arr {
        prefix[i+1] = prefix[i] + v
    }
    return prefix
}

// Sum of arr[l..r] (inclusive)
func rangeSum(prefix []int, l, r int) int {
    return prefix[r+1] - prefix[l]
}
```

**Java:**

```java
public static int[] buildPrefixSum(int[] arr) {
    int[] prefix = new int[arr.length + 1];
    for (int i = 0; i < arr.length; i++) {
        prefix[i + 1] = prefix[i] + arr[i];
    }
    return prefix;
}

public static int rangeSum(int[] prefix, int l, int r) {
    return prefix[r + 1] - prefix[l];
}
```

**Python:**

```python
def build_prefix_sum(arr):
    prefix = [0] * (len(arr) + 1)
    for i in range(len(arr)):
        prefix[i + 1] = prefix[i] + arr[i]
    return prefix

def range_sum(prefix, l, r):
    return prefix[r + 1] - prefix[l]

# Using itertools
from itertools import accumulate
prefix = [0] + list(accumulate(arr))
```

### Kadane's Algorithm

Find the maximum sum contiguous subarray in O(n) time.

**Go:**

```go
func kadane(arr []int) int {
    maxSoFar := arr[0]
    maxEndingHere := arr[0]
    for i := 1; i < len(arr); i++ {
        if maxEndingHere+arr[i] > arr[i] {
            maxEndingHere = maxEndingHere + arr[i]
        } else {
            maxEndingHere = arr[i]
        }
        if maxEndingHere > maxSoFar {
            maxSoFar = maxEndingHere
        }
    }
    return maxSoFar
}
```

**Java:**

```java
public static int kadane(int[] arr) {
    int maxSoFar = arr[0];
    int maxEndingHere = arr[0];
    for (int i = 1; i < arr.length; i++) {
        maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
    }
    return maxSoFar;
}
```

**Python:**

```python
def kadane(arr):
    max_so_far = arr[0]
    max_ending_here = arr[0]
    for i in range(1, len(arr)):
        max_ending_here = max(arr[i], max_ending_here + arr[i])
        max_so_far = max(max_so_far, max_ending_here)
    return max_so_far

# Example
print(kadane([-2, 1, -3, 4, -1, 2, 1, -5, 4]))  # 6 (subarray [4,-1,2,1])
```

---

## Array vs Linked List

| Criterion              | Array                        | Linked List                  |
| ---------------------- | ---------------------------- | ---------------------------- |
| Access by index        | O(1)                         | O(n)                         |
| Search                 | O(n)                         | O(n)                         |
| Insert at beginning    | O(n)                         | O(1)                         |
| Insert at end          | O(1) amortized               | O(1) with tail pointer       |
| Insert at middle       | O(n)                         | O(1) after finding position  |
| Delete at beginning    | O(n)                         | O(1)                         |
| Delete at end          | O(1)                         | O(n) or O(1) with doubly     |
| Memory layout          | Contiguous                   | Scattered (nodes + pointers) |
| Cache performance      | Excellent                    | Poor                         |
| Memory overhead        | Low (data only)              | High (data + pointer(s))     |
| Size flexibility       | Fixed (static) or amortized  | Naturally dynamic            |

**Use arrays when:** you need fast random access, iterate mostly sequentially, or want cache-friendly performance. This is the common case.

**Use linked lists when:** you need frequent insertions/deletions at arbitrary positions (and already have a reference to the node), or memory fragmentation prevents large contiguous allocations.

---

## When to Use Arrays vs Other Data Structures

| Need                                      | Best Choice          | Why                                |
| ----------------------------------------- | -------------------- | ---------------------------------- |
| Fast access by index                      | Array                | O(1) random access                 |
| Fast search by value                      | Hash set/map         | O(1) average lookup                |
| Fast insert/delete at both ends           | Deque                | O(1) at both ends                  |
| Fast insert/delete in sorted order        | Balanced BST / Heap  | O(log n) operations                |
| FIFO ordering                             | Queue (array-based)  | Circular buffer is array-backed    |
| LIFO ordering                             | Stack (array-based)  | Array is natural for stack         |
| Key-value lookup                          | Hash map             | O(1) average                       |
| Sorted data with range queries            | Sorted array + bisect| Binary search is O(log n)          |

---

## Cache Locality Advantage

Modern CPUs do not read individual bytes from RAM. They read **cache lines** (typically 64 bytes). When you access `arr[0]`, the CPU loads bytes for `arr[0]` through approximately `arr[15]` (for 4-byte integers) into L1 cache. Subsequent accesses to `arr[1]`, `arr[2]`, etc. are served from the cache -- up to 100x faster than a RAM fetch.

This is why arrays significantly outperform linked lists in practice, even when the theoretical Big-O is the same. Linked list nodes are scattered in memory, causing frequent **cache misses**.

**Benchmark intuition:**
- Iterating an array of 1M integers: ~0.5ms (sequential, cache-friendly)
- Iterating a linked list of 1M nodes: ~5-10ms (random memory access, cache-hostile)

---

## Summary

| Topic                  | Key Insight                                                |
| ---------------------- | ---------------------------------------------------------- |
| Dynamic array growth   | Doubling (or 1.5x) strategy gives amortized O(1) append   |
| 2D arrays              | Row-major layout; iterate row-by-row for performance       |
| Two pointers           | O(n) solution for sorted array pair problems               |
| Sliding window         | O(n) for fixed/variable-size subarray problems             |
| Prefix sum             | O(n) precompute, O(1) per range query                     |
| Kadane's algorithm     | O(n) maximum subarray sum                                  |
| Array vs linked list   | Array wins in practice due to cache locality               |
| Cache locality         | Contiguous memory = CPU cache friendly = fast iteration    |
