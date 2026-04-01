/**
 * 0043. Multiply Strings
 * https://leetcode.com/problems/multiply-strings/
 * Difficulty: Medium
 * Tags: Math, String, Simulation
 */
class Solution {

    /**
     * Optimal Solution (Grade School Multiplication)
     * Approach: Multiply digit by digit, accumulate at correct positions
     * Time:  O(m*n) — multiply every digit pair
     * Space: O(m+n) — result array of size m+n
     */
    public String multiply(String num1, String num2) {
        // Edge case: anything times zero is zero
        if (num1.equals("0") || num2.equals("0")) {
            return "0";
        }

        int m = num1.length(), n = num2.length();
        // Product of m-digit and n-digit numbers has at most m+n digits
        int[] result = new int[m + n];

        // Multiply each digit pair and accumulate at correct positions
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                int p1 = i + j, p2 = i + j + 1; // p1=tens position, p2=ones position

                // Add product to the ones position and propagate carry
                int sum = mul + result[p2];
                result[p2] = sum % 10;
                result[p1] += sum / 10;
            }
        }

        // Build result string, skip leading zeros
        StringBuilder sb = new StringBuilder();
        for (int d : result) {
            if (sb.length() == 0 && d == 0) continue;
            sb.append(d);
        }

        return sb.length() == 0 ? "0" : sb.toString();
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, String got, String expected) {
        if (got.equals(expected)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Grade School Multiplication (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1", sol.multiply("2", "3"), "6");

        // Test 2: LeetCode Example 2
        test("Example 2", sol.multiply("123", "456"), "56088");

        // Test 3: Zero times number
        test("Zero * number", sol.multiply("0", "12345"), "0");

        // Test 4: Number times zero
        test("Number * zero", sol.multiply("12345", "0"), "0");

        // Test 5: Both zeros
        test("Zero * zero", sol.multiply("0", "0"), "0");

        // Test 6: Single digit multiplication
        test("Single digits", sol.multiply("9", "9"), "81");

        // Test 7: One times number (identity)
        test("Identity", sol.multiply("1", "999"), "999");

        // Test 8: Large carry propagation
        test("Large carry", sol.multiply("999", "999"), "998001");

        // Test 9: Different lengths
        test("Different lengths", sol.multiply("12", "3456"), "41472");

        // Test 10: Result with internal zeros
        test("Internal zeros", sol.multiply("100", "100"), "10000");

        // Test 11: Large numbers
        test("Large numbers", sol.multiply("123456789", "987654321"), "121932631112635269");

        // Test 12: Power of 10
        test("Power of 10", sol.multiply("10", "10"), "100");

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
