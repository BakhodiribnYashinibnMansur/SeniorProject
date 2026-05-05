# Binary Search — Senior Level

> **Audience:** Engineers building or maintaining production systems where binary search is the underlying primitive — databases, version control, log processing, distributed indexes. Prerequisites: `junior.md`, `middle.md`.

This document treats binary search as **infrastructure**, not as a coding-interview puzzle. We cover where it actually lives in production: B-tree leaf node search, database index lookups, sorted-file scans, `git bisect`, distributed sorted indexes (Bigtable, Cassandra SSTables, RocksDB), and — crucially — the cases where binary search outperforms hash tables in real workloads, which is more often than the textbook comparison suggests.

---

## Table of Contents

1. [B-tree Leaf Node Search](#btree)
2. [Database Index Lookup](#db-index)
3. [Sorted File Binary Search](#sorted-file)
4. [Version Control Bisect (`git bisect`)](#git-bisect)
5. [Distributed Sorted Indexes](#distributed)
6. [When Binary Search Beats Hash Tables](#vs-hash)

---

<a name="btree"></a>
## 1. B-tree Leaf Node Search

A **B-tree** is a balanced multiway search tree where each node holds many keys (often 100–1000) instead of just one or two. They are the foundation of essentially every relational database index (PostgreSQL, MySQL InnoDB, SQL Server, Oracle), the file system metadata structure in NTFS / ReiserFS / HFS+ / APFS, and the core lookup structure inside MongoDB's WiredTiger and SQLite.

### Why so many keys per node?

Disk and SSD reads happen in **pages** — typically 4 KB on modern Linux, 8 KB in PostgreSQL, 16 KB in InnoDB. A B-tree sized so that one node fills one page minimizes the number of physical I/O operations per lookup. With 100 keys per node, a tree of depth 4 holds 100^4 = 100 million entries. Depth 5 holds 10 billion. The entire lookup is **at most 5 disk reads** for any database.

### Where binary search comes in

Inside one node, you have a sorted array of (key, child_pointer) pairs. To navigate to the next level, you need to find the largest key `<= search_key`. This is **binary search inside the node**. With 1000 keys per node, that's 10 comparisons per node × 5 nodes = 50 comparisons total — negligible CPU work compared to the disk I/O.

**PostgreSQL's `nbtsearch.c` snippet (paraphrased):**

```c
/* Binary search a B-tree page for the leftmost key >= scankey */
static OffsetNumber
_bt_binsrch(Relation rel, BTScanInsert key, Buffer buf)
{
    OffsetNumber low  = P_FIRSTDATAKEY(opaque);
    OffsetNumber high = PageGetMaxOffsetNumber(page);
    while (high > low) {
        OffsetNumber mid = low + ((high - low) / 2);
        if (_bt_compare(rel, key, page, mid) >= cmpval)
            low = mid + 1;
        else
            high = mid;
    }
    return low;
}
```

This is the exact `lower_bound` template from `junior.md`. **Every** index lookup in PostgreSQL passes through this function multiple times. It is one of the most-executed binary searches on Earth.

### Cache considerations

A 16 KB InnoDB page holds maybe 1000 keys. After 4–5 binary search probes, you're hitting the same cache line repeatedly — the comparisons run from L1 cache. The actual cost is dominated by the **first probe**, which often fetches a fresh cache line (~10 ns L2, ~100 ns L3, ~300 ns DRAM). Subsequent probes are nearly free.

This is why **fan-out matters more than tree depth**: a wider tree means fewer nodes touched (fewer cache misses), and the binary search inside each node is essentially free CPU.

### Linear scan vs binary search inside a node

Some databases (early SQLite, some embedded engines) actually use **linear scan** inside small nodes (< 32 keys). Why? Because:
- Branch prediction on a tight linear loop is near-perfect.
- SIMD comparisons (`_mm_cmpgt_epi32` on x86, `vceqq_s32` on ARM) compare 4–16 keys per cycle.
- Binary search has unpredictable branches.

The crossover point is usually 30–50 keys: above it, binary search wins; below it, linear (sometimes SIMD-accelerated linear) wins. Modern MySQL and PostgreSQL B-trees pack hundreds of keys per node, so binary search dominates.

---

<a name="db-index"></a>
## 2. Database Index Lookup

When you write:

```sql
SELECT * FROM events WHERE event_id = 12345;
```

and `event_id` has a B-tree index, the engine does the following (roughly):

1. **Root page binary search.** Find the child pointer for the range containing 12345.
2. **Descend** to the next level. Repeat binary search.
3. **Leaf node binary search.** Find the exact entry, which gives a row pointer (heap tuple TID in PostgreSQL, primary-key value in InnoDB clustered index).
4. **Heap fetch** (PostgreSQL) or done (InnoDB clustered, where leaf already holds the row).

For a 100-million-row table with B-tree depth 4, that is 4 disk I/Os in the worst case (more often 1–2 because the upper levels are cached in the buffer pool). The binary search within each page is O(log fanout) = O(10).

### Range queries

```sql
SELECT * FROM events WHERE event_time BETWEEN '2025-11-01' AND '2025-11-07';
```

The engine:
1. Locates the **first** leaf entry with `event_time >= '2025-11-01'` via binary search down the tree.
2. **Sequentially scans** sibling leaves (B-tree leaves are linked in a doubly linked list) until `event_time > '2025-11-07'`.
3. Stops.

The two endpoints are O(log n) binary searches; the middle is a linear scan of however many rows match. **Hash indexes cannot do this** — they would have to scan the whole table.

This single capability is why **PostgreSQL defaults to B-tree indexes** even though hash indexes exist: time-range queries, ID-range queries, and ordered scans are pervasive.

### Index-only scans

If your query only references columns in the index, PostgreSQL can return results **without** touching the heap at all — the entire query is satisfied by binary searches and sequential leaf scans. This is why composite indexes covering `(user_id, event_time, event_type)` accelerate queries that filter on user_id and event_time and only return event_type.

---

<a name="sorted-file"></a>
## 3. Sorted File Binary Search

When you have a sorted file too large to load into RAM, you can binary-search it on disk. Classic example: an alphabetically sorted log of error messages, or a sorted dump of (key, value) pairs.

### The Unix `look` command

```sh
$ look hello /usr/share/dict/words
hello
helloed
hello-girl
hellos
```

`look` does binary search on a sorted text file. It opens the file, seeks to the middle byte, scans backward to a newline, reads the line, compares, then seeks to the middle of the appropriate half. This is a binary search where each "comparison" is a `lseek + read`. Modern file systems and SSDs make each comparison cost ~50 µs; on a 1 GB sorted file with ~10 million lines, finding any word takes ~24 seeks ≈ 1.2 ms.

### Implementation sketch

```python
def disk_bisect(path, target):
    with open(path, 'rb') as f:
        f.seek(0, 2)             # SEEK_END
        size = f.tell()
        lo, hi = 0, size
        while lo < hi:
            mid = (lo + hi) // 2
            f.seek(mid)
            f.readline()         # discard partial line
            pos = f.tell()
            if pos >= hi:
                hi = mid         # avoid infinite loop near end
                continue
            line = f.readline().rstrip(b'\n')
            if line < target:
                lo = pos + len(line) + 1
            else:
                hi = mid
        f.seek(lo)
        return f.readline().rstrip(b'\n')
```

The trick: after seeking to a random byte, read forward to the next newline so you start at a line boundary. (Alternatively, a sorted *fixed-width* file — like a sorted CSV with padded fields — eliminates this seek-to-newline step entirely.)

### Production examples

- **Sorted log files** for fast time-window queries when you don't want a database.
- **Static lookup tables** (geo-IP databases like MaxMind's `.mmdb`, but those use a more sophisticated trie-binary hybrid).
- **Sorted-string tables (SSTables)** in LevelDB / RocksDB / Cassandra — see next section.

---

<a name="git-bisect"></a>
## 4. Version Control Bisect (`git bisect`)

`git bisect` is the most famous practical application of binary search outside of arrays. You have a known **good** commit (the bug doesn't exist) and a known **bad** commit (the bug exists). Somewhere in between, a commit introduced the bug. `git bisect` finds it in O(log n) tests.

### How it works

```sh
$ git bisect start
$ git bisect bad HEAD               # current is broken
$ git bisect good v2.0.0            # v2.0.0 worked
Bisecting: 1024 revisions left to test after this (roughly 10 steps)
$ # ... build, test ...
$ git bisect good                   # this commit is fine
Bisecting: 512 revisions left to test after this (roughly 9 steps)
$ # ... continue ...
```

Each step, Git checks out the commit halfway between the current good/bad markers. You build, test, and label it. After ~log₂(n) steps, Git points to the commit that introduced the bug.

### Why it's not exactly textbook binary search

The commit graph is a **DAG**, not a sorted array. Git uses a topological version of bisection that handles merges. The key property is **monotonicity**: if a commit is "bad", every descendant is also bad (assuming the bug, once introduced, is never fixed in the range). This monotonicity is what makes binary search applicable.

### Automating with `git bisect run`

```sh
$ git bisect run pytest tests/test_regression.py::test_specific_bug
```

You give Git a script that exits 0 (good), 1 (bad), or 125 (skip). Git runs the bisection loop automatically. On a project with 10,000 commits between good and bad, this takes ~14 test runs — maybe 20 minutes if your tests take 90 seconds. Compare with manually checking out and testing each commit: 10,000 × 90 s = 250 hours.

This single tool has saved the open-source community an unfathomable amount of debugging time.

### Other "bisect on a DAG" tools

- **`hg bisect`** in Mercurial (same concept).
- **`bisect.el`** in some Emacs extensions.
- Build-system bisection: `bazel build //... --bisect` style tools that find the broken target.
- Even Kubernetes has `kubectl rollout history` + bisect-style scripts.

### What `git bisect` teaches you

The lesson generalizes: **anywhere you have a monotonic predicate over a totally ordered (or topologically ordered) sequence, binary search works.** Bug introduction in commits, performance regressions across Docker image versions, configuration changes across infrastructure-as-code states — all amenable to bisection.

---

<a name="distributed"></a>
## 5. Distributed Sorted Indexes

Modern distributed databases use **sorted-string tables (SSTables)** as their on-disk storage. SSTables are immutable, sorted-by-key files. Examples: Google's Bigtable, Apache Cassandra, Apache HBase, RocksDB (used inside CockroachDB, TiDB, MyRocks, ScyllaDB), LevelDB, ClickHouse parts.

### SSTable structure

An SSTable consists of:
1. **Data blocks** — sorted (key, value) pairs, typically 4–64 KB each.
2. **Index block** — for each data block, the **first key** and the **byte offset**. The index is itself sorted.
3. **Bloom filter** — fast "definitely not present" check.
4. **Footer** — pointers to the index and metadata.

### Lookup flow

```
1. Bloom filter check       → if "no", return immediately.
2. Binary search the index  → find the data block whose first-key range covers the target.
3. Load that data block     → from disk or block cache.
4. Binary search inside it  → find the exact (key, value).
```

Two binary searches per SSTable, plus one disk I/O for the data block. With LSM-tree compaction keeping ~10 SSTable levels, a worst-case lookup is 10 × (2 binary searches + 1 disk I/O). The binary searches are nanoseconds; the disk I/Os are the cost.

### Bigtable's tablet structure

Google's Bigtable splits each table into **tablets** (range partitions), and each tablet contains a stack of SSTables plus a memtable. To find a key:

1. Look up the tablet location in the **METADATA** table (itself a Bigtable table — a 3-level lookup).
2. Within the tablet, check the memtable (in RAM) first.
3. Then check each SSTable in order, using its bloom filter to skip and binary search to find.

Every step uses binary search somewhere. The aggregate cost is dominated by I/O, but if you removed the binary search and used linear scan, the CPU would catch fire.

### Range scans across shards

For `SELECT * FROM users WHERE user_id BETWEEN 1000 AND 2000`:

1. Find the start tablet via binary search on the METADATA index (which is itself sorted by user_id range).
2. Stream tablets in order until you exceed user_id = 2000.

Because tablets are sorted, range scans cost O(rows_returned + log(num_tablets)). This is why Bigtable / HBase / Spanner are excellent for time-series and ordered-ID workloads but mediocre for random sparse lookups.

### Consistent hashing's relationship to binary search

In Cassandra, the cluster's nodes are placed on a **token ring** (a sorted circular list of hash values). To locate the node responsible for a key:

1. Compute `token = hash(key)`.
2. **Binary search** the sorted token list to find the smallest token `>= our_token`.

That's `lower_bound` on the token ring. Every Cassandra read and write uses it. Most consistent-hashing libraries (Ketama, Akka's distributed-data, Envoy's ring-hash) implement it this way.

---

<a name="vs-hash"></a>
## 6. When Binary Search Beats Hash Tables

The textbook says hash tables are O(1), binary search is O(log n), so hash tables win. **Reality is more nuanced.** Here are scenarios where binary search wins in production:

### 6.1 Range queries

Hash tables cannot answer "give me all keys in `[a, b]`" without scanning the entire table. Binary search on a sorted array does it in O(log n + k) where `k` is the result size.

```python
# Sorted array: 2 binary searches + slice
left  = bisect_left(arr, a)
right = bisect_right(arr, b)
result = arr[left:right]   # O(log n) total

# Hash map: full scan
result = [v for k, v in hashmap.items() if a <= k <= b]   # O(n)
```

Time-series databases (Prometheus, InfluxDB, TimescaleDB), order books (financial systems), event logs, audit trails — all are range-heavy. Binary search structures dominate.

### 6.2 Ordered iteration

"Give me the next 10 entries in sort order" is free on a sorted array (just keep walking the index). On a hash map, you must collect everything and sort: O(n log n).

This is why **TreeMaps / std::map / sorted indexes** exist. They're internally trees, but the API surface is "binary search + ordered iteration".

### 6.3 K-th element

The 95th-percentile latency in your monitoring dashboard is the **k-th element** of a sorted array of latency samples. Sorted array: O(1) to access by index. Hash map: useless.

### 6.4 Compactness and cache

A sorted `int[]` of 1 million elements occupies exactly 4 MB. The same data in a `HashMap<Integer, Integer>` (Java) occupies ~80 MB after autoboxing and hash-table overhead — 20× the memory.

For lookups within a small sorted array (e.g., 1000 elements = 4 KB), **everything fits in L1 cache**. Binary search runs from L1, ~1 cycle per probe, ~10 ns total. A hash table forces an L1-cache-line load + chain walk, often 30–100 ns.

### 6.5 Predictable worst case

Hash tables can degrade to O(n) on adversarial input (hash flooding, malicious key choices). Binary search is **provably** O(log n) regardless of input. For latency-sensitive systems with SLOs, this matters.

The 2003 paper "Denial of Service via Algorithmic Complexity Attacks" (Crosby & Wallach) demonstrated taking down web servers by sending requests with hash-colliding keys. Mitigations (randomized hash seeds, BLAKE3-based hash functions) help, but a sorted array is immune by construction.

### 6.6 Approximate / closest match

"What's the closest timestamp in the log to 2025-11-04T15:23:11?" Binary search returns the predecessor and successor in O(log n). A hash table cannot answer this without auxiliary structure.

### 6.7 No insertions

If your data is **read-only** after build (lookup tables, geographic data, dictionary words, embedding vectors after training), the O(n) insert cost of a sorted array is irrelevant. You pay it once at load time. The lookup phase wins on cache and memory.

This is why LevelDB / RocksDB SSTables are immutable — they're **optimized for binary-searched reads** because writes go elsewhere (the memtable).

### 6.8 Persistent / append-only data

When data lives on disk and is rarely rewritten, a sorted file or SSTable is the natural format. Hash tables don't serialize well — their bucket layout depends on the hash function, the rebuild cost is high, and they don't compress. Sorted files compress superbly (delta encoding on near-consecutive keys, run-length encoding on repeated values).

### Summary table

| Workload | Winner |
|---|---|
| High-QPS exact lookup, mutable, in-memory | **Hash table** |
| Range queries, ordered iteration | **Binary search** / B-tree |
| K-th order statistics | **Sorted array** |
| Read-only with cold-cache cost | **Sorted array** (cache-friendly) |
| Adversarial input, real-time SLO | **Sorted array** (deterministic) |
| Approximate / nearest-neighbor 1D | **Binary search** |
| Persistent storage / on-disk | **Sorted file / SSTable** |
| Frequent insert + ordered query | **B-tree / skip list** |

The right answer to "should I use a hash table or binary search?" is: **what queries do you need?** If you need order — bisect. If you don't — hash.

---

## Closing Thoughts

Binary search is not "an algorithm you use sometimes". It is **infrastructure**. Every database query, every git bisect, every Cassandra lookup, every B-tree probe, every consistent-hash routing decision passes through a binary search. The 20-line implementation in `junior.md` runs trillions of times per day across the world's data centers.

The senior-level takeaway: when you're choosing data structures, ask **what's the workload's query pattern?** If it's ordered-anything, a sorted structure backed by binary search is almost always part of the answer.

---

## Further Reading

- **Bayer & McCreight**, "Organization and Maintenance of Large Ordered Indexes" (1972) — the original B-tree paper.
- **O'Neil et al.**, "The Log-Structured Merge-Tree (LSM-tree)" (1996) — origins of SSTables.
- **PostgreSQL source** — `src/backend/access/nbtree/nbtsearch.c`.
- **RocksDB documentation** — block-based table format and binary-search-augmented bloom filters.
- **`git bisect` man page** and the Linux kernel's documentation on bisection workflows.
- **DeCandia et al.**, "Dynamo: Amazon's Highly Available Key-value Store" (2007) — consistent hashing.
- Continue with `professional.md` for the formal complexity analysis, Eytzinger layout, and cache complexity proofs.
