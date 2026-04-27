import java.util.*;

class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val; this.left = left; this.right = right;
    }
}

class Solution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) { stack.push(cur); cur = cur.left; }
            cur = stack.pop();
            result.add(cur.val);
            cur = cur.right;
        }
        return result;
    }

    static int passed = 0, failed = 0;
    static TreeNode buildTree(Integer[] arr) {
        if (arr.length == 0 || arr[0] == null) return null;
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> q = new ArrayDeque<>(); q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < arr.length) {
            TreeNode n = q.poll();
            if (i < arr.length && arr[i] != null) { n.left = new TreeNode(arr[i]); q.offer(n.left); }
            i++;
            if (i < arr.length && arr[i] != null) { n.right = new TreeNode(arr[i]); q.offer(n.right); }
            i++;
        }
        return root;
    }
    static void test(String name, List<Integer> got, List<Integer> want) {
        if (got.equals(want)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new Integer[]{1, null, 2, 3}, Arrays.asList(1, 3, 2)},
            {"Empty", new Integer[]{}, Arrays.asList()},
            {"Single", new Integer[]{1}, Arrays.asList(1)},
            {"Left only", new Integer[]{1, 2, null, 3}, Arrays.asList(3, 2, 1)},
            {"Right only", new Integer[]{1, null, 2, null, 3}, Arrays.asList(1, 2, 3)},
            {"Balanced", new Integer[]{1, 2, 3}, Arrays.asList(2, 1, 3)}
        };
        for (Object[] c : cases) test((String) c[0], sol.inorderTraversal(buildTree((Integer[]) c[1])), (List<Integer>) c[2]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
