package main

import "fmt"

// ============================================================
// 0031. Next Permutation
// https://leetcode.com/problems/next-permutation/
// Difficulty: Medium
// Tags: Array, Two Pointers
// ============================================================

// nextPermutation — Optimal Solution (Next Permutation Algorithm)
// Approach: Find pivot, swap with next larger, reverse suffix
// Time:  O(n) — at most 3 linear scans
// Space: O(1) — in-place swaps only
func nextPermutation(nums []int) {
	n := len(nums)

	// Step 1: Find the pivot — rightmost i where nums[i] < nums[i+1]
	i := n - 2
	for i >= 0 && nums[i] >= nums[i+1] {
		i--
	}

	// Step 2 & 3: Find the swap target and swap
	if i >= 0 {
		j := n - 1
		for nums[j] <= nums[i] {
			j--
		}
		nums[i], nums[j] = nums[j], nums[i]
	}

	// Step 4: Reverse the suffix after the pivot
	left, right := i+1, n-1
	for left < right {
		nums[left], nums[right] = nums[right], nums[left]
		left++
		right--
	}
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, nums, expected []int) {
		nextPermutation(nums)
		if equal(nums, expected) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, nums, expected)
			failed++
		}
	}

	fmt.Println("=== Next Permutation Algorithm ===")

	// Test 1: LeetCode Example 1
	test("Example 1", []int{1, 2, 3}, []int{1, 3, 2})

	// Test 2: LeetCode Example 2 — last permutation wraps around
	test("Example 2", []int{3, 2, 1}, []int{1, 2, 3})

	// Test 3: LeetCode Example 3 — duplicates
	test("Example 3", []int{1, 1, 5}, []int{1, 5, 1})

	// Test 4: Single element
	test("Single element", []int{1}, []int{1})

	// Test 5: Two elements ascending
	test("Two elements ascending", []int{1, 2}, []int{2, 1})

	// Test 6: Two elements descending
	test("Two elements descending", []int{2, 1}, []int{1, 2})

	// Test 7: All same elements
	test("All same elements", []int{2, 2, 2}, []int{2, 2, 2})

	// Test 8: Pivot at first position
	test("Pivot at first", []int{1, 5, 4, 3, 2}, []int{2, 1, 3, 4, 5})

	// Test 9: Longer array
	test("Longer array", []int{1, 3, 5, 4, 2}, []int{1, 4, 2, 3, 5})

	// Test 10: Middle pivot
	test("Middle pivot", []int{2, 3, 1}, []int{3, 1, 2})

	// Test 11: Duplicates in suffix
	test("Duplicates in suffix", []int{1, 3, 2, 2}, []int{2, 1, 2, 3})

	// Test 12: Already near last
	test("Near last", []int{5, 4, 7, 5, 3, 2}, []int{5, 5, 2, 3, 4, 7})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}

func equal(a, b []int) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if a[i] != b[i] {
			return false
		}
	}
	return true
}
