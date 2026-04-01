import java.util.*;

/**
 * 0046. Permutations
 * https://leetcode.com/problems/permutations/
 * Difficulty: Medium
 * Tags: Array, Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Backtracking with swaps)
     * Approach: Fix each element at each position via swapping
     * Time:  O(n * n!) — n! permutations, O(n) to copy each
     * Space: O(n) — recursion depth (excluding output)
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<List<Integer>> result) {
        // Base case: all positions are fixed
        if (start == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int num : nums) perm.add(num);
            result.add(perm);
            return;
        }

        // Try placing each element at position 'start'
        for (int i = start; i < nums.length; i++) {
            // Swap nums[start] and nums[i]
            int temp = nums[start];
            nums[start] = nums[i];
            nums[i] = temp;

            // Recurse on the remaining positions
            backtrack(nums, start + 1, result);

            // Swap back (undo)
            temp = nums[start];
            nums[start] = nums[i];
            nums[i] = temp;
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, List<List<Integer>> got, int[][] expected) {
        Set<List<Integer>> gotSet = new HashSet<>(got);
        Set<List<Integer>> expSet = new HashSet<>();
        for (int[] arr : expected) {
            List<Integer> list = new ArrayList<>();
            for (int v : arr) list.add(v);
            expSet.add(list);
        }

        if (gotSet.equals(expSet) && got.size() == expected.length) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expSet);
            failed++;
        }
    }

    static void testCount(String name, List<List<Integer>> got, int expectedCount) {
        Set<List<Integer>> unique = new HashSet<>(got);
        if (got.size() == expectedCount && unique.size() == expectedCount) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s — got %d permutations, expected %d%n",
                name, got.size(), expectedCount);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Basic case — 3 elements
        test("Basic [1,2,3]",
            sol.permute(new int[]{1, 2, 3}),
            new int[][]{{1,2,3},{1,3,2},{2,1,3},{2,3,1},{3,1,2},{3,2,1}});

        // Test 2: Two elements
        test("Two elements [0,1]",
            sol.permute(new int[]{0, 1}),
            new int[][]{{0,1},{1,0}});

        // Test 3: Single element
        test("Single element [1]",
            sol.permute(new int[]{1}),
            new int[][]{{1}});

        // Test 4: Negative numbers
        test("Negative numbers [-1,0,1]",
            sol.permute(new int[]{-1, 0, 1}),
            new int[][]{{-1,0,1},{-1,1,0},{0,-1,1},{0,1,-1},{1,-1,0},{1,0,-1}});

        // Test 5: Four elements — count check
        testCount("Four elements [1,2,3,4] — 24 permutations",
            sol.permute(new int[]{1, 2, 3, 4}), 24);

        // Test 6: Maximum length — 6 elements
        testCount("Max length [1,2,3,4,5,6] — 720 permutations",
            sol.permute(new int[]{1, 2, 3, 4, 5, 6}), 720);

        // Test 7: Mixed positive/negative
        test("Mixed [-10,10]",
            sol.permute(new int[]{-10, 10}),
            new int[][]{{-10,10},{10,-10}});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
