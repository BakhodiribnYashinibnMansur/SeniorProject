import java.util.*;

class TreeNode {
    int val; TreeNode left, right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
}

class Solution {
    public boolean isValidBST(TreeNode root) {
        return dfs(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    private boolean dfs(TreeNode n, long lo, long hi) {
        if (n == null) return true;
        if (n.val <= lo || n.val >= hi) return false;
        return dfs(n.left, lo, n.val) && dfs(n.right, n.val, hi);
    }

    static int passed = 0, failed = 0;
    static TreeNode buildTree(Integer[] arr) {
        if (arr.length == 0 || arr[0] == null) return null;
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> q = new ArrayDeque<>(); q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < arr.length) {
            TreeNode n = q.poll();
            if (i < arr.length && arr[i] != null) { n.left = new TreeNode(arr[i]); q.offer(n.left); } i++;
            if (i < arr.length && arr[i] != null) { n.right = new TreeNode(arr[i]); q.offer(n.right); } i++;
        }
        return root;
    }
    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Valid 1", new Integer[]{2,1,3}, true},
            {"Invalid 1", new Integer[]{5,1,4,null,null,3,6}, false},
            {"Single", new Integer[]{1}, true},
            {"Empty", new Integer[]{}, true},
            {"Equal not allowed", new Integer[]{1,1}, false},
            {"Deep valid", new Integer[]{4,2,6,1,3,5,7}, true},
            {"Deep invalid", new Integer[]{10,5,15,null,null,6,20}, false}
        };
        for (Object[] c : cases) {
            boolean got = sol.isValidBST(buildTree((Integer[]) c[1]));
            if (got == (boolean) c[2]) { System.out.println("PASS: " + c[0]); passed++; }
            else { System.out.println("FAIL: " + c[0] + " got=" + got); failed++; }
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
