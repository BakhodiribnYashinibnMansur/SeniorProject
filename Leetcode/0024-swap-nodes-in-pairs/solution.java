import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * 0024. Swap Nodes in Pairs
 * https://leetcode.com/problems/swap-nodes-in-pairs/
 * Difficulty: Medium
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
     * Optimal Solution (Iterative with Dummy Node)
     * Approach: Use a dummy node; for each pair, rewire pointers
     * Time:  O(n) — single pass through the list
     * Space: O(1) — only constant extra pointers
     */
    public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        while (prev.next != null && prev.next.next != null) {
            ListNode first = prev.next;
            ListNode second = prev.next.next;

            // Rewire pointers
            first.next = second.next;
            second.next = first;
            prev.next = second;

            // Advance past the swapped pair
            prev = first;
        }

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
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Standard even-length list
        test("[1,2,3,4] -> [2,1,4,3]",
            listToArray(sol.swapPairs(buildList(new int[]{1, 2, 3, 4}))),
            new int[]{2, 1, 4, 3});

        // Test 2: Empty list
        test("[] -> []",
            listToArray(sol.swapPairs(buildList(new int[]{}))),
            new int[]{});

        // Test 3: Single node
        test("[1] -> [1]",
            listToArray(sol.swapPairs(buildList(new int[]{1}))),
            new int[]{1});

        // Test 4: Two nodes
        test("[1,2] -> [2,1]",
            listToArray(sol.swapPairs(buildList(new int[]{1, 2}))),
            new int[]{2, 1});

        // Test 5: Odd-length list
        test("[1,2,3] -> [2,1,3]",
            listToArray(sol.swapPairs(buildList(new int[]{1, 2, 3}))),
            new int[]{2, 1, 3});

        // Test 6: Longer even-length list
        test("[1,2,3,4,5,6] -> [2,1,4,3,6,5]",
            listToArray(sol.swapPairs(buildList(new int[]{1, 2, 3, 4, 5, 6}))),
            new int[]{2, 1, 4, 3, 6, 5});

        // Test 7: Longer odd-length list
        test("[1,2,3,4,5] -> [2,1,4,3,5]",
            listToArray(sol.swapPairs(buildList(new int[]{1, 2, 3, 4, 5}))),
            new int[]{2, 1, 4, 3, 5});

        // Test 8: All same values
        test("[5,5,5,5] -> [5,5,5,5]",
            listToArray(sol.swapPairs(buildList(new int[]{5, 5, 5, 5}))),
            new int[]{5, 5, 5, 5});

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
