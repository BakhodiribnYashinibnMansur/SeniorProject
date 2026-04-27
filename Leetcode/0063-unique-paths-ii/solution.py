from typing import List
import copy
from functools import lru_cache

# ============================================================
# 0063. Unique Paths II
# https://leetcode.com/problems/unique-paths-ii/
# Difficulty: Medium
# Tags: Array, Dynamic Programming, Matrix
# ============================================================


class Solution:
    def uniquePathsWithObstacles(self, grid: List[List[int]]) -> int:
        """
        Optimal Solution (1D DP).
        Time:  O(m * n)
        Space: O(n)
        """
        m, n = len(grid), len(grid[0])
        if grid[0][0] == 1:
            return 0
        dp = [0] * n
        dp[0] = 1
        for r in range(m):
            for c in range(n):
                if grid[r][c] == 1:
                    dp[c] = 0
                elif c > 0:
                    dp[c] += dp[c - 1]
        return dp[n - 1]

    def uniquePathsWithObstacles2D(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        if grid[0][0] == 1:
            return 0
        dp = [[0] * n for _ in range(m)]
        dp[0][0] = 1
        for c in range(1, n):
            if grid[0][c] == 0:
                dp[0][c] = dp[0][c - 1]
        for r in range(1, m):
            if grid[r][0] == 0:
                dp[r][0] = dp[r - 1][0]
            for c in range(1, n):
                if grid[r][c] == 0:
                    dp[r][c] = dp[r - 1][c] + dp[r][c - 1]
        return dp[m - 1][n - 1]

    def uniquePathsWithObstaclesMemo(self, grid: List[List[int]]) -> int:
        m, n = len(grid), len(grid[0])
        @lru_cache(maxsize=None)
        def f(r: int, c: int) -> int:
            if r < 0 or c < 0 or grid[r][c] == 1: return 0
            if r == 0 and c == 0: return 1
            return f(r - 1, c) + f(r, c - 1)
        return f(m - 1, n - 1)


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}")
            passed += 1
        else:
            print(f"FAIL: {name} (got {got}, exp {expected})")
            failed += 1

    cases = [
        ("Example 1", [[0,0,0],[0,1,0],[0,0,0]], 2),
        ("Example 2", [[0,1],[0,0]], 1),
        ("Start blocked", [[1,0],[0,0]], 0),
        ("End blocked", [[0,0],[0,1]], 0),
        ("1x1 free", [[0]], 1),
        ("1x1 blocked", [[1]], 0),
        ("All free 3x3", [[0,0,0],[0,0,0],[0,0,0]], 6),
        ("First row obstacle", [[0,1,0],[0,0,0],[0,0,0]], 3),
        ("All vertical block", [[0],[1],[0]], 0),
        ("Diagonal blocked", [[0,0,0],[1,1,0],[0,0,0]], 1),
    ]

    print("=== 1D DP ===")
    for name, g, exp in cases:
        test(name, sol.uniquePathsWithObstacles(copy.deepcopy(g)), exp)

    print("\n=== 2D DP ===")
    for name, g, exp in cases:
        test("2D " + name, sol.uniquePathsWithObstacles2D(copy.deepcopy(g)), exp)

    print("\n=== Memoization ===")
    for name, g, exp in cases:
        # lru_cache holds onto grid in closure; reset by re-instantiating
        s = Solution()
        test("Memo " + name, s.uniquePathsWithObstaclesMemo(copy.deepcopy(g)), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
