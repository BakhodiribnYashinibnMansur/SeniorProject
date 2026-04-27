import java.util.*;

/**
 * 0075. Sort Colors
 * https://leetcode.com/problems/sort-colors/
 * Difficulty: Medium
 * Tags: Array, Two Pointers, Sorting
 */
class Solution {

    /**
     * Optimal Solution (Dutch National Flag).
     * Time:  O(n)
     * Space: O(1)
     */
    public void sortColors(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;
        while (mid <= high) {
            if (nums[mid] == 0) {
                int t = nums[low]; nums[low] = nums[mid]; nums[mid] = t;
                low++; mid++;
            } else if (nums[mid] == 1) {
                mid++;
            } else {
                int t = nums[mid]; nums[mid] = nums[high]; nums[high] = t;
                high--;
            }
        }
    }

    public void sortColorsCount(int[] nums) {
        int[] c = new int[3];
        for (int x : nums) c[x]++;
        int i = 0;
        for (int v = 0; v < 3; v++)
            for (int k = 0; k < c[v]; k++) nums[i++] = v;
    }

    static int passed = 0, failed = 0;
    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + Arrays.toString(got)); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{2, 0, 2, 1, 1, 0}, new int[]{0, 0, 1, 1, 2, 2}},
            {"Example 2", new int[]{2, 0, 1}, new int[]{0, 1, 2}},
            {"All zeros", new int[]{0, 0, 0}, new int[]{0, 0, 0}},
            {"All ones", new int[]{1, 1, 1}, new int[]{1, 1, 1}},
            {"All twos", new int[]{2, 2, 2}, new int[]{2, 2, 2}},
            {"Already sorted", new int[]{0, 1, 2}, new int[]{0, 1, 2}},
            {"Reverse sorted", new int[]{2, 1, 0}, new int[]{0, 1, 2}},
            {"Single element 0", new int[]{0}, new int[]{0}},
            {"Single element 2", new int[]{2}, new int[]{2}},
            {"Long mix", new int[]{0,1,2,0,1,2,0,1,2}, new int[]{0,0,0,1,1,1,2,2,2}},
            {"Two zeros one one", new int[]{1, 0, 0}, new int[]{0, 0, 1}}
        };
        System.out.println("=== Dutch National Flag ===");
        for (Object[] c : cases) {
            int[] got = ((int[]) c[1]).clone();
            sol.sortColors(got);
            test((String) c[0], got, (int[]) c[2]);
        }
        System.out.println("\n=== Counting Sort ===");
        for (Object[] c : cases) {
            int[] got = ((int[]) c[1]).clone();
            sol.sortColorsCount(got);
            test("Count " + c[0], got, (int[]) c[2]);
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
