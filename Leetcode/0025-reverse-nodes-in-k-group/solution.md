# 0025. Reverse Nodes in k-Group

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
| **Leetcode** | [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Linked List`, `Recursion` |

### Description

> Given the `head` of a linked list, reverse the nodes of the list `k` at a time, and return the modified list.
>
> `k` is a positive integer and is less than or equal to the length of the linked list. If the number of nodes is not a multiple of `k` then left-out nodes, in the end, should remain as it is.
>
> You may not alter the values in the list's nodes, only nodes themselves may be changed.

### Examples

```
Example 1:
Input:  head = [1,2,3,4,5], k = 2
Output: [2,1,4,3,5]
Explanation: Reverse every 2 nodes: [1,2] -> [2,1], [3,4] -> [4,3], 5 remains.

Example 2:
Input:  head = [1,2,3,4,5], k = 3
Output: [3,2,1,4,5]
Explanation: Reverse first 3 nodes: [1,2,3] -> [3,2,1], remaining [4,5] has < 3 nodes so stays.

Example 3:
Input:  head = [1,2,3,4,5], k = 1
Output: [1,2,3,4,5]
Explanation: k=1 means no reversal needed.
```

### Constraints

- The number of nodes in the list is `n`
- `1 <= k <= n <= 5000`
- `0 <= Node.val <= 1000`

---

## Problem Breakdown

### 1. What is being asked?

Given a singly-linked list and an integer `k`, reverse every consecutive group of `k` nodes. If the last group has fewer than `k` nodes, leave them in their original order.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `head` | `ListNode` | Head of a singly-linked list |
| `k` | `int` | Size of each group to reverse |

Important observations about the input:
- `k` is always valid (1 <= k <= list length)
- If `k == 1`, the list remains unchanged
- If `k == n` (list length), the entire list is reversed
- The last group may have fewer than `k` nodes and should not be reversed

### 3. What is the output?

- The **head** of the modified linked list after reversing every k-group

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `1 <= k <= n <= 5000` | Moderate size — O(n) solution is required |
| `0 <= Node.val <= 1000` | Values don't affect the algorithm |
| Cannot alter node values | Must actually re-link nodes, not just swap values |

### 5. Step-by-step example analysis

#### Example 1: `head = [1,2,3,4,5]`, `k = 2` -> `[2,1,4,3,5]`

```text
Original list: 1 -> 2 -> 3 -> 4 -> 5 -> null

Group 1: [1, 2] -> reverse -> [2, 1]
Group 2: [3, 4] -> reverse -> [4, 3]
Group 3: [5]    -> fewer than k=2, leave as is

Result: 2 -> 1 -> 4 -> 3 -> 5 -> null
```

#### Example 2: `head = [1,2,3,4,5]`, `k = 3` -> `[3,2,1,4,5]`

```text
Original list: 1 -> 2 -> 3 -> 4 -> 5 -> null

Group 1: [1, 2, 3] -> reverse -> [3, 2, 1]
Group 2: [4, 5]    -> fewer than k=3, leave as is

Result: 3 -> 2 -> 1 -> 4 -> 5 -> null
```

### 6. Key Observations

1. **Group identification** — we need to check if there are at least `k` nodes remaining before reversing.
2. **In-place reversal** — each group of `k` nodes is reversed by relinking pointers, not swapping values.
3. **Connecting groups** — after reversing a group, the previous group's tail must point to the new group's head.
4. **Dummy head trick** — a sentinel node before `head` simplifies connecting the first reversed group.
5. **Tail becomes connector** — the first node of each group becomes the tail after reversal and connects to the next group.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Linked List Reversal | Core operation for each k-group | Reverse k nodes at a time |
| Dummy Head | Simplifies edge case when first group is reversed | Connect new head seamlessly |
| Iterative Group Processing | Process list in chunks of k | Walk through groups one at a time |
| Recursion | Natural fit — reverse one group, recurse on rest | Divide and conquer approach |

**Chosen patterns:** `Iterative Group Reversal` and `Recursive Group Reversal`
**Reason:** The iterative approach processes groups sequentially with O(1) extra space, while the recursive approach elegantly handles the "reverse one group, then solve the rest" decomposition.

---

## Approach 1: Iterative

### Thought process

> We process the list in chunks of `k` nodes. For each chunk:
> 1. First check if there are `k` nodes remaining — if not, we're done.
> 2. Reverse the `k` nodes using standard linked-list reversal.
> 3. Connect the reversed group to the previous group's tail.
> 4. Move to the next group.
>
> A dummy node before `head` makes it easy to handle the first group uniformly.

### Algorithm (step-by-step)

1. Create a dummy node pointing to `head`
2. Set `groupPrev = dummy` (the node before the current group)
3. Loop:
   a. Check if there are `k` nodes after `groupPrev` — if not, break
   b. Identify `groupStart` (first node of the group) and `groupEnd` (kth node)
   c. Save `groupNext = groupEnd.next` (first node after the group)
   d. Reverse the `k` nodes between `groupStart` and `groupEnd`
   e. Connect: `groupPrev.next = groupEnd` (new head of reversed group)
   f. Connect: `groupStart.next = groupNext` (tail of reversed group to next group)
   g. Update: `groupPrev = groupStart` (now the tail of the reversed group)
4. Return `dummy.next`

### Pseudocode

```text
function reverseKGroup(head, k):
    dummy = ListNode(0)
    dummy.next = head
    groupPrev = dummy

    while true:
        // Check if k nodes remain
        kth = getKthNode(groupPrev, k)
        if kth == null:
            break

        groupNext = kth.next

        // Reverse k nodes: groupPrev.next ... kth
        prev = kth.next
        curr = groupPrev.next
        while curr != groupNext:
            tmp = curr.next
            curr.next = prev
            prev = curr
            curr = tmp

        // Reconnect
        tmp = groupPrev.next    // original first node (now tail of group)
        groupPrev.next = kth    // new head of reversed group
        groupPrev = tmp         // move to end of reversed group

    return dummy.next

function getKthNode(node, k):
    while node != null and k > 0:
        node = node.next
        k--
    return node
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each node is visited at most twice (once to check, once to reverse) |
| **Space** | O(1) | Only constant extra pointers used |

### Implementation

#### Go

```go
func reverseKGroup(head *ListNode, k int) *ListNode {
    dummy := &ListNode{Next: head}
    groupPrev := dummy

    for {
        kth := getKthNode(groupPrev, k)
        if kth == nil {
            break
        }

        groupNext := kth.Next

        // Reverse k nodes
        prev := kth.Next
        curr := groupPrev.Next
        for curr != groupNext {
            tmp := curr.Next
            curr.Next = prev
            prev = curr
            curr = tmp
        }

        // Reconnect
        tmp := groupPrev.Next
        groupPrev.Next = kth
        groupPrev = tmp
    }

    return dummy.Next
}

func getKthNode(node *ListNode, k int) *ListNode {
    for node != nil && k > 0 {
        node = node.Next
        k--
    }
    return node
}
```

#### Java

```java
public ListNode reverseKGroup(ListNode head, int k) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode groupPrev = dummy;

    while (true) {
        ListNode kth = getKthNode(groupPrev, k);
        if (kth == null) break;

        ListNode groupNext = kth.next;

        // Reverse k nodes
        ListNode prev = kth.next;
        ListNode curr = groupPrev.next;
        while (curr != groupNext) {
            ListNode tmp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = tmp;
        }

        // Reconnect
        ListNode tmp = groupPrev.next;
        groupPrev.next = kth;
        groupPrev = tmp;
    }

    return dummy.next;
}

private ListNode getKthNode(ListNode node, int k) {
    while (node != null && k > 0) {
        node = node.next;
        k--;
    }
    return node;
}
```

#### Python

```python
def reverseKGroup(self, head, k):
    dummy = ListNode(0, head)
    groupPrev = dummy

    while True:
        kth = self.getKthNode(groupPrev, k)
        if not kth:
            break

        groupNext = kth.next

        # Reverse k nodes
        prev, curr = kth.next, groupPrev.next
        while curr != groupNext:
            tmp = curr.next
            curr.next = prev
            prev = curr
            curr = tmp

        # Reconnect
        tmp = groupPrev.next
        groupPrev.next = kth
        groupPrev = tmp

    return dummy.next

def getKthNode(self, node, k):
    while node and k > 0:
        node = node.next
        k -= 1
    return node
```

### Dry Run

```text
Input: head = [1,2,3,4,5], k = 2

Setup: dummy -> [1] -> [2] -> [3] -> [4] -> [5] -> null
       groupPrev = dummy

--- Iteration 1 ---
kth = getKthNode(dummy, 2) = node[2]
groupNext = node[3]

Reverse nodes 1,2 (prev starts at node[3]):
  curr=[1]: tmp=[2], [1].next=[3], prev=[1], curr=[2]
  curr=[2]: tmp=[3], [2].next=[1], prev=[2], curr=[3] (== groupNext, stop)

Reconnect:
  tmp = dummy.next = [1]  (original first, now tail)
  dummy.next = [2]        (kth is new head)
  groupPrev = [1]         (move to tail)

List: dummy -> [2] -> [1] -> [3] -> [4] -> [5] -> null
                       ^ groupPrev

--- Iteration 2 ---
kth = getKthNode([1], 2) = node[4]
groupNext = node[5]

Reverse nodes 3,4 (prev starts at node[5]):
  curr=[3]: tmp=[4], [3].next=[5], prev=[3], curr=[4]
  curr=[4]: tmp=[5], [4].next=[3], prev=[4], curr=[5] (== groupNext, stop)

Reconnect:
  tmp = [1].next = [3]   (original first, now tail)
  [1].next = [4]         (kth is new head)
  groupPrev = [3]        (move to tail)

List: dummy -> [2] -> [1] -> [4] -> [3] -> [5] -> null
                                      ^ groupPrev

--- Iteration 3 ---
kth = getKthNode([3], 2) = null (only 1 node left)
Break!

Result: [2] -> [1] -> [4] -> [3] -> [5] -> null = [2,1,4,3,5] ✅
```

---

## Approach 2: Recursive

### The problem with Iterative

> The iterative approach works well but the reconnection logic can be tricky to get right. Can we make the code cleaner?

### Optimization idea

> **Recursion!** The problem has a natural recursive structure:
> 1. Check if there are `k` nodes remaining — if not, return head as is.
> 2. Reverse the first `k` nodes.
> 3. Recursively call on the remaining list.
> 4. Connect the tail of the reversed group to the result of the recursive call.
>
> Key insight:
> - The first node of the group becomes the tail after reversal
> - The recursive call returns the new head of the rest of the list
> - The tail connects to the recursive result

### Algorithm (step-by-step)

1. Count `k` nodes forward from `head` — if fewer than `k` exist, return `head` unchanged
2. Reverse the first `k` nodes (standard reversal)
3. Recursively call `reverseKGroup` on the node after the k-group
4. Connect: `head.next = recursive result` (`head` is now the tail of the reversed group)
5. Return `newHead` (the last node of the original k-group, now the head)

### Pseudocode

```text
function reverseKGroup(head, k):
    // Check if k nodes exist
    node = head
    count = 0
    while node != null and count < k:
        node = node.next
        count++

    if count < k:
        return head  // not enough nodes, leave as is

    // Reverse first k nodes
    prev = null
    curr = head
    for i = 0 to k-1:
        next = curr.next
        curr.next = prev
        prev = curr
        curr = next

    // Recurse on remaining list and connect
    head.next = reverseKGroup(curr, k)

    return prev  // prev is now the head of the reversed group
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each node is visited at most twice |
| **Space** | O(n/k) | Recursion stack depth = number of groups |

### Implementation

#### Go

```go
func reverseKGroupRecursive(head *ListNode, k int) *ListNode {
    // Check if k nodes exist
    node := head
    count := 0
    for node != nil && count < k {
        node = node.Next
        count++
    }

    if count < k {
        return head
    }

    // Reverse first k nodes
    var prev *ListNode
    curr := head
    for i := 0; i < k; i++ {
        next := curr.Next
        curr.Next = prev
        prev = curr
        curr = next
    }

    // Recurse on remaining list and connect
    head.Next = reverseKGroupRecursive(curr, k)

    return prev
}
```

#### Java

```java
public ListNode reverseKGroupRecursive(ListNode head, int k) {
    // Check if k nodes exist
    ListNode node = head;
    int count = 0;
    while (node != null && count < k) {
        node = node.next;
        count++;
    }

    if (count < k) return head;

    // Reverse first k nodes
    ListNode prev = null;
    ListNode curr = head;
    for (int i = 0; i < k; i++) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }

    // Recurse on remaining list and connect
    head.next = reverseKGroupRecursive(curr, k);

    return prev;
}
```

#### Python

```python
def reverseKGroupRecursive(self, head, k):
    # Check if k nodes exist
    node = head
    count = 0
    while node and count < k:
        node = node.next
        count += 1

    if count < k:
        return head

    # Reverse first k nodes
    prev, curr = None, head
    for _ in range(k):
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt

    # Recurse on remaining list and connect
    head.next = self.reverseKGroupRecursive(curr, k)

    return prev
```

### Dry Run

```text
Input: head = [1,2,3,4,5], k = 3

--- Call 1: reverseKGroup([1,2,3,4,5], 3) ---
Check: count 3 nodes [1,2,3] -> count=3 >= k=3, proceed
Reverse [1,2,3]:
  [1].next=null, prev=[1], curr=[2]
  [2].next=[1], prev=[2], curr=[3]
  [3].next=[2], prev=[3], curr=[4]
Now: prev=[3]->[2]->[1]->null, curr=[4]
head=[1], head.next = reverseKGroup([4,5], 3)

  --- Call 2: reverseKGroup([4,5], 3) ---
  Check: count nodes [4,5] -> count=2 < k=3
  Return [4]->[5] unchanged

head=[1].next = [4]->[5]
Result: [3]->[2]->[1]->[4]->[5]->null

Return prev = [3]

Final: [3,2,1,4,5] ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Iterative | O(n) | O(1) | Constant extra space, no stack overflow risk | More complex reconnection logic |
| 2 | Recursive | O(n) | O(n/k) | Cleaner, more intuitive code | Uses recursion stack space |

### Which solution to choose?

- **In an interview:** Approach 2 (Recursive) — cleaner code, easier to explain the logic
- **In production:** Approach 1 (Iterative) — O(1) space, no risk of stack overflow on very long lists
- **On Leetcode:** Both pass — Approach 2 for cleaner code, Approach 1 for optimal space
- **For learning:** Approach 2 first (understand the recursion), then Approach 1 (master pointer manipulation)

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | k = 1, no reversal | `head=[1,2,3], k=1` | `[1,2,3]` | Groups of 1 need no reversal |
| 2 | k = list length | `head=[1,2,3], k=3` | `[3,2,1]` | Entire list reversed |
| 3 | Remainder left out | `head=[1,2,3,4,5], k=3` | `[3,2,1,4,5]` | Last 2 nodes unchanged |
| 4 | Single node | `head=[1], k=1` | `[1]` | Trivial case |
| 5 | Two nodes, k=2 | `head=[1,2], k=2` | `[2,1]` | Simple swap |
| 6 | Exact multiple | `head=[1,2,3,4], k=2` | `[2,1,4,3]` | All groups reversed, no remainder |
| 7 | k = n, longer list | `head=[1,2,3,4,5], k=5` | `[5,4,3,2,1]` | Full reversal |

---

## Common Mistakes

### Mistake 1: Not checking if k nodes exist before reversing

```python
# ❌ WRONG — reverses even when fewer than k nodes remain
prev, curr = None, head
for _ in range(k):
    nxt = curr.next  # curr could be None!
    curr.next = prev
    prev = curr
    curr = nxt

# ✅ CORRECT — check first, then reverse
node = head
count = 0
while node and count < k:
    node = node.next
    count += 1
if count < k:
    return head  # leave as is
```

**Example:** `head=[1,2], k=3` — without the check, we'd try to reverse 3 nodes but only 2 exist.

### Mistake 2: Losing the connection between groups

```python
# ❌ WRONG — after reversing a group, forgetting to connect it to the previous group
prev = None
curr = head
for _ in range(k):
    nxt = curr.next
    curr.next = prev
    prev = curr
    curr = nxt
# prev is new head, but how does the previous group's tail point to it?

# ✅ CORRECT — use groupPrev to track the tail of the previous group
groupPrev.next = kth  # connect previous tail to new head
groupPrev = tmp       # update to current tail
```

### Mistake 3: Incorrect reversal boundary

```python
# ❌ WRONG — reversing one too many or one too few nodes
prev = None
curr = groupStart
while curr:  # This reverses the entire remaining list!
    ...

# ✅ CORRECT — stop at the boundary
prev = groupNext  # Initialize prev to the node AFTER the group
curr = groupStart
while curr != groupNext:  # Stop at the group boundary
    ...
```

### Mistake 4: Off-by-one in getKthNode

```python
# ❌ WRONG — returns (k+1)th node instead of kth
def getKthNode(node, k):
    for _ in range(k):
        node = node.next
    return node.next  # One too far!

# ✅ CORRECT
def getKthNode(node, k):
    while node and k > 0:
        node = node.next
        k -= 1
    return node
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [24. Swap Nodes in Pairs](https://leetcode.com/problems/swap-nodes-in-pairs/) | :orange_circle: Medium | Special case of k=2 |
| 2 | [206. Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | :green_circle: Easy | Core sub-problem — reversing a linked list |
| 3 | [92. Reverse Linked List II](https://leetcode.com/problems/reverse-linked-list-ii/) | :orange_circle: Medium | Reverse a sub-section of a linked list |
| 4 | [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | :orange_circle: Medium | Linked list traversal and manipulation |
| 5 | [61. Rotate List](https://leetcode.com/problems/rotate-list/) | :orange_circle: Medium | Rearranging linked list nodes in groups |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Iterative** tab — visualizes group detection, reversal within each group, and reconnection
> - **Recursive** tab — shows recursive decomposition: reverse one group, recurse on the rest
> - Custom input for list values and k value
