/**
 * 0010. Regular Expression Matching
 * https://leetcode.com/problems/regular-expression-matching/
 * Difficulty: Hard
 * Tags: String, Dynamic Programming, Recursion
 */
class Solution {

    /**
     * Approach 1: Recursion
     * Time:  O(2^(m+n)) worst case — exponential due to '*' branching
     * Space: O(m+n)     — recursion call stack depth
     */
    public boolean isMatchRecursive(String s, String p) {
        // Base case: pattern is empty
        if (p.isEmpty()) {
            return s.isEmpty();
        }

        // Does the first character of s match the first character of p?
        // '.' matches any single character
        boolean firstMatch = !s.isEmpty() &&
            (p.charAt(0) == '.' || p.charAt(0) == s.charAt(0));

        // Handle '*': 0 occurrences OR 1+ occurrences
        if (p.length() >= 2 && p.charAt(1) == '*') {
            // Option A: 0 occurrences — skip "x*" in pattern
            // Option B: 1+ occurrences — consume one char from s (if first matches)
            return isMatchRecursive(s, p.substring(2)) ||
                   (firstMatch && isMatchRecursive(s.substring(1), p));
        }

        // No '*': first chars must match, then recurse on the rest
        return firstMatch && isMatchRecursive(s.substring(1), p.substring(1));
    }

    /**
     * Optimal Solution (Bottom-up Dynamic Programming)
     * Time:  O(m * n) — fill an (m+1) × (n+1) DP table
     * Space: O(m * n) — the DP table itself
     * where m = len(s), n = len(p)
     */
    public boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();

        // dp[i][j] = true if s[0..i-1] matches p[0..j-1]
        boolean[][] dp = new boolean[m + 1][n + 1];

        // Base case: empty string matches empty pattern
        dp[0][0] = true;

        // Base case: empty string vs pattern with '*'
        // e.g., "a*b*c*" can match "" using each x* as 0 occurrences
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '*') {
                    // '*' as 0 occurrences: ignore the "x*" pair in pattern
                    dp[i][j] = dp[i][j - 2];

                    // '*' as 1+ occurrences: s[i-1] must match p[j-2]
                    if (p.charAt(j - 2) == '.' || p.charAt(j - 2) == s.charAt(i - 1)) {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                } else if (p.charAt(j - 1) == '.' || p.charAt(j - 1) == s.charAt(i - 1)) {
                    // Characters match (exact or '.' wildcard)
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // else: dp[i][j] stays false
            }
        }

        return dp[m][n];
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

        // Test 2: '*' matches multiple of preceding element
        test("s=\"aa\" p=\"a*\" → true", sol.isMatch("aa", "a*"), true);

        // Test 3: ".*" matches any sequence
        test("s=\"ab\" p=\".*\" → true", sol.isMatch("ab", ".*"), true);

        // Test 4: Mixed '*' with zero occurrences
        test("s=\"aab\" p=\"c*a*b\" → true", sol.isMatch("aab", "c*a*b"), true);

        // Test 5: No match
        test("s=\"mississippi\" p=\"mis*is*p*.\" → false",
            sol.isMatch("mississippi", "mis*is*p*."), false);

        // Test 6: Empty string matches "a*"
        test("s=\"\" p=\"a*\" → true", sol.isMatch("", "a*"), true);

        // Test 7: Empty string matches "a*b*"
        test("s=\"\" p=\"a*b*\" → true", sol.isMatch("", "a*b*"), true);

        // Test 8: Dot matches any character
        test("s=\"ab\" p=\"..\" → true", sol.isMatch("ab", ".."), true);

        // Test 9: Single character exact match
        test("s=\"a\" p=\"a\" → true", sol.isMatch("a", "a"), true);

        // Test 10: Pattern must cover full string
        test("s=\"aaa\" p=\"a*a\" → true", sol.isMatch("aaa", "a*a"), true);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
