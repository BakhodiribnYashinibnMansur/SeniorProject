# Quick Sort — Senior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Quick Sort in Production](#quick-sort-in-production)
3. [Adversarial Input and Algorithmic Complexity Attacks](#adversarial-input)
4. [Quickselect — Linear Selection](#quickselect)
5. [Parallel Quick Sort](#parallel-quick-sort)
6. [Concurrency](#concurrency)
7. [Code Examples](#code-examples)
8. [Observability](#observability)
9. [Failure Modes](#failure-modes)
10. [Summary](#summary)

---

## Introduction

> Focus: "How do I architect with Quick Sort and its derivatives?"

Quick Sort itself you rarely write — you use Pdqsort/Dual-Pivot via your language's built-in. The senior questions are:

1. **Algorithmic complexity attacks:** can an adversary cause O(n²) by crafting input?
2. **Quickselect:** O(n) selection of the k-th smallest using Quick Sort's partition.
3. **Parallel sort:** when does multi-threading actually win?

---

## Quick Sort in Production

### Languages Using Quick Sort Variants

| Language | Sort | Variant |
|----------|------|---------|
| Go | `slices.Sort`, `sort.Ints` | **Pdqsort** since Go 1.19 |
| Rust | `slice::sort_unstable` | **Pdqsort** |
| Java | `Arrays.sort(int[])` | **Dual-Pivot Quicksort** (Yaroslavskiy) |
| C++ STL | `std::sort` | **Introsort** (Quick + Heap fallback) |
| Boost | `boost::sort::pdqsort` | **Pdqsort** |
| LinkedIn (Andress Frey) | `radix_sort` | Quick + Radix hybrid for ints |

### Why Production Chose Quick Sort Family

1. **In-place** — minimal memory overhead.
2. **Cache-friendly** — partition keeps data hot in L1/L2.
3. **Fastest in practice** for in-memory random data.
4. **Modern variants are O(n log n) worst case** (introsort fallback).

---

## Adversarial Input

### The Attack

If your sort uses `arr[lo]` or `arr[hi]` as pivot, an attacker can craft input that causes O(n²) — a denial-of-service attack:

```python
# Attacker submits already-sorted input
adversarial = list(range(10000))  # already sorted
# Vanilla Quick Sort with first-as-pivot: O(n²) = 10⁸ ops
# Causes ~10s of CPU per request → easy DoS
```

This was a real issue in PHP, Java (pre-2008), and many web frameworks.

### Defenses

1. **Random pivot** with cryptographic seed — adversary can't predict.
2. **Median-of-three** — partial protection.
3. **Pdqsort/Introsort** — guaranteed O(n log n).
4. **Input size limit** — reject queries with >1M items.
5. **Use the language's built-in** — Go/Rust/Java are immune (Pdqsort/Dual-Pivot).

### Hash Map Comparison

The same attack pattern applies to **hash map collisions** (Hash DoS). Defense: random hash seed per process. Java added it in 2011; Python in 2012.

---

## Quickselect

Quick Sort's partition gives a powerful side benefit: **find the k-th smallest in O(n) average**.

```python
def quickselect(arr, k):
    """Return the k-th smallest (0-indexed) without fully sorting. O(n) avg."""
    lo, hi = 0, len(arr) - 1
    while lo < hi:
        p = partition(arr, lo, hi)
        if p == k: return arr[p]
        if p < k: lo = p + 1
        else: hi = p - 1
    return arr[lo]
```

**Why O(n)?** Each recursion halves the search space. T(n) = T(n/2) + O(n) → O(n) average.

**Worst case:** O(n²) if pivot always at extreme. **Median-of-medians algorithm** gives deterministic O(n).

### Applications

- **Top-k:** quickselect to find k-th, then partition gives the top k.
- **Median:** quickselect with k = n/2.
- **Quantiles:** quickselect with k = n*p for percentile p.

### Production Use

- C++ STL: `std::nth_element` is Quickselect.
- Python: `heapq.nsmallest(k, arr)` uses heap-based; `numpy.partition` uses Quickselect.
- Go: no built-in; implement via `sort.Slice` + take first k.

---

## Parallel Quick Sort

Quick Sort is naturally parallel: after partition, the two halves can be sorted independently.

### Naïve Parallel

```go
func ParallelQuickSort(arr []int) {
    if len(arr) < 10000 {
        QuickSort(arr); return
    }
    p := partition(arr, 0, len(arr) - 1)
    var wg sync.WaitGroup
    wg.Add(2)
    go func() { defer wg.Done(); ParallelQuickSort(arr[:p]) }()
    go func() { defer wg.Done(); ParallelQuickSort(arr[p+1:]) }()
    wg.Wait()
}
```

**Speedup:** ~3-5× on 8 cores for n > 10⁶.

### Issues

- Imbalanced partitions → uneven thread work.
- Synchronization overhead for small subarrays.

### Java's parallelSort

`Arrays.parallelSort` uses ForkJoin-style parallel merge sort, NOT parallel Quick Sort. Reason: parallel Quick Sort's load balancing is harder.

---

## Concurrency

Quick Sort modifies in-place. Concurrent access requires snapshots:

```go
type SortedView struct {
    mu       sync.RWMutex
    snapshot []int
}

func (s *SortedView) Update(data []int) {
    cp := append([]int{}, data...)
    sort.Ints(cp) // uses Pdqsort
    s.mu.Lock()
    s.snapshot = cp
    s.mu.Unlock()
}
```

For high write throughput, use a sorted concurrent data structure (skip list) instead.

---

## Code Examples

### Quickselect

```python
import random

def quickselect(arr, k):
    """k-th smallest, 0-indexed. O(n) average. Modifies arr in place."""
    lo, hi = 0, len(arr) - 1
    while lo < hi:
        # random pivot to avoid O(n²)
        rnd = random.randint(lo, hi)
        arr[rnd], arr[hi] = arr[hi], arr[rnd]
        p = _partition(arr, lo, hi)
        if p == k: return arr[p]
        if p < k: lo = p + 1
        else: hi = p - 1
    return arr[lo]

def _partition(arr, lo, hi):
    pivot = arr[hi]
    i = lo - 1
    for j in range(lo, hi):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i+1], arr[hi] = arr[hi], arr[i+1]
    return i + 1

# Usage
data = [3, 1, 4, 1, 5, 9, 2, 6]
print(quickselect(list(data), 0))   # 1 (smallest)
print(quickselect(list(data), len(data)//2))  # median
```

### Top-K via Quickselect

```python
def top_k(arr, k):
    """Return k smallest in any order. O(n) average."""
    if k <= 0: return []
    if k >= len(arr): return list(arr)
    a = list(arr)
    quickselect(a, k - 1)
    return a[:k]
```

---

## Observability

| Metric | Threshold | Why |
|--------|-----------|-----|
| `sort_recursion_depth_max` | > 2 log n | Suggests bad pivots; investigate input |
| `sort_duration_p99_ms` | Track baseline | Quadratic blow-up = DoS attack |
| `quickselect_iterations` | > 3 log n | Bad pivots in selection |
| `parallel_sort_speedup` | < 2× on 8 cores | Imbalanced partitions or memory bandwidth |

---

## Failure Modes

| Mode | Symptom | Mitigation |
|------|---------|------------|
| O(n²) on adversarial input | Sudden latency spike | Use Pdqsort or random pivot |
| Stack overflow on bad pivot | RecursionError | Tail-recursion optimization, introsort fallback |
| Concurrent modification | Sort fails | Snapshot pattern |
| Hash DoS-like sort attack | Specific inputs cause O(n²) | Verify production sort is Pdqsort/Dual-Pivot |

---

## Summary

At senior level, Quick Sort is consumed via your language's built-in (Pdqsort/Dual-Pivot/Introsort) — never written from scratch. Defend against algorithmic complexity attacks by using a randomized or pattern-defeating variant. Use **Quickselect** for O(n) selection. **Parallel Quick Sort** delivers 3-5× speedup on multi-core but has load-balancing challenges; ForkJoin Merge Sort is preferred for parallelism.

Production rule: **Use the built-in. It's faster than your code, harder to break, and well-tested.**
