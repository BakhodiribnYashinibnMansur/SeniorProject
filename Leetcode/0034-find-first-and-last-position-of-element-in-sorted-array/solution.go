package main

import "fmt"

// ============================================================
// 0034. Find First and Last Position of Element in Sorted Array
// https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
// Difficulty: Medium
// Tags: Array, Binary Search
// ============================================================

// searchRange — Optimal Solution (Two Binary Searches)
// Approach: Find left bound then right bound using modified binary search
// Time:  O(log n) — two binary searches, each O(log n)
// Space: O(1) — only a few variables
func searchRange(nums []int, target int) []int {
	left := findLeft(nums, target)
	if left == -1 {
		return []int{-1, -1}
	}
	right := findRight(nums, target)
	return []int{left, right}
}

// findLeft finds the first (leftmost) occurrence of target
func findLeft(nums []int, target int) int {
	lo, hi := 0, len(nums)-1
	result := -1

	for lo <= hi {
		mid := lo + (hi-lo)/2
		if nums[mid] == target {
			result = mid
			hi = mid - 1 // keep searching left
		} else if nums[mid] < target {
			lo = mid + 1
		} else {
			hi = mid - 1
		}
	}

	return result
}

// findRight finds the last (rightmost) occurrence of target
func findRight(nums []int, target int) int {
	lo, hi := 0, len(nums)-1
	result := -1

	for lo <= hi {
		mid := lo + (hi-lo)/2
		if nums[mid] == target {
			result = mid
			lo = mid + 1 // keep searching right
		} else if nums[mid] < target {
			lo = mid + 1
		} else {
			hi = mid - 1
		}
	}

	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []int) {
		if len(got) == len(expected) && got[0] == expected[0] && got[1] == expected[1] {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Two Binary Searches (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1", searchRange([]int{5, 7, 7, 8, 8, 10}, 8), []int{3, 4})

	// Test 2: LeetCode Example 2
	test("Example 2", searchRange([]int{5, 7, 7, 8, 8, 10}, 6), []int{-1, -1})

	// Test 3: LeetCode Example 3
	test("Example 3 (empty)", searchRange([]int{}, 0), []int{-1, -1})

	// Test 4: Single element found
	test("Single element found", searchRange([]int{1}, 1), []int{0, 0})

	// Test 5: Single element not found
	test("Single element not found", searchRange([]int{1}, 2), []int{-1, -1})

	// Test 6: All same elements
	test("All same elements", searchRange([]int{8, 8, 8, 8, 8}, 8), []int{0, 4})

	// Test 7: Target at the beginning
	test("Target at start", searchRange([]int{1, 1, 2, 3, 4}, 1), []int{0, 1})

	// Test 8: Target at the end
	test("Target at end", searchRange([]int{1, 2, 3, 4, 4}, 4), []int{3, 4})

	// Test 9: Target smaller than all
	test("Target smaller than all", searchRange([]int{2, 3, 4}, 1), []int{-1, -1})

	// Test 10: Target larger than all
	test("Target larger than all", searchRange([]int{2, 3, 4}, 5), []int{-1, -1})

	// Test 11: Single occurrence in the middle
	test("Single occurrence", searchRange([]int{1, 2, 3, 4, 5}, 3), []int{2, 2})

	// Test 12: Large run of duplicates
	test("Large duplicates", searchRange([]int{1, 2, 2, 2, 2, 2, 3}, 2), []int{1, 5})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
