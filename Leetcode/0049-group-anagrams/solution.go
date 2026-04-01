package main

import (
	"fmt"
	"sort"
	"strings"
)

// ============================================================
// 0049. Group Anagrams
// https://leetcode.com/problems/group-anagrams/
// Difficulty: Medium
// Tags: Array, Hash Table, String, Sorting
// ============================================================

// groupAnagrams — Optimal Solution (Character Count as Key)
// Approach: Use character frequency array as Hash Map key
// Time:  O(n * k) — n strings, each of length k
// Space: O(n * k) — storing all strings in the Hash Map
func groupAnagrams(strs []string) [][]string {
	// Hash Map: character count array → list of original strings
	// In Go, arrays (not slices) are comparable and can be map keys
	groups := make(map[[26]byte][]string)

	for _, s := range strs {
		// Count frequency of each character (a-z)
		var count [26]byte
		for _, c := range s {
			count[c-'a']++
		}

		// Use the count array directly as key
		// Anagrams produce the same count array
		groups[count] = append(groups[count], s)
	}

	// Collect all groups
	result := make([][]string, 0, len(groups))
	for _, group := range groups {
		result = append(result, group)
	}
	return result
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0

	test := func(name string, got, expected [][]string) {
		// Sort inner and outer for comparison
		sortGroups := func(groups [][]string) []string {
			sorted := make([]string, len(groups))
			for i, g := range groups {
				sort.Strings(g)
				sorted[i] = strings.Join(g, ",")
			}
			sort.Strings(sorted)
			return sorted
		}

		gotSorted := sortGroups(got)
		expSorted := sortGroups(expected)

		match := len(gotSorted) == len(expSorted)
		if match {
			for i := range gotSorted {
				if gotSorted[i] != expSorted[i] {
					match = false
					break
				}
			}
		}

		if match {
			fmt.Printf("✅ PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("❌ FAIL: %s\n  Got:      %v\n  Expected: %v\n", name, got, expected)
			failed++
		}
	}

	// Test 1: Main example
	test("Main example",
		groupAnagrams([]string{"eat", "tea", "tan", "ate", "nat", "bat"}),
		[][]string{{"bat"}, {"nat", "tan"}, {"ate", "eat", "tea"}})

	// Test 2: Empty string
	test("Empty string",
		groupAnagrams([]string{""}),
		[][]string{{""}})

	// Test 3: Single character
	test("Single character",
		groupAnagrams([]string{"a"}),
		[][]string{{"a"}})

	// Test 4: No anagrams
	test("No anagrams",
		groupAnagrams([]string{"abc", "def", "ghi"}),
		[][]string{{"abc"}, {"def"}, {"ghi"}})

	// Test 5: All anagrams
	test("All anagrams",
		groupAnagrams([]string{"abc", "bca", "cab"}),
		[][]string{{"abc", "bca", "cab"}})

	// Test 6: Duplicate strings
	test("Duplicate strings",
		groupAnagrams([]string{"a", "a"}),
		[][]string{{"a", "a"}})

	// Test 7: Mixed lengths
	test("Mixed lengths",
		groupAnagrams([]string{"a", "ab", "ba", "abc", "bca"}),
		[][]string{{"a"}, {"ab", "ba"}, {"abc", "bca"}})

	// Test 8: Multiple empty strings
	test("Multiple empty strings",
		groupAnagrams([]string{"", ""}),
		[][]string{{"", ""}})

	// Test 9: Long anagram group
	test("Long anagram group",
		groupAnagrams([]string{"listen", "silent", "enlist", "inlets", "tinsel"}),
		[][]string{{"listen", "silent", "enlist", "inlets", "tinsel"}})

	// Results
	fmt.Printf("\n📊 Results: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
