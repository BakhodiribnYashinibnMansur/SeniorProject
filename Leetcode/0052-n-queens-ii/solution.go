package main

import "fmt"

// ============================================================
// 0052. N-Queens II
// https://leetcode.com/problems/n-queens-ii/
// Difficulty: Hard
// Tags: Backtracking
// ============================================================

// totalNQueens — Optimal Solution (Bitmask Backtracking)
// Approach: count placements via bitmask backtracking; each leaf adds 1.
// Time:  O(n!) practical
// Space: O(n) — recursion depth
func totalNQueens(n int) int {
	full := (1 << n) - 1
	var solve func(cols, d1, d2 int) int
	solve = func(cols, d1, d2 int) int {
		if cols == full {
			return 1
		}
		free := full & ^(cols | d1 | d2)
		total := 0
		for free != 0 {
			bit := free & -free
			total += solve(cols|bit, ((d1|bit)<<1)&full, (d2|bit)>>1)
			free &= free - 1
		}
		return total
	}
	return solve(0, 0, 0)
}

// totalNQueensSets — Backtracking with hash maps (clearer but slower)
// Time:  O(n!) practical
// Space: O(n)
func totalNQueensSets(n int) int {
	count := 0
	cols := make(map[int]bool)
	d1 := make(map[int]bool)
	d2 := make(map[int]bool)
	var backtrack func(r int)
	backtrack = func(r int) {
		if r == n {
			count++
			return
		}
		for c := 0; c < n; c++ {
			if cols[c] || d1[r-c] || d2[r+c] {
				continue
			}
			cols[c], d1[r-c], d2[r+c] = true, true, true
			backtrack(r + 1)
			delete(cols, c)
			delete(d1, r-c)
			delete(d2, r+c)
		}
	}
	backtrack(0)
	return count
}

// totalNQueensLookup — Precomputed (valid because n <= 9)
// Time:  O(1)
// Space: O(1)
func totalNQueensLookup(n int) int {
	table := []int{0, 1, 0, 0, 2, 10, 4, 40, 92, 352}
	return table[n]
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

	expected := []int{0, 1, 0, 0, 2, 10, 4, 40, 92, 352}

	fmt.Println("=== Bitmask backtracking ===")
	for n := 1; n <= 9; n++ {
		test(fmt.Sprintf("n=%d", n), totalNQueens(n), expected[n])
	}

	fmt.Println("\n=== Sets backtracking ===")
	for n := 1; n <= 9; n++ {
		test(fmt.Sprintf("Sets n=%d", n), totalNQueensSets(n), expected[n])
	}

	fmt.Println("\n=== Lookup table ===")
	for n := 1; n <= 9; n++ {
		test(fmt.Sprintf("Lookup n=%d", n), totalNQueensLookup(n), expected[n])
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
