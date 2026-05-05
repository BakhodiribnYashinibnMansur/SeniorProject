# Big-O Notation -- Tasks

## Table of Contents

1. [Task 1: Identify Complexity of Simple Loops](#task-1-identify-complexity-of-simple-loops)
2. [Task 2: Prove Big-O from Definition](#task-2-prove-big-o-from-definition)
3. [Task 3: Compare Two Algorithms](#task-3-compare-two-algorithms)
4. [Task 4: Analyze Nested Loops with Different Bounds](#task-4-analyze-nested-loops-with-different-bounds)
5. [Task 5: Recursive Complexity Analysis](#task-5-recursive-complexity-analysis)
6. [Task 6: Optimize O(n^2) to O(n)](#task-6-optimize-on2-to-on)
7. [Task 7: Multi-Variable Complexity](#task-7-multi-variable-complexity)
8. [Task 8: Amortized Analysis of Dynamic Array](#task-8-amortized-analysis-of-dynamic-array)
9. [Task 9: Logarithmic Patterns](#task-9-logarithmic-patterns)
10. [Task 10: Space Complexity Analysis](#task-10-space-complexity-analysis)
11. [Task 11: String Complexity Traps](#task-11-string-complexity-traps)
12. [Task 12: Sorting Complexity Comparison](#task-12-sorting-complexity-comparison)
13. [Task 13: Hash Map vs Brute Force](#task-13-hash-map-vs-brute-force)
14. [Task 14: Recursive vs Iterative](#task-14-recursive-vs-iterative)
15. [Task 15: Real-World API Complexity Budget](#task-15-real-world-api-complexity-budget)
16. [Benchmark Task: Empirically Verify Big-O](#benchmark-task-empirically-verify-big-o)

---

## Task 1: Identify Complexity of Simple Loops

**Objective:** Determine the Big-O time complexity of each code snippet.

**Go:**
```go
// Snippet A
func snippetA(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        count++
    }
    return count
}

// Snippet B
func snippetB(n int) int {
    count := 0
    for i := 0; i < n; i += 3 {
        count++
    }
    return count
}

// Snippet C
func snippetC(n int) int {
    count := 0
    for i := 1; i < n; i *= 2 {
        count++
    }
    return count
}

// Snippet D
func snippetD(n int) int {
    count := 0
    for i := n; i > 0; i /= 3 {
        count++
    }
    return count
}

// Snippet E
func snippetE(n int) int {
    count := 0
    for i := 0; i < 1000; i++ {
        count++
    }
    return count
}
```

**Java:**
```java
// Snippet A
public static int snippetA(int n) { int c = 0; for (int i = 0; i < n; i++) c++; return c; }

// Snippet B
public static int snippetB(int n) { int c = 0; for (int i = 0; i < n; i += 3) c++; return c; }

// Snippet C
public static int snippetC(int n) { int c = 0; for (int i = 1; i < n; i *= 2) c++; return c; }

// Snippet D
public static int snippetD(int n) { int c = 0; for (int i = n; i > 0; i /= 3) c++; return c; }

// Snippet E
public static int snippetE(int n) { int c = 0; for (int i = 0; i < 1000; i++) c++; return c; }
```

**Python:**
```python
# Snippet A
def snippet_a(n):
    count = 0
    for i in range(n):
        count += 1
    return count

# Snippet B
def snippet_b(n):
    count = 0
    for i in range(0, n, 3):
        count += 1
    return count

# Snippet C
def snippet_c(n):
    count = 0
    i = 1
    while i < n:
        count += 1
        i *= 2
    return count

# Snippet D
def snippet_d(n):
    count = 0
    i = n
    while i > 0:
        count += 1
        i //= 3
    return count

# Snippet E
def snippet_e(n):
    count = 0
    for i in range(1000):
        count += 1
    return count
```

**Expected Answers:**
- A: O(n) -- linear loop
- B: O(n) -- step size 3 is a constant, so n/3 = O(n)
- C: O(log n) -- i doubles each step
- D: O(log n) -- i is divided by 3 each step (log base 3)
- E: O(1) -- loop runs fixed 1000 times regardless of n

---

## Task 2: Prove Big-O from Definition

**Objective:** For each function f(n), find constants c and n0 to prove the given Big-O claim.

1. Prove: f(n) = 7n + 12 is O(n). Find c and n0.
2. Prove: f(n) = 3n^2 + 10n + 5 is O(n^2). Find c and n0.
3. Prove: f(n) = log(n) + 100 is O(log n). Find c and n0 (assume n >= 2).
4. Prove: f(n) = n * (n + 1) / 2 is O(n^2). Find c and n0.
5. Disprove: f(n) = n^2 is NOT O(n). Provide a proof by contradiction.

**Expected Answers:**
1. c = 19, n0 = 1: 7n + 12 <= 7n + 12n = 19n for n >= 1.
2. c = 4, n0 = 11: 3n^2 + 10n + 5 <= 3n^2 + n^2 = 4n^2 when 10n + 5 <= n^2, which holds for n >= 11.
3. c = 101, n0 = 2: log(n) + 100 <= 101 * log(n) when 100 <= 100*log(n), i.e., log(n) >= 1, which holds for n >= 2 (base 2) or n >= 10 (base 10).
4. c = 1, n0 = 1: n(n+1)/2 = (n^2 + n)/2 <= n^2 for n >= 1.
5. Assume n^2 <= c*n for all n >= n0. Then n <= c for all n >= n0. Choose n = c + 1 >= n0. Then c + 1 <= c. Contradiction.

---

## Task 3: Compare Two Algorithms

**Objective:** Implement two solutions to the same problem, measure their performance, and explain the Big-O difference.

**Problem:** Find if any two numbers in an array sum to a target value.

**Go:**
```go
// Solution A: Brute Force O(n^2)
func twoSumBrute(arr []int, target int) bool {
    n := len(arr)
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            if arr[i]+arr[j] == target {
                return true
            }
        }
    }
    return false
}

// Solution B: Hash Map O(n)
func twoSumHash(arr []int, target int) bool {
    seen := make(map[int]bool)
    for _, v := range arr {
        if seen[target-v] {
            return true
        }
        seen[v] = true
    }
    return false
}
```

**Java:**
```java
// Solution A: Brute Force O(n^2)
public static boolean twoSumBrute(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[i] + arr[j] == target) return true;
        }
    }
    return false;
}

// Solution B: Hash Map O(n)
public static boolean twoSumHash(int[] arr, int target) {
    Set<Integer> seen = new HashSet<>();
    for (int v : arr) {
        if (seen.contains(target - v)) return true;
        seen.add(v);
    }
    return false;
}
```

**Python:**
```python
# Solution A: Brute Force O(n^2)
def two_sum_brute(arr, target):
    n = len(arr)
    for i in range(n):
        for j in range(i + 1, n):
            if arr[i] + arr[j] == target:
                return True
    return False

# Solution B: Hash Map O(n)
def two_sum_hash(arr, target):
    seen = set()
    for v in arr:
        if target - v in seen:
            return True
        seen.add(v)
    return False
```

**Task:** Time both solutions with n = 1000, 5000, 10000, 50000. Plot the results and verify that Solution A grows quadratically while Solution B grows linearly.

---

## Task 4: Analyze Nested Loops with Different Bounds

**Objective:** Determine the time complexity of each nested loop pattern.

**Go:**
```go
// Pattern A: Standard nested
func patternA(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        for j := 0; j < n; j++ {
            count++
        }
    }
    return count // ?
}

// Pattern B: Inner depends on outer
func patternB(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        for j := 0; j < i; j++ {
            count++
        }
    }
    return count // ?
}

// Pattern C: Inner is logarithmic
func patternC(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        for j := 1; j < n; j *= 2 {
            count++
        }
    }
    return count // ?
}

// Pattern D: Outer is logarithmic
func patternD(n int) int {
    count := 0
    for i := 1; i < n; i *= 2 {
        for j := 0; j < n; j++ {
            count++
        }
    }
    return count // ?
}

// Pattern E: Both logarithmic
func patternE(n int) int {
    count := 0
    for i := 1; i < n; i *= 2 {
        for j := 1; j < n; j *= 3 {
            count++
        }
    }
    return count // ?
}
```

**Expected Answers:**
- A: O(n^2) -- n * n
- B: O(n^2) -- 0 + 1 + 2 + ... + (n-1) = n(n-1)/2
- C: O(n log n) -- n iterations * log(n) each
- D: O(n log n) -- log(n) iterations * n each
- E: O(log^2 n) -- log2(n) * log3(n)

---

## Task 5: Recursive Complexity Analysis

**Objective:** Write the recurrence relation and solve for each recursive function.

**Go:**
```go
// Function A
func recA(n int) int {
    if n <= 0 { return 1 }
    return recA(n - 1) + recA(n - 1)
}

// Function B
func recB(n int) int {
    if n <= 1 { return 1 }
    return recB(n/2) + 1
}

// Function C
func recC(arr []int, lo, hi int) int {
    if lo >= hi { return 0 }
    mid := (lo + hi) / 2
    return 1 + recC(arr, lo, mid) + recC(arr, mid+1, hi)
}

// Function D
func recD(n int) int {
    if n <= 1 { return 1 }
    count := 0
    for i := 0; i < n; i++ {
        count++
    }
    return count + recD(n-1)
}
```

**Expected Answers:**
- A: T(n) = 2T(n-1) + O(1). Solution: O(2^n)
- B: T(n) = T(n/2) + O(1). Solution: O(log n)
- C: T(n) = 2T(n/2) + O(1). By Master Theorem (a=2, b=2, d=0): O(n)
- D: T(n) = T(n-1) + O(n). Solution: O(n) + O(n-1) + ... + O(1) = O(n^2)

---

## Task 6: Optimize O(n^2) to O(n)

**Objective:** Rewrite each O(n^2) solution to be O(n) or O(n log n).

**Problem:** Find the first duplicate in an array.

**Go:**
```go
// O(n^2) version -- YOUR STARTING POINT
func firstDuplicateSlow(arr []int) int {
    for i := 0; i < len(arr); i++ {
        for j := 0; j < i; j++ {
            if arr[i] == arr[j] {
                return arr[i]
            }
        }
    }
    return -1
}

// TODO: Write O(n) version using a hash set
```

**Java:**
```java
// O(n^2) version
public static int firstDuplicateSlow(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        for (int j = 0; j < i; j++) {
            if (arr[i] == arr[j]) return arr[i];
        }
    }
    return -1;
}

// TODO: Write O(n) version using a HashSet
```

**Python:**
```python
# O(n^2) version
def first_duplicate_slow(arr):
    for i in range(len(arr)):
        for j in range(i):
            if arr[i] == arr[j]:
                return arr[i]
    return -1

# TODO: Write O(n) version using a set
```

**Expected O(n) Solution (Go):**
```go
func firstDuplicateFast(arr []int) int {
    seen := make(map[int]bool)
    for _, v := range arr {
        if seen[v] {
            return v
        }
        seen[v] = true
    }
    return -1
}
```

---

## Task 7: Multi-Variable Complexity

**Objective:** Analyze complexity using multiple variables.

**Go:**
```go
// Given a graph as adjacency list, find all nodes reachable from start
func reachable(graph map[int][]int, start int) []int {
    visited := map[int]bool{start: true}
    stack := []int{start}
    result := []int{}

    for len(stack) > 0 {
        node := stack[len(stack)-1]
        stack = stack[:len(stack)-1]
        result = append(result, node)

        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                visited[neighbor] = true
                stack = append(stack, neighbor)
            }
        }
    }
    return result
}
// What is the time complexity in terms of V (vertices) and E (edges)?
// What is the space complexity?
```

**Expected Answer:** Time: O(V + E). Each vertex is processed once (O(V)), and each edge is examined once (O(E)). Space: O(V) for the visited map and stack.

---

## Task 8: Amortized Analysis of Dynamic Array

**Objective:** Implement a dynamic array and prove that append is amortized O(1).

**Go:**
```go
type DynArray struct {
    data     []int
    size     int
    capacity int
    resizeCount int // Track number of resizes for analysis
    copyCount   int // Track total elements copied
}

func NewDynArray() *DynArray {
    return &DynArray{data: make([]int, 1), capacity: 1}
}

func (d *DynArray) Append(val int) {
    if d.size == d.capacity {
        d.resizeCount++
        d.copyCount += d.size
        d.capacity *= 2
        newData := make([]int, d.capacity)
        copy(newData, d.data[:d.size])
        d.data = newData
    }
    d.data[d.size] = val
    d.size++
}

// TODO: Insert n elements and verify that:
// 1. Total copyCount is approximately 2n
// 2. resizeCount is approximately log2(n)
// 3. Average cost per append (copyCount/n) approaches a constant
```

---

## Task 9: Logarithmic Patterns

**Objective:** Identify which patterns produce O(log n) complexity.

**Go:**
```go
// Which of these are O(log n)?

func logA(n int) int { c := 0; for i := n; i > 0; i /= 2 { c++ }; return c }
func logB(n int) int { c := 0; for i := 1; i*i < n; i++ { c++ }; return c }
func logC(n int) int { c := 0; for i := 0; i < n; i++ { c++ }; return c }
func logD(n int) int { c := 0; for i := n; i > 1; i = int(math.Sqrt(float64(i))) { c++ }; return c }
func logE(n int) int { c := 0; for i := 2; i < n; i *= i { c++ }; return c }
```

**Expected Answers:**
- logA: O(log n) -- halving
- logB: O(sqrt(n)) -- i goes up to sqrt(n)
- logC: O(n) -- linear
- logD: O(log log n) -- repeated square root
- logE: O(log log n) -- i squares each iteration (2, 4, 16, 256, ...)

---

## Task 10: Space Complexity Analysis

**Objective:** Determine the space complexity of each function.

**Go:**
```go
// Function A
func spaceA(n int) [][]int {
    matrix := make([][]int, n)
    for i := 0; i < n; i++ {
        matrix[i] = make([]int, n)
    }
    return matrix
}

// Function B
func spaceB(n int) int {
    if n <= 1 { return n }
    return spaceB(n-1) + spaceB(n-2)
}

// Function C
func spaceC(arr []int) int {
    sum := 0
    for _, v := range arr { sum += v }
    return sum
}
```

**Expected Answers:**
- spaceA: O(n^2) -- creates n x n matrix
- spaceB: O(n) -- recursion stack goes n levels deep (despite O(2^n) time)
- spaceC: O(1) -- only a single variable regardless of input

---

## Task 11: String Complexity Traps

**Objective:** Identify the hidden complexity in string operations.

**Go:**
```go
// What is the TRUE time complexity?
func buildString(n int) string {
    result := ""
    for i := 0; i < n; i++ {
        result += "a" // String concatenation in a loop
    }
    return result
}

// TODO: Rewrite using strings.Builder to achieve true O(n)
```

**Java:**
```java
// What is the TRUE time complexity?
public static String buildString(int n) {
    String result = "";
    for (int i = 0; i < n; i++) {
        result += "a"; // String concatenation in a loop
    }
    return result;
}

// TODO: Rewrite using StringBuilder to achieve true O(n)
```

**Python:**
```python
# What is the TRUE time complexity?
def build_string(n):
    result = ""
    for i in range(n):
        result += "a"  # String concatenation in a loop
    return result

# TODO: Rewrite using list join to achieve true O(n)
```

**Expected Answer:** The naive versions are O(n^2) because each concatenation creates a new string, copying all previous characters. (Note: Python and some Go implementations may optimize this in certain cases, but the worst-case is O(n^2).)

---

## Task 12: Sorting Complexity Comparison

**Objective:** Implement three sorting algorithms and benchmark them to verify their Big-O.

Implement insertion sort O(n^2), merge sort O(n log n), and Python's built-in sort (Timsort, O(n log n)). Time each on arrays of size 1000, 5000, 10000, 50000, 100000. Verify the growth ratios match expected Big-O.

---

## Task 13: Hash Map vs Brute Force

**Objective:** Solve the "frequency count" problem two ways and compare.

**Problem:** Given an array, find the element that appears more than n/2 times.

**Go:**
```go
// Approach A: O(n^2) brute force
func majorityBrute(arr []int) int {
    n := len(arr)
    for _, candidate := range arr {
        count := 0
        for _, v := range arr {
            if v == candidate { count++ }
        }
        if count > n/2 { return candidate }
    }
    return -1
}

// TODO: Approach B using hash map -- O(n) time, O(n) space
// TODO: Approach C using Boyer-Moore voting -- O(n) time, O(1) space
```

---

## Task 14: Recursive vs Iterative

**Objective:** Convert a recursive O(2^n) Fibonacci to iterative O(n).

**Go:**
```go
// O(2^n) time, O(n) space
func fibRecursive(n int) int {
    if n <= 1 { return n }
    return fibRecursive(n-1) + fibRecursive(n-2)
}

// TODO: Write O(n) time, O(1) space iterative version
// TODO: Write O(n) time, O(n) space memoized version
// TODO: Benchmark all three for n = 10, 20, 30, 40
```

---

## Task 15: Real-World API Complexity Budget

**Objective:** Design an API endpoint and document its complexity budget.

**Scenario:** Build a "search users" endpoint that:
1. Validates the JWT token
2. Parses query parameters
3. Queries the database
4. Filters results by permission level
5. Serializes to JSON

**Task:**
- Assign a Big-O complexity to each step.
- Calculate the total complexity.
- Set a latency budget for each step.
- Identify which step is the bottleneck and propose an optimization.

---

## Benchmark Task: Empirically Verify Big-O

**Objective:** Write a comprehensive benchmark that tests O(1), O(log n), O(n), O(n log n), and O(n^2) algorithms, plots the results, and verifies they match theoretical predictions.

**Go:**
```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
    "time"
)

func benchmarkFunc(name string, sizes []int, f func([]int)) {
    fmt.Printf("\n--- %s ---\n", name)
    var prevTime float64
    for _, n := range sizes {
        data := make([]int, n)
        for i := range data { data[i] = rand.Intn(n * 10) }

        start := time.Now()
        f(data)
        elapsed := time.Since(start).Seconds()

        ratio := 0.0
        if prevTime > 0 { ratio = elapsed / prevTime }
        fmt.Printf("n=%8d  time=%.6fs  ratio=%.2f\n", n, elapsed, ratio)
        prevTime = elapsed
    }
}

func main() {
    sizes := []int{1000, 5000, 10000, 50000, 100000}

    // O(1): Access first element
    benchmarkFunc("O(1) - Array Access", sizes, func(arr []int) {
        _ = arr[0]
    })

    // O(n): Linear scan
    benchmarkFunc("O(n) - Linear Scan", sizes, func(arr []int) {
        sum := 0
        for _, v := range arr { sum += v }
    })

    // O(n log n): Sort
    benchmarkFunc("O(n log n) - Sort", sizes, func(arr []int) {
        sort.Ints(arr)
    })

    // O(n^2): Nested loop
    benchmarkFunc("O(n^2) - Nested Loop", sizes[:4], func(arr []int) {
        n := len(arr)
        for i := 0; i < n; i++ {
            for j := 0; j < n; j++ {
                _ = arr[i] + arr[j]
            }
        }
    })
}

// Expected output pattern:
// O(1):      ratio ~ 1.0 (constant, regardless of size increase)
// O(n):      ratio ~ 5.0 when size increases 5x
// O(n log n): ratio ~ 5-7 when size increases 5x
// O(n^2):    ratio ~ 25 when size increases 5x
```

**Java:**
```java
import java.util.*;

public class BigOBenchmark {
    interface BenchmarkFunc { void run(int[] arr); }

    static void benchmark(String name, int[] sizes, BenchmarkFunc f) {
        System.out.println("\n--- " + name + " ---");
        double prevTime = 0;
        Random rand = new Random(42);

        for (int n : sizes) {
            int[] data = new int[n];
            for (int i = 0; i < n; i++) data[i] = rand.nextInt(n * 10);

            long start = System.nanoTime();
            f.run(data);
            double elapsed = (System.nanoTime() - start) / 1e9;

            double ratio = prevTime > 0 ? elapsed / prevTime : 0;
            System.out.printf("n=%8d  time=%.6fs  ratio=%.2f%n", n, elapsed, ratio);
            prevTime = elapsed;
        }
    }

    public static void main(String[] args) {
        int[] sizes = {1000, 5000, 10000, 50000, 100000};

        benchmark("O(n) - Linear Scan", sizes, arr -> {
            int sum = 0;
            for (int v : arr) sum += v;
        });

        benchmark("O(n log n) - Sort", sizes, arr -> Arrays.sort(arr));

        int[] smallSizes = {1000, 5000, 10000, 50000};
        benchmark("O(n^2) - Nested Loop", smallSizes, arr -> {
            int n = arr.length;
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    { int x = arr[i] + arr[j]; }
        });
    }
}
```

**Python:**
```python
import time
import random

def benchmark(name, sizes, func):
    print(f"\n--- {name} ---")
    prev_time = 0

    for n in sizes:
        data = [random.randint(0, n * 10) for _ in range(n)]

        start = time.perf_counter()
        func(data)
        elapsed = time.perf_counter() - start

        ratio = elapsed / prev_time if prev_time > 0 else 0
        print(f"n={n:>8}  time={elapsed:.6f}s  ratio={ratio:.2f}")
        prev_time = elapsed

sizes = [1000, 5000, 10000, 50000, 100000]

benchmark("O(n) - Linear Scan", sizes, lambda arr: sum(arr))
benchmark("O(n log n) - Sort", sizes, lambda arr: sorted(arr))

small_sizes = [1000, 5000, 10000, 20000]
def nested_loop(arr):
    n = len(arr)
    for i in range(n):
        for j in range(n):
            _ = arr[i] + arr[j]

benchmark("O(n^2) - Nested Loop", small_sizes, nested_loop)
```
