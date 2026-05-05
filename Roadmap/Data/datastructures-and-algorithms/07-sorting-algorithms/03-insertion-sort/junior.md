# Insertion Sort — Junior Level

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

> Focus: "What is Insertion Sort?" and "Why do hybrid sorts use it?"

**Insertion Sort** builds the final sorted array one element at a time, exactly like sorting a hand of playing cards. You pick up cards one by one from the dealer (left to right) and insert each one into the correct position among the cards already in your hand.

Insertion Sort is **stable**, **in-place**, **O(n²) average**, but **O(n) on already-sorted input**. It's the **fastest O(n²) sort in practice** — about 2.5× faster than Bubble Sort and ~20% faster than Selection Sort on random data.

Most importantly, Insertion Sort is the **small-array fallback** in nearly every production sort:
- **TimSort** uses Insertion Sort for runs smaller than 32-64 elements.
- **Pdqsort** (Go, Rust) uses Insertion Sort below threshold 24.
- **Java's Dual-Pivot Quicksort** uses Insertion Sort for n ≤ 47.

For **nearly-sorted** data (most real-world data!), Insertion Sort runs in nearly O(n) — better than O(n log n) sorts in practice. That's why it's the small-array primitive.

---

## Prerequisites

- **Required:** Loops, arrays, swapping/shifting elements
- **Required:** Understanding "in-place" and "stable" sort terminology
- **Helpful:** Familiarity with Bubble Sort to compare approaches

---

## Glossary

| Term | Definition |
|------|-----------|
| **Insertion** | Placing a new element into its correct position in a sorted sequence |
| **Sorted Prefix** | At iteration `i`, `arr[0..i-1]` is already sorted |
| **Shift** | Moving elements one position to the right to make room for insertion |
| **Adaptive** | Faster on partially-sorted input |
| **Online** | Can sort as data arrives (one element at a time) — Insertion Sort qualifies |
| **Hybrid Sort** | Combines two algorithms; e.g., TimSort = Merge + Insertion |

---

## Core Concepts

### Concept 1: Maintain a Sorted Prefix

After processing element `i`, the prefix `arr[0..i]` is sorted. You start with `arr[0..0]` (one element, trivially sorted), then extend by one each iteration.

### Concept 2: Insert by Shifting, Not Swapping

For each new element `x = arr[i]`:
1. Find its correct position `j` in the sorted prefix (scan right-to-left).
2. **Shift** all elements `arr[j..i-1]` one position right.
3. Place `x` at position `j`.

This uses **assignments** (writes), not swaps. Each assignment is one write; a swap is three writes. So Insertion Sort does ~3× fewer writes than Bubble Sort.

### Concept 3: Adaptive — Best Case is O(n)

If the array is already sorted, the inner while-loop never executes (the new element is always larger than its predecessor). Total work: just `n-1` comparisons → O(n).

### Concept 4: Online — Can Sort Streaming Data

You can call `insert(arr, x)` for each new element as it arrives, keeping `arr` sorted at all times. No other O(n²) sort works this way.

### Concept 5: Stable — Equal Elements Stay in Order

The inner loop stops at `arr[j-1] <= x` (using `<=`, not `<`). This prevents shifting past an equal element, preserving original order.

---

## Big-O Summary

| Case | Time | Notes |
|------|------|-------|
| Best (sorted) | **O(n)** | Inner loop never runs |
| Average | **O(n²)** | ~n²/4 shifts |
| Worst (reverse sorted) | **O(n²)** | n²/2 shifts |
| Space | **O(1)** | In-place |
| Stable | **Yes** | |
| Adaptive | **Yes** | |
| Online | **Yes** | |

---

## Real-World Analogies

| Concept | Analogy |
|---------|--------|
| **Insertion** | Sorting a hand of cards — pick a new card, insert into the right place |
| **Sorted prefix** | Books on a shelf, sorted by title; new book inserted into right slot |
| **Shifting** | Pushing existing cards to the right to make room |
| **Online sort** | A queue check-in: each new arrival placed in the line by priority |

---

## Pros & Cons

| Pros | Cons |
|------|------|
| Simple — fits in 5 lines | O(n²) average — bad for large n |
| In-place, O(1) extra space | Slow on random data > ~50 elements |
| Stable | Many writes (shifts) |
| Adaptive — O(n) on sorted | Beaten by Quick/Merge for big n |
| Online — sort as data arrives | Cache hits help but Big-O dominates |
| Fastest O(n²) sort in practice | |

**When to use:**
- Small arrays (n ≤ 16-50)
- Nearly-sorted data
- Online / streaming insertion
- As the small-array fallback in hybrid sorts (TimSort, Pdqsort)

**When NOT to use:**
- Random arrays with n > 50 — use Merge/Quick Sort

---

## Step-by-Step Walkthrough

Sort `[5, 2, 4, 6, 1, 3]`:

```
Initial:  [5, 2, 4, 6, 1, 3]
i=1: x=2. Shift 5→position 1, place 2 at 0. [2, 5, 4, 6, 1, 3]
i=2: x=4. Shift 5→position 2, place 4 at 1. [2, 4, 5, 6, 1, 3]
i=3: x=6. 6 ≥ 5, no shift.                  [2, 4, 5, 6, 1, 3]
i=4: x=1. Shift 6,5,4,2→right; place 1 at 0. [1, 2, 4, 5, 6, 3]
i=5: x=3. Shift 6,5,4→right; place 3 at 2.   [1, 2, 3, 4, 5, 6]
```

---

## Code Examples

### Example 1: Standard Insertion Sort

#### Go

```go
package main

import "fmt"

func InsertionSort(arr []int) {
    for i := 1; i < len(arr); i++ {
        x := arr[i]
        j := i - 1
        for j >= 0 && arr[j] > x {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = x
    }
}

func main() {
    data := []int{5, 2, 4, 6, 1, 3}
    InsertionSort(data)
    fmt.Println(data) // [1 2 3 4 5 6]
}
```

#### Java

```java
import java.util.Arrays;

public class InsertionSort {
    public static void sort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int x = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > x) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = x;
        }
    }

    public static void main(String[] args) {
        int[] data = {5, 2, 4, 6, 1, 3};
        sort(data);
        System.out.println(Arrays.toString(data));
    }
}
```

#### Python

```python
def insertion_sort(arr):
    """In-place ascending sort. Best O(n), Avg/Worst O(n^2). Stable."""
    for i in range(1, len(arr)):
        x = arr[i]
        j = i - 1
        while j >= 0 and arr[j] > x:
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = x

if __name__ == "__main__":
    data = [5, 2, 4, 6, 1, 3]
    insertion_sort(data)
    print(data)
```

### Example 2: Binary Insertion Sort (find position via binary search)

#### Python

```python
import bisect

def binary_insertion_sort(arr):
    """Use binary search to find insertion position. O(n²) shifts but O(n log n) comparisons."""
    for i in range(1, len(arr)):
        x = arr[i]
        pos = bisect.bisect_left(arr, x, 0, i)
        # shift right and insert
        arr[pos+1:i+1] = arr[pos:i]
        arr[pos] = x
```

**Win:** Reduces comparisons from O(n²) to O(n log n). Shifts still O(n²). Useful when comparisons are expensive (e.g., string comparison).

### Example 3: Online Insertion (sorting a stream)

#### Python

```python
def insert_sorted(arr, x):
    """Insert x into already-sorted arr, maintaining order."""
    i = len(arr) - 1
    arr.append(0)  # extend
    while i >= 0 and arr[i] > x:
        arr[i+1] = arr[i]
        i -= 1
    arr[i+1] = x

# Usage: stream
sorted_arr = []
for x in [5, 2, 4, 6, 1, 3]:
    insert_sorted(sorted_arr, x)
print(sorted_arr)  # [1, 2, 3, 4, 5, 6]
```

---

## Coding Patterns

### Pattern 1: Shift-and-Insert (instead of swap)

```python
x = arr[i]
j = i - 1
while j >= 0 and arr[j] > x:
    arr[j + 1] = arr[j]   # shift right
    j -= 1
arr[j + 1] = x            # insert
```

### Pattern 2: Sentinel Insertion (Knuth)

```python
# Place a sentinel at arr[0] = -infinity. Removes the j >= 0 check.
arr[0] = float('-inf')
for i in range(2, len(arr)):
    x = arr[i]
    j = i - 1
    while arr[j] > x:  # no bound check needed
        arr[j + 1] = arr[j]
        j -= 1
    arr[j + 1] = x
```

Saves one check per inner iteration. Tiny speedup.

---

## Error Handling

| Error | Cause | Fix |
|-------|-------|-----|
| `IndexError` | `j` becomes -1 and accessed | Use `j >= 0` guard |
| Wrong order | Used `<` instead of `>` in inner | Verify direction matches sort order |
| Lost stability | Used `>=` instead of `>` | Use strict `>` for stability |
| Slow on big n | Wrong tool | Use Merge/Quick Sort for n > 50 |

---

## Performance Tips

- **Use shift, not swap** — 3× fewer writes than Bubble Sort.
- **Binary insertion** when comparisons are expensive.
- **Combine with Merge/Quick Sort** for hybrids — Insertion handles n ≤ 16-32 subarrays.
- **Use sentinel** to skip the bound check in inner loop (saves ~5%).

---

## Best Practices

- Always include the **`>` (strict)** comparison for stability.
- Test with: empty, single element, sorted, reverse-sorted, all-equal, with duplicates.
- For production: use built-in sort, not raw Insertion Sort.
- **Use Insertion Sort as small-array fallback** in your own divide-and-conquer code.

---

## Edge Cases & Pitfalls

- **Empty array** → outer loop body skipped. Safe.
- **Single element** → loop range `range(1, 1)` is empty. Safe.
- **All equal** → strict `>` means inner loop never runs → O(n).
- **Already sorted** → best case, O(n).
- **Reverse sorted** → worst case, O(n²) — shift everything left every iteration.

---

## Common Mistakes

1. **Using `>=` in inner loop** → loses stability.
2. **Forgetting `j >= 0` guard** → IndexError when `x` is the new minimum.
3. **Storing `arr[i]` after shifting** → original value lost. Save `x = arr[i]` BEFORE shifting.
4. **Off-by-one: writing `arr[j]` instead of `arr[j+1]`** → wrong result.
5. **Using swap instead of shift** → 3× slower in writes, same Big-O but worse constant.

---

## Cheat Sheet

```text
Insertion Sort

ALGORITHM:
  for i in 1..n-1:
    x = arr[i]
    j = i - 1
    while j >= 0 and arr[j] > x:
      arr[j+1] = arr[j]
      j -= 1
    arr[j+1] = x

COMPLEXITY:
  Best:   O(n)        sorted input
  Avg:    O(n^2)
  Worst:  O(n^2)      reverse sorted
  Space:  O(1)
  Stable: YES (with strict >)
  Adaptive: YES
  Online: YES

USE FOR:
  - n ≤ 16-50
  - Nearly-sorted
  - Streaming (online insert)
  - Small-array fallback in hybrid sorts
```

---

## Visual Animation

> See [`animation.html`](./animation.html) for interactive visualization.
> Shows: card-style insertion, shift animation, color-coded sorted prefix vs. unsorted suffix.

---

## Summary

Insertion Sort builds a sorted prefix by inserting each new element into its correct position via shifting. **Best O(n)**, **average O(n²)**, **worst O(n²)**, **in-place**, **stable**, **adaptive**, **online**. Faster than Bubble Sort and Selection Sort on average; the **standard small-array fallback** in TimSort, Pdqsort, and Java's Dual-Pivot Quicksort.

Two takeaways:
1. Shift-and-insert (not swap) — 3× fewer writes.
2. Use Insertion Sort as the n ≤ 16 fallback inside any hybrid sort.

---

## Further Reading

- CLRS §2.1 (Insertion Sort)
- Sedgewick & Wayne, Algorithms 4ed §2.1
- Knuth TAOCP Vol. 3, §5.2.1
- TimSort spec — uses Insertion for "minrun"
- Pdqsort paper — uses Insertion for small subarrays
