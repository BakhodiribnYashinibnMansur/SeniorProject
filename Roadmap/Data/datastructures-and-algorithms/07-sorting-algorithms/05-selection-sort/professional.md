# Selection Sort — Mathematical Foundations

## Table of Contents
1. [Formal Definition](#formal-definition)
2. [Correctness Proof](#correctness-proof)
3. [Comparison and Swap Counts](#comparison-and-swap-counts)
4. [Lower Bound on Swaps](#lower-bound-on-swaps)
5. [Cache and Parallel Analysis](#cache-and-parallel)
6. [Comparison](#comparison)
7. [Summary](#summary)

---

## Formal Definition

```text
SELECTION-SORT(A):
  for i = 0 to n - 2:
    min_idx = i
    for j = i + 1 to n - 1:
      if A[j] < A[min_idx]:
        min_idx = j
    if min_idx != i:
      exchange A[i] with A[min_idx]
```

---

## Correctness Proof

**Loop Invariant (outer):** At the start of iteration `i`, `A[0..i-1]` contains the i smallest elements of A in sorted order, and `max(A[0..i-1]) ≤ min(A[i..n-1])`.

**Base (i=0):** `A[0..-1]` is empty. Vacuously true.

**Maintenance:** Inner loop finds the index of `min(A[i..n-1])`. Swap places this minimum at position `i`. After swap:
- `A[0..i]` contains the i+1 smallest in sorted order.
- `max(A[0..i]) ≤ min(A[i+1..n-1])`.

**Termination:** When `i = n-1`, `A[0..n-2]` contains the n-1 smallest in sorted order. The largest is at `A[n-1]`. Sorted.

QED.

---

## Comparison and Swap Counts

### Comparisons (always)

```text
C(n) = (n-1) + (n-2) + ... + 1 = n(n-1)/2
```

Independent of input — always ~n²/2.

### Swaps

```text
Swaps(n) ≤ n - 1
```

At most one swap per outer iteration. On already-sorted input, zero swaps; on reverse-sorted, n-1 swaps.

### Writes

If we count assignments (read-modify-write) instead of swaps:
- Each swap = 3 writes.
- Total writes ≤ 3(n-1).

Compare:
- **Bubble Sort writes:** ~3 × n²/4.
- **Insertion Sort writes:** ~n²/4 (each shift is one write).
- **Selection Sort writes:** ≤ 3(n-1) — **linear in n**.

This is Selection Sort's mathematical advantage.

---

## Lower Bound on Swaps

**Theorem:** Any sorting algorithm requires at least ⌈n/2⌉ swaps in the worst case.

**Proof:** Consider input where each element needs to be swapped to a different position (no fixed points). Each swap can "fix" at most 2 elements (move both to correct positions simultaneously). So at least n/2 swaps are needed.

Selection Sort achieves at most n-1 swaps — within a factor of 2 of optimal. **Cycle Sort** achieves the exact minimum but is more complex.

---

## Cache and Parallel

### Cache Misses

Selection Sort scans `A[i..n-1]` each pass. Sequential read pattern → cache-friendly within a pass. Total cache misses: O(n²/B). Same as Bubble/Insertion.

### Parallel Complexity

Selection Sort is inherently sequential — each pass depends on the result of the previous. Parallelizing the min-find within a pass gives:

```text
T_p(n) = O(n²/p + n)    with p processors
```

With p = n: T = O(n) — but heap-based parallel selection achieves better constants.

---

## Comparison

| Sort | Worst Time | Avg Time | Swaps (worst) | Cache | Stable |
|------|-----------|----------|---------------|-------|--------|
| Selection | Θ(n²) | Θ(n²) | **n-1** | OK | No |
| Bubble | Θ(n²) | Θ(n²) | n²/2 | OK | Yes |
| Insertion | Θ(n²) | Θ(n²) | n²/4 (shifts) | OK | Yes |
| Heap | Θ(n log n) | Θ(n log n) | n log n | Bad | No |
| Cycle Sort | Θ(n²) | Θ(n²) | **min possible** | OK | No |

---

## Summary

Selection Sort: **always Θ(n²)** comparisons, **n-1 swaps**, **O(1) space**. Provably near-optimal in swap count (within factor 2 of cycle sort). Use when writes are expensive. **Heap Sort** generalizes to O(n log n) by using a heap for O(log n) min-find while preserving the few-writes intuition.
