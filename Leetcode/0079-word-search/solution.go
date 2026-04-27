package main

import "fmt"

// ============================================================
// 0079. Word Search
// https://leetcode.com/problems/word-search/
// Difficulty: Medium
// Tags: Array, Backtracking, Matrix
// ============================================================

// exist — Optimal Solution (DFS with Backtracking)
// Approach: try DFS from each cell; mark visited via in-place sentinel.
// Time:  O(m * n * 4^L) — L = len(word)
// Space: O(L) recursion
func exist(board [][]byte, word string) bool {
	m := len(board)
	n := len(board[0])
	var dfs func(r, c, i int) bool
	dfs = func(r, c, i int) bool {
		if i == len(word) {
			return true
		}
		if r < 0 || r >= m || c < 0 || c >= n || board[r][c] != word[i] {
			return false
		}
		save := board[r][c]
		board[r][c] = '#'
		ok := dfs(r+1, c, i+1) || dfs(r-1, c, i+1) ||
			dfs(r, c+1, i+1) || dfs(r, c-1, i+1)
		board[r][c] = save
		return ok
	}
	for r := 0; r < m; r++ {
		for c := 0; c < n; c++ {
			if dfs(r, c, 0) {
				return true
			}
		}
	}
	return false
}

// ============================================================
// Test Cases
// ============================================================

func toBoard(rows []string) [][]byte {
	out := make([][]byte, len(rows))
	for i, r := range rows {
		out[i] = []byte(r)
	}
	return out
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%v\n", name, got)
			failed++
		}
	}

	standard := []string{"ABCE", "SFCS", "ADEE"}

	cases := []struct {
		name  string
		board []string
		word  string
		want  bool
	}{
		{"Example 1", standard, "ABCCED", true},
		{"Example 2", standard, "SEE", true},
		{"Example 3", standard, "ABCB", false},
		{"Single cell match", []string{"A"}, "A", true},
		{"Single cell miss", []string{"A"}, "B", false},
		{"Word longer than grid", []string{"AB"}, "ABC", false},
		{"Same letter twice", []string{"AAB"}, "AAB", true},
		{"Spiral path", []string{"ABCD", "EFGH", "IJKL"}, "ABCDHGFE", true},
		{"Diagonal not allowed", []string{"AB", "CD"}, "AD", false},
		{"FCC found", standard, "FCC", true},
		{"BCBC not adjacent", standard, "BCBC", false},
	}

	for _, c := range cases {
		test(c.name, exist(toBoard(c.board), c.word), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
