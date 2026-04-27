# 0058. Length of Last Word

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Split and Pick Last](#approach-1-split-and-pick-last)
4. [Approach 2: Trim and Find Last Space](#approach-2-trim-and-find-last-space)
5. [Approach 3: Reverse Scan (Optimal)](#approach-3-reverse-scan-optimal)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [58. Length of Last Word](https://leetcode.com/problems/length-of-last-word/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `String` |

### Description

> Given a string `s` consisting of words and spaces, return *the length of the **last** word in the string.*
>
> A **word** is a maximal substring consisting of non-space characters only.

### Examples

```
Example 1:
Input: s = "Hello World"
Output: 5
Explanation: The last word is "World" with a length of 5.

Example 2:
Input: s = "   fly me   to   the moon  "
Output: 4
Explanation: The last word is "moon" with a length of 4.

Example 3:
Input: s = "luffy is still joyboy"
Output: 6
Explanation: The last word is "joyboy" with a length of 6.
```

### Constraints

- `1 <= s.length <= 10^4`
- `s` consists of only English letters and spaces `' '`
- There will be at least one word in `s`

---

## Problem Breakdown

### 1. What is being asked?

Find the length of the rightmost contiguous block of non-space characters in `s`. The string can have leading, trailing, and internal spaces; we ignore the trailing spaces and count the last word that remains.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `str` | A string of letters and spaces, with at least one word |

Important observations about the input:
- Trailing spaces are common -- the last *word* is not the last *character*
- Multiple spaces between words are possible
- At least one word is guaranteed

### 3. What is the output?

A single integer: the length of the last word.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^4` | Any algorithm works; the optimal one is O(length of last word) |
| Only letters and spaces | No need to worry about punctuation, digits, or unicode |
| At least one word | We never return 0 |

### 5. Step-by-step example analysis

#### Example 2: `s = "   fly me   to   the moon  "`

```text
Reverse-scan from the right end:
  i = len(s) - 1, while s[i] == ' ': i--
  After skipping trailing spaces, i points at 'n' (last char of "moon").

Then count non-spaces from i backward:
  'n' (count=1), 'o' (2), 'o' (3), 'm' (4), then s[i-1] is ' ' → stop.

Length = 4.
```

### 6. Key Observations

1. **Trailing spaces matter** -- They have to be skipped first; otherwise we count zero or undercount.
2. **We only need the suffix** -- No need to read or split the entire string. Scan from the end.
3. **`split` works** but does extra allocation. It runs in O(n).
4. **No special characters** -- Treat `' '` as the only delimiter.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Two-pointer / Reverse Scan | Walk from the end with a state machine | Approach 3 |
| `split` library call | High-level, idiomatic | Approach 1 |
| `rstrip` + last-space search | Mid-level abstraction | Approach 2 |

**Chosen pattern:** `Reverse Scan`
**Reason:** O(length of last word) time, O(1) memory, no allocations.

---

## Approach 1: Split and Pick Last

### Thought process

> Use the language's split function to break the string by whitespace, filter out empty strings, and return the length of the last token.

### Algorithm

1. Split `s` on whitespace.
2. Take the last non-empty element.
3. Return its length.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Walk the whole string |
| **Space** | O(n) | Token list allocation |

### Implementation

#### Python

```python
class Solution:
    def lengthOfLastWordSplit(self, s: str) -> int:
        return len(s.split()[-1])
```

#### Java

```java
class Solution {
    public int lengthOfLastWordSplit(String s) {
        String[] parts = s.trim().split("\\s+");
        return parts[parts.length - 1].length();
    }
}
```

#### Go

```go
import "strings"

func lengthOfLastWordSplit(s string) int {
    parts := strings.Fields(s)
    return len(parts[len(parts)-1])
}
```

---

## Approach 2: Trim and Find Last Space

### Idea

> Strip trailing whitespace, then locate the last space inside the trimmed string. The last word lies after that space (or is the entire trimmed string if there is no space).

### Algorithm

1. `t = s.rstrip()`.
2. Find the index of the last space in `t`.
3. Return `len(t) - lastSpaceIndex - 1` (or `len(t)` if no space exists).

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Trim + scan |
| **Space** | O(n) | Trimmed copy in some languages |

### Implementation

#### Python

```python
class Solution:
    def lengthOfLastWordTrim(self, s: str) -> int:
        t = s.rstrip()
        return len(t) - t.rfind(' ') - 1
```

#### Java

```java
class Solution {
    public int lengthOfLastWordTrim(String s) {
        String t = s.replaceAll("\\s+$", "");
        return t.length() - t.lastIndexOf(' ') - 1;
    }
}
```

#### Go

```go
import "strings"

func lengthOfLastWordTrim(s string) int {
    t := strings.TrimRight(s, " ")
    last := strings.LastIndex(t, " ")
    return len(t) - last - 1
}
```

---

## Approach 3: Reverse Scan (Optimal)

### Idea

> Walk from the right end. Skip trailing spaces, then count non-space characters until we hit a space or run out of characters.

### Algorithm (step-by-step)

1. Set `i = len(s) - 1`.
2. While `i >= 0` and `s[i] == ' '`: `i--`.
3. Set `count = 0`.
4. While `i >= 0` and `s[i] != ' '`: `count++; i--`.
5. Return `count`.

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(length of last word + trailing spaces) | We stop at the first space before the last word |
| **Space** | O(1) | Two integers |

### Implementation

#### Go

```go
func lengthOfLastWord(s string) int {
    i := len(s) - 1
    for i >= 0 && s[i] == ' ' {
        i--
    }
    count := 0
    for i >= 0 && s[i] != ' ' {
        count++
        i--
    }
    return count
}
```

#### Java

```java
class Solution {
    public int lengthOfLastWord(String s) {
        int i = s.length() - 1;
        while (i >= 0 && s.charAt(i) == ' ') i--;
        int count = 0;
        while (i >= 0 && s.charAt(i) != ' ') {
            count++;
            i--;
        }
        return count;
    }
}
```

#### Python

```python
class Solution:
    def lengthOfLastWord(self, s: str) -> int:
        i = len(s) - 1
        while i >= 0 and s[i] == ' ':
            i -= 1
        count = 0
        while i >= 0 and s[i] != ' ':
            count += 1
            i -= 1
        return count
```

### Dry Run

```text
s = "   fly me   to   the moon  "
                                  ^ i = 27 (space) — skip
                                 ^ i = 26 (space) — skip
                                ^ i = 25 ('n') — count=1
                               ^ i = 24 ('o') — count=2
                              ^ i = 23 ('o') — count=3
                             ^ i = 22 ('m') — count=4
                            ^ i = 21 (' ') — stop
Return 4.
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Split + Last | O(n) | O(n) | One-liner | Allocates the full token list |
| 2 | Trim + Last Space | O(n) | O(n) | Concise | Allocates trimmed copy |
| 3 | Reverse Scan | O(length of last word) | O(1) | Optimal, in-place | Slightly more code |

### Which solution to choose?

- **In an interview:** Approach 3 -- shows you understand strings without library help
- **In production:** Approach 1 if readability matters most; Approach 3 if performance matters
- **On Leetcode:** All three pass instantly given the small constraint

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Single word, no spaces | `"hello"` | 5 | Whole string is the last word |
| 2 | Single character | `"a"` | 1 | Trivial |
| 3 | Trailing spaces | `"hi   "` | 2 | Skip trailing |
| 4 | Leading spaces | `"   hi"` | 2 | Whole word is last |
| 5 | Multiple internal spaces | `"a    b"` | 1 | Last word is `b` |
| 6 | All-space prefix and trail | `"   abc   "` | 3 | Skip both |
| 7 | Long suffix | `"a aaaaaa"` | 6 | Long last word |
| 8 | Two words touching boundary | `"day day"` | 3 | Identical words |

---

## Common Mistakes

### Mistake 1: Counting from the end without skipping spaces

```python
# WRONG — returns 0 if there are trailing spaces
i = len(s) - 1
count = 0
while i >= 0 and s[i] != ' ':
    count += 1
    i -= 1
return count

# CORRECT — skip trailing spaces first
i = len(s) - 1
while i >= 0 and s[i] == ' ': i -= 1
count = 0
while i >= 0 and s[i] != ' ':
    count += 1; i -= 1
return count
```

**Reason:** Trailing spaces would terminate the count loop before we reach the actual last word.

### Mistake 2: Using `split(' ')` instead of `split()`

```python
# WRONG — split(' ') keeps empty tokens for runs of spaces
"a    b".split(' ')   # → ['a', '', '', '', 'b']
last = "a    b".split(' ')[-1]   # → 'b' (lucky here)
last = "a    ".split(' ')[-1]    # → '' (wrong)

# CORRECT — split() collapses any whitespace run
"a    b".split()      # → ['a', 'b']
```

**Reason:** With argument `' '`, Python returns empty strings between consecutive spaces. Calling `.split()` with no argument collapses runs.

### Mistake 3: Off-by-one with `rfind`

```python
# WRONG — without rstrip, the last "space" is the trailing one
s = "abc   "
s.rfind(' ')   # → 5
len(s) - 5 - 1 # → 0  (wrong)

# CORRECT — strip trailing spaces first
t = s.rstrip()
len(t) - t.rfind(' ') - 1   # → 3
```

**Reason:** Without trimming, `rfind(' ')` returns the position of a trailing space, not the boundary before the last word.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [557. Reverse Words in a String III](https://leetcode.com/problems/reverse-words-in-a-string-iii/) | :green_circle: Easy | Word boundary scanning |
| 2 | [151. Reverse Words in a String](https://leetcode.com/problems/reverse-words-in-a-string/) | :yellow_circle: Medium | Trim + split + reverse |
| 3 | [186. Reverse Words in a String II](https://leetcode.com/problems/reverse-words-in-a-string-ii/) | :yellow_circle: Medium | In-place word manipulation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Character-by-character pointer walking from the right
> - Phase 1 highlighting (skipping trailing spaces)
> - Phase 2 highlighting (counting last word)
> - Side-by-side compare with the `split` approach
