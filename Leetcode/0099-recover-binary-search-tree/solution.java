import java.util.*;

class TreeNode {
    int val; TreeNode left, right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
}

class Solution {
    private TreeNode first, second, prev;
    public void recoverTree(TreeNode root) {
        inorder(root);
        int t = first.val; first.val = second.val; second.val = t;
    }
    private void inorder(TreeNode n) {
        if (n == null) return;
        inorder(n.left);
        if (prev != null && prev.val > n.val) {
            if (first == null) first = prev;
            second = n;
        }
        prev = n;
        inorder(n.right);
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
    static List<Integer> inorder(TreeNode n) {
        List<Integer> r = new ArrayList<>();
        if (n == null) return r;
        r.addAll(inorder(n.left));
        r.add(n.val);
        r.addAll(inorder(n.right));
        return r;
    }
    static boolean isSorted(List<Integer> a) {
        for (int i = 1; i < a.size(); i++) if (a.get(i) < a.get(i - 1)) return false;
        return true;
    }
    public static void main(String[] args) {
        Object[][] cases = {
            {"Example 1", new Integer[]{1, 3, null, null, 2}},
            {"Example 2", new Integer[]{3, 1, 4, null, null, 2}},
            {"Adjacent swap", new Integer[]{1, 2}}
        };
        for (Object[] c : cases) {
            TreeNode root = buildTree((Integer[]) c[1]);
            new Solution().recoverTree(root);
            List<Integer> r = inorder(root);
            if (isSorted(r)) { System.out.println("PASS: " + c[0] + " → " + r); passed++; }
            else { System.out.println("FAIL: " + c[0] + " → " + r); failed++; }
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
