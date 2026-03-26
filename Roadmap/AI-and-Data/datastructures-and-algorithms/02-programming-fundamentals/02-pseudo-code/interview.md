# Pseudo Code — Interview Preparation

## Junior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is pseudo code and why do we use it? | Language-independent algorithm description; plan before coding |
| 2 | Write pseudo code for finding the minimum in an array. | Loop + tracking min variable |
| 3 | How do you represent a loop in pseudo code? | FOR, WHILE keywords with clear bounds |
| 4 | What's the difference between pseudo code and a flowchart? | Text vs visual; pseudo code is closer to code |
| 5 | How do you show recursion in pseudo code? | CALL function with smaller input + base case |

## Middle Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Write pseudo code for binary search and analyze its complexity. | O(log n), halving, loop invariant |
| 2 | How do you determine O(n) from pseudo code? | Count nested loops, recursive calls, Master Theorem |
| 3 | Write pseudo code for merge sort. | Divide, recurse, merge; T(n) = 2T(n/2) + O(n) |
| 4 | What's the difference between top-down and bottom-up DP in pseudo code? | Memoization with recursion vs tabulation with loops |
| 5 | Write pseudo code for BFS on a graph. | Queue + visited set + neighbor exploration |

## Senior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Design a rate limiter using pseudo code. | Token bucket or sliding window algorithm |
| 2 | Write pseudo code for consistent hashing. | Ring, virtual nodes, binary search for node lookup |
| 3 | How would you describe a distributed consensus algorithm? | Raft/Paxos — terms, voting, log replication |

## Professional Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Prove insertion sort correct using a loop invariant. | Initialization, maintenance, termination |
| 2 | Derive the recurrence for merge sort and solve it. | T(n) = 2T(n/2) + O(n) → O(n log n) via Master Theorem |
| 3 | What is Hoare logic and how does it relate to pseudo code? | {P} S {Q} triples, loop invariants, formal verification |

---

## Coding Challenge 1: Pseudo Code → Implementation

> Given this pseudo code, implement it in all 3 languages:

```text
FUNCTION countPairs(array, target)
    SET count = 0
    SET seen = empty set

    FOR each num IN array DO
        SET complement = target - num
        IF complement IN seen THEN
            SET count = count + 1
        END IF
        ADD num TO seen
    END FOR

    RETURN count
END FUNCTION
```

#### Go

```go
package main

import "fmt"

func countPairs(arr []int, target int) int {
    count := 0
    seen := make(map[int]bool)
    for _, num := range arr {
        complement := target - num
        if seen[complement] {
            count++
        }
        seen[num] = true
    }
    return count
}

func main() {
    fmt.Println(countPairs([]int{1, 5, 7, -1, 5}, 6)) // 3
}
```

#### Java

```java
import java.util.HashSet;

public class CountPairs {
    public static int countPairs(int[] arr, int target) {
        int count = 0;
        var seen = new HashSet<Integer>();
        for (int num : arr) {
            if (seen.contains(target - num)) count++;
            seen.add(num);
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(countPairs(new int[]{1, 5, 7, -1, 5}, 6)); // 3
    }
}
```

#### Python

```python
def count_pairs(arr, target):
    count = 0
    seen = set()
    for num in arr:
        if target - num in seen:
            count += 1
        seen.add(num)
    return count

print(count_pairs([1, 5, 7, -1, 5], 6))  # 3
```

---

## Coding Challenge 2: Write Pseudo Code First, Then Implement

> **Problem:** Given a string, find the first non-repeating character and return its index. Return -1 if none exists.

### Step 1: Pseudo Code

```text
FUNCTION firstUnique(string)
    SET charCount = empty hash map

    // First pass: count each character
    FOR each char IN string DO
        IF char IN charCount THEN
            SET charCount[char] = charCount[char] + 1
        ELSE
            SET charCount[char] = 1
        END IF
    END FOR

    // Second pass: find first with count 1
    FOR i = 0 TO length(string) - 1 DO
        IF charCount[string[i]] == 1 THEN
            RETURN i
        END IF
    END FOR

    RETURN -1
END FUNCTION

// Time: O(n) — two passes over string
// Space: O(k) — k = unique characters (at most 26 for lowercase)
```

### Step 2: Implementation

#### Go

```go
func firstUnique(s string) int {
    charCount := make(map[rune]int)
    for _, c := range s {
        charCount[c]++
    }
    for i, c := range s {
        if charCount[c] == 1 {
            return i
        }
    }
    return -1
}
```

#### Java

```java
public static int firstUnique(String s) {
    Map<Character, Integer> charCount = new HashMap<>();
    for (char c : s.toCharArray()) {
        charCount.merge(c, 1, Integer::sum);
    }
    for (int i = 0; i < s.length(); i++) {
        if (charCount.get(s.charAt(i)) == 1) return i;
    }
    return -1;
}
```

#### Python

```python
def first_unique(s):
    char_count = {}
    for c in s:
        char_count[c] = char_count.get(c, 0) + 1
    for i, c in enumerate(s):
        if char_count[c] == 1:
            return i
    return -1

# Or Pythonic:
from collections import Counter
def first_unique_v2(s):
    count = Counter(s)
    return next((i for i, c in enumerate(s) if count[c] == 1), -1)
```

---

## Interview Strategy: Using Pseudo Code

1. **Clarify the problem** — ask about edge cases, constraints, input size
2. **Write pseudo code FIRST** — 2-3 minutes, show your approach
3. **Walk through with an example** — trace with input `[1, 2, 3]`
4. **Analyze complexity** — state time and space Big-O
5. **Translate to real code** — the pseudo code guides you line by line
6. **Test edge cases** — empty input, single element, all duplicates

> This approach shows the interviewer your thought process, even if you don't finish the code.
