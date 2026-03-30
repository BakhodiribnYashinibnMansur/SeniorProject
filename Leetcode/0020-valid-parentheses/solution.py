# ============================================================
# 0020. Valid Parentheses
# https://leetcode.com/problems/valid-parentheses/
# Difficulty: Easy
# Tags: String, Stack
# ============================================================


class Solution:
    def isValid(self, s: str) -> bool:
        """
        Optimal Solution (Stack)
        Approach: Use a stack to track opening brackets
        Time:  O(n) -- single pass through the string
        Space: O(n) -- stack stores at most n/2 elements
        """
        # Stack to store opening brackets
        stack = []

        # Mapping: closing bracket -> opening bracket
        matching = {')': '(', ']': '[', '}': '{'}

        for ch in s:
            if ch in '([{':
                # Opening bracket: push onto stack
                stack.append(ch)
            else:
                # Closing bracket: check if stack is empty or top doesn't match
                if not stack or stack[-1] != matching[ch]:
                    return False
                # Pop the top element
                stack.pop()

        # Valid only if the stack is empty (all brackets matched)
        return len(stack) == 0


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

    # Test 1: Simple parentheses
    test("Simple parentheses", sol.isValid("()"), True)

    # Test 2: Multiple types
    test("Multiple types", sol.isValid("()[]{}"), True)

    # Test 3: Mismatched brackets
    test("Mismatched brackets", sol.isValid("(]"), False)

    # Test 4: Nested brackets
    test("Nested brackets", sol.isValid("{[()]}"), True)

    # Test 5: Incorrect nesting order
    test("Incorrect nesting", sol.isValid("([)]"), False)

    # Test 6: Empty string
    test("Empty string", sol.isValid(""), True)

    # Test 7: Single opening bracket
    test("Single opening bracket", sol.isValid("("), False)

    # Test 8: Single closing bracket
    test("Single closing bracket", sol.isValid("]"), False)

    # Test 9: Long nested valid string
    test("Long nested valid", sol.isValid("(({{[[]]}}))"), True)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
