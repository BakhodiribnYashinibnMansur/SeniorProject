# 0042. Trapping Rain Water

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Two Pointers (Optimal)](#approach-1-two-pointers-optimal)
4. [Approach 2: Dynamic Programming](#approach-2-dynamic-programming)
5. [Approach 3: Monotonic Stack](#approach-3-monotonic-stack)
6. [Complexity Comparison](#complexity-comparison)
7. [Edge Cases](#edge-cases)
8. [Common Mistakes](#common-mistakes)
9. [Related Problems](#related-problems)
10. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [42. Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) |
| **Difficulty** | :red_circle: Hard |
| **Tags** | `Array`, `Two Pointers`, `Dynamic Programming`, `Stack`, `Monotonic Stack` |

### Description

> Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.

### Examples

```
Example 1:
Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
Output: 6
Explanation: The elevation map [0,1,0,2,1,0,1,3,2,1,2,1] can trap 6 units of rain water.

Example 2:
Input: height = [4,2,0,3,2,5]
Output: 9
```

### Constraints

- `n == height.length`
- `1 <= n <= 2 * 10^4`
- `0 <= height[i] <= 10^5`

---

## Problem Breakdown

### 1. What is being asked?

Given an elevation map represented by an array of non-negative integers, compute the total amount of water that can be trapped between the bars after raining. Each bar has a width of 1.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `height` | `int[]` | Array of non-negative integers representing bar heights |

Important observations about the input:
- Heights can be **zero** (flat ground)
- The array can have a single element (no water can be trapped)
- The first and last bars can never hold water (nothing to their left/right)

### 3. What is the output?

- A single **integer** — the total units of water trapped
- Water at position `i` = `min(leftMax[i], rightMax[i]) - height[i]` (if positive)

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 2 * 10^4` | O(n^2) = 4 * 10^8 — borderline TLE. O(n) or O(n log n) preferred |
| `height[i] <= 10^5` | Max water at a single position is bounded by 10^5 |
| `height[i] >= 0` | No negative heights |
| `n >= 1` | Single bar means no water |

### 5. Step-by-step example analysis

#### Example 1: `height = [0,1,0,2,1,0,1,3,2,1,2,1]`

```text
Visual representation (height shown vertically):

Index:  0  1  2  3  4  5  6  7  8  9  10 11
                          #
              #           #  #     #
           #  #  #     #  #  #  #  #  #
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Height: 0  1  0  2  1  0  1  3  2  1  2  1

Water at each position:
  i=0: min(0, 3) - 0 = 0  (leftMax=0, no water at edges)
  i=1: min(1, 3) - 1 = 0
  i=2: min(1, 3) - 0 = 1  ← 1 unit
  i=3: min(2, 3) - 2 = 0
  i=4: min(2, 3) - 1 = 1  ← 1 unit
  i=5: min(2, 3) - 0 = 2  ← 2 units
  i=6: min(2, 3) - 1 = 1  ← 1 unit
  i=7: min(3, 3) - 3 = 0
  i=8: min(3, 2) - 2 = 0
  i=9: min(3, 2) - 1 = 1  ← 1 unit
  i=10: min(3, 2) - 2 = 0
  i=11: min(3, 1) - 1 = 0

Total: 1 + 1 + 2 + 1 + 1 = 6
```

#### Example 2: `height = [4,2,0,3,2,5]`

```text
Index:  0  1  2  3  4  5
                       #
#                      #
#        #             #
#  #     #  #          #
#  #     #  #          #

Water at each position:
  i=0: edge, 0
  i=1: min(4, 5) - 2 = 2  ← 2 units
  i=2: min(4, 5) - 0 = 4  ← 4 units
  i=3: min(4, 5) - 3 = 1  ← 1 unit
  i=4: min(4, 5) - 2 = 2  ← 2 units
  i=5: edge, 0

Total: 2 + 4 + 1 + 2 = 9
```

### 6. Key Observations

1. **Per-column thinking** — Water at any position `i` depends on the tallest bar to its left and the tallest bar to its right: `water[i] = min(leftMax[i], rightMax[i]) - height[i]`.
2. **Water level is bounded by the shorter side** — Just like a real container, water level at position `i` is limited by the shorter of the two tallest boundaries.
3. **Edge positions can never hold water** — The first and last bars have no wall on one side.
4. **Three viable approaches** — Two Pointers (O(n) time, O(1) space), DP with prefix arrays (O(n) time, O(n) space), and Monotonic Stack (O(n) time, O(n) space).

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Two Pointers | O(n) scan from both ends, track running max | Trapping Rain Water (this problem) |
| Dynamic Programming | Precompute leftMax[] and rightMax[] arrays | Prefix/suffix max pattern |
| Monotonic Stack | Process bars left-to-right, pop shorter bars to compute water | Decreasing stack for "next greater" |

**Chosen pattern:** `Two Pointers`
**Reason:** Optimal O(n) time with O(1) space. Leverages the insight that we only need the running max from each side, not the full arrays.

---

## Approach 1: Two Pointers (Optimal)

### Thought process

> The key insight: at any position, the water level depends on `min(leftMax, rightMax)`.
>
> Instead of precomputing both arrays, we use two pointers from both ends.
> We maintain `leftMax` and `rightMax` as we go.
>
> **Critical insight:** If `leftMax < rightMax`, then for the left pointer's position,
> the water is determined by `leftMax` regardless of what rightMax actually is (it could be even larger).
> So we can safely process the left side. Vice versa for the right side.

### Algorithm (step-by-step)

1. Initialize `left = 0`, `right = n - 1`, `leftMax = 0`, `rightMax = 0`, `water = 0`
2. While `left < right`:
   a. If `height[left] < height[right]`:
      - If `height[left] >= leftMax` → update `leftMax = height[left]`
      - Else → add `leftMax - height[left]` to `water`
      - Move `left++`
   b. Else:
      - If `height[right] >= rightMax` → update `rightMax = height[right]`
      - Else → add `rightMax - height[right]` to `water`
      - Move `right--`
3. Return `water`

### Pseudocode

```text
function trap(height):
    left = 0, right = n - 1
    leftMax = 0, rightMax = 0
    water = 0

    while left < right:
        if height[left] < height[right]:
            if height[left] >= leftMax:
                leftMax = height[left]
            else:
                water += leftMax - height[left]
            left++
        else:
            if height[right] >= rightMax:
                rightMax = height[right]
            else:
                water += rightMax - height[right]
            right--

    return water
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each element is visited exactly once |
| **Space** | O(1) | Only a few variables — no arrays needed |

### Implementation

#### Go

```go
// trap — Two Pointers approach (Optimal)
// Time: O(n), Space: O(1)
func trap(height []int) int {
    left, right := 0, len(height)-1
    leftMax, rightMax := 0, 0
    water := 0

    for left < right {
        if height[left] < height[right] {
            if height[left] >= leftMax {
                leftMax = height[left]
            } else {
                water += leftMax - height[left]
            }
            left++
        } else {
            if height[right] >= rightMax {
                rightMax = height[right]
            } else {
                water += rightMax - height[right]
            }
            right--
        }
    }

    return water
}
```

#### Java

```java
class Solution {
    // trap — Two Pointers approach (Optimal)
    // Time: O(n), Space: O(1)
    public int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }

        return water;
    }
}
```

#### Python

```python
class Solution:
    def trap(self, height: list[int]) -> int:
        """
        Two Pointers approach (Optimal)
        Time: O(n), Space: O(1)
        """
        left, right = 0, len(height) - 1
        left_max, right_max = 0, 0
        water = 0

        while left < right:
            if height[left] < height[right]:
                if height[left] >= left_max:
                    left_max = height[left]
                else:
                    water += left_max - height[left]
                left += 1
            else:
                if height[right] >= right_max:
                    right_max = height[right]
                else:
                    water += right_max - height[right]
                right -= 1

        return water
```

### Dry Run

```text
Input: height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]

left=0, right=11, leftMax=0, rightMax=0, water=0

Step 1:  h[0]=0 < h[11]=1
         h[0]=0 >= leftMax=0 → leftMax=0
         left++ → left=1

Step 2:  h[1]=1 >= h[11]=1
         h[11]=1 >= rightMax=0 → rightMax=1
         right-- → right=10

Step 3:  h[1]=1 < h[10]=2
         h[1]=1 >= leftMax=0 → leftMax=1
         left++ → left=2

Step 4:  h[2]=0 < h[10]=2
         h[2]=0 < leftMax=1 → water += 1-0 = 1
         water=1, left++ → left=3

Step 5:  h[3]=2 >= h[10]=2
         h[10]=2 >= rightMax=1 → rightMax=2
         right-- → right=9

Step 6:  h[3]=2 >= h[9]=1
         h[9]=1 < rightMax=2 → water += 2-1 = 1
         water=2, right-- → right=8

Step 7:  h[3]=2 >= h[8]=2
         h[8]=2 >= rightMax=2 → rightMax=2
         right-- → right=7

Step 8:  h[3]=2 < h[7]=3
         h[3]=2 >= leftMax=1 → leftMax=2
         left++ → left=4

Step 9:  h[4]=1 < h[7]=3
         h[4]=1 < leftMax=2 → water += 2-1 = 1
         water=3, left++ → left=5

Step 10: h[5]=0 < h[7]=3
         h[5]=0 < leftMax=2 → water += 2-0 = 2
         water=5, left++ → left=6

Step 11: h[6]=1 < h[7]=3
         h[6]=1 < leftMax=2 → water += 2-1 = 1
         water=6, left++ → left=7

left == right → STOP

Total operations: 11
Result: 6 ✓
```

---

## Approach 2: Dynamic Programming

### Thought process

> For each position `i`, if we know the maximum height to its left (`leftMax[i]`)
> and the maximum height to its right (`rightMax[i]`), the water at position `i` is:
>
> `water[i] = max(0, min(leftMax[i], rightMax[i]) - height[i])`
>
> We can precompute these two arrays in two passes, then sum up the water.

### Algorithm (step-by-step)

1. Create `leftMax[n]` — scan left to right, `leftMax[i] = max(leftMax[i-1], height[i])`
2. Create `rightMax[n]` — scan right to left, `rightMax[i] = max(rightMax[i+1], height[i])`
3. For each `i`, add `min(leftMax[i], rightMax[i]) - height[i]` to total water
4. Return total water

### Pseudocode

```text
function trap(height):
    n = len(height)
    leftMax[0] = height[0]
    for i = 1 to n-1:
        leftMax[i] = max(leftMax[i-1], height[i])

    rightMax[n-1] = height[n-1]
    for i = n-2 down to 0:
        rightMax[i] = max(rightMax[i+1], height[i])

    water = 0
    for i = 0 to n-1:
        water += min(leftMax[i], rightMax[i]) - height[i]

    return water
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Three linear passes |
| **Space** | O(n) | Two arrays of size n for leftMax and rightMax |

### Implementation

#### Go

```go
// trapDP — Dynamic Programming approach
// Time: O(n), Space: O(n)
func trapDP(height []int) int {
    n := len(height)
    if n <= 2 {
        return 0
    }

    leftMax := make([]int, n)
    rightMax := make([]int, n)

    // Build leftMax: max height from left up to index i
    leftMax[0] = height[0]
    for i := 1; i < n; i++ {
        leftMax[i] = leftMax[i-1]
        if height[i] > leftMax[i] {
            leftMax[i] = height[i]
        }
    }

    // Build rightMax: max height from right up to index i
    rightMax[n-1] = height[n-1]
    for i := n - 2; i >= 0; i-- {
        rightMax[i] = rightMax[i+1]
        if height[i] > rightMax[i] {
            rightMax[i] = height[i]
        }
    }

    // Calculate water at each position
    water := 0
    for i := 0; i < n; i++ {
        level := leftMax[i]
        if rightMax[i] < level {
            level = rightMax[i]
        }
        water += level - height[i]
    }

    return water
}
```

#### Java

```java
class Solution {
    // trapDP — Dynamic Programming approach
    // Time: O(n), Space: O(n)
    public int trapDP(int[] height) {
        int n = height.length;
        if (n <= 2) return 0;

        int[] leftMax = new int[n];
        int[] rightMax = new int[n];

        // Build leftMax
        leftMax[0] = height[0];
        for (int i = 1; i < n; i++) {
            leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        }

        // Build rightMax
        rightMax[n - 1] = height[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        }

        // Calculate water
        int water = 0;
        for (int i = 0; i < n; i++) {
            water += Math.min(leftMax[i], rightMax[i]) - height[i];
        }

        return water;
    }
}
```

#### Python

```python
class Solution:
    def trapDP(self, height: list[int]) -> int:
        """
        Dynamic Programming approach
        Time: O(n), Space: O(n)
        """
        n = len(height)
        if n <= 2:
            return 0

        left_max = [0] * n
        right_max = [0] * n

        # Build leftMax
        left_max[0] = height[0]
        for i in range(1, n):
            left_max[i] = max(left_max[i - 1], height[i])

        # Build rightMax
        right_max[n - 1] = height[n - 1]
        for i in range(n - 2, -1, -1):
            right_max[i] = max(right_max[i + 1], height[i])

        # Calculate water
        water = 0
        for i in range(n):
            water += min(left_max[i], right_max[i]) - height[i]

        return water
```

### Dry Run

```text
Input: height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]

Pass 1 — Build leftMax (scan left → right):
  leftMax = [0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3]

Pass 2 — Build rightMax (scan right → left):
  rightMax = [3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 1]

Pass 3 — Calculate water at each position:
  i=0:  min(0, 3) - 0 = 0
  i=1:  min(1, 3) - 1 = 0
  i=2:  min(1, 3) - 0 = 1  ← 1 unit
  i=3:  min(2, 3) - 2 = 0
  i=4:  min(2, 3) - 1 = 1  ← 1 unit
  i=5:  min(2, 3) - 0 = 2  ← 2 units
  i=6:  min(2, 3) - 1 = 1  ← 1 unit
  i=7:  min(3, 3) - 3 = 0
  i=8:  min(3, 2) - 2 = 0
  i=9:  min(3, 2) - 1 = 1  ← 1 unit
  i=10: min(3, 2) - 2 = 0
  i=11: min(3, 1) - 1 = 0

Total: 0+0+1+0+1+2+1+0+0+1+0+0 = 6
Result: 6 ✓
```

---

## Approach 3: Monotonic Stack

### Thought process

> Instead of computing water column-by-column (vertically), we can compute it
> layer-by-layer (horizontally) using a stack.
>
> We maintain a **decreasing stack** of indices. When we encounter a bar taller
> than the stack top, we pop the top — the popped bar is a "valley" that can
> hold water bounded by the current bar and the new stack top.

### Algorithm (step-by-step)

1. Initialize empty stack and `water = 0`
2. For each index `i` from 0 to n-1:
   a. While stack is not empty AND `height[i] > height[stack.top]`:
      - Pop `mid = stack.pop()`
      - If stack is empty → break (no left boundary)
      - `left = stack.top`
      - `width = i - left - 1`
      - `bounded_height = min(height[left], height[i]) - height[mid]`
      - `water += width * bounded_height`
   b. Push `i` onto stack
3. Return `water`

### Pseudocode

```text
function trap(height):
    stack = []
    water = 0

    for i = 0 to n-1:
        while stack not empty AND height[i] > height[stack.top]:
            mid = stack.pop()
            if stack is empty:
                break
            left = stack.top
            width = i - left - 1
            h = min(height[left], height[i]) - height[mid]
            water += width * h
        stack.push(i)

    return water
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each index is pushed and popped at most once |
| **Space** | O(n) | Stack can hold up to n indices in worst case |

### Implementation

#### Go

```go
// trapStack — Monotonic Stack approach
// Time: O(n), Space: O(n)
func trapStack(height []int) int {
    stack := []int{}
    water := 0

    for i := 0; i < len(height); i++ {
        for len(stack) > 0 && height[i] > height[stack[len(stack)-1]] {
            mid := stack[len(stack)-1]
            stack = stack[:len(stack)-1]

            if len(stack) == 0 {
                break
            }

            left := stack[len(stack)-1]
            width := i - left - 1
            h := height[left]
            if height[i] < h {
                h = height[i]
            }
            h -= height[mid]
            water += width * h
        }
        stack = append(stack, i)
    }

    return water
}
```

#### Java

```java
class Solution {
    // trapStack — Monotonic Stack approach
    // Time: O(n), Space: O(n)
    public int trapStack(int[] height) {
        Deque<Integer> stack = new ArrayDeque<>();
        int water = 0;

        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int mid = stack.pop();
                if (stack.isEmpty()) break;

                int left = stack.peek();
                int width = i - left - 1;
                int h = Math.min(height[left], height[i]) - height[mid];
                water += width * h;
            }
            stack.push(i);
        }

        return water;
    }
}
```

#### Python

```python
class Solution:
    def trapStack(self, height: list[int]) -> int:
        """
        Monotonic Stack approach
        Time: O(n), Space: O(n)
        """
        stack = []
        water = 0

        for i in range(len(height)):
            while stack and height[i] > height[stack[-1]]:
                mid = stack.pop()
                if not stack:
                    break
                left = stack[-1]
                width = i - left - 1
                h = min(height[left], height[i]) - height[mid]
                water += width * h
            stack.append(i)

        return water
```

### Dry Run

```text
Input: height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]

i=0: h=0, stack=[], push 0        → stack=[0]
i=1: h=1 > h[0]=0
     pop mid=0, stack empty → break
     push 1                        → stack=[1]
i=2: h=0, push 2                  → stack=[1, 2]
i=3: h=2 > h[2]=0
     pop mid=2, left=1, width=3-1-1=1, h=min(1,2)-0=1
     water += 1*1 = 1              water=1
     h=2 > h[1]=1
     pop mid=1, stack empty → break
     push 3                        → stack=[3]
i=4: h=1, push 4                  → stack=[3, 4]
i=5: h=0, push 5                  → stack=[3, 4, 5]
i=6: h=1 > h[5]=0
     pop mid=5, left=4, width=6-4-1=1, h=min(1,1)-0=1
     water += 1*1 = 1              water=2
     h=1 == h[4]=1 → stop
     push 6                        → stack=[3, 4, 6]
i=7: h=3 > h[6]=1
     pop mid=6, left=4, width=7-4-1=2, h=min(1,3)-1=0
     water += 2*0 = 0              water=2
     h=3 > h[4]=1
     pop mid=4, left=3, width=7-3-1=3, h=min(2,3)-1=1
     water += 3*1 = 3              water=5
     h=3 > h[3]=2
     pop mid=3, stack empty → break
     push 7                        → stack=[7]
i=8: h=2, push 8                  → stack=[7, 8]
i=9: h=1, push 9                  → stack=[7, 8, 9]
i=10: h=2 > h[9]=1
      pop mid=9, left=8, width=10-8-1=1, h=min(2,2)-1=1
      water += 1*1 = 1             water=6
      h=2 == h[8]=2 → stop
      push 10                      → stack=[7, 8, 10]
i=11: h=1, push 11                → stack=[7, 8, 10, 11]

Result: 6 ✓
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Two Pointers | O(n) | O(1) | Optimal time and space | Requires understanding pointer logic |
| 2 | Dynamic Programming | O(n) | O(n) | Intuitive, easy to understand | Uses extra O(n) space for arrays |
| 3 | Monotonic Stack | O(n) | O(n) | Horizontal water calculation, elegant | More complex logic, harder to debug |

### Which solution to choose?

- **In an interview:** Approach 1 (Two Pointers) — optimal time and space, demonstrates mastery
- **For understanding:** Approach 2 (DP) — most intuitive, easy to visualize per-column water
- **For stack practice:** Approach 3 (Monotonic Stack) — great for learning the monotonic stack pattern
- **On Leetcode:** Any of the three — all are O(n) and pass within the time limit

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Empty or single bar | `height=[0]` | `0` | No walls to trap water |
| 2 | Two bars | `height=[1,2]` | `0` | No space between for water |
| 3 | Ascending | `height=[1,2,3,4]` | `0` | No valley to trap water |
| 4 | Descending | `height=[4,3,2,1]` | `0` | No valley to trap water |
| 5 | V-shape | `height=[3,0,3]` | `3` | Simple valley |
| 6 | All zeros | `height=[0,0,0,0]` | `0` | No bars, no water |
| 7 | All same height | `height=[2,2,2,2]` | `0` | Flat surface, no valleys |
| 8 | Single valley | `height=[5,1,5]` | `4` | Water = min(5,5)-1 = 4 |

---

## Common Mistakes

### Mistake 1: Not handling edge cases (n <= 2)

```python
# WRONG — crashes on empty or single-element input
def trap(self, height):
    left, right = 0, len(height) - 1
    # If n=0 or n=1, left > right causes issues

# CORRECT — guard clause
def trap(self, height):
    if len(height) <= 2:
        return 0
    left, right = 0, len(height) - 1
    ...
```

### Mistake 2: Forgetting to check for negative water

```python
# WRONG — can add negative water if height > water level
water += min(left_max[i], right_max[i]) - height[i]
# This is actually safe because leftMax[i] >= height[i] always
# But if you compute leftMax incorrectly, you might get negatives

# SAFE — clamp to zero
water += max(0, min(left_max[i], right_max[i]) - height[i])
```

### Mistake 3: Two Pointers — comparing with wrong max

```python
# WRONG — using leftMax when processing right side
if height[left] < height[right]:
    water += left_max - height[right]  # Should use height[left]!
    left += 1

# CORRECT — left side uses leftMax, right side uses rightMax
if height[left] < height[right]:
    if height[left] >= left_max:
        left_max = height[left]
    else:
        water += left_max - height[left]
    left += 1
```

### Mistake 4: Stack — forgetting to check if stack is empty after pop

```python
# WRONG — accessing stack[-1] after pop without checking
mid = stack.pop()
left = stack[-1]  # IndexError if stack is empty!

# CORRECT — check if stack has elements
mid = stack.pop()
if not stack:
    break  # No left boundary, skip
left = stack[-1]
```

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [11. Container With Most Water](https://leetcode.com/problems/container-with-most-water/) | :yellow_circle: Medium | Two Pointers, water between bars |
| 2 | [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | :red_circle: Hard | Stack-based, elevation bars |
| 3 | [407. Trapping Rain Water II](https://leetcode.com/problems/trapping-rain-water-ii/) | :red_circle: Hard | 3D version of this problem |
| 4 | [238. Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/) | :yellow_circle: Medium | Prefix/suffix array pattern |
| 5 | [739. Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | :yellow_circle: Medium | Monotonic stack pattern |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Two Pointers** visualization with left/right pointers moving inward
> - Elevation bars with water filling animation
> - Step-by-step log showing water calculations
> - Preset examples and custom input support
> - Speed control slider and Play/Pause/Reset controls
