# Type Switch — Tasks

## Easy 🟢

### Task 1
`describe(i any) string` — int, string, bool, nil va default.

### Task 2
`humanReadable(i any)` — sonni "kichik/katta", string-ni "qisqa/uzun".

### Task 3
`isNumeric(i any) bool` — int, int64, float64.

### Task 4
JSON dynamic value-ni text-ga aylantirish.

### Task 5
Error category — sodda type switch.

---

## Medium 🟡

### Task 6
AST eval — `Num`, `Add`, `Mul`, `Sub`.

### Task 7
JSON deeply walker — map, slice, primitive.

### Task 8
HTTP request dispatcher — Login, Logout, Refresh.

### Task 9
Migration: V1 va V2 format aniqlash.

### Task 10
Reflect alternative — type bo'yicha hajmi.

---

## Hard 🔴

### Task 11
Polymorphism refactor: type switch → interface.

### Task 12
Error categorization with `errors.As` aralash.

### Task 13
Generic type switch (1.18+).

### Task 14
Plugin discovery — Configurable, Runnable.

---

## Solutions

### Solution 1
```go
func describe(i any) string {
    switch v := i.(type) {
    case nil:    return "nil"
    case int:    return fmt.Sprintf("int:%d", v)
    case string: return fmt.Sprintf("str:%s", v)
    case bool:   return fmt.Sprintf("bool:%v", v)
    default:     return fmt.Sprintf("%T", v)
    }
}
```

### Solution 2
```go
func humanReadable(i any) string {
    switch v := i.(type) {
    case int:
        if v > 100 { return "katta" }
        return "kichik"
    case string:
        if len(v) > 10 { return "uzun" }
        return "qisqa"
    }
    return "unknown"
}
```

### Solution 3
```go
func isNumeric(i any) bool {
    switch i.(type) {
    case int, int64, float32, float64:
        return true
    }
    return false
}
```

### Solution 4
```go
func toText(v any) string {
    switch v := v.(type) {
    case nil: return "null"
    case bool: return strconv.FormatBool(v)
    case float64: return strconv.FormatFloat(v, 'f', -1, 64)
    case string: return strconv.Quote(v)
    case []any:
        parts := make([]string, len(v))
        for i, x := range v { parts[i] = toText(x) }
        return "[" + strings.Join(parts, ",") + "]"
    case map[string]any:
        keys := make([]string, 0, len(v))
        for k := range v { keys = append(keys, k) }
        sort.Strings(keys)
        parts := make([]string, 0, len(keys))
        for _, k := range keys { parts = append(parts, strconv.Quote(k)+":"+toText(v[k])) }
        return "{" + strings.Join(parts, ",") + "}"
    }
    return ""
}
```

### Solution 5
```go
func category(err error) string {
    switch err.(type) {
    case nil: return "ok"
    case *NotFound: return "not_found"
    case *Validation: return "validation"
    case *Timeout: return "timeout"
    default: return "unknown"
    }
}
```

### Solution 6
```go
type Expr interface { isExpr() }
type Num struct{ val int }
type Add struct{ a, b Expr }
type Mul struct{ a, b Expr }
type Sub struct{ a, b Expr }

func (Num) isExpr() {}
func (Add) isExpr() {}
func (Mul) isExpr() {}
func (Sub) isExpr() {}

func eval(e Expr) int {
    switch e := e.(type) {
    case Num: return e.val
    case Add: return eval(e.a) + eval(e.b)
    case Mul: return eval(e.a) * eval(e.b)
    case Sub: return eval(e.a) - eval(e.b)
    }
    return 0
}
```

### Solution 7
```go
func walk(v any, depth int) {
    indent := strings.Repeat("  ", depth)
    switch v := v.(type) {
    case map[string]any:
        for k, val := range v {
            fmt.Printf("%s%s:\n", indent, k)
            walk(val, depth+1)
        }
    case []any:
        for i, val := range v {
            fmt.Printf("%s[%d]:\n", indent, i)
            walk(val, depth+1)
        }
    case nil:
        fmt.Printf("%snull\n", indent)
    default:
        fmt.Printf("%s%v (%T)\n", indent, v, v)
    }
}
```

### Solution 8
```go
type LoginRequest struct{ Username, Password string }
type LogoutRequest struct{ SessionID string }
type RefreshRequest struct{ Token string }

func handle(req any) string {
    switch r := req.(type) {
    case *LoginRequest:
        return "login: " + r.Username
    case *LogoutRequest:
        return "logout: " + r.SessionID
    case *RefreshRequest:
        return "refresh: " + r.Token
    default:
        return "unknown"
    }
}
```

### Solution 9
```go
type V1 struct{ Name string; Age int }
type V2 struct{ FullName string; YearsOld int }

func toV2(data any) (V2, error) {
    switch v := data.(type) {
    case V1:
        return V2{FullName: v.Name, YearsOld: v.Age}, nil
    case V2:
        return v, nil
    default:
        return V2{}, fmt.Errorf("unsupported version: %T", v)
    }
}
```

### Solution 10
```go
func size(v any) int {
    switch v := v.(type) {
    case int8, uint8: return 1
    case int16, uint16: return 2
    case int32, uint32, float32: return 4
    case int64, uint64, float64: return 8
    case string: return len(v)
    case []byte: return len(v)
    default: return -1
    }
}
```

### Solution 11
```go
// Before
switch e := entity.(type) {
case *User: e.Save()
case *Order: e.Save()
case *Product: e.Save()
}

// After
type Saveable interface { Save() error }

func (u *User) Save() error    { ... }
func (o *Order) Save() error   { ... }
func (p *Product) Save() error { ... }

func process(s Saveable) error { return s.Save() }
```

### Solution 12
```go
func category(err error) string {
    if err == nil { return "ok" }

    var notFound *NotFoundError
    if errors.As(err, &notFound) { return "404" }

    var validation *ValidationError
    if errors.As(err, &validation) { return "400" }

    var netErr net.Error
    if errors.As(err, &netErr) {
        if netErr.Timeout() { return "timeout" }
        return "network"
    }

    return "500"
}
```

### Solution 13
```go
func describe[T any](v T) string {
    switch any(v).(type) {
    case int: return "int"
    case string: return "string"
    case bool: return "bool"
    default: return fmt.Sprintf("%T", v)
    }
}
```

### Solution 14
```go
type Plugin any

func discover(p Plugin) []string {
    capabilities := []string{}
    if _, ok := p.(interface{ Configure(map[string]any) error }); ok {
        capabilities = append(capabilities, "Configurable")
    }
    if _, ok := p.(interface{ Run(ctx context.Context) error }); ok {
        capabilities = append(capabilities, "Runnable")
    }
    if _, ok := p.(interface{ Stop() error }); ok {
        capabilities = append(capabilities, "Stoppable")
    }
    return capabilities
}
```
