package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0040. Combination Sum II
// https://leetcode.com/problems/combination-sum-ii/
// Difficulty: Medium
// Tags: Array, Backtracking
// ============================================================

// combinationSum2 — Optimal Solution (Backtracking with Duplicate Skipping)
// Approach: Sort candidates, backtrack with pruning and duplicate skipping
// Time:  O(2^n) — worst case all subsets explored; much less with pruning
// Space: O(n)   — recursion depth + current path
func combinationSum2(candidates []int, target int) [][]int {
	// 1. Sort candidates — enables pruning and duplicate skipping
	sort.Ints(candidates)
	result := [][]int{}
	current := []int{}

	var backtrack func(start, remaining int)
	backtrack = func(start, remaining int) {
		// Base case: found a valid combination
		if remaining == 0 {
			combo := make([]int, len(current))
			copy(combo, current)
			result = append(result, combo)
			return
		}

		for i := start; i < len(candidates); i++ {
			// Pruning: if current candidate exceeds remaining, all subsequent will too
			if candidates[i] > remaining {
				break
			}

			// Skip duplicates at the same recursion level
			if i > start && candidates[i] == candidates[i-1] {
				continue
			}

			// Choose candidates[i]
			current = append(current, candidates[i])

			// Recurse: move to i+1 (each element used at most once)
			backtrack(i+1, remaining-candidates[i])

			// Undo choice (backtrack)
			current = current[:len(current)-1]
		}
	}

	backtrack(0, target)
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected [][]int) {
		// Sort inner slices and outer slice for consistent comparison
		sortResult := func(r [][]int) {
			for _, combo := range r {
				sort.Ints(combo)
			}
			sort.Slice(r, func(a, b int) bool {
				for k := 0; k < len(r[a]) && k < len(r[b]); k++ {
					if r[a][k] != r[b][k] {
						return r[a][k] < r[b][k]
					}
				}
				return len(r[a]) < len(r[b])
			})
		}
		sortResult(got)
		sortResult(expected)

		if reflect.DeepEqual(got, expected) {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Backtracking with Duplicate Skipping (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1",
		combinationSum2([]int{10, 1, 2, 7, 6, 1, 5}, 8),
		[][]int{{1, 1, 6}, {1, 2, 5}, {1, 7}, {2, 6}})

	// Test 2: LeetCode Example 2
	test("Example 2",
		combinationSum2([]int{2, 5, 2, 1, 2}, 5),
		[][]int{{1, 2, 2}, {5}})

	// Test 3: Single element match
	test("Single element match",
		combinationSum2([]int{1}, 1),
		[][]int{{1}})

	// Test 4: Single element no match
	test("Single element no match",
		combinationSum2([]int{2}, 1),
		[][]int{})

	// Test 5: All same elements
	test("All same elements",
		combinationSum2([]int{1, 1, 1, 1, 1}, 3),
		[][]int{{1, 1, 1}})

	// Test 6: No valid combination
	test("No valid combination",
		combinationSum2([]int{3, 5, 7}, 1),
		[][]int{})

	// Test 7: Exact target with all elements
	test("Use all elements",
		combinationSum2([]int{1, 2, 3}, 6),
		[][]int{{1, 2, 3}})

	// Test 8: Multiple duplicate values
	test("Multiple duplicates",
		combinationSum2([]int{1, 1, 1, 2, 2}, 4),
		[][]int{{1, 1, 2}, {2, 2}})

	// Test 9: Large single value equals target
	test("Large single match",
		combinationSum2([]int{8, 7, 4, 3}, 7),
		[][]int{{3, 4}, {7}})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
