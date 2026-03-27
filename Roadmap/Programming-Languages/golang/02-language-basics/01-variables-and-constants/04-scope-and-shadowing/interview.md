# Scope and Shadowing — Interview Questions

## Table of Contents

1. [Junior Level Questions](#junior-level-questions)
2. [Middle Level Questions](#middle-level-questions)
3. [Senior Level Questions](#senior-level-questions)
4. [Scenario-Based Questions](#scenario-based-questions)
5. [FAQ](#faq)

---

## Junior Level Questions

### Q1: What is variable scope in Go?

**Answer:**

Variable scope is the region of code where a variable is visible and accessible. In Go, scope is determined by where a variable is declared:

- **Package scope** — declared outside any function, visible to all files in the package
- **Function scope** — declared inside a function, visible only within that function
- **Block scope** — declared inside `{}` (if, for, switch), visible only within those braces

```go
var packageVar = "visible everywhere in package" // package scope

func main() {
    funcVar := "visible in main only" // function scope

    if true {
        blockVar := "visible in this if block only" // block scope
        fmt.Println(blockVar) // OK
    }
    // fmt.Println(blockVar) // ERROR: undefined
    fmt.Println(funcVar)     // OK
}
```

---

### Q2: What is variable shadowing? Give an example.

**Answer:**

Variable shadowing occurs when a variable declared in an inner scope has the same name as a variable in an outer scope. The inner variable "hides" the outer one within its scope.

```go
func main() {
    x := 10
    fmt.Println(x) // 10

    if true {
        x := 20 // shadows the outer x
        fmt.Println(x) // 20
    }

    fmt.Println(x) // 10 — outer x was never changed
}
```

The inner `x := 20` creates a completely new variable. The outer `x` is untouched.

---

### Q3: What is the difference between exported and unexported identifiers in Go?

**Answer:**

- **Exported**: Starts with an uppercase letter. Visible from other packages.
- **Unexported**: Starts with a lowercase letter. Visible only within the declaring package.

```go
package mylib

var PublicVar = "accessible from other packages"   // exported
var privateVar = "only accessible within mylib"     // unexported

func PublicFunc() {}  // exported
func privateFunc() {} // unexported
```

This is Go's access control mechanism — there are no `public`/`private` keywords.

---

### Q4: What is the universe block in Go?

**Answer:**

The universe block is the outermost scope containing all predeclared (built-in) identifiers:

- Constants: `true`, `false`, `iota`
- Zero value: `nil`
- Types: `int`, `string`, `bool`, `error`, `byte`, `rune`, etc.
- Functions: `len`, `cap`, `make`, `new`, `append`, `copy`, `delete`, `panic`, `recover`, `print`, `println`

These are available everywhere without importing anything.

```go
func main() {
    fmt.Println(len("hello")) // len is from the universe block
    fmt.Println(true)          // true is from the universe block
}
```

---

### Q5: Can you shadow built-in identifiers like `true` or `len`?

**Answer:**

Yes, Go allows it, but it is dangerous and strongly discouraged:

```go
func main() {
    len := 42          // shadows built-in len function
    fmt.Println(len)   // 42
    // fmt.Println(len("hello")) // ERROR: len is int, not a function

    true := "yes"      // shadows built-in true constant
    fmt.Println(true)  // "yes"
}
```

The compiler does not warn about this. Use `go vet` with the shadow analyzer to detect such issues.

---

### Q6: What will this code print?

```go
func main() {
    x := 1
    x, y := 2, 3
    fmt.Println(x, y)
}
```

**Answer:**

It prints `2 3`. The `:=` operator has a special rule: when at least one variable on the left is new (here, `y`), existing variables (here, `x`) are reused (assigned to) rather than redeclared. So `x` is updated to 2, and `y` is created with value 3.

---

### Q7: What is the scope of a variable declared in the init part of an `if` statement?

**Answer:**

The variable is scoped to the entire `if-else` chain, including the `else` block, but not outside:

```go
func main() {
    if x := compute(); x > 0 {
        fmt.Println("positive:", x) // x is accessible
    } else {
        fmt.Println("non-positive:", x) // x is also accessible here
    }
    // fmt.Println(x) // ERROR: x is out of scope
}
```

---

### Q8: Why is shadowing `err` in error handling dangerous?

**Answer:**

Because the outer `err` variable is not modified, and error information is lost:

```go
func main() {
    var err error

    if true {
        err := riskyOperation() // NEW err — shadows outer one
        fmt.Println("inner:", err)
    }

    fmt.Println("outer:", err) // nil — the error was lost!
}
```

**Fix:** Use `=` instead of `:=` to assign to the outer `err`.

---

### Q9: Are variables from a `for` loop visible outside the loop?

**Answer:**

No. Variables declared in the loop header (init statement) and loop body are scoped to the loop:

```go
for i := 0; i < 5; i++ {
    temp := i * 2
    fmt.Println(temp)
}
// fmt.Println(i)    // ERROR: undefined
// fmt.Println(temp) // ERROR: undefined
```

---

### Q10: How do you detect variable shadowing in Go?

**Answer:**

Use the `shadow` analyzer:

```bash
# Install
go install golang.org/x/tools/go/analysis/passes/shadow/cmd/shadow@latest

# Run
go vet -vettool=$(which shadow) ./...
```

Or use `golangci-lint`:

```bash
golangci-lint run --enable govet
```

The Go compiler itself does NOT warn about shadowing.

---

## Middle Level Questions

### Q1: Explain the `:=` reuse rule in detail. When does it shadow vs reuse?

**Answer:**

The `:=` operator follows this rule:
- In a **multi-variable** declaration, if **at least one** variable on the left is **new** in the **current scope**, existing variables in the **same scope** are **reused** (assigned to).
- If the existing variable is in an **outer scope**, `:=` creates a **new** variable (shadowing).

```go
func main() {
    x := 1
    // Same scope — x is REUSED (y is new)
    x, y := 2, 3
    fmt.Println(x, y) // 2 3

    // Inner scope — x is SHADOWED (new x)
    if true {
        x, z := 10, 20
        fmt.Println(x, z) // 10 20
    }
    fmt.Println(x) // 2 — outer x unchanged
}
```

---

### Q2: How does scope affect closures and goroutines?

**Answer:**

Closures capture variables by **reference**, not by value. This means:

```go
func main() {
    values := []int{1, 2, 3}

    // Pre-Go 1.22 BUG: all goroutines share the same 'v'
    for _, v := range values {
        go func() {
            fmt.Println(v) // may print 3, 3, 3
        }()
    }

    // Fix 1: Intentional shadowing
    for _, v := range values {
        v := v // shadow v with a per-iteration copy
        go func() {
            fmt.Println(v) // correct: 1, 2, 3 (any order)
        }()
    }

    // Fix 2: Pass as argument
    for _, v := range values {
        go func(val int) {
            fmt.Println(val)
        }(v)
    }
}
```

Go 1.22+ fixes this for `for range` loops by creating per-iteration variables.

---

### Q3: What is the difference between file scope and package scope?

**Answer:**

- **Package scope**: Variables and functions declared at the top level are visible across ALL files in the package.
- **File scope**: Only `import` declarations are file-scoped. Each file must import its own dependencies.

```go
// file1.go
package mypackage
import "fmt"       // file-scoped: only usable in file1.go
var Shared = "yes" // package-scoped: usable in all files

// file2.go
package mypackage
import "fmt"       // must import separately
func Use() {
    fmt.Println(Shared) // OK — Shared is package-scoped
}
```

---

### Q4: What happens when you shadow named return values?

**Answer:**

Named return values are function-scoped. Shadowing them inside a block means the named return is not modified:

```go
func divide(a, b int) (result int, err error) {
    if b == 0 {
        err := fmt.Errorf("division by zero") // SHADOWS named err
        _ = err
        return // returns result=0, err=nil — BUG!
    }
    result = a / b
    return
}
```

Fix: use `err = fmt.Errorf(...)` (assignment, not declaration).

---

### Q5: How does type switch interact with scope?

**Answer:**

In a type switch, the switch variable is rebound in each case with the concrete type:

```go
func describe(v interface{}) string {
    switch v := v.(type) {
    case int:
        return fmt.Sprintf("int: %d", v*2)     // v is int here
    case string:
        return fmt.Sprintf("string: %s", v)     // v is string here
    case bool:
        return fmt.Sprintf("bool: %t", v)       // v is bool here
    default:
        return fmt.Sprintf("unknown: %v", v)    // v is interface{}
    }
}
```

Each `case` creates a new `v` with the concrete type — this is intentional shadowing by design.

---

### Q6: Can you shadow an imported package name?

**Answer:**

Yes, and it is a common source of confusion:

```go
import "fmt"

func main() {
    fmt := "I am a string now" // shadows the fmt package
    fmt.Println("hello")       // ERROR: fmt is string, has no method Println
    _ = fmt
}
```

This compiles but fails when you try to use the package. Avoid naming local variables the same as imported packages.

---

### Q7: How does `select` interact with scope?

**Answer:**

Each `case` in a `select` statement has its own scope:

```go
result := 0
select {
case result := <-ch: // SHADOWS outer result
    fmt.Println(result) // inner result
case err := <-errCh:
    fmt.Println(err)    // scoped to this case
}
fmt.Println(result) // 0 — outer result unchanged
```

To modify the outer `result`, use `=`:

```go
select {
case result = <-ch: // modifies outer result
    // ...
}
```

---

### Q8: What tools exist for detecting scope-related issues?

**Answer:**

| Tool | What It Detects |
|------|----------------|
| `go vet -shadow` | Variable shadowing |
| `golangci-lint` | Shadowing, unused variables, scope depth |
| `gocyclo` | Cyclomatic complexity (related to nesting) |
| `go build -gcflags="-m"` | Escape analysis (scope → allocation) |
| `go test -race` | Race conditions (often scope-related in closures) |
| `staticcheck` | Various scope-related anti-patterns |

---

### Q9: Explain the scope of variables in a `switch` statement.

**Answer:**

```go
switch x := getValue(); {
case x > 100:
    label := "high"  // scoped to this case
    fmt.Println(label)
case x > 50:
    label := "medium" // scoped to this case (different variable)
    fmt.Println(label)
default:
    label := "low"    // scoped to this case
    fmt.Println(label)
}
// x is out of scope here
// label is out of scope here (all three versions)
```

The init variable `x` is scoped to the entire `switch`. Each `case` body has its own scope.

---

### Q10: What changed in Go 1.22 regarding loop variable scope?

**Answer:**

Before Go 1.22, `for` loop variables were scoped to the entire loop (single variable, reused each iteration). This caused the famous goroutine capture bug.

Go 1.22 changed this: each iteration creates a **new** copy of the loop variable. This applies when:
- The module's `go.mod` has `go 1.22` or later
- Applies to both `for range` and classic `for i := 0; i < n; i++` loops

```go
// Go 1.22+: each iteration gets its own 'i'
for i := 0; i < 3; i++ {
    go func() {
        fmt.Println(i) // prints 0, 1, 2 (in some order)
    }()
}
```

---

## Senior Level Questions

### Q1: How does scope affect escape analysis?

**Answer:**

Scope is a primary input to escape analysis. The compiler analyzes whether a variable's address outlives its declaring scope:

```go
func noEscape() int {
    x := 42    // x stays on stack — scope ends with function
    return x   // value copy
}

func escapes() *int {
    x := 42     // x escapes to heap — pointer outlives function scope
    return &x
}

func closureEscape() {
    x := 42
    go func() {
        fmt.Println(x) // x captured by goroutine — escapes
    }()
}
```

Verify with: `go build -gcflags="-m" ./...`

Variables in narrow scopes that do not have their address taken are strong candidates for stack allocation.

---

### Q2: How does the Go compiler represent scope in SSA form?

**Answer:**

SSA (Static Single Assignment) eliminates the concept of scope entirely. Each variable assignment creates a new SSA value:

```go
// Source
x := 10       // SSA: v1 = Const 10
if cond {
    x := 20   // SSA: v2 = Const 20 (completely separate from v1)
    return x   // SSA: Return v2
}
return x       // SSA: Return v1
```

There is no shadowing in SSA — v1 and v2 are independent values. The phi functions at join points handle the correct selection. Shadowing is purely a source-level concept that disappears after SSA construction.

---

### Q3: How does stack slot reuse work for scoped variables?

**Answer:**

The compiler uses an interference graph and graph coloring to reuse stack slots:

1. **Build liveness info** — determine the live range of each variable
2. **Build interference graph** — variables whose live ranges overlap interfere
3. **Color the graph** — assign stack slots (colors) to variables, minimizing total slots
4. **Non-overlapping scopes** — variables in sequential blocks can share slots

```go
func reuse() {
    {
        a := compute() // slot offset -8
        process(a)
    } // a is dead
    {
        b := compute() // reuses slot offset -8
        process(b)
    }
}
```

This reduces stack frame size and improves cache performance.

---

### Q4: Describe a production incident caused by variable shadowing.

**Answer:**

A classic incident: payment processing where `err` was shadowed:

```go
func processPayment(orderID string) error {
    var err error

    if tx, err := db.Begin(); err == nil {
        _, err := tx.Exec("UPDATE ...", orderID) // shadows tx-level err
        if err != nil {
            tx.Rollback()
            return err
        }
        err = tx.Commit() // modifies inner err, not outer
    }
    return err // outer err is nil even if Begin() or Commit() failed
}
```

**Impact:** Payments appeared successful but were not committed. **Fix:** Flat error handling with early returns. **Prevention:** `go vet -shadow` in CI.

---

### Q5: How does the `internal` package convention enforce scope?

**Answer:**

The Go toolchain enforces that packages under `internal/` can only be imported by code in the parent directory tree:

```
project/
├── internal/
│   └── auth/        # only project/ code can import
├── pkg/
│   └── api/         # anyone can import
└── cmd/
    └── server/      # can import internal/auth
```

This is a **build-time** scope restriction — the compiler/linker rejects imports from outside the allowed tree. It provides module-level encapsulation without language changes.

---

### Q6: How does scope interact with defer and panic/recover?

**Answer:**

Deferred functions access variables from the enclosing scope by reference (closures) or by value (direct calls):

```go
func safeDiv(a, b int) (result int, err error) {
    defer func() {
        if r := recover(); r != nil {
            // r is scoped to this deferred closure
            // Named returns can be modified here
            err = fmt.Errorf("panic: %v", r)
            result = 0
        }
    }()

    return a / b, nil // if b==0, panic occurs, defer catches it
}
```

Critical: named return values are function-scoped, so the deferred function can modify them. If `err` were shadowed inside the defer, the named return would not be set correctly.

---

### Q7: How would you design a code review bot that detects scope anti-patterns?

**Answer:**

Use the `go/ast` and `go/types` packages to build a custom analyzer:

```go
package analyzer

import (
    "go/ast"
    "go/types"
    "golang.org/x/tools/go/analysis"
)

var Analyzer = &analysis.Analyzer{
    Name: "scopecheck",
    Doc:  "checks for scope anti-patterns",
    Run:  run,
}

func run(pass *analysis.Pass) (interface{}, error) {
    for _, file := range pass.Files {
        ast.Inspect(file, func(n ast.Node) bool {
            switch node := n.(type) {
            case *ast.IfStmt:
                checkNestingDepth(pass, node, 0)
            case *ast.AssignStmt:
                if node.Tok == token.DEFINE {
                    checkShadowing(pass, node)
                }
            }
            return true
        })
    }
    return nil, nil
}
```

Integrate via `golangci-lint` custom plugin or as a standalone `go vet` analyzer.

---

### Q8: How does scope affect inlining decisions?

**Answer:**

The Go compiler uses a cost model for inlining. Each AST node has a cost. Deeply nested scopes increase the total cost:

- `if/else`: ~2 each
- `for`: ~5
- `range`: ~5
- Variable declaration: ~1
- Function call: depends on callee

Functions with total cost exceeding ~80 are not inlined. A function with many scoped blocks and local variables will have higher cost.

Check: `go build -gcflags="-m -m" ./... 2>&1 | grep "cannot inline"`

Refactoring to extract inner scopes into separate (inlineable) functions can improve performance.

---

### Q9: How do you handle scope in generated code?

**Answer:**

Code generators must produce scope-correct Go code:

1. Use unique variable names to avoid shadowing: `__gen_var_001`
2. Use bare blocks `{}` to isolate generated sections
3. Use `_` prefix convention for generated helper variables
4. Test generated code with `go vet -shadow`

```go
// Generated code pattern
func generatedHandler(w http.ResponseWriter, r *http.Request) {
    { // generated block — isolated scope
        _genVar1 := extractParam(r, "id")
        _genVar2, _genErr := strconv.Atoi(_genVar1)
        if _genErr != nil {
            http.Error(w, _genErr.Error(), 400)
            return
        }
        // ... use _genVar2
    }
}
```

---

### Q10: Compare scope mechanisms across Go, Rust, and Java.

**Answer:**

| Feature | Go | Rust | Java |
|---------|-----|------|------|
| Block scope | `{}` | `{}` | `{}` |
| Shadowing | Allowed (no warning) | Encouraged (`let x = x + 1`) | Error (same scope), warning (nested) |
| Access control | Uppercase/lowercase | `pub`, `pub(crate)`, `pub(super)` | `public`, `private`, `protected`, package-private |
| Scope = lifetime | No (GC handles lifetime) | Yes (ownership system) | No (GC handles lifetime) |
| Module scope | `internal/` directory | `mod` + visibility | Package + module system (Java 9+) |
| Loop variable | Per-iteration (1.22+) | Per-iteration (always) | Per-iteration (always) |
| Closure capture | By reference (always) | By ref, by move (explicit) | By reference (effectively final) |

---

## Scenario-Based Questions

### Scenario 1: Debugging a Silent Error Loss

**Scenario:** Your service processes orders but occasionally returns success when the database write failed. The error log shows nothing. Code:

```go
func saveOrder(ctx context.Context, order *Order) error {
    var err error

    if conn, err := db.Conn(ctx); err == nil {
        defer conn.Close()

        if _, err := conn.ExecContext(ctx, "INSERT INTO orders ...", order.ID); err != nil {
            return fmt.Errorf("insert: %w", err)
        }
    }

    return err
}
```

**Question:** What is wrong and how do you fix it?

**Answer:** The `err` in `if conn, err := db.Conn(ctx)` shadows the outer `err`. If `db.Conn` fails, the outer `err` remains `nil`, and the function returns `nil` (success).

**Fix:**
```go
func saveOrder(ctx context.Context, order *Order) error {
    conn, err := db.Conn(ctx)
    if err != nil {
        return fmt.Errorf("get connection: %w", err)
    }
    defer conn.Close()

    _, err = conn.ExecContext(ctx, "INSERT INTO orders ...", order.ID)
    if err != nil {
        return fmt.Errorf("insert: %w", err)
    }
    return nil
}
```

---

### Scenario 2: Goroutine Data Race

**Scenario:** You are writing a concurrent file processor. Tests pass but `go test -race` reports data races:

```go
func processFiles(paths []string) []Result {
    results := make([]Result, len(paths))
    var wg sync.WaitGroup

    for i, path := range paths {
        wg.Add(1)
        go func() {
            defer wg.Done()
            results[i] = processFile(path) // race on i and path
        }()
    }

    wg.Wait()
    return results
}
```

**Question:** Identify the scope issue and fix it.

**Answer:** Before Go 1.22, `i` and `path` are shared across all goroutines (single variable, updated each iteration). Even with Go 1.22+, this specific pattern with index-based assignment needs care.

**Fix (works on all versions):**
```go
for i, path := range paths {
    wg.Add(1)
    go func(idx int, p string) {
        defer wg.Done()
        results[idx] = processFile(p)
    }(i, path)
}
```

---

### Scenario 3: Configuration Override Not Working

**Scenario:** Your app reads config from file, then overrides with environment variables. But env overrides are ignored:

```go
func loadConfig() *Config {
    cfg := defaultConfig()

    if data, err := os.ReadFile("config.yaml"); err == nil {
        if cfg, err := parseConfig(data); err == nil {
            // cfg here is a NEW variable — shadows outer cfg
            fmt.Println("loaded from file:", cfg.Addr)
        }
    }

    // Environment override
    if addr := os.Getenv("ADDR"); addr != "" {
        cfg.Addr = addr
    }

    return cfg // returns defaultConfig, not the file config
}
```

**Question:** Why do env overrides appear to work but file config does not?

**Answer:** `cfg, err := parseConfig(data)` shadows the outer `cfg`. The parsed config is lost. Env overrides modify the default config (outer `cfg`), which is why they appear to work.

**Fix:** Use `=` for cfg and declare err separately, or restructure with flat error handling.

---

### Scenario 4: Test Flakiness from Shared Test Variable

**Scenario:** Table-driven tests pass individually but fail when run together:

```go
func TestProcess(t *testing.T) {
    var result string

    tests := []struct{ input, expected string }{
        {"a", "A"},
        {"b", "B"},
    }

    for _, tc := range tests {
        t.Run(tc.input, func(t *testing.T) {
            result = process(tc.input) // shared variable!
        })
    }

    // Checking result here only sees the last test's value
    if result != "A" {
        t.Error("expected A")
    }
}
```

**Question:** What is the scope issue?

**Answer:** `result` is declared in the outer test function scope and shared across all sub-tests. Each sub-test overwrites it. The assertion only checks the final value.

**Fix:** Check results inside each sub-test:
```go
t.Run(tc.input, func(t *testing.T) {
    result := process(tc.input) // scoped to sub-test
    if result != tc.expected {
        t.Errorf("got %s, want %s", result, tc.expected)
    }
})
```

---

### Scenario 5: Memory Leak from Closure Scope

**Scenario:** Your long-running service's memory usage grows steadily. Profiling shows large byte slices being retained:

```go
func processStream(ch <-chan []byte) {
    for data := range ch {
        // data is a large byte slice (several MB)
        summary := extractSummary(data) // returns a small string

        go func() {
            // This closure captures 'data', not just 'summary'
            // Even though only summary is used, the compiler may
            // retain the entire data slice via the closure
            sendNotification(summary)
        }()
    }
}
```

**Question:** How does scope cause the memory leak?

**Answer:** The closure captures variables from the enclosing scope by reference. Even though only `summary` is used in the goroutine, the closure's capture of the loop body scope may keep `data` alive until the goroutine completes. If goroutines are slow, many large `data` slices accumulate.

**Fix:**
```go
go func(s string) {
    sendNotification(s) // no closure capture of data
}(summary)
```

---

## FAQ

### Q: Is shadowing always bad?

**A:** No. Intentional shadowing is idiomatic in Go:
- `v := v` before a goroutine (pre-Go 1.22)
- `t` parameter in `t.Run` callbacks (shadows outer test t)
- Type switch: `v := x.(type)` reuses `v` with concrete types

The problem is **accidental** shadowing, especially with `err`.

### Q: Does shadowing have any runtime cost?

**A:** No. Shadowing is a compile-time concept. In SSA form, shadowed variables become independent values. The generated assembly is identical whether you shadow or use different names.

### Q: Should I always use `golangci-lint` to detect shadows?

**A:** Yes, it should be in your CI pipeline. However, be prepared to handle false positives — intentional shadowing (like `v := v`) will also be flagged. You can use `//nolint:govet` comments for intentional cases.

### Q: Why does not the Go compiler warn about shadowing?

**A:** The Go team's philosophy is that the compiler should only report errors, not warnings. Shadowing is valid Go code. Optional tools (`go vet`, linters) provide warnings for those who want them. This keeps the compiler simple and fast.

### Q: How do I choose between `:=` and `var` for declaration?

**A:**
- Use `:=` when the initial value is known and you are inside a function
- Use `var` when you want the zero value, or at package level
- Use `var` when you need to declare a variable in an outer scope to avoid shadowing in inner blocks

```go
// Use var to avoid shadowing
var err error
if condition {
    err = someOperation() // = not :=
}
```

### Q: What is the best way to handle multiple errors without shadowing?

**A:** Use flat error handling with early returns:

```go
func process() error {
    a, err := step1()
    if err != nil {
        return fmt.Errorf("step1: %w", err)
    }

    b, err := step2(a) // reuses err (same scope)
    if err != nil {
        return fmt.Errorf("step2: %w", err)
    }

    return step3(b) // err from step3 is returned directly
}
```

### Q: Does Go 1.22 completely solve the loop variable issue?

**A:** For code compiled with `go 1.22` or later in `go.mod`, yes — both `for range` and classic `for` loops create per-iteration variables. But if you build with an older `go.mod` directive, the old behavior persists. Always update your `go.mod` to benefit from the fix.
