# ============================================================
# 0011. Container With Most Water
# https://leetcode.com/problems/container-with-most-water/
# Difficulty: Medium
# Tags: Array, Two Pointers, Greedy
# ============================================================


class Solution:
    def maxArea(self, height: list[int]) -> int:
        """
        Optimal Solution (Two Pointers)
        Approach: Start from both ends, move the shorter line inward
        Time:  O(n) — single pass through the array
        Space: O(1) — only two pointers and a variable for max area
        """
        # Two Pointers: start from the widest container
        # and move the shorter side inward
        left, right = 0, len(height) - 1
        max_water = 0

        while left < right:
            # Calculate the area: width * min(height[left], height[right])
            width = right - left
            h = min(height[left], height[right])
            area = width * h

            # Update the maximum area
            max_water = max(max_water, area)

            # Move the pointer with the shorter line
            # Moving the taller line can never increase the area
            if height[left] < height[right]:
                left += 1
            else:
                right -= 1

        return max_water

    def maxAreaBruteForce(self, height: list[int]) -> int:
        """
        Brute Force approach
        Approach: Check all pairs of lines
        Time:  O(n^2) — check every pair
        Space: O(1) — no extra memory
        """
        n = len(height)
        max_water = 0

        for i in range(n):
            for j in range(i + 1, n):
                # Width between the two lines
                width = j - i

                # Height is limited by the shorter line
                h = min(height[i], height[j])

                # Calculate area
                area = width * h
                max_water = max(max_water, area)

        return max_water


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
    test("Example 1", sol.maxArea([1, 8, 6, 2, 5, 4, 8, 3, 7]), 49)

    # Test 2: LeetCode Example 2
    test("Example 2", sol.maxArea([1, 1]), 1)

    # Test 3: Decreasing heights
    test("Decreasing heights", sol.maxArea([5, 4, 3, 2, 1]), 6)

    # Test 4: Increasing heights
    test("Increasing heights", sol.maxArea([1, 2, 3, 4, 5]), 6)

    # Test 5: Same heights
    test("Same heights", sol.maxArea([3, 3, 3, 3, 3]), 12)

    # Test 6: Two elements
    test("Two elements", sol.maxArea([4, 7]), 4)

    # Test 7: Peak in the middle
    test("Peak in middle", sol.maxArea([1, 2, 4, 3]), 4)

    # Test 8: Large values at ends
    test("Large values at ends", sol.maxArea([10, 1, 1, 1, 10]), 40)

    # Test 9: Single tall line
    test("Single tall line", sol.maxArea([1, 1, 1, 100, 1, 1, 1]), 6)

    print("\n=== Brute Force ===")

    # Test 10: Brute Force — Example 1
    test("BF Example 1", sol.maxAreaBruteForce([1, 8, 6, 2, 5, 4, 8, 3, 7]), 49)

    # Test 11: Brute Force — Example 2
    test("BF Example 2", sol.maxAreaBruteForce([1, 1]), 1)

    # Test 12: Brute Force — Same heights
    test("BF Same heights", sol.maxAreaBruteForce([3, 3, 3, 3, 3]), 12)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
