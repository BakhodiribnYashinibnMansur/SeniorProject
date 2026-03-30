# ============================================================
# 0015. 3Sum
# https://leetcode.com/problems/3sum/
# Difficulty: Medium
# Tags: Array, Two Pointers, Sorting
# ============================================================


class Solution:
    def threeSum(self, nums: list[int]) -> list[list[int]]:
        """
        Optimal Solution (Sort + Two Pointers)
        Approach: Sort, fix one element, use Two Pointers for the remaining two
        Time:  O(n^2) — outer loop O(n) * inner two-pointer scan O(n)
        Space: O(1)   — sorting in place, output not counted
        """
        # 1. Sort the array — enables Two Pointers and duplicate skipping
        nums.sort()
        n = len(nums)
        result = []

        # 2. Fix the first element (i), then find two elements that sum to -nums[i]
        for i in range(n - 2):
            # Skip duplicate values for the first element
            if i > 0 and nums[i] == nums[i - 1]:
                continue

            # Early termination: if smallest value > 0, no triplet can sum to 0
            if nums[i] > 0:
                break

            # Two Pointers for the remaining subarray
            left, right = i + 1, n - 1
            target = -nums[i]

            while left < right:
                total = nums[left] + nums[right]

                if total == target:
                    # Found a valid triplet
                    result.append([nums[i], nums[left], nums[right]])

                    # Skip duplicates for the second element
                    while left < right and nums[left] == nums[left + 1]:
                        left += 1
                    # Skip duplicates for the third element
                    while left < right and nums[right] == nums[right - 1]:
                        right -= 1

                    # Move both pointers inward
                    left += 1
                    right -= 1
                elif total < target:
                    left += 1   # Need a larger sum
                else:
                    right -= 1  # Need a smaller sum

        return result

    def threeSumBruteForce(self, nums: list[int]) -> list[list[int]]:
        """
        Brute Force approach
        Approach: Check all possible triplets with three nested loops
        Time:  O(n^3) — three nested loops
        Space: O(n)   — set for deduplication
        """
        nums.sort()
        n = len(nums)
        result = []
        seen = set()

        for i in range(n - 2):
            for j in range(i + 1, n - 1):
                for k in range(j + 1, n):
                    if nums[i] + nums[j] + nums[k] == 0:
                        triplet = (nums[i], nums[j], nums[k])
                        if triplet not in seen:
                            seen.add(triplet)
                            result.append([nums[i], nums[j], nums[k]])

        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        # Sort for consistent comparison
        def sort_result(r):
            return sorted([sorted(t) for t in r])

        if sort_result(got) == sort_result(expected):
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Sort + Two Pointers (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1", sol.threeSum([-1, 0, 1, 2, -1, -4]), [[-1, -1, 2], [-1, 0, 1]])

    # Test 2: All zeros
    test("All zeros", sol.threeSum([0, 0, 0]), [[0, 0, 0]])

    # Test 3: No triplet sums to zero
    test("No triplets [0,1,1]", sol.threeSum([0, 1, 1]), [])

    # Test 4: Empty array
    test("Empty array", sol.threeSum([]), [])

    # Test 5: All positive
    test("All positive", sol.threeSum([1, 2, 3, 4, 5]), [])

    # Test 6: All negative
    test("All negative", sol.threeSum([-5, -4, -3, -2, -1]), [])

    # Test 7: Multiple triplets with duplicates
    test("Multiple triplets", sol.threeSum([-2, 0, 1, 1, 2]), [[-2, 0, 2], [-2, 1, 1]])

    # Test 8: Many zeros
    test("Many zeros", sol.threeSum([0, 0, 0, 0, 0]), [[0, 0, 0]])

    # Test 9: Two elements only
    test("Two elements", sol.threeSum([-1, 1]), [])

    print("\n=== Brute Force ===")

    test("BF: Example 1", sol.threeSumBruteForce([-1, 0, 1, 2, -1, -4]), [[-1, -1, 2], [-1, 0, 1]])
    test("BF: All zeros", sol.threeSumBruteForce([0, 0, 0]), [[0, 0, 0]])
    test("BF: No triplets", sol.threeSumBruteForce([0, 1, 1]), [])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
