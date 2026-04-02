# Constant Time O(1) -- Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What Does O(1) Mean?](#what-does-o1-mean)
3. [Real-World Analogies](#real-world-analogies)
4. [Common O(1) Operations](#common-o1-operations)
   - [Array Access by Index](#array-access-by-index)
   - [Hash Table Lookup](#hash-table-lookup)
   - [Stack Push and Pop](#stack-push-and-pop)
   - [Arithmetic Operations](#arithmetic-operations)
   - [Linked List Insert at Head](#linked-list-insert-at-head)
5. [Misconception: O(1) Does NOT Mean One Operation](#misconception-o1-does-not-mean-one-operation)
6. [Visualizing Constant Time](#visualizing-constant-time)
7. [Code Examples](#code-examples)
8. [Comparison with Other Complexities](#comparison-with-other-complexities)
9. [Key Takeaways](#key-takeaways)
10. [Further Reading](#further-reading)

---

## Introduction

When we analyze algorithms, one of the most important questions we ask is: **how does
the running time grow as the input size increases?** The best possible answer to that
question is **constant time**, written as **O(1)**. An O(1) operation takes roughly the
same amount of time regardless of whether you are working with 10 items or 10 million
items.

Understanding O(1) is foundational. It is the gold standard of efficiency and appears
everywhere in computer science -- from accessing an element in an array to looking up
a key in a hash map.

---

## What Does O(1) Mean?

**O(1)** means that the time required to complete an operation does **not depend on the
size of the input**. More formally:

> An algorithm runs in O(1) time if there exists a constant `c` such that the algorithm
> completes in at most `c` steps for **any** input size `n`.

Key points:

- The "1" in O(1) is symbolic. It does **not** mean the operation takes exactly one step.
- It means the number of steps is **bounded by a constant** that never changes with `n`.
- Whether your dataset has 5 elements or 5 billion elements, the operation takes the
  same (bounded) amount of time.

### The Formal Intuition

If `T(n)` is the time an algorithm takes on input of size `n`, then:

```
T(n) = O(1)  means  T(n) <= c  for some constant c and all n >= n0
```

The time function is essentially a flat horizontal line on a graph.

---

## Real-World Analogies

### 1. Light Switch

Flipping a light switch takes the same amount of time whether your house has 1 room or
100 rooms. The operation itself is independent of the "size" of the house.

### 2. Looking at a Clock

Checking the time on a wall clock takes the same amount of time regardless of how many
people are in the room. You simply glance at it -- done.

### 3. Opening a Book to a Bookmarked Page

If you have a bookmark in a book, opening directly to that page takes the same time
whether the book has 100 pages or 1,000 pages. You are not scanning through pages; you
go directly to the marked location.

### 4. Pressing an Elevator Button

Pressing the button for floor 5 takes the same effort whether the building has 10 floors
or 50 floors. The action of pressing the button is constant.

### 5. Dictionary with Known Page Number

If someone tells you "the word is on page 342," you open directly to page 342. The
total number of pages in the dictionary does not matter.

---

## Common O(1) Operations

### Array Access by Index

The most classic example. Accessing `arr[i]` in an array is O(1) because arrays store
elements in **contiguous memory**. The memory address of element `i` is calculated as:

```
address = base_address + i * element_size
```

This is a single arithmetic calculation regardless of the array's length.

#### Go

```go
package main

import "fmt"

func main() {
    // Array access is O(1) -- same speed for any index
    arr := [10]int{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}

    // Accessing the first element: O(1)
    fmt.Println("First element:", arr[0])

    // Accessing the last element: O(1) -- same cost
    fmt.Println("Last element:", arr[9])

    // Accessing a middle element: O(1) -- still the same cost
    fmt.Println("Fifth element:", arr[4])

    // Slice access is also O(1)
    slice := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
    fmt.Println("Slice element:", slice[7])
}
```

#### Java

```java
public class ArrayAccess {
    public static void main(String[] args) {
        // Array access is O(1) -- same speed for any index
        int[] arr = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

        // Accessing the first element: O(1)
        System.out.println("First element: " + arr[0]);

        // Accessing the last element: O(1) -- same cost
        System.out.println("Last element: " + arr[9]);

        // Accessing a middle element: O(1) -- still the same cost
        System.out.println("Fifth element: " + arr[4]);

        // ArrayList get() is also O(1)
        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        for (int v : arr) list.add(v);
        System.out.println("ArrayList element: " + list.get(7));
    }
}
```

#### Python

```python
# Array (list) access is O(1) -- same speed for any index
arr = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]

# Accessing the first element: O(1)
print("First element:", arr[0])

# Accessing the last element: O(1) -- same cost
print("Last element:", arr[9])

# Accessing a middle element: O(1) -- still the same cost
print("Fifth element:", arr[4])

# Negative indexing is also O(1)
print("Last via negative index:", arr[-1])
```

---

### Hash Table Lookup

Hash tables provide **average-case O(1)** lookup. The key is passed through a hash
function, which computes an index directly.

#### Go

```go
package main

import "fmt"

func main() {
    // Map lookup is O(1) average case
    phonebook := map[string]string{
        "Alice":   "555-0101",
        "Bob":     "555-0102",
        "Charlie": "555-0103",
        "Diana":   "555-0104",
    }

    // Direct lookup -- O(1) regardless of map size
    if number, exists := phonebook["Bob"]; exists {
        fmt.Println("Bob's number:", number)
    }

    // Checking existence is also O(1)
    _, exists := phonebook["Eve"]
    fmt.Println("Eve exists:", exists)
}
```

#### Java

```java
import java.util.HashMap;

public class HashTableLookup {
    public static void main(String[] args) {
        // HashMap lookup is O(1) average case
        HashMap<String, String> phonebook = new HashMap<>();
        phonebook.put("Alice", "555-0101");
        phonebook.put("Bob", "555-0102");
        phonebook.put("Charlie", "555-0103");
        phonebook.put("Diana", "555-0104");

        // Direct lookup -- O(1) regardless of map size
        String number = phonebook.get("Bob");
        System.out.println("Bob's number: " + number);

        // Checking existence is also O(1)
        boolean exists = phonebook.containsKey("Eve");
        System.out.println("Eve exists: " + exists);
    }
}
```

#### Python

```python
# Dictionary lookup is O(1) average case
phonebook = {
    "Alice": "555-0101",
    "Bob": "555-0102",
    "Charlie": "555-0103",
    "Diana": "555-0104",
}

# Direct lookup -- O(1) regardless of dict size
print("Bob's number:", phonebook["Bob"])

# Checking existence with 'in' is also O(1)
print("Eve exists:", "Eve" in phonebook)

# .get() with default is O(1)
print("Eve's number:", phonebook.get("Eve", "not found"))
```

---

### Stack Push and Pop

A stack supports **push** (add to top) and **pop** (remove from top) in O(1). You only
ever interact with the top element.

#### Go

```go
package main

import "fmt"

func main() {
    // Using a slice as a stack
    stack := []int{}

    // Push: O(1) amortized
    stack = append(stack, 10)
    stack = append(stack, 20)
    stack = append(stack, 30)
    fmt.Println("Stack after pushes:", stack)

    // Pop: O(1)
    top := stack[len(stack)-1]
    stack = stack[:len(stack)-1]
    fmt.Println("Popped:", top)
    fmt.Println("Stack after pop:", stack)

    // Peek (look at top without removing): O(1)
    peek := stack[len(stack)-1]
    fmt.Println("Top element:", peek)
}
```

#### Java

```java
import java.util.Stack;

public class StackOperations {
    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();

        // Push: O(1) amortized
        stack.push(10);
        stack.push(20);
        stack.push(30);
        System.out.println("Stack after pushes: " + stack);

        // Pop: O(1)
        int top = stack.pop();
        System.out.println("Popped: " + top);
        System.out.println("Stack after pop: " + stack);

        // Peek (look at top without removing): O(1)
        int peek = stack.peek();
        System.out.println("Top element: " + peek);
    }
}
```

#### Python

```python
# Using a list as a stack
stack = []

# Push: O(1) amortized
stack.append(10)
stack.append(20)
stack.append(30)
print("Stack after pushes:", stack)

# Pop: O(1)
top = stack.pop()
print("Popped:", top)
print("Stack after pop:", stack)

# Peek (look at top without removing): O(1)
peek = stack[-1]
print("Top element:", peek)
```

---

### Arithmetic Operations

Basic arithmetic on fixed-size numbers (integers that fit in a machine word) is O(1).

#### Go

```go
package main

import "fmt"

func main() {
    a, b := 1000000, 2000000

    // All of these are O(1) for fixed-size integers
    sum := a + b
    diff := a - b
    product := a * b
    quotient := b / a
    remainder := b % a

    fmt.Println("Sum:", sum)
    fmt.Println("Difference:", diff)
    fmt.Println("Product:", product)
    fmt.Println("Quotient:", quotient)
    fmt.Println("Remainder:", remainder)

    // Bitwise operations are also O(1)
    fmt.Println("AND:", a & b)
    fmt.Println("OR:", a | b)
    fmt.Println("XOR:", a ^ b)
    fmt.Println("Left shift:", a << 2)
}
```

#### Java

```java
public class ArithmeticOps {
    public static void main(String[] args) {
        int a = 1000000, b = 2000000;

        // All of these are O(1) for fixed-size integers
        int sum = a + b;
        int diff = a - b;
        long product = (long) a * b;
        int quotient = b / a;
        int remainder = b % a;

        System.out.println("Sum: " + sum);
        System.out.println("Difference: " + diff);
        System.out.println("Product: " + product);
        System.out.println("Quotient: " + quotient);
        System.out.println("Remainder: " + remainder);

        // Bitwise operations are also O(1)
        System.out.println("AND: " + (a & b));
        System.out.println("OR: " + (a | b));
        System.out.println("XOR: " + (a ^ b));
        System.out.println("Left shift: " + (a << 2));
    }
}
```

#### Python

```python
a, b = 1000000, 2000000

# All of these are O(1) for fixed-size integers
# Note: Python integers can be arbitrary precision, so very large
# numbers may not be strictly O(1). For typical sizes, treat as O(1).
print("Sum:", a + b)
print("Difference:", a - b)
print("Product:", a * b)
print("Quotient:", b // a)
print("Remainder:", b % a)

# Bitwise operations are also O(1) for typical integer sizes
print("AND:", a & b)
print("OR:", a | b)
print("XOR:", a ^ b)
print("Left shift:", a << 2)
```

---

### Linked List Insert at Head

Inserting a node at the beginning of a linked list is O(1) because you only update a
pointer -- you never traverse the list.

#### Go

```go
package main

import "fmt"

type Node struct {
    Value int
    Next  *Node
}

type LinkedList struct {
    Head *Node
}

// InsertAtHead is O(1) -- no traversal needed
func (ll *LinkedList) InsertAtHead(value int) {
    newNode := &Node{Value: value, Next: ll.Head}
    ll.Head = newNode
}

func (ll *LinkedList) Print() {
    current := ll.Head
    for current != nil {
        fmt.Printf("%d -> ", current.Value)
        current = current.Next
    }
    fmt.Println("nil")
}

func main() {
    list := &LinkedList{}

    // Each insert is O(1) regardless of list size
    list.InsertAtHead(30)
    list.InsertAtHead(20)
    list.InsertAtHead(10)

    list.Print() // 10 -> 20 -> 30 -> nil
}
```

#### Java

```java
public class LinkedListInsert {
    static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    static class SinglyLinkedList {
        Node head;

        // insertAtHead is O(1) -- no traversal needed
        void insertAtHead(int value) {
            Node newNode = new Node(value);
            newNode.next = head;
            head = newNode;
        }

        void print() {
            Node current = head;
            while (current != null) {
                System.out.print(current.value + " -> ");
                current = current.next;
            }
            System.out.println("null");
        }
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();

        // Each insert is O(1) regardless of list size
        list.insertAtHead(30);
        list.insertAtHead(20);
        list.insertAtHead(10);

        list.print(); // 10 -> 20 -> 30 -> null
    }
}
```

#### Python

```python
class Node:
    def __init__(self, value):
        self.value = value
        self.next = None

class LinkedList:
    def __init__(self):
        self.head = None

    def insert_at_head(self, value):
        """O(1) -- no traversal needed."""
        new_node = Node(value)
        new_node.next = self.head
        self.head = new_node

    def __str__(self):
        parts = []
        current = self.head
        while current:
            parts.append(str(current.value))
            current = current.next
        return " -> ".join(parts) + " -> None"

linked_list = LinkedList()

# Each insert is O(1) regardless of list size
linked_list.insert_at_head(30)
linked_list.insert_at_head(20)
linked_list.insert_at_head(10)

print(linked_list)  # 10 -> 20 -> 30 -> None
```

---

## Misconception: O(1) Does NOT Mean One Operation

This is the single most common misunderstanding among beginners. Let us be clear:

**O(1) means the number of operations is bounded by a constant, NOT that it equals 1.**

An operation that always performs exactly 100 steps is still O(1).
An operation that always performs exactly 1,000,000 steps is still O(1).

What matters is that the number of steps does **not grow** as the input grows.

### Example: An O(1) Function That Does Many Things

#### Go

```go
// This function is O(1) even though it performs multiple operations.
// The number of operations is fixed (does not depend on any input size).
func processRecord(x int) int {
    result := x * 2       // step 1
    result = result + 10   // step 2
    result = result / 3    // step 3
    result = result % 7    // step 4
    result = result << 1   // step 5
    result = result | 0x0F // step 6
    return result
}
```

#### Java

```java
// This function is O(1) even though it performs multiple operations.
// The number of operations is fixed (does not depend on any input size).
static int processRecord(int x) {
    int result = x * 2;       // step 1
    result = result + 10;     // step 2
    result = result / 3;      // step 3
    result = result % 7;      // step 4
    result = result << 1;     // step 5
    result = result | 0x0F;   // step 6
    return result;
}
```

#### Python

```python
# This function is O(1) even though it performs multiple operations.
# The number of operations is fixed (does not depend on any input size).
def process_record(x):
    result = x * 2       # step 1
    result = result + 10  # step 2
    result = result // 3  # step 3
    result = result % 7   # step 4
    result = result << 1  # step 5
    result = result | 0x0F  # step 6
    return result
```

All three versions perform exactly 6 arithmetic operations every time, regardless of
the value of `x`. That is O(1).

---

## Visualizing Constant Time

Imagine a graph where the x-axis is input size `n` and the y-axis is time:

```
Time
 |
 |  ___________________________________  O(1)
 |
 |                              /  O(n)
 |                           /
 |                        /
 |                     /
 |                  /
 |               /
 |            /
 |         /
 |      /
 |   /
 |_/_________________________________________ n
```

The O(1) line is perfectly flat. No matter how far right you go (bigger input), the
time stays the same. The O(n) line rises steadily. This visual difference is the core
idea behind why O(1) operations are so desirable.

---

## Comparison with Other Complexities

| Complexity | n=10 | n=1,000 | n=1,000,000 | Description |
|-----------|------|---------|-------------|-------------|
| O(1) | 1 | 1 | 1 | Constant |
| O(log n) | ~3 | ~10 | ~20 | Logarithmic |
| O(n) | 10 | 1,000 | 1,000,000 | Linear |
| O(n log n) | ~33 | ~10,000 | ~20,000,000 | Linearithmic |
| O(n^2) | 100 | 1,000,000 | 10^12 | Quadratic |

As you can see, O(1) stays at 1 while others grow dramatically.

---

## Summary of O(1) Operations You Should Know

| Data Structure | O(1) Operation | Notes |
|---------------|---------------|-------|
| Array / List | Access by index | `arr[i]` |
| Array / List | Get length | `len(arr)` |
| Hash Map | Insert | Average case |
| Hash Map | Lookup | Average case |
| Hash Map | Delete | Average case |
| Stack | Push | Top of stack |
| Stack | Pop | Top of stack |
| Stack | Peek | Top of stack |
| Queue (deque) | Enqueue | At tail |
| Queue (deque) | Dequeue | From head |
| Linked List | Insert at head | With head pointer |
| Linked List | Remove head | With head pointer |
| Bit operations | AND, OR, XOR, shift | Fixed-size integers |
| Variables | Assignment | Single value |
| Variables | Comparison | Single value |

---

## Key Takeaways

1. **O(1) means constant time** -- the operation takes a fixed amount of time regardless
   of input size.

2. **O(1) does NOT mean one step** -- it means the number of steps is bounded by a
   constant.

3. **Array access by index is the classic O(1) example** because address calculation
   is a simple formula.

4. **Hash table operations are O(1) on average** -- this is why hash maps are used so
   heavily in practice.

5. **Stack push and pop are O(1)** because they only touch the top element.

6. **O(1) is the best time complexity** you can achieve. When designing algorithms,
   always look for ways to achieve constant-time operations.

7. **Not everything can be O(1)** -- searching an unsorted array, for example, requires
   looking at every element (O(n)). Recognizing what can and cannot be O(1) is a key
   skill.

---

## Further Reading

- [02-middle.md](middle.md) -- Amortized O(1), expected O(1), and deeper analysis.
- [03-senior.md](senior.md) -- O(1) in distributed systems and lock-free programming.
- [04-professional.md](professional.md) -- Formal proofs and perfect hashing.
- [05-interview.md](interview.md) -- Interview questions on O(1) operations.
