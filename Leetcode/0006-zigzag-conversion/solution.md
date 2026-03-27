# 0006. Zigzag Conversion

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Simulate Row Traversal](#approach-1-simulate-row-traversal)
4. [Approach 2: Mathematical (Direct Index)](#approach-2-mathematical-direct-index)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [6. Zigzag Conversion](https://leetcode.com/problems/zigzag-conversion/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `String` |

### Description

> The string `"PAYPALISHIRING"` is written in a zigzag pattern on a given number of rows like this (you may want to display this pattern in a fixed font for better legibility):
>
> ```
> P   A   H   N
> A P L S I I G
> Y   I   R
> ```
>
> And then read line by line: `"PAHNAPLSIIGYIR"`.
>
> Write the code that will take a string and make this conversion given a number of rows.

### Examples

```
Example 1:
Input:  s = "PAYPALISHIRING", numRows = 3
Output: "PAHNAPLSIIGYIR"

Example 2:
Input:  s = "PAYPALISHIRING", numRows = 4
Output: "PINALSIGYAHRPI"
Explanation:
P     I    N
A   L S  I G
Y A   H R
P     I

Example 3:
Input:  s = "A", numRows = 1
Output: "A"
```

### Constraints

- `1 <= s.length <= 1000`
- `s` consists of English letters (lower-case and upper-case), `','` and `'.'`
- `1 <= numRows <= 1000`

---

## Problem Breakdown

### 1. What is being asked?

Rearrange the characters of the input string `s` by writing them in a zigzag (down-then-up diagonal) pattern across `numRows` rows, then reading each row left-to-right and concatenating to produce the output string.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `s` | `string` | Input string of length 1 to 1000 |
| `numRows` | `int` | Number of rows in the zigzag pattern |

Important observations:
- The string can contain uppercase/lowercase letters, commas, and periods.
- `numRows` can equal 1 (no reordering at all) or even exceed `len(s)`.
- The string is **not necessarily longer than numRows** — edge case.

### 3. What is the output?

- A **single string** — the zigzag-rearranged version of `s`.
- Same characters, same count, but in a different order.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `s.length <= 1000` | O(n) or O(n²) both feasible, but O(n) is clean |
| `numRows <= 1000` | Rows can be up to 1000; row count can exceed string length |
| `numRows >= 1` | Special case: numRows = 1 → return `s` unchanged |

### 5. Step-by-step example analysis

#### Example 1: `s = "PAYPALISHIRING", numRows = 3`

```text
Characters assigned to rows (simulate zigzag, direction reverses at row 0 and row 2):

Index:  0  1  2  3  4  5  6  7  8  9 10 11 12 13
Char:   P  A  Y  P  A  L  I  S  H  I  R  I  N  G
Row:    0  1  2  1  0  1  2  1  0  1  2  1  0  1

Row 0: P(0)  A(4)  H(8)  N(12)  → "PAHN"
Row 1: A(1)  P(3)  L(5)  S(7)  I(9)  I(11) G(13) → "APLSIIG"
Row 2: Y(2)  I(6)  R(10) → "YIR"

Concatenated: "PAHN" + "APLSIIG" + "YIR" = "PAHNAPLSIIGYIR" ✅
```

#### Example 2: `s = "PAYPALISHIRING", numRows = 4`

```text
Row:    0  1  2  3  2  1  0  1  2  3  2  1  0  1
Char:   P  A  Y  P  A  L  I  S  H  I  R  I  N  G

Row 0: P  I  N  → "PIN"
Row 1: A  L  S  I  G → "ALSIG"
Row 2: Y  A  H  R → "YAHR"
Row 3: P  I  → "PI"

Concatenated: "PIN" + "ALSIG" + "YAHR" + "PI" = "PINALSIGYAHRPI" ✅
```

#### Example 3: `s = "A", numRows = 1`

```text
Only one row, one character → "A"
No zigzag is possible. Return as-is.
```

### 6. Key Observations

1. **Zigzag period** — The pattern repeats every `2 * (numRows - 1)` characters. This is the "cycle length".
2. **Row assignment** — Character at index `i` in the original string belongs to a row that follows the pattern: 0, 1, 2, …, numRows-1, numRows-2, …, 1, 0, 1, 2, … (a bounce).
3. **Direction flag** — Simulating the bounce with a `goingDown` boolean is the cleanest approach.
4. **Edge cases** — `numRows == 1` and `numRows >= len(s)` both reduce to returning `s` unchanged.

### 7. Pattern identification

| Pattern | Why it fits | Notes |
|---|---|---|
| Simulate with rows | Directly models the zigzag bounce | Cleanest code |
| Mathematical index | Compute row membership via period formula | No extra buffers needed conceptually, same O(n) |

**Chosen pattern:** `Simulate (Approach 1)` for clarity; `Math (Approach 2)` as the elegant alternative.

---

## Approach 1: Simulate Row Traversal

### Thought process

> Imagine writing the string physically: start at row 0, move **down** one row per character, and when you hit the last row, reverse direction and move **up**. When you hit row 0 again, reverse again.
>
> Track which row each character belongs to, accumulate in per-row buffers, then concatenate.

### Algorithm (step-by-step)

1. If `numRows == 1` or `numRows >= len(s)`, return `s` immediately.
2. Create `numRows` empty string builders (one per row).
3. Initialize `curRow = 0`, `goingDown = false`.
4. For each character `c` in `s`:
   - Append `c` to `rows[curRow]`.
   - If `curRow == 0` or `curRow == numRows - 1`, flip `goingDown`.
   - Move: `curRow += 1` if going down, else `curRow -= 1`.
5. Concatenate all rows in order → return result.

### Pseudocode

```text
function convert(s, numRows):
    if numRows == 1 or numRows >= len(s):
        return s

    rows = array of numRows empty string builders
    curRow = 0
    goingDown = false

    for c in s:
        rows[curRow].append(c)
        if curRow == 0 or curRow == numRows - 1:
            goingDown = not goingDown
        curRow += 1 if goingDown else -1

    return join(rows)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each of the n characters is visited exactly once |
| **Space** | O(n) | The row buffers collectively hold all n characters |

### Implementation

#### Go

```go
func convert(s string, numRows int) string {
    if numRows == 1 || numRows >= len(s) {
        return s
    }

    rows := make([][]byte, numRows)
    for i := range rows {
        rows[i] = make([]byte, 0)
    }

    curRow := 0
    goingDown := false

    for i := 0; i < len(s); i++ {
        rows[curRow] = append(rows[curRow], s[i])
        if curRow == 0 || curRow == numRows-1 {
            goingDown = !goingDown
        }
        if goingDown {
            curRow++
        } else {
            curRow--
        }
    }

    result := make([]byte, 0, len(s))
    for _, row := range rows {
        result = append(result, row...)
    }
    return string(result)
}
```

#### Java

```java
public String convert(String s, int numRows) {
    if (numRows == 1 || numRows >= s.length()) return s;

    StringBuilder[] rows = new StringBuilder[numRows];
    for (int i = 0; i < numRows; i++) rows[i] = new StringBuilder();

    int curRow = 0;
    boolean goingDown = false;

    for (char c : s.toCharArray()) {
        rows[curRow].append(c);
        if (curRow == 0 || curRow == numRows - 1) goingDown = !goingDown;
        curRow += goingDown ? 1 : -1;
    }

    StringBuilder result = new StringBuilder();
    for (StringBuilder row : rows) result.append(row);
    return result.toString();
}
```

#### Python

```python
def convert(self, s: str, numRows: int) -> str:
    if numRows == 1 or numRows >= len(s):
        return s

    rows = [[] for _ in range(numRows)]
    cur_row, going_down = 0, False

    for ch in s:
        rows[cur_row].append(ch)
        if cur_row == 0 or cur_row == numRows - 1:
            going_down = not going_down
        cur_row += 1 if going_down else -1

    return "".join(ch for row in rows for ch in row)
```

### Dry Run

```text
Input: s = "PAYPALISHIRING", numRows = 3

rows = [[], [], []]
curRow=0, goingDown=false

i=0  c='P'  curRow=0 → rows[0]=['P'] | hit top → goingDown=true  | curRow→1
i=1  c='A'  curRow=1 → rows[1]=['A'] |                           | curRow→2
i=2  c='Y'  curRow=2 → rows[2]=['Y'] | hit bot → goingDown=false | curRow→1
i=3  c='P'  curRow=1 → rows[1]=['A','P'] |                       | curRow→0
i=4  c='A'  curRow=0 → rows[0]=['P','A'] | hit top → goingDown=true | curRow→1
i=5  c='L'  curRow=1 → rows[1]=['A','P','L'] |                   | curRow→2
i=6  c='I'  curRow=2 → rows[2]=['Y','I'] | hit bot → goingDown=false | curRow→1
i=7  c='S'  curRow=1 → rows[1]=['A','P','L','S'] |               | curRow→0
i=8  c='H'  curRow=0 → rows[0]=['P','A','H'] | hit top → true   | curRow→1
i=9  c='I'  curRow=1 → rows[1]=['A','P','L','S','I'] |           | curRow→2
i=10 c='R'  curRow=2 → rows[2]=['Y','I','R'] | hit bot → false  | curRow→1
i=11 c='I'  curRow=1 → rows[1]=['A','P','L','S','I','I'] |       | curRow→0
i=12 c='N'  curRow=0 → rows[0]=['P','A','H','N'] | hit top →true | curRow→1
i=13 c='G'  curRow=1 → rows[1]=['A','P','L','S','I','I','G'] |   | curRow→2

rows[0] = "PAHN"
rows[1] = "APLSIIG"
rows[2] = "YIR"

Result: "PAHNAPLSIIGYIR" ✅
```

---

## Approach 2: Mathematical (Direct Index)

### Thought process

> Instead of simulating the traversal, observe the **periodic structure**.
>
> The zigzag cycle length is `period = 2 * (numRows - 1)`.
> For each row `r`, the characters that belong to it are at positions:
> - Main column: `r`, `r + period`, `r + 2*period`, …
> - Middle diagonal (for interior rows only): `period - r`, `period - r + period`, …
>
> We iterate row by row and pick characters at those computed indices directly.

### Algorithm (step-by-step)

1. If `numRows == 1` or `numRows >= len(s)`, return `s`.
2. Compute `period = 2 * (numRows - 1)`.
3. For each row `r` from `0` to `numRows - 1`:
   - For each cycle starting at index `j = r, r + period, r + 2*period, …`:
     - Always add `s[j]` (main column character).
     - If `r != 0` and `r != numRows - 1` (interior row) and `j + period - 2*r < len(s)`:
       - Add `s[j + period - 2*r]` (diagonal character within the same cycle).
4. Concatenate and return.

### Pseudocode

```text
function convert(s, numRows):
    if numRows == 1 or numRows >= len(s):
        return s

    period = 2 * (numRows - 1)
    result = []

    for r = 0 to numRows - 1:
        for j = r; j < len(s); j += period:
            result.append(s[j])                     // main column
            if r != 0 and r != numRows - 1:
                diag = j + period - 2*r
                if diag < len(s):
                    result.append(s[diag])           // diagonal

    return join(result)
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Every character is visited exactly once across all row iterations |
| **Space** | O(n) | Output buffer holds all n characters |

### Implementation

#### Go

```go
func convertMath(s string, numRows int) string {
    if numRows == 1 || numRows >= len(s) {
        return s
    }

    period := 2 * (numRows - 1)
    result := make([]byte, 0, len(s))

    for r := 0; r < numRows; r++ {
        for j := r; j < len(s); j += period {
            // Main column character
            result = append(result, s[j])
            // Diagonal character (interior rows only)
            if r != 0 && r != numRows-1 {
                diag := j + period - 2*r
                if diag < len(s) {
                    result = append(result, s[diag])
                }
            }
        }
    }
    return string(result)
}
```

#### Java

```java
public String convertMath(String s, int numRows) {
    if (numRows == 1 || numRows >= s.length()) return s;

    int period = 2 * (numRows - 1);
    StringBuilder result = new StringBuilder();

    for (int r = 0; r < numRows; r++) {
        for (int j = r; j < s.length(); j += period) {
            result.append(s.charAt(j));
            if (r != 0 && r != numRows - 1) {
                int diag = j + period - 2 * r;
                if (diag < s.length()) result.append(s.charAt(diag));
            }
        }
    }
    return result.toString();
}
```

#### Python

```python
def convert_math(self, s: str, numRows: int) -> str:
    if numRows == 1 or numRows >= len(s):
        return s

    period = 2 * (numRows - 1)
    result = []

    for r in range(numRows):
        for j in range(r, len(s), period):
            result.append(s[j])            # main column
            if 0 < r < numRows - 1:        # interior row has a diagonal char
                diag = j + period - 2 * r
                if diag < len(s):
                    result.append(s[diag])

    return "".join(result)
```

### Dry Run

```text
Input: s = "PAYPALISHIRING", numRows = 3
period = 2 * (3 - 1) = 4

Row 0 (top, no diagonals):
  j=0 → s[0]='P'
  j=4 → s[4]='A'
  j=8 → s[8]='H'
  j=12 → s[12]='N'
  → "PAHN"

Row 1 (interior, diagonals exist, period - 2*r = 4-2 = 2):
  j=1 → s[1]='A', diag=1+2=3 → s[3]='P'
  j=5 → s[5]='L', diag=5+2=7 → s[7]='S'
  j=9 → s[9]='I', diag=9+2=11 → s[11]='I'
  j=13 → s[13]='G', diag=13+2=15 → out of bounds
  → "APLSIIG"

Row 2 (bottom, no diagonals):
  j=2 → s[2]='Y'
  j=6 → s[6]='I'
  j=10 → s[10]='R'
  → "YIR"

Result: "PAHN" + "APLSIIG" + "YIR" = "PAHNAPLSIIGYIR" ✅
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Simulate Row Traversal | O(n) | O(n) | Intuitive, easy to reason about | Slightly more code |
| 2 | Mathematical (Direct) | O(n) | O(n) | No simulation state needed | Requires understanding of period formula |

### Which solution to choose?

- **In an interview:** Approach 1 (Simulate) — easiest to explain and code without errors under pressure.
- **On Leetcode:** Both are equivalent; Approach 2 avoids allocating the intermediate row arrays.
- **For learning:** Approach 2 teaches pattern recognition (periodicity in strings).

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | `numRows = 1` | `s="ABCDE"`, `numRows=1` | `"ABCDE"` | No reordering — single row is a straight line |
| 2 | `numRows >= len(s)` | `s="AB"`, `numRows=5` | `"AB"` | Each char gets its own row, order unchanged |
| 3 | Single character | `s="A"`, `numRows=1` | `"A"` | Trivially the same |
| 4 | Two rows | `s="ABCDE"`, `numRows=2` | `"ACEBD"` | Even indices go to row 0, odd to row 1 |
| 5 | `numRows = len(s)` | `s="ABC"`, `numRows=3` | `"ABC"` | Each char on its own row, read top-to-bottom = original |
| 6 | Full cycle | `s="PAYPALISHIRING"`, `numRows=3` | `"PAHNAPLSIIGYIR"` | Standard problem example |

---

## Common Mistakes

### Mistake 1: Forgetting the edge case for `numRows = 1`

```python
# ❌ WRONG — will trigger index out of bounds or wrong result
# period = 2 * (1 - 1) = 0 → infinite loop or division by zero

# ✅ CORRECT — guard at the top
if numRows == 1 or numRows >= len(s):
    return s
```

**Reason:** When `numRows = 1`, `period = 0` which breaks both approaches. Always return early.

### Mistake 2: Initializing `goingDown = true` instead of `false`

```python
# ❌ WRONG — starts moving UP from row 0, which is already at the top
going_down = True   # starts at 0, immediately tries curRow=-1

# ✅ CORRECT — start false; the bounce at curRow=0 flips it to true on the first character
going_down = False
```

**Reason:** The direction flip happens **after** appending the character. At `curRow = 0`, `goingDown` starts `false`, the character is appended, the flip makes it `true`, then we move to `curRow = 1`. This is correct.

### Mistake 3: Off-by-one in the Mathematical approach — wrong diagonal formula

```python
# ❌ WRONG — diagonal index is wrong
diag = j + 2 * r          # incorrect formula

# ✅ CORRECT — diagonal is at j + (period - 2*r) within the same cycle
diag = j + period - 2 * r
```

**Reason:** In a cycle of length `period`, the main column character for row `r` is at offset `r` from the cycle start, and the diagonal character is at offset `period - r`. The gap between them is `period - 2*r`.

### Mistake 4: Not skipping first and last rows in Mathematical approach

```python
# ❌ WRONG — adds extra characters for row 0 and row numRows-1
if diag < len(s):
    result.append(s[diag])

# ✅ CORRECT — only interior rows have a diagonal character
if 0 < r < numRows - 1:
    diag = j + period - 2 * r
    if diag < len(s):
        result.append(s[diag])
```

**Reason:** For row 0 (top) and row `numRows-1` (bottom), there is no diagonal column — just the main vertical column.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | 🟡 Medium | String manipulation with index logic |
| 2 | [8. String to Integer (atoi)](https://leetcode.com/problems/string-to-integer-atoi/) | 🟡 Medium | String character-by-character processing |
| 3 | [443. String Compression](https://leetcode.com/problems/string-compression/) | 🟡 Medium | In-place string rewriting |
| 4 | [468. Validate IP Address](https://leetcode.com/problems/validate-ip-address/) | 🟡 Medium | String pattern parsing |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Simulate** tab — Watch each character being placed into its row with the direction arrow bouncing between rows.
> - **Math** tab — Highlights which characters belong to each row using the period formula, no simulation needed.
> - **Compare** tab — Side-by-side view of both approaches building the result character by character.
