# 0010. Regular Expression Matching

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Recursion](#approach-1-recursion)
4. [Approach 2: Bottom-up Dynamic Programming (Optimal)](#approach-2-bottom-up-dynamic-programming-optimal)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [10. Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) |
| **Difficulty** | 🔴 Hard |
| **Tags** | `String`, `Dynamic Programming`, `Recursion` |

### Description

> Given an input string `s` and a pattern `p`, implement regular expression matching with support for `'.'` and `'*'` where:
> - `'.'` Matches any single character.
> - `'*'` Matches zero or more of the preceding element.
>
> The matching should cover the **entire** input string (not partial).

### Examples

```
Example 1:
Input:  s = "aa", p = "a"
Output: false
Explanation: "a" does not match the entire string "aa".

Example 2:
Input:  s = "aa", p = "a*"
Output: true
Explanation: '*' means zero or more 'a's. "a*" matches "aa".

Example 3:
Input:  s = "ab", p = ".*"
Output: true
Explanation: ".*" means zero or more of any character. It matches "ab".

Example 4:
Input:  s = "aab", p = "c*a*b"
Output: true
Explanation: 'c' is repeated 0 times, 'a' is repeated 2 times, 'b' once. Matches "aab".
```

### Constraints

- `1 <= s.length <= 20`
- `1 <= p.length <= 30`
- `s` contains only lowercase English letters
- `p` contains only lowercase English letters, `'.'`, and `'*'`
- It is guaranteed that for each occurrence of `'*'`, there will be a previous valid character to match

---

## Problem Breakdown

### 1. What is being asked?

Implement a regex matcher that supports two special characters. The entire string must match the pattern — partial matches do not count.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | Input string (only lowercase letters) |
| `p` | `string` | Pattern (lowercase letters, `'.'`, `'*'`) |

Key observations about `'*'`:
- `'*'` is always preceded by a valid character or `'.'`
- It means "**zero or more** of the preceding element"
- `"a*"` can match `""`, `"a"`, `"aa"`, `"aaa"`, ...
- `".*"` can match any string (including `""`)

### 3. What is the output?

- `true` if `s` fully matches `p`
- `false` otherwise

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 20, p.length <= 30` | Small input — both O(m*n) DP and recursion are feasible |
| `'*'` always has a preceding element | No need to handle `'*'` at the start of pattern |
| Entire string must match | Cannot stop early; every character of `s` must be consumed |

### 5. Step-by-step example analysis

#### Example: `s = "aab"`, `p = "c*a*b"`

```text
Pattern breakdown:
  "c*" → 0 or more 'c'
  "a*" → 0 or more 'a'
  "b"  → exactly one 'b'

Matching:
  "c*" → matches "" (0 c's)
  "a*" → matches "aa" (2 a's)
  "b"  → matches "b"
  → Total: "" + "aa" + "b" = "aab" ✅
```

#### Example: `s = "mississippi"`, `p = "mis*is*p*."`

```text
Pattern:  m  i  s*  i  s*  p*  .
String:   m  i  ss  i  ss  i   pp  i
                          ↑
                 After "s*" (2 s's), we have 'i', but pattern expects 'p*'.
                 No way to match → false ✅
```

### 6. Key Observations

1. **`'*'` introduces choice:** It can match zero or more of the preceding element. This creates branching in the solution space.
2. **Two sub-problems for `'*'`:**
   - Use `'*'` as **zero occurrences** → skip `p[j-2]p[j-1]` entirely
   - Use `'*'` as **one more occurrence** → consume one matching char from `s`
3. **Overlapping subproblems:** `isMatch(s[i:], p[j:])` may be called multiple times with the same arguments → memoization / DP applies.
4. **Full match required:** The base case is `s` empty AND `p` empty (or reducible to empty via `x*` eliminations).

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Recursion | Natural decomposition: match first char, recurse on rest |
| Dynamic Programming | Overlapping subproblems, optimal substructure |

**DP State:** `dp[i][j]` = does `s[0..i-1]` match `p[0..j-1]`?

---

## Approach 1: Recursion

### Thought process

> Break the problem into subproblems: if the first characters match (considering `'.'`), then the answer depends on whether the remaining suffixes match. The tricky case is `'*'`: we either skip the `x*` pair entirely (0 occurrences) or consume one character from `s` and recurse with the same pattern (1+ occurrences).

### Algorithm (step-by-step)

1. **Base case:** if `p` is empty, return `s` is empty
2. **First match:** `first_match = s non-empty AND (p[0] == '.' OR p[0] == s[0])`
3. **If `p[1] == '*'`:**
   - **0 occurrences:** `isMatch(s, p[2:])` — skip `x*`
   - **1+ occurrences:** `first_match AND isMatch(s[1:], p)` — consume one char, keep pattern
4. **Otherwise:** `first_match AND isMatch(s[1:], p[1:])`

### Pseudocode

```text
function isMatch(s, p):
    if p is empty:
        return s is empty

    first_match = s not empty AND (p[0] == '.' OR p[0] == s[0])

    if len(p) >= 2 AND p[1] == '*':
        return isMatch(s, p[2:])           // 0 occurrences
            OR (first_match AND isMatch(s[1:], p))  // 1+ occurrences
    else:
        return first_match AND isMatch(s[1:], p[1:])
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(2^(m+n)) | Each `'*'` creates a binary choice; worst case exponential branching |
| **Space** | O(m+n) | Maximum recursion depth is `m+n` |

### Implementation

#### Go

```go
// isMatch — Recursion
// Time: O(2^(m+n)), Space: O(m+n)
func isMatch(s string, p string) bool {
    if len(p) == 0 {
        return len(s) == 0
    }
    firstMatch := len(s) > 0 && (p[0] == '.' || p[0] == s[0])
    if len(p) >= 2 && p[1] == '*' {
        return isMatch(s, p[2:]) || (firstMatch && isMatch(s[1:], p))
    }
    return firstMatch && isMatch(s[1:], p[1:])
}
```

#### Java

```java
// isMatch — Recursion
// Time: O(2^(m+n)), Space: O(m+n)
public boolean isMatch(String s, String p) {
    if (p.isEmpty()) return s.isEmpty();
    boolean firstMatch = !s.isEmpty() &&
        (p.charAt(0) == '.' || p.charAt(0) == s.charAt(0));
    if (p.length() >= 2 && p.charAt(1) == '*') {
        return isMatch(s, p.substring(2)) ||
               (firstMatch && isMatch(s.substring(1), p));
    }
    return firstMatch && isMatch(s.substring(1), p.substring(1));
}
```

#### Python

```python
# isMatch — Recursion
# Time: O(2^(m+n)), Space: O(m+n)
def isMatch(self, s: str, p: str) -> bool:
    if not p:
        return not s
    first_match = bool(s) and (p[0] == '.' or p[0] == s[0])
    if len(p) >= 2 and p[1] == '*':
        return self.isMatch(s, p[2:]) or (first_match and self.isMatch(s[1:], p))
    return first_match and self.isMatch(s[1:], p[1:])
```

### Dry Run

```text
isMatch("aab", "c*a*b")

p[1]='*' → try:
  A: isMatch("aab", "a*b")       [0 c's]
     p[1]='*' → try:
       A1: isMatch("aab", "b")   [0 a's] → first='a'!='b' → false
       A2: first='a'=='a' → isMatch("ab", "a*b")
           A2a: isMatch("ab", "b")  → 'a'!='b' → false
           A2b: first='a'=='a' → isMatch("b", "a*b")
               A2b-a: isMatch("b","b") → true ✅
```

---

## Approach 2: Bottom-up Dynamic Programming (Optimal)

### Thought process

> The recursion re-computes the same subproblems many times. We memoize by building a 2D table `dp[i][j]` where each cell represents whether `s[0..i-1]` matches `p[0..j-1]`. We fill the table bottom-up from smaller substrings to larger ones.

### Algorithm (step-by-step)

1. Create `dp` table of size `(m+1) × (n+1)`, initialized to `false`
2. **Base case 1:** `dp[0][0] = true` (empty matches empty)
3. **Base case 2:** For `j` from 2 to `n`: if `p[j-1] == '*'` then `dp[0][j] = dp[0][j-2]`
   - This handles patterns like `"a*b*"` matching an empty string
4. **Fill for `i = 1..m`, `j = 1..n`:**
   - If `p[j-1] == '*'`:
     - **Zero occurrences:** `dp[i][j] |= dp[i][j-2]`
     - **One+ occurrences:** if `p[j-2] == '.'` or `p[j-2] == s[i-1]` then `dp[i][j] |= dp[i-1][j]`
   - Else if `p[j-1] == '.'` or `p[j-1] == s[i-1]`:
     - **Exact/dot match:** `dp[i][j] = dp[i-1][j-1]`
5. Return `dp[m][n]`

### Pseudocode

```text
function isMatch(s, p):
    m, n = len(s), len(p)
    dp = (m+1) × (n+1) table of false

    dp[0][0] = true
    for j = 2 to n:
        if p[j-1] == '*':
            dp[0][j] = dp[0][j-2]

    for i = 1 to m:
        for j = 1 to n:
            if p[j-1] == '*':
                dp[i][j] = dp[i][j-2]                            // 0 occurrences
                if p[j-2] == '.' or p[j-2] == s[i-1]:
                    dp[i][j] = dp[i][j] OR dp[i-1][j]            // 1+ occurrences
            elif p[j-1] == '.' or p[j-1] == s[i-1]:
                dp[i][j] = dp[i-1][j-1]                          // single char match

    return dp[m][n]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m * n) | We compute each cell exactly once; m = len(s), n = len(p) |
| **Space** | O(m * n) | The DP table has (m+1)(n+1) cells |

> Space can be optimized to O(n) using only the previous row, but the 2D table is clearest for understanding.

### Implementation

#### Go

```go
// isMatch — Bottom-up DP
// Time: O(m*n), Space: O(m*n)
func isMatch(s string, p string) bool {
    m, n := len(s), len(p)
    dp := make([][]bool, m+1)
    for i := range dp { dp[i] = make([]bool, n+1) }

    dp[0][0] = true
    for j := 2; j <= n; j++ {
        if p[j-1] == '*' { dp[0][j] = dp[0][j-2] }
    }

    for i := 1; i <= m; i++ {
        for j := 1; j <= n; j++ {
            if p[j-1] == '*' {
                dp[i][j] = dp[i][j-2]
                if p[j-2] == '.' || p[j-2] == s[i-1] {
                    dp[i][j] = dp[i][j] || dp[i-1][j]
                }
            } else if p[j-1] == '.' || p[j-1] == s[i-1] {
                dp[i][j] = dp[i-1][j-1]
            }
        }
    }
    return dp[m][n]
}
```

#### Java

```java
// isMatch — Bottom-up DP
// Time: O(m*n), Space: O(m*n)
public boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m + 1][n + 1];
    dp[0][0] = true;

    for (int j = 2; j <= n; j++) {
        if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];
    }

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[i][j] = dp[i][j - 2];
                if (p.charAt(j - 2) == '.' || p.charAt(j - 2) == s.charAt(i - 1))
                    dp[i][j] = dp[i][j] || dp[i - 1][j];
            } else if (p.charAt(j - 1) == '.' || p.charAt(j - 1) == s.charAt(i - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            }
        }
    }
    return dp[m][n];
}
```

#### Python

```python
# isMatch — Bottom-up DP
# Time: O(m*n), Space: O(m*n)
def isMatch(self, s: str, p: str) -> bool:
    m, n = len(s), len(p)
    dp = [[False] * (n + 1) for _ in range(m + 1)]
    dp[0][0] = True

    for j in range(2, n + 1):
        if p[j - 1] == '*':
            dp[0][j] = dp[0][j - 2]

    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if p[j - 1] == '*':
                dp[i][j] = dp[i][j - 2]
                if p[j - 2] == '.' or p[j - 2] == s[i - 1]:
                    dp[i][j] = dp[i][j] or dp[i - 1][j]
            elif p[j - 1] == '.' or p[j - 1] == s[i - 1]:
                dp[i][j] = dp[i - 1][j - 1]

    return dp[m][n]
```

### Dry Run

```text
s = "aab", p = "c*a*b"
m=3, n=5

Indices:      j=0  j=1  j=2  j=3  j=4  j=5
               ""   "c"  "c*"  "a"  "a*"  "b"

i=0 (s=""):  [T,   F,   T,    F,   T,    F]
             dp[0][0]=T  (base)
             dp[0][2]=dp[0][0]=T  (p[1]='*', skip "c*")
             dp[0][4]=dp[0][2]=T  (p[3]='*', skip "a*")

i=1 (s="a"):
  j=1 (p='c'): 'c'!='a' → F
  j=2 (p='*'): dp[1][0] | (p[0]='c'!='a') = F
  j=3 (p='a'): 'a'=='a' → dp[0][2] = T
  j=4 (p='*'): dp[1][2]=F | (p[2]='a'=='a' → dp[0][4]=T) → T
  j=5 (p='b'): 'b'!='a' → F

Row i=1: [F, F, F, T, T, F]

i=2 (s="aa"):
  j=3 (p='a'): 'a'=='a' → dp[1][2]=F → F
  j=4 (p='*'): dp[2][2]=F | (p[2]='a'=='a' → dp[1][4]=T) → T
  j=5 (p='b'): 'b'!='a' → F

Row i=2: [F, F, F, F, T, F]

i=3 (s="aab"):
  j=4 (p='*'): dp[3][2]=F | (p[2]='a'!='b') → F
  j=5 (p='b'): 'b'=='b' → dp[2][4]=T → T ✅

dp[3][5] = true → "aab" matches "c*a*b" ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Recursion | O(2^(m+n)) | O(m+n) | Simple, matches problem definition directly | Exponential time, TLE on large inputs |
| 2 | Bottom-up DP | O(m*n) | O(m*n) | Polynomial time, handles all cases efficiently | Requires understanding the DP transition |

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Pattern must match entire string | `s="aa"`, `p="a"` | `false` | Partial match does not count |
| 2 | `'*'` as zero occurrences | `s="b"`, `p="a*b"` | `true` | `a*` matches `""`, then `b` matches `b` |
| 3 | `".*"` matches everything | `s="anystring"`, `p=".*"` | `true` | `.` matches any char, `*` repeats it |
| 4 | Empty string vs non-`*` pattern | `s=""`, `p="a"` | `false` | `s` is empty, pattern requires a char |
| 5 | Empty string with `x*` chain | `s=""`, `p="a*b*c*"` | `true` | Every `x*` used as 0 occurrences |
| 6 | Single char exact match | `s="a"`, `p="a"` | `true` | Direct character match |
| 7 | Dot matches exactly one | `s=""`, `p="."` | `false` | `.` must match exactly one char, `s` is empty |
| 8 | Complex pattern | `s="aaa"`, `p="a*a"` | `true` | `a*` matches `aa`, then `a` matches last `a` |

---

## Common Mistakes

### Mistake 1: Partial match (not checking full string consumed)

```python
# ❌ WRONG — pattern matches a prefix of s, not the full string
def isMatch(s, p):
    if not p: return True   # BUG: returns true even if s still has characters

# ✅ CORRECT — base case must check BOTH s and p are empty
def isMatch(s, p):
    if not p: return not s  # s must also be empty
```

**Reason:** The problem says the matching should cover the **entire** input string. A pattern `"a"` should NOT match `"ab"`.

### Mistake 2: Swapping the `'*'` cases

```python
# ❌ WRONG — checking 1+ occurrences first without verifying first_match
dp[i][j] = dp[i-1][j]  # assumes match, but p[j-2] might not match s[i-1]

# ✅ CORRECT — guard with character match check
if p[j-2] == '.' or p[j-2] == s[i-1]:
    dp[i][j] = dp[i][j] or dp[i-1][j]
```

**Reason:** The 1+ occurrences case requires that the character before `'*'` actually matches `s[i-1]`. Skipping this check causes false positives.

### Mistake 3: Off-by-one in DP indices

```python
# ❌ WRONG — using s[i] and p[j] directly (zero-indexed), forgetting the offset
if p[j] == '*':  # should be p[j-1] since dp[i][j] covers s[0..i-1], p[0..j-1]

# ✅ CORRECT — dp[i][j] refers to the first i chars of s and first j chars of p
if p[j-1] == '*':  # p[j-1] is the j-th character (1-indexed pattern position)
```

**Reason:** The DP table is 1-indexed (size `m+1` × `n+1`) to allow an empty-string base case at index 0. Character `s[i-1]` corresponds to `dp` row `i`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [44. Wildcard Matching](https://leetcode.com/problems/wildcard-matching/) | 🔴 Hard | Similar DP; `'*'` matches any sequence (not "zero or more of preceding") |
| 2 | [72. Edit Distance](https://leetcode.com/problems/edit-distance/) | 🟡 Medium | 2D DP on two strings |
| 3 | [97. Interleaving String](https://leetcode.com/problems/interleaving-string/) | 🟡 Medium | 2D DP — can s3 be formed by interleaving s1 and s2 |
| 4 | [115. Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | 🔴 Hard | 2D DP on string matching / subsequences |
| 5 | [516. Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) | 🟡 Medium | 2D DP on strings |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Recursion** tab — shows the call tree branching at each `'*'` (0 vs 1+ occurrences)
> - **DP Table** tab — fills the `(m+1) × (n+1)` grid cell by cell
> - Highlights current `s[i-1]` and `p[j-1]` being compared
> - Shows base case initialization and the `'*'` zero-occurrence row
> - Step-by-step trace for `s="aab"`, `p="c*a*b"`
