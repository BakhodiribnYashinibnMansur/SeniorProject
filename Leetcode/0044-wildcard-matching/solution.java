/**
 * 0044. Wildcard Matching
 * https://leetcode.com/problems/wildcard-matching/
 * Difficulty: Hard
 * Tags: String, Dynamic Programming, Greedy, Recursion
 */
class Solution {

    /**
     * Approach 1: Bottom-up Dynamic Programming
     * Time:  O(m * n) — fill an (m+1) x (n+1) DP table
     * Space: O(m * n) — the DP table itself
     * where m = len(s), n = len(p)
     */
    public boolean isMatchDP(String s, String p) {
        int m = s.length(), n = p.length();

        // dp[i][j] = true if s[0..i-1] matches p[0..j-1]
        boolean[][] dp = new boolean[m + 1][n + 1];

        // Base case: empty string matches empty pattern
        dp[0][0] = true;

        // Base case: empty string vs pattern with leading '*'s
        // e.g., "***" can match "" since each '*' matches empty
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1];
            }
        }

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '*') {
                    // '*' matches empty (dp[i][j-1]) OR one more char (dp[i-1][j])
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else if (p.charAt(j - 1) == '?' || p.charAt(j - 1) == s.charAt(i - 1)) {
                    // Exact match or '?' wildcard
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // else: dp[i][j] stays false
            }
        }

        return dp[m][n];
    }

    /**
     * Optimal Solution — Greedy with Backtracking
     * Time:  O(m * n) worst case, often O(m + n) in practice
     * Space: O(1)     — only a few pointer variables
     * where m = len(s), n = len(p)
     */
    public boolean isMatch(String s, String p) {
        int sIdx = 0, pIdx = 0;
        int starIdx = -1, matchIdx = 0;

        while (sIdx < s.length()) {
            // Case 1: exact match or '?' matches any single char
            if (pIdx < p.length() && (p.charAt(pIdx) == s.charAt(sIdx) || p.charAt(pIdx) == '?')) {
                sIdx++;
                pIdx++;
            }
            // Case 2: '*' — record position, try matching 0 chars first
            else if (pIdx < p.length() && p.charAt(pIdx) == '*') {
                starIdx = pIdx;
                matchIdx = sIdx;
                pIdx++;
            }
            // Case 3: mismatch — backtrack to last '*', match one more char
            else if (starIdx != -1) {
                matchIdx++;
                sIdx = matchIdx;
                pIdx = starIdx + 1;
            }
            // Case 4: no match possible
            else {
                return false;
            }
        }

        // Skip any remaining '*'s in pattern (they match empty)
        while (pIdx < p.length() && p.charAt(pIdx) == '*') {
            pIdx++;
        }

        return pIdx == p.length();
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, boolean got, boolean expected) {
        if (got == expected) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %b%n  Expected: %b%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Pattern shorter, no wildcard — no match
        test("s=\"aa\" p=\"a\" → false", sol.isMatch("aa", "a"), false);

        // Test 2: '*' matches any sequence
        test("s=\"aa\" p=\"*\" → true", sol.isMatch("aa", "*"), true);

        // Test 3: '?' matches single char, but second char doesn't match
        test("s=\"cb\" p=\"?a\" → false", sol.isMatch("cb", "?a"), false);

        // Test 4: '*' matches subsequence in the middle
        test("s=\"adceb\" p=\"*a*b\" → true", sol.isMatch("adceb", "*a*b"), true);

        // Test 5: Cannot match — 'c' at end instead of 'b'
        test("s=\"acdcb\" p=\"a*c?b\" → false", sol.isMatch("acdcb", "a*c?b"), false);

        // Test 6: Both empty
        test("s=\"\" p=\"\" → true", sol.isMatch("", ""), true);

        // Test 7: Empty string matches "*"
        test("s=\"\" p=\"*\" → true", sol.isMatch("", "*"), true);

        // Test 8: Empty string, non-empty pattern without '*'
        test("s=\"\" p=\"?\" → false", sol.isMatch("", "?"), false);

        // Test 9: Exact match
        test("s=\"abc\" p=\"abc\" → true", sol.isMatch("abc", "abc"), true);

        // Test 10: '?' matches each character
        test("s=\"abc\" p=\"???\" → true", sol.isMatch("abc", "???"), true);

        // Test 11: Multiple '*' same as single
        test("s=\"abc\" p=\"a***c\" → true", sol.isMatch("abc", "a***c"), true);

        // Test 12: Star at end matches remaining
        test("s=\"abcdef\" p=\"abc*\" → true", sol.isMatch("abcdef", "abc*"), true);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
