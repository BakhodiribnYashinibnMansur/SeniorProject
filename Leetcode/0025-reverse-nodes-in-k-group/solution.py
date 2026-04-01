# ============================================================
# 0025. Reverse Nodes in k-Group
# https://leetcode.com/problems/reverse-nodes-in-k-group/
# Difficulty: Hard
# Tags: Linked List, Recursion
# ============================================================

from __future__ import annotations


class ListNode:
    """Singly-linked list node."""
    def __init__(self, val: int = 0, next: ListNode | None = None):
        self.val = val
        self.next = next


class Solution:
    def reverseKGroup(self, head: ListNode | None, k: int) -> ListNode | None:
        """
        Approach 1: Iterative
        Process list in groups of k, reversing each group in-place.
        Time:  O(n) — each node is visited at most twice
        Space: O(1) — only constant extra pointers
        """
        dummy = ListNode(0, head)
        groupPrev = dummy

        while True:
            # Check if k nodes remain
            kth = self._getKthNode(groupPrev, k)
            if not kth:
                break

            groupNext = kth.next

            # Reverse k nodes in this group
            prev, curr = kth.next, groupPrev.next
            while curr != groupNext:
                tmp = curr.next
                curr.next = prev
                prev = curr
                curr = tmp

            # Reconnect: groupPrev -> new head (kth), old head -> groupPrev for next iteration
            tmp = groupPrev.next   # original first node, now the tail
            groupPrev.next = kth   # point to new head of reversed group
            groupPrev = tmp        # advance to the tail of the reversed group

        return dummy.next

    def _getKthNode(self, node: ListNode | None, k: int) -> ListNode | None:
        """Returns the kth node after the given node, or None if fewer than k nodes remain."""
        while node and k > 0:
            node = node.next
            k -= 1
        return node

    def reverseKGroupRecursive(self, head: ListNode | None, k: int) -> ListNode | None:
        """
        Approach 2: Recursive
        Reverse first k nodes, recurse on the rest, connect them.
        Time:  O(n) — each node is visited at most twice
        Space: O(n/k) — recursion stack depth equals number of groups
        """
        # Check if k nodes exist
        node = head
        count = 0
        while node and count < k:
            node = node.next
            count += 1

        if count < k:
            return head  # not enough nodes, leave as is

        # Reverse first k nodes
        prev, curr = None, head
        for _ in range(k):
            nxt = curr.next
            curr.next = prev
            prev = curr
            curr = nxt

        # head is now the tail of the reversed group — connect to recursion result
        head.next = self.reverseKGroupRecursive(curr, k)

        return prev  # prev is the new head of the reversed group


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
            print(f"  \u2705 PASS: {name}")
            passed += 1
        else:
            print(f"  \u274c FAIL: {name}")
            print(f"    Got:      {got}")
            print(f"    Expected: {expected}")
            failed += 1

    # --- Iterative approach tests ---
    print("Approach 1: Iterative")

    test("[1,2,3,4,5] k=2 -> [2,1,4,3,5]",
         list_to_array(sol.reverseKGroup(build_list([1, 2, 3, 4, 5]), 2)),
         [2, 1, 4, 3, 5])

    test("[1,2,3,4,5] k=3 -> [3,2,1,4,5]",
         list_to_array(sol.reverseKGroup(build_list([1, 2, 3, 4, 5]), 3)),
         [3, 2, 1, 4, 5])

    test("[1,2,3,4,5] k=1 -> [1,2,3,4,5]",
         list_to_array(sol.reverseKGroup(build_list([1, 2, 3, 4, 5]), 1)),
         [1, 2, 3, 4, 5])

    test("[1] k=1 -> [1]",
         list_to_array(sol.reverseKGroup(build_list([1]), 1)),
         [1])

    test("[1,2] k=2 -> [2,1]",
         list_to_array(sol.reverseKGroup(build_list([1, 2]), 2)),
         [2, 1])

    test("[1,2,3,4] k=2 -> [2,1,4,3]",
         list_to_array(sol.reverseKGroup(build_list([1, 2, 3, 4]), 2)),
         [2, 1, 4, 3])

    test("[1,2,3,4,5] k=5 -> [5,4,3,2,1]",
         list_to_array(sol.reverseKGroup(build_list([1, 2, 3, 4, 5]), 5)),
         [5, 4, 3, 2, 1])

    test("[1,2,3] k=3 -> [3,2,1]",
         list_to_array(sol.reverseKGroup(build_list([1, 2, 3]), 3)),
         [3, 2, 1])

    # --- Recursive approach tests ---
    print("\nApproach 2: Recursive")

    test("[1,2,3,4,5] k=2 -> [2,1,4,3,5]",
         list_to_array(sol.reverseKGroupRecursive(build_list([1, 2, 3, 4, 5]), 2)),
         [2, 1, 4, 3, 5])

    test("[1,2,3,4,5] k=3 -> [3,2,1,4,5]",
         list_to_array(sol.reverseKGroupRecursive(build_list([1, 2, 3, 4, 5]), 3)),
         [3, 2, 1, 4, 5])

    test("[1,2,3,4,5] k=1 -> [1,2,3,4,5]",
         list_to_array(sol.reverseKGroupRecursive(build_list([1, 2, 3, 4, 5]), 1)),
         [1, 2, 3, 4, 5])

    test("[1] k=1 -> [1]",
         list_to_array(sol.reverseKGroupRecursive(build_list([1]), 1)),
         [1])

    test("[1,2,3,4,5] k=5 -> [5,4,3,2,1]",
         list_to_array(sol.reverseKGroupRecursive(build_list([1, 2, 3, 4, 5]), 5)),
         [5, 4, 3, 2, 1])

    test("[1,2,3,4] k=2 -> [2,1,4,3]",
         list_to_array(sol.reverseKGroupRecursive(build_list([1, 2, 3, 4]), 2)),
         [2, 1, 4, 3])

    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
