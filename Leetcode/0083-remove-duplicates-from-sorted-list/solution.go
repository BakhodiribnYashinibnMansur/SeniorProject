package main

import "fmt"

// 0083. Remove Duplicates from Sorted List
// Time: O(n), Space: O(1)

type ListNode struct {
	Val  int
	Next *ListNode
}

func deleteDuplicates(head *ListNode) *ListNode {
	cur := head
	for cur != nil && cur.Next != nil {
		if cur.Next.Val == cur.Val {
			cur.Next = cur.Next.Next
		} else {
			cur = cur.Next
		}
	}
	return head
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
		name string
		in   []int
		want []int
	}{
		{"Example 1", []int{1, 1, 2}, []int{1, 2}},
		{"Example 2", []int{1, 1, 2, 3, 3}, []int{1, 2, 3}},
		{"Empty", []int{}, []int{}},
		{"Single", []int{5}, []int{5}},
		{"All same", []int{1, 1, 1}, []int{1}},
		{"No dup", []int{1, 2, 3}, []int{1, 2, 3}},
		{"Long", []int{0, 0, 0, 1, 2, 2, 3, 3, 3, 4}, []int{0, 1, 2, 3, 4}},
	}
	for _, c := range cases {
		test(c.name, toSlice(deleteDuplicates(toList(c.in))), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
