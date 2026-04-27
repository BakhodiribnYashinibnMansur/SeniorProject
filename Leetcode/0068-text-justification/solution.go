package main

import (
	"fmt"
	"reflect"
	"strings"
)

// ============================================================
// 0068. Text Justification
// https://leetcode.com/problems/text-justification/
// Difficulty: Hard
// Tags: Array, String, Simulation
// ============================================================

// fullJustify — Optimal Solution (Greedy Line Packing)
// Approach: greedily fit words on each line; on full lines distribute
//   extra spaces left-heavy; last line is left-justified.
// Time:  O(n * maxWidth)
// Space: O(n * maxWidth)
func fullJustify(words []string, maxWidth int) []string {
	result := []string{}
	n := len(words)
	i := 0
	for i < n {
		j := i
		lineLen := 0
		for j < n && lineLen+len(words[j])+(j-i) <= maxWidth {
			lineLen += len(words[j])
			j++
		}
		isLast := j == n
		var line string
		if isLast || j-i == 1 {
			line = strings.Join(words[i:j], " ")
			line += strings.Repeat(" ", maxWidth-len(line))
		} else {
			gaps := j - i - 1
			slots := maxWidth - lineLen
			base := slots / gaps
			extra := slots % gaps
			var sb strings.Builder
			for k := i; k < j-1; k++ {
				sb.WriteString(words[k])
				spaces := base
				if k-i < extra {
					spaces++
				}
				sb.WriteString(strings.Repeat(" ", spaces))
			}
			sb.WriteString(words[j-1])
			line = sb.String()
		}
		result = append(result, line)
		i = j
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []string) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	type tc struct {
		name     string
		words    []string
		width    int
		expected []string
	}
	cases := []tc{
		{"Example 1",
			[]string{"This", "is", "an", "example", "of", "text", "justification."}, 16,
			[]string{
				"This    is    an",
				"example  of text",
				"justification.  ",
			}},
		{"Example 2",
			[]string{"What", "must", "be", "acknowledgment", "shall", "be"}, 16,
			[]string{
				"What   must   be",
				"acknowledgment  ",
				"shall be        ",
			}},
		{"Example 3",
			[]string{"Science", "is", "what", "we", "understand", "well", "enough", "to", "explain",
				"to", "a", "computer.", "Art", "is", "everything", "else", "we", "do"}, 20,
			[]string{
				"Science  is  what we",
				"understand      well",
				"enough to explain to",
				"a  computer.  Art is",
				"everything  else  we",
				"do                  ",
			}},
		{"Single word", []string{"Hello"}, 10, []string{"Hello     "}},
		{"Single word equals width", []string{"abc"}, 3, []string{"abc"}},
		{"Two words last line", []string{"a", "b"}, 5, []string{"a b  "}},
	}

	for _, c := range cases {
		test(c.name, fullJustify(c.words, c.width), c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
