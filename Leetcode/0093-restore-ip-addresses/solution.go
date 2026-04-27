package main

import (
	"fmt"
	"sort"
	"strconv"
	"strings"
)

// 0093. Restore IP Addresses
func restoreIpAddresses(s string) []string {
	result := []string{}
	var bt func(start, segs int, parts []string)
	bt = func(start, segs int, parts []string) {
		if segs == 4 {
			if start == len(s) {
				result = append(result, strings.Join(parts, "."))
			}
			return
		}
		remaining := len(s) - start
		need := 4 - segs
		if remaining < need || remaining > need*3 {
			return
		}
		for length := 1; length <= 3 && start+length <= len(s); length++ {
			seg := s[start : start+length]
			if len(seg) > 1 && seg[0] == '0' {
				continue
			}
			n, _ := strconv.Atoi(seg)
			if n > 255 {
				continue
			}
			newParts := append([]string{}, parts...)
			newParts = append(newParts, seg)
			bt(start+length, segs+1, newParts)
		}
	}
	bt(0, 0, []string{})
	return result
}

func main() {
	passed, failed := 0, 0
	test := func(name string, got, exp []string) {
		sort.Strings(got)
		sort.Strings(exp)
		if len(got) == len(exp) {
			ok := true
			for i := range got {
				if got[i] != exp[i] {
					ok = false
					break
				}
			}
			if ok {
				fmt.Printf("PASS: %s\n", name)
				passed++
				return
			}
		}
		fmt.Printf("FAIL: %s got=%v want=%v\n", name, got, exp)
		failed++
	}
	cases := []struct {
		name string
		s    string
		want []string
	}{
		{"Example 1", "25525511135", []string{"255.255.11.135", "255.255.111.35"}},
		{"Example 2", "0000", []string{"0.0.0.0"}},
		{"Example 3", "101023", []string{"1.0.10.23", "1.0.102.3", "10.1.0.23", "10.10.2.3", "101.0.2.3"}},
		{"Too short", "111", []string{}},
		{"Too long", "1111111111111", []string{}},
		{"Min", "1111", []string{"1.1.1.1"}},
	}
	for _, c := range cases {
		test(c.name, restoreIpAddresses(c.s), c.want)
	}
	fmt.Printf("\nResults: %d passed, %d failed, %d total\n", passed, failed, passed+failed)
}
