import java.util.Arrays;

/**
 * 0034. Find First and Last Position of Element in Sorted Array
 * https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
 * Difficulty: Medium
 * Tags: Array, Binary Search
 */
class Solution {

    /**
     * Optimal Solution (Two Binary Searches)
     * Approach: Find left bound then right bound using modified binary search
     * Time:  O(log n) — two binary searches, each O(log n)
     * Space: O(1) — only a few variables
     */
    public int[] searchRange(int[] nums, int target) {
        int left = findLeft(nums, target);
        if (left == -1) {
            return new int[]{-1, -1};
        }
        int right = findRight(nums, target);
        return new int[]{left, right};
    }

    /**
     * Find the first (leftmost) occurrence of target.
     */
    private int findLeft(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        int result = -1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                result = mid;
                hi = mid - 1; // keep searching left
            } else if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return result;
    }

    /**
     * Find the last (rightmost) occurrence of target.
     */
    private int findRight(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        int result = -1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                result = mid;
                lo = mid + 1; // keep searching right
            } else if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }

        return result;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Two Binary Searches (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1", sol.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 8), new int[]{3, 4});

        // Test 2: LeetCode Example 2
        test("Example 2", sol.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 6), new int[]{-1, -1});

        // Test 3: LeetCode Example 3
        test("Example 3 (empty)", sol.searchRange(new int[]{}, 0), new int[]{-1, -1});

        // Test 4: Single element found
        test("Single element found", sol.searchRange(new int[]{1}, 1), new int[]{0, 0});

        // Test 5: Single element not found
        test("Single element not found", sol.searchRange(new int[]{1}, 2), new int[]{-1, -1});

        // Test 6: All same elements
        test("All same elements", sol.searchRange(new int[]{8, 8, 8, 8, 8}, 8), new int[]{0, 4});

        // Test 7: Target at the beginning
        test("Target at start", sol.searchRange(new int[]{1, 1, 2, 3, 4}, 1), new int[]{0, 1});

        // Test 8: Target at the end
        test("Target at end", sol.searchRange(new int[]{1, 2, 3, 4, 4}, 4), new int[]{3, 4});

        // Test 9: Target smaller than all
        test("Target smaller than all", sol.searchRange(new int[]{2, 3, 4}, 1), new int[]{-1, -1});

        // Test 10: Target larger than all
        test("Target larger than all", sol.searchRange(new int[]{2, 3, 4}, 5), new int[]{-1, -1});

        // Test 11: Single occurrence in the middle
        test("Single occurrence", sol.searchRange(new int[]{1, 2, 3, 4, 5}, 3), new int[]{2, 2});

        // Test 12: Large run of duplicates
        test("Large duplicates", sol.searchRange(new int[]{1, 2, 2, 2, 2, 2, 3}, 2), new int[]{1, 5});

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
