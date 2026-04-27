from typing import List

# ============================================================
# 0080. Remove Duplicates from Sorted Array II
# https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/
# Difficulty: Medium
# Tags: Array, Two Pointers
# ============================================================


class Solution:
    def removeDuplicates(self, nums: List[int]) -> int:
        """
        Optimal Solution (Two Pointers, At-Most-K = 2).
        Time:  O(n)
        Space: O(1)
        """
        k = 2
        if len(nums) <= k: return len(nums)
        i = k
        for j in range(k, len(nums)):
            if nums[j] != nums[i - k]:
                nums[i] = nums[j]
                i += 1
        return i


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got_len, got_prefix, want_len, want_prefix):
        global passed, failed
        if got_len == want_len and got_prefix == want_prefix:
            print(f"PASS: {name}"); passed += 1
        else:
            print(f"FAIL: {name} got len={got_len} prefix={got_prefix}"); failed += 1

    cases = [
        ("Example 1", [1, 1, 1, 2, 2, 3], [1, 1, 2, 2, 3]),
        ("Example 2", [0, 0, 1, 1, 1, 1, 2, 3, 3], [0, 0, 1, 1, 2, 3, 3]),
        ("All same", [5, 5, 5, 5, 5], [5, 5]),
        ("All distinct", [1, 2, 3, 4, 5], [1, 2, 3, 4, 5]),
        ("Single", [7], [7]),
        ("Two same", [4, 4], [4, 4]),
        ("Three same", [4, 4, 4], [4, 4]),
        ("Negatives", [-3, -3, -3, 0, 0, 0, 1], [-3, -3, 0, 0, 1]),
        ("Long", [1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 5, 5, 5, 6], [1, 1, 2, 2, 3, 3, 4, 5, 5, 6]),
    ]

    for name, inp, want in cases:
        a = inp.copy()
        k = sol.removeDuplicates(a)
        test(name, k, a[:k], len(want), want)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
