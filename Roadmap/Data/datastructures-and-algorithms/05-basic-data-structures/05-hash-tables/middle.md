# Hash Table — Middle Level

## Table of Contents

1. [Hash Function Design](#hash-function-design)
   - [Division Method](#division-method)
   - [Multiplication Method](#multiplication-method)
   - [Universal Hashing](#universal-hashing)
2. [Collision Resolution Strategies](#collision-resolution-strategies)
   - [Separate Chaining](#separate-chaining)
   - [Linear Probing](#linear-probing)
   - [Quadratic Probing](#quadratic-probing)
   - [Double Hashing](#double-hashing)
3. [Advanced Hashing Schemes](#advanced-hashing-schemes)
   - [Robin Hood Hashing](#robin-hood-hashing)
   - [Cuckoo Hashing](#cuckoo-hashing)
4. [Resizing and Rehashing](#resizing-and-rehashing)
5. [Load Factor Management](#load-factor-management)
6. [HashMap vs HashSet](#hashmap-vs-hashset)
7. [Ordered Maps vs Unordered Maps](#ordered-maps-vs-unordered-maps)
8. [Implementation: Open Addressing with Linear Probing](#implementation-open-addressing-with-linear-probing)

---

## Hash Function Design

The hash function is the heart of a hash table. Its quality directly determines the distribution of keys across buckets and thus the performance of every operation.

### Division Method

The simplest approach: `h(k) = k mod m`, where `m` is the table size.

- **Best practice**: Choose `m` as a prime number not close to a power of 2.
- **Why prime?** If `m = 2^p`, only the lowest p bits of the key matter. A prime forces all bits to contribute.
- Example: `m = 97` is better than `m = 100` or `m = 128`.

```
h("key") = hashCode("key") % 97
```

### Multiplication Method

`h(k) = floor(m * (k * A mod 1))`, where `A` is a constant in `(0, 1)`.

- Knuth recommends `A = (sqrt(5) - 1) / 2 = 0.6180339887...` (the golden ratio conjugate).
- The value of `m` is less critical — often a power of 2 for efficient bit shifting.

```
A = 0.6180339887
h(k) = floor(m * frac(k * A))
```

**Advantage**: Works well regardless of `m`; good distribution even without prime sizing.

### Universal Hashing

A family of hash functions `H = { h_a,b }` where:

```
h(k) = ((a * k + b) mod p) mod m
```

- `p` is a prime larger than any key
- `a` is chosen randomly from `{1, ..., p-1}`
- `b` is chosen randomly from `{0, ..., p-1}`

**Key property**: For any two distinct keys `k1 != k2`, the probability of collision is at most `1/m`. This is a **worst-case guarantee on average** — no adversary can craft inputs to cause many collisions because the function is chosen randomly.

---

## Collision Resolution Strategies

### Separate Chaining

Each bucket holds a linked list (or dynamic array) of entries that hash to the same index.

- **Load factor can exceed 1.0** (more entries than buckets).
- Average chain length = alpha (load factor).
- **Performance**: Expected O(1 + alpha) for search.
- **Variants**: Use balanced BSTs instead of linked lists for O(log n) worst-case per bucket (Java 8+ HashMap does this when chains exceed 8 entries — treeification).

### Linear Probing

Probe sequence: `h(k), h(k)+1, h(k)+2, ...` (all mod m).

- **Primary clustering**: Long runs of occupied slots form. If a cluster has length `c`, an insertion hitting any slot in the cluster extends it, making it even more likely for the next insertion to hit the same cluster.
- **Requires load factor < 1.0** (typically keep below 0.7).
- **Cache-friendly**: Sequential memory access.
- **Deletion**: Must use **tombstones** (a special marker indicating "deleted but not empty") — otherwise search may stop prematurely.

### Quadratic Probing

Probe sequence: `h(k), h(k)+1^2, h(k)+2^2, h(k)+3^2, ...`

```
probe_i = (h(k) + c1*i + c2*i^2) mod m
```

- **Reduces primary clustering** but introduces **secondary clustering** (keys with the same initial hash follow the same probe sequence).
- **Caveat**: Does not visit all buckets unless table size is prime and load factor < 0.5, or table size is a power of 2 with specific c1/c2.

### Double Hashing

Probe sequence: `h1(k), h1(k)+h2(k), h1(k)+2*h2(k), ...`

```
probe_i = (h1(k) + i * h2(k)) mod m
```

- Uses **two independent hash functions**.
- **Eliminates both primary and secondary clustering**.
- `h2(k)` must never be 0 — common choice: `h2(k) = 1 + (k mod (m-1))`.
- Best distribution among probing methods, but slightly more expensive per probe.

**Comparison Table**:

| Method | Clustering | Cache | Delete Complexity | Load Factor Limit |
|--------|-----------|-------|-------------------|-------------------|
| Linear Probing | Primary | Excellent | Tombstones | < 0.7 |
| Quadratic Probing | Secondary | Good | Tombstones | < 0.5 (prime m) |
| Double Hashing | None | Fair | Tombstones | < 0.7 |
| Separate Chaining | None | Poor | Simple remove | > 1.0 OK |

---

## Advanced Hashing Schemes

### Robin Hood Hashing

A refinement of linear probing. The key idea: **steal from the rich, give to the poor**.

When inserting, if the new key's **probe distance** (how far it is from its ideal bucket) is greater than the current occupant's probe distance, **swap** and continue inserting the displaced entry.

```
Insert key K at index i:
  probe_distance(K) = 3
  probe_distance(occupant) = 1
  Since 3 > 1, swap K with occupant, continue inserting occupant.
```

**Benefits**:
- Expected maximum probe distance is O(log n) — dramatically tighter than standard linear probing.
- Variance in probe distances is very low — all entries are roughly equidistant from their ideal positions.
- Lookups can **stop early**: if the current slot's probe distance is less than what we would have for our search key, the key is not in the table.

### Cuckoo Hashing

Uses **two hash functions** (`h1`, `h2`) and **two tables** (T1, T2).

**Insert(key)**:
1. Compute `h1(key)`. If `T1[h1(key)]` is empty, place there. Done.
2. Else, evict the existing entry from `T1[h1(key)]`, place key there.
3. Re-insert the evicted entry using `h2` into `T2[h2(evicted)]`.
4. If that slot is also occupied, evict and repeat (ping-pong).
5. If cycle detected (more than threshold evictions), **rehash** with new functions.

**Lookup is always O(1) worst case**: check `T1[h1(key)]` and `T2[h2(key)]` — at most 2 lookups.

**Tradeoff**: Insert can be expensive (cascading evictions, rare rehash), but lookup is guaranteed constant time.

---

## Resizing and Rehashing

When the load factor exceeds the threshold, the table must grow.

**Process**:
1. Allocate a new bucket array of size `2 * old_capacity`.
2. For every entry in the old array, recompute its hash with the new capacity and insert into the new array.
3. Replace the old array with the new one.

**Why rehash?** Because `index = hash(key) % capacity` — when capacity changes, the index changes.

**Amortized cost**: Even though a single resize is O(n), it happens so infrequently that the amortized cost per insertion is O(1). (Each element is rehashed at most O(log n) times total across all resizes.)

**Shrinking**: Some implementations also shrink when the load factor drops below a lower threshold (e.g., 0.1 or 0.25) to save memory.

---

## Load Factor Management

| Language | Default Capacity | Load Factor Threshold | Growth Factor |
|----------|-----------------|----------------------|---------------|
| Go `map` | Small (depends on runtime) | 6.5 (average per bucket) | 2x |
| Java `HashMap` | 16 | 0.75 | 2x |
| Python `dict` | 8 | 2/3 (~0.67) | 2x (to next power of 2) |

**Why different thresholds?**
- Go maps use a bucket structure with 8 slots per bucket plus overflow chains, so a higher effective load factor is fine.
- Java and Python use more traditional approaches where 0.67-0.75 balances memory and speed.

---

## HashMap vs HashSet

| Feature | HashMap | HashSet |
|---------|---------|---------|
| Stores | Key-Value pairs | Keys only (values are implicit/absent) |
| Lookup | `get(key)` returns value | `contains(key)` returns boolean |
| Duplicates | Duplicate keys overwrite the value | Duplicate keys are ignored |
| Use case | Dictionary, cache, index | Membership testing, deduplication |
| Internal | Full key-value storage | Often implemented as HashMap with dummy values |

In Java, `HashSet` is literally backed by a `HashMap` where every value is a static `PRESENT` object.

In Python, `set` and `dict` share the same underlying hash table implementation but `set` only stores keys.

In Go, there is no built-in set — developers use `map[T]struct{}` (struct{} is zero-size).

---

## Ordered Maps vs Unordered Maps

| Feature | Unordered Map (HashMap) | Ordered Map (TreeMap) |
|---------|------------------------|----------------------|
| Underlying structure | Hash table | Balanced BST (Red-Black tree) |
| Insert | O(1) average | O(log n) |
| Search | O(1) average | O(log n) |
| Delete | O(1) average | O(log n) |
| Iteration order | Unpredictable | Sorted by key |
| Range queries | Not supported | O(log n + k) where k = results |
| Min/Max key | O(n) | O(log n) |
| Memory | Less overhead | Tree node overhead |

**Language equivalents**:
| Language | Unordered | Ordered |
|----------|-----------|---------|
| Go | `map[K]V` | No built-in (use `btree` package) |
| Java | `HashMap<K,V>` | `TreeMap<K,V>` |
| Python | `dict` (insertion-ordered since 3.7) | `sortedcontainers.SortedDict` (third-party) |

**When to use ordered maps**: When you need sorted iteration, range queries, or floor/ceiling lookups. Otherwise, prefer unordered maps for their O(1) average operations.

---

## Implementation: Open Addressing with Linear Probing

### Go

```go
package hashtable

import "fmt"

const (
	emptySlot   = 0
	occupiedSlot = 1
	deletedSlot  = 2 // tombstone
)

type probeEntry struct {
	key    string
	value  int
	state  int // emptySlot, occupiedSlot, or deletedSlot
}

type LinearProbingTable struct {
	table    []probeEntry
	capacity int
	size     int
	loadMax  float64
}

func NewLinearProbing(capacity int) *LinearProbingTable {
	if capacity < 8 {
		capacity = 8
	}
	table := make([]probeEntry, capacity)
	return &LinearProbingTable{
		table:    table,
		capacity: capacity,
		size:     0,
		loadMax:  0.6,
	}
}

func (lp *LinearProbingTable) hash(key string) int {
	h := 0
	for _, ch := range key {
		h = h*31 + int(ch)
	}
	if h < 0 {
		h = -h
	}
	return h % lp.capacity
}

func (lp *LinearProbingTable) Insert(key string, value int) {
	if float64(lp.size+1)/float64(lp.capacity) > lp.loadMax {
		lp.resize()
	}

	index := lp.hash(key)
	firstTombstone := -1

	for i := 0; i < lp.capacity; i++ {
		pos := (index + i) % lp.capacity
		switch lp.table[pos].state {
		case emptySlot:
			if firstTombstone != -1 {
				pos = firstTombstone
			}
			lp.table[pos] = probeEntry{key: key, value: value, state: occupiedSlot}
			lp.size++
			return
		case deletedSlot:
			if firstTombstone == -1 {
				firstTombstone = pos
			}
		case occupiedSlot:
			if lp.table[pos].key == key {
				lp.table[pos].value = value
				return
			}
		}
	}

	// Should not reach here if load factor is managed.
	if firstTombstone != -1 {
		lp.table[firstTombstone] = probeEntry{key: key, value: value, state: occupiedSlot}
		lp.size++
	}
}

func (lp *LinearProbingTable) Search(key string) (int, bool) {
	index := lp.hash(key)
	for i := 0; i < lp.capacity; i++ {
		pos := (index + i) % lp.capacity
		switch lp.table[pos].state {
		case emptySlot:
			return 0, false
		case occupiedSlot:
			if lp.table[pos].key == key {
				return lp.table[pos].value, true
			}
		case deletedSlot:
			// Continue probing past tombstones.
		}
	}
	return 0, false
}

func (lp *LinearProbingTable) Delete(key string) bool {
	index := lp.hash(key)
	for i := 0; i < lp.capacity; i++ {
		pos := (index + i) % lp.capacity
		switch lp.table[pos].state {
		case emptySlot:
			return false
		case occupiedSlot:
			if lp.table[pos].key == key {
				lp.table[pos].state = deletedSlot
				lp.size--
				return true
			}
		}
	}
	return false
}

func (lp *LinearProbingTable) resize() {
	oldTable := lp.table
	lp.capacity *= 2
	lp.table = make([]probeEntry, lp.capacity)
	lp.size = 0

	for _, entry := range oldTable {
		if entry.state == occupiedSlot {
			lp.Insert(entry.key, entry.value)
		}
	}
}

func (lp *LinearProbingTable) Display() {
	for i, e := range lp.table {
		switch e.state {
		case emptySlot:
			fmt.Printf("[%d] empty\n", i)
		case occupiedSlot:
			fmt.Printf("[%d] %s:%d\n", i, e.key, e.value)
		case deletedSlot:
			fmt.Printf("[%d] TOMBSTONE\n", i)
		}
	}
}
```

### Java

```java
/**
 * Hash table with linear probing and tombstone deletion.
 */
public class LinearProbingTable {

    private static final int EMPTY = 0;
    private static final int OCCUPIED = 1;
    private static final int DELETED = 2;

    private String[] keys;
    private int[] values;
    private int[] states;
    private int capacity;
    private int size;
    private final double loadMax;

    public LinearProbingTable(int capacity) {
        this.capacity = Math.max(capacity, 8);
        this.keys = new String[this.capacity];
        this.values = new int[this.capacity];
        this.states = new int[this.capacity]; // All EMPTY by default
        this.size = 0;
        this.loadMax = 0.6;
    }

    private int hash(String key) {
        int h = key.hashCode();
        h ^= (h >>> 16);
        return Math.abs(h) % capacity;
    }

    public void insert(String key, int value) {
        if ((double) (size + 1) / capacity > loadMax) {
            resize();
        }

        int index = hash(key);
        int firstTombstone = -1;

        for (int i = 0; i < capacity; i++) {
            int pos = (index + i) % capacity;
            switch (states[pos]) {
                case EMPTY:
                    if (firstTombstone != -1) pos = firstTombstone;
                    keys[pos] = key;
                    values[pos] = value;
                    states[pos] = OCCUPIED;
                    size++;
                    return;
                case DELETED:
                    if (firstTombstone == -1) firstTombstone = pos;
                    break;
                case OCCUPIED:
                    if (keys[pos].equals(key)) {
                        values[pos] = value;
                        return;
                    }
                    break;
            }
        }
    }

    public Integer search(String key) {
        int index = hash(key);
        for (int i = 0; i < capacity; i++) {
            int pos = (index + i) % capacity;
            if (states[pos] == EMPTY) return null;
            if (states[pos] == OCCUPIED && keys[pos].equals(key)) {
                return values[pos];
            }
        }
        return null;
    }

    public boolean delete(String key) {
        int index = hash(key);
        for (int i = 0; i < capacity; i++) {
            int pos = (index + i) % capacity;
            if (states[pos] == EMPTY) return false;
            if (states[pos] == OCCUPIED && keys[pos].equals(key)) {
                states[pos] = DELETED;
                size--;
                return true;
            }
        }
        return false;
    }

    private void resize() {
        String[] oldKeys = keys;
        int[] oldValues = values;
        int[] oldStates = states;
        int oldCapacity = capacity;

        capacity *= 2;
        keys = new String[capacity];
        values = new int[capacity];
        states = new int[capacity];
        size = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldStates[i] == OCCUPIED) {
                insert(oldKeys[i], oldValues[i]);
            }
        }
    }

    public int size() { return size; }
}
```

### Python

```python
"""Hash table with linear probing and tombstone deletion."""

EMPTY = 0
OCCUPIED = 1
DELETED = 2  # tombstone


class LinearProbingTable:
    def __init__(self, capacity: int = 8, load_max: float = 0.6):
        self._capacity = max(capacity, 8)
        self._keys = [None] * self._capacity
        self._values = [None] * self._capacity
        self._states = [EMPTY] * self._capacity
        self._size = 0
        self._load_max = load_max

    def _hash(self, key: str) -> int:
        return hash(key) % self._capacity

    def insert(self, key: str, value: int) -> None:
        if (self._size + 1) / self._capacity > self._load_max:
            self._resize()

        index = self._hash(key)
        first_tombstone = -1

        for i in range(self._capacity):
            pos = (index + i) % self._capacity
            state = self._states[pos]

            if state == EMPTY:
                if first_tombstone != -1:
                    pos = first_tombstone
                self._keys[pos] = key
                self._values[pos] = value
                self._states[pos] = OCCUPIED
                self._size += 1
                return
            elif state == DELETED:
                if first_tombstone == -1:
                    first_tombstone = pos
            elif state == OCCUPIED:
                if self._keys[pos] == key:
                    self._values[pos] = value
                    return

    def search(self, key: str) -> int | None:
        index = self._hash(key)
        for i in range(self._capacity):
            pos = (index + i) % self._capacity
            if self._states[pos] == EMPTY:
                return None
            if self._states[pos] == OCCUPIED and self._keys[pos] == key:
                return self._values[pos]
        return None

    def delete(self, key: str) -> bool:
        index = self._hash(key)
        for i in range(self._capacity):
            pos = (index + i) % self._capacity
            if self._states[pos] == EMPTY:
                return False
            if self._states[pos] == OCCUPIED and self._keys[pos] == key:
                self._states[pos] = DELETED
                self._size -= 1
                return True
        return False

    def _resize(self) -> None:
        old_keys = self._keys
        old_values = self._values
        old_states = self._states
        old_capacity = self._capacity

        self._capacity *= 2
        self._keys = [None] * self._capacity
        self._values = [None] * self._capacity
        self._states = [EMPTY] * self._capacity
        self._size = 0

        for i in range(old_capacity):
            if old_states[i] == OCCUPIED:
                self.insert(old_keys[i], old_values[i])

    @property
    def size(self) -> int:
        return self._size

    def display(self) -> None:
        for i in range(self._capacity):
            if self._states[i] == EMPTY:
                print(f"[{i}] empty")
            elif self._states[i] == OCCUPIED:
                print(f"[{i}] {self._keys[i]}:{self._values[i]}")
            else:
                print(f"[{i}] TOMBSTONE")
```

---

## Key Takeaways

1. **Hash function quality** is the single most important factor for hash table performance.
2. **Separate chaining** is simpler and handles high load factors gracefully; **open addressing** is more cache-efficient.
3. **Robin Hood hashing** and **cuckoo hashing** are modern improvements that reduce worst-case probe lengths.
4. **Resizing** is essential — without it, performance degrades linearly.
5. **Know when to use ordered vs unordered maps** — use TreeMap/SortedDict only when you need sorted access or range queries.
6. **Tombstones** are necessary for deletion in open addressing but can accumulate and degrade performance — periodic cleanup or resize helps.
