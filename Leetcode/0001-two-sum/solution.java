import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * 0001. Two Sum
 * https://leetcode.com/problems/two-sum/
 * Difficulty: Easy
 * Tags: Array, Hash Table
 */
class Solution {

    /**
     * Optimal Solution (One-pass Hash Map)
     * Approach: Look up complement in Hash Map
     * Time:  O(n) — single pass through the array
     * Space: O(n) — Hash Map stores at most n elements
     */
    public int[] twoSum(int[] nums, int target) {
        // Hash Map: value → index
        // For each element, check if its complement (target - num)
        // has been seen before
        HashMap<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            // Calculate complement
            int complement = target - nums[i];

            // Is complement in the Hash Map?
            if (seen.containsKey(complement)) {
                // Found! complement's index is seen[complement], current index is i
                return new int[]{seen.get(complement), i};
            }

            // Add current element to Hash Map
            // It will serve as complement for future elements
            seen.put(nums[i], i);
        }

        // Per constraints, a solution always exists
        // This line is never reached
        return new int[]{};
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, Arrays.toString(got), Arrays.toString(expected));
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Basic case — found in first pair
        test("Basic case", sol.twoSum(new int[]{2, 7, 11, 15}, 9), new int[]{0, 1});

        // Test 2: Found in the middle
        test("Found in middle", sol.twoSum(new int[]{3, 2, 4}, 6), new int[]{1, 2});

        // Test 3: Duplicate values
        test("Duplicate values", sol.twoSum(new int[]{3, 3}, 6), new int[]{0, 1});

        // Test 4: Negative numbers
        test("Negative numbers", sol.twoSum(new int[]{-1, -2, -3, -4, -5}, -8), new int[]{2, 4});

        // Test 5: Mixed numbers (negative + positive)
        test("Mixed numbers", sol.twoSum(new int[]{-3, 4, 3, 90}, 0), new int[]{0, 2});

        // Test 6: Zero values
        test("Zero values", sol.twoSum(new int[]{0, 4, 3, 0}, 0), new int[]{0, 3});

        // Test 7: Large values
        test("Large values", sol.twoSum(new int[]{1000000000, -1000000000}, 0), new int[]{0, 1});

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
