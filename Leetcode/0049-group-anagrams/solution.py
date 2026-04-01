# ============================================================
# 0049. Group Anagrams
# https://leetcode.com/problems/group-anagrams/
# Difficulty: Medium
# Tags: Array, Hash Table, String, Sorting
# ============================================================

from collections import defaultdict


class Solution:
    def groupAnagrams(self, strs: list[str]) -> list[list[str]]:
        """
        Optimal Solution (Character Count as Key)
        Approach: Use character frequency tuple as Hash Map key
        Time:  O(n * k) — n strings, each of length k
        Space: O(n * k) — storing all strings in the Hash Map
        """
        # Hash Map: character count tuple → list of original strings
        groups = defaultdict(list)

        for s in strs:
            # Count frequency of each character (a-z)
            count = [0] * 26
            for c in s:
                count[ord(c) - ord('a')] += 1

            # Tuple is hashable — use as key
            # Anagrams produce the same count tuple
            groups[tuple(count)].append(s)

        return list(groups.values())


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got: list[list[str]], expected: list[list[str]]):
        global passed, failed
        # Sort inner lists and outer list for comparison
        got_sorted = sorted([sorted(g) for g in got])
        exp_sorted = sorted([sorted(g) for g in expected])
        if got_sorted == exp_sorted:
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Main example
    test("Main example",
         sol.groupAnagrams(["eat", "tea", "tan", "ate", "nat", "bat"]),
         [["bat"], ["nat", "tan"], ["ate", "eat", "tea"]])

    # Test 2: Empty string
    test("Empty string",
         sol.groupAnagrams([""]),
         [[""]])

    # Test 3: Single character
    test("Single character",
         sol.groupAnagrams(["a"]),
         [["a"]])

    # Test 4: No anagrams
    test("No anagrams",
         sol.groupAnagrams(["abc", "def", "ghi"]),
         [["abc"], ["def"], ["ghi"]])

    # Test 5: All anagrams
    test("All anagrams",
         sol.groupAnagrams(["abc", "bca", "cab"]),
         [["abc", "bca", "cab"]])

    # Test 6: Duplicate strings
    test("Duplicate strings",
         sol.groupAnagrams(["a", "a"]),
         [["a", "a"]])

    # Test 7: Mixed lengths
    test("Mixed lengths",
         sol.groupAnagrams(["a", "ab", "ba", "abc", "bca"]),
         [["a"], ["ab", "ba"], ["abc", "bca"]])

    # Test 8: Multiple empty strings
    test("Multiple empty strings",
         sol.groupAnagrams(["", ""]),
         [["", ""]])

    # Test 9: Long anagram group
    test("Long anagram group",
         sol.groupAnagrams(["listen", "silent", "enlist", "inlets", "tinsel"]),
         [["listen", "silent", "enlist", "inlets", "tinsel"]])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
