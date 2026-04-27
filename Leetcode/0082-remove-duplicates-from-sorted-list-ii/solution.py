from typing import Optional, List


class ListNode:
    def __init__(self, val: int = 0, next: Optional['ListNode'] = None):
        self.val = val
        self.next = next


class Solution:
    def deleteDuplicates(self, head: Optional[ListNode]) -> Optional[ListNode]:
        """Time O(n), Space O(1)."""
        dummy = ListNode(0, head)
        prev = dummy
        cur = head
        while cur:
            if cur.next and cur.next.val == cur.val:
                v = cur.val
                while cur and cur.val == v:
                    cur = cur.next
                prev.next = cur
            else:
                prev = cur
                cur = cur.next
        return dummy.next


def to_list(arr):
    if not arr: return None
    head = ListNode(arr[0])
    cur = head
    for v in arr[1:]:
        cur.next = ListNode(v)
        cur = cur.next
    return head


def to_arr(h):
    out = []
    while h:
        out.append(h.val)
        h = h.next
    return out


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        ("Example 1", [1, 2, 3, 3, 4, 4, 5], [1, 2, 5]),
        ("Example 2", [1, 1, 1, 2, 3], [2, 3]),
        ("Empty", [], []),
        ("All duplicates", [1, 1, 1, 1], []),
        ("No duplicates", [1, 2, 3, 4], [1, 2, 3, 4]),
        ("Single", [5], [5]),
        ("Duplicate tail", [1, 2, 3, 3], [1, 2]),
        ("Multiple runs", [1, 1, 2, 3, 3, 4, 5, 5], [2, 4]),
        ("Negatives", [-2, -1, -1, 0, 1], [-2, 0, 1]),
    ]
    for n, a, exp in cases: test(n, to_arr(sol.deleteDuplicates(to_list(a))), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
