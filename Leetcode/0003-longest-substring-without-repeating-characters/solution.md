# 0003. Longest Substring Without Repeating Characters

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Sliding Window with Set](#approach-2-sliding-window-with-set)
5. [Approach 3: Optimal (Sliding Window with Last-Seen Index Map)](#approach-3-optimal-sliding-window-with-last-seen-index-map)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `Hash Table`, `String`, `Sliding Window` |

### Description

> Given a string `s`, find the length of the **longest substring** without repeating characters.

### Examples

```
Example 1:
Input: s = "abcabcbb"
Output: 3
Explanation: The answer is "abc", with the length of 3.

Example 2:
Input: s = "bbbbb"
Output: 1
Explanation: The answer is "b", with the length of 1.

Example 3:
Input: s = "pwwkew"
Output: 3
Explanation: The answer is "wke", with the length of 3.
             Note that "pwke" is a subsequence and not a substring.
```

### Constraints

- `0 <= s.length <= 5 * 10^4`
- `s` consists of English letters, digits, symbols, and spaces

---

## Problem Breakdown

### 1. What is being asked?

Find the **length** of the longest contiguous portion (substring) of `s` that contains no character more than once.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | The input string |

Important observations about the input:
- Can be **empty** (`s = ""`) → answer is 0
- Characters include letters, digits, symbols, and spaces — not just lowercase letters
- The same character can appear multiple times across the string

### 3. What is the output?

- A single **integer**: the length of the longest substring without any repeated character
- We need only the length, not the actual substring itself

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 5*10^4` | O(n^2) might just pass (~2.5*10^9 ops, likely TLE), but O(n) is clearly preferred |
| All ASCII characters | At most 128 unique characters — the map/set is bounded in size |

### 5. Step-by-step example analysis

#### Example 1: `s = "abcabcbb"` → `3`

```text
Substrings without repeats:
  "a"     length 1
  "ab"    length 2
  "abc"   length 3  ← longest
  "abca"  ❌ 'a' repeats
  "bcab"  ❌ 'b' repeats
  "cab"   length 3
  "abc"   length 3
  ...

Answer: 3  ("abc")
```

#### Example 3: `s = "pwwkew"` → `3`

```text
  "p"    length 1
  "pw"   length 2
  "pww"  ❌ 'w' repeats
  "w"    length 1
  "wk"   length 2
  "wke"  length 3  ← longest
  "wkew" ❌ 'w' repeats
  "kew"  length 3

Answer: 3  ("wke")
```

### 6. Key Observations

1. **Substring vs subsequence** — we need a contiguous segment, not just any selection of characters.
2. **Sliding window** — we can maintain a window `[left, right]` that expands to the right; when a duplicate is detected, shrink from the left.
3. **No need to enumerate all substrings** — once we know a window is valid, we only need to extend it or shrink it from the left.
4. **Index map beats a set** — instead of slowly shrinking the left pointer one step at a time, we can jump it directly to `lastSeen[ch] + 1`.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Sliding Window | Fixed structure of "longest valid window" | Longest Substring Without Repeating Characters (this) |
| Hash Table / Map | O(1) lookup for character existence or last index | All "seen before" problems |
| Two Pointers | left and right define the window boundaries | Any window-based problem |

**Chosen pattern:** `Sliding Window + Last-Seen Index Map`
**Reason:** O(n) time with a single pass; jumping left pointer avoids redundant work.

---

## Approach 1: Brute Force

### Thought process

> Check every possible substring. For each one, verify that all characters are unique.
> Keep track of the longest valid one found.

### Algorithm (step-by-step)

1. Outer loop: start index `i = 0` to `n-1`
2. Inner loop: end index `j = i` to `n-1`
3. For substring `s[i..j]`, check if all characters are unique (e.g., using a set)
4. If unique → update `maxLen = max(maxLen, j - i + 1)`
5. If not unique → break inner loop (any extension will also repeat)

### Pseudocode

```text
function lengthOfLongestSubstring(s):
    maxLen = 0
    n = len(s)
    for i = 0 to n-1:
        seen = {}
        for j = i to n-1:
            if s[j] in seen: break
            seen.add(s[j])
            maxLen = max(maxLen, j - i + 1)
    return maxLen
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | O(n^2) pairs (i,j), each with O(1) set lookup → O(n^2) total |
| **Space** | O(min(n, a)) | Set holds at most min(window size, alphabet size) characters |

### Implementation

#### Go

```go
// lengthOfLongestSubstring — Brute Force
// Time: O(n²), Space: O(min(n, a))
func lengthOfLongestSubstringBrute(s string) int {
    maxLen := 0
    n := len(s)
    for i := 0; i < n; i++ {
        seen := make(map[byte]bool)
        for j := i; j < n; j++ {
            if seen[s[j]] {
                break // duplicate found — no point extending further
            }
            seen[s[j]] = true
            if j-i+1 > maxLen {
                maxLen = j - i + 1
            }
        }
    }
    return maxLen
}
```

#### Java

```java
// lengthOfLongestSubstring — Brute Force
// Time: O(n²), Space: O(min(n, a))
public int lengthOfLongestSubstringBrute(String s) {
    int maxLen = 0, n = s.length();
    for (int i = 0; i < n; i++) {
        java.util.HashSet<Character> seen = new java.util.HashSet<>();
        for (int j = i; j < n; j++) {
            if (seen.contains(s.charAt(j))) break; // duplicate found
            seen.add(s.charAt(j));
            maxLen = Math.max(maxLen, j - i + 1);
        }
    }
    return maxLen;
}
```

#### Python

```python
def lengthOfLongestSubstringBrute(self, s: str) -> int:
    """
    Brute Force
    Time: O(n²), Space: O(min(n, a))
    """
    max_len = 0
    n = len(s)
    for i in range(n):
        seen = set()
        for j in range(i, n):
            if s[j] in seen:
                break  # duplicate found — no point extending further
            seen.add(s[j])
            max_len = max(max_len, j - i + 1)
    return max_len
```

### Dry Run

```text
Input: s = "abcabcbb"

i=0: seen={}
  j=0: 'a' new → seen={a}, len=1
  j=1: 'b' new → seen={a,b}, len=2
  j=2: 'c' new → seen={a,b,c}, len=3  ← maxLen=3
  j=3: 'a' DUPLICATE → break

i=1: seen={}
  j=1: 'b' → seen={b}, len=1
  j=2: 'c' → seen={b,c}, len=2
  j=3: 'a' → seen={b,c,a}, len=3
  j=4: 'b' DUPLICATE → break

... (no window longer than 3 found)

Result: 3 ✅
```

---

## Approach 2: Sliding Window with Set

### The problem with Brute Force

> We're re-examining the same characters over and over.
> Optimization: maintain a moving window `[left, right]`. Expand right; when a duplicate appears, shrink from the left until the duplicate is removed.

### Optimization idea

> **Set + two pointers:** the set tracks characters currently in the window.
> - Expand: add `s[right]` to the set, advance `right`
> - Shrink: if `s[right]` already in set, remove `s[left]` and advance `left`
> - This is still O(n) but each character may be added and removed once each.

### Algorithm (step-by-step)

1. `left = 0`, `maxLen = 0`, `seen = set()`
2. For `right` from `0` to `n-1`:
   a. While `s[right]` in `seen`: remove `s[left]`, increment `left`
   b. Add `s[right]` to `seen`
   c. `maxLen = max(maxLen, right - left + 1)`
3. Return `maxLen`

### Pseudocode

```text
function lengthOfLongestSubstring(s):
    seen = {}
    left = 0, maxLen = 0
    for right = 0 to n-1:
        while s[right] in seen:
            seen.remove(s[left])
            left++
        seen.add(s[right])
        maxLen = max(maxLen, right - left + 1)
    return maxLen
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each character is added to and removed from the set at most once |
| **Space** | O(min(n, a)) | Set holds at most the alphabet size of unique characters |

### Implementation

#### Go

```go
// Time: O(n), Space: O(min(n, a))
func lengthOfLongestSubstringSet(s string) int {
    seen := make(map[byte]bool)
    left, maxLen := 0, 0
    for right := 0; right < len(s); right++ {
        for seen[s[right]] {
            delete(seen, s[left])
            left++
        }
        seen[s[right]] = true
        if right-left+1 > maxLen {
            maxLen = right - left + 1
        }
    }
    return maxLen
}
```

#### Java

```java
// Time: O(n), Space: O(min(n, a))
public int lengthOfLongestSubstringSet(String s) {
    java.util.HashSet<Character> seen = new java.util.HashSet<>();
    int left = 0, maxLen = 0;
    for (int right = 0; right < s.length(); right++) {
        while (seen.contains(s.charAt(right))) {
            seen.remove(s.charAt(left));
            left++;
        }
        seen.add(s.charAt(right));
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

#### Python

```python
def lengthOfLongestSubstringSet(self, s: str) -> int:
    """Sliding Window with Set — Time: O(n), Space: O(min(n, a))"""
    seen = set()
    left = max_len = 0
    for right, ch in enumerate(s):
        while ch in seen:
            seen.remove(s[left])
            left += 1
        seen.add(ch)
        max_len = max(max_len, right - left + 1)
    return max_len
```

### Dry Run

```text
Input: s = "abcabcbb"

left=0, seen={}

right=0 ('a'): 'a' not in seen → add 'a' → seen={a}, window=[a], len=1, maxLen=1
right=1 ('b'): 'b' not in seen → add 'b' → seen={a,b}, len=2, maxLen=2
right=2 ('c'): 'c' not in seen → add 'c' → seen={a,b,c}, len=3, maxLen=3
right=3 ('a'): 'a' IN seen → remove s[0]='a', left=1 → seen={b,c}
               'a' not in seen → add 'a' → seen={b,c,a}, len=3, maxLen=3
right=4 ('b'): 'b' IN seen → remove s[1]='b', left=2 → seen={c,a}
               'b' not in seen → add 'b' → seen={c,a,b}, len=3, maxLen=3
right=5 ('c'): 'c' IN seen → remove s[2]='c', left=3 → seen={a,b}
               'c' not in seen → add 'c' → seen={a,b,c}, len=3, maxLen=3
right=6 ('b'): 'b' IN seen → remove s[3]='a', left=4 → seen={b,c}
               'b' IN seen → remove s[4]='b', left=5 → seen={c}
               'b' not in seen → add 'b' → seen={c,b}, len=2, maxLen=3
right=7 ('b'): 'b' IN seen → remove s[5]='c', left=6 → seen={b}
               'b' IN seen → remove s[6]='b', left=7 → seen={}
               'b' not in seen → add 'b' → seen={b}, len=1, maxLen=3

Result: 3 ✅
```

---

## Approach 3: Optimal (Sliding Window with Last-Seen Index Map)

### The problem with Approach 2

> When a duplicate is found, Approach 2 shrinks left one step at a time until the duplicate is removed.
> For a string like `"aXXXXXa"`, finding the second `'a'` forces `left` to step through all the `X`s.
> We can do better: **jump left directly** to `lastSeen['a'] + 1`.

### Optimization idea

> **Replace the set with a map** that stores the **most recent index** of each character.
> When a duplicate `ch` is found at `right`, set `left = max(left, lastSeen[ch] + 1)`.
> The `max` ensures we never move `left` backwards (the duplicate might be to the left of the current window).

### Algorithm (step-by-step)

1. `left = 0`, `maxLen = 0`, `lastSeen = {}`
2. For `right` from `0` to `n-1`:
   a. `ch = s[right]`
   b. If `ch` in `lastSeen` AND `lastSeen[ch] >= left`:
      - `left = lastSeen[ch] + 1`  (jump past the previous occurrence)
   c. `lastSeen[ch] = right`
   d. `maxLen = max(maxLen, right - left + 1)`
3. Return `maxLen`

### Pseudocode

```text
function lengthOfLongestSubstring(s):
    lastSeen = {}
    left = 0, maxLen = 0
    for right = 0 to n-1:
        ch = s[right]
        if ch in lastSeen AND lastSeen[ch] >= left:
            left = lastSeen[ch] + 1
        lastSeen[ch] = right
        maxLen = max(maxLen, right - left + 1)
    return maxLen
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass; each character processed exactly once |
| **Space** | O(min(n, a)) | Map holds at most `a` entries, where `a` = alphabet size (≤128 for ASCII) |

### Implementation

#### Go

```go
// lengthOfLongestSubstring — Optimal: Sliding Window + Last-Seen Index Map
// Time: O(n), Space: O(min(n, a))
func lengthOfLongestSubstring(s string) int {
    lastSeen := make(map[byte]int)
    maxLen, left := 0, 0

    for right := 0; right < len(s); right++ {
        ch := s[right]
        if idx, ok := lastSeen[ch]; ok && idx >= left {
            left = idx + 1
        }
        lastSeen[ch] = right
        if right-left+1 > maxLen {
            maxLen = right - left + 1
        }
    }
    return maxLen
}
```

#### Java

```java
// lengthOfLongestSubstring — Optimal: Sliding Window + Last-Seen Index Map
// Time: O(n), Space: O(min(n, a))
public int lengthOfLongestSubstring(String s) {
    HashMap<Character, Integer> lastSeen = new HashMap<>();
    int maxLen = 0, left = 0;

    for (int right = 0; right < s.length(); right++) {
        char ch = s.charAt(right);
        if (lastSeen.containsKey(ch) && lastSeen.get(ch) >= left) {
            left = lastSeen.get(ch) + 1;
        }
        lastSeen.put(ch, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

#### Python

```python
def lengthOfLongestSubstring(self, s: str) -> int:
    """
    Optimal: Sliding Window + Last-Seen Index Map
    Time: O(n), Space: O(min(n, a))
    """
    last_seen: dict[str, int] = {}
    max_len = left = 0

    for right, ch in enumerate(s):
        if ch in last_seen and last_seen[ch] >= left:
            left = last_seen[ch] + 1
        last_seen[ch] = right
        max_len = max(max_len, right - left + 1)

    return max_len
```

### Dry Run

```text
Input: s = "abcabcbb"

lastSeen={}, left=0, maxLen=0

right=0, ch='a': not in lastSeen → lastSeen={a:0}, window=[0,0]="a", len=1, maxLen=1
right=1, ch='b': not in lastSeen → lastSeen={a:0,b:1}, window=[0,1]="ab", len=2, maxLen=2
right=2, ch='c': not in lastSeen → lastSeen={a:0,b:1,c:2}, window=[0,2]="abc", len=3, maxLen=3
right=3, ch='a': lastSeen[a]=0 >= left(0) → left=0+1=1
                 lastSeen={a:3,b:1,c:2}, window=[1,3]="bca", len=3, maxLen=3
right=4, ch='b': lastSeen[b]=1 >= left(1) → left=1+1=2
                 lastSeen={a:3,b:4,c:2}, window=[2,4]="cab", len=3, maxLen=3
right=5, ch='c': lastSeen[c]=2 >= left(2) → left=2+1=3
                 lastSeen={a:3,b:4,c:5}, window=[3,5]="abc", len=3, maxLen=3
right=6, ch='b': lastSeen[b]=4 >= left(3) → left=4+1=5
                 lastSeen={a:3,b:6,c:5}, window=[5,6]="cb", len=2, maxLen=3
right=7, ch='b': lastSeen[b]=6 >= left(5) → left=6+1=7
                 lastSeen={a:3,b:7,c:5}, window=[7,7]="b", len=1, maxLen=3

Result: 3 ✅
```

```text
Input: s = "dvdf"

lastSeen={}, left=0, maxLen=0

right=0, ch='d': not in map → lastSeen={d:0}, window="d", len=1, maxLen=1
right=1, ch='v': not in map → lastSeen={d:0,v:1}, window="dv", len=2, maxLen=2
right=2, ch='d': lastSeen[d]=0 >= left(0) → left=0+1=1
                 lastSeen={d:2,v:1}, window=[1,2]="vd", len=2, maxLen=2
right=3, ch='f': not in map → lastSeen={d:2,v:1,f:3}, window=[1,3]="vdf", len=3, maxLen=3

Result: 3 ✅

Key insight: at right=3, left is still 1 (not 0).
The 'd' at index 2 is still valid — the window [1,3]="vdf" has no duplicates.
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^2) | O(min(n,a)) | Simple, easy to understand | Slow, TLE on large inputs |
| 2 | Sliding Window + Set | O(n) | O(min(n,a)) | Clean two-pointer pattern | Left pointer moves one step at a time (extra iterations) |
| 3 | Sliding Window + Index Map | O(n) | O(min(n,a)) | Single pass, left jumps directly | Slightly more complex to reason about the `>= left` guard |

### Which solution to choose?

- **In an interview:** Approach 3 — demonstrates mastery of sliding window and hash map together
- **In production:** Approach 3 — fastest, clean code
- **On Leetcode:** Approach 3 — optimal time and space
- **For learning:** Approach 1 first → then Approach 2 → then Approach 3 to understand each optimization

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty string | `s = ""` | `0` | No characters at all |
| 2 | Single character | `s = "a"` | `1` | Only one character |
| 3 | All same characters | `s = "aaaa"` | `1` | Every window of length > 1 has a repeat |
| 4 | All unique characters | `s = "abcde"` | `5` | Entire string is the answer |
| 5 | Duplicate only at start | `s = "aab"` | `2` | Window "ab" is valid |
| 6 | Duplicate only at end | `s = "abb"` | `2` | Window "ab" is valid |
| 7 | Duplicate far left (stale) | `s = "dvdf"` | `3` | First 'd' is outside window when second duplicate arrives |
| 8 | Space characters | `s = "a b"` | `3` | Space is a valid unique character |

---

## Common Mistakes

### Mistake 1: Not guarding against stale map entries

```python
# ❌ WRONG — moves left even if the duplicate is outside the window
if ch in last_seen:
    left = last_seen[ch] + 1

# Example: s = "abba"
# right=3 ('a'), lastSeen['a']=0, left=2
# Without guard: left = 0+1 = 1 (moves left BACKWARDS — wrong!)
# Result would be 2 instead of correct 2 — coincidence; try "tmmzuxt" → wrong!

# ✅ CORRECT — only move left if the duplicate is inside the current window
if ch in last_seen and last_seen[ch] >= left:
    left = last_seen[ch] + 1
```

### Mistake 2: Moving left backwards

Related to Mistake 1 — always use `left = max(left, last_seen[ch] + 1)` as an alternative guard:

```python
# ✅ Also correct — max() prevents moving left backwards
if ch in last_seen:
    left = max(left, last_seen[ch] + 1)
```

### Mistake 3: Forgetting to update the map after moving left

```python
# ❌ WRONG — map not updated, so 'ch' still points to old index
if ch in last_seen and last_seen[ch] >= left:
    left = last_seen[ch] + 1
# forgot: last_seen[ch] = right  ← must always update!

# ✅ CORRECT — always record the latest position
if ch in last_seen and last_seen[ch] >= left:
    left = last_seen[ch] + 1
last_seen[ch] = right  # must always update regardless of the if-branch
```

### Mistake 4: Confusing substring with subsequence

```text
s = "pwwkew"
"pwke" has length 4 and no repeats — but it is NOT a substring (characters are not contiguous).
The answer is "wke" with length 3.
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [159. Longest Substring with At Most Two Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/) | 🟡 Medium | Same sliding window, allow up to 2 distinct |
| 2 | [340. Longest Substring with At Most K Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/) | 🟡 Medium | Generalization — allow up to K distinct |
| 3 | [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/) | 🔴 Hard | Sliding window with frequency map |
| 4 | [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | 🟡 Medium | Fixed-size sliding window with character counts |
| 5 | [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/) | 🟡 Medium | Fixed-size sliding window pattern |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — highlights every (i,j) pair, shows when a duplicate causes a break
> - **Sliding Window** tab — shows the window [left, right] expanding and shrinking in real time
> - **Index Map** tab — shows the map updating and left pointer jumping directly on duplicate
> - **Compare All** tab — side-by-side comparison of operations across all three approaches
