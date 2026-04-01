import java.util.Arrays;

/**
 * 0027. Remove Element
 * https://leetcode.com/problems/remove-element/
 * Difficulty: Easy
 * Tags: Array, Two Pointers
 */
class Solution {

    /**
     * Optimal Solution (Two Pointers — Opposite Direction)
     * Approach: Swap val elements with end elements
     * Time:  O(n) — each element visited at most once
     * Space: O(1) — only two pointer variables
     */
    public int removeElement(int[] nums, int val) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            if (nums[left] == val) {
                // Replace with last element, shrink from right
                nums[left] = nums[right];
                right--;
                // Do NOT advance left — swapped element needs checking
            } else {
                left++;
            }
        }

        return left;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] nums, int val, int expectedK, int[] expectedElems) {
        Solution sol = new Solution();
        int[] numsCopy = nums.clone();
        int k = sol.removeElement(numsCopy, val);

        int[] result = Arrays.copyOf(numsCopy, k);
        Arrays.sort(result);
        int[] expected = expectedElems.clone();
        Arrays.sort(expected);

        if (k == expectedK && Arrays.equals(result, expected)) {
            System.out.printf("\u2705 PASS: %s \u2192 k=%d, nums[:k]=%s%n",
                name, k, Arrays.toString(Arrays.copyOf(numsCopy, k)));
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      k=%d, nums[:k]=%s%n  Expected: k=%d, elements=%s%n",
                name, k, Arrays.toString(Arrays.copyOf(numsCopy, k)),
                expectedK, Arrays.toString(expectedElems));
            failed++;
        }
    }

    public static void main(String[] args) {
        // Test 1: Basic case — remove 3s
        test("Basic [3,2,2,3] val=3",
            new int[]{3, 2, 2, 3}, 3, 2, new int[]{2, 2});

        // Test 2: Multiple removals
        test("Multiple [0,1,2,2,3,0,4,2] val=2",
            new int[]{0, 1, 2, 2, 3, 0, 4, 2}, 2, 5, new int[]{0, 1, 3, 0, 4});

        // Test 3: Empty array
        test("Empty array",
            new int[]{}, 1, 0, new int[]{});

        // Test 4: All elements equal val
        test("All same [3,3,3] val=3",
            new int[]{3, 3, 3}, 3, 0, new int[]{});

        // Test 5: No elements equal val
        test("None match [1,2,3] val=4",
            new int[]{1, 2, 3}, 4, 3, new int[]{1, 2, 3});

        // Test 6: Single element (keep)
        test("Single keep [1] val=2",
            new int[]{1}, 2, 1, new int[]{1});

        // Test 7: Single element (remove)
        test("Single remove [1] val=1",
            new int[]{1}, 1, 0, new int[]{});

        // Test 8: Val at beginning
        test("Val at start [3,1,2] val=3",
            new int[]{3, 1, 2}, 3, 2, new int[]{1, 2});

        // Test 9: Val at end
        test("Val at end [1,2,3] val=3",
            new int[]{1, 2, 3}, 3, 2, new int[]{1, 2});

        // Test 10: All same, not val
        test("All same not val [2,2,2] val=3",
            new int[]{2, 2, 2}, 3, 3, new int[]{2, 2, 2});

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
