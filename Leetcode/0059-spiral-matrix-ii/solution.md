# 0059. Spiral Matrix II

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Direction Vectors with Visited Matrix](#approach-1-direction-vectors-with-visited-matrix)
4. [Approach 2: Layer by Layer (Boundaries)](#approach-2-layer-by-layer-boundaries)
5. [Approach 3: Direction Vectors with Auto-Rotate (No Visited)](#approach-3-direction-vectors-with-auto-rotate-no-visited)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [59. Spiral Matrix II](https://leetcode.com/problems/spiral-matrix-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Matrix`, `Simulation` |

### Description

> Given a positive integer `n`, generate an `n x n` matrix filled with elements from `1` to `n^2` in spiral order.

### Examples

```
Example 1:
Input: n = 3
Output: [[1,2,3],[8,9,4],[7,6,5]]

Example 2:
Input: n = 1
Output: [[1]]
```

### Constraints

- `1 <= n <= 20`

---

## Problem Breakdown

### 1. What is being asked?

Build a square matrix of size `n × n` where the number `k` (for `k = 1..n^2`) is placed at the `k`-th cell of the spiral path that starts at `(0, 0)` and turns right -> down -> left -> up.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | Side length of the square matrix (`1 <= n <= 20`) |

Important observations:
- Always square (m == n)
- Small (`n <= 20`), so any reasonable algorithm passes
- Total cells = `n^2`

### 3. What is the output?

An `n × n` 2D array filled with `1..n^2` in spiral order.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 20` | At most 400 cells. Speed irrelevant; clarity matters |

### 5. Step-by-step example analysis

#### Example: `n = 3`

```text
Layer 0:
  Right along top:    1, 2, 3      → [[1,2,3],[ , , ],[ , , ]]
  Down along right:   4, 5         → [[1,2,3],[ , ,4],[ , ,5]]
  Left along bottom:  6, 7         → [[1,2,3],[ , ,4],[7,6,5]]
  Up along left:      8            → [[1,2,3],[8, ,4],[7,6,5]]

Layer 1 (just one cell):
  Right along top:    9            → [[1,2,3],[8,9,4],[7,6,5]]
```

### 6. Key Observations

1. **Mirror of [Problem 54](../0054-spiral-matrix/solution.md)** -- Same path, but we *write* instead of *read*.
2. **Always exactly `n^2` writes** -- We can simply loop `k = 1..n^2` and place each value.
3. **Direction-vector trick** -- The classic "rotate when next cell would be out of bounds or already filled" works perfectly because filled cells are non-zero.
4. **Layer-by-layer is also clean** -- Same boundary-shrinking idea as Problem 54.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Simulation / Direction vectors | Classic spiral walk |
| Boundary tracking | Layer by layer, shrink edges |

**Chosen pattern:** `Layer by Layer` for clarity, `Direction Vectors` for elegance.

---

## Approach 1: Direction Vectors with Visited Matrix

### Algorithm

1. Initialize `(r, c) = (0, 0)`, direction `d = 0` (right), `visited[n][n]`.
2. For `k = 1..n^2`:
   - Place `k` at `(r, c)`, mark `visited[r][c] = true`.
   - Compute next cell `(nr, nc)`. If out of bounds or visited, rotate.
   - Move.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n^2) |
| **Space** | O(n^2) — visited grid |

### Implementation

#### Python

```python
class Solution:
    def generateMatrixDirVec(self, n: int) -> List[List[int]]:
        m = [[0] * n for _ in range(n)]
        DR, DC = [0, 1, 0, -1], [1, 0, -1, 0]
        r, c, d = 0, 0, 0
        for k in range(1, n * n + 1):
            m[r][c] = k
            nr, nc = r + DR[d], c + DC[d]
            if not (0 <= nr < n and 0 <= nc < n) or m[nr][nc] != 0:
                d = (d + 1) % 4
                nr, nc = r + DR[d], c + DC[d]
            r, c = nr, nc
        return m
```

#### Go

```go
func generateMatrixDirVec(n int) [][]int {
    m := make([][]int, n)
    for i := range m { m[i] = make([]int, n) }
    dr := []int{0, 1, 0, -1}
    dc := []int{1, 0, -1, 0}
    r, c, d := 0, 0, 0
    for k := 1; k <= n*n; k++ {
        m[r][c] = k
        nr, nc := r+dr[d], c+dc[d]
        if nr < 0 || nr >= n || nc < 0 || nc >= n || m[nr][nc] != 0 {
            d = (d + 1) % 4
            nr, nc = r+dr[d], c+dc[d]
        }
        r, c = nr, nc
    }
    return m
}
```

#### Java

```java
class Solution {
    public int[][] generateMatrixDirVec(int n) {
        int[][] m = new int[n][n];
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, d = 0;
        for (int k = 1; k <= n * n; k++) {
            m[r][c] = k;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= n || nc < 0 || nc >= n || m[nr][nc] != 0) {
                d = (d + 1) % 4;
                nr = r + dr[d]; nc = c + dc[d];
            }
            r = nr; c = nc;
        }
        return m;
    }
}
```

> **Note:** We use the matrix itself as the "visited" indicator -- a non-zero cell means "filled". No extra grid is needed.

---

## Approach 2: Layer by Layer (Boundaries)

### Idea

> Write each ring of the spiral with four bounded sweeps.

### Algorithm

1. `top = 0, bottom = n-1, left = 0, right = n-1, value = 1`.
2. While `value <= n^2`:
   - Right along `row = top`: increment `value`, columns from `left` to `right`. Then `top++`.
   - Down along `col = right`: rows from `top` to `bottom`. Then `right--`.
   - Left along `row = bottom`: cols from `right` to `left`. Then `bottom--`.
   - Up along `col = left`: rows from `bottom` to `top`. Then `left++`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n^2) |
| **Space** | O(1) extra (output excluded) |

### Implementation

#### Go

```go
func generateMatrix(n int) [][]int {
    m := make([][]int, n)
    for i := range m { m[i] = make([]int, n) }
    top, bottom, left, right := 0, n-1, 0, n-1
    val := 1
    for val <= n*n {
        for c := left; c <= right; c++ { m[top][c] = val; val++ }
        top++
        for r := top; r <= bottom; r++ { m[r][right] = val; val++ }
        right--
        if top <= bottom {
            for c := right; c >= left; c-- { m[bottom][c] = val; val++ }
            bottom--
        }
        if left <= right {
            for r := bottom; r >= top; r-- { m[r][left] = val; val++ }
            left++
        }
    }
    return m
}
```

#### Java

```java
class Solution {
    public int[][] generateMatrix(int n) {
        int[][] m = new int[n][n];
        int top = 0, bottom = n - 1, left = 0, right = n - 1, val = 1;
        while (val <= n * n) {
            for (int c = left; c <= right; c++) m[top][c] = val++;
            top++;
            for (int r = top; r <= bottom; r++) m[r][right] = val++;
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) m[bottom][c] = val++;
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) m[r][left] = val++;
                left++;
            }
        }
        return m;
    }
}
```

#### Python

```python
class Solution:
    def generateMatrix(self, n: int) -> List[List[int]]:
        m = [[0] * n for _ in range(n)]
        top, bottom, left, right = 0, n - 1, 0, n - 1
        val = 1
        while val <= n * n:
            for c in range(left, right + 1):
                m[top][c] = val; val += 1
            top += 1
            for r in range(top, bottom + 1):
                m[r][right] = val; val += 1
            right -= 1
            if top <= bottom:
                for c in range(right, left - 1, -1):
                    m[bottom][c] = val; val += 1
                bottom -= 1
            if left <= right:
                for r in range(bottom, top - 1, -1):
                    m[r][left] = val; val += 1
                left += 1
        return m
```

### Dry Run

```text
n = 3, val = 1
Iter 1: top=0, bottom=2, left=0, right=2
  Right row 0: [1,2,3]      val=4; top=1
  Down  col 2: m[1][2]=4, m[2][2]=5  val=6; right=1
  Left  row 2: m[2][1]=6, m[2][0]=7  val=8; bottom=1
  Up    col 0: m[1][0]=8                val=9; left=1
Iter 2: top=1, bottom=1, left=1, right=1
  Right row 1: m[1][1]=9                val=10; top=2
  val=10 > n^2=9 → exit
Final: [[1,2,3],[8,9,4],[7,6,5]]
```

---

## Approach 3: Direction Vectors with Auto-Rotate (No Visited)

### Idea

> Same as Approach 1, but lean on the matrix itself as the visited marker. Cells start at `0`; a non-zero cell signals "already written".

> This is essentially Approach 1 written compactly. Listed here as an alternative phrasing without the explicit `visited` boolean array.

### Implementation

#### Python

```python
class Solution:
    def generateMatrixAuto(self, n: int) -> List[List[int]]:
        m = [[0] * n for _ in range(n)]
        DR, DC = [0, 1, 0, -1], [1, 0, -1, 0]
        r = c = d = 0
        for k in range(1, n * n + 1):
            m[r][c] = k
            nr, nc = r + DR[d], c + DC[d]
            if not (0 <= nr < n and 0 <= nc < n) or m[nr][nc]:
                d = (d + 1) % 4
                nr, nc = r + DR[d], c + DC[d]
            r, c = nr, nc
        return m
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Direction + Visited | O(n^2) | O(n^2) | Direction-vector pattern | Extra visited grid |
| 2 | Layer by Layer | O(n^2) | O(1) | Cleanest, no aux memory | Tricky boundary checks |
| 3 | Direction (auto) | O(n^2) | O(1) | Combines both ideas | Same as Approach 1 in spirit |

### Which solution to choose?

- **In an interview:** Approach 2 -- shows boundary discipline
- **In production:** Approach 2
- **On Leetcode:** All three accepted

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Smallest input | `n = 1` | `[[1]]` | Trivial single cell |
| 2 | Even side | `n = 2` | `[[1,2],[4,3]]` | No center cell |
| 3 | Standard | `n = 3` | `[[1,2,3],[8,9,4],[7,6,5]]` | Classic example |
| 4 | Even side larger | `n = 4` | full 4x4 spiral | Tests inner ring |
| 5 | Largest | `n = 20` | full 20x20 spiral | Confirms loop scales |

---

## Common Mistakes

### Mistake 1: Forgetting the inner-row guard (Approach 2)

```python
# WRONG — re-emits the row when only one row remains
while val <= n*n:
    sweep right; top++
    sweep down; right--
    sweep left; bottom--    # always; double-writes for n odd
    sweep up; left++

# CORRECT
while val <= n*n:
    sweep right; top++
    sweep down; right--
    if top <= bottom: sweep left; bottom--
    if left <= right: sweep up; left++
```

**Reason:** After the right and down sweeps, the leftover area might be a single row or column. Without guards, the third sweep re-writes the row already filled and overflows `val`.

### Mistake 2: Off-by-one in the value loop

```python
# WRONG — stops one early
while val < n*n:           # should be <=
    ...

# CORRECT
while val <= n*n:
    ...
```

**Reason:** We need to write `n*n` values total, including the one at `val == n*n`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [54. Spiral Matrix](https://leetcode.com/problems/spiral-matrix/) | :yellow_circle: Medium | Read instead of write |
| 2 | [885. Spiral Matrix III](https://leetcode.com/problems/spiral-matrix-iii/) | :yellow_circle: Medium | Spiral starting from arbitrary cell |
| 3 | [48. Rotate Image](https://leetcode.com/problems/rotate-image/) | :yellow_circle: Medium | Layer-by-layer rotation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - n × n grid that fills cell by cell
> - Direction arrow on the current cell
> - Boundary band highlighting (Layer-by-Layer view)
> - Selectable n (1..10)
