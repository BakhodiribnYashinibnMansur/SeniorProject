import java.util.*;

class TreeNode {
    int val; TreeNode left, right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
}

class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        if (p.val != q.val) return false;
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
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
            {"Example 1", new Integer[]{1,2,3}, new Integer[]{1,2,3}, true},
            {"Example 2", new Integer[]{1,2}, new Integer[]{1,null,2}, false},
            {"Both empty", new Integer[]{}, new Integer[]{}, true},
            {"One empty", new Integer[]{}, new Integer[]{1}, false},
            {"Different values", new Integer[]{1,2,1}, new Integer[]{1,1,2}, false},
            {"Single same", new Integer[]{5}, new Integer[]{5}, true},
            {"Single different", new Integer[]{5}, new Integer[]{6}, false}
        };
        for (Object[] c : cases) {
            boolean got = sol.isSameTree(buildTree((Integer[]) c[1]), buildTree((Integer[]) c[2]));
            if (got == (boolean) c[3]) { System.out.println("PASS: " + c[0]); passed++; }
            else { System.out.println("FAIL: " + c[0] + " got=" + got); failed++; }
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
