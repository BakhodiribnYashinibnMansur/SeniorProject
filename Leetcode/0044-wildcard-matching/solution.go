package main

import "fmt"

// ============================================================
// 0044. Wildcard Matching
// https://leetcode.com/problems/wildcard-matching/
// Difficulty: Hard
// Tags: String, Dynamic Programming, Greedy, Recursion
// ============================================================

// isMatchDP — Approach 1 (Bottom-up Dynamic Programming)
// Time:  O(m * n) — fill an (m+1) x (n+1) DP table
// Space: O(m * n) — the DP table itself
// where m = len(s), n = len(p)
func isMatchDP(s string, p string) bool {
	m, n := len(s), len(p)

	// dp[i][j] = true if s[0..i-1] matches p[0..j-1]
	dp := make([][]bool, m+1)
	for i := range dp {
		dp[i] = make([]bool, n+1)
	}

	// Base case: empty string matches empty pattern
	dp[0][0] = true

	// Base case: empty string vs pattern with leading '*'s
	// e.g., "***" can match "" since each '*' matches empty
	for j := 1; j <= n; j++ {
		if p[j-1] == '*' {
			dp[0][j] = dp[0][j-1]
		}
	}

	// Fill the DP table
	for i := 1; i <= m; i++ {
		for j := 1; j <= n; j++ {
			if p[j-1] == '*' {
				// '*' matches empty (dp[i][j-1]) OR one more char (dp[i-1][j])
				dp[i][j] = dp[i][j-1] || dp[i-1][j]
			} else if p[j-1] == '?' || p[j-1] == s[i-1] {
				// Exact match or '?' wildcard
				dp[i][j] = dp[i-1][j-1]
			}
			// else: dp[i][j] stays false
		}
	}

	return dp[m][n]
}

// isMatch — Optimal Solution (Greedy with Backtracking)
// Time:  O(m * n) worst case, often O(m + n) in practice
// Space: O(1)     — only a few pointer variables
// where m = len(s), n = len(p)
func isMatch(s string, p string) bool {
	sIdx, pIdx := 0, 0
	starIdx, matchIdx := -1, 0

	for sIdx < len(s) {
		// Case 1: exact match or '?' matches any single char
		if pIdx < len(p) && (p[pIdx] == s[sIdx] || p[pIdx] == '?') {
			sIdx++
			pIdx++
		} else if pIdx < len(p) && p[pIdx] == '*' {
			// Case 2: '*' — record position, try matching 0 chars first
			starIdx = pIdx
			matchIdx = sIdx
			pIdx++
		} else if starIdx != -1 {
			// Case 3: mismatch — backtrack to last '*', match one more char
			matchIdx++
			sIdx = matchIdx
			pIdx = starIdx + 1
		} else {
			// Case 4: no match possible
			return false
		}
	}

	// Skip any remaining '*'s in pattern (they match empty)
	for pIdx < len(p) && p[pIdx] == '*' {
		pIdx++
	}

	return pIdx == len(p)
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Pattern shorter, no wildcard — no match
	test(`s="aa" p="a" → false`, isMatch("aa", "a"), false)

	// Test 2: '*' matches any sequence
	test(`s="aa" p="*" → true`, isMatch("aa", "*"), true)

	// Test 3: '?' matches single char, but second char doesn't match
	test(`s="cb" p="?a" → false`, isMatch("cb", "?a"), false)

	// Test 4: '*' matches subsequence in the middle
	test(`s="adceb" p="*a*b" → true`, isMatch("adceb", "*a*b"), true)

	// Test 5: Cannot match — 'c' at end instead of 'b'
	test(`s="acdcb" p="a*c?b" → false`, isMatch("acdcb", "a*c?b"), false)

	// Test 6: Both empty
	test(`s="" p="" → true`, isMatch("", ""), true)

	// Test 7: Empty string matches "*"
	test(`s="" p="*" → true`, isMatch("", "*"), true)

	// Test 8: Empty string, non-empty pattern without '*'
	test(`s="" p="?" → false`, isMatch("", "?"), false)

	// Test 9: Exact match
	test(`s="abc" p="abc" → true`, isMatch("abc", "abc"), true)

	// Test 10: '?' matches each character
	test(`s="abc" p="???" → true`, isMatch("abc", "???"), true)

	// Test 11: Multiple '*' same as single
	test(`s="abc" p="a***c" → true`, isMatch("abc", "a***c"), true)

	// Test 12: Star at end matches remaining
	test(`s="abcdef" p="abc*" → true`, isMatch("abcdef", "abc*"), true)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
