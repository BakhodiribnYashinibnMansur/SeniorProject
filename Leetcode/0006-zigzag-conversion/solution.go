package main

import "fmt"

// ============================================================
// 0006. Zigzag Conversion
// https://leetcode.com/problems/zigzag-conversion/
// Difficulty: Medium
// Tags: String
// ============================================================

// convert — Optimal Solution (Simulate Row Traversal)
// Approach: Use numRows string builders; simulate zigzag with a direction flag.
// Time:  O(n) — single pass through the string, each character processed once
// Space: O(n) — rows slice holds all n characters distributed across numRows buffers
func convert(s string, numRows int) string {
	// Edge case: one row or string fits in one row — no zigzag needed
	if numRows == 1 || numRows >= len(s) {
		return s
	}

	// Create one byte slice per row to accumulate characters
	rows := make([][]byte, numRows)
	for i := range rows {
		rows[i] = make([]byte, 0)
	}

	curRow := 0    // which row we are currently writing into
	goingDown := false // direction: true = moving down, false = moving up

	for i := 0; i < len(s); i++ {
		// Append current character to its row
		rows[curRow] = append(rows[curRow], s[i])

		// When we hit the top row (0) or the bottom row (numRows-1),
		// reverse the direction of traversal
		if curRow == 0 || curRow == numRows-1 {
			goingDown = !goingDown
		}

		// Move to the next row based on current direction
		if goingDown {
			curRow++
		} else {
			curRow--
		}
	}

	// Concatenate all rows together to form the final result
	result := make([]byte, 0, len(s))
	for _, row := range rows {
		result = append(result, row...)
	}
	return string(result)
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected string) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %q\n  Expected: %q\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Example 1 from problem — 3 rows
	test("3 rows PAYPALISHIRING", convert("PAYPALISHIRING", 3), "PAHNAPLSIIGYIR")

	// Test 2: Example 2 from problem — 4 rows
	test("4 rows PAYPALISHIRING", convert("PAYPALISHIRING", 4), "PINALSIGYAHRPI")

	// Test 3: Single character
	test("Single char A", convert("A", 1), "A")

	// Test 4: numRows = 1 — no zigzag, return as-is
	test("numRows=1 no zigzag", convert("ABCDE", 1), "ABCDE")

	// Test 5: numRows equals string length — one char per row, column order = original
	test("numRows >= len(s)", convert("AB", 3), "AB")

	// Test 6: Two rows — odd-indexed chars go to row 1, even-indexed to row 0
	// Row 0: A C E, Row 1: B D  → "ACEBD"
	test("2 rows ABCDE", convert("ABCDE", 2), "ACEBD")

	// Test 7: Single character with numRows > 1
	test("Single char numRows=5", convert("Z", 5), "Z")

	// Test 8: Two characters, two rows
	test("2 chars 2 rows", convert("AB", 2), "AB")

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
