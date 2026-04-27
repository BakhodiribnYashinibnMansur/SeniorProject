# ============================================================
# 0058. Length of Last Word
# https://leetcode.com/problems/length-of-last-word/
# Difficulty: Easy
# Tags: String
# ============================================================


class Solution:
    def lengthOfLastWord(self, s: str) -> int:
        """
        Optimal Solution (Reverse Scan).
        Time:  O(length of last word + trailing spaces)
        Space: O(1)
        """
        i = len(s) - 1
        while i >= 0 and s[i] == ' ':
            i -= 1
        count = 0
        while i >= 0 and s[i] != ' ':
            count += 1
            i -= 1
        return count

    def lengthOfLastWordSplit(self, s: str) -> int:
        """Split approach. Time O(n), Space O(n)."""
        parts = s.split()
        return len(parts[-1]) if parts else 0

    def lengthOfLastWordTrim(self, s: str) -> int:
        """Trim + last index. Time O(n), Space O(n)."""
        t = s.rstrip()
        return len(t) - t.rfind(' ') - 1


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}")
            passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    cases = [
        ("Example 1", "Hello World", 5),
        ("Example 2", "   fly me   to   the moon  ", 4),
        ("Example 3", "luffy is still joyboy", 6),
        ("Single word", "hello", 5),
        ("Single char", "a", 1),
        ("Trailing spaces", "hi   ", 2),
        ("Leading spaces", "   hi", 2),
        ("Multiple internal spaces", "a    b", 1),
        ("Pad both sides", "   abc   ", 3),
        ("Long suffix", "a aaaaaa", 6),
        ("Same words", "day day", 3),
    ]

    print("=== Reverse Scan ===")
    for name, s, exp in cases:
        test(name, sol.lengthOfLastWord(s), exp)

    print("\n=== Split ===")
    for name, s, exp in cases:
        test("Split " + name, sol.lengthOfLastWordSplit(s), exp)

    print("\n=== Trim + LastSpace ===")
    for name, s, exp in cases:
        test("Trim " + name, sol.lengthOfLastWordTrim(s), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
