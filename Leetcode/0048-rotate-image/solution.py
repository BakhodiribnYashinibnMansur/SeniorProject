# ============================================================
# 0048. Rotate Image
# https://leetcode.com/problems/rotate-image/
# Difficulty: Medium
# Tags: Array, Math, Matrix
# ============================================================


class Solution:
    def rotate(self, matrix: list[list[int]]) -> None:
        """
        Optimal Solution (Transpose + Reverse Rows)
        Approach: Transpose the matrix, then reverse each row
        Time:  O(n^2) — visit each cell a constant number of times
        Space: O(1) — all swaps done in-place
        """
        n = len(matrix)

        # Step 1: Transpose the matrix (swap across the diagonal)
        for i in range(n):
            for j in range(i + 1, n):
                matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]

        # Step 2: Reverse each row
        for i in range(n):
            matrix[i].reverse()

    def rotateFourWay(self, matrix: list[list[int]]) -> None:
        """
        Four-way Swap approach
        Approach: Rotate 4 cells at a time in a cycle
        Time:  O(n^2) — each cell is moved exactly once
        Space: O(1) — only one temp variable
        """
        n = len(matrix)

        # Process layer by layer from outside to inside
        for i in range(n // 2):
            for j in range(i, n - 1 - i):
                # Save top
                temp = matrix[i][j]

                # Left → Top
                matrix[i][j] = matrix[n - 1 - j][i]

                # Bottom → Left
                matrix[n - 1 - j][i] = matrix[n - 1 - i][n - 1 - j]

                # Right → Bottom
                matrix[n - 1 - i][n - 1 - j] = matrix[j][n - 1 - i]

                # Top → Right
                matrix[j][n - 1 - i] = temp


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    import copy

    sol = Solution()
    passed = failed = 0

    def test(name: str, matrix, expected, method="rotate"):
        global passed, failed
        mat = copy.deepcopy(matrix)
        if method == "rotate":
            sol.rotate(mat)
        else:
            sol.rotateFourWay(mat)
        if mat == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {mat}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Transpose + Reverse (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1",
         [[1, 2, 3], [4, 5, 6], [7, 8, 9]],
         [[7, 4, 1], [8, 5, 2], [9, 6, 3]])

    # Test 2: LeetCode Example 2
    test("Example 2",
         [[5, 1, 9, 11], [2, 4, 8, 10], [13, 3, 6, 7], [15, 14, 12, 16]],
         [[15, 13, 2, 5], [14, 3, 4, 1], [12, 6, 8, 9], [16, 7, 10, 11]])

    # Test 3: 1x1 matrix
    test("1x1 matrix", [[1]], [[1]])

    # Test 4: 2x2 matrix
    test("2x2 matrix", [[1, 2], [3, 4]], [[3, 1], [4, 2]])

    # Test 5: Negative values
    test("Negative values",
         [[-1, -2], [-3, -4]],
         [[-3, -1], [-4, -2]])

    # Test 6: All same values
    test("All same values",
         [[5, 5], [5, 5]],
         [[5, 5], [5, 5]])

    # Test 7: 5x5 matrix
    test("5x5 matrix",
         [[1, 2, 3, 4, 5],
          [6, 7, 8, 9, 10],
          [11, 12, 13, 14, 15],
          [16, 17, 18, 19, 20],
          [21, 22, 23, 24, 25]],
         [[21, 16, 11, 6, 1],
          [22, 17, 12, 7, 2],
          [23, 18, 13, 8, 3],
          [24, 19, 14, 9, 4],
          [25, 20, 15, 10, 5]])

    print("\n=== Four-way Swap ===")

    # Test 8: Four-way — Example 1
    test("FW Example 1",
         [[1, 2, 3], [4, 5, 6], [7, 8, 9]],
         [[7, 4, 1], [8, 5, 2], [9, 6, 3]],
         method="fourway")

    # Test 9: Four-way — Example 2
    test("FW Example 2",
         [[5, 1, 9, 11], [2, 4, 8, 10], [13, 3, 6, 7], [15, 14, 12, 16]],
         [[15, 13, 2, 5], [14, 3, 4, 1], [12, 6, 8, 9], [16, 7, 10, 11]],
         method="fourway")

    # Test 10: Four-way — 1x1
    test("FW 1x1 matrix", [[1]], [[1]], method="fourway")

    # Test 11: Four-way — 2x2
    test("FW 2x2 matrix", [[1, 2], [3, 4]], [[3, 1], [4, 2]], method="fourway")

    # Test 12: Four-way — 5x5
    test("FW 5x5 matrix",
         [[1, 2, 3, 4, 5],
          [6, 7, 8, 9, 10],
          [11, 12, 13, 14, 15],
          [16, 17, 18, 19, 20],
          [21, 22, 23, 24, 25]],
         [[21, 16, 11, 6, 1],
          [22, 17, 12, 7, 2],
          [23, 18, 13, 8, 3],
          [24, 19, 14, 9, 4],
          [25, 20, 15, 10, 5]],
         method="fourway")

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
