import java.util.*;

/**
 * 0030. Substring with Concatenation of All Words
 * https://leetcode.com/problems/substring-with-concatenation-of-all-words/
 * Difficulty: Hard
 * Tags: Hash Table, String, Sliding Window
 */
class Solution {

    /**
     * Optimal Solution (Sliding Window with Hash Map)
     * Approach: For each of wordLen offsets, slide a window of numWords words
     * Time:  O(n * wordLen) — wordLen offsets, each processes n/wordLen words
     * Space: O(m)           — frequency maps with at most m distinct words
     */
    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.isEmpty() || words == null || words.length == 0) return result;

        int wordLen = words[0].length();
        int numWords = words.length;
        int totalLen = wordLen * numWords;

        if (s.length() < totalLen) return result;

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String w : words) wordFreq.merge(w, 1, Integer::sum);

        // Try each starting offset from 0 to wordLen-1
        for (int i = 0; i < wordLen; i++) {
            int left = i, count = 0;
            Map<String, Integer> seen = new HashMap<>();

            // Slide right pointer one word at a time
            for (int right = i; right + wordLen <= s.length(); right += wordLen) {
                String word = s.substring(right, right + wordLen);

                if (wordFreq.containsKey(word)) {
                    seen.merge(word, 1, Integer::sum);
                    count++;

                    // Shrink window if word count exceeds target
                    while (seen.get(word) > wordFreq.get(word)) {
                        String leftWord = s.substring(left, left + wordLen);
                        seen.merge(leftWord, -1, Integer::sum);
                        count--;
                        left += wordLen;
                    }

                    // Check if we have a valid concatenation
                    if (count == numWords) {
                        result.add(left);
                    }
                } else {
                    // Invalid word — reset the window
                    seen.clear();
                    count = 0;
                    left = right + wordLen;
                }
            }
        }

        return result;
    }

    /**
     * Brute Force approach
     * Approach: Check every starting position, build frequency map each time
     * Time:  O(n * m * wordLen) — n positions, m words per position
     * Space: O(m)               — frequency map
     */
    public List<Integer> findSubstringBrute(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.isEmpty() || words == null || words.length == 0) return result;

        int wordLen = words[0].length();
        int numWords = words.length;
        int totalLen = wordLen * numWords;

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String w : words) wordFreq.merge(w, 1, Integer::sum);

        for (int i = 0; i <= s.length() - totalLen; i++) {
            Map<String, Integer> seen = new HashMap<>();
            boolean valid = true;
            for (int j = 0; j < numWords; j++) {
                String word = s.substring(i + j * wordLen, i + (j + 1) * wordLen);
                if (!wordFreq.containsKey(word)) {
                    valid = false;
                    break;
                }
                seen.merge(word, 1, Integer::sum);
                if (seen.get(word) > wordFreq.get(word)) {
                    valid = false;
                    break;
                }
            }
            if (valid) result.add(i);
        }

        return result;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, List<Integer> got, List<Integer> expected) {
        List<Integer> sortedGot = new ArrayList<>(got);
        List<Integer> sortedExp = new ArrayList<>(expected);
        Collections.sort(sortedGot);
        Collections.sort(sortedExp);

        if (sortedGot.equals(sortedExp)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    static List<Integer> list(int... vals) {
        List<Integer> result = new ArrayList<>();
        for (int v : vals) result.add(v);
        return result;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Sliding Window with Hash Map (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1",
            sol.findSubstring("barfoothefoobarman", new String[]{"foo", "bar"}),
            list(0, 9));

        // Test 2: LeetCode Example 2 — no match
        test("Example 2",
            sol.findSubstring("wordgoodgoodgoodbestword", new String[]{"word", "good", "best", "word"}),
            list());

        // Test 3: LeetCode Example 3 — multiple matches
        test("Example 3",
            sol.findSubstring("barfoofoobarthefoobarman", new String[]{"bar", "foo", "the"}),
            list(6, 9, 12));

        // Test 4: Single character words
        test("Single char words",
            sol.findSubstring("aaa", new String[]{"a", "a"}),
            list(0, 1));

        // Test 5: All same words
        test("All same words",
            sol.findSubstring("aaa", new String[]{"a", "a", "a"}),
            list(0));

        // Test 6: No match — string too short
        test("String too short",
            sol.findSubstring("ab", new String[]{"abc"}),
            list());

        // Test 7: Exact match
        test("Exact match",
            sol.findSubstring("foobar", new String[]{"foo", "bar"}),
            list(0));

        // Test 8: Duplicate words with match
        test("Duplicate words match",
            sol.findSubstring("wordgoodgoodgoodbestword", new String[]{"word", "good", "best", "good"}),
            list(8));

        // Test 9: Single word
        test("Single word",
            sol.findSubstring("foobarfoo", new String[]{"foo"}),
            list(0, 6));

        System.out.println("\n=== Brute Force ===");

        test("BF: Example 1",
            sol.findSubstringBrute("barfoothefoobarman", new String[]{"foo", "bar"}),
            list(0, 9));

        test("BF: Example 2",
            sol.findSubstringBrute("wordgoodgoodgoodbestword", new String[]{"word", "good", "best", "word"}),
            list());

        test("BF: Example 3",
            sol.findSubstringBrute("barfoofoobarthefoobarman", new String[]{"bar", "foo", "the"}),
            list(6, 9, 12));

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
