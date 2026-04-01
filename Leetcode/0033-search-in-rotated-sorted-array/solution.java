/**
 * 0033. Search in Rotated Sorted Array
 * https://leetcode.com/problems/search-in-rotated-sorted-array/
 * Difficulty: Medium
 * Tags: Array, Binary Search
 */
class Solution {

    /**
     * Modified Binary Search on rotated sorted array
     * Approach: Determine which half is sorted, then decide search direction
     * Time:  O(log n) — each step halves the search space
     * Space: O(1) — only pointer variables
     */
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Found the target
            if (nums[mid] == target) {
                return mid;
            }

            // Determine which half is sorted
            if (nums[left] <= nums[mid]) {
                // Left half [left..mid] is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1; // Target is in the sorted left half
                } else {
                    left = mid + 1; // Target is in the right half
                }
            } else {
                // Right half [mid..right] is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1; // Target is in the sorted right half
                } else {
                    right = mid - 1; // Target is in the left half
                }
            }
        }

        return -1;
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

        // Test 1: Basic case — target in right half
        test("Basic case", sol.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 0), 4);

        // Test 2: Target not in array
        test("Target not found", sol.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 3), -1);

        // Test 3: Single element — not found
        test("Single element not found", sol.search(new int[]{1}, 0), -1);

        // Test 4: Single element — found
        test("Single element found", sol.search(new int[]{1}, 1), 0);

        // Test 5: No rotation
        test("No rotation", sol.search(new int[]{1, 2, 3, 4, 5}, 3), 2);

        // Test 6: Target at first position
        test("Target at first", sol.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 4), 0);

        // Test 7: Target at last position
        test("Target at last", sol.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 2), 6);

        // Test 8: Target at rotation point
        test("Target at pivot", sol.search(new int[]{6, 7, 0, 1, 2, 4, 5}, 0), 2);

        // Test 9: Two elements — rotated
        test("Two elements rotated", sol.search(new int[]{3, 1}, 1), 1);

        // Test 10: Two elements — not found
        test("Two elements not found", sol.search(new int[]{3, 1}, 2), -1);

        // Test 11: Target in left sorted half
        test("Target in left half", sol.search(new int[]{4, 5, 6, 7, 0, 1, 2}, 5), 1);

        // Test 12: Large rotation
        test("Large rotation", sol.search(new int[]{2, 3, 4, 5, 1}, 1), 4);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
