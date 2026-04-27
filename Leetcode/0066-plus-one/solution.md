# 0066. Plus One

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Convert to Integer (Naive)](#approach-1-convert-to-integer-naive)
4. [Approach 2: Walk From End with Carry](#approach-2-walk-from-end-with-carry)
5. [Approach 3: Early-Exit Optimization](#approach-3-early-exit-optimization)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [66. Plus One](https://leetcode.com/problems/plus-one/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Array`, `Math` |

### Description

> You are given a **large integer** represented as an integer array `digits`, where each `digits[i]` is the `i`-th digit of the integer. The digits are ordered from most significant to least significant in left-to-right order. The large integer does not contain any leading `0`'s.
>
> Increment the large integer by one and return *the resulting array of digits*.

### Examples

```
Example 1:
Input: digits = [1,2,3]
Output: [1,2,4]

Example 2:
Input: digits = [4,3,2,1]
Output: [4,3,2,2]

Example 3:
Input: digits = [9]
Output: [1,0]
```

### Constraints

- `1 <= digits.length <= 100`
- `0 <= digits[i] <= 9`
- `digits` does not contain any leading `0`'s.

---

## Problem Breakdown

### 1. What is being asked?

Treat `digits` as the decimal representation of a number, add `1`, and return the new digit array.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `digits` | `int[]` | Decimal digits, MSB first, no leading zeros |

### 3. What is the output?

A new digit array (still MSB first, no leading zeros) representing `original + 1`.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 100` | Number can have up to 100 digits — too big for `int64` (which fits at most 19 digits). Must operate digit by digit |
| No leading zeros | Output must obey the same rule |

### 5. Step-by-step example analysis

#### Example 3: `[9]`

```text
Walk from end:
  i = 0 (only digit): 9 + 1 = 10. Write 0, carry 1.
Carry remains → prepend 1.

Result: [1, 0].
```

#### `[1, 9, 9]`

```text
i = 2: 9 + 1 = 10 → write 0, carry 1.
i = 1: 9 + 1 = 10 → write 0, carry 1.
i = 0: 1 + 1 = 2 → write 2, carry 0.

Result: [2, 0, 0].
```

### 6. Key Observations

1. **Carry propagates only through trailing 9s** -- The first non-9 digit from the right absorbs the increment and we can stop.
2. **All-9s case** -- Result has one extra digit at the front. Pre-allocate or prepend.
3. **Big numbers** -- 100 digits exceed any 64-bit integer. Avoid converting to `int`/`long`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Right-to-left scan with carry | Standard for arbitrary-length arithmetic |

**Chosen pattern:** `Walk From End with Carry`.

---

## Approach 1: Convert to Integer (Naive)

### Idea

> Build a number by joining digits, add 1, and split back. Works for short arrays, fails for long ones.

> Listed only as a contrast.

### Implementation

#### Python

```python
class Solution:
    def plusOneInt(self, digits: List[int]) -> List[int]:
        # WARNING: only valid for arrays where the number fits in built-in int.
        # Python int is unbounded, so this works in Python but not in Go/Java.
        n = int(''.join(map(str, digits))) + 1
        return [int(c) for c in str(n)]
```

> In languages with fixed-width integers, this approach fails for `n > 19` digits.

---

## Approach 2: Walk From End with Carry

### Algorithm (step-by-step)

1. `carry = 1` (we are adding one).
2. For `i = n - 1` down to `0`:
   - `sum = digits[i] + carry`. Set `digits[i] = sum % 10`. Update `carry = sum / 10`.
   - If `carry == 0`, break (optimization).
3. If `carry > 0`, prepend `carry` to the array.

### Pseudocode

```text
carry = 1
for i in n-1 downto 0:
    s = digits[i] + carry
    digits[i] = s % 10
    carry = s // 10
    if carry == 0: break
if carry > 0:
    return [1] + digits
return digits
```

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) (or O(n) when prepending) |

### Implementation

#### Go

```go
func plusOne(digits []int) []int {
    carry := 1
    for i := len(digits) - 1; i >= 0 && carry > 0; i-- {
        s := digits[i] + carry
        digits[i] = s % 10
        carry = s / 10
    }
    if carry > 0 {
        return append([]int{carry}, digits...)
    }
    return digits
}
```

#### Java

```java
class Solution {
    public int[] plusOne(int[] digits) {
        int carry = 1;
        for (int i = digits.length - 1; i >= 0 && carry > 0; i--) {
            int s = digits[i] + carry;
            digits[i] = s % 10;
            carry = s / 10;
        }
        if (carry > 0) {
            int[] out = new int[digits.length + 1];
            out[0] = carry;
            System.arraycopy(digits, 0, out, 1, digits.length);
            return out;
        }
        return digits;
    }
}
```

#### Python

```python
class Solution:
    def plusOne(self, digits: List[int]) -> List[int]:
        carry = 1
        for i in range(len(digits) - 1, -1, -1):
            if carry == 0: break
            s = digits[i] + carry
            digits[i] = s % 10
            carry = s // 10
        if carry > 0:
            return [carry] + digits
        return digits
```

### Dry Run

```text
digits = [1, 2, 3]
carry = 1

i = 2: s = 3 + 1 = 4 → digits[2] = 4, carry = 0; break
carry = 0 → return [1, 2, 4]
```

```text
digits = [9, 9]
carry = 1

i = 1: s = 9 + 1 = 10 → digits[1] = 0, carry = 1
i = 0: s = 9 + 1 = 10 → digits[0] = 0, carry = 1
carry > 0 → prepend → [1, 0, 0]
```

---

## Approach 3: Early-Exit Optimization

### Idea

> The first non-9 digit (scanning right-to-left) absorbs the carry. Increment it, set every digit after it to 0. If all are 9s, allocate `n + 1` zeros and set the first to 1.

### Implementation

#### Python

```python
class Solution:
    def plusOneEarly(self, digits: List[int]) -> List[int]:
        n = len(digits)
        for i in range(n - 1, -1, -1):
            if digits[i] != 9:
                digits[i] += 1
                for j in range(i + 1, n): digits[j] = 0
                return digits
        return [1] + [0] * n
```

#### Go

```go
func plusOneEarly(digits []int) []int {
    n := len(digits)
    for i := n - 1; i >= 0; i-- {
        if digits[i] != 9 {
            digits[i]++
            for j := i + 1; j < n; j++ {
                digits[j] = 0
            }
            return digits
        }
    }
    out := make([]int, n+1)
    out[0] = 1
    return out
}
```

> Same big-O as Approach 2 but avoids the inner `% 10` arithmetic and reads more directly as "find non-9, bump, zero out".

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Convert to int | O(n) | O(n) | One-liner (Python) | Fails for big n in fixed-width languages |
| 2 | Walk + Carry | O(n) | O(1)/O(n) | Robust | Slightly verbose |
| 3 | Early Exit | O(n) | O(1)/O(n) | Reads naturally | Same big-O as 2 |

### Which solution to choose?

- **In an interview:** Approach 2 or 3
- **In production:** Approach 2
- **On Leetcode:** Approach 2

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Single zero | `[0]` | `[1]` | 0 + 1 = 1 |
| 2 | Single nine | `[9]` | `[1, 0]` | 9 + 1 → carry |
| 3 | All nines | `[9, 9, 9]` | `[1, 0, 0, 0]` | Cascade carry |
| 4 | Trailing zeros | `[1, 0, 0]` | `[1, 0, 1]` | No carry |
| 5 | Trailing nines | `[1, 9, 9]` | `[2, 0, 0]` | Inner carry only |
| 6 | Long number | `[4,3,2,1]` | `[4,3,2,2]` | Standard |
| 7 | 99-digit | length 100 | length 100 or 101 | No fixed-int support |

---

## Common Mistakes

### Mistake 1: Using a fixed-width integer

```java
// WRONG — overflows for n > 19
long n = 0;
for (int d : digits) n = n * 10 + d;
n += 1;
```

**Reason:** Constraint allows 100 digits — far beyond `long` range. Operate on the digit array directly.

### Mistake 2: Forgetting the all-9s case

```python
# WRONG — just returns digits even when carry remains
for i in ...:
    s = digits[i] + carry
    digits[i] = s % 10
    carry = s // 10
return digits   # missing prepend

# CORRECT
if carry > 0:
    return [1] + digits
return digits
```

**Reason:** When the input is `[9, 9, ..., 9]`, the carry propagates beyond index 0 and must be added as a new most-significant digit.

### Mistake 3: Not breaking early

```python
# Works but wastes time
for i in range(n - 1, -1, -1):
    s = digits[i] + carry
    ...
# All other iterations after carry==0 do nothing; safe but inefficient.
```

**Reason:** Adding `if carry == 0: break` keeps O(n) worst-case but lets average case finish early.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [67. Add Binary](https://leetcode.com/problems/add-binary/) | :green_circle: Easy | Same idea, base 2 |
| 2 | [415. Add Strings](https://leetcode.com/problems/add-strings/) | :green_circle: Easy | Add two arbitrary-length numbers |
| 3 | [989. Add to Array-Form of Integer](https://leetcode.com/problems/add-to-array-form-of-integer/) | :green_circle: Easy | Add an int to a digit array |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Digit row with carry indicator
> - Step-by-step right-to-left walk
> - Highlight current digit, modulo result, carry propagation
> - Final prepend animation when needed
