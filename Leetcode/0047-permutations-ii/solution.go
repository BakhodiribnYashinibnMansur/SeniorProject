package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0047. Permutations II
// https://leetcode.com/problems/permutations-ii/
// Difficulty: Medium
// Tags: Array, Backtracking
// ============================================================

// permuteUnique — Optimal Solution (Backtracking with Sorting + Duplicate Skipping)
// Approach: Sort array, use backtracking with used[] array, skip duplicates
// Time:  O(n * n!) — at most n! permutations, O(n) to copy each
// Space: O(n)      — recursion depth + used array + path
func permuteUnique(nums []int) [][]int {
	// 1. Sort to group duplicates together
	sort.Ints(nums)
	result := [][]int{}
	used := make([]bool, len(nums))
	path := []int{}

	var backtrack func()
	backtrack = func() {
		// Base case: permutation is complete
		if len(path) == len(nums) {
			tmp := make([]int, len(path))
			copy(tmp, path)
			result = append(result, tmp)
			return
		}

		for i := 0; i < len(nums); i++ {
			// Skip if already used in current permutation
			if used[i] {
				continue
			}

			// Skip duplicate: same value as previous, and previous was not used
			// (meaning previous was backtracked at this level — duplicate branch)
			if i > 0 && nums[i] == nums[i-1] && !used[i-1] {
				continue
			}

			// Place nums[i] and recurse
			used[i] = true
			path = append(path, nums[i])
			backtrack()
			path = path[:len(path)-1]
			used[i] = false
		}
	}

	backtrack()
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, got, expected [][]int) {
		// Compare as sets of permutations
		toSet := func(r [][]int) map[[8]int]bool {
			set := map[[8]int]bool{}
			for _, perm := range r {
				var key [8]int
				key[0] = len(perm)
				for j, v := range perm {
					key[j+1] = v
				}
				set[key] = true
			}
			return set
		}

		gotSet := toSet(got)
		expSet := toSet(expected)

		if reflect.DeepEqual(gotSet, expSet) && len(got) == len(expected) {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Backtracking with Sorting + Duplicate Skipping ===")

	// Test 1: LeetCode Example 1 — duplicates
	test("Example 1: [1,1,2]",
		permuteUnique([]int{1, 1, 2}),
		[][]int{{1, 1, 2}, {1, 2, 1}, {2, 1, 1}})

	// Test 2: LeetCode Example 2 — all unique
	test("Example 2: [1,2,3]",
		permuteUnique([]int{1, 2, 3}),
		[][]int{{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}})

	// Test 3: All same elements
	test("All same [1,1,1]",
		permuteUnique([]int{1, 1, 1}),
		[][]int{{1, 1, 1}})

	// Test 4: Single element
	test("Single [0]",
		permuteUnique([]int{0}),
		[][]int{{0}})

	// Test 5: Two same elements
	test("Two same [1,1]",
		permuteUnique([]int{1, 1}),
		[][]int{{1, 1}})

	// Test 6: Two different elements
	test("Two different [1,2]",
		permuteUnique([]int{1, 2}),
		[][]int{{1, 2}, {2, 1}})

	// Test 7: Negative numbers with duplicates
	test("Negatives [-1,1,1]",
		permuteUnique([]int{-1, 1, 1}),
		[][]int{{-1, 1, 1}, {1, -1, 1}, {1, 1, -1}})

	// Test 8: Multiple duplicate groups
	test("Two pairs [1,1,2,2]",
		permuteUnique([]int{1, 1, 2, 2}),
		[][]int{{1, 1, 2, 2}, {1, 2, 1, 2}, {1, 2, 2, 1},
			{2, 1, 1, 2}, {2, 1, 2, 1}, {2, 2, 1, 1}})

	// Test 9: Larger input
	test("Four elements [1,1,1,2]",
		permuteUnique([]int{1, 1, 1, 2}),
		[][]int{{1, 1, 1, 2}, {1, 1, 2, 1}, {1, 2, 1, 1}, {2, 1, 1, 1}})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
