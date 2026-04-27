from typing import List
import copy

# ============================================================
# 0073. Set Matrix Zeroes
# https://leetcode.com/problems/set-matrix-zeroes/
# Difficulty: Medium
# Tags: Array, Hash Table, Matrix
# ============================================================


class Solution:
    def setZeroes(self, matrix: List[List[int]]) -> None:
        """
        Optimal Solution (First Row/Col Markers, O(1) space).
        Time:  O(m * n)
        Space: O(1)
        """
        m, n = len(matrix), len(matrix[0])
        first_row_zero = any(matrix[0][j] == 0 for j in range(n))
        first_col_zero = any(matrix[i][0] == 0 for i in range(m))
        for i in range(1, m):
            for j in range(1, n):
                if matrix[i][j] == 0:
                    matrix[i][0] = 0
                    matrix[0][j] = 0
        for i in range(1, m):
            for j in range(1, n):
                if matrix[i][0] == 0 or matrix[0][j] == 0:
                    matrix[i][j] = 0
        if first_row_zero:
            for j in range(n): matrix[0][j] = 0
        if first_col_zero:
            for i in range(m): matrix[i][0] = 0

    def setZeroesAux(self, matrix: List[List[int]]) -> None:
        m, n = len(matrix), len(matrix[0])
        zr = [False] * m
        zc = [False] * n
        for i in range(m):
            for j in range(n):
                if matrix[i][j] == 0:
                    zr[i] = True; zc[j] = True
        for i in range(m):
            for j in range(n):
                if zr[i] or zc[j]:
                    matrix[i][j] = 0


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name}"); failed += 1

    cases = [
        ("Example 1", [[1,1,1],[1,0,1],[1,1,1]], [[1,0,1],[0,0,0],[1,0,1]]),
        ("Example 2", [[0,1,2,0],[3,4,5,2],[1,3,1,5]], [[0,0,0,0],[0,4,5,0],[0,3,1,0]]),
        ("No zeros", [[1,2],[3,4]], [[1,2],[3,4]]),
        ("All zeros", [[0,0],[0,0]], [[0,0],[0,0]]),
        ("Single zero corner", [[0,1],[1,1]], [[0,0],[0,1]]),
        ("Single row", [[1,0,1]], [[0,0,0]]),
        ("Single col", [[1],[0],[1]], [[0],[0],[0]]),
        ("1x1 zero", [[0]], [[0]]),
        ("1x1 nonzero", [[5]], [[5]]),
    ]

    print("=== O(1) markers ===")
    for n, g, exp in cases:
        m = copy.deepcopy(g)
        sol.setZeroes(m)
        test(n, m, exp)
    print("\n=== O(m+n) auxiliary ===")
    for n, g, exp in cases:
        m = copy.deepcopy(g)
        sol.setZeroesAux(m)
        test("Aux " + n, m, exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
