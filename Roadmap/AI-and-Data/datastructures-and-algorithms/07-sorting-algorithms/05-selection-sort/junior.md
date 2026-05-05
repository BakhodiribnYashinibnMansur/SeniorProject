# Selection Sort — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Glossary](#glossary)
4. [Core Concepts](#core-concepts)
5. [Big-O Summary](#big-o-summary)
6. [Real-World Analogies](#real-world-analogies)
7. [Pros & Cons](#pros--cons)
8. [Step-by-Step Walkthrough](#step-by-step-walkthrough)
9. [Code Examples](#code-examples)
10. [Coding Patterns](#coding-patterns)
11. [Error Handling](#error-handling)
12. [Performance Tips](#performance-tips)
13. [Best Practices](#best-practices)
14. [Edge Cases & Pitfalls](#edge-cases--pitfalls)
15. [Common Mistakes](#common-mistakes)
16. [Cheat Sheet](#cheat-sheet)
17. [Visual Animation](#visual-animation)
18. [Summary](#summary)
19. [Further Reading](#further-reading)

---

## Introduction

> Focus: "What is Selection Sort?" and "When are minimum writes more important than minimum time?"

**Selection Sort** is the simplest sort that minimizes **writes**. It works by repeatedly:
1. Finding the **minimum** element in the unsorted portion.
2. Swapping it to the front of the unsorted portion.

After each pass, one more element is in its final sorted position. After `n-1` passes, the entire array is sorted.

Selection Sort is **O(n²) time** in all cases (best, average, worst), **in-place** (O(1) extra space), and crucially, performs only **n-1 swaps total** — far fewer than Bubble Sort or Insertion Sort. This makes it useful when **writes are very expensive** (e.g., **flash memory wear-leveling**, EEPROM with limited write cycles).

For most other use cases, Selection Sort is dominated by Insertion Sort (same Big-O, but Insertion is adaptive and usually faster). Use Selection Sort only when minimizing writes matters more than minimizing comparisons.

---

## Prerequisites

- **Required:** Arrays, loops, swap operation
- **Required:** Finding the minimum element of an array
- **Helpful:** Familiarity with Bubble Sort and Insertion Sort

---

## Glossary

| Term | Definition |
|------|-----------|
| **Selection** | Choosing the minimum (or maximum) of a range |
| **Sorted Prefix** | After pass i, `arr[0..i]` is in final sorted order |
| **Min-Index Search** | Linear scan to find the minimum's index |
| **Stable Selection Sort** | Variant that preserves relative order — requires shifts (not swaps) |
| **Heap Sort** | Generalizes Selection Sort using a heap for O(log n) min-find → O(n log n) total |

---

## Core Concepts

### Concept 1: Find Min, Swap to Front

For each iteration `i`:
1. Find the index of the minimum in `arr[i..n-1]`.
2. Swap `arr[i]` with `arr[min_index]`.

After this, `arr[0..i]` is sorted and contains the i+1 smallest elements.

### Concept 2: Always O(n²) — No Best Case

Selection Sort always scans the unsorted portion fully to find the minimum. Even on already-sorted input, it does `n(n-1)/2` comparisons. **Not adaptive.**

### Concept 3: Only n-1 Swaps Total

Each pass performs at most one swap. Total swaps = n-1, regardless of input. Compare to Bubble Sort / Insertion Sort with O(n²) swaps. This is Selection Sort's superpower.

### Concept 4: Not Stable (Standard Form)

When swapping the minimum to position `i`, the element at `i` jumps to the minimum's old position — possibly past equal elements that should have stayed before it. Example: `[(1,'a'), (1,'b'), (1,'c'), (0,'x')]` → after pass 0, `(0,'x')` is at position 0, `(1,'a'), (1,'b'), (1,'c')` are in unspecified order.

A stable variant exists (use shifts instead of swaps) but loses the "few writes" advantage.

### Concept 5: Heap Sort = Generalized Selection Sort

Heap Sort uses a heap data structure to find the minimum in O(log n) instead of O(n). This makes it O(n log n) instead of O(n²). Heap Sort is essentially "Selection Sort with a smart min-finder."

---

## Big-O Summary

| Case | Time | Notes |
|------|------|-------|
| Best | O(n²) | Same as worst — not adaptive |
| Average | O(n²) | |
| Worst | O(n²) | |
| Space | O(1) | In-place |
| Comparisons (always) | n(n-1)/2 ≈ n²/2 | |
| Swaps (always) | n - 1 | The minimum possible for a comparison sort |
| Stable | No (typical) | Stable variant exists with O(n²) shifts |
| Adaptive | No | |

---

## Real-World Analogies

| Concept | Analogy |
|---------|--------|
| **Find min, swap to front** | Lining up people by height — find the shortest, send them to the front |
| **Sorted prefix grows** | Each pass, one more person joins the sorted line |
| **Few swaps** | Each person moves at most twice (in + final position) |
| **Why few writes matter** | If writing to a chalkboard wears it out, prefer fewer writes |

---

## Pros & Cons

| Pros | Cons |
|------|------|
| **Minimum number of swaps** (n-1) — best for write-expensive media | **O(n²) always** — even on sorted input |
| Simple to understand and implement | Not adaptive |
| In-place, O(1) extra space | Not stable (typical impl) |
| Predictable runtime | Slower than Insertion Sort on most inputs |

**When to use:**
- **Write-expensive media**: flash memory wear-leveling, EEPROM with limited write cycles.
- When swap cost is much higher than compare cost.
- Embedded systems where simplicity matters.

**When NOT to use:**
- General sorting — Insertion Sort beats it.
- Need stability — use Merge Sort.
- Need O(n log n) — use Heap Sort (which IS Selection Sort with a heap).

---

## Step-by-Step Walkthrough

Sort `[64, 25, 12, 22, 11]`:

```
Pass 1: scan [64, 25, 12, 22, 11], min = 11 at index 4. Swap with index 0.
        [11, 25, 12, 22, 64]

Pass 2: scan [25, 12, 22, 64], min = 12 at index 2. Swap with index 1.
        [11, 12, 25, 22, 64]

Pass 3: scan [25, 22, 64], min = 22 at index 3. Swap with index 2.
        [11, 12, 22, 25, 64]

Pass 4: scan [25, 64], min = 25 at index 3. Already at index 3. No swap (or no-op swap).
        [11, 12, 22, 25, 64]

Sorted!
```

Comparisons: 4+3+2+1 = 10 = n(n-1)/2.
Swaps: 3 (we count "no-op" swaps as 0).

---

## Code Examples

### Example 1: Standard Selection Sort

#### Go

```go
package main

import "fmt"

func SelectionSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        minIdx := i
        for j := i + 1; j < n; j++ {
            if arr[j] < arr[minIdx] {
                minIdx = j
            }
        }
        if minIdx != i {
            arr[i], arr[minIdx] = arr[minIdx], arr[i]
        }
    }
}

func main() {
    data := []int{64, 25, 12, 22, 11}
    SelectionSort(data)
    fmt.Println(data) // [11 12 22 25 64]
}
```

#### Java

```java
import java.util.Arrays;

public class SelectionSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            if (minIdx != i) {
                int t = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = t;
            }
        }
    }

    public static void main(String[] args) {
        int[] data = {64, 25, 12, 22, 11};
        sort(data);
        System.out.println(Arrays.toString(data));
    }
}
```

#### Python

```python
def selection_sort(arr):
    """In-place ascending sort. O(n²) time, O(1) space, n-1 swaps. Not stable."""
    n = len(arr)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            if arr[j] < arr[min_idx]:
                min_idx = j
        if min_idx != i:
            arr[i], arr[min_idx] = arr[min_idx], arr[i]

if __name__ == "__main__":
    data = [64, 25, 12, 22, 11]
    selection_sort(data)
    print(data)
```

### Example 2: Stable Selection Sort (Shifts Instead of Swap)

```python
def stable_selection_sort(arr):
    """Stable but does O(n²) writes — defeats the 'few swaps' benefit."""
    n = len(arr)
    for i in range(n - 1):
        min_idx = i
        for j in range(i + 1, n):
            if arr[j] < arr[min_idx]:
                min_idx = j
        # Shift everything between i and min_idx right by one
        x = arr[min_idx]
        while min_idx > i:
            arr[min_idx] = arr[min_idx - 1]
            min_idx -= 1
        arr[i] = x
```

**Trade-off:** Stable but O(n²) writes. Use Merge Sort for stable O(n log n).

### Example 3: Bidirectional Selection Sort (Cocktail Selection)

Find both min AND max in one pass; place min at front, max at back. ~2× faster.

```python
def bidirectional_selection_sort(arr):
    n = len(arr)
    lo, hi = 0, n - 1
    while lo < hi:
        min_idx = lo
        max_idx = lo
        for j in range(lo, hi + 1):
            if arr[j] < arr[min_idx]: min_idx = j
            if arr[j] > arr[max_idx]: max_idx = j
        # Place min at lo
        if min_idx != lo:
            arr[lo], arr[min_idx] = arr[min_idx], arr[lo]
            if max_idx == lo: max_idx = min_idx
        # Place max at hi
        if max_idx != hi:
            arr[hi], arr[max_idx] = arr[max_idx], arr[hi]
        lo += 1; hi -= 1
```

---

## Coding Patterns

### Pattern 1: Min-Index Search

```python
min_idx = i
for j in range(i + 1, n):
    if arr[j] < arr[min_idx]:
        min_idx = j
```

Reusable for any "find smallest in range" problem.

### Pattern 2: Swap-If-Different

```python
if min_idx != i:
    arr[i], arr[min_idx] = arr[min_idx], arr[i]
```

Avoids the wasteful no-op self-swap.

---

## Error Handling

| Error | Cause | Fix |
|-------|-------|-----|
| `IndexError` | Off-by-one in inner loop | Use `range(i+1, n)`, not `range(i, n)` |
| Unstable when stability expected | Standard impl is unstable | Use stable variant (with shifts) or Merge Sort |
| Wrong result on duplicates | Off-by-one in `<` vs `<=` | Use `<` to maintain pseudo-stability when min appears multiple times |

---

## Performance Tips

- Skip self-swap when `min_idx == i` — saves ~1 write per pass.
- Use bidirectional variant for ~2× speedup.
- For O(n log n), use Heap Sort (generalized Selection Sort with heap).

---

## Best Practices

- Document that Selection Sort is **not stable** in your function comment.
- Use Insertion Sort instead unless minimizing writes is critical.
- For real production: use built-in sort.

---

## Edge Cases & Pitfalls

- **Empty array** → outer loop body skipped. Safe.
- **Single element** → outer loop runs but no inner work. Safe.
- **All equal** → no swaps performed (since `<` is strict). O(n²) comparisons.
- **Already sorted** → no swaps; O(n²) comparisons.
- **Duplicates** → relative order may change → not stable.

---

## Common Mistakes

1. **Inner loop starts at `i` instead of `i+1`** → wasteful self-comparison.
2. **Outer goes to `n` instead of `n-1`** → useless last iteration.
3. **Using `<=` instead of `<`** → still works but may swap unnecessarily.
4. **Saying "Selection Sort is stable"** → False in standard form.

---

## Cheat Sheet

```text
Selection Sort

ALGORITHM:
  for i in 0..n-2:
    min_idx = i
    for j in i+1..n-1:
      if arr[j] < arr[min_idx]: min_idx = j
    if min_idx != i: swap(arr[i], arr[min_idx])

COMPLEXITY:
  Time:  O(n²) ALL cases (not adaptive)
  Space: O(1)
  Swaps: n - 1 (minimum possible)
  Stable: NO (typical)

USE WHEN:
  - Writes are very expensive (flash memory)
  - Need predictable runtime
  - Need fewest swaps

NEVER USE FOR:
  - General fast sorting (use Insertion or Merge)
  - When stability needed
```

---

## Visual Animation

> See [`animation.html`](./animation.html) for interactive visualization.

---

## Summary

Selection Sort: **find min, swap to front, repeat**. O(n²) time, O(1) space, **n-1 swaps total** (the minimum possible for a comparison sort). **Not stable**, **not adaptive**. The right choice when **writes are expensive** (flash memory) or **predictable runtime** matters more than peak speed. For everything else, prefer Insertion Sort (faster) or Merge Sort (stable, scalable). Heap Sort is the O(n log n) generalization.

---

## Further Reading

- CLRS Problem 2-2 (variant)
- Sedgewick Algorithms 4ed §2.1 (selection sort)
- Knuth TAOCP Vol. 3, §5.2.3
- "EEPROM wear leveling" — flash memory write minimization
- `../06-heap-sort/` — Selection Sort with a heap, O(n log n)
