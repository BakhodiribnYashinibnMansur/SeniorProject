# 0020. Valid Parentheses

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Stack](#approach-1-stack)
4. [Approach 2: Replace Pairs](#approach-2-replace-pairs)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [20. Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `String`, `Stack` |

### Description

> Given a string `s` containing just the characters `'('`, `')'`, `'{'`, `'}'`, `'['` and `']'`, determine if the input string is valid.
>
> An input string is valid if:
> 1. Open brackets must be closed by the **same type** of brackets.
> 2. Open brackets must be closed in the **correct order**.
> 3. Every close bracket has a corresponding **open bracket** of the same type.

### Examples

```
Example 1:
Input: s = "()"
Output: true

Example 2:
Input: s = "()[]{}"
Output: true

Example 3:
Input: s = "(]"
Output: false

Example 4:
Input: s = "([])"
Output: true
```

### Constraints

- `1 <= s.length <= 10^4`
- `s` consists of parentheses only `'()[]{}'`

---

## Problem Breakdown

### 1. What is being asked?

Determine whether a string of brackets is **well-formed** -- every opening bracket has a matching closing bracket of the same type, and they are properly nested.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | String containing only `()[]{}` characters |

Important observations about the input:
- The string contains **only bracket characters** (no letters, digits, etc.)
- The string can be **empty** (length >= 1 per constraints, but logically empty = valid)
- **Multiple bracket types** can be intermixed
- Brackets can be **nested** to any depth

### 3. What is the output?

- A **boolean** value: `true` if the string is valid, `false` otherwise
- Valid means all three conditions are satisfied simultaneously

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 10^4` | Both O(n) and O(n^2) solutions will pass |
| Only bracket characters | No need to filter or skip non-bracket characters |
| Three bracket types | Need to handle matching between `()`, `[]`, `{}` |

### 5. Step-by-step example analysis

#### Example 1: `s = "()"`

```text
Initial state: s = "()"

Character '(' -- opening bracket, push onto stack
  Stack: ['(']

Character ')' -- closing bracket, matches top '(' -- pop
  Stack: []

Stack is empty -> true
```

#### Example 2: `s = "(]"`

```text
Initial state: s = "(]"

Character '(' -- opening bracket, push onto stack
  Stack: ['(']

Character ']' -- closing bracket, top is '(' but expected '[' -- MISMATCH!
  return false
```

#### Example 3: `s = "{[()]}"`

```text
Initial state: s = "{[()]}"

Character '{' -> push    Stack: ['{']
Character '[' -> push    Stack: ['{', '[']
Character '(' -> push    Stack: ['{', '[', '(']
Character ')' -> match ( Stack: ['{', '[']
Character ']' -> match [ Stack: ['{']
Character '}' -> match { Stack: []

Stack is empty -> true
```

### 6. Key Observations

1. **LIFO order** -- The most recently opened bracket must be closed first. This is exactly how a **stack** works.
2. **Bracket matching** -- Each closing bracket must match the type of the most recent unmatched opening bracket.
3. **Even length** -- A valid string must have even length (each open has a close).
4. **Stack empty at end** -- If the stack is not empty after processing all characters, there are unmatched opening brackets.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Stack | LIFO matches bracket nesting order | Valid Parentheses (this problem) |
| String replacement | Repeatedly remove valid pairs | Simpler but slower approach |
| Counter | Only works for single bracket type | Not sufficient here |

**Chosen pattern:** `Stack`
**Reason:** The problem requires matching brackets in LIFO order, which is the fundamental property of a stack.

---

## Approach 1: Stack

### Thought process

> Use a stack to track opening brackets. When we encounter a closing bracket, check if it matches the top of the stack.
> If it matches, pop the stack. If not, the string is invalid.
> At the end, the stack must be empty for the string to be valid.

### Algorithm (step-by-step)

1. Create an empty stack
2. Create a mapping: closing bracket -> corresponding opening bracket
3. Iterate through each character in the string:
   - If it's an opening bracket (`(`, `[`, `{`): push onto stack
   - If it's a closing bracket:
     - If stack is empty -> return false
     - If top of stack != matching opening bracket -> return false
     - Otherwise, pop the top of the stack
4. Return true if the stack is empty, false otherwise

### Pseudocode

```text
function isValid(s):
    stack = []
    matching = {')': '(', ']': '[', '}': '{'}

    for ch in s:
        if ch is opening bracket:
            stack.push(ch)
        else:
            if stack is empty or stack.top != matching[ch]:
                return false
            stack.pop()

    return stack is empty
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the string. Each character is pushed/popped at most once. |
| **Space** | O(n) | In the worst case (all opening brackets), the stack stores n elements. |

### Implementation

#### Go

```go
func isValid(s string) bool {
    stack := []byte{}
    matching := map[byte]byte{')': '(', ']': '[', '}': '{'}

    for i := 0; i < len(s); i++ {
        ch := s[i]
        if ch == '(' || ch == '[' || ch == '{' {
            stack = append(stack, ch)
        } else {
            if len(stack) == 0 || stack[len(stack)-1] != matching[ch] {
                return false
            }
            stack = stack[:len(stack)-1]
        }
    }

    return len(stack) == 0
}
```

#### Java

```java
class Solution {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> matching = Map.of(
            ')', '(', ']', '[', '}', '{'
        );

        for (char ch : s.toCharArray()) {
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            } else {
                if (stack.isEmpty() || !stack.peek().equals(matching.get(ch))) {
                    return false;
                }
                stack.pop();
            }
        }

        return stack.isEmpty();
    }
}
```

#### Python

```python
class Solution:
    def isValid(self, s: str) -> bool:
        stack = []
        matching = {')': '(', ']': '[', '}': '{'}

        for ch in s:
            if ch in '([{':
                stack.append(ch)
            else:
                if not stack or stack[-1] != matching[ch]:
                    return False
                stack.pop()

        return len(stack) == 0
```

### Dry Run

```text
Input: s = "{[()]}"

stack = []

Step 1: ch='{', opening -> push
        stack = ['{']

Step 2: ch='[', opening -> push
        stack = ['{', '[']

Step 3: ch='(', opening -> push
        stack = ['{', '[', '(']

Step 4: ch=')', closing, matching[')']='('
        stack top = '(' == '(' -> match, pop
        stack = ['{', '[']

Step 5: ch=']', closing, matching[']']='['
        stack top = '[' == '[' -> match, pop
        stack = ['{']

Step 6: ch='}', closing, matching['}']='{'
        stack top = '{' == '{' -> match, pop
        stack = []

Stack is empty -> return true
```

```text
Input: s = "([)]"

stack = []

Step 1: ch='(', opening -> push
        stack = ['(']

Step 2: ch='[', opening -> push
        stack = ['(', '[']

Step 3: ch=')', closing, matching[')']='('
        stack top = '[' != '(' -> MISMATCH!
        return false
```

---

## Approach 2: Replace Pairs

### Thought process

> Repeatedly remove all adjacent valid pairs `()`, `[]`, `{}` from the string.
> If the string becomes empty, it was valid. Otherwise, it's invalid.
> This is simpler to understand but much slower.

### Algorithm (step-by-step)

1. Repeat until no more replacements can be made:
   - Replace all occurrences of `()` with `""`
   - Replace all occurrences of `[]` with `""`
   - Replace all occurrences of `{}` with `""`
2. If the string is empty -> return true
3. Otherwise -> return false

### Pseudocode

```text
function isValid(s):
    while true:
        new_s = s.replace("()", "").replace("[]", "").replace("{}", "")
        if new_s == s:
            break  // no more replacements possible
        s = new_s
    return s is empty
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Each replacement pass is O(n), and we may need up to n/2 passes. |
| **Space** | O(n) | New strings are created during replacement. |

### Implementation

#### Go

```go
import "strings"

func isValid(s string) bool {
    for {
        newS := strings.ReplaceAll(s, "()", "")
        newS = strings.ReplaceAll(newS, "[]", "")
        newS = strings.ReplaceAll(newS, "{}", "")
        if newS == s {
            break
        }
        s = newS
    }
    return len(s) == 0
}
```

#### Java

```java
class Solution {
    public boolean isValid(String s) {
        while (true) {
            String newS = s.replace("()", "")
                           .replace("[]", "")
                           .replace("{}", "");
            if (newS.equals(s)) break;
            s = newS;
        }
        return s.isEmpty();
    }
}
```

#### Python

```python
class Solution:
    def isValid(self, s: str) -> bool:
        while True:
            new_s = s.replace("()", "").replace("[]", "").replace("{}", "")
            if new_s == s:
                break
            s = new_s
        return len(s) == 0
```

### Dry Run

```text
Input: s = "{[()]}"

Pass 1: replace "()" -> "{[]}"
         replace "[]" -> "{}"
         replace "{}" -> ""
         s changed: "{[()]}" -> ""

s is empty -> return true
```

```text
Input: s = "([)]"

Pass 1: replace "()" -> "([)]" (no "()" found)
         replace "[]" -> "([)]" (no "[]" found)
         replace "{}" -> "([)]" (no "{}" found)
         s unchanged -> break

s = "([)]" is not empty -> return false
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Stack | O(n) | O(n) | Optimal, single pass, clean logic | Requires stack data structure |
| 2 | Replace Pairs | O(n^2) | O(n) | Very intuitive, easy to code | Slow, creates many string copies |

### Which solution to choose?

- **In an interview:** Approach 1 (Stack) -- demonstrates understanding of data structures
- **In production:** Approach 1 -- optimal time complexity
- **On Leetcode:** Approach 1 -- best Time Complexity
- **For learning:** Both -- Approach 2 helps build intuition, Approach 1 teaches the Stack pattern

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty string | `s = ""` | `true` | No brackets to mismatch |
| 2 | Single character | `s = "("` | `false` | Unmatched opening bracket |
| 3 | Single closing | `s = ")"` | `false` | No matching opening bracket |
| 4 | Simple valid | `s = "()"` | `true` | Basic matching pair |
| 5 | Nested valid | `s = "{[()]}"` | `true` | Properly nested, multiple types |
| 6 | Interleaved invalid | `s = "([)]"` | `false` | Correct types but wrong nesting order |
| 7 | All opening | `s = "((("` | `false` | Stack not empty at end |
| 8 | All closing | `s = ")))"` | `false` | Stack empty when closing bracket found |
| 9 | Long nested valid | `s = "(({{[[]]}}))"` | `true` | Deep nesting, all types |

---

## Common Mistakes

### Mistake 1: Forgetting to check if stack is empty before popping

```python
# WRONG -- stack[-1] throws IndexError if stack is empty
for ch in s:
    if ch in '([{':
        stack.append(ch)
    else:
        if stack[-1] != matching[ch]:  # crash if stack is empty!
            return False
        stack.pop()

# CORRECT -- check stack is not empty first
for ch in s:
    if ch in '([{':
        stack.append(ch)
    else:
        if not stack or stack[-1] != matching[ch]:
            return False
        stack.pop()
```

**Reason:** Input like `"]"` has a closing bracket with nothing on the stack.

### Mistake 2: Returning true immediately when a match is found

```python
# WRONG -- returns true on first match, ignoring remaining characters
if stack and stack[-1] == matching[ch]:
    stack.pop()
    return True  # premature!

# CORRECT -- process all characters, then check if stack is empty
return len(stack) == 0
```

**Reason:** `"()("` has a valid pair but the string is still invalid.

### Mistake 3: Forgetting to check stack is empty at the end

```python
# WRONG -- doesn't check for unmatched opening brackets
for ch in s:
    if ch in '([{':
        stack.append(ch)
    else:
        if not stack or stack[-1] != matching[ch]:
            return False
        stack.pop()
return True  # what if stack still has elements?

# CORRECT -- check stack is empty
return len(stack) == 0
```

**Reason:** `"(("` processes without errors but leaves unmatched brackets on the stack.

### Mistake 4: Using a counter instead of a stack

```python
# WRONG -- counter only works for one bracket type
count = 0
for ch in s:
    if ch == '(':
        count += 1
    elif ch == ')':
        count -= 1
# This doesn't handle multiple bracket types or nesting order

# CORRECT -- use a stack to track bracket types
```

**Reason:** A counter cannot distinguish between `"([])"` (valid) and `"([)]"` (invalid).

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [22. Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) | :yellow_circle: Medium | Generate all valid combinations of parentheses |
| 2 | [32. Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/) | :red_circle: Hard | Find the longest valid substring |
| 3 | [71. Simplify Path](https://leetcode.com/problems/simplify-path/) | :yellow_circle: Medium | Stack-based string processing |
| 4 | [150. Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | :yellow_circle: Medium | Stack-based evaluation |
| 5 | [1249. Minimum Remove to Make Valid Parentheses](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/) | :yellow_circle: Medium | Remove minimum brackets to make valid |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Stack** tab -- character-by-character processing with stack visualization
> - **Replace Pairs** tab -- repeatedly removes matching pairs until string is empty or unchanged
