# ============================================================
# 0050. Pow(x, n)
# https://leetcode.com/problems/powx-n/
# Difficulty: Medium
# Tags: Math, Recursion
# ============================================================


class Solution:
    def myPow(self, x: float, n: int) -> float:
        """
        Optimal Solution (Iterative Binary Exponentiation)
        Approach: Process bits of n from LSB to MSB, squaring x each step
        Time:  O(log n) — process each bit of n once
        Space: O(1) — only a few variables
        """
        if n < 0:
            x = 1 / x
            n = -n

        result = 1.0
        while n > 0:
            # If current bit is 1, multiply result by current x
            if n % 2 == 1:
                result *= x
            # Square x for the next bit position
            x *= x
            # Shift to the next bit
            n >>= 1

        return result

    def myPowRecursive(self, x: float, n: int) -> float:
        """
        Recursive Binary Exponentiation (Fast Power)
        Approach: x^n = (x^(n/2))^2 if even, x * (x^(n/2))^2 if odd
        Time:  O(log n) — halve n each step
        Space: O(log n) — recursion stack depth
        """
        if n < 0:
            x = 1 / x
            n = -n
        return self._fastPow(x, n)

    def _fastPow(self, x: float, n: int) -> float:
        if n == 0:
            return 1.0
        half = self._fastPow(x, n // 2)
        if n % 2 == 0:
            return half * half
        return half * half * x


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected, tolerance=1e-5):
        global passed, failed
        if abs(got - expected) < tolerance:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Iterative Binary Exponentiation (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1: 2.0^10", sol.myPow(2.0, 10), 1024.0)

    # Test 2: LeetCode Example 2
    test("Example 2: 2.1^3", sol.myPow(2.1, 3), 9.261)

    # Test 3: LeetCode Example 3 — negative exponent
    test("Example 3: 2.0^(-2)", sol.myPow(2.0, -2), 0.25)

    # Test 4: Exponent is 0
    test("n=0: 5.0^0", sol.myPow(5.0, 0), 1.0)

    # Test 5: Exponent is 1
    test("n=1: 3.0^1", sol.myPow(3.0, 1), 3.0)

    # Test 6: x = 1.0, large n
    test("x=1: 1.0^2147483647", sol.myPow(1.0, 2147483647), 1.0)

    # Test 7: x = -1.0, even n
    test("x=-1, even n: (-1)^2", sol.myPow(-1.0, 2), 1.0)

    # Test 8: x = -1.0, odd n
    test("x=-1, odd n: (-1)^3", sol.myPow(-1.0, 3), -1.0)

    # Test 9: n = -2^31 (INT_MIN)
    test("INT_MIN: 1.0^(-2^31)", sol.myPow(1.0, -2147483648), 1.0)

    # Test 10: Small base, negative exponent
    test("0.5^(-2)", sol.myPow(0.5, -2), 4.0)

    # Test 11: Negative base, positive exponent
    test("(-2)^3", sol.myPow(-2.0, 3), -8.0)

    # Test 12: Negative base, even exponent
    test("(-2)^4", sol.myPow(-2.0, 4), 16.0)

    print("\n=== Recursive Binary Exponentiation ===")

    # Test 13: Recursive — Example 1
    test("Recursive: 2.0^10", sol.myPowRecursive(2.0, 10), 1024.0)

    # Test 14: Recursive — negative exponent
    test("Recursive: 2.0^(-2)", sol.myPowRecursive(2.0, -2), 0.25)

    # Test 15: Recursive — zero exponent
    test("Recursive: 5.0^0", sol.myPowRecursive(5.0, 0), 1.0)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
