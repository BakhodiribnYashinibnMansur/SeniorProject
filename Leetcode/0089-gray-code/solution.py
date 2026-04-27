from typing import List


class Solution:
    def grayCode(self, n: int) -> List[int]:
        """Time O(2^n), Space O(2^n)."""
        return [i ^ (i >> 1) for i in range(1 << n)]


def validate(seq, n):
    if len(seq) != (1 << n): return False
    if seq[0] != 0: return False
    seen = set()
    for v in seq:
        if v < 0 or v >= (1 << n) or v in seen: return False
        seen.add(v)
    for i in range(len(seq)):
        j = (i + 1) % len(seq)
        if bin(seq[i] ^ seq[j]).count('1') != 1: return False
    return True


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    for n in [1, 2, 3, 4, 5, 8]:
        seq = sol.grayCode(n)
        if validate(seq, n):
            print(f"PASS: n={n}, len={len(seq)}"); passed += 1
        else:
            print(f"FAIL: n={n}, seq={seq}"); failed += 1
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
