# Why are Data Structures Important? — Practical Tasks

## Table of Contents

1. [Beginner Tasks](#beginner-tasks)
   - [Task 1: Compare Array vs Hash Set for Membership](#task-1-compare-array-vs-hash-set-for-membership)
   - [Task 2: Benchmark Array vs Linked List vs Hash Map](#task-2-benchmark-array-vs-linked-list-vs-hash-map)
   - [Task 3: Frequency Counter with Different DS](#task-3-frequency-counter-with-different-ds)
   - [Task 4: Build a Simple Cache](#task-4-build-a-simple-cache)
   - [Task 5: Two Sum with Different DS](#task-5-two-sum-with-different-ds)
2. [Intermediate Tasks](#intermediate-tasks)
   - [Task 6: Design DS for Autocomplete](#task-6-design-ds-for-autocomplete)
   - [Task 7: Implement a Rate Limiter](#task-7-implement-a-rate-limiter)
   - [Task 8: Find K Most Frequent Elements](#task-8-find-k-most-frequent-elements)
   - [Task 9: Sliding Window Maximum](#task-9-sliding-window-maximum)
   - [Task 10: Design a Browser History](#task-10-design-a-browser-history)
3. [Advanced Tasks](#advanced-tasks)
   - [Task 11: LRU Cache with O(1) Operations](#task-11-lru-cache-with-o1-operations)
   - [Task 12: Median Finder with Two Heaps](#task-12-median-finder-with-two-heaps)
   - [Task 13: Design a Leaderboard](#task-13-design-a-leaderboard)
   - [Task 14: Implement Insert/Delete/GetRandom O(1)](#task-14-implement-insertdeletegetrandom-o1)
   - [Task 15: Design a File System](#task-15-design-a-file-system)
4. [Benchmark Task](#benchmark-task)

---

## Beginner Tasks

### Task 1: Compare Array vs Hash Set for Membership

**Objective:** Write two versions of a membership check function — one using an array (linear scan) and one using a hash set. Benchmark both on 1,000,000 elements with 10,000 lookups.

**Requirements:**
- `containsArray(items, target)` — Linear scan, O(n) per call.
- `containsSet(items, target)` — Hash set lookup, O(1) per call.
- Measure and print elapsed time for both approaches.

**Starter Code:**

**Go:**

```go
package main

import (
    "fmt"
    "time"
)

func containsArray(items []int, target int) bool {
    // TODO: Linear scan
    return false
}

func containsSet(items map[int]struct{}, target int) bool {
    // TODO: Hash set lookup
    return false
}

func main() {
    size := 1_000_000
    arr := make([]int, size)
    set := make(map[int]struct{}, size)
    for i := 0; i < size; i++ {
        arr[i] = i
        set[i] = struct{}{}
    }

    targets := make([]int, 10_000)
    for i := range targets {
        targets[i] = i * 100
    }

    // TODO: Benchmark containsArray with all targets
    start := time.Now()
    // ...
    fmt.Printf("Array: %v\n", time.Since(start))

    // TODO: Benchmark containsSet with all targets
    start = time.Now()
    // ...
    fmt.Printf("Set: %v\n", time.Since(start))
}
```

**Java:**

```java
import java.util.*;

public class MembershipBenchmark {
    public static boolean containsArray(int[] items, int target) {
        // TODO: Linear scan
        return false;
    }

    public static boolean containsSet(Set<Integer> items, int target) {
        // TODO: Hash set lookup
        return false;
    }

    public static void main(String[] args) {
        int size = 1_000_000;
        int[] arr = new int[size];
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            arr[i] = i;
            set.add(i);
        }

        int[] targets = new int[10_000];
        for (int i = 0; i < targets.length; i++) {
            targets[i] = i * 100;
        }

        // TODO: Benchmark both approaches and print times
    }
}
```

**Python:**

```python
import time

def contains_array(items, target):
    # TODO: Linear scan
    pass

def contains_set(items, target):
    # TODO: Set lookup
    pass

size = 1_000_000
arr = list(range(size))
s = set(range(size))
targets = [i * 100 for i in range(10_000)]

# TODO: Benchmark both approaches and print times
```

**Expected Result:** Hash set should be 100-1000x faster than array scan.

---

### Task 2: Benchmark Array vs Linked List vs Hash Map

**Objective:** Measure insert, search, and delete times for array, linked list, and hash map with N = 100,000 elements. Present results in a table.

**Requirements:**
- Insert N elements into each DS.
- Search for 1,000 random elements.
- Delete 1,000 random elements.
- Print a comparison table with times for each operation.

**Starter Code:**

**Go:**

```go
package main

import (
    "container/list"
    "fmt"
    "math/rand"
    "time"
)

func main() {
    n := 100_000
    searchKeys := make([]int, 1000)
    for i := range searchKeys {
        searchKeys[i] = rand.Intn(n)
    }

    // Array (slice)
    var arr []int
    start := time.Now()
    for i := 0; i < n; i++ {
        arr = append(arr, i)
    }
    arrInsert := time.Since(start)

    // Linked list
    ll := list.New()
    start = time.Now()
    for i := 0; i < n; i++ {
        ll.PushBack(i)
    }
    llInsert := time.Since(start)

    // Hash map
    m := make(map[int]bool)
    start = time.Now()
    for i := 0; i < n; i++ {
        m[i] = true
    }
    mapInsert := time.Since(start)

    // TODO: Benchmark search and delete for each DS
    // TODO: Print comparison table
    fmt.Println("Insert:", arrInsert, llInsert, mapInsert)
}
```

**Java:**

```java
import java.util.*;

public class DSBenchmark {
    public static void main(String[] args) {
        int n = 100_000;
        Random rand = new Random();
        int[] searchKeys = new int[1000];
        for (int i = 0; i < 1000; i++) searchKeys[i] = rand.nextInt(n);

        // TODO: Benchmark ArrayList, LinkedList, HashMap
        // TODO: Measure insert, search, delete for each
        // TODO: Print comparison table
    }
}
```

**Python:**

```python
import time
import random

n = 100_000
search_keys = [random.randint(0, n - 1) for _ in range(1000)]

# TODO: Benchmark list, collections.deque (as linked list), dict
# TODO: Measure insert, search, delete for each
# TODO: Print comparison table
```

---

### Task 3: Frequency Counter with Different DS

**Objective:** Count word frequencies in a text using three approaches: nested loops O(n^2), sorted array O(n log n), and hash map O(n). Benchmark all three.

**Requirements:**
- `frequencyBruteForce(words)` — For each unique word, count occurrences with inner loop.
- `frequencySorted(words)` — Sort words, then count consecutive duplicates.
- `frequencyHashMap(words)` — Single pass with hash map.
- All three must return identical results.

**Starter Code:**

**Go:**

```go
package main

func frequencyBruteForce(words []string) map[string]int {
    // TODO: O(n^2) — for each word, scan all words to count
    return nil
}

func frequencySorted(words []string) map[string]int {
    // TODO: O(n log n) — sort, then count consecutive runs
    return nil
}

func frequencyHashMap(words []string) map[string]int {
    // TODO: O(n) — single pass
    return nil
}
```

**Java:**

```java
import java.util.*;

public class FrequencyCounter {
    public static Map<String, Integer> frequencyBruteForce(String[] words) {
        // TODO
        return null;
    }

    public static Map<String, Integer> frequencySorted(String[] words) {
        // TODO
        return null;
    }

    public static Map<String, Integer> frequencyHashMap(String[] words) {
        // TODO
        return null;
    }
}
```

**Python:**

```python
def frequency_brute_force(words):
    # TODO: O(n^2)
    pass

def frequency_sorted(words):
    # TODO: O(n log n)
    pass

def frequency_hash_map(words):
    # TODO: O(n)
    pass
```

---

### Task 4: Build a Simple Cache

**Objective:** Implement a cache with a maximum capacity. When the cache is full and a new item is added, evict the oldest item (FIFO eviction).

**Requirements:**
- `put(key, value)` — Store key-value pair. If full, evict oldest entry.
- `get(key)` — Return value or indicate miss. O(1).
- `size()` — Return current number of entries.
- Maximum capacity is set at creation.

**Starter Code:**

**Go:**

```go
package main

type FIFOCache struct {
    capacity int
    data     map[string]string
    order    []string // tracks insertion order
}

func NewFIFOCache(capacity int) *FIFOCache {
    // TODO
    return nil
}

func (c *FIFOCache) Put(key, value string) {
    // TODO: If full, evict oldest (first in order slice)
    // Add new entry
}

func (c *FIFOCache) Get(key string) (string, bool) {
    // TODO: O(1) lookup
    return "", false
}

func (c *FIFOCache) Size() int {
    // TODO
    return 0
}
```

**Java:**

```java
import java.util.*;

public class FIFOCache {
    private final int capacity;
    private final Map<String, String> data;
    private final Queue<String> order;

    public FIFOCache(int capacity) {
        // TODO
        this.capacity = capacity;
        this.data = null;
        this.order = null;
    }

    public void put(String key, String value) {
        // TODO
    }

    public String get(String key) {
        // TODO
        return null;
    }

    public int size() {
        // TODO
        return 0;
    }
}
```

**Python:**

```python
from collections import OrderedDict

class FIFOCache:
    def __init__(self, capacity):
        # TODO
        pass

    def put(self, key, value):
        # TODO
        pass

    def get(self, key):
        # TODO
        pass

    def size(self):
        # TODO
        pass
```

---

### Task 5: Two Sum with Different DS

**Objective:** Given an array of integers and a target sum, find two numbers that add up to the target. Implement brute-force O(n^2) and hash map O(n) solutions.

**Starter Code:**

**Go:**

```go
package main

func twoSumBrute(nums []int, target int) (int, int) {
    // TODO: O(n^2) nested loop
    return -1, -1
}

func twoSumHash(nums []int, target int) (int, int) {
    // TODO: O(n) single pass with hash map
    return -1, -1
}
```

**Java:**

```java
public class TwoSum {
    public static int[] twoSumBrute(int[] nums, int target) {
        // TODO: O(n^2)
        return new int[]{-1, -1};
    }

    public static int[] twoSumHash(int[] nums, int target) {
        // TODO: O(n)
        return new int[]{-1, -1};
    }
}
```

**Python:**

```python
def two_sum_brute(nums, target):
    # TODO: O(n^2)
    return (-1, -1)

def two_sum_hash(nums, target):
    # TODO: O(n)
    return (-1, -1)
```

---

## Intermediate Tasks

### Task 6: Design DS for Autocomplete

**Objective:** Build an autocomplete system that suggests words given a prefix. Use a trie for O(k) prefix lookup where k is the prefix length.

**Requirements:**
- `insert(word)` — Add a word to the dictionary.
- `search(prefix)` — Return all words starting with the given prefix.
- Use a trie (prefix tree) internally.

**Starter Code:**

**Go:**

```go
package main

type TrieNode struct {
    children map[byte]*TrieNode
    isEnd    bool
    word     string
}

type Autocomplete struct {
    root *TrieNode
}

func NewAutocomplete() *Autocomplete {
    // TODO
    return nil
}

func (ac *Autocomplete) Insert(word string) {
    // TODO: Walk trie, create nodes as needed, mark end
}

func (ac *Autocomplete) Search(prefix string) []string {
    // TODO: Walk to prefix node, then DFS to collect all words
    return nil
}
```

**Java:**

```java
import java.util.*;

public class Autocomplete {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        String word;
    }

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        // TODO
    }

    public List<String> search(String prefix) {
        // TODO
        return new ArrayList<>();
    }
}
```

**Python:**

```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False
        self.word = None

class Autocomplete:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word):
        # TODO
        pass

    def search(self, prefix):
        # TODO
        pass
```

---

### Task 7: Implement a Rate Limiter

**Objective:** Build a sliding window rate limiter that allows at most `max_requests` in the last `window_seconds`. Use a queue (deque) to track request timestamps.

**Requirements:**
- `allow(timestamp)` — Return True if the request is allowed, False otherwise.
- Remove expired timestamps from the window.

**Starter Code:**

**Go:**

```go
package main

type RateLimiter struct {
    maxRequests int
    windowSecs  int
    timestamps  []int // use as a queue
}

func NewRateLimiter(maxRequests, windowSecs int) *RateLimiter {
    // TODO
    return nil
}

func (rl *RateLimiter) Allow(timestamp int) bool {
    // TODO: Remove expired, check count, add if allowed
    return false
}
```

**Java:**

```java
import java.util.*;

public class RateLimiter {
    private final int maxRequests;
    private final int windowSecs;
    private final Deque<Integer> timestamps;

    public RateLimiter(int maxRequests, int windowSecs) {
        // TODO
        this.maxRequests = maxRequests;
        this.windowSecs = windowSecs;
        this.timestamps = new ArrayDeque<>();
    }

    public boolean allow(int timestamp) {
        // TODO
        return false;
    }
}
```

**Python:**

```python
from collections import deque

class RateLimiter:
    def __init__(self, max_requests, window_secs):
        # TODO
        pass

    def allow(self, timestamp):
        # TODO
        pass
```

---

### Task 8: Find K Most Frequent Elements

**Objective:** Given an array, find the K most frequent elements. Use a hash map for counting + a min-heap of size K.

**Requirements:**
- Time: O(n log k) where n is array length and k is the number of top elements.
- Space: O(n) for the frequency map + O(k) for the heap.

**Starter Code:**

**Go:**

```go
package main

func topKFrequent(nums []int, k int) []int {
    // TODO: 1. Count frequencies with hash map
    // TODO: 2. Use min-heap of size k to find top k
    return nil
}
```

**Java:**

```java
import java.util.*;

public class TopKFrequent {
    public static List<Integer> topKFrequent(int[] nums, int k) {
        // TODO: HashMap + PriorityQueue (min-heap)
        return new ArrayList<>();
    }
}
```

**Python:**

```python
import heapq
from collections import Counter

def top_k_frequent(nums, k):
    # TODO: Counter + heapq.nlargest
    pass
```

---

### Task 9: Sliding Window Maximum

**Objective:** Given an array and window size k, find the maximum in each sliding window. Use a deque (monotonic queue) for O(n) total.

**Starter Code:**

**Go:**

```go
package main

func maxSlidingWindow(nums []int, k int) []int {
    // TODO: Use deque to maintain indices of candidates in decreasing order
    return nil
}
```

**Java:**

```java
import java.util.*;

public class SlidingWindowMax {
    public static int[] maxSlidingWindow(int[] nums, int k) {
        // TODO: Deque-based monotonic queue
        return new int[0];
    }
}
```

**Python:**

```python
from collections import deque

def max_sliding_window(nums, k):
    # TODO: Deque-based monotonic queue
    pass
```

---

### Task 10: Design a Browser History

**Objective:** Implement browser history with back and forward functionality using two stacks.

**Requirements:**
- `visit(url)` — Visit a new URL. Clears forward history.
- `back(steps)` — Go back up to `steps` pages. Return current URL.
- `forward(steps)` — Go forward up to `steps` pages. Return current URL.

**Starter Code:**

**Go:**

```go
package main

type BrowserHistory struct {
    backStack    []string
    forwardStack []string
    current      string
}

func NewBrowserHistory(homepage string) *BrowserHistory {
    // TODO
    return nil
}

func (bh *BrowserHistory) Visit(url string) {
    // TODO: Push current to backStack, clear forwardStack
}

func (bh *BrowserHistory) Back(steps int) string {
    // TODO: Move pages from backStack to forwardStack
    return ""
}

func (bh *BrowserHistory) Forward(steps int) string {
    // TODO: Move pages from forwardStack to backStack
    return ""
}
```

**Java:**

```java
import java.util.*;

public class BrowserHistory {
    private final Deque<String> backStack = new ArrayDeque<>();
    private final Deque<String> forwardStack = new ArrayDeque<>();
    private String current;

    public BrowserHistory(String homepage) {
        // TODO
    }

    public void visit(String url) { /* TODO */ }
    public String back(int steps) { /* TODO */ return ""; }
    public String forward(int steps) { /* TODO */ return ""; }
}
```

**Python:**

```python
class BrowserHistory:
    def __init__(self, homepage):
        # TODO
        pass

    def visit(self, url):
        # TODO
        pass

    def back(self, steps):
        # TODO
        pass

    def forward(self, steps):
        # TODO
        pass
```

---

## Advanced Tasks

### Task 11: LRU Cache with O(1) Operations

**Objective:** Implement an LRU cache using a hash map + doubly linked list. All operations must be O(1).

**Requirements:**
- `get(key)` — Return value and mark as recently used. O(1).
- `put(key, value)` — Insert or update. If over capacity, evict LRU item. O(1).

**Starter Code:**

**Go:**

```go
package main

type Node struct {
    key, value int
    prev, next *Node
}

type LRUCache struct {
    capacity   int
    cache      map[int]*Node
    head, tail *Node // dummy sentinel nodes
}

func NewLRUCache(capacity int) *LRUCache {
    // TODO: Initialize with dummy head and tail
    return nil
}

func (c *LRUCache) Get(key int) int {
    // TODO: Lookup in map, move to front, return value
    return -1
}

func (c *LRUCache) Put(key, value int) {
    // TODO: Insert/update, move to front, evict tail if over capacity
}
```

**Java:**

```java
import java.util.*;

public class LRUCache {
    // TODO: Define Node class with key, value, prev, next
    // TODO: HashMap<Integer, Node> + doubly linked list with sentinel nodes

    public LRUCache(int capacity) { /* TODO */ }
    public int get(int key) { /* TODO */ return -1; }
    public void put(int key, int value) { /* TODO */ }
}
```

**Python:**

```python
class Node:
    def __init__(self, key=0, value=0):
        self.key = key
        self.value = value
        self.prev = None
        self.next = None

class LRUCache:
    def __init__(self, capacity):
        # TODO: dict + doubly linked list with sentinel nodes
        pass

    def get(self, key):
        # TODO
        pass

    def put(self, key, value):
        # TODO
        pass
```

---

### Task 12: Median Finder with Two Heaps

**Objective:** Design a data structure that finds the median from a stream of numbers. Use a max-heap for the lower half and a min-heap for the upper half.

**Requirements:**
- `addNum(num)` — Add a number. O(log n).
- `findMedian()` — Return the median. O(1).

**Starter Code:**

**Go:**

```go
package main

import "container/heap"

type MedianFinder struct {
    // TODO: maxHeap for lower half, minHeap for upper half
}

func (mf *MedianFinder) AddNum(num int) {
    // TODO: Add to appropriate heap, rebalance if needed
}

func (mf *MedianFinder) FindMedian() float64 {
    // TODO: Return median based on heap tops
    return 0
}
```

**Java:**

```java
import java.util.*;

public class MedianFinder {
    private PriorityQueue<Integer> maxHeap; // lower half
    private PriorityQueue<Integer> minHeap; // upper half

    public MedianFinder() {
        // TODO
    }

    public void addNum(int num) { /* TODO */ }
    public double findMedian() { /* TODO */ return 0; }
}
```

**Python:**

```python
import heapq

class MedianFinder:
    def __init__(self):
        self.max_heap = []  # negated values for max-heap
        self.min_heap = []

    def add_num(self, num):
        # TODO
        pass

    def find_median(self):
        # TODO
        pass
```

---

### Task 13: Design a Leaderboard

**Objective:** Build a leaderboard that supports adding scores, querying top K players, and resetting scores. Use a hash map + sorted structure.

**Requirements:**
- `addScore(playerId, score)` — Add score to player's total.
- `top(K)` — Return sum of top K scores.
- `reset(playerId)` — Reset player's score to 0.

---

### Task 14: Implement Insert/Delete/GetRandom O(1)

**Objective:** Design a data structure that supports insert, delete, and getRandom in O(1) average time. Use an array + hash map.

**Requirements:**
- `insert(val)` — Insert if not present. O(1).
- `remove(val)` — Remove if present. O(1).
- `getRandom()` — Return a random element with equal probability. O(1).

**Hint:** Array stores elements. Hash map stores element-to-index mapping. On delete, swap with last element.

---

### Task 15: Design a File System

**Objective:** Implement an in-memory file system using a tree structure (trie of directories).

**Requirements:**
- `mkdir(path)` — Create a directory path.
- `ls(path)` — List contents of a directory.
- `addFile(path, content)` — Create a file with content.
- `readFile(path)` — Read file content.

---

## Benchmark Task

**Objective:** Create a comprehensive benchmark comparing the following data structures for the same set of operations (insert 100K, search 10K, delete 10K, iterate all):

1. Array / ArrayList / list
2. Hash Map / HashMap / dict
3. Hash Set / HashSet / set
4. Sorted Array (with binary search)
5. Tree Map / TreeMap (Java only, simulate in Go/Python)

**Output Format:**

```
| DS           | Insert 100K | Search 10K | Delete 10K | Iterate 100K | Memory |
|--------------|-------------|------------|------------|--------------|--------|
| Array        | ___ms       | ___ms      | ___ms      | ___ms        | ___MB  |
| Hash Map     | ___ms       | ___ms      | ___ms      | ___ms        | ___MB  |
| Hash Set     | ___ms       | ___ms      | ___ms      | ___ms        | ___MB  |
| Sorted Array | ___ms       | ___ms      | ___ms      | ___ms        | ___MB  |
| Tree Map     | ___ms       | ___ms      | ___ms      | ___ms        | ___MB  |
```

**Goal:** Verify that theoretical Big-O predictions match practical measurements. Note where constant factors or cache effects cause surprises.
