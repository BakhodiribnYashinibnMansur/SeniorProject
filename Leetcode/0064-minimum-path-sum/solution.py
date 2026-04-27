from typing import List
import copy
from functools import lru_cache

# ============================================================
# 0064. Minimum Path Sum
# https://leetcode.com/problems/minimum-path-sum/
# Difficulty: Medium
# Tags: Array, Dynamic Programming, Matrix
# ============================================================


class Solution:
    def minPathSum(self, grid: List[List[int]]) -> int:
        """
        Optimal Solution (1D Bottom-Up DP).
        Time:  O(m * n)
        Space: O(n)
        """
        m, n = len(grid), len(grid[0])
        dp = [0] * n
        dp[0] = grid[0][0]
        for c in range(1, n):
            dp[c] = dp[c - 1] + grid[0][c]
        for r in range(1, m):
            dp[0] += grid[r][0]
            for c in range(1, n):
                dp[c] = grid[r][c] + min(dp[c], dp[c - 1])
        return dp[n - 1]

    def minPathSum2D(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        dp = [[0] * n for _ in range(m)]
        dp[0][0] = grid[0][0]
        for c in range(1, n): dp[0][c] = dp[0][c - 1] + grid[0][c]
        for r in range(1, m): dp[r][0] = dp[r - 1][0] + grid[r][0]
        for r in range(1, m):
            for c in range(1, n):
                dp[r][c] = grid[r][c] + min(dp[r - 1][c], dp[r][c - 1])
        return dp[m - 1][n - 1]

    def minPathSumMemo(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        @lru_cache(maxsize=None)
        def f(r: int, c: int) -> int:
            if r == 0 and c == 0: return grid[0][0]
            if r < 0 or c < 0: return float('inf')
            return grid[r][c] + min(f(r - 1, c), f(r, c - 1))
        return f(m - 1, n - 1)


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}"); passed += 1
        else:
            print(f"FAIL: {name} (got {got}, exp {expected})"); failed += 1

    cases = [
        ("Example 1", [[1,3,1],[1,5,1],[4,2,1]], 7),
        ("Example 2", [[1,2,3],[4,5,6]], 12),
        ("Single cell", [[5]], 5),
        ("Single row", [[1,2,3]], 6),
        ("Single column", [[1],[2],[3]], 6),
        ("All zeros", [[0,0],[0,0]], 0),
        ("All same 3x3", [[5,5,5],[5,5,5],[5,5,5]], 25),
        ("Strong gradient", [[1,100,100],[1,1,1],[100,1,1]], 5),
        ("Larger 4x4", [[1,2,3,4],[2,3,4,5],[3,4,5,6],[4,5,6,7]], 28),
        ("Single zero", [[0]], 0),
    ]

    print("=== 1D DP ===")
    for name, g, exp in cases:
        test(name, sol.minPathSum(copy.deepcopy(g)), exp)
    print("\n=== 2D DP ===")
    for name, g, exp in cases:
        test("2D " + name, sol.minPathSum2D(copy.deepcopy(g)), exp)
    print("\n=== Memoization ===")
    for name, g, exp in cases:
        s = Solution()
        test("Memo " + name, s.minPathSumMemo(copy.deepcopy(g)), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
