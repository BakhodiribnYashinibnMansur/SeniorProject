# Bubble Sort — Interview Preparation

## Junior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is Bubble Sort and how does it work? | Compare adjacent pairs, swap if out of order, repeat. Largest "bubbles up" each pass. |
| 2 | What is the time complexity of Bubble Sort? | Best: O(n) (early exit); Average: O(n²); Worst: O(n²). Space: O(1). |
| 3 | Is Bubble Sort stable? Why? | Yes — uses strict `>` (not `>=`), so equal elements never swap. |
| 4 | Is Bubble Sort in-place? | Yes — only one temp variable for swap. O(1) auxiliary space. |
| 5 | What is the early-exit optimization? | Use a `swapped` flag; if a pass completes with no swap, the array is sorted — stop. |
| 6 | Difference between Bubble Sort and Selection Sort? | Bubble swaps adjacent on every comparison; Selection finds min, swaps once per pass. Selection does fewer swaps. |
| 7 | What's the worst-case input for Bubble Sort? | Reverse-sorted array — every adjacent pair is an inversion, n²/2 swaps. |
| 8 | What's the best-case input? | Already-sorted — with early exit, runs in O(n). |
| 9 | When would you use Bubble Sort? | Almost never. Teaching only, or detecting "is sorted?" in one pass. |
| 10 | Implement Bubble Sort on a whiteboard. | See code in junior.md — should fit in 5-7 lines. |

## Middle Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What's an "inversion" and how does it relate to Bubble Sort? | Pair (i, j) with i<j, A[i]>A[j]. Each swap removes exactly 1 inversion. Total swaps = inv(A). |
| 2 | Why is Bubble Sort O(n²) on average? | Random permutation has E[inv] = n(n-1)/4, so E[swaps] = O(n²). |
| 3 | What is the "turtle" problem? | Small values near the end migrate left only 1 position per pass. Cocktail Sort fixes by alternating direction. |
| 4 | Compare Bubble Sort and Insertion Sort. | Same Big-O, same stability, same space. Insertion Sort is ~2.5× faster in practice (fewer swaps, smarter inner loop). |
| 5 | What is Cocktail Shaker Sort? | Bidirectional Bubble Sort. Forward pass + backward pass per iteration. Same Big-O, smaller constant. |
| 6 | What is Comb Sort? | Bubble Sort with shrinking gap (start gap = n/1.3). Empirically near O(n log n) on random data. |
| 7 | Prove Bubble Sort is correct using a loop invariant. | After pass i, the i+1 largest elements are in final positions. Induction on i. |
| 8 | Why does Bubble Sort use "strict >"? | Stability — equal elements never swap, so original order is preserved. |
| 9 | Can Bubble Sort be parallelized? | Yes — odd-even transposition sort. Compare pairs (0,1),(2,3)... in parallel; alternate phase. O(n) parallel time on n processors. |
| 10 | What is the lower bound on adjacent-swap sorts? | Ω(n²) — reverse-sorted has n(n-1)/2 inversions, each swap fixes only one. Bubble Sort matches this bound. |

## Senior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | How would you detect that someone accidentally implemented Bubble Sort in your codebase? | Latency-vs-payload graph (parabola), flame graphs showing nested loops, integration tests with realistic n that fail under O(n²). |
| 2 | Why would a database never use Bubble Sort? | Cache misses O(n²/B); for external/disk sorts, this is catastrophic. Database uses external merge sort with B-way merge. |
| 3 | Where do "Bubble-family" sorts appear in production? | GPU shaders (median filter), FPGA packet routers (oblivious latency), sorting networks for homomorphic encryption. |
| 4 | Design a "is this list sorted?" check in O(n). | Single pass: `for i in 1..n-1: if arr[i-1] > arr[i] return false; return true`. (Note: not Bubble Sort, just a single sorted-check pass.) |
| 5 | A teammate wrote `for x in list: for y in list: if x.score < y.score and ...`. What do you flag? | O(n²) anti-pattern; replace with sort + linear scan, or hash map. Add regression test with realistic n. |
| 6 | How do you safely sort a shared mutable structure under concurrency? | Snapshot-then-sort: copy → sort copy → atomic publish via AtomicReference / mutex-protected pointer swap. |
| 7 | What's the cache complexity of Bubble Sort? | O(n²/B) cache misses where B = cache line size. Bad in absolute terms but predictable. |
| 8 | When is Odd-Even Transposition Sort the right choice over a faster sort? | When you need a data-oblivious algorithm (encrypted computation), hardware pipeline (FPGA), or fixed-latency comparator network on a GPU. |
| 9 | How do you distinguish O(n²) from O(n log n) experimentally? | Double n; measure time. O(n log n): ~2.1× growth. O(n²): 4× growth. Plot log-log: slope = exponent. |
| 10 | What's the relationship between Bubble Sort and sorting networks? | Bubble Sort (no early exit) is a fixed comparator sequence — a sorting network with n(n-1)/2 comparators and n(n-1)/2 depth. Other networks (bitonic, AKS) reduce depth to O(log² n) or O(log n). |

## Professional Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Prove Bubble Sort's correctness with a loop invariant. | Outer invariant: A[n-i..n-1] holds the i largest in sorted order. Base: i=0 vacuously true. Inductive: inner loop bubbles max of A[0..n-1-i] to position n-1-i. |
| 2 | Prove the Ω(n log n) lower bound for comparison sorts. | Decision tree must have ≥ n! leaves. Binary tree depth ≥ log₂(n!) = Θ(n log n) by Stirling. |
| 3 | Prove Bubble Sort performs exactly inv(A) swaps. | Each adjacent swap removes one inversion (the (j, j+1) pair) and changes no other inversion's status. Sort terminates at inv = 0. |
| 4 | What is the 0-1 principle and how does it apply to Bubble Sort? | A sorting network sorts every input correctly iff it sorts every binary input correctly. Reduces verification of network correctness from n! to 2^n inputs. |
| 5 | Derive the expected number of inversions in a random permutation. | E[inv] = Σ_{i<j} Pr[A[i] > A[j]] = (1/2) C(n,2) = n(n-1)/4. |
| 6 | What's the parallel time complexity of Odd-Even Transposition Sort? | T_p(n) = Θ(n²/p + n). With p=n: Θ(n). Cannot beat depth Θ(n) regardless of processor count. |
| 7 | Why doesn't amortized analysis help Bubble Sort? | Every operation has the same worst-case cost (O(n) per pass). Amortization redistributes between cheap and expensive ops; Bubble Sort has only expensive ones. |
| 8 | Compare Bubble Sort to AKS sorting network in theoretical terms. | Bubble Sort: depth O(n²) sequential, O(n) parallel. AKS: depth O(log n) parallel, but constant factor ~6100. AKS is asymptotically optimal but practically useless. |
| 9 | What are the cache-miss bounds for Bubble Sort? | O(n²/B) where B = cache line size. Compare to optimal cache-oblivious O((n/B) log_{M/B}(n/B)). |
| 10 | Construct an input where Bubble Sort with early-exit is strictly worse than Insertion Sort. | Any "turtle" input like [2, 3, 4, 5, 1] — Bubble needs n-1 passes; Insertion Sort places elements in O(n²/4) total ops vs. Bubble's O(n²/2). |

---

## Coding Challenge (3 Languages)

### Challenge 1: Implement Bubble Sort with Statistics

> Write a Bubble Sort that returns (sorted_array, num_comparisons, num_swaps).
> Must implement early-exit optimization.
> Must handle empty array and single element.

#### Go

```go
package main

import "fmt"

type SortResult struct {
    Sorted      []int
    Comparisons int
    Swaps       int
}

func BubbleSortWithStats(arr []int) SortResult {
    result := SortResult{Sorted: append([]int(nil), arr...)}
    n := len(result.Sorted)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-1-i; j++ {
            result.Comparisons++
            if result.Sorted[j] > result.Sorted[j+1] {
                result.Sorted[j], result.Sorted[j+1] = result.Sorted[j+1], result.Sorted[j]
                result.Swaps++
                swapped = true
            }
        }
        if !swapped {
            break
        }
    }
    return result
}

func main() {
    res := BubbleSortWithStats([]int{5, 1, 4, 2, 8})
    fmt.Printf("%+v\n", res)
    // Expected: Sorted=[1 2 4 5 8], Comparisons=10, Swaps=4
}
```

#### Java

```java
import java.util.Arrays;

public class BubbleSortStats {
    public static class Result {
        public int[] sorted;
        public int comparisons;
        public int swaps;
        public String toString() {
            return String.format("sorted=%s, comparisons=%d, swaps=%d",
                                 Arrays.toString(sorted), comparisons, swaps);
        }
    }

    public static Result sort(int[] arr) {
        Result r = new Result();
        r.sorted = arr.clone();
        int n = r.sorted.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                r.comparisons++;
                if (r.sorted[j] > r.sorted[j + 1]) {
                    int t = r.sorted[j];
                    r.sorted[j] = r.sorted[j + 1];
                    r.sorted[j + 1] = t;
                    r.swaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println(sort(new int[]{5, 1, 4, 2, 8}));
    }
}
```

#### Python

```python
from dataclasses import dataclass, field
from typing import List

@dataclass
class SortResult:
    sorted: List[int] = field(default_factory=list)
    comparisons: int = 0
    swaps: int = 0

def bubble_sort_with_stats(arr):
    r = SortResult(sorted=list(arr))
    n = len(r.sorted)
    for i in range(n - 1):
        swapped = False
        for j in range(n - 1 - i):
            r.comparisons += 1
            if r.sorted[j] > r.sorted[j + 1]:
                r.sorted[j], r.sorted[j + 1] = r.sorted[j + 1], r.sorted[j]
                r.swaps += 1
                swapped = True
        if not swapped:
            break
    return r

if __name__ == "__main__":
    print(bubble_sort_with_stats([5, 1, 4, 2, 8]))
```

---

### Challenge 2: Count Inversions in O(n²)

> Given an array, return the number of inversions (pairs i < j with A[i] > A[j]).
> Use the Bubble Sort connection: number of swaps = number of inversions.

#### Go

```go
package main

import "fmt"

func CountInversions(arr []int) int {
    a := append([]int(nil), arr...)
    n := len(a)
    inv := 0
    for i := 0; i < n-1; i++ {
        for j := 0; j < n-1-i; j++ {
            if a[j] > a[j+1] {
                a[j], a[j+1] = a[j+1], a[j]
                inv++
            }
        }
    }
    return inv
}

func main() {
    fmt.Println(CountInversions([]int{5, 4, 3, 2, 1})) // 10
    fmt.Println(CountInversions([]int{1, 2, 3, 4, 5})) // 0
}
```

#### Java

```java
public class CountInversions {
    public static int count(int[] arr) {
        int[] a = arr.clone();
        int n = a.length;
        int inv = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int t = a[j]; a[j] = a[j + 1]; a[j + 1] = t;
                    inv++;
                }
            }
        }
        return inv;
    }

    public static void main(String[] args) {
        System.out.println(count(new int[]{5,4,3,2,1})); // 10
    }
}
```

#### Python

```python
def count_inversions(arr):
    a = list(arr)
    n = len(a)
    inv = 0
    for i in range(n - 1):
        for j in range(n - 1 - i):
            if a[j] > a[j + 1]:
                a[j], a[j + 1] = a[j + 1], a[j]
                inv += 1
    return inv

print(count_inversions([5, 4, 3, 2, 1]))  # 10
```

> **Follow-up:** Achieve O(n log n) using Merge Sort — see `02-merge-sort/`.

---

### Challenge 3: Cocktail Shaker Sort

> Implement bidirectional Bubble Sort (Cocktail Shaker).

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
    return arr

print(cocktail_sort([5, 1, 4, 2, 8]))  # [1, 2, 4, 5, 8]
```

(Go and Java implementations identical to `middle.md`.)

---

### Challenge 4 (Trick Question): "Sort Without Sorting"

> Given an array, determine if it is sorted in ascending order — without using any sort.

```python
def is_sorted(arr):
    return all(arr[i] <= arr[i + 1] for i in range(len(arr) - 1))
```

> **Why this matters in interviews:** Candidates who reach for Bubble Sort here lose points. The right answer is a single linear scan — *no* sort needed. This question tests whether you "default to a sort" or "default to the simplest answer."

---

## Common Interview Pitfalls

| Pitfall | What goes wrong | Fix |
|---------|----------------|-----|
| Off-by-one on inner loop | Inner runs to `n` not `n-1` → IndexError | Use `j < n - 1 - i` |
| Forgetting early-exit flag | Always O(n²) even on sorted input | Add `swapped` boolean |
| Reversing > / < | Sort descending instead of ascending | Verify with [5,1,4,2,8] → [1,2,4,5,8] |
| Saying "Bubble Sort is stable" without explaining why | Loses depth points | Strict `>`, equals never swap |
| Saying "Bubble Sort is bad" with no nuance | Misses partial credit on "when?" | Mention teaching, n ≤ 10, "is-sorted" check |
| Not handling empty/single | Code crashes on edge case | Test before submitting |
| Implementing Selection Sort by mistake | Whole answer is wrong | Selection finds-min-then-swaps; Bubble swaps adjacents |

---

## Behavioral / System Design

> "You're reviewing a junior engineer's PR. They've implemented Bubble Sort to sort customer orders by date. Production currently has ~1k orders per day. What feedback do you give?"

**Strong answer focuses on:**
1. **Now:** at 1k items, O(n²) = 10⁶ ops ≈ 10 ms. Tolerable, but borderline.
2. **Next year:** at 10k items, O(n²) = 10⁸ ops ≈ 1 s. Customer-visible latency.
3. **The fix:** replace with `sort.Slice` / `Collections.sort` / `sorted` — same code complexity, O(n log n).
4. **Beyond:** add a perf regression test that fails if sort takes >100 ms on n=10k. Set up an SLO.
5. **Mentorship angle:** Don't just say "use builtin"; explain *why*, link to docs, and pair on the fix.

> "Why does Python's `sorted` use TimSort instead of Bubble Sort?"

**Strong answer:**
- TimSort is O(n log n) worst case (vs. Bubble's O(n²)).
- TimSort is adaptive — exploits "runs" of already-sorted data, achieving O(n) on partially-sorted inputs (real-world data is rarely random).
- TimSort is stable — important for Python's dict-based sorting by key.
- TimSort is well-tested on real workloads (originally written for Python's list.sort by Tim Peters in 2002).

---

## One-Liner Summary

> **Bubble Sort:** Walk the array, swap adjacent pairs out of order, repeat. O(n²) average. Stable, in-place, adaptive (with early exit). Teaching-only — production uses O(n log n) sorts (TimSort, Quicksort, Pdqsort).
