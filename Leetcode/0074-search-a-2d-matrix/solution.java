/**
 * 0074. Search a 2D Matrix
 * https://leetcode.com/problems/search-a-2d-matrix/
 * Difficulty: Medium
 * Tags: Array, Binary Search, Matrix
 */
class Solution {

    /**
     * Optimal Solution (Single Binary Search on Flat Index).
     * Time:  O(log(m * n))
     * Space: O(1)
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int lo = 0, hi = m * n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int v = matrix[mid / n][mid % n];
            if (v == target) return true;
            if (v < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return false;
    }

    public boolean searchMatrixStaircase(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int r = 0, c = n - 1;
        while (r < m && c >= 0) {
            if (matrix[r][c] == target) return true;
            if (matrix[r][c] < target) r++;
            else c--;
        }
        return false;
    }

    public boolean searchMatrixTwo(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int lo = 0, hi = m - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            if (matrix[mid][0] <= target && target <= matrix[mid][n - 1]) {
                int l = 0, r = n - 1;
                while (l <= r) {
                    int mm = (l + r) / 2;
                    if (matrix[mid][mm] == target) return true;
                    if (matrix[mid][mm] < target) l = mm + 1;
                    else r = mm - 1;
                }
                return false;
            }
            if (matrix[mid][0] > target) hi = mid - 1;
            else lo = mid + 1;
        }
        return false;
    }

    static int passed = 0, failed = 0;
    static void test(String name, boolean got, boolean expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][] matrix = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}};
        Object[][] cases = {
            {"Example 1", matrix, 3, true},
            {"Example 2", matrix, 13, false},
            {"Found min", matrix, 1, true},
            {"Found max", matrix, 60, true},
            {"Below min", matrix, 0, false},
            {"Above max", matrix, 100, false},
            {"Found mid", matrix, 16, true},
            {"Not at boundary", matrix, 8, false},
            {"1x1 found", new int[][]{{5}}, 5, true},
            {"1x1 not found", new int[][]{{5}}, 6, false},
            {"Single row found", new int[][]{{1, 3, 5}}, 3, true},
            {"Single row not found", new int[][]{{1, 3, 5}}, 4, false},
            {"Single col found", new int[][]{{1}, {3}, {5}}, 5, true},
            {"Single col not found", new int[][]{{1}, {3}, {5}}, 4, false},
        };
        System.out.println("=== Single Binary Search ===");
        for (Object[] c : cases) test((String) c[0], sol.searchMatrix((int[][]) c[1], (int) c[2]), (boolean) c[3]);
        System.out.println("\n=== Staircase ===");
        for (Object[] c : cases) test("Stair " + c[0], sol.searchMatrixStaircase((int[][]) c[1], (int) c[2]), (boolean) c[3]);
        System.out.println("\n=== Two Binary Searches ===");
        for (Object[] c : cases) test("Two " + c[0], sol.searchMatrixTwo((int[][]) c[1], (int) c[2]), (boolean) c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
