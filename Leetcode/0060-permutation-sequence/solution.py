from typing import List

# ============================================================
# 0060. Permutation Sequence
# https://leetcode.com/problems/permutation-sequence/
# Difficulty: Hard
# Tags: Math, Recursion
# ============================================================


class Solution:
    def getPermutation(self, n: int, k: int) -> str:
        """
        Optimal Solution (Factorial Number System).
        Time:  O(n^2)
        Space: O(n)
        """
        fact = [1] * (n + 1)
        for i in range(1, n + 1):
            fact[i] = fact[i - 1] * i
        digits = list(range(1, n + 1))
        k -= 1  # 0-based
        result = []
        for i in range(n):
            m = n - i
            q, k = divmod(k, fact[m - 1])
            result.append(str(digits.pop(q)))
        return ''.join(result)

    def getPermutationBrute(self, n: int, k: int) -> str:
        """
        Step (k-1) next permutations from "12...n".
        Time:  O(k * n)
        Space: O(n)
        """
        arr = list(range(1, n + 1))
        for _ in range(k - 1):
            self._nextPerm(arr)
        return ''.join(map(str, arr))

    def _nextPerm(self, a: List[int]) -> None:
        n = len(a)
        i = n - 2
        while i >= 0 and a[i] >= a[i + 1]:
            i -= 1
        if i >= 0:
            j = n - 1
            while a[j] <= a[i]:
                j -= 1
            a[i], a[j] = a[j], a[i]
        l, r = i + 1, n - 1
        while l < r:
            a[l], a[r] = a[r], a[l]
            l += 1
            r -= 1


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
            print(f"  Got:      {got!r}")
            print(f"  Expected: {expected!r}")
            failed += 1

    cases = [
        ("Example 1", 3, 3, "213"),
        ("Example 2", 4, 9, "2314"),
        ("Example 3", 3, 1, "123"),
        ("n=1", 1, 1, "1"),
        ("n=3 last", 3, 6, "321"),
        ("n=4 first", 4, 1, "1234"),
        ("n=4 last", 4, 24, "4321"),
        ("n=4 boundary", 4, 7, "2134"),
        ("n=9 first", 9, 1, "123456789"),
        ("n=9 last", 9, 362880, "987654321"),
        ("n=9 middle", 9, 200000, "596742183"),
    ]

    print("=== Factorial Number System (Optimal) ===")
    for name, n, k, exp in cases:
        test(name, sol.getPermutation(n, k), exp)

    print("\n=== Brute Force (small only) ===")
    for name, n, k, exp in cases:
        if k > 1000: continue
        test("Brute " + name, sol.getPermutationBrute(n, k), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
