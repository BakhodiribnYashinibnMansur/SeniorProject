# 0062. Unique Paths

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Recursion (Brute Force)](#approach-1-recursion-brute-force)
4. [Approach 2: Top-Down DP (Memoization)](#approach-2-top-down-dp-memoization)
5. [Approach 3: Bottom-Up DP (2D)](#approach-3-bottom-up-dp-2d)
6. [Approach 4: Bottom-Up DP (1D, Space Optimized)](#approach-4-bottom-up-dp-1d-space-optimized)
7. [Approach 5: Combinatorics (Math)](#approach-5-combinatorics-math)
8. [Complexity Comparison](#complexity-comparison)
9. [Edge Cases](#edge-cases)
10. [Common Mistakes](#common-mistakes)
11. [Related Problems](#related-problems)
12. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [62. Unique Paths](https://leetcode.com/problems/unique-paths/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Math`, `Dynamic Programming`, `Combinatorics` |

### Description

> There is a robot on an `m x n` grid. The robot is initially located at the top-left corner. The robot tries to move to the bottom-right corner. The robot can only move either down or right at any point in time.
>
> Given the two integers `m` and `n`, return *the number of possible unique paths*.

### Examples

```
Example 1:
Input: m = 3, n = 7
Output: 28

Example 2:
Input: m = 3, n = 2
Output: 3
Explanation: From the top-left corner, there are 3 ways to reach the bottom-right corner:
1. Right -> Down -> Down
2. Down -> Down -> Right
3. Down -> Right -> Down
```

### Constraints

- `1 <= m, n <= 100`
- The answer will be less than or equal to `2 * 10^9`

---

## Problem Breakdown

### 1. What is being asked?

Count the number of distinct sequences of `right` and `down` moves that start at `(0, 0)` and end at `(m-1, n-1)`. Every such path uses exactly `m - 1` down moves and `n - 1` right moves.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `m` | `int` | Number of rows (`1 <= m <= 100`) |
| `n` | `int` | Number of columns (`1 <= n <= 100`) |

### 3. What is the output?

A single integer: the count of unique paths.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 100` | Up to 10^4 cells. O(m*n) is fast |
| Answer up to `2 * 10^9` | Fits in `int` (32-bit signed) |

### 5. Step-by-step example analysis

#### Example 2: `m = 3, n = 2`

```text
DP table (dp[r][c] = number of paths from (0,0) to (r,c)):

  c=0  c=1
r=0  1    1
r=1  1    2     # right + 1, down + 1 = 1 + 1
r=2  1    3     # right + 1, down + 2 = 1 + 2

Answer: dp[2][1] = 3.
```

### 6. Key Observations

1. **Recurrence** -- `dp[r][c] = dp[r-1][c] + dp[r][c-1]`. The first row and column are all 1.
2. **1D rolling array** -- We only need the previous row. Compress to `O(n)` space.
3. **Closed form** -- The number of paths is `C(m+n-2, m-1)`. O(min(m,n)) time, O(1) space.
4. **Symmetry** -- The grid is symmetric in `m` and `n` -- `paths(m,n) == paths(n,m)`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| DP on grid | Optimal substructure, count paths |
| Combinatorics | Choose `m-1` downs out of `m+n-2` moves |

**Chosen pattern:** `Bottom-Up DP (1D)` for clarity, `Combinatorics` for theoretical optimum.

---

## Approach 1: Recursion (Brute Force)

### Thought process

> From `(r, c)`, the count is `f(r-1, c) + f(r, c-1)` for moves coming from above/left, with base cases at the borders.

### Algorithm

1. `f(0, 0) = 1`. (or define `f(r, c)` = paths from `(0,0)` to `(r,c)`)
2. `f(r, c) = f(r-1, c) + f(r, c-1)` for `r, c > 0`.
3. `f(0, c) = f(r, 0) = 1` (only one straight path).

### Complexity

| | Complexity |
|---|---|
| **Time** | O(2^(m+n)) | Exponential without memoization |
| **Space** | O(m+n) | Recursion depth |

> Too slow for `m, n > ~25`. Listed for understanding.

---

## Approach 2: Top-Down DP (Memoization)

### Idea

> Same recursion, cache `f(r, c)`. Each cell computed once.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Python

```python
class Solution:
    def uniquePathsMemo(self, m: int, n: int) -> int:
        from functools import lru_cache
        @lru_cache(maxsize=None)
        def f(r: int, c: int) -> int:
            if r == 0 or c == 0: return 1
            return f(r - 1, c) + f(r, c - 1)
        return f(m - 1, n - 1)
```

---

## Approach 3: Bottom-Up DP (2D)

### Algorithm

1. Allocate `dp[m][n]`. Set `dp[0][c] = dp[r][0] = 1`.
2. For `r = 1..m-1`, `c = 1..n-1`: `dp[r][c] = dp[r-1][c] + dp[r][c-1]`.
3. Return `dp[m-1][n-1]`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Python

```python
class Solution:
    def uniquePaths2D(self, m: int, n: int) -> int:
        dp = [[1] * n for _ in range(m)]
        for r in range(1, m):
            for c in range(1, n):
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1]
        return dp[m - 1][n - 1]
```

---

## Approach 4: Bottom-Up DP (1D, Space Optimized)

### Idea

> When computing row `r`, we only need row `r-1` and the partially-computed row `r`. Use a single 1D array.

### Algorithm

1. `dp = [1] * n`.
2. For `r = 1..m-1`:
   - For `c = 1..n-1`: `dp[c] = dp[c] + dp[c-1]`. (`dp[c]` on the right side is row `r-1` value; on the left side it becomes row `r` value.)
3. Return `dp[n-1]`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(n) |

### Implementation

#### Go

```go
func uniquePaths(m int, n int) int {
    dp := make([]int, n)
    for i := range dp { dp[i] = 1 }
    for r := 1; r < m; r++ {
        for c := 1; c < n; c++ {
            dp[c] = dp[c] + dp[c-1]
        }
    }
    return dp[n-1]
}
```

#### Java

```java
class Solution {
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[c] = dp[c] + dp[c - 1];
            }
        }
        return dp[n - 1];
    }
}
```

#### Python

```python
class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        dp = [1] * n
        for _ in range(1, m):
            for c in range(1, n):
                dp[c] += dp[c - 1]
        return dp[n - 1]
```

### Dry Run

```text
m=3, n=2
dp = [1, 1]

r=1: c=1: dp[1] = dp[1] + dp[0] = 1 + 1 = 2 → dp = [1, 2]
r=2: c=1: dp[1] = dp[1] + dp[0] = 2 + 1 = 3 → dp = [1, 3]

Return dp[1] = 3.
```

---

## Approach 5: Combinatorics (Math)

### Idea

> Every path has exactly `m + n - 2` moves: `m - 1` downs and `n - 1` rights. The number of distinct orderings is the binomial coefficient `C(m+n-2, m-1)`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(min(m, n)) |
| **Space** | O(1) |

### Implementation

#### Python

```python
class Solution:
    def uniquePathsMath(self, m: int, n: int) -> int:
        # C(m+n-2, min(m-1, n-1))
        a, b = m + n - 2, min(m - 1, n - 1)
        result = 1
        for i in range(b):
            result = result * (a - i) // (i + 1)
        return result
```

#### Go

```go
func uniquePathsMath(m, n int) int {
    a := m + n - 2
    b := m - 1
    if n-1 < b { b = n - 1 }
    result := 1
    for i := 0; i < b; i++ {
        result = result * (a - i) / (i + 1)
    }
    return result
}
```

#### Java

```java
class Solution {
    public int uniquePathsMath(int m, int n) {
        long a = m + n - 2;
        int b = Math.min(m - 1, n - 1);
        long result = 1;
        for (int i = 0; i < b; i++) {
            result = result * (a - i) / (i + 1);
        }
        return (int) result;
    }
}
```

> **Note:** Compute `result * (a - i)` before dividing by `(i + 1)` to keep intermediate values exact.

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Recursion | O(2^(m+n)) | O(m+n) | Simplest to write | Exponential |
| 2 | Memoization | O(m*n) | O(m*n) | Easy DP | Recursion overhead |
| 3 | Bottom-Up 2D | O(m*n) | O(m*n) | Iterative | 2D table |
| 4 | Bottom-Up 1D | O(m*n) | O(n) | Optimal DP | Same time |
| 5 | Combinatorics | O(min(m,n)) | O(1) | Theoretical optimum | Requires math insight |

### Which solution to choose?

- **In an interview:** Approach 4 -- common and demonstrates space optimization
- **In production:** Approach 5 -- closed form
- **On Leetcode:** Approach 4 or 5
- **For learning:** All five illustrate the DP family

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | 1x1 | `m=1, n=1` | `1` | Already at destination |
| 2 | 1xN | `m=1, n=10` | `1` | Only one row, one path |
| 3 | Mx1 | `m=10, n=1` | `1` | Only one column |
| 4 | Equal | `m=3, n=3` | `6` | Symmetric |
| 5 | Standard | `m=3, n=7` | `28` | Example 1 |
| 6 | Largest | `m=100, n=100` | `22750883079422934966181954039568885395604168260154104734000` mod 32-bit overflow concern | But constraint says ≤ 2*10^9 fits |

> The Leetcode constraint guarantees the answer fits in 32-bit signed int. For larger m,n use Python (arbitrary precision) or `long`.

---

## Common Mistakes

### Mistake 1: Initialization off-by-one

```python
# WRONG — sets dp[0][0] = 0 instead of 1
dp = [[0] * n for _ in range(m)]
for r in range(m): dp[r][0] = 1
for c in range(n): dp[0][c] = 1
# but if dp[0][0] was set after row init, fine; if before, lost

# CORRECT — fill all with 1 then iterate
dp = [[1] * n for _ in range(m)]
for r in range(1, m):
    for c in range(1, n):
        dp[r][c] = dp[r-1][c] + dp[r][c-1]
```

**Reason:** All border cells (row 0 or column 0) have exactly 1 path; ensure they are initialized before the recurrence reads them.

### Mistake 2: Combinatorics order of operations

```python
# WRONG — division loses precision
result = (a - i) / (i + 1) * result   # float division

# CORRECT — multiply first, then integer divide
result = result * (a - i) // (i + 1)
```

**Reason:** `result * (a - i)` is always divisible by `(i + 1)` because partial products of binomial coefficients are integers. Dividing too early introduces floats.

### Mistake 3: 1D update from the wrong direction

```python
# WRONG — updates dp[c] from current row instead of previous
for c in range(n - 1, 0, -1):    # backwards!
    dp[c] = dp[c] + dp[c - 1]

# CORRECT — forward direction; dp[c-1] already updated for current row
for c in range(1, n):
    dp[c] = dp[c] + dp[c - 1]
```

**Reason:** In the forward sweep, `dp[c]` (right side) holds the previous row, and `dp[c-1]` holds the current row. Backward sweep would mix dimensions incorrectly.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [63. Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | :yellow_circle: Medium | Same with obstacles |
| 2 | [64. Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | :yellow_circle: Medium | Same DP, different aggregator |
| 3 | [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | :green_circle: Easy | 1D version of the recurrence |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Grid that fills with the DP table values
> - Highlight current cell and the two contributors (`dp[r-1][c]`, `dp[r][c-1]`)
> - Toggle between 2D DP and 1D rolling array views
