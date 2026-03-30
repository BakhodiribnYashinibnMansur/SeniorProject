# ============================================================
# 0018. 4Sum
# https://leetcode.com/problems/4sum/
# Difficulty: Medium
# Tags: Array, Two Pointers, Sorting
# ============================================================


class Solution:
    def fourSum(self, nums: list[int], target: int) -> list[list[int]]:
        """
        Optimal Solution (Sort + Two Pointers)
        Approach: Sort, fix two elements (i, j), use Two Pointers for the remaining two
        Time:  O(n^3) — two outer loops O(n^2) * inner two-pointer scan O(n)
        Space: O(1)   — sorting in place, output not counted
        """
        # 1. Sort the array — enables Two Pointers and duplicate skipping
        nums.sort()
        n = len(nums)
        result = []

        # 2. Fix the first element (i)
        for i in range(n - 3):
            # Skip duplicate values for the first element
            if i > 0 and nums[i] == nums[i - 1]:
                continue

            # Early termination: if smallest possible sum > target
            if nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] > target:
                break

            # Skip: if largest possible sum with nums[i] < target
            if nums[i] + nums[n - 3] + nums[n - 2] + nums[n - 1] < target:
                continue

            # 3. Fix the second element (j)
            for j in range(i + 1, n - 2):
                # Skip duplicate values for the second element
                if j > i + 1 and nums[j] == nums[j - 1]:
                    continue

                # Early termination for j
                if nums[i] + nums[j] + nums[j + 1] + nums[j + 2] > target:
                    break

                # Skip for j
                if nums[i] + nums[j] + nums[n - 2] + nums[n - 1] < target:
                    continue

                # 4. Two Pointers for the remaining subarray
                left, right = j + 1, n - 1
                remain = target - nums[i] - nums[j]

                while left < right:
                    total = nums[left] + nums[right]

                    if total == remain:
                        # Found a valid quadruplet
                        result.append([nums[i], nums[j], nums[left], nums[right]])

                        # Skip duplicates for the third element
                        while left < right and nums[left] == nums[left + 1]:
                            left += 1
                        # Skip duplicates for the fourth element
                        while left < right and nums[right] == nums[right - 1]:
                            right -= 1

                        # Move both pointers inward
                        left += 1
                        right -= 1
                    elif total < remain:
                        left += 1   # Need a larger sum
                    else:
                        right -= 1  # Need a smaller sum

        return result

    def fourSumBruteForce(self, nums: list[int], target: int) -> list[list[int]]:
        """
        Brute Force approach
        Approach: Check all possible quadruplets with four nested loops
        Time:  O(n^4) — four nested loops
        Space: O(n)   — set for deduplication
        """
        nums.sort()
        n = len(nums)
        result = []
        seen = set()

        for i in range(n - 3):
            for j in range(i + 1, n - 2):
                for k in range(j + 1, n - 1):
                    for l in range(k + 1, n):
                        if nums[i] + nums[j] + nums[k] + nums[l] == target:
                            quad = (nums[i], nums[j], nums[k], nums[l])
                            if quad not in seen:
                                seen.add(quad)
                                result.append([nums[i], nums[j], nums[k], nums[l]])

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
            return sorted([sorted(q) for q in r])

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
    test("Example 1", sol.fourSum([1, 0, -1, 0, -2, 2], 0), [[-2, -1, 1, 2], [-2, 0, 0, 2], [-1, 0, 0, 1]])

    # Test 2: LeetCode Example 2
    test("Example 2", sol.fourSum([2, 2, 2, 2, 2], 8), [[2, 2, 2, 2]])

    # Test 3: No quadruplets
    test("No quadruplets", sol.fourSum([1, 2, 3, 4, 5], 100), [])

    # Test 4: Negative target
    test("Negative target", sol.fourSum([-3, -2, -1, 0, 0, 1, 2, 3], -1),
         [[-3, -2, 1, 3], [-3, -1, 0, 3], [-3, -1, 1, 2], [-3, 0, 0, 2], [-2, -1, 0, 2], [-2, 0, 0, 1]])

    # Test 5: All zeros
    test("All zeros target 0", sol.fourSum([0, 0, 0, 0], 0), [[0, 0, 0, 0]])

    # Test 6: Less than 4 elements
    test("Less than 4 elements", sol.fourSum([1, 2, 3], 6), [])

    # Test 7: Large values — overflow check (Python handles big integers natively)
    test("Large values", sol.fourSum([1000000000, 1000000000, 1000000000, 1000000000], -294967296), [])

    # Test 8: Two quadruplets
    test("Two quadruplets", sol.fourSum([-1, 0, 1, 2, -1, -4], -1), [[-4, 0, 1, 2], [-1, -1, 0, 1]])

    # Test 9: Empty array
    test("Empty array", sol.fourSum([], 0), [])

    print("\n=== Brute Force ===")

    test("BF: Example 1", sol.fourSumBruteForce([1, 0, -1, 0, -2, 2], 0), [[-2, -1, 1, 2], [-2, 0, 0, 2], [-1, 0, 0, 1]])
    test("BF: Example 2", sol.fourSumBruteForce([2, 2, 2, 2, 2], 8), [[2, 2, 2, 2]])
    test("BF: All zeros", sol.fourSumBruteForce([0, 0, 0, 0], 0), [[0, 0, 0, 0]])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
