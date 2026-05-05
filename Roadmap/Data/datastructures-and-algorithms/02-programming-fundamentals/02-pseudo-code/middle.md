# Pseudo Code — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Deeper Concepts](#deeper-concepts)
3. [Pseudo Code for Complex Algorithms](#pseudo-code-for-complex-algorithms)
4. [Translating Patterns](#translating-patterns)
5. [Complexity Analysis from Pseudo Code](#complexity-analysis-from-pseudo-code)
6. [Code Examples](#code-examples)
7. [Best Practices](#best-practices)
8. [Summary](#summary)

---

## Introduction

> Focus: "Why is pseudo code structured this way?" and "When does pseudo code help most?"

At the middle level, you learn to write pseudo code for complex algorithms (recursion, divide & conquer, dynamic programming) and to analyze time/space complexity directly from pseudo code — before writing a single line of real code.

---

## Deeper Concepts

### Levels of Abstraction

Pseudo code can be written at different abstraction levels:

```text
// HIGH LEVEL — for communicating the idea
SORT the array
FIND the median
RETURN the result

// MEDIUM LEVEL — for planning implementation
FUNCTION findMedian(array)
    SORT array using merge sort
    IF length(array) is odd THEN
        RETURN array[length/2]
    ELSE
        RETURN (array[length/2 - 1] + array[length/2]) / 2
    END IF
END FUNCTION

// LOW LEVEL — for detailed algorithm design (close to code)
FUNCTION mergeSort(array, left, right)
    IF left >= right THEN
        RETURN
    END IF
    SET mid = left + (right - left) / 2
    CALL mergeSort(array, left, mid)
    CALL mergeSort(array, mid + 1, right)
    CALL merge(array, left, mid, right)
END FUNCTION
```

**Rule of thumb:**
- **Interviews:** Medium level — show your thought process
- **Textbooks (CLRS):** Low level — precise enough to implement
- **Team discussions:** High level — communicate the approach

### Recursion in Pseudo Code

```text
FUNCTION factorial(n)
    // Base case
    IF n <= 1 THEN
        RETURN 1
    END IF
    // Recursive case
    RETURN n * CALL factorial(n - 1)
END FUNCTION

// Trace for n=4:
// factorial(4) = 4 * factorial(3)
//              = 4 * 3 * factorial(2)
//              = 4 * 3 * 2 * factorial(1)
//              = 4 * 3 * 2 * 1
//              = 24
```

---

## Pseudo Code for Complex Algorithms

### Binary Search

```text
FUNCTION binarySearch(sortedArray, target)
    SET left = 0
    SET right = length(sortedArray) - 1

    WHILE left <= right DO
        SET mid = left + (right - left) / 2    // avoids overflow

        IF sortedArray[mid] == target THEN
            RETURN mid                          // found!
        ELSE IF sortedArray[mid] < target THEN
            SET left = mid + 1                  // search right half
        ELSE
            SET right = mid - 1                 // search left half
        END IF
    END WHILE

    RETURN -1                                   // not found
END FUNCTION
```

#### Go

```go
func binarySearch(arr []int, target int) int {
    left, right := 0, len(arr)-1
    for left <= right {
        mid := left + (right-left)/2
        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }
    return -1
}
```

#### Java

```java
public static int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left <= right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) left = mid + 1;
        else right = mid - 1;
    }
    return -1;
}
```

#### Python

```python
def binary_search(arr, target):
    left, right = 0, len(arr) - 1
    while left <= right:
        mid = left + (right - left) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1
```

---

### Merge Sort (Divide and Conquer)

```text
FUNCTION mergeSort(array)
    IF length(array) <= 1 THEN
        RETURN array
    END IF

    SET mid = length(array) / 2
    SET left = CALL mergeSort(array[0..mid-1])
    SET right = CALL mergeSort(array[mid..end])
    RETURN CALL merge(left, right)
END FUNCTION

FUNCTION merge(left, right)
    SET result = empty array
    SET i = 0, j = 0

    WHILE i < length(left) AND j < length(right) DO
        IF left[i] <= right[j] THEN
            APPEND left[i] TO result
            SET i = i + 1
        ELSE
            APPEND right[j] TO result
            SET j = j + 1
        END IF
    END WHILE

    APPEND remaining elements of left TO result
    APPEND remaining elements of right TO result
    RETURN result
END FUNCTION
```

#### Go

```go
func mergeSort(arr []int) []int {
    if len(arr) <= 1 {
        return arr
    }
    mid := len(arr) / 2
    left := mergeSort(arr[:mid])
    right := mergeSort(arr[mid:])
    return merge(left, right)
}

func merge(left, right []int) []int {
    result := make([]int, 0, len(left)+len(right))
    i, j := 0, 0
    for i < len(left) && j < len(right) {
        if left[i] <= right[j] {
            result = append(result, left[i])
            i++
        } else {
            result = append(result, right[j])
            j++
        }
    }
    result = append(result, left[i:]...)
    result = append(result, right[j:]...)
    return result
}
```

#### Java

```java
public static int[] mergeSort(int[] arr) {
    if (arr.length <= 1) return arr;
    int mid = arr.length / 2;
    int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));
    int[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length));
    return merge(left, right);
}

public static int[] merge(int[] left, int[] right) {
    int[] result = new int[left.length + right.length];
    int i = 0, j = 0, k = 0;
    while (i < left.length && j < right.length) {
        if (left[i] <= right[j]) result[k++] = left[i++];
        else result[k++] = right[j++];
    }
    while (i < left.length) result[k++] = left[i++];
    while (j < right.length) result[k++] = right[j++];
    return result;
}
```

#### Python

```python
def merge_sort(arr):
    if len(arr) <= 1:
        return arr
    mid = len(arr) // 2
    left = merge_sort(arr[:mid])
    right = merge_sort(arr[mid:])
    return merge(left, right)

def merge(left, right):
    result = []
    i = j = 0
    while i < len(left) and j < len(right):
        if left[i] <= right[j]:
            result.append(left[i]); i += 1
        else:
            result.append(right[j]); j += 1
    result.extend(left[i:])
    result.extend(right[j:])
    return result
```

---

### Dynamic Programming Pattern

```text
// Top-down (memoization)
FUNCTION fib(n, memo)
    IF n IN memo THEN
        RETURN memo[n]
    END IF
    IF n <= 1 THEN
        RETURN n
    END IF
    SET memo[n] = CALL fib(n-1, memo) + CALL fib(n-2, memo)
    RETURN memo[n]
END FUNCTION

// Bottom-up (tabulation)
FUNCTION fib(n)
    SET dp[0] = 0
    SET dp[1] = 1
    FOR i = 2 TO n DO
        SET dp[i] = dp[i-1] + dp[i-2]
    END FOR
    RETURN dp[n]
END FUNCTION
```

---

## Translating Patterns

### Pseudo Code → 3 Languages Mapping

| Pseudo Code | Go | Java | Python |
|------------|-----|------|--------|
| `SET x = 5` | `x := 5` | `int x = 5;` | `x = 5` |
| `IF x > 0 THEN` | `if x > 0 {` | `if (x > 0) {` | `if x > 0:` |
| `ELSE IF` | `} else if {` | `} else if {` | `elif` |
| `END IF` | `}` | `}` | (dedent) |
| `WHILE x > 0 DO` | `for x > 0 {` | `while (x > 0) {` | `while x > 0:` |
| `FOR i = 0 TO n-1 DO` | `for i := 0; i < n; i++ {` | `for (int i = 0; i < n; i++) {` | `for i in range(n):` |
| `FOR each item IN list DO` | `for _, item := range list {` | `for (var item : list) {` | `for item in list:` |
| `FUNCTION f(x)` | `func f(x int) int {` | `public static int f(int x) {` | `def f(x):` |
| `RETURN x` | `return x` | `return x;` | `return x` |
| `PRINT x` | `fmt.Println(x)` | `System.out.println(x);` | `print(x)` |
| `length(array)` | `len(arr)` | `arr.length` | `len(arr)` |
| `APPEND x TO list` | `list = append(list, x)` | `list.add(x)` | `list.append(x)` |

---

## Complexity Analysis from Pseudo Code

You can determine Big-O directly from pseudo code structure:

### Rule 1: Sequential Statements

```text
SET a = 5         // O(1)
SET b = 10        // O(1)
PRINT a + b       // O(1)
// Total: O(1) + O(1) + O(1) = O(1)
```

### Rule 2: Single Loop

```text
FOR i = 0 TO n-1 DO      // runs n times
    PRINT array[i]        // O(1) per iteration
END FOR
// Total: O(n)
```

### Rule 3: Nested Loops

```text
FOR i = 0 TO n-1 DO          // outer: n times
    FOR j = 0 TO n-1 DO      // inner: n times each
        PRINT i, j            // O(1)
    END FOR
END FOR
// Total: O(n) × O(n) = O(n²)
```

### Rule 4: Divide and Conquer (Recursion)

```text
FUNCTION solve(array, left, right)
    IF left >= right THEN RETURN          // base case
    SET mid = (left + right) / 2
    CALL solve(array, left, mid)          // T(n/2)
    CALL solve(array, mid+1, right)       // T(n/2)
    CALL merge(array, left, mid, right)   // O(n)
END FUNCTION

// Recurrence: T(n) = 2T(n/2) + O(n)
// By Master Theorem: T(n) = O(n log n)
```

### Rule 5: Logarithmic Loops

```text
SET i = n
WHILE i > 1 DO
    SET i = i / 2     // halves each time
END WHILE
// Runs log₂(n) times → O(log n)
```

---

## Code Examples

### Example: Two Sum (Hash Map Approach)

#### Pseudo Code

```text
FUNCTION twoSum(array, target)
    SET map = empty hash map

    FOR i = 0 TO length(array) - 1 DO
        SET complement = target - array[i]
        IF complement IN map THEN
            RETURN [map[complement], i]
        END IF
        SET map[array[i]] = i
    END FOR

    RETURN "not found"
END FUNCTION

// Time: O(n) — single pass
// Space: O(n) — hash map stores up to n elements
```

#### Go

```go
func twoSum(nums []int, target int) []int {
    seen := make(map[int]int)
    for i, v := range nums {
        if j, ok := seen[target-v]; ok {
            return []int{j, i}
        }
        seen[v] = i
    }
    return nil
}
```

#### Java

```java
public static int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        seen.put(nums[i], i);
    }
    return new int[]{};
}
```

#### Python

```python
def two_sum(nums, target):
    seen = {}
    for i, v in enumerate(nums):
        complement = target - v
        if complement in seen:
            return [seen[complement], i]
        seen[v] = i
    return []
```

---

## Best Practices

1. **Start high, then refine:** Write a 3-line high-level plan, then expand to detailed pseudo code
2. **Include complexity comments:** Note `// O(n)` next to loops, `// O(n log n)` for recursive divides
3. **Handle edge cases explicitly:** Empty input, single element, duplicates
4. **Use consistent naming:** `array`, `list`, `target`, `result` — not language-specific names
5. **Show the trace:** For recursive algorithms, write out 2-3 call steps to verify correctness
6. **Test with a small example:** Walk through your pseudo code with input `[3, 1, 4]` before coding

---

## Summary

At the middle level, pseudo code becomes a tool for designing complex algorithms (binary search, merge sort, DP) and analyzing their complexity before implementation. The key skill is translating between pseudo code and real code fluently in Go, Java, and Python.
