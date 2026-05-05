# Type Switch — Find the Bug

## Bug 1 — Multi-case operation
```go
switch v := i.(type) {
case int, int64:
    n := v + 1   // ?
}
```
**Bug:** Compile error. `v` is `any` in a multi-case.
**Fix:** Use separate cases:
```go
case int: n := v + 1
case int64: n := int64(v + 1)
```

---

## Bug 2 — fallthrough attempt
```go
switch i.(type) {
case int:
    fmt.Println("int")
    fallthrough
case string:
    fmt.Println("string")
}
```
**Bug:** Compile error.
**Fix:** Use a multi-case or restructure the logic.

---

## Bug 3 — nil concrete in interface
```go
var p *int = nil
var i any = p

switch i.(type) {
case nil:
    fmt.Println("nil")   // ?
case *int:
    fmt.Println("*int")
}
```
**Bug:** The `nil` case does not match. Type info is present.
**Output:** `*int`.

---

## Bug 4 — Order matters (interface case)
```go
type S struct{}
func (S) String() string { return "S" }

var i any = S{}
switch i.(type) {
case fmt.Stringer:
    fmt.Println("Stringer")
case S:
    fmt.Println("S")  // never reached
}
```
**Not strictly a bug, but a design issue:** Order matters — list specific cases first.
**Fix:** Swap the order.

---

## Bug 5 — Wrapped error
```go
err := fmt.Errorf("wrap: %w", &MyErr{})
switch err.(type) {
case *MyErr:
    fmt.Println("MyErr")   // ?
}
```
**Bug:** A direct switch does not see through wrapping.
**Fix:** Use `errors.As`.

---

## Bug 6 — Type switch on non-interface
```go
var n int = 42
switch n.(type) { ... }
```
**Bug:** Compile error.
**Fix:** Use an interface-typed value.

---

## Bug 7 — Domain polymorphism via switch
```go
func process(e any) {
    switch e := e.(type) {
    case *User: e.Save()
    case *Order: e.Save()
    case *Product: e.Save()
    }
}
```
**Not a bug, but a design flaw:** Prefer interfaces for domain entities.
**Fix:**
```go
type Saveable interface { Save() error }
func process(s Saveable) error { return s.Save() }
```

---

## Bug 8 — Missing default
```go
switch v := i.(type) {
case int: process(v)
case string: process(v)
}
// unknown type — silently ignored
```
**Bug:** Unexpected types are silently ignored.
**Fix:**
```go
default:
    log.Printf("unexpected type: %T", v)
```

---

## Bug 9 — Pointer/value confusion
```go
type T struct{ v int }
var i any = T{v: 5}

switch i.(type) {
case *T: fmt.Println("ptr")   // ?
case T:  fmt.Println("val")
}
```
**Output:** `val`. The value is T, not *T.

---

## Bug 10 — `case nil` in the wrong place
```go
var i any = (*int)(nil)
switch i.(type) {
case nil: fmt.Println("nil")     // ?
case *int: fmt.Println("*int")
}
```
**Output:** `*int`. The type *int is present.

If `var i any` is truly nil, then the `nil` case fires.
