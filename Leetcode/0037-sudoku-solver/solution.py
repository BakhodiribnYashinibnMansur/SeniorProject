# ============================================================
# 0037. Sudoku Solver
# https://leetcode.com/problems/sudoku-solver/
# Difficulty: Hard
# Tags: Array, Hash Table, Backtracking, Matrix
# ============================================================


class Solution:
    def solveSudoku(self, board: list[list[str]]) -> None:
        """
        Optimal Solution (Backtracking with Set tracking)
        Approach: Try digits 1-9 for each empty cell, backtrack on conflict
        Time:  O(9^m) — m is number of empty cells, heavily pruned in practice
        Space: O(m)   — recursion depth equals the number of empty cells
        """
        # Tracking sets: which digits are used in each row/col/box
        rows = [set() for _ in range(9)]
        cols = [set() for _ in range(9)]
        boxes = [set() for _ in range(9)]
        empty = []

        # Initialize: scan existing digits
        for r in range(9):
            for c in range(9):
                if board[r][c] != '.':
                    d = board[r][c]
                    rows[r].add(d)
                    cols[c].add(d)
                    boxes[(r // 3) * 3 + c // 3].add(d)
                else:
                    empty.append((r, c))

        def solve(idx: int) -> bool:
            if idx == len(empty):
                return True  # All cells filled

            r, c = empty[idx]
            box_id = (r // 3) * 3 + c // 3

            for d in '123456789':
                if d not in rows[r] and d not in cols[c] and d not in boxes[box_id]:
                    # Place digit
                    board[r][c] = d
                    rows[r].add(d)
                    cols[c].add(d)
                    boxes[box_id].add(d)

                    if solve(idx + 1):
                        return True

                    # Backtrack
                    board[r][c] = '.'
                    rows[r].remove(d)
                    cols[c].remove(d)
                    boxes[box_id].remove(d)

            return False

        solve(0)


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, board: list[list[str]], expected: list[list[str]]):
        global passed, failed
        sol.solveSudoku(board)
        if board == expected:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:")
            for row in board:
                print(f"    {row}")
            print(f"  Expected:")
            for row in expected:
                print(f"    {row}")
            failed += 1

    # Test 1: LeetCode example
    test("LeetCode example",
        [["5","3",".",".","7",".",".",".","."],
         ["6",".",".","1","9","5",".",".","."],
         [".","9","8",".",".",".",".","6","."],
         ["8",".",".",".","6",".",".",".","3"],
         ["4",".",".","8",".","3",".",".","1"],
         ["7",".",".",".","2",".",".",".","6"],
         [".","6",".",".",".",".","2","8","."],
         [".",".",".","4","1","9",".",".","5"],
         [".",".",".",".","8",".",".","7","9"]],
        [["5","3","4","6","7","8","9","1","2"],
         ["6","7","2","1","9","5","3","4","8"],
         ["1","9","8","3","4","2","5","6","7"],
         ["8","5","9","7","6","1","4","2","3"],
         ["4","2","6","8","5","3","7","9","1"],
         ["7","1","3","9","2","4","8","5","6"],
         ["9","6","1","5","3","7","2","8","4"],
         ["2","8","7","4","1","9","6","3","5"],
         ["3","4","5","2","8","6","1","7","9"]])

    # Test 2: Almost solved — only one empty cell
    test("Almost solved",
        [["5","3","4","6","7","8","9","1","2"],
         ["6","7","2","1","9","5","3","4","8"],
         ["1","9","8","3","4","2","5","6","7"],
         ["8","5","9","7","6","1","4","2","3"],
         ["4","2","6","8","5","3","7","9","1"],
         ["7","1","3","9","2","4","8","5","6"],
         ["9","6","1","5","3","7","2","8","4"],
         ["2","8","7","4","1","9","6","3","5"],
         ["3","4","5","2","8","6","1",".","9"]],
        [["5","3","4","6","7","8","9","1","2"],
         ["6","7","2","1","9","5","3","4","8"],
         ["1","9","8","3","4","2","5","6","7"],
         ["8","5","9","7","6","1","4","2","3"],
         ["4","2","6","8","5","3","7","9","1"],
         ["7","1","3","9","2","4","8","5","6"],
         ["9","6","1","5","3","7","2","8","4"],
         ["2","8","7","4","1","9","6","3","5"],
         ["3","4","5","2","8","6","1","7","9"]])

    # Test 3: Hard puzzle — requires deep backtracking
    test("Hard puzzle",
        [[".",".","9","7","4","8",".",".","."],
         ["7",".",".",".",".",".",".",".","."],
         [".","2",".","1",".","9",".",".","."],
         [".",".","7",".",".",".","2","4","."],
         [".","6","4",".","1",".","5","9","."],
         [".","9","8",".",".",".","3",".","."],
         [".",".",".","8",".","3",".","2","."],
         [".",".",".",".",".",".",".",".","6"],
         [".",".",".","2","7","5","9",".","."]],
        [["5","1","9","7","4","8","6","3","2"],
         ["7","8","3","6","5","2","4","1","9"],
         ["4","2","6","1","3","9","8","7","5"],
         ["3","5","7","9","8","6","2","4","1"],
         ["2","6","4","3","1","7","5","9","8"],
         ["1","9","8","5","2","4","3","6","7"],
         ["9","7","5","8","6","3","1","2","4"],
         ["8","3","2","4","9","1","7","5","6"],
         ["6","4","1","2","7","5","9","8","3"]])

    # Test 4: Sparse puzzle — validate solution correctness
    board4 = [[".",".",".",".",".",".",".","1","."],
              ["4",".",".",".",".",".",".",".","2"],
              [".",".",".",".",".",".",".",".","."],
              [".",".",".",".",".",".",".",".","."],
              [".",".",".",".",".",".",".",".","3"],
              [".",".",".",".",".","4",".",".","."],
              [".",".",".",".",".",".",".",".","."],
              [".",".",".","5",".",".",".",".","."],
              [".","6",".",".",".",".",".",".","."]]

    sol.solveSudoku(board4)
    valid = True
    for r in range(9):
        if len(set(board4[r])) != 9:
            valid = False
    for c in range(9):
        if len(set(board4[r][c] for r in range(9))) != 9:
            valid = False
    for br in range(3):
        for bc in range(3):
            box = set()
            for r in range(br*3, br*3+3):
                for c in range(bc*3, bc*3+3):
                    box.add(board4[r][c])
            if len(box) != 9:
                valid = False
    if valid:
        print("\u2705 PASS: Sparse puzzle (validated)")
        passed += 1
    else:
        print("\u274c FAIL: Sparse puzzle (invalid solution)")
        failed += 1

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
