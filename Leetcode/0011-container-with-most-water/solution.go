package main

import "fmt"

// ============================================================
// 0011. Container With Most Water
// https://leetcode.com/problems/container-with-most-water/
// Difficulty: Medium
// Tags: Array, Two Pointers, Greedy
// ============================================================

// maxArea — Optimal Solution (Two Pointers)
// Approach: Start from both ends, move the shorter line inward
// Time:  O(n) — single pass through the array
// Space: O(1) — only two pointers and a variable for max area
func maxArea(height []int) int {
	// Two Pointers: start from the widest container
	// and move the shorter side inward
	left, right := 0, len(height)-1
	maxWater := 0

	for left < right {
		// Calculate the area: width * min(height[left], height[right])
		width := right - left
		h := height[left]
		if height[right] < h {
			h = height[right]
		}
		area := width * h

		// Update the maximum area
		if area > maxWater {
			maxWater = area
		}

		// Move the pointer with the shorter line
		// Moving the taller line can never increase the area
		if height[left] < height[right] {
			left++
		} else {
			right--
		}
	}

	return maxWater
}

// maxAreaBruteForce — Brute Force approach
// Approach: Check all pairs of lines
// Time:  O(n²) — check every pair
// Space: O(1) — no extra memory
func maxAreaBruteForce(height []int) int {
	n := len(height)
	maxWater := 0

	for i := 0; i < n; i++ {
		for j := i + 1; j < n; j++ {
			// Width between the two lines
			width := j - i

			// Height is limited by the shorter line
			h := height[i]
			if height[j] < h {
				h = height[j]
			}

			// Calculate area
			area := width * h
			if area > maxWater {
				maxWater = area
			}
		}
	}

	return maxWater
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	// Test helper function
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
	test("Example 1", maxArea([]int{1, 8, 6, 2, 5, 4, 8, 3, 7}), 49)

	// Test 2: LeetCode Example 2
	test("Example 2", maxArea([]int{1, 1}), 1)

	// Test 3: Decreasing heights
	test("Decreasing heights", maxArea([]int{5, 4, 3, 2, 1}), 6)

	// Test 4: Increasing heights
	test("Increasing heights", maxArea([]int{1, 2, 3, 4, 5}), 6)

	// Test 5: Same heights
	test("Same heights", maxArea([]int{3, 3, 3, 3, 3}), 12)

	// Test 6: Two elements
	test("Two elements", maxArea([]int{4, 7}), 4)

	// Test 7: Peak in the middle
	test("Peak in middle", maxArea([]int{1, 2, 4, 3}), 4)

	// Test 8: Large values at ends
	test("Large values at ends", maxArea([]int{10, 1, 1, 1, 10}), 40)

	// Test 9: Single tall line
	test("Single tall line", maxArea([]int{1, 1, 1, 100, 1, 1, 1}), 6)

	fmt.Println("\n=== Brute Force ===")

	// Test 10: Brute Force — Example 1
	test("BF Example 1", maxAreaBruteForce([]int{1, 8, 6, 2, 5, 4, 8, 3, 7}), 49)

	// Test 11: Brute Force — Example 2
	test("BF Example 2", maxAreaBruteForce([]int{1, 1}), 1)

	// Test 12: Brute Force — Same heights
	test("BF Same heights", maxAreaBruteForce([]int{3, 3, 3, 3, 3}), 12)

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
