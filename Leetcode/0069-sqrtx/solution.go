package main

import "fmt"

// ============================================================
// 0069. Sqrt(x)
// https://leetcode.com/problems/sqrtx/
// Difficulty: Easy
// Tags: Math, Binary Search
// ============================================================

// mySqrt — Optimal Solution (Binary Search)
// Approach: classic binary search on r in [1, x/2]; compare mid <= x/mid
//   to avoid overflow.
// Time:  O(log x)
// Space: O(1)
func mySqrt(x int) int {
	if x < 2 {
		return x
	}
	lo, hi := 1, x/2
	ans := 0
	for lo <= hi {
		mid := (lo + hi) / 2
		if mid <= x/mid {
			ans = mid
			lo = mid + 1
		} else {
			hi = mid - 1
		}
	}
	return ans
}

// mySqrtNewton — Newton's Method
// Time:  O(log log x)
// Space: O(1)
func mySqrtNewton(x int) int {
	if x < 2 {
		return x
	}
	r := x
	for r*r > x {
		r = (r + x/r) / 2
	}
	return r
}

// mySqrtLinear — Linear search (slow but simple)
// Time:  O(sqrt(x))
// Space: O(1)
func mySqrtLinear(x int) int {
	if x < 2 {
		return x
	}
	r := 1
	for (r+1)*(r+1) <= x {
		r++
	}
	return r
}

// mySqrtBits — Bit-by-bit
// Time:  O(log x)
// Space: O(1)
func mySqrtBits(x int) int {
	if x < 2 {
		return x
	}
	result := 0
	bit := 1 << 16
	for bit > 0 {
		cand := result | bit
		if cand <= x/cand {
			// cand*cand <= x, but use division to avoid overflow
			if cand*cand <= x {
				result = cand
			}
		}
		bit >>= 1
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %d\n  Expected: %d\n", name, got, expected)
			failed++
		}
	}

	cases := []struct {
		x    int
		want int
	}{
		{0, 0}, {1, 1}, {2, 1}, {3, 1}, {4, 2}, {8, 2}, {15, 3}, {16, 4},
		{17, 4}, {25, 5}, {26, 5}, {99, 9}, {100, 10}, {101, 10},
		{2147395599, 46339}, {2147483647, 46340},
	}

	fmt.Println("=== Binary Search ===")
	for _, c := range cases {
		test(fmt.Sprintf("x=%d", c.x), mySqrt(c.x), c.want)
	}
	fmt.Println("\n=== Newton's Method ===")
	for _, c := range cases {
		test(fmt.Sprintf("Newton x=%d", c.x), mySqrtNewton(c.x), c.want)
	}
	fmt.Println("\n=== Bit Manipulation ===")
	for _, c := range cases {
		test(fmt.Sprintf("Bits x=%d", c.x), mySqrtBits(c.x), c.want)
	}
	fmt.Println("\n=== Linear (small only) ===")
	for _, c := range cases {
		if c.x > 10000 {
			continue
		}
		test(fmt.Sprintf("Linear x=%d", c.x), mySqrtLinear(c.x), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
