package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0054. Spiral Matrix
// https://leetcode.com/problems/spiral-matrix/
// Difficulty: Medium
// Tags: Array, Matrix, Simulation
// ============================================================

// spiralOrder — Optimal Solution (Layer by Layer / Boundaries)
// Approach: shrink top/bottom/left/right after each sweep.
// Time:  O(m*n) — each cell once
// Space: O(1) — boundary pointers only (output excluded)
func spiralOrder(matrix [][]int) []int {
	if len(matrix) == 0 {
		return []int{}
	}
	m, n := len(matrix), len(matrix[0])
	top, bottom, left, right := 0, m-1, 0, n-1
	result := make([]int, 0, m*n)

	for top <= bottom && left <= right {
		for c := left; c <= right; c++ {
			result = append(result, matrix[top][c])
		}
		top++
		for r := top; r <= bottom; r++ {
			result = append(result, matrix[r][right])
		}
		right--
		if top <= bottom {
			for c := right; c >= left; c-- {
				result = append(result, matrix[bottom][c])
			}
			bottom--
		}
		if left <= right {
			for r := bottom; r >= top; r-- {
				result = append(result, matrix[r][left])
			}
			left++
		}
	}
	return result
}

// spiralOrderDirVec — Direction vectors with visited matrix
// Time:  O(m*n)
// Space: O(m*n) — visited grid
func spiralOrderDirVec(matrix [][]int) []int {
	if len(matrix) == 0 {
		return []int{}
	}
	m, n := len(matrix), len(matrix[0])
	visited := make([][]bool, m)
	for i := range visited {
		visited[i] = make([]bool, n)
	}
	dr := []int{0, 1, 0, -1}
	dc := []int{1, 0, -1, 0}

	result := make([]int, 0, m*n)
	r, c, d := 0, 0, 0
	for i := 0; i < m*n; i++ {
		result = append(result, matrix[r][c])
		visited[r][c] = true
		nr, nc := r+dr[d], c+dc[d]
		if nr < 0 || nr >= m || nc < 0 || nc >= n || visited[nr][nc] {
			d = (d + 1) % 4
			nr, nc = r+dr[d], c+dc[d]
		}
		r, c = nr, nc
	}
	return result
}

// spiralOrderInPlace — In-place marker (mutates input)
// Time:  O(m*n)
// Space: O(1)
func spiralOrderInPlace(matrix [][]int) []int {
	if len(matrix) == 0 {
		return []int{}
	}
	const SENTINEL = 1 << 30
	m, n := len(matrix), len(matrix[0])
	dr := []int{0, 1, 0, -1}
	dc := []int{1, 0, -1, 0}
	result := make([]int, 0, m*n)
	r, c, d := 0, 0, 0
	for i := 0; i < m*n; i++ {
		result = append(result, matrix[r][c])
		matrix[r][c] = SENTINEL
		nr, nc := r+dr[d], c+dc[d]
		if nr < 0 || nr >= m || nc < 0 || nc >= n || matrix[nr][nc] == SENTINEL {
			d = (d + 1) % 4
			nr, nc = r+dr[d], c+dc[d]
		}
		r, c = nr, nc
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func cloneMatrix(m [][]int) [][]int {
	out := make([][]int, len(m))
	for i := range m {
		out[i] = append([]int{}, m[i]...)
	}
	return out
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []int) {
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
		matrix   [][]int
		expected []int
	}
	cases := []tc{
		{"3x3", [][]int{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
			[]int{1, 2, 3, 6, 9, 8, 7, 4, 5}},
		{"3x4", [][]int{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}},
			[]int{1, 2, 3, 4, 8, 12, 11, 10, 9, 5, 6, 7}},
		{"1x1", [][]int{{5}}, []int{5}},
		{"1x4 row", [][]int{{1, 2, 3, 4}}, []int{1, 2, 3, 4}},
		{"3x1 column", [][]int{{1}, {2}, {3}}, []int{1, 2, 3}},
		{"2x2", [][]int{{1, 2}, {3, 4}}, []int{1, 2, 4, 3}},
		{"2x4", [][]int{{1, 2, 3, 4}, {5, 6, 7, 8}}, []int{1, 2, 3, 4, 8, 7, 6, 5}},
		{"3x2", [][]int{{1, 2}, {3, 4}, {5, 6}}, []int{1, 2, 4, 6, 5, 3}},
		{"4x4", [][]int{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}},
			[]int{1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10}},
		{"Negatives", [][]int{{-1, -2}, {-3, -4}}, []int{-1, -2, -4, -3}},
	}

	fmt.Println("=== Layer by Layer ===")
	for _, c := range cases {
		test(c.name, spiralOrder(cloneMatrix(c.matrix)), c.expected)
	}

	fmt.Println("\n=== Direction Vectors + Visited ===")
	for _, c := range cases {
		test("DirVec "+c.name, spiralOrderDirVec(cloneMatrix(c.matrix)), c.expected)
	}

	fmt.Println("\n=== In-Place Marker ===")
	for _, c := range cases {
		test("InPlace "+c.name, spiralOrderInPlace(cloneMatrix(c.matrix)), c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
