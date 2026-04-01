import java.util.Arrays;

/**
 * 0026. Remove Duplicates from Sorted Array
 * https://leetcode.com/problems/remove-duplicates-from-sorted-array/
 * Difficulty: Easy
 * Tags: Array, Two Pointers
 */
class Solution {

    /**
     * Optimal Solution (Two Pointers)
     * Approach: slow pointer tracks unique position, fast pointer scans
     * Time:  O(n) — single pass through the array
     * Space: O(1) — only two pointer variables
     */
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        // slow points to the last unique element
        int slow = 0;

        for (int fast = 1; fast < nums.length; fast++) {
            // Found a new unique element
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
        }

        // slow is 0-indexed, so count = slow + 1
        return slow + 1;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] nums, int expectedK, int[] expectedNums) {
        Solution sol = new Solution();
        int[] numsCopy = Arrays.copyOf(nums, nums.length);
        int gotK = sol.removeDuplicates(numsCopy);
        int[] gotNums = Arrays.copyOf(numsCopy, gotK);
        if (gotK == expectedK && Arrays.equals(gotNums, expectedNums)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      k=%d, nums=%s%n  Expected: k=%d, nums=%s%n",
                name, gotK, Arrays.toString(gotNums), expectedK, Arrays.toString(expectedNums));
            failed++;
        }
    }

    public static void main(String[] args) {
        // Test 1: Basic case with one duplicate
        test("Basic case", new int[]{1, 1, 2}, 2, new int[]{1, 2});

        // Test 2: Multiple duplicates
        test("Multiple duplicates", new int[]{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}, 5, new int[]{0, 1, 2, 3, 4});

        // Test 3: Single element
        test("Single element", new int[]{1}, 1, new int[]{1});

        // Test 4: All same elements
        test("All same elements", new int[]{1, 1, 1, 1}, 1, new int[]{1});

        // Test 5: No duplicates
        test("No duplicates", new int[]{1, 2, 3, 4, 5}, 5, new int[]{1, 2, 3, 4, 5});

        // Test 6: Two elements — same
        test("Two elements same", new int[]{1, 1}, 1, new int[]{1});

        // Test 7: Two elements — different
        test("Two elements different", new int[]{1, 2}, 2, new int[]{1, 2});

        // Test 8: Negative numbers
        test("Negative numbers", new int[]{-3, -1, 0, 0, 2}, 4, new int[]{-3, -1, 0, 2});

        // Test 9: Large consecutive duplicates
        test("Large consecutive duplicates", new int[]{0, 0, 0, 0, 1, 1, 1, 2}, 3, new int[]{0, 1, 2});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
