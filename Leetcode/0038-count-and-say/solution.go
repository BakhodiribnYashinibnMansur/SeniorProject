package main

import (
	"fmt"
	"strconv"
	"strings"
)

// ============================================================
// 0038. Count and Say
// https://leetcode.com/problems/count-and-say/
// Difficulty: Medium
// Tags: String
// ============================================================

// countAndSay — Optimal Solution (Iterative)
// Approach: Build each term by performing RLE on the previous term
// Time:  O(n * L) — n iterations, each processing string of length L
// Space: O(L) — store the current and next strings
func countAndSay(n int) string {
	result := "1"

	for step := 2; step <= n; step++ {
		var next strings.Builder
		i := 0

		for i < len(result) {
			digit := result[i]
			count := 1

			// Count consecutive identical digits
			for i+count < len(result) && result[i+count] == digit {
				count++
			}

			// Append count and digit
			next.WriteString(strconv.Itoa(count))
			next.WriteByte(digit)
			i += count
		}

		result = next.String()
	}

	return result
}

// countAndSayRecursive — Recursive approach
// Approach: Base case n=1 returns "1", otherwise RLE of countAndSay(n-1)
// Time:  O(n * L) — n recursive calls
// Space: O(n * L) — recursion stack + strings
func countAndSayRecursive(n int) string {
	// Base case
	if n == 1 {
		return "1"
	}

	// Recursively get the previous term
	prev := countAndSayRecursive(n - 1)

	// Perform RLE on the previous term
	var result strings.Builder
	i := 0

	for i < len(prev) {
		digit := prev[i]
		count := 1

		// Count consecutive identical digits
		for i+count < len(prev) && prev[i+count] == digit {
			count++
		}

		// Append count and digit
		result.WriteString(strconv.Itoa(count))
		result.WriteByte(digit)
		i += count
	}

	return result.String()
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

	fmt.Println("=== Iterative (Optimal) ===")

	// Test 1: Base case
	test("n=1", countAndSay(1), "1")

	// Test 2: One 1
	test("n=2", countAndSay(2), "11")

	// Test 3: Two 1s
	test("n=3", countAndSay(3), "21")

	// Test 4: LeetCode Example
	test("n=4", countAndSay(4), "1211")

	// Test 5: Multiple runs
	test("n=5", countAndSay(5), "111221")

	// Test 6: Longer sequence
	test("n=6", countAndSay(6), "312211")

	// Test 7: Even longer
	test("n=7", countAndSay(7), "13112221")

	// Test 8: n=8
	test("n=8", countAndSay(8), "1113213211")

	// Test 9: n=10
	test("n=10", countAndSay(10), "13211311123113112211")

	fmt.Println("\n=== Recursive ===")

	// Test 10: Recursive — Base case
	test("Recursive n=1", countAndSayRecursive(1), "1")

	// Test 11: Recursive — n=4
	test("Recursive n=4", countAndSayRecursive(4), "1211")

	// Test 12: Recursive — n=6
	test("Recursive n=6", countAndSayRecursive(6), "312211")

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
