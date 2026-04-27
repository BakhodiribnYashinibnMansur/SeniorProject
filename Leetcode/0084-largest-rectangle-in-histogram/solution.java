import java.util.*;

class Solution {
    public int largestRectangleArea(int[] heights) {
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
    static void test(String name, int got, int exp) {
        if (got == exp) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][] cases = {
            {2, 1, 5, 6, 2, 3}, {10, 0, 0, 0, 0, 0},
            {2, 4}, {4, 0, 0, 0, 0, 0},
            {5}, {5, 0, 0, 0, 0, 0},
            {3, 3, 3, 3}, {12, 0, 0, 0, 0, 0},
            {1, 2, 3, 4, 5}, {9, 0, 0, 0, 0, 0},
            {5, 4, 3, 2, 1}, {9, 0, 0, 0, 0, 0},
            {0, 1, 0, 2}, {2, 0, 0, 0, 0, 0},
            {0, 0, 0}, {0, 0, 0, 0, 0, 0},
            {1, 100}, {100, 0, 0, 0, 0, 0},
            {1, 2, 3, 2, 1}, {6, 0, 0, 0, 0, 0},
            {0, 0, 2, 1, 2}, {3, 0, 0, 0, 0, 0}
        };
        for (int i = 0; i < cases.length; i += 2) {
            test("case " + (i / 2), sol.largestRectangleArea(cases[i]), cases[i + 1][0]);
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
