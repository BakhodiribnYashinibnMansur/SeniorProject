package main

import "fmt"

// ============================================================
// 0013. Roman to Integer
// https://leetcode.com/problems/roman-to-integer/
// Difficulty: Easy
// Tags: Hash Table, Math, String
// ============================================================

// romanToInt1 — Left-to-Right with Subtraction Rule
// Approach: If current value < next value, subtract; otherwise add
// Time:  O(n) — single pass through the string
// Space: O(1) — fixed-size map with 7 entries
func romanToInt1(s string) int {
	// Roman numeral values
	romanMap := map[byte]int{
		'I': 1,
		'V': 5,
		'X': 10,
		'L': 50,
		'C': 100,
		'D': 500,
		'M': 1000,
	}

	result := 0
	n := len(s)

	for i := 0; i < n; i++ {
		// If current value is less than next value → subtraction case
		// e.g., IV = -1 + 5 = 4, IX = -1 + 10 = 9
		if i+1 < n && romanMap[s[i]] < romanMap[s[i+1]] {
			result -= romanMap[s[i]]
		} else {
			result += romanMap[s[i]]
		}
	}

	return result
}

// romanToInt2 — Right-to-Left
// Approach: Process from right to left; if current < previous, subtract
// Time:  O(n) — single pass through the string (reversed)
// Space: O(1) — fixed-size map with 7 entries
func romanToInt2(s string) int {
	// Roman numeral values
	romanMap := map[byte]int{
		'I': 1,
		'V': 5,
		'X': 10,
		'L': 50,
		'C': 100,
		'D': 500,
		'M': 1000,
	}

	result := 0
	prev := 0

	// Traverse from right to left
	for i := len(s) - 1; i >= 0; i-- {
		curr := romanMap[s[i]]

		// If current value is less than the previous (right neighbor),
		// it means subtraction: e.g., in "IV", I < V → subtract 1
		if curr < prev {
			result -= curr
		} else {
			result += curr
		}

		prev = curr
	}

	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %d\n  Expected: %d\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Approach 1: Left-to-Right with Subtraction Rule ===")

	// Test 1: Basic — single character
	test("Single character I", romanToInt1("I"), 1)

	// Test 2: Simple addition
	test("Simple addition III", romanToInt1("III"), 3)

	// Test 3: Subtraction case IV
	test("Subtraction IV", romanToInt1("IV"), 4)

	// Test 4: Subtraction case IX
	test("Subtraction IX", romanToInt1("IX"), 9)

	// Test 5: Mixed — LVIII
	test("Mixed LVIII", romanToInt1("LVIII"), 58)

	// Test 6: Complex — MCMXCIV
	test("Complex MCMXCIV", romanToInt1("MCMXCIV"), 1994)

	// Test 7: Large — MMMCMXCIX (3999)
	test("Max MMMCMXCIX", romanToInt1("MMMCMXCIX"), 3999)

	// Test 8: All subtraction pairs — CDXLIV
	test("Subtraction pairs CDXLIV", romanToInt1("CDXLIV"), 444)

	// Test 9: Single large — M
	test("Single M", romanToInt1("M"), 1000)

	fmt.Println()
	fmt.Println("=== Approach 2: Right-to-Left ===")

	// Test 1: Basic — single character
	test("Single character I", romanToInt2("I"), 1)

	// Test 2: Simple addition
	test("Simple addition III", romanToInt2("III"), 3)

	// Test 3: Subtraction case IV
	test("Subtraction IV", romanToInt2("IV"), 4)

	// Test 4: Subtraction case IX
	test("Subtraction IX", romanToInt2("IX"), 9)

	// Test 5: Mixed — LVIII
	test("Mixed LVIII", romanToInt2("LVIII"), 58)

	// Test 6: Complex — MCMXCIV
	test("Complex MCMXCIV", romanToInt2("MCMXCIV"), 1994)

	// Test 7: Large — MMMCMXCIX (3999)
	test("Max MMMCMXCIX", romanToInt2("MMMCMXCIX"), 3999)

	// Test 8: All subtraction pairs — CDXLIV
	test("Subtraction pairs CDXLIV", romanToInt2("CDXLIV"), 444)

	// Test 9: Single large — M
	test("Single M", romanToInt2("M"), 1000)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
