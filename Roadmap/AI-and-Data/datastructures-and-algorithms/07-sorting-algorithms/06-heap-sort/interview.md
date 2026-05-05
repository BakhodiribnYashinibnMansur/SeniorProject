# Heap Sort — Interview Questions

> **Audience:** candidates preparing for technical interviews. Includes questions ranging from junior conceptual ("what is a heap?") to professional algorithmic ("derive the comparison count"), plus 5 coding challenges with full Go / Java / Python solutions.

---

## Table of Contents

1. [Question Bank by Level](#question-bank-by-level)
2. [Coding Challenges](#coding-challenges)
3. [Behavioral and System-Design Angles](#behavioral-and-system-design-angles)

---

## Question Bank by Level

### Junior

| # | Question | Sample Answer (one sentence) |
|---|----------|------------------------------|
| 1 | What is a binary heap? | A complete binary tree stored as an array, where every parent satisfies a heap property (≥ for max-heap, ≤ for min-heap) with respect to its children. |
| 2 | Difference between max-heap and min-heap? | Max-heap root is the largest; min-heap root is the smallest. |
| 3 | What are the index formulas (zero-indexed)? | `parent(i) = (i-1)/2`, `left = 2i+1`, `right = 2i+2`. |
| 4 | Time complexity of Heap Sort (best/avg/worst)? | `O(n log n)` in all three. |
| 5 | Space complexity of Heap Sort? | `O(1)` — in place. |
| 6 | Is Heap Sort stable? | No. |
| 7 | What does "in-place" mean? | Uses `O(1)` extra memory beyond the input array. |
| 8 | What does "complete binary tree" mean? | All levels full except possibly the last, which fills left-to-right. |
| 9 | Why use a max-heap to sort ascending? | Each extracted max goes to the rightmost free slot, leaving the smallest at index 0. |
| 10 | Two phases of Heap Sort? | Build-heap (`O(n)`), then extract `n` times (`O(n log n)`). |
| 11 | What is `siftDown`? | An operation that restores the heap property by repeatedly swapping a node with its larger child until it lands. |
| 12 | What is `siftUp`? | The same idea upward — swap with parent until heap property holds. |
| 13 | When do you use `siftDown` vs `siftUp`? | `siftDown` for build-heap and extract-root; `siftUp` for insert. |
| 14 | What's the height of a heap with n nodes? | `⌊log₂ n⌋`. |
| 15 | Give one real-world use of a heap. | Priority queue (e.g., Dijkstra's algorithm, OS task scheduler). |

### Middle

| # | Question | Sample Answer |
|---|----------|---------------|
| 16 | Why is build-heap `O(n)` and not `O(n log n)`? | Because most nodes are near the bottom and need very little sift-down work; the geometric series `Σ h/2ʰ = 2` collapses the cost. |
| 17 | When would you choose Heap Sort over Quick Sort? | When you need a guaranteed `O(n log n)` worst-case time and `O(1)` extra space — e.g., real-time systems or untrusted input. |
| 18 | When is Quick Sort better in practice? | Always for cache-rich modern hardware where adversarial input is unlikely; Quick Sort has better cache locality and lower constants. |
| 19 | Why is Heap Sort cache-unfriendly? | Children at index `2i+1, 2i+2` have doubling memory stride; deep sift-downs miss cache on every level. |
| 20 | What's a priority queue? | An ADT supporting insert + extract-min (or extract-max), typically backed by a binary heap. |
| 21 | What's the cost of finding the max in a min-heap? | `O(n)` — the max could be at any leaf; a min-heap doesn't track it. |
| 22 | How do you implement a max-heap with Python's `heapq`? | Push negated values, or wrap items in a class with reversed `__lt__`. |
| 23 | How do you efficiently find the k largest in an array of size n? | Maintain a min-heap of size k, replacing the min when a larger element arrives — `O(n log k)`. |
| 24 | How do you merge k sorted arrays? | Use a min-heap of `(value, list_index, element_index)` tuples; `O(N log k)` total. |
| 25 | Why isn't Heap Sort adaptive? | Even an already-sorted input requires the full build-heap and full extract — no fast path. |
| 26 | What's `heapreplace` vs `heappushpop`? | `heapreplace` always pops then pushes (single sift); `heappushpop` checks if the new value is smaller than the root and skips the heap op. |
| 27 | What's the difference between `siftDown` recursive vs iterative? | Same complexity; iterative is friendlier to JIT and avoids deep stack frames. |
| 28 | How do you handle decrease-key on a binary heap? | Need an "indexed heap" that maps items → array positions; then sift up after lowering the priority. `O(log n)`. |
| 29 | Why are equal keys reordered by Heap Sort? | During sift-down, the algorithm can swap a parent with a child of equal value, breaking the original input order. |
| 30 | How does Heap Sort behave on `[5,5,5,5]`? | Builds a heap (no swaps needed), then extracts each 5 in turn — output is `[5,5,5,5]` but original positional identity is lost. |

### Senior

| # | Question | Sample Answer |
|---|----------|---------------|
| 31 | What is Introsort and why does it use Heap Sort? | A hybrid that runs Quick Sort but switches to Heap Sort when recursion depth exceeds `2 log n`, guaranteeing `O(n log n)` worst case while keeping Quick Sort's average speed. |
| 32 | Where in production do you find Introsort? | C++ `std::sort`, .NET `Array.Sort`, Rust `sort_unstable`, old Go `sort.Slice` (now pdqsort). |
| 33 | What's the difference between Heap Sort and pdqsort? | pdqsort is pattern-defeating Quick Sort that detects adversarial inputs and switches to a different pivot strategy; Heap Sort is the worst-case fallback. |
| 34 | How would you implement top-k across a sharded dataset? | Each shard computes local top-k with a min-heap of size k; coordinator merges the s × k items with a final heap. |
| 35 | What's the time/space cost of Dijkstra with a binary heap? | `O((V + E) log V)` time, `O(V)` space (excluding the graph). |
| 36 | Why isn't Fibonacci heap used in practice for Dijkstra? | The constants are huge and pointer-heavy — binary heap with lazy stale-skipping is faster on most graphs. |
| 37 | How do you make a thread-safe priority queue? | Single-mutex binary heap (Java's `PriorityBlockingQueue`); for high contention, use `ConcurrentSkipListMap` or per-thread heaps + merge. |
| 38 | What metrics would you monitor for a heap-backed job queue in production? | Heap size (backlog), push/pop latency, age of root item (head-of-line blocking), staleness ratio. |
| 39 | Explain the `O(log n / log log n)` bound for d-ary heaps with d = log n. | Tree height is `log_d n = log n / log log n`; sift-down does this many levels but compares `d = log n` siblings per level. |
| 40 | Why is parallel Heap Sort hard? | The extraction phase has a strict sequential dependence (each extraction depends on the previous one), so the critical path is `Ω(n log n)`. |

### Professional

| # | Question | Sample Answer |
|---|----------|---------------|
| 41 | Prove build-heap is `O(n)`. | Σ over levels of (nodes at height h) × h ≤ Σ (n/2^(h+1)) · h = (n/2) · Σ h/2^h = (n/2) · 2 = n. |
| 42 | Derive the comparison count of standard Heap Sort. | Phase 1: `Θ(n)`. Phase 2: `n` extractions × `2 log n` per sift-down (one comp to find larger child, one to compare with parent) = `2 n log n`. |
| 43 | What does Wegener's bottom-up sift-down save? | Reduces comparisons from `~2 n log n` to `~n log n + O(n log log n)` by following the path of larger children first, then sifting up to find the right insertion point. |
| 44 | What's the cache complexity of Heap Sort? | `Θ((n / B) log n)` — strictly worse than Merge Sort's `Θ((n/B) log(n/M))`, by a factor of `log M`. |
| 45 | Prove the comparison-sort lower bound `Ω(n log n)`. | A decision tree distinguishing `n!` permutations has at least `n!` leaves, so height `≥ log₂(n!) = Ω(n log n)`. |
| 46 | Does Heap Sort match the lower bound? | Yes asymptotically (`Θ(n log n)`). Standard Heap Sort's constant is `2`; bottom-up Heap Sort matches the optimal constant. |
| 47 | How does cache-oblivious heap (funnel-heap) achieve optimal cache complexity? | By restructuring the heap so every subtree of size B fits in a cache line; sift-down accesses contiguous memory at every level. |
| 48 | What is a soft heap and when is it useful? | A heap allowing some priority "corruption" (priorities can be silently raised) for `O(1)` amortized per op; used in Chazelle's `O(E α(V))` MST algorithm. |
| 49 | Why does the heap invariant not give a total order on cousins? | The invariant only constrains parent-child relationships; siblings and cousins can be in any order, so iterating a heap gives unsorted output. |
| 50 | What's the relationship between Heap Sort and the median? | Building a heap doesn't find the median; you'd need a separate heap-based median-finder (two heaps) or QuickSelect. |

---

## Coding Challenges

### Challenge 1 — Implement Heap Sort

> **Prompt:** Implement an in-place ascending Heap Sort. Do not use any standard library heap functions.

**Why interviewers like this:** tests understanding of build-heap and sift-down without a "cheat sheet" library.

#### Go

```go
package main

import "fmt"

func HeapSort(arr []int) {
	n := len(arr)
	for i := n/2 - 1; i >= 0; i-- {
		siftDown(arr, i, n)
	}
	for end := n - 1; end > 0; end-- {
		arr[0], arr[end] = arr[end], arr[0]
		siftDown(arr, 0, end)
	}
}

func siftDown(arr []int, i, n int) {
	for {
		l := 2*i + 1
		if l >= n {
			return
		}
		largest := l
		if r := l + 1; r < n && arr[r] > arr[l] {
			largest = r
		}
		if arr[i] >= arr[largest] {
			return
		}
		arr[i], arr[largest] = arr[largest], arr[i]
		i = largest
	}
}

func main() {
	a := []int{4, 10, 3, 5, 1, 9, 2, 8, 7, 6}
	HeapSort(a)
	fmt.Println(a) // [1 2 3 4 5 6 7 8 9 10]
}
```

#### Java

```java
public class HeapSort {
    public static void sort(int[] a) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) siftDown(a, i, n);
        for (int end = n - 1; end > 0; end--) {
            int tmp = a[0]; a[0] = a[end]; a[end] = tmp;
            siftDown(a, 0, end);
        }
    }

    private static void siftDown(int[] a, int i, int n) {
        while (true) {
            int l = 2 * i + 1;
            if (l >= n) return;
            int largest = l;
            int r = l + 1;
            if (r < n && a[r] > a[l]) largest = r;
            if (a[i] >= a[largest]) return;
            int tmp = a[i]; a[i] = a[largest]; a[largest] = tmp;
            i = largest;
        }
    }
}
```

#### Python

```python
def heap_sort(arr: list[int]) -> None:
    n = len(arr)
    for i in range(n // 2 - 1, -1, -1):
        sift_down(arr, i, n)
    for end in range(n - 1, 0, -1):
        arr[0], arr[end] = arr[end], arr[0]
        sift_down(arr, 0, end)

def sift_down(arr: list[int], i: int, n: int) -> None:
    while 2 * i + 1 < n:
        largest = 2 * i + 1
        right = 2 * i + 2
        if right < n and arr[right] > arr[largest]:
            largest = right
        if arr[i] >= arr[largest]:
            return
        arr[i], arr[largest] = arr[largest], arr[i]
        i = largest
```

**Follow-ups to expect:**
- Make it generic over a comparator.
- Make it stable.
- Implement bottom-up sift-down (Wegener's variant).
- What changes for a min-heap?

---

### Challenge 2 — K-th Largest Element

> **Prompt:** Given an unsorted integer array `nums` and integer `k`, return the `k`-th largest element. Aim for `O(n log k)` time, `O(k)` space.

**Idea:** maintain a min-heap of size `k`. The root is the k-th largest seen so far.

#### Go

```go
package main

import (
	"container/heap"
	"fmt"
)

type MinHeap []int

func (h MinHeap) Len() int            { return len(h) }
func (h MinHeap) Less(i, j int) bool  { return h[i] < h[j] }
func (h MinHeap) Swap(i, j int)       { h[i], h[j] = h[j], h[i] }
func (h *MinHeap) Push(x any)         { *h = append(*h, x.(int)) }
func (h *MinHeap) Pop() any           { old := *h; n := len(old); x := old[n-1]; *h = old[:n-1]; return x }

func KthLargest(nums []int, k int) int {
	h := &MinHeap{}
	heap.Init(h)
	for _, x := range nums {
		if h.Len() < k {
			heap.Push(h, x)
		} else if x > (*h)[0] {
			(*h)[0] = x
			heap.Fix(h, 0)
		}
	}
	return (*h)[0]
}

func main() {
	fmt.Println(KthLargest([]int{3, 2, 1, 5, 6, 4}, 2)) // 5
}
```

#### Java

```java
import java.util.PriorityQueue;

public class KthLargest {
    public static int find(int[] nums, int k) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(k);  // min-heap
        for (int x : nums) {
            if (pq.size() < k) pq.add(x);
            else if (x > pq.peek()) {
                pq.poll();
                pq.add(x);
            }
        }
        return pq.peek();
    }
}
```

#### Python

```python
import heapq

def kth_largest(nums: list[int], k: int) -> int:
    h = nums[:k]
    heapq.heapify(h)
    for x in nums[k:]:
        if x > h[0]:
            heapq.heapreplace(h, x)
    return h[0]
```

**Follow-ups:**
- What if `k` is also large (close to `n/2`)? → use Quickselect.
- What if data is streaming? → heap is the only option.
- What about k-th smallest? → flip to a max-heap of size k.

---

### Challenge 3 — Merge K Sorted Lists

> **Prompt:** You are given `k` sorted linked lists. Merge them into one sorted list. `O(N log k)` time where `N = total elements`.

**Idea:** push the head of each list into a min-heap; pop the smallest, advance that list's pointer, push the new head.

#### Go

```go
package main

import "container/heap"

type ListNode struct {
	Val  int
	Next *ListNode
}

type NodeHeap []*ListNode

func (h NodeHeap) Len() int            { return len(h) }
func (h NodeHeap) Less(i, j int) bool  { return h[i].Val < h[j].Val }
func (h NodeHeap) Swap(i, j int)       { h[i], h[j] = h[j], h[i] }
func (h *NodeHeap) Push(x any)         { *h = append(*h, x.(*ListNode)) }
func (h *NodeHeap) Pop() any           { old := *h; n := len(old); x := old[n-1]; *h = old[:n-1]; return x }

func MergeKLists(lists []*ListNode) *ListNode {
	h := &NodeHeap{}
	heap.Init(h)
	for _, l := range lists {
		if l != nil {
			heap.Push(h, l)
		}
	}
	dummy := &ListNode{}
	tail := dummy
	for h.Len() > 0 {
		node := heap.Pop(h).(*ListNode)
		tail.Next = node
		tail = node
		if node.Next != nil {
			heap.Push(h, node.Next)
		}
	}
	return dummy.Next
}
```

#### Java

```java
import java.util.PriorityQueue;

class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}

public class MergeKLists {
    public static ListNode merge(ListNode[] lists) {
        PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
        for (ListNode l : lists) if (l != null) pq.add(l);
        ListNode dummy = new ListNode(0), tail = dummy;
        while (!pq.isEmpty()) {
            ListNode n = pq.poll();
            tail.next = n;
            tail = n;
            if (n.next != null) pq.add(n.next);
        }
        return dummy.next;
    }
}
```

#### Python

```python
import heapq
from typing import List, Optional

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next
    def __lt__(self, other):
        return self.val < other.val   # required by heapq tiebreaker

def merge_k_lists(lists: List[Optional[ListNode]]) -> Optional[ListNode]:
    h = [l for l in lists if l]
    heapq.heapify(h)
    dummy = tail = ListNode()
    while h:
        node = heapq.heappop(h)
        tail.next = node
        tail = node
        if node.next:
            heapq.heappush(h, node.next)
    return dummy.next
```

**Follow-ups:**
- What if a list is `null` / empty? → skip during init.
- Why `O(N log k)`? → each push/pop is `O(log k)`, done `N` times.
- Can you do it without a heap? → divide-and-conquer pairwise merge (`O(N log k)` too).

---

### Challenge 4 — Top-K Frequent Elements

> **Prompt:** Given an integer array and `k`, return the `k` most frequent elements. `O(n log k)` time.

#### Go

```go
package main

import (
	"container/heap"
	"fmt"
)

type freqItem struct {
	val, count int
}

type FreqHeap []freqItem

func (h FreqHeap) Len() int           { return len(h) }
func (h FreqHeap) Less(i, j int) bool { return h[i].count < h[j].count } // min-heap of size k
func (h FreqHeap) Swap(i, j int)      { h[i], h[j] = h[j], h[i] }
func (h *FreqHeap) Push(x any)        { *h = append(*h, x.(freqItem)) }
func (h *FreqHeap) Pop() any          { old := *h; n := len(old); x := old[n-1]; *h = old[:n-1]; return x }

func TopKFrequent(nums []int, k int) []int {
	cnt := make(map[int]int)
	for _, x := range nums {
		cnt[x]++
	}
	h := &FreqHeap{}
	heap.Init(h)
	for v, c := range cnt {
		heap.Push(h, freqItem{v, c})
		if h.Len() > k {
			heap.Pop(h)
		}
	}
	result := make([]int, h.Len())
	for i := h.Len() - 1; i >= 0; i-- {
		result[i] = heap.Pop(h).(freqItem).val
	}
	return result
}

func main() {
	fmt.Println(TopKFrequent([]int{1, 1, 1, 2, 2, 3}, 2)) // [1 2]
}
```

#### Java

```java
import java.util.*;

public class TopKFrequent {
    public static int[] find(int[] nums, int k) {
        Map<Integer, Integer> cnt = new HashMap<>();
        for (int x : nums) cnt.merge(x, 1, Integer::sum);
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        for (var e : cnt.entrySet()) {
            pq.add(new int[]{e.getKey(), e.getValue()});
            if (pq.size() > k) pq.poll();
        }
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = pq.poll()[0];
        return result;
    }
}
```

#### Python

```python
from collections import Counter
import heapq

def top_k_frequent(nums: list[int], k: int) -> list[int]:
    cnt = Counter(nums)
    return [x for x, _ in heapq.nlargest(k, cnt.items(), key=lambda kv: kv[1])]
```

**Follow-ups:**
- Bucket-sort variant for `O(n)` time? → Group keys by frequency in arrays of buckets, scan from highest.
- Approximate top-k for streams? → Count-Min Sketch + heap.

---

### Challenge 5 — Sliding Window Median

> **Prompt:** Given an integer array and window size `k`, return the median of every window. `O(n log k)` per window with two heaps.

**Idea:** keep a max-heap for the lower half and a min-heap for the upper half; balance after each insert/remove.

#### Python (most concise)

```python
import heapq

def median_sliding_window(nums: list[int], k: int) -> list[float]:
    lo = []  # max-heap (negated)
    hi = []  # min-heap
    delayed = {}
    result = []

    def prune(heap):
        while heap:
            top = -heap[0] if heap is lo else heap[0]
            if delayed.get(top, 0) > 0:
                delayed[top] -= 1
                heapq.heappop(heap)
            else:
                break

    def balance():
        if len(lo) > len(hi) + 1:
            heapq.heappush(hi, -heapq.heappop(lo))
            prune(lo)
        elif len(hi) > len(lo):
            heapq.heappush(lo, -heapq.heappop(hi))
            prune(hi)

    def median():
        if k % 2:
            return float(-lo[0])
        return (-lo[0] + hi[0]) / 2

    for i in range(k):
        heapq.heappush(lo, -nums[i])
    for _ in range(k // 2):
        heapq.heappush(hi, -heapq.heappop(lo))
    result.append(median())

    for i in range(k, len(nums)):
        # add new
        if not lo or nums[i] <= -lo[0]:
            heapq.heappush(lo, -nums[i])
        else:
            heapq.heappush(hi, nums[i])
        # mark old as delayed
        out = nums[i - k]
        delayed[out] = delayed.get(out, 0) + 1
        if out <= -lo[0]:
            balance()                       # might be in lo
        else:
            balance()
        prune(lo); prune(hi)
        balance()
        result.append(median())
    return result
```

#### Java

```java
import java.util.*;

public class SlidingMedian {
    public static double[] medianSlidingWindow(int[] nums, int k) {
        TreeMap<Integer, Integer> low = new TreeMap<>(Collections.reverseOrder()), high = new TreeMap<>();
        int lowSize = 0, highSize = 0;
        double[] result = new double[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            // add nums[i]
            if (low.isEmpty() || nums[i] <= low.firstKey()) { low.merge(nums[i], 1, Integer::sum); lowSize++; }
            else { high.merge(nums[i], 1, Integer::sum); highSize++; }
            // remove nums[i - k]
            if (i >= k) {
                int out = nums[i - k];
                if (low.containsKey(out)) {
                    if (low.get(out) == 1) low.remove(out); else low.merge(out, -1, Integer::sum);
                    lowSize--;
                } else {
                    if (high.get(out) == 1) high.remove(out); else high.merge(out, -1, Integer::sum);
                    highSize--;
                }
            }
            // balance
            while (lowSize > highSize + 1) {
                int v = low.firstKey();
                if (low.get(v) == 1) low.remove(v); else low.merge(v, -1, Integer::sum);
                high.merge(v, 1, Integer::sum);
                lowSize--; highSize++;
            }
            while (highSize > lowSize) {
                int v = high.firstKey();
                if (high.get(v) == 1) high.remove(v); else high.merge(v, -1, Integer::sum);
                low.merge(v, 1, Integer::sum);
                highSize--; lowSize++;
            }
            // record
            if (i >= k - 1) {
                if (k % 2 == 1) result[i - k + 1] = low.firstKey();
                else result[i - k + 1] = ((double) low.firstKey() + high.firstKey()) / 2.0;
            }
        }
        return result;
    }
}
```

(Java implementation uses `TreeMap` for true `O(log k)` removal — pure two-heaps in Java requires lazy-delete bookkeeping similar to Python.)

#### Go

```go
// Implement with two `container/heap`s + a "delayed" map for lazy deletion.
// Logic mirrors the Python solution. Omitted here for brevity (≈80 lines).
```

**Follow-ups:**
- Why is this `O(n log k)`? → Each window does `O(log k)` heap ops + `O(log k)` median lookup.
- Can you use a `sortedlist` instead? → Yes — `O(n log k)` with `sortedcontainers.SortedList` in Python. Often simpler.
- What about a balanced BST? → Java's `TreeMap` gives clean `O(log k)` removal — preferred over two-heaps when removal is the bottleneck.

---

## Behavioral and System-Design Angles

### Common follow-up: "Tell me about a time you used a heap in production."

Prepare a story. Examples:

- **Job scheduler with priority** — heap holds pending jobs by priority + arrival time. Discuss eviction policy when heap is full.
- **Top-N analytics** — heap of size N over a stream of events; emit on tick. Discuss latency / freshness trade-offs.
- **Dijkstra in a routing service** — heap holds (estimated distance, vertex). Discuss A* heuristic and bidirectional search if asked deeper.

### "Design a real-time leaderboard for a game"

Heap-friendly setup:
- Per-region min-heap of size N for top-N scores.
- Periodic merge across regions for global top-N.
- Use Redis sorted sets if data size demands persistence.

Discuss:
- Update latency vs query latency trade-off.
- Hot-shard problem (one region with most traffic).
- Approximate vs exact top-N.

### "Design an event scheduler for a game engine"

Heap of `(scheduled_time, event_id)` ordered by time. Each tick:
- Pop all events with `time <= now`.
- Process in order; events may schedule new events.

Discuss:
- Tie-breaking (insertion-order counter to make heap deterministic).
- Heap reset on level reload (clear in `O(1)`).
- Scheduler as an actor with backpressure when heap exceeds size N.

---

> **End of interview file.** Move on to `tasks.md` for 15 graded coding exercises.
