package main

import (
	"fmt"
	"math"
	"sort"
)

// ============================================================
// 0004. Median of Two Sorted Arrays
// https://leetcode.com/problems/median-of-two-sorted-arrays/
// Difficulty: Hard
// Tags: Array, Binary Search, Divide and Conquer
// ============================================================

// findMedianSortedArrays — Optimal Solution (Binary Search on Smaller Array)
// Approach: Partition both arrays so that left half has (m+n+1)/2 elements.
//
//	Binary search on the smaller array to find the correct partition.
//	At the right partition: maxLeft1 <= minRight2 AND maxLeft2 <= minRight1.
//
// Time:  O(log(min(m,n))) — binary search on the shorter array only
// Space: O(1)             — no extra data structures, only a few variables
func findMedianSortedArrays(nums1 []int, nums2 []int) float64 {
	// Always binary-search the smaller array for efficiency
	if len(nums1) > len(nums2) {
		nums1, nums2 = nums2, nums1
	}

	m, n := len(nums1), len(nums2)
	// half is the total number of elements in the left partition
	half := (m + n + 1) / 2

	// Binary search boundaries for the partition index in nums1
	lo, hi := 0, m

	for lo <= hi {
		// i = number of elements taken from nums1 for the left partition
		i := (lo + hi) / 2
		// j = remaining elements needed from nums2
		j := half - i

		// Determine boundary values, using -Inf/+Inf for out-of-bounds
		maxLeft1 := math.MinInt64
		if i > 0 {
			maxLeft1 = nums1[i-1]
		}
		minRight1 := math.MaxInt64
		if i < m {
			minRight1 = nums1[i]
		}
		maxLeft2 := math.MinInt64
		if j > 0 {
			maxLeft2 = nums2[j-1]
		}
		minRight2 := math.MaxInt64
		if j < n {
			minRight2 = nums2[j]
		}

		if maxLeft1 <= minRight2 && maxLeft2 <= minRight1 {
			// Correct partition found
			if (m+n)%2 == 1 {
				// Odd total length: median is max of left half
				return float64(max(maxLeft1, maxLeft2))
			}
			// Even total length: average of max-left and min-right
			return float64(max(maxLeft1, maxLeft2)+min(minRight1, minRight2)) / 2.0
		} else if maxLeft1 > minRight2 {
			// Took too many from nums1 — move left
			hi = i - 1
		} else {
			// Took too few from nums1 — move right
			lo = i + 1
		}
	}

	return 0.0
}

// findMedianSortedArraysBrute — Brute Force (Merge and Find)
// Approach: Merge both arrays into one sorted array, then find the median.
// Time:  O((m+n)log(m+n)) — due to sort; O(m+n) if merging directly
// Space: O(m+n)           — merged array storage
func findMedianSortedArraysBrute(nums1 []int, nums2 []int) float64 {
	// Combine both arrays
	merged := append(append([]int{}, nums1...), nums2...)
	// Sort the merged array
	sort.Ints(merged)

	total := len(merged)
	if total%2 == 1 {
		// Odd: middle element
		return float64(merged[total/2])
	}
	// Even: average of two middle elements
	return float64(merged[total/2-1]+merged[total/2]) / 2.0
}

// max / min helpers (Go < 1.21 compatibility)
func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}

func min(a, b int) int {
	if a < b {
		return a
	}
	return b
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	// Compare floats with tolerance to handle floating-point precision
	const eps = 1e-5
	test := func(name string, got, expected float64) {
		diff := got - expected
		if diff < 0 {
			diff = -diff
		}
		if diff < eps {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %.5f\n  Expected: %.5f\n", name, got, expected)
			failed++
		}
	}

	// Test 1: LeetCode Example 1 — odd total length
	test("Example 1 (odd)", findMedianSortedArrays([]int{1, 3}, []int{2}), 2.00000)

	// Test 2: LeetCode Example 2 — even total length
	test("Example 2 (even)", findMedianSortedArrays([]int{1, 2}, []int{3, 4}), 2.50000)

	// Test 3: One empty array
	test("One empty array", findMedianSortedArrays([]int{}, []int{1}), 1.00000)

	// Test 4: Both single elements
	test("Both single elements", findMedianSortedArrays([]int{2}, []int{1}), 1.50000)

	// Test 5: nums1 entirely smaller than nums2
	test("nums1 all smaller", findMedianSortedArrays([]int{1, 2}, []int{3, 4, 5}), 3.00000)

	// Test 6: nums2 entirely smaller than nums1
	test("nums2 all smaller", findMedianSortedArrays([]int{3, 4, 5}, []int{1, 2}), 3.00000)

	// Test 7: Negative numbers
	test("Negative numbers", findMedianSortedArrays([]int{-5, -3, -1}, []int{-4, -2}), -3.00000)

	// Test 8: Mixed negative and positive
	test("Mixed neg/pos", findMedianSortedArrays([]int{-2, 0}, []int{1, 3}), 0.50000)

	// Test 9: Large equal arrays
	test("Equal arrays", findMedianSortedArrays([]int{1, 3}, []int{1, 3}), 2.00000)

	// Test 10: Single element arrays, even total
	test("Single elements even", findMedianSortedArrays([]int{3}, []int{4}), 3.50000)

	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
