# Big-Theta Notation -- Middle Level

## Table of Contents

1. [Formal Definition](#formal-definition)
2. [Understanding the Constants](#understanding-the-constants)
3. [Proving Theta by Proving O and Omega](#proving-theta-by-proving-o-and-omega)
4. [Proof Walkthrough: Theta(n^2) for 3n^2 + 5n + 2](#proof-walkthrough)
5. [Algorithms Where All Cases Match](#algorithms-where-all-cases-match)
6. [Algorithms Where Cases Differ](#algorithms-where-cases-differ)
7. [Comparison Table: O vs Theta vs Omega](#comparison-table)
8. [Properties of Big-Theta](#properties-of-big-theta)
9. [Code Examples with Analysis](#code-examples-with-analysis)
10. [Common Patterns and Their Theta](#common-patterns-and-their-theta)
11. [Exercises](#exercises)
12. [Summary](#summary)

---

## Formal Definition

**Definition**: We say f(n) = Theta(g(n)) if and only if there exist positive
constants c1, c2, and n0 such that for all n >= n0:

```
c1 * g(n) <= f(n) <= c2 * g(n)
```

In mathematical notation:

```
f(n) = Theta(g(n)) iff
    there exist c1 > 0, c2 > 0, n0 > 0 such that
    for all n >= n0:  c1 * g(n) <= f(n) <= c2 * g(n)
```

**Equivalently**: f(n) = Theta(g(n)) if and only if:
- f(n) = O(g(n))  AND
- f(n) = Omega(g(n))

This is why Theta is called a **tight bound** -- it is simultaneously an upper
bound and a lower bound.

---

## Understanding the Constants

The three constants in the definition each have a specific role:

### c1 (Lower Constant)
- Scales g(n) down to sit below f(n)
- Proves f(n) grows at LEAST as fast as g(n)
- Establishes the Omega (lower) bound

### c2 (Upper Constant)
- Scales g(n) up to sit above f(n)
- Proves f(n) grows at MOST as fast as g(n)
- Establishes the O (upper) bound

### n0 (Threshold)
- The point after which the sandwiching holds
- For small inputs (n < n0), the inequality may not hold
- We only care about asymptotic (large n) behavior

### Visual Representation

```
f(n)
  ^
  |                           / c2 * g(n)
  |                         /
  |                       / 
  |                     /  / f(n)
  |                   / /
  |                 //
  |               / / c1 * g(n)
  |             //
  |           //
  |     ----//---- n0 (threshold)
  |   /   /
  |  / (before n0, relationship may not hold)
  +----------------------------------------> n
```

---

## Proving Theta by Proving O and Omega

The standard technique for proving f(n) = Theta(g(n)) is to prove both bounds
separately.

### Step 1: Prove f(n) = O(g(n))
Find c2 and n0 such that f(n) <= c2 * g(n) for all n >= n0.

### Step 2: Prove f(n) = Omega(g(n))
Find c1 and n0 such that f(n) >= c1 * g(n) for all n >= n0.

### Step 3: Combine
Take the larger n0 from steps 1 and 2. Now you have c1, c2, and n0 such that
c1 * g(n) <= f(n) <= c2 * g(n) for all n >= n0.

---

## Proof Walkthrough

### Claim: f(n) = 3n^2 + 5n + 2 is Theta(n^2)

**Proof of O(n^2) -- Upper Bound:**

We need: 3n^2 + 5n + 2 <= c2 * n^2 for all n >= n0.

For n >= 1:
- 5n <= 5n^2
- 2 <= 2n^2

Therefore:
- 3n^2 + 5n + 2 <= 3n^2 + 5n^2 + 2n^2 = 10n^2

Choose c2 = 10, n0 = 1. Verified: 3n^2 + 5n + 2 <= 10n^2 for all n >= 1.

**Proof of Omega(n^2) -- Lower Bound:**

We need: 3n^2 + 5n + 2 >= c1 * n^2 for all n >= n0.

Since 5n >= 0 and 2 >= 0 for positive n:
- 3n^2 + 5n + 2 >= 3n^2

Choose c1 = 3, n0 = 1. Verified: 3n^2 + 5n + 2 >= 3n^2 for all n >= 1.

**Conclusion:**

With c1 = 3, c2 = 10, n0 = 1:
- 3 * n^2 <= 3n^2 + 5n + 2 <= 10 * n^2 for all n >= 1

Therefore f(n) = 3n^2 + 5n + 2 = Theta(n^2). QED.

---

### Claim: f(n) = 7n + 12 is NOT Theta(n^2)

**Proof by contradiction:**

Assume f(n) = Theta(n^2). Then there exist c1 > 0, n0 such that:
- c1 * n^2 <= 7n + 12 for all n >= n0

Rearranging: c1 * n <= 7 + 12/n

As n grows, the right side approaches 7, but c1 * n grows without bound.
For n > (7 + 12) / c1, the inequality fails.

Contradiction. Therefore 7n + 12 is NOT Theta(n^2). QED.

(Note: 7n + 12 IS O(n^2), but it is NOT Omega(n^2), so it cannot be Theta(n^2).)

---

## Algorithms Where All Cases Match

These algorithms have the same time complexity in best, average, and worst case.
We can give a single Theta bound.

### Merge Sort: Theta(n log n)

```
T(n) = 2T(n/2) + Theta(n)    (recurrence relation)
```

- **Best case**: Theta(n log n) -- still divides and merges everything
- **Average case**: Theta(n log n)
- **Worst case**: Theta(n log n)

The divide step always splits in half. The merge step always processes all n
elements at each level. There are always log(n) levels.

### Selection Sort: Theta(n^2)

```
for i = 0 to n-1:
    find minimum in arr[i..n-1]    // always scans remaining elements
    swap arr[i] and minimum
```

- **All cases**: Theta(n^2) -- always scans remaining array, no early exit

Total comparisons: (n-1) + (n-2) + ... + 1 = n(n-1)/2 = Theta(n^2)

### Matrix Multiplication (naive): Theta(n^3)

Three nested loops, each running n times, always. No data-dependent branching.

---

## Algorithms Where Cases Differ

For these algorithms, we CANNOT give a single Theta. We must specify the case.

### Quicksort

| Case    | Complexity       | When it happens                       |
|---------|------------------|---------------------------------------|
| Best    | Theta(n log n)   | Pivot always splits evenly            |
| Average | Theta(n log n)   | Random pivots, expected behavior      |
| Worst   | Theta(n^2)       | Pivot always picks min or max         |

We say: "Quicksort is O(n^2)" (worst case upper bound) or "Quicksort average
case is Theta(n log n)." We do NOT say "Quicksort is Theta(n log n)" without
qualification.

### Insertion Sort

| Case    | Complexity   | When it happens                         |
|---------|--------------|-----------------------------------------|
| Best    | Theta(n)     | Array already sorted                    |
| Average | Theta(n^2)   | Random order                            |
| Worst   | Theta(n^2)   | Array sorted in reverse                 |

### Binary Search

| Case    | Complexity     | When it happens                       |
|---------|----------------|---------------------------------------|
| Best    | Theta(1)       | Target is at the middle               |
| Average | Theta(log n)   | Random target position                |
| Worst   | Theta(log n)   | Target not present                    |

### Linear Search

| Case    | Complexity   | When it happens                         |
|---------|--------------|-----------------------------------------|
| Best    | Theta(1)     | Target is the first element             |
| Average | Theta(n)     | Target at random position               |
| Worst   | Theta(n)     | Target is last or not present           |

---

## Comparison Table

| Property            | Big-O (O)           | Big-Theta (Theta)       | Big-Omega (Omega)    |
|---------------------|---------------------|-------------------------|----------------------|
| Meaning             | Upper bound         | Tight bound             | Lower bound          |
| Analogy             | <=                  | =                       | >=                   |
| f(n) = 3n + 5      | O(n), O(n^2), O(n^3)| Theta(n) only          | Omega(n), Omega(1)   |
| Direction           | "At most"           | "Exactly"               | "At least"           |
| Formal              | f(n) <= c*g(n)      | c1*g(n) <= f(n) <= c2*g(n)| f(n) >= c*g(n)    |
| Multiple valid?     | Yes (can be loose)  | No (unique class)       | Yes (can be loose)   |
| Common in practice  | Very common         | More precise contexts   | Less common          |
| Relationship        | Ceiling             | Theta = O AND Omega     | Floor                |

### Key Relationships

1. **f(n) = Theta(g(n))** implies **f(n) = O(g(n))** AND **f(n) = Omega(g(n))**
2. **f(n) = O(g(n))** AND **f(n) = Omega(g(n))** implies **f(n) = Theta(g(n))**
3. **f(n) = Theta(g(n))** implies **g(n) = Theta(f(n))** (symmetry)
4. **f(n) = O(g(n))** is equivalent to **g(n) = Omega(f(n))**

---

## Properties of Big-Theta

### 1. Reflexivity
f(n) = Theta(f(n)) for any function f(n).

### 2. Symmetry
If f(n) = Theta(g(n)), then g(n) = Theta(f(n)).

This is unique to Theta. Big-O is NOT symmetric:
- n = O(n^2) is true, but n^2 = O(n) is false.

### 3. Transitivity
If f(n) = Theta(g(n)) and g(n) = Theta(h(n)), then f(n) = Theta(h(n)).

### 4. Constants Don't Matter
Theta(c * f(n)) = Theta(f(n)) for any constant c > 0.

### 5. Sum Rule
If f1(n) = Theta(g1(n)) and f2(n) = Theta(g2(n)), then:
f1(n) + f2(n) = Theta(max(g1(n), g2(n)))

### 6. Product Rule
If f1(n) = Theta(g1(n)) and f2(n) = Theta(g2(n)), then:
f1(n) * f2(n) = Theta(g1(n) * g2(n))

---

## Code Examples with Analysis

### Example 1: Theta Comparison of Two Algorithms

**Go:**

```go
package main

import (
    "fmt"
    "time"
)

// algorithm1 has Theta(n) complexity
// It always performs exactly n iterations
func algorithm1(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        count += i
    }
    return count
}

// algorithm2 has Theta(n^2) complexity
// It always performs exactly n*(n+1)/2 iterations
func algorithm2(n int) int {
    count := 0
    for i := 0; i < n; i++ {
        for j := 0; j <= i; j++ {
            count++
        }
    }
    return count
}

// algorithm3 has Theta(n log n) -- simulates merge sort work
func algorithm3(n int) int {
    if n <= 1 {
        return 1
    }
    work := n // merging at this level
    work += algorithm3(n / 2)
    work += algorithm3(n / 2)
    return work
}

func benchmark(name string, f func(int) int, n int) {
    start := time.Now()
    result := f(n)
    elapsed := time.Since(start)
    fmt.Printf("%-20s n=%-8d result=%-12d time=%v\n", name, n, result, elapsed)
}

func main() {
    sizes := []int{1000, 2000, 4000, 8000}

    for _, n := range sizes {
        fmt.Printf("--- n = %d ---\n", n)
        benchmark("Theta(n)", algorithm1, n)
        benchmark("Theta(n^2)", algorithm2, n)
        benchmark("Theta(n log n)", algorithm3, n)
        fmt.Println()
    }
    // Observe: when n doubles...
    // Theta(n):       time roughly doubles      (2x)
    // Theta(n^2):     time roughly quadruples   (4x)
    // Theta(n log n): time roughly doubles+      (2x + small extra)
}
```

**Java:**

```java
public class ThetaComparison {

    // Theta(n) -- always performs exactly n iterations
    public static long algorithm1(int n) {
        long count = 0;
        for (int i = 0; i < n; i++) {
            count += i;
        }
        return count;
    }

    // Theta(n^2) -- always performs n*(n+1)/2 iterations
    public static long algorithm2(int n) {
        long count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                count++;
            }
        }
        return count;
    }

    // Theta(n log n) -- simulates merge sort work
    public static long algorithm3(int n) {
        if (n <= 1) return 1;
        long work = n;
        work += algorithm3(n / 2);
        work += algorithm3(n / 2);
        return work;
    }

    public static void benchmark(String name, int n) {
        long start = System.nanoTime();
        long result;
        switch (name) {
            case "Theta(n)":       result = algorithm1(n); break;
            case "Theta(n^2)":     result = algorithm2(n); break;
            case "Theta(n log n)": result = algorithm3(n); break;
            default: return;
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("%-20s n=%-8d result=%-12d time=%dns%n",
                          name, n, result, elapsed);
    }

    public static void main(String[] args) {
        int[] sizes = {1000, 2000, 4000, 8000};
        for (int n : sizes) {
            System.out.printf("--- n = %d ---%n", n);
            benchmark("Theta(n)", n);
            benchmark("Theta(n^2)", n);
            benchmark("Theta(n log n)", n);
            System.out.println();
        }
    }
}
```

**Python:**

```python
import time


def algorithm1(n: int) -> int:
    """Theta(n) -- always performs exactly n iterations."""
    count = 0
    for i in range(n):
        count += i
    return count


def algorithm2(n: int) -> int:
    """Theta(n^2) -- always performs n*(n+1)/2 iterations."""
    count = 0
    for i in range(n):
        for j in range(i + 1):
            count += 1
    return count


def algorithm3(n: int) -> int:
    """Theta(n log n) -- simulates merge sort work."""
    if n <= 1:
        return 1
    work = n
    work += algorithm3(n // 2)
    work += algorithm3(n // 2)
    return work


def benchmark(name: str, func, n: int):
    start = time.perf_counter()
    result = func(n)
    elapsed = time.perf_counter() - start
    print(f"{name:<20} n={n:<8} result={result:<12} time={elapsed:.6f}s")


if __name__ == "__main__":
    sizes = [1000, 2000, 4000, 8000]
    for n in sizes:
        print(f"--- n = {n} ---")
        benchmark("Theta(n)", algorithm1, n)
        benchmark("Theta(n^2)", algorithm2, n)
        benchmark("Theta(n log n)", algorithm3, n)
        print()
    # Observe: when n doubles...
    # Theta(n):       time roughly doubles      (2x)
    # Theta(n^2):     time roughly quadruples   (4x)
    # Theta(n log n): time roughly doubles+      (2x + small extra)
```

---

### Example 2: Proving Theta Empirically

**Go:**

```go
package main

import "fmt"

// countOps counts the exact number of operations for
// the triangular nested loop: sum from i=0 to n-1 of (i+1)
// Expected: n*(n+1)/2 = Theta(n^2)
func countOps(n int) int {
    ops := 0
    for i := 0; i < n; i++ {
        for j := 0; j <= i; j++ {
            ops++
        }
    }
    return ops
}

func main() {
    fmt.Printf("%-10s %-15s %-15s %-15s %-15s\n",
        "n", "ops", "ops/n", "ops/n^2", "ops/(n^2/2)")

    for _, n := range []int{100, 200, 400, 800, 1600, 3200} {
        ops := countOps(n)
        fmt.Printf("%-10d %-15d %-15.2f %-15.4f %-15.4f\n",
            n, ops,
            float64(ops)/float64(n),
            float64(ops)/float64(n*n),
            float64(ops)/(float64(n*n)/2.0))
    }
    // The ops/(n^2/2) column should approach 1.0
    // This confirms Theta(n^2) with the leading constant ~0.5
    // c1 = 0.4 and c2 = 0.6 would sandwich f(n)/n^2 for large n
}
```

**Java:**

```java
public class ThetaEmpirical {

    public static long countOps(int n) {
        long ops = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                ops++;
            }
        }
        return ops;
    }

    public static void main(String[] args) {
        System.out.printf("%-10s %-15s %-15s %-15s %-15s%n",
            "n", "ops", "ops/n", "ops/n^2", "ops/(n^2/2)");

        int[] sizes = {100, 200, 400, 800, 1600, 3200};
        for (int n : sizes) {
            long ops = countOps(n);
            System.out.printf("%-10d %-15d %-15.2f %-15.4f %-15.4f%n",
                n, ops,
                (double) ops / n,
                (double) ops / ((long) n * n),
                (double) ops / ((double) n * n / 2));
        }
    }
}
```

**Python:**

```python
def count_ops(n: int) -> int:
    ops = 0
    for i in range(n):
        for j in range(i + 1):
            ops += 1
    return ops


if __name__ == "__main__":
    print(f"{'n':<10} {'ops':<15} {'ops/n':<15} {'ops/n^2':<15} {'ops/(n^2/2)':<15}")

    for n in [100, 200, 400, 800, 1600, 3200]:
        ops = count_ops(n)
        print(f"{n:<10} {ops:<15} {ops/n:<15.2f} {ops/n**2:<15.4f} {ops/(n**2/2):<15.4f}")

    # The ops/(n^2/2) column approaches 1.0
    # confirming Theta(n^2) with leading constant ~0.5
```

---

## Common Patterns and Their Theta

### Pattern 1: Single loop through array
```
for i in range(n): ...
```
**Theta(n)** -- always n iterations.

### Pattern 2: Nested loops (independent bounds)
```
for i in range(n):
    for j in range(n): ...
```
**Theta(n^2)** -- always n*n iterations.

### Pattern 3: Triangular nested loop
```
for i in range(n):
    for j in range(i): ...
```
**Theta(n^2)** -- iterations = n(n-1)/2 = Theta(n^2).

### Pattern 4: Halving loop
```
while n > 0:
    n = n // 2
```
**Theta(log n)** -- halves each time.

### Pattern 5: Loop then halving
```
for i in range(n):      # Theta(n)
while m > 0:             # Theta(log n)
    m = m // 2
```
**Theta(n)** if sequential (sum rule: max(n, log n) = n).

### Pattern 6: Loop with halving inside
```
for i in range(n):
    m = n
    while m > 0:
        m = m // 2
```
**Theta(n log n)** -- product rule: n * log(n).

### Pattern 7: Divide and conquer (balanced)
```
T(n) = 2T(n/2) + Theta(n)
```
**Theta(n log n)** -- Master Theorem case 2.

### Pattern 8: Triple nested loop
```
for i in range(n):
    for j in range(n):
        for k in range(n): ...
```
**Theta(n^3)** -- always n^3 iterations.

---

## Exercises

### Exercise 1: Identify the Theta

For each function, determine the Big-Theta:

a) f(n) = 5n^3 + 100n^2 + 9999
b) f(n) = 2^n + n^100
c) f(n) = log(n) + sqrt(n)
d) f(n) = n * log(n) + n
e) f(n) = 100

**Answers:**
a) Theta(n^3) -- leading term dominates
b) Theta(2^n) -- exponential dominates polynomial
c) Theta(sqrt(n)) -- sqrt(n) dominates log(n)
d) Theta(n log n) -- n*log(n) dominates n
e) Theta(1) -- constant

### Exercise 2: True or False

a) 3n^2 = Theta(n^2) -- **True**
b) 3n^2 = Theta(n^3) -- **False** (too high, fails lower bound)
c) n + log(n) = Theta(n) -- **True** (n dominates)
d) 2^n = Theta(3^n) -- **False** (different exponential bases)
e) Theta(n) implies O(n) -- **True** (Theta implies both O and Omega)
f) O(n) implies Theta(n) -- **False** (O alone is not sufficient)

### Exercise 3: Prove or Disprove

Prove that f(n) = n^2 + n is Theta(n^2).

**Solution:**
Upper bound: n^2 + n <= n^2 + n^2 = 2n^2 for n >= 1. So c2 = 2, n0 = 1.
Lower bound: n^2 + n >= n^2 for n >= 1. So c1 = 1, n0 = 1.
Therefore: 1*n^2 <= n^2 + n <= 2*n^2 for all n >= 1. QED.

---

## Summary

1. **Formal definition**: f(n) = Theta(g(n)) requires finding c1, c2, n0 such
   that c1*g(n) <= f(n) <= c2*g(n) for all n >= n0.

2. **Proving Theta** = proving O (upper) AND Omega (lower) separately.

3. **Algorithms with uniform complexity** (merge sort, selection sort) can be
   given a single Theta bound for all cases.

4. **Algorithms with varying complexity** (quicksort, insertion sort, binary
   search) need case-specific Theta bounds.

5. **O vs Theta vs Omega**: O is upper bound, Omega is lower bound, Theta is
   both combined (tight bound).

6. **Theta has useful properties**: reflexivity, symmetry, transitivity, and
   the sum/product rules.

7. **Empirical verification**: compute ops/g(n) for growing n. If the ratio
   converges to a constant, f(n) = Theta(g(n)).

---

*Next: Continue to the [Senior Level](senior.md) for production applications.*
