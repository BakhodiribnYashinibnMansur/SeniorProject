# 0100. Same Tree

## Problem

| | |
|---|---|
| **Leetcode** | [100. Same Tree](https://leetcode.com/problems/same-tree/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Tree`, `DFS`, `BFS` |

> Given the roots of two binary trees `p` and `q`, write a function to check if they are the same or not.
>
> Two binary trees are considered the same if they are structurally identical, and the nodes have the same value.

### Examples

```
Input: p = [1,2,3], q = [1,2,3]
Output: true

Input: p = [1,2], q = [1,null,2]
Output: false
```

---

## Approach: Recursive Comparison

```text
isSame(p, q):
    if both null: true
    if one null: false
    if values differ: false
    return isSame(p.left, q.left) AND isSame(p.right, q.right)
```

### Complexity

- Time: O(min(|p|, |q|))
- Space: O(h)

### Implementation

#### Go

```go
type TreeNode struct {
    Val int
    Left, Right *TreeNode
}

func isSameTree(p, q *TreeNode) bool {
    if p == nil && q == nil { return true }
    if p == nil || q == nil { return false }
    if p.Val != q.Val { return false }
    return isSameTree(p.Left, q.Left) && isSameTree(p.Right, q.Right)
}
```

#### Java

```java
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        if (p.val != q.val) return false;
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
}
```

#### Python

```python
class Solution:
    def isSameTree(self, p, q):
        if not p and not q: return True
        if not p or not q: return False
        if p.val != q.val: return False
        return self.isSameTree(p.left, q.left) and self.isSameTree(p.right, q.right)
```

---

## Visual Animation

> [animation.html](./animation.html)
