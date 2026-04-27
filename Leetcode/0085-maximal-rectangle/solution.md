# 0085. Maximal Rectangle

## Problem

| | |
|---|---|
| **Leetcode** | [85. Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Array`, `Hash Table`, `Dynamic Programming`, `Stack`, `Matrix`, `Monotonic Stack` |

> Given a `rows x cols` binary matrix filled with `0`'s and `1`'s, find the largest rectangle containing only `1`'s and return *its area*.

### Examples

```
Input: matrix = [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
Output: 6

Input: matrix = [["0"]]
Output: 0
```

### Constraints

- `rows == matrix.length`
- `cols == matrix[i].length`
- `1 <= row, cols <= 200`
- `matrix[i][j]` is `'0'` or `'1'`.

---

## Approach: Row-wise Histogram + [Problem 84](../0084-largest-rectangle-in-histogram/solution.md)

### Idea

Treat each row as the "ground" of a histogram where each column's height is the number of consecutive `1`s ending at that row. Apply [Largest Rectangle in Histogram](../0084-largest-rectangle-in-histogram/solution.md) to each row's histogram and track the global maximum.

### Algorithm

1. Initialize `heights[cols] = 0`.
2. For each row:
   - For each column `c`: if `matrix[r][c] == '1'`, `heights[c]++`; else `heights[c] = 0`.
   - Compute largest rectangle in `heights` and update best.
3. Return best.

### Complexity

- Time: O(rows * cols)
- Space: O(cols)

---

## Implementation

#### Go

```go
func maximalRectangle(matrix [][]byte) int {
    if len(matrix) == 0 {
        return 0
    }
    cols := len(matrix[0])
    heights := make([]int, cols)
    best := 0
    for _, row := range matrix {
        for c := 0; c < cols; c++ {
            if row[c] == '1' {
                heights[c]++
            } else {
                heights[c] = 0
            }
        }
        if a := largestRect(heights); a > best {
            best = a
        }
    }
    return best
}

func largestRect(heights []int) int {
    n := len(heights)
    stack := []int{}
    best := 0
    for i := 0; i <= n; i++ {
        var h int
        if i == n {
            h = 0
        } else {
            h = heights[i]
        }
        for len(stack) > 0 && heights[stack[len(stack)-1]] > h {
            top := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            width := i
            if len(stack) > 0 {
                width = i - stack[len(stack)-1] - 1
            }
            if heights[top]*width > best {
                best = heights[top] * width
            }
        }
        stack = append(stack, i)
    }
    return best
}
```

#### Java

```java
class Solution {
    public int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0) return 0;
        int cols = matrix[0].length;
        int[] heights = new int[cols];
        int best = 0;
        for (char[] row : matrix) {
            for (int c = 0; c < cols; c++) {
                heights[c] = row[c] == '1' ? heights[c] + 1 : 0;
            }
            best = Math.max(best, largestRect(heights));
        }
        return best;
    }
    private int largestRect(int[] heights) {
        int n = heights.length, best = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                int top = stack.pop();
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                best = Math.max(best, heights[top] * width);
            }
            stack.push(i);
        }
        return best;
    }
}
```

#### Python

```python
class Solution:
    def maximalRectangle(self, matrix: List[List[str]]) -> int:
        if not matrix: return 0
        cols = len(matrix[0])
        heights = [0] * cols
        best = 0
        for row in matrix:
            for c in range(cols):
                heights[c] = heights[c] + 1 if row[c] == '1' else 0
            best = max(best, self.largestRect(heights))
        return best

    def largestRect(self, heights):
        n = len(heights)
        stack = []
        best = 0
        for i in range(n + 1):
            h = 0 if i == n else heights[i]
            while stack and heights[stack[-1]] > h:
                top = stack.pop()
                width = i if not stack else i - stack[-1] - 1
                best = max(best, heights[top] * width)
            stack.append(i)
        return best
```

---

## Edge Cases

- Empty matrix: 0
- All zeros: 0
- All ones: rows * cols
- Single row: equivalent to histogram of that row
- Single column: max consecutive ones

---

## Visual Animation

> [animation.html](./animation.html)
