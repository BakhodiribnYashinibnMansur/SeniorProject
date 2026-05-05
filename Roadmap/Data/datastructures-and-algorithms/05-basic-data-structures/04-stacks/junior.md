# Stack -- Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What Is a Stack?](#what-is-a-stack)
3. [The LIFO Principle](#the-lifo-principle)
4. [Real-World Analogies](#real-world-analogies)
5. [Core Operations](#core-operations)
6. [Array-Based Implementation](#array-based-implementation)
7. [Linked-List-Based Implementation](#linked-list-based-implementation)
8. [Time and Space Complexity](#time-and-space-complexity)
9. [Common Uses](#common-uses)
10. [Parentheses Matching](#parentheses-matching)
11. [Reverse a String](#reverse-a-string)
12. [Function Call Stack](#function-call-stack)
13. [Summary](#summary)

---

## Introduction

A **stack** is one of the most fundamental data structures in computer science. It appears everywhere -- from how your browser's "Back" button works to how programming languages execute function calls. Understanding stacks deeply is the first step toward mastering more advanced topics like expression parsing, graph traversal, and compiler design.

This document covers everything a junior developer needs to know about stacks: the core concept, all basic operations, two implementation strategies, and the most common practical applications.

---

## What Is a Stack?

A stack is a **linear data structure** that stores elements in a particular order. You can only add or remove elements from **one end**, called the **top** of the stack.

Think of it as a restricted list: you cannot insert or remove from the middle or bottom -- only from the top.

```
    +-------+
    |   30  |  <-- top (most recently added)
    +-------+
    |   20  |
    +-------+
    |   10  |  <-- bottom (first element added)
    +-------+
```

---

## The LIFO Principle

LIFO stands for **Last In, First Out**. The last element you put into the stack is the first one you take out. This single rule defines all stack behavior.

**Sequence example:**

```
Push 10  ->  [10]
Push 20  ->  [10, 20]
Push 30  ->  [10, 20, 30]
Pop      ->  returns 30, stack is [10, 20]
Pop      ->  returns 20, stack is [10]
Pop      ->  returns 10, stack is []
```

The element 30 was added last but removed first. The element 10 was added first but removed last.

---

## Real-World Analogies

### Stack of Plates

In a cafeteria, plates are stacked one on top of another. You always take the top plate (the last one placed). You never pull a plate from the middle. This is exactly how a stack works.

### Undo/Redo

In a text editor, every action you perform is pushed onto a stack. When you press Ctrl+Z (undo), the most recent action is popped. Redo operations use a second stack to store undone actions.

### Browser Back Button

Every page you visit is pushed onto a history stack. When you click "Back," the current page is popped, and you see the previous one. "Forward" works like a second stack.

### Stack of Books

If you place books on a desk one by one, the only book you can grab without disturbing the others is the one on top.

---

## Core Operations

A stack supports four primary operations, each running in **O(1)** constant time:

| Operation   | Description                                    | Time Complexity |
| ----------- | ---------------------------------------------- | --------------- |
| `push(x)`   | Add element `x` to the top of the stack        | O(1)            |
| `pop()`     | Remove and return the top element               | O(1)            |
| `peek()`    | Return the top element without removing it      | O(1)            |
| `isEmpty()`  | Check whether the stack has no elements         | O(1)            |

Additional operations some implementations include:

| Operation   | Description                                    | Time Complexity |
| ----------- | ---------------------------------------------- | --------------- |
| `size()`    | Return the number of elements in the stack      | O(1)            |
| `isFull()`  | Check if the stack is at capacity (array-based) | O(1)            |

---

## Array-Based Implementation

An array-based stack uses a fixed or dynamic array and an integer `top` that tracks the index of the topmost element. This is the simplest and most cache-friendly implementation.

### How It Works

```
Index:   0    1    2    3    4    5    6    7
       +----+----+----+----+----+----+----+----+
       | 10 | 20 | 30 |    |    |    |    |    |
       +----+----+----+----+----+----+----+----+
                    ^
                   top = 2
```

- **Push**: Increment `top`, place element at `array[top]`.
- **Pop**: Read `array[top]`, decrement `top`.
- **Peek**: Read `array[top]` without changing `top`.

### Go Implementation

```go
package stack

import "errors"

// ArrayStack is a stack backed by a slice.
type ArrayStack struct {
    data []int
}

// NewArrayStack creates a new empty stack.
func NewArrayStack() *ArrayStack {
    return &ArrayStack{
        data: make([]int, 0),
    }
}

// Push adds an element to the top of the stack.
func (s *ArrayStack) Push(val int) {
    s.data = append(s.data, val)
}

// Pop removes and returns the top element.
func (s *ArrayStack) Pop() (int, error) {
    if s.IsEmpty() {
        return 0, errors.New("stack is empty")
    }
    top := s.data[len(s.data)-1]
    s.data = s.data[:len(s.data)-1]
    return top, nil
}

// Peek returns the top element without removing it.
func (s *ArrayStack) Peek() (int, error) {
    if s.IsEmpty() {
        return 0, errors.New("stack is empty")
    }
    return s.data[len(s.data)-1], nil
}

// IsEmpty returns true if the stack has no elements.
func (s *ArrayStack) IsEmpty() bool {
    return len(s.data) == 0
}

// Size returns the number of elements in the stack.
func (s *ArrayStack) Size() int {
    return len(s.data)
}
```

### Java Implementation

```java
import java.util.EmptyStackException;

public class ArrayStack {
    private int[] data;
    private int top;
    private int capacity;

    public ArrayStack(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.top = -1;
    }

    // Push an element onto the stack.
    public void push(int val) {
        if (top == capacity - 1) {
            resize();
        }
        data[++top] = val;
    }

    // Pop and return the top element.
    public int pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return data[top--];
    }

    // Return the top element without removing it.
    public int peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return data[top];
    }

    // Check if the stack is empty.
    public boolean isEmpty() {
        return top == -1;
    }

    // Return the number of elements.
    public int size() {
        return top + 1;
    }

    // Double the capacity when full.
    private void resize() {
        capacity *= 2;
        int[] newData = new int[capacity];
        System.arraycopy(data, 0, newData, 0, top + 1);
        data = newData;
    }
}
```

### Python Implementation

```python
class ArrayStack:
    """Stack implemented using a Python list."""

    def __init__(self):
        self._data = []

    def push(self, val):
        """Add an element to the top of the stack."""
        self._data.append(val)

    def pop(self):
        """Remove and return the top element."""
        if self.is_empty():
            raise IndexError("pop from empty stack")
        return self._data.pop()

    def peek(self):
        """Return the top element without removing it."""
        if self.is_empty():
            raise IndexError("peek from empty stack")
        return self._data[-1]

    def is_empty(self):
        """Check whether the stack has no elements."""
        return len(self._data) == 0

    def size(self):
        """Return the number of elements in the stack."""
        return len(self._data)

    def __repr__(self):
        return f"ArrayStack({self._data})"
```

---

## Linked-List-Based Implementation

A linked-list-based stack uses nodes where each node stores a value and a pointer to the next node. The "top" of the stack is the head of the linked list.

### How It Works

```
  top
   |
   v
 +------+    +------+    +------+
 |  30  | -> |  20  | -> |  10  | -> nil
 +------+    +------+    +------+
```

- **Push**: Create a new node, point it to the current top, update top.
- **Pop**: Store top's value, move top to the next node, return the value.
- **Peek**: Return top's value.

**Advantages over array-based:**
- No wasted pre-allocated memory.
- Never needs resizing.

**Disadvantages:**
- Each node requires extra memory for the pointer.
- Less cache-friendly due to non-contiguous memory.

### Go Implementation

```go
package stack

import "errors"

// node is an internal linked-list node.
type node struct {
    val  int
    next *node
}

// LinkedStack is a stack backed by a singly linked list.
type LinkedStack struct {
    top  *node
    size int
}

// NewLinkedStack creates a new empty linked stack.
func NewLinkedStack() *LinkedStack {
    return &LinkedStack{}
}

// Push adds an element to the top of the stack.
func (s *LinkedStack) Push(val int) {
    s.top = &node{val: val, next: s.top}
    s.size++
}

// Pop removes and returns the top element.
func (s *LinkedStack) Pop() (int, error) {
    if s.IsEmpty() {
        return 0, errors.New("stack is empty")
    }
    val := s.top.val
    s.top = s.top.next
    s.size--
    return val, nil
}

// Peek returns the top element without removing it.
func (s *LinkedStack) Peek() (int, error) {
    if s.IsEmpty() {
        return 0, errors.New("stack is empty")
    }
    return s.top.val, nil
}

// IsEmpty returns true if the stack has no elements.
func (s *LinkedStack) IsEmpty() bool {
    return s.top == nil
}

// Size returns the number of elements in the stack.
func (s *LinkedStack) Size() int {
    return s.size
}
```

### Java Implementation

```java
import java.util.EmptyStackException;

public class LinkedStack {
    private static class Node {
        int val;
        Node next;

        Node(int val, Node next) {
            this.val = val;
            this.next = next;
        }
    }

    private Node top;
    private int size;

    public LinkedStack() {
        this.top = null;
        this.size = 0;
    }

    // Push an element onto the stack.
    public void push(int val) {
        top = new Node(val, top);
        size++;
    }

    // Pop and return the top element.
    public int pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        int val = top.val;
        top = top.next;
        size--;
        return val;
    }

    // Return the top element without removing it.
    public int peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.val;
    }

    // Check if the stack is empty.
    public boolean isEmpty() {
        return top == null;
    }

    // Return the number of elements.
    public int size() {
        return size;
    }
}
```

### Python Implementation

```python
class _Node:
    """Internal node for the linked stack."""
    __slots__ = ("val", "next")

    def __init__(self, val, nxt=None):
        self.val = val
        self.next = nxt


class LinkedStack:
    """Stack implemented using a singly linked list."""

    def __init__(self):
        self._top = None
        self._size = 0

    def push(self, val):
        """Add an element to the top of the stack."""
        self._top = _Node(val, self._top)
        self._size += 1

    def pop(self):
        """Remove and return the top element."""
        if self.is_empty():
            raise IndexError("pop from empty stack")
        val = self._top.val
        self._top = self._top.next
        self._size -= 1
        return val

    def peek(self):
        """Return the top element without removing it."""
        if self.is_empty():
            raise IndexError("peek from empty stack")
        return self._top.val

    def is_empty(self):
        """Check whether the stack has no elements."""
        return self._top is None

    def size(self):
        """Return the number of elements in the stack."""
        return self._size

    def __repr__(self):
        items = []
        current = self._top
        while current:
            items.append(str(current.val))
            current = current.next
        return "LinkedStack(top -> " + " -> ".join(items) + ")"
```

---

## Time and Space Complexity

### Time Complexity

| Operation | Array-Based | Linked-List-Based |
| --------- | ----------- | ----------------- |
| Push      | O(1)*       | O(1)              |
| Pop       | O(1)        | O(1)              |
| Peek      | O(1)        | O(1)              |
| isEmpty   | O(1)        | O(1)              |
| Size      | O(1)        | O(1)              |
| Search    | O(n)        | O(n)              |

*Amortized O(1) for dynamic arrays. A single push may trigger a resize costing O(n), but averaged over n pushes, the cost per push is O(1).

### Space Complexity

| Implementation    | Space     | Notes                                  |
| ----------------- | --------- | -------------------------------------- |
| Array-based       | O(n)      | May waste space if capacity >> size    |
| Linked-list-based | O(n)      | Extra pointer per node                 |

---

## Common Uses

### Parentheses Matching

One of the most classic stack problems: determine whether a string of brackets is balanced.

**Algorithm:**
1. Scan each character left to right.
2. If it is an opening bracket, push it.
3. If it is a closing bracket, pop the stack and check that it matches.
4. At the end, the stack must be empty.

#### Go

```go
func isBalanced(s string) bool {
    stack := []rune{}
    pairs := map[rune]rune{
        ')': '(',
        ']': '[',
        '}': '{',
    }

    for _, ch := range s {
        switch ch {
        case '(', '[', '{':
            stack = append(stack, ch)
        case ')', ']', '}':
            if len(stack) == 0 {
                return false
            }
            top := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            if top != pairs[ch] {
                return false
            }
        }
    }
    return len(stack) == 0
}
```

#### Java

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class ParenthesesMatcher {
    public static boolean isBalanced(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> pairs = Map.of(
            ')', '(',
            ']', '[',
            '}', '{'
        );

        for (char ch : s.toCharArray()) {
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            } else if (pairs.containsKey(ch)) {
                if (stack.isEmpty() || stack.pop() != pairs.get(ch)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}
```

#### Python

```python
def is_balanced(s: str) -> bool:
    stack = []
    pairs = {")": "(", "]": "[", "}": "{"}

    for ch in s:
        if ch in "([{":
            stack.append(ch)
        elif ch in pairs:
            if not stack or stack.pop() != pairs[ch]:
                return False
    return len(stack) == 0
```

---

### Reverse a String

Push every character onto a stack, then pop them all. The LIFO order reverses the sequence.

#### Go

```go
func reverseString(s string) string {
    stack := []byte{}
    for i := 0; i < len(s); i++ {
        stack = append(stack, s[i])
    }

    result := make([]byte, len(s))
    for i := 0; i < len(s); i++ {
        result[i] = stack[len(stack)-1]
        stack = stack[:len(stack)-1]
    }
    return string(result)
}
```

#### Java

```java
public class ReverseString {
    public static String reverse(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char ch : s.toCharArray()) {
            stack.push(ch);
        }

        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }
        return sb.toString();
    }
}
```

#### Python

```python
def reverse_string(s: str) -> str:
    stack = list(s)
    result = []
    while stack:
        result.append(stack.pop())
    return "".join(result)
```

---

### Function Call Stack

Every time a function is called, the runtime pushes a **stack frame** onto the **call stack**. This frame contains:

- Local variables
- Parameters
- Return address (where to continue after the function returns)

When the function returns, its frame is popped, and execution resumes at the saved return address.

**Example: Recursive factorial**

```
factorial(4)
  -> pushes frame for factorial(4)
  -> calls factorial(3)
     -> pushes frame for factorial(3)
     -> calls factorial(2)
        -> pushes frame for factorial(2)
        -> calls factorial(1)
           -> pushes frame for factorial(1)
           -> returns 1, pops frame
        -> returns 2 * 1 = 2, pops frame
     -> returns 3 * 2 = 6, pops frame
  -> returns 4 * 6 = 24, pops frame
```

#### Go

```go
func factorial(n int) int {
    if n <= 1 {
        return 1
    }
    return n * factorial(n-1)
}
```

#### Java

```java
public class Factorial {
    public static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}
```

#### Python

```python
def factorial(n: int) -> int:
    if n <= 1:
        return 1
    return n * factorial(n - 1)
```

**Converting recursion to iteration with an explicit stack:**

#### Go

```go
func factorialIterative(n int) int {
    stack := []int{}
    for n > 1 {
        stack = append(stack, n)
        n--
    }
    result := 1
    for len(stack) > 0 {
        result *= stack[len(stack)-1]
        stack = stack[:len(stack)-1]
    }
    return result
}
```

#### Java

```java
public static int factorialIterative(int n) {
    Deque<Integer> stack = new ArrayDeque<>();
    while (n > 1) {
        stack.push(n);
        n--;
    }
    int result = 1;
    while (!stack.isEmpty()) {
        result *= stack.pop();
    }
    return result;
}
```

#### Python

```python
def factorial_iterative(n: int) -> int:
    stack = []
    while n > 1:
        stack.append(n)
        n -= 1
    result = 1
    while stack:
        result *= stack.pop()
    return result
```

---

## Summary

| Concept                   | Key Takeaway                                          |
| ------------------------- | ----------------------------------------------------- |
| LIFO                      | Last In, First Out -- the defining rule of a stack     |
| Push / Pop / Peek         | All O(1) -- the three essential operations             |
| Array-based               | Simple, cache-friendly, amortized O(1) push            |
| Linked-list-based         | No wasted capacity, O(1) push always                   |
| Parentheses matching      | Classic stack application -- push open, pop on close   |
| Reverse string            | Push all chars, pop them for reversed order             |
| Function call stack       | Runtime uses a stack for function calls and returns     |
| Recursion to iteration    | Any recursion can be converted using an explicit stack  |

**What to study next:** monotonic stacks, min-stack, expression evaluation, and stack-based DFS -- covered in the [middle.md](middle.md) document.
