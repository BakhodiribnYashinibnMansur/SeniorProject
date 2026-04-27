# 0093. Restore IP Addresses

## Problem

| | |
|---|---|
| **Leetcode** | [93. Restore IP Addresses](https://leetcode.com/problems/restore-ip-addresses/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `String`, `Backtracking` |

> A **valid IP address** consists of exactly four integers separated by single dots. Each integer is between `0` and `255` (**inclusive**) and cannot have leading zeros.
>
> Given a string `s` containing only digits, return *all possible valid IP addresses that can be formed by inserting dots into* `s`.

### Examples

```
Input: s = "25525511135"
Output: ["255.255.11.135", "255.255.111.35"]

Input: s = "0000"
Output: ["0.0.0.0"]

Input: s = "101023"
Output: ["1.0.10.23","1.0.102.3","10.1.0.23","10.10.2.3","101.0.2.3"]
```

### Constraints

- `1 <= s.length <= 20`
- `s` consists of digits only.

---

## Approach: Backtracking with Length Pruning

### Idea

Try each split: at each step pick 1, 2, or 3 chars as the next octet. Validate (no leading zeros unless the segment is exactly "0", value ≤ 255). Recurse. Stop when 4 segments are placed and the index reached the end.

### Pruning

- If `4 - segments_chosen` segments remain and `(20-i) > 3 * remaining` or `(20-i) < remaining`, no valid split possible.

### Complexity

- Time: O(1) — at most `3^4 = 81` splits to try
- Space: O(1) extra

### Implementation

#### Go

```go
import "strconv"

func restoreIpAddresses(s string) []string {
    result := []string{}
    var bt func(start, segs int, parts []string)
    bt = func(start, segs int, parts []string) {
        if segs == 4 {
            if start == len(s) {
                result = append(result, strings.Join(parts, "."))
            }
            return
        }
        remaining := len(s) - start
        need := 4 - segs
        if remaining < need || remaining > need*3 {
            return
        }
        for length := 1; length <= 3 && start+length <= len(s); length++ {
            seg := s[start : start+length]
            if (len(seg) > 1 && seg[0] == '0') {
                continue
            }
            n, _ := strconv.Atoi(seg)
            if n > 255 {
                continue
            }
            bt(start+length, segs+1, append(parts, seg))
        }
    }
    bt(0, 0, []string{})
    return result
}
```

(needs `import "strings"`)

#### Java

```java
class Solution {
    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        bt(s, 0, 0, new ArrayList<>(), result);
        return result;
    }
    private void bt(String s, int start, int segs, List<String> parts, List<String> result) {
        if (segs == 4) {
            if (start == s.length()) result.add(String.join(".", parts));
            return;
        }
        int remaining = s.length() - start, need = 4 - segs;
        if (remaining < need || remaining > need * 3) return;
        for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
            String seg = s.substring(start, start + len);
            if (seg.length() > 1 && seg.charAt(0) == '0') continue;
            int n = Integer.parseInt(seg);
            if (n > 255) continue;
            parts.add(seg);
            bt(s, start + len, segs + 1, parts, result);
            parts.remove(parts.size() - 1);
        }
    }
}
```

#### Python

```python
class Solution:
    def restoreIpAddresses(self, s: str) -> List[str]:
        result = []
        def bt(start: int, parts: List[str]):
            if len(parts) == 4:
                if start == len(s): result.append('.'.join(parts))
                return
            remaining = len(s) - start
            need = 4 - len(parts)
            if remaining < need or remaining > need * 3: return
            for length in range(1, 4):
                if start + length > len(s): break
                seg = s[start:start + length]
                if (len(seg) > 1 and seg[0] == '0') or int(seg) > 255: continue
                bt(start + length, parts + [seg])
        bt(0, [])
        return result
```

---

## Edge Cases

- All zeros: "0000" → "0.0.0.0"
- Too short (< 4): no answer
- Too long (> 12): no answer
- Leading zero in a segment: skip

---

## Visual Animation

> [animation.html](./animation.html)
