# 0096. Unique Binary Search Trees

## Problem

| | |
|---|---|
| **Leetcode** | [96. Unique Binary Search Trees](https://leetcode.com/problems/unique-binary-search-trees/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Math`, `Dynamic Programming`, `Tree`, `Binary Search Tree` |

> Given an integer `n`, return *the number of structurally unique **BST**'s (binary search trees) which has exactly* `n` *nodes of unique values from* `1` *to* `n`.

### Examples

```
Input: n = 3
Output: 5

Input: n = 1
Output: 1
```

### Constraints

- `1 <= n <= 19`

---

## Approach: Catalan via DP

Let `G(n)` = number of unique BSTs with `n` nodes. With root `i`:
- Left subtree has `i-1` nodes → `G(i-1)` shapes.
- Right subtree has `n-i` nodes → `G(n-i)` shapes.

Recurrence: `G(n) = sum_{i=1..n} G(i-1) * G(n-i)`. Base: `G(0) = G(1) = 1`.

### Complexity

- Time: O(n^2)
- Space: O(n)

### Implementation

#### Go

```go
func numTrees(n int) int {
    g := make([]int, n+1)
    g[0], g[1] = 1, 1
    for i := 2; i <= n; i++ {
        for j := 0; j < i; j++ {
            g[i] += g[j] * g[i-1-j]
        }
    }
    return g[n]
}
```

#### Java

```java
class Solution {
    public int numTrees(int n) {
        int[] g = new int[n + 1];
        g[0] = 1;
        if (n >= 1) g[1] = 1;
        for (int i = 2; i <= n; i++)
            for (int j = 0; j < i; j++) g[i] += g[j] * g[i - 1 - j];
        return g[n];
    }
}
```

#### Python

```python
class Solution:
    def numTrees(self, n: int) -> int:
        g = [0] * (n + 1)
        g[0] = 1
        if n >= 1: g[1] = 1
        for i in range(2, n + 1):
            for j in range(i):
                g[i] += g[j] * g[i - 1 - j]
        return g[n]
```

---

## Visual Animation

> [animation.html](./animation.html)
