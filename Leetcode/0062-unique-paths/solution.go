package main

import "fmt"

// ============================================================
// 0062. Unique Paths
// https://leetcode.com/problems/unique-paths/
// Difficulty: Medium
// Tags: Math, Dynamic Programming, Combinatorics
// ============================================================

// uniquePaths — Optimal Solution (1D Bottom-Up DP)
// Approach: rolling 1D array of size n; dp[c] = dp[c] + dp[c-1].
// Time:  O(m * n)
// Space: O(n)
func uniquePaths(m int, n int) int {
	dp := make([]int, n)
	for i := range dp {
		dp[i] = 1
	}
	for r := 1; r < m; r++ {
		for c := 1; c < n; c++ {
			dp[c] = dp[c] + dp[c-1]
		}
	}
	return dp[n-1]
}

// uniquePaths2D — 2D Bottom-Up DP (clearer)
// Time:  O(m * n)
// Space: O(m * n)
func uniquePaths2D(m, n int) int {
	dp := make([][]int, m)
	for r := 0; r < m; r++ {
		dp[r] = make([]int, n)
		for c := 0; c < n; c++ {
			dp[r][c] = 1
		}
	}
	for r := 1; r < m; r++ {
		for c := 1; c < n; c++ {
			dp[r][c] = dp[r-1][c] + dp[r][c-1]
		}
	}
	return dp[m-1][n-1]
}

// uniquePathsMemo — Top-Down with memoization
// Time:  O(m * n)
// Space: O(m * n)
func uniquePathsMemo(m, n int) int {
	memo := make([][]int, m)
	for i := range memo {
		memo[i] = make([]int, n)
	}
	var f func(r, c int) int
	f = func(r, c int) int {
		if r == 0 || c == 0 {
			return 1
		}
		if memo[r][c] != 0 {
			return memo[r][c]
		}
		v := f(r-1, c) + f(r, c-1)
		memo[r][c] = v
		return v
	}
	return f(m-1, n-1)
}

// uniquePathsMath — Combinatorics: C(m+n-2, min(m-1, n-1))
// Time:  O(min(m, n))
// Space: O(1)
func uniquePathsMath(m, n int) int {
	a := m + n - 2
	b := m - 1
	if n-1 < b {
		b = n - 1
	}
	result := 1
	for i := 0; i < b; i++ {
		result = result * (a - i) / (i + 1)
	}
	return result
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

	type tc struct {
		name string
		m, n int
		want int
	}
	cases := []tc{
		{"Example 1", 3, 7, 28},
		{"Example 2", 3, 2, 3},
		{"1x1", 1, 1, 1},
		{"1xN", 1, 10, 1},
		{"Mx1", 10, 1, 1},
		{"Equal 3x3", 3, 3, 6},
		{"Equal 5x5", 5, 5, 70},
		{"7x3 symmetric", 7, 3, 28},
		{"Larger 10x10", 10, 10, 48620},
		{"23x12", 23, 12, 193536720},
	}

	fmt.Println("=== 1D DP ===")
	for _, c := range cases {
		test(c.name, uniquePaths(c.m, c.n), c.want)
	}
	fmt.Println("\n=== 2D DP ===")
	for _, c := range cases {
		test("2D "+c.name, uniquePaths2D(c.m, c.n), c.want)
	}
	fmt.Println("\n=== Memoization ===")
	for _, c := range cases {
		test("Memo "+c.name, uniquePathsMemo(c.m, c.n), c.want)
	}
	fmt.Println("\n=== Combinatorics ===")
	for _, c := range cases {
		test("Math "+c.name, uniquePathsMath(c.m, c.n), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
