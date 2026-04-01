import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 0037. Sudoku Solver
 * https://leetcode.com/problems/sudoku-solver/
 * Difficulty: Hard
 * Tags: Array, Hash Table, Backtracking, Matrix
 */
class Solution {

    /**
     * Optimal Solution (Backtracking with boolean array tracking)
     * Approach: Try digits 1-9 for each empty cell, backtrack on conflict
     * Time:  O(9^m) — m is number of empty cells, heavily pruned in practice
     * Space: O(m)   — recursion depth equals the number of empty cells
     */
    public void solveSudoku(char[][] board) {
        // Tracking arrays: which digits are used in each row/col/box
        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];
        List<int[]> empty = new ArrayList<>();

        // Initialize: scan existing digits
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') {
                    int d = board[r][c] - '1';
                    rows[r][d] = true;
                    cols[c][d] = true;
                    boxes[(r / 3) * 3 + c / 3][d] = true;
                } else {
                    empty.add(new int[]{r, c});
                }
            }
        }

        solve(board, empty, rows, cols, boxes, 0);
    }

    private boolean solve(char[][] board, List<int[]> empty,
                          boolean[][] rows, boolean[][] cols,
                          boolean[][] boxes, int idx) {
        if (idx == empty.size()) {
            return true; // All cells filled
        }

        int r = empty.get(idx)[0];
        int c = empty.get(idx)[1];
        int boxId = (r / 3) * 3 + c / 3;

        for (int d = 0; d < 9; d++) {
            if (!rows[r][d] && !cols[c][d] && !boxes[boxId][d]) {
                // Place digit
                board[r][c] = (char) (d + '1');
                rows[r][d] = true;
                cols[c][d] = true;
                boxes[boxId][d] = true;

                if (solve(board, empty, rows, cols, boxes, idx + 1)) {
                    return true;
                }

                // Backtrack
                board[r][c] = '.';
                rows[r][d] = false;
                cols[c][d] = false;
                boxes[boxId][d] = false;
            }
        }

        return false;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, char[][] board, char[][] expected) {
        new Solution().solveSudoku(board);
        boolean match = true;
        for (int r = 0; r < 9; r++) {
            if (!Arrays.equals(board[r], expected[r])) {
                match = false;
                break;
            }
        }
        if (match) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n", name);
            System.out.println("  Got:");
            for (char[] row : board) {
                System.out.printf("    %s%n", new String(row));
            }
            System.out.println("  Expected:");
            for (char[] row : expected) {
                System.out.printf("    %s%n", new String(row));
            }
            failed++;
        }
    }

    static char[][] makeBoard(String... rows) {
        char[][] board = new char[9][9];
        for (int i = 0; i < 9; i++) {
            board[i] = rows[i].toCharArray();
        }
        return board;
    }

    public static void main(String[] args) {
        // Test 1: LeetCode example
        test("LeetCode example",
            makeBoard(
                "53..7....",
                "6..195...",
                ".98....6.",
                "8...6...3",
                "4..8.3..1",
                "7...2...6",
                ".6....28.",
                "...419..5",
                "....8..79"),
            makeBoard(
                "534678912",
                "672195348",
                "198342567",
                "859761423",
                "426853791",
                "713924856",
                "961537284",
                "287419635",
                "345286179"));

        // Test 2: Almost solved — only one empty cell
        test("Almost solved",
            makeBoard(
                "534678912",
                "672195348",
                "198342567",
                "859761423",
                "426853791",
                "713924856",
                "961537284",
                "287419635",
                "34528617."),
            makeBoard(
                "534678912",
                "672195348",
                "198342567",
                "859761423",
                "426853791",
                "713924856",
                "961537284",
                "287419635",
                "345286179"));

        // Test 3: Hard puzzle — requires deep backtracking
        test("Hard puzzle",
            makeBoard(
                "..9748...",
                "7........",
                ".2.1.9...",
                "..7...24.",
                ".64.1.59.",
                ".98...3..",
                "...8.3.2.",
                "........6",
                "...2759.."),
            makeBoard(
                "519748632",
                "783652419",
                "426139875",
                "357986241",
                "264317598",
                "198524367",
                "975863124",
                "832491756",
                "641275983"));

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
