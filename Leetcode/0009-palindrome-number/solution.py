# ============================================================
# 0009. Palindrome Number
# https://leetcode.com/problems/palindrome-number/
# Difficulty: Easy
# Tags: Math
# ============================================================


class Solution:
    def isPalindromeString(self, x: int) -> bool:
        """
        Approach 1 — String Conversion
        Time:  O(log x) — number of digits in x
        Space: O(log x) — string representation of x
        """
        # Negative numbers are never palindromes
        if x < 0:
            return False
        s = str(x)
        return s == s[::-1]

    def isPalindrome(self, x: int) -> bool:
        """
        Optimal Solution — Reverse Second Half (No String)
        Time:  O(log x) — we process half the digits
        Space: O(1)    — only integer variables, no extra memory
        """
        # Negative numbers are never palindromes.
        # Numbers ending in 0 (but not 0 itself) cannot be palindromes
        # because no number starts with 0.
        if x < 0 or (x % 10 == 0 and x != 0):
            return False

        # Reverse only the second half of x.
        # When reversed_half >= x, we've processed at least half the digits.
        reversed_half = 0
        while x > reversed_half:
            reversed_half = reversed_half * 10 + x % 10
            x //= 10

        # Even number of digits: x == reversed_half   (e.g., 1221 → x=12, rH=12)
        # Odd number of digits:  x == reversed_half//10 (e.g., 12321 → x=12, rH=123)
        # Integer division by 10 discards the middle digit.
        return x == reversed_half or x == reversed_half // 10


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

    # Test 1: Basic palindrome
    test("121 is palindrome", sol.isPalindrome(121), True)

    # Test 2: Negative number — never a palindrome
    test("-121 is not palindrome", sol.isPalindrome(-121), False)

    # Test 3: Ends in 0 (but not 0 itself) — cannot be palindrome
    test("10 is not palindrome", sol.isPalindrome(10), False)

    # Test 4: Single digit — always a palindrome
    test("7 is palindrome", sol.isPalindrome(7), True)

    # Test 5: Zero — palindrome
    test("0 is palindrome", sol.isPalindrome(0), True)

    # Test 6: Even-length palindrome
    test("1221 is palindrome", sol.isPalindrome(1221), True)

    # Test 7: Odd-length palindrome
    test("12321 is palindrome", sol.isPalindrome(12321), True)

    # Test 8: Non-palindrome
    test("123 is not palindrome", sol.isPalindrome(123), False)

    # Test 9: Large palindrome
    test("1000000001 is palindrome", sol.isPalindrome(1000000001), True)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
