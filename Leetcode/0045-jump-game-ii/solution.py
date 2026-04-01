# ============================================================
# 0045. Jump Game II
# https://leetcode.com/problems/jump-game-ii/
# Difficulty: Medium
# Tags: Array, Dynamic Programming, Greedy
# ============================================================


class Solution:
    def jump(self, nums: list[int]) -> int:
        """
        Optimal Solution (Greedy / BFS-like)
        Approach: Track jump levels using end and farthest pointers
        Time:  O(n) — single pass through the array
        Space: O(1) — only three variables
        """
        n = len(nums)
        jumps = 0
        end = 0        # boundary of current jump level
        farthest = 0   # farthest reachable from current level

        for i in range(n - 1):
            # Update the farthest we can reach from this level
            farthest = max(farthest, i + nums[i])

            # If we've reached the end of the current level, jump
            if i == end:
                jumps += 1
                end = farthest
                if end >= n - 1:
                    break

        return jumps

    def jumpDP(self, nums: list[int]) -> int:
        """
        Dynamic Programming approach
        Approach: dp[i] = minimum jumps to reach index i
        Time:  O(n * max(nums[i])) — for each index, update reachable indices
        Space: O(n) — dp array
        """
        n = len(nums)
        dp = [float('inf')] * n
        dp[0] = 0

        for i in range(n - 1):
            end = min(i + nums[i], n - 1)
            for j in range(i + 1, end + 1):
                dp[j] = min(dp[j], dp[i] + 1)

        return dp[n - 1]


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

    print("=== Greedy / BFS-like (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1", sol.jump([2, 3, 1, 1, 4]), 2)

    # Test 2: LeetCode Example 2
    test("Example 2", sol.jump([2, 3, 0, 1, 4]), 2)

    # Test 3: Single element
    test("Single element", sol.jump([0]), 0)

    # Test 4: Two elements
    test("Two elements", sol.jump([1, 0]), 1)

    # Test 5: Already covers the end
    test("One big jump", sol.jump([5, 1, 1, 1, 1]), 1)

    # Test 6: All ones
    test("All ones", sol.jump([1, 1, 1, 1, 1]), 4)

    # Test 7: Decreasing jumps
    test("Decreasing", sol.jump([4, 3, 2, 1, 0]), 1)

    # Test 8: Increasing jumps
    test("Increasing", sol.jump([1, 2, 3, 4, 5]), 3)

    # Test 9: Larger example
    test("Larger example", sol.jump([1, 2, 1, 1, 1]), 3)

    # Test 10: Jump exactly to end
    test("Exact jump", sol.jump([2, 1, 1]), 1)

    print("\n=== Dynamic Programming ===")

    # Test 11: DP — Example 1
    test("DP Example 1", sol.jumpDP([2, 3, 1, 1, 4]), 2)

    # Test 12: DP — Example 2
    test("DP Example 2", sol.jumpDP([2, 3, 0, 1, 4]), 2)

    # Test 13: DP — Single element
    test("DP Single element", sol.jumpDP([0]), 0)

    # Test 14: DP — All ones
    test("DP All ones", sol.jumpDP([1, 1, 1, 1, 1]), 4)

    # Test 15: DP — One big jump
    test("DP One big jump", sol.jumpDP([5, 1, 1, 1, 1]), 1)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
