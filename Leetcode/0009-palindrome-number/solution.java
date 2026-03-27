/**
 * 0009. Palindrome Number
 * https://leetcode.com/problems/palindrome-number/
 * Difficulty: Easy
 * Tags: Math
 */
class Solution {

    /**
     * Approach 1: String Conversion
     * Time:  O(log x) — number of digits in x
     * Space: O(log x) — string representation of x
     */
    public boolean isPalindromeString(int x) {
        // Negative numbers are never palindromes
        if (x < 0) return false;

        // Convert to string and compare with its reverse
        String s = Integer.toString(x);
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }

    /**
     * Optimal Solution (Reverse Second Half, No String)
     * Time:  O(log x) — we process half the digits
     * Space: O(1)    — only integer variables, no extra memory
     */
    public boolean isPalindrome(int x) {
        // Negative numbers are never palindromes.
        // Numbers ending in 0 (but not 0 itself) cannot be palindromes
        // because no number starts with 0.
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }

        // Reverse only the second half of x.
        // When reversedHalf >= x, we've processed at least half the digits.
        int reversedHalf = 0;
        while (x > reversedHalf) {
            reversedHalf = reversedHalf * 10 + x % 10;
            x /= 10;
        }

        // Even number of digits: x == reversedHalf   (e.g., 1221 → x=12, rH=12)
        // Odd number of digits:  x == reversedHalf/10 (e.g., 12321 → x=12, rH=123)
        // Dividing by 10 discards the middle digit.
        return x == reversedHalf || x == reversedHalf / 10;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, boolean got, boolean expected) {
        if (got == expected) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %b%n  Expected: %b%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Basic palindrome
        test("121 is palindrome", sol.isPalindrome(121), true);

        // Test 2: Negative number — never a palindrome
        test("-121 is not palindrome", sol.isPalindrome(-121), false);

        // Test 3: Ends in 0 (but not 0 itself) — cannot be palindrome
        test("10 is not palindrome", sol.isPalindrome(10), false);

        // Test 4: Single digit — always a palindrome
        test("7 is palindrome", sol.isPalindrome(7), true);

        // Test 5: Zero — palindrome
        test("0 is palindrome", sol.isPalindrome(0), true);

        // Test 6: Even-length palindrome
        test("1221 is palindrome", sol.isPalindrome(1221), true);

        // Test 7: Odd-length palindrome
        test("12321 is palindrome", sol.isPalindrome(12321), true);

        // Test 8: Non-palindrome
        test("123 is not palindrome", sol.isPalindrome(123), false);

        // Test 9: Large palindrome
        test("1000000001 is palindrome", sol.isPalindrome(1000000001), true);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
