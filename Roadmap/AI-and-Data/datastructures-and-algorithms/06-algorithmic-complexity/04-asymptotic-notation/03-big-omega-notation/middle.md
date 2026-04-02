# Big-Omega Notation — Middle Level

## Prerequisites

- Understanding of Big-O notation and asymptotic analysis
- Basic knowledge of logarithms and combinatorics
- Familiarity with recursion and recurrence relations
- Junior-level understanding of Big-Omega concepts

## Learning Objectives

By the end of this section, you will be able to:

1. State and apply the formal mathematical definition of Big-Omega
2. Prove lower bounds using the formal definition
3. Understand and explain the decision tree argument for sorting
4. Apply adversary arguments to prove lower bounds
5. Distinguish clearly between best-case analysis and problem lower bounds
6. Use Big-Omega to prove algorithm optimality

---

## Table of Contents

1. [Formal Definition of Big-Omega](#formal-definition-of-big-omega)
2. [Proving Lower Bounds with the Formal Definition](#proving-lower-bounds-with-the-formal-definition)
3. [Decision Tree Argument for Sorting](#decision-tree-argument-for-sorting)
4. [Adversary Arguments](#adversary-arguments)
5. [Best-Case vs Lower Bound — A Rigorous Distinction](#best-case-vs-lower-bound)
6. [Big-Omega in Algorithm Design: Proving Optimality](#proving-optimality)
7. [Code Examples with Formal Analysis](#code-examples-with-formal-analysis)
8. [Practice Proofs](#practice-proofs)
9. [Summary](#summary)

---

## Formal Definition of Big-Omega

### The Definition

For functions f(n) and g(n) mapping non-negative integers to non-negative real numbers:

```
f(n) = Omega(g(n)) if and only if:

    There exist positive constants c > 0 and n0 >= 0 such that:
    
        f(n) >= c * g(n)    for all n >= n0
```

In set notation:

```
Omega(g(n)) = { f(n) : there exist positive constants c and n0 such that
                        0 <= c * g(n) <= f(n) for all n >= n0 }
```

### Breaking Down the Definition

| Component   | Meaning                                                    |
|-------------|-------------------------------------------------------------|
| c > 0       | A positive constant multiplier (the "scaling factor")       |
| n0 >= 0     | The threshold after which the bound holds                   |
| f(n) >= c*g(n) | f grows at least as fast as g (up to constant c)         |
| for all n >= n0 | The relationship holds for all sufficiently large inputs |

### Key Properties

**1. Transitivity:** If f(n) = Omega(g(n)) and g(n) = Omega(h(n)), then f(n) = Omega(h(n)).

**2. Reflexivity:** f(n) = Omega(f(n)) for any function f.

**3. Symmetry with Big-O:** f(n) = Omega(g(n)) if and only if g(n) = O(f(n)).

**4. Theta connection:** f(n) = Theta(g(n)) if and only if f(n) = O(g(n)) AND f(n) = Omega(g(n)).

### Limit Definition (Alternative)

Using limits, when the limit exists:

```
If lim (n -> infinity) f(n) / g(n) = L, then:

    - If L > 0 (including L = infinity), then f(n) = Omega(g(n))
    - If L = infinity, then f(n) is NOT O(g(n))
    - If 0 < L < infinity, then f(n) = Theta(g(n))
```

---

## Proving Lower Bounds with the Formal Definition

### Proof Template

To prove f(n) = Omega(g(n)):

1. **State what you need to show:** Find c > 0 and n0 >= 0 such that f(n) >= c * g(n) for all n >= n0.
2. **Choose c and n0:** Pick specific values that work.
3. **Verify:** Show the inequality holds for all n >= n0.

### Proof 1: 3n^2 + 5n = Omega(n^2)

**Claim:** f(n) = 3n^2 + 5n is Omega(n^2).

**Proof:**
We need to find c > 0 and n0 such that 3n^2 + 5n >= c * n^2 for all n >= n0.

Choose c = 3 and n0 = 0.

For all n >= 0:
```
3n^2 + 5n >= 3n^2    (since 5n >= 0 for n >= 0)
          = 3 * n^2
          = c * n^2
```

Therefore 3n^2 + 5n >= 3 * n^2 for all n >= 0.

By definition, 3n^2 + 5n = Omega(n^2). QED.

### Proof 2: n^2 - 3n = Omega(n^2)

**Claim:** f(n) = n^2 - 3n is Omega(n^2).

**Proof:**
We need c > 0 and n0 such that n^2 - 3n >= c * n^2 for all n >= n0.

Choose c = 1/2 and n0 = 6.

For n >= 6:
```
n^2 - 3n = n^2 - 3n
         >= n^2 - (n/2) * n    (since 3 <= n/2 when n >= 6)
         = n^2 - n^2/2
         = n^2 / 2
         = (1/2) * n^2
         = c * n^2
```

Therefore n^2 - 3n >= (1/2) * n^2 for all n >= 6.

By definition, n^2 - 3n = Omega(n^2). QED.

### Proof 3: n log n = Omega(n)

**Claim:** f(n) = n log n is Omega(n).

**Proof:**
Choose c = 1 and n0 = 2.

For n >= 2:
```
n log n >= n * log(2)    (since log n >= log 2 for n >= 2)
        >= n * 1          (log base 2 of 2 = 1)
        = 1 * n
        = c * n
```

Therefore n log n >= n for all n >= 2.

By definition, n log n = Omega(n). QED.

### Proof 4: 2^n = Omega(n^3)

**Claim:** f(n) = 2^n is Omega(n^3).

**Proof:**
Using the limit definition:

```
lim (n -> infinity) 2^n / n^3
```

Applying L'Hopital's rule three times (or noting that exponentials dominate polynomials):

```
= lim (n -> infinity) (ln 2)^3 * 2^n / 6 = infinity
```

Since the limit is infinity (> 0), 2^n = Omega(n^3). QED.

---

## Decision Tree Argument for Sorting

### The Argument

This is one of the most important lower bound proofs in computer science. It proves
that **any comparison-based sorting algorithm requires Omega(n log n) comparisons**
in the worst case.

### Setup

- A comparison sort can only learn about the order of elements by comparing pairs.
- Each comparison has two outcomes: `<=` or `>`.
- We can model any comparison sort as a **binary decision tree**.

### The Decision Tree Model

```
                    a[1] <= a[2]?
                   /             \
              yes /               \ no
               /                   \
        a[2] <= a[3]?         a[1] <= a[3]?
         /        \              /        \
        /          \            /          \
   [1,2,3]    a[1]<=a[3]?  [2,1,3]   a[2]<=a[3]?
                /     \                 /      \
           [1,3,2]  [3,1,2]       [2,3,1]   [3,2,1]
```

Each **leaf** represents a permutation (one possible sorted order).
Each **internal node** represents a comparison.

### Key Observations

1. There are n! possible permutations of n elements.
2. The decision tree must have **at least n! leaves** (one for each permutation).
3. A binary tree with L leaves has height at least log2(L).
4. Therefore, the height (worst-case comparisons) is at least log2(n!).

### Deriving the Bound

```
Height >= log2(n!)
```

Using Stirling's approximation: n! >= (n/e)^n

```
log2(n!) >= log2((n/e)^n)
          = n * log2(n/e)
          = n * (log2(n) - log2(e))
          = n * log2(n) - n * log2(e)
          = Omega(n log n)
```

Therefore: **Any comparison-based sorting algorithm requires Omega(n log n) comparisons
in the worst case.**

### What This Means

| Algorithm      | Worst Case    | Matches Lower Bound? |
|----------------|---------------|----------------------|
| Merge Sort     | O(n log n)    | Yes — optimal        |
| Heap Sort      | O(n log n)    | Yes — optimal        |
| Quick Sort     | O(n^2)        | No — worst case is worse |
| Bubble Sort    | O(n^2)        | No — not optimal     |
| Insertion Sort | O(n^2)        | No — not optimal     |

**Note:** Non-comparison sorts (counting sort, radix sort) can beat n log n because
they use additional information about the input (like knowing elements are integers in a range).

---

## Adversary Arguments

### What is an Adversary Argument?

An adversary argument proves a lower bound by playing a "game" against any algorithm:

1. The **adversary** controls the input.
2. The **algorithm** asks questions (comparisons, queries).
3. The adversary answers consistently but adversarially — choosing answers that
   force the algorithm to do the most work.

If the adversary can always force the algorithm to ask at least f(n) questions,
then f(n) is a lower bound for the problem.

### Example: Finding the Maximum

**Problem:** Find the maximum element in an array of n distinct elements.

**Claim:** Any algorithm must make at least n - 1 comparisons.

**Adversary Strategy:**
- The adversary tracks which elements have "lost" a comparison (were found to be
  smaller than some other element).
- An element that has never lost could be the maximum.
- To determine the maximum, every element except the maximum must lose at least
  one comparison.
- That's n - 1 elements that must each lose at least once.
- Each comparison eliminates at most 1 element (the smaller one).
- Therefore, at least n - 1 comparisons are needed.

**Result:** Finding the maximum is Omega(n) (specifically, requires exactly n - 1 comparisons).

### Example: Finding Both Min and Max

**Problem:** Find both the minimum and maximum in an array of n elements.

**Claim:** Any algorithm requires at least ceil(3n/2) - 2 comparisons.

**Adversary Strategy:**
- Every element except the min must "win" at least one comparison.
- Every element except the max must "lose" at least one comparison.
- A single comparison can establish both a win and a loss.
- But there are n elements needing to lose and n needing to win, minus 2
  (min does not need to win, max does not need to lose).
- Careful counting yields the ceil(3n/2) - 2 bound.

---

## Best-Case vs Lower Bound

### The Rigorous Distinction

These concepts are often confused but are fundamentally different:

**Best case of an algorithm A on input size n:**
```
T_best(n) = min { T(A, x) : |x| = n }

The minimum running time over ALL inputs of size n.
```

**Lower bound of a problem P on input size n:**
```
L(n) = min { T_worst(A, n) : A solves P correctly }

The minimum worst-case time over ALL correct algorithms for P.
```

### Comparison Table

| Aspect              | Best Case                     | Lower Bound                       |
|---------------------|-------------------------------|-----------------------------------|
| Applies to          | A specific algorithm          | An entire problem                 |
| Input               | Most favorable input          | Any input (worst case of best algo)|
| Measures            | Minimum time for one algorithm| Minimum time for any algorithm    |
| Can be beaten by    | Another algorithm? No, same algorithm | Cannot be beaten by anything |
| Example             | Insertion sort on sorted input: O(n) | Sorting: Omega(n log n) |

### Why the Confusion Matters

Consider insertion sort:
- **Best case:** O(n) — on an already sorted array.
- **Worst case:** O(n^2) — on a reverse-sorted array.

If someone says "insertion sort is Omega(n)", they might mean:
1. The best case is n (correct, but not the whole picture).
2. The algorithm always takes at least n steps (correct — must read input).

But the **problem** of sorting has lower bound Omega(n log n) for comparison sorts.
Insertion sort's best case of O(n) does NOT contradict this, because:
- The lower bound applies to the **worst case** of any algorithm.
- Insertion sort's worst case is O(n^2), which is >= Omega(n log n). No contradiction.

---

## Big-Omega in Algorithm Design: Proving Optimality

### The Optimality Framework

An algorithm A is **optimal** for problem P if:

```
T_worst(A, n) = Theta(L(n))

where L(n) is the lower bound for problem P.
```

In other words: the algorithm's worst case matches the problem's lower bound
(up to constant factors).

### Steps to Prove Optimality

1. **Design** an algorithm A and analyze its worst-case complexity: O(f(n)).
2. **Prove** a lower bound for the problem: Omega(g(n)).
3. **Show** that f(n) = Theta(g(n)).
4. **Conclude** that A is optimal.

### Classic Optimality Results

| Problem             | Lower Bound      | Optimal Algorithm    | Complexity      |
|---------------------|------------------|----------------------|-----------------|
| Find max            | Omega(n)         | Single scan          | Theta(n)        |
| Comparison sort     | Omega(n log n)   | Merge sort           | Theta(n log n)  |
| Search sorted array | Omega(log n)     | Binary search        | Theta(log n)    |
| Matrix multiply     | Omega(n^2)       | Unknown!             | Best: O(n^2.37) |

**Note:** For matrix multiplication, there is a gap between the lower bound Omega(n^2)
(you must at least write the output) and the best known algorithm. Closing this gap
is a major open problem.

---

## Code Examples with Formal Analysis

### Example 1: Proving Linear Scan is Optimal for Finding Max

**Go:**

```go
package main

import "fmt"

// findMax finds the maximum element.
// ANALYSIS:
//   Upper bound: O(n) — single pass through the array.
//   Lower bound: Omega(n) — must examine every element (adversary argument).
//   Conclusion: Theta(n) — this algorithm is OPTIMAL.
//
// Proof of lower bound:
//   Suppose an algorithm does not examine element a[k].
//   An adversary could set a[k] to be larger than all examined elements.
//   The algorithm would return the wrong answer.
//   Therefore, every element must be examined: Omega(n).
func findMax(arr []int) int {
    if len(arr) == 0 {
        panic("empty array")
    }
    max := arr[0]
    comparisons := 0
    
    for i := 1; i < len(arr); i++ {
        comparisons++
        if arr[i] > max {
            max = arr[i]
        }
    }
    
    fmt.Printf("  Elements: %d, Comparisons: %d (lower bound: %d)\n",
        len(arr), comparisons, len(arr)-1)
    return max
}

func main() {
    sizes := []int{10, 100, 1000}
    for _, n := range sizes {
        arr := make([]int, n)
        for i := range arr {
            arr[i] = i
        }
        fmt.Printf("n = %d:\n", n)
        max := findMax(arr)
        fmt.Printf("  Max: %d\n\n", max)
    }
    // Each case uses exactly n-1 comparisons = lower bound.
    // This proves the algorithm is optimal.
}
```

**Java:**

```java
public class OptimalMax {
    
    // findMax is Theta(n) — matches the Omega(n) lower bound.
    // Uses exactly n-1 comparisons, which is proven optimal.
    public static int findMax(int[] arr) {
        if (arr.length == 0) throw new IllegalArgumentException("empty");
        
        int max = arr[0];
        int comparisons = 0;
        
        for (int i = 1; i < arr.length; i++) {
            comparisons++;
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        
        System.out.printf("  n=%d, comparisons=%d, lower_bound=%d%n",
            arr.length, comparisons, arr.length - 1);
        return max;
    }
    
    public static void main(String[] args) {
        int[] sizes = {10, 100, 1000};
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i;
            System.out.println("n = " + n + ":");
            int max = findMax(arr);
            System.out.println("  Max: " + max + "\n");
        }
    }
}
```

**Python:**

```python
def find_max(arr):
    """
    Theta(n) — matches the Omega(n) lower bound for finding maximum.
    Uses exactly n-1 comparisons, which is proven optimal by adversary argument.
    """
    if not arr:
        raise ValueError("empty array")
    
    max_val = arr[0]
    comparisons = 0
    
    for i in range(1, len(arr)):
        comparisons += 1
        if arr[i] > max_val:
            max_val = arr[i]
    
    print(f"  n={len(arr)}, comparisons={comparisons}, lower_bound={len(arr)-1}")
    return max_val


for n in [10, 100, 1000]:
    arr = list(range(n))
    print(f"n = {n}:")
    mx = find_max(arr)
    print(f"  Max: {mx}\n")
```

### Example 2: Binary Search — Matching the Omega(log n) Lower Bound

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

// binarySearch demonstrates an optimal Theta(log n) search algorithm.
// LOWER BOUND PROOF:
//   - There are n possible positions for the target.
//   - Each comparison yields 2 outcomes (yes/no).
//   - After k comparisons, we can distinguish at most 2^k outcomes.
//   - We need 2^k >= n, so k >= log2(n).
//   - Therefore: Omega(log n) comparisons required.
// UPPER BOUND: O(log n) — binary search halves the range each step.
// CONCLUSION: Binary search is Theta(log n) — OPTIMAL.
func binarySearch(arr []int, target int) (int, int) {
    lo, hi := 0, len(arr)-1
    comparisons := 0
    
    for lo <= hi {
        mid := lo + (hi-lo)/2
        comparisons++
        
        if arr[mid] == target {
            return mid, comparisons
        } else if arr[mid] < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return -1, comparisons
}

func main() {
    sizes := []int{16, 256, 4096, 65536}
    
    for _, n := range sizes {
        arr := make([]int, n)
        for i := range arr {
            arr[i] = i * 2 // Even numbers
        }
        
        // Worst case: search for element not in array
        target := n*2 + 1
        _, comps := binarySearch(arr, target)
        lowerBound := int(math.Ceil(math.Log2(float64(n))))
        
        fmt.Printf("n=%5d: comparisons=%2d, log2(n)=%2d\n",
            n, comps, lowerBound)
    }
}
```

**Java:**

```java
public class OptimalSearch {
    
    public static int[] binarySearch(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1;
        int comparisons = 0;
        
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            comparisons++;
            
            if (arr[mid] == target) {
                return new int[]{mid, comparisons};
            } else if (arr[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return new int[]{-1, comparisons};
    }
    
    public static void main(String[] args) {
        int[] sizes = {16, 256, 4096, 65536};
        
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = i * 2;
            
            int target = n * 2 + 1; // Not in array — worst case
            int[] result = binarySearch(arr, target);
            int lowerBound = (int) Math.ceil(Math.log(n) / Math.log(2));
            
            System.out.printf("n=%5d: comparisons=%2d, log2(n)=%2d%n",
                n, result[1], lowerBound);
        }
    }
}
```

**Python:**

```python
import math

def binary_search(arr, target):
    """Theta(log n) search — matches Omega(log n) lower bound."""
    lo, hi = 0, len(arr) - 1
    comparisons = 0
    
    while lo <= hi:
        mid = lo + (hi - lo) // 2
        comparisons += 1
        
        if arr[mid] == target:
            return mid, comparisons
        elif arr[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    
    return -1, comparisons


for n in [16, 256, 4096, 65536]:
    arr = list(range(0, n * 2, 2))
    target = n * 2 + 1  # Not in array — worst case
    _, comps = binary_search(arr, target)
    lower_bound = math.ceil(math.log2(n))
    
    print(f"n={n:5d}: comparisons={comps:2d}, log2(n)={lower_bound:2d}")
```

### Example 3: Counting Comparisons in Sorting

**Go:**

```go
package main

import (
    "fmt"
    "math"
)

var compCount int

func mergeSortCount(arr []int) []int {
    if len(arr) <= 1 {
        return arr
    }
    mid := len(arr) / 2
    left := mergeSortCount(arr[:mid])
    right := mergeSortCount(arr[mid:])
    return mergeCount(left, right)
}

func mergeCount(left, right []int) []int {
    result := make([]int, 0, len(left)+len(right))
    i, j := 0, 0
    for i < len(left) && j < len(right) {
        compCount++ // Count each comparison
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
    sizes := []int{8, 64, 512, 4096}
    
    fmt.Println("Merge Sort: comparisons vs Omega(n log n) lower bound")
    fmt.Println("=====================================================")
    
    for _, n := range sizes {
        arr := make([]int, n)
        // Worst case for merge sort: interleaved values
        for i := range arr {
            arr[i] = n - i
        }
        
        compCount = 0
        mergeSortCount(arr)
        
        lowerBound := float64(n) * math.Log2(float64(n))
        ratio := float64(compCount) / lowerBound
        
        fmt.Printf("n=%4d: comparisons=%6d, n*log2(n)=%8.0f, ratio=%.2f\n",
            n, compCount, lowerBound, ratio)
    }
    // The ratio stays close to 1, showing merge sort is near-optimal.
}
```

**Java:**

```java
public class SortComparisons {
    static int compCount = 0;
    
    public static int[] mergeSort(int[] arr) {
        if (arr.length <= 1) return arr;
        int mid = arr.length / 2;
        int[] left = new int[mid];
        int[] right = new int[arr.length - mid];
        System.arraycopy(arr, 0, left, 0, mid);
        System.arraycopy(arr, mid, right, 0, arr.length - mid);
        left = mergeSort(left);
        right = mergeSort(right);
        return merge(left, right);
    }
    
    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            compCount++;
            if (left[i] <= right[j]) result[k++] = left[i++];
            else result[k++] = right[j++];
        }
        while (i < left.length) result[k++] = left[i++];
        while (j < right.length) result[k++] = right[j++];
        return result;
    }
    
    public static void main(String[] args) {
        int[] sizes = {8, 64, 512, 4096};
        System.out.println("Merge Sort: comparisons vs Omega(n log n)");
        
        for (int n : sizes) {
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = n - i;
            
            compCount = 0;
            mergeSort(arr);
            
            double lowerBound = n * (Math.log(n) / Math.log(2));
            double ratio = compCount / lowerBound;
            
            System.out.printf("n=%4d: comparisons=%6d, n*log2(n)=%8.0f, ratio=%.2f%n",
                n, compCount, lowerBound, ratio);
        }
    }
}
```

**Python:**

```python
import math

comp_count = 0

def merge_sort_count(arr):
    global comp_count
    if len(arr) <= 1:
        return arr
    mid = len(arr) // 2
    left = merge_sort_count(arr[:mid])
    right = merge_sort_count(arr[mid:])
    return merge_count(left, right)

def merge_count(left, right):
    global comp_count
    result = []
    i = j = 0
    while i < len(left) and j < len(right):
        comp_count += 1
        if left[i] <= right[j]:
            result.append(left[i])
            i += 1
        else:
            result.append(right[j])
            j += 1
    result.extend(left[i:])
    result.extend(right[j:])
    return result


print("Merge Sort: comparisons vs Omega(n log n) lower bound")
print("=" * 55)

for n in [8, 64, 512, 4096]:
    arr = list(range(n, 0, -1))  # Reverse sorted
    comp_count = 0
    merge_sort_count(arr)
    
    lower_bound = n * math.log2(n)
    ratio = comp_count / lower_bound
    
    print(f"n={n:4d}: comparisons={comp_count:6d}, "
          f"n*log2(n)={lower_bound:8.0f}, ratio={ratio:.2f}")
```

---

## Practice Proofs

### Exercise 1: Prove 5n^3 - 2n^2 = Omega(n^3)

**Hint:** Choose c = 3 and find appropriate n0. Show that 5n^3 - 2n^2 >= 3n^3
by proving 2n^3 >= 2n^2, which simplifies to n >= 1.

### Exercise 2: Prove n! = Omega(2^n)

**Hint:** For n >= 4, show n! >= 2^n by induction. Base: 4! = 24 >= 16 = 2^4.
Step: (n+1)! = (n+1) * n! >= (n+1) * 2^n >= 2 * 2^n = 2^(n+1) for n >= 1.

### Exercise 3: Prove log(n!) = Omega(n log n)

**Hint:** n! >= (n/2)^(n/2), so log(n!) >= (n/2) * log(n/2) = Omega(n log n).

### Exercise 4: Adversary Argument for Merge

**Problem:** Merging two sorted arrays of size n/2 each into one sorted array.

**Claim:** Any merge algorithm requires at least n - 1 comparisons in the worst case.

**Hint:** The adversary can choose inputs where each comparison only determines
the position of one element. With 2 * (n/2) = n elements, we need at least n - 1
comparisons to place all elements.

---

## Summary

### Key Formulas

```
f(n) = Omega(g(n))  <=>  exists c > 0, n0 >= 0: f(n) >= c*g(n) for all n >= n0
f(n) = Omega(g(n))  <=>  g(n) = O(f(n))
f(n) = Theta(g(n))  <=>  f(n) = O(g(n)) AND f(n) = Omega(g(n))
```

### Key Lower Bounds

| Problem                    | Lower Bound     | Proof Technique      |
|----------------------------|-----------------|----------------------|
| Find maximum               | Omega(n)        | Adversary argument   |
| Comparison sort             | Omega(n log n)  | Decision tree        |
| Search sorted array         | Omega(log n)    | Information theory   |
| Merge two sorted arrays     | Omega(n)        | Adversary argument   |
| Find min and max            | Omega(3n/2 - 2) | Adversary argument   |

### Key Takeaways

1. The formal definition requires finding **specific constants** c and n0.
2. The **decision tree argument** is the standard proof for sorting lower bounds.
3. **Adversary arguments** are a powerful technique for proving lower bounds.
4. **Best case != lower bound** — they measure fundamentally different things.
5. An algorithm is **optimal** when its complexity matches the problem's lower bound.
6. **Theta = O intersection Omega** — tight bounds require both upper and lower bounds.

---

*Mastering Big-Omega and lower bound proofs separates algorithm users from algorithm
designers. When you can prove your solution is optimal, you have reached a deep
understanding of the problem.*
