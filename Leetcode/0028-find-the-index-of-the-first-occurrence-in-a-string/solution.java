/**
 * 0028. Find the Index of the First Occurrence in a String
 * https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/
 * Difficulty: Easy
 * Tags: Two Pointers, String, String Matching
 */
class Solution {

    /**
     * Approach 1: Brute Force (Sliding Window)
     * Try every starting position, compare character by character
     * Time:  O(n * m) — n = haystack.length, m = needle.length
     * Space: O(1) — only uses index variables
     */
    public int strStr(String haystack, String needle) {
        int n = haystack.length(), m = needle.length();

        // Try each valid starting position
        for (int i = 0; i <= n - m; i++) {
            boolean match = true;
            for (int j = 0; j < m; j++) {
                if (haystack.charAt(i + j) != needle.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }

        return -1;
    }

    /**
     * Approach 2: KMP Algorithm
     * Preprocess needle to build LPS array, search without backtracking
     * Time:  O(n + m) — linear in total input size
     * Space: O(m) — LPS array for the needle
     */
    public int strStrKMP(String haystack, String needle) {
        int n = haystack.length(), m = needle.length();
        if (m > n) return -1;

        // Step 1: Build LPS (Longest Proper Prefix Suffix) array
        int[] lps = new int[m];
        int length = 0;
        int i = 1;
        while (i < m) {
            if (needle.charAt(i) == needle.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        // Step 2: Search using LPS array
        i = 0;
        int j = 0;
        while (i < n) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;
            }
            if (j == m) {
                return i - j;
            } else if (i < n && haystack.charAt(i) != needle.charAt(j)) {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return -1;
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

        System.out.println("=== Approach 1: Brute Force (Sliding Window) ===");

        // Test 1: LeetCode Example 1 — needle at the start
        test("Example 1: sadbutsad/sad",
            sol.strStr("sadbutsad", "sad"), 0);

        // Test 2: LeetCode Example 2 — needle not found
        test("Example 2: leetcode/leeto",
            sol.strStr("leetcode", "leeto"), -1);

        // Test 3: Needle at the end
        test("Needle at end: hello/llo",
            sol.strStr("hello", "llo"), 2);

        // Test 4: Needle equals haystack
        test("Needle equals haystack: abc/abc",
            sol.strStr("abc", "abc"), 0);

        // Test 5: Needle longer than haystack
        test("Needle longer: ab/abc",
            sol.strStr("ab", "abc"), -1);

        // Test 6: Single character match
        test("Single char match: a/a",
            sol.strStr("a", "a"), 0);

        // Test 7: Single character no match
        test("Single char no match: a/b",
            sol.strStr("a", "b"), -1);

        // Test 8: Repeated characters
        test("Repeated chars: aaaa/aa",
            sol.strStr("aaaa", "aa"), 0);

        // Test 9: Tricky partial match — mississippi
        test("Mississippi: mississippi/issip",
            sol.strStr("mississippi", "issip"), 4);

        // Test 10: Needle in the middle
        test("Middle match: abcdef/cde",
            sol.strStr("abcdef", "cde"), 2);

        System.out.println();
        System.out.println("=== Approach 2: KMP Algorithm ===");

        // Test 1: LeetCode Example 1 — needle at the start
        test("Example 1: sadbutsad/sad",
            sol.strStrKMP("sadbutsad", "sad"), 0);

        // Test 2: LeetCode Example 2 — needle not found
        test("Example 2: leetcode/leeto",
            sol.strStrKMP("leetcode", "leeto"), -1);

        // Test 3: Needle at the end
        test("Needle at end: hello/llo",
            sol.strStrKMP("hello", "llo"), 2);

        // Test 4: Needle equals haystack
        test("Needle equals haystack: abc/abc",
            sol.strStrKMP("abc", "abc"), 0);

        // Test 5: Needle longer than haystack
        test("Needle longer: ab/abc",
            sol.strStrKMP("ab", "abc"), -1);

        // Test 6: Single character match
        test("Single char match: a/a",
            sol.strStrKMP("a", "a"), 0);

        // Test 7: Single character no match
        test("Single char no match: a/b",
            sol.strStrKMP("a", "b"), -1);

        // Test 8: Repeated characters
        test("Repeated chars: aaaa/aa",
            sol.strStrKMP("aaaa", "aa"), 0);

        // Test 9: Tricky partial match — mississippi
        test("Mississippi: mississippi/issip",
            sol.strStrKMP("mississippi", "issip"), 4);

        // Test 10: KMP advantage case — repeated patterns
        test("KMP advantage: aaabaaab/aaab",
            sol.strStrKMP("aaabaaab", "aaab"), 0);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
