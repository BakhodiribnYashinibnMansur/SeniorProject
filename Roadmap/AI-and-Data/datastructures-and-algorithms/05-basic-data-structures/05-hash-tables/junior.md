# Hash Table — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [What is a Hash Table?](#what-is-a-hash-table)
3. [Real-World Analogies](#real-world-analogies)
4. [Core Concepts](#core-concepts)
   - [Key-Value Pairs](#key-value-pairs)
   - [Hash Functions](#hash-functions)
   - [Buckets](#buckets)
   - [Collisions](#collisions)
5. [Collision Handling](#collision-handling)
   - [Chaining (Separate Chaining)](#chaining-separate-chaining)
   - [Open Addressing](#open-addressing)
6. [Operations and Complexity](#operations-and-complexity)
7. [Load Factor](#load-factor)
8. [Full Implementation — Chaining](#full-implementation--chaining)
   - [Go](#go-implementation)
   - [Java](#java-implementation)
   - [Python](#python-implementation)
9. [Common Mistakes](#common-mistakes)
10. [Summary](#summary)

---

## Introduction

A **hash table** (also called a hash map) is one of the most important data structures in computer science. It allows you to store and retrieve data in **average O(1) time** — constant time — regardless of how much data you have. Nearly every programming language provides a built-in hash table: Go has `map`, Java has `HashMap`, and Python has `dict`.

Understanding how hash tables work under the hood will make you a better programmer and help you choose the right data structure for the right problem.

---

## What is a Hash Table?

A hash table is a data structure that maps **keys** to **values**. Internally it uses a **hash function** to convert each key into an index in an underlying array (called "buckets"). When you want to store or look up a value, the hash function tells you exactly which bucket to go to — no scanning required.

```
Key "alice" --> hash("alice") = 3 --> buckets[3] = "alice: 95"
Key "bob"   --> hash("bob")   = 7 --> buckets[7] = "bob: 82"
Key "carol" --> hash("carol") = 1 --> buckets[1] = "carol: 91"
```

---

## Real-World Analogies

### 1. Dictionary / Phone Book

A phone book maps **names** (keys) to **phone numbers** (values). You do not read every page — you jump to the letter section and narrow down quickly. A hash table does something similar but even faster: it computes exactly where to look.

### 2. School Lockers

Imagine 100 lockers numbered 0-99. Each student is assigned a locker by a formula: `locker = student_id % 100`. Given any student ID you can compute the locker number instantly — no searching.

### 3. Library Catalog

Books are organized by call numbers. The call number acts like a hash — it tells you the exact shelf. You do not browse every shelf; you go directly to the right one.

### 4. Coat Check

You hand in your coat, receive a numbered ticket. The ticket is the "hash" — it maps directly to the hook where your coat hangs. Retrieval is instant.

---

## Core Concepts

### Key-Value Pairs

A hash table stores **entries**, each consisting of:
- **Key** — the identifier used for lookup (must be unique within the table)
- **Value** — the data associated with that key

Examples:
| Key | Value |
|-----|-------|
| `"alice"` | `95` |
| `"bob"` | `82` |
| `"carol"` | `91` |

### Hash Functions

A **hash function** takes a key and returns an integer (the **hash code**). This integer is then mapped to a valid bucket index, typically via the modulo operator:

```
index = hash(key) % number_of_buckets
```

A good hash function should:
1. Be **deterministic** — same key always produces the same hash
2. Be **fast** to compute
3. **Distribute keys uniformly** across buckets (minimize clustering)

Simple example for string keys:
```
hash("abc") = ('a'*31^2 + 'b'*31^1 + 'c'*31^0) % bucket_count
```

### Buckets

The underlying array in a hash table is called the **bucket array**. Each position (bucket) holds zero or more entries. The size of this array is called the **capacity**.

```
Buckets:  [0] [1] [2] [3] [4] [5] [6] [7]
```

### Collisions

A **collision** occurs when two different keys hash to the same bucket index:

```
hash("alice") % 8 = 3
hash("dave")  % 8 = 3   <-- collision!
```

Collisions are inevitable (by the Pigeonhole Principle: if you have more possible keys than buckets, some keys must share a bucket). The question is how we handle them.

---

## Collision Handling

### Chaining (Separate Chaining)

Each bucket holds a **linked list** (or any list) of all entries that hash to that index.

```
Bucket 0: []
Bucket 1: [("carol", 91)]
Bucket 2: []
Bucket 3: [("alice", 95) -> ("dave", 78)]   <-- chain of 2
Bucket 4: []
Bucket 5: []
Bucket 6: []
Bucket 7: [("bob", 82)]
```

**Insert**: hash the key, go to the bucket, append to the chain.
**Search**: hash the key, go to the bucket, walk the chain comparing keys.
**Delete**: hash the key, go to the bucket, find and remove from the chain.

**Pros**: Simple, never "fills up", good with high load factors.
**Cons**: Extra memory for pointers, cache-unfriendly (linked list traversal).

### Open Addressing

Instead of chains, all entries are stored directly in the bucket array. When a collision occurs, we **probe** for the next open slot.

**Linear Probing**: Try index, index+1, index+2, ...
```
hash("alice") % 8 = 3  --> buckets[3] = ("alice", 95)
hash("dave")  % 8 = 3  --> buckets[3] occupied, try 4 --> buckets[4] = ("dave", 78)
```

**Pros**: Cache-friendly (sequential memory access), no extra pointers.
**Cons**: Clustering (entries bunch together), deletion is tricky (need tombstones).

---

## Operations and Complexity

| Operation | Average Case | Worst Case |
|-----------|-------------|------------|
| **Insert** | O(1) | O(n) |
| **Search** | O(1) | O(n) |
| **Delete** | O(1) | O(n) |

- **Average O(1)**: With a good hash function and low load factor, each bucket has very few entries (often 0 or 1), so operations are constant time.
- **Worst O(n)**: If every key hashes to the same bucket, you have a single linked list of n elements — this almost never happens with a good hash function.

---

## Load Factor

The **load factor** (alpha) is:

```
alpha = number_of_entries / number_of_buckets
```

| Load Factor | Meaning |
|-------------|---------|
| 0.0 | Empty table |
| 0.5 | Half full — good performance |
| 0.75 | Common threshold to trigger resize |
| 1.0 | As many entries as buckets |
| > 1.0 | Only possible with chaining |

When the load factor exceeds a threshold (commonly 0.75), the table **resizes** — typically doubling the bucket count and rehashing all entries. This keeps average chain length short and operations fast.

---

## Full Implementation — Chaining

Below is a complete hash table with separate chaining, supporting insert, search, delete, and automatic resizing.

### Go Implementation

```go
package hashtable

import (
	"fmt"
	"hash/fnv"
)

// Entry represents a key-value pair in the hash table.
type Entry struct {
	Key   string
	Value int
	Next  *Entry
}

// HashTable is a hash table with separate chaining.
type HashTable struct {
	buckets  []*Entry
	capacity int
	size     int
	loadMax  float64
}

// New creates a hash table with the given initial capacity.
func New(capacity int) *HashTable {
	if capacity < 1 {
		capacity = 16
	}
	return &HashTable{
		buckets:  make([]*Entry, capacity),
		capacity: capacity,
		size:     0,
		loadMax:  0.75,
	}
}

// hash returns a bucket index for the given key.
func (ht *HashTable) hash(key string) int {
	h := fnv.New32a()
	h.Write([]byte(key))
	return int(h.Sum32()) % ht.capacity
}

// Insert adds or updates a key-value pair.
func (ht *HashTable) Insert(key string, value int) {
	if float64(ht.size+1)/float64(ht.capacity) > ht.loadMax {
		ht.resize()
	}

	index := ht.hash(key)
	current := ht.buckets[index]

	// Check if key already exists — update value.
	for current != nil {
		if current.Key == key {
			current.Value = value
			return
		}
		current = current.Next
	}

	// Key not found — prepend new entry to chain.
	newEntry := &Entry{
		Key:   key,
		Value: value,
		Next:  ht.buckets[index],
	}
	ht.buckets[index] = newEntry
	ht.size++
}

// Search looks up a key and returns its value and whether it was found.
func (ht *HashTable) Search(key string) (int, bool) {
	index := ht.hash(key)
	current := ht.buckets[index]

	for current != nil {
		if current.Key == key {
			return current.Value, true
		}
		current = current.Next
	}
	return 0, false
}

// Delete removes a key from the hash table. Returns true if found.
func (ht *HashTable) Delete(key string) bool {
	index := ht.hash(key)
	current := ht.buckets[index]
	var prev *Entry

	for current != nil {
		if current.Key == key {
			if prev == nil {
				ht.buckets[index] = current.Next
			} else {
				prev.Next = current.Next
			}
			ht.size--
			return true
		}
		prev = current
		current = current.Next
	}
	return false
}

// resize doubles the bucket array and rehashes all entries.
func (ht *HashTable) resize() {
	oldBuckets := ht.buckets
	ht.capacity *= 2
	ht.buckets = make([]*Entry, ht.capacity)
	ht.size = 0

	for _, head := range oldBuckets {
		current := head
		for current != nil {
			ht.Insert(current.Key, current.Value)
			current = current.Next
		}
	}
}

// Size returns the number of entries.
func (ht *HashTable) Size() int {
	return ht.size
}

// LoadFactor returns the current load factor.
func (ht *HashTable) LoadFactor() float64 {
	return float64(ht.size) / float64(ht.capacity)
}

// Display prints the hash table for debugging.
func (ht *HashTable) Display() {
	for i, head := range ht.buckets {
		fmt.Printf("Bucket %d: ", i)
		current := head
		for current != nil {
			fmt.Printf("(%s:%d) -> ", current.Key, current.Value)
			current = current.Next
		}
		fmt.Println("nil")
	}
}

// Usage example:
// func main() {
//     ht := hashtable.New(8)
//     ht.Insert("alice", 95)
//     ht.Insert("bob", 82)
//     ht.Insert("carol", 91)
//     ht.Insert("dave", 78)
//
//     val, found := ht.Search("bob")
//     fmt.Println(val, found) // 82 true
//
//     ht.Delete("carol")
//     _, found = ht.Search("carol")
//     fmt.Println(found) // false
//
//     fmt.Printf("Size: %d, Load: %.2f\n", ht.Size(), ht.LoadFactor())
//     ht.Display()
// }
```

### Java Implementation

```java
import java.util.ArrayList;
import java.util.List;

/**
 * Hash table with separate chaining collision resolution.
 */
public class HashTable {

    /**
     * A single key-value entry in the chain.
     */
    private static class Entry {
        String key;
        int value;
        Entry next;

        Entry(String key, int value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry[] buckets;
    private int capacity;
    private int size;
    private final double loadMax;

    /**
     * Creates a hash table with the given initial capacity.
     */
    public HashTable(int capacity) {
        this.capacity = Math.max(capacity, 16);
        this.buckets = new Entry[this.capacity];
        this.size = 0;
        this.loadMax = 0.75;
    }

    public HashTable() {
        this(16);
    }

    /**
     * Computes a bucket index for the given key.
     * Uses Java's built-in hashCode() with bit-spreading.
     */
    private int hash(String key) {
        int h = key.hashCode();
        // Spread high bits to low bits to improve distribution.
        h ^= (h >>> 16);
        return Math.abs(h) % capacity;
    }

    /**
     * Inserts or updates a key-value pair.
     */
    public void insert(String key, int value) {
        if ((double) (size + 1) / capacity > loadMax) {
            resize();
        }

        int index = hash(key);
        Entry current = buckets[index];

        // Check if key already exists — update.
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        // Prepend new entry to chain.
        buckets[index] = new Entry(key, value, buckets[index]);
        size++;
    }

    /**
     * Searches for a key. Returns the value or null if not found.
     */
    public Integer search(String key) {
        int index = hash(key);
        Entry current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Deletes a key from the table. Returns true if found and removed.
     */
    public boolean delete(String key) {
        int index = hash(key);
        Entry current = buckets[index];
        Entry prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    /**
     * Doubles the bucket array and rehashes all entries.
     */
    private void resize() {
        Entry[] oldBuckets = buckets;
        capacity *= 2;
        buckets = new Entry[capacity];
        size = 0;

        for (Entry head : oldBuckets) {
            Entry current = head;
            while (current != null) {
                insert(current.key, current.value);
                current = current.next;
            }
        }
    }

    public int size() {
        return size;
    }

    public double loadFactor() {
        return (double) size / capacity;
    }

    /**
     * Prints the hash table for debugging.
     */
    public void display() {
        for (int i = 0; i < capacity; i++) {
            System.out.printf("Bucket %d: ", i);
            Entry current = buckets[i];
            while (current != null) {
                System.out.printf("(%s:%d) -> ", current.key, current.value);
                current = current.next;
            }
            System.out.println("null");
        }
    }

    // Usage example:
    // public static void main(String[] args) {
    //     HashTable ht = new HashTable(8);
    //     ht.insert("alice", 95);
    //     ht.insert("bob", 82);
    //     ht.insert("carol", 91);
    //     ht.insert("dave", 78);
    //
    //     System.out.println(ht.search("bob"));    // 82
    //     ht.delete("carol");
    //     System.out.println(ht.search("carol"));  // null
    //
    //     System.out.printf("Size: %d, Load: %.2f%n", ht.size(), ht.loadFactor());
    //     ht.display();
    // }
}
```

### Python Implementation

```python
"""Hash table with separate chaining collision resolution."""


class Entry:
    """A single key-value entry in the chain."""

    __slots__ = ("key", "value", "next")

    def __init__(self, key: str, value: int, next_entry=None):
        self.key = key
        self.value = value
        self.next = next_entry


class HashTable:
    """Hash table using separate chaining for collision resolution."""

    def __init__(self, capacity: int = 16, load_max: float = 0.75):
        self._capacity = max(capacity, 16)
        self._buckets: list[Entry | None] = [None] * self._capacity
        self._size = 0
        self._load_max = load_max

    def _hash(self, key: str) -> int:
        """Compute bucket index using Python's built-in hash."""
        return hash(key) % self._capacity

    def insert(self, key: str, value: int) -> None:
        """Insert or update a key-value pair."""
        if (self._size + 1) / self._capacity > self._load_max:
            self._resize()

        index = self._hash(key)
        current = self._buckets[index]

        # Check if key already exists — update.
        while current is not None:
            if current.key == key:
                current.value = value
                return
            current = current.next

        # Prepend new entry to chain.
        new_entry = Entry(key, value, self._buckets[index])
        self._buckets[index] = new_entry
        self._size += 1

    def search(self, key: str) -> int | None:
        """Search for a key. Returns value or None if not found."""
        index = self._hash(key)
        current = self._buckets[index]

        while current is not None:
            if current.key == key:
                return current.value
            current = current.next
        return None

    def delete(self, key: str) -> bool:
        """Delete a key. Returns True if found and removed."""
        index = self._hash(key)
        current = self._buckets[index]
        prev = None

        while current is not None:
            if current.key == key:
                if prev is None:
                    self._buckets[index] = current.next
                else:
                    prev.next = current.next
                self._size -= 1
                return True
            prev = current
            current = current.next
        return False

    def _resize(self) -> None:
        """Double the bucket array and rehash all entries."""
        old_buckets = self._buckets
        self._capacity *= 2
        self._buckets = [None] * self._capacity
        self._size = 0

        for head in old_buckets:
            current = head
            while current is not None:
                self.insert(current.key, current.value)
                current = current.next

    @property
    def size(self) -> int:
        return self._size

    @property
    def load_factor(self) -> float:
        return self._size / self._capacity

    def display(self) -> None:
        """Print the hash table for debugging."""
        for i in range(self._capacity):
            chain = []
            current = self._buckets[i]
            while current is not None:
                chain.append(f"({current.key}:{current.value})")
                current = current.next
            print(f"Bucket {i}: {' -> '.join(chain) if chain else 'empty'}")

    def __contains__(self, key: str) -> bool:
        return self.search(key) is not None

    def __repr__(self) -> str:
        items = []
        for head in self._buckets:
            current = head
            while current is not None:
                items.append(f"{current.key!r}: {current.value}")
                current = current.next
        return "HashTable({" + ", ".join(items) + "})"


# Usage example:
# if __name__ == "__main__":
#     ht = HashTable(capacity=8)
#     ht.insert("alice", 95)
#     ht.insert("bob", 82)
#     ht.insert("carol", 91)
#     ht.insert("dave", 78)
#
#     print(ht.search("bob"))      # 82
#     ht.delete("carol")
#     print(ht.search("carol"))    # None
#
#     print(f"Size: {ht.size}, Load: {ht.load_factor:.2f}")
#     ht.display()
```

---

## Common Mistakes

| Mistake | Why It Is Wrong |
|---------|-----------------|
| Using mutable objects as keys | If the key changes after insertion, its hash changes and the entry becomes unreachable |
| Forgetting to handle collisions | Two keys can map to the same bucket — you must handle this |
| Not resizing the table | Without resizing, chains grow long and O(1) degrades to O(n) |
| Using a poor hash function | Bad distribution causes many collisions and clustering |
| Comparing hash codes instead of keys | Different keys can have the same hash — always compare actual keys |
| Confusing hash code with bucket index | Hash code is a large integer; bucket index = hashCode % capacity |

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Hash Table** | Maps keys to values using a hash function |
| **Hash Function** | Converts a key to a bucket index |
| **Collision** | Two keys mapping to the same bucket |
| **Chaining** | Each bucket holds a linked list of entries |
| **Open Addressing** | Entries stored directly in the array; probe for open slots |
| **Load Factor** | entries / buckets — controls when to resize |
| **Average Complexity** | O(1) for insert, search, and delete |
| **Worst Complexity** | O(n) when all keys collide |
| **Resize** | Double capacity and rehash when load factor exceeds threshold |

Hash tables are the workhorse of modern programming. Master them well — they appear everywhere from database indexes to compiler symbol tables to network routing.
