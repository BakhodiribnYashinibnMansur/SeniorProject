# ============================================================
# 0003. Longest Substring Without Repeating Characters
# https://leetcode.com/problems/longest-substring-without-repeating-characters/
# Difficulty: Medium
# Tags: Hash Table, String, Sliding Window
# ============================================================


class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        """
        Optimal Solution (Sliding Window with Last-Seen Index Map)
        Approach: Maintain a window [left, right]; on duplicate, jump left past the last occurrence
        Time:  O(n) — each character is visited at most twice (once by right, once when left jumps)
        Space: O(min(n, a)) — map holds at most a unique characters, where a = alphabet size
        """
        # Map: character → its most recently seen index
        last_seen: dict[str, int] = {}

        max_len = 0
        left = 0

        for right, ch in enumerate(s):
            # If the character was seen before AND it is inside the current window,
            # move left pointer past the duplicate to shrink the window
            if ch in last_seen and last_seen[ch] >= left:
                left = last_seen[ch] + 1

            # Update the most recent index of this character
            last_seen[ch] = right

            # Update the maximum window length
            window_len = right - left + 1
            if window_len > max_len:
                max_len = window_len

        return max_len


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
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    # Test 1: Classic example — "abc" is the longest
    test('"abcabcbb" → 3', sol.lengthOfLongestSubstring("abcabcbb"), 3)

    # Test 2: All same characters
    test('"bbbbb" → 1', sol.lengthOfLongestSubstring("bbbbb"), 1)

    # Test 3: Longest at the end — "wke"
    test('"pwwkew" → 3', sol.lengthOfLongestSubstring("pwwkew"), 3)

    # Test 4: Empty string
    test('"" → 0', sol.lengthOfLongestSubstring(""), 0)

    # Test 5: Single character
    test('"a" → 1', sol.lengthOfLongestSubstring("a"), 1)

    # Test 6: All unique characters
    test('"abcdef" → 6', sol.lengthOfLongestSubstring("abcdef"), 6)

    # Test 7: Digits and symbols
    test('"1234567890" → 10', sol.lengthOfLongestSubstring("1234567890"), 10)

    # Test 8: Duplicate at start and end
    test('"dvdf" → 3', sol.lengthOfLongestSubstring("dvdf"), 3)

    # Test 9: Space characters
    test('"a b" → 3', sol.lengthOfLongestSubstring("a b"), 3)

    print(f"\n📊 Results: {passed} passed, {failed} failed, {passed + failed} total")
