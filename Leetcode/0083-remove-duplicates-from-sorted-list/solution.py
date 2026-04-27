from typing import Optional, List


class ListNode:
    def __init__(self, val: int = 0, next: Optional['ListNode'] = None):
        self.val = val
        self.next = next


class Solution:
    def deleteDuplicates(self, head: Optional[ListNode]) -> Optional[ListNode]:
        """Time O(n), Space O(1)."""
        cur = head
        while cur and cur.next:
            if cur.next.val == cur.val:
                cur.next = cur.next.next
            else:
                cur = cur.next
        return head


def to_list(arr):
    if not arr: return None
    h = ListNode(arr[0]); c = h
    for v in arr[1:]: c.next = ListNode(v); c = c.next
    return h


def to_arr(h):
    out = []
    while h: out.append(h.val); h = h.next
    return out


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name}"); failed += 1
    cases = [
        ("Example 1", [1, 1, 2], [1, 2]),
        ("Example 2", [1, 1, 2, 3, 3], [1, 2, 3]),
        ("Empty", [], []),
        ("Single", [5], [5]),
        ("All same", [1, 1, 1], [1]),
        ("No dup", [1, 2, 3], [1, 2, 3]),
        ("Long", [0, 0, 0, 1, 2, 2, 3, 3, 3, 4], [0, 1, 2, 3, 4]),
    ]
    for n, a, exp in cases: test(n, to_arr(sol.deleteDuplicates(to_list(a))), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
