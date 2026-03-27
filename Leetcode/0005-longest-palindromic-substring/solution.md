# 0005. Longest Palindromic Substring

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Dynamic Programming](#approach-2-dynamic-programming)
5. [Approach 3: Optimal (Expand Around Center)](#approach-3-optimal-expand-around-center)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `String`, `Dynamic Programming`, `Two Pointers` |

### Description

> Given a string `s`, return the **longest palindromic substring** in `s`.

### Examples

```
Example 1:
Input:  s = "babad"
Output: "bab"
Note:   "aba" is also a valid answer.

Example 2:
Input:  s = "cbbd"
Output: "bb"
```

### Constraints

- `1 <= s.length <= 1000`
- `s` consists of only digits and English letters

---

## Problem Breakdown

### 1. What is being asked?

Find the **longest contiguous substring** of `s` that reads the same forwards and backwards (a palindrome).

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | Input string of digits and/or English letters |

Key observations:
- Length at least 1 — single-character input is always a valid palindrome
- May contain digits as well as letters
- Multiple valid answers may exist (any of equal maximum length is accepted)

### 3. What is the output?

- A **substring** of `s` (not a new string — a contiguous slice)
- It must be a palindrome
- If multiple palindromes share the maximum length, **any** one is acceptable
- Minimum length is 1 (a single character)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 1000` | O(n^2) is acceptable (~10^6 operations); O(n^3) brute force also passes but is slow |
| Only digits and letters | No special Unicode characters — safe for direct index access |
| At least 1 character | No empty-string edge case from constraints (but good practice to handle it) |

### 5. Step-by-step example analysis

#### Example 1: `s = "babad"`

```text
All palindromic substrings:
  Length 1: "b", "a", "b", "a", "d"  (every single character)
  Length 2: none (no two adjacent equal characters)
  Length 3: "bab" (indices 0-2), "aba" (indices 1-3)
  Length 4: none
  Length 5: "babad" — NOT a palindrome ("b" != "d")

Longest: "bab" (length 3) or "aba" (length 3) — both valid

Output: "bab" (or "aba")
```

#### Example 2: `s = "cbbd"`

```text
All palindromic substrings:
  Length 1: "c", "b", "b", "d"
  Length 2: "bb" (indices 1-2)  ← s[1]='b' == s[2]='b'
  Length 3: "cbb" — NOT ("c" != "b"), "bbd" — NOT ("b" != "d")
  Length 4: "cbbd" — NOT ("c" != "d")

Longest: "bb" (length 2)

Output: "bb"
```

### 6. Key Observations

1. **Palindrome structure** — `s[i..j]` is a palindrome iff `s[i] == s[j]` AND `s[i+1..j-1]` is also a palindrome.
2. **Two types of palindromes** — Odd-length (single center: `"aba"`) and even-length (two centers: `"abba"`). Both must be handled.
3. **Center expansion** — Every palindrome has a center. Expanding outward from each possible center is efficient: O(n) centers × O(n) expansion = O(n^2) total.
4. **Multiple valid answers** — Since any maximum-length palindrome is accepted, the first one found is fine.
5. **DP recurrence** — `dp[i][j] = (s[i] == s[j]) AND dp[i+1][j-1]`, base cases: `dp[i][i] = true` and `dp[i][i+1] = (s[i] == s[i+1])`.

### 7. Pattern identification

| Pattern | Why it fits | Complexity |
|---|---|---|
| Brute Force | Check every substring | O(n^3) time, O(1) space |
| Dynamic Programming | Overlapping subproblems: palindrome check builds on shorter palindromes | O(n^2) time, O(n^2) space |
| Expand Around Center | Every palindrome has a center; expand to find its extent | O(n^2) time, O(1) space |
| Manacher's Algorithm | Advanced linear-time palindrome algorithm | O(n) time, O(n) space |

**Chosen pattern for solution files:** `Expand Around Center`
**Reason:** Same time complexity as DP but uses O(1) space. Simpler implementation than Manacher's. Best practical choice.

---

## Approach 1: Brute Force

### Thought process

> The simplest idea: check every possible substring and keep track of the longest palindrome found.
> A substring is identified by its start index `i` and end index `j`.
> There are O(n^2) substrings. Checking each one for palindrome takes O(n) → total O(n^3).

### Algorithm (step-by-step)

1. Set `result = s[0]` (a single character is always a palindrome)
2. Outer loop: `i = 0` to `n-1`
3. Inner loop: `j = i+1` to `n-1`
4. Check if `s[i..j]` is a palindrome using a two-pointer check
5. If it is and its length > current best → update result
6. Return `result`

### Pseudocode

```text
function longestPalindrome(s):
    result = s[0]
    n = len(s)

    for i = 0 to n-1:
        for j = i+1 to n-1:
            if isPalindrome(s, i, j) and j-i+1 > len(result):
                result = s[i..j]

    return result

function isPalindrome(s, l, r):
    while l < r:
        if s[l] != s[r]: return false
        l++; r--
    return true
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^3) | O(n^2) substrings × O(n) palindrome check each |
| **Space** | O(1) | No extra data structures (result pointer only) |

### Implementation

#### Go

```go
// longestPalindromeBrute — Brute Force
// Time: O(n³), Space: O(1)
func longestPalindromeBrute(s string) string {
    n := len(s)
    best := s[:1] // at minimum, first character

    isPalin := func(l, r int) bool {
        for l < r {
            if s[l] != s[r] {
                return false
            }
            l++
            r--
        }
        return true
    }

    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            if isPalin(i, j) && j-i+1 > len(best) {
                best = s[i : j+1]
            }
        }
    }
    return best
}
```

#### Java

```java
// Brute Force — Time: O(n³), Space: O(1)
public String longestPalindromeBrute(String s) {
    int n = s.length();
    String best = s.substring(0, 1);

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (isPalindrome(s, i, j) && j - i + 1 > best.length()) {
                best = s.substring(i, j + 1);
            }
        }
    }
    return best;
}

private boolean isPalindrome(String s, int l, int r) {
    while (l < r) {
        if (s.charAt(l) != s.charAt(r)) return false;
        l++; r--;
    }
    return true;
}
```

#### Python

```python
def longestPalindromeBrute(s: str) -> str:
    """Brute Force. Time: O(n³), Space: O(1)"""
    n = len(s)
    best = s[0]

    def is_palindrome(l, r):
        while l < r:
            if s[l] != s[r]:
                return False
            l += 1
            r -= 1
        return True

    for i in range(n):
        for j in range(i + 1, n):
            if is_palindrome(i, j) and j - i + 1 > len(best):
                best = s[i:j + 1]
    return best
```

### Dry Run

```text
Input: s = "babad"

i=0, j=1: "ba" — b≠a → not palindrome
i=0, j=2: "bab" — b==b, center a → PALINDROME (length 3)! best = "bab"
i=0, j=3: "baba" — b≠a → not palindrome
i=0, j=4: "babad" — b≠d → not palindrome
i=1, j=2: "ab" — a≠b → not palindrome
i=1, j=3: "aba" — a==a, center b → PALINDROME (length 3) — not longer than "bab"
i=1, j=4: "abad" — a≠d → not palindrome
i=2, j=3: "ba" — b≠a → not palindrome
i=2, j=4: "bad" — b≠d → not palindrome
i=3, j=4: "ad" — a≠d → not palindrome

Result: "bab"
```

---

## Approach 2: Dynamic Programming

### The problem with Brute Force

> O(n^3) is too slow — it rechecks inner substrings repeatedly.
> For example, to check `"babad"` is a palindrome, we check `"aba"` again.
> DP avoids this by storing results for subproblems.

### Optimization idea

> **DP recurrence:** `s[i..j]` is a palindrome if and only if:
> - `s[i] == s[j]` (outer characters match), AND
> - `s[i+1..j-1]` is also a palindrome (inner substring is a palindrome)
>
> **Base cases:**
> - Any single character `s[i..i]` is a palindrome → `dp[i][i] = true`
> - Two equal adjacent characters `s[i..i+1]` → `dp[i][i+1] = (s[i] == s[i+1])`
>
> Fill the DP table for increasing lengths (2, 3, 4, ..., n).

### Algorithm (step-by-step)

1. Create a 2D boolean array `dp[n][n]`, initialized to `false`
2. Set `dp[i][i] = true` for all i (single characters)
3. Check all pairs `(i, i+1)` for length-2 palindromes
4. For lengths `len = 3` to `n`: for each start `i`, set `j = i + len - 1`
   - `dp[i][j] = (s[i] == s[j]) && dp[i+1][j-1]`
   - If true and length > best → update best
5. Return the best substring

### Pseudocode

```text
function longestPalindrome(s):
    n = len(s)
    dp = 2D boolean array, all false
    bestStart = 0, bestLen = 1

    // Base case: single characters
    for i = 0 to n-1:
        dp[i][i] = true

    // Base case: length-2
    for i = 0 to n-2:
        if s[i] == s[i+1]:
            dp[i][i+1] = true
            bestStart = i
            bestLen = 2

    // Fill for lengths 3 to n
    for length = 3 to n:
        for i = 0 to n-length:
            j = i + length - 1
            if s[i] == s[j] and dp[i+1][j-1]:
                dp[i][j] = true
                if length > bestLen:
                    bestStart = i
                    bestLen = length

    return s[bestStart : bestStart + bestLen]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Fill n^2 cells of the DP table, each in O(1) |
| **Space** | O(n^2) | The DP table itself |

### Implementation

#### Go

```go
// longestPalindromeDP — Dynamic Programming
// Time: O(n²), Space: O(n²)
func longestPalindromeDP(s string) string {
    n := len(s)
    // dp[i][j] = true if s[i..j] is a palindrome
    dp := make([][]bool, n)
    for i := range dp {
        dp[i] = make([]bool, n)
        dp[i][i] = true // every single character is a palindrome
    }

    bestStart, bestLen := 0, 1

    // Length-2 substrings
    for i := 0; i < n-1; i++ {
        if s[i] == s[i+1] {
            dp[i][i+1] = true
            bestStart = i
            bestLen = 2
        }
    }

    // Lengths 3 to n
    for length := 3; length <= n; length++ {
        for i := 0; i <= n-length; i++ {
            j := i + length - 1
            if s[i] == s[j] && dp[i+1][j-1] {
                dp[i][j] = true
                if length > bestLen {
                    bestStart = i
                    bestLen = length
                }
            }
        }
    }

    return s[bestStart : bestStart+bestLen]
}
```

#### Java

```java
// Dynamic Programming — Time: O(n²), Space: O(n²)
public String longestPalindromeDP(String s) {
    int n = s.length();
    boolean[][] dp = new boolean[n][n];
    int bestStart = 0, bestLen = 1;

    // Base: single characters
    for (int i = 0; i < n; i++) dp[i][i] = true;

    // Base: length-2
    for (int i = 0; i < n - 1; i++) {
        if (s.charAt(i) == s.charAt(i + 1)) {
            dp[i][i + 1] = true;
            bestStart = i;
            bestLen = 2;
        }
    }

    // Lengths 3 to n
    for (int len = 3; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                dp[i][j] = true;
                if (len > bestLen) { bestStart = i; bestLen = len; }
            }
        }
    }

    return s.substring(bestStart, bestStart + bestLen);
}
```

#### Python

```python
def longestPalindromeDP(s: str) -> str:
    """Dynamic Programming. Time: O(n²), Space: O(n²)"""
    n = len(s)
    dp = [[False] * n for _ in range(n)]
    best_start, best_len = 0, 1

    # Base: single characters
    for i in range(n):
        dp[i][i] = True

    # Base: length-2
    for i in range(n - 1):
        if s[i] == s[i + 1]:
            dp[i][i + 1] = True
            best_start, best_len = i, 2

    # Lengths 3 to n
    for length in range(3, n + 1):
        for i in range(n - length + 1):
            j = i + length - 1
            if s[i] == s[j] and dp[i + 1][j - 1]:
                dp[i][j] = True
                if length > best_len:
                    best_start, best_len = i, length

    return s[best_start:best_start + best_len]
```

### Dry Run

```text
Input: s = "babad", n = 5

dp table (i = row, j = col):

Base (length 1): dp[0][0]=T, dp[1][1]=T, dp[2][2]=T, dp[3][3]=T, dp[4][4]=T
Base (length 2):
  i=0: s[0]='b' vs s[1]='a' → ≠ → dp[0][1]=F
  i=1: s[1]='a' vs s[2]='b' → ≠ → dp[1][2]=F
  i=2: s[2]='b' vs s[3]='a' → ≠ → dp[2][3]=F
  i=3: s[3]='a' vs s[4]='d' → ≠ → dp[3][4]=F

Length 3:
  i=0, j=2: s[0]='b'==s[2]='b' AND dp[1][1]=T → dp[0][2]=T  ✅ best="bab" (len=3)
  i=1, j=3: s[1]='a'==s[3]='a' AND dp[2][2]=T → dp[1][3]=T  ✅ same length
  i=2, j=4: s[2]='b'≠s[4]='d' → dp[2][4]=F

Length 4:
  i=0, j=3: s[0]='b'≠s[3]='a' → F
  i=1, j=4: s[1]='a'≠s[4]='d' → F

Length 5:
  i=0, j=4: s[0]='b'≠s[4]='d' → F

Result: bestStart=0, bestLen=3 → "bab"
```

---

## Approach 3: Optimal (Expand Around Center)

### The problem with DP

> DP requires O(n^2) space for the table.
> The expand-around-center insight achieves the same O(n^2) time but with O(1) space.

### Optimization idea

> **Key insight:** Every palindrome has a center.
> - Odd-length palindromes (`"aba"`) have a single character as center
> - Even-length palindromes (`"abba"`) have two equal characters as center
>
> There are `2n - 1` possible centers for a string of length `n`:
> - `n` single-character centers (for odd-length)
> - `n - 1` two-character centers (for even-length)
>
> For each center, expand outward while `s[l] == s[r]`.
> Track the start index and maximum length of the best palindrome found.

### Algorithm (step-by-step)

1. If `s` is empty, return `""`
2. Set `start = 0`, `maxLen = 1`
3. For each index `i` from `0` to `n-1`:
   - **Odd expansion:** call `expand(i, i)`
   - **Even expansion:** call `expand(i, i+1)`
4. `expand(l, r)`: while `l >= 0` AND `r < n` AND `s[l] == s[r]`
   - If `r - l + 1 > maxLen`, update `start = l`, `maxLen = r - l + 1`
   - `l--`, `r++`
5. Return `s[start : start + maxLen]`

### Pseudocode

```text
function longestPalindrome(s):
    start = 0, maxLen = 1

    function expand(l, r):
        while l >= 0 and r < n and s[l] == s[r]:
            if r - l + 1 > maxLen:
                start = l
                maxLen = r - l + 1
            l--; r++

    for i = 0 to n-1:
        expand(i, i)      // odd-length center
        expand(i, i+1)    // even-length center

    return s[start : start + maxLen]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | 2n-1 centers × up to n/2 expansions each |
| **Space** | O(1) | Only a few integer variables — no DP table |

### Implementation

#### Go

```go
// longestPalindrome — Expand Around Center
// Time: O(n²), Space: O(1)
func longestPalindrome(s string) string {
    if len(s) == 0 {
        return ""
    }

    start, maxLen := 0, 1

    expand := func(l, r int) {
        for l >= 0 && r < len(s) && s[l] == s[r] {
            if r-l+1 > maxLen {
                start = l
                maxLen = r - l + 1
            }
            l--
            r++
        }
    }

    for i := 0; i < len(s); i++ {
        expand(i, i)     // odd-length center
        expand(i, i+1)   // even-length center
    }

    return s[start : start+maxLen]
}
```

#### Java

```java
// Expand Around Center — Time: O(n²), Space: O(1)
public String longestPalindrome(String s) {
    if (s == null || s.length() == 0) return "";
    int start = 0, maxLen = 1;

    for (int i = 0; i < s.length(); i++) {
        int oddLen  = expand(s, i, i);
        int evenLen = expand(s, i, i + 1);
        int best = Math.max(oddLen, evenLen);
        if (best > maxLen) {
            maxLen = best;
            start = i - (best - 1) / 2;
        }
    }
    return s.substring(start, start + maxLen);
}

private int expand(String s, int l, int r) {
    while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
        l--; r++;
    }
    return r - l - 1; // length after the final failed step
}
```

#### Python

```python
def longestPalindrome(s: str) -> str:
    """Expand Around Center. Time: O(n²), Space: O(1)"""
    if not s:
        return ""
    start, max_len = 0, 1

    def expand(l: int, r: int) -> None:
        nonlocal start, max_len
        while l >= 0 and r < len(s) and s[l] == s[r]:
            if r - l + 1 > max_len:
                start = l
                max_len = r - l + 1
            l -= 1
            r += 1

    for i in range(len(s)):
        expand(i, i)      # odd-length center
        expand(i, i + 1)  # even-length center

    return s[start:start + max_len]
```

### Dry Run

```text
Input: s = "babad", n = 5
Initial: start=0, maxLen=1

--- i=0 (s[0]='b') ---
Odd  expand(0,0):
  l=0, r=0: s[0]='b'==s[0]='b' → len=1, not > 1 → l=-1, r=1 → stop
Even expand(0,1):
  l=0, r=1: s[0]='b' ≠ s[1]='a' → stop immediately

--- i=1 (s[1]='a') ---
Odd  expand(1,1):
  l=1, r=1: s[1]='a'==s[1]='a' → len=1, not > 1 → l=0, r=2
  l=0, r=2: s[0]='b'==s[2]='b' → len=3 > 1 ✅ start=0, maxLen=3 → l=-1, r=3 → stop
Even expand(1,2):
  l=1, r=2: s[1]='a' ≠ s[2]='b' → stop immediately

--- i=2 (s[2]='b') ---
Odd  expand(2,2):
  l=2, r=2: s[2]='b'==s[2]='b' → len=1, not > 3 → l=1, r=3
  l=1, r=3: s[1]='a'==s[3]='a' → len=3, not > 3 → l=0, r=4
  l=0, r=4: s[0]='b' ≠ s[4]='d' → stop
Even expand(2,3):
  l=2, r=3: s[2]='b' ≠ s[3]='a' → stop immediately

--- i=3, i=4: No improvement (palindromes found are length 1 or 2) ---

Final: start=0, maxLen=3 → s[0:3] = "bab"
```

```text
Input: s = "cbbd"

--- i=0: expand(0,0) → len=1. expand(0,1): 'c'≠'b' → stop
--- i=1: expand(1,1) → l=0,r=2: 'c'≠'b' → len=1.
         expand(1,2): s[1]='b'==s[2]='b' → len=2 > 1 ✅ start=1, maxLen=2
                      l=0, r=3: s[0]='c'≠s[3]='d' → stop
--- i=2: expand(2,2) → l=1,r=3: 'b'≠'d' → len=1.
         expand(2,3): 'b'≠'d' → stop
--- i=3: expand(3,3) → len=1. expand(3,4): r out of bounds → stop

Final: start=1, maxLen=2 → s[1:3] = "bb"
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^3) | O(1) | Simplest, easiest to understand | Too slow for n=1000 |
| 2 | Dynamic Programming | O(n^2) | O(n^2) | Clear subproblem structure, easy to debug | High memory usage |
| 3 | Expand Around Center | O(n^2) | O(1) | Same time as DP, optimal space | Requires tracking two center types |

### Which solution to choose?

- **In an interview:** Approach 3 (Expand Around Center) — best time/space tradeoff, shows understanding
- **For learning:** Approach 2 (DP) first — the recurrence is instructive; then switch to Approach 3
- **On LeetCode:** Approach 3 — O(1) space is the preferred optimal solution
- **Note:** Manacher's Algorithm achieves O(n) time, but is rarely expected in interviews

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single character | `s = "a"` | `"a"` | A single char is always a palindrome |
| 2 | All same characters | `s = "aaaa"` | `"aaaa"` | The whole string is a palindrome |
| 3 | No palindrome > length 1 | `s = "abcd"` | any of `"a","b","c","d"` | All characters distinct, no pair matches |
| 4 | Whole string is palindrome | `s = "racecar"` | `"racecar"` | Odd-length full palindrome |
| 5 | Even-length palindrome | `s = "abccba"` | `"abccba"` | Even-length full palindrome |
| 6 | Multiple answers same length | `s = "babad"` | `"bab"` or `"aba"` | Two palindromes of length 3 |
| 7 | Palindrome at the end | `s = "xyzabba"` | `"abba"` | Even palindrome at tail |
| 8 | Two-char string | `s = "ab"` | `"a"` or `"b"` | No two-character palindrome |

---

## Common Mistakes

### Mistake 1: Forgetting to handle even-length palindromes

```python
# ❌ WRONG — only expanding odd-length centers misses "bb", "abba", etc.
for i in range(len(s)):
    expand(i, i)   # odd only

# ✅ CORRECT — expand both odd and even centers
for i in range(len(s)):
    expand(i, i)       # odd-length center
    expand(i, i + 1)   # even-length center
```

### Mistake 2: Wrong index recovery after expansion (Java style)

```java
// ❌ WRONG — returning r-l-1 from expand, then computing start incorrectly
int len = expand(s, i, i);  // returns length
start = i - len / 2;        // off by one for even lengths

// ✅ CORRECT — use (best - 1) / 2 to recover start
start = i - (best - 1) / 2;
// For odd best=3: start = i - 1  ✅
// For even best=2: start = i - 0 = i  ✅
```

### Mistake 3: Using `s.reverse() == s` check (O(n) per substring)

```python
# ❌ SLOW — creating reversed substrings for every pair is O(n³) total
for i in range(n):
    for j in range(i, n):
        if s[i:j+1] == s[i:j+1][::-1]:  # O(n) operation
            ...

# ✅ CORRECT — use two-pointer check or expand-around-center
```

### Mistake 4: DP table filled in wrong order

```python
# ❌ WRONG — filling by rows first means dp[i+1][j-1] hasn't been computed yet
for i in range(n):
    for j in range(i, n):
        dp[i][j] = (s[i] == s[j]) and dp[i+1][j-1]

# ✅ CORRECT — fill by increasing substring length
for length in range(3, n + 1):
    for i in range(n - length + 1):
        j = i + length - 1
        dp[i][j] = (s[i] == s[j]) and dp[i+1][j-1]
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [647. Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | 🟡 Medium | Count all palindromic substrings — same expand technique |
| 2 | [9. Palindrome Number](https://leetcode.com/problems/palindrome-number/) | 🟢 Easy | Check if a number is a palindrome |
| 3 | [125. Valid Palindrome](https://leetcode.com/problems/valid-palindrome/) | 🟢 Easy | Check if a string is a palindrome (ignoring non-alphanum) |
| 4 | [516. Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) | 🟡 Medium | Subsequence (not substring) — DP |
| 5 | [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/) | 🔴 Hard | Add characters to make a palindrome — KMP/Manacher |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — highlights each pair (i, j), shows the two-pointer palindrome check
> - **DP** tab — fills the DP table cell-by-cell, color-coding palindromic cells
> - **Expand Around Center** tab — shows the center expanding outward, highlighting matched characters
> - **Compare All** tab — operation counts for each approach on the same input
