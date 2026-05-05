# Hash Table — Senior Level

## Table of Contents

1. [Hash Tables in Production Systems](#hash-tables-in-production-systems)
   - [Redis](#redis)
   - [Memcached](#memcached)
   - [Database Hash Indexes](#database-hash-indexes)
2. [Consistent Hashing](#consistent-hashing)
3. [Distributed Hash Tables (DHTs)](#distributed-hash-tables-dhts)
4. [Concurrent Hash Maps](#concurrent-hash-maps)
   - [Go: sync.Map](#go-syncmap)
   - [Java: ConcurrentHashMap](#java-concurrenthashmap)
   - [Python: Thread-Safe Dictionaries](#python-thread-safe-dictionaries)
5. [Hash-Based Caching Strategies](#hash-based-caching-strategies)
6. [Bloom Filters](#bloom-filters)
7. [Implementation: Consistent Hash Ring](#implementation-consistent-hash-ring)

---

## Hash Tables in Production Systems

### Redis

Redis is an in-memory key-value store built around hash tables.

**Internal structure**: Redis uses a `dict` with two hash tables (`ht[0]` and `ht[1]`) to support **incremental rehashing**.

- **Incremental rehashing**: Instead of blocking to rehash all entries at once (which could pause a server for seconds), Redis rehashes a few entries on every read/write operation. During rehashing, lookups check both tables; new inserts go to `ht[1]`.
- **Hash function**: Redis uses **SipHash** (a keyed hash function resistant to hash-flooding DoS attacks).
- **Load factor trigger**: Resize when load factor > 1 (or > 5 during background save, to avoid COW memory amplification).

**Lesson for engineers**: Incremental rehashing is critical when a single rehash could freeze your service. The dual-table approach is elegant and widely applicable.

### Memcached

Memcached uses a hash table to map cache keys to slab allocations.

- **Hash function**: Uses Jenkins hash (or MurmurHash3 in newer versions).
- **Lock striping**: The hash table is divided into lock segments — each segment protects a range of buckets, allowing concurrent access without a global lock.
- **Hash power**: Table size is always 2^n. When expanding, Memcached uses the "hash power" to double the table.

### Database Hash Indexes

Most relational databases offer hash indexes as an alternative to B-tree indexes.

**PostgreSQL** hash index:
- Single exact-match lookups in O(1).
- Does NOT support range queries, ordering, or partial matches.
- WAL-logged since PostgreSQL 10 (before that, they were not crash-safe).

**When to use hash indexes**:
- Equality comparisons only (`WHERE id = 42`).
- Very large tables where B-tree depth becomes significant.
- When range scans are never needed for that column.

**When NOT to use**: Range queries, ORDER BY, LIKE patterns — all require B-tree.

---

## Consistent Hashing

In a distributed system with N servers, simple hashing (`server = hash(key) % N`) fails badly when servers are added or removed — nearly all keys remap.

**Consistent hashing** solves this by mapping both keys and servers onto a virtual ring (0 to 2^32 - 1):

1. Each server is placed at one or more positions on the ring (via hashing the server ID).
2. To find which server stores a key, hash the key and walk clockwise until you hit a server.
3. When a server is added, only keys between the new server and its predecessor need to move.
4. When a server is removed, only its keys move to the next server clockwise.

**Virtual nodes**: Each physical server gets multiple positions on the ring (e.g., 150-200 virtual nodes). This ensures even distribution even with few physical servers.

**Key property**: Adding/removing a server remaps only `K/N` keys on average (where K = total keys, N = servers), compared to nearly all keys with naive modular hashing.

**Used by**: Amazon DynamoDB, Apache Cassandra, Akamai CDN, Riak.

---

## Distributed Hash Tables (DHTs)

A DHT is a decentralized system where the hash table is spread across many nodes, with no central coordinator.

**Properties**:
- **Decentralized**: No single point of failure.
- **Scalable**: Each node stores only a portion of the key space.
- **Fault-tolerant**: Data is replicated across multiple nodes.

**Chord Protocol** (canonical DHT):
- Each node and key is assigned an m-bit identifier via SHA-1.
- Node `n` is responsible for all keys `k` where `k` falls between `n`'s predecessor and `n` on the ring.
- Each node maintains a **finger table** of O(log N) pointers, enabling lookups in O(log N) hops.
- When a node joins or leaves, only O(log^2 N) entries need to be updated.

**Kademlia** (used by BitTorrent, Ethereum):
- Uses XOR distance metric: `distance(a, b) = a XOR b`.
- Each node maintains k-buckets for different distance ranges.
- Lookup in O(log N) hops.

---

## Concurrent Hash Maps

### Go: sync.Map

Go's `sync.Map` is optimized for two specific use cases:
1. Keys are written once but read many times (stable key set).
2. Multiple goroutines read/write disjoint sets of keys.

```go
package main

import (
    "fmt"
    "sync"
)

func main() {
    var m sync.Map

    // Store key-value pairs (concurrent-safe).
    m.Store("alice", 95)
    m.Store("bob", 82)
    m.Store("carol", 91)

    // Load a value.
    val, ok := m.Load("bob")
    if ok {
        fmt.Printf("bob: %d\n", val.(int))
    }

    // LoadOrStore: load existing or store new.
    actual, loaded := m.LoadOrStore("dave", 78)
    fmt.Printf("dave: %d (loaded=%v)\n", actual.(int), loaded)

    // Delete.
    m.Delete("carol")

    // Range over all entries.
    m.Range(func(key, value any) bool {
        fmt.Printf("%s: %d\n", key.(string), value.(int))
        return true // continue iteration
    })
}
```

**Internals**: `sync.Map` uses a read-only map (atomic pointer) plus a dirty map (mutex-protected). Reads that hit the read-only map are lock-free. After enough "misses," the dirty map is promoted to read-only.

**When NOT to use**: If you have frequent writes to overlapping keys, a `sync.RWMutex` + regular `map` is often faster.

### Java: ConcurrentHashMap

Java's `ConcurrentHashMap` is the standard for concurrent hash maps.

```java
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentExample {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // Basic operations (thread-safe).
        map.put("alice", 95);
        map.put("bob", 82);
        map.put("carol", 91);

        // putIfAbsent — atomic check-and-insert.
        map.putIfAbsent("dave", 78);

        // compute — atomic read-modify-write.
        map.compute("alice", (key, val) -> val == null ? 1 : val + 5);
        System.out.println("alice: " + map.get("alice")); // 100

        // merge — atomic merge with existing value.
        map.merge("bob", 10, Integer::sum);
        System.out.println("bob: " + map.get("bob")); // 92

        // forEach with parallelism threshold.
        map.forEach(1, (key, val) ->
            System.out.printf("%s: %d%n", key, val));
    }
}
```

**Internals (Java 8+)**:
- Uses **CAS (Compare-And-Swap)** operations for lock-free reads and most writes.
- Each bucket is synchronized independently (fine-grained locking).
- Long chains (> 8 entries) are converted to red-black trees (treeification).
- No global lock — throughput scales nearly linearly with cores.

### Python: Thread-Safe Dictionaries

Python's `dict` is protected by the **GIL** (Global Interpreter Lock), which makes individual operations atomic — but compound operations are NOT safe.

```python
import threading
from collections import defaultdict

# Individual operations are GIL-atomic:
shared = {}
shared["key"] = 42      # Safe
val = shared.get("key") # Safe

# But check-then-act is NOT atomic:
# Thread 1: if "key" not in d: d["key"] = 1
# Thread 2: if "key" not in d: d["key"] = 2
# Both may pass the check before either writes.

# Solution: use a lock for compound operations.
lock = threading.Lock()

def safe_increment(d: dict, key: str) -> None:
    with lock:
        d[key] = d.get(key, 0) + 1

# For high-concurrency scenarios, consider:
# 1. concurrent.futures with isolated data
# 2. multiprocessing.Manager().dict() for process-safe dicts
# 3. Third-party: diskcache, redis-py for distributed caching
```

---

## Hash-Based Caching Strategies

### LRU Cache with Hash Map

An LRU (Least Recently Used) cache combines a **hash map** (O(1) lookup) with a **doubly linked list** (O(1) eviction):

- Hash map: `key -> node pointer` for O(1) lookup.
- Doubly linked list: maintains access order. Most recent at head, least recent at tail.
- On access: move node to head.
- On eviction: remove tail node, delete from hash map.

### Write-Through vs Write-Behind

| Strategy | Hash Table Role | Consistency | Performance |
|----------|----------------|-------------|-------------|
| Write-Through | Cache entry updated, then DB written | Strong | Slower writes |
| Write-Behind | Cache entry updated, DB write is async | Eventual | Faster writes |
| Read-Through | On miss, cache loads from DB | Strong reads | First read slow |

### Cache Stampede Prevention

When a popular cache entry expires, many threads may simultaneously compute the new value (thundering herd).

Solutions:
1. **Locking**: First thread to miss acquires a lock; others wait.
2. **Probabilistic early expiration**: Each reader has a small chance of recomputing before TTL expires.
3. **Stale-while-revalidate**: Serve stale data while one thread recomputes.

---

## Bloom Filters

A **Bloom filter** is a probabilistic data structure that uses hashing to test set membership.

- **No false negatives**: If the Bloom filter says "not present," the element is definitely not in the set.
- **Possible false positives**: If it says "maybe present," the element might or might not be in the set.
- **Space-efficient**: Uses far less memory than a hash set.

**How it works**:
1. A bit array of `m` bits, initially all 0.
2. `k` independent hash functions, each mapping a key to a position in [0, m).
3. **Insert(key)**: Compute `h1(key), h2(key), ..., hk(key)` and set those bits to 1.
4. **Query(key)**: Compute all k hashes. If ALL corresponding bits are 1, return "maybe present." If ANY bit is 0, return "definitely not present."

**Optimal parameters**:
- For `n` expected elements and desired false positive rate `p`:
  - `m = -n * ln(p) / (ln(2))^2` (bits)
  - `k = (m / n) * ln(2)` (hash functions)

**Applications**:
- **Databases**: PostgreSQL and Cassandra use Bloom filters to avoid unnecessary disk reads.
- **Web browsers**: Chrome uses Bloom filters to check URLs against a malicious-site blacklist.
- **Network routers**: Check if a packet has been seen before (deduplication).
- **Spell checkers**: Quick "definitely not a word" check.

---

## Implementation: Consistent Hash Ring

### Go

```go
package main

import (
    "fmt"
    "hash/crc32"
    "sort"
    "strconv"
)

type ConsistentHash struct {
    ring       map[uint32]string // hash -> node name
    sortedKeys []uint32
    replicas   int // virtual nodes per physical node
}

func NewConsistentHash(replicas int) *ConsistentHash {
    return &ConsistentHash{
        ring:     make(map[uint32]string),
        replicas: replicas,
    }
}

func (ch *ConsistentHash) hashKey(key string) uint32 {
    return crc32.ChecksumIEEE([]byte(key))
}

func (ch *ConsistentHash) AddNode(node string) {
    for i := 0; i < ch.replicas; i++ {
        virtualKey := node + "#" + strconv.Itoa(i)
        h := ch.hashKey(virtualKey)
        ch.ring[h] = node
        ch.sortedKeys = append(ch.sortedKeys, h)
    }
    sort.Slice(ch.sortedKeys, func(i, j int) bool {
        return ch.sortedKeys[i] < ch.sortedKeys[j]
    })
}

func (ch *ConsistentHash) GetNode(key string) string {
    if len(ch.sortedKeys) == 0 {
        return ""
    }
    h := ch.hashKey(key)
    idx := sort.Search(len(ch.sortedKeys), func(i int) bool {
        return ch.sortedKeys[i] >= h
    })
    if idx >= len(ch.sortedKeys) {
        idx = 0 // Wrap around the ring.
    }
    return ch.ring[ch.sortedKeys[idx]]
}

func main() {
    ch := NewConsistentHash(150)
    ch.AddNode("server-A")
    ch.AddNode("server-B")
    ch.AddNode("server-C")

    keys := []string{"user:1001", "user:1002", "user:1003", "session:abc", "session:xyz"}
    for _, k := range keys {
        fmt.Printf("%s -> %s\n", k, ch.GetNode(k))
    }
}
```

### Java

```java
import java.util.*;
import java.util.zip.CRC32;

public class ConsistentHash {
    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final int replicas;

    public ConsistentHash(int replicas) {
        this.replicas = replicas;
    }

    private long hash(String key) {
        CRC32 crc = new CRC32();
        crc.update(key.getBytes());
        return crc.getValue();
    }

    public void addNode(String node) {
        for (int i = 0; i < replicas; i++) {
            long h = hash(node + "#" + i);
            ring.put(h, node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < replicas; i++) {
            long h = hash(node + "#" + i);
            ring.remove(h);
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        long h = hash(key);
        Map.Entry<Long, String> entry = ring.ceilingEntry(h);
        if (entry == null) {
            entry = ring.firstEntry(); // Wrap around.
        }
        return entry.getValue();
    }

    public static void main(String[] args) {
        ConsistentHash ch = new ConsistentHash(150);
        ch.addNode("server-A");
        ch.addNode("server-B");
        ch.addNode("server-C");

        String[] keys = {"user:1001", "user:1002", "user:1003", "session:abc"};
        for (String k : keys) {
            System.out.printf("%s -> %s%n", k, ch.getNode(k));
        }
    }
}
```

### Python

```python
"""Consistent hash ring implementation."""

import hashlib
from bisect import bisect_right


class ConsistentHash:
    def __init__(self, replicas: int = 150):
        self._replicas = replicas
        self._ring: dict[int, str] = {}
        self._sorted_keys: list[int] = []

    def _hash(self, key: str) -> int:
        digest = hashlib.md5(key.encode()).hexdigest()
        return int(digest, 16)

    def add_node(self, node: str) -> None:
        for i in range(self._replicas):
            virtual_key = f"{node}#{i}"
            h = self._hash(virtual_key)
            self._ring[h] = node
            self._sorted_keys.append(h)
        self._sorted_keys.sort()

    def remove_node(self, node: str) -> None:
        for i in range(self._replicas):
            virtual_key = f"{node}#{i}"
            h = self._hash(virtual_key)
            self._ring.pop(h, None)
            self._sorted_keys.remove(h)

    def get_node(self, key: str) -> str | None:
        if not self._sorted_keys:
            return None
        h = self._hash(key)
        idx = bisect_right(self._sorted_keys, h)
        if idx >= len(self._sorted_keys):
            idx = 0  # Wrap around.
        return self._ring[self._sorted_keys[idx]]


if __name__ == "__main__":
    ch = ConsistentHash(replicas=150)
    ch.add_node("server-A")
    ch.add_node("server-B")
    ch.add_node("server-C")

    keys = ["user:1001", "user:1002", "user:1003", "session:abc", "session:xyz"]
    for k in keys:
        print(f"{k} -> {ch.get_node(k)}")
```

---

## Key Takeaways

1. **Production hash tables** (Redis, Memcached) use incremental rehashing to avoid latency spikes.
2. **Consistent hashing** with virtual nodes is essential for distributed systems — it minimizes key remapping when nodes change.
3. **Concurrent hash maps** use fine-grained locking, CAS operations, or read-only snapshots — never a single global lock.
4. **Bloom filters** provide space-efficient probabilistic membership testing — invaluable for avoiding expensive I/O.
5. **Cache design** combines hash maps with eviction policies (LRU, LFU) and must handle stampedes.
6. **DHTs** (Chord, Kademlia) enable fully decentralized key-value storage with O(log N) lookups.
