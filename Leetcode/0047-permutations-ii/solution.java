import java.util.*;

/**
 * 0047. Permutations II
 * https://leetcode.com/problems/permutations-ii/
 * Difficulty: Medium
 * Tags: Array, Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Backtracking with Sorting + Duplicate Skipping)
     * Approach: Sort array, use backtracking with used[] array, skip duplicates
     * Time:  O(n * n!) — at most n! permutations, O(n) to copy each
     * Space: O(n)      — recursion depth + used array + path
     */
    public List<List<Integer>> permuteUnique(int[] nums) {
        // 1. Sort to group duplicates together
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
        // Base case: permutation is complete
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            // Skip if already used in current permutation
            if (used[i]) continue;

            // Skip duplicate: same value as previous, and previous was not used
            // (meaning previous was backtracked at this level — duplicate branch)
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;

            // Place nums[i] and recurse
            used[i] = true;
            path.add(nums[i]);
            backtrack(nums, used, path, result);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, List<List<Integer>> got, List<List<Integer>> expected) {
        // Compare as sets of permutations
        Set<List<Integer>> gotSet = new HashSet<>(got);
        Set<List<Integer>> expSet = new HashSet<>(expected);

        if (gotSet.equals(expSet) && got.size() == expected.size()) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    static List<List<Integer>> list(int[]... perms) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] p : perms) {
            List<Integer> perm = new ArrayList<>();
            for (int v : p) perm.add(v);
            result.add(perm);
        }
        return result;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Backtracking with Sorting + Duplicate Skipping ===");

        // Test 1: LeetCode Example 1 — duplicates
        test("Example 1: [1,1,2]",
            sol.permuteUnique(new int[]{1, 1, 2}),
            list(new int[]{1, 1, 2}, new int[]{1, 2, 1}, new int[]{2, 1, 1}));

        // Test 2: LeetCode Example 2 — all unique
        test("Example 2: [1,2,3]",
            sol.permuteUnique(new int[]{1, 2, 3}),
            list(new int[]{1, 2, 3}, new int[]{1, 3, 2}, new int[]{2, 1, 3},
                 new int[]{2, 3, 1}, new int[]{3, 1, 2}, new int[]{3, 2, 1}));

        // Test 3: All same elements
        test("All same [1,1,1]",
            sol.permuteUnique(new int[]{1, 1, 1}),
            list(new int[]{1, 1, 1}));

        // Test 4: Single element
        test("Single [0]",
            sol.permuteUnique(new int[]{0}),
            list(new int[]{0}));

        // Test 5: Two same elements
        test("Two same [1,1]",
            sol.permuteUnique(new int[]{1, 1}),
            list(new int[]{1, 1}));

        // Test 6: Two different elements
        test("Two different [1,2]",
            sol.permuteUnique(new int[]{1, 2}),
            list(new int[]{1, 2}, new int[]{2, 1}));

        // Test 7: Negative numbers with duplicates
        test("Negatives [-1,1,1]",
            sol.permuteUnique(new int[]{-1, 1, 1}),
            list(new int[]{-1, 1, 1}, new int[]{1, -1, 1}, new int[]{1, 1, -1}));

        // Test 8: Multiple duplicate groups
        test("Two pairs [1,1,2,2]",
            sol.permuteUnique(new int[]{1, 1, 2, 2}),
            list(new int[]{1, 1, 2, 2}, new int[]{1, 2, 1, 2}, new int[]{1, 2, 2, 1},
                 new int[]{2, 1, 1, 2}, new int[]{2, 1, 2, 1}, new int[]{2, 2, 1, 1}));

        // Test 9: Larger input
        test("Four elements [1,1,1,2]",
            sol.permuteUnique(new int[]{1, 1, 1, 2}),
            list(new int[]{1, 1, 1, 2}, new int[]{1, 1, 2, 1},
                 new int[]{1, 2, 1, 1}, new int[]{2, 1, 1, 1}));

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
