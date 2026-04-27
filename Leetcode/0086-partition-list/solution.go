package main

import "fmt"

// 0086. Partition List
type ListNode struct {
	Val  int
	Next *ListNode
}

func partition(head *ListNode, x int) *ListNode {
	lessDummy := &ListNode{}
	geDummy := &ListNode{}
	lt, gt := lessDummy, geDummy
	for cur := head; cur != nil; cur = cur.Next {
		if cur.Val < x {
			lt.Next = cur
			lt = lt.Next
		} else {
			gt.Next = cur
			gt = gt.Next
		}
	}
	gt.Next = nil
	lt.Next = geDummy.Next
	return lessDummy.Next
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
	test := func(name string, got, want []int) {
		if eq(got, want) {
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
		x    int
		want []int
	}{
		{"Example 1", []int{1, 4, 3, 2, 5, 2}, 3, []int{1, 2, 2, 4, 3, 5}},
		{"Example 2", []int{2, 1}, 2, []int{1, 2}},
		{"Empty", []int{}, 5, []int{}},
		{"Single", []int{5}, 5, []int{5}},
		{"All less", []int{1, 2, 3}, 5, []int{1, 2, 3}},
		{"All greater", []int{5, 6, 7}, 1, []int{5, 6, 7}},
		{"All equal", []int{3, 3, 3}, 3, []int{3, 3, 3}},
		{"Negatives", []int{-3, 1, -1, 0}, 0, []int{-3, -1, 1, 0}},
		{"Mixed", []int{1, 4, 3, 2, 5, 2}, 3, []int{1, 2, 2, 4, 3, 5}},
	}
	for _, c := range cases {
		test(c.name, toSlice(partition(toList(c.in), c.x)), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
