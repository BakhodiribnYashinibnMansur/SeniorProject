package main

import "fmt"

// ============================================================
// 0036. Valid Sudoku
// https://leetcode.com/problems/valid-sudoku/
// Difficulty: Medium
// Tags: Array, Hash Table, Matrix
// ============================================================

// isValidSudoku — Optimal Solution (Array-based Validation)
// Approach: Single pass with boolean arrays for rows, cols, and boxes
// Time:  O(1) — always 81 cells (9x9 fixed board)
// Space: O(1) — three 9x9 boolean arrays (fixed size)
func isValidSudoku(board [][]byte) bool {
	var rows, cols, boxes [9][9]bool

	for r := 0; r < 9; r++ {
		for c := 0; c < 9; c++ {
			if board[r][c] == '.' {
				continue
			}

			d := board[r][c] - '1'
			box := (r/3)*3 + c/3

			if rows[r][d] || cols[c][d] || boxes[box][d] {
				return false
			}

			rows[r][d] = true
			cols[c][d] = true
			boxes[box][d] = true
		}
	}

	return true
}

// isValidSudokuHashSet — Hash Set approach
// Approach: Use maps to track seen digits per row, column, and box
// Time:  O(1) — always 81 cells
// Space: O(1) — 27 maps, each at most 9 entries
func isValidSudokuHashSet(board [][]byte) bool {
	var rows, cols, boxes [9]map[byte]bool

	for i := 0; i < 9; i++ {
		rows[i] = make(map[byte]bool)
		cols[i] = make(map[byte]bool)
		boxes[i] = make(map[byte]bool)
	}

	for r := 0; r < 9; r++ {
		for c := 0; c < 9; c++ {
			val := board[r][c]
			if val == '.' {
				continue
			}

			box := (r/3)*3 + c/3

			if rows[r][val] || cols[c][val] || boxes[box][val] {
				return false
			}

			rows[r][val] = true
			cols[c][val] = true
			boxes[box][val] = true
		}
	}

	return true
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Helper to create board from strings
	makeBoard := func(rows ...string) [][]byte {
		board := make([][]byte, 9)
		for i, row := range rows {
			board[i] = []byte(row)
		}
		return board
	}

	// Valid board (LeetCode Example 1)
	validBoard := makeBoard(
		"53..7....",
		"6..195...",
		".98....6.",
		"8...6...3",
		"4..8.3..1",
		"7...2...6",
		".6....28.",
		"...419..5",
		"....8..79",
	)

	// Invalid board (LeetCode Example 2) — duplicate 8 in column 0 and top-left box
	invalidBoard := makeBoard(
		"83..7....",
		"6..195...",
		".98....6.",
		"8...6...3",
		"4..8.3..1",
		"7...2...6",
		".6....28.",
		"...419..5",
		"....8..79",
	)

	// Board with duplicate in a row
	rowDupBoard := makeBoard(
		"53..7...5", // 5 appears twice in row 0
		"6..195...",
		".98....6.",
		"8...6...3",
		"4..8.3..1",
		"7...2...6",
		".6....28.",
		"...419..5",
		"....8..79",
	)

	// Board with duplicate in a column
	colDupBoard := makeBoard(
		"53..7....",
		"6..195...",
		".98....6.",
		"8...6...3",
		"4..8.3..1",
		"7...2...6",
		".6....28.",
		"...419..5",
		"5...8..79", // 5 in col 0 (same as row 0)
	)

	// Almost empty board
	emptyBoard := makeBoard(
		".........",
		".........",
		".........",
		".........",
		".........",
		".........",
		".........",
		".........",
		".........",
	)

	// Single value board
	singleBoard := makeBoard(
		".........",
		".........",
		".........",
		".........",
		"....5....",
		".........",
		".........",
		".........",
		".........",
	)

	fmt.Println("=== Array-based Validation (Optimal) ===")

	test("Example 1 (valid)", isValidSudoku(validBoard), true)
	test("Example 2 (invalid box)", isValidSudoku(invalidBoard), false)
	test("Row duplicate", isValidSudoku(rowDupBoard), false)
	test("Column duplicate", isValidSudoku(colDupBoard), false)
	test("Almost empty board", isValidSudoku(emptyBoard), true)
	test("Single value board", isValidSudoku(singleBoard), true)

	fmt.Println("\n=== Hash Set Approach ===")

	// Need fresh boards since byte slices were consumed
	validBoard2 := makeBoard(
		"53..7....",
		"6..195...",
		".98....6.",
		"8...6...3",
		"4..8.3..1",
		"7...2...6",
		".6....28.",
		"...419..5",
		"....8..79",
	)
	invalidBoard2 := makeBoard(
		"83..7....",
		"6..195...",
		".98....6.",
		"8...6...3",
		"4..8.3..1",
		"7...2...6",
		".6....28.",
		"...419..5",
		"....8..79",
	)

	test("HS: Example 1 (valid)", isValidSudokuHashSet(validBoard2), true)
	test("HS: Example 2 (invalid box)", isValidSudokuHashSet(invalidBoard2), false)

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
