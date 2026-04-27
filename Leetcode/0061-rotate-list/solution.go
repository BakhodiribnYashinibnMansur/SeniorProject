package main

import "fmt"

// ============================================================
// 0061. Rotate List
// https://leetcode.com/problems/rotate-list/
// Difficulty: Medium
// Tags: Linked List, Two Pointers
// ============================================================

type ListNode struct {
	Val  int
	Next *ListNode
}

// rotateRight — Optimal Solution (Length + Re-Link)
// Approach: count the length and tail in one pass, then walk to the new
//   tail and rewire two pointers.
// Time:  O(n)
// Space: O(1)
func rotateRight(head *ListNode, k int) *ListNode {
	if head == nil || head.Next == nil {
		return head
	}
	n, tail := 1, head
	for tail.Next != nil {
		tail = tail.Next
		n++
	}
	k %= n
	if k == 0 {
		return head
	}
	newTail := head
	for i := 0; i < n-k-1; i++ {
		newTail = newTail.Next
	}
	newHead := newTail.Next
	newTail.Next = nil
	tail.Next = head
	return newHead
}

// rotateRightCircular — Make-circular-then-cut
// Time:  O(n)
// Space: O(1)
func rotateRightCircular(head *ListNode, k int) *ListNode {
	if head == nil {
		return head
	}
	n, tail := 1, head
	for tail.Next != nil {
		tail = tail.Next
		n++
	}
	tail.Next = head // close cycle
	k %= n
	newTail := head
	for i := 0; i < n-k-1; i++ {
		newTail = newTail.Next
	}
	newHead := newTail.Next
	newTail.Next = nil
	return newHead
}

// ============================================================
// Helpers + Test Cases
// ============================================================

func sliceToList(arr []int) *ListNode {
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

func listToSlice(head *ListNode) []int {
	out := []int{}
	for head != nil {
		out = append(out, head.Val)
		head = head.Next
	}
	return out
}

func equalSlices(a, b []int) bool {
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
		if equalSlices(got, expected) {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	type tc struct {
		name     string
		input    []int
		k        int
		expected []int
	}
	cases := []tc{
		{"Example 1", []int{1, 2, 3, 4, 5}, 2, []int{4, 5, 1, 2, 3}},
		{"Example 2", []int{0, 1, 2}, 4, []int{2, 0, 1}},
		{"Empty list", []int{}, 3, []int{}},
		{"Single node", []int{5}, 10, []int{5}},
		{"k=0", []int{1, 2, 3}, 0, []int{1, 2, 3}},
		{"k=n", []int{1, 2, 3}, 3, []int{1, 2, 3}},
		{"Two nodes k=1", []int{1, 2}, 1, []int{2, 1}},
		{"k = 2n", []int{1, 2, 3}, 6, []int{1, 2, 3}},
		{"Big k", []int{1, 2, 3, 4, 5, 6, 7, 8}, 100, []int{5, 6, 7, 8, 1, 2, 3, 4}},
		{"Negatives in list", []int{-1, -2, -3}, 1, []int{-3, -1, -2}},
	}

	fmt.Println("=== Length + Re-Link ===")
	for _, c := range cases {
		test(c.name, listToSlice(rotateRight(sliceToList(c.input), c.k)), c.expected)
	}
	fmt.Println("\n=== Make Circular + Cut ===")
	for _, c := range cases {
		test("Circ "+c.name, listToSlice(rotateRightCircular(sliceToList(c.input), c.k)), c.expected)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
