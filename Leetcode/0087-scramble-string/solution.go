package main

import "fmt"

// 0087. Scramble String
// Time: O(n^4), Space: O(n^3)

type pair struct{ a, b string }

func isScramble(s1, s2 string) bool {
	memo := make(map[pair]bool)
	var solve func(a, b string) bool
	solve = func(a, b string) bool {
		if a == b {
			return true
		}
		if v, ok := memo[pair{a, b}]; ok {
			return v
		}
		var c [26]int
		for i := 0; i < len(a); i++ {
			c[a[i]-'a']++
			c[b[i]-'a']--
		}
		for _, v := range c {
			if v != 0 {
				memo[pair{a, b}] = false
				return false
			}
		}
		n := len(a)
		for i := 1; i < n; i++ {
			if (solve(a[:i], b[:i]) && solve(a[i:], b[i:])) ||
				(solve(a[:i], b[n-i:]) && solve(a[i:], b[:n-i])) {
				memo[pair{a, b}] = true
				return true
			}
		}
		memo[pair{a, b}] = false
		return false
	}
	return solve(s1, s2)
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
		name, s1, s2 string
		want         bool
	}{
		{"Example 1", "great", "rgeat", true},
		{"Example 2", "abcde", "caebd", false},
		{"Single", "a", "a", true},
		{"Different chars", "ab", "ba", true},
		{"Different chars 2", "ab", "ab", true},
		{"Different chars 3", "ab", "cd", false},
		{"Same string", "abc", "abc", true},
		{"Three chars swap", "abc", "bca", true},
		{"Three chars cab", "abc", "cab", true},
		{"abcd dcba", "abcd", "dcba", true},
		{"abcd cdab", "abcd", "cdab", true},
	}
	for _, c := range cases {
		test(c.name, isScramble(c.s1, c.s2), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
