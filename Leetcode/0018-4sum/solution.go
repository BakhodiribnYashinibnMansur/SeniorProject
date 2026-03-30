package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0018. 4Sum
// https://leetcode.com/problems/4sum/
// Difficulty: Medium
// Tags: Array, Two Pointers, Sorting
// ============================================================

// fourSum — Optimal Solution (Sort + Two Pointers)
// Approach: Sort, fix two elements (i, j), use Two Pointers for the remaining two
// Time:  O(n^3) — two outer loops O(n^2) * inner two-pointer scan O(n)
// Space: O(1)   — sorting in place, output not counted
func fourSum(nums []int, target int) [][]int {
	// 1. Sort the array — enables Two Pointers and duplicate skipping
	sort.Ints(nums)
	n := len(nums)
	result := [][]int{}

	// 2. Fix the first element (i)
	for i := 0; i < n-3; i++ {
		// Skip duplicate values for the first element
		if i > 0 && nums[i] == nums[i-1] {
			continue
		}

		// Early termination: if smallest possible sum > target, no quadruplet possible
		if int64(nums[i])+int64(nums[i+1])+int64(nums[i+2])+int64(nums[i+3]) > int64(target) {
			break
		}

		// Skip: if largest possible sum with nums[i] < target, try next i
		if int64(nums[i])+int64(nums[n-3])+int64(nums[n-2])+int64(nums[n-1]) < int64(target) {
			continue
		}

		// 3. Fix the second element (j)
		for j := i + 1; j < n-2; j++ {
			// Skip duplicate values for the second element
			if j > i+1 && nums[j] == nums[j-1] {
				continue
			}

			// Early termination for j
			if int64(nums[i])+int64(nums[j])+int64(nums[j+1])+int64(nums[j+2]) > int64(target) {
				break
			}

			// Skip for j
			if int64(nums[i])+int64(nums[j])+int64(nums[n-2])+int64(nums[n-1]) < int64(target) {
				continue
			}

			// 4. Two Pointers for the remaining subarray
			left, right := j+1, n-1
			remain := int64(target) - int64(nums[i]) - int64(nums[j])

			for left < right {
				sum := int64(nums[left]) + int64(nums[right])

				if sum == remain {
					// Found a valid quadruplet
					result = append(result, []int{nums[i], nums[j], nums[left], nums[right]})

					// Skip duplicates for the third element
					for left < right && nums[left] == nums[left+1] {
						left++
					}
					// Skip duplicates for the fourth element
					for left < right && nums[right] == nums[right-1] {
						right--
					}

					// Move both pointers inward
					left++
					right--
				} else if sum < remain {
					left++ // Need a larger sum
				} else {
					right-- // Need a smaller sum
				}
			}
		}
	}

	return result
}

// fourSumBruteForce — Brute Force approach
// Approach: Check all possible quadruplets with four nested loops
// Time:  O(n^4) — four nested loops
// Space: O(n)   — set for deduplication
func fourSumBruteForce(nums []int, target int) [][]int {
	sort.Ints(nums) // Sort to make deduplication easier
	n := len(nums)
	result := [][]int{}
	seen := map[[4]int]bool{}

	for i := 0; i < n-3; i++ {
		for j := i + 1; j < n-2; j++ {
			for k := j + 1; k < n-1; k++ {
				for l := k + 1; l < n; l++ {
					if int64(nums[i])+int64(nums[j])+int64(nums[k])+int64(nums[l]) == int64(target) {
						quad := [4]int{nums[i], nums[j], nums[k], nums[l]}
						if !seen[quad] {
							seen[quad] = true
							result = append(result, []int{nums[i], nums[j], nums[k], nums[l]})
						}
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
			for _, quad := range r {
				sort.Ints(quad)
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
	test("Example 1", fourSum([]int{1, 0, -1, 0, -2, 2}, 0), [][]int{{-2, -1, 1, 2}, {-2, 0, 0, 2}, {-1, 0, 0, 1}})

	// Test 2: LeetCode Example 2
	test("Example 2", fourSum([]int{2, 2, 2, 2, 2}, 8), [][]int{{2, 2, 2, 2}})

	// Test 3: Empty result — no quadruplet sums to target
	test("No quadruplets", fourSum([]int{1, 2, 3, 4, 5}, 100), [][]int{})

	// Test 4: Negative target
	test("Negative target", fourSum([]int{-3, -2, -1, 0, 0, 1, 2, 3}, -1), [][]int{{-3, -1, 0, 3}, {-3, -1, 1, 2}, {-3, 0, 0, 2}, {-2, -1, 0, 2}, {-2, 0, 0, 1}, {-3, -2, 1, 3}})

	// Test 5: All zeros
	test("All zeros target 0", fourSum([]int{0, 0, 0, 0}, 0), [][]int{{0, 0, 0, 0}})

	// Test 6: Less than 4 elements
	test("Less than 4 elements", fourSum([]int{1, 2, 3}, 6), [][]int{})

	// Test 7: Large values — overflow check
	test("Large values", fourSum([]int{1000000000, 1000000000, 1000000000, 1000000000}, -294967296), [][]int{})

	// Test 8: Two quadruplets
	test("Two quadruplets", fourSum([]int{-1, 0, 1, 2, -1, -4}, -1), [][]int{{-4, 0, 1, 2}, {-1, -1, 0, 1}})

	// Test 9: Empty array
	test("Empty array", fourSum([]int{}, 0), [][]int{})

	fmt.Println("\n=== Brute Force ===")

	// Test same cases with brute force
	test("BF: Example 1", fourSumBruteForce([]int{1, 0, -1, 0, -2, 2}, 0), [][]int{{-2, -1, 1, 2}, {-2, 0, 0, 2}, {-1, 0, 0, 1}})
	test("BF: Example 2", fourSumBruteForce([]int{2, 2, 2, 2, 2}, 8), [][]int{{2, 2, 2, 2}})
	test("BF: All zeros", fourSumBruteForce([]int{0, 0, 0, 0}, 0), [][]int{{0, 0, 0, 0}})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
