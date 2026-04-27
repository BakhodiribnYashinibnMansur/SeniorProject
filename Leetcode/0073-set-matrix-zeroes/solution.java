import java.util.*;

/**
 * 0073. Set Matrix Zeroes
 * https://leetcode.com/problems/set-matrix-zeroes/
 * Difficulty: Medium
 * Tags: Array, Hash Table, Matrix
 */
class Solution {

    /**
     * Optimal Solution (First Row/Col Markers, O(1) space).
     * Time:  O(m * n)
     * Space: O(1)
     */
    public void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstRowZero = false, firstColZero = false;
        for (int j = 0; j < n; j++) if (matrix[0][j] == 0) { firstRowZero = true; break; }
        for (int i = 0; i < m; i++) if (matrix[i][0] == 0) { firstColZero = true; break; }
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                if (matrix[i][0] == 0 || matrix[0][j] == 0)
                    matrix[i][j] = 0;
        if (firstRowZero) for (int j = 0; j < n; j++) matrix[0][j] = 0;
        if (firstColZero) for (int i = 0; i < m; i++) matrix[i][0] = 0;
    }

    public void setZeroesAux(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean[] zr = new boolean[m];
        boolean[] zc = new boolean[n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (matrix[i][j] == 0) { zr[i] = true; zc[j] = true; }
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (zr[i] || zc[j]) matrix[i][j] = 0;
    }

    static int passed = 0, failed = 0;
    static int[][] cloneGrid(int[][] g) {
        int[][] out = new int[g.length][];
        for (int i = 0; i < g.length; i++) out[i] = g[i].clone();
        return out;
    }
    static void test(String name, int[][] got, int[][] expected) {
        if (Arrays.deepEquals(got, expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[][]{{1,1,1},{1,0,1},{1,1,1}}, new int[][]{{1,0,1},{0,0,0},{1,0,1}}},
            {"Example 2", new int[][]{{0,1,2,0},{3,4,5,2},{1,3,1,5}}, new int[][]{{0,0,0,0},{0,4,5,0},{0,3,1,0}}},
            {"No zeros", new int[][]{{1,2},{3,4}}, new int[][]{{1,2},{3,4}}},
            {"All zeros", new int[][]{{0,0},{0,0}}, new int[][]{{0,0},{0,0}}},
            {"Single zero corner", new int[][]{{0,1},{1,1}}, new int[][]{{0,0},{0,1}}},
            {"Single row", new int[][]{{1,0,1}}, new int[][]{{0,0,0}}},
            {"Single col", new int[][]{{1},{0},{1}}, new int[][]{{0},{0},{0}}},
            {"1x1 zero", new int[][]{{0}}, new int[][]{{0}}},
            {"1x1 nonzero", new int[][]{{5}}, new int[][]{{5}}}
        };
        System.out.println("=== O(1) markers ===");
        for (Object[] c : cases) {
            int[][] got = cloneGrid((int[][]) c[1]);
            sol.setZeroes(got);
            test((String) c[0], got, (int[][]) c[2]);
        }
        System.out.println("\n=== O(m+n) auxiliary ===");
        for (Object[] c : cases) {
            int[][] got = cloneGrid((int[][]) c[1]);
            sol.setZeroesAux(got);
            test("Aux " + c[0], got, (int[][]) c[2]);
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
