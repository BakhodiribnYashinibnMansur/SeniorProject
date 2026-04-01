# ============================================================
# 0047. Permutations II
# https://leetcode.com/problems/permutations-ii/
# Difficulty: Medium
# Tags: Array, Backtracking
# ============================================================


class Solution:
    def permuteUnique(self, nums: list[int]) -> list[list[int]]:
        """
        Optimal Solution (Backtracking with Sorting + Duplicate Skipping)
        Approach: Sort array, use backtracking with used[] array, skip duplicates
        Time:  O(n * n!) — at most n! permutations, O(n) to copy each
        Space: O(n)      — recursion depth + used array + path
        """
        # 1. Sort to group duplicates together
        nums.sort()
        n = len(nums)
        result = []
        used = [False] * n

        def backtrack(path: list[int]) -> None:
            # Base case: permutation is complete
            if len(path) == n:
                result.append(path[:])
                return

            for i in range(n):
                # Skip if already used in current permutation
                if used[i]:
                    continue

                # Skip duplicate: same value as previous, and previous was not used
                # (meaning previous was backtracked at this level — duplicate branch)
                if i > 0 and nums[i] == nums[i - 1] and not used[i - 1]:
                    continue

                # Place nums[i] and recurse
                used[i] = True
                path.append(nums[i])
                backtrack(path)
                path.pop()
                used[i] = False

        backtrack([])
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
            return sorted([sorted(p) for p in r])

        # For permutations, compare as sorted list of tuples
        got_set = set(tuple(p) for p in got)
        exp_set = set(tuple(p) for p in expected)

        if got_set == exp_set and len(got) == len(expected):
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Backtracking with Sorting + Duplicate Skipping ===")

    # Test 1: LeetCode Example 1 — duplicates
    test("Example 1: [1,1,2]",
         sol.permuteUnique([1, 1, 2]),
         [[1, 1, 2], [1, 2, 1], [2, 1, 1]])

    # Test 2: LeetCode Example 2 — all unique
    test("Example 2: [1,2,3]",
         sol.permuteUnique([1, 2, 3]),
         [[1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1]])

    # Test 3: All same elements
    test("All same [1,1,1]",
         sol.permuteUnique([1, 1, 1]),
         [[1, 1, 1]])

    # Test 4: Single element
    test("Single [0]",
         sol.permuteUnique([0]),
         [[0]])

    # Test 5: Two same elements
    test("Two same [1,1]",
         sol.permuteUnique([1, 1]),
         [[1, 1]])

    # Test 6: Two different elements
    test("Two different [1,2]",
         sol.permuteUnique([1, 2]),
         [[1, 2], [2, 1]])

    # Test 7: Negative numbers with duplicates
    test("Negatives [-1,1,1]",
         sol.permuteUnique([-1, 1, 1]),
         [[-1, 1, 1], [1, -1, 1], [1, 1, -1]])

    # Test 8: Multiple duplicate groups
    test("Two pairs [1,1,2,2]",
         sol.permuteUnique([1, 1, 2, 2]),
         [[1, 1, 2, 2], [1, 2, 1, 2], [1, 2, 2, 1],
          [2, 1, 1, 2], [2, 1, 2, 1], [2, 2, 1, 1]])

    # Test 9: Larger input
    test("Four elements [1,1,1,2]",
         sol.permuteUnique([1, 1, 1, 2]),
         [[1, 1, 1, 2], [1, 1, 2, 1], [1, 2, 1, 1], [2, 1, 1, 1]])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
