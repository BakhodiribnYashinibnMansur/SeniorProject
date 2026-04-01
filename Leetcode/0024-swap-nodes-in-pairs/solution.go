package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0024. Swap Nodes in Pairs
// https://leetcode.com/problems/swap-nodes-in-pairs/
// Difficulty: Medium
// Tags: Linked List, Recursion
// ============================================================

// ListNode — singly-linked list node
type ListNode struct {
	Val  int
	Next *ListNode
}

// swapPairs — Iterative approach with dummy node
// Approach: Use a dummy node; for each pair, rewire pointers
// Time:  O(n) — single pass through the list
// Space: O(1) — only constant extra pointers
func swapPairs(head *ListNode) *ListNode {
	dummy := &ListNode{Next: head}
	prev := dummy

	for prev.Next != nil && prev.Next.Next != nil {
		first := prev.Next
		second := prev.Next.Next

		// Rewire pointers
		first.Next = second.Next
		second.Next = first
		prev.Next = second

		// Advance past the swapped pair
		prev = first
	}

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

	// Test 1: Standard even-length list
	test("[1,2,3,4] → [2,1,4,3]",
		listToSlice(swapPairs(buildList([]int{1, 2, 3, 4}))),
		[]int{2, 1, 4, 3})

	// Test 2: Empty list
	test("[] → []",
		listToSlice(swapPairs(buildList([]int{}))),
		[]int{})

	// Test 3: Single node
	test("[1] → [1]",
		listToSlice(swapPairs(buildList([]int{1}))),
		[]int{1})

	// Test 4: Two nodes
	test("[1,2] → [2,1]",
		listToSlice(swapPairs(buildList([]int{1, 2}))),
		[]int{2, 1})

	// Test 5: Odd-length list
	test("[1,2,3] → [2,1,3]",
		listToSlice(swapPairs(buildList([]int{1, 2, 3}))),
		[]int{2, 1, 3})

	// Test 6: Longer even-length list
	test("[1,2,3,4,5,6] → [2,1,4,3,6,5]",
		listToSlice(swapPairs(buildList([]int{1, 2, 3, 4, 5, 6}))),
		[]int{2, 1, 4, 3, 6, 5})

	// Test 7: Longer odd-length list
	test("[1,2,3,4,5] → [2,1,4,3,5]",
		listToSlice(swapPairs(buildList([]int{1, 2, 3, 4, 5}))),
		[]int{2, 1, 4, 3, 5})

	// Test 8: All same values
	test("[5,5,5,5] → [5,5,5,5]",
		listToSlice(swapPairs(buildList([]int{5, 5, 5, 5}))),
		[]int{5, 5, 5, 5})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
