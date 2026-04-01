package main

import (
	"fmt"
	"sort"
	"strings"
)

// ============================================================
// 0039. Combination Sum
// https://leetcode.com/problems/combination-sum/
// Difficulty: Medium
// Tags: Array, Backtracking
// ============================================================

// combinationSum — Optimal Solution (Backtracking with Pruning)
// Approach: Sort candidates, then recursively build combinations.
//
//	Use a start index to avoid duplicate combinations.
//	Reuse candidates by recursing with same index (not i+1).
//	Prune when candidate exceeds remaining target.
//
// Time:  O(n^(T/M)) — n candidates, T target, M min candidate
// Space: O(T/M)     — max recursion depth
func combinationSum(candidates []int, target int) [][]int {
	sort.Ints(candidates)
	result := [][]int{}

	var backtrack func(start, remaining int, current []int)
	backtrack = func(start, remaining int, current []int) {
		// Base case: found a valid combination
		if remaining == 0 {
			combo := make([]int, len(current))
			copy(combo, current)
			result = append(result, combo)
			return
		}

		for i := start; i < len(candidates); i++ {
			// Pruning: sorted, so all subsequent candidates are also too large
			if candidates[i] > remaining {
				break
			}
			current = append(current, candidates[i])
			backtrack(i, remaining-candidates[i], current) // i, not i+1: allow reuse
			current = current[:len(current)-1]             // undo choice (backtrack)
		}
	}

	backtrack(0, target, []int{})
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, got, expected [][]int) {
		// Sort inner slices and outer slice for comparison
		sortNested := func(arr [][]int) []string {
			strs := []string{}
			for _, inner := range arr {
				sorted := make([]int, len(inner))
				copy(sorted, inner)
				sort.Ints(sorted)
				parts := []string{}
				for _, v := range sorted {
					parts = append(parts, fmt.Sprintf("%d", v))
				}
				strs = append(strs, strings.Join(parts, ","))
			}
			sort.Strings(strs)
			return strs
		}

		gotStr := strings.Join(sortNested(got), "|")
		expStr := strings.Join(sortNested(expected), "|")

		if gotStr == expStr {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: LeetCode Example 1
	test("Example 1: [2,3,6,7] target=7",
		combinationSum([]int{2, 3, 6, 7}, 7),
		[][]int{{2, 2, 3}, {7}})

	// Test 2: LeetCode Example 2
	test("Example 2: [2,3,5] target=8",
		combinationSum([]int{2, 3, 5}, 8),
		[][]int{{2, 2, 2, 2}, {2, 3, 3}, {3, 5}})

	// Test 3: LeetCode Example 3 — no valid combination
	test("Example 3: [2] target=1",
		combinationSum([]int{2}, 1),
		[][]int{})

	// Test 4: Single candidate equals target
	test("Single candidate = target: [7] target=7",
		combinationSum([]int{7}, 7),
		[][]int{{7}})

	// Test 5: Single candidate used multiple times
	test("Single candidate reuse: [3] target=9",
		combinationSum([]int{3}, 9),
		[][]int{{3, 3, 3}})

	// Test 6: All candidates too large
	test("All too large: [5,6,7] target=3",
		combinationSum([]int{5, 6, 7}, 3),
		[][]int{})

	// Test 7: Target equals smallest candidate
	test("Target = smallest: [2,3,5] target=2",
		combinationSum([]int{2, 3, 5}, 2),
		[][]int{{2}})

	// Test 8: Multiple valid combinations
	test("Multiple combos: [2,3,5] target=10",
		combinationSum([]int{2, 3, 5}, 10),
		[][]int{{2, 2, 2, 2, 2}, {2, 2, 3, 3}, {2, 3, 5}, {5, 5}})

	// Test 9: Larger target
	test("Larger: [2,3,6,7] target=13",
		combinationSum([]int{2, 3, 6, 7}, 13),
		[][]int{{2, 2, 2, 2, 2, 3}, {2, 2, 2, 7}, {2, 2, 3, 3, 3}, {2, 2, 3, 6}, {3, 3, 7}, {6, 7}})

	// Test 10: Single element, not divisible
	test("Not divisible: [3] target=7",
		combinationSum([]int{3}, 7),
		[][]int{})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
