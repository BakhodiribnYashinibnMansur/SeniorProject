# 0084. Largest Rectangle in Histogram

## Problem

| | |
|---|---|
| **Leetcode** | [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Array`, `Stack`, `Monotonic Stack` |

> Given an array of integers `heights` representing the histogram's bar height where the width of each bar is `1`, return *the area of the largest rectangle in the histogram*.

### Examples

```
Input: heights = [2,1,5,6,2,3]
Output: 10

Input: heights = [2,4]
Output: 4
```

### Constraints

- `1 <= heights.length <= 10^5`
- `0 <= heights[i] <= 10^4`

---

## Approach 1: Brute Force (For Each Pair)

For each pair `(i, j)`, compute the minimum height in `heights[i..j]` and area. O(n^2) or O(n^3).

---

## Approach 2: Monotonic Stack (Optimal)

### Idea

Maintain a stack of indices with strictly increasing heights. When we see a new bar `heights[i]` shorter than the stack top, the bar at the stack top has its right boundary at `i - 1`; pop and compute its area. The left boundary is the new stack top + 1 (or 0 if empty).

### Algorithm

1. Push a sentinel index `n` with height `0` to flush the stack at the end.
2. Iterate `i = 0..n`:
   - While stack non-empty and `heights[i] < heights[stack.top]`:
     - Pop `t`. Width = `i - stack.top - 1` (or `i` if stack empty). Area = `heights[t] * width`. Update best.
   - Push `i`.
3. Return best.

### Complexity

- Time: O(n) — each index pushed and popped once
- Space: O(n) — stack

### Implementation

#### Go

```go
func largestRectangleArea(heights []int) int {
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
            area := heights[top] * width
            if area > best {
                best = area
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
    public int largestRectangleArea(int[] heights) {
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
    def largestRectangleArea(self, heights: List[int]) -> int:
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

### Dry Run

```text
heights = [2, 1, 5, 6, 2, 3]
i=0, h=2: stack [0]
i=1, h=1: pop 0 (h=2), width=1, area=2; best=2. stack [1]
i=2, h=5: stack [1, 2]
i=3, h=6: stack [1, 2, 3]
i=4, h=2: pop 3 (h=6), width=4-2-1=1, area=6; best=6.
         pop 2 (h=5), width=4-1-1=2, area=10; best=10. stack [1, 4]
i=5, h=3: stack [1, 4, 5]
i=6 (sentinel), h=0:
  pop 5 (h=3), width=6-4-1=1, area=3; best=10.
  pop 4 (h=2), width=6-1-1=4, area=8; best=10.
  pop 1 (h=1), width=6 (stack empty), area=6; best=10.
Return 10.
```

---

## Edge Cases

- All same height: `n * h`.
- Strictly increasing: largest is the rightmost bar's rectangle if it dominates.
- Single bar: that bar's area.
- Heights of 0: width contribution is 0.

---

## Common Mistakes

- Forgetting the sentinel (causing leftover bars on the stack).
- Wrong width formula when the stack is empty after pop.

---

## Related

- [85. Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle/) — 2D variant using this as a building block.
- [42. Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) — monotonic stack.

---

## Visual Animation

> [animation.html](./animation.html)
