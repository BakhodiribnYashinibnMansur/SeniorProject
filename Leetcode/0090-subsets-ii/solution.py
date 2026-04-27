from typing import List


class Solution:
    def subsetsWithDup(self, nums: List[int]) -> List[List[int]]:
        """Time O(n*2^n), Space O(n)."""
        nums.sort()
        result = []
        cur = []
        def bt(start: int):
            result.append(cur.copy())
            for i in range(start, len(nums)):
                if i > start and nums[i] == nums[i - 1]:
                    continue
                cur.append(nums[i])
                bt(i + 1)
                cur.pop()
        bt(0)
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
        ("Example 1", [1, 2, 2], [[], [1], [2], [1, 2], [2, 2], [1, 2, 2]]),
        ("Example 2", [0], [[], [0]]),
        ("All same", [4, 4, 4], [[], [4], [4, 4], [4, 4, 4]]),
        ("Two different", [1, 2], [[], [1], [2], [1, 2]]),
        ("Mixed dup", [1, 2, 2, 3], [
            [], [1], [2], [3], [1, 2], [1, 3], [2, 2], [2, 3],
            [1, 2, 2], [1, 2, 3], [2, 2, 3], [1, 2, 2, 3]]),
    ]
    for n, x, exp in cases: test(n, sol.subsetsWithDup(x.copy()), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
