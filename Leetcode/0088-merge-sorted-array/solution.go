package main

import (
	"fmt"
	"reflect"
)

// 0088. Merge Sorted Array
// Time: O(m + n), Space: O(1)
func merge(nums1 []int, m int, nums2 []int, n int) {
	i, j, k := m-1, n-1, m+n-1
	for j >= 0 {
		if i >= 0 && nums1[i] > nums2[j] {
			nums1[k] = nums1[i]
			i--
		} else {
			nums1[k] = nums2[j]
			j--
		}
		k--
	}
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []int) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%v\n", name, got)
			failed++
		}
	}
	cases := []struct {
		name             string
		nums1            []int
		m                int
		nums2            []int
		n                int
		expected         []int
	}{
		{"Example 1", []int{1, 2, 3, 0, 0, 0}, 3, []int{2, 5, 6}, 3, []int{1, 2, 2, 3, 5, 6}},
		{"Example 2", []int{1}, 1, []int{}, 0, []int{1}},
		{"Example 3", []int{0}, 0, []int{1}, 1, []int{1}},
		{"All nums2 smaller", []int{4, 5, 6, 0, 0, 0}, 3, []int{1, 2, 3}, 3, []int{1, 2, 3, 4, 5, 6}},
		{"All nums2 larger", []int{1, 2, 3, 0, 0, 0}, 3, []int{4, 5, 6}, 3, []int{1, 2, 3, 4, 5, 6}},
		{"Interleaved", []int{1, 3, 5, 0, 0, 0}, 3, []int{2, 4, 6}, 3, []int{1, 2, 3, 4, 5, 6}},
		{"With duplicates", []int{1, 2, 2, 0, 0, 0}, 3, []int{2, 2, 2}, 3, []int{1, 2, 2, 2, 2, 2}},
		{"Single element each", []int{1, 0}, 1, []int{2}, 1, []int{1, 2}},
	}
	for _, c := range cases {
		nums1 := append([]int{}, c.nums1...)
		nums2 := append([]int{}, c.nums2...)
		merge(nums1, c.m, nums2, c.n)
		test(c.name, nums1, c.expected)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
