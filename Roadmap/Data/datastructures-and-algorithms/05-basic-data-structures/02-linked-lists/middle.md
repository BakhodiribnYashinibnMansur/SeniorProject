# Linked Lists -- Middle Level

## Prerequisites

- Singly linked list implementation (junior level)
- Big-O notation and time complexity
- Pointers/references in your chosen language

## Table of Contents

1. [Doubly Linked List](#doubly-linked-list)
2. [Circular Linked List](#circular-linked-list)
3. [Sentinel Nodes](#sentinel-nodes)
4. [Fast and Slow Pointers](#fast-and-slow-pointers)
5. [Reversing a Linked List](#reversing-a-linked-list)
6. [Merge Two Sorted Lists](#merge-two-sorted-lists)
7. [Performance Comparison: Linked List vs Array](#performance-comparison)
8. [LRU Cache with Hash Map + Doubly Linked List](#lru-cache)
9. [Summary](#summary)

---

## Doubly Linked List

A doubly linked list gives each node two pointers: `next` (forward) and `prev` (backward). This enables bidirectional traversal and O(1) deletion when you have a reference to the node.

```
nil <-- [A] <--> [B] <--> [C] <--> [D] --> nil
         ^                          ^
        head                       tail
```

### Go

```go
package main

import "fmt"

type DNode struct {
    Data int
    Prev *DNode
    Next *DNode
}

type DoublyLinkedList struct {
    Head *DNode
    Tail *DNode
    Size int
}

func NewDoublyLinkedList() *DoublyLinkedList {
    return &DoublyLinkedList{}
}

func (dll *DoublyLinkedList) InsertAtHead(data int) {
    node := &DNode{Data: data, Next: dll.Head}
    if dll.Head != nil {
        dll.Head.Prev = node
    } else {
        dll.Tail = node
    }
    dll.Head = node
    dll.Size++
}

func (dll *DoublyLinkedList) InsertAtTail(data int) {
    node := &DNode{Data: data, Prev: dll.Tail}
    if dll.Tail != nil {
        dll.Tail.Next = node
    } else {
        dll.Head = node
    }
    dll.Tail = node
    dll.Size++
}

// DeleteNode removes a specific node in O(1) given a direct reference.
func (dll *DoublyLinkedList) DeleteNode(node *DNode) {
    if node.Prev != nil {
        node.Prev.Next = node.Next
    } else {
        dll.Head = node.Next
    }
    if node.Next != nil {
        node.Next.Prev = node.Prev
    } else {
        dll.Tail = node.Prev
    }
    dll.Size--
}

func (dll *DoublyLinkedList) Print() {
    curr := dll.Head
    for curr != nil {
        fmt.Printf("%d", curr.Data)
        if curr.Next != nil {
            fmt.Print(" <-> ")
        }
        curr = curr.Next
    }
    fmt.Println()
}
```

### Java

```java
public class DoublyLinkedList {

    static class DNode {
        int data;
        DNode prev;
        DNode next;

        DNode(int data) {
            this.data = data;
        }
    }

    private DNode head;
    private DNode tail;
    private int size;

    public void insertAtHead(int data) {
        DNode node = new DNode(data);
        node.next = head;
        if (head != null) {
            head.prev = node;
        } else {
            tail = node;
        }
        head = node;
        size++;
    }

    public void insertAtTail(int data) {
        DNode node = new DNode(data);
        node.prev = tail;
        if (tail != null) {
            tail.next = node;
        } else {
            head = node;
        }
        tail = node;
        size++;
    }

    /**
     * Delete a specific node in O(1) given a direct reference.
     */
    public void deleteNode(DNode node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        size--;
    }

    public void print() {
        DNode curr = head;
        while (curr != null) {
            System.out.print(curr.data);
            if (curr.next != null) System.out.print(" <-> ");
            curr = curr.next;
        }
        System.out.println();
    }
}
```

### Python

```python
class DNode:
    def __init__(self, data):
        self.data = data
        self.prev = None
        self.next = None


class DoublyLinkedList:
    def __init__(self):
        self.head = None
        self.tail = None
        self.size = 0

    def insert_at_head(self, data):
        node = DNode(data)
        node.next = self.head
        if self.head:
            self.head.prev = node
        else:
            self.tail = node
        self.head = node
        self.size += 1

    def insert_at_tail(self, data):
        node = DNode(data)
        node.prev = self.tail
        if self.tail:
            self.tail.next = node
        else:
            self.head = node
        self.tail = node
        self.size += 1

    def delete_node(self, node):
        """Delete a specific node in O(1) given a direct reference."""
        if node.prev:
            node.prev.next = node.next
        else:
            self.head = node.next
        if node.next:
            node.next.prev = node.prev
        else:
            self.tail = node.prev
        self.size -= 1

    def print_list(self):
        parts = []
        curr = self.head
        while curr:
            parts.append(str(curr.data))
            curr = curr.next
        print(" <-> ".join(parts))
```

**Key advantage:** When you have a direct reference to a node, deletion is O(1) in a doubly linked list versus O(n) in a singly linked list (because you do not need to find the predecessor).

---

## Circular Linked List

In a circular linked list, the last node's `next` pointer points back to the head instead of `nil`. This creates a cycle by design.

```
head -> [A] -> [B] -> [C] -> [D] --+
         ^                          |
         +--------------------------+
```

### Use Cases

- **Round-robin scheduling** -- processes take turns in a circular queue.
- **Circular buffers** -- media players looping a playlist.
- **Board games** -- players take turns in a cycle.

### Go

```go
type CircularList struct {
    Tail *Node // pointing to tail; tail.Next = head
    Size int
}

func (cl *CircularList) Insert(data int) {
    node := &Node{Data: data}
    if cl.Tail == nil {
        node.Next = node // points to itself
        cl.Tail = node
    } else {
        node.Next = cl.Tail.Next // new node points to head
        cl.Tail.Next = node      // old tail points to new node
        cl.Tail = node           // update tail
    }
    cl.Size++
}

func (cl *CircularList) Print() {
    if cl.Tail == nil {
        fmt.Println("empty")
        return
    }
    curr := cl.Tail.Next // start at head
    for {
        fmt.Printf("%d ", curr.Data)
        curr = curr.Next
        if curr == cl.Tail.Next {
            break
        }
    }
    fmt.Println()
}
```

### Java

```java
public class CircularList {
    private Node tail;
    private int size;

    public void insert(int data) {
        Node node = new Node(data);
        if (tail == null) {
            node.next = node;
            tail = node;
        } else {
            node.next = tail.next;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public void print() {
        if (tail == null) { System.out.println("empty"); return; }
        Node curr = tail.next;
        do {
            System.out.print(curr.data + " ");
            curr = curr.next;
        } while (curr != tail.next);
        System.out.println();
    }
}
```

### Python

```python
class CircularList:
    def __init__(self):
        self.tail = None
        self.size = 0

    def insert(self, data):
        node = Node(data)
        if self.tail is None:
            node.next = node
            self.tail = node
        else:
            node.next = self.tail.next
            self.tail.next = node
            self.tail = node
        self.size += 1

    def print_list(self):
        if self.tail is None:
            print("empty")
            return
        curr = self.tail.next  # head
        parts = []
        while True:
            parts.append(str(curr.data))
            curr = curr.next
            if curr == self.tail.next:
                break
        print(" -> ".join(parts) + " -> (back to head)")
```

---

## Sentinel Nodes

A **sentinel node** (dummy node) is a special node that does not hold real data. It simplifies edge-case handling by ensuring that `head` and `tail` always exist.

```
sentinel_head <-> [A] <-> [B] <-> [C] <-> sentinel_tail
```

With sentinels, you never have to check for `nil` head or tail. Every real node always has a valid `prev` and `next`.

```python
class SentinelDoublyLinkedList:
    def __init__(self):
        self.sentinel_head = DNode(0)  # dummy
        self.sentinel_tail = DNode(0)  # dummy
        self.sentinel_head.next = self.sentinel_tail
        self.sentinel_tail.prev = self.sentinel_head
        self.size = 0

    def insert_after(self, prev_node, data):
        """Insert a new node after prev_node. No nil checks needed."""
        node = DNode(data)
        node.prev = prev_node
        node.next = prev_node.next
        prev_node.next.prev = node
        prev_node.next = node
        self.size += 1

    def delete_node(self, node):
        """Delete node. No nil checks needed because of sentinels."""
        node.prev.next = node.next
        node.next.prev = node.prev
        self.size -= 1

    def insert_at_head(self, data):
        self.insert_after(self.sentinel_head, data)

    def insert_at_tail(self, data):
        self.insert_after(self.sentinel_tail.prev, data)
```

Sentinel nodes are used extensively in production code (e.g., the Linux kernel's linked list implementation uses them).

---

## Fast and Slow Pointers

The **two-pointer technique** (also called the tortoise and hare algorithm) uses two pointers that move at different speeds. It solves several classic linked list problems.

### Cycle Detection (Floyd's Algorithm)

If a linked list has a cycle, a fast pointer (moves 2 steps) will eventually meet a slow pointer (moves 1 step).

### Go

```go
func HasCycle(head *Node) bool {
    slow, fast := head, head
    for fast != nil && fast.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
        if slow == fast {
            return true
        }
    }
    return false
}
```

### Java

```java
public static boolean hasCycle(Node head) {
    Node slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

### Python

```python
def has_cycle(head):
    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next
        if slow is fast:
            return True
    return False
```

**Why it works:** In a cycle of length C, the fast pointer gains 1 step per iteration on the slow pointer. After at most C iterations inside the cycle, they will overlap.

### Find the Middle Node

The slow pointer will be at the middle when the fast pointer reaches the end.

```go
func FindMiddle(head *Node) *Node {
    slow, fast := head, head
    for fast != nil && fast.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
    }
    return slow
}
```

```java
public static Node findMiddle(Node head) {
    Node slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}
```

```python
def find_middle(head):
    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next
    return slow
```

---

## Reversing a Linked List

### Iterative Approach

Maintain three pointers: `prev`, `current`, and `next_node`. Walk through the list, reversing each pointer.

### Go

```go
func Reverse(head *Node) *Node {
    var prev *Node
    current := head
    for current != nil {
        nextNode := current.Next
        current.Next = prev
        prev = current
        current = nextNode
    }
    return prev // new head
}
```

### Java

```java
public static Node reverse(Node head) {
    Node prev = null, current = head;
    while (current != null) {
        Node nextNode = current.next;
        current.next = prev;
        prev = current;
        current = nextNode;
    }
    return prev;
}
```

### Python

```python
def reverse(head):
    prev = None
    current = head
    while current:
        next_node = current.next
        current.next = prev
        prev = current
        current = next_node
    return prev  # new head
```

**Trace:**
```
Original: 1 -> 2 -> 3 -> nil
Step 1:   nil <- 1    2 -> 3 -> nil   (prev=1, current=2)
Step 2:   nil <- 1 <- 2    3 -> nil   (prev=2, current=3)
Step 3:   nil <- 1 <- 2 <- 3          (prev=3, current=nil)
Return prev (3): 3 -> 2 -> 1 -> nil
```

### Recursive Approach

```go
func ReverseRecursive(head *Node) *Node {
    if head == nil || head.Next == nil {
        return head
    }
    newHead := ReverseRecursive(head.Next)
    head.Next.Next = head
    head.Next = nil
    return newHead
}
```

```java
public static Node reverseRecursive(Node head) {
    if (head == null || head.next == null) return head;
    Node newHead = reverseRecursive(head.next);
    head.next.next = head;
    head.next = null;
    return newHead;
}
```

```python
def reverse_recursive(head):
    if head is None or head.next is None:
        return head
    new_head = reverse_recursive(head.next)
    head.next.next = head
    head.next = None
    return new_head
```

**How recursion works:** The function recurses to the end of the list, then on the way back up, each node's `next.next` is set to point back to it, effectively reversing the link.

---

## Merge Two Sorted Lists

Given two sorted linked lists, merge them into one sorted list.

### Go

```go
func MergeSorted(l1, l2 *Node) *Node {
    dummy := &Node{}
    current := dummy

    for l1 != nil && l2 != nil {
        if l1.Data <= l2.Data {
            current.Next = l1
            l1 = l1.Next
        } else {
            current.Next = l2
            l2 = l2.Next
        }
        current = current.Next
    }

    if l1 != nil {
        current.Next = l1
    } else {
        current.Next = l2
    }

    return dummy.Next
}
```

### Java

```java
public static Node mergeSorted(Node l1, Node l2) {
    Node dummy = new Node(0);
    Node current = dummy;

    while (l1 != null && l2 != null) {
        if (l1.data <= l2.data) {
            current.next = l1;
            l1 = l1.next;
        } else {
            current.next = l2;
            l2 = l2.next;
        }
        current = current.next;
    }

    current.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

### Python

```python
def merge_sorted(l1, l2):
    dummy = Node(0)
    current = dummy

    while l1 and l2:
        if l1.data <= l2.data:
            current.next = l1
            l1 = l1.next
        else:
            current.next = l2
            l2 = l2.next
        current = current.next

    current.next = l1 if l1 else l2
    return dummy.next
```

Time: O(n + m) where n and m are the lengths of the two lists.
Space: O(1) -- we reuse existing nodes.

---

## Performance Comparison

### Benchmark: Insert 100,000 elements at the beginning

| Structure       | Time (approx) | Why                           |
|-----------------|---------------|-------------------------------|
| Linked list     | ~5 ms         | O(1) per insert               |
| ArrayList/slice | ~500 ms       | O(n) shift per insert         |

### Benchmark: Sequential access of all elements

| Structure       | Time (approx) | Why                           |
|-----------------|---------------|-------------------------------|
| Linked list     | ~15 ms        | O(n) but cache-unfriendly     |
| Array           | ~3 ms         | O(n) and cache-friendly       |

### Benchmark: Random access (access element at index i)

| Structure       | Time per access | Why                         |
|-----------------|----------------|-----------------------------|
| Linked list     | O(n)           | Must traverse               |
| Array           | O(1)           | Direct index                |

**Takeaway:** Linked lists win for insertion/deletion-heavy workloads. Arrays win for access-heavy and iteration-heavy workloads due to CPU cache locality.

---

## LRU Cache

An **LRU (Least Recently Used) Cache** evicts the least recently accessed item when the cache is full. It combines a **hash map** (for O(1) lookup) with a **doubly linked list** (for O(1) insertion/removal and ordering).

```
Most Recent                            Least Recent
    |                                       |
    v                                       v
sentinel_head <-> [A] <-> [B] <-> [C] <-> sentinel_tail
                   ^       ^       ^
                   |       |       |
              map["A"]  map["B"]  map["C"]
```

### Python

```python
class LRUNode:
    def __init__(self, key, value):
        self.key = key
        self.value = value
        self.prev = None
        self.next = None


class LRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.cache = {}  # key -> LRUNode

        # Sentinel nodes
        self.head = LRUNode(0, 0)
        self.tail = LRUNode(0, 0)
        self.head.next = self.tail
        self.tail.prev = self.head

    def _remove(self, node):
        """Remove a node from the doubly linked list."""
        node.prev.next = node.next
        node.next.prev = node.prev

    def _add_to_front(self, node):
        """Add a node right after the sentinel head (most recent)."""
        node.next = self.head.next
        node.prev = self.head
        self.head.next.prev = node
        self.head.next = node

    def get(self, key):
        """
        Get value by key. Move the accessed node to front.
        Time: O(1)
        """
        if key not in self.cache:
            return -1
        node = self.cache[key]
        self._remove(node)
        self._add_to_front(node)
        return node.value

    def put(self, key, value):
        """
        Insert or update a key-value pair.
        Evict the least recently used item if at capacity.
        Time: O(1)
        """
        if key in self.cache:
            self._remove(self.cache[key])

        node = LRUNode(key, value)
        self.cache[key] = node
        self._add_to_front(node)

        if len(self.cache) > self.capacity:
            # Evict from back (least recently used)
            lru = self.tail.prev
            self._remove(lru)
            del self.cache[lru.key]


# Usage
cache = LRUCache(3)
cache.put("a", 1)
cache.put("b", 2)
cache.put("c", 3)
print(cache.get("a"))   # 1 -- moves "a" to front
cache.put("d", 4)       # evicts "b" (least recently used)
print(cache.get("b"))   # -1 -- evicted
```

### Why this works

| Operation | Hash Map  | Doubly Linked List | Combined |
|-----------|-----------|--------------------|----------|
| Get       | O(1)      | O(1) move to front | O(1)     |
| Put       | O(1)      | O(1) insert/remove | O(1)     |
| Evict     | O(1) del  | O(1) remove tail   | O(1)     |

This is one of the most important practical applications of doubly linked lists in software engineering.

---

## Summary

| Topic                   | Key Takeaway                                                |
|-------------------------|-------------------------------------------------------------|
| Doubly linked list      | O(1) deletion with node reference, bidirectional traversal  |
| Circular linked list    | Last node links to head; useful for round-robin             |
| Sentinel nodes          | Eliminate nil checks; simplify insertion/deletion            |
| Fast/slow pointers      | Detect cycles O(n), find middle O(n)                        |
| Reverse (iterative)     | Three pointers: prev, current, next -- O(n) time, O(1) space |
| Reverse (recursive)     | Reverse links on the way back from recursion base case      |
| Merge sorted lists      | Two-pointer merge in O(n+m) time, O(1) space                |
| LRU Cache               | Hash map + doubly linked list = all O(1) operations         |

**Next step:** Move to the senior level for lock-free lists, skip lists, and production system applications.
