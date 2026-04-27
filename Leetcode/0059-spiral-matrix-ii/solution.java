import java.util.*;

/**
 * 0059. Spiral Matrix II
 * https://leetcode.com/problems/spiral-matrix-ii/
 * Difficulty: Medium
 * Tags: Array, Matrix, Simulation
 */
class Solution {

    /**
     * Optimal Solution (Layer by Layer).
     * Time:  O(n^2)
     * Space: O(1) extra
     */
    public int[][] generateMatrix(int n) {
        int[][] m = new int[n][n];
        int top = 0, bottom = n - 1, left = 0, right = n - 1, val = 1;
        while (val <= n * n) {
            for (int c = left; c <= right; c++) m[top][c] = val++;
            top++;
            for (int r = top; r <= bottom; r++) m[r][right] = val++;
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) m[bottom][c] = val++;
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) m[r][left] = val++;
                left++;
            }
        }
        return m;
    }

    /**
     * Direction vectors using matrix as visited.
     * Time:  O(n^2)
     * Space: O(1) extra
     */
    public int[][] generateMatrixDirVec(int n) {
        int[][] m = new int[n][n];
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, d = 0;
        for (int k = 1; k <= n * n; k++) {
            m[r][c] = k;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= n || nc < 0 || nc >= n || m[nr][nc] != 0) {
                d = (d + 1) % 4;
                nr = r + dr[d]; nc = c + dc[d];
            }
            r = nr; c = nc;
        }
        return m;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;
    static void test(String name, int[][] got, int[][] expected) {
        if (Arrays.deepEquals(got, expected)) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + Arrays.deepToString(got));
            System.out.println("  Expected: " + Arrays.deepToString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"n=1", 1, new int[][]{{1}}},
            {"n=2", 2, new int[][]{{1, 2}, {4, 3}}},
            {"n=3", 3, new int[][]{{1, 2, 3}, {8, 9, 4}, {7, 6, 5}}},
            {"n=4", 4, new int[][]{
                {1, 2, 3, 4},
                {12, 13, 14, 5},
                {11, 16, 15, 6},
                {10, 9, 8, 7}
            }},
            {"n=5", 5, new int[][]{
                {1, 2, 3, 4, 5},
                {16, 17, 18, 19, 6},
                {15, 24, 25, 20, 7},
                {14, 23, 22, 21, 8},
                {13, 12, 11, 10, 9}
            }}
        };

        System.out.println("=== Layer by Layer ===");
        for (Object[] c : cases) test((String) c[0], sol.generateMatrix((int) c[1]), (int[][]) c[2]);

        System.out.println("\n=== Direction Vectors ===");
        for (Object[] c : cases) test("DirVec " + c[0], sol.generateMatrixDirVec((int) c[1]),
                                       (int[][]) c[2]);

        // n=20 sanity check
        int[][] big = sol.generateMatrix(20);
        Set<Integer> seen = new HashSet<>();
        for (int[] row : big) for (int v : row) seen.add(v);
        boolean ok = seen.size() == 400;
        for (int k = 1; k <= 400 && ok; k++) if (!seen.contains(k)) ok = false;
        if (ok) {
            System.out.println("PASS: n=20 contains 1..400 exactly once");
            passed++;
        } else {
            System.out.println("FAIL: n=20 missing values");
            failed++;
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
