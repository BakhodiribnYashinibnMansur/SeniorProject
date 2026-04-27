from typing import List

# ============================================================
# 0055. Jump Game
# https://leetcode.com/problems/jump-game/
# Difficulty: Medium
# Tags: Array, Dynamic Programming, Greedy
# ============================================================


class Solution:
    def canJump(self, nums: List[int]) -> bool:
        """
        Optimal Solution (Greedy).
        Time:  O(n)
        Space: O(1)
        """
        farthest = 0
        for i, x in enumerate(nums):
            if i > farthest:
                return False
            if i + x > farthest:
                farthest = i + x
            if farthest >= len(nums) - 1:
                return True
        return True

    def canJumpDP(self, nums: List[int]) -> bool:
        """Bottom-Up DP. Time O(n), Space O(1)."""
        n = len(nums)
        last_good = n - 1
        for i in range(n - 2, -1, -1):
            if i + nums[i] >= last_good:
                last_good = i
        return last_good == 0

    def canJumpMemo(self, nums: List[int]) -> bool:
        """Top-Down DP. Time O(n^2), Space O(n)."""
        n = len(nums)
        dp = [0] * n  # 0 unknown, 1 good, -1 bad

        def solve(i: int) -> bool:
            if i >= n - 1: return True
            if dp[i] != 0: return dp[i] == 1
            furthest = min(i + nums[i], n - 1)
            for j in range(i + 1, furthest + 1):
                if solve(j):
                    dp[i] = 1
                    return True
            dp[i] = -1
            return False

        return solve(0)

    def canJumpBrute(self, nums: List[int]) -> bool:
        """Brute force. Time O(2^n)."""
        n = len(nums)
        def rec(i: int) -> bool:
            if i >= n - 1: return True
            furthest = min(i + nums[i], n - 1)
            for j in range(i + 1, furthest + 1):
                if rec(j): return True
            return False
        return rec(0)


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}")
            passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    cases = [
        ("Example 1", [2, 3, 1, 1, 4], True),
        ("Example 2", [3, 2, 1, 0, 4], False),
        ("Single zero", [0], True),
        ("Single positive", [5], True),
        ("Cannot move", [0, 1], False),
        ("Big first jump", [100, 0, 0, 0], True),
        ("All ones", [1, 1, 1, 1, 1], True),
        ("Edge size 2 reachable", [1, 0], True),
        ("Two zeros block", [2, 0, 0, 1], False),
        ("Just barely", [2, 0, 0], True),
        ("Need two jumps", [1, 2, 3], True),
        ("All zeros except first", [4, 0, 0, 0, 0], True),
        ("Long zeros chain", [1, 1, 0, 1], False),
        ("Final element irrelevant", [2, 1, 0, 0, 0], False),
    ]

    print("=== Greedy ===")
    for name, nums, exp in cases:
        test(name, sol.canJump(nums), exp)

    print("\n=== Bottom-Up DP ===")
    for name, nums, exp in cases:
        test("DP " + name, sol.canJumpDP(nums), exp)

    print("\n=== Top-Down DP (Memo) ===")
    for name, nums, exp in cases:
        test("Memo " + name, sol.canJumpMemo(nums), exp)

    print("\n=== Brute Force (small only) ===")
    for name, nums, exp in cases:
        if len(nums) > 12: continue
        test("Brute " + name, sol.canJumpBrute(nums), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
