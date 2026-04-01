package main

import "fmt"

// ============================================================
// 0028. Find the Index of the First Occurrence in a String
// https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/
// Difficulty: Easy
// Tags: Two Pointers, String, String Matching
// ============================================================

// strStr1 — Brute Force (Sliding Window)
// Approach: Try every starting position, compare character by character
// Time:  O(n * m) — n = len(haystack), m = len(needle)
// Space: O(1) — only uses index variables
func strStr1(haystack string, needle string) int {
	n, m := len(haystack), len(needle)

	// Try each valid starting position
	for i := 0; i <= n-m; i++ {
		match := true
		for j := 0; j < m; j++ {
			if haystack[i+j] != needle[j] {
				match = false
				break
			}
		}
		if match {
			return i
		}
	}

	return -1
}

// strStr2 — KMP Algorithm
// Approach: Preprocess needle to build LPS array, search without backtracking
// Time:  O(n + m) — linear in total input size
// Space: O(m) — LPS array for the needle
func strStr2(haystack string, needle string) int {
	n, m := len(haystack), len(needle)
	if m > n {
		return -1
	}

	// Step 1: Build LPS (Longest Proper Prefix Suffix) array
	lps := make([]int, m)
	length := 0
	i := 1
	for i < m {
		if needle[i] == needle[length] {
			length++
			lps[i] = length
			i++
		} else if length > 0 {
			length = lps[length-1]
		} else {
			lps[i] = 0
			i++
		}
	}

	// Step 2: Search using LPS array
	i = 0
	j := 0
	for i < n {
		if haystack[i] == needle[j] {
			i++
			j++
		}
		if j == m {
			return i - j
		} else if i < n && haystack[i] != needle[j] {
			if j > 0 {
				j = lps[j-1]
			} else {
				i++
			}
		}
	}

	return -1
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %d\n  Expected: %d\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Approach 1: Brute Force (Sliding Window) ===")

	// Test 1: LeetCode Example 1 — needle at the start
	test("Example 1: sadbutsad/sad", strStr1("sadbutsad", "sad"), 0)

	// Test 2: LeetCode Example 2 — needle not found
	test("Example 2: leetcode/leeto", strStr1("leetcode", "leeto"), -1)

	// Test 3: Needle at the end
	test("Needle at end: hello/llo", strStr1("hello", "llo"), 2)

	// Test 4: Needle equals haystack
	test("Needle equals haystack: abc/abc", strStr1("abc", "abc"), 0)

	// Test 5: Needle longer than haystack
	test("Needle longer: ab/abc", strStr1("ab", "abc"), -1)

	// Test 6: Single character match
	test("Single char match: a/a", strStr1("a", "a"), 0)

	// Test 7: Single character no match
	test("Single char no match: a/b", strStr1("a", "b"), -1)

	// Test 8: Repeated characters
	test("Repeated chars: aaaa/aa", strStr1("aaaa", "aa"), 0)

	// Test 9: Tricky partial match — mississippi
	test("Mississippi: mississippi/issip", strStr1("mississippi", "issip"), 4)

	// Test 10: Needle in the middle
	test("Middle match: abcdef/cde", strStr1("abcdef", "cde"), 2)

	fmt.Println()
	fmt.Println("=== Approach 2: KMP Algorithm ===")

	// Test 1: LeetCode Example 1 — needle at the start
	test("Example 1: sadbutsad/sad", strStr2("sadbutsad", "sad"), 0)

	// Test 2: LeetCode Example 2 — needle not found
	test("Example 2: leetcode/leeto", strStr2("leetcode", "leeto"), -1)

	// Test 3: Needle at the end
	test("Needle at end: hello/llo", strStr2("hello", "llo"), 2)

	// Test 4: Needle equals haystack
	test("Needle equals haystack: abc/abc", strStr2("abc", "abc"), 0)

	// Test 5: Needle longer than haystack
	test("Needle longer: ab/abc", strStr2("ab", "abc"), -1)

	// Test 6: Single character match
	test("Single char match: a/a", strStr2("a", "a"), 0)

	// Test 7: Single character no match
	test("Single char no match: a/b", strStr2("a", "b"), -1)

	// Test 8: Repeated characters
	test("Repeated chars: aaaa/aa", strStr2("aaaa", "aa"), 0)

	// Test 9: Tricky partial match — mississippi
	test("Mississippi: mississippi/issip", strStr2("mississippi", "issip"), 4)

	// Test 10: KMP advantage case — repeated patterns
	test("KMP advantage: aaabaaab/aaab", strStr2("aaabaaab", "aaab"), 0)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
