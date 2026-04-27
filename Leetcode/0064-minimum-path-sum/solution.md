# 0064. Minimum Path Sum

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Top-Down DP (Memoization)](#approach-1-top-down-dp-memoization)
4. [Approach 2: Bottom-Up DP (2D)](#approach-2-bottom-up-dp-2d)
5. [Approach 3: Bottom-Up DP (1D, Space Optimized)](#approach-3-bottom-up-dp-1d-space-optimized)
6. [Approach 4: In-Place DP](#approach-4-in-place-dp)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [64. Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Dynamic Programming`, `Matrix` |

### Description

> Given a `m x n` grid filled with non-negative numbers, find a path from top left to bottom right, which minimizes the sum of all numbers along its path.
>
> **Note:** You can only move either down or right at any point in time.

### Examples

```
Example 1:
Input: grid = [[1,3,1],[1,5,1],[4,2,1]]
Output: 7
Explanation: Path 1 → 3 → 1 → 1 → 1 minimizes the sum.

Example 2:
Input: grid = [[1,2,3],[4,5,6]]
Output: 12
Explanation: 1 → 2 → 3 → 6 sums to 12.
```

### Constraints

- `m == grid.length`
- `n == grid[i].length`
- `1 <= m, n <= 200`
- `0 <= grid[i][j] <= 200`

---

## Problem Breakdown

### 1. What is being asked?

Find a path from `(0, 0)` to `(m-1, n-1)` moving only right or down, with minimum total sum of cell values.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `grid` | `int[m][n]` | Non-negative integer grid |

### 3. What is the output?

A single integer: minimum sum among all valid paths.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 200` | Up to 40,000 cells. O(mn) is fast |
| Values up to 200 | Max sum bounded by 200 * 400 = 80,000, fits in `int` |
| Non-negative values | Greedy fails (no shortcut), but DP works perfectly |

### 5. Step-by-step example analysis

#### Example 1: `[[1,3,1],[1,5,1],[4,2,1]]`

```text
grid:        DP table:
1 3 1         1 4 5
1 5 1   →     2 7 6
4 2 1         6 8 7

dp[r][c] = grid[r][c] + min(dp[r-1][c], dp[r][c-1]).
First row: cumulative sum from left.
First col: cumulative sum from top.

Answer: dp[2][2] = 7.
Path: 1 → 3 → 1 → 1 → 1.
```

### 6. Key Observations

1. **Recurrence** -- `dp[r][c] = grid[r][c] + min(dp[r-1][c], dp[r][c-1])`.
2. **Border init** -- First row sums left-to-right; first column sums top-to-bottom.
3. **1D rolling array** -- Only need previous row. `dp[c] = grid[r][c] + min(dp[c], dp[c-1])`.
4. **In-place** -- We can mutate `grid` itself.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| DP on grid | Same family as Problems 62, 63 |
| Min instead of count | Same recurrence shape, swap `+` for `min` |

**Chosen pattern:** `Bottom-Up DP (1D)`.

---

## Approach 1: Top-Down DP (Memoization)

### Idea

> `f(r, c)` = min sum from `(0,0)` to `(r,c)`. Recurse on the predecessors with memoization.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Python

```python
class Solution:
    def minPathSumMemo(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        from functools import lru_cache
        @lru_cache(maxsize=None)
        def f(r: int, c: int) -> int:
            if r == 0 and c == 0: return grid[0][0]
            if r < 0 or c < 0: return float('inf')
            return grid[r][c] + min(f(r - 1, c), f(r, c - 1))
        return f(m - 1, n - 1)
```

---

## Approach 2: Bottom-Up DP (2D)

### Algorithm

1. `dp[0][0] = grid[0][0]`.
2. Fill first row: `dp[0][c] = dp[0][c-1] + grid[0][c]`.
3. Fill first column: `dp[r][0] = dp[r-1][0] + grid[r][0]`.
4. For `r = 1..m-1`, `c = 1..n-1`: `dp[r][c] = grid[r][c] + min(dp[r-1][c], dp[r][c-1])`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Go

```go
func minPathSum2D(grid [][]int) int {
    m := len(grid)
    n := len(grid[0])
    dp := make([][]int, m)
    for i := range dp { dp[i] = make([]int, n) }
    dp[0][0] = grid[0][0]
    for c := 1; c < n; c++ { dp[0][c] = dp[0][c-1] + grid[0][c] }
    for r := 1; r < m; r++ { dp[r][0] = dp[r-1][0] + grid[r][0] }
    for r := 1; r < m; r++ {
        for c := 1; c < n; c++ {
            up, left := dp[r-1][c], dp[r][c-1]
            if up < left {
                dp[r][c] = grid[r][c] + up
            } else {
                dp[r][c] = grid[r][c] + left
            }
        }
    }
    return dp[m-1][n-1]
}
```

---

## Approach 3: Bottom-Up DP (1D, Space Optimized)

### Algorithm

1. `dp = [0] * n`. `dp[0] = grid[0][0]`. Fill first row prefix.
2. For each `r = 1..m-1`:
   - `dp[0] += grid[r][0]`.
   - For `c = 1..n-1`: `dp[c] = grid[r][c] + min(dp[c], dp[c-1])`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(n) |

### Implementation

#### Go

```go
func minPathSum(grid [][]int) int {
    m := len(grid)
    n := len(grid[0])
    dp := make([]int, n)
    dp[0] = grid[0][0]
    for c := 1; c < n; c++ { dp[c] = dp[c-1] + grid[0][c] }
    for r := 1; r < m; r++ {
        dp[0] += grid[r][0]
        for c := 1; c < n; c++ {
            if dp[c] < dp[c-1] {
                dp[c] = grid[r][c] + dp[c]
            } else {
                dp[c] = grid[r][c] + dp[c-1]
            }
        }
    }
    return dp[n-1]
}
```

#### Java

```java
class Solution {
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] dp = new int[n];
        dp[0] = grid[0][0];
        for (int c = 1; c < n; c++) dp[c] = dp[c - 1] + grid[0][c];
        for (int r = 1; r < m; r++) {
            dp[0] += grid[r][0];
            for (int c = 1; c < n; c++) {
                dp[c] = grid[r][c] + Math.min(dp[c], dp[c - 1]);
            }
        }
        return dp[n - 1];
    }
}
```

#### Python

```python
class Solution:
    def minPathSum(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dp = [0] * n
        dp[0] = grid[0][0]
        for c in range(1, n):
            dp[c] = dp[c - 1] + grid[0][c]
        for r in range(1, m):
            dp[0] += grid[r][0]
            for c in range(1, n):
                dp[c] = grid[r][c] + min(dp[c], dp[c - 1])
        return dp[n - 1]
```

### Dry Run

```text
grid = [[1,3,1],[1,5,1],[4,2,1]]

Init: dp = [1, 4, 5]   # cumulative on row 0

Row 1: dp[0] = 1 + 1 = 2
  c=1: dp[1] = 5 + min(4, 2) = 5 + 2 = 7
  c=2: dp[2] = 1 + min(5, 7) = 1 + 5 = 6
  dp = [2, 7, 6]

Row 2: dp[0] = 2 + 4 = 6
  c=1: dp[1] = 2 + min(7, 6) = 2 + 6 = 8
  c=2: dp[2] = 1 + min(6, 8) = 1 + 6 = 7
  dp = [6, 8, 7]

Return dp[2] = 7.
```

---

## Approach 4: In-Place DP

### Idea

> Mutate `grid[r][c]` to hold the min sum to reach it. Same recurrence with no extra allocation.

### Implementation

#### Python

```python
class Solution:
    def minPathSumInPlace(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        for c in range(1, n): grid[0][c] += grid[0][c - 1]
        for r in range(1, m): grid[r][0] += grid[r - 1][0]
        for r in range(1, m):
            for c in range(1, n):
                grid[r][c] += min(grid[r - 1][c], grid[r][c - 1])
        return grid[m - 1][n - 1]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Memoization | O(mn) | O(mn) | Easy DP | Recursion overhead |
| 2 | 2D Bottom-Up | O(mn) | O(mn) | Iterative | 2D table |
| 3 | 1D Bottom-Up | O(mn) | O(n) | Optimal DP | -- |
| 4 | In-Place | O(mn) | O(1) | No extra grid | Mutates input |

### Which solution to choose?

- **In an interview:** Approach 3
- **In production:** Approach 3 (or 4 if mutation acceptable)
- **On Leetcode:** Approach 3

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Single cell | `[[5]]` | `5` | Just the value |
| 2 | Single row | `[[1,2,3]]` | `6` | Sum across |
| 3 | Single column | `[[1],[2],[3]]` | `6` | Sum down |
| 4 | All zeros | `[[0,0],[0,0]]` | `0` | Zero path |
| 5 | Standard | `[[1,3,1],[1,5,1],[4,2,1]]` | `7` | Example 1 |
| 6 | All same | `[[5,5,5],[5,5,5],[5,5,5]]` | `25` | 5 cells × 5 |
| 7 | Asymmetric | `[[1,2,3],[4,5,6]]` | `12` | Example 2 |
| 8 | Strong gradient | `[[1,100,100],[1,1,1],[100,1,1]]` | `5` | Pick low corridor |

---

## Common Mistakes

### Mistake 1: Greedy along the path

```python
# WRONG — at each cell pick the smaller of right/down value
# This is myopic and can miss the global minimum

# CORRECT — DP from end backward, or from start forward, considering all paths
```

**Reason:** Greedy ignores future costs. A small local saving may lead to a large later cost.

### Mistake 2: Forgetting first-column update in 1D form

```python
# WRONG — forgets dp[0] += grid[r][0]
for r in range(1, m):
    for c in range(1, n):
        dp[c] = grid[r][c] + min(dp[c], dp[c - 1])

# CORRECT
for r in range(1, m):
    dp[0] += grid[r][0]
    for c in range(1, n):
        dp[c] = grid[r][c] + min(dp[c], dp[c - 1])
```

**Reason:** Without updating `dp[0]`, the column-0 contribution stays at row 0, giving wrong totals.

### Mistake 3: Using `+=` instead of `=`

```python
# WRONG — dp[c] += min(...) double-counts the previous row's contribution
dp[c] += grid[r][c] + min(dp[c], dp[c - 1])

# CORRECT
dp[c] = grid[r][c] + min(dp[c], dp[c - 1])
```

**Reason:** The recurrence already includes the previous row's value via `dp[c]` on the right side; assignment must replace the old value.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [62. Unique Paths](https://leetcode.com/problems/unique-paths/) | :yellow_circle: Medium | Count paths instead of min sum |
| 2 | [63. Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | :yellow_circle: Medium | Same with obstacles |
| 3 | [120. Triangle](https://leetcode.com/problems/triangle/) | :yellow_circle: Medium | Min path on triangle |
| 4 | [931. Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) | :yellow_circle: Medium | Min path with diagonal moves |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Grid coloring by current DP value
> - Highlight current cell + its predecessors (above / left)
> - Trace the optimal path from end back to start
> - Toggle 2D vs 1D view
