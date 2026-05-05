# Selection Sort — Practice Tasks

> All in **Go**, **Java**, **Python**.

## Beginner

### Task 1: Standard Selection Sort
```python
def selection_sort(arr):
    # TODO
    pass
```

### Task 2: Sort Descending
```python
def selection_sort_desc(arr):
    # TODO: find max instead of min
    pass
```

### Task 3: Track Number of Swaps
```python
def selection_sort_count(arr):
    swaps = 0
    # TODO
    return swaps
```

### Task 4: Generic with Comparator
```python
def selection_sort_by(arr, key=lambda x: x):
    # TODO
    pass
```

### Task 5: Skip Self-Swap
Verify your impl doesn't perform `arr[i], arr[i] = arr[i], arr[i]` no-op when min is already at i.

## Intermediate

### Task 6: Bidirectional Selection Sort
```python
def bidirectional_selection_sort(arr):
    lo, hi = 0, len(arr) - 1
    while lo < hi:
        # TODO: find min AND max in one pass; place at lo and hi
        lo += 1; hi -= 1
```

### Task 7: Stable Selection Sort
```python
def stable_selection_sort(arr):
    # TODO: use shifts instead of swap to preserve order
    pass
```

### Task 8: Recursive Selection Sort
```python
def selection_sort_recursive(arr, start=0):
    if start >= len(arr) - 1: return
    # TODO
```

### Task 9: Selection Sort on Linked List
```python
class Node:
    def __init__(self, val, nxt=None):
        self.val = val; self.next = nxt

def selection_sort_linked(head):
    # TODO: find min in remaining list, swap values
    return head
```

### Task 10: Iterative Selection of Top-K
```python
def top_k_selection(arr, k):
    # TODO: do k passes of selection sort, return first k
    return arr[:k]
```

## Advanced

### Task 11: Cycle Sort (True Minimum Writes)
```python
def cycle_sort(arr):
    # TODO: implement cycle sort - achieves min possible writes
    pass
```

### Task 12: Write-Counting Wrapper
```python
class WriteCountingArray:
    def __init__(self, data):
        self._data = list(data); self.writes = 0
    def __getitem__(self, i): return self._data[i]
    def __setitem__(self, i, v):
        self._data[i] = v; self.writes += 1
    def __len__(self): return len(self._data)

# Compare write counts: Selection vs Insertion vs Bubble
```

### Task 13: Selection Sort for EEPROM-backed Storage
Implement a simulator with realistic write delays; compare Selection vs Insertion.

### Task 14: Parallel Min-Find
Parallelize just the min-find within a pass. Speedup vs sequential?

### Task 15: Heap Sort (Generalize Selection)
```python
import heapq
def heap_sort(arr):
    # TODO: build min-heap, repeatedly extract min into output
    pass
```

## Benchmark

| n | Selection | Bubble | Insertion | Built-in |
|---|-----------|--------|-----------|----------|
| 100 | 30 µs | 50 µs | 25 µs | 8 µs |
| 1,000 | 600 µs | 1.5 ms | 600 µs | 100 µs |
| 10,000 | 80 ms | 145 ms | 55 ms | 1.3 ms |

**Selection writes:** ~n. **Bubble/Insertion writes:** ~n²/4.
