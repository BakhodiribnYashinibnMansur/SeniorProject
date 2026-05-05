# Constant Time O(1) -- Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [Amortized O(1) -- Dynamic Arrays](#amortized-o1--dynamic-arrays)
   - [How Dynamic Arrays Grow](#how-dynamic-arrays-grow)
   - [Amortized Analysis: Aggregate Method](#amortized-analysis-aggregate-method)
   - [Amortized Analysis: Banker's Method](#amortized-analysis-bankers-method)
   - [Implementation Examples](#implementation-examples)
3. [Expected O(1) -- Hash Tables](#expected-o1--hash-tables)
   - [Average vs Worst Case](#average-vs-worst-case)
   - [Load Factor and Rehashing](#load-factor-and-rehashing)
   - [Collision Resolution Strategies](#collision-resolution-strategies)
4. [O(1) with Large Constants](#o1-with-large-constants)
5. [O(1) vs O(log n): When Does It Matter?](#o1-vs-olog-n-when-does-it-matter)
6. [When O(1) Isn't Always Best](#when-o1-isnt-always-best)
   - [Cache Miss Penalties](#cache-miss-penalties)
   - [Memory Overhead](#memory-overhead)
   - [Practical Crossover Points](#practical-crossover-points)
7. [Hash Function Design for O(1)](#hash-function-design-for-o1)
8. [Code Examples](#code-examples)
9. [Key Takeaways](#key-takeaways)

---

## Introduction

At the junior level, you learned that O(1) means constant time. At this level, we
explore the nuances. Not all "O(1)" claims are the same. There is a significant
difference between:

- **Worst-case O(1)**: Every single operation is bounded by a constant.
- **Amortized O(1)**: Most operations are fast, occasional ones are expensive, but
  averaged over a sequence of operations, each one is O(1).
- **Expected O(1)**: On average (over random inputs or random internal choices), the
  operation is O(1), but individual operations can be slow.

Understanding these distinctions is critical for writing performant code and making
correct claims during design reviews and interviews.

---

## Amortized O(1) -- Dynamic Arrays

### How Dynamic Arrays Grow

Dynamic arrays (Go slices, Java ArrayList, Python list) automatically resize when they
run out of capacity. The typical strategy is to **double the capacity** when full.

Here is what happens during a sequence of `append` operations:

```
Capacity: 1  -> append -> [x]         (no resize)
Capacity: 1  -> append -> [x, _]      (resize to 2, copy 1 element)
Capacity: 2  -> append -> [x, x, _]   (no resize)
Capacity: 2  -> append -> [x,x,x, _]  (resize to 4, copy 2 elements)
Capacity: 4  -> append -> [x,x,x,x,_] (no resize)
...
```

Most appends are O(1) (just place the element). But when a resize happens, we must
copy all existing elements -- that is O(n). So how can we call append "O(1)"?

### Amortized Analysis: Aggregate Method

Consider `n` append operations starting from an empty array with doubling strategy:

- Resizes happen at sizes 1, 2, 4, 8, 16, ..., up to n.
- The cost of resizing at size `k` is `k` (copying `k` elements).
- Total copy cost = 1 + 2 + 4 + 8 + ... + n = 2n - 1.
- Total cost of n operations = n (placing elements) + (2n - 1) (copying) = 3n - 1.
- **Amortized cost per operation = (3n - 1) / n ~ 3 = O(1).**

### Amortized Analysis: Banker's Method

Think of each append "paying" 3 coins:
- 1 coin for placing the element.
- 2 coins saved in a "bank" attached to the element.

When a resize happens, each element that needs to be copied has 2 saved coins --
exactly enough to pay for its copy. No operation ever goes into "debt."

This proves amortized O(1) without averaging -- each operation independently covers
its own costs.

### Implementation Examples

#### Go

```go
package main

import "fmt"

// DynamicArray demonstrates amortized O(1) append
type DynamicArray struct {
    data     []int
    size     int
    capacity int
    resizes  int
}

func NewDynamicArray() *DynamicArray {
    return &DynamicArray{
        data:     make([]int, 1),
        size:     0,
        capacity: 1,
    }
}

func (da *DynamicArray) Append(value int) {
    if da.size == da.capacity {
        // Resize: double the capacity -- this step is O(n)
        da.capacity *= 2
        newData := make([]int, da.capacity)
        copy(newData, da.data)
        da.data = newData
        da.resizes++
    }
    // Place element -- this step is O(1)
    da.data[da.size] = value
    da.size++
}

func main() {
    da := NewDynamicArray()

    // Append 1 million elements
    for i := 0; i < 1_000_000; i++ {
        da.Append(i)
    }

    fmt.Printf("Size: %d, Capacity: %d, Resizes: %d\n",
        da.size, da.capacity, da.resizes)
    // Resizes will be ~20 (log2 of 1,000,000), proving most appends are cheap
}
```

#### Java

```java
public class DynamicArray {
    private int[] data;
    private int size;
    private int capacity;
    private int resizes;

    public DynamicArray() {
        capacity = 1;
        data = new int[capacity];
        size = 0;
        resizes = 0;
    }

    public void append(int value) {
        if (size == capacity) {
            // Resize: double the capacity -- this step is O(n)
            capacity *= 2;
            int[] newData = new int[capacity];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
            resizes++;
        }
        // Place element -- this step is O(1)
        data[size] = value;
        size++;
    }

    public static void main(String[] args) {
        DynamicArray da = new DynamicArray();

        for (int i = 0; i < 1_000_000; i++) {
            da.append(i);
        }

        System.out.printf("Size: %d, Capacity: %d, Resizes: %d%n",
            da.size, da.capacity, da.resizes);
    }
}
```

#### Python

```python
class DynamicArray:
    def __init__(self):
        self.data = [None]
        self.size = 0
        self.capacity = 1
        self.resizes = 0

    def append(self, value):
        if self.size == self.capacity:
            # Resize: double the capacity -- this step is O(n)
            self.capacity *= 2
            new_data = [None] * self.capacity
            for i in range(self.size):
                new_data[i] = self.data[i]
            self.data = new_data
            self.resizes += 1

        # Place element -- this step is O(1)
        self.data[self.size] = value
        self.size += 1


da = DynamicArray()
for i in range(1_000_000):
    da.append(i)

print(f"Size: {da.size}, Capacity: {da.capacity}, Resizes: {da.resizes}")
```

---

## Expected O(1) -- Hash Tables

### Average vs Worst Case

Hash table operations are **expected O(1)** -- meaning on average, across all possible
inputs, they complete in constant time. But individual operations can be O(n) in the
worst case (when all keys hash to the same bucket).

| Scenario | Insert | Lookup | Delete |
|----------|--------|--------|--------|
| Best case | O(1) | O(1) | O(1) |
| Average case | O(1) | O(1) | O(1) |
| Worst case | O(n) | O(n) | O(n) |

### Load Factor and Rehashing

The **load factor** `alpha = n / m` (elements / buckets) determines performance:

- Low alpha (< 0.5): Fewer collisions, faster operations, more memory waste.
- High alpha (> 0.75): More collisions, slower operations, less memory waste.
- When alpha exceeds a threshold, the table **rehashes**: creates a larger table and
  re-inserts all elements. This is O(n) but amortized O(1) (similar to dynamic arrays).

#### Go

```go
package main

import "fmt"

// Demonstrating load factor concepts
func main() {
    m := make(map[string]int)

    // Go maps automatically handle resizing internally.
    // The runtime keeps the load factor around 6.5 elements per bucket.
    for i := 0; i < 100; i++ {
        key := fmt.Sprintf("key_%d", i)
        m[key] = i // O(1) amortized
    }

    // Lookup remains O(1) expected even with 100 elements
    val, ok := m["key_50"]
    fmt.Println(val, ok) // 50 true
}
```

#### Java

```java
import java.util.HashMap;

public class LoadFactorDemo {
    public static void main(String[] args) {
        // Default load factor is 0.75 in Java HashMap
        // When exceeded, the table doubles in size and rehashes
        HashMap<String, Integer> map = new HashMap<>(16, 0.75f);

        for (int i = 0; i < 100; i++) {
            map.put("key_" + i, i); // O(1) amortized
        }

        // Lookup remains O(1) expected
        System.out.println(map.get("key_50")); // 50

        // You can pre-size to avoid rehashing if you know the count
        HashMap<String, Integer> preSized = new HashMap<>(200);
        // This avoids rehashing for up to 150 elements (200 * 0.75)
    }
}
```

#### Python

```python
# Python dicts use open addressing with a load factor around 2/3
# Rehashing happens automatically
d = {}

for i in range(100):
    d[f"key_{i}"] = i  # O(1) amortized

# Lookup remains O(1) expected
print(d["key_50"])  # 50

# You can see the internal size grows in powers of 2
import sys
d_small = {i: i for i in range(10)}
d_large = {i: i for i in range(10000)}
print(f"Small dict memory: {sys.getsizeof(d_small)} bytes")
print(f"Large dict memory: {sys.getsizeof(d_large)} bytes")
```

### Collision Resolution Strategies

Different strategies affect the constant factor in O(1):

**Chaining (separate chaining):**
- Each bucket holds a linked list of entries.
- Lookup: hash to bucket, then linear search through the chain.
- Average chain length = alpha.

**Open addressing (linear probing, quadratic probing, double hashing):**
- All entries stored in the table itself.
- On collision, probe for the next available slot.
- Better cache performance than chaining (contiguous memory).

**Robin Hood hashing:**
- Variant of open addressing.
- Steals from "rich" (short probe distance) to give to "poor" (long probe distance).
- Reduces variance in probe lengths.

---

## O(1) with Large Constants

An algorithm is O(1) if it runs in constant time, even if that constant is large.
Consider two lookup structures:

- **Structure A**: O(1) with constant = 1000 nanoseconds.
- **Structure B**: O(log n) with constant = 10 nanoseconds.

For n < 2^100 (which is every practical scenario), Structure B is faster. This is why
**asymptotic complexity is not the whole story**.

#### Go

```go
package main

import (
    "fmt"
    "time"
)

// simulateExpensiveO1 does many fixed operations (still O(1))
func simulateExpensiveO1(key int) int {
    result := key
    // 1000 fixed operations -- still O(1) but with large constant
    for i := 0; i < 1000; i++ {
        result = (result*31 + 17) % 1000003
    }
    return result
}

// binarySearch is O(log n) but with small constant
func binarySearch(arr []int, target int) int {
    lo, hi := 0, len(arr)-1
    for lo <= hi {
        mid := (lo + hi) / 2
        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return -1
}

func main() {
    // For small n, O(log n) with small constant beats O(1) with large constant
    arr := make([]int, 100)
    for i := range arr {
        arr[i] = i
    }

    start := time.Now()
    for i := 0; i < 10000; i++ {
        simulateExpensiveO1(i)
    }
    fmt.Println("O(1) large constant:", time.Since(start))

    start = time.Now()
    for i := 0; i < 10000; i++ {
        binarySearch(arr, i%100)
    }
    fmt.Println("O(log n) small constant:", time.Since(start))
}
```

#### Java

```java
public class LargeConstantDemo {
    static int simulateExpensiveO1(int key) {
        int result = key;
        for (int i = 0; i < 1000; i++) {
            result = (result * 31 + 17) % 1000003;
        }
        return result;
    }

    static int binarySearch(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (arr[mid] == target) return mid;
            else if (arr[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] arr = new int[100];
        for (int i = 0; i < 100; i++) arr[i] = i;

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            simulateExpensiveO1(i);
        }
        System.out.println("O(1) large constant: " +
            (System.nanoTime() - start) / 1_000_000 + "ms");

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            binarySearch(arr, i % 100);
        }
        System.out.println("O(log n) small constant: " +
            (System.nanoTime() - start) / 1_000_000 + "ms");
    }
}
```

#### Python

```python
import time

def simulate_expensive_o1(key):
    """O(1) but with a large constant (1000 fixed iterations)."""
    result = key
    for _ in range(1000):
        result = (result * 31 + 17) % 1000003
    return result

def binary_search(arr, target):
    """O(log n) with a small constant."""
    lo, hi = 0, len(arr) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1

arr = list(range(100))

start = time.perf_counter()
for i in range(10000):
    simulate_expensive_o1(i)
print(f"O(1) large constant: {time.perf_counter() - start:.4f}s")

start = time.perf_counter()
for i in range(10000):
    binary_search(arr, i % 100)
print(f"O(log n) small constant: {time.perf_counter() - start:.4f}s")
```

---

## O(1) vs O(log n): When Does It Matter?

The difference between O(1) and O(log n) is often **negligible in practice** for
reasonable input sizes. Here is why:

| n | log2(n) |
|---|---------|
| 1,000 | ~10 |
| 1,000,000 | ~20 |
| 1,000,000,000 | ~30 |
| 2^64 | 64 |

Even for astronomically large datasets, log n is at most 64 (for data that fits in
memory addressable by 64-bit pointers). A constant factor of 2x in an O(1) operation
makes it slower than O(log n) for all practical purposes.

**When O(1) wins over O(log n):**
- Operations called billions of times in tight loops (the constant savings compound).
- Real-time systems where worst-case guarantees matter.
- When the O(1) operation has a small constant (hash table with a good hash function).

**When O(log n) wins over O(1):**
- The O(1) structure uses significantly more memory (hash table vs. balanced BST).
- Ordered operations are needed (hash tables don't support range queries).
- The hash function is expensive to compute.
- Cache behavior of the O(log n) structure is better.

---

## When O(1) Isn't Always Best

### Cache Miss Penalties

Modern CPUs have caches (L1, L2, L3) that make sequential memory access fast. Hash
tables, despite being O(1), often cause **cache misses** because:

1. The hash function scatters data across memory.
2. Pointer chasing (in chaining) jumps to random memory locations.
3. Each lookup may fetch a new cache line.

A sorted array with binary search (O(log n)) may outperform a hash table for moderate
sizes because the array is stored contiguously and benefits from CPU prefetching.

### Memory Overhead

Hash tables trade memory for speed:
- Typically use 2-3x the memory of the stored data.
- Must maintain a low load factor.
- Each entry may have overhead (pointers, hash values).

For memory-constrained environments, an O(log n) structure like a sorted array or B-tree
may be preferable.

### Practical Crossover Points

Rule of thumb:
- **n < 50**: Linear search O(n) on a sorted array often beats hash table O(1).
- **n < 1000**: Binary search O(log n) is competitive with hash table O(1).
- **n > 10,000**: Hash table O(1) typically wins decisively.

These crossover points depend heavily on the specific hardware, data types, and access
patterns.

---

## Hash Function Design for O(1)

A good hash function is essential for achieving O(1) in practice. Properties:

1. **Deterministic**: Same input always produces same output.
2. **Uniform distribution**: Outputs are spread evenly across the range.
3. **Fast to compute**: The hash function itself should be O(1).
4. **Low collision rate**: Different inputs rarely produce the same hash.

### Common Hash Functions

#### Go

```go
package main

import (
    "fmt"
    "hash/fnv"
)

// Simple multiplication hash -- fast but not great distribution
func multiplyHash(key int, tableSize int) int {
    const A = 0.6180339887 // (sqrt(5) - 1) / 2
    return int(float64(tableSize) * (float64(key) * A - float64(int(float64(key)*A))))
}

// FNV-1a hash for strings -- good distribution, fast
func fnvHash(key string) uint32 {
    h := fnv.New32a()
    h.Write([]byte(key))
    return h.Sum32()
}

func main() {
    // Multiplication hash distributes keys across 16 buckets
    for i := 0; i < 20; i++ {
        fmt.Printf("Key %2d -> Bucket %d\n", i, multiplyHash(i, 16))
    }

    // FNV hash for strings
    words := []string{"hello", "world", "foo", "bar", "baz"}
    for _, w := range words {
        fmt.Printf("'%s' -> %d\n", w, fnvHash(w))
    }
}
```

#### Java

```java
public class HashFunctionDemo {
    // Simple multiplication hash
    static int multiplyHash(int key, int tableSize) {
        double A = 0.6180339887; // (sqrt(5) - 1) / 2
        return (int) (tableSize * ((key * A) % 1.0));
    }

    // Java's built-in String.hashCode() uses a polynomial rolling hash:
    // s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            System.out.printf("Key %2d -> Bucket %d%n", i, multiplyHash(i, 16));
        }

        String[] words = {"hello", "world", "foo", "bar", "baz"};
        for (String w : words) {
            System.out.printf("'%s' -> %d%n", w, w.hashCode());
        }
    }
}
```

#### Python

```python
def multiply_hash(key, table_size):
    """Simple multiplication hash."""
    A = 0.6180339887  # (sqrt(5) - 1) / 2
    return int(table_size * ((key * A) % 1.0))

# Distribution across 16 buckets
for i in range(20):
    print(f"Key {i:2d} -> Bucket {multiply_hash(i, 16)}")

# Python's built-in hash() function
words = ["hello", "world", "foo", "bar", "baz"]
for w in words:
    print(f"'{w}' -> {hash(w)}")

# Note: Python randomizes hash seeds between runs (for security).
# Use hashlib for deterministic hashing:
import hashlib
for w in words:
    h = hashlib.md5(w.encode()).hexdigest()
    print(f"'{w}' MD5 -> {h}")
```

---

## Key Takeaways

1. **Amortized O(1)** is not the same as worst-case O(1). Dynamic array appends are
   amortized O(1) because rare expensive resizes are "paid for" by many cheap appends.

2. **Expected O(1)** (hash tables) assumes a good hash function and that inputs are not
   adversarially chosen. Worst case is O(n).

3. **Large constants matter.** An O(1) operation with a constant of 1000 is slower than
   O(log n) with a constant of 1 for any practical input size.

4. **Cache behavior can override asymptotic analysis.** A cache-friendly O(log n)
   structure may outperform a cache-hostile O(1) structure.

5. **Hash function quality directly impacts O(1) performance.** Poor hash functions
   cause collisions, degrading O(1) to O(n).

6. **Know the crossover points.** For small n, simpler data structures often beat
   theoretically superior ones.

7. **Pre-sizing hash tables** (when you know the expected number of elements) avoids
   rehashing and improves both time and memory.

---

## Further Reading

- [junior.md](junior.md) -- Basics of O(1) and common examples.
- [senior.md](senior.md) -- O(1) in distributed systems and lock-free programming.
- [professional.md](professional.md) -- Formal proofs and perfect hashing.
