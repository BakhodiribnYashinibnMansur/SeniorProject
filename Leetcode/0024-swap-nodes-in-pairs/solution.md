# 0024. Swap Nodes in Pairs

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Iterative](#approach-1-iterative)
4. [Approach 2: Recursive](#approach-2-recursive)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [24. Swap Nodes in Pairs](https://leetcode.com/problems/swap-nodes-in-pairs/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Linked List`, `Recursion` |

### Description

> Given a linked list, swap every two adjacent nodes and return its head. You must solve the problem without modifying the values in the list's nodes (i.e., only nodes themselves may be changed.)

### Examples

```
Example 1:
Input:  head = [1,2,3,4]
Output: [2,1,4,3]

Example 2:
Input:  head = []
Output: []

Example 3:
Input:  head = [1]
Output: [1]

Example 4:
Input:  head = [1,2,3]
Output: [2,1,3]
```

### Constraints

- The number of nodes in the list is in the range `[0, 100]`
- `0 <= Node.val <= 100`

---

## Problem Breakdown

### 1. What is being asked?

Given a singly linked list, swap every two adjacent nodes **by manipulating pointers** (not values). Return the new head of the modified list.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `head` | `ListNode` | Head of the singly linked list |

Important observations about the input:
- The list can be empty (0 nodes)
- The list can have an odd number of nodes — the last node stays in place
- We must not swap values, only rewire pointers

### 3. What is the output?

- The **head** of the modified linked list where every pair of adjacent nodes is swapped

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `0 <= nodes <= 100` | Small input; both iterative and recursive approaches are fine |
| `0 <= Node.val <= 100` | Values don't matter since we swap nodes, not values |

### 5. Step-by-step example analysis

#### Example 1: `head = [1,2,3,4]` → `[2,1,4,3]`

```text
Original:  1 → 2 → 3 → 4 → null

Swap pair (1,2):  2 → 1 → 3 → 4 → null
Swap pair (3,4):  2 → 1 → 4 → 3 → null

Result: [2, 1, 4, 3]
```

#### Example 4: `head = [1,2,3]` → `[2,1,3]`

```text
Original:  1 → 2 → 3 → null

Swap pair (1,2):  2 → 1 → 3 → null
Node 3 has no pair → stays in place

Result: [2, 1, 3]
```

### 6. Key Observations

1. **Pairs only** — we swap nodes in groups of two. If there's a leftover single node at the end, it stays.
2. **Pointer manipulation** — we cannot simply swap values; we must rewire `.next` pointers.
3. **Dummy node** — a sentinel node before the head simplifies handling the first pair.
4. **Three pointers needed per swap** — for each pair `(first, second)`, we also need a reference to the node before the pair (`prev`) to reconnect it.
5. **Recursive structure** — swapping a pair and then solving the rest of the list is a natural recursive decomposition.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Linked List Pointer Manipulation | Rewire .next pointers to swap adjacent nodes | Swap Nodes in Pairs (this problem) |
| Dummy Head | Simplifies the edge case of swapping the first pair | Merge Two Sorted Lists, Remove Nth Node |
| Recursion | Swap one pair, then recurse on the rest | Reverse Nodes in k-Group |

**Chosen patterns:** `Iterative pointer manipulation` and `Recursion`
**Reason:** Both are clean and efficient; the iterative approach is more space-efficient, while the recursive approach is more elegant.

---

## Approach 1: Iterative

### Thought process

> Use a dummy node before the head. For each pair of nodes, rewire the pointers:
> 1. `prev` points to the node before the current pair
> 2. `first` is the first node of the pair
> 3. `second` is the second node of the pair
> 
> After swapping: `prev → second → first → (rest of list)`
> Then advance `prev` to `first` (which is now second in the pair) and repeat.

### Algorithm (step-by-step)

1. Create a dummy node and set `dummy.next = head`
2. Set `prev = dummy`
3. While `prev.next != null` AND `prev.next.next != null`:
   a. `first = prev.next`
   b. `second = prev.next.next`
   c. Rewire: `first.next = second.next` (first now points past the pair)
   d. Rewire: `second.next = first` (second now points to first)
   e. Rewire: `prev.next = second` (prev now points to second, the new front of the pair)
   f. Advance: `prev = first` (move past the swapped pair)
4. Return `dummy.next`

### Pseudocode

```text
function swapPairs(head):
    dummy = ListNode(0)
    dummy.next = head
    prev = dummy

    while prev.next != null AND prev.next.next != null:
        first  = prev.next
        second = prev.next.next

        // Rewire
        first.next  = second.next
        second.next = first
        prev.next   = second

        // Advance
        prev = first

    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the list, processing two nodes at a time |
| **Space** | O(1) | Only a constant number of pointers used |

### Implementation

#### Go

```go
// swapPairs — Iterative approach with dummy node
// Time: O(n), Space: O(1)
func swapPairs(head *ListNode) *ListNode {
    dummy := &ListNode{Next: head}
    prev := dummy

    for prev.Next != nil && prev.Next.Next != nil {
        first := prev.Next
        second := prev.Next.Next

        // Rewire
        first.Next = second.Next
        second.Next = first
        prev.Next = second

        // Advance past the swapped pair
        prev = first
    }

    return dummy.Next
}
```

#### Java

```java
// swapPairs — Iterative approach with dummy node
// Time: O(n), Space: O(1)
public ListNode swapPairs(ListNode head) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;

    while (prev.next != null && prev.next.next != null) {
        ListNode first = prev.next;
        ListNode second = prev.next.next;

        // Rewire
        first.next = second.next;
        second.next = first;
        prev.next = second;

        // Advance past the swapped pair
        prev = first;
    }

    return dummy.next;
}
```

#### Python

```python
def swapPairs(self, head):
    """
    Iterative approach with dummy node
    Time: O(n), Space: O(1)
    """
    dummy = ListNode(0)
    dummy.next = head
    prev = dummy

    while prev.next and prev.next.next:
        first = prev.next
        second = prev.next.next

        # Rewire
        first.next = second.next
        second.next = first
        prev.next = second

        # Advance past the swapped pair
        prev = first

    return dummy.next
```

### Dry Run

```text
Input: head = [1, 2, 3, 4]

Initial state:
  dummy → 1 → 2 → 3 → 4 → null
  prev = dummy

Step 1: first = 1, second = 2
  Rewire:
    1.next = 2.next = 3       →  1 → 3
    2.next = 1                 →  2 → 1 → 3
    prev.next = 2              →  dummy → 2 → 1 → 3 → 4 → null
  Advance: prev = 1
  State: dummy → 2 → 1 → 3 → 4 → null
                       ^ prev

Step 2: first = 3, second = 4
  Rewire:
    3.next = 4.next = null     →  3 → null
    4.next = 3                 →  4 → 3 → null
    prev.next = 4              →  1 → 4 → 3 → null
  Advance: prev = 3
  State: dummy → 2 → 1 → 4 → 3 → null
                              ^ prev

prev.next = null → STOP

Return dummy.next = [2, 1, 4, 3] ✅
```

```text
Input: head = [1, 2, 3]

Initial: dummy → 1 → 2 → 3 → null, prev = dummy

Step 1: first = 1, second = 2
  1.next = 3, 2.next = 1, prev.next = 2
  State: dummy → 2 → 1 → 3 → null
  prev = 1

prev.next = 3, prev.next.next = null → STOP (odd node stays)

Return: [2, 1, 3] ✅
```

---

## Approach 2: Recursive

### Thought process

> The recursive approach breaks the problem into a subproblem:
> 1. If there are fewer than 2 nodes, return head (base case)
> 2. Take the first two nodes (`first` and `second`)
> 3. Recursively swap the rest of the list starting from the third node
> 4. Rewire: `first.next = recursiveResult`, `second.next = first`
> 5. Return `second` (the new head of this pair)

### Algorithm (step-by-step)

1. **Base case:** if `head == null` or `head.next == null`, return `head`
2. `first = head`, `second = head.next`
3. `rest = swapPairs(second.next)` — recursively swap the remaining list
4. `first.next = rest` — first now points to the swapped rest
5. `second.next = first` — second now points to first
6. Return `second` — second is the new head of this swapped pair

### Pseudocode

```text
function swapPairs(head):
    if head == null OR head.next == null:
        return head

    first  = head
    second = head.next

    // Recurse on the rest of the list
    first.next = swapPairs(second.next)

    // Swap the pair
    second.next = first

    return second   // second is now the head of this pair
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each node is visited once |
| **Space** | O(n) | Recursion stack depth is n/2, which is O(n) |

### Implementation

#### Go

```go
// swapPairsRecursive — Recursive approach
// Time: O(n), Space: O(n) due to recursion stack
func swapPairsRecursive(head *ListNode) *ListNode {
    // Base case: 0 or 1 node left
    if head == nil || head.Next == nil {
        return head
    }

    first := head
    second := head.Next

    // Recurse on the rest, then rewire
    first.Next = swapPairsRecursive(second.Next)
    second.Next = first

    return second
}
```

#### Java

```java
// swapPairsRecursive — Recursive approach
// Time: O(n), Space: O(n) due to recursion stack
public ListNode swapPairsRecursive(ListNode head) {
    // Base case: 0 or 1 node left
    if (head == null || head.next == null) {
        return head;
    }

    ListNode first = head;
    ListNode second = head.next;

    // Recurse on the rest, then rewire
    first.next = swapPairsRecursive(second.next);
    second.next = first;

    return second;
}
```

#### Python

```python
def swapPairsRecursive(self, head):
    """
    Recursive approach
    Time: O(n), Space: O(n) due to recursion stack
    """
    # Base case: 0 or 1 node left
    if not head or not head.next:
        return head

    first = head
    second = head.next

    # Recurse on the rest, then rewire
    first.next = self.swapPairsRecursive(second.next)
    second.next = first

    return second
```

### Dry Run

```text
Input: head = [1, 2, 3, 4]

Call 1: swapPairs(1→2→3→4)
  first = 1, second = 2
  Recurse on 3→4...

  Call 2: swapPairs(3→4)
    first = 3, second = 4
    Recurse on null...

    Call 3: swapPairs(null)
      Base case: return null

    Back in Call 2:
      first.next = null  →  3 → null
      second.next = 3    →  4 → 3 → null
      return 4 (head of this pair)

  Back in Call 1:
    first.next = 4→3→null  →  1 → 4 → 3 → null
    second.next = 1         →  2 → 1 → 4 → 3 → null
    return 2 (head of this pair)

Result: [2, 1, 4, 3] ✅
```

```text
Input: head = [1, 2, 3]

Call 1: swapPairs(1→2→3)
  first = 1, second = 2
  Recurse on 3...

  Call 2: swapPairs(3)
    Base case: head.next == null → return 3

  Back in Call 1:
    first.next = 3  →  1 → 3
    second.next = 1 →  2 → 1 → 3
    return 2

Result: [2, 1, 3] ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Iterative | O(n) | O(1) | Constant extra space | More pointer manipulation to track |
| 2 | Recursive | O(n) | O(n) | Elegant, easier to understand | Uses O(n) stack space |

### Which solution to choose?

- **In an interview:** Approach 1 (Iterative) — shows mastery of pointer manipulation
- **In production:** Approach 1 — O(1) space is preferred for large lists
- **On Leetcode:** Either works — both are accepted
- **For learning:** Start with Approach 2 (Recursive) to build intuition, then implement Approach 1

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty list | `[]` | `[]` | Nothing to swap |
| 2 | Single node | `[1]` | `[1]` | No pair to swap |
| 3 | Two nodes | `[1,2]` | `[2,1]` | Exactly one pair |
| 4 | Even number of nodes | `[1,2,3,4]` | `[2,1,4,3]` | All nodes are paired |
| 5 | Odd number of nodes | `[1,2,3]` | `[2,1,3]` | Last node has no pair |
| 6 | All same values | `[5,5,5,5]` | `[5,5,5,5]` | Nodes are swapped even though values look the same |
| 7 | Long list | `[1,2,3,4,5,6,7,8]` | `[2,1,4,3,6,5,8,7]` | Multiple pairs |

---

## Common Mistakes

### Mistake 1: Forgetting to use a dummy node

```python
# ❌ WRONG — special-casing the first pair is error-prone
if head and head.next:
    new_head = head.next
    head.next = head.next.next
    new_head.next = head
    # ... now how do we connect to the next pair?

# ✅ CORRECT — dummy node handles the first pair uniformly
dummy = ListNode(0)
dummy.next = head
prev = dummy
```

### Mistake 2: Losing the rest of the list during swap

```python
# ❌ WRONG — overwrites second.next before saving it
second.next = first        # rest of list is lost!
first.next = second.next   # this now points to first itself!

# ✅ CORRECT — save or rewire in the right order
first.next = second.next   # first points past the pair
second.next = first        # second points to first
prev.next = second         # prev points to second
```

### Mistake 3: Swapping values instead of nodes

```python
# ❌ WRONG — problem explicitly forbids modifying values
first.val, second.val = second.val, first.val

# ✅ CORRECT — swap by rewiring pointers
first.next = second.next
second.next = first
prev.next = second
```

### Mistake 4: Incorrect advance of `prev`

```python
# ❌ WRONG — prev advances to second (which is now the head of pair)
prev = second  # This skips ahead incorrectly

# ✅ CORRECT — prev advances to first (which is now the tail of the swapped pair)
prev = first
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | :red_circle: Hard | Generalization — swap in groups of k instead of 2 |
| 2 | [206. Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | :green_circle: Easy | Pointer manipulation on a linked list |
| 3 | [92. Reverse Linked List II](https://leetcode.com/problems/reverse-linked-list-ii/) | :yellow_circle: Medium | Reversing a sub-portion of the list |
| 4 | [1721. Swapping Nodes in a Linked List](https://leetcode.com/problems/swapping-nodes-in-a-linked-list/) | :yellow_circle: Medium | Swapping specific nodes (kth from start and end) |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Iterative** tab — visualizes the step-by-step pointer rewiring with prev, first, and second
> - **Recursive** tab — shows the recursion call stack and how pairs are swapped on the way back up
> - Step/Play/Pause/Reset controls with speed adjustment
> - Preset examples for common test cases
