# 0097. Interleaving String

## Problem

| | |
|---|---|
| **Leetcode** | [97. Interleaving String](https://leetcode.com/problems/interleaving-string/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `String`, `Dynamic Programming` |

> Given strings `s1`, `s2`, and `s3`, find whether `s3` is formed by an **interleaving** of `s1` and `s2`.

### Examples

```
Input: s1 = "aabcc", s2 = "dbbca", s3 = "aadbbcbcac"
Output: true

Input: s1 = "aabcc", s2 = "dbbca", s3 = "aadbbbaccc"
Output: false

Input: s1 = "", s2 = "", s3 = ""
Output: true
```

### Constraints

- `0 <= s1.length, s2.length <= 100`
- `0 <= s3.length <= 200`
- All consist of lowercase letters.

---

## Approach: 2D DP

Let `dp[i][j]` = whether `s3[:i+j]` is an interleaving of `s1[:i]` and `s2[:j]`.

Recurrence:
`dp[i][j] = (dp[i-1][j] && s1[i-1] == s3[i+j-1]) || (dp[i][j-1] && s2[j-1] == s3[i+j-1])`

### Complexity

- Time: O(m * n)
- Space: O(n) with rolling array

### Implementation

#### Go

```go
func isInterleave(s1, s2, s3 string) bool {
    m, n := len(s1), len(s2)
    if m+n != len(s3) {
        return false
    }
    dp := make([]bool, n+1)
    dp[0] = true
    for j := 1; j <= n; j++ {
        dp[j] = dp[j-1] && s2[j-1] == s3[j-1]
    }
    for i := 1; i <= m; i++ {
        dp[0] = dp[0] && s1[i-1] == s3[i-1]
        for j := 1; j <= n; j++ {
            dp[j] = (dp[j] && s1[i-1] == s3[i+j-1]) ||
                (dp[j-1] && s2[j-1] == s3[i+j-1])
        }
    }
    return dp[n]
}
```

#### Java

```java
class Solution {
    public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length(), n = s2.length();
        if (m + n != s3.length()) return false;
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int j = 1; j <= n; j++) dp[j] = dp[j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
        for (int i = 1; i <= m; i++) {
            dp[0] = dp[0] && s1.charAt(i - 1) == s3.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                dp[j] = (dp[j] && s1.charAt(i - 1) == s3.charAt(i + j - 1)) ||
                        (dp[j - 1] && s2.charAt(j - 1) == s3.charAt(i + j - 1));
            }
        }
        return dp[n];
    }
}
```

#### Python

```python
class Solution:
    def isInterleave(self, s1: str, s2: str, s3: str) -> bool:
        m, n = len(s1), len(s2)
        if m + n != len(s3): return False
        dp = [False] * (n + 1)
        dp[0] = True
        for j in range(1, n + 1):
            dp[j] = dp[j - 1] and s2[j - 1] == s3[j - 1]
        for i in range(1, m + 1):
            dp[0] = dp[0] and s1[i - 1] == s3[i - 1]
            for j in range(1, n + 1):
                dp[j] = (dp[j] and s1[i - 1] == s3[i + j - 1]) or \
                        (dp[j - 1] and s2[j - 1] == s3[i + j - 1])
        return dp[n]
```

---

## Visual Animation

> [animation.html](./animation.html)
