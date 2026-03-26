# Pseudo Code — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Glossary](#glossary)
4. [Core Concepts](#core-concepts)
5. [Real-World Analogies](#real-world-analogies)
6. [Pros & Cons](#pros--cons)
7. [Pseudo Code Syntax Rules](#pseudo-code-syntax-rules)
8. [Code Examples](#code-examples)
9. [Common Patterns](#common-patterns)
10. [Best Practices](#best-practices)
11. [Common Mistakes](#common-mistakes)
12. [Cheat Sheet](#cheat-sheet)
13. [Summary](#summary)
14. [Further Reading](#further-reading)

---

## Introduction

> Focus: "What is pseudo code?" and "How to write it?"

Pseudo code is a way of describing an algorithm using plain language mixed with programming-like structure. It's not a real programming language — no compiler or interpreter runs it. Instead, it's a tool for **thinking** and **communicating** algorithms before writing actual code.

Think of pseudo code as a blueprint: an architect draws a blueprint before building a house. Similarly, a programmer writes pseudo code before writing actual code. It helps you focus on the **logic** without worrying about syntax details.

---

## Prerequisites

- **Required:** Understanding of basic programming concepts (variables, loops, conditions)
- **Helpful:** Experience writing code in any language
- **Helpful:** Basic math knowledge (arithmetic, comparisons)

---

## Glossary

| Term | Definition |
|------|-----------|
| **Pseudo Code** | Informal description of an algorithm using structured, human-readable text |
| **Algorithm** | A step-by-step procedure for solving a problem |
| **Flowchart** | A visual diagram representation of an algorithm using shapes and arrows |
| **Abstraction** | Hiding implementation details to focus on the high-level logic |
| **Control Flow** | The order in which instructions are executed (sequential, conditional, loop) |
| **Indentation** | Using spaces/tabs to show the structure and nesting of code blocks |

---

## Core Concepts

### Concept 1: Why Pseudo Code Exists

Real programming languages have strict syntax rules. When you're designing an algorithm, you don't want to be distracted by semicolons, brackets, or type declarations. Pseudo code lets you express the core idea in a language anyone can understand — regardless of whether they know Go, Java, or Python.

### Concept 2: Pseudo Code is Language-Independent

A well-written pseudo code can be translated into any programming language. It captures the **what** (logic) without the **how** (language-specific syntax). This makes it invaluable for interviews, textbooks, and team discussions.

### Concept 3: Structure Matters

Even though pseudo code is informal, it should follow a consistent structure: clear variable names, proper indentation for nesting, and explicit control flow keywords (IF, WHILE, FOR, RETURN).

---

## Real-World Analogies

| Concept | Analogy |
|---------|--------|
| **Pseudo Code** | A recipe written in plain language — "mix flour and eggs" instead of exact measurements |
| **Algorithm** | Step-by-step directions to a destination — "turn left, go straight, turn right" |
| **Translating to Code** | Translating a recipe from English to Japanese — same dish, different language |

> **Where the analogy breaks down:** Recipes tolerate ambiguity ("a pinch of salt"), but algorithms must be precise. Pseudo code should be detailed enough that any programmer can translate it without guessing.

---

## Pros & Cons

| Pros | Cons |
|------|------|
| Language-independent — anyone can read it | Cannot be executed or tested |
| Focuses on logic, not syntax | No standard format — varies by author |
| Great for planning and communication | May miss edge cases that real code forces you to handle |
| Used universally in textbooks and interviews | Can become ambiguous if not written carefully |

**When to use:** Planning algorithms, interviews, teaching, documenting algorithms in papers.
**When NOT to use:** Actual implementation — write real code instead.

---

## Pseudo Code Syntax Rules

There's no universal standard, but these conventions are widely used:

### Keywords (UPPERCASE)

| Keyword | Meaning | Example |
|---------|---------|---------|
| `SET` / `LET` | Assign a value | `SET x = 5` |
| `IF ... THEN ... ELSE` | Conditional | `IF x > 0 THEN print "positive"` |
| `WHILE ... DO` | While loop | `WHILE x > 0 DO x = x - 1` |
| `FOR ... TO ... DO` | For loop | `FOR i = 1 TO n DO` |
| `RETURN` | Return a value | `RETURN result` |
| `PRINT` / `OUTPUT` | Display output | `PRINT "Hello"` |
| `INPUT` / `READ` | Read user input | `INPUT name` |
| `FUNCTION` / `PROCEDURE` | Define a function | `FUNCTION add(a, b)` |
| `CALL` | Call a function | `CALL sort(array)` |
| `END` | End a block | `END IF`, `END WHILE` |

### Indentation

Use indentation (2-4 spaces) to show nesting:

```text
IF condition THEN
    do something
    IF nested condition THEN
        do nested thing
    END IF
END IF
```

---

## Code Examples

### Example 1: Find Maximum in Array

#### Pseudo Code

```text
FUNCTION findMax(array)
    SET max = array[0]
    FOR i = 1 TO length(array) - 1 DO
        IF array[i] > max THEN
            SET max = array[i]
        END IF
    END FOR
    RETURN max
END FUNCTION
```

#### Go

```go
package main

import "fmt"

func findMax(arr []int) int {
    max := arr[0]
    for i := 1; i < len(arr); i++ {
        if arr[i] > max {
            max = arr[i]
        }
    }
    return max
}

func main() {
    fmt.Println(findMax([]int{3, 7, 2, 9, 1})) // 9
}
```

#### Java

```java
public class FindMax {
    public static int findMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(findMax(new int[]{3, 7, 2, 9, 1})); // 9
    }
}
```

#### Python

```python
def find_max(arr):
    max_val = arr[0]
    for i in range(1, len(arr)):
        if arr[i] > max_val:
            max_val = arr[i]
    return max_val

print(find_max([3, 7, 2, 9, 1]))  # 9
```

**Observation:** The pseudo code translates almost directly to all 3 languages. The logic is identical — only syntax changes.

---

### Example 2: Linear Search

#### Pseudo Code

```text
FUNCTION linearSearch(array, target)
    FOR i = 0 TO length(array) - 1 DO
        IF array[i] == target THEN
            RETURN i
        END IF
    END FOR
    RETURN -1     // not found
END FUNCTION
```

#### Go

```go
func linearSearch(arr []int, target int) int {
    for i := 0; i < len(arr); i++ {
        if arr[i] == target {
            return i
        }
    }
    return -1
}
```

#### Java

```java
public static int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) return i;
    }
    return -1;
}
```

#### Python

```python
def linear_search(arr, target):
    for i in range(len(arr)):
        if arr[i] == target:
            return i
    return -1
```

---

### Example 3: Swap Two Variables

#### Pseudo Code

```text
FUNCTION swap(a, b)
    SET temp = a
    SET a = b
    SET b = temp
    RETURN a, b
END FUNCTION
```

#### Go

```go
func swap(a, b int) (int, int) {
    return b, a  // Go supports multiple return
}
```

#### Java

```java
// Java needs an array or object for swap
public static void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}
```

#### Python

```python
def swap(a, b):
    return b, a  # Python supports tuple unpacking
```

---

### Example 4: Check if Number is Prime

#### Pseudo Code

```text
FUNCTION isPrime(n)
    IF n <= 1 THEN
        RETURN false
    END IF
    FOR i = 2 TO sqrt(n) DO
        IF n MOD i == 0 THEN
            RETURN false
        END IF
    END FOR
    RETURN true
END FUNCTION
```

#### Go

```go
import "math"

func isPrime(n int) bool {
    if n <= 1 {
        return false
    }
    for i := 2; i <= int(math.Sqrt(float64(n))); i++ {
        if n%i == 0 {
            return false
        }
    }
    return true
}
```

#### Java

```java
public static boolean isPrime(int n) {
    if (n <= 1) return false;
    for (int i = 2; i <= Math.sqrt(n); i++) {
        if (n % i == 0) return false;
    }
    return true;
}
```

#### Python

```python
import math

def is_prime(n):
    if n <= 1:
        return False
    for i in range(2, int(math.sqrt(n)) + 1):
        if n % i == 0:
            return False
    return True
```

---

### Example 5: Reverse an Array

#### Pseudo Code

```text
FUNCTION reverseArray(array)
    SET left = 0
    SET right = length(array) - 1
    WHILE left < right DO
        SWAP array[left] AND array[right]
        SET left = left + 1
        SET right = right - 1
    END WHILE
    RETURN array
END FUNCTION
```

#### Go

```go
func reverseArray(arr []int) []int {
    left, right := 0, len(arr)-1
    for left < right {
        arr[left], arr[right] = arr[right], arr[left]
        left++
        right--
    }
    return arr
}
```

#### Java

```java
public static int[] reverseArray(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
        left++;
        right--;
    }
    return arr;
}
```

#### Python

```python
def reverse_array(arr):
    left, right = 0, len(arr) - 1
    while left < right:
        arr[left], arr[right] = arr[right], arr[left]
        left += 1
        right -= 1
    return arr
```

---

## Common Patterns

### Pattern 1: Accumulator

```text
SET sum = 0
FOR each item IN collection DO
    SET sum = sum + item
END FOR
RETURN sum
```

### Pattern 2: Search and Return

```text
FOR each item IN collection DO
    IF item matches condition THEN
        RETURN item
    END IF
END FOR
RETURN "not found"
```

### Pattern 3: Filter

```text
SET result = empty list
FOR each item IN collection DO
    IF item matches condition THEN
        APPEND item TO result
    END IF
END FOR
RETURN result
```

### Pattern 4: Counter

```text
SET count = 0
FOR each item IN collection DO
    IF item matches condition THEN
        SET count = count + 1
    END IF
END FOR
RETURN count
```

---

## Best Practices

1. **Use clear, descriptive names:** `maxValue` not `m`, `studentList` not `sl`
2. **Keep it language-independent:** Don't use `fmt.Println` or `System.out.println` — use `PRINT`
3. **Use consistent indentation:** 2-4 spaces per level
4. **One action per line:** Don't combine multiple operations
5. **Be explicit about types only when it matters:** Don't write `int x = 5`, just `SET x = 5`
6. **Comment complex logic:** Add `//` comments for tricky parts
7. **Always handle edge cases:** Include IF checks for empty arrays, zero values, etc.

---

## Common Mistakes

| Mistake | Example | Fix |
|---------|---------|-----|
| Too vague | "sort the array" | Show the actual sorting steps |
| Too language-specific | `for i in range(len(arr))` | Use `FOR i = 0 TO n-1 DO` |
| Missing edge cases | No check for empty array | Add `IF length(array) == 0 THEN RETURN` |
| Inconsistent formatting | Mixed indentation | Use consistent 4-space indentation |
| No RETURN statement | Function ends without returning | Always include explicit RETURN |
| Ambiguous operators | `a = b` (assignment or comparison?) | Use `SET a = b` for assignment, `a == b` for comparison |

---

## Cheat Sheet

| Action | Pseudo Code |
|--------|------------|
| Assign | `SET x = 5` |
| Conditional | `IF x > 0 THEN ... ELSE ... END IF` |
| While loop | `WHILE x > 0 DO ... END WHILE` |
| For loop | `FOR i = 0 TO n-1 DO ... END FOR` |
| For-each | `FOR each item IN list DO ... END FOR` |
| Function | `FUNCTION name(params) ... RETURN value ... END FUNCTION` |
| Print | `PRINT "message"` or `OUTPUT x` |
| Input | `INPUT x` or `READ x` |
| Array access | `array[i]` |
| Array length | `length(array)` |
| Swap | `SWAP a AND b` |
| Comment | `// this is a comment` |

---

## Summary

Pseudo code is the universal language of algorithms. It lets you focus on logic without syntax distractions. Write pseudo code first, then translate to Go, Java, or Python. This approach prevents you from getting stuck on language details and helps you think clearly about the algorithm itself.

---

## Further Reading

- *Introduction to Algorithms* (CLRS) — uses pseudo code throughout
- *The Algorithm Design Manual* (Skiena) — practical pseudo code examples
- *Grokking Algorithms* — visual pseudo code for beginners
- LeetCode editorial solutions — often include pseudo code before implementation
