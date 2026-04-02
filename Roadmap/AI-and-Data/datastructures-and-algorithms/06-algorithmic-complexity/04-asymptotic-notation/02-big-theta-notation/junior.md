# Big-Theta Notation -- Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What Is Big-Theta?](#what-is-big-theta)
3. [Big-O vs Big-Theta: The Key Difference](#big-o-vs-big-theta)
4. [Real-World Analogies](#real-world-analogies)
5. [Common Big-Theta Examples](#common-big-theta-examples)
6. [Simple Loop Analysis](#simple-loop-analysis)
7. [Linear Search Worst Case](#linear-search-worst-case)
8. [Merge Sort: Always Theta(n log n)](#merge-sort-always-theta-n-log-n)
9. [When to Use Theta vs O](#when-to-use-theta-vs-o)
10. [Code Examples](#code-examples)
11. [Common Misconceptions](#common-misconceptions)
12. [Quick Reference Table](#quick-reference-table)
13. [Practice Problems](#practice-problems)
14. [Summary](#summary)

---

## Introduction

When we analyze algorithms, we often want to describe exactly how fast they grow
-- not just an upper bound, but a **tight** description. That is what Big-Theta
(Big-Theta) notation gives us.

If Big-O says "this algorithm is **at most** this fast," then Big-Theta says
"this algorithm is **exactly** this fast" (in terms of growth rate).

Think of it this way:
- **Big-O** is like saying "I will arrive in **at most** 30 minutes."
- **Big-Theta** is like saying "I will arrive in **exactly about** 30 minutes."

---

## What Is Big-Theta?

Big-Theta notation describes the **tight bound** of an algorithm's growth rate.
When we say an algorithm is Theta(n), we mean:

- It grows **at least** as fast as n (lower bound)
- It grows **at most** as fast as n (upper bound)
- Combined: it grows **exactly** at the rate of n

In simpler terms, Theta(f(n)) means the algorithm's running time is "sandwiched"
between two constant multiples of f(n) for large inputs.

### The Sandwich Analogy

Imagine a sandwich:
- The **top bread** = upper bound (Big-O)
- The **bottom bread** = lower bound (Big-Omega)
- The **filling** = the actual function

Big-Theta says: the filling always stays between the two slices of bread.

```
        ^
  Time  |      / c2 * g(n)  (upper bound)
        |     /
        |    /  <-- f(n) is sandwiched here
        |   /
        |  / c1 * g(n)  (lower bound)
        | /
        +-------------------->
                  n
```

---

## Big-O vs Big-Theta: The Key Difference

This is the single most important distinction to understand at this level.

### Big-O (Upper Bound Only)

Big-O gives you a **ceiling**. It says: "The algorithm will never be worse than
this." But it could be much better.

Example: Linear search is O(n). But it is also technically O(n^2), O(n^3), and
O(2^n). These are all valid upper bounds, even if they are not tight.

### Big-Theta (Tight Bound)

Big-Theta gives you the **exact** growth rate. It says: "The algorithm grows at
exactly this rate." It is both an upper and lower bound simultaneously.

Example: A simple loop that visits every element is Theta(n). It is not
Theta(n^2) because that would be too high, and not Theta(1) because that would
be too low.

### Side-by-Side Comparison

| Aspect               | Big-O                  | Big-Theta              |
|----------------------|------------------------|------------------------|
| Meaning              | At most (upper bound)  | Exactly (tight bound)  |
| Direction            | One-sided (ceiling)    | Two-sided (sandwich)   |
| "3n + 5 is ..."     | O(n), O(n^2), O(n^3)  | Theta(n) only          |
| Looseness            | Can be very loose      | Always tight           |
| Common usage         | Very common            | More precise contexts  |
| Everyday analogy     | "At most 30 min"       | "About 30 min"         |

---

## Real-World Analogies

### Analogy 1: Salary Range

- **Big-O**: "You will earn at most $100,000." (Could be $20,000.)
- **Big-Omega**: "You will earn at least $60,000."
- **Big-Theta**: "You will earn between $60,000 and $100,000." (Tight range.)

### Analogy 2: Travel Time

- **Big-O**: "The trip takes at most 2 hours." (Could be 15 minutes.)
- **Big-Omega**: "The trip takes at least 1.5 hours."
- **Big-Theta**: "The trip takes about 1.5 to 2 hours." (Tight estimate.)

### Analogy 3: Box Size

If you are shipping a product:
- **Big-O** = the biggest box it could ever need
- **Big-Omega** = the smallest box that could ever hold it
- **Big-Theta** = the right-sized box (snug fit)

---

## Common Big-Theta Examples

| Algorithm / Operation    | Big-Theta         | Why                                     |
|--------------------------|-------------------|-----------------------------------------|
| Accessing array element  | Theta(1)          | Direct index, always constant           |
| Simple for loop (n)      | Theta(n)          | Always visits n elements                |
| Nested loops (n x n)     | Theta(n^2)        | Always visits n*n pairs                 |
| Merge sort               | Theta(n log n)    | Always divides and merges               |
| Binary search (worst)    | Theta(log n)      | Halves search space each time           |
| Matrix multiplication    | Theta(n^3)        | Three nested loops (naive)              |

---

## Simple Loop Analysis

The most basic example: a single loop that iterates through all elements.

```
for i = 0 to n-1:
    do_something()
```

- The loop runs **exactly** n times
- It cannot run fewer than n times
- It cannot run more than n times
- Therefore: **Theta(n)**

This is a tight bound because the algorithm always does exactly n operations,
regardless of the input values.

### Nested Loop Example

```
for i = 0 to n-1:
    for j = 0 to n-1:
        do_something()
```

- The inner loop runs n times for each of the n outer iterations
- Total operations: exactly n * n = n^2
- Therefore: **Theta(n^2)**

---

## Linear Search Worst Case

Linear search scans through an array looking for a target value.

**Worst case**: The element is at the last position or not present at all.

- We must check all n elements
- We cannot avoid checking fewer than n elements (in the worst case)
- Therefore worst case is: **Theta(n)**

**Important note**: The *best case* of linear search is Theta(1) (element found
at position 0). This is why we often specify "worst case" when giving Theta
bounds. The worst case of linear search is both O(n) and Omega(n), therefore
it is Theta(n).

---

## Merge Sort: Always Theta(n log n)

Merge sort is a classic example of an algorithm with the **same** time
complexity in all cases:

- **Best case**: Theta(n log n)
- **Average case**: Theta(n log n)
- **Worst case**: Theta(n log n)

Why? Because merge sort always:
1. Divides the array in half (log n levels)
2. Merges all elements at each level (n work per level)
3. Total: n * log n operations, always

This makes merge sort a great example for Big-Theta: we can say merge sort IS
Theta(n log n) without specifying a case.

Compare this with quicksort:
- Best/average: O(n log n)
- Worst: O(n^2)
- We CANNOT say quicksort is Theta(n log n) for all cases

---

## When to Use Theta vs O

### Use Big-Theta when:

1. The algorithm has the **same complexity** in all cases
   - Merge sort: always Theta(n log n)
   - Simple loop: always Theta(n)

2. You want to be **precise** about growth rate
   - "This function is Theta(n^2)" is more informative than "This function is O(n^2)"

3. Discussing **specific cases**
   - "Linear search worst case is Theta(n)"

### Use Big-O when:

1. You want a **general upper bound**
   - "This algorithm is O(n^2) in the worst case"

2. The algorithm has **different complexities** for different cases
   - Quicksort: O(n^2) worst, O(n log n) average

3. In **everyday conversation** (Big-O is more commonly used in practice)

### Rule of Thumb

If someone asks "What is the complexity?" and the answer is the same for all
cases, prefer Theta. If it varies by case, use O for the worst case.

---

## Code Examples

### Example 1: Simple Sum -- Theta(n)

**Go:**

```go
package main

import "fmt"

// sumArray computes the sum of all elements
// Time: Theta(n) -- always visits every element exactly once
// Space: Theta(1) -- only uses a single accumulator
func sumArray(arr []int) int {
    total := 0
    for _, val := range arr {
        total += val
    }
    return total
}

func main() {
    data := []int{3, 7, 1, 9, 4, 6, 2, 8, 5}
    fmt.Printf("Sum = %d\n", sumArray(data))
    // The loop ALWAYS runs n times regardless of values
    // Lower bound: n iterations (must visit every element)
    // Upper bound: n iterations (visits every element once)
    // Therefore: Theta(n)
}
```

**Java:**

```java
public class SimpleSum {

    /**
     * Computes the sum of all elements.
     * Time: Theta(n) -- always visits every element exactly once.
     * Space: Theta(1) -- only uses a single accumulator.
     */
    public static int sumArray(int[] arr) {
        int total = 0;
        for (int val : arr) {
            total += val;
        }
        return total;
    }

    public static void main(String[] args) {
        int[] data = {3, 7, 1, 9, 4, 6, 2, 8, 5};
        System.out.printf("Sum = %d%n", sumArray(data));
        // The loop ALWAYS runs n times regardless of values
        // Lower bound: n iterations (must visit every element)
        // Upper bound: n iterations (visits every element once)
        // Therefore: Theta(n)
    }
}
```

**Python:**

```python
def sum_array(arr: list[int]) -> int:
    """
    Computes the sum of all elements.
    Time: Theta(n) -- always visits every element exactly once.
    Space: Theta(1) -- only uses a single accumulator.
    """
    total = 0
    for val in arr:
        total += val
    return total


if __name__ == "__main__":
    data = [3, 7, 1, 9, 4, 6, 2, 8, 5]
    print(f"Sum = {sum_array(data)}")
    # The loop ALWAYS runs n times regardless of values
    # Lower bound: n iterations (must visit every element)
    # Upper bound: n iterations (visits every element once)
    # Therefore: Theta(n)
```

---

### Example 2: Nested Loop -- Theta(n^2)

**Go:**

```go
package main

import "fmt"

// printAllPairs prints every pair (i, j)
// Time: Theta(n^2) -- two nested loops, each runs n times
// Space: Theta(1) -- no extra data structures
func printAllPairs(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        for j := 0; j < n; j++ {
            count++
        }
    }
    return count
}

func main() {
    n := 100
    pairs := printAllPairs(n)
    fmt.Printf("Total pairs for n=%d: %d\n", n, pairs)
    // Always exactly n*n iterations
    // Cannot be fewer, cannot be more
    // Theta(n^2)
}
```

**Java:**

```java
public class NestedLoop {

    /**
     * Counts every pair (i, j).
     * Time: Theta(n^2) -- two nested loops, each runs n times.
     * Space: Theta(1) -- no extra data structures.
     */
    public static int printAllPairs(int n) {
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int n = 100;
        int pairs = printAllPairs(n);
        System.out.printf("Total pairs for n=%d: %d%n", n, pairs);
        // Always exactly n*n iterations
        // Cannot be fewer, cannot be more
        // Theta(n^2)
    }
}
```

**Python:**

```python
def print_all_pairs(n: int) -> int:
    """
    Counts every pair (i, j).
    Time: Theta(n^2) -- two nested loops, each runs n times.
    Space: Theta(1) -- no extra data structures.
    """
    count = 0
    for i in range(n):
        for j in range(n):
            count += 1
    return count


if __name__ == "__main__":
    n = 100
    pairs = print_all_pairs(n)
    print(f"Total pairs for n={n}: {pairs}")
    # Always exactly n*n iterations
    # Cannot be fewer, cannot be more
    # Theta(n^2)
```

---

### Example 3: Merge Sort -- Theta(n log n)

**Go:**

```go
package main

import "fmt"

// mergeSort sorts a slice using the merge sort algorithm
// Time: Theta(n log n) -- always divides and merges, regardless of input
// Space: Theta(n) -- temporary arrays for merging
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

func main() {
    data := []int{38, 27, 43, 3, 9, 82, 10}
    sorted := mergeSort(data)
    fmt.Println("Sorted:", sorted)
    // Merge sort ALWAYS:
    //   - divides into log(n) levels
    //   - does n work at each level (merging)
    //   - total: n * log(n)
    // Best = Average = Worst = Theta(n log n)
}
```

**Java:**

```java
import java.util.Arrays;

public class MergeSort {

    /**
     * Sorts an array using merge sort.
     * Time: Theta(n log n) -- always divides and merges.
     * Space: Theta(n) -- temporary arrays for merging.
     */
    public static int[] mergeSort(int[] arr) {
        if (arr.length <= 1) return arr;
        int mid = arr.length / 2;
        int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));
        int[] right = mergeSort(Arrays.copyOfRange(arr, mid, arr.length));
        return merge(left, right);
    }

    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }
        while (i < left.length) result[k++] = left[i++];
        while (j < right.length) result[k++] = right[j++];
        return result;
    }

    public static void main(String[] args) {
        int[] data = {38, 27, 43, 3, 9, 82, 10};
        int[] sorted = mergeSort(data);
        System.out.println("Sorted: " + Arrays.toString(sorted));
        // Merge sort ALWAYS:
        //   - divides into log(n) levels
        //   - does n work at each level (merging)
        //   - total: n * log(n)
        // Best = Average = Worst = Theta(n log n)
    }
}
```

**Python:**

```python
def merge_sort(arr: list[int]) -> list[int]:
    """
    Sorts a list using merge sort.
    Time: Theta(n log n) -- always divides and merges.
    Space: Theta(n) -- temporary lists for merging.
    """
    if len(arr) <= 1:
        return arr
    mid = len(arr) // 2
    left = merge_sort(arr[:mid])
    right = merge_sort(arr[mid:])
    return merge(left, right)


def merge(left: list[int], right: list[int]) -> list[int]:
    result = []
    i = j = 0
    while i < len(left) and j < len(right):
        if left[i] <= right[j]:
            result.append(left[i])
            i += 1
        else:
            result.append(right[j])
            j += 1
    result.extend(left[i:])
    result.extend(right[j:])
    return result


if __name__ == "__main__":
    data = [38, 27, 43, 3, 9, 82, 10]
    sorted_data = merge_sort(data)
    print(f"Sorted: {sorted_data}")
    # Merge sort ALWAYS:
    #   - divides into log(n) levels
    #   - does n work at each level (merging)
    #   - total: n * log(n)
    # Best = Average = Worst = Theta(n log n)
```

---

## Common Misconceptions

### Misconception 1: "Big-O and Big-Theta are the same thing"

**Wrong.** Big-O is an upper bound. Big-Theta is a tight bound.

- 3n is O(n^2) -- TRUE (valid upper bound, but not tight)
- 3n is Theta(n^2) -- FALSE (n^2 grows much faster than 3n)
- 3n is Theta(n) -- TRUE (tight bound)

### Misconception 2: "Big-Theta means average case"

**Wrong.** Big-Theta can describe any case (best, average, worst). It means
the bound is tight for whatever case you are analyzing.

- Linear search best case is Theta(1)
- Linear search worst case is Theta(n)
- Both use Theta, but for different cases

### Misconception 3: "Every algorithm has a Big-Theta"

**Mostly true, but nuanced.** For a specific case (like worst case), yes.
But we cannot always assign a single Theta to an algorithm overall if its best
and worst cases differ.

- Merge sort: Theta(n log n) overall (all cases same)
- Insertion sort: cannot say a single Theta (best is Theta(n), worst is Theta(n^2))

### Misconception 4: "If f(n) is O(g(n)), then f(n) is Theta(g(n))"

**Wrong.** O is necessary but not sufficient for Theta.

- n is O(n^2) -- TRUE
- n is Theta(n^2) -- FALSE (also needs lower bound Omega(n^2))

---

## Quick Reference Table

| Function f(n)     | Big-O       | Big-Omega   | Big-Theta    |
|-------------------|-------------|-------------|--------------|
| 5n + 3            | O(n)        | Omega(n)    | Theta(n)     |
| 2n^2 + 10n        | O(n^2)      | Omega(n^2)  | Theta(n^2)   |
| 7                 | O(1)        | Omega(1)    | Theta(1)     |
| 3n log n + n      | O(n log n)  | Omega(n log n)| Theta(n log n)|
| n^3 + 100n^2      | O(n^3)      | Omega(n^3)  | Theta(n^3)   |

**Key insight**: If O and Omega match, then Theta equals them. Big-Theta =
Big-O AND Big-Omega combined (when they agree).

---

## Practice Problems

### Problem 1
What is the Big-Theta of this code?

```
for i = 0 to n-1:
    print(i)
```

**Answer**: Theta(n). The loop always runs exactly n times.

### Problem 2
What is the Big-Theta of this code?

```
for i = 0 to n-1:
    for j = 0 to i:
        print(i, j)
```

**Answer**: Theta(n^2). The total iterations are 1 + 2 + 3 + ... + n =
n(n+1)/2, which is Theta(n^2).

### Problem 3
Is it correct to say "binary search is Theta(log n)"?

**Answer**: Only for the worst case. Binary search best case is Theta(1) (found
at middle immediately). We should say "binary search worst case is Theta(log n)."

### Problem 4
True or False: 2n + 100 is Theta(n).

**Answer**: True. For large n, 2n + 100 grows at the same rate as n. We can
find constants c1 = 1, c2 = 3 such that c1*n <= 2n+100 <= c2*n for large n.

### Problem 5
Can we say quicksort is Theta(n log n)?

**Answer**: Not in general. Quicksort worst case is Theta(n^2), while its
average case is Theta(n log n). Since best and worst differ, we cannot give a
single Theta for all cases.

---

## Summary

1. **Big-Theta = tight bound** = upper bound AND lower bound together
2. **Big-O = upper bound only** = "at most this fast"
3. **Big-Theta is more precise** than Big-O
4. Use Theta when the algorithm has the **same growth** in all cases (like merge sort)
5. Use O when you want a **general worst-case guarantee**
6. **Theta(f(n))** means the function grows at **exactly** the rate of f(n)
7. If Big-O and Big-Omega of a function agree, then Big-Theta equals both

**The Sandwich Rule**: f(n) is Theta(g(n)) when f(n) is forever sandwiched
between c1*g(n) and c2*g(n) for some positive constants c1 and c2.

---

*Next: Continue to the [Middle Level](middle.md) for formal definitions and proofs.*
