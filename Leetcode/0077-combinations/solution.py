from typing import List
import math

# ============================================================
# 0077. Combinations
# https://leetcode.com/problems/combinations/
# Difficulty: Medium
# Tags: Backtracking
# ============================================================


class Solution:
    def combine(self, n: int, k: int) -> List[List[int]]:
        """
        Optimal Solution (Backtracking with Pruning).
        Time:  O(C(n, k) * k)
        Space: O(k)
        """
        result: List[List[int]] = []
        cur: List[int] = []
        def bt(start: int):
            if len(cur) == k:
                result.append(cur.copy())
                return
            need = k - len(cur)
            for v in range(start, n - need + 2):
                cur.append(v)
                bt(v + 1)
                cur.pop()
        bt(1)
        return result

    def combineIter(self, n: int, k: int) -> List[List[int]]:
        cur = list(range(1, k + 1))
        result = [cur.copy()]
        while True:
            i = k - 1
            while i >= 0 and cur[i] == n - k + 1 + i:
                i -= 1
            if i < 0: break
            cur[i] += 1
            for j in range(i + 1, k):
                cur[j] = cur[j - 1] + 1
            result.append(cur.copy())
        return result


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if sorted(got) == sorted(expected): print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name}"); failed += 1

    cases = [
        ("Example 1", 4, 2, [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]),
        ("Example 2", 1, 1, [[1]]),
        ("k = n", 3, 3, [[1,2,3]]),
        ("k = 1", 3, 1, [[1],[2],[3]]),
        ("n=4 k=3", 4, 3, [[1,2,3],[1,2,4],[1,3,4],[2,3,4]]),
    ]

    print("=== Backtracking + Pruning ===")
    for n, ni, k, exp in cases:
        test(n, sol.combine(ni, k), exp)
    print("\n=== Iterative ===")
    for n, ni, k, exp in cases:
        test("Iter " + n, sol.combineIter(ni, k), exp)

    # Count checks
    for n, k in [(5, 3), (10, 5), (20, 10)]:
        got = len(sol.combine(n, k))
        want = math.comb(n, k)
        if got == want: print(f"PASS: count C({n},{k}) = {got}"); passed += 1
        else: print(f"FAIL: count C({n},{k}) got {got} want {want}"); failed += 1

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
