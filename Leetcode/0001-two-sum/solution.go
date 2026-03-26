package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0001. Two Sum
// https://leetcode.com/problems/two-sum/
// Difficulty: Easy
// Tags: Array, Hash Table
// ============================================================

// twoSum — Optimal Solution (One-pass Hash Map)
// Approach: Look up complement in Hash Map
// Time:  O(n) — single pass through the array
// Space: O(n) — Hash Map stores at most n elements
func twoSum(nums []int, target int) []int {
	// Hash Map: value → index
	// For each element, check if its complement (target - num)
	// has been seen before
	seen := make(map[int]int)

	for i, num := range nums {
		// Calculate complement
		complement := target - num

		// Is complement in the Hash Map?
		if j, ok := seen[complement]; ok {
			// Found! complement's index is j, current index is i
			return []int{j, i}
		}

		// Add current element to Hash Map
		// It will serve as complement for future elements
		seen[num] = i
	}

	// Per constraints, a solution always exists
	// This line is never reached
	return nil
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected []int) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Basic case — found in first pair
	test("Basic case", twoSum([]int{2, 7, 11, 15}, 9), []int{0, 1})

	// Test 2: Found in the middle
	test("Found in middle", twoSum([]int{3, 2, 4}, 6), []int{1, 2})

	// Test 3: Duplicate values
	test("Duplicate values", twoSum([]int{3, 3}, 6), []int{0, 1})

	// Test 4: Negative numbers
	test("Negative numbers", twoSum([]int{-1, -2, -3, -4, -5}, -8), []int{2, 4})

	// Test 5: Mixed numbers (negative + positive)
	test("Mixed numbers", twoSum([]int{-3, 4, 3, 90}, 0), []int{0, 2})

	// Test 6: Zero values
	test("Zero values", twoSum([]int{0, 4, 3, 0}, 0), []int{0, 3})

	// Test 7: Large values
	test("Large values", twoSum([]int{1000000000, -1000000000}, 0), []int{0, 1})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
