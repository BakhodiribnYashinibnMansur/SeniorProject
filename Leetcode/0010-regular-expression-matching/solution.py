# ============================================================
# 0010. Regular Expression Matching
# https://leetcode.com/problems/regular-expression-matching/
# Difficulty: Hard
# Tags: String, Dynamic Programming, Recursion
# ============================================================


class Solution:
    def isMatchRecursive(self, s: str, p: str) -> bool:
        """
        Approach 1 — Recursion
        Time:  O(2^(m+n)) worst case — exponential due to '*' branching
        Space: O(m+n)     — recursion call stack depth
        """
        # Base case: pattern is empty
        if not p:
            return not s

        # Does the first character of s match the first character of p?
        # '.' matches any single character
        first_match = bool(s) and (p[0] == '.' or p[0] == s[0])

        # Handle '*': 0 occurrences OR 1+ occurrences
        if len(p) >= 2 and p[1] == '*':
            # Option A: 0 occurrences — skip "x*" in pattern
            # Option B: 1+ occurrences — consume one char from s (if first matches)
            return (self.isMatchRecursive(s, p[2:]) or
                    (first_match and self.isMatchRecursive(s[1:], p)))

        # No '*': first chars must match, then recurse on the rest
        return first_match and self.isMatchRecursive(s[1:], p[1:])

    def isMatch(self, s: str, p: str) -> bool:
        """
        Optimal Solution — Bottom-up Dynamic Programming
        Time:  O(m * n) — fill an (m+1) × (n+1) DP table
        Space: O(m * n) — the DP table itself
        where m = len(s), n = len(p)
        """
        m, n = len(s), len(p)

        # dp[i][j] = True if s[0..i-1] matches p[0..j-1]
        dp = [[False] * (n + 1) for _ in range(m + 1)]

        # Base case: empty string matches empty pattern
        dp[0][0] = True

        # Base case: empty string vs pattern with '*'
        # e.g., "a*b*c*" can match "" using each x* as 0 occurrences
        for j in range(2, n + 1):
            if p[j - 1] == '*':
                dp[0][j] = dp[0][j - 2]

        # Fill the DP table
        for i in range(1, m + 1):
            for j in range(1, n + 1):
                if p[j - 1] == '*':
                    # '*' as 0 occurrences: ignore the "x*" pair in pattern
                    dp[i][j] = dp[i][j - 2]

                    # '*' as 1+ occurrences: s[i-1] must match p[j-2]
                    if p[j - 2] == '.' or p[j - 2] == s[i - 1]:
                        dp[i][j] = dp[i][j] or dp[i - 1][j]

                elif p[j - 1] == '.' or p[j - 1] == s[i - 1]:
                    # Characters match (exact or '.' wildcard)
                    dp[i][j] = dp[i - 1][j - 1]
                # else: dp[i][j] stays False

        return dp[m][n]


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Pattern shorter, no wildcard — no match
    test('s="aa" p="a" → false', sol.isMatch("aa", "a"), False)

    # Test 2: '*' matches multiple of preceding element
    test('s="aa" p="a*" → true', sol.isMatch("aa", "a*"), True)

    # Test 3: ".*" matches any sequence
    test('s="ab" p=".*" → true', sol.isMatch("ab", ".*"), True)

    # Test 4: Mixed '*' with zero occurrences
    test('s="aab" p="c*a*b" → true', sol.isMatch("aab", "c*a*b"), True)

    # Test 5: No match
    test('s="mississippi" p="mis*is*p*." → false',
         sol.isMatch("mississippi", "mis*is*p*."), False)

    # Test 6: Empty string matches "a*"
    test('s="" p="a*" → true', sol.isMatch("", "a*"), True)

    # Test 7: Empty string matches "a*b*"
    test('s="" p="a*b*" → true', sol.isMatch("", "a*b*"), True)

    # Test 8: Dot matches any character
    test('s="ab" p=".." → true', sol.isMatch("ab", ".."), True)

    # Test 9: Single character exact match
    test('s="a" p="a" → true', sol.isMatch("a", "a"), True)

    # Test 10: Pattern must cover full string
    test('s="aaa" p="a*a" → true', sol.isMatch("aaa", "a*a"), True)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
