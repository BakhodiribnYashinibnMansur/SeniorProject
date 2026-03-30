# 0012. Integer to Roman

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Greedy with Value Table](#approach-1-greedy-with-value-table)
4. [Approach 2: Hardcoded Digit Mapping](#approach-2-hardcoded-digit-mapping)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [12. Integer to Roman](https://leetcode.com/problems/integer-to-roman/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `Hash Table`, `Math`, `String` |

### Description

> Seven different symbols represent Roman numerals with the following values:
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
> Roman numerals are formed by appending the conversions of decimal place values from highest to lowest.
> Converting a decimal place value into a Roman numeral has the following rules:
>
> - If the value does not start with 4 or 9, select the symbol of the maximal value that can be subtracted from the input, append that symbol, and subtract its value, repeating until the value is 0.
> - If the value starts with 4 or 9, use the **subtractive form** representing one symbol subtracted from the following symbol: 4=IV, 9=IX, 40=XL, 90=XC, 400=CD, 900=CM.
>
> Only powers of 10 (I, X, C, M) can be repeated, and at most 3 times in a row.
>
> Given an integer, convert it to a Roman numeral.

### Examples

```
Example 1:
Input: num = 3749
Output: "MMMDCCXLIX"
Explanation: 3000=MMM, 700=DCC, 40=XL, 9=IX

Example 2:
Input: num = 58
Output: "LVIII"
Explanation: 50=L, 5=V, 3=III

Example 3:
Input: num = 1994
Output: "MCMXCIV"
Explanation: 1000=M, 900=CM, 90=XC, 4=IV
```

### Constraints

- `1 <= num <= 3999`

---

## Problem Breakdown

### 1. What is being asked?

Convert an integer (1-3999) into its Roman numeral string representation, following standard Roman numeral rules including subtractive forms.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `num` | `int` | An integer between 1 and 3999 |

Important observations about the input:
- The value is always **positive** (minimum 1)
- The value is **bounded** (maximum 3999)
- No edge case for 0 or negative numbers

### 3. What is the output?

- A **string** containing the Roman numeral representation
- The string uses only the characters: `I`, `V`, `X`, `L`, `C`, `D`, `M`
- Maximum length is 15 characters (`MMMCMXCIX` = 3999 has 9 chars, but `MMMDCCCLXXXVIII` = 3888 has 15)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `1 <= num <= 3999` | Input is bounded, so time and space are technically O(1) |
| Only 7 base symbols + 6 subtractive forms | Total of 13 value-symbol pairs to consider |
| No symbol repeats more than 3 times | Natural consequence of using subtractive forms |

### 5. Step-by-step example analysis

#### Example 1: `num = 3749`

```text
Initial: num = 3749, result = ""

Step 1: 3749 >= 1000 (M)  -> result = "M",    num = 2749
Step 2: 2749 >= 1000 (M)  -> result = "MM",   num = 1749
Step 3: 1749 >= 1000 (M)  -> result = "MMM",  num = 749
Step 4: 749  >= 500  (D)  -> result = "MMMD", num = 249
Step 5: 249  >= 100  (C)  -> result = "MMMDC", num = 149
Step 6: 149  >= 100  (C)  -> result = "MMMDCC", num = 49
Step 7: 49   >= 40   (XL) -> result = "MMMDCCXL", num = 9
Step 8: 9    >= 9    (IX) -> result = "MMMDCCXLIX", num = 0

Result: "MMMDCCXLIX"
```

#### Example 3: `num = 1994`

```text
Initial: num = 1994, result = ""

Step 1: 1994 >= 1000 (M)  -> result = "M",    num = 994
Step 2: 994  >= 900  (CM) -> result = "MCM",  num = 94
Step 3: 94   >= 90   (XC) -> result = "MCMXC", num = 4
Step 4: 4    >= 4    (IV) -> result = "MCMXCIV", num = 0

Result: "MCMXCIV"
```

### 6. Key Observations

1. **Greedy works** -- Always taking the largest possible value produces the correct Roman numeral.
2. **Subtractive forms are finite** -- Only 6 subtractive pairs: IV(4), IX(9), XL(40), XC(90), CD(400), CM(900).
3. **Bounded input** -- Since num <= 3999, both time and space are technically O(1).
4. **Digit independence** -- Each decimal digit (thousands, hundreds, tens, ones) can be converted independently.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Greedy | Always pick the largest symbol that fits | Standard Roman numeral construction |
| Lookup Table | Finite, bounded set of values | 13 value-symbol pairs |
| Digit Decomposition | Each place value converts independently | 1994 = 1000 + 900 + 90 + 4 |

**Chosen pattern:** `Greedy with Lookup Table`
**Reason:** Simple, efficient, and naturally handles subtractive forms when included in the table.

---

## Approach 1: Greedy with Value Table

### Thought process

> Build a table of all 13 values (7 base + 6 subtractive) in descending order.
> For each value, while it fits into the remaining number, append its symbol and subtract.
> This naturally produces the correct Roman numeral.

### Algorithm (step-by-step)

1. Create a table of 13 (value, symbol) pairs in descending order: `[(1000,"M"), (900,"CM"), ..., (1,"I")]`
2. Initialize an empty result string
3. For each (value, symbol) pair:
   - While `num >= value`: append symbol to result, subtract value from num
4. Return the result

### Pseudocode

```text
function intToRoman(num):
    values  = [1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1]
    symbols = ["M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"]

    result = ""
    for i = 0 to 12:
        while num >= values[i]:
            result += symbols[i]
            num -= values[i]
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(1) | The outer loop runs 13 times. The inner while loop runs at most 3 times per symbol (e.g., 3000 = MMM). Total iterations bounded by ~15. |
| **Space** | O(1) | The result string has at most 15 characters. The lookup table has 13 fixed entries. |

### Implementation

#### Go

```go
func intToRoman(num int) string {
    values  := []int{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1}
    symbols := []string{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"}

    result := ""
    for i, val := range values {
        for num >= val {
            result += symbols[i]
            num -= val
        }
    }
    return result
}
```

#### Java

```java
class Solution {
    public String intToRoman(int num) {
        int[] values =    {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                result.append(symbols[i]);
                num -= values[i];
            }
        }
        return result.toString();
    }
}
```

#### Python

```python
class Solution:
    def intToRoman(self, num: int) -> str:
        value_symbols = [
            (1000, "M"), (900, "CM"), (500, "D"), (400, "CD"),
            (100, "C"),  (90, "XC"),  (50, "L"),  (40, "XL"),
            (10, "X"),   (9, "IX"),   (5, "V"),   (4, "IV"),
            (1, "I"),
        ]
        result = []
        for val, sym in value_symbols:
            while num >= val:
                result.append(sym)
                num -= val
        return "".join(result)
```

### Dry Run

```text
Input: num = 1994

values  = [1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1]
symbols = ["M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"]

i=0 (1000, "M"):  1994 >= 1000 -> result="M",     num=994
                    994 < 1000  -> move on
i=1 (900, "CM"):   994 >= 900  -> result="MCM",   num=94
                     94 < 900   -> move on
i=2 (500, "D"):     94 < 500   -> skip
i=3 (400, "CD"):    94 < 400   -> skip
i=4 (100, "C"):     94 < 100   -> skip
i=5 (90, "XC"):     94 >= 90   -> result="MCMXC", num=4
                      4 < 90    -> move on
i=6..10:             4 < 50,40,10,9,5 -> skip
i=11 (4, "IV"):      4 >= 4    -> result="MCMXCIV", num=0
                      0 < 4     -> move on
i=12:                0 < 1     -> skip

Return "MCMXCIV"
```

---

## Approach 2: Hardcoded Digit Mapping

### Thought process

> Since the input is bounded (1-3999), we can precompute the Roman representation
> for each digit (0-9) at each place value (ones, tens, hundreds, thousands).
> Then simply decompose the number and concatenate.

### Algorithm (step-by-step)

1. Create 4 lookup arrays: `thousands[0..3]`, `hundreds[0..9]`, `tens[0..9]`, `ones[0..9]`
2. Extract each digit: `num/1000`, `(num%1000)/100`, `(num%100)/10`, `num%10`
3. Concatenate the corresponding Roman strings
4. Return the result

### Pseudocode

```text
function intToRoman(num):
    thousands = ["", "M", "MM", "MMM"]
    hundreds  = ["", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"]
    tens      = ["", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"]
    ones      = ["", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"]

    return thousands[num/1000] + hundreds[(num%1000)/100] + tens[(num%100)/10] + ones[num%10]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(1) | Always exactly 4 array lookups + string concatenation |
| **Space** | O(1) | Lookup tables are constant size (4 arrays with a total of 33 entries) |

### Implementation

#### Go

```go
func intToRomanDigitMap(num int) string {
    thousands := []string{"", "M", "MM", "MMM"}
    hundreds  := []string{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"}
    tens      := []string{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"}
    ones      := []string{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"}

    return thousands[num/1000] + hundreds[(num%1000)/100] + tens[(num%100)/10] + ones[num%10]
}
```

#### Java

```java
class Solution {
    public String intToRoman(int num) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds  = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens      = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones      = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

        return thousands[num / 1000] + hundreds[(num % 1000) / 100] + tens[(num % 100) / 10] + ones[num % 10];
    }
}
```

#### Python

```python
class Solution:
    def intToRoman(self, num: int) -> str:
        thousands = ["", "M", "MM", "MMM"]
        hundreds  = ["", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"]
        tens      = ["", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"]
        ones      = ["", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"]

        return thousands[num // 1000] + hundreds[(num % 1000) // 100] + tens[(num % 100) // 10] + ones[num % 10]
```

### Dry Run

```text
Input: num = 1994

thousands[1994/1000]     = thousands[1] = "M"
hundreds[(1994%1000)/100] = hundreds[9]  = "CM"
tens[(1994%100)/10]       = tens[9]      = "XC"
ones[1994%10]             = ones[4]      = "IV"

Result: "M" + "CM" + "XC" + "IV" = "MCMXCIV"
```

```text
Input: num = 3749

thousands[3749/1000]     = thousands[3] = "MMM"
hundreds[(3749%1000)/100] = hundreds[7]  = "DCC"
tens[(3749%100)/10]       = tens[4]      = "XL"
ones[3749%10]             = ones[9]      = "IX"

Result: "MMM" + "DCC" + "XL" + "IX" = "MMMDCCXLIX"
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Greedy with Value Table | O(1) | O(1) | Intuitive, extensible, easy to understand | Slightly more iterations than Approach 2 |
| 2 | Hardcoded Digit Mapping | O(1) | O(1) | Fastest (exactly 4 lookups), no loops | Hardcoded tables, less flexible |

### Which solution to choose?

- **In an interview:** Approach 1 (Greedy) -- demonstrates algorithmic thinking, easy to explain
- **In production:** Approach 2 (Digit Mapping) -- slightly faster, direct lookup
- **On Leetcode:** Both are accepted with similar performance
- **For learning:** Approach 1 -- teaches the greedy pattern applicable to many problems

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Minimum value | `num = 1` | `"I"` | Smallest valid input |
| 2 | Maximum value | `num = 3999` | `"MMMCMXCIX"` | Largest valid input, uses all subtractive forms at thousands level |
| 3 | All subtractive forms | `num = 944` | `"CMXLIV"` | 900+40+4, three subtractive pairs |
| 4 | Round thousand | `num = 2000` | `"MM"` | No hundreds, tens, or ones |
| 5 | Longest output | `num = 3888` | `"MMMDCCCLXXXVIII"` | 15 characters, maximum length Roman numeral |
| 6 | Single base symbol | `num = 500` | `"D"` | Single character output |
| 7 | All same symbol | `num = 3` | `"III"` | Maximum repetition of a single symbol |

---

## Common Mistakes

### Mistake 1: Forgetting subtractive forms

```python
# WRONG -- only using 7 base symbols
values = [1000, 500, 100, 50, 10, 5, 1]
# For num=4: produces "IIII" instead of "IV"
# For num=900: produces "DCCCC" instead of "CM"

# CORRECT -- include all 13 value-symbol pairs
values = [1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1]
```

**Reason:** Without subtractive forms in the table, the greedy algorithm produces invalid Roman numerals that violate the "no more than 3 consecutive same symbols" rule.

### Mistake 2: Wrong order in the value table

```python
# WRONG -- ascending order
values = [1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000]

# CORRECT -- descending order (greedy picks largest first)
values = [1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1]
```

**Reason:** The greedy algorithm must process values from largest to smallest to produce the correct result.

### Mistake 3: Using string concatenation in a loop (performance)

```java
// SLOW -- String concatenation creates new objects each time
String result = "";
for (...) {
    result += symbols[i];  // O(n) per concatenation
}

// FAST -- StringBuilder for efficient appending
StringBuilder result = new StringBuilder();
for (...) {
    result.append(symbols[i]);  // O(1) amortized
}
```

**Reason:** In Java, strings are immutable. Each `+=` creates a new String object. Use `StringBuilder` for O(1) amortized appends.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [13. Roman to Integer](https://leetcode.com/problems/roman-to-integer/) | 🟢 Easy | Reverse operation -- parse Roman to integer |
| 2 | [273. Integer to English Words](https://leetcode.com/problems/integer-to-english-words/) | 🔴 Hard | Number-to-string conversion with rules |
| 3 | [168. Excel Sheet Column Title](https://leetcode.com/problems/excel-sheet-column-title/) | 🟢 Easy | Number-to-symbol mapping |
| 4 | [171. Excel Sheet Column Number](https://leetcode.com/problems/excel-sheet-column-number/) | 🟢 Easy | Symbol-to-number mapping (reverse of 168) |
| 5 | [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/) | 🟡 Medium | String/number conversion with rules |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Greedy** tab -- step-by-step greedy conversion with value table
> - **Digit Map** tab -- digit decomposition approach
> - Number input field for custom values
> - Value table reference panel
