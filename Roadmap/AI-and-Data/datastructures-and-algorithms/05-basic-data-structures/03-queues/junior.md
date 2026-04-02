# Queue -- Junior Level

## Table of Contents

- [Introduction](#introduction)
- [What Is a Queue?](#what-is-a-queue)
- [Real-World Analogies](#real-world-analogies)
- [How Queues Work in Memory](#how-queues-work-in-memory)
- [Core Operations and Their Complexity](#core-operations-and-their-complexity)
  - [Enqueue (Add to Rear)](#enqueue-add-to-rear)
  - [Dequeue (Remove from Front)](#dequeue-remove-from-front)
  - [Peek (View Front)](#peek-view-front)
  - [IsEmpty](#isempty)
  - [Size](#size)
- [Array-Based Queue Implementation](#array-based-queue-implementation)
  - [Go: Array-Based Queue](#go-array-based-queue)
  - [Java: Array-Based Queue](#java-array-based-queue)
  - [Python: Array-Based Queue](#python-array-based-queue)
- [Linked List-Based Queue Implementation](#linked-list-based-queue-implementation)
  - [Go: Linked List Queue](#go-linked-list-queue)
  - [Java: Linked List Queue](#java-linked-list-queue)
  - [Python: Linked List Queue](#python-linked-list-queue)
- [Circular Queue](#circular-queue)
  - [Why Circular Queue?](#why-circular-queue)
  - [Go: Circular Queue](#go-circular-queue)
  - [Java: Circular Queue](#java-circular-queue)
  - [Python: Circular Queue](#python-circular-queue)
- [Queue vs Stack](#queue-vs-stack)
- [Common Mistakes](#common-mistakes)
- [Summary](#summary)

---

## Introduction

The **queue** is one of the most fundamental data structures in computer science. It models real-world scenarios where things are processed in the order they arrive. Queues appear everywhere: operating system task schedulers, network packet processing, print spoolers, and web server request handling.

This document covers everything a junior developer needs to know about queues: the FIFO principle, all core operations, and three different implementation strategies with full code in Go, Java, and Python.

---

## What Is a Queue?

A queue is a **linear data structure** that follows the **FIFO (First-In, First-Out)** principle. The element that is added first is the first one to be removed.

Key properties of a queue:

| Property          | Description                                                 |
| ----------------- | ----------------------------------------------------------- |
| FIFO ordering     | First element added is the first element removed            |
| Two access points | Elements enter at the **rear** and leave from the **front** |
| Linear structure  | Elements are arranged in a sequence                         |
| Restricted access | You can only add at the rear and remove from the front      |

Think of it like a one-way tunnel: elements go in one end and come out the other, in the same order.

```
  Enqueue (rear)                     Dequeue (front)
       |                                   |
       v                                   v
  [  ] [  ] [  ] [  ] [  ] [  ] ---> [  ] --->
       ^                               ^
      REAR                           FRONT
```

---

## Real-World Analogies

**1. Waiting in line at a grocery store**
The first person to join the line is the first person to be served. New customers join at the back of the line. No one can skip ahead. This is exactly FIFO.

**2. Printer queue**
When multiple documents are sent to a printer, they are printed in the order they were submitted. The first document sent is printed first. If you send a 50-page report and then someone sends a 1-page letter, the report prints first.

**3. Ticket counter at a train station**
People form a line. The person who arrived first gets their ticket first. New arrivals go to the end of the line. The counter serves from the front.

**4. Fast-food drive-through**
Cars enter the drive-through lane in order. The first car to arrive is the first to receive food and leave. Cars cannot overtake each other.

**5. Conveyor belt in a factory**
Items placed on the belt first reach the end first. New items are placed at the start. The belt processes items in strict FIFO order.

---

## How Queues Work in Memory

There are two main ways to implement a queue in memory:

### Array-Based Queue

```
Array:   [ 10 | 20 | 30 | 40 | 50 |    |    |    ]
           ^                   ^
         front                rear
Index:     0    1    2    3    4    5    6    7
```

When you **dequeue**, the front pointer moves right. When you **enqueue**, the rear pointer moves right.

Problem: after many enqueue/dequeue operations, the front of the array has unused space. This is why we use **circular queues** (covered later).

### Linked List-Based Queue

```
front -> [10 | *] -> [20 | *] -> [30 | *] -> [40 | null]  <- rear
```

Each node points to the next. Enqueue adds a new node after rear. Dequeue removes the node at front. No wasted space, but each node requires extra memory for the pointer.

---

## Core Operations and Their Complexity

| Operation  | Description                     | Time Complexity | Space Complexity |
| ---------- | ------------------------------- | --------------- | ---------------- |
| Enqueue    | Add element to the rear         | O(1)            | O(1)             |
| Dequeue    | Remove element from the front   | O(1)            | O(1)             |
| Peek/Front | View the front element          | O(1)            | O(1)             |
| IsEmpty    | Check if queue is empty         | O(1)            | O(1)             |
| Size       | Return number of elements       | O(1)            | O(1)             |

All core queue operations are **O(1)**. This is what makes queues efficient.

### Enqueue (Add to Rear)

Adds an element to the back of the queue.

```
Before: front -> [A] [B] [C] <- rear
Enqueue(D)
After:  front -> [A] [B] [C] [D] <- rear
```

### Dequeue (Remove from Front)

Removes and returns the element at the front of the queue.

```
Before:  front -> [A] [B] [C] [D] <- rear
Dequeue() returns A
After:   front -> [B] [C] [D] <- rear
```

### Peek (View Front)

Returns the front element without removing it.

```
Queue: front -> [A] [B] [C] <- rear
Peek() returns A
Queue unchanged: front -> [A] [B] [C] <- rear
```

### IsEmpty

Returns true if the queue has no elements.

### Size

Returns the number of elements currently in the queue.

---

## Array-Based Queue Implementation

### Go: Array-Based Queue

```go
package main

import (
	"errors"
	"fmt"
)

type ArrayQueue struct {
	data []int
}

func NewArrayQueue() *ArrayQueue {
	return &ArrayQueue{
		data: make([]int, 0),
	}
}

func (q *ArrayQueue) Enqueue(val int) {
	q.data = append(q.data, val)
}

func (q *ArrayQueue) Dequeue() (int, error) {
	if q.IsEmpty() {
		return 0, errors.New("queue is empty")
	}
	val := q.data[0]
	q.data = q.data[1:]
	return val, nil
}

func (q *ArrayQueue) Peek() (int, error) {
	if q.IsEmpty() {
		return 0, errors.New("queue is empty")
	}
	return q.data[0], nil
}

func (q *ArrayQueue) IsEmpty() bool {
	return len(q.data) == 0
}

func (q *ArrayQueue) Size() int {
	return len(q.data)
}

func main() {
	q := NewArrayQueue()

	q.Enqueue(10)
	q.Enqueue(20)
	q.Enqueue(30)

	fmt.Println("Size:", q.Size())       // 3
	fmt.Println("Front:", mustPeek(q))    // 10

	val, _ := q.Dequeue()
	fmt.Println("Dequeued:", val)         // 10
	fmt.Println("New front:", mustPeek(q)) // 20
	fmt.Println("Size:", q.Size())        // 2
}

func mustPeek(q *ArrayQueue) int {
	val, _ := q.Peek()
	return val
}
```

**Note:** This simple version has an issue -- `q.data = q.data[1:]` does not free the memory at the start. For a production queue, use a circular buffer (covered below).

### Java: Array-Based Queue

```java
import java.util.NoSuchElementException;

public class ArrayQueue {
    private int[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public void enqueue(int val) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        rear++;
        data[rear] = val;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        int val = data[front];
        front++;
        size--;
        return val;
    }

    public int peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return data[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void resize(int newCapacity) {
        int[] newData = new int[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[front + i];
        }
        data = newData;
        front = 0;
        rear = size - 1;
        capacity = newCapacity;
    }

    public static void main(String[] args) {
        ArrayQueue q = new ArrayQueue(4);

        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);

        System.out.println("Size: " + q.size());     // 3
        System.out.println("Front: " + q.peek());     // 10

        int val = q.dequeue();
        System.out.println("Dequeued: " + val);        // 10
        System.out.println("New front: " + q.peek());  // 20
        System.out.println("Size: " + q.size());       // 2
    }
}
```

### Python: Array-Based Queue

```python
class ArrayQueue:
    def __init__(self):
        self._data = []

    def enqueue(self, val):
        """Add element to the rear of the queue."""
        self._data.append(val)

    def dequeue(self):
        """Remove and return the front element."""
        if self.is_empty():
            raise IndexError("Queue is empty")
        return self._data.pop(0)  # O(n) -- see note below

    def peek(self):
        """Return the front element without removing it."""
        if self.is_empty():
            raise IndexError("Queue is empty")
        return self._data[0]

    def is_empty(self):
        """Check if the queue is empty."""
        return len(self._data) == 0

    def size(self):
        """Return the number of elements."""
        return len(self._data)

    def __str__(self):
        return f"Queue(front={self._data})"


# Usage
q = ArrayQueue()
q.enqueue(10)
q.enqueue(20)
q.enqueue(30)

print(f"Size: {q.size()}")       # 3
print(f"Front: {q.peek()}")      # 10

val = q.dequeue()
print(f"Dequeued: {val}")         # 10
print(f"New front: {q.peek()}")   # 20
print(f"Size: {q.size()}")        # 2
```

**Warning:** In Python, `list.pop(0)` is O(n) because all remaining elements must shift left. For an efficient queue in Python, use `collections.deque` (covered in the specification document).

---

## Linked List-Based Queue Implementation

A linked list queue solves the wasted-space problem of the array-based approach. Both enqueue and dequeue are true O(1) with no amortization.

### Go: Linked List Queue

```go
package main

import (
	"errors"
	"fmt"
)

type Node struct {
	val  int
	next *Node
}

type LinkedQueue struct {
	front *Node
	rear  *Node
	size  int
}

func NewLinkedQueue() *LinkedQueue {
	return &LinkedQueue{}
}

func (q *LinkedQueue) Enqueue(val int) {
	newNode := &Node{val: val}
	if q.rear != nil {
		q.rear.next = newNode
	}
	q.rear = newNode
	if q.front == nil {
		q.front = newNode
	}
	q.size++
}

func (q *LinkedQueue) Dequeue() (int, error) {
	if q.IsEmpty() {
		return 0, errors.New("queue is empty")
	}
	val := q.front.val
	q.front = q.front.next
	if q.front == nil {
		q.rear = nil
	}
	q.size--
	return val, nil
}

func (q *LinkedQueue) Peek() (int, error) {
	if q.IsEmpty() {
		return 0, errors.New("queue is empty")
	}
	return q.front.val, nil
}

func (q *LinkedQueue) IsEmpty() bool {
	return q.size == 0
}

func (q *LinkedQueue) Size() int {
	return q.size
}

func main() {
	q := NewLinkedQueue()

	q.Enqueue(10)
	q.Enqueue(20)
	q.Enqueue(30)

	fmt.Println("Size:", q.Size()) // 3

	val, _ := q.Dequeue()
	fmt.Println("Dequeued:", val) // 10

	front, _ := q.Peek()
	fmt.Println("Front:", front) // 20
}
```

### Java: Linked List Queue

```java
import java.util.NoSuchElementException;

public class LinkedQueue {
    private static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    private Node front;
    private Node rear;
    private int size;

    public LinkedQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    public void enqueue(int val) {
        Node newNode = new Node(val);
        if (rear != null) {
            rear.next = newNode;
        }
        rear = newNode;
        if (front == null) {
            front = newNode;
        }
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        int val = front.val;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return val;
    }

    public int peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return front.val;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        LinkedQueue q = new LinkedQueue();

        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);

        System.out.println("Size: " + q.size());       // 3
        System.out.println("Dequeued: " + q.dequeue()); // 10
        System.out.println("Front: " + q.peek());       // 20
    }
}
```

### Python: Linked List Queue

```python
class Node:
    def __init__(self, val):
        self.val = val
        self.next = None


class LinkedQueue:
    def __init__(self):
        self._front = None
        self._rear = None
        self._size = 0

    def enqueue(self, val):
        """Add element to the rear of the queue. O(1)."""
        new_node = Node(val)
        if self._rear is not None:
            self._rear.next = new_node
        self._rear = new_node
        if self._front is None:
            self._front = new_node
        self._size += 1

    def dequeue(self):
        """Remove and return the front element. O(1)."""
        if self.is_empty():
            raise IndexError("Queue is empty")
        val = self._front.val
        self._front = self._front.next
        if self._front is None:
            self._rear = None
        self._size -= 1
        return val

    def peek(self):
        """Return the front element without removing it. O(1)."""
        if self.is_empty():
            raise IndexError("Queue is empty")
        return self._front.val

    def is_empty(self):
        return self._size == 0

    def size(self):
        return self._size


# Usage
q = LinkedQueue()
q.enqueue(10)
q.enqueue(20)
q.enqueue(30)

print(f"Size: {q.size()}")        # 3
print(f"Dequeued: {q.dequeue()}")  # 10
print(f"Front: {q.peek()}")       # 20
```

---

## Circular Queue

### Why Circular Queue?

In a basic array queue, once we dequeue elements, the space at the beginning of the array is wasted. We could shift all elements left on every dequeue, but that would make dequeue O(n).

A **circular queue** (ring buffer) solves this by treating the array as if it wraps around. When the rear pointer reaches the end of the array, it wraps back to index 0 (if that space is available).

```
Logical view:
  [ ] [20] [30] [40] [  ] [  ]
        ^              ^
      front          rear

After enqueue(50), enqueue(60), enqueue(70):
  [70] [20] [30] [40] [50] [60]
    ^    ^
  rear  front

The array "wraps around" -- rear is before front.
```

The wrap-around is computed with modulo: `index = (index + 1) % capacity`

### Go: Circular Queue

```go
package main

import (
	"errors"
	"fmt"
)

type CircularQueue struct {
	data     []int
	front    int
	rear     int
	size     int
	capacity int
}

func NewCircularQueue(capacity int) *CircularQueue {
	return &CircularQueue{
		data:     make([]int, capacity),
		front:    0,
		rear:     -1,
		size:     0,
		capacity: capacity,
	}
}

func (q *CircularQueue) Enqueue(val int) error {
	if q.IsFull() {
		return errors.New("queue is full")
	}
	q.rear = (q.rear + 1) % q.capacity
	q.data[q.rear] = val
	q.size++
	return nil
}

func (q *CircularQueue) Dequeue() (int, error) {
	if q.IsEmpty() {
		return 0, errors.New("queue is empty")
	}
	val := q.data[q.front]
	q.front = (q.front + 1) % q.capacity
	q.size--
	return val, nil
}

func (q *CircularQueue) Peek() (int, error) {
	if q.IsEmpty() {
		return 0, errors.New("queue is empty")
	}
	return q.data[q.front], nil
}

func (q *CircularQueue) IsEmpty() bool {
	return q.size == 0
}

func (q *CircularQueue) IsFull() bool {
	return q.size == q.capacity
}

func (q *CircularQueue) Size() int {
	return q.size
}

func main() {
	q := NewCircularQueue(5)

	q.Enqueue(10)
	q.Enqueue(20)
	q.Enqueue(30)
	q.Enqueue(40)
	q.Enqueue(50)

	fmt.Println("Full?", q.IsFull()) // true

	val, _ := q.Dequeue()
	fmt.Println("Dequeued:", val) // 10

	q.Enqueue(60) // wraps around to index 0
	fmt.Println("Size:", q.Size()) // 5

	front, _ := q.Peek()
	fmt.Println("Front:", front) // 20
}
```

### Java: Circular Queue

```java
import java.util.NoSuchElementException;

public class CircularQueue {
    private int[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public void enqueue(int val) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }
        rear = (rear + 1) % capacity;
        data[rear] = val;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        int val = data[front];
        front = (front + 1) % capacity;
        size--;
        return val;
    }

    public int peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return data[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        CircularQueue q = new CircularQueue(5);

        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.enqueue(40);
        q.enqueue(50);

        System.out.println("Full? " + q.isFull());      // true

        int val = q.dequeue();
        System.out.println("Dequeued: " + val);           // 10

        q.enqueue(60);  // wraps around
        System.out.println("Size: " + q.size());          // 5
        System.out.println("Front: " + q.peek());         // 20
    }
}
```

### Python: Circular Queue

```python
class CircularQueue:
    def __init__(self, capacity):
        self._data = [None] * capacity
        self._front = 0
        self._rear = -1
        self._size = 0
        self._capacity = capacity

    def enqueue(self, val):
        """Add element to the rear. O(1)."""
        if self.is_full():
            raise OverflowError("Queue is full")
        self._rear = (self._rear + 1) % self._capacity
        self._data[self._rear] = val
        self._size += 1

    def dequeue(self):
        """Remove and return the front element. O(1)."""
        if self.is_empty():
            raise IndexError("Queue is empty")
        val = self._data[self._front]
        self._data[self._front] = None  # help garbage collection
        self._front = (self._front + 1) % self._capacity
        self._size -= 1
        return val

    def peek(self):
        """Return the front element without removing it. O(1)."""
        if self.is_empty():
            raise IndexError("Queue is empty")
        return self._data[self._front]

    def is_empty(self):
        return self._size == 0

    def is_full(self):
        return self._size == self._capacity

    def size(self):
        return self._size


# Usage
q = CircularQueue(5)

q.enqueue(10)
q.enqueue(20)
q.enqueue(30)
q.enqueue(40)
q.enqueue(50)

print(f"Full? {q.is_full()}")       # True

val = q.dequeue()
print(f"Dequeued: {val}")            # 10

q.enqueue(60)  # wraps around to index 0
print(f"Size: {q.size()}")           # 5
print(f"Front: {q.peek()}")          # 20
```

---

## Queue vs Stack

| Feature           | Queue                   | Stack                   |
| ----------------- | ----------------------- | ----------------------- |
| Ordering          | FIFO (First-In-First-Out) | LIFO (Last-In-First-Out) |
| Add operation     | Enqueue (rear)          | Push (top)              |
| Remove operation  | Dequeue (front)         | Pop (top)               |
| Access point      | Two ends (front + rear) | One end (top)           |
| Analogy           | Line at a store         | Stack of plates         |
| Use cases         | BFS, scheduling, buffers | DFS, undo, parsing     |

---

## Common Mistakes

**1. Forgetting to check if the queue is empty before dequeue/peek**
Always check `isEmpty()` before calling `dequeue()` or `peek()`. Dequeuing from an empty queue causes panics, exceptions, or undefined behavior.

**2. Using Python list.pop(0) as a queue**
`list.pop(0)` is O(n) because all elements shift left. Use `collections.deque` for O(1) operations on both ends.

**3. Not handling wrap-around in circular queue**
Forgetting the modulo operation `% capacity` causes index-out-of-bounds errors. The wrap-around formula is the core of circular queue correctness.

**4. Memory leak in array-based queue (Go/Java)**
When dequeuing from a slice-based queue in Go (`q.data = q.data[1:]`), the underlying array still holds references to dequeued elements. The garbage collector cannot free them. Use a circular buffer or explicitly nil out dequeued positions.

**5. Confusing full and empty states in circular queue**
If you track full/empty by `front == rear`, an empty queue and a full queue look the same. Solution: track `size` separately (as shown in implementations above), or sacrifice one slot.

**6. Off-by-one with rear initialization**
If `rear` starts at 0, the first enqueue puts the element at index 1 (after `rear = (rear + 1) % capacity`). If `rear` starts at -1, the first element goes to index 0. Be consistent.

---

## Summary

| Concept                    | Key Takeaway                                                |
| -------------------------- | ----------------------------------------------------------- |
| FIFO principle             | First element added is first removed                        |
| Enqueue                    | Add to rear -- O(1)                                         |
| Dequeue                    | Remove from front -- O(1)                                   |
| Peek                       | View front element without removal -- O(1)                  |
| Array-based queue          | Simple but wastes space at front after dequeues             |
| Linked list queue          | No wasted space, true O(1) for all operations               |
| Circular queue             | Array-based with wrap-around, no wasted space, fixed size   |
| Python gotcha              | `list.pop(0)` is O(n) -- use `collections.deque` instead   |
| Empty check                | Always check before dequeue/peek                            |

Next steps: move on to the **middle level** to learn about deques, priority queues, BFS, monotonic queues, and the producer-consumer pattern.
