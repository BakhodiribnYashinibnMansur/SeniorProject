# 0063. Unique Paths II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Top-Down DP with Memoization](#approach-1-top-down-dp-with-memoization)
4. [Approach 2: Bottom-Up DP (2D)](#approach-2-bottom-up-dp-2d)
5. [Approach 3: Bottom-Up DP (1D, Space Optimized)](#approach-3-bottom-up-dp-1d-space-optimized)
6. [Approach 4: In-Place DP (Mutate Input)](#approach-4-in-place-dp-mutate-input)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [63. Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Dynamic Programming`, `Matrix` |

### Description

> You are given an `m x n` integer array `grid`. There is a robot initially located at the top-left corner. The robot tries to move to the bottom-right corner. The robot can only move either down or right at any point in time.
>
> An obstacle and space are marked as `1` or `0` respectively in `grid`. A path that the robot takes cannot include any square that is an obstacle.
>
> Return *the number of possible unique paths that the robot can take to reach the bottom-right corner*.

### Examples

```
Example 1:
Input: obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
Output: 2

Example 2:
Input: obstacleGrid = [[0,1],[0,0]]
Output: 1
```

### Constraints

- `m == obstacleGrid.length`
- `n == obstacleGrid[i].length`
- `1 <= m, n <= 100`
- `obstacleGrid[i][j]` is `0` or `1`

---

## Problem Breakdown

### 1. What is being asked?

Same as [Problem 62](../0062-unique-paths/solution.md), but some cells are obstacles. A path cannot step on any obstacle.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `obstacleGrid` | `int[m][n]` | `0` = free, `1` = obstacle |

### 3. What is the output?

A single integer: the number of unique obstacle-free paths.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 100` | O(m*n) is fast |
| Boolean grid | DP recurrence has a simple modification |

### 5. Step-by-step example analysis

#### Example 1: `[[0,0,0],[0,1,0],[0,0,0]]`

```text
grid:        DP table:
0 0 0        1 1 1
0 1 0   →    1 0 1
0 0 0        1 1 2

dp[r][c] = 0 if obstacle, else dp[r-1][c] + dp[r][c-1].

Answer: dp[2][2] = 2.
```

### 6. Key Observations

1. **Recurrence with obstacle clamp** -- `dp[r][c] = (grid[r][c] == 1) ? 0 : dp[r-1][c] + dp[r][c-1]`.
2. **Border init must respect obstacles** -- `dp[0][c] = 1` only while no obstacle has been seen on row 0; once an obstacle appears, every cell to its right has 0 paths.
3. **Start or end blocked = 0** -- If `grid[0][0] == 1` or `grid[m-1][n-1] == 1`, return 0 (caught naturally by the recurrence).
4. **In-place DP** -- We can overwrite `obstacleGrid` itself if we accept mutation.

### 7. Pattern identification

Same DP family as Problem 62, with one extra check.

**Chosen pattern:** `Bottom-Up DP (1D)`.

---

## Approach 1: Top-Down DP with Memoization

### Idea

> Recurse on `(r, c)`. If obstacle, return 0. If at start, return 1. Otherwise sum the two predecessors. Cache.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Python

```python
class Solution:
    def uniquePathsWithObstaclesMemo(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        from functools import lru_cache
        @lru_cache(maxsize=None)
        def f(r: int, c: int) -> int:
            if r < 0 or c < 0 or grid[r][c] == 1: return 0
            if r == 0 and c == 0: return 1
            return f(r - 1, c) + f(r, c - 1)
        return f(m - 1, n - 1)
```

---

## Approach 2: Bottom-Up DP (2D)

### Algorithm

1. If `grid[0][0] == 1`: return 0.
2. `dp[0][0] = 1`. For `c = 1..n-1`: `dp[0][c] = (grid[0][c] == 1) ? 0 : dp[0][c-1]`.
3. For `r = 1..m-1`: `dp[r][0] = (grid[r][0] == 1) ? 0 : dp[r-1][0]`.
4. For `r = 1..m-1`, `c = 1..n-1`: `dp[r][c] = (grid[r][c] == 1) ? 0 : dp[r-1][c] + dp[r][c-1]`.
5. Return `dp[m-1][n-1]`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Go

```go
func uniquePathsWithObstacles2D(grid [][]int) int {
    m := len(grid)
    n := len(grid[0])
    if grid[0][0] == 1 {
        return 0
    }
    dp := make([][]int, m)
    for i := range dp {
        dp[i] = make([]int, n)
    }
    dp[0][0] = 1
    for c := 1; c < n; c++ {
        if grid[0][c] == 0 {
            dp[0][c] = dp[0][c-1]
        }
    }
    for r := 1; r < m; r++ {
        if grid[r][0] == 0 {
            dp[r][0] = dp[r-1][0]
        }
        for c := 1; c < n; c++ {
            if grid[r][c] == 0 {
                dp[r][c] = dp[r-1][c] + dp[r][c-1]
            }
        }
    }
    return dp[m-1][n-1]
}
```

---

## Approach 3: Bottom-Up DP (1D, Space Optimized)

### Idea

> Same as 2D but reuse a single array. After processing row `r`, `dp[c]` holds the count for `(r, c)`.

### Algorithm

1. If `grid[0][0] == 1`, return 0.
2. `dp = [0] * n`; `dp[0] = 1`.
3. For each row `r`:
   - For each column `c`:
     - If `grid[r][c] == 1`: `dp[c] = 0`.
     - Else if `c > 0`: `dp[c] += dp[c - 1]`.
4. Return `dp[n - 1]`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(n) |

### Implementation

#### Go

```go
func uniquePathsWithObstacles(grid [][]int) int {
    m := len(grid)
    n := len(grid[0])
    if grid[0][0] == 1 {
        return 0
    }
    dp := make([]int, n)
    dp[0] = 1
    for r := 0; r < m; r++ {
        for c := 0; c < n; c++ {
            if grid[r][c] == 1 {
                dp[c] = 0
            } else if c > 0 {
                dp[c] += dp[c-1]
            }
        }
    }
    return dp[n-1]
}
```

#### Java

```java
class Solution {
    public int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        if (grid[0][0] == 1) return 0;
        int[] dp = new int[n];
        dp[0] = 1;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) {
                    dp[c] = 0;
                } else if (c > 0) {
                    dp[c] += dp[c - 1];
                }
            }
        }
        return dp[n - 1];
    }
}
```

#### Python

```python
class Solution:
    def uniquePathsWithObstacles(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        if grid[0][0] == 1:
            return 0
        dp = [0] * n
        dp[0] = 1
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 1:
                    dp[c] = 0
                elif c > 0:
                    dp[c] += dp[c - 1]
        return dp[n - 1]
```

### Dry Run

```text
grid = [[0,0,0],[0,1,0],[0,0,0]]

dp = [1, 0, 0]   # initial

Row 0:
  c=0: grid[0][0]=0, c==0 → dp = [1, 0, 0]
  c=1: grid[0][1]=0 → dp[1] = 0 + 1 = 1 → dp = [1, 1, 0]
  c=2: grid[0][2]=0 → dp[2] = 0 + 1 = 1 → dp = [1, 1, 1]

Row 1:
  c=0: grid[1][0]=0 → dp = [1, 1, 1]
  c=1: grid[1][1]=1 → dp[1] = 0 → dp = [1, 0, 1]
  c=2: grid[1][2]=0 → dp[2] = 1 + 0 = 1 → dp = [1, 0, 1]

Row 2:
  c=0: grid[2][0]=0 → dp = [1, 0, 1]
  c=1: grid[2][1]=0 → dp[1] = 0 + 1 = 1 → dp = [1, 1, 1]
  c=2: grid[2][2]=0 → dp[2] = 1 + 1 = 2 → dp = [1, 1, 2]

Return dp[2] = 2.
```

---

## Approach 4: In-Place DP (Mutate Input)

### Idea

> Same recurrence but reuse `grid` to hold path counts. Saves the `dp` array entirely.

> Use only when mutating input is acceptable.

### Implementation

#### Python

```python
class Solution:
    def uniquePathsWithObstaclesInPlace(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        if grid[0][0] == 1: return 0
        grid[0][0] = 1
        for c in range(1, n):
            grid[0][c] = 0 if grid[0][c] == 1 else grid[0][c - 1]
        for r in range(1, m):
            grid[r][0] = 0 if grid[r][0] == 1 else grid[r - 1][0]
            for c in range(1, n):
                if grid[r][c] == 1:
                    grid[r][c] = 0
                else:
                    grid[r][c] = grid[r - 1][c] + grid[r][c - 1]
        return grid[m - 1][n - 1]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Memoization | O(mn) | O(mn) | Easy DP | Recursion |
| 2 | 2D Bottom-Up | O(mn) | O(mn) | Iterative | 2D extra grid |
| 3 | 1D Bottom-Up | O(mn) | O(n) | Optimal DP | -- |
| 4 | In-Place | O(mn) | O(1) | No extra grid | Mutates input |

### Which solution to choose?

- **In an interview:** Approach 3 -- shows space optimization
- **In production:** Approach 3 unless input mutation is acceptable
- **On Leetcode:** Approach 3

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Start blocked | `[[1,0],[0,0]]` | `0` | No path from `(0,0)` |
| 2 | End blocked | `[[0,0],[0,1]]` | `0` | No path to `(m-1,n-1)` |
| 3 | Single cell, free | `[[0]]` | `1` | Already at destination |
| 4 | Single cell, obstacle | `[[1]]` | `0` | Trapped |
| 5 | All free 3x3 | `[[0,0,0],[0,0,0],[0,0,0]]` | `6` | Reduces to Problem 62 |
| 6 | Center obstacle | `[[0,0,0],[0,1,0],[0,0,0]]` | `2` | Example 1 |
| 7 | Border row blocked | `[[0,1,0],[0,0,0],[0,0,0]]` | `3` | First row obstacle limits |
| 8 | All obstacles in row | `[[0],[1],[0]]` | `0` | Vertical block |

---

## Common Mistakes

### Mistake 1: Forgetting to handle a blocked starting cell

```python
# WRONG
dp[0][0] = 1   # always

# CORRECT
if grid[0][0] == 1: return 0
dp[0][0] = 1
```

**Reason:** If the start is itself an obstacle, no path exists.

### Mistake 2: Using `dp[c]` before checking obstacle in the 1D form

```python
# WRONG — adds dp[c-1] before checking that current cell is free
dp[c] += dp[c - 1]
if grid[r][c] == 1: dp[c] = 0   # too late

# CORRECT
if grid[r][c] == 1:
    dp[c] = 0
elif c > 0:
    dp[c] += dp[c - 1]
```

**Reason:** Adding before the check still works because we then zero out, but the order matters when reading the result — keep the check first for clarity.

### Mistake 3: Using `dp[c-1]` when `c == 0`

```python
# WRONG — out of bounds at c == 0
dp[c] += dp[c - 1]    # at c = 0 → dp[-1] → wraps in Python or crashes elsewhere

# CORRECT — guard
elif c > 0:
    dp[c] += dp[c - 1]
```

**Reason:** Column 0 has no predecessor on the left; only the row above contributes (handled by leaving `dp[c]` unchanged).

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [62. Unique Paths](https://leetcode.com/problems/unique-paths/) | :yellow_circle: Medium | Same DP without obstacles |
| 2 | [64. Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | :yellow_circle: Medium | Same DP, min instead of sum-of-paths |
| 3 | [980. Unique Paths III](https://leetcode.com/problems/unique-paths-iii/) | :red_circle: Hard | Variant with bidirectional moves |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Grid with obstacles marked
> - Step-by-step DP table fill
> - Highlight contributors (above and left)
> - Generate / clear obstacles, adjustable size
