# ============================================================
# 0028. Find the Index of the First Occurrence in a String
# https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/
# Difficulty: Easy
# Tags: Two Pointers, String, String Matching
# ============================================================


class Solution:
    def strStr(self, haystack: str, needle: str) -> int:
        """
        Approach 1: Brute Force (Sliding Window)
        Try every starting position, compare character by character
        Time:  O(n * m) — n = len(haystack), m = len(needle)
        Space: O(1) — only uses index variables
        """
        n, m = len(haystack), len(needle)

        # Try each valid starting position
        for i in range(n - m + 1):
            # Compare the window haystack[i:i+m] with needle
            if haystack[i:i + m] == needle:
                return i

        return -1

    def strStrKMP(self, haystack: str, needle: str) -> int:
        """
        Approach 2: KMP Algorithm
        Preprocess needle to build LPS array, search without backtracking
        Time:  O(n + m) — linear in total input size
        Space: O(m) — LPS array for the needle
        """
        n, m = len(haystack), len(needle)
        if m > n:
            return -1

        # Step 1: Build LPS (Longest Proper Prefix Suffix) array
        lps = [0] * m
        length = 0
        i = 1
        while i < m:
            if needle[i] == needle[length]:
                length += 1
                lps[i] = length
                i += 1
            elif length > 0:
                length = lps[length - 1]
            else:
                lps[i] = 0
                i += 1

        # Step 2: Search using LPS array
        i = j = 0
        while i < n:
            if haystack[i] == needle[j]:
                i += 1
                j += 1
            if j == m:
                return i - j
            elif i < n and haystack[i] != needle[j]:
                if j > 0:
                    j = lps[j - 1]
                else:
                    i += 1

        return -1


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
            print(f"  Got:      {got!r}")
            print(f"  Expected: {expected!r}")
            failed += 1

    print("=== Approach 1: Brute Force (Sliding Window) ===")

    # Test 1: LeetCode Example 1 — needle at the start
    test("Example 1: sadbutsad/sad",
         sol.strStr("sadbutsad", "sad"), 0)

    # Test 2: LeetCode Example 2 — needle not found
    test("Example 2: leetcode/leeto",
         sol.strStr("leetcode", "leeto"), -1)

    # Test 3: Needle at the end
    test("Needle at end: hello/llo",
         sol.strStr("hello", "llo"), 2)

    # Test 4: Needle equals haystack
    test("Needle equals haystack: abc/abc",
         sol.strStr("abc", "abc"), 0)

    # Test 5: Needle longer than haystack
    test("Needle longer: ab/abc",
         sol.strStr("ab", "abc"), -1)

    # Test 6: Single character match
    test("Single char match: a/a",
         sol.strStr("a", "a"), 0)

    # Test 7: Single character no match
    test("Single char no match: a/b",
         sol.strStr("a", "b"), -1)

    # Test 8: Repeated characters
    test("Repeated chars: aaaa/aa",
         sol.strStr("aaaa", "aa"), 0)

    # Test 9: Tricky partial match — mississippi
    test("Mississippi: mississippi/issip",
         sol.strStr("mississippi", "issip"), 4)

    # Test 10: Needle in the middle
    test("Middle match: abcdef/cde",
         sol.strStr("abcdef", "cde"), 2)

    print()
    print("=== Approach 2: KMP Algorithm ===")

    # Test 1: LeetCode Example 1 — needle at the start
    test("Example 1: sadbutsad/sad",
         sol.strStrKMP("sadbutsad", "sad"), 0)

    # Test 2: LeetCode Example 2 — needle not found
    test("Example 2: leetcode/leeto",
         sol.strStrKMP("leetcode", "leeto"), -1)

    # Test 3: Needle at the end
    test("Needle at end: hello/llo",
         sol.strStrKMP("hello", "llo"), 2)

    # Test 4: Needle equals haystack
    test("Needle equals haystack: abc/abc",
         sol.strStrKMP("abc", "abc"), 0)

    # Test 5: Needle longer than haystack
    test("Needle longer: ab/abc",
         sol.strStrKMP("ab", "abc"), -1)

    # Test 6: Single character match
    test("Single char match: a/a",
         sol.strStrKMP("a", "a"), 0)

    # Test 7: Single character no match
    test("Single char no match: a/b",
         sol.strStrKMP("a", "b"), -1)

    # Test 8: Repeated characters
    test("Repeated chars: aaaa/aa",
         sol.strStrKMP("aaaa", "aa"), 0)

    # Test 9: Tricky partial match — mississippi
    test("Mississippi: mississippi/issip",
         sol.strStrKMP("mississippi", "issip"), 4)

    # Test 10: KMP advantage case — repeated patterns
    test("KMP advantage: aaabaaab/aaab",
         sol.strStrKMP("aaabaaab", "aaab"), 0)

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
