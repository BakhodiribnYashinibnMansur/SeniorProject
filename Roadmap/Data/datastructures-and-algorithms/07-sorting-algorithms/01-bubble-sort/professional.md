# Bubble Sort — Mathematical Foundations and Complexity Theory

## Table of Contents

1. [Formal Definition](#formal-definition)
2. [Correctness Proof — Loop Invariants](#correctness-proof)
3. [Lower Bound for Comparison Sorts](#lower-bound-for-comparison-sorts)
4. [Inversion Counting and Optimality of Adjacent-Swap Sorts](#inversion-counting-and-optimality)
5. [Amortized Analysis (Why It Doesn't Help Here)](#amortized-analysis)
6. [Average-Case Analysis via Permutations](#average-case-analysis)
7. [Sorting Networks: 0–1 Principle](#sorting-networks-01-principle)
8. [Cache-Oblivious Analysis](#cache-oblivious-analysis)
9. [Parallel Complexity](#parallel-complexity)
10. [Comparison with Alternatives](#comparison)
11. [Summary](#summary)

---

## Formal Definition

```text
Definition: A sorting algorithm SORT for arrays over a totally ordered set (S, ≤)
is a function SORT : S* → S* such that for every input array A:
  1. Permutation property: SORT(A) is a permutation of A.
  2. Order property:       For all i < j: SORT(A)[i] ≤ SORT(A)[j].

BUBBLE-SORT(A):
  n ← length(A)
  for i ← 1 to n-1:
      for j ← 1 to n-i:
          if A[j] > A[j+1]:
              SWAP(A[j], A[j+1])

Invariant I_outer(i): At the start of iteration i+1 of the outer loop,
                     the subarray A[n-i+1 .. n] contains the i largest
                     elements of A in non-decreasing order, AND every
                     element in A[1 .. n-i] is ≤ every element in A[n-i+1 .. n].
```

This is the textbook (CLRS) definition with 1-based indexing. We use 0-based in code.

---

## Correctness Proof — Loop Invariants

### Inner Loop Invariant

**Invariant I_inner(j):** At the start of iteration `j+1` of the inner loop in pass `i`, `A[1 .. j]` contains some elements of the original array (some may have been swapped from later positions), and `A[j]` is the maximum of `A[1 .. j]` for that pass.

**Base (j=1):** `A[1]` is trivially the maximum of `{A[1]}`. ✓

**Inductive step:** Assume `A[j]` is the max of `A[1 .. j]`. The next iteration compares `A[j]` with `A[j+1]`:
- If `A[j] ≤ A[j+1]`: don't swap. `A[j+1]` is now the max of `A[1 .. j+1]` because either `A[j+1] > A[j]` (already max) or `A[j+1] = A[j]` (still max).
- If `A[j] > A[j+1]`: swap. After swap, `A[j+1]` (formerly `A[j]`) holds the max.

In both cases, `A[j+1]` is the max of `A[1 .. j+1]` after the iteration. ✓

**Termination:** When `j = n-i+1`, `A[n-i+1]` holds the max of `A[1 .. n-i+1]` — i.e., the maximum of the unsorted prefix.

### Outer Loop Invariant

**Invariant I_outer(i):** At the start of outer iteration `i+1` (i ≥ 0), the subarray `A[n-i+1 .. n]` contains the `i` largest elements of `A` in sorted order, AND `max(A[1 .. n-i]) ≤ min(A[n-i+1 .. n])`.

**Base (i=0):** `A[n+1 .. n]` is empty; the conditions hold vacuously. ✓

**Inductive step:** Assume I_outer(i). At outer iteration `i+1`, the inner loop bubbles `max(A[1 .. n-i])` to position `n-i`. By inductive hypothesis, this maximum is the `(i+1)`-th largest of A. After the inner loop:
- `A[n-i .. n]` contains the `i+1` largest in sorted order (the `(i+1)`-th largest is at `A[n-i]`, and `A[n-i+1 .. n]` was already sorted by IH).
- `max(A[1 .. n-i-1]) ≤ A[n-i]` because `A[n-i]` was the max of `A[1 .. n-i]` after the bubble.

So I_outer(i+1) holds. ✓

**Termination:** When `i = n-1`, `A[2 .. n]` contains the `n-1` largest in sorted order, and `A[1] ≤ A[2]`. Therefore A is fully sorted.

QED.

---

## Lower Bound for Comparison Sorts

### Theorem (Decision Tree Lower Bound)

Any comparison-based sorting algorithm requires **Ω(n log n)** comparisons in the worst case.

**Proof sketch:** A comparison sort can be modeled as a binary decision tree where each internal node is a comparison `A[i] : A[j]` and each leaf corresponds to a permutation of the input.

- There are `n!` possible input permutations.
- Each must lead to a distinct leaf (the algorithm must distinguish them).
- A binary tree with L leaves has depth ≥ ⌈log₂ L⌉.

Therefore depth ≥ log₂(n!) = Θ(n log n) by Stirling's approximation.

### Implication for Bubble Sort

Bubble Sort uses Θ(n²) comparisons in the worst case — worse than the lower bound. This is **not** because the lower bound is loose; it's because Bubble Sort is **restricted** to adjacent swaps, which makes it **suboptimal among comparison sorts**.

### Sub-Theorem: Lower Bound for Adjacent-Swap-Only Sorts

Any sorting algorithm restricted to adjacent swaps requires **Ω(n²)** swaps in the worst case.

**Proof:** Each adjacent swap removes at most 1 inversion. A reverse-sorted array of size `n` has `n(n-1)/2` inversions. So at least `n(n-1)/2 = Θ(n²)` swaps are required. Bubble Sort, Insertion Sort (with swaps), and Cocktail Sort all achieve this bound — they are **optimal among adjacent-swap sorts**.

---

## Inversion Counting and Optimality

### Definition

An **inversion** in array A is a pair `(i, j)` with `i < j` and `A[i] > A[j]`. Let `inv(A)` denote the number of inversions in A.

| Property | Value |
|----------|-------|
| Min inversions | 0 (sorted) |
| Max inversions | n(n-1)/2 (reverse sorted) |
| Expected inversions in random permutation | n(n-1)/4 |

### Theorem: Bubble Sort Performs Exactly inv(A) Swaps

**Proof:** Each Bubble Sort swap exchanges adjacent elements `A[j]` and `A[j+1]` where `A[j] > A[j+1]` — i.e., it removes the inversion `(j, j+1)`.

We claim no other inversion is created or destroyed:
- Inversions involving indices outside `{j, j+1}` are unchanged (those elements don't move).
- Inversions of the form `(k, j)` with `k < j`: pair becomes `(k, j)` referring to the new `A[j]` (former `A[j+1]`). The `≤/>` status against `A[k]` may change either way, but those are inversions with `j` which are tracked separately... 

Hmm, this argument is subtle. The cleaner statement: **net change in inv(A) per swap = -1**.

**Cleaner proof:** Consider the swap of `A[j]` (= a) and `A[j+1]` (= b) where `a > b`. After the swap:
- Pairs `(i, k)` with `i, k ∉ {j, j+1}`: unchanged (no element moved).
- Pairs involving exactly one of `j, j+1`: these are pairs `(k, j)` with `k < j` and pairs `(j+1, k)` with `k > j+1`. Each such pair compares some `A[k]` with one of `{a, b}`. Since `a > b`, swapping which element is at position `j` vs `j+1` doesn't change whether `A[k]` is greater or less than the value — it changes which inversion-direction we count. The number of inversions involving position `j` (with elements outside `{j, j+1}`) stays the same.
- Pair `(j, j+1)`: before = inverted (since `a > b`); after = not inverted. **Net change: -1.**

Therefore `inv(A)` decreases by exactly 1 per swap. Bubble Sort terminates when `inv(A) = 0`. So total swaps = initial `inv(A)`.

QED.

### Corollary: Bubble Sort is Swap-Optimal

Any adjacent-swap-only sort must perform at least `inv(A)` swaps. Bubble Sort performs exactly `inv(A)`. Therefore Bubble Sort is optimal in swap count among adjacent-swap-only sorts.

(Note: Insertion Sort with swap-based implementation also achieves this bound.)

---

## Amortized Analysis

Bubble Sort doesn't benefit from amortized analysis — every operation is O(n) per pass, and there's no "expensive but rare" operation to amortize. The Aggregate Method just confirms the obvious:

```text
Total cost = Σ (cost of pass i) = Σ (n-i) for i=0 to n-2 = n(n-1)/2 = O(n²)
Amortized per pass = O(n) — same as actual.
Amortized per element = O(n) — same as worst.
```

The Potential Method is similarly uninformative because there's no slack to redistribute. By contrast, dynamic-array push has O(1) amortized cost despite O(n) worst-case resize — that's where amortization shines.

**Lesson:** Amortized analysis helps when expensive operations are rare. Bubble Sort's expensive operations are constant — every pass is O(n).

---

## Average-Case Analysis

**Setup:** Assume input is a uniformly random permutation of `n` distinct elements.

### Expected Number of Inversions

Let `X_{ij}` (for `i < j`) be the indicator that `(i, j)` is an inversion. Then:

```text
E[inv(A)] = Σ_{i<j} E[X_{ij}] = Σ_{i<j} Pr[A[i] > A[j]]
         = Σ_{i<j} 1/2  (uniform: each pair equally likely either order)
         = (1/2) · C(n, 2)
         = n(n-1)/4
```

So **expected number of swaps in Bubble Sort on random input = n(n-1)/4**.

### Expected Number of Comparisons

Without early exit: always n(n-1)/2 comparisons.

With early exit: depends on when the array becomes "passes-only-swap-free." For random input, asymptotically still Θ(n²) — early exit usually saves only a constant fraction.

### Expected Number of Passes

The number of passes equals 1 + (length of the longest "left-to-right migration" needed). For a random permutation, the smallest element's expected position is `n/2`, so it needs ~`n/2` passes to reach position 0 — confirming Θ(n) passes on average.

---

## Sorting Networks: 0–1 Principle

### The 0–1 Principle (Knuth)

> A sorting network sorts every input correctly **if and only if** it sorts every binary (0–1) input correctly.

**Why this matters:** Verifying sorting correctness on `n!` inputs is intractable. The 0–1 principle reduces verification to `2^n` binary inputs — still exponential, but tractable for small n.

### Application to Odd-Even Transposition Sort

For odd-even transposition sort with `n` inputs and `n` phases (depth O(n) parallel time):

1. Verify on all `2^n` binary inputs.
2. By the 0–1 principle, correctness follows for arbitrary inputs.

This is how parallel sorting algorithm correctness is rigorously verified.

### Bubble Sort as a Sorting Network

Bubble Sort is **not** a sorting network in the strict sense because the early-exit optimization makes it data-dependent. The naive (no-early-exit) version IS a sorting network: it's a sequence of fixed comparators.

Network depth: `n(n-1)/2` (sequential).
Network size (total comparators): `n(n-1)/2`.

By contrast:
- **AKS sorting network**: O(log n) depth, O(n log n) comparators — but constant factor is huge (impractical).
- **Batcher's bitonic sort**: O(log² n) depth, O(n log² n) comparators — practical for GPU/FPGA.
- **Odd-even merge sort**: similar to bitonic — O(log² n) depth.

---

## Cache-Oblivious Analysis

```text
Parameters: N = problem size, M = cache size, B = block (cache line) size

Bubble Sort cache behavior:
  - One pass scans N consecutive elements: N/B cache misses.
  - Total passes: N (worst case).
  - Total cache misses: O(N²/B).

Compare to optimal cache-oblivious sort (e.g., Funnelsort, Lazy Funnelsort):
  - Cache misses: O((N/B) · log_{M/B}(N/B))
  - For typical N=10⁶, B=64, M=10⁵: ~16k misses for optimal vs. 10¹⁰/64 ≈ 1.5·10⁸ for bubble.

Ratio: Bubble Sort is ~10⁴× worse in cache misses than cache-optimal.

External sorting (data > RAM):
  - Bubble Sort: O(N²/B) disk seeks — utterly impractical for any N > RAM.
  - Merge Sort with B-way merge: O((N/B) · log_{M/B}(N/B)) — the foundation of database sort.
```

**Implication:** For external/disk-based sorting, even the constant factors of Bubble Sort are catastrophic. Database systems and big-data engines never use Bubble Sort or its variants.

---

## Parallel Complexity

### Sequential Bubble Sort

Sequential time: Θ(n²).

### Parallel Bubble Sort (Odd-Even Transposition)

With `p ≤ n` processors:

```text
Parallel time T_p(n) = Θ(n²/p + n)
                       └─ work │  └─ depth (number of phases)
```

- With `p = 1`: T = Θ(n²) — back to sequential.
- With `p = n`: T = Θ(n) — linear time.
- With `p = n²`: still Θ(n) — depth is the bottleneck, not work.

**PRAM model:** Odd-even transposition is in NC₁ if we relax to O(n) parallel depth — though strictly NC requires polylog depth.

**True NC sorts:**
- AKS network: O(log n) depth — theoretically optimal but huge constant.
- Cole's parallel merge sort: O(log n) — practical foundation of NC sorting.

---

## Comparison with Alternatives (Theoretical)

| Algorithm | Worst Time | Avg Time | Worst Space | Cache Misses | Stable | Parallel Depth |
|-----------|-----------|----------|-------------|--------------|--------|----------------|
| Bubble Sort | Θ(n²) | Θ(n²) | Θ(1) | O(n²/B) | Yes | Θ(n²) seq, Θ(n) parallel |
| Insertion Sort | Θ(n²) | Θ(n²) | Θ(1) | O(n²/B) | Yes | Θ(n²) seq |
| Selection Sort | Θ(n²) | Θ(n²) | Θ(1) | O(n²/B) | No | Θ(n²) seq |
| Merge Sort | Θ(n log n) | Θ(n log n) | Θ(n) | O((n/B)log(n/M)) | Yes | Θ(log² n) parallel |
| Quick Sort (avg) | Θ(n²) | Θ(n log n) | Θ(log n) | O((n/B)log(n/M)) | No | Θ(log² n) parallel (avg) |
| Heap Sort | Θ(n log n) | Θ(n log n) | Θ(1) | O(n log n) | No | Θ(log² n) parallel |
| Bitonic Sort | Θ(n log² n) | Θ(n log² n) | Θ(1) network | O((n/B)log²n) | Yes | Θ(log² n) |
| AKS Network | Θ(log n) | Θ(log n) | Θ(1) network | impractical | Yes | Θ(log n) |

---

## Summary

At professional level, Bubble Sort is a complete, well-understood case study in algorithm analysis:

- **Correctness** is proven by nested loop invariants — the cleanest example of structural induction in sorting.
- **Optimality** within the class of adjacent-swap algorithms is proven via inversion counting (it does the minimum possible swaps for that class).
- **Suboptimality** in the comparison-sort hierarchy is established by the Ω(n log n) decision-tree lower bound.
- **Cache behavior** is O(n²/B) — bad in absolute terms but predictable.
- **Parallelism** redeems it: odd-even transposition sort achieves Θ(n) time on n processors and forms the basis of sorting networks that run on GPUs, FPGAs, and homomorphic-encryption pipelines.
- **Sorting Networks** (built from bubble-style comparators) are verified via the 0–1 principle and underpin oblivious / hardware sorting.

The algorithm's pedagogical value is permanent; its production utility is essentially zero outside the sorting-network niche.

> **Key takeaway:** Bubble Sort is what you teach to demonstrate that O(n²) sorts exist and to motivate the search for O(n log n) algorithms. Once Insertion Sort, Merge Sort, and Quick Sort exist, Bubble Sort retires to textbooks and sorting networks.
