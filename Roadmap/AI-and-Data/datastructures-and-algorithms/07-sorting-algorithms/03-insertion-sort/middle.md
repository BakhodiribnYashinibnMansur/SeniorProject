# Insertion Sort — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Deeper Concepts](#deeper-concepts)
3. [Loop Invariants](#loop-invariants)
4. [Comparison with Bubble & Selection](#comparison-with-bubble--selection)
5. [Variants](#variants)
6. [Why Hybrid Sorts Use Insertion Sort](#why-hybrid-sorts-use-insertion-sort)
7. [Code Examples](#code-examples)
8. [Performance Analysis](#performance-analysis)
9. [Cache and Memory Effects](#cache-and-memory-effects)
10. [Summary](#summary)

---

## Introduction

> Focus: "Why does Insertion Sort beat its O(n²) siblings?"

At middle level, you understand that Insertion Sort is the **best** of the three classical O(n²) sorts (Bubble, Selection, Insertion). It does fewer writes than Bubble (shift, not swap), fewer comparisons than Selection on partially-sorted data, and is the only one that's truly **adaptive** AND **online**.

You also see why every production hybrid sort uses Insertion Sort for small arrays: at n ≤ 32, the recursion overhead and cache miss cost of O(n log n) sorts exceeds Insertion Sort's actual work. Pdqsort, TimSort, and Java's Dual-Pivot Quick Sort all switch to Insertion below 16-47.

---

## Deeper Concepts

### Concept 1: Number of Inversions = Number of Shifts

Each shift moves one element across one inversion. Insertion Sort performs **exactly inv(A) shifts** — same as Bubble Sort's swaps.

For random input: E[inv] = n(n-1)/4 → O(n²) shifts.
For sorted: 0 shifts → O(n).
For nearly-sorted (k inversions): O(n + k) → near-linear.

### Concept 2: Comparisons = Shifts + 1 Per Iteration

Each outer iteration does (shifts + 1) comparisons (the +1 is the comparison that exits the inner while). Total comparisons ≈ inversions + n.

### Concept 3: Adaptive Performance

Sorts where time depends on inversions (like Insertion Sort) are called **adaptive**. They run in O(n + k) where k = number of inversions. For nearly-sorted real-world data (logs, time-series), this is huge.

---

## Loop Invariants

**Outer Invariant:** At start of iteration `i`, `arr[0..i-1]` is sorted and is a permutation of the original `arr[0..i-1]`.

**Inner Invariant:** At start of iteration of the inner while-loop, `arr[j+1..i]` are all > `x`, and `arr[0..j]` is sorted.

**Termination:** Inner exits when `j == -1` or `arr[j] <= x`. In either case, position `j+1` is the correct place to insert `x`. The new `arr[0..i]` is sorted.

---

## Comparison with Bubble & Selection

| Attribute | Insertion | Bubble | Selection |
|-----------|-----------|--------|-----------|
| Best time | O(n) | O(n) (early exit) | O(n²) |
| Avg time | O(n²) | O(n²) | O(n²) |
| Worst time | O(n²) | O(n²) | O(n²) |
| Comparisons (avg) | n²/4 | n²/2 | n²/2 |
| Writes (avg) | n²/4 | n²/4 | n |
| Stable | Yes | Yes | No (typical) |
| Adaptive | **Yes (very)** | Yes (modest) | No |
| Online | **Yes** | No | No |
| Best of 3 for | Most cases | Teaching only | Fewest writes (flash memory) |

**Insertion Sort wins for:** general small-array sorting, nearly-sorted data, online insertion.

**Selection Sort wins for:** when writes are very expensive (e.g., flash memory).

**Bubble Sort wins for:** never (educational only).

---

## Variants

### Binary Insertion Sort

Use binary search to find insertion position. Reduces comparisons from O(n²) to O(n log n). Shifts still O(n²).

**When useful:** comparisons expensive (long string comparison, complex objects).

### Shell Sort

Generalization: insert with a "gap" instead of 1. Sort with gap=h, then h/2, ..., until h=1. Subquadratic in practice (depends on gap sequence).

```python
def shell_sort(arr):
    n = len(arr)
    gap = n // 2
    while gap > 0:
        for i in range(gap, n):
            x = arr[i]; j = i
            while j >= gap and arr[j - gap] > x:
                arr[j] = arr[j - gap]
                j -= gap
            arr[j] = x
        gap //= 2
```

**Time:** O(n^1.3) average with good gap sequence (Ciura's). Beats Insertion Sort, worse than Merge Sort.

### Tree Insertion Sort

Insert into a balanced BST (AVL/RB), then in-order traverse. O(n log n) but defeats the purpose of "simple in-place sort."

---

## Why Hybrid Sorts Use Insertion Sort

For small subarrays (n ≤ 16-32), Insertion Sort is **faster than O(n log n) sorts** because:

1. **No recursion overhead** (function call cost ~50ns dominates for tiny n).
2. **Better cache locality** (small array fits in L1).
3. **Simple control flow** (good for branch prediction).
4. **Few comparisons** on nearly-sorted data.

Production examples:
- **TimSort:** uses Insertion for runs < `minrun` (32-64).
- **Pdqsort (Go's sort):** Insertion for n < 24.
- **Java Dual-Pivot Quicksort:** Insertion for n ≤ 47.
- **C++ std::sort (Introsort):** Insertion for n ≤ 16.

---

## Code Examples

### Generic Insertion Sort with Comparator

#### Go

```go
func InsertionSortBy[T any](arr []T, less func(a, b T) bool) {
    for i := 1; i < len(arr); i++ {
        x := arr[i]
        j := i - 1
        for j >= 0 && less(x, arr[j]) {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = x
    }
}
```

#### Java

```java
import java.util.Comparator;
public class InsertionGeneric {
    public static <T> void sort(T[] arr, Comparator<T> cmp) {
        for (int i = 1; i < arr.length; i++) {
            T x = arr[i];
            int j = i - 1;
            while (j >= 0 && cmp.compare(x, arr[j]) < 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = x;
        }
    }
}
```

#### Python

```python
def insertion_sort_by(arr, key=lambda x: x, reverse=False):
    for i in range(1, len(arr)):
        x = arr[i]; kx = key(x); j = i - 1
        while j >= 0:
            kj = key(arr[j])
            if (kj < kx) if reverse else (kj > kx):
                arr[j + 1] = arr[j]; j -= 1
            else: break
        arr[j + 1] = x
```

---

## Performance Analysis

For n = 10,000 random ints (Go):

| Sort | Time |
|------|------|
| Insertion Sort | 55 ms |
| Bubble Sort | 145 ms |
| Selection Sort | 80 ms |
| sort.Ints (Pdqsort) | 1.3 ms |

For n = 10,000 nearly-sorted (5% perturbation):

| Sort | Time |
|------|------|
| Insertion Sort | **5 ms** ← wins on nearly-sorted |
| Bubble Sort | 80 ms |
| Merge Sort | 12 ms |

**Lesson:** On nearly-sorted data, Insertion Sort can beat O(n log n) sorts.

---

## Cache and Memory Effects

Insertion Sort's access pattern is **highly localized**:
- Inner loop touches `arr[j..j+1]` (2 adjacent cells).
- Outer loop progresses linearly.
- Total: O(n²) reads but on a small working set → cache-friendly.

For n ≤ L1 cache size (~8000 ints), Insertion Sort runs at full memory bandwidth. That's the secondary reason it wins for small subarrays.

---

## Summary

Insertion Sort: **adaptive O(n²) sort** that beats Bubble and Selection in practice, and beats O(n log n) sorts for small or nearly-sorted arrays. The standard small-array fallback in TimSort, Pdqsort, and Java's Dual-Pivot Quicksort. Online (sort as data arrives). Use it for n ≤ 32 or as a hybrid building block.
