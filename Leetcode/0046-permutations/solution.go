package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0046. Permutations
// https://leetcode.com/problems/permutations/
// Difficulty: Medium
// Tags: Array, Backtracking
// ============================================================

// permute — Optimal Solution (Backtracking with swaps)
// Approach: Fix each element at each position via swapping
// Time:  O(n * n!) — n! permutations, O(n) to copy each
// Space: O(n) — recursion depth (excluding output)
func permute(nums []int) [][]int {
	var result [][]int
	n := len(nums)

	var backtrack func(start int)
	backtrack = func(start int) {
		// Base case: all positions are fixed
		if start == n {
			perm := make([]int, n)
			copy(perm, nums)
			result = append(result, perm)
			return
		}

		// Try placing each element at position 'start'
		for i := start; i < n; i++ {
			// Swap nums[start] and nums[i]
			nums[start], nums[i] = nums[i], nums[start]

			// Recurse on the remaining positions
			backtrack(start + 1)

			// Swap back (undo)
			nums[start], nums[i] = nums[i], nums[start]
		}
	}

	backtrack(0)
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	// Helper: sort each permutation and sort the list for comparison
	normalize := func(perms [][]int) [][]int {
		sorted := make([][]int, len(perms))
		for i, p := range perms {
			cp := make([]int, len(p))
			copy(cp, p)
			sorted[i] = cp
		}
		sort.Slice(sorted, func(i, j int) bool {
			for k := 0; k < len(sorted[i]) && k < len(sorted[j]); k++ {
				if sorted[i][k] != sorted[j][k] {
					return sorted[i][k] < sorted[j][k]
				}
			}
			return len(sorted[i]) < len(sorted[j])
		})
		return sorted
	}

	test := func(name string, got, expected [][]int) {
		gotN := normalize(got)
		expN := normalize(expected)
		if reflect.DeepEqual(gotN, expN) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	testCount := func(name string, got [][]int, expectedCount int) {
		unique := make(map[[6]int]bool)
		for _, p := range got {
			var key [6]int
			for i, v := range p {
				key[i] = v
			}
			unique[key] = true
		}
		if len(got) == expectedCount && len(unique) == expectedCount {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s — got %d permutations, expected %d\n", name, len(got), expectedCount)
			failed++
		}
	}

	// Test 1: Basic case — 3 elements
	test("Basic [1,2,3]",
		permute([]int{1, 2, 3}),
		[][]int{{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}})

	// Test 2: Two elements
	test("Two elements [0,1]",
		permute([]int{0, 1}),
		[][]int{{0, 1}, {1, 0}})

	// Test 3: Single element
	test("Single element [1]",
		permute([]int{1}),
		[][]int{{1}})

	// Test 4: Negative numbers
	test("Negative numbers [-1,0,1]",
		permute([]int{-1, 0, 1}),
		[][]int{{-1, 0, 1}, {-1, 1, 0}, {0, -1, 1}, {0, 1, -1}, {1, -1, 0}, {1, 0, -1}})

	// Test 5: Four elements — count check
	testCount("Four elements [1,2,3,4] — 24 permutations",
		permute([]int{1, 2, 3, 4}), 24)

	// Test 6: Maximum length — 6 elements
	testCount("Max length [1,2,3,4,5,6] — 720 permutations",
		permute([]int{1, 2, 3, 4, 5, 6}), 720)

	// Test 7: Mixed positive/negative
	test("Mixed [-10,10]",
		permute([]int{-10, 10}),
		[][]int{{-10, 10}, {10, -10}})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
