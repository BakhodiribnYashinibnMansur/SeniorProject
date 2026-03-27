# ============================================================
# 0005. Longest Palindromic Substring
# https://leetcode.com/problems/longest-palindromic-substring/
# Difficulty: Medium
# Tags: String, Dynamic Programming, Two Pointers
# ============================================================


class Solution:
    def longestPalindrome(self, s: str) -> str:
        """
        Optimal Solution (Expand Around Center)
        Approach: For each index i, expand outward for both odd-length palindromes
                  (center at i) and even-length palindromes (center between i and i+1).
                  Track start index and maximum length seen so far.
        Time:  O(n^2) — up to 2n-1 centers, each can expand up to n/2 steps
        Space: O(1)   — only a few variables; result is a slice of the input string
        """
        if not s:
            return ""

        start, max_len = 0, 1

        def expand(l: int, r: int) -> None:
            """Expand from center (l, r) and update start/max_len if longer."""
            nonlocal start, max_len
            while l >= 0 and r < len(s) and s[l] == s[r]:
                # Update best palindrome if current expansion is longer
                if r - l + 1 > max_len:
                    start = l
                    max_len = r - l + 1
                l -= 1
                r += 1

        for i in range(len(s)):
            # Odd-length palindrome: single center at i
            expand(i, i)
            # Even-length palindrome: center between i and i+1
            expand(i, i + 1)

        return s[start:start + max_len]


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        """Standard single-answer test."""
        global passed, failed
        if got == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got!r}")
            print(f"  Expected: {expected!r}")
            failed += 1

    def test_any(name: str, got, valid: list):
        """Multi-answer test — passes if got matches any element of valid."""
        global passed, failed
        if got in valid:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:           {got!r}")
            print(f"  Expected one of: {valid}")
            failed += 1

    # Test 1: LeetCode Example 1 — "bab" or "aba" both valid
    test_any("Example 1 babad", sol.longestPalindrome("babad"), ["bab", "aba"])

    # Test 2: LeetCode Example 2 — only "bb" is valid
    test("Example 2 cbbd", sol.longestPalindrome("cbbd"), "bb")

    # Test 3: Single character
    test("Single char", sol.longestPalindrome("a"), "a")

    # Test 4: All same characters — entire string
    test("All same chars", sol.longestPalindrome("aaaa"), "aaaa")

    # Test 5: No palindrome longer than 1 — any single character is valid
    test_any("All distinct", sol.longestPalindrome("abcd"), ["a", "b", "c", "d"])

    # Test 6: Entire string is a palindrome
    test("Whole string palindrome", sol.longestPalindrome("racecar"), "racecar")

    # Test 7: Even-length palindrome covers the whole string
    test("Even palindrome", sol.longestPalindrome("abccba"), "abccba")

    # Test 8: Palindrome at the end
    test("Palindrome at end", sol.longestPalindrome("xyzabba"), "abba")

    # Test 9: Palindrome at the beginning
    test("Palindrome at start", sol.longestPalindrome("madam xyz"), "madam")

    # Test 10: Two-character string, not a palindrome
    test_any("Two chars not palindrome", sol.longestPalindrome("ab"), ["a", "b"])

    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
