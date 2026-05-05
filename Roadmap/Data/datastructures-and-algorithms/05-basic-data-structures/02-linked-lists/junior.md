# Linked Lists -- Junior Level

## Table of Contents

1. [What is a Linked List?](#what-is-a-linked-list)
2. [Core Concepts: Nodes and Pointers](#core-concepts-nodes-and-pointers)
3. [Singly Linked List](#singly-linked-list)
4. [Doubly Linked List (Introduction)](#doubly-linked-list-introduction)
5. [Operations and Time Complexity](#operations-and-time-complexity)
6. [Linked Lists vs Arrays](#linked-lists-vs-arrays)
7. [Real-World Analogies](#real-world-analogies)
8. [Full Implementation: Singly Linked List](#full-implementation-singly-linked-list)
9. [Common Mistakes](#common-mistakes)
10. [Practice Exercises](#practice-exercises)
11. [Summary](#summary)

---

## What is a Linked List?

A **linked list** is a linear data structure where each element (called a **node**) contains two things:

1. **Data** -- the value stored in the node.
2. **Pointer (reference)** -- a link to the next node in the sequence.

Unlike arrays, linked list elements are **not stored in contiguous memory**. Each node can live anywhere in memory; the pointers are what hold the structure together.

```
[data|next] -> [data|next] -> [data|next] -> nil
```

### Why do we need linked lists?

Arrays have a fixed size (in many languages) and inserting/deleting elements in the middle requires shifting all subsequent elements. Linked lists solve these problems:

- **Dynamic size** -- grow and shrink at runtime without pre-allocation.
- **Efficient insertion/deletion** -- add or remove nodes without shifting other elements.
- **No wasted memory** -- allocate exactly what you need (though each node has pointer overhead).

---

## Core Concepts: Nodes and Pointers

### The Node

A node is the fundamental building block of a linked list. It is a small object (or struct) that holds:

| Field  | Description                                      |
|--------|--------------------------------------------------|
| `data` | The value stored (int, string, any type)         |
| `next` | A reference/pointer to the next node (or `nil`)  |

### The Head Pointer

Every linked list maintains a **head** pointer -- a reference to the first node. If the list is empty, `head` is `nil` (or `null`).

### The Tail Pointer (optional)

Some implementations also keep a **tail** pointer to the last node for O(1) insertion at the end.

### Traversal

To access elements, you start at `head` and follow `next` pointers one by one until you reach `nil`. There is **no random access** -- you cannot jump to the 5th element directly.

```
head -> [10|*] -> [20|*] -> [30|*] -> nil
```

To reach 30, you must visit 10, then 20, then 30. This takes O(n) time.

---

## Singly Linked List

In a **singly linked list**, each node has exactly one pointer: `next`. You can only traverse in one direction -- from head to tail.

```
head
 |
 v
[A] -> [B] -> [C] -> [D] -> nil
```

**Pros:**
- Simple to implement.
- Less memory per node (only one pointer).

**Cons:**
- Cannot traverse backwards.
- Deleting a node requires knowing the previous node.

---

## Doubly Linked List (Introduction)

In a **doubly linked list**, each node has two pointers: `next` and `prev`.

```
nil <- [A] <-> [B] <-> [C] <-> [D] -> nil
        ^                        ^
       head                     tail
```

**Pros:**
- Traverse in both directions.
- Easier deletion (node knows its predecessor).

**Cons:**
- More memory per node (two pointers).
- Slightly more complex insertion/deletion logic.

We will cover doubly linked lists in detail at the middle level.

---

## Operations and Time Complexity

| Operation             | Singly LL (with tail) | Array (dynamic) |
|-----------------------|-----------------------|-----------------|
| Insert at head        | O(1)                  | O(n)            |
| Insert at tail        | O(1)                  | O(1) amortized  |
| Insert at position k  | O(k)                  | O(n)            |
| Delete at head        | O(1)                  | O(n)            |
| Delete at tail        | O(n)*                 | O(1)            |
| Delete by value       | O(n)                  | O(n)            |
| Search                | O(n)                  | O(n)            |
| Access by index       | O(n)                  | O(1)            |

*Delete at tail is O(n) for singly linked lists because you need to find the second-to-last node.

### Insert at Head -- O(1)

1. Create a new node.
2. Point the new node's `next` to the current `head`.
3. Update `head` to point to the new node.

```
Before: head -> [B] -> [C] -> nil
Step 1: newNode = [A]
Step 2: [A].next = head   =>  [A] -> [B] -> [C] -> nil
Step 3: head = [A]        =>  head -> [A] -> [B] -> [C] -> nil
```

### Insert at Tail -- O(1) with tail pointer

1. Create a new node with `next = nil`.
2. Point the current tail's `next` to the new node.
3. Update `tail` to the new node.

```
Before: head -> [A] -> [B] -> nil   (tail = [B])
Step 1: newNode = [C], [C].next = nil
Step 2: [B].next = [C]  =>  head -> [A] -> [B] -> [C] -> nil
Step 3: tail = [C]
```

### Search -- O(n)

Start at `head`, compare each node's data with the target. Stop when found or when you reach `nil`.

### Delete -- O(n)

1. Find the node to delete and its predecessor.
2. Update the predecessor's `next` to skip over the deleted node.
3. If deleting the head, update `head`.

```
Before: head -> [A] -> [B] -> [C] -> nil
Delete B:
  - Find B, predecessor is A.
  - A.next = C
After:  head -> [A] -> [C] -> nil
```

---

## Linked Lists vs Arrays

| Feature               | Linked List                      | Array                        |
|-----------------------|----------------------------------|------------------------------|
| Memory layout         | Scattered (non-contiguous)       | Contiguous                   |
| Size                  | Dynamic                          | Fixed (or resize needed)     |
| Access by index       | O(n) -- must traverse            | O(1) -- direct access        |
| Insert at beginning   | O(1)                             | O(n) -- shift all elements   |
| Insert at end         | O(1) with tail pointer           | O(1) amortized               |
| Insert in middle      | O(n) to find + O(1) to insert    | O(n) -- shift elements       |
| Delete at beginning   | O(1)                             | O(n) -- shift all elements   |
| Memory overhead       | Extra pointer per node           | None (or minimal)            |
| Cache performance     | Poor (scattered memory)          | Excellent (locality)         |

**When to use a linked list:**
- Frequent insertions/deletions at the head.
- You do not need random access.
- You want guaranteed O(1) insertion without amortized resizing.

**When to use an array:**
- You need fast random access by index.
- You iterate sequentially often (cache-friendly).
- Memory overhead per element matters.

---

## Real-World Analogies

### Train Cars

A linked list is like a train. Each car (node) is connected to the next car by a coupling (pointer). The locomotive is the head. To reach the 5th car, you must walk through cars 1 through 4. You can easily add or remove a car by connecting/disconnecting couplings.

### Chain Links

Think of a chain. Each link connects to the next. To find a specific link, you must follow the chain from the beginning. Breaking a link and re-connecting is easy -- no need to "shift" anything.

### Scavenger Hunt

In a scavenger hunt, each clue tells you where the next clue is. You cannot jump to clue 7 directly; you must follow clues 1 through 6 first. Each clue (node) contains information (data) and directions to the next location (pointer).

### Browser History

Your browser's back button works like a linked list. Each page has a reference to the previous page. You navigate backwards one page at a time.

---

## Full Implementation: Singly Linked List

### Go

```go
package main

import "fmt"

// Node represents a single element in the linked list.
type Node struct {
    Data int
    Next *Node
}

// LinkedList holds a reference to the head and tail, plus the size.
type LinkedList struct {
    Head *Node
    Tail *Node
    Size int
}

// NewLinkedList creates and returns an empty linked list.
func NewLinkedList() *LinkedList {
    return &LinkedList{Head: nil, Tail: nil, Size: 0}
}

// InsertAtHead adds a new node with the given value at the beginning.
// Time: O(1)
func (ll *LinkedList) InsertAtHead(data int) {
    newNode := &Node{Data: data, Next: ll.Head}
    ll.Head = newNode
    if ll.Tail == nil {
        ll.Tail = newNode
    }
    ll.Size++
}

// InsertAtTail adds a new node with the given value at the end.
// Time: O(1)
func (ll *LinkedList) InsertAtTail(data int) {
    newNode := &Node{Data: data, Next: nil}
    if ll.Tail != nil {
        ll.Tail.Next = newNode
    } else {
        ll.Head = newNode
    }
    ll.Tail = newNode
    ll.Size++
}

// Search returns true if the value exists in the list.
// Time: O(n)
func (ll *LinkedList) Search(target int) bool {
    current := ll.Head
    for current != nil {
        if current.Data == target {
            return true
        }
        current = current.Next
    }
    return false
}

// Delete removes the first occurrence of the given value.
// Returns true if the value was found and deleted, false otherwise.
// Time: O(n)
func (ll *LinkedList) Delete(target int) bool {
    if ll.Head == nil {
        return false
    }

    // Special case: deleting the head node.
    if ll.Head.Data == target {
        ll.Head = ll.Head.Next
        if ll.Head == nil {
            ll.Tail = nil
        }
        ll.Size--
        return true
    }

    // General case: find the node before the target.
    current := ll.Head
    for current.Next != nil {
        if current.Next.Data == target {
            if current.Next == ll.Tail {
                ll.Tail = current
            }
            current.Next = current.Next.Next
            ll.Size--
            return true
        }
        current = current.Next
    }

    return false
}

// Print displays all elements in the linked list.
func (ll *LinkedList) Print() {
    current := ll.Head
    for current != nil {
        fmt.Printf("%d", current.Data)
        if current.Next != nil {
            fmt.Print(" -> ")
        }
        current = current.Next
    }
    fmt.Println(" -> nil")
}

func main() {
    list := NewLinkedList()

    // Insert elements
    list.InsertAtHead(3)
    list.InsertAtHead(2)
    list.InsertAtHead(1)
    list.InsertAtTail(4)
    list.InsertAtTail(5)

    fmt.Print("List: ")
    list.Print()
    // Output: 1 -> 2 -> 3 -> 4 -> 5 -> nil

    fmt.Printf("Size: %d\n", list.Size)
    // Output: Size: 5

    // Search
    fmt.Printf("Search 3: %v\n", list.Search(3))   // true
    fmt.Printf("Search 99: %v\n", list.Search(99))  // false

    // Delete
    list.Delete(3)
    fmt.Print("After deleting 3: ")
    list.Print()
    // Output: 1 -> 2 -> 4 -> 5 -> nil

    list.Delete(1)
    fmt.Print("After deleting 1 (head): ")
    list.Print()
    // Output: 2 -> 4 -> 5 -> nil

    list.Delete(5)
    fmt.Print("After deleting 5 (tail): ")
    list.Print()
    // Output: 2 -> 4 -> nil
}
```

### Java

```java
public class LinkedList {

    // Node represents a single element in the linked list.
    static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Inserts a new node at the head of the list.
     * Time: O(1)
     */
    public void insertAtHead(int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
        if (tail == null) {
            tail = newNode;
        }
        size++;
    }

    /**
     * Inserts a new node at the tail of the list.
     * Time: O(1)
     */
    public void insertAtTail(int data) {
        Node newNode = new Node(data);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        size++;
    }

    /**
     * Searches for a value in the list.
     * Time: O(n)
     */
    public boolean search(int target) {
        Node current = head;
        while (current != null) {
            if (current.data == target) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Deletes the first occurrence of the given value.
     * Returns true if found and deleted, false otherwise.
     * Time: O(n)
     */
    public boolean delete(int target) {
        if (head == null) {
            return false;
        }

        // Deleting the head node.
        if (head.data == target) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return true;
        }

        // Find the node before the target.
        Node current = head;
        while (current.next != null) {
            if (current.next.data == target) {
                if (current.next == tail) {
                    tail = current;
                }
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Prints all elements in the list.
     */
    public void print() {
        Node current = head;
        while (current != null) {
            System.out.print(current.data);
            if (current.next != null) {
                System.out.print(" -> ");
            }
            current = current.next;
        }
        System.out.println(" -> null");
    }

    public int getSize() {
        return size;
    }

    public static void main(String[] args) {
        LinkedList list = new LinkedList();

        // Insert elements
        list.insertAtHead(3);
        list.insertAtHead(2);
        list.insertAtHead(1);
        list.insertAtTail(4);
        list.insertAtTail(5);

        System.out.print("List: ");
        list.print();
        // Output: 1 -> 2 -> 3 -> 4 -> 5 -> null

        System.out.println("Size: " + list.getSize());
        // Output: Size: 5

        // Search
        System.out.println("Search 3: " + list.search(3));   // true
        System.out.println("Search 99: " + list.search(99));  // false

        // Delete
        list.delete(3);
        System.out.print("After deleting 3: ");
        list.print();

        list.delete(1);
        System.out.print("After deleting 1 (head): ");
        list.print();

        list.delete(5);
        System.out.print("After deleting 5 (tail): ");
        list.print();
    }
}
```

### Python

```python
class Node:
    """Represents a single element in the linked list."""

    def __init__(self, data):
        self.data = data
        self.next = None


class LinkedList:
    """Singly linked list with head and tail pointers."""

    def __init__(self):
        self.head = None
        self.tail = None
        self.size = 0

    def insert_at_head(self, data):
        """
        Insert a new node at the beginning of the list.
        Time: O(1)
        """
        new_node = Node(data)
        new_node.next = self.head
        self.head = new_node
        if self.tail is None:
            self.tail = new_node
        self.size += 1

    def insert_at_tail(self, data):
        """
        Insert a new node at the end of the list.
        Time: O(1)
        """
        new_node = Node(data)
        if self.tail is not None:
            self.tail.next = new_node
        else:
            self.head = new_node
        self.tail = new_node
        self.size += 1

    def search(self, target):
        """
        Search for a value in the list.
        Time: O(n)
        Returns True if found, False otherwise.
        """
        current = self.head
        while current is not None:
            if current.data == target:
                return True
            current = current.next
        return False

    def delete(self, target):
        """
        Delete the first occurrence of the given value.
        Time: O(n)
        Returns True if found and deleted, False otherwise.
        """
        if self.head is None:
            return False

        # Deleting the head node.
        if self.head.data == target:
            self.head = self.head.next
            if self.head is None:
                self.tail = None
            self.size -= 1
            return True

        # Find the node before the target.
        current = self.head
        while current.next is not None:
            if current.next.data == target:
                if current.next == self.tail:
                    self.tail = current
                current.next = current.next.next
                self.size -= 1
                return True
            current = current.next

        return False

    def print_list(self):
        """Print all elements in the list."""
        elements = []
        current = self.head
        while current is not None:
            elements.append(str(current.data))
            current = current.next
        print(" -> ".join(elements) + " -> None")


if __name__ == "__main__":
    ll = LinkedList()

    # Insert elements
    ll.insert_at_head(3)
    ll.insert_at_head(2)
    ll.insert_at_head(1)
    ll.insert_at_tail(4)
    ll.insert_at_tail(5)

    print("List: ", end="")
    ll.print_list()
    # Output: 1 -> 2 -> 3 -> 4 -> 5 -> None

    print(f"Size: {ll.size}")
    # Output: Size: 5

    # Search
    print(f"Search 3: {ll.search(3)}")    # True
    print(f"Search 99: {ll.search(99)}")  # False

    # Delete
    ll.delete(3)
    print("After deleting 3: ", end="")
    ll.print_list()

    ll.delete(1)
    print("After deleting 1 (head): ", end="")
    ll.print_list()

    ll.delete(5)
    print("After deleting 5 (tail): ", end="")
    ll.print_list()
```

---

## Common Mistakes

### 1. Forgetting to update the tail pointer

When you delete the tail node or insert into an empty list, you must update the tail pointer. Forgetting this leads to a dangling pointer.

### 2. Not handling the empty list case

Always check if `head == nil` before accessing `head.data` or `head.next`.

### 3. Losing references

When deleting a node, if you move your pointer past the node before saving a reference to the previous node, you lose the ability to re-link the chain.

```
# WRONG -- you've moved past the node you need
current = current.next
current.next = current.next.next  # skips wrong node!

# CORRECT -- save predecessor, then skip
if current.next.data == target:
    current.next = current.next.next
```

### 4. Off-by-one errors in traversal

Remember: if `current.next` is `nil`, you are at the last node. Do not try to access `current.next.next` without checking.

### 5. Not freeing memory (in non-GC languages)

In languages like C/C++, you must explicitly free deleted nodes. In Go, Java, and Python the garbage collector handles this, but be aware that in C you will create memory leaks if you just unlink nodes without freeing them.

---

## Practice Exercises

1. **Implement `insertAtPosition(index, data)`** -- Insert a node at a specific index (0-based). Handle edge cases: empty list, index 0, index equal to size.

2. **Implement `getAt(index)`** -- Return the data at the given index. Return -1 or raise an error for invalid indices.

3. **Implement `length()`** -- Count and return the number of nodes by traversing the list (without using the `size` field).

4. **Reverse print** -- Print the list in reverse order without modifying it. Hint: use recursion or a stack.

5. **Remove duplicates** -- Given a sorted linked list, remove all duplicate values so each value appears only once.

---

## Summary

| Concept                | Key Point                                    |
|------------------------|----------------------------------------------|
| Node                   | Holds data + pointer to next node            |
| Head                   | Pointer to the first node                    |
| Tail                   | Pointer to the last node (optional)          |
| Insert at head/tail    | O(1) -- constant time                        |
| Search                 | O(n) -- must traverse                        |
| Delete                 | O(n) -- must find predecessor                |
| Access by index        | O(n) -- no random access                     |
| vs Array               | Better insertion/deletion, worse random access|
| Memory                 | Each node has pointer overhead               |

**Next step:** Move to the middle level to learn about doubly linked lists, circular lists, and classic linked list algorithms like cycle detection and reversal.
