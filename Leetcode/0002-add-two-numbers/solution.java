import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 0002. Add Two Numbers
 * https://leetcode.com/problems/add-two-numbers/
 * Difficulty: Medium
 * Tags: Linked List, Math, Recursion
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
     * Optimal Solution (Simultaneous Traversal with Carry)
     * Approach: Iterate both lists digit by digit, propagate carry forward
     * Time:  O(max(m,n)) — traverse both lists once, where m and n are their lengths
     * Space: O(max(m,n)) — the result list has at most max(m,n)+1 nodes
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        // Dummy head makes it easy to append nodes without a null check
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        int carry = 0;

        // Continue until both lists are exhausted AND carry is zero
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;

            // Add digit from l1 (if available)
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            // Add digit from l2 (if available)
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            // Compute new carry and current digit
            carry = sum / 10;
            int digit = sum % 10;

            // Append new node to result
            curr.next = new ListNode(digit);
            curr = curr.next;
        }

        // Return the real head (skip dummy)
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

        // Test 1: Basic case — 342 + 465 = 807
        test("342 + 465 = 807",
            listToArray(sol.addTwoNumbers(buildList(new int[]{2, 4, 3}), buildList(new int[]{5, 6, 4}))),
            new int[]{7, 0, 8});

        // Test 2: Both zeros
        test("0 + 0 = 0",
            listToArray(sol.addTwoNumbers(buildList(new int[]{0}), buildList(new int[]{0}))),
            new int[]{0});

        // Test 3: Carry propagation across all digits — 9999999 + 9999 = 10009998
        test("9999999 + 9999 = 10009998",
            listToArray(sol.addTwoNumbers(buildList(new int[]{9, 9, 9, 9, 9, 9, 9}), buildList(new int[]{9, 9, 9, 9}))),
            new int[]{8, 9, 9, 9, 0, 0, 0, 1});

        // Test 4: Single digit addition without carry — 1 + 2 = 3
        test("1 + 2 = 3",
            listToArray(sol.addTwoNumbers(buildList(new int[]{1}), buildList(new int[]{2}))),
            new int[]{3});

        // Test 5: Single digit addition with carry — 5 + 5 = 10
        test("5 + 5 = 10",
            listToArray(sol.addTwoNumbers(buildList(new int[]{5}), buildList(new int[]{5}))),
            new int[]{0, 1});

        // Test 6: Different lengths — 99 + 1 = 100
        test("99 + 1 = 100",
            listToArray(sol.addTwoNumbers(buildList(new int[]{9, 9}), buildList(new int[]{1}))),
            new int[]{0, 0, 1});

        // Test 7: l1 longer than l2 — 123 + 4 = 127
        test("123 + 4 = 127",
            listToArray(sol.addTwoNumbers(buildList(new int[]{3, 2, 1}), buildList(new int[]{4}))),
            new int[]{7, 2, 1});

        // Test 8: l2 longer than l1 — 5 + 678 = 683
        test("5 + 678 = 683",
            listToArray(sol.addTwoNumbers(buildList(new int[]{5}), buildList(new int[]{8, 7, 6}))),
            new int[]{3, 8, 6});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
