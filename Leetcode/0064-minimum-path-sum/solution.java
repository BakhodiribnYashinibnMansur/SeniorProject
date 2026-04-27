import java.util.*;

/**
 * 0064. Minimum Path Sum
 * https://leetcode.com/problems/minimum-path-sum/
 * Difficulty: Medium
 * Tags: Array, Dynamic Programming, Matrix
 */
class Solution {

    /**
     * Optimal Solution (1D Bottom-Up DP).
     * Time:  O(m * n)
     * Space: O(n)
     */
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] dp = new int[n];
        dp[0] = grid[0][0];
        for (int c = 1; c < n; c++) dp[c] = dp[c - 1] + grid[0][c];
        for (int r = 1; r < m; r++) {
            dp[0] += grid[r][0];
            for (int c = 1; c < n; c++) {
                dp[c] = grid[r][c] + Math.min(dp[c], dp[c - 1]);
            }
        }
        return dp[n - 1];
    }

    public int minPathSum2D(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for (int c = 1; c < n; c++) dp[0][c] = dp[0][c - 1] + grid[0][c];
        for (int r = 1; r < m; r++) dp[r][0] = dp[r - 1][0] + grid[r][0];
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[r][c] = grid[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
            }
        }
        return dp[m - 1][n - 1];
    }

    public int minPathSumMemo(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] memo = new int[m][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return memoHelper(grid, m - 1, n - 1, memo);
    }

    private int memoHelper(int[][] grid, int r, int c, int[][] memo) {
        if (r == 0 && c == 0) return grid[0][0];
        if (r < 0 || c < 0) return Integer.MAX_VALUE / 2;
        if (memo[r][c] != -1) return memo[r][c];
        return memo[r][c] = grid[r][c] + Math.min(memoHelper(grid, r - 1, c, memo),
                                                   memoHelper(grid, r, c - 1, memo));
    }

    static int passed = 0, failed = 0;
    static int[][] cloneGrid(int[][] g) {
        int[][] out = new int[g.length][];
        for (int i = 0; i < g.length; i++) out[i] = g[i].clone();
        return out;
    }
    static void test(String name, int got, int expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " (got " + got + ", exp " + expected + ")"); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[][]{{1,3,1},{1,5,1},{4,2,1}}, 7},
            {"Example 2", new int[][]{{1,2,3},{4,5,6}}, 12},
            {"Single cell", new int[][]{{5}}, 5},
            {"Single row", new int[][]{{1,2,3}}, 6},
            {"Single column", new int[][]{{1},{2},{3}}, 6},
            {"All zeros", new int[][]{{0,0},{0,0}}, 0},
            {"All same 3x3", new int[][]{{5,5,5},{5,5,5},{5,5,5}}, 25},
            {"Strong gradient", new int[][]{{1,100,100},{1,1,1},{100,1,1}}, 5},
            {"Larger 4x4", new int[][]{{1,2,3,4},{2,3,4,5},{3,4,5,6},{4,5,6,7}}, 28},
            {"Single zero", new int[][]{{0}}, 0}
        };
        System.out.println("=== 1D DP ===");
        for (Object[] c : cases) test((String) c[0], sol.minPathSum(cloneGrid((int[][]) c[1])), (int) c[2]);
        System.out.println("\n=== 2D DP ===");
        for (Object[] c : cases) test("2D " + c[0], sol.minPathSum2D(cloneGrid((int[][]) c[1])), (int) c[2]);
        System.out.println("\n=== Memoization ===");
        for (Object[] c : cases) test("Memo " + c[0], sol.minPathSumMemo(cloneGrid((int[][]) c[1])), (int) c[2]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
