import java.util.*;

/**
 * 0066. Plus One
 * https://leetcode.com/problems/plus-one/
 * Difficulty: Easy
 * Tags: Array, Math
 */
class Solution {

    /**
     * Optimal Solution (Walk + Carry).
     * Time:  O(n)
     * Space: O(1) amortized
     */
    public int[] plusOne(int[] digits) {
        int carry = 1;
        for (int i = digits.length - 1; i >= 0 && carry > 0; i--) {
            int s = digits[i] + carry;
            digits[i] = s % 10;
            carry = s / 10;
        }
        if (carry > 0) {
            int[] out = new int[digits.length + 1];
            out[0] = carry;
            System.arraycopy(digits, 0, out, 1, digits.length);
            return out;
        }
        return digits;
    }

    public int[] plusOneEarly(int[] digits) {
        int n = digits.length;
        for (int i = n - 1; i >= 0; i--) {
            if (digits[i] != 9) {
                digits[i]++;
                for (int j = i + 1; j < n; j++) digits[j] = 0;
                return digits;
            }
        }
        int[] out = new int[n + 1];
        out[0] = 1;
        return out;
    }

    static int passed = 0, failed = 0;
    static int[] cloneArr(int[] a) { return a.clone(); }
    static void test(String name, int[] got, int[] expected) {
        if (Arrays.equals(got, expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + Arrays.toString(got)); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", new int[]{1, 2, 3}, new int[]{1, 2, 4}},
            {"Example 2", new int[]{4, 3, 2, 1}, new int[]{4, 3, 2, 2}},
            {"Example 3", new int[]{9}, new int[]{1, 0}},
            {"Single zero", new int[]{0}, new int[]{1}},
            {"All nines 3", new int[]{9, 9, 9}, new int[]{1, 0, 0, 0}},
            {"All nines 5", new int[]{9, 9, 9, 9, 9}, new int[]{1, 0, 0, 0, 0, 0}},
            {"Trailing zeros", new int[]{1, 0, 0}, new int[]{1, 0, 1}},
            {"Trailing nines", new int[]{1, 9, 9}, new int[]{2, 0, 0}},
            {"Mid nines", new int[]{2, 9, 9, 1}, new int[]{2, 9, 9, 2}},
            {"Long number",
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0},
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1}}
        };

        System.out.println("=== Walk + Carry ===");
        for (Object[] c : cases) test((String) c[0], sol.plusOne(cloneArr((int[]) c[1])), (int[]) c[2]);

        System.out.println("\n=== Early Exit ===");
        for (Object[] c : cases) test("Early " + c[0], sol.plusOneEarly(cloneArr((int[]) c[1])), (int[]) c[2]);

        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
