import java.util.HashMap;
import java.util.Map;

/**
 * 0013. Roman to Integer
 * https://leetcode.com/problems/roman-to-integer/
 * Difficulty: Easy
 * Tags: Hash Table, Math, String
 */
class Solution {

    /**
     * Approach 1: Left-to-Right with Subtraction Rule
     * If current value < next value, subtract; otherwise add
     * Time:  O(n) — single pass through the string
     * Space: O(1) — fixed-size map with 7 entries
     */
    public int romanToInt(String s) {
        Map<Character, Integer> romanMap = Map.of(
            'I', 1, 'V', 5, 'X', 10, 'L', 50,
            'C', 100, 'D', 500, 'M', 1000
        );

        int result = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            int curr = romanMap.get(s.charAt(i));

            // If current value is less than next value → subtraction case
            // e.g., IV = -1 + 5 = 4, IX = -1 + 10 = 9
            if (i + 1 < n && curr < romanMap.get(s.charAt(i + 1))) {
                result -= curr;
            } else {
                result += curr;
            }
        }

        return result;
    }

    /**
     * Approach 2: Right-to-Left
     * Process from right to left; if current < previous, subtract
     * Time:  O(n) — single pass through the string (reversed)
     * Space: O(1) — fixed-size map with 7 entries
     */
    public int romanToIntRightToLeft(String s) {
        Map<Character, Integer> romanMap = Map.of(
            'I', 1, 'V', 5, 'X', 10, 'L', 50,
            'C', 100, 'D', 500, 'M', 1000
        );

        int result = 0;
        int prev = 0;

        // Traverse from right to left
        for (int i = s.length() - 1; i >= 0; i--) {
            int curr = romanMap.get(s.charAt(i));

            // If current value is less than the previous (right neighbor),
            // it means subtraction: e.g., in "IV", I < V → subtract 1
            if (curr < prev) {
                result -= curr;
            } else {
                result += curr;
            }

            prev = curr;
        }

        return result;
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

        System.out.println("=== Approach 1: Left-to-Right with Subtraction Rule ===");

        // Test 1: Basic — single character
        test("Single character I", sol.romanToInt("I"), 1);

        // Test 2: Simple addition
        test("Simple addition III", sol.romanToInt("III"), 3);

        // Test 3: Subtraction case IV
        test("Subtraction IV", sol.romanToInt("IV"), 4);

        // Test 4: Subtraction case IX
        test("Subtraction IX", sol.romanToInt("IX"), 9);

        // Test 5: Mixed — LVIII
        test("Mixed LVIII", sol.romanToInt("LVIII"), 58);

        // Test 6: Complex — MCMXCIV
        test("Complex MCMXCIV", sol.romanToInt("MCMXCIV"), 1994);

        // Test 7: Large — MMMCMXCIX (3999)
        test("Max MMMCMXCIX", sol.romanToInt("MMMCMXCIX"), 3999);

        // Test 8: All subtraction pairs — CDXLIV
        test("Subtraction pairs CDXLIV", sol.romanToInt("CDXLIV"), 444);

        // Test 9: Single large — M
        test("Single M", sol.romanToInt("M"), 1000);

        System.out.println();
        System.out.println("=== Approach 2: Right-to-Left ===");

        // Test 1: Basic — single character
        test("Single character I", sol.romanToIntRightToLeft("I"), 1);

        // Test 2: Simple addition
        test("Simple addition III", sol.romanToIntRightToLeft("III"), 3);

        // Test 3: Subtraction case IV
        test("Subtraction IV", sol.romanToIntRightToLeft("IV"), 4);

        // Test 4: Subtraction case IX
        test("Subtraction IX", sol.romanToIntRightToLeft("IX"), 9);

        // Test 5: Mixed — LVIII
        test("Mixed LVIII", sol.romanToIntRightToLeft("LVIII"), 58);

        // Test 6: Complex — MCMXCIV
        test("Complex MCMXCIV", sol.romanToIntRightToLeft("MCMXCIV"), 1994);

        // Test 7: Large — MMMCMXCIX (3999)
        test("Max MMMCMXCIX", sol.romanToIntRightToLeft("MMMCMXCIX"), 3999);

        // Test 8: All subtraction pairs — CDXLIV
        test("Subtraction pairs CDXLIV", sol.romanToIntRightToLeft("CDXLIV"), 444);

        // Test 9: Single large — M
        test("Single M", sol.romanToIntRightToLeft("M"), 1000);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
