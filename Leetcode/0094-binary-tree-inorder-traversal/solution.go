package main

import (
	"fmt"
	"reflect"
)

// 0094. Binary Tree Inorder Traversal

type TreeNode struct {
	Val         int
	Left, Right *TreeNode
}

func inorderTraversal(root *TreeNode) []int {
	result := []int{}
	stack := []*TreeNode{}
	cur := root
	for cur != nil || len(stack) > 0 {
		for cur != nil {
			stack = append(stack, cur)
			cur = cur.Left
		}
		cur = stack[len(stack)-1]
		stack = stack[:len(stack)-1]
		result = append(result, cur.Val)
		cur = cur.Right
	}
	return result
}

// build tree from level-order with -1 for null
func buildTree(arr []interface{}) *TreeNode {
	if len(arr) == 0 || arr[0] == nil {
		return nil
	}
	root := &TreeNode{Val: arr[0].(int)}
	queue := []*TreeNode{root}
	i := 1
	for len(queue) > 0 && i < len(arr) {
		node := queue[0]
		queue = queue[1:]
		if i < len(arr) && arr[i] != nil {
			node.Left = &TreeNode{Val: arr[i].(int)}
			queue = append(queue, node.Left)
		}
		i++
		if i < len(arr) && arr[i] != nil {
			node.Right = &TreeNode{Val: arr[i].(int)}
			queue = append(queue, node.Right)
		}
		i++
	}
	return root
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, want []int) {
		if reflect.DeepEqual(got, want) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%v\n", name, got)
			failed++
		}
	}
	cases := []struct {
		name string
		tree []interface{}
		want []int
	}{
		{"Example 1", []interface{}{1, nil, 2, 3}, []int{1, 3, 2}},
		{"Empty", []interface{}{}, []int{}},
		{"Single", []interface{}{1}, []int{1}},
		{"Left only", []interface{}{1, 2, nil, 3}, []int{3, 2, 1}},
		{"Right only", []interface{}{1, nil, 2, nil, 3}, []int{1, 2, 3}},
		{"Balanced", []interface{}{1, 2, 3}, []int{2, 1, 3}},
	}
	for _, c := range cases {
		got := inorderTraversal(buildTree(c.tree))
		if got == nil {
			got = []int{}
		}
		test(c.name, got, c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
