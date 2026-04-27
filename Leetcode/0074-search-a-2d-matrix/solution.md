# 0074. Search a 2D Matrix

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Linear Scan](#approach-1-linear-scan)
4. [Approach 2: Two Binary Searches](#approach-2-two-binary-searches)
5. [Approach 3: Single Binary Search (Treat as 1D)](#approach-3-single-binary-search-treat-as-1d)
6. [Approach 4: Staircase Search (from Top-Right)](#approach-4-staircase-search-from-top-right)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [74. Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Binary Search`, `Matrix` |

### Description

> You are given an `m x n` integer matrix `matrix` with the following two properties:
>
> 1. Each row is sorted in non-decreasing order.
> 2. The first integer of each row is greater than the last integer of the previous row.
>
> Given an integer `target`, return `true` *if `target` is in `matrix` or `false` otherwise*.
>
> You must write a solution in O(log(m * n)) time complexity.

### Examples

```
Example 1:
Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
Output: true

Example 2:
Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 13
Output: false
```

### Constraints

- `m == matrix.length`
- `n == matrix[i].length`
- `1 <= m, n <= 100`
- `-10^4 <= matrix[i][j], target <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Determine whether `target` exists in a matrix that is globally sorted: traversing rows top-to-bottom, then left-to-right, yields a sorted sequence.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `matrix` | `int[m][n]` | Sorted as described |
| `target` | `int` | Value to search |

### 3. What is the output?

A boolean.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `m, n <= 100` | At most 10,000 cells |
| O(log(mn)) required | Binary search |

### 5. Step-by-step example analysis

#### Example 1: target = 3

```text
Treat matrix as a flattened sorted array of 12 elements.
Index 0..11 → row = idx / n, col = idx % n.

Binary search:
  lo=0, hi=11, mid=5 → row=1, col=1 → 11 → 11 > 3 → hi=4
  lo=0, hi=4, mid=2 → row=0, col=2 → 5 → 5 > 3 → hi=1
  lo=0, hi=1, mid=0 → 1 → 1 < 3 → lo=1
  lo=1, hi=1, mid=1 → 3 → match → return true
```

### 6. Key Observations

1. **Globally sorted** -- Treat the matrix as a 1D sorted array of length `m*n` and binary search.
2. **Two-step search** -- Binary search rows (via first column), then binary search within the row.
3. **Staircase from top-right** -- A different but elegant O(m + n) walk.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Binary Search on flattened index | Globally sorted array |
| Two-Phase Binary Search | Independent row + column searches |
| Staircase Walk | Works on any monotone matrix (not just this one) |

**Chosen pattern:** `Single Binary Search (Treat as 1D)`.

---

## Approach 1: Linear Scan

### Idea

> Scan all `m * n` cells. Trivially correct but O(mn).

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(1) |

> Listed only as a baseline.

---

## Approach 2: Two Binary Searches

### Idea

> First binary-search the column of first elements to find which row could contain `target`. Then binary-search within that row.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(log m + log n) = O(log(m * n)) |
| **Space** | O(1) |

### Implementation

#### Python

```python
class Solution:
    def searchMatrixTwo(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        # Find candidate row
        lo, hi = 0, m - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if matrix[mid][0] <= target <= matrix[mid][n - 1]:
                # Search this row
                l, r = 0, n - 1
                while l <= r:
                    m2 = (l + r) // 2
                    if matrix[mid][m2] == target: return True
                    if matrix[mid][m2] < target: l = m2 + 1
                    else: r = m2 - 1
                return False
            if matrix[mid][0] > target: hi = mid - 1
            else: lo = mid + 1
        return False
```

---

## Approach 3: Single Binary Search (Treat as 1D)

### Algorithm (step-by-step)

1. `lo = 0`, `hi = m * n - 1`.
2. While `lo <= hi`:
   - `mid = (lo + hi) // 2`. `r = mid // n`, `c = mid % n`.
   - If `matrix[r][c] == target`: return true.
   - If less: `lo = mid + 1`. Else `hi = mid - 1`.
3. Return false.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(log(m * n)) |
| **Space** | O(1) |

### Implementation

#### Go

```go
func searchMatrix(matrix [][]int, target int) bool {
    m, n := len(matrix), len(matrix[0])
    lo, hi := 0, m*n-1
    for lo <= hi {
        mid := (lo + hi) / 2
        r, c := mid/n, mid%n
        v := matrix[r][c]
        if v == target {
            return true
        }
        if v < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return false
}
```

#### Java

```java
class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int lo = 0, hi = m * n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int v = matrix[mid / n][mid % n];
            if (v == target) return true;
            if (v < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return false;
    }
}
```

#### Python

```python
class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        lo, hi = 0, m * n - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            v = matrix[mid // n][mid % n]
            if v == target: return True
            if v < target: lo = mid + 1
            else: hi = mid - 1
        return False
```

---

## Approach 4: Staircase Search (from Top-Right)

### Idea

> Start at the top-right corner. If the cell is the target → done. If less → move down (target is bigger). If more → move left (target is smaller). Each step eliminates a row or a column.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m + n) |
| **Space** | O(1) |

> Slower than binary search for this strict matrix, but works on the related problem [240. Search a 2D Matrix II](../). Here it makes a fun comparison.

### Implementation

#### Python

```python
class Solution:
    def searchMatrixStaircase(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        r, c = 0, n - 1
        while r < m and c >= 0:
            v = matrix[r][c]
            if v == target: return True
            if v < target: r += 1
            else: c -= 1
        return False
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Linear | O(mn) | O(1) | Trivial | Slow |
| 2 | Two Binary Searches | O(log m + log n) | O(1) | Educational | Slightly more code |
| 3 | Single Binary Search | O(log(mn)) | O(1) | Cleanest, optimal | -- |
| 4 | Staircase | O(m + n) | O(1) | Works on weaker preconditions | Not log time |

### Which solution to choose?

- **In an interview:** Approach 3 (single binary search)
- **In production:** Approach 3
- **On Leetcode:** Approach 3

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | Target less than min | Stay false |
| 2 | Target greater than max | Stay false |
| 3 | Target equals min | Found at (0, 0) |
| 4 | Target equals max | Found at (m-1, n-1) |
| 5 | 1x1 matrix | Compare once |
| 6 | Single row | Reduces to 1D binary search |
| 7 | Single column | Same |
| 8 | Repeated values | Equality stops the search |

---

## Common Mistakes

### Mistake 1: Wrong index conversion

```python
# WRONG — divides by m instead of n
r, c = mid // m, mid % m

# CORRECT — n columns per row
r, c = mid // n, mid % n
```

**Reason:** The flat index goes `r * n + c`, so dividing by `n` gives the row.

### Mistake 2: Off-by-one in `hi`

```python
# WRONG — uses len(matrix) * len(matrix[0]) (one past last)
hi = m * n   # walks off the end

# CORRECT
hi = m * n - 1
```

**Reason:** Inclusive upper bound matches the standard binary search template.

### Mistake 3: Searching the wrong row

```python
# WRONG — picks row by binary searching on the first column,
#         but target could equal matrix[mid][0]
if matrix[mid][0] > target: hi = mid - 1
else: lo = mid + 1
# When loop ends, hi might be the desired row but we haven't searched it.
```

**Reason:** Easier to use Approach 3 (single search) and avoid edge issues.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [240. Search a 2D Matrix II](https://leetcode.com/problems/search-a-2d-matrix-ii/) | :yellow_circle: Medium | Weaker sortedness, staircase shines |
| 2 | [4. Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/) | :red_circle: Hard | Binary search on sorted structure |
| 3 | [704. Binary Search](https://leetcode.com/problems/binary-search/) | :green_circle: Easy | 1D binary search practice |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Matrix view with the binary-search range highlighted
> - Live `mid` cell in the matrix and its flat index
> - Toggle to staircase walk
