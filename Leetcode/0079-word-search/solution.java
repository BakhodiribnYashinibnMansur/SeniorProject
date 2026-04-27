/**
 * 0079. Word Search
 * https://leetcode.com/problems/word-search/
 * Difficulty: Medium
 * Tags: Array, Backtracking, Matrix
 */
class Solution {

    /**
     * Optimal Solution (DFS + Backtracking).
     * Time:  O(m * n * 4^L)
     * Space: O(L)
     */
    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                if (dfs(board, word, r, c, 0)) return true;
        return false;
    }

    private boolean dfs(char[][] board, String word, int r, int c, int i) {
        if (i == word.length()) return true;
        int m = board.length, n = board[0].length;
        if (r < 0 || r >= m || c < 0 || c >= n || board[r][c] != word.charAt(i)) return false;
        char save = board[r][c];
        board[r][c] = '#';
        boolean ok = dfs(board, word, r + 1, c, i + 1)
                  || dfs(board, word, r - 1, c, i + 1)
                  || dfs(board, word, r, c + 1, i + 1)
                  || dfs(board, word, r, c - 1, i + 1);
        board[r][c] = save;
        return ok;
    }

    static int passed = 0, failed = 0;

    static char[][] toBoard(String[] rows) {
        char[][] out = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) out[i] = rows[i].toCharArray();
        return out;
    }

    static void test(String name, boolean got, boolean expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        String[] standard = {"ABCE", "SFCS", "ADEE"};
        Object[][] cases = {
            {"Example 1", standard, "ABCCED", true},
            {"Example 2", standard, "SEE", true},
            {"Example 3", standard, "ABCB", false},
            {"Single cell match", new String[]{"A"}, "A", true},
            {"Single cell miss", new String[]{"A"}, "B", false},
            {"Word longer than grid", new String[]{"AB"}, "ABC", false},
            {"Same letter twice", new String[]{"AAB"}, "AAB", true},
            {"Spiral path", new String[]{"ABCD", "EFGH", "IJKL"}, "ABCDHGFE", true},
            {"Diagonal not allowed", new String[]{"AB", "CD"}, "AD", false},
            {"FCC found", standard, "FCC", true},
            {"BCBC not adjacent", standard, "BCBC", false},
        };
        for (Object[] c : cases) test((String) c[0], sol.exist(toBoard((String[]) c[1]), (String) c[2]), (boolean) c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
