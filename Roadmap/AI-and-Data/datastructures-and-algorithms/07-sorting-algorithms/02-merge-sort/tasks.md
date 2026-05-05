# Merge Sort — Practice Tasks

> All tasks must be solved in **Go**, **Java**, and **Python**.

---

## Beginner Tasks

### Task 1: Implement Top-Down Merge Sort

> Write Merge Sort that returns a new sorted array. Don't mutate the input.

#### Go

```go
func mergeSort(arr []int) []int {
    // TODO
    return arr
}
```

#### Java

```java
public static int[] mergeSort(int[] arr) {
    // TODO
    return arr;
}
```

#### Python

```python
def merge_sort(arr):
    # TODO
    return arr
```

- **Test:** `[5, 2, 8, 1, 9, 3, 7, 4]` → `[1, 2, 3, 4, 5, 7, 8, 9]`.
- **Edge cases:** empty array, single element, two elements, all equal.

---

### Task 2: Implement the `merge` Helper

> Write `merge(left, right)` that takes two sorted arrays and returns a single sorted array. Must be stable (use `<=`).

```python
def merge(left, right):
    # TODO
    return []
```

- **Test:** `merge([1, 3, 5], [2, 4, 6])` → `[1, 2, 3, 4, 5, 6]`.
- **Stability test:** `merge([(1, 'a')], [(1, 'b')])` → `[(1, 'a'), (1, 'b')]` (original order preserved).

---

### Task 3: In-Place Merge Sort with Shared Buffer

> Write Merge Sort that mutates the input and uses ONE auxiliary buffer (allocated in entry function, reused across recursive calls).

```python
def merge_sort_in_place(arr):
    aux = [0] * len(arr)
    # TODO
```

- **Memory check:** Function should never allocate within recursive calls.

---

### Task 4: Bottom-Up (Iterative) Merge Sort

> Implement Merge Sort without recursion. Merge size-1 pairs, then size-2, then size-4, etc.

```python
def merge_sort_bottom_up(arr):
    aux = [0] * len(arr)
    size = 1
    while size < len(arr):
        # TODO: merge pairs of subarrays of length `size`
        size *= 2
```

- **Why useful:** No stack overflow risk for huge arrays.

---

### Task 5: Sort with Stability Test

> Sort an array of `(score, name)` tuples by score. Verify that equal scores preserve the original name order.

```python
def merge_sort_stable(items, key=lambda x: x[0]):
    # TODO
    pass

data = [(90, 'A'), (80, 'B'), (90, 'C'), (85, 'D')]
result = merge_sort_stable(data)
# Expected: [(80, 'B'), (85, 'D'), (90, 'A'), (90, 'C')]
# Note: A before C because original order is preserved (stability).
```

---

## Intermediate Tasks

### Task 6: Count Inversions in O(n log n)

> Modify Merge Sort to count inversions during the merge step.

```python
def count_inversions(arr):
    # TODO
    return 0

assert count_inversions([2, 4, 1, 3, 5]) == 3
assert count_inversions([5, 4, 3, 2, 1]) == 10
assert count_inversions([1, 2, 3, 4, 5]) == 0
```

- **Hint:** When taking from `right`, all remaining `left` elements are inversions with the taken right element.

---

### Task 7: k-Way Merge with Heap

> Merge k sorted lists into one sorted list using a min-heap.

#### Python

```python
import heapq

def k_way_merge(sorted_lists):
    """Merge k sorted iterables. O(n log k)."""
    # TODO
    yield from []

result = list(k_way_merge([[1, 4, 7], [2, 5, 8], [3, 6, 9]]))
assert result == [1, 2, 3, 4, 5, 6, 7, 8, 9]
```

---

### Task 8: Sort Linked List

> Sort a singly linked list using Merge Sort. Use slow/fast pointer to find midpoint.

```python
class Node:
    def __init__(self, val, nxt=None):
        self.val = val
        self.next = nxt

def sort_linked_list(head):
    # TODO
    return head
```

- **Constraint:** O(log n) extra space (recursion stack only); no array conversion.

---

### Task 9: Merge Sort with Insertion Cutoff

> Use Insertion Sort for subarrays of size ≤ 16. Measure speedup.

```python
CUTOFF = 16

def merge_sort_hybrid(arr):
    aux = [0] * len(arr)
    _sort(arr, aux, 0, len(arr) - 1)

def _sort(arr, aux, lo, hi):
    if hi - lo < CUTOFF:
        # TODO: insertion sort arr[lo..hi]
        return
    mid = lo + (hi - lo) // 2
    _sort(arr, aux, lo, mid)
    _sort(arr, aux, mid + 1, hi)
    if arr[mid] <= arr[mid + 1]:
        return  # already sorted
    # TODO: merge arr[lo..mid] and arr[mid+1..hi] using aux
```

- **Benchmark:** Compare with and without cutoff on n=10000 random ints. Expect 1.5-2× speedup.

---

### Task 10: Stable Sort Using Merge Sort + Custom Key

> Generic stable sort with a key function (mirrors Python's `sorted(arr, key=...)`).

#### Go

```go
func MergeSortBy[T any, K constraints.Ordered](arr []T, key func(T) K) []T {
    // TODO
    return arr
}
```

#### Python

```python
def merge_sort_by(arr, key=lambda x: x, reverse=False):
    # TODO
    return arr
```

- **Test:** Sort strings by length: `["bb", "aaa", "c"]` → `["c", "bb", "aaa"]`.

---

## Advanced Tasks

### Task 11: External Merge Sort

> Implement external sort: input is a file with one number per line; sort using only 100 KB of RAM at a time.

```python
import os, tempfile, heapq

CHUNK_SIZE = 25_000  # ~100 KB for 4-byte ints

def external_merge_sort(input_path, output_path):
    # Phase 1: chunk and sort
    runs = []
    chunk = []
    with open(input_path) as f:
        for line in f:
            chunk.append(int(line))
            if len(chunk) >= CHUNK_SIZE:
                # TODO: sort chunk and write to a temp file; append path to runs
                chunk = []
        # TODO: handle leftover chunk
    
    # Phase 2: k-way merge
    iterators = [(_iter(p) for p in runs)]
    with open(output_path, 'w') as out:
        for val in heapq.merge(*iterators):
            out.write(f"{val}\n")
    
    # Cleanup
    for p in runs:
        os.unlink(p)

def _iter(path):
    with open(path) as f:
        for line in f:
            yield int(line)
```

- **Test:** Generate 1M random integers, external-sort using chunks of 25k. Verify output is sorted.

---

### Task 12: Parallel Merge Sort

> Implement Merge Sort that sorts the two halves on separate threads/goroutines for n > 10000.

#### Go

```go
package main

import "sync"

const PAR_THRESHOLD = 10000

func ParallelMergeSort(arr []int) []int {
    if len(arr) < PAR_THRESHOLD {
        return SequentialMergeSort(arr)
    }
    mid := len(arr) / 2
    var left, right []int
    var wg sync.WaitGroup
    wg.Add(2)
    // TODO: spawn two goroutines
    wg.Wait()
    return merge(left, right)
}
```

- **Benchmark:** Compare with sequential on n=1M. Expect 3-5× speedup on 8 cores.

---

### Task 13: Detect Sorted Suffix and Skip Merge

> If `arr[mid] <= arr[mid+1]`, the two halves are already in order — skip the merge.

```python
def merge_sort_smart(arr):
    aux = [0] * len(arr)
    _sort(arr, aux, 0, len(arr) - 1)

def _sort(arr, aux, lo, hi):
    if lo >= hi: return
    mid = (lo + hi) // 2
    _sort(arr, aux, lo, mid)
    _sort(arr, aux, mid + 1, hi)
    # TODO: skip merge if arr[mid] <= arr[mid + 1]
    _merge(arr, aux, lo, mid, hi)
```

- **Benchmark on already-sorted input n=10000:** Without skip → O(n log n). With skip → O(n).

---

### Task 14: Top-K Using Merge Sort with Early Termination

> Find top-k elements without fully sorting. Stop merging once you have the smallest k.

```python
def top_k_merge_sort(arr, k):
    # TODO: merge sort but only produce the smallest k elements
    return []
```

- **Comparison:** vs. heap-based `heapq.nsmallest(k, arr)` — should be similar speed for small k, slower for large k.

---

### Task 15: Sort by Multiple Keys (Composite Key Sort)

> Sort records by (date, score, name) — by date first, then by score within same date, then by name within same date+score.

```python
def merge_sort_composite(records):
    # records: list of dicts with 'date', 'score', 'name'
    # TODO: sort by multiple keys, using stability
    return records

data = [
    {'date': '2024-01-01', 'score': 90, 'name': 'B'},
    {'date': '2024-01-02', 'score': 80, 'name': 'A'},
    {'date': '2024-01-01', 'score': 95, 'name': 'A'},
]
# Expected output sorted by date, then score, then name
```

- **Hint:** Sort by least significant key first, then by next, etc. — relies on stability.

---

## Benchmark Task

> Compare Merge Sort variants and built-in sorts on the same inputs.

#### Python

```python
import random
import timeit

def merge_sort(arr):
    if len(arr) <= 1: return list(arr)
    mid = len(arr) // 2
    return _merge(merge_sort(arr[:mid]), merge_sort(arr[mid:]))

def _merge(l, r):
    out, i, j = [], 0, 0
    while i < len(l) and j < len(r):
        if l[i] <= r[j]: out.append(l[i]); i += 1
        else:            out.append(r[j]); j += 1
    out += l[i:]; out += r[j:]
    return out

if __name__ == "__main__":
    for n in (1_000, 10_000, 100_000):
        random.seed(42)
        data = [random.randint(0, n) for _ in range(n)]
        t1 = timeit.timeit(lambda: merge_sort(list(data)), number=1)
        t2 = timeit.timeit(lambda: sorted(data), number=1)
        print(f"n={n:>7}: merge_sort={t1*1000:.1f}ms, sorted (TimSort)={t2*1000:.1f}ms")
```

### Expected Results

| n | Merge Sort (Python) | sorted (TimSort) |
|---|--------------------|--------------------|
| 1,000 | 5 ms | 0.1 ms |
| 10,000 | 70 ms | 1.3 ms |
| 100,000 | 900 ms | 18 ms |

TimSort is ~50× faster than naive Python merge sort because it's implemented in C and is adaptive.

#### Go

```go
package main

import (
    "fmt"
    "math/rand"
    "sort"
    "time"
)

func main() {
    for _, n := range []int{1000, 10000, 100000, 1000000} {
        data := make([]int, n)
        for i := range data { data[i] = rand.Intn(n) }

        cp := append([]int{}, data...)
        start := time.Now()
        MergeSort(cp)
        fmt.Printf("n=%7d: MergeSort=%v, ", n, time.Since(start))

        cp = append([]int{}, data...)
        start = time.Now()
        sort.Ints(cp)
        fmt.Printf("sort.Ints (Pdqsort)=%v\n", time.Since(start))
    }
}
```

---

## Self-Assessment Rubric

| Skill | Beginner | Intermediate | Advanced |
|-------|---------|-------------|----------|
| Top-down recursive Merge Sort | Required | — | — |
| Bottom-up iterative Merge Sort | Required | — | — |
| Inversion count via Merge Sort | — | Required | — |
| k-way merge with heap | — | Required | — |
| Linked list Merge Sort | — | Required | — |
| Hybrid (Merge + Insertion) | — | Required | — |
| External merge sort | — | — | Required |
| Parallel Merge Sort | — | — | Required |
| Composite key sort | — | — | Required |
| Explain TimSort vs Merge Sort | Required | Required | Required |

---

## Stretch Challenges

1. **TimSort skeleton:** Implement run detection and merge-stack-based scheduling (real TimSort requires invariants A > B + C and B > C).
2. **Lazy merge sort:** Don't merge until you need an element — useful for streaming top-k.
3. **Cache-aware merge sort:** Tune chunk size to L1/L2 cache size, measure cache misses.
4. **Distributed sort:** Implement a simple range-partition + per-partition sort using multiprocessing.
5. **Inversion distance:** Find the minimum number of adjacent swaps to sort — equals inversion count from merge sort.
