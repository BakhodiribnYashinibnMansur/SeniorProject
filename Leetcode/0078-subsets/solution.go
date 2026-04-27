package main

import (
	"fmt"
	"sort"
)

// ============================================================
// 0078. Subsets
// https://leetcode.com/problems/subsets/
// Difficulty: Medium
// Tags: Array, Backtracking, Bit Manipulation
// ============================================================

// subsets — Optimal Solution (Backtracking)
// Time:  O(n * 2^n)
// Space: O(n) recursion
func subsets(nums []int) [][]int {
	result := [][]int{}
	cur := []int{}
	var bt func(start int)
	bt = func(start int) {
		cp := make([]int, len(cur))
		copy(cp, cur)
		result = append(result, cp)
		for i := start; i < len(nums); i++ {
			cur = append(cur, nums[i])
			bt(i + 1)
			cur = cur[:len(cur)-1]
		}
	}
	bt(0)
	return result
}

func subsetsCascade(nums []int) [][]int {
	result := [][]int{{}}
	for _, x := range nums {
		size := len(result)
		for i := 0; i < size; i++ {
			cp := make([]int, len(result[i])+1)
			copy(cp, result[i])
			cp[len(cp)-1] = x
			result = append(result, cp)
		}
	}
	return result
}

func subsetsBits(nums []int) [][]int {
	n := len(nums)
	result := [][]int{}
	for mask := 0; mask < (1 << n); mask++ {
		sub := []int{}
		for i := 0; i < n; i++ {
			if (mask>>i)&1 == 1 {
				sub = append(sub, nums[i])
			}
		}
		result = append(result, sub)
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func canon(out [][]int) [][]int {
	cp := make([][]int, len(out))
	for i, s := range out {
		c := append([]int{}, s...)
		sort.Ints(c)
		cp[i] = c
	}
	sort.Slice(cp, func(i, j int) bool {
		for k := 0; k < len(cp[i]) && k < len(cp[j]); k++ {
			if cp[i][k] != cp[j][k] {
				return cp[i][k] < cp[j][k]
			}
		}
		return len(cp[i]) < len(cp[j])
	})
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
	test := func(name string, got, expected [][]int) {
		if equal(canon(got), canon(expected)) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n", name)
			failed++
		}
	}

	cases := []struct {
		name string
		in   []int
		want [][]int
	}{
		{"Example 1", []int{1, 2, 3}, [][]int{
			{}, {1}, {2}, {3}, {1, 2}, {1, 3}, {2, 3}, {1, 2, 3}}},
		{"Example 2", []int{0}, [][]int{{}, {0}}},
		{"Two", []int{4, 5}, [][]int{{}, {4}, {5}, {4, 5}}},
		{"Negatives", []int{-1, 2}, [][]int{{}, {-1}, {2}, {-1, 2}}},
	}

	fmt.Println("=== Backtracking ===")
	for _, c := range cases {
		test(c.name, subsets(c.in), c.want)
	}
	fmt.Println("\n=== Cascade ===")
	for _, c := range cases {
		test("Casc "+c.name, subsetsCascade(c.in), c.want)
	}
	fmt.Println("\n=== Bits ===")
	for _, c := range cases {
		test("Bits "+c.name, subsetsBits(c.in), c.want)
	}

	// Count check
	for _, sz := range []int{5, 8, 10} {
		nums := make([]int, sz)
		for i := range nums {
			nums[i] = i
		}
		got := len(subsets(nums))
		want := 1 << sz
		if got == want {
			fmt.Printf("PASS: count n=%d → %d subsets\n", sz, got)
			passed++
		} else {
			fmt.Printf("FAIL: count n=%d → got %d want %d\n", sz, got, want)
			failed++
		}
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
