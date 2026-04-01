package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0025. Reverse Nodes in k-Group
// https://leetcode.com/problems/reverse-nodes-in-k-group/
// Difficulty: Hard
// Tags: Linked List, Recursion
// ============================================================

// ListNode — singly-linked list node
type ListNode struct {
	Val  int
	Next *ListNode
}

// reverseKGroup — Approach 1: Iterative
// Process list in groups of k, reversing each group in-place.
// Time:  O(n) — each node is visited at most twice
// Space: O(1) — only constant extra pointers
func reverseKGroup(head *ListNode, k int) *ListNode {
	dummy := &ListNode{Next: head}
	groupPrev := dummy

	for {
		// Check if k nodes remain
		kth := getKthNode(groupPrev, k)
		if kth == nil {
			break
		}

		groupNext := kth.Next

		// Reverse k nodes in this group
		prev := kth.Next
		curr := groupPrev.Next
		for curr != groupNext {
			tmp := curr.Next
			curr.Next = prev
			prev = curr
			curr = tmp
		}

		// Reconnect
		tmp := groupPrev.Next // original first node, now the tail
		groupPrev.Next = kth  // point to new head of reversed group
		groupPrev = tmp       // advance to the tail of the reversed group
	}

	return dummy.Next
}

// getKthNode returns the kth node after the given node, or nil if fewer than k nodes remain.
func getKthNode(node *ListNode, k int) *ListNode {
	for node != nil && k > 0 {
		node = node.Next
		k--
	}
	return node
}

// reverseKGroupRecursive — Approach 2: Recursive
// Reverse first k nodes, recurse on the rest, connect them.
// Time:  O(n) — each node is visited at most twice
// Space: O(n/k) — recursion stack depth equals number of groups
func reverseKGroupRecursive(head *ListNode, k int) *ListNode {
	// Check if k nodes exist
	node := head
	count := 0
	for node != nil && count < k {
		node = node.Next
		count++
	}

	if count < k {
		return head // not enough nodes, leave as is
	}

	// Reverse first k nodes
	var prev *ListNode
	curr := head
	for i := 0; i < k; i++ {
		next := curr.Next
		curr.Next = prev
		prev = curr
		curr = next
	}

	// head is now the tail of the reversed group — connect to recursion result
	head.Next = reverseKGroupRecursive(curr, k)

	return prev // prev is the new head of the reversed group
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
			fmt.Printf("  ✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("  ❌ FAIL: %s\n    Got:      %v\n    Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// --- Iterative approach tests ---
	fmt.Println("Approach 1: Iterative")

	test("[1,2,3,4,5] k=2 -> [2,1,4,3,5]",
		listToSlice(reverseKGroup(buildList([]int{1, 2, 3, 4, 5}), 2)),
		[]int{2, 1, 4, 3, 5})

	test("[1,2,3,4,5] k=3 -> [3,2,1,4,5]",
		listToSlice(reverseKGroup(buildList([]int{1, 2, 3, 4, 5}), 3)),
		[]int{3, 2, 1, 4, 5})

	test("[1,2,3,4,5] k=1 -> [1,2,3,4,5]",
		listToSlice(reverseKGroup(buildList([]int{1, 2, 3, 4, 5}), 1)),
		[]int{1, 2, 3, 4, 5})

	test("[1] k=1 -> [1]",
		listToSlice(reverseKGroup(buildList([]int{1}), 1)),
		[]int{1})

	test("[1,2] k=2 -> [2,1]",
		listToSlice(reverseKGroup(buildList([]int{1, 2}), 2)),
		[]int{2, 1})

	test("[1,2,3,4] k=2 -> [2,1,4,3]",
		listToSlice(reverseKGroup(buildList([]int{1, 2, 3, 4}), 2)),
		[]int{2, 1, 4, 3})

	test("[1,2,3,4,5] k=5 -> [5,4,3,2,1]",
		listToSlice(reverseKGroup(buildList([]int{1, 2, 3, 4, 5}), 5)),
		[]int{5, 4, 3, 2, 1})

	test("[1,2,3] k=3 -> [3,2,1]",
		listToSlice(reverseKGroup(buildList([]int{1, 2, 3}), 3)),
		[]int{3, 2, 1})

	// --- Recursive approach tests ---
	fmt.Println("\nApproach 2: Recursive")

	test("[1,2,3,4,5] k=2 -> [2,1,4,3,5]",
		listToSlice(reverseKGroupRecursive(buildList([]int{1, 2, 3, 4, 5}), 2)),
		[]int{2, 1, 4, 3, 5})

	test("[1,2,3,4,5] k=3 -> [3,2,1,4,5]",
		listToSlice(reverseKGroupRecursive(buildList([]int{1, 2, 3, 4, 5}), 3)),
		[]int{3, 2, 1, 4, 5})

	test("[1,2,3,4,5] k=1 -> [1,2,3,4,5]",
		listToSlice(reverseKGroupRecursive(buildList([]int{1, 2, 3, 4, 5}), 1)),
		[]int{1, 2, 3, 4, 5})

	test("[1] k=1 -> [1]",
		listToSlice(reverseKGroupRecursive(buildList([]int{1}), 1)),
		[]int{1})

	test("[1,2,3,4,5] k=5 -> [5,4,3,2,1]",
		listToSlice(reverseKGroupRecursive(buildList([]int{1, 2, 3, 4, 5}), 5)),
		[]int{5, 4, 3, 2, 1})

	test("[1,2,3,4] k=2 -> [2,1,4,3]",
		listToSlice(reverseKGroupRecursive(buildList([]int{1, 2, 3, 4}), 2)),
		[]int{2, 1, 4, 3})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
