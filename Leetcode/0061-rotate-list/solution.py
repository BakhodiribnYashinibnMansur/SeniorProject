from typing import Optional, List

# ============================================================
# 0061. Rotate List
# https://leetcode.com/problems/rotate-list/
# Difficulty: Medium
# Tags: Linked List, Two Pointers
# ============================================================


class ListNode:
    def __init__(self, val: int = 0, next: Optional['ListNode'] = None):
        self.val = val
        self.next = next


class Solution:
    def rotateRight(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        """
        Optimal Solution (Length + Re-Link).
        Time:  O(n)
        Space: O(1)
        """
        if head is None or head.next is None:
            return head
        n, tail = 1, head
        while tail.next:
            tail = tail.next
            n += 1
        k %= n
        if k == 0:
            return head
        new_tail = head
        for _ in range(n - k - 1):
            new_tail = new_tail.next
        new_head = new_tail.next
        new_tail.next = None
        tail.next = head
        return new_head

    def rotateRightCircular(self, head: Optional[ListNode], k: int) -> Optional[ListNode]:
        """Make-circular-then-cut. Time O(n), Space O(1)."""
        if head is None:
            return head
        n, tail = 1, head
        while tail.next:
            tail = tail.next
            n += 1
        tail.next = head
        k %= n
        new_tail = head
        for _ in range(n - k - 1):
            new_tail = new_tail.next
        new_head = new_tail.next
        new_tail.next = None
        return new_head


# ============================================================
# Helpers + Test Cases
# ============================================================

def to_list(arr: List[int]) -> Optional[ListNode]:
    if not arr:
        return None
    head = ListNode(arr[0])
    cur = head
    for v in arr[1:]:
        cur.next = ListNode(v)
        cur = cur.next
    return head


def to_array(head: Optional[ListNode]) -> List[int]:
    out = []
    while head:
        out.append(head.val)
        head = head.next
    return out


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}")
            passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    cases = [
        ("Example 1", [1, 2, 3, 4, 5], 2, [4, 5, 1, 2, 3]),
        ("Example 2", [0, 1, 2], 4, [2, 0, 1]),
        ("Empty list", [], 3, []),
        ("Single node", [5], 10, [5]),
        ("k=0", [1, 2, 3], 0, [1, 2, 3]),
        ("k=n", [1, 2, 3], 3, [1, 2, 3]),
        ("Two nodes k=1", [1, 2], 1, [2, 1]),
        ("k = 2n", [1, 2, 3], 6, [1, 2, 3]),
        ("Big k", [1, 2, 3, 4, 5, 6, 7, 8], 100, [5, 6, 7, 8, 1, 2, 3, 4]),
        ("Negatives in list", [-1, -2, -3], 1, [-3, -1, -2]),
    ]

    print("=== Length + Re-Link ===")
    for name, arr, k, exp in cases:
        test(name, to_array(sol.rotateRight(to_list(arr), k)), exp)

    print("\n=== Make Circular + Cut ===")
    for name, arr, k, exp in cases:
        test("Circ " + name, to_array(sol.rotateRightCircular(to_list(arr), k)), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
