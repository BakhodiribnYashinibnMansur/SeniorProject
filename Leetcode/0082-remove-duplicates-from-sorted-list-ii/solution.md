# 0082. Remove Duplicates from Sorted List II

## Problem

| | |
|---|---|
| **Leetcode** | [82. Remove Duplicates from Sorted List II](https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Linked List`, `Two Pointers` |

> Given the `head` of a sorted linked list, *delete all nodes that have duplicate numbers, leaving only distinct numbers from the original list*. Return *the linked list **sorted** as well*.

### Examples

```
Input: head = [1,2,3,3,4,4,5]
Output: [1,2,5]

Input: head = [1,1,1,2,3]
Output: [2,3]
```

### Constraints

- `0 <= number of nodes <= 300`
- `-100 <= Node.val <= 100`
- The list is guaranteed to be **sorted in ascending order**.

---

## Approach 1: Dummy Head + Two Pointers

### Idea

Use a sentinel `dummy` before the head. Walk with `prev` and `cur`. When `cur.next.val == cur.val`, advance `cur` until distinct, then `prev.next = cur.next` (skip the entire run). Otherwise advance `prev = prev.next`.

### Algorithm

1. `dummy = new node, dummy.next = head; prev = dummy; cur = head`.
2. While `cur`:
   - If `cur.next` and `cur.next.val == cur.val`:
     - Skip while next has same value.
     - `prev.next = cur.next` (drop the whole run).
   - Else: `prev = prev.next`.
   - `cur = prev.next`.
3. Return `dummy.next`.

### Complexity

- Time: O(n)
- Space: O(1)

---

## Implementation

#### Go

```go
type ListNode struct { Val int; Next *ListNode }

func deleteDuplicates(head *ListNode) *ListNode {
    dummy := &ListNode{Next: head}
    prev := dummy
    cur := head
    for cur != nil {
        if cur.Next != nil && cur.Next.Val == cur.Val {
            v := cur.Val
            for cur != nil && cur.Val == v { cur = cur.Next }
            prev.Next = cur
        } else {
            prev = cur
            cur = cur.Next
        }
    }
    return dummy.Next
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
    public ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy, cur = head;
        while (cur != null) {
            if (cur.next != null && cur.next.val == cur.val) {
                int v = cur.val;
                while (cur != null && cur.val == v) cur = cur.next;
                prev.next = cur;
            } else {
                prev = cur;
                cur = cur.next;
            }
        }
        return dummy.next;
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
    def deleteDuplicates(self, head):
        dummy = ListNode(0, head)
        prev = dummy
        cur = head
        while cur:
            if cur.next and cur.next.val == cur.val:
                v = cur.val
                while cur and cur.val == v:
                    cur = cur.next
                prev.next = cur
            else:
                prev = cur
                cur = cur.next
        return dummy.next
```

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | Empty list | Return null |
| 2 | All duplicates | Return null |
| 3 | No duplicates | Unchanged |
| 4 | Duplicate at head | Drop the whole run, head shifts |
| 5 | Duplicate at tail | Tail truncated |
| 6 | Multiple separate duplicate runs | Each dropped |

---

## Common Mistakes

- Forgetting the dummy head — duplicates at the original head cause head reassignment.
- Comparing `cur.val == cur.next.val` with `==` on `Integer` in Java (boxed) — use `.equals` or unbox.
- Updating `prev` even when removing — `prev` should *not* move when a removal happens.

---

## Related

- [83. Remove Duplicates from Sorted List](https://leetcode.com/problems/remove-duplicates-from-sorted-list/) — keep one copy.

---

## Visual Animation

> [animation.html](./animation.html)
