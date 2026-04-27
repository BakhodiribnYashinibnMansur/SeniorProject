# 0094. Binary Tree Inorder Traversal

## Problem

| | |
|---|---|
| **Leetcode** | [94. Binary Tree Inorder Traversal](https://leetcode.com/problems/binary-tree-inorder-traversal/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Stack`, `Tree`, `Depth-First Search`, `Binary Tree` |

> Given the `root` of a binary tree, return *the inorder traversal of its nodes' values*.

### Examples

```
Input: root = [1,null,2,3]
Output: [1,3,2]

Input: root = []
Output: []

Input: root = [1]
Output: [1]
```

### Constraints

- The number of nodes is `[0, 100]`.
- `-100 <= Node.val <= 100`.

---

## Approach 1: Recursion

```text
inorder(root):
    if root: inorder(root.left); emit root.val; inorder(root.right)
```

## Approach 2: Iterative with Stack

Walk down the leftmost path pushing each node, pop and emit, then move to right child.

### Complexity

- Time: O(n)
- Space: O(h) where h is tree height

### Implementation

#### Go

```go
type TreeNode struct {
    Val int
    Left, Right *TreeNode
}

func inorderTraversal(root *TreeNode) []int {
    result := []int{}
    stack := []*TreeNode{}
    cur := root
    for cur != nil || len(stack) > 0 {
        for cur != nil {
            stack = append(stack, cur)
            cur = cur.Left
        }
        cur = stack[len(stack)-1]
        stack = stack[:len(stack)-1]
        result = append(result, cur.Val)
        cur = cur.Right
    }
    return result
}
```

#### Java

```java
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val; this.left = left; this.right = right;
    }
}

class Solution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            result.add(cur.val);
            cur = cur.right;
        }
        return result;
    }
}
```

#### Python

```python
class Solution:
    def inorderTraversal(self, root):
        result, stack, cur = [], [], root
        while cur or stack:
            while cur:
                stack.append(cur)
                cur = cur.left
            cur = stack.pop()
            result.append(cur.val)
            cur = cur.right
        return result
```

---

## Visual Animation

> [animation.html](./animation.html)
