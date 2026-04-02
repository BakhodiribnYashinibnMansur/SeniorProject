# Linear Time O(n) — Professional Level

## Table of Contents

1. [Overview](#overview)
2. [Proving O(n) Lower Bounds](#proving-on-lower-bounds)
   - [Information-Theoretic Arguments](#information-theoretic-arguments)
   - [Adversary Arguments](#adversary-arguments)
3. [Adversary Argument for Finding Minimum](#adversary-argument-for-finding-minimum)
4. [Linear-Time Selection: Median of Medians](#linear-time-selection)
   - [The Algorithm](#the-algorithm)
   - [Why It Is O(n)](#why-it-is-on)
   - [Implementation](#implementation)
5. [Linear-Time Sorting: Breaking the O(n log n) Barrier](#linear-time-sorting)
   - [The Comparison-Based Lower Bound](#comparison-based-lower-bound)
   - [Counting Sort — O(n + k)](#counting-sort)
   - [Radix Sort — O(d * (n + k))](#radix-sort)
   - [Bucket Sort — O(n) Expected](#bucket-sort)
6. [Linear-Time Algorithms in Theory](#linear-time-algorithms-in-theory)
7. [Key Takeaways](#key-takeaways)
8. [References](#references)

---

## Overview

At the professional level, we go beyond using linear-time algorithms to **proving** that linear time is optimal, understanding the theoretical machinery behind O(n) lower bounds, and studying algorithms that achieve linear time in surprising contexts (selection, sorting with restricted inputs).

---

## Proving O(n) Lower Bounds

### Information-Theoretic Arguments

An information-theoretic lower bound argues that the algorithm must gather enough information to determine the answer, and the only way to gather that information is to examine input elements.

**For computing the sum of n numbers:**

- The answer depends on all n input values.
- Changing any single input value changes the output.
- Therefore, any correct algorithm must read all n inputs.
- Lower bound: Omega(n).

**Formally:** Let f(x_1, ..., x_n) be a function such that for each i, there exist inputs that differ only in position i and produce different outputs. Then computing f requires Omega(n) time.

### Adversary Arguments

An adversary argument constructs a worst-case input adaptively in response to the algorithm's queries. The adversary forces the algorithm to make many queries before the answer is determined.

**Structure of an adversary argument:**

1. Define a set of possible inputs consistent with the algorithm's observations so far.
2. For each query the algorithm makes, the adversary answers in a way that maximizes the remaining ambiguity.
3. Show that after fewer than n queries, the adversary can still change the answer.
4. Conclude that Omega(n) queries are necessary.

---

## Adversary Argument for Finding Minimum

**Theorem:** Any comparison-based algorithm for finding the minimum of n elements requires at least n - 1 comparisons.

**Proof by adversary argument:**

Consider a tournament metaphor. Each comparison between two elements determines a "winner" (smaller element). An element can only be confirmed as the minimum if it has directly or indirectly beaten all other elements.

**Key insight:** Each comparison eliminates at most one element from being the minimum. We start with n candidates. After k comparisons, at least n - k candidates remain. To narrow down to 1 candidate, we need at least n - 1 comparisons.

**More rigorously:**

1. Before any comparisons, all n elements are potential minimums.
2. Each comparison between elements a and b: the loser (larger element) is eliminated as a minimum candidate. At most one element is eliminated per comparison.
3. The minimum is identified only when exactly one candidate remains.
4. Starting with n candidates and eliminating one per comparison: n - 1 comparisons needed.

**This is tight:** Linear scan achieves exactly n - 1 comparisons:

```
min = arr[0]
for i = 1 to n-1:        // n - 1 comparisons
    if arr[i] < min:
        min = arr[i]
```

---

## Linear-Time Selection: Median of Medians

The **selection problem** asks: given an unsorted array and an integer k, find the k-th smallest element.

Naive approach: sort the array in O(n log n) and return arr[k-1]. But we can do better: **O(n) worst case.**

### The Algorithm

The Median of Medians algorithm (also called BFPRT after Blum, Floyd, Pratt, Rivest, Tarjan, 1973):

1. **Divide** the array into groups of 5.
2. **Find the median** of each group (O(1) per group since groups are constant size).
3. **Recursively find the median** of these medians — this is the pivot.
4. **Partition** the array around this pivot.
5. **Recurse** on the appropriate side.

### Why It Is O(n)

The key guarantee: the median of medians is greater than at least 3n/10 elements and less than at least 3n/10 elements. This ensures each recursive call eliminates at least 30% of elements.

**Recurrence:**

```
T(n) = T(n/5) + T(7n/10) + O(n)
```

- T(n/5): finding median of medians recursively.
- T(7n/10): recursing on the larger partition (worst case 70% of elements).
- O(n): partitioning.

Since n/5 + 7n/10 = 9n/10 < n, this solves to **T(n) = O(n)**.

### Implementation

**Go:**

```go
package main

import (
    "fmt"
    "sort"
)

// medianOfMedians selects the k-th smallest element (0-indexed) in O(n) worst case.
func medianOfMedians(arr []int, k int) int {
    if len(arr) <= 5 {
        sort.Ints(arr)
        return arr[k]
    }

    // Step 1: Divide into groups of 5 and find medians
    medians := make([]int, 0, (len(arr)+4)/5)
    for i := 0; i < len(arr); i += 5 {
        end := i + 5
        if end > len(arr) {
            end = len(arr)
        }
        group := make([]int, end-i)
        copy(group, arr[i:end])
        sort.Ints(group)
        medians = append(medians, group[len(group)/2])
    }

    // Step 2: Find median of medians recursively
    pivot := medianOfMedians(medians, len(medians)/2)

    // Step 3: Partition around pivot
    less := []int{}
    equal := []int{}
    greater := []int{}
    for _, v := range arr {
        if v < pivot {
            less = append(less, v)
        } else if v == pivot {
            equal = append(equal, v)
        } else {
            greater = append(greater, v)
        }
    }

    // Step 4: Recurse on the correct partition
    if k < len(less) {
        return medianOfMedians(less, k)
    } else if k < len(less)+len(equal) {
        return pivot
    } else {
        return medianOfMedians(greater, k-len(less)-len(equal))
    }
}

func main() {
    arr := []int{12, 3, 5, 7, 4, 19, 26, 1, 15, 8, 11, 2, 9, 6}
    n := len(arr)
    median := medianOfMedians(append([]int{}, arr...), n/2)
    fmt.Printf("Array: %v\n", arr)
    fmt.Printf("Median (element at position %d): %d\n", n/2, median)

    // Find the 3rd smallest (0-indexed: k=2)
    third := medianOfMedians(append([]int{}, arr...), 2)
    fmt.Printf("3rd smallest: %d\n", third)
}
```

**Java:**

```java
import java.util.*;

public class MedianOfMedians {

    public static int select(int[] arr, int k) {
        return selectHelper(Arrays.copyOf(arr, arr.length), k);
    }

    private static int selectHelper(int[] arr, int k) {
        if (arr.length <= 5) {
            Arrays.sort(arr);
            return arr[k];
        }

        // Find medians of groups of 5
        int numGroups = (arr.length + 4) / 5;
        int[] medians = new int[numGroups];
        for (int i = 0; i < numGroups; i++) {
            int start = i * 5;
            int end = Math.min(start + 5, arr.length);
            int[] group = Arrays.copyOfRange(arr, start, end);
            Arrays.sort(group);
            medians[i] = group[group.length / 2];
        }

        // Median of medians
        int pivot = selectHelper(medians, medians.length / 2);

        // Three-way partition
        List<Integer> less = new ArrayList<>();
        List<Integer> equal = new ArrayList<>();
        List<Integer> greater = new ArrayList<>();
        for (int v : arr) {
            if (v < pivot) less.add(v);
            else if (v == pivot) equal.add(v);
            else greater.add(v);
        }

        if (k < less.size()) {
            return selectHelper(less.stream().mapToInt(Integer::intValue).toArray(), k);
        } else if (k < less.size() + equal.size()) {
            return pivot;
        } else {
            return selectHelper(
                greater.stream().mapToInt(Integer::intValue).toArray(),
                k - less.size() - equal.size()
            );
        }
    }

    public static void main(String[] args) {
        int[] arr = {12, 3, 5, 7, 4, 19, 26, 1, 15, 8, 11, 2, 9, 6};
        System.out.println("Median: " + select(arr, arr.length / 2));
        System.out.println("3rd smallest: " + select(arr, 2));
    }
}
```

**Python:**

```python
def median_of_medians(arr: list[int], k: int) -> int:
    """Select k-th smallest element in O(n) worst case."""
    if len(arr) <= 5:
        return sorted(arr)[k]

    # Groups of 5 and their medians
    groups = [arr[i:i+5] for i in range(0, len(arr), 5)]
    medians = [sorted(g)[len(g) // 2] for g in groups]

    # Median of medians as pivot
    pivot = median_of_medians(medians, len(medians) // 2)

    # Three-way partition
    less = [x for x in arr if x < pivot]
    equal = [x for x in arr if x == pivot]
    greater = [x for x in arr if x > pivot]

    if k < len(less):
        return median_of_medians(less, k)
    elif k < len(less) + len(equal):
        return pivot
    else:
        return median_of_medians(greater, k - len(less) - len(equal))


if __name__ == "__main__":
    arr = [12, 3, 5, 7, 4, 19, 26, 1, 15, 8, 11, 2, 9, 6]
    print(f"Median: {median_of_medians(arr[:], len(arr) // 2)}")
    print(f"3rd smallest: {median_of_medians(arr[:], 2)}")
```

---

## Linear-Time Sorting: Breaking the O(n log n) Barrier

### Comparison-Based Lower Bound

**Theorem:** Any comparison-based sorting algorithm requires Omega(n log n) comparisons in the worst case.

**Proof sketch:** A comparison-based sort constructs a decision tree where each internal node is a comparison and each leaf is a permutation of the input. There are n! possible permutations. A binary tree with n! leaves has height at least log2(n!) = Omega(n log n) by Stirling's approximation.

**However,** this bound applies only to comparison-based sorts. Non-comparison sorts can achieve O(n) by exploiting the structure of the input.

### Counting Sort

Counting sort works when input values are integers in a known range [0, k].

- **Time:** O(n + k)
- **Space:** O(k)
- **When linear:** k = O(n)

(See middle.md for implementation.)

### Radix Sort

Radix sort processes each digit position using a stable sort (typically counting sort).

- **Time:** O(d * (n + k)) where d is the number of digits and k is the radix.
- **When linear:** d and k are constants (e.g., sorting 32-bit integers with radix 256: d=4, k=256).

**Go:**

```go
package main

import "fmt"

// radixSort sorts non-negative integers using LSD radix sort.
func radixSort(arr []int) []int {
    if len(arr) == 0 {
        return arr
    }

    // Find maximum to determine number of digits
    max := arr[0]
    for _, v := range arr {
        if v > max {
            max = v
        }
    }

    result := make([]int, len(arr))
    copy(result, arr)

    // Process each digit position
    for exp := 1; max/exp > 0; exp *= 10 {
        count := make([]int, 10)
        output := make([]int, len(result))

        for _, v := range result {
            digit := (v / exp) % 10
            count[digit]++
        }
        for i := 1; i < 10; i++ {
            count[i] += count[i-1]
        }
        for i := len(result) - 1; i >= 0; i-- {
            digit := (result[i] / exp) % 10
            count[digit]--
            output[count[digit]] = result[i]
        }
        copy(result, output)
    }
    return result
}

func main() {
    arr := []int{170, 45, 75, 90, 802, 24, 2, 66}
    fmt.Printf("Original: %v\n", arr)
    fmt.Printf("Sorted:   %v\n", radixSort(arr))
}
```

**Java:**

```java
import java.util.Arrays;

public class RadixSort {

    public static void radixSort(int[] arr) {
        if (arr.length == 0) return;

        int max = Arrays.stream(arr).max().getAsInt();

        for (int exp = 1; max / exp > 0; exp *= 10) {
            int[] count = new int[10];
            int[] output = new int[arr.length];

            for (int v : arr) count[(v / exp) % 10]++;
            for (int i = 1; i < 10; i++) count[i] += count[i - 1];
            for (int i = arr.length - 1; i >= 0; i--) {
                int digit = (arr[i] / exp) % 10;
                output[--count[digit]] = arr[i];
            }
            System.arraycopy(output, 0, arr, 0, arr.length);
        }
    }

    public static void main(String[] args) {
        int[] arr = {170, 45, 75, 90, 802, 24, 2, 66};
        System.out.println("Original: " + Arrays.toString(arr));
        radixSort(arr);
        System.out.println("Sorted:   " + Arrays.toString(arr));
    }
}
```

**Python:**

```python
def radix_sort(arr: list[int]) -> list[int]:
    """LSD radix sort for non-negative integers. O(d * (n + k))."""
    if not arr:
        return arr

    max_val = max(arr)
    result = arr[:]
    exp = 1

    while max_val // exp > 0:
        count = [0] * 10
        output = [0] * len(result)

        for v in result:
            count[(v // exp) % 10] += 1
        for i in range(1, 10):
            count[i] += count[i - 1]
        for i in range(len(result) - 1, -1, -1):
            digit = (result[i] // exp) % 10
            count[digit] -= 1
            output[count[digit]] = result[i]
        result = output
        exp *= 10

    return result


if __name__ == "__main__":
    arr = [170, 45, 75, 90, 802, 24, 2, 66]
    print(f"Original: {arr}")
    print(f"Sorted:   {radix_sort(arr)}")
```

### Bucket Sort

Bucket sort distributes elements into buckets, sorts each bucket, and concatenates.

- **Time:** O(n) expected when input is uniformly distributed.
- **Worst case:** O(n^2) if all elements go to one bucket.

**Python:**

```python
def bucket_sort(arr: list[float]) -> list[float]:
    """Bucket sort for floats in [0, 1). O(n) expected."""
    n = len(arr)
    if n <= 1:
        return arr

    buckets = [[] for _ in range(n)]
    for v in arr:
        idx = int(v * n)
        if idx == n:
            idx = n - 1
        buckets[idx].append(v)

    # Sort individual buckets (insertion sort for small buckets)
    for bucket in buckets:
        bucket.sort()  # O(k log k) per bucket, O(n) total expected

    result = []
    for bucket in buckets:
        result.extend(bucket)
    return result


if __name__ == "__main__":
    import random
    arr = [random.random() for _ in range(20)]
    print(f"Sorted: {bucket_sort(arr)}")
```

---

## Linear-Time Algorithms in Theory

| Algorithm / Problem               | Time         | Key Insight                                    |
|------------------------------------|--------------|-------------------------------------------------|
| Median of Medians (BFPRT)         | O(n)         | Guaranteed good pivot from groups of 5          |
| Counting Sort                      | O(n + k)     | Non-comparison; counts occurrences              |
| Radix Sort                         | O(d(n + k))  | Non-comparison; processes digit by digit        |
| Suffix Array (SA-IS / DC3)         | O(n)         | Induced sorting / difference cover              |
| Union-Find (amortized per op)      | O(alpha(n))  | Inverse Ackermann; effectively O(1)             |
| Linear-time MST verification       | O(n)         | Koml\'os's algorithm for tree path maxima       |
| Convex hull of sorted points       | O(n)         | Andrew's monotone chain on pre-sorted input     |

---

## Key Takeaways

1. **Adversary arguments** prove that n - 1 comparisons are necessary and sufficient to find the minimum — a tight lower bound.

2. **Median of Medians** achieves O(n) worst-case selection by guaranteeing at least 30% of elements are eliminated each round.

3. **The O(n log n) sorting lower bound** applies only to comparison-based sorts. Counting, radix, and bucket sorts bypass it.

4. **Counting sort** is the simplest linear-time sort but requires bounded integer keys.

5. **Radix sort** achieves O(n) for fixed-width integers by decomposing into digit-level counting sorts.

6. **Proving lower bounds** is as important as designing fast algorithms — it tells us when to stop trying to optimize.

---

## References

- Blum, M., Floyd, R. W., Pratt, V., Rivest, R. L., & Tarjan, R. E. (1973). "Time Bounds for Selection." *Journal of Computer and System Sciences*, 7(4), 448-461.
- Cormen, T. H., et al. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press. Chapters 8 (Sorting in Linear Time) and 9 (Medians and Order Statistics).
- Knuth, D. E. (1998). *The Art of Computer Programming, Volume 3: Sorting and Searching* (2nd ed.). Addison-Wesley.
- Skiena, S. S. (2020). *The Algorithm Design Manual* (3rd ed.). Springer. Chapter 4: Sorting.
