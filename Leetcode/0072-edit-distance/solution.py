from functools import lru_cache

# ============================================================
# 0072. Edit Distance
# https://leetcode.com/problems/edit-distance/
# Difficulty: Hard
# Tags: String, Dynamic Programming
# ============================================================


class Solution:
    def minDistance(self, word1: str, word2: str) -> int:
        """
        Optimal Solution (1D Bottom-Up DP).
        Time:  O(m * n)
        Space: O(n)
        """
        m, n = len(word1), len(word2)
        dp = list(range(n + 1))
        for i in range(1, m + 1):
            prev_diag = dp[0]
            dp[0] = i
            for j in range(1, n + 1):
                temp = dp[j]
                if word1[i - 1] == word2[j - 1]:
                    dp[j] = prev_diag
                else:
                    dp[j] = 1 + min(dp[j], dp[j - 1], prev_diag)
                prev_diag = temp
        return dp[n]

    def minDistance2D(self, word1: str, word2: str) -> int:
        m, n = len(word1), len(word2)
        dp = [[0] * (n + 1) for _ in range(m + 1)]
        for i in range(m + 1): dp[i][0] = i
        for j in range(n + 1): dp[0][j] = j
        for i in range(1, m + 1):
            for j in range(1, n + 1):
                if word1[i - 1] == word2[j - 1]:
                    dp[i][j] = dp[i - 1][j - 1]
                else:
                    dp[i][j] = 1 + min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
        return dp[m][n]

    def minDistanceMemo(self, word1: str, word2: str) -> int:
        @lru_cache(maxsize=None)
        def f(i: int, j: int) -> int:
            if i == 0: return j
            if j == 0: return i
            if word1[i - 1] == word2[j - 1]: return f(i - 1, j - 1)
            return 1 + min(f(i - 1, j), f(i, j - 1), f(i - 1, j - 1))
        return f(len(word1), len(word2))


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        ("Example 1", "horse", "ros", 3),
        ("Example 2", "intention", "execution", 5),
        ("Both empty", "", "", 0),
        ("Empty w1", "", "abc", 3),
        ("Empty w2", "abc", "", 3),
        ("Identical", "abc", "abc", 0),
        ("All replace", "abc", "xyz", 3),
        ("One char insert", "a", "ab", 1),
        ("One char delete", "ab", "a", 1),
        ("Single replace", "a", "b", 1),
        ("Larger same", "abcdef", "abcdef", 0),
    ]

    print("=== 1D DP ===")
    for n, a, b, exp in cases: test(n, sol.minDistance(a, b), exp)
    print("\n=== 2D DP ===")
    for n, a, b, exp in cases: test("2D " + n, sol.minDistance2D(a, b), exp)
    print("\n=== Memoization ===")
    for n, a, b, exp in cases:
        s = Solution()
        test("Memo " + n, s.minDistanceMemo(a, b), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
