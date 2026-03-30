# ============================================================
# 0017. Letter Combinations of a Phone Number
# https://leetcode.com/problems/letter-combinations-of-a-phone-number/
# Difficulty: Medium
# Tags: Hash Table, String, Backtracking
# ============================================================

PHONE = {
    "2": "abc", "3": "def", "4": "ghi", "5": "jkl",
    "6": "mno", "7": "pqrs", "8": "tuv", "9": "wxyz",
}


class Solution:
    def letterCombinations(self, digits: str) -> list[str]:
        """
        Optimal Solution (Backtracking / DFS)
        Approach: Build combinations character by character using recursive backtracking
        Time:  O(4^n * n) — at most 4 choices per digit, n digits
        Space: O(n)       — recursion depth equals number of digits
        """
        if not digits:
            return []

        result = []

        def backtrack(index: int, current: list[str]) -> None:
            # Base case: built a full-length combination
            if index == len(digits):
                result.append("".join(current))
                return

            # Get letters mapped to the current digit
            letters = PHONE[digits[index]]

            # Try each letter for this digit position
            for ch in letters:
                current.append(ch)
                backtrack(index + 1, current)
                current.pop()  # undo choice (backtrack)

        backtrack(0, [])
        return result

    def letterCombinationsIterative(self, digits: str) -> list[str]:
        """
        Iterative Solution (BFS-like)
        Approach: Build combinations level by level, expanding each existing combination
        Time:  O(4^n * n) — same as backtracking
        Space: O(4^n * n) — stores all intermediate combinations
        """
        if not digits:
            return []

        # Start with an empty combination
        result = [""]

        # For each digit, expand every existing combination with each mapped letter
        for digit in digits:
            letters = PHONE[digit]
            result = [combo + ch for combo in result for ch in letters]

        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got: list[str], expected: list[str]) -> None:
        global passed, failed
        if sorted(got) == sorted(expected):
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Backtracking (Optimal) ===")

    # Test 1: LeetCode Example 1
    test('Example 1: "23"',
         sol.letterCombinations("23"),
         ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"])

    # Test 2: Empty string
    test("Example 2: empty string",
         sol.letterCombinations(""),
         [])

    # Test 3: Single digit
    test('Example 3: "2"',
         sol.letterCombinations("2"),
         ["a", "b", "c"])

    # Test 4: Digit with 4 letters (7 = pqrs)
    test("Single digit 7 (4 letters)",
         sol.letterCombinations("7"),
         ["p", "q", "r", "s"])

    # Test 5: Two digits with 4 letters each
    test('"79" (4x4 = 16 combos)',
         sol.letterCombinations("79"),
         ["pw", "px", "py", "pz", "qw", "qx", "qy", "qz",
          "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz"])

    # Test 6: Three digits
    test('"234" (27 combos)',
         sol.letterCombinations("234"),
         ["adg", "adh", "adi", "aeg", "aeh", "aei", "afg", "afh", "afi",
          "bdg", "bdh", "bdi", "beg", "beh", "bei", "bfg", "bfh", "bfi",
          "cdg", "cdh", "cdi", "ceg", "ceh", "cei", "cfg", "cfh", "cfi"])

    # Test 7: Digit 9
    test("Single digit 9",
         sol.letterCombinations("9"),
         ["w", "x", "y", "z"])

    # Test 8: Same digit repeated
    test('"22" (same digit twice)',
         sol.letterCombinations("22"),
         ["aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"])

    # Test 9: Four digits (count check)
    result_2345 = sol.letterCombinations("2345")
    test('"2345" (81 combos)',
         result_2345,
         [a + b + c + d
          for a in "abc" for b in "def" for c in "ghi" for d in "jkl"])

    print("\n=== Iterative (BFS-like) ===")

    test('Iter: Example 1 "23"',
         sol.letterCombinationsIterative("23"),
         ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"])

    test("Iter: Empty string",
         sol.letterCombinationsIterative(""),
         [])

    test('Iter: Single digit "2"',
         sol.letterCombinationsIterative("2"),
         ["a", "b", "c"])

    test('Iter: "79" (4x4)',
         sol.letterCombinationsIterative("79"),
         ["pw", "px", "py", "pz", "qw", "qx", "qy", "qz",
          "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz"])

    test('Iter: "22" (same digit)',
         sol.letterCombinationsIterative("22"),
         ["aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
