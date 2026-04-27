package main

import "fmt"

// 0091. Decode Ways
// Time: O(n), Space: O(1)
func numDecodings(s string) int {
	n := len(s)
	if n == 0 || s[0] == '0' {
		return 0
	}
	prev2, prev1 := 1, 1
	for i := 2; i <= n; i++ {
		cur := 0
		if s[i-1] != '0' {
			cur += prev1
		}
		two := int(s[i-2]-'0')*10 + int(s[i-1]-'0')
		if two >= 10 && two <= 26 {
			cur += prev2
		}
		prev2, prev1 = prev1, cur
	}
	return prev1
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
		s    string
		want int
	}{
		{"12", 2}, {"226", 3}, {"06", 0}, {"0", 0}, {"10", 1}, {"20", 1},
		{"27", 1}, {"100", 0}, {"1", 1}, {"11106", 2}, {"111111", 13},
		{"2611055971756562", 4}, {"301", 0},
	}
	for _, c := range cases {
		test(c.s, numDecodings(c.s), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
