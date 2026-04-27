# 0089. Gray Code

## Problem

| | |
|---|---|
| **Leetcode** | [89. Gray Code](https://leetcode.com/problems/gray-code/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Math`, `Backtracking`, `Bit Manipulation` |

> An **n-bit gray code sequence** is a sequence of `2^n` integers where:
>
> - Every integer is in the inclusive range `[0, 2^n - 1]`,
> - The first integer is `0`,
> - An integer appears no more than once in the sequence,
> - The binary representation of every pair of adjacent integers differs by exactly one bit, and
> - The binary representation of the first and last integers differs by exactly one bit.
>
> Given an integer `n`, return *any valid n-bit gray code sequence*.

### Examples

```
Input: n = 2
Output: [0, 1, 3, 2]

Input: n = 1
Output: [0, 1]
```

### Constraints

- `1 <= n <= 16`

---

## Approach: Direct Formula G(i) = i ^ (i >> 1)

### Idea

The standard *Reflected Binary Gray Code* of index `i` is `i XOR (i >> 1)`. Iterate `i = 0..2^n-1` and emit this value.

### Algorithm

```text
result = []
for i in 0 .. (1 << n) - 1:
    result.append(i ^ (i >> 1))
return result
```

### Complexity

- Time: O(2^n)
- Space: O(2^n) — output

### Implementation

#### Go

```go
func grayCode(n int) []int {
    size := 1 << n
    result := make([]int, size)
    for i := 0; i < size; i++ {
        result[i] = i ^ (i >> 1)
    }
    return result
}
```

#### Java

```java
class Solution {
    public List<Integer> grayCode(int n) {
        int size = 1 << n;
        List<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) result.add(i ^ (i >> 1));
        return result;
    }
}
```

#### Python

```python
class Solution:
    def grayCode(self, n: int) -> List[int]:
        size = 1 << n
        return [i ^ (i >> 1) for i in range(size)]
```

---

## Why does it work?

Adjacent integers `i` and `i+1` differ by some lowest-set-bit pattern. The XOR with `i >> 1` "carries" the higher bits unchanged, so consecutive Gray codes differ in exactly one bit — the bit that flips when we go from `i` to `i+1`.

---

## Verification

For `n = 3`: 0, 1, 3, 2, 6, 7, 5, 4 — adjacent pairs differ by exactly 1 bit, and 4 ↔ 0 differ by 1 bit (the wraparound).

---

## Visual Animation

> [animation.html](./animation.html)
