# ============================================================
# 0008. String to Integer (atoi)
# https://leetcode.com/problems/string-to-integer-atoi/
# Difficulty: Medium
# Tags: String, Simulation
# ============================================================

import math


class Solution:
    def myAtoi(self, s: str) -> int:
        """
        Optimal Solution (Single-pass Simulation)
        Time:  O(n) — single pass through the string characters
        Space: O(1) — only a few integer variables used
        """
        i = 0
        n = len(s)

        # Step 1: Skip leading whitespace
        while i < n and s[i] == ' ':
            i += 1

        # Step 2: Determine sign
        sign = 1
        if i < n and s[i] in ('+', '-'):
            if s[i] == '-':
                sign = -1
            i += 1

        # Step 3: Read digits and build result
        result = 0
        INT_MAX = 2**31 - 1  # 2147483647
        INT_MIN = -(2**31)   # -2147483648

        while i < n and s[i].isdigit():
            digit = int(s[i])

            # Step 4: Check for overflow BEFORE updating result
            # If result > INT_MAX // 10, the next multiply will overflow
            # If result == INT_MAX // 10 and digit > 7, it will overflow
            if result > INT_MAX // 10 or (result == INT_MAX // 10 and digit > 7):
                return INT_MAX if sign == 1 else INT_MIN

            result = result * 10 + digit
            i += 1

        return sign * result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Basic positive number
    test("Basic positive", sol.myAtoi("42"), 42)

    # Test 2: Leading whitespace and negative sign
    test("Leading spaces, negative", sol.myAtoi("   -42"), -42)

    # Test 3: Digits followed by letters — stop at non-digit
    test("Digits then words", sol.myAtoi("4193 with words"), 4193)

    # Test 4: Leading letters — no digits found, return 0
    test("Words then digits", sol.myAtoi("words and 987"), 0)

    # Test 5: Overflow negative — clamp to INT_MIN
    test("Overflow negative clamp", sol.myAtoi("-91283472332"), -2147483648)

    # Test 6: Overflow positive — clamp to INT_MAX
    test("Overflow positive clamp", sol.myAtoi("9999999999"), 2147483647)

    # Test 7: Explicit plus sign
    test("Explicit plus sign", sol.myAtoi("+1"), 1)

    # Test 8: Empty string
    test("Empty string", sol.myAtoi(""), 0)

    # Test 9: Only whitespace
    test("Only whitespace", sol.myAtoi("   "), 0)

    # Test 10: Zero
    test("Zero", sol.myAtoi("0"), 0)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
