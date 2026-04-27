import java.util.*;

/**
 * 0072. Edit Distance
 * https://leetcode.com/problems/edit-distance/
 * Difficulty: Hard
 * Tags: String, Dynamic Programming
 */
class Solution {

    /**
     * Optimal Solution (1D Bottom-Up DP).
     * Time:  O(m * n)
     * Space: O(n)
     */
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[] dp = new int[n + 1];
        for (int j = 0; j <= n; j++) dp[j] = j;
        for (int i = 1; i <= m; i++) {
            int prevDiag = dp[0];
            dp[0] = i;
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[j] = prevDiag;
                } else {
                    dp[j] = 1 + Math.min(prevDiag, Math.min(dp[j], dp[j - 1]));
                }
                prevDiag = temp;
            }
        }
        return dp[n];
    }

    public int minDistance2D(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    public int minDistanceMemo(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] memo = new int[m + 1][n + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return memoHelper(word1, word2, m, n, memo);
    }

    private int memoHelper(String w1, String w2, int i, int j, int[][] memo) {
        if (i == 0) return j;
        if (j == 0) return i;
        if (memo[i][j] != -1) return memo[i][j];
        int v;
        if (w1.charAt(i - 1) == w2.charAt(j - 1)) {
            v = memoHelper(w1, w2, i - 1, j - 1, memo);
        } else {
            v = 1 + Math.min(memoHelper(w1, w2, i - 1, j - 1, memo),
                             Math.min(memoHelper(w1, w2, i - 1, j, memo),
                                      memoHelper(w1, w2, i, j - 1, memo)));
        }
        return memo[i][j] = v;
    }

    static int passed = 0, failed = 0;
    static void test(String name, int got, int expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", "horse", "ros", 3},
            {"Example 2", "intention", "execution", 5},
            {"Both empty", "", "", 0},
            {"Empty w1", "", "abc", 3},
            {"Empty w2", "abc", "", 3},
            {"Identical", "abc", "abc", 0},
            {"All replace", "abc", "xyz", 3},
            {"One char insert", "a", "ab", 1},
            {"One char delete", "ab", "a", 1},
            {"Single replace", "a", "b", 1},
            {"Larger same", "abcdef", "abcdef", 0}
        };
        System.out.println("=== 1D DP ===");
        for (Object[] c : cases) test((String) c[0], sol.minDistance((String) c[1], (String) c[2]), (int) c[3]);
        System.out.println("\n=== 2D DP ===");
        for (Object[] c : cases) test("2D " + c[0], sol.minDistance2D((String) c[1], (String) c[2]), (int) c[3]);
        System.out.println("\n=== Memoization ===");
        for (Object[] c : cases) test("Memo " + c[0], sol.minDistanceMemo((String) c[1], (String) c[2]), (int) c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
