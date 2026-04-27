package main

import "fmt"

// ============================================================
// 0060. Permutation Sequence
// https://leetcode.com/problems/permutation-sequence/
// Difficulty: Hard
// Tags: Math, Recursion
// ============================================================

// getPermutation — Optimal Solution (Factorial Number System)
// Approach: decompose k-1 into factorial digits; each digit selects the
//   index into the remaining sorted list of digits.
// Time:  O(n^2) — n iterations, each pops from a list (O(n))
// Space: O(n)
func getPermutation(n int, k int) string {
	fact := make([]int, n+1)
	fact[0] = 1
	for i := 1; i <= n; i++ {
		fact[i] = fact[i-1] * i
	}
	digits := make([]int, n)
	for i := 0; i < n; i++ {
		digits[i] = i + 1
	}
	k-- // 0-based
	result := make([]byte, 0, n)
	for i := 0; i < n; i++ {
		m := n - i
		q := k / fact[m-1]
		result = append(result, byte('0'+digits[q]))
		digits = append(digits[:q], digits[q+1:]...)
		k %= fact[m-1]
	}
	return string(result)
}

// getPermutationBrute — Step k-1 next permutations (slow for large k)
// Time:  O(k * n)
// Space: O(n)
func getPermutationBrute(n int, k int) string {
	arr := make([]byte, n)
	for i := 0; i < n; i++ {
		arr[i] = byte('0' + (i + 1))
	}
	for step := 1; step < k; step++ {
		nextPermInPlace(arr)
	}
	return string(arr)
}

// nextPermInPlace — standard next-permutation on an ASCII digit slice.
func nextPermInPlace(a []byte) {
	n := len(a)
	i := n - 2
	for i >= 0 && a[i] >= a[i+1] {
		i--
	}
	if i >= 0 {
		j := n - 1
		for a[j] <= a[i] {
			j--
		}
		a[i], a[j] = a[j], a[i]
	}
	// Reverse suffix
	l, r := i+1, n-1
	for l < r {
		a[l], a[r] = a[r], a[l]
		l++
		r--
	}
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name, got, expected string) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %q\n  Expected: %q\n", name, got, expected)
			failed++
		}
	}

	type tc struct {
		name string
		n, k int
		want string
	}
	cases := []tc{
		{"Example 1", 3, 3, "213"},
		{"Example 2", 4, 9, "2314"},
		{"Example 3", 3, 1, "123"},
		{"n=1", 1, 1, "1"},
		{"n=3 last", 3, 6, "321"},
		{"n=4 first", 4, 1, "1234"},
		{"n=4 last", 4, 24, "4321"},
		{"n=4 boundary", 4, 7, "2134"},
		{"n=9 first", 9, 1, "123456789"},
		{"n=9 last", 9, 362880, "987654321"},
		{"n=9 middle", 9, 200000, "596742183"},
	}

	fmt.Println("=== Factorial Number System (Optimal) ===")
	for _, c := range cases {
		test(c.name, getPermutation(c.n, c.k), c.want)
	}

	fmt.Println("\n=== Brute Force (small only) ===")
	for _, c := range cases {
		if c.k > 1000 {
			continue
		}
		test("Brute "+c.name, getPermutationBrute(c.n, c.k), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
