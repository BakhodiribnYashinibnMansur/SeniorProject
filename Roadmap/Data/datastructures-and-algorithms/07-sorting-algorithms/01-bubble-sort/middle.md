# Bubble Sort — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Deeper Concepts](#deeper-concepts)
3. [Invariants and Loop Analysis](#invariants-and-loop-analysis)
4. [Recurrence Relations](#recurrence-relations)
5. [Comparison with Alternatives](#comparison-with-alternatives)
6. [Adversarial Inputs](#adversarial-inputs)
7. [Variants of Bubble Sort](#variants-of-bubble-sort)
8. [Advanced Patterns](#advanced-patterns)
9. [Code Examples](#code-examples)
10. [Error Handling](#error-handling)
11. [Performance Analysis](#performance-analysis)
12. [Best Practices](#best-practices)
13. [Hardware and Cache Effects](#hardware-and-cache-effects)
14. [Visual Animation](#visual-animation)
15. [Summary](#summary)

---

## Introduction

> Focus: "Why does Bubble Sort work?" and "When (rarely) should I choose it?"

At the middle level, you stop running Bubble Sort and start *analyzing* it. You learn to **prove** it sorts correctly via loop invariants, you derive its expected number of swaps using **inversion counting**, and you understand **why** it loses to Insertion Sort on essentially every input. You also meet its variants — **cocktail shaker sort**, **comb sort**, **odd-even transposition sort** — and understand which problems each variant addresses.

You'll see that Bubble Sort's terrible reputation is partly deserved (O(n²) is bad for big arrays) and partly mythology (its constant factor is fine; the real loss is to its sibling Insertion Sort, not to merge or quick sort). And you'll learn the one place Bubble Sort is genuinely interesting in modern systems: **odd-even transposition sort** — the natural parallelization of bubble sort — runs in O(n) parallel time on n processors, making it the building block for sorting networks.

---

## Deeper Concepts

### Invariant 1: After Pass i, the i+1 Largest Elements Are in Their Final Positions

This is the **structural invariant** that makes Bubble Sort work. Specifically:

> **Loop Invariant (outer loop):** After iteration `i` of the outer loop completes, `arr[n-1-i .. n-1]` contains the `i+1` largest elements of the original array, in sorted order.

If this invariant holds for all `i` from 0 to `n-1`, then after `n-1` passes, the entire array is sorted.

### Invariant 2: One Swap Removes Exactly One Inversion

An **inversion** is a pair `(i, j)` with `i < j` and `arr[i] > arr[j]`. Bubble Sort's swap of adjacent elements `arr[j]` and `arr[j+1]` (when `arr[j] > arr[j+1]`) removes exactly one inversion: the pair `(j, j+1)`. It cannot create new inversions because the relative order of every other pair is unchanged.

This gives us a tight bound:

> **Total swaps = number of inversions in the input array.**

A reverse-sorted array of size `n` has `n(n-1)/2` inversions — the maximum possible. That's why reverse-sorted is Bubble Sort's worst case for swaps.

### Invariant 3: Stability Through "Strict Greater Than"

Because we swap only when `arr[j] > arr[j+1]` (strictly greater), equal elements never swap, so their relative order is preserved. Change `>` to `>=` and Bubble Sort becomes **unstable** while still being correct. This is a one-character change with significant semantic consequences.

### Concept: Inversion Distance

Beyond swap count, the **inversion distance** of an array is the minimum number of adjacent swaps required to sort it. It equals the inversion count. Therefore Bubble Sort (with the optimization of "no wasted swaps") is **optimal among adjacent-swap-only sorts**: no algorithm restricted to adjacent swaps can do better. Insertion Sort, Cocktail Sort, and Comb Sort with shrink factor 1 all share this property.

---

## Invariants and Loop Analysis

### Formal Invariant Proof

**Claim:** After `k` iterations of the outer loop (k ≥ 1), `arr[n-k .. n-1]` contains the `k` largest elements of the input, in non-decreasing order.

**Base case (k=1):** After iteration 0 of the outer loop, the inner loop has compared `(arr[0], arr[1]), (arr[1], arr[2]), ..., (arr[n-2], arr[n-1])`. Each comparison either left the larger of the two elements at the higher index, or swapped them so that the larger is at the higher index. Therefore the maximum of the array has propagated all the way to index `n-1`. ✓

**Inductive step:** Assume the claim holds after iteration `k`. Then `arr[n-k .. n-1]` contains the `k` largest, in order, untouched. Iteration `k+1`'s inner loop runs on `arr[0 .. n-1-(k+1)] = arr[0 .. n-2-k]`. By the same argument as the base case, the maximum of `arr[0 .. n-2-k]` propagates to index `n-1-k`. That maximum is the `(k+1)`-th largest of the whole array. So after iteration `k+1`, `arr[n-1-k .. n-1]` contains the `k+1` largest, in order. ✓

**Termination:** After `n-1` iterations, `arr[1 .. n-1]` contains the `n-1` largest in order. The smallest element must therefore be at `arr[0]`. The array is sorted.

**With early exit:** If iteration `k` performed no swaps, then for every adjacent pair `arr[j] ≤ arr[j+1]` — i.e., the array is sorted. So we can return immediately.

### Counting Operations

Without early exit:

| Quantity | Formula | Notes |
|----------|---------|-------|
| Comparisons | `(n-1) + (n-2) + ... + 1 = n(n-1)/2` | Inner loop shrinks each pass |
| Swaps (worst, reverse-sorted) | `n(n-1)/2` | Every comparison swaps |
| Swaps (best, sorted) | `0` | No swaps at all |
| Swaps (average, random permutation) | `n(n-1)/4` | Half the inversions on average |

For random input, the **expected number of inversions** in a permutation of size `n` is `n(n-1)/4 ≈ n²/4`. So Bubble Sort's expected swap count on random input is `n²/4`.

---

## Recurrence Relations

Bubble Sort isn't recursive in its standard form, but a recursive view helps frame it:

```text
Recursive Bubble Sort:
  bubbleSort(arr, n):
      if n <= 1: return
      bubble_largest_to_end(arr, n)   // one pass: O(n)
      bubbleSort(arr, n - 1)          // recurse on prefix

Recurrence:
  T(n) = T(n-1) + O(n)
  T(1) = O(1)

Solving:
  T(n) = O(n) + O(n-1) + ... + O(1) = O(n(n+1)/2) = O(n²)
```

This is the same complexity but illuminates why O(n²) is unavoidable: each pass takes O(n) and we need O(n) passes.

By the **Master Theorem**, this recurrence with `a=1, b=1` (no division of subproblem) is **not** in the standard form — Master Theorem doesn't apply. Use the substitution method (above) or recognize the arithmetic sum directly.

---

## Comparison with Alternatives

| Attribute | Bubble Sort | Insertion Sort | Selection Sort | Merge Sort | Quick Sort |
|-----------|-------------|----------------|----------------|------------|------------|
| Best time | O(n) (with early exit) | O(n) (sorted input) | O(n²) | O(n log n) | O(n log n) |
| Average time | O(n²) | O(n²) | O(n²) | O(n log n) | O(n log n) |
| Worst time | O(n²) | O(n²) | O(n²) | O(n log n) | O(n²) |
| Space (aux) | O(1) | O(1) | O(1) | O(n) | O(log n) stack |
| Stable? | **Yes** | **Yes** | No (typical impl) | **Yes** | No (typical impl) |
| In-place? | Yes | Yes | Yes | No | Yes |
| Adaptive? | Yes (early exit) | **Yes (very)** | No | No | Partially (Pdqsort) |
| Swaps (worst) | n²/2 | n²/2 | n-1 | 0 (uses moves) | n log n |
| Cache-friendly? | OK | OK | OK | Good | Best |
| Best for | Teaching only | Small/nearly-sorted | When swap is much cheaper than compare | Stable sort, linked lists | General-purpose, large arrays |

**Choose Bubble Sort when:** Almost never. Specifically: teaching, tiny arrays where simplicity dominates, or detecting "is this already sorted?" in O(n).

**Choose Insertion Sort over Bubble Sort when:** Always. Insertion Sort is the same Big-O but does fewer comparisons and writes on every realistic input. The only place Bubble Sort might match Insertion Sort is when "already sorted" detection is the dominant case (both achieve O(n)).

**Choose Selection Sort over Bubble Sort when:** Writes are very expensive (e.g., flash memory wear-leveling) — Selection Sort does at most `n-1` writes vs. Bubble Sort's O(n²).

**Choose Merge Sort over Bubble Sort when:** n > ~50 and you need stability or are sorting linked lists.

**Choose Quick Sort over Bubble Sort when:** n > ~50 and you have random-access memory and don't need stability.

---

## Adversarial Inputs

### Worst Case: Reverse-Sorted

`[5, 4, 3, 2, 1]` → every adjacent pair is an inversion → maximum swaps.

For n=5: 4+3+2+1 = 10 comparisons, 10 swaps.
For n=1000: ~500,000 comparisons and swaps.

### Worst Case for Early Exit: One Element Out of Place at the Front

`[5, 1, 2, 3, 4]` (one big element at the start).

Pass 1 carries the `5` all the way to the end — n-1 comparisons, n-1 swaps. After pass 1, the array is `[1, 2, 3, 4, 5]`. Pass 2 runs (because pass 1 had swaps), finds zero swaps, and exits.

Total: 2(n-1) comparisons. **Linear**, not quadratic — the "rabbit" (large value at the front) moves fast.

### "Turtles" — The Achilles Heel

`[2, 3, 4, 5, 1]` (one small element at the end).

Pass 1: 1 only moves left by ONE position per pass (small values "drift" left slowly).
- After pass 1: `[2, 3, 4, 1, 5]`
- After pass 2: `[2, 3, 1, 4, 5]`
- After pass 3: `[2, 1, 3, 4, 5]`
- After pass 4: `[1, 2, 3, 4, 5]`

This requires `n-1` passes, each O(n) — **back to O(n²)**. The naming "turtles" comes from Knuth: small values near the end are the slowest to migrate to their correct position.

**Cocktail Sort fixes this** by alternating direction, letting small values move right-to-left in alternating passes.

---

## Variants of Bubble Sort

### Variant 1: Cocktail Shaker Sort (Bidirectional Bubble Sort)

Alternate left-to-right and right-to-left passes. Each pass shrinks the active range from both ends. Solves the "turtle" problem.

**Complexity:** Same Big-O (O(n²) worst), but ~2× faster in practice on inputs with both rabbits and turtles.

### Variant 2: Odd-Even Transposition Sort

Compare and swap pairs `(0,1), (2,3), (4,5), ...` in odd phase, then `(1,2), (3,4), (5,6), ...` in even phase. Repeat until sorted.

**Why?** Each phase has independent comparisons — they can run **in parallel**. With `n/2` processors, sorts in O(n) parallel time. Foundation of sorting networks.

### Variant 3: Comb Sort

Bubble Sort with a shrinking gap. Initially compare `arr[i]` to `arr[i + gap]` (gap = n / 1.3); shrink gap each pass; when gap = 1, becomes ordinary bubble sort. Solves turtles by allowing long-distance swaps early.

**Complexity:** Average ~O(n² / 2^p) for p passes; in practice **closer to O(n log n)** for many inputs. Not theoretically O(n log n) but empirically much faster than vanilla Bubble Sort.

### Variant 4: Recursive Bubble Sort

Same algorithm, recursive form: do one pass, recurse on `arr[0 .. n-2]`. Pedagogical only — no performance benefit.

### Variant 5: Bubble Sort on a Linked List

Possible but inefficient: each "swap" requires updating up to 6 pointers (prev, current, next on both nodes). Use Merge Sort for linked lists instead.

---

## Advanced Patterns

### Pattern: Track and Use the Last Swap Position

After a pass, the last swap occurred at index `k`. Everything from `k+1` onward is sorted (because no swaps happened there in this pass). Set the next pass's upper bound to `k` instead of `n-1-i`.

#### Go

```go
func BubbleSortAdaptive(arr []int) {
    n := len(arr)
    end := n - 1
    for end > 0 {
        lastSwap := 0
        for j := 0; j < end; j++ {
            if arr[j] > arr[j+1] {
                arr[j], arr[j+1] = arr[j+1], arr[j]
                lastSwap = j
            }
        }
        end = lastSwap // skip already-sorted suffix
    }
}
```

#### Java

```java
public static void bubbleSortAdaptive(int[] arr) {
    int n = arr.length;
    int end = n - 1;
    while (end > 0) {
        int lastSwap = 0;
        for (int j = 0; j < end; j++) {
            if (arr[j] > arr[j + 1]) {
                int tmp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = tmp;
                lastSwap = j;
            }
        }
        end = lastSwap;
    }
}
```

#### Python

```python
def bubble_sort_adaptive(arr):
    """Tracks the last swap position to skip already-sorted suffix."""
    n = len(arr)
    end = n - 1
    while end > 0:
        last_swap = 0
        for j in range(end):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                last_swap = j
        end = last_swap
```

### Pattern: Cocktail Shaker Sort (Full)

#### Go

```go
func CocktailSort(arr []int) {
    n := len(arr)
    start, end := 0, n-1
    for start < end {
        // Forward pass: bubble largest to end
        newEnd := start
        for j := start; j < end; j++ {
            if arr[j] > arr[j+1] {
                arr[j], arr[j+1] = arr[j+1], arr[j]
                newEnd = j
            }
        }
        end = newEnd
        if start >= end { break }
        // Backward pass: bubble smallest to start
        newStart := end
        for j := end; j > start; j-- {
            if arr[j-1] > arr[j] {
                arr[j-1], arr[j] = arr[j], arr[j-1]
                newStart = j
            }
        }
        start = newStart
    }
}
```

#### Java

```java
public static void cocktailSort(int[] arr) {
    int n = arr.length;
    int start = 0, end = n - 1;
    while (start < end) {
        int newEnd = start;
        for (int j = start; j < end; j++) {
            if (arr[j] > arr[j + 1]) {
                int t = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = t;
                newEnd = j;
            }
        }
        end = newEnd;
        if (start >= end) break;
        int newStart = end;
        for (int j = end; j > start; j--) {
            if (arr[j - 1] > arr[j]) {
                int t = arr[j - 1]; arr[j - 1] = arr[j]; arr[j] = t;
                newStart = j;
            }
        }
        start = newStart;
    }
}
```

#### Python

```python
def cocktail_sort(arr):
    n = len(arr)
    start, end = 0, n - 1
    while start < end:
        new_end = start
        for j in range(start, end):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                new_end = j
        end = new_end
        if start >= end: break
        new_start = end
        for j in range(end, start, -1):
            if arr[j - 1] > arr[j]:
                arr[j - 1], arr[j] = arr[j], arr[j - 1]
                new_start = j
        start = new_start
```

### Pattern: Comb Sort (Bubble Sort with Shrinking Gap)

#### Go

```go
func CombSort(arr []int) {
    n := len(arr)
    gap := n
    shrink := 1.3
    sorted := false
    for !sorted {
        gap = int(float64(gap) / shrink)
        if gap <= 1 {
            gap = 1
            sorted = true
        }
        for j := 0; j+gap < n; j++ {
            if arr[j] > arr[j+gap] {
                arr[j], arr[j+gap] = arr[j+gap], arr[j]
                sorted = false
            }
        }
    }
}
```

#### Python

```python
def comb_sort(arr):
    n = len(arr)
    gap = n
    shrink = 1.3
    sorted_ = False
    while not sorted_:
        gap = int(gap / shrink)
        if gap <= 1:
            gap = 1
            sorted_ = True
        for j in range(n - gap):
            if arr[j] > arr[j + gap]:
                arr[j], arr[j + gap] = arr[j + gap], arr[j]
                sorted_ = False
```

> Comb Sort with shrink factor `1.3` (Lacey & Box, 1991) reaches "near-O(n log n)" behavior on random data despite being a Bubble Sort family member.

---

## Code Examples

### Generic Bubble Sort with Comparator and Statistics

#### Go

```go
package main

import "fmt"

type SortStats struct {
    Comparisons int
    Swaps       int
    Passes      int
}

// BubbleSortGeneric sorts a slice using a comparator and returns statistics.
func BubbleSortGeneric[T any](arr []T, less func(a, b T) bool) SortStats {
    var stats SortStats
    n := len(arr)
    for i := 0; i < n-1; i++ {
        stats.Passes++
        swapped := false
        for j := 0; j < n-1-i; j++ {
            stats.Comparisons++
            if !less(arr[j], arr[j+1]) {
                arr[j], arr[j+1] = arr[j+1], arr[j]
                stats.Swaps++
                swapped = true
            }
        }
        if !swapped { break }
    }
    return stats
}

func main() {
    data := []int{5, 1, 4, 2, 8}
    stats := BubbleSortGeneric(data, func(a, b int) bool { return a < b })
    fmt.Printf("sorted=%v, %+v\n", data, stats)
}
```

#### Java

```java
import java.util.*;
import java.util.function.*;

public class BubbleSortGeneric {
    public static class Stats {
        public int comparisons, swaps, passes;
        public String toString() {
            return String.format("comparisons=%d, swaps=%d, passes=%d",
                                 comparisons, swaps, passes);
        }
    }

    public static <T> Stats sort(T[] arr, Comparator<T> cmp) {
        Stats s = new Stats();
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            s.passes++;
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                s.comparisons++;
                if (cmp.compare(arr[j], arr[j + 1]) > 0) {
                    T t = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = t;
                    s.swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return s;
    }

    public static void main(String[] args) {
        Integer[] data = {5, 1, 4, 2, 8};
        Stats st = sort(data, Comparator.naturalOrder());
        System.out.println(Arrays.toString(data) + " " + st);
    }
}
```

#### Python

```python
from dataclasses import dataclass
from typing import Callable, TypeVar, List

T = TypeVar("T")

@dataclass
class SortStats:
    comparisons: int = 0
    swaps: int = 0
    passes: int = 0

def bubble_sort_generic(arr: List[T], less: Callable[[T, T], bool]) -> SortStats:
    stats = SortStats()
    n = len(arr)
    for i in range(n - 1):
        stats.passes += 1
        swapped = False
        for j in range(n - 1 - i):
            stats.comparisons += 1
            if not less(arr[j], arr[j + 1]):
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                stats.swaps += 1
                swapped = True
        if not swapped:
            break
    return stats

if __name__ == "__main__":
    data = [5, 1, 4, 2, 8]
    s = bubble_sort_generic(data, lambda a, b: a < b)
    print(data, s)
```

---

## Error Handling

| Scenario | What goes wrong | Correct approach |
|----------|----------------|-----------------|
| Comparator that violates total order (e.g., `a < b` and `b < a` both true) | Infinite loop or incorrect sort | Verify comparator is irreflexive, antisymmetric, and transitive — the Java `Comparator` contract |
| Sorting a `Collection<T>` directly in Java without converting to array/list | `UnsupportedOperationException` if backed by an immutable view | Wrap with `new ArrayList<>(coll)` first |
| Mutating list during iteration in Python | `RuntimeError: list changed size during iteration` (rare with index loop, but possible if sort runs in a thread) | Don't mutate from another thread; use a lock |
| `NaN` in floating-point arrays | Sort result undefined — `NaN > x` is always false | Filter NaNs first or use `math.isnan` guard |
| Mixing types (`[1, "2", 3.0]`) in Python | `TypeError` on comparison | Use a key function that maps to a uniform type |

---

## Performance Analysis

### Empirical Benchmark Code

#### Go

```go
package main

import (
    "fmt"
    "math/rand"
    "time"
)

func BubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-1-i; j++ {
            if arr[j] > arr[j+1] {
                arr[j], arr[j+1] = arr[j+1], arr[j]
                swapped = true
            }
        }
        if !swapped { return }
    }
}

func benchmark() {
    sizes := []int{100, 1000, 5000, 10000}
    for _, n := range sizes {
        data := make([]int, n)
        for i := range data { data[i] = rand.Intn(n) }
        start := time.Now()
        BubbleSort(data)
        elapsed := time.Since(start)
        fmt.Printf("n=%6d: %v\n", n, elapsed)
    }
}

func main() { benchmark() }
```

Typical results on a modern laptop (Go 1.22):

| n       | Time      | Notes             |
|---------|-----------|-------------------|
| 100     | 30 µs     | indistinguishable |
| 1,000   | 1.5 ms    |                   |
| 5,000   | 35 ms     | n² scaling visible |
| 10,000  | 145 ms    | 4× from 5k → confirms O(n²) |
| 100,000 | ~14 s     | painful           |

### Comparison with Insertion Sort (same n)

| n       | Bubble Sort | Insertion Sort | Ratio |
|---------|-------------|----------------|-------|
| 1,000   | 1.5 ms      | 0.6 ms         | 2.5×  |
| 10,000  | 145 ms      | 55 ms          | 2.6×  |
| 100,000 | 14 s        | 5.5 s          | 2.5×  |

Insertion Sort is consistently ~2.5× faster, even though both are O(n²). The reason: Insertion Sort does about half as many swaps and skips the inner loop entirely once it finds the insertion point.

---

## Best Practices

- **Validate comparator correctness** before passing to a generic sort. A non-transitive comparator can cause infinite loops or wrong results.
- **Always use early-exit** even though it adds one boolean — the cost on random input is negligible, and the gain on sorted input is huge.
- **Don't reach for Bubble Sort even for small n.** Insertion Sort dominates. The only case Bubble Sort wins is detecting "already sorted" — and even then, a single linear scan checking `arr[i] <= arr[i+1]` is more direct.
- **Document mutation.** "In-place" is a contract — callers must know their array will be modified.
- **For parallelism**, prefer Odd-Even Transposition Sort (a Bubble Sort variant) over plain Bubble Sort — it's the natural parallel form.

---

## Hardware and Cache Effects

Bubble Sort is **cache-friendly within a pass**: it touches `arr[j]` and `arr[j+1]` — sequential memory access, perfect for hardware prefetching. On modern CPUs with 64-byte cache lines, that means ~16 ints per line; one cache miss serves many comparisons.

But it's **cache-hostile across passes**: each pass walks the entire array again, evicting and reloading it from main memory if the array is larger than L1/L2. For n > a few thousand, the sheer number of passes dominates and cache locality stops helping.

Comparison:
- **Bubble Sort:** O(n²/B) cache misses where B = cache line size. Same as Insertion Sort.
- **Merge Sort:** O((n/B) log(n/M)) cache misses (M = cache size) — much better for large n.
- **Cache-oblivious sorts** (funnel sort): O((n/B) log_{M/B}(n/B)) — optimal.

For small n (below ~50, fits comfortably in L1), Bubble Sort's cache friendliness is an advantage that makes it competitive with more complex algorithms.

---

## Visual Animation

> See [`animation.html`](./animation.html) for interactive visualization.
>
> Middle-level animation features:
> - Toggle between vanilla, optimized (early exit + shrinking bound), and cocktail variants
> - Side-by-side worst-case (reverse-sorted) vs. best-case (already sorted)
> - Counter overlay: comparisons, swaps, passes, current inversion count
> - Adjustable input size and randomness slider
> - Pause + step-through

---

## Summary

At the middle level, Bubble Sort transitions from "an algorithm I run" to "an algorithm I analyze." You prove correctness via loop invariants, you bound expected work via inversion counting (`E[swaps] = n(n-1)/4` on random input), and you understand its variants — cocktail, comb, odd-even — each addressing a specific weakness. Cocktail Sort handles "turtles," Comb Sort gets near-O(n log n) empirically, and Odd-Even Transposition Sort parallelizes to O(n) on n processors.

**The bottom line for production:** Insertion Sort beats Bubble Sort on every realistic workload. Bubble Sort's enduring relevance is teaching, sorting networks, and historical interest. Move on to **Merge Sort** for the canonical O(n log n) divide-and-conquer experience and **Quick Sort** for cache-friendly speed.
