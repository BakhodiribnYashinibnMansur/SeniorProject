# ============================================================
# 0012. Integer to Roman
# https://leetcode.com/problems/integer-to-roman/
# Difficulty: Medium
# Tags: Hash Table, Math, String
# ============================================================


class Solution:
    def intToRoman(self, num: int) -> str:
        """
        Approach 1: Greedy with Value Table
        Approach: Use a lookup table of values and symbols sorted descending.
                  Greedily subtract the largest possible value and append its symbol.
        Time:  O(1) — bounded by the finite set of roman numeral symbols
        Space: O(1) — the result string length is bounded
        """
        # Value-symbol pairs in descending order
        # Includes subtractive forms (e.g., 900=CM, 400=CD, etc.)
        value_symbols = [
            (1000, "M"), (900, "CM"), (500, "D"), (400, "CD"),
            (100, "C"),  (90, "XC"),  (50, "L"),  (40, "XL"),
            (10, "X"),   (9, "IX"),   (5, "V"),   (4, "IV"),
            (1, "I"),
        ]

        result = []
        for val, sym in value_symbols:
            # While the current value fits into num, append symbol
            while num >= val:
                result.append(sym)
                num -= val

        return "".join(result)

    def intToRomanDigitMap(self, num: int) -> str:
        """
        Approach 2: Hardcoded Digit Mapping
        Approach: Predefine roman representations for each digit at each place value.
                  Extract thousands, hundreds, tens, ones and look up each.
        Time:  O(1) — always exactly 4 lookups
        Space: O(1) — lookup tables are constant size
        """
        thousands = ["", "M", "MM", "MMM"]
        hundreds  = ["", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"]
        tens      = ["", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"]
        ones      = ["", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"]

        return thousands[num // 1000] + hundreds[(num % 1000) // 100] + tens[(num % 100) // 10] + ones[num % 10]


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"  PASS: {name}")
            passed += 1
        else:
            print(f"  FAIL: {name}")
            print(f"  Got:      {got!r}")
            print(f"  Expected: {expected!r}")
            failed += 1

    print("=== Approach 1: Greedy with Value Table ===")

    # Test 1: LeetCode example 1
    test("Example 1 (3749)", sol.intToRoman(3749), "MMMDCCXLIX")

    # Test 2: LeetCode example 2
    test("Example 2 (58)", sol.intToRoman(58), "LVIII")

    # Test 3: LeetCode example 3
    test("Example 3 (1994)", sol.intToRoman(1994), "MCMXCIV")

    # Test 4: Minimum value
    test("Minimum (1)", sol.intToRoman(1), "I")

    # Test 5: Maximum value
    test("Maximum (3999)", sol.intToRoman(3999), "MMMCMXCIX")

    # Test 6: All subtractive forms
    test("Subtractive (944)", sol.intToRoman(944), "CMXLIV")

    # Test 7: Round thousand
    test("Round thousand (2000)", sol.intToRoman(2000), "MM")

    # Test 8: Single symbols
    test("Single symbol (500)", sol.intToRoman(500), "D")

    # Test 9: Repeating symbol
    test("Repeating (3)", sol.intToRoman(3), "III")

    print("\n=== Approach 2: Hardcoded Digit Mapping ===")

    # Verify Approach 2 matches Approach 1 on all test cases
    test("Digit Map (3749)", sol.intToRomanDigitMap(3749), "MMMDCCXLIX")
    test("Digit Map (58)", sol.intToRomanDigitMap(58), "LVIII")
    test("Digit Map (1994)", sol.intToRomanDigitMap(1994), "MCMXCIV")
    test("Digit Map (1)", sol.intToRomanDigitMap(1), "I")
    test("Digit Map (3999)", sol.intToRomanDigitMap(3999), "MMMCMXCIX")
    test("Digit Map (944)", sol.intToRomanDigitMap(944), "CMXLIV")
    test("Digit Map (2000)", sol.intToRomanDigitMap(2000), "MM")
    test("Digit Map (500)", sol.intToRomanDigitMap(500), "D")
    test("Digit Map (3)", sol.intToRomanDigitMap(3), "III")

    # Results
    print(f"\n  Results: {passed} passed, {failed} failed, {passed + failed} total")
