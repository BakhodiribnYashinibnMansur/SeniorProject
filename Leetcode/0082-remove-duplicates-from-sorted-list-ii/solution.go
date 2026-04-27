package main

import "fmt"

// 0082. Remove Duplicates from Sorted List II
// Time:  O(n), Space: O(1)

type ListNode struct {
	Val  int
	Next *ListNode
}

func deleteDuplicates(head *ListNode) *ListNode {
	dummy := &ListNode{Next: head}
	prev := dummy
	cur := head
	for cur != nil {
		if cur.Next != nil && cur.Next.Val == cur.Val {
			v := cur.Val
			for cur != nil && cur.Val == v {
				cur = cur.Next
			}
			prev.Next = cur
		} else {
			prev = cur
			cur = cur.Next
		}
	}
	return dummy.Next
}

// helpers
func toList(arr []int) *ListNode {
	if len(arr) == 0 {
		return nil
	}
	head := &ListNode{Val: arr[0]}
	cur := head
	for _, v := range arr[1:] {
		cur.Next = &ListNode{Val: v}
		cur = cur.Next
	}
	return head
}

func toSlice(head *ListNode) []int {
	out := []int{}
	for head != nil {
		out = append(out, head.Val)
		head = head.Next
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
	test := func(name string, got, expected []int) {
		if eq(got, expected) {
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
		{"Example 1", []int{1, 2, 3, 3, 4, 4, 5}, []int{1, 2, 5}},
		{"Example 2", []int{1, 1, 1, 2, 3}, []int{2, 3}},
		{"Empty", []int{}, []int{}},
		{"All duplicates", []int{1, 1, 1, 1}, []int{}},
		{"No duplicates", []int{1, 2, 3, 4}, []int{1, 2, 3, 4}},
		{"Single", []int{5}, []int{5}},
		{"Duplicate tail", []int{1, 2, 3, 3}, []int{1, 2}},
		{"Multiple runs", []int{1, 1, 2, 3, 3, 4, 5, 5}, []int{2, 4}},
		{"Negatives", []int{-2, -1, -1, 0, 1}, []int{-2, 0, 1}},
	}
	for _, c := range cases {
		test(c.name, toSlice(deleteDuplicates(toList(c.in))), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
