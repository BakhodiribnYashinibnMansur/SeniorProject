package main

import "fmt"

type TreeNode struct {
	Val         int
	Left, Right *TreeNode
}

func isSameTree(p, q *TreeNode) bool {
	if p == nil && q == nil {
		return true
	}
	if p == nil || q == nil {
		return false
	}
	if p.Val != q.Val {
		return false
	}
	return isSameTree(p.Left, q.Left) && isSameTree(p.Right, q.Right)
}

func buildTree(arr []interface{}) *TreeNode {
	if len(arr) == 0 || arr[0] == nil {
		return nil
	}
	root := &TreeNode{Val: arr[0].(int)}
	q := []*TreeNode{root}
	i := 1
	for len(q) > 0 && i < len(arr) {
		n := q[0]
		q = q[1:]
		if i < len(arr) && arr[i] != nil {
			n.Left = &TreeNode{Val: arr[i].(int)}
			q = append(q, n.Left)
		}
		i++
		if i < len(arr) && arr[i] != nil {
			n.Right = &TreeNode{Val: arr[i].(int)}
			q = append(q, n.Right)
		}
		i++
	}
	return root
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
		name string
		p, q []interface{}
		want bool
	}{
		{"Example 1", []interface{}{1, 2, 3}, []interface{}{1, 2, 3}, true},
		{"Example 2", []interface{}{1, 2}, []interface{}{1, nil, 2}, false},
		{"Both empty", []interface{}{}, []interface{}{}, true},
		{"One empty", []interface{}{}, []interface{}{1}, false},
		{"Different values", []interface{}{1, 2, 1}, []interface{}{1, 1, 2}, false},
		{"Single same", []interface{}{5}, []interface{}{5}, true},
		{"Single different", []interface{}{5}, []interface{}{6}, false},
	}
	for _, c := range cases {
		test(c.name, isSameTree(buildTree(c.p), buildTree(c.q)), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
