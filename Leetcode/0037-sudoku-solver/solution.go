package main

import "fmt"

// ============================================================
// 0037. Sudoku Solver
// https://leetcode.com/problems/sudoku-solver/
// Difficulty: Hard
// Tags: Array, Hash Table, Backtracking, Matrix
// ============================================================

// solveSudoku — Optimal Solution (Backtracking with boolean array tracking)
// Approach: Try digits 1-9 for each empty cell, backtrack on conflict
// Time:  O(9^m) — m is number of empty cells, heavily pruned in practice
// Space: O(m)   — recursion depth equals the number of empty cells
func solveSudoku(board [][]byte) {
	// Tracking arrays: which digits are used in each row/col/box
	var rows, cols, boxes [9][9]bool
	var empty [][2]int

	// Initialize: scan existing digits
	for r := 0; r < 9; r++ {
		for c := 0; c < 9; c++ {
			if board[r][c] != '.' {
				d := board[r][c] - '1'
				rows[r][d] = true
				cols[c][d] = true
				boxes[(r/3)*3+c/3][d] = true
			} else {
				empty = append(empty, [2]int{r, c})
			}
		}
	}

	var solve func(idx int) bool
	solve = func(idx int) bool {
		if idx == len(empty) {
			return true // All cells filled
		}

		r, c := empty[idx][0], empty[idx][1]
		boxId := (r/3)*3 + c/3

		for d := byte(0); d < 9; d++ {
			if !rows[r][d] && !cols[c][d] && !boxes[boxId][d] {
				// Place digit
				board[r][c] = d + '1'
				rows[r][d] = true
				cols[c][d] = true
				boxes[boxId][d] = true

				if solve(idx + 1) {
					return true
				}

				// Backtrack
				board[r][c] = '.'
				rows[r][d] = false
				cols[c][d] = false
				boxes[boxId][d] = false
			}
		}

		return false
	}

	solve(0)
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, board [][]byte, expected [][]byte) {
		solveSudoku(board)
		match := true
		for r := 0; r < 9; r++ {
			for c := 0; c < 9; c++ {
				if board[r][c] != expected[r][c] {
					match = false
				}
			}
		}
		if match {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n", name)
			fmt.Println("  Got:")
			for _, row := range board {
				fmt.Printf("    %s\n", string(row))
			}
			fmt.Println("  Expected:")
			for _, row := range expected {
				fmt.Printf("    %s\n", string(row))
			}
			failed++
		}
	}

	// Helper to create board from strings
	makeBoard := func(rows [9]string) [][]byte {
		board := make([][]byte, 9)
		for i, row := range rows {
			board[i] = []byte(row)
		}
		return board
	}

	// Test 1: LeetCode example
	test("LeetCode example",
		makeBoard([9]string{
			"53..7....",
			"6..195...",
			".98....6.",
			"8...6...3",
			"4..8.3..1",
			"7...2...6",
			".6....28.",
			"...419..5",
			"....8..79",
		}),
		makeBoard([9]string{
			"534678912",
			"672195348",
			"198342567",
			"859761423",
			"426853791",
			"713924856",
			"961537284",
			"287419635",
			"345286179",
		}))

	// Test 2: Almost solved — only one empty cell
	test("Almost solved",
		makeBoard([9]string{
			"534678912",
			"672195348",
			"198342567",
			"859761423",
			"426853791",
			"713924856",
			"961537284",
			"287419635",
			"34528617.",
		}),
		makeBoard([9]string{
			"534678912",
			"672195348",
			"198342567",
			"859761423",
			"426853791",
			"713924856",
			"961537284",
			"287419635",
			"345286179",
		}))

	// Test 3: Hard puzzle — requires deep backtracking
	test("Hard puzzle",
		makeBoard([9]string{
			"..9748...",
			"7........",
			".2.1.9...",
			"..7...24.",
			".64.1.59.",
			".98...3..",
			"...8.3.2.",
			"........6",
			"...2759..",
		}),
		makeBoard([9]string{
			"519748632",
			"783652419",
			"426139875",
			"357986241",
			"264317598",
			"198524367",
			"975863124",
			"832491756",
			"641275983",
		}))

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
