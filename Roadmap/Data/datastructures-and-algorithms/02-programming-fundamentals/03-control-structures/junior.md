# Control Structures — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Glossary](#glossary)
4. [Core Concepts](#core-concepts)
5. [Conditional Statements](#conditional-statements)
6. [Loops](#loops)
7. [Switch/Match Statements](#switchmatch-statements)
8. [Code Examples](#code-examples)
9. [Best Practices](#best-practices)
10. [Common Mistakes](#common-mistakes)
11. [Cheat Sheet](#cheat-sheet)
12. [Summary](#summary)
13. [Further Reading](#further-reading)

---

## Introduction

> Focus: "What are control structures?" and "How to use them?"

Control structures determine the **order** in which statements are executed. Without them, code runs top to bottom — every line, every time. Control structures let you **skip** lines (conditionals), **repeat** lines (loops), and **choose** between paths (switch/match).

Every algorithm is built from just three building blocks:
1. **Sequence** — execute one statement after another
2. **Selection** — choose which statements to execute (if/else)
3. **Iteration** — repeat statements (for/while)

---

## Prerequisites

- **Required:** Variables and data types in Go/Java/Python
- **Required:** Boolean expressions (`true`/`false`, `==`, `!=`, `<`, `>`)
- **Helpful:** Basic understanding of pseudo code

---

## Glossary

| Term | Definition |
|------|-----------|
| **Conditional** | A statement that executes code only if a condition is true |
| **Loop** | A statement that repeats code while a condition is true |
| **Boolean Expression** | An expression that evaluates to `true` or `false` |
| **Iteration** | One complete execution of a loop body |
| **Infinite Loop** | A loop that never terminates (usually a bug) |
| **Break** | A statement that exits the current loop immediately |
| **Continue** | A statement that skips the rest of the current iteration |
| **Short-circuit** | Evaluation stops as soon as the result is determined |

---

## Core Concepts

### Concept 1: Boolean Expressions

Every control structure depends on a boolean expression — something that is either `true` or `false`.

#### Go

```go
x := 10
fmt.Println(x > 5)      // true
fmt.Println(x == 10)     // true
fmt.Println(x > 5 && x < 20) // true (AND)
fmt.Println(x > 5 || x < 3)  // true (OR)
fmt.Println(!(x > 5))        // false (NOT)
```

#### Java

```java
int x = 10;
System.out.println(x > 5);          // true
System.out.println(x == 10);        // true
System.out.println(x > 5 && x < 20); // true
System.out.println(x > 5 || x < 3);  // true
System.out.println(!(x > 5));        // false
```

#### Python

```python
x = 10
print(x > 5)           # True
print(x == 10)          # True
print(x > 5 and x < 20) # True
print(x > 5 or x < 3)   # True
print(not (x > 5))       # False
```

### Concept 2: Truthy and Falsy

Python treats some values as "falsy" even though they're not literally `False`:

```python
# Falsy values in Python:
if not 0:       print("0 is falsy")
if not "":      print("empty string is falsy")
if not []:      print("empty list is falsy")
if not None:    print("None is falsy")
if not {}:      print("empty dict is falsy")

# Everything else is truthy
if 42:          print("non-zero is truthy")
if "hello":     print("non-empty string is truthy")
if [1, 2]:      print("non-empty list is truthy")
```

Go and Java don't have truthy/falsy — conditions must be explicit booleans:

```go
// Go: if x {} — COMPILE ERROR (x is int, not bool)
if x != 0 { } // Correct
```

```java
// Java: if (x) {} — COMPILE ERROR (x is int, not boolean)
if (x != 0) { } // Correct
```

---

## Conditional Statements

### if / else if / else

#### Go

```go
score := 85

if score >= 90 {
    fmt.Println("A")
} else if score >= 80 {
    fmt.Println("B")
} else if score >= 70 {
    fmt.Println("C")
} else {
    fmt.Println("F")
}
// Output: B
```

#### Java

```java
int score = 85;

if (score >= 90) {
    System.out.println("A");
} else if (score >= 80) {
    System.out.println("B");
} else if (score >= 70) {
    System.out.println("C");
} else {
    System.out.println("F");
}
```

#### Python

```python
score = 85

if score >= 90:
    print("A")
elif score >= 80:
    print("B")
elif score >= 70:
    print("C")
else:
    print("F")
```

### Ternary / Conditional Expression

#### Go

```go
// Go has NO ternary operator
// Use if-else instead
var result string
if x > 0 {
    result = "positive"
} else {
    result = "non-positive"
}
```

#### Java

```java
String result = (x > 0) ? "positive" : "non-positive";
```

#### Python

```python
result = "positive" if x > 0 else "non-positive"
```

---

## Loops

### for Loop

#### Go

```go
// Classic for loop
for i := 0; i < 5; i++ {
    fmt.Println(i) // 0 1 2 3 4
}

// Range-based (for-each)
nums := []int{10, 20, 30}
for index, value := range nums {
    fmt.Printf("index=%d value=%d\n", index, value)
}

// While-style loop (Go only has 'for')
n := 10
for n > 0 {
    fmt.Println(n)
    n--
}

// Infinite loop
for {
    // runs forever until break
    break
}
```

#### Java

```java
// Classic for loop
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}

// Enhanced for (for-each)
int[] nums = {10, 20, 30};
for (int value : nums) {
    System.out.println(value);
}

// While loop
int n = 10;
while (n > 0) {
    System.out.println(n);
    n--;
}

// Do-while (runs at least once)
do {
    System.out.println("runs once");
} while (false);
```

#### Python

```python
# For loop (over range)
for i in range(5):
    print(i)  # 0 1 2 3 4

# For-each
nums = [10, 20, 30]
for value in nums:
    print(value)

# With index
for index, value in enumerate(nums):
    print(f"index={index} value={value}")

# While loop
n = 10
while n > 0:
    print(n)
    n -= 1

# Python has no do-while
```

### break and continue

#### Go

```go
// break — exit loop
for i := 0; i < 10; i++ {
    if i == 5 {
        break // stops at 5
    }
    fmt.Println(i) // 0 1 2 3 4
}

// continue — skip iteration
for i := 0; i < 10; i++ {
    if i%2 == 0 {
        continue // skip even numbers
    }
    fmt.Println(i) // 1 3 5 7 9
}
```

#### Java

```java
for (int i = 0; i < 10; i++) {
    if (i == 5) break;
    System.out.println(i);
}

for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue;
    System.out.println(i);
}
```

#### Python

```python
for i in range(10):
    if i == 5:
        break
    print(i)

for i in range(10):
    if i % 2 == 0:
        continue
    print(i)
```

### Nested Loops

#### Go

```go
for i := 1; i <= 3; i++ {
    for j := 1; j <= 3; j++ {
        fmt.Printf("%d×%d=%d  ", i, j, i*j)
    }
    fmt.Println()
}
// 1×1=1  1×2=2  1×3=3
// 2×1=2  2×2=4  2×3=6
// 3×1=3  3×2=6  3×3=9
```

#### Java

```java
for (int i = 1; i <= 3; i++) {
    for (int j = 1; j <= 3; j++) {
        System.out.printf("%d×%d=%d  ", i, j, i * j);
    }
    System.out.println();
}
```

#### Python

```python
for i in range(1, 4):
    for j in range(1, 4):
        print(f"{i}×{j}={i*j}", end="  ")
    print()
```

---

## Switch/Match Statements

#### Go

```go
day := "Tuesday"
switch day {
case "Monday":
    fmt.Println("Start of week")
case "Tuesday", "Wednesday", "Thursday":
    fmt.Println("Midweek")
case "Friday":
    fmt.Println("TGIF!")
default:
    fmt.Println("Weekend")
}
// Go: no fallthrough by default (opposite of Java/C)
```

#### Java

```java
String day = "Tuesday";

// Classic switch (needs break!)
switch (day) {
    case "Monday":
        System.out.println("Start of week");
        break;
    case "Tuesday":
    case "Wednesday":
    case "Thursday":
        System.out.println("Midweek");
        break;
    case "Friday":
        System.out.println("TGIF!");
        break;
    default:
        System.out.println("Weekend");
}

// Switch expression (Java 14+, no break needed)
String msg = switch (day) {
    case "Monday" -> "Start of week";
    case "Tuesday", "Wednesday", "Thursday" -> "Midweek";
    case "Friday" -> "TGIF!";
    default -> "Weekend";
};
```

#### Python

```python
day = "Tuesday"

# match/case (Python 3.10+)
match day:
    case "Monday":
        print("Start of week")
    case "Tuesday" | "Wednesday" | "Thursday":
        print("Midweek")
    case "Friday":
        print("TGIF!")
    case _:
        print("Weekend")

# Before 3.10: use if/elif/else or dict dispatch
messages = {
    "Monday": "Start of week",
    "Friday": "TGIF!",
}
print(messages.get(day, "Midweek"))
```

---

## Code Examples

### Example 1: FizzBuzz

#### Go

```go
for i := 1; i <= 100; i++ {
    switch {
    case i%15 == 0:
        fmt.Println("FizzBuzz")
    case i%3 == 0:
        fmt.Println("Fizz")
    case i%5 == 0:
        fmt.Println("Buzz")
    default:
        fmt.Println(i)
    }
}
```

#### Java

```java
for (int i = 1; i <= 100; i++) {
    if (i % 15 == 0) System.out.println("FizzBuzz");
    else if (i % 3 == 0) System.out.println("Fizz");
    else if (i % 5 == 0) System.out.println("Buzz");
    else System.out.println(i);
}
```

#### Python

```python
for i in range(1, 101):
    if i % 15 == 0:
        print("FizzBuzz")
    elif i % 3 == 0:
        print("Fizz")
    elif i % 5 == 0:
        print("Buzz")
    else:
        print(i)
```

---

### Example 2: Find All Pairs that Sum to Target

#### Go

```go
func findPairs(arr []int, target int) [][2]int {
    var pairs [][2]int
    for i := 0; i < len(arr); i++ {
        for j := i + 1; j < len(arr); j++ {
            if arr[i]+arr[j] == target {
                pairs = append(pairs, [2]int{arr[i], arr[j]})
            }
        }
    }
    return pairs
}
```

#### Java

```java
public static List<int[]> findPairs(int[] arr, int target) {
    List<int[]> pairs = new ArrayList<>();
    for (int i = 0; i < arr.length; i++) {
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[i] + arr[j] == target) {
                pairs.add(new int[]{arr[i], arr[j]});
            }
        }
    }
    return pairs;
}
```

#### Python

```python
def find_pairs(arr, target):
    pairs = []
    for i in range(len(arr)):
        for j in range(i + 1, len(arr)):
            if arr[i] + arr[j] == target:
                pairs.append((arr[i], arr[j]))
    return pairs
```

---

## Best Practices

1. **Keep conditions simple:** Extract complex conditions into boolean variables
   ```go
   isAdult := age >= 18
   hasPermission := role == "admin"
   if isAdult && hasPermission { ... }
   ```
2. **Avoid deep nesting:** Use early return/continue to flatten code
3. **Prefer `for-each` over index loops** when you don't need the index
4. **Always have an exit condition** in while loops to avoid infinite loops
5. **Use `switch`/`match`** instead of long `if-else` chains for multiple values

---

## Common Mistakes

| Mistake | Language | Example | Fix |
|---------|----------|---------|-----|
| `=` instead of `==` | Java/Python | `if (x = 5)` | `if (x == 5)` |
| Missing `break` in switch | Java | Falls through all cases | Add `break` or use `->` syntax |
| Off-by-one in loop | All | `for i = 0 to n` | `for i = 0 to n-1` |
| Infinite loop | All | Forgetting to update loop variable | Ensure condition will eventually be false |
| Modifying collection in loop | All | Remove elements while iterating | Use separate filtered list |
| `!` vs `not` | Go/Java vs Python | `!condition` vs `not condition` | Know your language |

---

## Cheat Sheet

### Conditionals

| | Go | Java | Python |
|---|-----|------|--------|
| if | `if x > 0 {` | `if (x > 0) {` | `if x > 0:` |
| else if | `} else if {` | `} else if {` | `elif` |
| else | `} else {` | `} else {` | `else:` |
| ternary | N/A | `x > 0 ? "a" : "b"` | `"a" if x > 0 else "b"` |
| AND | `&&` | `&&` | `and` |
| OR | `\|\|` | `\|\|` | `or` |
| NOT | `!` | `!` | `not` |

### Loops

| | Go | Java | Python |
|---|-----|------|--------|
| for | `for i := 0; i < n; i++ {` | `for (int i=0; i<n; i++) {` | `for i in range(n):` |
| for-each | `for _, v := range arr {` | `for (int v : arr) {` | `for v in arr:` |
| while | `for x > 0 {` | `while (x > 0) {` | `while x > 0:` |
| do-while | N/A | `do {...} while(cond);` | N/A |
| infinite | `for {` | `while (true) {` | `while True:` |
| break | `break` | `break;` | `break` |
| continue | `continue` | `continue;` | `continue` |

---

## Summary

Control structures are the backbone of every algorithm. Master `if/else` for decisions, `for/while` for repetition, and `switch/match` for multi-way branching. The logic is the same across Go, Java, and Python — only the syntax differs.

---

## Further Reading

- **Go:** [Effective Go — Control Structures](https://go.dev/doc/effective_go#control-structures)
- **Java:** [Oracle — Control Flow Statements](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/flow.html)
- **Python:** [Python Tutorial — Control Flow](https://docs.python.org/3/tutorial/controlflow.html)
