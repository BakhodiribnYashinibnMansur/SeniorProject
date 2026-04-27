import re

# ============================================================
# 0065. Valid Number
# https://leetcode.com/problems/valid-number/
# Difficulty: Hard
# Tags: String
# ============================================================


class Solution:
    def isNumber(self, s: str) -> bool:
        """
        Optimal Solution (Single Pass with Flags).
        Time:  O(n)
        Space: O(1)
        """
        sawDigit = sawDot = sawE = False
        digitAfterE = True
        for i, c in enumerate(s):
            if c.isdigit():
                sawDigit = True
                if sawE: digitAfterE = True
            elif c in '+-':
                if i != 0 and s[i-1] not in 'eE': return False
            elif c == '.':
                if sawDot or sawE: return False
                sawDot = True
            elif c in 'eE':
                if sawE or not sawDigit: return False
                sawE = True
                digitAfterE = False
            else:
                return False
        return sawDigit and digitAfterE

    _pattern = re.compile(r'^[+-]?(\d+\.\d*|\.\d+|\d+)([eE][+-]?\d+)?$')

    def isNumberRegex(self, s: str) -> bool:
        return bool(self._pattern.fullmatch(s))

    def isNumberDFA(self, s: str) -> bool:
        transitions = [
            {'digit': 2, 'sign': 1, 'dot': 4},
            {'digit': 2, 'dot': 4},
            {'digit': 2, 'dot': 3, 'exp': 6},
            {'digit': 5, 'exp': 6},
            {'digit': 5},
            {'digit': 5, 'exp': 6},
            {'digit': 8, 'sign': 7},
            {'digit': 8},
            {'digit': 8},
        ]
        accept = {2, 3, 5, 8}
        def klass(c):
            if c.isdigit(): return 'digit'
            if c in '+-': return 'sign'
            if c == '.': return 'dot'
            if c in 'eE': return 'exp'
            return None
        state = 0
        for c in s:
            k = klass(c)
            if k is None or k not in transitions[state]:
                return False
            state = transitions[state][k]
        return state in accept


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}"); passed += 1
        else:
            print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        ("0", True), ("e", False), (".", False), ("2", True), ("0089", True),
        ("-0.1", True), ("+3.14", True), ("4.", True), ("-.9", True),
        ("2e10", True), ("-90E3", True), ("3e+7", True), ("+6e-1", True),
        ("53.5e93", True), ("-123.456e789", True),
        ("abc", False), ("1a", False), ("1e", False), ("e3", False),
        ("99e2.5", False), ("--6", False), ("-+3", False), ("95a54e53", False),
        ("6+1", False), ("+", False), ("-", False),
        ("+.", False), (".e1", False), ("6e6.5", False),
        (".1", True), ("1.", True), ("1.5", True), ("+1", True), ("-1", True),
        (".e", False), ("+e", False), ("6.e2", True),
    ]

    print("=== Single Pass ===")
    for s, exp in cases: test(f"isNumber({s!r})", sol.isNumber(s), exp)

    print("\n=== Regex ===")
    for s, exp in cases: test(f"regex({s!r})", sol.isNumberRegex(s), exp)

    print("\n=== DFA ===")
    for s, exp in cases: test(f"dfa({s!r})", sol.isNumberDFA(s), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
