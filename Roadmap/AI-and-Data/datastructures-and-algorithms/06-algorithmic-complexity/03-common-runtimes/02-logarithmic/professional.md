# Logarithmic Time O(log n) — Professional / Theoretical Level

## Table of Contents

- [Introduction](#introduction)
- [Proving O(log n) via Recurrence Relations](#proving-olog-n-via-recurrence-relations)
- [Information-Theoretic Lower Bound for Search](#information-theoretic-lower-bound-for-search)
- [Optimal Binary Search Trees — Knuth's Algorithm](#optimal-binary-search-trees--knuths-algorithm)
- [Self-Balancing Tree Proofs — AVL Rotations](#self-balancing-tree-proofs--avl-rotations)
- [Iterated Logarithm and Beyond](#iterated-logarithm-and-beyond)
- [Key Takeaways](#key-takeaways)
- [References](#references)

---

## Introduction

This level examines the mathematical foundations behind O(log n). We prove that binary search is
optimal, analyze the structure of recurrences that produce logarithmic time, explore Knuth's
optimal BST construction, and formally verify the correctness of AVL tree balancing.

---

## Proving O(log n) via Recurrence Relations

### Binary Search Recurrence

Binary search on an array of size n satisfies:

```
T(n) = T(n/2) + O(1)
T(1) = O(1)
```

**Proof by substitution.** Assume T(n) = O(log n), i.e., T(n) <= c * log n for some constant c.

```
T(n) = T(n/2) + d                     (for some constant d)
     <= c * log(n/2) + d              (by induction hypothesis)
     = c * (log n - 1) + d
     = c * log n - c + d
     <= c * log n                      (when c >= d)
```

The base case T(1) = d <= c * log(1) = 0 requires special handling: we use T(2) = d + d = 2d as
the effective base case, with c >= 2d.

### Proof via Unrolling (Telescoping)

More directly, unroll the recurrence:

```
T(n) = T(n/2) + d
     = T(n/4) + d + d
     = T(n/8) + 3d
     ...
     = T(n/2^k) + k*d
```

The recursion bottoms out when `n/2^k = 1`, i.e., `k = log₂ n`:

```
T(n) = T(1) + d * log₂ n = O(log n)
```

### Master Theorem Application

For `T(n) = a*T(n/b) + O(n^d)`:

Binary search: a=1, b=2, d=0.

```
log_b(a) = log₂(1) = 0 = d
```

Case 2 of the Master Theorem applies: **T(n) = O(n^d * log n) = O(log n)**.

### Generalization: T(n) = T(n/c) + O(1)

For any constant c > 1, the recurrence T(n) = T(n/c) + O(1) solves to O(log n). This is why
algorithms that reduce the problem by any constant fraction (not just half) achieve logarithmic
time.

- Ternary search: T(n) = T(n/3) + O(1) = O(log₃ n) = O(log n)
- Skip list search: Expected T(n) = T(n/2) + O(1) = O(log n)

---

## Information-Theoretic Lower Bound for Search

### Theorem: Comparison-Based Search Requires Omega(log n)

**Claim:** Any deterministic comparison-based algorithm that searches for an element in a sorted
array of n elements must make at least ⌈log₂(n+1)⌉ comparisons in the worst case.

**Proof via decision trees.**

Any comparison-based search algorithm can be modeled as a binary decision tree:
- Each internal node represents a comparison (is target < arr[mid]?).
- Each leaf represents a possible outcome (found at index i, or not found).
- There are n+1 possible outcomes: n "found" outcomes and 1 "not found" outcome.

A binary tree with L leaves has height at least ⌈log₂ L⌉. Since L >= n+1:

```
height >= ⌈log₂(n+1)⌉
```

Therefore, the worst-case number of comparisons is **Omega(log n)**.

**Corollary:** Binary search is **asymptotically optimal** among comparison-based search
algorithms. No comparison-based algorithm can beat O(log n) for searching sorted data.

### Information-Theoretic Interpretation

Each comparison yields 1 bit of information (yes or no). To distinguish among n+1 possibilities,
you need at least log₂(n+1) bits. Therefore, at least ⌈log₂(n+1)⌉ comparisons are necessary.

This is an instance of the general principle: **to identify one item from a set of N, you need
at least log₂(N) bits of information**.

### Beyond Comparisons

The lower bound applies only to comparison-based models. Non-comparison techniques can beat it:

- **Hashing**: O(1) expected time for point lookups.
- **Interpolation search**: O(log log n) for uniformly distributed data.
- **Van Emde Boas tree**: O(log log U) for integer keys in universe [0, U).

These do not violate the theorem because they use information beyond mere comparisons (they
exploit the structure of the key space).

---

## Optimal Binary Search Trees — Knuth's Algorithm

### Problem Statement

Given n keys k₁ < k₂ < ... < kₙ with access probabilities p₁, p₂, ..., pₙ, and gap
probabilities q₀, q₁, ..., qₙ (probability of searching for a value between kᵢ and kᵢ₊₁),
construct a BST that minimizes the expected search cost:

```
E[cost] = Σᵢ pᵢ * (depth(kᵢ) + 1) + Σⱼ qⱼ * depth(dⱼ)
```

where dⱼ represents the "dummy" leaves between keys.

### Naive DP: O(n³)

Let `C[i][j]` = minimum expected cost of an optimal BST for keys kᵢ...kⱼ.

```
C[i][j] = min over r in [i,j] of { C[i][r-1] + C[r+1][j] + W[i][j] }
```

where `W[i][j] = Σ(pₖ for k in [i,j]) + Σ(qₖ for k in [i-1,j])` is the total probability weight.

This has O(n²) subproblems, each examining O(n) possible roots, giving O(n³) total.

### Knuth's Optimization: O(n²)

**Knuth's Monotonicity Lemma (1971):** If `R[i][j]` is the optimal root for keys kᵢ...kⱼ, then:

```
R[i][j-1] <= R[i][j] <= R[i+1][j]
```

This constrains the search range for each root, reducing the total work from O(n³) to O(n²).

```python
def optimal_bst(p: list[float], q: list[float], n: int):
    """
    Compute optimal BST using Knuth's O(n^2) optimization.

    p[1..n]: access probabilities for keys
    q[0..n]: gap probabilities
    """
    INF = float('inf')

    # C[i][j] = min cost for keys i..j
    # R[i][j] = optimal root for keys i..j
    # W[i][j] = total weight for keys i..j
    C = [[0.0] * (n + 2) for _ in range(n + 2)]
    R = [[0] * (n + 2) for _ in range(n + 2)]
    W = [[0.0] * (n + 2) for _ in range(n + 2)]

    # Base case: empty subtrees
    for i in range(1, n + 2):
        W[i][i - 1] = q[i - 1]
        C[i][i - 1] = q[i - 1]

    # Fill for increasing lengths
    for length in range(1, n + 1):
        for i in range(1, n - length + 2):
            j = i + length - 1
            W[i][j] = W[i][j - 1] + p[j] + q[j]
            C[i][j] = INF

            # Knuth's optimization: restrict root search
            lo = R[i][j - 1] if length > 1 else i
            hi = R[i + 1][j] if length > 1 else j

            for r in range(lo, hi + 1):
                cost = C[i][r - 1] + C[r + 1][j] + W[i][j]
                if cost < C[i][j]:
                    C[i][j] = cost
                    R[i][j] = r

    return C[1][n], R


if __name__ == "__main__":
    # Example: 5 keys with given probabilities
    p = [0, 0.15, 0.10, 0.05, 0.10, 0.20]  # p[1..5]
    q = [0.05, 0.10, 0.05, 0.05, 0.05, 0.10]  # q[0..5]

    cost, roots = optimal_bst(p, q, 5)
    print(f"Minimum expected cost: {cost:.2f}")  # Output: 2.75
    print(f"Root of optimal BST: key {roots[1][5]}")
```

### Significance

Knuth's optimization is a foundational result in algorithm design. It demonstrates that
exploiting **structural properties** of optimal solutions (monotonicity of roots) can reduce
complexity by an entire polynomial factor.

---

## Self-Balancing Tree Proofs — AVL Rotations

### AVL Height Bound

**Theorem:** An AVL tree with n nodes has height at most **1.44 * log₂(n + 2) - 0.328**.

**Proof sketch:**

Let N(h) be the minimum number of nodes in an AVL tree of height h. An AVL tree of height h has:
- One subtree of height h-1
- One subtree of height at least h-2 (AVL property: heights differ by at most 1)
- One root node

Therefore:

```
N(h) = N(h-1) + N(h-2) + 1
```

with N(0) = 1, N(1) = 2.

This is similar to the Fibonacci recurrence. In fact:

```
N(h) = F(h+2) - 1
```

where F is the Fibonacci sequence. Since F(k) ~ phi^k / sqrt(5) where phi = (1+sqrt(5))/2:

```
n >= N(h) = F(h+2) - 1 ≈ phi^(h+2) / sqrt(5) - 1
```

Solving for h:

```
h <= log_phi(sqrt(5) * (n+1)) - 2
  = log_phi(n+1) + log_phi(sqrt(5)) - 2
  ≈ 1.44 * log₂(n+1) + constant
```

Therefore h = O(log n).

### Rotation Correctness

A **right rotation** at node y (where y.left = x) produces:

```
Before:         After:
    y              x
   / \            / \
  x   C   →     A   y
 / \                / \
A   B              B   C
```

**Claim:** The in-order traversal is preserved (A, x, B, y, C before and after).

**Proof:** Before rotation, in-order gives: A, x, B, y, C. After rotation, in-order gives:
A, x, B, y, C. The BST property is maintained.

**Claim:** At most O(1) rotations per insertion restore the AVL invariant.

**Proof:** After insertion, at most one node on the insertion path has its balance factor
become ±2. A single or double rotation at that node restores the balance factor to 0 or ±1,
and no ancestor's balance factor is affected because the subtree height returns to its
pre-insertion value.

For deletion, up to O(log n) rotations may be needed (one per ancestor on the path to the root).

---

## Iterated Logarithm and Beyond

### The log* Function

The **iterated logarithm** log*(n) is the number of times you must apply log₂ before the result
drops to 1 or below:

```
log*(2) = 1
log*(4) = 2
log*(16) = 3
log*(65536) = 4
log*(2^65536) = 5
```

log*(n) grows so slowly that for any practical n, log*(n) <= 5. It appears in:

- **Union-Find** with path compression + union by rank: O(alpha(n)) per operation, where alpha
  is the inverse Ackermann function, even slower than log*.
- **Minimum spanning tree** algorithms by Chazelle: O(n * alpha(n)).

### Hierarchy of Logarithmic Functions

```
O(1) < O(log log n) < O(log n) < O(sqrt(n)) < O(n)

Even slower growth:
O(1) < O(log* n) < O(alpha(n)) < O(log log n) < O(log n)
```

All of these are considered "nearly constant" in practice, but they are theoretically distinct.

---

## Key Takeaways

1. **The recurrence T(n) = T(n/c) + O(1)** for any constant c > 1 yields O(log n). This is
   provable by substitution, unrolling, or the Master Theorem.

2. **The information-theoretic lower bound** proves that comparison-based search cannot do better
   than Omega(log n). Binary search is optimal.

3. **Knuth's optimal BST** reduces the naive O(n³) DP to O(n²) by exploiting monotonicity of
   optimal roots — a technique applicable to many DP problems.

4. **AVL trees** have height at most 1.44 * log₂(n), proven via the Fibonacci connection.
   Rotations preserve the BST property and restore balance in O(1) per insertion.

5. **Beyond O(log n)**, functions like log log n, log* n, and alpha(n) exist and appear in
   advanced data structures, but all are "essentially constant" in practice.

---

## References

1. Knuth, D. E. "Optimum Binary Search Trees," *Acta Informatica*, 1971.
2. Cormen, T. H., et al. *Introduction to Algorithms* (CLRS), Chapter 15 — Optimal BST.
3. Adelson-Velsky, G. M., Landis, E. M. "An Algorithm for the Organization of Information," 1962.
4. Tarjan, R. E. "Efficiency of a Good But Not Linear Set Union Algorithm," *JACM*, 1975.
5. Fredman, M., Saks, M. "The Cell Probe Complexity of Dynamic Data Structures," *STOC*, 1989.
