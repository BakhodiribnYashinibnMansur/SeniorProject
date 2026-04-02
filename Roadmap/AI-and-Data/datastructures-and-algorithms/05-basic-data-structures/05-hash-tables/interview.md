# Hash Table — Interview Questions & Coding Challenges

## Table of Contents

1. [Conceptual Questions](#conceptual-questions)
2. [Coding Challenge 1: Two Sum](#coding-challenge-1-two-sum)
3. [Coding Challenge 2: Group Anagrams](#coding-challenge-2-group-anagrams)
4. [Coding Challenge 3: LRU Cache](#coding-challenge-3-lru-cache)
5. [Quick-Fire Questions](#quick-fire-questions)

---

## Conceptual Questions

**Q1**: What is the difference between a hash map and a hash set?
> A hash map stores key-value pairs; a hash set stores only keys (for membership testing). Internally, many hash set implementations are backed by a hash map with dummy values.

**Q2**: Why is the average time complexity of hash table operations O(1)?
> A good hash function distributes keys uniformly. With load factor alpha = O(1), each bucket has a constant expected number of entries. Lookup, insert, and delete touch only one bucket on average.

**Q3**: When does O(1) degrade to O(n)?
> When many keys collide into the same bucket (poor hash function, adversarial input, or very high load factor). The entire bucket must be scanned linearly.

**Q4**: Why do we need to resize a hash table?
> Without resizing, the load factor grows unbounded, chains lengthen, and O(1) performance degrades to O(n). Resizing (typically doubling) keeps the load factor below a threshold.

**Q5**: What is the problem with using mutable objects as hash map keys?
> If the object is modified after insertion, its hash code changes. The entry becomes unreachable because lookups compute the new hash, pointing to a different bucket.

**Q6**: How does Java 8+ HashMap handle long chains?
> When a chain exceeds 8 entries (treeify threshold), it converts the linked list into a red-black tree, guaranteeing O(log n) worst-case lookup per bucket.

**Q7**: Explain consistent hashing and why it matters.
> Consistent hashing maps keys and servers onto a ring. When a server is added/removed, only K/N keys need to remap (instead of nearly all). This is critical for distributed caches and databases.

---

## Coding Challenge 1: Two Sum

**Problem**: Given an array of integers and a target sum, return the indices of the two numbers that add up to the target. Assume exactly one solution exists.

**Approach**: Use a hash map to store `value -> index`. For each element, check if `target - element` exists in the map.

**Time**: O(n) | **Space**: O(n)

### Go

```go
package main

import "fmt"

func twoSum(nums []int, target int) [2]int {
    seen := make(map[int]int) // value -> index

    for i, num := range nums {
        complement := target - num
        if j, ok := seen[complement]; ok {
            return [2]int{j, i}
        }
        seen[num] = i
    }
    return [2]int{-1, -1} // No solution (should not happen per problem statement).
}

func main() {
    nums := []int{2, 7, 11, 15}
    result := twoSum(nums, 9)
    fmt.Println(result) // [0 1]

    nums2 := []int{3, 2, 4}
    fmt.Println(twoSum(nums2, 6)) // [1 2]
}
```

### Java

```java
import java.util.HashMap;
import java.util.Arrays;

public class TwoSum {

    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> seen = new HashMap<>(); // value -> index

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        System.out.println(Arrays.toString(twoSum(nums, 9))); // [0, 1]

        int[] nums2 = {3, 2, 4};
        System.out.println(Arrays.toString(twoSum(nums2, 6))); // [1, 2]
    }
}
```

### Python

```python
def two_sum(nums: list[int], target: int) -> list[int]:
    seen: dict[int, int] = {}  # value -> index

    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i

    return [-1, -1]


print(two_sum([2, 7, 11, 15], 9))  # [0, 1]
print(two_sum([3, 2, 4], 6))       # [1, 2]
```

---

## Coding Challenge 2: Group Anagrams

**Problem**: Given a list of strings, group the anagrams together. Two strings are anagrams if they contain the same characters in any order.

**Approach**: For each string, compute a canonical key (sorted characters). Use a hash map to group strings by their key.

**Time**: O(n * k log k) where k = max string length | **Space**: O(n * k)

### Go

```go
package main

import (
    "fmt"
    "sort"
    "strings"
)

func groupAnagrams(strs []string) [][]string {
    groups := make(map[string][]string)

    for _, s := range strs {
        // Sort the characters to create a canonical key.
        chars := strings.Split(s, "")
        sort.Strings(chars)
        key := strings.Join(chars, "")

        groups[key] = append(groups[key], s)
    }

    result := make([][]string, 0, len(groups))
    for _, group := range groups {
        result = append(result, group)
    }
    return result
}

func main() {
    strs := []string{"eat", "tea", "tan", "ate", "nat", "bat"}
    for _, group := range groupAnagrams(strs) {
        fmt.Println(group)
    }
    // Output (order may vary):
    // [eat tea ate]
    // [tan nat]
    // [bat]
}
```

### Java

```java
import java.util.*;

public class GroupAnagrams {

    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(groups.values());
    }

    public static void main(String[] args) {
        String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result = groupAnagrams(strs);
        for (List<String> group : result) {
            System.out.println(group);
        }
    }
}
```

### Python

```python
from collections import defaultdict


def group_anagrams(strs: list[str]) -> list[list[str]]:
    groups: dict[str, list[str]] = defaultdict(list)

    for s in strs:
        key = "".join(sorted(s))
        groups[key].append(s)

    return list(groups.values())


strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
for group in group_anagrams(strs):
    print(group)
# Output (order may vary):
# ['eat', 'tea', 'ate']
# ['tan', 'nat']
# ['bat']
```

**Alternative O(n * k) approach**: Instead of sorting, use a character frequency tuple as the key:
```python
def group_anagrams_freq(strs):
    groups = defaultdict(list)
    for s in strs:
        freq = [0] * 26
        for c in s:
            freq[ord(c) - ord('a')] += 1
        groups[tuple(freq)].append(s)
    return list(groups.values())
```

---

## Coding Challenge 3: LRU Cache

**Problem**: Design a data structure that supports `get(key)` and `put(key, value)` in O(1) time, evicting the least recently used entry when capacity is exceeded.

**Approach**: Hash map (O(1) lookup) + doubly linked list (O(1) move/remove). Most recent at head, least recent at tail.

### Go

```go
package main

import "fmt"

type node struct {
    key, value int
    prev, next *node
}

type LRUCache struct {
    capacity int
    cache    map[int]*node
    head     *node // dummy head (most recent side)
    tail     *node // dummy tail (least recent side)
}

func NewLRUCache(capacity int) *LRUCache {
    head := &node{}
    tail := &node{}
    head.next = tail
    tail.prev = head

    return &LRUCache{
        capacity: capacity,
        cache:    make(map[int]*node),
        head:     head,
        tail:     tail,
    }
}

func (c *LRUCache) removeNode(n *node) {
    n.prev.next = n.next
    n.next.prev = n.prev
}

func (c *LRUCache) addToFront(n *node) {
    n.next = c.head.next
    n.prev = c.head
    c.head.next.prev = n
    c.head.next = n
}

func (c *LRUCache) moveToFront(n *node) {
    c.removeNode(n)
    c.addToFront(n)
}

func (c *LRUCache) Get(key int) int {
    if n, ok := c.cache[key]; ok {
        c.moveToFront(n)
        return n.value
    }
    return -1
}

func (c *LRUCache) Put(key, value int) {
    if n, ok := c.cache[key]; ok {
        n.value = value
        c.moveToFront(n)
        return
    }

    newNode := &node{key: key, value: value}
    c.cache[key] = newNode
    c.addToFront(newNode)

    if len(c.cache) > c.capacity {
        // Evict least recently used (just before tail).
        lru := c.tail.prev
        c.removeNode(lru)
        delete(c.cache, lru.key)
    }
}

func main() {
    cache := NewLRUCache(2)
    cache.Put(1, 1)
    cache.Put(2, 2)
    fmt.Println(cache.Get(1)) // 1
    cache.Put(3, 3)           // Evicts key 2
    fmt.Println(cache.Get(2)) // -1
    cache.Put(4, 4)           // Evicts key 1
    fmt.Println(cache.Get(1)) // -1
    fmt.Println(cache.Get(3)) // 3
    fmt.Println(cache.Get(4)) // 4
}
```

### Java

```java
import java.util.HashMap;

public class LRUCache {

    private static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
        Node() {} // For dummy nodes.
    }

    private final int capacity;
    private final HashMap<Integer, Node> cache;
    private final Node head; // Dummy head.
    private final Node tail; // Dummy tail.

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node();
        this.tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) return -1;
        moveToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);
        if (node != null) {
            node.value = value;
            moveToFront(node);
            return;
        }

        Node newNode = new Node(key, value);
        cache.put(key, newNode);
        addToFront(newNode);

        if (cache.size() > capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            cache.remove(lru.key);
        }
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1)); // 1
        cache.put(3, 3);                  // Evicts key 2
        System.out.println(cache.get(2)); // -1
        cache.put(4, 4);                  // Evicts key 1
        System.out.println(cache.get(1)); // -1
        System.out.println(cache.get(3)); // 3
        System.out.println(cache.get(4)); // 4
    }
}
```

### Python

```python
"""LRU Cache using hash map + doubly linked list."""


class Node:
    __slots__ = ("key", "value", "prev", "next")

    def __init__(self, key: int = 0, value: int = 0):
        self.key = key
        self.value = value
        self.prev: Node | None = None
        self.next: Node | None = None


class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache: dict[int, Node] = {}
        # Dummy head and tail.
        self.head = Node()
        self.tail = Node()
        self.head.next = self.tail
        self.tail.prev = self.head

    def _remove(self, node: Node) -> None:
        node.prev.next = node.next
        node.next.prev = node.prev

    def _add_to_front(self, node: Node) -> None:
        node.next = self.head.next
        node.prev = self.head
        self.head.next.prev = node
        self.head.next = node

    def _move_to_front(self, node: Node) -> None:
        self._remove(node)
        self._add_to_front(node)

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1
        node = self.cache[key]
        self._move_to_front(node)
        return node.value

    def put(self, key: int, value: int) -> None:
        if key in self.cache:
            node = self.cache[key]
            node.value = value
            self._move_to_front(node)
            return

        new_node = Node(key, value)
        self.cache[key] = new_node
        self._add_to_front(new_node)

        if len(self.cache) > self.capacity:
            lru = self.tail.prev
            self._remove(lru)
            del self.cache[lru.key]


# Test
cache = LRUCache(2)
cache.put(1, 1)
cache.put(2, 2)
print(cache.get(1))  # 1
cache.put(3, 3)      # Evicts key 2
print(cache.get(2))  # -1
cache.put(4, 4)      # Evicts key 1
print(cache.get(1))  # -1
print(cache.get(3))  # 3
print(cache.get(4))  # 4
```

---

## Quick-Fire Questions

| # | Question | Expected Answer |
|---|----------|----------------|
| 1 | Time complexity of hash table insert (average)? | O(1) |
| 2 | What triggers a rehash? | Load factor exceeding threshold |
| 3 | How does chaining handle collisions? | Linked list per bucket |
| 4 | How does linear probing handle collisions? | Check next slot sequentially |
| 5 | What is a tombstone? | Marker for deleted slot in open addressing |
| 6 | Why is `HashMap` not thread-safe in Java? | No synchronization — concurrent modifications cause corruption |
| 7 | What data structure backs Python's `dict`? | Open-addressing hash table |
| 8 | What is the load factor of Java HashMap before resize? | 0.75 |
| 9 | Name a hash function resistant to DoS attacks. | SipHash |
| 10 | What is the worst-case complexity of hash table search? | O(n) |
| 11 | How does an LRU cache achieve O(1) eviction? | Hash map + doubly linked list |
| 12 | What does Go use instead of HashSet? | `map[T]struct{}` |
