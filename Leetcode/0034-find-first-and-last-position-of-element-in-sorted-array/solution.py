# ============================================================
# 0034. Find First and Last Position of Element in Sorted Array
# https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
# Difficulty: Medium
# Tags: Array, Binary Search
# ============================================================


class Solution:
    def searchRange(self, nums: list[int], target: int) -> list[int]:
        """
        Optimal Solution (Two Binary Searches)
        Approach: Find left bound then right bound using modified binary search
        Time:  O(log n) — two binary searches, each O(log n)
        Space: O(1) — only a few variables
        """
        left = self.find_left(nums, target)
        if left == -1:
            return [-1, -1]
        right = self.find_right(nums, target)
        return [left, right]

    def find_left(self, nums: list[int], target: int) -> int:
        """Find the first (leftmost) occurrence of target."""
        lo, hi = 0, len(nums) - 1
        result = -1

        while lo <= hi:
            mid = lo + (hi - lo) // 2
            if nums[mid] == target:
                result = mid
                hi = mid - 1  # keep searching left
            elif nums[mid] < target:
                lo = mid + 1
            else:
                hi = mid - 1

        return result

    def find_right(self, nums: list[int], target: int) -> int:
        """Find the last (rightmost) occurrence of target."""
        lo, hi = 0, len(nums) - 1
        result = -1

        while lo <= hi:
            mid = lo + (hi - lo) // 2
            if nums[mid] == target:
                result = mid
                lo = mid + 1  # keep searching right
            elif nums[mid] < target:
                lo = mid + 1
            else:
                hi = mid - 1

        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Two Binary Searches (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1", sol.searchRange([5, 7, 7, 8, 8, 10], 8), [3, 4])

    # Test 2: LeetCode Example 2
    test("Example 2", sol.searchRange([5, 7, 7, 8, 8, 10], 6), [-1, -1])

    # Test 3: LeetCode Example 3
    test("Example 3 (empty)", sol.searchRange([], 0), [-1, -1])

    # Test 4: Single element found
    test("Single element found", sol.searchRange([1], 1), [0, 0])

    # Test 5: Single element not found
    test("Single element not found", sol.searchRange([1], 2), [-1, -1])

    # Test 6: All same elements
    test("All same elements", sol.searchRange([8, 8, 8, 8, 8], 8), [0, 4])

    # Test 7: Target at the beginning
    test("Target at start", sol.searchRange([1, 1, 2, 3, 4], 1), [0, 1])

    # Test 8: Target at the end
    test("Target at end", sol.searchRange([1, 2, 3, 4, 4], 4), [3, 4])

    # Test 9: Target smaller than all
    test("Target smaller than all", sol.searchRange([2, 3, 4], 1), [-1, -1])

    # Test 10: Target larger than all
    test("Target larger than all", sol.searchRange([2, 3, 4], 5), [-1, -1])

    # Test 11: Single occurrence in the middle
    test("Single occurrence", sol.searchRange([1, 2, 3, 4, 5], 3), [2, 2])

    # Test 12: Large run of duplicates
    test("Large duplicates", sol.searchRange([1, 2, 2, 2, 2, 2, 3], 2), [1, 5])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
