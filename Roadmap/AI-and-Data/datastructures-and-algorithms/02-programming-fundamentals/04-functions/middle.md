# Functions — Middle Level

## Table of Contents

1. [Introduction](#introduction)
2. [First-Class Functions](#first-class-functions)
3. [Higher-Order Functions](#higher-order-functions)
4. [Closures and Lexical Scoping](#closures-and-lexical-scoping)
5. [Anonymous Functions and Lambdas](#anonymous-functions-and-lambdas)
6. [Callback Pattern](#callback-pattern)
7. [Recursion vs Iteration](#recursion-vs-iteration)
8. [Function Composition](#function-composition)
9. [Code Examples](#code-examples)
10. [Performance Analysis](#performance-analysis)
11. [Summary](#summary)

---

## Introduction

> Focus: "How do functions become powerful building blocks?" and "When to use which pattern?"

At the middle level, functions stop being just "named blocks of code" and become **values** you can pass around, combine, and compose. You'll learn that functions can create other functions (closures), accept functions as input (higher-order functions), and call themselves (recursion).

---

## First-Class Functions

A language has **first-class functions** when functions can be:
1. Assigned to variables
2. Passed as arguments to other functions
3. Returned from other functions
4. Stored in data structures

All three languages — Go, Java (since Java 8), and Python — support first-class functions.

### Go

```go
// Assign function to variable
add := func(a, b int) int { return a + b }
fmt.Println(add(3, 4)) // 7

// Function type
type MathOp func(int, int) int

func apply(op MathOp, a, b int) int {
    return op(a, b)
}

fmt.Println(apply(add, 10, 20)) // 30

// Store in a map
ops := map[string]MathOp{
    "+": func(a, b int) int { return a + b },
    "-": func(a, b int) int { return a - b },
    "*": func(a, b int) int { return a * b },
}
fmt.Println(ops["*"](6, 7)) // 42
```

### Java

```java
// Function interface (java.util.function)
Function<Integer, Integer> square = x -> x * x;
System.out.println(square.apply(5)); // 25

// BiFunction for two arguments
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
System.out.println(add.apply(3, 4)); // 7

// Store in a map
Map<String, BiFunction<Integer, Integer, Integer>> ops = Map.of(
    "+", (a, b) -> a + b,
    "-", (a, b) -> a - b,
    "*", (a, b) -> a * b
);
System.out.println(ops.get("*").apply(6, 7)); // 42

// Method references — shorthand for lambdas
Function<String, Integer> len = String::length;
System.out.println(len.apply("hello")); // 5
```

### Python

```python
# Assign function to variable
add = lambda a, b: a + b
print(add(3, 4))  # 7

# Or use a regular function
def multiply(a, b):
    return a * b

op = multiply
print(op(6, 7))  # 42

# Store in a dict
ops = {
    "+": lambda a, b: a + b,
    "-": lambda a, b: a - b,
    "*": lambda a, b: a * b,
}
print(ops["*"](6, 7))  # 42

# Functions are objects — they have attributes
print(multiply.__name__)  # multiply
print(multiply.__doc__)   # None (no docstring)
```

---

## Higher-Order Functions

A **higher-order function** (HOF) either takes a function as an argument or returns a function. The classic trio: `map`, `filter`, `reduce`.

### Map — transform each element

#### Go

```go
func mapInts(arr []int, f func(int) int) []int {
    result := make([]int, len(arr))
    for i, v := range arr {
        result[i] = f(v)
    }
    return result
}

nums := []int{1, 2, 3, 4, 5}
squared := mapInts(nums, func(x int) int { return x * x })
fmt.Println(squared) // [1 4 9 16 25]
```

#### Java

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

List<Integer> squared = nums.stream()
    .map(x -> x * x)
    .collect(Collectors.toList());

System.out.println(squared); // [1, 4, 9, 16, 25]
```

#### Python

```python
nums = [1, 2, 3, 4, 5]

squared = list(map(lambda x: x ** 2, nums))
print(squared)  # [1, 4, 9, 16, 25]

# Pythonic alternative: list comprehension
squared = [x ** 2 for x in nums]
```

### Filter — keep elements that match a condition

#### Go

```go
func filter(arr []int, predicate func(int) bool) []int {
    var result []int
    for _, v := range arr {
        if predicate(v) {
            result = append(result, v)
        }
    }
    return result
}

nums := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
evens := filter(nums, func(x int) bool { return x%2 == 0 })
fmt.Println(evens) // [2 4 6 8 10]
```

#### Java

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

List<Integer> evens = nums.stream()
    .filter(x -> x % 2 == 0)
    .collect(Collectors.toList());

System.out.println(evens); // [2, 4, 6, 8, 10]
```

#### Python

```python
nums = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

evens = list(filter(lambda x: x % 2 == 0, nums))
print(evens)  # [2, 4, 6, 8, 10]

# Pythonic: list comprehension
evens = [x for x in nums if x % 2 == 0]
```

### Reduce — combine all elements into one value

#### Go

```go
func reduce(arr []int, initial int, f func(int, int) int) int {
    result := initial
    for _, v := range arr {
        result = f(result, v)
    }
    return result
}

nums := []int{1, 2, 3, 4, 5}
sum := reduce(nums, 0, func(acc, x int) int { return acc + x })
fmt.Println(sum) // 15

product := reduce(nums, 1, func(acc, x int) int { return acc * x })
fmt.Println(product) // 120
```

#### Java

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

int sum = nums.stream().reduce(0, Integer::sum);
System.out.println(sum); // 15

int product = nums.stream().reduce(1, (a, b) -> a * b);
System.out.println(product); // 120
```

#### Python

```python
from functools import reduce

nums = [1, 2, 3, 4, 5]

total = reduce(lambda acc, x: acc + x, nums, 0)
print(total)  # 15

product = reduce(lambda acc, x: acc * x, nums, 1)
print(product)  # 120
```

### Chaining Map/Filter/Reduce

#### Go

```go
// Sum of squares of even numbers
nums := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
evens := filter(nums, func(x int) bool { return x%2 == 0 })
squares := mapInts(evens, func(x int) int { return x * x })
sum := reduce(squares, 0, func(a, b int) int { return a + b })
fmt.Println(sum) // 220 (4+16+36+64+100)
```

#### Java

```java
int sum = IntStream.rangeClosed(1, 10)
    .filter(x -> x % 2 == 0)
    .map(x -> x * x)
    .sum();
System.out.println(sum); // 220
```

#### Python

```python
result = sum(x**2 for x in range(1, 11) if x % 2 == 0)
print(result)  # 220
```

---

## Closures and Lexical Scoping

A **closure** is a function that "remembers" variables from its enclosing scope, even after that scope has finished executing.

**Lexical scoping** means a function accesses variables based on where it was **defined**, not where it is **called**.

### Go

```go
func makeCounter() func() int {
    count := 0                   // captured by closure
    return func() int {
        count++                  // modifies the captured variable
        return count
    }
}

counter := makeCounter()
fmt.Println(counter()) // 1
fmt.Println(counter()) // 2
fmt.Println(counter()) // 3

// Each call to makeCounter creates a NEW closure with its own 'count'
counter2 := makeCounter()
fmt.Println(counter2()) // 1 — independent from counter
```

### Java

```java
// Java closures via lambdas (variables must be effectively final)
public static Supplier<Integer> makeCounter() {
    int[] count = {0};  // array trick: reference is final, contents are not
    return () -> {
        count[0]++;
        return count[0];
    };
}

Supplier<Integer> counter = makeCounter();
System.out.println(counter.get()); // 1
System.out.println(counter.get()); // 2
System.out.println(counter.get()); // 3

// Using AtomicInteger (cleaner approach)
public static Supplier<Integer> makeCounter2() {
    AtomicInteger count = new AtomicInteger(0);
    return () -> count.incrementAndGet();
}
```

### Python

```python
def make_counter():
    count = 0
    def counter():
        nonlocal count     # needed to modify enclosing variable
        count += 1
        return count
    return counter

counter = make_counter()
print(counter())  # 1
print(counter())  # 2
print(counter())  # 3

counter2 = make_counter()
print(counter2())  # 1 — independent
```

### Classic Closure Gotcha: Loop Variables

#### Go

```go
// Before Go 1.22 — BUG (fixed in Go 1.22+)
funcs := []func(){}
for i := 0; i < 3; i++ {
    funcs = append(funcs, func() { fmt.Println(i) })
}
for _, f := range funcs {
    f() // Go 1.22+: prints 0, 1, 2 (each iteration has its own i)
}

// Pre-1.22 fix: capture explicitly
for i := 0; i < 3; i++ {
    i := i  // shadow with new variable
    funcs = append(funcs, func() { fmt.Println(i) })
}
```

#### Java

```java
// Java requires effectively final — forced to do it right
List<Runnable> funcs = new ArrayList<>();
for (int i = 0; i < 3; i++) {
    int captured = i;  // must copy to final variable
    funcs.add(() -> System.out.println(captured));
}
funcs.forEach(Runnable::run); // 0, 1, 2
```

#### Python

```python
# BUG: all lambdas share the same 'i'
funcs = [lambda: print(i) for i in range(3)]
for f in funcs:
    f()  # 2, 2, 2 — all see final value of i

# FIX: capture via default parameter
funcs = [lambda i=i: print(i) for i in range(3)]
for f in funcs:
    f()  # 0, 1, 2
```

---

## Anonymous Functions and Lambdas

Anonymous functions are functions without a name. Used for short, throwaway logic.

### Go — anonymous functions (full-featured)

```go
// Inline anonymous function
result := func(a, b int) int {
    return a + b
}(3, 4)
fmt.Println(result) // 7

// As argument
sort.Slice(people, func(i, j int) bool {
    return people[i].Age < people[j].Age
})

// Immediately invoked
func() {
    fmt.Println("executed immediately")
}()
```

### Java — lambdas (since Java 8)

```java
// Lambda expressions
Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());

List<String> words = new ArrayList<>(List.of("banana", "apple", "fig", "date"));
words.sort(byLength);
System.out.println(words); // [fig, date, apple, banana]

// Single expression — no braces needed
Function<Integer, Integer> double_ = x -> x * 2;

// Multiple statements — need braces and return
Function<Integer, Integer> factorial = n -> {
    int result = 1;
    for (int i = 2; i <= n; i++) result *= i;
    return result;
};
```

### Python — lambdas (single expression only)

```python
# Lambda: limited to one expression
square = lambda x: x ** 2
print(square(5))  # 25

# Used inline for sorting
people = [("Alice", 30), ("Bob", 25), ("Charlie", 35)]
people.sort(key=lambda p: p[1])
print(people)  # [('Bob', 25), ('Alice', 30), ('Charlie', 35)]

# For complex logic, use a regular function
# BAD: lambda with complex logic
# GOOD: named function
def complex_key(item):
    if item.priority == "high":
        return 0
    return item.created_at
```

---

## Callback Pattern

A **callback** is a function passed as an argument to another function, to be called later (often after an event or async operation).

### Go

```go
// Callback for event handling
type EventHandler func(event string)

func onUserLogin(handler EventHandler) {
    // ... authentication logic ...
    user := "Alice"
    handler("login:" + user)
}

func main() {
    onUserLogin(func(event string) {
        fmt.Println("Event received:", event)
    })
    // Output: Event received: login:Alice
}

// Callback for processing pipeline
func processFile(path string, onLine func(int, string)) error {
    file, err := os.Open(path)
    if err != nil {
        return err
    }
    defer file.Close()

    scanner := bufio.NewScanner(file)
    lineNum := 0
    for scanner.Scan() {
        lineNum++
        onLine(lineNum, scanner.Text())
    }
    return scanner.Err()
}

// Usage
processFile("data.txt", func(num int, line string) {
    if strings.Contains(line, "ERROR") {
        fmt.Printf("Line %d: %s\n", num, line)
    }
})
```

### Java

```java
// Functional interface as callback
@FunctionalInterface
interface EventHandler {
    void handle(String event);
}

public static void onUserLogin(EventHandler handler) {
    String user = "Alice";
    handler.handle("login:" + user);
}

// Usage
onUserLogin(event -> System.out.println("Event: " + event));

// Built-in functional interfaces as callbacks
public static <T> List<T> processItems(
        List<T> items,
        Predicate<T> filter,
        Function<T, T> transform) {
    return items.stream()
        .filter(filter)
        .map(transform)
        .collect(Collectors.toList());
}

// Usage
List<String> result = processItems(
    List.of("hello", "world", "hi", "hey"),
    s -> s.length() > 2,              // filter callback
    String::toUpperCase               // transform callback
);
// ["HELLO", "WORLD", "HEY"]
```

### Python

```python
# Simple callback
def on_user_login(handler):
    user = "Alice"
    handler(f"login:{user}")

on_user_login(lambda event: print(f"Event: {event}"))
# Event: login:Alice

# Callback with error handling
def retry(action, on_success, on_failure, max_attempts=3):
    for attempt in range(1, max_attempts + 1):
        try:
            result = action()
            on_success(result)
            return
        except Exception as e:
            if attempt == max_attempts:
                on_failure(e)

# Usage
retry(
    action=lambda: int("not_a_number"),
    on_success=lambda r: print(f"Got: {r}"),
    on_failure=lambda e: print(f"Failed: {e}")
)
# Failed: invalid literal for int() with base 10: 'not_a_number'
```

---

## Recursion vs Iteration

**Recursion**: a function calls itself. **Iteration**: a loop repeats a block.

### When to use recursion:
- Tree/graph traversal
- Divide and conquer (merge sort, quicksort)
- Problems naturally defined recursively (Fibonacci, factorial, Tower of Hanoi)
- Backtracking (maze solving, N-queens)

### When to use iteration:
- Simple counting/accumulation
- Performance-critical code (no call stack overhead)
- When the recursive depth could be huge (stack overflow risk)

### Example: Factorial

#### Go

```go
// Recursive
func factorialRec(n int) int {
    if n <= 1 {
        return 1
    }
    return n * factorialRec(n-1)
}

// Iterative
func factorialIter(n int) int {
    result := 1
    for i := 2; i <= n; i++ {
        result *= i
    }
    return result
}
```

#### Java

```java
// Recursive
public static long factorialRec(int n) {
    if (n <= 1) return 1;
    return n * factorialRec(n - 1);
}

// Iterative
public static long factorialIter(int n) {
    long result = 1;
    for (int i = 2; i <= n; i++) {
        result *= i;
    }
    return result;
}
```

#### Python

```python
# Recursive
def factorial_rec(n):
    if n <= 1:
        return 1
    return n * factorial_rec(n - 1)

# Iterative
def factorial_iter(n):
    result = 1
    for i in range(2, n + 1):
        result *= i
    return result
```

### Example: Binary Search (recursive vs iterative)

#### Go

```go
// Recursive
func binarySearchRec(arr []int, target, lo, hi int) int {
    if lo > hi {
        return -1
    }
    mid := lo + (hi-lo)/2
    if arr[mid] == target {
        return mid
    } else if arr[mid] < target {
        return binarySearchRec(arr, target, mid+1, hi)
    }
    return binarySearchRec(arr, target, lo, mid-1)
}

// Iterative
func binarySearchIter(arr []int, target int) int {
    lo, hi := 0, len(arr)-1
    for lo <= hi {
        mid := lo + (hi-lo)/2
        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return -1
}
```

#### Java

```java
// Recursive
public static int binarySearchRec(int[] arr, int target, int lo, int hi) {
    if (lo > hi) return -1;
    int mid = lo + (hi - lo) / 2;
    if (arr[mid] == target) return mid;
    if (arr[mid] < target) return binarySearchRec(arr, target, mid + 1, hi);
    return binarySearchRec(arr, target, lo, mid - 1);
}

// Iterative
public static int binarySearchIter(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
```

#### Python

```python
# Recursive
def binary_search_rec(arr, target, lo, hi):
    if lo > hi:
        return -1
    mid = lo + (hi - lo) // 2
    if arr[mid] == target:
        return mid
    elif arr[mid] < target:
        return binary_search_rec(arr, target, mid + 1, hi)
    return binary_search_rec(arr, target, lo, mid - 1)

# Iterative
def binary_search_iter(arr, target):
    lo, hi = 0, len(arr) - 1
    while lo <= hi:
        mid = lo + (hi - lo) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1
```

### Tradeoff Comparison

| Factor | Recursion | Iteration |
|--------|----------|-----------|
| **Readability** | Better for tree/divide-conquer | Better for simple loops |
| **Memory** | O(n) stack frames | O(1) extra space |
| **Speed** | Slower (function call overhead) | Faster |
| **Stack overflow** | Risk for deep recursion | No risk |
| **Tail call opt** | Python: No, Java: No, Go: No | N/A |
| **When to use** | Trees, graphs, backtracking | Counting, accumulation |

---

## Function Composition

**Composition** is combining two or more functions to create a new function: `compose(f, g)(x) = f(g(x))`.

### Go

```go
func compose(f, g func(int) int) func(int) int {
    return func(x int) int {
        return f(g(x))
    }
}

func pipe(funcs ...func(int) int) func(int) int {
    return func(x int) int {
        result := x
        for _, f := range funcs {
            result = f(result)
        }
        return result
    }
}

double := func(x int) int { return x * 2 }
addOne := func(x int) int { return x + 1 }
square := func(x int) int { return x * x }

// compose: right-to-left → addOne first, then double
doubleAfterIncrement := compose(double, addOne)
fmt.Println(doubleAfterIncrement(5)) // double(addOne(5)) = double(6) = 12

// pipe: left-to-right → double first, then addOne, then square
pipeline := pipe(double, addOne, square)
fmt.Println(pipeline(3)) // square(addOne(double(3))) = square(7) = 49
```

### Java

```java
Function<Integer, Integer> double_ = x -> x * 2;
Function<Integer, Integer> addOne = x -> x + 1;
Function<Integer, Integer> square = x -> x * x;

// compose: f.compose(g) = f(g(x))
Function<Integer, Integer> doubleAfterIncrement = double_.compose(addOne);
System.out.println(doubleAfterIncrement.apply(5)); // 12

// andThen: f.andThen(g) = g(f(x)) — left-to-right
Function<Integer, Integer> pipeline = double_.andThen(addOne).andThen(square);
System.out.println(pipeline.apply(3)); // 49

// Generic pipe
@SafeVarargs
public static <T> Function<T, T> pipe(Function<T, T>... funcs) {
    return Arrays.stream(funcs)
        .reduce(Function.identity(), Function::andThen);
}
```

### Python

```python
from functools import reduce

def compose(*funcs):
    """Right-to-left composition: compose(f, g)(x) = f(g(x))"""
    def composed(x):
        result = x
        for f in reversed(funcs):
            result = f(result)
        return result
    return composed

def pipe(*funcs):
    """Left-to-right composition: pipe(f, g)(x) = g(f(x))"""
    def piped(x):
        result = x
        for f in funcs:
            result = f(result)
        return result
    return piped

double = lambda x: x * 2
add_one = lambda x: x + 1
square = lambda x: x ** 2

double_after_increment = compose(double, add_one)
print(double_after_increment(5))  # 12

pipeline = pipe(double, add_one, square)
print(pipeline(3))  # 49
```

---

## Code Examples

### Example: Event Emitter Using Closures and Callbacks

#### Go

```go
type EventEmitter struct {
    handlers map[string][]func(interface{})
}

func NewEventEmitter() *EventEmitter {
    return &EventEmitter{handlers: make(map[string][]func(interface{}))}
}

func (e *EventEmitter) On(event string, handler func(interface{})) {
    e.handlers[event] = append(e.handlers[event], handler)
}

func (e *EventEmitter) Emit(event string, data interface{}) {
    for _, handler := range e.handlers[event] {
        handler(data)
    }
}

func main() {
    emitter := NewEventEmitter()
    emitter.On("message", func(data interface{}) {
        fmt.Println("Received:", data)
    })
    emitter.On("message", func(data interface{}) {
        fmt.Println("Logged:", data)
    })
    emitter.Emit("message", "hello world")
    // Received: hello world
    // Logged: hello world
}
```

#### Java

```java
public class EventEmitter {
    private Map<String, List<Consumer<Object>>> handlers = new HashMap<>();

    public void on(String event, Consumer<Object> handler) {
        handlers.computeIfAbsent(event, k -> new ArrayList<>()).add(handler);
    }

    public void emit(String event, Object data) {
        handlers.getOrDefault(event, List.of()).forEach(h -> h.accept(data));
    }

    public static void main(String[] args) {
        EventEmitter emitter = new EventEmitter();
        emitter.on("message", data -> System.out.println("Received: " + data));
        emitter.on("message", data -> System.out.println("Logged: " + data));
        emitter.emit("message", "hello world");
    }
}
```

#### Python

```python
from collections import defaultdict

class EventEmitter:
    def __init__(self):
        self.handlers = defaultdict(list)

    def on(self, event, handler):
        self.handlers[event].append(handler)

    def emit(self, event, data):
        for handler in self.handlers[event]:
            handler(data)

emitter = EventEmitter()
emitter.on("message", lambda data: print(f"Received: {data}"))
emitter.on("message", lambda data: print(f"Logged: {data}"))
emitter.emit("message", "hello world")
# Received: hello world
# Logged: hello world
```

### Example: Building a Validator Pipeline with Composition

#### Go

```go
type Validator func(string) error

func minLength(n int) Validator {
    return func(s string) error {
        if len(s) < n {
            return fmt.Errorf("must be at least %d chars", n)
        }
        return nil
    }
}

func maxLength(n int) Validator {
    return func(s string) error {
        if len(s) > n {
            return fmt.Errorf("must be at most %d chars", n)
        }
        return nil
    }
}

func matchesRegex(pattern string) Validator {
    re := regexp.MustCompile(pattern)
    return func(s string) error {
        if !re.MatchString(s) {
            return fmt.Errorf("must match pattern %s", pattern)
        }
        return nil
    }
}

func combine(validators ...Validator) Validator {
    return func(s string) error {
        for _, v := range validators {
            if err := v(s); err != nil {
                return err
            }
        }
        return nil
    }
}

// Usage
validateUsername := combine(
    minLength(3),
    maxLength(20),
    matchesRegex(`^[a-zA-Z0-9_]+$`),
)

fmt.Println(validateUsername("ab"))          // must be at least 3 chars
fmt.Println(validateUsername("valid_user"))  // <nil>
```

#### Java

```java
@FunctionalInterface
interface Validator {
    Optional<String> validate(String input);
}

static Validator minLength(int n) {
    return s -> s.length() < n
        ? Optional.of("must be at least " + n + " chars")
        : Optional.empty();
}

static Validator maxLength(int n) {
    return s -> s.length() > n
        ? Optional.of("must be at most " + n + " chars")
        : Optional.empty();
}

static Validator combine(Validator... validators) {
    return s -> Arrays.stream(validators)
        .map(v -> v.validate(s))
        .filter(Optional::isPresent)
        .findFirst()
        .orElse(Optional.empty());
}

// Usage
Validator validateUsername = combine(minLength(3), maxLength(20));
System.out.println(validateUsername.validate("ab"));           // Optional[must be at least 3 chars]
System.out.println(validateUsername.validate("valid_user"));   // Optional.empty
```

#### Python

```python
import re

def min_length(n):
    def validate(s):
        if len(s) < n:
            return f"must be at least {n} chars"
        return None
    return validate

def max_length(n):
    def validate(s):
        if len(s) > n:
            return f"must be at most {n} chars"
        return None
    return validate

def matches_regex(pattern):
    compiled = re.compile(pattern)
    def validate(s):
        if not compiled.match(s):
            return f"must match pattern {pattern}"
        return None
    return validate

def combine(*validators):
    def validate(s):
        for v in validators:
            error = v(s)
            if error:
                return error
        return None
    return validate

# Usage
validate_username = combine(
    min_length(3),
    max_length(20),
    matches_regex(r'^[a-zA-Z0-9_]+$')
)

print(validate_username("ab"))          # must be at least 3 chars
print(validate_username("valid_user"))  # None
```

---

## Performance Analysis

| Pattern | Time Overhead | Space Overhead | Notes |
|---------|--------------|----------------|-------|
| Higher-order functions | Minimal function call cost | Allocations for closures | Java streams have boxing cost for primitives |
| Closures | Heap allocation for captured vars | Small per closure | GC pressure in hot loops |
| Recursion | Function call per level | O(depth) stack | Risk of stack overflow |
| Composition (pipe) | One call per composed function | One closure per step | Negligible for small chains |
| Callbacks | One indirection | One allocation | Inline if possible |

**Rule of thumb**: Use these patterns for **clarity**. Optimize only when profiling shows a bottleneck.

---

## Summary

- **First-class functions** can be assigned, passed, and returned like any value
- **Higher-order functions** (map/filter/reduce) transform collections declaratively
- **Closures** capture variables from their enclosing scope — watch out for loop variable bugs
- **Lambdas/anonymous functions** are great for short, inline logic
- **Callbacks** let you inject behavior into generic functions
- **Recursion** excels at tree/graph problems; **iteration** is better for simple loops
- **Composition** chains small functions into powerful pipelines
