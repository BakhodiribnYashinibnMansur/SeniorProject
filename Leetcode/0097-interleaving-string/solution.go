package main

import "fmt"

func isInterleave(s1, s2, s3 string) bool {
	m, n := len(s1), len(s2)
	if m+n != len(s3) {
		return false
	}
	dp := make([]bool, n+1)
	dp[0] = true
	for j := 1; j <= n; j++ {
		dp[j] = dp[j-1] && s2[j-1] == s3[j-1]
	}
	for i := 1; i <= m; i++ {
		dp[0] = dp[0] && s1[i-1] == s3[i-1]
		for j := 1; j <= n; j++ {
			dp[j] = (dp[j] && s1[i-1] == s3[i+j-1]) || (dp[j-1] && s2[j-1] == s3[i+j-1])
		}
	}
	return dp[n]
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, exp bool) {
		if got == exp {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%v\n", name, got)
			failed++
		}
	}
	cases := []struct {
		name, s1, s2, s3 string
		want             bool
	}{
		{"Example 1", "aabcc", "dbbca", "aadbbcbcac", true},
		{"Example 2", "aabcc", "dbbca", "aadbbbaccc", false},
		{"All empty", "", "", "", true},
		{"s1 empty", "", "abc", "abc", true},
		{"s2 empty", "abc", "", "abc", true},
		{"Different length", "a", "b", "abc", false},
		{"Length mismatch", "ab", "cd", "abc", false},
		{"Same chars different ord", "ab", "ba", "abba", true},
	}
	for _, c := range cases {
		test(c.name, isInterleave(c.s1, c.s2, c.s3), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
