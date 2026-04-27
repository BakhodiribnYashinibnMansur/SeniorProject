from typing import List

# ============================================================
# 0079. Word Search
# https://leetcode.com/problems/word-search/
# Difficulty: Medium
# Tags: Array, Backtracking, Matrix
# ============================================================


class Solution:
    def exist(self, board: List[List[str]], word: str) -> bool:
        """
        Optimal Solution (DFS + Backtracking).
        Time:  O(m * n * 4^L)
        Space: O(L)
        """
        m, n = len(board), len(board[0])
        def dfs(r: int, c: int, i: int) -> bool:
            if i == len(word): return True
            if r < 0 or r >= m or c < 0 or c >= n or board[r][c] != word[i]:
                return False
            save = board[r][c]
            board[r][c] = '#'
            ok = (dfs(r+1, c, i+1) or dfs(r-1, c, i+1) or
                  dfs(r, c+1, i+1) or dfs(r, c-1, i+1))
            board[r][c] = save
            return ok
        for r in range(m):
            for c in range(n):
                if dfs(r, c, 0): return True
        return False


def to_board(rows):
    return [list(r) for r in rows]


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    standard = ["ABCE", "SFCS", "ADEE"]
    cases = [
        ("Example 1", standard, "ABCCED", True),
        ("Example 2", standard, "SEE", True),
        ("Example 3", standard, "ABCB", False),
        ("Single cell match", ["A"], "A", True),
        ("Single cell miss", ["A"], "B", False),
        ("Word longer than grid", ["AB"], "ABC", False),
        ("Same letter twice", ["AAB"], "AAB", True),
        ("Spiral path", ["ABCD", "EFGH", "IJKL"], "ABCDHGFE", True),
        ("Diagonal not allowed", ["AB", "CD"], "AD", False),
        ("FCC found", standard, "FCC", True),
        ("BCB not adjacent path", standard, "BCBC", False),
    ]
    for n, b, w, exp in cases:
        test(n, sol.exist(to_board(b), w), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
