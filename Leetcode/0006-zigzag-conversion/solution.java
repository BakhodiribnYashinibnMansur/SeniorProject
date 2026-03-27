/**
 * 0006. Zigzag Conversion
 * https://leetcode.com/problems/zigzag-conversion/
 * Difficulty: Medium
 * Tags: String
 */
class Solution {

    /**
     * Optimal Solution (Simulate Row Traversal)
     * Approach: Use numRows StringBuilders; simulate zigzag with a direction flag.
     * Time:  O(n) — single pass through the string
     * Space: O(n) — StringBuilders hold all n characters distributed across rows
     */
    public String convert(String s, int numRows) {
        // Edge case: one row or string fits in one row — no zigzag needed
        if (numRows == 1 || numRows >= s.length()) {
            return s;
        }

        // One StringBuilder per row to accumulate characters
        StringBuilder[] rows = new StringBuilder[numRows];
        for (int i = 0; i < numRows; i++) {
            rows[i] = new StringBuilder();
        }

        int curRow = 0;       // which row we are currently writing into
        boolean goingDown = false; // direction flag

        for (char c : s.toCharArray()) {
            // Append current character to its row
            rows[curRow].append(c);

            // Reverse direction at the top or bottom row
            if (curRow == 0 || curRow == numRows - 1) {
                goingDown = !goingDown;
            }

            // Move to the next row
            curRow += goingDown ? 1 : -1;
        }

        // Concatenate all rows to form the result
        StringBuilder result = new StringBuilder();
        for (StringBuilder row : rows) {
            result.append(row);
        }
        return result.toString();
    }

    // ============================================================
    // Test Cases
    // ============================================================

    static int passed = 0, failed = 0;

    static void test(String name, String got, String expected) {
        if (got.equals(expected)) {
            System.out.printf("✅ PASS: %s%n", name);
            passed++;
        } else {
            System.out.printf("❌ FAIL: %s%n  Got:      %s%n  Expected: %s%n",
                name, got, expected);
            failed++;
        }
    }

    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test 1: Example 1 from problem — 3 rows
        test("3 rows PAYPALISHIRING", sol.convert("PAYPALISHIRING", 3), "PAHNAPLSIIGYIR");

        // Test 2: Example 2 from problem — 4 rows
        test("4 rows PAYPALISHIRING", sol.convert("PAYPALISHIRING", 4), "PINALSIGYAHRPI");

        // Test 3: Single character
        test("Single char A", sol.convert("A", 1), "A");

        // Test 4: numRows = 1 — no zigzag, return as-is
        test("numRows=1 no zigzag", sol.convert("ABCDE", 1), "ABCDE");

        // Test 5: numRows >= string length — each char on its own row
        test("numRows >= len(s)", sol.convert("AB", 3), "AB");

        // Test 6: Two rows — Row 0: A C E, Row 1: B D → "ACEBD"
        test("2 rows ABCDE", sol.convert("ABCDE", 2), "ACEBD");

        // Test 7: Single character with numRows > 1
        test("Single char numRows=5", sol.convert("Z", 5), "Z");

        // Test 8: Two characters, two rows
        test("2 chars 2 rows", sol.convert("AB", 2), "AB");

        // Results
        System.out.printf("%n📊 Results: %d passed, %d failed, %d total%n",
            passed, failed, passed + failed);
    }
}
