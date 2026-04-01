# ============================================================
# 0021. Merge Two Sorted Lists
# https://leetcode.com/problems/merge-two-sorted-lists/
# Difficulty: Easy
# Tags: Linked List, Recursion
# ============================================================


# Definition for singly-linked list
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    def mergeTwoLists(self, list1, list2):
        """
        Optimal Solution (Iterative)
        Approach: Use a dummy node and two pointers to merge both lists
        Time:  O(n + m) -- each node is visited exactly once
        Space: O(1) -- only constant extra pointers used
        """
        dummy = ListNode(0)
        current = dummy

        while list1 and list2:
            if list1.val <= list2.val:
                current.next = list1
                list1 = list1.next
            else:
                current.next = list2
                list2 = list2.next
            current = current.next

        # Attach remaining nodes
        current.next = list1 if list1 else list2

        return dummy.next


# ============================================================
# Helper Functions
# ============================================================

def list_to_linked(arr):
    """Convert a Python list to a linked list."""
    dummy = ListNode(0)
    current = dummy
    for val in arr:
        current.next = ListNode(val)
        current = current.next
    return dummy.next


def linked_to_list(head):
    """Convert a linked list to a Python list."""
    result = []
    while head:
        result.append(head.val)
        head = head.next
    return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name, list1_arr, list2_arr, expected):
        global passed, failed
        l1 = list_to_linked(list1_arr)
        l2 = list_to_linked(list2_arr)
        result = linked_to_list(sol.mergeTwoLists(l1, l2))
        if result == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {result}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Basic merge
    test("Basic merge", [1, 2, 4], [1, 3, 4], [1, 1, 2, 3, 4, 4])

    # Test 2: Both empty
    test("Both empty", [], [], [])

    # Test 3: First empty
    test("First empty", [], [0], [0])

    # Test 4: Second empty
    test("Second empty", [1], [], [1])

    # Test 5: Single elements
    test("Single elements", [2], [1], [1, 2])

    # Test 6: Non-overlapping ranges
    test("Non-overlapping", [1, 2, 3], [4, 5, 6], [1, 2, 3, 4, 5, 6])

    # Test 7: Equal elements
    test("Equal elements", [1, 1], [1, 1], [1, 1, 1, 1])

    # Test 8: Negative values
    test("Negative values", [-3, -1], [-2, 0], [-3, -2, -1, 0])

    # Test 9: One much longer
    test("One much longer", [1], [2, 3, 4, 5], [1, 2, 3, 4, 5])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
