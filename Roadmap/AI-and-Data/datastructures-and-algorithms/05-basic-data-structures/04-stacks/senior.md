# Stack -- Senior Level

## Table of Contents

1. [Overview](#overview)
2. [Call Stack in Runtime Systems](#call-stack-in-runtime-systems)
3. [Stack Overflow Prevention](#stack-overflow-prevention)
4. [Coroutines, Goroutines, and Stack Management](#coroutines-goroutines-and-stack-management)
5. [Stack-Based Virtual Machines](#stack-based-virtual-machines)
6. [Undo/Redo Systems in Production](#undoredo-systems-in-production)
7. [Concurrent Stack -- Treiber Stack](#concurrent-stack----treiber-stack)
8. [Summary](#summary)

---

## Overview

At the senior level, stacks move beyond algorithm puzzles and into the domain of systems programming, runtime internals, and concurrent data structures. This document explores how stacks operate at the runtime level, how to prevent stack overflow in production systems, how modern languages manage lightweight thread stacks, and how to build thread-safe stacks for concurrent applications.

---

## Call Stack in Runtime Systems

Every thread of execution has its own **call stack** managed by the operating system or runtime. Understanding its structure is critical for debugging, performance tuning, and writing safe recursive code.

### Anatomy of a Stack Frame

Each function invocation creates a stack frame containing:

```
High address
+---------------------------+
|    Return address          |   Where to resume after return
+---------------------------+
|    Saved frame pointer     |   Previous frame's base
+---------------------------+
|    Local variables         |   Function-local data
+---------------------------+
|    Saved registers         |   Callee-saved register values
+---------------------------+
|    Function arguments      |   Parameters (some in registers)
+---------------------------+
Low address (stack grows downward on x86)
```

### Default Stack Sizes

| Runtime         | Default Stack Size | Configurable?                     |
| --------------- | ------------------ | --------------------------------- |
| Go goroutine    | 8 KB (initial)     | Grows dynamically up to 1 GB      |
| Java thread     | 512 KB -- 1 MB     | `-Xss` flag                       |
| Python thread   | 8 MB (OS default)  | `sys.setrecursionlimit()` + ulimit |
| C/C++ thread    | 1 -- 8 MB          | `ulimit -s` or pthread attr        |

### Stack Inspection

**Go:** Use `runtime.Stack()` or `runtime/debug.PrintStack()` to capture a goroutine's stack trace programmatically.

```go
import "runtime/debug"

func debugCurrentStack() {
    debug.PrintStack()
}
```

**Java:** Use `Thread.currentThread().getStackTrace()` or throw an exception and inspect the trace.

```java
StackTraceElement[] trace = Thread.currentThread().getStackTrace();
for (StackTraceElement frame : trace) {
    System.out.println(frame);
}
```

**Python:** Use the `traceback` module.

```python
import traceback
traceback.print_stack()
```

---

## Stack Overflow Prevention

Stack overflow occurs when a program exhausts the call stack, typically through deep or unbounded recursion. In production systems, this is a crash-level failure that must be prevented.

### Techniques

**1. Tail Call Optimization (TCO)**

Some languages/compilers rewrite tail-recursive calls to reuse the current stack frame. Go and Java do **not** perform TCO. Python does not either (by design). Functional languages (Scheme, Erlang, Scala with `@tailrec`) do.

**2. Convert Recursion to Iteration**

Replace the implicit call stack with an explicit stack data structure. This gives you control over memory allocation.

```go
// Recursive tree traversal -- risk of stack overflow on deep trees
func inorderRecursive(root *TreeNode, result *[]int) {
    if root == nil {
        return
    }
    inorderRecursive(root.Left, result)
    *result = append(*result, root.Val)
    inorderRecursive(root.Right, result)
}

// Iterative version -- bounded by heap, not call stack
func inorderIterative(root *TreeNode) []int {
    var result []int
    stack := []*TreeNode{}
    current := root

    for current != nil || len(stack) > 0 {
        for current != nil {
            stack = append(stack, current)
            current = current.Left
        }
        current = stack[len(stack)-1]
        stack = stack[:len(stack)-1]
        result = append(result, current.Val)
        current = current.Right
    }
    return result
}
```

**3. Trampolining**

A technique where recursive calls return a "thunk" (a function to call next) instead of making the call directly. A loop then repeatedly invokes thunks until a result is produced.

```python
def trampoline(fn, *args):
    result = fn(*args)
    while callable(result):
        result = result()
    return result

def factorial_trampoline(n, acc=1):
    if n <= 1:
        return acc
    return lambda: factorial_trampoline(n - 1, n * acc)

# Usage: trampoline(factorial_trampoline, 100000) -- no stack overflow
```

**4. Depth Limiting**

Set explicit depth limits and abort or switch strategies when exceeded.

```java
public int solve(Node node, int depth, int maxDepth) {
    if (depth > maxDepth) {
        throw new StackOverflowError("Depth limit exceeded: " + maxDepth);
    }
    // ... recursive logic
}
```

---

## Coroutines, Goroutines, and Stack Management

### Go: Growable Stacks

Go goroutines start with a tiny stack (~8 KB) that grows and shrinks dynamically. The runtime uses **contiguous stack copying**: when a goroutine needs more stack space, the runtime allocates a new, larger stack, copies all frames, and updates all pointers.

Key points:
- Goroutines are cheap to create (thousands or millions possible).
- Stack growth triggers a copy (amortized cost is low).
- You **cannot** take the address of a stack variable and expect it to remain valid after a growth event (the compiler handles this).

### Java: Virtual Threads (Project Loom)

Java 21+ virtual threads use **heap-allocated continuation frames** instead of fixed OS thread stacks. When a virtual thread blocks, its stack frames are saved to the heap and the carrier thread is freed.

```java
// Virtual thread with small footprint
Thread.startVirtualThread(() -> {
    // Stack frames live on heap, not OS stack
    deepRecursiveCall(1000);
});
```

### Python: asyncio and Stack Frames

Python coroutines (`async def`) use heap-allocated frame objects. They do not consume call stack space when suspended.

```python
import asyncio

async def process(depth: int):
    if depth <= 0:
        return
    await asyncio.sleep(0)  # yield -- frame is saved on heap
    await process(depth - 1)
```

---

## Stack-Based Virtual Machines

The JVM, CPython, and WebAssembly are **stack-based virtual machines**. Instructions operate on an operand stack rather than named registers.

### JVM Bytecode Example

Java source:

```java
int a = 3;
int b = 4;
int c = a + b;
```

Bytecode (simplified):

```
iconst_3        // push 3            stack: [3]
istore_1        // pop to local 1    stack: []
iconst_4        // push 4            stack: [4]
istore_2        // pop to local 2    stack: []
iload_1         // push local 1      stack: [3]
iload_2         // push local 2      stack: [3, 4]
iadd            // pop 2, push sum   stack: [7]
istore_3        // pop to local 3    stack: []
```

### CPython Bytecode

```python
import dis
def add(a, b):
    return a + b

dis.dis(add)
```

Output:
```
  LOAD_FAST    0 (a)       # push a
  LOAD_FAST    1 (b)       # push b
  BINARY_ADD                # pop a and b, push a+b
  RETURN_VALUE              # pop and return
```

### Why Stack-Based?

- **Simplicity**: Instructions are compact (no register encoding).
- **Portability**: No assumption about number of physical registers.
- **Trade-off**: More instructions needed than register-based VMs (like Lua VM or Dalvik).

---

## Undo/Redo Systems in Production

Production undo/redo goes beyond a simple stack of strings. Real systems use the **Command Pattern** with stacks.

### Architecture

```
User Action --> Command Object --> Execute --> Push to Undo Stack
                                              Clear Redo Stack

Undo        --> Pop Undo Stack --> Reverse --> Push to Redo Stack
Redo        --> Pop Redo Stack --> Execute --> Push to Undo Stack
```

### Go Implementation

```go
type Command interface {
    Execute()
    Undo()
    Description() string
}

type UndoManager struct {
    undoStack []Command
    redoStack []Command
    maxSize   int
}

func NewUndoManager(maxSize int) *UndoManager {
    return &UndoManager{maxSize: maxSize}
}

func (m *UndoManager) Execute(cmd Command) {
    cmd.Execute()
    m.undoStack = append(m.undoStack, cmd)
    if len(m.undoStack) > m.maxSize {
        m.undoStack = m.undoStack[1:]
    }
    m.redoStack = nil // clear redo on new action
}

func (m *UndoManager) Undo() bool {
    if len(m.undoStack) == 0 {
        return false
    }
    cmd := m.undoStack[len(m.undoStack)-1]
    m.undoStack = m.undoStack[:len(m.undoStack)-1]
    cmd.Undo()
    m.redoStack = append(m.redoStack, cmd)
    return true
}

func (m *UndoManager) Redo() bool {
    if len(m.redoStack) == 0 {
        return false
    }
    cmd := m.redoStack[len(m.redoStack)-1]
    m.redoStack = m.redoStack[:len(m.redoStack)-1]
    cmd.Execute()
    m.undoStack = append(m.undoStack, cmd)
    return true
}
```

### Production Considerations

- **Memory limits**: Cap the undo stack size (e.g., 100 actions). Older commands are discarded.
- **Command merging**: Consecutive small edits (e.g., typing characters) should merge into a single "type text" command.
- **Serialization**: For crash recovery, commands should be serializable to disk.
- **Branching**: Some systems (e.g., Vim) support undo trees instead of linear stacks, allowing access to any prior state.

---

## Concurrent Stack -- Treiber Stack

The **Treiber stack** is a classic lock-free concurrent stack that uses Compare-And-Swap (CAS) for thread safety without mutexes.

### Algorithm

1. **Push**: Create a new node. Set its `next` to the current top. CAS the top pointer from old to new. Retry on failure.
2. **Pop**: Read the current top. CAS the top from `top` to `top.next`. Retry on failure.

### Go Implementation (using atomic)

```go
import (
    "sync/atomic"
    "unsafe"
)

type tNode struct {
    val  int
    next unsafe.Pointer // *tNode
}

type TreiberStack struct {
    top unsafe.Pointer // *tNode
}

func (s *TreiberStack) Push(val int) {
    newNode := &tNode{val: val}
    for {
        oldTop := atomic.LoadPointer(&s.top)
        newNode.next = oldTop
        if atomic.CompareAndSwapPointer(&s.top, oldTop, unsafe.Pointer(newNode)) {
            return
        }
    }
}

func (s *TreiberStack) Pop() (int, bool) {
    for {
        oldTop := atomic.LoadPointer(&s.top)
        if oldTop == nil {
            return 0, false
        }
        node := (*tNode)(oldTop)
        newTop := node.next
        if atomic.CompareAndSwapPointer(&s.top, oldTop, newTop) {
            return node.val, true
        }
    }
}
```

### Java Implementation (using AtomicReference)

```java
import java.util.concurrent.atomic.AtomicReference;

public class TreiberStack<T> {
    private static class Node<T> {
        final T val;
        final Node<T> next;
        Node(T val, Node<T> next) {
            this.val = val;
            this.next = next;
        }
    }

    private final AtomicReference<Node<T>> top = new AtomicReference<>();

    public void push(T val) {
        Node<T> newNode = new Node<>(val, null);
        while (true) {
            Node<T> oldTop = top.get();
            newNode = new Node<>(val, oldTop);
            if (top.compareAndSet(oldTop, newNode)) return;
        }
    }

    public T pop() {
        while (true) {
            Node<T> oldTop = top.get();
            if (oldTop == null) return null;
            if (top.compareAndSet(oldTop, oldTop.next)) {
                return oldTop.val;
            }
        }
    }
}
```

### The ABA Problem

CAS-based stacks are susceptible to the **ABA problem**: a thread reads value A, another thread pops A, pushes B, pushes A back. The first thread's CAS succeeds even though the stack changed. Solutions include tagged pointers (version counters) or hazard pointers.

---

## Summary

| Topic                     | Key Insight                                                          |
| ------------------------- | -------------------------------------------------------------------- |
| Call stack internals      | Each frame stores locals, return address, saved registers             |
| Stack overflow prevention | Convert to iteration, trampoline, or limit depth                     |
| Goroutine stacks          | Grow dynamically via contiguous copying; start at 8 KB                |
| Stack-based VMs           | JVM/CPython use operand stacks; compact but more instructions         |
| Undo/redo in production   | Command pattern + bounded stacks + merging + serialization            |
| Treiber stack             | Lock-free concurrent stack using CAS; watch for ABA problem           |

**Next level:** For formal proofs, amortized analysis, and stack-sortable permutations, see [professional.md](professional.md).
