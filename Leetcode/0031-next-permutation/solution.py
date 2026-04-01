# ============================================================
# 0031. Next Permutation
# https://leetcode.com/problems/next-permutation/
# Difficulty: Medium
# Tags: Array, Two Pointers
# ============================================================


class Solution:
    def nextPermutation(self, nums: list[int]) -> None:
        """
        Optimal Solution (Next Permutation Algorithm)
        Approach: Find pivot, swap with next larger, reverse suffix
        Time:  O(n) — at most 3 linear scans
        Space: O(1) — in-place swaps only
        """
        n = len(nums)

        # Step 1: Find the pivot — rightmost i where nums[i] < nums[i+1]
        i = n - 2
        while i >= 0 and nums[i] >= nums[i + 1]:
            i -= 1

        # Step 2 & 3: Find the swap target and swap
        if i >= 0:
            j = n - 1
            while nums[j] <= nums[i]:
                j -= 1
            nums[i], nums[j] = nums[j], nums[i]

        # Step 4: Reverse the suffix after the pivot
        left, right = i + 1, n - 1
        while left < right:
            nums[left], nums[right] = nums[right], nums[left]
            left += 1
            right -= 1


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, nums: list[int], expected: list[int]):
        global passed, failed
        sol.nextPermutation(nums)
        if nums == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {nums}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Next Permutation Algorithm ===")

    # Test 1: LeetCode Example 1
    test("Example 1", [1, 2, 3], [1, 3, 2])

    # Test 2: LeetCode Example 2 — last permutation wraps around
    test("Example 2", [3, 2, 1], [1, 2, 3])

    # Test 3: LeetCode Example 3 — duplicates
    test("Example 3", [1, 1, 5], [1, 5, 1])

    # Test 4: Single element
    test("Single element", [1], [1])

    # Test 5: Two elements ascending
    test("Two elements ascending", [1, 2], [2, 1])

    # Test 6: Two elements descending
    test("Two elements descending", [2, 1], [1, 2])

    # Test 7: All same elements
    test("All same elements", [2, 2, 2], [2, 2, 2])

    # Test 8: Pivot at first position
    test("Pivot at first", [1, 5, 4, 3, 2], [2, 1, 3, 4, 5])

    # Test 9: Longer array
    test("Longer array", [1, 3, 5, 4, 2], [1, 4, 2, 3, 5])

    # Test 10: Middle pivot
    test("Middle pivot", [2, 3, 1], [3, 1, 2])

    # Test 11: Duplicates in suffix
    test("Duplicates in suffix", [1, 3, 2, 2], [2, 1, 2, 3])

    # Test 12: Already near last
    test("Near last", [5, 4, 7, 5, 3, 2], [5, 5, 2, 3, 4, 7])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
