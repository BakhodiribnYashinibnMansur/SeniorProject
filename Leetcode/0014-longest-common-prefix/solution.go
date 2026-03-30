package main

import "fmt"

// ============================================================
// 0014. Longest Common Prefix
// https://leetcode.com/problems/longest-common-prefix/
// Difficulty: Easy
// Tags: String, Trie
// ============================================================

// longestCommonPrefix1 — Vertical Scanning
// Approach: Compare characters column by column across all strings
// Time:  O(S) — where S is the sum of all characters in all strings
// Space: O(1) — only uses a few variables
func longestCommonPrefix1(strs []string) string {
	if len(strs) == 0 {
		return ""
	}

	// Use the first string as reference
	// Compare each character position across all strings
	for i := 0; i < len(strs[0]); i++ {
		ch := strs[0][i]

		for j := 1; j < len(strs); j++ {
			// If we've reached the end of any string, or characters don't match
			if i >= len(strs[j]) || strs[j][i] != ch {
				return strs[0][:i]
			}
		}
	}

	// The entire first string is the common prefix
	return strs[0]
}

// longestCommonPrefix2 — Horizontal Scanning
// Approach: Start with first string as prefix, reduce it pairwise
// Time:  O(S) — where S is the sum of all characters in all strings
// Space: O(1) — modifies prefix in place using slicing
func longestCommonPrefix2(strs []string) string {
	if len(strs) == 0 {
		return ""
	}

	// Start with the first string as the prefix
	prefix := strs[0]

	// Compare prefix with each subsequent string
	for i := 1; i < len(strs); i++ {
		// Shrink prefix until it matches the beginning of strs[i]
		for !startsWith(strs[i], prefix) {
			prefix = prefix[:len(prefix)-1]
			if len(prefix) == 0 {
				return ""
			}
		}
	}

	return prefix
}

// startsWith checks if s starts with the given prefix
func startsWith(s, prefix string) bool {
	if len(prefix) > len(s) {
		return false
	}
	return s[:len(prefix)] == prefix
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected string) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %q\n  Expected: %q\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Approach 1: Vertical Scanning ===")

	// Test 1: LeetCode Example 1 — common prefix "fl"
	test("Example 1: flower/flow/flight", longestCommonPrefix1([]string{"flower", "flow", "flight"}), "fl")

	// Test 2: LeetCode Example 2 — no common prefix
	test("Example 2: dog/racecar/car", longestCommonPrefix1([]string{"dog", "racecar", "car"}), "")

	// Test 3: Single string
	test("Single string", longestCommonPrefix1([]string{"alone"}), "alone")

	// Test 4: All identical strings
	test("All identical", longestCommonPrefix1([]string{"abc", "abc", "abc"}), "abc")

	// Test 5: Empty string in array
	test("Empty string in array", longestCommonPrefix1([]string{"abc", "", "abc"}), "")

	// Test 6: Single character strings
	test("Single char strings", longestCommonPrefix1([]string{"a", "a", "a"}), "a")

	// Test 7: First char mismatch
	test("First char mismatch", longestCommonPrefix1([]string{"abc", "xyz", "def"}), "")

	// Test 8: Two strings with partial match
	test("Two strings partial", longestCommonPrefix1([]string{"interview", "internet"}), "inter")

	// Test 9: One character prefix
	test("One char prefix", longestCommonPrefix1([]string{"ab", "ac", "ad"}), "a")

	fmt.Println()
	fmt.Println("=== Approach 2: Horizontal Scanning ===")

	// Test 1: LeetCode Example 1 — common prefix "fl"
	test("Example 1: flower/flow/flight", longestCommonPrefix2([]string{"flower", "flow", "flight"}), "fl")

	// Test 2: LeetCode Example 2 — no common prefix
	test("Example 2: dog/racecar/car", longestCommonPrefix2([]string{"dog", "racecar", "car"}), "")

	// Test 3: Single string
	test("Single string", longestCommonPrefix2([]string{"alone"}), "alone")

	// Test 4: All identical strings
	test("All identical", longestCommonPrefix2([]string{"abc", "abc", "abc"}), "abc")

	// Test 5: Empty string in array
	test("Empty string in array", longestCommonPrefix2([]string{"abc", "", "abc"}), "")

	// Test 6: Single character strings
	test("Single char strings", longestCommonPrefix2([]string{"a", "a", "a"}), "a")

	// Test 7: First char mismatch
	test("First char mismatch", longestCommonPrefix2([]string{"abc", "xyz", "def"}), "")

	// Test 8: Two strings with partial match
	test("Two strings partial", longestCommonPrefix2([]string{"interview", "internet"}), "inter")

	// Test 9: One character prefix
	test("One char prefix", longestCommonPrefix2([]string{"ab", "ac", "ad"}), "a")

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
