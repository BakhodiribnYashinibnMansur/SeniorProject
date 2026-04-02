# Hash Table — Professional / Theoretical Level

## Table of Contents

1. [Universal Hashing — Formal Treatment](#universal-hashing--formal-treatment)
2. [Perfect Hashing (FKS Scheme)](#perfect-hashing-fks-scheme)
3. [Expected Chain Length Analysis](#expected-chain-length-analysis)
4. [Amortized Rehashing Analysis](#amortized-rehashing-analysis)
5. [Cuckoo Hashing Analysis](#cuckoo-hashing-analysis)
6. [Tabulation Hashing](#tabulation-hashing)
7. [Lower Bounds for Hashing](#lower-bounds-for-hashing)
8. [Advanced Topics](#advanced-topics)

---

## Universal Hashing — Formal Treatment

### Definition

A family of hash functions `H` mapping universe `U` to `{0, 1, ..., m-1}` is **universal** if for any two distinct keys `x, y in U`:

```
Pr[h(x) = h(y)] <= 1/m   (where h is chosen uniformly at random from H)
```

### Construction: Carter-Wegman

For keys in `Z_p = {0, 1, ..., p-1}` where `p` is prime:

```
h_{a,b}(x) = ((a * x + b) mod p) mod m
```

where `a in {1, ..., p-1}` and `b in {0, ..., p-1}` are chosen randomly.

**Proof of universality**:

Let `x != y` be two distinct keys. Consider:
```
h_{a,b}(x) = h_{a,b}(y)
=> (ax + b) mod p ≡ (ay + b) mod p  (mod m)
=> a(x - y) mod p ≡ 0  (mod m)
```

Since `p` is prime and `a != 0`, the value `a(x - y) mod p` is uniformly distributed over `{1, ..., p-1}` as `a` ranges over `{1, ..., p-1}` (for fixed `x != y`). For any fixed value `z in Z_p`, the number of values in `{0, ..., p-1}` that map to the same bucket modulo `m` is at most `ceil(p/m)`. Therefore:

```
Pr[h(x) = h(y)] <= ceil(p/m) / (p-1) <= (p/m + 1) / (p-1)
```

For `p >> m`, this approaches `1/m`.

### Strongly Universal (2-Independent) Hashing

A family is **strongly universal** (or 2-independent) if for any two distinct keys `x, y` and any two hash values `s, t`:

```
Pr[h(x) = s AND h(y) = t] = 1/m^2
```

The Carter-Wegman family `h_{a,b}(x) = (ax + b) mod p mod m` is 2-independent when `p = m` is prime.

### k-Independent Hashing

A family is **k-independent** if for any k distinct keys, their hash values are uniformly and independently distributed. A degree-(k-1) polynomial hash gives k-independence:

```
h_{a0,...,a_{k-1}}(x) = (a_0 + a_1*x + a_2*x^2 + ... + a_{k-1}*x^{k-1}) mod p mod m
```

Higher independence provides stronger concentration bounds on chain lengths but requires more random bits to store the hash function.

---

## Perfect Hashing (FKS Scheme)

**Goal**: O(1) worst-case lookup with O(n) space for a static set of n keys.

### Two-Level FKS Construction (Fredman, Komlós, Szemerédi, 1984)

**Level 1**: Choose a universal hash function `h` mapping n keys to m = n buckets. Let `n_j` be the number of keys hashing to bucket `j`.

**Level 2**: For each bucket `j`, construct a secondary hash table of size `m_j = n_j^2` with its own universal hash function `h_j`. By the birthday paradox, a random function into `n_j^2` slots has no collisions with probability >= 1/2. So we try a few random functions until one is collision-free.

**Space analysis**:

The total space for secondary tables is `sum(n_j^2)`. We need to show this is O(n).

**Theorem**: If `h` is chosen from a universal family and `m = n`, then:

```
E[sum_{j=0}^{m-1} n_j^2] = n + n(n-1)/m <= 2n
```

**Proof**:
```
sum(n_j^2) = sum(n_j) + 2 * C(n_j, 2) summed over all j
           = n + 2 * (number of collisions)

E[number of collisions] = C(n, 2) * Pr[collision for a pair]
                        <= C(n, 2) * 1/m
                        = n(n-1) / (2m)

E[sum(n_j^2)] = n + 2 * n(n-1)/(2m) = n + n(n-1)/m
```

For `m = n`: `E[sum(n_j^2)] = n + (n-1) < 2n`. So expected total space is O(n).

**Lookup**: Compute `h(key)` to find bucket j, then `h_j(key)` to find the slot. Two hash computations = O(1) worst case.

**Limitation**: Static — the key set must be known in advance. Dynamic perfect hashing (Dietzfelbinger et al.) allows insertions and deletions in amortized O(1).

---

## Expected Chain Length Analysis

For separate chaining with n keys and m buckets, using a universal hash family.

### Average Case

Load factor `alpha = n/m`. Expected chain length for a random bucket = alpha.

**Expected search time for a key present in the table**:

The key's chain has expected length `1 + (n-1)/m` (the key itself plus the expected number of other keys in its bucket).

```
E[search time for present key] = 1 + (n-1)/m = 1 + alpha - 1/m ≈ 1 + alpha
```

For `alpha = O(1)`, this is O(1).

### Maximum Chain Length

With universal hashing (2-independent):
```
E[max chain length] = O(sqrt(n))  (for m = n)
```

With O(log n)-independent hashing:
```
Pr[max chain length > c * log(n)/log(log(n))] < 1/n^c
```

This matches the bound achieved by truly random hash functions: the maximum chain length is `Theta(log n / log log n)` with high probability.

### Balls-into-Bins Analysis

Hashing n keys into m = n buckets is equivalent to throwing n balls into n bins.

- **Expected maximum load**: `Theta(log n / log log n)` (with truly random hashing).
- **With "Power of Two Choices"** (check 2 bins, place in the emptier one): maximum load drops to `Theta(log log n)` — an exponential improvement.

---

## Amortized Rehashing Analysis

### Table Doubling

When the load factor exceeds a threshold, we double the table size and rehash all n elements. The cost of rehashing is O(n). We analyze the amortized cost per insertion.

**Aggregate method**:

Starting from an empty table of size 1, after n insertions the table has been doubled `log(n)` times. Total rehashing cost:

```
1 + 2 + 4 + 8 + ... + n = 2n - 1 = O(n)
```

Adding the n individual insertions (each O(1) assuming no resize):

```
Total cost = n + O(n) = O(n)
Amortized cost per insertion = O(1)
```

**Potential method**:

Define potential `Phi = 2 * size - capacity`.

- After a resize: `size = capacity/2`, so `Phi = 2*(capacity/2) - capacity = 0`.
- Just before a resize: `size = capacity`, so `Phi = 2*capacity - capacity = capacity`.

Amortized cost of insert (no resize): `c_hat = 1 + delta_Phi = 1 + 2 = 3`.
Amortized cost of insert (with resize): `c_hat = (1 + n) + (0 - n) = 1` (actual cost n+1, potential drops by n).

So every insert has amortized cost O(1). The potential "saves up" credit during cheap inserts and "spends" it during expensive resizes.

### Shrinking

If we also halve the table when the load factor drops below 1/4 (not 1/2 — this is important!):

- **Why 1/4 and not 1/2?** If we shrink at 1/2, a sequence of alternating inserts and deletes at the threshold causes repeated resize/shrink cycles, each costing O(n). Shrinking at 1/4 avoids this pathology.
- Amortized cost per operation remains O(1) with the 1/4 threshold.

---

## Cuckoo Hashing Analysis

### Setup

Two tables T1, T2 of size m each. Two hash functions h1, h2 from a universal family.

Insert(x): place at T1[h1(x)]. If occupied, evict the occupant and re-insert it at its alternate position (T2[h2(occupant)]). Continue until finding an empty slot or detecting a cycle.

### Expected Insertion Time

**Theorem (Pagh and Rodler, 2004)**: With load factor `alpha < 1/2` (combined) and hash functions from an O(log n)-independent family:

- Expected insertion time is O(1).
- The probability of a cycle (requiring rehash) is O(1/n^2) per insertion.
- Expected amortized cost including rehashes: O(1).

**Proof sketch**: Model the process as a random walk on a "cuckoo graph" where vertices are table positions and edges connect h1(x) and h2(x) for each key x. A cycle in this graph corresponds to an insertion failure. For alpha < 1/2, the cuckoo graph is sparse and the expected component size is O(1). The probability that any component contains more than one cycle is O(1/n^2).

### Lookup Time

Always O(1) worst case: check T1[h1(key)] and T2[h2(key)]. At most 2 memory accesses.

### Space Utilization

Maximum load factor is slightly below 50% for two tables. With 3 hash functions and a small stash, utilization reaches ~91%.

---

## Tabulation Hashing

### Simple Tabulation

Partition a key into c chunks of r bits each (key has c*r bits). Pre-compute c random tables, each mapping r-bit values to hash values. The hash is the XOR of the table lookups:

```
h(x) = T_1[x_1] XOR T_2[x_2] XOR ... XOR T_c[x_c]
```

where `x_i` is the i-th chunk of x.

**Properties**:
- Only 3-independent (not 4-independent).
- Despite limited independence, simple tabulation provides strong guarantees:
  - Chernoff-like concentration for bin loads.
  - Expected O(1) for linear probing (Patrascu and Thorup, 2012).
  - Works for cuckoo hashing.
- **Very fast**: c table lookups and c-1 XOR operations.

### Twisted Tabulation

A variant that achieves stronger guarantees (similar to 5-independent) while keeping the same speed. Modifies the last table lookup to depend on intermediate XOR results.

### Double Tabulation

Apply simple tabulation twice: `h(x) = g(f(x))` where both f and g are simple tabulation hash functions. This achieves high independence (approaching full randomness) and is still practical.

---

## Lower Bounds for Hashing

### Static Dictionary Lower Bound

**Theorem (Miltersen, 1999)**: Any static dictionary supporting O(1) worst-case queries must use at least `n * log(U/n) + n - O(log n)` bits of space (where U is the universe size and n is the number of stored keys). This is close to the information-theoretic minimum.

### Cell-Probe Lower Bounds

In the cell-probe model (where we count only memory accesses, not computation):

**Theorem (Patrascu, 2008)**: For dynamic dictionaries with n keys from a universe of size U, any data structure using S bits of space requires:

```
query time >= Omega(log(n) / log(S/n))
```

in the worst case, for `S = n * polylog(n)`.

This means O(1) query time requires `S = n^(1+epsilon)` space for some `epsilon > 0` — you cannot achieve both optimal space and optimal time simultaneously.

### Hashing with Worst-Case Guarantees

**Theorem**: With truly random hash functions and separate chaining, the maximum bucket load is `Theta(log n / log log n)` with high probability. No data-oblivious hash function can do better in the worst case.

**The power of two choices** breaks this barrier by using two hash functions and placing each key in the less-loaded bucket, achieving `O(log log n)` maximum load.

---

## Advanced Topics

### Hopscotch Hashing

Combines ideas from linear probing and cuckoo hashing. Each bucket has a "neighborhood" of H consecutive buckets. An entry must reside within its neighborhood. On collision, entries are displaced within neighborhoods using a hopping strategy.

- **Benefits**: Cache-friendly (bounded neighborhood), concurrent-friendly (each neighborhood is an independent lock scope).
- **Lookup**: Check at most H positions = O(1) worst case (for fixed H).

### Swiss Table (abseil::flat_hash_map)

Google's production hash table used in Abseil (C++) and adapted in many other languages:

- Uses SIMD (SSE2/AVX) instructions to probe 16 slots in parallel.
- Metadata array: 1 byte per slot storing the top 7 bits of the hash (for fast filtering) + 1 control bit (empty/deleted/full).
- Dramatically reduces branch mispredictions compared to traditional probing.

### Robin Hood Hashing — Formal Analysis

**Theorem (Devroye and Morin, 2003)**: With Robin Hood linear probing and load factor alpha < 1:

```
Expected maximum probe distance = O(log log n)
Variance of probe distance = O(1)
```

This is much tighter than standard linear probing, where maximum probe distance is `Theta(log n)`.

### Hash Flooding Attacks and Mitigations

Adversarial inputs can cause all keys to hash to the same bucket, degrading O(1) to O(n).

**Mitigations**:
1. **Randomized hash functions** (SipHash): Keyed hash function where the key is chosen randomly at table creation. The adversary cannot predict which keys collide without knowing the secret key.
2. **Treeification** (Java 8+): Convert long chains to balanced BSTs, guaranteeing O(log n) worst case.
3. **Universal hashing**: Theoretical guarantee of `1/m` collision probability for any pair.
4. **Per-process randomization**: Python randomizes hash seeds per process (since 3.3, made default in 3.6).

---

## Summary of Theoretical Guarantees

| Scheme | Lookup | Insert | Space | Independence Required |
|--------|--------|--------|-------|----------------------|
| Chaining (universal) | O(1) expected | O(1) expected | O(n) | 2-independent |
| FKS perfect hashing | O(1) worst case | Static | O(n) | Universal |
| Cuckoo hashing | O(1) worst case | O(1) amortized | O(n) | O(log n)-independent |
| Linear probing | O(1) expected | O(1) expected | O(n) | 5-independent (tight) |
| Robin Hood | O(1) expected, low variance | O(1) amortized | O(n) | 5-independent |
| Power of 2 choices | O(log log n) max load | O(1) expected | O(n) | 2-independent |
