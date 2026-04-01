import java.util.*;

/**
 * 0039. Combination Sum
 * https://leetcode.com/problems/combination-sum/
 * Difficulty: Medium
 * Tags: Array, Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Backtracking with Pruning)
     * Approach: Sort candidates, then recursively build combinations.
     *           Use a start index to avoid duplicate combinations.
     *           Reuse candidates by recursing with same index (not i+1).
     *           Prune when candidate exceeds remaining target.
     * Time:  O(n^(T/M)) — n candidates, T target, M min candidate
     * Space: O(T/M)     — max recursion depth
     */
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, 0, target, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int start, int remaining,
                           List<Integer> current, List<List<Integer>> result) {
        // Base case: found a valid combination
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            // Pruning: sorted, so all subsequent candidates are also too large
            if (candidates[i] > remaining) break;

            current.add(candidates[i]);
            backtrack(candidates, i, remaining - candidates[i], current, result); // i, not i+1: allow reuse
            current.remove(current.size() - 1); // undo choice (backtrack)
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, List<List<Integer>> got, List<List<Integer>> expected) {
        // Sort inner lists and outer list for comparison
        String gotStr = normalize(got);
        String expStr = normalize(expected);

        if (gotStr.equals(expStr)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    static String normalize(List<List<Integer>> lists) {
        List<String> strs = new ArrayList<>();
        for (List<Integer> inner : lists) {
            List<Integer> sorted = new ArrayList<>(inner);
            Collections.sort(sorted);
            strs.add(sorted.toString());
        }
        Collections.sort(strs);
        return strs.toString();
    }

    static List<List<Integer>> list(int[]... arrays) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] arr : arrays) {
            List<Integer> inner = new ArrayList<>();
            for (int v : arr) inner.add(v);
            result.add(inner);
        }
        return result;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: LeetCode Example 1
        test("Example 1: [2,3,6,7] target=7",
            sol.combinationSum(new int[]{2, 3, 6, 7}, 7),
            list(new int[]{2, 2, 3}, new int[]{7}));

        // Test 2: LeetCode Example 2
        test("Example 2: [2,3,5] target=8",
            sol.combinationSum(new int[]{2, 3, 5}, 8),
            list(new int[]{2, 2, 2, 2}, new int[]{2, 3, 3}, new int[]{3, 5}));

        // Test 3: LeetCode Example 3 — no valid combination
        test("Example 3: [2] target=1",
            sol.combinationSum(new int[]{2}, 1),
            list());

        // Test 4: Single candidate equals target
        test("Single candidate = target: [7] target=7",
            sol.combinationSum(new int[]{7}, 7),
            list(new int[]{7}));

        // Test 5: Single candidate used multiple times
        test("Single candidate reuse: [3] target=9",
            sol.combinationSum(new int[]{3}, 9),
            list(new int[]{3, 3, 3}));

        // Test 6: All candidates too large
        test("All too large: [5,6,7] target=3",
            sol.combinationSum(new int[]{5, 6, 7}, 3),
            list());

        // Test 7: Target equals smallest candidate
        test("Target = smallest: [2,3,5] target=2",
            sol.combinationSum(new int[]{2, 3, 5}, 2),
            list(new int[]{2}));

        // Test 8: Multiple valid combinations
        test("Multiple combos: [2,3,5] target=10",
            sol.combinationSum(new int[]{2, 3, 5}, 10),
            list(new int[]{2, 2, 2, 2, 2}, new int[]{2, 2, 3, 3}, new int[]{2, 3, 5}, new int[]{5, 5}));

        // Test 9: Larger target
        test("Larger: [2,3,6,7] target=13",
            sol.combinationSum(new int[]{2, 3, 6, 7}, 13),
            list(new int[]{2, 2, 2, 2, 2, 3}, new int[]{2, 2, 2, 7},
                 new int[]{2, 2, 3, 3, 3}, new int[]{2, 2, 3, 6},
                 new int[]{3, 3, 7}, new int[]{6, 7}));

        // Test 10: Single element, not divisible
        test("Not divisible: [3] target=7",
            sol.combinationSum(new int[]{3}, 7),
            list());

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
