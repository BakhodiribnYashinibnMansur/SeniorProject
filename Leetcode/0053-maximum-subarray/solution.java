import java.util.*;

/**
 * 0053. Maximum Subarray
 * https://leetcode.com/problems/maximum-subarray/
 * Difficulty: Medium
 * Tags: Array, Divide and Conquer, Dynamic Programming
 */
class Solution {

    /**
     * Optimal Solution (Kadane's Algorithm).
     * Time:  O(n)
     * Space: O(1)
     */
    public int maxSubArray(int[] nums) {
        int cur = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            best = Math.max(best, cur);
        }
        return best;
    }

    /**
     * Brute force double sweep.
     * Time:  O(n^2)
     * Space: O(1)
     */
    public int maxSubArrayBrute(int[] nums) {
        int best = nums[0];
        for (int i = 0; i < nums.length; i++) {
            int s = 0;
            for (int j = i; j < nums.length; j++) {
                s += nums[j];
                if (s > best) best = s;
            }
        }
        return best;
    }

    /**
     * Divide and Conquer.
     * Time:  O(n log n)
     * Space: O(log n)
     */
    public int maxSubArrayDC(int[] nums) {
        return solveDC(nums, 0, nums.length - 1);
    }

    private int solveDC(int[] nums, int l, int r) {
        if (l == r) return nums[l];
        int m = (l + r) >>> 1;
        int leftMax = solveDC(nums, l, m);
        int rightMax = solveDC(nums, m + 1, r);

        int bestL = nums[m], sumL = 0;
        for (int i = m; i >= l; i--) {
            sumL += nums[i];
            bestL = Math.max(bestL, sumL);
        }
        int bestR = nums[m + 1], sumR = 0;
        for (int j = m + 1; j <= r; j++) {
            sumR += nums[j];
            bestR = Math.max(bestR, sumR);
        }
        return Math.max(Math.max(leftMax, rightMax), bestL + bestR);
    }

    /**
     * Prefix sum view.
     * Time:  O(n)
     * Space: O(1)
     */
    public int maxSubArrayPrefix(int[] nums) {
        int best = nums[0];
        int running = 0, minPrefix = 0;
        for (int x : nums) {
            running += x;
            best = Math.max(best, running - minPrefix);
            minPrefix = Math.min(minPrefix, running);
        }
        return best;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int got, int expected) {
        if (got == expected) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + got);
            System.out.println("  Expected: " + expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        int[][] inputs = {
            {-2, 1, -3, 4, -1, 2, 1, -5, 4},
            {1},
            {5, 4, -1, 7, 8},
            {-5},
            {-3, -1, -2},
            {0, 0, 0},
            {1, 2, 3, 4, 5},
            {-1, 2, -1, 2, -1, 2},
            {0, 0, -1, 0, 0},
            {-1, 2},
            {-1, -2},
            {0},
            {-2, -3, 4}
        };
        int[] expected = {6, 1, 23, -5, -1, 0, 15, 4, 0, 2, -1, 0, 4};
        String[] names = {
            "Example 1", "Single positive", "Mostly positive", "Single negative",
            "All negative", "All zeros", "All positive", "Alternating",
            "Long sequence with zeros", "Two elements pos/neg", "Two elements neg/neg",
            "Single zero", "Boundary peak"
        };

        System.out.println("=== Kadane's Algorithm ===");
        for (int i = 0; i < inputs.length; i++)
            test(names[i], sol.maxSubArray(inputs[i]), expected[i]);

        System.out.println("\n=== Brute Force ===");
        for (int i = 0; i < inputs.length; i++)
            test("Brute " + names[i], sol.maxSubArrayBrute(inputs[i]), expected[i]);

        System.out.println("\n=== Divide and Conquer ===");
        for (int i = 0; i < inputs.length; i++)
            test("DC " + names[i], sol.maxSubArrayDC(inputs[i]), expected[i]);

        System.out.println("\n=== Prefix Sum ===");
        for (int i = 0; i < inputs.length; i++)
            test("Prefix " + names[i], sol.maxSubArrayPrefix(inputs[i]), expected[i]);

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
