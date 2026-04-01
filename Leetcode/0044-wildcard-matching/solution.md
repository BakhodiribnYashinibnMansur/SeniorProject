# 0044. Wildcard Matching

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Dynamic Programming](#approach-1-dynamic-programming)
4. [Approach 2: Greedy with Backtracking](#approach-2-greedy-with-backtracking)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [44. Wildcard Matching](https://leetcode.com/problems/wildcard-matching/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `String`, `Dynamic Programming`, `Greedy`, `Recursion` |

### Description

> Given an input string `s` and a pattern `p`, implement wildcard pattern matching with support for `'?'` and `'*'` where:
> - `'?'` Matches any **single** character.
> - `'*'` Matches any **sequence** of characters (including the empty sequence).
>
> The matching should cover the **entire** input string (not partial).

### Examples

```
Example 1:
Input:  s = "aa", p = "a"
Output: false
Explanation: "a" does not match the entire string "aa".

Example 2:
Input:  s = "aa", p = "*"
Output: true
Explanation: '*' matches any sequence.

Example 3:
Input:  s = "cb", p = "?a"
Output: false
Explanation: '?' matches 'c', but 'a' does not match 'b'.

Example 4:
Input:  s = "adceb", p = "*a*b"
Output: true
Explanation: '*' matches "" (empty), then 'a' matches 'a', '*' matches "dce", then 'b' matches 'b'.
```

### Constraints

- `0 <= s.length, p.length <= 2000`
- `s` contains only lowercase English letters
- `p` contains only lowercase English letters, `'?'`, and `'*'`

---

## Problem Breakdown

### 1. What is being asked?

Implement a wildcard pattern matcher that supports two special characters. The entire string must match the pattern — partial matches do not count.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | Input string (only lowercase letters) |
| `p` | `string` | Pattern (lowercase letters, `'?'`, `'*'`) |

Key observations about wildcards:
- `'?'` matches exactly one character (any character)
- `'*'` matches any sequence of characters, including the empty string
- Unlike regex `'*'`, wildcard `'*'` is standalone — it does NOT reference a preceding element
- Multiple consecutive `'*'` behave the same as a single `'*'`

### 3. What is the output?

- `true` if `s` fully matches `p`
- `false` otherwise

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length, p.length <= 2000` | O(m*n) DP is feasible (4 million cells max) |
| `s` is only lowercase letters | No wildcards in `s`, only in `p` |
| `'*'` is standalone | Unlike regex — simpler semantics, different DP transitions |

### 5. Step-by-step example analysis

#### Example: `s = "adceb"`, `p = "*a*b"`

```text
Pattern breakdown:
  "*"  → matches "" (empty prefix)
  "a"  → matches 'a'
  "*"  → matches "dce"
  "b"  → matches 'b'
  → Total: "" + "a" + "dce" + "b" = "adceb" ✅
```

#### Example: `s = "acdcb"`, `p = "a*c?b"`

```text
Pattern: a  *  c  ?  b
String:  a  cd c  b
                   ↑
         After "a*c?", we've consumed "acdc". Remaining is 'b'.
         Pattern 'b' matches 'b'?
         BUT '*' matched "cd", then 'c' matches 'c', '?' matches 'b',
         but then 'b' has nothing left to match in s → false

         Actually: a matches a, * matches "", c matches c, ? matches d,
         b needs to match c → false
         Or: a matches a, * matches "cd", c matches c, ? matches b,
         b has nothing to match → false
         No way to partition → false ✅
```

### 6. Key Observations

1. **`'*'` is standalone:** Unlike regex where `'*'` modifies the preceding element, here `'*'` independently matches any sequence. This means `'*'` can consume 0, 1, 2, ... characters from `s`.
2. **Consecutive `'*'` collapse:** `"***"` is equivalent to `"*"`.
3. **Two sub-problems for `'*'`:**
   - Match **empty** → advance pattern pointer, keep string pointer
   - Match **one more character** → advance string pointer, keep pattern pointer
4. **Overlapping subproblems:** `match(s[i:], p[j:])` may be called with the same `(i, j)` many times → DP applies.
5. **Greedy insight:** We can process `'*'` greedily — try matching 0 chars first, and if we get stuck later, backtrack to the last `'*'` and let it consume one more character.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Dynamic Programming | Overlapping subproblems, optimal substructure, 2D state (i, j) |
| Greedy + Backtracking | `'*'` can be handled greedily with a single backtrack pointer |

**DP State:** `dp[i][j]` = does `s[0..i-1]` match `p[0..j-1]`?

---

## Approach 1: Dynamic Programming

### Thought process

> Build a 2D table `dp[i][j]` where each cell represents whether `s[0..i-1]` matches `p[0..j-1]`. The transitions depend on whether `p[j-1]` is a letter, `'?'`, or `'*'`. For `'*'`, we combine two sub-cases: match empty (`dp[i][j-1]`) or match one more char (`dp[i-1][j]`).

### Algorithm (step-by-step)

1. Create `dp` table of size `(m+1) x (n+1)`, initialized to `false`
2. **Base case 1:** `dp[0][0] = true` (empty matches empty)
3. **Base case 2:** For `j` from 1 to `n`: if `p[j-1] == '*'` then `dp[0][j] = dp[0][j-1]`
   - Leading `'*'`s can match the empty string
4. **Fill for `i = 1..m`, `j = 1..n`:**
   - If `p[j-1] == '*'`:
     - `dp[i][j] = dp[i][j-1] || dp[i-1][j]`
     - `dp[i][j-1]`: `'*'` matches empty sequence
     - `dp[i-1][j]`: `'*'` matches one more character from `s`
   - Else if `p[j-1] == '?'` or `p[j-1] == s[i-1]`:
     - `dp[i][j] = dp[i-1][j-1]` (single character match)
5. Return `dp[m][n]`

### Pseudocode

```text
function isMatch(s, p):
    m, n = len(s), len(p)
    dp = (m+1) x (n+1) table of false

    dp[0][0] = true
    for j = 1 to n:
        if p[j-1] == '*':
            dp[0][j] = dp[0][j-1]

    for i = 1 to m:
        for j = 1 to n:
            if p[j-1] == '*':
                dp[i][j] = dp[i][j-1] OR dp[i-1][j]
            elif p[j-1] == '?' or p[j-1] == s[i-1]:
                dp[i][j] = dp[i-1][j-1]

    return dp[m][n]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m * n) | We compute each cell exactly once; m = len(s), n = len(p) |
| **Space** | O(m * n) | The DP table has (m+1)(n+1) cells |

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
    for j := 1; j <= n; j++ {
        if p[j-1] == '*' { dp[0][j] = dp[0][j-1] }
    }

    for i := 1; i <= m; i++ {
        for j := 1; j <= n; j++ {
            if p[j-1] == '*' {
                dp[i][j] = dp[i][j-1] || dp[i-1][j]
            } else if p[j-1] == '?' || p[j-1] == s[i-1] {
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

    for (int j = 1; j <= n; j++) {
        if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 1];
    }

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
            } else if (p.charAt(j - 1) == '?' || p.charAt(j - 1) == s.charAt(i - 1)) {
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

    for j in range(1, n + 1):
        if p[j - 1] == '*':
            dp[0][j] = dp[0][j - 1]

    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if p[j - 1] == '*':
                dp[i][j] = dp[i][j - 1] or dp[i - 1][j]
            elif p[j - 1] == '?' or p[j - 1] == s[i - 1]:
                dp[i][j] = dp[i - 1][j - 1]

    return dp[m][n]
```

### Dry Run

```text
s = "adceb", p = "*a*b"
m=5, n=4

Indices:      j=0  j=1  j=2  j=3  j=4
               ""   "*"  "a"  "*"  "b"

i=0 (s=""):  [T,   T,   F,   F,   F]
             dp[0][0]=T  (base)
             dp[0][1]=dp[0][0]=T  (p[0]='*', matches empty)
             dp[0][2]: p[1]='a' ≠ '*' → F
             dp[0][3]: p[2]='*' but dp[0][2]=F → F
             dp[0][4]: p[3]='b' ≠ '*' → F

i=1 (s="a"):
  j=1 ('*'): dp[1][0]=F | dp[0][1]=T → T
  j=2 ('a'): 'a'=='a' → dp[0][1]=T → T
  j=3 ('*'): dp[1][2]=T | dp[0][3]=F → T
  j=4 ('b'): 'b'≠'a' → F

Row i=1: [F, T, T, T, F]

i=2 (s="ad"):
  j=1 ('*'): dp[2][0]=F | dp[1][1]=T → T
  j=2 ('a'): 'a'≠'d' → F
  j=3 ('*'): dp[2][2]=F | dp[1][3]=T → T
  j=4 ('b'): 'b'≠'d' → F

Row i=2: [F, T, F, T, F]

i=3 (s="adc"):
  j=1 ('*'): dp[3][0]=F | dp[2][1]=T → T
  j=2 ('a'): 'a'≠'c' → F
  j=3 ('*'): dp[3][2]=F | dp[2][3]=T → T
  j=4 ('b'): 'b'≠'c' → F

Row i=3: [F, T, F, T, F]

i=4 (s="adce"):
  j=1 ('*'): dp[4][0]=F | dp[3][1]=T → T
  j=2 ('a'): 'a'≠'e' → F
  j=3 ('*'): dp[4][2]=F | dp[3][3]=T → T
  j=4 ('b'): 'b'≠'e' → F

Row i=4: [F, T, F, T, F]

i=5 (s="adceb"):
  j=1 ('*'): dp[5][0]=F | dp[4][1]=T → T
  j=2 ('a'): 'a'≠'b' → F
  j=3 ('*'): dp[5][2]=F | dp[4][3]=T → T
  j=4 ('b'): 'b'=='b' → dp[4][3]=T → T ✅

dp[5][4] = true → "adceb" matches "*a*b" ✅
```

---

## Approach 2: Greedy with Backtracking

### Thought process

> Instead of building a full DP table, we use two pointers — one for `s` and one for `p`. When we encounter `'*'`, we record its position and try matching 0 characters first. If we get stuck later, we backtrack to the last `'*'` and let it consume one more character. This approach runs in O(m*n) worst case but O(m+n) in practice for most inputs.

### Algorithm (step-by-step)

1. Initialize `sIdx = 0`, `pIdx = 0`, `starIdx = -1`, `matchIdx = 0`
2. While `sIdx < len(s)`:
   - If `pIdx < len(p)` and (`p[pIdx] == s[sIdx]` or `p[pIdx] == '?'`):
     - Characters match → advance both pointers
   - Else if `pIdx < len(p)` and `p[pIdx] == '*'`:
     - Record `starIdx = pIdx`, `matchIdx = sIdx`
     - Advance `pIdx` (try `'*'` matching 0 chars first)
   - Else if `starIdx != -1`:
     - Backtrack: let the last `'*'` match one more char
     - `matchIdx++`, `sIdx = matchIdx`, `pIdx = starIdx + 1`
   - Else: return `false`
3. After consuming all of `s`, skip any remaining `'*'`s in `p`
4. Return `pIdx == len(p)`

### Pseudocode

```text
function isMatch(s, p):
    sIdx = 0, pIdx = 0
    starIdx = -1, matchIdx = 0

    while sIdx < len(s):
        if pIdx < len(p) AND (p[pIdx] == s[sIdx] OR p[pIdx] == '?'):
            sIdx++, pIdx++
        elif pIdx < len(p) AND p[pIdx] == '*':
            starIdx = pIdx, matchIdx = sIdx
            pIdx++
        elif starIdx != -1:
            matchIdx++
            sIdx = matchIdx
            pIdx = starIdx + 1
        else:
            return false

    while pIdx < len(p) AND p[pIdx] == '*':
        pIdx++

    return pIdx == len(p)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m * n) worst case | Each backtrack resets sIdx; but in practice often O(m+n) |
| **Space** | O(1) | Only uses a fixed number of variables |

### Implementation

#### Go

```go
// isMatch — Greedy with Backtracking
// Time: O(m*n) worst case, Space: O(1)
func isMatch(s string, p string) bool {
    sIdx, pIdx := 0, 0
    starIdx, matchIdx := -1, 0

    for sIdx < len(s) {
        if pIdx < len(p) && (p[pIdx] == s[sIdx] || p[pIdx] == '?') {
            sIdx++
            pIdx++
        } else if pIdx < len(p) && p[pIdx] == '*' {
            starIdx = pIdx
            matchIdx = sIdx
            pIdx++
        } else if starIdx != -1 {
            matchIdx++
            sIdx = matchIdx
            pIdx = starIdx + 1
        } else {
            return false
        }
    }
    for pIdx < len(p) && p[pIdx] == '*' {
        pIdx++
    }
    return pIdx == len(p)
}
```

#### Java

```java
// isMatch — Greedy with Backtracking
// Time: O(m*n) worst case, Space: O(1)
public boolean isMatch(String s, String p) {
    int sIdx = 0, pIdx = 0;
    int starIdx = -1, matchIdx = 0;

    while (sIdx < s.length()) {
        if (pIdx < p.length() && (p.charAt(pIdx) == s.charAt(sIdx) || p.charAt(pIdx) == '?')) {
            sIdx++;
            pIdx++;
        } else if (pIdx < p.length() && p.charAt(pIdx) == '*') {
            starIdx = pIdx;
            matchIdx = sIdx;
            pIdx++;
        } else if (starIdx != -1) {
            matchIdx++;
            sIdx = matchIdx;
            pIdx = starIdx + 1;
        } else {
            return false;
        }
    }
    while (pIdx < p.length() && p.charAt(pIdx) == '*') {
        pIdx++;
    }
    return pIdx == p.length();
}
```

#### Python

```python
# isMatch — Greedy with Backtracking
# Time: O(m*n) worst case, Space: O(1)
def isMatch(self, s: str, p: str) -> bool:
    s_idx, p_idx = 0, 0
    star_idx, match_idx = -1, 0

    while s_idx < len(s):
        if p_idx < len(p) and (p[p_idx] == s[s_idx] or p[p_idx] == '?'):
            s_idx += 1
            p_idx += 1
        elif p_idx < len(p) and p[p_idx] == '*':
            star_idx = p_idx
            match_idx = s_idx
            p_idx += 1
        elif star_idx != -1:
            match_idx += 1
            s_idx = match_idx
            p_idx = star_idx + 1
        else:
            return False

    while p_idx < len(p) and p[p_idx] == '*':
        p_idx += 1

    return p_idx == len(p)
```

### Dry Run

```text
s = "adceb", p = "*a*b"

Step 1: sIdx=0('a'), pIdx=0('*')
  p[0]='*' → starIdx=0, matchIdx=0, pIdx=1

Step 2: sIdx=0('a'), pIdx=1('a')
  'a'=='a' → sIdx=1, pIdx=2

Step 3: sIdx=1('d'), pIdx=2('*')
  p[2]='*' → starIdx=2, matchIdx=1, pIdx=3

Step 4: sIdx=1('d'), pIdx=3('b')
  'd'≠'b', starIdx=2 → backtrack
  matchIdx=2, sIdx=2, pIdx=3

Step 5: sIdx=2('c'), pIdx=3('b')
  'c'≠'b', starIdx=2 → backtrack
  matchIdx=3, sIdx=3, pIdx=3

Step 6: sIdx=3('e'), pIdx=3('b')
  'e'≠'b', starIdx=2 → backtrack
  matchIdx=4, sIdx=4, pIdx=3

Step 7: sIdx=4('b'), pIdx=3('b')
  'b'=='b' → sIdx=5, pIdx=4

sIdx=5 ≥ len(s)=5 → exit loop
pIdx=4 == len(p)=4 → true ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Dynamic Programming | O(m*n) | O(m*n) | Handles all cases, easy to reason about | Uses O(m*n) memory |
| 2 | Greedy + Backtracking | O(m*n) worst | O(1) | Constant space, fast in practice | Harder to prove correctness |

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Both empty | `s=""`, `p=""` | `true` | Empty matches empty |
| 2 | Empty string, star pattern | `s=""`, `p="*"` | `true` | `'*'` matches empty sequence |
| 3 | Empty pattern, non-empty string | `s="a"`, `p=""` | `false` | No pattern to match 'a' |
| 4 | `'?'` needs exactly one char | `s=""`, `p="?"` | `false` | `'?'` must match one char |
| 5 | Pattern shorter | `s="aa"`, `p="a"` | `false` | Partial match doesn't count |
| 6 | Single star matches all | `s="anything"`, `p="*"` | `true` | `'*'` matches any sequence |
| 7 | Star at end | `s="abc"`, `p="abc*"` | `true` | `'*'` matches empty |
| 8 | Multiple consecutive stars | `s="abc"`, `p="a***c"` | `true` | `***` same as `*` |

---

## Common Mistakes

### Mistake 1: Forgetting to handle trailing `'*'`s in greedy approach

```python
# ❌ WRONG — after consuming all of s, remaining pattern may have '*'s
while s_idx < len(s):
    ...
return p_idx == len(p)  # fails if pattern has trailing '*'

# ✅ CORRECT — skip trailing '*'s before final check
while p_idx < len(p) and p[p_idx] == '*':
    p_idx += 1
return p_idx == len(p)
```

**Reason:** After matching all characters in `s`, the remaining pattern characters must all be `'*'` to still yield a match (since `'*'` can match empty).

### Mistake 2: Confusing wildcard `'*'` with regex `'*'`

```python
# ❌ WRONG — treating '*' like regex (zero or more of PRECEDING element)
if p[j-1] == '*':
    dp[i][j] = dp[i][j-2]  # this is for regex, not wildcard!

# ✅ CORRECT — wildcard '*' matches any sequence independently
if p[j-1] == '*':
    dp[i][j] = dp[i][j-1] or dp[i-1][j]  # match empty or match one more char
```

**Reason:** In wildcard matching, `'*'` is standalone and matches any sequence. It does not reference a preceding element. The DP transition is `dp[i][j-1]` (match empty) or `dp[i-1][j]` (match one more character).

### Mistake 3: Not handling empty string vs star-only pattern

```python
# ❌ WRONG — base case only sets dp[0][0]
dp[0][0] = True
# Misses that "*", "**", "***" all match ""

# ✅ CORRECT — leading '*'s can match empty string
dp[0][0] = True
for j in range(1, n + 1):
    if p[j - 1] == '*':
        dp[0][j] = dp[0][j - 1]
```

**Reason:** A pattern like `"***"` should match the empty string. Each `'*'` propagates the "matches empty" from `dp[0][j-1]`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [10. Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | :red_circle: Hard | Similar DP; `'*'` modifies preceding element instead of being standalone |
| 2 | [72. Edit Distance](https://leetcode.com/problems/edit-distance/) | :yellow_circle: Medium | 2D DP on two strings |
| 3 | [97. Interleaving String](https://leetcode.com/problems/interleaving-string/) | :yellow_circle: Medium | 2D DP — can s3 be formed by interleaving s1 and s2 |
| 4 | [115. Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | :red_circle: Hard | 2D DP on string matching |
| 5 | [1143. Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | :yellow_circle: Medium | Classic 2D DP on two strings |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **DP Table** tab — fills the `(m+1) x (n+1)` grid cell by cell
> - **Greedy** tab — shows the two-pointer approach with backtracking
> - Highlights current `s[i]` and `p[j]` being compared
> - Shows base case initialization and `'*'` propagation
> - Step-by-step trace with presets like `s="adceb"`, `p="*a*b"`
