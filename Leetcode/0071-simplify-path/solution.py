# ============================================================
# 0071. Simplify Path
# https://leetcode.com/problems/simplify-path/
# Difficulty: Medium
# Tags: String, Stack
# ============================================================


class Solution:
    def simplifyPath(self, path: str) -> str:
        """
        Optimal Solution (Split + Stack).
        Time:  O(n)
        Space: O(n)
        """
        stack = []
        for p in path.split('/'):
            if p == '' or p == '.':
                continue
            if p == '..':
                if stack: stack.pop()
                continue
            stack.append(p)
        return '/' + '/'.join(stack)

    def simplifyPathManual(self, path: str) -> str:
        stack = []
        i, n = 0, len(path)
        while i < n:
            while i < n and path[i] == '/': i += 1
            j = i
            while j < n and path[j] != '/': j += 1
            seg = path[i:j]
            i = j
            if seg == '' or seg == '.': continue
            if seg == '..':
                if stack: stack.pop()
                continue
            stack.append(seg)
        return '/' + '/'.join(stack)


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0
    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else: print(f"FAIL: {name} got={got!r}"); failed += 1

    cases = [
        ("Trailing slash", "/home/", "/home"),
        ("Up from root", "/../", "/"),
        ("Double slash", "/home//foo/", "/home/foo"),
        ("Mixed", "/a/./b/../../c/", "/c"),
        ("Just root", "/", "/"),
        ("Many ups", "/a/b/c/../../../", "/"),
        ("Hidden file", "/.hidden/file", "/.hidden/file"),
        ("Three dots", "/.../", "/..."),
        ("Multiple slashes", "//", "/"),
        ("Long names", "/abc/def/", "/abc/def"),
        ("Mixed with up at start", "/../abc/", "/abc"),
        ("Trailing trailing", "/abc//", "/abc"),
    ]

    print("=== Split + Stack ===")
    for n, p, exp in cases: test(n, sol.simplifyPath(p), exp)
    print("\n=== Manual Parser ===")
    for n, p, exp in cases: test("Manual " + n, sol.simplifyPathManual(p), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
