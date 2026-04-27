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
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy, cur = head;
        while (cur != null) {
            if (cur.next != null && cur.next.val == cur.val) {
                int v = cur.val;
                while (cur != null && cur.val == v) cur = cur.next;
                prev.next = cur;
            } else {
                prev = cur;
                cur = cur.next;
            }
        }
        return dummy.next;
    }

    static int passed = 0, failed = 0;
    static ListNode toList(int[] arr) {
        if (arr.length == 0) return null;
        ListNode head = new ListNode(arr[0]);
        ListNode cur = head;
        for (int i = 1; i < arr.length; i++) {
            cur.next = new ListNode(arr[i]);
            cur = cur.next;
        }
        return head;
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
        else { System.out.println("FAIL: " + name + " got=" + Arrays.toString(got)); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        int[][][] cases = {
            {{1,2,3,3,4,4,5}, {1,2,5}},
            {{1,1,1,2,3}, {2,3}},
            {{}, {}},
            {{1,1,1,1}, {}},
            {{1,2,3,4}, {1,2,3,4}},
            {{5}, {5}},
            {{1,2,3,3}, {1,2}},
            {{1,1,2,3,3,4,5,5}, {2,4}},
            {{-2,-1,-1,0,1}, {-2,0,1}}
        };
        for (int[][] c : cases) test(Arrays.toString(c[0]), toArr(sol.deleteDuplicates(toList(c[0]))), c[1]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
