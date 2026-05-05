# Quick Sort — Practice Tasks

> All in **Go**, **Java**, **Python**.

## Beginner

### Task 1: Lomuto Partition Quick Sort
```python
def quick_sort_lomuto(arr):
    # TODO
    pass
```

### Task 2: Hoare Partition Quick Sort
```python
def quick_sort_hoare(arr):
    # TODO
    pass
```

### Task 3: Random Pivot
```python
import random
def quick_sort_random(arr):
    # TODO: pick random pivot per partition
    pass
```

### Task 4: Median-of-Three
```python
def median_of_three(arr, lo, hi):
    # TODO: return index of median of arr[lo], arr[mid], arr[hi]
    return 0
```

### Task 5: Iterative (Stack-Based) Quick Sort
```python
def quick_sort_iter(arr):
    stack = [(0, len(arr)-1)]
    # TODO
```

## Intermediate

### Task 6: 3-Way Partition (Dutch Flag)
```python
def quick_sort_3way(arr, lo=0, hi=None):
    # TODO: handle duplicates in O(n) on all-equal input
    pass
```

### Task 7: Hybrid with Insertion Cutoff
```python
CUTOFF = 16
def hybrid_quick_sort(arr, lo=0, hi=None):
    if hi is None: hi = len(arr) - 1
    if hi - lo < CUTOFF:
        # insertion sort range
        return
    # quick sort
```

### Task 8: Quickselect
```python
def quickselect(arr, k):
    # TODO: O(n) average
    pass
```

### Task 9: Top-K via Quickselect
```python
def top_k(arr, k):
    # TODO
    pass
```

### Task 10: Tail-Recursion Optimization
```python
def quick_sort_tco(arr, lo=0, hi=None):
    # TODO: recurse on smaller, iterate on larger
    pass
```

## Advanced

### Task 11: Introsort (Quick + Heap Fallback)
```python
import math, heapq
def introsort(arr):
    max_depth = 2 * int(math.log2(max(len(arr), 1)))
    # TODO: track depth; switch to heap_sort when depth=0
```

### Task 12: Dual-Pivot Quicksort
```python
def dual_pivot_quicksort(arr, lo=0, hi=None):
    # TODO: pick two pivots p1 ≤ p2; partition into 3 regions
    pass
```

### Task 13: Parallel Quick Sort

#### Go
```go
import "sync"
func ParallelQuickSort(arr []int) {
    if len(arr) < 10000 { QuickSort(arr); return }
    p := partition(arr, 0, len(arr)-1)
    var wg sync.WaitGroup
    wg.Add(2)
    // TODO: sort halves in goroutines
    wg.Wait()
}
```

### Task 14: Median-of-Medians (Deterministic Linear Selection)
```python
def select_mom(arr, k):
    # TODO: deterministic O(n)
    pass
```

### Task 15: Multi-Key Quicksort for Strings
```python
def multi_key_quicksort(strings, d=0):
    # TODO: partition by character at position d
    pass
```

## Benchmark
Compare on n=10⁶ random ints:
- Vanilla Quick Sort (first as pivot)
- Random pivot
- Median-of-three
- 3-way partition
- Hybrid (cutoff=16)
- Pdqsort (built-in)

Expected: Pdqsort 4× faster than vanilla random pivot. First-as-pivot may DNF on sorted input.
