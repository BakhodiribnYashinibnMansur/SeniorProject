package main

import (
	"fmt"
	"reflect"
)

// ============================================================
// 0002. Add Two Numbers
// https://leetcode.com/problems/add-two-numbers/
// Difficulty: Medium
// Tags: Linked List, Math, Recursion
// ============================================================

// ListNode — singly-linked list node
type ListNode struct {
	Val  int
	Next *ListNode
}

// addTwoNumbers — Optimal Solution (Simultaneous Traversal with Carry)
// Approach: Iterate both lists digit by digit, propagate carry forward
// Time:  O(max(m,n)) — traverse both lists once, where m and n are their lengths
// Space: O(max(m,n)) — the result list has at most max(m,n)+1 nodes
func addTwoNumbers(l1 *ListNode, l2 *ListNode) *ListNode {
	// Dummy head makes it easy to append nodes without a nil check
	dummy := &ListNode{}
	curr := dummy
	carry := 0

	// Continue until both lists are exhausted AND carry is zero
	for l1 != nil || l2 != nil || carry != 0 {
		sum := carry

		// Add digit from l1 (if available)
		if l1 != nil {
			sum += l1.Val
			l1 = l1.Next
		}

		// Add digit from l2 (if available)
		if l2 != nil {
			sum += l2.Val
			l2 = l2.Next
		}

		// Compute new carry and current digit
		carry = sum / 10
		digit := sum % 10

		// Append new node to result
		curr.Next = &ListNode{Val: digit}
		curr = curr.Next
	}

	// Return the real head (skip dummy)
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

	// Test 1: Basic case — 342 + 465 = 807
	test("342 + 465 = 807",
		listToSlice(addTwoNumbers(buildList([]int{2, 4, 3}), buildList([]int{5, 6, 4}))),
		[]int{7, 0, 8})

	// Test 2: Both zeros
	test("0 + 0 = 0",
		listToSlice(addTwoNumbers(buildList([]int{0}), buildList([]int{0}))),
		[]int{0})

	// Test 3: Carry propagation across all digits — 9999999 + 9999 = 10009998
	test("9999999 + 9999 = 10009998",
		listToSlice(addTwoNumbers(buildList([]int{9, 9, 9, 9, 9, 9, 9}), buildList([]int{9, 9, 9, 9}))),
		[]int{8, 9, 9, 9, 0, 0, 0, 1})

	// Test 4: Single digit addition without carry — 1 + 2 = 3
	test("1 + 2 = 3",
		listToSlice(addTwoNumbers(buildList([]int{1}), buildList([]int{2}))),
		[]int{3})

	// Test 5: Single digit addition with carry — 5 + 5 = 10
	test("5 + 5 = 10",
		listToSlice(addTwoNumbers(buildList([]int{5}), buildList([]int{5}))),
		[]int{0, 1})

	// Test 6: Different lengths — 99 + 1 = 100
	test("99 + 1 = 100",
		listToSlice(addTwoNumbers(buildList([]int{9, 9}), buildList([]int{1}))),
		[]int{0, 0, 1})

	// Test 7: l1 longer than l2 — 123 + 4 = 127
	test("123 + 4 = 127",
		listToSlice(addTwoNumbers(buildList([]int{3, 2, 1}), buildList([]int{4}))),
		[]int{7, 2, 1})

	// Test 8: l2 longer than l1 — 5 + 678 = 683
	test("5 + 678 = 683",
		listToSlice(addTwoNumbers(buildList([]int{5}), buildList([]int{8, 7, 6}))),
		[]int{3, 8, 6})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
