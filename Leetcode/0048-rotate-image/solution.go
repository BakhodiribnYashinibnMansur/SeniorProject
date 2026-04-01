package main

import "fmt"

// ============================================================
// 0048. Rotate Image
// https://leetcode.com/problems/rotate-image/
// Difficulty: Medium
// Tags: Array, Math, Matrix
// ============================================================

// rotate — Optimal Solution (Transpose + Reverse Rows)
// Approach: Transpose the matrix, then reverse each row
// Time:  O(n^2) — visit each cell a constant number of times
// Space: O(1) — all swaps done in-place
func rotate(matrix [][]int) {
	n := len(matrix)

	// Step 1: Transpose the matrix (swap across the diagonal)
	for i := 0; i < n; i++ {
		for j := i + 1; j < n; j++ {
			matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]
		}
	}

	// Step 2: Reverse each row
	for i := 0; i < n; i++ {
		left, right := 0, n-1
		for left < right {
			matrix[i][left], matrix[i][right] = matrix[i][right], matrix[i][left]
			left++
			right--
		}
	}
}

// rotateFourWay — Four-way Swap approach
// Approach: Rotate 4 cells at a time in a cycle
// Time:  O(n^2) — each cell is moved exactly once
// Space: O(1) — only one temp variable
func rotateFourWay(matrix [][]int) {
	n := len(matrix)

	// Process layer by layer from outside to inside
	for i := 0; i < n/2; i++ {
		for j := i; j < n-1-i; j++ {
			// Save top
			temp := matrix[i][j]

			// Left → Top
			matrix[i][j] = matrix[n-1-j][i]

			// Bottom → Left
			matrix[n-1-j][i] = matrix[n-1-i][n-1-j]

			// Right → Bottom
			matrix[n-1-i][n-1-j] = matrix[j][n-1-i]

			// Top → Right
			matrix[j][n-1-i] = temp
		}
	}
}

// ============================================================
// Test Cases
// ============================================================

func deepCopy(matrix [][]int) [][]int {
	n := len(matrix)
	cp := make([][]int, n)
	for i := range cp {
		cp[i] = make([]int, n)
		copy(cp[i], matrix[i])
	}
	return cp
}

func equal(a, b [][]int) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if len(a[i]) != len(b[i]) {
			return false
		}
		for j := range a[i] {
			if a[i][j] != b[i][j] {
				return false
			}
		}
	}
	return true
}

func main() {
	passed, failed := 0, 0

	testRotate := func(name string, matrix, expected [][]int) {
		mat := deepCopy(matrix)
		rotate(mat)
		if equal(mat, expected) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, mat, expected)
			failed++
		}
	}

	testFourWay := func(name string, matrix, expected [][]int) {
		mat := deepCopy(matrix)
		rotateFourWay(mat)
		if equal(mat, expected) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, mat, expected)
			failed++
		}
	}

	fmt.Println("=== Transpose + Reverse (Optimal) ===")

	// Test 1: LeetCode Example 1
	testRotate("Example 1",
		[][]int{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
		[][]int{{7, 4, 1}, {8, 5, 2}, {9, 6, 3}})

	// Test 2: LeetCode Example 2
	testRotate("Example 2",
		[][]int{{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}},
		[][]int{{15, 13, 2, 5}, {14, 3, 4, 1}, {12, 6, 8, 9}, {16, 7, 10, 11}})

	// Test 3: 1x1 matrix
	testRotate("1x1 matrix",
		[][]int{{1}},
		[][]int{{1}})

	// Test 4: 2x2 matrix
	testRotate("2x2 matrix",
		[][]int{{1, 2}, {3, 4}},
		[][]int{{3, 1}, {4, 2}})

	// Test 5: Negative values
	testRotate("Negative values",
		[][]int{{-1, -2}, {-3, -4}},
		[][]int{{-3, -1}, {-4, -2}})

	// Test 6: All same values
	testRotate("All same values",
		[][]int{{5, 5}, {5, 5}},
		[][]int{{5, 5}, {5, 5}})

	// Test 7: 5x5 matrix
	testRotate("5x5 matrix",
		[][]int{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 25}},
		[][]int{{21, 16, 11, 6, 1}, {22, 17, 12, 7, 2}, {23, 18, 13, 8, 3}, {24, 19, 14, 9, 4}, {25, 20, 15, 10, 5}})

	fmt.Println("\n=== Four-way Swap ===")

	// Test 8: Four-way — Example 1
	testFourWay("FW Example 1",
		[][]int{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}},
		[][]int{{7, 4, 1}, {8, 5, 2}, {9, 6, 3}})

	// Test 9: Four-way — Example 2
	testFourWay("FW Example 2",
		[][]int{{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}},
		[][]int{{15, 13, 2, 5}, {14, 3, 4, 1}, {12, 6, 8, 9}, {16, 7, 10, 11}})

	// Test 10: Four-way — 1x1
	testFourWay("FW 1x1 matrix",
		[][]int{{1}},
		[][]int{{1}})

	// Test 11: Four-way — 2x2
	testFourWay("FW 2x2 matrix",
		[][]int{{1, 2}, {3, 4}},
		[][]int{{3, 1}, {4, 2}})

	// Test 12: Four-way — 5x5
	testFourWay("FW 5x5 matrix",
		[][]int{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}, {21, 22, 23, 24, 25}},
		[][]int{{21, 16, 11, 6, 1}, {22, 17, 12, 7, 2}, {23, 18, 13, 8, 3}, {24, 19, 14, 9, 4}, {25, 20, 15, 10, 5}})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
