package main

import "fmt"

// ============================================================
// 0064. Minimum Path Sum
// https://leetcode.com/problems/minimum-path-sum/
// Difficulty: Medium
// Tags: Array, Dynamic Programming, Matrix
// ============================================================

// minPathSum — Optimal Solution (1D Bottom-Up DP)
// Approach: rolling 1D array; dp[c] = grid[r][c] + min(dp[c], dp[c-1]).
// Time:  O(m * n)
// Space: O(n)
func minPathSum(grid [][]int) int {
	m := len(grid)
	n := len(grid[0])
	dp := make([]int, n)
	dp[0] = grid[0][0]
	for c := 1; c < n; c++ {
		dp[c] = dp[c-1] + grid[0][c]
	}
	for r := 1; r < m; r++ {
		dp[0] += grid[r][0]
		for c := 1; c < n; c++ {
			if dp[c] < dp[c-1] {
				dp[c] = grid[r][c] + dp[c]
			} else {
				dp[c] = grid[r][c] + dp[c-1]
			}
		}
	}
	return dp[n-1]
}

func minPathSum2D(grid [][]int) int {
	m := len(grid)
	n := len(grid[0])
	dp := make([][]int, m)
	for i := range dp {
		dp[i] = make([]int, n)
	}
	dp[0][0] = grid[0][0]
	for c := 1; c < n; c++ {
		dp[0][c] = dp[0][c-1] + grid[0][c]
	}
	for r := 1; r < m; r++ {
		dp[r][0] = dp[r-1][0] + grid[r][0]
	}
	for r := 1; r < m; r++ {
		for c := 1; c < n; c++ {
			up, left := dp[r-1][c], dp[r][c-1]
			if up < left {
				dp[r][c] = grid[r][c] + up
			} else {
				dp[r][c] = grid[r][c] + left
			}
		}
	}
	return dp[m-1][n-1]
}

func minPathSumMemo(grid [][]int) int {
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
		if r == 0 && c == 0 {
			return grid[0][0]
		}
		if r < 0 || c < 0 {
			return 1 << 30
		}
		if memo[r][c] != -1 {
			return memo[r][c]
		}
		up, left := f(r-1, c), f(r, c-1)
		v := grid[r][c]
		if up < left {
			v += up
		} else {
			v += left
		}
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
		{"Example 1", [][]int{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}, 7},
		{"Example 2", [][]int{{1, 2, 3}, {4, 5, 6}}, 12},
		{"Single cell", [][]int{{5}}, 5},
		{"Single row", [][]int{{1, 2, 3}}, 6},
		{"Single column", [][]int{{1}, {2}, {3}}, 6},
		{"All zeros", [][]int{{0, 0}, {0, 0}}, 0},
		{"All same 3x3", [][]int{{5, 5, 5}, {5, 5, 5}, {5, 5, 5}}, 25},
		{"Strong gradient", [][]int{{1, 100, 100}, {1, 1, 1}, {100, 1, 1}}, 5},
		{"Larger 4x4",
			[][]int{{1, 2, 3, 4}, {2, 3, 4, 5}, {3, 4, 5, 6}, {4, 5, 6, 7}}, 28},
		{"Single zero", [][]int{{0}}, 0},
	}

	fmt.Println("=== 1D DP ===")
	for _, c := range cases {
		test(c.name, minPathSum(cloneGrid(c.grid)), c.want)
	}
	fmt.Println("\n=== 2D DP ===")
	for _, c := range cases {
		test("2D "+c.name, minPathSum2D(cloneGrid(c.grid)), c.want)
	}
	fmt.Println("\n=== Memoization ===")
	for _, c := range cases {
		test("Memo "+c.name, minPathSumMemo(cloneGrid(c.grid)), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
