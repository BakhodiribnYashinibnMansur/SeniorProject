# ============================================================
# 0040. Combination Sum II
# https://leetcode.com/problems/combination-sum-ii/
# Difficulty: Medium
# Tags: Array, Backtracking
# ============================================================


class Solution:
    def combinationSum2(self, candidates: list[int], target: int) -> list[list[int]]:
        """
        Optimal Solution (Backtracking with Duplicate Skipping)
        Approach: Sort candidates, backtrack with pruning and duplicate skipping
        Time:  O(2^n) — worst case all subsets explored; much less with pruning
        Space: O(n)   — recursion depth + current path
        """
        # 1. Sort candidates — enables pruning and duplicate skipping
        candidates.sort()
        result = []

        def backtrack(start: int, remaining: int, current: list[int]):
            # Base case: found a valid combination
            if remaining == 0:
                result.append(current[:])
                return

            for i in range(start, len(candidates)):
                # Pruning: if current candidate exceeds remaining, all subsequent will too
                if candidates[i] > remaining:
                    break

                # Skip duplicates at the same recursion level
                if i > start and candidates[i] == candidates[i - 1]:
                    continue

                # Choose candidates[i]
                current.append(candidates[i])

                # Recurse: move to i+1 (each element used at most once)
                backtrack(i + 1, remaining - candidates[i], current)

                # Undo choice (backtrack)
                current.pop()

        backtrack(0, target, [])
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
            return sorted([sorted(c) for c in r])

        if sort_result(got) == sort_result(expected):
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Backtracking with Duplicate Skipping (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1",
         sol.combinationSum2([10, 1, 2, 7, 6, 1, 5], 8),
         [[1, 1, 6], [1, 2, 5], [1, 7], [2, 6]])

    # Test 2: LeetCode Example 2
    test("Example 2",
         sol.combinationSum2([2, 5, 2, 1, 2], 5),
         [[1, 2, 2], [5]])

    # Test 3: Single element match
    test("Single element match",
         sol.combinationSum2([1], 1),
         [[1]])

    # Test 4: Single element no match
    test("Single element no match",
         sol.combinationSum2([2], 1),
         [])

    # Test 5: All same elements
    test("All same elements",
         sol.combinationSum2([1, 1, 1, 1, 1], 3),
         [[1, 1, 1]])

    # Test 6: No valid combination
    test("No valid combination",
         sol.combinationSum2([3, 5, 7], 1),
         [])

    # Test 7: Exact target with all elements
    test("Use all elements",
         sol.combinationSum2([1, 2, 3], 6),
         [[1, 2, 3]])

    # Test 8: Multiple duplicate values
    test("Multiple duplicates",
         sol.combinationSum2([1, 1, 1, 2, 2], 4),
         [[1, 1, 2], [2, 2]])

    # Test 9: Large single value equals target
    test("Large single match",
         sol.combinationSum2([8, 7, 4, 3], 7),
         [[3, 4], [7]])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
