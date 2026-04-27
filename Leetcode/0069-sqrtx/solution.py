# ============================================================
# 0069. Sqrt(x)
# https://leetcode.com/problems/sqrtx/
# Difficulty: Easy
# Tags: Math, Binary Search
# ============================================================


class Solution:
    def mySqrt(self, x: int) -> int:
        """
        Optimal Solution (Binary Search).
        Time:  O(log x)
        Space: O(1)
        """
        if x < 2: return x
        lo, hi, ans = 1, x // 2, 0
        while lo <= hi:
            mid = (lo + hi) // 2
            if mid * mid <= x:
                ans = mid
                lo = mid + 1
            else:
                hi = mid - 1
        return ans

    def mySqrtNewton(self, x: int) -> int:
        """Newton's Method. Time O(log log x), Space O(1)."""
        if x < 2: return x
        r = x
        while r * r > x:
            r = (r + x // r) // 2
        return r

    def mySqrtLinear(self, x: int) -> int:
        if x < 2: return x
        r = 1
        while (r + 1) * (r + 1) <= x:
            r += 1
        return r

    def mySqrtBits(self, x: int) -> int:
        if x < 2: return x
        result = 0
        bit = 1 << 16
        while bit > 0:
            cand = result | bit
            if cand * cand <= x:
                result = cand
            bit >>= 1
        return result


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1

    cases = [
        (0, 0), (1, 1), (2, 1), (3, 1), (4, 2), (8, 2),
        (15, 3), (16, 4), (17, 4), (25, 5), (26, 5),
        (99, 9), (100, 10), (101, 10),
        (2147395599, 46339), (2147483647, 46340),
    ]

    print("=== Binary Search ===")
    for x, exp in cases: test(f"x={x}", sol.mySqrt(x), exp)
    print("\n=== Newton's Method ===")
    for x, exp in cases: test(f"Newton x={x}", sol.mySqrtNewton(x), exp)
    print("\n=== Bit Manipulation ===")
    for x, exp in cases: test(f"Bits x={x}", sol.mySqrtBits(x), exp)
    print("\n=== Linear (small only) ===")
    for x, exp in cases:
        if x > 10000: continue
        test(f"Linear x={x}", sol.mySqrtLinear(x), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
