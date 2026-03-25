# Why Use Go — Interview Questions

## Table of Contents
1. [Junior Level](#junior-level)
2. [Middle Level](#middle-level)
3. [Senior Level](#senior-level)
4. [Scenario-Based Questions](#scenario-based-questions)
5. [FAQ](#faq)

---

## 1. Junior Level

### Savol 1: Go dasturlash tili nima va uni kim yaratgan?

**Answer:**

Go (Golang) — bu Google tomonidan 2009-yilda yaratilgan open-source, compiled, statically typed dasturlash tili. Uni Robert Griesemer, Rob Pike va Ken Thompson loyihalagan.

Go quyidagi muammolarni hal qilish uchun yaratilgan:
- **Sekin kompilatsiya** — C++ da katta loyihalar minutlab kompilatsiya bo'lardi
- **Murakkab concurrency** — thread management qiyin edi
- **Murakkab dependency management** — C/C++ da header files muammolari
- **Tilning murakkabligi** — C++ standartining 1000+ sahifasi

Go'ning asosiy xususiyatlari: sodda sintaksis (faqat 25 keyword), tez kompilatsiya, built-in concurrency (goroutine), garbage collection, va bitta static binary yaratish.

---

### Savol 2: Goroutine nima va u OS thread'dan qanday farq qiladi?

**Answer:**

Goroutine — Go'ning lightweight concurrent execution birligi. `go` keyword bilan yaratiladi.

| Xususiyat | Goroutine | OS Thread |
|-----------|-----------|-----------|
| **Xotira** | ~2-8 KB (boshlang'ich) | ~1-8 MB (fixed) |
| **Yaratish vaqti** | ~300 nanosekund | ~50 mikrosekund |
| **Context switch** | ~100 ns (userspace) | ~1-10 μs (kernel) |
| **Soni** | Millionlab mumkin | Minglab (OS limit) |
| **Boshqarish** | Go runtime (GMP scheduler) | OS kernel |
| **Stack** | Growable (2KB → 1GB) | Fixed size |

```go
// Goroutine yaratish
go func() {
    fmt.Println("Bu goroutine'da ishlaydi")
}()
```

Goroutine'lar Go runtime scheduler tomonidan OS thread'larga map qilinadi (M:N threading model).

---

### Savol 3: Go'da error handling qanday qilinadi? Nima uchun try/catch yo'q?

**Answer:**

Go'da error handling **explicit** qilinadi — funksiyalar error qiymat qaytaradi va dasturchi uni tekshirishi **shart**:

```go
file, err := os.Open("config.json")
if err != nil {
    return fmt.Errorf("config ochishda xato: %w", err)
}
defer file.Close()
```

**Nima uchun try/catch yo'q:**
1. **Invisible control flow** — try/catch kodning execution flow'ini yashiradi, xatoning qayerda yuz berganini topish qiyin
2. **Error is a value** — Go'da error oddiy qiymat, u bilan istalgan pattern qo'llash mumkin
3. **Explicit vs implicit** — Go dizaynerlari explicit yondashuvni tanlagan, chunki kodni o'qish osonroq
4. **Performance** — exception throw/catch qimmat operatsiya

Go'da `panic/recover` mexanizmi bor, lekin bu faqat dastur davom eta olmaydigan holatlar uchun (nil pointer dereference, index out of range).

---

### Savol 4: Go'da qanday data type'lar bor?

**Answer:**

Go'ning asosiy data type'lari:

| Kategoriya | Tiplar | Misol |
|-----------|--------|-------|
| **Boolean** | `bool` | `true`, `false` |
| **Integer** | `int`, `int8`, `int16`, `int32`, `int64`, `uint`, `uint8`... | `42`, `0xFF` |
| **Float** | `float32`, `float64` | `3.14` |
| **Complex** | `complex64`, `complex128` | `1+2i` |
| **String** | `string` | `"hello"` |
| **Byte** | `byte` (= `uint8`) | `'A'` |
| **Rune** | `rune` (= `int32`) | `'Ш'` (Unicode) |
| **Composite** | `array`, `slice`, `map`, `struct` | `[]int{1,2,3}` |
| **Reference** | `pointer`, `channel`, `function`, `interface` | `*int`, `chan int` |

Zero values: `int → 0`, `string → ""`, `bool → false`, `pointer → nil`

---

### Savol 5: Go qaysi sohalarda eng ko'p ishlatiladi?

**Answer:**

Go eng ko'p quyidagi sohalarda ishlatiladi:

1. **Backend / API development** — REST API, gRPC services (Uber, Twitch)
2. **Microservices** — kichik binary, tez start, kam memory (Monzo: 1600+ microservices)
3. **CLI tools** — cross-platform binary (Docker CLI, kubectl, terraform, gh)
4. **DevOps / Infrastructure** — Terraform, Prometheus, Grafana, Consul
5. **Cloud-native** — Kubernetes, Docker, containerd, etcd
6. **Network programming** — proxy, load balancer, DNS (Cloudflare, CockroachDB)

Go **mos kelmaydigan** sohalar: ML/AI (Python yaxshiroq), Mobile (Swift/Kotlin), Frontend (JavaScript), Game development (C++/C#).

---

### Savol 6: `go run` va `go build` farqi nima?

**Answer:**

| Buyruq | Nima qiladi | Binary saqlanadimi? | Qachon ishlatiladi |
|--------|------------|---------------------|-------------------|
| `go run main.go` | Kompilyatsiya + ishga tushirish | Yo'q (temp papkada, keyin o'chiriladi) | Development, tez test |
| `go build main.go` | Faqat kompilyatsiya | Ha (joriy papkada) | Production build |
| `go install` | Kompilyatsiya + `$GOPATH/bin` ga o'rnatish | Ha (global) | CLI tool o'rnatish |

```bash
# Development
go run main.go

# Production build
go build -ldflags="-s -w" -o myapp main.go

# Cross-compilation
GOOS=linux GOARCH=amd64 go build -o myapp-linux main.go
```

---

### Savol 7: Go'da package nima va import qanday ishlaydi?

**Answer:**

Package — Go'da kodni tashkil qilish birligi. Har bir `.go` fayl bitta package'ga tegishli.

```go
package main // Executable package (main funksiya bo'lishi kerak)

import (
    "fmt"          // Standart kutubxona
    "os"           // Standart kutubxona
    "github.com/gin-gonic/gin" // Tashqi paket
    myutil "myapp/internal/util" // Alias bilan
    _ "github.com/lib/pq"      // Side-effect import (init() funksiya uchun)
)
```

**Qoidalar:**
- Bosh harf bilan boshlangan nomlar **exported** (public): `fmt.Println`
- Kichik harf bilan boshlangan nomlar **unexported** (private): `fmt.println` ❌
- Ishlatilmagan import — **compilation error**
- Circular (tsiklik) import — **compilation error**

---

## 2. Middle Level

### Savol 1: Go'ning concurrency modeli (CSP) nima va shared memory threading'dan qanday farq qiladi?

**Answer:**

Go **CSP (Communicating Sequential Processes)** modelini ishlatadi. Bu 1978-yilda Tony Hoare tomonidan taklif qilingan.

**CSP vs Shared Memory:**

| Aspect | CSP (Go) | Shared Memory (Java/C++) |
|--------|----------|------------------------|
| **Muloqot** | Channel orqali message passing | Shared variable + lock |
| **Synchronization** | Channel send/receive | Mutex, semaphore, condition variable |
| **Safety** | Channel ownership — bitta goroutine egalik qiladi | Lock discipline — qo'lda boshqarish |
| **Deadlock** | Channel timeout + select | Complex lock ordering |
| **Debugging** | Race detector (`-race` flag) | Thread sanitizer, Valgrind |

```go
// CSP approach — Go idiom
func producer(ch chan<- int) {
    for i := 0; i < 10; i++ {
        ch <- i
    }
    close(ch)
}

func consumer(ch <-chan int) {
    for v := range ch {
        fmt.Println(v)
    }
}
```

**Go proverb:** "Don't communicate by sharing memory; share memory by communicating."

Lekin Go'da `sync.Mutex` ham bor — qachon channel, qachon mutex ishlatish kerakligini bilish muhim:
- **Channel:** goroutine'lar o'rtasida data uzatish, ownership transfer
- **Mutex:** shared state himoyalash (counter, cache)

---

### Savol 2: Go'da interface qanday ishlaydi? Duck typing deyish mumkinmi?

**Answer:**

Go'da interface **implicit satisfaction** orqali ishlaydi — hech qanday `implements` keyword yo'q. Struct barcha method'larga ega bo'lsa, avtomatik interface'ni satisfy qiladi.

```go
type Writer interface {
    Write([]byte) (int, error)
}

// os.File, bytes.Buffer, net.Conn — hammasi Writer interface'ni satisfy qiladi
// Chunki ularda Write method bor
```

**Duck typing vs Go interface:**
- **Duck typing** (Python) — runtime'da tekshiriladi
- **Go interface** — compile-time'da tekshiriladi

Go'niki **structural typing** deb ataladi — duck typing'ning compile-time versiyasi.

**Best practices:**
- Interface'lar **kichik** bo'lsin (1-3 method)
- Interface'ni **consumer** tomonida define qilish
- `io.Reader`, `io.Writer`, `fmt.Stringer` — ideal misolllar

---

### Savol 3: Go'da memory management qanday ishlaydi? GC ning pros/cons?

**Answer:**

Go **garbage collector** (GC) ishlatadi — concurrent, tri-color mark & sweep algorithm.

**GC jarayoni:**
1. **Mark Setup** (STW ~10-30μs) — write barrier enable
2. **Marking** (concurrent) — reachable ob'ektlarni belgilash
3. **Mark Termination** (STW ~10-30μs) — yakunlash
4. **Sweeping** (concurrent) — unmarked ob'ektlarni free qilish

**Pros:**
- Developer xotira haqida o'ylamaydi
- Use-after-free, double-free muammolari yo'q
- Production-ready — sub-millisecond pauses

**Cons:**
- GC pauzalar (latency spikes) — real-time systems uchun muammo
- Memory overhead (~2x actual usage)
- Throughput — GC CPU vaqt oladi (1-5%)

**Tuning:**
```bash
GOGC=100        # Default: heap 100% o'sganda GC ishlaydi
GOMEMLIMIT=512MiB  # Soft memory limit (Go 1.19+)
```

---

### Savol 4: Go'da `context` package nima uchun ishlatiladi?

**Answer:**

`context` package goroutine lifecycle management uchun ishlatiladi:

1. **Cancellation** — parent cancel bo'lsa, barcha child'lar ham cancel
2. **Timeout/Deadline** — operatsiyaga vaqt limiti berish
3. **Value passing** — request-scoped qiymatlar (request ID, auth token)

```go
// Timeout bilan HTTP request
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

req, _ := http.NewRequestWithContext(ctx, "GET", "https://api.example.com", nil)
resp, err := http.DefaultClient.Do(req)
```

**Best practices:**
- Context har doim **birinchi parameter** bo'lsin: `func DoWork(ctx context.Context, ...)`
- Context'ni **struct'da saqlang EMAS** — parameter sifatida uzating
- `context.Value` — faqat request-scoped data uchun, configuration uchun emas
- `context.Background()` — top-level, `context.TODO()` — refactoring vaqtida

---

### Savol 5: Go vs Python — qaysi birini qachon tanlash kerak?

**Answer:**

| Criteria | Go tanlash | Python tanlash |
|----------|-----------|---------------|
| **Performance** | ✅ 50-100x tezroq | Tezlik muhim emas |
| **Concurrency** | ✅ Goroutine (true parallel) | GIL — true parallelism yo'q |
| **Type safety** | ✅ Compile-time | Runtime errors |
| **Deploy** | ✅ Bitta binary | pip + virtualenv + interpreter |
| **Startup** | ✅ ~5ms | ~100ms+ |
| **ML/Data Science** | ❌ | ✅ NumPy, TensorFlow, PyTorch |
| **Rapid prototyping** | ❌ (ko'proq kod) | ✅ Tez yoziladi |
| **Ecosystem** | O'rta | ✅ Eng katta |
| **Learning curve** | 1-2 hafta | ✅ 1-3 kun |

**Real misollar:**
- Dropbox: Python → Go migration (5x performance gain)
- Uber: Go for high-throughput services, Python for ML
- Google: Go for infrastructure, Python for ML/scripting

---

### Savol 6: Go binary nima uchun katta va qanday kichiklashtirish mumkin?

**Answer:**

Go binary katta bo'lishining sabablari:
1. **Static linking** — barcha dependency binary ichida (~2-5MB)
2. **Go runtime** — GC, scheduler, allocator (~2-3MB)
3. **Debug info** — DWARF symbols, `.gopclntab` (~20-30% binary)
4. **Reflection data** — type metadata

**Kichiklashtirish usullari:**

```bash
# 1. Debug info o'chirish (~30% kamaytiradi)
go build -ldflags="-s -w" -o myapp

# 2. UPX compression (~50-70% kamaytiradi)
upx --best myapp

# 3. TinyGo (embedded/WASM uchun)
tinygo build -o myapp main.go

# Natija misoli:
# Default:        20 MB
# -ldflags="-s -w": 14 MB
# + UPX:            5 MB
```

---

## 3. Senior Level

### Savol 1: Go'da GMP scheduler qanday ishlaydi va work stealing nima?

**Answer:**

GMP Scheduler:
- **G (Goroutine):** Execution unit, 2-8KB stack, million'lab yaratish mumkin
- **M (Machine):** OS thread, blocking syscall uchun yangi M yaratilishi mumkin
- **P (Processor):** Logical processor, local run queue (256 goroutine), mcache

**Ishlash tartibi:**
1. Goroutine `go func()` bilan yaratiladi → P ning local queue'siga qo'shiladi
2. P o'zining queue'sidan G ni oladi va M da execute qiladi
3. G channel/mutex/IO da block bo'lsa → P boshqa G ga switch qiladi
4. G syscall qilsa → M blocked, P yangi M ga bog'lanadi

**Work Stealing:**
Agar P ning local queue'si bo'sh bo'lsa:
1. Global run queue'dan olishga harakat
2. Boshqa P'larning local queue'sidan yarmin o'g'irlaydi
3. Network poller'dan ready goroutine'larni oladi

**Preemption (Go 1.14+):**
- `sysmon` goroutine 10ms+ ishlab turgan G ni topadi
- `SIGURG` signal yuboriladi
- Signal handler context save qiladi, scheduler'ga o'tadi

**GOMAXPROCS:** P soni = CPU yadrolari soni (default). Container'da: `uber-go/automaxprocs` kutubxonasi CPU limit'ni avtomatik aniqlaydi.

---

### Savol 2: Go qachon to'g'ri tanlov EMAS va qanday qaror qabul qilasiz?

**Answer:**

Go to'g'ri tanlov emas bo'lgan holatlar:

1. **Real-time systems** (audio/video processing, HFT)
   - Sabab: GC pauzalar (~10-30μs STW, lekin unpredictable)
   - Alternativa: Rust (no GC), C++ (manual memory)

2. **Complex domain modeling** (financial instruments, type-level programming)
   - Sabab: Sum types yo'q, generics cheklangan, type system kam expressive
   - Alternativa: Haskell, Scala, Kotlin

3. **ML/AI**
   - Sabab: Ecosystem yo'q (NumPy, TensorFlow, PyTorch Python'da)
   - Alternativa: Python + Go API gateway

4. **Mobile/Desktop GUI**
   - Sabab: GUI toolkit'lar kam, community support yo'q
   - Alternativa: Swift/Kotlin (mobile), Electron/.NET (desktop)

5. **Embedded systems**
   - Sabab: Runtime (~2-3MB), GC latency
   - Alternativa: C, Rust, TinyGo (cheklangan)

**Decision framework:**
```
1. Performance requirement → GC tolerable? → Ha: Go, Yo'q: Rust/C++
2. Team size → 5+: Go (onboarding), 2-3 expert: Rust mumkin
3. Ecosystem → ML: Python, Frontend: JS, System: Rust
4. Operational → Container/cloud: Go ideal
5. Timeline → MVP tez: Python/Node, Production: Go
```

---

### Savol 3: Kompaniya miqyosida Go'ga migration strategiyasi qanday bo'lishi kerak?

**Answer:**

**Phased Migration (Strangler Fig Pattern):**

**Phase 1: Assessment (2-4 hafta)**
- Mavjud tizimni audit qilish: bottleneck'lar, pain points
- Go'ning qanday muammolarni hal qilishini aniqlash
- Jamoani baholash: Go bilimi, o'rganish tayorligli
- POC (Proof of Concept): kichik service Go'da yozib test qilish

**Phase 2: Pilot (1-3 oy)**
- Yangi, uncritical microservice'ni Go'da yozish
- CI/CD pipeline Go uchun sozlash
- Coding standards, linting, testing conventions belgilash
- Internal Go training program boshlash

**Phase 3: Expand (3-6 oy)**
- Yangi service'lar Go'da yozish (default)
- Shared Go libraries yaratish (logging, metrics, auth, config)
- Performance benchmarking: Go vs existing stack
- Go template project (boilerplate) yaratish

**Phase 4: Critical Path Migration (6-12 oy)**
- Performance-critical service'larni migrate qilish
- Strangler Fig: eski service oldiga Go proxy qo'yish, asta-sekin traffic shift
- A/B testing: Go vs existing implementation
- Rollback plan har doim tayyor

**Phase 5: Optimization (ongoing)**
- Performance tuning (pprof, benchmarks)
- Go version upgrade strategy
- Open source contribution
- Hiring: Go experience job requirement'ga qo'shish

**Risk mitigation:**
- Har doim rollback plan
- Gradual traffic shift (1% → 5% → 25% → 50% → 100%)
- Monitoring: latency, error rate, memory usage comparison

---

### Savol 4: Go'da high-performance service yozishda qanday optimization'lar qilasiz?

**Answer:**

**1. Profiling-first approach:**
```bash
# CPU profiling
go tool pprof http://localhost:6060/debug/pprof/profile?seconds=30

# Memory profiling
go tool pprof http://localhost:6060/debug/pprof/heap

# Allocation profiling
go tool pprof -alloc_space http://localhost:6060/debug/pprof/heap
```

**2. Common optimizations (evidence-based):**

| Optimization | When | Impact |
|-------------|------|--------|
| `sync.Pool` for buffers | Hot path, high allocation | 5-50x alloc reduction |
| Pre-allocate slices/maps | Known capacity | 2-5x faster |
| `strings.Builder` | String concat in loop | 100-1000x faster |
| Struct field alignment | Millions of structs | 20-40% memory |
| Avoid `interface{}` | Type assertion hot path | 1.5-3x faster |
| Worker pool pattern | Unbounded goroutine creation | Memory stability |
| Connection pooling | DB/HTTP clients | Latency reduction |
| `GOMEMLIMIT` | Container environment | OOM prevention |

**3. GC tuning:**
```bash
GOGC=50       # More frequent GC, less memory
GOGC=200      # Less frequent GC, more memory
GOMEMLIMIT=1GiB  # Soft limit for container
```

**4. Architecture-level:**
- Read-heavy: `sync.RWMutex` (Mutex emas)
- Write-heavy: sharding (per-CPU data structures)
- Contention: lock-free (atomic operations)
- Network: connection pooling, keep-alive

**Qoida:** Avval profiling, keyin optimization. "Premature optimization is the root of all evil."

---

### Savol 5: Go'da distributed system yozishda qanday pattern'lar ishlatiladi?

**Answer:**

**Core Patterns:**

1. **Circuit Breaker** — cascading failure prevention
   - Muvaffaqiyatsiz service'ga so'rov to'xtatish
   - States: Closed → Open → Half-Open → Closed

2. **Rate Limiting** (Token Bucket / Leaky Bucket)
   - So'rovlar sonini cheklash
   - `golang.org/x/time/rate` kutubxonasi

3. **Retry with Exponential Backoff**
   - Muvaffaqiyatsiz so'rovni qayta yuborish
   - `1s → 2s → 4s → 8s` (jitter bilan)

4. **Graceful Degradation**
   - Primary service down → cache/fallback ishlatish
   - `context.WithTimeout` bilan

5. **Health Check / Readiness Probe**
   - `/healthz` — alive
   - `/readyz` — traffic qabul qilishga tayyor

6. **Distributed Tracing**
   - OpenTelemetry SDK
   - Context propagation goroutine'lar o'rtasida

7. **Leader Election**
   - etcd/Consul bilan distributed lock
   - Faqat bitta instance write qiladi

8. **Event Sourcing / CQRS**
   - Kafka/NATS bilan event-driven architecture
   - Command va Query alohida service'larda

---

### Savol 6: Go binary'ni production'ga deploy qilishda qanday best practice'lar bor?

**Answer:**

**Build:**
```bash
# Static binary, debug info o'chirilgan
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -ldflags="-s -w -X main.version=1.2.3" -o myapp
```

**Docker (minimal image):**
```dockerfile
# Multi-stage build
FROM golang:1.22-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -ldflags="-s -w" -o myapp

FROM gcr.io/distroless/static:nonroot
COPY --from=builder /app/myapp /myapp
USER nonroot:nonroot
EXPOSE 8080
ENTRYPOINT ["/myapp"]
```

**Runtime configuration:**
```bash
GOMEMLIMIT=512MiB    # Container memory * 0.7
GOMAXPROCS=4         # Container CPU limit (or automaxprocs)
GODEBUG=gctrace=1    # GC monitoring (optional)
```

**Observability:**
- Structured logging (JSON format)
- Prometheus metrics endpoint
- Health check endpoints (`/healthz`, `/readyz`)
- pprof endpoints (secured, internal network only)
- Distributed tracing (OpenTelemetry)

**Security:**
- Non-root user in container
- `govulncheck` in CI pipeline
- Minimal base image (distroless/scratch)
- No secrets in binary/env — use secret manager

---

## 4. Scenario-Based Questions

### Scenario 1: Sizning jamoangiz Python backend'ni Go'ga migrate qilmoqchi. Qanday qaror qabul qilasiz?

**Answer:**

**Assessment bosqichi:**

1. **Muammoni aniqlash:**
   - Python'ning qaysi cheklovi muammo? (performance? concurrency? deploy?)
   - Metrikalar bilan isbotlash (p99 latency, CPU usage, memory, deploy time)

2. **Go'ning mos kelishini tekshirish:**
   - Service CPU-bound yoki IO-bound?
   - Team Go o'rganishga tayyormi?
   - Ecosystem dependencies (ML kutubxonalar kerakmi?)

3. **ROI hisoblash:**
   - Migration cost: developer vaqti, testing, monitoring
   - Benefit: server cost saving, latency improvement, operational simplicity
   - Risk: bug introduction, knowledge gap

4. **POC:**
   - Bitta kichik, non-critical service'ni Go'da qayta yozish
   - A/B test: Python vs Go performance/reliability
   - Developer feedback yig'ish

5. **Qaror:**
   - ROI ijobiy va risk manageable → Phase-by-phase migrate
   - ML/data heavy → Python qoldiring, faqat API layer Go'ga
   - Small team, Go knowledge yo'q → Python optimize qiling (PyPy, asyncio)

---

### Scenario 2: Sizning Go service'ingiz production'da memory leak ko'rsatyapti. Qanday investigate qilasiz?

**Answer:**

**Step 1: Monitoring**
```bash
# Goroutine count o'sib boryaptimi?
curl http://localhost:6060/debug/pprof/goroutine?debug=1 | head -5

# Heap profiling
go tool pprof http://localhost:6060/debug/pprof/heap
```

**Step 2: Goroutine leak tekshirish**
```bash
# Goroutine dump
curl http://localhost:6060/debug/pprof/goroutine?debug=2 > goroutines.txt
# Qaysi goroutine'lar ko'p kutib turibdi?
grep -c "goroutine" goroutines.txt
```

**Step 3: Heap diff**
```bash
# T=0 da snapshot
curl -o heap1.prof http://localhost:6060/debug/pprof/heap
# 5 min keyin
curl -o heap2.prof http://localhost:6060/debug/pprof/heap
# Diff
go tool pprof -base heap1.prof heap2.prof
# top — qaysi funksiya ko'p allocate qilyapti?
```

**Step 4: Common causes:**
- Goroutine leak (channel waiting, no context timeout)
- Global map/slice o'sib borishi
- Slice memory retention (katta slice'dan kichik sub-slice)
- `time.Ticker` `Stop()` chaqirilmagan
- HTTP response body `Close()` chaqirilmagan

**Step 5: Fix va verify**
- `context.WithTimeout()` barcha goroutine'larga
- `defer resp.Body.Close()` barcha HTTP response'larga
- GOMEMLIMIT o'rnatish

---

### Scenario 3: Siz yangi microservices platform loyihalayapsiz. Go vs Java vs Rust — qanday tanlaysiz?

**Answer:**

**Decision Matrix:**

| Criteria | Weight | Go | Java | Rust |
|----------|--------|:--:|:----:|:----:|
| Team onboarding (15 developers) | 25% | ⭐⭐⭐ | ⭐⭐ | ⭐ |
| Performance (< 10ms p99) | 20% | ⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ |
| Container efficiency | 15% | ⭐⭐⭐ | ⭐ | ⭐⭐⭐ |
| Ecosystem maturity | 15% | ⭐⭐ | ⭐⭐⭐ | ⭐⭐ |
| Operational simplicity | 15% | ⭐⭐⭐ | ⭐⭐ | ⭐⭐ |
| Hiring market | 10% | ⭐⭐ | ⭐⭐⭐ | ⭐ |

**Tavsiya:**
- **Go** — katta jamoa, tez deliver, cloud-native uchun optimal
- **Java** — mavjud Java expertise, Spring ecosystem kerak, enterprise patterns
- **Rust** — maximum performance critical, kichik expert team, safety-critical

**Mening tanlashim:** Ushbu scenario uchun **Go** — 15 developer bilan tez onboarding, 10ms p99 achievable, container-friendly. Agar specific service'da maximum performance kerak bo'lsa, uni Rust'da yozish mumkin (polyglot approach).

---

### Scenario 4: Go service 100K+ concurrent connection handle qilishi kerak. Qanday arxitektura qilasiz?

**Answer:**

**Architecture:**

```
┌────────────────┐
│  Load Balancer │ ← L4/L7 (HAProxy, NGINX)
└────────┬───────┘
         │
┌────────┴───────┐
│  Go Service    │
│  ┌───────────┐ │
│  │ Acceptor  │ │ ← net.Listener
│  │ goroutine │ │
│  └─────┬─────┘ │
│        │       │
│  ┌─────┴─────┐ │
│  │ Connection │ │ ← Per-connection goroutine
│  │ Handler    │ │    (100K goroutine = ~800MB stack)
│  │ goroutine  │ │
│  └─────┬─────┘ │
│        │       │
│  ┌─────┴─────┐ │
│  │ Worker    │ │ ← Bounded worker pool
│  │ Pool      │ │    (CPU-bound work)
│  └───────────┘ │
└────────────────┘
```

**Key decisions:**
1. **Per-connection goroutine** — 100K goroutine = ~800MB (2KB * 100K). Manageable.
2. **Worker pool** — CPU-bound work uchun goroutine sonini cheklash (GOMAXPROCS * 2-4)
3. **Connection limits** — `net.ListenConfig{Control: setSocketOpts}` — SO_REUSEPORT
4. **Buffer pooling** — `sync.Pool` bilan read/write buffer reuse
5. **Timeouts** — `SetReadDeadline`, `SetWriteDeadline` — stale connection cleanup
6. **GOMEMLIMIT** — container memory'ning 70% ga o'rnatish
7. **Monitoring** — goroutine count, connection count, latency p99, GC pause

**OS tuning:**
```bash
# File descriptor limit
ulimit -n 1048576
# TCP tuning
sysctl -w net.core.somaxconn=65535
sysctl -w net.ipv4.tcp_max_syn_backlog=65535
```

---

### Scenario 5: Sizning Go loyihangizda race condition topildi. Qanday investigate va fix qilasiz?

**Answer:**

**Step 1: Race detector bilan aniqlash**
```bash
go test -race ./...
go run -race main.go
```

Output:
```
WARNING: DATA RACE
Write at 0x00c0000b4020 by goroutine 7:
  main.worker()
      main.go:15 +0x5c

Previous read at 0x00c0000b4020 by goroutine 6:
  main.main()
      main.go:22 +0x9c
```

**Step 2: Root cause analysis**
- Qaysi o'zgaruvchiga concurrent access?
- Goroutine'lar o'rtasida synchronization bormi?
- Channel/Mutex kerakmi?

**Step 3: Fix variants**
1. **sync.Mutex** — shared state himoyalash
2. **sync/atomic** — oddiy counter/flag uchun
3. **Channel** — ownership transfer
4. **sync.RWMutex** — read-heavy workload
5. **Redesign** — shared state'ni yo'qotish (per-goroutine data)

**Step 4: Prevention**
- CI/CD da `-race` flag mandatory
- Code review: shared state + goroutine = red flag
- Linting: `go vet`, `staticcheck`

---

## 5. FAQ

### FAQ 1: "Go o'rganishga arzidimi? Go'ning kelajagi qanday?"

**Answer:**

**Interviewerlar nima kutadi:** Go'ning ekosistema va market holatini bilishingiz.

**Javob:**
Ha, Go o'rganishga arziydi. Dalillar:
1. **Market demand** — Go developer'lar uchun talab o'sib bormoqda (Stack Overflow Survey: eng yaxshi ish haqi tillardan biri)
2. **Cloud-native standard** — Kubernetes, Docker, Terraform, Prometheus — hammasi Go
3. **Google qo'llab-quvvatlaydi** — 15+ yil, active development (Go 1.22+)
4. **CNCF ecosystem** — Cloud Native Computing Foundation loyihalarining 70%+ i Go'da
5. **Hiring companies** — Google, Uber, Cloudflare, Twitch, Monzo, American Express

**Lekin:** Go hamma narsa uchun emas. ML/AI — Python, mobile — Swift/Kotlin, frontend — JS/TS. Go — backend, infrastructure, cloud-native uchun eng yaxshi.

---

### FAQ 2: "Go'da OOP bormi?"

**Answer:**

**Interviewerlar nima kutadi:** OOP tamoyillarini Go kontekstida tushunganingiz.

**Javob:**
Go'da **klassik OOP yo'q** (class, inheritance). Lekin OOP tamoyillarini qo'llash mumkin:

| OOP tamoyili | Go'dagi ekvivalent |
|-------------|-------------------|
| **Encapsulation** | Exported/unexported (bosh/kichik harf) |
| **Abstraction** | Interface |
| **Polymorphism** | Interface satisfaction |
| **Inheritance** | ❌ Yo'q → **Composition** (struct embedding) |

```go
// Composition > Inheritance
type Logger struct{}
func (l Logger) Log(msg string) { fmt.Println(msg) }

type UserService struct {
    Logger      // Embedding — Logger ning method'lari UserService da
    db Database
}
```

Go ataylab inheritance'ni olib tashlagan: fragile base class, diamond problem, tight coupling muammolari yo'q.

---

### FAQ 3: "Go'da generics bormi? Ular yetarlimi?"

**Answer:**

**Interviewerlar nima kutadi:** Generics evolyutsiyasi va cheklovlarini bilishingiz.

**Javob:**
Go 1.18 (2022) dan generics qo'shildi. Lekin Java/Rust darajasida emas — **ataylab sodda** qilingan.

**Bor:**
```go
func Max[T constraints.Ordered](a, b T) T {
    if a > b { return a }
    return b
}
```

**Cheklovlar:**
- Method'larda type parameter yo'q (`func (s Stack[T]) Push(v T)` ishlaydi)
- Operator overloading yo'q
- Specialization yo'q
- Higher-kinded types yo'q

**Amalda:** 90% use-case uchun yetarli. Qolgan 10% uchun `interface{}` + type assertion yoki code generation.

---

### FAQ 4: "Nima uchun Go'da unused variable va import xato beradi?"

**Answer:**

**Interviewerlar nima kutadi:** Go'ning design philosophy'sini tushunganingiz.

**Javob:**
Bu Go'ning **opinionated design** qarorlaridan biri. Sabablari:

1. **Toza kod** — production'ga keraksiz import/variable bormaydi
2. **Xatolarni erta topish** — o'zgaruvchi yaratib unutish ko'p uchraydigan bug
3. **O'qish oson** — hamma narsa ishlatilyapti deb ishonch hosil qilasiz
4. **Code review** — reviewer "bu nima uchun kerak?" deb so'ramaydi

**Workaround:**
```go
_ = unusedVariable     // Blank identifier
import _ "side/effect" // Side-effect import
```

Bu qaror ba'zan noixting tuyuladi (development paytida), lekin katta jamoalarda va production'da **juda foydali**.

---

### FAQ 5: "Go web framework tanlashda nimaga e'tibor berish kerak?"

**Answer:**

**Interviewerlar nima kutadi:** Ecosystem bilimingiz va framework tanlash strategiyangiz.

**Javob:**

| Framework | Xususiyat | Qachon tanlash |
|-----------|-----------|---------------|
| **net/http** (stdlib) | Minimal, dependency yo'q | Oddiy API, max kontrol |
| **Gin** | Eng mashhur, tez, middleware rich | Ko'p hollarda default tanlov |
| **Echo** | Gin'ga o'xshash, yaxshi docs | Gin alternativasi |
| **Fiber** | Express.js style, fasthttp | Node.js dan kelayotgan jamoa |
| **Chi** | net/http compatible, lightweight | stdlib yaqin, middleware kerak |
| **gRPC** | Protocol Buffers, streaming | Microservice aro aloqa |

**Tanlash criteria:**
1. **Performance requirement** — Gin/Echo yetarli (99% hollarda)
2. **Team experience** — Node.js jamoasi → Fiber, stdlib purists → Chi
3. **Ecosystem** — Gin'ning middleware ekosistemi eng katta
4. **net/http compatibility** — Chi va Echo stdlib bilan compatible
5. **Production proven** — Gin va gRPC eng ko'p production'da ishlatiladi

**My recommendation:** Yangi loyiha uchun **Gin** (REST) yoki **gRPC** (microservice). Oddiy loyiha uchun **net/http** stdlib yetarli.
