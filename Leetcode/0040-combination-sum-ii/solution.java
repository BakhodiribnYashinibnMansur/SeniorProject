import java.util.*;

/**
 * 0040. Combination Sum II
 * https://leetcode.com/problems/combination-sum-ii/
 * Difficulty: Medium
 * Tags: Array, Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Backtracking with Duplicate Skipping)
     * Approach: Sort candidates, backtrack with pruning and duplicate skipping
     * Time:  O(2^n) — worst case all subsets explored; much less with pruning
     * Space: O(n)   — recursion depth + current path
     */
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        // 1. Sort candidates — enables pruning and duplicate skipping
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int remaining, int start,
                           List<Integer> current, List<List<Integer>> result) {
        // Base case: found a valid combination
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            // Pruning: if current candidate exceeds remaining, all subsequent will too
            if (candidates[i] > remaining) break;

            // Skip duplicates at the same recursion level
            if (i > start && candidates[i] == candidates[i - 1]) continue;

            // Choose candidates[i]
            current.add(candidates[i]);

            // Recurse: move to i+1 (each element used at most once)
            backtrack(candidates, remaining - candidates[i], i + 1, current, result);

            // Undo choice (backtrack)
            current.remove(current.size() - 1);
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, List<List<Integer>> got, List<List<Integer>> expected) {
        // Sort for consistent comparison
        List<List<Integer>> sortedGot = sortResult(got);
        List<List<Integer>> sortedExpected = sortResult(expected);

        if (sortedGot.equals(sortedExpected)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, sortedGot, sortedExpected);
            failed++;
        }
    }

    static List<List<Integer>> sortResult(List<List<Integer>> result) {
        List<List<Integer>> sorted = new ArrayList<>();
        for (List<Integer> combo : result) {
            List<Integer> copy = new ArrayList<>(combo);
            Collections.sort(copy);
            sorted.add(copy);
        }
        sorted.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                if (!a.get(i).equals(b.get(i))) return a.get(i) - b.get(i);
            }
            return a.size() - b.size();
        });
        return sorted;
    }

    static List<List<Integer>> list(int[]... combos) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] c : combos) {
            List<Integer> list = new ArrayList<>();
            for (int v : c) list.add(v);
            result.add(list);
        }
        return result;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Backtracking with Duplicate Skipping (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1",
            sol.combinationSum2(new int[]{10, 1, 2, 7, 6, 1, 5}, 8),
            list(new int[]{1, 1, 6}, new int[]{1, 2, 5}, new int[]{1, 7}, new int[]{2, 6}));

        // Test 2: LeetCode Example 2
        test("Example 2",
            sol.combinationSum2(new int[]{2, 5, 2, 1, 2}, 5),
            list(new int[]{1, 2, 2}, new int[]{5}));

        // Test 3: Single element match
        test("Single element match",
            sol.combinationSum2(new int[]{1}, 1),
            list(new int[]{1}));

        // Test 4: Single element no match
        test("Single element no match",
            sol.combinationSum2(new int[]{2}, 1),
            list());

        // Test 5: All same elements
        test("All same elements",
            sol.combinationSum2(new int[]{1, 1, 1, 1, 1}, 3),
            list(new int[]{1, 1, 1}));

        // Test 6: No valid combination
        test("No valid combination",
            sol.combinationSum2(new int[]{3, 5, 7}, 1),
            list());

        // Test 7: Exact target with all elements
        test("Use all elements",
            sol.combinationSum2(new int[]{1, 2, 3}, 6),
            list(new int[]{1, 2, 3}));

        // Test 8: Multiple duplicate values
        test("Multiple duplicates",
            sol.combinationSum2(new int[]{1, 1, 1, 2, 2}, 4),
            list(new int[]{1, 1, 2}, new int[]{2, 2}));

        // Test 9: Large single value equals target
        test("Large single match",
            sol.combinationSum2(new int[]{8, 7, 4, 3}, 7),
            list(new int[]{3, 4}, new int[]{7}));

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
