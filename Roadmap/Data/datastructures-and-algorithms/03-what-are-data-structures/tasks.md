# What are Data Structures? — Practical Tasks

## Table of Contents

1. [Beginner Tasks](#beginner-tasks)
   - [Task 1: Dynamic Array](#task-1-dynamic-array)
   - [Task 2: Singly Linked List](#task-2-singly-linked-list)
   - [Task 3: Stack](#task-3-stack)
   - [Task 4: Queue](#task-4-queue)
   - [Task 5: Array vs Linked List Performance](#task-5-array-vs-linked-list-performance)
2. [Intermediate Tasks](#intermediate-tasks)
   - [Task 6: Deque](#task-6-deque)
   - [Task 7: Circular Buffer](#task-7-circular-buffer)
   - [Task 8: Frequency Count](#task-8-frequency-count)
   - [Task 9: Two Stacks in One Array](#task-9-two-stacks-in-one-array)
   - [Task 10: Reverse Linked List](#task-10-reverse-linked-list)
3. [Advanced Tasks](#advanced-tasks)
   - [Task 11: LRU Cache](#task-11-lru-cache)
   - [Task 12: Self-Resizing Hash Table](#task-12-self-resizing-hash-table)
   - [Task 13: Insert/Delete/GetRandom O(1)](#task-13-insertdeletegetrandom-o1)
   - [Task 14: Min-Heap](#task-14-min-heap)
   - [Task 15: Trie (Prefix Tree)](#task-15-trie-prefix-tree)
4. [Benchmark Task](#benchmark-task)

---

## Beginner Tasks

### Task 1: Dynamic Array

**Objective:** Implement a dynamic array from scratch that automatically resizes when full.

**Requirements:**
- `append(val)` — Add element to the end. Amortized O(1).
- `get(index)` — Access element by index. O(1).
- `set(index, val)` — Update element at index. O(1).
- `size()` — Return current number of elements. O(1).
- `remove_last()` — Remove and return the last element. O(1).
- Start with capacity 4. Double when full. Halve when size drops below 1/4 capacity.

**Starter Code:**

**Go:**

```go
package main

import "fmt"

type DynamicArray struct {
    data     []int
    count    int
    capacity int
}

func NewDynamicArray() *DynamicArray {
    return &DynamicArray{
        data:     make([]int, 4),
        count:    0,
        capacity: 4,
    }
}

func (a *DynamicArray) Append(val int) {
    // TODO: If count == capacity, double the capacity (allocate new slice, copy)
    // Then set data[count] = val and increment count
}

func (a *DynamicArray) Get(index int) int {
    // TODO: Check bounds, return data[index]
    return 0
}

func (a *DynamicArray) Set(index int, val int) {
    // TODO: Check bounds, set data[index] = val
}

func (a *DynamicArray) Size() int {
    // TODO
    return 0
}

func (a *DynamicArray) RemoveLast() int {
    // TODO: Decrement count, optionally shrink if count < capacity/4
    return 0
}

func main() {
    arr := NewDynamicArray()
    for i := 0; i < 20; i++ {
        arr.Append(i * 10)
    }
    fmt.Println("Size:", arr.Size())
    fmt.Println("Element at 5:", arr.Get(5))
}
```

**Java:**

```java
public class DynamicArray {
    private int[] data;
    private int count;
    private int capacity;

    public DynamicArray() {
        capacity = 4;
        data = new int[capacity];
        count = 0;
    }

    public void append(int val) {
        // TODO: If count == capacity, double capacity (new array, copy)
        // Then set data[count] = val and increment count
    }

    public int get(int index) {
        // TODO: Check bounds, return data[index]
        return 0;
    }

    public void set(int index, int val) {
        // TODO: Check bounds, set data[index] = val
    }

    public int size() {
        // TODO
        return 0;
    }

    public int removeLast() {
        // TODO: Decrement count, optionally shrink
        return 0;
    }

    public static void main(String[] args) {
        DynamicArray arr = new DynamicArray();
        for (int i = 0; i < 20; i++) {
            arr.append(i * 10);
        }
        System.out.println("Size: " + arr.size());
        System.out.println("Element at 5: " + arr.get(5));
    }
}
```

**Python:**

```python
class DynamicArray:
    def __init__(self):
        self._capacity = 4
        self._data = [None] * self._capacity
        self._count = 0

    def append(self, val):
        # TODO: If count == capacity, double capacity (new list, copy)
        # Then set data[count] = val and increment count
        pass

    def get(self, index):
        # TODO: Check bounds, return data[index]
        pass

    def set(self, index, val):
        # TODO: Check bounds, set data[index] = val
        pass

    def size(self):
        # TODO
        return 0

    def remove_last(self):
        # TODO: Decrement count, optionally shrink
        pass


if __name__ == "__main__":
    arr = DynamicArray()
    for i in range(20):
        arr.append(i * 10)
    print("Size:", arr.size())
    print("Element at 5:", arr.get(5))
```

---

### Task 2: Singly Linked List

**Objective:** Implement a singly linked list with core operations.

**Requirements:**
- `push_front(val)` — Insert at head. O(1).
- `push_back(val)` — Insert at tail. O(1) with tail pointer.
- `pop_front()` — Remove and return head value. O(1).
- `find(val)` — Return true if value exists. O(n).
- `delete(val)` — Remove first occurrence. O(n).
- `size()` — Return element count. O(1).
- `to_list()` — Return all elements as an array.

**Starter Code:**

**Go:**

```go
package main

import "fmt"

type Node struct {
    Val  int
    Next *Node
}

type LinkedList struct {
    Head  *Node
    Tail  *Node
    Count int
}

func (ll *LinkedList) PushFront(val int) {
    // TODO: Create node, set next to head, update head. If empty, update tail.
}

func (ll *LinkedList) PushBack(val int) {
    // TODO: Create node, set tail.next to it, update tail. If empty, update head.
}

func (ll *LinkedList) PopFront() (int, bool) {
    // TODO: Return head value, advance head. If now empty, set tail to nil.
    return 0, false
}

func (ll *LinkedList) Find(val int) bool {
    // TODO: Traverse from head, return true if found
    return false
}

func (ll *LinkedList) Delete(val int) bool {
    // TODO: Find node with val, update previous node's next pointer
    return false
}

func (ll *LinkedList) Size() int {
    return ll.Count
}

func (ll *LinkedList) ToSlice() []int {
    // TODO: Traverse and collect values
    return nil
}

func main() {
    ll := &LinkedList{}
    ll.PushBack(10)
    ll.PushBack(20)
    ll.PushBack(30)
    ll.PushFront(5)
    fmt.Println(ll.ToSlice())
}
```

**Java:**

```java
public class SinglyLinkedList {
    private static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    private Node head, tail;
    private int count;

    public void pushFront(int val) {
        // TODO
    }

    public void pushBack(int val) {
        // TODO
    }

    public int popFront() {
        // TODO
        return 0;
    }

    public boolean find(int val) {
        // TODO
        return false;
    }

    public boolean delete(int val) {
        // TODO
        return false;
    }

    public int size() {
        return count;
    }

    public static void main(String[] args) {
        SinglyLinkedList ll = new SinglyLinkedList();
        ll.pushBack(10);
        ll.pushBack(20);
        ll.pushBack(30);
        ll.pushFront(5);
        System.out.println("Size: " + ll.size());
    }
}
```

**Python:**

```python
class Node:
    def __init__(self, val):
        self.val = val
        self.next = None

class SinglyLinkedList:
    def __init__(self):
        self.head = None
        self.tail = None
        self.count = 0

    def push_front(self, val):
        # TODO
        pass

    def push_back(self, val):
        # TODO
        pass

    def pop_front(self):
        # TODO
        pass

    def find(self, val):
        # TODO
        return False

    def delete(self, val):
        # TODO
        return False

    def size(self):
        return self.count

    def to_list(self):
        # TODO: traverse and collect
        return []


if __name__ == "__main__":
    ll = SinglyLinkedList()
    ll.push_back(10)
    ll.push_back(20)
    ll.push_back(30)
    ll.push_front(5)
    print(ll.to_list())
```

---

### Task 3: Stack

**Objective:** Implement a stack using an array (not using built-in stack classes).

**Requirements:**
- `push(val)` — O(1).
- `pop()` — O(1). Raise error if empty.
- `peek()` — O(1). Raise error if empty.
- `is_empty()` — O(1).
- `size()` — O(1).

**Starter Code:**

**Go:**

```go
package main

import (
    "errors"
    "fmt"
)

type Stack struct {
    items []int
}

func (s *Stack) Push(val int) {
    // TODO
}

func (s *Stack) Pop() (int, error) {
    // TODO: return error if empty
    return 0, errors.New("stack is empty")
}

func (s *Stack) Peek() (int, error) {
    // TODO
    return 0, errors.New("stack is empty")
}

func (s *Stack) IsEmpty() bool {
    // TODO
    return true
}

func (s *Stack) Size() int {
    // TODO
    return 0
}

func main() {
    s := &Stack{}
    s.Push(1)
    s.Push(2)
    s.Push(3)
    val, _ := s.Pop()
    fmt.Println("Popped:", val)
    top, _ := s.Peek()
    fmt.Println("Top:", top)
}
```

**Java:**

```java
public class ArrayStack {
    private int[] data;
    private int top;

    public ArrayStack(int capacity) {
        data = new int[capacity];
        top = -1;
    }

    public void push(int val) {
        // TODO: check capacity, then data[++top] = val
    }

    public int pop() {
        // TODO: check empty, return data[top--]
        return 0;
    }

    public int peek() {
        // TODO
        return 0;
    }

    public boolean isEmpty() {
        // TODO
        return true;
    }

    public int size() {
        // TODO
        return 0;
    }

    public static void main(String[] args) {
        ArrayStack s = new ArrayStack(100);
        s.push(1);
        s.push(2);
        s.push(3);
        System.out.println("Popped: " + s.pop());
        System.out.println("Top: " + s.peek());
    }
}
```

**Python:**

```python
class Stack:
    def __init__(self):
        self._items = []

    def push(self, val):
        # TODO
        pass

    def pop(self):
        # TODO: raise IndexError if empty
        pass

    def peek(self):
        # TODO: raise IndexError if empty
        pass

    def is_empty(self):
        # TODO
        return True

    def size(self):
        # TODO
        return 0


if __name__ == "__main__":
    s = Stack()
    s.push(1)
    s.push(2)
    s.push(3)
    print("Popped:", s.pop())
    print("Top:", s.peek())
```

---

### Task 4: Queue

**Objective:** Implement a queue using a circular array.

**Requirements:**
- `enqueue(val)` — O(1). Resize if full.
- `dequeue()` — O(1). Raise error if empty.
- `front()` — O(1). Peek at front element.
- `is_empty()` — O(1).
- `size()` — O(1).

**Starter Code:**

**Go:**

```go
package main

import "fmt"

type CircularQueue struct {
    data     []int
    head     int
    tail     int
    count    int
    capacity int
}

func NewCircularQueue(cap int) *CircularQueue {
    return &CircularQueue{
        data:     make([]int, cap),
        capacity: cap,
    }
}

func (q *CircularQueue) Enqueue(val int) {
    // TODO: if full, resize. Set data[tail] = val. Advance tail = (tail+1) % capacity.
}

func (q *CircularQueue) Dequeue() (int, bool) {
    // TODO: if empty, return false. Get data[head]. Advance head = (head+1) % capacity.
    return 0, false
}

func (q *CircularQueue) Front() (int, bool) {
    // TODO
    return 0, false
}

func (q *CircularQueue) IsEmpty() bool {
    return q.count == 0
}

func (q *CircularQueue) Size() int {
    return q.count
}

func main() {
    q := NewCircularQueue(4)
    q.Enqueue(10)
    q.Enqueue(20)
    q.Enqueue(30)
    val, _ := q.Dequeue()
    fmt.Println("Dequeued:", val)
    fmt.Println("Front:", q.data[q.head])
}
```

**Java:**

```java
public class CircularQueue {
    private int[] data;
    private int head, tail, count, capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        data = new int[capacity];
        head = 0;
        tail = 0;
        count = 0;
    }

    public void enqueue(int val) {
        // TODO: if full, resize. data[tail] = val. tail = (tail+1) % capacity.
    }

    public int dequeue() {
        // TODO: if empty, throw. val = data[head]. head = (head+1) % capacity.
        return 0;
    }

    public int front() {
        // TODO
        return 0;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public static void main(String[] args) {
        CircularQueue q = new CircularQueue(4);
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        System.out.println("Dequeued: " + q.dequeue());
    }
}
```

**Python:**

```python
class CircularQueue:
    def __init__(self, capacity=4):
        self._data = [None] * capacity
        self._head = 0
        self._tail = 0
        self._count = 0
        self._capacity = capacity

    def enqueue(self, val):
        # TODO: if full, resize. data[tail] = val. tail = (tail+1) % capacity.
        pass

    def dequeue(self):
        # TODO: if empty, raise. val = data[head]. head = (head+1) % capacity.
        pass

    def front(self):
        # TODO
        pass

    def is_empty(self):
        return self._count == 0

    def size(self):
        return self._count


if __name__ == "__main__":
    q = CircularQueue(4)
    q.enqueue(10)
    q.enqueue(20)
    q.enqueue(30)
    print("Dequeued:", q.dequeue())
```

---

### Task 5: Array vs Linked List Performance

**Objective:** Write a benchmark that compares arrays and linked lists for insertion and access patterns.

**Requirements:**
- Insert 100,000 elements at the **beginning** of both an array and a linked list. Measure time.
- Insert 100,000 elements at the **end**. Measure time.
- Access 100,000 random elements by index. Measure time.
- Print results in a table.

**Starter Code:**

**Go:**

```go
package main

import (
    "container/list"
    "fmt"
    "math/rand"
    "time"
)

func main() {
    n := 100000

    // Array: insert at beginning
    start := time.Now()
    arr := make([]int, 0)
    for i := 0; i < n; i++ {
        arr = append([]int{i}, arr...) // O(n) each time
    }
    fmt.Printf("Array insert at beginning: %v\n", time.Since(start))

    // Linked list: insert at beginning
    start = time.Now()
    ll := list.New()
    for i := 0; i < n; i++ {
        ll.PushFront(i)
    }
    fmt.Printf("Linked list insert at beginning: %v\n", time.Since(start))

    // TODO: Add insert-at-end and random-access benchmarks
    _ = rand.Intn
}
```

**Java:**

```java
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class ArrayVsLinkedList {
    public static void main(String[] args) {
        int n = 100000;
        Random rand = new Random();

        // Array: insert at beginning
        ArrayList<Integer> arr = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            arr.add(0, i);
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("ArrayList insert at beginning: %d ms%n", elapsed / 1_000_000);

        // LinkedList: insert at beginning
        LinkedList<Integer> ll = new LinkedList<>();
        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            ll.addFirst(i);
        }
        elapsed = System.nanoTime() - start;
        System.out.printf("LinkedList insert at beginning: %d ms%n", elapsed / 1_000_000);

        // TODO: Add insert-at-end and random-access benchmarks
    }
}
```

**Python:**

```python
import time
import random

n = 100_000

# Array: insert at beginning
arr = []
start = time.time()
for i in range(n):
    arr.insert(0, i)
print(f"List insert at beginning: {time.time() - start:.3f}s")

# Linked list simulation (using deque for appendleft)
from collections import deque
dll = deque()
start = time.time()
for i in range(n):
    dll.appendleft(i)
print(f"Deque insert at beginning: {time.time() - start:.3f}s")

# TODO: Add insert-at-end and random-access benchmarks
```

---

## Intermediate Tasks

### Task 6: Deque

**Objective:** Implement a double-ended queue (deque) supporting O(1) insertion and removal at both ends.

**Requirements:**
- `push_front(val)` — O(1).
- `push_back(val)` — O(1).
- `pop_front()` — O(1).
- `pop_back()` — O(1).
- Use a doubly linked list internally.

**Starter Code:**

**Go:**

```go
package main

type DNode struct {
    Val        int
    Prev, Next *DNode
}

type Deque struct {
    Head, Tail *DNode
    Count      int
}

func (d *Deque) PushFront(val int) {
    // TODO: Create node, link to current head
}

func (d *Deque) PushBack(val int) {
    // TODO: Create node, link after current tail
}

func (d *Deque) PopFront() (int, bool) {
    // TODO: Remove head, return its value
    return 0, false
}

func (d *Deque) PopBack() (int, bool) {
    // TODO: Remove tail, return its value
    return 0, false
}
```

**Java:**

```java
public class Deque {
    private static class DNode {
        int val;
        DNode prev, next;
        DNode(int val) { this.val = val; }
    }

    private DNode head, tail;
    private int count;

    public void pushFront(int val) { /* TODO */ }
    public void pushBack(int val)  { /* TODO */ }
    public int popFront()          { /* TODO */ return 0; }
    public int popBack()           { /* TODO */ return 0; }
    public int size()              { return count; }
}
```

**Python:**

```python
class DNode:
    def __init__(self, val):
        self.val = val
        self.prev = None
        self.next = None

class Deque:
    def __init__(self):
        self.head = None
        self.tail = None
        self.count = 0

    def push_front(self, val): pass  # TODO
    def push_back(self, val):  pass  # TODO
    def pop_front(self):       pass  # TODO
    def pop_back(self):        pass  # TODO
    def size(self):            return self.count
```

---

### Task 7: Circular Buffer

**Objective:** Implement a fixed-size circular buffer (ring buffer). When full, new writes overwrite the oldest data.

**Requirements:**
- `write(val)` — Add element. Overwrites oldest if full.
- `read()` — Read and remove oldest element.
- `is_full()`, `is_empty()`, `size()`.

**Starter Code:**

**Go:**

```go
package main

type RingBuffer struct {
    data     []int
    head     int
    tail     int
    count    int
    capacity int
}

func NewRingBuffer(cap int) *RingBuffer {
    return &RingBuffer{data: make([]int, cap), capacity: cap}
}

func (rb *RingBuffer) Write(val int) {
    // TODO: data[tail] = val. Advance tail. If full, advance head too.
}

func (rb *RingBuffer) Read() (int, bool) {
    // TODO: if empty return false. val = data[head]. Advance head.
    return 0, false
}

func (rb *RingBuffer) IsFull() bool  { return rb.count == rb.capacity }
func (rb *RingBuffer) IsEmpty() bool { return rb.count == 0 }
func (rb *RingBuffer) Size() int     { return rb.count }
```

**Java:**

```java
public class RingBuffer {
    private int[] data;
    private int head, tail, count, capacity;

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        data = new int[capacity];
    }

    public void write(int val)  { /* TODO */ }
    public int read()           { /* TODO */ return 0; }
    public boolean isFull()     { return count == capacity; }
    public boolean isEmpty()    { return count == 0; }
    public int size()           { return count; }
}
```

**Python:**

```python
class RingBuffer:
    def __init__(self, capacity):
        self._data = [None] * capacity
        self._head = 0
        self._tail = 0
        self._count = 0
        self._capacity = capacity

    def write(self, val): pass   # TODO
    def read(self):       pass   # TODO
    def is_full(self):    return self._count == self._capacity
    def is_empty(self):   return self._count == 0
    def size(self):       return self._count
```

---

### Task 8: Frequency Count

**Objective:** Given a list of words, count the frequency of each word using a hash map.

**Requirements:**
- Return a map of word to count.
- Find the top-K most frequent words.
- Handle case-insensitivity.

**Starter Code:**

**Go:**

```go
package main

import (
    "fmt"
    "sort"
    "strings"
)

func frequencyCount(words []string) map[string]int {
    // TODO: Count each word (lowercased)
    return nil
}

func topK(freq map[string]int, k int) []string {
    // TODO: Return top-k words by frequency
    // Hint: Collect into slice of pairs, sort by count descending
    _ = sort.Slice
    return nil
}

func main() {
    words := []string{"Go", "go", "Java", "python", "go", "java", "Go"}
    freq := frequencyCount(words)
    fmt.Println(freq)
    fmt.Println("Top 2:", topK(freq, 2))
}
```

**Java:**

```java
import java.util.*;

public class FrequencyCount {
    public static Map<String, Integer> frequencyCount(String[] words) {
        // TODO: count each word (lowercased) using HashMap
        return null;
    }

    public static List<String> topK(Map<String, Integer> freq, int k) {
        // TODO: return top-k words by frequency
        return null;
    }

    public static void main(String[] args) {
        String[] words = {"Go", "go", "Java", "python", "go", "java", "Go"};
        Map<String, Integer> freq = frequencyCount(words);
        System.out.println(freq);
        System.out.println("Top 2: " + topK(freq, 2));
    }
}
```

**Python:**

```python
from collections import Counter

def frequency_count(words):
    # TODO: count each word (lowercased) using dict or Counter
    pass

def top_k(freq, k):
    # TODO: return top-k words by frequency
    pass

if __name__ == "__main__":
    words = ["Go", "go", "Java", "python", "go", "java", "Go"]
    freq = frequency_count(words)
    print(freq)
    print("Top 2:", top_k(freq, 2))
```

---

### Task 9: Two Stacks in One Array

**Objective:** Implement two stacks using a single array, where one grows from the left and the other from the right.

**Requirements:**
- `push1(val)` and `push2(val)` — Push to stack 1 (left side) or stack 2 (right side).
- `pop1()` and `pop2()` — Pop from the respective stack.
- Raise error on overflow (stacks meet in the middle) or underflow (pop empty stack).

**Starter Code:**

**Go:**

```go
package main

type TwoStacks struct {
    data []int
    top1 int
    top2 int
}

func NewTwoStacks(capacity int) *TwoStacks {
    return &TwoStacks{
        data: make([]int, capacity),
        top1: -1,
        top2: capacity,
    }
}

func (ts *TwoStacks) Push1(val int) error { /* TODO */ return nil }
func (ts *TwoStacks) Push2(val int) error { /* TODO */ return nil }
func (ts *TwoStacks) Pop1() (int, error)  { /* TODO */ return 0, nil }
func (ts *TwoStacks) Pop2() (int, error)  { /* TODO */ return 0, nil }
```

**Java:**

```java
public class TwoStacks {
    private int[] data;
    private int top1, top2;

    public TwoStacks(int capacity) {
        data = new int[capacity];
        top1 = -1;
        top2 = capacity;
    }

    public void push1(int val) { /* TODO: check overflow, data[++top1] = val */ }
    public void push2(int val) { /* TODO: check overflow, data[--top2] = val */ }
    public int pop1()          { /* TODO: check underflow, return data[top1--] */ return 0; }
    public int pop2()          { /* TODO: check underflow, return data[top2++] */ return 0; }
}
```

**Python:**

```python
class TwoStacks:
    def __init__(self, capacity):
        self._data = [None] * capacity
        self._top1 = -1
        self._top2 = capacity

    def push1(self, val): pass  # TODO
    def push2(self, val): pass  # TODO
    def pop1(self):       pass  # TODO
    def pop2(self):       pass  # TODO
```

---

### Task 10: Reverse Linked List

**Objective:** Reverse a singly linked list in-place.

**Requirements:**
- Implement both iterative and recursive approaches.
- O(n) time, O(1) space (iterative) or O(n) stack space (recursive).

**Starter Code:**

**Go:**

```go
package main

import "fmt"

type Node struct {
    Val  int
    Next *Node
}

func reverseIterative(head *Node) *Node {
    // TODO: Use three pointers: prev, curr, next
    // Walk through list, reversing each pointer
    return nil
}

func reverseRecursive(head *Node) *Node {
    // TODO: Base case: head == nil || head.Next == nil
    // Recursive: reverse rest, set head.Next.Next = head, head.Next = nil
    return nil
}

func printList(head *Node) {
    for head != nil {
        fmt.Printf("%d -> ", head.Val)
        head = head.Next
    }
    fmt.Println("nil")
}

func main() {
    head := &Node{1, &Node{2, &Node{3, &Node{4, &Node{5, nil}}}}}
    printList(head)
    head = reverseIterative(head)
    printList(head)
}
```

**Java:**

```java
public class ReverseLinkedList {
    static class Node {
        int val;
        Node next;
        Node(int val, Node next) { this.val = val; this.next = next; }
    }

    static Node reverseIterative(Node head) {
        // TODO
        return null;
    }

    static Node reverseRecursive(Node head) {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        Node head = new Node(1, new Node(2, new Node(3, new Node(4, new Node(5, null)))));
        head = reverseIterative(head);
    }
}
```

**Python:**

```python
class Node:
    def __init__(self, val, next_node=None):
        self.val = val
        self.next = next_node

def reverse_iterative(head):
    # TODO: prev, curr, next pointers
    return None

def reverse_recursive(head):
    # TODO
    return None

if __name__ == "__main__":
    head = Node(1, Node(2, Node(3, Node(4, Node(5)))))
    head = reverse_iterative(head)
```

---

## Advanced Tasks

### Task 11: LRU Cache

**Objective:** Implement an LRU (Least Recently Used) Cache with O(1) get and put.

**Requirements:**
- `get(key)` — Return value if exists, mark as recently used. O(1).
- `put(key, value)` — Insert or update. If at capacity, evict LRU entry. O(1).
- Use a hash map + doubly linked list.

**Starter Code:**

**Go:**

```go
package main

type LRUNode struct {
    Key, Val   int
    Prev, Next *LRUNode
}

type LRUCache struct {
    capacity   int
    cache      map[int]*LRUNode
    head, tail *LRUNode // sentinel nodes
}

func NewLRUCache(capacity int) *LRUCache {
    head := &LRUNode{}
    tail := &LRUNode{}
    head.Next = tail
    tail.Prev = head
    return &LRUCache{
        capacity: capacity,
        cache:    make(map[int]*LRUNode),
        head:     head,
        tail:     tail,
    }
}

func (c *LRUCache) Get(key int) (int, bool) {
    // TODO: Lookup in cache. If found, move to head, return value.
    return 0, false
}

func (c *LRUCache) Put(key, value int) {
    // TODO: If exists, update and move to head. Else create node, add to head.
    // If over capacity, remove node before tail (LRU), delete from cache.
}
```

**Java:**

```java
import java.util.HashMap;

public class LRUCache {
    private static class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    private int capacity;
    private HashMap<Integer, Node> cache;
    private Node head, tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) { /* TODO */ return -1; }
    public void put(int key, int value) { /* TODO */ }
}
```

**Python:**

```python
class LRUNode:
    def __init__(self, key=0, val=0):
        self.key = key
        self.val = val
        self.prev = None
        self.next = None

class LRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.cache = {}
        self.head = LRUNode()
        self.tail = LRUNode()
        self.head.next = self.tail
        self.tail.prev = self.head

    def get(self, key):   pass  # TODO
    def put(self, key, value): pass  # TODO
```

---

### Task 12: Self-Resizing Hash Table

**Objective:** Implement a hash table from scratch with separate chaining and automatic resizing.

**Requirements:**
- `put(key, value)` — Insert or update.
- `get(key)` — Retrieve value.
- `delete(key)` — Remove entry.
- Resize (double) when load factor exceeds 0.75.
- Resize (halve) when load factor drops below 0.25.
- Use separate chaining (linked list per bucket).

**Starter Code:**

**Go:**

```go
package main

type Entry struct {
    Key   string
    Value int
    Next  *Entry
}

type HashTable struct {
    buckets  []*Entry
    size     int
    capacity int
}

func NewHashTable() *HashTable {
    return &HashTable{buckets: make([]*Entry, 8), capacity: 8}
}

func (ht *HashTable) hash(key string) int {
    h := 0
    for _, ch := range key {
        h = h*31 + int(ch)
    }
    if h < 0 { h = -h }
    return h % ht.capacity
}

func (ht *HashTable) Put(key string, value int) {
    // TODO: hash key, walk chain, update if exists, else prepend. Check load factor.
}

func (ht *HashTable) Get(key string) (int, bool) {
    // TODO: hash key, walk chain, return value if found
    return 0, false
}

func (ht *HashTable) Delete(key string) bool {
    // TODO: hash key, walk chain, unlink node. Check load factor.
    return false
}

func (ht *HashTable) resize(newCap int) {
    // TODO: Create new buckets, rehash all entries
}
```

**Java:**

```java
public class HashTable {
    private static class Entry {
        String key;
        int value;
        Entry next;
        Entry(String key, int value) { this.key = key; this.value = value; }
    }

    private Entry[] buckets;
    private int size, capacity;

    public HashTable() {
        capacity = 8;
        buckets = new Entry[capacity];
    }

    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    public void put(String key, int value) { /* TODO */ }
    public Integer get(String key)         { /* TODO */ return null; }
    public boolean delete(String key)      { /* TODO */ return false; }
    private void resize(int newCap)        { /* TODO */ }
}
```

**Python:**

```python
class HashTable:
    def __init__(self):
        self._capacity = 8
        self._buckets = [[] for _ in range(self._capacity)]
        self._size = 0

    def _hash(self, key):
        return hash(key) % self._capacity

    def put(self, key, value):   pass  # TODO
    def get(self, key):          pass  # TODO
    def delete(self, key):       pass  # TODO
    def _resize(self, new_cap):  pass  # TODO
```

---

### Task 13: Insert/Delete/GetRandom O(1)

**Objective:** Design a data structure that supports insert, delete, and getRandom, each in O(1) average time.

**Requirements:**
- `insert(val)` — Insert if not present. O(1).
- `remove(val)` — Remove if present. O(1).
- `get_random()` — Return a random element with equal probability. O(1).

**Hint:** Use an array + hash map. On remove, swap with the last element.

**Starter Code:**

**Go:**

```go
package main

import "math/rand"

type RandomizedSet struct {
    data    []int
    indices map[int]int // val -> index in data
}

func NewRandomizedSet() *RandomizedSet {
    return &RandomizedSet{indices: make(map[int]int)}
}

func (rs *RandomizedSet) Insert(val int) bool {
    // TODO: if exists, return false. Append to data. Store index in map.
    return false
}

func (rs *RandomizedSet) Remove(val int) bool {
    // TODO: if not exists, return false. Swap with last. Update map. Remove last.
    return false
}

func (rs *RandomizedSet) GetRandom() int {
    // TODO: return data[rand.Intn(len(data))]
    return 0
}
```

**Java:**

```java
import java.util.*;

public class RandomizedSet {
    private List<Integer> data;
    private Map<Integer, Integer> indices;
    private Random rand;

    public RandomizedSet() {
        data = new ArrayList<>();
        indices = new HashMap<>();
        rand = new Random();
    }

    public boolean insert(int val) { /* TODO */ return false; }
    public boolean remove(int val) { /* TODO */ return false; }
    public int getRandom()         { /* TODO */ return 0; }
}
```

**Python:**

```python
import random

class RandomizedSet:
    def __init__(self):
        self._data = []
        self._indices = {}  # val -> index

    def insert(self, val):     pass  # TODO
    def remove(self, val):     pass  # TODO
    def get_random(self):      pass  # TODO
```

---

### Task 14: Min-Heap

**Objective:** Implement a min-heap (priority queue) from scratch using an array.

**Requirements:**
- `insert(val)` — Add element and bubble up. O(log n).
- `extract_min()` — Remove and return minimum. O(log n).
- `peek_min()` — Return minimum without removing. O(1).
- `size()` — O(1).
- Maintain the heap property: parent <= children.

**Starter Code:**

**Go:**

```go
package main

type MinHeap struct {
    data []int
}

func (h *MinHeap) Insert(val int) {
    // TODO: Append val, then bubble up (swap with parent while smaller)
}

func (h *MinHeap) ExtractMin() (int, bool) {
    // TODO: Save root, swap root with last, remove last, bubble down
    return 0, false
}

func (h *MinHeap) PeekMin() (int, bool) {
    // TODO: return data[0] if not empty
    return 0, false
}

func (h *MinHeap) Size() int { return len(h.data) }

func (h *MinHeap) bubbleUp(i int) {
    // TODO: while data[i] < data[parent(i)], swap and move up
}

func (h *MinHeap) bubbleDown(i int) {
    // TODO: while data[i] > smallest child, swap and move down
}

func parent(i int) int    { return (i - 1) / 2 }
func leftChild(i int) int { return 2*i + 1 }
func rightChild(i int) int { return 2*i + 2 }
```

**Java:**

```java
import java.util.ArrayList;

public class MinHeap {
    private ArrayList<Integer> data = new ArrayList<>();

    public void insert(int val) { /* TODO: add, bubbleUp */ }
    public int extractMin()     { /* TODO: swap root/last, remove last, bubbleDown */ return 0; }
    public int peekMin()        { /* TODO */ return 0; }
    public int size()           { return data.size(); }

    private void bubbleUp(int i)   { /* TODO */ }
    private void bubbleDown(int i) { /* TODO */ }
}
```

**Python:**

```python
class MinHeap:
    def __init__(self):
        self._data = []

    def insert(self, val):    pass  # TODO: append, bubble_up
    def extract_min(self):    pass  # TODO: swap root/last, pop, bubble_down
    def peek_min(self):       pass  # TODO
    def size(self):           return len(self._data)

    def _bubble_up(self, i):   pass  # TODO
    def _bubble_down(self, i): pass  # TODO
```

---

### Task 15: Trie (Prefix Tree)

**Objective:** Implement a trie that supports insert, search, and prefix matching.

**Requirements:**
- `insert(word)` — Add a word. O(k) where k = word length.
- `search(word)` — Return true if exact word exists. O(k).
- `starts_with(prefix)` — Return true if any word starts with prefix. O(k).
- `autocomplete(prefix)` — Return all words with given prefix.

**Starter Code:**

**Go:**

```go
package main

type TrieNode struct {
    Children map[rune]*TrieNode
    IsEnd    bool
}

type Trie struct {
    Root *TrieNode
}

func NewTrie() *Trie {
    return &Trie{Root: &TrieNode{Children: make(map[rune]*TrieNode)}}
}

func (t *Trie) Insert(word string) {
    // TODO: Walk/create nodes for each character, mark last as IsEnd
}

func (t *Trie) Search(word string) bool {
    // TODO: Walk nodes, return true if reached end and IsEnd == true
    return false
}

func (t *Trie) StartsWith(prefix string) bool {
    // TODO: Walk nodes, return true if all characters found
    return false
}

func (t *Trie) Autocomplete(prefix string) []string {
    // TODO: Walk to prefix node, then DFS to collect all words
    return nil
}
```

**Java:**

```java
import java.util.*;

public class Trie {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
    }

    private TrieNode root = new TrieNode();

    public void insert(String word)          { /* TODO */ }
    public boolean search(String word)       { /* TODO */ return false; }
    public boolean startsWith(String prefix) { /* TODO */ return false; }
    public List<String> autocomplete(String prefix) { /* TODO */ return new ArrayList<>(); }
}
```

**Python:**

```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False

class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word):          pass  # TODO
    def search(self, word):          pass  # TODO: return False
    def starts_with(self, prefix):   pass  # TODO: return False
    def autocomplete(self, prefix):  pass  # TODO: return []
```

---

## Benchmark Task

### Objective

Create a comprehensive benchmark comparing the performance of the following operations across multiple data structures:

1. **Sequential insert** (append 1,000,000 elements)
2. **Random access** (read 100,000 random indices)
3. **Search** (find 10,000 random values)
4. **Delete from middle** (remove 10,000 elements)

### Data structures to compare:
- Dynamic array (built-in)
- Linked list
- Hash set
- Binary search tree (or sorted structure)

### Expected output format:

```
Operation          | Array      | LinkedList | HashSet    | BST
-------------------+------------+------------+------------+-----------
Insert 1M          | 120ms      | 450ms      | 350ms      | 800ms
Random Access 100K | 5ms        | 12000ms    | N/A        | 200ms
Search 10K         | 1500ms     | 2000ms     | 8ms        | 150ms
Delete 10K middle  | 8000ms     | 15ms*      | 10ms       | 180ms
```

*\*Linked list delete is O(1) at known node, but O(n) to find the node.*

### Starter Code (Python):

```python
import time
import random
from collections import deque

def benchmark(name, func):
    start = time.time()
    result = func()
    elapsed = time.time() - start
    print(f"{name}: {elapsed:.4f}s")
    return result

n = 1_000_000

# TODO: Implement benchmarks for each operation and data structure
# 1. Sequential insert
# 2. Random access
# 3. Search
# 4. Delete from middle
# Print results in table format
```

---

> **Note:** For all tasks, write tests to verify correctness before optimizing for performance. The benchmark task should be done last, after you have working implementations of the individual data structures.
