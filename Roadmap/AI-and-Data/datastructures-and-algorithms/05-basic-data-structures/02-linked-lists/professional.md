# Linked Lists -- Professional Level

## Prerequisites

- Strong understanding of linked list variants and algorithms
- Mathematical maturity (proof techniques, probability)
- Familiarity with amortized analysis and cache models

## Table of Contents

1. [Formal Linked List ADT](#formal-linked-list-adt)
2. [Correctness Proofs](#correctness-proofs)
3. [Amortized Analysis of Skip Lists](#amortized-analysis-of-skip-lists)
4. [Cache-Oblivious Linked Structures](#cache-oblivious-linked-structures)
5. [Lower Bounds for Linked List Operations](#lower-bounds)
6. [Summary](#summary)

---

## Formal Linked List ADT

### Algebraic Specification

A linked list can be formally defined as an Abstract Data Type (ADT) using algebraic specification.

**Sorts:** `List`, `Element`, `Boolean`, `Natural`

**Operations:**

```
empty    : -> List
prepend  : Element x List -> List
head     : List -> Element
tail     : List -> List
isEmpty  : List -> Boolean
length   : List -> Natural
```

**Axioms (for all e: Element, L: List):**

```
A1: isEmpty(empty()) = true
A2: isEmpty(prepend(e, L)) = false
A3: head(prepend(e, L)) = e
A4: tail(prepend(e, L)) = L
A5: length(empty()) = 0
A6: length(prepend(e, L)) = 1 + length(L)
```

**Preconditions:**

```
P1: head(L) requires isEmpty(L) = false
P2: tail(L) requires isEmpty(L) = false
```

### Representation Invariant

For a singly linked list with head and tail pointers:

```
RI(ll) =
  (ll.head = nil <=> ll.tail = nil <=> ll.size = 0)
  AND (ll.size > 0 =>
    ll.tail is reachable from ll.head by following exactly ll.size - 1 next pointers
    AND ll.tail.next = nil)
  AND (for all nodes n reachable from ll.head: n.next = nil => n = ll.tail)
```

### Abstraction Function

The abstraction function maps the concrete linked list representation to the abstract sequence:

```
AF(ll) = [n1.data, n2.data, ..., nk.data]
  where n1 = ll.head
  and n_{i+1} = n_i.next for 1 <= i < k
  and k = ll.size
```

---

## Correctness Proofs

### Proof: Iterative Reversal is Correct

**Claim:** Given a singly linked list L = [a1, a2, ..., an], the iterative reversal algorithm produces [an, an-1, ..., a1].

**Algorithm:**
```
reverse(head):
    prev = nil
    current = head
    while current != nil:
        next = current.next
        current.next = prev
        prev = current
        current = next
    return prev
```

**Proof by loop invariant.**

**Loop invariant:** At the start of each iteration:
- `prev` points to a reversed list of all nodes already processed.
- `current` points to the first unprocessed node.
- The concatenation of reverse(prev-list) and current-list equals the original list.

**Initialization (before first iteration):**
- `prev = nil` (empty reversed list).
- `current = head` (entire original list).
- Invariant holds trivially: reverse([]) ++ [a1, ..., an] = [a1, ..., an].

**Maintenance:** Assume the invariant holds at the start of iteration i, where `prev` points to the reversed list [a_{i-1}, ..., a1] and `current` points to [a_i, ..., an].

After one iteration:
- `next = current.next` (saves a_{i+1}).
- `current.next = prev` (a_i now points to a_{i-1}).
- `prev = current` (prev now represents [a_i, a_{i-1}, ..., a1]).
- `current = next` (current now represents [a_{i+1}, ..., an]).

The invariant is maintained.

**Termination:** When `current = nil`, all nodes have been processed. `prev` points to [an, an-1, ..., a1]. The algorithm returns `prev`, which is the correctly reversed list. QED.

### Proof: Merge of Two Sorted Lists Produces a Sorted List

**Claim:** Given two sorted lists L1 and L2, the merge algorithm produces a sorted list containing all elements of L1 and L2.

**Proof by induction on |L1| + |L2|.**

**Base case:** If L1 is empty, return L2 (sorted). If L2 is empty, return L1 (sorted). Both correct.

**Inductive step:** Assume correctness for all pairs with |L1| + |L2| < k. Consider |L1| + |L2| = k where both are non-empty.

Without loss of generality, assume head(L1) <= head(L2). The algorithm selects head(L1) as the next element and recursively merges tail(L1) with L2.

By the inductive hypothesis, merge(tail(L1), L2) produces a sorted list M containing all elements of tail(L1) and L2.

The result is head(L1) followed by M. Since:
1. head(L1) <= head(L2) (by our assumption).
2. head(L1) <= all elements of tail(L1) (since L1 is sorted).
3. Therefore head(L1) <= all elements of M.

So the result is sorted and contains all |L1| + |L2| elements. QED.

### Proof: Floyd's Cycle Detection Terminates and is Correct

**Claim:** If a linked list has a cycle, the slow and fast pointers will meet. If it has no cycle, the algorithm terminates with fast reaching nil.

**No-cycle case:** Fast advances 2 steps per iteration. After at most n/2 iterations (where n is the list length), fast reaches nil. The loop terminates.

**Cycle case:** Let:
- mu = number of nodes before the cycle entry.
- lambda = cycle length.

When slow enters the cycle (after mu steps), fast is already in the cycle. Let d be the distance (in steps ahead) of fast from slow within the cycle. At each iteration, the gap changes:

```
gap(t+1) = gap(t) - 1  (mod lambda)
```

Because fast advances 2 and slow advances 1, the fast pointer gains 1 step per iteration. Equivalently, the distance between them decreases by 1 each iteration. After exactly `lambda - d` iterations, the gap becomes 0 (they meet).

Total steps: O(mu + lambda) = O(n). QED.

---

## Amortized Analysis of Skip Lists

### Expected Height

Each node has height h with probability p^h * (1-p), where p is the promotion probability (typically 0.5).

**Expected height of a single node:** E[h] = p / (1-p). For p = 0.5, E[h] = 1.

**Expected maximum height** of n nodes: O(log n). More precisely, with high probability the maximum level is at most c * log_2(n) for a constant c.

### Expected Search Cost

**Theorem:** The expected number of comparisons for a search in a skip list with n elements is O(log n).

**Proof sketch (backward analysis):**

Consider the search path from the found node back to the top-left. At each node on the path, we either:
1. Move up (with probability p) -- this happened when the node was promoted during insertion.
2. Move left (with probability 1-p) -- this happened when the node was not promoted.

The expected number of left moves at each level is 1/(1-p). Since there are O(log n) levels, the expected total path length is O(log n / (1-p)) = O(log n) for constant p.

### Expected Space

Each node at level 0 is promoted to level 1 with probability p, to level 2 with probability p^2, etc. The expected total number of forward pointers across all nodes:

```
E[space] = n * sum_{i=0}^{inf} p^i = n / (1 - p)
```

For p = 0.5, this is 2n. So the expected space overhead is O(n) -- linear, with a constant factor of 2.

### Amortized Insert Cost

Insertion consists of:
1. Search for the correct position: O(log n) expected.
2. Determine random height: O(1) expected.
3. Update forward pointers at each level: O(height) = O(log n) worst case, O(1) expected.

Total expected amortized cost per insertion: O(log n).

---

## Cache-Oblivious Linked Structures

### The Problem

Standard linked lists have terrible cache performance. Each node is a separate heap allocation, and following pointers causes cache misses at nearly every step. With a cache line of B bytes, traversing n nodes causes O(n) cache misses -- no better than random access.

### Cache-Oblivious Analysis Model

The cache-oblivious model assumes a two-level memory hierarchy with:
- Cache of size M.
- Cache lines of size B.
- Optimal replacement policy.

The algorithm does not know M or B.

### Unrolled Linked Lists

Store up to B elements per node (where B is the cache line size). This improves traversal to O(n/B) cache misses -- matching array performance.

```
[a1,a2,...,aB | next] -> [aB+1,...,a2B | next] -> ...
```

In the cache-oblivious setting, even without knowing B, you can use a fixed block size k. If k >= B, you get O(n/B) cache misses for traversal.

### Van Emde Boas Layout

For tree-based structures built from linked lists (e.g., skip lists used as balanced trees), the **van Emde Boas (vEB) layout** arranges nodes in memory recursively:

1. Split the tree at the middle level.
2. Layout the top subtree contiguously, then each bottom subtree contiguously.
3. Recurse.

This achieves O(log_B n) cache misses per search without knowing B.

### Practical Mitigation: Arena Allocation

Rather than relying on the general-purpose allocator, allocate all linked list nodes from a contiguous arena:

```go
type Arena struct {
    nodes []Node
    next  int
}

func (a *Arena) Alloc(data int) *Node {
    if a.next >= len(a.nodes) {
        panic("arena full")
    }
    node := &a.nodes[a.next]
    node.Data = data
    a.next++
    return node
}
```

Nodes allocated sequentially from the arena will be contiguous in memory, improving cache performance for traversal if the traversal order matches the allocation order.

---

## Lower Bounds

### Comparison-Based Search Lower Bound

**Theorem:** Any comparison-based search on a linked list of n elements requires Omega(n) comparisons in the worst case.

**Proof:** Unlike an array, a linked list does not support random access. The only way to reach the k-th element is to traverse k-1 pointers from the head. Therefore, any algorithm must potentially visit all n nodes. An adversary can always place the target at the last position visited, forcing n comparisons.

This is in contrast to sorted arrays, where binary search achieves O(log n) via random access.

### Why Skip Lists Do Not Violate This Bound

Skip lists achieve O(log n) search by adding O(n) extra pointers that provide "shortcuts." The base linked list at level 0 still requires O(n) for a linear scan, but the additional levels transform the structure into something that is no longer a simple linked list -- it is a multi-level structure with random access at higher levels.

### Reversal Lower Bound

**Theorem:** Reversing a singly linked list requires Omega(n) time.

**Proof:** Every node's `next` pointer must be changed (from pointing forward to pointing backward). There are n-1 such pointers. Each pointer modification takes O(1) time, so the total is Omega(n).

### Space Lower Bounds for Linked Lists

A singly linked list storing n elements requires at least n * (sizeof(element) + sizeof(pointer)) space. A doubly linked list requires n * (sizeof(element) + 2 * sizeof(pointer)).

The XOR linked list achieves n * (sizeof(element) + sizeof(pointer)) while supporting bidirectional traversal, showing that the doubly linked list's second pointer is not information-theoretically necessary -- the XOR trick encodes both directions in one pointer-sized field.

### Cell Probe Lower Bounds

In the cell probe model, any dynamic data structure supporting both insertions and predecessor queries (which a sorted linked list supports) requires Omega(log log n) amortized time per operation when using O(n * polylog(n)) space. Skip lists nearly match this with O(log n), and more sophisticated structures like van Emde Boas trees achieve O(log log n) for integer keys.

---

## Summary

| Topic                          | Key Result                                              |
|--------------------------------|---------------------------------------------------------|
| Formal ADT                     | Algebraic specification with 6 axioms                   |
| Representation invariant       | Ensures head/tail/size consistency                      |
| Reversal correctness           | Loop invariant proof over processed/unprocessed split   |
| Merge correctness              | Induction on total list length                          |
| Floyd's correctness            | Gap decreases by 1 per iteration in cycle               |
| Skip list search               | O(log n) expected via backward analysis                 |
| Skip list space                | O(n) expected with constant factor 1/(1-p)             |
| Cache-oblivious linked lists   | Unrolled nodes achieve O(n/B) cache misses              |
| Search lower bound             | Omega(n) for simple linked list (no random access)      |
| Reversal lower bound           | Omega(n) -- must modify n-1 pointers                    |
