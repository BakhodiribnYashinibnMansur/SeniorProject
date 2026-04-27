# 0095. Unique Binary Search Trees II

## Problem

| | |
|---|---|
| **Leetcode** | [95. Unique Binary Search Trees II](https://leetcode.com/problems/unique-binary-search-trees-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Dynamic Programming`, `Backtracking`, `Tree`, `Binary Search Tree` |

> Given an integer `n`, return *all the structurally unique BST's (binary search trees), which has exactly* `n` *nodes of unique values from* `1` *to* `n`.

### Examples

```
Input: n = 3
Output: [[1,null,2,null,3],[1,null,3,2],[2,1,3],[3,1,null,null,2],[3,2,null,1]]

Input: n = 1
Output: [[1]]
```

### Constraints

- `1 <= n <= 8`

---

## Approach: Recursion (Pick Root, Recurse on Subtrees)

For each `i in [start..end]` as root:
- Recurse on `[start..i-1]` for left subtrees.
- Recurse on `[i+1..end]` for right subtrees.
- Combine each pair as a new tree.

### Complexity

Catalan number — about `O(4^n / n^{1.5})`.

### Implementation

#### Python

```python
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val; self.left = left; self.right = right


class Solution:
    def generateTrees(self, n: int) -> List[Optional[TreeNode]]:
        def gen(lo, hi):
            if lo > hi: return [None]
            result = []
            for root in range(lo, hi + 1):
                for L in gen(lo, root - 1):
                    for R in gen(root + 1, hi):
                        result.append(TreeNode(root, L, R))
            return result
        return gen(1, n) if n > 0 else []
```

#### Go

```go
type TreeNode struct {
    Val int
    Left, Right *TreeNode
}

func generateTrees(n int) []*TreeNode {
    if n == 0 {
        return []*TreeNode{}
    }
    var gen func(lo, hi int) []*TreeNode
    gen = func(lo, hi int) []*TreeNode {
        if lo > hi {
            return []*TreeNode{nil}
        }
        result := []*TreeNode{}
        for root := lo; root <= hi; root++ {
            for _, l := range gen(lo, root-1) {
                for _, r := range gen(root+1, hi) {
                    result = append(result, &TreeNode{Val: root, Left: l, Right: r})
                }
            }
        }
        return result
    }
    return gen(1, n)
}
```

#### Java

```java
class TreeNode {
    int val; TreeNode left, right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
}

class Solution {
    public List<TreeNode> generateTrees(int n) {
        if (n == 0) return new ArrayList<>();
        return gen(1, n);
    }
    private List<TreeNode> gen(int lo, int hi) {
        List<TreeNode> result = new ArrayList<>();
        if (lo > hi) { result.add(null); return result; }
        for (int root = lo; root <= hi; root++) {
            for (TreeNode l : gen(lo, root - 1)) {
                for (TreeNode r : gen(root + 1, hi)) {
                    result.add(new TreeNode(root, l, r));
                }
            }
        }
        return result;
    }
}
```

---

## Visual Animation

> [animation.html](./animation.html)
