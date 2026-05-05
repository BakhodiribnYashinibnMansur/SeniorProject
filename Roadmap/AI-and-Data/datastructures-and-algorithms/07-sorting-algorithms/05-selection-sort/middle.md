# Selection Sort — Middle Level

## Table of Contents
1. [Introduction](#introduction)
2. [Why Selection Sort Has Few Writes](#few-writes)
3. [Comparison with Bubble & Insertion](#comparison)
4. [Variants](#variants)
5. [Selection Sort and Heap Sort Connection](#heap-sort-connection)
6. [Code Examples](#code-examples)
7. [Performance Analysis](#performance-analysis)
8. [Summary](#summary)

---

## Introduction

> Focus: "When are minimum writes more important than minimum time?"

Selection Sort's defining characteristic is **n-1 swaps total**. For random input, Bubble does ~n²/4 swaps, Insertion does ~n²/4 shifts, Selection does just **n-1**.

This matters in:
- **Flash memory:** each write erases and rewrites a block (10⁵ - 10⁶ cycle limit). Write minimization extends device life.
- **EEPROM:** similar wear concerns.
- **Network-attached storage:** each write requires sync; fewer writes = lower latency.
- **Distributed databases:** writes propagate to replicas → fewer writes = less network traffic.

For pure CPU performance, Selection Sort loses to Insertion Sort. The trade-off is write-cost vs. time.

---

## Few Writes

| Sort | Writes (avg, n=1000) | Comparisons (avg) |
|------|---------------------|-------------------|
| Selection | 999 | ~500,000 |
| Bubble | ~250,000 | ~500,000 |
| Insertion | ~250,000 | ~250,000 |

**Selection Sort does ~250× fewer writes than Bubble Sort** at the same comparison count.

---

## Comparison

| Attribute | Selection | Bubble | Insertion |
|-----------|-----------|--------|-----------|
| Best time | O(n²) | O(n) (early exit) | O(n) |
| Avg time | O(n²) | O(n²) | O(n²) |
| Worst time | O(n²) | O(n²) | O(n²) |
| Comparisons (avg) | n²/2 | n²/2 | n²/4 |
| Writes (avg) | **n** ← winner | n²/4 | n²/4 |
| Stable | No | Yes | Yes |
| Adaptive | No | Yes (modest) | Yes (very) |

**Selection Sort wins on writes; loses on stability and adaptivity.**

---

## Variants

### Stable Selection Sort

Use shifts instead of swaps to preserve order. O(n²) writes — defeats the few-writes benefit.

### Bidirectional Selection Sort (Cocktail Selection)

Find min AND max in one pass. Place at both ends. ~2× faster.

### Recursive Selection Sort

Same algorithm, recursive form. Pedagogical only.

### Heap Sort

Use a heap to find the min in O(log n) instead of O(n). Total O(n log n). The "real" upgrade.

---

## Heap Sort Connection

Heap Sort = Selection Sort with a smart min-finder:
- Selection Sort: find min in O(n) → total O(n²).
- Heap Sort: find min in O(log n) using a heap → total O(n log n).

Both share:
- In-place
- Not stable
- Few writes (Heap Sort: O(n log n) writes vs. O(n) for Selection)

See `../06-heap-sort/` for the full algorithm.

---

## Code Examples

### Selection Sort with Statistics

```python
from dataclasses import dataclass

@dataclass
class SortStats:
    comparisons: int = 0
    swaps: int = 0

def selection_sort_stats(arr):
    s = SortStats()
    n = len(arr)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            s.comparisons += 1
            if arr[j] < arr[min_idx]: min_idx = j
        if min_idx != i:
            arr[i], arr[min_idx] = arr[min_idx], arr[i]
            s.swaps += 1
    return s

# n=1000 random
import random
data = [random.randint(0, 1000) for _ in range(1000)]
stats = selection_sort_stats(data)
print(stats)  # comparisons~500k, swaps~999
```

### Generic Selection Sort

```go
func SelectionSortBy[T any](arr []T, less func(a, b T) bool) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        minIdx := i
        for j := i + 1; j < n; j++ {
            if less(arr[j], arr[minIdx]) {
                minIdx = j
            }
        }
        if minIdx != i {
            arr[i], arr[minIdx] = arr[minIdx], arr[i]
        }
    }
}
```

---

## Performance Analysis

For n = 10,000 random ints (Go):
| Sort | Time | Swaps |
|------|------|-------|
| Selection | 80 ms | 9,999 |
| Bubble | 145 ms | ~25,000,000 |
| Insertion | 55 ms | ~25,000,000 (shifts) |
| sort.Ints | 1.3 ms | n/a |

Selection is faster than Bubble (fewer writes amortize the comparisons) but slower than Insertion. Built-in is 60× faster.

---

## Summary

Selection Sort: O(n²) time always, **n-1 swaps** total. Wins for write-expensive media. Loses to Insertion on most metrics. Generalizes to **Heap Sort** (use a heap for O(log n) min-find → O(n log n) total). Use Selection Sort only when minimizing writes is critical.
