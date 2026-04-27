from typing import List

# ============================================================
# 0074. Search a 2D Matrix
# https://leetcode.com/problems/search-a-2d-matrix/
# Difficulty: Medium
# Tags: Array, Binary Search, Matrix
# ============================================================


class Solution:
    def searchMatrix(self, matrix: List[List[int]], target: int) -> bool:
        """
        Optimal Solution (Single Binary Search on Flat Index).
        Time:  O(log(m * n))
        Space: O(1)
        """
        m, n = len(matrix), len(matrix[0])
        lo, hi = 0, m * n - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            v = matrix[mid // n][mid % n]
            if v == target: return True
            if v < target: lo = mid + 1
            else: hi = mid - 1
        return False

    def searchMatrixStaircase(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        r, c = 0, n - 1
        while r < m and c >= 0:
            if matrix[r][c] == target: return True
            if matrix[r][c] < target: r += 1
            else: c -= 1
        return False

    def searchMatrixTwo(self, matrix: List[List[int]], target: int) -> bool:
        m, n = len(matrix), len(matrix[0])
        lo, hi = 0, m - 1
        while lo <= hi:
            mid = (lo + hi) // 2
            if matrix[mid][0] <= target <= matrix[mid][n - 1]:
                l, r = 0, n - 1
                while l <= r:
                    mm = (l + r) // 2
                    if matrix[mid][mm] == target: return True
                    if matrix[mid][mm] < target: l = mm + 1
                    else: r = mm - 1
                return False
            if matrix[mid][0] > target: hi = mid - 1
            else: lo = mid + 1
        return False


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    matrix = [[1, 3, 5, 7], [10, 11, 16, 20], [23, 30, 34, 60]]
    cases = [
        ("Example 1", matrix, 3, True),
        ("Example 2", matrix, 13, False),
        ("Found min", matrix, 1, True),
        ("Found max", matrix, 60, True),
        ("Below min", matrix, 0, False),
        ("Above max", matrix, 100, False),
        ("Found mid", matrix, 16, True),
        ("Not at boundary", matrix, 8, False),
        ("1x1 found", [[5]], 5, True),
        ("1x1 not found", [[5]], 6, False),
        ("Single row found", [[1, 3, 5]], 3, True),
        ("Single row not found", [[1, 3, 5]], 4, False),
        ("Single col found", [[1], [3], [5]], 5, True),
        ("Single col not found", [[1], [3], [5]], 4, False),
    ]

    print("=== Single Binary Search ===")
    for n, m, t, exp in cases: test(n, sol.searchMatrix(m, t), exp)
    print("\n=== Staircase ===")
    for n, m, t, exp in cases: test("Stair " + n, sol.searchMatrixStaircase(m, t), exp)
    print("\n=== Two Binary Searches ===")
    for n, m, t, exp in cases: test("Two " + n, sol.searchMatrixTwo(m, t), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
