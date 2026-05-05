# Linear Search — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Glossary](#glossary)
4. [Core Concepts](#core-concepts)
5. [Big-O Summary](#big-o-summary)
6. [Real-World Analogies](#real-world-analogies)
7. [Pros & Cons](#pros--cons)
8. [Step-by-Step Walkthrough](#step-by-step-walkthrough)
9. [Code Examples](#code-examples)
10. [Coding Patterns](#coding-patterns)
11. [Error Handling](#error-handling)
12. [Performance Tips](#performance-tips)
13. [Best Practices](#best-practices)
14. [Edge Cases & Pitfalls](#edge-cases--pitfalls)
15. [Common Mistakes](#common-mistakes)
16. [Cheat Sheet](#cheat-sheet)
17. [Visual Animation](#visual-animation)
18. [Summary](#summary)
19. [Further Reading](#further-reading)

---

## Introduction

> Focus: "What is Linear Search?" and "When is it the right choice?"

**Linear Search** (also called **sequential search**) is the simplest search algorithm: walk through the collection from one end to the other, comparing each element to the target. The first match wins; if you reach the end without a match, the target is absent.

It is the **default** search you reach for when you know nothing about the data. It does not require the array to be sorted, indexed, hashed, or otherwise pre-processed. You can run it on a linked list, a stream, a generator, a file you read line-by-line — anything you can iterate.

The trade-off is speed. For an array of `n` elements, linear search makes up to `n` comparisons. That is **O(n)** time. Compare this with **binary search** at **O(log n)** — but binary search demands a sorted array. Compare it with **hash table lookup** at **O(1) average** — but a hash table needs to be built and maintained.

Linear search is the foundation of more advanced search algorithms. Even when you build an inverted index for a search engine, the per-bucket scan inside that index is a linear search. Understanding linear search is understanding **the cost of "find me X in a pile of stuff"**.

In production code, linear search shows up everywhere:
- `Array.prototype.indexOf` in JavaScript
- `list.index(x)` in Python
- `slices.Contains` in Go (since 1.21)
- `Collection.contains` in Java (for non-Set collections)
- `strchr`, `strstr` in C
- `grep` walking lines of a file

It is **fast enough** for small `n` (typically n < 100), and it is **the only choice** when the data has no exploitable structure.

---

## Prerequisites

- **Required:** Loops (`for`, `while`), arrays/lists, conditionals
- **Required:** Function return values, sentinel values (returning `-1` for "not found")
- **Helpful:** Big-O notation basics
- **Helpful:** Understanding of how comparison works on integers, strings, and custom types

---

## Glossary

| Term | Definition |
|------|-----------|
| **Sequential search** | Synonym for linear search — scan elements one by one |
| **Target** (key) | The value you are looking for |
| **Found / Hit** | The target equals the current element being inspected |
| **Miss** | The element is not the target — keep going |
| **Absent / Not found** | The full collection was scanned and no match was found |
| **Sentinel** | A special value (e.g. `-1`) returned to signal "not found" |
| **Linear time** | Running time proportional to the input size — O(n) |
| **Early exit** | Returning the moment a match is found, without continuing the scan |
| **Index** | The position (zero-based) of a found element in the array |
| **Predicate** | A boolean function used as the matching criterion (e.g. `x -> x.id == 42`) |

---

## Core Concepts

### Concept 1: Iterate One by One Until Found

Walk the array from left to right. At each position `i`, compare `arr[i]` to `target`. If they match, return `i`. If you reach the end with no match, return `-1`.

```text
arr    = [4, 7, 2, 9, 1, 5, 8]
target = 9

i=0: arr[0]=4, 4 != 9 → continue
i=1: arr[1]=7, 7 != 9 → continue
i=2: arr[2]=2, 2 != 9 → continue
i=3: arr[3]=9, 9 == 9 → return 3 ✓
```

The algorithm is **completely deterministic**: given the same array and target, it always inspects the same positions in the same order.

### Concept 2: Return the Index, or a Sentinel

The conventional contract is:
- Found → return the **index** (0-based) of the first occurrence.
- Not found → return **`-1`** (or `None` in Python, `Optional.empty()` in Java, an `(int, bool)` pair in Go).

Returning the index (not just `true`/`false`) is more useful because callers can then read or modify the element. If they only care about presence, they wrap the call in `result != -1`.

### Concept 3: No Preconditions on the Data

This is the killer feature. Binary search requires a sorted array. Hash-table lookup requires a hash table. Linear search requires **nothing** — just the ability to iterate.

You can run linear search on:
- An unsorted array (`[5, 2, 8, 1, 9]`)
- A sorted array (`[1, 2, 5, 8, 9]`) — though binary search is faster here
- A linked list (no random access — binary search is impossible)
- A generator / iterator / stream (data arrives one piece at a time)
- A file (read line-by-line; you can't "binary search" a text file without an index)

### Concept 4: Early Exit on First Match

Once you find the target, **stop scanning**. Do not continue to the end.
- **Find first**: stop at the first match (the standard contract).
- **Find all**: collect all indices, then keep scanning to the end.
- **Find last**: scan from right to left, or scan everything and remember the last hit.

### Concept 5: Comparison Cost Matters

Each comparison is one operation. For integers, comparison is one CPU cycle. For strings, comparison is O(k) where k is the string length — so a linear search through `n` strings of length `k` is actually **O(n × k)** time. For deep object equality, comparison can be even more expensive.

---

## Big-O Summary

| Case | Time | Notes |
|------|------|-------|
| **Best** | **O(1)** | Target is at index 0 — found on first comparison |
| **Average** | **O(n)** | Target uniformly random in array → ~n/2 comparisons |
| **Worst** | **O(n)** | Target at the end, or target not present → n comparisons |
| **Space** | **O(1)** | Only a loop counter is needed |

**Compared to other search algorithms:**

| Algorithm | Time | Requires |
|-----------|------|----------|
| Linear search | O(n) | Nothing |
| Binary search | O(log n) | Sorted array |
| Hash lookup | O(1) avg, O(n) worst | Hash table built |
| BST search (balanced) | O(log n) | BST built |
| Trie lookup | O(k) (k = key length) | Trie built |

**Cache behavior:** Linear search has near-perfect cache behavior. The CPU prefetcher recognizes the sequential access pattern and pulls subsequent cache lines before they're needed. This makes linear search on small arrays **shockingly fast** — often beating binary search for n ≤ ~20.

---

## Real-World Analogies

| Scenario | How Linear Search Maps |
|----------|------------------------|
| Looking for your friend Sara in a movie theater seat-by-seat | You walk down each row, glance at each face. Stop when you find her. |
| Reading every email in your inbox from top to bottom looking for one from "Bank" | Each email is one comparison. Found → click. Reach the end → "no email from Bank." |
| Searching a stack of unsorted papers for a specific receipt | You go through one by one. There's no "binary search" for unsorted papers. |
| Looking up a contact in a phone *book* (sorted) — wrong fit | This is binary search territory; you don't read every name. |
| Looking up a contact in a *recently used* call list (unsorted) | This is linear search — you scroll through one by one. |
| Searching for a typo in a 200-line essay | You read every line until you find it. |

The key insight from these analogies: **linear search is the natural human instinct for "find this thing"** when you have no organizing principle.

---

## Pros & Cons

### Pros

| Pro | Why it Matters |
|-----|----------------|
| **Works on any iterable** | No sorting, no indexing, no hashing required |
| **Trivial to implement** | 5 lines of code in any language |
| **Zero memory overhead** | O(1) extra space — just a loop counter |
| **Cache-friendly** | Sequential memory access is the fastest pattern for modern CPUs |
| **Beats binary search for small n** | n < ~20: linear is faster due to lower constant factor |
| **Works on unsorted data** | Inserting, deleting → no need to re-sort |
| **Online / streaming** | Can search a stream as data arrives |
| **Easy to parallelize** | Split the array into chunks, search each in a separate thread |

### Cons

| Con | Why it Matters |
|-----|----------------|
| **O(n) is slow for large n** | n = 1,000,000 → up to 1 million comparisons per query |
| **Worst case scans everything** | Especially bad when the target is absent (no early exit) |
| **Repeated queries waste work** | If you search the same array 1000 times, you do 1000 × O(n) = O(n × queries) work. A hash table would cost O(n + queries). |
| **No fault tolerance** | A typo in the target → "not found." No fuzzy matching built-in. |

**Rule of thumb:**
- n < 100, query is rare → linear search.
- n large, single query, data already sorted → binary search.
- n large, many queries → build a hash set / index, then O(1) lookups.

---

## Step-by-Step Walkthrough

Let's trace `linearSearch([10, 25, 3, 47, 8], 47)`:

```
Initial: arr = [10, 25, 3, 47, 8], target = 47
                 ^
                 i = 0

Step 1: i=0, arr[0]=10, 10 != 47 → i++
Step 2: i=1, arr[1]=25, 25 != 47 → i++
Step 3: i=2, arr[2]=3,   3 != 47 → i++
Step 4: i=3, arr[3]=47, 47 == 47 → return 3 ✓
```

Found at index 3. Total comparisons: 4 (out of 5 possible).

Now trace `linearSearch([10, 25, 3, 47, 8], 99)`:

```
Initial: arr = [10, 25, 3, 47, 8], target = 99

Step 1: i=0, arr[0]=10, 10 != 99 → i++
Step 2: i=1, arr[1]=25, 25 != 99 → i++
Step 3: i=2, arr[2]=3,   3 != 99 → i++
Step 4: i=3, arr[3]=47, 47 != 99 → i++
Step 5: i=4, arr[4]=8,   8 != 99 → i++
Step 6: i=5, i >= n=5 → exit loop, return -1
```

Not found. Total comparisons: 5 (the worst case).

---

## Code Examples

### Go — Basic Linear Search

```go
package linsearch

// LinearSearch returns the index of the first occurrence of target in arr,
// or -1 if target is not present.
func LinearSearch(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i
        }
    }
    return -1
}
```

### Go — Find All Occurrences

```go
func FindAll(arr []int, target int) []int {
    var indices []int
    for i, v := range arr {
        if v == target {
            indices = append(indices, i)
        }
    }
    return indices
}
```

### Go — Find First and Find Last

```go
func FindFirst(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i  // early exit on first match
        }
    }
    return -1
}

func FindLast(arr []int, target int) int {
    // Scan right-to-left → first match IS the last occurrence.
    for i := len(arr) - 1; i >= 0; i-- {
        if arr[i] == target {
            return i
        }
    }
    return -1
}
```

### Java — Basic Linear Search

```java
public class LinearSearch {

    public static int search(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }

    // Generic version using equals() — works for any type.
    public static <T> int search(T[] arr, T target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null ? target == null : arr[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}
```

### Java — Find All Occurrences

```java
import java.util.ArrayList;
import java.util.List;

public static List<Integer> findAll(int[] arr, int target) {
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            indices.add(i);
        }
    }
    return indices;
}
```

### Python — Basic Linear Search

```python
def linear_search(arr: list[int], target: int) -> int:
    """Return the index of target in arr, or -1 if not present."""
    for i, value in enumerate(arr):
        if value == target:
            return i
    return -1
```

### Python — Find All Occurrences

```python
def find_all(arr: list[int], target: int) -> list[int]:
    """Return all indices where target appears."""
    return [i for i, v in enumerate(arr) if v == target]
```

### Python — First and Last

```python
def find_first(arr, target):
    for i, v in enumerate(arr):
        if v == target:
            return i
    return -1

def find_last(arr, target):
    for i in range(len(arr) - 1, -1, -1):
        if arr[i] == target:
            return i
    return -1
```

---

## Coding Patterns

### Pattern 1: Early Return

```python
def linear_search(arr, target):
    for i, v in enumerate(arr):
        if v == target:
            return i      # ← Early return: no need to continue
    return -1
```

**Why:** Avoids unnecessary work after the first match. Cuts average runtime in half on uniformly distributed targets.

### Pattern 2: Sentinel (Place Target at End)

```python
def sentinel_search(arr, target):
    arr_copy = arr + [target]   # append target as sentinel
    i = 0
    while arr_copy[i] != target:
        i += 1
    if i < len(arr):
        return i
    return -1
```

**Why:** The inner `while` loop has only **one** condition to check (`arr[i] != target`) instead of two (`i < n AND arr[i] != target`). Halves the per-iteration work. Used to be a big win in the 1980s; modern CPUs make the savings small but still measurable in tight loops.

**Note:** The sentinel pattern modifies (or copies) the array. In a real system you'd want to operate on a buffer that already has space for the sentinel, to avoid the copy.

### Pattern 3: Predicate Search (Find First Matching)

```python
def find_first_matching(arr, predicate):
    """Find first element where predicate(x) is True."""
    for i, v in enumerate(arr):
        if predicate(v):
            return i
    return -1

# Usage
users = [{"id": 1, "age": 22}, {"id": 2, "age": 35}, {"id": 3, "age": 41}]
idx = find_first_matching(users, lambda u: u["age"] > 30)  # → 1
```

**Why:** Generalizes from "equality match" to "any condition." This is what `Stream.filter().findFirst()` (Java), `arr.find(...)` (JS), `next(... for ... in ... if ...)` (Python) all do under the hood.

### Pattern 4: Use the Built-In

In every modern language, there's a built-in linear-search function. **Use it** in production code — it's been tuned by language maintainers.

```python
# Python
if target in arr:           # uses C-level linear scan
    idx = arr.index(target) # raises ValueError if absent

# Java
boolean present = list.contains(target);
int idx = list.indexOf(target);

// Go (1.21+)
import "slices"
present := slices.Contains(arr, target)
idx := slices.Index(arr, target)
```

---

## Error Handling

### Empty Array

Linear search on an empty array should return `-1` (or `None`). The loop body never executes; the function falls through to the `return -1`.

```python
def linear_search(arr, target):
    for i, v in enumerate(arr):  # if arr is [], loop body never runs
        if v == target:
            return i
    return -1                    # ← reached immediately for empty arr

assert linear_search([], 5) == -1
```

### Null / None Array

Decide early: do you accept `None` and treat it as "empty," or do you raise?

```python
def linear_search(arr, target):
    if arr is None:
        raise ValueError("arr must not be None")
    ...
```

```java
public static int search(int[] arr, int target) {
    if (arr == null) {
        throw new IllegalArgumentException("arr must not be null");
    }
    ...
}
```

### Comparing Floats

Direct equality on floats is **dangerous** due to rounding (`0.1 + 0.2 != 0.3`). Use a tolerance:

```python
def find_close(arr, target, eps=1e-9):
    for i, v in enumerate(arr):
        if abs(v - target) < eps:
            return i
    return -1
```

### Comparing NaN

`NaN != NaN` is `True` in IEEE-754. A naive linear search will **never find NaN**:

```python
import math
arr = [1.0, 2.0, math.nan, 4.0]
arr.index(math.nan)   # ValueError: nan is not in list (in some versions)
```

Use `math.isnan` explicitly when searching for NaN.

---

## Performance Tips

1. **Early exit** on first match — never scan past it (unless you need all occurrences).
2. **Place common targets first** if you know the access distribution. Frequently-searched values at the front of the array → average comparisons drop.
3. **Use the language built-in** (`in`, `contains`, `indexOf`). The interpreter / JIT often vectorizes these to SIMD instructions.
4. **For repeated queries on the same data, switch to a hash set** — O(1) per lookup after a one-time O(n) build.
5. **For sorted data, switch to binary search** — O(log n) per query.
6. **For small arrays (n < ~20), don't bother optimizing** — even O(n²) algorithms are faster than the overhead of cleverer approaches.
7. **Avoid expensive comparisons inside the loop** — pre-compute hash codes or pre-extract a key.
8. **Iterate in cache-friendly order** — for 2D arrays, iterate row-by-row (row-major) not column-by-column.

---

## Best Practices

1. **Return an index, not a boolean** — callers can derive the boolean from `idx >= 0`, but they can't derive an index from `true`.
2. **Document the "not found" sentinel** — `-1`, `None`, `Optional.empty()`, or `(0, false)` in Go.
3. **Prefer `Optional<T>` in Java / `option[T]` in Rust** for type safety where idiomatic.
4. **Keep the loop body small** — push complex logic into helper functions to keep the search tight.
5. **Use `for-each` / `range` loops** when you don't need the index of *non-matching* elements — cleaner and harder to off-by-one.
6. **Don't reinvent `contains`** — use the built-in. Only write your own loop when you need the index, all matches, or a custom predicate.
7. **Test edge cases explicitly** — empty array, target at index 0, target at index n-1, target not present, duplicates.

---

## Edge Cases & Pitfalls

| Edge Case | Behavior |
|-----------|----------|
| **Empty array** | Return `-1` immediately. No comparisons performed. |
| **Single element, match** | Return `0`. One comparison. |
| **Single element, no match** | Return `-1`. One comparison. |
| **Target at index 0** | Return `0`. One comparison. (Best case) |
| **Target at index n-1** | Return `n-1`. n comparisons. |
| **Target not present** | Return `-1`. n comparisons. (Worst case) |
| **Duplicates** | Return first occurrence (or as documented — first vs last vs all). |
| **All elements same as target** | Return `0`. One comparison. |
| **All elements same, target different** | Return `-1`. n comparisons. |
| **Array length = 0 vs nil/null** | Handle both — `len(nil) == 0` in Go is safe; `null.length` is a NPE in Java. |

---

## Common Mistakes

### Mistake 1: Off-by-one in the Loop Bound

```python
# BUG: range(len(arr) - 1) misses the last element
for i in range(len(arr) - 1):    # ❌ should be range(len(arr))
    if arr[i] == target:
        return i
```

**Fix:** Use `range(len(arr))` — or better, `enumerate(arr)`.

### Mistake 2: Returning `True` Instead of the Index

```python
# BUG: callers can't use this for arr[idx] = new_value
def linear_search(arr, target):
    for v in arr:
        if v == target:
            return True
    return False
```

**Fix:** Return the index. If callers want a boolean, they write `idx >= 0`.

### Mistake 3: Forgetting the "Not Found" Case

```go
// BUG: no return after the loop — compile error in Go.
// In dynamic languages it returns None/undefined silently.
func search(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i
        }
    }
    // missing return -1
}
```

**Fix:** Always have a `return -1` (or equivalent) after the loop.

### Mistake 4: Continuing After a Found Match

```python
# BUG: returns the LAST match, not the first.
def linear_search(arr, target):
    found = -1
    for i, v in enumerate(arr):
        if v == target:
            found = i
            # missing break / early return
    return found
```

**Fix:** `return i` immediately on match, or `break` out of the loop.

### Mistake 5: Modifying the Array While Iterating

```python
# BUG: removing elements shifts indices — you skip the next element.
for i, v in enumerate(arr):
    if v == target:
        arr.pop(i)   # ❌ corrupts iteration
```

**Fix:** Build a new list, iterate over a copy, or iterate in reverse.

### Mistake 6: Comparing Mutable Objects by Identity

```python
# BUG: `is` checks object identity, not equality.
for v in arr:
    if v is target:   # ❌ should be ==
        return ...
```

**Fix:** Use `==` (or `.equals()` in Java) for value equality.

---

## Cheat Sheet

```text
                    ┌─────────────────────────┐
                    │     LINEAR SEARCH       │
                    ├─────────────────────────┤
                    │ Best:    O(1)           │
                    │ Average: O(n)           │
                    │ Worst:   O(n)           │
                    │ Space:   O(1)           │
                    │ Stable:  N/A (search)   │
                    │ Sort?:   not required   │
                    └─────────────────────────┘

PSEUDOCODE
----------
function linear_search(arr, target):
    for i from 0 to length(arr) - 1:
        if arr[i] == target:
            return i
    return -1

WHEN TO USE
-----------
✓ Data is unsorted
✓ n is small (< 100)
✓ Single query
✓ Iterator / streaming source (no random access)
✓ Custom predicate (not just equality)

WHEN NOT TO USE
---------------
✗ n is large AND data is sorted          → binary search
✗ Many lookups on the same data          → hash set / hash map
✗ Range queries ("all values 5..10")     → BST or sorted array + binary search
✗ Prefix lookup on strings               → trie
```

---

## Visual Animation

See [`animation.html`](./animation.html) for an interactive visualization. Watch the scan position move left-to-right, with each cell colored:

- **Blue** → not yet visited
- **Yellow** → currently being inspected
- **Red** → mismatch, moving on
- **Green** → match found, search ends

Use the speed slider to step through one comparison at a time. Try the preset "target at end" to see the worst case.

---

## Summary

Linear search is the **plain, honest, no-prerequisites** search algorithm. It scans every element until it finds the target, taking O(n) time and O(1) space. It is **the only choice** when data is unsorted and you have no index. It is **the fastest choice** for tiny arrays (n < ~20) due to cache effects and low constant factors.

Key facts:

1. **O(n) time, O(1) space.**
2. **No preconditions** — works on anything iterable.
3. **Early exit** on first match keeps the average case at ~n/2 comparisons.
4. **Used everywhere** — `in`, `contains`, `indexOf`, `find` are all linear search.
5. **Not the fastest for large n** — but the simplest, and often "fast enough."

Master linear search and you'll understand the **base case** all other search algorithms try to improve upon.

---

## Further Reading

- **Cormen, Leiserson, Rivest, Stein** — *Introduction to Algorithms*, Chapter 2.1 (Linear Search)
- **Sedgewick & Wayne** — *Algorithms (4th ed.)*, Section 1.4 (Sequential search)
- **Knuth** — *The Art of Computer Programming, Vol. 3*, Section 6.1 (Sequential Searching)
- **Go standard library:** `slices.Index`, `slices.Contains` — [pkg.go.dev/slices](https://pkg.go.dev/slices)
- **Java standard library:** `Collection.contains`, `List.indexOf`, `Arrays.asList(...).indexOf(...)`
- **Python standard library:** `list.index`, `in` operator
- **Linus Torvalds on cache effects:** ["arrays are simpler than linked lists"](https://lkml.org/lkml/2006/9/12/255)
- Continue to [`middle.md`](./middle.md) for sentinel search, bidirectional, parallel, and SIMD variants.
