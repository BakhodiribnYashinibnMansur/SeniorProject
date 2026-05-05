# Constant Time O(1) -- Professional Level

## Table of Contents

1. [Formal Definition of O(1)](#formal-definition-of-o1)
2. [Proving Operations Are Constant Time](#proving-operations-are-constant-time)
   - [Array Access Proof](#array-access-proof)
   - [Hash Table Expected O(1) Proof](#hash-table-expected-o1-proof)
   - [Amortized O(1) Proof via Potential Method](#amortized-o1-proof-via-potential-method)
3. [The Word-RAM Model](#the-word-ram-model)
4. [Perfect Hashing -- O(1) Worst Case](#perfect-hashing--o1-worst-case)
   - [Static Perfect Hashing](#static-perfect-hashing)
   - [FKS Hashing Scheme](#fks-hashing-scheme)
   - [Cuckoo Hashing](#cuckoo-hashing)
5. [Theoretical Limits](#theoretical-limits)
6. [Key Takeaways](#key-takeaways)

---

## Formal Definition of O(1)

**Definition (Big-O notation for constant functions):**

A function `T(n)` is `O(1)` if and only if there exist positive constants `c` and `n0`
such that for all `n >= n0`:

```
T(n) <= c
```

Equivalently: `T(n) = O(1)` if `lim sup_{n -> infinity} T(n) < infinity`.

**Important distinctions:**

- `O(1)` describes an **upper bound**. If `T(n) = O(1)`, the function is bounded above
  by a constant.
- `Theta(1)` means the function is bounded both above AND below by constants. For most
  practical algorithms, when we say O(1) we implicitly mean Theta(1).
- `Omega(1)` means the function is at least constant -- trivially true for any algorithm
  that does at least one operation.

**What O(1) is NOT:**

- It does not mean the function equals 1.
- It does not mean the function is "fast" in absolute terms.
- It does not describe cache behavior, branch prediction, or hardware effects.
- `T(n) = 10^100` is technically O(1) -- the constant may be enormous.

---

## Proving Operations Are Constant Time

### Array Access Proof

**Claim:** Accessing element `A[i]` in an array of size `n` is O(1) in the word-RAM model.

**Proof:**

In the word-RAM model, we assume:
1. Memory is a flat array of words.
2. Each word has `w >= log n` bits.
3. Arithmetic on word-sized operands takes O(1) time.
4. Memory access by address takes O(1) time.

Given array `A` with base address `b` and element size `s`:

```
address(A[i]) = b + i * s
```

This requires:
- One multiplication: `i * s` -- O(1) in word-RAM.
- One addition: `b + (i * s)` -- O(1) in word-RAM.
- One memory read at the computed address -- O(1) in word-RAM.

Total: 3 operations, all O(1). Therefore `T(n) <= 3` for all `n`. QED.

### Hash Table Expected O(1) Proof

**Claim:** Under the Simple Uniform Hashing Assumption (SUHA), lookup in a hash table
with chaining is O(1) expected time when `alpha = n/m = O(1)`.

**Proof sketch:**

Under SUHA, each key is equally likely to hash to any of the `m` slots, independently
of other keys.

- Expected chain length at any slot = `n/m = alpha`.
- Expected time for unsuccessful search = `Theta(1 + alpha)`.
- Expected time for successful search = `Theta(1 + alpha/2)`.

If we maintain `alpha = O(1)` (e.g., `alpha <= 0.75` by rehashing), then both are
`Theta(1 + O(1)) = O(1)`. QED.

**Caveat:** SUHA is an idealization. Real hash functions may not satisfy it. Universal
hashing provides a rigorous alternative.

### Amortized O(1) Proof via Potential Method

**Claim:** Appending to a dynamic array with doubling strategy has amortized O(1) cost.

**Proof using the potential method:**

Define the potential function:

```
Phi(D_i) = 2 * size(D_i) - capacity(D_i)
```

where `D_i` is the state of the data structure after the `i`-th operation.

**Case 1: No resize.** `size < capacity`.
- Actual cost: `c_i = 1` (place element).
- `Phi(D_i) - Phi(D_{i-1}) = 2(s+1) - cap - (2s - cap) = 2`.
- Amortized cost: `c_hat_i = c_i + Phi(D_i) - Phi(D_{i-1}) = 1 + 2 = 3`.

**Case 2: Resize.** `size = capacity = k`. New capacity = `2k`.
- Actual cost: `c_i = k + 1` (copy `k` elements + place new element).
- Before: `Phi = 2k - k = k`.
- After: `Phi = 2(k+1) - 2k = 2`.
- `Phi(D_i) - Phi(D_{i-1}) = 2 - k`.
- Amortized cost: `c_hat_i = (k + 1) + (2 - k) = 3`.

In both cases, the amortized cost is exactly 3 = O(1). QED.

---

## The Word-RAM Model

The **word-RAM model** is the standard computational model for analyzing algorithms
in practice. Key assumptions:

1. **Word size `w`:** The machine operates on words of `w` bits, where `w >= log n`
   (enough bits to address the input).

2. **O(1) operations on words:**
   - Arithmetic: addition, subtraction, multiplication, division.
   - Bitwise: AND, OR, XOR, NOT, shifts.
   - Comparison: equality, less-than.
   - Memory: read/write a word at any address.

3. **Memory:** Unlimited flat address space with O(1) random access.

**Why this matters for O(1):**

Many "O(1)" claims depend on the word-RAM model. For example:
- Array access is O(1) because address arithmetic is O(1) on word-sized operands.
- Hash computation is O(1) when keys fit in O(1) words.
- Integer comparison is O(1) for word-sized integers.

**When the model breaks:**
- Arbitrary-precision integers: Addition of `k`-bit numbers is O(k/w), not O(1).
- Python's integers are arbitrary precision, so `a + b` on very large integers is NOT O(1).
- Strings of length `k`: Hashing is O(k), not O(1), unless the string fits in O(1) words.

---

## Perfect Hashing -- O(1) Worst Case

Standard hash tables are O(1) expected (average case) but O(n) worst case. **Perfect
hashing** achieves O(1) **worst case** for a static set of keys.

### Static Perfect Hashing

A **perfect hash function** for a set `S` of `n` keys is a hash function `h: S -> {0, ..., m-1}`
with **no collisions**: `h(x) != h(y)` for all `x != y` in `S`.

A **minimal perfect hash function** achieves `m = n` (table size equals number of keys).

### FKS Hashing Scheme

The **Fredman-Komloss-Szemeredi (FKS)** scheme (1984) achieves:
- O(1) **worst-case** lookup time.
- O(n) space.
- O(n) expected construction time.

**Two-level structure:**

**Level 1:** Hash `n` keys into `m = n` buckets using a universal hash function `h1`.
Let `b_j` = number of keys hashing to bucket `j`.

**Level 2:** For each bucket `j`, create a secondary hash table of size `b_j^2` with
its own universal hash function `h_j`. By the birthday paradox, a random function into
a table of size `k^2` is collision-free with probability >= 1/2 for `k` keys.

**Space analysis:**

By universality of `h1`, `E[sum(b_j^2)] = O(n)`. If `sum(b_j^2) > cn` for some
constant `c`, re-choose `h1`. Expected number of trials is O(1).

Total space: `O(n)` for the primary table + `O(sum(b_j^2))` = O(n) for secondary tables.

**Lookup:**
1. Compute `j = h1(key)` -- O(1).
2. Compute `k = h_j(key)` -- O(1).
3. Check if `table[j][k] == key` -- O(1).

Total: O(1) worst case.

#### Go

```go
package main

import (
    "fmt"
    "math/rand"
)

// Simplified FKS-style perfect hash for demonstration.
// In production, use a proper universal hash family.
type FKSHashTable struct {
    primary    [][]entry
    hashA      int
    hashB      int
    tableSize  int
    secondaryA []int
    secondaryB []int
    secondaryS []int
}

type entry struct {
    key   int
    value string
    used  bool
}

func universalHash(key, a, b, m int) int {
    // h(k) = ((a*k + b) mod p) mod m  where p is a large prime
    p := 1000000007
    return ((a*key+b)%p + p) % p % m
}

func BuildFKS(keys []int, values []string) *FKSHashTable {
    n := len(keys)
    m := n
    ht := &FKSHashTable{tableSize: m}

    // Level 1: find a hash function that gives sum(b_j^2) <= 4n
    for {
        ht.hashA = rand.Intn(999999) + 1
        ht.hashB = rand.Intn(999999)
        buckets := make([][]int, m)
        bucketVals := make([][]string, m)

        for i, k := range keys {
            j := universalHash(k, ht.hashA, ht.hashB, m)
            buckets[j] = append(buckets[j], k)
            bucketVals[j] = append(bucketVals[j], values[i])
        }

        sumSquares := 0
        for _, b := range buckets {
            sumSquares += len(b) * len(b)
        }
        if sumSquares <= 4*n {
            // Build level 2
            ht.primary = make([][]entry, m)
            ht.secondaryA = make([]int, m)
            ht.secondaryB = make([]int, m)
            ht.secondaryS = make([]int, m)

            for j := 0; j < m; j++ {
                bj := len(buckets[j])
                if bj == 0 {
                    continue
                }
                s := bj * bj
                ht.secondaryS[j] = s
                // Find collision-free secondary hash
                for {
                    a2 := rand.Intn(999999) + 1
                    b2 := rand.Intn(999999)
                    table := make([]entry, s)
                    collision := false
                    for idx, k := range buckets[j] {
                        pos := universalHash(k, a2, b2, s)
                        if table[pos].used {
                            collision = true
                            break
                        }
                        table[pos] = entry{key: k, value: bucketVals[j][idx], used: true}
                    }
                    if !collision {
                        ht.primary[j] = table
                        ht.secondaryA[j] = a2
                        ht.secondaryB[j] = b2
                        break
                    }
                }
            }
            return ht
        }
    }
}

// Lookup is O(1) WORST CASE -- two hash computations and one comparison
func (ht *FKSHashTable) Lookup(key int) (string, bool) {
    j := universalHash(key, ht.hashA, ht.hashB, ht.tableSize)
    if ht.primary[j] == nil {
        return "", false
    }
    s := ht.secondaryS[j]
    pos := universalHash(key, ht.secondaryA[j], ht.secondaryB[j], s)
    e := ht.primary[j][pos]
    if e.used && e.key == key {
        return e.value, true
    }
    return "", false
}

func main() {
    keys := []int{10, 22, 37, 40, 52, 60, 70, 82}
    vals := []string{"ten", "twenty-two", "thirty-seven", "forty",
        "fifty-two", "sixty", "seventy", "eighty-two"}

    ht := BuildFKS(keys, vals)

    for _, k := range keys {
        v, ok := ht.Lookup(k)
        fmt.Printf("Lookup(%d) = %q, found=%v\n", k, v, ok)
    }

    // Lookup for a key not in the set
    v, ok := ht.Lookup(99)
    fmt.Printf("Lookup(99) = %q, found=%v\n", v, ok)
}
```

#### Java

```java
import java.util.*;

public class FKSHashTable {
    private static final int PRIME = 1000000007;
    private int hashA, hashB, tableSize;
    private int[] secondaryA, secondaryB, secondaryS;
    private int[][] keyTable;
    private String[][] valTable;
    private boolean[][] usedTable;

    static int universalHash(int key, int a, int b, int m) {
        long h = ((long) a * key + b) % PRIME;
        if (h < 0) h += PRIME;
        return (int) (h % m);
    }

    public FKSHashTable(int[] keys, String[] values) {
        Random rng = new Random();
        int n = keys.length;
        tableSize = Math.max(n, 1);

        while (true) {
            hashA = rng.nextInt(999999) + 1;
            hashB = rng.nextInt(999999);
            List<List<Integer>> buckets = new ArrayList<>();
            List<List<String>> bucketVals = new ArrayList<>();
            for (int i = 0; i < tableSize; i++) {
                buckets.add(new ArrayList<>());
                bucketVals.add(new ArrayList<>());
            }
            for (int i = 0; i < n; i++) {
                int j = universalHash(keys[i], hashA, hashB, tableSize);
                buckets.get(j).add(keys[i]);
                bucketVals.get(j).add(values[i]);
            }
            int sumSq = 0;
            for (var b : buckets) sumSq += b.size() * b.size();
            if (sumSq > 4 * n) continue;

            secondaryA = new int[tableSize];
            secondaryB = new int[tableSize];
            secondaryS = new int[tableSize];
            keyTable = new int[tableSize][];
            valTable = new String[tableSize][];
            usedTable = new boolean[tableSize][];

            for (int j = 0; j < tableSize; j++) {
                int bj = buckets.get(j).size();
                if (bj == 0) continue;
                int s = bj * bj;
                secondaryS[j] = s;
                while (true) {
                    int a2 = rng.nextInt(999999) + 1;
                    int b2 = rng.nextInt(999999);
                    int[] kt = new int[s];
                    String[] vt = new String[s];
                    boolean[] ut = new boolean[s];
                    boolean collision = false;
                    for (int idx = 0; idx < bj; idx++) {
                        int pos = universalHash(buckets.get(j).get(idx), a2, b2, s);
                        if (ut[pos]) { collision = true; break; }
                        kt[pos] = buckets.get(j).get(idx);
                        vt[pos] = bucketVals.get(j).get(idx);
                        ut[pos] = true;
                    }
                    if (!collision) {
                        secondaryA[j] = a2;
                        secondaryB[j] = b2;
                        keyTable[j] = kt;
                        valTable[j] = vt;
                        usedTable[j] = ut;
                        break;
                    }
                }
            }
            return;
        }
    }

    // O(1) worst-case lookup
    public String lookup(int key) {
        int j = universalHash(key, hashA, hashB, tableSize);
        if (keyTable[j] == null) return null;
        int pos = universalHash(key, secondaryA[j], secondaryB[j], secondaryS[j]);
        if (usedTable[j][pos] && keyTable[j][pos] == key) return valTable[j][pos];
        return null;
    }

    public static void main(String[] args) {
        int[] keys = {10, 22, 37, 40, 52, 60, 70, 82};
        String[] vals = {"ten", "twenty-two", "thirty-seven", "forty",
            "fifty-two", "sixty", "seventy", "eighty-two"};
        FKSHashTable ht = new FKSHashTable(keys, vals);
        for (int k : keys) {
            System.out.printf("Lookup(%d) = %s%n", k, ht.lookup(k));
        }
        System.out.printf("Lookup(99) = %s%n", ht.lookup(99));
    }
}
```

#### Python

```python
import random

PRIME = 1000000007

def universal_hash(key, a, b, m):
    return ((a * key + b) % PRIME) % m

def build_fks(keys, values):
    """Build an FKS perfect hash table. O(n) expected time."""
    n = len(keys)
    m = max(n, 1)

    while True:
        a1 = random.randint(1, 999999)
        b1 = random.randint(0, 999999)
        buckets = [[] for _ in range(m)]
        bucket_vals = [[] for _ in range(m)]

        for i, k in enumerate(keys):
            j = universal_hash(k, a1, b1, m)
            buckets[j].append(k)
            bucket_vals[j].append(values[i])

        sum_sq = sum(len(b) ** 2 for b in buckets)
        if sum_sq > 4 * n:
            continue

        secondary = [None] * m
        for j in range(m):
            bj = len(buckets[j])
            if bj == 0:
                continue
            s = bj * bj
            while True:
                a2 = random.randint(1, 999999)
                b2 = random.randint(0, 999999)
                table = [None] * s
                collision = False
                for idx, k in enumerate(buckets[j]):
                    pos = universal_hash(k, a2, b2, s)
                    if table[pos] is not None:
                        collision = True
                        break
                    table[pos] = (k, bucket_vals[j][idx])
                if not collision:
                    secondary[j] = (a2, b2, s, table)
                    break

        return (a1, b1, m, secondary)


def fks_lookup(ht, key):
    """O(1) worst-case lookup."""
    a1, b1, m, secondary = ht
    j = universal_hash(key, a1, b1, m)
    if secondary[j] is None:
        return None
    a2, b2, s, table = secondary[j]
    pos = universal_hash(key, a2, b2, s)
    if table[pos] is not None and table[pos][0] == key:
        return table[pos][1]
    return None


keys = [10, 22, 37, 40, 52, 60, 70, 82]
vals = ["ten", "twenty-two", "thirty-seven", "forty",
        "fifty-two", "sixty", "seventy", "eighty-two"]

ht = build_fks(keys, vals)

for k in keys:
    print(f"Lookup({k}) = {fks_lookup(ht, k)}")
print(f"Lookup(99) = {fks_lookup(ht, 99)}")
```

### Cuckoo Hashing

Cuckoo hashing is another approach to O(1) worst-case lookups:

- Uses two hash functions `h1` and `h2`.
- Each key is in exactly one of two possible positions: `h1(key)` or `h2(key)`.
- **Lookup: O(1) worst case** -- check exactly two positions.
- **Insert: O(1) amortized** -- may trigger a chain of evictions, but amortized O(1).
- **Space: O(n)** with load factor up to ~50%.

---

## Theoretical Limits

**Lower bound:** Any data structure that supports membership queries on a set of `n`
elements from a universe of size `u` requires `Omega(n log(u/n))` bits of space. This
is the **information-theoretic lower bound**.

**Cell-probe lower bound:** In the cell-probe model, any data structure that answers
membership queries in `t` probes requires `Omega(n log(u/n) / t)` bits.

**Practical implication:** O(1) worst-case lookup is achievable with O(n) space for
static sets (FKS, cuckoo hashing). For dynamic sets, expected O(1) with O(n) space
(standard hash tables) or O(1) worst-case with O(n) space (dynamic perfect hashing,
de la Brandais, Dietzfelbinger et al.).

---

## Key Takeaways

1. **The formal definition** of O(1) is simple: `T(n) <= c` for some constant `c` and
   all sufficiently large `n`.

2. **The word-RAM model** is the standard framework for analyzing O(1) claims. It
   assumes word-sized operations take constant time.

3. **The potential method** provides rigorous amortized O(1) proofs for dynamic arrays,
   hash table resizing, and other amortized data structures.

4. **FKS hashing** achieves O(1) worst-case lookup with O(n) space for static key sets,
   using a two-level hashing scheme.

5. **Cuckoo hashing** provides O(1) worst-case lookups for dynamic sets with O(1)
   amortized insertions.

6. **Perfect hashing** is not just theoretical -- it is used in compilers (keyword
   lookup), databases (static indices), and networking (packet classification).

7. **Python's arbitrary-precision integers** break the word-RAM assumption. Operations
   on very large integers are NOT O(1).
