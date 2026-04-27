import java.util.*;

/**
 * 0080. Remove Duplicates from Sorted Array II
 * https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/
 * Difficulty: Medium
 * Tags: Array, Two Pointers
 */
class Solution {

    /**
     * Optimal Solution (Two Pointers, At-Most-K = 2).
     * Time:  O(n)
     * Space: O(1)
     */
    public int removeDuplicates(int[] nums) {
        int k = 2;
        if (nums.length <= k) return nums.length;
        int i = k;
        for (int j = k; j < nums.length; j++) {
            if (nums[j] != nums[i - k]) {
                nums[i] = nums[j];
                i++;
            }
        }
        return i;
    }

    static int passed = 0, failed = 0;
    static void test(String name, int gotLen, int[] gotPrefix, int wantLen, int[] wantPrefix) {
        if (gotLen == wantLen && Arrays.equals(gotPrefix, wantPrefix)) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name + " got=" + gotLen);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{1, 1, 1, 2, 2, 3}, new int[]{1, 1, 2, 2, 3}},
            {"Example 2", new int[]{0, 0, 1, 1, 1, 1, 2, 3, 3}, new int[]{0, 0, 1, 1, 2, 3, 3}},
            {"All same", new int[]{5, 5, 5, 5, 5}, new int[]{5, 5}},
            {"All distinct", new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5}},
            {"Single", new int[]{7}, new int[]{7}},
            {"Two same", new int[]{4, 4}, new int[]{4, 4}},
            {"Three same", new int[]{4, 4, 4}, new int[]{4, 4}},
            {"Negatives", new int[]{-3, -3, -3, 0, 0, 0, 1}, new int[]{-3, -3, 0, 0, 1}},
            {"Long", new int[]{1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 5, 5, 5, 6}, new int[]{1, 1, 2, 2, 3, 3, 4, 5, 5, 6}}
        };
        for (Object[] c : cases) {
            int[] in = ((int[]) c[1]).clone();
            int k = sol.removeDuplicates(in);
            int[] prefix = Arrays.copyOf(in, k);
            test((String) c[0], k, prefix, ((int[]) c[2]).length, (int[]) c[2]);
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
