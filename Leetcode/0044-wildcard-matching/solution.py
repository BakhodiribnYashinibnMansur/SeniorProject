# ============================================================
# 0044. Wildcard Matching
# https://leetcode.com/problems/wildcard-matching/
# Difficulty: Hard
# Tags: String, Dynamic Programming, Greedy, Recursion
# ============================================================


class Solution:
    def isMatchDP(self, s: str, p: str) -> bool:
        """
        Approach 1 — Bottom-up Dynamic Programming
        Time:  O(m * n) — fill an (m+1) x (n+1) DP table
        Space: O(m * n) — the DP table itself
        where m = len(s), n = len(p)
        """
        m, n = len(s), len(p)

        # dp[i][j] = True if s[0..i-1] matches p[0..j-1]
        dp = [[False] * (n + 1) for _ in range(m + 1)]

        # Base case: empty string matches empty pattern
        dp[0][0] = True

        # Base case: empty string vs pattern with leading '*'s
        # e.g., "***" can match "" since each '*' matches empty
        for j in range(1, n + 1):
            if p[j - 1] == '*':
                dp[0][j] = dp[0][j - 1]

        # Fill the DP table
        for i in range(1, m + 1):
            for j in range(1, n + 1):
                if p[j - 1] == '*':
                    # '*' matches empty (dp[i][j-1]) OR one more char (dp[i-1][j])
                    dp[i][j] = dp[i][j - 1] or dp[i - 1][j]
                elif p[j - 1] == '?' or p[j - 1] == s[i - 1]:
                    # Exact match or '?' wildcard
                    dp[i][j] = dp[i - 1][j - 1]
                # else: dp[i][j] stays False

        return dp[m][n]

    def isMatch(self, s: str, p: str) -> bool:
        """
        Optimal Solution — Greedy with Backtracking
        Time:  O(m * n) worst case, often O(m + n) in practice
        Space: O(1)     — only a few pointer variables
        where m = len(s), n = len(p)
        """
        s_idx, p_idx = 0, 0
        star_idx, match_idx = -1, 0

        while s_idx < len(s):
            # Case 1: exact match or '?' matches any single char
            if p_idx < len(p) and (p[p_idx] == s[s_idx] or p[p_idx] == '?'):
                s_idx += 1
                p_idx += 1
            # Case 2: '*' — record position, try matching 0 chars first
            elif p_idx < len(p) and p[p_idx] == '*':
                star_idx = p_idx
                match_idx = s_idx
                p_idx += 1
            # Case 3: mismatch — backtrack to last '*', match one more char
            elif star_idx != -1:
                match_idx += 1
                s_idx = match_idx
                p_idx = star_idx + 1
            # Case 4: no match possible
            else:
                return False

        # Skip any remaining '*'s in pattern (they match empty)
        while p_idx < len(p) and p[p_idx] == '*':
            p_idx += 1

        return p_idx == len(p)


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Pattern shorter, no wildcard — no match
    test('s="aa" p="a" \u2192 false', sol.isMatch("aa", "a"), False)

    # Test 2: '*' matches any sequence
    test('s="aa" p="*" \u2192 true', sol.isMatch("aa", "*"), True)

    # Test 3: '?' matches single char, but second char doesn't match
    test('s="cb" p="?a" \u2192 false', sol.isMatch("cb", "?a"), False)

    # Test 4: '*' matches subsequence in the middle
    test('s="adceb" p="*a*b" \u2192 true', sol.isMatch("adceb", "*a*b"), True)

    # Test 5: Cannot match — 'c' at end instead of 'b'
    test('s="acdcb" p="a*c?b" \u2192 false', sol.isMatch("acdcb", "a*c?b"), False)

    # Test 6: Both empty
    test('s="" p="" \u2192 true', sol.isMatch("", ""), True)

    # Test 7: Empty string matches "*"
    test('s="" p="*" \u2192 true', sol.isMatch("", "*"), True)

    # Test 8: Empty string, non-empty pattern without '*'
    test('s="" p="?" \u2192 false', sol.isMatch("", "?"), False)

    # Test 9: Exact match
    test('s="abc" p="abc" \u2192 true', sol.isMatch("abc", "abc"), True)

    # Test 10: '?' matches each character
    test('s="abc" p="???" \u2192 true', sol.isMatch("abc", "???"), True)

    # Test 11: Multiple '*' same as single
    test('s="abc" p="a***c" \u2192 true', sol.isMatch("abc", "a***c"), True)

    # Test 12: Star at end matches remaining
    test('s="abcdef" p="abc*" \u2192 true', sol.isMatch("abcdef", "abc*"), True)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
