from typing import List
import copy

# ============================================================
# 0056. Merge Intervals
# https://leetcode.com/problems/merge-intervals/
# Difficulty: Medium
# Tags: Array, Sorting
# ============================================================


class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        """
        Optimal Solution (Sort + Sweep).
        Time:  O(n log n)
        Space: O(n)
        """
        if not intervals:
            return []
        items = sorted(([iv[0], iv[1]] for iv in intervals), key=lambda x: x[0])
        result: List[List[int]] = []
        for s, e in items:
            if not result or result[-1][1] < s:
                result.append([s, e])
            else:
                result[-1][1] = max(result[-1][1], e)
        return result

    def mergeSweep(self, intervals: List[List[int]]) -> List[List[int]]:
        """Sweep Line. Time O(n log n), Space O(n)."""
        events = []
        for s, e in intervals:
            events.append((s, 0, +1))
            events.append((e, 1, -1))
        events.sort()
        result, cur, start = [], 0, 0
        for pos, _, delta in events:
            if cur == 0 and delta == +1:
                start = pos
            cur += delta
            if cur == 0:
                result.append([start, pos])
        return result

    def mergeBrute(self, intervals: List[List[int]]) -> List[List[int]]:
        """Pairwise merge. Time O(n^3), Space O(n)."""
        items = [list(x) for x in intervals]
        changed = True
        while changed:
            changed = False
            i = 0
            while i < len(items):
                j = i + 1
                while j < len(items):
                    a, b = items[i]
                    c, d = items[j]
                    if a <= d and c <= b:
                        items[i] = [min(a, c), max(b, d)]
                        items.pop(j)
                        changed = True
                    else:
                        j += 1
                i += 1
        items.sort(key=lambda x: x[0])
        return items


# ============================================================
# Test Cases
# ============================================================

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
        ("Example 1", [[1, 3], [2, 6], [8, 10], [15, 18]], [[1, 6], [8, 10], [15, 18]]),
        ("Touching endpoints", [[1, 4], [4, 5]], [[1, 5]]),
        ("Single interval", [[1, 4]], [[1, 4]]),
        ("Disjoint", [[1, 2], [3, 4]], [[1, 2], [3, 4]]),
        ("Contained", [[1, 10], [2, 3]], [[1, 10]]),
        ("Identical", [[1, 1], [1, 1]], [[1, 1]]),
        ("Reverse sorted", [[4, 5], [1, 3]], [[1, 3], [4, 5]]),
        ("Two chains", [[1, 3], [2, 4], [6, 8], [7, 9]], [[1, 4], [6, 9]]),
        ("Zero-length disjoint", [[1, 1], [2, 2]], [[1, 1], [2, 2]]),
        ("Large containment", [[1, 100], [2, 3], [4, 50]], [[1, 100]]),
        ("Mixed order", [[2, 6], [1, 3], [15, 18], [8, 10]],
         [[1, 6], [8, 10], [15, 18]]),
    ]

    print("=== Sort + Merge ===")
    for name, inp, exp in cases:
        test(name, sol.merge(copy.deepcopy(inp)), exp)

    print("\n=== Sweep Line ===")
    for name, inp, exp in cases:
        test("Sweep " + name, sol.mergeSweep(copy.deepcopy(inp)), exp)

    print("\n=== Brute Force ===")
    for name, inp, exp in cases:
        test("Brute " + name, sol.mergeBrute(copy.deepcopy(inp)), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
