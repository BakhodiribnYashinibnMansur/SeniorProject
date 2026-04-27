# 0054. Spiral Matrix

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Direction Vectors with Visited Matrix](#approach-1-direction-vectors-with-visited-matrix)
4. [Approach 2: Layer by Layer (Boundaries)](#approach-2-layer-by-layer-boundaries)
5. [Approach 3: In-Place Marker (No Extra Space)](#approach-3-in-place-marker-no-extra-space)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [54. Spiral Matrix](https://leetcode.com/problems/spiral-matrix/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Matrix`, `Simulation` |

### Description

> Given an `m x n` matrix, return *all elements of the matrix in spiral order*.

### Examples

```
Example 1:
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [1,2,3,6,9,8,7,4,5]

Example 2:
Input: matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
Output: [1,2,3,4,8,12,11,10,9,5,6,7]
```

### Constraints

- `m == matrix.length`
- `n == matrix[i].length`
- `1 <= m, n <= 10`
- `-100 <= matrix[i][j] <= 100`

---

## Problem Breakdown

### 1. What is being asked?

Walk the matrix from the top-left corner, moving right until the boundary, then down, then left, then up, peeling off the outermost ring. Spiral inward and emit each element exactly once.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `matrix` | `int[m][n]` | A 2D rectangular grid (`1 <= m, n <= 10`) |

Important observations about the input:
- Rectangular, may be non-square
- Small dimensions (`m, n <= 10`), so any reasonable algorithm passes
- All cells must appear in the output exactly once

### 3. What is the output?

A list of `m * n` integers in spiral order: right -> down -> left -> up -> right -> ... peeling toward the center.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 10` | At most 100 cells. Speed is not a concern; clarity matters most |
| Rectangular shape | Last spiral can be a single row, column, or cell |

### 5. Step-by-step example analysis

#### Example 2: `matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]`

```text
Layer 0 (outer ring):
  Right along top:    1, 2, 3, 4
  Down along right:   8, 12
  Left along bottom: 11, 10, 9
  Up along left:      5

Layer 1 (inner ring):
  Right along inner top: 6, 7
  No more layers.

Order emitted: 1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7
```

### 6. Key Observations

1. **Spiral = four bounded sweeps repeated** -- right, down, left, up. Each sweep shrinks one boundary by one.
2. **Stopping condition** -- We stop when we have emitted `m * n` elements, or equivalently when boundaries cross.
3. **Edge case: a single remaining row or column** -- After the right and down passes, if `top > bottom` or `left > right`, the left/up passes must be skipped to avoid double-counting.
4. **Direction vectors give a uniform algorithm** -- Use deltas `(dr, dc)` and rotate when we hit a boundary or a visited cell.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Simulation | Walk the matrix following a fixed rule | This problem |
| Boundary tracking | Keep four shrinking pointers | Layer-by-layer approach |
| Direction array | Cycle through `(dr, dc)` tuples | Direction-vector approach |

**Chosen pattern:** `Layer by Layer (Boundaries)` for clarity, `Direction Vectors` for elegance.

---

## Approach 1: Direction Vectors with Visited Matrix

### Thought process

> Walk the matrix one step at a time. Use the direction array `[(0,1),(1,0),(0,-1),(-1,0)]`. When the next step would go out of bounds or onto a visited cell, rotate to the next direction.

### Algorithm (step-by-step)

1. Initialize position `(r, c) = (0, 0)`, direction index `d = 0`, and a visited 2D array.
2. Repeat `m * n` times:
   - Append `matrix[r][c]` to the result.
   - Mark `visited[r][c] = true`.
   - Compute the candidate next cell `(nr, nc) = (r + dr[d], c + dc[d])`.
   - If out of bounds or visited, rotate: `d = (d + 1) % 4` and recompute next cell.
   - Move: `r, c = nr, nc`.

### Pseudocode

```text
DR = [0, 1, 0, -1]
DC = [1, 0, -1, 0]
function spiralOrder(matrix):
    m, n = rows, cols
    visited = m x n grid of false
    r, c, d = 0, 0, 0
    result = []
    for _ in 0..m*n-1:
        result.append(matrix[r][c])
        visited[r][c] = true
        nr, nc = r + DR[d], c + DC[d]
        if out of bounds or visited[nr][nc]:
            d = (d + 1) % 4
            nr, nc = r + DR[d], c + DC[d]
        r, c = nr, nc
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m * n) | Each cell visited exactly once |
| **Space** | O(m * n) | Visited matrix (output excluded) |

### Implementation

#### Go

```go
func spiralOrderDirVec(matrix [][]int) []int {
    if len(matrix) == 0 {
        return []int{}
    }
    m, n := len(matrix), len(matrix[0])
    visited := make([][]bool, m)
    for i := range visited {
        visited[i] = make([]bool, n)
    }
    dr := []int{0, 1, 0, -1}
    dc := []int{1, 0, -1, 0}

    result := make([]int, 0, m*n)
    r, c, d := 0, 0, 0
    for i := 0; i < m*n; i++ {
        result = append(result, matrix[r][c])
        visited[r][c] = true
        nr, nc := r+dr[d], c+dc[d]
        if nr < 0 || nr >= m || nc < 0 || nc >= n || visited[nr][nc] {
            d = (d + 1) % 4
            nr, nc = r+dr[d], c+dc[d]
        }
        r, c = nr, nc
    }
    return result
}
```

#### Java

```java
class Solution {
    public List<Integer> spiralOrderDirVec(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;
        int m = matrix.length, n = matrix[0].length;
        boolean[][] visited = new boolean[m][n];
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, d = 0;
        for (int i = 0; i < m * n; i++) {
            result.add(matrix[r][c]);
            visited[r][c] = true;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || visited[nr][nc]) {
                d = (d + 1) % 4;
                nr = r + dr[d]; nc = c + dc[d];
            }
            r = nr; c = nc;
        }
        return result;
    }
}
```

#### Python

```python
class Solution:
    def spiralOrderDirVec(self, matrix: List[List[int]]) -> List[int]:
        if not matrix:
            return []
        m, n = len(matrix), len(matrix[0])
        visited = [[False] * n for _ in range(m)]
        DR, DC = [0, 1, 0, -1], [1, 0, -1, 0]
        r, c, d = 0, 0, 0
        result = []
        for _ in range(m * n):
            result.append(matrix[r][c])
            visited[r][c] = True
            nr, nc = r + DR[d], c + DC[d]
            if not (0 <= nr < m and 0 <= nc < n) or visited[nr][nc]:
                d = (d + 1) % 4
                nr, nc = r + DR[d], c + DC[d]
            r, c = nr, nc
        return result
```

### Dry Run

```text
matrix =
  [1, 2, 3, 4]
  [5, 6, 7, 8]
  [9,10,11,12]

Start (r,c)=(0,0), d=0 (right).
i=0: append 1, mark (0,0). next=(0,1) ok. → (0,1)
i=1: append 2, mark (0,1). next=(0,2) ok. → (0,2)
i=2: append 3, mark (0,2). next=(0,3) ok. → (0,3)
i=3: append 4, mark (0,3). next=(0,4) OOB → rotate to d=1 (down). next=(1,3) → (1,3)
i=4: append 8, mark. next=(2,3) → (2,3)
i=5: append 12, mark. next=(3,3) OOB → rotate d=2 (left). next=(2,2) → (2,2)
i=6: append 11. next=(2,1) → (2,1)
i=7: append 10. next=(2,0) → (2,0)
i=8: append 9. next=(2,-1) OOB → rotate d=3 (up). next=(1,0) → (1,0)
i=9: append 5. next=(0,0) visited → rotate d=0 (right). next=(1,1) → (1,1)
i=10: append 6. next=(1,2) → (1,2)
i=11: append 7. (last)

Result: [1,2,3,4,8,12,11,10,9,5,6,7]
```

---

## Approach 2: Layer by Layer (Boundaries)

### The problem with Approach 1

> Approach 1 uses an `m * n` boolean array. Boundaries let us avoid that and walk in fewer iterations of the outer loop.

### Optimization idea

> Track four boundaries -- `top`, `bottom`, `left`, `right` -- and shrink them as each side is traversed.

### Algorithm (step-by-step)

1. Initialize `top = 0`, `bottom = m - 1`, `left = 0`, `right = n - 1`.
2. While `top <= bottom` and `left <= right`:
   - Sweep right along row `top` from `left` to `right`. Increment `top`.
   - Sweep down along col `right` from `top` to `bottom`. Decrement `right`.
   - If `top <= bottom`: sweep left along row `bottom` from `right` to `left`. Decrement `bottom`.
   - If `left <= right`: sweep up along col `left` from `bottom` to `top`. Increment `left`.

### Pseudocode

```text
top, bottom, left, right = 0, m-1, 0, n-1
result = []
while top <= bottom and left <= right:
    for c in left..right: result.append(matrix[top][c])
    top++
    for r in top..bottom: result.append(matrix[r][right])
    right--
    if top <= bottom:
        for c in right downto left: result.append(matrix[bottom][c])
        bottom--
    if left <= right:
        for r in bottom downto top: result.append(matrix[r][left])
        left++
return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m * n) | Each cell visited once |
| **Space** | O(1) | Just four boundary pointers (output excluded) |

### Implementation

#### Go

```go
func spiralOrder(matrix [][]int) []int {
    if len(matrix) == 0 {
        return []int{}
    }
    m, n := len(matrix), len(matrix[0])
    top, bottom, left, right := 0, m-1, 0, n-1
    result := make([]int, 0, m*n)

    for top <= bottom && left <= right {
        // Sweep right along the top row
        for c := left; c <= right; c++ {
            result = append(result, matrix[top][c])
        }
        top++
        // Sweep down along the right column
        for r := top; r <= bottom; r++ {
            result = append(result, matrix[r][right])
        }
        right--
        // Sweep left along the bottom row, if a row remains
        if top <= bottom {
            for c := right; c >= left; c-- {
                result = append(result, matrix[bottom][c])
            }
            bottom--
        }
        // Sweep up along the left column, if a column remains
        if left <= right {
            for r := bottom; r >= top; r-- {
                result = append(result, matrix[r][left])
            }
            left++
        }
    }
    return result
}
```

#### Java

```java
class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;
        int m = matrix.length, n = matrix[0].length;
        int top = 0, bottom = m - 1, left = 0, right = n - 1;
        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) result.add(matrix[top][c]);
            top++;
            for (int r = top; r <= bottom; r++) result.add(matrix[r][right]);
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) result.add(matrix[bottom][c]);
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) result.add(matrix[r][left]);
                left++;
            }
        }
        return result;
    }
}
```

#### Python

```python
class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        if not matrix:
            return []
        m, n = len(matrix), len(matrix[0])
        top, bottom, left, right = 0, m - 1, 0, n - 1
        result: List[int] = []
        while top <= bottom and left <= right:
            for c in range(left, right + 1):
                result.append(matrix[top][c])
            top += 1
            for r in range(top, bottom + 1):
                result.append(matrix[r][right])
            right -= 1
            if top <= bottom:
                for c in range(right, left - 1, -1):
                    result.append(matrix[bottom][c])
                bottom -= 1
            if left <= right:
                for r in range(bottom, top - 1, -1):
                    result.append(matrix[r][left])
                left += 1
        return result
```

### Dry Run

```text
matrix =
  [1, 2, 3, 4]
  [5, 6, 7, 8]
  [9,10,11,12]

Iteration 1: top=0, bottom=2, left=0, right=3
  Right sweep row 0: [1, 2, 3, 4]; top=1
  Down sweep col 3: [8, 12]; right=2
  top(1) <= bottom(2): Left sweep row 2: [11, 10, 9]; bottom=1
  left(0) <= right(2): Up sweep col 0: [5]; left=1

Iteration 2: top=1, bottom=1, left=1, right=2
  Right sweep row 1: [6, 7]; top=2
  Down sweep col 2: nothing (top=2 > bottom=1); right=1
  top(2) > bottom(1): skip left sweep
  left(1) <= right(1): up sweep col 1 from bottom=1 to top=2 — empty; left=2

Iteration 3: top=2 > bottom=1 → stop.

Result: [1,2,3,4,8,12,11,10,9,5,6,7]
```

---

## Approach 3: In-Place Marker (No Extra Space)

### Idea

> Replace each visited cell with a sentinel value that cannot occur in the input. We choose a value outside `[-100, 100]`, e.g. `INT_MAX`. Then the same direction-vector loop works without an extra `visited` array, but we mutate the input.

> Use only when mutating the input is acceptable. In an interview, mention this trade-off explicitly.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m * n) | Each cell visited once |
| **Space** | O(1) | No auxiliary structure beyond output |

### Implementation

#### Go

```go
func spiralOrderInPlace(matrix [][]int) []int {
    if len(matrix) == 0 {
        return []int{}
    }
    const SENTINEL = 1 << 30
    m, n := len(matrix), len(matrix[0])
    dr := []int{0, 1, 0, -1}
    dc := []int{1, 0, -1, 0}
    result := make([]int, 0, m*n)
    r, c, d := 0, 0, 0
    for i := 0; i < m*n; i++ {
        result = append(result, matrix[r][c])
        matrix[r][c] = SENTINEL
        nr, nc := r+dr[d], c+dc[d]
        if nr < 0 || nr >= m || nc < 0 || nc >= n || matrix[nr][nc] == SENTINEL {
            d = (d + 1) % 4
            nr, nc = r+dr[d], c+dc[d]
        }
        r, c = nr, nc
    }
    return result
}
```

#### Java

```java
class Solution {
    public List<Integer> spiralOrderInPlace(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;
        final int SENTINEL = Integer.MIN_VALUE;
        int m = matrix.length, n = matrix[0].length;
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, d = 0;
        for (int i = 0; i < m * n; i++) {
            result.add(matrix[r][c]);
            matrix[r][c] = SENTINEL;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || matrix[nr][nc] == SENTINEL) {
                d = (d + 1) % 4;
                nr = r + dr[d]; nc = c + dc[d];
            }
            r = nr; c = nc;
        }
        return result;
    }
}
```

#### Python

```python
class Solution:
    def spiralOrderInPlace(self, matrix: List[List[int]]) -> List[int]:
        if not matrix:
            return []
        SENTINEL = 1 << 30
        m, n = len(matrix), len(matrix[0])
        DR, DC = [0, 1, 0, -1], [1, 0, -1, 0]
        r, c, d = 0, 0, 0
        result = []
        for _ in range(m * n):
            result.append(matrix[r][c])
            matrix[r][c] = SENTINEL
            nr, nc = r + DR[d], c + DC[d]
            if not (0 <= nr < m and 0 <= nc < n) or matrix[nr][nc] == SENTINEL:
                d = (d + 1) % 4
                nr, nc = r + DR[d], c + DC[d]
            r, c = nr, nc
        return result
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Direction Vectors + Visited | O(mn) | O(mn) | Easy to generalize | Extra m*n boolean array |
| 2 | Layer by Layer (Boundaries) | O(mn) | O(1) | No auxiliary memory | Tricky boundary checks |
| 3 | In-Place Marker | O(mn) | O(1) | Combines both ideas | Mutates input |

### Which solution to choose?

- **In an interview:** Approach 2 -- shows understanding of boundaries and avoids extra memory
- **In production:** Approach 2 unless mutating input is acceptable
- **On Leetcode:** Any of the three accepts
- **For learning:** All three illustrate different simulation styles

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Single cell | `[[5]]` | `[5]` | Smallest grid |
| 2 | Single row | `[[1,2,3,4]]` | `[1,2,3,4]` | Only the right sweep applies |
| 3 | Single column | `[[1],[2],[3]]` | `[1,2,3]` | Only the down sweep applies |
| 4 | Square | `[[1,2,3],[4,5,6],[7,8,9]]` | `[1,2,3,6,9,8,7,4,5]` | Standard spiral |
| 5 | Wide rectangle | `[[1,2,3,4],[5,6,7,8]]` | `[1,2,3,4,8,7,6,5]` | Two-row case |
| 6 | Tall rectangle | `[[1,2],[3,4],[5,6]]` | `[1,2,4,6,5,3]` | Two-column case |
| 7 | 2x2 | `[[1,2],[3,4]]` | `[1,2,4,3]` | Smallest non-trivial spiral |
| 8 | Includes negative | `[[-1,-2],[-3,-4]]` | `[-1,-2,-4,-3]` | Same shape |

---

## Common Mistakes

### Mistake 1: Forgetting the inner-row / inner-column guard

```python
# WRONG — emits the row twice when only one row remains
while top <= bottom and left <= right:
    sweep right (top++)
    sweep down (right--)
    sweep left (bottom--)        # always; double-counts on single row
    sweep up (left++)

# CORRECT
while top <= bottom and left <= right:
    sweep right (top++)
    sweep down (right--)
    if top <= bottom: sweep left (bottom--)
    if left <= right: sweep up (left++)
```

**Reason:** After incrementing `top` and decrementing `right`, the leftover area might be a single row or a single column. Without guards, the third sweep re-emits the row already consumed.

### Mistake 2: Wrong loop count

```python
# WRONG — uses while True with no exit
while True:
    ...   # never terminates

# CORRECT — count exactly m*n iterations or use boundary loop
for _ in range(m * n): ...
```

**Reason:** Without a definite exit condition, infinite loops sneak in for non-square inputs.

### Mistake 3: Off-by-one in the down/up sweep

```python
# WRONG — visits the same row twice
for r in range(top - 1, bottom + 1): ...  # off by one in start

# CORRECT
for r in range(top, bottom + 1): ...   # after top has been incremented
```

**Reason:** The right column was already entered at row `top - 1` during the right sweep. Start the down sweep at the *new* `top`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [59. Spiral Matrix II](https://leetcode.com/problems/spiral-matrix-ii/) | :yellow_circle: Medium | Build a matrix in spiral order |
| 2 | [885. Spiral Matrix III](https://leetcode.com/problems/spiral-matrix-iii/) | :yellow_circle: Medium | Spiral with arbitrary start, allowed to leave |
| 3 | [48. Rotate Image](https://leetcode.com/problems/rotate-image/) | :yellow_circle: Medium | Layer-by-layer rotations |
| 4 | [73. Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes/) | :yellow_circle: Medium | Matrix simulation with in-place state |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Matrix grid with current pointer and trail of visited cells
> - Direction-vector view: arrows showing the current heading
> - Layer-by-layer view: highlighted boundary that shrinks
> - Random matrix generator with selectable shape
