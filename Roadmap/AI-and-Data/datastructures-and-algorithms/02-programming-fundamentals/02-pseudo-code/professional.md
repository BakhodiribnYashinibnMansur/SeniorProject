# Pseudo Code — Mathematical Foundations and Formal Notation

## Table of Contents

1. [Formal Algorithm Specification](#formal-algorithm-specification)
2. [CLRS-Style Pseudo Code](#clrs-style-pseudo-code)
3. [Correctness Proofs from Pseudo Code](#correctness-proofs-from-pseudo-code)
4. [Recurrence Relations and Master Theorem](#recurrence-relations)
5. [Formal Verification](#formal-verification)
6. [Summary](#summary)

---

## Formal Algorithm Specification

```text
An algorithm A is a tuple (I, O, S, T) where:
  I = set of valid inputs (preconditions)
  O = set of valid outputs (postconditions)
  S = sequence of well-defined steps
  T = termination argument (proof that S halts for all i ∈ I)

Algorithm A is CORRECT if:
  ∀ i ∈ I: A(i) ∈ O ∧ A(i) satisfies the postcondition

Algorithm A is TOTAL if:
  ∀ i ∈ I: A(i) terminates in finite steps

Algorithm A is EFFICIENT if:
  ∃ polynomial p: ∀ i ∈ I: steps(A, i) ≤ p(|i|)
```

---

## CLRS-Style Pseudo Code

The standard used in *Introduction to Algorithms* (Cormen, Leiserson, Rivest, Stein):

### Conventions

```text
1. Array indexing starts at 1 (not 0)
2. A.length = number of elements in array A
3. A[i..j] = subarray from index i to j inclusive
4. Assignment: x = expr (not SET x = expr)
5. Keywords: if, then, else, while, do, for, to, downto, return
6. Comments: // right-aligned
7. Multiple assignments: x = y = 0
8. Boolean: TRUE, FALSE, NIL
9. Error: error "message"
```

### CLRS Insertion Sort

```text
INSERTION-SORT(A, n)
1  for j = 2 to n
2      key = A[j]
3      // Insert A[j] into sorted subarray A[1..j-1]
4      i = j - 1
5      while i > 0 and A[i] > key
6          A[i + 1] = A[i]
7          i = i - 1
8      A[i + 1] = key
```

### CLRS Binary Search

```text
BINARY-SEARCH(A, v, low, high)
1  if low > high
2      return NIL
3  mid = floor((low + high) / 2)
4  if v == A[mid]
5      return mid
6  else if v > A[mid]
7      return BINARY-SEARCH(A, v, mid + 1, high)
8  else
9      return BINARY-SEARCH(A, v, low, mid - 1)
```

### CLRS Merge Sort

```text
MERGE-SORT(A, p, r)
1  if p >= r                      // zero or one element
2      return
3  q = floor((p + r) / 2)        // midpoint
4  MERGE-SORT(A, p, q)           // sort left half
5  MERGE-SORT(A, q + 1, r)       // sort right half
6  MERGE(A, p, q, r)             // merge two halves

MERGE(A, p, q, r)
1  n₁ = q - p + 1
2  n₂ = r - q
3  let L[1..n₁+1] and R[1..n₂+1] be new arrays
4  for i = 1 to n₁
5      L[i] = A[p + i - 1]
6  for j = 1 to n₂
7      R[j] = A[q + j]
8  L[n₁ + 1] = ∞                 // sentinel
9  R[n₂ + 1] = ∞
10 i = 1
11 j = 1
12 for k = p to r
13     if L[i] ≤ R[j]
14         A[k] = L[i]
15         i = i + 1
16     else
17         A[k] = R[j]
18         j = j + 1
```

---

## Correctness Proofs from Pseudo Code

### Loop Invariant for Insertion Sort

```text
Claim: INSERTION-SORT correctly sorts A[1..n] in non-decreasing order.

Loop Invariant: At the start of each iteration of the for loop (line 1),
the subarray A[1..j-1] consists of the elements originally in A[1..j-1]
but in sorted order.

Proof:
  Initialization (j=2):
    A[1..1] has one element, which is trivially sorted. ✓

  Maintenance (j → j+1):
    Assume A[1..j-1] is sorted at start of iteration j.
    The while loop (lines 5-7) moves all elements in A[1..j-1]
    that are greater than key one position to the right.
    Line 8 places key in the correct position.
    Result: A[1..j] is sorted. ✓

  Termination (j = n+1):
    The loop terminates when j = n + 1.
    By the invariant, A[1..n] is sorted. ✓

  QED
```

### Correctness of Binary Search

```text
Claim: BINARY-SEARCH(A, v, 1, n) returns index i such that A[i] = v,
or NIL if v ∉ A, given that A is sorted.

Loop Invariant: If v ∈ A, then v ∈ A[low..high].

Proof:
  Initialization: low=1, high=n → v ∈ A[1..n] if v ∈ A. ✓

  Maintenance:
    Case 1: v == A[mid] → return mid. Correct. ✓
    Case 2: v > A[mid] → since A is sorted, v ∉ A[low..mid].
            New search: A[mid+1..high]. Invariant holds. ✓
    Case 3: v < A[mid] → v ∉ A[mid..high].
            New search: A[low..mid-1]. Invariant holds. ✓

  Termination:
    The range [low..high] shrinks by at least half each call.
    After at most ⌈log₂ n⌉ + 1 calls, low > high → return NIL. ✓

  QED
```

---

## Recurrence Relations and Master Theorem

### Deriving Recurrences from Pseudo Code

```text
Pattern 1: Single recursive call, linear work
  T(n) = T(n-1) + O(1)     → T(n) = O(n)
  Example: Factorial, linear recursion

Pattern 2: Single recursive call, halving
  T(n) = T(n/2) + O(1)     → T(n) = O(log n)
  Example: Binary search

Pattern 3: Two recursive calls, halving, linear merge
  T(n) = 2T(n/2) + O(n)    → T(n) = O(n log n)
  Example: Merge sort

Pattern 4: Two recursive calls, halving, constant work
  T(n) = 2T(n/2) + O(1)    → T(n) = O(n)
  Example: Tree traversal
```

### Master Theorem

```text
For recurrences of the form T(n) = aT(n/b) + O(n^d):

  Case 1: If d < log_b(a) → T(n) = O(n^{log_b(a)})
  Case 2: If d = log_b(a) → T(n) = O(n^d · log n)
  Case 3: If d > log_b(a) → T(n) = O(n^d)

Examples:
  Merge Sort:   a=2, b=2, d=1 → log₂(2) = 1 = d → Case 2 → O(n log n) ✓
  Binary Search: a=1, b=2, d=0 → log₂(1) = 0 = d → Case 2 → O(log n) ✓
  Strassen:     a=7, b=2, d=2 → log₂(7) ≈ 2.81 > 2 → Case 1 → O(n^{2.81}) ✓
```

### Akra-Bazzi Theorem (Generalization)

```text
For more complex recurrences where subproblems have different sizes:
  T(n) = Σ aᵢ T(n/bᵢ) + g(n)

Find p such that Σ aᵢ / bᵢ^p = 1

Then: T(n) = Θ(n^p (1 + ∫₁ⁿ g(u)/u^{p+1} du))
```

---

## Formal Verification

### Hoare Logic

```text
{P} S {Q}    means: If precondition P holds and statement S executes,
                     then postcondition Q holds afterward.

Assignment axiom:
  {Q[x/e]} x = e {Q}

Sequence rule:
  {P} S₁ {R}    {R} S₂ {Q}
  ─────────────────────────
       {P} S₁; S₂ {Q}

If-then-else rule:
  {P ∧ B} S₁ {Q}    {P ∧ ¬B} S₂ {Q}
  ─────────────────────────────────────
        {P} if B then S₁ else S₂ {Q}

While rule:
  {I ∧ B} S {I}
  ──────────────────────────
  {I} while B do S {I ∧ ¬B}
  (I is the loop invariant)
```

### Example: Proving x = x + 1 increments x

```text
Claim: {x = n} x = x + 1 {x = n + 1}

By assignment axiom:
  Q = (x = n + 1)
  Q[x/(x+1)] = (x + 1 = n + 1) = (x = n) = P ✓

Therefore: {x = n} x = x + 1 {x = n + 1} ✓
```

### Weakest Precondition (wp)

```text
wp(S, Q) = the weakest precondition P such that {P} S {Q}

wp(x = e, Q) = Q[x/e]
wp(S₁; S₂, Q) = wp(S₁, wp(S₂, Q))
wp(if B then S₁ else S₂, Q) = (B → wp(S₁, Q)) ∧ (¬B → wp(S₂, Q))
```

---

## Summary

At the professional level, pseudo code connects to formal computer science: CLRS notation for textbook-quality algorithms, loop invariants for correctness proofs, recurrence relations for complexity analysis, and Hoare logic for formal verification. This is the foundation for publishing algorithms in papers, proving correctness guarantees, and understanding the theoretical limits of computation.
