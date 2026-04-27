# 0065. Valid Number

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Regular Expression](#approach-1-regular-expression)
4. [Approach 2: Manual Single Pass](#approach-2-manual-single-pass)
5. [Approach 3: Deterministic Finite Automaton (DFA)](#approach-3-deterministic-finite-automaton-dfa)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [65. Valid Number](https://leetcode.com/problems/valid-number/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `String` |

### Description

> A **valid number** can be split up into these components (in order):
>
> 1. A **decimal number** or an **integer**.
> 2. (Optional) An `'e'` or `'E'`, followed by an **integer**.
>
> A **decimal number** can be split up into these components (in order):
>
> 1. (Optional) A sign character (either `'+'` or `'-'`).
> 2. One of the following formats:
>    - One or more digits, followed by a dot `.`.
>    - One or more digits, followed by a dot `.`, followed by one or more digits.
>    - A dot `.`, followed by one or more digits.
>
> An **integer** can be split up into these components (in order):
>
> 1. (Optional) A sign character (either `'+'` or `'-'`).
> 2. One or more digits.
>
> Given a string `s`, return `true` *if* `s` *is a valid number*.

### Examples

```
Example 1:
Input: s = "0"
Output: true

Example 2:
Input: s = "e"
Output: false

Example 3:
Input: s = "."
Output: false

Examples that pass: "2", "0089", "-0.1", "+3.14", "4.", "-.9", "2e10", "-90E3", "3e+7", "+6e-1", "53.5e93", "-123.456e789"
Examples that fail: "abc", "1a", "1e", "e3", "99e2.5", "--6", "-+3", "95a54e53"
```

### Constraints

- `1 <= s.length <= 20`
- `s` consists of only English letters (both uppercase and lowercase), digits (`0-9`), plus `+`, minus `-`, or dot `.`.

---

## Problem Breakdown

### 1. What is being asked?

Decide whether a string represents a "number" under a fairly relaxed grammar that supports signs, optional decimals, and optional scientific exponents.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `str` | A non-empty string of letters, digits, `+`, `-`, `.` |

### 3. What is the output?

`true` if `s` matches the number grammar, `false` otherwise.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 20` | Speed irrelevant; correctness is everything |
| Tiny alphabet | Easy to enumerate transitions |

### 5. Step-by-step example analysis

#### `"-123.456e789"`

```text
Tokens encountered:
  '-'   sign before mantissa
  '1','2','3'  digits before dot
  '.'   decimal point
  '4','5','6'  digits after dot
  'e'   exponent marker
  '7','8','9'  digits in exponent

State path:
  start → sign → digit → dot-after-digit → digit-after-dot → exp → digit-in-exp ✓
```

#### `"99e2.5"`

```text
After 'e' we expect an integer (no dot allowed).
'.5' breaks the rule → false.
```

### 6. Key Observations

1. **Three sections** -- (optional sign) (mantissa) (optional `e`/`E` + signed-integer exponent).
2. **Mantissa shapes** -- "digits", "digits.", "digits.digits", ".digits". At least one digit must appear somewhere.
3. **Exponent constraint** -- Always integer (signed allowed), no dot.
4. **At most one sign per section** -- Signs only at the start of mantissa or just after `e`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Regular expression | Closed-form regex captures the grammar |
| Single-pass with flags | Track `sawDigit`, `sawDot`, `sawE`, `digitAfterE` |
| DFA | Build explicit transition table |

**Chosen pattern:** `Manual Single Pass` for clarity, `DFA` for absolute precision.

---

## Approach 1: Regular Expression

### Idea

> One regex captures everything.

### Pattern

```text
^[+-]?(\d+\.\d*|\.\d+|\d+)([eE][+-]?\d+)?$
```

Breakdown:
- `^[+-]?` optional leading sign
- `(\d+\.\d*|\.\d+|\d+)` mantissa: digits.digits | .digits | digits
- `([eE][+-]?\d+)?` optional exponent
- `$` end of string

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) |

### Implementation

#### Python

```python
import re

class Solution:
    pattern = re.compile(r'^[+-]?(\d+\.\d*|\.\d+|\d+)([eE][+-]?\d+)?$')
    def isNumberRegex(self, s: str) -> bool:
        return bool(self.pattern.fullmatch(s))
```

#### Go

```go
import "regexp"

var validNumberRe = regexp.MustCompile(`^[+-]?(\d+\.\d*|\.\d+|\d+)([eE][+-]?\d+)?$`)

func isNumberRegex(s string) bool {
    return validNumberRe.MatchString(s)
}
```

#### Java

```java
import java.util.regex.Pattern;

class Solution {
    private static final Pattern P =
        Pattern.compile("^[+-]?(\\d+\\.\\d*|\\.\\d+|\\d+)([eE][+-]?\\d+)?$");
    public boolean isNumberRegex(String s) {
        return P.matcher(s).matches();
    }
}
```

---

## Approach 2: Manual Single Pass

### Idea

> Walk the string with four flags: `sawDigit`, `sawDot`, `sawE`, `digitAfterE`. At every character, validate against the current state.

### Algorithm (step-by-step)

1. `sawDigit = sawDot = sawE = digitAfterE = false`. Wait -- we use `digitAfterE` only when needed.
2. For each character `c` at index `i`:
   - Digit: `sawDigit = true`. If `sawE`, `digitAfterE = true`.
   - `+`/`-`: Allowed only at index 0 or immediately after `e`/`E`.
   - `.`: Allowed only if not yet seen and no `e` yet.
   - `e`/`E`: Allowed only if not yet seen and at least one digit so far.
   - Anything else: invalid.
3. After the loop, `sawDigit` must be true. If `sawE`, `digitAfterE` must be true.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) |

### Implementation

#### Go

```go
func isNumber(s string) bool {
    sawDigit, sawDot, sawE, digitAfterE := false, false, false, true
    for i, c := range s {
        if c >= '0' && c <= '9' {
            sawDigit = true
            if sawE {
                digitAfterE = true
            }
        } else if c == '+' || c == '-' {
            if i != 0 && s[i-1] != 'e' && s[i-1] != 'E' {
                return false
            }
        } else if c == '.' {
            if sawDot || sawE {
                return false
            }
            sawDot = true
        } else if c == 'e' || c == 'E' {
            if sawE || !sawDigit {
                return false
            }
            sawE = true
            digitAfterE = false
        } else {
            return false
        }
    }
    return sawDigit && digitAfterE
}
```

#### Java

```java
class Solution {
    public boolean isNumber(String s) {
        boolean sawDigit = false, sawDot = false, sawE = false, digitAfterE = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                sawDigit = true;
                if (sawE) digitAfterE = true;
            } else if (c == '+' || c == '-') {
                if (i != 0 && s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E') return false;
            } else if (c == '.') {
                if (sawDot || sawE) return false;
                sawDot = true;
            } else if (c == 'e' || c == 'E') {
                if (sawE || !sawDigit) return false;
                sawE = true;
                digitAfterE = false;
            } else {
                return false;
            }
        }
        return sawDigit && digitAfterE;
    }
}
```

#### Python

```python
class Solution:
    def isNumber(self, s: str) -> bool:
        sawDigit = sawDot = sawE = False
        digitAfterE = True
        for i, c in enumerate(s):
            if c.isdigit():
                sawDigit = True
                if sawE: digitAfterE = True
            elif c in '+-':
                if i != 0 and s[i-1] not in 'eE': return False
            elif c == '.':
                if sawDot or sawE: return False
                sawDot = True
            elif c in 'eE':
                if sawE or not sawDigit: return False
                sawE = True
                digitAfterE = False
            else:
                return False
        return sawDigit and digitAfterE
```

### Dry Run

```text
s = "-90E3"

i=0: '-' → ok (i==0)
i=1: '9' → sawDigit=true
i=2: '0' → sawDigit=true
i=3: 'E' → sawE=true, digitAfterE=false (must see digit later)
i=4: '3' → sawDigit=true, digitAfterE=true (because sawE)

End: sawDigit && digitAfterE → true
```

---

## Approach 3: Deterministic Finite Automaton (DFA)

### Idea

> Build an explicit table of states and transitions. Each character moves the automaton to a new state. Accepting states correspond to valid numbers.

> Useful for very precise parsing or when extending the grammar later. Requires more boilerplate but is iron-clad.

### States (one possible design)

```text
0  start
1  saw sign
2  saw digit (mantissa)
3  saw dot after digits
4  saw dot first (no digits yet)
5  saw digit after dot
6  saw e/E
7  saw sign after e/E
8  saw digit in exponent
```

Accepting: 2, 3, 5, 8.

### Implementation

#### Python

```python
class Solution:
    def isNumberDFA(self, s: str) -> bool:
        # Each map: char-class -> next state. Char classes: digit, sign, dot, exp.
        transitions = [
            {'digit': 2, 'sign': 1, 'dot': 4},          # 0 start
            {'digit': 2, 'dot': 4},                     # 1 sign
            {'digit': 2, 'dot': 3, 'exp': 6},           # 2 digit (mantissa)
            {'digit': 5, 'exp': 6},                     # 3 dot after digit
            {'digit': 5},                               # 4 dot first
            {'digit': 5, 'exp': 6},                     # 5 digit after dot
            {'digit': 8, 'sign': 7},                    # 6 e/E
            {'digit': 8},                               # 7 sign after e/E
            {'digit': 8},                               # 8 digit in exp
        ]
        accepting = {2, 3, 5, 8}

        def klass(c: str):
            if c.isdigit(): return 'digit'
            if c in '+-':   return 'sign'
            if c == '.':    return 'dot'
            if c in 'eE':   return 'exp'
            return None

        state = 0
        for c in s:
            k = klass(c)
            if k is None or k not in transitions[state]:
                return False
            state = transitions[state][k]
        return state in accepting
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Regex | O(n) | O(1) | One-liner | Hides grammar |
| 2 | Single Pass + Flags | O(n) | O(1) | Clear, easy to debug | Many cases to enumerate |
| 3 | DFA Table | O(n) | O(1) | Most rigorous | Boilerplate |

### Which solution to choose?

- **In an interview:** Approach 2 -- demonstrates understanding without library help
- **In production:** Approach 1 if regex is acceptable; otherwise 2
- **On Leetcode:** All three accepted

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Bare digit | `"0"` | true | Smallest integer |
| 2 | Leading zeros | `"0089"` | true | Allowed |
| 3 | Negative decimal | `"-0.1"` | true | Sign + decimal |
| 4 | Trailing dot | `"4."` | true | digits + dot |
| 5 | Leading dot | `"-.9"` | true | sign + .digits |
| 6 | Scientific positive | `"2e10"` | true | digits + exponent |
| 7 | Scientific signed exp | `"+6e-1"` | true | sign + digit + exp + sign + digit |
| 8 | Bare dot | `"."` | false | Not a number |
| 9 | Bare e | `"e"` | false | No mantissa |
| 10 | Two signs | `"--6"` | false | Sign in middle |
| 11 | Letter contamination | `"1a"` | false | Invalid char |
| 12 | Exp without digits before | `"e3"` | false | No mantissa |
| 13 | Exp with decimal | `"99e2.5"` | false | Exponent must be integer |
| 14 | Mixed signs | `"-+3"` | false | Two signs at start |
| 15 | Empty exponent | `"1e"` | false | No digits after e |

---

## Common Mistakes

### Mistake 1: Allowing sign anywhere

```python
# WRONG — allows "1-2" or "-+3"
if c in '+-': continue  # no position check

# CORRECT
if c in '+-':
    if i != 0 and s[i-1] not in 'eE': return False
```

**Reason:** Sign characters must be at the start or directly after the exponent marker.

### Mistake 2: Allowing dot after `e`

```python
# WRONG — accepts "1e2.5"
elif c == '.':
    if sawDot: return False
    sawDot = True

# CORRECT
elif c == '.':
    if sawDot or sawE: return False
    sawDot = True
```

**Reason:** The exponent must be an integer; dots are only allowed in the mantissa.

### Mistake 3: Forgetting to require a digit after `e`

```python
# WRONG — accepts "1e"
return sawDigit

# CORRECT
return sawDigit and digitAfterE
```

**Reason:** `1e` ends mid-token; the exponent is incomplete.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/) | :yellow_circle: Medium | Parse integers from a string |
| 2 | [468. Validate IP Address](https://leetcode.com/problems/validate-ip-address/) | :yellow_circle: Medium | String validation with multiple rules |
| 3 | [10. Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | :red_circle: Hard | Full regex implementation |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Character-by-character pointer with state flags shown in real time
> - Highlight the rule that triggers on each character
> - Multiple preset inputs
> - Accept/reject output with explanation
