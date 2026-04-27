package main

import "fmt"

// 0084. Largest Rectangle in Histogram
// Time: O(n), Space: O(n)
func largestRectangleArea(heights []int) int {
	n := len(heights)
	stack := []int{}
	best := 0
	for i := 0; i <= n; i++ {
		var h int
		if i == n {
			h = 0
		} else {
			h = heights[i]
		}
		for len(stack) > 0 && heights[stack[len(stack)-1]] > h {
			top := stack[len(stack)-1]
			stack = stack[:len(stack)-1]
			width := i
			if len(stack) > 0 {
				width = i - stack[len(stack)-1] - 1
			}
			area := heights[top] * width
			if area > best {
				best = area
			}
		}
		stack = append(stack, i)
	}
	return best
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%d want=%d\n", name, got, expected)
			failed++
		}
	}
	cases := []struct {
		name string
		in   []int
		want int
	}{
		{"Example 1", []int{2, 1, 5, 6, 2, 3}, 10},
		{"Example 2", []int{2, 4}, 4},
		{"Single", []int{5}, 5},
		{"All same", []int{3, 3, 3, 3}, 12},
		{"Strictly increasing", []int{1, 2, 3, 4, 5}, 9},
		{"Strictly decreasing", []int{5, 4, 3, 2, 1}, 9},
		{"With zeros", []int{0, 1, 0, 2}, 2},
		{"All zeros", []int{0, 0, 0}, 0},
		{"Spike", []int{1, 100}, 100},
		{"Peak", []int{1, 2, 3, 2, 1}, 6},
		{"Empty bars then bars", []int{0, 0, 2, 1, 2}, 3},
	}
	for _, c := range cases {
		test(c.name, largestRectangleArea(c.in), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
