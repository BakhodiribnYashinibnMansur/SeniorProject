package main

import "fmt"

// ============================================================
// 0043. Multiply Strings
// https://leetcode.com/problems/multiply-strings/
// Difficulty: Medium
// Tags: Math, String, Simulation
// ============================================================

// multiply — Optimal Solution (Grade School Multiplication)
// Approach: Multiply digit by digit, accumulate at correct positions
// Time:  O(m*n) — multiply every digit pair
// Space: O(m+n) — result array of size m+n
func multiply(num1 string, num2 string) string {
	// Edge case: anything times zero is zero
	if num1 == "0" || num2 == "0" {
		return "0"
	}

	m, n := len(num1), len(num2)
	// Product of m-digit and n-digit numbers has at most m+n digits
	result := make([]int, m+n)

	// Multiply each digit pair and accumulate at correct positions
	for i := m - 1; i >= 0; i-- {
		for j := n - 1; j >= 0; j-- {
			mul := int(num1[i]-'0') * int(num2[j]-'0')
			p1, p2 := i+j, i+j+1 // p1=tens position, p2=ones position

			// Add product to the ones position and propagate carry
			sum := mul + result[p2]
			result[p2] = sum % 10
			result[p1] += sum / 10
		}
	}

	// Build result string, skip leading zeros
	var sb []byte
	for _, d := range result {
		if len(sb) == 0 && d == 0 {
			continue
		}
		sb = append(sb, byte(d)+'0')
	}

	if len(sb) == 0 {
		return "0"
	}
	return string(sb)
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected string) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Grade School Multiplication (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1", multiply("2", "3"), "6")

	// Test 2: LeetCode Example 2
	test("Example 2", multiply("123", "456"), "56088")

	// Test 3: Zero times number
	test("Zero * number", multiply("0", "12345"), "0")

	// Test 4: Number times zero
	test("Number * zero", multiply("12345", "0"), "0")

	// Test 5: Both zeros
	test("Zero * zero", multiply("0", "0"), "0")

	// Test 6: Single digit multiplication
	test("Single digits", multiply("9", "9"), "81")

	// Test 7: One times number (identity)
	test("Identity", multiply("1", "999"), "999")

	// Test 8: Large carry propagation
	test("Large carry", multiply("999", "999"), "998001")

	// Test 9: Different lengths
	test("Different lengths", multiply("12", "3456"), "41472")

	// Test 10: Result with internal zeros
	test("Internal zeros", multiply("100", "100"), "10000")

	// Test 11: Large numbers
	test("Large numbers", multiply("123456789", "987654321"), "121932631112635269")

	// Test 12: Power of 10
	test("Power of 10", multiply("10", "10"), "100")

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
