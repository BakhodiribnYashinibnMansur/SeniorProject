import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * 0020. Valid Parentheses
 * https://leetcode.com/problems/valid-parentheses/
 * Difficulty: Easy
 * Tags: String, Stack
 */
class Solution {

    /**
     * Optimal Solution (Stack)
     * Approach: Use a stack to track opening brackets
     * Time:  O(n) -- single pass through the string
     * Space: O(n) -- stack stores at most n/2 elements
     */
    public boolean isValid(String s) {
        // Stack to store opening brackets
        Deque<Character> stack = new ArrayDeque<>();

        // Mapping: closing bracket -> opening bracket
        Map<Character, Character> matching = Map.of(
            ')', '(',
            ']', '[',
            '}', '{'
        );

        for (char ch : s.toCharArray()) {
            if (ch == '(' || ch == '[' || ch == '{') {
                // Opening bracket: push onto stack
                stack.push(ch);
            } else {
                // Closing bracket: check if stack is empty or top doesn't match
                if (stack.isEmpty() || !stack.peek().equals(matching.get(ch))) {
                    return false;
                }
                // Pop the top element
                stack.pop();
            }
        }

        // Valid only if the stack is empty (all brackets matched)
        return stack.isEmpty();
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, boolean got, boolean expected) {
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

        // Test 1: Simple parentheses
        test("Simple parentheses", sol.isValid("()"), true);

        // Test 2: Multiple types
        test("Multiple types", sol.isValid("()[]{}"), true);

        // Test 3: Mismatched brackets
        test("Mismatched brackets", sol.isValid("(]"), false);

        // Test 4: Nested brackets
        test("Nested brackets", sol.isValid("{[()]}"), true);

        // Test 5: Incorrect nesting order
        test("Incorrect nesting", sol.isValid("([)]"), false);

        // Test 6: Empty string
        test("Empty string", sol.isValid(""), true);

        // Test 7: Single opening bracket
        test("Single opening bracket", sol.isValid("("), false);

        // Test 8: Single closing bracket
        test("Single closing bracket", sol.isValid("]"), false);

        // Test 9: Long nested valid string
        test("Long nested valid", sol.isValid("(({{[[]]}}))"), true);

        // Results
        System.out.printf("%n\uD83D\uDCCA Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
