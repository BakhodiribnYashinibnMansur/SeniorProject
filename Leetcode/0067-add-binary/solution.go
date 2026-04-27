package main

import (
	"fmt"
	"strings"
)

// ============================================================
// 0067. Add Binary
// https://leetcode.com/problems/add-binary/
// Difficulty: Easy
// Tags: Math, String, Bit Manipulation, Simulation
// ============================================================

// addBinary — Optimal Solution (Walk + Carry)
// Approach: two pointers from the end, propagate carry bit.
// Time:  O(max(n, m))
// Space: O(max(n, m))
func addBinary(a string, b string) string {
	i, j := len(a)-1, len(b)-1
	carry := 0
	var sb strings.Builder
	for i >= 0 || j >= 0 || carry > 0 {
		s := carry
		if i >= 0 {
			s += int(a[i] - '0')
			i--
		}
		if j >= 0 {
			s += int(b[j] - '0')
			j--
		}
		sb.WriteByte(byte('0' + (s % 2)))
		carry = s / 2
	}
	r := []byte(sb.String())
	for l, h := 0, len(r)-1; l < h; l, h = l+1, h-1 {
		r[l], r[h] = r[h], r[l]
	}
	return string(r)
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name, got, expected string) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %q\n  Expected: %q\n", name, got, expected)
			failed++
		}
	}

	type tc struct {
		name string
		a, b string
		want string
	}
	cases := []tc{
		{"Example 1", "11", "1", "100"},
		{"Example 2", "1010", "1011", "10101"},
		{"Both zero", "0", "0", "0"},
		{"One zero", "0", "1", "1"},
		{"One zero left", "1", "0", "1"},
		{"Carry chain", "1", "1", "10"},
		{"Cascading carry", "111", "1", "1000"},
		{"Different lengths", "1", "111", "1000"},
		{"All ones", "1111", "1111", "11110"},
		{"Large", "100000000000000000000000000000000", "1", "100000000000000000000000000000001"},
		{"Result with leading carry", "11111111", "1", "100000000"},
		{"Equal length no carry", "1010", "0101", "1111"},
	}

	fmt.Println("=== Walk + Carry ===")
	for _, c := range cases {
		test(c.name, addBinary(c.a, c.b), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
