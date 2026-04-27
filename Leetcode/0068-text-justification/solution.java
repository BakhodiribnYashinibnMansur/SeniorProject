import java.util.*;

/**
 * 0068. Text Justification
 * https://leetcode.com/problems/text-justification/
 * Difficulty: Hard
 * Tags: Array, String, Simulation
 */
class Solution {

    /**
     * Optimal Solution (Greedy Line Packing).
     * Time:  O(n * maxWidth)
     * Space: O(n * maxWidth)
     */
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int n = words.length, i = 0;
        while (i < n) {
            int j = i, lineLen = 0;
            while (j < n && lineLen + words[j].length() + (j - i) <= maxWidth) {
                lineLen += words[j].length();
                j++;
            }
            StringBuilder sb = new StringBuilder();
            boolean isLast = (j == n);
            if (isLast || j - i == 1) {
                for (int k = i; k < j; k++) {
                    sb.append(words[k]);
                    if (k < j - 1) sb.append(' ');
                }
                while (sb.length() < maxWidth) sb.append(' ');
            } else {
                int gaps = j - i - 1;
                int slots = maxWidth - lineLen;
                int base = slots / gaps;
                int extra = slots % gaps;
                for (int k = i; k < j - 1; k++) {
                    sb.append(words[k]);
                    int sp = base + ((k - i) < extra ? 1 : 0);
                    for (int s = 0; s < sp; s++) sb.append(' ');
                }
                sb.append(words[j - 1]);
            }
            result.add(sb.toString());
            i = j;
        }
        return result;
    }

    static int passed = 0, failed = 0;
    static void test(String name, List<String> got, List<String> expected) {
        if (got.equals(expected)) { System.out.println("PASS: " + name); passed++; }
        else {
            System.out.println("FAIL: " + name);
            System.out.println("  Got:      " + got);
            System.out.println("  Expected: " + expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Object[][] cases = {
            {"Example 1",
                new String[]{"This", "is", "an", "example", "of", "text", "justification."}, 16,
                Arrays.asList(
                    "This    is    an",
                    "example  of text",
                    "justification.  ")},
            {"Example 2",
                new String[]{"What", "must", "be", "acknowledgment", "shall", "be"}, 16,
                Arrays.asList(
                    "What   must   be",
                    "acknowledgment  ",
                    "shall be        ")},
            {"Example 3",
                new String[]{"Science", "is", "what", "we", "understand", "well", "enough", "to",
                             "explain", "to", "a", "computer.", "Art", "is", "everything", "else",
                             "we", "do"}, 20,
                Arrays.asList(
                    "Science  is  what we",
                    "understand      well",
                    "enough to explain to",
                    "a  computer.  Art is",
                    "everything  else  we",
                    "do                  ")},
            {"Single word", new String[]{"Hello"}, 10, Arrays.asList("Hello     ")},
            {"Single word equals width", new String[]{"abc"}, 3, Arrays.asList("abc")},
            {"Two words last line", new String[]{"a", "b"}, 5, Arrays.asList("a b  ")},
        };
        for (Object[] c : cases) {
            test((String) c[0], sol.fullJustify((String[]) c[1], (int) c[2]), (List<String>) c[3]);
        }
        System.out.printf("%nResults: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
    }
}
