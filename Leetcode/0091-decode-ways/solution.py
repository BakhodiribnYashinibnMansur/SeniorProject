class Solution:
    def numDecodings(self, s: str) -> int:
        """Time O(n), Space O(1)."""
        n = len(s)
        if n == 0 or s[0] == '0':
            return 0
        prev2, prev1 = 1, 1
        for i in range(2, n + 1):
            cur = 0
            if s[i - 1] != '0':
                cur += prev1
            two = int(s[i - 2:i])
            if 10 <= two <= 26:
                cur += prev2
            prev2, prev1 = prev1, cur
        return prev1


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, exp):
        global passed, failed
        if got == exp: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got} want={exp}"); failed += 1
    cases = [
        ("12", 2), ("226", 3), ("06", 0), ("0", 0), ("10", 1), ("20", 1),
        ("27", 1), ("100", 0), ("1", 1), ("11106", 2), ("111111", 13),
        ("2611055971756562", 4), ("301", 0),
    ]
    for s, exp in cases: test(s, sol.numDecodings(s), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
