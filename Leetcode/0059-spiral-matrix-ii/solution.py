from typing import List

# ============================================================
# 0059. Spiral Matrix II
# https://leetcode.com/problems/spiral-matrix-ii/
# Difficulty: Medium
# Tags: Array, Matrix, Simulation
# ============================================================


class Solution:
    def generateMatrix(self, n: int) -> List[List[int]]:
        """
        Optimal Solution (Layer by Layer).
        Time:  O(n^2)
        Space: O(1) extra
        """
        m = [[0] * n for _ in range(n)]
        top, bottom, left, right = 0, n - 1, 0, n - 1
        val = 1
        while val <= n * n:
            for c in range(left, right + 1):
                m[top][c] = val; val += 1
            top += 1
            for r in range(top, bottom + 1):
                m[r][right] = val; val += 1
            right -= 1
            if top <= bottom:
                for c in range(right, left - 1, -1):
                    m[bottom][c] = val; val += 1
                bottom -= 1
            if left <= right:
                for r in range(bottom, top - 1, -1):
                    m[r][left] = val; val += 1
                left += 1
        return m

    def generateMatrixDirVec(self, n: int) -> List[List[int]]:
        """Direction vectors using matrix as visited. Time O(n^2), Space O(1) extra."""
        m = [[0] * n for _ in range(n)]
        DR, DC = [0, 1, 0, -1], [1, 0, -1, 0]
        r, c, d = 0, 0, 0
        for k in range(1, n * n + 1):
            m[r][c] = k
            nr, nc = r + DR[d], c + DC[d]
            if not (0 <= nr < n and 0 <= nc < n) or m[nr][nc] != 0:
                d = (d + 1) % 4
                nr, nc = r + DR[d], c + DC[d]
            r, c = nr, nc
        return m


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
        ("n=1", 1, [[1]]),
        ("n=2", 2, [[1, 2], [4, 3]]),
        ("n=3", 3, [[1, 2, 3], [8, 9, 4], [7, 6, 5]]),
        ("n=4", 4, [
            [1, 2, 3, 4],
            [12, 13, 14, 5],
            [11, 16, 15, 6],
            [10, 9, 8, 7],
        ]),
        ("n=5", 5, [
            [1, 2, 3, 4, 5],
            [16, 17, 18, 19, 6],
            [15, 24, 25, 20, 7],
            [14, 23, 22, 21, 8],
            [13, 12, 11, 10, 9],
        ]),
    ]

    print("=== Layer by Layer ===")
    for name, n, exp in cases:
        test(name, sol.generateMatrix(n), exp)

    print("\n=== Direction Vectors ===")
    for name, n, exp in cases:
        test("DirVec " + name, sol.generateMatrixDirVec(n), exp)

    # n=20 sanity
    big = sol.generateMatrix(20)
    seen = set()
    for row in big:
        for v in row:
            seen.add(v)
    if seen == set(range(1, 401)):
        print("PASS: n=20 contains 1..400 exactly once")
        passed += 1
    else:
        print("FAIL: n=20 missing values")
        failed += 1

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
