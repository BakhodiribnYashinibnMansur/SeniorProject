import java.util.*;

/**
 * 0017. Letter Combinations of a Phone Number
 * https://leetcode.com/problems/letter-combinations-of-a-phone-number/
 * Difficulty: Medium
 * Tags: Hash Table, String, Backtracking
 */
class Solution {

    // Phone keypad mapping
    private static final Map<Character, String> PHONE = Map.of(
        '2', "abc", '3', "def", '4', "ghi", '5', "jkl",
        '6', "mno", '7', "pqrs", '8', "tuv", '9', "wxyz"
    );

    /**
     * Optimal Solution (Backtracking / DFS)
     * Approach: Build combinations character by character using recursive backtracking
     * Time:  O(4^n * n) — at most 4 choices per digit, n digits
     * Space: O(n)       — recursion depth equals number of digits
     */
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty()) return result;

        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(String digits, int index, StringBuilder current, List<String> result) {
        // Base case: built a full-length combination
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }

        // Get letters mapped to the current digit
        String letters = PHONE.get(digits.charAt(index));

        // Try each letter for this digit position
        for (char ch : letters.toCharArray()) {
            current.append(ch);
            backtrack(digits, index + 1, current, result);
            current.deleteCharAt(current.length() - 1); // undo choice (backtrack)
        }
    }

    /**
     * Iterative Solution (BFS-like)
     * Approach: Build combinations level by level, expanding each existing combination
     * Time:  O(4^n * n) — same as backtracking
     * Space: O(4^n * n) — stores all intermediate combinations
     */
    public List<String> letterCombinationsIterative(String digits) {
        if (digits == null || digits.isEmpty()) return new ArrayList<>();

        // Start with an empty combination
        List<String> result = new ArrayList<>();
        result.add("");

        // For each digit, expand every existing combination with each mapped letter
        for (char digit : digits.toCharArray()) {
            String letters = PHONE.get(digit);
            List<String> newResult = new ArrayList<>();

            for (String combo : result) {
                for (char ch : letters.toCharArray()) {
                    newResult.add(combo + ch);
                }
            }

            result = newResult;
        }

        return result;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, List<String> got, List<String> expected) {
        List<String> sortedGot = new ArrayList<>(got);
        Collections.sort(sortedGot);
        List<String> sortedExpected = new ArrayList<>(expected);
        Collections.sort(sortedExpected);

        if (sortedGot.equals(sortedExpected)) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, sortedGot, sortedExpected);
            failed++;
        }
    }

    static List<String> list(String... items) {
        return Arrays.asList(items);
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        System.out.println("=== Backtracking (Optimal) ===");

        // Test 1: LeetCode Example 1
        test("Example 1: \"23\"",
            sol.letterCombinations("23"),
            list("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"));

        // Test 2: Empty string
        test("Example 2: empty string",
            sol.letterCombinations(""),
            list());

        // Test 3: Single digit
        test("Example 3: \"2\"",
            sol.letterCombinations("2"),
            list("a", "b", "c"));

        // Test 4: Digit with 4 letters
        test("Single digit 7 (4 letters)",
            sol.letterCombinations("7"),
            list("p", "q", "r", "s"));

        // Test 5: Two digits with 4 letters each
        test("\"79\" (4x4 = 16 combos)",
            sol.letterCombinations("79"),
            list("pw", "px", "py", "pz", "qw", "qx", "qy", "qz",
                 "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz"));

        // Test 6: Three digits
        test("\"234\" (27 combos)",
            sol.letterCombinations("234"),
            list("adg", "adh", "adi", "aeg", "aeh", "aei", "afg", "afh", "afi",
                 "bdg", "bdh", "bdi", "beg", "beh", "bei", "bfg", "bfh", "bfi",
                 "cdg", "cdh", "cdi", "ceg", "ceh", "cei", "cfg", "cfh", "cfi"));

        // Test 7: Digit 9
        test("Single digit 9",
            sol.letterCombinations("9"),
            list("w", "x", "y", "z"));

        // Test 8: Same digit repeated
        test("\"22\" (same digit twice)",
            sol.letterCombinations("22"),
            list("aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"));

        // Test 9: null input
        test("Null input",
            sol.letterCombinations(null),
            list());

        System.out.println("\n=== Iterative (BFS-like) ===");

        test("Iter: Example 1 \"23\"",
            sol.letterCombinationsIterative("23"),
            list("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"));

        test("Iter: Empty string",
            sol.letterCombinationsIterative(""),
            list());

        test("Iter: Single digit \"2\"",
            sol.letterCombinationsIterative("2"),
            list("a", "b", "c"));

        test("Iter: \"79\" (4x4)",
            sol.letterCombinationsIterative("79"),
            list("pw", "px", "py", "pz", "qw", "qx", "qy", "qz",
                 "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz"));

        test("Iter: \"22\" (same digit)",
            sol.letterCombinationsIterative("22"),
            list("aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"));

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
