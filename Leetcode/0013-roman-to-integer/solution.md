# 0013. Roman to Integer

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Left-to-Right with Subtraction Rule](#approach-1-left-to-right-with-subtraction-rule)
4. [Approach 2: Right-to-Left](#approach-2-right-to-left)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [13. Roman to Integer](https://leetcode.com/problems/roman-to-integer/) |
| **Difficulty** | 🟢 Easy |
| **Tags** | `Hash Table`, `Math`, `String` |

### Description

> Roman numerals are represented by seven different symbols:
>
> | Symbol | Value |
> |---|---|
> | I | 1 |
> | V | 5 |
> | X | 10 |
> | L | 50 |
> | C | 100 |
> | D | 500 |
> | M | 1000 |
>
> For example, `2` is written as `II` in Roman numeral, just two ones added together. `12` is written as `XII`, which is simply `X + II`. The number `27` is written as `XXVII`, which is `XX + V + II`.
>
> Roman numerals are usually written largest to smallest from left to right. However, the numeral for four is not `IIII`. Instead, the number four is written as `IV`. Because the one is before the five we subtract it making four. The same principle applies to the number nine, which is written as `IX`. There are six instances where subtraction is used:
>
> - `I` can be placed before `V` (5) and `X` (10) to make 4 and 9.
> - `X` can be placed before `L` (50) and `C` (100) to make 40 and 90.
> - `C` can be placed before `D` (500) and `M` (1000) to make 400 and 900.
>
> Given a roman numeral, convert it to an integer.

### Examples

```
Example 1:
Input: s = "III"
Output: 3
Explanation: III = 3.

Example 2:
Input: s = "LVIII"
Output: 58
Explanation: L = 50, V = 5, III = 3.

Example 3:
Input: s = "MCMXCIV"
Output: 1994
Explanation: M = 1000, CM = 900, XC = 90 and IV = 4.
```

### Constraints

- `1 <= s.length <= 15`
- `s` contains only the characters `('I', 'V', 'X', 'L', 'C', 'D', 'M')`
- It is **guaranteed** that `s` is a valid roman numeral in the range `[1, 3999]`

---

## Problem Breakdown

### 1. What is being asked?

Convert a Roman numeral string to its integer equivalent. The key challenge is handling the **subtraction rule** where a smaller value placed before a larger one means subtraction (e.g., `IV` = 4, not 6).

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | A valid Roman numeral string |

Important observations about the input:
- The string contains only valid Roman characters: `I, V, X, L, C, D, M`
- The input is always a **valid** Roman numeral
- Length is between 1 and 15 characters
- The value is in the range `[1, 3999]`

### 3. What is the output?

- **An integer** representing the Roman numeral value
- Range: `1` to `3999`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 15` | Extremely small input — any approach works |
| Valid Roman numeral | No need to validate the input |
| Range `[1, 3999]` | int32 is more than sufficient |

### 5. Step-by-step example analysis

#### Example 1: `s = "III"`

```text
I = 1, I = 1, I = 1
1 + 1 + 1 = 3

No subtraction cases — all characters are the same.
Result: 3
```

#### Example 2: `s = "LVIII"`

```text
L = 50, V = 5, I = 1, I = 1, I = 1
50 + 5 + 1 + 1 + 1 = 58

No subtraction cases — values are in decreasing order.
Result: 58
```

#### Example 3: `s = "MCMXCIV"`

```text
M  = 1000  (M > C? No next... add 1000)
C  = 100   (C < M? Yes → subtract 100)
M  = 1000  (M > X? No next... add 1000)  → CM = 900
X  = 10    (X < C? Yes → subtract 10)
C  = 100   (C > I? No next... add 100)   → XC = 90
I  = 1     (I < V? Yes → subtract 1)
V  = 5     (no next... add 5)            → IV = 4

1000 - 100 + 1000 - 10 + 100 - 1 + 5 = 1994
Result: 1994
```

### 6. Key Observations

1. **Subtraction rule** — When a smaller value appears **before** a larger value, subtract the smaller value.
2. **Addition rule** — When values are in decreasing or equal order, simply add them.
3. **Only 6 subtraction pairs** — IV(4), IX(9), XL(40), XC(90), CD(400), CM(900).
4. **Two directions** — The problem can be solved left-to-right or right-to-left.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Hash Map + Linear Scan | O(1) lookup for each character | Roman to Integer (this problem) |
| Left-to-Right | Compare current with next | Natural reading order |
| Right-to-Left | Compare current with previous | Eliminates need for bounds checking on next |

**Chosen pattern:** `Hash Map + Linear Scan`
**Reason:** Each character maps to a fixed value, and the subtraction rule depends on comparing adjacent values.

---

## Approach 1: Left-to-Right with Subtraction Rule

### Thought process

> Traverse the string from left to right. For each character, look at the **next** character:
> - If the current value is **less than** the next value → **subtract** the current value
> - Otherwise → **add** the current value
>
> This handles the subtraction rule naturally: in `IV`, `I(1) < V(5)`, so subtract 1.

### Algorithm (step-by-step)

1. Create a map of Roman characters to their integer values
2. Initialize `result = 0`
3. For each index `i` from `0` to `n-1`:
   - If `i+1 < n` and `map[s[i]] < map[s[i+1]]` → `result -= map[s[i]]`
   - Otherwise → `result += map[s[i]]`
4. Return `result`

### Pseudocode

```text
function romanToInt(s):
    map = {'I':1, 'V':5, 'X':10, 'L':50, 'C':100, 'D':500, 'M':1000}
    result = 0
    for i = 0 to len(s) - 1:
        if i + 1 < len(s) and map[s[i]] < map[s[i+1]]:
            result -= map[s[i]]
        else:
            result += map[s[i]]
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the string. Map lookup is O(1). |
| **Space** | O(1) | The map has a fixed size of 7 entries regardless of input. |

### Implementation

#### Go

```go
func romanToInt(s string) int {
    romanMap := map[byte]int{
        'I': 1, 'V': 5, 'X': 10, 'L': 50,
        'C': 100, 'D': 500, 'M': 1000,
    }

    result := 0
    n := len(s)

    for i := 0; i < n; i++ {
        if i+1 < n && romanMap[s[i]] < romanMap[s[i+1]] {
            result -= romanMap[s[i]]
        } else {
            result += romanMap[s[i]]
        }
    }

    return result
}
```

#### Java

```java
class Solution {
    public int romanToInt(String s) {
        Map<Character, Integer> romanMap = Map.of(
            'I', 1, 'V', 5, 'X', 10, 'L', 50,
            'C', 100, 'D', 500, 'M', 1000
        );

        int result = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            int curr = romanMap.get(s.charAt(i));
            if (i + 1 < n && curr < romanMap.get(s.charAt(i + 1))) {
                result -= curr;
            } else {
                result += curr;
            }
        }

        return result;
    }
}
```

#### Python

```python
class Solution:
    def romanToInt(self, s: str) -> int:
        roman_map = {'I': 1, 'V': 5, 'X': 10, 'L': 50,
                     'C': 100, 'D': 500, 'M': 1000}

        result = 0
        n = len(s)

        for i in range(n):
            if i + 1 < n and roman_map[s[i]] < roman_map[s[i + 1]]:
                result -= roman_map[s[i]]
            else:
                result += roman_map[s[i]]

        return result
```

### Dry Run

```text
Input: s = "MCMXCIV"

map = {M:1000, C:100, X:10, I:1, V:5, L:50, D:500}

i=0: s[0]='M'(1000), s[1]='C'(100)  → 1000 >= 100 → result += 1000 → result = 1000
i=1: s[1]='C'(100),  s[2]='M'(1000) → 100 < 1000  → result -= 100  → result = 900
i=2: s[2]='M'(1000), s[3]='X'(10)   → 1000 >= 10  → result += 1000 → result = 1900
i=3: s[3]='X'(10),   s[4]='C'(100)  → 10 < 100    → result -= 10   → result = 1890
i=4: s[4]='C'(100),  s[5]='I'(1)    → 100 >= 1    → result += 100  → result = 1990
i=5: s[5]='I'(1),    s[6]='V'(5)    → 1 < 5       → result -= 1    → result = 1989
i=6: s[6]='V'(5),    no next        →              → result += 5    → result = 1994

Result: 1994 ✅
```

---

## Approach 2: Right-to-Left

### Thought process

> Instead of looking ahead (comparing current with next), traverse from **right to left** and compare the current value with the **previous** value we processed (which is the character to the right).
>
> - If current value **<** previous value → **subtract** (we are part of a subtraction pair)
> - Otherwise → **add**
>
> This avoids bounds checking for `i+1 < n` since we track the previous value.

### Algorithm (step-by-step)

1. Create a map of Roman characters to their integer values
2. Initialize `result = 0`, `prev = 0`
3. For each index `i` from `n-1` down to `0`:
   - `curr = map[s[i]]`
   - If `curr < prev` → `result -= curr`
   - Otherwise → `result += curr`
   - `prev = curr`
4. Return `result`

### Pseudocode

```text
function romanToInt(s):
    map = {'I':1, 'V':5, 'X':10, 'L':50, 'C':100, 'D':500, 'M':1000}
    result = 0
    prev = 0
    for i = len(s) - 1 down to 0:
        curr = map[s[i]]
        if curr < prev:
            result -= curr
        else:
            result += curr
        prev = curr
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Single pass through the string (reversed). Map lookup is O(1). |
| **Space** | O(1) | The map has a fixed size of 7 entries. Only one extra variable `prev`. |

### Implementation

#### Go

```go
func romanToInt(s string) int {
    romanMap := map[byte]int{
        'I': 1, 'V': 5, 'X': 10, 'L': 50,
        'C': 100, 'D': 500, 'M': 1000,
    }

    result := 0
    prev := 0

    for i := len(s) - 1; i >= 0; i-- {
        curr := romanMap[s[i]]
        if curr < prev {
            result -= curr
        } else {
            result += curr
        }
        prev = curr
    }

    return result
}
```

#### Java

```java
class Solution {
    public int romanToInt(String s) {
        Map<Character, Integer> romanMap = Map.of(
            'I', 1, 'V', 5, 'X', 10, 'L', 50,
            'C', 100, 'D', 500, 'M', 1000
        );

        int result = 0;
        int prev = 0;

        for (int i = s.length() - 1; i >= 0; i--) {
            int curr = romanMap.get(s.charAt(i));
            if (curr < prev) {
                result -= curr;
            } else {
                result += curr;
            }
            prev = curr;
        }

        return result;
    }
}
```

#### Python

```python
class Solution:
    def romanToInt(self, s: str) -> int:
        roman_map = {'I': 1, 'V': 5, 'X': 10, 'L': 50,
                     'C': 100, 'D': 500, 'M': 1000}

        result = 0
        prev = 0

        for i in range(len(s) - 1, -1, -1):
            curr = roman_map[s[i]]
            if curr < prev:
                result -= curr
            else:
                result += curr
            prev = curr

        return result
```

### Dry Run

```text
Input: s = "MCMXCIV"

Traversal: V → I → C → X → M → C → M (right to left)

i=6: s[6]='V'(5),    prev=0    → 5 >= 0    → result += 5    → result = 5,    prev = 5
i=5: s[5]='I'(1),    prev=5    → 1 < 5     → result -= 1    → result = 4,    prev = 1
i=4: s[4]='C'(100),  prev=1    → 100 >= 1  → result += 100  → result = 104,  prev = 100
i=3: s[3]='X'(10),   prev=100  → 10 < 100  → result -= 10   → result = 94,   prev = 10
i=2: s[2]='M'(1000), prev=10   → 1000 >= 10→ result += 1000 → result = 1094, prev = 1000
i=1: s[1]='C'(100),  prev=1000 → 100 < 1000→ result -= 100  → result = 994,  prev = 100
i=0: s[0]='M'(1000), prev=100  → 1000 >= 100→result += 1000 → result = 1994, prev = 1000

Result: 1994 ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Left-to-Right | O(n) | O(1) | Natural reading order, intuitive | Needs bounds check for `i+1` |
| 2 | Right-to-Left | O(n) | O(1) | No bounds check needed, elegant | Less intuitive direction |

### Which solution to choose?

- **In an interview:** Approach 1 (Left-to-Right) — intuitive, easy to explain
- **In production:** Either — both are O(n) time, O(1) space
- **On Leetcode:** Either — identical performance
- **For learning:** Both — understanding both directions helps with similar problems

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Single character | `"I"` | `1` | Minimal input |
| 2 | Single large | `"M"` | `1000` | Largest single symbol |
| 3 | All same characters | `"III"` | `3` | Pure addition, no subtraction |
| 4 | Simple subtraction | `"IV"` | `4` | Basic subtraction pair |
| 5 | All subtraction pairs | `"MCMXCIV"` | `1994` | Multiple subtractions: CM, XC, IV |
| 6 | Maximum value | `"MMMCMXCIX"` | `3999` | Largest valid Roman numeral |
| 7 | Decreasing order | `"DCXXI"` | `621` | No subtraction — all decreasing |
| 8 | Triple subtraction | `"CDXLIV"` | `444` | CD(400) + XL(40) + IV(4) |

---

## Common Mistakes

### Mistake 1: Not handling subtraction rule

```python
# WRONG — simply adding all values
def romanToInt(self, s: str) -> int:
    roman_map = {'I': 1, 'V': 5, 'X': 10, 'L': 50,
                 'C': 100, 'D': 500, 'M': 1000}
    return sum(roman_map[c] for c in s)
# "IV" → 1 + 5 = 6 (should be 4)

# CORRECT — check if current < next
for i in range(n):
    if i + 1 < n and roman_map[s[i]] < roman_map[s[i + 1]]:
        result -= roman_map[s[i]]
    else:
        result += roman_map[s[i]]
```

**Reason:** The subtraction rule is the core of this problem. `IV` is 4, not 6.

### Mistake 2: Off-by-one error in left-to-right

```python
# WRONG — missing bounds check
for i in range(n):
    if roman_map[s[i]] < roman_map[s[i + 1]]:  # IndexError when i = n-1!
        result -= roman_map[s[i]]

# CORRECT — check i + 1 < n first
for i in range(n):
    if i + 1 < n and roman_map[s[i]] < roman_map[s[i + 1]]:
        result -= roman_map[s[i]]
```

**Reason:** The last character has no next character to compare with.

### Mistake 3: Wrong direction comparison in right-to-left

```python
# WRONG — comparing with wrong direction
prev = 0
for i in range(len(s) - 1, -1, -1):
    curr = roman_map[s[i]]
    if curr > prev:  # Should be curr < prev for subtraction!
        result -= curr

# CORRECT — subtract when current is LESS than previous (right neighbor)
if curr < prev:
    result -= curr
```

**Reason:** In right-to-left traversal, `prev` is the character to the right. If the current character is smaller, it's a subtraction case.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [12. Integer to Roman](https://leetcode.com/problems/integer-to-roman/) | 🟡 Medium | Reverse operation — integer to Roman numeral |
| 2 | [273. Integer to English Words](https://leetcode.com/problems/integer-to-english-words/) | 🔴 Hard | Number system conversion |
| 3 | [168. Excel Sheet Column Title](https://leetcode.com/problems/excel-sheet-column-title/) | 🟢 Easy | Number system encoding |
| 4 | [171. Excel Sheet Column Number](https://leetcode.com/problems/excel-sheet-column-number/) | 🟢 Easy | Positional decoding (similar to Roman) |
| 5 | [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/) | 🟡 Medium | String to number conversion |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Left-to-Right** tab — compares current with next, subtracts if smaller
> - **Right-to-Left** tab — compares current with previous, subtracts if smaller
> - Enter any Roman numeral to see character-by-character processing
> - Subtraction cases are highlighted in red
> - Running total updates in real time
