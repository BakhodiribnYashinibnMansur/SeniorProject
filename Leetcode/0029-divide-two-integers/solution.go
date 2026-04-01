package main

import (
	"fmt"
	"math"
)

// ============================================================
// 0029. Divide Two Integers
// https://leetcode.com/problems/divide-two-integers/
// Difficulty: Medium
// Tags: Math, Bit Manipulation
// ============================================================

// divide — Optimal Solution (Exponential Search / Bit Shifting)
// Approach: Double the divisor using left shifts to subtract large chunks
// Time:  O(log^2 n) — outer loop O(log n), inner doubling O(log n)
// Space: O(1) — only a few variables
func divide(dividend int, divisor int) int {
	// Edge case: overflow when -2^31 / -1 = 2^31 > INT_MAX
	if dividend == math.MinInt32 && divisor == -1 {
		return math.MaxInt32
	}

	// Determine the sign of the result
	negative := (dividend < 0) != (divisor < 0)

	// Work with absolute values (use int64 to handle -2^31)
	a := int64(dividend)
	if a < 0 {
		a = -a
	}
	b := int64(divisor)
	if b < 0 {
		b = -b
	}

	quotient := int64(0)

	// Exponential search: double the divisor each time
	for a >= b {
		temp := b
		multiple := int64(1)
		// Double temp until it would exceed a
		for a >= temp<<1 {
			temp <<= 1
			multiple <<= 1
		}
		// Subtract the largest chunk and add to quotient
		a -= temp
		quotient += multiple
	}

	// Apply sign
	if negative {
		quotient = -quotient
	}

	// Clamp to 32-bit range
	if quotient > math.MaxInt32 {
		return math.MaxInt32
	}
	if quotient < math.MinInt32 {
		return math.MinInt32
	}
	return int(quotient)
}

// divideRepeatedSubtraction — Brute Force (Repeated Subtraction) — TLE but educational
// Approach: Subtract divisor from dividend one at a time
// Time:  O(dividend / divisor) — up to 2^31 subtractions
// Space: O(1)
func divideRepeatedSubtraction(dividend int, divisor int) int {
	// Edge case: overflow
	if dividend == math.MinInt32 && divisor == -1 {
		return math.MaxInt32
	}

	negative := (dividend < 0) != (divisor < 0)

	a := int64(dividend)
	if a < 0 {
		a = -a
	}
	b := int64(divisor)
	if b < 0 {
		b = -b
	}

	quotient := int64(0)
	for a >= b {
		a -= b
		quotient++
	}

	if negative {
		quotient = -quotient
	}
	return int(quotient)
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

	fmt.Println("=== Exponential Search (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1: 10 / 3", divide(10, 3), 3)

	// Test 2: LeetCode Example 2
	test("Example 2: 7 / -2", divide(7, -2), -3)

	// Test 3: Overflow edge case
	test("Overflow: -2^31 / -1", divide(-2147483648, -1), 2147483647)

	// Test 4: Dividend = 0
	test("Zero dividend: 0 / 5", divide(0, 5), 0)

	// Test 5: Divisor = 1
	test("Divisor 1: 100 / 1", divide(100, 1), 100)

	// Test 6: Divisor = -1
	test("Divisor -1: 100 / -1", divide(100, -1), -100)

	// Test 7: Both negative
	test("Both negative: -7 / -2", divide(-7, -2), 3)

	// Test 8: Dividend < divisor
	test("Small dividend: 1 / 3", divide(1, 3), 0)

	// Test 9: Equal values
	test("Equal values: 7 / 7", divide(7, 7), 1)

	// Test 10: Large dividend, divisor = 1
	test("MIN_INT / 1", divide(-2147483648, 1), -2147483648)

	// Test 11: Large dividend, divisor = 2
	test("MIN_INT / 2", divide(-2147483648, 2), -1073741824)

	// Test 12: Negative dividend, positive divisor
	test("-10 / 3", divide(-10, 3), -3)

	fmt.Println("\n=== Repeated Subtraction (Brute Force) ===")

	// Test 13: Brute Force — Example 1
	test("BF: 10 / 3", divideRepeatedSubtraction(10, 3), 3)

	// Test 14: Brute Force — Example 2
	test("BF: 7 / -2", divideRepeatedSubtraction(7, -2), -3)

	// Test 15: Brute Force — Overflow
	test("BF: -2^31 / -1", divideRepeatedSubtraction(-2147483648, -1), 2147483647)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
