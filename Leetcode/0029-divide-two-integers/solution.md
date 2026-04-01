# 0029. Divide Two Integers

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Repeated Subtraction](#approach-1-repeated-subtraction)
4. [Approach 2: Exponential Search (Bit Shifting)](#approach-2-exponential-search-bit-shifting)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [29. Divide Two Integers](https://leetcode.com/problems/divide-two-integers/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Math`, `Bit Manipulation` |

### Description

> Given two integers `dividend` and `divisor`, divide two integers **without using multiplication, division, and mod operator**.
>
> The integer division should truncate toward zero, which means losing its fractional part. For example, `8.345` would be truncated to `8`, and `-2.7335` would be truncated to `-2`.
>
> Return the **quotient** after dividing `dividend` by `divisor`.
>
> **Note:** Assume we are dealing with an environment that could only store integers within the **32-bit signed integer** range: `[-2^31, 2^31 - 1]`. For this problem, if the quotient is **strictly greater than** `2^31 - 1`, then return `2^31 - 1`, and if the quotient is **strictly less than** `-2^31`, then return `-2^31`.

### Examples

```
Example 1:
Input: dividend = 10, divisor = 3
Output: 3
Explanation: 10 / 3 = 3.33333... which is truncated to 3.

Example 2:
Input: dividend = 7, divisor = -2
Output: -3
Explanation: 7 / -2 = -3.5, which is truncated to -3.

Example 3:
Input: dividend = -2147483648, divisor = -1
Output: 2147483647
Explanation: -2147483648 / -1 = 2147483648, which overflows 32-bit int.
             Return 2^31 - 1 = 2147483647.
```

### Constraints

- `-2^31 <= dividend, divisor <= 2^31 - 1`
- `divisor != 0`

---

## Problem Breakdown

### 1. What is being asked?

Implement integer division without using `*`, `/`, or `%` operators. The result must truncate toward zero and handle 32-bit signed integer overflow.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `dividend` | `int` | The number being divided |
| `divisor` | `int` | The number to divide by |

Important observations about the input:
- Both values can be **negative**
- `divisor` is never zero
- The range is `[-2^31, 2^31 - 1]` (asymmetric!)
- `dividend = -2^31` and `divisor = -1` causes overflow

### 3. What is the output?

- A single **integer** -- the truncated quotient of `dividend / divisor`
- Must be clamped to `[-2^31, 2^31 - 1]`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| No `*`, `/`, `%` | Must use addition, subtraction, and bit shifts |
| 32-bit signed range | Only one overflow case: `-2^31 / -1` |
| Truncate toward zero | `7 / -2 = -3` (not -4). Negative results round toward zero |
| `divisor != 0` | No need to handle division by zero |

### 5. Step-by-step example analysis

#### Example 1: `dividend = 10, divisor = 3`

```text
10 / 3 = ?

How many times does 3 fit into 10?
  3 * 1 = 3   (10 - 3 = 7, still >= 3)
  3 * 2 = 6   (10 - 6 = 4, still >= 3)
  3 * 3 = 9   (10 - 9 = 1, now < 3)

Answer: 3 (remainder 1)
```

#### Example 2: `dividend = 7, divisor = -2`

```text
7 / -2 = ?

Different signs -> result is negative
Work with absolute values: 7 / 2

  2 * 1 = 2   (7 - 2 = 5)
  2 * 2 = 4   (7 - 4 = 3)
  2 * 3 = 6   (7 - 6 = 1, now < 2)

|quotient| = 3, apply negative sign -> -3
```

#### Example 3: `dividend = -2147483648, divisor = -1`

```text
-2147483648 / -1 = 2147483648

But 2147483648 > 2^31 - 1 = 2147483647 (overflow!)
Clamp to 2147483647
```

### 6. Key Observations

1. **Sign handling** -- Determine the sign from the inputs, then work with absolute values.
2. **Only one overflow case** -- `-2^31 / -1 = 2^31` which exceeds `INT_MAX`. All other divisions produce results within range.
3. **Subtraction is slow** -- Subtracting divisor one at a time is O(dividend/divisor), which can be 2^31 for `dividend = 2^31, divisor = 1`.
4. **Doubling trick** -- Instead of subtracting divisor once, double it repeatedly (using bit shifts) to subtract large chunks at once.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Repeated Subtraction | Conceptually simple, subtract divisor until remainder < divisor | Educational but TLE |
| Exponential Search | Double the divisor via left shift, subtract largest power-of-2 multiples first | Optimal: O(log^2 n) |

**Chosen pattern:** `Exponential Search (Bit Shifting)`
**Reason:** By doubling the divisor, we reduce the dividend exponentially fast, achieving O(log^2 n) time.

---

## Approach 1: Repeated Subtraction

### Thought process

> The most intuitive approach: "How many times can I subtract divisor from dividend?"
> Keep subtracting and counting until the remainder is less than the divisor.
> This works but is extremely slow for large dividends with small divisors.

### Algorithm (step-by-step)

1. Handle the overflow edge case: `dividend = -2^31` and `divisor = -1` -> return `2^31 - 1`
2. Determine the sign of the result (negative if signs differ)
3. Work with absolute values of dividend and divisor
4. Repeatedly subtract divisor from dividend, incrementing quotient each time
5. Stop when remainder < divisor
6. Apply sign and return

### Pseudocode

```text
function divide(dividend, divisor):
    if dividend == -2^31 and divisor == -1:
        return 2^31 - 1

    negative = (dividend < 0) XOR (divisor < 0)
    a = abs(dividend)
    b = abs(divisor)
    quotient = 0

    while a >= b:
        a -= b
        quotient += 1

    return -quotient if negative else quotient
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(dividend / divisor) | Worst case: `2^31 / 1` = 2 billion subtractions. TLE! |
| **Space** | O(1) | Only a few variables. |

### Implementation

#### Go

```go
func divide(dividend int, divisor int) int {
    // Overflow edge case
    if dividend == math.MinInt32 && divisor == -1 {
        return math.MaxInt32
    }

    negative := (dividend < 0) != (divisor < 0)
    a, b := abs(dividend), abs(divisor)
    quotient := 0

    for a >= b {
        a -= b
        quotient++
    }

    if negative {
        return -quotient
    }
    return quotient
}
```

#### Java

```java
class Solution {
    public int divide(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        boolean negative = (dividend < 0) != (divisor < 0);
        long a = Math.abs((long) dividend);
        long b = Math.abs((long) divisor);
        int quotient = 0;

        while (a >= b) {
            a -= b;
            quotient++;
        }

        return negative ? -quotient : quotient;
    }
}
```

#### Python

```python
class Solution:
    def divide(self, dividend: int, divisor: int) -> int:
        INT_MAX = 2**31 - 1
        INT_MIN = -(2**31)

        if dividend == INT_MIN and divisor == -1:
            return INT_MAX

        negative = (dividend < 0) != (divisor < 0)
        a, b = abs(dividend), abs(divisor)
        quotient = 0

        while a >= b:
            a -= b
            quotient += 1

        return -quotient if negative else quotient
```

### Dry Run

```text
Input: dividend = 10, divisor = 3

negative = False (both positive)
a = 10, b = 3

Step 1: a = 10 >= 3 -> a = 10 - 3 = 7, quotient = 1
Step 2: a = 7  >= 3 -> a = 7 - 3  = 4, quotient = 2
Step 3: a = 4  >= 3 -> a = 4 - 3  = 1, quotient = 3
Step 4: a = 1  <  3 -> STOP

Result: 3

Total subtractions: 3
```

---

## Approach 2: Exponential Search (Bit Shifting)

### The problem with Repeated Subtraction

> Subtracting one divisor at a time is O(dividend/divisor).
> For `dividend = 2^31, divisor = 1`, that is ~2 billion operations -- TLE.
> Question: "Can we subtract larger chunks to speed this up?"

### Optimization idea

> **Double the divisor** using left shifts (`<< 1`) until it would exceed the remaining dividend.
> Then subtract that largest doubled value and add the corresponding power of 2 to the quotient.
> Repeat with the remaining dividend.
>
> **Example:** `43 / 3`
> - `3 << 0 = 3`, `3 << 1 = 6`, `3 << 2 = 12`, `3 << 3 = 24`, `3 << 4 = 48 > 43` -> stop
> - Subtract `24` (= `3 * 8`), quotient += `8`, remainder = `43 - 24 = 19`
> - `3 << 0 = 3`, `3 << 1 = 6`, `3 << 2 = 12`, `3 << 3 = 24 > 19` -> stop
> - Subtract `12` (= `3 * 4`), quotient += `4`, remainder = `19 - 12 = 7`
> - `3 << 0 = 3`, `3 << 1 = 6`, `3 << 2 = 12 > 7` -> stop
> - Subtract `6` (= `3 * 2`), quotient += `2`, remainder = `7 - 6 = 1`
> - `1 < 3` -> STOP
> - Total quotient = `8 + 4 + 2 = 14`

### Algorithm (step-by-step)

1. Handle the overflow edge case: `dividend = -2^31` and `divisor = -1` -> return `2^31 - 1`
2. Determine the sign of the result
3. Work with absolute values (using `long` / 64-bit to avoid overflow with `-2^31`)
4. While `remainder >= divisor`:
   a. Start with `temp = divisor`, `multiple = 1`
   b. While `remainder >= temp << 1` (and no overflow): double `temp` and `multiple`
   c. Subtract `temp` from remainder, add `multiple` to quotient
5. Apply sign, clamp to 32-bit range, and return

### Pseudocode

```text
function divide(dividend, divisor):
    if dividend == -2^31 and divisor == -1:
        return 2^31 - 1

    negative = (dividend < 0) XOR (divisor < 0)
    a = abs(dividend)    // use 64-bit
    b = abs(divisor)     // use 64-bit
    quotient = 0

    while a >= b:
        temp = b
        multiple = 1
        while a >= temp << 1:
            temp <<= 1
            multiple <<= 1
        a -= temp
        quotient += multiple

    return -quotient if negative else quotient
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log^2 n) | Outer loop runs O(log n) times (quotient halves each iteration). Inner loop runs O(log n) to find the largest power. |
| **Space** | O(1) | Only a few variables. |

### Implementation

#### Go

```go
func divide(dividend int, divisor int) int {
    // Overflow edge case
    if dividend == math.MinInt32 && divisor == -1 {
        return math.MaxInt32
    }

    // Determine sign
    negative := (dividend < 0) != (divisor < 0)

    // Work with absolute values (use int64 to handle -2^31)
    a := int64(dividend)
    if a < 0 {
        a = -a
    }
    b := int64(divisor)
    if b < 0 {
        b = -b
    }

    quotient := int64(0)

    for a >= b {
        temp := b
        multiple := int64(1)
        // Double temp until it would exceed a
        for a >= temp<<1 {
            temp <<= 1
            multiple <<= 1
        }
        a -= temp
        quotient += multiple
    }

    if negative {
        quotient = -quotient
    }

    // Clamp to 32-bit range
    if quotient > math.MaxInt32 {
        return math.MaxInt32
    }
    if quotient < math.MinInt32 {
        return math.MinInt32
    }
    return int(quotient)
}
```

#### Java

```java
class Solution {
    public int divide(int dividend, int divisor) {
        // Overflow edge case
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        // Determine sign
        boolean negative = (dividend < 0) != (divisor < 0);

        // Work with absolute values (use long to handle -2^31)
        long a = Math.abs((long) dividend);
        long b = Math.abs((long) divisor);
        long quotient = 0;

        while (a >= b) {
            long temp = b;
            long multiple = 1;
            // Double temp until it would exceed a
            while (a >= temp << 1) {
                temp <<= 1;
                multiple <<= 1;
            }
            a -= temp;
            quotient += multiple;
        }

        return negative ? (int) -quotient : (int) quotient;
    }
}
```

#### Python

```python
class Solution:
    def divide(self, dividend: int, divisor: int) -> int:
        INT_MAX = 2**31 - 1
        INT_MIN = -(2**31)

        # Overflow edge case
        if dividend == INT_MIN and divisor == -1:
            return INT_MAX

        # Determine sign
        negative = (dividend < 0) != (divisor < 0)

        # Work with absolute values
        a, b = abs(dividend), abs(divisor)
        quotient = 0

        while a >= b:
            temp, multiple = b, 1
            # Double temp until it would exceed a
            while a >= temp << 1:
                temp <<= 1
                multiple <<= 1
            a -= temp
            quotient += multiple

        return -quotient if negative else quotient
```

### Dry Run

```text
Input: dividend = 43, divisor = 3

negative = False
a = 43, b = 3, quotient = 0

--- Outer iteration 1 ---
  temp = 3, multiple = 1
  43 >= 3<<1 = 6?   Yes -> temp=6,  multiple=2
  43 >= 6<<1 = 12?  Yes -> temp=12, multiple=4
  43 >= 12<<1 = 24? Yes -> temp=24, multiple=8
  43 >= 24<<1 = 48? No  -> STOP
  a = 43 - 24 = 19, quotient = 0 + 8 = 8

--- Outer iteration 2 ---
  temp = 3, multiple = 1
  19 >= 6?  Yes -> temp=6,  multiple=2
  19 >= 12? Yes -> temp=12, multiple=4
  19 >= 24? No  -> STOP
  a = 19 - 12 = 7, quotient = 8 + 4 = 12

--- Outer iteration 3 ---
  temp = 3, multiple = 1
  7 >= 6?  Yes -> temp=6, multiple=2
  7 >= 12? No  -> STOP
  a = 7 - 6 = 1, quotient = 12 + 2 = 14

--- Outer iteration 4 ---
  a = 1 < b = 3 -> STOP

Result: 14
Verification: 43 / 3 = 14.333... -> truncated to 14 ✓
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Repeated Subtraction | O(n) | O(1) | Simple, easy to understand | TLE for large inputs |
| 2 | Exponential Search | O(log^2 n) | O(1) | Fast, passes all test cases | Slightly more complex logic |

Where `n = |dividend / divisor|`.

### Which solution to choose?

- **In an interview:** Approach 2 (Exponential Search) -- demonstrates understanding of bit manipulation and optimization
- **In production:** Use the language's built-in `/` operator (this is a puzzle problem)
- **On Leetcode:** Approach 2 -- only O(log^2 n) passes within the time limit
- **For learning:** Both -- Repeated Subtraction to understand the concept, Exponential Search to learn bit shifting patterns

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Overflow case | `dividend=-2147483648, divisor=-1` | `2147483647` | Result exceeds INT_MAX, clamp |
| 2 | Negative dividend | `dividend=7, divisor=-2` | `-3` | Truncate toward zero |
| 3 | Negative divisor | `dividend=-7, divisor=2` | `-3` | Truncate toward zero |
| 4 | Both negative | `dividend=-7, divisor=-2` | `3` | Negatives cancel out |
| 5 | Divisor = 1 | `dividend=100, divisor=1` | `100` | Identity division |
| 6 | Divisor = -1 | `dividend=100, divisor=-1` | `-100` | Negate the result |
| 7 | Dividend = 0 | `dividend=0, divisor=5` | `0` | Zero divided by anything is 0 |
| 8 | Dividend < divisor | `dividend=1, divisor=3` | `0` | Truncated to 0 |
| 9 | Dividend = MIN_INT | `dividend=-2147483648, divisor=1` | `-2147483648` | Large negative, divisor=1 |
| 10 | Equal values | `dividend=7, divisor=7` | `1` | Any number / itself = 1 |

---

## Common Mistakes

### Mistake 1: Not handling the `-2^31` absolute value overflow

```python
# WRONG -- abs(-2147483648) overflows in 32-bit int!
a = abs(dividend)  # In Java/Go, abs(Integer.MIN_VALUE) = Integer.MIN_VALUE

# CORRECT -- cast to long/int64 first
a = abs((long) dividend)  // Java
a = int64(dividend); if a < 0 { a = -a }  // Go
```

**Reason:** `-2^31` has no positive counterpart in 32-bit signed integers since `2^31 > INT_MAX`.

### Mistake 2: Infinite loop when doubling temp

```python
# WRONG -- temp<<1 can overflow, causing infinite loop
while a >= temp << 1:
    temp <<= 1
    multiple <<= 1

# SAFER -- add overflow guard
while a >= temp << 1 and temp << 1 > 0:
    temp <<= 1
    multiple <<= 1
```

**Reason:** In languages with fixed-width integers, shifting left can cause overflow to negative, breaking the comparison.

### Mistake 3: Wrong sign handling

```python
# WRONG -- using multiplication to determine sign
negative = dividend * divisor < 0  # Can overflow!

# CORRECT -- use XOR of sign bits
negative = (dividend < 0) != (divisor < 0)
```

**Reason:** Multiplying two large negative numbers can overflow.

### Mistake 4: Not truncating toward zero

```python
# WRONG -- Python's // truncates toward negative infinity
result = abs(dividend) // abs(divisor)  # For 7 // -2, Python gives -4

# CORRECT -- work with absolute values, apply sign manually
quotient = abs(dividend) // abs(divisor)
result = -quotient if negative else quotient  # -3, correct
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [50. Pow(x, n)](https://leetcode.com/problems/powx-n/) | :yellow_circle: Medium | Exponential search / binary exponentiation |
| 2 | [69. Sqrt(x)](https://leetcode.com/problems/sqrtx/) | :green_circle: Easy | Binary search on answer |
| 3 | [166. Fraction to Recurring Decimal](https://leetcode.com/problems/fraction-to-recurring-decimal/) | :yellow_circle: Medium | Division with edge cases |
| 4 | [371. Sum of Two Integers](https://leetcode.com/problems/sum-of-two-integers/) | :yellow_circle: Medium | Bit manipulation arithmetic |
| 5 | [67. Add Binary](https://leetcode.com/problems/add-binary/) | :green_circle: Easy | Arithmetic without built-in operators |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - Visual representation of dividend being reduced by doubling divisor
> - Bit shifting visualization showing `divisor << 0`, `divisor << 1`, `divisor << 2`, ...
> - Step-by-step decomposition of quotient into powers of 2
> - Preset examples including the overflow edge case
