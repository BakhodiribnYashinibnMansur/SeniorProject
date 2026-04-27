package main

import "fmt"

// ============================================================
// 0063. Unique Paths II
// https://leetcode.com/problems/unique-paths-ii/
// Difficulty: Medium
// Tags: Array, Dynamic Programming, Matrix
// ============================================================

// uniquePathsWithObstacles — Optimal Solution (1D DP)
// Approach: 1D rolling array; cell with obstacle resets to 0.
// Time:  O(m * n)
// Space: O(n)
func uniquePathsWithObstacles(grid [][]int) int {
	m := len(grid)
	n := len(grid[0])
	if grid[0][0] == 1 {
		return 0
	}
	dp := make([]int, n)
	dp[0] = 1
	for r := 0; r < m; r++ {
		for c := 0; c < n; c++ {
			if grid[r][c] == 1 {
				dp[c] = 0
			} else if c > 0 {
				dp[c] += dp[c-1]
			}
		}
	}
	return dp[n-1]
}

// uniquePathsWithObstacles2D — 2D DP (clearer)
// Time:  O(m * n)
// Space: O(m * n)
func uniquePathsWithObstacles2D(grid [][]int) int {
	m := len(grid)
	n := len(grid[0])
	if grid[0][0] == 1 {
		return 0
	}
	dp := make([][]int, m)
	for i := range dp {
		dp[i] = make([]int, n)
	}
	dp[0][0] = 1
	for c := 1; c < n; c++ {
		if grid[0][c] == 0 {
			dp[0][c] = dp[0][c-1]
		}
	}
	for r := 1; r < m; r++ {
		if grid[r][0] == 0 {
			dp[r][0] = dp[r-1][0]
		}
		for c := 1; c < n; c++ {
			if grid[r][c] == 0 {
				dp[r][c] = dp[r-1][c] + dp[r][c-1]
			}
		}
	}
	return dp[m-1][n-1]
}

// uniquePathsWithObstaclesMemo — Top-Down DP
// Time:  O(m * n)
// Space: O(m * n)
func uniquePathsWithObstaclesMemo(grid [][]int) int {
	m := len(grid)
	n := len(grid[0])
	memo := make([][]int, m)
	for i := range memo {
		memo[i] = make([]int, n)
		for j := range memo[i] {
			memo[i][j] = -1
		}
	}
	var f func(r, c int) int
	f = func(r, c int) int {
		if r < 0 || c < 0 || grid[r][c] == 1 {
			return 0
		}
		if r == 0 && c == 0 {
			return 1
		}
		if memo[r][c] != -1 {
			return memo[r][c]
		}
		v := f(r-1, c) + f(r, c-1)
		memo[r][c] = v
		return v
	}
	return f(m-1, n-1)
}

// ============================================================
// Test Cases
// ============================================================

func cloneGrid(g [][]int) [][]int {
	out := make([][]int, len(g))
	for i := range g {
		out[i] = append([]int{}, g[i]...)
	}
	return out
}

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
		grid [][]int
		want int
	}
	cases := []tc{
		{"Example 1", [][]int{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}, 2},
		{"Example 2", [][]int{{0, 1}, {0, 0}}, 1},
		{"Start blocked", [][]int{{1, 0}, {0, 0}}, 0},
		{"End blocked", [][]int{{0, 0}, {0, 1}}, 0},
		{"1x1 free", [][]int{{0}}, 1},
		{"1x1 blocked", [][]int{{1}}, 0},
		{"All free 3x3", [][]int{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, 6},
		{"First row obstacle", [][]int{{0, 1, 0}, {0, 0, 0}, {0, 0, 0}}, 3},
		{"All vertical block", [][]int{{0}, {1}, {0}}, 0},
		{"Diagonal of obstacles avoided",
			[][]int{{0, 0, 0}, {1, 1, 0}, {0, 0, 0}}, 1},
	}

	fmt.Println("=== 1D DP ===")
	for _, c := range cases {
		test(c.name, uniquePathsWithObstacles(cloneGrid(c.grid)), c.want)
	}
	fmt.Println("\n=== 2D DP ===")
	for _, c := range cases {
		test("2D "+c.name, uniquePathsWithObstacles2D(cloneGrid(c.grid)), c.want)
	}
	fmt.Println("\n=== Memoization ===")
	for _, c := range cases {
		test("Memo "+c.name, uniquePathsWithObstaclesMemo(cloneGrid(c.grid)), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
