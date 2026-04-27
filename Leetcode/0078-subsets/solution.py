from typing import List

# ============================================================
# 0078. Subsets
# https://leetcode.com/problems/subsets/
# Difficulty: Medium
# Tags: Array, Backtracking, Bit Manipulation
# ============================================================


class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        """
        Optimal Solution (Backtracking).
        Time:  O(n * 2^n)
        Space: O(n)
        """
        result = []
        cur = []
        def bt(start: int):
            result.append(cur.copy())
            for i in range(start, len(nums)):
                cur.append(nums[i])
                bt(i + 1)
                cur.pop()
        bt(0)
        return result

    def subsetsCascade(self, nums: List[int]) -> List[List[int]]:
        result = [[]]
        for x in nums:
            result += [s + [x] for s in result]
        return result

    def subsetsBits(self, nums: List[int]) -> List[List[int]]:
        n = len(nums)
        result = []
        for mask in range(1 << n):
            sub = [nums[i] for i in range(n) if (mask >> i) & 1]
            result.append(sub)
        return result


def canon(out):
    return sorted([sorted(s) for s in out])


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if canon(got) == canon(expected): print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name}"); failed += 1

    cases = [
        ("Example 1", [1, 2, 3], [[], [1], [2], [3], [1, 2], [1, 3], [2, 3], [1, 2, 3]]),
        ("Example 2", [0], [[], [0]]),
        ("Two", [4, 5], [[], [4], [5], [4, 5]]),
        ("Negatives", [-1, 2], [[], [-1], [2], [-1, 2]]),
    ]
    print("=== Backtracking ===")
    for n, x, exp in cases: test(n, sol.subsets(x), exp)
    print("\n=== Cascade ===")
    for n, x, exp in cases: test("Casc " + n, sol.subsetsCascade(x), exp)
    print("\n=== Bits ===")
    for n, x, exp in cases: test("Bits " + n, sol.subsetsBits(x), exp)

    for sz in [5, 8, 10]:
        nums = list(range(sz))
        got = len(sol.subsets(nums))
        want = 1 << sz
        if got == want: print(f"PASS: count n={sz} → {got}"); passed += 1
        else: print(f"FAIL: count {got} vs {want}"); failed += 1

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
