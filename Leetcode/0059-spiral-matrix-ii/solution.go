package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0059. Spiral Matrix II
// https://leetcode.com/problems/spiral-matrix-ii/
// Difficulty: Medium
// Tags: Array, Matrix, Simulation
// ============================================================

// generateMatrix — Optimal Solution (Layer by Layer)
// Approach: shrink top/bottom/left/right after each side, write 1..n^2.
// Time:  O(n^2)
// Space: O(1) extra (output excluded)
func generateMatrix(n int) [][]int {
	m := make([][]int, n)
	for i := range m {
		m[i] = make([]int, n)
	}
	top, bottom, left, right := 0, n-1, 0, n-1
	val := 1
	for val <= n*n {
		for c := left; c <= right; c++ {
			m[top][c] = val
			val++
		}
		top++
		for r := top; r <= bottom; r++ {
			m[r][right] = val
			val++
		}
		right--
		if top <= bottom {
			for c := right; c >= left; c-- {
				m[bottom][c] = val
				val++
			}
			bottom--
		}
		if left <= right {
			for r := bottom; r >= top; r-- {
				m[r][left] = val
				val++
			}
			left++
		}
	}
	return m
}

// generateMatrixDirVec — Direction vectors using matrix as visited
// Time:  O(n^2)
// Space: O(1) extra
func generateMatrixDirVec(n int) [][]int {
	m := make([][]int, n)
	for i := range m {
		m[i] = make([]int, n)
	}
	dr := []int{0, 1, 0, -1}
	dc := []int{1, 0, -1, 0}
	r, c, d := 0, 0, 0
	for k := 1; k <= n*n; k++ {
		m[r][c] = k
		nr, nc := r+dr[d], c+dc[d]
		if nr < 0 || nr >= n || nc < 0 || nc >= n || m[nr][nc] != 0 {
			d = (d + 1) % 4
			nr, nc = r+dr[d], c+dc[d]
		}
		r, c = nr, nc
	}
	return m
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected [][]int) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	type tc struct {
		name     string
		n        int
		expected [][]int
	}
	cases := []tc{
		{"n=1", 1, [][]int{{1}}},
		{"n=2", 2, [][]int{{1, 2}, {4, 3}}},
		{"n=3", 3, [][]int{{1, 2, 3}, {8, 9, 4}, {7, 6, 5}}},
		{"n=4", 4, [][]int{
			{1, 2, 3, 4},
			{12, 13, 14, 5},
			{11, 16, 15, 6},
			{10, 9, 8, 7},
		}},
		{"n=5", 5, [][]int{
			{1, 2, 3, 4, 5},
			{16, 17, 18, 19, 6},
			{15, 24, 25, 20, 7},
			{14, 23, 22, 21, 8},
			{13, 12, 11, 10, 9},
		}},
	}

	fmt.Println("=== Layer by Layer ===")
	for _, c := range cases {
		test(c.name, generateMatrix(c.n), c.expected)
	}

	fmt.Println("\n=== Direction Vectors ===")
	for _, c := range cases {
		test("DirVec "+c.name, generateMatrixDirVec(c.n), c.expected)
	}

	// Validate values 1..n^2 contained for n=20
	check := generateMatrix(20)
	seen := make(map[int]bool)
	for _, row := range check {
		for _, v := range row {
			seen[v] = true
		}
	}
	allPresent := true
	for k := 1; k <= 400; k++ {
		if !seen[k] {
			allPresent = false
			break
		}
	}
	if allPresent && len(seen) == 400 {
		fmt.Println("PASS: n=20 contains 1..400 exactly once")
		passed++
	} else {
		fmt.Println("FAIL: n=20 missing values")
		failed++
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
