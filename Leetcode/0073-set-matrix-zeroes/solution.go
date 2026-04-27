package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0073. Set Matrix Zeroes
// https://leetcode.com/problems/set-matrix-zeroes/
// Difficulty: Medium
// Tags: Array, Hash Table, Matrix
// ============================================================

// setZeroes — Optimal Solution (First Row/Col Markers, O(1) space)
// Approach: use first row/col to remember which rows/cols contain a zero,
//   plus two booleans for the first row/col themselves.
// Time:  O(m * n)
// Space: O(1)
func setZeroes(matrix [][]int) {
	m := len(matrix)
	n := len(matrix[0])
	firstRowZero, firstColZero := false, false
	for j := 0; j < n; j++ {
		if matrix[0][j] == 0 {
			firstRowZero = true
			break
		}
	}
	for i := 0; i < m; i++ {
		if matrix[i][0] == 0 {
			firstColZero = true
			break
		}
	}
	for i := 1; i < m; i++ {
		for j := 1; j < n; j++ {
			if matrix[i][j] == 0 {
				matrix[i][0] = 0
				matrix[0][j] = 0
			}
		}
	}
	for i := 1; i < m; i++ {
		for j := 1; j < n; j++ {
			if matrix[i][0] == 0 || matrix[0][j] == 0 {
				matrix[i][j] = 0
			}
		}
	}
	if firstRowZero {
		for j := 0; j < n; j++ {
			matrix[0][j] = 0
		}
	}
	if firstColZero {
		for i := 0; i < m; i++ {
			matrix[i][0] = 0
		}
	}
}

// setZeroesAux — O(m+n) auxiliary arrays
// Time:  O(m * n)
// Space: O(m + n)
func setZeroesAux(matrix [][]int) {
	m := len(matrix)
	n := len(matrix[0])
	zr := make([]bool, m)
	zc := make([]bool, n)
	for i := 0; i < m; i++ {
		for j := 0; j < n; j++ {
			if matrix[i][j] == 0 {
				zr[i] = true
				zc[j] = true
			}
		}
	}
	for i := 0; i < m; i++ {
		for j := 0; j < n; j++ {
			if zr[i] || zc[j] {
				matrix[i][j] = 0
			}
		}
	}
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
	test := func(name string, got, expected [][]int) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	cases := []struct {
		name     string
		input    [][]int
		expected [][]int
	}{
		{"Example 1", [][]int{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}},
			[][]int{{1, 0, 1}, {0, 0, 0}, {1, 0, 1}}},
		{"Example 2", [][]int{{0, 1, 2, 0}, {3, 4, 5, 2}, {1, 3, 1, 5}},
			[][]int{{0, 0, 0, 0}, {0, 4, 5, 0}, {0, 3, 1, 0}}},
		{"No zeros", [][]int{{1, 2}, {3, 4}}, [][]int{{1, 2}, {3, 4}}},
		{"All zeros", [][]int{{0, 0}, {0, 0}}, [][]int{{0, 0}, {0, 0}}},
		{"Single zero corner", [][]int{{0, 1}, {1, 1}}, [][]int{{0, 0}, {0, 1}}},
		{"Single row", [][]int{{1, 0, 1}}, [][]int{{0, 0, 0}}},
		{"Single col", [][]int{{1}, {0}, {1}}, [][]int{{0}, {0}, {0}}},
		{"1x1 zero", [][]int{{0}}, [][]int{{0}}},
		{"1x1 nonzero", [][]int{{5}}, [][]int{{5}}},
	}

	fmt.Println("=== O(1) markers ===")
	for _, c := range cases {
		got := cloneGrid(c.input)
		setZeroes(got)
		test(c.name, got, c.expected)
	}
	fmt.Println("\n=== O(m+n) auxiliary ===")
	for _, c := range cases {
		got := cloneGrid(c.input)
		setZeroesAux(got)
		test("Aux "+c.name, got, c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
