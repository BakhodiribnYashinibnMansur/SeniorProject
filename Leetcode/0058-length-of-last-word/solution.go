package main

import (
	"fmt"
	"strings"
)

// ============================================================
// 0058. Length of Last Word
// https://leetcode.com/problems/length-of-last-word/
// Difficulty: Easy
// Tags: String
// ============================================================

// lengthOfLastWord — Optimal Solution (Reverse Scan)
// Approach: skip trailing spaces, then count non-spaces until a space.
// Time:  O(length of last word + trailing spaces)
// Space: O(1)
func lengthOfLastWord(s string) int {
	i := len(s) - 1
	for i >= 0 && s[i] == ' ' {
		i--
	}
	count := 0
	for i >= 0 && s[i] != ' ' {
		count++
		i--
	}
	return count
}

// lengthOfLastWordSplit — strings.Fields collapses runs of whitespace
// Time:  O(n)
// Space: O(n)
func lengthOfLastWordSplit(s string) int {
	parts := strings.Fields(s)
	if len(parts) == 0 {
		return 0
	}
	return len(parts[len(parts)-1])
}

// lengthOfLastWordTrim — Trim + last space index
// Time:  O(n)
// Space: O(n) (trimmed copy)
func lengthOfLastWordTrim(s string) int {
	t := strings.TrimRight(s, " ")
	last := strings.LastIndex(t, " ")
	return len(t) - last - 1
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

	type tc struct {
		name     string
		s        string
		expected int
	}
	cases := []tc{
		{"Example 1", "Hello World", 5},
		{"Example 2", "   fly me   to   the moon  ", 4},
		{"Example 3", "luffy is still joyboy", 6},
		{"Single word", "hello", 5},
		{"Single char", "a", 1},
		{"Trailing spaces", "hi   ", 2},
		{"Leading spaces", "   hi", 2},
		{"Multiple internal spaces", "a    b", 1},
		{"Pad both sides", "   abc   ", 3},
		{"Long suffix", "a aaaaaa", 6},
		{"Same words", "day day", 3},
	}

	fmt.Println("=== Reverse Scan ===")
	for _, c := range cases {
		test(c.name, lengthOfLastWord(c.s), c.expected)
	}
	fmt.Println("\n=== Split ===")
	for _, c := range cases {
		test("Split "+c.name, lengthOfLastWordSplit(c.s), c.expected)
	}
	fmt.Println("\n=== Trim + LastSpace ===")
	for _, c := range cases {
		test("Trim "+c.name, lengthOfLastWordTrim(c.s), c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
