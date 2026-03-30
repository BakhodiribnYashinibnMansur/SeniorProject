# 0014. Longest Common Prefix

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Vertical Scanning](#approach-1-vertical-scanning)
4. [Approach 2: Horizontal Scanning](#approach-2-horizontal-scanning)
5. [Approach 3: Sorting](#approach-3-sorting)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [14. Longest Common Prefix](https://leetcode.com/problems/longest-common-prefix/) |
| **Difficulty** | 🟢 Easy |
| **Tags** | `String`, `Trie` |

### Description

> Write a function to find the longest common prefix string amongst an array of strings.
>
> If there is no common prefix, return an empty string `""`.

### Examples

```
Example 1:
Input: strs = ["flower","flow","flight"]
Output: "fl"

Example 2:
Input: strs = ["dog","racecar","car"]
Output: ""
Explanation: There is no common prefix among the input strings.
```

### Constraints

- `1 <= strs.length <= 200`
- `0 <= strs[i].length <= 200`
- `strs[i]` consists of only lowercase English letters

---

## Problem Breakdown

### 1. What is being asked?

Find the longest string that is a prefix of **every** string in the given array. If no common prefix exists (even the first characters differ), return an empty string.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `strs` | `string[]` | An array of strings |

Important observations about the input:
- The array has at least 1 string
- Individual strings can be empty (length 0)
- All characters are lowercase English letters
- Up to 200 strings, each up to 200 characters long

### 3. What is the output?

- **A string** representing the longest common prefix
- Can be empty `""` if no common prefix exists
- Can be the entire first string if all strings start with it

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `strs.length <= 200` | Small array — no need for advanced algorithms |
| `strs[i].length <= 200` | Short strings — brute force is efficient enough |
| Lowercase English only | No special character handling needed |
| `strs.length >= 1` | Array is never empty |

### 5. Step-by-step example analysis

#### Example 1: `strs = ["flower", "flow", "flight"]`

```text
Column 0: f, f, f → all match ✅
Column 1: l, l, l → all match ✅
Column 2: o, o, i → mismatch ❌ (flower='o', flight='i')

Stop at column 2 → prefix = "fl"
Result: "fl"
```

#### Example 2: `strs = ["dog", "racecar", "car"]`

```text
Column 0: d, r, c → mismatch ❌ (all different)

Stop at column 0 → prefix = ""
Result: ""
```

### 6. Key Observations

1. **The prefix can be at most as long as the shortest string** — no string can have a prefix longer than itself.
2. **Early termination** — As soon as a mismatch is found at any position, we can stop.
3. **Any string can serve as a starting reference** — typically the first string is used.
4. **Sorting trick** — After sorting, only the first and last strings need to be compared (they are the most different).

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Vertical Scanning | Compare column by column across all strings | Check position 0 of all strings, then position 1, etc. |
| Horizontal Scanning | Reduce prefix pairwise | Start with "flower", reduce with "flow" → "flow", reduce with "flight" → "fl" |
| Sorting + Compare | After sorting, first and last are most different | Sort → compare only first and last string |

**Chosen pattern:** `Vertical Scanning`
**Reason:** Most intuitive — check each character position across all strings and stop at the first mismatch.

---

## Approach 1: Vertical Scanning

### Thought process

> Compare characters **column by column** across all strings. For each position `i`, check if all strings have the same character at index `i`. Stop as soon as a mismatch is found or any string runs out of characters.
>
> This is the most intuitive approach — think of aligning all strings vertically and scanning down each column.

### Algorithm (step-by-step)

1. Handle edge case: if `strs` is empty, return `""`
2. Use the first string `strs[0]` as the reference
3. For each index `i` from `0` to `len(strs[0]) - 1`:
   - Get the character `ch = strs[0][i]`
   - For each other string `strs[j]` (j from 1 to n-1):
     - If `i >= len(strs[j])` or `strs[j][i] != ch` → return `strs[0][:i]`
4. Return `strs[0]` (the entire first string is the prefix)

### Pseudocode

```text
function longestCommonPrefix(strs):
    if strs is empty: return ""
    for i = 0 to len(strs[0]) - 1:
        ch = strs[0][i]
        for j = 1 to len(strs) - 1:
            if i >= len(strs[j]) or strs[j][i] != ch:
                return strs[0][0..i]
    return strs[0]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(S) | S = sum of all characters. In the worst case, all strings are identical and we scan every character. |
| **Space** | O(1) | Only a few index variables. The returned string is a substring of strs[0]. |

### Implementation

#### Go

```go
func longestCommonPrefix(strs []string) string {
    if len(strs) == 0 {
        return ""
    }

    for i := 0; i < len(strs[0]); i++ {
        ch := strs[0][i]
        for j := 1; j < len(strs); j++ {
            if i >= len(strs[j]) || strs[j][i] != ch {
                return strs[0][:i]
            }
        }
    }

    return strs[0]
}
```

#### Java

```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        for (int i = 0; i < strs[0].length(); i++) {
            char ch = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                if (i >= strs[j].length() || strs[j].charAt(i) != ch) {
                    return strs[0].substring(0, i);
                }
            }
        }

        return strs[0];
    }
}
```

#### Python

```python
class Solution:
    def longestCommonPrefix(self, strs: list[str]) -> str:
        if not strs:
            return ""

        for i in range(len(strs[0])):
            ch = strs[0][i]
            for j in range(1, len(strs)):
                if i >= len(strs[j]) or strs[j][i] != ch:
                    return strs[0][:i]

        return strs[0]
```

### Dry Run

```text
Input: strs = ["flower", "flow", "flight"]

Reference string: "flower"

i=0: ch='f'
  j=1: strs[1][0]='f' → match ✅
  j=2: strs[2][0]='f' → match ✅

i=1: ch='l'
  j=1: strs[1][1]='l' → match ✅
  j=2: strs[2][1]='l' → match ✅

i=2: ch='o'
  j=1: strs[1][2]='o' → match ✅
  j=2: strs[2][2]='i' → mismatch ❌ → return "flower"[0:2] = "fl"

Result: "fl" ✅
```

---

## Approach 2: Horizontal Scanning

### Thought process

> Start with the first string as the initial prefix. Then compare it with each subsequent string and **shrink** the prefix until it matches the beginning of that string. After processing all strings, the remaining prefix is the answer.
>
> Think of it as: "What prefix do strings 1 and 2 share? Now what prefix does THAT share with string 3?" and so on.

### Algorithm (step-by-step)

1. Handle edge case: if `strs` is empty, return `""`
2. Set `prefix = strs[0]`
3. For each string `strs[i]` (i from 1 to n-1):
   - While `strs[i]` does not start with `prefix`:
     - Remove the last character from `prefix`
     - If `prefix` is empty → return `""`
4. Return `prefix`

### Pseudocode

```text
function longestCommonPrefix(strs):
    if strs is empty: return ""
    prefix = strs[0]
    for i = 1 to len(strs) - 1:
        while strs[i] does not start with prefix:
            prefix = prefix[0..len(prefix)-1]
            if prefix is empty: return ""
    return prefix
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(S) | S = sum of all characters. In the worst case, all comparisons scan every character. |
| **Space** | O(1) | Only stores the prefix string which shrinks from strs[0]. |

### Implementation

#### Go

```go
func longestCommonPrefix(strs []string) string {
    if len(strs) == 0 {
        return ""
    }

    prefix := strs[0]

    for i := 1; i < len(strs); i++ {
        for !strings.HasPrefix(strs[i], prefix) {
            prefix = prefix[:len(prefix)-1]
            if len(prefix) == 0 {
                return ""
            }
        }
    }

    return prefix
}
```

#### Java

```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        String prefix = strs[0];

        for (int i = 1; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) return "";
            }
        }

        return prefix;
    }
}
```

#### Python

```python
class Solution:
    def longestCommonPrefix(self, strs: list[str]) -> str:
        if not strs:
            return ""

        prefix = strs[0]

        for i in range(1, len(strs)):
            while not strs[i].startswith(prefix):
                prefix = prefix[:-1]
                if not prefix:
                    return ""

        return prefix
```

### Dry Run

```text
Input: strs = ["flower", "flow", "flight"]

prefix = "flower"

i=1: strs[1] = "flow"
  "flow".startsWith("flower")? No → prefix = "flowe"
  "flow".startsWith("flowe")?  No → prefix = "flow"
  "flow".startsWith("flow")?   Yes ✅

i=2: strs[2] = "flight"
  "flight".startsWith("flow")?  No → prefix = "flo"
  "flight".startsWith("flo")?   No → prefix = "fl"
  "flight".startsWith("fl")?    Yes ✅

Result: "fl" ✅
```

---

## Approach 3: Sorting

### Thought process

> Sort the array of strings lexicographically. After sorting, the strings that share the **least** in common will be at the opposite ends of the array. Therefore, the longest common prefix of the **entire array** is simply the longest common prefix of the **first** and **last** strings after sorting.
>
> This works because if the first and last (most different) strings share a prefix, all strings in between must also share that prefix.

### Algorithm (step-by-step)

1. Handle edge case: if `strs` is empty, return `""`
2. Sort `strs` lexicographically
3. Compare the first string `strs[0]` and the last string `strs[n-1]` character by character
4. Return the matching prefix

### Pseudocode

```text
function longestCommonPrefix(strs):
    if strs is empty: return ""
    sort(strs)
    first = strs[0]
    last = strs[len(strs) - 1]
    i = 0
    while i < len(first) and i < len(last) and first[i] == last[i]:
        i++
    return first[0..i]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(S log n) | Sorting n strings takes O(S log n) where S is the sum of all characters. Comparison after sorting is O(m) where m is the shortest string. |
| **Space** | O(1) | Sorting is in-place (ignoring language-specific sort overhead). |

### Implementation

#### Go

```go
func longestCommonPrefix(strs []string) string {
    if len(strs) == 0 {
        return ""
    }

    sort.Strings(strs)

    first := strs[0]
    last := strs[len(strs)-1]
    i := 0

    for i < len(first) && i < len(last) && first[i] == last[i] {
        i++
    }

    return first[:i]
}
```

#### Java

```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        Arrays.sort(strs);

        String first = strs[0];
        String last = strs[strs.length - 1];
        int i = 0;

        while (i < first.length() && i < last.length() && first.charAt(i) == last.charAt(i)) {
            i++;
        }

        return first.substring(0, i);
    }
}
```

#### Python

```python
class Solution:
    def longestCommonPrefix(self, strs: list[str]) -> str:
        if not strs:
            return ""

        strs.sort()

        first = strs[0]
        last = strs[-1]
        i = 0

        while i < len(first) and i < len(last) and first[i] == last[i]:
            i += 1

        return first[:i]
```

### Dry Run

```text
Input: strs = ["flower", "flow", "flight"]

After sorting: ["flight", "flow", "flower"]

first = "flight"
last  = "flower"

i=0: first[0]='f' == last[0]='f' → match ✅ → i=1
i=1: first[1]='l' == last[1]='l' → match ✅ → i=2
i=2: first[2]='i' != last[2]='o' → mismatch ❌ → stop

Result: "flight"[0:2] = "fl" ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Vertical Scanning | O(S) | O(1) | Optimal — stops at first mismatch, no extra work | Slightly more complex nested loop |
| 2 | Horizontal Scanning | O(S) | O(1) | Simple logic, easy to implement | May do redundant comparisons when prefix shrinks |
| 3 | Sorting | O(S log n) | O(1) | Elegant — only compare two strings | Sorting overhead makes it slower overall |

### Which solution to choose?

- **In an interview:** Approach 1 (Vertical Scanning) — optimal, intuitive, easy to explain
- **In production:** Approach 1 or 2 — both are O(S) and handle all edge cases
- **On Leetcode:** Any — all three are accepted
- **For learning:** All three — each demonstrates a different problem-solving strategy

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single string | `["alone"]` | `"alone"` | Only one string — it is its own prefix |
| 2 | All identical | `["abc", "abc", "abc"]` | `"abc"` | Entire string is the common prefix |
| 3 | Empty string in array | `["abc", "", "abc"]` | `""` | Empty string has no characters to share |
| 4 | No common prefix | `["dog", "racecar", "car"]` | `""` | First characters all differ |
| 5 | Single character strings | `["a", "a", "a"]` | `"a"` | Minimal matching strings |
| 6 | First char mismatch | `["abc", "xyz"]` | `""` | Mismatch at the very first position |
| 7 | Two strings partial | `["interview", "internet"]` | `"inter"` | Common prefix is a proper prefix of both |
| 8 | One char prefix | `["ab", "ac", "ad"]` | `"a"` | Only the first character matches |
| 9 | Very different lengths | `["a", "abcdefgh"]` | `"a"` | Short string limits the prefix |

---

## Common Mistakes

### Mistake 1: Not handling empty strings in the array

```python
# WRONG — assumes all strings are non-empty
def longestCommonPrefix(self, strs):
    for i in range(len(strs[0])):
        for j in range(1, len(strs)):
            if strs[j][i] != strs[0][i]:  # IndexError if strs[j] is empty!
                return strs[0][:i]

# CORRECT — check bounds first
if i >= len(strs[j]) or strs[j][i] != ch:
    return strs[0][:i]
```

**Reason:** A string in the array can have length 0, causing an index out of bounds.

### Mistake 2: Using the shortest string length without checking all strings

```python
# WRONG — only compares first two strings
def longestCommonPrefix(self, strs):
    prefix = ""
    for i in range(min(len(strs[0]), len(strs[1]))):
        if strs[0][i] == strs[1][i]:
            prefix += strs[0][i]
    return prefix
# Ignores strs[2], strs[3], etc.

# CORRECT — compare across ALL strings
for i in range(len(strs[0])):
    ch = strs[0][i]
    for j in range(1, len(strs)):  # Check every string
        if i >= len(strs[j]) or strs[j][i] != ch:
            return strs[0][:i]
```

**Reason:** The common prefix must be shared by ALL strings, not just two.

### Mistake 3: Building prefix by concatenation instead of slicing

```python
# INEFFICIENT — string concatenation is O(n) per operation in many languages
prefix = ""
for i in range(len(strs[0])):
    if all(i < len(s) and s[i] == strs[0][i] for s in strs):
        prefix += strs[0][i]  # O(n) copy each time!

# BETTER — use slicing at the end
for i in range(len(strs[0])):
    ch = strs[0][i]
    for j in range(1, len(strs)):
        if i >= len(strs[j]) or strs[j][i] != ch:
            return strs[0][:i]  # Single slice at the end
return strs[0]
```

**Reason:** Repeated string concatenation creates a new string each time, leading to O(n^2) behavior.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [28. Find the Index of the First Occurrence in a String](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/) | 🟢 Easy | String matching / prefix comparison |
| 2 | [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) | 🟡 Medium | Trie data structure for prefix operations |
| 3 | [211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | 🟡 Medium | Trie-based prefix matching |
| 4 | [720. Longest Word in Dictionary](https://leetcode.com/problems/longest-word-in-dictionary/) | 🟡 Medium | Building words from prefixes |
| 5 | [1143. Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | 🟡 Medium | Related concept — subsequence instead of prefix |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Vertical Scanning** tab — compares characters column by column across all strings
> - **Horizontal Scanning** tab — reduces prefix pairwise
> - **Sorting** tab — sorts strings and compares first with last
> - Enter any comma-separated strings to see the algorithm step by step
> - Matching characters are highlighted in green, mismatches in red
> - Running prefix updates in real time
