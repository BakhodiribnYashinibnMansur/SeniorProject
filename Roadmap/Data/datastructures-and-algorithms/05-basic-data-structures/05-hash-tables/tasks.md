# Hash Table — Tasks & Exercises

## Table of Contents

1. [Task 1: Basic Hash Table](#task-1-basic-hash-table)
2. [Task 2: Frequency Counter](#task-2-frequency-counter)
3. [Task 3: First Non-Repeating Character](#task-3-first-non-repeating-character)
4. [Task 4: Intersection of Two Arrays](#task-4-intersection-of-two-arrays)
5. [Task 5: Isomorphic Strings](#task-5-isomorphic-strings)
6. [Task 6: Subarray Sum Equals K](#task-6-subarray-sum-equals-k)
7. [Task 7: Longest Consecutive Sequence](#task-7-longest-consecutive-sequence)
8. [Task 8: Open Addressing with Double Hashing](#task-8-open-addressing-with-double-hashing)
9. [Task 9: Hash Set from Scratch](#task-9-hash-set-from-scratch)
10. [Task 10: Top K Frequent Elements](#task-10-top-k-frequent-elements)
11. [Task 11: Valid Sudoku](#task-11-valid-sudoku)
12. [Task 12: Copy Random List](#task-12-copy-random-list)
13. [Task 13: Minimum Window Substring](#task-13-minimum-window-substring)
14. [Task 14: Consistent Hashing Simulator](#task-14-consistent-hashing-simulator)
15. [Task 15: Bloom Filter](#task-15-bloom-filter)
16. [Benchmark: Chaining vs Open Addressing](#benchmark-chaining-vs-open-addressing)

---

## Task 1: Basic Hash Table

**Difficulty**: Easy

Implement a hash table from scratch that supports `put(key, value)`, `get(key)`, and `remove(key)`. Use separate chaining. The table should automatically resize when the load factor exceeds 0.75.

**Requirements**:
- String keys, integer values
- Resize by doubling when load factor > 0.75
- Handle key updates (same key overwrites value)

**Test cases**:
```
put("a", 1), put("b", 2), put("c", 3)
get("b") -> 2
remove("b")
get("b") -> not found
put("a", 10)  // update
get("a") -> 10
```

---

## Task 2: Frequency Counter

**Difficulty**: Easy

Given a string, count the frequency of each character using a hash map. Return the characters sorted by frequency (descending).

**Example**:
```
Input:  "aabbbcccc"
Output: [('c', 4), ('b', 3), ('a', 2)]
```

**Hint**: Use a hash map to count, then sort the entries.

---

## Task 3: First Non-Repeating Character

**Difficulty**: Easy

Given a string, find the first character that does not repeat. Return its index, or -1 if all characters repeat.

**Example**:
```
Input:  "aabbcdd"
Output: 4  (character 'c')

Input:  "aabb"
Output: -1
```

**Approach**: Two passes — first pass counts frequencies, second pass finds the first with count 1.

---

## Task 4: Intersection of Two Arrays

**Difficulty**: Easy

Given two integer arrays, return their intersection (each element appears as many times as it appears in both arrays).

**Example**:
```
Input:  nums1 = [1, 2, 2, 1], nums2 = [2, 2]
Output: [2, 2]

Input:  nums1 = [4, 9, 5], nums2 = [9, 4, 9, 8, 4]
Output: [4, 9]  (order does not matter)
```

**Approach**: Count frequencies in one array, iterate through the other and decrement counts.

---

## Task 5: Isomorphic Strings

**Difficulty**: Medium

Two strings `s` and `t` are isomorphic if characters in `s` can be replaced to get `t`, with a one-to-one mapping.

**Example**:
```
Input:  s = "egg", t = "add"
Output: true  (e->a, g->d)

Input:  s = "foo", t = "bar"
Output: false  (o maps to both a and r)

Input:  s = "paper", t = "title"
Output: true
```

**Approach**: Use two hash maps — one for `s->t` mapping and one for `t->s` mapping. Both must be consistent.

---

## Task 6: Subarray Sum Equals K

**Difficulty**: Medium

Given an integer array and an integer `k`, find the total number of continuous subarrays whose sum equals `k`.

**Example**:
```
Input:  nums = [1, 1, 1], k = 2
Output: 2  ([1,1] starting at index 0, [1,1] starting at index 1)

Input:  nums = [1, 2, 3], k = 3
Output: 2  ([1,2] and [3])
```

**Approach**: Use a hash map to store prefix sum frequencies. For each prefix sum, check if `prefixSum - k` exists in the map.

**Key insight**: If `prefixSum[j] - prefixSum[i] = k`, then the subarray from `i+1` to `j` sums to `k`.

---

## Task 7: Longest Consecutive Sequence

**Difficulty**: Medium

Given an unsorted array of integers, find the length of the longest consecutive elements sequence in O(n) time.

**Example**:
```
Input:  [100, 4, 200, 1, 3, 2]
Output: 4  (sequence: [1, 2, 3, 4])

Input:  [0, 3, 7, 2, 5, 8, 4, 6, 0, 1]
Output: 9
```

**Approach**: Put all numbers in a hash set. For each number `n`, if `n-1` is NOT in the set (start of a sequence), count consecutive numbers `n, n+1, n+2, ...` until one is missing.

---

## Task 8: Open Addressing with Double Hashing

**Difficulty**: Medium

Implement a hash table using open addressing with **double hashing**. Use:
- `h1(key) = key % capacity`
- `h2(key) = 1 + (key % (capacity - 1))`
- Probe sequence: `(h1 + i * h2) % capacity`

**Requirements**:
- Integer keys and values
- Tombstone-based deletion
- Resize when load factor > 0.5
- Test with at least 20 insertions and verify correctness

---

## Task 9: Hash Set from Scratch

**Difficulty**: Medium

Implement a hash set (not a hash map) that supports `add(key)`, `remove(key)`, and `contains(key)`. Do NOT use any built-in hash set or hash map.

**Requirements**:
- Use separate chaining
- Support `add`, `remove`, `contains`
- Implement `union(other)`, `intersection(other)`, and `difference(other)` set operations
- All individual operations in O(1) average

---

## Task 10: Top K Frequent Elements

**Difficulty**: Medium

Given an integer array and an integer k, return the k most frequent elements.

**Example**:
```
Input:  nums = [1, 1, 1, 2, 2, 3], k = 2
Output: [1, 2]

Input:  nums = [1], k = 1
Output: [1]
```

**Approach 1**: Hash map for frequencies + min-heap of size k. Time: O(n log k).
**Approach 2**: Hash map for frequencies + bucket sort (index = frequency). Time: O(n).

Implement **both** approaches and compare performance.

---

## Task 11: Valid Sudoku

**Difficulty**: Medium

Determine if a 9x9 Sudoku board is valid. Only filled cells need to be validated (no duplicates in each row, column, and 3x3 sub-box).

**Approach**: Use hash sets — one per row, one per column, one per box. For each filled cell, check membership in all three sets.

**Key**: Map cell `(r, c)` to box index: `box = (r / 3) * 3 + (c / 3)`.

---

## Task 12: Copy Random List

**Difficulty**: Medium

A linked list where each node has a `next` pointer and a `random` pointer (which can point to any node or null). Create a deep copy of this list.

**Approach**: Use a hash map: `original_node -> copied_node`. First pass creates all copied nodes. Second pass sets `next` and `random` pointers using the map.

**Why hash map?** The `random` pointer can point to any node — without a mapping from original to copy, we cannot reconstruct the pointers.

---

## Task 13: Minimum Window Substring

**Difficulty**: Hard

Given strings `s` and `t`, find the minimum window in `s` that contains all characters of `t` (including duplicates).

**Example**:
```
Input:  s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"
```

**Approach**: Sliding window with two hash maps — one for required character counts (from `t`), one for the current window's character counts. Expand the window rightward until all characters are covered, then shrink from the left to minimize.

---

## Task 14: Consistent Hashing Simulator

**Difficulty**: Hard

Build a consistent hashing simulator:
1. Implement a hash ring with virtual nodes.
2. Support `addServer(name)` and `removeServer(name)`.
3. Support `getServer(key)` to find which server handles a key.
4. Simulate adding/removing servers and track how many keys remap.

**Requirements**:
- At least 100 virtual nodes per server
- Hash 10,000 keys and measure distribution across servers
- Print statistics: keys per server, standard deviation
- Show percentage of keys remapped when a server is added/removed

---

## Task 15: Bloom Filter

**Difficulty**: Hard

Implement a Bloom filter from scratch.

**Requirements**:
- Configurable `m` (bit array size) and `k` (number of hash functions)
- Use the formula `h_i(x) = (h1(x) + i * h2(x)) % m` to generate k hashes from 2 base hashes
- Support `add(element)` and `might_contain(element)`
- Write tests demonstrating: no false negatives, measurable false positive rate
- Compare measured false positive rate to theoretical rate: `(1 - e^(-kn/m))^k`

**Bonus**: Implement a **counting Bloom filter** that supports deletion.

---

## Benchmark: Chaining vs Open Addressing

Implement both chaining and linear probing hash tables. Benchmark them on the following workloads:

### Workload 1: Sequential Inserts
Insert 1,000,000 keys with sequential integer keys. Measure total time.

### Workload 2: Random Inserts
Insert 1,000,000 keys with random string keys. Measure total time.

### Workload 3: Mixed Read/Write
Pre-fill with 500,000 entries. Then perform 500,000 operations: 80% reads, 20% writes. Measure throughput.

### Workload 4: High Load Factor
Insert until load factor reaches 0.5, 0.7, 0.9, 0.95, 0.99 (open addressing) or 1.0, 2.0, 3.0 (chaining). Measure average lookup time at each level.

### Workload 5: Delete-Heavy
Pre-fill with 500,000 entries. Delete 400,000. Insert 400,000 new entries. Measure total time. (This tests tombstone accumulation in open addressing.)

### What to Measure
- Wall-clock time per operation (nanoseconds)
- Memory usage (bytes per entry)
- Cache miss rate (if profiling tools available)
- Distribution of chain lengths / probe distances

### Go Benchmark Skeleton

```go
package main

import (
    "fmt"
    "math/rand"
    "time"
)

func benchmarkInsert(n int) {
    // --- Chaining ---
    chainTable := NewChainHashTable(16)
    start := time.Now()
    for i := 0; i < n; i++ {
        chainTable.Insert(fmt.Sprintf("key-%d", i), i)
    }
    chainDuration := time.Since(start)

    // --- Linear Probing ---
    probeTable := NewLinearProbingTable(16)
    start = time.Now()
    for i := 0; i < n; i++ {
        probeTable.Insert(fmt.Sprintf("key-%d", i), i)
    }
    probeDuration := time.Since(start)

    fmt.Printf("Chaining insert %d: %v (%.0f ns/op)\n",
        n, chainDuration, float64(chainDuration.Nanoseconds())/float64(n))
    fmt.Printf("Probing  insert %d: %v (%.0f ns/op)\n",
        n, probeDuration, float64(probeDuration.Nanoseconds())/float64(n))
}

func benchmarkLookup(n int) {
    chainTable := NewChainHashTable(16)
    probeTable := NewLinearProbingTable(16)

    for i := 0; i < n; i++ {
        key := fmt.Sprintf("key-%d", i)
        chainTable.Insert(key, i)
        probeTable.Insert(key, i)
    }

    lookups := make([]string, n)
    for i := 0; i < n; i++ {
        lookups[i] = fmt.Sprintf("key-%d", rand.Intn(n))
    }

    start := time.Now()
    for _, key := range lookups {
        chainTable.Search(key)
    }
    chainDuration := time.Since(start)

    start = time.Now()
    for _, key := range lookups {
        probeTable.Search(key)
    }
    probeDuration := time.Since(start)

    fmt.Printf("Chaining lookup %d: %v (%.0f ns/op)\n",
        n, chainDuration, float64(chainDuration.Nanoseconds())/float64(n))
    fmt.Printf("Probing  lookup %d: %v (%.0f ns/op)\n",
        n, probeDuration, float64(probeDuration.Nanoseconds())/float64(n))
}

func main() {
    for _, n := range []int{10_000, 100_000, 1_000_000} {
        fmt.Printf("\n=== n = %d ===\n", n)
        benchmarkInsert(n)
        benchmarkLookup(n)
    }
}
```

### Java Benchmark Skeleton

```java
import java.util.*;

public class HashTableBenchmark {

    public static void main(String[] args) {
        int[] sizes = {10_000, 100_000, 1_000_000};

        for (int n : sizes) {
            System.out.printf("%n=== n = %d ===%n", n);

            // Benchmark HashMap (chaining-based internally)
            HashMap<String, Integer> hashMap = new HashMap<>();
            long start = System.nanoTime();
            for (int i = 0; i < n; i++) {
                hashMap.put("key-" + i, i);
            }
            long duration = System.nanoTime() - start;
            System.out.printf("HashMap insert: %.0f ns/op%n", (double) duration / n);

            // Benchmark lookup
            Random rand = new Random(42);
            start = System.nanoTime();
            for (int i = 0; i < n; i++) {
                hashMap.get("key-" + rand.nextInt(n));
            }
            duration = System.nanoTime() - start;
            System.out.printf("HashMap lookup: %.0f ns/op%n", (double) duration / n);
        }
    }
}
```

### Python Benchmark Skeleton

```python
"""Benchmark hash table implementations."""

import time
import random


def benchmark_insert(n: int):
    # Built-in dict (open addressing)
    d = {}
    start = time.perf_counter_ns()
    for i in range(n):
        d[f"key-{i}"] = i
    builtin_ns = time.perf_counter_ns() - start

    # Custom chaining hash table
    ht = HashTable(capacity=16)  # From junior.md implementation
    start = time.perf_counter_ns()
    for i in range(n):
        ht.insert(f"key-{i}", i)
    custom_ns = time.perf_counter_ns() - start

    print(f"  Built-in dict insert: {builtin_ns / n:.0f} ns/op")
    print(f"  Custom chaining insert: {custom_ns / n:.0f} ns/op")


def benchmark_lookup(n: int):
    d = {f"key-{i}": i for i in range(n)}
    keys = [f"key-{random.randint(0, n - 1)}" for _ in range(n)]

    start = time.perf_counter_ns()
    for k in keys:
        _ = d.get(k)
    duration = time.perf_counter_ns() - start

    print(f"  Built-in dict lookup: {duration / n:.0f} ns/op")


if __name__ == "__main__":
    for n in [10_000, 100_000, 1_000_000]:
        print(f"\n=== n = {n} ===")
        benchmark_insert(n)
        benchmark_lookup(n)
```

---

## Expected Outcomes

After completing all tasks you should be able to:

1. Implement hash tables from scratch with both chaining and open addressing.
2. Solve hash-table-based interview problems fluently.
3. Understand the performance characteristics of different collision strategies.
4. Build practical systems (Bloom filters, consistent hashing) on top of hashing primitives.
5. Benchmark and compare implementations with data-driven analysis.
