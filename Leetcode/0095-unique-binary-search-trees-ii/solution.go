package main

import "fmt"

type TreeNode struct {
	Val         int
	Left, Right *TreeNode
}

func generateTrees(n int) []*TreeNode {
	if n == 0 {
		return []*TreeNode{}
	}
	var gen func(lo, hi int) []*TreeNode
	gen = func(lo, hi int) []*TreeNode {
		if lo > hi {
			return []*TreeNode{nil}
		}
		result := []*TreeNode{}
		for root := lo; root <= hi; root++ {
			for _, l := range gen(lo, root-1) {
				for _, r := range gen(root+1, hi) {
					result = append(result, &TreeNode{Val: root, Left: l, Right: r})
				}
			}
		}
		return result
	}
	return gen(1, n)
}

// Catalan numbers for verification: C(0)=1, C(1)=1, C(2)=2, C(3)=5, C(4)=14, ...
func catalan(n int) int {
	if n <= 1 {
		return 1
	}
	c := []int{1, 1}
	for i := 2; i <= n; i++ {
		s := 0
		for j := 0; j < i; j++ {
			s += c[j] * c[i-1-j]
		}
		c = append(c, s)
	}
	return c[n]
}

func main() {
	passed, failed := 0, 0
	for n := 0; n <= 6; n++ {
		got := len(generateTrees(n))
		want := catalan(n)
		if n == 0 {
			want = 0
		}
		if got == want {
			fmt.Printf("PASS: n=%d → %d trees\n", n, got)
			passed++
		} else {
			fmt.Printf("FAIL: n=%d got=%d want=%d\n", n, got, want)
			failed++
		}
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
