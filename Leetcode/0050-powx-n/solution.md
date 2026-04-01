# 0050. Pow(x, n)

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Fast Power (Recursive Binary Exponentiation)](#approach-1-fast-power-recursive-binary-exponentiation)
4. [Approach 2: Iterative Binary Exponentiation](#approach-2-iterative-binary-exponentiation)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [50. Pow(x, n)](https://leetcode.com/problems/powx-n/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Math`, `Recursion` |

### Description

> Implement `pow(x, n)`, which calculates `x` raised to the power `n` (i.e., `x^n`).

### Examples

```
Example 1:
Input: x = 2.00000, n = 10
Output: 1024.00000

Example 2:
Input: x = 2.10000, n = 3
Output: 9.26100

Example 3:
Input: x = 2.00000, n = -2
Output: 0.25000
Explanation: 2^(-2) = 1/(2^2) = 1/4 = 0.25
```

### Constraints

- `-100.0 < x < 100.0`
- `-2^31 <= n <= 2^31 - 1`
- `n` is an integer
- Either `x` is not zero or `n > 0`
- `-10^4 <= x^n <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Compute `x^n` efficiently. We cannot simply multiply `x` by itself `n` times because `n` can be up to 2^31, which would be ~2 billion multiplications and cause TLE.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `x` | `float` | The base number |
| `n` | `int` | The exponent (can be negative, zero, or positive) |

Important observations about the input:
- `n` can be **negative** -- meaning we need `1 / x^|n|`
- `n` can be **zero** -- any non-zero `x^0 = 1`
- `n` can be as large as `2^31 - 1` or as small as `-2^31`
- `x` can be **negative**, **zero**, or a **decimal**

### 3. What is the output?

- A single **float** -- the value of `x^n`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n` up to 2^31 | O(n) brute force = 2 billion ops -- TLE. Need O(log n) |
| `n = -2^31` | `-n` overflows int32! Must handle carefully |
| `x` can be 0 | `0^positive = 0`, `0^0` is not tested per constraints |
| Result fits in 10^4 | No overflow concern for the result itself |

### 5. Step-by-step example analysis

#### Example 1: `x = 2.0, n = 10`

```text
Naive: 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 = 1024  (10 multiplications)

Binary Exponentiation:
  n = 10 = 1010 in binary
  
  2^10 = 2^8 * 2^2
  
  Step by step:
    2^1  = 2
    2^2  = (2^1)^2 = 4
    2^4  = (2^2)^2 = 16
    2^8  = (2^4)^2 = 256
    2^10 = 2^8 * 2^2 = 256 * 4 = 1024  (only 4 multiplications!)

Result: 1024.0
```

#### Example 3: `x = 2.0, n = -2`

```text
n is negative, so:
  2^(-2) = 1 / 2^2 = 1 / 4 = 0.25

Convert: x = 1/x = 0.5, n = |n| = 2
  0.5^2 = 0.25

Result: 0.25
```

### 6. Key Observations

1. **Binary representation of n** -- Any exponent can be decomposed into powers of 2. For example, `x^13 = x^8 * x^4 * x^1` because `13 = 1101` in binary.
2. **Repeated squaring** -- We can compute `x^1, x^2, x^4, x^8, ...` by squaring the previous result. Each step doubles the exponent.
3. **Negative exponent** -- `x^(-n) = (1/x)^n`. Convert to positive case.
4. **Overflow trap** -- When `n = -2^31`, computing `-n` overflows a 32-bit signed integer. Use `long` or handle specially.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Binary Exponentiation | Reduces O(n) to O(log n) by squaring | Pow(x, n) (this problem) |
| Divide and Conquer | Split `x^n` into `x^(n/2) * x^(n/2)` | This problem, Matrix Power |

**Chosen pattern:** `Binary Exponentiation (Fast Power)`
**Reason:** Reduces the number of multiplications from O(n) to O(log n) by exploiting the binary representation of the exponent.

---

## Approach 1: Fast Power (Recursive Binary Exponentiation)

### Thought process

> The key insight: `x^n` can be broken down recursively.
> - If `n` is even: `x^n = (x^(n/2))^2`
> - If `n` is odd: `x^n = x * (x^(n/2))^2`
>
> Each recursive call halves `n`, so we reach the base case in O(log n) steps.
> For negative `n`, convert: `x^(-n) = (1/x)^n`.

### Algorithm (step-by-step)

1. **Base case:** If `n == 0`, return `1.0`
2. **Handle negative n:** If `n < 0`, set `x = 1/x` and `n = -n` (use long to avoid overflow)
3. **Recursive step:**
   a. Compute `half = fastPow(x, n / 2)`
   b. If `n` is even: return `half * half`
   c. If `n` is odd: return `half * half * x`

### Pseudocode

```text
function myPow(x, n):
    if n < 0:
        x = 1 / x
        n = -n          // use long to avoid overflow
    return fastPow(x, n)

function fastPow(x, n):
    if n == 0:
        return 1.0
    half = fastPow(x, n / 2)
    if n is even:
        return half * half
    else:
        return half * half * x
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log n) | Each recursive call halves n. Total log2(n) calls. |
| **Space** | O(log n) | Recursion stack depth is log2(n). |

### Implementation

#### Go

```go
func myPow(x float64, n int) float64 {
    N := int64(n)
    if N < 0 {
        x = 1 / x
        N = -N
    }
    return fastPow(x, N)
}

func fastPow(x float64, n int64) float64 {
    if n == 0 {
        return 1.0
    }
    half := fastPow(x, n/2)
    if n%2 == 0 {
        return half * half
    }
    return half * half * x
}
```

#### Java

```java
class Solution {
    public double myPow(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }
        return fastPow(x, N);
    }

    private double fastPow(double x, long n) {
        if (n == 0) {
            return 1.0;
        }
        double half = fastPow(x, n / 2);
        if (n % 2 == 0) {
            return half * half;
        }
        return half * half * x;
    }
}
```

#### Python

```python
class Solution:
    def myPow(self, x: float, n: int) -> float:
        if n < 0:
            x = 1 / x
            n = -n
        return self.fastPow(x, n)

    def fastPow(self, x: float, n: int) -> float:
        if n == 0:
            return 1.0
        half = self.fastPow(x, n // 2)
        if n % 2 == 0:
            return half * half
        return half * half * x
```

### Dry Run

```text
Input: x = 2.0, n = 10

myPow(2.0, 10):
  N = 10 (positive, no conversion)
  fastPow(2.0, 10)

fastPow(2.0, 10):
  half = fastPow(2.0, 5)
    fastPow(2.0, 5):
      half = fastPow(2.0, 2)
        fastPow(2.0, 2):
          half = fastPow(2.0, 1)
            fastPow(2.0, 1):
              half = fastPow(2.0, 0)
                fastPow(2.0, 0): return 1.0
              half = 1.0
              n=1 is odd: return 1.0 * 1.0 * 2.0 = 2.0
          half = 2.0
          n=2 is even: return 2.0 * 2.0 = 4.0
      half = 4.0
      n=5 is odd: return 4.0 * 4.0 * 2.0 = 32.0
  half = 32.0
  n=10 is even: return 32.0 * 32.0 = 1024.0

Total multiplications: 4 (vs 10 for brute force)
Result: 1024.0
```

---

## Approach 2: Iterative Binary Exponentiation

### The problem with Approach 1

> The recursive approach uses O(log n) stack space.
> We can eliminate the recursion stack by processing the bits of `n` iteratively.

### Optimization idea

> Think of `n` in binary. For each bit of `n` from the least significant to the most significant:
> - If the current bit is 1, multiply the result by the current `x`
> - Square `x` for the next bit position
> - Right-shift `n` by 1
>
> This processes all bits of `n` in O(log n) time with O(1) space.

### Algorithm (step-by-step)

1. Handle negative `n`: set `x = 1/x`, `n = -n` (use long)
2. Initialize `result = 1.0`
3. While `n > 0`:
   a. If `n` is odd (last bit is 1): `result *= x`
   b. Square `x`: `x *= x`
   c. Halve `n`: `n >>= 1`
4. Return `result`

### Pseudocode

```text
function myPow(x, n):
    if n < 0:
        x = 1 / x
        n = -n
    
    result = 1.0
    while n > 0:
        if n is odd:
            result = result * x
        x = x * x
        n = n >> 1
    
    return result
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(log n) | Each iteration processes one bit of n. |
| **Space** | O(1) | No recursion, only a few variables. |

### Implementation

#### Go

```go
func myPow(x float64, n int) float64 {
    N := int64(n)
    if N < 0 {
        x = 1 / x
        N = -N
    }

    result := 1.0
    for N > 0 {
        if N%2 == 1 {
            result *= x
        }
        x *= x
        N >>= 1
    }

    return result
}
```

#### Java

```java
class Solution {
    public double myPow(double x, int n) {
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        double result = 1.0;
        while (N > 0) {
            if (N % 2 == 1) {
                result *= x;
            }
            x *= x;
            N >>= 1;
        }

        return result;
    }
}
```

#### Python

```python
class Solution:
    def myPow(self, x: float, n: int) -> float:
        if n < 0:
            x = 1 / x
            n = -n

        result = 1.0
        while n > 0:
            if n % 2 == 1:
                result *= x
            x *= x
            n >>= 1

        return result
```

### Dry Run

```text
Input: x = 2.0, n = 10

n = 10, binary = 1010
x = 2.0, result = 1.0

Iteration 1: n = 10 (1010)
  n is even → skip multiply
  x = 2.0 * 2.0 = 4.0
  n = 10 >> 1 = 5

Iteration 2: n = 5 (101)
  n is odd → result = 1.0 * 4.0 = 4.0
  x = 4.0 * 4.0 = 16.0
  n = 5 >> 1 = 2

Iteration 3: n = 2 (10)
  n is even → skip multiply
  x = 16.0 * 16.0 = 256.0
  n = 2 >> 1 = 1

Iteration 4: n = 1 (1)
  n is odd → result = 4.0 * 256.0 = 1024.0
  x = 256.0 * 256.0 = 65536.0
  n = 1 >> 1 = 0

n == 0 → STOP

Total multiplications: 4
Result: 1024.0

Verification: 2^10 = 2^8 * 2^2 = 256 * 4 = 1024 ✓
Binary decomposition: 1010 → bits 1 and 3 are set → x^2 * x^8
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Recursive Binary Exponentiation | O(log n) | O(log n) | Intuitive, easy to understand | Uses recursion stack |
| 2 | Iterative Binary Exponentiation | O(log n) | O(1) | Optimal time and space | Slightly less intuitive |

### Which solution to choose?

- **In an interview:** Either approach -- both demonstrate understanding of binary exponentiation
- **In production:** Approach 2 (Iterative) -- no risk of stack overflow for very large `n`
- **On Leetcode:** Both pass -- same time complexity
- **For learning:** Approach 1 first to understand the recursion, then Approach 2 for the iterative optimization

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Zero exponent | `x=2.0, n=0` | `1.0` | Any non-zero number raised to 0 is 1 |
| 2 | Exponent is 1 | `x=5.0, n=1` | `5.0` | Any number to the power 1 is itself |
| 3 | Negative exponent | `x=2.0, n=-2` | `0.25` | `2^(-2) = 1/4 = 0.25` |
| 4 | x = 1.0 | `x=1.0, n=2147483647` | `1.0` | 1 raised to any power is 1 |
| 5 | x = -1.0, even n | `x=-1.0, n=2` | `1.0` | `(-1)^2 = 1` |
| 6 | x = -1.0, odd n | `x=-1.0, n=3` | `-1.0` | `(-1)^3 = -1` |
| 7 | n = -2^31 (INT_MIN) | `x=2.0, n=-2147483648` | `0.0` | Overflow trap: `-n` overflows int32 |
| 8 | x = 0.0 | `x=0.0, n=5` | `0.0` | Zero raised to positive power is 0 |

---

## Common Mistakes

### Mistake 1: Overflow when negating n = -2^31

```python
# WRONG — overflows in languages with 32-bit int
n = -n  # If n = -2147483648, -n = 2147483648 > INT_MAX

# CORRECT — use long/int64
N = int64(n)  # or long N = n; in Java
N = -N
```

**Reason:** In Java/Go/C++, `-(-2^31)` overflows a 32-bit integer. Always cast to `long`/`int64` first.

### Mistake 2: O(n) brute force multiplication

```python
# WRONG — TLE for n = 2^31
result = 1
for i in range(n):
    result *= x

# CORRECT — O(log n) binary exponentiation
while n > 0:
    if n % 2 == 1:
        result *= x
    x *= x
    n >>= 1
```

**Reason:** With `n` up to 2 billion, linear multiplication is far too slow.

### Mistake 3: Not handling negative exponents

```python
# WRONG — ignores negative n
def myPow(x, n):
    result = 1
    while n > 0:  # Never enters loop if n < 0
        ...

# CORRECT — convert negative n
if n < 0:
    x = 1 / x
    n = -n
```

**Reason:** `x^(-n) = (1/x)^n`. Must convert both `x` and `n`.

### Mistake 4: Computing half twice in recursion

```python
# WRONG — computes fastPow twice, making it O(n)
def fastPow(x, n):
    if n == 0:
        return 1.0
    if n % 2 == 0:
        return fastPow(x, n // 2) * fastPow(x, n // 2)  # Two calls!
    return x * fastPow(x, n // 2) * fastPow(x, n // 2)   # Two calls!

# CORRECT — compute half once, reuse it
def fastPow(x, n):
    if n == 0:
        return 1.0
    half = fastPow(x, n // 2)
    if n % 2 == 0:
        return half * half
    return half * half * x
```

**Reason:** Two recursive calls create a binary tree of calls, resulting in O(n) time instead of O(log n).

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [372. Super Pow](https://leetcode.com/problems/super-pow/) | :yellow_circle: Medium | Power with modular arithmetic |
| 2 | [69. Sqrt(x)](https://leetcode.com/problems/sqrtx/) | :green_circle: Easy | Math, binary search |
| 3 | [29. Divide Two Integers](https://leetcode.com/problems/divide-two-integers/) | :yellow_circle: Medium | Bit manipulation, repeated doubling |
| 4 | [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | :yellow_circle: Medium | Math operations on linked lists |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - Step-by-step visualization of binary exponentiation
> - Shows squaring and multiplying operations at each step
> - Binary representation of the exponent with bit highlighting
> - Preset examples including negative exponents and edge cases
> - Step/Play/Pause/Reset controls with speed slider
