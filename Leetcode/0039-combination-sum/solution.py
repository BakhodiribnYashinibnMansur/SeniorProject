# ============================================================
# 0039. Combination Sum
# https://leetcode.com/problems/combination-sum/
# Difficulty: Medium
# Tags: Array, Backtracking
# ============================================================


class Solution:
    def combinationSum(self, candidates: list[int], target: int) -> list[list[int]]:
        """
        Optimal Solution (Backtracking with Pruning)
        Approach: Sort candidates, then recursively build combinations.
                  Use a start index to avoid duplicate combinations.
                  Reuse candidates by recursing with same index (not i+1).
                  Prune when candidate exceeds remaining target.
        Time:  O(n^(T/M)) — n candidates, T target, M min candidate
        Space: O(T/M)     — max recursion depth
        """
        candidates.sort()
        result = []

        def backtrack(start: int, remaining: int, current: list[int]) -> None:
            if remaining == 0:
                result.append(current[:])
                return
            for i in range(start, len(candidates)):
                if candidates[i] > remaining:
                    break  # pruning: sorted, so all subsequent are too large
                current.append(candidates[i])
                backtrack(i, remaining - candidates[i], current)
                current.pop()  # undo choice (backtrack)

        backtrack(0, target, [])
        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got: list[list[int]], expected: list[list[int]]) -> None:
        global passed, failed
        # Sort inner lists and outer list for comparison
        sorted_got = sorted([sorted(x) for x in got])
        sorted_exp = sorted([sorted(x) for x in expected])
        if sorted_got == sorted_exp:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: LeetCode Example 1
    test("Example 1: [2,3,6,7] target=7",
         sol.combinationSum([2, 3, 6, 7], 7),
         [[2, 2, 3], [7]])

    # Test 2: LeetCode Example 2
    test("Example 2: [2,3,5] target=8",
         sol.combinationSum([2, 3, 5], 8),
         [[2, 2, 2, 2], [2, 3, 3], [3, 5]])

    # Test 3: LeetCode Example 3 — no valid combination
    test("Example 3: [2] target=1",
         sol.combinationSum([2], 1),
         [])

    # Test 4: Single candidate equals target
    test("Single candidate = target: [7] target=7",
         sol.combinationSum([7], 7),
         [[7]])

    # Test 5: Single candidate used multiple times
    test("Single candidate reuse: [3] target=9",
         sol.combinationSum([3], 9),
         [[3, 3, 3]])

    # Test 6: All candidates too large
    test("All too large: [5,6,7] target=3",
         sol.combinationSum([5, 6, 7], 3),
         [])

    # Test 7: Target equals smallest candidate
    test("Target = smallest: [2,3,5] target=2",
         sol.combinationSum([2, 3, 5], 2),
         [[2]])

    # Test 8: Multiple valid combinations
    test("Multiple combos: [2,3,5] target=10",
         sol.combinationSum([2, 3, 5], 10),
         [[2, 2, 2, 2, 2], [2, 2, 3, 3], [2, 3, 5], [5, 5]])

    # Test 9: Larger target
    test("Larger: [2,3,6,7] target=13",
         sol.combinationSum([2, 3, 6, 7], 13),
         [[2, 2, 2, 2, 2, 3], [2, 2, 2, 7], [2, 2, 3, 3, 3], [2, 2, 3, 6], [3, 3, 7], [6, 7]])

    # Test 10: Single element, not divisible
    test("Not divisible: [3] target=7",
         sol.combinationSum([3], 7),
         [])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
