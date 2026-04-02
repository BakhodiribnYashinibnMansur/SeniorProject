# Stack -- Middle Level

## Table of Contents

1. [Overview](#overview)
2. [Monotonic Stack](#monotonic-stack)
3. [Min Stack -- getMin in O(1)](#min-stack----getmin-in-o1)
4. [Expression Evaluation](#expression-evaluation)
5. [DFS Using an Explicit Stack](#dfs-using-an-explicit-stack)
6. [Stack in Backtracking](#stack-in-backtracking)
7. [Two Stacks as a Queue](#two-stacks-as-a-queue)
8. [Comparison: Stack vs Queue vs Deque](#comparison-stack-vs-queue-vs-deque)
9. [Summary](#summary)

---

## Overview

This document builds on the junior-level fundamentals and explores intermediate stack techniques that appear frequently in coding interviews and real-world software. You should already be comfortable with push, pop, peek, and basic stack implementations before proceeding.

---

## Monotonic Stack

A **monotonic stack** maintains elements in either non-increasing or non-decreasing order from bottom to top. It is the key technique for problems like "next greater element," "daily temperatures," and "largest rectangle in histogram."

### Pattern: Next Greater Element

For each element, find the first element to its right that is larger.

**Algorithm:**
1. Traverse the array from right to left.
2. For each element, pop from the stack while the stack's top is smaller or equal.
3. The stack's current top (if any) is the next greater element.
4. Push the current element.

### Go

```go
func nextGreaterElements(nums []int) []int {
    n := len(nums)
    result := make([]int, n)
    stack := []int{} // stores values

    for i := n - 1; i >= 0; i-- {
        // Pop elements that are not greater than nums[i]
        for len(stack) > 0 && stack[len(stack)-1] <= nums[i] {
            stack = stack[:len(stack)-1]
        }
        if len(stack) == 0 {
            result[i] = -1 // no greater element
        } else {
            result[i] = stack[len(stack)-1]
        }
        stack = append(stack, nums[i])
    }
    return result
}
```

### Java

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class MonotonicStack {
    public static int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // stores values

        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && stack.peek() <= nums[i]) {
                stack.pop();
            }
            result[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(nums[i]);
        }
        return result;
    }
}
```

### Python

```python
def next_greater_elements(nums: list[int]) -> list[int]:
    n = len(nums)
    result = [-1] * n
    stack = []  # stores values

    for i in range(n - 1, -1, -1):
        while stack and stack[-1] <= nums[i]:
            stack.pop()
        if stack:
            result[i] = stack[-1]
        stack.append(nums[i])
    return result
```

### Monotonic Decreasing Stack (Index-Based)

Many problems require storing indices instead of values. The "Daily Temperatures" problem is a classic example: for each day, find how many days until a warmer temperature.

```python
def daily_temperatures(temps: list[int]) -> list[int]:
    n = len(temps)
    result = [0] * n
    stack = []  # stores indices

    for i in range(n):
        while stack and temps[i] > temps[stack[-1]]:
            j = stack.pop()
            result[j] = i - j
        stack.append(i)
    return result
```

---

## Min Stack -- getMin in O(1)

Design a stack that supports push, pop, peek, and retrieving the minimum element, all in O(1) time.

**Idea:** Maintain a second stack (or parallel tracking) that records the minimum at each level.

### Go

```go
type MinStack struct {
    data    []int
    minData []int
}

func NewMinStack() *MinStack {
    return &MinStack{}
}

func (s *MinStack) Push(val int) {
    s.data = append(s.data, val)
    if len(s.minData) == 0 || val <= s.minData[len(s.minData)-1] {
        s.minData = append(s.minData, val)
    } else {
        s.minData = append(s.minData, s.minData[len(s.minData)-1])
    }
}

func (s *MinStack) Pop() int {
    val := s.data[len(s.data)-1]
    s.data = s.data[:len(s.data)-1]
    s.minData = s.minData[:len(s.minData)-1]
    return val
}

func (s *MinStack) Peek() int {
    return s.data[len(s.data)-1]
}

func (s *MinStack) GetMin() int {
    return s.minData[len(s.minData)-1]
}
```

### Java

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class MinStack {
    private final Deque<Integer> data = new ArrayDeque<>();
    private final Deque<Integer> minData = new ArrayDeque<>();

    public void push(int val) {
        data.push(val);
        if (minData.isEmpty() || val <= minData.peek()) {
            minData.push(val);
        } else {
            minData.push(minData.peek());
        }
    }

    public int pop() {
        minData.pop();
        return data.pop();
    }

    public int peek() {
        return data.peek();
    }

    public int getMin() {
        return minData.peek();
    }
}
```

### Python

```python
class MinStack:
    """Stack with O(1) getMin using an auxiliary min-stack."""

    def __init__(self):
        self._data = []
        self._min_data = []

    def push(self, val: int) -> None:
        self._data.append(val)
        if not self._min_data or val <= self._min_data[-1]:
            self._min_data.append(val)
        else:
            self._min_data.append(self._min_data[-1])

    def pop(self) -> int:
        self._min_data.pop()
        return self._data.pop()

    def peek(self) -> int:
        return self._data[-1]

    def get_min(self) -> int:
        return self._min_data[-1]
```

### Space-Optimized Min Stack

Instead of storing a min for every element, only push to the min-stack when a new minimum is encountered:

```python
class MinStackOptimized:
    def __init__(self):
        self._data = []
        self._min_stack = []  # only stores when min changes

    def push(self, val: int) -> None:
        self._data.append(val)
        if not self._min_stack or val <= self._min_stack[-1]:
            self._min_stack.append(val)

    def pop(self) -> int:
        val = self._data.pop()
        if val == self._min_stack[-1]:
            self._min_stack.pop()
        return val

    def get_min(self) -> int:
        return self._min_stack[-1]
```

---

## Expression Evaluation

Stacks are the backbone of expression parsing. We cover two classic algorithms: converting infix to postfix (Shunting-Yard) and evaluating postfix.

### Operator Precedence

| Operator | Precedence | Associativity |
| -------- | ---------- | ------------- |
| `+`, `-` | 1          | Left          |
| `*`, `/` | 2          | Left          |
| `^`      | 3          | Right         |

### Infix to Postfix (Shunting-Yard Algorithm)

**Algorithm:**
1. Scan tokens left to right.
2. If operand, append to output.
3. If operator, pop operators with higher/equal precedence (left-associative) from the stack to output, then push current operator.
4. If `(`, push to stack.
5. If `)`, pop to output until `(` is found; discard the `(`.
6. At end, pop remaining operators to output.

### Go

```go
func infixToPostfix(tokens []string) []string {
    precedence := map[string]int{"+": 1, "-": 1, "*": 2, "/": 2, "^": 3}
    rightAssoc := map[string]bool{"^": true}
    var output []string
    var stack []string

    for _, tok := range tokens {
        switch {
        case tok == "(":
            stack = append(stack, tok)
        case tok == ")":
            for len(stack) > 0 && stack[len(stack)-1] != "(" {
                output = append(output, stack[len(stack)-1])
                stack = stack[:len(stack)-1]
            }
            stack = stack[:len(stack)-1] // discard "("
        case precedence[tok] > 0:
            for len(stack) > 0 {
                top := stack[len(stack)-1]
                if top == "(" {
                    break
                }
                if precedence[top] > precedence[tok] ||
                    (precedence[top] == precedence[tok] && !rightAssoc[tok]) {
                    output = append(output, top)
                    stack = stack[:len(stack)-1]
                } else {
                    break
                }
            }
            stack = append(stack, tok)
        default: // operand
            output = append(output, tok)
        }
    }
    for len(stack) > 0 {
        output = append(output, stack[len(stack)-1])
        stack = stack[:len(stack)-1]
    }
    return output
}
```

### Java

```java
import java.util.*;

public class ShuntingYard {
    private static final Map<String, Integer> PREC = Map.of(
        "+", 1, "-", 1, "*", 2, "/", 2, "^", 3
    );
    private static final Set<String> RIGHT_ASSOC = Set.of("^");

    public static List<String> infixToPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String tok : tokens) {
            if (tok.equals("(")) {
                stack.push(tok);
            } else if (tok.equals(")")) {
                while (!stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop(); // discard "("
            } else if (PREC.containsKey(tok)) {
                while (!stack.isEmpty() && !stack.peek().equals("(") &&
                       (PREC.getOrDefault(stack.peek(), 0) > PREC.get(tok) ||
                        (PREC.getOrDefault(stack.peek(), 0).equals(PREC.get(tok))
                         && !RIGHT_ASSOC.contains(tok)))) {
                    output.add(stack.pop());
                }
                stack.push(tok);
            } else {
                output.add(tok); // operand
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }
}
```

### Python

```python
def infix_to_postfix(tokens: list[str]) -> list[str]:
    precedence = {"+": 1, "-": 1, "*": 2, "/": 2, "^": 3}
    right_assoc = {"^"}
    output = []
    stack = []

    for tok in tokens:
        if tok == "(":
            stack.append(tok)
        elif tok == ")":
            while stack[-1] != "(":
                output.append(stack.pop())
            stack.pop()  # discard "("
        elif tok in precedence:
            while (stack and stack[-1] != "(" and
                   (precedence.get(stack[-1], 0) > precedence[tok] or
                    (precedence.get(stack[-1], 0) == precedence[tok]
                     and tok not in right_assoc))):
                output.append(stack.pop())
            stack.append(tok)
        else:
            output.append(tok)  # operand

    while stack:
        output.append(stack.pop())
    return output
```

### Postfix Evaluation

```python
def eval_postfix(tokens: list[str]) -> float:
    stack = []
    ops = {
        "+": lambda a, b: a + b,
        "-": lambda a, b: a - b,
        "*": lambda a, b: a * b,
        "/": lambda a, b: a / b,
    }
    for tok in tokens:
        if tok in ops:
            b, a = stack.pop(), stack.pop()
            stack.append(ops[tok](a, b))
        else:
            stack.append(float(tok))
    return stack[0]
```

---

## DFS Using an Explicit Stack

Depth-First Search on a graph or tree naturally uses the call stack via recursion. You can replace it with an explicit stack for better control over memory and to avoid stack overflow on deep graphs.

### Go

```go
func dfsIterative(graph map[int][]int, start int) []int {
    visited := map[int]bool{}
    stack := []int{start}
    var order []int

    for len(stack) > 0 {
        node := stack[len(stack)-1]
        stack = stack[:len(stack)-1]

        if visited[node] {
            continue
        }
        visited[node] = true
        order = append(order, node)

        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                stack = append(stack, neighbor)
            }
        }
    }
    return order
}
```

### Java

```java
import java.util.*;

public class DFS {
    public static List<Integer> dfsIterative(Map<Integer, List<Integer>> graph, int start) {
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        stack.push(start);
        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (visited.contains(node)) continue;
            visited.add(node);
            order.add(node);

            for (int neighbor : graph.getOrDefault(node, List.of())) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
        return order;
    }
}
```

### Python

```python
def dfs_iterative(graph: dict[int, list[int]], start: int) -> list[int]:
    visited = set()
    stack = [start]
    order = []

    while stack:
        node = stack.pop()
        if node in visited:
            continue
        visited.add(node)
        order.append(node)

        for neighbor in graph.get(node, []):
            if neighbor not in visited:
                stack.append(neighbor)
    return order
```

---

## Stack in Backtracking

Backtracking algorithms explore choices and undo them. An explicit stack models this naturally.

### Example: Generate All Subsets

```python
def subsets(nums: list[int]) -> list[list[int]]:
    result = []
    stack = [(0, [])]  # (index, current_subset)

    while stack:
        idx, current = stack.pop()
        if idx == len(nums):
            result.append(current[:])
            continue
        # Branch 1: exclude nums[idx]
        stack.append((idx + 1, current[:]))
        # Branch 2: include nums[idx]
        stack.append((idx + 1, current + [nums[idx]]))
    return result
```

---

## Two Stacks as a Queue

You can implement a FIFO queue using two LIFO stacks. This is a classic interview question and also appears in functional programming languages where stacks (lists) are primitive.

**Idea:** Use an "in" stack for enqueue and an "out" stack for dequeue. When the out stack is empty, pour all elements from the in stack to the out stack (reversing order).

### Go

```go
type StackQueue struct {
    inStack  []int
    outStack []int
}

func (q *StackQueue) Enqueue(val int) {
    q.inStack = append(q.inStack, val)
}

func (q *StackQueue) transfer() {
    for len(q.inStack) > 0 {
        top := q.inStack[len(q.inStack)-1]
        q.inStack = q.inStack[:len(q.inStack)-1]
        q.outStack = append(q.outStack, top)
    }
}

func (q *StackQueue) Dequeue() (int, error) {
    if len(q.outStack) == 0 {
        q.transfer()
    }
    if len(q.outStack) == 0 {
        return 0, errors.New("queue is empty")
    }
    val := q.outStack[len(q.outStack)-1]
    q.outStack = q.outStack[:len(q.outStack)-1]
    return val, nil
}
```

### Java

```java
public class StackQueue {
    private final Deque<Integer> inStack = new ArrayDeque<>();
    private final Deque<Integer> outStack = new ArrayDeque<>();

    public void enqueue(int val) {
        inStack.push(val);
    }

    public int dequeue() {
        if (outStack.isEmpty()) {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop());
            }
        }
        if (outStack.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return outStack.pop();
    }

    public int peek() {
        if (outStack.isEmpty()) {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop());
            }
        }
        return outStack.peek();
    }
}
```

### Python

```python
from collections import deque

class StackQueue:
    """FIFO queue implemented with two stacks."""

    def __init__(self):
        self._in_stack = []
        self._out_stack = []

    def enqueue(self, val) -> None:
        self._in_stack.append(val)

    def dequeue(self):
        if not self._out_stack:
            while self._in_stack:
                self._out_stack.append(self._in_stack.pop())
        if not self._out_stack:
            raise IndexError("dequeue from empty queue")
        return self._out_stack.pop()

    def peek(self):
        if not self._out_stack:
            while self._in_stack:
                self._out_stack.append(self._in_stack.pop())
        return self._out_stack[-1]

    def is_empty(self) -> bool:
        return not self._in_stack and not self._out_stack
```

**Amortized Analysis:** Each element is pushed and popped at most twice (once per stack), so both enqueue and dequeue are **amortized O(1)**.

---

## Comparison: Stack vs Queue vs Deque

| Feature        | Stack           | Queue           | Deque                    |
| -------------- | --------------- | --------------- | ------------------------ |
| Order          | LIFO            | FIFO            | Both ends                |
| Insert         | Top only        | Rear only       | Front or Rear            |
| Remove         | Top only        | Front only      | Front or Rear            |
| Peek           | Top             | Front           | Front or Rear            |
| Use case       | DFS, undo, expr | BFS, scheduling | Sliding window, palindrome |
| Go             | `[]T` (slice)   | Custom / list   | `container/list`         |
| Java           | `ArrayDeque`    | `ArrayDeque`    | `ArrayDeque`             |
| Python         | `list`          | `deque`         | `deque`                  |

### When to Use Which

- **Stack**: When you need to reverse order, match nested structures, or do DFS.
- **Queue**: When processing in arrival order (BFS, task scheduling).
- **Deque**: When you need efficient insert/remove at both ends (sliding window maximum, palindrome checking).

---

## Summary

| Technique               | Key Idea                                          | Complexity         |
| ----------------------- | ------------------------------------------------- | ------------------ |
| Monotonic stack         | Maintain sorted order; next greater/smaller element | O(n) total         |
| Min stack               | Auxiliary stack tracks running minimum              | O(1) all ops       |
| Infix to postfix        | Shunting-Yard algorithm using operator stack        | O(n)               |
| Postfix evaluation      | Push operands, pop two on operator                  | O(n)               |
| Iterative DFS           | Replace call stack with explicit stack              | O(V + E)           |
| Backtracking with stack | Push choices, pop to undo                           | Depends on problem |
| Two stacks as queue     | In-stack and out-stack with lazy transfer            | Amortized O(1)     |

**Next level:** For senior-level topics, see [senior.md](senior.md) -- runtime call stacks, stack overflow prevention, concurrent stacks, and stack-based virtual machines.
