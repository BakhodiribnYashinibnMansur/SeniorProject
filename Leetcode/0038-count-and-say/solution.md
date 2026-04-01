# 0038. Count and Say

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Iterative](#approach-1-iterative)
4. [Approach 2: Recursive](#approach-2-recursive)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [38. Count and Say](https://leetcode.com/problems/count-and-say/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `String` |

### Description

> The **count-and-say** sequence is a sequence of digit strings defined by the recursive formula:
>
> - `countAndSay(1) = "1"`
> - `countAndSay(n)` is the run-length encoding of `countAndSay(n - 1)`.
>
> Run-length encoding (RLE) is a string compression method that works by replacing consecutive identical characters (repeated 2 or more times) with the concatenation of the character and the number marking the count of the characters (length of the run). For example, to compress the string `"3322251"` we replace `"33"` with `"23"`, replace `"222"` with `"32"`, replace `"5"` with `"15"` and replace `"1"` with `"11"`. Thus the compressed string becomes `"23321511"`.
>
> Given a positive integer `n`, return the `n`th element of the count-and-say sequence.

### Examples

```
Example 1:
Input: n = 4
Output: "1211"
Explanation:
  countAndSay(1) = "1"
  countAndSay(2) = RLE of "1" = "11"       (one 1)
  countAndSay(3) = RLE of "11" = "21"      (two 1s)
  countAndSay(4) = RLE of "21" = "1211"    (one 2, one 1)

Example 2:
Input: n = 1
Output: "1"
Explanation: This is the base case.
```

### Constraints

- `1 <= n <= 30`

---

## Problem Breakdown

### 1. What is being asked?

Generate the nth term of the count-and-say sequence. Each term is produced by "reading aloud" the digits of the previous term — counting consecutive runs of the same digit.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | The index (1-based) of the desired term in the sequence |

Important observations about the input:
- `n` is always at least 1
- The base case is `n = 1` which returns `"1"`
- Each term is built from the previous term

### 3. What is the output?

- A **string** — the nth term of the count-and-say sequence
- The string only contains digits `1`, `2`, and `3` (proven by the sequence properties)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 30` | The 30th term has ~5,808 characters. No performance concerns. |
| `n >= 1` | Always valid input, base case is trivial. |

### 5. Step-by-step example analysis

#### Building the sequence from n=1 to n=7

```text
n=1: "1"
     Read: one 1
n=2: "11"
     Read: two 1s
n=3: "21"
     Read: one 2, one 1
n=4: "1211"
     Read: one 1, one 2, two 1s
n=5: "111221"
     Read: three 1s, two 2s, one 1
n=6: "312211"
     Read: one 3, one 1, two 2s, two 1s
n=7: "13112221"
```

#### Detailed RLE process for n=5 -> n=6

```text
Input:  "111221"

Scan left to right:
  "111" → three 1s → "31"
  "22"  → two 2s   → "22"
  "1"   → one 1    → "11"

Concatenate: "31" + "22" + "11" = "312211"

Result: countAndSay(6) = "312211"
```

### 6. Key Observations

1. **Sequential dependency** — Each term depends on the previous term. We must build the sequence step by step.
2. **Run-length encoding** — The core operation is counting consecutive identical characters.
3. **String grows** — The length of each term generally grows, but n <= 30 keeps it manageable.
4. **Only digits 1, 2, 3** — The sequence never produces digits larger than 3 (mathematical property).

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Iterative simulation | Build each term from the previous one | Count and Say (this problem) |
| Recursion | Natural recursive definition | Base case n=1, recurse on n-1 |

**Chosen pattern:** `String simulation with RLE`
**Reason:** Directly simulate the process described in the problem. Either iterate from 1 to n, or recurse from n down to 1.

---

## Approach 1: Iterative

### Thought process

> Start with `"1"` and build each subsequent term by performing run-length encoding on the current term. Repeat `n-1` times to get the nth term.
>
> For RLE: scan the string left to right, count consecutive identical characters, and append "count" + "digit" to the result.

### Algorithm (step-by-step)

1. Initialize `result = "1"`
2. Repeat `n-1` times:
   a. Initialize an empty string `next`
   b. Set `i = 0`
   c. While `i < len(result)`:
      - Set `digit = result[i]`, `count = 1`
      - While `i + count < len(result)` and `result[i + count] == digit`: increment `count`
      - Append `str(count) + digit` to `next`
      - Move `i += count`
   d. Set `result = next`
3. Return `result`

### Pseudocode

```text
function countAndSay(n):
    result = "1"
    for step = 2 to n:
        next = ""
        i = 0
        while i < len(result):
            digit = result[i]
            count = 1
            while i + count < len(result) AND result[i + count] == digit:
                count++
            next += str(count) + digit
            i += count
        result = next
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * L) | n iterations, each processing a string of length L (max ~5808 for n=30) |
| **Space** | O(L) | Store the current and next strings |

### Implementation

#### Go

```go
// countAndSay — Iterative approach
// Time: O(n * L), Space: O(L)
func countAndSay(n int) string {
    result := "1"

    for step := 2; step <= n; step++ {
        var next strings.Builder
        i := 0

        for i < len(result) {
            digit := result[i]
            count := 1

            // Count consecutive identical digits
            for i+count < len(result) && result[i+count] == digit {
                count++
            }

            // Append count and digit
            next.WriteString(strconv.Itoa(count))
            next.WriteByte(digit)
            i += count
        }

        result = next.String()
    }

    return result
}
```

#### Java

```java
class Solution {
    // countAndSay — Iterative approach
    // Time: O(n * L), Space: O(L)
    public String countAndSay(int n) {
        String result = "1";

        for (int step = 2; step <= n; step++) {
            StringBuilder next = new StringBuilder();
            int i = 0;

            while (i < result.length()) {
                char digit = result.charAt(i);
                int count = 1;

                // Count consecutive identical digits
                while (i + count < result.length() && result.charAt(i + count) == digit) {
                    count++;
                }

                // Append count and digit
                next.append(count);
                next.append(digit);
                i += count;
            }

            result = next.toString();
        }

        return result;
    }
}
```

#### Python

```python
class Solution:
    def countAndSay(self, n: int) -> str:
        """
        Iterative approach
        Time: O(n * L), Space: O(L)
        """
        result = "1"

        for step in range(2, n + 1):
            next_result = []
            i = 0

            while i < len(result):
                digit = result[i]
                count = 1

                # Count consecutive identical digits
                while i + count < len(result) and result[i + count] == digit:
                    count += 1

                # Append count and digit
                next_result.append(str(count))
                next_result.append(digit)
                i += count

            result = "".join(next_result)

        return result
```

### Dry Run

```text
Input: n = 5

Step 1: result = "1" (base case)

Step 2: Process "1"
  i=0: digit='1', count=1 → "11"
  result = "11"

Step 3: Process "11"
  i=0: digit='1', count=2 → "21"
  result = "21"

Step 4: Process "21"
  i=0: digit='2', count=1 → "12"
  i=1: digit='1', count=1 → "11"
  result = "1211"

Step 5: Process "1211"
  i=0: digit='1', count=1 → "11"
  i=1: digit='2', count=1 → "12"
  i=2: digit='1', count=2 → "21"
  result = "111221"

Result: "111221"
```

---

## Approach 2: Recursive

### Thought process

> The problem has a natural recursive definition:
> - Base case: `countAndSay(1) = "1"`
> - Recursive case: `countAndSay(n) = RLE(countAndSay(n-1))`
>
> We can directly translate this definition into code. First recursively compute the (n-1)th term, then perform RLE on it.

### Algorithm (step-by-step)

1. Base case: if `n == 1`, return `"1"`
2. Recursively compute `prev = countAndSay(n - 1)`
3. Perform run-length encoding on `prev`:
   a. Scan left to right, count consecutive identical digits
   b. Append `str(count) + digit` for each run
4. Return the RLE result

### Pseudocode

```text
function countAndSay(n):
    if n == 1:
        return "1"

    prev = countAndSay(n - 1)

    result = ""
    i = 0
    while i < len(prev):
        digit = prev[i]
        count = 1
        while i + count < len(prev) AND prev[i + count] == digit:
            count++
        result += str(count) + digit
        i += count

    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n * L) | n recursive calls, each processing a string of length L |
| **Space** | O(n * L) | Recursion stack depth n, plus strings at each level |

### Implementation

#### Go

```go
// countAndSay — Recursive approach
// Time: O(n * L), Space: O(n * L)
func countAndSay(n int) string {
    // Base case
    if n == 1 {
        return "1"
    }

    // Recursively get the previous term
    prev := countAndSay(n - 1)

    // Perform RLE on the previous term
    var result strings.Builder
    i := 0

    for i < len(prev) {
        digit := prev[i]
        count := 1

        // Count consecutive identical digits
        for i+count < len(prev) && prev[i+count] == digit {
            count++
        }

        // Append count and digit
        result.WriteString(strconv.Itoa(count))
        result.WriteByte(digit)
        i += count
    }

    return result.String()
}
```

#### Java

```java
class Solution {
    // countAndSay — Recursive approach
    // Time: O(n * L), Space: O(n * L)
    public String countAndSay(int n) {
        // Base case
        if (n == 1) {
            return "1";
        }

        // Recursively get the previous term
        String prev = countAndSay(n - 1);

        // Perform RLE on the previous term
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < prev.length()) {
            char digit = prev.charAt(i);
            int count = 1;

            // Count consecutive identical digits
            while (i + count < prev.length() && prev.charAt(i + count) == digit) {
                count++;
            }

            // Append count and digit
            result.append(count);
            result.append(digit);
            i += count;
        }

        return result.toString();
    }
}
```

#### Python

```python
class Solution:
    def countAndSay(self, n: int) -> str:
        """
        Recursive approach
        Time: O(n * L), Space: O(n * L)
        """
        # Base case
        if n == 1:
            return "1"

        # Recursively get the previous term
        prev = self.countAndSay(n - 1)

        # Perform RLE on the previous term
        result = []
        i = 0

        while i < len(prev):
            digit = prev[i]
            count = 1

            # Count consecutive identical digits
            while i + count < len(prev) and prev[i + count] == digit:
                count += 1

            # Append count and digit
            result.append(str(count))
            result.append(digit)
            i += count

        return "".join(result)
```

### Dry Run

```text
Input: n = 4

countAndSay(4)
  → calls countAndSay(3)
    → calls countAndSay(2)
      → calls countAndSay(1)
      ← returns "1"
    RLE("1"):
      i=0: digit='1', count=1 → "11"
    ← returns "11"
  RLE("11"):
    i=0: digit='1', count=2 → "21"
  ← returns "21"
RLE("21"):
  i=0: digit='2', count=1 → "12"
  i=1: digit='1', count=1 → "11"
← returns "1211"

Call stack unwinding:
  countAndSay(1) = "1"
  countAndSay(2) = RLE("1")   = "11"
  countAndSay(3) = RLE("11")  = "21"
  countAndSay(4) = RLE("21")  = "1211"

Result: "1211"
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Iterative | O(n * L) | O(L) | Lower space usage, no stack overhead | Slightly more code |
| 2 | Recursive | O(n * L) | O(n * L) | Clean, mirrors problem definition | Extra stack space, risk of stack overflow for large n |

### Which solution to choose?

- **In an interview:** Approach 1 (Iterative) — straightforward, efficient, easy to explain
- **In production:** Approach 1 — better space complexity, no recursion overhead
- **On Leetcode:** Both pass easily since n <= 30
- **For learning:** Approach 2 — helps understand the recursive nature of the problem

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Base case | `n=1` | `"1"` | First term is defined as "1" |
| 2 | Second term | `n=2` | `"11"` | One 1 |
| 3 | Third term | `n=3` | `"21"` | Two 1s |
| 4 | Multiple runs | `n=5` | `"111221"` | Three 1s, two 2s, one 1 |
| 5 | Maximum input | `n=30` | 5808-char string | Must handle long strings |

---

## Common Mistakes

### Mistake 1: Confusing the "count" and "digit" order

```python
# WRONG — digit before count
result += digit + str(count)  # "12" instead of "21" for two 1s

# CORRECT — count before digit
result += str(count) + digit  # "21" for two 1s
```

**Reason:** Run-length encoding is "how many" followed by "what digit". "21" means "two 1s", not "twelve".

### Mistake 2: Not handling consecutive runs correctly

```python
# WRONG — only comparing adjacent pairs
for i in range(len(s)):
    if i + 1 < len(s) and s[i] == s[i + 1]:
        count += 1
    else:
        result += str(count) + s[i]
        count = 1

# CORRECT — use a while loop to consume entire run
i = 0
while i < len(s):
    digit = s[i]
    count = 1
    while i + count < len(s) and s[i + count] == digit:
        count += 1
    result += str(count) + digit
    i += count
```

**Reason:** The first approach can miscount runs of length 3+ if the logic for resetting count is not carefully handled.

### Mistake 3: Off-by-one in iteration count

```python
# WRONG — iterating n times instead of n-1
result = "1"
for i in range(n):  # This produces the (n+1)th term!
    result = rle(result)

# CORRECT — iterate n-1 times (we start with the 1st term)
result = "1"
for i in range(n - 1):
    result = rle(result)
```

**Reason:** We start with `"1"` as the 1st term, so we only need `n-1` transformations.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [443. String Compression](https://leetcode.com/problems/string-compression/) | :yellow_circle: Medium | Run-length encoding |
| 2 | [271. Encode and Decode Strings](https://leetcode.com/problems/encode-and-decode-strings/) | :yellow_circle: Medium | String encoding |
| 3 | [394. Decode String](https://leetcode.com/problems/decode-string/) | :yellow_circle: Medium | String decoding with counts |
| 4 | [1313. Decompress Run-Length Encoded List](https://leetcode.com/problems/decompress-run-length-encoded-list/) | :green_circle: Easy | RLE decompression |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Step-by-step** sequence building from n=1 to chosen n
> - **Run-length encoding** visualization with character grouping
> - Preset buttons for n=1 through n=7
> - Play/Pause/Step/Reset controls with speed slider
> - Detailed log of each step's RLE process
