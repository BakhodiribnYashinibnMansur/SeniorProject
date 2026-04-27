# 0051. N-Queens

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force (Generate All Permutations)](#approach-1-brute-force-generate-all-permutations)
4. [Approach 2: Backtracking with Sets](#approach-2-backtracking-with-sets)
5. [Approach 3: Backtracking with Bitmasks](#approach-3-backtracking-with-bitmasks)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [51. N-Queens](https://leetcode.com/problems/n-queens/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Array`, `Backtracking` |

### Description

> The **n-queens puzzle** is the problem of placing `n` queens on an `n × n` chessboard such that no two queens attack each other.
>
> Given an integer `n`, return all distinct solutions to the n-queens puzzle. You may return the answer in any order.
>
> Each solution contains a distinct board configuration of the n-queens' placement, where `'Q'` and `'.'` both indicate a queen and an empty space, respectively.

### Examples

```
Example 1:
Input: n = 4
Output: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
Explanation: There exist two distinct solutions to the 4-queens puzzle as shown.

Example 2:
Input: n = 1
Output: [["Q"]]
```

### Constraints

- `1 <= n <= 9`

---

## Problem Breakdown

### 1. What is being asked?

Place `n` queens on an `n × n` chessboard so that no two queens can attack each other. A queen attacks any piece on the same **row**, same **column**, or same **diagonal**. We must return every valid arrangement as a list of board strings.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | Size of the board and number of queens (`1 <= n <= 9`) |

Important observations about the input:
- `n` is small (max 9), so even exponential algorithms are tractable
- Exactly `n` queens, exactly one per row in any valid solution
- The board is square

### 3. What is the output?

- A list of all valid configurations
- Each configuration is a list of `n` strings of length `n`
- Each string uses `'Q'` for a queen and `'.'` for an empty cell
- Order of solutions does not matter

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 9` | Solutions count grows fast: n=8 → 92, n=9 → 352. Backtracking is fast enough |
| One queen per row | Reduces search space from `n^(n*n)` to `n^n` |
| Diagonal conflict | Two queens `(r1,c1)` and `(r2,c2)` conflict iff `r1-c1 == r2-c2` or `r1+c2 == r2+c1` |

### 5. Step-by-step example analysis

#### Example 1: `n = 4`

```text
Row 0: try col 0
  Row 1: col 0,1 blocked by row 0
         try col 2
    Row 2: col 0,1 blocked, col 2 blocked, col 3 blocked → DEAD END
  Row 1: try col 3
    Row 2: col 0 blocked (col), col 1 blocked (col 3, diag: 1+3=4 vs 2+1=3 OK; row 1 col 3 → row 2 col 1: |1-2|==|3-1|? 1!=2 OK)
           Wait, let's re-check: try col 1
           Row 0 col 0, Row 1 col 3, Row 2 col 1
           Row 1 col 3 vs Row 2 col 1: |1-2|=1, |3-1|=2 → not diagonal, OK
           Row 0 col 0 vs Row 2 col 1: |0-2|=2, |0-1|=1 → not diagonal, OK
      Row 3: col 0 blocked (row 0), col 1 blocked (row 2), col 2 blocked (diag from row 1 col 3? |1-3|=2, |3-2|=1, no), col 3 blocked (row 1) → DEAD END

  Row 1 col 3, Row 2 try col 1 (worked) → Row 3 dead end → backtrack
  No more cols for row 2 → backtrack to row 1 → no more cols → backtrack to row 0

Row 0: try col 1
  Row 1: col 0 blocked (diag), col 1 blocked (col), col 2 blocked (diag), try col 3
    Row 2: col 0 → check OK
      Row 3: col 0 blocked (row 2), col 1 blocked (row 0), col 2 → check |0-3|=3, |1-2|=1, |1-3|=2,|3-2|=1, |2-3|=1,|0-2|=2 → OK!
        SOLUTION FOUND: [".Q..","...Q","Q...","..Q."]
  ...

Total solutions for n=4: 2
```

### 6. Key Observations

1. **One queen per row** -- We process row by row, so two queens in the same row is impossible. This eliminates one of three conflict types automatically.
2. **Diagonal identity** -- All cells on the `\` diagonal share `row - col`; all cells on the `/` diagonal share `row + col`. We can track conflicts in O(1) using sets keyed on these values.
3. **Backtracking is natural** -- We make a tentative placement, recurse, and undo on failure. The search tree has depth `n` and branching factor up to `n`.
4. **Symmetry could halve work** but is not needed -- For the leftmost queen, you can try only `cols < n/2` and reflect, but the constraint `n <= 9` makes this unnecessary.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking | Place, recurse, unplace -- classic constraint search | N-Queens, Sudoku Solver |
| State Tracking with Sets | Need O(1) conflict checking | N-Queens, Word Search |
| Bitmask DP | Replace sets with bitmask integers for speed | N-Queens (this problem), Travelling Salesman |

**Chosen pattern:** `Backtracking with Sets / Bitmasks`
**Reason:** We must enumerate all solutions, so a search tree with pruning is the right structure. Sets give O(1) conflict checks; bitmasks give the same with less constant overhead.

---

## Approach 1: Brute Force (Generate All Permutations)

### Thought process

> Since at most one queen sits in each row, we can think of a placement as a permutation of columns: position `i` of the permutation gives the column of the queen in row `i`. Generate every permutation and check whether it has any diagonal conflicts.

### Algorithm (step-by-step)

1. Generate all permutations of `[0, 1, ..., n-1]`.
2. For each permutation `p`, check whether any two queens `(i, p[i])` and `(j, p[j])` lie on the same diagonal: `|i - j| == |p[i] - p[j]|`.
3. If no diagonal conflicts, convert the permutation into the board string format and add to the result.

### Pseudocode

```text
function solveNQueens(n):
    result = []
    for p in permutations([0..n-1]):
        if no diagonal conflict in p:
            result.append(buildBoard(p, n))
    return result

function noDiagonalConflict(p):
    for i in 0..n-1:
        for j in i+1..n-1:
            if abs(i - j) == abs(p[i] - p[j]):
                return false
    return true
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n! * n^2) | n! permutations, each checked in O(n^2) |
| **Space** | O(n) | Permutation buffer (output excluded) |

### Implementation

#### Go

```go
func solveNQueensBrute(n int) [][]string {
    cols := make([]int, n)
    for i := range cols {
        cols[i] = i
    }
    var result [][]string
    permute(cols, 0, &result, n)
    return result
}

func permute(arr []int, start int, result *[][]string, n int) {
    if start == n {
        if validDiagonals(arr) {
            *result = append(*result, buildBoard(arr, n))
        }
        return
    }
    for i := start; i < n; i++ {
        arr[start], arr[i] = arr[i], arr[start]
        permute(arr, start+1, result, n)
        arr[start], arr[i] = arr[i], arr[start]
    }
}

func validDiagonals(p []int) bool {
    for i := 0; i < len(p); i++ {
        for j := i + 1; j < len(p); j++ {
            di, dj := i-j, p[i]-p[j]
            if di < 0 { di = -di }
            if dj < 0 { dj = -dj }
            if di == dj {
                return false
            }
        }
    }
    return true
}
```

#### Java

```java
class Solution {
    public List<List<String>> solveNQueensBrute(int n) {
        int[] cols = new int[n];
        for (int i = 0; i < n; i++) cols[i] = i;
        List<List<String>> result = new ArrayList<>();
        permute(cols, 0, result, n);
        return result;
    }

    private void permute(int[] arr, int start, List<List<String>> result, int n) {
        if (start == n) {
            if (validDiagonals(arr)) result.add(buildBoard(arr, n));
            return;
        }
        for (int i = start; i < n; i++) {
            int t = arr[start]; arr[start] = arr[i]; arr[i] = t;
            permute(arr, start + 1, result, n);
            t = arr[start]; arr[start] = arr[i]; arr[i] = t;
        }
    }
}
```

#### Python

```python
from itertools import permutations

class Solution:
    def solveNQueensBrute(self, n: int) -> List[List[str]]:
        result = []
        for p in permutations(range(n)):
            if all(abs(i - j) != abs(p[i] - p[j])
                   for i in range(n) for j in range(i + 1, n)):
                result.append(['.' * c + 'Q' + '.' * (n - c - 1) for c in p])
        return result
```

### Dry Run

```text
Input: n = 4

Permutation [0,1,2,3]:
  i=0,j=1: |0-1|=1, |0-1|=1 → CONFLICT, reject

Permutation [0,2,1,3]:
  i=0,j=1: |0-1|=1, |0-2|=2 → ok
  i=0,j=2: |0-2|=2, |0-1|=1 → ok
  i=0,j=3: |0-3|=3, |0-3|=3 → CONFLICT, reject

Permutation [1,3,0,2]:
  All pairs checked, no diagonal conflict → SOLUTION!

Permutation [2,0,3,1]:
  All pairs checked, no diagonal conflict → SOLUTION!

Total permutations checked: 24 (= 4!)
Solutions found: 2
```

---

## Approach 2: Backtracking with Sets

### The problem with Approach 1

> We generate `n!` permutations even though most are quickly rejectable. For `n = 9`, that is 362,880 permutations, each scanned in O(n^2). We can prune the search tree aggressively as soon as a partial placement becomes invalid.

### Optimization idea

> Place queens row by row. Maintain three sets: occupied columns, occupied "down" diagonals (`row - col`), and occupied "up" diagonals (`row + col`). Before placing a queen at `(r, c)`, check all three sets in O(1). If any conflict, skip this column entirely -- the entire subtree is pruned.

### Algorithm (step-by-step)

1. Initialize three empty sets: `cols`, `diag1` (row - col), `diag2` (row + col).
2. Recurse on row `r`:
   - If `r == n`: record the current placement as a solution.
   - For each column `c` in `0..n-1`:
     - If `c in cols` or `r-c in diag1` or `r+c in diag2`: skip.
     - Otherwise, add to all three sets, recurse on `r+1`, then remove from all three sets (backtrack).

### Pseudocode

```text
function solveNQueens(n):
    cols, diag1, diag2 = empty sets
    queens = array of size n
    result = []
    backtrack(0, n, cols, diag1, diag2, queens, result)
    return result

function backtrack(r, n, cols, diag1, diag2, queens, result):
    if r == n:
        result.append(buildBoard(queens, n))
        return
    for c in 0..n-1:
        if c in cols or (r - c) in diag1 or (r + c) in diag2:
            continue
        queens[r] = c
        cols.add(c); diag1.add(r - c); diag2.add(r + c)
        backtrack(r + 1, n, cols, diag1, diag2, queens, result)
        cols.remove(c); diag1.remove(r - c); diag2.remove(r + c)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n!) worst case | Pruning makes the practical tree much smaller |
| **Space** | O(n) | Recursion depth n + three sets each up to size n |

### Implementation

#### Go

```go
func solveNQueens(n int) [][]string {
    var result [][]string
    queens := make([]int, n)
    cols := make(map[int]bool)
    diag1 := make(map[int]bool) // r - c
    diag2 := make(map[int]bool) // r + c

    var backtrack func(r int)
    backtrack = func(r int) {
        if r == n {
            board := make([]string, n)
            for i, c := range queens {
                row := make([]byte, n)
                for j := range row { row[j] = '.' }
                row[c] = 'Q'
                board[i] = string(row)
            }
            result = append(result, board)
            return
        }
        for c := 0; c < n; c++ {
            if cols[c] || diag1[r-c] || diag2[r+c] {
                continue
            }
            queens[r] = c
            cols[c], diag1[r-c], diag2[r+c] = true, true, true
            backtrack(r + 1)
            cols[c], diag1[r-c], diag2[r+c] = false, false, false
        }
    }
    backtrack(0)
    return result
}
```

#### Java

```java
class Solution {
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queens = new int[n];
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>();
        Set<Integer> diag2 = new HashSet<>();
        backtrack(0, n, queens, cols, diag1, diag2, result);
        return result;
    }

    private void backtrack(int r, int n, int[] queens,
                           Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2,
                           List<List<String>> result) {
        if (r == n) {
            List<String> board = new ArrayList<>();
            for (int c : queens) {
                char[] row = new char[n];
                Arrays.fill(row, '.');
                row[c] = 'Q';
                board.add(new String(row));
            }
            result.add(board);
            return;
        }
        for (int c = 0; c < n; c++) {
            if (cols.contains(c) || diag1.contains(r - c) || diag2.contains(r + c)) continue;
            queens[r] = c;
            cols.add(c); diag1.add(r - c); diag2.add(r + c);
            backtrack(r + 1, n, queens, cols, diag1, diag2, result);
            cols.remove(c); diag1.remove(r - c); diag2.remove(r + c);
        }
    }
}
```

#### Python

```python
class Solution:
    def solveNQueens(self, n: int) -> List[List[str]]:
        result, queens = [], [0] * n
        cols, diag1, diag2 = set(), set(), set()

        def backtrack(r: int) -> None:
            if r == n:
                result.append(['.' * c + 'Q' + '.' * (n - c - 1) for c in queens])
                return
            for c in range(n):
                if c in cols or (r - c) in diag1 or (r + c) in diag2:
                    continue
                queens[r] = c
                cols.add(c); diag1.add(r - c); diag2.add(r + c)
                backtrack(r + 1)
                cols.remove(c); diag1.remove(r - c); diag2.remove(r + c)

        backtrack(0)
        return result
```

### Dry Run

```text
Input: n = 4

backtrack(r=0):
  c=0: place at (0,0); cols={0}, d1={0}, d2={0}
    backtrack(r=1):
      c=0: in cols, skip
      c=1: r-c=0 in d1, skip
      c=2: place at (1,2); cols={0,2}, d1={0,-1}, d2={0,3}
        backtrack(r=2):
          c=0: in cols, skip
          c=1: r+c=3 in d2, skip
          c=2: in cols, skip
          c=3: r+c=5 not in d2, r-c=-1 in d1 → skip
          → no valid c, return
        unplace (1,2)
      c=3: place at (1,3); cols={0,3}, d1={0,-2}, d2={0,4}
        backtrack(r=2):
          c=0: in cols, skip
          c=1: place at (2,1); cols={0,3,1}, d1={0,-2,1}, d2={0,4,3}
            backtrack(r=3):
              c=0: in cols, skip
              c=1: in cols, skip
              c=2: r-c=1 in d1, skip
              c=3: in cols, skip
              → no valid c, return
            unplace (2,1)
          c=2: r-c=0 in d1, skip
          c=3: in cols, skip
          → no more c, return
        unplace (1,3)
      → return, unplace (0,0)

  c=1: place at (0,1); cols={1}, d1={-1}, d2={1}
    backtrack(r=1):
      c=0: r-c=1 not in d1, r+c=1 in d2 → skip
      c=1: in cols, skip
      c=2: r-c=-1 in d1, skip
      c=3: place at (1,3); cols={1,3}, d1={-1,-2}, d2={1,4}
        backtrack(r=2):
          c=0: place at (2,0); cols={1,3,0}, d1={-1,-2,2}, d2={1,4,2}
            backtrack(r=3):
              c=0: in cols, skip
              c=1: in cols, skip
              c=2: place at (3,2); valid!
                backtrack(r=4): r==n, ADD SOLUTION [".Q..","...Q","Q...","..Q."]
              unplace (3,2)
              c=3: in cols, skip
            unplace (2,0)
          c=1: in cols, skip
          c=2: r+c=4 in d2, skip
          c=3: in cols, skip
        unplace (1,3)
      → unplace (0,1)

  c=2: place at (0,2); ... → finds ["..Q.","Q...","...Q",".Q.."]

  c=3: ... → no solutions (mirror of c=0 path)

Total solutions: 2
```

---

## Approach 3: Backtracking with Bitmasks

### The problem with Approach 2

> Hash sets work but have constant overhead from hashing and dynamic memory. Since `n <= 9`, every queen state fits in a small integer. We can replace the three sets with three bitmasks and check conflicts using bitwise AND.

### Optimization idea

> Use three integers `cols`, `diag1`, `diag2` as bitmasks where bit `k` indicates "column `k` is occupied" (and similarly for diagonals). Conflict check at column `c` becomes `(cols | diag1 | diag2) & (1 << c) == 0`. This is faster than set lookups because everything stays in CPU registers.

### Algorithm (step-by-step)

1. Maintain three integer bitmasks: `cols`, `d1` (the `\` diagonals), `d2` (the `/` diagonals).
2. At row `r`, the bitmask of forbidden columns is `cols | d1 | d2`.
3. For each free column `c`:
   - Update masks: `cols |= bit, d1 |= bit, d2 |= bit`, where `bit = 1 << c`.
   - Recurse to row `r+1`. When recursing, **shift** `d1` left by 1 and `d2` right by 1 so that diagonals migrate naturally.
   - Backtrack by removing the bit.

> **Note:** A common trick is to combine all three masks and iterate on the lowest set bit using `bits & -bits`, which gives the fastest possible loop.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n!) worst case | Same as Approach 2; bitmask operations are O(1) but with smaller constant |
| **Space** | O(n) | Recursion depth, three integers |

### Implementation

#### Go

```go
func solveNQueensBitmask(n int) [][]string {
    var result [][]string
    queens := make([]int, n)
    full := (1 << n) - 1

    var backtrack func(r, cols, d1, d2 int)
    backtrack = func(r, cols, d1, d2 int) {
        if r == n {
            board := make([]string, n)
            for i, c := range queens {
                row := make([]byte, n)
                for j := range row { row[j] = '.' }
                row[c] = 'Q'
                board[i] = string(row)
            }
            result = append(result, board)
            return
        }
        free := full & ^(cols | d1 | d2)
        for free != 0 {
            bit := free & -free      // lowest set bit
            c := bitsTrailingZeros(bit)
            queens[r] = c
            backtrack(r+1, cols|bit, (d1|bit)<<1&full, (d2|bit)>>1)
            free &= free - 1         // clear lowest set bit
        }
    }
    backtrack(0, 0, 0, 0)
    return result
}

func bitsTrailingZeros(x int) int {
    n := 0
    for x&1 == 0 {
        x >>= 1
        n++
    }
    return n
}
```

#### Java

```java
class Solution {
    public List<List<String>> solveNQueensBitmask(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queens = new int[n];
        backtrack(0, 0, 0, 0, n, queens, result);
        return result;
    }

    private void backtrack(int r, int cols, int d1, int d2, int n,
                           int[] queens, List<List<String>> result) {
        if (r == n) {
            List<String> board = new ArrayList<>();
            for (int c : queens) {
                char[] row = new char[n];
                Arrays.fill(row, '.');
                row[c] = 'Q';
                board.add(new String(row));
            }
            result.add(board);
            return;
        }
        int full = (1 << n) - 1;
        int free = full & ~(cols | d1 | d2);
        while (free != 0) {
            int bit = free & -free;
            int c = Integer.numberOfTrailingZeros(bit);
            queens[r] = c;
            backtrack(r + 1, cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1,
                      n, queens, result);
            free &= free - 1;
        }
    }
}
```

#### Python

```python
class Solution:
    def solveNQueensBitmask(self, n: int) -> List[List[str]]:
        result, queens = [], [0] * n
        full = (1 << n) - 1

        def backtrack(r: int, cols: int, d1: int, d2: int) -> None:
            if r == n:
                result.append(['.' * c + 'Q' + '.' * (n - c - 1) for c in queens])
                return
            free = full & ~(cols | d1 | d2)
            while free:
                bit = free & -free
                c = bit.bit_length() - 1
                queens[r] = c
                backtrack(r + 1, cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1)
                free &= free - 1

        backtrack(0, 0, 0, 0)
        return result
```

### Dry Run

```text
Input: n = 4 → full = 0b1111 = 15

backtrack(r=0, cols=0, d1=0, d2=0):
  free = 15 & ~0 = 0b1111
  bit=0b0001, c=0, queens[0]=0
  → backtrack(1, cols=0001, d1=(0001)<<1=0010, d2=(0001)>>1=0)

  backtrack(r=1, cols=0001, d1=0010, d2=0):
    blocked = 0001 | 0010 | 0000 = 0011
    free = 15 & ~0011 = 0b1100
    bit=0b0100, c=2, queens[1]=2
    → backtrack(2, cols=0101, d1=(0010|0100)<<1=1100, d2=(0|0100)>>1=0010)

    backtrack(r=2, cols=0101, d1=1100, d2=0010):
      blocked = 0101 | 1100 | 0010 = 1111
      free = 0 → return (dead end)

    bit=0b1000, c=3, queens[1]=3
    → backtrack(2, cols=1001, d1=(0010|1000)<<1=10100&1111=0100, d2=(0|1000)>>1=0100)

    backtrack(r=2, cols=1001, d1=0100, d2=0100):
      blocked = 1001 | 0100 | 0100 = 1101
      free = 0010
      bit=0b0010, c=1, queens[2]=1
      → backtrack(3, cols=1011, d1=(0100|0010)<<1=1100, d2=(0100|0010)>>1=0011)

      backtrack(r=3, cols=1011, d1=1100, d2=0011):
        blocked = 1011 | 1100 | 0011 = 1111
        free = 0 → dead end

  ...continues...

  Eventually finds: queens = [1,3,0,2] and queens = [2,0,3,1]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force Permutations | O(n! * n^2) | O(n) | Simple to write | No early pruning, slowest |
| 2 | Backtracking + Sets | O(n!) practical | O(n) | Easy to read, good pruning | Hash overhead |
| 3 | Backtracking + Bitmasks | O(n!) practical | O(n) | Fastest in practice | Less readable |

### Which solution to choose?

- **In an interview:** Approach 2 (Backtracking with Sets) -- explains intent clearly
- **In production:** Approach 3 if performance matters, Approach 2 otherwise
- **On Leetcode:** All three pass for `n <= 9`. Approach 2 is the sweet spot
- **For learning:** Start with 2, then learn 3 to understand bitmask elegance

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Smallest board | `n = 1` | `[["Q"]]` | Single cell, trivial solution |
| 2 | No solution exists | `n = 2` | `[]` | Two queens always attack on a 2x2 |
| 3 | No solution exists | `n = 3` | `[]` | Provably no valid placement |
| 4 | Classic case | `n = 4` | 2 solutions | Smallest non-trivial answer |
| 5 | Standard chessboard | `n = 8` | 92 solutions | Famous historical instance |
| 6 | Largest allowed | `n = 9` | 352 solutions | Upper bound of constraint |

---

## Common Mistakes

### Mistake 1: Forgetting to undo state on backtrack

```python
# WRONG — never removes from sets, contaminates future paths
def backtrack(r):
    for c in range(n):
        if c in cols: continue
        cols.add(c)
        backtrack(r + 1)
        # missing cols.remove(c)!

# CORRECT — symmetric add/remove
def backtrack(r):
    for c in range(n):
        if c in cols: continue
        cols.add(c)
        backtrack(r + 1)
        cols.remove(c)
```

**Reason:** Backtracking depends on the invariant that the state at the start of a call equals the state at its end. Any add must be paired with a remove.

### Mistake 2: Wrong diagonal formulas

```python
# WRONG — both diagonals use the same key
diag1 = r + c
diag2 = r + c   # should be r - c

# CORRECT
diag1 = r - c   # cells on '\' diagonal share this
diag2 = r + c   # cells on '/' diagonal share this
```

**Reason:** A `\` diagonal has constant `r - c` (each step down-right increases both equally). A `/` diagonal has constant `r + c`. Mixing them lets queens attack each other diagonally.

### Mistake 3: Building the board string incorrectly

```python
# WRONG — uses 'Q' * n + '.' or similar concatenation gymnastics
row = 'Q' if c == col else '.' * n   # produces 'Q' or '....'

# CORRECT
row = '.' * c + 'Q' + '.' * (n - c - 1)
```

**Reason:** The output expects a string of length exactly `n`, with one `Q` at the queen's column and `.` elsewhere.

### Mistake 4: Bitmask off-by-one

```python
# WRONG — full mask should not include bit n
full = 1 << n + 1   # off by one

# CORRECT
full = (1 << n) - 1   # bits 0..n-1 all set
```

**Reason:** For an `n`-column board, columns are `0..n-1`, so the mask should have exactly `n` low bits set, which is `(1 << n) - 1`.

### Mistake 5: Forgetting to mask `d1` after the left shift

```python
# WRONG — d1 grows beyond n bits
backtrack(r + 1, cols | bit, (d1 | bit) << 1, ...)

# CORRECT
backtrack(r + 1, cols | bit, ((d1 | bit) << 1) & full, ...)
```

**Reason:** Without masking, `d1` accumulates high bits that are never cleared and may incorrectly mark columns as blocked in deeper recursion.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [52. N-Queens II](https://leetcode.com/problems/n-queens-ii/) | :red_circle: Hard | Same problem but only count solutions |
| 2 | [37. Sudoku Solver](https://leetcode.com/problems/sudoku-solver/) | :red_circle: Hard | Backtracking with row/col/box constraints |
| 3 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Backtracking enumeration |
| 4 | [79. Word Search](https://leetcode.com/problems/word-search/) | :yellow_circle: Medium | Grid backtracking with state |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Step-by-step backtracking on the chessboard
> - Visualization of column / diagonal conflicts in real time
> - Queen placement, attack rays, and undo (backtrack) animations
> - Counter for placements / backtracks / solutions found
> - Selectable board size (n = 4..9)
