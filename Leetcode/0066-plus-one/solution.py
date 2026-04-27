from typing import List

# ============================================================
# 0066. Plus One
# https://leetcode.com/problems/plus-one/
# Difficulty: Easy
# Tags: Array, Math
# ============================================================


class Solution:
    def plusOne(self, digits: List[int]) -> List[int]:
        """
        Optimal Solution (Walk + Carry).
        Time:  O(n)
        Space: O(1) amortized
        """
        carry = 1
        for i in range(len(digits) - 1, -1, -1):
            if carry == 0:
                break
            s = digits[i] + carry
            digits[i] = s % 10
            carry = s // 10
        if carry > 0:
            return [carry] + digits
        return digits

    def plusOneEarly(self, digits: List[int]) -> List[int]:
        n = len(digits)
        for i in range(n - 1, -1, -1):
            if digits[i] != 9:
                digits[i] += 1
                for j in range(i + 1, n): digits[j] = 0
                return digits
        return [1] + [0] * n


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        ("Example 1", [1, 2, 3], [1, 2, 4]),
        ("Example 2", [4, 3, 2, 1], [4, 3, 2, 2]),
        ("Example 3", [9], [1, 0]),
        ("Single zero", [0], [1]),
        ("All nines 3", [9, 9, 9], [1, 0, 0, 0]),
        ("All nines 5", [9, 9, 9, 9, 9], [1, 0, 0, 0, 0, 0]),
        ("Trailing zeros", [1, 0, 0], [1, 0, 1]),
        ("Trailing nines", [1, 9, 9], [2, 0, 0]),
        ("Mid nines", [2, 9, 9, 1], [2, 9, 9, 2]),
        ("Long number", [1, 2, 3, 4, 5, 6, 7, 8, 9, 0], [1, 2, 3, 4, 5, 6, 7, 8, 9, 1]),
    ]

    print("=== Walk + Carry ===")
    for name, d, exp in cases:
        test(name, sol.plusOne(d.copy()), exp)

    print("\n=== Early Exit ===")
    for name, d, exp in cases:
        test("Early " + name, sol.plusOneEarly(d.copy()), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
