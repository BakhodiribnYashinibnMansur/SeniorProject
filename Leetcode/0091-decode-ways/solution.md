# 0091. Decode Ways

## Problem

| | |
|---|---|
| **Leetcode** | [91. Decode Ways](https://leetcode.com/problems/decode-ways/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `String`, `Dynamic Programming` |

> A message containing letters from `A-Z` can be encoded into numbers using the following mapping:
>
> ```
> 'A' -> "1"
> 'B' -> "2"
> ...
> 'Z' -> "26"
> ```
>
> To **decode** an encoded message, all the digits must be grouped then mapped back into letters using the reverse of the mapping above (there may be multiple ways).
>
> Given a string `s` containing only digits, return *the **number** of ways to decode it*.
>
> The test cases are generated so that the answer fits in a **32-bit** integer.

### Examples

```
Input: s = "12"
Output: 2  ("AB" or "L")

Input: s = "226"
Output: 3  ("BZ", "VF", "BBF")

Input: s = "06"
Output: 0
```

### Constraints

- `1 <= s.length <= 100`
- `s` contains only digits and may contain leading zero(s).

---

## Approach: Dynamic Programming (O(1) Space)

### Recurrence

Let `dp[i]` = ways to decode `s[:i]`.
- Base: `dp[0] = 1` (empty string has 1 decoding).
- Single-digit step: if `s[i-1] != '0'`, `dp[i] += dp[i-1]`.
- Two-digit step: if `s[i-2..i-1]` is `10..26`, `dp[i] += dp[i-2]`.

Use rolling variables `prev2 = dp[i-2]`, `prev1 = dp[i-1]`.

### Complexity

- Time: O(n)
- Space: O(1)

### Implementation

#### Go

```go
func numDecodings(s string) int {
    n := len(s)
    if n == 0 || s[0] == '0' {
        return 0
    }
    prev2, prev1 := 1, 1
    for i := 2; i <= n; i++ {
        cur := 0
        if s[i-1] != '0' {
            cur += prev1
        }
        two := (int(s[i-2]-'0'))*10 + int(s[i-1]-'0')
        if two >= 10 && two <= 26 {
            cur += prev2
        }
        prev2, prev1 = prev1, cur
    }
    return prev1
}
```

#### Java

```java
class Solution {
    public int numDecodings(String s) {
        int n = s.length();
        if (n == 0 || s.charAt(0) == '0') return 0;
        int prev2 = 1, prev1 = 1;
        for (int i = 2; i <= n; i++) {
            int cur = 0;
            if (s.charAt(i - 1) != '0') cur += prev1;
            int two = (s.charAt(i - 2) - '0') * 10 + (s.charAt(i - 1) - '0');
            if (two >= 10 && two <= 26) cur += prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }
}
```

#### Python

```python
class Solution:
    def numDecodings(self, s: str) -> int:
        n = len(s)
        if n == 0 or s[0] == '0': return 0
        prev2, prev1 = 1, 1
        for i in range(2, n + 1):
            cur = 0
            if s[i - 1] != '0': cur += prev1
            two = int(s[i - 2:i])
            if 10 <= two <= 26: cur += prev2
            prev2, prev1 = prev1, cur
        return prev1
```

---

## Edge Cases

- Empty: 0
- Leading zero: 0
- "0": 0
- "10", "20": 1 each
- "30": 0
- "27": 1 (only single-digit)
- "100": 0 (00 is invalid)
- Long string of "1"s: Fibonacci-like growth

---

## Visual Animation

> [animation.html](./animation.html)
