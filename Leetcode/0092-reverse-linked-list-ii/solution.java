import java.util.*;

class ListNode {
    int val; ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    public ListNode reverseBetween(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        for (int i = 1; i < left; i++) prev = prev.next;
        ListNode cur = prev.next;
        for (int i = 0; i < right - left; i++) {
            ListNode nxt = cur.next;
            cur.next = nxt.next;
            nxt.next = prev.next;
            prev.next = nxt;
        }
        return dummy.next;
    }

    static int passed = 0, failed = 0;
    static ListNode toList(int[] a) {
        if (a.length == 0) return null;
        ListNode h = new ListNode(a[0]); ListNode c = h;
        for (int i = 1; i < a.length; i++) { c.next = new ListNode(a[i]); c = c.next; }
        return h;
    }
    static int[] toArr(ListNode h) {
        List<Integer> out = new ArrayList<>();
        while (h != null) { out.add(h.val); h = h.next; }
        int[] r = new int[out.size()];
        for (int i = 0; i < r.length; i++) r[i] = out.get(i);
        return r;
    }
    static void test(String name, int[] got, int[] exp) {
        if (Arrays.equals(got, exp)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{1,2,3,4,5}, 2, 4, new int[]{1,4,3,2,5}},
            {"Example 2", new int[]{5}, 1, 1, new int[]{5}},
            {"Reverse all", new int[]{1,2,3}, 1, 3, new int[]{3,2,1}},
            {"left==right", new int[]{1,2,3}, 2, 2, new int[]{1,2,3}},
            {"Reverse from start", new int[]{1,2,3,4}, 1, 2, new int[]{2,1,3,4}},
            {"Reverse to end", new int[]{1,2,3,4}, 3, 4, new int[]{1,2,4,3}},
            {"Two nodes", new int[]{1,2}, 1, 2, new int[]{2,1}}
        };
        for (Object[] c : cases) test((String) c[0], toArr(sol.reverseBetween(toList((int[]) c[1]), (int) c[2], (int) c[3])), (int[]) c[4]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
