# var vs := — Interview Questions

> A comprehensive collection of interview questions about Go's variable declaration mechanisms, organized by experience level.

---

## 1. Junior Level

### Q1: What are the two ways to declare a variable in Go?

**Answer:**

1. **`var` keyword** — the explicit way: `var name string = "Alice"`
2. **`:=` short declaration** — the concise way: `name := "Alice"`

The `var` keyword supports three forms:
- `var x int = 5` (full declaration)
- `var x int` (zero value)
- `var x = 5` (type inference)

---

### Q2: What is a zero value in Go? Give examples.

**Answer:**

A zero value is the default value assigned to a variable when no explicit value is provided:

```go
var i int       // 0
var f float64   // 0.0
var b bool      // false
var s string    // "" (empty string)
var p *int      // nil
var sl []int    // nil
var m map[string]int // nil
```

---

### Q3: Can you use `:=` outside of a function?

**Answer:**

**No.** The `:=` operator can only be used inside function bodies. At the package level, you must use `var`:

```go
var x = 10       // OK at package level
// y := 20       // Compile error

func main() {
    z := 30      // OK inside a function
}
```

---

### Q4: What type does `x := 42` infer?

**Answer:**

`x` will be `int`. Untyped integer constants default to `int` (platform-dependent: 64-bit on 64-bit systems). Similarly: `3.14` -> `float64`, `true` -> `bool`, `"hello"` -> `string`, `'A'` -> `int32` (rune).

---

### Q5: What is the difference between `=` and `:=`?

**Answer:**

| Operator | Purpose | Creates New Variable? |
|----------|---------|----------------------|
| `=` | Assignment to an existing variable | No |
| `:=` | Declaration AND assignment in one step | Yes |

---

### Q6: What happens if you declare a variable with `:=` but never use it?

**Answer:**

**Compile error:** `x declared and not used`. Go requires all declared variables to be used. Use `_` to discard values.

---

### Q7: How do you declare multiple variables at once?

**Answer:**

```go
// var block
var (
    name = "Alice"
    age  = 30
)

// Short declaration
x, y, z := 1, 2, 3

// var on one line
var a, b, c int = 1, 2, 3
```

---

### Q8: What is the output of this code?

```go
x := 10
if true {
    x := 20
    fmt.Println(x)
}
fmt.Println(x)
```

**Answer:** `20` then `10`. The inner `x := 20` creates a new variable that shadows the outer `x`. The outer `x` remains `10`.

---

### Q9: Can you use `var` with type inference?

**Answer:**

**Yes.** `var name = "Alice"` infers type `string`. This works at both package and function level, unlike `:=` which only works in functions.

---

### Q10: When would you use `var x int` instead of `x := 0`?

**Answer:**

Use `var x int` when you want to emphasize that the zero value is intentional: `var count int` (starts at zero), `var buf bytes.Buffer` (ready-to-use buffer), `var mu sync.Mutex` (unlocked mutex).

---

## 2. Middle Level

### Q1: Explain the redeclaration rule with `:=`.

**Answer:**

When using `:=` with multiple variables, at least one must be new. Existing variables in the same scope are reassigned:

```go
x, err := doA()    // both new
y, err := doB()    // y is new, err is reassigned (same variable)
```

`x := 1; x := 2` fails — no new variables.

---

### Q2: What is variable shadowing? How can it cause bugs?

**Answer:**

Shadowing occurs when `:=` in an inner scope creates a new variable with the same name as an outer variable:

```go
err := fmt.Errorf("initial")
if true {
    val, err := strconv.Atoi("bad") // NEW err, shadows outer
    _ = val; _ = err
}
fmt.Println(err) // Still "initial"!
```

Detection: `go vet -shadow` or `golangci-lint`.

---

### Q3: What is the difference between `var s []int` and `s := []int{}`?

**Answer:**

| Property | `var s []int` | `s := []int{}` |
|----------|---------------|----------------|
| Value | nil | empty (non-nil) |
| `s == nil` | true | false |
| `append` | Works | Works |
| JSON marshal | `null` | `[]` |
| Allocation | None | Slice header |

---

### Q4: How does type inference work with expressions?

**Answer:**

```go
var a int32 = 10
b := a + 1       // b is int32 (type propagates from a)
c := 1 + 2       // c is int (untyped constants default)
d := 1.0 + 2     // d is float64 (broader type wins)
```

---

### Q5: Can you use `:=` to declare an interface variable?

**Answer:**

Not directly without a concrete value. `var w io.Writer` is the only way to declare an interface without assigning a concrete value. You cannot write `x := nil` because nil has no type.

---

### Q6: How do named return values relate to variable declarations?

**Answer:**

Named returns act like `var` declarations at the function top:

```go
func divide(a, b float64) (result float64, err error) {
    // result and err are already declared (zero values)
    if b == 0 {
        err = fmt.Errorf("division by zero")
        return
    }
    result = a / b
    return
}
```

Be careful: `:=` inside the function can shadow named returns.

---

### Q7: What is the `if` init statement pattern?

**Answer:**

```go
if err := doSomething(); err != nil {
    fmt.Println(err)
}
// err is NOT accessible here — scoped to the if block
```

Keeps variables scoped to where they are needed.

---

### Q8: Why does Go require all variables to be used?

**Answer:**

Design choice to catch bugs early. Unused variables often indicate forgotten logic, stale code after refactoring, or copy-paste errors.

---

### Q9: How do you handle the "no new variables" error?

**Answer:**

```go
x, err := step1()
// err := step2()  // ERROR: no new variables

err = step2()           // Fix 1: use =
y, err := step3()       // Fix 2: introduce new variable
```

---

### Q10: What is the difference between `new(int)` and `var x int`?

**Answer:**

```go
var x int     // x is int with value 0
p := new(int) // p is *int pointing to zero-value int
```

`new(T)` returns a pointer. Despite the name, `new` does not always allocate on the heap — escape analysis decides.

---

## 3. Senior Level

### Q1: How does escape analysis determine stack vs heap for variable declarations?

**Answer:**

Escape analysis tracks where variable references flow. A variable escapes when its lifetime exceeds its function's stack frame. Declaration syntax (`var` vs `:=`) has **zero impact** — what matters: is the address returned? Stored in interface? Captured by goroutine? Check: `go build -gcflags="-m"`.

---

### Q2: How do you minimize heap allocations in a hot path?

**Answer:**

1. Return values not pointers
2. Pre-allocate with `make([]T, 0, n)`
3. Use `sync.Pool` for reusable objects
4. Avoid interface boxing in tight loops
5. Pre-declare loop variables: `var buf bytes.Buffer; for { buf.Reset() }`
6. Use `var s []int` (nil) instead of `s := []int{}` (allocated)

---

### Q3: What is `var _ Interface = (*Type)(nil)`?

**Answer:**

Compile-time interface compliance check. If `*Type` does not implement `Interface`, compilation fails. No runtime cost — the variable is never allocated.

---

### Q4: How does Go handle goroutine stack growth with many local variables?

**Answer:**

Each goroutine starts with ~4 KB stack. The function prologue checks space. If insufficient, `runtime.morestack` allocates 2x larger stack, copies everything, and updates pointers. Large local variables (e.g., `var arr [10000]int`) can trigger growth.

---

### Q5: What is the performance difference between `var x T` and `x := T{}`?

**Answer:**

**Zero.** Identical machine code. Performance differences come from escape analysis, pre-allocation, and object pooling — not declaration syntax.

---

### Q6: How do you make package-level variables thread-safe?

**Answer:**

| Pattern | Tool |
|---------|------|
| Read-only after init | No protection needed |
| Counter | `atomic.Int64` |
| Config reload | `atomic.Value` |
| Map access | `sync.RWMutex` |
| Lazy init | `sync.Once` |
| Concurrent map | `sync.Map` |

---

### Q7: Describe a production incident caused by variable shadowing.

**Answer:**

Classic: `db, err := sql.Open(...)` in `init()` shadows package-level `var db *sql.DB`. The local `db` works in init, but the package-level `db` stays nil, causing panic on first query. Fix: `var err error; db, err = sql.Open(...)`.

---

### Q8: When would you prefer dependency injection over package-level `var`?

**Answer:**

Almost always. Package `var` is untestable, not concurrent by default, creates implicit coupling, and has fragile init order. Use `NewService(db *sql.DB)` instead. Exceptions: error sentinels, `sync.Pool`, compile-time interface checks.

---

### Q9: How does Go 1.22 loop variable change affect `:=`?

**Answer:**

Before 1.22: loop variable shared across iterations (closure bug). Go 1.22+: per-iteration variable. Eliminates need for `i := i` idiom.

---

### Q10: How do you benchmark declaration pattern impact?

**Answer:**

```go
func BenchmarkPattern(b *testing.B) {
    b.ReportAllocs()
    for i := 0; i < b.N; i++ { /* pattern */ }
}
```

Run: `go test -bench=. -benchmem -count=5`. Key metrics: `B/op`, `allocs/op`, `ns/op`. For deeper analysis: `go test -memprofile=mem.out` then `go tool pprof`.

---

## 4. Scenario-Based Questions

### Scenario 1: The Mysterious Nil Pointer

```go
var config *Config

func init() {
    config, err := loadConfig("config.yaml") // shadows package config
    if err != nil { log.Fatal(err) }
    log.Println("Loaded:", config.Name)
}

func main() {
    fmt.Println(config.Name) // PANIC: nil pointer
}
```

**Question:** Why does `main()` panic?

**Answer:** `:=` in `init()` creates a local `config` that shadows the package-level one. Fix: `var err error; config, err = loadConfig(...)`.

---

### Scenario 2: The Slow API Handler

Handler allocates 50+ objects per request with `json.Marshal`, pointer returns, and temporary structs.

**Question:** How to reduce allocations?

**Answer:** Use `sync.Pool` for reusable objects, return values instead of pointers, pre-allocate `json.Encoder` on response writer, use `var buf bytes.Buffer` with pooling.

---

### Scenario 3: The Race Condition

```go
var cache = map[string][]byte{}

func handleGet(w http.ResponseWriter, r *http.Request) {
    key := r.URL.Path
    data := cache[key]    // concurrent read
    cache[key] = newData  // concurrent write
}
```

**Question:** What is wrong?

**Answer:** Package-level map accessed concurrently without synchronization. Fix: `sync.RWMutex` for read-heavy workloads, or `sync.Map`.

---

### Scenario 4: The Memory Leak

```go
func processLogs(logChan <-chan string) {
    for line := range logChan {
        parts := strings.Split(line, "|")
        var timestamp = parts[0] // keeps reference to parts[0]
        saveTimestamp(timestamp)
    }
}
```

**Question:** Why might this leak memory?

**Answer:** `parts[0]` references the original string's backing array, keeping the entire `line` alive. Fix: `timestamp := strings.Clone(parts[0])`.

---

### Scenario 5: The Configuration Dilemma

**Question:** Package-level `var`, functional options, or config struct for a library?

**Answer:**

| Approach | Best For |
|----------|----------|
| Package `var` | Never for libraries |
| Config struct | Internal services |
| Functional options | Public libraries |

Libraries should use functional options: `client := mylib.New(mylib.WithTimeout(5*time.Second))`.

---

## 5. FAQ

**Q: Does `var` vs `:=` affect performance?**
A: No. Identical compiled code.

**Q: Should I always use `:=` inside functions?**
A: Default to `:=`, but use `var` for zero values, specific types, and interfaces.

**Q: Can I redeclare with `:=` in the same scope?**
A: Only if at least one other variable is new.

**Q: Why two declaration syntaxes?**
A: `var` is general-purpose (works everywhere, all features). `:=` is sugar for the common case inside functions.

**Q: What tools detect declaration issues?**
A: `go build` (unused vars), `go vet -shadow` (shadowing), `go build -gcflags="-m"` (escape analysis), `golangci-lint` (style), `go test -race` (races).

**Q: Can you use `:=` with `nil`?**
A: No. `nil` has no default type. Use `var x *T`.

**Q: Should error sentinels use `var` or `const`?**
A: `var` — `errors.New()` returns a pointer (not a compile-time constant).
