from typing import List

# ============================================================
# 0051. N-Queens
# https://leetcode.com/problems/n-queens/
# Difficulty: Hard
# Tags: Array, Backtracking
# ============================================================


class Solution:
    def solveNQueens(self, n: int) -> List[List[str]]:
        """
        Optimal Solution (Backtracking with Sets).
        Approach: place one queen per row, prune via three sets that track
          occupied columns and the two diagonal kinds.
        Time:  O(n!) practical
        Space: O(n)
        """
        result: List[List[str]] = []
        queens = [0] * n
        cols: set = set()
        diag1: set = set()  # r - c (the '\' diagonal)
        diag2: set = set()  # r + c (the '/' diagonal)

        def backtrack(r: int) -> None:
            if r == n:
                result.append(['.' * c + 'Q' + '.' * (n - c - 1) for c in queens])
                return
            for c in range(n):
                if c in cols or (r - c) in diag1 or (r + c) in diag2:
                    continue
                queens[r] = c
                cols.add(c); diag1.add(r - c); diag2.add(r + c)
                backtrack(r + 1)
                cols.remove(c); diag1.remove(r - c); diag2.remove(r + c)

        backtrack(0)
        return result

    def solveNQueensBitmask(self, n: int) -> List[List[str]]:
        """
        Bitmask Backtracking (fastest in practice).
        Time:  O(n!) practical
        Space: O(n)
        """
        result: List[List[str]] = []
        queens = [0] * n
        full = (1 << n) - 1

        def backtrack(r: int, cols: int, d1: int, d2: int) -> None:
            if r == n:
                result.append(['.' * c + 'Q' + '.' * (n - c - 1) for c in queens])
                return
            free = full & ~(cols | d1 | d2)
            while free:
                bit = free & -free
                c = bit.bit_length() - 1
                queens[r] = c
                backtrack(r + 1, cols | bit, ((d1 | bit) << 1) & full, (d2 | bit) >> 1)
                free &= free - 1

        backtrack(0, 0, 0, 0)
        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def canonical(boards):
        return sorted([list(b) for b in boards])

    def test(name, got, expected):
        global passed, failed
        if canonical(got) == canonical(expected):
            print(f"PASS: {name}")
            passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    def test_count(name, got, expected_count):
        global passed, failed
        if len(got) == expected_count:
            print(f"PASS: {name} (count={expected_count})")
            passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got count:      {len(got)}")
            print(f"  Expected count: {expected_count}")
            failed += 1

    print("=== Backtracking with Sets ===")

    # Test 1: n = 1
    test("n=1", sol.solveNQueens(1), [["Q"]])

    # Test 2: n = 2 — no solution
    test("n=2 (no solution)", sol.solveNQueens(2), [])

    # Test 3: n = 3 — no solution
    test("n=3 (no solution)", sol.solveNQueens(3), [])

    # Test 4: n = 4 — 2 solutions
    test("n=4", sol.solveNQueens(4), [
        [".Q..", "...Q", "Q...", "..Q."],
        ["..Q.", "Q...", "...Q", ".Q.."],
    ])

    # Counts for larger n
    test_count("n=5 count", sol.solveNQueens(5), 10)
    test_count("n=6 count", sol.solveNQueens(6), 4)
    test_count("n=7 count", sol.solveNQueens(7), 40)
    test_count("n=8 count (classic)", sol.solveNQueens(8), 92)
    test_count("n=9 count", sol.solveNQueens(9), 352)

    print("\n=== Backtracking with Bitmasks ===")

    test("Bitmask n=4", sol.solveNQueensBitmask(4), [
        [".Q..", "...Q", "Q...", "..Q."],
        ["..Q.", "Q...", "...Q", ".Q.."],
    ])

    test_count("Bitmask n=8 count", sol.solveNQueensBitmask(8), 92)
    test_count("Bitmask n=9 count", sol.solveNQueensBitmask(9), 352)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
