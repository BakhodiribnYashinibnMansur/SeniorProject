# Control Structures — Interview Preparation

## Junior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What are the 3 basic control structures? | Sequence, selection (if/else), iteration (loop) |
| 2 | What's the difference between `while` and `for`? | `for` = known iterations; `while` = condition-based |
| 3 | What does `break` vs `continue` do? | `break` exits loop; `continue` skips to next iteration |
| 4 | Does Go have a `while` keyword? | No — `for` serves all loop purposes in Go |
| 5 | What is short-circuit evaluation? | `&&` stops on false; `\|\|` stops on true |
| 6 | What happens if you forget `break` in Java's `switch`? | Falls through to next case |

## Middle Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | What is a guard clause? Why use it? | Early return to flatten nesting; improves readability |
| 2 | How does loop structure affect Big-O? | Single=O(n), nested=O(n²), halving=O(log n) |
| 3 | Write 3 versions of FizzBuzz (if/else, switch, map). | Show different control flow approaches |
| 4 | Explain labeled break. Which languages support it? | Go and Java support it; Python uses functions instead |
| 5 | What is the EAFP principle in Python? | Try/except instead of checking conditions first |

## Senior Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | Explain Go's `select` statement for channels. | Multiplexed wait on multiple channels + timeout |
| 2 | Design a state machine for a TCP connection. | States: LISTEN, SYN_SENT, ESTABLISHED, etc. |
| 3 | What is backpressure? How do you implement it? | Bounded buffer/channel; producer blocks when full |
| 4 | Explain the circuit breaker pattern. | Open/closed/half-open states; failure threshold |

## Professional Questions

| # | Question | Expected Answer Focus |
|---|----------|-----------------------|
| 1 | State and prove the Bohm-Jacopini theorem. | Sequence + selection + iteration = Turing complete |
| 2 | Why is the halting problem undecidable? | Diagonal argument; no universal loop detector |
| 3 | How does branch prediction affect algorithm performance? | Sorted data → predictable branches → faster |
| 4 | What is cyclomatic complexity? | M = E - V + 2P; measures code complexity |

---

## Coding Challenge 1: Nested Loop Optimization

> Given an array, find if there exist two elements that sum to zero. First write O(n²), then optimize to O(n).

#### Go

```go
// O(n²)
func hasPairSumZeroBrute(arr []int) bool {
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i]+arr[j] == 0 {
                return true
            }
        }
    }
    return false
}

// O(n)
func hasPairSumZero(arr []int) bool {
    seen := make(map[int]bool)
    for _, v := range arr {
        if seen[-v] {
            return true
        }
        seen[v] = true
    }
    return false
}
```

#### Java

```java
// O(n)
public static boolean hasPairSumZero(int[] arr) {
    Set<Integer> seen = new HashSet<>();
    for (int v : arr) {
        if (seen.contains(-v)) return true;
        seen.add(v);
    }
    return false;
}
```

#### Python

```python
# O(n)
def has_pair_sum_zero(arr):
    seen = set()
    for v in arr:
        if -v in seen:
            return True
        seen.add(v)
    return False
```

---

## Coding Challenge 2: Flatten Nested Loops with Early Exit

> Given a 2D matrix, find the first negative number. Return its position or (-1, -1).

#### Go

```go
func findFirstNegative(matrix [][]int) (int, int) {
    for i, row := range matrix {
        for j, val := range row {
            if val < 0 {
                return i, j
            }
        }
    }
    return -1, -1
}
```

#### Java

```java
public static int[] findFirstNegative(int[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[i].length; j++) {
            if (matrix[i][j] < 0) return new int[]{i, j};
        }
    }
    return new int[]{-1, -1};
}
```

#### Python

```python
def find_first_negative(matrix):
    for i, row in enumerate(matrix):
        for j, val in enumerate(row):
            if val < 0:
                return (i, j)
    return (-1, -1)
```
