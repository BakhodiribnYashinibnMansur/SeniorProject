# How to Calculate Complexity? -- Middle Level

## Prerequisites

- [Junior Level](junior.md) -- counting operations, loop analysis, Big-O basics

## Table of Contents

1. [Introduction](#introduction)
2. [Recurrence Relations](#recurrence-relations)
   - [What is a Recurrence?](#what-is-a-recurrence)
   - [Writing Recurrences from Code](#writing-recurrences-from-code)
   - [Solving by Expansion (Substitution)](#solving-by-expansion)
3. [The Master Theorem](#the-master-theorem)
   - [The Formula](#the-formula)
   - [The Three Cases](#the-three-cases)
   - [Worked Examples](#worked-examples)
4. [Analyzing Recursive Algorithms](#analyzing-recursive-algorithms)
   - [Merge Sort](#merge-sort)
   - [Quick Sort](#quick-sort)
   - [Tree Traversal](#tree-traversal)
5. [Amortized Analysis Introduction](#amortized-analysis-introduction)
   - [Aggregate Method](#aggregate-method)
   - [Dynamic Array Example](#dynamic-array-example)
6. [Multiple Variables -- O(nm)](#multiple-variables--onm)
7. [Comparison of Analysis Approaches](#comparison-of-analysis-approaches)
8. [Key Takeaways](#key-takeaways)

---

## Introduction

At the junior level, you learned to count operations in iterative code. But many algorithms are **recursive**, and loops alone cannot describe their behavior. To analyze recursive algorithms, we need **recurrence relations** -- mathematical equations that describe the running time in terms of smaller inputs.

This guide covers the tools you need: recurrence relations, the Master Theorem, amortized analysis, and multi-variable complexity.

---

## Recurrence Relations

### What is a Recurrence?

A recurrence relation defines a function in terms of itself on smaller inputs. For algorithm analysis, it takes the form:

```
T(n) = [cost of recursive calls] + [cost of non-recursive work]
T(base case) = O(1)
```

For example, if an algorithm splits the problem in half and does O(n) work at each level:

```
T(n) = 2T(n/2) + O(n)
T(1) = O(1)
```

### Writing Recurrences from Code

The key is to identify:
1. **How many** recursive calls are made
2. **What size** each subproblem is
3. **How much non-recursive work** is done at each level

#### Go

```go
// Recurrence: T(n) = 2T(n/2) + O(n)
func mergeSort(arr []int) []int {
    if len(arr) <= 1 {           // Base case: T(1) = O(1)
        return arr
    }
    mid := len(arr) / 2
    left := mergeSort(arr[:mid])   // T(n/2)
    right := mergeSort(arr[mid:])  // T(n/2)
    return merge(left, right)      // O(n) work to merge
}

func merge(a, b []int) []int {
    result := make([]int, 0, len(a)+len(b))
    i, j := 0, 0
    for i < len(a) && j < len(b) {
        if a[i] <= b[j] {
            result = append(result, a[i])
            i++
        } else {
            result = append(result, b[j])
            j++
        }
    }
    result = append(result, a[i:]...)
    result = append(result, b[j:]...)
    return result
}
```

#### Java

```java
// Recurrence: T(n) = 2T(n/2) + O(n)
public static int[] mergeSort(int[] arr) {
    if (arr.length <= 1) {              // Base case: T(1) = O(1)
        return arr;
    }
    int mid = arr.length / 2;
    int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));    // T(n/2)
    int[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length)); // T(n/2)
    return merge(left, right);          // O(n) work to merge
}
```

#### Python

```python
# Recurrence: T(n) = 2T(n/2) + O(n)
def merge_sort(arr):
    if len(arr) <= 1:             # Base case: T(1) = O(1)
        return arr
    mid = len(arr) // 2
    left = merge_sort(arr[:mid])   # T(n/2)
    right = merge_sort(arr[mid:])  # T(n/2)
    return merge(left, right)      # O(n) work to merge
```

### Solving by Expansion

The expansion (or "unrolling") method substitutes the recurrence into itself repeatedly.

**Example**: T(n) = 2T(n/2) + n, T(1) = 1

```
T(n) = 2T(n/2) + n
     = 2[2T(n/4) + n/2] + n
     = 4T(n/4) + n + n
     = 4[2T(n/8) + n/4] + 2n
     = 8T(n/8) + n + n + n
     = 8T(n/8) + 3n
     ...
     = 2^k * T(n/2^k) + k*n
```

At the base case, n/2^k = 1, so k = log2(n):

```
T(n) = 2^(log n) * T(1) + n * log(n)
     = n * 1 + n log n
     = n + n log n
     = O(n log n)
```

---

## The Master Theorem

The Master Theorem provides a direct formula for recurrences of the form:

### The Formula

```
T(n) = aT(n/b) + O(n^d)
```

Where:
- **a** = number of recursive calls (a >= 1)
- **b** = factor by which the problem size shrinks (b > 1)
- **d** = exponent of the non-recursive work (d >= 0)

### The Three Cases

Compare **log_b(a)** with **d**:

| Case | Condition | Result |
|---|---|---|
| Case 1 | d < log_b(a) | T(n) = O(n^(log_b(a))) |
| Case 2 | d = log_b(a) | T(n) = O(n^d * log n) |
| Case 3 | d > log_b(a) | T(n) = O(n^d) |

**Intuition**:
- **Case 1**: The recursion tree is "bottom-heavy" -- most work happens at the leaves.
- **Case 2**: Work is evenly distributed across all levels.
- **Case 3**: The recursion tree is "top-heavy" -- most work happens at the root.

### Worked Examples

#### Example 1: Merge Sort

```
T(n) = 2T(n/2) + O(n)
a = 2, b = 2, d = 1
log_b(a) = log_2(2) = 1
d = log_b(a) => Case 2
T(n) = O(n^1 * log n) = O(n log n)
```

#### Example 2: Binary Search

```
T(n) = 1T(n/2) + O(1)
a = 1, b = 2, d = 0
log_b(a) = log_2(1) = 0
d = log_b(a) => Case 2
T(n) = O(n^0 * log n) = O(log n)
```

#### Example 3: Strassen's Matrix Multiplication

```
T(n) = 7T(n/2) + O(n^2)
a = 7, b = 2, d = 2
log_b(a) = log_2(7) ≈ 2.807
d < log_b(a) => Case 1
T(n) = O(n^2.807)
```

#### Example 4: Simple Traversal (visit once, recurse on half)

```
T(n) = 2T(n/2) + O(1)
a = 2, b = 2, d = 0
log_b(a) = log_2(2) = 1
d < log_b(a) => Case 1
T(n) = O(n^1) = O(n)
```

#### Example 5: Linear scan then recurse

```
T(n) = 3T(n/4) + O(n)
a = 3, b = 4, d = 1
log_b(a) = log_4(3) ≈ 0.792
d > log_b(a) => Case 3
T(n) = O(n^1) = O(n)
```

---

## Analyzing Recursive Algorithms

### Merge Sort

Already analyzed above: **O(n log n)** via T(n) = 2T(n/2) + O(n).

### Quick Sort

Quick sort's analysis is trickier because the partition is not always balanced.

#### Go

```go
func quickSort(arr []int, low, high int) {
    if low < high {
        pivot := partition(arr, low, high)  // O(n) work
        quickSort(arr, low, pivot-1)        // T(k)
        quickSort(arr, pivot+1, high)       // T(n-k-1)
    }
}

func partition(arr []int, low, high int) int {
    pivot := arr[high]
    i := low - 1
    for j := low; j < high; j++ {
        if arr[j] <= pivot {
            i++
            arr[i], arr[j] = arr[j], arr[i]
        }
    }
    arr[i+1], arr[high] = arr[high], arr[i+1]
    return i + 1
}
```

#### Java

```java
public static void quickSort(int[] arr, int low, int high) {
    if (low < high) {
        int pivot = partition(arr, low, high);   // O(n) work
        quickSort(arr, low, pivot - 1);          // T(k)
        quickSort(arr, pivot + 1, high);         // T(n-k-1)
    }
}
```

#### Python

```python
def quick_sort(arr, low, high):
    if low < high:
        pivot = partition(arr, low, high)   # O(n) work
        quick_sort(arr, low, pivot - 1)     # T(k)
        quick_sort(arr, pivot + 1, high)    # T(n-k-1)
```

**Complexity analysis**:
- **Best/Average case**: Partition splits evenly. T(n) = 2T(n/2) + O(n) = **O(n log n)**.
- **Worst case**: Partition always picks min or max. T(n) = T(n-1) + O(n). Expanding: n + (n-1) + (n-2) + ... + 1 = n(n+1)/2 = **O(n^2)**.

### Tree Traversal

#### Go

```go
type Node struct {
    Val   int
    Left  *Node
    Right *Node
}

// Recurrence: T(n) = T(k) + T(n-k-1) + O(1)
// where k = nodes in left subtree
// For a balanced tree: T(n) = 2T(n/2) + O(1) -> O(n)
func inorder(root *Node, result *[]int) {
    if root == nil {
        return
    }
    inorder(root.Left, result)
    *result = append(*result, root.Val)
    inorder(root.Right, result)
}
```

#### Java

```java
// Recurrence: T(n) = T(k) + T(n-k-1) + O(1) -> O(n)
public static void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    inorder(root.left, result);
    result.add(root.val);
    inorder(root.right, result);
}
```

#### Python

```python
# Recurrence: T(n) = T(k) + T(n-k-1) + O(1) -> O(n)
def inorder(root, result):
    if root is None:
        return
    inorder(root.left, result)
    result.append(root.val)
    inorder(root.right, result)
```

**Regardless of tree shape**, every node is visited exactly once, so tree traversal is always **O(n)** where n is the number of nodes.

---

## Amortized Analysis Introduction

Sometimes a single operation is expensive, but it happens rarely. **Amortized analysis** spreads the cost of expensive operations over many cheap ones.

### Aggregate Method

Count the total cost of n operations, then divide by n to get the amortized cost per operation.

### Dynamic Array Example

A dynamic array (like Go slices, Java ArrayList, Python list) doubles its capacity when full.

#### Go

```go
// Simulating dynamic array growth
func appendItems(n int) {
    arr := make([]int, 0, 1)  // initial capacity 1
    for i := 0; i < n; i++ {
        if len(arr) == cap(arr) {
            // Resize: allocate new array of double size, copy all elements
            // Cost of this copy: current length
            newArr := make([]int, len(arr), cap(arr)*2)
            copy(newArr, arr)
            arr = newArr
        }
        arr = append(arr, i)  // O(1) when no resize needed
    }
}
```

#### Java

```java
// Simulating dynamic array growth
public static void appendItems(int n) {
    int capacity = 1;
    int size = 0;
    int[] arr = new int[capacity];

    for (int i = 0; i < n; i++) {
        if (size == capacity) {
            // Resize: double capacity, copy everything
            capacity *= 2;
            arr = Arrays.copyOf(arr, capacity);  // Cost: O(size)
        }
        arr[size++] = i;  // O(1) when no resize
    }
}
```

#### Python

```python
# Python lists handle this internally, but here is the concept:
def append_items(n):
    arr = []
    for i in range(n):
        arr.append(i)   # occasionally triggers resize
```

**Analysis using the aggregate method**:

Resizes happen at sizes 1, 2, 4, 8, 16, ..., up to n. The cost of each resize is the current size:

```
Total resize cost = 1 + 2 + 4 + 8 + ... + n = 2n - 1
Total append cost (no resize) = n * O(1) = n
Total cost = n + 2n - 1 = 3n - 1
Amortized cost per append = (3n - 1) / n ≈ 3 = O(1)
```

So even though individual appends can cost O(n) during a resize, the **amortized** cost per append is **O(1)**.

---

## Multiple Variables -- O(nm)

When your algorithm processes two independent inputs of different sizes, use separate variables.

#### Go

```go
// O(n * m) -- where n = len(text), m = len(pattern)
func bruteForceSearch(text, pattern string) int {
    n := len(text)
    m := len(pattern)
    for i := 0; i <= n-m; i++ {            // O(n) iterations
        match := true
        for j := 0; j < m; j++ {           // O(m) iterations
            if text[i+j] != pattern[j] {
                match = false
                break
            }
        }
        if match {
            return i
        }
    }
    return -1
}
```

#### Java

```java
// O(n * m) -- where n = text.length(), m = pattern.length()
public static int bruteForceSearch(String text, String pattern) {
    int n = text.length();
    int m = pattern.length();
    for (int i = 0; i <= n - m; i++) {         // O(n) iterations
        boolean match = true;
        for (int j = 0; j < m; j++) {          // O(m) iterations
            if (text.charAt(i + j) != pattern.charAt(j)) {
                match = false;
                break;
            }
        }
        if (match) return i;
    }
    return -1;
}
```

#### Python

```python
# O(n * m) -- where n = len(text), m = len(pattern)
def brute_force_search(text, pattern):
    n = len(text)
    m = len(pattern)
    for i in range(n - m + 1):           # O(n) iterations
        match = True
        for j in range(m):               # O(m) iterations
            if text[i + j] != pattern[j]:
                match = False
                break
        if match:
            return i
    return -1
```

**Important**: Do NOT simplify O(nm) to O(n^2) unless you know n = m. They are fundamentally different when n and m differ significantly.

### Another Example: Graph BFS

```
BFS visits every vertex and every edge once.
V = number of vertices, E = number of edges.
Time complexity: O(V + E) -- not O(V^2)
```

---

## Comparison of Analysis Approaches

| Approach | Best For | Difficulty | Precision |
|---|---|---|---|
| Counting operations | Simple iterative code | Easy | Exact count |
| Loop pattern recognition | Common patterns (nested, halving) | Easy | Big-O class |
| Recurrence + expansion | Simple recursions | Medium | Exact solution |
| Master Theorem | Divide-and-conquer recurrences | Medium | Big-O class |
| Amortized analysis | Operations with occasional expensive steps | Medium-Hard | Amortized cost |
| Recursion tree method | Visualizing recursive work | Medium | Approximate |

### When to Use Each

1. **Iterative code with simple loops** -> Count operations directly
2. **Divide-and-conquer algorithms** -> Write recurrence, apply Master Theorem
3. **Data structures with resize/rebalance** -> Amortized analysis
4. **Recursive code with uneven splits** -> Recursion tree or expansion
5. **Algorithms on graphs** -> Count vertices and edges separately

---

## Key Takeaways

1. **Recurrence relations** express recursive running times: T(n) = aT(n/b) + f(n).
2. **The Master Theorem** solves T(n) = aT(n/b) + O(n^d) by comparing d with log_b(a).
3. **Quick Sort** is O(n log n) average but O(n^2) worst case -- partition quality matters.
4. **Amortized analysis** shows that rare expensive operations (like array resizing) can still give O(1) per operation on average.
5. **Use separate variables** (O(nm), O(V+E)) when inputs have independent sizes.
6. **Choose the right tool**: iterative counting for loops, Master Theorem for divide-and-conquer, amortized for data structures.

---

> **Next**: [Senior Level](senior.md) -- Profiling, benchmarking, and complexity at system scale.
