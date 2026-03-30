# 0011. Container With Most Water

## Table of Contents

1. [Problem](#problem)
2. [Problem Breakdown](#problem-breakdown)
3. [Approach 1: Brute Force](#approach-1-brute-force)
4. [Approach 2: Two Pointers](#approach-2-two-pointers)
5. [Complexity Comparison](#complexity-comparison)
6. [Edge Cases](#edge-cases)
7. [Common Mistakes](#common-mistakes)
8. [Related Problems](#related-problems)
9. [Visual Animation](#visual-animation)

---

## Problem

| | |
|---|---|
| **Leetcode** | [11. Container With Most Water](https://leetcode.com/problems/container-with-most-water/) |
| **Difficulty** | 🟡 Medium |
| **Tags** | `Array`, `Two Pointers`, `Greedy` |

### Description

> You are given an integer array `height` of length `n`. There are `n` vertical lines drawn such that the two endpoints of the `i`th line are `(i, 0)` and `(i, height[i])`.
>
> Find two lines that together with the x-axis forms a container, such that the container contains the most water.
>
> Return the maximum amount of water a container can store.
>
> **Notice** that you may not slant the container.

### Examples

```
Example 1:
Input: height = [1,8,6,2,5,4,8,3,7]
Output: 49
Explanation: The vertical lines are represented by array [1,8,6,2,5,4,8,3,7].
In this case, the max area of water the container can contain is 49
(between index 1 and index 8, width = 7, height = min(8, 7) = 7, area = 7 * 7 = 49).

Example 2:
Input: height = [1,1]
Output: 1
Explanation: width = 1, height = min(1, 1) = 1, area = 1 * 1 = 1.

Example 3:
Input: height = [4,3,2,1,4]
Output: 16
Explanation: Between index 0 and index 4, width = 4, height = min(4, 4) = 4, area = 4 * 4 = 16.
```

### Constraints

- `n == height.length`
- `2 <= n <= 10^5`
- `0 <= height[i] <= 10^4`

---

## Problem Breakdown

### 1. What is being asked?

Given `n` vertical lines on a coordinate plane, find two lines that form a container holding the maximum amount of water. The water level is determined by the **shorter** of the two lines, and the width is the distance between them.

### 2. What is the input?

| Parameter | Type | Description |
|---|---|---|
| `height` | `int[]` | Array of non-negative integers representing line heights |

Important observations about the input:
- The array is **not sorted**
- Heights can be **zero** (a line of height 0 holds no water)
- The array always has at least 2 elements
- Indices represent the x-coordinates of the lines

### 3. What is the output?

- A single **integer** — the maximum area of water that can be contained
- Area is calculated as: `min(height[i], height[j]) * (j - i)`

### 4. Constraints analysis

| Constraint | Significance |
|---|---|
| `n <= 10^5` | O(n^2) = 10^10 — will TLE. Need O(n) or O(n log n) |
| `height[i] <= 10^4` | Max area = 10^4 * 10^5 = 10^9 — fits in int32 |
| `height[i] >= 0` | Lines can have zero height |
| `n >= 2` | Always at least one pair to check |

### 5. Step-by-step example analysis

#### Example 1: `height = [1,8,6,2,5,4,8,3,7]`

```text
Initial state: height = [1, 8, 6, 2, 5, 4, 8, 3, 7]

Visual representation:
Index:  0  1  2  3  4  5  6  7  8
        |  |              |     |
        |  |  |           |     |
        |  |  |        |  |     |
        |  |  |     |  |  |     |
        |  |  |     |  |  |  |  |
        |  |  |  |  |  |  |  |  |
        |  |  |  |  |  |  |  |  |
        |  |  |  |  |  |  |  |  |

Best container: lines at index 1 (h=8) and index 8 (h=7)
  width  = 8 - 1 = 7
  height = min(8, 7) = 7
  area   = 7 * 7 = 49

Result: 49
```

#### Example 2: `height = [1,1]`

```text
Index: 0  1
       |  |

Only one pair: lines at index 0 and index 1
  width  = 1 - 0 = 1
  height = min(1, 1) = 1
  area   = 1 * 1 = 1

Result: 1
```

#### Example 3: `height = [4,3,2,1,4]`

```text
Index: 0  1  2  3  4
       |           |
       |  |        |
       |  |  |     |
       |  |  |  |  |

Best container: lines at index 0 (h=4) and index 4 (h=4)
  width  = 4 - 0 = 4
  height = min(4, 4) = 4
  area   = 4 * 4 = 16

Result: 16
```

### 6. Key Observations

1. **Area formula** — `area = min(height[i], height[j]) * (j - i)`. The area depends on both the width and the shorter height.
2. **Width vs Height tradeoff** — Wider containers have more width but might have shorter limiting heights. We need to balance both factors.
3. **Greedy insight** — Starting from the widest container (left=0, right=n-1), we can only improve by finding taller lines. Moving the shorter pointer inward is the only way to potentially increase the area.
4. **Why move the shorter pointer?** — If we move the taller pointer, the width decreases and the height can only stay the same or decrease (limited by the shorter line), so the area can never increase.

### 7. Pattern identification

| Pattern | Why it fits | Example |
|---|---|---|
| Two Pointers | O(n) scan from both ends, greedy elimination | Container With Most Water (this problem) |
| Brute Force | Always works, check all pairs | All problems |

**Chosen pattern:** `Two Pointers (Greedy)`
**Reason:** Starting with the maximum width container and greedily moving the shorter pointer inward guarantees we explore all candidates that could potentially hold more water.

---

## Approach 1: Brute Force

### Thought process

> The simplest idea: check every pair of lines and calculate the area.
> Using two nested loops, compute the area for all pairs.
> Track the maximum area found.

### Algorithm (step-by-step)

1. Initialize `maxArea = 0`
2. Outer loop: `i = 0` to `n-1`
3. Inner loop: `j = i+1` to `n-1`
4. Calculate `area = min(height[i], height[j]) * (j - i)`
5. Update `maxArea = max(maxArea, area)`
6. Return `maxArea`

### Pseudocode

```text
function maxArea(height):
    maxArea = 0
    for i = 0 to n-1:
        for j = i+1 to n-1:
            area = min(height[i], height[j]) * (j - i)
            maxArea = max(maxArea, area)
    return maxArea
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n^2) | Check every pair. n*(n-1)/2 pairs total. |
| **Space** | O(1) | No extra memory — only a few variables. |

### Implementation

#### Go

```go
// maxArea — Brute Force approach
// Time: O(n^2), Space: O(1)
func maxArea(height []int) int {
    n := len(height)
    maxWater := 0

    for i := 0; i < n; i++ {
        for j := i + 1; j < n; j++ {
            // Width between the two lines
            width := j - i

            // Height is limited by the shorter line
            h := height[i]
            if height[j] < h {
                h = height[j]
            }

            // Calculate area
            area := width * h
            if area > maxWater {
                maxWater = area
            }
        }
    }

    return maxWater
}
```

#### Java

```java
class Solution {
    // maxArea — Brute Force approach
    // Time: O(n^2), Space: O(1)
    public int maxArea(int[] height) {
        int n = height.length;
        int maxWater = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Width between the two lines
                int width = j - i;

                // Height is limited by the shorter line
                int h = Math.min(height[i], height[j]);

                // Calculate area
                int area = width * h;
                maxWater = Math.max(maxWater, area);
            }
        }

        return maxWater;
    }
}
```

#### Python

```python
class Solution:
    def maxArea(self, height: list[int]) -> int:
        """
        Brute Force approach
        Time: O(n^2), Space: O(1)
        """
        n = len(height)
        max_water = 0

        for i in range(n):
            for j in range(i + 1, n):
                # Width between the two lines
                width = j - i

                # Height is limited by the shorter line
                h = min(height[i], height[j])

                # Calculate area
                area = width * h
                max_water = max(max_water, area)

        return max_water
```

### Dry Run

```text
Input: height = [1, 8, 6, 2, 5, 4, 8, 3, 7]

i=0 (h=1):
  j=1: min(1,8)*1 = 1    j=2: min(1,6)*2 = 2    j=3: min(1,2)*3 = 3
  j=4: min(1,5)*4 = 4    j=5: min(1,4)*5 = 5    j=6: min(1,8)*6 = 6
  j=7: min(1,3)*7 = 7    j=8: min(1,7)*8 = 8
  maxArea = 8

i=1 (h=8):
  j=2: min(8,6)*1 = 6    j=3: min(8,2)*2 = 4    j=4: min(8,5)*3 = 15
  j=5: min(8,4)*4 = 16   j=6: min(8,8)*5 = 40   j=7: min(8,3)*6 = 18
  j=8: min(8,7)*7 = 49   ← NEW MAX!
  maxArea = 49

... (remaining pairs don't exceed 49)

Total comparisons: 36 (n*(n-1)/2)
Result: 49
```

---

## Approach 2: Two Pointers

### The problem with Brute Force

> Brute Force checks all n*(n-1)/2 pairs — that's O(n^2).
> With n = 100,000 elements, that's ~5 billion operations — way too slow.
> Question: "Can we eliminate pairs without checking them?"

### Optimization idea

> **Start from the widest container** — place `left` at index 0 and `right` at the last index.
> This gives us the maximum possible width.
>
> **Greedy elimination:** At each step, move the pointer pointing to the **shorter** line.
> Why? Because the area is limited by the shorter line. If we move the taller pointer:
> - Width decreases by 1
> - The limiting height stays the same (or gets worse)
> - Area can only decrease or stay the same
>
> By moving the shorter pointer, we might find a taller line that increases the area.

### Algorithm (step-by-step)

1. Initialize `left = 0`, `right = n - 1`, `maxArea = 0`
2. While `left < right`:
   a. Calculate `area = min(height[left], height[right]) * (right - left)`
   b. Update `maxArea = max(maxArea, area)`
   c. If `height[left] < height[right]` → move `left++`
   d. Else → move `right--`
3. Return `maxArea`

### Pseudocode

```text
function maxArea(height):
    left = 0, right = n - 1
    maxArea = 0

    while left < right:
        area = min(height[left], height[right]) * (right - left)
        maxArea = max(maxArea, area)

        if height[left] < height[right]:
            left++
        else:
            right--

    return maxArea
```

### Complexity

| | Complexity | Explanation |
|---|---|---|
| **Time** | O(n) | Each pointer moves at most n times. Total at most n steps. |
| **Space** | O(1) | Only two pointers and a variable for max area. |

### Implementation

#### Go

```go
// maxArea — Two Pointers approach
// Time: O(n), Space: O(1)
func maxArea(height []int) int {
    // Start from the widest container
    left, right := 0, len(height)-1
    maxWater := 0

    for left < right {
        // Calculate the area
        width := right - left
        h := height[left]
        if height[right] < h {
            h = height[right]
        }
        area := width * h

        // Update the maximum area
        if area > maxWater {
            maxWater = area
        }

        // Move the pointer with the shorter line
        if height[left] < height[right] {
            left++
        } else {
            right--
        }
    }

    return maxWater
}
```

#### Java

```java
class Solution {
    // maxArea — Two Pointers approach
    // Time: O(n), Space: O(1)
    public int maxArea(int[] height) {
        // Start from the widest container
        int left = 0, right = height.length - 1;
        int maxWater = 0;

        while (left < right) {
            // Calculate the area
            int width = right - left;
            int h = Math.min(height[left], height[right]);
            int area = width * h;

            // Update the maximum area
            maxWater = Math.max(maxWater, area);

            // Move the pointer with the shorter line
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxWater;
    }
}
```

#### Python

```python
class Solution:
    def maxArea(self, height: list[int]) -> int:
        """
        Two Pointers approach
        Time: O(n), Space: O(1)
        """
        # Start from the widest container
        left, right = 0, len(height) - 1
        max_water = 0

        while left < right:
            # Calculate the area
            width = right - left
            h = min(height[left], height[right])
            area = width * h

            # Update the maximum area
            max_water = max(max_water, area)

            # Move the pointer with the shorter line
            if height[left] < height[right]:
                left += 1
            else:
                right -= 1

        return max_water
```

### Dry Run

```text
Input: height = [1, 8, 6, 2, 5, 4, 8, 3, 7]

left=0, right=8, maxArea=0

Step 1: h[0]=1, h[8]=7, area = min(1,7) * 8 = 8
        maxArea = 8
        h[0] < h[8] → left++ → left=1

Step 2: h[1]=8, h[8]=7, area = min(8,7) * 7 = 49
        maxArea = 49
        h[1] > h[8] → right-- → right=7

Step 3: h[1]=8, h[7]=3, area = min(8,3) * 6 = 18
        maxArea = 49
        h[1] > h[7] → right-- → right=6

Step 4: h[1]=8, h[6]=8, area = min(8,8) * 5 = 40
        maxArea = 49
        h[1] >= h[6] → right-- → right=5

Step 5: h[1]=8, h[5]=4, area = min(8,4) * 4 = 16
        maxArea = 49
        h[1] > h[5] → right-- → right=4

Step 6: h[1]=8, h[4]=5, area = min(8,5) * 3 = 15
        maxArea = 49
        h[1] > h[4] → right-- → right=3

Step 7: h[1]=8, h[3]=2, area = min(8,2) * 2 = 4
        maxArea = 49
        h[1] > h[3] → right-- → right=2

Step 8: h[1]=8, h[2]=6, area = min(8,6) * 1 = 6
        maxArea = 49
        h[1] > h[2] → right-- → right=1

left == right → STOP

Total operations: 8 (vs Brute Force: 36)
Result: 49
```

---

## Complexity Comparison

| # | Approach | Time | Space | Pros | Cons |
|---|---|---|---|---|---|
| 1 | Brute Force | O(n^2) | O(1) | Simple, easy to understand | TLE on large inputs (n=10^5) |
| 2 | Two Pointers | O(n) | O(1) | Optimal time and space | Requires understanding greedy proof |

### Which solution to choose?

- **In an interview:** Approach 2 (Two Pointers) — fast, elegant, and demonstrates greedy thinking
- **In production:** Approach 2 — optimal time and space
- **On Leetcode:** Approach 2 — only O(n) passes within the time limit for n=10^5
- **For learning:** Both — Brute Force to understand the problem, Two Pointers to learn the greedy pattern

---

## Edge Cases

| # | Case | Input | Expected Output | Reason |
|---|---|---|---|---|
| 1 | Minimum input | `height=[1,1]` | `1` | Two elements, smallest possible container |
| 2 | Decreasing heights | `height=[5,4,3,2,1]` | `6` | Best pair: index 0 and 2 (min(5,3)*2=6) or 0 and 3 (min(5,2)*3=6) |
| 3 | Increasing heights | `height=[1,2,3,4,5]` | `6` | Best pair: index 2 and 4 (min(3,5)*2=6) or 1 and 4 (min(2,5)*3=6) |
| 4 | All same heights | `height=[3,3,3,3,3]` | `12` | Best pair: index 0 and 4 (min(3,3)*4=12) |
| 5 | Tall lines at ends | `height=[10,1,1,1,10]` | `40` | Maximum width with tall lines |
| 6 | Zero heights | `height=[0,0]` | `0` | No water can be held |
| 7 | One zero height | `height=[0,5]` | `0` | min(0,5)*1 = 0 |

---

## Common Mistakes

### Mistake 1: Moving the taller pointer instead of the shorter one

```python
# WRONG — moving the taller pointer
if height[left] > height[right]:
    left += 1  # Moving the tall side can only decrease area
else:
    right -= 1

# CORRECT — move the shorter pointer
if height[left] < height[right]:
    left += 1  # Might find a taller line
else:
    right -= 1
```

**Reason:** Moving the taller pointer can never increase the area because the limiting height (the shorter line) stays the same while the width decreases.

### Mistake 2: Using height[i] * width instead of min(height[i], height[j]) * width

```python
# WRONG — using one height instead of the minimum
area = height[left] * (right - left)

# CORRECT — water level is limited by the shorter line
area = min(height[left], height[right]) * (right - left)
```

**Reason:** Water spills over the shorter line, so the area is always limited by `min(height[left], height[right])`.

### Mistake 3: Forgetting to handle equal heights

```python
# WRONG — infinite loop when heights are equal
if height[left] < height[right]:
    left += 1
elif height[left] > height[right]:
    right -= 1
# What if they are equal? Neither pointer moves!

# CORRECT — move either pointer when heights are equal
if height[left] < height[right]:
    left += 1
else:
    right -= 1  # Handles both > and == cases
```

**Reason:** When heights are equal, moving either pointer is fine — the area can only potentially increase by finding a taller line.

---

## Related Problems

| # | Problem | Difficulty | Similarity |
|---|---|---|---|
| 1 | [42. Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | 🔴 Hard | Water between bars, Two Pointers |
| 2 | [15. 3Sum](https://leetcode.com/problems/3sum/) | 🟡 Medium | Two Pointers pattern |
| 3 | [167. Two Sum II](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | 🟡 Medium | Two Pointers from both ends |
| 4 | [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | 🔴 Hard | Maximum area with heights |
| 5 | [407. Trapping Rain Water II](https://leetcode.com/problems/trapping-rain-water-ii/) | 🔴 Hard | 3D version of trapping water |

---

## Visual Animation

> Interactive animation: [animation.html](./animation.html)
>
> In the animation:
> - **Brute Force** tab — two nested loops (i, j) check all pairs
> - **Two Pointers** tab — left and right pointers move inward greedily
> - Height bars visualization with water area highlighting
> - Custom input controls for experimenting with different arrays
