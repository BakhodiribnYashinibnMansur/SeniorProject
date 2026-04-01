# ============================================================
# 0026. Remove Duplicates from Sorted Array
# https://leetcode.com/problems/remove-duplicates-from-sorted-array/
# Difficulty: Easy
# Tags: Array, Two Pointers
# ============================================================


class Solution:
    def removeDuplicates(self, nums: list[int]) -> int:
        """
        Optimal Solution (Two Pointers)
        Approach: slow pointer tracks unique position, fast pointer scans
        Time:  O(n) — single pass through the array
        Space: O(1) — only two pointer variables
        """
        if not nums:
            return 0

        # slow points to the last unique element
        slow = 0

        for fast in range(1, len(nums)):
            # Found a new unique element
            if nums[fast] != nums[slow]:
                slow += 1
                nums[slow] = nums[fast]

        # slow is 0-indexed, so count = slow + 1
        return slow + 1


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, nums: list[int], expected_k: int, expected_nums: list[int]):
        global passed, failed
        nums_copy = nums[:]
        got_k = sol.removeDuplicates(nums_copy)
        got_nums = nums_copy[:got_k]
        if got_k == expected_k and got_nums == expected_nums:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      k={got_k}, nums={got_nums}")
            print(f"  Expected: k={expected_k}, nums={expected_nums}")
            failed += 1

    # Test 1: Basic case with one duplicate
    test("Basic case", [1, 1, 2], 2, [1, 2])

    # Test 2: Multiple duplicates
    test("Multiple duplicates", [0, 0, 1, 1, 1, 2, 2, 3, 3, 4], 5, [0, 1, 2, 3, 4])

    # Test 3: Single element
    test("Single element", [1], 1, [1])

    # Test 4: All same elements
    test("All same elements", [1, 1, 1, 1], 1, [1])

    # Test 5: No duplicates
    test("No duplicates", [1, 2, 3, 4, 5], 5, [1, 2, 3, 4, 5])

    # Test 6: Two elements — same
    test("Two elements same", [1, 1], 1, [1])

    # Test 7: Two elements — different
    test("Two elements different", [1, 2], 2, [1, 2])

    # Test 8: Negative numbers
    test("Negative numbers", [-3, -1, 0, 0, 2], 4, [-3, -1, 0, 2])

    # Test 9: Large consecutive duplicates
    test("Large consecutive duplicates", [0, 0, 0, 0, 1, 1, 1, 2], 3, [0, 1, 2])

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
