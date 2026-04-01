# ============================================================
# 0036. Valid Sudoku
# https://leetcode.com/problems/valid-sudoku/
# Difficulty: Medium
# Tags: Array, Hash Table, Matrix
# ============================================================


class Solution:
    def isValidSudoku(self, board: list[list[str]]) -> bool:
        """
        Optimal Solution (Array-based Validation)
        Approach: Single pass with boolean arrays for rows, cols, and boxes
        Time:  O(1) — always 81 cells (9x9 fixed board)
        Space: O(1) — three 9x9 boolean arrays (fixed size)
        """
        rows = [[False] * 9 for _ in range(9)]
        cols = [[False] * 9 for _ in range(9)]
        boxes = [[False] * 9 for _ in range(9)]

        for r in range(9):
            for c in range(9):
                if board[r][c] == '.':
                    continue

                d = int(board[r][c]) - 1
                box = (r // 3) * 3 + c // 3

                if rows[r][d] or cols[c][d] or boxes[box][d]:
                    return False

                rows[r][d] = True
                cols[c][d] = True
                boxes[box][d] = True

        return True

    def isValidSudokuHashSet(self, board: list[list[str]]) -> bool:
        """
        Hash Set approach
        Approach: Use sets to track seen digits per row, column, and box
        Time:  O(1) — always 81 cells
        Space: O(1) — 27 sets, each at most 9 elements
        """
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxes = [set() for _ in range(9)]

        for r in range(9):
            for c in range(9):
                val = board[r][c]
                if val == '.':
                    continue

                box = (r // 3) * 3 + c // 3

                if val in rows[r] or val in cols[c] or val in boxes[box]:
                    return False

                rows[r].add(val)
                cols[c].add(val)
                boxes[box].add(val)

        return True


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Valid board (LeetCode Example 1)
    valid_board = [
        ["5","3",".",".","7",".",".",".","."],
        ["6",".",".","1","9","5",".",".","."],
        [".","9","8",".",".",".",".","6","."],
        ["8",".",".",".","6",".",".",".","3"],
        ["4",".",".","8",".","3",".",".","1"],
        ["7",".",".",".","2",".",".",".","6"],
        [".","6",".",".",".",".","2","8","."],
        [".",".",".","4","1","9",".",".","5"],
        [".",".",".",".","8",".",".","7","9"]
    ]

    # Invalid board (LeetCode Example 2) — duplicate 8 in top-left box
    invalid_board = [
        ["8","3",".",".","7",".",".",".","."],
        ["6",".",".","1","9","5",".",".","."],
        [".","9","8",".",".",".",".","6","."],
        ["8",".",".",".","6",".",".",".","3"],
        ["4",".",".","8",".","3",".",".","1"],
        ["7",".",".",".","2",".",".",".","6"],
        [".","6",".",".",".",".","2","8","."],
        [".",".",".","4","1","9",".",".","5"],
        [".",".",".",".","8",".",".","7","9"]
    ]

    # Board with duplicate in a row
    row_dup_board = [
        ["5","3",".",".","7",".",".",".","5"],  # 5 appears twice in row 0
        ["6",".",".","1","9","5",".",".","."],
        [".","9","8",".",".",".",".","6","."],
        ["8",".",".",".","6",".",".",".","3"],
        ["4",".",".","8",".","3",".",".","1"],
        ["7",".",".",".","2",".",".",".","6"],
        [".","6",".",".",".",".","2","8","."],
        [".",".",".","4","1","9",".",".","5"],
        [".",".",".",".","8",".",".","7","9"]
    ]

    # Board with duplicate in a column
    col_dup_board = [
        ["5","3",".",".","7",".",".",".","."],
        ["6",".",".","1","9","5",".",".","."],
        [".","9","8",".",".",".",".","6","."],
        ["8",".",".",".","6",".",".",".","3"],
        ["4",".",".","8",".","3",".",".","1"],
        ["7",".",".",".","2",".",".",".","6"],
        [".","6",".",".",".",".","2","8","."],
        [".",".",".","4","1","9",".",".","5"],
        ["5",".",".",".","8",".",".","7","9"]  # 5 in col 0 (same as row 0)
    ]

    # Almost empty board
    empty_board = [
        [".",".",".",".",".",".",".",".","."]] * 8 + [
        [".",".",".",".",".",".",".",".","."]
    ]

    # Board with single value
    single_board = [["." for _ in range(9)] for _ in range(9)]
    single_board[4][4] = "5"

    print("=== Array-based Validation (Optimal) ===")

    test("Example 1 (valid)", sol.isValidSudoku(valid_board), True)
    test("Example 2 (invalid box)", sol.isValidSudoku(invalid_board), False)
    test("Row duplicate", sol.isValidSudoku(row_dup_board), False)
    test("Column duplicate", sol.isValidSudoku(col_dup_board), False)
    test("Almost empty board", sol.isValidSudoku(empty_board), True)
    test("Single value board", sol.isValidSudoku(single_board), True)

    print("\n=== Hash Set Approach ===")

    test("HS: Example 1 (valid)", sol.isValidSudokuHashSet(valid_board), True)
    test("HS: Example 2 (invalid box)", sol.isValidSudokuHashSet(invalid_board), False)
    test("HS: Row duplicate", sol.isValidSudokuHashSet(row_dup_board), False)
    test("HS: Column duplicate", sol.isValidSudokuHashSet(col_dup_board), False)
    test("HS: Almost empty board", sol.isValidSudokuHashSet(empty_board), True)
    test("HS: Single value board", sol.isValidSudokuHashSet(single_board), True)

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
