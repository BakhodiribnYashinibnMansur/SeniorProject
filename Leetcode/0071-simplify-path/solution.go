package main

import (
	"fmt"
	"strings"
)

// ============================================================
// 0071. Simplify Path
// https://leetcode.com/problems/simplify-path/
// Difficulty: Medium
// Tags: String, Stack
// ============================================================

// simplifyPath — Optimal Solution (Split + Stack)
// Approach: split by '/', use stack to handle '..' (pop), '.' / '' (skip),
//   and other tokens (push).
// Time:  O(n)
// Space: O(n)
func simplifyPath(path string) string {
	parts := strings.Split(path, "/")
	stack := []string{}
	for _, p := range parts {
		if p == "" || p == "." {
			continue
		}
		if p == ".." {
			if len(stack) > 0 {
				stack = stack[:len(stack)-1]
			}
			continue
		}
		stack = append(stack, p)
	}
	return "/" + strings.Join(stack, "/")
}

// simplifyPathManual — Manual Parser (no split allocation)
// Time:  O(n)
// Space: O(n)
func simplifyPathManual(path string) string {
	stack := []string{}
	i, n := 0, len(path)
	for i < n {
		for i < n && path[i] == '/' {
			i++
		}
		j := i
		for j < n && path[j] != '/' {
			j++
		}
		seg := path[i:j]
		i = j
		if seg == "" || seg == "." {
			continue
		}
		if seg == ".." {
			if len(stack) > 0 {
				stack = stack[:len(stack)-1]
			}
			continue
		}
		stack = append(stack, seg)
	}
	return "/" + strings.Join(stack, "/")
}

// ============================================================
// Test Cases
// ============================================================

func main() {
	passed, failed := 0, 0
	test := func(name, got, expected string) {
		if got == expected {
			fmt.Printf("PASS: %s\n", name)
			passed++
		} else {
			fmt.Printf("FAIL: %s\n  Got:      %q\n  Expected: %q\n", name, got, expected)
			failed++
		}
	}

	cases := []struct {
		name, path, want string
	}{
		{"Trailing slash", "/home/", "/home"},
		{"Up from root", "/../", "/"},
		{"Double slash", "/home//foo/", "/home/foo"},
		{"Mixed", "/a/./b/../../c/", "/c"},
		{"Just root", "/", "/"},
		{"Many ups", "/a/b/c/../../../", "/"},
		{"Hidden file", "/.hidden/file", "/.hidden/file"},
		{"Three dots", "/.../", "/..."},
		{"Multiple slashes", "//", "/"},
		{"Long names", "/abc/def/", "/abc/def"},
		{"Mixed with up at start", "/../abc/", "/abc"},
		{"Trailing trailing", "/abc//", "/abc"},
	}

	fmt.Println("=== Split + Stack ===")
	for _, c := range cases {
		test(c.name, simplifyPath(c.path), c.want)
	}
	fmt.Println("\n=== Manual Parser ===")
	for _, c := range cases {
		test("Manual "+c.name, simplifyPathManual(c.path), c.want)
	}

	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
