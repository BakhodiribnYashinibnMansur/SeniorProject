from typing import List


class Solution:
    def largestRectangleArea(self, heights: List[int]) -> int:
        """Time O(n), Space O(n) — Monotonic Stack."""
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


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got} want={expected}"); failed += 1

    cases = [
        ("Example 1", [2, 1, 5, 6, 2, 3], 10),
        ("Example 2", [2, 4], 4),
        ("Single", [5], 5),
        ("All same", [3, 3, 3, 3], 12),
        ("Strictly increasing", [1, 2, 3, 4, 5], 9),
        ("Strictly decreasing", [5, 4, 3, 2, 1], 9),
        ("With zeros", [0, 1, 0, 2], 2),
        ("All zeros", [0, 0, 0], 0),
        ("Spike", [1, 100], 100),
        ("Peak", [1, 2, 3, 2, 1], 6),
        ("Empty bars then bars", [0, 0, 2, 1, 2], 3),
    ]
    for n, h, exp in cases: test(n, sol.largestRectangleArea(h), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
