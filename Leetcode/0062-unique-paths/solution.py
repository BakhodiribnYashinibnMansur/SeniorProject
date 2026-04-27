from typing import List
from functools import lru_cache

# ============================================================
# 0062. Unique Paths
# https://leetcode.com/problems/unique-paths/
# Difficulty: Medium
# Tags: Math, Dynamic Programming, Combinatorics
# ============================================================


class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        """
        Optimal Solution (1D Bottom-Up DP).
        Time:  O(m * n)
        Space: O(n)
        """
        dp = [1] * n
        for _ in range(1, m):
            for c in range(1, n):
                dp[c] += dp[c - 1]
        return dp[n - 1]

    def uniquePaths2D(self, m: int, n: int) -> int:
        """2D DP. Time O(m*n), Space O(m*n)."""
        dp = [[1] * n for _ in range(m)]
        for r in range(1, m):
            for c in range(1, n):
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1]
        return dp[m - 1][n - 1]

    def uniquePathsMemo(self, m: int, n: int) -> int:
        """Top-Down DP. Time O(m*n), Space O(m*n)."""
        @lru_cache(maxsize=None)
        def f(r: int, c: int) -> int:
            if r == 0 or c == 0: return 1
            return f(r - 1, c) + f(r, c - 1)
        return f(m - 1, n - 1)

    def uniquePathsMath(self, m: int, n: int) -> int:
        """Combinatorics. Time O(min(m,n)), Space O(1)."""
        a, b = m + n - 2, min(m - 1, n - 1)
        result = 1
        for i in range(b):
            result = result * (a - i) // (i + 1)
        return result


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
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    cases = [
        ("Example 1", 3, 7, 28),
        ("Example 2", 3, 2, 3),
        ("1x1", 1, 1, 1),
        ("1xN", 1, 10, 1),
        ("Mx1", 10, 1, 1),
        ("Equal 3x3", 3, 3, 6),
        ("Equal 5x5", 5, 5, 70),
        ("7x3 symmetric", 7, 3, 28),
        ("Larger 10x10", 10, 10, 48620),
        ("23x12", 23, 12, 193536720),
    ]

    print("=== 1D DP ===")
    for name, m, n, exp in cases:
        test(name, sol.uniquePaths(m, n), exp)

    print("\n=== 2D DP ===")
    for name, m, n, exp in cases:
        test("2D " + name, sol.uniquePaths2D(m, n), exp)

    print("\n=== Memoization ===")
    for name, m, n, exp in cases:
        test("Memo " + name, sol.uniquePathsMemo(m, n), exp)

    print("\n=== Combinatorics ===")
    for name, m, n, exp in cases:
        test("Math " + name, sol.uniquePathsMath(m, n), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
