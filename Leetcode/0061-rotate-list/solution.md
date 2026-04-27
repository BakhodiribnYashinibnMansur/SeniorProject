# 0061. Rotate List

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force (Rotate One at a Time)](#approach-1-brute-force-rotate-one-at-a-time)
4. [Approach 2: Find Length and Re-Link](#approach-2-find-length-and-re-link)
5. [Approach 3: Make Circular and Cut](#approach-3-make-circular-and-cut)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [61. Rotate List](https://leetcode.com/problems/rotate-list/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Linked List`, `Two Pointers` |

### Description

> Given the `head` of a linked list, rotate the list to the right by `k` places.

### Examples

```
Example 1:
Input: head = [1,2,3,4,5], k = 2
Output: [4,5,1,2,3]

Example 2:
Input: head = [0,1,2], k = 4
Output: [2,0,1]
```

### Constraints

- The number of nodes in the list is in the range `[0, 500]`.
- `-100 <= Node.val <= 100`
- `0 <= k <= 2 * 10^9`

---

## Problem Breakdown

### 1. What is being asked?

Cut the last `k` nodes off the end of the list and stick them at the front, repeated as a single rotation. Equivalently, find the new head at position `n - k % n` (0-indexed) from the front.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `head` | `ListNode` | Head of the linked list (may be `null`) |
| `k` | `int` | Number of rotations, `0 <= k <= 2*10^9` |

Important observations:
- `k` may be much larger than `n` -- always reduce by `k % n`
- If `n == 0` or `n == 1` or `k % n == 0`, no rotation is needed

### 3. What is the output?

The head of the rotated list.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 500` | O(n) is plenty fast |
| `k <= 2 * 10^9` | Don't loop k times directly -- mod by n first |

### 5. Step-by-step example analysis

#### Example 1: `head = [1,2,3,4,5], k = 2`

```text
Length n = 5. Effective k = 2 % 5 = 2.
New tail index from front: n - k - 1 = 5 - 2 - 1 = 2 (0-based) → node with value 3.

Steps:
  - Walk to node at index 2 (value 3). Call it newTail.
  - newHead = newTail.next  (value 4)
  - Walk to actual tail (value 5).
  - Tail.next = head        (value 1)
  - newTail.next = null

Result: 4 -> 5 -> 1 -> 2 -> 3 -> null
```

### 6. Key Observations

1. **Modulo reduction** -- `k = k % n` so we never rotate more than necessary.
2. **The cut is the only mutation** -- We need exactly two pointer rewrites: `tail.next = head`, `newTail.next = null`.
3. **Edge cases** -- Empty list, single node, or `k % n == 0` mean "do nothing".
4. **Two-pass is the cleanest** -- First pass finds length and tail; second pass walks to the new tail.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Two passes | Length + cut |
| Make-circular-and-cut | One pass through length, then re-walk |
| Brute force step-by-step | Move last node to front k times |

**Chosen pattern:** `Make Circular and Cut` (or equivalently the two-pass version).

---

## Approach 1: Brute Force (Rotate One at a Time)

### Thought process

> One rotation = unhook the last node and put it at the front. Repeat `k % n` times.

### Algorithm

1. Compute `n`. If `n <= 1` or `k % n == 0`, return head.
2. Reduce `k = k % n`.
3. Repeat `k` times:
   - Walk to the second-to-last node `prev`.
   - `prev.next.next = head`; `head = prev.next`; `prev.next = null`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n * k) |
| **Space** | O(1) |

> For `k` close to `n`, this is O(n^2). Listed for completeness.

---

## Approach 2: Find Length and Re-Link

### Idea

> Pass 1: walk the list, count `n`, remember the tail. Pass 2: walk to position `n - k - 1` (the new tail). Cut and reattach.

### Algorithm (step-by-step)

1. If `head` or `head.next` is null, return head.
2. Walk from `head` to count `n` and find `tail`.
3. `k %= n`. If `k == 0`, return head.
4. Walk `n - k - 1` steps from head to the new tail.
5. `newHead = newTail.next`; `newTail.next = null`; `tail.next = head`.
6. Return `newHead`.

### Pseudocode

```text
if head is null or head.next is null: return head
n = 1, tail = head
while tail.next is not null: tail = tail.next; n++
k = k % n
if k == 0: return head
newTail = head
for _ in 0..n-k-2: newTail = newTail.next
newHead = newTail.next
newTail.next = null
tail.next = head
return newHead
```

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) |

### Implementation

#### Go

```go
type ListNode struct {
    Val  int
    Next *ListNode
}

func rotateRight(head *ListNode, k int) *ListNode {
    if head == nil || head.Next == nil {
        return head
    }
    n, tail := 1, head
    for tail.Next != nil {
        tail = tail.Next
        n++
    }
    k %= n
    if k == 0 {
        return head
    }
    newTail := head
    for i := 0; i < n-k-1; i++ {
        newTail = newTail.Next
    }
    newHead := newTail.Next
    newTail.Next = nil
    tail.Next = head
    return newHead
}
```

#### Java

```java
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) return head;
        int n = 1;
        ListNode tail = head;
        while (tail.next != null) { tail = tail.next; n++; }
        k %= n;
        if (k == 0) return head;
        ListNode newTail = head;
        for (int i = 0; i < n - k - 1; i++) newTail = newTail.next;
        ListNode newHead = newTail.next;
        newTail.next = null;
        tail.next = head;
        return newHead;
    }
}
```

#### Python

```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

class Solution:
    def rotateRight(self, head: 'ListNode', k: int) -> 'ListNode':
        if not head or not head.next:
            return head
        n, tail = 1, head
        while tail.next:
            tail = tail.next
            n += 1
        k %= n
        if k == 0:
            return head
        new_tail = head
        for _ in range(n - k - 1):
            new_tail = new_tail.next
        new_head = new_tail.next
        new_tail.next = None
        tail.next = head
        return new_head
```

### Dry Run

```text
head = 1 → 2 → 3 → 4 → 5, k = 2

Pass 1: tail walks to 5. n = 5.
k = 2 % 5 = 2.
Pass 2: newTail walks n - k - 1 = 2 steps:
  newTail starts at 1, then 2, then 3 (after 2 steps).
newHead = newTail.next = 4.
newTail.next = null  → 1 → 2 → 3
tail.next = head     → 5 → 1 → 2 → 3
Final: 4 → 5 → 1 → 2 → 3 → null
```

---

## Approach 3: Make Circular and Cut

### Idea

> Connect the tail back to the head to form a cycle, then walk forward `n - k % n` steps from the head and cut. The node where we cut becomes the new tail; its next becomes the new head.

> Same big-O as Approach 2; some prefer the symmetry of "make-then-cut".

### Algorithm

1. If `head` is null, return null.
2. Compute `n` and link `tail.next = head` (cycle).
3. `k %= n`.
4. Walk `n - k - 1` steps from head to find new tail.
5. `newHead = newTail.next`; `newTail.next = null`.
6. Return `newHead`.

### Implementation

#### Python

```python
class Solution:
    def rotateRightCircular(self, head, k):
        if not head: return head
        n, tail = 1, head
        while tail.next:
            tail = tail.next; n += 1
        tail.next = head            # close cycle
        k %= n
        new_tail = head
        for _ in range(n - k - 1):
            new_tail = new_tail.next
        new_head = new_tail.next
        new_tail.next = None        # cut
        return new_head
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Rotate One | O(n * k) | O(1) | Trivial | TLE for large k near n |
| 2 | Length + Re-Link | O(n) | O(1) | Two-pass linear | Two passes |
| 3 | Make Circular + Cut | O(n) | O(1) | Symmetric | Same as 2 essentially |

### Which solution to choose?

- **In an interview:** Approach 2 -- canonical
- **In production:** Approach 2 or 3
- **On Leetcode:** Approach 2

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Empty list | `[], k=3` | `[]` | Nothing to rotate |
| 2 | Single node | `[5], k=10` | `[5]` | One node always returns itself |
| 3 | k = 0 | `[1,2,3], k=0` | `[1,2,3]` | No rotation |
| 4 | k = n | `[1,2,3], k=3` | `[1,2,3]` | Full circle |
| 5 | k > n | `[0,1,2], k=4` | `[2,0,1]` | Mod by n |
| 6 | Two nodes, k = 1 | `[1,2], k=1` | `[2,1]` | Swap |
| 7 | k = 2 * 10^9 | `[1,2,3], k=2_000_000_000` | depends on mod | Mod handles huge k |

---

## Common Mistakes

### Mistake 1: Rotating `k` times directly

```python
# WRONG — TLE when k is huge
for _ in range(k):
    rotate_one(head)

# CORRECT — reduce first
k %= n
```

**Reason:** `k` can be `2 * 10^9`. Reducing modulo `n` makes the work bounded by `n`.

### Mistake 2: Ignoring `k == 0` after modulo

```python
# WRONG — when k % n == 0, we walk to the wrong newTail
newTail = head
for _ in range(n - k - 1): ...   # n-k-1 = n-1, walks to actual tail
newHead = newTail.next            # which is null!

# CORRECT — early return
if k == 0: return head
```

**Reason:** When `k % n == 0`, the rotation is a no-op. Without the early return, the algorithm walks too far and dereferences `null`.

### Mistake 3: Forgetting to terminate the new tail

```python
# WRONG — leaves a cycle (or dangling old tail link)
tail.next = head
return new_head

# CORRECT — cut the new tail before reconnecting
new_tail.next = None
tail.next = head
return new_head
```

**Reason:** Without `new_tail.next = None`, traversing the result hits the old prefix again, causing infinite loops.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [189. Rotate Array](https://leetcode.com/problems/rotate-array/) | :yellow_circle: Medium | Same idea on arrays |
| 2 | [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | :red_circle: Hard | Linked-list pointer rewrites |
| 3 | [206. Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | :green_circle: Easy | Pointer manipulation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Linked-list rendering with pointer arrows
> - Step-by-step length count, mod-k reduction, walk to new tail
> - "Cut" animation showing the two pointer rewrites
> - Adjustable list length (1..10) and k slider
