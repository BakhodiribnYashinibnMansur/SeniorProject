# 0022. Generate Parentheses

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Backtracking](#approach-1-backtracking)
4. [Approach 2: Dynamic Programming (Catalan Build)](#approach-2-dynamic-programming-catalan-build)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [22. Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `String`, `Dynamic Programming`, `Backtracking` |

### Description

> Given `n` pairs of parentheses, write a function to **generate all combinations** of well-formed parentheses.

### Examples

```
Example 1:
Input: n = 3
Output: ["((()))","(()())","(())()","()(())","()()()"]

Example 2:
Input: n = 1
Output: ["()"]
```

### Constraints

- `1 <= n <= 8`

---

## Problem Breakdown

### 1. What is being asked?

Generate **every possible** string of length `2n` that consists of properly nested parentheses. Each result must use exactly `n` opening and `n` closing parentheses.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | Number of parenthesis pairs |

Important observations about the input:
- `n` is always at least 1 -- there is always at least one pair
- Maximum `n = 8` means at most 1430 valid strings (the 8th Catalan number)
- The answer count follows the **Catalan number** sequence: 1, 2, 5, 14, 42, 132, 429, 1430

### 3. What is the output?

- A **list of strings**, where each string is a valid combination of `n` pairs of parentheses
- The order of strings does not matter

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `1 <= n <= 8` | Very small input size -- even exponential solutions run instantly |
| Output is all valid combos | Must enumerate, not just count |
| Well-formed | At every prefix, `count('(') >= count(')')` and total of each is `n` |

### 5. Step-by-step example analysis

#### Example: `n = 3`

```text
We need strings of length 6 using exactly 3 '(' and 3 ')'.

At each position, we can place '(' if we haven't used all n.
We can place ')' only if open_count > close_count (ensures validity).

                    ""
                    |
                   "("
                 /     \
              "(("     "()"
             /    \       \
          "((("  "(()("  "()("
            |      |   \    |  \
        "((())" "(()())" ... ...
            |      |
       "((()))" "(()())"  ...

Full results: ((())), (()()),  (())(), ()(()), ()()()
```

### 6. Key Observations

1. **Choice at each step** -- We can add `(` if `open < n`, and `)` if `close < open`.
2. **Constraint guarantees validity** -- By only adding `)` when `close < open`, we never create an invalid prefix.
3. **Base case** -- When the string has length `2n`, it is complete and valid.
4. **Catalan numbers** -- The count of valid combinations for `n` pairs is the nth Catalan number `C(n) = C(2n, n) / (n+1)`.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Backtracking | Build solutions character by character with constraints | Generate Parentheses (this problem) |
| Dynamic Programming | Build n-pair solutions from smaller solutions | Catalan number recurrence |
| Recursion | Natural tree structure of choices | Binary choice at each step |

**Chosen pattern:** `Backtracking`
**Reason:** The problem naturally forms a decision tree where we choose `(` or `)` at each position, with pruning constraints that keep all paths valid.

---

## Approach 1: Backtracking

### Thought process

> Build the string one character at a time. At each step we have two choices: add `(` or add `)`.
> We can add `(` only if we haven't used all `n` opening parentheses.
> We can add `)` only if the number of `)` used so far is less than the number of `(` used.
> When the string reaches length `2n`, we have a complete valid combination.

### Algorithm (step-by-step)

1. Start with an empty string and counters `open = 0`, `close = 0`
2. If `open + close == 2n`: add the current string to results, return
3. If `open < n`: recurse with `(` appended and `open + 1`
4. If `close < open`: recurse with `)` appended and `close + 1`
5. Return the collected results

### Pseudocode

```text
function generateParenthesis(n):
    result = []

    function backtrack(current, open, close):
        if len(current) == 2 * n:
            result.add(current)
            return

        if open < n:
            backtrack(current + '(', open + 1, close)

        if close < open:
            backtrack(current + ')', open, close + 1)

    backtrack("", 0, 0)
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(4^n / sqrt(n)) | The nth Catalan number bounds the number of valid sequences. Each sequence has length 2n. |
| **Space** | O(n) | Recursion depth is at most 2n. Output space is O(n * C(n)) but that's the required output. |

### Implementation

#### Go

```go
func generateParenthesis(n int) []string {
    result := []string{}

    var backtrack func(current []byte, open, close int)
    backtrack = func(current []byte, open, close int) {
        if len(current) == 2*n {
            result = append(result, string(current))
            return
        }
        if open < n {
            backtrack(append(current, '('), open+1, close)
        }
        if close < open {
            backtrack(append(current, ')'), open, close+1)
        }
    }

    backtrack([]byte{}, 0, 0)
    return result
}
```

#### Java

```java
class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder current,
                           int open, int close, int n) {
        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }
        if (open < n) {
            current.append('(');
            backtrack(result, current, open + 1, close, n);
            current.deleteCharAt(current.length() - 1);
        }
        if (close < open) {
            current.append(')');
            backtrack(result, current, open, close + 1, n);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
```

#### Python

```python
class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        result = []

        def backtrack(current: list[str], open_count: int, close_count: int):
            if len(current) == 2 * n:
                result.append("".join(current))
                return

            if open_count < n:
                current.append("(")
                backtrack(current, open_count + 1, close_count)
                current.pop()

            if close_count < open_count:
                current.append(")")
                backtrack(current, open_count, close_count + 1)
                current.pop()

        backtrack([], 0, 0)
        return result
```

### Dry Run

```text
Input: n = 2

backtrack("", open=0, close=0)
  open < 2 -> backtrack("(", 1, 0)
    open < 2 -> backtrack("((", 2, 0)
      close < open -> backtrack("(()", 2, 1)
        close < open -> backtrack("(())", 2, 2)
          len == 4 -> ADD "(())"
    close < open -> backtrack("()", 1, 1)
      open < 2 -> backtrack("()(", 2, 1)
        close < open -> backtrack("()()", 2, 2)
          len == 4 -> ADD "()()"

Result: ["(())", "()()"]
```

```text
Input: n = 3

backtrack("", 0, 0)
  -> "(" (1,0)
    -> "((" (2,0)
      -> "(((" (3,0)
        -> "((()" (3,1) -> "((())" (3,2) -> "((()))" ADD
      -> "(()" (2,1)
        -> "(()(" (3,1)
          -> "(()()" (3,2) -> "(()())" ADD
        -> "(())" (2,2)
          -> "(())(" (3,2) -> "(())()" ADD
    -> "()" (1,1)
      -> "()(" (2,1)
        -> "()((" (3,1)
          -> "()(()" (3,2) -> "()(())" ADD
        -> "()()" (2,2)
          -> "()()(" (3,2) -> "()()()" ADD

Result: ["((()))", "(()())", "(())()", "()(())", "()()()"]
```

---

## Approach 2: Dynamic Programming (Catalan Build)

### Thought process

> Use the recursive structure of Catalan numbers: every valid string of `n` pairs can be written as `(` + [valid string of `a` pairs] + `)` + [valid string of `b` pairs], where `a + b = n - 1`.
> Build solutions bottom-up from `n = 0` to the target `n`.

### Algorithm (step-by-step)

1. Create `dp[0] = [""]` (empty string is the only valid combo for 0 pairs)
2. For each `i` from 1 to `n`:
   - For each split `j` from 0 to `i-1`:
     - For each `left` in `dp[j]` and `right` in `dp[i-1-j]`:
       - Add `"(" + left + ")" + right` to `dp[i]`
3. Return `dp[n]`

### Pseudocode

```text
function generateParenthesis(n):
    dp = map of int -> list of strings
    dp[0] = [""]

    for i from 1 to n:
        dp[i] = []
        for j from 0 to i-1:
            for left in dp[j]:
                for right in dp[i-1-j]:
                    dp[i].add("(" + left + ")" + right)

    return dp[n]
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(4^n / sqrt(n)) | Same as backtracking -- we generate all Catalan(n) strings of length 2n. |
| **Space** | O(4^n / sqrt(n)) | Stores all valid strings for dp[0] through dp[n]. |

### Implementation

#### Go

```go
func generateParenthesis(n int) []string {
    dp := make([][]string, n+1)
    dp[0] = []string{""}

    for i := 1; i <= n; i++ {
        dp[i] = []string{}
        for j := 0; j < i; j++ {
            for _, left := range dp[j] {
                for _, right := range dp[i-1-j] {
                    dp[i] = append(dp[i], "("+left+")"+right)
                }
            }
        }
    }

    return dp[n]
}
```

#### Java

```java
class Solution {
    public List<String> generateParenthesis(int n) {
        List<List<String>> dp = new ArrayList<>();
        dp.add(List.of(""));  // dp[0]

        for (int i = 1; i <= n; i++) {
            List<String> current = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                for (String left : dp.get(j)) {
                    for (String right : dp.get(i - 1 - j)) {
                        current.add("(" + left + ")" + right);
                    }
                }
            }
            dp.add(current);
        }

        return dp.get(n);
    }
}
```

#### Python

```python
class Solution:
    def generateParenthesis(self, n: int) -> list[str]:
        dp = {0: [""]}

        for i in range(1, n + 1):
            dp[i] = []
            for j in range(i):
                for left in dp[j]:
                    for right in dp[i - 1 - j]:
                        dp[i].append("(" + left + ")" + right)

        return dp[n]
```

### Dry Run

```text
Input: n = 3

dp[0] = [""]

dp[1]:
  j=0: left="" (from dp[0]), right="" (from dp[0])
       "(" + "" + ")" + "" = "()"
  dp[1] = ["()"]

dp[2]:
  j=0: left="" (dp[0]), right="()" (dp[1])
       "(" + "" + ")" + "()" = "()()"
  j=1: left="()" (dp[1]), right="" (dp[0])
       "(" + "()" + ")" + "" = "(())"
  dp[2] = ["()()", "(())"]

dp[3]:
  j=0: left="" (dp[0]), right from dp[2]: "()()", "(())"
       "()" + "()()" = "()()()"
       "()" + "(())" = "()(())"
  j=1: left="()" (dp[1]), right from dp[1]: "()"
       "(" + "()" + ")" + "()" = "(())()"
  j=2: left from dp[2]: "()()", "(())", right="" (dp[0])
       "(" + "()()" + ")" + "" = "(()())"
       "(" + "(())" + ")" + "" = "((()))"
  dp[3] = ["()()()", "()(())", "(())()", "(()())", "((()))"]

Result: ["()()()", "()(())", "(())()", "(()())", "((()))"]
(Same 5 strings as backtracking, different order)
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Backtracking | O(4^n / sqrt(n)) | O(n) call stack | Simple, intuitive, low memory | Recursive overhead |
| 2 | DP (Catalan Build) | O(4^n / sqrt(n)) | O(4^n / sqrt(n)) | Iterative, builds from subproblems | Higher memory, stores all intermediate results |

### Which solution to choose?

- **In an interview:** Approach 1 (Backtracking) -- clean, easy to explain, shows recursion skills
- **In production:** Approach 1 -- lower memory footprint
- **On Leetcode:** Approach 1 -- best combination of simplicity and performance
- **For learning:** Both -- Approach 1 teaches backtracking, Approach 2 shows the Catalan number structure

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Minimum input | `n = 1` | `["()"]` | Only one valid combination |
| 2 | Two pairs | `n = 2` | `["(())", "()()"]` | Two valid combinations |
| 3 | Three pairs | `n = 3` | 5 combinations | Catalan(3) = 5 |
| 4 | Four pairs | `n = 4` | 14 combinations | Catalan(4) = 14 |
| 5 | Maximum input | `n = 8` | 1430 combinations | Catalan(8) = 1430 |

---

## Common Mistakes

### Mistake 1: Allowing close count to exceed open count

```python
# WRONG -- generates invalid parentheses like ")("
def backtrack(current, open_count, close_count):
    if len(current) == 2 * n:
        result.append(current)
        return
    if open_count < n:
        backtrack(current + "(", open_count + 1, close_count)
    if close_count < n:  # BUG: should be close_count < open_count
        backtrack(current + ")", open_count, close_count + 1)

# CORRECT -- only add ')' when close_count < open_count
if close_count < open_count:
    backtrack(current + ")", open_count, close_count + 1)
```

**Reason:** The constraint `close < open` ensures that at every prefix, the string remains valid.

### Mistake 2: Not using backtracking (removing the last character)

```python
# WRONG in Java/Go -- StringBuilder not reverted
current.append('(')
backtrack(result, current, open + 1, close, n)
# Missing: current.deleteCharAt(current.length() - 1)
current.append(')')
backtrack(result, current, open, close + 1, n)

# CORRECT -- revert after each recursive call
current.append('(')
backtrack(result, current, open + 1, close, n)
current.deleteCharAt(current.length() - 1)  # backtrack!
```

**Reason:** Without reverting, the StringBuilder accumulates characters from all branches.

### Mistake 3: Using string concatenation in a tight loop (Python)

```python
# SLOW -- creates a new string every time
def backtrack(current, open_count, close_count):
    backtrack(current + "(", ...)  # O(n) string copy each call

# FASTER -- use a list and join at the end
def backtrack(current_list, open_count, close_count):
    if len(current_list) == 2 * n:
        result.append("".join(current_list))
        return
    current_list.append("(")
    backtrack(current_list, open_count + 1, close_count)
    current_list.pop()
```

**Reason:** String concatenation in Python is O(n) per call. Using a list with append/pop is O(1) amortized.

### Mistake 4: Off-by-one in DP approach

```python
# WRONG -- range should go from 0 to i-1, not 0 to i
for j in range(i + 1):  # BUG: includes j = i
    for left in dp[j]:
        for right in dp[i - 1 - j]:  # i-1-i = -1, KeyError!

# CORRECT -- j ranges from 0 to i-1
for j in range(i):
    for left in dp[j]:
        for right in dp[i - 1 - j]:
```

**Reason:** The decomposition is `(left)right` where `left` has `j` pairs and `right` has `i-1-j` pairs, so `j` must be at most `i-1`.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [20. Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | :green_circle: Easy | Validate parentheses (this problem generates them) |
| 2 | [17. Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) | :yellow_circle: Medium | Backtracking to generate combinations |
| 3 | [39. Combination Sum](https://leetcode.com/problems/combination-sum/) | :yellow_circle: Medium | Backtracking with pruning |
| 4 | [32. Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/) | :red_circle: Hard | Parentheses-related DP problem |
| 5 | [241. Different Ways to Add Parentheses](https://leetcode.com/problems/different-ways-to-add-parentheses/) | :yellow_circle: Medium | Divide and conquer with parentheses |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Backtracking** tab -- step-by-step tree exploration showing how parentheses are generated
> - **DP (Catalan Build)** tab -- shows how solutions are built from smaller subproblems
