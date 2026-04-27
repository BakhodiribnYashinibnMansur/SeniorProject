package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0077. Combinations
// https://leetcode.com/problems/combinations/
// Difficulty: Medium
// Tags: Backtracking
// ============================================================

// combine — Optimal Solution (Backtracking with Pruning)
// Time:  O(C(n, k) * k)
// Space: O(k) recursion
func combine(n int, k int) [][]int {
	result := [][]int{}
	cur := []int{}
	var bt func(start int)
	bt = func(start int) {
		if len(cur) == k {
			cp := make([]int, k)
			copy(cp, cur)
			result = append(result, cp)
			return
		}
		need := k - len(cur)
		for v := start; v <= n-need+1; v++ {
			cur = append(cur, v)
			bt(v + 1)
			cur = cur[:len(cur)-1]
		}
	}
	bt(1)
	return result
}

func combineIter(n, k int) [][]int {
	cur := make([]int, k)
	for i := 0; i < k; i++ {
		cur[i] = i + 1
	}
	cp := func() []int { c := make([]int, k); copy(c, cur); return c }
	result := [][]int{cp()}
	for {
		i := k - 1
		for i >= 0 && cur[i] == n-k+1+i {
			i--
		}
		if i < 0 {
			break
		}
		cur[i]++
		for j := i + 1; j < k; j++ {
			cur[j] = cur[j-1] + 1
		}
		result = append(result, cp())
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func sortLexico(out [][]int) [][]int {
	// Sort lists lexicographically (each inner list is already ascending by construction)
	for i := 0; i < len(out); i++ {
		for j := i + 1; j < len(out); j++ {
			if !lex(out[i], out[j]) {
				out[i], out[j] = out[j], out[i]
			}
		}
	}
	return out
}

func lex(a, b []int) bool {
	for i := 0; i < len(a) && i < len(b); i++ {
		if a[i] != b[i] {
			return a[i] < b[i]
		}
	}
	return len(a) <= len(b)
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected [][]int) {
		g := sortLexico(got)
		e := sortLexico(expected)
		if reflect.DeepEqual(g, e) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, g, e)
			failed++
		}
	}

	cases := []struct {
		name string
		n, k int
		want [][]int
	}{
		{"Example 1", 4, 2, [][]int{{1, 2}, {1, 3}, {1, 4}, {2, 3}, {2, 4}, {3, 4}}},
		{"Example 2", 1, 1, [][]int{{1}}},
		{"k = n", 3, 3, [][]int{{1, 2, 3}}},
		{"k = 1", 3, 1, [][]int{{1}, {2}, {3}}},
		{"n=4 k=3", 4, 3, [][]int{{1, 2, 3}, {1, 2, 4}, {1, 3, 4}, {2, 3, 4}}},
	}

	fmt.Println("=== Backtracking + Pruning ===")
	for _, c := range cases {
		test(c.name, combine(c.n, c.k), c.want)
	}
	fmt.Println("\n=== Iterative ===")
	for _, c := range cases {
		test("Iter "+c.name, combineIter(c.n, c.k), c.want)
	}

	// Count check for larger inputs
	expCount := func(n, k int) int {
		if k > n-k {
			k = n - k
		}
		v := 1
		for i := 0; i < k; i++ {
			v = v * (n - i) / (i + 1)
		}
		return v
	}
	for _, p := range [][]int{{5, 3}, {10, 5}, {20, 10}} {
		got := len(combine(p[0], p[1]))
		want := expCount(p[0], p[1])
		if got == want {
			fmt.Printf("PASS: count C(%d,%d) = %d\n", p[0], p[1], got)
			passed++
		} else {
			fmt.Printf("FAIL: count C(%d,%d) got %d, want %d\n", p[0], p[1], got, want)
			failed++
		}
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
