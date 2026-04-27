import java.util.*;

/**
 * 0061. Rotate List
 * https://leetcode.com/problems/rotate-list/
 * Difficulty: Medium
 * Tags: Linked List, Two Pointers
 */
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {

    /**
     * Optimal Solution (Length + Re-Link).
     * Time:  O(n)
     * Space: O(1)
     */
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) return head;
        int n = 1;
        ListNode tail = head;
        while (tail.next != null) { tail = tail.next; n++; }
        k %= n;
        if (k == 0) return head;
        ListNode newTail = head;
        for (int i = 0; i < n - k - 1; i++) newTail = newTail.next;
        ListNode newHead = newTail.next;
        newTail.next = null;
        tail.next = head;
        return newHead;
    }

    /**
     * Make-circular-then-cut.
     * Time:  O(n)
     * Space: O(1)
     */
    public ListNode rotateRightCircular(ListNode head, int k) {
        if (head == null) return head;
        int n = 1;
        ListNode tail = head;
        while (tail.next != null) { tail = tail.next; n++; }
        tail.next = head;
        k %= n;
        ListNode newTail = head;
        for (int i = 0; i < n - k - 1; i++) newTail = newTail.next;
        ListNode newHead = newTail.next;
        newTail.next = null;
        return newHead;
    }

    // ============================================================
    // Helpers + Test Cases
    // ============================================================

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

    static int[] toArray(ListNode head) {
        List<Integer> out = new ArrayList<>();
        while (head != null) { out.add(head.val); head = head.next; }
        int[] r = new int[out.size()];
        for (int i = 0; i < r.length; i++) r[i] = out.get(i);
        return r;
    }

    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + Arrays.toString(got));
            System.out.println("  Expected: " + Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{1, 2, 3, 4, 5}, 2, new int[]{4, 5, 1, 2, 3}},
            {"Example 2", new int[]{0, 1, 2}, 4, new int[]{2, 0, 1}},
            {"Empty list", new int[]{}, 3, new int[]{}},
            {"Single node", new int[]{5}, 10, new int[]{5}},
            {"k=0", new int[]{1, 2, 3}, 0, new int[]{1, 2, 3}},
            {"k=n", new int[]{1, 2, 3}, 3, new int[]{1, 2, 3}},
            {"Two nodes k=1", new int[]{1, 2}, 1, new int[]{2, 1}},
            {"k = 2n", new int[]{1, 2, 3}, 6, new int[]{1, 2, 3}},
            {"Big k", new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 100, new int[]{5, 6, 7, 8, 1, 2, 3, 4}},
            {"Negatives in list", new int[]{-1, -2, -3}, 1, new int[]{-3, -1, -2}},
        };

        System.out.println("=== Length + Re-Link ===");
        for (Object[] c : cases) {
            test((String) c[0], toArray(sol.rotateRight(toList((int[]) c[1]), (int) c[2])),
                 (int[]) c[3]);
        }
        System.out.println("\n=== Make Circular + Cut ===");
        for (Object[] c : cases) {
            test("Circ " + c[0],
                 toArray(sol.rotateRightCircular(toList((int[]) c[1]), (int) c[2])),
                 (int[]) c[3]);
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
