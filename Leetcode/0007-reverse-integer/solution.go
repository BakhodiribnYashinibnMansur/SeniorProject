package main

import (
	"fmt"
	"math"
)

// ============================================================
// 0007. Reverse Integer
// https://leetcode.com/problems/reverse-integer/
// Difficulty: Medium
// Tags: Math
// ============================================================

// reverse — Optimal Solution (Mathematical Digit Pop)
// Approach: Pop digits one by one using modulo; check overflow before each push.
// Time:  O(log x) — number of digits in x (at most 10 for a 32-bit integer)
// Space: O(1)    — only a few integer variables, no extra data structures
func reverse(x int) int {
	rev := 0

	for x != 0 {
		// Pop the last digit
		digit := x % 10
		x /= 10

		// Overflow check BEFORE pushing the digit onto rev
		// INT_MAX =  2147483647, last digit 7 → rev must be < INT_MAX/10,
		//            or rev == INT_MAX/10 and digit <= 7
		// INT_MIN = -2147483648, last digit -8 → rev must be > INT_MIN/10,
		//            or rev == INT_MIN/10 and digit >= -8
		if rev > math.MaxInt32/10 || (rev == math.MaxInt32/10 && digit > 7) {
			return 0
		}
		if rev < math.MinInt32/10 || (rev == math.MinInt32/10 && digit < -8) {
			return 0
		}

		// Push digit onto the reversed number
		rev = rev*10 + digit
	}

	return rev
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
	test("Positive 123", reverse(123), 321)

	// Test 2: Negative number
	test("Negative -123", reverse(-123), -321)

	// Test 3: Trailing zero is dropped
	test("Trailing zero 120", reverse(120), 21)

	// Test 4: Single digit
	test("Single digit 5", reverse(5), 5)

	// Test 5: Zero
	test("Zero", reverse(0), 0)

	// Test 6: Overflow — reversed value exceeds INT_MAX (2147483647)
	// 1534236469 reversed = 9646324351 > 2^31 - 1
	test("Overflow positive", reverse(1534236469), 0)

	// Test 7: Overflow — reversed value below INT_MIN (-2147483648)
	// -1534236469 reversed = -9646324351 < -2^31
	test("Overflow negative", reverse(-1534236469), 0)

	// Test 8: INT_MAX itself reversed
	// math.MaxInt32 = 2147483647, reversed = 7463847412 → overflow
	test("INT_MAX reversed overflows", reverse(math.MaxInt32), 0)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
