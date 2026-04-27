import java.util.*;

/**
 * 0052. N-Queens II
 * https://leetcode.com/problems/n-queens-ii/
 * Difficulty: Hard
 * Tags: Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Bitmask Backtracking).
     * Time:  O(n!) practical
     * Space: O(n)
     */
    public int totalNQueens(int n) {
        int full = (1 << n) - 1;
        return solve(0, 0, 0, full);
    }

    private int solve(int cols, int d1, int d2, int full) {
        if (cols == full) return 1;
        int free = full & ~(cols | d1 | d2);
        int total = 0;
        while (free != 0) {
            int bit = free & -free;
            total += solve(cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1, full);
            free &= free - 1;
        }
        return total;
    }

    int count;

    /**
     * Backtracking with hash sets.
     * Time:  O(n!) practical
     * Space: O(n)
     */
    public int totalNQueensSets(int n) {
        count = 0;
        Set<Integer> cols = new HashSet<>();
        Set<Integer> d1 = new HashSet<>();
        Set<Integer> d2 = new HashSet<>();
        backtrack(0, n, cols, d1, d2);
        return count;
    }

    private void backtrack(int r, int n,
                           Set<Integer> cols, Set<Integer> d1, Set<Integer> d2) {
        if (r == n) { count++; return; }
        for (int c = 0; c < n; c++) {
            if (cols.contains(c) || d1.contains(r - c) || d2.contains(r + c)) continue;
            cols.add(c); d1.add(r - c); d2.add(r + c);
            backtrack(r + 1, n, cols, d1, d2);
            cols.remove(c); d1.remove(r - c); d2.remove(r + c);
        }
    }

    /**
     * Lookup table (n <= 9).
     * Time:  O(1)
     * Space: O(1)
     */
    public int totalNQueensLookup(int n) {
        int[] table = {0, 1, 0, 0, 2, 10, 4, 40, 92, 352};
        return table[n];
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;
    static void test(String name, int got, int expected) {
        if (got == expected) {
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
        int[] expected = {0, 1, 0, 0, 2, 10, 4, 40, 92, 352};

        System.out.println("=== Bitmask backtracking ===");
        for (int n = 1; n <= 9; n++) test("n=" + n, sol.totalNQueens(n), expected[n]);

        System.out.println("\n=== Sets backtracking ===");
        for (int n = 1; n <= 9; n++) test("Sets n=" + n, sol.totalNQueensSets(n), expected[n]);

        System.out.println("\n=== Lookup table ===");
        for (int n = 1; n <= 9; n++) test("Lookup n=" + n, sol.totalNQueensLookup(n), expected[n]);

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
