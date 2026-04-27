import java.util.*;

/**
 * 0063. Unique Paths II
 * https://leetcode.com/problems/unique-paths-ii/
 * Difficulty: Medium
 * Tags: Array, Dynamic Programming, Matrix
 */
class Solution {

    /**
     * Optimal Solution (1D DP).
     * Time:  O(m * n)
     * Space: O(n)
     */
    public int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        if (grid[0][0] == 1) return 0;
        int[] dp = new int[n];
        dp[0] = 1;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) dp[c] = 0;
                else if (c > 0) dp[c] += dp[c - 1];
            }
        }
        return dp[n - 1];
    }

    public int uniquePathsWithObstacles2D(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        if (grid[0][0] == 1) return 0;
        int[][] dp = new int[m][n];
        dp[0][0] = 1;
        for (int c = 1; c < n; c++)
            if (grid[0][c] == 0) dp[0][c] = dp[0][c - 1];
        for (int r = 1; r < m; r++) {
            if (grid[r][0] == 0) dp[r][0] = dp[r - 1][0];
            for (int c = 1; c < n; c++) {
                if (grid[r][c] == 0) dp[r][c] = dp[r - 1][c] + dp[r][c - 1];
            }
        }
        return dp[m - 1][n - 1];
    }

    public int uniquePathsWithObstaclesMemo(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] memo = new int[m][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return memoHelper(grid, m - 1, n - 1, memo);
    }

    private int memoHelper(int[][] grid, int r, int c, int[][] memo) {
        if (r < 0 || c < 0 || grid[r][c] == 1) return 0;
        if (r == 0 && c == 0) return 1;
        if (memo[r][c] != -1) return memo[r][c];
        return memo[r][c] = memoHelper(grid, r - 1, c, memo) + memoHelper(grid, r, c - 1, memo);
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;
    static int[][] cloneGrid(int[][] g) {
        int[][] out = new int[g.length][];
        for (int i = 0; i < g.length; i++) out[i] = g[i].clone();
        return out;
    }
    static void test(String name, int got, int expected) {
        if (got == expected) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name + " (got " + got + ", exp " + expected + ")");
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[][]{{0,0,0},{0,1,0},{0,0,0}}, 2},
            {"Example 2", new int[][]{{0,1},{0,0}}, 1},
            {"Start blocked", new int[][]{{1,0},{0,0}}, 0},
            {"End blocked", new int[][]{{0,0},{0,1}}, 0},
            {"1x1 free", new int[][]{{0}}, 1},
            {"1x1 blocked", new int[][]{{1}}, 0},
            {"All free 3x3", new int[][]{{0,0,0},{0,0,0},{0,0,0}}, 6},
            {"First row obstacle", new int[][]{{0,1,0},{0,0,0},{0,0,0}}, 3},
            {"All vertical block", new int[][]{{0},{1},{0}}, 0},
            {"Diagonal blocked", new int[][]{{0,0,0},{1,1,0},{0,0,0}}, 1}
        };
        System.out.println("=== 1D DP ===");
        for (Object[] c : cases) test((String) c[0], sol.uniquePathsWithObstacles(cloneGrid((int[][]) c[1])), (int) c[2]);
        System.out.println("\n=== 2D DP ===");
        for (Object[] c : cases) test("2D " + c[0], sol.uniquePathsWithObstacles2D(cloneGrid((int[][]) c[1])), (int) c[2]);
        System.out.println("\n=== Memoization ===");
        for (Object[] c : cases) test("Memo " + c[0], sol.uniquePathsWithObstaclesMemo(cloneGrid((int[][]) c[1])), (int) c[2]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
