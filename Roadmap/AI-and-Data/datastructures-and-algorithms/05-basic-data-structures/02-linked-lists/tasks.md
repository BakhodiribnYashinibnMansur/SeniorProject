# Linked Lists -- Tasks

## Overview

15 progressive tasks plus a benchmark exercise. Each task builds on linked list concepts from basic operations to advanced algorithms. Implement in your preferred language (Go, Java, or Python).

---

## Task 1: Build a Singly Linked List from Scratch

**Difficulty:** Easy

**Description:** Implement a singly linked list class with the following methods:
- `insertAtHead(data)` -- insert at the beginning.
- `insertAtTail(data)` -- insert at the end.
- `deleteByValue(data)` -- delete the first node with the given value.
- `search(data)` -- return true if the value exists.
- `printList()` -- print all elements.

**Requirements:**
- Maintain both `head` and `tail` pointers.
- Track the `size` of the list.
- Handle edge cases: empty list, single element, deleting head/tail.

**Expected output:**
```
Insert 1,2,3 at tail: 1 -> 2 -> 3 -> nil
Insert 0 at head: 0 -> 1 -> 2 -> 3 -> nil
Delete 2: 0 -> 1 -> 3 -> nil
Search 3: true
Search 99: false
Size: 3
```

---

## Task 2: Implement `insertAtPosition(index, data)`

**Difficulty:** Easy

**Description:** Add a method to insert a node at a specific 0-based index. If the index is 0, insert at head. If the index equals the size, insert at tail. If the index is out of bounds, print an error message.

**Test cases:**
```
List: 10 -> 20 -> 30
insertAtPosition(1, 15) => 10 -> 15 -> 20 -> 30
insertAtPosition(0, 5)  => 5 -> 10 -> 15 -> 20 -> 30
insertAtPosition(5, 35) => 5 -> 10 -> 15 -> 20 -> 30 -> 35
insertAtPosition(99, 1) => Error: index out of bounds
```

---

## Task 3: Reverse a Linked List (Iterative)

**Difficulty:** Easy

**Description:** Write a function that reverses a singly linked list in-place using the iterative three-pointer approach (prev, current, next).

**Requirements:**
- Time: O(n), Space: O(1).
- Update the head pointer after reversal.
- Do NOT create new nodes.

**Test:**
```
Input:  1 -> 2 -> 3 -> 4 -> 5
Output: 5 -> 4 -> 3 -> 2 -> 1
```

---

## Task 4: Reverse a Linked List (Recursive)

**Difficulty:** Medium

**Description:** Reverse a singly linked list using recursion. The recursive call should handle the subproblem of reversing the rest of the list, and each returning call should fix the pointers.

**Requirements:**
- No loops allowed.
- Time: O(n), Space: O(n) due to call stack.

---

## Task 5: Detect a Cycle

**Difficulty:** Medium

**Description:** Implement Floyd's cycle detection algorithm. Given a linked list that may contain a cycle, return:
- `true` if there is a cycle, `false` otherwise.
- Bonus: return the node where the cycle begins.

**Test:** Create a list `1 -> 2 -> 3 -> 4 -> 5` and make node 5 point back to node 3. Your function should detect the cycle and return node 3 as the entry point.

---

## Task 6: Find the Middle Node

**Difficulty:** Easy

**Description:** Use the fast/slow pointer technique to find the middle node of a linked list in a single pass.

**Requirements:**
- For even-length lists, return the second middle node.
- Time: O(n), Space: O(1).

**Test:**
```
1 -> 2 -> 3 -> 4 -> 5       => middle is 3
1 -> 2 -> 3 -> 4 -> 5 -> 6  => middle is 4
```

---

## Task 7: Merge Two Sorted Lists

**Difficulty:** Medium

**Description:** Given two sorted linked lists, merge them into one sorted linked list. Reuse existing nodes (do not create new ones).

**Test:**
```
L1: 1 -> 3 -> 5
L2: 2 -> 4 -> 6
Result: 1 -> 2 -> 3 -> 4 -> 5 -> 6
```

---

## Task 8: Remove Duplicates from a Sorted List

**Difficulty:** Easy

**Description:** Given a sorted linked list, delete all duplicate values so that each element appears only once.

**Test:**
```
Input:  1 -> 1 -> 2 -> 3 -> 3 -> 3 -> 4
Output: 1 -> 2 -> 3 -> 4
```

---

## Task 9: Remove Duplicates from an Unsorted List

**Difficulty:** Medium

**Description:** Given an unsorted linked list, remove all duplicate values. You may use a hash set for O(n) time, or solve it in O(n^2) time with O(1) space (no extra data structures).

**Test:**
```
Input:  3 -> 1 -> 3 -> 2 -> 1 -> 5
Output: 3 -> 1 -> 2 -> 5
```

---

## Task 10: Implement a Doubly Linked List

**Difficulty:** Medium

**Description:** Implement a full doubly linked list with:
- `insertAtHead(data)`
- `insertAtTail(data)`
- `deleteNode(node)` -- O(1) given a direct node reference.
- `printForward()` and `printBackward()`.
- Sentinel head and tail nodes.

**Requirements:**
- Use sentinel nodes to eliminate nil checks.
- All insert/delete operations should be O(1).

---

## Task 11: Implement an LRU Cache

**Difficulty:** Hard

**Description:** Implement a Least Recently Used (LRU) cache using a hash map and a doubly linked list.

**Methods:**
- `get(key)` -- return the value if it exists, move to front. Return -1 if not found.
- `put(key, value)` -- insert or update. If at capacity, evict the least recently used item.

**Requirements:**
- Both operations must be O(1).
- Capacity is set at construction time.

**Test:**
```
cache = LRUCache(2)
cache.put(1, 1)
cache.put(2, 2)
cache.get(1)      => 1
cache.put(3, 3)   => evicts key 2
cache.get(2)      => -1
```

---

## Task 12: Palindrome Linked List

**Difficulty:** Medium

**Description:** Determine whether a singly linked list is a palindrome in O(n) time and O(1) space.

**Hint:** Find the middle, reverse the second half, compare both halves, then restore the list.

**Test:**
```
1 -> 2 -> 3 -> 2 -> 1  => true
1 -> 2 -> 3 -> 4 -> 5  => false
```

---

## Task 13: Add Two Numbers

**Difficulty:** Medium

**Description:** Two numbers are represented as linked lists where each node contains a single digit, stored in reverse order. Add the two numbers and return the sum as a linked list.

**Test:**
```
L1: 2 -> 4 -> 3   (represents 342)
L2: 5 -> 6 -> 4   (represents 465)
Result: 7 -> 0 -> 8 (represents 807)
```

---

## Task 14: Flatten a Multilevel Doubly Linked List

**Difficulty:** Hard

**Description:** A doubly linked list where some nodes have a `child` pointer to another doubly linked list. Flatten all levels into a single-level doubly linked list using depth-first order.

```
1 <-> 2 <-> 3 <-> 4
            |
            5 <-> 6
            |
            7

Result: 1 <-> 2 <-> 3 <-> 5 <-> 7 <-> 6 <-> 4
```

---

## Task 15: Implement a Skip List

**Difficulty:** Hard

**Description:** Implement a skip list supporting:
- `insert(key, value)`
- `search(key)` -- return value or nil.
- `delete(key)`
- `printLevels()` -- print each level of the skip list.

**Requirements:**
- Use random level generation with probability 0.5.
- Maximum level: 16.
- All operations should be O(log n) on average.

---

## Benchmark Task: Linked List vs Array Performance

**Difficulty:** Medium

**Description:** Write a benchmark that compares a singly linked list to a dynamic array (Go slice / Java ArrayList / Python list) on the following operations:

### Operations to benchmark:

1. **Insert at head** -- insert 100,000 elements at the beginning.
2. **Insert at tail** -- insert 100,000 elements at the end.
3. **Sequential access** -- iterate through all 100,000 elements and sum them.
4. **Search** -- search for 1,000 random values.
5. **Delete at head** -- delete all 100,000 elements from the head.

### Expected output format:

```
Operation           | Linked List  | Array/Slice  | Winner
--------------------|-------------|-------------|--------
Insert at head      |    5.2 ms   |   412.0 ms  | LL
Insert at tail      |    4.8 ms   |     3.1 ms  | Array
Sequential access   |   12.3 ms   |     2.1 ms  | Array
Search (1000x)      |  820.0 ms   |   780.0 ms  | Array
Delete at head      |    3.1 ms   |   395.0 ms  | LL
```

### Go starter code:

```go
package main

import (
    "fmt"
    "math/rand"
    "time"
)

func benchmarkInsertHead(n int) {
    // Linked list
    start := time.Now()
    ll := NewLinkedList()
    for i := 0; i < n; i++ {
        ll.InsertAtHead(i)
    }
    llTime := time.Since(start)

    // Slice
    start = time.Now()
    arr := make([]int, 0)
    for i := 0; i < n; i++ {
        arr = append([]int{i}, arr...)
    }
    arrTime := time.Since(start)

    winner := "LL"
    if arrTime < llTime { winner = "Array" }
    fmt.Printf("Insert at head      | %10s | %10s | %s\n",
        llTime, arrTime, winner)
}

func main() {
    n := 100_000
    fmt.Println("Operation           | Linked List  | Array/Slice  | Winner")
    fmt.Println("--------------------|-------------|-------------|--------")
    benchmarkInsertHead(n)
    // ... implement remaining benchmarks
}
```

### What to observe:

1. Linked lists dominate for head insertion and deletion.
2. Arrays dominate for sequential access due to cache locality.
3. Tail insertion is comparable (both O(1) amortized).
4. Search is similar (both O(n)) but arrays have better cache performance.

### Report:

After running the benchmark, write a brief analysis explaining:
- Which data structure won each operation and why.
- How cache locality affects traversal performance.
- When you would choose a linked list in production code.
