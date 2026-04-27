# 0086. Partition List

## Problem

| | |
|---|---|
| **Leetcode** | [86. Partition List](https://leetcode.com/problems/partition-list/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Linked List`, `Two Pointers` |

> Given the `head` of a linked list and a value `x`, partition it such that all nodes less than `x` come before nodes greater than or equal to `x`.
>
> You should preserve the **original relative order** of the nodes in each of the two partitions.

### Examples

```
Input: head = [1,4,3,2,5,2], x = 3
Output: [1,2,2,4,3,5]

Input: head = [2,1], x = 2
Output: [1,2]
```

### Constraints

- The number of nodes is in the range `[0, 200]`.
- `-100 <= Node.val <= 100`
- `-200 <= x <= 200`

---

## Approach: Two Dummy Lists

Build two lists with dummy heads:
- `lessHead` for nodes with `val < x`
- `geHead` for nodes with `val >= x`

Walk the original list, append each node to the right list. Finally, link `lessTail.next = geHead.next` and `geTail.next = null`.

### Complexity

- Time: O(n)
- Space: O(1)

### Implementation

#### Go

```go
type ListNode struct { Val int; Next *ListNode }

func partition(head *ListNode, x int) *ListNode {
    lessDummy := &ListNode{}
    geDummy := &ListNode{}
    lt, gt := lessDummy, geDummy
    for cur := head; cur != nil; cur = cur.Next {
        if cur.Val < x {
            lt.Next = cur
            lt = lt.Next
        } else {
            gt.Next = cur
            gt = gt.Next
        }
    }
    gt.Next = nil
    lt.Next = geDummy.Next
    return lessDummy.Next
}
```

#### Java

```java
class Solution {
    public ListNode partition(ListNode head, int x) {
        ListNode lessDummy = new ListNode(0), geDummy = new ListNode(0);
        ListNode lt = lessDummy, gt = geDummy;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.val < x) { lt.next = cur; lt = lt.next; }
            else { gt.next = cur; gt = gt.next; }
        }
        gt.next = null;
        lt.next = geDummy.next;
        return lessDummy.next;
    }
}
```

#### Python

```python
class Solution:
    def partition(self, head, x):
        less_dummy = ListNode(0); ge_dummy = ListNode(0)
        lt, gt = less_dummy, ge_dummy
        cur = head
        while cur:
            if cur.val < x: lt.next = cur; lt = lt.next
            else: gt.next = cur; gt = gt.next
            cur = cur.next
        gt.next = None
        lt.next = ge_dummy.next
        return less_dummy.next
```

---

## Edge Cases

- Empty list → null
- All values < x → unchanged
- All values ≥ x → unchanged
- x smaller than min → unchanged
- x larger than max → unchanged
- Single node → unchanged

---

## Visual Animation

> [animation.html](./animation.html)
