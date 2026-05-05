# Quick Sort — Interview Preparation

## Junior Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | What is Quick Sort? | Pick pivot, partition into ≤/≥ halves, recurse. No merge step. |
| 2 | Time complexity? | Best/Avg O(n log n), Worst O(n²). |
| 3 | Space? | O(log n) avg, O(n) worst (recursion stack). |
| 4 | Stable? | No (typical implementations). |
| 5 | In-place? | Yes (just stack overhead). |
| 6 | What's a pivot? | Element used to split the array; ends up at its final sorted position. |
| 7 | Difference from Merge Sort? | No merge step; partition does the sorting work. In-place vs O(n) extra space. |
| 8 | Worst case input? | Already sorted with first/last as pivot — O(n²). |
| 9 | How to avoid worst case? | Random or median-of-three pivot. |
| 10 | Implement Lomuto partition. | See `junior.md`. |

## Middle Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | Lomuto vs Hoare partition. | Lomuto: simple, more swaps. Hoare: trickier, ~3× fewer swaps. |
| 2 | What is 3-way partition? | Split into <, ==, > pivot. Handles duplicates in O(n). |
| 3 | Why median-of-three? | Avoids worst case on sorted/reverse inputs. |
| 4 | What is Introsort? | Quick Sort with Heap Sort fallback when depth > 2 log n. C++ STL uses it. |
| 5 | What is Pdqsort? | Pattern-Defeating Quick Sort. Median-of-three + pattern detection + introsort fallback. |
| 6 | What is Dual-Pivot Quicksort? | Two pivots → 3 partitions. Java uses it for primitives. ~5% faster. |
| 7 | Average comparisons? | ~1.39 n log n — about 39% above information-theoretic minimum. |
| 8 | How does tail-recursion optimization help? | Recurse on smaller, iterate on larger → stack bounded to O(log n). |
| 9 | Why doesn't Quick Sort work well on linked lists? | Needs random access for partition; linked lists are sequential. Use Merge Sort. |
| 10 | When to choose Quick Sort over Merge Sort? | In-memory random data, no stability needed, memory-constrained. |

## Senior Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | Algorithmic complexity attack on Quick Sort? | Adversarial input causes O(n²); defend with random/Pdqsort/input limits. |
| 2 | Implement Quickselect for k-th smallest. | Partition; if pivot index == k, done; else recurse on appropriate side. O(n) avg. |
| 3 | Why does Java use Quick for primitives but Merge for objects? | Primitives don't need stability + cache locality wins; objects often need stability. |
| 4 | When would parallel Quick Sort beat parallel Merge Sort? | Rarely — Merge is easier to parallelize. Quick wins on shared memory. |
| 5 | How to make Quick Sort stable? | Auxiliary index array, sort by (value, index). Defeats in-place benefit. |
| 6 | Compare Pdqsort vs Dual-Pivot vs Introsort. | All Quick Sort variants with different fallback strategies. Pdqsort wins on random; Dual-Pivot ~5% faster on dense ints; Introsort is the C++ choice. |
| 7 | What's the cache behavior of Quick Sort? | O((n/B) log(n/M)) — optimal; partition data stays hot in cache. |
| 8 | When is Quick Sort the wrong choice? | External sort, linked list, real-time SLA, need stability. |

## Professional Questions

| # | Question | Answer Focus |
|---|----------|--------------|
| 1 | Prove Quick Sort correctness via partition invariants. | At each step: A[p..i] ≤ pivot, A[i+1..j-1] > pivot. Induction. |
| 2 | Derive average comparison count. | E[X_{ij}] = 2/(j-i+1); sum gives 1.39 n log n. |
| 3 | Why does randomization give expected O(n log n)? | Each split balanced w.p. ≥ 1/2; depth = O(log n) w.h.p. |
| 4 | Prove Quickselect is O(n) expected. | T(n) = T(3n/4) + O(n) → O(n) by Master Theorem. |
| 5 | What is the Median-of-Medians algorithm? | Deterministic O(n) selection. Recurrence T(n) = T(n/5) + T(7n/10) + O(n). |
| 6 | Cache complexity of Quick Sort? | O((n/B) log(n/M)) — matches lower bound. |
| 7 | Parallel span complexity? | O(log² n) with parallel partition; O(n) sequential partition. |

---

## Coding Challenge

### Challenge 1: Implement Quick Sort with Random Pivot

```python
import random

def quick_sort(arr):
    _sort(arr, 0, len(arr) - 1)

def _sort(arr, lo, hi):
    if lo < hi:
        # random pivot
        rnd = random.randint(lo, hi)
        arr[rnd], arr[hi] = arr[hi], arr[rnd]
        p = _partition(arr, lo, hi)
        _sort(arr, lo, p - 1)
        _sort(arr, p + 1, hi)

def _partition(arr, lo, hi):
    pivot = arr[hi]
    i = lo - 1
    for j in range(lo, hi):
        if arr[j] <= pivot:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i+1], arr[hi] = arr[hi], arr[i+1]
    return i + 1
```

### Challenge 2: Implement 3-Way Partition

```python
def quick_sort_3way(arr, lo=0, hi=None):
    if hi is None: hi = len(arr) - 1
    if lo >= hi: return
    pivot = arr[lo]; lt, gt, i = lo, hi, lo + 1
    while i <= gt:
        if arr[i] < pivot:
            arr[lt], arr[i] = arr[i], arr[lt]; lt += 1; i += 1
        elif arr[i] > pivot:
            arr[i], arr[gt] = arr[gt], arr[i]; gt -= 1
        else:
            i += 1
    quick_sort_3way(arr, lo, lt - 1)
    quick_sort_3way(arr, gt + 1, hi)
```

### Challenge 3: Quickselect

```python
def quickselect(arr, k):
    """k-th smallest, 0-indexed. O(n) avg."""
    lo, hi = 0, len(arr) - 1
    while lo < hi:
        rnd = random.randint(lo, hi)
        arr[rnd], arr[hi] = arr[hi], arr[rnd]
        p = _partition(arr, lo, hi)
        if p == k: return arr[p]
        if p < k: lo = p + 1
        else: hi = p - 1
    return arr[lo]
```

### Challenge 4: Top-K via Quickselect

```python
def top_k(arr, k):
    if k >= len(arr): return list(arr)
    a = list(arr)
    quickselect(a, k - 1)
    return a[:k]
```

### Challenge 5: Iterative Quick Sort (Stack-Based)

```python
def quick_sort_iter(arr):
    stack = [(0, len(arr) - 1)]
    while stack:
        lo, hi = stack.pop()
        if lo < hi:
            p = _partition(arr, lo, hi)
            stack.append((lo, p - 1))
            stack.append((p + 1, hi))
```

---

## Pitfalls

| Pitfall | Fix |
|---------|-----|
| First/last as pivot on sorted | Use random or median-of-three |
| Not handling duplicates | Use 3-way partition |
| Stack overflow from bad pivots | Tail-recursion optimization |
| Saying "Quick Sort is stable" | False — typical impl is unstable |
| Off-by-one in Hoare partition | Use do-while pattern |

## One-Liner

> **Quick Sort:** Pick pivot, partition into ≤/≥ halves, recurse. **O(n log n) avg, O(n²) worst**, **in-place**, **NOT stable**, **cache-friendly**. Modern variants (Pdqsort, Dual-Pivot, Introsort) eliminate worst case. The fastest in-practice in-memory sort.
