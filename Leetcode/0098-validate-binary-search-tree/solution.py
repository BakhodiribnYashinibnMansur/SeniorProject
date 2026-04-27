from typing import Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val; self.left = left; self.right = right


class Solution:
    def isValidBST(self, root: Optional[TreeNode]) -> bool:
        """Time O(n), Space O(h)."""
        def dfs(n, lo, hi):
            if not n: return True
            if n.val <= lo or n.val >= hi: return False
            return dfs(n.left, lo, n.val) and dfs(n.right, n.val, hi)
        return dfs(root, float('-inf'), float('inf'))


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


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    cases = [
        ("Valid 1", [2, 1, 3], True),
        ("Invalid 1", [5, 1, 4, None, None, 3, 6], False),
        ("Single", [1], True),
        ("Empty", [], True),
        ("Equal not allowed", [1, 1], False),
        ("Deep valid", [4, 2, 6, 1, 3, 5, 7], True),
        ("Deep invalid", [10, 5, 15, None, None, 6, 20], False),
    ]
    for n, t, exp in cases:
        got = sol.isValidBST(build_tree(t))
        if got == exp: print(f"PASS: {n}"); passed += 1
        else: print(f"FAIL: {n} got={got}"); failed += 1
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
