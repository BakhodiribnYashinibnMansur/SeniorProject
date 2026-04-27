/**
 * 0076. Minimum Window Substring
 * https://leetcode.com/problems/minimum-window-substring/
 * Difficulty: Hard
 * Tags: Hash Table, String, Sliding Window
 */
class Solution {

    /**
     * Optimal Solution (Sliding Window with Counts).
     * Time:  O(m + n)
     * Space: O(σ)
     */
    public String minWindow(String s, String t) {
        if (s.isEmpty() || t.isEmpty()) return "";
        int[] need = new int[128];
        int distinct = 0;
        for (char c : t.toCharArray()) {
            if (need[c] == 0) distinct++;
            need[c]++;
        }
        int[] window = new int[128];
        int have = 0, l = 0, bestLen = -1, bestL = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            window[c]++;
            if (need[c] > 0 && window[c] == need[c]) have++;
            while (have == distinct) {
                if (bestLen == -1 || r - l + 1 < bestLen) {
                    bestLen = r - l + 1;
                    bestL = l;
                }
                char c2 = s.charAt(l);
                window[c2]--;
                if (need[c2] > 0 && window[c2] < need[c2]) have--;
                l++;
            }
        }
        return bestLen == -1 ? "" : s.substring(bestL, bestL + bestLen);
    }

    static int passed = 0, failed = 0;
    static void test(String name, String got, String expected) {
        if (got.equals(expected)) { System.out.println("PASS: " + name); passed++; }
        else { System.out.println("FAIL: " + name + " got=" + got); failed++; }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        String[][] cases = {
            {"Example 1", "ADOBECODEBANC", "ABC", "BANC"},
            {"Example 2", "a", "a", "a"},
            {"Example 3", "a", "aa", ""},
            {"Same string", "abc", "abc", "abc"},
            {"Reordered", "cba", "abc", "cba"},
            {"Duplicates in t", "aabbcc", "abc", "abbc"},
            {"Single char in t", "abcabc", "a", "a"},
            {"No window", "abc", "d", ""},
            {"Long t", "abcdef", "abcdefg", ""},
            {"All same char", "aaaa", "aa", "aa"},
            {"Mixed case", "AbCdEf", "AcF", ""},
            {"Larger", "this is a test string", "tist", "t stri"}
        };
        for (String[] c : cases) test(c[0], sol.minWindow(c[1], c[2]), c[3]);
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
