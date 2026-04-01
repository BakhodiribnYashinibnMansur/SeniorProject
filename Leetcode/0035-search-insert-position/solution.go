package main

import "fmt"

// ============================================================
// 0035. Search Insert Position
// https://leetcode.com/problems/search-insert-position/
// Difficulty: Easy
// Tags: Array, Binary Search
// ============================================================

// searchInsert — Optimal Solution (Binary Search)
// Approach: Binary search for target; if not found, left pointer = insert position
// Time:  O(log n) — halves the search space each step
// Space: O(1) — only three variables: left, right, mid
func searchInsert(nums []int, target int) int {
	left, right := 0, len(nums)-1

	for left <= right {
		// Avoid integer overflow
		mid := left + (right-left)/2

		if nums[mid] == target {
			return mid
		} else if nums[mid] < target {
			left = mid + 1
		} else {
			right = mid - 1
		}
	}

	// left is the correct insert position
	return left
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
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Binary Search (Optimal) ===")

	// Test 1: LeetCode Example 1 — target found
	test("Example 1", searchInsert([]int{1, 3, 5, 6}, 5), 2)

	// Test 2: LeetCode Example 2 — insert in middle
	test("Example 2", searchInsert([]int{1, 3, 5, 6}, 2), 1)

	// Test 3: LeetCode Example 3 — insert after all
	test("Example 3", searchInsert([]int{1, 3, 5, 6}, 7), 4)

	// Test 4: LeetCode Example 4 — insert before all
	test("Example 4", searchInsert([]int{1, 3, 5, 6}, 0), 0)

	// Test 5: Single element — found
	test("Single element found", searchInsert([]int{1}, 1), 0)

	// Test 6: Single element — insert before
	test("Single element insert before", searchInsert([]int{5}, 3), 0)

	// Test 7: Single element — insert after
	test("Single element insert after", searchInsert([]int{5}, 8), 1)

	// Test 8: Target at first index
	test("Target at start", searchInsert([]int{1, 3, 5, 7, 9}, 1), 0)

	// Test 9: Target at last index
	test("Target at end", searchInsert([]int{1, 3, 5, 7, 9}, 9), 4)

	// Test 10: Insert between middle elements
	test("Insert in middle", searchInsert([]int{1, 3, 5, 7, 9}, 4), 2)

	// Test 11: Two elements — insert between
	test("Two elements insert", searchInsert([]int{1, 3}, 2), 1)

	// Test 12: Two elements — found
	test("Two elements found", searchInsert([]int{1, 3}, 3), 1)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
