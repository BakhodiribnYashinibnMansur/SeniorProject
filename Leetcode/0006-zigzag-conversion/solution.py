# ============================================================
# 0006. Zigzag Conversion
# https://leetcode.com/problems/zigzag-conversion/
# Difficulty: Medium
# Tags: String
# ============================================================


class Solution:
    def convert(self, s: str, numRows: int) -> str:
        """
        Optimal Solution (Simulate Row Traversal)
        Approach: Use numRows lists; simulate zigzag with a direction flag.
        Time:  O(n) — single pass through the string
        Space: O(n) — rows lists hold all n characters distributed across rows
        """
        # Edge case: one row or string fits in one row — no zigzag needed
        if numRows == 1 or numRows >= len(s):
            return s

        # One list per row to accumulate characters
        rows = [[] for _ in range(numRows)]

        cur_row = 0       # which row we are currently writing into
        going_down = False  # direction flag

        for ch in s:
            # Append current character to its row
            rows[cur_row].append(ch)

            # Reverse direction at the top or bottom row
            if cur_row == 0 or cur_row == numRows - 1:
                going_down = not going_down

            # Move to the next row based on current direction
            cur_row += 1 if going_down else -1

        # Concatenate all rows to form the result
        return "".join(ch for row in rows for ch in row)


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got!r}")
            print(f"  Expected: {expected!r}")
            failed += 1

    # Test 1: Example 1 from problem — 3 rows
    test("3 rows PAYPALISHIRING", sol.convert("PAYPALISHIRING", 3), "PAHNAPLSIIGYIR")

    # Test 2: Example 2 from problem — 4 rows
    test("4 rows PAYPALISHIRING", sol.convert("PAYPALISHIRING", 4), "PINALSIGYAHRPI")

    # Test 3: Single character
    test("Single char A", sol.convert("A", 1), "A")

    # Test 4: numRows = 1 — no zigzag, return as-is
    test("numRows=1 no zigzag", sol.convert("ABCDE", 1), "ABCDE")

    # Test 5: numRows >= string length — each char on its own row
    test("numRows >= len(s)", sol.convert("AB", 3), "AB")

    # Test 6: Two rows — Row 0: A C E, Row 1: B D → "ACEBD"
    test("2 rows ABCDE", sol.convert("ABCDE", 2), "ACEBD")

    # Test 7: Single character with numRows > 1
    test("Single char numRows=5", sol.convert("Z", 5), "Z")

    # Test 8: Two characters, two rows
    test("2 chars 2 rows", sol.convert("AB", 2), "AB")

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
