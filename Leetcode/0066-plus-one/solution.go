package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0066. Plus One
// https://leetcode.com/problems/plus-one/
// Difficulty: Easy
// Tags: Array, Math
// ============================================================

// plusOne — Optimal Solution (Walk From End with Carry)
// Approach: scan right-to-left, propagate carry. Prepend 1 if carry remains.
// Time:  O(n)
// Space: O(1) amortized (O(n) only when prepending)
func plusOne(digits []int) []int {
	carry := 1
	for i := len(digits) - 1; i >= 0 && carry > 0; i-- {
		s := digits[i] + carry
		digits[i] = s % 10
		carry = s / 10
	}
	if carry > 0 {
		return append([]int{carry}, digits...)
	}
	return digits
}

// plusOneEarly — Find first non-9 from right, bump it, zero out the rest
// Time:  O(n)
// Space: O(1) / O(n)
func plusOneEarly(digits []int) []int {
	n := len(digits)
	for i := n - 1; i >= 0; i-- {
		if digits[i] != 9 {
			digits[i]++
			for j := i + 1; j < n; j++ {
				digits[j] = 0
			}
			return digits
		}
	}
	out := make([]int, n+1)
	out[0] = 1
	return out
}

// ============================================================
// Test Cases
// ============================================================

func cloneSlice(a []int) []int {
	b := make([]int, len(a))
	copy(b, a)
	return b
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
		name string
		in   []int
		want []int
	}
	cases := []tc{
		{"Example 1", []int{1, 2, 3}, []int{1, 2, 4}},
		{"Example 2", []int{4, 3, 2, 1}, []int{4, 3, 2, 2}},
		{"Example 3", []int{9}, []int{1, 0}},
		{"Single zero", []int{0}, []int{1}},
		{"All nines 3", []int{9, 9, 9}, []int{1, 0, 0, 0}},
		{"All nines 5", []int{9, 9, 9, 9, 9}, []int{1, 0, 0, 0, 0, 0}},
		{"Trailing zeros", []int{1, 0, 0}, []int{1, 0, 1}},
		{"Trailing nines", []int{1, 9, 9}, []int{2, 0, 0}},
		{"Mid nines", []int{2, 9, 9, 1}, []int{2, 9, 9, 2}},
		{"Long number", []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}, []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 1}},
	}

	fmt.Println("=== Walk + Carry ===")
	for _, c := range cases {
		test(c.name, plusOne(cloneSlice(c.in)), c.want)
	}
	fmt.Println("\n=== Early Exit ===")
	for _, c := range cases {
		test("Early "+c.name, plusOneEarly(cloneSlice(c.in)), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
