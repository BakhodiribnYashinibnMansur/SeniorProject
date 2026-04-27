import java.util.*;

/**
 * 0054. Spiral Matrix
 * https://leetcode.com/problems/spiral-matrix/
 * Difficulty: Medium
 * Tags: Array, Matrix, Simulation
 */
class Solution {

    /**
     * Optimal Solution (Layer by Layer).
     * Time:  O(m*n)
     * Space: O(1)
     */
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;
        int m = matrix.length, n = matrix[0].length;
        int top = 0, bottom = m - 1, left = 0, right = n - 1;
        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) result.add(matrix[top][c]);
            top++;
            for (int r = top; r <= bottom; r++) result.add(matrix[r][right]);
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) result.add(matrix[bottom][c]);
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) result.add(matrix[r][left]);
                left++;
            }
        }
        return result;
    }

    /**
     * Direction vectors with visited matrix.
     * Time:  O(m*n)
     * Space: O(m*n)
     */
    public List<Integer> spiralOrderDirVec(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;
        int m = matrix.length, n = matrix[0].length;
        boolean[][] visited = new boolean[m][n];
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, d = 0;
        for (int i = 0; i < m * n; i++) {
            result.add(matrix[r][c]);
            visited[r][c] = true;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || visited[nr][nc]) {
                d = (d + 1) % 4;
                nr = r + dr[d]; nc = c + dc[d];
            }
            r = nr; c = nc;
        }
        return result;
    }

    /**
     * In-place marker (mutates input).
     * Time:  O(m*n)
     * Space: O(1)
     */
    public List<Integer> spiralOrderInPlace(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;
        final int SENTINEL = Integer.MIN_VALUE;
        int m = matrix.length, n = matrix[0].length;
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int r = 0, c = 0, d = 0;
        for (int i = 0; i < m * n; i++) {
            result.add(matrix[r][c]);
            matrix[r][c] = SENTINEL;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || matrix[nr][nc] == SENTINEL) {
                d = (d + 1) % 4;
                nr = r + dr[d]; nc = c + dc[d];
            }
            r = nr; c = nc;
        }
        return result;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static int[][] clone2d(int[][] m) {
        int[][] out = new int[m.length][];
        for (int i = 0; i < m.length; i++) out[i] = m[i].clone();
        return out;
    }

    static void test(String name, List<Integer> got, List<Integer> expected) {
        if (got.equals(expected)) {
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
        Object[][] cases = {
            {"3x3", new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
                Arrays.asList(1, 2, 3, 6, 9, 8, 7, 4, 5)},
            {"3x4", new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}},
                Arrays.asList(1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7)},
            {"1x1", new int[][]{{5}}, Arrays.asList(5)},
            {"1x4 row", new int[][]{{1, 2, 3, 4}}, Arrays.asList(1, 2, 3, 4)},
            {"3x1 column", new int[][]{{1}, {2}, {3}}, Arrays.asList(1, 2, 3)},
            {"2x2", new int[][]{{1, 2}, {3, 4}}, Arrays.asList(1, 2, 4, 3)},
            {"2x4", new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}}, Arrays.asList(1, 2, 3, 4, 8, 7, 6, 5)},
            {"3x2", new int[][]{{1, 2}, {3, 4}, {5, 6}}, Arrays.asList(1, 2, 4, 6, 5, 3)},
            {"4x4", new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}},
                Arrays.asList(1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10)},
            {"Negatives", new int[][]{{-1, -2}, {-3, -4}}, Arrays.asList(-1, -2, -4, -3)},
        };

        System.out.println("=== Layer by Layer ===");
        for (Object[] c : cases) {
            test((String) c[0], sol.spiralOrder(clone2d((int[][]) c[1])),
                 (List<Integer>) c[2]);
        }

        System.out.println("\n=== Direction Vectors + Visited ===");
        for (Object[] c : cases) {
            test("DirVec " + c[0], sol.spiralOrderDirVec(clone2d((int[][]) c[1])),
                 (List<Integer>) c[2]);
        }

        System.out.println("\n=== In-Place Marker ===");
        for (Object[] c : cases) {
            test("InPlace " + c[0], sol.spiralOrderInPlace(clone2d((int[][]) c[1])),
                 (List<Integer>) c[2]);
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
