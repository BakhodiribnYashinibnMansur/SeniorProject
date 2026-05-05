# Linked Lists -- Interview Questions

## Table of Contents

1. [Conceptual Questions](#conceptual-questions)
2. [Coding Challenge 1: Reverse a Linked List](#coding-challenge-1-reverse-a-linked-list)
3. [Coding Challenge 2: Detect a Cycle](#coding-challenge-2-detect-a-cycle)
4. [Coding Challenge 3: Merge Two Sorted Lists](#coding-challenge-3-merge-two-sorted-lists)
5. [Coding Challenge 4: Remove Nth Node From End](#coding-challenge-4-remove-nth-node-from-end)
6. [Coding Challenge 5: Find Intersection Point](#coding-challenge-5-find-intersection-point)
7. [Tips for Linked List Interviews](#tips-for-linked-list-interviews)

---

## Conceptual Questions

**Q1: When would you choose a linked list over an array?**

Use a linked list when you need frequent insertions and deletions at the head (O(1) vs O(n) for arrays), when the data size is unknown and dynamic resizing is expensive, or when you need guaranteed worst-case O(1) insertion without amortized resizing.

**Q2: What is the time complexity to access the k-th element in a singly linked list?**

O(k). There is no random access; you must traverse k nodes from the head.

**Q3: How does a doubly linked list differ from a singly linked list?**

Each node has both `next` and `prev` pointers. This enables bidirectional traversal and O(1) deletion when you have a direct reference to the node (no need to find the predecessor).

**Q4: What is Floyd's cycle detection algorithm?**

Two pointers move at different speeds (slow by 1, fast by 2). If there is a cycle, they will eventually meet. If the fast pointer reaches nil, there is no cycle. Time: O(n), Space: O(1).

**Q5: What is the space overhead of a singly linked list compared to an array?**

Each node stores an extra pointer (8 bytes on 64-bit systems). For an array of integers, this doubles the memory per element. Additionally, each node is a separate heap allocation, adding allocator overhead and reducing cache locality.

---

## Coding Challenge 1: Reverse a Linked List

**Problem:** Reverse a singly linked list in-place.

**Input:** `1 -> 2 -> 3 -> 4 -> 5 -> nil`
**Output:** `5 -> 4 -> 3 -> 2 -> 1 -> nil`

### Go

```go
type ListNode struct {
    Val  int
    Next *ListNode
}

func reverseList(head *ListNode) *ListNode {
    var prev *ListNode
    curr := head
    for curr != nil {
        next := curr.Next
        curr.Next = prev
        prev = curr
        curr = next
    }
    return prev
}
```

### Java

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null, curr = head;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }
}
```

### Python

```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

def reverse_list(head: ListNode) -> ListNode:
    prev = None
    curr = head
    while curr:
        next_node = curr.next
        curr.next = prev
        prev = curr
        curr = next_node
    return prev
```

**Complexity:** Time O(n), Space O(1).

---

## Coding Challenge 2: Detect a Cycle

**Problem:** Given a linked list, determine if it has a cycle. If yes, return the node where the cycle begins. If no, return nil.

### Go

```go
func detectCycle(head *ListNode) *ListNode {
    slow, fast := head, head
    for fast != nil && fast.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
        if slow == fast {
            // Cycle detected. Find the entry point.
            slow = head
            for slow != fast {
                slow = slow.Next
                fast = fast.Next
            }
            return slow
        }
    }
    return nil
}
```

### Java

```java
class Solution {
    public ListNode detectCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                slow = head;
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next;
                }
                return slow;
            }
        }
        return null;
    }
}
```

### Python

```python
def detect_cycle(head: ListNode) -> ListNode:
    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next
        if slow is fast:
            slow = head
            while slow is not fast:
                slow = slow.next
                fast = fast.next
            return slow
    return None
```

**Complexity:** Time O(n), Space O(1).

**Why the entry-point trick works:** When slow and fast meet, slow has traveled mu + k steps (mu = distance to cycle entry, k = distance into cycle). Fast has traveled 2*(mu + k). The difference mu + k equals a multiple of the cycle length lambda. So starting a pointer from head and another from the meeting point, both moving 1 step at a time, they will meet at the cycle entry after mu steps.

---

## Coding Challenge 3: Merge Two Sorted Lists

**Problem:** Merge two sorted linked lists into one sorted list.

### Go

```go
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

### Java

```java
class Solution {
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
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
}
```

### Python

```python
def merge_two_lists(l1: ListNode, l2: ListNode) -> ListNode:
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

**Complexity:** Time O(n + m), Space O(1).

---

## Coding Challenge 4: Remove Nth Node From End

**Problem:** Given a linked list, remove the n-th node from the end and return the head.

**Key insight:** Use two pointers separated by n nodes. When the first reaches the end, the second is at the node to remove.

### Go

```go
func removeNthFromEnd(head *ListNode, n int) *ListNode {
    dummy := &ListNode{Next: head}
    first, second := dummy, dummy
    for i := 0; i <= n; i++ {
        first = first.Next
    }
    for first != nil {
        first = first.Next
        second = second.Next
    }
    second.Next = second.Next.Next
    return dummy.Next
}
```

### Java

```java
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode first = dummy, second = dummy;
        for (int i = 0; i <= n; i++) first = first.next;
        while (first != null) {
            first = first.next;
            second = second.next;
        }
        second.next = second.next.next;
        return dummy.next;
    }
}
```

### Python

```python
def remove_nth_from_end(head: ListNode, n: int) -> ListNode:
    dummy = ListNode(0, head)
    first = second = dummy
    for _ in range(n + 1):
        first = first.next
    while first:
        first = first.next
        second = second.next
    second.next = second.next.next
    return dummy.next
```

**Complexity:** Time O(L) where L is the list length, Space O(1).

---

## Coding Challenge 5: Find Intersection Point

**Problem:** Given two linked lists that may converge at some node, find the intersection node. If they do not intersect, return nil.

### Go

```go
func getIntersectionNode(headA, headB *ListNode) *ListNode {
    a, b := headA, headB
    for a != b {
        if a != nil {
            a = a.Next
        } else {
            a = headB
        }
        if b != nil {
            b = b.Next
        } else {
            b = headA
        }
    }
    return a
}
```

### Java

```java
class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) {
            a = (a != null) ? a.next : headB;
            b = (b != null) ? b.next : headA;
        }
        return a;
    }
}
```

### Python

```python
def get_intersection_node(head_a: ListNode, head_b: ListNode) -> ListNode:
    a, b = head_a, head_b
    while a is not b:
        a = a.next if a else head_b
        b = b.next if b else head_a
    return a
```

**Why it works:** Pointer `a` traverses list A then list B. Pointer `b` traverses list B then list A. Both travel the same total distance (len_A + len_B). If the lists intersect, the pointers align at the intersection node. If not, both reach nil simultaneously.

**Complexity:** Time O(n + m), Space O(1).

---

## Tips for Linked List Interviews

1. **Always clarify:** singly or doubly linked? Sorted? Can it have cycles? Can it be empty?

2. **Use a dummy/sentinel node** to avoid edge cases with head deletion or empty lists.

3. **Draw it out.** Sketch the list on paper/whiteboard before coding. Update the drawing as you trace through your algorithm.

4. **Two-pointer technique** solves many problems: cycle detection, finding the middle, nth from end, intersection.

5. **Watch for null/nil dereferences.** Always check `node != nil` before accessing `node.next`.

6. **Consider edge cases:** empty list, single node, two nodes, deleting the head, deleting the tail.

7. **State your complexity clearly.** Interviewers expect you to know: reversal is O(n)/O(1), merge is O(n+m)/O(1), cycle detection is O(n)/O(1).
