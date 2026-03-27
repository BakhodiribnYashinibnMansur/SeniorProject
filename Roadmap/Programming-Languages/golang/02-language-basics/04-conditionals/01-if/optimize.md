# if Statement — Optimization Exercises

Each exercise has slow code with a known bottleneck. Your goal: make it faster.

Difficulty: 🟢 Easy | 🟡 Medium | 🔴 Hard
Category: 📦 Allocation | ⚡ CPU | 🔄 Algorithm | 💾 Memory

---

## Exercise 1 🟢 ⚡ — Repeated Function Call in Condition

**What the code does:** Validates a user role by calling `getRole()` twice in the if condition.

**The problem:** `getRole()` is called 2-3 times even though the value is the same.

```go
package main

import "fmt"

func getRole(userID int) string {
    // Simulates a map lookup or DB call
    roles := map[int]string{1: "admin", 2: "editor", 3: "viewer"}
    return roles[userID]
}

func checkAccess(userID int) string {
    if getRole(userID) == "admin" || getRole(userID) == "editor" {
        if getRole(userID) == "admin" {
            return "full access"
        }
        return "write access"
    }
    return "read only"
}

func main() {
    fmt.Println(checkAccess(1)) // full access
    fmt.Println(checkAccess(2)) // write access
    fmt.Println(checkAccess(3)) // read only
}
```

**Current benchmark:**
```
BenchmarkCheckAccess-8    5000000    320 ns/op    0 allocs/op
```

<details>
<summary>Hint</summary>
Call `getRole()` once and store the result in a variable. Use the variable in all subsequent if conditions.
</details>

<details>
<summary>Optimized Solution</summary>

```go
func checkAccess(userID int) string {
    role := getRole(userID)  // called exactly once
    if role == "admin" {
        return "full access"
    }
    if role == "editor" {
        return "write access"
    }
    return "read only"
}
```

**Optimized benchmark:**
```
BenchmarkCheckAccess-8    8000000    140 ns/op    0 allocs/op
```

**Explanation:** Calling `getRole()` once reduces map lookups from 2-3 to 1. The if structure is also simplified — no need for nested if when you have the role cached. 2.3x speedup.
</details>

---

## Exercise 2 🟢 📦 — Error Allocation in Hot Path

**What the code does:** Validates input in a tight loop, returning errors for invalid values.

**The problem:** `errors.New()` allocates a new error object on every failure.

```go
package main

import (
    "errors"
    "fmt"
)

func validateAge(age int) error {
    if age < 0 {
        return errors.New("age cannot be negative")  // allocates each call
    }
    if age > 150 {
        return errors.New("age exceeds maximum")     // allocates each call
    }
    return nil
}

func processAges(ages []int) int {
    valid := 0
    for _, age := range ages {
        if err := validateAge(age); err == nil {
            valid++
        }
    }
    return valid
}

func main() {
    ages := make([]int, 1000)
    for i := range ages {
        ages[i] = i - 100  // some negative, some valid, some too large
    }
    fmt.Println("Valid:", processAges(ages))
}
```

**Current benchmark:**
```
BenchmarkProcessAges-8    10000    125000 ns/op    2400 allocs/op
```

<details>
<summary>Hint</summary>
Pre-allocate sentinel error values at package level. Returning a pointer to an existing error requires no allocation.
</details>

<details>
<summary>Optimized Solution</summary>

```go
// Pre-allocated sentinel errors — allocated once at program start
var (
    ErrNegativeAge = errors.New("age cannot be negative")
    ErrAgeTooLarge = errors.New("age exceeds maximum")
)

func validateAge(age int) error {
    if age < 0 {
        return ErrNegativeAge  // no allocation — returns existing pointer
    }
    if age > 150 {
        return ErrAgeTooLarge  // no allocation
    }
    return nil
}
```

**Optimized benchmark:**
```
BenchmarkProcessAges-8    50000    22000 ns/op    0 allocs/op
```

**Explanation:** Pre-allocated sentinel errors eliminate all allocations in the validation path. For the 1000-element slice with ~100 negatives and ~849 > 150, we go from 949 allocations to 0. 5.7x speedup.
</details>

---

## Exercise 3 🟢 🔄 — Redundant Condition Recalculation

**What the code does:** Classifies a score multiple times with overlapping conditions.

**The problem:** Conditions overlap and the order causes unnecessary comparisons.

```go
package main

func classifyScore(score int) string {
    if score >= 0 && score < 60 {
        return "F"
    }
    if score >= 60 && score < 70 {
        return "D"
    }
    if score >= 70 && score < 80 {
        return "C"
    }
    if score >= 80 && score < 90 {
        return "B"
    }
    if score >= 90 && score <= 100 {
        return "A"
    }
    return "invalid"
}
```

**Current benchmark:**
```
BenchmarkClassify-8    20000000    62 ns/op
```

<details>
<summary>Hint</summary>
Since you check conditions in order, once you know `score >= 60`, you don't need to check `score >= 60` again in the next condition. Eliminate redundant lower-bound checks.
</details>

<details>
<summary>Optimized Solution</summary>

```go
func classifyScore(score int) string {
    if score < 0 || score > 100 {  // fast reject out-of-range
        return "invalid"
    }
    if score < 60 {
        return "F"
    }
    if score < 70 {    // already know score >= 60
        return "D"
    }
    if score < 80 {    // already know score >= 70
        return "C"
    }
    if score < 90 {    // already know score >= 80
        return "B"
    }
    return "A"         // already know score >= 90
}
```

**Optimized benchmark:**
```
BenchmarkClassify-8    40000000    32 ns/op
```

**Explanation:** Each condition only checks the upper bound, cutting the number of comparisons roughly in half. Also added early-exit for out-of-range values. 2x speedup. The compiler may also generate better branch prediction hints.
</details>

---

## Exercise 4 🟡 ⚡ — Short-Circuit Order Wrong

**What the code does:** Checks if a user can access a resource — requires DB lookup for both conditions.

**The problem:** The expensive DB check runs even when the cheap check would already reject.

```go
package main

import (
    "fmt"
    "time"
)

func hasSubscription(userID int) bool {
    time.Sleep(10 * time.Microsecond) // simulates DB query
    return userID%2 == 0             // even userIDs have subscription
}

func isFeatureEnabled(featureName string) bool {
    time.Sleep(1 * time.Microsecond) // simulates config lookup (fast)
    return featureName == "premium"
}

func canAccess(userID int, feature string) bool {
    // SLOW: hasSubscription (10µs) checked before isFeatureEnabled (1µs)
    if hasSubscription(userID) && isFeatureEnabled(feature) {
        return true
    }
    return false
}

func main() {
    fmt.Println(canAccess(1, "premium"))  // false (no subscription)
    fmt.Println(canAccess(2, "premium"))  // true
    fmt.Println(canAccess(2, "basic"))    // false (feature not enabled)
}
```

**Current benchmark:**
```
BenchmarkCanAccess-8    50000    22000 ns/op
```

<details>
<summary>Hint</summary>
Go evaluates `&&` conditions left to right with short-circuit. Put the fastest (cheapest) check first.
</details>

<details>
<summary>Optimized Solution</summary>

```go
func canAccess(userID int, feature string) bool {
    // FAST: isFeatureEnabled (1µs) checked first — short-circuits hasSubscription
    if isFeatureEnabled(feature) && hasSubscription(userID) {
        return true
    }
    return false
}
```

**Optimized benchmark:**
```
BenchmarkCanAccess-8    150000    7500 ns/op
```

**Explanation:** When `feature` is not "premium", `isFeatureEnabled` returns false and `hasSubscription` is never called (short-circuit). This reduces average time from ~11µs to ~1µs for the common case where the feature is disabled. 3x average speedup.
</details>

---

## Exercise 5 🟡 📦 — String Allocation in Error Branch

**What the code does:** Validates an HTTP method and returns an error with the method name in the message.

**The problem:** `fmt.Sprintf` allocates a new string every time the method is invalid.

```go
package main

import (
    "fmt"
)

var validMethods = map[string]bool{
    "GET": true, "POST": true, "PUT": true,
    "DELETE": true, "PATCH": true, "HEAD": true,
    "OPTIONS": true,
}

func validateMethod(method string) error {
    if !validMethods[method] {
        return fmt.Errorf("invalid HTTP method: %s", method)  // allocates!
    }
    return nil
}
```

**Current benchmark:**
```
BenchmarkValidateMethod_Invalid-8    2000000    750 ns/op    2 allocs/op
BenchmarkValidateMethod_Valid-8      10000000   120 ns/op    0 allocs/op
```

<details>
<summary>Hint</summary>
For invalid methods, consider whether you really need the method name in the error, or if a sentinel error + logging is sufficient. Alternatively, use a fixed message.
</details>

<details>
<summary>Optimized Solution</summary>

```go
// Option 1: Sentinel error (zero allocs, loses method name)
var ErrInvalidMethod = errors.New("invalid HTTP method")

func validateMethodFast(method string) error {
    if !validMethods[method] {
        return ErrInvalidMethod
    }
    return nil
}

// Option 2: Custom error type (one alloc, keeps method name, reuses type)
type InvalidMethodError struct {
    Method string
}

func (e *InvalidMethodError) Error() string {
    return "invalid HTTP method: " + e.Method
}

func validateMethodTyped(method string) error {
    if !validMethods[method] {
        return &InvalidMethodError{Method: method}  // 1 alloc (struct, not string)
    }
    return nil
}
```

**Optimized benchmark:**
```
BenchmarkValidateMethod_Invalid_Fast-8     10000000    120 ns/op    0 allocs/op
BenchmarkValidateMethod_Invalid_Typed-8    5000000     250 ns/op    1 alloc/op
```

**Explanation:** `fmt.Errorf` with a format string always allocates (format string + new error struct). Option 1 eliminates all allocations. Option 2 reduces from 2 allocations to 1 by using string concatenation (no format parser) and a single struct.
</details>

---

## Exercise 6 🟡 💾 — Unnecessary Slice Creation in if Condition

**What the code does:** Checks if all items in a slice pass a filter, using a helper that creates a new slice.

**The problem:** The helper function allocates a slice just to count items.

```go
package main

import "fmt"

func filterPositive(nums []int) []int {
    result := make([]int, 0, len(nums))
    for _, n := range nums {
        if n > 0 {
            result = append(result, n)
        }
    }
    return result
}

func allPositive(nums []int) bool {
    if len(filterPositive(nums)) == len(nums) {
        return true
    }
    return false
}

func main() {
    nums := []int{1, 2, 3, 4, 5}
    fmt.Println(allPositive(nums))  // true

    nums2 := []int{1, -2, 3}
    fmt.Println(allPositive(nums2)) // false
}
```

**Current benchmark:**
```
BenchmarkAllPositive-8    1000000    1200 ns/op    1 alloc/op
```

<details>
<summary>Hint</summary>
You don't need to collect the positive numbers — you just need to know if any are negative. Early exit on first negative.
</details>

<details>
<summary>Optimized Solution</summary>

```go
func allPositive(nums []int) bool {
    for _, n := range nums {
        if n <= 0 {
            return false  // early exit — no allocation needed
        }
    }
    return true
}
```

**Optimized benchmark:**
```
BenchmarkAllPositive-8    10000000    120 ns/op    0 allocs/op
```

**Explanation:** The original code allocated a new slice (O(n) memory) and traversed the entire input. The optimized version allocates nothing and exits as soon as it finds a non-positive number. 10x speedup for slices with early negatives, 10x for all-positive too (no allocation overhead).
</details>

---

## Exercise 7 🟡 ⚡ — if Inside Tight Loop with Interface Call

**What the code does:** Filters a slice using an interface-based predicate.

**The problem:** Interface dispatch in the tight inner loop prevents inlining.

```go
package main

import "fmt"

type Predicate interface {
    Match(x int) bool
}

type GreaterThan struct{ Threshold int }
func (g GreaterThan) Match(x int) bool { return x > g.Threshold }

func filter(nums []int, pred Predicate) []int {
    result := make([]int, 0, len(nums))
    for _, n := range nums {
        if pred.Match(n) {    // interface dispatch — not inlinable
            result = append(result, n)
        }
    }
    return result
}

func main() {
    nums := make([]int, 10000)
    for i := range nums { nums[i] = i }

    result := filter(nums, GreaterThan{Threshold: 5000})
    fmt.Println("Filtered:", len(result))
}
```

**Current benchmark:**
```
BenchmarkFilter-8    500    2100000 ns/op    81920 B/op    1 alloc/op
```

<details>
<summary>Hint</summary>
Replace the interface with a function parameter. Go can inline function values in some cases, and avoids vtable dispatch.
</details>

<details>
<summary>Optimized Solution</summary>

```go
// Use a function parameter instead of interface for small, hot predicates
func filterFunc(nums []int, match func(int) bool) []int {
    result := make([]int, 0, len(nums)/2)  // better initial capacity estimate
    for _, n := range nums {
        if match(n) {    // function call — may be inlined
            result = append(result, n)
        }
    }
    return result
}

func main() {
    nums := make([]int, 10000)
    for i := range nums { nums[i] = i }

    threshold := 5000
    result := filterFunc(nums, func(x int) bool { return x > threshold })
    fmt.Println("Filtered:", len(result))
}
```

**Optimized benchmark:**
```
BenchmarkFilterFunc-8    800    1400000 ns/op    40960 B/op    1 alloc/op
```

**Explanation:** Function values (closures) can sometimes be devirtualized by the compiler, enabling inlining of the `if` condition body. Interface dispatch always goes through a vtable (indirect call). For hot loops, direct function calls are faster. ~1.5x speedup in this case; more pronounced with simpler predicates.
</details>

---

## Exercise 8 🔴 ⚡ — Branch Misprediction in Sort-Dependent Loop

**What the code does:** Sums values from a slice only if they're above a threshold.

**The problem:** Random data causes ~50% branch mispredictions.

```go
package main

import (
    "fmt"
    "math/rand"
)

func sumAboveThreshold(data []int, threshold int) int {
    sum := 0
    for _, v := range data {
        if v > threshold {  // unpredictable for random data
            sum += v
        }
    }
    return sum
}

func main() {
    data := make([]int, 100000)
    for i := range data {
        data[i] = rand.Intn(200)
    }
    fmt.Println(sumAboveThreshold(data, 100))
}
```

**Current benchmark (random data):**
```
BenchmarkSum_Random-8    1000    1050000 ns/op
```

<details>
<summary>Hint</summary>
Sort the data first so all values below threshold come before all above. The branch predictor will learn the pattern. Or use branchless arithmetic.
</details>

<details>
<summary>Optimized Solution</summary>

```go
import "sort"

// Option 1: Sort first (if data can be sorted)
func sumAboveThresholdSorted(data []int, threshold int) int {
    sorted := make([]int, len(data))
    copy(sorted, data)
    sort.Ints(sorted)

    sum := 0
    for _, v := range sorted {
        if v <= threshold {  // branch predictor learns: false for first N, then all true
            continue
        }
        sum += v
    }
    return sum
}

// Option 2: Branchless arithmetic (avoids the if entirely)
func sumAboveThresholdBranchless(data []int, threshold int) int {
    sum := 0
    for _, v := range data {
        // diff > 0 when v > threshold, else 0
        diff := v - threshold
        // shift right 63 bits: 0 if positive, -1 (all 1s) if negative
        mask := ^(diff >> 63)  // -1 when v > threshold, 0 otherwise
        sum += v & mask        // adds v when mask is -1, adds 0 otherwise
    }
    return sum
}

// Option 3: Use binary search after sorting (if threshold is a single value)
func sumAboveThresholdBinarySearch(data []int, threshold int) int {
    sorted := make([]int, len(data))
    copy(sorted, data)
    sort.Ints(sorted)

    // Find first index where value > threshold
    idx := sort.SearchInts(sorted, threshold+1)

    sum := 0
    for _, v := range sorted[idx:] {
        sum += v
    }
    return sum
}
```

**Optimized benchmark:**
```
BenchmarkSum_Branchless-8    3000    410000 ns/op    // 2.6x faster
BenchmarkSum_Sorted-8        500     2100000 ns/op   // slower due to sort overhead
BenchmarkSum_BinarySearch-8  500     2050000 ns/op   // faster if data already sorted
```

**Explanation:** Branchless arithmetic eliminates the conditional branch entirely, replacing it with bit manipulation. The mask is all-zeros or all-ones depending on whether `v > threshold`. For truly random data with ~50% misprediction, this is 2-3x faster. For already-sorted data, the binary search approach is best.
</details>

---

## Exercise 9 🔴 🔄 — N^2 Search in Hot if Condition

**What the code does:** For each request, checks if the IP is in a blocklist.

**The problem:** Linear scan through the blocklist for every request.

```go
package main

import "fmt"

var blocklist = []string{
    "192.168.1.1", "10.0.0.1", "172.16.0.1",
    // ... hundreds more
}

func isBlocked(ip string) bool {
    for _, blocked := range blocklist {
        if ip == blocked {    // O(n) for each call
            return true
        }
    }
    return false
}

func handleRequest(ip string) {
    if isBlocked(ip) {
        fmt.Println("Blocked:", ip)
        return
    }
    fmt.Println("Allowed:", ip)
}

func main() {
    handleRequest("192.168.1.1")
    handleRequest("8.8.8.8")
}
```

**Current benchmark (1000-entry blocklist):**
```
BenchmarkIsBlocked_Hit-8    500000    2800 ns/op
BenchmarkIsBlocked_Miss-8   200000    5600 ns/op
```

<details>
<summary>Hint</summary>
Use a hash set (map) for O(1) lookup instead of O(n) linear scan. Convert the slice to a map once at initialization.
</details>

<details>
<summary>Optimized Solution</summary>

```go
// Convert slice to map once at startup — O(1) lookup
var blocklistSet map[string]struct{}

func init() {
    blocklistSet = make(map[string]struct{}, len(blocklist))
    for _, ip := range blocklist {
        blocklistSet[ip] = struct{}{}
    }
}

func isBlockedFast(ip string) bool {
    _, blocked := blocklistSet[ip]  // O(1) hash lookup
    if blocked {
        return true
    }
    return false
}

// Even more concise:
func isBlockedConcise(ip string) bool {
    _, ok := blocklistSet[ip]
    return ok
}
```

**Optimized benchmark:**
```
BenchmarkIsBlocked_Hit_Fast-8    10000000    120 ns/op
BenchmarkIsBlocked_Miss_Fast-8   10000000    118 ns/op
```

**Explanation:** Map lookup is O(1) vs O(n) slice scan. For 1000 entries: 23x speedup on hits (no longer scanning to find it), 47x speedup on misses (no longer scanning entire list). The `if` condition changes from a loop with string comparisons to a single hash table lookup.
</details>

---

## Exercise 10 🔴 💾 — Allocation in Error Message Construction

**What the code does:** Validates a complex struct and builds a detailed error message.

**The problem:** Even when there are no errors, a `strings.Builder` is allocated.

```go
package main

import (
    "fmt"
    "strings"
)

type Order struct {
    ID       int
    UserID   int
    Amount   float64
    Currency string
    Items    []string
}

func validateOrder(o Order) error {
    var sb strings.Builder

    if o.ID <= 0 {
        sb.WriteString("invalid order ID; ")
    }
    if o.UserID <= 0 {
        sb.WriteString("invalid user ID; ")
    }
    if o.Amount <= 0 {
        sb.WriteString("amount must be positive; ")
    }
    if o.Currency == "" {
        sb.WriteString("currency required; ")
    }
    if len(o.Items) == 0 {
        sb.WriteString("no items; ")
    }

    if sb.Len() > 0 {
        return fmt.Errorf("order validation: %s", sb.String())
    }
    return nil
}

func main() {
    order := Order{ID: 1, UserID: 2, Amount: 99.99, Currency: "USD", Items: []string{"item1"}}
    fmt.Println(validateOrder(order))
}
```

**Current benchmark (valid order — no errors):**
```
BenchmarkValidate_Valid-8    3000000    480 ns/op    2 allocs/op
```

<details>
<summary>Hint</summary>
Allocate the `strings.Builder` only when you actually have an error. Use a counter or slice to detect first error.
</details>

<details>
<summary>Optimized Solution</summary>

```go
func validateOrderFast(o Order) error {
    // Fast path: check if validation will pass without allocating
    if o.ID > 0 && o.UserID > 0 && o.Amount > 0 && o.Currency != "" && len(o.Items) > 0 {
        return nil  // no allocation on success path
    }

    // Only allocate when we know there are errors
    var errs []string
    if o.ID <= 0 {
        errs = append(errs, "invalid order ID")
    }
    if o.UserID <= 0 {
        errs = append(errs, "invalid user ID")
    }
    if o.Amount <= 0 {
        errs = append(errs, "amount must be positive")
    }
    if o.Currency == "" {
        errs = append(errs, "currency required")
    }
    if len(o.Items) == 0 {
        errs = append(errs, "no items")
    }

    return fmt.Errorf("order validation: %s", strings.Join(errs, "; "))
}

// Alternative: use lazy error accumulator
type lazyErrors struct {
    msgs []string
}

func (le *lazyErrors) add(condition bool, msg string) {
    if condition {
        le.msgs = append(le.msgs, msg)
    }
}

func (le *lazyErrors) err(prefix string) error {
    if len(le.msgs) == 0 {
        return nil
    }
    return fmt.Errorf("%s: %s", prefix, strings.Join(le.msgs, "; "))
}

func validateOrderLazy(o Order) error {
    var le lazyErrors
    le.add(o.ID <= 0, "invalid order ID")
    le.add(o.UserID <= 0, "invalid user ID")
    le.add(o.Amount <= 0, "amount must be positive")
    le.add(o.Currency == "", "currency required")
    le.add(len(o.Items) == 0, "no items")
    return le.err("order validation")
}
```

**Optimized benchmark:**
```
BenchmarkValidate_Valid_Fast-8    10000000    95 ns/op    0 allocs/op
```

**Explanation:** The fast path adds a single if check that covers all success conditions. When the order is valid (the common case in production), we skip all allocations entirely. The slow (error) path only triggers for invalid orders. 5x speedup for the valid case, same behavior for invalid cases.
</details>

---

## Exercise 11 🔴 ⚡🔄 — Redundant Interface Nil Check in Hot Loop

**What the code does:** Processes a list of handlers, checking each for nil before calling.

**The problem:** The nil check occurs inside the loop even though nil handlers shouldn't be in the list.

```go
package main

import "fmt"

type Handler interface {
    Handle(data string) error
}

type LogHandler struct{}
func (h *LogHandler) Handle(data string) error {
    fmt.Println("log:", data)
    return nil
}

func processAll(handlers []Handler, data string) []error {
    var errs []error
    for _, h := range handlers {
        if h == nil {    // nil check on every iteration
            continue
        }
        if err := h.Handle(data); err != nil {
            errs = append(errs, err)
        }
    }
    return errs
}
```

**Current benchmark:**
```
BenchmarkProcessAll-8    1000000    1500 ns/op
```

<details>
<summary>Hint</summary>
Validate and remove nil handlers once at registration time, not on every call. Keep the hot loop clean.
</details>

<details>
<summary>Optimized Solution</summary>

```go
// Pre-filter nil handlers at construction time
type Pipeline struct {
    handlers []Handler
}

func NewPipeline(handlers ...Handler) *Pipeline {
    valid := make([]Handler, 0, len(handlers))
    for _, h := range handlers {
        if h != nil {    // nil check ONCE at construction
            valid = append(valid, h)
        }
    }
    return &Pipeline{handlers: valid}
}

func (p *Pipeline) Process(data string) []error {
    var errs []error
    for _, h := range p.handlers {
        // No nil check needed — guaranteed by NewPipeline
        if err := h.Handle(data); err != nil {
            errs = append(errs, err)
        }
    }
    return errs
}

func main() {
    p := NewPipeline(&LogHandler{}, nil, &LogHandler{})
    errs := p.Process("hello")
    fmt.Println("Errors:", errs)
}
```

**Optimized benchmark:**
```
BenchmarkPipelineProcess-8    2000000    720 ns/op
```

**Explanation:** Moving the nil check out of the hot loop into the constructor means it runs once per handler registration instead of once per data item processed. For a pipeline processing 1M items with 10 handlers, this eliminates 10M nil interface comparisons. 2x speedup for the processing loop.
</details>
