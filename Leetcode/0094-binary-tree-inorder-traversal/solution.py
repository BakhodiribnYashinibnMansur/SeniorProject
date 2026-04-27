from typing import Optional, List


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    def inorderTraversal(self, root: Optional[TreeNode]) -> List[int]:
        """Time O(n), Space O(h)."""
        result, stack, cur = [], [], root
        while cur or stack:
            while cur:
                stack.append(cur)
                cur = cur.left
            cur = stack.pop()
            result.append(cur.val)
            cur = cur.right
        return result


def build_tree(arr):
    if not arr or arr[0] is None: return None
    root = TreeNode(arr[0])
    q = [root]; i = 1
    while q and i < len(arr):
        node = q.pop(0)
        if i < len(arr) and arr[i] is not None:
            node.left = TreeNode(arr[i]); q.append(node.left)
        i += 1
        if i < len(arr) and arr[i] is not None:
            node.right = TreeNode(arr[i]); q.append(node.right)
        i += 1
    return root


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, exp):
        global passed, failed
        if got == exp: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1
    cases = [
        ("Example 1", [1, None, 2, 3], [1, 3, 2]),
        ("Empty", [], []),
        ("Single", [1], [1]),
        ("Left only", [1, 2, None, 3], [3, 2, 1]),
        ("Right only", [1, None, 2, None, 3], [1, 2, 3]),
        ("Balanced", [1, 2, 3], [2, 1, 3]),
    ]
    for n, t, exp in cases:
        test(n, sol.inorderTraversal(build_tree(t)), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
