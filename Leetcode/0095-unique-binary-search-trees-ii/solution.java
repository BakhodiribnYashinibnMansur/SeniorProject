import java.util.*;

class TreeNode {
    int val; TreeNode left, right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
}

class Solution {
    public List<TreeNode> generateTrees(int n) {
        if (n == 0) return new ArrayList<>();
        return gen(1, n);
    }
    private List<TreeNode> gen(int lo, int hi) {
        List<TreeNode> result = new ArrayList<>();
        if (lo > hi) { result.add(null); return result; }
        for (int root = lo; root <= hi; root++) {
            for (TreeNode l : gen(lo, root - 1)) {
                for (TreeNode r : gen(root + 1, hi)) {
                    result.add(new TreeNode(root, l, r));
                }
            }
        }
        return result;
    }

    static int passed = 0, failed = 0;
    static int catalan(int n) {
        if (n <= 1) return 1;
        int[] c = new int[n + 1]; c[0] = c[1] = 1;
        for (int i = 2; i <= n; i++) for (int j = 0; j < i; j++) c[i] += c[j] * c[i - 1 - j];
        return c[n];
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        for (int n = 0; n <= 6; n++) {
            int got = sol.generateTrees(n).size();
            int want = (n == 0) ? 0 : catalan(n);
            if (got == want) { System.out.println("PASS: n=" + n + " → " + got); passed++; }
            else { System.out.println("FAIL: n=" + n + " got=" + got + " want=" + want); failed++; }
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
