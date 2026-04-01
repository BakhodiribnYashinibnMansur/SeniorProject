# 0037. Sudoku Solver

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking](#approach-1-backtracking)
4. [Complexity Comparison](#complexity-comparison)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Related Problems](#related-problems)
8. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [37. Sudoku Solver](https://leetcode.com/problems/sudoku-solver/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Array`, `Hash Table`, `Backtracking`, `Matrix` |

### Description

> Write a program to solve a Sudoku puzzle by filling the empty cells.
>
> A sudoku solution must satisfy **all of the following rules**:
> 1. Each of the digits `1-9` must occur exactly once in each row.
> 2. Each of the digits `1-9` must occur exactly once in each column.
> 3. Each of the digits `1-9` must occur exactly once in each of the 9 `3x3` sub-boxes of the grid.
>
> The `'.'` character indicates empty cells.

### Examples

```
Example 1:

Input: board =
[["5","3",".",".","7",".",".",".","."],
 ["6",".",".","1","9","5",".",".","."],
 [".","9","8",".",".",".",".","6","."],
 ["8",".",".",".","6",".",".",".","3"],
 ["4",".",".","8",".","3",".",".","1"],
 ["7",".",".",".","2",".",".",".","6"],
 [".","6",".",".",".",".","2","8","."],
 [".",".",".","4","1","9",".",".","5"],
 [".",".",".",".","8",".",".","7","9"]]

Output:
[["5","3","4","6","7","8","9","1","2"],
 ["6","7","2","1","9","5","3","4","8"],
 ["1","9","8","3","4","2","5","6","7"],
 ["8","5","9","7","6","1","4","2","3"],
 ["4","2","6","8","5","3","7","9","1"],
 ["7","1","3","9","2","4","8","5","6"],
 ["9","6","1","5","3","7","2","8","4"],
 ["2","8","7","4","1","9","6","3","5"],
 ["3","4","5","2","8","6","1","7","9"]]

Explanation: The input board is shown above and the only valid solution is shown below.
```

### Constraints

- `board.length == 9`
- `board[i].length == 9`
- `board[i][j]` is a digit or `'.'`
- It is **guaranteed** that the input board has only one solution

---

## Problem Breakdown

### 1. What is being asked?

Fill in all empty cells (`'.'`) of a 9x9 Sudoku grid such that every row, column, and 3x3 sub-box contains exactly the digits 1-9. The board is modified **in-place** (no return value).

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `board` | `char[][]` | 9x9 grid with digits `'1'`-`'9'` and `'.'` for empty |

Important observations about the input:
- The grid is always exactly 9x9
- Pre-filled digits are always valid (no conflicts)
- There is always **exactly one** solution
- We must modify the board in-place

### 3. What is the output?

- **No return value** — the board is modified in-place
- All `'.'` cells must be replaced with digits `'1'`-`'9'`
- The result must satisfy all three Sudoku rules

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| Board is 9x9 | Fixed size — no scalability concerns with the grid itself |
| Exactly one solution | We can stop as soon as we find one valid filling |
| Pre-filled digits are valid | No need to validate the initial board |

### 5. Step-by-step example analysis

```text
Initial board (. = empty):
5 3 . | . 7 . | . . .
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

We need to fill 51 empty cells.
For each empty cell, we try digits 1-9:
  - Check if the digit is valid (not in row, column, or 3x3 box)
  - If valid, place it and move to the next empty cell
  - If no digit works, backtrack to the previous cell and try the next digit
```

### 6. Key Observations

1. **Constraint Satisfaction Problem (CSP)** — Each cell has constraints from its row, column, and 3x3 box.
2. **Backtracking** — Try a digit, if it leads to a dead end, undo and try the next digit.
3. **Fixed grid size** — 9x9 is small enough for backtracking to work efficiently.
4. **Pre-computation** — Tracking which digits are used in each row/column/box with sets enables O(1) validity checks.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking | Explore all possibilities, undo invalid choices | N-Queens, Sudoku |
| Constraint Propagation | Reduce search space by eliminating impossible digits | Advanced Sudoku solvers |
| Brute Force | Try every combination | Too slow (9^81 worst case) |

**Chosen pattern:** `Backtracking with Hash Set tracking`
**Reason:** Backtracking systematically explores the search space, and using sets to track used digits gives O(1) validity checks.

---

## Approach 1: Backtracking

### Thought process

> We treat the Sudoku grid as a constraint satisfaction problem.
> For each empty cell, we try placing digits 1-9.
> Before placing a digit, we check if it violates any Sudoku rule.
> If the digit is valid, we place it and recurse to the next empty cell.
> If we reach a dead end (no valid digit for the current cell), we backtrack — undo the last placement and try the next digit.
>
> **Optimization:** Use three arrays of sets (rows, cols, boxes) to track which digits are already used. This gives O(1) checks instead of scanning the entire row/column/box each time.
>
> The box index for cell (r, c) is: `(r / 3) * 3 + (c / 3)`.

### Algorithm (step-by-step)

1. **Initialize tracking sets:** For each of the 9 rows, 9 columns, and 9 boxes, create a set of digits already placed.
2. **Collect empty cells:** Scan the board and record all positions where `board[r][c] == '.'`.
3. **Backtrack function:** `solve(index)`
   - **Base case:** If `index == len(empty_cells)`, all cells are filled — return `true`.
   - Get the position `(r, c)` of the current empty cell.
   - For each digit `d` from `'1'` to `'9'`:
     - If `d` is NOT in `rows[r]`, `cols[c]`, or `boxes[box_id]`:
       - Place `d`: add to sets, set `board[r][c] = d`
       - Recurse: `solve(index + 1)`
       - If recursion returns `true`, return `true` (solution found)
       - Otherwise, undo: remove from sets, set `board[r][c] = '.'`
   - If no digit works, return `false` (trigger backtracking)
4. Call `solve(0)` to start solving.

### Pseudocode

```text
function solveSudoku(board):
    rows = [set() for 9]
    cols = [set() for 9]
    boxes = [set() for 9]
    empty = []

    // Initialize: scan existing digits
    for r = 0 to 8:
        for c = 0 to 8:
            if board[r][c] != '.':
                d = board[r][c]
                rows[r].add(d)
                cols[c].add(d)
                boxes[(r/3)*3 + c/3].add(d)
            else:
                empty.append((r, c))

    function solve(idx):
        if idx == len(empty):
            return true  // All filled!

        r, c = empty[idx]
        box_id = (r/3)*3 + c/3

        for d = '1' to '9':
            if d not in rows[r] and d not in cols[c] and d not in boxes[box_id]:
                // Place digit
                board[r][c] = d
                rows[r].add(d)
                cols[c].add(d)
                boxes[box_id].add(d)

                if solve(idx + 1):
                    return true  // Solution found!

                // Backtrack
                board[r][c] = '.'
                rows[r].remove(d)
                cols[c].remove(d)
                boxes[box_id].remove(d)

        return false  // No valid digit, backtrack

    solve(0)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(9^m) | Where m is the number of empty cells. In the worst case, we try 9 digits for each empty cell. With constraint checking, most branches are pruned early. In practice, much faster than the theoretical bound. |
| **Space** | O(m) | Recursion depth equals the number of empty cells (at most 81). The tracking sets use O(81) = O(1) additional space. |

### Implementation

#### Go

```go
func solveSudoku(board [][]byte) {
    // Tracking sets: rows[r], cols[c], boxes[boxId]
    var rows, cols, boxes [9][9]bool
    var empty [][2]int

    // Initialize: scan existing digits
    for r := 0; r < 9; r++ {
        for c := 0; c < 9; c++ {
            if board[r][c] != '.' {
                d := board[r][c] - '1'
                rows[r][d] = true
                cols[c][d] = true
                boxes[(r/3)*3+c/3][d] = true
            } else {
                empty = append(empty, [2]int{r, c})
            }
        }
    }

    var solve func(idx int) bool
    solve = func(idx int) bool {
        if idx == len(empty) {
            return true // All cells filled
        }

        r, c := empty[idx][0], empty[idx][1]
        boxId := (r/3)*3 + c/3

        for d := byte(0); d < 9; d++ {
            if !rows[r][d] && !cols[c][d] && !boxes[boxId][d] {
                // Place digit
                board[r][c] = d + '1'
                rows[r][d] = true
                cols[c][d] = true
                boxes[boxId][d] = true

                if solve(idx + 1) {
                    return true
                }

                // Backtrack
                board[r][c] = '.'
                rows[r][d] = false
                cols[c][d] = false
                boxes[boxId][d] = false
            }
        }

        return false
    }

    solve(0)
}
```

#### Java

```java
class Solution {
    public void solveSudoku(char[][] board) {
        // Tracking sets: rows[r][d], cols[c][d], boxes[boxId][d]
        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];
        List<int[]> empty = new ArrayList<>();

        // Initialize: scan existing digits
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') {
                    int d = board[r][c] - '1';
                    rows[r][d] = true;
                    cols[c][d] = true;
                    boxes[(r / 3) * 3 + c / 3][d] = true;
                } else {
                    empty.add(new int[]{r, c});
                }
            }
        }

        solve(board, empty, rows, cols, boxes, 0);
    }

    private boolean solve(char[][] board, List<int[]> empty,
                          boolean[][] rows, boolean[][] cols,
                          boolean[][] boxes, int idx) {
        if (idx == empty.size()) {
            return true; // All cells filled
        }

        int r = empty.get(idx)[0];
        int c = empty.get(idx)[1];
        int boxId = (r / 3) * 3 + c / 3;

        for (int d = 0; d < 9; d++) {
            if (!rows[r][d] && !cols[c][d] && !boxes[boxId][d]) {
                // Place digit
                board[r][c] = (char) (d + '1');
                rows[r][d] = true;
                cols[c][d] = true;
                boxes[boxId][d] = true;

                if (solve(board, empty, rows, cols, boxes, idx + 1)) {
                    return true;
                }

                // Backtrack
                board[r][c] = '.';
                rows[r][d] = false;
                cols[c][d] = false;
                boxes[boxId][d] = false;
            }
        }

        return false;
    }
}
```

#### Python

```python
class Solution:
    def solveSudoku(self, board: list[list[str]]) -> None:
        # Tracking sets: which digits are used in each row/col/box
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxes = [set() for _ in range(9)]
        empty = []

        # Initialize: scan existing digits
        for r in range(9):
            for c in range(9):
                if board[r][c] != '.':
                    d = board[r][c]
                    rows[r].add(d)
                    cols[c].add(d)
                    boxes[(r // 3) * 3 + c // 3].add(d)
                else:
                    empty.append((r, c))

        def solve(idx: int) -> bool:
            if idx == len(empty):
                return True  # All cells filled

            r, c = empty[idx]
            box_id = (r // 3) * 3 + c // 3

            for d in '123456789':
                if d not in rows[r] and d not in cols[c] and d not in boxes[box_id]:
                    # Place digit
                    board[r][c] = d
                    rows[r].add(d)
                    cols[c].add(d)
                    boxes[box_id].add(d)

                    if solve(idx + 1):
                        return True

                    # Backtrack
                    board[r][c] = '.'
                    rows[r].remove(d)
                    cols[c].remove(d)
                    boxes[box_id].remove(d)

            return False

        solve(0)
```

### Dry Run

```text
Input (partial board, focusing on the first few empty cells):

5 3 . | . 7 . | . . .
6 . . | 1 9 5 | . . .
. 9 8 | . . . | . 6 .
...

Empty cells (first 5): (0,2), (0,3), (0,4)=7 already filled, (0,5), (0,6), ...

Solving cell (0,2) — row 0, col 2, box 0:
  Row 0 has: {5, 3, 7}
  Col 2 has: {8}
  Box 0 has: {5, 3, 6, 9, 8}

  Try '1': not in row ✅, not in col ✅, not in box ✅ → but leads to dead end later
  Try '2': not in row ✅, not in col ✅, not in box ✅ → but leads to dead end later
  Try '4': not in row ✅, not in col ✅, not in box ✅ → proceed!

  Place '4' at (0,2). Move to (0,3).

Solving cell (0,3) — row 0, col 3, box 1:
  Row 0 has: {5, 3, 4, 7}
  Col 3 has: {1, 8}
  Box 1 has: {1, 9, 5}

  Try '2': not in row ✅, not in col ✅, not in box ✅ → but dead end later
  Try '6': not in row ✅, not in col ✅, not in box ✅ → proceed!

  Place '6' at (0,3). Move to next empty cell.
  ...

Eventually all cells are filled:
5 3 4 | 6 7 8 | 9 1 2
6 7 2 | 1 9 5 | 3 4 8
1 9 8 | 3 4 2 | 5 6 7
------+-------+------
8 5 9 | 7 6 1 | 4 2 3
4 2 6 | 8 5 3 | 7 9 1
7 1 3 | 9 2 4 | 8 5 6
------+-------+------
9 6 1 | 5 3 7 | 2 8 4
2 8 7 | 4 1 9 | 6 3 5
3 4 5 | 2 8 6 | 1 7 9

solve() returns true — done!
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking + Sets | O(9^m) | O(m) | Fast with pruning, clean code | Worst case exponential |
| 2 | Backtracking + Bitmask | O(9^m) | O(m) | Slightly faster constant factor | More complex code |
| 3 | Constraint Propagation + Backtracking | O(9^m) | O(m) | Fewest nodes explored | Complex implementation |

### Which solution to choose?

- **In an interview:** Approach 1 (Backtracking + Sets) — clear, elegant, easy to explain
- **In production:** Approach 1 — fast enough for 9x9 grids, readable code
- **On Leetcode:** Approach 1 — passes all test cases comfortably
- **For learning:** Approach 1 — teaches backtracking, the foundation for constraint satisfaction

---

## Edge Cases

| # | Case | Description | Reason |
|---|---|---|---|
| 1 | Almost full board | Only 1-2 empty cells | Minimal backtracking needed |
| 2 | Many empty cells | 50+ empty cells | Stress test for backtracking efficiency |
| 3 | First cell has one option | Only one valid digit for (0,0) | No branching at the first cell |
| 4 | Deep backtracking | Requires undoing many placements | Tests correctness of undo logic |

---

## Common Mistakes

### Mistake 1: Forgetting to undo the placement on backtrack

```python
# WRONG — digit is placed but never removed on failure
board[r][c] = d
rows[r].add(d)
if solve(idx + 1):
    return True
# Missing: board[r][c] = '.', rows[r].remove(d), etc.

# CORRECT — always undo on backtrack
board[r][c] = d
rows[r].add(d)
cols[c].add(d)
boxes[box_id].add(d)

if solve(idx + 1):
    return True

board[r][c] = '.'
rows[r].remove(d)
cols[c].remove(d)
boxes[box_id].remove(d)
```

### Mistake 2: Wrong box index calculation

```python
# WRONG — this gives wrong box grouping
box_id = r / 3 + c / 3

# CORRECT — row group * 3 + col group
box_id = (r // 3) * 3 + c // 3
```

**Reason:** `(r // 3)` gives the box row (0, 1, or 2), multiplied by 3 gives the base, plus `(c // 3)` gives the box column offset.

### Mistake 3: Not stopping after finding a solution

```python
# WRONG — continues searching after solution is found
for d in '123456789':
    if is_valid(d):
        board[r][c] = d
        solve(idx + 1)  # ignores return value!

# CORRECT — stop when solution is found
for d in '123456789':
    if is_valid(d):
        board[r][c] = d
        if solve(idx + 1):
            return True  # propagate success up
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [36. Valid Sudoku](https://leetcode.com/problems/valid-sudoku/) | :yellow_circle: Medium | Validate a Sudoku board (check only, no solving) |
| 2 | [51. N-Queens](https://leetcode.com/problems/n-queens/) | :red_circle: Hard | Backtracking on a grid with constraints |
| 3 | [52. N-Queens II](https://leetcode.com/problems/n-queens-ii/) | :red_circle: Hard | Count solutions instead of listing them |
| 4 | [79. Word Search](https://leetcode.com/problems/word-search/) | :yellow_circle: Medium | Backtracking on a grid |
| 5 | [212. Word Search II](https://leetcode.com/problems/word-search-ii/) | :red_circle: Hard | Backtracking + Trie on a grid |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **9x9 Sudoku grid** with color-coded cells
> - **Step-by-step** visualization of the backtracking algorithm
> - **Cell coloring:** trying (yellow), placed (green), backtracked (red)
> - **Controls:** Step, Play, Pause, Reset, Speed slider
> - **Presets:** Choose from different Sudoku puzzles
> - **Log:** Real-time log of algorithm decisions
