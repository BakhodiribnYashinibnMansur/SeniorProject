# How to Calculate Complexity? -- Practice Tasks

## Table of Contents

1. [Beginner Tasks (1-5)](#beginner-tasks)
2. [Intermediate Tasks (6-10)](#intermediate-tasks)
3. [Advanced Tasks (11-15)](#advanced-tasks)
4. [Benchmark Task](#benchmark-task)

---

## Beginner Tasks

### Task 1: Count and Classify

**Goal**: Determine the Big-O complexity of the following function. Count operations, identify the dominant term, and justify your answer.

#### Go

```go
func task1(arr []int) int {
    n := len(arr)
    sum := 0
    for i := 0; i < n; i++ {
        sum += arr[i]
    }
    product := 1
    for i := 0; i < n; i++ {
        product *= arr[i]
    }
    return sum + product
}
// YOUR ANSWER: O(?)
// YOUR JUSTIFICATION:
```

#### Java

```java
public static int task1(int[] arr) {
    int n = arr.length;
    int sum = 0;
    for (int i = 0; i < n; i++) {
        sum += arr[i];
    }
    int product = 1;
    for (int i = 0; i < n; i++) {
        product *= arr[i];
    }
    return sum + product;
}
// YOUR ANSWER: O(?)
```

#### Python

```python
def task1(arr):
    n = len(arr)
    total = sum(arr)        # what is the cost of sum()?
    product = 1
    for x in arr:
        product *= x
    return total + product
# YOUR ANSWER: O(?)
```

**Expected Answer**: O(n) -- two sequential O(n) loops, O(n) + O(n) = O(n).

---

### Task 2: Nested Loop Analysis

**Goal**: Determine the exact number of times the inner statement executes, then express in Big-O.

#### Go

```go
func task2(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        for j := i; j < n; j++ {
            count++
        }
    }
    return count
}
// How many times does count++ execute for n = 5?
// Express the general formula and Big-O:
```

#### Java

```java
public static int task2(int n) {
    int count = 0;
    for (int i = 0; i < n; i++) {
        for (int j = i; j < n; j++) {
            count++;
        }
    }
    return count;
}
```

#### Python

```python
def task2(n):
    count = 0
    for i in range(n):
        for j in range(i, n):
            count += 1
    return count
```

**Expected Answer**: Inner loop runs (n-i) times for each i. Total: n + (n-1) + (n-2) + ... + 1 = n(n+1)/2. For n=5: 15. Big-O: O(n^2).

---

### Task 3: Logarithmic Pattern

**Goal**: Determine the complexity and trace the loop variable values.

#### Go

```go
func task3(n int) int {
    count := 0
    i := n
    for i > 1 {
        i = i / 2
        count++
    }
    return count
}
// Trace for n = 32: i takes values ?, ?, ?, ?, ?, done
// Complexity: O(?)
```

#### Java

```java
public static int task3(int n) {
    int count = 0;
    int i = n;
    while (i > 1) {
        i = i / 2;
        count++;
    }
    return count;
}
```

#### Python

```python
def task3(n):
    count = 0
    i = n
    while i > 1:
        i = i // 2
        count += 1
    return count
```

**Expected Answer**: For n=32: i = 32, 16, 8, 4, 2, 1 (5 iterations = log2(32)). Complexity: O(log n).

---

### Task 4: Mixed Complexity

**Goal**: Analyze this function that has both a loop and a nested halving pattern.

#### Go

```go
func task4(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        j := n
        for j > 0 {
            count++
            j = j / 2
        }
    }
    return count
}
// Complexity: O(?)
```

#### Java

```java
public static int task4(int n) {
    int count = 0;
    for (int i = 0; i < n; i++) {
        int j = n;
        while (j > 0) {
            count++;
            j = j / 2;
        }
    }
    return count;
}
```

#### Python

```python
def task4(n):
    count = 0
    for i in range(n):
        j = n
        while j > 0:
            count += 1
            j = j // 2
    return count
```

**Expected Answer**: Outer loop: O(n). Inner loop halves j from n: O(log n). Total: O(n log n).

---

### Task 5: Constant vs Variable Bounds

**Goal**: Determine complexity. Be careful about what depends on n.

#### Go

```go
func task5(arr []int) int {
    n := len(arr)
    total := 0
    for i := 0; i < min(n, 1000); i++ {
        for j := 0; j < min(n, 1000); j++ {
            total += arr[i%n] * arr[j%n]
        }
    }
    return total
}

func min(a, b int) int {
    if a < b { return a }
    return b
}
```

#### Java

```java
public static int task5(int[] arr) {
    int n = arr.length;
    int total = 0;
    int bound = Math.min(n, 1000);
    for (int i = 0; i < bound; i++) {
        for (int j = 0; j < bound; j++) {
            total += arr[i % n] * arr[j % n];
        }
    }
    return total;
}
```

#### Python

```python
def task5(arr):
    n = len(arr)
    total = 0
    bound = min(n, 1000)
    for i in range(bound):
        for j in range(bound):
            total += arr[i % n] * arr[j % n]
    return total
```

**Expected Answer**: The loops are bounded by min(n, 1000). For n >= 1000, both loops run exactly 1000 times: 1000^2 = 1,000,000 = O(1). For n < 1000, it is O(n^2). Overall: O(min(n, 1000)^2) = O(min(n^2, 10^6)). Since 10^6 is a constant, this is technically O(n^2) for small n and O(1) for large n.

---

## Intermediate Tasks

### Task 6: Recurrence from Code

**Goal**: Write the recurrence relation for this recursive function, then solve it.

#### Go

```go
func task6(arr []int) int {
    if len(arr) <= 1 {
        return 0
    }
    mid := len(arr) / 2
    left := task6(arr[:mid])
    right := task6(arr[mid:])

    crossCount := 0
    for _, l := range arr[:mid] {
        for _, r := range arr[mid:] {
            if l > r {
                crossCount++
            }
        }
    }
    return left + right + crossCount
}
// Recurrence: T(n) = ?
// Solution: T(n) = O(?)
```

#### Java

```java
public static int task6(int[] arr, int lo, int hi) {
    if (hi - lo <= 1) return 0;
    int mid = lo + (hi - lo) / 2;
    int left = task6(arr, lo, mid);
    int right = task6(arr, mid, hi);

    int crossCount = 0;
    for (int i = lo; i < mid; i++) {
        for (int j = mid; j < hi; j++) {
            if (arr[i] > arr[j]) crossCount++;
        }
    }
    return left + right + crossCount;
}
```

#### Python

```python
def task6(arr):
    if len(arr) <= 1:
        return 0
    mid = len(arr) // 2
    left = task6(arr[:mid])
    right = task6(arr[mid:])

    cross_count = 0
    for l in arr[:mid]:
        for r in arr[mid:]:
            if l > r:
                cross_count += 1
    return left + right + cross_count
```

**Expected Answer**: T(n) = 2T(n/2) + O(n^2). The cross-counting is a nested loop over two halves: (n/2)*(n/2) = n^2/4 = O(n^2). By Master Theorem: a=2, b=2, d=2. log_2(2) = 1 < 2 = d. Case 3: T(n) = O(n^2).

---

### Task 7: Amortized Analysis

**Goal**: Implement a stack with a "multipop" operation and analyze the amortized cost.

#### Go

```go
type Stack struct {
    data []int
}

func (s *Stack) Push(val int) {
    s.data = append(s.data, val)
}

func (s *Stack) Pop() int {
    if len(s.data) == 0 {
        return -1
    }
    val := s.data[len(s.data)-1]
    s.data = s.data[:len(s.data)-1]
    return val
}

// MultiPop removes up to k elements
func (s *Stack) MultiPop(k int) {
    for i := 0; i < k && len(s.data) > 0; i++ {
        s.Pop()
    }
}

// Task: Starting with an empty stack, perform a sequence of n operations
// (mix of Push and MultiPop). What is the amortized cost per operation?
// YOUR ANSWER:
```

#### Java

```java
class AmortizedStack {
    private List<Integer> data = new ArrayList<>();

    public void push(int val) { data.add(val); }

    public int pop() {
        if (data.isEmpty()) return -1;
        return data.remove(data.size() - 1);
    }

    public void multiPop(int k) {
        for (int i = 0; i < k && !data.isEmpty(); i++) {
            pop();
        }
    }
}
// Amortized cost per operation over n operations: O(?)
```

#### Python

```python
class AmortizedStack:
    def __init__(self):
        self.data = []

    def push(self, val):
        self.data.append(val)

    def pop(self):
        if not self.data:
            return -1
        return self.data.pop()

    def multi_pop(self, k):
        for _ in range(min(k, len(self.data))):
            self.pop()

# Amortized cost per operation over n operations: O(?)
```

**Expected Answer**: O(1) amortized. Each element can be popped at most once for each time it is pushed. Over n operations, the total number of pops (including within MultiPop) cannot exceed the total number of pushes, which is at most n. Total work: O(n). Amortized per operation: O(1).

---

### Task 8: Multiple Variables

**Goal**: Analyze complexity in terms of all relevant variables.

#### Go

```go
func task8(grid [][]int) []int {
    rows := len(grid)
    if rows == 0 { return nil }
    cols := len(grid[0])

    result := make([]int, rows)
    for i := 0; i < rows; i++ {
        maxVal := grid[i][0]
        for j := 1; j < cols; j++ {
            if grid[i][j] > maxVal {
                maxVal = grid[i][j]
            }
        }
        result[i] = maxVal
    }
    return result
}
// Complexity in terms of rows (r) and cols (c): O(?)
```

#### Java

```java
public static int[] task8(int[][] grid) {
    int rows = grid.length;
    int cols = grid[0].length;
    int[] result = new int[rows];
    for (int i = 0; i < rows; i++) {
        int maxVal = grid[i][0];
        for (int j = 1; j < cols; j++) {
            if (grid[i][j] > maxVal) maxVal = grid[i][j];
        }
        result[i] = maxVal;
    }
    return result;
}
```

#### Python

```python
def task8(grid):
    rows = len(grid)
    cols = len(grid[0])
    result = []
    for i in range(rows):
        max_val = grid[i][0]
        for j in range(1, cols):
            if grid[i][j] > max_val:
                max_val = grid[i][j]
        result.append(max_val)
    return result
```

**Expected Answer**: O(r * c) where r = rows and c = cols. The outer loop runs r times, the inner loop runs c-1 times per row. Total: r * (c-1) = O(rc).

---

### Task 9: Recursive Tree Traversal Complexity

**Goal**: Write the recurrence and determine complexity.

#### Go

```go
type TreeNode struct {
    Val   int
    Left  *TreeNode
    Right *TreeNode
}

func task9(root *TreeNode) int {
    if root == nil {
        return 0
    }
    leftHeight := height(root.Left)
    rightHeight := height(root.Right)
    if leftHeight == rightHeight {
        // Complete left subtree: 2^h - 1 nodes
        return (1 << leftHeight) + task9(root.Right)
    }
    // Complete right subtree: 2^h - 1 nodes
    return (1 << rightHeight) + task9(root.Left)
}

func height(node *TreeNode) int {
    h := 0
    for node != nil {
        h++
        node = node.Left
    }
    return h
}
// Assume the tree is a COMPLETE binary tree.
// What is the complexity of task9?
```

#### Java

```java
public static int task9(TreeNode root) {
    if (root == null) return 0;
    int leftH = height(root.left);
    int rightH = height(root.right);
    if (leftH == rightH) {
        return (1 << leftH) + task9(root.right);
    }
    return (1 << rightH) + task9(root.left);
}

private static int height(TreeNode node) {
    int h = 0;
    while (node != null) { h++; node = node.left; }
    return h;
}
```

#### Python

```python
def task9(root):
    if root is None:
        return 0
    left_h = height(root.left)
    right_h = height(root.right)
    if left_h == right_h:
        return (1 << left_h) + task9(root.right)
    return (1 << right_h) + task9(root.left)

def height(node):
    h = 0
    while node:
        h += 1
        node = node.left
    return h
```

**Expected Answer**: This counts nodes in a complete binary tree. At each level, height() costs O(log n) and we recurse once (going either left or right). We recurse O(log n) times (tree height). Total: O(log n * log n) = O(log^2 n).

---

### Task 10: Master Theorem Application

**Goal**: For each recurrence, identify a, b, d and apply the Master Theorem.

```
(a) T(n) = 9T(n/3) + n
(b) T(n) = T(2n/3) + 1
(c) T(n) = 3T(n/4) + n*log(n)
(d) T(n) = 2T(n/2) + n*log(n)
(e) T(n) = 8T(n/2) + n^2
```

**Expected Answers**:
- (a) a=9, b=3, d=1. log_3(9)=2 > 1. Case 1: O(n^2).
- (b) a=1, b=3/2, d=0. log_{3/2}(1)=0 = 0. Case 2: O(log n).
- (c) a=3, b=4. log_4(3) ≈ 0.792. g(n) = n*log(n). Since n*log(n) = Omega(n^0.793) and the regularity condition holds, Case 3: O(n log n).
- (d) a=2, b=2. log_2(2)=1. g(n) = n*log(n). This does NOT fit the simple Master Theorem (n*log(n) is not n^d). Using the extended form: O(n * log^2(n)).
- (e) a=8, b=2, d=2. log_2(8)=3 > 2. Case 1: O(n^3).

---

## Advanced Tasks

### Task 11: Substitution Method Proof

**Goal**: Prove by substitution (induction) that T(n) = 2T(floor(n/2)) + n is O(n log n).

Write out the full proof including base case, inductive hypothesis, and inductive step.

---

### Task 12: Akra-Bazzi Application

**Goal**: Solve T(n) = T(n/2) + T(n/3) + n using the Akra-Bazzi method.

Find p such that (1/2)^p + (1/3)^p = 1, then compute the integral.

**Hint**: p is between 0 and 1. Use numerical methods or trial and error.

---

### Task 13: Adversary Lower Bound

**Goal**: Prove that finding the second-largest element in an array of n distinct elements requires at least n + ceil(log_2(n)) - 2 comparisons.

**Hint**: The second-largest element must have lost only to the largest. Track which elements the eventual winner defeated directly.

---

### Task 14: Complexity of Recursive Fibonacci

**Goal**: Analyze both the naive recursive and memoized Fibonacci implementations.

#### Go

```go
// Version A: Naive
func fibNaive(n int) int {
    if n <= 1 { return n }
    return fibNaive(n-1) + fibNaive(n-2)
}

// Version B: Memoized
func fibMemo(n int, memo map[int]int) int {
    if n <= 1 { return n }
    if val, ok := memo[n]; ok { return val }
    memo[n] = fibMemo(n-1, memo) + fibMemo(n-2, memo)
    return memo[n]
}
```

#### Java

```java
// Version A: Naive
public static int fibNaive(int n) {
    if (n <= 1) return n;
    return fibNaive(n - 1) + fibNaive(n - 2);
}

// Version B: Memoized
public static int fibMemo(int n, Map<Integer, Integer> memo) {
    if (n <= 1) return n;
    if (memo.containsKey(n)) return memo.get(n);
    memo.put(n, fibMemo(n - 1, memo) + fibMemo(n - 2, memo));
    return memo.get(n);
}
```

#### Python

```python
# Version A: Naive
def fib_naive(n):
    if n <= 1: return n
    return fib_naive(n - 1) + fib_naive(n - 2)

# Version B: Memoized
def fib_memo(n, memo={}):
    if n <= 1: return n
    if n in memo: return memo[n]
    memo[n] = fib_memo(n - 1, memo) + fib_memo(n - 2, memo)
    return memo[n]
```

**Expected Answer**: Version A: T(n) = T(n-1) + T(n-2) + O(1). This grows as O(phi^n) where phi = (1+sqrt(5))/2 ≈ 1.618 (exponential). Version B: Each fib(k) is computed once for k=0..n, each costing O(1) after memoization. Total: O(n).

---

### Task 15: Space Complexity Analysis

**Goal**: Analyze both time AND space complexity.

#### Go

```go
func task15(n int) [][]int {
    matrix := make([][]int, n)
    for i := 0; i < n; i++ {
        matrix[i] = make([]int, n)
        for j := 0; j < n; j++ {
            matrix[i][j] = i * j
        }
    }

    result := make([]int, 0)
    for i := 0; i < n; i++ {
        for j := 0; j < n; j++ {
            if matrix[i][j]%2 == 0 {
                result = append(result, matrix[i][j])
            }
        }
    }
    return [][]int{matrix[0], result} // simplified return
}
// Time complexity: O(?)
// Space complexity: O(?)
```

#### Java

```java
public static List<int[]> task15(int n) {
    int[][] matrix = new int[n][n];
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            matrix[i][j] = i * j;

    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            if (matrix[i][j] % 2 == 0)
                result.add(matrix[i][j]);

    return List.of(matrix[0], result.stream().mapToInt(x->x).toArray());
}
```

#### Python

```python
def task15(n):
    matrix = [[i * j for j in range(n)] for i in range(n)]
    result = [matrix[i][j] for i in range(n) for j in range(n) if matrix[i][j] % 2 == 0]
    return matrix, result
```

**Expected Answer**: Time: O(n^2) for building the matrix + O(n^2) for scanning = O(n^2). Space: O(n^2) for the matrix + O(n^2) worst case for result = O(n^2).

---

## Benchmark Task

**Goal**: Write benchmarks that empirically verify the complexity of three algorithms: linear search O(n), binary search O(log n), and bubble sort O(n^2). Run at input sizes 1000, 2000, 4000, 8000, 16000 and compute the doubling ratio T(2n)/T(n).

#### Go

```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
    "time"
)

func linearSearch(arr []int, target int) int {
    for i, v := range arr {
        if v == target { return i }
    }
    return -1
}

func binarySearch(arr []int, target int) int {
    lo, hi := 0, len(arr)-1
    for lo <= hi {
        mid := lo + (hi-lo)/2
        if arr[mid] == target { return mid }
        if arr[mid] < target { lo = mid + 1 } else { hi = mid - 1 }
    }
    return -1
}

func bubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n; i++ {
        for j := 0; j < n-i-1; j++ {
            if arr[j] > arr[j+1] { arr[j], arr[j+1] = arr[j+1], arr[j] }
        }
    }
}

func main() {
    sizes := []int{1000, 2000, 4000, 8000, 16000}
    // TODO: measure each algorithm at each size
    // TODO: compute and print doubling ratios
    // Expected: linear ≈ 2.0, binary ≈ 1.0, bubble ≈ 4.0
    fmt.Println("Implement the benchmark loop here")
    _ = sizes
}
```

#### Java

```java
import java.util.*;

public class BenchmarkTask {
    // TODO: Implement linearSearch, binarySearch, bubbleSort
    // TODO: Measure each at sizes 1000, 2000, 4000, 8000, 16000
    // TODO: Compute doubling ratios

    public static void main(String[] args) {
        int[] sizes = {1000, 2000, 4000, 8000, 16000};
        // Your benchmarking code here
    }
}
```

#### Python

```python
import time
import random

def linear_search(arr, target):
    for i, v in enumerate(arr):
        if v == target: return i
    return -1

def binary_search(arr, target):
    lo, hi = 0, len(arr) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if arr[mid] == target: return mid
        if arr[mid] < target: lo = mid + 1
        else: hi = mid - 1
    return -1

def bubble_sort(arr):
    n = len(arr)
    for i in range(n):
        for j in range(n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]

# TODO: Benchmark each at sizes [1000, 2000, 4000, 8000, 16000]
# TODO: Compute and print doubling ratios
# Expected: linear ≈ 2.0, binary ≈ 1.0, bubble ≈ 4.0
```

**Expected Results**:

| Size | Linear Ratio | Binary Ratio | Bubble Ratio |
|---|---|---|---|
| 2000/1000 | ~2.0 | ~1.1 | ~4.0 |
| 4000/2000 | ~2.0 | ~1.1 | ~4.0 |
| 8000/4000 | ~2.0 | ~1.1 | ~4.0 |
| 16000/8000 | ~2.0 | ~1.1 | ~4.0 |
