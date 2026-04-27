from typing import Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val; self.left = left; self.right = right


class Solution:
    def isSameTree(self, p: Optional[TreeNode], q: Optional[TreeNode]) -> bool:
        """Time O(min(|p|, |q|)), Space O(h)."""
        if not p and not q: return True
        if not p or not q: return False
        if p.val != q.val: return False
        return self.isSameTree(p.left, q.left) and self.isSameTree(p.right, q.right)


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
        ("Example 1", [1, 2, 3], [1, 2, 3], True),
        ("Example 2", [1, 2], [1, None, 2], False),
        ("Both empty", [], [], True),
        ("One empty", [], [1], False),
        ("Different values", [1, 2, 1], [1, 1, 2], False),
        ("Single same", [5], [5], True),
        ("Single different", [5], [6], False),
    ]
    for n, p, q, exp in cases:
        got = sol.isSameTree(build_tree(p), build_tree(q))
        if got == exp: print(f"PASS: {n}"); passed += 1
        else: print(f"FAIL: {n} got={got}"); failed += 1
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
