package main

import "fmt"

// ============================================================
// 0045. Jump Game II
// https://leetcode.com/problems/jump-game-ii/
// Difficulty: Medium
// Tags: Array, Dynamic Programming, Greedy
// ============================================================

// jump — Optimal Solution (Greedy / BFS-like)
// Approach: Track jump levels using end and farthest pointers
// Time:  O(n) — single pass through the array
// Space: O(1) — only three variables
func jump(nums []int) int {
	n := len(nums)
	jumps := 0
	end := 0      // boundary of current jump level
	farthest := 0 // farthest reachable from current level

	for i := 0; i < n-1; i++ {
		// Update the farthest we can reach from this level
		if i+nums[i] > farthest {
			farthest = i + nums[i]
		}

		// If we've reached the end of the current level, jump
		if i == end {
			jumps++
			end = farthest
			if end >= n-1 {
				break
			}
		}
	}

	return jumps
}

// jumpDP — Dynamic Programming approach
// Approach: dp[i] = minimum jumps to reach index i
// Time:  O(n * max(nums[i])) — for each index, update reachable indices
// Space: O(n) — dp array
func jumpDP(nums []int) int {
	n := len(nums)
	dp := make([]int, n)
	for i := 1; i < n; i++ {
		dp[i] = n // initialize to a large value
	}

	for i := 0; i < n-1; i++ {
		end := i + nums[i]
		if end >= n {
			end = n - 1
		}
		for j := i + 1; j <= end; j++ {
			if dp[i]+1 < dp[j] {
				dp[j] = dp[i] + 1
			}
		}
	}

	return dp[n-1]
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
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

	fmt.Println("=== Greedy / BFS-like (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1", jump([]int{2, 3, 1, 1, 4}), 2)

	// Test 2: LeetCode Example 2
	test("Example 2", jump([]int{2, 3, 0, 1, 4}), 2)

	// Test 3: Single element
	test("Single element", jump([]int{0}), 0)

	// Test 4: Two elements
	test("Two elements", jump([]int{1, 0}), 1)

	// Test 5: Already covers the end
	test("One big jump", jump([]int{5, 1, 1, 1, 1}), 1)

	// Test 6: All ones
	test("All ones", jump([]int{1, 1, 1, 1, 1}), 4)

	// Test 7: Decreasing jumps
	test("Decreasing", jump([]int{4, 3, 2, 1, 0}), 1)

	// Test 8: Increasing jumps
	test("Increasing", jump([]int{1, 2, 3, 4, 5}), 3)

	// Test 9: Larger example
	test("Larger example", jump([]int{1, 2, 1, 1, 1}), 3)

	// Test 10: Jump exactly to end
	test("Exact jump", jump([]int{2, 1, 1}), 1)

	fmt.Println("\n=== Dynamic Programming ===")

	// Test 11: DP — Example 1
	test("DP Example 1", jumpDP([]int{2, 3, 1, 1, 4}), 2)

	// Test 12: DP — Example 2
	test("DP Example 2", jumpDP([]int{2, 3, 0, 1, 4}), 2)

	// Test 13: DP — Single element
	test("DP Single element", jumpDP([]int{0}), 0)

	// Test 14: DP — All ones
	test("DP All ones", jumpDP([]int{1, 1, 1, 1, 1}), 4)

	// Test 15: DP — One big jump
	test("DP One big jump", jumpDP([]int{5, 1, 1, 1, 1}), 1)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
