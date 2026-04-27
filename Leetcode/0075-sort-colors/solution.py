from typing import List

# ============================================================
# 0075. Sort Colors
# https://leetcode.com/problems/sort-colors/
# Difficulty: Medium
# Tags: Array, Two Pointers, Sorting
# ============================================================


class Solution:
    def sortColors(self, nums: List[int]) -> None:
        """
        Optimal Solution (Dutch National Flag).
        Time:  O(n)
        Space: O(1)
        """
        low, mid, high = 0, 0, len(nums) - 1
        while mid <= high:
            if nums[mid] == 0:
                nums[low], nums[mid] = nums[mid], nums[low]
                low += 1; mid += 1
            elif nums[mid] == 1:
                mid += 1
            else:
                nums[mid], nums[high] = nums[high], nums[mid]
                high -= 1

    def sortColorsCount(self, nums: List[int]) -> None:
        c = [0, 0, 0]
        for x in nums: c[x] += 1
        i = 0
        for v in range(3):
            for _ in range(c[v]):
                nums[i] = v
                i += 1


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        ("Example 1", [2, 0, 2, 1, 1, 0], [0, 0, 1, 1, 2, 2]),
        ("Example 2", [2, 0, 1], [0, 1, 2]),
        ("All zeros", [0, 0, 0], [0, 0, 0]),
        ("All ones", [1, 1, 1], [1, 1, 1]),
        ("All twos", [2, 2, 2], [2, 2, 2]),
        ("Already sorted", [0, 1, 2], [0, 1, 2]),
        ("Reverse sorted", [2, 1, 0], [0, 1, 2]),
        ("Single element 0", [0], [0]),
        ("Single element 2", [2], [2]),
        ("Long mix", [0, 1, 2, 0, 1, 2, 0, 1, 2], [0, 0, 0, 1, 1, 1, 2, 2, 2]),
        ("Two zeros one one", [1, 0, 0], [0, 0, 1]),
    ]

    print("=== Dutch National Flag ===")
    for n, a, exp in cases:
        b = a.copy(); sol.sortColors(b); test(n, b, exp)
    print("\n=== Counting Sort ===")
    for n, a, exp in cases:
        b = a.copy(); sol.sortColorsCount(b); test("Count " + n, b, exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
