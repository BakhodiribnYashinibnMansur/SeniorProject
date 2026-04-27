# 0073. Set Matrix Zeroes

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Extra Boolean Arrays](#approach-1-extra-boolean-arrays)
4. [Approach 2: First Row / Column as Markers (O(1) space)](#approach-2-first-row--column-as-markers-o1-space)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [73. Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Hash Table`, `Matrix` |

### Description

> Given an `m x n` integer matrix `matrix`, if an element is `0`, set its entire row and column to `0`'s.
>
> You must do it **in place**.
>
> **Follow up:**
> - A straight forward solution using `O(mn)` space is probably a bad idea.
> - A simple improvement uses `O(m + n)` space, but still not the best solution.
> - Could you devise a constant space solution?

### Examples

```
Example 1:
Input: matrix = [[1,1,1],[1,0,1],[1,1,1]]
Output: [[1,0,1],[0,0,0],[1,0,1]]

Example 2:
Input: matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
Output: [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
```

### Constraints

- `m == matrix.length`
- `n == matrix[0].length`
- `1 <= m, n <= 200`
- `-2^31 <= matrix[i][j] <= 2^31 - 1`

---

## Problem Breakdown

### 1. What is being asked?

Find every cell containing `0`, and set every cell in its row and column to `0`. The catch is that we should do it without allocating a copy of the matrix.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `matrix` | `int[m][n]` | 2D matrix to be modified in place |

### 3. What is the output?

The matrix is modified in place; no return value (or returns the same matrix in some signatures).

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 200` | At most 40,000 cells. Speed easy. Constant-space is the interesting challenge |

### 5. Step-by-step example analysis

#### Example 1: `[[1,1,1],[1,0,1],[1,1,1]]`

```text
First pass — collect rows and columns containing 0:
  zero rows = {1}, zero cols = {1}

Second pass — zero those rows and columns:
  row 1 → 0 0 0
  col 1 → 0 in every row

Result:
1 0 1
0 0 0
1 0 1
```

### 6. Key Observations

1. **Two-pass with row/col sets** -- O(m + n) auxiliary space.
2. **Constant space trick** -- Use the first row and first column as markers. Two booleans capture whether the first row / first column themselves contained zeros.
3. **Order of zeroing matters** -- If we zero out the first row before reading marker cells, we lose information.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Auxiliary arrays | Standard, easy |
| In-place markers | Reuse part of the matrix |

**Chosen pattern:** `In-Place First Row/Column Markers` for the optimal answer.

---

## Approach 1: Extra Boolean Arrays

### Algorithm

1. Create `zeroRows[m]` and `zeroCols[n]` (booleans, all false).
2. Scan the matrix: if `matrix[i][j] == 0`, set `zeroRows[i] = true` and `zeroCols[j] = true`.
3. Scan again: if `zeroRows[i]` or `zeroCols[j]`, set `matrix[i][j] = 0`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m + n) |

### Implementation

#### Python

```python
class Solution:
    def setZeroesO_m_plus_n(self, matrix: List[List[int]]) -> None:
        m, n = len(matrix), len(matrix[0])
        zr = [False] * m
        zc = [False] * n
        for i in range(m):
            for j in range(n):
                if matrix[i][j] == 0:
                    zr[i] = True; zc[j] = True
        for i in range(m):
            for j in range(n):
                if zr[i] or zc[j]:
                    matrix[i][j] = 0
```

---

## Approach 2: First Row / Column as Markers (O(1) space)

### Idea

> Rather than allocating two extra arrays, reuse the first row of the matrix to store column markers and the first column to store row markers. Use two extra booleans to remember whether the first row / first column themselves contained any zero, since the marker cell `matrix[0][0]` is shared and ambiguous.

### Algorithm (step-by-step)

1. Determine `firstRowZero` and `firstColZero`: scan the first row and column for any `0`.
2. For each cell `(i, j)` with `i, j >= 1`: if `matrix[i][j] == 0`, set `matrix[i][0] = 0` and `matrix[0][j] = 0`.
3. For each cell `(i, j)` with `i, j >= 1`: if `matrix[i][0] == 0` or `matrix[0][j] == 0`, set `matrix[i][j] = 0`.
4. If `firstRowZero`, zero out the first row.
5. If `firstColZero`, zero out the first column.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(1) |

### Implementation

#### Go

```go
func setZeroes(matrix [][]int) {
    m := len(matrix)
    n := len(matrix[0])
    firstRowZero, firstColZero := false, false
    for j := 0; j < n; j++ {
        if matrix[0][j] == 0 {
            firstRowZero = true
            break
        }
    }
    for i := 0; i < m; i++ {
        if matrix[i][0] == 0 {
            firstColZero = true
            break
        }
    }
    for i := 1; i < m; i++ {
        for j := 1; j < n; j++ {
            if matrix[i][j] == 0 {
                matrix[i][0] = 0
                matrix[0][j] = 0
            }
        }
    }
    for i := 1; i < m; i++ {
        for j := 1; j < n; j++ {
            if matrix[i][0] == 0 || matrix[0][j] == 0 {
                matrix[i][j] = 0
            }
        }
    }
    if firstRowZero {
        for j := 0; j < n; j++ {
            matrix[0][j] = 0
        }
    }
    if firstColZero {
        for i := 0; i < m; i++ {
            matrix[i][0] = 0
        }
    }
}
```

#### Java

```java
class Solution {
    public void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstRowZero = false, firstColZero = false;
        for (int j = 0; j < n; j++) if (matrix[0][j] == 0) { firstRowZero = true; break; }
        for (int i = 0; i < m; i++) if (matrix[i][0] == 0) { firstColZero = true; break; }
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                if (matrix[i][0] == 0 || matrix[0][j] == 0)
                    matrix[i][j] = 0;
        if (firstRowZero) for (int j = 0; j < n; j++) matrix[0][j] = 0;
        if (firstColZero) for (int i = 0; i < m; i++) matrix[i][0] = 0;
    }
}
```

#### Python

```python
class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        m, n = len(matrix), len(matrix[0])
        first_row_zero = any(matrix[0][j] == 0 for j in range(n))
        first_col_zero = any(matrix[i][0] == 0 for i in range(m))
        for i in range(1, m):
            for j in range(1, n):
                if matrix[i][j] == 0:
                    matrix[i][0] = 0
                    matrix[0][j] = 0
        for i in range(1, m):
            for j in range(1, n):
                if matrix[i][0] == 0 or matrix[0][j] == 0:
                    matrix[i][j] = 0
        if first_row_zero:
            for j in range(n): matrix[0][j] = 0
        if first_col_zero:
            for i in range(m): matrix[i][0] = 0
```

### Dry Run

```text
matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]

firstRowZero = True (row 0 has 0)
firstColZero = True (col 0 has 0 at (0,0))

Mark: scan i,j >= 1
  No zeros inside the inner sub-matrix.
After marking: matrix unchanged.

Apply: scan i,j >= 1; if matrix[i][0]==0 or matrix[0][j]==0, set 0.
  (1,1): matrix[1][0]=3, matrix[0][1]=1 → no
  (1,2): matrix[0][2]=2 → no
  (1,3): matrix[0][3]=0 → 0
  (2,1): col 0 was originally 1, but was the first col zero based on (0,0)? Yes, firstColZero=True implied... wait, look more carefully:
  matrix[2][0]=1, matrix[0][1]=1 → no
  (2,2): matrix[2][0]=1, matrix[0][2]=2 → no
  (2,3): matrix[0][3]=0 → 0

After apply: [[0,1,2,0],[3,4,5,0],[1,3,1,0]]

firstRowZero → zero first row: [[0,0,0,0],...]
firstColZero → zero first col: [[0,...],[0,...],[0,...]]

Final: [[0,0,0,0],[0,4,5,0],[0,3,1,0]]   ✓
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Extra Boolean Arrays | O(mn) | O(m+n) | Simplest | Extra space |
| 2 | First Row/Col Markers | O(mn) | O(1) | Constant space | Trickier |

### Which solution to choose?

- **In an interview:** Approach 2 (the asked follow-up)
- **In production:** Either; readability often wins
- **On Leetcode:** Approach 2

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | No zeros | Matrix unchanged |
| 2 | All zeros | All zeros |
| 3 | Single row | Standard with col marker only |
| 4 | Single column | Standard with row marker only |
| 5 | 1x1 | Trivial: zero stays zero, non-zero stays |
| 6 | Zero in `(0, 0)` | Both first-row and first-col flags set |
| 7 | Zero in first row but not first col | firstRowZero=true |
| 8 | Zero in first col but not first row | firstColZero=true |

---

## Common Mistakes

### Mistake 1: Zeroing the first row/column too early

```python
# WRONG — zeros markers before reading them
if matrix[0][j] == 0:
    matrix[0][j] = 0  # well, it's already zero
    for i in range(m): matrix[i][j] = 0   # zeros the whole column
# This loses marker information when later cells reference it.

# CORRECT — capture flags, defer zeroing of markers until end
```

### Mistake 2: Forgetting first row/column scan

```python
# WRONG — only checks inner submatrix, leaves first row/col zeros undetected
for i in range(1, m):
    for j in range(1, n): ...

# CORRECT — explicit first-row / first-col scans
first_row_zero = any(matrix[0][j] == 0 for j in range(n))
first_col_zero = any(matrix[i][0] == 0 for i in range(m))
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [289. Game of Life](https://leetcode.com/problems/game-of-life/) | :yellow_circle: Medium | In-place matrix mutation with markers |
| 2 | [48. Rotate Image](https://leetcode.com/problems/rotate-image/) | :yellow_circle: Medium | In-place matrix transformation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Matrix display with row/col markers highlighted
> - Step-by-step three phases (find / mark / apply)
> - Toggle between O(m+n) and O(1) approaches
