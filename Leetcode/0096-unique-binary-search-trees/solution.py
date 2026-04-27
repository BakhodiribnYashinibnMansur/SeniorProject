class Solution:
    def numTrees(self, n: int) -> int:
        """Time O(n^2), Space O(n)."""
        g = [0] * (n + 1)
        g[0] = 1
        if n >= 1: g[1] = 1
        for i in range(2, n + 1):
            for j in range(i):
                g[i] += g[j] * g[i - 1 - j]
        return g[n]


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    cases = [(1, 1), (2, 2), (3, 5), (4, 14), (5, 42), (6, 132), (7, 429), (19, 1767263190)]
    for n, exp in cases:
        got = sol.numTrees(n)
        if got == exp: print(f"PASS: n={n}"); passed += 1
        else: print(f"FAIL: n={n} got={got}"); failed += 1
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
