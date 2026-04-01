import java.util.*;

/**
 * 0036. Valid Sudoku
 * https://leetcode.com/problems/valid-sudoku/
 * Difficulty: Medium
 * Tags: Array, Hash Table, Matrix
 */
class Solution {

    /**
     * Optimal Solution (Array-based Validation)
     * Approach: Single pass with boolean arrays for rows, cols, and boxes
     * Time:  O(1) — always 81 cells (9x9 fixed board)
     * Space: O(1) — three 9x9 boolean arrays (fixed size)
     */
    public boolean isValidSudoku(char[][] board) {
        boolean[][] rows = new boolean[9][9];
        boolean[][] cols = new boolean[9][9];
        boolean[][] boxes = new boolean[9][9];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] == '.') continue;

                int d = board[r][c] - '1';
                int box = (r / 3) * 3 + c / 3;

                if (rows[r][d] || cols[c][d] || boxes[box][d]) {
                    return false;
                }

                rows[r][d] = true;
                cols[c][d] = true;
                boxes[box][d] = true;
            }
        }

        return true;
    }

    /**
     * Hash Set approach
     * Approach: Use HashSets to track seen digits per row, column, and box
     * Time:  O(1) — always 81 cells
     * Space: O(1) — 27 sets, each at most 9 elements
     */
    @SuppressWarnings("unchecked")
    public boolean isValidSudokuHashSet(char[][] board) {
        Set<Character>[] rows = new HashSet[9];
        Set<Character>[] cols = new HashSet[9];
        Set<Character>[] boxes = new HashSet[9];

        for (int i = 0; i < 9; i++) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            boxes[i] = new HashSet<>();
        }

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char val = board[r][c];
                if (val == '.') continue;

                int box = (r / 3) * 3 + c / 3;

                if (!rows[r].add(val) || !cols[c].add(val) || !boxes[box].add(val)) {
                    return false;
                }
            }
        }

        return true;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, boolean got, boolean expected) {
        if (got == expected) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
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
        Solution sol = new Solution();

        // Valid board (LeetCode Example 1)
        char[][] validBoard = makeBoard(
            "53..7....",
            "6..195...",
            ".98....6.",
            "8...6...3",
            "4..8.3..1",
            "7...2...6",
            ".6....28.",
            "...419..5",
            "....8..79"
        );

        // Invalid board (LeetCode Example 2) — duplicate 8 in column 0 and top-left box
        char[][] invalidBoard = makeBoard(
            "83..7....",
            "6..195...",
            ".98....6.",
            "8...6...3",
            "4..8.3..1",
            "7...2...6",
            ".6....28.",
            "...419..5",
            "....8..79"
        );

        // Board with duplicate in a row
        char[][] rowDupBoard = makeBoard(
            "53..7...5",
            "6..195...",
            ".98....6.",
            "8...6...3",
            "4..8.3..1",
            "7...2...6",
            ".6....28.",
            "...419..5",
            "....8..79"
        );

        // Board with duplicate in a column
        char[][] colDupBoard = makeBoard(
            "53..7....",
            "6..195...",
            ".98....6.",
            "8...6...3",
            "4..8.3..1",
            "7...2...6",
            ".6....28.",
            "...419..5",
            "5...8..79"
        );

        // Almost empty board
        char[][] emptyBoard = makeBoard(
            ".........",
            ".........",
            ".........",
            ".........",
            ".........",
            ".........",
            ".........",
            ".........",
            "........."
        );

        // Single value board
        char[][] singleBoard = makeBoard(
            ".........",
            ".........",
            ".........",
            ".........",
            "....5....",
            ".........",
            ".........",
            ".........",
            "........."
        );

        System.out.println("=== Array-based Validation (Optimal) ===");

        test("Example 1 (valid)", sol.isValidSudoku(validBoard), true);
        test("Example 2 (invalid box)", sol.isValidSudoku(invalidBoard), false);
        test("Row duplicate", sol.isValidSudoku(rowDupBoard), false);
        test("Column duplicate", sol.isValidSudoku(colDupBoard), false);
        test("Almost empty board", sol.isValidSudoku(emptyBoard), true);
        test("Single value board", sol.isValidSudoku(singleBoard), true);

        System.out.println("\n=== Hash Set Approach ===");

        // Fresh boards for hash set approach
        char[][] validBoard2 = makeBoard(
            "53..7....",
            "6..195...",
            ".98....6.",
            "8...6...3",
            "4..8.3..1",
            "7...2...6",
            ".6....28.",
            "...419..5",
            "....8..79"
        );
        char[][] invalidBoard2 = makeBoard(
            "83..7....",
            "6..195...",
            ".98....6.",
            "8...6...3",
            "4..8.3..1",
            "7...2...6",
            ".6....28.",
            "...419..5",
            "....8..79"
        );

        test("HS: Example 1 (valid)", sol.isValidSudokuHashSet(validBoard2), true);
        test("HS: Example 2 (invalid box)", sol.isValidSudokuHashSet(invalidBoard2), false);

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
