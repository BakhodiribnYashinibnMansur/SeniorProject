# 0071. Simplify Path

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Split + Stack](#approach-1-split--stack)
4. [Approach 2: Manual Single-Pass Parser](#approach-2-manual-single-pass-parser)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [71. Simplify Path](https://leetcode.com/problems/simplify-path/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `String`, `Stack` |

### Description

> Given an absolute path for a Unix-style file system, which begins with a slash `'/'`, transform this path into the simplified canonical path.
>
> The canonical path follows these rules:
>
> 1. Always begins with `'/'`.
> 2. Two directories are separated by a single slash `'/'`.
> 3. Does not end with a trailing `'/'` (unless the path is the root `'/'`).
> 4. Excludes any periods `'.'` (current directory) or double periods `'..'` (parent directory).
>
> Return the simplified canonical path.

### Examples

```
Example 1:
Input: path = "/home/"
Output: "/home"

Example 2:
Input: path = "/../"
Output: "/"

Example 3:
Input: path = "/home//foo/"
Output: "/home/foo"

Example 4:
Input: path = "/a/./b/../../c/"
Output: "/c"
```

### Constraints

- `1 <= path.length <= 3000`
- `path` consists of English letters, digits, period `'.'`, slash `'/'`, or `'_'`.
- `path` is a valid absolute Unix path.

---

## Problem Breakdown

### 1. What is being asked?

Resolve a Unix path string by collapsing `.` (current dir), `..` (parent dir), and consecutive `/` (no-op), and produce the canonical absolute path.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `path` | `str` | Unix-style absolute path |

### 3. What is the output?

The canonical absolute path string.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `length <= 3000` | O(n) is fine |
| Only letters, digits, `.`, `/`, `_` | No special character handling beyond `.` and `/` |

### 5. Step-by-step example analysis

#### Example 4: `"/a/./b/../../c/"`

```text
Split by '/': ["", "a", ".", "b", "..", "..", "c", ""]
Filter and apply:
  ""    → skip (empty token between slashes)
  "a"   → push → stack = ["a"]
  "."   → skip (current dir)
  "b"   → push → stack = ["a", "b"]
  ".."  → pop → stack = ["a"]
  ".."  → pop → stack = []
  "c"   → push → stack = ["c"]
  ""    → skip

Join: "/" + "c" = "/c".
```

### 6. Key Observations

1. **Stack of directory names** -- Push real names, pop on `..`, ignore `.` and empty tokens.
2. **Edge: `..` from root** -- Stack is already empty, so popping is a no-op. The path stays at root.
3. **Trailing slash** -- Drop, except for the root path `/`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Stack | LIFO matches parent traversal |
| String split | Simple to enumerate tokens |

**Chosen pattern:** `Split + Stack`.

---

## Approach 1: Split + Stack

### Algorithm (step-by-step)

1. Split `path` by `'/'`.
2. Initialize empty stack.
3. For each token:
   - Empty or `'.'`: skip.
   - `'..'`: pop the stack if it is non-empty.
   - Otherwise: push.
4. Return `'/' + '/'.join(stack)`. If stack is empty, return `'/'`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(n) |

### Implementation

#### Go

```go
import "strings"

func simplifyPath(path string) string {
    parts := strings.Split(path, "/")
    stack := []string{}
    for _, p := range parts {
        if p == "" || p == "." {
            continue
        }
        if p == ".." {
            if len(stack) > 0 {
                stack = stack[:len(stack)-1]
            }
            continue
        }
        stack = append(stack, p)
    }
    return "/" + strings.Join(stack, "/")
}
```

#### Java

```java
class Solution {
    public String simplifyPath(String path) {
        String[] parts = path.split("/");
        Deque<String> stack = new ArrayDeque<>();
        for (String p : parts) {
            if (p.isEmpty() || p.equals(".")) continue;
            if (p.equals("..")) {
                if (!stack.isEmpty()) stack.pop();
                continue;
            }
            stack.push(p);
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = stack.descendingIterator();
        while (it.hasNext()) {
            sb.append('/').append(it.next());
        }
        return sb.length() == 0 ? "/" : sb.toString();
    }
}
```

#### Python

```python
class Solution:
    def simplifyPath(self, path: str) -> str:
        stack = []
        for p in path.split('/'):
            if p == '' or p == '.':
                continue
            if p == '..':
                if stack:
                    stack.pop()
                continue
            stack.append(p)
        return '/' + '/'.join(stack)
```

### Dry Run

```text
path = "/a/./b/../../c/"
parts = ["", "a", ".", "b", "..", "..", "c", ""]
stack = []
"" → skip
"a" → push → ["a"]
"." → skip
"b" → push → ["a", "b"]
".." → pop → ["a"]
".." → pop → []
"c" → push → ["c"]
"" → skip
return "/c"
```

---

## Approach 2: Manual Single-Pass Parser

### Idea

> Walk the string with two pointers, capturing each segment between slashes without allocating a full split list. Same logic as Approach 1, slightly less memory.

### Implementation

#### Python

```python
class Solution:
    def simplifyPathManual(self, path: str) -> str:
        stack = []
        i, n = 0, len(path)
        while i < n:
            while i < n and path[i] == '/':
                i += 1
            j = i
            while j < n and path[j] != '/':
                j += 1
            seg = path[i:j]
            i = j
            if seg == '' or seg == '.':
                continue
            if seg == '..':
                if stack:
                    stack.pop()
                continue
            stack.append(seg)
        return '/' + '/'.join(stack)
```

> Same big-O. Useful when split is expensive or for streaming parsers.

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Split + Stack | O(n) | O(n) | Concise | Allocates split list |
| 2 | Manual Parser | O(n) | O(n) | Avoids extra list | Slightly more code |

### Which solution to choose?

Approach 1 in almost every case.

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Root | `"/"` | `"/"` | Stack empty after parse |
| 2 | Trailing slash | `"/home/"` | `"/home"` | Drop trailing |
| 3 | Multiple slashes | `"/home//foo/"` | `"/home/foo"` | Empty tokens skipped |
| 4 | `..` from root | `"/../"` | `"/"` | Pop from empty stack is no-op |
| 5 | Mixed | `"/a/./b/../../c/"` | `"/c"` | Standard |
| 6 | Many `..` | `"/a/b/c/../../../"` | `"/"` | All popped |
| 7 | Hidden file | `"/.hidden/file"` | `"/.hidden/file"` | `.hidden` is a name, not `.` |
| 8 | `...` | `"/.../"` | `"/..."` | Three dots is a name |

---

## Common Mistakes

### Mistake 1: Treating `...` as parent

```python
# WRONG — only "." and ".." are special
if p in {'.', '..', '...'}: ...

# CORRECT — only "." and ".."
if p in {'.', '..'}: ...
```

**Reason:** `...` is a valid file/dir name; only `.` and `..` are reserved.

### Mistake 2: Popping on empty stack throws

```python
# WRONG (in some languages)
stack.pop()   # IndexError when empty

# CORRECT
if stack: stack.pop()
```

**Reason:** Going up from root must remain at root, not raise an error.

### Mistake 3: Not handling root output

```python
# WRONG — empty stack returns "" not "/"
return '/'.join(stack)   # missing leading slash

# CORRECT
return '/' + '/'.join(stack)
```

**Reason:** Canonical path always begins with `/`. When stack is empty, the result must be `/`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [394. Decode String](https://leetcode.com/problems/decode-string/) | :yellow_circle: Medium | Stack-based string parsing |
| 2 | [388. Longest Absolute File Path](https://leetcode.com/problems/longest-absolute-file-path/) | :yellow_circle: Medium | File system traversal |
| 3 | [726. Number of Atoms](https://leetcode.com/problems/number-of-atoms/) | :red_circle: Hard | Stack-based parser |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Token stream from splitting the path
> - Stack visualization that grows / shrinks per token
> - Action label per step (skip / push / pop)
> - Final canonical path output
