# Insertion Sort — Mathematical Foundations

## Table of Contents

1. [Formal Definition](#formal-definition)
2. [Correctness Proof — Loop Invariants](#correctness-proof)
3. [Adaptive Complexity Bound](#adaptive-complexity-bound)
4. [Optimality on Inversion Distance](#optimality-on-inversion-distance)
5. [Average-Case Analysis](#average-case-analysis)
6. [Cache and I/O Analysis](#cache-and-io-analysis)
7. [Comparison with Alternatives](#comparison)
8. [Summary](#summary)

---

## Formal Definition

```text
INSERTION-SORT(A):
    for i = 1 to length(A) - 1:
        key = A[i]
        j = i - 1
        while j >= 0 and A[j] > key:
            A[j + 1] = A[j]
            j = j - 1
        A[j + 1] = key
```

CLRS Chapter 2.1 reference form.

---

## Correctness Proof

### Outer Loop Invariant

At the start of iteration `i` of the outer for-loop:
- `A[0..i-1]` consists of the original elements but in sorted order.

**Initialization (i = 1):** `A[0..0]` = single element, trivially sorted.

**Maintenance:** Inner while-loop shifts elements `A[j+1..i-1]` (those greater than key) one position right; then key is placed at position `j+1`. By inner invariant, after termination `A[0..i]` is sorted (consisting of original elements).

**Termination:** When `i = length(A)`, `A[0..length(A)-1]` is sorted (the entire array).

### Inner Loop Invariant

At the start of each iteration of the inner while-loop:
- `A[j+2..i]` are all > key (have been shifted right).
- `A[0..j]` is unchanged from the start of the outer iteration.

QED.

---

## Adaptive Complexity Bound

**Theorem:** Insertion Sort runs in `Θ(n + I)` time where `I` = number of inversions in input.

**Proof:** Each iteration of the inner while-loop shifts one element right and decreases inversions by 1 (the inversion between `A[j]` and `A[i]` is resolved). Total shifts = total inversions = `I`. Outer loop iterates `n-1` times with O(1) overhead each. Total: `Θ(n + I)`.

For sorted input: I = 0 → Θ(n).
For reverse-sorted: I = n(n-1)/2 → Θ(n²).
For random: E[I] = n(n-1)/4 → Θ(n²).

This is the **adaptive bound** — better than fixed Θ(n²) and tight.

---

## Optimality on Inversion Distance

**Definition:** The **inversion distance** of an array is the minimum number of adjacent swaps (or shifts) required to sort it.

**Theorem:** Inversion distance = number of inversions.

**Proof:** Each adjacent swap removes ≤ 1 inversion (Bubble Sort's analysis). So at least `I` swaps are needed. Insertion Sort achieves exactly `I` shifts. Therefore Insertion Sort is **optimal among adjacent-shift-only sorts**.

This optimality is shared with Bubble Sort and Cocktail Sort.

---

## Average-Case Analysis

For a uniformly random permutation of n distinct elements:

```text
E[number of inversions] = Σ_{i<j} Pr[A[i] > A[j]]
                        = Σ_{i<j} 1/2
                        = (1/2) · C(n,2)
                        = n(n-1)/4
                        ≈ n²/4
```

So **average shifts ≈ n²/4** and **average comparisons ≈ n²/4 + n** (one extra comparison per outer iteration).

Compare to Bubble Sort: average swaps n²/4, average comparisons n²/2 (it always compares all adjacent pairs in each pass). Insertion Sort wins on comparisons.

---

## Cache and I/O Analysis

### Cache Misses

Insertion Sort accesses `A[j]` and `A[j+1]` (adjacent) → highly sequential.

Cache misses: `Θ(n²/B)` where B = cache line size.

For n ≤ M (cache size in elements): all data fits in cache → comparable to in-memory bandwidth.

For n > M: cache misses dominate but pattern is still sequential → prefetcher helps.

### I/O Complexity

For external memory: Θ(n²/B) — same as Bubble. Catastrophically slow for n > RAM. Use external merge sort instead.

---

## Comparison

| Sort | Adaptive | Best | Avg | Worst | Stable | Inversion-optimal |
|------|----------|------|-----|-------|--------|-------------------|
| Insertion | **Yes** | Θ(n) | Θ(n²) | Θ(n²) | Yes | Yes |
| Bubble | Yes (modest) | Θ(n) | Θ(n²) | Θ(n²) | Yes | Yes |
| Selection | No | Θ(n²) | Θ(n²) | Θ(n²) | No | No |
| Merge | No | Θ(n log n) | Θ(n log n) | Θ(n log n) | Yes | No (uses moves) |
| TimSort | Yes (very) | Θ(n) | Θ(n log n) | Θ(n log n) | Yes | Approximately |

---

## Summary

Insertion Sort is **adaptive optimal**: runs in Θ(n + I) where I = inversions. **Optimal among adjacent-shift sorts**. Average Θ(n²) comparisons and shifts on random input — half of Bubble Sort's comparisons. **Stable** with strict `>` comparison. **The pedagogically and theoretically clean** representative of the O(n²) family, and the **production small-array primitive** in TimSort, Pdqsort, and Java's Dual-Pivot Quicksort.
