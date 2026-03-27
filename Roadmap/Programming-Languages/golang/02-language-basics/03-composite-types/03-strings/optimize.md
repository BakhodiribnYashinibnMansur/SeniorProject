# Strings as Composite Type — Optimize

Each exercise has a slow implementation. Your goal is to make it faster by reducing allocations, improving algorithmic complexity, or using more appropriate standard library functions.

---

## Exercise 1 — Replace + Concatenation with strings.Builder 🟢

**Description:** A function joins a slice of strings with a separator. The naive `+=` version has O(n²) time complexity due to repeated string allocation.

**Slow Code:**
```go
func joinStrings(parts []string, sep string) string {
    result := ""
    for i, p := range parts {
        if i > 0 {
            result += sep
        }
        result += p
    }
    return result
}
// Problem: for n=1000 parts, this does ~999 allocations of growing strings
```

<details>
<summary>Hint</summary>

Use `strings.Builder` with `Grow` to pre-allocate exact capacity. Calculate total size = sum of all part lengths + sep length × (n-1).

</details>

<details>
<summary>Optimized Solution</summary>

```go
func joinStrings(parts []string, sep string) string {
    if len(parts) == 0 { return "" }
    if len(parts) == 1 { return parts[0] }

    // Calculate exact size
    n := len(sep) * (len(parts) - 1)
    for _, p := range parts { n += len(p) }

    var sb strings.Builder
    sb.Grow(n) // single allocation
    sb.WriteString(parts[0])
    for _, p := range parts[1:] {
        sb.WriteString(sep)
        sb.WriteString(p)
    }
    return sb.String()
}
// 1 allocation vs O(n²) allocations
// Note: strings.Join is the stdlib version of this — use it when possible!
```

</details>

---

## Exercise 2 — Replace ToLower+== with EqualFold 🟢

**Description:** A search function performs case-insensitive comparison by converting both strings to lowercase.

**Slow Code:**
```go
func containsCaseInsensitive(haystack, needle string) bool {
    lh := strings.ToLower(haystack)
    ln := strings.ToLower(needle)
    return strings.Contains(lh, ln)
}
// Problem: allocates 2 new strings on every call
```

<details>
<summary>Hint</summary>

For full equality checks, `strings.EqualFold` is the zero-allocation alternative. For containment, you can use a byte-by-byte approach or `strings.ToLower` on just the needle (amortized), caching it.

</details>

<details>
<summary>Optimized Solution</summary>

```go
// For case-insensitive equality (2 strings):
func equalCI(a, b string) bool {
    return strings.EqualFold(a, b) // 0 allocations
}

// For case-insensitive containment, the stdlib doesn't have a direct function.
// Best approach: normalize needle once, then search:
type CISearcher struct {
    needle string // pre-lowercased
}

func NewCISearcher(needle string) *CISearcher {
    return &CISearcher{needle: strings.ToLower(needle)} // 1 alloc, done once
}

func (cs *CISearcher) Contains(haystack string) bool {
    // Still 1 alloc per call for ToLower(haystack), but needle is pre-computed
    return strings.Contains(strings.ToLower(haystack), cs.needle)
}
```

</details>

---

## Exercise 3 — Replace Chained Replace with strings.NewReplacer 🟢

**Description:** A log sanitizer removes multiple sensitive patterns from log lines by chaining `strings.ReplaceAll` calls.

**Slow Code:**
```go
func sanitizeLog(line string) string {
    line = strings.ReplaceAll(line, "password=", "password=***")
    line = strings.ReplaceAll(line, "token=", "token=***")
    line = strings.ReplaceAll(line, "secret=", "secret=***")
    line = strings.ReplaceAll(line, "key=", "key=***")
    line = strings.ReplaceAll(line, "auth=", "auth=***")
    return line
    // Problem: 5 passes over the string, 5 intermediate string allocations
}
```

<details>
<summary>Hint</summary>

`strings.NewReplacer` applies all substitutions in a single pass. Create it once (as a package-level variable) and call `.Replace` on each input.

</details>

<details>
<summary>Optimized Solution</summary>

```go
var logSanitizer = strings.NewReplacer(
    "password=", "password=***",
    "token=",    "token=***",
    "secret=",   "secret=***",
    "key=",      "key=***",
    "auth=",     "auth=***",
)

func sanitizeLog(line string) string {
    return logSanitizer.Replace(line)
    // 1 pass over the string
    // logSanitizer created once — zero overhead on subsequent calls
}
```

</details>

---

## Exercise 4 — Avoid []byte(s) Conversion for Map Lookup 🟡

**Description:** A router frequently looks up routes using a `[]byte` key. The naive implementation converts to `string` each time.

**Slow Code:**
```go
var routes = map[string]http.HandlerFunc{
    "/api/users":  usersHandler,
    "/api/orders": ordersHandler,
    "/health":     healthHandler,
}

func route(path []byte) http.HandlerFunc {
    return routes[string(path)] // allocation: []byte → string copy
}
// Problem: 1 allocation per request for string(path)
```

<details>
<summary>Hint</summary>

Go has a special compiler optimization: `map[string(b)]` where `b` is `[]byte` does NOT allocate when used as a map lookup (not assignment). The compiler passes the `[]byte` data directly to the map hash function.

</details>

<details>
<summary>Optimized Solution</summary>

```go
// The fix is simply: use the map lookup syntax directly.
// The compiler already optimizes this in modern Go!

func route(path []byte) http.HandlerFunc {
    return routes[string(path)]
    // In Go 1.5+: the compiler special-cases map[string(b)] for lookups
    // to avoid the []byte→string allocation.
    // Verify: go test -benchmem shows 0 allocs/op
}

// To explicitly verify, benchmark:
func BenchmarkRoute(b *testing.B) {
    path := []byte("/api/users")
    for i := 0; i < b.N; i++ {
        _ = routes[string(path)] // 0 allocs in map lookup context
    }
}
```

</details>

---

## Exercise 5 — Zero-Copy string→[]byte for Hashing 🟡

**Description:** A caching system computes a hash key from a string. The naive approach converts `string` to `[]byte` which always allocates.

**Slow Code:**
```go
import "crypto/sha256"

func hashKey(s string) [32]byte {
    b := []byte(s)         // allocation: copies string bytes to mutable []byte
    return sha256.Sum256(b)
}
// Problem: 1 allocation per call to copy the string data
```

<details>
<summary>Hint</summary>

Use `unsafe.Slice(unsafe.StringData(s), len(s))` to get a `[]byte` view of the string without allocation. The resulting `[]byte` must NOT be written to — only used for read-only operations like hashing.

</details>

<details>
<summary>Optimized Solution</summary>

```go
import (
    "crypto/sha256"
    "unsafe"
)

func hashKey(s string) [32]byte {
    // Zero-copy: treat string bytes as []byte for read-only hashing
    // SAFE: sha256.Sum256 only reads the bytes, never writes
    b := unsafe.Slice(unsafe.StringData(s), len(s))
    return sha256.Sum256(b)
}
// 0 allocations vs 1 allocation
// IMPORTANT: never pass the unsafe []byte to code that might modify it!
```

</details>

---

## Exercise 6 — Use strings.Reader Instead of []byte Conversion for io.Reader 🟡

**Description:** A function feeds a string to an `io.Reader`-based pipeline. The naive approach converts to `[]byte` first.

**Slow Code:**
```go
func processString(s string, w io.Writer) error {
    b := []byte(s)                    // allocation: copies all bytes
    _, err := bytes.NewReader(b).WriteTo(w)
    return err
}
// Problem: allocates a []byte copy of the entire string
```

<details>
<summary>Hint</summary>

`strings.NewReader(s)` creates an `io.Reader` from a string without copying the string data. It implements `io.Reader`, `io.WriterTo`, `io.Seeker`, etc.

</details>

<details>
<summary>Optimized Solution</summary>

```go
func processString(s string, w io.Writer) error {
    _, err := strings.NewReader(s).WriteTo(w)
    return err
    // 0 extra allocations for the reader — strings.Reader is 3 fields
    // The string data is NOT copied
}
// strings.NewReader is a 24-byte struct allocation (negligible)
// vs []byte copy of potentially megabytes
```

</details>

---

## Exercise 7 — Avoid Repeated strings.ToLower in Hot Loop 🟡

**Description:** A tag matching system checks if any tag in a list matches a query, case-insensitively. The query is the same for all tags.

**Slow Code:**
```go
func matchesAnyTag(tags []string, query string) bool {
    for _, tag := range tags {
        if strings.ToLower(tag) == strings.ToLower(query) { // 2 allocs per iter
            return true
        }
    }
    return false
}
// Problem: allocates 2 strings per tag in the loop
// If len(tags) = 100, that's 200 allocations per call
```

<details>
<summary>Hint</summary>

Pre-lowercase the `query` once outside the loop. Inside the loop, use `strings.EqualFold` (0 allocs) instead of `ToLower` + `==`.

</details>

<details>
<summary>Optimized Solution</summary>

```go
func matchesAnyTag(tags []string, query string) bool {
    // Use EqualFold: 0 allocations per comparison
    for _, tag := range tags {
        if strings.EqualFold(tag, query) {
            return true
        }
    }
    return false
}
// 0 allocations total (EqualFold does in-place comparison)
// Alternatively, pre-lower query and use strings.ToLower only on each tag:
func matchesAnyTagAlt(tags []string, query string) bool {
    ql := strings.ToLower(query) // 1 alloc, outside loop
    for _, tag := range tags {
        if strings.ToLower(tag) == ql { // 1 alloc per tag (for tag's lower)
            return true
        }
    }
    return false
}
// EqualFold is still better for the inner comparison
```

</details>

---

## Exercise 8 — Pre-compute strings.NewReplacer 🟢

**Description:** A templating function is called millions of times. It creates a new `strings.NewReplacer` on each call.

**Slow Code:**
```go
func renderEmailTemplate(template string, name, company, role string) string {
    r := strings.NewReplacer( // BUG: creates new Replacer on every call!
        "{{Name}}", name,
        "{{Company}}", company,
        "{{Role}}", role,
    )
    return r.Replace(template)
}
// Problem: strings.NewReplacer builds an internal trie each time — O(k) overhead
// where k = number of patterns
```

<details>
<summary>Hint</summary>

The *pattern pairs* (the `old → new` mappings) are fixed. Only the replacement values change. Separate concerns: use a fixed replacer for static patterns, or pre-compute a template-specific replacer.

</details>

<details>
<summary>Optimized Solution</summary>

```go
// For variable values, we must create a new Replacer each time.
// But we can reduce overhead by accepting a map:
func renderTemplate(tmpl string, vars map[string]string) string {
    // Build pairs once from the map
    pairs := make([]string, 0, len(vars)*2)
    for k, v := range vars {
        pairs = append(pairs, "{{"+k+"}}", v)
    }
    return strings.NewReplacer(pairs...).Replace(tmpl)
}

// For truly static patterns with static replacements:
var staticReplacer = strings.NewReplacer(
    "{{AppName}}", "MyApp",
    "{{Version}}", "1.0.0",
    "{{Year}}",    "2026",
)

func renderStaticTemplate(tmpl string) string {
    return staticReplacer.Replace(tmpl) // zero overhead, trie built once
}
```

</details>

---

## Exercise 9 — strings.Builder Reset vs New Instance 🟡

**Description:** A logger creates a new `strings.Builder` for each log line. This causes repeated small allocations.

**Slow Code:**
```go
type Logger struct{}

func (l *Logger) Log(level, msg string) string {
    var sb strings.Builder // NEW builder each call — new allocation each time
    sb.WriteString("[")
    sb.WriteString(level)
    sb.WriteString("] ")
    sb.WriteString(msg)
    return sb.String()
}
// Problem: each call allocates a new []byte backing for the Builder
```

<details>
<summary>Hint</summary>

Reuse a `strings.Builder` by storing it as a struct field and calling `Reset()` between uses. After the first call, the internal buffer is already allocated — `Reset()` just sets length to 0, retaining capacity.

</details>

<details>
<summary>Optimized Solution</summary>

```go
type Logger struct {
    mu sync.Mutex
    sb strings.Builder
}

func (l *Logger) Log(level, msg string) string {
    l.mu.Lock()
    defer l.mu.Unlock()

    l.sb.Reset() // reuse existing buffer — no allocation after first call!
    l.sb.WriteString("[")
    l.sb.WriteString(level)
    l.sb.WriteString("] ")
    l.sb.WriteString(msg)
    return strings.Clone(l.sb.String()) // independent copy for the caller
}
// After warmup: 1 alloc per call (for Clone) vs 2 allocs (Builder + String)
// At high concurrency, use sync.Pool instead of a single mutex
```

</details>

---

## Exercise 10 — Avoid fmt.Sprintf for Simple String→Number Conversion 🟢

**Description:** A metrics formatter converts numeric values to strings using `fmt.Sprintf`.

**Slow Code:**
```go
func formatMetric(name string, value int64) string {
    return name + "=" + fmt.Sprintf("%d", value)
    // Problem: fmt.Sprintf uses reflection and is 3-5x slower than strconv
}
```

<details>
<summary>Hint</summary>

Use `strconv.FormatInt` or `strconv.Itoa` for integer-to-string conversion. These are direct numeric conversions without reflection.

</details>

<details>
<summary>Optimized Solution</summary>

```go
import "strconv"

func formatMetric(name string, value int64) string {
    var sb strings.Builder
    sb.Grow(len(name) + 1 + 20) // 20 digits max for int64
    sb.WriteString(name)
    sb.WriteByte('=')
    sb.WriteString(strconv.FormatInt(value, 10))
    return sb.String()
}
// strconv.FormatInt: ~10 ns/op
// fmt.Sprintf("%d"): ~50 ns/op
// 5x faster, fewer allocations
```

</details>

---

## Exercise 11 — Use strings.Count Instead of Manual Loop 🟢

**Description:** A text analyzer counts how many times a substring appears.

**Slow Code:**
```go
func countOccurrences(text, sub string) int {
    count := 0
    pos := 0
    for {
        idx := strings.Index(text[pos:], sub)
        if idx == -1 { break }
        count++
        pos += idx + len(sub)
    }
    return count
}
// Problem: verbose and manually re-slices the string repeatedly
```

<details>
<summary>Hint</summary>

`strings.Count(s, sub)` does exactly this for non-overlapping occurrences. It's implemented in the standard library with optimized byte scanning (uses SIMD on some platforms).

</details>

<details>
<summary>Optimized Solution</summary>

```go
func countOccurrences(text, sub string) int {
    return strings.Count(text, sub)
    // One line, optimized implementation, handles edge cases correctly
    // Note: counts NON-overlapping occurrences
    // strings.Count("aaa", "aa") = 1, not 2
}
```

</details>

---

## Exercise 12 — Use bufio.Scanner Over io.ReadAll for Large String Processing 🔴

**Description:** A log processor reads an entire multi-gigabyte log file into memory before processing.

**Slow Code:**
```go
func processLogs(r io.Reader) []string {
    data, err := io.ReadAll(r) // PROBLEM: loads entire file into memory!
    if err != nil { return nil }
    lines := strings.Split(string(data), "\n") // PROBLEM: another full copy
    var errors []string
    for _, line := range lines {
        if strings.Contains(line, "ERROR") {
            errors = append(errors, line)
        }
    }
    return errors
}
// Problem: 2× memory: once for io.ReadAll, once for string(data)
// For a 1 GB log file: 2 GB RAM peak usage
```

<details>
<summary>Hint</summary>

Use `bufio.Scanner` to process one line at a time. This uses O(line_length) memory instead of O(file_size).

</details>

<details>
<summary>Optimized Solution</summary>

```go
import "bufio"

func processLogs(r io.Reader) []string {
    var errors []string
    scanner := bufio.NewScanner(r)
    // Default scanner buffer: 64 KB per line
    // For very long lines: scanner.Buffer(make([]byte, 1MB), 1MB)
    for scanner.Scan() {
        line := scanner.Text() // returns string without heap allocation (in many cases)
        if strings.Contains(line, "ERROR") {
            errors = append(errors, line)
        }
    }
    return errors
}
// Memory usage: O(max_line_length) instead of O(file_size)
// For 1 GB log file: ~64 KB peak instead of 2 GB
```

</details>
