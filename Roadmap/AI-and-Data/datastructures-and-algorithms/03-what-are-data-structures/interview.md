# What are Data Structures? — Interview Questions

## Table of Contents

1. [Junior-Level Questions](#junior-level-questions)
2. [Middle-Level Questions](#middle-level-questions)
3. [Senior-Level Questions](#senior-level-questions)
4. [Professional-Level Questions](#professional-level-questions)
5. [Coding Challenge: MinStack](#coding-challenge-minstack)

---

## Junior-Level Questions

### Q1: What is a data structure?

**Answer:**

A data structure is a way of organizing and storing data in a computer so that it can be accessed and modified efficiently. It defines what operations can be performed (insert, delete, search) and what time/space guarantees those operations provide.

Every program uses data structures. A variable is stored in memory. A string is an array of characters. Choosing the right data structure is the most important design decision you make because it determines the performance of your code.

---

### Q2: Name 5 common data structures and give a one-sentence description of each.

**Answer:**

1. **Array** — A contiguous block of memory that stores elements accessible by index in O(1) time.
2. **Linked List** — A chain of nodes where each node holds data and a pointer to the next node, allowing O(1) insertion at the head.
3. **Hash Table** — A key-value store that uses a hash function to map keys to array indices, providing O(1) average lookups.
4. **Stack** — A LIFO (Last-In, First-Out) collection where you push and pop from the top.
5. **Queue** — A FIFO (First-In, First-Out) collection where you enqueue at the back and dequeue from the front.

---

### Q3: What is the difference between an array and a linked list?

**Answer:**

| Feature | Array | Linked List |
|---|---|---|
| Memory layout | Contiguous | Scattered (nodes linked by pointers) |
| Access by index | O(1) | O(n) |
| Insert at beginning | O(n) (shift all) | O(1) (update head pointer) |
| Insert at end | O(1) amortized | O(1) with tail pointer |
| Cache performance | Excellent (spatial locality) | Poor (pointer chasing) |
| Memory overhead | None | One pointer per node (two for doubly linked) |
| Size | Fixed or dynamically resized | Always dynamic |

**When to choose an array:** You need fast random access and the data size is relatively stable.

**When to choose a linked list:** You need frequent insertions/deletions at arbitrary positions and do not need random access.

---

### Q4: What is Big-O notation and why does it matter for data structures?

**Answer:**

Big-O notation describes the **upper bound** of how an operation's time or space grows as the input size (n) increases. It ignores constants and lower-order terms.

| Big-O | Name | Example |
|---|---|---|
| O(1) | Constant | Array access by index, hash table lookup |
| O(log n) | Logarithmic | Binary search, BST lookup |
| O(n) | Linear | Linear search, linked list traversal |
| O(n log n) | Linearithmic | Merge sort, heap sort |
| O(n^2) | Quadratic | Bubble sort, nested loops |

It matters because at scale, the difference is enormous. O(n) with n=1,000,000 means 1 million operations. O(n^2) means 1 trillion. Choosing a hash table (O(1) lookup) over a list (O(n) lookup) can be the difference between a program that runs in milliseconds and one that runs for hours.

---

## Middle-Level Questions

### Q5: How does a hash table degrade from O(1) to O(n)?

**Answer:**

A hash table achieves O(1) average case when keys are distributed uniformly across buckets. It degrades to O(n) when:

1. **Poor hash function** — If the hash function maps many keys to the same index, all those keys end up in the same bucket (collision chain). In the worst case, all n keys map to one bucket, and the hash table becomes a linked list.

2. **High load factor** — The load factor is `n / capacity`. As it approaches 1.0 (or exceeds it), collisions become more frequent. Most implementations resize when the load factor exceeds a threshold (0.75 in Java's HashMap).

3. **Adversarial input** — An attacker can craft keys that all hash to the same bucket, causing worst-case O(n) for every operation. This is called a **hash collision attack** (HashDoS). Mitigations include randomized hash seeds and switching from chaining to balanced trees at high collision counts (Java 8+ converts chains to red-black trees when a bucket has 8+ entries).

**Prevention:**
- Use a well-distributed hash function (e.g., MurmurHash, SipHash)
- Keep the load factor below 0.75
- Resize the table when the threshold is exceeded
- Use a randomized hash seed to prevent adversarial attacks

---

### Q6: How would you implement a stack using two queues?

**Answer:**

**Approach (costly push):**
- Maintain two queues: `q1` (main) and `q2` (helper).
- **Push(x):** Enqueue x into `q2`. Then dequeue everything from `q1` and enqueue into `q2`. Swap `q1` and `q2`.
- **Pop():** Dequeue from `q1`.
- Push is O(n), Pop is O(1).

**Approach (costly pop):**
- **Push(x):** Enqueue x into `q1`. O(1).
- **Pop():** Dequeue all elements except the last from `q1` into `q2`. Dequeue the last element (this is the "top"). Swap `q1` and `q2`.
- Push is O(1), Pop is O(n).

Both approaches ensure LIFO ordering using FIFO queues.

---

### Q7: When should you use a BST instead of a hash table?

**Answer:**

| Requirement | BST | Hash Table |
|---|---|---|
| Ordered traversal | Yes (in-order) | No |
| Range queries (find all keys between A and B) | O(log n + k) | O(n) |
| Find min/max | O(log n) or O(1) with pointer | O(n) |
| Guaranteed worst-case | O(log n) if balanced | O(n) if many collisions |
| Memory | Predictable | May waste space (empty buckets) |

**Use a BST when:**
- You need data in sorted order
- You need range queries or nearest-neighbor lookups
- You need guaranteed O(log n) worst-case (red-black tree, AVL tree)
- Your keys are not easily hashable

**Use a hash table when:**
- You only need lookup/insert/delete by exact key
- You want O(1) average performance
- Ordering does not matter

---

## Senior-Level Questions

### Q8: Design an LRU Cache. What data structures would you use and why?

**Answer:**

An **LRU (Least Recently Used) Cache** evicts the least recently accessed entry when the cache is full.

**Required operations (all O(1)):**
- `get(key)` — Return value if key exists, mark as recently used.
- `put(key, value)` — Insert or update. If full, evict the least recently used entry.

**Data structures:**
1. **Hash Map** — Maps keys to linked list nodes for O(1) lookup.
2. **Doubly Linked List** — Maintains access order. Most recently used at the head, least recently used at the tail.

**How it works:**
- `get(key)`: Look up in hash map (O(1)). Move the node to the head of the linked list (O(1)).
- `put(key, value)`: If key exists, update value and move to head. If full, remove the tail node (O(1)), delete its key from the hash map (O(1)), then insert the new node at the head.

**Why this combination?**
- Hash map alone cannot track access order efficiently.
- Linked list alone cannot provide O(1) lookup by key.
- Together, they give O(1) for all operations.

---

### Q9: What data structures would you choose for a messaging system?

**Answer:**

A messaging system has several requirements, each served by different data structures:

1. **Message Queue (delivery order):** Use a **queue** (FIFO) for ordered message delivery. For priority messages, use a **priority queue** (min-heap) keyed by priority and timestamp.

2. **User lookup:** Use a **hash table** mapping user ID to user metadata and their message inbox.

3. **Conversation threads:** Use a **hash table** mapping conversation ID to a **doubly linked list** of messages (for efficient insertion at the end and pagination from either direction).

4. **Unread count:** Use a **hash table** mapping user ID to an integer counter, or a **sorted set** (like Redis ZSET) for ranked notification feeds.

5. **Message search:** Use an **inverted index** (hash map from word to list of message IDs) or a **trie** for prefix-based autocomplete search.

6. **Online status:** Use a **hash set** of currently online user IDs for O(1) membership check.

7. **Rate limiting:** Use a **circular buffer** or **sliding window counter** per user to track message frequency.

---

## Professional-Level Questions

### Q10: Prove that dynamic array append is amortized O(1).

**Answer:**

**Setup:** A dynamic array doubles its capacity when full. Starting capacity is 1.

**Aggregate method:**

After n appends, the total cost (including copies during resizing) is:

- n appends cost 1 each = n
- Resizing copies happen at sizes 1, 2, 4, 8, ..., up to the largest power of 2 <= n
- Total copy cost = 1 + 2 + 4 + 8 + ... + n/2 + n = 2n - 1

Total cost = n + (2n - 1) = 3n - 1

Amortized cost per operation = (3n - 1) / n < 3 = O(1).

**Potential method (Banker's method):**

Assign each append a cost of 3 "coins":
- 1 coin to pay for the append itself.
- 2 coins saved for future copying.

When the array doubles from capacity k to 2k, we need to copy k elements. The k/2 elements inserted since the last resize each saved 2 coins, giving k coins total — exactly enough to pay for the k copies.

Therefore, the amortized cost per append is 3, which is O(1).

---

### Q11: Prove the comparison-sort lower bound of Omega(n log n).

**Answer:**

**Theorem:** Any comparison-based sorting algorithm requires at least Omega(n log n) comparisons in the worst case.

**Proof via decision tree:**

1. A comparison-based sort can be modeled as a binary decision tree where each internal node represents a comparison (a[i] < a[j]?), left child = yes, right child = no.

2. The leaves of the tree represent the possible output permutations. There are n! permutations of n elements.

3. A binary tree of height h has at most 2^h leaves.

4. For the tree to have at least n! leaves: 2^h >= n!

5. Taking log base 2: h >= log2(n!)

6. By Stirling's approximation: log2(n!) = n log2(n) - n log2(e) + O(log n) = Theta(n log n)

7. Therefore h = Omega(n log n).

Since the height of the decision tree equals the worst-case number of comparisons, any comparison sort must make at least Omega(n log n) comparisons.

**Implication:** Algorithms like merge sort and heap sort are asymptotically optimal. Faster sorting (e.g., radix sort at O(nk)) is only possible by exploiting non-comparison properties of the data.

---

## Coding Challenge: MinStack

### Problem

Design a stack that supports the following operations, all in O(1) time:

- `push(val)` — Push an element onto the stack.
- `pop()` — Remove the element on top of the stack.
- `top()` — Get the top element.
- `getMin()` — Retrieve the minimum element in the stack.

**Constraint:** All operations must be O(1).

### Approach

Maintain two stacks:
1. **Main stack** — holds all values.
2. **Min stack** — holds the current minimum. When pushing, push the new minimum (min of val and current min). When popping, pop from both.

---

### Solution: Go

```go
package main

import "fmt"

type MinStack struct {
    stack    []int
    minStack []int
}

func NewMinStack() *MinStack {
    return &MinStack{}
}

func (s *MinStack) Push(val int) {
    s.stack = append(s.stack, val)
    if len(s.minStack) == 0 || val <= s.minStack[len(s.minStack)-1] {
        s.minStack = append(s.minStack, val)
    } else {
        s.minStack = append(s.minStack, s.minStack[len(s.minStack)-1])
    }
}

func (s *MinStack) Pop() {
    if len(s.stack) == 0 {
        return
    }
    s.stack = s.stack[:len(s.stack)-1]
    s.minStack = s.minStack[:len(s.minStack)-1]
}

func (s *MinStack) Top() int {
    return s.stack[len(s.stack)-1]
}

func (s *MinStack) GetMin() int {
    return s.minStack[len(s.minStack)-1]
}

func main() {
    ms := NewMinStack()
    ms.Push(5)
    ms.Push(3)
    ms.Push(7)
    ms.Push(1)

    fmt.Println("Top:", ms.Top())       // 1
    fmt.Println("Min:", ms.GetMin())    // 1

    ms.Pop()
    fmt.Println("Top:", ms.Top())       // 7
    fmt.Println("Min:", ms.GetMin())    // 3

    ms.Pop()
    fmt.Println("Top:", ms.Top())       // 3
    fmt.Println("Min:", ms.GetMin())    // 3

    ms.Pop()
    fmt.Println("Top:", ms.Top())       // 5
    fmt.Println("Min:", ms.GetMin())    // 5
}
```

### Solution: Java

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class MinStack {
    private Deque<Integer> stack;
    private Deque<Integer> minStack;

    public MinStack() {
        stack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        } else {
            minStack.push(minStack.peek());
        }
    }

    public void pop() {
        stack.pop();
        minStack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }

    public static void main(String[] args) {
        MinStack ms = new MinStack();
        ms.push(5);
        ms.push(3);
        ms.push(7);
        ms.push(1);

        System.out.println("Top: " + ms.top());       // 1
        System.out.println("Min: " + ms.getMin());    // 1

        ms.pop();
        System.out.println("Top: " + ms.top());       // 7
        System.out.println("Min: " + ms.getMin());    // 3

        ms.pop();
        System.out.println("Top: " + ms.top());       // 3
        System.out.println("Min: " + ms.getMin());    // 3

        ms.pop();
        System.out.println("Top: " + ms.top());       // 5
        System.out.println("Min: " + ms.getMin());    // 5
    }
}
```

### Solution: Python

```python
class MinStack:
    def __init__(self):
        self.stack = []
        self.min_stack = []

    def push(self, val: int) -> None:
        self.stack.append(val)
        if not self.min_stack or val <= self.min_stack[-1]:
            self.min_stack.append(val)
        else:
            self.min_stack.append(self.min_stack[-1])

    def pop(self) -> None:
        self.stack.pop()
        self.min_stack.pop()

    def top(self) -> int:
        return self.stack[-1]

    def get_min(self) -> int:
        return self.min_stack[-1]


if __name__ == "__main__":
    ms = MinStack()
    ms.push(5)
    ms.push(3)
    ms.push(7)
    ms.push(1)

    print("Top:", ms.top())       # 1
    print("Min:", ms.get_min())   # 1

    ms.pop()
    print("Top:", ms.top())       # 7
    print("Min:", ms.get_min())   # 3

    ms.pop()
    print("Top:", ms.top())       # 3
    print("Min:", ms.get_min())   # 3

    ms.pop()
    print("Top:", ms.top())       # 5
    print("Min:", ms.get_min())   # 5
```

### Complexity Analysis

| Operation | Time | Space |
|---|---|---|
| push | O(1) | O(1) per element |
| pop | O(1) | O(1) |
| top | O(1) | O(1) |
| getMin | O(1) | O(1) |
| Total space | — | O(n) for n elements |

### Key Insight

The trick is that the minimum can only change when we push or pop. By mirroring every push/pop on the min stack, we always know the current minimum without scanning.

### Space Optimization

Instead of storing the minimum for every element, you can push to the min stack only when the new value is less than or equal to the current minimum. On pop, only pop the min stack if the popped value equals the current minimum. This reduces space usage when many pushed values are larger than the minimum.

---

> **Tip for interviews:** Always clarify edge cases — what happens when you pop/top/getMin on an empty stack? In production code, you would throw an exception or return an error.
