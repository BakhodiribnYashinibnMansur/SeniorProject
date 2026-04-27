package main

import (
	"fmt"
	"regexp"
)

// ============================================================
// 0065. Valid Number
// https://leetcode.com/problems/valid-number/
// Difficulty: Hard
// Tags: String
// ============================================================

// isNumber — Optimal Solution (Single Pass with Flags)
// Approach: walk the string, track flags for digit / dot / exponent /
//   digit-after-exponent. Validate each character against the rules.
// Time:  O(n)
// Space: O(1)
func isNumber(s string) bool {
	sawDigit, sawDot, sawE, digitAfterE := false, false, false, true
	for i := 0; i < len(s); i++ {
		c := s[i]
		switch {
		case c >= '0' && c <= '9':
			sawDigit = true
			if sawE {
				digitAfterE = true
			}
		case c == '+' || c == '-':
			if i != 0 && s[i-1] != 'e' && s[i-1] != 'E' {
				return false
			}
		case c == '.':
			if sawDot || sawE {
				return false
			}
			sawDot = true
		case c == 'e' || c == 'E':
			if sawE || !sawDigit {
				return false
			}
			sawE = true
			digitAfterE = false
		default:
			return false
		}
	}
	return sawDigit && digitAfterE
}

// validNumberRe — Regex (Approach 1)
var validNumberRe = regexp.MustCompile(`^[+-]?(\d+\.\d*|\.\d+|\d+)([eE][+-]?\d+)?$`)

func isNumberRegex(s string) bool {
	return validNumberRe.MatchString(s)
}

// isNumberDFA — Deterministic Finite Automaton
// Time:  O(n)
// Space: O(1)
func isNumberDFA(s string) bool {
	// transitions[state][class] = nextState (-1 = invalid)
	const (
		Digit = iota
		Sign
		Dot
		Exp
		Other
	)
	transitions := [][]int{
		{2, 1, 4, -1, -1}, // 0 start
		{2, -1, 4, -1, -1}, // 1 sign
		{2, -1, 3, 6, -1},  // 2 digit mantissa
		{5, -1, -1, 6, -1}, // 3 dot after digit
		{5, -1, -1, -1, -1}, // 4 dot first
		{5, -1, -1, 6, -1}, // 5 digit after dot
		{8, 7, -1, -1, -1}, // 6 e/E
		{8, -1, -1, -1, -1}, // 7 sign after e
		{8, -1, -1, -1, -1}, // 8 digit in exp
	}
	accepting := map[int]bool{2: true, 3: true, 5: true, 8: true}
	classify := func(c byte) int {
		switch {
		case c >= '0' && c <= '9':
			return Digit
		case c == '+' || c == '-':
			return Sign
		case c == '.':
			return Dot
		case c == 'e' || c == 'E':
			return Exp
		}
		return Other
	}
	state := 0
	for i := 0; i < len(s); i++ {
		k := classify(s[i])
		if k == Other {
			return false
		}
		next := transitions[state][k]
		if next == -1 {
			return false
		}
		state = next
	}
	return accepting[state]
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	type tc struct {
		s    string
		want bool
	}
	cases := []tc{
		{"0", true}, {"e", false}, {".", false},
		{"2", true}, {"0089", true}, {"-0.1", true}, {"+3.14", true},
		{"4.", true}, {"-.9", true}, {"2e10", true}, {"-90E3", true},
		{"3e+7", true}, {"+6e-1", true}, {"53.5e93", true}, {"-123.456e789", true},
		{"abc", false}, {"1a", false}, {"1e", false}, {"e3", false},
		{"99e2.5", false}, {"--6", false}, {"-+3", false}, {"95a54e53", false},
		{"6+1", false}, {"+", false}, {"-", false},
		{"+.", false}, {".e1", false}, {"6e6.5", false},
		{".1", true}, {"1.", true}, {"1.5", true}, {"+1", true}, {"-1", true},
		{".e", false}, {"+e", false}, {"6.e2", true},
	}

	fmt.Println("=== Single Pass ===")
	for _, c := range cases {
		test("isNumber("+c.s+")", isNumber(c.s), c.want)
	}
	fmt.Println("\n=== Regex ===")
	for _, c := range cases {
		test("regex("+c.s+")", isNumberRegex(c.s), c.want)
	}
	fmt.Println("\n=== DFA ===")
	for _, c := range cases {
		test("dfa("+c.s+")", isNumberDFA(c.s), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
