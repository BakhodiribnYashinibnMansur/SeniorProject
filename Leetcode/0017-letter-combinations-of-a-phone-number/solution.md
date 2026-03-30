# 0017. Letter Combinations of a Phone Number

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking (DFS)](#approach-1-backtracking-dfs)
4. [Approach 2: Iterative (BFS-like)](#approach-2-iterative-bfs-like)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [17. Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Hash Table`, `String`, `Backtracking` |

### Description

> Given a string containing digits from `2-9` inclusive, return all possible letter combinations that the number could represent. Return the answer in **any order**.
>
> A mapping of digits to letters (just like on the telephone buttons) is given below. Note that 1 does not map to any letters.

```
2 -> abc    3 -> def    4 -> ghi    5 -> jkl
6 -> mno    7 -> pqrs   8 -> tuv    9 -> wxyz
```

### Examples

```
Example 1:
Input: digits = "23"
Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]

Example 2:
Input: digits = ""
Output: []

Example 3:
Input: digits = "2"
Output: ["a","b","c"]
```

### Constraints

- `0 <= digits.length <= 4`
- `digits[i]` is a digit in the range `['2', '9']`

---

## Problem Breakdown

### 1. What is being asked?

Generate **all possible letter combinations** that a string of phone digits could represent. Each digit maps to 3 or 4 letters (like a phone keypad). We must produce every valid combination by picking exactly one letter per digit.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `digits` | `string` | A string of digits from '2' to '9' |

Important observations about the input:
- The string can be **empty** (return empty list)
- Maximum length is **4** (at most 4^4 = 256 combinations)
- Only digits 2-9 are valid (no 0 or 1)
- Each digit maps to 3 or 4 letters

### 3. What is the output?

- A **list of strings**, each string being one valid combination
- Each combination has **length equal to the number of digits**
- Order of the output does not matter
- Return an empty list if the input is empty

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `digits.length <= 4` | Maximum 4^4 = 256 combinations — any approach works |
| Digits are 2-9 only | Each digit maps to 3 or 4 letters |
| Any order accepted | No sorting requirement for output |

### 5. Step-by-step example analysis

#### Example 1: `digits = "23"`

```text
Digit '2' maps to: a, b, c
Digit '3' maps to: d, e, f

Pick one letter from each digit:
  '2' -> 'a': 'a' + 'd' = "ad", 'a' + 'e' = "ae", 'a' + 'f' = "af"
  '2' -> 'b': 'b' + 'd' = "bd", 'b' + 'e' = "be", 'b' + 'f' = "bf"
  '2' -> 'c': 'c' + 'd' = "cd", 'c' + 'e' = "ce", 'c' + 'f' = "cf"

Result: ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"]
Total: 3 * 3 = 9 combinations
```

#### Example 3: `digits = "2"`

```text
Digit '2' maps to: a, b, c

Only one digit, so each letter is a complete combination:
Result: ["a", "b", "c"]
Total: 3 combinations
```

### 6. Key Observations

1. **This is a Cartesian product** — for each digit, pick one letter; the result is all possible selections.
2. **The number of combinations** is the product of letter counts: e.g., "23" = 3 * 3 = 9, "79" = 4 * 4 = 16.
3. **Backtracking naturally fits** — build a combination one character at a time, exploring all choices.
4. **Iterative expansion also works** — start with `[""]`, and for each digit, append each mapped letter to every existing combination.
5. **No duplicates to worry about** — each digit position is independent, so all combinations are unique.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking / DFS | Build combinations one digit at a time, explore all branches | This problem (pick one letter per digit) |
| Iterative / BFS | Expand combinations level by level | Same problem, different implementation |
| Cartesian Product | Each digit contributes an independent set of choices | `itertools.product` in Python |

**Chosen patterns:** `Backtracking` (primary) and `Iterative` (alternative)
**Reason:** Both naturally express the "pick one from each group" structure. Backtracking uses O(n) space (recursion), while iterative stores all intermediate results.

---

## Approach 1: Backtracking (DFS)

### Thought process

> Build each combination one character at a time. At each position (digit), try every letter mapped to that digit. When all positions are filled, add the combination to the result. Then backtrack (undo the last choice) and try the next letter.

### Algorithm (step-by-step)

1. If `digits` is empty, return `[]`
2. Create a mapping of digit -> letters
3. Define a recursive `backtrack(index, current)` function:
   - If `index == len(digits)` -> add `current` to result (base case)
   - Otherwise, for each letter mapped to `digits[index]`:
     - Append the letter to `current`
     - Recurse with `index + 1`
     - Remove the last character (backtrack)
4. Call `backtrack(0, [])` and return the result

### Pseudocode

```text
function letterCombinations(digits):
    if digits is empty: return []
    result = []
    phone = {2: "abc", 3: "def", ..., 9: "wxyz"}

    function backtrack(index, current):
        if index == len(digits):
            result.append(join(current))
            return
        for letter in phone[digits[index]]:
            current.append(letter)
            backtrack(index + 1, current)
            current.pop()    // backtrack

    backtrack(0, [])
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(4^n * n) | At most 4 branches per digit, n digits deep, O(n) to build each string |
| **Space** | O(n) | Recursion stack depth = n (output not counted) |

### Implementation

#### Go

```go
func letterCombinations(digits string) []string {
    if len(digits) == 0 {
        return []string{}
    }
    phone := map[byte]string{
        '2': "abc", '3': "def", '4': "ghi", '5': "jkl",
        '6': "mno", '7': "pqrs", '8': "tuv", '9': "wxyz",
    }
    result := []string{}
    var backtrack func(index int, current []byte)
    backtrack = func(index int, current []byte) {
        if index == len(digits) {
            result = append(result, string(current))
            return
        }
        for _, ch := range phone[digits[index]] {
            current = append(current, byte(ch))
            backtrack(index+1, current)
            current = current[:len(current)-1]
        }
    }
    backtrack(0, []byte{})
    return result
}
```

#### Java

```java
public List<String> letterCombinations(String digits) {
    List<String> result = new ArrayList<>();
    if (digits == null || digits.isEmpty()) return result;
    Map<Character, String> phone = Map.of(
        '2', "abc", '3', "def", '4', "ghi", '5', "jkl",
        '6', "mno", '7', "pqrs", '8', "tuv", '9', "wxyz");
    backtrack(digits, 0, new StringBuilder(), result, phone);
    return result;
}

private void backtrack(String digits, int index, StringBuilder current,
                       List<String> result, Map<Character, String> phone) {
    if (index == digits.length()) {
        result.add(current.toString());
        return;
    }
    for (char ch : phone.get(digits.charAt(index)).toCharArray()) {
        current.append(ch);
        backtrack(digits, index + 1, current, result, phone);
        current.deleteCharAt(current.length() - 1);
    }
}
```

#### Python

```python
def letterCombinations(self, digits: str) -> list[str]:
    if not digits:
        return []
    phone = {"2": "abc", "3": "def", "4": "ghi", "5": "jkl",
             "6": "mno", "7": "pqrs", "8": "tuv", "9": "wxyz"}
    result = []

    def backtrack(index, current):
        if index == len(digits):
            result.append("".join(current))
            return
        for ch in phone[digits[index]]:
            current.append(ch)
            backtrack(index + 1, current)
            current.pop()

    backtrack(0, [])
    return result
```

### Dry Run

```text
Input: digits = "23"
phone: {'2': "abc", '3': "def"}

backtrack(0, []):
  digit '2', letters = "abc"

  letter 'a': current = ['a']
    backtrack(1, ['a']):
      digit '3', letters = "def"
      letter 'd': current = ['a','d'] -> index==2, add "ad" -> pop -> ['a']
      letter 'e': current = ['a','e'] -> index==2, add "ae" -> pop -> ['a']
      letter 'f': current = ['a','f'] -> index==2, add "af" -> pop -> ['a']
    pop -> []

  letter 'b': current = ['b']
    backtrack(1, ['b']):
      letter 'd': add "bd", letter 'e': add "be", letter 'f': add "bf"
    pop -> []

  letter 'c': current = ['c']
    backtrack(1, ['c']):
      letter 'd': add "cd", letter 'e': add "ce", letter 'f': add "cf"
    pop -> []

Result: ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"]
```

---

## Approach 2: Iterative (BFS-like)

### The alternative to recursion

> Instead of building combinations depth-first, build them **level by level**. Start with an empty string. For each digit, take every existing partial combination and extend it with every letter mapped to that digit.

### Algorithm (step-by-step)

1. If `digits` is empty, return `[]`
2. Initialize `result = [""]` (one empty combination)
3. For each digit in `digits`:
   - Create a new empty list `newResult`
   - For each existing combination in `result`:
     - For each letter mapped to the current digit:
       - Append `combination + letter` to `newResult`
   - Replace `result` with `newResult`
4. Return `result`

### Pseudocode

```text
function letterCombinationsIterative(digits):
    if digits is empty: return []
    result = [""]
    for digit in digits:
        letters = phone[digit]
        newResult = []
        for combo in result:
            for letter in letters:
                newResult.append(combo + letter)
        result = newResult
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(4^n * n) | Same total work as backtracking |
| **Space** | O(4^n * n) | Stores all intermediate combinations at each level |

### Implementation

#### Go

```go
func letterCombinationsIterative(digits string) []string {
    if len(digits) == 0 {
        return []string{}
    }
    phone := map[byte]string{
        '2': "abc", '3': "def", '4': "ghi", '5': "jkl",
        '6': "mno", '7': "pqrs", '8': "tuv", '9': "wxyz",
    }
    result := []string{""}
    for i := 0; i < len(digits); i++ {
        letters := phone[digits[i]]
        newResult := []string{}
        for _, combo := range result {
            for j := 0; j < len(letters); j++ {
                newResult = append(newResult, combo+string(letters[j]))
            }
        }
        result = newResult
    }
    return result
}
```

#### Java

```java
public List<String> letterCombinationsIterative(String digits) {
    if (digits == null || digits.isEmpty()) return new ArrayList<>();
    List<String> result = new ArrayList<>();
    result.add("");
    for (char digit : digits.toCharArray()) {
        String letters = PHONE.get(digit);
        List<String> newResult = new ArrayList<>();
        for (String combo : result) {
            for (char ch : letters.toCharArray()) {
                newResult.add(combo + ch);
            }
        }
        result = newResult;
    }
    return result;
}
```

#### Python

```python
def letterCombinationsIterative(self, digits: str) -> list[str]:
    if not digits:
        return []
    phone = {"2": "abc", "3": "def", "4": "ghi", "5": "jkl",
             "6": "mno", "7": "pqrs", "8": "tuv", "9": "wxyz"}
    result = [""]
    for digit in digits:
        result = [combo + ch for combo in result for ch in phone[digit]]
    return result
```

### Dry Run

```text
Input: digits = "23"

Step 0: result = [""]

Step 1: digit '2', letters = "abc"
  "" + 'a' = "a"
  "" + 'b' = "b"
  "" + 'c' = "c"
  result = ["a", "b", "c"]

Step 2: digit '3', letters = "def"
  "a" + 'd' = "ad"   "a" + 'e' = "ae"   "a" + 'f' = "af"
  "b" + 'd' = "bd"   "b" + 'e' = "be"   "b" + 'f' = "bf"
  "c" + 'd' = "cd"   "c" + 'e' = "ce"   "c" + 'f' = "cf"
  result = ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"]

Final result: ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking (DFS) | O(4^n * n) | O(n) | Minimal extra memory, intuitive recursion | Stack depth = n |
| 2 | Iterative (BFS) | O(4^n * n) | O(4^n * n) | No recursion, simple loop | Stores all intermediate combos |

### Which solution to choose?

- **In an interview:** Approach 1 (Backtracking) — demonstrates understanding of recursion and backtracking pattern
- **In production:** Either works — input is at most 4 digits (256 combinations max)
- **On Leetcode:** Both pass easily given the small constraint (n <= 4)
- **For learning:** Both — Backtracking teaches DFS/recursion, Iterative teaches BFS-like expansion

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | LeetCode Example 1 | `"23"` | `["ad","ae","af","bd","be","bf","cd","ce","cf"]` | Standard two-digit case |
| 2 | Empty string | `""` | `[]` | No digits means no combinations |
| 3 | Single digit (3 letters) | `"2"` | `["a","b","c"]` | Only one digit with 3 choices |
| 4 | Single digit (4 letters) | `"7"` | `["p","q","r","s"]` | Digit 7 has 4 letter mappings |
| 5 | Two 4-letter digits | `"79"` | 16 combinations | 4 * 4 = 16 total |
| 6 | Maximum length | `"2379"` | 108 combinations | 3 * 3 * 4 * 4 = 144... but any 4-digit combo works |
| 7 | Same digit repeated | `"22"` | `["aa","ab","ac","ba","bb","bc","ca","cb","cc"]` | Same letters used at each position |
| 8 | All 4-letter digits | `"7979"` | 256 combinations | Maximum possible: 4^4 = 256 |

---

## Common Mistakes

### Mistake 1: Returning `[""]` instead of `[]` for empty input

```python
# WRONG — returns a list with one empty string
def letterCombinations(self, digits):
    result = [""]
    for digit in digits:
        result = [c + ch for c in result for ch in phone[digit]]
    return result  # returns [""] when digits is ""

# CORRECT — check for empty input first
def letterCombinations(self, digits):
    if not digits:
        return []
    result = [""]
    for digit in digits:
        result = [c + ch for c in result for ch in phone[digit]]
    return result
```

**Reason:** The iterative approach starts with `[""]` as a seed. If no digits are processed, it returns the seed unchanged.

### Mistake 2: Forgetting to undo the choice (backtrack)

```python
# WRONG — current keeps growing, combinations are incorrect
def backtrack(index, current):
    if index == len(digits):
        result.append("".join(current))
        return
    for ch in phone[digits[index]]:
        current.append(ch)
        backtrack(index + 1, current)
        # Missing: current.pop()

# CORRECT — pop after recursion to undo the choice
def backtrack(index, current):
    if index == len(digits):
        result.append("".join(current))
        return
    for ch in phone[digits[index]]:
        current.append(ch)
        backtrack(index + 1, current)
        current.pop()  # backtrack!
```

**Reason:** Without popping, `current` accumulates all letters across branches, producing wrong combinations.

### Mistake 3: Including mapping for digits 0 and 1

```python
# WRONG — digits 0 and 1 have no letters on a phone keypad
phone = {"0": "+", "1": "", "2": "abc", ...}

# CORRECT — only map digits 2-9
phone = {"2": "abc", "3": "def", ..., "9": "wxyz"}
```

**Reason:** The problem constraints state digits are 2-9 only. Including 0/1 could cause empty iterations or wrong results.

### Mistake 4: Using string concatenation in backtracking (performance)

```python
# SLOWER — creates new string at every step: O(n) per concatenation
def backtrack(index, current_str):
    if index == len(digits):
        result.append(current_str)
        return
    for ch in phone[digits[index]]:
        backtrack(index + 1, current_str + ch)  # new string each time

# FASTER — use a list and join only at base case
def backtrack(index, current_list):
    if index == len(digits):
        result.append("".join(current_list))
        return
    for ch in phone[digits[index]]:
        current_list.append(ch)
        backtrack(index + 1, current_list)
        current_list.pop()
```

**Reason:** String concatenation in Python creates a new object each time. Using a list with `append/pop` is more efficient.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [22. Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) | :yellow_circle: Medium | Backtracking to generate all valid combinations |
| 2 | [39. Combination Sum](https://leetcode.com/problems/combination-sum/) | :yellow_circle: Medium | Backtracking with choices at each step |
| 3 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Backtracking to explore all orderings |
| 4 | [77. Combinations](https://leetcode.com/problems/combinations/) | :yellow_circle: Medium | Generate all combinations of k elements |
| 5 | [78. Subsets](https://leetcode.com/problems/subsets/) | :yellow_circle: Medium | Backtracking to generate all subsets |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Phone keypad** shows the digit-to-letter mapping visually
> - **Backtracking** tab — DFS tree building combinations one letter at a time
> - **Iterative** tab — BFS-like expansion of combinations level by level
> - **Step/Play/Reset** controls with adjustable speed
