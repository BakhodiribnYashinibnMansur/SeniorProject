from typing import List


class Solution:
    def maximalRectangle(self, matrix: List[List[str]]) -> int:
        """Time O(rows*cols), Space O(cols)."""
        if not matrix or not matrix[0]:
            return 0
        cols = len(matrix[0])
        heights = [0] * cols
        best = 0
        for row in matrix:
            for c in range(cols):
                heights[c] = heights[c] + 1 if row[c] == '1' else 0
            best = max(best, self._largestRect(heights))
        return best

    def _largestRect(self, heights):
        n = len(heights)
        stack = []
        best = 0
        for i in range(n + 1):
            h = 0 if i == n else heights[i]
            while stack and heights[stack[-1]] > h:
                top = stack.pop()
                width = i if not stack else i - stack[-1] - 1
                best = max(best, heights[top] * width)
            stack.append(i)
        return best


def to_mat(rows):
    return [list(r) for r in rows]


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, exp):
        global passed, failed
        if got == exp: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got} want={exp}"); failed += 1
    cases = [
        ("Example 1", ["10100","10111","11111","10010"], 6),
        ("Single 0", ["0"], 0),
        ("Single 1", ["1"], 1),
        ("All zeros", ["00","00"], 0),
        ("All ones 3x3", ["111","111","111"], 9),
        ("Single row mixed", ["01101"], 2),
        ("Single col", ["1","1","0","1","1","1"], 3),
        ("L shape", ["110","110","111"], 6),
    ]
    for n, m, exp in cases: test(n, sol.maximalRectangle(to_mat(m)), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
