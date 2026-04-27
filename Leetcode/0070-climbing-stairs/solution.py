from functools import lru_cache

# ============================================================
# 0070. Climbing Stairs
# https://leetcode.com/problems/climbing-stairs/
# Difficulty: Easy
# Tags: Math, Dynamic Programming, Memoization
# ============================================================


class Solution:
    def climbStairs(self, n: int) -> int:
        """
        Optimal Solution (DP with O(1) Space).
        Time:  O(n)
        Space: O(1)
        """
        if n <= 2: return n
        prev2, prev1 = 1, 2
        for _ in range(3, n + 1):
            prev2, prev1 = prev1, prev1 + prev2
        return prev1

    def climbStairsDP(self, n: int) -> int:
        if n <= 2: return n
        dp = [0] * (n + 1)
        dp[1], dp[2] = 1, 2
        for i in range(3, n + 1):
            dp[i] = dp[i - 1] + dp[i - 2]
        return dp[n]

    def climbStairsMemo(self, n: int) -> int:
        @lru_cache(maxsize=None)
        def f(k: int) -> int:
            if k <= 2: return k
            return f(k - 1) + f(k - 2)
        return f(n)

    def climbStairsMatrix(self, n: int) -> int:
        def mat_mul(a, b):
            return [
                [a[0][0]*b[0][0] + a[0][1]*b[1][0], a[0][0]*b[0][1] + a[0][1]*b[1][1]],
                [a[1][0]*b[0][0] + a[1][1]*b[1][0], a[1][0]*b[0][1] + a[1][1]*b[1][1]],
            ]
        def mat_pow(m, p):
            result = [[1, 0], [0, 1]]
            while p:
                if p & 1: result = mat_mul(result, m)
                m = mat_mul(m, m)
                p >>= 1
            return result
        m = mat_pow([[1, 1], [1, 0]], n)
        return m[0][0]


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        (1, 1), (2, 2), (3, 3), (4, 5), (5, 8), (6, 13), (7, 21), (8, 34),
        (10, 89), (15, 987), (20, 10946), (30, 1346269), (45, 1836311903),
    ]

    print("=== O(1) DP ===")
    for n, exp in cases: test(f"n={n}", sol.climbStairs(n), exp)
    print("\n=== O(n) DP ===")
    for n, exp in cases: test(f"DP n={n}", sol.climbStairsDP(n), exp)
    print("\n=== Memoization ===")
    for n, exp in cases: test(f"Memo n={n}", sol.climbStairsMemo(n), exp)
    print("\n=== Matrix Exponentiation ===")
    for n, exp in cases: test(f"Matrix n={n}", sol.climbStairsMatrix(n), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
