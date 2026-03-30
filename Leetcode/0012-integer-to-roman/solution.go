package main

import "fmt"

// ============================================================
// 0012. Integer to Roman
// https://leetcode.com/problems/integer-to-roman/
// Difficulty: Medium
// Tags: Hash Table, Math, String
// ============================================================

// intToRoman — Approach 1: Greedy with Value Table
// Approach: Use a lookup table of values and symbols sorted descending.
//           Greedily subtract the largest possible value and append its symbol.
// Time:  O(1) — bounded by the finite set of roman numeral symbols (at most ~15 iterations)
// Space: O(1) — the result string length is bounded (max "MMMCMXCIX" = 15 chars)
func intToRoman(num int) string {
	// Value-symbol pairs in descending order
	// Includes subtractive forms (e.g., 900=CM, 400=CD, etc.)
	values := []int{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1}
	symbols := []string{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"}

	result := ""
	for i, val := range values {
		// While the current value fits into num, append symbol
		for num >= val {
			result += symbols[i]
			num -= val
		}
	}

	return result
}

// intToRomanDigitMap — Approach 2: Hardcoded Digit Mapping
// Approach: Predefine roman representations for each digit at each place value.
//           Extract thousands, hundreds, tens, ones and look up each.
// Time:  O(1) — always exactly 4 lookups
// Space: O(1) — lookup tables are constant size
func intToRomanDigitMap(num int) string {
	thousands := []string{"", "M", "MM", "MMM"}
	hundreds := []string{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"}
	tens := []string{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"}
	ones := []string{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"}

	return thousands[num/1000] + hundreds[(num%1000)/100] + tens[(num%100)/10] + ones[num%10]
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected string) {
		if got == expected {
			fmt.Printf("  PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("  FAIL: %s\n  Got:      %q\n  Expected: %q\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Approach 1: Greedy with Value Table ===")

	// Test 1: LeetCode example 1
	test("Example 1 (3749)", intToRoman(3749), "MMMDCCXLIX")

	// Test 2: LeetCode example 2
	test("Example 2 (58)", intToRoman(58), "LVIII")

	// Test 3: LeetCode example 3
	test("Example 3 (1994)", intToRoman(1994), "MCMXCIV")

	// Test 4: Minimum value
	test("Minimum (1)", intToRoman(1), "I")

	// Test 5: Maximum value
	test("Maximum (3999)", intToRoman(3999), "MMMCMXCIX")

	// Test 6: All subtractive forms
	test("Subtractive (944)", intToRoman(944), "CMXLIV")

	// Test 7: Round thousand
	test("Round thousand (2000)", intToRoman(2000), "MM")

	// Test 8: Single symbols
	test("Single symbol (500)", intToRoman(500), "D")

	// Test 9: Repeating symbol
	test("Repeating (3)", intToRoman(3), "III")

	fmt.Println("\n=== Approach 2: Hardcoded Digit Mapping ===")

	// Verify Approach 2 matches Approach 1 on all test cases
	test("Digit Map (3749)", intToRomanDigitMap(3749), "MMMDCCXLIX")
	test("Digit Map (58)", intToRomanDigitMap(58), "LVIII")
	test("Digit Map (1994)", intToRomanDigitMap(1994), "MCMXCIV")
	test("Digit Map (1)", intToRomanDigitMap(1), "I")
	test("Digit Map (3999)", intToRomanDigitMap(3999), "MMMCMXCIX")
	test("Digit Map (944)", intToRomanDigitMap(944), "CMXLIV")
	test("Digit Map (2000)", intToRomanDigitMap(2000), "MM")
	test("Digit Map (500)", intToRomanDigitMap(500), "D")
	test("Digit Map (3)", intToRomanDigitMap(3), "III")

	// Results
	fmt.Printf("\n  Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
