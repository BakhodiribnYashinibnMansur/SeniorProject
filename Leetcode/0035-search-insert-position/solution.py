# ============================================================
# 0035. Search Insert Position
# https://leetcode.com/problems/search-insert-position/
# Difficulty: Easy
# Tags: Array, Binary Search
# ============================================================


class Solution:
    def searchInsert(self, nums: list[int], target: int) -> int:
        """
        Optimal Solution (Binary Search)
        Approach: Binary search for target; if not found, left pointer = insert position
        Time:  O(log n) — halves the search space each step
        Space: O(1) — only three variables: left, right, mid
        """
        left, right = 0, len(nums) - 1

        while left <= right:
            # Avoid integer overflow (not an issue in Python, but good practice)
            mid = left + (right - left) // 2

            if nums[mid] == target:
                return mid
            elif nums[mid] < target:
                left = mid + 1
            else:
                right = mid - 1

        # left is the correct insert position
        return left


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

    print("=== Binary Search (Optimal) ===")

    # Test 1: LeetCode Example 1 — target found
    test("Example 1", sol.searchInsert([1, 3, 5, 6], 5), 2)

    # Test 2: LeetCode Example 2 — insert in middle
    test("Example 2", sol.searchInsert([1, 3, 5, 6], 2), 1)

    # Test 3: LeetCode Example 3 — insert after all
    test("Example 3", sol.searchInsert([1, 3, 5, 6], 7), 4)

    # Test 4: LeetCode Example 4 — insert before all
    test("Example 4", sol.searchInsert([1, 3, 5, 6], 0), 0)

    # Test 5: Single element — found
    test("Single element found", sol.searchInsert([1], 1), 0)

    # Test 6: Single element — insert before
    test("Single element insert before", sol.searchInsert([5], 3), 0)

    # Test 7: Single element — insert after
    test("Single element insert after", sol.searchInsert([5], 8), 1)

    # Test 8: Target at first index
    test("Target at start", sol.searchInsert([1, 3, 5, 7, 9], 1), 0)

    # Test 9: Target at last index
    test("Target at end", sol.searchInsert([1, 3, 5, 7, 9], 9), 4)

    # Test 10: Insert between middle elements
    test("Insert in middle", sol.searchInsert([1, 3, 5, 7, 9], 4), 2)

    # Test 11: Large array — target found
    test("Large array found", sol.searchInsert(list(range(0, 10000, 2)), 500), 250)

    # Test 12: Large array — target not found
    test("Large array insert", sol.searchInsert(list(range(0, 10000, 2)), 501), 251)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
