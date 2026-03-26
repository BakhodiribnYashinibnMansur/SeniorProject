# ============================================================
# 0001. Two Sum
# https://leetcode.com/problems/two-sum/
# Difficulty: Easy
# Tags: Array, Hash Table
# ============================================================


class Solution:
    def twoSum(self, nums: list[int], target: int) -> list[int]:
        """
        Optimal Solution (One-pass Hash Map)
        Approach: Look up complement in Hash Map
        Time:  O(n) — single pass through the array
        Space: O(n) — Hash Map stores at most n elements
        """
        # Hash Map: value → index
        # For each element, check if its complement (target - num)
        # has been seen before
        seen = {}

        for i, num in enumerate(nums):
            # Calculate complement
            complement = target - num

            # Is complement in the Hash Map?
            if complement in seen:
                # Found! complement's index is seen[complement], current index is i
                return [seen[complement], i]

            # Add current element to Hash Map
            # It will serve as complement for future elements
            seen[num] = i

        # Per constraints, a solution always exists
        # This line is never reached
        return []


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

    # Test 1: Basic case — found in first pair
    test("Basic case", sol.twoSum([2, 7, 11, 15], 9), [0, 1])

    # Test 2: Found in the middle
    test("Found in middle", sol.twoSum([3, 2, 4], 6), [1, 2])

    # Test 3: Duplicate values
    test("Duplicate values", sol.twoSum([3, 3], 6), [0, 1])

    # Test 4: Negative numbers
    test("Negative numbers", sol.twoSum([-1, -2, -3, -4, -5], -8), [2, 4])

    # Test 5: Mixed numbers (negative + positive)
    test("Mixed numbers", sol.twoSum([-3, 4, 3, 90], 0), [0, 2])

    # Test 6: Zero values
    test("Zero values", sol.twoSum([0, 4, 3, 0], 0), [0, 3])

    # Test 7: Large values
    test("Large values", sol.twoSum([1000000000, -1000000000], 0), [0, 1])

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
