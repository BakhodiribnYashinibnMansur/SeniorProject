package main

import "fmt"

// ============================================================
// 0010. Regular Expression Matching
// https://leetcode.com/problems/regular-expression-matching/
// Difficulty: Hard
// Tags: String, Dynamic Programming, Recursion
// ============================================================

// isMatchRecursive — Approach 1 (Recursion)
// Time:  O(2^(m+n)) worst case — exponential due to '*' branching
// Space: O(m+n)     — recursion call stack depth
func isMatchRecursive(s string, p string) bool {
	// Base case: pattern is empty
	if len(p) == 0 {
		return len(s) == 0
	}

	// Does the first character of s match the first character of p?
	// '.' matches any single character
	firstMatch := len(s) > 0 && (p[0] == '.' || p[0] == s[0])

	// Handle '*': it can mean 0 occurrences or 1+ occurrences
	if len(p) >= 2 && p[1] == '*' {
		// Option A: use '*' as 0 occurrences — skip "x*" in pattern
		// Option B: use '*' as 1+ occurrences — consume one char from s (if first matches)
		return isMatchRecursive(s, p[2:]) ||
			(firstMatch && isMatchRecursive(s[1:], p))
	}

	// No '*': first chars must match, then recurse on the rest
	return firstMatch && isMatchRecursive(s[1:], p[1:])
}

// isMatch — Optimal Solution (Bottom-up Dynamic Programming)
// Time:  O(m * n) — fill an (m+1) × (n+1) DP table
// Space: O(m * n) — the DP table itself
// where m = len(s), n = len(p)
func isMatch(s string, p string) bool {
	m, n := len(s), len(p)

	// dp[i][j] = true if s[0..i-1] matches p[0..j-1]
	dp := make([][]bool, m+1)
	for i := range dp {
		dp[i] = make([]bool, n+1)
	}

	// Base case: empty string matches empty pattern
	dp[0][0] = true

	// Base case: empty string vs pattern with '*'
	// e.g., "a*b*c*" can match "" by using each x* as 0 occurrences
	for j := 2; j <= n; j++ {
		if p[j-1] == '*' {
			dp[0][j] = dp[0][j-2]
		}
	}

	// Fill the DP table
	for i := 1; i <= m; i++ {
		for j := 1; j <= n; j++ {
			if p[j-1] == '*' {
				// '*' used as 0 occurrences: ignore the "x*" pair in pattern
				dp[i][j] = dp[i][j-2]

				// '*' used as 1+ occurrences: s[i-1] must match p[j-2]
				// p[j-2] is the character before '*'
				if p[j-2] == '.' || p[j-2] == s[i-1] {
					dp[i][j] = dp[i][j] || dp[i-1][j]
				}
			} else if p[j-1] == '.' || p[j-1] == s[i-1] {
				// Characters match (exact or '.' wildcard)
				dp[i][j] = dp[i-1][j-1]
			}
			// else: characters do not match → dp[i][j] stays false
		}
	}

	return dp[m][n]
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

	// Test 2: '*' matches multiple of preceding element
	test(`s="aa" p="a*" → true`, isMatch("aa", "a*"), true)

	// Test 3: ".*" matches any sequence
	test(`s="ab" p=".*" → true`, isMatch("ab", ".*"), true)

	// Test 4: Mixed '*' with zero occurrences
	test(`s="aab" p="c*a*b" → true`, isMatch("aab", "c*a*b"), true)

	// Test 5: No match
	test(`s="mississippi" p="mis*is*p*." → false`, isMatch("mississippi", "mis*is*p*."), false)

	// Test 6: Empty string matches "a*"
	test(`s="" p="a*" → true`, isMatch("", "a*"), true)

	// Test 7: Empty string matches "a*b*"
	test(`s="" p="a*b*" → true`, isMatch("", "a*b*"), true)

	// Test 8: Dot matches any character
	test(`s="ab" p=".." → true`, isMatch("ab", ".."), true)

	// Test 9: Single character exact match
	test(`s="a" p="a" → true`, isMatch("a", "a"), true)

	// Test 10: Pattern must cover full string
	test(`s="aaa" p="a*a" → true`, isMatch("aaa", "a*a"), true)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
