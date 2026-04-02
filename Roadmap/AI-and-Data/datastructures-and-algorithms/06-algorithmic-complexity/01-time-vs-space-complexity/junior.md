# Time vs Space Complexity — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Glossary](#glossary)
4. [Core Concepts](#core-concepts)
5. [Big-O Summary](#big-o-summary)
6. [Real-World Analogies](#real-world-analogies)
7. [Pros & Cons](#pros--cons)
8. [Code Examples](#code-examples)
9. [Coding Patterns](#coding-patterns)
10. [Error Handling](#error-handling)
11. [Performance Tips](#performance-tips)
12. [Best Practices](#best-practices)
13. [Edge Cases & Pitfalls](#edge-cases--pitfalls)
14. [Common Mistakes](#common-mistakes)
15. [Cheat Sheet](#cheat-sheet)
16. [Visual Animation](#visual-animation)
17. [Summary](#summary)
18. [Further Reading](#further-reading)

---

## Introduction

> Focus: "What is it?" and "How to use it?"

Every algorithm uses two fundamental resources: **time** (how many operations it performs) and **space** (how much memory it uses). Time complexity describes how the runtime of an algorithm grows as the input size increases. Space complexity describes how the memory usage grows with input size.

Understanding these two concepts is the foundation of algorithm analysis. When you write code, you're always making trade-offs between speed and memory — sometimes you can make your program faster by using more memory, or reduce memory usage at the cost of slower execution.

---

## Prerequisites

- **Required:** Basic programming — variables, loops, functions, arrays (in any of Go/Java/Python)
- **Required:** Understanding of what an algorithm is (a step-by-step procedure to solve a problem)
- **Helpful:** Basic math — exponents, logarithms

---

## Glossary

| Term | Definition |
|------|-----------|
| **Algorithm** | A finite set of well-defined instructions to solve a specific problem |
| **Time Complexity** | A measure of how the number of operations grows relative to input size |
| **Space Complexity** | A measure of how memory usage grows relative to input size |
| **Big-O Notation** | Mathematical notation that describes the upper bound of growth rate — O(n), O(n²), etc. |
| **Input Size (n)** | The size of the data the algorithm processes (e.g., array length, string length) |
| **Operation** | A basic computational step — comparison, assignment, arithmetic, etc. |
| **Auxiliary Space** | Extra memory used by an algorithm beyond the input itself |
| **In-Place Algorithm** | An algorithm that uses O(1) auxiliary space — modifies input directly |
| **Trade-Off** | Gaining an advantage in one area (time) at the cost of another (space) |
| **Constant Factor** | The hidden multiplier in Big-O — O(2n) simplifies to O(n) |

---

## Core Concepts

### Concept 1: What is Time Complexity?

Time complexity measures how the number of basic operations an algorithm performs grows as the input size increases. We don't measure actual seconds because that depends on hardware — instead, we count operations. If an algorithm scans every element in an array of size `n`, it performs `n` operations, so its time complexity is O(n). If it compares every pair, it performs `n × n` operations, giving O(n²).

### Concept 2: What is Space Complexity?

Space complexity measures the total memory an algorithm needs relative to input size. This includes two parts: (1) the space for the input itself, and (2) **auxiliary space** — any extra memory the algorithm allocates. When we say "space complexity," we usually mean auxiliary space. For example, creating a copy of an array takes O(n) extra space, while swapping two variables uses O(1) extra space.

### Concept 3: The Time-Space Trade-Off

You can often make an algorithm faster by using more memory, or use less memory at the cost of speed. A classic example: to check for duplicates in an array, you can use a nested loop (O(n²) time, O(1) space) or a hash set (O(n) time, O(n) space). The hash set trades memory for speed. This trade-off is one of the most important decisions in algorithm design.

### Concept 4: Best, Worst, and Average Case

An algorithm can behave differently depending on the input:
- **Best case:** The most favorable input (e.g., searching for the first element — found immediately)
- **Worst case:** The most unfavorable input (e.g., element not in the array — must check everything)
- **Average case:** Expected behavior over all possible inputs

We usually analyze **worst case** because it guarantees an upper bound on performance.

### Concept 5: How to Measure Complexity

To analyze complexity: (1) identify the input size `n`, (2) count the basic operations as a function of `n`, (3) drop constants and lower-order terms, (4) express using Big-O notation. For example, if an algorithm does `3n² + 5n + 10` operations, its time complexity is O(n²) because the `n²` term dominates for large `n`.

---

## Big-O Summary

| Complexity | Name | Time Example | Space Example |
|-----------|------|-------------|---------------|
| O(1) | Constant | Array access by index | Swapping two variables |
| O(log n) | Logarithmic | Binary search | Binary search (iterative) |
| O(n) | Linear | Linear search | Creating a copy of array |
| O(n log n) | Linearithmic | Merge sort | Merge sort (auxiliary array) |
| O(n²) | Quadratic | Bubble sort | Creating 2D matrix |
| O(2ⁿ) | Exponential | Recursive Fibonacci | Recursive call stack for Fibonacci |
| O(n!) | Factorial | Generating all permutations | Storing all permutations |

---

## Real-World Analogies

| Concept | Analogy |
|---------|--------|
| **Time Complexity** | How long it takes to find a book in a library — depends on how many books there are and your search strategy |
| **Space Complexity** | How many sticky notes you need while solving a puzzle — more notes = more memory |
| **O(1) Time** | Knowing exactly which shelf a book is on — instant access regardless of library size |
| **O(n) Time** | Reading every book title on a shelf until you find the right one — time grows with shelf size |
| **O(n²) Time** | Comparing every book with every other book to find duplicates — time grows quadratically |
| **Time-Space Trade-Off** | Using a phone book index (extra space) to find names faster (less time) vs. scanning page by page (no extra space, more time) |

> **Where analogies break down:** Real-world "operations" aren't uniform — finding a physical book may depend on its weight or location. In algorithm analysis, we assume every basic operation takes the same amount of time.

---

## Pros & Cons

### Time-Optimized Approaches

| Pros | Cons |
|------|------|
| Faster execution | Uses more memory |
| Better user experience | May be more complex to implement |
| Handles large inputs well | Memory can be expensive (embedded systems) |

### Space-Optimized Approaches

| Pros | Cons |
|------|------|
| Uses less memory | Slower execution |
| Works on memory-constrained devices | May not scale for large inputs |
| Often simpler code | Can hit time limits in competitive programming |

**When to optimize for time:** User-facing applications, real-time systems, large datasets
**When to optimize for space:** Embedded systems, mobile devices, when data barely fits in memory

---

## Code Examples

### Example 1: Measuring Time — O(n) vs O(n²)

#### Go

```go
package main

import (
    "fmt"
    "time"
)

// O(n) — linear search
func linearSearch(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i
        }
    }
    return -1
}

// O(n²) — check all pairs
func hasDuplicateBrute(arr []int) bool {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] == arr[j] {
                return true
            }
        }
    }
    return false
}

func main() {
    n := 10000
    arr := make([]int, n)
    for i := range arr {
        arr[i] = i
    }

    start := time.Now()
    linearSearch(arr, n-1) // worst case
    fmt.Printf("Linear search O(n): %v\n", time.Since(start))

    start = time.Now()
    hasDuplicateBrute(arr)
    fmt.Printf("Duplicate check O(n²): %v\n", time.Since(start))
}
```

#### Java

```java
public class TimeComplexity {
    // O(n) — linear search
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    // O(n²) — check all pairs
    public static boolean hasDuplicateBrute(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] == arr[j]) return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int n = 10000;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;

        long start = System.nanoTime();
        linearSearch(arr, n - 1);
        System.out.printf("Linear search O(n): %.3f ms%n",
            (System.nanoTime() - start) / 1_000_000.0);

        start = System.nanoTime();
        hasDuplicateBrute(arr);
        System.out.printf("Duplicate check O(n²): %.3f ms%n",
            (System.nanoTime() - start) / 1_000_000.0);
    }
}
```

#### Python

```python
import time

# O(n) — linear search
def linear_search(arr, target):
    for i, v in enumerate(arr):
        if v == target:
            return i
    return -1

# O(n²) — check all pairs
def has_duplicate_brute(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False

if __name__ == "__main__":
    n = 10000
    arr = list(range(n))

    start = time.time()
    linear_search(arr, n - 1)
    print(f"Linear search O(n): {(time.time() - start) * 1000:.3f} ms")

    start = time.time()
    has_duplicate_brute(arr)
    print(f"Duplicate check O(n²): {(time.time() - start) * 1000:.3f} ms")
```

**What it does:** Compares the execution time of O(n) linear search vs O(n²) duplicate check on the same array.
**Run:** `go run main.go` | `javac TimeComplexity.java && java TimeComplexity` | `python time_complexity.py`

---

### Example 2: Time-Space Trade-Off — Duplicate Detection

#### Go

```go
package main

import "fmt"

// O(n²) time, O(1) space — brute force
func hasDuplicateSlow(arr []int) bool {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i] == arr[j] {
                return true
            }
        }
    }
    return false
}

// O(n) time, O(n) space — hash set
func hasDuplicateFast(arr []int) bool {
    seen := make(map[int]bool)
    for _, v := range arr {
        if seen[v] {
            return true
        }
        seen[v] = true
    }
    return false
}

func main() {
    arr := []int{1, 2, 3, 4, 5, 3}
    fmt.Println("Brute force:", hasDuplicateSlow(arr)) // true
    fmt.Println("Hash set:", hasDuplicateFast(arr))     // true
}
```

#### Java

```java
import java.util.HashSet;

public class TradeOff {
    // O(n²) time, O(1) space
    public static boolean hasDuplicateSlow(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] == arr[j]) return true;
            }
        }
        return false;
    }

    // O(n) time, O(n) space
    public static boolean hasDuplicateFast(int[] arr) {
        HashSet<Integer> seen = new HashSet<>();
        for (int v : arr) {
            if (!seen.add(v)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 3};
        System.out.println("Brute force: " + hasDuplicateSlow(arr));
        System.out.println("Hash set: " + hasDuplicateFast(arr));
    }
}
```

#### Python

```python
# O(n²) time, O(1) space
def has_duplicate_slow(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False

# O(n) time, O(n) space
def has_duplicate_fast(arr):
    seen = set()
    for v in arr:
        if v in seen:
            return True
        seen.add(v)
    return False

arr = [1, 2, 3, 4, 5, 3]
print("Brute force:", has_duplicate_slow(arr))  # True
print("Hash set:", has_duplicate_fast(arr))      # True
```

**What it does:** Demonstrates the classic time-space trade-off — using a hash set (O(n) extra space) to reduce time from O(n²) to O(n).

---

### Example 3: Measuring Space — In-Place vs Extra Array

#### Go

```go
package main

import "fmt"

// O(n) space — creates a new array
func reverseWithCopy(arr []int) []int {
    n := len(arr)
    result := make([]int, n) // O(n) auxiliary space
    for i := 0; i < n; i++ {
        result[i] = arr[n-1-i]
    }
    return result
}

// O(1) space — reverses in-place
func reverseInPlace(arr []int) {
    left, right := 0, len(arr)-1
    for left < right {
        arr[left], arr[right] = arr[right], arr[left] // O(1) auxiliary space
        left++
        right--
    }
}

func main() {
    arr1 := []int{1, 2, 3, 4, 5}
    fmt.Println("Copy reverse:", reverseWithCopy(arr1))

    arr2 := []int{1, 2, 3, 4, 5}
    reverseInPlace(arr2)
    fmt.Println("In-place reverse:", arr2)
}
```

#### Java

```java
import java.util.Arrays;

public class SpaceComplexity {
    // O(n) space — creates a new array
    public static int[] reverseWithCopy(int[] arr) {
        int n = arr.length;
        int[] result = new int[n]; // O(n) auxiliary space
        for (int i = 0; i < n; i++) {
            result[i] = arr[n - 1 - i];
        }
        return result;
    }

    // O(1) space — reverses in-place
    public static void reverseInPlace(int[] arr) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int temp = arr[left]; // O(1) auxiliary space
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }

    public static void main(String[] args) {
        int[] arr1 = {1, 2, 3, 4, 5};
        System.out.println("Copy reverse: " + Arrays.toString(reverseWithCopy(arr1)));

        int[] arr2 = {1, 2, 3, 4, 5};
        reverseInPlace(arr2);
        System.out.println("In-place reverse: " + Arrays.toString(arr2));
    }
}
```

#### Python

```python
# O(n) space — creates a new list
def reverse_with_copy(arr):
    return arr[::-1]  # O(n) auxiliary space

# O(1) space — reverses in-place
def reverse_in_place(arr):
    left, right = 0, len(arr) - 1
    while left < right:
        arr[left], arr[right] = arr[right], arr[left]  # O(1) auxiliary space
        left += 1
        right -= 1

arr1 = [1, 2, 3, 4, 5]
print("Copy reverse:", reverse_with_copy(arr1))

arr2 = [1, 2, 3, 4, 5]
reverse_in_place(arr2)
print("In-place reverse:", arr2)
```

**What it does:** Shows O(n) space (new array) vs O(1) space (in-place swap) for reversing an array. Both are O(n) time.

---

### Example 4: Recursive Space — Call Stack

#### Go

```go
package main

import "fmt"

// O(n) time, O(n) space (call stack)
func sumRecursive(n int) int {
    if n <= 0 {
        return 0
    }
    return n + sumRecursive(n-1) // each call adds a frame to the stack
}

// O(n) time, O(1) space (iterative)
func sumIterative(n int) int {
    total := 0
    for i := 1; i <= n; i++ {
        total += i
    }
    return total
}

func main() {
    fmt.Println("Recursive sum(10):", sumRecursive(10)) // 55
    fmt.Println("Iterative sum(10):", sumIterative(10))  // 55
}
```

#### Java

```java
public class RecursiveSpace {
    // O(n) time, O(n) space (call stack)
    public static int sumRecursive(int n) {
        if (n <= 0) return 0;
        return n + sumRecursive(n - 1);
    }

    // O(n) time, O(1) space (iterative)
    public static int sumIterative(int n) {
        int total = 0;
        for (int i = 1; i <= n; i++) {
            total += i;
        }
        return total;
    }

    public static void main(String[] args) {
        System.out.println("Recursive sum(10): " + sumRecursive(10));
        System.out.println("Iterative sum(10): " + sumIterative(10));
    }
}
```

#### Python

```python
import sys

# O(n) time, O(n) space (call stack)
def sum_recursive(n):
    if n <= 0:
        return 0
    return n + sum_recursive(n - 1)

# O(n) time, O(1) space (iterative)
def sum_iterative(n):
    total = 0
    for i in range(1, n + 1):
        total += i
    return total

sys.setrecursionlimit(10000)
print("Recursive sum(10):", sum_recursive(10))   # 55
print("Iterative sum(10):", sum_iterative(10))    # 55
```

**What it does:** Shows that recursion uses O(n) stack space even though no explicit data structures are created. The iterative version uses O(1) space.

---

## Coding Patterns

### Pattern 1: Counting Operations to Determine Time Complexity

**Intent:** Analyze time complexity by identifying loops and their iteration counts.

#### Go

```go
// Single loop → O(n)
func singleLoop(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        count++ // runs n times
    }
    return count
}

// Nested loop → O(n²)
func nestedLoop(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        for j := 0; j < n; j++ {
            count++ // runs n × n times
        }
    }
    return count
}

// Loop with halving → O(log n)
func halvingLoop(n int) int {
    count := 0
    for i := n; i > 0; i /= 2 {
        count++ // runs log₂(n) times
    }
    return count
}
```

#### Java

```java
public class Patterns {
    static int singleLoop(int n) {
        int count = 0;
        for (int i = 0; i < n; i++) count++;
        return count;  // O(n)
    }

    static int nestedLoop(int n) {
        int count = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) count++;
        return count;  // O(n²)
    }

    static int halvingLoop(int n) {
        int count = 0;
        for (int i = n; i > 0; i /= 2) count++;
        return count;  // O(log n)
    }
}
```

#### Python

```python
def single_loop(n):
    count = 0
    for _ in range(n):
        count += 1
    return count  # O(n)

def nested_loop(n):
    count = 0
    for _ in range(n):
        for _ in range(n):
            count += 1
    return count  # O(n²)

def halving_loop(n):
    count = 0
    i = n
    while i > 0:
        count += 1
        i //= 2
    return count  # O(log n)
```

### Pattern 2: Tracking Auxiliary Space

**Intent:** Determine space complexity by identifying all memory allocations.

#### Go

```go
// O(1) space — only fixed variables
func maxElement(arr []int) int {
    max := arr[0]
    for _, v := range arr {
        if v > max {
            max = v
        }
    }
    return max
}

// O(n) space — new slice created
func filterPositive(arr []int) []int {
    result := []int{}
    for _, v := range arr {
        if v > 0 {
            result = append(result, v)
        }
    }
    return result
}
```

#### Java

```java
import java.util.ArrayList;
import java.util.List;

public class SpacePatterns {
    // O(1) space
    public static int maxElement(int[] arr) {
        int max = arr[0];
        for (int v : arr) {
            if (v > max) max = v;
        }
        return max;
    }

    // O(n) space
    public static List<Integer> filterPositive(int[] arr) {
        List<Integer> result = new ArrayList<>();
        for (int v : arr) {
            if (v > 0) result.add(v);
        }
        return result;
    }
}
```

#### Python

```python
# O(1) space
def max_element(arr):
    m = arr[0]
    for v in arr:
        if v > m:
            m = v
    return m

# O(n) space
def filter_positive(arr):
    return [v for v in arr if v > 0]
```

---

## Error Handling

| Error | Cause | Fix |
|-------|-------|-----|
| Stack overflow | Deep recursion without base case or with O(n) depth | Convert to iterative or use tail recursion |
| Out of memory | Allocating O(n²) space for large n | Use in-place algorithm or streaming approach |
| Timeout / TLE | O(n²) or worse algorithm on large input | Optimize to O(n log n) or O(n) |
| Integer overflow | Summing large numbers without checking bounds | Use long/int64 or big integer library |

---

## Performance Tips

- Always analyze complexity **before** coding — choosing the right algorithm is more impactful than micro-optimizations
- Use hash maps/sets to reduce O(n²) searches to O(n) — the most common optimization
- Prefer iterative over recursive solutions for large `n` to avoid stack overflow (especially in Python with its default 1000 recursion limit)
- Pre-allocate memory when you know the size: `make([]int, 0, n)` in Go, `new ArrayList<>(n)` in Java, `[None] * n` in Python
- In-place algorithms save memory but may be harder to debug — start with the simple version, optimize if needed

---

## Best Practices

- Write the brute-force solution first, then optimize — correct code first, fast code second
- Always state the time AND space complexity of your solution
- Test with small inputs first, then scale up to verify your complexity analysis
- When choosing between time and space, consider the constraints: competitive programming usually has generous memory but strict time limits
- Document trade-offs in comments: `// O(n) space trade-off for O(n) time instead of O(n²)`

---

## Edge Cases & Pitfalls

- **Empty input (n=0):** Make sure your algorithm handles arrays of length 0 without crashing
- **Single element (n=1):** Loops may not execute, off-by-one errors are common
- **Very large n:** O(n²) becomes unusable around n=10⁴-10⁵; O(2ⁿ) is unusable past n≈25
- **Recursion depth:** Python limits recursion to ~1000 by default; Go and Java have larger stacks but still finite
- **Hidden space costs:** Strings are immutable in all 3 languages — concatenation in a loop creates O(n) copies, using O(n²) total space

---

## Common Mistakes

| Mistake | Why It's Wrong | Correct Approach |
|---------|---------------|-----------------|
| Ignoring space complexity | Memory limits exist in real systems | Always analyze both time and space |
| Confusing O(n) with actual time | O(n) with large constant can be slower than O(n²) for small n | Big-O matters for large n; profile for small n |
| Forgetting recursion stack space | Each recursive call uses O(1) stack space, n calls = O(n) | Count recursion depth as space |
| Saying "O(2n) is different from O(n)" | Constants are dropped in Big-O | O(2n) = O(n) — they have the same growth rate |
| Optimizing before profiling | You might optimize the wrong part | Measure first, then optimize the bottleneck |

---

## Cheat Sheet

| Algorithm Pattern | Time | Space | Example |
|-------------------|------|-------|---------|
| Single loop | O(n) | O(1) | Find max, linear search |
| Nested loops | O(n²) | O(1) | Bubble sort, brute force pairs |
| Loop with halving | O(log n) | O(1) | Binary search (iterative) |
| Sort then scan | O(n log n) | O(n) | Find duplicates via sorting |
| Hash map lookup | O(n) | O(n) | Two sum, frequency count |
| Recursion (linear) | O(n) | O(n) | Factorial, sum of list |
| Recursion (branching) | O(2ⁿ) | O(n) | Naive Fibonacci |
| Divide and conquer | O(n log n) | O(n) | Merge sort |

### Quick Decision Guide

```
Need to reduce time from O(n²) to O(n)?
  → Use a hash map/set (costs O(n) space)

Need to reduce space from O(n) to O(1)?
  → Use in-place algorithm (may be harder to implement)

Recursive solution hitting stack limits?
  → Convert to iterative with explicit stack

Algorithm too slow for large n?
  → Check if sorting + binary search helps: O(n log n) < O(n²)
```

---

## Visual Animation

> See [`animation.html`](./animation.html) for an interactive visual animation of Time vs Space Complexity.
>
> The animation demonstrates:
> - Growth curves for different complexity classes (O(1), O(log n), O(n), O(n²), O(2ⁿ))
> - Interactive input size slider to see how operations scale
> - Side-by-side comparison of time vs space usage
> - Step-by-step execution with operation counter

---

## Summary

Time complexity measures how runtime grows with input size; space complexity measures how memory usage grows. You're always balancing these two — hash maps trade O(n) space for faster lookups, in-place algorithms save memory at the cost of complexity. Learn to analyze both before coding, start with brute force, and optimize based on constraints. Big-O gives you the language to compare algorithms and make informed decisions.

---

## Further Reading

- *Introduction to Algorithms* (CLRS) — Chapter 2: Getting Started, Chapter 3: Growth of Functions
- *Grokking Algorithms* by Aditya Bhargava — Chapter 1: Big-O Notation
- Go: `testing.Benchmark` for measuring performance
- Java: JMH (Java Microbenchmark Harness) for accurate benchmarks
- Python: `timeit` module documentation — https://docs.python.org/3/library/timeit.html
- visualgo.net — interactive algorithm visualizations
