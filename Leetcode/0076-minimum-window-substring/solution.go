package main

import "fmt"

// ============================================================
// 0076. Minimum Window Substring
// https://leetcode.com/problems/minimum-window-substring/
// Difficulty: Hard
// Tags: Hash Table, String, Sliding Window
// ============================================================

// minWindow — Optimal Solution (Sliding Window with Counts)
// Approach: expand right until window contains all required chars,
//   then shrink left while still valid; track the minimum window.
// Time:  O(m + n)
// Space: O(σ) where σ ≤ 128 (ASCII)
func minWindow(s string, t string) string {
	if len(s) == 0 || len(t) == 0 {
		return ""
	}
	need := [128]int{}
	distinct := 0
	for i := 0; i < len(t); i++ {
		if need[t[i]] == 0 {
			distinct++
		}
		need[t[i]]++
	}
	have := 0
	window := [128]int{}
	l := 0
	bestLen := -1
	bestL := 0
	for r := 0; r < len(s); r++ {
		c := s[r]
		window[c]++
		if need[c] > 0 && window[c] == need[c] {
			have++
		}
		for have == distinct {
			if bestLen == -1 || r-l+1 < bestLen {
				bestLen = r - l + 1
				bestL = l
			}
			c2 := s[l]
			window[c2]--
			if need[c2] > 0 && window[c2] < need[c2] {
				have--
			}
			l++
		}
	}
	if bestLen == -1 {
		return ""
	}
	return s[bestL : bestL+bestLen]
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

	cases := []struct {
		name, s, t, want string
	}{
		{"Example 1", "ADOBECODEBANC", "ABC", "BANC"},
		{"Example 2", "a", "a", "a"},
		{"Example 3", "a", "aa", ""},
		{"Same string", "abc", "abc", "abc"},
		{"Reordered", "cba", "abc", "cba"},
		{"Duplicates in t", "aabbcc", "abc", "abbc"},
		{"Single char in t", "abcabc", "a", "a"},
		{"No window", "abc", "d", ""},
		{"Long t", "abcdef", "abcdefg", ""},
		{"All same char", "aaaa", "aa", "aa"},
		{"Mixed case", "AbCdEf", "AcF", ""},
		{"Larger", "this is a test string", "tist", "t stri"},
	}

	for _, c := range cases {
		test(c.name, minWindow(c.s, c.t), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
