# 0099. Recover Binary Search Tree

## Problem

| | |
|---|---|
| **Leetcode** | [99. Recover Binary Search Tree](https://leetcode.com/problems/recover-binary-search-tree/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Tree`, `DFS`, `BST` |

> You are given the `root` of a binary search tree (BST), where the values of **exactly two** nodes of the tree were swapped by mistake. *Recover the tree without changing its structure*.

### Examples

```
Input: root = [1,3,null,null,2]
Output: [3,1,null,null,2]

Input: root = [3,1,4,null,null,2]
Output: [2,1,4,null,null,3]
```

---

## Approach: Inorder Traversal — Find Two Misordered Nodes

A correct BST inorder is strictly increasing. Walking inorder, find pairs `(prev, cur)` with `prev.val > cur.val`. The first such pair gives the bigger misplaced node (`prev`); the second pair (if any) gives the smaller (`cur`); if there is only one violation, the smaller is the `cur` of that pair.

### Complexity

- Time: O(n)
- Space: O(h)

### Implementation

#### Go

```go
type TreeNode struct {
    Val int
    Left, Right *TreeNode
}

func recoverTree(root *TreeNode) {
    var first, second, prev *TreeNode
    var inorder func(n *TreeNode)
    inorder = func(n *TreeNode) {
        if n == nil { return }
        inorder(n.Left)
        if prev != nil && prev.Val > n.Val {
            if first == nil { first = prev }
            second = n
        }
        prev = n
        inorder(n.Right)
    }
    inorder(root)
    first.Val, second.Val = second.Val, first.Val
}
```

#### Java

```java
class Solution {
    private TreeNode first, second, prev;
    public void recoverTree(TreeNode root) {
        inorder(root);
        int t = first.val; first.val = second.val; second.val = t;
    }
    private void inorder(TreeNode n) {
        if (n == null) return;
        inorder(n.left);
        if (prev != null && prev.val > n.val) {
            if (first == null) first = prev;
            second = n;
        }
        prev = n;
        inorder(n.right);
    }
}
```

#### Python

```python
class Solution:
    def recoverTree(self, root):
        first = second = prev = None
        def inorder(n):
            nonlocal first, second, prev
            if not n: return
            inorder(n.left)
            if prev and prev.val > n.val:
                if not first: first = prev
                second = n
            prev = n
            inorder(n.right)
        inorder(root)
        first.val, second.val = second.val, first.val
```

---

## Visual Animation

> [animation.html](./animation.html)
