# Merge Sort — Mathematical Foundations and Complexity Theory

## Table of Contents

1. [Formal Definition](#formal-definition)
2. [Correctness Proof — Loop Invariants](#correctness-proof)
3. [Master Theorem Application](#master-theorem-application)
4. [Tight Comparison Bound](#tight-comparison-bound)
5. [Lower Bound Match — Comparison Sort Optimality](#lower-bound-match)
6. [Amortized Analysis (External Sort)](#amortized-analysis)
7. [Cache-Oblivious Optimality](#cache-oblivious-optimality)
8. [Parallel Complexity](#parallel-complexity)
9. [I/O Complexity](#io-complexity)
10. [Comparison with Alternatives](#comparison)
11. [Summary](#summary)

---

## Formal Definition

```text
Definition: Merge Sort is the comparison sort defined recursively as:

MERGE-SORT(A, p, r):
    if p < r:
        q ← ⌊(p + r) / 2⌋
        MERGE-SORT(A, p, q)
        MERGE-SORT(A, q + 1, r)
        MERGE(A, p, q, r)

MERGE(A, p, q, r):
    n₁ ← q − p + 1
    n₂ ← r − q
    Allocate L[1..n₁ + 1] and R[1..n₂ + 1]
    for i ← 1 to n₁: L[i] ← A[p + i − 1]
    for j ← 1 to n₂: R[j] ← A[q + j]
    L[n₁ + 1] ← ∞;  R[n₂ + 1] ← ∞    // sentinels
    i ← 1; j ← 1
    for k ← p to r:
        if L[i] ≤ R[j]:
            A[k] ← L[i]; i ← i + 1
        else:
            A[k] ← R[j]; j ← j + 1
```

This is the CLRS reference form using sentinels — eliminates the "leftover" loops.

### Postcondition

For input A[1..n]:
- A is a permutation of its initial value.
- A[1] ≤ A[2] ≤ ... ≤ A[n].
- For any pair (i, j) with i < j and A[i] = A[j] in the original, in the output the element originally at position i still appears before the element originally at position j (stability).

---

## Correctness Proof — Loop Invariants

### MERGE Correctness

**Loop Invariant (MERGE):** At the start of each iteration of the for-k loop:
- The subarray A[p..k − 1] contains the k − p smallest elements of L[1..n₁+1] ∪ R[1..n₂+1] in sorted order.
- L[i] and R[j] are the smallest elements of L and R that have not been copied to A.

**Initialization (k = p):** A[p..p − 1] is empty. The k − p = 0 smallest elements have been placed (vacuously). L[1] and R[1] are by definition the smallest of L and R. ✓

**Maintenance:** Suppose the invariant holds at iteration k. We pick `min(L[i], R[j])` and place it at A[k]. Since by IH L[i] and R[j] are the smallest unused, the chosen one is the (k − p + 1)-th smallest overall. Now A[p..k] contains the k − p + 1 smallest in sorted order. Either i or j increments, maintaining the "smallest unused" property. ✓

**Termination:** When k = r + 1, A[p..r] contains all r − p + 1 elements in sorted order. Sentinels (∞) ensure the loop terminates without out-of-bounds access. ✓

### MERGE-SORT Correctness

**Inductive proof on n = r − p + 1.**

**Base (n ≤ 1):** Single element or empty range — trivially sorted.

**Inductive step:** Assume MERGE-SORT correctly sorts ranges of size < n. For range of size n:
1. We split at q = ⌊(p + r) / 2⌋.
2. Left half (size ⌈n/2⌉) is sorted by IH.
3. Right half (size ⌊n/2⌋) is sorted by IH.
4. MERGE produces a sorted range of size n.

QED.

---

## Master Theorem Application

### Recurrence

```text
T(n) = 2 · T(n/2) + Θ(n)
T(1) = Θ(1)
```

### Master Theorem (CLRS 4.5)

For T(n) = a·T(n/b) + f(n) with a ≥ 1, b > 1:

| Case | Condition | Solution |
|------|-----------|----------|
| 1 | f(n) = O(n^(c₁)) where c₁ < log_b a | T(n) = Θ(n^(log_b a)) |
| 2 | f(n) = Θ(n^(c) · log^k n), c = log_b a, k ≥ 0 | T(n) = Θ(n^c · log^(k+1) n) |
| 3 | f(n) = Ω(n^(c₂)) where c₂ > log_b a, AND af(n/b) ≤ kf(n) for some k < 1 | T(n) = Θ(f(n)) |

### Application

a = 2, b = 2, f(n) = Θ(n). Compute log_b(a) = log₂(2) = 1. f(n) = Θ(n¹ · log⁰ n) → **Case 2 with c = 1, k = 0**.

T(n) = Θ(n^1 · log^(0+1) n) = **Θ(n log n)**.

### Akra-Bazzi Generalization

For non-balanced splits:
```text
T(n) = Σᵢ aᵢ · T(bᵢ · n) + g(n)
```

For unbalanced merge sort (split 1/3 vs 2/3):
```text
T(n) = T(n/3) + T(2n/3) + Θ(n)
```

By Akra-Bazzi: find p such that (1/3)^p + (2/3)^p = 1. Solution: p = 1. So T(n) = Θ(n^p · log n) = Θ(n log n) — same Big-O!

Even with skewed splits, merge sort stays O(n log n).

---

## Tight Comparison Bound

### Number of Comparisons

The exact worst-case comparison count for top-down merge sort on n elements is bounded by:
```text
C(n) ≤ n · ⌈log₂ n⌉ − 2^⌈log₂ n⌉ + 1
```

For n = 2^k (power of 2):
```text
C(2^k) = k · 2^k − 2^k + 1 = (k − 1) · 2^k + 1
```

Concretely: C(8) = 17, C(16) = 49, C(32) = 129, C(1024) = 9217.

### Best-Case Comparisons

Even on already-sorted input, vanilla merge sort makes Θ(n log n) comparisons (it doesn't detect the ordering). TimSort detects runs and achieves O(n) on sorted input.

### Average-Case Comparisons

The average is approximately n log₂ n − 1.2645 · n + O(log n). Within ~1.5n of the worst case for typical n.

---

## Lower Bound Match

### Theorem

Any deterministic comparison sort requires Ω(n log n) comparisons in the worst case.

### Proof (Decision Tree)

A comparison sort can be modeled as a binary decision tree:
- Internal nodes: comparisons of the form A[i] : A[j].
- Leaves: distinct outputs (one per permutation).

For input of size n, there are n! permutations. Each must lead to a unique leaf. A binary tree with L leaves has depth ≥ ⌈log₂ L⌉ = log₂(n!).

By Stirling's approximation: log₂(n!) = n log₂ n − n / ln 2 + O(log n) = Θ(n log n).

### Optimality

Merge Sort achieves O(n log n) comparisons → Merge Sort is **asymptotically optimal** among comparison sorts.

The constant factor is also near-optimal: the information-theoretic lower bound on comparisons is `⌈log₂(n!)⌉`, and Merge Sort uses about 1.05× this for large n.

---

## Amortized Analysis

For external merge sort, the amortized I/O cost per element is meaningful:

```text
Total I/Os = O(N · log_k(N/M))    where:
    N = total data size
    M = memory size
    k = merge fanout
    
Amortized I/O per element = O(log_k(N/M))

For N = 1 TB, M = 16 GB, k = 16:
    log_k(N/M) = log_16(64) = log_16(2^6) = 6/4 = 1.5 passes
    Round up to 2 passes
    Total I/O = 4N (read + write per pass × 2 passes)
    Per-element: 4 read/write operations on average.
```

This amortized view drives the design of database query optimizers — they estimate the cost of `ORDER BY` based on this formula.

---

## Cache-Oblivious Optimality

### Cache Complexity (Frigo, Leiserson et al., 1999)

Standard recursive Merge Sort is **cache-oblivious** — its access pattern is optimal even though the algorithm doesn't know cache parameters.

```text
Q(n) = O((n/B) · log_{M/B}(n/B))
where:
    B = cache line size (typical: 64 bytes)
    M = cache size
```

This matches the lower bound for any comparison sort (Aggarwal & Vitter, 1988):
```text
Ω((n/B) · log_{M/B}(n/B)) cache misses
```

### Comparison

| Sort | Cache misses |
|------|--------------|
| Merge Sort | O((n/B) · log_{M/B}(n/B)) ← **optimal** |
| Quick Sort (in-place) | O((n/B) · log(n/M)) ← also optimal-ish |
| Heap Sort | O(n log n / B) ← suboptimal |
| Bubble/Insertion Sort | O(n²/B) ← terrible |

### Why Merge Sort Achieves It

- Each merge accesses two sorted runs sequentially → optimal prefetching.
- Recursive subproblems eventually fit in cache → no cache misses for small enough subproblems.
- The recursion tree's leaves are in cache; only the merging at higher levels causes misses.

### Funnel Sort

Funnel sort is an even more cache-oblivious sort. It uses a k-way merge "funnel" that's recursively defined to fit in cache. Achieves O((n/B) · log_{M/B}(n/B)) with smaller constants than vanilla Merge Sort.

---

## Parallel Complexity

### PRAM Model

In the PRAM (Parallel Random Access Machine) model:

| Algorithm | Work (W) | Span / Depth (D) |
|-----------|----------|------------------|
| Sequential Merge Sort | O(n log n) | O(n log n) |
| Parallel Merge Sort (sequential merge) | O(n log n) | O(n) — bottlenecked by merge |
| Parallel Merge Sort with parallel merge | O(n log n) | O(log² n) |
| Cole's Parallel Merge Sort | O(n log n) | O(log n) |

### Cole's Parallel Merge Sort

Achieves O(n log n) work and O(log n) span. The key idea: pipeline the merging — start the next level's merge while the current level is still in progress. Considered the canonical "optimal parallel sort."

### Practical Parallel Merge Sort (ForkJoin)

Java's `Arrays.parallelSort` uses ForkJoin:
1. Split array into ~p chunks (p = number of cores).
2. Sort each chunk sequentially with TimSort.
3. Merge pairs in parallel: ⌈p/2⌉ merges, then ⌈p/4⌉, ...

Achieves T_p ≈ T_seq / p for large enough n. Speedup limited by:
- Memory bandwidth.
- Goroutine/thread overhead for small chunks.
- The final merges become serial bottlenecks.

---

## I/O Complexity

### External Memory Model (Aggarwal-Vitter)

Parameters:
- N = number of records on disk.
- M = number of records in main memory.
- B = number of records per disk block.

### Lower Bound

```text
Ω((N/B) · log_{M/B}(N/B))     I/Os for sorting
```

### Merge Sort Achieves It

Two-phase merge sort:
1. Read N/B blocks into M-record chunks → N/B I/Os to write sorted runs.
2. Merge in passes of M/B-way merge → log_{M/B}(N/M) passes, each with N/B I/Os.

Total: O((N/B) · (1 + log_{M/B}(N/M))) = **O((N/B) · log_{M/B}(N/B))** ← matches lower bound.

### Practical Numbers (PostgreSQL)

PostgreSQL's external sort:
- M = `work_mem` (default 4 MB; production tuning: 64 MB - 1 GB).
- B = 8 KB (default page size).
- Typical fan-out: 128-way merge (limited by file descriptor budget).

For 100 GB sort with 64 MB work_mem, B = 8 KB: ~1 pass of merging needed (because 100 GB / 64 MB = 1600 runs, fits in one 1600-way merge if FDs allow).

---

## Comparison with Alternatives (Theoretical)

| Algorithm | Worst Time | Avg Time | Space | I/O Complexity | Cache Misses | Stable | Parallel Depth |
|-----------|-----------|----------|-------|----------------|--------------|--------|----------------|
| Merge Sort | Θ(n log n) | Θ(n log n) | Θ(n) | O((n/B) log_{M/B}(n/B)) ✓ | O((n/B) log_{M/B}(n/B)) ✓ | Yes | O(log² n) |
| Quick Sort | Θ(n²) | Θ(n log n) | Θ(log n) | O((n/B) log(n/M)) | O((n/B) log(n/M)) | No (typical) | O(log² n) avg |
| Heap Sort | Θ(n log n) | Θ(n log n) | Θ(1) | O(n log n / B) | O(n log n / B) | No | Hard |
| TimSort | Θ(n log n) | Θ(n log n) (Θ(n) if sorted) | Θ(n) | similar to Merge Sort | similar to Merge Sort | Yes | Θ(log² n) |
| Pdqsort | Θ(n log n) | Θ(n log n) | Θ(log n) | similar to Quick Sort | similar to Quick Sort | No | Hard |
| Funnel Sort | Θ(n log n) | Θ(n log n) | Θ(n) | O((n/B) log_{M/B}(n/B)) ✓ | O((n/B) log_{M/B}(n/B)) ✓ | Yes | O(log² n) |

---

## Summary

At professional level, Merge Sort is the **theoretically optimal** comparison sort across all standard models:

- **Time:** Θ(n log n) matches the comparison-sort lower bound (decision tree argument).
- **I/O:** O((N/B) log_{M/B}(N/B)) matches the external-memory lower bound (Aggarwal-Vitter).
- **Cache:** O((n/B) log_{M/B}(n/B)) matches the cache-oblivious optimum.
- **Parallel:** Cole's variant achieves O(log n) span with O(n log n) work.

The Master Theorem (Case 2) gives the closed-form Θ(n log n). The recurrence T(n) = 2T(n/2) + Θ(n) is the canonical example for divide-and-conquer analysis. Even with unbalanced splits, the Akra-Bazzi method shows Θ(n log n) is preserved.

Merge Sort's optimality across so many models — sequential, external, cache, parallel — explains its persistence as the production-grade sort. Quick Sort beats it in average-case in-cache performance, but Merge Sort's *guarantees* are unmatched. That's why TimSort (a Merge Sort variant) runs in Python and Java — language designers prioritize predictability over peak speed.

> **Key insight:** Merge Sort is optimal for *every* model that matters at scale. Quick Sort is optimal only for *one* (in-memory random-access). When in doubt, choose Merge Sort.
