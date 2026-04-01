package main

import "fmt"

// ============================================================
// 0032. Longest Valid Parentheses
// https://leetcode.com/problems/longest-valid-parentheses/
// Difficulty: Hard
// Tags: String, Dynamic Programming, Stack
// ============================================================

// longestValidParentheses -- Optimal Solution (Stack with indices)
// Approach: Use a stack storing indices to track unmatched positions
// Time:  O(n) -- single pass through the string
// Space: O(n) -- stack stores at most n+1 elements
func longestValidParentheses(s string) int {
	// Initialize stack with -1 as base for length calculation
	stack := []int{-1}
	maxLen := 0

	for i := 0; i < len(s); i++ {
		if s[i] == '(' {
			// Opening bracket: push index onto stack
			stack = append(stack, i)
		} else {
			// Closing bracket: pop the top element
			stack = stack[:len(stack)-1]
			if len(stack) == 0 {
				// Stack empty: push current index as new base
				stack = append(stack, i)
			} else {
				// Valid match: compute length from current base
				length := i - stack[len(stack)-1]
				if length > maxLen {
					maxLen = length
				}
			}
		}
	}

	return maxLen
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Example 1 -- valid pair in the middle
	test("Example 1: '(()'", longestValidParentheses("(()"), 2)

	// Test 2: Example 2 -- valid substring surrounded by unmatched
	test("Example 2: ')()())'", longestValidParentheses(")()())"), 4)

	// Test 3: Example 3 -- empty string
	test("Example 3: ''", longestValidParentheses(""), 0)

	// Test 4: Simple valid pair
	test("Simple pair: '()'", longestValidParentheses("()"), 2)

	// Test 5: Entire string valid (nested)
	test("Nested: '(())'", longestValidParentheses("(())"), 4)

	// Test 6: Adjacent valid pairs merge
	test("Adjacent: '()()'", longestValidParentheses("()()"), 4)

	// Test 7: All opening brackets
	test("All opening: '((('", longestValidParentheses("((("), 0)

	// Test 8: All closing brackets
	test("All closing: ')))'", longestValidParentheses(")))"), 0)

	// Test 9: Complex nesting and adjacency
	test("Complex: '()(())'", longestValidParentheses("()(())"), 6)

	// Test 10: Single character
	test("Single char: '('", longestValidParentheses("("), 0)

	// Test 11: Long alternating valid
	test("Long valid: '()()()()'", longestValidParentheses("()()()()"), 8)

	// Test 12: Valid in middle with unmatched ends
	test("Middle valid: '(()()'", longestValidParentheses("(()()"), 4)

	// Test 13: Deep nesting
	test("Deep nesting: '(((())))'", longestValidParentheses("(((())))"), 8)

	// Test 14: Multiple disjoint valid substrings
	test("Disjoint: '(())(('", longestValidParentheses("(())(("), 4)

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
