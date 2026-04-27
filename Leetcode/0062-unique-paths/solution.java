import java.util.*;

/**
 * 0062. Unique Paths
 * https://leetcode.com/problems/unique-paths/
 * Difficulty: Medium
 * Tags: Math, Dynamic Programming, Combinatorics
 */
class Solution {

    /**
     * Optimal Solution (1D Bottom-Up DP).
     * Time:  O(m * n)
     * Space: O(n)
     */
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[c] = dp[c] + dp[c - 1];
            }
        }
        return dp[n - 1];
    }

    public int uniquePaths2D(int m, int n) {
        int[][] dp = new int[m][n];
        for (int[] row : dp) Arrays.fill(row, 1);
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1];
            }
        }
        return dp[m - 1][n - 1];
    }

    public int uniquePathsMemo(int m, int n) {
        int[][] memo = new int[m][n];
        return memoHelper(m - 1, n - 1, memo);
    }

    private int memoHelper(int r, int c, int[][] memo) {
        if (r == 0 || c == 0) return 1;
        if (memo[r][c] != 0) return memo[r][c];
        return memo[r][c] = memoHelper(r - 1, c, memo) + memoHelper(r, c - 1, memo);
    }

    public int uniquePathsMath(int m, int n) {
        long a = m + n - 2;
        int b = Math.min(m - 1, n - 1);
        long result = 1;
        for (int i = 0; i < b; i++) {
            result = result * (a - i) / (i + 1);
        }
        return (int) result;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int got, int expected) {
        if (got == expected) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + got);
            System.out.println("  Expected: " + expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][] cases = {
            {3, 7, 28}, {3, 2, 3}, {1, 1, 1}, {1, 10, 1}, {10, 1, 1},
            {3, 3, 6}, {5, 5, 70}, {7, 3, 28}, {10, 10, 48620}, {23, 12, 193536720}
        };

        System.out.println("=== 1D DP ===");
        for (int[] c : cases) test("m=" + c[0] + ",n=" + c[1], sol.uniquePaths(c[0], c[1]), c[2]);

        System.out.println("\n=== 2D DP ===");
        for (int[] c : cases) test("2D m=" + c[0] + ",n=" + c[1], sol.uniquePaths2D(c[0], c[1]), c[2]);

        System.out.println("\n=== Memoization ===");
        for (int[] c : cases) test("Memo m=" + c[0] + ",n=" + c[1], sol.uniquePathsMemo(c[0], c[1]), c[2]);

        System.out.println("\n=== Combinatorics ===");
        for (int[] c : cases) test("Math m=" + c[0] + ",n=" + c[1], sol.uniquePathsMath(c[0], c[1]), c[2]);

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
