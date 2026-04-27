# 0083. Remove Duplicates from Sorted List

## Problem

| | |
|---|---|
| **Leetcode** | [83. Remove Duplicates from Sorted List](https://leetcode.com/problems/remove-duplicates-from-sorted-list/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Linked List` |

> Given the `head` of a sorted linked list, *delete all duplicates such that each element appears only once*. Return *the linked list **sorted** as well*.

### Examples

```
Input: head = [1,1,2]
Output: [1,2]

Input: head = [1,1,2,3,3]
Output: [1,2,3]
```

### Constraints

- `0 <= number of nodes <= 300`
- `-100 <= Node.val <= 100`
- The list is guaranteed to be sorted.

---

## Approach

Walk the list with a single pointer `cur`. If `cur.next.val == cur.val`, skip the next node by `cur.next = cur.next.next`. Otherwise advance.

### Complexity

- Time: O(n)
- Space: O(1)

---

## Implementation

#### Go

```go
type ListNode struct { Val int; Next *ListNode }

func deleteDuplicates(head *ListNode) *ListNode {
    cur := head
    for cur != nil && cur.Next != nil {
        if cur.Next.Val == cur.Val {
            cur.Next = cur.Next.Next
        } else {
            cur = cur.Next
        }
    }
    return head
}
```

#### Java

```java
class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode cur = head;
        while (cur != null && cur.next != null) {
            if (cur.next.val == cur.val) cur.next = cur.next.next;
            else cur = cur.next;
        }
        return head;
    }
}
```

#### Python

```python
class Solution:
    def deleteDuplicates(self, head):
        cur = head
        while cur and cur.next:
            if cur.next.val == cur.val:
                cur.next = cur.next.next
            else:
                cur = cur.next
        return head
```

---

## Edge Cases

- Empty list / single node: unchanged.
- All duplicates: collapses to one node.
- No duplicates: unchanged.

---

## Related

- [82. Remove Duplicates from Sorted List II](https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/) — delete *all* nodes that have duplicates.

---

## Visual Animation

> [animation.html](./animation.html)
