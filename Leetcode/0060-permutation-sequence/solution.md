# 0060. Permutation Sequence

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Generate All Permutations](#approach-1-generate-all-permutations)
4. [Approach 2: Step k-1 Times via Next Permutation](#approach-2-step-k-1-times-via-next-permutation)
5. [Approach 3: Factorial Number System (Optimal)](#approach-3-factorial-number-system-optimal)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [60. Permutation Sequence](https://leetcode.com/problems/permutation-sequence/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Math`, `Recursion` |

### Description

> The set `[1, 2, 3, ..., n]` contains a total of `n!` unique permutations.
>
> By listing and labeling all of the permutations in order, we get the following sequence for `n = 3`:
>
> 1. `"123"`
> 2. `"132"`
> 3. `"213"`
> 4. `"231"`
> 5. `"312"`
> 6. `"321"`
>
> Given `n` and `k`, return the `k`-th permutation sequence.

### Examples

```
Example 1:
Input: n = 3, k = 3
Output: "213"

Example 2:
Input: n = 4, k = 9
Output: "2314"

Example 3:
Input: n = 3, k = 1
Output: "123"
```

### Constraints

- `1 <= n <= 9`
- `1 <= k <= n!`

---

## Problem Breakdown

### 1. What is being asked?

If we list all permutations of `[1, 2, ..., n]` in lexicographic order and number them `1..n!`, return the permutation at position `k`.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | Length of the permutation, `1 <= n <= 9` |
| `k` | `int` | 1-based index, `1 <= k <= n!` |

Important observations:
- `n` is small (`<= 9`), so `n! <= 362880` -- still tractable
- `k` is 1-based -- be careful with the off-by-one

### 3. What is the output?

A string representing the k-th permutation, e.g. `"2314"`.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 9` | All approaches work, but optimal is O(n^2) |
| `k <= n!` | Always valid; never need bounds checking |
| `n!` up to 362880 | A 32-bit integer easily holds it |

### 5. Step-by-step example analysis

#### Example 2: `n = 4, k = 9`

```text
We want the 9th permutation (1-based) of [1, 2, 3, 4].

Total perms = 4! = 24.
First 6 (= 3!) start with 1: positions 1..6
Next  6 start with 2:                    7..12
Next  6 start with 3:                   13..18
Next  6 start with 4:                   19..24

k = 9 → starts with 2 (since 9 is in [7, 12]).
Convert k to 0-based: k = 8. Group index = 8 / 3! = 8 / 6 = 1. So pick index 1 → 2.
Remaining: [1, 3, 4], remaining position = 8 % 6 = 2.

Now find permutation #2 (0-based) of [1, 3, 4]:
Total perms of remaining = 3! = 6.
Group index = 2 / 2! = 2 / 2 = 1. Pick index 1 → 3.
Remaining: [1, 4], remaining position = 2 % 2 = 0.

Find permutation #0 of [1, 4]:
Group index = 0 / 1! = 0. Pick index 0 → 1.
Remaining: [4], remaining position = 0.

Find permutation #0 of [4]: → 4.

Result: "2" + "3" + "1" + "4" = "2314"
```

### 6. Key Observations

1. **Factorial blocks** -- The first `(n-1)!` permutations all start with the smallest remaining digit; the next `(n-1)!` with the second smallest; and so on.
2. **0-based simplifies the math** -- Convert `k` to `k - 1` once, then operate with integer division and modulo.
3. **Removing the picked digit** -- After picking digit at index `q`, the remaining digits keep their relative order. Use a list and `pop(q)`.
4. **Factorials precomputed** -- Compute `0!..n!` once.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Factorial number system | k-th permutation maps to digits `(d_{n-1}, d_{n-2}, ...)` | This problem (Approach 3) |
| Brute force enumeration | Generate all and pick k-th | Approach 1 |
| Iteratively next-permutation | Step from "1234" k-1 times | Approach 2 |

**Chosen pattern:** `Factorial Number System`
**Reason:** O(n^2) time, no recursion or full enumeration.

---

## Approach 1: Generate All Permutations

### Thought process

> Use library or manual permutation generation in lex order, take the (k-1)th.

### Algorithm

1. Build a list of digits `[1..n]`.
2. Use `itertools.permutations` (or a manual recursion) to enumerate.
3. Skip `k - 1` permutations and emit the next one as a string.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n * n!) | Generate up to k permutations, each O(n) |
| **Space** | O(n) for current permutation |

### Implementation

#### Python

```python
from itertools import permutations

class Solution:
    def getPermutationBrute(self, n: int, k: int) -> str:
        for i, p in enumerate(permutations(range(1, n + 1)), start=1):
            if i == k:
                return ''.join(map(str, p))
        return ''
```

---

## Approach 2: Step k-1 Times via Next Permutation

### Idea

> Start with the smallest permutation `"123...n"` and apply [Problem 31's](../0031-next-permutation/solution.md) `nextPermutation` `k-1` times.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(k * n) |
| **Space** | O(n) |

> For `k` close to `n!`, this is O(n^2 * n!), too slow. Listed for completeness.

---

## Approach 3: Factorial Number System (Optimal)

### Idea

> Convert `k - 1` (0-based) into a sequence of digits in the factorial base. The `i`-th digit selects which remaining element goes at the `i`-th position.

> Concretely, at position `i` (0-based, leftmost first), with `m = n - i` remaining digits sorted ascending:
>   - `q = (k - 1) / (m - 1)!` is the index into the remaining list.
>   - Pick that element, append to result, remove it from the list.
>   - `k - 1 = (k - 1) % (m - 1)!`.

### Algorithm (step-by-step)

1. Precompute `fact[0..n]` where `fact[i] = i!`.
2. `digits = [1, 2, ..., n]`.
3. Decrement `k` to make it 0-based.
4. For `i = 0..n-1`:
   - `m = n - i`, `q = k / fact[m - 1]`, `k = k % fact[m - 1]`.
   - Append `digits.pop(q)` to result.
5. Return the result as a string.

### Pseudocode

```text
fact[0] = 1
for i in 1..n: fact[i] = fact[i-1] * i

digits = [1, 2, ..., n]
k = k - 1                  # 0-based
result = ""
for i in 0..n-1:
    m = n - i
    q = k // fact[m - 1]
    result += str(digits[q])
    digits.remove_at(q)
    k = k % fact[m - 1]
return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Each `pop(q)` from a list is O(n), repeated n times |
| **Space** | O(n) | Digits list + factorial array |

### Implementation

#### Go

```go
func getPermutation(n int, k int) string {
    fact := make([]int, n+1)
    fact[0] = 1
    for i := 1; i <= n; i++ {
        fact[i] = fact[i-1] * i
    }
    digits := make([]int, n)
    for i := 0; i < n; i++ {
        digits[i] = i + 1
    }
    k-- // 0-based
    result := make([]byte, 0, n)
    for i := 0; i < n; i++ {
        m := n - i
        q := k / fact[m-1]
        result = append(result, byte('0'+digits[q]))
        digits = append(digits[:q], digits[q+1:]...)
        k %= fact[m-1]
    }
    return string(result)
}
```

#### Java

```java
class Solution {
    public String getPermutation(int n, int k) {
        int[] fact = new int[n + 1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) fact[i] = fact[i - 1] * i;
        List<Integer> digits = new ArrayList<>();
        for (int i = 1; i <= n; i++) digits.add(i);
        k--;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int m = n - i;
            int q = k / fact[m - 1];
            sb.append(digits.remove(q));
            k %= fact[m - 1];
        }
        return sb.toString();
    }
}
```

#### Python

```python
class Solution:
    def getPermutation(self, n: int, k: int) -> str:
        fact = [1] * (n + 1)
        for i in range(1, n + 1):
            fact[i] = fact[i - 1] * i
        digits = list(range(1, n + 1))
        k -= 1  # 0-based
        result = []
        for i in range(n):
            m = n - i
            q, k = divmod(k, fact[m - 1])
            result.append(str(digits.pop(q)))
        return ''.join(result)
```

### Dry Run

```text
n=4, k=9
fact = [1, 1, 2, 6, 24]
digits = [1, 2, 3, 4]
k = 8

i=0: m=4, fact[3]=6
  q = 8 / 6 = 1, k = 8 % 6 = 2
  digits.pop(1) → 2; digits = [1, 3, 4]
  result = "2"

i=1: m=3, fact[2]=2
  q = 2 / 2 = 1, k = 2 % 2 = 0
  digits.pop(1) → 3; digits = [1, 4]
  result = "23"

i=2: m=2, fact[1]=1
  q = 0 / 1 = 0, k = 0 % 1 = 0
  digits.pop(0) → 1; digits = [4]
  result = "231"

i=3: m=1, fact[0]=1
  q = 0 / 1 = 0
  digits.pop(0) → 4
  result = "2314"

Return "2314"
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Generate All | O(n * n!) | O(n) | Trivial | Slow for large k |
| 2 | Iterate next-permutation | O(k * n) | O(n) | Reuses Problem 31 | k can be near n! |
| 3 | Factorial Number System | O(n^2) | O(n) | Optimal, math-based | Requires factorial insight |

### Which solution to choose?

- **In an interview:** Approach 3 -- shows mathematical understanding
- **In production:** Approach 3
- **On Leetcode:** Approach 3

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Smallest n | `n=1, k=1` | `"1"` | Single element |
| 2 | First permutation | `n=3, k=1` | `"123"` | Identity |
| 3 | Last permutation | `n=3, k=6` | `"321"` | Reverse |
| 4 | Middle | `n=3, k=3` | `"213"` | Standard |
| 5 | Larger | `n=4, k=9` | `"2314"` | Example 2 |
| 6 | Largest n, last k | `n=9, k=362880` | `"987654321"` | Reverse of 1..9 |
| 7 | First with n=9 | `n=9, k=1` | `"123456789"` | Identity |
| 8 | Boundary block | `n=4, k=7` | `"2134"` | First permutation starting with 2 |

---

## Common Mistakes

### Mistake 1: Off-by-one (1-based vs 0-based)

```python
# WRONG — uses 1-based k directly
q = k // fact[m - 1]

# CORRECT — convert to 0-based first
k -= 1
q = k // fact[m - 1]
```

**Reason:** The factorial decomposition assumes a 0-based index of the permutation list.

### Mistake 2: Removing by value instead of by index

```python
# WRONG — list.remove() removes the first matching value, but here we want index q
digits.remove(digits[q])   # works, but conflated; if there were duplicates it would fail

# CORRECT — pop by index
digits.pop(q)
```

**Reason:** Although the inputs have no duplicates, popping by index is more direct and immune to duplicates if the problem ever changes.

### Mistake 3: Forgetting to re-anchor `k`

```python
# WRONG — keeps stale k
for i in range(n):
    q = k // fact[m - 1]
    result.append(...)
    # forgot k %= fact[m-1]!

# CORRECT
k %= fact[m - 1]
```

**Reason:** Without updating `k`, every iteration sees the same value and emits a wrong digit.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [31. Next Permutation](https://leetcode.com/problems/next-permutation/) | :yellow_circle: Medium | Step one permutation forward |
| 2 | [46. Permutations](https://leetcode.com/problems/permutations/) | :yellow_circle: Medium | Generate all |
| 3 | [47. Permutations II](https://leetcode.com/problems/permutations-ii/) | :yellow_circle: Medium | Generate all with duplicates |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Step-by-step factorial decomposition of `k - 1`
> - Current pool of remaining digits and the chosen index
> - Live formula display: `q = k // (m-1)!`, `k %= (m-1)!`
> - Selectable n (1..9) and slider for k
