# 0090. Subsets II

## Problem

| | |
|---|---|
| **Leetcode** | [90. Subsets II](https://leetcode.com/problems/subsets-ii/) |
| **Difficulty** | :yellow_circle: Medium |
| **Tags** | `Array`, `Backtracking`, `Bit Manipulation` |

> Given an integer array `nums` that may contain duplicates, return *all possible subsets (the power set)*.
>
> The solution set **must not** contain duplicate subsets. Return the solution in any order.

### Examples

```
Input: nums = [1,2,2]
Output: [[],[1],[1,2],[1,2,2],[2],[2,2]]

Input: nums = [0]
Output: [[],[0]]
```

### Constraints

- `1 <= nums.length <= 10`
- `-10 <= nums[i] <= 10`

---

## Approach: Sort + Backtracking with Skip-Duplicates

### Idea

Sort `nums`. In backtracking, when iterating from `start`, skip any `i > start` where `nums[i] == nums[i-1]` — this avoids choosing the same duplicate twice at the same recursion depth.

### Algorithm

```text
sort(nums)
def bt(start):
    emit cur
    for i from start to n-1:
        if i > start and nums[i] == nums[i-1]: continue
        cur.push(nums[i])
        bt(i + 1)
        cur.pop()
```

### Complexity

- Time: O(n * 2^n)
- Space: O(n)

### Implementation

#### Go

```go
import "sort"

func subsetsWithDup(nums []int) [][]int {
    sort.Ints(nums)
    result := [][]int{}
    cur := []int{}
    var bt func(start int)
    bt = func(start int) {
        cp := make([]int, len(cur))
        copy(cp, cur)
        result = append(result, cp)
        for i := start; i < len(nums); i++ {
            if i > start && nums[i] == nums[i-1] {
                continue
            }
            cur = append(cur, nums[i])
            bt(i + 1)
            cur = cur[:len(cur)-1]
        }
    }
    bt(0)
    return result
}
```

#### Java

```java
class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        bt(0, nums, new ArrayList<>(), result);
        return result;
    }
    private void bt(int start, int[] nums, List<Integer> cur, List<List<Integer>> result) {
        result.add(new ArrayList<>(cur));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;
            cur.add(nums[i]);
            bt(i + 1, nums, cur, result);
            cur.remove(cur.size() - 1);
        }
    }
}
```

#### Python

```python
class Solution:
    def subsetsWithDup(self, nums: List[int]) -> List[List[int]]:
        nums.sort()
        result = []
        cur = []
        def bt(start: int):
            result.append(cur.copy())
            for i in range(start, len(nums)):
                if i > start and nums[i] == nums[i - 1]:
                    continue
                cur.append(nums[i])
                bt(i + 1)
                cur.pop()
        bt(0)
        return result
```

---

## Visual Animation

> [animation.html](./animation.html)
