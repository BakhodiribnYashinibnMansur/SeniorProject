# 0092. Reverse Linked List II

## Problem

| | |
|---|---|
| **Leetcode** | [92. Reverse Linked List II](https://leetcode.com/problems/reverse-linked-list-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Linked List` |

> Given the `head` of a singly linked list and two integers `left` and `right` where `left <= right`, reverse the nodes of the list from position `left` to position `right`, and return *the reversed list*.

### Examples

```
Input: head = [1,2,3,4,5], left = 2, right = 4
Output: [1,4,3,2,5]

Input: head = [5], left = 1, right = 1
Output: [5]
```

### Constraints

- The number of nodes in the list is `n`.
- `1 <= n <= 500`
- `-500 <= Node.val <= 500`
- `1 <= left <= right <= n`

---

## Approach: One-Pass Iterative with Dummy Head

### Idea

Walk to the node before `left` (call it `prev`). Then for each of `right - left` iterations, take the node after the current one and splice it just after `prev` (head-insertion technique).

### Algorithm

```text
dummy = new node, dummy.next = head
prev = dummy
for _ in 1..left-1: prev = prev.next
cur = prev.next
for _ in 1..right-left:
    nxt = cur.next
    cur.next = nxt.next
    nxt.next = prev.next
    prev.next = nxt
return dummy.next
```

### Complexity

- Time: O(n)
- Space: O(1)

### Implementation

#### Go

```go
type ListNode struct { Val int; Next *ListNode }

func reverseBetween(head *ListNode, left int, right int) *ListNode {
    dummy := &ListNode{Next: head}
    prev := dummy
    for i := 1; i < left; i++ {
        prev = prev.Next
    }
    cur := prev.Next
    for i := 0; i < right-left; i++ {
        nxt := cur.Next
        cur.Next = nxt.Next
        nxt.Next = prev.Next
        prev.Next = nxt
    }
    return dummy.Next
}
```

#### Java

```java
class Solution {
    public ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        for (int i = 1; i < left; i++) prev = prev.next;
        ListNode cur = prev.next;
        for (int i = 0; i < right - left; i++) {
            ListNode nxt = cur.next;
            cur.next = nxt.next;
            nxt.next = prev.next;
            prev.next = nxt;
        }
        return dummy.next;
    }
}
```

#### Python

```python
class Solution:
    def reverseBetween(self, head, left, right):
        dummy = ListNode(0, head)
        prev = dummy
        for _ in range(left - 1):
            prev = prev.next
        cur = prev.next
        for _ in range(right - left):
            nxt = cur.next
            cur.next = nxt.next
            nxt.next = prev.next
            prev.next = nxt
        return dummy.next
```

---

## Edge Cases

- left == right: nothing changes.
- Reverse entire list: left=1, right=n.
- Single node: unchanged.

---

## Visual Animation

> [animation.html](./animation.html)
