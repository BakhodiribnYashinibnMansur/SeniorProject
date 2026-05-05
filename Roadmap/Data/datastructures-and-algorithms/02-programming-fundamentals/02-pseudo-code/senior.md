# Pseudo Code — Senior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Pseudo Code in System Design](#pseudo-code-in-system-design)
3. [Algorithm Design Paradigms](#algorithm-design-paradigms)
4. [Pseudo Code for Distributed Systems](#pseudo-code-for-distributed-systems)
5. [Code Examples](#code-examples)
6. [Pseudo Code in Technical Documentation](#pseudo-code-in-technical-documentation)
7. [Summary](#summary)

---

## Introduction

> Focus: "How to use pseudo code for system-level algorithm design?"

At the senior level, pseudo code is used to describe distributed algorithms, consensus protocols, and system-level designs. It serves as a communication bridge between architects, engineers, and stakeholders.

---

## Pseudo Code in System Design

### Rate Limiter (Token Bucket)

```text
STRUCTURE TokenBucket
    SET capacity = maxTokens
    SET tokens = maxTokens
    SET refillRate = tokensPerSecond
    SET lastRefillTime = currentTime()

FUNCTION allowRequest(bucket)
    CALL refill(bucket)
    IF bucket.tokens >= 1 THEN
        SET bucket.tokens = bucket.tokens - 1
        RETURN true      // request allowed
    ELSE
        RETURN false     // rate limited
    END IF
END FUNCTION

FUNCTION refill(bucket)
    SET now = currentTime()
    SET elapsed = now - bucket.lastRefillTime
    SET newTokens = elapsed * bucket.refillRate
    SET bucket.tokens = min(bucket.capacity, bucket.tokens + newTokens)
    SET bucket.lastRefillTime = now
END FUNCTION
```

#### Go

```go
package main

import (
    "sync"
    "time"
)

type TokenBucket struct {
    mu             sync.Mutex
    capacity       float64
    tokens         float64
    refillRate     float64
    lastRefillTime time.Time
}

func NewTokenBucket(capacity, refillRate float64) *TokenBucket {
    return &TokenBucket{
        capacity:       capacity,
        tokens:         capacity,
        refillRate:     refillRate,
        lastRefillTime: time.Now(),
    }
}

func (tb *TokenBucket) Allow() bool {
    tb.mu.Lock()
    defer tb.mu.Unlock()

    now := time.Now()
    elapsed := now.Sub(tb.lastRefillTime).Seconds()
    tb.tokens = min(tb.capacity, tb.tokens+elapsed*tb.refillRate)
    tb.lastRefillTime = now

    if tb.tokens >= 1 {
        tb.tokens--
        return true
    }
    return false
}

func min(a, b float64) float64 {
    if a < b { return a }
    return b
}
```

#### Java

```java
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {
    private final ReentrantLock lock = new ReentrantLock();
    private final double capacity;
    private double tokens;
    private final double refillRate;
    private long lastRefillTime;

    public TokenBucket(double capacity, double refillRate) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.refillRate = refillRate;
        this.lastRefillTime = System.nanoTime();
    }

    public boolean allow() {
        lock.lock();
        try {
            long now = System.nanoTime();
            double elapsed = (now - lastRefillTime) / 1e9;
            tokens = Math.min(capacity, tokens + elapsed * refillRate);
            lastRefillTime = now;
            if (tokens >= 1) {
                tokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
```

#### Python

```python
import time
import threading

class TokenBucket:
    def __init__(self, capacity, refill_rate):
        self.capacity = capacity
        self.tokens = capacity
        self.refill_rate = refill_rate
        self.last_refill = time.monotonic()
        self.lock = threading.Lock()

    def allow(self):
        with self.lock:
            now = time.monotonic()
            elapsed = now - self.last_refill
            self.tokens = min(self.capacity, self.tokens + elapsed * self.refill_rate)
            self.last_refill = now
            if self.tokens >= 1:
                self.tokens -= 1
                return True
            return False
```

---

## Algorithm Design Paradigms

### Paradigm: Greedy (Activity Selection)

```text
FUNCTION activitySelection(activities)
    SORT activities by finish time (ascending)
    SET selected = [activities[0]]
    SET lastFinish = activities[0].finish

    FOR i = 1 TO length(activities) - 1 DO
        IF activities[i].start >= lastFinish THEN
            APPEND activities[i] TO selected
            SET lastFinish = activities[i].finish
        END IF
    END FOR

    RETURN selected
END FUNCTION

// Correctness argument (greedy choice property):
// The activity that finishes first leaves the most room
// for remaining activities. Choosing it is always safe.
//
// Time: O(n log n) for sort + O(n) for selection = O(n log n)
```

### Paradigm: Backtracking (N-Queens)

```text
FUNCTION solveNQueens(n)
    SET board = n×n grid of zeros
    SET solutions = empty list
    CALL placeQueens(board, 0, n, solutions)
    RETURN solutions
END FUNCTION

FUNCTION placeQueens(board, row, n, solutions)
    IF row == n THEN
        APPEND copy of board TO solutions  // found valid placement
        RETURN
    END IF

    FOR col = 0 TO n-1 DO
        IF CALL isSafe(board, row, col, n) THEN
            SET board[row][col] = 1         // place queen
            CALL placeQueens(board, row + 1, n, solutions)
            SET board[row][col] = 0         // backtrack (remove queen)
        END IF
    END FOR
END FUNCTION

FUNCTION isSafe(board, row, col, n)
    // Check column above
    FOR i = 0 TO row-1 DO
        IF board[i][col] == 1 THEN RETURN false
    END FOR
    // Check upper-left diagonal
    SET i = row-1, j = col-1
    WHILE i >= 0 AND j >= 0 DO
        IF board[i][j] == 1 THEN RETURN false
        SET i = i-1, j = j-1
    END WHILE
    // Check upper-right diagonal
    SET i = row-1, j = col+1
    WHILE i >= 0 AND j < n DO
        IF board[i][j] == 1 THEN RETURN false
        SET i = i-1, j = j+1
    END WHILE
    RETURN true
END FUNCTION
```

---

## Pseudo Code for Distributed Systems

### Consensus: Simplified Raft Leader Election

```text
// Each node has: state (follower/candidate/leader), term, votedFor, log[]

FUNCTION onElectionTimeout(node)
    SET node.state = "candidate"
    SET node.term = node.term + 1
    SET node.votedFor = node.id
    SET votesReceived = 1    // vote for self

    FOR each peer IN cluster DO
        SEND RequestVote(node.term, node.id, node.lastLogIndex, node.lastLogTerm)
              TO peer
    END FOR

    IF votesReceived > clusterSize / 2 THEN
        SET node.state = "leader"
        CALL startHeartbeat(node)
    END IF
END FUNCTION

FUNCTION onReceiveRequestVote(node, candidateTerm, candidateId, lastLogIndex, lastLogTerm)
    IF candidateTerm < node.term THEN
        RETURN (node.term, false)    // reject — stale term
    END IF

    IF candidateTerm > node.term THEN
        SET node.term = candidateTerm
        SET node.state = "follower"
        SET node.votedFor = null
    END IF

    IF (node.votedFor == null OR node.votedFor == candidateId)
       AND candidateLog is at least as up-to-date as node.log THEN
        SET node.votedFor = candidateId
        RETURN (node.term, true)     // grant vote
    END IF

    RETURN (node.term, false)        // reject
END FUNCTION
```

### Consistent Hashing

```text
STRUCTURE ConsistentHash
    SET ring = sorted circular array of hash values
    SET nodeMap = hash -> physical node mapping
    SET virtualNodes = number of virtual nodes per physical node

FUNCTION addNode(node)
    FOR i = 0 TO virtualNodes - 1 DO
        SET hash = HASH(node.id + ":" + i)
        INSERT hash INTO ring (maintain sorted order)
        SET nodeMap[hash] = node
    END FOR
END FUNCTION

FUNCTION getNode(key)
    SET hash = HASH(key)
    SET position = first entry in ring >= hash     // binary search
    IF position == end of ring THEN
        SET position = ring[0]                     // wrap around
    END IF
    RETURN nodeMap[position]
END FUNCTION

// Why virtual nodes?
// Without them, adding/removing a node only affects adjacent node.
// Virtual nodes spread the load evenly across the ring.
```

---

## Code Examples

### LRU Cache (Pseudo Code → 3 Languages)

#### Pseudo Code

```text
STRUCTURE LRUCache
    SET capacity = maxSize
    SET map = hash map (key -> node)
    SET list = doubly linked list (most recent at head)

FUNCTION get(key)
    IF key NOT IN map THEN
        RETURN -1
    END IF
    SET node = map[key]
    CALL moveToHead(node)
    RETURN node.value
END FUNCTION

FUNCTION put(key, value)
    IF key IN map THEN
        SET node = map[key]
        SET node.value = value
        CALL moveToHead(node)
    ELSE
        IF size(map) >= capacity THEN
            SET tail = removeTail()
            DELETE map[tail.key]
        END IF
        SET newNode = createNode(key, value)
        CALL addToHead(newNode)
        SET map[key] = newNode
    END IF
END FUNCTION
```

#### Go

```go
type LRUCache struct {
    capacity int
    cache    map[int]*Node
    head     *Node
    tail     *Node
}

type Node struct {
    key, val   int
    prev, next *Node
}

func Constructor(capacity int) LRUCache {
    head := &Node{}
    tail := &Node{}
    head.next = tail
    tail.prev = head
    return LRUCache{capacity: capacity, cache: make(map[int]*Node), head: head, tail: tail}
}

func (c *LRUCache) Get(key int) int {
    if node, ok := c.cache[key]; ok {
        c.moveToHead(node)
        return node.val
    }
    return -1
}

func (c *LRUCache) Put(key, value int) {
    if node, ok := c.cache[key]; ok {
        node.val = value
        c.moveToHead(node)
    } else {
        if len(c.cache) >= c.capacity {
            tail := c.removeTail()
            delete(c.cache, tail.key)
        }
        node := &Node{key: key, val: value}
        c.addToHead(node)
        c.cache[key] = node
    }
}

func (c *LRUCache) addToHead(node *Node) {
    node.prev = c.head
    node.next = c.head.next
    c.head.next.prev = node
    c.head.next = node
}

func (c *LRUCache) removeNode(node *Node) {
    node.prev.next = node.next
    node.next.prev = node.prev
}

func (c *LRUCache) moveToHead(node *Node) {
    c.removeNode(node)
    c.addToHead(node)
}

func (c *LRUCache) removeTail() *Node {
    node := c.tail.prev
    c.removeNode(node)
    return node
}
```

#### Java

```java
import java.util.*;

class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> cache = new HashMap<>();
    private final Node head = new Node(0, 0);
    private final Node tail = new Node(0, 0);

    static class Node {
        int key, val;
        Node prev, next;
        Node(int k, int v) { key = k; val = v; }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) return -1;
        Node node = cache.get(key);
        moveToHead(node);
        return node.val;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.val = value;
            moveToHead(node);
        } else {
            if (cache.size() >= capacity) {
                Node t = tail.prev;
                removeNode(t);
                cache.remove(t.key);
            }
            Node node = new Node(key, value);
            addToHead(node);
            cache.put(key, node);
        }
    }

    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
}
```

#### Python

```python
class Node:
    def __init__(self, key=0, val=0):
        self.key = key
        self.val = val
        self.prev = None
        self.next = None

class LRUCache:
    def __init__(self, capacity):
        self.capacity = capacity
        self.cache = {}
        self.head = Node()
        self.tail = Node()
        self.head.next = self.tail
        self.tail.prev = self.head

    def get(self, key):
        if key not in self.cache:
            return -1
        node = self.cache[key]
        self._move_to_head(node)
        return node.val

    def put(self, key, value):
        if key in self.cache:
            node = self.cache[key]
            node.val = value
            self._move_to_head(node)
        else:
            if len(self.cache) >= self.capacity:
                tail = self.tail.prev
                self._remove(tail)
                del self.cache[tail.key]
            node = Node(key, value)
            self._add_to_head(node)
            self.cache[key] = node

    def _add_to_head(self, node):
        node.prev = self.head
        node.next = self.head.next
        self.head.next.prev = node
        self.head.next = node

    def _remove(self, node):
        node.prev.next = node.next
        node.next.prev = node.prev

    def _move_to_head(self, node):
        self._remove(node)
        self._add_to_head(node)
```

---

## Pseudo Code in Technical Documentation

### RFC-Style Pseudo Code

Used in protocol specifications, design docs, and architecture proposals:

```text
// RFC-style: MUST, SHOULD, MAY keywords

FUNCTION handleRequest(request)
    // Validate
    request MUST have valid authentication token
    request MUST have Content-Type header
    request body SHOULD NOT exceed MAX_BODY_SIZE

    // Process
    IF request.method == "GET" THEN
        RETURN CALL handleGet(request)
    ELSE IF request.method == "POST" THEN
        request body MUST be valid JSON
        RETURN CALL handlePost(request)
    ELSE
        RETURN 405 Method Not Allowed
    END IF
END FUNCTION
```

---

## Summary

At the senior level, pseudo code describes system-level algorithms: rate limiters, consensus protocols, consistent hashing, and LRU caches. It bridges the gap between architecture diagrams and implementation code. The skill is knowing the right level of abstraction — detailed enough to implement, abstract enough to discuss with non-engineers.
