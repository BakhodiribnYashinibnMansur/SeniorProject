from functools import lru_cache


class Solution:
    def isScramble(self, s1: str, s2: str) -> bool:
        """Time O(n^4), Space O(n^3)."""
        @lru_cache(maxsize=None)
        def solve(a: str, b: str) -> bool:
            if a == b: return True
            if sorted(a) != sorted(b): return False
            n = len(a)
            for i in range(1, n):
                if (solve(a[:i], b[:i]) and solve(a[i:], b[i:])) or \
                   (solve(a[:i], b[n - i:]) and solve(a[i:], b[:n - i])):
                    return True
            return False
        return solve(s1, s2)


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1
    cases = [
        ("Example 1", "great", "rgeat", True),
        ("Example 2", "abcde", "caebd", False),
        ("Single", "a", "a", True),
        ("ab ba", "ab", "ba", True),
        ("ab ab", "ab", "ab", True),
        ("ab cd", "ab", "cd", False),
        ("abc abc", "abc", "abc", True),
        ("abc bca", "abc", "bca", True),
        ("abc cab", "abc", "cab", True),
        ("abcd dcba", "abcd", "dcba", True),
        ("abcd cdab", "abcd", "cdab", True),
    ]
    for n, s1, s2, exp in cases:
        s = Solution()
        test(n, s.isScramble(s1, s2), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
