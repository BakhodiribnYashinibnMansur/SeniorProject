/**
 * 0007. Reverse Integer
 * https://leetcode.com/problems/reverse-integer/
 * Difficulty: Medium
 * Tags: Math
 */
class Solution {

    /**
     * Optimal Solution (Mathematical Digit Pop)
     * Approach: Pop digits one by one using modulo; check overflow before each push.
     * Time:  O(log x) — number of digits in x (at most 10 for a 32-bit integer)
     * Space: O(1)    — only a few integer variables
     */
    public int reverse(int x) {
        int rev = 0;

        while (x != 0) {
            // Pop the last digit
            int digit = x % 10;
            x /= 10;

            // Overflow check BEFORE pushing digit onto rev:
            // Integer.MAX_VALUE =  2147483647 → last digit 7
            // Integer.MIN_VALUE = -2147483648 → last digit -8
            if (rev > Integer.MAX_VALUE / 10 ||
               (rev == Integer.MAX_VALUE / 10 && digit > 7)) {
                return 0;
            }
            if (rev < Integer.MIN_VALUE / 10 ||
               (rev == Integer.MIN_VALUE / 10 && digit < -8)) {
                return 0;
            }

            // Push digit onto the reversed number
            rev = rev * 10 + digit;
        }

        return rev;
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

        // Test 1: Basic positive number
        test("Positive 123", sol.reverse(123), 321);

        // Test 2: Negative number
        test("Negative -123", sol.reverse(-123), -321);

        // Test 3: Trailing zero is dropped
        test("Trailing zero 120", sol.reverse(120), 21);

        // Test 4: Single digit
        test("Single digit 5", sol.reverse(5), 5);

        // Test 5: Zero
        test("Zero", sol.reverse(0), 0);

        // Test 6: Overflow — reversed value exceeds Integer.MAX_VALUE
        // 1534236469 reversed = 9646324351 > 2^31 - 1
        test("Overflow positive", sol.reverse(1534236469), 0);

        // Test 7: Overflow — reversed value below Integer.MIN_VALUE
        // -1534236469 reversed = -9646324351 < -2^31
        test("Overflow negative", sol.reverse(-1534236469), 0);

        // Test 8: Integer.MAX_VALUE reversed → overflows
        test("INT_MAX reversed overflows", sol.reverse(Integer.MAX_VALUE), 0);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
