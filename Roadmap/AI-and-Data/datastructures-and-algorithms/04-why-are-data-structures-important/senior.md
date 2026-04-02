# Why are Data Structures Important? — Senior Level

## Table of Contents

1. [Introduction](#introduction)
2. [DS in Production Systems](#ds-in-production-systems)
3. [Choosing DS for Distributed Systems](#choosing-ds-for-distributed-systems)
4. [DS for High-Throughput Low-Latency Systems](#ds-for-high-throughput-low-latency-systems)
5. [Trade-offs at Scale](#trade-offs-at-scale)
6. [Concurrent Data Structures](#concurrent-data-structures)
7. [Code Examples](#code-examples)
8. [Failure Modes](#failure-modes)
9. [Summary](#summary)

---

## Introduction

> Focus: "How do data structure choices shape production systems at scale?"

At the senior level, data structures are no longer abstract containers you choose during coding exercises. They are **architectural decisions** that determine latency percentiles, memory budgets, operational complexity, and failure domains. A wrong choice discovered in production costs weeks of engineering effort to replace.

This document covers how production systems like Redis, PostgreSQL, Kafka, and Memcached rely on specific data structures, how to choose DS for distributed environments, and how to build concurrent data structures that perform under contention.

---

## DS in Production Systems

### Redis Data Structures

Redis is fundamentally a **data structure server**. Every Redis command maps to an operation on a specific DS:

| Redis Type | Underlying DS | Use Case | Key Operations |
|---|---|---|---|
| String | Simple Dynamic String (SDS) | Caching, counters, session data | GET, SET, INCR |
| List | Quicklist (linked list of ziplists) | Message queues, activity feeds | LPUSH, RPOP, LRANGE |
| Set | Hash table (intset for small sets) | Tags, unique visitors, dedup | SADD, SMEMBERS, SINTER |
| Sorted Set | Skip list + hash table | Leaderboards, rate limiting, scheduling | ZADD, ZRANGE, ZRANGEBYSCORE |
| Hash | Ziplist (small) / Hash table (large) | Object storage, user profiles | HSET, HGET, HGETALL |
| Stream | Radix tree of listpacks | Event sourcing, log aggregation | XADD, XREAD, XRANGE |
| HyperLogLog | Probabilistic registers | Cardinality estimation (unique counts) | PFADD, PFCOUNT |
| Bloom Filter | Bit array + hash functions | Membership testing with false positives | BF.ADD, BF.EXISTS |

### Database Indexes — B+ Trees

Every relational database (PostgreSQL, MySQL, SQLite) uses **B+ trees** for indexes:

- Internal nodes store keys and pointers to children (fanout of 100-1000).
- Leaf nodes store key-value pairs and are linked sequentially.
- Height is typically 3-4 for billions of rows (log_{fanout}(N)).
- One disk read per level = 3-4 disk reads to find any row.

**Why B+ trees and not hash tables?** Hash tables support only equality queries (`WHERE id = 42`). B+ trees support equality, range queries (`WHERE age BETWEEN 20 AND 30`), prefix queries, and sorted iteration.

**Why B+ trees and not BSTs?** BSTs have one key per node and high height. B+ trees pack hundreds of keys per node, reducing disk reads. Each node fits in one disk page (4-16 KB).

### Message Queues — Log-Structured Storage

Kafka uses an **append-only log** (essentially a persistent array):

- Producers append messages to the end: O(1).
- Consumers read from any offset: O(1) with direct disk seek.
- No random writes, no deletions — sequential I/O only.
- This design achieves millions of messages per second per partition.

### Caches — Hash Tables with Eviction

Memcached and application-level caches are hash tables augmented with eviction policies:

| Eviction Policy | DS Required | Behavior |
|---|---|---|
| LRU (Least Recently Used) | Hash map + doubly linked list | Evict least recently accessed |
| LFU (Least Frequently Used) | Hash map + frequency buckets | Evict least frequently accessed |
| TTL (Time To Live) | Hash map + min-heap or timer wheel | Evict expired entries |
| Random | Hash map | Evict random entry (simplest) |

---

## Choosing DS for Distributed Systems

### Distributed Hash Tables (DHTs)

In distributed systems, a single hash map is not enough. You need **consistent hashing** to distribute keys across nodes:

```
Traditional hashing:   node = hash(key) % N
  Problem: Adding a node remaps almost all keys.

Consistent hashing:    node = first_node_clockwise_from(hash(key))
  Benefit: Adding a node remaps only 1/N keys.
```

| Distributed DS | Based On | Used By | Trade-off |
|---|---|---|---|
| Consistent hash ring | Hash table + ring | DynamoDB, Cassandra, Memcached | Even distribution vs rebalancing cost |
| Merkle tree | Hash tree | Git, blockchain, anti-entropy | Integrity verification vs storage |
| CRDTs | Various (counters, sets, maps) | Riak, Redis Enterprise | Eventual consistency vs complexity |
| LSM Tree | Sorted runs + merge | Cassandra, RocksDB, LevelDB | Write throughput vs read amplification |
| Raft/Paxos log | Append-only log | etcd, CockroachDB | Consensus vs latency |

### CAP Theorem and DS Implications

The choice of distributed data structure determines which guarantees you get:

- **CP systems** (Consistency + Partition tolerance): Use replicated logs with consensus (Raft). Strong consistency but higher latency.
- **AP systems** (Availability + Partition tolerance): Use CRDTs or last-writer-wins registers. Always available but eventually consistent.
- **CA systems** (not realistic in distributed systems): Single-node database with B+ tree indexes.

---

## DS for High-Throughput Low-Latency Systems

### Requirements

- **P99 latency < 1 ms** — Cannot afford O(n) operations on any path.
- **Millions of operations per second** — Must minimize allocation and contention.
- **Predictable performance** — Avoid data structures with amortized costs (hash table resize spikes).

### DS Selection for Low-Latency

| Requirement | DS Choice | Why |
|---|---|---|
| Fast key lookup | Open-addressing hash map | No pointer chasing, cache-friendly |
| Sorted data | B-tree (not BST) | Better cache locality, fewer levels |
| Queue with priority | D-ary heap (d=4 or 8) | Fewer cache misses than binary heap |
| Rate limiting | Token bucket (circular buffer) | O(1) operations, fixed memory |
| Time-based expiry | Timer wheel (hierarchical) | O(1) insert/cancel/expire |
| Deduplication | Bloom filter | O(1) check, minimal memory |
| Approximate counting | Count-Min Sketch | O(1) increment/query, fixed memory |

### Avoiding Latency Spikes

| DS | Spike Source | Mitigation |
|---|---|---|
| Hash table | Resize (copy all elements) | Pre-allocate, incremental resize |
| Dynamic array | Resize (copy all elements) | Pre-allocate capacity |
| BST (unbalanced) | Degenerate to O(n) | Use self-balancing (Red-Black, AVL) |
| Garbage-collected DS | GC pause | Use arena allocation, object pools |
| Linked structures | Memory fragmentation | Use slab allocation |

---

## Trade-offs at Scale

### Read-Optimized vs Write-Optimized

| Dimension | Read-Optimized | Write-Optimized |
|---|---|---|
| DS | B+ tree, sorted array, hash table | LSM tree, append-only log |
| Read | O(log n) or O(1) | O(log n) * levels (read amplification) |
| Write | O(log n) with random I/O | O(1) sequential append |
| Space | 1x data + index overhead | 2-10x due to compaction |
| Use case | OLTP databases, caches | Time-series DBs, event stores |

### Mutable vs Immutable

| Dimension | Mutable DS | Immutable DS |
|---|---|---|
| Update | In-place modification | Create new version, keep old |
| Concurrency | Needs locks or CAS | Lock-free (readers never blocked) |
| Memory | One copy | Multiple versions (higher memory) |
| Crash recovery | Complex (WAL needed) | Simple (old version always valid) |
| Examples | B+ tree, hash table | Persistent tree, append-only log |

### Memory vs Disk

| Dimension | In-Memory DS | On-Disk DS |
|---|---|---|
| Latency | Nanoseconds | Microseconds-milliseconds |
| Capacity | GBs (expensive) | TBs (cheap) |
| DS design | Optimize for CPU cache | Optimize for disk page size |
| Examples | Hash maps, skip lists, heaps | B+ trees, LSM trees, SSTables |

---

## Concurrent Data Structures

### Lock-Free vs Lock-Based

| Approach | Mechanism | Pros | Cons |
|---|---|---|---|
| Mutex | One thread at a time | Simple, correct | Contention, priority inversion |
| Read-Write Lock | Multiple readers OR one writer | Better read throughput | Write starvation possible |
| Lock-Free (CAS) | Compare-and-swap atomic ops | No blocking, no deadlock | Complex, ABA problem |
| Wait-Free | Every operation completes in bounded steps | Strongest guarantee | Very hard to implement |

### Common Concurrent DS

| DS | Concurrent Version | Used In |
|---|---|---|
| Hash Map | ConcurrentHashMap (Java), sync.Map (Go) | Caches, routing tables |
| Queue | Lock-free MPSC/MPMC queues | Message passing, work stealing |
| Skip List | ConcurrentSkipListMap (Java) | Sorted concurrent access |
| Counter | Atomic integer, LongAdder (Java) | Metrics, counters |
| Ring Buffer | Disruptor pattern (LMAX) | Ultra-low-latency event processing |

---

## Code Examples

### Concurrent-Safe Cache with Sharding

**Go:**

```go
package main

import (
    "fmt"
    "hash/fnv"
    "sync"
)

const numShards = 16

type ShardedCache struct {
    shards [numShards]struct {
        sync.RWMutex
        data map[string]string
    }
}

func NewShardedCache() *ShardedCache {
    c := &ShardedCache{}
    for i := 0; i < numShards; i++ {
        c.shards[i].data = make(map[string]string)
    }
    return c
}

func (c *ShardedCache) getShard(key string) int {
    h := fnv.New32a()
    h.Write([]byte(key))
    return int(h.Sum32()) % numShards
}

func (c *ShardedCache) Set(key, value string) {
    shard := c.getShard(key)
    c.shards[shard].Lock()
    c.shards[shard].data[key] = value
    c.shards[shard].Unlock()
}

func (c *ShardedCache) Get(key string) (string, bool) {
    shard := c.getShard(key)
    c.shards[shard].RLock()
    val, ok := c.shards[shard].data[key]
    c.shards[shard].RUnlock()
    return val, ok
}

func main() {
    cache := NewShardedCache()
    cache.Set("user:1", "Alice")
    cache.Set("user:2", "Bob")

    if val, ok := cache.Get("user:1"); ok {
        fmt.Println("Found:", val)
    }
}
```

**Java:**

```java
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentCache {
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        cache.put(key, value);
    }

    public String get(String key) {
        return cache.get(key);
    }

    public String getOrCompute(String key, java.util.function.Function<String, String> loader) {
        return cache.computeIfAbsent(key, loader);
    }

    public static void main(String[] args) {
        ConcurrentCache cache = new ConcurrentCache();
        cache.set("user:1", "Alice");
        cache.set("user:2", "Bob");

        // Thread-safe get-or-compute
        String value = cache.getOrCompute("user:3", key -> {
            // Simulate expensive computation
            return "Carol (computed)";
        });
        System.out.println("user:3 = " + value);
    }
}
```

**Python:**

```python
import threading
from collections import OrderedDict

class ThreadSafeLRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.cache = OrderedDict()
        self.lock = threading.Lock()

    def get(self, key):
        with self.lock:
            if key not in self.cache:
                return None
            self.cache.move_to_end(key)
            return self.cache[key]

    def put(self, key, value):
        with self.lock:
            if key in self.cache:
                self.cache.move_to_end(key)
            self.cache[key] = value
            if len(self.cache) > self.capacity:
                self.cache.popitem(last=False)

cache = ThreadSafeLRUCache(capacity=3)
cache.put("user:1", "Alice")
cache.put("user:2", "Bob")
cache.put("user:3", "Carol")
cache.put("user:4", "Dave")  # Evicts "user:1"

print(cache.get("user:1"))  # None (evicted)
print(cache.get("user:2"))  # Bob
```

---

### Timer Wheel for Scheduled Expiration

**Go:**

```go
package main

import (
    "fmt"
    "time"
)

type TimerWheel struct {
    slots    [][]string
    numSlots int
    current  int
    interval time.Duration
}

func NewTimerWheel(numSlots int, interval time.Duration) *TimerWheel {
    slots := make([][]string, numSlots)
    for i := range slots {
        slots[i] = []string{}
    }
    return &TimerWheel{slots: slots, numSlots: numSlots, interval: interval}
}

func (tw *TimerWheel) Schedule(key string, delay time.Duration) {
    ticks := int(delay / tw.interval)
    slot := (tw.current + ticks) % tw.numSlots
    tw.slots[slot] = append(tw.slots[slot], key)
}

func (tw *TimerWheel) Tick() []string {
    tw.current = (tw.current + 1) % tw.numSlots
    expired := tw.slots[tw.current]
    tw.slots[tw.current] = []string{}
    return expired
}

func main() {
    wheel := NewTimerWheel(60, time.Second)
    wheel.Schedule("session:abc", 5*time.Second)
    wheel.Schedule("session:def", 5*time.Second)
    wheel.Schedule("session:ghi", 10*time.Second)

    for i := 0; i < 15; i++ {
        expired := wheel.Tick()
        if len(expired) > 0 {
            fmt.Printf("Tick %d: expired %v\n", i+1, expired)
        }
    }
}
```

**Java:**

```java
import java.util.*;

public class TimerWheel {
    private final List<String>[] slots;
    private final int numSlots;
    private int current = 0;

    @SuppressWarnings("unchecked")
    public TimerWheel(int numSlots) {
        this.numSlots = numSlots;
        this.slots = new ArrayList[numSlots];
        for (int i = 0; i < numSlots; i++) {
            slots[i] = new ArrayList<>();
        }
    }

    public void schedule(String key, int delayTicks) {
        int slot = (current + delayTicks) % numSlots;
        slots[slot].add(key);
    }

    public List<String> tick() {
        current = (current + 1) % numSlots;
        List<String> expired = slots[current];
        slots[current] = new ArrayList<>();
        return expired;
    }

    public static void main(String[] args) {
        TimerWheel wheel = new TimerWheel(60);
        wheel.schedule("session:abc", 5);
        wheel.schedule("session:def", 5);
        wheel.schedule("session:ghi", 10);

        for (int i = 0; i < 15; i++) {
            List<String> expired = wheel.tick();
            if (!expired.isEmpty()) {
                System.out.printf("Tick %d: expired %s%n", i + 1, expired);
            }
        }
    }
}
```

**Python:**

```python
class TimerWheel:
    def __init__(self, num_slots):
        self.num_slots = num_slots
        self.slots = [[] for _ in range(num_slots)]
        self.current = 0

    def schedule(self, key, delay_ticks):
        slot = (self.current + delay_ticks) % self.num_slots
        self.slots[slot].append(key)

    def tick(self):
        self.current = (self.current + 1) % self.num_slots
        expired = self.slots[self.current]
        self.slots[self.current] = []
        return expired

wheel = TimerWheel(60)
wheel.schedule("session:abc", 5)
wheel.schedule("session:def", 5)
wheel.schedule("session:ghi", 10)

for i in range(15):
    expired = wheel.tick()
    if expired:
        print(f"Tick {i + 1}: expired {expired}")
```

---

## Failure Modes

| DS in Production | Failure Mode | Impact | Mitigation |
|---|---|---|---|
| Hash table cache | Hash collision attack | O(n) lookups, DoS | Use SipHash or randomized hash |
| B+ tree index | Index bloat after deletions | Slow queries, wasted disk | REINDEX periodically |
| Bloom filter | False positive rate too high | Unnecessary work | Size filter for target FP rate |
| LSM tree | Write amplification | Disk I/O spikes during compaction | Leveled compaction, rate limiting |
| Skip list | Memory fragmentation | Increased latency over time | Memory pool / arena allocator |
| Concurrent hash map | Hot shard | One shard gets all traffic | Better hash function, more shards |
| Timer wheel | Slot overflow | Missed expirations | Hierarchical timer wheel |

---

## Summary

| Concept | Key Takeaway |
|---|---|
| Redis | A data structure server — choosing the right Redis type = choosing the right DS |
| Database indexes | B+ trees enable O(log n) queries on billions of rows |
| Message queues | Append-only logs achieve millions of writes/sec with sequential I/O |
| Distributed DS | Consistent hashing, CRDTs, LSM trees power distributed databases |
| Low-latency DS | Open-addressing hash maps, D-ary heaps, timer wheels minimize cache misses |
| Trade-offs at scale | Read-optimized vs write-optimized, mutable vs immutable, memory vs disk |
| Concurrent DS | Sharding, lock-free algorithms, and CAS operations enable safe parallelism |
| Failure modes | Every production DS has failure modes — plan for them |

---

> **Remember:** In production, the data structure IS the system. Redis is a skip list. PostgreSQL is a B+ tree. Kafka is an append-only log. Choose wisely.
