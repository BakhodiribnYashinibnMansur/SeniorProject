package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0030. Substring with Concatenation of All Words
// https://leetcode.com/problems/substring-with-concatenation-of-all-words/
// Difficulty: Hard
// Tags: Hash Table, String, Sliding Window
// ============================================================

// findSubstring — Optimal Solution (Sliding Window with Hash Map)
// Approach: For each of wordLen offsets, slide a window of numWords words
// Time:  O(n * wordLen) — wordLen offsets, each processes n/wordLen words
// Space: O(m)           — frequency maps with at most m distinct words
func findSubstring(s string, words []string) []int {
	if len(s) == 0 || len(words) == 0 {
		return []int{}
	}

	wordLen := len(words[0])
	numWords := len(words)
	totalLen := wordLen * numWords

	if len(s) < totalLen {
		return []int{}
	}

	wordFreq := map[string]int{}
	for _, w := range words {
		wordFreq[w]++
	}

	result := []int{}

	// Try each starting offset from 0 to wordLen-1
	for i := 0; i < wordLen; i++ {
		left := i
		count := 0
		seen := map[string]int{}

		// Slide right pointer one word at a time
		for right := i; right+wordLen <= len(s); right += wordLen {
			word := s[right : right+wordLen]

			if _, ok := wordFreq[word]; ok {
				seen[word]++
				count++

				// Shrink window if word count exceeds target
				for seen[word] > wordFreq[word] {
					leftWord := s[left : left+wordLen]
					seen[leftWord]--
					count--
					left += wordLen
				}

				// Check if we have a valid concatenation
				if count == numWords {
					result = append(result, left)
				}
			} else {
				// Invalid word — reset the window
				seen = map[string]int{}
				count = 0
				left = right + wordLen
			}
		}
	}

	return result
}

// findSubstringBrute — Brute Force approach
// Approach: Check every starting position, build frequency map each time
// Time:  O(n * m * wordLen) — n positions, m words per position
// Space: O(m)               — frequency map
func findSubstringBrute(s string, words []string) []int {
	if len(s) == 0 || len(words) == 0 {
		return []int{}
	}

	wordLen := len(words[0])
	numWords := len(words)
	totalLen := wordLen * numWords
	result := []int{}

	wordFreq := map[string]int{}
	for _, w := range words {
		wordFreq[w]++
	}

	for i := 0; i <= len(s)-totalLen; i++ {
		seen := map[string]int{}
		valid := true
		for j := 0; j < numWords; j++ {
			word := s[i+j*wordLen : i+(j+1)*wordLen]
			if wordFreq[word] == 0 {
				valid = false
				break
			}
			seen[word]++
			if seen[word] > wordFreq[word] {
				valid = false
				break
			}
		}
		if valid {
			result = append(result, i)
		}
	}

	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []int) {
		sortedGot := make([]int, len(got))
		sortedExp := make([]int, len(expected))
		copy(sortedGot, got)
		copy(sortedExp, expected)
		sort.Ints(sortedGot)
		sort.Ints(sortedExp)

		if reflect.DeepEqual(sortedGot, sortedExp) {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Sliding Window with Hash Map (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1",
		findSubstring("barfoothefoobarman", []string{"foo", "bar"}),
		[]int{0, 9})

	// Test 2: LeetCode Example 2 — no match
	test("Example 2",
		findSubstring("wordgoodgoodgoodbestword", []string{"word", "good", "best", "word"}),
		[]int{})

	// Test 3: LeetCode Example 3 — multiple matches
	test("Example 3",
		findSubstring("barfoofoobarthefoobarman", []string{"bar", "foo", "the"}),
		[]int{6, 9, 12})

	// Test 4: Single character words
	test("Single char words",
		findSubstring("aaa", []string{"a", "a"}),
		[]int{0, 1})

	// Test 5: All same words
	test("All same words",
		findSubstring("aaa", []string{"a", "a", "a"}),
		[]int{0})

	// Test 6: No match — string too short
	test("String too short",
		findSubstring("ab", []string{"abc"}),
		[]int{})

	// Test 7: Exact match
	test("Exact match",
		findSubstring("foobar", []string{"foo", "bar"}),
		[]int{0})

	// Test 8: Duplicate words with match
	test("Duplicate words match",
		findSubstring("wordgoodgoodgoodbestword", []string{"word", "good", "best", "good"}),
		[]int{8})

	// Test 9: Single word
	test("Single word",
		findSubstring("foobarfoo", []string{"foo"}),
		[]int{0, 6})

	fmt.Println("\n=== Brute Force ===")

	test("BF: Example 1",
		findSubstringBrute("barfoothefoobarman", []string{"foo", "bar"}),
		[]int{0, 9})

	test("BF: Example 2",
		findSubstringBrute("wordgoodgoodgoodbestword", []string{"word", "good", "best", "word"}),
		[]int{})

	test("BF: Example 3",
		findSubstringBrute("barfoofoobarthefoobarman", []string{"bar", "foo", "the"}),
		[]int{6, 9, 12})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
