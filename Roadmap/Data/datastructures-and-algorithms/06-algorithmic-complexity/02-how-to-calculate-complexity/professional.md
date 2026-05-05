# How to Calculate Complexity? -- Professional Level

## Prerequisites

- [Junior Level](junior.md) -- counting operations, loop analysis
- [Middle Level](middle.md) -- recurrence relations, Master Theorem
- [Senior Level](senior.md) -- profiling, benchmarking, system-scale analysis

## Table of Contents

1. [Formal Proofs of Complexity](#formal-proofs-of-complexity)
   - [Big-O Formal Definition and Proofs](#big-o-formal-definition-and-proofs)
   - [Proving Tight Bounds (Theta)](#proving-tight-bounds-theta)
2. [The Substitution Method](#the-substitution-method)
   - [Technique Overview](#technique-overview)
   - [Worked Examples](#worked-examples)
   - [Common Pitfalls](#common-pitfalls)
3. [The Akra-Bazzi Method](#the-akra-bazzi-method)
   - [When Master Theorem Fails](#when-master-theorem-fails)
   - [The Akra-Bazzi Formula](#the-akra-bazzi-formula)
   - [Worked Examples](#akra-bazzi-examples)
4. [Decision Tree Lower Bounds](#decision-tree-lower-bounds)
   - [Comparison-Based Sorting Lower Bound](#comparison-based-sorting-lower-bound)
   - [Adversary Arguments](#adversary-arguments)
5. [Key Takeaways](#key-takeaways)

---

## Formal Proofs of Complexity

### Big-O Formal Definition and Proofs

**Definition**: f(n) = O(g(n)) if and only if there exist positive constants c and n0 such that:

```
f(n) <= c * g(n)  for all n >= n0
```

**Example proof**: Show that 3n^2 + 5n + 10 = O(n^2).

```
Proof:
We need to find c and n0 such that 3n^2 + 5n + 10 <= c * n^2 for all n >= n0.

For n >= 1:
  5n <= 5n^2
  10 <= 10n^2

Therefore:
  3n^2 + 5n + 10 <= 3n^2 + 5n^2 + 10n^2 = 18n^2

Choose c = 18, n0 = 1.
We have 3n^2 + 5n + 10 <= 18 * n^2 for all n >= 1.
Therefore 3n^2 + 5n + 10 = O(n^2).  QED.
```

**Example proof**: Show that n^2 is NOT O(n).

```
Proof by contradiction:
Assume n^2 = O(n). Then there exist c, n0 > 0 such that:
  n^2 <= c * n  for all n >= n0

Dividing both sides by n (valid since n > 0):
  n <= c  for all n >= n0

But this is impossible since n can grow without bound, while c is a fixed constant.
Contradiction. Therefore n^2 is not O(n).  QED.
```

### Proving Tight Bounds (Theta)

**Definition**: f(n) = Theta(g(n)) if and only if f(n) = O(g(n)) AND f(n) = Omega(g(n)).

Equivalently, there exist c1, c2, n0 > 0 such that:

```
c1 * g(n) <= f(n) <= c2 * g(n)  for all n >= n0
```

**Example**: Prove that n(n-1)/2 = Theta(n^2).

```
Upper bound (Big-O):
  n(n-1)/2 = n^2/2 - n/2 <= n^2/2 <= n^2
  Choose c2 = 1, n0 = 1. So n(n-1)/2 = O(n^2).

Lower bound (Omega):
  n(n-1)/2 = n^2/2 - n/2
  For n >= 2: n/2 <= n^2/4, so:
    n^2/2 - n/2 >= n^2/2 - n^2/4 = n^2/4
  Choose c1 = 1/4, n0 = 2. So n(n-1)/2 = Omega(n^2).

Since n(n-1)/2 = O(n^2) and n(n-1)/2 = Omega(n^2),
we have n(n-1)/2 = Theta(n^2).  QED.
```

---

## The Substitution Method

### Technique Overview

The substitution method for solving recurrences has two steps:

1. **Guess** the form of the solution (e.g., T(n) = O(n log n)).
2. **Prove** the guess correct using mathematical induction.

This is useful when the Master Theorem does not apply, or when you want a rigorous proof.

### Worked Examples

#### Example 1: T(n) = 2T(n/2) + n

**Guess**: T(n) = O(n log n). We claim T(n) <= c * n * log(n) for some constant c.

```
Inductive step: Assume T(k) <= c * k * log(k) for all k < n.

T(n) = 2T(n/2) + n
     <= 2 * c * (n/2) * log(n/2) + n        [by inductive hypothesis]
     = c * n * (log(n) - log(2)) + n
     = c * n * log(n) - c * n * log(2) + n
     = c * n * log(n) - c * n + n
     = c * n * log(n) - (c - 1) * n

For c >= 1, the term -(c-1)*n <= 0, so:
T(n) <= c * n * log(n)

Choose c large enough to satisfy the base case. QED.
```

#### Example 2: T(n) = T(n/3) + T(2n/3) + n

This recurrence arises in unbalanced divide-and-conquer (e.g., quick sort with a fixed-ratio split). The Master Theorem does not apply here because the subproblems have different sizes.

**Guess**: T(n) = O(n log n). Claim T(n) <= c * n * log(n).

```
Inductive step:
T(n) = T(n/3) + T(2n/3) + n
     <= c * (n/3) * log(n/3) + c * (2n/3) * log(2n/3) + n

     = c * (n/3) * (log(n) - log(3)) + c * (2n/3) * (log(n) - log(3/2)) + n

     = c * n * log(n) * (1/3 + 2/3)
       - c * (n/3) * log(3) - c * (2n/3) * log(3/2) + n

     = c * n * log(n) - c * n * [log(3)/3 + 2*log(3/2)/3] + n

The bracketed term is a positive constant (approximately 0.921 for log base 2).
Call it alpha. Then:

T(n) <= c * n * log(n) - c * alpha * n + n
     <= c * n * log(n)

provided c * alpha >= 1, i.e., c >= 1/alpha ≈ 1.086.

Choose c = 2 (or any c >= 1/alpha) to satisfy both the inductive step
and the base case. QED.
```

### Common Pitfalls

**Pitfall 1**: Forgetting to prove the base case.

The induction must hold at T(1) or T(2). Always check that your chosen constant c satisfies the base case.

**Pitfall 2**: Guessing too loosely.

If you guess T(n) = O(n^2) for a recurrence that is actually O(n log n), the proof will succeed -- but you have not found a tight bound. Always try to prove the tightest bound.

**Pitfall 3**: Incorrect algebra with lower-order terms.

When proving T(n) <= c * n * log(n), you cannot have leftover positive terms. A common mistake:

```
WRONG:
T(n) <= c * n * log(n) + n    <-- the +n prevents the proof!

You need:
T(n) <= c * n * log(n)         <-- strictly, for large enough c
```

Solution: subtract a lower-order term in your hypothesis. Guess T(n) <= c * n * log(n) - d * n for some d > 0.

---

## The Akra-Bazzi Method

### When Master Theorem Fails

The Master Theorem requires:
- All subproblems to have the same size (n/b)
- Integer division only

It fails for recurrences like:
- T(n) = T(n/3) + T(2n/3) + n (unequal splits)
- T(n) = T(n/2) + T(n/4) + n (different fractions)
- T(n) = 3T(n/2 + 5) + n^2 (additive terms in the argument)

The **Akra-Bazzi method** handles all of these.

### The Akra-Bazzi Formula

For a recurrence of the form:

```
T(n) = sum_{i=1}^{k} a_i * T(b_i * n + h_i(n)) + g(n)
```

where a_i > 0, 0 < b_i < 1, and h_i(n) = O(n / log^2(n)), g(n) = O(n^c):

1. Find **p** such that: sum_{i=1}^{k} a_i * b_i^p = 1
2. Then: T(n) = Theta( n^p * (1 + integral from 1 to n of g(u)/u^(p+1) du) )

### Akra-Bazzi Examples

#### Example 1: T(n) = T(n/3) + T(2n/3) + n

Here a1 = 1, b1 = 1/3, a2 = 1, b2 = 2/3, g(n) = n.

**Step 1**: Find p such that (1/3)^p + (2/3)^p = 1.

For p = 1: 1/3 + 2/3 = 1. So p = 1.

**Step 2**: Compute the integral.

```
T(n) = Theta( n^1 * (1 + integral_1^n  u / u^2  du) )
     = Theta( n * (1 + integral_1^n  1/u  du) )
     = Theta( n * (1 + ln(n)) )
     = Theta(n log n)
```

#### Example 2: T(n) = T(n/2) + T(n/4) + n

Here a1 = 1, b1 = 1/2, a2 = 1, b2 = 1/4, g(n) = n.

**Step 1**: Find p such that (1/2)^p + (1/4)^p = 1.

Let x = (1/2)^p. Then x + x^2 = 1, so x^2 + x - 1 = 0.
x = (-1 + sqrt(5)) / 2 = 0.618... (the golden ratio minus 1).
(1/2)^p = 0.618, so p = log(1/0.618) / log(2) = 0.694...

**Step 2**: Since g(n) = n and p < 1, we have n / n^(p+1) = n^(-p) which is integrable.

```
integral_1^n  n / u^(p+1)  du  -- actually g(u) = u, so:
integral_1^n  u / u^(p+1)  du = integral_1^n  u^(-p)  du
= [u^(1-p) / (1-p)]_1^n
= (n^(1-p) - 1) / (1-p)
= Theta(n^(1-p))

T(n) = Theta(n^p * (1 + n^(1-p)))
     = Theta(n^p + n)
     = Theta(n)
```

Since p ≈ 0.694 < 1, the n term dominates and T(n) = **Theta(n)**.

#### Example 3: T(n) = 3T(n/2) + n^2

For comparison, let us verify with both Master Theorem and Akra-Bazzi.

**Master Theorem**: a=3, b=2, d=2. log_2(3) ≈ 1.585. Since d=2 > 1.585, Case 3: T(n) = O(n^2).

**Akra-Bazzi**: a=3, b=1/2, g(n)=n^2. Find p: 3*(1/2)^p = 1, so p = log_2(3) ≈ 1.585.

```
integral_1^n  u^2 / u^(p+1) du = integral_1^n  u^(1-p) du = Theta(n^(2-p))

T(n) = Theta(n^p * (1 + n^(2-p))) = Theta(n^p + n^2) = Theta(n^2)
```

Both methods agree: **Theta(n^2)**.

---

## Decision Tree Lower Bounds

### Comparison-Based Sorting Lower Bound

One of the most famous complexity results is: **Any comparison-based sorting algorithm requires Omega(n log n) comparisons in the worst case.**

**Proof using decision trees**:

1. A comparison-based sorting algorithm can be modeled as a binary decision tree.
2. Each internal node represents a comparison (a_i < a_j?).
3. Each leaf represents one possible output permutation.
4. There are n! possible permutations of n elements.
5. A binary tree of height h has at most 2^h leaves.
6. We need 2^h >= n!, so h >= log_2(n!).

Using Stirling's approximation: n! ≈ (n/e)^n * sqrt(2*pi*n)

```
log_2(n!) = n*log_2(n) - n*log_2(e) + O(log n)
          = Theta(n log n)
```

Therefore any comparison-based sort requires **Omega(n log n)** comparisons. Merge sort and heap sort achieve this bound, making them **asymptotically optimal** among comparison sorts.

### Why This Matters

This lower bound tells us:
- No clever comparison-based algorithm can do better than O(n log n).
- To sort faster (e.g., O(n)), you must use non-comparison methods (counting sort, radix sort) that exploit properties of the data.

### Adversary Arguments

An adversary argument is another technique for proving lower bounds. The idea: an adversary answers queries in the worst possible way, forcing the algorithm to ask many questions.

**Example**: Finding the minimum of n elements requires at least n-1 comparisons.

```
Proof (adversary):
Consider the "tournament" interpretation. Each comparison
eliminates one element from being the minimum (the loser).
To determine the minimum, every element except one must
lose at least one comparison.
That requires at least n-1 comparisons.

Any algorithm making fewer than n-1 comparisons leaves at
least two elements that have never lost, so it cannot
determine the minimum.  QED.
```

**Example**: Finding both minimum and maximum of n elements.

Naive approach: 2(n-1) comparisons (find min, then find max).

Optimal approach: ceil(3n/2) - 2 comparisons by processing elements in pairs.

```go
// Optimal min-max: ceil(3n/2) - 2 comparisons
func minMax(arr []int) (int, int) {
    n := len(arr)
    if n == 0 {
        panic("empty array")
    }

    var lo, hi int
    var start int

    if n%2 == 0 {
        if arr[0] < arr[1] {
            lo, hi = arr[0], arr[1]
        } else {
            lo, hi = arr[1], arr[0]
        }
        start = 2
    } else {
        lo, hi = arr[0], arr[0]
        start = 1
    }

    // Process in pairs: 3 comparisons per 2 elements
    for i := start; i < n-1; i += 2 {
        if arr[i] < arr[i+1] {
            if arr[i] < lo { lo = arr[i] }
            if arr[i+1] > hi { hi = arr[i+1] }
        } else {
            if arr[i+1] < lo { lo = arr[i+1] }
            if arr[i] > hi { hi = arr[i] }
        }
    }
    return lo, hi
}
```

```java
// Optimal min-max: ceil(3n/2) - 2 comparisons
public static int[] minMax(int[] arr) {
    int n = arr.length;
    int lo, hi, start;

    if (n % 2 == 0) {
        if (arr[0] < arr[1]) { lo = arr[0]; hi = arr[1]; }
        else { lo = arr[1]; hi = arr[0]; }
        start = 2;
    } else {
        lo = hi = arr[0];
        start = 1;
    }

    for (int i = start; i < n - 1; i += 2) {
        if (arr[i] < arr[i + 1]) {
            if (arr[i] < lo) lo = arr[i];
            if (arr[i + 1] > hi) hi = arr[i + 1];
        } else {
            if (arr[i + 1] < lo) lo = arr[i + 1];
            if (arr[i] > hi) hi = arr[i];
        }
    }
    return new int[]{lo, hi};
}
```

```python
# Optimal min-max: ceil(3n/2) - 2 comparisons
def min_max(arr):
    n = len(arr)
    if n % 2 == 0:
        lo, hi = (arr[0], arr[1]) if arr[0] < arr[1] else (arr[1], arr[0])
        start = 2
    else:
        lo = hi = arr[0]
        start = 1

    for i in range(start, n - 1, 2):
        if arr[i] < arr[i + 1]:
            if arr[i] < lo: lo = arr[i]
            if arr[i + 1] > hi: hi = arr[i + 1]
        else:
            if arr[i + 1] < lo: lo = arr[i + 1]
            if arr[i] > hi: hi = arr[i]

    return lo, hi
```

---

## Key Takeaways

1. **Formal proofs** require finding explicit constants c and n0 that satisfy the Big-O (or Theta) definition.
2. **The substitution method** guesses and proves by induction -- powerful but requires algebraic care with lower-order terms.
3. **The Akra-Bazzi method** generalizes the Master Theorem to handle unequal subproblem sizes and additive terms.
4. **Decision tree lower bounds** prove that comparison-based sorting cannot be faster than O(n log n).
5. **Adversary arguments** prove lower bounds by showing any algorithm must ask enough questions.
6. These formal tools distinguish a professional from someone who merely memorizes complexity classes.

---

> **References**: Cormen et al. (CLRS) Chapters 3-4, Akra & Bazzi (1998), Knuth (TAOCP Volume 3).
