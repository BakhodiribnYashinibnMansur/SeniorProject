# 0070. Climbing Stairs

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Recursion (Brute Force)](#approach-1-recursion-brute-force)
4. [Approach 2: Memoization](#approach-2-memoization)
5. [Approach 3: Bottom-Up DP](#approach-3-bottom-up-dp)
6. [Approach 4: Bottom-Up DP with O(1) Space](#approach-4-bottom-up-dp-with-o1-space)
7. [Approach 5: Matrix Exponentiation](#approach-5-matrix-exponentiation)
8. [Complexity Comparison](#complexity-comparison)
9. [Edge Cases](#edge-cases)
10. [Common Mistakes](#common-mistakes)
11. [Related Problems](#related-problems)
12. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Math`, `Dynamic Programming`, `Memoization` |

### Description

> You are climbing a staircase. It takes `n` steps to reach the top.
>
> Each time you can either climb `1` or `2` steps. In how many distinct ways can you climb to the top?

### Examples

```
Example 1:
Input: n = 2
Output: 2
Explanation: 1+1 or 2.

Example 2:
Input: n = 3
Output: 3
Explanation: 1+1+1, 1+2, 2+1.
```

### Constraints

- `1 <= n <= 45`

---

## Problem Breakdown

### 1. What is being asked?

Count the number of distinct sequences of `1`s and `2`s that sum to `n`.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `n` | `int` | Number of steps (`1 <= n <= 45`) |

### 3. What is the output?

A single integer: the number of distinct climbing sequences.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 45` | Answer fits in 32-bit signed int (`F(46) = 1836311903`) |
| Small `n` | Even simple approaches work |

### 5. Step-by-step example analysis

#### `n = 4`

```text
Ways:
  1+1+1+1
  1+1+2
  1+2+1
  2+1+1
  2+2

Total: 5

Notice: ways(4) = ways(3) + ways(2) = 3 + 2 = 5. Fibonacci!
```

### 6. Key Observations

1. **Fibonacci recurrence** -- `ways(n) = ways(n-1) + ways(n-2)`. The last move is either a 1-step (from `n-1`) or a 2-step (from `n-2`).
2. **Base cases** -- `ways(1) = 1`, `ways(2) = 2`.
3. **O(1) space** -- We only need the previous two values.
4. **Matrix exponentiation** -- For very large `n`, raise the Fibonacci matrix to a power in `O(log n)` time.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Fibonacci sequence | Same recurrence |
| Dynamic programming | Optimal substructure |
| Matrix exponentiation | Logarithmic Fibonacci |

**Chosen pattern:** `Bottom-Up DP with O(1) space`.

---

## Approach 1: Recursion (Brute Force)

### Idea

> Direct translation of the recurrence: `ways(n) = ways(n-1) + ways(n-2)`. Without memoization, exponential.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(2^n) |
| **Space** | O(n) |

> TLE for `n > ~30`.

### Implementation

#### Python

```python
class Solution:
    def climbStairsRec(self, n: int) -> int:
        if n <= 2: return n
        return self.climbStairsRec(n - 1) + self.climbStairsRec(n - 2)
```

---

## Approach 2: Memoization

### Idea

> Cache `ways(n)` so each subproblem is computed once.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(n) |

### Implementation

#### Python

```python
class Solution:
    def climbStairsMemo(self, n: int) -> int:
        from functools import lru_cache
        @lru_cache(maxsize=None)
        def f(k: int) -> int:
            if k <= 2: return k
            return f(k - 1) + f(k - 2)
        return f(n)
```

---

## Approach 3: Bottom-Up DP

### Algorithm

1. `dp[1] = 1`, `dp[2] = 2`.
2. For `i = 3..n`: `dp[i] = dp[i-1] + dp[i-2]`.
3. Return `dp[n]`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(n) |

### Implementation

#### Python

```python
class Solution:
    def climbStairsDP(self, n: int) -> int:
        if n <= 2: return n
        dp = [0] * (n + 1)
        dp[1], dp[2] = 1, 2
        for i in range(3, n + 1):
            dp[i] = dp[i - 1] + dp[i - 2]
        return dp[n]
```

---

## Approach 4: Bottom-Up DP with O(1) Space

### Algorithm

1. Track only the previous two values.
2. `prev2 = 1`, `prev1 = 2`.
3. For `i = 3..n`: `cur = prev1 + prev2`; shift.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(n) |
| **Space** | O(1) |

### Implementation

#### Go

```go
func climbStairs(n int) int {
    if n <= 2 {
        return n
    }
    prev2, prev1 := 1, 2
    for i := 3; i <= n; i++ {
        cur := prev1 + prev2
        prev2 = prev1
        prev1 = cur
    }
    return prev1
}
```

#### Java

```java
class Solution {
    public int climbStairs(int n) {
        if (n <= 2) return n;
        int prev2 = 1, prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int cur = prev1 + prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }
}
```

#### Python

```python
class Solution:
    def climbStairs(self, n: int) -> int:
        if n <= 2: return n
        prev2, prev1 = 1, 2
        for _ in range(3, n + 1):
            prev2, prev1 = prev1, prev1 + prev2
        return prev1
```

### Dry Run

```text
n = 5
prev2 = 1, prev1 = 2

i = 3: cur = 3, prev2 = 2, prev1 = 3
i = 4: cur = 5, prev2 = 3, prev1 = 5
i = 5: cur = 8, prev2 = 5, prev1 = 8

Return 8.
```

---

## Approach 5: Matrix Exponentiation

### Idea

> The Fibonacci recurrence can be expressed as:
>
> | F(n+1) |   | 1 1 | ^n   | F(1) |
> | F(n)   | = | 1 0 |    * | F(0) |
>
> Compute the matrix power in `O(log n)` via repeated squaring. For our problem, `ways(n) = F(n + 1)` of the standard Fibonacci.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(log n) |
| **Space** | O(1) |

### Implementation

#### Python

```python
class Solution:
    def climbStairsMatrix(self, n: int) -> int:
        def mat_mul(a, b):
            return [
                [a[0][0]*b[0][0] + a[0][1]*b[1][0], a[0][0]*b[0][1] + a[0][1]*b[1][1]],
                [a[1][0]*b[0][0] + a[1][1]*b[1][0], a[1][0]*b[0][1] + a[1][1]*b[1][1]],
            ]
        def mat_pow(m, p):
            result = [[1, 0], [0, 1]]
            while p:
                if p & 1: result = mat_mul(result, m)
                m = mat_mul(m, m)
                p >>= 1
            return result
        # ways(n) = F(n+1) where F(1) = 1, F(2) = 1; here ways(1) = 1, ways(2) = 2
        # So we need [[1,1],[1,0]]^n applied to [ways(1), ways(0)] = [1, 1]
        m = mat_pow([[1, 1], [1, 0]], n)
        return m[0][0]   # ways(n) = F(n+1) which appears at m[0][0] when M^n applied to [1, 0]
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Recursion | O(2^n) | O(n) | Simplest | TLE |
| 2 | Memoization | O(n) | O(n) | Easy DP | Recursion stack |
| 3 | Bottom-Up DP | O(n) | O(n) | Iterative | Array overhead |
| 4 | DP O(1) space | O(n) | O(1) | Optimal | -- |
| 5 | Matrix exponentiation | O(log n) | O(1) | Best for very large n | More code |

### Which solution to choose?

- **In an interview:** Approach 4 -- the standard answer
- **In production:** Approach 4 (or 5 for very large n)
- **On Leetcode:** Approach 4

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | `n = 1` | `1` | `1` | Only 1 step |
| 2 | `n = 2` | `2` | `2` | Two ways |
| 3 | `n = 3` | `3` | `3` | Three ways |
| 4 | `n = 4` | `4` | `5` | Standard |
| 5 | `n = 5` | `5` | `8` | Fibonacci continues |
| 6 | `n = 45` | `45` | `1836311903` | Largest input |

---

## Common Mistakes

### Mistake 1: Plain recursion

```python
# WRONG — exponential
def ways(n):
    if n <= 2: return n
    return ways(n-1) + ways(n-2)
```

**Reason:** Without caching, the same subproblem is solved many times.

### Mistake 2: Off-by-one in base case

```python
# WRONG — uses dp[0] = 1, dp[1] = 1, returns dp[n], gives one less
dp[0] = 1; dp[1] = 1
for i in range(2, n+1): dp[i] = dp[i-1] + dp[i-2]
return dp[n]   # ways(2) = 2 but this returns 2 ✓
                # ways(3) = 3 but returns 3 ✓
# This is consistent with F(n+1) so works; verify with examples.

# CORRECT (with our convention)
dp[1] = 1; dp[2] = 2
for i in range(3, n+1): dp[i] = dp[i-1] + dp[i-2]
return dp[n]
```

**Reason:** Multiple equivalent indexings exist. Pick one and stay consistent.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [509. Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | :green_circle: Easy | Same recurrence |
| 2 | [746. Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | :green_circle: Easy | Adds cost, same DP |
| 3 | [1137. N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | :green_circle: Easy | Three-term version |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Stair-step visualization with current step highlighted
> - Live `dp[i]` values shown next to each stair
> - Toggle between DP and matrix-exponentiation views
