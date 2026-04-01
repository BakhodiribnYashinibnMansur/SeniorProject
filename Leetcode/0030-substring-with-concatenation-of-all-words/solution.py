# ============================================================
# 0030. Substring with Concatenation of All Words
# https://leetcode.com/problems/substring-with-concatenation-of-all-words/
# Difficulty: Hard
# Tags: Hash Table, String, Sliding Window
# ============================================================

from collections import Counter, defaultdict


class Solution:
    def findSubstring(self, s: str, words: list[str]) -> list[int]:
        """
        Optimal Solution (Sliding Window with Hash Map)
        Approach: For each of wordLen offsets, slide a window of numWords words
        Time:  O(n * wordLen) — wordLen offsets, each processes n/wordLen words
        Space: O(m)           — frequency maps with at most m distinct words
        """
        if not s or not words:
            return []

        word_len = len(words[0])
        num_words = len(words)
        total_len = word_len * num_words

        if len(s) < total_len:
            return []

        word_freq = Counter(words)
        result = []

        # Try each starting offset from 0 to wordLen-1
        for i in range(word_len):
            left = i
            count = 0
            seen = defaultdict(int)

            # Slide right pointer one word at a time
            for right in range(i, len(s) - word_len + 1, word_len):
                word = s[right:right + word_len]

                if word in word_freq:
                    seen[word] += 1
                    count += 1

                    # Shrink window if word count exceeds target
                    while seen[word] > word_freq[word]:
                        left_word = s[left:left + word_len]
                        seen[left_word] -= 1
                        count -= 1
                        left += word_len

                    # Check if we have a valid concatenation
                    if count == num_words:
                        result.append(left)
                else:
                    # Invalid word — reset the window
                    seen.clear()
                    count = 0
                    left = right + word_len

        return result

    def findSubstringBrute(self, s: str, words: list[str]) -> list[int]:
        """
        Brute Force approach
        Approach: Check every starting position, build frequency map each time
        Time:  O(n * m * wordLen) — n positions, m words per position
        Space: O(m)               — frequency map
        """
        if not s or not words:
            return []

        word_len = len(words[0])
        num_words = len(words)
        total_len = word_len * num_words
        word_freq = Counter(words)
        result = []

        for i in range(len(s) - total_len + 1):
            seen = defaultdict(int)
            valid = True
            for j in range(num_words):
                word = s[i + j * word_len: i + (j + 1) * word_len]
                if word not in word_freq:
                    valid = False
                    break
                seen[word] += 1
                if seen[word] > word_freq[word]:
                    valid = False
                    break
            if valid:
                result.append(i)

        return result


# ============================================================
# Test Cases
# ============================================================

if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name: str, got, expected):
        global passed, failed
        if sorted(got) == sorted(expected):
            print(f"\u2705 PASS: {name}")
            passed += 1
        else:
            print(f"\u274c FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    print("=== Sliding Window with Hash Map (Optimal) ===")

    # Test 1: LeetCode Example 1
    test("Example 1",
         sol.findSubstring("barfoothefoobarman", ["foo", "bar"]),
         [0, 9])

    # Test 2: LeetCode Example 2 — no match
    test("Example 2",
         sol.findSubstring("wordgoodgoodgoodbestword", ["word", "good", "best", "word"]),
         [])

    # Test 3: LeetCode Example 3 — multiple matches
    test("Example 3",
         sol.findSubstring("barfoofoobarthefoobarman", ["bar", "foo", "the"]),
         [6, 9, 12])

    # Test 4: Single character words
    test("Single char words",
         sol.findSubstring("aaa", ["a", "a"]),
         [0, 1])

    # Test 5: All same words
    test("All same words",
         sol.findSubstring("aaa", ["a", "a", "a"]),
         [0])

    # Test 6: No match — string too short
    test("String too short",
         sol.findSubstring("ab", ["abc"]),
         [])

    # Test 7: Exact match
    test("Exact match",
         sol.findSubstring("foobar", ["foo", "bar"]),
         [0])

    # Test 8: Duplicate words with match
    test("Duplicate words match",
         sol.findSubstring("wordgoodgoodgoodbestword", ["word", "good", "best", "good"]),
         [8])

    # Test 9: Single word
    test("Single word",
         sol.findSubstring("foobarfoo", ["foo"]),
         [0, 6])

    # Test 10: Long words
    test("Overlapping pattern",
         sol.findSubstring("lingmindraboofooowingdingbarrede", ["]foo", "bar"]),
         [])

    print("\n=== Brute Force ===")

    test("BF: Example 1",
         sol.findSubstringBrute("barfoothefoobarman", ["foo", "bar"]),
         [0, 9])

    test("BF: Example 2",
         sol.findSubstringBrute("wordgoodgoodgoodbestword", ["word", "good", "best", "word"]),
         [])

    test("BF: Example 3",
         sol.findSubstringBrute("barfoofoobarthefoobarman", ["bar", "foo", "the"]),
         [6, 9, 12])

    # Results
    print(f"\n\U0001f4ca Results: {passed} passed, {failed} failed, {passed + failed} total")
