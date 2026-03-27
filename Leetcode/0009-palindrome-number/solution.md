# 0009. Palindrome Number

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: String Conversion](#approach-1-string-conversion)
4. [Approach 2: Reverse Second Half — No String (Optimal)](#approach-2-reverse-second-half--no-string-optimal)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [9. Palindrome Number](https://leetcode.com/problems/palindrome-number/) |
| **Difficulty** | 🟢 Easy |
| **Tags** | `Math` |

### Description

> Given an integer `x`, return `true` if `x` is a palindrome, and `false` otherwise.

### Examples

```
Example 1:
Input:  x = 121
Output: true
Explanation: 121 reads as 121 from left to right and from right to left.

Example 2:
Input:  x = -121
Output: false
Explanation: From left to right it reads -121. From right to left it reads 121-.
Since it doesn't read the same, it is not a palindrome.

Example 3:
Input:  x = 10
Output: false
Explanation: Reads 01 from right to left. Not a palindrome.
```

### Constraints

- `-2^31 <= x <= 2^31 - 1`

**Follow-up:** Could you solve it without converting the integer to a string?

---

## Problem Breakdown

### 1. What is being asked?

Determine whether an integer reads the same forwards and backwards. For example, `121` → `121` reversed is `121` → same → palindrome.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `x` | `int` | 32-bit signed integer |

### 3. What is the output?

- `true` if `x` is a palindrome
- `false` otherwise

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| Negative numbers | `-121` reversed is `121-` → never a palindrome |
| Trailing zero (non-zero) | `100` reversed starts with `0` → never a palindrome |
| `x = 0` | `0` reversed is `0` → palindrome (special case) |
| No string constraint (follow-up) | Hints at a purely mathematical approach |

### 5. Step-by-step example analysis

#### Example: `x = 12321`

```text
String approach:
  "12321" reversed = "12321"
  "12321" == "12321" → true ✅

Half-reversal approach:
  x=12321, reversedHalf=0

  Step 1: x=1232, reversedHalf = 0*10 + 1 = 1
  Step 2: x=123,  reversedHalf = 1*10 + 2 = 12
  Step 3: x=12,   reversedHalf = 12*10 + 3 = 123

  Now x(12) <= reversedHalf(123) → stop

  Odd digits: x == reversedHalf / 10?
    12 == 123 / 10 = 12 → true ✅
```

#### Example: `x = 1221`

```text
Half-reversal:
  x=1221, reversedHalf=0

  Step 1: x=122, reversedHalf = 0*10 + 1 = 1
  Step 2: x=12,  reversedHalf = 1*10 + 2 = 12

  Now x(12) <= reversedHalf(12) → stop

  Even digits: x == reversedHalf?
    12 == 12 → true ✅
```

#### Example: `x = -121`

```text
x < 0 → return false immediately ✅
```

### 6. Key Observations

1. **Negative → false always.** A `-` sign cannot match its mirror.
2. **Trailing zero → false (except 0).** Reversing any number ending in `0` (other than `0` itself) would produce a leading `0`, which is invalid.
3. **Reverse only half.** Full reversal could overflow for large numbers. Reversing only the second half avoids overflow and is twice as fast.
4. **Middle digit (odd length).** For an odd-length number, the middle digit doesn't affect palindrome status. Discard it with `reversedHalf / 10`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Math / Digit manipulation | Extracting digits with `% 10` and `/ 10` |
| Two-pointer analog | Comparing front half with reversed back half |

---

## Approach 1: String Conversion

### Thought process

> The most intuitive solution: convert the integer to a string, then check if the string equals its reverse using two pointers. Straightforward and easy to reason about.

### Algorithm (step-by-step)

1. If `x < 0`, return `false`
2. Convert `x` to string `s`
3. Set `left = 0`, `right = len(s) - 1`
4. While `left < right`:
   - If `s[left] != s[right]` → return `false`
   - `left++`, `right--`
5. Return `true`

### Pseudocode

```text
function isPalindrome(x):
    if x < 0: return false
    s = toString(x)
    left, right = 0, len(s) - 1
    while left < right:
        if s[left] != s[right]: return false
        left++; right--
    return true
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log x) | Number of digits ≈ log₁₀(x) |
| **Space** | O(log x) | String holds all digits |

### Implementation

#### Go

```go
// isPalindrome — String Conversion approach
// Time: O(log x), Space: O(log x)
func isPalindrome(x int) bool {
    if x < 0 {
        return false
    }
    s := strconv.Itoa(x)
    left, right := 0, len(s)-1
    for left < right {
        if s[left] != s[right] {
            return false
        }
        left++
        right--
    }
    return true
}
```

#### Java

```java
// isPalindrome — String Conversion approach
// Time: O(log x), Space: O(log x)
public boolean isPalindrome(int x) {
    if (x < 0) return false;
    String s = Integer.toString(x);
    int left = 0, right = s.length() - 1;
    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) return false;
        left++;
        right--;
    }
    return true;
}
```

#### Python

```python
# isPalindrome — String Conversion approach
# Time: O(log x), Space: O(log x)
def isPalindrome(self, x: int) -> bool:
    if x < 0:
        return False
    s = str(x)
    return s == s[::-1]
```

### Dry Run

```text
Input: x = 121

s = "121"
left=0, right=2

Step 1: s[0]='1' == s[2]='1' ✅, left=1, right=1
Step 2: left(1) not < right(1) → exit loop

return true ✅
```

---

## Approach 2: Reverse Second Half — No String (Optimal)

### Thought process

> Instead of converting to a string, we reverse **only the second half** of the number using digit-by-digit extraction (`% 10` and `/ 10`). When the reversed half meets or exceeds the remaining first half, we've processed half the digits. Then we compare the two halves.

### Algorithm (step-by-step)

1. If `x < 0` or (`x % 10 == 0` and `x != 0`) → return `false`
2. `reversedHalf = 0`
3. While `x > reversedHalf`:
   - `reversedHalf = reversedHalf * 10 + x % 10`  (peel last digit of x)
   - `x = x / 10`                                  (drop last digit from x)
4. After loop:
   - **Even digits:** return `x == reversedHalf`
   - **Odd digits:** return `x == reversedHalf / 10` (discard middle digit)

### Pseudocode

```text
function isPalindrome(x):
    if x < 0 or (x % 10 == 0 and x != 0):
        return false

    reversedHalf = 0
    while x > reversedHalf:
        reversedHalf = reversedHalf * 10 + x % 10
        x = x / 10

    return x == reversedHalf or x == reversedHalf / 10
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log x) | We process only half the digits: ⌊log₁₀(x)/2⌋ iterations |
| **Space** | O(1) | Only `reversedHalf` and `x` are used |

### Implementation

#### Go

```go
// isPalindrome — Reverse Second Half (No String)
// Time: O(log x), Space: O(1)
func isPalindrome(x int) bool {
    if x < 0 || (x%10 == 0 && x != 0) {
        return false
    }
    reversedHalf := 0
    for x > reversedHalf {
        reversedHalf = reversedHalf*10 + x%10
        x /= 10
    }
    return x == reversedHalf || x == reversedHalf/10
}
```

#### Java

```java
// isPalindrome — Reverse Second Half (No String)
// Time: O(log x), Space: O(1)
public boolean isPalindrome(int x) {
    if (x < 0 || (x % 10 == 0 && x != 0)) {
        return false;
    }
    int reversedHalf = 0;
    while (x > reversedHalf) {
        reversedHalf = reversedHalf * 10 + x % 10;
        x /= 10;
    }
    return x == reversedHalf || x == reversedHalf / 10;
}
```

#### Python

```python
# isPalindrome — Reverse Second Half (No String)
# Time: O(log x), Space: O(1)
def isPalindrome(self, x: int) -> bool:
    if x < 0 or (x % 10 == 0 and x != 0):
        return False
    reversed_half = 0
    while x > reversed_half:
        reversed_half = reversed_half * 10 + x % 10
        x //= 10
    return x == reversed_half or x == reversed_half // 10
```

### Dry Run

```text
Input: x = 1221 (even digits)

Initial: x=1221, reversedHalf=0

Iteration 1: x=1221 > reversedHalf=0
  reversedHalf = 0*10 + 1221%10 = 1
  x = 1221 / 10 = 122

Iteration 2: x=122 > reversedHalf=1
  reversedHalf = 1*10 + 122%10 = 12
  x = 122 / 10 = 12

Iteration 3: x=12, reversedHalf=12 → x NOT > reversedHalf → STOP

Even-digit check: x(12) == reversedHalf(12) → true ✅
```

```text
Input: x = 12321 (odd digits)

Initial: x=12321, reversedHalf=0

Iteration 1: reversedHalf = 1,   x = 1232
Iteration 2: reversedHalf = 12,  x = 123
Iteration 3: reversedHalf = 123, x = 12  → STOP (12 <= 123)

Odd-digit check: x(12) == reversedHalf/10 (123/10=12) → true ✅
```

```text
Input: x = 123 (not palindrome)

Iteration 1: reversedHalf = 3,  x = 12
Iteration 2: x(12) <= reversedHalf(3)? No → reversedHalf = 32, x = 1

Wait, re-check loop: while x > reversedHalf
  Start: x=123, rH=0
  x=12, rH=3
  x=1,  rH=32 → STOP (1 < 32)

Even check: 1 == 32? NO
Odd check:  1 == 32/10 = 3? NO
return false ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | String Conversion | O(log x) | O(log x) | Simple, easy to understand | Allocates a string, uses extra space |
| 2 | Reverse Second Half | O(log x) | O(1) | No string allocation, optimal space | Slightly more logic to understand |

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Negative number | `-121` | `false` | Minus sign can't match mirror |
| 2 | Zero | `0` | `true` | `0` reversed is `0` |
| 3 | Single digit | `5` | `true` | Single digit always a palindrome |
| 4 | Multiple of 10 (non-zero) | `10` | `false` | Reversed would start with `0` |
| 5 | Even-length palindrome | `1221` | `true` | Both halves equal |
| 6 | Odd-length palindrome | `12321` | `true` | Halves equal after discarding middle |
| 7 | Not a palindrome | `123` | `false` | `123` ≠ `321` |
| 8 | INT_MAX | `2147483647` | `false` | `7463847412` ≠ original |

---

## Common Mistakes

### Mistake 1: Reversing the full number (potential overflow)

```go
// ❌ WRONG — reversing the full number can overflow int32
reversed := 0
temp := x
for temp > 0 {
    reversed = reversed*10 + temp%10
    temp /= 10
}
return reversed == x

// ✅ CORRECT — reverse only the second half
// When reversedHalf >= x, we've covered half the digits
```

**Reason:** For a 9-digit number like `2147447412`, reversing fully gives `2147447412`, which fits. But for borderline values, intermediate overflow can occur. Reversing only half eliminates this risk.

### Mistake 2: Forgetting the trailing zero case

```python
# ❌ WRONG — misses x=10 → incorrectly reports true
def isPalindrome(x):
    if x < 0: return False
    reversed_half = 0
    while x > reversed_half:
        reversed_half = reversed_half * 10 + x % 10
        x //= 10
    return x == reversed_half or x == reversed_half // 10
# x=10: loop → rH=0, x=1 → 1>0 → rH=1, x=0 → stop → 0==1? NO, 0==0? YES ← BUG!

# ✅ CORRECT — add the guard at the start
if x < 0 or (x % 10 == 0 and x != 0):
    return False
```

**Reason:** For `x=10`: after the loop `x=0`, `reversedHalf=1`, `reversedHalf//10=0`. The odd-digit check `0 == 0` returns `true` incorrectly. The guard `x % 10 == 0 and x != 0` catches this.

### Mistake 3: Using integer reverse for comparison (off-by-one on middle digit)

```python
# ❌ WRONG — trying to use reversed_half == original_second_half exactly
# without accounting for the middle digit in odd-length numbers

# ✅ CORRECT — always check both conditions:
return x == reversed_half or x == reversed_half // 10
# Second condition handles odd-length by discarding the middle digit
```

**Reason:** For `12321`: at stop point `x=12`, `reversedHalf=123`. `x==reversedHalf` is `false`, but `x == 123//10 = 12` is `true`. Missing the second condition causes odd-length palindromes to fail.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [234. Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list/) | 🟢 Easy | Palindrome check on linked list (reverse second half) |
| 2 | [125. Valid Palindrome](https://leetcode.com/problems/valid-palindrome/) | 🟢 Easy | Palindrome check on strings with filtering |
| 3 | [7. Reverse Integer](https://leetcode.com/problems/reverse-integer/) | 🟡 Medium | Digit reversal with overflow handling |
| 4 | [680. Valid Palindrome II](https://leetcode.com/problems/valid-palindrome-ii/) | 🟢 Easy | Palindrome check allowing one deletion |
| 5 | [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | 🟡 Medium | Finding palindromes within a string |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **String approach** tab — digits displayed as characters, two pointers move inward
> - **Half-reversal approach** tab — shows `x` shrinking from the right while `reversedHalf` grows
> - Step-by-step trace with even vs odd digit count examples
> - Edge case demos: negative numbers, trailing zeros, zero itself
