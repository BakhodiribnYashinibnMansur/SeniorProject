package main

import (
	"fmt"
	"sort"
	"strings"
)

// ============================================================
// 0017. Letter Combinations of a Phone Number
// https://leetcode.com/problems/letter-combinations-of-a-phone-number/
// Difficulty: Medium
// Tags: Hash Table, String, Backtracking
// ============================================================

// phone maps digits to their corresponding letters on a phone keypad
var phone = map[byte]string{
	'2': "abc",
	'3': "def",
	'4': "ghi",
	'5': "jkl",
	'6': "mno",
	'7': "pqrs",
	'8': "tuv",
	'9': "wxyz",
}

// letterCombinations — Optimal Solution (Backtracking / DFS)
// Approach: Build combinations character by character using recursive backtracking
// Time:  O(4^n * n) — at most 4 choices per digit, n digits, each combination is length n
// Space: O(n)       — recursion depth equals number of digits (output not counted)
func letterCombinations(digits string) []string {
	if len(digits) == 0 {
		return []string{}
	}

	result := []string{}

	// backtrack builds combinations by choosing one letter per digit
	var backtrack func(index int, current []byte)
	backtrack = func(index int, current []byte) {
		// Base case: built a full-length combination
		if index == len(digits) {
			result = append(result, string(current))
			return
		}

		// Get letters mapped to the current digit
		letters := phone[digits[index]]

		// Try each letter for this digit position
		for i := 0; i < len(letters); i++ {
			current = append(current, letters[i])
			backtrack(index+1, current)
			current = current[:len(current)-1] // undo choice (backtrack)
		}
	}

	backtrack(0, []byte{})
	return result
}

// letterCombinationsIterative — Iterative Solution (BFS-like)
// Approach: Build combinations level by level, expanding each existing combination
// Time:  O(4^n * n) — same as backtracking
// Space: O(4^n * n) — stores all intermediate combinations
func letterCombinationsIterative(digits string) []string {
	if len(digits) == 0 {
		return []string{}
	}

	// Start with an empty combination
	result := []string{""}

	// For each digit, expand every existing combination with each mapped letter
	for i := 0; i < len(digits); i++ {
		letters := phone[digits[i]]
		newResult := []string{}

		for _, combo := range result {
			for j := 0; j < len(letters); j++ {
				newResult = append(newResult, combo+string(letters[j]))
			}
		}

		result = newResult
	}

	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []string) {
		// Sort both for consistent comparison
		sortedGot := make([]string, len(got))
		copy(sortedGot, got)
		sort.Strings(sortedGot)

		sortedExp := make([]string, len(expected))
		copy(sortedExp, expected)
		sort.Strings(sortedExp)

		if len(sortedGot) == 0 && len(sortedExp) == 0 {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
			return
		}

		if strings.Join(sortedGot, ",") == strings.Join(sortedExp, ",") {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Backtracking (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1: \"23\"",
		letterCombinations("23"),
		[]string{"ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"})

	// Test 2: LeetCode Example 2 — empty string
	test("Example 2: empty string",
		letterCombinations(""),
		[]string{})

	// Test 3: LeetCode Example 3 — single digit
	test("Example 3: \"2\"",
		letterCombinations("2"),
		[]string{"a", "b", "c"})

	// Test 4: Digit with 4 letters (7 = pqrs)
	test("Single digit 7 (4 letters)",
		letterCombinations("7"),
		[]string{"p", "q", "r", "s"})

	// Test 5: Two digits with 4 letters each
	test("\"79\" (4x4 = 16 combos)",
		letterCombinations("79"),
		[]string{"pw", "px", "py", "pz", "qw", "qx", "qy", "qz", "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz"})

	// Test 6: Three digits
	test("\"234\" (3x3x3 = 27 combos)",
		letterCombinations("234"),
		[]string{
			"adg", "adh", "adi", "aeg", "aeh", "aei", "afg", "afh", "afi",
			"bdg", "bdh", "bdi", "beg", "beh", "bei", "bfg", "bfh", "bfi",
			"cdg", "cdh", "cdi", "ceg", "ceh", "cei", "cfg", "cfh", "cfi",
		})

	// Test 7: Four digits
	test("\"2345\" (81 combos, check count)",
		letterCombinations("2345"),
		func() []string {
			// Generate all 81 expected combinations
			r := []string{}
			for _, a := range "abc" {
				for _, b := range "def" {
					for _, c := range "ghi" {
						for _, d := range "jkl" {
							r = append(r, string([]rune{a, b, c, d}))
						}
					}
				}
			}
			return r
		}())

	// Test 8: Digit 9 (wxyz)
	test("Single digit 9",
		letterCombinations("9"),
		[]string{"w", "x", "y", "z"})

	// Test 9: Same digit repeated
	test("\"22\" (same digit twice)",
		letterCombinations("22"),
		[]string{"aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"})

	fmt.Println("\n=== Iterative (BFS-like) ===")

	test("Iter: Example 1 \"23\"",
		letterCombinationsIterative("23"),
		[]string{"ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"})

	test("Iter: Empty string",
		letterCombinationsIterative(""),
		[]string{})

	test("Iter: Single digit \"2\"",
		letterCombinationsIterative("2"),
		[]string{"a", "b", "c"})

	test("Iter: \"79\" (4x4)",
		letterCombinationsIterative("79"),
		[]string{"pw", "px", "py", "pz", "qw", "qx", "qy", "qz", "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz"})

	test("Iter: \"22\" (same digit)",
		letterCombinationsIterative("22"),
		[]string{"aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
