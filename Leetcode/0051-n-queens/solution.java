import java.util.*;

/**
 * 0051. N-Queens
 * https://leetcode.com/problems/n-queens/
 * Difficulty: Hard
 * Tags: Array, Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Backtracking with Sets).
     * Approach: place one queen per row; track column and the two diagonal
     *   keys (r - c) and (r + c) in hash sets to prune in O(1).
     * Time:  O(n!) practical
     * Space: O(n)
     */
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queens = new int[n];
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>(); // r - c
        Set<Integer> diag2 = new HashSet<>(); // r + c
        backtrack(0, n, queens, cols, diag1, diag2, result);
        return result;
    }

    private void backtrack(int r, int n, int[] queens,
                           Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2,
                           List<List<String>> result) {
        if (r == n) {
            List<String> board = new ArrayList<>();
            for (int c : queens) {
                char[] row = new char[n];
                Arrays.fill(row, '.');
                row[c] = 'Q';
                board.add(new String(row));
            }
            result.add(board);
            return;
        }
        for (int c = 0; c < n; c++) {
            if (cols.contains(c) || diag1.contains(r - c) || diag2.contains(r + c)) continue;
            queens[r] = c;
            cols.add(c); diag1.add(r - c); diag2.add(r + c);
            backtrack(r + 1, n, queens, cols, diag1, diag2, result);
            cols.remove(c); diag1.remove(r - c); diag2.remove(r + c);
        }
    }

    /**
     * Bitmask Backtracking (fastest in practice).
     * Time:  O(n!) practical
     * Space: O(n)
     */
    public List<List<String>> solveNQueensBitmask(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queens = new int[n];
        backtrackBits(0, 0, 0, 0, n, queens, result);
        return result;
    }

    private void backtrackBits(int r, int cols, int d1, int d2, int n,
                               int[] queens, List<List<String>> result) {
        if (r == n) {
            List<String> board = new ArrayList<>();
            for (int c : queens) {
                char[] row = new char[n];
                Arrays.fill(row, '.');
                row[c] = 'Q';
                board.add(new String(row));
            }
            result.add(board);
            return;
        }
        int full = (1 << n) - 1;
        int free = full & ~(cols | d1 | d2);
        while (free != 0) {
            int bit = free & -free;
            int c = Integer.numberOfTrailingZeros(bit);
            queens[r] = c;
            backtrackBits(r + 1, cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1,
                          n, queens, result);
            free &= free - 1;
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static List<List<String>> canonicalize(List<List<String>> boards) {
        List<List<String>> out = new ArrayList<>();
        for (List<String> b : boards) out.add(new ArrayList<>(b));
        out.sort((a, b) -> {
            int n = Math.min(a.size(), b.size());
            for (int i = 0; i < n; i++) {
                int cmp = a.get(i).compareTo(b.get(i));
                if (cmp != 0) return cmp;
            }
            return Integer.compare(a.size(), b.size());
        });
        return out;
    }

    static void test(String name, List<List<String>> got, List<List<String>> expected) {
        if (canonicalize(got).equals(canonicalize(expected))) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + got);
            System.out.println("  Expected: " + expected);
            failed++;
        }
    }

    static void testCount(String name, List<List<String>> got, int expected) {
        if (got.size() == expected) {
            System.out.println("PASS: " + name + " (count=" + expected + ")");
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got count:      " + got.size());
            System.out.println("  Expected count: " + expected);
            failed++;
        }
    }

    static List<List<String>> boards(String[]... rows) {
        List<List<String>> out = new ArrayList<>();
        for (String[] r : rows) out.add(Arrays.asList(r));
        return out;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Backtracking with Sets ===");

        // Test 1: n = 1
        test("n=1", sol.solveNQueens(1),
             boards(new String[]{"Q"}));

        // Test 2: n = 2 — no solution
        test("n=2 (no solution)", sol.solveNQueens(2), new ArrayList<>());

        // Test 3: n = 3 — no solution
        test("n=3 (no solution)", sol.solveNQueens(3), new ArrayList<>());

        // Test 4: n = 4 — 2 solutions
        test("n=4", sol.solveNQueens(4),
             boards(
                 new String[]{".Q..", "...Q", "Q...", "..Q."},
                 new String[]{"..Q.", "Q...", "...Q", ".Q.."}
             ));

        // Counts for larger n
        testCount("n=5 count", sol.solveNQueens(5), 10);
        testCount("n=6 count", sol.solveNQueens(6), 4);
        testCount("n=7 count", sol.solveNQueens(7), 40);
        testCount("n=8 count (classic)", sol.solveNQueens(8), 92);
        testCount("n=9 count", sol.solveNQueens(9), 352);

        System.out.println("\n=== Backtracking with Bitmasks ===");

        // Test: bitmask agrees on n=4
        test("Bitmask n=4", sol.solveNQueensBitmask(4),
             boards(
                 new String[]{".Q..", "...Q", "Q...", "..Q."},
                 new String[]{"..Q.", "Q...", "...Q", ".Q.."}
             ));

        testCount("Bitmask n=8 count", sol.solveNQueensBitmask(8), 92);
        testCount("Bitmask n=9 count", sol.solveNQueensBitmask(9), 352);

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
