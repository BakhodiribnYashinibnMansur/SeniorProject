# 0098. Validate Binary Search Tree

## Problem

| | |
|---|---|
| **Leetcode** | [98. Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Tree`, `DFS`, `BST` |

> Given the `root` of a binary tree, *determine if it is a valid binary search tree (BST)*.

A **valid BST** is defined as:
- Left subtree contains only nodes with keys **less than** the node's key.
- Right subtree contains only nodes with keys **greater than** the node's key.
- Both subtrees must also be BSTs.

### Examples

```
Input: root = [2,1,3]   → true
Input: root = [5,1,4,null,null,3,6]   → false
```

---

## Approach 1: Recursive with Bounds

Pass `lo, hi` bounds; node value must be strictly between.

## Approach 2: Inorder Traversal

A BST inorder produces strictly increasing values. Track previous node.

### Complexity

- Time: O(n); Space: O(h)

### Implementation

#### Go

```go
type TreeNode struct {
    Val int
    Left, Right *TreeNode
}

func isValidBST(root *TreeNode) bool {
    var dfs func(n *TreeNode, lo, hi int64) bool
    dfs = func(n *TreeNode, lo, hi int64) bool {
        if n == nil { return true }
        v := int64(n.Val)
        if v <= lo || v >= hi { return false }
        return dfs(n.Left, lo, v) && dfs(n.Right, v, hi)
    }
    return dfs(root, -1<<62, 1<<62)
}
```

#### Java

```java
class Solution {
    public boolean isValidBST(TreeNode root) {
        return dfs(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    private boolean dfs(TreeNode n, long lo, long hi) {
        if (n == null) return true;
        if (n.val <= lo || n.val >= hi) return false;
        return dfs(n.left, lo, n.val) && dfs(n.right, n.val, hi);
    }
}
```

#### Python

```python
class Solution:
    def isValidBST(self, root):
        def dfs(n, lo, hi):
            if not n: return True
            if n.val <= lo or n.val >= hi: return False
            return dfs(n.left, lo, n.val) and dfs(n.right, n.val, hi)
        return dfs(root, float('-inf'), float('inf'))
```

---

## Visual Animation

> [animation.html](./animation.html)
