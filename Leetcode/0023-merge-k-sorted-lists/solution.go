package main

import (
	"container/heap"
	"fmt"
	"reflect"
	"sort"
)

// ============================================================
// 0023. Merge k Sorted Lists
// https://leetcode.com/problems/merge-k-sorted-lists/
// Difficulty: Hard
// Tags: Linked List, Divide and Conquer, Heap (Priority Queue), Merge Sort
// ============================================================

// ListNode — singly-linked list node
type ListNode struct {
	Val  int
	Next *ListNode
}

// MinHeap implements heap.Interface for *ListNode
type MinHeap []*ListNode

func (h MinHeap) Len() int            { return len(h) }
func (h MinHeap) Less(i, j int) bool   { return h[i].Val < h[j].Val }
func (h MinHeap) Swap(i, j int)        { h[i], h[j] = h[j], h[i] }
func (h *MinHeap) Push(x interface{})  { *h = append(*h, x.(*ListNode)) }
func (h *MinHeap) Pop() interface{} {
	old := *h
	n := len(old)
	x := old[n-1]
	*h = old[:n-1]
	return x
}

// mergeKListsBrute — Approach 1: Brute Force (Collect All Values, Sort)
// Time:  O(N log N) — sorting dominates
// Space: O(N) — storing all values
func mergeKListsBrute(lists []*ListNode) *ListNode {
	vals := []int{}
	for _, l := range lists {
		for l != nil {
			vals = append(vals, l.Val)
			l = l.Next
		}
	}
	sort.Ints(vals)
	dummy := &ListNode{}
	curr := dummy
	for _, v := range vals {
		curr.Next = &ListNode{Val: v}
		curr = curr.Next
	}
	return dummy.Next
}

// mergeKListsHeap — Approach 2: Min Heap / Priority Queue
// Time:  O(N log k) — each node pushed/popped from heap of size k
// Space: O(k) — heap stores at most k nodes
func mergeKListsHeap(lists []*ListNode) *ListNode {
	h := &MinHeap{}
	heap.Init(h)
	for _, l := range lists {
		if l != nil {
			heap.Push(h, l)
		}
	}
	dummy := &ListNode{}
	curr := dummy
	for h.Len() > 0 {
		node := heap.Pop(h).(*ListNode)
		curr.Next = node
		curr = curr.Next
		if node.Next != nil {
			heap.Push(h, node.Next)
		}
	}
	return dummy.Next
}

// mergeKLists — Approach 3: Divide and Conquer (Merge Sort Style)
// Time:  O(N log k) — O(log k) rounds, each processing all N nodes
// Space: O(1) — iterative pair-wise merging
func mergeKLists(lists []*ListNode) *ListNode {
	if len(lists) == 0 {
		return nil
	}
	for len(lists) > 1 {
		merged := []*ListNode{}
		for i := 0; i < len(lists); i += 2 {
			var l2 *ListNode
			if i+1 < len(lists) {
				l2 = lists[i+1]
			}
			merged = append(merged, mergeTwoLists(lists[i], l2))
		}
		lists = merged
	}
	return lists[0]
}

// mergeTwoLists merges two sorted linked lists into one sorted list.
func mergeTwoLists(l1, l2 *ListNode) *ListNode {
	dummy := &ListNode{}
	curr := dummy
	for l1 != nil && l2 != nil {
		if l1.Val <= l2.Val {
			curr.Next = l1
			l1 = l1.Next
		} else {
			curr.Next = l2
			l2 = l2.Next
		}
		curr = curr.Next
	}
	if l1 != nil {
		curr.Next = l1
	} else {
		curr.Next = l2
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

// buildLists creates a slice of linked lists from a slice of integer slices
func buildLists(arrays [][]int) []*ListNode {
	lists := make([]*ListNode, len(arrays))
	for i, arr := range arrays {
		lists[i] = buildList(arr)
	}
	return lists
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

	// --- Test Divide and Conquer (primary solution) ---
	fmt.Println("Approach 3: Divide and Conquer")

	// Test 1: LeetCode Example 1
	test("Example 1: [[1,4,5],[1,3,4],[2,6]]",
		listToSlice(mergeKLists(buildLists([][]int{{1, 4, 5}, {1, 3, 4}, {2, 6}}))),
		[]int{1, 1, 2, 3, 4, 4, 5, 6})

	// Test 2: Empty input
	test("Example 2: []",
		listToSlice(mergeKLists([]*ListNode{})),
		[]int{})

	// Test 3: Single empty list
	test("Example 3: [[]]",
		listToSlice(mergeKLists(buildLists([][]int{{}}))),
		[]int{})

	// Test 4: Single list
	test("Single list: [[1,2,3]]",
		listToSlice(mergeKLists(buildLists([][]int{{1, 2, 3}}))),
		[]int{1, 2, 3})

	// Test 5: Two lists
	test("Two lists: [[1,3,5],[2,4,6]]",
		listToSlice(mergeKLists(buildLists([][]int{{1, 3, 5}, {2, 4, 6}}))),
		[]int{1, 2, 3, 4, 5, 6})

	// Test 6: Lists with duplicates
	test("Duplicates: [[1,1],[1,1]]",
		listToSlice(mergeKLists(buildLists([][]int{{1, 1}, {1, 1}}))),
		[]int{1, 1, 1, 1})

	// Test 7: Negative values
	test("Negative values: [[-3,-1],[0,2],[-2,1]]",
		listToSlice(mergeKLists(buildLists([][]int{{-3, -1}, {0, 2}, {-2, 1}}))),
		[]int{-3, -2, -1, 0, 1, 2})

	// Test 8: Multiple empty lists
	test("Multiple empty: [[], [], []]",
		listToSlice(mergeKLists(buildLists([][]int{{}, {}, {}}))),
		[]int{})

	// Test 9: Mixed empty and non-empty
	test("Mixed: [[], [1], [], [2,3]]",
		listToSlice(mergeKLists(buildLists([][]int{{}, {1}, {}, {2, 3}}))),
		[]int{1, 2, 3})

	// --- Test Heap approach ---
	fmt.Println("\nApproach 2: Min Heap")

	test("Heap: Example 1",
		listToSlice(mergeKListsHeap(buildLists([][]int{{1, 4, 5}, {1, 3, 4}, {2, 6}}))),
		[]int{1, 1, 2, 3, 4, 4, 5, 6})

	test("Heap: Empty input",
		listToSlice(mergeKListsHeap([]*ListNode{})),
		[]int{})

	test("Heap: Negative values",
		listToSlice(mergeKListsHeap(buildLists([][]int{{-3, -1}, {0, 2}, {-2, 1}}))),
		[]int{-3, -2, -1, 0, 1, 2})

	// --- Test Brute Force ---
	fmt.Println("\nApproach 1: Brute Force")

	test("Brute: Example 1",
		listToSlice(mergeKListsBrute(buildLists([][]int{{1, 4, 5}, {1, 3, 4}, {2, 6}}))),
		[]int{1, 1, 2, 3, 4, 4, 5, 6})

	test("Brute: Empty input",
		listToSlice(mergeKListsBrute([]*ListNode{})),
		[]int{})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
