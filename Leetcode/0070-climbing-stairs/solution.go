package main

import "fmt"

// ============================================================
// 0070. Climbing Stairs
// https://leetcode.com/problems/climbing-stairs/
// Difficulty: Easy
// Tags: Math, Dynamic Programming, Memoization
// ============================================================

// climbStairs — Optimal Solution (DP with O(1) Space)
// Approach: rolling pair tracking the previous two values.
// Time:  O(n)
// Space: O(1)
func climbStairs(n int) int {
	if n <= 2 {
		return n
	}
	prev2, prev1 := 1, 2
	for i := 3; i <= n; i++ {
		cur := prev1 + prev2
		prev2 = prev1
		prev1 = cur
	}
	return prev1
}

// climbStairsDP — Bottom-Up DP with array
// Time:  O(n)
// Space: O(n)
func climbStairsDP(n int) int {
	if n <= 2 {
		return n
	}
	dp := make([]int, n+1)
	dp[1] = 1
	dp[2] = 2
	for i := 3; i <= n; i++ {
		dp[i] = dp[i-1] + dp[i-2]
	}
	return dp[n]
}

// climbStairsMemo — Top-Down DP
// Time:  O(n)
// Space: O(n)
func climbStairsMemo(n int) int {
	memo := make([]int, n+1)
	var f func(k int) int
	f = func(k int) int {
		if k <= 2 {
			return k
		}
		if memo[k] != 0 {
			return memo[k]
		}
		v := f(k-1) + f(k-2)
		memo[k] = v
		return v
	}
	return f(n)
}

// climbStairsMatrix — Matrix exponentiation
// Time:  O(log n)
// Space: O(1)
func climbStairsMatrix(n int) int {
	matMul := func(a, b [2][2]int) [2][2]int {
		return [2][2]int{
			{a[0][0]*b[0][0] + a[0][1]*b[1][0], a[0][0]*b[0][1] + a[0][1]*b[1][1]},
			{a[1][0]*b[0][0] + a[1][1]*b[1][0], a[1][0]*b[0][1] + a[1][1]*b[1][1]},
		}
	}
	matPow := func(m [2][2]int, p int) [2][2]int {
		result := [2][2]int{{1, 0}, {0, 1}}
		for p > 0 {
			if p&1 == 1 {
				result = matMul(result, m)
			}
			m = matMul(m, m)
			p >>= 1
		}
		return result
	}
	m := matPow([2][2]int{{1, 1}, {1, 0}}, n)
	return m[0][0]
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
		n    int
		want int
	}{
		{1, 1}, {2, 2}, {3, 3}, {4, 5}, {5, 8}, {6, 13}, {7, 21}, {8, 34},
		{10, 89}, {15, 987}, {20, 10946}, {30, 1346269}, {45, 1836311903},
	}

	fmt.Println("=== O(1) DP ===")
	for _, c := range cases {
		test(fmt.Sprintf("n=%d", c.n), climbStairs(c.n), c.want)
	}
	fmt.Println("\n=== O(n) DP ===")
	for _, c := range cases {
		test(fmt.Sprintf("DP n=%d", c.n), climbStairsDP(c.n), c.want)
	}
	fmt.Println("\n=== Memoization ===")
	for _, c := range cases {
		test(fmt.Sprintf("Memo n=%d", c.n), climbStairsMemo(c.n), c.want)
	}
	fmt.Println("\n=== Matrix Exponentiation ===")
	for _, c := range cases {
		test(fmt.Sprintf("Matrix n=%d", c.n), climbStairsMatrix(c.n), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
