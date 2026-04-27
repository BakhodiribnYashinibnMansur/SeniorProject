# 0072. Edit Distance

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Top-Down DP (Memoization)](#approach-1-top-down-dp-memoization)
4. [Approach 2: Bottom-Up DP (2D)](#approach-2-bottom-up-dp-2d)
5. [Approach 3: Bottom-Up DP (1D, Space Optimized)](#approach-3-bottom-up-dp-1d-space-optimized)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [72. Edit Distance](https://leetcode.com/problems/edit-distance/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `String`, `Dynamic Programming` |

### Description

> Given two strings `word1` and `word2`, return *the minimum number of operations required to convert `word1` to `word2`*.
>
> You have the following three operations permitted on a word:
>
> - Insert a character
> - Delete a character
> - Replace a character

### Examples

```
Example 1:
Input: word1 = "horse", word2 = "ros"
Output: 3
Explanation: horse -> rorse (replace 'h' with 'r') -> rose (remove 'r') -> ros (remove 'e').

Example 2:
Input: word1 = "intention", word2 = "execution"
Output: 5
```

### Constraints

- `0 <= word1.length, word2.length <= 500`
- `word1` and `word2` consist of lowercase English letters.

---

## Problem Breakdown

### 1. What is being asked?

Compute the **Levenshtein distance** between two strings: the minimum number of single-character edits (insertions, deletions, substitutions) needed to transform one into the other.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `word1`, `word2` | `str` | Lowercase strings, length up to 500 |

### 3. What is the output?

A single integer: the minimum number of edits.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| Lengths up to 500 | O(m*n) = 250,000 cells — fine |
| Lowercase only | No special character handling |

### 5. Step-by-step example analysis

#### Example 1: `word1 = "horse", word2 = "ros"`

```text
DP table (dp[i][j] = edit distance between word1[:i] and word2[:j]):

         ""  r  o  s
    ""    0  1  2  3
    h     1  1  2  3
    o     2  2  1  2
    r     3  2  2  2
    s     4  3  3  2
    e     5  4  4  3

Answer: dp[5][3] = 3.

Operations: replace 'h' → 'r', delete 'r' (or keep 'rose', delete 'e').
```

### 6. Key Observations

1. **Three sub-problems** -- At `(i, j)`:
   - If `word1[i-1] == word2[j-1]`: `dp[i][j] = dp[i-1][j-1]` (no edit).
   - Else: `dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])`
     - `dp[i-1][j]`: delete from `word1`
     - `dp[i][j-1]`: insert from `word2`
     - `dp[i-1][j-1]`: replace
2. **Base cases** -- Empty string requires `len(other)` insertions.
3. **1D rolling array** -- Only need previous row.

### 7. Pattern identification

Classic 2D DP on two-string problems.

**Chosen pattern:** `Bottom-Up DP (2D)` for clarity, `1D` for space.

---

## Approach 1: Top-Down DP (Memoization)

### Idea

> Recursive definition with memoization. `solve(i, j)` = edits to transform `word1[i:]` to `word2[j:]` (or `word1[:i]` to `word2[:j]` -- choice of indexing).

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Python

```python
class Solution:
    def minDistanceMemo(self, word1: str, word2: str) -> int:
        from functools import lru_cache
        @lru_cache(maxsize=None)
        def f(i: int, j: int) -> int:
            if i == 0: return j
            if j == 0: return i
            if word1[i - 1] == word2[j - 1]:
                return f(i - 1, j - 1)
            return 1 + min(f(i - 1, j), f(i, j - 1), f(i - 1, j - 1))
        return f(len(word1), len(word2))
```

---

## Approach 2: Bottom-Up DP (2D)

### Algorithm (step-by-step)

1. `m = len(word1), n = len(word2)`. Allocate `dp[m+1][n+1]`.
2. `dp[i][0] = i`, `dp[0][j] = j`.
3. For `i = 1..m`, `j = 1..n`:
   - If `word1[i-1] == word2[j-1]`: `dp[i][j] = dp[i-1][j-1]`.
   - Else: `dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])`.
4. Return `dp[m][n]`.

### Pseudocode

```text
m, n = len(word1), len(word2)
dp = (m+1) x (n+1) array
for i in 0..m: dp[i][0] = i
for j in 0..n: dp[0][j] = j
for i in 1..m:
    for j in 1..n:
        if word1[i-1] == word2[j-1]:
            dp[i][j] = dp[i-1][j-1]
        else:
            dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
return dp[m][n]
```

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(m * n) |

### Implementation

#### Go

```go
func minDistance2D(word1 string, word2 string) int {
    m, n := len(word1), len(word2)
    dp := make([][]int, m+1)
    for i := range dp {
        dp[i] = make([]int, n+1)
        dp[i][0] = i
    }
    for j := 0; j <= n; j++ {
        dp[0][j] = j
    }
    for i := 1; i <= m; i++ {
        for j := 1; j <= n; j++ {
            if word1[i-1] == word2[j-1] {
                dp[i][j] = dp[i-1][j-1]
            } else {
                a, b, c := dp[i-1][j], dp[i][j-1], dp[i-1][j-1]
                m := a
                if b < m {
                    m = b
                }
                if c < m {
                    m = c
                }
                dp[i][j] = 1 + m
            }
        }
    }
    return dp[m][n]
}
```

#### Java

```java
class Solution {
    public int minDistance2D(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }
}
```

#### Python

```python
class Solution:
    def minDistance2D(self, word1: str, word2: str) -> int:
        m, n = len(word1), len(word2)
        dp = [[0] * (n + 1) for _ in range(m + 1)]
        for i in range(m + 1): dp[i][0] = i
        for j in range(n + 1): dp[0][j] = j
        for i in range(1, m + 1):
            for j in range(1, n + 1):
                if word1[i - 1] == word2[j - 1]:
                    dp[i][j] = dp[i - 1][j - 1]
                else:
                    dp[i][j] = 1 + min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
        return dp[m][n]
```

---

## Approach 3: Bottom-Up DP (1D, Space Optimized)

### Idea

> Only need the previous row. Track `prevDiag` to remember `dp[i-1][j-1]` before it is overwritten.

### Algorithm

1. `dp = [0..n]` (initial row).
2. For `i = 1..m`:
   - `prevDiag = dp[0]`.
   - `dp[0] = i`.
   - For `j = 1..n`:
     - `temp = dp[j]`.
     - If chars match: `dp[j] = prevDiag`.
     - Else: `dp[j] = 1 + min(dp[j], dp[j-1], prevDiag)`.
     - `prevDiag = temp`.
3. Return `dp[n]`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(m * n) |
| **Space** | O(n) |

### Implementation

#### Go

```go
func minDistance(word1 string, word2 string) int {
    m, n := len(word1), len(word2)
    dp := make([]int, n+1)
    for j := 0; j <= n; j++ {
        dp[j] = j
    }
    for i := 1; i <= m; i++ {
        prevDiag := dp[0]
        dp[0] = i
        for j := 1; j <= n; j++ {
            temp := dp[j]
            if word1[i-1] == word2[j-1] {
                dp[j] = prevDiag
            } else {
                m := dp[j]
                if dp[j-1] < m {
                    m = dp[j-1]
                }
                if prevDiag < m {
                    m = prevDiag
                }
                dp[j] = 1 + m
            }
            prevDiag = temp
        }
    }
    return dp[n]
}
```

#### Java

```java
class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[] dp = new int[n + 1];
        for (int j = 0; j <= n; j++) dp[j] = j;
        for (int i = 1; i <= m; i++) {
            int prevDiag = dp[0];
            dp[0] = i;
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[j] = prevDiag;
                } else {
                    dp[j] = 1 + Math.min(prevDiag, Math.min(dp[j], dp[j - 1]));
                }
                prevDiag = temp;
            }
        }
        return dp[n];
    }
}
```

#### Python

```python
class Solution:
    def minDistance(self, word1: str, word2: str) -> int:
        m, n = len(word1), len(word2)
        dp = list(range(n + 1))
        for i in range(1, m + 1):
            prev_diag = dp[0]
            dp[0] = i
            for j in range(1, n + 1):
                temp = dp[j]
                if word1[i - 1] == word2[j - 1]:
                    dp[j] = prev_diag
                else:
                    dp[j] = 1 + min(dp[j], dp[j - 1], prev_diag)
                prev_diag = temp
        return dp[n]
```

### Dry Run

```text
word1 = "horse", word2 = "ros"
dp = [0, 1, 2, 3]   # j-axis

i=1 (h):
  prev_diag=0, dp[0]=1
  j=1: temp=1, 'h'!='r' → dp[1]=1+min(1,1,0)=1, prev_diag=1
  j=2: temp=2, 'h'!='o' → dp[2]=1+min(2,1,1)=2, prev_diag=2
  j=3: temp=3, 'h'!='s' → dp[3]=1+min(3,2,2)=3, prev_diag=3
  dp = [1, 1, 2, 3]

i=2 (o):
  prev_diag=1, dp[0]=2
  j=1: 'o'!='r' → dp[1]=1+min(1,2,1)=2, prev_diag=1
  j=2: 'o'=='o' → dp[2]=prev_diag=1, prev_diag=2
  j=3: 'o'!='s' → dp[3]=1+min(3,1,2)=2, prev_diag=3
  dp = [2, 2, 1, 2]

... (continue) ...

Final dp[3] = 3.
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Top-Down DP | O(mn) | O(mn) | Easy to derive | Recursion |
| 2 | Bottom-Up 2D | O(mn) | O(mn) | Iterative | 2D array |
| 3 | Bottom-Up 1D | O(mn) | O(n) | Optimal space | More care needed |

### Which solution to choose?

- **In an interview:** Approach 2 -- shows the recurrence clearly
- **In production:** Approach 3 for memory; Approach 2 for clarity
- **On Leetcode:** Either passes

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Both empty | `"", ""` | `0` | No edits |
| 2 | One empty | `"abc", ""` | `3` | Delete all |
| 3 | Other empty | `"", "abc"` | `3` | Insert all |
| 4 | Identical | `"abc", "abc"` | `0` | No edits |
| 5 | All replace | `"abc", "xyz"` | `3` | Replace each |
| 6 | Standard | `"horse", "ros"` | `3` | Example 1 |
| 7 | Larger | `"intention", "execution"` | `5` | Example 2 |

---

## Common Mistakes

### Mistake 1: Wrong DP base case

```python
# WRONG — dp[i][0] = 0 instead of i
for i in range(m + 1): dp[i][0] = 0

# CORRECT
for i in range(m + 1): dp[i][0] = i
```

**Reason:** Empty `word2` means we must delete all `i` characters of `word1`.

### Mistake 2: Forgetting one of three operations

```python
# WRONG — only considers replace, not insert/delete
dp[i][j] = 1 + dp[i - 1][j - 1]

# CORRECT — all three predecessors
dp[i][j] = 1 + min(dp[i - 1][j],     # delete
                   dp[i][j - 1],      # insert
                   dp[i - 1][j - 1])  # replace
```

**Reason:** Any of the three operations can yield the minimum; we must consider all.

### Mistake 3: Off-by-one in 1D update

```python
# WRONG — overwrites dp[j] before saving for prev_diag of next column
prev_diag = dp[j]
dp[j] = ...
# next iteration reads stale prev_diag

# CORRECT — save first, then update
temp = dp[j]
dp[j] = ...
prev_diag = temp
```

**Reason:** The 1D version requires careful sequencing because `dp[j]` is shared between the previous and current rows.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [583. Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) | :yellow_circle: Medium | Only delete operations |
| 2 | [1143. Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | :yellow_circle: Medium | Same DP shape |
| 3 | [161. One Edit Distance](https://leetcode.com/problems/one-edit-distance/) | :yellow_circle: Medium | Boolean version |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - DP grid that fills left-to-right, top-to-bottom
> - Highlight current cell + three predecessor cells
> - Operation labels (insert / delete / replace) per step
> - Trace optimal edit path
