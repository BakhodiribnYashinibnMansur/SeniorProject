# 0008. String to Integer (atoi)

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Parse with Built-in / Naive](#approach-1-parse-with-built-in--naive)
4. [Approach 2: Single-pass Simulation (Optimal)](#approach-2-single-pass-simulation-optimal)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `String`, `Simulation` |

### Description

> Implement the `myAtoi(string s)` function, which converts a string to a 32-bit signed integer (similar to C/C++'s `atoi` function).
>
> The algorithm for `myAtoi(string s)` is as follows:
> 1. Read in and ignore any leading whitespace.
> 2. Check if the next character (if not already at the end of the string) is `'-'` or `'+'`. Read this character in if it is either. This determines if the final result is negative or positive respectively. Assume the result is positive if neither is present.
> 3. Read in next the characters until the next non-digit character or the end of the input is reached. The rest of the string is ignored.
> 4. Convert these digits into an integer. If no digits were read, then the integer is `0`.
> 5. Change the sign as necessary.
> 6. If the integer is out of the 32-bit signed integer range `[-2^31, 2^31 - 1]`, clamp the integer so that it remains in the range.
> 7. Return the integer as the final result.

### Examples

```
Example 1:
Input:  s = "42"
Output: 42

Example 2:
Input:  s = "   -42"
Output: -42
Explanation: Leading whitespace is ignored, '-' makes it negative.

Example 3:
Input:  s = "4193 with words"
Output: 4193
Explanation: Reading stops at the space character.

Example 4:
Input:  s = "words and 987"
Output: 0
Explanation: First non-whitespace character is 'w', not a digit or sign.

Example 5:
Input:  s = "-91283472332"
Output: -2147483648
Explanation: The number is less than -2^31, so clamped to INT_MIN.
```

### Constraints

- `0 <= s.length <= 200`
- `s` consists of English letters, digits, `' '`, `'+'`, `'-'`, `'.'`

---

## Problem Breakdown

### 1. What is being asked?

Simulate the standard C library `atoi` function: parse a string and extract the integer it represents, respecting sign, stopping at non-digit characters, and clamping at 32-bit signed integer boundaries.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | Input string, may contain whitespace, sign, digits, letters, dots |

Key observations:
- The string may start with **leading spaces**
- There may be an **optional sign** (`+` or `-`)
- Digits may be followed by **non-digit characters** which are ignored
- The number may **overflow** 32-bit integer range

### 3. What is the output?

- A **32-bit signed integer**
- Clamped to `[-2147483648, 2147483647]` on overflow
- Returns `0` if no valid number can be parsed

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 200` | Input is short; O(n) is trivially efficient |
| Characters include `'.'` | A dot must be treated as a non-digit stop character |
| 32-bit clamp required | Overflow checking must happen before multiplication |

### 5. Step-by-step example analysis

#### Example: `s = "   -42"`

```text
i=0: s[0]=' ' → skip whitespace
i=1: s[1]=' ' → skip whitespace
i=2: s[2]=' ' → skip whitespace
i=3: s[3]='-' → sign = -1, advance i
i=4: s[4]='4' → digit=4, result = 0*10+4 = 4
i=5: s[5]='2' → digit=2, result = 4*10+2 = 42
i=6: end of string

Return: -1 * 42 = -42
```

#### Example: `s = "4193 with words"`

```text
i=0: s[0]='4' → digit=4, result=4
i=1: s[1]='1' → digit=1, result=41
i=2: s[2]='9' → digit=9, result=419
i=3: s[3]='3' → digit=3, result=4193
i=4: s[4]=' ' → NOT a digit → STOP

Return: 1 * 4193 = 4193
```

### 6. Key Observations

1. **Strict order matters** — whitespace → sign → digits. Any deviation breaks parsing.
2. **Overflow detection before multiply** — if you multiply first, you lose the information needed to detect overflow. Check `result > INT_MAX / 10` before doing `result * 10`.
3. **Sign and digit '7'** — INT_MAX is `2147483647` (ends in 7) and INT_MIN is `-2147483648` (absolute ends in 8). Checking `digit > 7` handles both by clamping to the appropriate boundary.
4. **Stop on first non-digit** — only the leading contiguous digit sequence is used; everything after is ignored.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Simulation | Follow the algorithm steps exactly as described |
| Single-pass | One left-to-right traversal is sufficient |

---

## Approach 1: Parse with Built-in / Naive

### Thought process

> The simplest approach: use language built-ins to strip whitespace, detect sign, extract the digit prefix, then convert and clamp manually. This is closer to pseudo-code and helpful for understanding.

### Algorithm (step-by-step)

1. Strip leading whitespace with `trim`
2. Check first character for sign `+`/`-`
3. Collect contiguous digit characters from the front
4. Convert the digit string to an integer (or 0 if empty)
5. Apply sign and clamp to `[-2^31, 2^31-1]`

### Pseudocode

```text
function myAtoi(s):
    s = lstrip(s)
    if s is empty: return 0

    sign = 1
    if s[0] == '-': sign = -1; s = s[1:]
    elif s[0] == '+': s = s[1:]

    digits = ""
    for ch in s:
        if ch is not a digit: break
        digits += ch

    if digits is empty: return 0

    result = toInt(digits)
    result = sign * result
    return clamp(result, -2^31, 2^31-1)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | We scan each character at most once |
| **Space** | O(n) | We build a substring of digits |

### Implementation

#### Go

```go
// myAtoi — Naive approach using string slicing
// Time: O(n), Space: O(n)
func myAtoi(s string) int {
    // Strip leading spaces
    i := 0
    for i < len(s) && s[i] == ' ' {
        i++
    }
    s = s[i:]
    if len(s) == 0 {
        return 0
    }

    sign := 1
    if s[0] == '-' {
        sign = -1
        s = s[1:]
    } else if s[0] == '+' {
        s = s[1:]
    }

    // Collect digit characters
    j := 0
    for j < len(s) && s[j] >= '0' && s[j] <= '9' {
        j++
    }
    digits := s[:j]
    if len(digits) == 0 {
        return 0
    }

    // Convert and clamp
    result := 0
    for _, ch := range digits {
        result = result*10 + int(ch-'0')
        if result > math.MaxInt32 {
            if sign == 1 {
                return math.MaxInt32
            }
            return math.MinInt32
        }
    }
    return sign * result
}
```

#### Java

```java
// myAtoi — Naive approach using string helpers
// Time: O(n), Space: O(n)
public int myAtoi(String s) {
    s = s.stripLeading();
    if (s.isEmpty()) return 0;

    int sign = 1;
    int start = 0;
    if (s.charAt(0) == '-') { sign = -1; start = 1; }
    else if (s.charAt(0) == '+') { start = 1; }

    StringBuilder digits = new StringBuilder();
    for (int i = start; i < s.length(); i++) {
        if (!Character.isDigit(s.charAt(i))) break;
        digits.append(s.charAt(i));
    }
    if (digits.length() == 0) return 0;

    int result = 0;
    for (char ch : digits.toString().toCharArray()) {
        int digit = ch - '0';
        if (result > Integer.MAX_VALUE / 10 ||
           (result == Integer.MAX_VALUE / 10 && digit > 7)) {
            return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        result = result * 10 + digit;
    }
    return sign * result;
}
```

#### Python

```python
# myAtoi — Naive approach using string helpers
# Time: O(n), Space: O(n)
def myAtoi(self, s: str) -> int:
    s = s.lstrip()
    if not s:
        return 0

    sign = 1
    i = 0
    if s[0] in ('+', '-'):
        if s[0] == '-':
            sign = -1
        i = 1

    digits = ""
    while i < len(s) and s[i].isdigit():
        digits += s[i]
        i += 1

    if not digits:
        return 0

    result = int(digits)
    result = sign * result
    INT_MAX, INT_MIN = 2**31 - 1, -(2**31)
    return max(INT_MIN, min(INT_MAX, result))
```

### Dry Run

```text
Input: s = "   -42"

After lstrip: s = "-42"
sign = -1, s = "42"
digits = "42"
result = 42
return -1 * 42 = -42  ✅
```

---

## Approach 2: Single-pass Simulation (Optimal)

### Thought process

> Rather than building a digit substring and converting it separately, we can do everything in one pass: detect whitespace, sign, and digits simultaneously, updating the running result integer while checking for overflow before each multiply.

### Algorithm (step-by-step)

1. Use pointer `i = 0`, advance past whitespace while `s[i] == ' '`
2. Read optional sign character `+`/`-`, set `sign = ±1`, advance `i`
3. Loop while `s[i]` is a digit (`'0'`..`'9'`):
   - Extract `digit = s[i] - '0'`
   - **Overflow check:** if `result > INT_MAX/10` OR `(result == INT_MAX/10 AND digit > 7)` → clamp and return
   - `result = result * 10 + digit`
   - Advance `i`
4. Return `sign * result`

### Pseudocode

```text
function myAtoi(s):
    i = 0
    while s[i] == ' ': i++

    sign = 1
    if s[i] == '-': sign = -1; i++
    elif s[i] == '+': i++

    result = 0
    while s[i] is digit:
        digit = s[i] - '0'
        if result > INT_MAX/10 or (result == INT_MAX/10 and digit > 7):
            return INT_MAX if sign==1 else INT_MIN
        result = result * 10 + digit
        i++

    return sign * result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single left-to-right pass, each character visited at most once |
| **Space** | O(1) | Only integer variables: `i`, `sign`, `result`, `digit` |

### Implementation

#### Go

```go
// myAtoi — Single-pass Simulation
// Time: O(n), Space: O(1)
func myAtoi(s string) int {
    i, n := 0, len(s)

    // Skip whitespace
    for i < n && s[i] == ' ' { i++ }

    // Determine sign
    sign := 1
    if i < n && (s[i] == '+' || s[i] == '-') {
        if s[i] == '-' { sign = -1 }
        i++
    }

    // Read digits with overflow check
    result := 0
    for i < n && s[i] >= '0' && s[i] <= '9' {
        digit := int(s[i] - '0')
        if result > math.MaxInt32/10 || (result == math.MaxInt32/10 && digit > 7) {
            if sign == 1 { return math.MaxInt32 }
            return math.MinInt32
        }
        result = result*10 + digit
        i++
    }

    return sign * result
}
```

#### Java

```java
// myAtoi — Single-pass Simulation
// Time: O(n), Space: O(1)
public int myAtoi(String s) {
    int i = 0, n = s.length();

    // Skip whitespace
    while (i < n && s.charAt(i) == ' ') i++;

    // Determine sign
    int sign = 1;
    if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
        if (s.charAt(i) == '-') sign = -1;
        i++;
    }

    // Read digits with overflow check
    int result = 0;
    while (i < n && Character.isDigit(s.charAt(i))) {
        int digit = s.charAt(i) - '0';
        if (result > Integer.MAX_VALUE / 10 ||
           (result == Integer.MAX_VALUE / 10 && digit > 7)) {
            return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        result = result * 10 + digit;
        i++;
    }

    return sign * result;
}
```

#### Python

```python
# myAtoi — Single-pass Simulation
# Time: O(n), Space: O(1)
def myAtoi(self, s: str) -> int:
    i, n = 0, len(s)
    INT_MAX, INT_MIN = 2**31 - 1, -(2**31)

    # Skip whitespace
    while i < n and s[i] == ' ':
        i += 1

    # Determine sign
    sign = 1
    if i < n and s[i] in ('+', '-'):
        if s[i] == '-': sign = -1
        i += 1

    # Read digits with overflow check
    result = 0
    while i < n and s[i].isdigit():
        digit = int(s[i])
        if result > INT_MAX // 10 or (result == INT_MAX // 10 and digit > 7):
            return INT_MAX if sign == 1 else INT_MIN
        result = result * 10 + digit
        i += 1

    return sign * result
```

### Dry Run

```text
Input: s = "-91283472332"

i=0: s[0]='-' → sign=-1, i=1
i=1: digit=9,  result=9
i=2: digit=1,  result=91
i=3: digit=2,  result=912
i=4: digit=8,  result=9128
i=5: digit=3,  result=91283
i=6: digit=4,  result=912834
i=7: digit=7,  result=9128347
i=8: digit=2,  result=91283472

i=9: digit=3
  result=91283472 > INT_MAX/10 (214748364)?
  91283472 > 214748364? NO... wait:
  Actually 91283472 < 214748364, so not yet.
  result = 912834723

i=10: digit=3
  912834723 > 214748364? YES → clamp
  sign=-1 → return INT_MIN = -2147483648  ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Naive (built-ins + substring) | O(n) | O(n) | Easy to read | Extra string allocation |
| 2 | Single-pass Simulation | O(n) | O(1) | No extra memory, elegant | Requires careful overflow check |

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Empty string | `""` | `0` | No characters to parse |
| 2 | Only spaces | `"   "` | `0` | Nothing after whitespace |
| 3 | Only sign | `"+"` or `"-"` | `0` | Sign without digits |
| 4 | Plus sign positive | `"+42"` | `42` | Explicit positive sign |
| 5 | Overflow positive | `"9999999999"` | `2147483647` | Clamp to INT_MAX |
| 6 | Overflow negative | `"-91283472332"` | `-2147483648` | Clamp to INT_MIN |
| 7 | Dot stops parsing | `"3.14"` | `3` | Dot is not a digit |
| 8 | Leading zeroes | `"0032"` | `32` | Zeroes ignored naturally |

---

## Common Mistakes

### Mistake 1: Overflow check after multiplication

```python
# ❌ WRONG — overflow happens silently in languages with fixed int size
result = result * 10 + digit
if result > INT_MAX:
    return INT_MAX

# ✅ CORRECT — check BEFORE multiplying
if result > INT_MAX // 10 or (result == INT_MAX // 10 and digit > 7):
    return INT_MAX if sign == 1 else INT_MIN
result = result * 10 + digit
```

**Reason:** In Go/Java, `result * 10` can silently wrap around if `result` is already near INT_MAX. Always check the condition before the multiply.

### Mistake 2: Forgetting that dot `'.'` is not a digit

```python
# ❌ WRONG — treating dot as part of the number
# Input "3.14" → trying to parse 3.14 as float → incorrect

# ✅ CORRECT — stop at dot
while i < n and s[i].isdigit():  # isdigit() returns False for '.'
    ...
# "3.14" → stops at '.', returns 3
```

**Reason:** The problem explicitly states that `s` may contain `'.'`, which must be treated as a stop character.

### Mistake 3: Not handling sign-only input

```python
# ❌ WRONG — assumes a digit always follows the sign
sign_char = s[0]
result = int(s[1:])  # crashes if s == "+" or s == "-"

# ✅ CORRECT — check if any digits follow
if i < n and s[i].isdigit():
    # parse digits
else:
    return 0
```

**Reason:** Input like `"+"` or `"-"` is valid (no digits follow), and must return `0`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [7. Reverse Integer](https://leetcode.com/problems/reverse-integer/) | 🟡 Medium | Overflow detection, 32-bit int boundaries |
| 2 | [65. Valid Number](https://leetcode.com/problems/valid-number/) | 🔴 Hard | Validates numeric strings with full rules |
| 3 | [150. Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | 🟡 Medium | String-to-int parsing as sub-step |
| 4 | [227. Basic Calculator II](https://leetcode.com/problems/basic-calculator-ii/) | 🟡 Medium | Parsing numbers from a string expression |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - Watch the pointer advance character by character
> - See whitespace, sign, and digit phases highlighted
> - Observe the overflow check triggering on large numbers
> - Compare the naive (substring) vs single-pass approaches
