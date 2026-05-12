# Hello World in Go — Find the Bug

> **Practice finding and fixing bugs in Go code related to Hello World in Go.**
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
| 🟡 | **Medium** — Logic errors, subtle behavior, concurrency issues |
| 🔴 | **Hard** — Race conditions, memory issues, compiler/runtime edge cases |

---

## Bug 1: The Silent Greeting 🟢

**What the code should do:** Print "Hello, World!" to the terminal.

```go
package main

import "fmt"

func main() {
	message := fmt.Sprintf("Hello, World!")
	_ = message
}
```

**Expected output:**
```
Hello, World!
```

**Actual output:**
```
(no output)
```

<details>
<summary>💡 Hint</summary>

Look at which `fmt` function is being used — does `fmt.Sprintf` actually print anything to the terminal?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `fmt.Sprintf` returns a formatted string but does not print it to stdout.
**Why it happens:** `fmt.Sprintf` is designed for string formatting and returns the result as a `string`. It never writes to any output stream. The variable `message` holds the correct string, but it is assigned to the blank identifier `_` and never printed.
**Impact:** The program runs and exits silently with no output at all.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

func main() {
	// Use fmt.Println to print to stdout
	fmt.Println("Hello, World!")
}
```

**What changed:** Replaced `fmt.Sprintf` (which returns a string) with `fmt.Println` (which prints to stdout).

</details>

---

## Bug 2: The Extra Formatting 🟢

**What the code should do:** Print the greeting "Hello, Alice! You are 25 years old."

```go
package main

import "fmt"

func main() {
	name := "Alice"
	age := 25
	fmt.Println("Hello, %s! You are %d years old.", name, age)
}
```

**Expected output:**
```
Hello, Alice! You are 25 years old.
```

**Actual output:**
```
Hello, %s! You are %d years old. Alice 25
```

<details>
<summary>💡 Hint</summary>

Look at the difference between `fmt.Println` and `fmt.Printf` — which one interprets format verbs like `%s` and `%d`?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `fmt.Println` does not interpret format verbs (`%s`, `%d`). It prints all arguments separated by spaces, with a newline at the end.
**Why it happens:** `fmt.Println` treats the format string as a plain string and appends `name` and `age` as additional arguments separated by spaces. Only `fmt.Printf` (and `fmt.Sprintf`, `fmt.Fprintf`) interpret format verbs.
**Impact:** The output contains literal `%s` and `%d` text followed by the raw values, instead of the properly formatted sentence.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

func main() {
	name := "Alice"
	age := 25
	// Use fmt.Printf for formatted output (note: Printf does not add a newline)
	fmt.Printf("Hello, %s! You are %d years old.\n", name, age)
}
```

**What changed:** Replaced `fmt.Println` with `fmt.Printf`, which interprets format verbs. Added `\n` because `fmt.Printf` does not append a newline automatically.

</details>

---

## Bug 3: The Wrong Verb 🟢

**What the code should do:** Print "Price: 19.99 dollars"

```go
package main

import "fmt"

func main() {
	price := 19.99
	fmt.Printf("Price: %d dollars\n", price)
}
```

**Expected output:**
```
Price: 19.99 dollars
```

**Actual output:**
```
Price: %!d(float64=19.99) dollars
```

<details>
<summary>💡 Hint</summary>

Look at the format verb `%d` — what type of value does it expect? What type is `price`?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The format verb `%d` is used for integers, but `price` is a `float64`.
**Why it happens:** When a format verb receives a value of an incompatible type, Go does not panic. Instead, it prints a diagnostic string like `%!d(float64=19.99)` to indicate the mismatch. The `%d` verb expects an integer type (`int`, `int64`, etc.), not a floating-point number.
**Impact:** The output contains an ugly diagnostic string instead of the properly formatted price.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

func main() {
	price := 19.99
	// Use %f or %.2f for floating-point numbers
	fmt.Printf("Price: %.2f dollars\n", price)
}
```

**What changed:** Replaced `%d` (integer verb) with `%.2f` (float verb with 2 decimal places) to match the `float64` type of `price`.

</details>

---

## Bug 4: The Vanishing Result 🟡

**What the code should do:** Build a greeting string and print it.

```go
package main

import "fmt"

func main() {
	name := "Bob"
	greeting := fmt.Sprintf("Welcome, %s!", name)
	fmt.Sprintf("Result: %s", greeting)
}
```

**Expected output:**
```
Result: Welcome, Bob!
```

**Actual output:**
```
(no output)
```

<details>
<summary>💡 Hint</summary>

Look at the second `fmt.Sprintf` call — what does `Sprintf` return, and is the return value being used?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The second `fmt.Sprintf` correctly formats the string but its return value is discarded. `fmt.Sprintf` never prints to stdout — it only returns a string.
**Why it happens:** The developer likely confused `fmt.Sprintf` with `fmt.Printf`. The first `Sprintf` is used correctly (its return value is stored in `greeting`), but the second one's return value is simply thrown away. Go allows discarding return values from non-assignment function calls without error.
**Impact:** The program compiles and runs successfully but produces absolutely no output.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

func main() {
	name := "Bob"
	greeting := fmt.Sprintf("Welcome, %s!", name)
	// Use fmt.Printf or fmt.Println to actually print
	fmt.Printf("Result: %s\n", greeting)
}
```

**What changed:** Replaced the second `fmt.Sprintf` with `fmt.Printf` so the result is actually printed to stdout.

</details>

---

## Bug 5: The Newline Surprise 🟡

**What the code should do:** Print three lines of a poem, each on its own line.

```go
package main

import "fmt"

func main() {
	fmt.Printf("Roses are red\n")
	fmt.Printf("Violets are blue\n")
	fmt.Printf("Go is great\n")
	fmt.Printf("And so are you")
	fmt.Println()

	// Count and print total lines
	lines := 4
	fmt.Printf("Total: %d lines", lines)
	fmt.Println(" of poetry")
}
```

**Expected output:**
```
Roses are red
Violets are blue
Go is great
And so are you
Total: 4 lines of poetry
```

**Actual output:**
```
Roses are red
Violets are blue
Go is great
And so are you
Total: 4 lines of poetry
```

<details>
<summary>💡 Hint</summary>

Look very closely at the spacing — run the program and check if there is an extra blank line between the poem and the total. What does `fmt.Println()` with no arguments do?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `fmt.Println()` called with no arguments prints just a newline character. Combined with the `\n` at the end of the "And so are you" `Printf`, this results in a double newline — creating an unexpected blank line between the poem and the total.
**Why it happens:** The last poem line `fmt.Printf("And so are you")` has no trailing `\n`, so `fmt.Println()` is used to add the newline. But `fmt.Println()` always prints a newline. Since the developer added `\n` to the previous three `Printf` calls but not the fourth, the behavior is inconsistent: the `Println()` adds the missing newline, which is correct here. However, the actual bug is that `fmt.Printf("Total: %d lines", lines)` followed by `fmt.Println(" of poetry")` inserts a space — `Println` adds a space between its internal arguments but here it is called separately, so no extra space is added. Actually the output looks correct at first glance, but there is a blank line.

Wait — let me re-examine. `fmt.Printf("And so are you")` prints without newline. Then `fmt.Println()` prints `\n`. Then `fmt.Printf("Total: %d lines", lines)` prints `Total: 4 lines`. Then `fmt.Println(" of poetry")` prints ` of poetry\n`. This produces the expected output. The real bug: `fmt.Println()` with no args just prints a newline, which is actually correct here. Let me reconsider the actual bug.

**Bug:** Actually there is no blank line issue. The real subtle bug is that `fmt.Println(" of poetry")` starts with a space, so the output reads `Total: 4 lines of poetry` with a space before "of" — which looks correct. The code actually works as expected in this specific case. Disregard this analysis — see the corrected explanation below.

**Revised Bug:** The code produces correct output by coincidence, but if the developer intended `fmt.Printf` + `fmt.Println` to seamlessly join, they should know that `fmt.Println` always appends a newline. The program works here but the pattern is fragile.

**Impact:** Misleading code pattern that works by accident.

</details>

Let me replace this bug with a better one.

---

## Bug 5: The Scanf Trap 🟡

**What the code should do:** Read a user's full name and print a greeting.

```go
package main

import "fmt"

func main() {
	var firstName, lastName string
	fmt.Print("Enter your full name: ")
	fmt.Scanf("%s %s", &firstName, &lastName)
	fmt.Printf("Hello, %s %s!\n", firstName, lastName)
}
```

**Expected output (when user types "John Doe"):**
```
Enter your full name: John Doe
Hello, John Doe!
```

**Actual output (on some systems):**
```
Enter your full name: John Doe
Hello, John !
```

<details>
<summary>💡 Hint</summary>

`fmt.Scanf` on some platforms (especially Windows and certain terminals) has issues with reading multiple space-separated values. Look at how `Scanf` handles newlines vs spaces.

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `fmt.Scanf` has platform-dependent behavior with whitespace and newlines. On some systems (notably Windows), `Scanf` stops reading after the first whitespace-delimited token and the newline is not consumed properly, leaving `lastName` as an empty string.
**Why it happens:** Go's `fmt.Scanf` is known to have inconsistent behavior across platforms. On Unix-like systems, `Scanf("%s %s", ...)` may work correctly, but on Windows it often fails because the newline character `\r\n` interferes with parsing. The space in the format string matches any whitespace including newlines, causing the second `%s` to sometimes not match.
**Impact:** The program works on some platforms but silently fails on others, printing an incomplete greeting with a missing last name.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func main() {
	reader := bufio.NewReader(os.Stdin)
	fmt.Print("Enter your full name: ")
	// ReadString reads until the delimiter (newline)
	input, _ := reader.ReadString('\n')
	input = strings.TrimSpace(input)
	parts := strings.SplitN(input, " ", 2)

	firstName := parts[0]
	lastName := ""
	if len(parts) > 1 {
		lastName = parts[1]
	}
	fmt.Printf("Hello, %s %s!\n", firstName, lastName)
}
```

**What changed:** Replaced `fmt.Scanf` with `bufio.NewReader` for reliable cross-platform input reading. Used `strings.TrimSpace` to handle `\r\n` on Windows.

</details>

---

## Bug 6: The init() Order Confusion 🟡

**What the code should do:** Print messages in order: first "Setting up...", then "Hello, World!".

```go
package main

import "fmt"

var message string

func init() {
	message = "Hello, World!"
	fmt.Println("Setting up...")
}

func main() {
	fmt.Println("Starting program...")
	fmt.Println(message)
}
```

**Expected output:**
```
Setting up...
Hello, World!
```

**Actual output:**
```
Setting up...
Starting program...
Hello, World!
```

<details>
<summary>💡 Hint</summary>

The developer forgot about the extra `fmt.Println("Starting program...")` line in `main()`. But also — think about when `init()` runs relative to `main()`. Is the output order surprising?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The developer expected only two lines of output but gets three. The `fmt.Println("Starting program...")` line in `main()` was likely added during debugging and forgotten. While `init()` does run before `main()` (which is correct), the unintended "Starting program..." line appears between the setup message and the actual greeting.
**Why it happens:** `init()` functions in Go are guaranteed to run before `main()`. The developer likely intended `main()` to only print `message`, but the debugging print statement was left in. This is a logic bug — the code compiles and runs, but the output does not match the specification.
**Impact:** Extra unexpected output that breaks the expected two-line format.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

var message string

func init() {
	message = "Hello, World!"
	fmt.Println("Setting up...")
}

func main() {
	// Removed the extra "Starting program..." print
	fmt.Println(message)
}
```

**What changed:** Removed the unintended `fmt.Println("Starting program...")` from `main()` so the output matches the expected two-line format.

</details>

---

## Bug 7: The Swapped Arguments 🟡

**What the code should do:** Print user info in the format "Name: Alice, Age: 30".

```go
package main

import "fmt"

func main() {
	name := "Alice"
	age := 30
	fmt.Printf("Name: %s, Age: %d\n", age, name)
}
```

**Expected output:**
```
Name: Alice, Age: 30
```

**Actual output:**
```
Name: %!s(int=30), Age: %!d(string=Alice)
```

<details>
<summary>💡 Hint</summary>

Look at the order of arguments passed to `fmt.Printf` — do they match the order of format verbs in the format string?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** The arguments `age` and `name` are passed in the wrong order. The format string expects a string (`%s`) first and an integer (`%d`) second, but `age` (int) is passed first and `name` (string) is passed second.
**Why it happens:** Go's `fmt.Printf` does not enforce type checking at compile time for format verbs. When `%s` receives an `int`, it prints `%!s(int=30)` as a diagnostic. When `%d` receives a `string`, it prints `%!d(string=Alice)`. The code compiles without errors because `Printf` accepts `...interface{}` arguments.
**Impact:** The output contains ugly diagnostic strings instead of the properly formatted user info. This bug is especially common when refactoring code and reordering format string parameters.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

func main() {
	name := "Alice"
	age := 30
	// Arguments must match the order of format verbs
	fmt.Printf("Name: %s, Age: %d\n", name, age)
}
```

**What changed:** Swapped `age, name` to `name, age` so the arguments match the order of `%s` and `%d` in the format string.

</details>

---

## Bug 8: The os.Exit Trap 🔴

**What the code should do:** Print "Hello, World!", then clean up by printing "Goodbye!" before exiting.

```go
package main

import (
	"fmt"
	"os"
)

func main() {
	defer fmt.Println("Goodbye!")

	fmt.Println("Hello, World!")
	os.Exit(0)
}
```

**Expected output:**
```
Hello, World!
Goodbye!
```

**Actual output:**
```
Hello, World!
```

<details>
<summary>💡 Hint</summary>

Think about what `os.Exit` does to the program — does it give deferred functions a chance to run?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `os.Exit` terminates the program immediately without running any deferred functions.
**Why it happens:** According to the Go specification, `os.Exit` causes the program to exit with the given status code immediately. Unlike a normal return from `main()`, it does not unwind the stack and does not execute deferred function calls. The `defer fmt.Println("Goodbye!")` is registered but never executed.
**Impact:** Cleanup code in deferred functions is silently skipped. In production, this can lead to unflushed buffers, unclosed file handles, unreleased locks, and incomplete transactions.
**Go spec reference:** The `os.Exit` documentation states: "The program terminates immediately; deferred functions are not run."

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import "fmt"

func main() {
	defer fmt.Println("Goodbye!")

	fmt.Println("Hello, World!")
	// Simply return from main instead of calling os.Exit
	// Deferred functions will execute normally
}
```

**What changed:** Removed `os.Exit(0)` and let `main()` return naturally, allowing deferred functions to execute.
**Alternative fix:** If an exit code must be set, restructure the program to use a separate `run()` function that returns an exit code, and call `os.Exit` after all cleanup:

```go
func run() int {
    defer fmt.Println("Goodbye!")
    fmt.Println("Hello, World!")
    return 0
}

func main() {
    os.Exit(run())
}
```

Note: Even in this alternative, the `defer` inside `run()` executes before `run()` returns, but `os.Exit` is called after.

</details>

---

## Bug 9: The Stderr Surprise 🔴

**What the code should do:** Print "Hello, World!" to standard output so it can be captured by shell redirection.

```go
package main

import (
	"fmt"
	"os"
)

func main() {
	fmt.Fprintf(os.Stderr, "Hello, World!\n")
}
```

**Expected output (when running `go run main.go > output.txt`):**
```
(output.txt should contain "Hello, World!")
```

**Actual output:**
```
(output.txt is empty, "Hello, World!" appears on the terminal)
```

<details>
<summary>💡 Hint</summary>

Look at the first argument to `fmt.Fprintf` — where is it writing to? What is the difference between `os.Stdout` and `os.Stderr`?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** `fmt.Fprintf` is writing to `os.Stderr` instead of `os.Stdout`.
**Why it happens:** `fmt.Fprintf` takes an `io.Writer` as its first argument and writes to that destination. `os.Stderr` is the standard error stream, which is separate from `os.Stdout`. When shell redirection (`>`) is used, only stdout is redirected to the file — stderr still goes to the terminal. The program appears to work when run normally (both stderr and stdout appear on the terminal), but fails when output is captured.
**Impact:** The output cannot be captured by standard shell redirection (`>`). Piping (`|`) also fails to capture the output. This is especially insidious because the program appears to work correctly during manual testing but fails in scripts and automation.
**How to detect:** Run the program with output redirection: `go run main.go > /dev/null` — if "Hello, World!" still appears, it is being written to stderr.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"fmt"
	"os"
)

func main() {
	// Write to os.Stdout so output can be captured by shell redirection
	fmt.Fprintf(os.Stdout, "Hello, World!\n")
}
```

**What changed:** Changed `os.Stderr` to `os.Stdout` so the output goes to the standard output stream.
**Alternative fix:** Use `fmt.Println("Hello, World!")` or `fmt.Printf("Hello, World!\n")` which write to stdout by default.

</details>

---

## Bug 10: The Race in Concurrent Hello 🔴

**What the code should do:** Print "Hello" and "World" on separate lines using goroutines, always in that order.

```go
package main

import (
	"fmt"
	"sync"
)

func main() {
	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		defer wg.Done()
		fmt.Println("Hello")
	}()

	go func() {
		defer wg.Done()
		fmt.Println("World")
	}()

	wg.Wait()
}
```

**Expected output:**
```
Hello
World
```

**Actual output:**
```
World
Hello
(or sometimes: Hello\nWorld — order is unpredictable)
```

<details>
<summary>💡 Hint</summary>

Run with `go run -race main.go` multiple times. Are goroutines guaranteed to execute in the order they are launched?

</details>

<details>
<summary>🐛 Bug Explanation</summary>

**Bug:** Two goroutines are launched concurrently with no synchronization to enforce execution order. The Go scheduler does not guarantee that goroutines run in the order they are created.
**Why it happens:** When `go func()` launches a goroutine, it is placed on a run queue, but the scheduler decides when each goroutine actually executes. The second goroutine may run before the first, causing "World" to print before "Hello". The `sync.WaitGroup` only ensures both goroutines complete before `main` returns — it does not control their execution order.
**Impact:** Non-deterministic output order. The program may appear to work correctly in testing (goroutines often run in launch order on single-core machines or under light load) but fail unpredictably in production under different scheduling conditions.

</details>

<details>
<summary>✅ Fixed Code</summary>

```go
package main

import (
	"fmt"
	"sync"
)

func main() {
	var wg sync.WaitGroup
	wg.Add(2)

	// Use a channel to enforce ordering
	ready := make(chan struct{})

	go func() {
		defer wg.Done()
		fmt.Println("Hello")
		close(ready) // Signal that "Hello" has been printed
	}()

	go func() {
		defer wg.Done()
		<-ready // Wait until "Hello" is printed
		fmt.Println("World")
	}()

	wg.Wait()
}
```

**What changed:** Added a channel `ready` to synchronize the two goroutines. The second goroutine waits on the channel until the first goroutine signals (by closing it) that "Hello" has been printed.
**Alternative fix:** If ordering is required, the simplest fix is to not use goroutines at all — just call `fmt.Println("Hello")` and `fmt.Println("World")` sequentially.

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
| 8 | 🔴 | ☐ | ☐ | ☐ |
| 9 | 🔴 | ☐ | ☐ | ☐ |
| 10 | 🔴 | ☐ | ☐ | ☐ |

### Rating:
- **10/10 without hints** → Senior-level debugging skills
- **7-9/10** → Solid middle-level understanding
- **4-6/10** → Good junior, keep practicing
- **< 4/10** → Review the topic fundamentals first
