from typing import Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val; self.left = left; self.right = right


class Solution:
    def recoverTree(self, root: Optional[TreeNode]) -> None:
        """Time O(n), Space O(h)."""
        self.first = self.second = self.prev = None
        def inorder(n):
            if not n: return
            inorder(n.left)
            if self.prev and self.prev.val > n.val:
                if not self.first: self.first = self.prev
                self.second = n
            self.prev = n
            inorder(n.right)
        inorder(root)
        self.first.val, self.second.val = self.second.val, self.first.val


def build_tree(arr):
    if not arr or arr[0] is None: return None
    root = TreeNode(arr[0])
    q = [root]; i = 1
    while q and i < len(arr):
        n = q.pop(0)
        if i < len(arr) and arr[i] is not None: n.left = TreeNode(arr[i]); q.append(n.left)
        i += 1
        if i < len(arr) and arr[i] is not None: n.right = TreeNode(arr[i]); q.append(n.right)
        i += 1
    return root


def inorder(n):
    if not n: return []
    return inorder(n.left) + [n.val] + inorder(n.right)


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    cases = [
        ("Example 1", [1, 3, None, None, 2]),
        ("Example 2", [3, 1, 4, None, None, 2]),
        ("Adjacent swap", [1, 2]),
    ]
    for name, t in cases:
        root = build_tree(t)
        Solution().recoverTree(root)
        r = inorder(root)
        if all(r[i] >= r[i - 1] for i in range(1, len(r))):
            print(f"PASS: {name} → {r}"); passed += 1
        else:
            print(f"FAIL: {name} → {r}"); failed += 1
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
