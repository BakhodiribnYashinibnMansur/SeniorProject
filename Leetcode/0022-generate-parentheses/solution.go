package main

import (
	"fmt"
	"sort"
	"strings"
)

// ============================================================
// 0022. Generate Parentheses
// https://leetcode.com/problems/generate-parentheses/
// Difficulty: Medium
// Tags: String, Dynamic Programming, Backtracking
// ============================================================

// generateParenthesis -- Optimal Solution (Backtracking)
// Approach: Build valid strings character by character using constraints
// Time:  O(4^n / sqrt(n)) -- nth Catalan number of valid sequences
// Space: O(n) -- recursion depth is 2n
func generateParenthesis(n int) []string {
	result := []string{}

	var backtrack func(current []byte, open, close int)
	backtrack = func(current []byte, open, close int) {
		// Base case: string is complete
		if len(current) == 2*n {
			result = append(result, string(current))
			return
		}

		// Choice 1: add '(' if we haven't used all n
		if open < n {
			current = append(current, '(')
			backtrack(current, open+1, close)
			current = current[:len(current)-1]
		}

		// Choice 2: add ')' if it won't create an invalid prefix
		if close < open {
			current = append(current, ')')
			backtrack(current, open, close+1)
			current = current[:len(current)-1]
		}
	}

	backtrack([]byte{}, 0, 0)
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, got, expected interface{}) {
		match := false
		switch g := got.(type) {
		case []string:
			e := expected.([]string)
			sortedG := make([]string, len(g))
			sortedE := make([]string, len(e))
			copy(sortedG, g)
			copy(sortedE, e)
			sort.Strings(sortedG)
			sort.Strings(sortedE)
			match = strings.Join(sortedG, ",") == strings.Join(sortedE, ",")
		case int:
			match = g == expected.(int)
		case bool:
			match = g == expected.(bool)
		}

		if match {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: n = 1
	test("n = 1", generateParenthesis(1), []string{"()"})

	// Test 2: n = 2
	test("n = 2", generateParenthesis(2), []string{"(())", "()()"})

	// Test 3: n = 3
	test("n = 3", generateParenthesis(3),
		[]string{"((()))", "(()())", "(())()", "()(())", "()()()"})

	// Test 4: n = 4 (should have 14 results)
	test("n = 4 count", len(generateParenthesis(4)), 14)

	// Test 5: All results are valid parentheses
	isValid := func(s string) bool {
		count := 0
		for _, ch := range s {
			if ch == '(' {
				count++
			} else {
				count--
			}
			if count < 0 {
				return false
			}
		}
		return count == 0
	}

	resultsN3 := generateParenthesis(3)
	allValid := true
	for _, s := range resultsN3 {
		if !isValid(s) {
			allValid = false
			break
		}
	}
	test("All n=3 results are valid", allValid, true)

	// Test 6: No duplicates
	resultsN4 := generateParenthesis(4)
	seen := map[string]bool{}
	for _, s := range resultsN4 {
		seen[s] = true
	}
	test("No duplicates for n=4", len(seen), len(resultsN4))

	// Test 7: Correct length of each string
	allCorrectLen := true
	for _, s := range resultsN3 {
		if len(s) != 6 {
			allCorrectLen = false
			break
		}
	}
	test("All n=3 strings have length 6", allCorrectLen, true)

	// Test 8: n = 5 (should have 42 results)
	test("n = 5 count", len(generateParenthesis(5)), 42)

	// Test 9: n = 8 (should have 1430 results)
	test("n = 8 count", len(generateParenthesis(8)), 1430)

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
