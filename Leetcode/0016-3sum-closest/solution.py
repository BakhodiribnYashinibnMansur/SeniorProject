# ============================================================
# 0016. 3Sum Closest
# https://leetcode.com/problems/3sum-closest/
# Difficulty: Medium
# Tags: Array, Two Pointers, Sorting
# ============================================================


class Solution:
    def threeSumClosest(self, nums: list[int], target: int) -> int:
        """
        Optimal Solution (Sort + Two Pointers)
        Approach: Sort the array, fix one element, use two pointers for the remaining two
        Time:  O(n^2) — one loop * two-pointer scan
        Space: O(log n) — sorting space
        """
        nums.sort()
        n = len(nums)
        closest = nums[0] + nums[1] + nums[2]

        for i in range(n - 2):
            # Skip duplicate values for i to avoid redundant work
            if i > 0 and nums[i] == nums[i - 1]:
                continue

            left, right = i + 1, n - 1

            while left < right:
                current_sum = nums[i] + nums[left] + nums[right]

                # If exact match, return immediately
                if current_sum == target:
                    return target

                # Update closest if current sum is nearer to target
                if abs(current_sum - target) < abs(closest - target):
                    closest = current_sum

                # Move pointers based on comparison with target
                if current_sum < target:
                    left += 1
                else:
                    right -= 1

        return closest

    def threeSumClosestBruteForce(self, nums: list[int], target: int) -> int:
        """
        Brute Force approach
        Approach: Check all triplets and track the closest sum
        Time:  O(n^3) — three nested loops
        Space: O(1) — no extra memory
        """
        n = len(nums)
        closest = nums[0] + nums[1] + nums[2]

        for i in range(n - 2):
            for j in range(i + 1, n - 1):
                for k in range(j + 1, n):
                    current_sum = nums[i] + nums[j] + nums[k]

                    # Update closest if current sum is nearer to target
                    if abs(current_sum - target) < abs(closest - target):
                        closest = current_sum

        return closest


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

    print("=== Sort + Two Pointers (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1", sol.threeSumClosest([-1, 2, 1, -4], 1), 2)

    # Test 2: LeetCode Example 2
    test("Example 2", sol.threeSumClosest([0, 0, 0], 1), 0)

    # Test 3: Exact match exists
    test("Exact match", sol.threeSumClosest([1, 1, 1, 0], 3), 3)

    # Test 4: All negative numbers
    test("All negatives", sol.threeSumClosest([-3, -2, -5, -1], -8), -8)

    # Test 5: Large positive target
    test("Large target", sol.threeSumClosest([1, 2, 3, 4, 5], 100), 12)

    # Test 6: Negative target
    test("Negative target", sol.threeSumClosest([-1, 0, 1, 1, 55], -3), 0)

    # Test 7: Minimum length array
    test("Min length", sol.threeSumClosest([1, 1, 1], 2), 3)

    # Test 8: Mixed positive and negative
    test("Mixed values", sol.threeSumClosest([-10, -4, -1, 0, 3, 7, 11], 5), 4)

    # Test 9: Duplicates in array
    test("Duplicates", sol.threeSumClosest([1, 1, 1, 1], 3), 3)

    print("\n=== Brute Force ===")

    # Test 10: Brute Force — Example 1
    test("BF Example 1", sol.threeSumClosestBruteForce([-1, 2, 1, -4], 1), 2)

    # Test 11: Brute Force — Example 2
    test("BF Example 2", sol.threeSumClosestBruteForce([0, 0, 0], 1), 0)

    # Test 12: Brute Force — Exact match
    test("BF Exact match", sol.threeSumClosestBruteForce([1, 1, 1, 0], 3), 3)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
