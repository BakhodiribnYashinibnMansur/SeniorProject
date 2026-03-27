# ============================================================
# 0007. Reverse Integer
# https://leetcode.com/problems/reverse-integer/
# Difficulty: Medium
# Tags: Math
# ============================================================

INT_MAX =  2147483647   # 2^31 - 1
INT_MIN = -2147483648   # -2^31


class Solution:
    def reverse(self, x: int) -> int:
        """
        Optimal Solution (Mathematical Digit Pop)
        Approach: Pop digits one by one using modulo; check overflow before each push.
        Time:  O(log x) — number of digits in x (at most 10 for a 32-bit integer)
        Space: O(1)    — only a few integer variables
        """
        rev = 0

        # Python's % always returns a non-negative result for positive divisor,
        # so we work with the absolute value and restore the sign at the end.
        sign = -1 if x < 0 else 1
        x = abs(x)

        while x != 0:
            # Pop the last digit
            digit = x % 10
            x //= 10

            # Overflow check BEFORE pushing digit onto rev
            # We work with unsigned magnitude here and apply sign after
            if rev > INT_MAX // 10 or (rev == INT_MAX // 10 and digit > 7):
                return 0

            # Push digit onto the reversed number
            rev = rev * 10 + digit

        return sign * rev


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
    test("Positive 123", sol.reverse(123), 321)

    # Test 2: Negative number
    test("Negative -123", sol.reverse(-123), -321)

    # Test 3: Trailing zero is dropped
    test("Trailing zero 120", sol.reverse(120), 21)

    # Test 4: Single digit
    test("Single digit 5", sol.reverse(5), 5)

    # Test 5: Zero
    test("Zero", sol.reverse(0), 0)

    # Test 6: Overflow — reversed value exceeds INT_MAX
    # 1534236469 reversed = 9646324351 > 2^31 - 1
    test("Overflow positive", sol.reverse(1534236469), 0)

    # Test 7: Overflow — reversed value below INT_MIN
    # -1534236469 reversed = -9646324351 < -2^31
    test("Overflow negative", sol.reverse(-1534236469), 0)

    # Test 8: INT_MAX reversed → overflows (7463847412 > INT_MAX)
    test("INT_MAX reversed overflows", sol.reverse(2147483647), 0)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
