# Stack -- Interview Questions & Coding Challenges

## Table of Contents

1. [Valid Parentheses](#1-valid-parentheses)
2. [Min Stack](#2-min-stack)
3. [Daily Temperatures](#3-daily-temperatures)
4. [Evaluate Reverse Polish Notation](#4-evaluate-reverse-polish-notation)
5. [Decode String](#5-decode-string)
6. [Largest Rectangle in Histogram](#6-largest-rectangle-in-histogram)
7. [Trapping Rain Water (Stack Approach)](#7-trapping-rain-water-stack-approach)
8. [Tips for Stack Interviews](#tips-for-stack-interviews)

---

## 1. Valid Parentheses

**Problem:** Given a string containing `()[]{}`, determine if the input is valid. Every open bracket must be closed by the same type in the correct order.

**Complexity:** O(n) time, O(n) space.

### Go

```go
func isValid(s string) bool {
    stack := []byte{}
    match := map[byte]byte{')': '(', ']': '[', '}': '{'}

    for i := 0; i < len(s); i++ {
        c := s[i]
        if c == '(' || c == '[' || c == '{' {
            stack = append(stack, c)
        } else {
            if len(stack) == 0 || stack[len(stack)-1] != match[c] {
                return false
            }
            stack = stack[:len(stack)-1]
        }
    }
    return len(stack) == 0
}
```

### Java

```java
public boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> match = Map.of(')', '(', ']', '[', '}', '{');

    for (char c : s.toCharArray()) {
        if (c == '(' || c == '[' || c == '{') {
            stack.push(c);
        } else {
            if (stack.isEmpty() || stack.pop() != match.get(c)) {
                return false;
            }
        }
    }
    return stack.isEmpty();
}
```

### Python

```python
def is_valid(s: str) -> bool:
    stack = []
    match = {")": "(", "]": "[", "}": "{"}

    for c in s:
        if c in "([{":
            stack.append(c)
        else:
            if not stack or stack.pop() != match[c]:
                return False
    return len(stack) == 0
```

---

## 2. Min Stack

**Problem:** Design a stack that supports push, pop, top, and retrieving the minimum element, all in O(1).

### Go

```go
type MinStack struct {
    data []int
    mins []int
}

func Constructor() MinStack {
    return MinStack{}
}

func (s *MinStack) Push(val int) {
    s.data = append(s.data, val)
    if len(s.mins) == 0 || val <= s.mins[len(s.mins)-1] {
        s.mins = append(s.mins, val)
    } else {
        s.mins = append(s.mins, s.mins[len(s.mins)-1])
    }
}

func (s *MinStack) Pop() {
    s.data = s.data[:len(s.data)-1]
    s.mins = s.mins[:len(s.mins)-1]
}

func (s *MinStack) Top() int {
    return s.data[len(s.data)-1]
}

func (s *MinStack) GetMin() int {
    return s.mins[len(s.mins)-1]
}
```

### Java

```java
class MinStack {
    private Deque<Integer> data = new ArrayDeque<>();
    private Deque<Integer> mins = new ArrayDeque<>();

    public void push(int val) {
        data.push(val);
        mins.push(mins.isEmpty() ? val : Math.min(val, mins.peek()));
    }

    public void pop() {
        data.pop();
        mins.pop();
    }

    public int top() {
        return data.peek();
    }

    public int getMin() {
        return mins.peek();
    }
}
```

### Python

```python
class MinStack:
    def __init__(self):
        self.data = []
        self.mins = []

    def push(self, val: int) -> None:
        self.data.append(val)
        self.mins.append(min(val, self.mins[-1]) if self.mins else val)

    def pop(self) -> None:
        self.data.pop()
        self.mins.pop()

    def top(self) -> int:
        return self.data[-1]

    def get_min(self) -> int:
        return self.mins[-1]
```

---

## 3. Daily Temperatures

**Problem:** Given an array of temperatures, return an array where `result[i]` is the number of days until a warmer temperature. If no future day is warmer, put 0.

**Key technique:** Monotonic decreasing stack of indices.

### Go

```go
func dailyTemperatures(temps []int) []int {
    n := len(temps)
    result := make([]int, n)
    stack := []int{} // indices

    for i := 0; i < n; i++ {
        for len(stack) > 0 && temps[i] > temps[stack[len(stack)-1]] {
            j := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            result[j] = i - j
        }
        stack = append(stack, i)
    }
    return result
}
```

### Java

```java
public int[] dailyTemperatures(int[] temps) {
    int n = temps.length;
    int[] result = new int[n];
    Deque<Integer> stack = new ArrayDeque<>(); // indices

    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temps[i] > temps[stack.peek()]) {
            int j = stack.pop();
            result[j] = i - j;
        }
        stack.push(i);
    }
    return result;
}
```

### Python

```python
def daily_temperatures(temps: list[int]) -> list[int]:
    n = len(temps)
    result = [0] * n
    stack = []  # indices

    for i in range(n):
        while stack and temps[i] > temps[stack[-1]]:
            j = stack.pop()
            result[j] = i - j
        stack.append(i)
    return result
```

---

## 4. Evaluate Reverse Polish Notation

**Problem:** Evaluate an expression in Reverse Polish Notation (postfix). Valid operators are `+`, `-`, `*`, `/`. Division truncates toward zero.

### Go

```go
func evalRPN(tokens []string) int {
    stack := []int{}
    for _, tok := range tokens {
        switch tok {
        case "+", "-", "*", "/":
            b, a := stack[len(stack)-1], stack[len(stack)-2]
            stack = stack[:len(stack)-2]
            switch tok {
            case "+": stack = append(stack, a+b)
            case "-": stack = append(stack, a-b)
            case "*": stack = append(stack, a*b)
            case "/": stack = append(stack, a/b)
            }
        default:
            num, _ := strconv.Atoi(tok)
            stack = append(stack, num)
        }
    }
    return stack[0]
}
```

### Java

```java
public int evalRPN(String[] tokens) {
    Deque<Integer> stack = new ArrayDeque<>();
    for (String tok : tokens) {
        switch (tok) {
            case "+": { int b = stack.pop(), a = stack.pop(); stack.push(a + b); break; }
            case "-": { int b = stack.pop(), a = stack.pop(); stack.push(a - b); break; }
            case "*": { int b = stack.pop(), a = stack.pop(); stack.push(a * b); break; }
            case "/": { int b = stack.pop(), a = stack.pop(); stack.push(a / b); break; }
            default: stack.push(Integer.parseInt(tok));
        }
    }
    return stack.pop();
}
```

### Python

```python
def eval_rpn(tokens: list[str]) -> int:
    stack = []
    ops = {"+", "-", "*", "/"}
    for tok in tokens:
        if tok in ops:
            b, a = stack.pop(), stack.pop()
            if tok == "+": stack.append(a + b)
            elif tok == "-": stack.append(a - b)
            elif tok == "*": stack.append(a * b)
            else: stack.append(int(a / b))  # truncate toward zero
        else:
            stack.append(int(tok))
    return stack[0]
```

---

## 5. Decode String

**Problem:** Given an encoded string like `3[a2[c]]`, return the decoded string `accaccacc`.

### Go

```go
func decodeString(s string) string {
    countStack := []int{}
    strStack := []string{}
    current := ""
    k := 0

    for _, ch := range s {
        if ch >= '0' && ch <= '9' {
            k = k*10 + int(ch-'0')
        } else if ch == '[' {
            countStack = append(countStack, k)
            strStack = append(strStack, current)
            current = ""
            k = 0
        } else if ch == ']' {
            count := countStack[len(countStack)-1]
            countStack = countStack[:len(countStack)-1]
            prev := strStack[len(strStack)-1]
            strStack = strStack[:len(strStack)-1]
            current = prev + strings.Repeat(current, count)
        } else {
            current += string(ch)
        }
    }
    return current
}
```

### Java

```java
public String decodeString(String s) {
    Deque<Integer> countStack = new ArrayDeque<>();
    Deque<StringBuilder> strStack = new ArrayDeque<>();
    StringBuilder current = new StringBuilder();
    int k = 0;

    for (char ch : s.toCharArray()) {
        if (Character.isDigit(ch)) {
            k = k * 10 + (ch - '0');
        } else if (ch == '[') {
            countStack.push(k);
            strStack.push(current);
            current = new StringBuilder();
            k = 0;
        } else if (ch == ']') {
            int count = countStack.pop();
            StringBuilder prev = strStack.pop();
            prev.append(String.valueOf(current).repeat(count));
            current = prev;
        } else {
            current.append(ch);
        }
    }
    return current.toString();
}
```

### Python

```python
def decode_string(s: str) -> str:
    count_stack = []
    str_stack = []
    current = ""
    k = 0

    for ch in s:
        if ch.isdigit():
            k = k * 10 + int(ch)
        elif ch == "[":
            count_stack.append(k)
            str_stack.append(current)
            current = ""
            k = 0
        elif ch == "]":
            count = count_stack.pop()
            prev = str_stack.pop()
            current = prev + current * count
        else:
            current += ch
    return current
```

---

## 6. Largest Rectangle in Histogram

**Problem:** Given an array of bar heights, find the area of the largest rectangle that can be formed.

**Key technique:** Monotonic increasing stack. O(n) time.

### Python

```python
def largest_rectangle_area(heights: list[int]) -> int:
    stack = []  # indices of increasing heights
    max_area = 0
    heights.append(0)  # sentinel to flush the stack

    for i, h in enumerate(heights):
        while stack and heights[stack[-1]] > h:
            height = heights[stack.pop()]
            width = i if not stack else i - stack[-1] - 1
            max_area = max(max_area, height * width)
        stack.append(i)

    heights.pop()  # restore input
    return max_area
```

---

## 7. Trapping Rain Water (Stack Approach)

**Problem:** Given elevation map bars, compute how much water can be trapped.

### Python

```python
def trap(height: list[int]) -> int:
    stack = []  # indices
    water = 0

    for i, h in enumerate(height):
        while stack and h > height[stack[-1]]:
            bottom = height[stack.pop()]
            if not stack:
                break
            width = i - stack[-1] - 1
            bounded_height = min(h, height[stack[-1]]) - bottom
            water += width * bounded_height
        stack.append(i)
    return water
```

---

## Tips for Stack Interviews

1. **Recognize the pattern:** If the problem involves matching, nesting, or "next greater/smaller," think stack.

2. **Monotonic stack:** When you need the next greater or next smaller element, a monotonic stack gives O(n).

3. **Index vs value:** Decide whether to store indices or values on the stack. Indices give you positional information (distances, widths).

4. **Sentinel values:** Adding a dummy element (e.g., 0 height at the end of histogram) can simplify boundary handling.

5. **Two stacks:** Problems requiring O(1) access to both extremes (min, max) often use auxiliary stacks.

6. **Simulate recursion:** Any recursive DFS or backtracking can be rewritten with an explicit stack. Interviewers may ask you to do this.

7. **Edge cases to test:**
   - Empty input
   - Single element
   - All elements identical
   - Already sorted (ascending or descending)
   - Nested structures at maximum depth
