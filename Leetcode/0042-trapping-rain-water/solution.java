/**
 * 0042. Trapping Rain Water
 * https://leetcode.com/problems/trapping-rain-water/
 * Difficulty: Hard
 * Tags: Array, Two Pointers, Dynamic Programming, Stack, Monotonic Stack
 */
class Solution {

    /**
     * Optimal Solution (Two Pointers)
     * Approach: Two pointers from both ends, track running leftMax and rightMax
     * Time:  O(n) — each element is visited exactly once
     * Space: O(1) — only a few variables
     */
    public int trap(int[] height) {
        if (height.length <= 2) return 0;

        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                // Left side is the bottleneck
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                // Right side is the bottleneck
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }

        return water;
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
        test("Example 1", sol.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}), 6);

        // Test 2: LeetCode Example 2
        test("Example 2", sol.trap(new int[]{4, 2, 0, 3, 2, 5}), 9);

        // Test 3: No water — ascending
        test("Ascending", sol.trap(new int[]{1, 2, 3, 4, 5}), 0);

        // Test 4: No water — descending
        test("Descending", sol.trap(new int[]{5, 4, 3, 2, 1}), 0);

        // Test 5: V-shape
        test("V-shape", sol.trap(new int[]{3, 0, 3}), 3);

        // Test 6: Single valley
        test("Single valley", sol.trap(new int[]{5, 1, 5}), 4);

        // Test 7: All zeros
        test("All zeros", sol.trap(new int[]{0, 0, 0, 0}), 0);

        // Test 8: Single element
        test("Single element", sol.trap(new int[]{5}), 0);

        // Test 9: Two elements
        test("Two elements", sol.trap(new int[]{1, 2}), 0);

        // Test 10: Complex terrain
        test("Complex terrain", sol.trap(new int[]{5, 2, 1, 2, 1, 5}), 14);

        // Test 11: Flat surface
        test("Flat surface", sol.trap(new int[]{3, 3, 3, 3}), 0);

        // Test 12: W-shape
        test("W-shape", sol.trap(new int[]{3, 0, 2, 0, 4}), 7);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
