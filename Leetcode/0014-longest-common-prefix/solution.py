# ============================================================
# 0014. Longest Common Prefix
# https://leetcode.com/problems/longest-common-prefix/
# Difficulty: Easy
# Tags: String, Trie
# ============================================================


class Solution:
    def longestCommonPrefix(self, strs: list[str]) -> str:
        """
        Approach 1: Vertical Scanning
        Compare characters column by column across all strings
        Time:  O(S) — where S is the sum of all characters in all strings
        Space: O(1) — only uses a few variables
        """
        if not strs:
            return ""

        # Use the first string as reference
        # Compare each character position across all strings
        for i in range(len(strs[0])):
            ch = strs[0][i]

            for j in range(1, len(strs)):
                # If we've reached the end of any string, or characters don't match
                if i >= len(strs[j]) or strs[j][i] != ch:
                    return strs[0][:i]

        # The entire first string is the common prefix
        return strs[0]

    def longestCommonPrefixHorizontal(self, strs: list[str]) -> str:
        """
        Approach 2: Horizontal Scanning
        Start with first string as prefix, reduce it pairwise
        Time:  O(S) — where S is the sum of all characters in all strings
        Space: O(1) — modifies prefix in place using slicing
        """
        if not strs:
            return ""

        # Start with the first string as the prefix
        prefix = strs[0]

        # Compare prefix with each subsequent string
        for i in range(1, len(strs)):
            # Shrink prefix until it matches the beginning of strs[i]
            while not strs[i].startswith(prefix):
                prefix = prefix[:-1]
                if not prefix:
                    return ""

        return prefix


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if got == expected:
            print(f"✅ PASS: {name}")
            passed += 1
        else:
            print(f"❌ FAIL: {name}")
            print(f"  Got:      {got!r}")
            print(f"  Expected: {expected!r}")
            failed += 1

    print("=== Approach 1: Vertical Scanning ===")

    # Test 1: LeetCode Example 1 — common prefix "fl"
    test("Example 1: flower/flow/flight",
         sol.longestCommonPrefix(["flower", "flow", "flight"]), "fl")

    # Test 2: LeetCode Example 2 — no common prefix
    test("Example 2: dog/racecar/car",
         sol.longestCommonPrefix(["dog", "racecar", "car"]), "")

    # Test 3: Single string
    test("Single string",
         sol.longestCommonPrefix(["alone"]), "alone")

    # Test 4: All identical strings
    test("All identical",
         sol.longestCommonPrefix(["abc", "abc", "abc"]), "abc")

    # Test 5: Empty string in array
    test("Empty string in array",
         sol.longestCommonPrefix(["abc", "", "abc"]), "")

    # Test 6: Single character strings
    test("Single char strings",
         sol.longestCommonPrefix(["a", "a", "a"]), "a")

    # Test 7: First char mismatch
    test("First char mismatch",
         sol.longestCommonPrefix(["abc", "xyz", "def"]), "")

    # Test 8: Two strings with partial match
    test("Two strings partial",
         sol.longestCommonPrefix(["interview", "internet"]), "inter")

    # Test 9: One character prefix
    test("One char prefix",
         sol.longestCommonPrefix(["ab", "ac", "ad"]), "a")

    print()
    print("=== Approach 2: Horizontal Scanning ===")

    # Test 1: LeetCode Example 1 — common prefix "fl"
    test("Example 1: flower/flow/flight",
         sol.longestCommonPrefixHorizontal(["flower", "flow", "flight"]), "fl")

    # Test 2: LeetCode Example 2 — no common prefix
    test("Example 2: dog/racecar/car",
         sol.longestCommonPrefixHorizontal(["dog", "racecar", "car"]), "")

    # Test 3: Single string
    test("Single string",
         sol.longestCommonPrefixHorizontal(["alone"]), "alone")

    # Test 4: All identical strings
    test("All identical",
         sol.longestCommonPrefixHorizontal(["abc", "abc", "abc"]), "abc")

    # Test 5: Empty string in array
    test("Empty string in array",
         sol.longestCommonPrefixHorizontal(["abc", "", "abc"]), "")

    # Test 6: Single character strings
    test("Single char strings",
         sol.longestCommonPrefixHorizontal(["a", "a", "a"]), "a")

    # Test 7: First char mismatch
    test("First char mismatch",
         sol.longestCommonPrefixHorizontal(["abc", "xyz", "def"]), "")

    # Test 8: Two strings with partial match
    test("Two strings partial",
         sol.longestCommonPrefixHorizontal(["interview", "internet"]), "inter")

    # Test 9: One character prefix
    test("One char prefix",
         sol.longestCommonPrefixHorizontal(["ab", "ac", "ad"]), "a")

    # Results
    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
