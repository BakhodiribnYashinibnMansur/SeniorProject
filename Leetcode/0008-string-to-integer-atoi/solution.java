/**
 * 0008. String to Integer (atoi)
 * https://leetcode.com/problems/string-to-integer-atoi/
 * Difficulty: Medium
 * Tags: String, Simulation
 */
class Solution {

    /**
     * Optimal Solution (Single-pass Simulation)
     * Time:  O(n) — single pass through the string characters
     * Space: O(1) — only a few integer variables used
     */
    public int myAtoi(String s) {
        int i = 0;
        int n = s.length();

        // Step 1: Skip leading whitespace
        while (i < n && s.charAt(i) == ' ') {
            i++;
        }

        // Step 2: Determine sign
        int sign = 1;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            if (s.charAt(i) == '-') {
                sign = -1;
            }
            i++;
        }

        // Step 3: Read digits and build result
        int result = 0;
        while (i < n && Character.isDigit(s.charAt(i))) {
            int digit = s.charAt(i) - '0';

            // Step 4: Check for overflow BEFORE updating result
            // If result > INT_MAX/10, the next multiply will overflow
            // If result == INT_MAX/10 and digit > 7, it will overflow
            if (result > Integer.MAX_VALUE / 10 ||
               (result == Integer.MAX_VALUE / 10 && digit > 7)) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            result = result * 10 + digit;
            i++;
        }

        return sign * result;
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
        test("Basic positive", sol.myAtoi("42"), 42);

        // Test 2: Leading whitespace and negative sign
        test("Leading spaces, negative", sol.myAtoi("   -42"), -42);

        // Test 3: Digits followed by letters — stop at non-digit
        test("Digits then words", sol.myAtoi("4193 with words"), 4193);

        // Test 4: Leading letters — no digits found, return 0
        test("Words then digits", sol.myAtoi("words and 987"), 0);

        // Test 5: Overflow negative — clamp to INT_MIN
        test("Overflow negative clamp", sol.myAtoi("-91283472332"), -2147483648);

        // Test 6: Overflow positive — clamp to INT_MAX
        test("Overflow positive clamp", sol.myAtoi("9999999999"), 2147483647);

        // Test 7: Explicit plus sign
        test("Explicit plus sign", sol.myAtoi("+1"), 1);

        // Test 8: Empty string
        test("Empty string", sol.myAtoi(""), 0);

        // Test 9: Only whitespace
        test("Only whitespace", sol.myAtoi("   "), 0);

        // Test 10: Zero
        test("Zero", sol.myAtoi("0"), 0);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
