# 0019. Remove Nth Node From End of List

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Two-Pass (Count Length, Then Remove)](#approach-1-two-pass-count-length-then-remove)
4. [Approach 2: One-Pass Two Pointers (Optimal)](#approach-2-one-pass-two-pointers-optimal)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [19. Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `Linked List`, `Two Pointers` |

### Description

> Given the `head` of a linked list, remove the `nth` node from the end of the list and return its head.

### Examples

```
Example 1:
Input:  head = [1,2,3,4,5], n = 2
Output: [1,2,3,5]
Explanation: Remove the 2nd node from the end (node with value 4).

Example 2:
Input:  head = [1], n = 1
Output: []
Explanation: The only node is removed, resulting in an empty list.

Example 3:
Input:  head = [1,2], n = 1
Output: [1]
Explanation: Remove the last node (value 2).
```

### Constraints

- The number of nodes in the list is `sz`
- `1 <= sz <= 30`
- `0 <= Node.val <= 100`
- `1 <= n <= sz`

---

## Problem Breakdown

### 1. What is being asked?

Given a singly-linked list and an integer `n`, remove the `n`th node from the **end** of the list and return the modified list's head. The challenge is that we cannot directly index from the end in a singly-linked list.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `head` | `ListNode` | Head of a singly-linked list |
| `n` | `int` | Position from the end of the node to remove (1-indexed) |

Important observations about the input:
- `n` is always valid (1 <= n <= list length), so we never need to handle invalid `n`
- The list has at least 1 node
- We may need to remove the head node itself (when n equals the list length)

### 3. What is the output?

- The **head** of the modified linked list after removing the target node
- If the list becomes empty (single node removed), return `null`/`nil`/`None`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `1 <= sz <= 30` | Small list — both O(L) approaches are fine |
| `1 <= n <= sz` | `n` is always valid — no need for bounds checking |
| `0 <= Node.val <= 100` | Values don't affect the algorithm |

### 5. Step-by-step example analysis

#### Example 1: `head = [1,2,3,4,5]`, `n = 2` → `[1,2,3,5]`

```text
Original list: 1 → 2 → 3 → 4 → 5 → null

The 2nd node from the end is node with value 4.
(Counting from end: 5 is 1st, 4 is 2nd, 3 is 3rd, ...)

Remove node 4 by linking node 3 directly to node 5:
1 → 2 → 3 → 5 → null

Result: [1, 2, 3, 5]
```

#### Example 2: `head = [1]`, `n = 1` → `[]`

```text
Original list: 1 → null

The 1st node from the end is node with value 1 (the only node).
Remove it → empty list.

Result: []
```

### 6. Key Observations

1. **Counting from the end** — the nth node from end is the (length - n + 1)th node from the start.
2. **Dummy head trick** — using a sentinel node before head avoids special-casing removal of the head node.
3. **Two-pointer gap** — if we maintain a gap of exactly `n` nodes between two pointers, when the fast pointer reaches the end, the slow pointer is right before the target.
4. **Single-pass possible** — the two-pointer technique lets us solve this in one traversal.
5. **Deletion in linked list** — to remove a node, we need a pointer to the node **before** it, so we can set `prev.next = prev.next.next`.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Two Pointers (Fast & Slow) | Maintain fixed gap to find nth-from-end | This problem |
| Dummy Head | Simplifies edge case when head is removed | Remove Nth Node (this problem) |
| Linked List Traversal | Fundamental operation for counting or positioning | All linked list problems |

**Chosen pattern:** `Two Pointers with fixed gap`
**Reason:** By advancing the fast pointer `n` steps first, the gap ensures that when fast reaches the end, slow is positioned exactly before the node to remove — all in one pass.

---

## Approach 1: Two-Pass (Count Length, Then Remove)

### Thought process

> The nth node from the end is the (L - n + 1)th node from the beginning, where L is the list length.
> First pass: count L. Second pass: walk to position (L - n) to find the node just before the target.

### Algorithm (step-by-step)

1. Create a dummy node pointing to `head`
2. **First pass:** traverse the entire list to count its length `L`
3. **Second pass:** starting from dummy, advance `L - n` steps to reach the node just before the target
4. Skip the target node: `curr.next = curr.next.next`
5. Return `dummy.next`

### Pseudocode

```text
function removeNthFromEnd(head, n):
    dummy = ListNode(0)
    dummy.next = head

    // First pass: count length
    length = 0
    curr = head
    while curr != null:
        length++
        curr = curr.next

    // Second pass: find node before target
    curr = dummy
    for i = 0 to length - n - 1:
        curr = curr.next

    // Remove target
    curr.next = curr.next.next

    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(L) | Two passes: one to count, one to find the target |
| **Space** | O(1) | Only a few extra pointers |

### Implementation

#### Go

```go
func removeNthFromEndTwoPass(head *ListNode, n int) *ListNode {
    dummy := &ListNode{Next: head}

    length := 0
    curr := head
    for curr != nil {
        length++
        curr = curr.Next
    }

    curr = dummy
    for i := 0; i < length-n; i++ {
        curr = curr.Next
    }

    curr.Next = curr.Next.Next
    return dummy.Next
}
```

#### Java

```java
public ListNode removeNthFromEndTwoPass(ListNode head, int n) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;

    int length = 0;
    ListNode curr = head;
    while (curr != null) {
        length++;
        curr = curr.next;
    }

    curr = dummy;
    for (int i = 0; i < length - n; i++) {
        curr = curr.next;
    }

    curr.next = curr.next.next;
    return dummy.next;
}
```

#### Python

```python
def removeNthFromEndTwoPass(self, head, n):
    dummy = ListNode(0, head)

    length = 0
    curr = head
    while curr:
        length += 1
        curr = curr.next

    curr = dummy
    for _ in range(length - n):
        curr = curr.next

    curr.next = curr.next.next
    return dummy.next
```

### Dry Run

```text
Input: head = [1,2,3,4,5], n = 2

First pass — count length:
  1 → 2 → 3 → 4 → 5 → null
  length = 5

Second pass — advance (5 - 2) = 3 steps from dummy:
  dummy → [1] → [2] → [3]    (curr is now at node 3)

Remove: curr.next = curr.next.next
  node 3's next changes from [4] to [5]

Result: dummy.next = [1] → [2] → [3] → [5] → null = [1, 2, 3, 5] ✅
```

---

## Approach 2: One-Pass Two Pointers (Optimal)

### The problem with Two-Pass

> Two-pass works, but requires traversing the list twice. Can we do it in a single pass?

### Optimization idea

> **Two-pointer gap technique!** Place two pointers starting at a dummy node. Advance the `fast` pointer `n + 1` steps ahead. Now the gap between `fast` and `slow` is exactly `n + 1`.
>
> Move both pointers forward one step at a time. When `fast` reaches `null` (end of list), `slow` is exactly at the node **before** the target node.
>
> Key insight:
> - Gap of `n + 1` ensures `slow` stops one position before the nth-from-end node
> - Only one traversal needed

### Algorithm (step-by-step)

1. Create a dummy node pointing to `head`
2. Set both `fast` and `slow` to dummy
3. Advance `fast` by `n + 1` steps
4. Move both `fast` and `slow` forward simultaneously until `fast == null`
5. `slow` is now just before the target node — remove it: `slow.next = slow.next.next`
6. Return `dummy.next`

### Pseudocode

```text
function removeNthFromEnd(head, n):
    dummy = ListNode(0)
    dummy.next = head
    fast = dummy
    slow = dummy

    // Advance fast n+1 steps
    for i = 0 to n:
        fast = fast.next

    // Move both until fast reaches end
    while fast != null:
        fast = fast.next
        slow = slow.next

    // Remove the target node
    slow.next = slow.next.next

    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(L) | Single pass through the list |
| **Space** | O(1) | Only two extra pointers |

### Implementation

#### Go

```go
func removeNthFromEnd(head *ListNode, n int) *ListNode {
    dummy := &ListNode{Next: head}
    fast := dummy
    slow := dummy

    for i := 0; i <= n; i++ {
        fast = fast.Next
    }

    for fast != nil {
        fast = fast.Next
        slow = slow.Next
    }

    slow.Next = slow.Next.Next
    return dummy.Next
}
```

#### Java

```java
public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode fast = dummy;
    ListNode slow = dummy;

    for (int i = 0; i <= n; i++) {
        fast = fast.next;
    }

    while (fast != null) {
        fast = fast.next;
        slow = slow.next;
    }

    slow.next = slow.next.next;
    return dummy.next;
}
```

#### Python

```python
def removeNthFromEnd(self, head, n):
    dummy = ListNode(0, head)
    fast = dummy
    slow = dummy

    for _ in range(n + 1):
        fast = fast.next

    while fast:
        fast = fast.next
        slow = slow.next

    slow.next = slow.next.next
    return dummy.next
```

### Dry Run

```text
Input: head = [1,2,3,4,5], n = 2

Setup: dummy → [1] → [2] → [3] → [4] → [5] → null
       fast = dummy, slow = dummy

Step 1 — Advance fast n+1 = 3 steps:
  fast: dummy → [1] → [2] → [3]
  (fast is now at node 3)

Step 2 — Move both until fast is null:
  Iteration 1: fast → [4], slow → [1]
  Iteration 2: fast → [5], slow → [2]
  Iteration 3: fast → null, slow → [3]   ← STOP

Step 3 — Remove: slow.next = slow.next.next
  Node [3].next changes from [4] to [5]

Result: [1] → [2] → [3] → [5] → null = [1, 2, 3, 5] ✅
```

```text
Input: head = [1], n = 1

Setup: dummy → [1] → null
       fast = dummy, slow = dummy

Advance fast 2 steps:
  fast: dummy → [1] → null   (fast is null)

While loop does not execute (fast is already null).

Remove: slow.next = slow.next.next
  dummy.next changes from [1] to null

Result: null = [] ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Two-Pass (Count Length) | O(L) | O(1) | Simple logic, easy to understand | Requires two traversals |
| 2 | One-Pass Two Pointers | O(L) | O(1) | Single traversal, elegant | Slightly more nuanced pointer logic |

### Which solution to choose?

- **In an interview:** Approach 2 — demonstrates understanding of the two-pointer technique and one-pass optimization
- **In production:** Both are equally valid since the time complexity is the same
- **On Leetcode:** Approach 2 — the follow-up explicitly asks "Could you do this in one pass?"
- **For learning:** Approach 1 first (build intuition), then Approach 2

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single node, remove it | `head=[1], n=1` | `[]` | List becomes empty |
| 2 | Remove the head node | `head=[1,2], n=2` | `[2]` | Head removal needs dummy node |
| 3 | Remove the tail node | `head=[1,2], n=1` | `[1]` | Last node removal |
| 4 | Remove from middle | `head=[1,2,3,4,5], n=3` | `[1,2,4,5]` | Middle node (value 3) removed |
| 5 | Remove 2nd from end | `head=[1,2,3,4,5], n=2` | `[1,2,3,5]` | Classic example |
| 6 | Remove first (n = length) | `head=[1,2,3,4,5], n=5` | `[2,3,4,5]` | Same as removing head |
| 7 | Two elements, remove last | `head=[1,2], n=1` | `[1]` | Minimal list, tail removal |

---

## Common Mistakes

### Mistake 1: Not using a dummy node

```python
# ❌ WRONG — fails when removing the head node
fast = head
slow = head
# ... if n == length, slow never advances, and we can't easily remove head

# ✅ CORRECT — dummy node before head handles all cases uniformly
dummy = ListNode(0)
dummy.next = head
fast = dummy
slow = dummy
```

**Example:** `head=[1], n=1` — without a dummy, there's no node before the head to update its `next` pointer.

### Mistake 2: Wrong gap size (off-by-one)

```python
# ❌ WRONG — advancing fast by n steps puts slow AT the target, not before it
for _ in range(n):
    fast = fast.next

# ✅ CORRECT — advance n+1 steps so slow lands one node BEFORE the target
for _ in range(n + 1):
    fast = fast.next
```

**Why:** We need `slow` to be the **predecessor** of the target node to perform the deletion `slow.next = slow.next.next`.

### Mistake 3: Forgetting to return dummy.next instead of head

```python
# ❌ WRONG — if head was removed, this returns the deleted node
return head

# ✅ CORRECT — dummy.next always points to the true head
return dummy.next
```

**Example:** `head=[1,2], n=2` — head (value 1) is removed. `dummy.next` is now `[2]`, but `head` still points to the removed node `[1]`.

### Mistake 4: Not handling single-node list

```python
# ❌ WRONG — assumes there's always a next node
slow.next = slow.next.next  # crashes if slow.next.next is null? No — this is fine!

# Actually, slow.next.next being null is perfectly valid.
# Setting slow.next = null effectively removes the last node.
# The real danger is if slow.next is null (which can't happen with valid n).
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [876. Middle of the Linked List](https://leetcode.com/problems/middle-of-the-linked-list/) | 🟢 Easy | Two-pointer technique on linked list (fast moves 2x) |
| 2 | [141. Linked List Cycle](https://leetcode.com/problems/linked-list-cycle/) | 🟢 Easy | Fast/slow pointer pattern |
| 3 | [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | 🟡 Medium | Linked list traversal and manipulation |
| 4 | [203. Remove Linked List Elements](https://leetcode.com/problems/remove-linked-list-elements/) | 🟢 Easy | Node removal with dummy head technique |
| 5 | [237. Delete Node in a Linked List](https://leetcode.com/problems/delete-node-in-a-linked-list/) | 🟡 Medium | Different approach to node deletion |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Two-Pass** tab — visualizes counting the length, then finding and removing the target node
> - **One-Pass Two Pointers** tab — shows fast pointer advancing n+1 steps ahead, then both moving simultaneously until fast reaches the end
> - Custom input for list values and n value
