class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length()) return false;
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int j = 1; j <= n; j++) dp[j] = dp[j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        for (int i = 1; i <= m; i++) {
            dp[0] = dp[0] && s1.charAt(i - 1) == s3.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                dp[j] = (dp[j] && s1.charAt(i - 1) == s3.charAt(i + j - 1)) ||
                        (dp[j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1));
            }
        }
        return dp[n];
    }

    static int passed = 0, failed = 0;
    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", "aabcc", "dbbca", "aadbbcbcac", true},
            {"Example 2", "aabcc", "dbbca", "aadbbbaccc", false},
            {"All empty", "", "", "", true},
            {"s1 empty", "", "abc", "abc", true},
            {"s2 empty", "abc", "", "abc", true},
            {"Length mismatch", "a", "b", "abc", false},
            {"Same chars", "ab", "ba", "abba", true}
        };
        for (Object[] c : cases) {
            boolean got = sol.isInterleave((String) c[1], (String) c[2], (String) c[3]);
            if (got == (boolean) c[4]) { System.out.println("PASS: " + c[0]); passed++; }
            else { System.out.println("FAIL: " + c[0] + " got=" + got); failed++; }
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
