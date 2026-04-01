# ============================================================
# 0041. First Missing Positive
# https://leetcode.com/problems/first-missing-positive/
# Difficulty: Hard
# Tags: Array, Hash Table
# ============================================================


class Solution:
    def firstMissingPositive(self, nums: list[int]) -> int:
        """
        Optimal Solution (Cyclic Sort / Index Mapping)
        Approach: Place each number x at index x-1, then scan
        Time:  O(n) — each element is swapped at most once
        Space: O(1) — in-place, no extra memory
        """
        n = len(nums)

        # Phase 1: Place each number at its correct index
        # Number x should be at index x-1
        for i in range(n):
            while 1 <= nums[i] <= n and nums[nums[i] - 1] != nums[i]:
                # Swap nums[i] to its correct position
                correct = nums[i] - 1
                nums[i], nums[correct] = nums[correct], nums[i]

        # Phase 2: Find the first missing positive
        # The first index i where nums[i] != i+1 gives the answer
        for i in range(n):
            if nums[i] != i + 1:
                return i + 1

        # All 1..n are present, answer is n+1
        return n + 1


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

    # Test 1: Basic case — missing 3
    test("Basic case [1,2,0]", sol.firstMissingPositive([1, 2, 0]), 3)

    # Test 2: Missing in the middle
    test("Missing 2 [3,4,-1,1]", sol.firstMissingPositive([3, 4, -1, 1]), 2)

    # Test 3: Missing 1
    test("Missing 1 [7,8,9,11,12]", sol.firstMissingPositive([7, 8, 9, 11, 12]), 1)

    # Test 4: Single element — missing 1
    test("Single [2]", sol.firstMissingPositive([2]), 1)

    # Test 5: Single element — has 1
    test("Single [1]", sol.firstMissingPositive([1]), 2)

    # Test 6: All negative
    test("All negative [-1,-2,-3]", sol.firstMissingPositive([-1, -2, -3]), 1)

    # Test 7: Consecutive from 1 — answer is n+1
    test("Consecutive [1,2,3,4,5]", sol.firstMissingPositive([1, 2, 3, 4, 5]), 6)

    # Test 8: Duplicates
    test("Duplicates [1,1,1]", sol.firstMissingPositive([1, 1, 1]), 2)

    # Test 9: Contains zero
    test("Contains zero [0,1,2]", sol.firstMissingPositive([0, 1, 2]), 3)

    # Test 10: Unsorted complete
    test("Unsorted [3,1,2]", sol.firstMissingPositive([3, 1, 2]), 4)

    # Test 11: Large gap
    test("Large gap [1,1000]", sol.firstMissingPositive([1, 1000]), 2)

    # Test 12: Mixed negatives and positives
    test("Mixed [-1,4,2,1,9,10]", sol.firstMissingPositive([-1, 4, 2, 1, 9, 10]), 3)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
