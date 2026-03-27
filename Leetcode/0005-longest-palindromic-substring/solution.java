import java.util.Arrays;
import java.util.List;

/**
 * 0005. Longest Palindromic Substring
 * https://leetcode.com/problems/longest-palindromic-substring/
 * Difficulty: Medium
 * Tags: String, Dynamic Programming, Two Pointers
 */
class Solution {

    /**
     * Optimal Solution (Expand Around Center)
     * Approach: For each index i, expand outward for both odd-length palindromes
     *           (center at i) and even-length palindromes (center between i and i+1).
     *           Track start index and maximum length.
     * Time:  O(n^2) — up to 2n-1 centers, each expands up to n/2 steps
     * Space: O(1)   — only a few integer variables; result is a substring of input
     */
    public String longestPalindrome(String s) {
        if (s == null || s.length() == 0) return "";

        // Track the start index and length of the best palindrome found
        int start = 0, maxLen = 1;

        for (int i = 0; i < s.length(); i++) {
            // Odd-length palindrome centered at i
            int oddLen  = expand(s, i, i);
            // Even-length palindrome centered between i and i+1
            int evenLen = expand(s, i, i + 1);

            int best = Math.max(oddLen, evenLen);
            if (best > maxLen) {
                maxLen = best;
                // Recover starting index from center and length
                start = i - (best - 1) / 2;
            }
        }

        return s.substring(start, start + maxLen);
    }

    /**
     * Expand from center (l, r) and return the length of the longest palindrome.
     */
    private int expand(String s, int l, int r) {
        // Expand outward as long as characters match
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
            l--;
            r++;
        }
        // Length of the valid palindrome region: r-l-1 (after one extra step)
        return r - l - 1;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    // For palindrome problems, multiple answers can be valid.
    static void testAny(String name, String got, List<String> valid) {
        if (valid.contains(got)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      \"%s\"%n  Expected one of: %s%n",
                name, got, valid);
            failed++;
        }
    }

    static void test(String name, String got, String expected) {
        testAny(name, got, Arrays.asList(expected));
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: LeetCode Example 1 — "bab" or "aba" both valid
        testAny("Example 1 babad",
            sol.longestPalindrome("babad"),
            Arrays.asList("bab", "aba"));

        // Test 2: LeetCode Example 2 — only "bb" is valid
        test("Example 2 cbbd", sol.longestPalindrome("cbbd"), "bb");

        // Test 3: Single character
        test("Single char", sol.longestPalindrome("a"), "a");

        // Test 4: All same characters — entire string
        test("All same chars", sol.longestPalindrome("aaaa"), "aaaa");

        // Test 5: No palindrome longer than 1 — any single character valid
        testAny("All distinct",
            sol.longestPalindrome("abcd"),
            Arrays.asList("a", "b", "c", "d"));

        // Test 6: Entire string is a palindrome
        test("Whole string palindrome", sol.longestPalindrome("racecar"), "racecar");

        // Test 7: Even-length palindrome
        test("Even palindrome", sol.longestPalindrome("abccba"), "abccba");

        // Test 8: Palindrome at the end
        test("Palindrome at end", sol.longestPalindrome("xyzabba"), "abba");

        // Test 9: Palindrome at the beginning
        test("Palindrome at start", sol.longestPalindrome("madam xyz"), "madam");

        // Test 10: Two-character string, not a palindrome
        testAny("Two chars not palindrome",
            sol.longestPalindrome("ab"),
            Arrays.asList("a", "b"));

        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
