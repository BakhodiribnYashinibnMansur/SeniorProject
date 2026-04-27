package main

import (
	"fmt"
	"sort"
)

// 0090. Subsets II
func subsetsWithDup(nums []int) [][]int {
	sort.Ints(nums)
	result := [][]int{}
	cur := []int{}
	var bt func(start int)
	bt = func(start int) {
		cp := make([]int, len(cur))
		copy(cp, cur)
		result = append(result, cp)
		for i := start; i < len(nums); i++ {
			if i > start && nums[i] == nums[i-1] {
				continue
			}
			cur = append(cur, nums[i])
			bt(i + 1)
			cur = cur[:len(cur)-1]
		}
	}
	bt(0)
	return result
}

func canon(out [][]int) [][]int {
	cp := make([][]int, len(out))
	for i, s := range out {
		c := append([]int{}, s...)
		sort.Ints(c)
		cp[i] = c
	}
	sort.Slice(cp, func(i, j int) bool {
		for k := 0; k < len(cp[i]) && k < len(cp[j]); k++ {
			if cp[i][k] != cp[j][k] {
				return cp[i][k] < cp[j][k]
			}
		}
		return len(cp[i]) < len(cp[j])
	})
	return cp
}

func equal(a, b [][]int) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if len(a[i]) != len(b[i]) {
			return false
		}
		for j := range a[i] {
			if a[i][j] != b[i][j] {
				return false
			}
		}
	}
	return true
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected [][]int) {
		if equal(canon(got), canon(expected)) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n", name)
			failed++
		}
	}
	cases := []struct {
		name string
		in   []int
		want [][]int
	}{
		{"Example 1", []int{1, 2, 2}, [][]int{{}, {1}, {2}, {1, 2}, {2, 2}, {1, 2, 2}}},
		{"Example 2", []int{0}, [][]int{{}, {0}}},
		{"All same", []int{4, 4, 4}, [][]int{{}, {4}, {4, 4}, {4, 4, 4}}},
		{"Two different", []int{1, 2}, [][]int{{}, {1}, {2}, {1, 2}}},
		{"Mixed dup", []int{1, 2, 2, 3}, [][]int{
			{}, {1}, {2}, {3}, {1, 2}, {1, 3}, {2, 2}, {2, 3}, {1, 2, 2}, {1, 2, 3}, {2, 2, 3}, {1, 2, 2, 3}}},
	}
	for _, c := range cases {
		test(c.name, subsetsWithDup(append([]int{}, c.in...)), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
