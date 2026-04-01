import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * 0025. Reverse Nodes in k-Group
 * https://leetcode.com/problems/reverse-nodes-in-k-group/
 * Difficulty: Hard
 * Tags: Linked List, Recursion
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
     * Approach 1: Iterative
     * Process list in groups of k, reversing each group in-place.
     * Time:  O(n) — each node is visited at most twice
     * Space: O(1) — only constant extra pointers
     */
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode groupPrev = dummy;

        while (true) {
            // Check if k nodes remain
            ListNode kth = getKthNode(groupPrev, k);
            if (kth == null) break;

            ListNode groupNext = kth.next;

            // Reverse k nodes in this group
            ListNode prev = kth.next;
            ListNode curr = groupPrev.next;
            while (curr != groupNext) {
                ListNode tmp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = tmp;
            }

            // Reconnect
            ListNode tmp = groupPrev.next;  // original first node, now the tail
            groupPrev.next = kth;           // point to new head of reversed group
            groupPrev = tmp;                // advance to the tail of the reversed group
        }

        return dummy.next;
    }

    /** Returns the kth node after the given node, or null if fewer than k nodes remain. */
    private ListNode getKthNode(ListNode node, int k) {
        while (node != null && k > 0) {
            node = node.next;
            k--;
        }
        return node;
    }

    /**
     * Approach 2: Recursive
     * Reverse first k nodes, recurse on the rest, connect them.
     * Time:  O(n) — each node is visited at most twice
     * Space: O(n/k) — recursion stack depth equals number of groups
     */
    public ListNode reverseKGroupRecursive(ListNode head, int k) {
        // Check if k nodes exist
        ListNode node = head;
        int count = 0;
        while (node != null && count < k) {
            node = node.next;
            count++;
        }

        if (count < k) return head;  // not enough nodes, leave as is

        // Reverse first k nodes
        ListNode prev = null;
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        // head is now the tail of the reversed group — connect to recursion result
        head.next = reverseKGroupRecursive(curr, k);

        return prev;  // prev is the new head of the reversed group
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
            System.out.printf("  \u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("  \u274c FAIL: %s%n    Got:      %s%n    Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // --- Iterative approach tests ---
        System.out.println("Approach 1: Iterative");

        test("[1,2,3,4,5] k=2 -> [2,1,4,3,5]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1, 2, 3, 4, 5}), 2)),
            new int[]{2, 1, 4, 3, 5});

        test("[1,2,3,4,5] k=3 -> [3,2,1,4,5]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1, 2, 3, 4, 5}), 3)),
            new int[]{3, 2, 1, 4, 5});

        test("[1,2,3,4,5] k=1 -> [1,2,3,4,5]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1, 2, 3, 4, 5}), 1)),
            new int[]{1, 2, 3, 4, 5});

        test("[1] k=1 -> [1]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1}), 1)),
            new int[]{1});

        test("[1,2] k=2 -> [2,1]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1, 2}), 2)),
            new int[]{2, 1});

        test("[1,2,3,4] k=2 -> [2,1,4,3]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1, 2, 3, 4}), 2)),
            new int[]{2, 1, 4, 3});

        test("[1,2,3,4,5] k=5 -> [5,4,3,2,1]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1, 2, 3, 4, 5}), 5)),
            new int[]{5, 4, 3, 2, 1});

        test("[1,2,3] k=3 -> [3,2,1]",
            listToArray(sol.reverseKGroup(buildList(new int[]{1, 2, 3}), 3)),
            new int[]{3, 2, 1});

        // --- Recursive approach tests ---
        System.out.println("\nApproach 2: Recursive");

        test("[1,2,3,4,5] k=2 -> [2,1,4,3,5]",
            listToArray(sol.reverseKGroupRecursive(buildList(new int[]{1, 2, 3, 4, 5}), 2)),
            new int[]{2, 1, 4, 3, 5});

        test("[1,2,3,4,5] k=3 -> [3,2,1,4,5]",
            listToArray(sol.reverseKGroupRecursive(buildList(new int[]{1, 2, 3, 4, 5}), 3)),
            new int[]{3, 2, 1, 4, 5});

        test("[1,2,3,4,5] k=1 -> [1,2,3,4,5]",
            listToArray(sol.reverseKGroupRecursive(buildList(new int[]{1, 2, 3, 4, 5}), 1)),
            new int[]{1, 2, 3, 4, 5});

        test("[1] k=1 -> [1]",
            listToArray(sol.reverseKGroupRecursive(buildList(new int[]{1}), 1)),
            new int[]{1});

        test("[1,2,3,4,5] k=5 -> [5,4,3,2,1]",
            listToArray(sol.reverseKGroupRecursive(buildList(new int[]{1, 2, 3, 4, 5}), 5)),
            new int[]{5, 4, 3, 2, 1});

        test("[1,2,3,4] k=2 -> [2,1,4,3]",
            listToArray(sol.reverseKGroupRecursive(buildList(new int[]{1, 2, 3, 4}), 2)),
            new int[]{2, 1, 4, 3});

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
