package main

import (
	"fmt"
	"math/bits"
)

// 0089. Gray Code
// Time: O(2^n), Space: O(2^n)
func grayCode(n int) []int {
	size := 1 << n
	result := make([]int, size)
	for i := 0; i < size; i++ {
		result[i] = i ^ (i >> 1)
	}
	return result
}

func validate(seq []int, n int) bool {
	if len(seq) != 1<<n {
		return false
	}
	if seq[0] != 0 {
		return false
	}
	seen := make(map[int]bool)
	for _, v := range seq {
		if v < 0 || v >= 1<<n || seen[v] {
			return false
		}
		seen[v] = true
	}
	for i := 0; i < len(seq); i++ {
		j := (i + 1) % len(seq)
		if bits.OnesCount(uint(seq[i]^seq[j])) != 1 {
			return false
		}
	}
	return true
}

func main() {
	passed, failed := 0, 0
	for _, n := range []int{1, 2, 3, 4, 5, 8} {
		seq := grayCode(n)
		if validate(seq, n) {
			fmt.Printf("PASS: n=%d, len=%d\n", n, len(seq))
			passed++
		} else {
			fmt.Printf("FAIL: n=%d, seq=%v\n", n, seq)
			failed++
		}
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
