from typing import List

# ============================================================
# 0068. Text Justification
# https://leetcode.com/problems/text-justification/
# Difficulty: Hard
# Tags: Array, String, Simulation
# ============================================================


class Solution:
    def fullJustify(self, words: List[str], maxWidth: int) -> List[str]:
        """
        Optimal Solution (Greedy Line Packing).
        Time:  O(n * maxWidth)
        Space: O(n * maxWidth)
        """
        result: List[str] = []
        n, i = len(words), 0
        while i < n:
            j = i
            line_len = 0
            while j < n and line_len + len(words[j]) + (j - i) <= maxWidth:
                line_len += len(words[j])
                j += 1
            is_last = (j == n)
            if is_last or j - i == 1:
                line = ' '.join(words[i:j])
                line += ' ' * (maxWidth - len(line))
            else:
                gaps = j - i - 1
                slots = maxWidth - line_len
                base, extra = divmod(slots, gaps)
                parts = []
                for k in range(i, j - 1):
                    parts.append(words[k])
                    parts.append(' ' * (base + (1 if k - i < extra else 0)))
                parts.append(words[j - 1])
                line = ''.join(parts)
            result.append(line)
            i = j
        return result


if __name__ == "__main__":
    sol = Solution()
    passed = failed = 0

    def test(name, got, expected):
        global passed, failed
        if got == expected: print(f"PASS: {name}"); passed += 1
        else:
            print(f"FAIL: {name}")
            print(f"  Got:      {got}")
            print(f"  Expected: {expected}")
            failed += 1

    cases = [
        ("Example 1",
         ["This", "is", "an", "example", "of", "text", "justification."], 16,
         [
             "This    is    an",
             "example  of text",
             "justification.  ",
         ]),
        ("Example 2",
         ["What", "must", "be", "acknowledgment", "shall", "be"], 16,
         [
             "What   must   be",
             "acknowledgment  ",
             "shall be        ",
         ]),
        ("Example 3",
         ["Science", "is", "what", "we", "understand", "well", "enough", "to",
          "explain", "to", "a", "computer.", "Art", "is", "everything", "else",
          "we", "do"], 20,
         [
             "Science  is  what we",
             "understand      well",
             "enough to explain to",
             "a  computer.  Art is",
             "everything  else  we",
             "do                  ",
         ]),
        ("Single word", ["Hello"], 10, ["Hello     "]),
        ("Single word equals width", ["abc"], 3, ["abc"]),
        ("Two words last line", ["a", "b"], 5, ["a b  "]),
    ]

    for name, words, width, exp in cases:
        test(name, sol.fullJustify(words, width), exp)

    print(f"\nResults: {passed} passed, {failed} failed, {passed + failed} total")
