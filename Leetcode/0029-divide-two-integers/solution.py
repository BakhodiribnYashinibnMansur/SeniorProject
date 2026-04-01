# ============================================================
# 0029. Divide Two Integers
# https://leetcode.com/problems/divide-two-integers/
# Difficulty: Medium
# Tags: Math, Bit Manipulation
# ============================================================


class Solution:
    def divide(self, dividend: int, divisor: int) -> int:
        """
        Optimal Solution (Exponential Search / Bit Shifting)
        Approach: Double the divisor using left shifts to subtract large chunks
        Time:  O(log^2 n) — outer loop O(log n), inner doubling O(log n)
        Space: O(1) — only a few variables
        """
        INT_MAX = 2**31 - 1
        INT_MIN = -(2**31)

        # Edge case: overflow when -2^31 / -1 = 2^31 > INT_MAX
        if dividend == INT_MIN and divisor == -1:
            return INT_MAX

        # Determine the sign of the result
        negative = (dividend < 0) != (divisor < 0)

        # Work with absolute values
        a = abs(dividend)
        b = abs(divisor)
        quotient = 0

        # Exponential search: double the divisor each time
        while a >= b:
            temp = b
            multiple = 1
            # Double temp until it would exceed a
            while a >= temp << 1:
                temp <<= 1
                multiple <<= 1
            # Subtract the largest chunk and add to quotient
            a -= temp
            quotient += multiple

        # Apply sign
        return -quotient if negative else quotient

    def divideRepeatedSubtraction(self, dividend: int, divisor: int) -> int:
        """
        Brute Force (Repeated Subtraction) — TLE but educational
        Approach: Subtract divisor from dividend one at a time
        Time:  O(dividend / divisor) — up to 2^31 subtractions
        Space: O(1)
        """
        INT_MAX = 2**31 - 1
        INT_MIN = -(2**31)

        # Edge case: overflow
        if dividend == INT_MIN and divisor == -1:
            return INT_MAX

        # Determine sign
        negative = (dividend < 0) != (divisor < 0)

        a = abs(dividend)
        b = abs(divisor)
        quotient = 0

        while a >= b:
            a -= b
            quotient += 1

        return -quotient if negative else quotient


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

    print("=== Exponential Search (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1: 10 / 3", sol.divide(10, 3), 3)

    # Test 2: LeetCode Example 2
    test("Example 2: 7 / -2", sol.divide(7, -2), -3)

    # Test 3: Overflow edge case
    test("Overflow: -2^31 / -1", sol.divide(-2147483648, -1), 2147483647)

    # Test 4: Dividend = 0
    test("Zero dividend: 0 / 5", sol.divide(0, 5), 0)

    # Test 5: Divisor = 1
    test("Divisor 1: 100 / 1", sol.divide(100, 1), 100)

    # Test 6: Divisor = -1
    test("Divisor -1: 100 / -1", sol.divide(100, -1), -100)

    # Test 7: Both negative
    test("Both negative: -7 / -2", sol.divide(-7, -2), 3)

    # Test 8: Dividend < divisor
    test("Small dividend: 1 / 3", sol.divide(1, 3), 0)

    # Test 9: Equal values
    test("Equal values: 7 / 7", sol.divide(7, 7), 1)

    # Test 10: Large dividend, divisor = 1
    test("MIN_INT / 1", sol.divide(-2147483648, 1), -2147483648)

    # Test 11: Large dividend, divisor = 2
    test("MIN_INT / 2", sol.divide(-2147483648, 2), -1073741824)

    # Test 12: Negative dividend, positive divisor
    test("-10 / 3", sol.divide(-10, 3), -3)

    print("\n=== Repeated Subtraction (Brute Force) ===")

    # Test 13: Brute Force -- Example 1
    test("BF: 10 / 3", sol.divideRepeatedSubtraction(10, 3), 3)

    # Test 14: Brute Force -- Example 2
    test("BF: 7 / -2", sol.divideRepeatedSubtraction(7, -2), -3)

    # Test 15: Brute Force -- Overflow
    test("BF: -2^31 / -1", sol.divideRepeatedSubtraction(-2147483648, -1), 2147483647)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
