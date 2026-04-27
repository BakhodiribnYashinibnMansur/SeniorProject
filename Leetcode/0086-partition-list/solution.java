import java.util.*;

class ListNode {
    int val; ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    public ListNode partition(ListNode head, int x) {
        ListNode lessDummy = new ListNode(0), geDummy = new ListNode(0);
        ListNode lt = lessDummy, gt = geDummy;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.val < x) { lt.next = cur; lt = lt.next; }
            else { gt.next = cur; gt = gt.next; }
        }
        gt.next = null;
        lt.next = geDummy.next;
        return lessDummy.next;
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
            {"Example 1", new int[]{1,4,3,2,5,2}, 3, new int[]{1,2,2,4,3,5}},
            {"Example 2", new int[]{2,1}, 2, new int[]{1,2}},
            {"Empty", new int[]{}, 5, new int[]{}},
            {"Single", new int[]{5}, 5, new int[]{5}},
            {"All less", new int[]{1,2,3}, 5, new int[]{1,2,3}},
            {"All greater", new int[]{5,6,7}, 1, new int[]{5,6,7}},
            {"All equal", new int[]{3,3,3}, 3, new int[]{3,3,3}},
            {"Negatives", new int[]{-3,1,-1,0}, 0, new int[]{-3,-1,1,0}},
        };
        for (Object[] c : cases) test((String) c[0], toArr(sol.partition(toList((int[]) c[1]), (int) c[2])), (int[]) c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
