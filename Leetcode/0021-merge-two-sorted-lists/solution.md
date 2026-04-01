# 0021. Merge Two Sorted Lists

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
| **Leetcode** | [21. Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Linked List`, `Recursion` |

### Description

> You are given the heads of two sorted linked lists `list1` and `list2`.
>
> Merge the two lists into one sorted list. The list should be made by splicing together the nodes of the first two lists.
>
> Return the head of the merged linked list.

### Examples

```
Example 1:
Input: list1 = [1,2,4], list2 = [1,3,4]
Output: [1,1,2,3,4,4]

Example 2:
Input: list1 = [], list2 = []
Output: []

Example 3:
Input: list1 = [], list2 = [0]
Output: [0]
```

### Constraints

- The number of nodes in both lists is in the range `[0, 50]`
- `-100 <= Node.val <= 100`
- Both `list1` and `list2` are sorted in **non-decreasing** order

---

## Problem Breakdown

### 1. What is being asked?

Merge two already-sorted linked lists into a single sorted linked list by rearranging (splicing) the existing nodes.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `list1` | `ListNode` | Head of first sorted linked list |
| `list2` | `ListNode` | Head of second sorted linked list |

Important observations about the input:
- Both lists are **already sorted** in non-decreasing order
- Either list can be **empty** (null)
- Both lists can be empty simultaneously
- Node values can be **negative** (-100 to 100)
- Lists can have **duplicate** values

### 3. What is the output?

- The **head** of the merged linked list
- The merged list must be sorted in non-decreasing order
- Must reuse existing nodes (splice), not create new ones

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `0 <= n, m <= 50` | Small input -- any approach works within time |
| `-100 <= val <= 100` | Simple integer comparisons |
| Both sorted | We can use a two-pointer / merge approach |

### 5. Step-by-step example analysis

#### Example 1: `list1 = [1,2,4], list2 = [1,3,4]`

```text
list1: 1 -> 2 -> 4
list2: 1 -> 3 -> 4

Compare 1 vs 1: pick list1's 1, advance list1
  merged: 1
Compare 2 vs 1: pick list2's 1, advance list2
  merged: 1 -> 1
Compare 2 vs 3: pick list1's 2, advance list1
  merged: 1 -> 1 -> 2
Compare 4 vs 3: pick list2's 3, advance list2
  merged: 1 -> 1 -> 2 -> 3
Compare 4 vs 4: pick list1's 4, advance list1
  merged: 1 -> 1 -> 2 -> 3 -> 4
list1 exhausted, append remaining list2: 4
  merged: 1 -> 1 -> 2 -> 3 -> 4 -> 4
```

#### Example 2: `list1 = [], list2 = [0]`

```text
list1 is empty -> return list2
  merged: 0
```

### 6. Key Observations

1. **Merge sort merge step** -- This is exactly the merge step of merge sort applied to linked lists.
2. **Two pointers** -- We compare the current nodes of both lists and pick the smaller one.
3. **Dummy head** -- Using a sentinel/dummy node simplifies edge case handling (avoids special-casing the first node).
4. **Remaining nodes** -- When one list is exhausted, append the rest of the other list directly.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Two pointers (merge) | Compare heads of both lists, pick smaller | This problem |
| Recursion | Subproblem is merging remaining lists | Elegant alternative |
| Dummy node | Avoids special-casing the first node | Standard linked list technique |

**Chosen pattern:** `Two pointers with dummy node` (iterative) and `Recursion`
**Reason:** The iterative approach is efficient and easy to follow; the recursive approach is elegant and builds intuition.

---

## Approach 1: Iterative

### Thought process

> Use a dummy node as the head of the result list and a `current` pointer to build the merged list.
> Compare the values at the heads of both lists. Attach the smaller node to `current` and advance that list's pointer.
> When one list is exhausted, attach the remaining nodes of the other list.

### Algorithm (step-by-step)

1. Create a dummy node (sentinel) to serve as the start of the merged list
2. Set `current` pointer to the dummy node
3. While both `list1` and `list2` are not null:
   - If `list1.val <= list2.val`: attach `list1` to `current.next`, advance `list1`
   - Else: attach `list2` to `current.next`, advance `list2`
   - Advance `current` to `current.next`
4. Attach the remaining non-null list to `current.next`
5. Return `dummy.next` (skip the dummy node)

### Pseudocode

```text
function mergeTwoLists(list1, list2):
    dummy = ListNode(0)
    current = dummy

    while list1 != null and list2 != null:
        if list1.val <= list2.val:
            current.next = list1
            list1 = list1.next
        else:
            current.next = list2
            list2 = list2.next
        current = current.next

    current.next = list1 if list1 != null else list2
    return dummy.next
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n + m) | Each node is visited exactly once, where n and m are the lengths of the two lists. |
| **Space** | O(1) | Only a constant number of pointers are used (no new nodes created). |

### Implementation

#### Go

```go
func mergeTwoLists(list1 *ListNode, list2 *ListNode) *ListNode {
    dummy := &ListNode{}
    current := dummy

    for list1 != nil && list2 != nil {
        if list1.Val <= list2.Val {
            current.Next = list1
            list1 = list1.Next
        } else {
            current.Next = list2
            list2 = list2.Next
        }
        current = current.Next
    }

    if list1 != nil {
        current.Next = list1
    } else {
        current.Next = list2
    }

    return dummy.Next
}
```

#### Java

```java
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        current.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }
}
```

#### Python

```python
class Solution:
    def mergeTwoLists(self, list1: Optional[ListNode], list2: Optional[ListNode]) -> Optional[ListNode]:
        dummy = ListNode(0)
        current = dummy

        while list1 and list2:
            if list1.val <= list2.val:
                current.next = list1
                list1 = list1.next
            else:
                current.next = list2
                list2 = list2.next
            current = current.next

        current.next = list1 if list1 else list2
        return dummy.next
```

### Dry Run

```text
Input: list1 = [1,2,4], list2 = [1,3,4]

dummy -> 0
current = dummy

Step 1: list1.val=1 <= list2.val=1 -> attach list1(1)
        dummy -> 1     list1 = [2,4]   list2 = [1,3,4]
        current = node(1)

Step 2: list1.val=2 > list2.val=1 -> attach list2(1)
        dummy -> 1 -> 1     list1 = [2,4]   list2 = [3,4]
        current = node(1)

Step 3: list1.val=2 <= list2.val=3 -> attach list1(2)
        dummy -> 1 -> 1 -> 2     list1 = [4]   list2 = [3,4]
        current = node(2)

Step 4: list1.val=4 > list2.val=3 -> attach list2(3)
        dummy -> 1 -> 1 -> 2 -> 3     list1 = [4]   list2 = [4]
        current = node(3)

Step 5: list1.val=4 <= list2.val=4 -> attach list1(4)
        dummy -> 1 -> 1 -> 2 -> 3 -> 4     list1 = null   list2 = [4]
        current = node(4)

list1 is null, attach remaining list2:
        dummy -> 1 -> 1 -> 2 -> 3 -> 4 -> 4

return dummy.next = [1, 1, 2, 3, 4, 4]
```

---

## Approach 2: Recursive

### Thought process

> The merge of two sorted lists can be defined recursively:
> - If one list is empty, return the other.
> - Otherwise, the head of the merged list is the smaller of the two heads.
> - The rest of the merged list is the merge of the remaining nodes.

### Algorithm (step-by-step)

1. Base cases:
   - If `list1` is null, return `list2`
   - If `list2` is null, return `list1`
2. If `list1.val <= list2.val`:
   - Set `list1.next` = recursive merge of `list1.next` and `list2`
   - Return `list1`
3. Else:
   - Set `list2.next` = recursive merge of `list1` and `list2.next`
   - Return `list2`

### Pseudocode

```text
function mergeTwoLists(list1, list2):
    if list1 is null: return list2
    if list2 is null: return list1

    if list1.val <= list2.val:
        list1.next = mergeTwoLists(list1.next, list2)
        return list1
    else:
        list2.next = mergeTwoLists(list1, list2.next)
        return list2
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n + m) | Each recursive call processes one node. Total calls = n + m. |
| **Space** | O(n + m) | Recursion stack depth is at most n + m in the worst case. |

### Implementation

#### Go

```go
func mergeTwoLists(list1 *ListNode, list2 *ListNode) *ListNode {
    if list1 == nil {
        return list2
    }
    if list2 == nil {
        return list1
    }

    if list1.Val <= list2.Val {
        list1.Next = mergeTwoLists(list1.Next, list2)
        return list1
    }
    list2.Next = mergeTwoLists(list1, list2.Next)
    return list2
}
```

#### Java

```java
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        if (list1 == null) return list2;
        if (list2 == null) return list1;

        if (list1.val <= list2.val) {
            list1.next = mergeTwoLists(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoLists(list1, list2.next);
            return list2;
        }
    }
}
```

#### Python

```python
class Solution:
    def mergeTwoLists(self, list1: Optional[ListNode], list2: Optional[ListNode]) -> Optional[ListNode]:
        if not list1:
            return list2
        if not list2:
            return list1

        if list1.val <= list2.val:
            list1.next = self.mergeTwoLists(list1.next, list2)
            return list1
        else:
            list2.next = self.mergeTwoLists(list1, list2.next)
            return list2
```

### Dry Run

```text
Input: list1 = [1,2,4], list2 = [1,3,4]

Call 1: list1=1, list2=1 -> 1 <= 1, pick list1(1)
        list1(1).next = merge([2,4], [1,3,4])

  Call 2: list1=2, list2=1 -> 2 > 1, pick list2(1)
          list2(1).next = merge([2,4], [3,4])

    Call 3: list1=2, list2=3 -> 2 <= 3, pick list1(2)
            list1(2).next = merge([4], [3,4])

      Call 4: list1=4, list2=3 -> 4 > 3, pick list2(3)
              list2(3).next = merge([4], [4])

        Call 5: list1=4, list2=4 -> 4 <= 4, pick list1(4)
                list1(4).next = merge(null, [4])

          Call 6: list1=null -> return list2(4)

                list1(4).next = 4 -> return list1(4): [4,4]
              list2(3).next = [4,4] -> return list2(3): [3,4,4]
            list1(2).next = [3,4,4] -> return list1(2): [2,3,4,4]
          list2(1).next = [2,3,4,4] -> return list2(1): [1,2,3,4,4]
        list1(1).next = [1,2,3,4,4] -> return list1(1): [1,1,2,3,4,4]

Result: [1, 1, 2, 3, 4, 4]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Iterative | O(n + m) | O(1) | Optimal space, no stack overflow risk | Slightly more code with dummy node |
| 2 | Recursive | O(n + m) | O(n + m) | Elegant, minimal code | Uses stack space, risk of overflow for very long lists |

### Which solution to choose?

- **In an interview:** Approach 1 (Iterative) -- demonstrates understanding of linked list manipulation with optimal space
- **In production:** Approach 1 -- O(1) space, no stack overflow risk
- **On Leetcode:** Both pass -- Approach 2 is shorter to write
- **For learning:** Both -- Approach 2 builds recursion intuition, Approach 1 teaches the dummy node pattern

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Both empty | `list1 = [], list2 = []` | `[]` | No nodes to merge |
| 2 | First empty | `list1 = [], list2 = [0]` | `[0]` | Return non-empty list |
| 3 | Second empty | `list1 = [1], list2 = []` | `[1]` | Return non-empty list |
| 4 | Equal elements | `list1 = [1,1], list2 = [1,1]` | `[1,1,1,1]` | All same values |
| 5 | Non-overlapping | `list1 = [1,2], list2 = [3,4]` | `[1,2,3,4]` | First list entirely smaller |
| 6 | Single elements | `list1 = [2], list2 = [1]` | `[1,2]` | Minimal non-empty case |
| 7 | Negative values | `list1 = [-3,-1], list2 = [-2,0]` | `[-3,-2,-1,0]` | Negative numbers |
| 8 | One much longer | `list1 = [1], list2 = [2,3,4,5]` | `[1,2,3,4,5]` | Different lengths |

---

## Common Mistakes

### Mistake 1: Forgetting to handle null/empty lists

```python
# WRONG -- crashes if list1 or list2 is None
while list1 and list2:
    ...
# forgets to attach remaining nodes
return dummy.next

# CORRECT -- attach remaining nodes after the loop
current.next = list1 if list1 else list2
return dummy.next
```

**Reason:** When one list is exhausted, the remaining nodes of the other list must be appended.

### Mistake 2: Creating new nodes instead of splicing

```python
# WRONG -- creates new nodes, wastes memory
if list1.val <= list2.val:
    current.next = ListNode(list1.val)  # unnecessary copy
    list1 = list1.next

# CORRECT -- reuse existing nodes
if list1.val <= list2.val:
    current.next = list1
    list1 = list1.next
```

**Reason:** The problem asks to splice the existing nodes, not create new ones.

### Mistake 3: Forgetting to advance the current pointer

```python
# WRONG -- current never moves, only the last node is attached
while list1 and list2:
    if list1.val <= list2.val:
        current.next = list1
        list1 = list1.next
    else:
        current.next = list2
        list2 = list2.next
    # missing: current = current.next

# CORRECT -- advance current after each attachment
    current = current.next
```

**Reason:** Without advancing `current`, each iteration overwrites the previous attachment.

### Mistake 4: Returning dummy instead of dummy.next

```python
# WRONG -- returns the dummy node with value 0
return dummy

# CORRECT -- skip the dummy sentinel
return dummy.next
```

**Reason:** The dummy node is a sentinel with an arbitrary value; the actual merged list starts at `dummy.next`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | :red_circle: Hard | Generalization to k lists |
| 2 | [88. Merge Sorted Array](https://leetcode.com/problems/merge-sorted-array/) | :green_circle: Easy | Same merge concept with arrays |
| 3 | [148. Sort List](https://leetcode.com/problems/sort-list/) | :yellow_circle: Medium | Merge sort on linked list uses this as subroutine |
| 4 | [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | :yellow_circle: Medium | Simultaneous linked list traversal |
| 5 | [86. Partition List](https://leetcode.com/problems/partition-list/) | :yellow_circle: Medium | Linked list rearrangement with dummy node |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Iterative** tab -- step-by-step merging with dummy node and two-pointer visualization
> - **Recursive** tab -- recursive call stack visualization showing how the merged list is built from base case up
