# Heap Sort — Practice Tasks

> All in **Go**, **Java**, **Python**.

## Beginner

### Task 1: Implement Min-Heap from Scratch
```python
class MinHeap:
    def __init__(self): self._h = []
    def push(self, x): pass  # TODO: append + sift-up
    def pop(self): pass      # TODO: swap root with last, pop, sift-down
    def peek(self): return self._h[0]
```

### Task 2: Implement Max-Heap
Same as Min-Heap with reversed comparison.

### Task 3: Heap Sort (Ascending) using Max-Heap
```python
def heap_sort(arr):
    # TODO: build max-heap, repeatedly swap root to end and sift-down
    pass
```
- Test: `[5, 2, 8, 1, 9, 3]` → `[1, 2, 3, 5, 8, 9]`

### Task 4: Heap Sort using Built-in heapq
```python
import heapq
def heap_sort_builtin(arr):
    h = list(arr)
    heapq.heapify(h)  # O(n)
    return [heapq.heappop(h) for _ in range(len(h))]
```

### Task 5: Iterative siftDown
```python
def sift_down(heap, i, n):
    # TODO: iterative version (no recursion)
    pass
```

## Intermediate

### Task 6: K-th Smallest Element
```python
def kth_smallest(arr, k):
    # TODO: build min-heap, pop k-1 times, return next
    pass
```
**Time:** O(n + k log n).

### Task 7: K-th Largest Element (Heap of Size K)
```python
def kth_largest(arr, k):
    # TODO: maintain min-heap of size k; smallest of top-k at root
    pass
```
**Time:** O(n log k). Better than full sort for k << n.

### Task 8: Top-K Frequent Elements (LeetCode 347)
```python
def top_k_frequent(nums, k):
    # TODO: count frequencies, then heap of (-freq, num) → top k
    pass
```

### Task 9: Merge K Sorted Lists (LeetCode 23)
```python
import heapq
def merge_k_lists(lists):
    # TODO: heap with (head_val, list_idx, node)
    pass
```
**Time:** O(N log k).

### Task 10: Priority Queue with Custom Comparator
```python
import heapq
def priority_queue_demo():
    # TODO: store tuples (priority, item); push/pop in priority order
    pass
```

## Advanced

### Task 11: Dijkstra's Shortest Path
```python
import heapq
def dijkstra(graph, start):
    # graph: {u: [(v, weight), ...]}
    dist = {start: 0}
    pq = [(0, start)]
    # TODO: pop min-distance node, relax edges, push updated distances
    return dist
```

### Task 12: Huffman Coding
```python
import heapq
def huffman_codes(freqs):
    # freqs: {char: count}
    # TODO: build huffman tree using min-heap
    return {}
```

### Task 13: Sliding Window Maximum (LeetCode 239)
Heap-based version (deque is faster but heap is the simpler conceptual fit):
```python
import heapq
def max_sliding_window_heap(nums, k):
    # TODO: max-heap (negate values); lazy deletion of out-of-window items
    pass
```

### Task 14: Median Maintenance (Two Heaps)
```python
import heapq
class MedianFinder:
    def __init__(self):
        self.lo = []  # max-heap (negate)
        self.hi = []  # min-heap
    def add(self, num): pass    # TODO
    def median(self): pass       # O(1)
```

### Task 15: Indexed Min-Heap (Decrease-Key Support)
```python
class IndexedMinHeap:
    """Supports decrease_key(item, new_priority) in O(log n)."""
    def __init__(self):
        self._h = []
        self._idx = {}  # item → position in _h
    def push(self, item, priority): pass  # TODO
    def pop(self): pass
    def decrease_key(self, item, new_priority): pass
```
**Use:** Dijkstra with O((V + E) log V) instead of O((V + E) log V) with re-pushing.

## Benchmark

| n | Heap Sort | Quick Sort (Pdqsort) | Merge Sort | TimSort |
|---|-----------|----------------------|------------|---------|
| 1,000 | 0.3 ms | 0.05 ms | 0.1 ms | 0.05 ms |
| 10,000 | 4 ms | 0.5 ms | 1.2 ms | 0.8 ms |
| 100,000 | 50 ms | 5 ms | 12 ms | 8 ms |

**Heap Sort is 10× slower than Pdqsort** because:
- Heap traversal is cache-unfriendly (parent/child indices jump in memory).
- More comparisons per element (~2 log n vs ~1.05 log n for Pdqsort).

**Heap Sort wins when:** O(1) extra space + O(n log n) worst case both required (e.g., Introsort fallback).

---

## Self-Assessment

| Skill | Beginner | Intermediate | Advanced |
|-------|----------|-------------|----------|
| Min-heap & max-heap | Required | — | — |
| Heap sort | Required | — | — |
| Top-K with heap | — | Required | — |
| Merge K sorted lists | — | Required | — |
| Dijkstra | — | — | Required |
| Indexed heap (decrease-key) | — | — | Required |
| Median maintenance | — | — | Required |
