import java.util.*;

/**
 * 0022. Generate Parentheses
 * https://leetcode.com/problems/generate-parentheses/
 * Difficulty: Medium
 * Tags: String, Dynamic Programming, Backtracking
 */
class Solution {

    /**
     * Optimal Solution (Backtracking)
     * Approach: Build valid strings character by character using constraints
     * Time:  O(4^n / sqrt(n)) -- nth Catalan number of valid sequences
     * Space: O(n) -- recursion depth is 2n
     */
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder current,
                           int open, int close, int n) {
        // Base case: string is complete
        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }

        // Choice 1: add '(' if we haven't used all n
        if (open < n) {
            current.append('(');
            backtrack(result, current, open + 1, close, n);
            current.deleteCharAt(current.length() - 1);
        }

        // Choice 2: add ')' if it won't create an invalid prefix
        if (close < open) {
            current.append(')');
            backtrack(result, current, open, close + 1, n);
            current.deleteCharAt(current.length() - 1);
        }
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, Object got, Object expected) {
        boolean match;
        if (got instanceof List && expected instanceof List) {
            List<String> gotList = new ArrayList<>((List<String>) got);
            List<String> expList = new ArrayList<>((List<String>) expected);
            Collections.sort(gotList);
            Collections.sort(expList);
            match = gotList.equals(expList);
        } else {
            match = got.equals(expected);
        }

        if (match) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    static boolean isValid(String s) {
        int count = 0;
        for (char ch : s.toCharArray()) {
            if (ch == '(') count++;
            else count--;
            if (count < 0) return false;
        }
        return count == 0;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: n = 1
        test("n = 1", sol.generateParenthesis(1), List.of("()"));

        // Test 2: n = 2
        test("n = 2", sol.generateParenthesis(2), List.of("(())", "()()"));

        // Test 3: n = 3
        test("n = 3", sol.generateParenthesis(3),
            List.of("((()))", "(()())", "(())()", "()(())", "()()()"));

        // Test 4: n = 4 (should have 14 results)
        test("n = 4 count", sol.generateParenthesis(4).size(), 14);

        // Test 5: All results are valid parentheses
        List<String> resultsN3 = sol.generateParenthesis(3);
        boolean allValid = resultsN3.stream().allMatch(Solution::isValid);
        test("All n=3 results are valid", allValid, true);

        // Test 6: No duplicates
        List<String> resultsN4 = sol.generateParenthesis(4);
        test("No duplicates for n=4", new HashSet<>(resultsN4).size(), resultsN4.size());

        // Test 7: Correct length of each string
        boolean allCorrectLen = resultsN3.stream().allMatch(s -> s.length() == 6);
        test("All n=3 strings have length 6", allCorrectLen, true);

        // Test 8: n = 5 (should have 42 results)
        test("n = 5 count", sol.generateParenthesis(5).size(), 42);

        // Test 9: n = 8 (should have 1430 results)
        test("n = 8 count", sol.generateParenthesis(8).size(), 1430);

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
