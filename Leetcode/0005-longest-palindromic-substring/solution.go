package main

import (
	"fmt"
)

// ============================================================
// 0005. Longest Palindromic Substring
// https://leetcode.com/problems/longest-palindromic-substring/
// Difficulty: Medium
// Tags: String, Dynamic Programming, Two Pointers
// ============================================================

// longestPalindrome — Optimal Solution (Expand Around Center)
// Approach: For each index i, expand outward for both odd-length palindromes
//
//	(center at i) and even-length palindromes (center between i and i+1).
//	Track the start index and maximum length seen so far.
//
// Time:  O(n^2) — each of the 2n-1 centers expands at most n/2 times
// Space: O(1)   — only a few index variables, result is a slice of the input
func longestPalindrome(s string) string {
	if len(s) == 0 {
		return ""
	}

	// start and maxLen track the best palindrome found
	start, maxLen := 0, 1

	// expand returns the length of the longest palindrome centered at (l, r)
	expand := func(l, r int) {
		// Expand outward while characters match
		for l >= 0 && r < len(s) && s[l] == s[r] {
			// Update best if this palindrome is longer
			if r-l+1 > maxLen {
				start = l
				maxLen = r - l + 1
			}
			l--
			r++
		}
	}

	for i := 0; i < len(s); i++ {
		// Odd-length palindrome: single center character at i
		expand(i, i)
		// Even-length palindrome: center between i and i+1
		expand(i, i+1)
	}

	return s[start : start+maxLen]
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	// For palindrome problems, multiple valid answers may exist.
	// testAny accepts a slice of valid expected answers.
	testAny := func(name string, got string, valid []string) {
		for _, v := range valid {
			if got == v {
				fmt.Printf("✅ PASS: %s\n", name)
				passed++
				return
			}
		}
		fmt.Printf("❌ FAIL: %s\n  Got:      %q\n  Expected one of: %v\n", name, got, valid)
		failed++
	}

	// Standard test helper for single expected answer
	test := func(name string, got string, expected string) {
		testAny(name, got, []string{expected})
	}

	// Test 1: LeetCode Example 1 — "bab" or "aba" both valid
	testAny("Example 1 babad", longestPalindrome("babad"), []string{"bab", "aba"})

	// Test 2: LeetCode Example 2 — only "bb" is valid
	test("Example 2 cbbd", longestPalindrome("cbbd"), "bb")

	// Test 3: Single character — the character itself
	test("Single char", longestPalindrome("a"), "a")

	// Test 4: All same characters — entire string
	test("All same chars", longestPalindrome("aaaa"), "aaaa")

	// Test 5: No palindrome longer than 1 — return any single char
	testAny("All distinct", longestPalindrome("abcd"), []string{"a", "b", "c", "d"})

	// Test 6: Entire string is a palindrome
	test("Whole string palindrome", longestPalindrome("racecar"), "racecar")

	// Test 7: Even-length palindrome in the middle
	test("Even palindrome", longestPalindrome("abccba"), "abccba")

	// Test 8: Palindrome at the end
	test("Palindrome at end", longestPalindrome("xyzabba"), "abba")

	// Test 9: Palindrome at the beginning
	test("Palindrome at start", longestPalindrome("madam xyz"), "madam")

	// Test 10: Two-character string, not a palindrome
	testAny("Two chars not palindrome", longestPalindrome("ab"), []string{"a", "b"})

	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
