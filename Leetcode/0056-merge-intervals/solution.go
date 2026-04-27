package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0056. Merge Intervals
// https://leetcode.com/problems/merge-intervals/
// Difficulty: Medium
// Tags: Array, Sorting
// ============================================================

// merge — Optimal Solution (Sort + Sweep)
// Approach: sort by start, then extend or append based on overlap with last.
// Time:  O(n log n) — sort dominates
// Space: O(n) — output (sort itself is O(log n) extra)
func merge(intervals [][]int) [][]int {
	if len(intervals) == 0 {
		return [][]int{}
	}
	// Avoid mutating caller's slice
	cp := make([][]int, len(intervals))
	for i, iv := range intervals {
		cp[i] = []int{iv[0], iv[1]}
	}
	sort.Slice(cp, func(i, j int) bool {
		return cp[i][0] < cp[j][0]
	})
	result := make([][]int, 0, len(cp))
	for _, iv := range cp {
		if len(result) == 0 || result[len(result)-1][1] < iv[0] {
			result = append(result, []int{iv[0], iv[1]})
		} else if iv[1] > result[len(result)-1][1] {
			result[len(result)-1][1] = iv[1]
		}
	}
	return result
}

// mergeSweep — Sweep Line / Boundary Counting
// Time:  O(n log n)
// Space: O(n)
func mergeSweep(intervals [][]int) [][]int {
	type ev struct{ pos, rank, delta int }
	events := make([]ev, 0, 2*len(intervals))
	for _, iv := range intervals {
		events = append(events, ev{iv[0], 0, +1})
		events = append(events, ev{iv[1], 1, -1})
	}
	sort.Slice(events, func(i, j int) bool {
		if events[i].pos != events[j].pos {
			return events[i].pos < events[j].pos
		}
		return events[i].rank < events[j].rank
	})
	result := [][]int{}
	cur, start := 0, 0
	for _, e := range events {
		if cur == 0 && e.delta == +1 {
			start = e.pos
		}
		cur += e.delta
		if cur == 0 {
			result = append(result, []int{start, e.pos})
		}
	}
	return result
}

// mergeBrute — Pairwise merge until fixed point
// Time:  O(n^3)
// Space: O(n)
func mergeBrute(intervals [][]int) [][]int {
	items := make([][]int, len(intervals))
	for i, iv := range intervals {
		items[i] = []int{iv[0], iv[1]}
	}
	changed := true
	for changed {
		changed = false
		for i := 0; i < len(items); i++ {
			for j := i + 1; j < len(items); {
				a, b := items[i][0], items[i][1]
				c, d := items[j][0], items[j][1]
				if a <= d && c <= b {
					if c < a {
						items[i][0] = c
					}
					if d > b {
						items[i][1] = d
					}
					items = append(items[:j], items[j+1:]...)
					changed = true
				} else {
					j++
				}
			}
		}
	}
	sort.Slice(items, func(i, j int) bool {
		return items[i][0] < items[j][0]
	})
	return items
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected [][]int) {
		// Empty slices comparison
		if len(got) == 0 && len(expected) == 0 {
			fmt.Printf("PASS: %s\n", name)
			passed++
			return
		}
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
		input    [][]int
		expected [][]int
	}
	cases := []tc{
		{"Example 1", [][]int{{1, 3}, {2, 6}, {8, 10}, {15, 18}},
			[][]int{{1, 6}, {8, 10}, {15, 18}}},
		{"Touching endpoints", [][]int{{1, 4}, {4, 5}},
			[][]int{{1, 5}}},
		{"Single interval", [][]int{{1, 4}}, [][]int{{1, 4}}},
		{"Disjoint", [][]int{{1, 2}, {3, 4}}, [][]int{{1, 2}, {3, 4}}},
		{"Contained", [][]int{{1, 10}, {2, 3}}, [][]int{{1, 10}}},
		{"Identical", [][]int{{1, 1}, {1, 1}}, [][]int{{1, 1}}},
		{"Reverse sorted", [][]int{{4, 5}, {1, 3}}, [][]int{{1, 3}, {4, 5}}},
		{"Two chains", [][]int{{1, 3}, {2, 4}, {6, 8}, {7, 9}},
			[][]int{{1, 4}, {6, 9}}},
		{"Zero-length disjoint", [][]int{{1, 1}, {2, 2}}, [][]int{{1, 1}, {2, 2}}},
		{"Large containment", [][]int{{1, 100}, {2, 3}, {4, 50}}, [][]int{{1, 100}}},
		{"Mixed order", [][]int{{2, 6}, {1, 3}, {15, 18}, {8, 10}},
			[][]int{{1, 6}, {8, 10}, {15, 18}}},
	}

	fmt.Println("=== Sort + Merge ===")
	for _, c := range cases {
		test(c.name, merge(c.input), c.expected)
	}

	fmt.Println("\n=== Sweep Line ===")
	for _, c := range cases {
		test("Sweep "+c.name, mergeSweep(c.input), c.expected)
	}

	fmt.Println("\n=== Brute Force ===")
	for _, c := range cases {
		test("Brute "+c.name, mergeBrute(c.input), c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
