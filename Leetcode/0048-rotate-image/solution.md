# 0048. Rotate Image

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Transpose + Reverse Rows](#approach-1-transpose--reverse-rows)
4. [Approach 2: Rotate Four Cells at a Time](#approach-2-rotate-four-cells-at-a-time)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [48. Rotate Image](https://leetcode.com/problems/rotate-image/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Math`, `Matrix` |

### Description

> You are given an `n x n` 2D matrix representing an image, rotate the image by **90 degrees** (clockwise).
>
> You have to rotate the image **in-place**, which means you have to modify the input 2D matrix directly. **DO NOT** allocate another 2D matrix and do the rotation.

### Examples

```
Example 1:
Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
Output: [[7,4,1],[8,5,2],[9,6,3]]

Example 2:
Input: matrix = [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]
Output: [[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]
```

### Constraints

- `n == matrix.length == matrix[i].length`
- `1 <= n <= 20`
- `-1000 <= matrix[i][j] <= 1000`

---

## Problem Breakdown

### 1. What is being asked?

Rotate an `n x n` matrix 90 degrees clockwise **in-place**. After rotation, the element at position `(i, j)` should end up at position `(j, n-1-i)`.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `matrix` | `int[][]` | An n x n 2D integer matrix |

Important observations about the input:
- The matrix is always **square** (n x n)
- Rotation must be done **in-place** — no extra matrix allowed
- Values can be **negative**
- `n` is small (max 20), so even O(n^2) solutions are fine

### 3. What is the output?

- No return value — modify the matrix in-place
- After rotation, `matrix[i][j]` should contain the value that was originally at `matrix[n-1-j][i]`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 20` | Very small — O(n^2) is at most 400 operations |
| `n >= 1` | Could be a 1x1 matrix (trivial — already rotated) |
| In-place | Cannot allocate another n x n matrix |

### 5. Step-by-step example analysis

#### Example 1: `matrix = [[1,2,3],[4,5,6],[7,8,9]]`

```text
Original:          After 90° CW rotation:
1  2  3            7  4  1
4  5  6     →      8  5  2
7  8  9            9  6  3

Mapping:
(0,0)=1 → (0,2)    (0,1)=2 → (1,2)    (0,2)=3 → (2,2)
(1,0)=4 → (0,1)    (1,1)=5 → (1,1)    (1,2)=6 → (2,1)
(2,0)=7 → (0,0)    (2,1)=8 → (1,0)    (2,2)=9 → (2,0)

General rule: element at (i, j) moves to (j, n-1-i)
```

#### Example 2: `matrix = [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]`

```text
Original:              After 90° CW rotation:
 5   1   9  11         15  13   2   5
 2   4   8  10    →    14   3   4   1
13   3   6   7         12   6   8   9
15  14  12  16         16   7  10  11
```

### 6. Key Observations

1. **Rotation formula** — `(i, j) → (j, n-1-i)` for 90° clockwise rotation.
2. **Two-step decomposition** — A 90° clockwise rotation equals: **transpose** (swap rows and columns) then **reverse each row**.
3. **Four-way cycle** — Each element is part of a cycle of 4 positions: `(i,j) → (j,n-1-i) → (n-1-i,n-1-j) → (n-1-j,i) → (i,j)`. We can rotate all four at once.
4. **Center element** — For odd `n`, the center element stays in place.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Transpose + Reverse | Decompose rotation into two simple operations | Rotate Image (this problem) |
| Four-way swap | Directly move 4 elements in a cycle | Rotate Image (this problem) |

**Chosen patterns:** Both are optimal with O(n^2) time and O(1) space.

---

## Approach 1: Transpose + Reverse Rows

### Thought process

> A 90° clockwise rotation can be decomposed into two steps:
> 1. **Transpose** the matrix (swap `matrix[i][j]` with `matrix[j][i]`)
> 2. **Reverse each row**
>
> Why does this work?
> - Transpose maps `(i, j) → (j, i)`
> - Reverse row maps `(j, i) → (j, n-1-i)`
> - Combined: `(i, j) → (j, n-1-i)` — exactly the 90° clockwise rotation!

### Algorithm (step-by-step)

1. **Transpose the matrix:**
   - For each cell `(i, j)` where `j > i`, swap `matrix[i][j]` with `matrix[j][i]`
   - We only iterate the upper triangle to avoid swapping twice
2. **Reverse each row:**
   - For each row, use two pointers to swap elements from both ends

### Pseudocode

```text
function rotate(matrix):
    n = matrix.length

    // Step 1: Transpose
    for i = 0 to n-1:
        for j = i+1 to n-1:
            swap(matrix[i][j], matrix[j][i])

    // Step 2: Reverse each row
    for i = 0 to n-1:
        left = 0, right = n - 1
        while left < right:
            swap(matrix[i][left], matrix[i][right])
            left++, right--
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Transpose: n*(n-1)/2 swaps. Reverse: n*n/2 swaps. Total O(n^2). |
| **Space** | O(1) | All swaps done in-place — only a temp variable needed. |

### Implementation

#### Go

```go
// rotate — Transpose + Reverse Rows approach
// Time: O(n^2), Space: O(1)
func rotate(matrix [][]int) {
    n := len(matrix)

    // Step 1: Transpose the matrix
    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
        }
    }

    // Step 2: Reverse each row
    for i := 0; i < n; i++ {
        left, right := 0, n-1
        for left < right {
            matrix[i][left], matrix[i][right] = matrix[i][right], matrix[i][left]
            left++
            right--
        }
    }
}
```

#### Java

```java
class Solution {
    // rotate — Transpose + Reverse Rows approach
    // Time: O(n^2), Space: O(1)
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Step 1: Transpose the matrix
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Step 2: Reverse each row
        for (int i = 0; i < n; i++) {
            int left = 0, right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }
}
```

#### Python

```python
class Solution:
    def rotate(self, matrix: list[list[int]]) -> None:
        """
        Transpose + Reverse Rows approach
        Time: O(n^2), Space: O(1)
        """
        n = len(matrix)

        # Step 1: Transpose the matrix
        for i in range(n):
            for j in range(i + 1, n):
                matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]

        # Step 2: Reverse each row
        for i in range(n):
            matrix[i].reverse()
```

### Dry Run

```text
Input: matrix = [[1, 2, 3],
                 [4, 5, 6],
                 [7, 8, 9]]

Step 1: Transpose (swap upper triangle with lower triangle)
  swap (0,1)↔(1,0): 2↔4 → [[1, 4, 3], [2, 5, 6], [7, 8, 9]]
  swap (0,2)↔(2,0): 3↔7 → [[1, 4, 7], [2, 5, 6], [3, 8, 9]]
  swap (1,2)↔(2,1): 6↔8 → [[1, 4, 7], [2, 5, 8], [3, 6, 9]]

After transpose:
  1  4  7
  2  5  8
  3  6  9

Step 2: Reverse each row
  Row 0: [1, 4, 7] → [7, 4, 1]
  Row 1: [2, 5, 8] → [8, 5, 2]
  Row 2: [3, 6, 9] → [9, 6, 3]

Final result:
  7  4  1
  8  5  2
  9  6  3   ✓ Correct!
```

---

## Approach 2: Rotate Four Cells at a Time

### Thought process

> Each element in the matrix is part of a cycle of 4 positions during rotation.
> Instead of decomposing into transpose + reverse, we can directly perform
> the 4-way swap for each cycle.
>
> For each position `(i, j)` in the top-left quadrant, the four positions
> involved in the cycle are:
> - `(i, j)` — top
> - `(j, n-1-i)` — right
> - `(n-1-i, n-1-j)` — bottom
> - `(n-1-j, i)` — left

### Algorithm (step-by-step)

1. Process layer by layer, from the outermost to the innermost
2. For each layer `i` (from `0` to `n/2 - 1`):
   a. For each position `j` in the layer (from `i` to `n - 1 - i - 1`):
      - Save `top = matrix[i][j]`
      - Move left to top: `matrix[i][j] = matrix[n-1-j][i]`
      - Move bottom to left: `matrix[n-1-j][i] = matrix[n-1-i][n-1-j]`
      - Move right to bottom: `matrix[n-1-i][n-1-j] = matrix[j][n-1-i]`
      - Move saved top to right: `matrix[j][n-1-i] = top`

### Pseudocode

```text
function rotate(matrix):
    n = matrix.length

    for i = 0 to n/2 - 1:
        for j = i to n - 1 - i - 1:
            // Save top
            temp = matrix[i][j]

            // Left → Top
            matrix[i][j] = matrix[n-1-j][i]

            // Bottom → Left
            matrix[n-1-j][i] = matrix[n-1-i][n-1-j]

            // Right → Bottom
            matrix[n-1-i][n-1-j] = matrix[j][n-1-i]

            // Top → Right
            matrix[j][n-1-i] = temp
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Each cell is moved exactly once. Total n^2 cells (minus center for odd n). |
| **Space** | O(1) | Only one temp variable for the 4-way swap. |

### Implementation

#### Go

```go
// rotate — Rotate Four Cells at a Time approach
// Time: O(n^2), Space: O(1)
func rotate(matrix [][]int) {
    n := len(matrix)

    // Process layer by layer
    for i := 0; i < n/2; i++ {
        for j := i; j < n-1-i; j++ {
            // Save top
            temp := matrix[i][j]

            // Left → Top
            matrix[i][j] = matrix[n-1-j][i]

            // Bottom → Left
            matrix[n-1-j][i] = matrix[n-1-i][n-1-j]

            // Right → Bottom
            matrix[n-1-i][n-1-j] = matrix[j][n-1-i]

            // Top → Right
            matrix[j][n-1-i] = temp
        }
    }
}
```

#### Java

```java
class Solution {
    // rotate — Rotate Four Cells at a Time approach
    // Time: O(n^2), Space: O(1)
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Process layer by layer
        for (int i = 0; i < n / 2; i++) {
            for (int j = i; j < n - 1 - i; j++) {
                // Save top
                int temp = matrix[i][j];

                // Left → Top
                matrix[i][j] = matrix[n - 1 - j][i];

                // Bottom → Left
                matrix[n - 1 - j][i] = matrix[n - 1 - i][n - 1 - j];

                // Right → Bottom
                matrix[n - 1 - i][n - 1 - j] = matrix[j][n - 1 - i];

                // Top → Right
                matrix[j][n - 1 - i] = temp;
            }
        }
    }
}
```

#### Python

```python
class Solution:
    def rotate(self, matrix: list[list[int]]) -> None:
        """
        Rotate Four Cells at a Time approach
        Time: O(n^2), Space: O(1)
        """
        n = len(matrix)

        # Process layer by layer
        for i in range(n // 2):
            for j in range(i, n - 1 - i):
                # Save top
                temp = matrix[i][j]

                # Left → Top
                matrix[i][j] = matrix[n - 1 - j][i]

                # Bottom → Left
                matrix[n - 1 - j][i] = matrix[n - 1 - i][n - 1 - j]

                # Right → Bottom
                matrix[n - 1 - i][n - 1 - j] = matrix[j][n - 1 - i]

                # Top → Right
                matrix[j][n - 1 - i] = temp
```

### Dry Run

```text
Input: matrix = [[1, 2, 3],
                 [4, 5, 6],
                 [7, 8, 9]]
n = 3

Layer i=0 (outermost):
  j=0: cycle (0,0)→(0,2)→(2,2)→(2,0)
    temp = 1
    (0,0) = matrix[2][0] = 7    → [[7, 2, 3], [4, 5, 6], [_, 8, 9]]
    (2,0) = matrix[2][2] = 9    → [[7, 2, 3], [4, 5, 6], [9, 8, _]]
    (2,2) = matrix[0][2] = 3    → [[7, 2, _], [4, 5, 6], [9, 8, 3]]
    (0,2) = temp = 1            → [[7, 2, 1], [4, 5, 6], [9, 8, 3]]

  j=1: cycle (0,1)→(1,2)→(2,1)→(1,0)
    temp = 2
    (0,1) = matrix[1][0] = 4    → [[7, 4, 1], [_, 5, 6], [9, 8, 3]]
    (1,0) = matrix[2][1] = 8    → [[7, 4, 1], [8, 5, _], [9, _, 3]]
    (2,1) = matrix[1][2] = 6    → [[7, 4, 1], [8, 5, _], [9, 6, 3]]
    (1,2) = temp = 2            → [[7, 4, 1], [8, 5, 2], [9, 6, 3]]

No inner layers (n/2 = 1, center element 5 stays).

Final result:
  7  4  1
  8  5  2
  9  6  3   ✓ Correct!
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Transpose + Reverse | O(n^2) | O(1) | Intuitive, easy to remember | Two passes over the matrix |
| 2 | Four Cells at a Time | O(n^2) | O(1) | Single pass, elegant cycles | Index math is tricky |

### Which solution to choose?

- **In an interview:** Approach 1 (Transpose + Reverse) — easier to explain and implement correctly
- **In production:** Both are equivalent — same time and space complexity
- **On Leetcode:** Both pass — choose whichever you're more comfortable with
- **For learning:** Both — Approach 1 to learn matrix transpose, Approach 2 to learn cyclic rotation

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | 1x1 matrix | `[[1]]` | `[[1]]` | Single element — already rotated |
| 2 | 2x2 matrix | `[[1,2],[3,4]]` | `[[3,1],[4,2]]` | Smallest non-trivial case |
| 3 | Negative values | `[[-1,-2],[-3,-4]]` | `[[-3,-1],[-4,-2]]` | Sign doesn't affect rotation |
| 4 | All same values | `[[5,5],[5,5]]` | `[[5,5],[5,5]]` | Rotation has no visible effect |
| 5 | Odd dimension | `[[1,2,3],[4,5,6],[7,8,9]]` | `[[7,4,1],[8,5,2],[9,6,3]]` | Center element stays in place |

---

## Common Mistakes

### Mistake 1: Transposing the full matrix instead of upper triangle

```python
# WRONG — swaps each pair twice, resulting in no change
for i in range(n):
    for j in range(n):
        matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]

# CORRECT — only swap upper triangle (j > i)
for i in range(n):
    for j in range(i + 1, n):
        matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
```

**Reason:** Swapping `(i,j)` and `(j,i)` twice undoes the swap. Only iterate where `j > i`.

### Mistake 2: Wrong cycle direction in 4-way swap

```python
# WRONG — rotates counter-clockwise
matrix[i][j] = matrix[j][n-1-i]

# CORRECT — rotates clockwise
matrix[i][j] = matrix[n-1-j][i]
```

**Reason:** The direction of the assignment matters. For clockwise, each cell receives the value from the cell that will rotate INTO it.

### Mistake 3: Allocating a new matrix

```python
# WRONG — not in-place
result = [[0] * n for _ in range(n)]
for i in range(n):
    for j in range(n):
        result[j][n-1-i] = matrix[i][j]
matrix = result  # doesn't modify the original!

# CORRECT — modify in-place using transpose + reverse
for i in range(n):
    for j in range(i + 1, n):
        matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
for row in matrix:
    row.reverse()
```

**Reason:** The problem requires in-place rotation. Reassigning `matrix` doesn't modify the original reference.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [54. Spiral Matrix](https://leetcode.com/problems/spiral-matrix/) | :yellow_circle: Medium | Matrix traversal by layers |
| 2 | [59. Spiral Matrix II](https://leetcode.com/problems/spiral-matrix-ii/) | :yellow_circle: Medium | Layer-by-layer matrix filling |
| 3 | [73. Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes/) | :yellow_circle: Medium | In-place matrix modification |
| 4 | [867. Transpose Matrix](https://leetcode.com/problems/transpose-matrix/) | :green_circle: Easy | Matrix transpose operation |
| 5 | [1886. Determine Whether Matrix Can Be Obtained By Rotation](https://leetcode.com/problems/determine-whether-matrix-can-be-obtained-by-rotation/) | :green_circle: Easy | Matrix rotation check |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Transpose + Reverse** tab — Step 1: transpose the matrix, Step 2: reverse each row
> - **Four-way Swap** tab — rotate 4 cells at a time, layer by layer
> - Color-coded cells to track movements
> - Custom input and preset examples
