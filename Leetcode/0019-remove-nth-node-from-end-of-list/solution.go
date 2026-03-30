package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0019. Remove Nth Node From End of List
// https://leetcode.com/problems/remove-nth-node-from-end-of-list/
// Difficulty: Medium
// Tags: Linked List, Two Pointers
// ============================================================

// ListNode — singly-linked list node
type ListNode struct {
	Val  int
	Next *ListNode
}

// removeNthFromEnd — Approach 1: Two-Pass (Count Length, Then Remove)
// Approach: First count the total length, then find the (length - n)th node to remove
// Time:  O(L) — two passes through the list, where L is the length
// Space: O(1) — only constant extra space
func removeNthFromEndTwoPass(head *ListNode, n int) *ListNode {
	// Dummy node handles edge case of removing the head
	dummy := &ListNode{Next: head}

	// First pass: count total length
	length := 0
	curr := head
	for curr != nil {
		length++
		curr = curr.Next
	}

	// Second pass: advance to the node just before the target
	curr = dummy
	for i := 0; i < length-n; i++ {
		curr = curr.Next
	}

	// Remove the target node by skipping it
	curr.Next = curr.Next.Next

	return dummy.Next
}

// removeNthFromEnd — Approach 2: One-Pass Two Pointers (Optimal)
// Approach: Move fast pointer n+1 steps ahead, then move both until fast reaches end
// Time:  O(L) — single pass through the list
// Space: O(1) — only constant extra space
func removeNthFromEnd(head *ListNode, n int) *ListNode {
	// Dummy node handles edge case of removing the head
	dummy := &ListNode{Next: head}
	fast := dummy
	slow := dummy

	// Move fast pointer n+1 steps ahead so that the gap between fast and slow is n
	for i := 0; i <= n; i++ {
		fast = fast.Next
	}

	// Move both pointers until fast reaches the end
	// Now slow is at the node just before the target
	for fast != nil {
		fast = fast.Next
		slow = slow.Next
	}

	// Remove the target node by skipping it
	slow.Next = slow.Next.Next

	return dummy.Next
}

// ============================================================
// Helper Functions for Testing
// ============================================================

// buildList creates a linked list from a slice of integers
func buildList(nums []int) *ListNode {
	dummy := &ListNode{}
	curr := dummy
	for _, v := range nums {
		curr.Next = &ListNode{Val: v}
		curr = curr.Next
	}
	return dummy.Next
}

// listToSlice converts a linked list back to a slice for easy comparison
func listToSlice(head *ListNode) []int {
	result := []int{}
	for head != nil {
		result = append(result, head.Val)
		head = head.Next
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected []int) {
		if reflect.DeepEqual(got, expected) {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Basic case — remove 2nd from end of [1,2,3,4,5]
	test("Remove 2nd from end of [1,2,3,4,5]",
		listToSlice(removeNthFromEnd(buildList([]int{1, 2, 3, 4, 5}), 2)),
		[]int{1, 2, 3, 5})

	// Test 2: Single element — remove 1st from end of [1]
	test("Remove 1st from end of [1]",
		listToSlice(removeNthFromEnd(buildList([]int{1}), 1)),
		[]int{})

	// Test 3: Two elements — remove 1st from end of [1,2]
	test("Remove 1st from end of [1,2]",
		listToSlice(removeNthFromEnd(buildList([]int{1, 2}), 1)),
		[]int{1})

	// Test 4: Remove head — remove 2nd from end of [1,2]
	test("Remove head (2nd from end of [1,2])",
		listToSlice(removeNthFromEnd(buildList([]int{1, 2}), 2)),
		[]int{2})

	// Test 5: Remove last element — remove 1st from end of [1,2,3]
	test("Remove last (1st from end of [1,2,3])",
		listToSlice(removeNthFromEnd(buildList([]int{1, 2, 3}), 1)),
		[]int{1, 2})

	// Test 6: Remove first element — remove 5th from end of [1,2,3,4,5]
	test("Remove first (5th from end of [1,2,3,4,5])",
		listToSlice(removeNthFromEnd(buildList([]int{1, 2, 3, 4, 5}), 5)),
		[]int{2, 3, 4, 5})

	// Test 7: Remove middle — remove 3rd from end of [1,2,3,4,5]
	test("Remove middle (3rd from end of [1,2,3,4,5])",
		listToSlice(removeNthFromEnd(buildList([]int{1, 2, 3, 4, 5}), 3)),
		[]int{1, 2, 4, 5})

	// Test 8: Longer list — remove 4th from end of [1,2,3,4,5,6,7]
	test("Remove 4th from end of [1,2,3,4,5,6,7]",
		listToSlice(removeNthFromEnd(buildList([]int{1, 2, 3, 4, 5, 6, 7}), 4)),
		[]int{1, 2, 3, 5, 6, 7})

	// Test 9: Two-pass approach — same basic case
	test("Two-pass: Remove 2nd from end of [1,2,3,4,5]",
		listToSlice(removeNthFromEndTwoPass(buildList([]int{1, 2, 3, 4, 5}), 2)),
		[]int{1, 2, 3, 5})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
