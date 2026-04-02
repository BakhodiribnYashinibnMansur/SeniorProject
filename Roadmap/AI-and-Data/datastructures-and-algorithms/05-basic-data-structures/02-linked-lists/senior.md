# Linked Lists -- Senior Level

## Prerequisites

- Solid understanding of singly and doubly linked lists
- Familiarity with concurrent programming and CAS operations
- Understanding of memory management and garbage collection

## Table of Contents

1. [Linked Lists in Production Systems](#linked-lists-in-production-systems)
2. [Lock-Free Linked Lists](#lock-free-linked-lists)
3. [Skip Lists](#skip-lists)
4. [XOR Linked List](#xor-linked-list)
5. [Persistent Linked Lists](#persistent-linked-lists)
6. [Garbage Collection Implications](#garbage-collection-implications)
7. [Summary](#summary)

---

## Linked Lists in Production Systems

### Operating System Process Lists

Most operating systems maintain processes in linked lists. The Linux kernel uses a circular doubly linked list (`struct list_head`) to manage the task list, scheduling queues, and device driver chains.

```c
// Linux kernel pattern (simplified)
struct list_head {
    struct list_head *next, *prev;
};

struct task_struct {
    pid_t pid;
    struct list_head tasks;  // embedded linked list node
    // ... hundreds of other fields
};
```

The kernel uses **intrusive linked lists**: the list node is embedded inside the data structure rather than wrapping it. This avoids extra allocations and allows a single struct to be on multiple lists simultaneously.

### Memory Allocators (Free Lists)

Memory allocators like `malloc` maintain free lists -- linked lists of available memory blocks. When you free memory, the block is added to a free list. When you allocate, the allocator searches the free list for a suitable block.

```
Free list: [64 bytes] -> [128 bytes] -> [32 bytes] -> [256 bytes] -> nil
```

Strategies:
- **First fit** -- return the first block that is large enough.
- **Best fit** -- return the smallest block that is large enough.
- **Segregated free lists** -- maintain separate lists for different size classes (used by jemalloc, tcmalloc).

### Undo/Redo Systems

Text editors and design tools implement undo/redo using a doubly linked list of states (or commands). The current state is a pointer into the list. Undo moves backward; redo moves forward.

```
[state1] <-> [state2] <-> [state3] <-> [state4]
                            ^
                         current
              Undo <---             ---> Redo
```

When the user makes a new change after undoing, all redo states are discarded (the forward portion of the list is truncated).

### Transaction Logs

Database systems use linked lists for write-ahead logs (WAL). Each log entry points to the previous entry, allowing the system to replay or roll back transactions in order.

---

## Lock-Free Linked Lists

In concurrent systems, traditional mutexes can cause contention, priority inversion, and deadlock. **Lock-free data structures** use atomic operations (Compare-And-Swap) instead of locks.

### Compare-And-Swap (CAS)

CAS atomically compares a memory location to an expected value and, if they match, updates it to a new value. It returns whether the swap succeeded.

```
CAS(address, expected, new) -> bool
```

### Lock-Free Singly Linked List (Insert at Head)

```go
import (
    "sync/atomic"
    "unsafe"
)

type LFNode struct {
    Data int
    Next unsafe.Pointer // *LFNode stored as unsafe.Pointer
}

type LFList struct {
    Head unsafe.Pointer // *LFNode
}

func (l *LFList) InsertAtHead(data int) {
    newNode := &LFNode{Data: data}
    for {
        oldHead := atomic.LoadPointer(&l.Head)
        newNode.Next = oldHead
        if atomic.CompareAndSwapPointer(&l.Head, oldHead, unsafe.Pointer(newNode)) {
            return // success
        }
        // CAS failed -- another thread modified head, retry
    }
}
```

### Java (using AtomicReference)

```java
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeList<T> {
    static class LFNode<T> {
        final T data;
        volatile LFNode<T> next;

        LFNode(T data, LFNode<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private final AtomicReference<LFNode<T>> head = new AtomicReference<>(null);

    public void insertAtHead(T data) {
        LFNode<T> newNode = new LFNode<>(data, null);
        while (true) {
            LFNode<T> oldHead = head.get();
            newNode.next = oldHead;
            if (head.compareAndSet(oldHead, newNode)) {
                return;
            }
        }
    }
}
```

### The ABA Problem

CAS can suffer from the ABA problem: a value changes from A to B and back to A. CAS sees A and thinks nothing changed, but the underlying structure may have been modified. Solutions:

- **Tagged pointers** -- attach a version counter to each pointer.
- **Hazard pointers** -- protect nodes from being reclaimed while in use.
- **Epoch-based reclamation** -- defer memory reclamation until all threads have passed through an epoch boundary.

### Lock-Free Deletion (Harris's Algorithm)

Michael and Harris proposed a lock-free deletion algorithm that uses **marked pointers**: before physically removing a node, you mark its `next` pointer (using the lowest bit, since pointers are word-aligned). This tells other threads that the node is logically deleted. Then a subsequent CAS physically unlinks it.

```
Step 1: Mark node B as deleted (logical delete)
  [A] -> [B*] -> [C]     (* = marked)

Step 2: CAS A.next from B to C (physical delete)
  [A] -> [C]
```

---

## Skip Lists

A **skip list** is a probabilistic data structure built from multiple layers of sorted linked lists. Each layer "skips" over some elements, enabling O(log n) average search, insert, and delete.

```
Level 3: head -----------------------------------------> nil
Level 2: head --------> [3] -------------------------> nil
Level 1: head --------> [3] --------> [7] -----------> nil
Level 0: head -> [1] -> [3] -> [5] -> [7] -> [9] ---> nil
```

### How it works

- **Level 0** contains all elements in sorted order.
- Each higher level contains a subset of elements from the level below.
- On insertion, a random "height" is chosen (typically geometric distribution with p=0.5).

### Search

Start at the top-left. Move right if the next element is less than the target. Move down if the next element is greater or equal. This gives O(log n) average time.

### Redis Sorted Sets

Redis uses skip lists (combined with a hash table) for its sorted set (`ZSET`) data type. Skip lists were chosen over balanced BSTs because:

1. **Simpler implementation** -- easier to maintain and debug.
2. **Better range query performance** -- traversing a range is just following `next` pointers at level 0.
3. **Easier concurrent access** -- locking a skip list is more fine-grained than locking a tree.
4. **Comparable performance** -- O(log n) average case matches balanced BSTs.

### Go Implementation (Simplified)

```go
import (
    "math/rand"
)

const MaxLevel = 16

type SkipNode struct {
    Key     int
    Value   interface{}
    Forward []*SkipNode // forward[i] = next node at level i
}

type SkipList struct {
    Header *SkipNode
    Level  int
}

func NewSkipList() *SkipList {
    header := &SkipNode{Forward: make([]*SkipNode, MaxLevel)}
    return &SkipList{Header: header, Level: 0}
}

func randomLevel() int {
    level := 0
    for level < MaxLevel-1 && rand.Float64() < 0.5 {
        level++
    }
    return level
}

func (sl *SkipList) Search(key int) (interface{}, bool) {
    current := sl.Header
    for i := sl.Level; i >= 0; i-- {
        for current.Forward[i] != nil && current.Forward[i].Key < key {
            current = current.Forward[i]
        }
    }
    current = current.Forward[0]
    if current != nil && current.Key == key {
        return current.Value, true
    }
    return nil, false
}

func (sl *SkipList) Insert(key int, value interface{}) {
    update := make([]*SkipNode, MaxLevel)
    current := sl.Header

    for i := sl.Level; i >= 0; i-- {
        for current.Forward[i] != nil && current.Forward[i].Key < key {
            current = current.Forward[i]
        }
        update[i] = current
    }

    level := randomLevel()
    if level > sl.Level {
        for i := sl.Level + 1; i <= level; i++ {
            update[i] = sl.Header
        }
        sl.Level = level
    }

    newNode := &SkipNode{
        Key:     key,
        Value:   value,
        Forward: make([]*SkipNode, level+1),
    }
    for i := 0; i <= level; i++ {
        newNode.Forward[i] = update[i].Forward[i]
        update[i].Forward[i] = newNode
    }
}
```

---

## XOR Linked List

An XOR linked list stores `prev XOR next` in a single pointer field instead of storing both `prev` and `next`. This halves the pointer overhead per node.

```
Address:   A        B        C        D
npx:     0^B     A^C      B^D      C^0
```

To traverse forward from node B (knowing we came from A):
```
next = npx(B) XOR A = (A^C) XOR A = C
```

To traverse backward from node C (knowing we came from D):
```
prev = npx(C) XOR D = (B^D) XOR D = B
```

### Limitations

- Cannot be implemented in garbage-collected languages (GC cannot trace XOR'd pointers).
- Primarily of theoretical interest; rarely used in practice.
- Debugging is extremely difficult.
- C/C++ only; requires `uintptr_t` for pointer arithmetic.

```c
// C pseudocode
struct XORNode {
    int data;
    uintptr_t npx; // prev XOR next
};

uintptr_t xor_addr(XORNode *a, XORNode *b) {
    return (uintptr_t)a ^ (uintptr_t)b;
}

// Traverse forward
void traverse(XORNode *head) {
    XORNode *prev = NULL;
    XORNode *curr = head;
    while (curr != NULL) {
        printf("%d ", curr->data);
        XORNode *next = (XORNode *)(curr->npx ^ (uintptr_t)prev);
        prev = curr;
        curr = next;
    }
}
```

---

## Persistent Linked Lists

A **persistent data structure** preserves all previous versions of itself when modified. Linked lists are naturally suited for persistence because of structural sharing.

### Path Copying

When you insert at the head of a singly linked list, the old list still exists:

```
Original: head1 -> [B] -> [C] -> [D] -> nil
Insert A: head2 -> [A] -> [B] -> [C] -> [D] -> nil
```

`head1` and `head2` share nodes B, C, D. This is **structural sharing** -- no data is duplicated. Both versions of the list coexist.

### Functional Programming

Persistent linked lists are fundamental in functional programming languages:

- **Haskell** -- lists are singly linked and immutable by default.
- **Clojure** -- persistent data structures are a core language feature.
- **Scala** -- `List` is an immutable singly linked list.

```python
# Simulating persistent list in Python
class PersistentList:
    def __init__(self, head_node=None):
        self.head = head_node

    def prepend(self, data):
        """Return a NEW list with data at the front. Original is unchanged."""
        node = Node(data)
        node.next = self.head
        return PersistentList(node)

    def tail(self):
        """Return a NEW list without the first element."""
        if self.head is None:
            raise ValueError("Empty list")
        return PersistentList(self.head.next)

    def head_value(self):
        if self.head is None:
            raise ValueError("Empty list")
        return self.head.data
```

---

## Garbage Collection Implications

### Reference Cycles

In reference-counted GC systems (Python's default), circular linked lists create reference cycles that the basic reference counter cannot collect. Python uses a **cycle detector** (generational GC) to handle this, but it adds overhead.

```python
# This creates a reference cycle
a = Node(1)
b = Node(2)
a.next = b
b.next = a  # cycle -- refcount of a and b never reaches 0
```

### GC Pressure from Many Small Objects

Each node in a linked list is a separate heap allocation. A linked list with 1 million nodes means 1 million small objects for the garbage collector to track. This causes:

- **GC pause spikes** -- more objects to scan during mark phase.
- **Memory fragmentation** -- small objects scattered across the heap.
- **Poor cache utilization** -- pointer chasing causes cache misses.

### Mitigations

1. **Unrolled linked lists** -- store multiple elements per node to reduce allocation count.
2. **Object pooling** -- pre-allocate a pool of nodes and reuse them.
3. **Arena allocation** -- allocate all nodes from a contiguous memory region.

```go
// Unrolled linked list node -- stores multiple values per node
type UnrolledNode struct {
    Data [64]int   // store up to 64 elements per node
    Count int      // how many elements are actually used
    Next  *UnrolledNode
}
```

This reduces the number of objects by 64x while maintaining linked list semantics.

---

## Summary

| Topic                  | Key Insight                                                    |
|------------------------|----------------------------------------------------------------|
| OS process lists       | Intrusive linked lists avoid extra allocations                 |
| Free lists             | Memory allocators chain free blocks via linked lists           |
| Undo/Redo              | Doubly linked list of states with a current pointer            |
| Lock-free insert       | CAS loop retries on contention; no locks needed                |
| ABA problem            | Tagged pointers or epoch-based reclamation prevent it          |
| Harris deletion        | Mark-then-CAS two-phase deletion for lock-free lists           |
| Skip lists             | Multi-level sorted lists; O(log n) avg; used in Redis          |
| XOR linked list        | Halves pointer overhead but impractical in GC languages        |
| Persistent lists       | Structural sharing enables immutable versioned lists           |
| GC implications        | Many small nodes cause GC pressure; mitigate with unrolling    |

**Next step:** Move to the professional level for formal proofs, amortized analysis, and cache-oblivious structures.
