# ============================================================
# 0032. Longest Valid Parentheses
# https://leetcode.com/problems/longest-valid-parentheses/
# Difficulty: Hard
# Tags: String, Dynamic Programming, Stack
# ============================================================


class Solution:
    def longestValidParentheses(self, s: str) -> int:
        """
        Optimal Solution (Stack with indices)
        Approach: Use a stack storing indices to track unmatched positions
        Time:  O(n) -- single pass through the string
        Space: O(n) -- stack stores at most n+1 elements
        """
        # Initialize stack with -1 as base for length calculation
        stack = [-1]
        max_len = 0

        for i, ch in enumerate(s):
            if ch == '(':
                # Opening bracket: push index onto stack
                stack.append(i)
            else:
                # Closing bracket: pop the top element
                stack.pop()
                if not stack:
                    # Stack empty: push current index as new base
                    stack.append(i)
                else:
                    # Valid match: compute length from current base
                    max_len = max(max_len, i - stack[-1])

        return max_len


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Example 1 -- valid pair in the middle
    test("Example 1: '(()'", sol.longestValidParentheses("(()"), 2)

    # Test 2: Example 2 -- valid substring surrounded by unmatched
    test("Example 2: ')()())'", sol.longestValidParentheses(")()())"), 4)

    # Test 3: Example 3 -- empty string
    test("Example 3: ''", sol.longestValidParentheses(""), 0)

    # Test 4: Simple valid pair
    test("Simple pair: '()'", sol.longestValidParentheses("()"), 2)

    # Test 5: Entire string valid (nested)
    test("Nested: '(())'", sol.longestValidParentheses("(())"), 4)

    # Test 6: Adjacent valid pairs merge
    test("Adjacent: '()()'", sol.longestValidParentheses("()()"), 4)

    # Test 7: All opening brackets
    test("All opening: '((('", sol.longestValidParentheses("((("), 0)

    # Test 8: All closing brackets
    test("All closing: ')))'", sol.longestValidParentheses(")))"), 0)

    # Test 9: Complex nesting and adjacency
    test("Complex: '()(())'", sol.longestValidParentheses("()(())"), 6)

    # Test 10: Single character
    test("Single char: '('", sol.longestValidParentheses("("), 0)

    # Test 11: Long alternating valid
    test("Long valid: '()()()()'", sol.longestValidParentheses("()()()()"), 8)

    # Test 12: Valid in middle with unmatched ends
    test("Middle valid: '(()()'", sol.longestValidParentheses("(()()"), 4)

    # Test 13: Deep nesting
    test("Deep nesting: '(((())))'", sol.longestValidParentheses("(((())))"), 8)

    # Test 14: Multiple disjoint valid substrings
    test("Disjoint: '(())(('", sol.longestValidParentheses("(())(("), 4)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
