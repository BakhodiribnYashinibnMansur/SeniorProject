# How to Calculate Complexity? -- Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What Does "Calculating Complexity" Mean?](#what-does-calculating-complexity-mean)
3. [Step 1: Count the Operations](#step-1-count-the-operations)
4. [Step 2: Identify the Dominant Term](#step-2-identify-the-dominant-term)
5. [Step 3: Drop Constants and Lower-Order Terms](#step-3-drop-constants-and-lower-order-terms)
6. [Analyzing Loops](#analyzing-loops)
   - [Single Loop -- O(n)](#single-loop--on)
   - [Nested Loops -- O(n^2)](#nested-loops--on2)
   - [Halving Loop -- O(log n)](#halving-loop--olog-n)
   - [Sequential Loops](#sequential-loops)
7. [Step-by-Step Calculation Method](#step-by-step-calculation-method)
8. [Common Patterns Cheat Sheet](#common-patterns-cheat-sheet)
9. [Practice Examples](#practice-examples)
10. [Key Takeaways](#key-takeaways)

---

## Introduction

Understanding how to **calculate** the time complexity of code is one of the most fundamental skills in computer science. It is not enough to memorize that "sorting is O(n log n)" -- you need to be able to look at any piece of code and derive its complexity from first principles.

This guide teaches you the systematic method for analyzing code, step by step.

---

## What Does "Calculating Complexity" Mean?

When we calculate complexity, we are answering: **How does the number of operations grow as the input size n increases?**

We do not care about:
- The exact number of operations (e.g., "47 operations")
- Constants (e.g., "3n" vs "5n" -- both are O(n))
- Hardware speed

We do care about:
- The **growth rate** as n gets large
- The **worst-case** scenario (unless stated otherwise)

---

## Step 1: Count the Operations

The first step is to go through the code line by line and count how many times each line executes.

### Go

```go
func sumArray(arr []int) int {
    total := 0           // 1 time
    for i := 0; i < len(arr); i++ {  // n times
        total += arr[i]  // n times
    }
    return total          // 1 time
}
// Total operations: 1 + n + n + 1 = 2n + 2
```

### Java

```java
public static int sumArray(int[] arr) {
    int total = 0;                    // 1 time
    for (int i = 0; i < arr.length; i++) {  // n times
        total += arr[i];              // n times
    }
    return total;                     // 1 time
}
// Total operations: 1 + n + n + 1 = 2n + 2
```

### Python

```python
def sum_array(arr):
    total = 0                # 1 time
    for x in arr:            # n times
        total += x           # n times
    return total             # 1 time
# Total operations: 1 + n + n + 1 = 2n + 2
```

In all three cases the operation count is **2n + 2**. In Big-O notation this becomes **O(n)**.

---

## Step 2: Identify the Dominant Term

When you have an expression like `3n^2 + 5n + 100`, the **dominant term** is the one that grows fastest as n increases.

| Expression | Dominant Term | Big-O |
|---|---|---|
| 2n + 2 | n | O(n) |
| 3n^2 + 5n + 100 | n^2 | O(n^2) |
| 4n^3 + 2n^2 + n | n^3 | O(n^3) |
| 5 * 2^n + n^3 | 2^n | O(2^n) |
| 10 log n + n | n | O(n) |

**Rule**: The term with the highest growth rate dominates. The growth rate hierarchy is:

```
1 < log n < sqrt(n) < n < n log n < n^2 < n^3 < 2^n < n!
```

---

## Step 3: Drop Constants and Lower-Order Terms

Big-O notation ignores:
- **Multiplicative constants**: O(3n) = O(n)
- **Additive constants**: O(n + 5) = O(n)
- **Lower-order terms**: O(n^2 + n) = O(n^2)

### Why?

Because for large n, these do not matter:

| n | n^2 | n^2 + 1000n |
|---|---|---|
| 10 | 100 | 10,100 |
| 1,000 | 1,000,000 | 2,000,000 |
| 1,000,000 | 10^12 | 1.001 * 10^12 |

At n = 1,000,000, the "+1000n" term contributes less than 0.1% to the total.

---

## Analyzing Loops

Loops are the primary source of complexity in most code. Let us analyze each common loop pattern.

### Single Loop -- O(n)

A loop that iterates once for each element in the input runs n times.

#### Go

```go
// O(n) -- linear scan
func findMax(arr []int) int {
    max := arr[0]                     // 1 operation
    for i := 1; i < len(arr); i++ {   // runs n-1 times
        if arr[i] > max {             // 1 comparison per iteration
            max = arr[i]              // at most 1 assignment per iteration
        }
    }
    return max                        // 1 operation
}
// Count: 1 + (n-1)*2 + 1 = 2n, so O(n)
```

#### Java

```java
// O(n) -- linear scan
public static int findMax(int[] arr) {
    int max = arr[0];                         // 1 operation
    for (int i = 1; i < arr.length; i++) {    // runs n-1 times
        if (arr[i] > max) {                   // 1 comparison per iteration
            max = arr[i];                     // at most 1 assignment per iteration
        }
    }
    return max;                               // 1 operation
}
// Count: 1 + (n-1)*2 + 1 = 2n, so O(n)
```

#### Python

```python
# O(n) -- linear scan
def find_max(arr):
    max_val = arr[0]          # 1 operation
    for x in arr[1:]:         # runs n-1 times
        if x > max_val:       # 1 comparison per iteration
            max_val = x       # at most 1 assignment per iteration
    return max_val            # 1 operation
# Count: 1 + (n-1)*2 + 1 = 2n, so O(n)
```

**Key insight**: If a loop runs proportional to n, the body executes n times. Multiply the body cost by n.

---

### Nested Loops -- O(n^2)

When one loop is inside another, multiply the iteration counts.

#### Go

```go
// O(n^2) -- checking all pairs
func hasDuplicate(arr []int) bool {
    n := len(arr)
    for i := 0; i < n; i++ {           // outer: n times
        for j := i + 1; j < n; j++ {   // inner: n-i-1 times on average
            if arr[i] == arr[j] {       // 1 comparison
                return true
            }
        }
    }
    return false
}
// Inner loop runs: (n-1) + (n-2) + ... + 1 + 0 = n(n-1)/2
// That is O(n^2)
```

#### Java

```java
// O(n^2) -- checking all pairs
public static boolean hasDuplicate(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n; i++) {               // outer: n times
        for (int j = i + 1; j < n; j++) {       // inner: n-i-1 times on average
            if (arr[i] == arr[j]) {              // 1 comparison
                return true;
            }
        }
    }
    return false;
}
// Inner loop runs: (n-1) + (n-2) + ... + 1 + 0 = n(n-1)/2
// That is O(n^2)
```

#### Python

```python
# O(n^2) -- checking all pairs
def has_duplicate(arr):
    n = len(arr)
    for i in range(n):               # outer: n times
        for j in range(i + 1, n):    # inner: n-i-1 times on average
            if arr[i] == arr[j]:     # 1 comparison
                return True
    return False
# Inner loop runs: (n-1) + (n-2) + ... + 1 + 0 = n(n-1)/2
# That is O(n^2)
```

**Mathematical proof that n(n-1)/2 = O(n^2)**:
```
n(n-1)/2 = (n^2 - n) / 2 = n^2/2 - n/2
```
Drop constants and lower-order terms: O(n^2).

---

### Halving Loop -- O(log n)

A loop where the variable is **halved** (or multiplied) each iteration runs O(log n) times.

#### Go

```go
// O(log n) -- binary search
func binarySearch(arr []int, target int) int {
    low, high := 0, len(arr)-1
    for low <= high {                    // runs log2(n) times
        mid := low + (high-low)/2       // 1 operation
        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            low = mid + 1               // eliminate half
        } else {
            high = mid - 1              // eliminate half
        }
    }
    return -1
}
// Each iteration halves the search space: n -> n/2 -> n/4 -> ... -> 1
// Number of steps: log2(n), so O(log n)
```

#### Java

```java
// O(log n) -- binary search
public static int binarySearch(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    while (low <= high) {                       // runs log2(n) times
        int mid = low + (high - low) / 2;       // 1 operation
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            low = mid + 1;                      // eliminate half
        } else {
            high = mid - 1;                     // eliminate half
        }
    }
    return -1;
}
// Each iteration halves the search space: n -> n/2 -> n/4 -> ... -> 1
// Number of steps: log2(n), so O(log n)
```

#### Python

```python
# O(log n) -- binary search
def binary_search(arr, target):
    low, high = 0, len(arr) - 1
    while low <= high:                      # runs log2(n) times
        mid = low + (high - low) // 2       # 1 operation
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            low = mid + 1                   # eliminate half
        else:
            high = mid - 1                  # eliminate half
    return -1
# Each iteration halves the search space: n -> n/2 -> n/4 -> ... -> 1
# Number of steps: log2(n), so O(log n)
```

**Why log n?** If you start with n and keep dividing by 2:
```
n -> n/2 -> n/4 -> n/8 -> ... -> 1
```
How many steps k does it take? Solve n / 2^k = 1, which gives k = log2(n).

---

### Sequential Loops

When loops are **not nested** (they come one after another), **add** their complexities.

#### Go

```go
func processData(arr []int) {
    // Loop 1: O(n)
    for i := 0; i < len(arr); i++ {
        fmt.Println(arr[i])
    }

    // Loop 2: O(n)
    for i := 0; i < len(arr); i++ {
        arr[i] *= 2
    }

    // Loop 3: O(n^2)
    for i := 0; i < len(arr); i++ {
        for j := 0; j < len(arr); j++ {
            fmt.Println(arr[i] + arr[j])
        }
    }
}
// Total: O(n) + O(n) + O(n^2) = O(n^2)
// The dominant term wins
```

#### Java

```java
public static void processData(int[] arr) {
    // Loop 1: O(n)
    for (int i = 0; i < arr.length; i++) {
        System.out.println(arr[i]);
    }

    // Loop 2: O(n)
    for (int i = 0; i < arr.length; i++) {
        arr[i] *= 2;
    }

    // Loop 3: O(n^2)
    for (int i = 0; i < arr.length; i++) {
        for (int j = 0; j < arr.length; j++) {
            System.out.println(arr[i] + arr[j]);
        }
    }
}
// Total: O(n) + O(n) + O(n^2) = O(n^2)
```

#### Python

```python
def process_data(arr):
    # Loop 1: O(n)
    for x in arr:
        print(x)

    # Loop 2: O(n)
    arr = [x * 2 for x in arr]

    # Loop 3: O(n^2)
    for i in range(len(arr)):
        for j in range(len(arr)):
            print(arr[i] + arr[j])

# Total: O(n) + O(n) + O(n^2) = O(n^2)
```

**Rule**: For sequential sections, take the maximum.

---

## Step-by-Step Calculation Method

Follow these five steps for any code:

### Step 1: Identify Input Size(s)

What is n? It could be:
- Length of an array
- Number of nodes in a tree
- Number of characters in a string
- Multiple inputs (n and m)

### Step 2: Go Line by Line

For each statement, determine:
- How many times does this line execute?
- What is the cost of each execution?

### Step 3: Sum Up the Counts

Write the total as a mathematical expression.

### Step 4: Simplify

- Drop constants
- Drop lower-order terms
- Keep only the dominant term

### Step 5: Express in Big-O

Write the final answer as O(f(n)).

### Full Worked Example

#### Go

```go
func mystery(n int) int {
    result := 0                          // Step 2: executes 1 time
    for i := 0; i < n; i++ {            // Step 2: loop runs n times
        for j := 0; j < n; j++ {        // Step 2: inner loop runs n times per outer iteration
            result += i * j             // Step 2: runs n*n = n^2 times total
        }
    }
    for k := 0; k < n; k++ {            // Step 2: loop runs n times
        result += k                     // Step 2: runs n times
    }
    return result                        // Step 2: executes 1 time
}

// Step 3: Total = 1 + n^2 + n + 1 = n^2 + n + 2
// Step 4: Drop constants and lower terms -> n^2
// Step 5: O(n^2)
```

#### Java

```java
public static int mystery(int n) {
    int result = 0;                              // 1 time
    for (int i = 0; i < n; i++) {                // n times
        for (int j = 0; j < n; j++) {            // n times per outer
            result += i * j;                     // n^2 times total
        }
    }
    for (int k = 0; k < n; k++) {                // n times
        result += k;                             // n times
    }
    return result;                               // 1 time
}
// Total = n^2 + n + 2 -> O(n^2)
```

#### Python

```python
def mystery(n):
    result = 0                       # 1 time
    for i in range(n):               # n times
        for j in range(n):           # n times per outer
            result += i * j          # n^2 times total
    for k in range(n):               # n times
        result += k                  # n times
    return result                    # 1 time
# Total = n^2 + n + 2 -> O(n^2)
```

---

## Common Patterns Cheat Sheet

| Code Pattern | Example | Complexity |
|---|---|---|
| Single statement | `x = x + 1` | O(1) |
| Single loop 0..n | `for i in range(n)` | O(n) |
| Nested loop n*n | Two nested loops over n | O(n^2) |
| Triple nested loop | Three nested loops over n | O(n^3) |
| Halving loop | `while n > 0: n //= 2` | O(log n) |
| Loop * halving | Outer 0..n, inner halves | O(n log n) |
| Two sequential loops | O(n) then O(n) | O(n) |
| Loop over n then n^2 | O(n) then O(n^2) | O(n^2) |

### Additional Patterns

**Loop where variable doubles each time**:

```go
// O(log n)
for i := 1; i < n; i *= 2 {
    // constant work
}
```

**Loop with inner loop depending on outer variable**:

```go
// Sum: 1 + 2 + 3 + ... + n = n(n+1)/2 = O(n^2)
for i := 0; i < n; i++ {
    for j := 0; j < i; j++ {
        // constant work
    }
}
```

**Two nested independent loops with different sizes**:

```go
// O(n * m) -- not O(n^2) unless n == m
for i := 0; i < n; i++ {
    for j := 0; j < m; j++ {
        // constant work
    }
}
```

---

## Practice Examples

### Example 1: What is the complexity?

#### Go

```go
func example1(arr []int) int {
    n := len(arr)
    sum := 0
    for i := 0; i < n; i += 2 {
        sum += arr[i]
    }
    return sum
}
```

**Analysis**: The loop runs n/2 times. Each iteration does O(1) work. Total: n/2 = O(n). Dropping the constant 1/2 still gives **O(n)**.

### Example 2: What is the complexity?

#### Go

```go
func example2(n int) int {
    count := 0
    for i := 1; i < n; i *= 3 {
        count++
    }
    return count
}
```

**Analysis**: i takes values 1, 3, 9, 27, ... until i >= n. The number of steps is log3(n). Since O(log3(n)) = O(log n) (base does not matter in Big-O), this is **O(log n)**.

### Example 3: What is the complexity?

#### Python

```python
def example3(matrix):
    n = len(matrix)
    total = 0
    for i in range(n):
        for j in range(n):
            for k in range(10):
                total += matrix[i][j]
    return total
```

**Analysis**: The outer two loops give n^2 iterations. The innermost loop runs exactly 10 times (a constant, not dependent on n). Total: 10 * n^2 = O(n^2). The constant 10 is dropped.

### Example 4: What is the complexity?

#### Java

```java
public static void example4(int n) {
    for (int i = 0; i < n; i++) {
        for (int j = 1; j < n; j *= 2) {
            System.out.println(i + ", " + j);
        }
    }
}
```

**Analysis**: Outer loop runs n times. Inner loop doubles j each time, running log2(n) times. Total: **O(n log n)**.

---

## Key Takeaways

1. **Count operations** line by line, tracking how many times each executes.
2. **Identify the dominant term** -- the one that grows fastest.
3. **Drop constants and lower-order terms** to get the Big-O class.
4. **Loops are the key**: single loop = O(n), nested = O(n^2), halving = O(log n).
5. **Sequential blocks**: add their complexities, then take the maximum.
6. **Nested blocks**: multiply their complexities.
7. **Constants in loop bounds** (like `for k < 10`) do not add to complexity.
8. **Practice on real code** -- the more you analyze, the faster you get.

---

> **Next**: [Middle Level](middle.md) -- Recurrence relations, Master Theorem, and amortized analysis.
