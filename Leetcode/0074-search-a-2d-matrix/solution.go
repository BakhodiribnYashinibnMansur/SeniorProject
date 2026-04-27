package main

import "fmt"

// ============================================================
// 0074. Search a 2D Matrix
// https://leetcode.com/problems/search-a-2d-matrix/
// Difficulty: Medium
// Tags: Array, Binary Search, Matrix
// ============================================================

// searchMatrix — Optimal Solution (Single Binary Search on Flat Index)
// Time:  O(log(m*n))
// Space: O(1)
func searchMatrix(matrix [][]int, target int) bool {
	m := len(matrix)
	n := len(matrix[0])
	lo, hi := 0, m*n-1
	for lo <= hi {
		mid := (lo + hi) / 2
		v := matrix[mid/n][mid%n]
		if v == target {
			return true
		}
		if v < target {
			lo = mid + 1
		} else {
			hi = mid - 1
		}
	}
	return false
}

// searchMatrixStaircase — Top-right staircase
// Time:  O(m + n)
// Space: O(1)
func searchMatrixStaircase(matrix [][]int, target int) bool {
	m := len(matrix)
	n := len(matrix[0])
	r, c := 0, n-1
	for r < m && c >= 0 {
		v := matrix[r][c]
		if v == target {
			return true
		}
		if v < target {
			r++
		} else {
			c--
		}
	}
	return false
}

// searchMatrixTwo — Two binary searches
// Time:  O(log m + log n)
// Space: O(1)
func searchMatrixTwo(matrix [][]int, target int) bool {
	m := len(matrix)
	n := len(matrix[0])
	lo, hi := 0, m-1
	for lo <= hi {
		mid := (lo + hi) / 2
		if matrix[mid][0] <= target && target <= matrix[mid][n-1] {
			l, r := 0, n-1
			for l <= r {
				mm := (l + r) / 2
				if matrix[mid][mm] == target {
					return true
				}
				if matrix[mid][mm] < target {
					l = mm + 1
				} else {
					r = mm - 1
				}
			}
			return false
		}
		if matrix[mid][0] > target {
			hi = mid - 1
		} else {
			lo = mid + 1
		}
	}
	return false
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	matrix := [][]int{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}

	cases := []struct {
		name   string
		mat    [][]int
		target int
		want   bool
	}{
		{"Example 1", matrix, 3, true},
		{"Example 2", matrix, 13, false},
		{"Found min", matrix, 1, true},
		{"Found max", matrix, 60, true},
		{"Below min", matrix, 0, false},
		{"Above max", matrix, 100, false},
		{"Found mid", matrix, 16, true},
		{"Not at boundary", matrix, 8, false},
		{"1x1 found", [][]int{{5}}, 5, true},
		{"1x1 not found", [][]int{{5}}, 6, false},
		{"Single row found", [][]int{{1, 3, 5}}, 3, true},
		{"Single row not found", [][]int{{1, 3, 5}}, 4, false},
		{"Single col found", [][]int{{1}, {3}, {5}}, 5, true},
		{"Single col not found", [][]int{{1}, {3}, {5}}, 4, false},
	}

	fmt.Println("=== Single Binary Search ===")
	for _, c := range cases {
		test(c.name, searchMatrix(c.mat, c.target), c.want)
	}
	fmt.Println("\n=== Staircase ===")
	for _, c := range cases {
		test("Stair "+c.name, searchMatrixStaircase(c.mat, c.target), c.want)
	}
	fmt.Println("\n=== Two Binary Searches ===")
	for _, c := range cases {
		test("Two "+c.name, searchMatrixTwo(c.mat, c.target), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
