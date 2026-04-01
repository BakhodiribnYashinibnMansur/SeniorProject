import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 0021. Merge Two Sorted Lists
 * https://leetcode.com/problems/merge-two-sorted-lists/
 * Difficulty: Easy
 * Tags: Linked List, Recursion
 */
class Solution {

    // Definition for singly-linked list
    static class ListNode {
        int val;
        ListNode next;

        ListNode() {}

        ListNode(int val) { this.val = val; }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    /**
     * Optimal Solution (Iterative)
     * Approach: Use a dummy node and two pointers to merge both lists
     * Time:  O(n + m) -- each node is visited exactly once
     * Space: O(1) -- only constant extra pointers used
     */
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        // Attach remaining nodes
        current.next = (list1 != null) ? list1 : list2;

        return dummy.next;
    }

    // ============================================================
    // Helper Functions
    // ============================================================

    /** Convert an array to a linked list */
    static ListNode listToLinked(int[] arr) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int val : arr) {
            current.next = new ListNode(val);
            current = current.next;
        }
        return dummy.next;
    }

    /** Convert a linked list to a List */
    static List<Integer> linkedToList(ListNode head) {
        List<Integer> result = new ArrayList<>();
        while (head != null) {
            result.add(head.val);
            head = head.next;
        }
        return result;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] list1Arr, int[] list2Arr, List<Integer> expected) {
        Solution sol = new Solution();
        ListNode l1 = listToLinked(list1Arr);
        ListNode l2 = listToLinked(list2Arr);
        List<Integer> result = linkedToList(sol.mergeTwoLists(l1, l2));
        if (result.equals(expected)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, result, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        // Test 1: Basic merge
        test("Basic merge",
            new int[]{1, 2, 4}, new int[]{1, 3, 4},
            Arrays.asList(1, 1, 2, 3, 4, 4));

        // Test 2: Both empty
        test("Both empty",
            new int[]{}, new int[]{},
            Arrays.asList());

        // Test 3: First empty
        test("First empty",
            new int[]{}, new int[]{0},
            Arrays.asList(0));

        // Test 4: Second empty
        test("Second empty",
            new int[]{1}, new int[]{},
            Arrays.asList(1));

        // Test 5: Single elements
        test("Single elements",
            new int[]{2}, new int[]{1},
            Arrays.asList(1, 2));

        // Test 6: Non-overlapping ranges
        test("Non-overlapping",
            new int[]{1, 2, 3}, new int[]{4, 5, 6},
            Arrays.asList(1, 2, 3, 4, 5, 6));

        // Test 7: Equal elements
        test("Equal elements",
            new int[]{1, 1}, new int[]{1, 1},
            Arrays.asList(1, 1, 1, 1));

        // Test 8: Negative values
        test("Negative values",
            new int[]{-3, -1}, new int[]{-2, 0},
            Arrays.asList(-3, -2, -1, 0));

        // Test 9: One much longer
        test("One much longer",
            new int[]{1}, new int[]{2, 3, 4, 5},
            Arrays.asList(1, 2, 3, 4, 5));

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
