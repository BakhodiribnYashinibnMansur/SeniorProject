package main

import "fmt"

// ============================================================
// 0021. Merge Two Sorted Lists
// https://leetcode.com/problems/merge-two-sorted-lists/
// Difficulty: Easy
// Tags: Linked List, Recursion
// ============================================================

// ListNode -- Definition for singly-linked list
type ListNode struct {
	Val  int
	Next *ListNode
}

// mergeTwoLists -- Optimal Solution (Iterative)
// Approach: Use a dummy node and two pointers to merge both lists
// Time:  O(n + m) -- each node is visited exactly once
// Space: O(1) -- only constant extra pointers used
func mergeTwoLists(list1 *ListNode, list2 *ListNode) *ListNode {
	dummy := &ListNode{}
	current := dummy

	for list1 != nil && list2 != nil {
		if list1.Val <= list2.Val {
			current.Next = list1
			list1 = list1.Next
		} else {
			current.Next = list2
			list2 = list2.Next
		}
		current = current.Next
	}

	// Attach remaining nodes
	if list1 != nil {
		current.Next = list1
	} else {
		current.Next = list2
	}

	return dummy.Next
}

// ============================================================
// Helper Functions
// ============================================================

// listToLinked converts a slice to a linked list
func listToLinked(arr []int) *ListNode {
	dummy := &ListNode{}
	current := dummy
	for _, val := range arr {
		current.Next = &ListNode{Val: val}
		current = current.Next
	}
	return dummy.Next
}

// linkedToList converts a linked list to a slice
func linkedToList(head *ListNode) []int {
	result := []int{}
	for head != nil {
		result = append(result, head.Val)
		head = head.Next
	}
	return result
}

// equal checks if two slices are equal
func equal(a, b []int) bool {
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

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, list1Arr, list2Arr, expected []int) {
		l1 := listToLinked(list1Arr)
		l2 := listToLinked(list2Arr)
		result := linkedToList(mergeTwoLists(l1, l2))
		if equal(result, expected) {
			fmt.Printf("\u2705 PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("\u274c FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, result, expected)
			failed++
		}
	}

	// Test 1: Basic merge
	test("Basic merge", []int{1, 2, 4}, []int{1, 3, 4}, []int{1, 1, 2, 3, 4, 4})

	// Test 2: Both empty
	test("Both empty", []int{}, []int{}, []int{})

	// Test 3: First empty
	test("First empty", []int{}, []int{0}, []int{0})

	// Test 4: Second empty
	test("Second empty", []int{1}, []int{}, []int{1})

	// Test 5: Single elements
	test("Single elements", []int{2}, []int{1}, []int{1, 2})

	// Test 6: Non-overlapping ranges
	test("Non-overlapping", []int{1, 2, 3}, []int{4, 5, 6}, []int{1, 2, 3, 4, 5, 6})

	// Test 7: Equal elements
	test("Equal elements", []int{1, 1}, []int{1, 1}, []int{1, 1, 1, 1})

	// Test 8: Negative values
	test("Negative values", []int{-3, -1}, []int{-2, 0}, []int{-3, -2, -1, 0})

	// Test 9: One much longer
	test("One much longer", []int{1}, []int{2, 3, 4, 5}, []int{1, 2, 3, 4, 5})

	// Results
	fmt.Printf("\n\U0001f4ca Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
