# Quick Sort — Junior Level

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

> Focus: "What is Quick Sort?" and "Why is it the fastest in-memory sort in practice?"

**Quick Sort** is the **fastest general-purpose comparison sort in practice**, despite having an O(n²) worst case. It uses **divide-and-conquer** like Merge Sort, but with a different strategy:

1. Pick a **pivot** element from the array.
2. **Partition** the array so all elements less than the pivot go left, and all greater go right.
3. **Recursively** sort the two partitions.

There is no "merge" step — the partition step does all the ordering work. This is why Quick Sort is **in-place** (uses O(log n) recursion stack only) and **cache-friendly** (data stays in cache across partition steps).

Invented by Tony Hoare in 1959 (he was 25 years old, working on machine translation in Moscow), Quick Sort dominates production sort libraries:

- **Go:** `sort.Slice`, `sort.Ints` — Pdqsort (Pattern-Defeating Quick Sort).
- **Java (primitives):** `Arrays.sort(int[])` — Dual-Pivot Quicksort.
- **C++ STL:** `std::sort` — Introsort (Quick Sort + Heap Sort fallback).
- **Rust:** `slice::sort_unstable` — Pdqsort.

The trade-off: **O(n²) worst case** on adversarial input (or naïve pivot choice). Modern variants (Pdqsort, Dual-Pivot, Introsort) eliminate this in practice.

---

## Prerequisites

- **Required:** Recursion, arrays, partition concept
- **Required:** Understanding of "divide and conquer"
- **Helpful:** Familiarity with Merge Sort to compare
- **Helpful:** Understanding of cache locality

---

## Glossary

| Term | Definition |
|------|-----------|
| **Pivot** | Chosen element used to partition the array |
| **Partition** | Rearrange so all `< pivot` come before, `> pivot` after |
| **Lomuto Partition** | Simpler partition scheme (one pointer scans, swaps to "less" region) |
| **Hoare Partition** | Original Hoare scheme (two pointers from both ends, ~3× fewer swaps) |
| **3-Way Partition** | Splits into `< pivot`, `== pivot`, `> pivot` — handles duplicates well |
| **Median-of-Three** | Pivot = median of first, middle, last elements — improves balance |
| **Tail Recursion** | Recurse on smaller partition first; iterate over larger — bounds stack to O(log n) |
| **Introsort** | Quick Sort + Heap Sort fallback when recursion exceeds 2 log n |
| **Pdqsort** | Pattern-Defeating Quick Sort — modern variant with no O(n²) on adversarial input |
| **Dual-Pivot** | Partition with TWO pivots into 3 regions; ~5% faster than single-pivot |

---

## Core Concepts

### Concept 1: Pivot and Partition

The pivot divides the array into two subarrays:
- All elements ≤ pivot on the left.
- All elements ≥ pivot on the right.
- The pivot itself ends up at its **final sorted position**.

After partitioning, the pivot is in place — we never need to move it again.

### Concept 2: Recurse on Both Sides (No Merge!)

Unlike Merge Sort, there's no combine step. Once partitioned, we recursively sort the two halves independently. The pivot's correct position guarantees the result is sorted.

### Concept 3: Pivot Choice Determines Performance

- **Bad pivot** (always the smallest or largest) → unbalanced partition → O(n²) recursion depth.
- **Good pivot** (median) → balanced partition → O(log n) recursion depth → O(n log n) time.

Common strategies:
- First/last element (simple but degrades on sorted input).
- Random element (probabilistic O(n log n) — good baseline).
- Median-of-three (first, middle, last) — fast and effective.
- Median-of-medians (deterministic median in O(n)) — used in worst-case linear selection.

### Concept 4: In-Place Partition

The partition rearranges in place using two pointers. No auxiliary array needed (unlike Merge Sort). This is why Quick Sort uses only O(log n) extra memory.

### Concept 5: Not Stable (Typical Implementations)

Standard Quick Sort is **not stable** — equal elements can swap order during partition. There are stable variants but they require O(n) extra space (defeating the in-place benefit).

---

## Big-O Summary

| Case | Time | Notes |
|------|------|-------|
| Best | **O(n log n)** | Balanced partitions every level |
| Average | **O(n log n)** | Random pivot |
| Worst | **O(n²)** | Always bad pivot (sorted input + first-as-pivot) |
| Space (avg) | **O(log n)** | Recursion stack |
| Space (worst) | **O(n)** | Skewed recursion |
| Stable | **No** (typical) | Equal elements can swap |
| In-place | **Yes** | |
| Adaptive | **Partially** (Pdqsort) | Vanilla doesn't adapt |
| Comparisons (avg) | ~1.39 n log n | Slightly more than Merge Sort |

---

## Real-World Analogies

| Concept | Analogy |
|---------|--------|
| **Pivot + partition** | Sorting people by age into "younger than Anna" vs "older than Anna" piles |
| **Recurse on both** | Each pile gets sorted separately by a different supervisor |
| **In-place** | Reorganizing books on a shelf without taking them off |
| **Bad pivot** | Splitting "everyone shorter than the tallest person" → one giant pile |
| **Median-of-three** | Pick the middle-tall person from sample of 3 candidates |

---

## Pros & Cons

| Pros | Cons |
|------|------|
| **Fastest in practice** for in-memory random data | **O(n²) worst case** (mitigated by Pdqsort) |
| In-place — only O(log n) stack | **Not stable** in standard form |
| Cache-friendly — locality of reference | Performance depends on pivot choice |
| Naturally parallelizable | Recursion depth can blow stack on bad pivot |
| Works well with Insertion Sort cutoff | Can be slow on already-sorted input (without smart pivot) |

**When to use:**
- General-purpose in-memory sorting.
- When stability isn't required.
- When memory is constrained (only O(log n) overhead).
- Pdqsort or Dual-Pivot Quicksort variants for production.

**When NOT to use:**
- Need stability → use Merge Sort or TimSort.
- External sort (data > RAM) → use Merge Sort.
- Linked list → use Merge Sort.
- Real-time systems with strict latency SLAs → use Merge Sort or Heap Sort (predictable O(n log n)).

---

## Step-by-Step Walkthrough

Sort `[7, 2, 1, 6, 8, 5, 3, 4]` using Lomuto partition with last element as pivot.

**Initial:** pivot = 4, array = `[7, 2, 1, 6, 8, 5, 3, 4]`

Partition pass:
- Scan left to right, maintain `i` = boundary of "≤ pivot" region.
- After partition: `[2, 1, 3, 4, 8, 5, 7, 6]` — pivot 4 is at index 3 (final position).

Recurse:
- Left of pivot: `[2, 1, 3]` → sort recursively.
- Right of pivot: `[8, 5, 7, 6]` → sort recursively.

Continue recursively until single elements.

Final result: `[1, 2, 3, 4, 5, 6, 7, 8]`

Recursion depth (best case): log₂(8) = 3.

---

## Code Examples

### Example 1: Lomuto Partition Quick Sort (Simple Form)

#### Go

```go
package main

import "fmt"

func QuickSort(arr []int) {
    quickSort(arr, 0, len(arr)-1)
}

func quickSort(arr []int, lo, hi int) {
    if lo < hi {
        p := partitionLomuto(arr, lo, hi)
        quickSort(arr, lo, p-1)
        quickSort(arr, p+1, hi)
    }
}

func partitionLomuto(arr []int, lo, hi int) int {
    pivot := arr[hi]
    i := lo - 1
    for j := lo; j < hi; j++ {
        if arr[j] <= pivot {
            i++
            arr[i], arr[j] = arr[j], arr[i]
        }
    }
    arr[i+1], arr[hi] = arr[hi], arr[i+1]
    return i + 1
}

func main() {
    data := []int{7, 2, 1, 6, 8, 5, 3, 4}
    QuickSort(data)
    fmt.Println(data) // [1 2 3 4 5 6 7 8]
}
```

#### Java

```java
import java.util.Arrays;

public class QuickSort {
    public static void sort(int[] arr) {
        sort(arr, 0, arr.length - 1);
    }

    private static void sort(int[] a, int lo, int hi) {
        if (lo < hi) {
            int p = partition(a, lo, hi);
            sort(a, lo, p - 1);
            sort(a, p + 1, hi);
        }
    }

    private static int partition(int[] a, int lo, int hi) {
        int pivot = a[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j] <= pivot) {
                i++;
                int t = a[i]; a[i] = a[j]; a[j] = t;
            }
        }
        int t = a[i+1]; a[i+1] = a[hi]; a[hi] = t;
        return i + 1;
    }

    public static void main(String[] args) {
        int[] data = {7, 2, 1, 6, 8, 5, 3, 4};
        sort(data);
        System.out.println(Arrays.toString(data));
    }
}
```

#### Python

```python
def quick_sort(arr):
    _sort(arr, 0, len(arr) - 1)

def _sort(arr, lo, hi):
    if lo < hi:
        p = partition_lomuto(arr, lo, hi)
        _sort(arr, lo, p - 1)
        _sort(arr, p + 1, hi)

def partition_lomuto(arr, lo, hi):
    pivot = arr[hi]
    i = lo - 1
    for j in range(lo, hi):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i+1], arr[hi] = arr[hi], arr[i+1]
    return i + 1

if __name__ == "__main__":
    data = [7, 2, 1, 6, 8, 5, 3, 4]
    quick_sort(data)
    print(data)
```

### Example 2: Hoare Partition (Original — Faster)

#### Python

```python
def quick_sort_hoare(arr):
    _sort_h(arr, 0, len(arr) - 1)

def _sort_h(arr, lo, hi):
    if lo < hi:
        p = partition_hoare(arr, lo, hi)
        _sort_h(arr, lo, p)
        _sort_h(arr, p + 1, hi)

def partition_hoare(arr, lo, hi):
    pivot = arr[(lo + hi) // 2]
    i, j = lo - 1, hi + 1
    while True:
        i += 1
        while arr[i] < pivot:
            i += 1
        j -= 1
        while arr[j] > pivot:
            j -= 1
        if i >= j:
            return j
        arr[i], arr[j] = arr[j], arr[i]
```

**Note:** Hoare uses ~3× fewer swaps than Lomuto in practice, but the boundary semantics differ: Hoare returns an index inside the partition (not the pivot index).

### Example 3: Random Pivot (Avoids O(n²) on Sorted Input)

#### Python

```python
import random

def quick_sort_random(arr):
    _sort_r(arr, 0, len(arr) - 1)

def _sort_r(arr, lo, hi):
    if lo < hi:
        # Pick random pivot, swap to end
        idx = random.randint(lo, hi)
        arr[idx], arr[hi] = arr[hi], arr[idx]
        p = partition_lomuto(arr, lo, hi)
        _sort_r(arr, lo, p - 1)
        _sort_r(arr, p + 1, hi)
```

**Why?** With random pivot, expected time is O(n log n) regardless of input order. No adversarial input can force O(n²).

---

## Coding Patterns

### Pattern 1: Lomuto Partition

```python
def lomuto(arr, lo, hi):
    pivot = arr[hi]
    i = lo - 1
    for j in range(lo, hi):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i+1], arr[hi] = arr[hi], arr[i+1]
    return i + 1
```

Simpler to understand. Used in CLRS textbook.

### Pattern 2: Hoare Partition (Original)

```python
def hoare(arr, lo, hi):
    pivot = arr[(lo + hi) // 2]
    i, j = lo - 1, hi + 1
    while True:
        while True:
            i += 1
            if arr[i] >= pivot: break
        while True:
            j -= 1
            if arr[j] <= pivot: break
        if i >= j: return j
        arr[i], arr[j] = arr[j], arr[i]
```

Faster (fewer swaps) but trickier to get right.

### Pattern 3: 3-Way Partition (Dutch National Flag)

For arrays with many duplicates, partition into 3 regions: `< pivot`, `== pivot`, `> pivot`.

```python
def three_way_partition(arr, lo, hi):
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
    return lt, gt  # equal-pivot region: arr[lt..gt]
```

Used by Java's Dual-Pivot Quicksort and Pdqsort. O(n) on arrays with O(1) distinct values.

---

## Error Handling

| Error | Cause | Fix |
|-------|-------|-----|
| `RecursionError` / `StackOverflow` | Worst-case O(n) recursion depth on bad pivot | Use random pivot or median-of-three; tail-recursion optimization |
| `IndexError` | Off-by-one in partition (lo, hi, i, j) | Verify boundary conditions carefully |
| Wrong output | Comparator semantics flipped | Use `<=` and `>=` consistently |
| Infinite loop in Hoare | Forgot to advance i/j past pivot | Use do-while pattern or pre-increment |
| O(n²) on sorted input | First/last as pivot on sorted | Switch to random or median-of-three |

---

## Performance Tips

- **Always use random or median-of-three pivot** — first/last is a footgun on sorted input.
- **Switch to Insertion Sort for small subarrays** (n ≤ 16-32). Used by Pdqsort, Introsort, Dual-Pivot.
- **Recurse on smaller partition first**, iterate on larger — bounds stack to O(log n).
- **Use 3-way partition** when input may have many duplicates.
- **Use Pdqsort** in production (Go's `sort.Slice` already does).

---

## Best Practices

- Document the pivot strategy in your function comment.
- Add an Insertion Sort cutoff at n ≤ 16-32.
- Use 3-way partition if your data has many duplicates.
- For production: use the language's built-in sort. Don't roll your own Quick Sort.
- For latency-sensitive paths: prefer Merge Sort or Heap Sort (predictable O(n log n)).

---

## Edge Cases & Pitfalls

- **Empty array** → `lo < hi` is false → no work.
- **Single element** → same.
- **All equal** → vanilla Quick Sort is O(n²) (pivot doesn't divide). 3-way partition is O(n).
- **Already sorted** → with first-as-pivot: O(n²). With random pivot: O(n log n).
- **Reverse sorted** → same as already sorted: bad pivot is catastrophic.
- **Duplicates** → 3-way partition.
- **Linked list** → don't use Quick Sort; use Merge Sort.
- **Floating-point with NaN** → undefined behavior; pre-filter NaN.

---

## Common Mistakes

1. **Using first or last element as pivot** → O(n²) on sorted input.
2. **Off-by-one in Lomuto partition** → wrong pivot position.
3. **Forgetting Hoare returns a different index than Lomuto** → wrong recursive bounds.
4. **Not handling duplicates** → vanilla Quick Sort is O(n²) on all-equal arrays.
5. **Saying "Quick Sort is in-place"** without noting O(log n) stack space.
6. **Recursion depth blow-up** → use tail-recursion optimization.

---

## Cheat Sheet

```text
Quick Sort

ALGORITHM:
  sort(arr, lo, hi):
    if lo < hi:
      p = partition(arr, lo, hi)
      sort(arr, lo, p - 1)
      sort(arr, p + 1, hi)

LOMUTO PARTITION:
  pivot = arr[hi]
  i = lo - 1
  for j in lo..hi-1:
    if arr[j] <= pivot:
      i++; swap(arr[i], arr[j])
  swap(arr[i+1], arr[hi])
  return i + 1

COMPLEXITY:
  Best:   O(n log n)
  Avg:    O(n log n)
  Worst:  O(n²)        bad pivot
  Space:  O(log n) stack avg, O(n) worst
  Stable: NO (typical)
  In-place: YES

PRODUCTION USE:
  - Pdqsort (Go, Rust)
  - Dual-Pivot Quicksort (Java primitives)
  - Introsort (C++ STL)
```

---

## Visual Animation

> See [`animation.html`](./animation.html) for interactive visualization.
> Shows partition step with pivot, two-pointer movement, recursive subarrays.

---

## Summary

Quick Sort: **divide-and-conquer with partition (no merge)**. Pick pivot, partition into ≤/≥ regions, recurse. **O(n log n) average**, **O(n²) worst** (mitigated by Pdqsort/random pivot). **In-place**, **NOT stable**, **cache-friendly**. The fastest in-practice sort for in-memory random data.

Production variants:
- **Pdqsort:** Go, Rust — never O(n²) in practice.
- **Dual-Pivot Quicksort:** Java primitives — ~5% faster.
- **Introsort:** C++ STL — Quick + Heap Sort fallback.

Two takeaways:
1. **Always use random or median-of-three pivot** — never first/last.
2. **For production: use the built-in.** It's already Quick Sort, optimized over decades.

---

## Further Reading

- CLRS Chapter 7 (Quicksort)
- Sedgewick & Wayne, Algorithms 4ed §2.3
- Tony Hoare, "Quicksort" (1962, Computer Journal)
- Pdqsort paper — [arxiv.org/abs/2106.05123](https://arxiv.org/abs/2106.05123)
- Yaroslavskiy's Dual-Pivot Quicksort — used by Java
- Go: [`sort` package](https://pkg.go.dev/sort)
- Java: [`Arrays.sort(int[])`](https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html)
