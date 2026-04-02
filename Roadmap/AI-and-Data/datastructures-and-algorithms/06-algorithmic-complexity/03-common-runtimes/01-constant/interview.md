# Constant Time O(1) -- Interview Questions

## Table of Contents

1. [Junior Level Questions](#junior-level-questions)
2. [Middle Level Questions](#middle-level-questions)
3. [Senior Level Questions](#senior-level-questions)
4. [Professional Level Questions](#professional-level-questions)
5. [Coding Challenge](#coding-challenge)

---

## Junior Level Questions

### Q1: What does O(1) mean?

**Answer:** O(1) means constant time -- the operation takes a fixed amount of time
regardless of the input size. Whether you have 10 elements or 10 million, the operation
completes in the same bounded number of steps. The "1" does not mean one step; it means
the step count is bounded by a constant.

### Q2: Give three examples of O(1) operations.

**Answer:**
1. **Array access by index** -- `arr[5]` is O(1) because the memory address is calculated
   directly: `base + index * element_size`.
2. **Hash map lookup** -- `map.get(key)` is O(1) average case because the hash function
   computes the bucket location directly.
3. **Stack push/pop** -- Adding or removing from the top of a stack is O(1) because only
   the top pointer changes.

### Q3: Is the following function O(1)? Why or why not?

```
function process(x):
    a = x + 1
    b = a * 2
    c = b - 3
    d = c / 4
    return d
```

**Answer:** Yes, this is O(1). It performs exactly 4 arithmetic operations regardless of
the value of `x`. The number of operations is constant (4), so it is O(1). Remember,
O(1) does not mean "one operation" -- it means "a fixed number of operations."

### Q4: Is searching for an element in an unsorted array O(1)?

**Answer:** No. In the worst case, you must examine every element to determine if the
target is present or absent. This requires up to `n` comparisons, making it O(n). You
cannot achieve O(1) search on an unsorted array without preprocessing (like building
a hash set first).

### Q5: Why is accessing `arr[0]` and `arr[999999]` the same speed?

**Answer:** Arrays store elements in contiguous memory. The address of any element is
calculated as `base_address + index * element_size`. This is a single arithmetic
calculation regardless of the index value. The CPU does not "walk" through the array;
it jumps directly to the computed address.

---

## Middle Level Questions

### Q6: Explain the difference between worst-case O(1), amortized O(1), and expected O(1).

**Answer:**
- **Worst-case O(1):** Every single operation is guaranteed to complete in constant time.
  Example: array access by index.
- **Amortized O(1):** Most operations are fast, but occasionally one is expensive (e.g.,
  resizing). Averaged over a sequence of `n` operations, each takes O(1). Example:
  dynamic array append.
- **Expected O(1):** On average over random inputs (or random internal choices), the
  operation is O(1). Individual operations may be slow. Example: hash table lookup.

### Q7: How does a dynamic array achieve amortized O(1) append?

**Answer:** When the array is full, it doubles its capacity and copies all elements --
an O(n) operation. But this happens rarely. Over `n` appends, the total copy cost is
1 + 2 + 4 + ... + n = 2n - 1. Adding the n placement costs gives 3n - 1 total. Divided
by n operations, the amortized cost is approximately 3, which is O(1).

### Q8: Can an O(1) algorithm be slower than an O(log n) algorithm?

**Answer:** Yes, absolutely. Big-O hides constant factors. An O(1) algorithm with a
constant of 1000 is slower than an O(log n) algorithm with a constant of 1 for all
inputs up to `n = 2^1000` (which exceeds any practical dataset). In practice, hash
tables (O(1) expected) can be slower than binary search on sorted arrays for small `n`
due to cache effects and hash computation overhead.

### Q9: What is the load factor of a hash table and why does it matter for O(1)?

**Answer:** The load factor `alpha = n/m` (elements / buckets) determines the average
chain length. Higher alpha means more collisions and slower lookups. To maintain O(1)
performance, hash tables rehash when alpha exceeds a threshold (0.75 in Java HashMap,
~0.67 in Python dict, ~6.5 per bucket in Go maps). Rehashing is O(n) but amortized O(1).

### Q10: Why might a sorted array with binary search outperform a hash table?

**Answer:** For small to moderate `n` (under ~1000 elements), binary search on a sorted
array can be faster because: (1) arrays are cache-friendly -- elements are contiguous in
memory, so CPU prefetching works well; (2) no hash function computation overhead; (3)
no memory overhead for buckets and pointers. The O(log n) with small constant beats
O(1) with larger constant.

---

## Senior Level Questions

### Q11: Design an O(1) LRU cache. What data structures do you use?

**Answer:** Combine a **hash map** and a **doubly-linked list**:
- The hash map maps keys to linked list nodes for O(1) lookup.
- The doubly-linked list maintains access order for O(1) removal and insertion.
- `get(key)`: Look up in hash map O(1), move node to front of list O(1).
- `put(key, value)`: Insert in hash map O(1), add node to front O(1). If over capacity,
  remove tail node O(1) and delete from hash map O(1).
- All operations are O(1).

### Q12: How does consistent hashing achieve O(1) key routing in distributed systems?

**Answer:** Servers are placed on a hash ring. A key is hashed and routed to the next
server clockwise on the ring. With a precomputed jump table or a sorted array of server
positions, this lookup is O(log S) where S is the number of servers. Since S is typically
small and fixed (not growing with the number of keys), this is effectively O(1) with
respect to the data size.

### Q13: What is a lock-free O(1) operation and when would you use one?

**Answer:** A lock-free O(1) operation uses atomic hardware instructions (like CAS --
Compare-And-Swap) instead of locks. It guarantees that at least one thread makes progress
in any contention scenario. Use cases: atomic counters, lock-free stacks/queues, and
concurrent hash maps. Benefits: no deadlocks, no priority inversion, lower latency under
contention. Downsides: harder to implement correctly, ABA problem.

### Q14: How do Bloom filters achieve O(1) membership testing?

**Answer:** A Bloom filter uses `k` hash functions and a bit array of size `m`. To add
an element, compute `k` hash positions and set those bits. To query, check if all `k`
bits are set. Since `k` is a fixed constant (typically 3-10), both add and query are
O(k) = O(1). Trade-off: false positives are possible (bits set by different elements
align), but false negatives are impossible.

---

## Professional Level Questions

### Q15: Prove that dynamic array append is amortized O(1) using the potential method.

**Answer:** Define potential `Phi = 2 * size - capacity`. For a non-resize append:
actual cost = 1, potential change = +2, amortized cost = 3. For a resize when
size = capacity = k: actual cost = k+1 (copy + place), potential changes from k to 2,
change = 2-k, amortized cost = (k+1) + (2-k) = 3. Both cases give amortized cost 3 =
O(1).

### Q16: Explain the FKS perfect hashing scheme.

**Answer:** FKS uses two levels of universal hashing. Level 1 hashes `n` keys into `n`
buckets. Level 2 resolves each bucket with a secondary table of size `b_j^2` (square
of bucket size), which is collision-free by the birthday paradox with probability >= 1/2.
Total space is O(n) because the expected sum of `b_j^2` is O(n). Lookup requires two
hash computations and one comparison -- O(1) worst case.

### Q17: Under what assumptions does hash table lookup become O(n) worst case?

**Answer:** Hash table lookup degrades to O(n) when: (1) all keys hash to the same
bucket (adversarial input without randomized hashing); (2) the hash function has poor
distribution; (3) the load factor is unbounded (no rehashing). Universal hashing
mitigates (1) by randomizing the hash function. Algorithms like Cuckoo hashing and
FKS eliminate worst-case O(n) entirely for lookups.

### Q18: What are the limitations of the word-RAM model for O(1) analysis?

**Answer:** The word-RAM model assumes: (1) word-sized operations are O(1), which fails
for arbitrary-precision arithmetic; (2) memory access is O(1) regardless of address,
which ignores cache hierarchy effects; (3) word size w >= log n, which is always true
in practice but is a formal requirement. Real hardware has cache misses (100x slower
than L1 hits), branch mispredictions, and TLB misses that the model ignores.

---

## Coding Challenge

**Problem:** Design a data structure that supports the following operations, all in O(1)
average time:

1. `insert(val)` -- Insert a value (duplicates allowed).
2. `remove(val)` -- Remove one occurrence of a value if present.
3. `getRandom()` -- Return a random element with equal probability.

### Go

```go
package main

import (
    "fmt"
    "math/rand"
)

type RandomizedSet struct {
    data    []int
    indices map[int][]int // value -> list of indices in data
}

func NewRandomizedSet() *RandomizedSet {
    return &RandomizedSet{
        data:    []int{},
        indices: make(map[int][]int),
    }
}

// Insert is O(1) amortized
func (rs *RandomizedSet) Insert(val int) {
    rs.indices[val] = append(rs.indices[val], len(rs.data))
    rs.data = append(rs.data, val)
}

// Remove is O(1) amortized -- swap with last element
func (rs *RandomizedSet) Remove(val int) bool {
    idxList, exists := rs.indices[val]
    if !exists || len(idxList) == 0 {
        return false
    }

    // Get index of one occurrence of val
    removeIdx := idxList[len(idxList)-1]
    rs.indices[val] = idxList[:len(idxList)-1]
    if len(rs.indices[val]) == 0 {
        delete(rs.indices, val)
    }

    // Swap with last element
    lastIdx := len(rs.data) - 1
    lastVal := rs.data[lastIdx]

    if removeIdx != lastIdx {
        rs.data[removeIdx] = lastVal

        // Update index of the swapped element
        lastIdxList := rs.indices[lastVal]
        for i, idx := range lastIdxList {
            if idx == lastIdx {
                lastIdxList[i] = removeIdx
                break
            }
        }
    }

    rs.data = rs.data[:lastIdx]
    return true
}

// GetRandom is O(1)
func (rs *RandomizedSet) GetRandom() int {
    return rs.data[rand.Intn(len(rs.data))]
}

func main() {
    rs := NewRandomizedSet()
    rs.Insert(1)
    rs.Insert(2)
    rs.Insert(3)
    rs.Insert(2)

    fmt.Println("Random:", rs.GetRandom())
    rs.Remove(2)
    fmt.Println("After removing one 2, random:", rs.GetRandom())
}
```

### Java

```java
import java.util.*;

public class RandomizedSet {
    private List<Integer> data;
    private Map<Integer, List<Integer>> indices;
    private Random rand;

    public RandomizedSet() {
        data = new ArrayList<>();
        indices = new HashMap<>();
        rand = new Random();
    }

    // O(1) amortized
    public void insert(int val) {
        indices.computeIfAbsent(val, k -> new ArrayList<>()).add(data.size());
        data.add(val);
    }

    // O(1) amortized
    public boolean remove(int val) {
        List<Integer> idxList = indices.get(val);
        if (idxList == null || idxList.isEmpty()) return false;

        int removeIdx = idxList.remove(idxList.size() - 1);
        if (idxList.isEmpty()) indices.remove(val);

        int lastIdx = data.size() - 1;
        int lastVal = data.get(lastIdx);

        if (removeIdx != lastIdx) {
            data.set(removeIdx, lastVal);
            List<Integer> lastIdxList = indices.get(lastVal);
            for (int i = 0; i < lastIdxList.size(); i++) {
                if (lastIdxList.get(i) == lastIdx) {
                    lastIdxList.set(i, removeIdx);
                    break;
                }
            }
        }

        data.remove(lastIdx);
        return true;
    }

    // O(1)
    public int getRandom() {
        return data.get(rand.nextInt(data.size()));
    }

    public static void main(String[] args) {
        RandomizedSet rs = new RandomizedSet();
        rs.insert(1);
        rs.insert(2);
        rs.insert(3);
        rs.insert(2);

        System.out.println("Random: " + rs.getRandom());
        rs.remove(2);
        System.out.println("After removing one 2, random: " + rs.getRandom());
    }
}
```

### Python

```python
import random

class RandomizedSet:
    def __init__(self):
        self.data = []
        self.indices = {}  # value -> list of indices

    def insert(self, val):
        """O(1) amortized."""
        if val not in self.indices:
            self.indices[val] = []
        self.indices[val].append(len(self.data))
        self.data.append(val)

    def remove(self, val):
        """O(1) amortized -- swap with last element."""
        if val not in self.indices or not self.indices[val]:
            return False

        remove_idx = self.indices[val].pop()
        if not self.indices[val]:
            del self.indices[val]

        last_idx = len(self.data) - 1
        last_val = self.data[last_idx]

        if remove_idx != last_idx:
            self.data[remove_idx] = last_val
            # Update the swapped element's index
            idx_list = self.indices[last_val]
            for i, idx in enumerate(idx_list):
                if idx == last_idx:
                    idx_list[i] = remove_idx
                    break

        self.data.pop()
        return True

    def get_random(self):
        """O(1)."""
        return random.choice(self.data)


rs = RandomizedSet()
rs.insert(1)
rs.insert(2)
rs.insert(3)
rs.insert(2)

print("Random:", rs.get_random())
rs.remove(2)
print("After removing one 2, random:", rs.get_random())
```
