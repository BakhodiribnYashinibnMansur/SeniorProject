package main

import (
	"fmt"
)

// ============================================================
// 0003. Longest Substring Without Repeating Characters
// https://leetcode.com/problems/longest-substring-without-repeating-characters/
// Difficulty: Medium
// Tags: Hash Table, String, Sliding Window
// ============================================================

// lengthOfLongestSubstring — Optimal Solution (Sliding Window with Last-Seen Index Map)
// Approach: Maintain a window [left, right]; on duplicate, jump left past the last occurrence
// Time:  O(n) — each character is visited at most twice (once by right, once when left jumps)
// Space: O(min(n, a)) — map holds at most a unique characters, where a = alphabet size
func lengthOfLongestSubstring(s string) int {
	// Map: character → its most recently seen index
	lastSeen := make(map[byte]int)

	maxLen := 0
	left := 0

	for right := 0; right < len(s); right++ {
		ch := s[right]

		// If the character was seen before AND it is inside the current window,
		// move left pointer past the duplicate to shrink the window
		if idx, ok := lastSeen[ch]; ok && idx >= left {
			left = idx + 1
		}

		// Update the most recent index of this character
		lastSeen[ch] = right

		// Update the maximum window length
		windowLen := right - left + 1
		if windowLen > maxLen {
			maxLen = windowLen
		}
	}

	return maxLen
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Classic example — "abc" is the longest
	test(`"abcabcbb" → 3`, lengthOfLongestSubstring("abcabcbb"), 3)

	// Test 2: All same characters
	test(`"bbbbb" → 1`, lengthOfLongestSubstring("bbbbb"), 1)

	// Test 3: Longest at the end — "wke"
	test(`"pwwkew" → 3`, lengthOfLongestSubstring("pwwkew"), 3)

	// Test 4: Empty string
	test(`"" → 0`, lengthOfLongestSubstring(""), 0)

	// Test 5: Single character
	test(`"a" → 1`, lengthOfLongestSubstring("a"), 1)

	// Test 6: All unique characters
	test(`"abcdef" → 6`, lengthOfLongestSubstring("abcdef"), 6)

	// Test 7: Digits and symbols
	test(`"1234567890" → 10`, lengthOfLongestSubstring("1234567890"), 10)

	// Test 8: Duplicate at start and end
	test(`"dvdf" → 3`, lengthOfLongestSubstring("dvdf"), 3)

	// Test 9: Space characters
	test(`"a b" → 3`, lengthOfLongestSubstring("a b"), 3)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
