# Stack -- Practice Tasks

## Table of Contents

1. [Task 1: Basic Stack from Scratch](#task-1-basic-stack-from-scratch)
2. [Task 2: Reverse a String Using a Stack](#task-2-reverse-a-string-using-a-stack)
3. [Task 3: Balanced Parentheses Checker](#task-3-balanced-parentheses-checker)
4. [Task 4: Postfix Expression Evaluator](#task-4-postfix-expression-evaluator)
5. [Task 5: Infix to Postfix Converter](#task-5-infix-to-postfix-converter)
6. [Task 6: Min Stack](#task-6-min-stack)
7. [Task 7: Next Greater Element](#task-7-next-greater-element)
8. [Task 8: Daily Temperatures](#task-8-daily-temperatures)
9. [Task 9: Decode String](#task-9-decode-string)
10. [Task 10: Two Stacks as a Queue](#task-10-two-stacks-as-a-queue)
11. [Task 11: Sort a Stack Using Another Stack](#task-11-sort-a-stack-using-another-stack)
12. [Task 12: Largest Rectangle in Histogram](#task-12-largest-rectangle-in-histogram)
13. [Task 13: Simplify Unix Path](#task-13-simplify-unix-path)
14. [Task 14: Asteroid Collision](#task-14-asteroid-collision)
15. [Task 15: Browser History with Undo/Redo](#task-15-browser-history-with-undoredo)
16. [Benchmark Task: Stack Performance Comparison](#benchmark-task-stack-performance-comparison)

---

## Task 1: Basic Stack from Scratch

**Difficulty:** Easy

**Description:** Implement a generic stack from scratch supporting `push`, `pop`, `peek`, `isEmpty`, and `size`. Implement both array-based and linked-list-based versions.

**Requirements:**
- Do NOT use any built-in stack or deque class.
- Handle underflow (pop/peek on empty stack) gracefully with an error.
- Write tests that verify LIFO ordering.

**Test cases:**
```
push(1), push(2), push(3)
pop() -> 3
peek() -> 2
size() -> 2
pop() -> 2
pop() -> 1
isEmpty() -> true
pop() -> error
```

**Expected output:** All operations pass. Both implementations produce identical results.

---

## Task 2: Reverse a String Using a Stack

**Difficulty:** Easy

**Description:** Given a string, reverse it by pushing every character onto a stack and then popping all characters.

**Input:** `"hello world"`
**Expected output:** `"dlrow olleh"`

**Additional tests:**
```
"" -> ""
"a" -> "a"
"abcba" -> "abcba"  (palindrome)
"12345" -> "54321"
```

---

## Task 3: Balanced Parentheses Checker

**Difficulty:** Easy

**Description:** Given a string containing `()`, `[]`, `{}`, determine if the brackets are balanced. Ignore non-bracket characters.

**Test cases:**
```
"()" -> true
"()[]{}" -> true
"(]" -> false
"([)]" -> false
"{[]}" -> true
"" -> true
"(((" -> false
"a(b[c]{d}e)f" -> true
```

---

## Task 4: Postfix Expression Evaluator

**Difficulty:** Medium

**Description:** Evaluate a mathematical expression given in Reverse Polish Notation (postfix). Support `+`, `-`, `*`, `/` (integer division truncating toward zero).

**Test cases:**
```
["2", "1", "+", "3", "*"] -> 9          // (2+1)*3
["4", "13", "5", "/", "+"] -> 6         // 4+(13/5)
["10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"] -> 22
```

---

## Task 5: Infix to Postfix Converter

**Difficulty:** Medium

**Description:** Convert an infix expression (with `+`, `-`, `*`, `/`, `^`, and parentheses) to postfix using the Shunting-Yard algorithm.

**Test cases:**
```
["3", "+", "4", "*", "2"] -> ["3", "4", "2", "*", "+"]
["(", "3", "+", "4", ")", "*", "2"] -> ["3", "4", "+", "2", "*"]
["2", "^", "3", "^", "2"] -> ["2", "3", "2", "^", "^"]  (right-associative)
```

**Bonus:** Chain both tasks -- convert infix to postfix, then evaluate the postfix. Verify that the result matches direct computation.

---

## Task 6: Min Stack

**Difficulty:** Medium

**Description:** Design a stack that supports `push`, `pop`, `top`, and `getMin`, all in O(1) time.

**Test cases:**
```
push(-2), push(0), push(-3)
getMin() -> -3
pop()
top() -> 0
getMin() -> -2
```

**Bonus:** Implement the space-optimized version where the min stack only stores values when the minimum changes.

---

## Task 7: Next Greater Element

**Difficulty:** Medium

**Description:** Given an array, for each element find the next greater element to its right. If none exists, use -1.

**Test cases:**
```
[4, 5, 2, 25] -> [5, 25, 25, -1]
[13, 7, 6, 12] -> [-1, 12, 12, -1]
[1, 2, 3, 4] -> [2, 3, 4, -1]
[4, 3, 2, 1] -> [-1, -1, -1, -1]
```

**Requirement:** Solution must be O(n), not O(n^2).

---

## Task 8: Daily Temperatures

**Difficulty:** Medium

**Description:** Given an array of daily temperatures, return an array where `result[i]` is the number of days until a warmer day. If no warmer day exists, put 0.

**Test cases:**
```
[73, 74, 75, 71, 69, 72, 76, 73] -> [1, 1, 4, 2, 1, 1, 0, 0]
[30, 40, 50, 60] -> [1, 1, 1, 0]
[30, 20, 10] -> [0, 0, 0]
```

---

## Task 9: Decode String

**Difficulty:** Medium

**Description:** Given an encoded string like `k[encoded_string]`, return the decoded string. The encoded string is repeated `k` times. Nesting is allowed.

**Test cases:**
```
"3[a]2[bc]" -> "aaabcbc"
"3[a2[c]]" -> "accaccacc"
"2[abc]3[cd]ef" -> "abcabccdcdcdef"
"10[a]" -> "aaaaaaaaaa"
```

---

## Task 10: Two Stacks as a Queue

**Difficulty:** Medium

**Description:** Implement a FIFO queue using only two stacks. Support `enqueue`, `dequeue`, `peek`, and `isEmpty`.

**Test cases:**
```
enqueue(1), enqueue(2), enqueue(3)
dequeue() -> 1
peek() -> 2
enqueue(4)
dequeue() -> 2
dequeue() -> 3
dequeue() -> 4
isEmpty() -> true
```

**Requirement:** Prove that amortized time per operation is O(1).

---

## Task 11: Sort a Stack Using Another Stack

**Difficulty:** Medium

**Description:** Sort a stack so that the smallest element is on top, using only one additional temporary stack. No other data structures allowed.

**Algorithm:**
1. Pop an element from the input stack.
2. While the temp stack's top is greater than this element, pop from temp and push to input.
3. Push the element onto temp.
4. Repeat until input is empty.

**Test cases:**
```
Input (top to bottom):  [5, 1, 4, 2, 3]
Output (top to bottom): [1, 2, 3, 4, 5]
```

**Complexity:** O(n^2) time, O(n) space.

---

## Task 12: Largest Rectangle in Histogram

**Difficulty:** Hard

**Description:** Given an array of bar heights representing a histogram, find the area of the largest rectangle.

**Test cases:**
```
[2, 1, 5, 6, 2, 3] -> 10  (bars at index 2-3, height 5, width 2)
[2, 4] -> 4
[1] -> 1
[6, 2, 5, 4, 5, 1, 6] -> 12
```

**Requirement:** O(n) solution using a monotonic stack.

---

## Task 13: Simplify Unix Path

**Difficulty:** Medium

**Description:** Given an absolute Unix file path, simplify it (resolve `.`, `..`, multiple slashes).

**Test cases:**
```
"/home/" -> "/home"
"/../" -> "/"
"/home//foo/" -> "/home/foo"
"/a/./b/../../c/" -> "/c"
"/a/b/c/../.." -> "/a"
```

---

## Task 14: Asteroid Collision

**Difficulty:** Medium

**Description:** Given an array of integers representing asteroids in a row, positive means moving right, negative means moving left. When two asteroids meet, the smaller one explodes. If equal size, both explode. Find the state after all collisions.

**Test cases:**
```
[5, 10, -5] -> [5, 10]
[8, -8] -> []
[10, 2, -5] -> [10]
[-2, -1, 1, 2] -> [-2, -1, 1, 2]
```

---

## Task 15: Browser History with Undo/Redo

**Difficulty:** Medium-Hard

**Description:** Implement a browser history system with:
- `visit(url)`: Visit a new URL (clears forward history).
- `back()`: Go back one page (return the URL).
- `forward()`: Go forward one page (return the URL).
- `current()`: Return the current URL.

**Test cases:**
```
visit("google.com")
visit("youtube.com")
visit("github.com")
current() -> "github.com"
back() -> "youtube.com"
back() -> "google.com"
forward() -> "youtube.com"
visit("leetcode.com")   // clears forward history
forward() -> "leetcode.com"  // no forward available, stay
current() -> "leetcode.com"
```

---

## Benchmark Task: Stack Performance Comparison

**Difficulty:** Medium

**Description:** Compare the performance of array-based vs linked-list-based stacks for different workloads.

### Benchmarks to Run

1. **Sequential push**: Push N elements (N = 10K, 100K, 1M, 10M). Measure time.
2. **Sequential push+pop**: Push N elements, then pop all. Measure time.
3. **Alternating push/pop**: Alternate push and pop for 2N operations. Measure time.
4. **Memory usage**: Measure peak memory for N = 1M elements.

### Expected Observations

| Metric            | Array-Based             | Linked-List-Based         |
| ----------------- | ----------------------- | ------------------------- |
| Sequential push   | Faster (cache-friendly) | Slower (allocations)      |
| Push+pop          | Faster                  | Comparable                |
| Alternating       | Fastest                 | Slightly slower           |
| Memory per element | 8 bytes (int64)        | 8 + 8 + overhead (node)  |

### Go Benchmark Template

```go
package stack

import "testing"

func BenchmarkArrayStackPush(b *testing.B) {
    s := NewArrayStack()
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        s.Push(i)
    }
}

func BenchmarkLinkedStackPush(b *testing.B) {
    s := NewLinkedStack()
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        s.Push(i)
    }
}

func BenchmarkArrayStackPushPop(b *testing.B) {
    s := NewArrayStack()
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        s.Push(i)
    }
    for i := 0; i < b.N; i++ {
        s.Pop()
    }
}

func BenchmarkLinkedStackPushPop(b *testing.B) {
    s := NewLinkedStack()
    b.ResetTimer()
    for i := 0; i < b.N; i++ {
        s.Push(i)
    }
    for i := 0; i < b.N; i++ {
        s.Pop()
    }
}
```

### Java Benchmark Template

```java
public class StackBenchmark {
    public static void main(String[] args) {
        int[] sizes = {10_000, 100_000, 1_000_000, 10_000_000};

        for (int n : sizes) {
            // Array-based
            long start = System.nanoTime();
            ArrayStack arrStack = new ArrayStack(16);
            for (int i = 0; i < n; i++) arrStack.push(i);
            long arrayTime = System.nanoTime() - start;

            // Linked-list-based
            start = System.nanoTime();
            LinkedStack linkStack = new LinkedStack();
            for (int i = 0; i < n; i++) linkStack.push(i);
            long linkedTime = System.nanoTime() - start;

            System.out.printf("N=%,d | Array: %.2f ms | Linked: %.2f ms%n",
                n, arrayTime / 1e6, linkedTime / 1e6);
        }
    }
}
```

### Python Benchmark Template

```python
import time

def benchmark_push(stack_class, n):
    s = stack_class()
    start = time.perf_counter()
    for i in range(n):
        s.push(i)
    return time.perf_counter() - start

def benchmark_push_pop(stack_class, n):
    s = stack_class()
    start = time.perf_counter()
    for i in range(n):
        s.push(i)
    for _ in range(n):
        s.pop()
    return time.perf_counter() - start

if __name__ == "__main__":
    for n in [10_000, 100_000, 1_000_000]:
        arr_time = benchmark_push(ArrayStack, n)
        link_time = benchmark_push(LinkedStack, n)
        print(f"N={n:>10,} | ArrayStack: {arr_time:.4f}s | LinkedStack: {link_time:.4f}s")
```

### What to Report

For each benchmark, report:
1. Execution time in milliseconds.
2. Operations per second.
3. Memory usage (if measurable).
4. A brief explanation of why one is faster than the other for that workload.
