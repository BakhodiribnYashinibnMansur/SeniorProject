# 0079. Word Search

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: DFS with Backtracking](#approach-1-dfs-with-backtracking)
4. [Approach 2: DFS with In-Place Marker (No Visited Set)](#approach-2-dfs-with-in-place-marker-no-visited-set)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [79. Word Search](https://leetcode.com/problems/word-search/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Backtracking`, `Matrix` |

### Description

> Given an `m x n` grid of characters `board` and a string `word`, return `true` *if `word` exists in the grid*.
>
> The word can be constructed from letters of sequentially adjacent cells, where adjacent cells are horizontally or vertically neighboring. The same letter cell may not be used more than once.

### Examples

```
Example 1:
Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
Output: true

Example 2:
Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "SEE"
Output: true

Example 3:
Input: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCB"
Output: false
```

### Constraints

- `m == board.length`
- `n = board[i].length`
- `1 <= m, n <= 6`
- `1 <= word.length <= 15`
- `board` and `word` consists of only lowercase and uppercase English letters.

---

## Problem Breakdown

### 1. What is being asked?

Search for `word` in a 2D grid by walking from cell to cell (4-directional) without revisiting a cell within the same path.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `board` | `char[m][n]` | Grid |
| `word` | `str` | Target |

### 3. What is the output?

A boolean.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 6` | At most 36 starting cells |
| `word <= 15` | DFS depth bounded by 15 |

### 5. Step-by-step example analysis

#### `word = "ABCCED"`

```text
Find all 'A's. For each, DFS in 4 directions matching the next char.

Starting at (0,0)='A':
  word[0] matches.
  Go right (0,1)='B' matches word[1].
  Go right (0,2)='C' matches word[2].
  Go down (1,2)='C' matches word[3].
  Go down (2,2)='E' matches word[4].
  Go left (2,1)='D' matches word[5]. → success.
```

### 6. Key Observations

1. **DFS with marking** -- Mark cells as visited (or temporarily replace with sentinel) to avoid reusing within a single path.
2. **Restore on backtrack** -- When the recursion returns, restore the cell.
3. **Pruning** -- If `board[r][c] != word[i]`, return immediately.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Backtracking on grid | Walk + undo |

**Chosen pattern:** `DFS with Backtracking`.

---

## Approach 1: DFS with Backtracking

### Algorithm (step-by-step)

1. For each cell `(r, c)`:
   - If DFS from `(r, c)` matching `word[0]` succeeds, return true.
2. DFS:
   - If out of bounds or `board[r][c] != word[i]` or visited: return false.
   - If `i == len(word) - 1`: return true.
   - Mark visited.
   - Recurse to four neighbors with `i + 1`. If any returns true, return true.
   - Unmark.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n * 4^L) | L = len(word). Each path branches up to 4 ways |
| **Space** | O(L) recursion (plus visited grid if used) |

### Implementation

#### Go

```go
func exist(board [][]byte, word string) bool {
    m := len(board)
    n := len(board[0])
    var dfs func(r, c, i int) bool
    dfs = func(r, c, i int) bool {
        if i == len(word) {
            return true
        }
        if r < 0 || r >= m || c < 0 || c >= n || board[r][c] != word[i] {
            return false
        }
        save := board[r][c]
        board[r][c] = '#' // mark
        ok := dfs(r+1, c, i+1) || dfs(r-1, c, i+1) ||
            dfs(r, c+1, i+1) || dfs(r, c-1, i+1)
        board[r][c] = save
        return ok
    }
    for r := 0; r < m; r++ {
        for c := 0; c < n; c++ {
            if dfs(r, c, 0) {
                return true
            }
        }
    }
    return false
}
```

#### Java

```java
class Solution {
    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                if (dfs(board, word, r, c, 0)) return true;
        return false;
    }
    private boolean dfs(char[][] board, String word, int r, int c, int i) {
        if (i == word.length()) return true;
        int m = board.length, n = board[0].length;
        if (r < 0 || r >= m || c < 0 || c >= n || board[r][c] != word.charAt(i)) return false;
        char save = board[r][c];
        board[r][c] = '#';
        boolean ok = dfs(board, word, r + 1, c, i + 1)
                  || dfs(board, word, r - 1, c, i + 1)
                  || dfs(board, word, r, c + 1, i + 1)
                  || dfs(board, word, r, c - 1, i + 1);
        board[r][c] = save;
        return ok;
    }
}
```

#### Python

```python
class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        m, n = len(board), len(board[0])
        def dfs(r: int, c: int, i: int) -> bool:
            if i == len(word):
                return True
            if r < 0 or r >= m or c < 0 or c >= n or board[r][c] != word[i]:
                return False
            save = board[r][c]
            board[r][c] = '#'
            ok = (dfs(r + 1, c, i + 1) or dfs(r - 1, c, i + 1) or
                  dfs(r, c + 1, i + 1) or dfs(r, c - 1, i + 1))
            board[r][c] = save
            return ok
        for r in range(m):
            for c in range(n):
                if dfs(r, c, 0):
                    return True
        return False
```

---

## Approach 2: DFS with In-Place Marker (No Visited Set)

> Same as Approach 1, with the marker technique already used. Listed for clarity.

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | DFS + Backtracking | O(m*n * 4^L) | O(L) | Standard | Mutates board temporarily |

### Which solution to choose?

Approach 1 always.

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | Word longer than total cells | False |
| 2 | Word equals single cell | Trivial match |
| 3 | Single row/col board | Linear search |
| 4 | Repeating letters | Marker prevents reuse |
| 5 | No matching first letter | Return false quickly |

---

## Common Mistakes

### Mistake 1: Forgetting to unmark

```python
# WRONG — leaves board polluted; further searches fail
board[r][c] = '#'
return dfs(...)

# CORRECT — restore on the way out
save = board[r][c]; board[r][c] = '#'
ok = dfs(...)
board[r][c] = save
return ok
```

### Mistake 2: Using a separate `visited` set without resetting

```python
# WRONG — visited persists across DFS attempts from different starting cells
visited = set()
for r, c: dfs(r, c, 0, visited)

# CORRECT — fresh visited per attempt OR reset after each call
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [212. Word Search II](https://leetcode.com/problems/word-search-ii/) | :red_circle: Hard | Multiple words via Trie |
| 2 | [200. Number of Islands](https://leetcode.com/problems/number-of-islands/) | :yellow_circle: Medium | DFS on grid |
| 3 | [130. Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) | :yellow_circle: Medium | DFS on grid |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Board with the current cell highlighted
> - Path traced through cells matching the word so far
> - Backtrack animations on dead-ends
