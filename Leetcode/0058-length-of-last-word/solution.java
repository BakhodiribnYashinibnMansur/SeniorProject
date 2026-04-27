/**
 * 0058. Length of Last Word
 * https://leetcode.com/problems/length-of-last-word/
 * Difficulty: Easy
 * Tags: String
 */
class Solution {

    /**
     * Optimal Solution (Reverse Scan).
     * Time:  O(length of last word + trailing spaces)
     * Space: O(1)
     */
    public int lengthOfLastWord(String s) {
        int i = s.length() - 1;
        while (i >= 0 && s.charAt(i) == ' ') i--;
        int count = 0;
        while (i >= 0 && s.charAt(i) != ' ') {
            count++;
            i--;
        }
        return count;
    }

    /**
     * Split approach.
     * Time:  O(n)
     * Space: O(n)
     */
    public int lengthOfLastWordSplit(String s) {
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return 0;
        String[] parts = trimmed.split("\\s+");
        return parts[parts.length - 1].length();
    }

    /**
     * Trim + last index.
     * Time:  O(n)
     * Space: O(n)
     */
    public int lengthOfLastWordTrim(String s) {
        String t = s.replaceAll("\\s+$", "");
        return t.length() - t.lastIndexOf(' ') - 1;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int got, int expected) {
        if (got == expected) {
            System.out.println("PASS: " + name);
            passed++;
        } else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + got);
            System.out.println("  Expected: " + expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1", "Hello World", 5},
            {"Example 2", "   fly me   to   the moon  ", 4},
            {"Example 3", "luffy is still joyboy", 6},
            {"Single word", "hello", 5},
            {"Single char", "a", 1},
            {"Trailing spaces", "hi   ", 2},
            {"Leading spaces", "   hi", 2},
            {"Multiple internal spaces", "a    b", 1},
            {"Pad both sides", "   abc   ", 3},
            {"Long suffix", "a aaaaaa", 6},
            {"Same words", "day day", 3},
        };

        System.out.println("=== Reverse Scan ===");
        for (Object[] c : cases) test((String) c[0], sol.lengthOfLastWord((String) c[1]), (int) c[2]);

        System.out.println("\n=== Split ===");
        for (Object[] c : cases) test("Split " + c[0], sol.lengthOfLastWordSplit((String) c[1]), (int) c[2]);

        System.out.println("\n=== Trim + LastSpace ===");
        for (Object[] c : cases) test("Trim " + c[0], sol.lengthOfLastWordTrim((String) c[1]), (int) c[2]);

        System.out.printf("%nResults: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
