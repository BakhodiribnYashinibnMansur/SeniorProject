package main

import "fmt"

// ============================================================
// 0042. Trapping Rain Water
// https://leetcode.com/problems/trapping-rain-water/
// Difficulty: Hard
// Tags: Array, Two Pointers, Dynamic Programming, Stack, Monotonic Stack
// ============================================================

// trap — Optimal Solution (Two Pointers)
// Approach: Two pointers from both ends, track running leftMax and rightMax
// Time:  O(n) — each element is visited exactly once
// Space: O(1) — only a few variables
func trap(height []int) int {
	if len(height) <= 2 {
		return 0
	}

	left, right := 0, len(height)-1
	leftMax, rightMax := 0, 0
	water := 0

	for left < right {
		if height[left] < height[right] {
			// Left side is the bottleneck
			if height[left] >= leftMax {
				leftMax = height[left]
			} else {
				water += leftMax - height[left]
			}
			left++
		} else {
			// Right side is the bottleneck
			if height[right] >= rightMax {
				rightMax = height[right]
			} else {
				water += rightMax - height[right]
			}
			right--
		}
	}

	return water
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name string, got, expected int) {
		if got == expected {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	fmt.Println("=== Two Pointers (Optimal) ===")

	// Test 1: LeetCode Example 1
	test("Example 1", trap([]int{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}), 6)

	// Test 2: LeetCode Example 2
	test("Example 2", trap([]int{4, 2, 0, 3, 2, 5}), 9)

	// Test 3: No water — ascending
	test("Ascending", trap([]int{1, 2, 3, 4, 5}), 0)

	// Test 4: No water — descending
	test("Descending", trap([]int{5, 4, 3, 2, 1}), 0)

	// Test 5: V-shape
	test("V-shape", trap([]int{3, 0, 3}), 3)

	// Test 6: Single valley
	test("Single valley", trap([]int{5, 1, 5}), 4)

	// Test 7: All zeros
	test("All zeros", trap([]int{0, 0, 0, 0}), 0)

	// Test 8: Single element
	test("Single element", trap([]int{5}), 0)

	// Test 9: Two elements
	test("Two elements", trap([]int{1, 2}), 0)

	// Test 10: Complex terrain
	test("Complex terrain", trap([]int{5, 2, 1, 2, 1, 5}), 14)

	// Test 11: Flat surface
	test("Flat surface", trap([]int{3, 3, 3, 3}), 0)

	// Test 12: W-shape
	test("W-shape", trap([]int{3, 0, 2, 0, 4}), 7)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
