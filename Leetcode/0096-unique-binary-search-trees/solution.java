class Solution {
    public int numTrees(int n) {
        int[] g = new int[n + 1];
        g[0] = 1;
        if (n >= 1) g[1] = 1;
        for (int i = 2; i <= n; i++)
            for (int j = 0; j < i; j++) g[i] += g[j] * g[i - 1 - j];
        return g[n];
    }

    static int passed = 0, failed = 0;
    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][] cases = {{1, 1}, {2, 2}, {3, 5}, {4, 14}, {5, 42}, {6, 132}, {7, 429}, {19, 1767263190}};
        for (int[] c : cases) {
            int got = sol.numTrees(c[0]);
            if (got == c[1]) { System.out.println("PASS: n=" + c[0]); passed++; }
            else { System.out.println("FAIL: n=" + c[0] + " got=" + got); failed++; }
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
