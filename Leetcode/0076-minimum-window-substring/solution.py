from collections import Counter

# ============================================================
# 0076. Minimum Window Substring
# https://leetcode.com/problems/minimum-window-substring/
# Difficulty: Hard
# Tags: Hash Table, String, Sliding Window
# ============================================================


class Solution:
    def minWindow(self, s: str, t: str) -> str:
        """
        Optimal Solution (Sliding Window with Counts).
        Time:  O(m + n)
        Space: O(σ)
        """
        if not s or not t:
            return ""
        need = Counter(t)
        required = len(need)
        window = Counter()
        have = 0
        l = 0
        best_len = float('inf')
        best_l = 0
        for r, c in enumerate(s):
            window[c] += 1
            if c in need and window[c] == need[c]:
                have += 1
            while have == required:
                if r - l + 1 < best_len:
                    best_len = r - l + 1
                    best_l = l
                c2 = s[l]
                window[c2] -= 1
                if c2 in need and window[c2] < need[c2]:
                    have -= 1
                l += 1
        return s[best_l:best_l + best_len] if best_len != float('inf') else ""


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got!r}"); failed += 1

    cases = [
        ("Example 1", "ADOBECODEBANC", "ABC", "BANC"),
        ("Example 2", "a", "a", "a"),
        ("Example 3", "a", "aa", ""),
        ("Same string", "abc", "abc", "abc"),
        ("Reordered", "cba", "abc", "cba"),
        ("Duplicates in t", "aabbcc", "abc", "abbc"),
        ("Single char in t", "abcabc", "a", "a"),
        ("No window", "abc", "d", ""),
        ("Long t", "abcdef", "abcdefg", ""),
        ("All same char", "aaaa", "aa", "aa"),
        ("Mixed case", "AbCdEf", "AcF", ""),
        ("Larger", "this is a test string", "tist", "t stri"),
    ]
    for n, s, t, exp in cases: test(n, sol.minWindow(s, t), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
