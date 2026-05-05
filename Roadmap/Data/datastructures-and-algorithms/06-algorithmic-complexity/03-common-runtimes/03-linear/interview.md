# Linear Time O(n) — Interview Questions

## Table of Contents

1. [Conceptual Questions](#conceptual-questions)
2. [Quick-Fire Questions](#quick-fire-questions)
3. [Scenario-Based Questions](#scenario-based-questions)
4. [Coding Challenge: Maximum Subarray (Kadane's Algorithm)](#coding-challenge)
5. [Answers and Discussion](#answers-and-discussion)

---

## Conceptual Questions

### Q1: What does O(n) mean in practical terms?

**Expected answer:** An algorithm is O(n) when its running time grows linearly with the input size. Doubling the input roughly doubles the time. Each element is processed a constant number of times.

### Q2: Is O(2n) the same as O(n)?

**Expected answer:** Yes. In Big-O notation, constant factors are dropped. O(2n) = O(n). An algorithm that makes two passes over the data is still O(n).

### Q3: Can you find the maximum element in an unsorted array faster than O(n)?

**Expected answer:** No. Every element must be examined because any unexamined element could be the maximum. The lower bound is Omega(n). This can be proven with an adversary argument.

### Q4: Name three techniques for converting O(n^2) algorithms to O(n).

**Expected answer:**
1. **Hash maps** — replace nested lookups with O(1) hash table lookups.
2. **Two pointers** — traverse from both ends or with fast/slow pointers.
3. **Sliding window** — maintain a running computation over a subarray.

### Q5: Why is linear search O(n) but binary search O(log n)?

**Expected answer:** Linear search makes no assumptions and must check each element sequentially. Binary search requires sorted data and eliminates half the remaining elements with each comparison. The prerequisite (sorted data) enables the logarithmic speedup.

### Q6: Give an example where O(n) is faster than O(n log n) in practice.

**Expected answer:** Counting sort on integers in a small range [0, k] is O(n + k). For k = O(n), this is O(n), beating O(n log n) comparison-based sorts. For example, sorting ages (0-150) of 1 million people.

### Q7: What is the difference between O(n) and Theta(n)?

**Expected answer:** O(n) is an upper bound — the algorithm runs in at most cn steps for large n. Theta(n) is a tight bound — the algorithm runs in both at least c1*n and at most c2*n steps. An algorithm that is Theta(n) is always O(n), but an O(n) algorithm might actually be faster (e.g., O(1) in the best case).

---

## Quick-Fire Questions

| # | Question | Expected Answer |
|---|----------|-----------------|
| 1 | Traversing a linked list of n nodes? | O(n) |
| 2 | Copying an array of n elements? | O(n) |
| 3 | Finding a value in a hash map? | O(1) average, O(n) worst case |
| 4 | Reversing a string of length n? | O(n) |
| 5 | Computing prefix sums of an array? | O(n) |
| 6 | Appending n elements to a dynamic array? | O(n) amortized |
| 7 | Checking if two strings are anagrams? | O(n) with counting |
| 8 | Finding the first non-repeating character? | O(n) with hash map |
| 9 | Merging two sorted arrays of size n? | O(n) |
| 10| Removing duplicates from a sorted array? | O(n) with two pointers |

---

## Scenario-Based Questions

### S1: Design Question

*"You have a stream of 1 billion integers and 1 GB of RAM. Find the median."*

**Expected discussion:**
- Cannot sort in memory (needs ~4 GB for 32-bit ints).
- Two-pass approach: first pass determines approximate median range using histogram; second pass finds exact median within that range.
- Both passes are O(n). Total: O(n) time with O(1) extra space relative to n.

### S2: Trade-off Question

*"You have a function called 10,000 times per second. Each call processes an array of 100 elements. Should you optimize from O(n^2) to O(n)?"*

**Expected discussion:**
- n = 100: O(n^2) = 10,000 operations, O(n) = 100 operations.
- 10,000 calls/sec * 10,000 ops = 100 million operations/sec for O(n^2).
- 10,000 calls/sec * 100 ops = 1 million operations/sec for O(n).
- The O(n) version frees 99% of CPU for other work. Worth optimizing.

### S3: System Design

*"Your database query does a full table scan on 50 million rows and takes 3 seconds. The product team wants sub-100ms response time. What do you do?"*

**Expected discussion:**
- Add an appropriate index to make it O(log n) — this is the primary answer.
- If the query is an aggregation over all rows (O(n) is unavoidable), pre-compute the result and cache it. Refresh periodically or on writes.
- Consider materialized views or summary tables.
- If the scan is needed but rare, move it to a background worker.

---

## Coding Challenge

### Maximum Subarray Sum (Kadane's Algorithm)

**Problem:** Given an integer array `nums`, find the contiguous subarray with the largest sum and return that sum.

**Examples:**
- Input: `[-2, 1, -3, 4, -1, 2, 1, -5, 4]` -> Output: `6` (subarray `[4, -1, 2, 1]`)
- Input: `[1]` -> Output: `1`
- Input: `[-1, -2, -3]` -> Output: `-1`

**Constraints:**
- 1 <= nums.length <= 100,000
- -10,000 <= nums[i] <= 10,000

**Requirements:**
- Solve in O(n) time and O(1) extra space.
- Return only the maximum sum (not the subarray itself).

---

### Solution

**Go:**

```go
package main

import "fmt"

// maxSubArray finds the maximum subarray sum using Kadane's algorithm.
// Time: O(n), Space: O(1).
func maxSubArray(nums []int) int {
    // Initialize both to the first element
    currentSum := nums[0]
    maxSum := nums[0]

    // Start from the second element
    for i := 1; i < len(nums); i++ {
        // Either extend the current subarray or start a new one
        if currentSum+nums[i] > nums[i] {
            currentSum = currentSum + nums[i]
        } else {
            currentSum = nums[i]
        }

        // Update global maximum
        if currentSum > maxSum {
            maxSum = currentSum
        }
    }

    return maxSum
}

func main() {
    tests := []struct {
        nums     []int
        expected int
    }{
        {[]int{-2, 1, -3, 4, -1, 2, 1, -5, 4}, 6},
        {[]int{1}, 1},
        {[]int{-1, -2, -3}, -1},
        {[]int{5, 4, -1, 7, 8}, 23},
    }

    for _, test := range tests {
        result := maxSubArray(test.nums)
        status := "PASS"
        if result != test.expected {
            status = "FAIL"
        }
        fmt.Printf("%s: maxSubArray(%v) = %d (expected %d)\n",
            status, test.nums, result, test.expected)
    }
}
```

**Java:**

```java
public class MaxSubArray {

    /**
     * Kadane's Algorithm: find maximum subarray sum.
     * Time: O(n), Space: O(1).
     */
    public static int maxSubArray(int[] nums) {
        int currentSum = nums[0];
        int maxSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            // Extend current subarray or start new one
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        int[][] tests = {
            {-2, 1, -3, 4, -1, 2, 1, -5, 4},
            {1},
            {-1, -2, -3},
            {5, 4, -1, 7, 8}
        };
        int[] expected = {6, 1, -1, 23};

        for (int i = 0; i < tests.length; i++) {
            int result = maxSubArray(tests[i]);
            String status = result == expected[i] ? "PASS" : "FAIL";
            System.out.printf("%s: maxSubArray = %d (expected %d)%n",
                              status, result, expected[i]);
        }
    }
}
```

**Python:**

```python
def max_sub_array(nums: list[int]) -> int:
    """
    Kadane's Algorithm: find maximum subarray sum.
    Time: O(n), Space: O(1).
    """
    current_sum = nums[0]
    max_sum = nums[0]

    for i in range(1, len(nums)):
        # Either extend the current subarray or start a new one
        current_sum = max(nums[i], current_sum + nums[i])
        max_sum = max(max_sum, current_sum)

    return max_sum


if __name__ == "__main__":
    tests = [
        ([-2, 1, -3, 4, -1, 2, 1, -5, 4], 6),
        ([1], 1),
        ([-1, -2, -3], -1),
        ([5, 4, -1, 7, 8], 23),
    ]

    for nums, expected in tests:
        result = max_sub_array(nums)
        status = "PASS" if result == expected else "FAIL"
        print(f"{status}: max_sub_array({nums}) = {result} (expected {expected})")
```

---

### Follow-Up Questions

1. **"Can you also return the start and end indices of the subarray?"**
   Track `temp_start` when starting a new subarray and update `start`/`end` when updating `maxSum`.

2. **"What if the array is circular (the subarray can wrap around)?"**
   The answer is `max(kadane(arr), total_sum - kadane_min(arr))`, handling the edge case where all elements are negative.

3. **"What if you need the maximum product subarray instead of sum?"**
   Track both the running maximum and minimum product (since multiplying by a negative flips sign).

4. **"Can you solve this with divide and conquer?"**
   Yes, in O(n log n): split the array in half, recursively solve both halves, and find the maximum crossing subarray in O(n). This is slower than Kadane's but demonstrates the divide-and-conquer paradigm.

---

## Answers and Discussion

### Conceptual Answers Summary

| Q# | Key Point |
|----|-----------|
| Q1 | Linear growth — double input, double time |
| Q2 | Yes, constants are dropped in Big-O |
| Q3 | No, Omega(n) lower bound for unsorted max |
| Q4 | Hash maps, two pointers, sliding window |
| Q5 | Binary search requires sorted data; linear search does not |
| Q6 | Counting sort on bounded integers |
| Q7 | O(n) is upper bound; Theta(n) is tight bound |

### Interviewer Evaluation Criteria

- **Junior:** Can explain O(n) and implement linear search / find max.
- **Middle:** Knows two-pointer, sliding window, hash map techniques. Can implement Kadane's.
- **Senior:** Discusses trade-offs, system design implications, when O(n) is acceptable.
- **Professional:** Understands lower bound proofs, median of medians, non-comparison sorts.
