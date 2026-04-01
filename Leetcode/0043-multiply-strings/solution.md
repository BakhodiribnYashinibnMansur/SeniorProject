# 0043. Multiply Strings

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Grade School Multiplication](#approach-1-grade-school-multiplication)
4. [Complexity Comparison](#complexity-comparison)
5. [Edge Cases](#edge-cases)
6. [Common Mistakes](#common-mistakes)
7. [Related Problems](#related-problems)
8. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [43. Multiply Strings](https://leetcode.com/problems/multiply-strings/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Math`, `String`, `Simulation` |

### Description

> Given two non-negative integers `num1` and `num2` represented as strings, return the product of `num1` and `num2`, also represented as a string.
>
> **Note:** You must not use any built-in BigInteger library or convert the inputs to integer directly.

### Examples

```
Example 1:
Input: num1 = "2", num2 = "3"
Output: "6"

Example 2:
Input: num1 = "123", num2 = "456"
Output: "56088"
```

### Constraints

- `1 <= num1.length, num2.length <= 200`
- `num1` and `num2` consist of digits only
- Both `num1` and `num2` do not contain any leading zero, except the number `0` itself

---

## Problem Breakdown

### 1. What is being asked?

Multiply two numbers given as strings without converting them to integers. Return the result as a string. This simulates how we multiply numbers by hand — digit by digit.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `num1` | `string` | Non-negative integer as a string (up to 200 digits) |
| `num2` | `string` | Non-negative integer as a string (up to 200 digits) |

Important observations about the input:
- Numbers can be **very large** (up to 200 digits — far beyond int64)
- No leading zeros except the number `0` itself
- All characters are digits `'0'` to `'9'`

### 3. What is the output?

- A **string** representing the product of `num1` and `num2`
- No leading zeros in the result (except `"0"` itself)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `length <= 200` | Up to 200 digits — cannot fit in any integer type. Must use digit-by-digit math. |
| `digits only` | No need to handle signs, decimals, or invalid characters |
| `no leading zeros` | Input is clean, but we must strip leading zeros from our result |

### 5. Step-by-step example analysis

#### Example 1: `num1 = "2", num2 = "3"`

```text
Simple single-digit multiplication:
  2 * 3 = 6

Result: "6"
```

#### Example 2: `num1 = "123", num2 = "456"`

```text
Grade school multiplication:

        1  2  3
     x  4  5  6
     ----------
        7  3  8    (123 * 6)
     6  1  5  0    (123 * 5, shifted left by 1)
  4  9  2  0  0    (123 * 4, shifted left by 2)
  ---------------
  5  6  0  8  8

Result: "56088"
```

### 6. Key Observations

1. **Position-based multiplication** — When we multiply digit at index `i` of `num1` by digit at index `j` of `num2`, the result contributes to position `i + j` and `i + j + 1` in the result array (counting from the right / least significant digit).
2. **Maximum result length** — The product of an `m`-digit number and an `n`-digit number has at most `m + n` digits. For example: `99 * 99 = 9801` (2 digits * 2 digits = 4 digits).
3. **Carry propagation** — Each position can accumulate values greater than 9, so we process carries from right to left.
4. **Zero case** — If either input is `"0"`, the result is `"0"`.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Simulation | Directly simulates grade school multiplication | This problem |
| Array accumulation | Use an array to accumulate partial products | Position-based digit multiplication |

**Chosen pattern:** `Simulation (Grade School Multiplication)`
**Reason:** We simulate the manual multiplication process, accumulating products at the correct positions, then handling carries.

---

## Approach 1: Grade School Multiplication

### Thought process

> We simulate long multiplication exactly as taught in school.
> Instead of multiplying row by row and then adding, we directly accumulate each digit-by-digit product into the correct position in a result array.
>
> Key insight: digit `num1[i]` * digit `num2[j]` contributes to positions `i+j` and `i+j+1` in the result.
> We use a result array of size `m + n` (maximum possible digits), multiply all pairs, accumulate, then propagate carries.

### Algorithm (step-by-step)

1. If either `num1` or `num2` is `"0"`, return `"0"`
2. Let `m = len(num1)`, `n = len(num2)`
3. Create a result array `result` of size `m + n`, initialized to 0
4. Iterate `i` from `m-1` down to `0` (right to left through `num1`):
   a. Iterate `j` from `n-1` down to `0` (right to left through `num2`):
      - Compute `mul = digit(num1[i]) * digit(num2[j])`
      - Positions: `p1 = i + j`, `p2 = i + j + 1`
      - Add `mul` to `result[p2]` (ones place)
      - Propagate carry: `result[p1] += result[p2] / 10`, `result[p2] %= 10`
5. Convert the result array to a string, skipping leading zeros
6. Return the result string (or `"0"` if empty)

### Pseudocode

```text
function multiply(num1, num2):
    if num1 == "0" or num2 == "0":
        return "0"

    m = len(num1), n = len(num2)
    result = array of size m + n, filled with 0

    for i = m-1 down to 0:
        for j = n-1 down to 0:
            mul = (num1[i] - '0') * (num2[j] - '0')
            p1 = i + j       // tens position
            p2 = i + j + 1   // ones position

            sum = mul + result[p2]
            result[p2] = sum % 10
            result[p1] += sum / 10

    // Build result string, skip leading zeros
    skip leading zeros in result
    return joined digits as string (or "0" if all zeros)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(m * n) | Multiply every digit of num1 by every digit of num2 |
| **Space** | O(m + n) | Result array of size m + n |

### Implementation

#### Go

```go
// multiply — Grade School Multiplication
// Time: O(m*n), Space: O(m+n)
func multiply(num1 string, num2 string) string {
    if num1 == "0" || num2 == "0" {
        return "0"
    }

    m, n := len(num1), len(num2)
    result := make([]int, m+n)

    // Multiply each digit pair and accumulate
    for i := m - 1; i >= 0; i-- {
        for j := n - 1; j >= 0; j-- {
            mul := int(num1[i]-'0') * int(num2[j]-'0')
            p1, p2 := i+j, i+j+1

            sum := mul + result[p2]
            result[p2] = sum % 10
            result[p1] += sum / 10
        }
    }

    // Build result string, skip leading zeros
    var sb []byte
    for _, d := range result {
        if len(sb) == 0 && d == 0 {
            continue
        }
        sb = append(sb, byte(d)+'0')
    }

    if len(sb) == 0 {
        return "0"
    }
    return string(sb)
}
```

#### Java

```java
class Solution {
    // multiply — Grade School Multiplication
    // Time: O(m*n), Space: O(m+n)
    public String multiply(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) {
            return "0";
        }

        int m = num1.length(), n = num2.length();
        int[] result = new int[m + n];

        // Multiply each digit pair and accumulate
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                int p1 = i + j, p2 = i + j + 1;

                int sum = mul + result[p2];
                result[p2] = sum % 10;
                result[p1] += sum / 10;
            }
        }

        // Build result string, skip leading zeros
        StringBuilder sb = new StringBuilder();
        for (int d : result) {
            if (sb.length() == 0 && d == 0) continue;
            sb.append(d);
        }

        return sb.length() == 0 ? "0" : sb.toString();
    }
}
```

#### Python

```python
class Solution:
    def multiply(self, num1: str, num2: str) -> str:
        """
        Grade School Multiplication
        Time: O(m*n), Space: O(m+n)
        """
        if num1 == "0" or num2 == "0":
            return "0"

        m, n = len(num1), len(num2)
        result = [0] * (m + n)

        # Multiply each digit pair and accumulate
        for i in range(m - 1, -1, -1):
            for j in range(n - 1, -1, -1):
                mul = int(num1[i]) * int(num2[j])
                p1, p2 = i + j, i + j + 1

                total = mul + result[p2]
                result[p2] = total % 10
                result[p1] += total // 10

        # Build result string, skip leading zeros
        result_str = ''.join(map(str, result)).lstrip('0')
        return result_str if result_str else "0"
```

### Dry Run

```text
Input: num1 = "123", num2 = "456"
m = 3, n = 3
result = [0, 0, 0, 0, 0, 0]  (size 6)

Indices:  num1[0]='1', num1[1]='2', num1[2]='3'
          num2[0]='4', num2[1]='5', num2[2]='6'

--- i=2 (digit '3') ---
  j=2: mul = 3*6 = 18, p1=4, p2=5
       sum = 18 + result[5] = 18 + 0 = 18
       result[5] = 18 % 10 = 8, result[4] += 18 / 10 = 1
       result = [0, 0, 0, 0, 1, 8]

  j=1: mul = 3*5 = 15, p1=3, p2=4
       sum = 15 + result[4] = 15 + 1 = 16
       result[4] = 16 % 10 = 6, result[3] += 16 / 10 = 1
       result = [0, 0, 0, 1, 6, 8]

  j=0: mul = 3*4 = 12, p1=2, p2=3
       sum = 12 + result[3] = 12 + 1 = 13
       result[3] = 13 % 10 = 3, result[2] += 13 / 10 = 1
       result = [0, 0, 1, 3, 6, 8]
       (partial: 123 * 6 = 738, stored as ...1368 with carry)

--- i=1 (digit '2') ---
  j=2: mul = 2*6 = 12, p1=3, p2=4
       sum = 12 + result[4] = 12 + 6 = 18
       result[4] = 18 % 10 = 8, result[3] += 18 / 10 = 1
       result = [0, 0, 1, 4, 8, 8]

  j=1: mul = 2*5 = 10, p1=2, p2=3
       sum = 10 + result[3] = 10 + 4 = 14
       result[3] = 14 % 10 = 4, result[2] += 14 / 10 = 1
       result = [0, 0, 2, 4, 8, 8]

  j=0: mul = 2*4 = 8, p1=1, p2=2
       sum = 8 + result[2] = 8 + 2 = 10
       result[2] = 10 % 10 = 0, result[1] += 10 / 10 = 1
       result = [0, 1, 0, 4, 8, 8]

--- i=0 (digit '1') ---
  j=2: mul = 1*6 = 6, p1=2, p2=3
       sum = 6 + result[3] = 6 + 4 = 10
       result[3] = 10 % 10 = 0, result[2] += 10 / 10 = 1
       result = [0, 1, 1, 0, 8, 8]

  j=1: mul = 1*5 = 5, p1=1, p2=2
       sum = 5 + result[2] = 5 + 1 = 6
       result[2] = 6 % 10 = 6, result[1] += 6 / 10 = 0
       result = [0, 1, 6, 0, 8, 8]

  j=0: mul = 1*4 = 4, p1=0, p2=1
       sum = 4 + result[1] = 4 + 1 = 5
       result[1] = 5 % 10 = 5, result[0] += 5 / 10 = 0
       result = [0, 5, 6, 0, 8, 8]

Skip leading zero: "56088"
Result: "56088" ✓
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Grade School Multiplication | O(m*n) | O(m+n) | Simple, intuitive, handles huge numbers | Not the fastest for very large numbers (Karatsuba is O(n^1.585)) |

### Which solution to choose?

- **In an interview:** Grade School Multiplication — clean, easy to explain, and optimal enough
- **In production:** For numbers up to 200 digits, O(m*n) is fast enough. For thousands of digits, consider Karatsuba or FFT-based multiplication
- **On Leetcode:** Grade School Multiplication passes all test cases comfortably

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Zero times anything | `num1="0", num2="123"` | `"0"` | Anything times zero is zero |
| 2 | Both zeros | `num1="0", num2="0"` | `"0"` | 0 * 0 = 0 |
| 3 | Single digits | `num1="2", num2="3"` | `"6"` | Simple multiplication |
| 4 | One times anything | `num1="1", num2="999"` | `"999"` | Identity multiplication |
| 5 | Large numbers | `num1="999", num2="999"` | `"998001"` | Maximum carry propagation |
| 6 | Different lengths | `num1="12", num2="3456"` | `"41472"` | Asymmetric multiplication |
| 7 | Result with internal zeros | `num1="100", num2="100"` | `"10000"` | Must not strip internal zeros |

---

## Common Mistakes

### Mistake 1: Forgetting to handle the zero case

```python
# WRONG — produces "0000..." or leading zeros
def multiply(self, num1, num2):
    m, n = len(num1), len(num2)
    result = [0] * (m + n)
    # ... multiplication logic ...
    return ''.join(map(str, result)).lstrip('0')  # Returns "" for "0"*"0"

# CORRECT — check for zero early
def multiply(self, num1, num2):
    if num1 == "0" or num2 == "0":
        return "0"
    # ... rest of logic ...
```

**Reason:** Without the zero check, `lstrip('0')` can return an empty string.

### Mistake 2: Wrong position mapping

```python
# WRONG — positions are off
p1 = i + j + 1  # This is the tens place
p2 = i + j + 2  # This goes out of bounds

# CORRECT — p1 is tens, p2 is ones
p1 = i + j      # Tens position (carry goes here)
p2 = i + j + 1  # Ones position (digit goes here)
```

**Reason:** The result array has size `m + n`. Position `i + j + 1` is the ones place for the product of `num1[i]` and `num2[j]`.

### Mistake 3: Not accumulating the carry properly

```python
# WRONG — overwrites the carry instead of adding
result[p1] = sum // 10

# CORRECT — add the carry to the existing value
result[p1] += sum // 10
```

**Reason:** Multiple digit products contribute to the same position. We must add carries, not overwrite them.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | :yellow_circle: Medium | Digit-by-digit arithmetic with carry |
| 2 | [66. Plus One](https://leetcode.com/problems/plus-one/) | :green_circle: Easy | Carry propagation in digit arrays |
| 3 | [67. Add Binary](https://leetcode.com/problems/add-binary/) | :green_circle: Easy | String-based arithmetic |
| 4 | [415. Add Strings](https://leetcode.com/problems/add-strings/) | :green_circle: Easy | String addition without conversion |
| 5 | [306. Additive Number](https://leetcode.com/problems/additive-number/) | :yellow_circle: Medium | String-based number operations |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Multiplication grid** showing digit-by-digit products
> - **Carry propagation** visualization through the result array
> - Step-by-step walkthrough with Play/Pause/Reset controls
> - Speed slider and preset examples for experimentation
