# Time vs Space Complexity — Mathematical Foundations and Complexity Theory

## Table of Contents

1. [Formal Definition](#formal-definition)
2. [Correctness Proof — Loop Invariants](#correctness-proof)
3. [Amortized Analysis](#amortized-analysis)
4. [NP-Completeness and Polynomial Reductions](#np-completeness)
5. [Randomized Algorithm Probability Bounds](#randomized-algorithms)
6. [Cache-Oblivious Analysis](#cache-oblivious-analysis)
7. [Comparison with Alternatives](#comparison)
8. [Summary](#summary)

---

## Formal Definition

```text
Definition: Time Complexity
  Let A be an algorithm and I be an input of size n.
  T_A(n) = max { t(A, I) : |I| = n }
  where t(A, I) is the number of basic operations A performs on input I.
  
  We say A runs in time O(f(n)) if there exist constants c > 0 and n₀ ≥ 0
  such that T_A(n) ≤ c · f(n) for all n ≥ n₀.

Definition: Space Complexity
  S_A(n) = max { s(A, I) : |I| = n }
  where s(A, I) is the maximum number of memory cells A uses on input I
  (beyond the input itself — auxiliary space).
  
  We say A uses space O(g(n)) if there exist constants c > 0 and n₀ ≥ 0
  such that S_A(n) ≤ c · g(n) for all n ≥ n₀.

Definition: Time-Space Trade-Off (Formal)
  For a computational problem P, define:
    T(P, S) = min { T_A(n) : S_A(n) ≤ S } 
  This is the minimum time achievable by any algorithm using at most S space.
  
  A time-space trade-off exists for P if T(P, S) is strictly decreasing in S
  for some range of S values.
```

### The Computation Model: RAM Machine

```text
The RAM (Random Access Machine) model:
  - Memory: infinite array M[0], M[1], M[2], ...
  - Each cell holds an integer of O(log n) bits (word-RAM model)
  - Operations: read M[i], write M[i], arithmetic (+, -, ×, ÷), comparison — each O(1)
  - Program counter: sequential execution with conditional jumps
  
  Time = number of operations executed
  Space = number of distinct memory cells accessed (auxiliary)
```

---

## Correctness Proof — Loop Invariants

### Proof: In-Place Array Reversal is Correct

```text
Algorithm: reverseInPlace(A, n)
  left ← 0, right ← n - 1
  while left < right:
    swap A[left] and A[right]
    left ← left + 1
    right ← right - 1

Claim: After termination, A[i] = A_orig[n-1-i] for all 0 ≤ i < n.

Loop Invariant I(k): After k iterations:
  (1) A[i] = A_orig[n-1-i] for 0 ≤ i < k  (left portion swapped)
  (2) A[i] = A_orig[n-1-i] for n-k ≤ i < n  (right portion swapped)
  (3) A[i] = A_orig[i] for k ≤ i < n-k  (middle unchanged)
  (4) left = k, right = n-1-k

Base case (k=0):
  No swaps performed. A[i] = A_orig[i] for all i.
  left = 0, right = n-1. ✓

Inductive step: Assume I(k) holds. Show I(k+1) holds.
  Guard: left < right → k < n-1-k → k < (n-1)/2.
  Swap A[k] and A[n-1-k]:
    A[k] = A_orig[n-1-k] and A[n-1-k] = A_orig[k].
  Now:
    Swapped left portion: 0 ≤ i ≤ k → A[i] = A_orig[n-1-i] ✓
    Swapped right portion: n-1-k ≤ i < n → A[i] = A_orig[n-1-i] ✓
    Middle: k+1 ≤ i < n-1-k → A[i] = A_orig[i] ✓
    left = k+1, right = n-2-k ✓
  I(k+1) holds.

Termination:
  left increases by 1 and right decreases by 1 each iteration.
  Gap = right - left decreases by 2 each step.
  Terminates when left ≥ right, i.e., after ⌊n/2⌋ iterations.

Postcondition:
  At termination, k = ⌊n/2⌋. The middle portion (3) is empty or a single
  element (which equals its own reverse). Combined with (1) and (2),
  A[i] = A_orig[n-1-i] for all i. QED

Complexity:
  Time: O(n/2) = O(n) — one swap per iteration, ⌊n/2⌋ iterations.
  Space: O(1) — only left, right, and one temp variable for swap.
```

#### Go

```go
// Time: O(n), Space: O(1) — proven correct above
func reverseInPlace(arr []int) {
    left, right := 0, len(arr)-1
    for left < right {
        arr[left], arr[right] = arr[right], arr[left]
        left++
        right--
    }
}
```

#### Java

```java
public static void reverseInPlace(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
        left++;
        right--;
    }
}
```

#### Python

```python
def reverse_in_place(arr):
    left, right = 0, len(arr) - 1
    while left < right:
        arr[left], arr[right] = arr[right], arr[left]
        left += 1
        right -= 1
```

---

## Amortized Analysis

### Dynamic Array: Three Methods

A dynamic array doubles its capacity when full. Individual push can cost O(n) (resize), but amortized cost is O(1).

### Aggregate Method

```text
Consider n push operations starting from an empty array with capacity 1.

Resizes occur at pushes 1, 2, 3, 5, 9, 17, ..., i.e., at push i where
i-1 is a power of 2. Cost of resize at capacity c is c (copy all elements).

Total cost = n (one write per push) + Σ 2^k for k=0 to ⌊log₂(n-1)⌋
           = n + (2^(⌊log₂(n-1)⌋+1) - 1)
           < n + 2n = 3n

Amortized cost per push = 3n / n = 3 = O(1). QED
```

### Potential Method

```text
Define potential function:
  Φ(D) = 2 · size(D) - capacity(D)

Properties:
  - After resize: size = capacity/2, so Φ = 2(cap/2) - cap = 0
  - Just before resize: size = capacity, so Φ = 2·cap - cap = cap
  - Φ(D₀) = 0 (empty array), Φ(Dᵢ) ≥ 0 for all i ✓

Amortized cost of push (no resize):
  â = c + Φ(Dᵢ) - Φ(Dᵢ₋₁) = 1 + 2 = 3

Amortized cost of push (with resize from capacity c to 2c):
  actual cost = 1 (push) + c (copy)
  Φ before = 2c - c = c
  Φ after = 2(c/2 + 1) - 2c = 2 - c  (wait, size after = c+1, capacity = 2c)
  Φ after = 2(c+1) - 2c = 2
  â = (1 + c) + 2 - c = 3

Every push has amortized cost 3 = O(1). QED
```

### Accounting Method

```text
Charge each push $3:
  - $1 to pay for the push itself
  - $2 saved as credit on the pushed element

When a resize occurs (capacity doubles from c to 2c):
  - c/2 elements entered since last resize, each with $2 credit
  - Total credit: c/2 × $2 = $c
  - Cost of copying c elements: $c
  - Credit exactly covers the resize cost ✓

Since each push pays $3 and total credit never goes negative,
amortized cost per push = O(1). QED
```

#### Go

```go
package main

import "fmt"

type DynamicArray struct {
    data     []int
    size     int
    capacity int
    resizes  int
}

func NewDynamicArray() *DynamicArray {
    return &DynamicArray{data: make([]int, 1), capacity: 1}
}

func (da *DynamicArray) Push(val int) {
    if da.size == da.capacity {
        da.capacity *= 2
        newData := make([]int, da.capacity)
        copy(newData, da.data)
        da.data = newData
        da.resizes++
    }
    da.data[da.size] = val
    da.size++
}

func main() {
    da := NewDynamicArray()
    for i := 0; i < 1000000; i++ {
        da.Push(i)
    }
    fmt.Printf("Size: %d, Capacity: %d, Resizes: %d\n",
        da.size, da.capacity, da.resizes)
    // Resizes ≈ 20 (log₂(1,000,000)), confirming amortized O(1)
}
```

#### Java

```java
public class DynamicArray {
    private int[] data;
    private int size = 0;
    private int capacity;
    private int resizes = 0;

    public DynamicArray() {
        capacity = 1;
        data = new int[capacity];
    }

    public void push(int val) {
        if (size == capacity) {
            capacity *= 2;
            int[] newData = new int[capacity];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
            resizes++;
        }
        data[size++] = val;
    }

    public static void main(String[] args) {
        DynamicArray da = new DynamicArray();
        for (int i = 0; i < 1_000_000; i++) da.push(i);
        System.out.printf("Size: %d, Capacity: %d, Resizes: %d%n",
            da.size, da.capacity, da.resizes);
    }
}
```

#### Python

```python
class DynamicArray:
    def __init__(self):
        self._data = [None]
        self._size = 0
        self._capacity = 1
        self._resizes = 0

    def push(self, val):
        if self._size == self._capacity:
            self._capacity *= 2
            new_data = [None] * self._capacity
            for i in range(self._size):
                new_data[i] = self._data[i]
            self._data = new_data
            self._resizes += 1
        self._data[self._size] = val
        self._size += 1

da = DynamicArray()
for i in range(1_000_000):
    da.push(i)
print(f"Size: {da._size}, Capacity: {da._capacity}, Resizes: {da._resizes}")
```

---

## NP-Completeness and Polynomial Reductions

```text
Time-space trade-offs connect to complexity theory through the following:

Theorem (Savitch, 1970):
  NSPACE(f(n)) ⊆ DSPACE(f(n)²)
  
  Any problem solvable by a nondeterministic Turing machine using f(n) space
  can be solved deterministically using O(f(n)²) space.
  
  Implication: NL ⊆ DSPACE(log²n) — nondeterministic log-space problems
  can be solved in deterministic log²(n) space. This is a fundamental
  time-space trade-off at the complexity class level.

Theorem (Time-Space Trade-Off for Sorting):
  Any comparison-based sorting algorithm using S(n) space requires
  T(n) = Ω(n² / S(n)) time in the worst case (Borodin-Cook, 1982).
  
  Corollaries:
    S = O(1)    → T = Ω(n²)    (e.g., insertion sort, selection sort)
    S = O(n)    → T = Ω(n)      (but merge sort achieves O(n log n))
    S = O(√n)   → T = Ω(n^{3/2})
  
  Merge sort uses O(n) space to achieve O(n log n) time.
  In-place merge sort exists but with worse constant factors.

Problem: Subset Sum
  Input: Set S = {s₁, ..., sₙ} of positive integers, target T
  Question: Is there a subset of S that sums to exactly T?
  
  Time-space trade-off:
    - Brute force: O(2ⁿ) time, O(n) space (enumerate all subsets)
    - DP: O(nT) time, O(T) space (pseudo-polynomial)
    - Meet-in-the-middle: O(2^{n/2}) time, O(2^{n/2}) space
  
  The meet-in-the-middle approach splits S into two halves, enumerates all
  subset sums for each half (2^{n/2} each), then uses a hash set to check
  if any pair sums to T. This is a direct time-space trade-off:
  using O(2^{n/2}) space reduces time from O(2ⁿ) to O(2^{n/2}).
```

---

## Randomized Algorithm Probability Bounds

```text
Theorem: Expected time of randomized quicksort is O(n log n).

Proof sketch:
  Let X = total number of comparisons.
  Let X_ij = indicator variable: X_ij = 1 iff elements z_i and z_j are compared.
  
  By linearity of expectation:
    E[X] = Σᵢ<ⱼ E[X_ij] = Σᵢ<ⱼ Pr[z_i and z_j are compared]
  
  Elements z_i and z_j are compared iff one of them is chosen as pivot before
  any element between them. Pr[z_i or z_j is first pivot in {z_i,...,z_j}] = 2/(j-i+1).
  
  E[X] = Σᵢ₌₁ⁿ Σⱼ₌ᵢ₊₁ⁿ 2/(j-i+1)
       = Σᵢ₌₁ⁿ Σₖ₌₂ⁿ 2/k        (substituting k = j-i+1)
       ≤ 2n · Hₙ = 2n · O(ln n) = O(n log n). QED

High-probability bound (Chernoff):
  Pr[X ≥ 4n ln n] ≤ 1/n²
  
  With probability ≥ 1 - 1/n², quicksort runs in O(n log n) time.
  Space: O(log n) expected stack depth (O(n) worst case).

Theorem: Hash table with universal hashing.
  For n elements in a table of size m = O(n):
    Expected chain length: O(1)
    Expected time per operation: O(1)
    Space: O(n)
  
  Using Chernoff bounds, with high probability no chain exceeds O(log n / log log n).
```

---

## Cache-Oblivious Analysis

```text
Memory hierarchy model:
  - CPU registers: ~1 ns access
  - L1 cache: ~1 ns, 32-64 KB
  - L2 cache: ~4 ns, 256 KB - 1 MB
  - L3 cache: ~10 ns, 2-32 MB  
  - Main memory: ~100 ns, 4-128 GB
  - SSD: ~100 μs
  - HDD: ~10 ms

The external memory (I/O) model:
  - Memory size: M words
  - Block/cache line size: B words
  - Cost metric: number of block transfers (I/Os)

Array scan: O(N/B) I/Os — optimal due to spatial locality
Linked list scan: O(N) I/Os — one cache miss per node in worst case
Binary search: O(log₂(N/B)) I/Os — each probe loads one block
B-tree search: O(log_B(N)) I/Os — each node fits in one block

Cache-oblivious algorithms achieve optimal I/O complexity without
knowing M or B:

Array scan: O(N/B) — automatic, sequential access
Van Emde Boas layout for binary tree:
  Store tree recursively: top half subtree, then bottom half subtrees.
  Search cost: O(log_B(N)) I/Os — matches B-tree without knowing B.

Merge sort (cache-oblivious):
  Standard merge sort: O((N/B) · log_{M/B}(N/B)) I/Os
  This matches the lower bound for comparison-based sorting in the I/O model.
  Key: merge step naturally scans arrays sequentially → O(N/B) I/Os per level.
```

---

## Comparison with Alternatives

| Problem | Algorithm | Time | Space | I/O (Cache) | Notes |
|---------|-----------|------|-------|-------------|-------|
| Search (sorted) | Binary search | O(log n) | O(1) | O(log(n/B)) | Cache-unfriendly jumps |
| Search (sorted) | B-tree search | O(log_B n) | O(n) | O(log_B n) | Cache-optimal |
| Sort | Merge sort | O(n log n) | O(n) | O((n/B)log_{M/B}(n/B)) | I/O optimal |
| Sort | Quicksort | O(n log n) exp. | O(log n) | O((n/B)log_{M/B}(n/B)) exp. | Less space than merge |
| Sort (in-place) | Heap sort | O(n log n) | O(1) | O(n log n) | Cache-unfriendly |
| Duplicate detection | Brute force | O(n²) | O(1) | O(n²/B) | No extra space |
| Duplicate detection | Hash set | O(n) | O(n) | O(n/B) exp. | Space for time |
| Range sum query | Prefix sum | O(1) query | O(n) | O(1) | Precompute O(n) |
| Range sum query | Naive | O(n) query | O(1) | O(n/B) | No precomputation |

---

## Summary

At the professional level, time-space complexity is analyzed through formal mathematical frameworks. We prove algorithm correctness with loop invariants, establish amortized bounds through aggregate, potential, and accounting methods, and connect to complexity theory through results like Savitch's theorem and time-space trade-offs for sorting. Randomized analysis provides expected-case guarantees via linearity of expectation and Chernoff bounds. Cache-oblivious analysis bridges the gap between asymptotic theory and hardware reality, showing that the memory hierarchy fundamentally changes which algorithms are practical. The interplay between time, space, and I/O complexity is the foundation of modern algorithm engineering.
