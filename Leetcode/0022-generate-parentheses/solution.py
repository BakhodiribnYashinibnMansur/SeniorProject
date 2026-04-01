# ============================================================
# 0022. Generate Parentheses
# https://leetcode.com/problems/generate-parentheses/
# Difficulty: Medium
# Tags: String, Dynamic Programming, Backtracking
# ============================================================


class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        """
        Optimal Solution (Backtracking)
        Approach: Build valid strings character by character using constraints
        Time:  O(4^n / sqrt(n)) -- nth Catalan number of valid sequences
        Space: O(n) -- recursion depth is 2n
        """
        result = []

        def backtrack(current: list[str], open_count: int, close_count: int):
            # Base case: string is complete
            if len(current) == 2 * n:
                result.append("".join(current))
                return

            # Choice 1: add '(' if we haven't used all n
            if open_count < n:
                current.append("(")
                backtrack(current, open_count + 1, close_count)
                current.pop()

            # Choice 2: add ')' if it won't create an invalid prefix
            if close_count < open_count:
                current.append(")")
                backtrack(current, open_count, close_count + 1)
                current.pop()

        backtrack([], 0, 0)
        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        # Sort both lists for comparison since order doesn't matter
        got_sorted = sorted(got) if isinstance(got, list) else got
        exp_sorted = sorted(expected) if isinstance(expected, list) else expected
        if got_sorted == exp_sorted:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: n = 1
    test("n = 1", sol.generateParenthesis(1), ["()"])

    # Test 2: n = 2
    test("n = 2", sol.generateParenthesis(2), ["(())", "()()"])

    # Test 3: n = 3
    test("n = 3", sol.generateParenthesis(3),
         ["((()))", "(()())", "(())()", "()(())", "()()()"])

    # Test 4: n = 4 (should have 14 results -- Catalan(4))
    test("n = 4 count", len(sol.generateParenthesis(4)), 14)

    # Test 5: All results are valid parentheses
    def is_valid(s: str) -> bool:
        count = 0
        for ch in s:
            if ch == '(':
                count += 1
            else:
                count -= 1
            if count < 0:
                return False
        return count == 0

    results_n3 = sol.generateParenthesis(3)
    all_valid = all(is_valid(s) for s in results_n3)
    test("All n=3 results are valid", all_valid, True)

    # Test 6: No duplicates
    results_n4 = sol.generateParenthesis(4)
    test("No duplicates for n=4", len(results_n4), len(set(results_n4)))

    # Test 7: Correct length of each string
    all_correct_len = all(len(s) == 6 for s in results_n3)
    test("All n=3 strings have length 6", all_correct_len, True)

    # Test 8: n = 5 (should have 42 results -- Catalan(5))
    test("n = 5 count", len(sol.generateParenthesis(5)), 42)

    # Test 9: n = 8 (should have 1430 results -- Catalan(8))
    test("n = 8 count", len(sol.generateParenthesis(8)), 1430)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
