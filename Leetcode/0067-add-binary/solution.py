# ============================================================
# 0067. Add Binary
# https://leetcode.com/problems/add-binary/
# Difficulty: Easy
# Tags: Math, String, Bit Manipulation, Simulation
# ============================================================


class Solution:
    def addBinary(self, a: str, b: str) -> str:
        """
        Optimal Solution (Walk + Carry).
        Time:  O(max(n, m))
        Space: O(max(n, m))
        """
        i, j, carry = len(a) - 1, len(b) - 1, 0
        out = []
        while i >= 0 or j >= 0 or carry > 0:
            s = carry
            if i >= 0: s += int(a[i]); i -= 1
            if j >= 0: s += int(b[j]); j -= 1
            out.append(str(s % 2))
            carry = s // 2
        return ''.join(reversed(out))

    def addBinaryBits(self, a: str, b: str) -> str:
        """XOR + AND. Time O((max len)^2), Space O(max len)."""
        x, y = int(a, 2), int(b, 2)
        while y != 0:
            x, y = x ^ y, (x & y) << 1
        return bin(x)[2:] if x else '0'

    def addBinaryInt(self, a: str, b: str) -> str:
        """Python int trick. Works because Python int is unbounded."""
        return bin(int(a, 2) + int(b, 2))[2:]


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got!r}"); failed += 1

    cases = [
        ("Example 1", "11", "1", "100"),
        ("Example 2", "1010", "1011", "10101"),
        ("Both zero", "0", "0", "0"),
        ("One zero", "0", "1", "1"),
        ("One zero left", "1", "0", "1"),
        ("Carry chain", "1", "1", "10"),
        ("Cascading carry", "111", "1", "1000"),
        ("Different lengths", "1", "111", "1000"),
        ("All ones", "1111", "1111", "11110"),
        ("Result with leading carry", "11111111", "1", "100000000"),
        ("Equal length no carry", "1010", "0101", "1111"),
    ]

    print("=== Walk + Carry ===")
    for name, a, b, exp in cases:
        test(name, sol.addBinary(a, b), exp)

    print("\n=== Bits XOR ===")
    for name, a, b, exp in cases:
        test("Bits " + name, sol.addBinaryBits(a, b), exp)

    print("\n=== Int trick ===")
    for name, a, b, exp in cases:
        test("Int " + name, sol.addBinaryInt(a, b), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
