import java.util.*;

/**
 * 0055. Jump Game
 * https://leetcode.com/problems/jump-game/
 * Difficulty: Medium
 * Tags: Array, Dynamic Programming, Greedy
 */
class Solution {

    /**
     * Optimal Solution (Greedy).
     * Time:  O(n)
     * Space: O(1)
     */
    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;
            farthest = Math.max(farthest, i + nums[i]);
            if (farthest >= nums.length - 1) return true;
        }
        return true;
    }

    /**
     * Bottom-Up DP.
     * Time:  O(n)
     * Space: O(1)
     */
    public boolean canJumpDP(int[] nums) {
        int n = nums.length;
        int lastGood = n - 1;
        for (int i = n - 2; i >= 0; i--) {
            if (i + nums[i] >= lastGood) lastGood = i;
        }
        return lastGood == 0;
    }

    /**
     * Top-Down DP with memoization.
     * Time:  O(n^2)
     * Space: O(n)
     */
    public boolean canJumpMemo(int[] nums) {
        byte[] dp = new byte[nums.length];
        return solveMemo(nums, dp, 0);
    }

    private boolean solveMemo(int[] nums, byte[] dp, int i) {
        int n = nums.length;
        if (i >= n - 1) return true;
        if (dp[i] != 0) return dp[i] == 1;
        int furthest = Math.min(i + nums[i], n - 1);
        for (int j = i + 1; j <= furthest; j++) {
            if (solveMemo(nums, dp, j)) {
                dp[i] = 1;
                return true;
            }
        }
        dp[i] = -1;
        return false;
    }

    /**
     * Brute force (TLE for large n).
     * Time:  O(2^n)
     * Space: O(n)
     */
    public boolean canJumpBrute(int[] nums) {
        return rec(nums, 0);
    }
    private boolean rec(int[] nums, int i) {
        if (i >= nums.length - 1) return true;
        int furthest = Math.min(i + nums[i], nums.length - 1);
        for (int j = i + 1; j <= furthest; j++) {
            if (rec(nums, j)) return true;
        }
        return false;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, boolean got, boolean expected) {
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
        Object[][] cases = {
            {"Example 1", new int[]{2, 3, 1, 1, 4}, true},
            {"Example 2", new int[]{3, 2, 1, 0, 4}, false},
            {"Single zero", new int[]{0}, true},
            {"Single positive", new int[]{5}, true},
            {"Cannot move", new int[]{0, 1}, false},
            {"Big first jump", new int[]{100, 0, 0, 0}, true},
            {"All ones", new int[]{1, 1, 1, 1, 1}, true},
            {"Edge size 2 reachable", new int[]{1, 0}, true},
            {"Two zeros block", new int[]{2, 0, 0, 1}, false},
            {"Just barely", new int[]{2, 0, 0}, true},
            {"Need two jumps", new int[]{1, 2, 3}, true},
            {"All zeros except first", new int[]{4, 0, 0, 0, 0}, true},
            {"Long zeros chain", new int[]{1, 1, 0, 1}, false},
            {"Final element irrelevant", new int[]{2, 1, 0, 0, 0}, false},
        };

        System.out.println("=== Greedy ===");
        for (Object[] c : cases) test((String) c[0], sol.canJump((int[]) c[1]), (boolean) c[2]);

        System.out.println("\n=== Bottom-Up DP ===");
        for (Object[] c : cases) test("DP " + c[0], sol.canJumpDP((int[]) c[1]), (boolean) c[2]);

        System.out.println("\n=== Top-Down DP (Memo) ===");
        for (Object[] c : cases) test("Memo " + c[0], sol.canJumpMemo((int[]) c[1]), (boolean) c[2]);

        System.out.println("\n=== Brute Force (small only) ===");
        for (Object[] c : cases) {
            if (((int[]) c[1]).length > 12) continue;
            test("Brute " + c[0], sol.canJumpBrute((int[]) c[1]), (boolean) c[2]);
        }

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
