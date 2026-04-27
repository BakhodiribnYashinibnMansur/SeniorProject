from typing import List


class Solution:
    def merge(self, nums1: List[int], m: int, nums2: List[int], n: int) -> None:
        """Time O(m+n), Space O(1)."""
        i, j, k = m - 1, n - 1, m + n - 1
        while j >= 0:
            if i >= 0 and nums1[i] > nums2[j]:
                nums1[k] = nums1[i]; i -= 1
            else:
                nums1[k] = nums2[j]; j -= 1
            k -= 1


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, exp):
        global passed, failed
        if got == exp: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1
    cases = [
        ("Example 1", [1, 2, 3, 0, 0, 0], 3, [2, 5, 6], 3, [1, 2, 2, 3, 5, 6]),
        ("Example 2", [1], 1, [], 0, [1]),
        ("Example 3", [0], 0, [1], 1, [1]),
        ("All nums2 smaller", [4, 5, 6, 0, 0, 0], 3, [1, 2, 3], 3, [1, 2, 3, 4, 5, 6]),
        ("All nums2 larger", [1, 2, 3, 0, 0, 0], 3, [4, 5, 6], 3, [1, 2, 3, 4, 5, 6]),
        ("Interleaved", [1, 3, 5, 0, 0, 0], 3, [2, 4, 6], 3, [1, 2, 3, 4, 5, 6]),
        ("With duplicates", [1, 2, 2, 0, 0, 0], 3, [2, 2, 2], 3, [1, 2, 2, 2, 2, 2]),
        ("Single each", [1, 0], 1, [2], 1, [1, 2]),
    ]
    for n, n1, m, n2, nn, exp in cases:
        a = n1.copy(); b = n2.copy()
        sol.merge(a, m, b, nn)
        test(n, a, exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
