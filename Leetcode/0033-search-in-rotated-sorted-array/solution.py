# ============================================================
# 0033. Search in Rotated Sorted Array
# https://leetcode.com/problems/search-in-rotated-sorted-array/
# Difficulty: Medium
# Tags: Array, Binary Search
# ============================================================


class Solution:
    def search(self, nums: list[int], target: int) -> int:
        """
        Modified Binary Search on rotated sorted array
        Approach: Determine which half is sorted, then decide search direction
        Time:  O(log n) — each step halves the search space
        Space: O(1) — only pointer variables
        """
        left, right = 0, len(nums) - 1

        while left <= right:
            mid = left + (right - left) // 2

            # Found the target
            if nums[mid] == target:
                return mid

            # Determine which half is sorted
            if nums[left] <= nums[mid]:
                # Left half [left..mid] is sorted
                if nums[left] <= target < nums[mid]:
                    right = mid - 1  # Target is in the sorted left half
                else:
                    left = mid + 1  # Target is in the right half
            else:
                # Right half [mid..right] is sorted
                if nums[mid] < target <= nums[right]:
                    left = mid + 1  # Target is in the sorted right half
                else:
                    right = mid - 1  # Target is in the left half

        return -1


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Basic case — target in right half
    test("Basic case", sol.search([4, 5, 6, 7, 0, 1, 2], 0), 4)

    # Test 2: Target not in array
    test("Target not found", sol.search([4, 5, 6, 7, 0, 1, 2], 3), -1)

    # Test 3: Single element — not found
    test("Single element not found", sol.search([1], 0), -1)

    # Test 4: Single element — found
    test("Single element found", sol.search([1], 1), 0)

    # Test 5: No rotation
    test("No rotation", sol.search([1, 2, 3, 4, 5], 3), 2)

    # Test 6: Target at first position
    test("Target at first", sol.search([4, 5, 6, 7, 0, 1, 2], 4), 0)

    # Test 7: Target at last position
    test("Target at last", sol.search([4, 5, 6, 7, 0, 1, 2], 2), 6)

    # Test 8: Target at rotation point
    test("Target at pivot", sol.search([6, 7, 0, 1, 2, 4, 5], 0), 2)

    # Test 9: Two elements — rotated
    test("Two elements rotated", sol.search([3, 1], 1), 1)

    # Test 10: Two elements — not found
    test("Two elements not found", sol.search([3, 1], 2), -1)

    # Test 11: Target in left sorted half
    test("Target in left half", sol.search([4, 5, 6, 7, 0, 1, 2], 5), 1)

    # Test 12: Large rotation
    test("Large rotation", sol.search([2, 3, 4, 5, 1], 1), 4)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
