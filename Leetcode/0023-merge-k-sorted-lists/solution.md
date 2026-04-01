# 0023. Merge k Sorted Lists

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force (Collect All Values, Sort)](#approach-1-brute-force-collect-all-values-sort)
4. [Approach 2: Min Heap / Priority Queue](#approach-2-min-heap--priority-queue)
5. [Approach 3: Divide and Conquer (Merge Sort Style)](#approach-3-divide-and-conquer-merge-sort-style)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Linked List`, `Divide and Conquer`, `Heap (Priority Queue)`, `Merge Sort` |

### Description

> You are given an array of `k` linked-lists `lists`, each linked-list is sorted in ascending order. Merge all the linked-lists into one sorted linked-list and return it.

### Examples

```
Example 1:
Input:  lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]
Explanation: The linked-lists are:
  1 -> 4 -> 5
  1 -> 3 -> 4
  2 -> 6
Merging them into one sorted list: 1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6

Example 2:
Input:  lists = []
Output: []

Example 3:
Input:  lists = [[]]
Output: []
```

### Constraints

- `k == lists.length`
- `0 <= k <= 10^4`
- `0 <= lists[i].length <= 500`
- `-10^4 <= lists[i][j] <= 10^4`
- `lists[i]` is sorted in ascending order
- The sum of `lists[i].length` will not exceed `10^4`

---

## Problem Breakdown

### 1. What is being asked?

Given `k` sorted linked lists, merge them all into a single sorted linked list. This is a generalization of merging 2 sorted lists (problem 21) to k lists.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `lists` | `ListNode[]` | Array of heads of k sorted linked lists |

Important observations about the input:
- `k` can be 0 (empty array) or lists can contain empty lists
- Each individual list is already sorted in ascending order
- Total number of nodes across all lists is at most 10^4

### 3. What is the output?

- The **head** of a single sorted linked list containing all nodes from all input lists
- If all input lists are empty, return `null`/`nil`/`None`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `0 <= k <= 10^4` | Could have many lists — O(k) operations per node matters |
| `0 <= lists[i].length <= 500` | Individual lists can be empty |
| `sum of lengths <= 10^4` | Total nodes N is moderate — O(N log k) is efficient |
| `lists[i]` sorted ascending | We can exploit sorted property with a min heap or merge |

### 5. Step-by-step example analysis

#### Example 1: `lists = [[1,4,5],[1,3,4],[2,6]]` -> `[1,1,2,3,4,4,5,6]`

```text
List 0: 1 -> 4 -> 5
List 1: 1 -> 3 -> 4
List 2: 2 -> 6

Step-by-step merge (heap approach):
  Heap: [(1, list0), (1, list1), (2, list2)]
  Pop min (1, list0) -> result: [1], push (4, list0)
  Heap: [(1, list1), (2, list2), (4, list0)]
  Pop min (1, list1) -> result: [1,1], push (3, list1)
  Heap: [(2, list2), (3, list1), (4, list0)]
  Pop min (2, list2) -> result: [1,1,2], push (6, list2)
  Heap: [(3, list1), (4, list0), (6, list2)]
  Pop min (3, list1) -> result: [1,1,2,3], push (4, list1)
  Pop min (4, list0) -> result: [1,1,2,3,4], push (5, list0)
  Pop min (4, list1) -> result: [1,1,2,3,4,4], list1 exhausted
  Pop min (5, list0) -> result: [1,1,2,3,4,4,5], list0 exhausted
  Pop min (6, list2) -> result: [1,1,2,3,4,4,5,6], list2 exhausted

Result: [1,1,2,3,4,4,5,6]
```

### 6. Key Observations

1. **Generalization of merge two** — merging k lists extends the classic merge-two-sorted-lists problem.
2. **Heap as k-way selector** — a min heap can efficiently pick the smallest among k candidates in O(log k) time.
3. **Divide and conquer** — recursively merge pairs of lists, halving the number of lists each round.
4. **All lists sorted** — we only need to compare the current heads, not all remaining elements.
5. **Empty list handling** — must gracefully handle empty input array and individual empty lists.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Min Heap / Priority Queue | Efficiently select minimum among k elements | This problem |
| Divide and Conquer | Merge pairs recursively like merge sort | This problem |
| Merge Two Sorted Lists | Building block for both approaches | LeetCode 21 |

**Chosen pattern:** `Min Heap` or `Divide and Conquer`
**Reason:** Both achieve O(N log k) time. The heap approach processes one node at a time; divide and conquer merges pairs of lists level by level.

---

## Approach 1: Brute Force (Collect All Values, Sort)

### Thought process

> Simplest approach: traverse all linked lists, collect every value into an array, sort the array, then build a new linked list from the sorted values.

### Algorithm (step-by-step)

1. Traverse all k linked lists, collecting every node's value into an array
2. Sort the array
3. Create a new linked list from the sorted values
4. Return the head of the new list

### Pseudocode

```text
function mergeKLists(lists):
    values = []
    for each list in lists:
        node = list
        while node != null:
            values.append(node.val)
            node = node.next

    sort(values)

    dummy = ListNode(0)
    curr = dummy
    for v in values:
        curr.next = ListNode(v)
        curr = curr.next

    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(N log N) | Sorting all N values dominates |
| **Space** | O(N) | Storing all values in an array |

### Implementation

#### Go

```go
func mergeKListsBrute(lists []*ListNode) *ListNode {
    vals := []int{}
    for _, l := range lists {
        for l != nil {
            vals = append(vals, l.Val)
            l = l.Next
        }
    }
    sort.Ints(vals)
    dummy := &ListNode{}
    curr := dummy
    for _, v := range vals {
        curr.Next = &ListNode{Val: v}
        curr = curr.Next
    }
    return dummy.Next
}
```

#### Java

```java
public ListNode mergeKListsBrute(ListNode[] lists) {
    List<Integer> vals = new ArrayList<>();
    for (ListNode l : lists) {
        while (l != null) {
            vals.add(l.val);
            l = l.next;
        }
    }
    Collections.sort(vals);
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    for (int v : vals) {
        curr.next = new ListNode(v);
        curr = curr.next;
    }
    return dummy.next;
}
```

#### Python

```python
def mergeKListsBrute(self, lists):
    vals = []
    for l in lists:
        while l:
            vals.append(l.val)
            l = l.next
    vals.sort()
    dummy = ListNode(0)
    curr = dummy
    for v in vals:
        curr.next = ListNode(v)
        curr = curr.next
    return dummy.next
```

### Dry Run

```text
Input: lists = [[1,4,5],[1,3,4],[2,6]]

Step 1 — Collect values:
  List 0: 1, 4, 5
  List 1: 1, 3, 4
  List 2: 2, 6
  values = [1, 4, 5, 1, 3, 4, 2, 6]

Step 2 — Sort:
  values = [1, 1, 2, 3, 4, 4, 5, 6]

Step 3 — Build linked list:
  1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6

Result: [1, 1, 2, 3, 4, 4, 5, 6] ✅
```

---

## Approach 2: Min Heap / Priority Queue

### The problem with Brute Force

> Brute force ignores the fact that each list is already sorted. We discard all ordering information by dumping into an array. We can do better by exploiting the sorted property.

### Optimization idea

> **Min Heap as a k-way merge selector!** Maintain a min heap of size at most k, containing the current head of each non-empty list. Repeatedly extract the minimum, append it to the result, and push the next node from that list (if any).
>
> Key insight:
> - At any point, the next smallest node must be the head of one of the k lists
> - A min heap finds the minimum among k elements in O(log k) time
> - We process each of the N total nodes exactly once

### Algorithm (step-by-step)

1. Create a min heap
2. Push the head of each non-empty list into the heap
3. While the heap is not empty:
   a. Pop the minimum node
   b. Append it to the result list
   c. If the popped node has a next, push next into the heap
4. Return the result head

### Pseudocode

```text
function mergeKLists(lists):
    heap = new MinHeap()
    for i = 0 to k-1:
        if lists[i] != null:
            heap.push(lists[i])

    dummy = ListNode(0)
    curr = dummy

    while heap is not empty:
        node = heap.pop()
        curr.next = node
        curr = curr.next
        if node.next != null:
            heap.push(node.next)

    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(N log k) | Each of N nodes is pushed/popped from a heap of size at most k |
| **Space** | O(k) | Heap stores at most k nodes at any time |

### Implementation

#### Go

```go
func mergeKLists(lists []*ListNode) *ListNode {
    h := &MinHeap{}
    heap.Init(h)
    for _, l := range lists {
        if l != nil {
            heap.Push(h, l)
        }
    }
    dummy := &ListNode{}
    curr := dummy
    for h.Len() > 0 {
        node := heap.Pop(h).(*ListNode)
        curr.Next = node
        curr = curr.Next
        if node.Next != nil {
            heap.Push(h, node.Next)
        }
    }
    return dummy.Next
}
```

#### Java

```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
    for (ListNode l : lists) {
        if (l != null) pq.offer(l);
    }
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    while (!pq.isEmpty()) {
        ListNode node = pq.poll();
        curr.next = node;
        curr = curr.next;
        if (node.next != null) pq.offer(node.next);
    }
    return dummy.next;
}
```

#### Python

```python
def mergeKLists(self, lists):
    heap = []
    for i, l in enumerate(lists):
        if l:
            heapq.heappush(heap, (l.val, i, l))
    dummy = ListNode(0)
    curr = dummy
    while heap:
        val, i, node = heapq.heappop(heap)
        curr.next = node
        curr = curr.next
        if node.next:
            heapq.heappush(heap, (node.next.val, i, node.next))
    return dummy.next
```

### Dry Run

```text
Input: lists = [[1,4,5],[1,3,4],[2,6]]

Initial heap (val, list_index):
  Push (1, 0), (1, 1), (2, 2)
  Heap: [(1,0), (1,1), (2,2)]

Iteration 1: Pop (1, 0) -> result: [1], push (4, 0)
  Heap: [(1,1), (2,2), (4,0)]

Iteration 2: Pop (1, 1) -> result: [1,1], push (3, 1)
  Heap: [(2,2), (3,1), (4,0)]

Iteration 3: Pop (2, 2) -> result: [1,1,2], push (6, 2)
  Heap: [(3,1), (4,0), (6,2)]

Iteration 4: Pop (3, 1) -> result: [1,1,2,3], push (4, 1)
  Heap: [(4,0), (4,1), (6,2)]

Iteration 5: Pop (4, 0) -> result: [1,1,2,3,4], push (5, 0)
  Heap: [(4,1), (5,0), (6,2)]

Iteration 6: Pop (4, 1) -> result: [1,1,2,3,4,4], list 1 exhausted
  Heap: [(5,0), (6,2)]

Iteration 7: Pop (5, 0) -> result: [1,1,2,3,4,4,5], list 0 exhausted
  Heap: [(6,2)]

Iteration 8: Pop (6, 2) -> result: [1,1,2,3,4,4,5,6], list 2 exhausted
  Heap: [] -> done

Result: [1, 1, 2, 3, 4, 4, 5, 6] ✅
```

---

## Approach 3: Divide and Conquer (Merge Sort Style)

### Alternative to Heap

> Instead of using a heap, we can apply merge sort's divide-and-conquer strategy. Pair up the lists and merge each pair. This halves the number of lists each round. After O(log k) rounds, only one merged list remains.

### Optimization idea

> **Pair-wise merging like merge sort!** In each round, merge lists[0] with lists[1], lists[2] with lists[3], etc. After one round, k lists become k/2 lists. After log(k) rounds, we have 1 list.
>
> Key insight:
> - Each round processes all N nodes exactly once -> O(N) per round
> - There are O(log k) rounds
> - Total: O(N log k) — same as heap, but with better constant factors (no heap overhead)

### Algorithm (step-by-step)

1. If lists is empty, return null
2. While there is more than one list:
   a. Create a new list of merged pairs
   b. For i = 0, 2, 4, ..., merge lists[i] and lists[i+1]
   c. If there's an odd one out, carry it over
   d. Replace lists with the merged results
3. Return the single remaining list

### Pseudocode

```text
function mergeKLists(lists):
    if lists is empty: return null

    while len(lists) > 1:
        merged = []
        for i = 0 to len(lists) - 1 step 2:
            l1 = lists[i]
            l2 = lists[i+1] if i+1 < len(lists) else null
            merged.append(mergeTwoLists(l1, l2))
        lists = merged

    return lists[0]

function mergeTwoLists(l1, l2):
    dummy = ListNode(0)
    curr = dummy
    while l1 != null and l2 != null:
        if l1.val <= l2.val:
            curr.next = l1
            l1 = l1.next
        else:
            curr.next = l2
            l2 = l2.next
        curr = curr.next
    curr.next = l1 if l1 != null else l2
    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(N log k) | O(log k) rounds, each processing all N nodes |
| **Space** | O(1) | In-place merging (ignoring recursion stack); O(log k) if using recursive merge |

### Implementation

#### Go

```go
func mergeKLists(lists []*ListNode) *ListNode {
    if len(lists) == 0 {
        return nil
    }
    for len(lists) > 1 {
        merged := []*ListNode{}
        for i := 0; i < len(lists); i += 2 {
            var l2 *ListNode
            if i+1 < len(lists) {
                l2 = lists[i+1]
            }
            merged = append(merged, mergeTwoLists(lists[i], l2))
        }
        lists = merged
    }
    return lists[0]
}

func mergeTwoLists(l1, l2 *ListNode) *ListNode {
    dummy := &ListNode{}
    curr := dummy
    for l1 != nil && l2 != nil {
        if l1.Val <= l2.Val {
            curr.Next = l1
            l1 = l1.Next
        } else {
            curr.Next = l2
            l2 = l2.Next
        }
        curr = curr.Next
    }
    if l1 != nil {
        curr.Next = l1
    } else {
        curr.Next = l2
    }
    return dummy.Next
}
```

#### Java

```java
public ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    List<ListNode> current = new ArrayList<>(Arrays.asList(lists));
    while (current.size() > 1) {
        List<ListNode> merged = new ArrayList<>();
        for (int i = 0; i < current.size(); i += 2) {
            ListNode l1 = current.get(i);
            ListNode l2 = (i + 1 < current.size()) ? current.get(i + 1) : null;
            merged.add(mergeTwoLists(l1, l2));
        }
        current = merged;
    }
    return current.get(0);
}

private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            curr.next = l1;
            l1 = l1.next;
        } else {
            curr.next = l2;
            l2 = l2.next;
        }
        curr = curr.next;
    }
    curr.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

#### Python

```python
def mergeKLists(self, lists):
    if not lists:
        return None
    while len(lists) > 1:
        merged = []
        for i in range(0, len(lists), 2):
            l1 = lists[i]
            l2 = lists[i + 1] if i + 1 < len(lists) else None
            merged.append(self.mergeTwoLists(l1, l2))
        lists = merged
    return lists[0]

def mergeTwoLists(self, l1, l2):
    dummy = ListNode(0)
    curr = dummy
    while l1 and l2:
        if l1.val <= l2.val:
            curr.next = l1
            l1 = l1.next
        else:
            curr.next = l2
            l2 = l2.next
        curr = curr.next
    curr.next = l1 if l1 else l2
    return dummy.next
```

### Dry Run

```text
Input: lists = [[1,4,5],[1,3,4],[2,6]]

Round 1 — Merge pairs:
  Merge lists[0]=[1,4,5] and lists[1]=[1,3,4]:
    Compare 1 vs 1 -> take 1 (list0), 1 (list1)
    Compare 4 vs 3 -> take 3 (list1)
    Compare 4 vs 4 -> take 4 (list0), 4 (list1)
    Remaining: 5 (list0)
    Merged: [1,1,3,4,4,5]
  lists[2]=[2,6] has no pair -> carry over
  After round 1: [[1,1,3,4,4,5], [2,6]]

Round 2 — Merge pairs:
  Merge [1,1,3,4,4,5] and [2,6]:
    1, 1, 2, 3, 4, 4, 5, 6
  After round 2: [[1,1,2,3,4,4,5,6]]

Result: [1, 1, 2, 3, 4, 4, 5, 6] ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force (Sort) | O(N log N) | O(N) | Simple, easy to implement | Ignores sorted property, extra memory |
| 2 | Min Heap / Priority Queue | O(N log k) | O(k) | Optimal time, works well for streaming | Heap overhead per node |
| 3 | Divide and Conquer | O(N log k) | O(1)* | Optimal time, in-place, no extra DS | Slightly more complex to implement |

*O(1) extra space for iterative version; O(log k) for recursive merge-two.

### Which solution to choose?

- **In an interview:** Approach 2 (Heap) or Approach 3 (D&C) — both O(N log k) and demonstrate different skills
- **In production:** Approach 3 — better cache locality, no heap overhead
- **On Leetcode:** Both Approach 2 and 3 pass easily
- **For learning:** Start with Approach 1 (build intuition), then Approach 2 (heap), then Approach 3 (D&C)

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty array | `lists = []` | `[]` | No lists to merge |
| 2 | Array with empty list | `lists = [[]]` | `[]` | Single empty list |
| 3 | Single list | `lists = [[1,2,3]]` | `[1,2,3]` | Nothing to merge |
| 4 | Multiple empty lists | `lists = [[], [], []]` | `[]` | All lists empty |
| 5 | Lists of different lengths | `lists = [[1],[2,3],[4,5,6]]` | `[1,2,3,4,5,6]` | Varying sizes |
| 6 | All same values | `lists = [[1,1],[1,1]]` | `[1,1,1,1]` | Duplicate handling |
| 7 | Negative values | `lists = [[-2,-1],[0,1]]` | `[-2,-1,0,1]` | Negative number support |

---

## Common Mistakes

### Mistake 1: Not handling empty lists in the input array

```python
# ❌ WRONG — crashes if lists[i] is None/empty
for l in lists:
    heapq.heappush(heap, (l.val, i, l))  # l could be None!

# ✅ CORRECT — check for non-empty lists
for i, l in enumerate(lists):
    if l:
        heapq.heappush(heap, (l.val, i, l))
```

### Mistake 2: Comparing ListNode objects directly in Python heap

```python
# ❌ WRONG — Python can't compare ListNode objects when values are equal
heapq.heappush(heap, (node.val, node))  # TypeError if two nodes have same val

# ✅ CORRECT — use a unique index as tiebreaker
heapq.heappush(heap, (node.val, unique_index, node))
```

**Why:** Python's `heapq` compares tuples element by element. If two nodes have the same value, it tries to compare the ListNode objects, which raises a TypeError.

### Mistake 3: Forgetting to advance to next node in heap approach

```python
# ❌ WRONG — keeps pushing the same node forever
node = heapq.heappop(heap)
heapq.heappush(heap, node)  # infinite loop!

# ✅ CORRECT — push node.next, not node itself
node = heapq.heappop(heap)
if node.next:
    heapq.heappush(heap, (node.next.val, i, node.next))
```

### Mistake 4: Not handling odd number of lists in divide and conquer

```python
# ❌ WRONG — index out of bounds when k is odd
for i in range(0, len(lists), 2):
    merged.append(mergeTwoLists(lists[i], lists[i+1]))  # fails if i+1 >= len

# ✅ CORRECT — handle the last unpaired list
for i in range(0, len(lists), 2):
    l1 = lists[i]
    l2 = lists[i+1] if i+1 < len(lists) else None
    merged.append(mergeTwoLists(l1, l2))
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [21. Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | :green_circle: Easy | Building block — merging two sorted lists |
| 2 | [148. Sort List](https://leetcode.com/problems/sort-list/) | :yellow_circle: Medium | Merge sort on linked list |
| 3 | [264. Ugly Number II](https://leetcode.com/problems/ugly-number-ii/) | :yellow_circle: Medium | Uses min heap to select next smallest |
| 4 | [378. Kth Smallest Element in a Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) | :yellow_circle: Medium | k-way merge / heap on sorted sequences |
| 5 | [355. Design Twitter](https://leetcode.com/problems/design-twitter/) | :yellow_circle: Medium | Merging k sorted feeds using heap |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Min Heap** tab — visualizes the priority queue selecting the minimum among k list heads
> - **Divide and Conquer** tab — shows pair-wise merging of lists in rounds
> - Preset examples, custom input, step/play/pause/reset controls
