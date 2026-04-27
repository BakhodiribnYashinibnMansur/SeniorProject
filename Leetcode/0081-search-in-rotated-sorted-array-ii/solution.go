package main

import "fmt"

// 0081. Search in Rotated Sorted Array II
// Time: O(log n) avg, O(n) worst
// Space: O(1)
func search(nums []int, target int) bool {
	lo, hi := 0, len(nums)-1
	for lo <= hi {
		mid := (lo + hi) / 2
		if nums[mid] == target {
			return true
		}
		if nums[lo] == nums[mid] && nums[mid] == nums[hi] {
			lo++
			hi--
		} else if nums[lo] <= nums[mid] {
			if nums[lo] <= target && target < nums[mid] {
				hi = mid - 1
			} else {
				lo = mid + 1
			}
		} else {
			if nums[mid] < target && target <= nums[hi] {
				lo = mid + 1
			} else {
				hi = mid - 1
			}
		}
	}
	return false
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected bool) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%v\n", name, got)
			failed++
		}
	}

	cases := []struct {
		name   string
		nums   []int
		target int
		want   bool
	}{
		{"Example 1", []int{2, 5, 6, 0, 0, 1, 2}, 0, true},
		{"Example 2", []int{2, 5, 6, 0, 0, 1, 2}, 3, false},
		{"Single match", []int{5}, 5, true},
		{"Single miss", []int{5}, 6, false},
		{"All duplicates match", []int{1, 1, 1, 1}, 1, true},
		{"All duplicates miss", []int{1, 1, 1, 1}, 2, false},
		{"Not rotated", []int{1, 2, 3, 4}, 3, true},
		{"Pivot at start", []int{4, 5, 6, 1, 2, 3}, 1, true},
		{"Edges first", []int{4, 5, 6, 7, 0, 1, 2}, 4, true},
		{"Edges last", []int{4, 5, 6, 7, 0, 1, 2}, 2, true},
		{"Tricky duplicates", []int{1, 0, 1, 1, 1}, 0, true},
		{"Tricky duplicates miss", []int{1, 0, 1, 1, 1}, 2, false},
	}

	for _, c := range cases {
		test(c.name, search(c.nums, c.target), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
