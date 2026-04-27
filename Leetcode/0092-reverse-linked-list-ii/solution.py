from typing import Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    def reverseBetween(self, head: Optional[ListNode], left: int, right: int) -> Optional[ListNode]:
        """Time O(n), Space O(1)."""
        dummy = ListNode(0, head)
        prev = dummy
        for _ in range(left - 1):
            prev = prev.next
        cur = prev.next
        for _ in range(right - left):
            nxt = cur.next
            cur.next = nxt.next
            nxt.next = prev.next
            prev.next = nxt
        return dummy.next


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
        ("Example 1", [1, 2, 3, 4, 5], 2, 4, [1, 4, 3, 2, 5]),
        ("Example 2", [5], 1, 1, [5]),
        ("Reverse all", [1, 2, 3], 1, 3, [3, 2, 1]),
        ("left==right", [1, 2, 3], 2, 2, [1, 2, 3]),
        ("Reverse from start", [1, 2, 3, 4], 1, 2, [2, 1, 3, 4]),
        ("Reverse to end", [1, 2, 3, 4], 3, 4, [1, 2, 4, 3]),
        ("Two nodes", [1, 2], 1, 2, [2, 1]),
    ]
    for n, a, l, r, exp in cases: test(n, to_arr(sol.reverseBetween(to_list(a), l, r)), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
