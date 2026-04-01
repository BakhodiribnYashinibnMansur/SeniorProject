/**
 * 0048. Rotate Image
 * https://leetcode.com/problems/rotate-image/
 * Difficulty: Medium
 * Tags: Array, Math, Matrix
 */
class Solution {

    /**
     * Optimal Solution (Transpose + Reverse Rows)
     * Approach: Transpose the matrix, then reverse each row
     * Time:  O(n^2) — visit each cell a constant number of times
     * Space: O(1) — all swaps done in-place
     */
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Step 1: Transpose the matrix (swap across the diagonal)
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Step 2: Reverse each row
        for (int i = 0; i < n; i++) {
            int left = 0, right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }

    /**
     * Four-way Swap approach
     * Approach: Rotate 4 cells at a time in a cycle
     * Time:  O(n^2) — each cell is moved exactly once
     * Space: O(1) — only one temp variable
     */
    public void rotateFourWay(int[][] matrix) {
        int n = matrix.length;

        // Process layer by layer from outside to inside
        for (int i = 0; i < n / 2; i++) {
            for (int j = i; j < n - 1 - i; j++) {
                // Save top
                int temp = matrix[i][j];

                // Left -> Top
                matrix[i][j] = matrix[n - 1 - j][i];

                // Bottom -> Left
                matrix[n - 1 - j][i] = matrix[n - 1 - i][n - 1 - j];

                // Right -> Bottom
                matrix[n - 1 - i][n - 1 - j] = matrix[j][n - 1 - i];

                // Top -> Right
                matrix[j][n - 1 - i] = temp;
            }
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static int[][] deepCopy(int[][] matrix) {
        int n = matrix.length;
        int[][] cp = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, cp[i], 0, n);
        }
        return cp;
    }

    static boolean equal(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length) return false;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }

    static String matStr(int[][] m) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < m.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append("[");
            for (int j = 0; j < m[i].length; j++) {
                if (j > 0) sb.append(", ");
                sb.append(m[i][j]);
            }
            sb.append("]");
        }
        sb.append("]");
        return sb.toString();
    }

    static void test(String name, int[][] got, int[][] expected) {
        if (equal(got, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, matStr(got), matStr(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][] mat;

        System.out.println("=== Transpose + Reverse (Optimal) ===");

        // Test 1: LeetCode Example 1
        mat = deepCopy(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        sol.rotate(mat);
        test("Example 1", mat, new int[][]{{7, 4, 1}, {8, 5, 2}, {9, 6, 3}});

        // Test 2: LeetCode Example 2
        mat = deepCopy(new int[][]{{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}});
        sol.rotate(mat);
        test("Example 2", mat, new int[][]{{15, 13, 2, 5}, {14, 3, 4, 1}, {12, 6, 8, 9}, {16, 7, 10, 11}});

        // Test 3: 1x1 matrix
        mat = deepCopy(new int[][]{{1}});
        sol.rotate(mat);
        test("1x1 matrix", mat, new int[][]{{1}});

        // Test 4: 2x2 matrix
        mat = deepCopy(new int[][]{{1, 2}, {3, 4}});
        sol.rotate(mat);
        test("2x2 matrix", mat, new int[][]{{3, 1}, {4, 2}});

        // Test 5: Negative values
        mat = deepCopy(new int[][]{{-1, -2}, {-3, -4}});
        sol.rotate(mat);
        test("Negative values", mat, new int[][]{{-3, -1}, {-4, -2}});

        // Test 6: All same values
        mat = deepCopy(new int[][]{{5, 5}, {5, 5}});
        sol.rotate(mat);
        test("All same values", mat, new int[][]{{5, 5}, {5, 5}});

        // Test 7: 5x5 matrix
        mat = deepCopy(new int[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 25}});
        sol.rotate(mat);
        test("5x5 matrix", mat, new int[][]{{21, 16, 11, 6, 1}, {22, 17, 12, 7, 2}, {23, 18, 13, 8, 3}, {24, 19, 14, 9, 4}, {25, 20, 15, 10, 5}});

        System.out.println("\n=== Four-way Swap ===");

        // Test 8: Four-way — Example 1
        mat = deepCopy(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        sol.rotateFourWay(mat);
        test("FW Example 1", mat, new int[][]{{7, 4, 1}, {8, 5, 2}, {9, 6, 3}});

        // Test 9: Four-way — Example 2
        mat = deepCopy(new int[][]{{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}});
        sol.rotateFourWay(mat);
        test("FW Example 2", mat, new int[][]{{15, 13, 2, 5}, {14, 3, 4, 1}, {12, 6, 8, 9}, {16, 7, 10, 11}});

        // Test 10: Four-way — 1x1
        mat = deepCopy(new int[][]{{1}});
        sol.rotateFourWay(mat);
        test("FW 1x1 matrix", mat, new int[][]{{1}});

        // Test 11: Four-way — 2x2
        mat = deepCopy(new int[][]{{1, 2}, {3, 4}});
        sol.rotateFourWay(mat);
        test("FW 2x2 matrix", mat, new int[][]{{3, 1}, {4, 2}});

        // Test 12: Four-way — 5x5
        mat = deepCopy(new int[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 25}});
        sol.rotateFourWay(mat);
        test("FW 5x5 matrix", mat, new int[][]{{21, 16, 11, 6, 1}, {22, 17, 12, 7, 2}, {23, 18, 13, 8, 3}, {24, 19, 14, 9, 4}, {25, 20, 15, 10, 5}});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
