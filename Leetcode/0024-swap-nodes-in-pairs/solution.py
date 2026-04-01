# ============================================================
# 0024. Swap Nodes in Pairs
# https://leetcode.com/problems/swap-nodes-in-pairs/
# Difficulty: Medium
# Tags: Linked List, Recursion
# ============================================================

from __future__ import annotations


class ListNode:
    """Singly-linked list node."""
    def __init__(self, val: int = 0, next: ListNode | None = None):
        self.val = val
        self.next = next


class Solution:
    def swapPairs(self, head: ListNode | None) -> ListNode | None:
        """
        Optimal Solution (Iterative with Dummy Node)
        Approach: Use a dummy node; for each pair, rewire pointers
        Time:  O(n) — single pass through the list
        Space: O(1) — only constant extra pointers
        """
        dummy = ListNode(0)
        dummy.next = head
        prev = dummy

        while prev.next and prev.next.next:
            first = prev.next
            second = prev.next.next

            # Rewire pointers
            first.next = second.next
            second.next = first
            prev.next = second

            # Advance past the swapped pair
            prev = first

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


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Standard even-length list
    test("[1,2,3,4] → [2,1,4,3]",
         list_to_array(sol.swapPairs(build_list([1, 2, 3, 4]))),
         [2, 1, 4, 3])

    # Test 2: Empty list
    test("[] → []",
         list_to_array(sol.swapPairs(build_list([]))),
         [])

    # Test 3: Single node
    test("[1] → [1]",
         list_to_array(sol.swapPairs(build_list([1]))),
         [1])

    # Test 4: Two nodes
    test("[1,2] → [2,1]",
         list_to_array(sol.swapPairs(build_list([1, 2]))),
         [2, 1])

    # Test 5: Odd-length list
    test("[1,2,3] → [2,1,3]",
         list_to_array(sol.swapPairs(build_list([1, 2, 3]))),
         [2, 1, 3])

    # Test 6: Longer even-length list
    test("[1,2,3,4,5,6] → [2,1,4,3,6,5]",
         list_to_array(sol.swapPairs(build_list([1, 2, 3, 4, 5, 6]))),
         [2, 1, 4, 3, 6, 5])

    # Test 7: Longer odd-length list
    test("[1,2,3,4,5] → [2,1,4,3,5]",
         list_to_array(sol.swapPairs(build_list([1, 2, 3, 4, 5]))),
         [2, 1, 4, 3, 5])

    # Test 8: All same values
    test("[5,5,5,5] → [5,5,5,5]",
         list_to_array(sol.swapPairs(build_list([5, 5, 5, 5]))),
         [5, 5, 5, 5])

    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
