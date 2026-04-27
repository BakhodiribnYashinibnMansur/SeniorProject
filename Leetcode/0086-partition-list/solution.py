from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    def partition(self, head: Optional[ListNode], x: int) -> Optional[ListNode]:
        """Time O(n), Space O(1)."""
        less_dummy = ListNode(0)
        ge_dummy = ListNode(0)
        lt = less_dummy
        gt = ge_dummy
        cur = head
        while cur:
            if cur.val < x:
                lt.next = cur; lt = lt.next
            else:
                gt.next = cur; gt = gt.next
            cur = cur.next
        gt.next = None
        lt.next = ge_dummy.next
        return less_dummy.next


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
    def test(name, got, exp):
        global passed, failed
        if got == exp: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1
    cases = [
        ("Example 1", [1, 4, 3, 2, 5, 2], 3, [1, 2, 2, 4, 3, 5]),
        ("Example 2", [2, 1], 2, [1, 2]),
        ("Empty", [], 5, []),
        ("Single", [5], 5, [5]),
        ("All less", [1, 2, 3], 5, [1, 2, 3]),
        ("All greater", [5, 6, 7], 1, [5, 6, 7]),
        ("All equal", [3, 3, 3], 3, [3, 3, 3]),
        ("Negatives", [-3, 1, -1, 0], 0, [-3, -1, 1, 0]),
    ]
    for n, a, x, exp in cases: test(n, to_arr(sol.partition(to_list(a), x)), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
