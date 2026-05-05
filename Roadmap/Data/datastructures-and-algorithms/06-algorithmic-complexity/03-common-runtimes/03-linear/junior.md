# Linear Time O(n) — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What is Linear Time?](#what-is-linear-time)
3. [Real-World Analogies](#real-world-analogies)
4. [Why O(n) Matters](#why-on-matters)
5. [Common Linear Time Operations](#common-linear-time-operations)
   - [Linear Search](#linear-search)
   - [Finding Maximum and Minimum](#finding-maximum-and-minimum)
   - [Counting Elements](#counting-elements)
   - [Sum of Elements](#sum-of-elements)
   - [Single Pass Algorithms](#single-pass-algorithms)
6. [When O(n) is Optimal](#when-on-is-optimal)
7. [Comparing O(n) with Other Complexities](#comparing-on-with-other-complexities)
8. [Common Mistakes](#common-mistakes)
9. [Key Takeaways](#key-takeaways)
10. [References](#references)

---

## Introduction

Linear time complexity, denoted **O(n)**, is one of the most fundamental and frequently encountered time complexities in computer science. An algorithm runs in linear time when its execution time grows directly proportional to the size of the input. If you double the input size, the algorithm takes roughly twice as long.

Understanding O(n) is essential because many of the most practical and efficient algorithms operate in linear time, and in many cases, linear time is the **best possible** performance you can achieve.

---

## What is Linear Time?

An algorithm has **O(n)** time complexity when:

- It processes each element of the input **exactly once** (or a constant number of times).
- The total number of operations is proportional to `n`, the size of the input.
- Doubling the input size doubles the running time.

**Mathematical definition:**

```
T(n) = c * n + d
```

Where `c` and `d` are constants. In Big-O notation, we drop constants and lower-order terms:

```
T(n) = O(n)
```

**Growth table:**

| Input Size (n) | Operations (approx.) | Time (at 1 billion ops/sec) |
|----------------|---------------------|-----------------------------|
| 10             | 10                  | 10 nanoseconds              |
| 100            | 100                 | 100 nanoseconds             |
| 1,000          | 1,000               | 1 microsecond               |
| 1,000,000      | 1,000,000           | 1 millisecond               |
| 1,000,000,000  | 1,000,000,000       | 1 second                    |

---

## Real-World Analogies

### Reading a Book Page by Page

Imagine you have a book with `n` pages. To read the entire book, you must look at every single page — there is no shortcut. If the book has 200 pages, it takes twice as long as a 100-page book. This is linear time: the work scales directly with the input size.

### Counting People in a Line

You are asked to count the number of people standing in a queue. You must point at each person one by one: "1, 2, 3, ..." If there are `n` people, you perform exactly `n` counting operations. Double the people, double the counting time.

### Searching for a Name on a Guest List (Unsorted)

You have a guest list that is not sorted alphabetically. To find a specific name, you must check each entry from top to bottom. In the worst case, you check all `n` entries. This is linear search.

### Checking Every Locker

A school has `n` lockers. The janitor must check each locker to see if it is locked. There is no way to skip lockers — each one must be inspected. The total work is proportional to `n`.

### Distributing Flyers

You must hand a flyer to every person in a crowd of `n` people. You walk up to each person and give them one flyer. The work is exactly `n` handoffs.

---

## Why O(n) Matters

1. **Often the best possible:** If an algorithm must examine every element of the input at least once (e.g., finding the maximum in an unsorted array), O(n) is the theoretical lower bound.

2. **Practical and fast:** Linear algorithms scale well. Processing 1 million items takes about 1 millisecond on modern hardware.

3. **Foundation for optimization:** Many algorithms that seem to require O(n^2) can be optimized to O(n) with the right technique (e.g., two-pointer, hash maps).

4. **Predictable performance:** The running time grows proportionally, making it easy to estimate how long an algorithm will take for a given input size.

---

## Common Linear Time Operations

### Linear Search

Linear search checks each element of an array sequentially until the target is found or the array is exhausted.

**Go:**

```go
package main

import "fmt"

// linearSearch returns the index of target in arr, or -1 if not found.
func linearSearch(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i
        }
    }
    return -1
}

func main() {
    arr := []int{4, 2, 7, 1, 9, 3, 6}
    target := 9

    index := linearSearch(arr, target)
    if index != -1 {
        fmt.Printf("Found %d at index %d\n", target, index)
    } else {
        fmt.Printf("%d not found in the array\n", target)
    }
}
```

**Java:**

```java
public class LinearSearch {

    /**
     * Returns the index of target in arr, or -1 if not found.
     * Time Complexity: O(n) — scans each element at most once.
     */
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {4, 2, 7, 1, 9, 3, 6};
        int target = 9;

        int index = linearSearch(arr, target);
        if (index != -1) {
            System.out.printf("Found %d at index %d%n", target, index);
        } else {
            System.out.printf("%d not found in the array%n", target);
        }
    }
}
```

**Python:**

```python
def linear_search(arr: list[int], target: int) -> int:
    """
    Returns the index of target in arr, or -1 if not found.
    Time Complexity: O(n) — scans each element at most once.
    """
    for i, value in enumerate(arr):
        if value == target:
            return i
    return -1


if __name__ == "__main__":
    arr = [4, 2, 7, 1, 9, 3, 6]
    target = 9

    index = linear_search(arr, target)
    if index != -1:
        print(f"Found {target} at index {index}")
    else:
        print(f"{target} not found in the array")
```

**Analysis:**

- **Best case:** O(1) — target is at the first position.
- **Worst case:** O(n) — target is at the last position or not in the array.
- **Average case:** O(n/2) = O(n) — on average, we check half the elements.

---

### Finding Maximum and Minimum

To find the largest or smallest element in an unsorted array, you must look at every element.

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

// findMax returns the maximum value in a non-empty slice.
func findMax(arr []int) int {
    max := math.MinInt64
    for _, v := range arr {
        if v > max {
            max = v
        }
    }
    return max
}

// findMin returns the minimum value in a non-empty slice.
func findMin(arr []int) int {
    min := math.MaxInt64
    for _, v := range arr {
        if v < min {
            min = v
        }
    }
    return min
}

func main() {
    arr := []int{3, 7, 2, 9, 1, 5, 8}
    fmt.Printf("Array: %v\n", arr)
    fmt.Printf("Maximum: %d\n", findMax(arr))
    fmt.Printf("Minimum: %d\n", findMin(arr))
}
```

**Java:**

```java
public class FindMaxMin {

    public static int findMax(int[] arr) {
        int max = Integer.MIN_VALUE;
        for (int value : arr) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static int findMin(int[] arr) {
        int min = Integer.MAX_VALUE;
        for (int value : arr) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static void main(String[] args) {
        int[] arr = {3, 7, 2, 9, 1, 5, 8};
        System.out.println("Maximum: " + findMax(arr));
        System.out.println("Minimum: " + findMin(arr));
    }
}
```

**Python:**

```python
def find_max(arr: list[int]) -> int:
    """Returns the maximum value in a non-empty list. O(n)."""
    max_val = float("-inf")
    for value in arr:
        if value > max_val:
            max_val = value
    return max_val


def find_min(arr: list[int]) -> int:
    """Returns the minimum value in a non-empty list. O(n)."""
    min_val = float("inf")
    for value in arr:
        if value < min_val:
            min_val = value
    return min_val


if __name__ == "__main__":
    arr = [3, 7, 2, 9, 1, 5, 8]
    print(f"Maximum: {find_max(arr)}")
    print(f"Minimum: {find_min(arr)}")
```

**Why O(n) is optimal here:** Every element could potentially be the maximum. If you skip even one element, it might be the largest, and your answer would be wrong. Therefore, you must examine all `n` elements — the lower bound is Omega(n).

---

### Counting Elements

Counting how many elements satisfy a condition requires examining each element.

**Go:**

```go
package main

import "fmt"

// countEven returns the number of even integers in the slice.
func countEven(arr []int) int {
    count := 0
    for _, v := range arr {
        if v%2 == 0 {
            count++
        }
    }
    return count
}

// countOccurrences counts how many times target appears in arr.
func countOccurrences(arr []int, target int) int {
    count := 0
    for _, v := range arr {
        if v == target {
            count++
        }
    }
    return count
}

func main() {
    arr := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    fmt.Printf("Even numbers: %d\n", countEven(arr))
    fmt.Printf("Occurrences of 5: %d\n", countOccurrences(arr, 5))
}
```

**Java:**

```java
public class CountElements {

    public static int countEven(int[] arr) {
        int count = 0;
        for (int value : arr) {
            if (value % 2 == 0) {
                count++;
            }
        }
        return count;
    }

    public static int countOccurrences(int[] arr, int target) {
        int count = 0;
        for (int value : arr) {
            if (value == target) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("Even numbers: " + countEven(arr));
        System.out.println("Occurrences of 5: " + countOccurrences(arr, 5));
    }
}
```

**Python:**

```python
def count_even(arr: list[int]) -> int:
    """Counts even numbers in the list. O(n)."""
    count = 0
    for value in arr:
        if value % 2 == 0:
            count += 1
    return count


def count_occurrences(arr: list[int], target: int) -> int:
    """Counts occurrences of target in the list. O(n)."""
    count = 0
    for value in arr:
        if value == target:
            count += 1
    return count


if __name__ == "__main__":
    arr = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    print(f"Even numbers: {count_even(arr)}")
    print(f"Occurrences of 5: {count_occurrences(arr, 5)}")
```

---

### Sum of Elements

Computing the sum of all elements in an array is a classic O(n) operation.

**Go:**

```go
package main

import "fmt"

// sum returns the sum of all elements in the slice.
func sum(arr []int) int {
    total := 0
    for _, v := range arr {
        total += v
    }
    return total
}

func main() {
    arr := []int{10, 20, 30, 40, 50}
    fmt.Printf("Sum: %d\n", sum(arr))
}
```

**Java:**

```java
public class SumElements {

    public static long sum(int[] arr) {
        long total = 0;
        for (int value : arr) {
            total += value;
        }
        return total;
    }

    public static void main(String[] args) {
        int[] arr = {10, 20, 30, 40, 50};
        System.out.println("Sum: " + sum(arr));
    }
}
```

**Python:**

```python
def array_sum(arr: list[int]) -> int:
    """Returns the sum of all elements. O(n)."""
    total = 0
    for value in arr:
        total += value
    return total


if __name__ == "__main__":
    arr = [10, 20, 30, 40, 50]
    print(f"Sum: {array_sum(arr)}")
    # Python built-in: sum(arr) is also O(n)
```

---

### Single Pass Algorithms

A **single pass** algorithm processes the input in one traversal from start to finish. Many linear-time algorithms use this pattern.

**Example: Find both max and min in a single pass.**

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

// findMaxMin returns both the maximum and minimum in one pass.
func findMaxMin(arr []int) (int, int) {
    if len(arr) == 0 {
        return 0, 0
    }
    max := math.MinInt64
    min := math.MaxInt64
    for _, v := range arr {
        if v > max {
            max = v
        }
        if v < min {
            min = v
        }
    }
    return max, min
}

func main() {
    arr := []int{5, 3, 8, 1, 9, 2, 7}
    max, min := findMaxMin(arr)
    fmt.Printf("Max: %d, Min: %d\n", max, min)
}
```

**Java:**

```java
public class SinglePass {

    public static int[] findMaxMin(int[] arr) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int value : arr) {
            if (value > max) max = value;
            if (value < min) min = value;
        }
        return new int[]{max, min};
    }

    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 1, 9, 2, 7};
        int[] result = findMaxMin(arr);
        System.out.printf("Max: %d, Min: %d%n", result[0], result[1]);
    }
}
```

**Python:**

```python
def find_max_min(arr: list[int]) -> tuple[int, int]:
    """Returns (max, min) in a single pass. O(n)."""
    if not arr:
        return (0, 0)
    max_val = float("-inf")
    min_val = float("inf")
    for value in arr:
        if value > max_val:
            max_val = value
        if value < min_val:
            min_val = value
    return (max_val, min_val)


if __name__ == "__main__":
    arr = [5, 3, 8, 1, 9, 2, 7]
    maximum, minimum = find_max_min(arr)
    print(f"Max: {maximum}, Min: {minimum}")
```

---

## When O(n) is Optimal

O(n) is the **theoretical lower bound** for problems where every element must be examined:

| Problem                        | Why O(n) is the lower bound                        |
|--------------------------------|-----------------------------------------------------|
| Finding max/min in unsorted    | Any skipped element could be the answer              |
| Summing all elements           | Every element contributes to the total               |
| Checking if element exists     | Must verify all elements in the worst case           |
| Counting occurrences           | Every element could match the target                 |
| Reversing an array             | Every element must be moved to its new position      |
| Copying an array               | Every element must be read and written               |

---

## Comparing O(n) with Other Complexities

| Complexity   | n=100     | n=10,000   | n=1,000,000  | Description                |
|-------------|-----------|------------|--------------|----------------------------|
| O(1)        | 1         | 1          | 1            | Constant time              |
| O(log n)    | 7         | 14         | 20           | Logarithmic                |
| **O(n)**    | **100**   | **10,000** | **1,000,000**| **Linear**                 |
| O(n log n)  | 700       | 140,000    | 20,000,000   | Linearithmic               |
| O(n^2)      | 10,000    | 100,000,000| 10^12        | Quadratic                  |

As the table shows, O(n) is significantly faster than O(n log n) and dramatically faster than O(n^2) for large inputs.

---

## Common Mistakes

### Mistake 1: Hidden Nested Loops

```python
# This LOOKS linear but is actually O(n^2)
def has_duplicate(arr):
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):  # nested loop!
            if arr[i] == arr[j]:
                return True
    return False
```

**Fix:** Use a hash set for O(n):

```python
def has_duplicate(arr):
    seen = set()
    for value in arr:
        if value in seen:
            return True
        seen.add(value)
    return False
```

### Mistake 2: String Concatenation in a Loop

```python
# O(n^2) due to string immutability — each += creates a new string
result = ""
for char in characters:
    result += char  # copies entire string each time
```

**Fix:** Use a list and join:

```python
parts = []
for char in characters:
    parts.append(char)
result = "".join(parts)  # O(n) total
```

### Mistake 3: Calling len() or contains() Inside a Loop on a List

```python
# O(n^2) if 'other_list' is a list
for item in my_list:
    if item in other_list:  # O(n) search each time
        process(item)
```

**Fix:** Convert to a set first:

```python
other_set = set(other_list)  # O(n) once
for item in my_list:
    if item in other_set:    # O(1) average lookup
        process(item)
# Total: O(n + m) which is O(n) if m ~ n
```

---

## Key Takeaways

1. **O(n) means the running time grows proportionally with the input size.** Double the input, double the time.

2. **Linear time is often the best achievable** for problems that require examining every element.

3. **Single pass** is the hallmark pattern — process each element once and maintain running state.

4. **Beware hidden quadratic behavior** from nested loops, string concatenation, or linear-time operations inside loops.

5. **O(n) scales well** — even for millions of elements, linear algorithms complete in milliseconds.

6. **Common O(n) operations:** searching, finding extremes, counting, summing, copying, reversing, single-pass aggregations.

---

## References

- Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press. Chapter 2: Getting Started.
- Sedgewick, R., & Wayne, K. (2011). *Algorithms* (4th ed.). Addison-Wesley. Section 1.4: Analysis of Algorithms.
- Skiena, S. S. (2020). *The Algorithm Design Manual* (3rd ed.). Springer. Chapter 2: Algorithm Analysis.
- Knuth, D. E. (1997). *The Art of Computer Programming, Volume 1*. Addison-Wesley.
