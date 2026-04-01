import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 0032. Longest Valid Parentheses
 * https://leetcode.com/problems/longest-valid-parentheses/
 * Difficulty: Hard
 * Tags: String, Dynamic Programming, Stack
 */
class Solution {

    /**
     * Optimal Solution (Stack with indices)
     * Approach: Use a stack storing indices to track unmatched positions
     * Time:  O(n) -- single pass through the string
     * Space: O(n) -- stack stores at most n+1 elements
     */
    public int longestValidParentheses(String s) {
        // Initialize stack with -1 as base for length calculation
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                // Opening bracket: push index onto stack
                stack.push(i);
            } else {
                // Closing bracket: pop the top element
                stack.pop();
                if (stack.isEmpty()) {
                    // Stack empty: push current index as new base
                    stack.push(i);
                } else {
                    // Valid match: compute length from current base
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }

        return maxLen;
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, int got, int expected) {
        if (got == expected) {
            System.out.printf("\u2705 PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("\u274c FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Example 1 -- valid pair in the middle
        test("Example 1: '(()'", sol.longestValidParentheses("(()"), 2);

        // Test 2: Example 2 -- valid substring surrounded by unmatched
        test("Example 2: ')()())'", sol.longestValidParentheses(")()())"), 4);

        // Test 3: Example 3 -- empty string
        test("Example 3: ''", sol.longestValidParentheses(""), 0);

        // Test 4: Simple valid pair
        test("Simple pair: '()'", sol.longestValidParentheses("()"), 2);

        // Test 5: Entire string valid (nested)
        test("Nested: '(())'", sol.longestValidParentheses("(())"), 4);

        // Test 6: Adjacent valid pairs merge
        test("Adjacent: '()()'", sol.longestValidParentheses("()()"), 4);

        // Test 7: All opening brackets
        test("All opening: '((('", sol.longestValidParentheses("((("), 0);

        // Test 8: All closing brackets
        test("All closing: ')))'", sol.longestValidParentheses(")))"), 0);

        // Test 9: Complex nesting and adjacency
        test("Complex: '()(())'", sol.longestValidParentheses("()(())"), 6);

        // Test 10: Single character
        test("Single char: '('", sol.longestValidParentheses("("), 0);

        // Test 11: Long alternating valid
        test("Long valid: '()()()()'", sol.longestValidParentheses("()()()()"), 8);

        // Test 12: Valid in middle with unmatched ends
        test("Middle valid: '(()()'", sol.longestValidParentheses("(()()"), 4);

        // Test 13: Deep nesting
        test("Deep nesting: '(((())))'", sol.longestValidParentheses("(((())))"), 8);

        // Test 14: Multiple disjoint valid substrings
        test("Disjoint: '(())(('", sol.longestValidParentheses("(())(("), 4);

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
