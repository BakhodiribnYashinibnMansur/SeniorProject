# ============================================================
# 0019. Remove Nth Node From End of List
# https://leetcode.com/problems/remove-nth-node-from-end-of-list/
# Difficulty: Medium
# Tags: Linked List, Two Pointers
# ============================================================

from __future__ import annotations


class ListNode:
    """Singly-linked list node."""
    def __init__(self, val: int = 0, next: ListNode | None = None):
        self.val = val
        self.next = next


class Solution:
    def removeNthFromEndTwoPass(self, head: ListNode | None, n: int) -> ListNode | None:
        """
        Approach 1: Two-Pass (Count Length, Then Remove)
        First count total length, then find the (length - n)th node to remove
        Time:  O(L) — two passes through the list
        Space: O(1) — only constant extra space
        """
        # Dummy node handles edge case of removing the head
        dummy = ListNode(0, head)

        # First pass: count total length
        length = 0
        curr = head
        while curr is not None:
            length += 1
            curr = curr.next

        # Second pass: advance to the node just before the target
        curr = dummy
        for _ in range(length - n):
            curr = curr.next

        # Remove the target node by skipping it
        curr.next = curr.next.next

        return dummy.next

    def removeNthFromEnd(self, head: ListNode | None, n: int) -> ListNode | None:
        """
        Approach 2: One-Pass Two Pointers (Optimal)
        Move fast pointer n+1 steps ahead, then move both until fast reaches end
        Time:  O(L) — single pass through the list
        Space: O(1) — only constant extra space
        """
        # Dummy node handles edge case of removing the head
        dummy = ListNode(0, head)
        fast = dummy
        slow = dummy

        # Move fast pointer n+1 steps ahead
        for _ in range(n + 1):
            fast = fast.next

        # Move both pointers until fast reaches the end
        while fast is not None:
            fast = fast.next
            slow = slow.next

        # Remove the target node by skipping it
        slow.next = slow.next.next

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

    # Test 1: Basic case — remove 2nd from end of [1,2,3,4,5]
    test("Remove 2nd from end of [1,2,3,4,5]",
         list_to_array(sol.removeNthFromEnd(build_list([1, 2, 3, 4, 5]), 2)),
         [1, 2, 3, 5])

    # Test 2: Single element — remove 1st from end of [1]
    test("Remove 1st from end of [1]",
         list_to_array(sol.removeNthFromEnd(build_list([1]), 1)),
         [])

    # Test 3: Two elements — remove 1st from end of [1,2]
    test("Remove 1st from end of [1,2]",
         list_to_array(sol.removeNthFromEnd(build_list([1, 2]), 1)),
         [1])

    # Test 4: Remove head — remove 2nd from end of [1,2]
    test("Remove head (2nd from end of [1,2])",
         list_to_array(sol.removeNthFromEnd(build_list([1, 2]), 2)),
         [2])

    # Test 5: Remove last element — remove 1st from end of [1,2,3]
    test("Remove last (1st from end of [1,2,3])",
         list_to_array(sol.removeNthFromEnd(build_list([1, 2, 3]), 1)),
         [1, 2])

    # Test 6: Remove first element — remove 5th from end of [1,2,3,4,5]
    test("Remove first (5th from end of [1,2,3,4,5])",
         list_to_array(sol.removeNthFromEnd(build_list([1, 2, 3, 4, 5]), 5)),
         [2, 3, 4, 5])

    # Test 7: Remove middle — remove 3rd from end of [1,2,3,4,5]
    test("Remove middle (3rd from end of [1,2,3,4,5])",
         list_to_array(sol.removeNthFromEnd(build_list([1, 2, 3, 4, 5]), 3)),
         [1, 2, 4, 5])

    # Test 8: Longer list — remove 4th from end of [1,2,3,4,5,6,7]
    test("Remove 4th from end of [1,2,3,4,5,6,7]",
         list_to_array(sol.removeNthFromEnd(build_list([1, 2, 3, 4, 5, 6, 7]), 4)),
         [1, 2, 3, 5, 6, 7])

    # Test 9: Two-pass approach — same basic case
    test("Two-pass: Remove 2nd from end of [1,2,3,4,5]",
         list_to_array(sol.removeNthFromEndTwoPass(build_list([1, 2, 3, 4, 5]), 2)),
         [1, 2, 3, 5])

    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
