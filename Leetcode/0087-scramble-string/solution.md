# 0087. Scramble String

## Problem

| | |
|---|---|
| **Leetcode** | [87. Scramble String](https://leetcode.com/problems/scramble-string/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `String`, `Dynamic Programming` |

> We can scramble a string `s` to get a string `t` using the following algorithm:
>
> 1. If the length of the string is 1, stop.
> 2. If the length of the string is `> 1`, do the following:
>    - Split the string into two non-empty substrings at a random index, i.e., if the string is `s`, divide it to `x` and `y` where `s = x + y`.
>    - **Randomly** decide to swap the two substrings or to keep them in the same order. i.e., after this step, `s` may become `s = x + y` or `s = y + x`.
>    - Apply step 1 recursively on each of the two substrings `x` and `y`.
>
> Given two strings `s1` and `s2` of **the same length**, return `true` if `s2` is a scrambled string of `s1`, otherwise, return `false`.

### Examples

```
Input: s1 = "great", s2 = "rgeat"
Output: true

Input: s1 = "abcde", s2 = "caebd"
Output: false

Input: s1 = "a", s2 = "a"
Output: true
```

### Constraints

- `s1.length == s2.length`
- `1 <= s1.length <= 30`
- `s1` and `s2` consist of lowercase English letters.

---

## Approach: Recursive DP with Memoization

### Idea

For each split point `i` in `s1`:
- "No swap" case: `s1[:i]` scrambles to `s2[:i]` AND `s1[i:]` scrambles to `s2[i:]`.
- "Swap" case: `s1[:i]` scrambles to `s2[n-i:]` AND `s1[i:]` scrambles to `s2[:n-i]`.

Pruning: if `s1` and `s2` have different character counts, return false.

### Complexity

- Time: O(n^4) with memoization
- Space: O(n^3)

### Implementation

#### Python

```python
class Solution:
    def isScramble(self, s1: str, s2: str) -> bool:
        from functools import lru_cache
        @lru_cache(maxsize=None)
        def solve(a: str, b: str) -> bool:
            if a == b: return True
            if sorted(a) != sorted(b): return False
            n = len(a)
            for i in range(1, n):
                if (solve(a[:i], b[:i]) and solve(a[i:], b[i:])) or \
                   (solve(a[:i], b[n - i:]) and solve(a[i:], b[:n - i])):
                    return True
            return False
        return solve(s1, s2)
```

#### Go

```go
type pair struct{ a, b string }

func isScramble(s1, s2 string) bool {
    memo := make(map[pair]bool)
    var solve func(a, b string) bool
    solve = func(a, b string) bool {
        if a == b {
            return true
        }
        if v, ok := memo[pair{a, b}]; ok {
            return v
        }
        // anagram check
        var c [26]int
        for i := 0; i < len(a); i++ {
            c[a[i]-'a']++
            c[b[i]-'a']--
        }
        for _, v := range c {
            if v != 0 {
                memo[pair{a, b}] = false
                return false
            }
        }
        n := len(a)
        for i := 1; i < n; i++ {
            if (solve(a[:i], b[:i]) && solve(a[i:], b[i:])) ||
                (solve(a[:i], b[n-i:]) && solve(a[i:], b[:n-i])) {
                memo[pair{a, b}] = true
                return true
            }
        }
        memo[pair{a, b}] = false
        return false
    }
    return solve(s1, s2)
}
```

#### Java

```java
class Solution {
    private Map<String, Boolean> memo = new HashMap<>();

    public boolean isScramble(String s1, String s2) {
        return solve(s1, s2);
    }

    private boolean solve(String a, String b) {
        if (a.equals(b)) return true;
        String key = a + "#" + b;
        if (memo.containsKey(key)) return memo.get(key);
        int[] c = new int[26];
        for (int i = 0; i < a.length(); i++) {
            c[a.charAt(i) - 'a']++;
            c[b.charAt(i) - 'a']--;
        }
        for (int v : c) if (v != 0) {
            memo.put(key, false); return false;
        }
        int n = a.length();
        for (int i = 1; i < n; i++) {
            if ((solve(a.substring(0, i), b.substring(0, i)) &&
                 solve(a.substring(i), b.substring(i))) ||
                (solve(a.substring(0, i), b.substring(n - i)) &&
                 solve(a.substring(i), b.substring(0, n - i)))) {
                memo.put(key, true);
                return true;
            }
        }
        memo.put(key, false);
        return false;
    }
}
```

---

## Visual Animation

> [animation.html](./animation.html)
