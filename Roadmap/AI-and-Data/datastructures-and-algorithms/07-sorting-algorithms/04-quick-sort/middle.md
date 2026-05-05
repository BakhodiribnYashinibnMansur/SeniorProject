# Quick Sort — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Pivot Strategies](#pivot-strategies)
3. [Partition Schemes Comparison](#partition-schemes-comparison)
4. [Recurrence Analysis](#recurrence-analysis)
5. [Worst-Case Avoidance](#worst-case-avoidance)
6. [3-Way Partition (Dutch National Flag)](#3-way-partition)
7. [Variants](#variants)
8. [Pdqsort Deep Dive](#pdqsort-deep-dive)
9. [Code Examples](#code-examples)
10. [Performance Analysis](#performance-analysis)
11. [Cache and Memory](#cache-and-memory)
12. [Summary](#summary)

---

## Introduction

> Focus: "How do production Quick Sorts avoid O(n²)?"

At middle level, you understand the partition mechanics in detail, you've memorized Lomuto AND Hoare schemes, and you know how production variants (Pdqsort, Dual-Pivot, Introsort) eliminate the O(n²) worst case. You also understand 3-way partition for handling duplicates and median-of-three pivot for resisting sorted-input attacks.

---

## Pivot Strategies

| Strategy | Complexity | Resistance to bad input |
|----------|-----------|------------------------|
| First element | O(n²) on sorted | None |
| Last element | O(n²) on sorted | None |
| Middle element | O(n²) on specific patterns | Low |
| Random | Expected O(n log n) | High |
| Median-of-three | O(n log n) on most | Medium-high |
| Median-of-medians | O(n log n) deterministic | Total |
| **Pdqsort heuristic** | O(n log n) in practice + introsort fallback | Total |

### Median-of-Three

Pick the median of `arr[lo]`, `arr[mid]`, `arr[hi]` as pivot:

```python
def median_of_three(arr, lo, hi):
    mid = (lo + hi) // 2
    if arr[lo] > arr[mid]: arr[lo], arr[mid] = arr[mid], arr[lo]
    if arr[lo] > arr[hi]:  arr[lo], arr[hi]  = arr[hi], arr[lo]
    if arr[mid] > arr[hi]: arr[mid], arr[hi] = arr[hi], arr[mid]
    # Now arr[lo] <= arr[mid] <= arr[hi]
    return mid  # use arr[mid] as pivot
```

**Effect:** Eliminates worst case on sorted/reverse-sorted input. ~10% faster than first-element pivot in practice.

### Ninther (median of three medians)

For very large arrays, take the median of three median-of-three candidates from the start, middle, and end. Even more robust.

### Pdqsort's Pivot Heuristic

1. For small arrays: use median-of-three.
2. For larger arrays: median-of-five or median-of-medians.
3. Detects "patterns" (e.g., sorted, reverse-sorted) → uses different strategy.
4. Falls back to **Heap Sort** if recursion depth exceeds 2 log n (introsort behavior).

---

## Partition Schemes Comparison

### Lomuto

- One pointer scans left to right.
- Swaps "≤ pivot" elements to the left region.
- Pivot is at the boundary at the end.
- ~3× more swaps than Hoare on average.
- Simpler to understand and prove correct.
- Used in CLRS textbook.

### Hoare (original)

- Two pointers from both ends.
- Swaps mismatched pairs.
- Returns an index inside the partition (not pivot index).
- Fewer swaps; faster in practice.
- Used in production (Pdqsort, etc.).

### Block Partition

Modern variant that batches comparisons before swaps to avoid branch mispredictions. Used in Pdqsort. 30-50% faster on random data.

```text
Standard partition: read, compare, swap (branchy)
Block partition:    
  buffer compare results for 64 elements
  perform all swaps in a single batch
```

---

## Recurrence Analysis

### Best Case (balanced partitions)

```text
T(n) = 2 · T(n/2) + Θ(n)
By Master Theorem (Case 2): T(n) = Θ(n log n)
```

### Worst Case (max imbalance)

```text
T(n) = T(n-1) + T(0) + Θ(n)
     = T(n-1) + Θ(n)
     = Θ(n²)
```

This is what happens with first-element pivot on sorted input.

### Average Case (random pivot)

Expected partition split: pivot lands at random position from 1 to n. Recurrence:
```text
T(n) = (1/n) · Σ_{k=0}^{n-1} (T(k) + T(n-1-k)) + Θ(n)
```

Solving: `T(n) ≈ 2n ln n ≈ 1.39 n log₂ n`. So Quick Sort does ~39% more comparisons than the information-theoretic minimum, but still O(n log n).

### Comparison Count Formula

For random input, expected comparisons:
```text
C(n) = 2(n+1)·H_n - 4n  ≈  1.39 n log₂ n
```

where H_n = harmonic number. Slightly worse than Merge Sort's `n log₂ n`, but cache locality more than compensates.

---

## Worst-Case Avoidance

### Strategy 1: Random Pivot

Pick pivot uniformly at random. Expected time = O(n log n) regardless of input. Adversary cannot construct a worst-case input without knowing your random seed.

### Strategy 2: Median-of-Three

Eliminates worst case on common patterns (sorted, reverse). Almost-worst-case inputs still possible but rare.

### Strategy 3: Introsort

Track recursion depth. If depth > 2 log n, switch to Heap Sort (guaranteed O(n log n)). Used by C++ STL's `std::sort`.

```python
import math
def introsort(arr):
    max_depth = 2 * int(math.log2(len(arr)))
    _intro(arr, 0, len(arr) - 1, max_depth)

def _intro(arr, lo, hi, depth):
    if hi - lo < 16:
        insertion_sort(arr, lo, hi)
        return
    if depth == 0:
        heap_sort_range(arr, lo, hi)
        return
    p = median_of_three_partition(arr, lo, hi)
    _intro(arr, lo, p - 1, depth - 1)
    _intro(arr, p + 1, hi, depth - 1)
```

### Strategy 4: Pdqsort

Pdqsort combines:
- Median-of-three (with shuffles for big arrays).
- Pattern detection — if input has patterns, partition differently.
- Block partition for cache efficiency.
- Introsort fallback to Heap Sort.

Result: **Provably O(n log n) worst case** with the Quick Sort speed on average.

---

## 3-Way Partition

For arrays with many duplicates, vanilla Quick Sort is O(n²) (pivot doesn't divide). 3-way partition handles this:

```python
def quick_sort_3way(arr, lo=0, hi=None):
    if hi is None: hi = len(arr) - 1
    if lo >= hi: return
    pivot = arr[lo]
    lt, gt = lo, hi
    i = lo + 1
    while i <= gt:
        if arr[i] < pivot:
            arr[lt], arr[i] = arr[i], arr[lt]
            lt += 1; i += 1
        elif arr[i] > pivot:
            arr[i], arr[gt] = arr[gt], arr[i]
            gt -= 1
        else:
            i += 1
    quick_sort_3way(arr, lo, lt - 1)
    quick_sort_3way(arr, gt + 1, hi)
```

**Win:** O(n) on arrays where all elements are equal; O(n · k) where k = number of distinct values. Asymptotically better than vanilla on duplicate-heavy data.

Used by Java's Dual-Pivot Quicksort.

---

## Variants

### Dual-Pivot Quicksort (Java's `Arrays.sort` for primitives)

Uses TWO pivots, partitioning into 3 regions. Yaroslavskiy's algorithm (2009):

```text
Choose two pivots p1 ≤ p2 (often arr[lo + n/3] and arr[hi - n/3]).
Partition into:
  A: < p1
  B: p1 ≤ x ≤ p2
  C: > p2
Recurse on A, B, C.
```

**Why it's faster:** ~5-10% fewer comparisons than single-pivot, better cache behavior.

### Pdqsort

See [Pdqsort Deep Dive](#pdqsort-deep-dive) below.

### Multi-key Quicksort

For sorting strings, partition by character at position `d`. Recurse on each character class. O(n · L) where L = string length.

### Parallel Quick Sort

Sort each partition on a separate thread. Speedup limited by partition imbalance and synchronization.

### Tail-Call-Optimized Quick Sort

```python
def qsort_tco(arr, lo, hi):
    while lo < hi:
        p = partition(arr, lo, hi)
        # Recurse on smaller side, iterate on larger
        if p - lo < hi - p:
            qsort_tco(arr, lo, p - 1)
            lo = p + 1
        else:
            qsort_tco(arr, p + 1, hi)
            hi = p - 1
```

**Win:** Bounds recursion depth to O(log n) even with bad pivots — prevents stack overflow.

---

## Pdqsort Deep Dive

**Pdqsort** (Pattern-Defeating Quicksort, Orson Peters, 2014) is the most modern Quick Sort variant. Used by Go, Rust, Boost C++.

### Key Innovations

1. **Median-of-three pivot** for arrays of size ≥ 24.
2. **Pattern detection:** if a partition produces highly imbalanced split, shuffle to break the pattern.
3. **Block partitioning:** batch comparisons to avoid branch mispredictions; 30-50% faster on random data.
4. **Insertion Sort cutoff** at n < 24.
5. **Introsort fallback:** when recursion depth limit reached, switch to Heap Sort.

### Performance

| Input | Pdqsort | Vanilla Quick Sort | Merge Sort |
|-------|---------|-------------------|------------|
| Random n=10⁶ | 38 ms | 65 ms | 90 ms |
| Sorted n=10⁶ | 5 ms | 50 s (O(n²)) | 90 ms |
| Reverse n=10⁶ | 8 ms | 50 s | 90 ms |
| Many duplicates | 12 ms | O(n²) | 90 ms |

Pdqsort dominates across all input shapes.

---

## Code Examples

### Quick Sort with Median-of-Three + Insertion Cutoff + Tail Recursion

```go
package main

import "fmt"

const CUTOFF = 16

func QuickSort(arr []int) {
    sort(arr, 0, len(arr)-1)
}

func sort(a []int, lo, hi int) {
    for hi - lo > CUTOFF {
        p := medianOfThreePartition(a, lo, hi)
        if p - lo < hi - p {
            sort(a, lo, p - 1)
            lo = p + 1
        } else {
            sort(a, p + 1, hi)
            hi = p - 1
        }
    }
    insertion(a, lo, hi)
}

func medianOfThreePartition(a []int, lo, hi int) int {
    mid := (lo + hi) / 2
    if a[lo] > a[mid] { a[lo], a[mid] = a[mid], a[lo] }
    if a[lo] > a[hi]  { a[lo], a[hi]  = a[hi], a[lo] }
    if a[mid] > a[hi] { a[mid], a[hi] = a[hi], a[mid] }
    a[mid], a[hi-1] = a[hi-1], a[mid]
    pivot := a[hi-1]
    i, j := lo, hi - 1
    for {
        for i++; a[i] < pivot; i++ {}
        for j--; a[j] > pivot; j-- {}
        if i >= j { break }
        a[i], a[j] = a[j], a[i]
    }
    a[i], a[hi-1] = a[hi-1], a[i]
    return i
}

func insertion(a []int, lo, hi int) {
    for i := lo + 1; i <= hi; i++ {
        x, j := a[i], i - 1
        for j >= lo && a[j] > x { a[j+1] = a[j]; j-- }
        a[j+1] = x
    }
}

func main() {
    data := []int{7, 2, 1, 6, 8, 5, 3, 4, 9, 10, 0}
    QuickSort(data)
    fmt.Println(data)
}
```

---

## Performance Analysis

For n = 10⁶ random ints (Go 1.22):

| Sort | Time | Comparisons |
|------|------|-------------|
| Pdqsort (`sort.Ints`) | 38 ms | ~1.05 n log n |
| Vanilla Quick Sort | 65 ms | ~1.39 n log n |
| Merge Sort (in-place) | 90 ms | ~1.0 n log n |
| Heap Sort | 110 ms | ~2.0 n log n |
| Insertion Sort | 50 s | ~n²/4 |

Pdqsort wins on average. Merge Sort wins on stability + worst-case guarantee.

For sorted input n = 10⁶:

| Sort | Time |
|------|------|
| Pdqsort | 5 ms |
| Vanilla Quick Sort (first as pivot) | **50 s** ← O(n²) |
| Merge Sort | 90 ms |
| TimSort | **2 ms** ← detects sortedness |

---

## Cache and Memory

Quick Sort's secret weapon: **cache locality**. Each partition step accesses adjacent memory; subarrays often fit entirely in L1 cache. Compare to Merge Sort which copies into and out of an auxiliary buffer.

Memory bandwidth comparison (n=10⁶, 8-byte ints):

| Sort | Memory Writes | Cache Misses |
|------|--------------|--------------|
| Quick Sort (in-place) | ~16 MB | O((n/B) log(n/M)) |
| Merge Sort | ~160 MB | O((n/B) log(n/M)) |
| Heap Sort | ~16 MB | O(n log n / B) ← bad |

Quick Sort has ~10× less memory write pressure than Merge Sort, despite same Big-O. This is why it dominates in-memory benchmarks.

---

## Summary

At middle level, Quick Sort is understood as a **family of partition-based divide-and-conquer sorts**. Production variants — **Pdqsort** (Go, Rust), **Dual-Pivot Quicksort** (Java), **Introsort** (C++) — eliminate the O(n²) worst case via smart pivot selection, pattern detection, and Heap Sort fallback. **3-way partition** handles duplicate-heavy inputs. **Median-of-three pivot** beats first/last on adversarial inputs. **Insertion Sort cutoff** at n ≤ 16 is the universal hybrid.

Two takeaways:
1. **Never use first-element pivot in production** — random or median-of-three.
2. **For best-in-class performance: use your language's built-in.** It's already Pdqsort/Dual-Pivot/Introsort, optimized for decades.
