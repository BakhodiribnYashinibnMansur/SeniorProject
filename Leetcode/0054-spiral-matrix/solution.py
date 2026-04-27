from typing import List
import copy

# ============================================================
# 0054. Spiral Matrix
# https://leetcode.com/problems/spiral-matrix/
# Difficulty: Medium
# Tags: Array, Matrix, Simulation
# ============================================================


class Solution:
    def spiralOrder(self, matrix: List[List[int]]) -> List[int]:
        """
        Optimal Solution (Layer by Layer).
        Time:  O(m*n)
        Space: O(1)
        """
        if not matrix:
            return []
        m, n = len(matrix), len(matrix[0])
        top, bottom, left, right = 0, m - 1, 0, n - 1
        result: List[int] = []
        while top <= bottom and left <= right:
            for c in range(left, right + 1):
                result.append(matrix[top][c])
            top += 1
            for r in range(top, bottom + 1):
                result.append(matrix[r][right])
            right -= 1
            if top <= bottom:
                for c in range(right, left - 1, -1):
                    result.append(matrix[bottom][c])
                bottom -= 1
            if left <= right:
                for r in range(bottom, top - 1, -1):
                    result.append(matrix[r][left])
                left += 1
        return result

    def spiralOrderDirVec(self, matrix: List[List[int]]) -> List[int]:
        """Direction vectors with visited grid. Time O(m*n), Space O(m*n)."""
        if not matrix:
            return []
        m, n = len(matrix), len(matrix[0])
        visited = [[False] * n for _ in range(m)]
        DR, DC = [0, 1, 0, -1], [1, 0, -1, 0]
        r, c, d = 0, 0, 0
        result = []
        for _ in range(m * n):
            result.append(matrix[r][c])
            visited[r][c] = True
            nr, nc = r + DR[d], c + DC[d]
            if not (0 <= nr < m and 0 <= nc < n) or visited[nr][nc]:
                d = (d + 1) % 4
                nr, nc = r + DR[d], c + DC[d]
            r, c = nr, nc
        return result

    def spiralOrderInPlace(self, matrix: List[List[int]]) -> List[int]:
        """In-place marker. Mutates input. Time O(m*n), Space O(1)."""
        if not matrix:
            return []
        SENTINEL = 1 << 30
        m, n = len(matrix), len(matrix[0])
        DR, DC = [0, 1, 0, -1], [1, 0, -1, 0]
        r, c, d = 0, 0, 0
        result = []
        for _ in range(m * n):
            result.append(matrix[r][c])
            matrix[r][c] = SENTINEL
            nr, nc = r + DR[d], c + DC[d]
            if not (0 <= nr < m and 0 <= nc < n) or matrix[nr][nc] == SENTINEL:
                d = (d + 1) % 4
                nr, nc = r + DR[d], c + DC[d]
            r, c = nr, nc
        return result


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
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    cases = [
        ("3x3", [[1, 2, 3], [4, 5, 6], [7, 8, 9]], [1, 2, 3, 6, 9, 8, 7, 4, 5]),
        ("3x4", [[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12]],
         [1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7]),
        ("1x1", [[5]], [5]),
        ("1x4 row", [[1, 2, 3, 4]], [1, 2, 3, 4]),
        ("3x1 column", [[1], [2], [3]], [1, 2, 3]),
        ("2x2", [[1, 2], [3, 4]], [1, 2, 4, 3]),
        ("2x4", [[1, 2, 3, 4], [5, 6, 7, 8]], [1, 2, 3, 4, 8, 7, 6, 5]),
        ("3x2", [[1, 2], [3, 4], [5, 6]], [1, 2, 4, 6, 5, 3]),
        ("4x4", [[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12], [13, 14, 15, 16]],
         [1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10]),
        ("Negatives", [[-1, -2], [-3, -4]], [-1, -2, -4, -3]),
    ]

    print("=== Layer by Layer ===")
    for name, mat, exp in cases:
        test(name, sol.spiralOrder(copy.deepcopy(mat)), exp)

    print("\n=== Direction Vectors + Visited ===")
    for name, mat, exp in cases:
        test("DirVec " + name, sol.spiralOrderDirVec(copy.deepcopy(mat)), exp)

    print("\n=== In-Place Marker ===")
    for name, mat, exp in cases:
        test("InPlace " + name, sol.spiralOrderInPlace(copy.deepcopy(mat)), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
