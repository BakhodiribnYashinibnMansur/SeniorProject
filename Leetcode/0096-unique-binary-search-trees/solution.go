package main

import "fmt"

// 0096. Unique Binary Search Trees
func numTrees(n int) int {
	g := make([]int, n+1)
	g[0] = 1
	if n >= 1 {
		g[1] = 1
	}
	for i := 2; i <= n; i++ {
		for j := 0; j < i; j++ {
			g[i] += g[j] * g[i-1-j]
		}
	}
	return g[n]
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
		n, want int
	}{{1, 1}, {2, 2}, {3, 5}, {4, 14}, {5, 42}, {6, 132}, {7, 429}, {19, 1767263190}}
	for _, c := range cases {
		test(fmt.Sprintf("n=%d", c.n), numTrees(c.n), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
