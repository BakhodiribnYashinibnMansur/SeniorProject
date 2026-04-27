# 0052. N-Queens II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking with Sets (Count Only)](#approach-1-backtracking-with-sets-count-only)
4. [Approach 2: Backtracking with Bitmasks](#approach-2-backtracking-with-bitmasks)
5. [Approach 3: Lookup Table (Precomputed)](#approach-3-lookup-table-precomputed)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [52. N-Queens II](https://leetcode.com/problems/n-queens-ii/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Backtracking` |

### Description

> The **n-queens puzzle** is the problem of placing `n` queens on an `n × n` chessboard such that no two queens attack each other.
>
> Given an integer `n`, return the **number of distinct solutions** to the n-queens puzzle.

### Examples

```
Example 1:
Input: n = 4
Output: 2
Explanation: There are two distinct solutions to the 4-queens puzzle.

Example 2:
Input: n = 1
Output: 1
```

### Constraints

- `1 <= n <= 9`

---

## Problem Breakdown

### 1. What is being asked?

This is the [N-Queens](../0051-n-queens/solution.md) problem with a smaller output: instead of every board, just **count** how many valid configurations exist. The constraints (no two queens share a row, column, or diagonal) and the search are identical -- we simply do not need to materialize each solution.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | Side length of the board and number of queens |

### 3. What is the output?

A single integer -- the count of valid placements.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 9` | Small enough for any reasonable backtracking algorithm; precomputation is even possible |
| Need only count | Skipping board construction lets the search run faster than [Problem 51](../0051-n-queens/solution.md) |

### 5. Step-by-step example analysis

#### Example: `n = 4`

```text
Same backtracking tree as N-Queens, but at every "r == n" leaf we
increment a counter rather than emitting a board.

Found:
  queens = [1, 3, 0, 2]    → count = 1
  queens = [2, 0, 3, 1]    → count = 2

Total: 2
```

### 6. Key Observations

1. **Count vs enumerate** -- Counting is strictly easier than enumerating because we avoid string allocation. No new algorithmic idea is needed.
2. **Symmetry** -- For each solution, its mirror image (column reflection) is also a solution. This can halve the search by trying only `cols < n/2` for row 0 and doubling appropriately, but for `n <= 9` it is unnecessary.
3. **Precomputation is feasible** -- The constraint says `1 <= n <= 9`, so the answers can be hard-coded if absolute speed matters.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking | Count placements over a constraint tree | This problem, [N-Queens](../0051-n-queens/solution.md) |
| Bitmask Backtracking | Same idea with faster constant factor | N-Queens (Approach 3) |
| Memoization / Cache | Constant-size answer set | Hard-coded lookup table |

**Chosen pattern:** `Backtracking with Bitmasks` for production use, simple recursion with sets for clarity.

---

## Approach 1: Backtracking with Sets (Count Only)

### Thought process

> Use the same backtracking template as [Problem 51](../0051-n-queens/solution.md), but instead of pushing a board onto a list at each leaf, increment a counter.

### Algorithm (step-by-step)

1. Start at row `0` with empty conflict sets.
2. At each row `r`, iterate over columns `c = 0..n-1`. Skip any column where the cell `(r, c)` is attacked by an earlier queen.
3. Otherwise, add `c` to `cols`, `r-c` to `diag1`, `r+c` to `diag2`, recurse on `r+1`, then remove the additions.
4. When `r == n`, increment the count.

### Pseudocode

```text
count = 0
function backtrack(r, cols, d1, d2):
    if r == n:
        count += 1
        return
    for c in 0..n-1:
        if c in cols or (r-c) in d1 or (r+c) in d2: continue
        add c, r-c, r+c
        backtrack(r+1)
        remove c, r-c, r+c
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n!) practical | Same pruning as N-Queens; no board materialization |
| **Space** | O(n) | Recursion depth + three sets |

### Implementation

#### Go

```go
func totalNQueens(n int) int {
    count := 0
    cols := make(map[int]bool)
    d1 := make(map[int]bool)
    d2 := make(map[int]bool)
    var backtrack func(r int)
    backtrack = func(r int) {
        if r == n {
            count++
            return
        }
        for c := 0; c < n; c++ {
            if cols[c] || d1[r-c] || d2[r+c] {
                continue
            }
            cols[c], d1[r-c], d2[r+c] = true, true, true
            backtrack(r + 1)
            delete(cols, c); delete(d1, r-c); delete(d2, r+c)
        }
    }
    backtrack(0)
    return count
}
```

#### Java

```java
class Solution {
    int count;
    public int totalNQueens(int n) {
        count = 0;
        Set<Integer> cols = new HashSet<>();
        Set<Integer> d1 = new HashSet<>();
        Set<Integer> d2 = new HashSet<>();
        backtrack(0, n, cols, d1, d2);
        return count;
    }
    private void backtrack(int r, int n,
                           Set<Integer> cols, Set<Integer> d1, Set<Integer> d2) {
        if (r == n) { count++; return; }
        for (int c = 0; c < n; c++) {
            if (cols.contains(c) || d1.contains(r - c) || d2.contains(r + c)) continue;
            cols.add(c); d1.add(r - c); d2.add(r + c);
            backtrack(r + 1, n, cols, d1, d2);
            cols.remove(c); d1.remove(r - c); d2.remove(r + c);
        }
    }
}
```

#### Python

```python
class Solution:
    def totalNQueens(self, n: int) -> int:
        cols, d1, d2 = set(), set(), set()
        count = 0

        def backtrack(r: int) -> None:
            nonlocal count
            if r == n:
                count += 1
                return
            for c in range(n):
                if c in cols or (r - c) in d1 or (r + c) in d2:
                    continue
                cols.add(c); d1.add(r - c); d2.add(r + c)
                backtrack(r + 1)
                cols.remove(c); d1.remove(r - c); d2.remove(r + c)

        backtrack(0)
        return count
```

### Dry Run

```text
Input: n = 4

backtrack(0):
  c=0 → backtrack(1)
    c=0 blocked, c=1 blocked, c=2 → backtrack(2)
      ... dead end
    c=3 → backtrack(2)
      c=1 → backtrack(3)
        all blocked → dead end
    backtrack to 0
  c=1 → backtrack(1) → ... → eventually count = 1
  c=2 → backtrack(1) → ... → eventually count = 2
  c=3 → backtrack(1) → ... → no solutions

Final count: 2
```

---

## Approach 2: Backtracking with Bitmasks

### The problem with Approach 1

> Hash sets are flexible but slower than direct integer arithmetic. For `n <= 9` everything fits in a single `int`.

### Optimization idea

> Replace the three sets with three bitmask integers `cols`, `d1`, `d2`. The set of forbidden columns at row `r` is `cols | d1 | d2`. After placing in column `c`, propagate the diagonal effects by shifting `d1` left and `d2` right when recursing -- this naturally moves diagonal bits to their next-row positions.

### Algorithm (step-by-step)

1. Compute `full = (1 << n) - 1`.
2. Recurse with `(r, cols, d1, d2)`. If `r == n`, return `1`. Otherwise sum the recursive results over all free columns.
3. For each free column bit, recurse with `cols | bit`, `((d1 | bit) << 1) & full`, `(d2 | bit) >> 1`.

### Pseudocode

```text
function totalNQueens(n):
    full = (1 << n) - 1
    return solve(0, 0, 0, 0)

function solve(cols, d1, d2):
    if cols == full: return 1
    free = full & ~(cols | d1 | d2)
    total = 0
    while free != 0:
        bit = free & -free
        total += solve(cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1)
        free &= free - 1
    return total
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n!) practical | Same tree size, smaller per-node cost |
| **Space** | O(n) | Recursion depth |

### Implementation

#### Go

```go
func totalNQueensBitmask(n int) int {
    full := (1 << n) - 1
    var solve func(cols, d1, d2 int) int
    solve = func(cols, d1, d2 int) int {
        if cols == full {
            return 1
        }
        free := full & ^(cols | d1 | d2)
        total := 0
        for free != 0 {
            bit := free & -free
            total += solve(cols|bit, ((d1|bit)<<1)&full, (d2|bit)>>1)
            free &= free - 1
        }
        return total
    }
    return solve(0, 0, 0, 0)
}
```

#### Java

```java
class Solution {
    public int totalNQueensBitmask(int n) {
        int full = (1 << n) - 1;
        return solve(0, 0, 0, full);
    }
    private int solve(int cols, int d1, int d2, int full) {
        if (cols == full) return 1;
        int free = full & ~(cols | d1 | d2);
        int total = 0;
        while (free != 0) {
            int bit = free & -free;
            total += solve(cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1, full);
            free &= free - 1;
        }
        return total;
    }
}
```

#### Python

```python
class Solution:
    def totalNQueensBitmask(self, n: int) -> int:
        full = (1 << n) - 1

        def solve(cols: int, d1: int, d2: int) -> int:
            if cols == full:
                return 1
            free = full & ~(cols | d1 | d2)
            total = 0
            while free:
                bit = free & -free
                total += solve(cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1)
                free &= free - 1
            return total

        return solve(0, 0, 0)
```

### Dry Run

```text
Input: n = 4 → full = 0b1111

solve(0, 0, 0):
  free = 0b1111
  bit=0001 → solve(0001, 0010, 0000)
    free = 0b1100
    bit=0100 → solve(0101, ((0010|0100)<<1)&1111=1100, 0010)
      free = 0
      → 0
    bit=1000 → solve(1001, 0100, 0100)
      free = 0010
      bit=0010 → solve(1011, ((0100|0010)<<1)=1100, 0011)
        free = 0
        → 0
      → 0
    → 0
  bit=0010 → solve(...)
    eventually returns 1
  bit=0100 → 1
  bit=1000 → 0

Total: 0 + 1 + 1 + 0 = 2
```

---

## Approach 3: Lookup Table (Precomputed)

### Idea

> Since `1 <= n <= 9`, there are only nine possible inputs. The answers are well-known constants from the OEIS sequence [A000170](https://oeis.org/A000170). We can hard-code them and answer in O(1).

> This is not a typical interview answer, but it shows that knowing constraints can dramatically simplify a problem. Use this only when the input range is small and fixed.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(1) | Single array lookup |
| **Space** | O(1) | Constant table |

### Implementation

#### Go

```go
func totalNQueensLookup(n int) int {
    table := []int{0, 1, 0, 0, 2, 10, 4, 40, 92, 352}
    return table[n]
}
```

#### Java

```java
class Solution {
    public int totalNQueensLookup(int n) {
        int[] table = {0, 1, 0, 0, 2, 10, 4, 40, 92, 352};
        return table[n];
    }
}
```

#### Python

```python
class Solution:
    def totalNQueensLookup(self, n: int) -> int:
        table = [0, 1, 0, 0, 2, 10, 4, 40, 92, 352]
        return table[n]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking + Sets | O(n!) | O(n) | Easy to read | Hash overhead |
| 2 | Backtracking + Bitmasks | O(n!) | O(n) | Fastest general approach | Less readable |
| 3 | Lookup Table | O(1) | O(1) | Instant, smallest code | Only valid for tiny constraint |

### Which solution to choose?

- **In an interview:** Approach 2 (bitmask) -- shows you understand the constraint and bitwise tricks
- **In production:** Approach 3 if the spec really fixes `n <= 9`; Approach 2 otherwise
- **On Leetcode:** All three accepted; Approach 3 is the fastest answer
- **For learning:** Approach 1 first, then 2 to internalize the bitmask trick

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Smallest `n` | `n = 1` | `1` | Single queen on a 1x1 board |
| 2 | No solution | `n = 2` | `0` | Two queens on a 2x2 always attack |
| 3 | No solution | `n = 3` | `0` | Provably no valid placement |
| 4 | Smallest non-trivial | `n = 4` | `2` | Two distinct solutions |
| 5 | Standard chessboard | `n = 8` | `92` | Famous historical answer |
| 6 | Largest allowed | `n = 9` | `352` | Upper bound of constraint |

---

## Common Mistakes

### Mistake 1: Counting boards instead of partial states

```python
# WRONG — increments at intermediate placements, double counts
def backtrack(r):
    count[0] += 1            # off — adds at every node, not only leaves
    if r == n: return
    ...

# CORRECT — count only at the leaf
def backtrack(r):
    if r == n:
        count[0] += 1
        return
    ...
```

**Reason:** A solution corresponds to a complete placement (`r == n`). Counting at internal nodes inflates the answer.

### Mistake 2: Returning a list and then taking its length

```python
# WORKS BUT WASTEFUL — builds and discards every board
boards = self.solveNQueens(n)
return len(boards)

# CORRECT — count without building boards
return self.totalNQueens(n)
```

**Reason:** Building strings is the slow part of the original problem. Counting avoids this entirely.

### Mistake 3: Incorrect bitmask diagonal propagation

```python
# WRONG — forgets to shift diagonals
solve(cols | bit, d1 | bit, d2 | bit)

# CORRECT — diagonals migrate by row
solve(cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1)
```

**Reason:** The diagonal that touches `(r, c)` reaches column `c-1` on the next row for `\` and column `c+1` for `/`. Shifting the masks performs this migration in bulk.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [51. N-Queens](https://leetcode.com/problems/n-queens/) | :red_circle: Hard | Same problem -- enumerate boards |
| 2 | [37. Sudoku Solver](https://leetcode.com/problems/sudoku-solver/) | :red_circle: Hard | Backtracking on board with constraints |
| 3 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Backtracking enumeration |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Same step-by-step backtracking visualization as N-Queens
> - Live counter that increments only at full placements
> - Toggle between Sets / Bitmask views
> - Selectable board size (n = 4..9)
