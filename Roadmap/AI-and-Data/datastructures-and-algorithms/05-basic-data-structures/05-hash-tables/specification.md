# Hash Tables — Specification

> **Official / Authoritative Reference**
>
> Source: [Python 3 Standard Library — Mapping Types](https://docs.python.org/3/library/stdtypes.html#mapping-types-dict) — §Mapping Types — dict;
> [Java SE 21 — java.util.HashMap](https://docs.oracle.com/en/java/docs/api/java.base/java/util/HashMap.html);
> [Go Standard Library — builtin map](https://pkg.go.dev/builtin#make);
> [CLRS 4th ed. — Chapter 11: Hash Tables](https://mitpress.mit.edu/9780262046305/introduction-to-algorithms/);
> [Knuth, TAOCP Vol. 3 §6.4 — Hashing]

See [animation.html](./animation.html)

---

## Table of Contents

1. [Reference](#1-reference)
2. [Formal Definition / Grammar](#2-formal-definition--grammar)
3. [Core Rules & Constraints](#3-core-rules--constraints)
4. [Type / Category Rules](#4-type--category-rules)
5. [Behavioral Specification](#5-behavioral-specification)
6. [Defined vs Undefined Behavior](#6-defined-vs-undefined-behavior)
7. [Edge Cases](#7-edge-cases)
8. [Version / Evolution History](#8-version--evolution-history)
9. [Implementation Notes](#9-implementation-notes)
10. [Compliance Checklist](#10-compliance-checklist)
11. [Official Examples](#11-official-examples)
12. [Related Topics](#12-related-topics)

---

## 1. Reference

| Attribute       | Value                                                                                         |
|-----------------|-----------------------------------------------------------------------------------------------|
| Formal Name     | Hash Table / Hash Map / Dictionary / Associative Array                                        |
| NIST DADS       | https://xlinux.nist.gov/dads/HTML/hashtab.html                                                |
| Python Source   | https://docs.python.org/3/library/stdtypes.html#mapping-types-dict                           |
| Java Source     | https://docs.oracle.com/en/java/docs/api/java.base/java/util/HashMap.html                    |
| Go Source       | https://pkg.go.dev/builtin — `map` built-in type                                             |
| CLRS Reference  | Introduction to Algorithms, 4th ed., Ch. 11 — "Hash Tables"                                  |
| Knuth Reference | TAOCP Vol. 3, §6.4 — "Hashing"                                                               |
| Python Version  | 3.7+ (insertion-order guaranteed); 3.6+ (CPython implementation detail)                      |
| Java Version    | JDK 8+ (treeified buckets); JDK 21 LTS                                                       |
| Go Version      | 1.0+ (built-in map type)                                                                      |

**CLRS 4th ed. Ch. 11.0 — Definition:**
> "A hash table is an effective data structure for implementing dictionaries. Although searching for an element in a hash table can take as long as searching for an element in a linked list — Θ(n) time in the worst case — in practice, hashing performs extremely well. Under reasonable assumptions, the average time to search for an element in a hash table is O(1)."

**NIST DADS — Definition:**
> "A dictionary in which keys are mapped to array positions by a hash function. Having two keys map to the same position is called a collision. There are two major approaches to solve this problem: chaining and open addressing."

---

## 2. Formal Definition / Grammar

### 2.1 Abstract Data Type (ADT) — Hash Table

A **Hash Table** H implements the **Dictionary ADT** — a dynamic set S of key–value pairs with operations:

```
HashTable(H, m):
  H = array of m slots: H[0], H[1], ..., H[m-1]
  n = number of key–value pairs stored in H
  h : U → {0, 1, ..., m-1}   (hash function; U = universe of all possible keys)

Operations:
  INSERT(H, k, v)  — store value v at key k
  SEARCH(H, k)     — return value v associated with key k, or NIL
  DELETE(H, k)     — remove key k (and its value) from H
```

**Load factor** (CLRS §11.1):
```
α = n / m

Where:
  n = number of key–value pairs currently stored
  m = number of buckets (slots) in the hash table
```

The load factor α directly governs average-case performance:
- α < 0.75 → O(1) expected time for all operations
- α → 1 (open addressing) → performance degrades severely
- α > 1 (chaining only) → O(α) expected per operation

### 2.2 Hash Function — Formal Specification

**Division method** (CLRS §11.3.1):
```
h(k) = k mod m

Requirement: m should be a prime not close to a power of 2
```

**Multiplication method** (CLRS §11.3.2):
```
h(k) = ⌊m · (k · A mod 1)⌋

Where:
  A ∈ (0, 1)  (Knuth recommends A = (√5 - 1)/2 ≈ 0.6180339887)
  k · A mod 1 = fractional part of k · A
```

**Universal Hashing** (CLRS §11.3.3):
```
h_{a,b}(k) = ((a · k + b) mod p) mod m

Where:
  p = prime ≥ |U|
  a ∈ {1, 2, ..., p-1}
  b ∈ {0, 1, ..., p-1}
  (a, b chosen uniformly at random from a family H_{p,m})
```

### 2.3 Collision Resolution — ADT Specification

#### Chaining (CLRS §11.2)
```
Each slot H[j] is a linked list of all keys that hash to j.

INSERT(H, k, v):
  Prepend (k, v) to list at H[h(k)]                  — O(1)

SEARCH(H, k):
  Search list at H[h(k)] for key k                    — O(length of list)

DELETE(H, k):
  Remove k from list at H[h(k)]                       — O(1) with doubly-linked list
```

#### Open Addressing (CLRS §11.4)
```
All keys stored in the table itself; m ≥ n always.

Probe sequence: h(k, 0), h(k, 1), ..., h(k, m-1)
  Must be a permutation of {0, 1, ..., m-1}

INSERT(H, k, v):
  for i = 0 to m-1:
      j = h(k, i)
      if H[j] == EMPTY or H[j] == DELETED:
          H[j] = (k, v); return

SEARCH(H, k):
  for i = 0 to m-1:
      j = h(k, i)
      if H[j] == EMPTY: return NIL
      if H[j].key == k: return H[j].value

Linear probing:         h(k, i) = (h'(k) + i) mod m
Quadratic probing:      h(k, i) = (h'(k) + c₁·i + c₂·i²) mod m
Double hashing:         h(k, i) = (h₁(k) + i·h₂(k)) mod m
```

---

## 3. Core Rules & Constraints

### Rule 1: Hash Function Determinism and Uniformity

> *Docs: [CLRS §11.3](https://mitpress.mit.edu/9780262046305/) — "A good hash function satisfies (approximately) the assumption of simple uniform hashing: each key is equally likely to hash to any of the m slots, independently of where any other key has hashed to."*

A hash function **must** be:
1. **Deterministic** — `h(k)` always returns the same value for the same key k
2. **Uniform** — keys are spread evenly across all m slots (minimizes collisions)
3. **Fast to compute** — O(1) time (for fixed-size keys)

```go
// ✅ Correct — deterministic, uniform (Go's built-in map uses this internally)
package main

import (
    "fmt"
    "hash/fnv"
)

func hashString(s string, m int) int {
    h := fnv.New32a()
    h.Write([]byte(s))
    return int(h.Sum32()) % m
}

func main() {
    fmt.Println(hashString("apple", 16))  // Always the same value for "apple"
    fmt.Println(hashString("banana", 16)) // Different from "apple" (typically)
}
```

```java
// ✅ Correct — Java's Object.hashCode() contract
public class PhoneNumber {
    private final int areaCode, exchange, subscriber;

    @Override
    public int hashCode() {
        // Effective Java (Bloch, 3rd ed.) — Item 11: recipe for good hashCode()
        int result = Integer.hashCode(areaCode);
        result = 31 * result + Integer.hashCode(exchange);
        result = 31 * result + Integer.hashCode(subscriber);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PhoneNumber)) return false;
        PhoneNumber pn = (PhoneNumber) obj;
        return areaCode == pn.areaCode
            && exchange == pn.exchange
            && subscriber == pn.subscriber;
    }
}
```

```python
# ✅ Correct — Python hashCode contract (Python Data Model)
# From: https://docs.python.org/3/reference/datamodel.html#object.__hash__
class Point:
    def __init__(self, x: int, y: int):
        self.x = x
        self.y = y

    def __eq__(self, other) -> bool:
        return isinstance(other, Point) and self.x == other.x and self.y == other.y

    def __hash__(self) -> int:
        # Objects that compare equal must have the same hash value
        return hash((self.x, self.y))   # Delegate to tuple hash (built-in)

p1 = Point(1, 2)
p2 = Point(1, 2)
print(p1 == p2)        # True
print(hash(p1) == hash(p2))  # True — contract satisfied

# ❌ Incorrect — defines __eq__ without __hash__: Python sets __hash__ = None
class BadPoint:
    def __init__(self, x, y): self.x, self.y = x, y
    def __eq__(self, other): return self.x == other.x and self.y == other.y
    # Missing __hash__: BadPoint instances become unhashable!
    # TypeError: unhashable type: 'BadPoint'
```

### Rule 2: Equal Objects Must Have Equal Hash Codes

> *Docs: [Python Data Model](https://docs.python.org/3/reference/datamodel.html#object.__hash__) — "If objects that compare equal should have the same hash value. It is also advised that the hash value be a function of the object's size and content."*
> *Docs: [Java Object.hashCode() contract](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/Object.html#hashCode()) — "If two objects are equal according to the equals method, then calling the hashCode method on each of the two objects must produce the same integer result."*

**The Hash–Equals Contract (universal across all languages):**
```
a.equals(b) == true  ⟹  a.hashCode() == b.hashCode()
```

Note: the **converse is not required** — two objects can have the same hash code without being equal (this is a *collision*, not a violation).

```go
// Go enforces this implicitly: map keys must be comparable (==).
// For structs, equality is field-by-field.
package main

import "fmt"

type Point struct{ X, Y int }

func main() {
    m := map[Point]string{}
    m[Point{1, 2}] = "A"
    m[Point{1, 2}] = "B"   // Same key — overwrites

    fmt.Println(len(m))     // 1 — Go struct equality is value-based
    fmt.Println(m[Point{1, 2}])  // "B"
}
```

```java
// Java — HashMap uses equals() to resolve collisions
import java.util.HashMap;

HashMap<String, Integer> map = new HashMap<>();
map.put("hello", 1);

// "hello" and new String("hello") are different objects but equal strings
String key1 = "hello";
String key2 = new String("hello");
System.out.println(key1 == key2);         // false — different references
System.out.println(key1.equals(key2));    // true
System.out.println(map.get(key2));        // 1 — equals() used for lookup
```

```python
# Python — dict uses __eq__ to resolve collisions within same-hash buckets
d = {}
d[(1, 2)] = "tuple key"
print(d[(1, 2)])   # "tuple key" — tuples are hashable (immutable)

# Mutable objects (lists) are NOT hashable by default
try:
    d[[1, 2]] = "list key"   # TypeError: unhashable type: 'list'
except TypeError as e:
    print(e)
```

### Rule 3: Load Factor Must Be Managed via Rehashing

> *Docs: [Java HashMap Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/HashMap.html) — "The default load factor (.75) offers a good tradeoff between time and space costs. Higher values decrease the space overhead but increase the lookup cost."*

When α ≥ threshold, the table must **rehash** (resize):
1. Allocate new array of size `2m` (or next prime ≥ 2m)
2. Re-insert every existing key–value pair using the new hash function
3. Time: O(n) — amortized O(1) per insertion

```go
// Go runtime automatically rehashes maps (no user control needed)
// Threshold: ~6.5 entries per bucket on average
package main

import "fmt"

func main() {
    m := make(map[string]int, 8)  // Initial capacity hint (optional)
    for i := 0; i < 100; i++ {
        m[fmt.Sprintf("key%d", i)] = i  // Go runtime rehashes automatically
    }
    fmt.Println(len(m))  // 100
}
```

```java
// Java — explicit control over initial capacity and load factor
import java.util.HashMap;

// Default: capacity=16, loadFactor=0.75 → rehash at n=12
HashMap<String, Integer> defaultMap = new HashMap<>();

// Custom: capacity=32, loadFactor=0.5 → rehash at n=16
// Use when you know approximate size to avoid rehashing
HashMap<String, Integer> tuned = new HashMap<>(32, 0.5f);

// Best practice when size is known upfront (Effective Java, Item 75):
int expectedSize = 1000;
HashMap<String, Integer> presized = new HashMap<>((int)(expectedSize / 0.75) + 1);
```

```python
# Python dict resizes when n > (2/3) * m  (load factor threshold: ~0.67)
# Python 3.6+ CPython: compact dict — uses indices array + entries array
# No user control over load factor; resizing is fully automatic

import sys

d = {}
sizes = []
for i in range(20):
    d[i] = i
    sizes.append(sys.getsizeof(d))

# Observe memory jumps at resize thresholds:
# 0-5 keys: 232 bytes; 6+ keys: 360 bytes; etc. (CPython 3.11)
print(sizes)
```

---

## 4. Type / Category Rules

### 4.1 Hash Table Taxonomy

```
Hash Table Variants
├── Chaining (Separate Chaining)
│   ├── Linked-list chaining — each bucket is a singly/doubly-linked list
│   ├── Tree chaining — Java 8+ HashMap: convert to red-black tree when bucket ≥ 8
│   └── Dynamic array chaining — bucket is a resizable array
│
├── Open Addressing
│   ├── Linear probing          — h(k, i) = (h'(k) + i) mod m
│   │   └── Suffers from primary clustering
│   ├── Quadratic probing       — h(k, i) = (h'(k) + c₁·i + c₂·i²) mod m
│   │   └── Suffers from secondary clustering
│   └── Double hashing          — h(k, i) = (h₁(k) + i·h₂(k)) mod m
│       └── Best distribution; no clustering
│
└── Specialized Variants
    ├── Cuckoo hashing          — two hash functions; O(1) worst-case lookup
    ├── Robin Hood hashing      — minimizes probe variance (used by Rust HashMap)
    ├── Swiss Table (absl)      — SIMD-accelerated probing (used by Python 3.6+ CPython)
    └── Perfect hashing         — O(1) worst-case; static key sets only
```

### 4.2 Language Standard Library Implementations

| Implementation       | Language | Strategy              | Default Load Factor | Ordered? | Thread-Safe? |
|---------------------|----------|-----------------------|--------------------:|----------|-------------|
| `dict`              | Python 3 | Open addressing (compact table, pseudo-random probing) | ~0.67 | Yes (3.7+) | No |
| `HashMap`           | Java     | Chaining + tree bins  | 0.75                | No       | No          |
| `LinkedHashMap`     | Java     | Chaining + doubly-linked list | 0.75       | Yes (insert) | No |
| `TreeMap`           | Java     | Red-black tree (not hash) | N/A              | Yes (sorted) | No |
| `ConcurrentHashMap` | Java     | Chaining (segmented) | 0.75               | No       | Yes         |
| `Hashtable`         | Java     | Chaining (legacy)    | 0.75                | No       | Yes (synchronized) |
| `map[K]V`           | Go       | Chaining (buckets of 8) | ~6.5 items/bucket | No      | No          |
| `sync.Map`          | Go       | Two-map structure    | N/A                 | No       | Yes         |
| `collections.OrderedDict` | Python | dict + doubly-linked list | ~0.67    | Yes      | No          |
| `collections.defaultdict` | Python | dict + default_factory | ~0.67         | Yes (3.7+) | No       |
| `collections.Counter` | Python | dict subclass        | ~0.67              | No       | No          |

### 4.3 Complexity Reference

| Operation      | Average Case | Worst Case | Notes                                          |
|---------------|:------------:|:----------:|------------------------------------------------|
| `INSERT(k, v)`| O(1)         | O(n)       | Worst case: all keys collide to same bucket    |
| `SEARCH(k)`   | O(1)         | O(n)       | Worst case: linear scan of one long chain      |
| `DELETE(k)`   | O(1)         | O(n)       | With doubly-linked chaining: O(1) amortized    |
| `REHASH`      | O(n)         | O(n)       | Amortized O(1) per insertion                   |
| `ITERATE`     | O(n + m)     | O(n + m)   | Must scan all m slots including empty ones     |

---

## 5. Behavioral Specification

### 5.1 Insertion — Formal Semantics

**Chaining INSERT** (CLRS §11.2):
```
CHAINED-HASH-INSERT(T, x)
// Precondition: x.key is not already in T (otherwise update semantics)
1  insert x at the head of list T[h(x.key)]
// Time: O(1)
```

**Open-Addressing INSERT** (CLRS §11.4):
```
HASH-INSERT(T, k)
// Precondition: Table is not full (n < m)
1  i = 0
2  repeat
3      j = h(k, i)
4      if T[j] == NIL or T[j] == DELETED
5          T[j] = k
6          return j
7      else i = i + 1
8  until i == m
9  error "hash table overflow"
// Expected probes: 1/(1-α) when α < 1 (CLRS Theorem 11.8)
```

### 5.2 Search — Formal Semantics

```
HASH-SEARCH(T, k)
1  i = 0
2  repeat
3      j = h(k, i)
4      if T[j].key == k
5          return T[j].value
6      i = i + 1
7  until T[j] == NIL or i == m
8  return NIL
// Search terminates at first NIL slot (not DELETED)
// This is why DELETED markers (tombstones) are needed for correctness
```

### 5.3 Deletion — The Tombstone Problem

**Critical behavioral constraint** (CLRS §11.4):

When using open addressing, a key cannot simply be set to NIL on deletion. This would break subsequent SEARCH operations for keys that were inserted after the deleted key (their probe sequence passed through this slot).

**Solution**: Mark deleted slots with a special `DELETED` (tombstone) sentinel:
- `SEARCH` continues probing past `DELETED` slots (cannot stop)
- `INSERT` treats `DELETED` slots as empty (can reuse)

```go
package main

import "fmt"

const (
    EMPTY   = 0
    DELETED = 1
    OCCUPIED = 2
)

type entry struct {
    state int
    key   int
    value string
}

type OpenHashTable struct {
    buckets []entry
    m       int
    n       int
}

func NewOpenHashTable(m int) *OpenHashTable {
    return &OpenHashTable{
        buckets: make([]entry, m),
        m:       m,
    }
}

func (t *OpenHashTable) hash(k, i int) int {
    return (k + i) % t.m   // Linear probing
}

func (t *OpenHashTable) Insert(k int, v string) {
    for i := 0; i < t.m; i++ {
        j := t.hash(k, i)
        if t.buckets[j].state == EMPTY || t.buckets[j].state == DELETED {
            t.buckets[j] = entry{OCCUPIED, k, v}
            t.n++
            return
        }
    }
    panic("hash table overflow")
}

func (t *OpenHashTable) Search(k int) (string, bool) {
    for i := 0; i < t.m; i++ {
        j := t.hash(k, i)
        if t.buckets[j].state == EMPTY {
            return "", false   // NOT found — stop here
        }
        if t.buckets[j].state == OCCUPIED && t.buckets[j].key == k {
            return t.buckets[j].value, true
        }
        // DELETED: keep probing — critical correctness requirement
    }
    return "", false
}

func (t *OpenHashTable) Delete(k int) {
    for i := 0; i < t.m; i++ {
        j := t.hash(k, i)
        if t.buckets[j].state == EMPTY {
            return
        }
        if t.buckets[j].state == OCCUPIED && t.buckets[j].key == k {
            t.buckets[j].state = DELETED   // Tombstone — do NOT set to EMPTY
            t.n--
            return
        }
    }
}

func main() {
    ht := NewOpenHashTable(11)
    ht.Insert(5, "five")
    ht.Insert(16, "sixteen")  // h(16,0) = 16%11 = 5 → collision → probes slot 6
    ht.Delete(5)               // Sets slot 5 to DELETED (tombstone)
    v, ok := ht.Search(16)    // Must probe past DELETED slot 5 → finds slot 6
    fmt.Println(v, ok)        // "sixteen" true
}
```

```java
import java.util.*;

public class HashTableDemo {
    public static void main(String[] args) {
        // Java HashMap — chaining, no tombstone issue
        HashMap<Integer, String> map = new HashMap<>();
        map.put(5, "five");
        map.put(16, "sixteen");  // Different bucket (16 % 16 = 0, 5 % 16 = 5)
        map.remove(5);           // Safe removal: just detach from chain
        System.out.println(map.get(16));   // "sixteen" — unaffected by deletion

        // LinkedHashMap — preserves insertion order
        LinkedHashMap<String, Integer> ordered = new LinkedHashMap<>();
        ordered.put("banana", 2);
        ordered.put("apple", 1);
        ordered.put("cherry", 3);
        System.out.println(ordered.keySet());  // [banana, apple, cherry]

        // TreeMap — sorted by key (not a hash table — uses red-black tree)
        TreeMap<String, Integer> sorted = new TreeMap<>(ordered);
        System.out.println(sorted.keySet());   // [apple, banana, cherry]
    }
}
```

```python
# Python dict — open addressing with pseudo-random probing
# CPython source: Objects/dictobject.c
# Probe sequence: j = ((j * 5) + perturb + 1) % m; perturb >>= 5

d = {}

# Python dict.update() semantics — value is overwritten on duplicate key
d["a"] = 1
d["a"] = 2    # Update: same key → value overwritten (no duplicate keys)
print(d)      # {'a': 2}

# setdefault — insert only if key absent
d.setdefault("b", 10)   # Inserts 'b': 10
d.setdefault("a", 99)   # 'a' already exists → no change
print(d)                 # {'a': 2, 'b': 10}

# get with default — no insertion
print(d.get("c", 0))    # 0 — 'c' absent; dict unchanged

# dict.pop() — safe deletion
val = d.pop("b", None)   # Returns 10; no KeyError if absent
print(val)               # 10
```

### 5.4 Python dict — Compact Hash Table (CPython 3.6+)

From CPython implementation (`Objects/dictobject.c`), Python 3.6+ uses a **compact dict** layout:

```
indices: [int8/int16/int32/int64 array, size m]   ← sparse index array
entries: [(hash, key, value), ...]               ← dense entries array

SEARCH:
  i = hash(key) % m
  probe through indices[] until indices[i] == empty or entries[indices[i]].key == key
```

This layout:
- Reduces memory: indices are small integers; entries are packed densely
- Preserves insertion order (entries array maintains insertion order)
- Enables O(1) iteration (iterate entries array, skip none)

---

## 6. Defined vs Undefined Behavior

| Situation                                        | Status              | Notes                                                              |
|--------------------------------------------------|---------------------|--------------------------------------------------------------------|
| SEARCH for key that was never inserted            | Defined             | Returns NIL / `None` / raises KeyError (depends on API)           |
| INSERT with duplicate key                         | Defined             | Value is overwritten; key count unchanged                          |
| DELETE a key that does not exist                  | Defined             | No-op (Go, Python `dict.pop(k,None)`); KeyError (Python `del d[k]`)|
| Mutating a key after insertion (mutable key)      | **Undefined**       | Hash value changes → key becomes permanently unfindable           |
| Modifying dict while iterating over it (Python)   | **Runtime Error**   | `RuntimeError: dictionary changed size during iteration`           |
| Modifying map while ranging over it (Go)          | Defined*            | Deletions are safe; new keys may or may not be visited             |
| Concurrent access to HashMap without sync (Java)  | **Undefined**       | ConcurrentModificationException or data corruption                 |
| Load factor at 1.0 (open addressing)              | **Overflow**        | INSERT fails; all slots occupied (cannot exceed m)                 |
| Hash function returns negative value              | Impl-defined        | Java: `Math.abs(hashCode()) % capacity` (with special case for MIN_VALUE) |
| NaN key in Python/Java                            | Defined*            | Python: `float('nan')` is hashable; only finds itself (`nan != nan`) |
| `None` / `null` key                               | Defined             | Python dict: valid key; Java HashMap: one `null` key allowed       |

---

## 7. Edge Cases

### 7.1 Mutable Key Mutation — Silent Data Loss

```go
// Go: maps require comparable (non-mutable reference) keys
// Slices are NOT comparable — compilation error
package main

func main() {
    // m := map[[]int]string{}  // compile error: invalid map key type []int
    // Use arrays (value type) instead:
    m := map[[3]int]string{}
    m[[3]int{1, 2, 3}] = "ok"
    fmt.Println(m[[3]int{1, 2, 3}])   // "ok"
}
```

```java
import java.util.*;

// Java: mutable object used as HashMap key — silent data loss
List<Integer> key = new ArrayList<>(Arrays.asList(1, 2, 3));
Map<List<Integer>, String> map = new HashMap<>();
map.put(key, "value");
System.out.println(map.get(key));   // "value"

key.add(4);   // Mutate the key AFTER insertion
// key's hashCode() has changed — it now hashes to a different bucket
System.out.println(map.get(key));   // null — key is lost!
System.out.println(map.size());     // 1 — entry exists but is unreachable
```

```python
# Python: attempts to use mutable object as key — TypeError at insert time
d = {}
key = [1, 2, 3]
try:
    d[key] = "value"
except TypeError as e:
    print(e)   # unhashable type: 'list'

# Safe: use tuple (immutable) as key
d[(1, 2, 3)] = "value"
print(d[(1, 2, 3)])   # "value"
```

### 7.2 NaN as a Key

```python
# Python — NaN (Not a Number) has a hash value but does NOT equal itself
import math

nan = float('nan')
d = {nan: "not a number"}

print(hash(nan))           # Some integer (e.g., 0)
print(nan == nan)          # False — IEEE 754 specification
print(d[nan])              # "not a number" — identity check (is) used as fallback
print(d[float('nan')])     # KeyError — different NaN object; hash same, but not 'is'

# Explanation: Python dict checks: hash(k1) == hash(k2) AND (k1 is k2 OR k1 == k2)
# NaN: hash is same (0), but nan is not nan (different objects), AND nan != nan
# Result: each NaN object is a distinct key
d2 = {float('nan'): i for i in range(5)}
print(len(d2))   # 5 — five distinct NaN keys!
```

### 7.3 Collision Attack / Hash DoS

**Algorithmic Complexity Attack** (CVE-2011-4885, CVE-2012-0830, etc.):

An attacker who can predict the hash function can craft inputs that all map to the same bucket, forcing O(n²) behavior on a supposedly O(1) hash table.

**Mitigations:**
- **Python 3.3+**: `PYTHONHASHSEED` — random seed added to hash at interpreter startup (PEP 456)
- **Java 8+**: HashMap uses red-black tree for buckets with ≥ 8 entries → O(log n) worst case per bucket
- **Go 1.0+**: Random seed per map instance (`runtime.maptype` uses randomized hash seed)

```python
# Python — hash randomization per process
import os, sys

# Each Python process gets a different random seed
# So hash("hello") gives different results across processes
print(hash("hello"))   # e.g., 8466676879265504940 (varies per run)

# To disable (NEVER in production — only for testing):
# PYTHONHASHSEED=0 python script.py

# Verify hash randomization is active:
print(sys.flags.hash_randomization)   # 1
```

### 7.4 Concurrent Access

```go
// Go: sync.Map for concurrent access (read-heavy workloads)
package main

import (
    "sync"
    "fmt"
)

func main() {
    var m sync.Map

    // Thread-safe operations
    m.Store("key1", 100)
    m.Store("key2", 200)

    val, ok := m.Load("key1")
    fmt.Println(val, ok)   // 100 true

    // LoadOrStore — atomic check-and-set
    actual, loaded := m.LoadOrStore("key1", 999)
    fmt.Println(actual, loaded)   // 100 true (already existed)

    m.Range(func(k, v interface{}) bool {
        fmt.Println(k, v)
        return true   // continue iteration
    })
}
```

```java
import java.util.concurrent.*;

// Java — ConcurrentHashMap for concurrent access
ConcurrentHashMap<String, Integer> concMap = new ConcurrentHashMap<>();
concMap.put("a", 1);
concMap.put("b", 2);

// Atomic compute operations (Java 8+)
concMap.compute("a", (k, v) -> v == null ? 1 : v + 1);   // Atomic increment
concMap.merge("b", 10, Integer::sum);                     // Atomic merge
System.out.println(concMap);   // {a=2, b=12}
```

```python
# Python — threading.Lock for concurrent dict access
import threading

shared_dict = {}
lock = threading.Lock()

def safe_increment(key: str) -> None:
    with lock:
        shared_dict[key] = shared_dict.get(key, 0) + 1

threads = [threading.Thread(target=safe_increment, args=("counter",))
           for _ in range(100)]
for t in threads: t.start()
for t in threads: t.join()

print(shared_dict["counter"])   # 100 — always correct with lock
```

### 7.5 Integer Key Overflow (Java)

```java
// Java: HashMap uses hashCode() & (capacity-1) for bucket index
// Integer.MIN_VALUE edge case: Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE (overflow)
System.out.println(Math.abs(Integer.MIN_VALUE));   // -2147483648 — still negative!

// Java's HashMap handles this correctly internally via:
// (h = key.hashCode()) ^ (h >>> 16)  — spread high bits into low bits
// Then: index = hash & (capacity - 1) — always non-negative for capacity = 2^k
```

---

## 8. Version / Evolution History

| Year | Version / Event                       | Change / Significance                                                                  |
|------|---------------------------------------|----------------------------------------------------------------------------------------|
| 1953 | H. P. Luhn (IBM)                      | First known proposal of hash tables (internal memo)                                    |
| 1956 | A. D. Dumey                           | First published description of hashing ("Computers and Automation")                    |
| 1968 | Knuth — TAOCP Vol. 3 §6.4            | Definitive mathematical treatment of hashing; open addressing, chaining, load factor   |
| 1990 | Carter & Wegman (1979) → universal ℋ | Universal hashing proven to give O(1) expected time regardless of input                |
| 1994 | Python 1.5                            | `dict` type using open addressing (uniform probing)                                    |
| 1995 | Java 1.0                              | `Hashtable` — synchronized, legacy; `HashMap` added in Java 1.2 (1998)                |
| 1998 | Java 1.2 Collections Framework        | `HashMap`, `LinkedHashMap`, `TreeMap`, `HashSet` standardized                         |
| 2001 | Python 2.2                            | `dict` compact representation; internal hash table improvements                         |
| 2003 | Python 2.3                            | `dict` growth policy changed to maintain load factor ≤ 2/3                             |
| 2008 | Go 1.0 (released 2009)               | `map` built-in with randomized hash seed; chaining with 8-element buckets              |
| 2011 | CVE-2011-4885                         | Hash DoS attack on PHP; exposed algorithmic complexity vulnerability in all languages   |
| 2012 | Python 3.3 (PEP 456)                 | `PYTHONHASHSEED` random seed by default; hash randomization for all str/bytes/datetime |
| 2012 | Java 7u6                              | String hash randomization option added (`-XX:+UseStringDeduplication`)                  |
| 2014 | Java 8                                | HashMap treeifies buckets with ≥ 8 entries (red-black tree) → O(log n) worst case     |
| 2016 | Python 3.6 (CPython)                 | Compact dict: indices + dense entries array; dict preserves insertion order (impl detail)|
| 2017 | Python 3.7                            | Insertion-order preservation made part of **language specification** (not just CPython) |
| 2018 | Rust std::collections::HashMap       | Adopts Robin Hood hashing + SIMD (Swiss Table) for better cache performance             |
| 2021 | Python 3.10                           | `dict` `|` and `|=` operators for merging (PEP 584)                                   |
| 2021 | Java 17 LTS                           | No HashMap API changes; record types make natural immutable keys                       |
| 2023 | Go 1.21                               | `maps` package added: `maps.Copy`, `maps.Delete`, `maps.Equal`, `maps.Keys`            |
| 2024 | Python 3.13                           | dict internals refactored for free-threaded (no-GIL) builds (PEP 703)                 |

---

## 9. Implementation Notes

### 9.1 Python `dict` Internal Layout (CPython 3.6+)

```
Compact Hash Table (Objects/dictobject.c):

  dk_indices:  [int8 | int16 | int32 | int64] array of size m
               Stores index into dk_entries, or EMPTY (-1) or DUMMY (-2)

  dk_entries:  [(hash_value, key, value)] dense array
               Entries appended in insertion order

  Probe sequence (for key k with hash h):
    slot = h % m
    while True:
        i = dk_indices[slot]
        if i == EMPTY: return not_found
        if dk_entries[i].hash == h and dk_entries[i].key == k: return found
        slot = ((5 * slot) + perturb + 1) % m   # pseudo-random probe
        perturb >>= PERTURB_SHIFT                # perturb = 5; shifts away
```

### 9.2 Java `HashMap` Internal Layout (JDK 8+)

```java
// Simplified internal structure (java.util.HashMap source):
transient Node<K,V>[] table;       // Array of buckets; size always power of 2
transient int size;                 // Number of key-value mappings
int threshold;                      // size at which to rehash = capacity * loadFactor
final float loadFactor;             // Default: 0.75f

// Node (linked list node):
static class Node<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;    // Chain to next node in same bucket
}

// TreeNode (red-black tree node, when bucket has ≥ TREEIFY_THRESHOLD = 8 entries):
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent, left, right, prev;
    boolean red;
}

// Bucket index calculation:
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    // XOR high bits into low bits for better distribution
}
int bucketIndex = hash & (capacity - 1);   // capacity always 2^k → bitwise AND
```

### 9.3 Go `map` Internal Layout

Go maps use a structure defined in `runtime/map.go`:

```
hmap:
  count     int          — number of live cells
  flags     uint8        — concurrent access flags (detected at runtime)
  B         uint8        — log₂ of number of buckets (capacity = 2^B)
  noverflow uint16       — approximate number of overflow buckets
  hash0     uint32       — hash seed (randomized at map creation)
  buckets   unsafe.Pointer — array of 2^B bmap structs
  oldbuckets unsafe.Pointer — previous bucket array (during incremental rehashing)
  nevacuate uintptr      — progress counter for rehashing

bmap (bucket):
  tophash   [8]uint8     — top 8 bits of hash for each key (fast comparison)
  keys      [8]K         — 8 key slots
  values    [8]V         — 8 value slots
  overflow  *bmap        — pointer to overflow bucket (if > 8 entries)
```

Key insight: Go maps use **incremental rehashing** — old buckets are evacuated
2 at a time during INSERT/DELETE, avoiding the O(n) pause of a full rehash.

### 9.4 Complexity Summary

| Operation       | Python `dict` | Java `HashMap` | Go `map`    | Notes                                  |
|----------------|:-------------:|:--------------:|:-----------:|----------------------------------------|
| `d[k] = v`     | O(1) avg      | O(1) avg       | O(1) avg    | O(n) worst case (all keys collide)     |
| `d[k]`         | O(1) avg      | O(1) avg       | O(1) avg    | Java: O(log n) worst (treeified bucket)|
| `del d[k]`     | O(1) avg      | O(1) avg       | O(1) avg    | Go: incremental rehash amortizes cost  |
| `k in d`       | O(1) avg      | O(1) avg       | O(1) avg    |                                        |
| `len(d)`       | O(1)          | O(1)           | O(1)        | Stored as field; not computed          |
| `for k in d`   | O(n)          | O(n + m)       | O(n + m)    | Python: iterates dense entries array   |
| Copy / merge   | O(n)          | O(n)           | O(n)        | `dict(d)`, `HashMap(m)`, `maps.Copy`  |
| Space          | O(n)          | O(n)           | O(n)        | m ≥ n always; constant factor varies  |

---

## 10. Compliance Checklist

- [ ] Key type is **immutable** (hashable): strings, integers, tuples (Python); value types or immutable objects (Java); comparable types (Go)
- [ ] `__hash__` and `__eq__` (Python) / `hashCode()` and `equals()` (Java) are both overridden together — **never one without the other**
- [ ] Equal objects have equal hash codes (hash–equals contract)
- [ ] Hash function is **deterministic** — same key always yields same hash
- [ ] Load factor is monitored; initial capacity pre-set when insertion count is known
- [ ] Concurrent access uses `sync.Map` (Go) / `ConcurrentHashMap` (Java) / explicit lock (Python)
- [ ] Dictionary is **not mutated during iteration** (use `list(d.keys())` snapshot in Python)
- [ ] `NaN` keys are **never used** (each NaN instance is a distinct key in Python)
- [ ] Mutable objects are **never used as keys** (Java `ArrayList`, Python `list`, Go `slice`)
- [ ] `get(k, default)` is used instead of `d[k]` when key absence is expected (Python)
- [ ] `computeIfAbsent` / `merge` used for atomic compound operations (Java)
- [ ] Hash DoS mitigation is confirmed: Python hash randomization enabled (`sys.flags.hash_randomization == 1`)
- [ ] `None` / `null` keys are intentional — document if used as a sentinel
- [ ] For ordered iteration, use `dict` (Python 3.7+) or `LinkedHashMap` (Java) — do **not** rely on `HashMap` order

---

## 11. Official Examples

### 11.1 Go — Word Frequency Counter (Official Go Tour Pattern)

> Source: [A Tour of Go — Maps](https://go.dev/tour/moretypes/19)

```go
package main

import (
    "fmt"
    "strings"
)

// WordCount returns a map of word → frequency in the string s.
// Official Go Tour exercise: https://go.dev/tour/moretypes/23
func WordCount(s string) map[string]int {
    counts := make(map[string]int)
    for _, word := range strings.Fields(s) {
        counts[word]++   // Zero-value for int is 0 — no initialization needed
    }
    return counts
}

// Two-value assignment for safe lookup (comma-ok idiom)
// From: https://go.dev/doc/effective_go#maps
func main() {
    counts := WordCount("the quick brown fox jumps over the lazy dog the fox")
    fmt.Println(counts)
    // map[brown:1 dog:1 fox:2 jumps:1 lazy:1 over:1 quick:1 the:3]

    // Comma-ok idiom — detect missing key vs zero value
    val, ok := counts["fox"]
    fmt.Println(val, ok)   // 2 true

    val, ok = counts["cat"]
    fmt.Println(val, ok)   // 0 false — "cat" absent; val is zero-value

    // Safe delete — no error if key absent
    delete(counts, "the")
    fmt.Println(len(counts))   // 7

    // Iterating over all entries (order not guaranteed in Go)
    for word, count := range counts {
        fmt.Printf("%s: %d\n", word, count)
    }
}
```

**Expected output:**
```
map[brown:1 dog:1 fox:2 jumps:1 lazy:1 over:1 quick:1 the:3]
2 true
0 false
7
```

### 11.2 Java — Grouping with `computeIfAbsent` (Official Pattern)

> Source: [Java Docs — HashMap.computeIfAbsent](https://docs.oracle.com/en/java/docs/api/java.base/java/util/HashMap.html#computeIfAbsent(K,java.util.function.Function))

```java
import java.util.*;
import java.util.stream.*;

public class GroupingExample {
    /**
     * Groups words by their first character.
     * Demonstrates HashMap.computeIfAbsent — the recommended pattern
     * for building a map-of-lists from Java Javadoc.
     */
    public static Map<Character, List<String>> groupByFirstChar(List<String> words) {
        Map<Character, List<String>> groups = new HashMap<>();
        for (String word : words) {
            char key = word.charAt(0);
            // computeIfAbsent: atomically creates empty list if key absent
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        return groups;
    }

    /**
     * Merge two maps: sum values for common keys.
     * Demonstrates Map.merge — Java 8+ official recommendation.
     */
    public static Map<String, Integer> mergeCounts(
            Map<String, Integer> m1, Map<String, Integer> m2) {
        Map<String, Integer> result = new HashMap<>(m1);
        m2.forEach((k, v) -> result.merge(k, v, Integer::sum));
        return result;
    }

    public static void main(String[] args) {
        List<String> words = Arrays.asList(
            "apple", "avocado", "banana", "blueberry", "cherry", "apricot"
        );

        Map<Character, List<String>> grouped = groupByFirstChar(words);
        System.out.println(grouped);
        // {a=[apple, avocado, apricot], b=[banana, blueberry], c=[cherry]}

        // Using Collectors.groupingBy (Stream API alternative)
        Map<Character, List<String>> streamed = words.stream()
            .collect(Collectors.groupingBy(w -> w.charAt(0)));
        System.out.println(streamed);  // Same result

        // Merge two frequency maps
        Map<String, Integer> freq1 = Map.of("a", 3, "b", 2);
        Map<String, Integer> freq2 = Map.of("b", 5, "c", 1);
        System.out.println(mergeCounts(freq1, freq2));   // {a=3, b=7, c=1}
    }
}
```

**Expected output:**
```
{a=[apple, avocado, apricot], b=[banana, blueberry], c=[cherry]}
{a=[apple, avocado, apricot], b=[banana, blueberry], c=[cherry]}
{a=3, b=7, c=1}
```

### 11.3 Python — Dict Comprehension and `collections` (Official Patterns)

> Source: [Python Docs — dict](https://docs.python.org/3/library/stdtypes.html#dict);
> [Python Docs — collections.defaultdict](https://docs.python.org/3/library/collections.html#collections.defaultdict);
> [Python Docs — collections.Counter](https://docs.python.org/3/library/collections.html#collections.Counter)

```python
from collections import defaultdict, Counter, OrderedDict

# --- Dict comprehension (Python 3) ---
squares = {x: x**2 for x in range(1, 6)}
print(squares)   # {1: 1, 2: 4, 3: 9, 4: 16, 5: 25}

# Invert a dict (assumes values are unique)
inv = {v: k for k, v in squares.items()}
print(inv)       # {1: 1, 4: 2, 9: 3, 16: 4, 25: 5}

# --- defaultdict — auto-initialize missing keys ---
# From: https://docs.python.org/3/library/collections.html#collections.defaultdict
graph = defaultdict(list)   # Missing key → empty list automatically
graph["A"].append("B")
graph["A"].append("C")
graph["B"].append("D")
print(dict(graph))   # {'A': ['B', 'C'], 'B': ['D']}

# --- Counter — multiset / frequency table ---
# From: https://docs.python.org/3/library/collections.html#collections.Counter
text = "the quick brown fox jumps over the lazy dog"
freq = Counter(text.split())
print(freq.most_common(3))   # [('the', 2), ('quick', 1), ('brown', 1)]

# Counter arithmetic (official feature)
c1 = Counter(a=4, b=2, c=0)
c2 = Counter(a=1, b=2, c=3)
print(c1 + c2)   # Counter({'a': 5, 'b': 4, 'c': 3})
print(c1 - c2)   # Counter({'a': 3})  — zero and negative counts removed

# --- dict merge operators (Python 3.9+ PEP 584) ---
defaults = {"color": "red", "size": 10}
overrides = {"color": "blue", "weight": 5.0}
merged = defaults | overrides   # New dict; overrides wins on conflict
print(merged)   # {'color': 'blue', 'size': 10, 'weight': 5.0}

defaults |= overrides   # In-place merge
print(defaults)         # {'color': 'blue', 'size': 10, 'weight': 5.0}
```

**Expected output:**
```
{1: 1, 2: 4, 3: 9, 4: 16, 5: 25}
{1: 1, 4: 2, 9: 3, 16: 4, 25: 5}
{'A': ['B', 'C'], 'B': ['D']}
[('the', 2), ('quick', 1), ('brown', 1)]
Counter({'a': 5, 'b': 4, 'c': 3})
Counter({'a': 3})
{'color': 'blue', 'size': 10, 'weight': 5.0}
{'color': 'blue', 'size': 10, 'weight': 5.0}
```

### 11.4 CLRS-Style Pseudocode — Chained Hash Table

```
// CLRS 4th ed. Ch. 11.2 — Hash Tables with Chaining

CHAINED-HASH-INSERT(T, x)
// Precondition: T is a hash table; x.key is not already present
// Postcondition: x is stored in T; T[h(x.key)] chain is one longer
1  insert x at the head of list T[h(x.key)]
// Time: O(1)

CHAINED-HASH-SEARCH(T, k)
// Returns element x with x.key == k, or NIL
1  search for an element with key k in list T[h(k)]
// Time: O(length of T[h(k)]) = O(1 + α) expected (CLRS Theorem 11.2)

CHAINED-HASH-DELETE(T, x)
// Delete element x from its chain
// Precondition: x is a pointer to the element (not just the key)
1  delete x from list T[h(x.key)]
// Time: O(1) with doubly-linked list; O(n/m) with singly-linked list

// Theorem 11.2 (CLRS §11.2):
// In a hash table with chaining and n keys in m slots,
// with simple uniform hashing:
//   Unsuccessful search: Θ(1 + α)
//   Successful search:   Θ(1 + α)
// If n = O(m), then all operations run in O(1) expected time.
```

---

## 12. Related Topics

| Topic                     | Relationship                                                              | Location / Reference                                                |
|---------------------------|---------------------------------------------------------------------------|---------------------------------------------------------------------|
| Arrays                    | Hash table's internal bucket storage is an array; O(1) random access     | `../01-array/`                                                      |
| Linked Lists              | Chaining collision resolution uses linked lists per bucket               | `../02-linked-lists/`                                               |
| Trees (Red-Black)         | Java 8+ HashMap treeifies buckets ≥ 8 entries for O(log n) worst case   | CLRS §13 — Red-Black Trees                                          |
| Stacks / Queues           | Implemented efficiently using hash tables for O(1) membership tests      | `../04-stacks/`, `../03-queues/`                                    |
| Sets                      | `set` (Python), `HashSet` (Java), `map[K]struct{}` (Go) = hash table without values | Python `set`, `java.util.HashSet`              |
| Sorting                   | Hash tables support O(n) frequency-based counting sort                   | CLRS §8 — Counting Sort                                             |
| Graphs                    | Adjacency maps use `dict[node, list[node]]` for O(1) neighbor lookup     | CLRS §22 — Graph Representations                                    |
| Caching / Memoization     | LRU cache implemented as dict + doubly-linked list (`functools.lru_cache`) | `functools.lru_cache`, `collections.OrderedDict`                   |
| Cryptographic Hash        | NOT the same as hash table hash: cryptographic hashes are one-way, collision-resistant | `hashlib` (Python), `java.security.MessageDigest`  |
| String Hashing            | Rolling hash (Rabin-Karp) for substring search; Bloom filters for membership | CLRS §32.2 — Rabin-Karp Algorithm                                 |
| Universal Hashing         | Theoretical guarantee of O(1) expected time regardless of input distribution | CLRS §11.3.3 — Universal Hashing                                 |
