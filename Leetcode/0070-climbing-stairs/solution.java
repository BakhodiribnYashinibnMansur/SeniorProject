/**
 * 0070. Climbing Stairs
 * https://leetcode.com/problems/climbing-stairs/
 * Difficulty: Easy
 * Tags: Math, Dynamic Programming, Memoization
 */
class Solution {

    /**
     * Optimal Solution (DP with O(1) Space).
     * Time:  O(n)
     * Space: O(1)
     */
    public int climbStairs(int n) {
        if (n <= 2) return n;
        int prev2 = 1, prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int cur = prev1 + prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    public int climbStairsDP(int n) {
        if (n <= 2) return n;
        int[] dp = new int[n + 1];
        dp[1] = 1; dp[2] = 2;
        for (int i = 3; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
        return dp[n];
    }

    public int climbStairsMemo(int n) {
        int[] memo = new int[n + 1];
        return memoHelper(n, memo);
    }
    private int memoHelper(int k, int[] memo) {
        if (k <= 2) return k;
        if (memo[k] != 0) return memo[k];
        return memo[k] = memoHelper(k - 1, memo) + memoHelper(k - 2, memo);
    }

    public int climbStairsMatrix(int n) {
        long[][] m = matPow(new long[][]{{1, 1}, {1, 0}}, n);
        return (int) m[0][0];
    }
    private long[][] matMul(long[][] a, long[][] b) {
        return new long[][]{
            {a[0][0]*b[0][0] + a[0][1]*b[1][0], a[0][0]*b[0][1] + a[0][1]*b[1][1]},
            {a[1][0]*b[0][0] + a[1][1]*b[1][0], a[1][0]*b[0][1] + a[1][1]*b[1][1]}
        };
    }
    private long[][] matPow(long[][] m, int p) {
        long[][] result = {{1, 0}, {0, 1}};
        while (p > 0) {
            if ((p & 1) == 1) result = matMul(result, m);
            m = matMul(m, m);
            p >>= 1;
        }
        return result;
    }

    static int passed = 0, failed = 0;
    static void test(String name, int got, int expected) {
        if (got == expected) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][] cases = {
            {1, 1}, {2, 2}, {3, 3}, {4, 5}, {5, 8}, {6, 13}, {7, 21}, {8, 34},
            {10, 89}, {15, 987}, {20, 10946}, {30, 1346269}, {45, 1836311903}
        };
        System.out.println("=== O(1) DP ===");
        for (int[] c : cases) test("n=" + c[0], sol.climbStairs(c[0]), c[1]);
        System.out.println("\n=== O(n) DP ===");
        for (int[] c : cases) test("DP n=" + c[0], sol.climbStairsDP(c[0]), c[1]);
        System.out.println("\n=== Memoization ===");
        for (int[] c : cases) test("Memo n=" + c[0], sol.climbStairsMemo(c[0]), c[1]);
        System.out.println("\n=== Matrix Exponentiation ===");
        for (int[] c : cases) test("Matrix n=" + c[0], sol.climbStairsMatrix(c[0]), c[1]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
