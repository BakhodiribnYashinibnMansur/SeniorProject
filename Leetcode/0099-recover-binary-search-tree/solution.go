package main

import "fmt"

type TreeNode struct {
	Val         int
	Left, Right *TreeNode
}

func recoverTree(root *TreeNode) {
	var first, second, prev *TreeNode
	var inorder func(n *TreeNode)
	inorder = func(n *TreeNode) {
		if n == nil {
			return
		}
		inorder(n.Left)
		if prev != nil && prev.Val > n.Val {
			if first == nil {
				first = prev
			}
			second = n
		}
		prev = n
		inorder(n.Right)
	}
	inorder(root)
	first.Val, second.Val = second.Val, first.Val
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

func inorder(n *TreeNode) []int {
	if n == nil {
		return nil
	}
	r := inorder(n.Left)
	r = append(r, n.Val)
	r = append(r, inorder(n.Right)...)
	return r
}

func isSorted(a []int) bool {
	for i := 1; i < len(a); i++ {
		if a[i] < a[i-1] {
			return false
		}
	}
	return true
}

func main() {
	passed, failed := 0, 0
	test := func(name string, root *TreeNode) {
		recoverTree(root)
		if isSorted(inorder(root)) {
			fmt.Printf("PASS: %s → %v\n", name, inorder(root))
			passed++
		} else {
			fmt.Printf("FAIL: %s → %v\n", name, inorder(root))
			failed++
		}
	}
	cases := []struct {
		name string
		arr  []interface{}
	}{
		{"Example 1", []interface{}{1, 3, nil, nil, 2}},
		{"Example 2", []interface{}{3, 1, 4, nil, nil, 2}},
		{"Adjacent swap", []interface{}{1, 2}},
	}
	for _, c := range cases {
		test(c.name, buildTree(c.arr))
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
