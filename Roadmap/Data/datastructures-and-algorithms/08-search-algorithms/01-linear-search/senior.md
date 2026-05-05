# Linear Search — Senior Level

## Table of Contents

1. [Production Usage](#production-usage)
2. [Database Table Scans](#database-table-scans)
3. [Full-Text Search Foundations](#full-text-search-foundations)
4. [grep, awk, ag, ripgrep — Internals](#grep-awk-ag-ripgrep--internals)
5. [When to NOT Use an Index](#when-to-not-use-an-index)
6. [CPU-Friendly Access Patterns](#cpu-friendly-access-patterns)
7. [`contains()` in Standard Libraries](#contains-in-standard-libraries)
8. [Network Packet Filtering](#network-packet-filtering)
9. [Anti-Patterns at Scale](#anti-patterns-at-scale)
10. [Summary](#summary)

---

## Production Usage

Linear search is **everywhere** in production code. It's not a "beginner" algorithm — it's the default for any small or unstructured collection. A non-exhaustive list:

- **Programming language standard libraries** (`contains`, `indexOf`, `find`, `in`).
- **Database engines** doing sequential scans on tables without applicable indexes.
- **Search engines** scanning postings lists inside an inverted index.
- **Compilers** searching small symbol tables before hashing them.
- **Operating systems** scanning small file descriptor tables, process lists, kernel module lists.
- **Network stacks** scanning iptables/nftables rules per packet.
- **Game engines** scanning entity lists for collisions, AI targets, hitboxes.
- **Web servers** scanning URL routing tables (small ones).
- **Build tools** (`make`, `bazel`) scanning rule lists.
- **`grep`, `ripgrep`, `ag`** — entire purpose is large-scale linear scan.

The pattern: **whenever you have a small or unindexed collection and need to find one element, you do a linear search.** The constant factors are so low that for `n < 100`, no fancier algorithm pays off.

---

## Database Table Scans

### Sequential Scans (PostgreSQL, MySQL, etc.)

When a SQL query has no usable index, the query planner falls back to a **full table scan** — read every row, evaluate the `WHERE` predicate, keep matches. This is **linear search at the row level.**

```sql
EXPLAIN SELECT * FROM orders WHERE total > 500;
-- If no index on (total), planner emits:
-- Seq Scan on orders  (cost=0.00..15234.00 rows=1234 width=...)
```

**Why this is sometimes the right choice:**

1. **Small tables.** A table with < 1000 rows fits in a single 8KB page (or two). Reading sequentially is faster than even a single index lookup.
2. **Low selectivity.** If the predicate matches 30%+ of rows, the planner often chooses a seq scan because random index lookups would be slower than sequential disk reads.
3. **No applicable index.** If you query `WHERE LOWER(name) = 'bob'` but the index is on `name`, the index doesn't apply — seq scan.

PostgreSQL uses **synchronized seq scans**: if multiple queries are scanning the same large table, they share the buffer pool reads. Linear scan + cooperative readers → near-disk-throughput.

### Page-Level vs Row-Level

A "linear search" at the database level operates on **pages** (typically 4-16 KB blocks), not individual rows:

1. The storage layer reads pages sequentially from disk (fast — no seeks).
2. For each page, it iterates the rows within that page — another linear scan, but in-memory and tiny.
3. The query executor evaluates the predicate on each row.

The cost model is approximately:
```
cost ≈ pages_to_read * SEQ_PAGE_COST + rows * CPU_TUPLE_COST
```

**Key insight:** sequential reads are 10-100× faster than random reads on spinning disks (and 5-10× on SSDs due to read-ahead). So a "slow" linear scan can beat a "fast" random-IO index lookup when the table is large but mostly read sequentially.

### Heap vs Index Scan Decision

Postgres planner formula (simplified):

| Scenario | Seq Scan Cost | Index Scan Cost | Winner |
|----------|--------------|-----------------|--------|
| Small table (< 1000 rows) | Low | Index overhead | **Seq scan** |
| 30%+ selectivity | Medium | High random IO | **Seq scan** |
| < 1% selectivity | High | Low | **Index scan** |
| LIKE 'pat%' on indexed col | High | Low | **Index scan** |
| LIKE '%pat%' (no leading anchor) | High | Inapplicable | **Seq scan** |

Senior engineers learn to **read EXPLAIN plans** and understand when seq scans are correct (don't index everything).

---

## Full-Text Search Foundations

Before inverted indexes (Lucene, Elasticsearch), full-text search was **literally `grep` on documents**: read each document linearly, check if the query term appears. This is O(n) where n = total document text size.

For a small corpus (a single user's notes, a wiki with 1000 pages), this is **still the right approach**. Inverted-index overhead (build cost, storage, update complexity) is wasted.

### Grep-Based Search Pattern

```python
def search_documents(documents, query):
    """Linear scan over documents; per-doc linear scan for query substring."""
    results = []
    for doc in documents:
        if query in doc.content:    # Python's `in` on str = optimized linear search
            results.append(doc)
    return results
```

For 100 documents of 10 KB each, that's 1 MB scanned per query — **microseconds** on modern hardware.

### When Inverted Indexes Take Over

You graduate to an inverted index (Lucene-style) when:
- Corpus exceeds ~10 MB.
- Query frequency exceeds ~10/sec.
- You need ranking (TF-IDF, BM25), faceting, fuzzy matching.

But the **postings list scan** inside an inverted index is itself a linear search over a (compressed, sorted) list of document IDs. Linear search never goes away — it just moves to a smaller, more focused level.

### Hybrid: Index + Filter

A common pattern: index the cheap fields (status, tags, date range), then **linearly scan** the result for the expensive predicate:

```sql
SELECT * FROM logs
WHERE date >= '2026-05-01'    -- index narrows to 10K rows
  AND request_path LIKE '%admin%'   -- linear scan on those 10K
;
```

The planner uses the index to get a candidate set, then a linear scan applies the un-indexable predicate. This is **filter-after-index** — production querying 101.

---

## grep, awk, ag, ripgrep — Internals

These are **the** tools for linear search at scale:

### `grep` (1973, BSD/GNU)

- **Boyer-Moore** for fixed strings (not strict linear, but linear in worst case).
- **Aho-Corasick** for multi-pattern matching.
- **NFA / DFA** for regex.
- Reads files in 32-128 KB blocks; scans memory linearly.
- Uses `mmap` on supported systems to skip kernel-userspace copy.

### `ripgrep` (Rust, 2016)

- **SIMD-accelerated** literal search via `memchr` (uses AVX2 / NEON).
- Parallel: scans multiple files concurrently using Rayon.
- Skips `.gitignore`d files by default.
- Routinely ~5× faster than `grep` on the same hardware.

The core inner loop is **literally linear search** — vectorized, but linear:

```rust
// Conceptual ripgrep inner loop
for chunk in mmap.chunks(64 * 1024) {
    if let Some(pos) = memchr_simd(chunk, b'X') {
        // Found candidate; verify full pattern.
        ...
    }
}
```

`memchr` is a hand-optimized assembly routine that finds a byte in a buffer at near-memory-bandwidth speed (~10-30 GB/s on modern x86).

### Why Linear Wins Here

Source code corpora are **unsorted text**. There is no way to "binary search" for a substring without first building an index — and indexing source code is expensive (every commit invalidates the index). For interactive use, linear scan with SIMD is **fast enough** and **always up-to-date**.

A 100 MB codebase + ripgrep = ~100 ms search. That's 1 GB/s throughput. Not bad for "the simplest algorithm in CS."

---

## When to NOT Use an Index

A senior engineer's superpower is knowing when **less infrastructure is better**.

### Case 1: Tables Smaller than Page Size

If a table fits in 1-2 pages, sequential scan is faster than index lookup. Indexing actually **slows queries down** because:
- Index lookup is 1 random IO + 1 table IO = 2 IOs.
- Seq scan is 1 sequential IO.

### Case 2: Low Selectivity (>30% rows match)

Index scans require **random IO** for each match (jumping between leaf pages and heap pages). At >30% selectivity, you're doing more random IO than a sequential scan would do total IO. Postgres planner switches to seq scan automatically — but if it doesn't, hint with `SET enable_indexscan = off` and benchmark.

### Case 3: Predicates the Index Can't Use

```sql
WHERE upper(email) = 'BOB@EX.COM'    -- index on email is useless
WHERE created_at::date = '2026-05-01' -- index on created_at is useless
WHERE name LIKE '%bob%'              -- index can't help middle-substring
```

Either:
- Add a **functional index** (`CREATE INDEX ON users (upper(email))`).
- Accept the seq scan.
- Add a separate **denormalized column** with the precomputed value.

### Case 4: Write-Heavy Tables

Every index slows writes by 5-30%. For tables with 1000s of writes/sec and rare reads, indexes are a tax with no benefit. A `pg_stat_user_indexes` query shows index usage — drop indexes with `idx_scan = 0`.

### Case 5: One-Off Analytics

If a query runs **once a quarter**, building an index for it is foolish. Let it seq-scan. The planner is fine; the query takes a minute; you go drink coffee.

### Case 6: OLAP / Columnar Engines

Column stores (ClickHouse, DuckDB, Parquet + Spark) **don't use B-tree indexes**. They rely on:
- Sequential scans (very fast — columns are contiguous).
- Block-skipping via min/max statistics per chunk.
- Bitmap indexes for low-cardinality columns.

The whole architecture is "make seq scan blazingly fast" rather than "skip data via index."

---

## CPU-Friendly Access Patterns

Linear search is **the** access pattern modern CPUs are optimized for. To stay friendly:

### Sequential, Not Strided

```c
// Cache-friendly: iterates 1 cache line per 16 iterations (int32).
for (i = 0; i < n; i++) {
    if (arr[i] == target) return i;
}

// Cache-hostile: each iteration touches a different cache line.
for (i = 0; i < n; i += 16) {
    if (arr[i] == target) return i;
}
```

The first version triggers the **hardware prefetcher**: the CPU notices the linear pattern and pre-loads cache lines before they're requested. The second version touches one element per cache line, wasting 60 of every 64 bytes.

### Struct-of-Arrays vs Array-of-Structs

```c
// Array of structs (AoS): each struct is 64 bytes.
struct Person { char name[32]; int age; int id; ... };
struct Person people[N];
// Searching by age touches all 64 bytes per person → 1 cache line per element.

// Struct of arrays (SoA):
int ages[N];
char* names[N];
int ids[N];
// Searching by age: 16 ages per cache line → 1 cache line per 16 elements.
```

Linear search through SoA is **16× more cache-efficient** than through AoS for single-field queries. Game engines use ECS (Entity Component System) precisely for this reason.

### Avoid Pointer Chasing

```c
// Linked list — each next pointer is unpredictable.
while (node) {
    if (node->value == target) return node;
    node = node->next;   // ← cache miss likely
}

// Array — predictable strides.
for (i = 0; i < n; i++) {
    if (arr[i] == target) return &arr[i];
}
```

A linked list scan can be **10-50× slower** than an array scan of the same length, even though both are O(n). The hardware prefetcher loves arrays and hates linked lists.

This is why Linus Torvalds (and many systems engineers) prefer arrays over linked lists. "The cache is the new disk."

---

## `contains()` in Standard Libraries

### Java

```java
public class ArrayList<E> {
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i] == null) return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i])) return i;
        }
        return -1;
    }
}
```

A textbook linear search. The null-handling fork is to avoid `NullPointerException` on `o.equals(null)`.

For `LinkedList<E>`, the same — but with pointer chasing, so several times slower.

For `HashSet<E>`, `contains` is O(1) — the right data structure for "does X exist."

### Python

```python
# `in` operator on a list:
3 in [1, 2, 3, 4]   # → True

# Implemented in C (Objects/listobject.c, list_contains):
# for (i = 0; i < Py_SIZE(a); i++) {
#     int cmp = PyObject_RichCompareBool(PyList_GET_ITEM(a, i), el, Py_EQ);
#     if (cmp != 0) return cmp;
# }
```

The C implementation skips the Python interpreter loop overhead, so it's ~50× faster than a Python-level `for` loop. But it's still O(n).

For `set`, `in` is O(1) average — same as `dict`.

### Go (1.21+)

```go
// slices.Contains
func Contains[S ~[]E, E comparable](s S, v E) bool {
    return Index(s, v) >= 0
}
func Index[S ~[]E, E comparable](s S, v E) int {
    for i := range s {
        if v == s[i] {
            return i
        }
    }
    return -1
}
```

For `[]byte`, Go has hand-tuned assembly (`bytes.IndexByte`) using SSE/AVX/NEON — same trick as `memchr`.

### JavaScript

```javascript
// V8 uses SIMD for Uint8Array/Int32Array:
const arr = new Int32Array([10, 20, 30, 40]);
arr.indexOf(30);   // SIMD-accelerated

// Generic Array uses a scalar loop with type-coerced equality:
[1, 2, 3].indexOf(2);    // → 1
[1, 2, 3].includes(2);   // → true (SameValueZero, handles NaN correctly)
```

`indexOf` uses `===`; `includes` uses `SameValueZero` (so `[NaN].includes(NaN)` returns `true`, but `[NaN].indexOf(NaN)` returns `-1`).

### Rust

```rust
// Vec::contains and slice::contains
impl<T: PartialEq> [T] {
    pub fn contains(&self, x: &T) -> bool {
        self.iter().any(|y| y == x)
    }
}
```

For `&[u8]`, Rust's `memchr` crate provides SIMD acceleration (same crate ripgrep uses).

---

## Network Packet Filtering

Linux's `iptables` (and BSD's `pf`, Cisco ACLs, etc.) walk a **rule list linearly** for every packet. A typical rule list has 10-1000 rules; each packet is matched against each rule in order until one matches.

```
Packet arrives
   │
   ▼
Rule 1: drop if src == 1.2.3.4   ← linear scan
Rule 2: accept if dst port == 80
Rule 3: drop if src in blocklist
   ...
Rule N: default policy
```

For high-throughput firewalls (millions of packets per second), linear rule scan is the bottleneck. Solutions:
- **`nftables`** — uses set-based matching for blocklists (O(1) hash lookup).
- **eBPF / XDP** — JIT-compiled rule trees that skip irrelevant rules.
- **Hardware acceleration** — SmartNICs match rules in silicon at line rate.

The takeaway: even at line rate, linear search is the **starting point** — and only when you exceed its limits do you reach for hashing or specialized hardware.

---

## Anti-Patterns at Scale

### Anti-Pattern 1: Linear Search Inside a Loop (Quadratic)

```python
for user in users:                    # n
    for order in orders:              # n
        if order.user_id == user.id:  # ← linear search inside loop
            ...
# Total: O(n × m) = O(n²) if m ≈ n
```

**Fix:** Build an index outside the outer loop:

```python
orders_by_user = {}
for order in orders:
    orders_by_user.setdefault(order.user_id, []).append(order)

for user in users:
    for order in orders_by_user.get(user.id, []):  # O(1) lookup
        ...
# Total: O(n + m)
```

This is the **N+1 query problem** in ORM disguise. Always preload or batch.

### Anti-Pattern 2: `list.contains()` in a Hot Loop

```java
// O(n²) — list.contains is O(n)
for (Item item : items) {
    if (allowedItems.contains(item)) { ... }   // ← O(n) per iteration
}
```

**Fix:** Convert `allowedItems` to a `HashSet`:

```java
Set<Item> allowed = new HashSet<>(allowedItems);
for (Item item : items) {
    if (allowed.contains(item)) { ... }   // ← O(1)
}
```

### Anti-Pattern 3: Searching for Existence When You Should Hash

If you're going to search the same collection repeatedly with different targets, **build a hash set once** instead of linear-searching N times.

```python
# Bad: O(n × m)
for target in targets:
    if target in big_list:   # O(n) each, m targets
        ...

# Good: O(n + m)
big_set = set(big_list)
for target in targets:
    if target in big_set:    # O(1) each
        ...
```

### Anti-Pattern 4: Linear Search on a Sorted Array

If the array is sorted, **use binary search**. The whole point of sorting is sub-linear search. Linear-searching a sorted array means you paid O(n log n) to sort and got nothing in return.

### Anti-Pattern 5: Recursive Linear Search

Don't recurse. Each call is a stack frame; for `n > 10000`, you'll blow the stack. Iterative linear search has zero stack pressure.

```python
# Bad: stack overflow at n ≈ 1000 in Python
def search(arr, target, i=0):
    if i >= len(arr): return -1
    if arr[i] == target: return i
    return search(arr, target, i + 1)

# Good
def search(arr, target):
    for i, v in enumerate(arr):
        if v == target: return i
    return -1
```

---

## Summary

Linear search at senior level is about **judgment**, not algorithms. You already know how to write the loop. The skill is knowing **when to use it, when to switch, and when production systems already do it for you.**

| Situation | Right Tool |
|-----------|-----------|
| Small list (n < 100) | Linear search |
| Repeated lookups, immutable data | Hash set |
| Sorted array | Binary search |
| Sorted on disk | B-tree index |
| Text search at small scale | Linear / regex |
| Text search at large scale | Inverted index (Lucene) |
| Database query, no index | Seq scan (let planner decide) |
| Real-time packet filter | nftables sets / eBPF |
| Source code search | ripgrep (SIMD linear) |

**Never** index reflexively. Indexes have a real cost (storage, write amplification, plan complexity). The default for small or write-heavy data is linear scan — and that's correct.

Continue to [`professional.md`](./professional.md) for the formal complexity analysis.
