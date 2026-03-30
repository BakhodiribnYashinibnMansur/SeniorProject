/**
 * 0016. 3Sum Closest
 * https://leetcode.com/problems/3sum-closest/
 * Difficulty: Medium
 * Tags: Array, Two Pointers, Sorting
 */

import java.util.Arrays;

class Solution {

    /**
     * Optimal Solution (Sort + Two Pointers)
     * Approach: Sort the array, fix one element, use two pointers for the remaining two
     * Time:  O(n^2) — one loop * two-pointer scan
     * Space: O(log n) — sorting space
     */
    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int n = nums.length;
        int closest = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < n - 2; i++) {
            // Skip duplicate values for i to avoid redundant work
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int left = i + 1, right = n - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                // If exact match, return immediately
                if (sum == target) return target;

                // Update closest if current sum is nearer to target
                if (Math.abs(sum - target) < Math.abs(closest - target)) {
                    closest = sum;
                }

                // Move pointers based on comparison with target
                if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return closest;
    }

    /**
     * Brute Force approach
     * Approach: Check all triplets and track the closest sum
     * Time:  O(n^3) — three nested loops
     * Space: O(1) — no extra memory
     */
    public int threeSumClosestBruteForce(int[] nums, int target) {
        int n = nums.length;
        int closest = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    int sum = nums[i] + nums[j] + nums[k];

                    // Update closest if current sum is nearer to target
                    if (Math.abs(sum - target) < Math.abs(closest - target)) {
                        closest = sum;
                    }
                }
            }
        }

        return closest;
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

        System.out.println("=== Sort + Two Pointers (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1", sol.threeSumClosest(new int[]{-1, 2, 1, -4}, 1), 2);

        // Test 2: LeetCode Example 2
        test("Example 2", sol.threeSumClosest(new int[]{0, 0, 0}, 1), 0);

        // Test 3: Exact match exists
        test("Exact match", sol.threeSumClosest(new int[]{1, 1, 1, 0}, 3), 3);

        // Test 4: All negative numbers
        test("All negatives", sol.threeSumClosest(new int[]{-3, -2, -5, -1}, -8), -8);

        // Test 5: Large positive target
        test("Large target", sol.threeSumClosest(new int[]{1, 2, 3, 4, 5}, 100), 12);

        // Test 6: Negative target
        test("Negative target", sol.threeSumClosest(new int[]{-1, 0, 1, 1, 55}, -3), 0);

        // Test 7: Minimum length array
        test("Min length", sol.threeSumClosest(new int[]{1, 1, 1}, 2), 3);

        // Test 8: Mixed positive and negative
        test("Mixed values", sol.threeSumClosest(new int[]{-10, -4, -1, 0, 3, 7, 11}, 5), 4);

        // Test 9: Duplicates in array
        test("Duplicates", sol.threeSumClosest(new int[]{1, 1, 1, 1}, 3), 3);

        System.out.println("\n=== Brute Force ===");

        // Test 10: Brute Force — Example 1
        test("BF Example 1", sol.threeSumClosestBruteForce(new int[]{-1, 2, 1, -4}, 1), 2);

        // Test 11: Brute Force — Example 2
        test("BF Example 2", sol.threeSumClosestBruteForce(new int[]{0, 0, 0}, 1), 0);

        // Test 12: Brute Force — Exact match
        test("BF Exact match", sol.threeSumClosestBruteForce(new int[]{1, 1, 1, 0}, 3), 3);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
