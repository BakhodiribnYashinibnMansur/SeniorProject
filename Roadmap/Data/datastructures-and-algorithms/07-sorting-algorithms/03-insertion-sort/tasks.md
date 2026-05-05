# Insertion Sort — Practice Tasks

> All tasks in **Go**, **Java**, **Python**.

---

## Beginner

### Task 1: Implement Insertion Sort
```python
def insertion_sort(arr):
    # TODO
    pass
```
- Test: `[5, 2, 4, 6, 1, 3]` → `[1, 2, 3, 4, 5, 6]`.

### Task 2: Sort Descending
```python
def insertion_sort_desc(arr):
    # TODO: change > to <
    pass
```

### Task 3: Sort Strings Lexicographically
```python
def insertion_sort_strings(arr):
    # TODO
    pass
```

### Task 4: Stable Sort by Key
```python
def insertion_sort_by(arr, key=lambda x: x):
    # TODO
    pass
```
- Test stability: `[(1,'a'),(1,'b')]` stays `[(1,'a'),(1,'b')]`.

### Task 5: Online Insert
```python
def online_insert(sorted_arr, x):
    # TODO
    pass
```

---

## Intermediate

### Task 6: Binary Insertion Sort
```python
import bisect
def binary_insertion_sort(arr):
    for i in range(1, len(arr)):
        # TODO: find position via bisect_left, then shift+insert
        pass
```

### Task 7: Shell Sort
```python
def shell_sort(arr):
    n = len(arr); gap = n // 2
    while gap > 0:
        # TODO: gapped insertion
        gap //= 2
```

### Task 8: Hybrid Merge + Insertion Sort
```python
CUTOFF = 16
def hybrid_sort(arr, lo=0, hi=None):
    if hi is None: hi = len(arr) - 1
    if hi - lo <= CUTOFF:
        # TODO: insertion sort range
        return
    # TODO: split, recurse, merge
```

### Task 9: Count Inversions via Insertion Sort
```python
def count_inversions(arr):
    # TODO: use insertion-shift count
    return 0
```

### Task 10: Insertion Sort Linked List
```python
class Node:
    def __init__(self, val, nxt=None):
        self.val = val; self.next = nxt

def insertion_sort_linked(head):
    # TODO: build sorted list by inserting each node
    return head
```

---

## Advanced

### Task 11: Maintain Top-K Stream via Insertion
```python
def top_k_stream(stream, k):
    sorted_top = []
    for x in stream:
        # TODO: insert if smaller than max; keep size ≤ k
        yield list(sorted_top)
```

### Task 12: Sentinel Insertion Sort
```python
def insertion_sort_sentinel(arr):
    # Place arr[0] = -infinity to skip bound check
    arr.insert(0, float('-inf'))
    # TODO
    arr.pop(0)
```

### Task 13: Adaptive Quadratic Detector
Test that Insertion Sort is faster on nearly-sorted vs random:
```python
import time, random
def benchmark():
    n = 10000
    sorted_data = list(range(n))
    random_data = random.sample(range(n), n)
    nearly = list(range(n))
    for _ in range(50): # 0.5% perturbation
        i, j = random.randint(0,n-1), random.randint(0,n-1)
        nearly[i], nearly[j] = nearly[j], nearly[i]
    for label, data in [("sorted",sorted_data),("nearly",nearly),("random",random_data)]:
        t = time.perf_counter()
        insertion_sort(list(data))
        print(f"{label}: {time.perf_counter()-t:.3f}s")
```

### Task 14: Compare Cutoff Values for Hybrid Sort
Test cutoffs of 8, 16, 32, 64, 128 in hybrid Merge+Insertion. Find the optimum.

### Task 15: Implement TimSort's Run Detection + Insertion Extension
```python
def find_run(arr, lo):
    # TODO: find max j such that arr[lo..j] is monotonic
    return lo
def extend_run(arr, lo, hi, target_len):
    # TODO: extend run to target_len using insertion sort
    pass
```

---

## Benchmark
Compare on n = 100, 1000, 10000:
- Insertion Sort
- Bubble Sort
- Selection Sort
- Built-in (`sorted` / `Arrays.sort` / `sort.Ints`)
- Hybrid Merge + Insertion (cutoff=16)

Expected: hybrid is within 1.5× of built-in; pure Insertion ~50× slower for n=10k random.
