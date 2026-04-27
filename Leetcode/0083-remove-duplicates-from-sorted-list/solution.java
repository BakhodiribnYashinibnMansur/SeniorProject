import java.util.*;

class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode cur = head;
        while (cur != null && cur.next != null) {
            if (cur.next.val == cur.val) cur.next = cur.next.next;
            else cur = cur.next;
        }
        return head;
    }

    static int passed = 0, failed = 0;
    static ListNode toList(int[] arr) {
        if (arr.length == 0) return null;
        ListNode h = new ListNode(arr[0]);
        ListNode c = h;
        for (int i = 1; i < arr.length; i++) {
            c.next = new ListNode(arr[i]);
            c = c.next;
        }
        return h;
    }
    static int[] toArr(ListNode h) {
        List<Integer> out = new ArrayList<>();
        while (h != null) { out.add(h.val); h = h.next; }
        int[] r = new int[out.size()];
        for (int i = 0; i < r.length; i++) r[i] = out.get(i);
        return r;
    }
    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][][] cases = {
            {{1, 1, 2}, {1, 2}},
            {{1, 1, 2, 3, 3}, {1, 2, 3}},
            {{}, {}},
            {{5}, {5}},
            {{1, 1, 1}, {1}},
            {{1, 2, 3}, {1, 2, 3}},
            {{0, 0, 0, 1, 2, 2, 3, 3, 3, 4}, {0, 1, 2, 3, 4}}
        };
        for (int[][] c : cases) test(Arrays.toString(c[0]), toArr(sol.deleteDuplicates(toList(c[0]))), c[1]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
