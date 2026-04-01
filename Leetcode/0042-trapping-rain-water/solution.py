# ============================================================
# 0042. Trapping Rain Water
# https://leetcode.com/problems/trapping-rain-water/
# Difficulty: Hard
# Tags: Array, Two Pointers, Dynamic Programming, Stack, Monotonic Stack
# ============================================================


class Solution:
    def trap(self, height: list[int]) -> int:
        """
        Optimal Solution (Two Pointers)
        Approach: Two pointers from both ends, track running leftMax and rightMax
        Time:  O(n) — each element is visited exactly once
        Space: O(1) — only a few variables
        """
        left, right = 0, len(height) - 1
        left_max, right_max = 0, 0
        water = 0

        while left < right:
            if height[left] < height[right]:
                # Left side is the bottleneck
                if height[left] >= left_max:
                    left_max = height[left]
                else:
                    water += left_max - height[left]
                left += 1
            else:
                # Right side is the bottleneck
                if height[right] >= right_max:
                    right_max = height[right]
                else:
                    water += right_max - height[right]
                right -= 1

        return water


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

    print("=== Two Pointers (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1", sol.trap([0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]), 6)

    # Test 2: LeetCode Example 2
    test("Example 2", sol.trap([4, 2, 0, 3, 2, 5]), 9)

    # Test 3: No water — ascending
    test("Ascending", sol.trap([1, 2, 3, 4, 5]), 0)

    # Test 4: No water — descending
    test("Descending", sol.trap([5, 4, 3, 2, 1]), 0)

    # Test 5: V-shape
    test("V-shape", sol.trap([3, 0, 3]), 3)

    # Test 6: Single valley
    test("Single valley", sol.trap([5, 1, 5]), 4)

    # Test 7: All zeros
    test("All zeros", sol.trap([0, 0, 0, 0]), 0)

    # Test 8: Single element
    test("Single element", sol.trap([5]), 0)

    # Test 9: Two elements
    test("Two elements", sol.trap([1, 2]), 0)

    # Test 10: Complex terrain
    test("Complex terrain", sol.trap([5, 2, 1, 2, 1, 5]), 14)

    # Test 11: Flat surface
    test("Flat surface", sol.trap([3, 3, 3, 3]), 0)

    # Test 12: W-shape
    test("W-shape", sol.trap([3, 0, 2, 0, 4]), 7)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
