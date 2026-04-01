package main

import "fmt"

// ============================================================
// 0041. First Missing Positive
// https://leetcode.com/problems/first-missing-positive/
// Difficulty: Hard
// Tags: Array, Hash Table
// ============================================================

// firstMissingPositive — Optimal Solution (Cyclic Sort / Index Mapping)
// Approach: Place each number x at index x-1, then scan
// Time:  O(n) — each element is swapped at most once
// Space: O(1) — in-place, no extra memory
func firstMissingPositive(nums []int) int {
	n := len(nums)

	// Phase 1: Place each number at its correct index
	// Number x should be at index x-1
	for i := 0; i < n; i++ {
		for nums[i] >= 1 && nums[i] <= n && nums[nums[i]-1] != nums[i] {
			// Swap nums[i] to its correct position
			nums[i], nums[nums[i]-1] = nums[nums[i]-1], nums[i]
		}
	}

	// Phase 2: Find the first missing positive
	// The first index i where nums[i] != i+1 gives the answer
	for i := 0; i < n; i++ {
		if nums[i] != i+1 {
			return i + 1
		}
	}

	// All 1..n are present, answer is n+1
	return n + 1
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, nums []int, expected int) {
		got := firstMissingPositive(nums)
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %d\n  Expected: %d\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Basic case — missing 3
	test("Basic case [1,2,0]", []int{1, 2, 0}, 3)

	// Test 2: Missing in the middle
	test("Missing 2 [3,4,-1,1]", []int{3, 4, -1, 1}, 2)

	// Test 3: Missing 1
	test("Missing 1 [7,8,9,11,12]", []int{7, 8, 9, 11, 12}, 1)

	// Test 4: Single element — missing 1
	test("Single [2]", []int{2}, 1)

	// Test 5: Single element — has 1
	test("Single [1]", []int{1}, 2)

	// Test 6: All negative
	test("All negative [-1,-2,-3]", []int{-1, -2, -3}, 1)

	// Test 7: Consecutive from 1 — answer is n+1
	test("Consecutive [1,2,3,4,5]", []int{1, 2, 3, 4, 5}, 6)

	// Test 8: Duplicates
	test("Duplicates [1,1,1]", []int{1, 1, 1}, 2)

	// Test 9: Contains zero
	test("Contains zero [0,1,2]", []int{0, 1, 2}, 3)

	// Test 10: Unsorted complete
	test("Unsorted [3,1,2]", []int{3, 1, 2}, 4)

	// Test 11: Large gap
	test("Large gap [1,1000]", []int{1, 1000}, 2)

	// Test 12: Mixed negatives and positives
	test("Mixed [-1,4,2,1,9,10]", []int{-1, 4, 2, 1, 9, 10}, 3)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
