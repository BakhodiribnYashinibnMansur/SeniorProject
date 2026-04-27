# 0067. Add Binary

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Convert to Integer (Naive)](#approach-1-convert-to-integer-naive)
4. [Approach 2: Walk Both From End With Carry](#approach-2-walk-both-from-end-with-carry)
5. [Approach 3: Bit Manipulation (XOR + AND)](#approach-3-bit-manipulation-xor--and)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [67. Add Binary](https://leetcode.com/problems/add-binary/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Math`, `String`, `Bit Manipulation`, `Simulation` |

### Description

> Given two binary strings `a` and `b`, return *their sum as a binary string*.

### Examples

```
Example 1:
Input: a = "11", b = "1"
Output: "100"

Example 2:
Input: a = "1010", b = "1011"
Output: "10101"
```

### Constraints

- `1 <= a.length, b.length <= 10^4`
- `a` and `b` consist only of `'0'` or `'1'` characters.
- Each string does not contain leading zeros except for the zero itself.

---

## Problem Breakdown

### 1. What is being asked?

Add two binary numbers given as strings. Return the sum, also as a binary string.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `a` | `str` | Binary string |
| `b` | `str` | Binary string |

### 3. What is the output?

A binary string representing `a + b`.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| Length up to 10^4 | Cannot use 64-bit integers (~64 bits max) -- must do digit-wise |
| Only `0`/`1` | Carry can only be 0 or 1 in binary |
| No leading zeros | Output must respect this rule too (just don't pad it) |

### 5. Step-by-step example analysis

#### Example 2: `a = "1010", b = "1011"`

```text
  1 0 1 0
+ 1 0 1 1
---------

i_a = 3, i_b = 3, carry = 0:
  bit_a=0, bit_b=1, sum = 1 → write '1', carry = 0
i_a = 2, i_b = 2, carry = 0:
  bit_a=1, bit_b=1, sum = 2 → write '0', carry = 1
i_a = 1, i_b = 1, carry = 1:
  bit_a=0, bit_b=0, sum = 1 → write '1', carry = 0
i_a = 0, i_b = 0, carry = 0:
  bit_a=1, bit_b=1, sum = 2 → write '0', carry = 1
i_a = -1, i_b = -1, carry = 1:
  sum = 1 → write '1', carry = 0

Result (reverse): "10101"
```

### 6. Key Observations

1. **Two pointers from the end** -- Standard arithmetic addition with carry.
2. **Carry can only be 0 or 1** -- Maximum digit sum is `1 + 1 + 1 = 3` → `'1'` written, carry = 1.
3. **Different lengths** -- Treat the shorter string as padded with `0`s on the left.
4. **Leftover carry** -- After both strings are exhausted, append the carry if it is 1.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Right-to-left digit walk with carry | Classic for arbitrary-length arithmetic |
| Bit manipulation | XOR = sum without carry, AND = carry |

**Chosen pattern:** `Walk Both From End With Carry`.

---

## Approach 1: Convert to Integer (Naive)

### Idea

> Parse to integers, add, format back. Works only if both fit in 64-bit (≤ 63 binary digits).

### Implementation

#### Python

```python
class Solution:
    def addBinaryInt(self, a: str, b: str) -> str:
        # Python int is unbounded; safe here. Other languages would overflow.
        return bin(int(a, 2) + int(b, 2))[2:]
```

> Don't use this in Java/Go/C++ for general inputs.

---

## Approach 2: Walk Both From End With Carry

### Algorithm (step-by-step)

1. `i = len(a) - 1`, `j = len(b) - 1`, `carry = 0`, `result = []`.
2. While `i >= 0 or j >= 0 or carry > 0`:
   - `bit_a = (i >= 0) ? int(a[i]) : 0`
   - `bit_b = (j >= 0) ? int(b[j]) : 0`
   - `s = bit_a + bit_b + carry`
   - Append `s % 2` (as a char/digit) to result.
   - `carry = s / 2`
   - `i--; j--`
3. Reverse the result and join into a string.

### Pseudocode

```text
i, j, carry = len(a)-1, len(b)-1, 0
out = []
while i >= 0 or j >= 0 or carry > 0:
    s = carry
    if i >= 0: s += int(a[i]); i--
    if j >= 0: s += int(b[j]); j--
    out.append(s % 2)
    carry = s // 2
return ''.join(reversed(out))
```

### Complexity

| | Complexity |
|---|---|
| **Time** | O(max(n, m)) |
| **Space** | O(max(n, m)) |

### Implementation

#### Go

```go
import (
    "strings"
)

func addBinary(a string, b string) string {
    i, j := len(a)-1, len(b)-1
    carry := 0
    var sb strings.Builder
    for i >= 0 || j >= 0 || carry > 0 {
        s := carry
        if i >= 0 {
            s += int(a[i] - '0')
            i--
        }
        if j >= 0 {
            s += int(b[j] - '0')
            j--
        }
        sb.WriteByte(byte('0' + (s % 2)))
        carry = s / 2
    }
    // Reverse
    r := []byte(sb.String())
    for l, h := 0, len(r)-1; l < h; l, h = l+1, h-1 {
        r[l], r[h] = r[h], r[l]
    }
    return string(r)
}
```

#### Java

```java
class Solution {
    public String addBinary(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, carry = 0;
        while (i >= 0 || j >= 0 || carry > 0) {
            int s = carry;
            if (i >= 0) s += a.charAt(i--) - '0';
            if (j >= 0) s += b.charAt(j--) - '0';
            sb.append(s % 2);
            carry = s / 2;
        }
        return sb.reverse().toString();
    }
}
```

#### Python

```python
class Solution:
    def addBinary(self, a: str, b: str) -> str:
        i, j, carry = len(a) - 1, len(b) - 1, 0
        out = []
        while i >= 0 or j >= 0 or carry > 0:
            s = carry
            if i >= 0: s += int(a[i]); i -= 1
            if j >= 0: s += int(b[j]); j -= 1
            out.append(str(s % 2))
            carry = s // 2
        return ''.join(reversed(out))
```

### Dry Run

```text
a = "1010", b = "1011"
i=3, j=3, carry=0

Iter: s=0+0+1=1 → out="1", carry=0; i=2, j=2
Iter: s=0+1+1=2 → out="10", carry=1; i=1, j=1
Iter: s=1+0+1=2 → out="101", wait sum: a[1]=0, b[1]=1, carry=1 → s=2 → out="100", carry=1
Hmm let me redo:

i=3: a[3]=0, b[3]=1, carry=0 → s=1 → out=[1], carry=0
i=2: a[2]=1, b[2]=1, carry=0 → s=2 → out=[1,0], carry=1
i=1: a[1]=0, b[1]=0, carry=1 → s=1 → out=[1,0,1], carry=0
i=0: a[0]=1, b[0]=1, carry=0 → s=2 → out=[1,0,1,0], carry=1
loop again carry=1: s=1 → out=[1,0,1,0,1], carry=0

Reverse: "10101"
```

---

## Approach 3: Bit Manipulation (XOR + AND)

### Idea

> When adding two integers in binary:
>   - `a XOR b` is the sum without carry.
>   - `(a AND b) << 1` is the carry.
>
> Repeat until carry becomes 0. This works as long as `a` and `b` fit in the integer type. For arbitrary-length binary strings, Python handles it natively; in Java/Go we'd need BigInteger.

### Implementation

#### Python

```python
class Solution:
    def addBinaryBits(self, a: str, b: str) -> str:
        x, y = int(a, 2), int(b, 2)
        while y != 0:
            x, y = x ^ y, (x & y) << 1
        return bin(x)[2:] if x else '0'
```

#### Java

```java
import java.math.BigInteger;
class Solution {
    public String addBinaryBits(String a, String b) {
        BigInteger x = new BigInteger(a, 2);
        BigInteger y = new BigInteger(b, 2);
        while (!y.equals(BigInteger.ZERO)) {
            BigInteger sum = x.xor(y);
            BigInteger carry = x.and(y).shiftLeft(1);
            x = sum;
            y = carry;
        }
        return x.toString(2);
    }
}
```

> Pretty for two-line solutions in Python; less practical in fixed-width languages.

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Convert to int | O(n) | O(n) | One-liner (Python) | Overflow in fixed-width |
| 2 | Walk + Carry | O(max(n,m)) | O(max(n,m)) | Robust, language-agnostic | Standard but verbose |
| 3 | XOR + AND | O((max len)^2) | O(max len) | Elegant in Python | Needs BigInteger elsewhere |

### Which solution to choose?

- **In an interview:** Approach 2
- **In production:** Approach 2
- **On Leetcode:** Approach 2 (universal)

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Both empty | n/a | n/a | Constraint forbids |
| 2 | Both `"0"` | `"0"`, `"0"` | `"0"` | Sum is 0 |
| 3 | One zero | `"0"`, `"1"` | `"1"` | Sum unchanged |
| 4 | Equal length | `"11"`, `"11"` | `"110"` | Carry to new position |
| 5 | Different lengths | `"1"`, `"111"` | `"1000"` | Shorter padded |
| 6 | Final carry | `"1"`, `"1"` | `"10"` | Single carry out |
| 7 | Long final carry | `"111"`, `"1"` | `"1000"` | Cascading carry |
| 8 | Long input | `"1" * 1000` | length 1000 or 1001 | No int overflow |

---

## Common Mistakes

### Mistake 1: Forgetting to reverse the output

```python
# WRONG — appended LSB first, but never reversed
return ''.join(out)

# CORRECT
return ''.join(reversed(out))
```

**Reason:** We process from the least significant digit upward, so the resulting list is reversed.

### Mistake 2: Stopping the loop too early

```python
# WRONG — misses the trailing carry
while i >= 0 and j >= 0:
    ...

# CORRECT
while i >= 0 or j >= 0 or carry > 0:
    ...
```

**Reason:** Without the `carry > 0` check, a final carry past both inputs is dropped.

### Mistake 3: Using fixed-width int conversion

```java
// WRONG — overflow for n > ~63
long x = Long.parseLong(a, 2) + Long.parseLong(b, 2);
return Long.toBinaryString(x);
```

**Reason:** Constraint allows 10^4 characters; only `BigInteger` or digit-wise simulation works in Java/Go.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [66. Plus One](https://leetcode.com/problems/plus-one/) | :green_circle: Easy | Same idea, base 10 |
| 2 | [415. Add Strings](https://leetcode.com/problems/add-strings/) | :green_circle: Easy | Add two decimal strings |
| 3 | [989. Add to Array-Form of Integer](https://leetcode.com/problems/add-to-array-form-of-integer/) | :green_circle: Easy | Add int to array of digits |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Two strings stacked, pointers walking right-to-left
> - Cell-by-cell sum, modulo, and carry
> - Highlight current bits and carry flow
> - Final reversal animation
