package main

import "fmt"

type TreeNode struct {
	Val         int
	Left, Right *TreeNode
}

func isValidBST(root *TreeNode) bool {
	var dfs func(n *TreeNode, lo, hi int64) bool
	dfs = func(n *TreeNode, lo, hi int64) bool {
		if n == nil {
			return true
		}
		v := int64(n.Val)
		if v <= lo || v >= hi {
			return false
		}
		return dfs(n.Left, lo, v) && dfs(n.Right, v, hi)
	}
	return dfs(root, -1<<62, 1<<62)
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
		arr  []interface{}
		want bool
	}{
		{"Valid 1", []interface{}{2, 1, 3}, true},
		{"Invalid 1", []interface{}{5, 1, 4, nil, nil, 3, 6}, false},
		{"Single", []interface{}{1}, true},
		{"Empty", []interface{}{}, true},
		{"Equal not allowed", []interface{}{1, 1}, false},
		{"Deep valid", []interface{}{4, 2, 6, 1, 3, 5, 7}, true},
		{"Deep invalid", []interface{}{10, 5, 15, nil, nil, 6, 20}, false},
	}
	for _, c := range cases {
		test(c.name, isValidBST(buildTree(c.arr)), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
