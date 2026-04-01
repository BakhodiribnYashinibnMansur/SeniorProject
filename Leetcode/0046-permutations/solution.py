# ============================================================
# 0046. Permutations
# https://leetcode.com/problems/permutations/
# Difficulty: Medium
# Tags: Array, Backtracking
# ============================================================


class Solution:
    def permute(self, nums: list[int]) -> list[list[int]]:
        """
        Optimal Solution (Backtracking with swaps)
        Approach: Fix each element at each position via swapping
        Time:  O(n * n!) — n! permutations, O(n) to copy each
        Space: O(n) — recursion depth (excluding output)
        """
        result = []
        n = len(nums)

        def backtrack(start: int):
            # Base case: all positions are fixed
            if start == n:
                result.append(nums[:])  # append a copy
                return

            # Try placing each element at position 'start'
            for i in range(start, n):
                # Swap nums[start] and nums[i]
                nums[start], nums[i] = nums[i], nums[start]

                # Recurse on the remaining positions
                backtrack(start + 1)

                # Swap back (undo)
                nums[start], nums[i] = nums[i], nums[start]

        backtrack(0)
        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        # Sort both for order-independent comparison
        got_sorted = sorted([sorted(p) for p in got])
        exp_sorted = sorted([sorted(p) for p in expected])
        if got_sorted == exp_sorted and len(got) == len(expected):
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Basic case — 3 elements
    test("Basic [1,2,3]",
         sol.permute([1, 2, 3]),
         [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]])

    # Test 2: Two elements
    test("Two elements [0,1]",
         sol.permute([0, 1]),
         [[0,1],[1,0]])

    # Test 3: Single element
    test("Single element [1]",
         sol.permute([1]),
         [[1]])

    # Test 4: Negative numbers
    test("Negative numbers [-1,0,1]",
         sol.permute([-1, 0, 1]),
         [[-1,0,1],[-1,1,0],[0,-1,1],[0,1,-1],[1,-1,0],[1,0,-1]])

    # Test 5: Four elements — count check
    result = sol.permute([1, 2, 3, 4])
    count_ok = len(result) == 24  # 4! = 24
    unique_ok = len(set(tuple(p) for p in result)) == 24
    if count_ok and unique_ok:
        print("✅ PASS: Four elements [1,2,3,4] — 24 permutations")
        passed += 1
    else:
        print(f"❌ FAIL: Four elements [1,2,3,4] — got {len(result)} permutations")
        failed += 1

    # Test 6: Maximum length — 6 elements
    result = sol.permute([1, 2, 3, 4, 5, 6])
    count_ok = len(result) == 720  # 6! = 720
    unique_ok = len(set(tuple(p) for p in result)) == 720
    if count_ok and unique_ok:
        print("✅ PASS: Max length [1,2,3,4,5,6] — 720 permutations")
        passed += 1
    else:
        print(f"❌ FAIL: Max length [1,2,3,4,5,6] — got {len(result)} permutations")
        failed += 1

    # Test 7: Mixed positive/negative
    test("Mixed [-10,10]",
         sol.permute([-10, 10]),
         [[-10,10],[10,-10]])

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
