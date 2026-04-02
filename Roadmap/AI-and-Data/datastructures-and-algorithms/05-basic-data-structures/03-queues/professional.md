# Queue -- Professional / Theoretical Level

## Table of Contents

- [Formal Queue ADT Specification](#formal-queue-adt-specification)
  - [Signature](#signature)
  - [Axioms](#axioms)
  - [Algebraic Properties](#algebraic-properties)
- [Amortized Analysis of Queue Operations](#amortized-analysis-of-queue-operations)
  - [Two-Stack Queue: Aggregate Method](#two-stack-queue-aggregate-method)
  - [Accounting Method](#accounting-method)
  - [Potential Method](#potential-method)
  - [Circular Array Queue: Doubling Analysis](#circular-array-queue-doubling-analysis)
- [Lock-Free Queue: Michael-Scott Algorithm](#lock-free-queue-michael-scott-algorithm)
  - [Design Overview](#design-overview)
  - [Pseudocode](#pseudocode)
  - [ABA Problem and Solutions](#aba-problem-and-solutions)
  - [Go: Lock-Free Queue Sketch](#go-lock-free-queue-sketch)
  - [Java: Lock-Free Queue with AtomicReference](#java-lock-free-queue-with-atomicreference)
- [Wait-Free Queues](#wait-free-queues)
  - [Definitions: Lock-Free vs Wait-Free](#definitions-lock-free-vs-wait-free)
  - [Kogan-Petrank Wait-Free Queue](#kogan-petrank-wait-free-queue)
  - [Practical Considerations](#practical-considerations)
- [Lower Bounds for Queue Operations](#lower-bounds-for-queue-operations)
  - [Sequential Lower Bounds](#sequential-lower-bounds)
  - [Concurrent Lower Bounds](#concurrent-lower-bounds)
  - [Space-Time Trade-offs](#space-time-trade-offs)
- [Summary](#summary)

---

## Formal Queue ADT Specification

### Signature

```
ADT Queue<T>

Types:
    T         -- element type
    Size      -- non-negative integer

Operations:
    new() -> Queue<T>
        -- Create an empty queue
        -- Post: isEmpty(result) = true, size(result) = 0

    enqueue(Q: Queue<T>, x: T) -> Queue<T>
        -- Add element x to the rear of Q
        -- Post: size(result) = size(Q) + 1
        -- Post: peek(result) = peek(Q) if not isEmpty(Q), else x

    dequeue(Q: Queue<T>) -> (T, Queue<T>)
        -- Remove and return the front element
        -- Pre:  not isEmpty(Q)
        -- Post: size(snd(result)) = size(Q) - 1

    peek(Q: Queue<T>) -> T
        -- Return the front element without removal
        -- Pre:  not isEmpty(Q)
        -- Post: Q is unchanged

    isEmpty(Q: Queue<T>) -> Boolean
        -- Return true iff Q has no elements
        -- Post: result = (size(Q) == 0)

    size(Q: Queue<T>) -> Size
        -- Return the number of elements
        -- Post: result >= 0
```

### Axioms

The following axioms fully characterize queue behavior. For all elements `x: T` and queues `Q: Queue<T>`:

```
A1. isEmpty(new()) = true
A2. isEmpty(enqueue(Q, x)) = false
A3. peek(enqueue(new(), x)) = x
A4. peek(enqueue(Q, x)) = peek(Q)          when not isEmpty(Q)
A5. dequeue(enqueue(new(), x)) = (x, new())
A6. dequeue(enqueue(Q, x)) = let (y, Q') = dequeue(Q)
                               in (y, enqueue(Q', x))    when not isEmpty(Q)
A7. size(new()) = 0
A8. size(enqueue(Q, x)) = size(Q) + 1
```

### Algebraic Properties

**FIFO property** (derived from axioms A3-A6):
If elements x1, x2, ..., xn are enqueued in that order to an empty queue, then dequeue returns them in the same order: x1, x2, ..., xn.

**Proof sketch:** By induction on the number of enqueue operations.
- Base: `dequeue(enqueue(new(), x1)) = (x1, new())` by A5.
- Inductive step: For queue Q with elements x1..xk, `dequeue(enqueue(Q, x_{k+1}))` returns `(x1, enqueue(Q', x_{k+1}))` by A6, where `dequeue(Q) = (x1, Q')`. The front is always x1.

**Commutativity of enqueue (from the perspective of dequeue order):**
`dequeue^n(enqueue(enqueue(Q, a), b))` produces `a` before `b` if both are enqueued after all existing elements.

---

## Amortized Analysis of Queue Operations

### Two-Stack Queue: Aggregate Method

A queue can be implemented using two stacks: an **inbox** stack (for enqueue) and an **outbox** stack (for dequeue). When the outbox is empty, we reverse the inbox into the outbox.

```
Enqueue(x):  push x onto inbox      -- O(1)
Dequeue():   if outbox is empty:
               transfer all from inbox to outbox (reverse)  -- O(k) for k elements
             pop from outbox         -- O(1)
```

**Aggregate analysis:**
Starting from an empty queue, consider a sequence of n operations (any mix of enqueue/dequeue). Each element is pushed onto inbox exactly once (O(1)), transferred from inbox to outbox exactly once (O(1) amortized), and popped from outbox exactly once (O(1)). Total work for n operations <= 3n. Therefore, the **amortized cost per operation is O(1)**.

### Accounting Method

Assign credits to operations:
- **Enqueue**: charge 3 credits (1 for pushing onto inbox, 1 saved for future transfer, 1 saved for future pop from outbox)
- **Dequeue**: charge 0 credits (paid by the saved credits from enqueue)

Each element carries 2 prepaid credits. When transferred to outbox, it spends 1 credit. When popped from outbox, it spends 1 credit. The total amortized cost of enqueue is O(3) = O(1). The total amortized cost of dequeue is O(0) = O(1). No operation exceeds O(1) amortized.

### Potential Method

Define the potential function:

```
Phi(Q) = |inbox|
```

where `|inbox|` is the number of elements in the inbox stack.

For **enqueue**: actual cost = 1, delta Phi = +1.
Amortized cost = 1 + 1 = **2**.

For **dequeue** (outbox non-empty): actual cost = 1, delta Phi = 0.
Amortized cost = 1 + 0 = **1**.

For **dequeue** (outbox empty, inbox has k elements): actual cost = k + 1 (transfer k elements + 1 pop), delta Phi = -k (inbox becomes empty).
Amortized cost = (k + 1) + (-k) = **1**.

In all cases, the amortized cost is O(1).

### Circular Array Queue: Doubling Analysis

When a circular array queue runs out of capacity, it doubles the array and copies all elements.

Using the **aggregate method**: after n enqueue operations, the total number of copy operations during all resizes is:

```
1 + 2 + 4 + 8 + ... + n/2 + n = 2n - 1
```

Plus n pushes (one per enqueue). Total cost = n + 2n - 1 = 3n - 1 = O(n). Amortized cost per enqueue: O(1).

Using the **accounting method**: charge each enqueue 3 units. 1 unit pays for the enqueue itself. 2 units are saved as credit on the element. When a resize occurs, each of the n/2 "new" elements (added since the last resize) pays 2 credits to copy itself and one "old" element.

---

## Lock-Free Queue: Michael-Scott Algorithm

The **Michael-Scott queue** (1996) is the foundational lock-free concurrent queue algorithm. It uses a singly-linked list with two atomic pointers: `Head` and `Tail`.

### Design Overview

```
Head -> [sentinel] -> [node1] -> [node2] -> [node3] -> nil
                                                ^
                                              Tail
```

Key ideas:
- A **sentinel node** (dummy) at the front simplifies edge cases
- **Enqueue**: CAS the `next` pointer of the current tail node, then CAS `Tail` to the new node
- **Dequeue**: CAS `Head` to `Head.next`, return the value from the old `Head.next`
- If a CAS fails, retry (another thread made progress)
- A "helping" mechanism advances `Tail` if it lags behind

### Pseudocode

```
Enqueue(Q, value):
    node = new Node(value, next=nil)
    loop:
        tail = Q.Tail
        next = tail.next
        if tail == Q.Tail:                         // consistent read
            if next == nil:                        // tail is truly the last node
                if CAS(&tail.next, nil, node):     // link new node
                    CAS(&Q.Tail, tail, node)       // advance tail (best effort)
                    return
            else:
                CAS(&Q.Tail, tail, next)           // help: advance lagging tail

Dequeue(Q):
    loop:
        head = Q.Head
        tail = Q.Tail
        next = head.next
        if head == Q.Head:                         // consistent read
            if head == tail:                       // queue may be empty
                if next == nil:
                    return EMPTY                   // truly empty
                CAS(&Q.Tail, tail, next)           // help: advance lagging tail
            else:
                value = next.value
                if CAS(&Q.Head, head, next):       // advance head
                    free(head)                     // reclaim old sentinel
                    return value
```

### ABA Problem and Solutions

The **ABA problem**: a CAS succeeds because the pointer value is the same (A), but the node was freed and a new node was allocated at the same address (A -> B -> A). The CAS sees "A" and incorrectly succeeds.

Solutions:
- **Tagged pointers**: pair the pointer with a monotonically increasing counter. CAS on the (pointer, counter) pair. Even if the pointer returns to A, the counter has changed.
- **Hazard pointers**: each thread publishes which nodes it is currently accessing. Nodes are not freed until no thread references them.
- **Epoch-based reclamation**: threads enter/exit epochs; garbage is freed only when all threads have advanced past the epoch in which it was retired.
- **Garbage collection**: languages with GC (Java, Go) largely avoid ABA because freed nodes are not reused until no references exist.

### Go: Lock-Free Queue Sketch

```go
package main

import (
	"sync/atomic"
	"unsafe"
)

type node struct {
	value int
	next  unsafe.Pointer // *node
}

type LockFreeQueue struct {
	head unsafe.Pointer // *node
	tail unsafe.Pointer // *node
}

func NewLockFreeQueue() *LockFreeQueue {
	sentinel := &node{}
	q := &LockFreeQueue{
		head: unsafe.Pointer(sentinel),
		tail: unsafe.Pointer(sentinel),
	}
	return q
}

func (q *LockFreeQueue) Enqueue(value int) {
	newNode := &node{value: value}
	for {
		tail := (*node)(atomic.LoadPointer(&q.tail))
		next := atomic.LoadPointer(&tail.next)
		if tail == (*node)(atomic.LoadPointer(&q.tail)) {
			if next == nil {
				if atomic.CompareAndSwapPointer(&tail.next, nil, unsafe.Pointer(newNode)) {
					atomic.CompareAndSwapPointer(&q.tail, unsafe.Pointer(tail), unsafe.Pointer(newNode))
					return
				}
			} else {
				atomic.CompareAndSwapPointer(&q.tail, unsafe.Pointer(tail), next)
			}
		}
	}
}

func (q *LockFreeQueue) Dequeue() (int, bool) {
	for {
		head := (*node)(atomic.LoadPointer(&q.head))
		tail := (*node)(atomic.LoadPointer(&q.tail))
		next := (*node)(atomic.LoadPointer(&head.next))
		if head == (*node)(atomic.LoadPointer(&q.head)) {
			if head == tail {
				if next == nil {
					return 0, false // empty
				}
				atomic.CompareAndSwapPointer(&q.tail, unsafe.Pointer(tail), unsafe.Pointer(next))
			} else {
				value := next.value
				if atomic.CompareAndSwapPointer(&q.head, unsafe.Pointer(head), unsafe.Pointer(next)) {
					return value, true
				}
			}
		}
	}
}
```

### Java: Lock-Free Queue with AtomicReference

```java
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> {
    private static class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next = new AtomicReference<>(null);
        Node(T value) { this.value = value; }
    }

    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;

    public LockFreeQueue() {
        Node<T> sentinel = new Node<>(null);
        head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        while (true) {
            Node<T> curTail = tail.get();
            Node<T> next = curTail.next.get();
            if (curTail == tail.get()) {
                if (next == null) {
                    if (curTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(curTail, newNode);
                        return;
                    }
                } else {
                    tail.compareAndSet(curTail, next); // help advance
                }
            }
        }
    }

    public T dequeue() {
        while (true) {
            Node<T> curHead = head.get();
            Node<T> curTail = tail.get();
            Node<T> next = curHead.next.get();
            if (curHead == head.get()) {
                if (curHead == curTail) {
                    if (next == null) return null; // empty
                    tail.compareAndSet(curTail, next);
                } else {
                    T value = next.value;
                    if (head.compareAndSet(curHead, next)) {
                        return value;
                    }
                }
            }
        }
    }
}
```

---

## Wait-Free Queues

### Definitions: Lock-Free vs Wait-Free

| Property    | Guarantee                                                                 |
| ----------- | ------------------------------------------------------------------------- |
| Lock-free   | At least one thread makes progress in a finite number of steps (global progress) |
| Wait-free   | Every thread completes its operation in a bounded number of steps (per-thread progress) |
| Obstruction-free | A thread completes if it runs in isolation (no contention guarantee) |

Wait-free is strictly stronger than lock-free. Lock-free algorithms can starve individual threads under high contention. Wait-free algorithms prevent starvation entirely.

### Kogan-Petrank Wait-Free Queue

The **Kogan-Petrank queue** (2011) extends the Michael-Scott queue with a **helping mechanism**:
- Each thread publishes a **pending operation descriptor** in a shared array
- If a thread fails to complete its operation, other threads detect the pending operation and help complete it
- After helping, the original thread sees its operation is done and returns
- Bounded number of steps: each thread helps at most O(P) other threads, where P is the number of threads

This ensures every enqueue and dequeue completes in O(P) steps, regardless of contention.

### Practical Considerations

In practice, wait-free queues are rarely used because:
1. The helping mechanism adds overhead (2-5x slower than lock-free in low contention)
2. Lock-free queues with exponential backoff provide near-fair scheduling
3. Hardware transactional memory (HTM) can provide lock-free semantics with less overhead
4. Most real-world systems prefer lock-free + back-off over wait-free

Wait-free is important in hard real-time systems (avionics, medical devices) where per-thread progress bounds are required by certification standards.

---

## Lower Bounds for Queue Operations

### Sequential Lower Bounds

For a sequential queue storing n elements:
- **Enqueue/dequeue**: Omega(1) -- cannot be faster than constant time per operation
- **Space**: Omega(n) -- must store all n elements
- These are tight: the circular array achieves O(1) time and O(n) space

### Concurrent Lower Bounds

**Theorem (Attiya, Hendler, Woelfel 2008):** Any linearizable concurrent queue implementation from CAS has operations that take Omega(log P) steps in the worst case, where P is the number of threads.

**Intuition:** when P threads contend on the same queue, the CAS operations create a serialization bottleneck. Even with helping, there is a fundamental contention lower bound.

**Theorem (Jayanti-Petrovic 2005):** Any wait-free queue from read/write registers requires Omega(n) space for n concurrent operations.

### Space-Time Trade-offs

| Implementation           | Enqueue (amortized) | Dequeue (amortized) | Space     | Progress      |
| ------------------------ | ------------------- | ------------------- | --------- | ------------- |
| Circular array           | O(1)                | O(1)                | O(n)      | Sequential    |
| Two-stack queue          | O(1)                | O(1)                | O(n)      | Sequential    |
| Michael-Scott (CAS)      | O(1) expected       | O(1) expected       | O(n)      | Lock-free     |
| Kogan-Petrank            | O(P)                | O(P)                | O(n + P)  | Wait-free     |
| Array-based bounded (CAS)| O(1) expected       | O(1) expected       | O(capacity)| Lock-free    |

---

## Summary

| Concept                | Key Takeaway                                                              |
| ---------------------- | ------------------------------------------------------------------------- |
| Formal ADT             | Queue is fully characterized by 8 axioms on new, enqueue, dequeue, peek  |
| Amortized analysis     | Two-stack queue achieves O(1) amortized via aggregate/accounting/potential|
| Michael-Scott queue    | Lock-free CAS-based queue with sentinel node and helping mechanism       |
| ABA problem            | Solved by tagged pointers, hazard pointers, or epoch-based reclamation   |
| Wait-free queue        | Guarantees per-thread bounded steps; Kogan-Petrank adds O(P) helping     |
| Lower bounds           | Concurrent queue operations have Omega(log P) worst-case step complexity |
