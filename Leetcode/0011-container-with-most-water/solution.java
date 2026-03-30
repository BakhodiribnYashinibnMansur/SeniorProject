/**
 * 0011. Container With Most Water
 * https://leetcode.com/problems/container-with-most-water/
 * Difficulty: Medium
 * Tags: Array, Two Pointers, Greedy
 */
class Solution {

    /**
     * Optimal Solution (Two Pointers)
     * Approach: Start from both ends, move the shorter line inward
     * Time:  O(n) — single pass through the array
     * Space: O(1) — only two pointers and a variable for max area
     */
    public int maxArea(int[] height) {
        // Two Pointers: start from the widest container
        // and move the shorter side inward
        int left = 0, right = height.length - 1;
        int maxWater = 0;

        while (left < right) {
            // Calculate the area: width * min(height[left], height[right])
            int width = right - left;
            int h = Math.min(height[left], height[right]);
            int area = width * h;

            // Update the maximum area
            maxWater = Math.max(maxWater, area);

            // Move the pointer with the shorter line
            // Moving the taller line can never increase the area
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxWater;
    }

    /**
     * Brute Force approach
     * Approach: Check all pairs of lines
     * Time:  O(n^2) — check every pair
     * Space: O(1) — no extra memory
     */
    public int maxAreaBruteForce(int[] height) {
        int n = height.length;
        int maxWater = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Width between the two lines
                int width = j - i;

                // Height is limited by the shorter line
                int h = Math.min(height[i], height[j]);

                // Calculate area
                int area = width * h;
                maxWater = Math.max(maxWater, area);
            }
        }

        return maxWater;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int got, int expected) {
        if (got == expected) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %d%n  Expected: %d%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Two Pointers (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1", sol.maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}), 49);

        // Test 2: LeetCode Example 2
        test("Example 2", sol.maxArea(new int[]{1, 1}), 1);

        // Test 3: Decreasing heights
        test("Decreasing heights", sol.maxArea(new int[]{5, 4, 3, 2, 1}), 6);

        // Test 4: Increasing heights
        test("Increasing heights", sol.maxArea(new int[]{1, 2, 3, 4, 5}), 6);

        // Test 5: Same heights
        test("Same heights", sol.maxArea(new int[]{3, 3, 3, 3, 3}), 12);

        // Test 6: Two elements
        test("Two elements", sol.maxArea(new int[]{4, 7}), 4);

        // Test 7: Peak in the middle
        test("Peak in middle", sol.maxArea(new int[]{1, 2, 4, 3}), 4);

        // Test 8: Large values at ends
        test("Large values at ends", sol.maxArea(new int[]{10, 1, 1, 1, 10}), 40);

        // Test 9: Single tall line
        test("Single tall line", sol.maxArea(new int[]{1, 1, 1, 100, 1, 1, 1}), 6);

        System.out.println("\n=== Brute Force ===");

        // Test 10: Brute Force — Example 1
        test("BF Example 1", sol.maxAreaBruteForce(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}), 49);

        // Test 11: Brute Force — Example 2
        test("BF Example 2", sol.maxAreaBruteForce(new int[]{1, 1}), 1);

        // Test 12: Brute Force — Same heights
        test("BF Same heights", sol.maxAreaBruteForce(new int[]{3, 3, 3, 3, 3}), 12);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
