package main

import "fmt"

// ============================================================
// 0072. Edit Distance
// https://leetcode.com/problems/edit-distance/
// Difficulty: Hard
// Tags: String, Dynamic Programming
// ============================================================

// minDistance — Optimal Solution (1D Bottom-Up DP)
// Approach: rolling 1D array of size n+1; track prevDiag for the
//   replacement transition.
// Time:  O(m * n)
// Space: O(n)
func minDistance(word1 string, word2 string) int {
	m, n := len(word1), len(word2)
	dp := make([]int, n+1)
	for j := 0; j <= n; j++ {
		dp[j] = j
	}
	for i := 1; i <= m; i++ {
		prevDiag := dp[0]
		dp[0] = i
		for j := 1; j <= n; j++ {
			temp := dp[j]
			if word1[i-1] == word2[j-1] {
				dp[j] = prevDiag
			} else {
				m := dp[j]
				if dp[j-1] < m {
					m = dp[j-1]
				}
				if prevDiag < m {
					m = prevDiag
				}
				dp[j] = 1 + m
			}
			prevDiag = temp
		}
	}
	return dp[n]
}

// minDistance2D — 2D Bottom-Up DP
// Time:  O(m * n)
// Space: O(m * n)
func minDistance2D(word1, word2 string) int {
	m, n := len(word1), len(word2)
	dp := make([][]int, m+1)
	for i := range dp {
		dp[i] = make([]int, n+1)
		dp[i][0] = i
	}
	for j := 0; j <= n; j++ {
		dp[0][j] = j
	}
	for i := 1; i <= m; i++ {
		for j := 1; j <= n; j++ {
			if word1[i-1] == word2[j-1] {
				dp[i][j] = dp[i-1][j-1]
			} else {
				a, b, c := dp[i-1][j], dp[i][j-1], dp[i-1][j-1]
				mn := a
				if b < mn {
					mn = b
				}
				if c < mn {
					mn = c
				}
				dp[i][j] = 1 + mn
			}
		}
	}
	return dp[m][n]
}

// minDistanceMemo — Top-Down DP
// Time:  O(m * n)
// Space: O(m * n)
func minDistanceMemo(word1, word2 string) int {
	m, n := len(word1), len(word2)
	memo := make([][]int, m+1)
	for i := range memo {
		memo[i] = make([]int, n+1)
		for j := range memo[i] {
			memo[i][j] = -1
		}
	}
	var f func(i, j int) int
	f = func(i, j int) int {
		if i == 0 {
			return j
		}
		if j == 0 {
			return i
		}
		if memo[i][j] != -1 {
			return memo[i][j]
		}
		var v int
		if word1[i-1] == word2[j-1] {
			v = f(i-1, j-1)
		} else {
			a, b, c := f(i-1, j), f(i, j-1), f(i-1, j-1)
			mn := a
			if b < mn {
				mn = b
			}
			if c < mn {
				mn = c
			}
			v = 1 + mn
		}
		memo[i][j] = v
		return v
	}
	return f(m, n)
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
		name, w1, w2 string
		want         int
	}{
		{"Example 1", "horse", "ros", 3},
		{"Example 2", "intention", "execution", 5},
		{"Both empty", "", "", 0},
		{"Empty w1", "", "abc", 3},
		{"Empty w2", "abc", "", 3},
		{"Identical", "abc", "abc", 0},
		{"All replace", "abc", "xyz", 3},
		{"One char insert", "a", "ab", 1},
		{"One char delete", "ab", "a", 1},
		{"Single replace", "a", "b", 1},
		{"Larger same", "abcdef", "abcdef", 0},
	}

	fmt.Println("=== 1D DP ===")
	for _, c := range cases {
		test(c.name, minDistance(c.w1, c.w2), c.want)
	}
	fmt.Println("\n=== 2D DP ===")
	for _, c := range cases {
		test("2D "+c.name, minDistance2D(c.w1, c.w2), c.want)
	}
	fmt.Println("\n=== Memoization ===")
	for _, c := range cases {
		test("Memo "+c.name, minDistanceMemo(c.w1, c.w2), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
