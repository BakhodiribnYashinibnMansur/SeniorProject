package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0015. 3Sum
// https://leetcode.com/problems/3sum/
// Difficulty: Medium
// Tags: Array, Two Pointers, Sorting
// ============================================================

// threeSum — Optimal Solution (Sort + Two Pointers)
// Approach: Sort, fix one element, use Two Pointers for the remaining two
// Time:  O(n^2) — outer loop O(n) * inner two-pointer scan O(n)
// Space: O(1)   — sorting in place, output not counted
func threeSum(nums []int) [][]int {
	// 1. Sort the array — enables Two Pointers and duplicate skipping
	sort.Ints(nums)
	n := len(nums)
	result := [][]int{}

	// 2. Fix the first element (i), then find two elements that sum to -nums[i]
	for i := 0; i < n-2; i++ {
		// Skip duplicate values for the first element
		if i > 0 && nums[i] == nums[i-1] {
			continue
		}

		// Early termination: if smallest value > 0, no triplet can sum to 0
		if nums[i] > 0 {
			break
		}

		// Two Pointers for the remaining subarray
		left, right := i+1, n-1
		target := -nums[i]

		for left < right {
			sum := nums[left] + nums[right]

			if sum == target {
				// Found a valid triplet
				result = append(result, []int{nums[i], nums[left], nums[right]})

				// Skip duplicates for the second element
				for left < right && nums[left] == nums[left+1] {
					left++
				}
				// Skip duplicates for the third element
				for left < right && nums[right] == nums[right-1] {
					right--
				}

				// Move both pointers inward
				left++
				right--
			} else if sum < target {
				left++ // Need a larger sum
			} else {
				right-- // Need a smaller sum
			}
		}
	}

	return result
}

// threeSumBruteForce — Brute Force approach
// Approach: Check all possible triplets with three nested loops
// Time:  O(n^3) — three nested loops
// Space: O(n)   — set for deduplication
func threeSumBruteForce(nums []int) [][]int {
	sort.Ints(nums) // Sort to make deduplication easier
	n := len(nums)
	result := [][]int{}
	seen := map[[3]int]bool{}

	for i := 0; i < n-2; i++ {
		for j := i + 1; j < n-1; j++ {
			for k := j + 1; k < n; k++ {
				if nums[i]+nums[j]+nums[k] == 0 {
					triplet := [3]int{nums[i], nums[j], nums[k]}
					if !seen[triplet] {
						seen[triplet] = true
						result = append(result, []int{nums[i], nums[j], nums[k]})
					}
				}
			}
		}
	}

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
			for _, triplet := range r {
				sort.Ints(triplet)
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

	fmt.Println("=== Sort + Two Pointers (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1", threeSum([]int{-1, 0, 1, 2, -1, -4}), [][]int{{-1, -1, 2}, {-1, 0, 1}})

	// Test 2: LeetCode Example 2 — all zeros
	test("All zeros", threeSum([]int{0, 0, 0}), [][]int{{0, 0, 0}})

	// Test 3: LeetCode Example 3 — no triplet sums to zero
	test("No triplets [0,1,1]", threeSum([]int{0, 1, 1}), [][]int{})

	// Test 4: Empty array
	test("Empty array", threeSum([]int{}), [][]int{})

	// Test 5: All positive — impossible
	test("All positive", threeSum([]int{1, 2, 3, 4, 5}), [][]int{})

	// Test 6: All negative — impossible
	test("All negative", threeSum([]int{-5, -4, -3, -2, -1}), [][]int{})

	// Test 7: Multiple triplets with duplicates
	test("Multiple triplets", threeSum([]int{-2, 0, 1, 1, 2}), [][]int{{-2, 0, 2}, {-2, 1, 1}})

	// Test 8: Large duplicate set
	test("Many zeros", threeSum([]int{0, 0, 0, 0, 0}), [][]int{{0, 0, 0}})

	// Test 9: Two elements only
	test("Two elements", threeSum([]int{-1, 1}), [][]int{})

	fmt.Println("\n=== Brute Force ===")

	// Test same cases with brute force
	test("BF: Example 1", threeSumBruteForce([]int{-1, 0, 1, 2, -1, -4}), [][]int{{-1, -1, 2}, {-1, 0, 1}})
	test("BF: All zeros", threeSumBruteForce([]int{0, 0, 0}), [][]int{{0, 0, 0}})
	test("BF: No triplets", threeSumBruteForce([]int{0, 1, 1}), [][]int{})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
