import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * 0019. Remove Nth Node From End of List
 * https://leetcode.com/problems/remove-nth-node-from-end-of-list/
 * Difficulty: Medium
 * Tags: Linked List, Two Pointers
 */
class Solution {

    // Definition for singly-linked list node
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    /**
     * Approach 1: Two-Pass (Count Length, Then Remove)
     * First count total length, then find the (length - n)th node to remove
     * Time:  O(L) — two passes through the list
     * Space: O(1) — only constant extra space
     */
    public ListNode removeNthFromEndTwoPass(ListNode head, int n) {
        // Dummy node handles edge case of removing the head
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // First pass: count total length
        int length = 0;
        ListNode curr = head;
        while (curr != null) {
            length++;
            curr = curr.next;
        }

        // Second pass: advance to the node just before the target
        curr = dummy;
        for (int i = 0; i < length - n; i++) {
            curr = curr.next;
        }

        // Remove the target node by skipping it
        curr.next = curr.next.next;

        return dummy.next;
    }

    /**
     * Approach 2: One-Pass Two Pointers (Optimal)
     * Move fast pointer n+1 steps ahead, then move both until fast reaches end
     * Time:  O(L) — single pass through the list
     * Space: O(1) — only constant extra space
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        // Dummy node handles edge case of removing the head
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode fast = dummy;
        ListNode slow = dummy;

        // Move fast pointer n+1 steps ahead
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        // Move both pointers until fast reaches the end
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }

        // Remove the target node by skipping it
        slow.next = slow.next.next;

        return dummy.next;
    }

    // ============================================================
    // Helper Functions for Testing
    // ============================================================

    /** Creates a linked list from an array of integers */
    static ListNode buildList(int[] nums) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        for (int v : nums) {
            curr.next = new ListNode(v);
            curr = curr.next;
        }
        return dummy.next;
    }

    /** Converts a linked list to an int array for easy comparison */
    static int[] listToArray(ListNode head) {
        List<Integer> list = new ArrayList<>();
        while (head != null) {
            list.add(head.val);
            head = head.next;
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Basic case — remove 2nd from end of [1,2,3,4,5]
        test("Remove 2nd from end of [1,2,3,4,5]",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1, 2, 3, 4, 5}), 2)),
            new int[]{1, 2, 3, 5});

        // Test 2: Single element — remove 1st from end of [1]
        test("Remove 1st from end of [1]",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1}), 1)),
            new int[]{});

        // Test 3: Two elements — remove 1st from end of [1,2]
        test("Remove 1st from end of [1,2]",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1, 2}), 1)),
            new int[]{1});

        // Test 4: Remove head — remove 2nd from end of [1,2]
        test("Remove head (2nd from end of [1,2])",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1, 2}), 2)),
            new int[]{2});

        // Test 5: Remove last element — remove 1st from end of [1,2,3]
        test("Remove last (1st from end of [1,2,3])",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1, 2, 3}), 1)),
            new int[]{1, 2});

        // Test 6: Remove first element — remove 5th from end of [1,2,3,4,5]
        test("Remove first (5th from end of [1,2,3,4,5])",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1, 2, 3, 4, 5}), 5)),
            new int[]{2, 3, 4, 5});

        // Test 7: Remove middle — remove 3rd from end of [1,2,3,4,5]
        test("Remove middle (3rd from end of [1,2,3,4,5])",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1, 2, 3, 4, 5}), 3)),
            new int[]{1, 2, 4, 5});

        // Test 8: Longer list — remove 4th from end of [1,2,3,4,5,6,7]
        test("Remove 4th from end of [1,2,3,4,5,6,7]",
            listToArray(sol.removeNthFromEnd(buildList(new int[]{1, 2, 3, 4, 5, 6, 7}), 4)),
            new int[]{1, 2, 3, 5, 6, 7});

        // Test 9: Two-pass approach — same basic case
        test("Two-pass: Remove 2nd from end of [1,2,3,4,5]",
            listToArray(sol.removeNthFromEndTwoPass(buildList(new int[]{1, 2, 3, 4, 5}), 2)),
            new int[]{1, 2, 3, 5});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
