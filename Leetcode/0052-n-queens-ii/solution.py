from typing import List

# ============================================================
# 0052. N-Queens II
# https://leetcode.com/problems/n-queens-ii/
# Difficulty: Hard
# Tags: Backtracking
# ============================================================


class Solution:
    def totalNQueens(self, n: int) -> int:
        """
        Optimal Solution (Bitmask Backtracking).
        Time:  O(n!) practical
        Space: O(n)
        """
        full = (1 << n) - 1

        def solve(cols: int, d1: int, d2: int) -> int:
            if cols == full:
                return 1
            free = full & ~(cols | d1 | d2)
            total = 0
            while free:
                bit = free & -free
                total += solve(cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1)
                free &= free - 1
            return total

        return solve(0, 0, 0)

    def totalNQueensSets(self, n: int) -> int:
        """
        Backtracking with sets (more readable).
        Time:  O(n!) practical
        Space: O(n)
        """
        cols, d1, d2 = set(), set(), set()
        count = 0

        def backtrack(r: int) -> None:
            nonlocal count
            if r == n:
                count += 1
                return
            for c in range(n):
                if c in cols or (r - c) in d1 or (r + c) in d2:
                    continue
                cols.add(c); d1.add(r - c); d2.add(r + c)
                backtrack(r + 1)
                cols.remove(c); d1.remove(r - c); d2.remove(r + c)

        backtrack(0)
        return count

    def totalNQueensLookup(self, n: int) -> int:
        """
        Precomputed table (valid because n <= 9).
        Time:  O(1)
        Space: O(1)
        """
        return [0, 1, 0, 0, 2, 10, 4, 40, 92, 352][n]


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"PASS: {name}")
            passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    expected = [0, 1, 0, 0, 2, 10, 4, 40, 92, 352]

    print("=== Bitmask backtracking ===")
    for n in range(1, 10):
        test(f"n={n}", sol.totalNQueens(n), expected[n])

    print("\n=== Sets backtracking ===")
    for n in range(1, 10):
        test(f"Sets n={n}", sol.totalNQueensSets(n), expected[n])

    print("\n=== Lookup table ===")
    for n in range(1, 10):
        test(f"Lookup n={n}", sol.totalNQueensLookup(n), expected[n])

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
