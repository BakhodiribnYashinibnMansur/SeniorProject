package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0080. Remove Duplicates from Sorted Array II
// https://leetcode.com/problems/remove-duplicates-from-sorted-array-ii/
// Difficulty: Medium
// Tags: Array, Two Pointers
// ============================================================

// removeDuplicates — Optimal Solution (Two Pointers, At-Most-K = 2)
// Time:  O(n)
// Space: O(1)
func removeDuplicates(nums []int) int {
	k := 2
	if len(nums) <= k {
		return len(nums)
	}
	i := k
	for j := k; j < len(nums); j++ {
		if nums[j] != nums[i-k] {
			nums[i] = nums[j]
			i++
		}
	}
	return i
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, gotLen int, gotPrefix []int, wantLen int, wantPrefix []int) {
		if gotLen == wantLen && reflect.DeepEqual(gotPrefix, wantPrefix) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      len=%d, prefix=%v\n  Expected: len=%d, prefix=%v\n",
				name, gotLen, gotPrefix, wantLen, wantPrefix)
			failed++
		}
	}

	cases := []struct {
		name string
		in   []int
		want []int
	}{
		{"Example 1", []int{1, 1, 1, 2, 2, 3}, []int{1, 1, 2, 2, 3}},
		{"Example 2", []int{0, 0, 1, 1, 1, 1, 2, 3, 3}, []int{0, 0, 1, 1, 2, 3, 3}},
		{"All same", []int{5, 5, 5, 5, 5}, []int{5, 5}},
		{"All distinct", []int{1, 2, 3, 4, 5}, []int{1, 2, 3, 4, 5}},
		{"Single", []int{7}, []int{7}},
		{"Two same", []int{4, 4}, []int{4, 4}},
		{"Three same", []int{4, 4, 4}, []int{4, 4}},
		{"Negatives", []int{-3, -3, -3, 0, 0, 0, 1}, []int{-3, -3, 0, 0, 1}},
		{"Long", []int{1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 5, 5, 5, 6}, []int{1, 1, 2, 2, 3, 3, 4, 5, 5, 6}},
	}

	for _, c := range cases {
		got := append([]int{}, c.in...)
		k := removeDuplicates(got)
		test(c.name, k, got[:k], len(c.want), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
