import java.util.*;

/**
 * 0023. Merge k Sorted Lists
 * https://leetcode.com/problems/merge-k-sorted-lists/
 * Difficulty: Hard
 * Tags: Linked List, Divide and Conquer, Heap (Priority Queue), Merge Sort
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
     * Approach 1: Brute Force (Collect All Values, Sort)
     * Collect all values, sort them, build a new linked list.
     * Time:  O(N log N) — sorting dominates
     * Space: O(N) — storing all values
     */
    public ListNode mergeKListsBrute(ListNode[] lists) {
        List<Integer> vals = new ArrayList<>();
        for (ListNode l : lists) {
            while (l != null) {
                vals.add(l.val);
                l = l.next;
            }
        }
        Collections.sort(vals);
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        for (int v : vals) {
            curr.next = new ListNode(v);
            curr = curr.next;
        }
        return dummy.next;
    }

    /**
     * Approach 2: Min Heap / Priority Queue
     * Maintain a heap of k list heads, repeatedly pop min and push next.
     * Time:  O(N log k) — each node pushed/popped from heap of size k
     * Space: O(k) — heap stores at most k nodes
     */
    public ListNode mergeKListsHeap(ListNode[] lists) {
        PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
        for (ListNode l : lists) {
            if (l != null) pq.offer(l);
        }
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        while (!pq.isEmpty()) {
            ListNode node = pq.poll();
            curr.next = node;
            curr = curr.next;
            if (node.next != null) pq.offer(node.next);
        }
        return dummy.next;
    }

    /**
     * Approach 3: Divide and Conquer (Merge Sort Style)
     * Pair-wise merge lists in rounds until one list remains.
     * Time:  O(N log k) — O(log k) rounds, each processing all N nodes
     * Space: O(1) — iterative pair-wise merging
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        List<ListNode> current = new ArrayList<>(Arrays.asList(lists));
        while (current.size() > 1) {
            List<ListNode> merged = new ArrayList<>();
            for (int i = 0; i < current.size(); i += 2) {
                ListNode l1 = current.get(i);
                ListNode l2 = (i + 1 < current.size()) ? current.get(i + 1) : null;
                merged.add(mergeTwoLists(l1, l2));
            }
            current = merged;
        }
        return current.get(0);
    }

    /** Merge two sorted linked lists into one sorted list. */
    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                curr.next = l1;
                l1 = l1.next;
            } else {
                curr.next = l2;
                l2 = l2.next;
            }
            curr = curr.next;
        }
        curr.next = (l1 != null) ? l1 : l2;
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

    /** Creates an array of linked lists from a 2D int array */
    static ListNode[] buildLists(int[][] arrays) {
        ListNode[] lists = new ListNode[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            lists[i] = buildList(arrays[i]);
        }
        return lists;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.printf("  ✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("  ❌ FAIL: %s%n    Got:      %s%n    Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // --- Test Divide and Conquer (primary solution) ---
        System.out.println("Approach 3: Divide and Conquer");

        // Test 1: LeetCode Example 1
        test("Example 1: [[1,4,5],[1,3,4],[2,6]]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{1, 4, 5}, {1, 3, 4}, {2, 6}}))),
            new int[]{1, 1, 2, 3, 4, 4, 5, 6});

        // Test 2: Empty input
        test("Example 2: []",
            listToArray(sol.mergeKLists(new ListNode[]{})),
            new int[]{});

        // Test 3: Single empty list
        test("Example 3: [[]]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{}}))),
            new int[]{});

        // Test 4: Single list
        test("Single list: [[1,2,3]]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{1, 2, 3}}))),
            new int[]{1, 2, 3});

        // Test 5: Two lists
        test("Two lists: [[1,3,5],[2,4,6]]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{1, 3, 5}, {2, 4, 6}}))),
            new int[]{1, 2, 3, 4, 5, 6});

        // Test 6: Duplicates
        test("Duplicates: [[1,1],[1,1]]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{1, 1}, {1, 1}}))),
            new int[]{1, 1, 1, 1});

        // Test 7: Negative values
        test("Negative values: [[-3,-1],[0,2],[-2,1]]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{-3, -1}, {0, 2}, {-2, 1}}))),
            new int[]{-3, -2, -1, 0, 1, 2});

        // Test 8: Multiple empty lists
        test("Multiple empty: [[], [], []]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{}, {}, {}}))),
            new int[]{});

        // Test 9: Mixed empty and non-empty
        test("Mixed: [[], [1], [], [2,3]]",
            listToArray(sol.mergeKLists(buildLists(new int[][]{{}, {1}, {}, {2, 3}}))),
            new int[]{1, 2, 3});

        // --- Test Heap approach ---
        System.out.println("\nApproach 2: Min Heap");

        test("Heap: Example 1",
            listToArray(sol.mergeKListsHeap(buildLists(new int[][]{{1, 4, 5}, {1, 3, 4}, {2, 6}}))),
            new int[]{1, 1, 2, 3, 4, 4, 5, 6});

        test("Heap: Empty input",
            listToArray(sol.mergeKListsHeap(new ListNode[]{})),
            new int[]{});

        test("Heap: Negative values",
            listToArray(sol.mergeKListsHeap(buildLists(new int[][]{{-3, -1}, {0, 2}, {-2, 1}}))),
            new int[]{-3, -2, -1, 0, 1, 2});

        // --- Test Brute Force ---
        System.out.println("\nApproach 1: Brute Force");

        test("Brute: Example 1",
            listToArray(sol.mergeKListsBrute(buildLists(new int[][]{{1, 4, 5}, {1, 3, 4}, {2, 6}}))),
            new int[]{1, 1, 2, 3, 4, 4, 5, 6});

        test("Brute: Empty input",
            listToArray(sol.mergeKListsBrute(new ListNode[]{})),
            new int[]{});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
