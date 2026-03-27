package main

import (
	"fmt"
	"math"
)

// ============================================================
// 0008. String to Integer (atoi)
// https://leetcode.com/problems/string-to-integer-atoi/
// Difficulty: Medium
// Tags: String, Simulation
// ============================================================

// myAtoi — Optimal Solution (Single-pass Simulation)
// Time:  O(n) — single pass through the string characters
// Space: O(1) — only a few integer variables used
func myAtoi(s string) int {
	i := 0
	n := len(s)

	// Step 1: Skip leading whitespace
	for i < n && s[i] == ' ' {
		i++
	}

	// Step 2: Determine sign
	sign := 1
	if i < n && (s[i] == '+' || s[i] == '-') {
		if s[i] == '-' {
			sign = -1
		}
		i++
	}

	// Step 3: Read digits and build result
	result := 0
	for i < n && s[i] >= '0' && s[i] <= '9' {
		digit := int(s[i] - '0')

		// Step 4: Check for overflow BEFORE updating result
		// If result > INT_MAX/10, the next multiply will overflow
		// If result == INT_MAX/10 and digit > 7, it will overflow
		if result > math.MaxInt32/10 || (result == math.MaxInt32/10 && digit > 7) {
			if sign == 1 {
				return math.MaxInt32 // 2147483647
			}
			return math.MinInt32 // -2147483648
		}

		result = result*10 + digit
		i++
	}

	return sign * result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Basic positive number
	test("Basic positive", myAtoi("42"), 42)

	// Test 2: Leading whitespace and negative sign
	test("Leading spaces, negative", myAtoi("   -42"), -42)

	// Test 3: Digits followed by letters — stop at non-digit
	test("Digits then words", myAtoi("4193 with words"), 4193)

	// Test 4: Leading letters — no digits found, return 0
	test("Words then digits", myAtoi("words and 987"), 0)

	// Test 5: Overflow negative — clamp to INT_MIN
	test("Overflow negative clamp", myAtoi("-91283472332"), -2147483648)

	// Test 6: Overflow positive — clamp to INT_MAX
	test("Overflow positive clamp", myAtoi("9999999999"), 2147483647)

	// Test 7: Explicit plus sign
	test("Explicit plus sign", myAtoi("+1"), 1)

	// Test 8: Empty string
	test("Empty string", myAtoi(""), 0)

	// Test 9: Only whitespace
	test("Only whitespace", myAtoi("   "), 0)

	// Test 10: Zero
	test("Zero", myAtoi("0"), 0)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
