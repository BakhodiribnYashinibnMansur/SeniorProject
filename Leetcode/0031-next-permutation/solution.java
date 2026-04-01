/**
 * 0031. Next Permutation
 * https://leetcode.com/problems/next-permutation/
 * Difficulty: Medium
 * Tags: Array, Two Pointers
 */
class Solution {

    /**
     * Optimal Solution (Next Permutation Algorithm)
     * Approach: Find pivot, swap with next larger, reverse suffix
     * Time:  O(n) — at most 3 linear scans
     * Space: O(1) — in-place swaps only
     */
    public void nextPermutation(int[] nums) {
        int n = nums.length;

        // Step 1: Find the pivot — rightmost i where nums[i] < nums[i+1]
        int i = n - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }

        // Step 2 & 3: Find the swap target and swap
        if (i >= 0) {
            int j = n - 1;
            while (nums[j] <= nums[i]) {
                j--;
            }
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }

        // Step 4: Reverse the suffix after the pivot
        int left = i + 1, right = n - 1;
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] nums, int[] expected) {
        Solution sol = new Solution();
        sol.nextPermutation(nums);
        if (java.util.Arrays.equals(nums, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, java.util.Arrays.toString(nums), java.util.Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Next Permutation Algorithm ===");

        // Test 1: LeetCode Example 1
        test("Example 1", new int[]{1, 2, 3}, new int[]{1, 3, 2});

        // Test 2: LeetCode Example 2 — last permutation wraps around
        test("Example 2", new int[]{3, 2, 1}, new int[]{1, 2, 3});

        // Test 3: LeetCode Example 3 — duplicates
        test("Example 3", new int[]{1, 1, 5}, new int[]{1, 5, 1});

        // Test 4: Single element
        test("Single element", new int[]{1}, new int[]{1});

        // Test 5: Two elements ascending
        test("Two elements ascending", new int[]{1, 2}, new int[]{2, 1});

        // Test 6: Two elements descending
        test("Two elements descending", new int[]{2, 1}, new int[]{1, 2});

        // Test 7: All same elements
        test("All same elements", new int[]{2, 2, 2}, new int[]{2, 2, 2});

        // Test 8: Pivot at first position
        test("Pivot at first", new int[]{1, 5, 4, 3, 2}, new int[]{2, 1, 3, 4, 5});

        // Test 9: Longer array
        test("Longer array", new int[]{1, 3, 5, 4, 2}, new int[]{1, 4, 2, 3, 5});

        // Test 10: Middle pivot
        test("Middle pivot", new int[]{2, 3, 1}, new int[]{3, 1, 2});

        // Test 11: Duplicates in suffix
        test("Duplicates in suffix", new int[]{1, 3, 2, 2}, new int[]{2, 1, 2, 3});

        // Test 12: Already near last
        test("Near last", new int[]{5, 4, 7, 5, 3, 2}, new int[]{5, 5, 2, 3, 4, 7});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
