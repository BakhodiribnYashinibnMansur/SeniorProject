# 0036. Valid Sudoku

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Hash Sets](#approach-1-hash-sets)
4. [Approach 2: Array-based Validation](#approach-2-array-based-validation)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [36. Valid Sudoku](https://leetcode.com/problems/valid-sudoku/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Hash Table`, `Matrix` |

### Description

> Determine if a `9 x 9` Sudoku board is valid. Only the filled cells need to be validated according to the following rules:
>
> 1. Each **row** must contain the digits `1-9` without repetition.
> 2. Each **column** must contain the digits `1-9` without repetition.
> 3. Each of the nine `3 x 3` sub-boxes of the grid must contain the digits `1-9` without repetition.
>
> **Note:**
> - A Sudoku board (partially filled) could be valid but is not necessarily solvable.
> - Only the filled cells need to be validated according to the mentioned rules.

### Examples

```
Example 1:
Input: board =
[["5","3",".",".","7",".",".",".","."]
,["6",".",".","1","9","5",".",".","."]
,[".","9","8",".",".",".",".","6","."]
,["8",".",".",".","6",".",".",".","3"]
,["4",".",".","8",".","3",".",".","1"]
,["7",".",".",".","2",".",".",".","6"]
,[".","6",".",".",".",".","2","8","."]
,[".",".",".","4","1","9",".",".","5"]
,[".",".",".",".","8",".",".","7","9"]]
Output: true

Example 2:
Input: board =
[["8","3",".",".","7",".",".",".","."]
,["6",".",".","1","9","5",".",".","."]
,[".","9","8",".",".",".",".","6","."]
,["8",".",".",".","6",".",".",".","3"]
,["4",".",".","8",".","3",".",".","1"]
,["7",".",".",".","2",".",".",".","6"]
,[".","6",".",".",".",".","2","8","."]
,[".",".",".","4","1","9",".",".","5"]
,[".",".",".",".","8",".",".","7","9"]]
Output: false
Explanation: Same as Example 1, except with the 5 in the top left corner
being modified to 8. Since there are two 8's in the top left 3x3 sub-box,
it is invalid.
```

### Constraints

- `board.length == 9`
- `board[i].length == 9`
- `board[i][j]` is a digit `1-9` or `'.'`

---

## Problem Breakdown

### 1. What is being asked?

Check whether a **partially filled** 9x9 Sudoku board is valid — meaning no digit `1-9` is repeated in any row, column, or 3x3 sub-box. Empty cells (`'.'`) are ignored.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `board` | `char[][]` | 9x9 grid of characters ('1'-'9' or '.') |

Important observations about the input:
- The board is always exactly 9x9
- Cells contain either a digit `'1'`-`'9'` or `'.'` (empty)
- We do NOT need to check if the board is solvable
- We only validate **filled** cells

### 3. What is the output?

- `true` if the board is valid (no violations)
- `false` if any row, column, or 3x3 sub-box contains a duplicate digit

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| Board is always 9x9 | Fixed size — any algorithm is O(1) in theory, but we analyze as O(n^2) for n=9 |
| Cells are `'1'-'9'` or `'.'` | No need to validate input characters |
| Partially filled | Many cells can be empty — skip them |

### 5. Step-by-step example analysis

#### Example 2 (Invalid board):

```text
Board:
  8 3 . | . 7 . | . . .
  6 . . | 1 9 5 | . . .
  . 9 8 | . . . | . 6 .
  ------+-------+------
  8 . . | . 6 . | . . 3
  4 . . | 8 . 3 | . . 1
  7 . . | . 2 . | . . 6
  ------+-------+------
  . 6 . | . . . | 2 8 .
  . . . | 4 1 9 | . . 5
  . . . | . 8 . | . 7 9

Check row 0: {5,3,7} → no duplicates ✅
Check column 0: {8,6,8,...} → 8 appears twice ❌ INVALID!
Check box (0,0): {8,3,6,9,8} → 8 appears twice ❌ INVALID!

Result: false
```

### 6. Key Observations

1. **Three separate constraints** — rows, columns, and 3x3 boxes must each independently have no duplicate digits.
2. **Box index formula** — For cell `(r, c)`, the box index is `(r // 3) * 3 + (c // 3)`, giving boxes numbered 0-8.
3. **Single pass possible** — We can check all three constraints simultaneously by iterating through every cell once.
4. **Fixed size** — The board is always 9x9, so the problem is inherently constant-time, but we reason about it as O(n^2) for clarity.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Hash Set tracking | Track seen digits per row/col/box | This problem |
| Array-based counting | Use fixed-size arrays instead of hash sets | Optimized version |
| Brute Force (3 passes) | Check rows, columns, boxes separately | Simpler but more code |

**Chosen pattern:** `Hash Set / Array tracking`
**Reason:** A single pass through all 81 cells, checking three sets (row, column, box) for each filled cell.

---

## Approach 1: Hash Sets

### Thought process

> For each filled cell, we need to verify it hasn't appeared before in the same row, same column, or same 3x3 box.
> We can maintain sets for each row (9 sets), each column (9 sets), and each box (9 sets) — 27 sets total.
> As we scan each cell, if the digit already exists in any relevant set, the board is invalid.

### Algorithm (step-by-step)

1. Create 9 sets for rows, 9 sets for columns, 9 sets for boxes
2. Iterate through every cell `(r, c)`:
   - If cell is `'.'` → skip
   - Calculate box index: `box = (r // 3) * 3 + (c // 3)`
   - If digit is already in `rows[r]` or `cols[c]` or `boxes[box]` → return `false`
   - Add digit to `rows[r]`, `cols[c]`, and `boxes[box]`
3. If no conflicts found → return `true`

### Pseudocode

```text
function isValidSudoku(board):
    rows = array of 9 empty sets
    cols = array of 9 empty sets
    boxes = array of 9 empty sets

    for r = 0 to 8:
        for c = 0 to 8:
            val = board[r][c]
            if val == '.': continue

            box = (r / 3) * 3 + (c / 3)

            if val in rows[r] or val in cols[c] or val in boxes[box]:
                return false

            rows[r].add(val)
            cols[c].add(val)
            boxes[box].add(val)

    return true
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(1) / O(n^2) | We visit each of the 81 cells once. O(1) because board is always 9x9 |
| **Space** | O(1) / O(n^2) | 27 sets, each holding at most 9 elements. O(1) because bounded |

### Implementation

#### Go

```go
func isValidSudoku(board [][]byte) bool {
    var rows, cols, boxes [9]map[byte]bool

    for i := 0; i < 9; i++ {
        rows[i] = make(map[byte]bool)
        cols[i] = make(map[byte]bool)
        boxes[i] = make(map[byte]bool)
    }

    for r := 0; r < 9; r++ {
        for c := 0; c < 9; c++ {
            val := board[r][c]
            if val == '.' {
                continue
            }

            box := (r/3)*3 + c/3

            if rows[r][val] || cols[c][val] || boxes[box][val] {
                return false
            }

            rows[r][val] = true
            cols[c][val] = true
            boxes[box][val] = true
        }
    }

    return true
}
```

#### Java

```java
public boolean isValidSudoku(char[][] board) {
    Set<Character>[] rows = new HashSet[9];
    Set<Character>[] cols = new HashSet[9];
    Set<Character>[] boxes = new HashSet[9];

    for (int i = 0; i < 9; i++) {
        rows[i] = new HashSet<>();
        cols[i] = new HashSet<>();
        boxes[i] = new HashSet<>();
    }

    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            char val = board[r][c];
            if (val == '.') continue;

            int box = (r / 3) * 3 + c / 3;

            if (!rows[r].add(val) || !cols[c].add(val) || !boxes[box].add(val)) {
                return false;
            }
        }
    }

    return true;
}
```

#### Python

```python
def isValidSudoku(self, board: list[list[str]]) -> bool:
    rows = [set() for _ in range(9)]
    cols = [set() for _ in range(9)]
    boxes = [set() for _ in range(9)]

    for r in range(9):
        for c in range(9):
            val = board[r][c]
            if val == '.':
                continue

            box = (r // 3) * 3 + c // 3

            if val in rows[r] or val in cols[c] or val in boxes[box]:
                return False

            rows[r].add(val)
            cols[c].add(val)
            boxes[box].add(val)

    return True
```

### Dry Run

```text
Input: Example 1 (valid board)
board[0] = ["5","3",".",".","7",".",".",".","."]

Processing row 0:
  (0,0) val='5': box=0, rows[0]={}, cols[0]={}, boxes[0]={} → add '5'
  (0,1) val='3': box=0, rows[0]={5}, cols[1]={}, boxes[0]={5} → add '3'
  (0,2) val='.': skip
  (0,3) val='.': skip
  (0,4) val='7': box=1, rows[0]={5,3}, cols[4]={}, boxes[1]={} → add '7'
  (0,5-8) val='.': skip

Processing row 1:
  (1,0) val='6': box=0, rows[1]={}, cols[0]={5}, boxes[0]={5,3} → add '6'
  (1,1) val='.': skip
  (1,2) val='.': skip
  (1,3) val='1': box=1, rows[1]={6}, cols[3]={}, boxes[1]={7} → add '1'
  (1,4) val='9': box=1, rows[1]={6,1}, cols[4]={7}, boxes[1]={7,1} → add '9'
  (1,5) val='5': box=1, rows[1]={6,1,9}, cols[5]={}, boxes[1]={7,1,9} → add '5'
  ...

All 81 cells processed, no conflicts found.
Result: true
```

```text
Input: Example 2 (invalid board — top-left '5' changed to '8')
board[0] = ["8","3",".",".","7",".",".",".","."]

Processing row 0:
  (0,0) val='8': box=0, rows[0]={}, cols[0]={}, boxes[0]={} → add '8'
  ...

Processing row 3:
  (3,0) val='8': box=3, rows[3]={}, cols[0]={8,6}, boxes[3]={}
    cols[0] already has '8'? → No, cols[0]={8,6}... wait
    Actually: cols[0] = {'8','6'} after rows 0-2
    (3,0) val='8': '8' in cols[0]? YES → return false ❌

Result: false
```

---

## Approach 2: Array-based Validation

### The difference from Hash Sets

> Instead of hash sets, use 2D boolean arrays (or bitmasks) to track seen digits.
> For digit `d` at position `(r, c)`:
> - `rows[r][d] = true`
> - `cols[c][d] = true`
> - `boxes[box][d] = true`
>
> Array lookups are faster than hash set operations in practice.

### Optimization idea

> Replace `Set<Character>` with `boolean[9][9]` arrays.
> The digit `'1'-'9'` maps to index `0-8` via `d - '1'`.
> This avoids hashing overhead and is more cache-friendly.

### Algorithm (step-by-step)

1. Create three 9x9 boolean arrays: `rows[9][9]`, `cols[9][9]`, `boxes[9][9]`
2. Iterate through every cell `(r, c)`:
   - If cell is `'.'` → skip
   - `d = board[r][c] - '1'` (digit index 0-8)
   - `box = (r / 3) * 3 + (c / 3)`
   - If `rows[r][d]` or `cols[c][d]` or `boxes[box][d]` is `true` → return `false`
   - Set all three to `true`
3. Return `true`

### Pseudocode

```text
function isValidSudoku(board):
    rows = boolean[9][9] (all false)
    cols = boolean[9][9] (all false)
    boxes = boolean[9][9] (all false)

    for r = 0 to 8:
        for c = 0 to 8:
            if board[r][c] == '.': continue
            d = board[r][c] - '1'
            box = (r / 3) * 3 + (c / 3)

            if rows[r][d] or cols[c][d] or boxes[box][d]:
                return false

            rows[r][d] = true
            cols[c][d] = true
            boxes[box][d] = true

    return true
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(1) / O(n^2) | Single pass through 81 cells, each with O(1) array access |
| **Space** | O(1) / O(n^2) | Three 9x9 boolean arrays = 243 booleans |

### Implementation

#### Go

```go
func isValidSudoku(board [][]byte) bool {
    var rows, cols, boxes [9][9]bool

    for r := 0; r < 9; r++ {
        for c := 0; c < 9; c++ {
            if board[r][c] == '.' {
                continue
            }

            d := board[r][c] - '1'
            box := (r/3)*3 + c/3

            if rows[r][d] || cols[c][d] || boxes[box][d] {
                return false
            }

            rows[r][d] = true
            cols[c][d] = true
            boxes[box][d] = true
        }
    }

    return true
}
```

#### Java

```java
public boolean isValidSudoku(char[][] board) {
    boolean[][] rows = new boolean[9][9];
    boolean[][] cols = new boolean[9][9];
    boolean[][] boxes = new boolean[9][9];

    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            if (board[r][c] == '.') continue;

            int d = board[r][c] - '1';
            int box = (r / 3) * 3 + c / 3;

            if (rows[r][d] || cols[c][d] || boxes[box][d]) {
                return false;
            }

            rows[r][d] = true;
            cols[c][d] = true;
            boxes[box][d] = true;
        }
    }

    return true;
}
```

#### Python

```python
def isValidSudoku(self, board: list[list[str]]) -> bool:
    rows = [[False] * 9 for _ in range(9)]
    cols = [[False] * 9 for _ in range(9)]
    boxes = [[False] * 9 for _ in range(9)]

    for r in range(9):
        for c in range(9):
            if board[r][c] == '.':
                continue

            d = int(board[r][c]) - 1
            box = (r // 3) * 3 + c // 3

            if rows[r][d] or cols[c][d] or boxes[box][d]:
                return False

            rows[r][d] = True
            cols[c][d] = True
            boxes[box][d] = True

    return True
```

### Dry Run

```text
Input: Example 1 (valid board)

Initialize: rows[9][9], cols[9][9], boxes[9][9] — all false

(0,0) val='5': d=4, box=0
  rows[0][4]=F, cols[0][4]=F, boxes[0][4]=F → set all true

(0,1) val='3': d=2, box=0
  rows[0][2]=F, cols[1][2]=F, boxes[0][2]=F → set all true

(0,4) val='7': d=6, box=1
  rows[0][6]=F, cols[4][6]=F, boxes[1][6]=F → set all true

(1,0) val='6': d=5, box=0
  rows[1][5]=F, cols[0][5]=F, boxes[0][5]=F → set all true

(1,3) val='1': d=0, box=1
  rows[1][0]=F, cols[3][0]=F, boxes[1][0]=F → set all true

(1,4) val='9': d=8, box=1
  rows[1][8]=F, cols[4][8]=F, boxes[1][8]=F → set all true

(1,5) val='5': d=4, box=1
  rows[1][4]=F, cols[5][4]=F, boxes[1][4]=F → set all true

... (continue for all cells)

No conflicts found → return true
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Hash Sets | O(1) | O(1) | Intuitive, easy to implement | Hash overhead |
| 2 | Array-based Validation | O(1) | O(1) | Fastest, cache-friendly | Slightly less readable |

### Which solution to choose?

- **In an interview:** Approach 1 (Hash Sets) — cleaner and easier to explain
- **In production:** Approach 2 (Array-based) — marginally faster, lower memory overhead
- **On Leetcode:** Either works — both pass all test cases with the same complexity
- **For learning:** Approach 1 first — understand the logic, then optimize with Approach 2

---

## Edge Cases

| # | Case | Expected | Reason |
|---|---|---|---|
| 1 | Valid board (Example 1) | `true` | All rules satisfied |
| 2 | Duplicate in box (Example 2) | `false` | Two 8's in top-left 3x3 box |
| 3 | Duplicate in row | `false` | Same digit appears twice in a row |
| 4 | Duplicate in column | `false` | Same digit appears twice in a column |
| 5 | Almost empty board | `true` | Very few filled cells, no conflicts |
| 6 | Fully valid board | `true` | Completed valid Sudoku |
| 7 | Single cell per row/col/box | `true` | Sparse board, no possible conflicts |
| 8 | All dots except one cell | `true` | One digit cannot conflict with anything |

---

## Common Mistakes

### Mistake 1: Wrong box index formula

```python
# ❌ WRONG — incorrect box calculation
box = r // 3 + c // 3  # gives 0-4, not 0-8!

# ✅ CORRECT — row_block * 3 + col_block
box = (r // 3) * 3 + c // 3  # gives 0-8
```

**Reason:** There are 3 box rows and 3 box columns. `r // 3` gives the box row (0-2), multiplied by 3 to offset, plus `c // 3` for the box column.

### Mistake 2: Not skipping empty cells

```python
# ❌ WRONG — '.' gets added to sets, causing false conflicts
for r in range(9):
    for c in range(9):
        val = board[r][c]
        if val in rows[r]:  # '.' might be "seen" multiple times!
            return False

# ✅ CORRECT — skip empty cells
for r in range(9):
    for c in range(9):
        val = board[r][c]
        if val == '.':
            continue
        # ...
```

### Mistake 3: Checking if board is solvable

```python
# ❌ WRONG — the problem does NOT ask to solve the Sudoku
# Don't try backtracking or constraint propagation

# ✅ CORRECT — only validate filled cells
# A valid board can still be unsolvable
```

### Mistake 4: Using character vs. integer confusion

```python
# ❌ WRONG — mixing char and int
d = board[r][c] - 1  # board[r][c] is a STRING '5', not integer 5

# ✅ CORRECT — convert properly
d = int(board[r][c]) - 1  # Python: str → int
d = board[r][c] - '1'     # Go/Java: byte arithmetic
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [37. Sudoku Solver](https://leetcode.com/problems/sudoku-solver/) | :red_circle: Hard | Actually solve the Sudoku (backtracking) |
| 2 | [48. Rotate Image](https://leetcode.com/problems/rotate-image/) | :yellow_circle: Medium | Matrix manipulation |
| 3 | [73. Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes/) | :yellow_circle: Medium | Matrix traversal with state tracking |
| 4 | [289. Game of Life](https://leetcode.com/problems/game-of-life/) | :yellow_circle: Medium | Grid validation with neighbor checks |
| 5 | [2133. Check if Every Row and Column Contains All Numbers](https://leetcode.com/problems/check-if-every-row-and-column-contains-all-numbers/) | :green_circle: Easy | Similar row/column validation without boxes |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **9x9 Sudoku grid** displayed with cell-by-cell validation
> - Highlights current cell being checked in blue
> - Shows **conflicts** in red when a duplicate is found
> - Validated cells turn green
> - **Step / Play / Pause / Reset** controls
> - **Speed slider** for animation speed
> - **Preset boards** to try different scenarios
> - **Log panel** showing each validation step
