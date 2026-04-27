from typing import List


class Solution:
    def restoreIpAddresses(self, s: str) -> List[str]:
        """Time O(1), Space O(1)."""
        result = []
        def bt(start: int, parts: List[str]):
            if len(parts) == 4:
                if start == len(s):
                    result.append('.'.join(parts))
                return
            remaining = len(s) - start
            need = 4 - len(parts)
            if remaining < need or remaining > need * 3:
                return
            for length in range(1, 4):
                if start + length > len(s): break
                seg = s[start:start + length]
                if (len(seg) > 1 and seg[0] == '0') or int(seg) > 255: continue
                bt(start + length, parts + [seg])
        bt(0, [])
        return result


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, exp):
        global passed, failed
        if sorted(got) == sorted(exp): print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got}"); failed += 1
    cases = [
        ("Example 1", "25525511135", ["255.255.11.135", "255.255.111.35"]),
        ("Example 2", "0000", ["0.0.0.0"]),
        ("Example 3", "101023",
            ["1.0.10.23", "1.0.102.3", "10.1.0.23", "10.10.2.3", "101.0.2.3"]),
        ("Too short", "111", []),
        ("Too long", "1111111111111", []),
        ("Min", "1111", ["1.1.1.1"]),
    ]
    for n, s, exp in cases: test(n, sol.restoreIpAddresses(s), exp)
    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
