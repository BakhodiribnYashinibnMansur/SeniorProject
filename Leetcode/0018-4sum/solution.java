import java.util.*;

/**
 * 0018. 4Sum
 * https://leetcode.com/problems/4sum/
 * Difficulty: Medium
 * Tags: Array, Two Pointers, Sorting
 */
class Solution {

    /**
     * Optimal Solution (Sort + Two Pointers)
     * Approach: Sort, fix two elements (i, j), use Two Pointers for the remaining two
     * Time:  O(n^3) — two outer loops O(n^2) * inner two-pointer scan O(n)
     * Space: O(1)   — sorting in place, output not counted
     */
    public List<List<Integer>> fourSum(int[] nums, int target) {
        // 1. Sort the array — enables Two Pointers and duplicate skipping
        Arrays.sort(nums);
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();

        // 2. Fix the first element (i)
        for (int i = 0; i < n - 3; i++) {
            // Skip duplicate values for the first element
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            // Early termination: if smallest possible sum > target
            if ((long) nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] > target) break;

            // Skip: if largest possible sum with nums[i] < target
            if ((long) nums[i] + nums[n - 3] + nums[n - 2] + nums[n - 1] < target) continue;

            // 3. Fix the second element (j)
            for (int j = i + 1; j < n - 2; j++) {
                // Skip duplicate values for the second element
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;

                // Early termination for j
                if ((long) nums[i] + nums[j] + nums[j + 1] + nums[j + 2] > target) break;

                // Skip for j
                if ((long) nums[i] + nums[j] + nums[n - 2] + nums[n - 1] < target) continue;

                // 4. Two Pointers for the remaining subarray
                int left = j + 1, right = n - 1;
                long remain = (long) target - nums[i] - nums[j];

                while (left < right) {
                    long sum = (long) nums[left] + nums[right];

                    if (sum == remain) {
                        // Found a valid quadruplet
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));

                        // Skip duplicates for the third element
                        while (left < right && nums[left] == nums[left + 1]) left++;
                        // Skip duplicates for the fourth element
                        while (left < right && nums[right] == nums[right - 1]) right--;

                        // Move both pointers inward
                        left++;
                        right--;
                    } else if (sum < remain) {
                        left++;  // Need a larger sum
                    } else {
                        right--; // Need a smaller sum
                    }
                }
            }
        }

        return result;
    }

    /**
     * Brute Force approach
     * Approach: Check all possible quadruplets with four nested loops
     * Time:  O(n^4) — four nested loops
     * Space: O(n)   — set for deduplication
     */
    public List<List<Integer>> fourSumBruteForce(int[] nums, int target) {
        Arrays.sort(nums);
        int n = nums.length;
        Set<List<Integer>> resultSet = new LinkedHashSet<>();

        for (int i = 0; i < n - 3; i++) {
            for (int j = i + 1; j < n - 2; j++) {
                for (int k = j + 1; k < n - 1; k++) {
                    for (int l = k + 1; l < n; l++) {
                        if ((long) nums[i] + nums[j] + nums[k] + nums[l] == target) {
                            resultSet.add(Arrays.asList(nums[i], nums[j], nums[k], nums[l]));
                        }
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
        for (List<Integer> quad : result) {
            List<Integer> copy = new ArrayList<>(quad);
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

    static List<List<Integer>> list(int[]... quads) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] q : quads) {
            List<Integer> list = new ArrayList<>();
            for (int v : q) list.add(v);
            result.add(list);
        }
        return result;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Sort + Two Pointers (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1",
            sol.fourSum(new int[]{1, 0, -1, 0, -2, 2}, 0),
            list(new int[]{-2, -1, 1, 2}, new int[]{-2, 0, 0, 2}, new int[]{-1, 0, 0, 1}));

        // Test 2: LeetCode Example 2
        test("Example 2",
            sol.fourSum(new int[]{2, 2, 2, 2, 2}, 8),
            list(new int[]{2, 2, 2, 2}));

        // Test 3: No quadruplets
        test("No quadruplets",
            sol.fourSum(new int[]{1, 2, 3, 4, 5}, 100),
            list());

        // Test 4: Negative target
        test("Negative target",
            sol.fourSum(new int[]{-3, -2, -1, 0, 0, 1, 2, 3}, -1),
            list(new int[]{-3, -2, 1, 3}, new int[]{-3, -1, 0, 3}, new int[]{-3, -1, 1, 2},
                 new int[]{-3, 0, 0, 2}, new int[]{-2, -1, 0, 2}, new int[]{-2, 0, 0, 1}));

        // Test 5: All zeros
        test("All zeros target 0",
            sol.fourSum(new int[]{0, 0, 0, 0}, 0),
            list(new int[]{0, 0, 0, 0}));

        // Test 6: Less than 4 elements
        test("Less than 4 elements",
            sol.fourSum(new int[]{1, 2, 3}, 6),
            list());

        // Test 7: Large values — overflow check
        test("Large values",
            sol.fourSum(new int[]{1000000000, 1000000000, 1000000000, 1000000000}, -294967296),
            list());

        // Test 8: Two quadruplets
        test("Two quadruplets",
            sol.fourSum(new int[]{-1, 0, 1, 2, -1, -4}, -1),
            list(new int[]{-4, 0, 1, 2}, new int[]{-1, -1, 0, 1}));

        // Test 9: Empty array
        test("Empty array",
            sol.fourSum(new int[]{}, 0),
            list());

        System.out.println("\n=== Brute Force ===");

        test("BF: Example 1",
            sol.fourSumBruteForce(new int[]{1, 0, -1, 0, -2, 2}, 0),
            list(new int[]{-2, -1, 1, 2}, new int[]{-2, 0, 0, 2}, new int[]{-1, 0, 0, 1}));

        test("BF: Example 2",
            sol.fourSumBruteForce(new int[]{2, 2, 2, 2, 2}, 8),
            list(new int[]{2, 2, 2, 2}));

        test("BF: All zeros",
            sol.fourSumBruteForce(new int[]{0, 0, 0, 0}, 0),
            list(new int[]{0, 0, 0, 0}));

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
