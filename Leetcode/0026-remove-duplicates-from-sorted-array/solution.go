package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0026. Remove Duplicates from Sorted Array
// https://leetcode.com/problems/remove-duplicates-from-sorted-array/
// Difficulty: Easy
// Tags: Array, Two Pointers
// ============================================================

// removeDuplicates — Optimal Solution (Two Pointers)
// Approach: slow pointer tracks unique position, fast pointer scans
// Time:  O(n) — single pass through the array
// Space: O(1) — only two pointer variables
func removeDuplicates(nums []int) int {
	if len(nums) == 0 {
		return 0
	}

	// slow points to the last unique element
	slow := 0

	for fast := 1; fast < len(nums); fast++ {
		// Found a new unique element
		if nums[fast] != nums[slow] {
			slow++
			nums[slow] = nums[fast]
		}
	}

	// slow is 0-indexed, so count = slow + 1
	return slow + 1
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, nums []int, expectedK int, expectedNums []int) {
		numsCopy := make([]int, len(nums))
		copy(numsCopy, nums)
		gotK := removeDuplicates(numsCopy)
		gotNums := numsCopy[:gotK]
		if gotK == expectedK && reflect.DeepEqual(gotNums, expectedNums) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      k=%d, nums=%v\n  Expected: k=%d, nums=%v\n",
				name, gotK, gotNums, expectedK, expectedNums)
			failed++
		}
	}

	// Test 1: Basic case with one duplicate
	test("Basic case", []int{1, 1, 2}, 2, []int{1, 2})

	// Test 2: Multiple duplicates
	test("Multiple duplicates", []int{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}, 5, []int{0, 1, 2, 3, 4})

	// Test 3: Single element
	test("Single element", []int{1}, 1, []int{1})

	// Test 4: All same elements
	test("All same elements", []int{1, 1, 1, 1}, 1, []int{1})

	// Test 5: No duplicates
	test("No duplicates", []int{1, 2, 3, 4, 5}, 5, []int{1, 2, 3, 4, 5})

	// Test 6: Two elements — same
	test("Two elements same", []int{1, 1}, 1, []int{1})

	// Test 7: Two elements — different
	test("Two elements different", []int{1, 2}, 2, []int{1, 2})

	// Test 8: Negative numbers
	test("Negative numbers", []int{-3, -1, 0, 0, 2}, 4, []int{-3, -1, 0, 2})

	// Test 9: Large consecutive duplicates
	test("Large consecutive duplicates", []int{0, 0, 0, 0, 1, 1, 1, 2}, 3, []int{0, 1, 2})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
