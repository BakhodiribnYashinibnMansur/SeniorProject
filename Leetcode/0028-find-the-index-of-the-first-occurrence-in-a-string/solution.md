# 0028. Find the Index of the First Occurrence in a String

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force (Sliding Window)](#approach-1-brute-force-sliding-window)
4. [Approach 2: KMP Algorithm](#approach-2-kmp-algorithm)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [28. Find the Index of the First Occurrence in a String](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Two Pointers`, `String`, `String Matching` |

### Description

> Given two strings `needle` and `haystack`, return the index of the first occurrence of `needle` in `haystack`, or `-1` if `needle` is not part of `haystack`.

### Examples

```
Example 1:
Input: haystack = "sadbutsad", needle = "sad"
Output: 0
Explanation: "sad" occurs at index 0 and 6. The first occurrence is at index 0.

Example 2:
Input: haystack = "leetcode", needle = "leeto"
Output: -1
Explanation: "leeto" did not occur in "leetcode", so we return -1.
```

### Constraints

- `1 <= haystack.length, needle.length <= 10^4`
- `haystack` and `needle` consist of only lowercase English characters

---

## Problem Breakdown

### 1. What is being asked?

Find the **first position** in `haystack` where `needle` appears as a contiguous substring. If `needle` never appears, return `-1`.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `haystack` | `string` | The string to search in |
| `needle` | `string` | The string to search for |

Important observations about the input:
- Both strings have at least 1 character
- All characters are lowercase English letters
- `needle` can be longer than `haystack`

### 3. What is the output?

- **An integer** representing the starting index of the first occurrence of `needle` in `haystack`
- Returns `-1` if `needle` is not found

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `haystack.length <= 10^4` | Moderate size — O(n*m) brute force is acceptable |
| `needle.length <= 10^4` | Needle can be as long as haystack |
| Lowercase English only | No special character handling needed |
| Both non-empty | No need to handle empty strings |

### 5. Step-by-step example analysis

#### Example 1: `haystack = "sadbutsad", needle = "sad"`

```text
Position 0: "sad" vs "sad" → match ✅
Return 0
```

#### Example 2: `haystack = "leetcode", needle = "leeto"`

```text
Position 0: "leetc" vs "leeto" → mismatch at index 4 ❌
Position 1: "eetco" vs "leeto" → mismatch at index 0 ❌
Position 2: "etcod" vs "leeto" → mismatch at index 0 ❌
Position 3: "tcode" vs "leeto" → mismatch at index 0 ❌
No more valid positions → return -1
```

### 6. Key Observations

1. **Sliding window size** — We only need to check positions `0` to `len(haystack) - len(needle)` because beyond that, there aren't enough characters left for a full match.
2. **Early termination** — As soon as we find a mismatch within a window, we can skip to the next position.
3. **Pattern repetition** — KMP exploits repeated prefixes in the needle to avoid re-scanning characters in the haystack.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Brute Force (Sliding Window) | Try every starting position, compare character by character | Check position 0, 1, 2, ... until match or exhaustion |
| KMP Algorithm | Preprocess needle to build failure function, skip redundant comparisons | Use prefix table to jump forward on mismatch |

**Chosen pattern:** `Brute Force (Sliding Window)` for simplicity, `KMP` for optimal performance.
**Reason:** Brute Force is intuitive and sufficient for the constraints. KMP avoids backtracking and is optimal for large inputs.

---

## Approach 1: Brute Force (Sliding Window)

### Thought process

> Slide a window of size `len(needle)` across `haystack`. At each position, compare the window with `needle` character by character. If all characters match, return the starting index. If no position yields a match, return `-1`.
>
> This is the most straightforward approach — try every possible starting position.

### Algorithm (step-by-step)

1. Let `n = len(haystack)`, `m = len(needle)`
2. If `m > n`, return `-1` (needle is longer than haystack)
3. For each starting position `i` from `0` to `n - m`:
   - Compare `haystack[i..i+m-1]` with `needle[0..m-1]`
   - If all characters match, return `i`
4. Return `-1` (no match found)

### Pseudocode

```text
function strStr(haystack, needle):
    n = len(haystack)
    m = len(needle)
    if m > n: return -1
    for i = 0 to n - m:
        match = true
        for j = 0 to m - 1:
            if haystack[i + j] != needle[j]:
                match = false
                break
        if match: return i
    return -1
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * m) | For each of the n-m+1 positions, we compare up to m characters. Worst case: haystack = "aaa...a", needle = "aaa...ab". |
| **Space** | O(1) | Only a few index variables. |

### Implementation

#### Go

```go
func strStr(haystack string, needle string) int {
    n, m := len(haystack), len(needle)

    for i := 0; i <= n-m; i++ {
        match := true
        for j := 0; j < m; j++ {
            if haystack[i+j] != needle[j] {
                match = false
                break
            }
        }
        if match {
            return i
        }
    }

    return -1
}
```

#### Java

```java
class Solution {
    public int strStr(String haystack, String needle) {
        int n = haystack.length(), m = needle.length();

        for (int i = 0; i <= n - m; i++) {
            boolean match = true;
            for (int j = 0; j < m; j++) {
                if (haystack.charAt(i + j) != needle.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }

        return -1;
    }
}
```

#### Python

```python
class Solution:
    def strStr(self, haystack: str, needle: str) -> int:
        n, m = len(haystack), len(needle)

        for i in range(n - m + 1):
            if haystack[i:i + m] == needle:
                return i

        return -1
```

### Dry Run

```text
Input: haystack = "sadbutsad", needle = "sad"

n = 9, m = 3
Valid positions: 0 to 6

i=0: haystack[0:3] = "sad" vs "sad"
  j=0: 's' == 's' ✅
  j=1: 'a' == 'a' ✅
  j=2: 'd' == 'd' ✅
  → match! return 0

Result: 0 ✅
```

```text
Input: haystack = "leetcode", needle = "leeto"

n = 8, m = 5
Valid positions: 0 to 3

i=0: haystack[0:5] = "leetc" vs "leeto"
  j=0: 'l' == 'l' ✅
  j=1: 'e' == 'e' ✅
  j=2: 'e' == 'e' ✅
  j=3: 't' == 't' ✅
  j=4: 'c' != 'o' ❌ → no match

i=1: haystack[1:6] = "eetco" vs "leeto"
  j=0: 'e' != 'l' ❌ → no match

i=2: haystack[2:7] = "etcod" vs "leeto"
  j=0: 'e' != 'l' ❌ → no match

i=3: haystack[3:8] = "tcode" vs "leeto"
  j=0: 't' != 'l' ❌ → no match

No match found → return -1

Result: -1 ✅
```

---

## Approach 2: KMP Algorithm

### Thought process

> The Knuth-Morris-Pratt (KMP) algorithm avoids redundant comparisons by preprocessing the `needle` to build a **failure function** (also called the **prefix table** or **LPS array** — Longest Proper Prefix which is also a Suffix).
>
> When a mismatch occurs at position `j` in `needle`, instead of restarting from position `0`, we use the failure function to jump to the longest prefix of `needle[0..j-1]` that is also a suffix. This way, we never backtrack in `haystack`.
>
> Think of it as: "I already matched some characters. Which part of the needle can I reuse without re-scanning the haystack?"

### Algorithm (step-by-step)

**Step 1: Build the LPS (Longest Proper Prefix Suffix) array**

1. Initialize `lps` array of size `m` with all zeros
2. Set `length = 0` (length of the previous longest prefix suffix)
3. Set `i = 1`
4. While `i < m`:
   - If `needle[i] == needle[length]`: set `lps[i] = length + 1`, increment both `i` and `length`
   - Else if `length > 0`: set `length = lps[length - 1]` (fall back)
   - Else: `lps[i] = 0`, increment `i`

**Step 2: Search using the LPS array**

1. Set `i = 0` (index in haystack), `j = 0` (index in needle)
2. While `i < n`:
   - If `haystack[i] == needle[j]`: increment both `i` and `j`
   - If `j == m`: return `i - j` (match found)
   - Else if `i < n` and `haystack[i] != needle[j]`:
     - If `j > 0`: set `j = lps[j - 1]`
     - Else: increment `i`
3. Return `-1` (no match)

### Pseudocode

```text
function buildLPS(needle):
    m = len(needle)
    lps = array of size m, all zeros
    length = 0
    i = 1
    while i < m:
        if needle[i] == needle[length]:
            length++
            lps[i] = length
            i++
        else if length > 0:
            length = lps[length - 1]
        else:
            lps[i] = 0
            i++
    return lps

function strStr(haystack, needle):
    n = len(haystack)
    m = len(needle)
    if m > n: return -1
    lps = buildLPS(needle)
    i = 0, j = 0
    while i < n:
        if haystack[i] == needle[j]:
            i++
            j++
        if j == m:
            return i - j
        else if i < n and haystack[i] != needle[j]:
            if j > 0:
                j = lps[j - 1]
            else:
                i++
    return -1
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n + m) | Building LPS takes O(m). Searching takes O(n). Each character in haystack is visited at most twice (once by `i`, once indirectly via `j` adjustments). |
| **Space** | O(m) | The LPS array stores m values. |

### Implementation

#### Go

```go
func strStr(haystack string, needle string) int {
    n, m := len(haystack), len(needle)
    if m > n {
        return -1
    }

    // Build LPS array
    lps := make([]int, m)
    length := 0
    i := 1
    for i < m {
        if needle[i] == needle[length] {
            length++
            lps[i] = length
            i++
        } else if length > 0 {
            length = lps[length-1]
        } else {
            lps[i] = 0
            i++
        }
    }

    // Search
    i = 0
    j := 0
    for i < n {
        if haystack[i] == needle[j] {
            i++
            j++
        }
        if j == m {
            return i - j
        } else if i < n && haystack[i] != needle[j] {
            if j > 0 {
                j = lps[j-1]
            } else {
                i++
            }
        }
    }

    return -1
}
```

#### Java

```java
class Solution {
    public int strStr(String haystack, String needle) {
        int n = haystack.length(), m = needle.length();
        if (m > n) return -1;

        // Build LPS array
        int[] lps = new int[m];
        int length = 0;
        int i = 1;
        while (i < m) {
            if (needle.charAt(i) == needle.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        // Search
        i = 0;
        int j = 0;
        while (i < n) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;
            }
            if (j == m) {
                return i - j;
            } else if (i < n && haystack.charAt(i) != needle.charAt(j)) {
                if (j > 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return -1;
    }
}
```

#### Python

```python
class Solution:
    def strStr(self, haystack: str, needle: str) -> int:
        n, m = len(haystack), len(needle)
        if m > n:
            return -1

        # Build LPS array
        lps = [0] * m
        length = 0
        i = 1
        while i < m:
            if needle[i] == needle[length]:
                length += 1
                lps[i] = length
                i += 1
            elif length > 0:
                length = lps[length - 1]
            else:
                lps[i] = 0
                i += 1

        # Search
        i = j = 0
        while i < n:
            if haystack[i] == needle[j]:
                i += 1
                j += 1
            if j == m:
                return i - j
            elif i < n and haystack[i] != needle[j]:
                if j > 0:
                    j = lps[j - 1]
                else:
                    i += 1

        return -1
```

### Dry Run

```text
Input: haystack = "sadbutsad", needle = "sad"

Step 1: Build LPS for "sad"
  needle = "sad"
  lps = [0, 0, 0]

  i=1: needle[1]='a' != needle[0]='s', length=0 → lps[1]=0, i=2
  i=2: needle[2]='d' != needle[0]='s', length=0 → lps[2]=0, i=3

  LPS = [0, 0, 0]

Step 2: Search
  i=0, j=0: haystack[0]='s' == needle[0]='s' → i=1, j=1
  i=1, j=1: haystack[1]='a' == needle[1]='a' → i=2, j=2
  i=2, j=2: haystack[2]='d' == needle[2]='d' → i=3, j=3
  j == m (3 == 3) → return i - j = 3 - 3 = 0

Result: 0 ✅
```

```text
Input: haystack = "aaabaaab", needle = "aaab"

Step 1: Build LPS for "aaab"
  needle = "aaab"

  i=1: needle[1]='a' == needle[0]='a' → length=1, lps[1]=1, i=2
  i=2: needle[2]='a' == needle[1]='a' → length=2, lps[2]=2, i=3
  i=3: needle[3]='b' != needle[2]='a' → length=lps[1]=1
        needle[3]='b' != needle[1]='a' → length=lps[0]=0
        needle[3]='b' != needle[0]='a' → lps[3]=0, i=4

  LPS = [0, 1, 2, 0]

Step 2: Search
  i=0, j=0: 'a' == 'a' → i=1, j=1
  i=1, j=1: 'a' == 'a' → i=2, j=2
  i=2, j=2: 'a' == 'a' → i=3, j=3
  i=3, j=3: 'b' != 'b'? No, 'b' == 'b' → i=4, j=4
  j == m (4 == 4) → return i - j = 4 - 4 = 0

Result: 0 ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force (Sliding Window) | O(n * m) | O(1) | Simple, intuitive, no preprocessing | Redundant comparisons in worst case |
| 2 | KMP Algorithm | O(n + m) | O(m) | Optimal — no backtracking in haystack | More complex to implement, extra space for LPS |

### Which solution to choose?

- **In an interview:** Approach 1 (Brute Force) — simple, correct, easy to implement quickly. Mention KMP as the optimal follow-up.
- **In production:** Language built-in (e.g., `str.find()`, `strings.Index()`, `indexOf()`) which often uses optimized algorithms internally.
- **On Leetcode:** Either — both are accepted. KMP shines with adversarial inputs.
- **For learning:** Both — Brute Force teaches the problem, KMP teaches string matching theory.

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Needle at start | `haystack="hello", needle="hel"` | `0` | Match at the very beginning |
| 2 | Needle at end | `haystack="hello", needle="llo"` | `2` | Match at the end |
| 3 | Needle equals haystack | `haystack="abc", needle="abc"` | `0` | Entire string matches |
| 4 | Needle longer than haystack | `haystack="ab", needle="abc"` | `-1` | Impossible to match |
| 5 | Single character match | `haystack="a", needle="a"` | `0` | Minimal matching case |
| 6 | Single character no match | `haystack="a", needle="b"` | `-1` | Minimal non-matching case |
| 7 | Repeated characters | `haystack="aaaa", needle="aa"` | `0` | First occurrence among overlapping matches |
| 8 | Partial match then fail | `haystack="mississippi", needle="issip"` | `4` | Tricky partial matches before success |
| 9 | No match at all | `haystack="leetcode", needle="leeto"` | `-1` | Close match but not exact |

---

## Common Mistakes

### Mistake 1: Off-by-one in the loop bound

```python
# WRONG — misses the last valid position
for i in range(len(haystack) - len(needle)):  # should be + 1
    if haystack[i:i+len(needle)] == needle:
        return i

# CORRECT — include the last valid starting position
for i in range(len(haystack) - len(needle) + 1):
    if haystack[i:i+len(needle)] == needle:
        return i
```

**Reason:** If `haystack = "abc"` and `needle = "abc"`, position `0` is valid and `n - m = 0`, so `range(0)` produces nothing.

### Mistake 2: Not handling needle longer than haystack

```python
# WRONG — index out of bounds when needle is longer
def strStr(self, haystack, needle):
    for i in range(len(haystack)):
        if haystack[i:i+len(needle)] == needle:
            return i
    return -1
# This works in Python (slicing is safe) but not in Java/Go without bounds check

# CORRECT — check lengths first
if len(needle) > len(haystack):
    return -1
```

**Reason:** In languages like Java and Go, accessing indices beyond the string length causes runtime errors.

### Mistake 3: Incorrect KMP LPS construction

```python
# WRONG — not falling back correctly
def buildLPS(needle):
    lps = [0] * len(needle)
    length = 0
    for i in range(1, len(needle)):
        if needle[i] == needle[length]:
            length += 1
            lps[i] = length
        else:
            length = 0  # Should fall back to lps[length-1], not reset to 0!
            lps[i] = 0

# CORRECT — use while loop to fall back through LPS chain
while i < m:
    if needle[i] == needle[length]:
        length += 1
        lps[i] = length
        i += 1
    elif length > 0:
        length = lps[length - 1]  # Fall back, don't reset!
    else:
        lps[i] = 0
        i += 1
```

**Reason:** When a mismatch occurs, we need to check if a shorter prefix-suffix exists by following the LPS chain, not just resetting to 0.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [14. Longest Common Prefix](https://leetcode.com/problems/longest-common-prefix/) | :green_circle: Easy | String matching / prefix comparison |
| 2 | [459. Repeated Substring Pattern](https://leetcode.com/problems/repeated-substring-pattern/) | :green_circle: Easy | KMP / string pattern detection |
| 3 | [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/) | :red_circle: Hard | KMP failure function application |
| 4 | [572. Subtree of Another Tree](https://leetcode.com/problems/subtree-of-another-tree/) | :green_circle: Easy | Pattern matching in tree structure |
| 5 | [686. Repeated String Match](https://leetcode.com/problems/repeated-string-match/) | :orange_circle: Medium | String matching with repeated concatenation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — slides a window across haystack, comparing character by character
> - **KMP Algorithm** tab — shows LPS array construction and efficient search with jump-back visualization
> - Enter any haystack and needle to see the algorithm step by step
> - Matching characters are highlighted in green, mismatches in red
> - Current comparison position and window are clearly marked
