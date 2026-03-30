package main

import (
	"fmt"
	"math"
	"sort"
)

// ============================================================
// 0016. 3Sum Closest
// https://leetcode.com/problems/3sum-closest/
// Difficulty: Medium
// Tags: Array, Two Pointers, Sorting
// ============================================================

// threeSumClosest — Optimal Solution (Sort + Two Pointers)
// Approach: Sort the array, fix one element, use two pointers for the remaining two
// Time:  O(n^2) — one loop * two-pointer scan
// Space: O(log n) — sorting space (or O(n) depending on sort implementation)
func threeSumClosest(nums []int, target int) int {
	sort.Ints(nums)
	n := len(nums)
	closest := nums[0] + nums[1] + nums[2]

	for i := 0; i < n-2; i++ {
		// Skip duplicate values for i to avoid redundant work
		if i > 0 && nums[i] == nums[i-1] {
			continue
		}

		left, right := i+1, n-1

		for left < right {
			sum := nums[i] + nums[left] + nums[right]

			// If exact match, return immediately
			if sum == target {
				return target
			}

			// Update closest if current sum is nearer to target
			if abs(sum-target) < abs(closest-target) {
				closest = sum
			}

			// Move pointers based on comparison with target
			if sum < target {
				left++
			} else {
				right--
			}
		}
	}

	return closest
}

// threeSumClosestBruteForce — Brute Force approach
// Approach: Check all triplets and track the closest sum
// Time:  O(n^3) — three nested loops
// Space: O(1) — no extra memory
func threeSumClosestBruteForce(nums []int, target int) int {
	n := len(nums)
	closest := nums[0] + nums[1] + nums[2]

	for i := 0; i < n-2; i++ {
		for j := i + 1; j < n-1; j++ {
			for k := j + 1; k < n; k++ {
				sum := nums[i] + nums[j] + nums[k]

				// Update closest if current sum is nearer to target
				if abs(sum-target) < abs(closest-target) {
					closest = sum
				}
			}
		}
	}

	return closest
}

// abs returns the absolute value of an integer
func abs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Silence the math import (used conceptually for abs)
	_ = math.MaxInt32

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

	fmt.Println("=== Sort + Two Pointers (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1", threeSumClosest([]int{-1, 2, 1, -4}, 1), 2)

	// Test 2: LeetCode Example 2
	test("Example 2", threeSumClosest([]int{0, 0, 0}, 1), 0)

	// Test 3: Exact match exists
	test("Exact match", threeSumClosest([]int{1, 1, 1, 0}, 3), 3)

	// Test 4: All negative numbers
	test("All negatives", threeSumClosest([]int{-3, -2, -5, -1}, -8), -8)

	// Test 5: Large positive target
	test("Large target", threeSumClosest([]int{1, 2, 3, 4, 5}, 100), 12)

	// Test 6: Negative target
	test("Negative target", threeSumClosest([]int{-1, 0, 1, 1, 55}, -3), 0)

	// Test 7: Minimum length array
	test("Min length", threeSumClosest([]int{1, 1, 1}, 2), 3)

	// Test 8: Mixed positive and negative
	test("Mixed values", threeSumClosest([]int{-10, -4, -1, 0, 3, 7, 11}, 5), 4)

	// Test 9: Duplicates in array
	test("Duplicates", threeSumClosest([]int{1, 1, 1, 1}, 3), 3)

	fmt.Println("\n=== Brute Force ===")

	// Test 10: Brute Force — Example 1
	test("BF Example 1", threeSumClosestBruteForce([]int{-1, 2, 1, -4}, 1), 2)

	// Test 11: Brute Force — Example 2
	test("BF Example 2", threeSumClosestBruteForce([]int{0, 0, 0}, 1), 0)

	// Test 12: Brute Force — Exact match
	test("BF Exact match", threeSumClosestBruteForce([]int{1, 1, 1, 0}, 3), 3)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
