from typing import List

# ============================================================
# 0053. Maximum Subarray
# https://leetcode.com/problems/maximum-subarray/
# Difficulty: Medium
# Tags: Array, Divide and Conquer, Dynamic Programming
# ============================================================


class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        """
        Optimal Solution (Kadane's Algorithm).
        Time:  O(n)
        Space: O(1)
        """
        cur = best = nums[0]
        for x in nums[1:]:
            cur = max(x, cur + x)
            best = max(best, cur)
        return best

    def maxSubArrayBrute(self, nums: List[int]) -> int:
        """Brute force double sweep. Time O(n^2), Space O(1)."""
        best = nums[0]
        for i in range(len(nums)):
            s = 0
            for j in range(i, len(nums)):
                s += nums[j]
                if s > best:
                    best = s
        return best

    def maxSubArrayDC(self, nums: List[int]) -> int:
        """Divide and Conquer. Time O(n log n), Space O(log n)."""
        def solve(l: int, r: int) -> int:
            if l == r:
                return nums[l]
            m = (l + r) // 2
            left_max = solve(l, m)
            right_max = solve(m + 1, r)

            best_l, s = nums[m], 0
            for i in range(m, l - 1, -1):
                s += nums[i]
                best_l = max(best_l, s)
            best_r, s = nums[m + 1], 0
            for j in range(m + 1, r + 1):
                s += nums[j]
                best_r = max(best_r, s)

            return max(left_max, right_max, best_l + best_r)

        return solve(0, len(nums) - 1)

    def maxSubArrayPrefix(self, nums: List[int]) -> int:
        """Prefix sum view. Time O(n), Space O(1)."""
        best = nums[0]
        running = 0
        min_prefix = 0
        for x in nums:
            running += x
            best = max(best, running - min_prefix)
            min_prefix = min(min_prefix, running)
        return best


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
        ("Example 1", [-2, 1, -3, 4, -1, 2, 1, -5, 4], 6),
        ("Single positive", [1], 1),
        ("Mostly positive", [5, 4, -1, 7, 8], 23),
        ("Single negative", [-5], -5),
        ("All negative", [-3, -1, -2], -1),
        ("All zeros", [0, 0, 0], 0),
        ("All positive", [1, 2, 3, 4, 5], 15),
        ("Alternating", [-1, 2, -1, 2, -1, 2], 4),
        ("Long sequence with zeros", [0, 0, -1, 0, 0], 0),
        ("Two elements pos/neg", [-1, 2], 2),
        ("Two elements neg/neg", [-1, -2], -1),
        ("Single zero", [0], 0),
        ("Boundary peak", [-2, -3, 4], 4),
    ]

    print("=== Kadane's Algorithm ===")
    for name, nums, exp in cases:
        test(name, sol.maxSubArray(nums), exp)

    print("\n=== Brute Force ===")
    for name, nums, exp in cases:
        test("Brute " + name, sol.maxSubArrayBrute(nums), exp)

    print("\n=== Divide and Conquer ===")
    for name, nums, exp in cases:
        test("DC " + name, sol.maxSubArrayDC(nums), exp)

    print("\n=== Prefix Sum ===")
    for name, nums, exp in cases:
        test("Prefix " + name, sol.maxSubArrayPrefix(nums), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
