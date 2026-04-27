package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0051. N-Queens
// https://leetcode.com/problems/n-queens/
// Difficulty: Hard
// Tags: Array, Backtracking
// ============================================================

// solveNQueens — Optimal Solution (Backtracking with Sets)
// Approach: Place one queen per row, track column / diagonal conflicts
//   in three sets and prune as early as possible.
// Time:  O(n!) practical (much smaller after pruning)
// Space: O(n) — recursion depth + three sets of size up to n
func solveNQueens(n int) [][]string {
	var result [][]string
	queens := make([]int, n)
	cols := make(map[int]bool)
	diag1 := make(map[int]bool) // r - c (the '\' diagonal)
	diag2 := make(map[int]bool) // r + c (the '/' diagonal)

	var backtrack func(r int)
	backtrack = func(r int) {
		if r == n {
			board := make([]string, n)
			for i, c := range queens {
				row := make([]byte, n)
				for j := range row {
					row[j] = '.'
				}
				row[c] = 'Q'
				board[i] = string(row)
			}
			result = append(result, board)
			return
		}
		for c := 0; c < n; c++ {
			if cols[c] || diag1[r-c] || diag2[r+c] {
				continue
			}
			queens[r] = c
			cols[c], diag1[r-c], diag2[r+c] = true, true, true
			backtrack(r + 1)
			delete(cols, c)
			delete(diag1, r-c)
			delete(diag2, r+c)
		}
	}
	backtrack(0)
	return result
}

// solveNQueensBitmask — Bitmask backtracking (fastest in practice)
// Time:  O(n!) practical
// Space: O(n)
func solveNQueensBitmask(n int) [][]string {
	var result [][]string
	queens := make([]int, n)
	full := (1 << n) - 1

	var backtrack func(r, cols, d1, d2 int)
	backtrack = func(r, cols, d1, d2 int) {
		if r == n {
			board := make([]string, n)
			for i, c := range queens {
				row := make([]byte, n)
				for j := range row {
					row[j] = '.'
				}
				row[c] = 'Q'
				board[i] = string(row)
			}
			result = append(result, board)
			return
		}
		free := full & ^(cols | d1 | d2)
		for free != 0 {
			bit := free & -free
			c := trailingZeros(bit)
			queens[r] = c
			backtrack(r+1, cols|bit, ((d1|bit)<<1)&full, (d2|bit)>>1)
			free &= free - 1
		}
	}
	backtrack(0, 0, 0, 0)
	return result
}

func trailingZeros(x int) int {
	n := 0
	for x&1 == 0 {
		x >>= 1
		n++
	}
	return n
}

// ============================================================
// Test Cases
// ============================================================

// canonicalize sorts each solution list to make comparison order-independent.
func canonicalize(boards [][]string) [][]string {
	out := make([][]string, len(boards))
	for i, b := range boards {
		cp := make([]string, len(b))
		copy(cp, b)
		out[i] = cp
	}
	sort.Slice(out, func(i, j int) bool {
		for k := 0; k < len(out[i]) && k < len(out[j]); k++ {
			if out[i][k] != out[j][k] {
				return out[i][k] < out[j][k]
			}
		}
		return len(out[i]) < len(out[j])
	})
	return out
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected [][]string) {
		g := canonicalize(got)
		e := canonicalize(expected)
		if reflect.DeepEqual(g, e) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, g, e)
			failed++
		}
	}

	testCount := func(name string, got [][]string, expected int) {
		if len(got) == expected {
			fmt.Printf("PASS: %s (count=%d)\n", name, expected)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got count:      %d\n  Expected count: %d\n", name, len(got), expected)
			failed++
		}
	}

	fmt.Println("=== Backtracking with Sets ===")

	// Test 1: n = 1 — trivial single cell
	test("n=1", solveNQueens(1), [][]string{{"Q"}})

	// Test 2: n = 2 — no solution
	test("n=2 (no solution)", solveNQueens(2), [][]string{})

	// Test 3: n = 3 — no solution
	test("n=3 (no solution)", solveNQueens(3), [][]string{})

	// Test 4: n = 4 — 2 solutions
	test("n=4", solveNQueens(4), [][]string{
		{".Q..", "...Q", "Q...", "..Q."},
		{"..Q.", "Q...", "...Q", ".Q.."},
	})

	// Test 5: n = 5 — 10 solutions
	testCount("n=5 count", solveNQueens(5), 10)

	// Test 6: n = 6 — 4 solutions
	testCount("n=6 count", solveNQueens(6), 4)

	// Test 7: n = 7 — 40 solutions
	testCount("n=7 count", solveNQueens(7), 40)

	// Test 8: n = 8 (classic) — 92 solutions
	testCount("n=8 count (classic)", solveNQueens(8), 92)

	// Test 9: n = 9 — 352 solutions
	testCount("n=9 count", solveNQueens(9), 352)

	fmt.Println("\n=== Backtracking with Bitmasks ===")

	// Test 10: bitmask agrees on n=4
	test("Bitmask n=4", solveNQueensBitmask(4), [][]string{
		{".Q..", "...Q", "Q...", "..Q."},
		{"..Q.", "Q...", "...Q", ".Q.."},
	})

	// Test 11: bitmask matches count for n=8
	testCount("Bitmask n=8 count", solveNQueensBitmask(8), 92)

	// Test 12: bitmask matches count for n=9
	testCount("Bitmask n=9 count", solveNQueensBitmask(9), 352)

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
