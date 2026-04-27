package main

import "fmt"

// ============================================================
// 0053. Maximum Subarray
// https://leetcode.com/problems/maximum-subarray/
// Difficulty: Medium
// Tags: Array, Divide and Conquer, Dynamic Programming
// ============================================================

// maxSubArray — Optimal Solution (Kadane's Algorithm)
// Approach: at each index decide whether to extend the running subarray
//   or restart it from the current element. Track the global best.
// Time:  O(n) — single pass
// Space: O(1) — two scalars
func maxSubArray(nums []int) int {
	cur, best := nums[0], nums[0]
	for i := 1; i < len(nums); i++ {
		if cur+nums[i] > nums[i] {
			cur = cur + nums[i]
		} else {
			cur = nums[i]
		}
		if cur > best {
			best = cur
		}
	}
	return best
}

// maxSubArrayBrute — Naive O(n^2) double sweep
// Time:  O(n^2)
// Space: O(1)
func maxSubArrayBrute(nums []int) int {
	best := nums[0]
	for i := 0; i < len(nums); i++ {
		s := 0
		for j := i; j < len(nums); j++ {
			s += nums[j]
			if s > best {
				best = s
			}
		}
	}
	return best
}

// maxSubArrayDC — Divide and Conquer
// Time:  O(n log n)
// Space: O(log n) recursion depth
func maxSubArrayDC(nums []int) int {
	var solve func(l, r int) int
	solve = func(l, r int) int {
		if l == r {
			return nums[l]
		}
		m := (l + r) / 2
		leftMax := solve(l, m)
		rightMax := solve(m+1, r)

		bestL, sumL := nums[m], 0
		for i := m; i >= l; i-- {
			sumL += nums[i]
			if sumL > bestL {
				bestL = sumL
			}
		}
		bestR, sumR := nums[m+1], 0
		for j := m + 1; j <= r; j++ {
			sumR += nums[j]
			if sumR > bestR {
				bestR = sumR
			}
		}
		cross := bestL + bestR

		best := leftMax
		if rightMax > best {
			best = rightMax
		}
		if cross > best {
			best = cross
		}
		return best
	}
	return solve(0, len(nums)-1)
}

// maxSubArrayPrefix — Prefix sum view
// Time:  O(n)
// Space: O(1)
func maxSubArrayPrefix(nums []int) int {
	best := nums[0]
	running := 0
	minPrefix := 0
	for _, x := range nums {
		running += x
		if running-minPrefix > best {
			best = running - minPrefix
		}
		if running < minPrefix {
			minPrefix = running
		}
	}
	return best
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %d\n  Expected: %d\n", name, got, expected)
			failed++
		}
	}

	cases := []struct {
		name     string
		nums     []int
		expected int
	}{
		{"Example 1", []int{-2, 1, -3, 4, -1, 2, 1, -5, 4}, 6},
		{"Single positive", []int{1}, 1},
		{"Mostly positive", []int{5, 4, -1, 7, 8}, 23},
		{"Single negative", []int{-5}, -5},
		{"All negative", []int{-3, -1, -2}, -1},
		{"All zeros", []int{0, 0, 0}, 0},
		{"All positive", []int{1, 2, 3, 4, 5}, 15},
		{"Alternating", []int{-1, 2, -1, 2, -1, 2}, 4},
		{"Long sequence with zeros", []int{0, 0, -1, 0, 0}, 0},
		{"Two elements pos/neg", []int{-1, 2}, 2},
		{"Two elements neg/neg", []int{-1, -2}, -1},
		{"Single zero", []int{0}, 0},
		{"Boundary peak", []int{-2, -3, 4}, 4},
	}

	fmt.Println("=== Kadane's Algorithm ===")
	for _, tc := range cases {
		test(tc.name, maxSubArray(tc.nums), tc.expected)
	}

	fmt.Println("\n=== Brute Force ===")
	for _, tc := range cases {
		test("Brute "+tc.name, maxSubArrayBrute(tc.nums), tc.expected)
	}

	fmt.Println("\n=== Divide and Conquer ===")
	for _, tc := range cases {
		test("DC "+tc.name, maxSubArrayDC(tc.nums), tc.expected)
	}

	fmt.Println("\n=== Prefix Sum ===")
	for _, tc := range cases {
		test("Prefix "+tc.name, maxSubArrayPrefix(tc.nums), tc.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
