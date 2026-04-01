# ============================================================
# 0027. Remove Element
# https://leetcode.com/problems/remove-element/
# Difficulty: Easy
# Tags: Array, Two Pointers
# ============================================================


class Solution:
    def removeElement(self, nums: list[int], val: int) -> int:
        """
        Optimal Solution (Two Pointers — Opposite Direction)
        Approach: Swap val elements with end elements
        Time:  O(n) — each element visited at most once
        Space: O(1) — only two pointer variables
        """
        left = 0
        right = len(nums) - 1

        while left <= right:
            if nums[left] == val:
                # Replace with last element, shrink from right
                nums[left] = nums[right]
                right -= 1
                # Do NOT advance left — swapped element needs checking
            else:
                left += 1

        return left


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    passed = failed = 0

    def test(name: str, nums: list[int], val: int, expected_k: int, expected_set: set[int]):
        global passed, failed
        nums_copy = nums[:]
        k = Solution().removeElement(nums_copy, val)
        result_set = set(nums_copy[:k])
        # Check: k matches AND first k elements contain only expected values
        if k == expected_k and sorted(nums_copy[:k]) == sorted(list(expected_set)):
            print(f"\u2705 PASS: {name} \u2192 k={k}, nums[:k]={nums_copy[:k]}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      k={k}, nums[:k]={nums_copy[:k]}")
            print(f"  Expected: k={expected_k}, elements={sorted(list(expected_set))}")
            failed += 1

    # Test 1: Basic case — remove 3s
    test("Basic [3,2,2,3] val=3",
         [3, 2, 2, 3], 3, 2, [2, 2])

    # Test 2: Multiple removals
    test("Multiple [0,1,2,2,3,0,4,2] val=2",
         [0, 1, 2, 2, 3, 0, 4, 2], 2, 5, [0, 1, 3, 0, 4])

    # Test 3: Empty array
    test("Empty array",
         [], 1, 0, [])

    # Test 4: All elements equal val
    test("All same [3,3,3] val=3",
         [3, 3, 3], 3, 0, [])

    # Test 5: No elements equal val
    test("None match [1,2,3] val=4",
         [1, 2, 3], 4, 3, [1, 2, 3])

    # Test 6: Single element (keep)
    test("Single keep [1] val=2",
         [1], 2, 1, [1])

    # Test 7: Single element (remove)
    test("Single remove [1] val=1",
         [1], 1, 0, [])

    # Test 8: Val at beginning
    test("Val at start [3,1,2] val=3",
         [3, 1, 2], 3, 2, [1, 2])

    # Test 9: Val at end
    test("Val at end [1,2,3] val=3",
         [1, 2, 3], 3, 2, [1, 2])

    # Test 10: All same, not val
    test("All same not val [2,2,2] val=3",
         [2, 2, 2], 3, 3, [2, 2, 2])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
