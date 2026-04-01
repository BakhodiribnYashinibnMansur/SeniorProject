# ============================================================
# 0023. Merge k Sorted Lists
# https://leetcode.com/problems/merge-k-sorted-lists/
# Difficulty: Hard
# Tags: Linked List, Divide and Conquer, Heap (Priority Queue), Merge Sort
# ============================================================

from __future__ import annotations
import heapq


class ListNode:
    """Singly-linked list node."""
    def __init__(self, val: int = 0, next: ListNode | None = None):
        self.val = val
        self.next = next


class Solution:
    def mergeKListsBrute(self, lists: list[ListNode | None]) -> ListNode | None:
        """
        Approach 1: Brute Force (Collect All Values, Sort)
        Collect all values, sort them, build a new linked list.
        Time:  O(N log N) — sorting dominates
        Space: O(N) — storing all values
        """
        vals: list[int] = []
        for l in lists:
            while l:
                vals.append(l.val)
                l = l.next
        vals.sort()
        dummy = ListNode(0)
        curr = dummy
        for v in vals:
            curr.next = ListNode(v)
            curr = curr.next
        return dummy.next

    def mergeKListsHeap(self, lists: list[ListNode | None]) -> ListNode | None:
        """
        Approach 2: Min Heap / Priority Queue
        Maintain a heap of k list heads, repeatedly pop min and push next.
        Time:  O(N log k) — each node pushed/popped from heap of size k
        Space: O(k) — heap stores at most k nodes
        """
        heap: list[tuple[int, int, ListNode]] = []
        for i, l in enumerate(lists):
            if l:
                heapq.heappush(heap, (l.val, i, l))

        dummy = ListNode(0)
        curr = dummy
        while heap:
            val, i, node = heapq.heappop(heap)
            curr.next = node
            curr = curr.next
            if node.next:
                heapq.heappush(heap, (node.next.val, i, node.next))

        return dummy.next

    def mergeKLists(self, lists: list[ListNode | None]) -> ListNode | None:
        """
        Approach 3: Divide and Conquer (Merge Sort Style)
        Pair-wise merge lists in rounds until one list remains.
        Time:  O(N log k) — O(log k) rounds, each processing all N nodes
        Space: O(1) — iterative pair-wise merging
        """
        if not lists:
            return None

        while len(lists) > 1:
            merged: list[ListNode | None] = []
            for i in range(0, len(lists), 2):
                l1 = lists[i]
                l2 = lists[i + 1] if i + 1 < len(lists) else None
                merged.append(self._mergeTwoLists(l1, l2))
            lists = merged

        return lists[0]

    def _mergeTwoLists(self, l1: ListNode | None, l2: ListNode | None) -> ListNode | None:
        """Merge two sorted linked lists into one sorted list."""
        dummy = ListNode(0)
        curr = dummy
        while l1 and l2:
            if l1.val <= l2.val:
                curr.next = l1
                l1 = l1.next
            else:
                curr.next = l2
                l2 = l2.next
            curr = curr.next
        curr.next = l1 if l1 else l2
        return dummy.next


# ============================================================
# Helper Functions for Testing
# ============================================================

def build_list(nums: list[int]) -> ListNode | None:
    """Creates a linked list from a list of integers."""
    dummy = ListNode()
    curr = dummy
    for v in nums:
        curr.next = ListNode(v)
        curr = curr.next
    return dummy.next


def list_to_array(head: ListNode | None) -> list[int]:
    """Converts a linked list back to a Python list for easy comparison."""
    result = []
    while head is not None:
        result.append(head.val)
        head = head.next
    return result


def build_lists(arrays: list[list[int]]) -> list[ListNode | None]:
    """Creates an array of linked lists from a list of integer arrays."""
    return [build_list(arr) for arr in arrays]


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"  ✅ PASS: {name}")
            passed += 1
        else:
            print(f"  ❌ FAIL: {name}")
            print(f"    Got:      {got}")
            print(f"    Expected: {expected}")
            failed += 1

    # --- Test Divide and Conquer (primary solution) ---
    print("Approach 3: Divide and Conquer")

    # Test 1: LeetCode Example 1
    test("Example 1: [[1,4,5],[1,3,4],[2,6]]",
         list_to_array(sol.mergeKLists(build_lists([[1, 4, 5], [1, 3, 4], [2, 6]]))),
         [1, 1, 2, 3, 4, 4, 5, 6])

    # Test 2: Empty input
    test("Example 2: []",
         list_to_array(sol.mergeKLists([])),
         [])

    # Test 3: Single empty list
    test("Example 3: [[]]",
         list_to_array(sol.mergeKLists(build_lists([[]]))),
         [])

    # Test 4: Single list
    test("Single list: [[1,2,3]]",
         list_to_array(sol.mergeKLists(build_lists([[1, 2, 3]]))),
         [1, 2, 3])

    # Test 5: Two lists
    test("Two lists: [[1,3,5],[2,4,6]]",
         list_to_array(sol.mergeKLists(build_lists([[1, 3, 5], [2, 4, 6]]))),
         [1, 2, 3, 4, 5, 6])

    # Test 6: Lists with duplicates
    test("Duplicates: [[1,1],[1,1]]",
         list_to_array(sol.mergeKLists(build_lists([[1, 1], [1, 1]]))),
         [1, 1, 1, 1])

    # Test 7: Negative values
    test("Negative values: [[-3,-1],[0,2],[-2,1]]",
         list_to_array(sol.mergeKLists(build_lists([[-3, -1], [0, 2], [-2, 1]]))),
         [-3, -2, -1, 0, 1, 2])

    # Test 8: Multiple empty lists
    test("Multiple empty: [[], [], []]",
         list_to_array(sol.mergeKLists(build_lists([[], [], []]))),
         [])

    # Test 9: Mixed empty and non-empty
    test("Mixed: [[], [1], [], [2,3]]",
         list_to_array(sol.mergeKLists(build_lists([[], [1], [], [2, 3]]))),
         [1, 2, 3])

    # --- Test Heap approach ---
    print("\nApproach 2: Min Heap")

    test("Heap: Example 1",
         list_to_array(sol.mergeKListsHeap(build_lists([[1, 4, 5], [1, 3, 4], [2, 6]]))),
         [1, 1, 2, 3, 4, 4, 5, 6])

    test("Heap: Empty input",
         list_to_array(sol.mergeKListsHeap([])),
         [])

    test("Heap: Negative values",
         list_to_array(sol.mergeKListsHeap(build_lists([[-3, -1], [0, 2], [-2, 1]]))),
         [-3, -2, -1, 0, 1, 2])

    # --- Test Brute Force ---
    print("\nApproach 1: Brute Force")

    test("Brute: Example 1",
         list_to_array(sol.mergeKListsBrute(build_lists([[1, 4, 5], [1, 3, 4], [2, 6]]))),
         [1, 1, 2, 3, 4, 4, 5, 6])

    test("Brute: Empty input",
         list_to_array(sol.mergeKListsBrute([])),
         [])

    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
