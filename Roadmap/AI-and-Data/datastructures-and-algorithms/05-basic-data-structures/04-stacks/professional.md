# Stack -- Professional / Theoretical Level

## Table of Contents

1. [Overview](#overview)
2. [Formal Stack ADT Axioms](#formal-stack-adt-axioms)
3. [Correctness Proof: Balanced Parentheses](#correctness-proof-balanced-parentheses)
4. [Lock-Free Treiber Stack: Linearizability Proof](#lock-free-treiber-stack-linearizability-proof)
5. [Amortized Analysis of Dynamic Array Stack](#amortized-analysis-of-dynamic-array-stack)
6. [Stack-Sortable Permutations](#stack-sortable-permutations)
7. [Catalan Numbers Connection](#catalan-numbers-connection)
8. [Summary](#summary)

---

## Overview

This document covers the mathematical and theoretical foundations of stacks. It is intended for those who need to reason formally about correctness, prove properties of concurrent data structures, or understand the combinatorial theory behind stack-based sorting.

---

## Formal Stack ADT Axioms

A stack can be defined axiomatically as an abstract data type with the following signature and laws.

### Signature

```
Stack<T>

Operations:
    empty    : () -> Stack<T>
    push     : Stack<T> x T -> Stack<T>
    pop      : Stack<T> -> Stack<T> x T       (partial: undefined on empty)
    peek     : Stack<T> -> T                   (partial: undefined on empty)
    isEmpty  : Stack<T> -> Bool
```

### Axioms

For all stacks `s` and elements `x`:

1. **isEmpty(empty()) = true**
   An empty stack is empty.

2. **isEmpty(push(s, x)) = false**
   A stack with at least one pushed element is not empty.

3. **pop(push(s, x)) = (s, x)**
   Popping after a push returns the original stack and the pushed element.

4. **peek(push(s, x)) = x**
   Peeking after a push returns the most recently pushed element.

These four axioms completely characterize the stack. Any implementation satisfying them is a correct stack.

### Derived Properties

From the axioms, we can derive:

- **LIFO order**: By repeated application of axiom 3, elements are removed in reverse insertion order.
- **Size conservation**: If `size(s) = n`, then `size(push(s, x)) = n + 1` and `size(fst(pop(s))) = n - 1`.
- **Commutativity of unrelated pushes is NOT guaranteed**: `push(push(s, a), b) != push(push(s, b), a)` in general (the stack remembers order).

---

## Correctness Proof: Balanced Parentheses

We prove that the standard stack-based algorithm correctly determines whether a string of parentheses is balanced.

### Definition

A string `w` over `{(, )}` is **balanced** if and only if:
1. `w` has equal numbers of `(` and `)`.
2. For every prefix of `w`, the number of `(` is >= the number of `)`.

### Algorithm

```
function isBalanced(w):
    stack = empty()
    for each character c in w:
        if c == '(':
            push(stack, c)
        elif c == ')':
            if isEmpty(stack):
                return false
            pop(stack)
    return isEmpty(stack)
```

### Loop Invariant

Let `w[0..i-1]` be the characters processed so far. The loop invariant is:

**`size(stack) = count('(', w[0..i-1]) - count(')', w[0..i-1])`**

and `size(stack) >= 0` at every step.

### Proof

**Initialization (i = 0):** Stack is empty, count of both brackets is 0. `0 = 0 - 0`. Invariant holds.

**Maintenance:** Assume the invariant holds after processing `w[0..i-1]`.

- Case `w[i] = '('`: We push, so `size(stack)` increases by 1. `count('(')` also increases by 1. Invariant maintained.
- Case `w[i] = ')'` and stack is non-empty: We pop, so `size(stack)` decreases by 1. `count(')')` increases by 1. The difference `count('(') - count(')')` decreases by 1. Invariant maintained.
- Case `w[i] = ')'` and stack is empty: We return `false`. This is correct because `size(stack) = 0` means `count('(') = count(')')`, and seeing another `)` means prefix condition 2 is violated.

**Termination:** After processing all characters, if `isEmpty(stack)`, then `count('(') = count(')')` and no prefix violated condition 2. The string is balanced. If the stack is non-empty, `count('(') > count(')')`, so the string is unbalanced.

**QED.**

---

## Lock-Free Treiber Stack: Linearizability Proof

### Linearizability

A concurrent data structure is **linearizable** if every concurrent execution is equivalent to some sequential execution where each operation appears to take effect atomically at some point between its invocation and response.

### Treiber Stack Operations

```
Push(x):
    loop:
        oldTop = read(top)
        newNode = Node(x, next=oldTop)
        if CAS(top, oldTop, newNode):
            return

Pop():
    loop:
        oldTop = read(top)
        if oldTop == null: return EMPTY
        newTop = oldTop.next
        if CAS(top, oldTop, newTop):
            return oldTop.val
```

### Linearization Points

- **Push**: The linearization point is the successful `CAS(top, oldTop, newNode)`. At this instant, the element is atomically added to the stack.
- **Pop (success)**: The linearization point is the successful `CAS(top, oldTop, newTop)`. At this instant, the element is atomically removed.
- **Pop (empty)**: The linearization point is the `read(top)` that returns `null`, provided top is still null at that moment.

### Proof Sketch

1. **Safety**: Each successful CAS atomically swings the top pointer. Between the CAS of a push and the CAS of the corresponding pop, the element is visible at the top (or below other elements). No element is lost or duplicated.

2. **Lock-freedom**: In every execution, at least one thread makes progress. If a CAS fails, it means another thread's CAS succeeded, so global progress is guaranteed.

3. **Stack ordering**: The linked list rooted at `top` maintains LIFO order at all times. A push prepends to the head; a pop removes the head. Any linearization of the CAS operations produces a valid sequential stack history.

### ABA Problem and Correctness

The basic Treiber stack is vulnerable to ABA:
- Thread T1 reads `top = A`.
- T2 pops A, pops B, pushes A back.
- T1's CAS succeeds (`top` is A), but `A.next` may now be different.

**Fix**: Use a version-tagged pointer `(pointer, version)` and perform a double-width CAS. Each successful CAS increments the version, preventing ABA.

---

## Amortized Analysis of Dynamic Array Stack

A dynamic array stack starts with capacity `c` and doubles when full. Individual pushes may cost O(n) due to copying, but the amortized cost per push is O(1).

### Aggregate Method

Consider a sequence of `n` pushes starting from an empty stack with initial capacity 1.

Resizes occur at pushes 1, 2, 4, 8, 16, ..., costing 1 + 2 + 4 + 8 + ... + n/2 + n copies.

Total resize cost = 1 + 2 + 4 + ... + n <= 2n (geometric series).

Total cost of n pushes = n (for the pushes themselves) + 2n (for all resizes) = 3n.

**Amortized cost per push = 3n / n = O(1).**

### Potential Method

Define the potential function: **Phi(S) = 2 * size(S) - capacity(S)**

For a non-resizing push:
- Actual cost = 1
- Phi increases by 2 (size grows by 1)
- Amortized cost = 1 + 2 = 3

For a resizing push (size = capacity = k, new capacity = 2k):
- Actual cost = 1 + k (push + copy)
- Before: Phi = 2k - k = k
- After: Phi = 2(k+1) - 2k = 2
- Delta Phi = 2 - k
- Amortized cost = (1 + k) + (2 - k) = 3

Both cases give amortized cost **3 = O(1)**.

### Pop Amortized Analysis

If we shrink the array when `size < capacity/4` (not capacity/2, to avoid thrashing):
- Amortized cost of pop is also O(1).
- The 1/4 threshold prevents the pathological case where alternating push/pop near capacity boundary causes repeated resize/shrink.

---

## Stack-Sortable Permutations

A permutation `p = (p1, p2, ..., pn)` is **stack-sortable** if it can be sorted into `(1, 2, ..., n)` by passing elements through a single stack.

### Algorithm

```
Input sequence: p1, p2, ..., pn
Output sequence: (must be 1, 2, 3, ..., n)
Stack: auxiliary storage

For each element in input:
    Push it onto the stack.
    While stack top equals the next expected output value:
        Pop and output it.
```

### Characterization (Knuth, 1968)

A permutation is stack-sortable if and only if it **avoids the pattern 231**.

A permutation contains the pattern 231 if there exist indices `i < j < k` such that `p[k] < p[i] < p[j]`.

**Example:**
- `(2, 3, 1)` contains the pattern 231 (at positions 1, 2, 3) and is NOT stack-sortable.
- `(2, 1, 3)` avoids 231 and IS stack-sortable.

### Proof Sketch

**If 231 exists, sorting fails:** Suppose `p[i] < p[j]` and `p[k] < p[i]` with `i < j < k`. When processing `p[j]`, `p[i]` is already on the stack. `p[j] > p[i]`, so `p[j]` is pushed on top of `p[i]`. Later, `p[k] < p[i]` arrives, but we need to output `p[k]` before `p[i]`. However, `p[j]` sits on top of `p[i]`, blocking access. Since `p[j] > p[i] > p[k]`, we cannot output `p[j]` before `p[k]` either. Deadlock.

**If 231 is avoided, sorting succeeds:** Without the 231 pattern, whenever we need to output the next value, it is either at the input head or the stack top. The proof proceeds by induction on `n`.

---

## Catalan Numbers Connection

The number of stack-sortable permutations of length `n` is the **n-th Catalan number**:

```
C(n) = (2n)! / ((n+1)! * n!)
```

First values: 1, 1, 2, 5, 14, 42, 132, 429, 1430, ...

### Why Catalan Numbers?

Stack-sortable permutations correspond bijectively to several Catalan-counted objects:

1. **Valid parenthesizations**: `n` pairs of matched parentheses. Each push is `(`, each pop is `)`.

2. **Binary trees**: `n`-node binary trees. The stack operations during sorting trace an in-order traversal.

3. **Dyck paths**: Paths from `(0,0)` to `(2n,0)` using up-steps `(1,1)` and down-steps `(1,-1)` that never go below the x-axis. Push = up-step, pop = down-step.

4. **Non-crossing partitions**: Partitions of `{1, ..., n}` where no two blocks "cross."

### Generating Function

The ordinary generating function for Catalan numbers satisfies:

```
C(x) = 1 + x * C(x)^2
```

Solving: `C(x) = (1 - sqrt(1 - 4x)) / (2x)`

### Asymptotic Growth

```
C(n) ~ 4^n / (n^(3/2) * sqrt(pi))
```

This means the fraction of all `n!` permutations that are stack-sortable shrinks rapidly. For large `n`, almost no permutation is stack-sortable with a single stack.

### Two-Stack Sortable Permutations

With two stacks in series, the set of sortable permutations is larger but still does not cover all permutations. West (1990) showed that 2-stack sortable permutations avoid a specific set of patterns and are counted by a different sequence.

---

## Summary

| Topic                       | Key Result                                                      |
| --------------------------- | --------------------------------------------------------------- |
| Stack ADT axioms            | 4 axioms fully characterize the stack                           |
| Balanced parentheses proof  | Loop invariant: stack size = open count - close count           |
| Treiber stack linearizability | CAS is the linearization point; lock-free but ABA-vulnerable |
| Amortized dynamic array     | Doubling gives O(1) amortized push via potential method          |
| Stack-sortable permutations | Exactly the 231-avoiding permutations (Knuth)                   |
| Catalan numbers             | C(n) counts stack-sortable permutations, Dyck paths, binary trees |
