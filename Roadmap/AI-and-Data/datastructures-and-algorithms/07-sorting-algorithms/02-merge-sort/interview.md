# Merge Sort — Interview Preparation

## Junior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is Merge Sort? | Divide-and-conquer: split in half, recursively sort, merge. |
| 2 | What is the time complexity? | O(n log n) in **all cases** — best, average, worst. |
| 3 | What is the space complexity? | O(n) auxiliary for arrays; O(log n) for linked lists. |
| 4 | Is Merge Sort stable? | Yes — uses `<=` in merge to preserve order of equal elements. |
| 5 | Is Merge Sort in-place? | No (standard array). Yes for linked lists. |
| 6 | What is the merge step? | Two-pointer technique: walk two sorted arrays, pick smaller current element each step. |
| 7 | What's the recurrence relation? | T(n) = 2T(n/2) + O(n). |
| 8 | What's the base case in recursion? | `len(arr) <= 1` — single element or empty is already sorted. |
| 9 | When would you use Merge Sort over Bubble Sort? | Whenever n > 50; Merge Sort is O(n log n) vs Bubble Sort's O(n²). |
| 10 | Implement Merge Sort on a whiteboard. | See `junior.md` Example 1. ~20 lines. |

## Middle Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Prove Merge Sort is O(n log n) using the Master Theorem. | T(n) = 2T(n/2) + O(n); a=2, b=2, log_b(a)=1, f(n)=Θ(n¹·log⁰); Case 2 → Θ(n log n). |
| 2 | What's the difference between top-down and bottom-up Merge Sort? | Top-down is recursive (split first); bottom-up is iterative (merge size-1, then size-2, etc.). Same Big-O. |
| 3 | Why is Merge Sort O(n) auxiliary space? | Each merge needs a buffer to hold the merged output before copying back. |
| 4 | How do you make Merge Sort more memory-efficient? | Allocate ONE shared buffer in the entry point, reuse for all merges. Or use bottom-up. |
| 5 | When does Merge Sort beat Quick Sort? | When you need stability, worst-case guarantee, sorting linked lists, or external sort. |
| 6 | When does Quick Sort beat Merge Sort? | Dense numeric arrays in cache — Quick Sort's locality wins, despite same Big-O. |
| 7 | How does Merge Sort sort a linked list in O(1) extra space? | Use slow/fast pointer to find midpoint, recursively sort halves, merge by relinking nodes. |
| 8 | What is k-way merge and when do you use it? | Merge k sorted streams using a min-heap. O(n log k). Used in external sort, MapReduce shuffle. |
| 9 | Count inversions using Merge Sort — explain. | During merge, when you take from right, all remaining left elements form inversions. Add `mid - i + 1` to count. |
| 10 | What is TimSort and why is it better? | Hybrid of Merge + Insertion Sort. Detects natural runs, uses Insertion for small subarrays. O(n) on sorted, O(n log n) worst. |

## Senior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Design an external sort for a 1 TB file with 16 GB RAM. | Phase 1: read 16 GB chunks, sort in memory, write to disk (64 runs). Phase 2: 64-way merge with min-heap. Total I/O: ~4× input size. |
| 2 | How does Spark's `sortByKey` work? | Sample data for quantiles → range-partition → per-partition sort (in-memory or external) → concatenate. |
| 3 | Compare Merge Sort and TimSort in production. | Both stable, O(n log n) worst. TimSort is adaptive (O(n) on sorted), used in Python/Java/Android. Vanilla merge always Θ(n log n). |
| 4 | Why does Java use Merge Sort for objects but Quick Sort for primitives? | Objects need stability (often sorted by composite keys); primitives don't, and cache locality of QS wins. |
| 5 | Design a sort-merge join for two billion-row tables. | Sort each table by join key (external sort if needed); two-pointer scan of sorted outputs. O(N log N + M log M + N + M). |
| 6 | How does parallel merge sort scale? | Sort halves on separate threads; expect 3-5× speedup on 8 cores; bottleneck is final merge unless you parallelize merge too. |
| 7 | What is replacement selection in external sort? | Use min-heap of size M to generate runs of average length 2M (vs M for chunk-and-sort). Halves number of runs. |
| 8 | Why does LSM-tree compaction use merge sort? | Multiple sorted SSTables on disk → k-way merge produces one larger sorted file. Sequential I/O = fast. |
| 9 | What's the cache complexity of Merge Sort? | O((n/B) log_{M/B}(n/B)) — matches the cache-oblivious lower bound for comparison sorts. |
| 10 | When would you choose Merge Sort over a database's built-in `ORDER BY`? | When you can pre-sort and reuse (e.g., for merge join); or for specialized data formats (e.g., parquet with sort hints). |

## Professional Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Prove correctness with loop invariants. | Inner: at iteration k, A[p..k-1] holds smallest k-p elements of L∪R in order. Outer: induction on subproblem size. |
| 2 | Apply Master Theorem to T(n) = 2T(n/2) + Θ(n). | Case 2: f(n) = Θ(n^(log_b a) · log^0 n) → Θ(n log n). |
| 3 | What's the tight comparison count? | n⌈log₂ n⌉ - 2^⌈log₂ n⌉ + 1 worst case. About 1.05× the information-theoretic lower bound. |
| 4 | Apply Akra-Bazzi to unbalanced merge sort T(n) = T(n/3) + T(2n/3) + Θ(n). | Find p: (1/3)^p + (2/3)^p = 1 → p = 1. T(n) = Θ(n log n). |
| 5 | Prove the Ω(n log n) lower bound for comparison sorts. | Decision tree must distinguish n! permutations → depth ≥ log₂(n!) = Θ(n log n) by Stirling. |
| 6 | What is the I/O complexity of external merge sort? | O((N/B) · log_{M/B}(N/B)) — matches the Aggarwal-Vitter lower bound. |
| 7 | Explain Cole's parallel merge sort. | O(n log n) work, O(log n) span — pipelines merging across levels. Asymptotically optimal parallel sort. |
| 8 | What is the cache-oblivious analysis? | Merge sort achieves O((n/B) log_{M/B}(n/B)) cache misses without knowing M, B → cache-oblivious optimal. |
| 9 | Why is TimSort's worst case O(n log n) provable? | Merge stack invariants ensure no run is merged with a much smaller one → balanced merges → log n levels. |
| 10 | Compare Merge Sort to Funnel Sort. | Funnel Sort is also O((n/B) log_{M/B}(n/B)) but with smaller constants. Practical for very large n. |

---

## Coding Challenge (3 Languages)

### Challenge 1: Implement Merge Sort

> Write Merge Sort that returns a new sorted array (does not mutate input). Must handle empty array.

#### Go

```go
package main

import "fmt"

func MergeSort(arr []int) []int {
    if len(arr) <= 1 {
        return append([]int{}, arr...)
    }
    mid := len(arr) / 2
    left  := MergeSort(arr[:mid])
    right := MergeSort(arr[mid:])
    return merge(left, right)
}

func merge(l, r []int) []int {
    out := make([]int, 0, len(l)+len(r))
    i, j := 0, 0
    for i < len(l) && j < len(r) {
        if l[i] <= r[j] {
            out = append(out, l[i]); i++
        } else {
            out = append(out, r[j]); j++
        }
    }
    out = append(out, l[i:]...)
    out = append(out, r[j:]...)
    return out
}

func main() {
    fmt.Println(MergeSort([]int{5, 2, 8, 1, 9, 3, 7, 4})) // [1 2 3 4 5 7 8 9]
}
```

#### Java

```java
import java.util.Arrays;

public class MergeSort {
    public static int[] sort(int[] arr) {
        if (arr.length <= 1) return arr.clone();
        int mid = arr.length / 2;
        int[] left  = sort(Arrays.copyOfRange(arr, 0, mid));
        int[] right = sort(Arrays.copyOfRange(arr, mid, arr.length));
        return merge(left, right);
    }

    private static int[] merge(int[] l, int[] r) {
        int[] out = new int[l.length + r.length];
        int i = 0, j = 0, k = 0;
        while (i < l.length && j < r.length) {
            if (l[i] <= r[j]) out[k++] = l[i++];
            else              out[k++] = r[j++];
        }
        while (i < l.length) out[k++] = l[i++];
        while (j < r.length) out[k++] = r[j++];
        return out;
    }
}
```

#### Python

```python
def merge_sort(arr):
    if len(arr) <= 1:
        return list(arr)
    mid = len(arr) // 2
    return _merge(merge_sort(arr[:mid]), merge_sort(arr[mid:]))

def _merge(l, r):
    out, i, j = [], 0, 0
    while i < len(l) and j < len(r):
        if l[i] <= r[j]:
            out.append(l[i]); i += 1
        else:
            out.append(r[j]); j += 1
    out += l[i:]
    out += r[j:]
    return out

print(merge_sort([5, 2, 8, 1, 9, 3, 7, 4]))
```

---

### Challenge 2: Count Inversions in O(n log n)

> Use Merge Sort to count inversions: pairs (i, j) with i < j and A[i] > A[j].

#### Python

```python
def count_inversions(arr):
    aux = [0] * len(arr)
    return _sort_count(arr, aux, 0, len(arr) - 1)

def _sort_count(a, aux, lo, hi):
    if lo >= hi: return 0
    mid = (lo + hi) // 2
    count = _sort_count(a, aux, lo, mid) + _sort_count(a, aux, mid + 1, hi)
    count += _merge_count(a, aux, lo, mid, hi)
    return count

def _merge_count(a, aux, lo, mid, hi):
    for k in range(lo, hi + 1): aux[k] = a[k]
    i, j, count = lo, mid + 1, 0
    for k in range(lo, hi + 1):
        if i > mid:                 a[k] = aux[j]; j += 1
        elif j > hi:                a[k] = aux[i]; i += 1
        elif aux[i] <= aux[j]:      a[k] = aux[i]; i += 1
        else:
            a[k] = aux[j]; j += 1
            count += mid - i + 1
    return count

print(count_inversions([5, 4, 3, 2, 1]))  # 10
print(count_inversions([2, 4, 1, 3, 5]))  # 3
```

(Go and Java versions in `middle.md`.)

---

### Challenge 3: Merge K Sorted Lists (LeetCode 23)

> Given an array of k sorted lists, merge them into one sorted list. Use a min-heap.

#### Python

```python
import heapq

class ListNode:
    def __init__(self, val=0, nxt=None):
        self.val = val
        self.next = nxt

def merge_k_lists(lists):
    heap = []
    # Initialize heap with first node from each list
    for i, head in enumerate(lists):
        if head:
            heapq.heappush(heap, (head.val, i, head))
    dummy = ListNode()
    tail = dummy
    while heap:
        val, i, node = heapq.heappop(heap)
        tail.next = node
        tail = tail.next
        if node.next:
            heapq.heappush(heap, (node.next.val, i, node.next))
    return dummy.next
```

**Time:** O(N log k) where N = total nodes, k = number of lists.
**Space:** O(k) for the heap.

---

### Challenge 4: Sort a Linked List (LeetCode 148)

> Sort a linked list in O(n log n) time using O(1) extra space (for the algorithm itself; recursion uses O(log n) stack).

#### Python

```python
class ListNode:
    def __init__(self, val=0, nxt=None):
        self.val = val
        self.next = nxt

def sort_list(head):
    if not head or not head.next:
        return head
    # Find midpoint
    slow, fast = head, head.next
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next
    mid = slow.next
    slow.next = None
    # Recursively sort halves
    left  = sort_list(head)
    right = sort_list(mid)
    return merge_two_lists(left, right)

def merge_two_lists(l, r):
    dummy = ListNode()
    tail = dummy
    while l and r:
        if l.val <= r.val:
            tail.next = l; l = l.next
        else:
            tail.next = r; r = r.next
        tail = tail.next
    tail.next = l or r
    return dummy.next
```

**Why Merge Sort?** Quick Sort needs random access (no O(1) middle access in linked lists). Merge Sort uses sequential access only.

---

### Challenge 5: External Sort

> Sort a 10 GB file with 100 MB RAM. Outline the algorithm.

**Answer:**
1. **Phase 1 (run generation):** Read 100 MB chunks, sort each in memory (TimSort), write to disk → 100 sorted runs.
2. **Phase 2 (k-way merge):** Open all 100 runs, use min-heap of size 100 to repeatedly take the smallest current element, write to output. Stream — never load all data at once.

```python
# Phase 1
def chunk_and_sort(input_file, chunk_size_mb=100):
    chunk_size = chunk_size_mb * 1_000_000 // 4  # int = 4 bytes
    runs = []
    with open(input_file) as f:
        chunk = []
        for line in f:
            chunk.append(int(line))
            if len(chunk) >= chunk_size:
                chunk.sort()
                runs.append(write_run(chunk))
                chunk = []
        if chunk:
            chunk.sort()
            runs.append(write_run(chunk))
    return runs

# Phase 2
import heapq
def merge_runs(run_paths, output_path):
    iterators = [iter_file(p) for p in run_paths]
    with open(output_path, 'w') as out:
        for val in heapq.merge(*iterators):
            out.write(f"{val}\n")
```

---

## Common Interview Pitfalls

| Pitfall | What goes wrong | Fix |
|---------|----------------|-----|
| Using `<` instead of `<=` in merge | Loses stability silently | Always use `<=` |
| Computing `mid = (lo + hi) / 2` | Integer overflow on huge n | Use `lo + (hi - lo) / 2` |
| Allocating new arrays per recursion | Wasteful memory | Use one shared aux buffer |
| Forgetting leftover loops after main merge | Some elements missing | Always `out += L[i:]; out += R[j:]` |
| Saying "Merge Sort is in-place" | False for arrays | Be specific: O(n) aux for arrays, O(log n) for linked lists |
| Confusing Merge Sort and Quick Sort | Both divide-and-conquer but very different | Merge: split, recurse, merge. Quick: pivot, partition, recurse on parts. |
| Not handling empty/single | Edge case crash | Always check `len <= 1` base case |
| Naive `arr[:mid]` slicing in Python | O(n) copy at every level → O(n log n) extra memory | Use index ranges (lo, hi), not slices |

---

## Behavioral / System Design

> **"Design a system to sort 100 TB of log entries by timestamp."**

**Strong answer:**
1. **Distributed sort** — partition by time range (use sample-based quantiles).
2. Each node performs **external merge sort** on its partition.
3. Concatenate sorted partitions → globally sorted output.
4. Use **Spark/MapReduce** for orchestration; **Parquet** with sort hints for storage.
5. Monitor: shuffle bytes, spill bytes per node, merge throughput.
6. Capacity plan: 100 TB / 10 nodes × 10 TB per node × 4× I/O for external sort = 400 TB total disk I/O.

> **"Why does Python's `sorted()` use TimSort instead of vanilla Merge Sort?"**

**Strong answer:**
- TimSort is **adaptive** — O(n) on sorted/reverse-sorted data; vanilla merge is always Θ(n log n).
- TimSort handles **runs** of natural ordering common in real-world data.
- TimSort uses **Insertion Sort for small subarrays** (cutoff ~32-64) — faster constant factor.
- TimSort has **galloping mode** when runs are very imbalanced — exponential search skip.
- Maintains **stability** and **worst-case O(n log n)** guarantees.

> **"You're processing time-series data from 1000 sensors. Each sensor's data is sorted by timestamp, but globally unsorted. Design an O(n log k) merger."**

**Strong answer:**
- Use **k-way merge with min-heap**, where k=1000.
- Heap entries: `(timestamp, sensor_id, iterator_to_next_value)`.
- Pop smallest, emit, push next from same sensor.
- O(n log 1000) ≈ 10n operations — much better than naive sort O(n log n) where n could be billions.

---

## One-Liner Summary

> **Merge Sort:** Divide-and-conquer sort. Split in half, recursively sort each half, merge. **O(n log n) in all cases**, **stable**, **O(n) auxiliary space**. The basis of TimSort (Python/Java), external merge sort (databases), and parallel sort (`Arrays.parallelSort`). The only sort that combines stability with worst-case O(n log n).
