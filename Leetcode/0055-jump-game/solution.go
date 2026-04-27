package main

import "fmt"

// ============================================================
// 0055. Jump Game
// https://leetcode.com/problems/jump-game/
// Difficulty: Medium
// Tags: Array, Dynamic Programming, Greedy
// ============================================================

// canJump — Optimal Solution (Greedy)
// Approach: maintain the farthest reachable index. If current index ever
//   exceeds it we are stuck; if farthest >= n-1 we have won.
// Time:  O(n)
// Space: O(1)
func canJump(nums []int) bool {
	farthest := 0
	for i := 0; i < len(nums); i++ {
		if i > farthest {
			return false
		}
		if i+nums[i] > farthest {
			farthest = i + nums[i]
		}
		if farthest >= len(nums)-1 {
			return true
		}
	}
	return true
}

// canJumpDP — Bottom-Up DP (right-to-left)
// Time:  O(n)
// Space: O(1)
func canJumpDP(nums []int) bool {
	n := len(nums)
	lastGood := n - 1
	for i := n - 2; i >= 0; i-- {
		if i+nums[i] >= lastGood {
			lastGood = i
		}
	}
	return lastGood == 0
}

// canJumpMemo — Top-Down DP with memoization
// Time:  O(n^2)
// Space: O(n)
func canJumpMemo(nums []int) bool {
	n := len(nums)
	dp := make([]int8, n)
	var solve func(i int) bool
	solve = func(i int) bool {
		if i >= n-1 {
			return true
		}
		if dp[i] != 0 {
			return dp[i] == 1
		}
		furthest := i + nums[i]
		if furthest >= n-1 {
			furthest = n - 1
		}
		for j := i + 1; j <= furthest; j++ {
			if solve(j) {
				dp[i] = 1
				return true
			}
		}
		dp[i] = -1
		return false
	}
	return solve(0)
}

// canJumpBrute — Pure recursion (TLE for large n)
// Time:  O(2^n)
// Space: O(n)
func canJumpBrute(nums []int) bool {
	var rec func(i int) bool
	rec = func(i int) bool {
		if i >= len(nums)-1 {
			return true
		}
		furthest := i + nums[i]
		if furthest >= len(nums)-1 {
			furthest = len(nums) - 1
		}
		for j := i + 1; j <= furthest; j++ {
			if rec(j) {
				return true
			}
		}
		return false
	}
	return rec(0)
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	type tc struct {
		name     string
		nums     []int
		expected bool
	}
	cases := []tc{
		{"Example 1", []int{2, 3, 1, 1, 4}, true},
		{"Example 2", []int{3, 2, 1, 0, 4}, false},
		{"Single zero", []int{0}, true},
		{"Single positive", []int{5}, true},
		{"Cannot move", []int{0, 1}, false},
		{"Big first jump", []int{100, 0, 0, 0}, true},
		{"All ones", []int{1, 1, 1, 1, 1}, true},
		{"Edge size 2 reachable", []int{1, 0}, true},
		{"Two zeros block", []int{2, 0, 0, 1}, false},
		{"Just barely", []int{2, 0, 0}, true},
		{"Need two jumps", []int{1, 2, 3}, true},
		{"All zeros except first", []int{4, 0, 0, 0, 0}, true},
		{"Long zeros chain", []int{1, 1, 0, 1}, false},
		{"Final element irrelevant value", []int{2, 1, 0, 0, 0}, false},
	}

	fmt.Println("=== Greedy ===")
	for _, c := range cases {
		test(c.name, canJump(c.nums), c.expected)
	}

	fmt.Println("\n=== Bottom-Up DP ===")
	for _, c := range cases {
		test("DP "+c.name, canJumpDP(c.nums), c.expected)
	}

	fmt.Println("\n=== Top-Down DP (Memo) ===")
	for _, c := range cases {
		test("Memo "+c.name, canJumpMemo(c.nums), c.expected)
	}

	// Brute only for small inputs
	fmt.Println("\n=== Brute Force (small only) ===")
	for _, c := range cases {
		if len(c.nums) > 12 {
			continue
		}
		test("Brute "+c.name, canJumpBrute(c.nums), c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
