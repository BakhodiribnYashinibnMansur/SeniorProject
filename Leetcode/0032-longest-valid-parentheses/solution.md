# 0032. Longest Valid Parentheses

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Stack](#approach-1-stack)
4. [Approach 2: Dynamic Programming](#approach-2-dynamic-programming)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [32. Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `String`, `Dynamic Programming`, `Stack` |

### Description

> Given a string containing just the characters `'('` and `')'`, return the length of the **longest valid (well-formed) parentheses substring**.

### Examples

```
Example 1:
Input: s = "(()"
Output: 2
Explanation: The longest valid parentheses substring is "()".

Example 2:
Input: s = ")()())"
Output: 4
Explanation: The longest valid parentheses substring is "()()".

Example 3:
Input: s = ""
Output: 0
```

### Constraints

- `0 <= s.length <= 3 * 10^4`
- `s[i]` is `'('` or `')'`

---

## Problem Breakdown

### 1. What is being asked?

Find the **length** of the longest contiguous substring that forms a valid (well-formed) sequence of parentheses. We are not asked to return the substring itself, only its length.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | String containing only `(` and `)` characters |

Important observations about the input:
- Only **one type** of bracket (round parentheses)
- The string can be **empty** (return 0)
- We need the longest **contiguous** valid substring, not total matched pairs
- Multiple disjoint valid substrings can exist; we want the longest one

### 3. What is the output?

- An **integer**: the length of the longest valid parentheses substring
- Minimum output is `0` (no valid pairs at all)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 3 * 10^4` | O(n) or O(n log n) solutions needed; O(n^2) may be too slow |
| Only `(` and `)` | Single bracket type simplifies matching logic |
| Length can be 0 | Must handle empty string gracefully |

### 5. Step-by-step example analysis

#### Example 1: `s = "(()"`

```text
Initial state: s = "(()"

Substrings to consider:
  "(("   -> invalid
  "(()"  -> invalid (unmatched opening)
  "()"   -> VALID, length 2
  ")"    -> invalid

Longest valid substring: "()" at index 1-2, length = 2
```

#### Example 2: `s = ")()())"`

```text
Initial state: s = ")()())"

Substrings to consider:
  ")("      -> invalid
  "()"      -> VALID, length 2, at index 1-2
  "()()"    -> VALID, length 4, at index 1-4
  "()"      -> VALID, length 2, at index 3-4
  ")()())"  -> invalid

Longest valid substring: "()()" at index 1-4, length = 4
```

#### Example 3: `s = "()(()"`

```text
Initial state: s = "()(()"`

  "()"   -> VALID, length 2, at index 0-1
  "(("   -> invalid
  "(()"  -> invalid as a whole, but contains "()" length 2
  "()((" -> invalid
  "()(())" would be valid but string ends at index 4

Longest valid substring: "()" at index 0-1, length = 2
```

### 6. Key Observations

1. **Contiguity matters** -- We need the longest *contiguous* valid substring, not just the count of matched pairs.
2. **Adjacent valid segments merge** -- `()()` is one valid substring of length 4, not two separate length-2 substrings.
3. **Nested segments count** -- `(())` is a valid substring of length 4.
4. **Boundary tracking** -- We need to know where valid substrings start and end to compute their lengths.
5. **Stack stores indices** -- Unlike problem 20 (Valid Parentheses) where we store characters, here we store **indices** to compute lengths.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Stack (with indices) | Track unmatched positions to compute valid segment lengths | This problem |
| Dynamic Programming | dp[i] = length of longest valid substring ending at index i | This problem |
| Two-pass counter | Count left/right parentheses in two passes | O(1) space variant |

**Chosen patterns:** `Stack` and `Dynamic Programming`
**Reason:** Both are O(n) time, O(n) space. Stack is more intuitive; DP gives a different perspective on the problem.

---

## Approach 1: Stack

### Thought process

> The key insight is to use a stack that stores **indices** (not characters).
> We initialize the stack with `-1` as a base for length calculation.
> When we see `(`, we push its index. When we see `)`:
> - Pop the top.
> - If the stack is empty, push the current index as the new base.
> - If the stack is not empty, the current valid length is `i - stack.top`.
> We track the maximum length seen.

### Algorithm (step-by-step)

1. Initialize stack with `[-1]` (base index for length calculation)
2. Initialize `maxLen = 0`
3. Iterate through each character with index `i`:
   - If `s[i] == '('`: push `i` onto the stack
   - If `s[i] == ')'`:
     - Pop the top element
     - If stack is empty: push `i` (new base index)
     - If stack is not empty: `maxLen = max(maxLen, i - stack.top)`
4. Return `maxLen`

### Pseudocode

```text
function longestValidParentheses(s):
    stack = [-1]
    maxLen = 0

    for i = 0 to len(s) - 1:
        if s[i] == '(':
            stack.push(i)
        else:
            stack.pop()
            if stack is empty:
                stack.push(i)
            else:
                maxLen = max(maxLen, i - stack.top())

    return maxLen
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the string. Each index is pushed/popped at most once. |
| **Space** | O(n) | In the worst case (all opening brackets), the stack stores n+1 elements. |

### Implementation

#### Go

```go
func longestValidParentheses(s string) int {
    stack := []int{-1}
    maxLen := 0

    for i := 0; i < len(s); i++ {
        if s[i] == '(' {
            stack = append(stack, i)
        } else {
            stack = stack[:len(stack)-1]
            if len(stack) == 0 {
                stack = append(stack, i)
            } else {
                length := i - stack[len(stack)-1]
                if length > maxLen {
                    maxLen = length
                }
            }
        }
    }

    return maxLen
}
```

#### Java

```java
class Solution {
    public int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }

        return maxLen;
    }
}
```

#### Python

```python
class Solution:
    def longestValidParentheses(self, s: str) -> int:
        stack = [-1]
        max_len = 0

        for i, ch in enumerate(s):
            if ch == '(':
                stack.append(i)
            else:
                stack.pop()
                if not stack:
                    stack.append(i)
                else:
                    max_len = max(max_len, i - stack[-1])

        return max_len
```

### Dry Run

```text
Input: s = ")()())"

stack = [-1], maxLen = 0

Step 0: ch=')', pop -> stack=[], empty -> push 0
        stack = [0], maxLen = 0

Step 1: ch='(', push 1
        stack = [0, 1], maxLen = 0

Step 2: ch=')', pop -> stack=[0], not empty
        maxLen = max(0, 2 - 0) = 2
        stack = [0], maxLen = 2

Step 3: ch='(', push 3
        stack = [0, 3], maxLen = 2

Step 4: ch=')', pop -> stack=[0], not empty
        maxLen = max(2, 4 - 0) = 4
        stack = [0], maxLen = 4

Step 5: ch=')', pop -> stack=[], empty -> push 5
        stack = [5], maxLen = 4

Return maxLen = 4
```

```text
Input: s = "(()"

stack = [-1], maxLen = 0

Step 0: ch='(', push 0
        stack = [-1, 0], maxLen = 0

Step 1: ch='(', push 1
        stack = [-1, 0, 1], maxLen = 0

Step 2: ch=')', pop -> stack=[-1, 0], not empty
        maxLen = max(0, 2 - 0) = 2
        stack = [-1, 0], maxLen = 2

Return maxLen = 2
```

---

## Approach 2: Dynamic Programming

### Thought process

> Define `dp[i]` as the length of the longest valid parentheses substring **ending at index i**.
> - If `s[i] == '('`, then `dp[i] = 0` (a valid substring cannot end with `(`).
> - If `s[i] == ')'`:
>   - If `s[i-1] == '('`: we have `...()`, so `dp[i] = dp[i-2] + 2`
>   - If `s[i-1] == ')'` and `s[i - dp[i-1] - 1] == '('`: we have `...))` where the inner `)` already has a valid substring, and the character before that inner valid substring is `(`. So `dp[i] = dp[i-1] + 2 + dp[i - dp[i-1] - 2]`

### Algorithm (step-by-step)

1. If string length < 2, return 0
2. Create `dp` array of size `n`, initialized to 0
3. Initialize `maxLen = 0`
4. For `i` from 1 to n-1:
   - If `s[i] == ')'`:
     - **Case 1:** `s[i-1] == '('` (pattern `...()`):
       - `dp[i] = (dp[i-2] if i >= 2 else 0) + 2`
     - **Case 2:** `s[i-1] == ')'` and `i - dp[i-1] - 1 >= 0` and `s[i - dp[i-1] - 1] == '('` (pattern `...))` with matching `(`):
       - `dp[i] = dp[i-1] + 2 + (dp[i - dp[i-1] - 2] if i - dp[i-1] - 2 >= 0 else 0)`
     - Update `maxLen = max(maxLen, dp[i])`
5. Return `maxLen`

### Pseudocode

```text
function longestValidParentheses(s):
    n = len(s)
    if n < 2: return 0

    dp = array of n zeros
    maxLen = 0

    for i from 1 to n-1:
        if s[i] == ')':
            if s[i-1] == '(':
                dp[i] = (dp[i-2] if i >= 2 else 0) + 2
            elif i - dp[i-1] - 1 >= 0 and s[i - dp[i-1] - 1] == '(':
                dp[i] = dp[i-1] + 2 + (dp[i - dp[i-1] - 2] if i - dp[i-1] >= 2 else 0)
            maxLen = max(maxLen, dp[i])

    return maxLen
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the string. Each element is computed in O(1). |
| **Space** | O(n) | The dp array stores n values. |

### Implementation

#### Go

```go
func longestValidParentheses(s string) int {
    n := len(s)
    if n < 2 {
        return 0
    }

    dp := make([]int, n)
    maxLen := 0

    for i := 1; i < n; i++ {
        if s[i] == ')' {
            if s[i-1] == '(' {
                // Case 1: ...()
                if i >= 2 {
                    dp[i] = dp[i-2] + 2
                } else {
                    dp[i] = 2
                }
            } else if i-dp[i-1]-1 >= 0 && s[i-dp[i-1]-1] == '(' {
                // Case 2: ...))
                dp[i] = dp[i-1] + 2
                if i-dp[i-1]-2 >= 0 {
                    dp[i] += dp[i-dp[i-1]-2]
                }
            }
            if dp[i] > maxLen {
                maxLen = dp[i]
            }
        }
    }

    return maxLen
}
```

#### Java

```java
class Solution {
    public int longestValidParentheses(String s) {
        int n = s.length();
        if (n < 2) return 0;

        int[] dp = new int[n];
        int maxLen = 0;

        for (int i = 1; i < n; i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    // Case 1: ...()
                    dp[i] = (i >= 2 ? dp[i - 2] : 0) + 2;
                } else if (i - dp[i - 1] - 1 >= 0
                           && s.charAt(i - dp[i - 1] - 1) == '(') {
                    // Case 2: ...))
                    dp[i] = dp[i - 1] + 2
                          + (i - dp[i - 1] - 2 >= 0 ? dp[i - dp[i - 1] - 2] : 0);
                }
                maxLen = Math.max(maxLen, dp[i]);
            }
        }

        return maxLen;
    }
}
```

#### Python

```python
class Solution:
    def longestValidParentheses(self, s: str) -> int:
        n = len(s)
        if n < 2:
            return 0

        dp = [0] * n
        max_len = 0

        for i in range(1, n):
            if s[i] == ')':
                if s[i - 1] == '(':
                    # Case 1: ...()
                    dp[i] = (dp[i - 2] if i >= 2 else 0) + 2
                elif i - dp[i - 1] - 1 >= 0 and s[i - dp[i - 1] - 1] == '(':
                    # Case 2: ...))
                    dp[i] = dp[i - 1] + 2
                    if i - dp[i - 1] - 2 >= 0:
                        dp[i] += dp[i - dp[i - 1] - 2]
                max_len = max(max_len, dp[i])

        return max_len
```

### Dry Run

```text
Input: s = ")()())"

n = 6, dp = [0, 0, 0, 0, 0, 0], maxLen = 0

i=1: s[1]='(' -> skip (not ')')
     dp = [0, 0, 0, 0, 0, 0]

i=2: s[2]=')', s[1]='(' -> Case 1: dp[2] = dp[0] + 2 = 0 + 2 = 2
     maxLen = max(0, 2) = 2
     dp = [0, 0, 2, 0, 0, 0]

i=3: s[3]='(' -> skip
     dp = [0, 0, 2, 0, 0, 0]

i=4: s[4]=')', s[3]='(' -> Case 1: dp[4] = dp[2] + 2 = 2 + 2 = 4
     maxLen = max(2, 4) = 4
     dp = [0, 0, 2, 0, 4, 0]

i=5: s[5]=')', s[4]=')'
     Check Case 2: i - dp[i-1] - 1 = 5 - 4 - 1 = 0
     s[0] = ')' != '(' -> Case 2 fails
     dp[5] = 0
     dp = [0, 0, 2, 0, 4, 0]

Return maxLen = 4
```

```text
Input: s = "(()"

n = 3, dp = [0, 0, 0], maxLen = 0

i=1: s[1]='(' -> skip
     dp = [0, 0, 0]

i=2: s[2]=')', s[1]='(' -> Case 1: dp[2] = dp[0] + 2 = 0 + 2 = 2
     maxLen = max(0, 2) = 2
     dp = [0, 0, 2]

Return maxLen = 2
```

```text
Input: s = "()(())"

n = 6, dp = [0, 0, 0, 0, 0, 0], maxLen = 0

i=1: s[1]=')', s[0]='(' -> Case 1: dp[1] = 0 + 2 = 2
     maxLen = 2
     dp = [0, 2, 0, 0, 0, 0]

i=2: s[2]='(' -> skip

i=3: s[3]='(' -> skip

i=4: s[4]=')', s[3]='(' -> Case 1: dp[4] = dp[2] + 2 = 0 + 2 = 2
     maxLen = 2
     dp = [0, 2, 0, 0, 2, 0]

i=5: s[5]=')', s[4]=')'
     Check Case 2: i - dp[i-1] - 1 = 5 - 2 - 1 = 2
     s[2] = '(' -> Case 2 matches!
     dp[5] = dp[4] + 2 + dp[5 - 2 - 2] = 2 + 2 + dp[1] = 2 + 2 + 2 = 6
     maxLen = max(2, 6) = 6
     dp = [0, 2, 0, 0, 2, 6]

Return maxLen = 6
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Stack | O(n) | O(n) | Intuitive, elegant, single pass | Stack overhead |
| 2 | Dynamic Programming | O(n) | O(n) | Clear recurrence relation | Multiple cases to handle correctly |

### Which solution to choose?

- **In an interview:** Approach 1 (Stack) -- easier to explain and fewer edge cases
- **In production:** Either -- both are O(n) time and space
- **On Leetcode:** Either -- same performance
- **For learning:** Both -- Stack teaches index-based stack usage; DP teaches recurrence design

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty string | `s = ""` | `0` | No characters, no valid substring |
| 2 | Single character | `s = "("` | `0` | Cannot form a pair |
| 3 | All opening | `s = "((("` | `0` | No closing brackets to match |
| 4 | All closing | `s = ")))"` | `0` | No opening brackets to match |
| 5 | Simple valid | `s = "()"` | `2` | One valid pair |
| 6 | Entire string valid | `s = "(())"` | `4` | Nested, entire string is valid |
| 7 | Adjacent valid pairs | `s = "()()"` | `4` | Two pairs merge into one valid substring |
| 8 | Valid in the middle | `s = ")()())"` | `4` | Valid portion surrounded by unmatched |
| 9 | Prefix valid | `s = "()(()"` | `2` | First `()` is longest; second `()` is also 2 |
| 10 | Complex nesting | `s = "()(())"` | `6` | Adjacent + nested combine |

---

## Common Mistakes

### Mistake 1: Counting total matched pairs instead of longest contiguous substring

```python
# WRONG -- counts total matched pairs, not longest contiguous substring
count = 0
stack = []
for ch in s:
    if ch == '(':
        stack.append(ch)
    elif stack:
        stack.pop()
        count += 2
return count  # "(())()" returns 6, but so does "())()((" which should be 2

# CORRECT -- use index-based stack to track contiguous segments
```

**Reason:** `"()((" has 1 matched pair (length 2), not 0. But `"(())"` has 2 matched pairs forming a contiguous length-4 substring. Simple counting loses positional information.

### Mistake 2: Not initializing stack with -1

```python
# WRONG -- no base index, cannot compute length for first valid pair
stack = []
max_len = 0
for i, ch in enumerate(s):
    if ch == '(':
        stack.append(i)
    else:
        stack.pop()
        if stack:
            max_len = max(max_len, i - stack[-1])
# For s = "()", after pop stack is empty, length is never computed!

# CORRECT -- push -1 as base
stack = [-1]
```

**Reason:** The `-1` serves as a boundary marker. When a `)` matches the last `(` on the stack, the length is computed relative to the element below it (the base).

### Mistake 3: Forgetting to add dp[i - dp[i-1] - 2] in the DP approach

```python
# WRONG -- misses the valid substring BEFORE the current nested match
if s[i-1] == ')' and i - dp[i-1] - 1 >= 0 and s[i - dp[i-1] - 1] == '(':
    dp[i] = dp[i-1] + 2  # forgot to add dp[i - dp[i-1] - 2]

# For s = "()(())", dp[5] would be 4 instead of 6
# The "()" before "(())" is lost

# CORRECT -- include the valid substring before the matching '('
dp[i] = dp[i-1] + 2 + (dp[i - dp[i-1] - 2] if i - dp[i-1] - 2 >= 0 else 0)
```

**Reason:** After matching `...((inner))`, there might be a valid substring immediately before the outer `(`. We must add its length to get the full contiguous valid substring.

### Mistake 4: Not handling index bounds in DP

```python
# WRONG -- dp[i-2] when i < 2 causes IndexError
if s[i-1] == '(':
    dp[i] = dp[i-2] + 2  # crash when i == 1

# CORRECT -- check bounds
dp[i] = (dp[i-2] if i >= 2 else 0) + 2
```

**Reason:** When `i == 1` and `s = "()"`, `i-2 == -1` which in Python wraps to the last element (wrong value) and in other languages may crash.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [20. Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | :green_circle: Easy | Basic bracket matching with stack |
| 2 | [22. Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) | :yellow_circle: Medium | Generate all valid parentheses combinations |
| 3 | [301. Remove Invalid Parentheses](https://leetcode.com/problems/remove-invalid-parentheses/) | :red_circle: Hard | Remove minimum brackets to make valid |
| 4 | [678. Valid Parenthesis String](https://leetcode.com/problems/valid-parenthesis-string/) | :yellow_circle: Medium | Parentheses with wildcard character |
| 5 | [1249. Minimum Remove to Make Valid Parentheses](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/) | :yellow_circle: Medium | Remove minimum brackets to make valid |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Stack** tab -- character-by-character processing with index-based stack visualization
> - **DP** tab -- step-by-step dp array computation with case highlighting
