# 0068. Text Justification

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Greedy Line Packing](#approach-1-greedy-line-packing)
4. [Approach 2: Greedy with Helper Functions (Cleaner)](#approach-2-greedy-with-helper-functions-cleaner)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [68. Text Justification](https://leetcode.com/problems/text-justification/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Array`, `String`, `Simulation` |

### Description

> Given an array of strings `words` and a width `maxWidth`, format the text such that each line has exactly `maxWidth` characters and is fully (left and right) justified.
>
> You should pack your words in a greedy approach; that is, pack as many words as you can in each line. Pad extra spaces `' '` when necessary so that each line has exactly `maxWidth` characters.
>
> Extra spaces between words should be distributed as evenly as possible. If the number of spaces on a line does not divide evenly between words, the empty slots on the left will be assigned more spaces than the slots on the right.
>
> For the last line of text, it should be left-justified, and no extra space is inserted between words.

### Examples

```
Example 1:
Input: words = ["This", "is", "an", "example", "of", "text", "justification."], maxWidth = 16
Output:
[
   "This    is    an",
   "example  of text",
   "justification.  "
]

Example 2:
Input: words = ["What","must","be","acknowledgment","shall","be"], maxWidth = 16
Output:
[
  "What   must   be",
  "acknowledgment  ",
  "shall be        "
]
```

### Constraints

- `1 <= words.length <= 300`
- `1 <= words[i].length <= 20`
- `words[i]` consists of only English letters and symbols.
- `1 <= maxWidth <= 100`
- `words[i].length <= maxWidth`

---

## Problem Breakdown

### 1. What is being asked?

Lay out a sequence of words into lines, each line exactly `maxWidth` characters wide. Pack as many words as fit (with at least one space between consecutive words), then distribute extra spaces evenly. The last line is left-justified instead of fully justified.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `words` | `str[]` | Words to lay out, in order |
| `maxWidth` | `int` | Exact line width |

### 3. What is the output?

A list of strings, each with length exactly `maxWidth`.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 300`, `maxWidth <= 100` | At most 30,000 characters total. O(n * maxWidth) is fine |
| `word.length <= maxWidth` | Every single word always fits |

### 5. Step-by-step example analysis

#### Example 1: `words = ["This","is","an","example","of","text","justification."], maxWidth = 16`

```text
Line 1: "This is an" (10 chars + 2 spaces = 12). Fits in 16. Try add "example"?
  10 + 1 + 7 = 18 > 16 → stop. Line uses ["This","is","an"], total chars = 8, gaps = 2, slots = 16 - 8 = 8 spaces.
  8 spaces / 2 gaps = 4 each. Result: "This" + "    " + "is" + "    " + "an" = "This    is    an" (16). ✓

Line 2: ["example","of","text"]. 7 + 2 + 4 = 13. Fits. Try add "justification."? 13 + 1 + 14 = 28 > 16, stop.
  total chars = 13, gaps = 2, spaces = 3. 3 / 2 = 1 base, remainder 1 → first gap gets +1.
  "example" + "  " + "of" + " " + "text" = "example  of text" (16). ✓

Line 3: ["justification."]. Last line → left-justified. "justification." + 2 spaces = "justification.  " (16). ✓
```

### 6. Key Observations

1. **Greedy fit** -- For each line, accumulate words while `current_len + len(word) + (number_of_words_so_far) <= maxWidth`. The `+ number_of_words_so_far` accounts for the single space we must add before each new word.
2. **Two distribution rules** --
   - Normal lines: distribute the remaining spaces over the gaps. If `gaps == 0` (single word), left-justify with trailing spaces.
   - Last line: always left-justify with single spaces between words and pad the right.
3. **Extra spaces go left** -- If `total_spaces / gaps` has a remainder, the leftmost `remainder` gaps get one extra space.
4. **Edge case: a line with one word** -- Fully justified one-word lines reduce to left-justified.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Greedy line packing | Always grab the next word if it fits |
| String building | Construct each line carefully |

**Chosen pattern:** `Greedy Line Packing`.

---

## Approach 1: Greedy Line Packing

### Algorithm (step-by-step)

1. Initialize `result = []`, `i = 0`.
2. While `i < len(words)`:
   - Greedy gather: starting at index `i`, accumulate words and a running `length` (sum of word lengths + minimum spaces between them) as long as adding another word fits in `maxWidth`.
   - Let `j` be one past the last index gathered.
   - Build the line:
     - If `j == n` (last line) or `j - i == 1` (only one word): join with single spaces and pad on the right with spaces.
     - Else: distribute spaces. Let `slots = maxWidth - sum_of_word_lengths`, `gaps = j - i - 1`, `base = slots / gaps`, `extra = slots % gaps`. The first `extra` gaps get `base + 1` spaces, the rest get `base`.
   - Append the line to `result`.
   - `i = j`.

### Pseudocode

```text
result = []
i = 0
while i < n:
    j = i
    line_len = 0
    while j < n and line_len + len(words[j]) + (j - i) <= maxWidth:
        line_len += len(words[j])
        j++
    is_last = j == n
    if is_last or j - i == 1:
        line = ' '.join(words[i..j-1])
        line += ' ' * (maxWidth - len(line))
    else:
        gaps = j - i - 1
        slots = maxWidth - line_len
        base, extra = divmod(slots, gaps)
        parts = []
        for k in i..j-2:
            parts.append(words[k])
            parts.append(' ' * (base + (1 if k - i < extra else 0)))
        parts.append(words[j-1])
        line = ''.join(parts)
    result.append(line)
    i = j
return result
```

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n * maxWidth) | Each character constructed once |
| **Space** | O(n * maxWidth) | Output strings |

### Implementation

#### Go

```go
import "strings"

func fullJustify(words []string, maxWidth int) []string {
    result := []string{}
    i := 0
    n := len(words)
    for i < n {
        j := i
        lineLen := 0
        for j < n && lineLen+len(words[j])+(j-i) <= maxWidth {
            lineLen += len(words[j])
            j++
        }
        isLast := j == n
        var line string
        if isLast || j-i == 1 {
            line = strings.Join(words[i:j], " ")
            line += strings.Repeat(" ", maxWidth-len(line))
        } else {
            gaps := j - i - 1
            slots := maxWidth - lineLen
            base := slots / gaps
            extra := slots % gaps
            var sb strings.Builder
            for k := i; k < j-1; k++ {
                sb.WriteString(words[k])
                spaces := base
                if k-i < extra {
                    spaces++
                }
                sb.WriteString(strings.Repeat(" ", spaces))
            }
            sb.WriteString(words[j-1])
            line = sb.String()
        }
        result = append(result, line)
        i = j
    }
    return result
}
```

#### Java

```java
class Solution {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int n = words.length, i = 0;
        while (i < n) {
            int j = i, lineLen = 0;
            while (j < n && lineLen + words[j].length() + (j - i) <= maxWidth) {
                lineLen += words[j].length();
                j++;
            }
            StringBuilder sb = new StringBuilder();
            boolean isLast = (j == n);
            if (isLast || j - i == 1) {
                for (int k = i; k < j; k++) {
                    sb.append(words[k]);
                    if (k < j - 1) sb.append(' ');
                }
                while (sb.length() < maxWidth) sb.append(' ');
            } else {
                int gaps = j - i - 1;
                int slots = maxWidth - lineLen;
                int base = slots / gaps;
                int extra = slots % gaps;
                for (int k = i; k < j - 1; k++) {
                    sb.append(words[k]);
                    int sp = base + ((k - i) < extra ? 1 : 0);
                    for (int s = 0; s < sp; s++) sb.append(' ');
                }
                sb.append(words[j - 1]);
            }
            result.add(sb.toString());
            i = j;
        }
        return result;
    }
}
```

#### Python

```python
class Solution:
    def fullJustify(self, words: List[str], maxWidth: int) -> List[str]:
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
```

### Dry Run

```text
words = ["This","is","an","example","of","text","justification."], maxWidth = 16

i = 0:
  Try j = 0: line_len 0 + 4 + 0 = 4 ≤ 16 → include "This", line_len = 4, j = 1
  Try j = 1: 4 + 2 + 1 = 7 ≤ 16 → include "is", line_len = 6, j = 2
  Try j = 2: 6 + 2 + 2 = 10 ≤ 16 → include "an", line_len = 8, j = 3
  Try j = 3: 8 + 7 + 3 = 18 > 16 → stop
  Line uses [0..2]. gaps = 2, slots = 16 - 8 = 8, base = 4, extra = 0.
  Build: "This" + "    " + "is" + "    " + "an" = "This    is    an" (16)
  i = 3

i = 3: (similar logic)
  Line uses [3..5]. gaps = 2, slots = 16 - 13 = 3, base = 1, extra = 1.
  k=3: "example" + 2 spaces, k=4: "of" + 1 space, last "text"
  → "example  of text" (16)
  i = 6

i = 6: only word "justification." → last line, left-justified.
  "justification." + 2 spaces = "justification.  " (16)
  i = 7 → done.
```

---

## Approach 2: Greedy with Helper Functions (Cleaner)

### Idea

> Same algorithm as Approach 1, but factor out three helpers:
>   1. `gather(start)` returns `(end, lineLen)` for the next greedy line.
>   2. `justifyMiddle(words[i..j], lineLen, maxWidth)` builds a fully justified line.
>   3. `justifyLeft(words[i..j], maxWidth)` builds a left-justified line for the final line or single-word lines.

> Identical complexity, easier to test piece by piece.

### Implementation Sketch

```python
def fullJustify(words, maxWidth):
    n, i = len(words), 0
    out = []
    while i < n:
        j, line_len = gather(words, i, n, maxWidth)
        if j == n or j - i == 1:
            out.append(left_justify(words, i, j, maxWidth))
        else:
            out.append(middle_justify(words, i, j, line_len, maxWidth))
        i = j
    return out
```

> Recommended for production.

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Greedy Inline | O(n * maxWidth) | O(n * maxWidth) | Self-contained | Long single function |
| 2 | Greedy + Helpers | O(n * maxWidth) | O(n * maxWidth) | More testable | Slightly more code |

### Which solution to choose?

Either works on Leetcode. In production, the helper-based version is easier to maintain.

---

## Edge Cases

| # | Case | Reason |
|---|---|---|
| 1 | Single word fills line | `gaps == 0` → left-justify with trailing spaces |
| 2 | Single word per line | Same as above for every line |
| 3 | Last line has multiple words | Always left-justify regardless of fit |
| 4 | Words exactly fit width | `slots == 0`, no extra spaces between |
| 5 | Word shorter than `maxWidth` | Padding correctness |
| 6 | All words equal length | Even space distribution every line |

---

## Common Mistakes

### Mistake 1: Forgetting last-line rule

```python
# WRONG — distributes spaces on the last line
if j - i > 1:
    distribute_spaces(...)
else:
    left_justify(...)

# CORRECT — last line ALWAYS left-justified
if is_last or j - i == 1:
    left_justify(...)
else:
    distribute_spaces(...)
```

**Reason:** The problem explicitly says the last line is left-justified.

### Mistake 2: Wrong space distribution direction

```python
# WRONG — extra spaces go on the right
extra_on_right = slots % gaps
for k in range(i, j - 1):
    ... spaces = base + (1 if (j - 2 - k) < extra_on_right else 0) ...

# CORRECT — extras go LEFT
for k in range(i, j - 1):
    ... spaces = base + (1 if (k - i) < extra else 0) ...
```

**Reason:** The problem specifies extra spaces go to the empty slots on the **left**.

### Mistake 3: Off-by-one in greedy fit check

```python
# WRONG — uses (j - i + 1) instead of (j - i) for spaces
while j < n and line_len + len(words[j]) + (j - i + 1) <= maxWidth:

# CORRECT — exactly (j - i) spaces are needed before adding word j
while j < n and line_len + len(words[j]) + (j - i) <= maxWidth:
```

**Reason:** Adding word at position `j` requires `(j - i)` spaces between the words already chosen and this new word.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [418. Sentence Screen Fitting](https://leetcode.com/problems/sentence-screen-fitting/) | :yellow_circle: Medium | Layout problem |
| 2 | [1071. Greatest Common Divisor of Strings](https://leetcode.com/problems/greatest-common-divisor-of-strings/) | :green_circle: Easy | String packing analogy |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Word stream with highlighted greedy selection per line
> - Visual distribution of spaces (with markers for "extra" spaces)
> - Toggle between full-justified and left-justified for the last line
