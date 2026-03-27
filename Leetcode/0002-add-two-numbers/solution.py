# ============================================================
# 0002. Add Two Numbers
# https://leetcode.com/problems/add-two-numbers/
# Difficulty: Medium
# Tags: Linked List, Math, Recursion
# ============================================================

from __future__ import annotations


class ListNode:
    """Singly-linked list node."""
    def __init__(self, val: int = 0, next: ListNode | None = None):
        self.val = val
        self.next = next


class Solution:
    def addTwoNumbers(self, l1: ListNode | None, l2: ListNode | None) -> ListNode | None:
        """
        Optimal Solution (Simultaneous Traversal with Carry)
        Approach: Iterate both lists digit by digit, propagate carry forward
        Time:  O(max(m,n)) — traverse both lists once, where m and n are their lengths
        Space: O(max(m,n)) — the result list has at most max(m,n)+1 nodes
        """
        # Dummy head makes it easy to append nodes without a None check
        dummy = ListNode()
        curr = dummy
        carry = 0

        # Continue until both lists are exhausted AND carry is zero
        while l1 is not None or l2 is not None or carry != 0:
            total = carry

            # Add digit from l1 (if available)
            if l1 is not None:
                total += l1.val
                l1 = l1.next

            # Add digit from l2 (if available)
            if l2 is not None:
                total += l2.val
                l2 = l2.next

            # Compute new carry and current digit
            carry, digit = divmod(total, 10)

            # Append new node to result
            curr.next = ListNode(digit)
            curr = curr.next

        # Return the real head (skip dummy)
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

    # Test 1: Basic case — 342 + 465 = 807
    test("342 + 465 = 807",
         list_to_array(sol.addTwoNumbers(build_list([2, 4, 3]), build_list([5, 6, 4]))),
         [7, 0, 8])

    # Test 2: Both zeros
    test("0 + 0 = 0",
         list_to_array(sol.addTwoNumbers(build_list([0]), build_list([0]))),
         [0])

    # Test 3: Carry propagation across all digits — 9999999 + 9999 = 10009998
    test("9999999 + 9999 = 10009998",
         list_to_array(sol.addTwoNumbers(build_list([9, 9, 9, 9, 9, 9, 9]), build_list([9, 9, 9, 9]))),
         [8, 9, 9, 9, 0, 0, 0, 1])

    # Test 4: Single digit addition without carry — 1 + 2 = 3
    test("1 + 2 = 3",
         list_to_array(sol.addTwoNumbers(build_list([1]), build_list([2]))),
         [3])

    # Test 5: Single digit addition with carry — 5 + 5 = 10
    test("5 + 5 = 10",
         list_to_array(sol.addTwoNumbers(build_list([5]), build_list([5]))),
         [0, 1])

    # Test 6: Different lengths — 99 + 1 = 100
    test("99 + 1 = 100",
         list_to_array(sol.addTwoNumbers(build_list([9, 9]), build_list([1]))),
         [0, 0, 1])

    # Test 7: l1 longer than l2 — 123 + 4 = 127
    test("123 + 4 = 127",
         list_to_array(sol.addTwoNumbers(build_list([3, 2, 1]), build_list([4]))),
         [7, 2, 1])

    # Test 8: l2 longer than l1 — 5 + 678 = 683
    test("5 + 678 = 683",
         list_to_array(sol.addTwoNumbers(build_list([5]), build_list([8, 7, 6]))),
         [3, 8, 6])

    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
