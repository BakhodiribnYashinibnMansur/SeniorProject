# Constant Time O(1) -- Senior Level

## Table of Contents

1. [Introduction](#introduction)
2. [O(1) in Distributed Systems](#o1-in-distributed-systems)
   - [Consistent Hashing](#consistent-hashing)
   - [Redis O(1) Operations](#redis-o1-operations)
   - [Bloom Filters](#bloom-filters)
3. [Lock-Free O(1) Operations](#lock-free-o1-operations)
   - [Compare-And-Swap (CAS)](#compare-and-swap-cas)
   - [Lock-Free Stack](#lock-free-stack)
   - [Atomic Counters](#atomic-counters)
4. [O(1) Memory Allocation Strategies](#o1-memory-allocation-strategies)
   - [Slab Allocation](#slab-allocation)
   - [Free List Allocation](#free-list-allocation)
   - [Arena Allocation](#arena-allocation)
5. [System Design with O(1) Lookups](#system-design-with-o1-lookups)
   - [LRU Cache](#lru-cache)
   - [Rate Limiter](#rate-limiter)
   - [URL Shortener](#url-shortener)
6. [Key Takeaways](#key-takeaways)

---

## Introduction

At the senior level, O(1) goes beyond data structure operations. It becomes a design
principle: how do you architect entire systems so that critical-path operations remain
constant time? This section covers O(1) in distributed systems, concurrent programming,
memory management, and system design.

---

## O(1) in Distributed Systems

### Consistent Hashing

Traditional hash-based sharding (`server = hash(key) % N`) breaks when you add or remove
servers -- all keys need remapping. Consistent hashing achieves O(1) key-to-server
mapping with minimal redistribution.

**How it works:**
1. Arrange servers on a virtual ring (0 to 2^32 - 1).
2. Hash each server's name to a position on the ring.
3. To find which server owns a key, hash the key and walk clockwise to the first server.
4. With virtual nodes and a sorted structure, lookup is O(log N) where N is the number
   of servers. With a precomputed lookup table, it becomes O(1).

#### Go

```go
package main

import (
    "fmt"
    "hash/crc32"
    "sort"
)

type ConsistentHash struct {
    ring       map[uint32]string // hash -> server name
    sortedKeys []uint32
    replicas   int
}

func NewConsistentHash(replicas int) *ConsistentHash {
    return &ConsistentHash{
        ring:     make(map[uint32]string),
        replicas: replicas,
    }
}

func (ch *ConsistentHash) AddServer(server string) {
    for i := 0; i < ch.replicas; i++ {
        key := fmt.Sprintf("%s-%d", server, i)
        hash := crc32.ChecksumIEEE([]byte(key))
        ch.ring[hash] = server
        ch.sortedKeys = append(ch.sortedKeys, hash)
    }
    sort.Slice(ch.sortedKeys, func(i, j int) bool {
        return ch.sortedKeys[i] < ch.sortedKeys[j]
    })
}

// GetServer returns the server for a given key.
// O(log N) with binary search on the ring, where N = servers * replicas.
// For a fixed number of servers, this is effectively O(1) per key lookup.
func (ch *ConsistentHash) GetServer(key string) string {
    hash := crc32.ChecksumIEEE([]byte(key))
    idx := sort.Search(len(ch.sortedKeys), func(i int) bool {
        return ch.sortedKeys[i] >= hash
    })
    if idx == len(ch.sortedKeys) {
        idx = 0
    }
    return ch.ring[ch.sortedKeys[idx]]
}

func main() {
    ch := NewConsistentHash(100)
    ch.AddServer("server-A")
    ch.AddServer("server-B")
    ch.AddServer("server-C")

    keys := []string{"user:1001", "user:1002", "session:abc", "cache:xyz"}
    for _, k := range keys {
        fmt.Printf("%s -> %s\n", k, ch.GetServer(k))
    }
}
```

#### Java

```java
import java.util.*;
import java.util.zip.CRC32;

public class ConsistentHash {
    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final int replicas;

    public ConsistentHash(int replicas) {
        this.replicas = replicas;
    }

    public void addServer(String server) {
        for (int i = 0; i < replicas; i++) {
            CRC32 crc = new CRC32();
            crc.update((server + "-" + i).getBytes());
            ring.put(crc.getValue(), server);
        }
    }

    // O(log N) lookup on the TreeMap, effectively O(1) for fixed server count
    public String getServer(String key) {
        CRC32 crc = new CRC32();
        crc.update(key.getBytes());
        long hash = crc.getValue();
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry();
        return entry.getValue();
    }

    public static void main(String[] args) {
        ConsistentHash ch = new ConsistentHash(100);
        ch.addServer("server-A");
        ch.addServer("server-B");
        ch.addServer("server-C");

        String[] keys = {"user:1001", "user:1002", "session:abc", "cache:xyz"};
        for (String k : keys) {
            System.out.printf("%s -> %s%n", k, ch.getServer(k));
        }
    }
}
```

#### Python

```python
import hashlib
from bisect import bisect_right

class ConsistentHash:
    def __init__(self, replicas=100):
        self.replicas = replicas
        self.ring = {}        # hash -> server
        self.sorted_keys = []

    def _hash(self, key):
        return int(hashlib.md5(key.encode()).hexdigest(), 16)

    def add_server(self, server):
        for i in range(self.replicas):
            h = self._hash(f"{server}-{i}")
            self.ring[h] = server
            self.sorted_keys.append(h)
        self.sorted_keys.sort()

    def get_server(self, key):
        """O(log N) on the ring, effectively O(1) for fixed server count."""
        h = self._hash(key)
        idx = bisect_right(self.sorted_keys, h) % len(self.sorted_keys)
        return self.ring[self.sorted_keys[idx]]


ch = ConsistentHash(replicas=100)
ch.add_server("server-A")
ch.add_server("server-B")
ch.add_server("server-C")

for k in ["user:1001", "user:1002", "session:abc", "cache:xyz"]:
    print(f"{k} -> {ch.get_server(k)}")
```

### Redis O(1) Operations

Redis is designed around O(1) complexity for its most common commands:

| Command | Complexity | Description |
|---------|-----------|-------------|
| GET key | O(1) | Retrieve a string value |
| SET key value | O(1) | Set a string value |
| HGET hash field | O(1) | Get field from hash |
| HSET hash field value | O(1) | Set field in hash |
| LPUSH list value | O(1) | Push to head of list |
| RPUSH list value | O(1) | Push to tail of list |
| LPOP list | O(1) | Pop from head of list |
| SADD set member | O(1) | Add member to set |
| SISMEMBER set member | O(1) | Check set membership |
| INCR key | O(1) | Atomic increment |

Redis achieves this by keeping everything in memory and using hash tables as the primary
data structure for key-value storage.

### Bloom Filters

A Bloom filter is a probabilistic data structure that answers "is X in the set?" in
O(1) time with possible false positives but no false negatives.

#### Go

```go
package main

import (
    "fmt"
    "hash"
    "hash/fnv"
)

type BloomFilter struct {
    bits    []bool
    size    int
    hashFns []hash.Hash64
}

func NewBloomFilter(size int, numHashes int) *BloomFilter {
    bf := &BloomFilter{
        bits: make([]bool, size),
        size: size,
    }
    // Use seeded FNV hashes
    for i := 0; i < numHashes; i++ {
        bf.hashFns = append(bf.hashFns, fnv.New64a())
    }
    return bf
}

func (bf *BloomFilter) hash(item string, seed int) int {
    bf.hashFns[0].Reset()
    bf.hashFns[0].Write([]byte(item))
    bf.hashFns[0].Write([]byte{byte(seed)})
    return int(bf.hashFns[0].Sum64() % uint64(bf.size))
}

// Add is O(k) where k = number of hash functions. Since k is constant, this is O(1).
func (bf *BloomFilter) Add(item string) {
    for i := range bf.hashFns {
        idx := bf.hash(item, i)
        bf.bits[idx] = true
    }
}

// Contains is O(k) = O(1) since k is constant.
// Returns true if item MIGHT be in the set (possible false positive).
// Returns false if item is DEFINITELY not in the set.
func (bf *BloomFilter) Contains(item string) bool {
    for i := range bf.hashFns {
        idx := bf.hash(item, i)
        if !bf.bits[idx] {
            return false
        }
    }
    return true
}

func main() {
    bf := NewBloomFilter(1000, 3)
    bf.Add("apple")
    bf.Add("banana")
    bf.Add("cherry")

    fmt.Println("apple:", bf.Contains("apple"))   // true
    fmt.Println("banana:", bf.Contains("banana"))  // true
    fmt.Println("grape:", bf.Contains("grape"))    // false (probably)
    fmt.Println("mango:", bf.Contains("mango"))    // false (probably)
}
```

#### Java

```java
import java.util.BitSet;

public class BloomFilter {
    private final BitSet bits;
    private final int size;
    private final int numHashes;

    public BloomFilter(int size, int numHashes) {
        this.bits = new BitSet(size);
        this.size = size;
        this.numHashes = numHashes;
    }

    private int hash(String item, int seed) {
        int h = item.hashCode() ^ (seed * 0x9e3779b9);
        return Math.abs(h % size);
    }

    // O(k) = O(1) since k is constant
    public void add(String item) {
        for (int i = 0; i < numHashes; i++) {
            bits.set(hash(item, i));
        }
    }

    // O(k) = O(1) since k is constant
    public boolean contains(String item) {
        for (int i = 0; i < numHashes; i++) {
            if (!bits.get(hash(item, i))) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        BloomFilter bf = new BloomFilter(1000, 3);
        bf.add("apple");
        bf.add("banana");
        bf.add("cherry");

        System.out.println("apple: " + bf.contains("apple"));
        System.out.println("grape: " + bf.contains("grape"));
    }
}
```

#### Python

```python
class BloomFilter:
    def __init__(self, size=1000, num_hashes=3):
        self.size = size
        self.num_hashes = num_hashes
        self.bits = [False] * size

    def _hashes(self, item):
        """Generate k hash positions. O(k) = O(1) since k is constant."""
        for seed in range(self.num_hashes):
            h = hash((item, seed)) % self.size
            yield abs(h)

    def add(self, item):
        for pos in self._hashes(item):
            self.bits[pos] = True

    def contains(self, item):
        return all(self.bits[pos] for pos in self._hashes(item))


bf = BloomFilter(1000, 3)
bf.add("apple")
bf.add("banana")
bf.add("cherry")

print("apple:", bf.contains("apple"))   # True
print("grape:", bf.contains("grape"))    # False (probably)
```

---

## Lock-Free O(1) Operations

### Compare-And-Swap (CAS)

CAS is a hardware-level atomic instruction that enables O(1) lock-free updates. It
atomically: reads the current value, compares it to an expected value, and writes a new
value only if the comparison succeeds.

### Atomic Counters

The simplest lock-free O(1) operation is an atomic counter.

#### Go

```go
package main

import (
    "fmt"
    "sync"
    "sync/atomic"
)

func main() {
    var counter int64 = 0
    var wg sync.WaitGroup

    // 1000 goroutines each incrementing 1000 times
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for j := 0; j < 1000; j++ {
                // Atomic increment is O(1) and lock-free
                atomic.AddInt64(&counter, 1)
            }
        }()
    }

    wg.Wait()
    fmt.Println("Counter:", counter) // Always 1,000,000
}
```

#### Java

```java
import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounterDemo {
    public static void main(String[] args) throws InterruptedException {
        AtomicLong counter = new AtomicLong(0);
        Thread[] threads = new Thread[1000];

        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // Atomic increment is O(1) and lock-free
                    counter.incrementAndGet();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();
        System.out.println("Counter: " + counter.get()); // Always 1,000,000
    }
}
```

#### Python

```python
import threading

# Python's GIL makes simple increments thread-safe, but for explicit atomicity:
counter = 0
lock = threading.Lock()

def increment():
    global counter
    for _ in range(1000):
        with lock:  # O(1) per increment (ignoring contention)
            counter += 1

threads = [threading.Thread(target=increment) for _ in range(100)]
for t in threads:
    t.start()
for t in threads:
    t.join()

print(f"Counter: {counter}")  # Always 100,000
```

### Lock-Free Stack

A lock-free stack using CAS provides O(1) push and pop without locks.

#### Go

```go
package main

import (
    "fmt"
    "sync"
    "sync/atomic"
    "unsafe"
)

type node struct {
    value int
    next  *node
}

type LockFreeStack struct {
    head unsafe.Pointer // *node
}

// Push is O(1) lock-free using CAS
func (s *LockFreeStack) Push(value int) {
    newNode := &node{value: value}
    for {
        oldHead := atomic.LoadPointer(&s.head)
        newNode.next = (*node)(oldHead)
        if atomic.CompareAndSwapPointer(&s.head, oldHead, unsafe.Pointer(newNode)) {
            return
        }
        // CAS failed, another goroutine modified head -- retry
    }
}

// Pop is O(1) lock-free using CAS
func (s *LockFreeStack) Pop() (int, bool) {
    for {
        oldHead := atomic.LoadPointer(&s.head)
        if oldHead == nil {
            return 0, false
        }
        oldNode := (*node)(oldHead)
        newHead := unsafe.Pointer(oldNode.next)
        if atomic.CompareAndSwapPointer(&s.head, oldHead, newHead) {
            return oldNode.value, true
        }
    }
}

func main() {
    stack := &LockFreeStack{}
    var wg sync.WaitGroup

    // Concurrent pushes
    for i := 0; i < 10; i++ {
        wg.Add(1)
        go func(val int) {
            defer wg.Done()
            stack.Push(val)
        }(i)
    }
    wg.Wait()

    // Pop all
    for {
        val, ok := stack.Pop()
        if !ok {
            break
        }
        fmt.Print(val, " ")
    }
    fmt.Println()
}
```

---

## O(1) Memory Allocation Strategies

### Free List Allocation

Maintain a linked list of pre-allocated free blocks. Allocate = pop from free list (O(1)).
Deallocate = push to free list (O(1)).

#### Go

```go
package main

import "fmt"

type Block struct {
    data [64]byte // Fixed-size block
    next *Block
}

type FreeListAllocator struct {
    freeList *Block
    allocs   int
    frees    int
}

func NewFreeListAllocator(poolSize int) *FreeListAllocator {
    alloc := &FreeListAllocator{}
    // Pre-allocate pool
    for i := 0; i < poolSize; i++ {
        block := &Block{next: alloc.freeList}
        alloc.freeList = block
    }
    return alloc
}

// Allocate is O(1) -- just pop from the free list
func (a *FreeListAllocator) Allocate() *Block {
    if a.freeList == nil {
        return nil // Pool exhausted
    }
    block := a.freeList
    a.freeList = block.next
    block.next = nil
    a.allocs++
    return block
}

// Free is O(1) -- just push to the free list
func (a *FreeListAllocator) Free(block *Block) {
    block.next = a.freeList
    a.freeList = block
    a.frees++
}

func main() {
    alloc := NewFreeListAllocator(1000)

    // Allocate 500 blocks -- each is O(1)
    blocks := make([]*Block, 500)
    for i := range blocks {
        blocks[i] = alloc.Allocate()
    }
    fmt.Printf("Allocated: %d\n", alloc.allocs)

    // Free them all -- each is O(1)
    for _, b := range blocks {
        alloc.Free(b)
    }
    fmt.Printf("Freed: %d\n", alloc.frees)
}
```

### Arena Allocation

An arena (bump allocator) allocates by simply incrementing a pointer. O(1) allocation,
O(1) bulk deallocation (free the entire arena at once).

---

## System Design with O(1) Lookups

### LRU Cache

An LRU (Least Recently Used) cache provides O(1) get and O(1) put by combining a hash
map with a doubly-linked list.

#### Go

```go
package main

import "fmt"

type entry struct {
    key        string
    value      int
    prev, next *entry
}

type LRUCache struct {
    capacity int
    cache    map[string]*entry
    head     *entry // most recently used
    tail     *entry // least recently used
}

func NewLRUCache(capacity int) *LRUCache {
    head := &entry{}
    tail := &entry{}
    head.next = tail
    tail.prev = head
    return &LRUCache{
        capacity: capacity,
        cache:    make(map[string]*entry),
        head:     head,
        tail:     tail,
    }
}

func (c *LRUCache) remove(e *entry) {
    e.prev.next = e.next
    e.next.prev = e.prev
}

func (c *LRUCache) addToFront(e *entry) {
    e.next = c.head.next
    e.prev = c.head
    c.head.next.prev = e
    c.head.next = e
}

// Get is O(1): hash map lookup + linked list pointer manipulation
func (c *LRUCache) Get(key string) (int, bool) {
    if e, ok := c.cache[key]; ok {
        c.remove(e)
        c.addToFront(e)
        return e.value, true
    }
    return 0, false
}

// Put is O(1): hash map insert + linked list pointer manipulation
func (c *LRUCache) Put(key string, value int) {
    if e, ok := c.cache[key]; ok {
        e.value = value
        c.remove(e)
        c.addToFront(e)
        return
    }
    e := &entry{key: key, value: value}
    c.cache[key] = e
    c.addToFront(e)
    if len(c.cache) > c.capacity {
        lru := c.tail.prev
        c.remove(lru)
        delete(c.cache, lru.key)
    }
}

func main() {
    cache := NewLRUCache(3)
    cache.Put("a", 1)
    cache.Put("b", 2)
    cache.Put("c", 3)

    fmt.Println(cache.Get("a")) // 1, true -- "a" moves to front
    cache.Put("d", 4)           // evicts "b" (least recently used)
    fmt.Println(cache.Get("b")) // 0, false -- evicted
    fmt.Println(cache.Get("c")) // 3, true
}
```

#### Java

```java
import java.util.*;

public class LRUCache {
    private final int capacity;
    private final Map<String, Integer> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        // LinkedHashMap with accessOrder=true provides O(1) LRU behavior
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    public Integer get(String key) { return cache.get(key); }
    public void put(String key, int value) { cache.put(key, value); }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        System.out.println(cache.get("a")); // 1
        cache.put("d", 4);                   // evicts "b"
        System.out.println(cache.get("b")); // null
    }
}
```

#### Python

```python
from collections import OrderedDict

class LRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.cache = OrderedDict()

    def get(self, key):
        """O(1) lookup + move to end."""
        if key in self.cache:
            self.cache.move_to_end(key)
            return self.cache[key]
        return None

    def put(self, key, value):
        """O(1) insert/update + eviction."""
        if key in self.cache:
            self.cache.move_to_end(key)
        self.cache[key] = value
        if len(self.cache) > self.capacity:
            self.cache.popitem(last=False)  # Remove oldest


cache = LRUCache(3)
cache.put("a", 1)
cache.put("b", 2)
cache.put("c", 3)
print(cache.get("a"))  # 1
cache.put("d", 4)       # evicts "b"
print(cache.get("b"))  # None
```

---

## Key Takeaways

1. **Consistent hashing** enables O(1) key-to-server mapping in distributed systems
   with minimal redistribution when servers are added/removed.

2. **Redis** is specifically designed so that common operations (GET, SET, HGET, LPUSH,
   etc.) are O(1), making it ideal for caching and real-time data.

3. **Lock-free O(1) operations** using CAS provide thread-safe constant-time operations
   without the overhead and contention of locks.

4. **Free list and arena allocators** provide O(1) memory allocation for fixed-size
   objects, critical in latency-sensitive systems.

5. **LRU caches** combine hash maps and doubly-linked lists to achieve O(1) for both
   get and put operations -- a fundamental system design pattern.

6. **Bloom filters** provide O(1) probabilistic set membership testing with configurable
   false positive rates, used in databases and network systems.
