# Functions — Junior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Glossary](#glossary)
4. [What Are Functions?](#what-are-functions)
5. [Function Syntax](#function-syntax)
6. [Parameters and Arguments](#parameters-and-arguments)
7. [Return Values](#return-values)
8. [Function Naming Conventions](#function-naming-conventions)
9. [Default Parameters and Variadic Arguments](#default-parameters-and-variadic-arguments)
10. [Scope and Variable Lifetime](#scope-and-variable-lifetime)
11. [Code Examples](#code-examples)
12. [Best Practices](#best-practices)
13. [Common Mistakes](#common-mistakes)
14. [Cheat Sheet](#cheat-sheet)
15. [Summary](#summary)
16. [Further Reading](#further-reading)

---

## Introduction

> Focus: "What are functions?" and "How to write and use them?"

A **function** is a named, reusable block of code that performs a specific task. Functions are the most fundamental tool for organizing code. Instead of writing the same logic over and over, you write it once inside a function and call it whenever needed.

Why functions exist:
1. **Reusability** — write once, use many times
2. **Abstraction** — hide complex logic behind a simple name
3. **Readability** — break a 500-line program into small, named pieces
4. **Testability** — test each piece in isolation
5. **Maintainability** — change logic in one place, not twenty

---

## Prerequisites

- **Required:** Variables and data types in Go/Java/Python
- **Required:** Control structures (if/else, loops)
- **Helpful:** Understanding of basic pseudo code

---

## Glossary

| Term | Definition |
|------|-----------|
| **Function** | A named block of code that performs a specific task |
| **Parameter** | A variable declared in a function's definition (placeholder) |
| **Argument** | The actual value passed to a function when calling it |
| **Return value** | The output a function sends back to its caller |
| **Signature** | The function's name + parameter types + return type |
| **Call / Invoke** | Execute a function by using its name with parentheses |
| **Scope** | The region of code where a variable is accessible |
| **Variadic** | A function that accepts a variable number of arguments |
| **Void** | A function that returns nothing (called "void" in Java) |

---

## What Are Functions?

Think of a function like a recipe. It has:
- A **name** (e.g., `bakeCake`)
- **Ingredients** (parameters: `flour`, `sugar`, `eggs`)
- **Instructions** (the body: mix, bake, cool)
- A **result** (return value: the cake)

You can use the same recipe many times with different ingredients.

### Without functions (bad):

```
// Calculate area of rectangle 1
width1 = 5
height1 = 10
area1 = width1 * height1
print(area1)

// Calculate area of rectangle 2
width2 = 3
height2 = 7
area2 = width2 * height2
print(area2)

// Same logic repeated — if formula changes, fix in multiple places
```

### With functions (good):

```
function area(width, height):
    return width * height

print(area(5, 10))
print(area(3, 7))
```

---

## Function Syntax

### Go

```go
func functionName(param1 type1, param2 type2) returnType {
    // body
    return value
}
```

```go
func add(a int, b int) int {
    return a + b
}

// Shorthand: same type params
func add(a, b int) int {
    return a + b
}

// Multiple return values (Go specialty)
func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

// No return value
func greet(name string) {
    fmt.Println("Hello,", name)
}
```

### Java

```java
returnType functionName(type1 param1, type2 param2) {
    // body
    return value;
}
```

```java
public static int add(int a, int b) {
    return a + b;
}

// No return value → void
public static void greet(String name) {
    System.out.println("Hello, " + name);
}

// Methods in classes
public class Calculator {
    public int add(int a, int b) {     // instance method
        return a + b;
    }

    public static int multiply(int a, int b) {  // static method
        return a * b;
    }
}
```

### Python

```python
def function_name(param1, param2):
    """Docstring: describes what the function does."""
    # body
    return value
```

```python
def add(a, b):
    """Return the sum of a and b."""
    return a + b

# No explicit return → returns None
def greet(name):
    print(f"Hello, {name}")

# Multiple return values (tuple)
def divide(a, b):
    if b == 0:
        return None, "division by zero"
    return a / b, None
```

---

## Parameters and Arguments

**Parameters** are declared in the function definition. **Arguments** are the actual values passed when calling.

```
def greet(name):      ← "name" is the PARAMETER
    print(name)

greet("Alice")        ← "Alice" is the ARGUMENT
```

### Pass by Value vs Pass by Reference

| Language | Primitives | Objects/Slices/Arrays |
|----------|-----------|----------------------|
| **Go** | Pass by value (copy) | Slices/maps pass reference to underlying data |
| **Java** | Pass by value (copy) | Object references passed by value (can mutate object, can't reassign reference) |
| **Python** | Pass by "object reference" | Mutable objects can be changed; immutable cannot |

#### Go

```go
// Value types are copied
func doubleVal(x int) {
    x = x * 2  // only changes local copy
}

n := 5
doubleVal(n)
fmt.Println(n) // 5 — unchanged

// Pointers let you modify the original
func doublePtr(x *int) {
    *x = *x * 2
}

doublePtr(&n)
fmt.Println(n) // 10 — changed

// Slices share underlying array
func appendItem(s []int) []int {
    return append(s, 99)
}
```

#### Java

```java
// Primitives are copied
public static void doubleVal(int x) {
    x = x * 2;  // only changes local copy
}

int n = 5;
doubleVal(n);
System.out.println(n); // 5 — unchanged

// Objects: reference is copied, but object is shared
public static void addItem(List<Integer> list) {
    list.add(99);  // modifies the original list
}

List<Integer> nums = new ArrayList<>(List.of(1, 2, 3));
addItem(nums);
System.out.println(nums); // [1, 2, 3, 99]
```

#### Python

```python
# Immutable types (int, str, tuple) can't be changed
def double_val(x):
    x = x * 2  # creates a new local int

n = 5
double_val(n)
print(n)  # 5 — unchanged

# Mutable types (list, dict) CAN be changed
def add_item(lst):
    lst.append(99)

nums = [1, 2, 3]
add_item(nums)
print(nums)  # [1, 2, 3, 99]
```

---

## Return Values

### Single Return

```go
// Go
func square(x int) int {
    return x * x
}
```

```java
// Java
public static int square(int x) {
    return x * x;
}
```

```python
# Python
def square(x):
    return x * x
```

### Multiple Return Values

#### Go — built-in multiple returns

```go
func minMax(arr []int) (int, int) {
    min, max := arr[0], arr[0]
    for _, v := range arr {
        if v < min { min = v }
        if v > max { max = v }
    }
    return min, max
}

lo, hi := minMax([]int{3, 1, 4, 1, 5})
fmt.Println(lo, hi) // 1 5
```

#### Java — return an object or array

```java
public static int[] minMax(int[] arr) {
    int min = arr[0], max = arr[0];
    for (int v : arr) {
        if (v < min) min = v;
        if (v > max) max = v;
    }
    return new int[]{min, max};
}

int[] result = minMax(new int[]{3, 1, 4, 1, 5});
System.out.println(result[0] + " " + result[1]); // 1 5
```

#### Python — return a tuple

```python
def min_max(arr):
    return min(arr), max(arr)

lo, hi = min_max([3, 1, 4, 1, 5])
print(lo, hi)  # 1 5
```

---

## Function Naming Conventions

| Language | Convention | Examples |
|----------|-----------|----------|
| **Go** | camelCase (exported = PascalCase) | `calculateArea`, `ParseJSON` |
| **Java** | camelCase | `calculateArea`, `getUser` |
| **Python** | snake_case | `calculate_area`, `get_user` |

**Good naming rules:**
- Use **verbs** for functions that do something: `calculate`, `fetch`, `validate`
- Use **is/has** prefix for boolean returns: `isValid`, `hasPermission`
- Be specific: `getUserById` not `get`
- Keep it short but descriptive: `calcTax` is fine, `c` is not

---

## Default Parameters and Variadic Arguments

### Default Parameters

Go does **not** have default parameters. Java does **not** have them either (use overloading). Python **does**.

#### Python — default parameters

```python
def greet(name, greeting="Hello"):
    print(f"{greeting}, {name}!")

greet("Alice")             # Hello, Alice!
greet("Alice", "Bonjour")  # Bonjour, Alice!
```

#### Java — method overloading (workaround)

```java
public static void greet(String name) {
    greet(name, "Hello");
}

public static void greet(String name, String greeting) {
    System.out.println(greeting + ", " + name + "!");
}

greet("Alice");             // Hello, Alice!
greet("Alice", "Bonjour");  // Bonjour, Alice!
```

#### Go — use variadic or options pattern (workaround)

```go
func greet(name string, greeting ...string) {
    g := "Hello"
    if len(greeting) > 0 {
        g = greeting[0]
    }
    fmt.Printf("%s, %s!\n", g, name)
}

greet("Alice")             // Hello, Alice!
greet("Alice", "Bonjour")  // Bonjour, Alice!
```

### Variadic Arguments

Functions that accept a variable number of arguments.

#### Go — `...type`

```go
func sum(nums ...int) int {
    total := 0
    for _, n := range nums {
        total += n
    }
    return total
}

fmt.Println(sum(1, 2, 3))       // 6
fmt.Println(sum(1, 2, 3, 4, 5)) // 15

// Spread a slice into variadic
nums := []int{1, 2, 3}
fmt.Println(sum(nums...)) // 6
```

#### Java — `type...`

```java
public static int sum(int... nums) {
    int total = 0;
    for (int n : nums) {
        total += n;
    }
    return total;
}

System.out.println(sum(1, 2, 3));       // 6
System.out.println(sum(1, 2, 3, 4, 5)); // 15
```

#### Python — `*args` and `**kwargs`

```python
def sum_all(*args):
    return sum(args)

print(sum_all(1, 2, 3))       # 6
print(sum_all(1, 2, 3, 4, 5)) # 15

# **kwargs for keyword arguments
def create_user(**kwargs):
    print(kwargs)  # dict

create_user(name="Alice", age=30)  # {'name': 'Alice', 'age': 30}
```

---

## Scope and Variable Lifetime

**Scope** is the region of code where a variable can be accessed. **Lifetime** is how long it exists in memory.

### Go

```go
var global = "I'm global"  // package-level scope

func example() {
    local := "I'm local"   // function scope
    fmt.Println(global)     // OK
    fmt.Println(local)      // OK

    if true {
        block := "I'm block-scoped"
        fmt.Println(block)  // OK
    }
    // fmt.Println(block)   // ERROR: block not accessible here
}
// fmt.Println(local)       // ERROR: local not accessible here
```

### Java

```java
public class Example {
    static String global = "I'm class-level";  // class scope

    public static void example() {
        String local = "I'm local";  // method scope
        System.out.println(global);  // OK
        System.out.println(local);   // OK

        if (true) {
            String block = "I'm block-scoped";
            System.out.println(block); // OK
        }
        // System.out.println(block);  // ERROR
    }
}
```

### Python

```python
global_var = "I'm global"  # module scope

def example():
    local_var = "I'm local"  # function scope
    print(global_var)        # OK — can READ global
    print(local_var)         # OK

    # To MODIFY a global, use 'global' keyword
    global global_var
    global_var = "modified"

# print(local_var)  # ERROR: not accessible

# LEGB rule: Local → Enclosing → Global → Built-in
```

### Scope Comparison

| Scope Level | Go | Java | Python |
|------------|-----|------|--------|
| Global/Module | Package-level `var` | `static` class fields | Module-level variables |
| Function | Inside `func` | Inside method | Inside `def` |
| Block | Inside `{}` | Inside `{}` | No block scope (if/for don't create scope) |
| Loop variable | Scoped to loop | Scoped to loop block | Leaks into function scope |

---

## Code Examples

### Example 1: Temperature Converter

#### Go

```go
func celsiusToFahrenheit(c float64) float64 {
    return c*9.0/5.0 + 32
}

func fahrenheitToCelsius(f float64) float64 {
    return (f - 32) * 5.0 / 9.0
}

func main() {
    fmt.Printf("100°C = %.1f°F\n", celsiusToFahrenheit(100))   // 212.0°F
    fmt.Printf("72°F = %.1f°C\n", fahrenheitToCelsius(72))     // 22.2°C
}
```

#### Java

```java
public static double celsiusToFahrenheit(double c) {
    return c * 9.0 / 5.0 + 32;
}

public static double fahrenheitToCelsius(double f) {
    return (f - 32) * 5.0 / 9.0;
}

public static void main(String[] args) {
    System.out.printf("100°C = %.1f°F%n", celsiusToFahrenheit(100));  // 212.0°F
    System.out.printf("72°F = %.1f°C%n", fahrenheitToCelsius(72));    // 22.2°C
}
```

#### Python

```python
def celsius_to_fahrenheit(c):
    return c * 9.0 / 5.0 + 32

def fahrenheit_to_celsius(f):
    return (f - 32) * 5.0 / 9.0

print(f"100°C = {celsius_to_fahrenheit(100):.1f}°F")  # 212.0°F
print(f"72°F = {fahrenheit_to_celsius(72):.1f}°C")    # 22.2°C
```

### Example 2: Validate Password

#### Go

```go
func validatePassword(password string) (bool, []string) {
    var errors []string

    if len(password) < 8 {
        errors = append(errors, "must be at least 8 characters")
    }

    hasUpper, hasLower, hasDigit := false, false, false
    for _, ch := range password {
        switch {
        case ch >= 'A' && ch <= 'Z':
            hasUpper = true
        case ch >= 'a' && ch <= 'z':
            hasLower = true
        case ch >= '0' && ch <= '9':
            hasDigit = true
        }
    }

    if !hasUpper { errors = append(errors, "must contain uppercase") }
    if !hasLower { errors = append(errors, "must contain lowercase") }
    if !hasDigit { errors = append(errors, "must contain digit") }

    return len(errors) == 0, errors
}
```

#### Java

```java
public static Map<String, Object> validatePassword(String password) {
    List<String> errors = new ArrayList<>();

    if (password.length() < 8)
        errors.add("must be at least 8 characters");
    if (!password.matches(".*[A-Z].*"))
        errors.add("must contain uppercase");
    if (!password.matches(".*[a-z].*"))
        errors.add("must contain lowercase");
    if (!password.matches(".*\\d.*"))
        errors.add("must contain digit");

    return Map.of("valid", errors.isEmpty(), "errors", errors);
}
```

#### Python

```python
def validate_password(password):
    errors = []

    if len(password) < 8:
        errors.append("must be at least 8 characters")
    if not any(c.isupper() for c in password):
        errors.append("must contain uppercase")
    if not any(c.islower() for c in password):
        errors.append("must contain lowercase")
    if not any(c.isdigit() for c in password):
        errors.append("must contain digit")

    return len(errors) == 0, errors

valid, errs = validate_password("Hello1")
print(valid, errs)  # False ['must be at least 8 characters']
```

---

## Best Practices

1. **One function, one task** — a function should do exactly one thing
2. **Keep functions short** — if it exceeds 20-30 lines, consider splitting
3. **Name describes what it does** — `calculateTax`, not `doStuff`
4. **Limit parameters** — 3-4 max; if more, use a struct/object
5. **Return early** — use guard clauses to handle edge cases first
6. **Don't use global variables** — pass data through parameters
7. **Document your functions** — Go: comment above function; Java: Javadoc; Python: docstring

---

## Common Mistakes

| Mistake | What Happens | Fix |
|---------|-------------|-----|
| Forgetting `return` | Function returns default/None | Always check return path |
| Modifying mutable arguments | Caller's data changes unexpectedly | Copy input if needed |
| Using global state | Hard to test, unpredictable | Pass as parameter |
| Too many parameters | Hard to read and call | Use struct/class/dict |
| Python: mutable default arg | `def f(lst=[])` — list shared across calls | Use `None` as default |
| Go: ignoring error return | Silent failures | Always check `if err != nil` |

### Python Mutable Default Argument Trap

```python
# BUG: default list is shared across all calls
def add_item(item, lst=[]):
    lst.append(item)
    return lst

print(add_item(1))  # [1]
print(add_item(2))  # [1, 2] — BUG! Expected [2]

# FIX: use None
def add_item(item, lst=None):
    if lst is None:
        lst = []
    lst.append(item)
    return lst
```

---

## Cheat Sheet

### Function Declaration

| Feature | Go | Java | Python |
|---------|-----|------|--------|
| Declare | `func name(p type) ret {}` | `retType name(type p) {}` | `def name(p):` |
| No return | `func name() {}` | `void name() {}` | `def name(): ...` (returns None) |
| Multiple returns | `func f() (int, error)` | Return object/array | `return a, b` (tuple) |
| Variadic | `func f(a ...int)` | `void f(int... a)` | `def f(*args)` |
| Default params | Not supported | Not supported (overload) | `def f(x=10)` |
| Named return | `func f() (n int) { n=1; return }` | N/A | N/A |
| Keyword args | N/A | N/A | `f(name="Alice")` |

### Calling Functions

| Feature | Go | Java | Python |
|---------|-----|------|--------|
| Call | `result := f(arg)` | `int r = f(arg);` | `r = f(arg)` |
| Ignore return | `f(arg)` or `_ = f(arg)` | `f(arg);` | `f(arg)` |
| Multiple returns | `a, b := f()` | N/A | `a, b = f()` |
| Spread slice/array | `f(slice...)` | N/A | `f(*list)` |

### Scope Rules

| Feature | Go | Java | Python |
|---------|-----|------|--------|
| Block scope | Yes | Yes | No (only function scope) |
| Loop var scope | Scoped to loop | Scoped to block | Leaks to function |
| Global access | Package var | `static` field | `global` keyword to modify |

---

## Summary

- A **function** is a named, reusable block of code
- Functions have **parameters** (inputs) and **return values** (outputs)
- Go supports multiple returns; Java uses objects; Python uses tuples
- **Variadic** functions accept variable number of arguments
- **Scope** determines where variables are accessible
- Follow naming conventions: Go=camelCase, Java=camelCase, Python=snake_case
- Keep functions small, focused, and well-named

---

## Further Reading

- [Go Functions (Tour of Go)](https://go.dev/tour/moretypes/24)
- [Java Methods (Oracle Docs)](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html)
- [Python Functions (Official Docs)](https://docs.python.org/3/tutorial/controlflow.html#defining-functions)
- *Clean Code* by Robert C. Martin — Chapter 3: Functions
