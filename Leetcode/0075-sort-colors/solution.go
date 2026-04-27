package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0075. Sort Colors
// https://leetcode.com/problems/sort-colors/
// Difficulty: Medium
// Tags: Array, Two Pointers, Sorting
// ============================================================

// sortColors — Optimal Solution (Dutch National Flag)
// Approach: three pointers (low/mid/high). At each mid, classify and
//   swap with low or high; advance pointers accordingly.
// Time:  O(n)
// Space: O(1)
func sortColors(nums []int) {
	low, mid, high := 0, 0, len(nums)-1
	for mid <= high {
		switch nums[mid] {
		case 0:
			nums[low], nums[mid] = nums[mid], nums[low]
			low++
			mid++
		case 1:
			mid++
		case 2:
			nums[mid], nums[high] = nums[high], nums[mid]
			high--
		}
	}
}

// sortColorsCount — Counting sort (two pass)
// Time:  O(n)
// Space: O(1)
func sortColorsCount(nums []int) {
	c := [3]int{}
	for _, x := range nums {
		c[x]++
	}
	i := 0
	for v := 0; v < 3; v++ {
		for k := 0; k < c[v]; k++ {
			nums[i] = v
			i++
		}
	}
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []int) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	cases := []struct {
		name string
		in   []int
		want []int
	}{
		{"Example 1", []int{2, 0, 2, 1, 1, 0}, []int{0, 0, 1, 1, 2, 2}},
		{"Example 2", []int{2, 0, 1}, []int{0, 1, 2}},
		{"All zeros", []int{0, 0, 0}, []int{0, 0, 0}},
		{"All ones", []int{1, 1, 1}, []int{1, 1, 1}},
		{"All twos", []int{2, 2, 2}, []int{2, 2, 2}},
		{"Already sorted", []int{0, 1, 2}, []int{0, 1, 2}},
		{"Reverse sorted", []int{2, 1, 0}, []int{0, 1, 2}},
		{"Single element 0", []int{0}, []int{0}},
		{"Single element 2", []int{2}, []int{2}},
		{"Long mix", []int{0, 1, 2, 0, 1, 2, 0, 1, 2}, []int{0, 0, 0, 1, 1, 1, 2, 2, 2}},
		{"Two zeros one one", []int{1, 0, 0}, []int{0, 0, 1}},
	}

	fmt.Println("=== Dutch National Flag ===")
	for _, c := range cases {
		got := append([]int{}, c.in...)
		sortColors(got)
		test(c.name, got, c.want)
	}
	fmt.Println("\n=== Counting Sort ===")
	for _, c := range cases {
		got := append([]int{}, c.in...)
		sortColorsCount(got)
		test("Count "+c.name, got, c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
