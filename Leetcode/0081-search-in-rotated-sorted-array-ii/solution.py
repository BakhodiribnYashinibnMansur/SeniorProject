from typing import List


class Solution:
    def search(self, nums: List[int], target: int) -> bool:
        """Time O(log n) avg, O(n) worst. Space O(1)."""
        lo, hi = 0, len(nums) - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if nums[mid] == target: return True
            if nums[lo] == nums[mid] == nums[hi]:
                lo += 1; hi -= 1
            elif nums[lo] <= nums[mid]:
                if nums[lo] <= target < nums[mid]: hi = mid - 1
                else: lo = mid + 1
            else:
                if nums[mid] < target <= nums[hi]: lo = mid + 1
                else: hi = mid - 1
        return False


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        ("Example 1", [2, 5, 6, 0, 0, 1, 2], 0, True),
        ("Example 2", [2, 5, 6, 0, 0, 1, 2], 3, False),
        ("Single match", [5], 5, True),
        ("Single miss", [5], 6, False),
        ("All duplicates match", [1, 1, 1, 1], 1, True),
        ("All duplicates miss", [1, 1, 1, 1], 2, False),
        ("Not rotated", [1, 2, 3, 4], 3, True),
        ("Pivot at start", [4, 5, 6, 1, 2, 3], 1, True),
        ("Edges first", [4, 5, 6, 7, 0, 1, 2], 4, True),
        ("Edges last", [4, 5, 6, 7, 0, 1, 2], 2, True),
        ("Tricky duplicates", [1, 0, 1, 1, 1], 0, True),
        ("Tricky duplicates miss", [1, 0, 1, 1, 1], 2, False),
    ]
    for n, ns, t, exp in cases: test(n, sol.search(ns, t), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
