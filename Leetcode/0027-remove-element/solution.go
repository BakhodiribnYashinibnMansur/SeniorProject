package main

import (
	"fmt"
	"sort"
)

// ============================================================
// 0027. Remove Element
// https://leetcode.com/problems/remove-element/
// Difficulty: Easy
// Tags: Array, Two Pointers
// ============================================================

// removeElement — Optimal Solution (Two Pointers — Opposite Direction)
// Approach: Swap val elements with end elements
// Time:  O(n) — each element visited at most once
// Space: O(1) — only two pointer variables
func removeElement(nums []int, val int) int {
	left := 0
	right := len(nums) - 1

	for left <= right {
		if nums[left] == val {
			// Replace with last element, shrink from right
			nums[left] = nums[right]
			right--
			// Do NOT advance left — swapped element needs checking
		} else {
			left++
		}
	}

	return left
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, nums []int, val int, expectedK int, expectedElems []int) {
		// Make a copy to preserve original
		numsCopy := make([]int, len(nums))
		copy(numsCopy, nums)

		k := removeElement(numsCopy, val)
		result := make([]int, k)
		copy(result, numsCopy[:k])
		sort.Ints(result)

		expected := make([]int, len(expectedElems))
		copy(expected, expectedElems)
		sort.Ints(expected)

		ok := k == expectedK && len(result) == len(expected)
		if ok {
			for i := range result {
				if result[i] != expected[i] {
					ok = false
					break
				}
			}
		}

		if ok {
			fmt.Printf("✅ PASS: %s → k=%d, nums[:k]=%v\n", name, k, numsCopy[:k])
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      k=%d, nums[:k]=%v\n  Expected: k=%d, elements=%v\n",
				name, k, numsCopy[:k], expectedK, expectedElems)
			failed++
		}
	}

	// Test 1: Basic case — remove 3s
	test("Basic [3,2,2,3] val=3",
		[]int{3, 2, 2, 3}, 3, 2, []int{2, 2})

	// Test 2: Multiple removals
	test("Multiple [0,1,2,2,3,0,4,2] val=2",
		[]int{0, 1, 2, 2, 3, 0, 4, 2}, 2, 5, []int{0, 1, 3, 0, 4})

	// Test 3: Empty array
	test("Empty array",
		[]int{}, 1, 0, []int{})

	// Test 4: All elements equal val
	test("All same [3,3,3] val=3",
		[]int{3, 3, 3}, 3, 0, []int{})

	// Test 5: No elements equal val
	test("None match [1,2,3] val=4",
		[]int{1, 2, 3}, 4, 3, []int{1, 2, 3})

	// Test 6: Single element (keep)
	test("Single keep [1] val=2",
		[]int{1}, 2, 1, []int{1})

	// Test 7: Single element (remove)
	test("Single remove [1] val=1",
		[]int{1}, 1, 0, []int{})

	// Test 8: Val at beginning
	test("Val at start [3,1,2] val=3",
		[]int{3, 1, 2}, 3, 2, []int{1, 2})

	// Test 9: Val at end
	test("Val at end [1,2,3] val=3",
		[]int{1, 2, 3}, 3, 2, []int{1, 2})

	// Test 10: All same, not val
	test("All same not val [2,2,2] val=3",
		[]int{2, 2, 2}, 3, 3, []int{2, 2, 2})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
