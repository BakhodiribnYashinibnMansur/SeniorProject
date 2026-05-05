# Constant Time O(1) -- Tasks

## Table of Contents

1. [Task 1: Array Element Swap](#task-1-array-element-swap)
2. [Task 2: Stack Min in O(1)](#task-2-stack-min-in-o1)
3. [Task 3: Hash Set Implementation](#task-3-hash-set-implementation)
4. [Task 4: Two-Sum with O(1) Lookup](#task-4-two-sum-with-o1-lookup)
5. [Task 5: Queue Using Two Stacks](#task-5-queue-using-two-stacks)
6. [Task 6: First Unique Character](#task-6-first-unique-character)
7. [Task 7: LRU Cache](#task-7-lru-cache)
8. [Task 8: Insert Delete GetRandom O(1)](#task-8-insert-delete-getrandom-o1)
9. [Task 9: O(1) Space Duplicate Detection](#task-9-o1-space-duplicate-detection)
10. [Task 10: Circular Buffer](#task-10-circular-buffer)
11. [Task 11: Frequency Counter with O(1) Max](#task-11-frequency-counter-with-o1-max)
12. [Task 12: Implement a Disjoint Set with O(1) Amortized](#task-12-implement-a-disjoint-set)
13. [Task 13: O(1) Matrix Element Access](#task-13-o1-matrix-element-access)
14. [Task 14: Bit Manipulation O(1) Operations](#task-14-bit-manipulation-o1-operations)
15. [Task 15: Time-Based Key-Value Store](#task-15-time-based-key-value-store)
16. [Benchmark Task: Prove O(1) Empirically](#benchmark-task-prove-o1-empirically)

---

## Task 1: Array Element Swap

**Difficulty:** Junior

**Description:** Write a function that swaps two elements in an array by index in O(1)
time. Verify that it works by swapping the first and last elements.

### Go

```go
package main

import "fmt"

// TODO: Implement swap in O(1)
func swap(arr []int, i, j int) {
    // Your code here
}

func main() {
    arr := []int{1, 2, 3, 4, 5}
    fmt.Println("Before:", arr)
    swap(arr, 0, 4)
    fmt.Println("After:", arr) // Expected: [5, 2, 3, 4, 1]
}
```

### Java

```java
public class Task1 {
    // TODO: Implement swap in O(1)
    static void swap(int[] arr, int i, int j) {
        // Your code here
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        swap(arr, 0, 4);
        System.out.println(java.util.Arrays.toString(arr)); // [5, 2, 3, 4, 1]
    }
}
```

### Python

```python
# TODO: Implement swap in O(1)
def swap(arr, i, j):
    pass  # Your code here

arr = [1, 2, 3, 4, 5]
swap(arr, 0, 4)
print(arr)  # Expected: [5, 2, 3, 4, 1]
```

---

## Task 2: Stack Min in O(1)

**Difficulty:** Junior

**Description:** Implement a stack that supports `push`, `pop`, `top`, and `getMin`,
all in O(1) time. Hint: use an auxiliary stack to track minimums.

### Go

```go
package main

import "fmt"

type MinStack struct {
    // TODO: Define your fields
}

func (s *MinStack) Push(val int) {
    // TODO
}

func (s *MinStack) Pop() int {
    // TODO
    return 0
}

func (s *MinStack) Top() int {
    // TODO
    return 0
}

func (s *MinStack) GetMin() int {
    // TODO
    return 0
}

func main() {
    s := &MinStack{}
    s.Push(5)
    s.Push(3)
    s.Push(7)
    s.Push(1)
    fmt.Println("Min:", s.GetMin()) // 1
    s.Pop()
    fmt.Println("Min:", s.GetMin()) // 3
    s.Pop()
    fmt.Println("Min:", s.GetMin()) // 3
    s.Pop()
    fmt.Println("Min:", s.GetMin()) // 5
}
```

### Java

```java
public class MinStack {
    // TODO: Define your fields

    public void push(int val) { /* TODO */ }
    public int pop() { /* TODO */ return 0; }
    public int top() { /* TODO */ return 0; }
    public int getMin() { /* TODO */ return 0; }

    public static void main(String[] args) {
        MinStack s = new MinStack();
        s.push(5); s.push(3); s.push(7); s.push(1);
        System.out.println("Min: " + s.getMin()); // 1
        s.pop();
        System.out.println("Min: " + s.getMin()); // 3
    }
}
```

### Python

```python
class MinStack:
    def __init__(self):
        pass  # TODO: Define your fields

    def push(self, val):
        pass  # TODO

    def pop(self):
        pass  # TODO

    def top(self):
        pass  # TODO

    def get_min(self):
        pass  # TODO

s = MinStack()
s.push(5); s.push(3); s.push(7); s.push(1)
print("Min:", s.get_min())  # 1
s.pop()
print("Min:", s.get_min())  # 3
```

---

## Task 3: Hash Set Implementation

**Difficulty:** Junior

**Description:** Implement a simple hash set with `add`, `remove`, and `contains`
operations, all O(1) average case. Use chaining for collision resolution.

### Go

```go
package main

import "fmt"

type HashSet struct {
    buckets [][]int
    size    int
}

func NewHashSet(capacity int) *HashSet {
    return &HashSet{
        buckets: make([][]int, capacity),
        size:    0,
    }
}

// TODO: Implement Add, Remove, Contains -- all O(1) average
func (hs *HashSet) Add(val int) {
    // Your code here
}

func (hs *HashSet) Remove(val int) {
    // Your code here
}

func (hs *HashSet) Contains(val int) bool {
    // Your code here
    return false
}

func main() {
    hs := NewHashSet(16)
    hs.Add(1); hs.Add(2); hs.Add(3)
    fmt.Println(hs.Contains(2)) // true
    hs.Remove(2)
    fmt.Println(hs.Contains(2)) // false
}
```

### Java

```java
import java.util.*;

public class MyHashSet {
    private List<List<Integer>> buckets;
    private int capacity;

    public MyHashSet(int capacity) {
        this.capacity = capacity;
        buckets = new ArrayList<>();
        for (int i = 0; i < capacity; i++) buckets.add(new LinkedList<>());
    }

    // TODO: Implement add, remove, contains
    public void add(int val) { /* TODO */ }
    public void remove(int val) { /* TODO */ }
    public boolean contains(int val) { /* TODO */ return false; }

    public static void main(String[] args) {
        MyHashSet hs = new MyHashSet(16);
        hs.add(1); hs.add(2); hs.add(3);
        System.out.println(hs.contains(2)); // true
        hs.remove(2);
        System.out.println(hs.contains(2)); // false
    }
}
```

### Python

```python
class HashSet:
    def __init__(self, capacity=16):
        self.capacity = capacity
        self.buckets = [[] for _ in range(capacity)]

    # TODO: Implement add, remove, contains
    def add(self, val):
        pass  # Your code here

    def remove(self, val):
        pass  # Your code here

    def contains(self, val):
        pass  # Your code here

hs = HashSet(16)
hs.add(1); hs.add(2); hs.add(3)
print(hs.contains(2))  # True
hs.remove(2)
print(hs.contains(2))  # False
```

---

## Task 4: Two-Sum with O(1) Lookup

**Difficulty:** Junior

**Description:** Given an array of integers and a target sum, find two numbers that add
up to the target. Use a hash map for O(1) lookups to achieve O(n) overall.

### Go

```go
package main

import "fmt"

// TODO: Return indices of two numbers that add up to target
// Use a hash map for O(1) lookup per element
func twoSum(nums []int, target int) (int, int) {
    // Your code here
    return -1, -1
}

func main() {
    nums := []int{2, 7, 11, 15}
    i, j := twoSum(nums, 9)
    fmt.Printf("Indices: %d, %d\n", i, j) // 0, 1
}
```

### Java

```java
import java.util.*;

public class TwoSum {
    // TODO: Return indices of two numbers that add up to target
    static int[] twoSum(int[] nums, int target) {
        // Your code here
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        int[] result = twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println(Arrays.toString(result)); // [0, 1]
    }
}
```

### Python

```python
# TODO: Return indices of two numbers that add up to target
def two_sum(nums, target):
    pass  # Your code here

print(two_sum([2, 7, 11, 15], 9))  # (0, 1)
```

---

## Task 5: Queue Using Two Stacks

**Difficulty:** Middle

**Description:** Implement a queue using two stacks. The `enqueue` operation should be
O(1) and the `dequeue` operation should be amortized O(1).

### Go

```go
package main

import "fmt"

type QueueTwoStacks struct {
    inbox  []int
    outbox []int
}

// TODO: Implement Enqueue (O(1)) and Dequeue (amortized O(1))
func (q *QueueTwoStacks) Enqueue(val int) {
    // Your code here
}

func (q *QueueTwoStacks) Dequeue() int {
    // Your code here
    return 0
}

func main() {
    q := &QueueTwoStacks{}
    q.Enqueue(1); q.Enqueue(2); q.Enqueue(3)
    fmt.Println(q.Dequeue()) // 1
    fmt.Println(q.Dequeue()) // 2
    q.Enqueue(4)
    fmt.Println(q.Dequeue()) // 3
    fmt.Println(q.Dequeue()) // 4
}
```

### Java

```java
import java.util.Stack;

public class QueueTwoStacks {
    private Stack<Integer> inbox = new Stack<>();
    private Stack<Integer> outbox = new Stack<>();

    // TODO: Implement enqueue (O(1)) and dequeue (amortized O(1))
    public void enqueue(int val) { /* TODO */ }
    public int dequeue() { /* TODO */ return 0; }

    public static void main(String[] args) {
        QueueTwoStacks q = new QueueTwoStacks();
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        System.out.println(q.dequeue()); // 1
        System.out.println(q.dequeue()); // 2
    }
}
```

### Python

```python
class QueueTwoStacks:
    def __init__(self):
        self.inbox = []
        self.outbox = []

    # TODO: Implement enqueue (O(1)) and dequeue (amortized O(1))
    def enqueue(self, val):
        pass  # Your code here

    def dequeue(self):
        pass  # Your code here

q = QueueTwoStacks()
q.enqueue(1); q.enqueue(2); q.enqueue(3)
print(q.dequeue())  # 1
print(q.dequeue())  # 2
```

---

## Task 6: First Unique Character

**Difficulty:** Middle

**Description:** Given a string, find the first non-repeating character and return its
index. Use a hash map for O(1) frequency lookups to achieve O(n) overall.

### Go

```go
package main

import "fmt"

// TODO: Return index of first unique character, or -1
func firstUniqChar(s string) int {
    // Your code here
    return -1
}

func main() {
    fmt.Println(firstUniqChar("leetcode"))     // 0 ('l')
    fmt.Println(firstUniqChar("loveleetcode")) // 2 ('v')
    fmt.Println(firstUniqChar("aabb"))         // -1
}
```

### Java

```java
import java.util.HashMap;

public class FirstUnique {
    // TODO: Return index of first unique character, or -1
    static int firstUniqChar(String s) {
        // Your code here
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(firstUniqChar("leetcode"));     // 0
        System.out.println(firstUniqChar("loveleetcode")); // 2
        System.out.println(firstUniqChar("aabb"));         // -1
    }
}
```

### Python

```python
# TODO: Return index of first unique character, or -1
def first_uniq_char(s):
    pass  # Your code here

print(first_uniq_char("leetcode"))     # 0
print(first_uniq_char("loveleetcode")) # 2
print(first_uniq_char("aabb"))         # -1
```

---

## Task 7: LRU Cache

**Difficulty:** Senior

**Description:** Implement an LRU cache with `get` and `put` operations, both in O(1)
time. Use a hash map + doubly linked list.

### Go

```go
package main

import "fmt"

// TODO: Implement LRUCache with O(1) get and put
type LRUCache struct {
    capacity int
    // Define additional fields
}

func NewLRUCache(capacity int) *LRUCache {
    // TODO
    return nil
}

func (c *LRUCache) Get(key int) int {
    // TODO: Return value or -1 if not found. Move to front.
    return -1
}

func (c *LRUCache) Put(key, value int) {
    // TODO: Insert or update. Evict LRU if over capacity.
}

func main() {
    cache := NewLRUCache(2)
    cache.Put(1, 1)
    cache.Put(2, 2)
    fmt.Println(cache.Get(1)) // 1
    cache.Put(3, 3)            // evicts key 2
    fmt.Println(cache.Get(2)) // -1
    cache.Put(4, 4)            // evicts key 1
    fmt.Println(cache.Get(1)) // -1
    fmt.Println(cache.Get(3)) // 3
    fmt.Println(cache.Get(4)) // 4
}
```

### Java

```java
public class LRUCacheTask {
    // TODO: Implement with O(1) get and put
    // Use LinkedHashMap or build from scratch with HashMap + DoublyLinkedList

    public static void main(String[] args) {
        // Test with capacity 2
    }
}
```

### Python

```python
# TODO: Implement LRU Cache with O(1) get and put
class LRUCache:
    def __init__(self, capacity):
        pass

    def get(self, key):
        pass  # Return value or -1

    def put(self, key, value):
        pass  # Insert or update, evict if over capacity

cache = LRUCache(2)
cache.put(1, 1)
cache.put(2, 2)
print(cache.get(1))  # 1
cache.put(3, 3)       # evicts key 2
print(cache.get(2))  # -1
```

---

## Task 8: Insert Delete GetRandom O(1)

**Difficulty:** Senior

**Description:** Design a data structure that supports `insert`, `remove`, and
`getRandom` in O(1) average time. See interview.md for full solution approach.

---

## Task 9: O(1) Space Duplicate Detection

**Difficulty:** Middle

**Description:** Given an array of n+1 integers where each integer is between 1 and n
(inclusive), find the duplicate number using O(1) extra space. (Floyd's tortoise and
hare algorithm.)

### Go

```go
package main

import "fmt"

// TODO: Find duplicate in O(n) time and O(1) space using cycle detection
func findDuplicate(nums []int) int {
    // Your code here
    return -1
}

func main() {
    fmt.Println(findDuplicate([]int{1, 3, 4, 2, 2})) // 2
    fmt.Println(findDuplicate([]int{3, 1, 3, 4, 2})) // 3
}
```

### Java

```java
public class FindDuplicate {
    static int findDuplicate(int[] nums) {
        // TODO: Floyd's cycle detection
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(findDuplicate(new int[]{1, 3, 4, 2, 2})); // 2
        System.out.println(findDuplicate(new int[]{3, 1, 3, 4, 2})); // 3
    }
}
```

### Python

```python
def find_duplicate(nums):
    """Find duplicate using Floyd's cycle detection -- O(1) space."""
    pass  # TODO

print(find_duplicate([1, 3, 4, 2, 2]))  # 2
print(find_duplicate([3, 1, 3, 4, 2]))  # 3
```

---

## Task 10: Circular Buffer

**Difficulty:** Middle

**Description:** Implement a fixed-size circular buffer (ring buffer) with O(1)
`enqueue`, `dequeue`, `front`, and `rear` operations.

### Go

```go
package main

import "fmt"

type CircularBuffer struct {
    data       []int
    head, tail int
    count, cap int
}

// TODO: Implement NewCircularBuffer, Enqueue, Dequeue, Front, Rear, IsFull, IsEmpty
// All operations should be O(1)

func main() {
    cb := NewCircularBuffer(3)
    cb.Enqueue(1); cb.Enqueue(2); cb.Enqueue(3)
    fmt.Println(cb.Dequeue()) // 1
    cb.Enqueue(4)
    fmt.Println(cb.Front()) // 2
    fmt.Println(cb.Rear())  // 4
}
```

### Java

```java
public class CircularBuffer {
    // TODO: Implement with O(1) operations

    public static void main(String[] args) {
        // Test
    }
}
```

### Python

```python
class CircularBuffer:
    # TODO: Implement with O(1) operations
    pass
```

---

## Task 11: Frequency Counter with O(1) Max

**Difficulty:** Senior

**Description:** Implement a data structure that supports:
- `increment(key)` -- increment the count for a key, O(1).
- `decrement(key)` -- decrement the count for a key, O(1).
- `getMaxKey()` -- return a key with the maximum count, O(1).
- `getMinKey()` -- return a key with the minimum count, O(1).

Hint: Use a doubly-linked list of frequency buckets + hash maps.

---

## Task 12: Implement a Disjoint Set

**Difficulty:** Senior

**Description:** Implement Union-Find with path compression and union by rank. Both
`find` and `union` should be nearly O(1) (amortized O(alpha(n)) which is effectively
constant for all practical inputs).

### Go

```go
package main

import "fmt"

type UnionFind struct {
    parent []int
    rank   []int
}

// TODO: Implement NewUnionFind, Find (with path compression), Union (by rank)

func main() {
    uf := NewUnionFind(10)
    uf.Union(0, 1)
    uf.Union(2, 3)
    uf.Union(1, 3)
    fmt.Println(uf.Find(0) == uf.Find(3)) // true
    fmt.Println(uf.Find(0) == uf.Find(5)) // false
}
```

### Java

```java
public class UnionFind {
    // TODO: Implement with path compression and union by rank

    public static void main(String[] args) {
        UnionFind uf = new UnionFind(10);
        uf.union(0, 1); uf.union(2, 3); uf.union(1, 3);
        System.out.println(uf.find(0) == uf.find(3)); // true
    }
}
```

### Python

```python
class UnionFind:
    # TODO: Implement with path compression and union by rank
    pass

uf = UnionFind(10)
uf.union(0, 1); uf.union(2, 3); uf.union(1, 3)
print(uf.find(0) == uf.find(3))  # True
```

---

## Task 13: O(1) Matrix Element Access

**Difficulty:** Junior

**Description:** Implement a 2D matrix stored as a 1D array. Access `matrix[row][col]`
in O(1) using the formula `index = row * cols + col`.

---

## Task 14: Bit Manipulation O(1) Operations

**Difficulty:** Middle

**Description:** Implement the following O(1) bit operations:
- `getBit(num, i)` -- check if bit i is set.
- `setBit(num, i)` -- set bit i to 1.
- `clearBit(num, i)` -- clear bit i to 0.
- `toggleBit(num, i)` -- toggle bit i.
- `countBits(num)` -- count set bits (use built-in popcount for O(1)).

---

## Task 15: Time-Based Key-Value Store

**Difficulty:** Senior

**Description:** Design a key-value store where each key can have multiple values at
different timestamps. `set(key, value, timestamp)` stores in O(1). `get(key, timestamp)`
returns the value with the largest timestamp <= given timestamp (uses binary search on
the timestamps for that key, which is O(log k) where k is the number of timestamps).

---

## Benchmark Task: Prove O(1) Empirically

**Difficulty:** Middle

**Description:** Write a benchmark that demonstrates O(1) array access and O(1) hash
map lookup by timing operations on arrays/maps of sizes 100, 10,000, 1,000,000, and
100,000,000. Show that the time per operation stays constant.

### Go

```go
package main

import (
    "fmt"
    "math/rand"
    "time"
)

func benchmarkArrayAccess(size int, iterations int) time.Duration {
    arr := make([]int, size)
    for i := range arr {
        arr[i] = i
    }

    start := time.Now()
    for i := 0; i < iterations; i++ {
        _ = arr[rand.Intn(size)]
    }
    return time.Since(start)
}

func benchmarkMapLookup(size int, iterations int) time.Duration {
    m := make(map[int]int, size)
    for i := 0; i < size; i++ {
        m[i] = i
    }

    start := time.Now()
    for i := 0; i < iterations; i++ {
        _ = m[rand.Intn(size)]
    }
    return time.Since(start)
}

func main() {
    iterations := 1_000_000
    sizes := []int{100, 10_000, 1_000_000, 100_000_000}

    fmt.Println("=== Array Access (should be ~constant) ===")
    for _, size := range sizes {
        elapsed := benchmarkArrayAccess(size, iterations)
        nsPerOp := float64(elapsed.Nanoseconds()) / float64(iterations)
        fmt.Printf("Size: %12d | Time/op: %8.2f ns\n", size, nsPerOp)
    }

    fmt.Println("\n=== Map Lookup (should be ~constant) ===")
    for _, size := range sizes[:3] { // Skip 100M for map (too much memory)
        elapsed := benchmarkMapLookup(size, iterations)
        nsPerOp := float64(elapsed.Nanoseconds()) / float64(iterations)
        fmt.Printf("Size: %12d | Time/op: %8.2f ns\n", size, nsPerOp)
    }
}
```

### Java

```java
import java.util.*;

public class BenchmarkO1 {
    static long benchmarkArrayAccess(int size, int iterations) {
        int[] arr = new int[size];
        Random rng = new Random();
        for (int i = 0; i < size; i++) arr[i] = i;

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int _ = arr[rng.nextInt(size)];
        }
        return System.nanoTime() - start;
    }

    static long benchmarkMapLookup(int size, int iterations) {
        HashMap<Integer, Integer> map = new HashMap<>(size);
        Random rng = new Random();
        for (int i = 0; i < size; i++) map.put(i, i);

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            map.get(rng.nextInt(size));
        }
        return System.nanoTime() - start;
    }

    public static void main(String[] args) {
        int iterations = 1_000_000;
        int[] sizes = {100, 10_000, 1_000_000};

        System.out.println("=== Array Access ===");
        for (int size : sizes) {
            long ns = benchmarkArrayAccess(size, iterations);
            System.out.printf("Size: %10d | Time/op: %.2f ns%n",
                size, (double) ns / iterations);
        }

        System.out.println("\n=== Map Lookup ===");
        for (int size : sizes) {
            long ns = benchmarkMapLookup(size, iterations);
            System.out.printf("Size: %10d | Time/op: %.2f ns%n",
                size, (double) ns / iterations);
        }
    }
}
```

### Python

```python
import time
import random

def benchmark_list_access(size, iterations):
    arr = list(range(size))
    indices = [random.randint(0, size - 1) for _ in range(iterations)]

    start = time.perf_counter()
    for idx in indices:
        _ = arr[idx]
    elapsed = time.perf_counter() - start
    return elapsed

def benchmark_dict_lookup(size, iterations):
    d = {i: i for i in range(size)}
    keys = [random.randint(0, size - 1) for _ in range(iterations)]

    start = time.perf_counter()
    for k in keys:
        _ = d[k]
    elapsed = time.perf_counter() - start
    return elapsed

iterations = 1_000_000
sizes = [100, 10_000, 1_000_000]

print("=== List Access ===")
for size in sizes:
    elapsed = benchmark_list_access(size, iterations)
    ns_per_op = elapsed * 1e9 / iterations
    print(f"Size: {size:>10,} | Time/op: {ns_per_op:.2f} ns")

print("\n=== Dict Lookup ===")
for size in sizes:
    elapsed = benchmark_dict_lookup(size, iterations)
    ns_per_op = elapsed * 1e9 / iterations
    print(f"Size: {size:>10,} | Time/op: {ns_per_op:.2f} ns")
```
