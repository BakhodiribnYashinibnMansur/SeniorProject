import java.util.*;

class Solution {
    public int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) return 0;
        int cols = matrix[0].length;
        int[] heights = new int[cols];
        int best = 0;
        for (char[] row : matrix) {
            for (int c = 0; c < cols; c++) {
                heights[c] = row[c] == '1' ? heights[c] + 1 : 0;
            }
            best = Math.max(best, largestRect(heights));
        }
        return best;
    }
    private int largestRect(int[] heights) {
        int n = heights.length, best = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];
            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                int top = stack.pop();
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                best = Math.max(best, heights[top] * width);
            }
            stack.push(i);
        }
        return best;
    }

    static int passed = 0, failed = 0;
    static char[][] toMat(String[] rows) {
        char[][] out = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) out[i] = rows[i].toCharArray();
        return out;
    }
    static void test(String name, int got, int exp) {
        if (got == exp) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }
    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new String[]{"10100","10111","11111","10010"}, 6},
            {"Single 0", new String[]{"0"}, 0},
            {"Single 1", new String[]{"1"}, 1},
            {"All zeros", new String[]{"00","00"}, 0},
            {"All ones 3x3", new String[]{"111","111","111"}, 9},
            {"Single row mixed", new String[]{"01101"}, 2},
            {"Single col", new String[]{"1","1","0","1","1","1"}, 3},
            {"L shape", new String[]{"110","110","111"}, 6}
        };
        for (Object[] c : cases) test((String) c[0], sol.maximalRectangle(toMat((String[]) c[1])), (int) c[2]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
