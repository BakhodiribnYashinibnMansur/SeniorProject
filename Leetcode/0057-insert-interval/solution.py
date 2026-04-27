from typing import List
import bisect
import copy

# ============================================================
# 0057. Insert Interval
# https://leetcode.com/problems/insert-interval/
# Difficulty: Medium
# Tags: Array
# ============================================================


class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        """
        Optimal Solution (Three-Phase Linear Scan).
        Time:  O(n)
        Space: O(n)
        """
        n = len(intervals)
        result: List[List[int]] = []
        cur = list(newInterval)
        i = 0
        while i < n and intervals[i][1] < cur[0]:
            result.append(list(intervals[i]))
            i += 1
        while i < n and intervals[i][0] <= cur[1]:
            cur[0] = min(cur[0], intervals[i][0])
            cur[1] = max(cur[1], intervals[i][1])
            i += 1
        result.append(cur)
        while i < n:
            result.append(list(intervals[i]))
            i += 1
        return result

    def insertBinary(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        """Binary search boundaries. Time O(n) copy + O(log n) lookup, Space O(n)."""
        n = len(intervals)
        if n == 0:
            return [list(newInterval)]
        ends = [iv[1] for iv in intervals]
        lo = bisect.bisect_left(ends, newInterval[0])
        starts = [iv[0] for iv in intervals]
        hi = bisect.bisect_right(starts, newInterval[1])
        merged_start = newInterval[0]
        merged_end = newInterval[1]
        if lo < hi:
            merged_start = min(merged_start, intervals[lo][0])
            merged_end = max(merged_end, intervals[hi - 1][1])
        result = [list(intervals[k]) for k in range(lo)]
        result.append([merged_start, merged_end])
        result.extend([list(intervals[k]) for k in range(hi, n)])
        return result

    def insertReMerge(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        """Append + sort + merge. Time O(n log n), Space O(n)."""
        items = sorted([list(x) for x in intervals] + [list(newInterval)],
                       key=lambda x: x[0])
        result: List[List[int]] = []
        for s, e in items:
            if not result or result[-1][1] < s:
                result.append([s, e])
            else:
                result[-1][1] = max(result[-1][1], e)
        return result


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
        ("Example 1", [[1, 3], [6, 9]], [2, 5], [[1, 5], [6, 9]]),
        ("Example 2", [[1, 2], [3, 5], [6, 7], [8, 10], [12, 16]], [4, 8],
         [[1, 2], [3, 10], [12, 16]]),
        ("Empty input", [], [5, 7], [[5, 7]]),
        ("Insert at start no overlap", [[3, 5]], [1, 2], [[1, 2], [3, 5]]),
        ("Insert at end no overlap", [[1, 2]], [4, 5], [[1, 2], [4, 5]]),
        ("Insert in middle no overlap", [[1, 2], [6, 7]], [3, 4],
         [[1, 2], [3, 4], [6, 7]]),
        ("Engulfs everything", [[1, 2], [5, 6]], [0, 10], [[0, 10]]),
        ("Touching merge left", [[1, 4]], [4, 6], [[1, 6]]),
        ("Touching merge right", [[4, 6]], [1, 4], [[1, 6]]),
        ("Zero-length new", [[1, 5]], [3, 3], [[1, 5]]),
        ("Insert before all touching", [[4, 6], [7, 9]], [0, 4],
         [[0, 6], [7, 9]]),
        ("Single existing absorbed", [[2, 3]], [1, 5], [[1, 5]]),
    ]

    print("=== Three-Phase Linear ===")
    for name, ivs, ni, exp in cases:
        test(name, sol.insert(copy.deepcopy(ivs), list(ni)), exp)

    print("\n=== Binary Search Boundaries ===")
    for name, ivs, ni, exp in cases:
        test("Binary " + name, sol.insertBinary(copy.deepcopy(ivs), list(ni)), exp)

    print("\n=== Re-Merge ===")
    for name, ivs, ni, exp in cases:
        test("ReMerge " + name, sol.insertReMerge(copy.deepcopy(ivs), list(ni)), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
