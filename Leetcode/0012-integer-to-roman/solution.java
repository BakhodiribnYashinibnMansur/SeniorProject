/**
 * 0012. Integer to Roman
 * https://leetcode.com/problems/integer-to-roman/
 * Difficulty: Medium
 * Tags: Hash Table, Math, String
 */
class Solution {

    /**
     * Approach 1: Greedy with Value Table
     * Approach: Use a lookup table of values and symbols sorted descending.
     *           Greedily subtract the largest possible value and append its symbol.
     * Time:  O(1) — bounded by the finite set of roman numeral symbols
     * Space: O(1) — the result string length is bounded
     */
    public String intToRoman(int num) {
        // Value-symbol pairs in descending order
        // Includes subtractive forms (e.g., 900=CM, 400=CD, etc.)
        int[] values =    {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            // While the current value fits into num, append symbol
            while (num >= values[i]) {
                result.append(symbols[i]);
                num -= values[i];
            }
        }

        return result.toString();
    }

    /**
     * Approach 2: Hardcoded Digit Mapping
     * Approach: Predefine roman representations for each digit at each place value.
     *           Extract thousands, hundreds, tens, ones and look up each.
     * Time:  O(1) — always exactly 4 lookups
     * Space: O(1) — lookup tables are constant size
     */
    public String intToRomanDigitMap(int num) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds  = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens      = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones      = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return thousands[num / 1000] + hundreds[(num % 1000) / 100] + tens[(num % 100) / 10] + ones[num % 10];
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, String got, String expected) {
        if (got.equals(expected)) {
            System.out.printf("  PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("  FAIL: %s%n  Got:      \"%s\"%n  Expected: \"%s\"%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Approach 1: Greedy with Value Table ===");

        // Test 1: LeetCode example 1
        test("Example 1 (3749)", sol.intToRoman(3749), "MMMDCCXLIX");

        // Test 2: LeetCode example 2
        test("Example 2 (58)", sol.intToRoman(58), "LVIII");

        // Test 3: LeetCode example 3
        test("Example 3 (1994)", sol.intToRoman(1994), "MCMXCIV");

        // Test 4: Minimum value
        test("Minimum (1)", sol.intToRoman(1), "I");

        // Test 5: Maximum value
        test("Maximum (3999)", sol.intToRoman(3999), "MMMCMXCIX");

        // Test 6: All subtractive forms
        test("Subtractive (944)", sol.intToRoman(944), "CMXLIV");

        // Test 7: Round thousand
        test("Round thousand (2000)", sol.intToRoman(2000), "MM");

        // Test 8: Single symbols
        test("Single symbol (500)", sol.intToRoman(500), "D");

        // Test 9: Repeating symbol
        test("Repeating (3)", sol.intToRoman(3), "III");

        System.out.println("\n=== Approach 2: Hardcoded Digit Mapping ===");

        // Verify Approach 2 matches Approach 1 on all test cases
        test("Digit Map (3749)", sol.intToRomanDigitMap(3749), "MMMDCCXLIX");
        test("Digit Map (58)", sol.intToRomanDigitMap(58), "LVIII");
        test("Digit Map (1994)", sol.intToRomanDigitMap(1994), "MCMXCIV");
        test("Digit Map (1)", sol.intToRomanDigitMap(1), "I");
        test("Digit Map (3999)", sol.intToRomanDigitMap(3999), "MMMCMXCIX");
        test("Digit Map (944)", sol.intToRomanDigitMap(944), "CMXLIV");
        test("Digit Map (2000)", sol.intToRomanDigitMap(2000), "MM");
        test("Digit Map (500)", sol.intToRomanDigitMap(500), "D");
        test("Digit Map (3)", sol.intToRomanDigitMap(3), "III");

        // Results
        System.out.printf("%n  Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
