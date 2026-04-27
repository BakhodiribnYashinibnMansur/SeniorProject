# 0069. Sqrt(x)

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Linear Search (Brute Force)](#approach-1-linear-search-brute-force)
4. [Approach 2: Binary Search](#approach-2-binary-search)
5. [Approach 3: Newton's Method](#approach-3-newtons-method)
6. [Approach 4: Bit Manipulation (Digit-by-Digit)](#approach-4-bit-manipulation-digit-by-digit)
7. [Complexity Comparison](#complexity-comparison)
8. [Edge Cases](#edge-cases)
9. [Common Mistakes](#common-mistakes)
10. [Related Problems](#related-problems)
11. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [69. Sqrt(x)](https://leetcode.com/problems/sqrtx/) |
| **Difficulty** | :green_circle: Easy |
| **Tags** | `Math`, `Binary Search` |

### Description

> Given a non-negative integer `x`, return *the square root of `x` rounded down to the nearest integer*. The returned integer should be **non-negative** as well.
>
> You **must not use** any built-in exponent function or operator.
>
>     - For example, do not use `pow(x, 0.5)` in c++ or `x ** 0.5` in python.

### Examples

```
Example 1:
Input: x = 4
Output: 2

Example 2:
Input: x = 8
Output: 2
Explanation: floor(sqrt(8)) = floor(2.828...) = 2.
```

### Constraints

- `0 <= x <= 2^31 - 1`

---

## Problem Breakdown

### 1. What is being asked?

Find `floor(sqrt(x))` -- the largest integer `r` such that `r * r <= x`.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `x` | `int` | `0 <= x <= 2^31 - 1` |

### 3. What is the output?

A single non-negative integer: the floor of the real square root.

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `x` up to ~2.1 * 10^9 | `r` up to ~46341. Linear search is OK but binary search / Newton's are tidier |
| Watch for overflow: `r * r` for `r` near `46341` is fine in 32-bit; `mid * mid` in binary search is at most `~2.1 * 10^9` -- fine for `int32`, but be careful in tight integer types |

### 5. Step-by-step example analysis

#### `x = 8`

```text
Binary search on [0, 8]:
  lo = 0, hi = 8
  mid = 4: 4*4 = 16 > 8 → hi = 3
  lo = 0, hi = 3, mid = 1: 1 ≤ 8 → ans = 1, lo = 2
  lo = 2, hi = 3, mid = 2: 4 ≤ 8 → ans = 2, lo = 3
  lo = 3, hi = 3, mid = 3: 9 > 8 → hi = 2
  loop ends.
Return ans = 2.
```

### 6. Key Observations

1. **Monotonic search space** -- `r * r` is monotonically non-decreasing in `r`. Binary search is the natural fit.
2. **Newton's method converges quadratically** -- Iterate `r = (r + x / r) / 2` until convergence. Very few iterations.
3. **Bit manipulation per-digit method** -- Compute the integer square root one bit at a time. Very efficient and overflow-safe.
4. **Overflow** -- Avoid `mid * mid` for very large `x`; use `x / mid` or `long`.

### 7. Pattern identification

| Pattern | Why it fits |
|---|---|
| Binary Search | Monotonic predicate `mid*mid <= x` |
| Newton's Method | Numerical analysis, quadratic convergence |
| Bit Manipulation | Compute root from MSB to LSB |

**Chosen pattern:** `Binary Search` for clarity, `Newton's` for speed.

---

## Approach 1: Linear Search (Brute Force)

### Idea

> Increment `r` from `0` until `(r+1)^2 > x`. The current `r` is the answer.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(sqrt(x)) |
| **Space** | O(1) |

> O(46341) at worst — fast in absolute terms, but O(log x) is far better.

### Implementation

#### Python

```python
class Solution:
    def mySqrtLinear(self, x: int) -> int:
        r = 0
        while (r + 1) * (r + 1) <= x:
            r += 1
        return r
```

---

## Approach 2: Binary Search

### Algorithm (step-by-step)

1. If `x < 2`, return `x`.
2. `lo = 1`, `hi = x // 2`, `ans = 0`.
3. While `lo <= hi`:
   - `mid = (lo + hi) // 2`.
   - If `mid * mid <= x`: `ans = mid`, `lo = mid + 1`.
   - Else: `hi = mid - 1`.
4. Return `ans`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(log x) |
| **Space** | O(1) |

### Implementation

#### Go

```go
func mySqrt(x int) int {
    if x < 2 {
        return x
    }
    lo, hi := 1, x/2
    ans := 0
    for lo <= hi {
        mid := (lo + hi) / 2
        if mid <= x/mid {
            ans = mid
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return ans
}
```

> Comparing `mid <= x/mid` instead of `mid*mid <= x` avoids overflow in 32-bit.

#### Java

```java
class Solution {
    public int mySqrt(int x) {
        if (x < 2) return x;
        int lo = 1, hi = x / 2, ans = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            // Use long to avoid overflow when mid * mid is computed
            if ((long) mid * mid <= x) {
                ans = mid;
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return ans;
    }
}
```

#### Python

```python
class Solution:
    def mySqrt(self, x: int) -> int:
        if x < 2: return x
        lo, hi, ans = 1, x // 2, 0
        while lo <= hi:
            mid = (lo + hi) // 2
            if mid * mid <= x:
                ans = mid
                lo = mid + 1
            else:
                hi = mid - 1
        return ans
```

### Dry Run

```text
x = 8
lo=1, hi=4, ans=0
mid=2: 4 ≤ 8 → ans=2, lo=3
mid=3: 9 > 8 → hi=2
lo>hi → return 2
```

---

## Approach 3: Newton's Method

### Idea

> The iteration `r := (r + x/r) / 2` converges quadratically to `sqrt(x)`.
>
> Start with `r = x` (or `x // 2` for speed). Iterate until `r * r <= x`. Then `r` is the floor of the true square root.

### Algorithm (step-by-step)

1. If `x < 2`, return `x`.
2. `r = x`.
3. While `r * r > x`: `r = (r + x // r) // 2`.
4. Return `r`.

### Complexity

| | Complexity |
|---|---|
| **Time** | O(log log x) (quadratic convergence) |
| **Space** | O(1) |

### Implementation

#### Python

```python
class Solution:
    def mySqrtNewton(self, x: int) -> int:
        if x < 2: return x
        r = x
        while r * r > x:
            r = (r + x // r) // 2
        return r
```

#### Go

```go
func mySqrtNewton(x int) int {
    if x < 2 {
        return x
    }
    r := x
    for r*r > x {
        r = (r + x/r) / 2
    }
    return r
}
```

#### Java

```java
class Solution {
    public int mySqrtNewton(int x) {
        if (x < 2) return x;
        long r = x;
        while (r * r > x) {
            r = (r + x / r) / 2;
        }
        return (int) r;
    }
}
```

### Dry Run

```text
x = 16
r = 16, 16*16 = 256 > 16 → r = (16 + 1) / 2 = 8
r = 8, 64 > 16 → r = (8 + 2) / 2 = 5
r = 5, 25 > 16 → r = (5 + 3) / 2 = 4
r = 4, 16 ≤ 16 → return 4
```

---

## Approach 4: Bit Manipulation (Digit-by-Digit)

### Idea

> Compute the square root by determining each bit from the highest to the lowest. For each candidate bit set in `r`, check whether the new `r * r <= x`. Equivalent to integer Newton with subtraction-only arithmetic.

> Useful when multiplication is expensive or you want a hardware-style algorithm.

### Implementation

#### Python

```python
class Solution:
    def mySqrtBits(self, x: int) -> int:
        if x < 2: return x
        result = 0
        bit = 1 << 16   # max useful bit for x <= 2^31 - 1 (since sqrt < 2^16)
        while bit > 0:
            cand = result | bit
            if cand * cand <= x:
                result = cand
            bit >>= 1
        return result
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Linear | O(sqrt(x)) | O(1) | Trivial | Slowest |
| 2 | Binary Search | O(log x) | O(1) | Clear, robust | Need to avoid overflow |
| 3 | Newton's Method | O(log log x) | O(1) | Fastest in practice | Slightly trickier convergence |
| 4 | Bit Manipulation | O(log x) | O(1) | No division | Less common |

### Which solution to choose?

- **In an interview:** Approach 2 (binary search) — clearest
- **In production:** Approach 2 or 3
- **On Leetcode:** Either passes

---

## Edge Cases

| # | Case | Input | Expected | Reason |
|---|---|---|---|---|
| 1 | Zero | `0` | `0` | Trivial |
| 2 | One | `1` | `1` | Trivial |
| 3 | Two | `2` | `1` | floor(sqrt(2)) = 1 |
| 4 | Three | `3` | `1` | floor(sqrt(3)) = 1 |
| 5 | Perfect square | `16` | `4` | Exact |
| 6 | Just above square | `17` | `4` | Round down |
| 7 | Just below square | `15` | `3` | Round down |
| 8 | INT_MAX | `2147483647` | `46340` | Overflow check |
| 9 | Just below INT_MAX | `2147395599` | `46339` | Boundary |

---

## Common Mistakes

### Mistake 1: Integer overflow with `mid * mid`

```java
// WRONG — int * int overflows
int mid = (lo + hi) / 2;
if (mid * mid <= x) ...

// CORRECT — cast or use division
if ((long) mid * mid <= x) ...
// or
if (mid <= x / mid) ...
```

**Reason:** For `x` near 2^31, `mid` can be ~46340 and `mid * mid` ~2.1 * 10^9, which fits but is risky.

### Mistake 2: Initial Newton seed of 0

```python
# WRONG — division by zero on the very first iteration
r = 0
while r * r > x:
    r = (r + x // r) // 2

# CORRECT
r = x
```

**Reason:** Newton needs a positive seed.

### Mistake 3: Forgetting `x < 2` shortcut

```python
# WRONG — for x == 0, x // 2 == 0, and binary search loop never executes (lo=1, hi=0), returns ans=0 — actually fine
# But for the Newton method, r = 0 trips division
```

**Reason:** Always handle `x = 0` and `x = 1` first.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [50. Pow(x, n)](https://leetcode.com/problems/powx-n/) | :yellow_circle: Medium | Reverse: power instead of root |
| 2 | [367. Valid Perfect Square](https://leetcode.com/problems/valid-perfect-square/) | :green_circle: Easy | Same algorithm, equality check |
| 3 | [704. Binary Search](https://leetcode.com/problems/binary-search/) | :green_circle: Easy | Pure binary search practice |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> The animation includes:
> - Number line with current `lo`, `hi`, `mid` markers
> - Live `mid * mid` evaluation
> - Newton-method tab showing the iteration `r := (r + x/r) / 2`
> - Adjustable input `x` slider
