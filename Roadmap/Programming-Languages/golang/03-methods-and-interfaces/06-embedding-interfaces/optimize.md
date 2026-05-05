# Embedding Interfaces — Optimize

## 1. Embedding o'zi performance ta'sir qilmaydi

Embedding is compile-time syntactic. The method set is identical:
```go
type AB interface { A; B }
// ekvivalent
type AB interface { Method1(); Method2() }
```

No runtime difference.

## 2. Granular interface — cheaper for callers

```go
// Yomon
func process(rwc io.ReadWriteCloser) { ... }

// Yaxshi
func process(r io.Reader) { ... }
```

Callers should depend only on the interface they need — mocks are cheaper and dependencies stay small.

## 3. Interface dispatch overhead

Embed-langan interface ham bir xil dispatch — itab orqali. ~2 ns.

## 4. Decorator overhead

```go
type LoggingReader struct{ Reader }
func (lr LoggingReader) Read(p []byte) (int, error) {
    log.Println("...")
    return lr.Reader.Read(p)
}
```

Har Read — log + interface dispatch. Hot path-da o'lchang.

## 5. Inline opportunity

Embed orqali method promotion — kompilyator inline qilishi mumkin (concrete tip aniq bo'lganda).

## 6. Cleaner code

### Granular small interfaces
```go
// Atomic
type Reader interface { ... }
type Writer interface { ... }
type Closer interface { ... }

// Composition
type ReadWriteCloser interface { Reader; Writer; Closer }
```

### Caller minimal interface
```go
func ReadOnly(r Reader) { ... }
```

### Decorator chain
```go
l := Filtered{
    Logger: Level{
        Logger: Timestamp{Logger: Console{}},
        level:  "INFO",
    },
    minLen: 3,
}
```

### Compile-time check
```go
var _ ReadCloser = (*File)(nil)
```

## 7. Cheat Sheet

```
PERFORMANCE
─────────────────────
Embed → no runtime cost
Method dispatch → ~2 ns (interface)
Decorator chain → har dispatch overhead

DESIGN
─────────────────────
Granular > big
Caller minimal interface
-er suffix
Documentation kontrakti

PATTERNS
─────────────────────
Decorator: embed + override
Mock: NoOp + override
Capability: granular per-role
Plugin: Configurable + Runnable
```

## 8. Summary

Embedding performance:
- Compile-time syntactic — no runtime overhead
- Decorator chain — har dispatch ~2 ns
- Inline opportunity — concrete tip bilan

Cleaner code:
- Granular atomic interfaces
- Caller minimal so'rasin
- Decorator pattern — embed + override
- Compile-time check — `var _ I = (*T)(nil)`

Embedding is the Go-style composition. Used correctly, it produces a modular, extensible design.
