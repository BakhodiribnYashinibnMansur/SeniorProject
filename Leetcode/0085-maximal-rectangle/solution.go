package main

import "fmt"

// 0085. Maximal Rectangle
// Time: O(rows * cols), Space: O(cols)
func maximalRectangle(matrix [][]byte) int {
	if len(matrix) == 0 || len(matrix[0]) == 0 {
		return 0
	}
	cols := len(matrix[0])
	heights := make([]int, cols)
	best := 0
	for _, row := range matrix {
		for c := 0; c < cols; c++ {
			if row[c] == '1' {
				heights[c]++
			} else {
				heights[c] = 0
			}
		}
		if a := largestRect(heights); a > best {
			best = a
		}
	}
	return best
}

func largestRect(heights []int) int {
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
			if heights[top]*width > best {
				best = heights[top] * width
			}
		}
		stack = append(stack, i)
	}
	return best
}

func toMat(rows []string) [][]byte {
	out := make([][]byte, len(rows))
	for i, r := range rows {
		out[i] = []byte(r)
	}
	return out
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, exp int) {
		if got == exp {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%d want=%d\n", name, got, exp)
			failed++
		}
	}
	cases := []struct {
		name string
		mat  []string
		want int
	}{
		{"Example 1", []string{"10100", "10111", "11111", "10010"}, 6},
		{"Single 0", []string{"0"}, 0},
		{"Single 1", []string{"1"}, 1},
		{"All zeros", []string{"00", "00"}, 0},
		{"All ones 3x3", []string{"111", "111", "111"}, 9},
		{"Single row mixed", []string{"01101"}, 2},
		{"Single col", []string{"1", "1", "0", "1", "1", "1"}, 3},
		{"L shape", []string{"110", "110", "111"}, 6},
	}
	for _, c := range cases {
		test(c.name, maximalRectangle(toMat(c.mat)), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
