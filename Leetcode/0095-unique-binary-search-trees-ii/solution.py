from typing import List, Optional


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    def generateTrees(self, n: int) -> List[Optional[TreeNode]]:
        if n == 0: return []
        def gen(lo, hi):
            if lo > hi: return [None]
            result = []
            for root in range(lo, hi + 1):
                for L in gen(lo, root - 1):
                    for R in gen(root + 1, hi):
                        result.append(TreeNode(root, L, R))
            return result
        return gen(1, n)


def catalan(n):
    if n <= 1: return 1
    c = [1, 1]
    for i in range(2, n + 1):
        c.append(sum(c[j] * c[i - 1 - j] for j in range(i)))
    return c[n]


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    for n in range(0, 7):
        got = len(sol.generateTrees(n))
        want = 0 if n == 0 else catalan(n)
        if got == want: print(f"PASS: n={n} → {got}"); passed += 1
        else: print(f"FAIL: n={n} got={got} want={want}"); failed += 1
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
