/**
 * 0014. Longest Common Prefix
 * https://leetcode.com/problems/longest-common-prefix/
 * Difficulty: Easy
 * Tags: String, Trie
 */
class Solution {

    /**
     * Approach 1: Vertical Scanning
     * Compare characters column by column across all strings
     * Time:  O(S) — where S is the sum of all characters in all strings
     * Space: O(1) — only uses a few variables
     */
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        // Use the first string as reference
        // Compare each character position across all strings
        for (int i = 0; i < strs[0].length(); i++) {
            char ch = strs[0].charAt(i);

            for (int j = 1; j < strs.length; j++) {
                // If we've reached the end of any string, or characters don't match
                if (i >= strs[j].length() || strs[j].charAt(i) != ch) {
                    return strs[0].substring(0, i);
                }
            }
        }

        // The entire first string is the common prefix
        return strs[0];
    }

    /**
     * Approach 2: Horizontal Scanning
     * Start with first string as prefix, reduce it pairwise
     * Time:  O(S) — where S is the sum of all characters in all strings
     * Space: O(1) — modifies prefix in place using substring
     */
    public String longestCommonPrefixHorizontal(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        // Start with the first string as the prefix
        String prefix = strs[0];

        // Compare prefix with each subsequent string
        for (int i = 1; i < strs.length; i++) {
            // Shrink prefix until it matches the beginning of strs[i]
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) return "";
            }
        }

        return prefix;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, String got, String expected) {
        if (got.equals(expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      \"%s\"%n  Expected: \"%s\"%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Approach 1: Vertical Scanning ===");

        // Test 1: LeetCode Example 1 — common prefix "fl"
        test("Example 1: flower/flow/flight",
            sol.longestCommonPrefix(new String[]{"flower", "flow", "flight"}), "fl");

        // Test 2: LeetCode Example 2 — no common prefix
        test("Example 2: dog/racecar/car",
            sol.longestCommonPrefix(new String[]{"dog", "racecar", "car"}), "");

        // Test 3: Single string
        test("Single string",
            sol.longestCommonPrefix(new String[]{"alone"}), "alone");

        // Test 4: All identical strings
        test("All identical",
            sol.longestCommonPrefix(new String[]{"abc", "abc", "abc"}), "abc");

        // Test 5: Empty string in array
        test("Empty string in array",
            sol.longestCommonPrefix(new String[]{"abc", "", "abc"}), "");

        // Test 6: Single character strings
        test("Single char strings",
            sol.longestCommonPrefix(new String[]{"a", "a", "a"}), "a");

        // Test 7: First char mismatch
        test("First char mismatch",
            sol.longestCommonPrefix(new String[]{"abc", "xyz", "def"}), "");

        // Test 8: Two strings with partial match
        test("Two strings partial",
            sol.longestCommonPrefix(new String[]{"interview", "internet"}), "inter");

        // Test 9: One character prefix
        test("One char prefix",
            sol.longestCommonPrefix(new String[]{"ab", "ac", "ad"}), "a");

        System.out.println();
        System.out.println("=== Approach 2: Horizontal Scanning ===");

        // Test 1: LeetCode Example 1 — common prefix "fl"
        test("Example 1: flower/flow/flight",
            sol.longestCommonPrefixHorizontal(new String[]{"flower", "flow", "flight"}), "fl");

        // Test 2: LeetCode Example 2 — no common prefix
        test("Example 2: dog/racecar/car",
            sol.longestCommonPrefixHorizontal(new String[]{"dog", "racecar", "car"}), "");

        // Test 3: Single string
        test("Single string",
            sol.longestCommonPrefixHorizontal(new String[]{"alone"}), "alone");

        // Test 4: All identical strings
        test("All identical",
            sol.longestCommonPrefixHorizontal(new String[]{"abc", "abc", "abc"}), "abc");

        // Test 5: Empty string in array
        test("Empty string in array",
            sol.longestCommonPrefixHorizontal(new String[]{"abc", "", "abc"}), "");

        // Test 6: Single character strings
        test("Single char strings",
            sol.longestCommonPrefixHorizontal(new String[]{"a", "a", "a"}), "a");

        // Test 7: First char mismatch
        test("First char mismatch",
            sol.longestCommonPrefixHorizontal(new String[]{"abc", "xyz", "def"}), "");

        // Test 8: Two strings with partial match
        test("Two strings partial",
            sol.longestCommonPrefixHorizontal(new String[]{"interview", "internet"}), "inter");

        // Test 9: One character prefix
        test("One char prefix",
            sol.longestCommonPrefixHorizontal(new String[]{"ab", "ac", "ad"}), "a");

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
