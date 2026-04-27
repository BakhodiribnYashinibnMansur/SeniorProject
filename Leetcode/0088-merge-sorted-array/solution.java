import java.util.*;

class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1, j = n - 1, k = m + n - 1;
        while (j >= 0) {
            if (i >= 0 && nums1[i] > nums2[j]) nums1[k--] = nums1[i--];
            else nums1[k--] = nums2[j--];
        }
    }

    static int passed = 0, failed = 0;
    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + Arrays.toString(got)); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{1,2,3,0,0,0}, 3, new int[]{2,5,6}, 3, new int[]{1,2,2,3,5,6}},
            {"Example 2", new int[]{1}, 1, new int[]{}, 0, new int[]{1}},
            {"Example 3", new int[]{0}, 0, new int[]{1}, 1, new int[]{1}},
            {"All nums2 smaller", new int[]{4,5,6,0,0,0}, 3, new int[]{1,2,3}, 3, new int[]{1,2,3,4,5,6}},
            {"All nums2 larger", new int[]{1,2,3,0,0,0}, 3, new int[]{4,5,6}, 3, new int[]{1,2,3,4,5,6}},
            {"Interleaved", new int[]{1,3,5,0,0,0}, 3, new int[]{2,4,6}, 3, new int[]{1,2,3,4,5,6}},
            {"With duplicates", new int[]{1,2,2,0,0,0}, 3, new int[]{2,2,2}, 3, new int[]{1,2,2,2,2,2}},
            {"Single element each", new int[]{1,0}, 1, new int[]{2}, 1, new int[]{1,2}}
        };
        for (Object[] c : cases) {
            int[] n1 = ((int[]) c[1]).clone();
            int[] n2 = ((int[]) c[3]).clone();
            sol.merge(n1, (int) c[2], n2, (int) c[4]);
            test((String) c[0], n1, (int[]) c[5]);
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
