# Variables & Constants in Go — Find the Bug

> **Practice finding and fixing bugs in Go code related to Variables & Constants.**
> Each exercise contains buggy code — your job is to find the bug, explain why it happens, and fix it.

---

## How to Use

1. Read the buggy code carefully
2. Try to find the bug **without** looking at the hint
3. Write the fix yourself before checking the solution
4. Understand **why** the bug happens — not just how to fix it

### Difficulty Levels

| Level | Description |
|:-----:|:-----------|
| 🟢 | **Easy** — Common beginner mistakes, syntax-level bugs |
| 🟡 | **Medium** — Logic errors, subtle behavior, scoping and type issues |
| 🔴 | **Hard** — Race conditions, constant overflow, untyped constant precision edge cases |

---

## Bug 1: The Unused Variable 🟢

**What the code should do:** Declare a variable, assign a value, and print it.

```go
package main

import "fmt"

func main() {
	x := 42
	y := 10
	fmt.Println(x)
}
```

**Expected output:**
```
42
```

**Actual output:**
```
./main.go:7:2: y declared and not used
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The variable `y` is declared but never used. In Go, unused local variables cause a compilation error.

**Why it happens:** Go enforces a strict rule that every local variable must be used. This is a deliberate language design decision to keep code clean. Unlike C or Python, Go will refuse to compile if any declared local variable goes unreferenced.

**Fixed code:**

```go
package main

import "fmt"

func main() {
	x := 42
	fmt.Println(x)
}
```

**Key lesson:** Go does not allow unused local variables. Either use the variable or remove its declaration entirely. If you need to keep it temporarily, assign it to the blank identifier `_ = y`, but this is discouraged in production code.

</details>

---

## Bug 2: The Short Declaration Trap 🟢

**What the code should do:** Declare a string variable and print it.

```go
package main

import "fmt"

func main() {
	var message string
	message = "Hello, Go!"
	fmt.Println(message)

	var count int
	count = 5
	fmt.Println(count)

	var pi float64
	pi := 3.14159
	fmt.Println(pi)
}
```

**Expected output:**
```
Hello, Go!
5
3.14159
```

**Actual output:**
```
./main.go:15:5: no new variables on left side of :=
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The variable `pi` is first declared with `var pi float64` and then re-declared on the very next line using `:=`. The short declaration operator `:=` requires at least one new variable on the left side, but `pi` already exists in the same scope.

**Why it happens:** The `:=` operator both declares and assigns. If the variable already exists in the same scope and there are no other new variables on the left side, the compiler rejects it. This is different from `=`, which only assigns to an already-declared variable.

**Fixed code:**

```go
package main

import "fmt"

func main() {
	var message string
	message = "Hello, Go!"
	fmt.Println(message)

	var count int
	count = 5
	fmt.Println(count)

	var pi float64
	pi = 3.14159
	fmt.Println(pi)
}
```

**Key lesson:** Use `=` to assign to an existing variable. Use `:=` only when declaring a new variable for the first time in that scope.

</details>

---

## Bug 3: The Missing Initialization 🟢

**What the code should do:** Print the sum of two numbers.

```go
package main

import "fmt"

func main() {
	var a int
	var b int
	var sum int

	a = 10

	sum = a + b
	fmt.Printf("The sum of %d and %d is %d\n", a, b, sum)
}
```

**Expected output:**
```
The sum of 10 and 20 is 30
```

**Actual output:**
```
The sum of 10 and 0 is 10
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The variable `b` is declared but never assigned a value. In Go, numeric types are zero-initialized by default, so `b` is `0` instead of the intended `20`.

**Why it happens:** Go guarantees that all variables are initialized to their zero value when declared. For `int`, the zero value is `0`. The developer declared `b` and forgot to assign the intended value `20` to it. The code compiles and runs without error because `b` is technically "used" in the expression `a + b` — it just holds the wrong value.

**Fixed code:**

```go
package main

import "fmt"

func main() {
	var a int
	var b int
	var sum int

	a = 10
	b = 20

	sum = a + b
	fmt.Printf("The sum of %d and %d is %d\n", a, b, sum)
}
```

**Key lesson:** Go zero-initializes variables, which prevents garbage values but can mask missing assignments. Always verify that every variable receives its intended value before use.

</details>

---

## Bug 4: The Variable Shadow 🟡

**What the code should do:** Set a configuration value inside an if block and print it afterward.

```go
package main

import "fmt"

func main() {
	config := "default"

	userOverride := true
	if userOverride {
		config := "custom"
		fmt.Println("Config set to:", config)
	}

	fmt.Println("Using config:", config)
}
```

**Expected output:**
```
Config set to: custom
Using config: custom
```

**Actual output:**
```
Config set to: custom
Using config: default
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** Inside the `if` block, `config := "custom"` creates a new variable that shadows the outer `config`. The outer `config` remains `"default"` after the `if` block ends.

**Why it happens:** The `:=` operator inside the `if` block creates a brand new variable named `config` that is local to that block. This is called "variable shadowing." The inner `config` goes out of scope when the `if` block ends, and the outer `config` was never modified. Go does not warn about shadowing by default, making this a very common source of bugs.

**Fixed code:**

```go
package main

import "fmt"

func main() {
	config := "default"

	userOverride := true
	if userOverride {
		config = "custom" // Use = instead of := to modify the outer variable
		fmt.Println("Config set to:", config)
	}

	fmt.Println("Using config:", config)
}
```

**Key lesson:** Use `=` to assign to an existing variable in an outer scope. Use `:=` only when you intentionally want a new variable. Run `go vet -shadow` or use `staticcheck` to detect accidental shadowing.

</details>

---

## Bug 5: The Scope Escape 🟡

**What the code should do:** Declare a variable inside a loop and use it after the loop ends.

```go
package main

import "fmt"

func main() {
	for i := 0; i < 5; i++ {
		result := i * 2
		fmt.Println("Step:", result)
	}

	fmt.Println("Final result:", result)
}
```

**Expected output:**
```
Step: 0
Step: 2
Step: 4
Step: 6
Step: 8
Final result: 8
```

**Actual output:**
```
./main.go:11:29: undefined: result
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The variable `result` is declared inside the `for` loop body using `:=`. It only exists within that block scope. Attempting to access `result` outside the loop causes a compilation error.

**Why it happens:** In Go, every pair of curly braces `{}` introduces a new scope. Variables declared with `:=` inside a block (such as a for loop body) are scoped to that block. Once the loop ends, `result` no longer exists. This is different from some languages like Python where loop variables leak into the enclosing scope.

**Fixed code:**

```go
package main

import "fmt"

func main() {
	var result int
	for i := 0; i < 5; i++ {
		result = i * 2
		fmt.Println("Step:", result)
	}

	fmt.Println("Final result:", result)
}
```

**Key lesson:** If you need a variable after a block ends, declare it before the block. Variables created with `:=` inside `if`, `for`, or `switch` blocks are local to those blocks.

</details>

---

## Bug 6: The Constant Assignment 🟡

**What the code should do:** Define a constant and update it based on a condition.

```go
package main

import "fmt"

func main() {
	const maxRetries = 3

	fmt.Println("Max retries:", maxRetries)

	tooSlow := true
	if tooSlow {
		maxRetries = 5
	}

	fmt.Println("Adjusted max retries:", maxRetries)
}
```

**Expected output:**
```
Max retries: 3
Adjusted max retries: 5
```

**Actual output:**
```
./main.go:12:3: cannot assign to maxRetries (untyped int constant 3)
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The code attempts to reassign a value to `maxRetries`, which is declared as a `const`. Constants in Go are immutable and cannot be changed after declaration.

**Why it happens:** The `const` keyword creates a compile-time constant whose value is fixed forever. Unlike variables, constants cannot appear on the left side of an assignment. The developer should use a variable if the value needs to change at runtime.

**Fixed code:**

```go
package main

import "fmt"

func main() {
	maxRetries := 3

	fmt.Println("Max retries:", maxRetries)

	tooSlow := true
	if tooSlow {
		maxRetries = 5
	}

	fmt.Println("Adjusted max retries:", maxRetries)
}
```

**Key lesson:** Use `const` for values that truly never change (mathematical constants, configuration keys, enum-like values). Use `var` or `:=` for values that may be modified at runtime.

</details>

---

## Bug 7: The iota Reset 🟡

**What the code should do:** Define two separate sets of enumerated constants using `iota`.

```go
package main

import "fmt"

const (
	Red   = iota // 0
	Green        // 1
	Blue         // 2
)

const (
	Small  = iota // expected: 3
	Medium        // expected: 4
	Large         // expected: 5
)

func main() {
	fmt.Println("Blue:", Blue)
	fmt.Println("Small:", Small)
	fmt.Println("Medium:", Medium)
}
```

**Expected output:**
```
Blue: 2
Small: 3
Medium: 4
```

**Actual output:**
```
Blue: 2
Small: 0
Medium: 1
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** `iota` resets to `0` at the beginning of every new `const` block. The second `const` block starts `iota` from `0` again, so `Small` is `0`, `Medium` is `1`, and `Large` is `2` — not `3`, `4`, `5`.

**Why it happens:** According to the Go specification, `iota` represents the index of the current constant specification within a `const` block. Each new `const` block resets `iota` to `0`. There is no way to make `iota` continue from a previous block.

**Fixed code:**

```go
package main

import "fmt"

const (
	Red   = iota // 0
	Green        // 1
	Blue         // 2

	Small  = iota // 3
	Medium        // 4
	Large         // 5
)

func main() {
	fmt.Println("Blue:", Blue)
	fmt.Println("Small:", Small)
	fmt.Println("Medium:", Medium)
}
```

**Key lesson:** `iota` resets to `0` in each new `const` block. If you need continuous numbering, keep all constants in the same `const` block. Alternatively, offset manually: `Small = iota + 3` in a new block.

</details>

---

## Bug 8: The Silent Type Conversion 🟡

**What the code should do:** Divide two integers and print the result as a floating-point number.

```go
package main

import "fmt"

func main() {
	total := 7
	count := 2

	average := float64(total / count)
	fmt.Printf("Average: %.2f\n", average)
}
```

**Expected output:**
```
Average: 3.50
```

**Actual output:**
```
Average: 3.00
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The expression `total / count` performs integer division first (yielding `3`), and only then is the result `3` converted to `float64`, giving `3.00`. The fractional part is lost before the conversion happens.

**Why it happens:** Operator precedence and type rules in Go mean that `total / count` is evaluated as an integer expression because both operands are `int`. Integer division in Go truncates toward zero. The `float64()` conversion happens after the division, so it converts the already-truncated integer `3` to `3.00`.

**Fixed code:**

```go
package main

import "fmt"

func main() {
	total := 7
	count := 2

	average := float64(total) / float64(count)
	fmt.Printf("Average: %.2f\n", average)
}
```

**Key lesson:** Convert operands to `float64` before dividing, not after. `float64(a) / float64(b)` preserves the fractional part, while `float64(a / b)` does not.

</details>

---

## Bug 9: The Goroutine Shadow 🔴

**What the code should do:** Launch 5 goroutines that each print their own index.

```go
package main

import (
	"fmt"
	"sync"
)

func main() {
	var wg sync.WaitGroup

	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			fmt.Println("goroutine:", i)
		}()
	}

	wg.Wait()
}
```

**Expected output (any order):**
```
goroutine: 0
goroutine: 1
goroutine: 2
goroutine: 3
goroutine: 4
```

**Actual output (typical):**
```
goroutine: 5
goroutine: 5
goroutine: 5
goroutine: 5
goroutine: 5
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** All goroutines capture the same variable `i` by reference (closure), not by value. By the time the goroutines execute, the loop has finished and `i` is `5`.

**Why it happens:** A closure in Go captures variables by reference. The anonymous function inside the goroutine does not get a copy of `i` at the moment the goroutine is created. Instead, it holds a reference to the single loop variable `i`. Since goroutines typically start executing after the loop has advanced (or completed), they all see the final value of `i`, which is `5` (the value that caused the loop condition `i < 5` to fail).

**Fixed code:**

```go
package main

import (
	"fmt"
	"sync"
)

func main() {
	var wg sync.WaitGroup

	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func(n int) {
			defer wg.Done()
			fmt.Println("goroutine:", n)
		}(i) // Pass i as an argument to capture its current value
	}

	wg.Wait()
}
```

**Key lesson:** When launching goroutines inside a loop, pass the loop variable as a function argument to capture its value at that iteration. Note: starting with Go 1.22, the loop variable semantics changed so that each iteration gets its own copy of `i`, which fixes this issue. But for earlier Go versions, passing as an argument is essential.

</details>

---

## Bug 10: The Constant Overflow 🔴

**What the code should do:** Define a large constant and assign it to a variable.

```go
package main

import "fmt"

const bigNumber = 9999999999999999999

func main() {
	var n int32 = bigNumber
	fmt.Println("Big number:", n)
}
```

**Expected output:**
```
Big number: 9999999999999999999
```

**Actual output:**
```
./main.go:8:16: cannot use bigNumber (untyped int constant 9999999999999999999) as int32 value in variable declaration (overflows)
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The constant `bigNumber` has the value `9999999999999999999`, which exceeds the maximum value of `int32` (2,147,483,647). Go checks constant overflow at compile time and refuses to compile.

**Why it happens:** Go untyped integer constants have arbitrary precision at compile time, so declaring the constant itself is fine. However, when assigning it to a typed variable (`int32`), the compiler checks whether the value fits in that type. Since `9999999999999999999` is far larger than the `int32` maximum, the compiler reports an overflow error.

**Fixed code:**

```go
package main

import "fmt"

const bigNumber = 9999999999999999999

func main() {
	var n int64 = bigNumber
	fmt.Println("Big number:", n)
}
```

**Key lesson:** When working with large constants, use a type that can hold the value. `int32` holds up to ~2.1 billion, `int64` holds up to ~9.2 quintillion. Go catches constant overflows at compile time, which prevents subtle truncation bugs that occur in languages like C.

</details>

---

## Bug 11: The Untyped Constant Precision 🔴

**What the code should do:** Perform a high-precision calculation using constants and print the result.

```go
package main

import "fmt"

func main() {
	const a = 1.0 / 3.0
	var f float32 = a
	var d float64 = a

	fmt.Printf("float32: %.20f\n", f)
	fmt.Printf("float64: %.20f\n", d)

	const exact = 1.0 / 3.0
	fmt.Printf("direct:  %.20f\n", exact)

	if f == float32(d) {
		fmt.Println("float32 and float64 are equal after conversion")
	} else {
		fmt.Println("float32 and float64 differ after conversion")
	}
}
```

**Expected output:**
```
float32: 0.33333333333333333333
float64: 0.33333333333333333333
direct:  0.33333333333333333333
float32 and float64 are equal after conversion
```

**Actual output:**
```
float32: 0.33333334326744079590
float64: 0.33333333333333331483
direct:  0.33333333333333331483
float32 and float64 differ after conversion
```

<details>
<summary>Bug Explanation & Fix</summary>

**Bug:** The developer expects `float32` and `float64` to hold the same precision, and expects both to match the mathematically exact value. In reality, `float32` has roughly 7 decimal digits of precision while `float64` has roughly 15. When the untyped constant `a` (which has high precision at compile time) is assigned to `float32`, it is rounded to fit, losing significant precision.

**Why it happens:** Go untyped constants are computed with at least 256 bits of precision during compilation. However, when assigned to a typed variable, the value is truncated to fit that type's precision. `float32` uses 23 mantissa bits (~7 decimal digits) while `float64` uses 52 mantissa bits (~15 decimal digits). Comparing `f == float32(d)` converts `d` down to `float32` precision, but even then the two may differ because the rounding paths are different: `f` was rounded from the high-precision constant directly to `float32`, while `float32(d)` was first rounded to `float64` and then to `float32`.

**Fixed code:**

```go
package main

import (
	"fmt"
	"math"
)

func main() {
	const a = 1.0 / 3.0

	// Use float64 for maximum precision
	var d float64 = a
	fmt.Printf("float64: %.20f\n", d)

	// If you must compare floats, use an epsilon threshold
	var f float32 = a
	diff := math.Abs(float64(f) - d)
	fmt.Printf("float32: %.20f\n", f)
	fmt.Printf("Difference between float32 and float64: %e\n", diff)

	epsilon := 1e-7
	if diff < epsilon {
		fmt.Println("Values are approximately equal within float32 precision")
	} else {
		fmt.Println("Values differ beyond acceptable tolerance")
	}
}
```

**Key lesson:** Never compare floating-point numbers with `==`. Use an epsilon-based comparison. Be aware that untyped constants in Go have higher precision than any runtime float type. When you assign them to `float32` or `float64`, precision is lost. Always use `float64` unless memory constraints force `float32`.

</details>

---

## Score Card

Track your progress:

| Bug | Difficulty | Found without hint? | Understood why? | Fixed correctly? |
|:---:|:---------:|:-------------------:|:---------------:|:----------------:|
| 1 | 🟢 | ☐ | ☐ | ☐ |
| 2 | 🟢 | ☐ | ☐ | ☐ |
| 3 | 🟢 | ☐ | ☐ | ☐ |
| 4 | 🟡 | ☐ | ☐ | ☐ |
| 5 | 🟡 | ☐ | ☐ | ☐ |
| 6 | 🟡 | ☐ | ☐ | ☐ |
| 7 | 🟡 | ☐ | ☐ | ☐ |
| 8 | 🟡 | ☐ | ☐ | ☐ |
| 9 | 🔴 | ☐ | ☐ | ☐ |
| 10 | 🔴 | ☐ | ☐ | ☐ |
| 11 | 🔴 | ☐ | ☐ | ☐ |

### Rating:
- **11/11 without hints** → Senior-level debugging skills
- **8-10/11** → Solid middle-level understanding
- **5-7/11** → Good junior, keep practicing
- **< 5/11** → Review the topic fundamentals first
