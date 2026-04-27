class Solution:
    def isInterleave(self, s1: str, s2: str, s3: str) -> bool:
        """Time O(m*n), Space O(n)."""
        m, n = len(s1), len(s2)
        if m + n != len(s3): return False
        dp = [False] * (n + 1)
        dp[0] = True
        for j in range(1, n + 1):
            dp[j] = dp[j - 1] and s2[j - 1] == s3[j - 1]
        for i in range(1, m + 1):
            dp[0] = dp[0] and s1[i - 1] == s3[i - 1]
            for j in range(1, n + 1):
                dp[j] = (dp[j] and s1[i - 1] == s3[i + j - 1]) or \
                        (dp[j - 1] and s2[j - 1] == s3[i + j - 1])
        return dp[n]


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    cases = [
        ("Example 1", "aabcc", "dbbca", "aadbbcbcac", True),
        ("Example 2", "aabcc", "dbbca", "aadbbbaccc", False),
        ("All empty", "", "", "", True),
        ("s1 empty", "", "abc", "abc", True),
        ("s2 empty", "abc", "", "abc", True),
        ("Length mismatch", "a", "b", "abc", False),
        ("Same chars", "ab", "ba", "abba", True),
    ]
    for n, s1, s2, s3, exp in cases:
        got = sol.isInterleave(s1, s2, s3)
        if got == exp: print(f"PASS: {n}"); passed += 1
        else: print(f"FAIL: {n} got={got}"); failed += 1
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
