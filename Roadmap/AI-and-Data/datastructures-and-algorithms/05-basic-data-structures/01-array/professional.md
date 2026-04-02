# Array -- Professional / Theoretical Level

## Table of Contents

- [Formal Array ADT Specification](#formal-array-adt-specification)
- [Amortized Analysis of Dynamic Arrays](#amortized-analysis-of-dynamic-arrays)
  - [Aggregate Method](#aggregate-method)
  - [Accounting Method](#accounting-method)
  - [Potential Method](#potential-method)
- [Cache-Oblivious Array Algorithms](#cache-oblivious-array-algorithms)
  - [The External Memory Model](#the-external-memory-model)
  - [Scanning and Permuting](#scanning-and-permuting)
  - [Cache-Oblivious Matrix Transpose](#cache-oblivious-matrix-transpose)
- [Lower Bounds for Array Operations](#lower-bounds-for-array-operations)
- [Succinct Arrays and Compact Representations](#succinct-arrays-and-compact-representations)
- [Summary](#summary)

---

## Formal Array ADT Specification

An **Abstract Data Type (ADT)** defines a data structure by its operations and their semantics, independent of implementation.

### Signature

```
ADT Array<T>

Types:
    T         -- element type
    Index     -- non-negative integer in [0, n-1]
    Size      -- non-negative integer

Operations:
    new(n: Size) -> Array<T>
        -- Create array of size n, all elements initialized to default(T)
        -- Pre:  n >= 0
        -- Post: size(result) = n

    get(A: Array<T>, i: Index) -> T
        -- Return element at index i
        -- Pre:  0 <= i < size(A)
        -- Post: result = A[i], A is unchanged

    set(A: Array<T>, i: Index, v: T) -> Array<T>
        -- Set element at index i to value v
        -- Pre:  0 <= i < size(A)
        -- Post: result[i] = v, result[j] = A[j] for all j != i

    size(A: Array<T>) -> Size
        -- Return number of elements
        -- Post: result >= 0, A is unchanged

Axioms:
    1. get(set(A, i, v), i) = v                           -- setting then getting same index
    2. get(set(A, i, v), j) = get(A, j)   for i != j      -- setting does not affect other indices
    3. size(new(n)) = n                                     -- new array has declared size
    4. size(set(A, i, v)) = size(A)                        -- set does not change size
```

### Dynamic Array ADT Extension

```
ADT DynamicArray<T> extends Array<T>

Additional Operations:
    append(A: DynamicArray<T>, v: T) -> DynamicArray<T>
        -- Add element to the end
        -- Post: size(result) = size(A) + 1
        -- Post: get(result, size(A)) = v
        -- Post: get(result, i) = get(A, i) for 0 <= i < size(A)

    removeLast(A: DynamicArray<T>) -> (DynamicArray<T>, T)
        -- Remove and return last element
        -- Pre:  size(A) > 0
        -- Post: size(result.array) = size(A) - 1
        -- Post: result.element = get(A, size(A) - 1)

    insertAt(A: DynamicArray<T>, i: Index, v: T) -> DynamicArray<T>
        -- Insert v at index i, shifting elements right
        -- Pre:  0 <= i <= size(A)
        -- Post: size(result) = size(A) + 1
        -- Post: get(result, i) = v
        -- Post: get(result, j) = get(A, j)     for 0 <= j < i
        -- Post: get(result, j+1) = get(A, j)   for i <= j < size(A)

    deleteAt(A: DynamicArray<T>, i: Index) -> (DynamicArray<T>, T)
        -- Remove element at index i, shifting elements left
        -- Pre:  0 <= i < size(A)
        -- Post: size(result.array) = size(A) - 1
        -- Post: result.element = get(A, i)
```

---

## Amortized Analysis of Dynamic Arrays

We prove that n consecutive `append` operations on an initially empty dynamic array cost O(n) total, giving O(1) amortized cost per append.

Assumptions: when the array is full (length = capacity), we allocate a new array of capacity 2 * old_capacity and copy all elements.

### Aggregate Method

Count the total work over n appends:
- Each append costs 1 (placing the element).
- Resizes occur when length reaches 1, 2, 4, 8, ..., 2^k where 2^k <= n.
- Resize at capacity c copies c elements.

Total cost = n (placements) + (1 + 2 + 4 + ... + 2^(floor(log2(n))))
           = n + (2^(floor(log2(n))+1) - 1)
           < n + 2n
           = 3n

Amortized cost per append = 3n / n = **O(1)**.

### Accounting Method

We charge each append operation **3 units** (its amortized cost):
- 1 unit to place the element.
- 2 units saved as "credit" on the element.

When a resize occurs at capacity c, we need to copy c elements. The c/2 elements added since the last resize each have 2 credits saved, providing c/2 * 2 = c credits total -- exactly enough to pay for copying c elements.

Every operation is paid for. No debt accumulates. Therefore, the amortized cost per append is **3 = O(1)**.

### Potential Method

Define a potential function:

```
Phi(D) = 2 * size(D) - capacity(D)
```

Where `size` is the number of elements and `capacity` is the allocated capacity.

**Properties of Phi:**
- After a resize: size = capacity/2, so Phi = 2*(capacity/2) - capacity = 0.
- Just before a resize: size = capacity, so Phi = 2*capacity - capacity = capacity.
- Phi >= 0 always (since size >= capacity/2 after any resize).

**Amortized cost of append without resize:**

```
c_hat = c_actual + Phi(after) - Phi(before)
      = 1 + (2*(size+1) - capacity) - (2*size - capacity)
      = 1 + 2
      = 3
```

**Amortized cost of append with resize (at capacity = old_cap, new capacity = 2*old_cap):**

```
c_actual = 1 + old_cap     (1 for placement, old_cap for copying)

Phi(before) = 2*old_cap - old_cap = old_cap
Phi(after)  = 2*(old_cap + 1) - 2*old_cap = 2

c_hat = (1 + old_cap) + 2 - old_cap = 3
```

In both cases, the amortized cost is **3 = O(1)**.

### Growth factor analysis

For growth factor alpha (e.g., alpha = 1.5 for Java ArrayList):

```
Total copy cost over n appends = sum_{k=0}^{log_alpha(n)} alpha^k
                                = (alpha^(log_alpha(n)+1) - 1) / (alpha - 1)
                                = O(n * alpha / (alpha - 1))
```

For alpha = 2: O(2n) copies. For alpha = 1.5: O(3n) copies. Smaller growth factors mean more copies but less wasted space (at most 1/alpha unused capacity).

---

## Cache-Oblivious Array Algorithms

### The External Memory Model

The **external memory model** (Aggarwal-Vitter, 1988) has two levels:
- **Cache** (fast memory) of size M, organized into blocks of size B.
- **Disk/RAM** (slow memory) of unlimited size.

The cost of an algorithm is measured in **I/O operations** (block transfers).

A **cache-oblivious** algorithm achieves optimal I/O complexity without knowing M or B.

### Scanning and Permuting

**Scanning** n elements requires Theta(n/B) I/O operations -- optimal because you read B elements per block transfer.

Linear array scan is naturally cache-oblivious: accessing elements sequentially triggers sequential block reads regardless of B.

**Permuting** n elements requires Theta(min(n, n/B * log_{M/B}(n/B))) I/O operations. For arbitrary permutations, this can be Theta(n) in the worst case when M is small.

### Cache-Oblivious Matrix Transpose

The naive row-by-row scan of a column is cache-hostile for large matrices (each element is in a different cache line when the matrix exceeds cache size).

The cache-oblivious approach uses **recursive block decomposition**:

```
Transpose(A, rows, cols):
    if rows * cols fits in cache:
        transpose naively
    else if rows >= cols:
        Transpose(top half of A)
        Transpose(bottom half of A)
    else:
        Transpose(left half of A)
        Transpose(right half of A)
```

This achieves O(n^2 / B) I/O operations for an n x n matrix, which is optimal, without knowing B.

**Go (recursive transpose):**

```go
func cacheObliviousTranspose(src, dst []float64, srcRows, srcCols int,
    r0, r1, c0, c1 int) {
    dr := r1 - r0
    dc := c1 - c0
    if dr <= 32 && dc <= 32 {
        // Base case: small enough to fit in cache
        for r := r0; r < r1; r++ {
            for c := c0; c < c1; c++ {
                dst[c*srcRows+r] = src[r*srcCols+c]
            }
        }
        return
    }
    if dr >= dc {
        mid := (r0 + r1) / 2
        cacheObliviousTranspose(src, dst, srcRows, srcCols, r0, mid, c0, c1)
        cacheObliviousTranspose(src, dst, srcRows, srcCols, mid, r1, c0, c1)
    } else {
        mid := (c0 + c1) / 2
        cacheObliviousTranspose(src, dst, srcRows, srcCols, r0, r1, c0, mid)
        cacheObliviousTranspose(src, dst, srcRows, srcCols, r0, r1, mid, c1)
    }
}
```

**Java:**

```java
public static void cacheObliviousTranspose(double[] src, double[] dst,
        int srcRows, int srcCols, int r0, int r1, int c0, int c1) {
    int dr = r1 - r0, dc = c1 - c0;
    if (dr <= 32 && dc <= 32) {
        for (int r = r0; r < r1; r++)
            for (int c = c0; c < c1; c++)
                dst[c * srcRows + r] = src[r * srcCols + c];
        return;
    }
    if (dr >= dc) {
        int mid = (r0 + r1) / 2;
        cacheObliviousTranspose(src, dst, srcRows, srcCols, r0, mid, c0, c1);
        cacheObliviousTranspose(src, dst, srcRows, srcCols, mid, r1, c0, c1);
    } else {
        int mid = (c0 + c1) / 2;
        cacheObliviousTranspose(src, dst, srcRows, srcCols, r0, r1, c0, mid);
        cacheObliviousTranspose(src, dst, srcRows, srcCols, r0, r1, mid, c1);
    }
}
```

**Python:**

```python
def cache_oblivious_transpose(src, dst, src_rows, src_cols, r0, r1, c0, c1):
    dr, dc = r1 - r0, c1 - c0
    if dr <= 32 and dc <= 32:
        for r in range(r0, r1):
            for c in range(c0, c1):
                dst[c * src_rows + r] = src[r * src_cols + c]
        return
    if dr >= dc:
        mid = (r0 + r1) // 2
        cache_oblivious_transpose(src, dst, src_rows, src_cols, r0, mid, c0, c1)
        cache_oblivious_transpose(src, dst, src_rows, src_cols, mid, r1, c0, c1)
    else:
        mid = (c0 + c1) // 2
        cache_oblivious_transpose(src, dst, src_rows, src_cols, r0, r1, c0, mid)
        cache_oblivious_transpose(src, dst, src_rows, src_cols, r0, r1, mid, c1)
```

---

## Lower Bounds for Array Operations

### Access lower bound

In the **cell probe model** (Yao, 1981), accessing an element in an array of n elements requires **Omega(1)** probes. Arrays achieve this bound -- they are optimal for access.

### Search lower bound

For comparison-based search in a sorted array, the information-theoretic lower bound is **Omega(log n)** comparisons. Binary search achieves this and is therefore optimal.

**Proof sketch:** A sorted array of n elements has n possible positions for the target. Each comparison eliminates at most half the candidates. After k comparisons, at most 2^k candidates remain. To reach 1 candidate: 2^k >= n, so k >= log2(n).

### Insertion/Deletion lower bound

Maintaining a sorted array under insertions requires **Omega(n)** element moves per insertion in the worst case (in the comparison model). This is because inserting at position 0 requires shifting all n elements.

This lower bound motivates balanced BSTs (O(log n) insertion) and B-trees (O(log_B n) I/O operations).

### Partial sums lower bound

The **partial sums problem**: maintain an array A[0..n-1] supporting:
- `update(i, delta)`: A[i] += delta
- `prefixSum(k)`: return A[0] + A[1] + ... + A[k]

Lower bound (Patrascu and Demaine, 2004): any data structure must have **Omega(log n / log log n)** amortized time for at least one of the two operations in the cell probe model with word size O(log n).

Fenwick trees achieve O(log n) for both operations, which is near-optimal.

---

## Succinct Arrays and Compact Representations

A **succinct data structure** uses space close to the information-theoretic minimum while supporting efficient operations.

### Bit arrays (bitvectors)

A bit array stores n bits in n/w words (where w is the word size, typically 64). It supports:
- `access(i)`: return bit i in O(1)
- `rank(i)`: count 1-bits in positions 0..i in O(1) (with o(n) extra bits)
- `select(k)`: find position of k-th 1-bit in O(1) (with o(n) extra bits)

The rank/select structure uses:
- n bits for the bit array itself
- o(n) extra bits for auxiliary tables (two-level directory + precomputed blocks)

This is **succinct**: n + o(n) bits total, vs the information-theoretic minimum of n bits.

### Compressed arrays

For an array of n integers from a universe of size U:
- Naive: n * ceil(log2(U)) bits
- Elias-Fano encoding: n * (2 + ceil(log2(U/n))) bits for sorted arrays
- Variable-length encoding (varint, PrefixVarint): effective for skewed distributions

**Go -- simple bit array with rank support:**

```go
type BitArray struct {
    bits   []uint64
    n      int
    // rank directory
    blocks []int // cumulative popcount per block of 64 bits
}

func NewBitArray(n int) *BitArray {
    words := (n + 63) / 64
    return &BitArray{
        bits:   make([]uint64, words),
        n:      n,
        blocks: make([]int, words+1),
    }
}

func (ba *BitArray) Set(i int) {
    ba.bits[i/64] |= 1 << (uint(i) % 64)
}

func (ba *BitArray) Get(i int) bool {
    return ba.bits[i/64]&(1<<(uint(i)%64)) != 0
}

func (ba *BitArray) BuildRank() {
    cumulative := 0
    for i, word := range ba.bits {
        ba.blocks[i] = cumulative
        cumulative += popcount(word)
    }
    ba.blocks[len(ba.bits)] = cumulative
}

func (ba *BitArray) Rank(i int) int {
    block := i / 64
    offset := uint(i) % 64
    mask := (uint64(1) << (offset + 1)) - 1
    return ba.blocks[block] + popcount(ba.bits[block]&mask)
}

func popcount(x uint64) int {
    // Kernighan's bit counting
    count := 0
    for x != 0 {
        x &= x - 1
        count++
    }
    return count
}
```

**Java:**

```java
import java.util.BitSet;

public class SuccinctBitArray {
    private long[] words;
    private int[] rankDirectory;
    private int n;

    public SuccinctBitArray(int n) {
        this.n = n;
        this.words = new long[(n + 63) / 64];
        this.rankDirectory = new int[words.length + 1];
    }

    public void set(int i) { words[i / 64] |= 1L << (i % 64); }
    public boolean get(int i) { return (words[i / 64] & (1L << (i % 64))) != 0; }

    public void buildRank() {
        int cumulative = 0;
        for (int i = 0; i < words.length; i++) {
            rankDirectory[i] = cumulative;
            cumulative += Long.bitCount(words[i]);
        }
        rankDirectory[words.length] = cumulative;
    }

    public int rank(int i) {
        int block = i / 64;
        long mask = (1L << ((i % 64) + 1)) - 1;
        return rankDirectory[block] + Long.bitCount(words[block] & mask);
    }
}
```

**Python:**

```python
class SuccinctBitArray:
    def __init__(self, n):
        self.n = n
        self.words = [0] * ((n + 63) // 64)
        self.rank_dir = [0] * (len(self.words) + 1)

    def set(self, i):
        self.words[i // 64] |= 1 << (i % 64)

    def get(self, i):
        return bool(self.words[i // 64] & (1 << (i % 64)))

    def build_rank(self):
        cumulative = 0
        for i, word in enumerate(self.words):
            self.rank_dir[i] = cumulative
            cumulative += bin(word).count('1')
        self.rank_dir[len(self.words)] = cumulative

    def rank(self, i):
        block = i // 64
        mask = (1 << ((i % 64) + 1)) - 1
        return self.rank_dir[block] + bin(self.words[block] & mask).count('1')
```

---

## Summary

| Topic                     | Key Result                                                      |
| ------------------------- | --------------------------------------------------------------- |
| Formal ADT                | Array defined by get/set/size with algebraic axioms             |
| Amortized analysis        | Potential method proves O(1) amortized append with 2x growth    |
| Cache-oblivious           | Recursive decomposition achieves optimal I/O without knowing B  |
| Access lower bound        | Omega(1) -- arrays are optimal                                  |
| Search lower bound        | Omega(log n) -- binary search is optimal                        |
| Insertion lower bound     | Omega(n) -- motivates tree structures                           |
| Succinct bit arrays       | n + o(n) bits with O(1) rank and select                         |
