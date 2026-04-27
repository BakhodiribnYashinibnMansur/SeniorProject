package main

import (
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0057. Insert Interval
// https://leetcode.com/problems/insert-interval/
// Difficulty: Medium
// Tags: Array
// ============================================================

// insert — Optimal Solution (Three-Phase Linear Scan)
// Approach: emit left-disjoint, merge overlapping into newInterval,
//   emit merged, then emit right-disjoint.
// Time:  O(n)
// Space: O(n) — output
func insert(intervals [][]int, newInterval []int) [][]int {
	n := len(intervals)
	result := make([][]int, 0, n+1)
	cur := []int{newInterval[0], newInterval[1]}
	i := 0
	for i < n && intervals[i][1] < cur[0] {
		result = append(result, []int{intervals[i][0], intervals[i][1]})
		i++
	}
	for i < n && intervals[i][0] <= cur[1] {
		if intervals[i][0] < cur[0] {
			cur[0] = intervals[i][0]
		}
		if intervals[i][1] > cur[1] {
			cur[1] = intervals[i][1]
		}
		i++
	}
	result = append(result, cur)
	for i < n {
		result = append(result, []int{intervals[i][0], intervals[i][1]})
		i++
	}
	return result
}

// insertBinary — Binary search boundaries
// Time:  O(n) for output copy, O(log n) for boundary lookup
// Space: O(n)
func insertBinary(intervals [][]int, newInterval []int) [][]int {
	n := len(intervals)
	if n == 0 {
		return [][]int{{newInterval[0], newInterval[1]}}
	}
	lo := sort.Search(n, func(i int) bool { return intervals[i][1] >= newInterval[0] })
	hi := sort.Search(n, func(i int) bool { return intervals[i][0] > newInterval[1] })
	mergedStart, mergedEnd := newInterval[0], newInterval[1]
	if lo < hi {
		if intervals[lo][0] < mergedStart {
			mergedStart = intervals[lo][0]
		}
		if intervals[hi-1][1] > mergedEnd {
			mergedEnd = intervals[hi-1][1]
		}
	}
	result := make([][]int, 0, n-(hi-lo)+1)
	for k := 0; k < lo; k++ {
		result = append(result, []int{intervals[k][0], intervals[k][1]})
	}
	result = append(result, []int{mergedStart, mergedEnd})
	for k := hi; k < n; k++ {
		result = append(result, []int{intervals[k][0], intervals[k][1]})
	}
	return result
}

// insertReMerge — Append + sort + merge
// Time:  O(n log n)
// Space: O(n)
func insertReMerge(intervals [][]int, newInterval []int) [][]int {
	items := make([][]int, 0, len(intervals)+1)
	for _, iv := range intervals {
		items = append(items, []int{iv[0], iv[1]})
	}
	items = append(items, []int{newInterval[0], newInterval[1]})
	sort.Slice(items, func(i, j int) bool { return items[i][0] < items[j][0] })
	result := [][]int{}
	for _, iv := range items {
		if len(result) == 0 || result[len(result)-1][1] < iv[0] {
			result = append(result, []int{iv[0], iv[1]})
		} else if iv[1] > result[len(result)-1][1] {
			result[len(result)-1][1] = iv[1]
		}
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected [][]int) {
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
		name        string
		intervals   [][]int
		newInterval []int
		expected    [][]int
	}
	cases := []tc{
		{"Example 1", [][]int{{1, 3}, {6, 9}}, []int{2, 5}, [][]int{{1, 5}, {6, 9}}},
		{"Example 2",
			[][]int{{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}},
			[]int{4, 8},
			[][]int{{1, 2}, {3, 10}, {12, 16}}},
		{"Empty input", [][]int{}, []int{5, 7}, [][]int{{5, 7}}},
		{"Insert at start no overlap", [][]int{{3, 5}}, []int{1, 2}, [][]int{{1, 2}, {3, 5}}},
		{"Insert at end no overlap", [][]int{{1, 2}}, []int{4, 5}, [][]int{{1, 2}, {4, 5}}},
		{"Insert in middle no overlap",
			[][]int{{1, 2}, {6, 7}}, []int{3, 4},
			[][]int{{1, 2}, {3, 4}, {6, 7}}},
		{"Engulfs everything",
			[][]int{{1, 2}, {5, 6}}, []int{0, 10}, [][]int{{0, 10}}},
		{"Touching merge left", [][]int{{1, 4}}, []int{4, 6}, [][]int{{1, 6}}},
		{"Touching merge right", [][]int{{4, 6}}, []int{1, 4}, [][]int{{1, 6}}},
		{"Zero-length new", [][]int{{1, 5}}, []int{3, 3}, [][]int{{1, 5}}},
		{"Insert before all touching",
			[][]int{{4, 6}, {7, 9}}, []int{0, 4}, [][]int{{0, 6}, {7, 9}}},
		{"Single existing absorbed",
			[][]int{{2, 3}}, []int{1, 5}, [][]int{{1, 5}}},
	}

	fmt.Println("=== Three-Phase Linear ===")
	for _, c := range cases {
		test(c.name, insert(c.intervals, c.newInterval), c.expected)
	}

	fmt.Println("\n=== Binary Search Boundaries ===")
	for _, c := range cases {
		test("Binary "+c.name, insertBinary(c.intervals, c.newInterval), c.expected)
	}

	fmt.Println("\n=== Re-Merge ===")
	for _, c := range cases {
		test("ReMerge "+c.name, insertReMerge(c.intervals, c.newInterval), c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
