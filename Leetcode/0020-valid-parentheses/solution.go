package main

import "fmt"

// ============================================================
// 0020. Valid Parentheses
// https://leetcode.com/problems/valid-parentheses/
// Difficulty: Easy
// Tags: String, Stack
// ============================================================

// isValid -- Optimal Solution (Stack)
// Approach: Use a stack to track opening brackets
// Time:  O(n) -- single pass through the string
// Space: O(n) -- stack stores at most n/2 elements
func isValid(s string) bool {
	// Stack to store opening brackets
	stack := []byte{}

	// Mapping: closing bracket -> opening bracket
	matching := map[byte]byte{
		')': '(',
		']': '[',
		'}': '{',
	}

	for i := 0; i < len(s); i++ {
		ch := s[i]

		if ch == '(' || ch == '[' || ch == '{' {
			// Opening bracket: push onto stack
			stack = append(stack, ch)
		} else {
			// Closing bracket: check if stack is empty or top doesn't match
			if len(stack) == 0 || stack[len(stack)-1] != matching[ch] {
				return false
			}
			// Pop the top element
			stack = stack[:len(stack)-1]
		}
	}

	// Valid only if the stack is empty (all brackets matched)
	return len(stack) == 0
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Simple parentheses
	test("Simple parentheses", isValid("()"), true)

	// Test 2: Multiple types
	test("Multiple types", isValid("()[]{}"), true)

	// Test 3: Mismatched brackets
	test("Mismatched brackets", isValid("(]"), false)

	// Test 4: Nested brackets
	test("Nested brackets", isValid("{[()]}"), true)

	// Test 5: Incorrect nesting order
	test("Incorrect nesting", isValid("([)]"), false)

	// Test 6: Empty string
	test("Empty string", isValid(""), true)

	// Test 7: Single opening bracket
	test("Single opening bracket", isValid("("), false)

	// Test 8: Single closing bracket
	test("Single closing bracket", isValid("]"), false)

	// Test 9: Long nested valid string
	test("Long nested valid", isValid("(({{[[]]}}))"), true)

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
