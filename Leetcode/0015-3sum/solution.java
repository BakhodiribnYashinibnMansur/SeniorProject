import java.util.*;

/**
 * 0015. 3Sum
 * https://leetcode.com/problems/3sum/
 * Difficulty: Medium
 * Tags: Array, Two Pointers, Sorting
 */
class Solution {

    /**
     * Optimal Solution (Sort + Two Pointers)
     * Approach: Sort, fix one element, use Two Pointers for the remaining two
     * Time:  O(n^2) — outer loop O(n) * inner two-pointer scan O(n)
     * Space: O(1)   — sorting in place, output not counted
     */
    public List<List<Integer>> threeSum(int[] nums) {
        // 1. Sort the array — enables Two Pointers and duplicate skipping
        Arrays.sort(nums);
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();

        // 2. Fix the first element (i), then find two elements that sum to -nums[i]
        for (int i = 0; i < n - 2; i++) {
            // Skip duplicate values for the first element
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            // Early termination: if smallest value > 0, no triplet can sum to 0
            if (nums[i] > 0) break;

            // Two Pointers for the remaining subarray
            int left = i + 1, right = n - 1;
            int target = -nums[i];

            while (left < right) {
                int sum = nums[left] + nums[right];

                if (sum == target) {
                    // Found a valid triplet
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    // Skip duplicates for the second element
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    // Skip duplicates for the third element
                    while (left < right && nums[right] == nums[right - 1]) right--;

                    // Move both pointers inward
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;  // Need a larger sum
                } else {
                    right--; // Need a smaller sum
                }
            }
        }

        return result;
    }

    /**
     * Brute Force approach
     * Approach: Check all possible triplets with three nested loops
     * Time:  O(n^3) — three nested loops
     * Space: O(n)   — set for deduplication
     */
    public List<List<Integer>> threeSumBruteForce(int[] nums) {
        Arrays.sort(nums);
        int n = nums.length;
        Set<List<Integer>> resultSet = new LinkedHashSet<>();

        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        resultSet.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    }
                }
            }
        }

        return new ArrayList<>(resultSet);
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
        for (List<Integer> triplet : result) {
            List<Integer> copy = new ArrayList<>(triplet);
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

    static List<List<Integer>> list(int[]... triplets) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] t : triplets) {
            List<Integer> list = new ArrayList<>();
            for (int v : t) list.add(v);
            result.add(list);
        }
        return result;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Sort + Two Pointers (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1",
            sol.threeSum(new int[]{-1, 0, 1, 2, -1, -4}),
            list(new int[]{-1, -1, 2}, new int[]{-1, 0, 1}));

        // Test 2: All zeros
        test("All zeros",
            sol.threeSum(new int[]{0, 0, 0}),
            list(new int[]{0, 0, 0}));

        // Test 3: No triplet sums to zero
        test("No triplets [0,1,1]",
            sol.threeSum(new int[]{0, 1, 1}),
            list());

        // Test 4: Empty array
        test("Empty array",
            sol.threeSum(new int[]{}),
            list());

        // Test 5: All positive
        test("All positive",
            sol.threeSum(new int[]{1, 2, 3, 4, 5}),
            list());

        // Test 6: All negative
        test("All negative",
            sol.threeSum(new int[]{-5, -4, -3, -2, -1}),
            list());

        // Test 7: Multiple triplets with duplicates
        test("Multiple triplets",
            sol.threeSum(new int[]{-2, 0, 1, 1, 2}),
            list(new int[]{-2, 0, 2}, new int[]{-2, 1, 1}));

        // Test 8: Many zeros
        test("Many zeros",
            sol.threeSum(new int[]{0, 0, 0, 0, 0}),
            list(new int[]{0, 0, 0}));

        // Test 9: Two elements only
        test("Two elements",
            sol.threeSum(new int[]{-1, 1}),
            list());

        System.out.println("\n=== Brute Force ===");

        test("BF: Example 1",
            sol.threeSumBruteForce(new int[]{-1, 0, 1, 2, -1, -4}),
            list(new int[]{-1, -1, 2}, new int[]{-1, 0, 1}));

        test("BF: All zeros",
            sol.threeSumBruteForce(new int[]{0, 0, 0}),
            list(new int[]{0, 0, 0}));

        test("BF: No triplets",
            sol.threeSumBruteForce(new int[]{0, 1, 1}),
            list());

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
