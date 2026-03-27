# 0007. Reverse Integer

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: String Conversion](#approach-1-string-conversion)
4. [Approach 2: Mathematical Digit Pop (Optimal)](#approach-2-mathematical-digit-pop-optimal)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [7. Reverse Integer](https://leetcode.com/problems/reverse-integer/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `Math` |

### Description

> Given a signed 32-bit integer `x`, return `x` with its digits reversed. If reversing `x` causes the value to go outside the signed 32-bit integer range `[-2^31, 2^31 - 1]`, return `0`.
>
> **Assume the environment does not allow you to store 64-bit integers (signed or unsigned).**

### Examples

```
Example 1:
Input:  x = 123
Output: 321

Example 2:
Input:  x = -123
Output: -321

Example 3:
Input:  x = 120
Output: 21
```

### Constraints

- `-2^31 <= x <= 2^31 - 1`

---

## Problem Breakdown

### 1. What is being asked?

Reverse the decimal digits of integer `x` and return the result as an integer. If the reversed value overflows a signed 32-bit integer, return `0` instead.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `x` | `int` (32-bit) | Signed integer in range [-2^31, 2^31 - 1] |

Important observations:
- `x` can be negative — the minus sign stays in front, only digits are reversed.
- `x` can end in zeros — trailing zeros become leading zeros after reversal and are dropped (e.g., `120 → 021 → 21`).
- The reversed value may overflow 32 bits — must detect and return `0`.

### 3. What is the output?

- The integer with digits reversed, OR
- `0` if the reversed value is outside `[-2^31, 2^31 - 1]`.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `[-2^31, 2^31 - 1]` | Must detect overflow without using 64-bit intermediate |
| `x` can be negative | Sign is preserved; only digits are reversed |
| Assumption: no 64-bit integers | Forces us to check overflow digit-by-digit, not by comparing to a 64-bit result |

### 5. Step-by-step example analysis

#### Example 1: `x = 123`

```text
123 → reverse digits → 321
321 is within [-2147483648, 2147483647] ✅
Output: 321
```

#### Example 2: `x = -123`

```text
Sign is negative. Magnitude = 123 → reversed = 321
Apply sign back: -321
-321 is within range ✅
Output: -321
```

#### Example 3: `x = 120`

```text
120 → digits: 1, 2, 0
Reversed order: 0, 2, 1 → "021" → drops leading zero → 21
Output: 21
```

#### Overflow example: `x = 1534236469`

```text
Reversed digits: 9646324351
2^31 - 1 = 2147483647
9646324351 > 2147483647 → overflow!
Output: 0
```

### 6. Key Observations

1. **Digit extraction** — `digit = x % 10` gives the last digit; `x /= 10` removes it. Repeat until `x == 0`.
2. **Sign handling** — In Go/Java, `%` preserves sign (`-123 % 10 = -3`). In Python, `%` always returns non-negative, so handle sign separately.
3. **Overflow detection** — Before pushing a new digit: check if `rev > INT_MAX / 10` (would overflow on multiplication), or `rev == INT_MAX / 10` and the digit exceeds the last digit of `INT_MAX` (7 for positive, -8 for negative).
4. **Trailing zeros disappear** — Because `rev` starts at 0 and we multiply, leading zeros in the reversed string simply never get pushed.

### 7. Pattern identification

| Pattern | Why it fits | Notes |
|---|---|---|
| String reversal | Simple but requires 64-bit result for overflow check | Violates problem's no-64-bit constraint |
| Mathematical digit pop | Works digit-by-digit, O(1) space, inline overflow check | Optimal solution |

**Chosen pattern:** `Mathematical Digit Pop (Approach 2)` — satisfies the no-64-bit constraint and is O(1) space.

---

## Approach 1: String Conversion

### Thought process

> Convert `x` to a string, reverse the string, strip the sign, then convert back to integer.
> Check if the result fits in 32 bits.
>
> **Limitation:** The reversed string could represent a number larger than `long` in some languages. In Python this is fine (arbitrary precision), but in Java/Go it requires using `long`/`int64` for the intermediate check — which violates the problem's spirit (and potentially the constraint in some environments).

### Algorithm (step-by-step)

1. Record sign: `sign = -1` if `x < 0`, else `1`.
2. Work with `abs(x)` as a string: `s = str(abs(x))`.
3. Reverse `s`.
4. Convert reversed string back to integer.
5. If result > INT_MAX, return `0`.
6. Return `sign * result`.

### Pseudocode

```text
function reverse(x):
    sign = -1 if x < 0 else 1
    s = str(abs(x))
    reversed_s = reverse(s)
    result = int(reversed_s)
    if result > 2^31 - 1:
        return 0
    return sign * result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log x) | Number of digits = O(log x); string ops are proportional |
| **Space** | O(log x) | String representation of x uses O(log x) characters |

### Implementation

#### Go

```go
import (
    "math"
    "strconv"
)

// reverse — String Conversion approach
// Time: O(log x), Space: O(log x)
func reverseStr(x int) int {
    sign := 1
    if x < 0 {
        sign = -1
        x = -x
    }

    s := strconv.Itoa(x)

    // Reverse the string
    runes := []rune(s)
    for i, j := 0, len(runes)-1; i < j; i, j = i+1, j-1 {
        runes[i], runes[j] = runes[j], runes[i]
    }

    result, err := strconv.Atoi(string(runes))
    if err != nil || result > math.MaxInt32 {
        return 0
    }
    return sign * result
}
```

#### Java

```java
// reverse — String Conversion approach
// Time: O(log x), Space: O(log x)
public int reverseStr(int x) {
    int sign = x < 0 ? -1 : 1;
    String s = String.valueOf(Math.abs((long) x)); // cast to avoid abs(MIN_VALUE) bug
    String reversed = new StringBuilder(s).reverse().toString();
    long result = Long.parseLong(reversed);
    if (result > Integer.MAX_VALUE) return 0;
    return sign * (int) result;
}
```

#### Python

```python
def reverse_str(self, x: int) -> int:
    sign = -1 if x < 0 else 1
    reversed_str = str(abs(x))[::-1]
    result = int(reversed_str)
    if result > 2**31 - 1:
        return 0
    return sign * result
```

### Dry Run

```text
Input: x = -123

1. sign = -1, x = 123
2. s = "123"
3. reversed = "321"
4. result = 321
5. 321 <= 2147483647 ✅
6. return -1 * 321 = -321
```

---

## Approach 2: Mathematical Digit Pop (Optimal)

### Thought process

> Instead of strings, work directly on the number mathematically.
> Use `% 10` to "pop" (extract) the last digit and `/ 10` to remove it.
> Use `* 10 + digit` to "push" the digit onto the reversed number.
>
> The critical insight: **check for overflow BEFORE pushing the digit**.
> If `rev > INT_MAX / 10`, then `rev * 10` will already overflow.
> If `rev == INT_MAX / 10`, then only `digit > 7` (for positive) causes overflow.

### Algorithm (step-by-step)

1. Initialize `rev = 0`.
2. While `x != 0`:
   a. `digit = x % 10` — pop last digit.
   b. `x /= 10` — remove last digit from x.
   c. **Overflow check:**
      - If `rev > INT_MAX / 10` OR (`rev == INT_MAX / 10` AND `digit > 7`) → return `0`.
      - If `rev < INT_MIN / 10` OR (`rev == INT_MIN / 10` AND `digit < -8`) → return `0`.
   d. `rev = rev * 10 + digit` — push digit.
3. Return `rev`.

### Pseudocode

```text
function reverse(x):
    rev = 0
    while x != 0:
        digit = x % 10
        x = x / 10
        if rev > INT_MAX / 10 or (rev == INT_MAX / 10 and digit > 7):
            return 0
        if rev < INT_MIN / 10 or (rev == INT_MIN / 10 and digit < -8):
            return 0
        rev = rev * 10 + digit
    return rev
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log x) | At most 10 iterations (digits in a 32-bit integer) |
| **Space** | O(1) | Only `rev`, `digit`, and `x` — no strings, no arrays |

### Implementation

#### Go

```go
import "math"

func reverse(x int) int {
    rev := 0
    for x != 0 {
        digit := x % 10
        x /= 10

        if rev > math.MaxInt32/10 || (rev == math.MaxInt32/10 && digit > 7) {
            return 0
        }
        if rev < math.MinInt32/10 || (rev == math.MinInt32/10 && digit < -8) {
            return 0
        }

        rev = rev*10 + digit
    }
    return rev
}
```

#### Java

```java
public int reverse(int x) {
    int rev = 0;
    while (x != 0) {
        int digit = x % 10;
        x /= 10;

        if (rev > Integer.MAX_VALUE / 10 ||
           (rev == Integer.MAX_VALUE / 10 && digit > 7)) return 0;
        if (rev < Integer.MIN_VALUE / 10 ||
           (rev == Integer.MIN_VALUE / 10 && digit < -8)) return 0;

        rev = rev * 10 + digit;
    }
    return rev;
}
```

#### Python

```python
def reverse(self, x: int) -> int:
    INT_MAX = 2147483647
    sign = -1 if x < 0 else 1
    x = abs(x)
    rev = 0

    while x != 0:
        digit = x % 10
        x //= 10

        if rev > INT_MAX // 10 or (rev == INT_MAX // 10 and digit > 7):
            return 0

        rev = rev * 10 + digit

    return sign * rev
```

### Dry Run

```text
Input: x = 123, INT_MAX = 2147483647, INT_MAX/10 = 214748364

rev=0, x=123
  digit = 123 % 10 = 3,  x = 12
  overflow? 0 > 214748364? No.  0 == 214748364 and 3 > 7? No.
  rev = 0 * 10 + 3 = 3

rev=3, x=12
  digit = 12 % 10 = 2,   x = 1
  overflow? 3 > 214748364? No.
  rev = 3 * 10 + 2 = 32

rev=32, x=1
  digit = 1 % 10 = 1,    x = 0
  overflow? 32 > 214748364? No.
  rev = 32 * 10 + 1 = 321

x == 0, loop ends.
return 321 ✅
```

```text
Overflow example: x = 1534236469

rev=0    digit=9  x=153423646 → rev=9
rev=9    digit=6  x=15342364  → rev=96
rev=96   digit=4  x=1534236   → rev=964
rev=964  digit=6  x=153423    → rev=9646
rev=9646 digit=3  x=15342     → rev=96463
rev=96463  digit=2 x=1534     → rev=964632
rev=964632 digit=4 x=153      → rev=9646324
rev=9646324 digit=3 x=15      → rev=96463243
rev=96463243 digit=5 x=1
  overflow check: 96463243 > 214748364? No.
  96463243 == 214748364? No.
  rev = 96463243*10 + 5 = 964632435

rev=964632435 digit=1 x=0
  overflow check: 964632435 > 214748364? YES! → return 0 ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | String Conversion | O(log x) | O(log x) | Easy to understand and code | Uses string heap allocation; requires 64-bit intermediate in Java/Go |
| 2 | Mathematical Digit Pop | O(log x) | O(1) | No extra memory, satisfies "no 64-bit" constraint | Overflow check logic requires careful reasoning |

### Which solution to choose?

- **In an interview:** Approach 2 (Mathematical) — demonstrates understanding of overflow and integer arithmetic. Interviewers specifically test this.
- **On Leetcode:** Approach 2 — satisfies the "no 64-bit integers" environment constraint.
- **In Python (casual use):** Approach 1 (String) — Python has arbitrary precision integers, so overflow is trivially handled with a simple comparison at the end.

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Positive no overflow | `123` | `321` | Normal case |
| 2 | Negative no overflow | `-123` | `-321` | Sign preserved, digits reversed |
| 3 | Trailing zeros | `120` | `21` | Leading zero after reversal dropped |
| 4 | Single digit | `5` | `5` | One digit reversed is itself |
| 5 | Zero | `0` | `0` | Zero reversed is zero |
| 6 | Positive overflow | `1534236469` | `0` | 9646324351 > 2^31 - 1 |
| 7 | Negative overflow | `-1534236469` | `0` | -9646324351 < -2^31 |
| 8 | INT_MAX | `2147483647` | `0` | 7463847412 > INT_MAX |
| 9 | INT_MIN | `-2147483648` | `0` | 8463847412 > INT_MAX magnitude |

---

## Common Mistakes

### Mistake 1: Using 64-bit intermediate without noting the constraint

```java
// ❌ May violate problem constraint ("no 64-bit integers")
long reversed = Long.parseLong(new StringBuilder(String.valueOf(Math.abs(x))).reverse().toString());
if (reversed > Integer.MAX_VALUE || reversed < Integer.MIN_VALUE) return 0;

// ✅ CORRECT — detect overflow before it happens, using only 32-bit arithmetic
```

**Reason:** The problem explicitly states the environment does not support 64-bit integers. The mathematical approach handles this correctly.

### Mistake 2: Python `%` behavior with negatives

```python
# ❌ WRONG — treating Python % the same as Java/Go
x = -123
digit = x % 10   # Python gives 7, not -3!
# This produces incorrect reversed digits for negative numbers

# ✅ CORRECT — work with abs(x) and restore sign at the end
sign = -1 if x < 0 else 1
x = abs(x)
# ... reverse x ...
return sign * rev
```

**Reason:** Python's `%` operator always returns a non-negative result when the divisor is positive (`-123 % 10 = 7` in Python, but `-3` in Java/Go). Always use `abs(x)` in Python for digit extraction.

### Mistake 3: Wrong overflow boundary digits

```python
# ❌ WRONG — using incorrect last digits for INT_MAX / INT_MIN
if rev == INT_MAX // 10 and digit > 8:   # should be 7, not 8
    return 0

# ✅ CORRECT — INT_MAX = 2147483647, last digit is 7
if rev == INT_MAX // 10 and digit > 7:
    return 0
# INT_MIN = -2147483648, last digit is -8
if rev == INT_MIN // 10 and digit < -8:
    return 0
```

**Reason:** `INT_MAX = 2147483647` → last digit is `7`. `INT_MIN = -2147483648` → last digit is `-8`. Getting these wrong causes false overflow returns or missed overflows.

### Mistake 4: Not handling `x = 0` or single-digit numbers

```python
# ❌ Edge case concern: what if x=0?
# The while loop condition "while x != 0" correctly handles this — rev stays 0.
# return rev → returns 0. ✅

# ❌ Edge case: x=5 → single digit
# Loop runs once: digit=5, x=0, rev=5. Correct. ✅
# No special case needed.
```

**Reason:** Both cases are naturally handled by the algorithm — no guard needed.

### Mistake 5: `abs(Integer.MIN_VALUE)` overflow in Java

```java
// ❌ WRONG in Java — Math.abs(Integer.MIN_VALUE) overflows!
// Integer.MIN_VALUE = -2147483648
// Math.abs(-2147483648) = -2147483648 (still negative in 32-bit!)
int abs = Math.abs(x);  // broken for x = Integer.MIN_VALUE

// ✅ CORRECT — the mathematical digit pop approach avoids abs() entirely
// When x = Integer.MIN_VALUE, the first digit popped is:
// x % 10 = -2147483648 % 10 = -8
// rev = 0, rev < MIN_VALUE/10 check → -8 < -8 is false
// but -8 == -8 and digit (-8) < -8 is false too
// Then we push: rev = -8. Next digit...
// Actually rev will eventually exceed INT_MIN/10 check → return 0.
// This is correct behavior since reversed MIN_VALUE overflows.
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/) | 🟡 Medium | Integer parsing with overflow detection |
| 2 | [9. Palindrome Number](https://leetcode.com/problems/palindrome-number/) | 🟢 Easy | Digit reversal logic (reverse half the digits) |
| 3 | [190. Reverse Bits](https://leetcode.com/problems/reverse-bits/) | 🟢 Easy | Bit-level reversal, similar pop/push pattern |
| 4 | [43. Multiply Strings](https://leetcode.com/problems/multiply-strings/) | 🟡 Medium | Large integer arithmetic without 64-bit |
| 5 | [29. Divide Two Integers](https://leetcode.com/problems/divide-two-integers/) | 🟡 Medium | Overflow detection in integer arithmetic |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **String** tab — Shows the string reversal process: character array flip, sign handling, overflow check.
> - **Math** tab — Digit-by-digit pop/push animation: extract last digit with `% 10`, build reversed number with `* 10 + digit`, overflow guard highlighted in red when triggered.
> - **Overflow** tab — Step-by-step trace of an overflow case showing exactly when the guard condition fires.
