/**
 * 0045. Jump Game II
 * https://leetcode.com/problems/jump-game-ii/
 * Difficulty: Medium
 * Tags: Array, Dynamic Programming, Greedy
 */
class Solution {

    /**
     * Optimal Solution (Greedy / BFS-like)
     * Approach: Track jump levels using end and farthest pointers
     * Time:  O(n) — single pass through the array
     * Space: O(1) — only three variables
     */
    public int jump(int[] nums) {
        int n = nums.length;
        int jumps = 0;
        int end = 0;      // boundary of current jump level
        int farthest = 0; // farthest reachable from current level

        for (int i = 0; i < n - 1; i++) {
            // Update the farthest we can reach from this level
            farthest = Math.max(farthest, i + nums[i]);

            // If we've reached the end of the current level, jump
            if (i == end) {
                jumps++;
                end = farthest;
                if (end >= n - 1) break;
            }
        }

        return jumps;
    }

    /**
     * Dynamic Programming approach
     * Approach: dp[i] = minimum jumps to reach index i
     * Time:  O(n * max(nums[i])) — for each index, update reachable indices
     * Space: O(n) — dp array
     */
    public int jumpDP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        java.util.Arrays.fill(dp, n); // initialize to a large value
        dp[0] = 0;

        for (int i = 0; i < n - 1; i++) {
            int end = Math.min(i + nums[i], n - 1);
            for (int j = i + 1; j <= end; j++) {
                dp[j] = Math.min(dp[j], dp[i] + 1);
            }
        }

        return dp[n - 1];
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

        System.out.println("=== Greedy / BFS-like (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1", sol.jump(new int[]{2, 3, 1, 1, 4}), 2);

        // Test 2: LeetCode Example 2
        test("Example 2", sol.jump(new int[]{2, 3, 0, 1, 4}), 2);

        // Test 3: Single element
        test("Single element", sol.jump(new int[]{0}), 0);

        // Test 4: Two elements
        test("Two elements", sol.jump(new int[]{1, 0}), 1);

        // Test 5: Already covers the end
        test("One big jump", sol.jump(new int[]{5, 1, 1, 1, 1}), 1);

        // Test 6: All ones
        test("All ones", sol.jump(new int[]{1, 1, 1, 1, 1}), 4);

        // Test 7: Decreasing jumps
        test("Decreasing", sol.jump(new int[]{4, 3, 2, 1, 0}), 1);

        // Test 8: Increasing jumps
        test("Increasing", sol.jump(new int[]{1, 2, 3, 4, 5}), 3);

        // Test 9: Larger example
        test("Larger example", sol.jump(new int[]{1, 2, 1, 1, 1}), 3);

        // Test 10: Jump exactly to end
        test("Exact jump", sol.jump(new int[]{2, 1, 1}), 1);

        System.out.println("\n=== Dynamic Programming ===");

        // Test 11: DP — Example 1
        test("DP Example 1", sol.jumpDP(new int[]{2, 3, 1, 1, 4}), 2);

        // Test 12: DP — Example 2
        test("DP Example 2", sol.jumpDP(new int[]{2, 3, 0, 1, 4}), 2);

        // Test 13: DP — Single element
        test("DP Single element", sol.jumpDP(new int[]{0}), 0);

        // Test 14: DP — All ones
        test("DP All ones", sol.jumpDP(new int[]{1, 1, 1, 1, 1}), 4);

        // Test 15: DP — One big jump
        test("DP One big jump", sol.jumpDP(new int[]{5, 1, 1, 1, 1}), 1);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
