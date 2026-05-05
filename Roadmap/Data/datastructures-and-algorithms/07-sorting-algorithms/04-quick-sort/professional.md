# Quick Sort — Mathematical Foundations

## Table of Contents

1. [Formal Definition](#formal-definition)
2. [Correctness Proof](#correctness-proof)
3. [Average-Case Analysis](#average-case-analysis)
4. [Randomized Quick Sort Probability Bounds](#randomized-quick-sort)
5. [Quickselect (Median in Linear Time)](#quickselect-linear)
6. [Cache-Oblivious Analysis](#cache-oblivious)
7. [Parallel Complexity](#parallel-complexity)
8. [Comparison](#comparison)
9. [Summary](#summary)

---

## Formal Definition

```text
QUICKSORT(A, p, r):
  if p < r:
    q = PARTITION(A, p, r)
    QUICKSORT(A, p, q - 1)
    QUICKSORT(A, q + 1, r)

PARTITION(A, p, r):    // Lomuto
  x = A[r]
  i = p - 1
  for j = p to r - 1:
    if A[j] <= x:
      i = i + 1
      exchange A[i] with A[j]
  exchange A[i+1] with A[r]
  return i + 1
```

CLRS Chapter 7 reference.

---

## Correctness Proof

### Partition Invariants (Lomuto)

**Invariant:** At the start of each iteration of the for-j loop:
- A[p..i]: all ≤ x
- A[i+1..j-1]: all > x
- A[r]: still = x (pivot)

**Initialization (j = p):** i = p - 1, so A[p..p-1] and A[p..p-1] are both empty. ✓

**Maintenance:** If A[j] ≤ x: increment i, swap → A[p..i] still all ≤ x. If A[j] > x: just advance j. ✓

**Termination (j = r):** A[p..i] all ≤ x, A[i+1..r-1] all > x. Final swap puts pivot at i+1; left = ≤ x, right = > x. ✓

### Quick Sort Correctness

**Inductive hypothesis:** Quick Sort sorts ranges of size < n.

**Inductive step:** For range of size n:
1. Partition places pivot at correct position q. Left is ≤ pivot, right is > pivot.
2. By IH, Quick Sort on A[p..q-1] sorts left.
3. By IH, Quick Sort on A[q+1..r] sorts right.
4. Concatenated result is sorted.

QED.

---

## Average-Case Analysis

Assume input is uniformly random permutation of n distinct elements. Let `X_{ij}` indicate that elements at original ranks i and j are compared during the sort.

**Theorem:** E[X_{ij}] = 2/(j - i + 1).

**Proof sketch:** Elements i and j are compared iff one of them is the first to be chosen as pivot from the set {z_i, z_{i+1}, ..., z_j}. There are (j - i + 1) such elements; by symmetry, each is equally likely to be picked first. Probability = 2/(j - i + 1) (because either i or j must be the first of the two among the (j - i + 1) candidates).

**Total expected comparisons:**
```text
E[C(n)] = Σ_{i<j} 2/(j - i + 1)
       = 2 Σ_{k=1}^{n-1} (n - k)/(k + 1)    (substituting k = j - i)
       ≈ 2n · ln n
       ≈ 1.39 n log₂ n
```

So Quick Sort does about **39% more comparisons than the information-theoretic minimum** (n log₂ n), but in practice the cache locality more than compensates.

---

## Randomized Quick Sort

Choose pivot uniformly at random from each partition. Expected time = O(n log n) regardless of input.

### Probability Bound (Concentration)

**Theorem (Hoeffding-type):** With probability ≥ 1 - 1/n^c, randomized Quick Sort runs in O(c n log n) time.

**Proof sketch:** Each split is "balanced" (between 1/4 and 3/4) with probability 1/2. Bad splits don't accumulate; with high probability, depth = O(log n). Combined with O(n) per level → O(n log n) w.h.p.

### Adversarial Resistance

Without randomization, an adversary can craft input causing O(n²). With random pivots from secure RNG, the adversary has no information about your pivot choice → expected O(n log n) maintained.

**Practical:** Always use random or pattern-defeating pivot in production.

---

## Quickselect Linear

**Theorem:** Quickselect with random pivot finds the k-th smallest in **expected O(n)** time.

**Recurrence:**
```text
T(n) ≤ T(3n/4) + O(n)   (with prob ≥ 1/2 the partition is balanced)
By Master Theorem: T(n) = O(n)
```

### Median-of-Medians (Worst-Case Linear)

Blum-Floyd-Pratt-Rivest-Tarjan (1973): **deterministic O(n)** selection.

```text
SELECT(A, k):
  Group A into ⌈n/5⌉ groups of 5.
  Find median of each group: M = list of medians.
  m = SELECT(M, ⌈|M|/2⌉)  -- median of medians (recursive)
  Partition A around m.
  Recurse on the side containing the k-th element.

Recurrence: T(n) ≤ T(n/5) + T(7n/10) + O(n) = O(n)
```

**Significance:** Eliminates worst case but constant factor is high. Used to make Quickselect deterministic O(n).

---

## Cache-Oblivious Analysis

### Cache Misses

```text
Q(n) = O((n/B) · log(n/M))
```

where B = cache line, M = cache size.

This is **asymptotically optimal** (matches the Aggarwal-Vitter lower bound for comparison sorts).

Why it's good: each partition step touches contiguous memory; subarrays eventually fit in cache.

### Memory Bandwidth

For n=10⁶ ints:
- **Quick Sort writes:** ~16 MB (just swaps).
- **Merge Sort writes:** ~160 MB (aux buffer round-trips).

Quick Sort uses **10× less memory bandwidth** → fits better in CPU pipelines.

---

## Parallel Complexity

### PRAM Model

| Algorithm | Work | Span (depth) |
|-----------|------|--------------|
| Sequential Quick Sort | Θ(n log n) | Θ(n log n) |
| Naïve parallel (sort halves on threads) | Θ(n log n) avg | Θ(n) avg (partition is sequential) |
| Parallel partition + parallel sort | Θ(n log n) | Θ(log² n) |

### Scaling Issues

- **Imbalanced partitions** cause uneven thread loads.
- **Synchronization** at small sub-array sizes is costly.

In practice, parallel Quick Sort achieves 3-5× speedup on 8 cores. Java prefers parallel Merge Sort (`Arrays.parallelSort`) for these reasons.

---

## Comparison

| Sort | Worst Time | Avg Time | Space | Stable | Cache | Comparisons |
|------|-----------|----------|-------|--------|-------|-------------|
| Quick Sort | Θ(n²) | Θ(n log n) | Θ(log n) | No | Best | 1.39 n log n |
| Merge Sort | Θ(n log n) | Θ(n log n) | Θ(n) | Yes | OK | 1.0 n log n |
| Heap Sort | Θ(n log n) | Θ(n log n) | Θ(1) | No | Bad | 2.0 n log n |
| Pdqsort | Θ(n log n) | Θ(n log n) | Θ(log n) | No | Best | ~1.05 n log n |
| Introsort | Θ(n log n) | Θ(n log n) | Θ(log n) | No | Best | ~1.4 n log n |
| Dual-Pivot | Θ(n²) (rare) | Θ(n log n) | Θ(log n) | No | Best | ~0.95 n log n |

**Pdqsort** offers worst-case O(n log n), best-in-class average performance, and cache locality. The current state of the art.

---

## Summary

At professional level, Quick Sort's **O(n log n) average** and **O(n²) worst** are derived from the partition recurrence and the comparison count formula `~1.39 n log₂ n`. **Randomized pivot selection** gives high-probability O(n log n) with adversarial-resistance guarantees. **Quickselect** finds the k-th smallest in O(n) average, O(n) deterministic with median-of-medians. **Cache-oblivious behavior** matches the optimal bound. **Parallel Quick Sort** is theoretically O(log² n) span but practically limited by partition imbalance.

The modern variants — **Pdqsort, Dual-Pivot, Introsort** — combine Quick Sort's average-case speed with Heap Sort's worst-case guarantee, eliminating the O(n²) tail. They are the production sort for in-memory data in every major systems language.
