import java.util.HashMap;
import java.util.Objects;

/**
 * 0003. Longest Substring Without Repeating Characters
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 * Difficulty: Medium
 * Tags: Hash Table, String, Sliding Window
 */
class Solution {

    /**
     * Optimal Solution (Sliding Window with Last-Seen Index Map)
     * Approach: Maintain a window [left, right]; on duplicate, jump left past the last occurrence
     * Time:  O(n) — each character is visited at most twice (once by right, once when left jumps)
     * Space: O(min(n, a)) — map holds at most a unique characters, where a = alphabet size
     */
    public int lengthOfLongestSubstring(String s) {
        // Map: character → its most recently seen index
        HashMap<Character, Integer> lastSeen = new HashMap<>();

        int maxLen = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);

            // If the character was seen before AND it is inside the current window,
            // move left pointer past the duplicate to shrink the window
            if (lastSeen.containsKey(ch) && lastSeen.get(ch) >= left) {
                left = lastSeen.get(ch) + 1;
            }

            // Update the most recent index of this character
            lastSeen.put(ch, right);

            // Update the maximum window length
            int windowLen = right - left + 1;
            if (windowLen > maxLen) {
                maxLen = windowLen;
            }
        }

        return maxLen;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int got, int expected) {
        if (Objects.equals(got, expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n", name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Classic example — "abc" is the longest
        test("\"abcabcbb\" → 3", sol.lengthOfLongestSubstring("abcabcbb"), 3);

        // Test 2: All same characters
        test("\"bbbbb\" → 1", sol.lengthOfLongestSubstring("bbbbb"), 1);

        // Test 3: Longest at the end — "wke"
        test("\"pwwkew\" → 3", sol.lengthOfLongestSubstring("pwwkew"), 3);

        // Test 4: Empty string
        test("\"\" → 0", sol.lengthOfLongestSubstring(""), 0);

        // Test 5: Single character
        test("\"a\" → 1", sol.lengthOfLongestSubstring("a"), 1);

        // Test 6: All unique characters
        test("\"abcdef\" → 6", sol.lengthOfLongestSubstring("abcdef"), 6);

        // Test 7: Digits and symbols
        test("\"1234567890\" → 10", sol.lengthOfLongestSubstring("1234567890"), 10);

        // Test 8: Duplicate at start and end
        test("\"dvdf\" → 3", sol.lengthOfLongestSubstring("dvdf"), 3);

        // Test 9: Space characters
        test("\"a b\" → 3", sol.lengthOfLongestSubstring("a b"), 3);

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
