package main

import "fmt"

// 0092. Reverse Linked List II

type ListNode struct {
	Val  int
	Next *ListNode
}

func reverseBetween(head *ListNode, left, right int) *ListNode {
	dummy := &ListNode{Next: head}
	prev := dummy
	for i := 1; i < left; i++ {
		prev = prev.Next
	}
	cur := prev.Next
	for i := 0; i < right-left; i++ {
		nxt := cur.Next
		cur.Next = nxt.Next
		nxt.Next = prev.Next
		prev.Next = nxt
	}
	return dummy.Next
}

func toList(arr []int) *ListNode {
	if len(arr) == 0 {
		return nil
	}
	h := &ListNode{Val: arr[0]}
	c := h
	for _, v := range arr[1:] {
		c.Next = &ListNode{Val: v}
		c = c.Next
	}
	return h
}
func toSlice(h *ListNode) []int {
	out := []int{}
	for h != nil {
		out = append(out, h.Val)
		h = h.Next
	}
	return out
}
func eq(a, b []int) bool {
	if len(a) != len(b) {
		return false
	}
	for i := range a {
		if a[i] != b[i] {
			return false
		}
	}
	return true
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, exp []int) {
		if eq(got, exp) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s got=%v\n", name, got)
			failed++
		}
	}
	cases := []struct {
		name        string
		in          []int
		left, right int
		want        []int
	}{
		{"Example 1", []int{1, 2, 3, 4, 5}, 2, 4, []int{1, 4, 3, 2, 5}},
		{"Example 2", []int{5}, 1, 1, []int{5}},
		{"Reverse all", []int{1, 2, 3}, 1, 3, []int{3, 2, 1}},
		{"left==right", []int{1, 2, 3}, 2, 2, []int{1, 2, 3}},
		{"Reverse from start", []int{1, 2, 3, 4}, 1, 2, []int{2, 1, 3, 4}},
		{"Reverse to end", []int{1, 2, 3, 4}, 3, 4, []int{1, 2, 4, 3}},
		{"Two nodes", []int{1, 2}, 1, 2, []int{2, 1}},
	}
	for _, c := range cases {
		test(c.name, toSlice(reverseBetween(toList(c.in), c.left, c.right)), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
