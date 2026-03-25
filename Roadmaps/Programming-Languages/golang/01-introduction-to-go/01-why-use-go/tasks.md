# Why Use Go — Practical Tasks

## Table of Contents
1. [Junior Tasks](#junior-tasks)
2. [Middle Tasks](#middle-tasks)
3. [Senior Tasks](#senior-tasks)
4. [Questions](#questions)
5. [Mini Projects](#mini-projects)
6. [Challenge](#challenge)

---

## 1. Junior Tasks

### Task 1: Go Environment Info Tool

**Maqsad:** Go'ning runtime ma'lumotlarini ko'rsatadigan CLI dastur yozing.

**Starter Code:**

```go
package main

import (
    "fmt"
    "runtime"
)

func main() {
    // TODO: Quyidagi ma'lumotlarni chiqaring:
    // 1. Go versiyasi
    // 2. Operatsion tizim (GOOS)
    // 3. Arxitektura (GOARCH)
    // 4. CPU yadrolari soni
    // 5. Goroutine'lar soni

    fmt.Println("=== Go Environment Info ===")

    // Sizning kodingiz shu yerda...
}
```

**Expected Output:**
```
=== Go Environment Info ===
Go Version: go1.22.0
OS:         linux
Arch:       amd64
CPUs:       8
Goroutines: 1
```

**Evaluation Criteria:**
- [ ] `runtime` package'dan to'g'ri funksiyalar ishlatilgan
- [ ] Output formatli va o'qish oson
- [ ] Dastur xatosiz kompilyatsiya bo'ladi
- [ ] Har bir ma'lumot to'g'ri chiqarilgan

**Yechim:**

<details><summary>Yechimni ko'rish</summary>

```go
package main

import (
    "fmt"
    "runtime"
)

func main() {
    fmt.Println("=== Go Environment Info ===")
    fmt.Printf("Go Version: %s\n", runtime.Version())
    fmt.Printf("OS:         %s\n", runtime.GOOS)
    fmt.Printf("Arch:       %s\n", runtime.GOARCH)
    fmt.Printf("CPUs:       %d\n", runtime.NumCPU())
    fmt.Printf("Goroutines: %d\n", runtime.NumGoroutine())
}
```
</details>

---

### Task 2: Go vs Python Tezlik Taqqoslash

**Maqsad:** 1 dan 100 milliongacha sonlar yig'indisini hisoblash va vaqtni o'lchash.

**Starter Code:**

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    // TODO:
    // 1. Hozirgi vaqtni saqlang (time.Now())
    // 2. 1 dan 100_000_000 gacha sonlar yig'indisini hisoblang
    // 3. O'tgan vaqtni hisoblang (time.Since())
    // 4. Natija va vaqtni chiqaring

    fmt.Println("=== Tezlik Testi ===")

    // Sizning kodingiz shu yerda...
}
```

**Expected Output:**
```
=== Tezlik Testi ===
Yig'indi: 4999999950000000
Vaqt:     42.567ms
```

**Evaluation Criteria:**
- [ ] `time.Now()` va `time.Since()` to'g'ri ishlatilgan
- [ ] Loop to'g'ri ishlaydi (0 dan 99_999_999 gacha yoki 1 dan 100_000_000 gacha)
- [ ] Natija to'g'ri: 4999999950000000
- [ ] Vaqt millisekund yoki mikrosekund formatda

**Yechim:**

<details><summary>Yechimni ko'rish</summary>

```go
package main

import (
    "fmt"
    "time"
)

func main() {
    fmt.Println("=== Tezlik Testi ===")

    start := time.Now()

    var sum int64
    for i := int64(0); i < 100_000_000; i++ {
        sum += i
    }

    elapsed := time.Since(start)

    fmt.Printf("Yig'indi: %d\n", sum)
    fmt.Printf("Vaqt:     %v\n", elapsed)
}
```
</details>

---

### Task 3: Oddiy Goroutine Misoli

**Maqsad:** 5 ta goroutine yarating, har biri o'z nomini va raqamini chiqarsin.

**Starter Code:**

```go
package main

import (
    "fmt"
    "sync"
)

func worker(id int, wg *sync.WaitGroup) {
    // TODO:
    // 1. defer bilan wg.Done() chaqiring
    // 2. "Worker {id}: ishni boshladi" chiqaring
    // 3. "Worker {id}: ishni tugatdi" chiqaring
}

func main() {
    var wg sync.WaitGroup

    fmt.Println("=== Goroutine Demo ===")

    for i := 1; i <= 5; i++ {
        wg.Add(1)
        // TODO: worker funksiyasini goroutine sifatida ishga tushiring
    }

    // TODO: Barcha goroutine'lar tugashini kuting
    fmt.Println("Barcha ishlar tugadi!")
}
```

**Expected Output (tartib o'zgarishi mumkin):**
```
=== Goroutine Demo ===
Worker 3: ishni boshladi
Worker 1: ishni boshladi
Worker 5: ishni boshladi
Worker 2: ishni boshladi
Worker 4: ishni boshladi
Worker 3: ishni tugatdi
Worker 1: ishni tugatdi
Worker 5: ishni tugatdi
Worker 2: ishni tugatdi
Worker 4: ishni tugatdi
Barcha ishlar tugadi!
```

**Evaluation Criteria:**
- [ ] `sync.WaitGroup` to'g'ri ishlatilgan (`Add`, `Done`, `Wait`)
- [ ] `go worker(i, &wg)` to'g'ri chaqirilgan
- [ ] `defer wg.Done()` ishlatilgan
- [ ] "Barcha ishlar tugadi!" oxirida chiqadi (goroutine'lardan keyin)
- [ ] Race condition yo'q

**Yechim:**

<details><summary>Yechimni ko'rish</summary>

```go
package main

import (
    "fmt"
    "sync"
)

func worker(id int, wg *sync.WaitGroup) {
    defer wg.Done()
    fmt.Printf("Worker %d: ishni boshladi\n", id)
    fmt.Printf("Worker %d: ishni tugatdi\n", id)
}

func main() {
    var wg sync.WaitGroup

    fmt.Println("=== Goroutine Demo ===")

    for i := 1; i <= 5; i++ {
        wg.Add(1)
        go worker(i, &wg)
    }

    wg.Wait()
    fmt.Println("Barcha ishlar tugadi!")
}
```
</details>

---

### Task 4: Cross-Platform Build Script

**Maqsad:** Oddiy Go dastur yozing va uni 3 ta platformaga build qiling.

**Starter Code:**

```go
package main

import (
    "fmt"
    "runtime"
)

func main() {
    fmt.Printf("Bu dastur %s/%s da ishlayapti\n", runtime.GOOS, runtime.GOARCH)
    fmt.Println("Go cross-compilation ishlaydi!")
}
```

**Vazifa:**
1. Yuqoridagi kodni `main.go` faylga saqlang
2. Quyidagi buyruqlarni ishga tushiring:

```bash
# TODO: Linux uchun build
# GOOS=??? GOARCH=??? go build -o myapp-linux main.go

# TODO: Windows uchun build
# GOOS=??? GOARCH=??? go build -o myapp.exe main.go

# TODO: macOS uchun build
# GOOS=??? GOARCH=??? go build -o myapp-mac main.go

# TODO: Barcha fayllar hajmini ko'ring
# ls -lh myapp-*
```

**Expected Output:**
```
-rwxr-xr-x 1 user user 1.8M myapp-linux
-rwxr-xr-x 1 user user 1.9M myapp-mac
-rwxr-xr-x 1 user user 1.9M myapp.exe
```

**Evaluation Criteria:**
- [ ] GOOS va GOARCH to'g'ri qiymatlar ishlatilgan
- [ ] Barcha 3 ta binary yaratilgan
- [ ] Binary hajmi taxminan 1.5-2.5MB atrofida
- [ ] `file myapp-linux` buyrug'i to'g'ri format ko'rsatadi

**Yechim:**

<details><summary>Yechimni ko'rish</summary>

```bash
# Linux uchun
GOOS=linux GOARCH=amd64 go build -o myapp-linux main.go

# Windows uchun
GOOS=windows GOARCH=amd64 go build -o myapp.exe main.go

# macOS uchun
GOOS=darwin GOARCH=amd64 go build -o myapp-mac main.go

# Fayllar hajmi
ls -lh myapp-*

# Fayl formatini tekshirish
file myapp-linux   # ELF 64-bit LSB executable
file myapp.exe     # PE32+ executable
file myapp-mac     # Mach-O 64-bit x86_64 executable
```
</details>

---

## 2. Middle Tasks

### Task 1: Worker Pool bilan Concurrent URL Checker

**Scenario:** Siz DevOps jamoasida ishlaysiz. 100+ web saytning availability'sini tekshirish kerak. Sequential tekshirish juda sekin, concurrent tekshirish kerak.

**Requirements:**
1. URL'lar ro'yxatini oluvchi funksiya yozing
2. Worker pool pattern ishlatib, parallel tekshiring
3. Har bir URL uchun status code va response vaqtini chiqaring
4. Worker soni configurable bo'lsin
5. Timeout'li context ishlatilsin
6. Natijalarni table formatda chiqaring

**Hints:**
- `net/http` package ishlatiladi
- `sync.WaitGroup` goroutine'lar uchun
- `context.WithTimeout` timeout uchun
- Channel'lar worker pool uchun

**Starter Code:**

```go
package main

import (
    "context"
    "fmt"
    "net/http"
    "sync"
    "time"
)

type Result struct {
    URL        string
    StatusCode int
    Duration   time.Duration
    Error      string
}

func checkURL(ctx context.Context, url string) Result {
    // TODO: HTTP GET request yuborish
    // TODO: Status code va duration qaytarish
    // TODO: Error bo'lsa, Error field'ni to'ldirish
    return Result{}
}

func worker(ctx context.Context, id int, urls <-chan string, results chan<- Result, wg *sync.WaitGroup) {
    // TODO: urls channel'dan URL olish va tekshirish
    // TODO: Natijani results channel'ga yuborish
}

func main() {
    urls := []string{
        "https://go.dev",
        "https://google.com",
        "https://github.com",
        "https://example.com",
        "https://httpstat.us/500",
        "https://nonexistent.invalid",
    }

    // TODO: Worker pool yaratish
    // TODO: Natijalarni yig'ish va table formatda chiqarish
}
```

**Expected Output:**
```
=== URL Checker (3 workers, 5s timeout) ===
+---+----------------------------+--------+-----------+-------+
| # | URL                        | Status | Duration  | Error |
+---+----------------------------+--------+-----------+-------+
| 1 | https://go.dev             | 200    | 245.3ms   |       |
| 2 | https://google.com         | 200    | 132.1ms   |       |
| 3 | https://github.com         | 200    | 189.7ms   |       |
| 4 | https://example.com        | 200    | 98.4ms    |       |
| 5 | https://httpstat.us/500     | 500    | 1.2s      |       |
| 6 | https://nonexistent.invalid| 0      | 2.1s      | DNS   |
+---+----------------------------+--------+-----------+-------+
Total: 6 URLs checked in 2.3s
```

**Evaluation Criteria:**
- [ ] Worker pool pattern to'g'ri implementatsiya qilingan
- [ ] Context timeout ishlaydi
- [ ] Race condition yo'q (`go run -race` bilan tekshirish)
- [ ] Barcha goroutine'lar to'g'ri tugaydi (no leak)
- [ ] Error handling mavjud
- [ ] Output formatli

**Yechim:**

<details><summary>Yechimni ko'rish</summary>

```go
package main

import (
    "context"
    "fmt"
    "net/http"
    "sync"
    "time"
)

type Result struct {
    URL        string
    StatusCode int
    Duration   time.Duration
    Error      string
}

func checkURL(ctx context.Context, url string) Result {
    start := time.Now()
    req, err := http.NewRequestWithContext(ctx, "GET", url, nil)
    if err != nil {
        return Result{URL: url, Duration: time.Since(start), Error: err.Error()}
    }

    resp, err := http.DefaultClient.Do(req)
    if err != nil {
        return Result{URL: url, Duration: time.Since(start), Error: err.Error()}
    }
    defer resp.Body.Close()

    return Result{URL: url, StatusCode: resp.StatusCode, Duration: time.Since(start)}
}

func worker(ctx context.Context, id int, urls <-chan string, results chan<- Result, wg *sync.WaitGroup) {
    defer wg.Done()
    for url := range urls {
        results <- checkURL(ctx, url)
    }
}

func main() {
    urls := []string{
        "https://go.dev",
        "https://google.com",
        "https://github.com",
        "https://example.com",
        "https://httpstat.us/200",
        "https://nonexistent.invalid",
    }

    numWorkers := 3
    ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
    defer cancel()

    urlsCh := make(chan string, len(urls))
    resultsCh := make(chan Result, len(urls))

    var wg sync.WaitGroup
    for w := 0; w < numWorkers; w++ {
        wg.Add(1)
        go worker(ctx, w, urlsCh, resultsCh, &wg)
    }

    start := time.Now()
    for _, url := range urls {
        urlsCh <- url
    }
    close(urlsCh)

    go func() {
        wg.Wait()
        close(resultsCh)
    }()

    fmt.Printf("=== URL Checker (%d workers, 10s timeout) ===\n", numWorkers)
    fmt.Printf("%-4s %-35s %-8s %-12s %s\n", "#", "URL", "Status", "Duration", "Error")
    fmt.Println("---  -----------------------------------  ------  ----------  -----")

    i := 1
    for r := range resultsCh {
        errMsg := ""
        if r.Error != "" {
            errMsg = "Error"
        }
        fmt.Printf("%-4d %-35s %-8d %-12v %s\n", i, r.URL, r.StatusCode, r.Duration.Round(time.Millisecond), errMsg)
        i++
    }

    fmt.Printf("\nTotal: %d URLs checked in %v\n", len(urls), time.Since(start).Round(time.Millisecond))
}
```
</details>

---

### Task 2: Benchmark — String Concatenation Methods

**Scenario:** Go'da string birlashtirish usullarini benchmark qiling va natijalarni taqqoslang.

**Requirements:**
1. 4 ta usulni implementatsiya qiling: `+` operator, `fmt.Sprintf`, `strings.Builder`, `bytes.Buffer`
2. Har birini N=10000, N=100000, N=1000000 bilan test qiling
3. Vaqt va xotira (taxminiy) ni o'lchang
4. Table formatda natijalarni chiqaring
5. Eng tez usulni aniqlang

**Hints:**
- `time.Now()` va `time.Since()` vaqt o'lchash uchun
- `runtime.MemStats` xotira o'lchash uchun
- `strings.Builder` da `Grow()` ishlatish

**Starter Code:**

```go
package main

import (
    "bytes"
    "fmt"
    "runtime"
    "strings"
    "time"
)

func concatPlus(n int) string {
    // TODO: + operator bilan
    return ""
}

func concatSprintf(n int) string {
    // TODO: fmt.Sprintf bilan
    return ""
}

func concatBuilder(n int) string {
    // TODO: strings.Builder bilan (Grow ishlatish)
    return ""
}

func concatBuffer(n int) string {
    // TODO: bytes.Buffer bilan
    return ""
}

func benchmark(name string, fn func(int) string, n int) time.Duration {
    // TODO: Funksiyani ishga tushirish va vaqtni qaytarish
    return 0
}

func main() {
    sizes := []int{10000, 100000}

    // TODO: Barcha usullarni har bir size bilan benchmark qilish
    // TODO: Table formatda chiqarish
}
```

**Evaluation Criteria:**
- [ ] Barcha 4 ta usul to'g'ri implementatsiya qilingan
- [ ] Benchmark natijalar izchil va mantiqiy
- [ ] `strings.Builder` `Grow()` bilan ishlatilgan
- [ ] Table format o'qish oson
- [ ] Xulosa: qaysi usul eng yaxshi va nima uchun

**Yechim:**

<details><summary>Yechimni ko'rish</summary>

```go
package main

import (
    "bytes"
    "fmt"
    "runtime"
    "strings"
    "time"
)

func concatPlus(n int) string {
    s := ""
    for i := 0; i < n; i++ {
        s += "x"
    }
    return s
}

func concatSprintf(n int) string {
    s := ""
    for i := 0; i < n; i++ {
        s = fmt.Sprintf("%sx", s)
    }
    return s
}

func concatBuilder(n int) string {
    var b strings.Builder
    b.Grow(n)
    for i := 0; i < n; i++ {
        b.WriteString("x")
    }
    return b.String()
}

func concatBuffer(n int) string {
    var buf bytes.Buffer
    buf.Grow(n)
    for i := 0; i < n; i++ {
        buf.WriteString("x")
    }
    return buf.String()
}

func getMemAlloc() uint64 {
    var m runtime.MemStats
    runtime.ReadMemStats(&m)
    return m.TotalAlloc
}

func benchmark(name string, fn func(int) string, n int) (time.Duration, uint64) {
    runtime.GC()
    memBefore := getMemAlloc()
    start := time.Now()
    _ = fn(n)
    elapsed := time.Since(start)
    memAfter := getMemAlloc()
    return elapsed, memAfter - memBefore
}

func main() {
    sizes := []int{10000, 100000}

    methods := []struct {
        name string
        fn   func(int) string
    }{
        {"String +", concatPlus},
        {"fmt.Sprintf", concatSprintf},
        {"strings.Builder", concatBuilder},
        {"bytes.Buffer", concatBuffer},
    }

    for _, size := range sizes {
        fmt.Printf("\n=== Benchmark: N = %d ===\n", size)
        fmt.Printf("%-20s %15s %15s\n", "Method", "Duration", "Memory (KB)")
        fmt.Println(strings.Repeat("-", 52))

        for _, m := range methods {
            if m.name == "String +" && size > 50000 {
                fmt.Printf("%-20s %15s %15s\n", m.name, "SKIPPED", "(too slow)")
                continue
            }
            if m.name == "fmt.Sprintf" && size > 50000 {
                fmt.Printf("%-20s %15s %15s\n", m.name, "SKIPPED", "(too slow)")
                continue
            }
            dur, mem := benchmark(m.name, m.fn, size)
            fmt.Printf("%-20s %15v %12d KB\n", m.name, dur, mem/1024)
        }
    }

    fmt.Println("\nXulosa: strings.Builder eng tez va xotira samarali usul.")
    fmt.Println("Sabab: Grow() bilan bitta marta allocate, keyin append.")
}
```
</details>

---

### Task 3: Graceful Shutdown HTTP Server

**Scenario:** Production HTTP server yozing va uni graceful shutdown bilan to'xtating.

**Requirements:**
1. HTTP server yarating (port 8080)
2. `/` endpoint — "Hello, Go!" qaytarsin
3. `/health` endpoint — `{"status": "ok"}` qaytarsin
4. SIGINT/SIGTERM signal'larini tutish
5. Graceful shutdown (30 sekund timeout)
6. Active connection'lar tugashini kutish

**Hints:**
- `os/signal` package signal tutish uchun
- `http.Server.Shutdown()` graceful shutdown uchun
- `context.WithTimeout` shutdown timeout uchun

**Evaluation Criteria:**
- [ ] Server to'g'ri ishga tushadi
- [ ] Signal handler to'g'ri ishlaydi
- [ ] Graceful shutdown active request'larni kutadi
- [ ] Timeout mavjud
- [ ] Log message'lar informatif

---

## 3. Senior Tasks

### Task 1: Go vs Python Performance Comparison Tool

**Architecture Focus:** Go dastur yozing, u turli xil benchmark'larni bajarib, natijalarni taqqoslaydi.

**Requirements:**
1. Quyidagi benchmark'larni implementatsiya qiling:
   - **CPU-bound:** Fibonachchi hisoblash (recursive va iterative)
   - **Memory-bound:** Katta array yaratish va sort qilish
   - **Concurrency:** N ta goroutine bilan parallel yig'indi
   - **IO-bound:** Fayl yozish/o'qish tezligi
2. Har bir benchmark uchun `testing.B` style benchmark yozing
3. Memory allocatsiya statistikasini ko'rsating (`runtime.MemStats`)
4. Natijalarni JSON formatda eksport qiling
5. Comparison jadval yarating (Go estimated vs Python estimated)

**Benchmark Requirements:**
```bash
# Ishga tushirish
go test -bench=. -benchmem -count=3

# Yoki standalone
go run main.go --benchmarks=all --output=results.json
```

**Evaluation Criteria:**
- [ ] Barcha 4 ta benchmark kategoriyasi implementatsiya qilingan
- [ ] `testing.B` benchmark to'g'ri yozilgan
- [ ] Memory statistika to'g'ri o'lchangan
- [ ] JSON output to'g'ri formatda
- [ ] Comparison jadval mantiqiy va dalillangan

**Starter Structure:**

```go
package main

import (
    "encoding/json"
    "fmt"
    "math/rand"
    "os"
    "runtime"
    "sort"
    "time"
)

type BenchmarkResult struct {
    Name       string        `json:"name"`
    Duration   time.Duration `json:"duration_ns"`
    Iterations int           `json:"iterations"`
    AllocBytes uint64        `json:"alloc_bytes"`
    AllocCount uint64        `json:"alloc_count"`
}

func benchmarkFibIterative(n int) BenchmarkResult {
    // TODO: Iterative fibonacci benchmark
    return BenchmarkResult{}
}

func benchmarkSort(size int) BenchmarkResult {
    // TODO: Array sort benchmark
    return BenchmarkResult{}
}

func benchmarkConcurrency(workers, tasks int) BenchmarkResult {
    // TODO: Concurrent worker benchmark
    return BenchmarkResult{}
}

func benchmarkFileIO(sizeMB int) BenchmarkResult {
    // TODO: File write/read benchmark
    return BenchmarkResult{}
}

func main() {
    results := []BenchmarkResult{
        benchmarkFibIterative(40),
        benchmarkSort(1_000_000),
        benchmarkConcurrency(10, 1000),
        benchmarkFileIO(100),
    }

    // TODO: Natijalarni table va JSON formatda chiqarish
    data, _ := json.MarshalIndent(results, "", "  ")
    fmt.Println(string(data))

    // TODO: results.json faylga yozish
    os.WriteFile("results.json", data, 0644)
}
```

---

### Task 2: Architecture Decision Tool

**Architecture Focus:** CLI tool yozing, u savollarga javob asosida Go vs boshqa tillarni tavsiya qiladi.

**Requirements:**
1. Interactive CLI (stdin dan input olish)
2. Quyidagi faktorlarni baholash:
   - Team size va Go experience
   - Performance requirements (latency, throughput)
   - Deployment environment (container, serverless, bare metal)
   - Project type (API, CLI, data pipeline, etc.)
   - Ecosystem needs (ML, GUI, etc.)
3. Weighted scoring algorithm
4. Recommendation chiqarish: Go, Python, Java, Rust, Node.js
5. Justification bilan

**Evaluation Criteria:**
- [ ] Scoring algorithm mantiqiy va izchil
- [ ] Barcha tillar uchun fair baholash
- [ ] CLI user-friendly
- [ ] Recommendation dalillangan
- [ ] Edge case'lar handled (masalan: ML + high performance)

---

### Task 3: Production-Ready Health Check Service

**Architecture Focus:** Microservice health check va monitoring service yozing.

**Requirements:**
1. HTTP server: `/healthz` (liveness), `/readyz` (readiness), `/metrics`
2. External dependency check: database ping, redis ping, external API check
3. Circuit breaker pattern dependency check'lar uchun
4. Prometheus-compatible metrics endpoint
5. Concurrent health checks (parallel dependency checking)
6. Configurable check intervals va timeouts
7. Graceful degradation (agar dependency down bo'lsa, partial health)

**Benchmark Required:**
```bash
# Load test
hey -n 10000 -c 100 http://localhost:8080/healthz

# Memory profiling
go tool pprof http://localhost:6060/debug/pprof/heap
```

**Evaluation Criteria:**
- [ ] Barcha endpoint'lar to'g'ri ishlaydi
- [ ] Circuit breaker implemented
- [ ] Concurrent dependency checks
- [ ] Benchmark natijalar: <5ms p99 for /healthz
- [ ] Graceful degradation ishlaydi
- [ ] Memory leak yo'q (pprof bilan tekshirish)

---

## 4. Questions

### Savol 1: Go nima uchun bitta binary fayl yaratadi va bu qanday afzallik beradi?

**Javob:**

Go **static linking** ishlatadi — barcha dependency'lar (standart kutubxona, uchinchi tomon paketlar, Go runtime) bitta binary faylga joylashtiriladi.

**Afzalliklari:**
1. **Deploy sodda** — bitta faylni ko'chirasiz, tamom. `apt install`, `pip install`, JRE kerak emas
2. **Container image kichik** — `scratch` yoki `distroless` image ishlatish mumkin (~5-15MB)
3. **Dependency hell yo'q** — "mening kompyuterimda ishlaydi" muammosi yo'q
4. **Cross-compilation oson** — `GOOS=linux go build` va tamom
5. **Reproducible builds** — bir xil input → bir xil output

**Kamchiligi:** Binary hajmi katta (5-20MB), chunki hamma narsa ichida.

---

### Savol 2: Goroutine va OS thread farqi nimada? Goroutine qanday schedule bo'ladi?

**Javob:**

Goroutine — Go runtime tomonidan boshqariladigan yengil execution unit. OS thread'dan farqi:

| Xususiyat | Goroutine | OS Thread |
|-----------|-----------|-----------|
| Xotira | 2-8 KB (growable) | 1-8 MB (fixed) |
| Yaratish | ~300 ns | ~50 μs |
| Context switch | ~100 ns (userspace) | ~1-10 μs (kernel) |
| Boshqarish | Go GMP scheduler | OS kernel scheduler |

**Scheduling:** GMP model — Goroutine'lar P (Processor) ga tayinlanadi, P M (OS Thread) da execute qiladi. Work stealing orqali load balance bo'ladi.

---

### Savol 3: Go'da memory leak qanday yuz beradi? GC bor-ku?

**Javob:**

GC reachable ob'ektlarni free qila olmaydi. Memory leak sabablari:

1. **Goroutine leak** — blocked goroutine GC tomonidan yig'ilmaydi
2. **Global map/slice** — o'sib boruvchi global data structure
3. **Slice memory retention** — katta slice'dan kichik sub-slice olish (original array band qoladi)
4. **time.Ticker** — `Stop()` chaqirilmasa tick goroutine qoladi
5. **HTTP response body** — `Close()` chaqirilmasa connection band

**Diagnostika:** `go tool pprof`, `runtime.NumGoroutine()`, `GODEBUG=gctrace=1`

---

### Savol 4: Go'da `defer` qanday ishlaydi va qachon evaluate bo'ladi?

**Javob:**

`defer` — funksiya tugaganda (return yoki panic) bajariladigan kodni rejalashtiradi.

**Muhim nuanslar:**
1. **LIFO tartib** — oxirgi defer birinchi bajariladi (stack)
2. **Argument evaluate** — defer chaqirilgan vaqtda (funksiya tugaganda emas!)
3. **Named return** — defer named return value'ni o'zgartirishi mumkin

```go
func example() int {
    x := 10
    defer fmt.Println(x) // 10 chiqadi (hozir evaluate bo'ladi)
    x = 20
    return x // return 20, defer 10 chiqaradi
}
```

---

### Savol 5: Go'da interface nil gotcha nima?

**Javob:**

Go interface ikkita qismdan iborat: `(type, value)`. Nil pointer ni interface ga assign qilganda, interface nil emas:

```go
var p *MyType = nil
var i interface{} = p
// i != nil — true! Chunki i = (*MyType, nil)

// To'g'ri yondashuv:
// return nil (interface type emas, bare nil)
```

Bu Go'ning eng keng tarqalgan gotcha'laridan biri, ayniqsa error return'larda.

---

### Savol 6: `GOMAXPROCS` nima va qachon o'zgartirish kerak?

**Javob:**

`GOMAXPROCS` — bir vaqtda goroutine execute qilishi mumkin bo'lgan P (Processor) soni. Default: `runtime.NumCPU()`.

**Qachon o'zgartirish kerak:**
- **Container'da:** CPU limit va GOMAXPROCS mos emas bo'lishi mumkin (container 2 CPU, host 64 CPU). `uber-go/automaxprocs` kutubxonasi avtomatik hal qiladi.
- **I/O-bound service:** Odatda default yaxshi
- **CPU-bound service:** Default yaxshi

**Qachon o'zgartirmang:** Ko'p hollarda default optimal. Profiling asosida qaror qiling.

---

### Savol 7: Go'da channel va mutex farqi nima? Qachon qaysi birini ishlatish kerak?

**Javob:**

| Aspect | Channel | Mutex |
|--------|---------|-------|
| **Maqsad** | Data transfer, signaling | Shared state protection |
| **Pattern** | Producer-consumer, fan-out/fan-in | Critical section |
| **Ownership** | Data ownership transfer | Shared access |
| **Blocking** | Receive/send blocking | Lock contention |

**Channel ishlatish:** Goroutine'lar o'rtasida data uzatish, pipeline, worker pool.
**Mutex ishlatish:** Counter, cache, shared map himoyalash.

**Qoida:** "If in doubt, use a mutex." — Dmitry Vyukov (Go team)

---

### Savol 8: Go'da `context.Context` qanday propagate qilinadi?

**Javob:**

Context tree sifatida propagate bo'ladi — parent cancel bo'lsa, barcha child'lar ham cancel:

```
Background (root)
├── WithTimeout(5s) — API handler
│   ├── WithValue(requestID) — logging
│   │   ├── DB query (inherits timeout)
│   │   └── Redis query (inherits timeout)
│   └── External API call (inherits timeout)
```

**Qoidalar:**
1. Context har doim birinchi parameter
2. Context'ni struct'da saqlang emas
3. `nil` context uzatmang — `context.TODO()` ishlatish
4. Child context parent'dan ko'p yashamaydi

---

### Savol 9: Go binary hajmini qanday kichiklashtirish mumkin?

**Javob:**

1. **Debug info o'chirish:** `go build -ldflags="-s -w"` (~30% kamayadi)
2. **UPX compression:** `upx --best myapp` (~50-70% kamayadi)
3. **TinyGo:** Embedded/WASM uchun (ancha kichik binary)
4. **Keraksiz import'larni o'chirish:** `go mod tidy`

**Natija:** 20MB → 14MB → 5MB

---

### Savol 10: Go'ning GC qanday ishlaydi va tuning qanday qilinadi?

**Javob:**

Go **concurrent tri-color mark & sweep** GC ishlatadi:
1. Mark Setup (STW ~10-30μs)
2. Marking (concurrent — goroutine'lar ishlaydi)
3. Mark Termination (STW ~10-30μs)
4. Sweeping (concurrent)

**Tuning:**
- `GOGC=100` (default) — heap 100% o'sganda GC
- `GOGC=50` — tez-tez GC, kam memory
- `GOMEMLIMIT=512MiB` — soft memory limit (Go 1.19+)

---

## 5. Mini Projects

### Mini Project 1: "GoCompare" — Language Comparison CLI Tool

**Maqsad:** CLI tool yozing, u Go'ni boshqa tillar bilan taqqoslaydi va interaktiv natija beradi.

**Funksionallik:**
1. `gocompare info` — Go haqida asosiy ma'lumotlar
2. `gocompare vs python` — Go vs Python taqqoslash
3. `gocompare vs rust` — Go vs Rust taqqoslash
4. `gocompare benchmark` — Local benchmark'lar (CPU, memory, concurrency)
5. `gocompare recommend` — Interactive recommendation (savol-javob)

**Texnologiyalar:**
- `flag` yoki `os.Args` CLI argument'lar uchun
- `runtime` package benchmark uchun
- `encoding/json` natijalarni saqlash uchun
- `fmt` formatli output uchun

**Starter Code:**

```go
package main

import (
    "encoding/json"
    "fmt"
    "os"
    "runtime"
    "strings"
    "time"
)

type LanguageInfo struct {
    Name        string   `json:"name"`
    Year        int      `json:"year"`
    Creator     string   `json:"creator"`
    Type        string   `json:"type"`
    UseCases    []string `json:"use_cases"`
    Pros        []string `json:"pros"`
    Cons        []string `json:"cons"`
}

var languages = map[string]LanguageInfo{
    "go": {
        Name:     "Go",
        Year:     2009,
        Creator:  "Google (Robert Griesemer, Rob Pike, Ken Thompson)",
        Type:     "Compiled, Static",
        UseCases: []string{"Backend", "CLI", "DevOps", "Cloud-native"},
        Pros:     []string{"Fast compilation", "Goroutines", "Single binary", "Simple syntax"},
        Cons:     []string{"No generics (limited)", "No OOP", "Verbose error handling"},
    },
    // TODO: Python, Rust, Java, Node.js qo'shish
}

func showInfo() {
    info := languages["go"]
    fmt.Printf("=== %s ===\n", info.Name)
    fmt.Printf("Year: %d\n", info.Year)
    fmt.Printf("Creator: %s\n", info.Creator)
    fmt.Printf("Type: %s\n", info.Type)
    // TODO: Use cases, pros, cons chiqarish
}

func compareWith(lang string) {
    // TODO: Go vs lang taqqoslash jadvali
}

func runBenchmark() {
    // TODO: CPU, memory, concurrency benchmark'lar
    fmt.Println("Running benchmarks...")
    start := time.Now()
    // CPU benchmark
    sum := 0
    for i := 0; i < 100_000_000; i++ {
        sum += i
    }
    fmt.Printf("CPU (100M sum): %v\n", time.Since(start))
    fmt.Printf("Memory: %d MB\n", runtime.MemStats{}.Alloc/1024/1024)
}

func main() {
    if len(os.Args) < 2 {
        fmt.Println("Usage: gocompare [info|vs|benchmark|recommend]")
        return
    }

    switch os.Args[1] {
    case "info":
        showInfo()
    case "vs":
        if len(os.Args) < 3 {
            fmt.Println("Usage: gocompare vs [python|rust|java|nodejs]")
            return
        }
        compareWith(strings.ToLower(os.Args[2]))
    case "benchmark":
        runBenchmark()
    case "recommend":
        // TODO: Interactive recommendation
        fmt.Println("Interactive recommendation coming soon...")
    default:
        fmt.Printf("Unknown command: %s\n", os.Args[1])
    }

    _ = json.Marshal // Suppress unused import
}
```

**Delivery:**
- `main.go` — asosiy kod
- `README.md` — qanday ishlatish
- Natija: `go build -o gocompare && ./gocompare info`

---

### Mini Project 2: "GoNews" — Concurrent RSS Feed Aggregator

**Maqsad:** Bir nechta RSS feed'larni parallel yuklab, birlashtirib, CLI da ko'rsatadigan tool.

**Funksionallik:**
1. Oldindan belgilangan RSS feed'larni concurrent yuklab olish
2. Worker pool (3-5 worker) ishlatish
3. Context timeout (10 sekund)
4. Natijalarni vaqt bo'yicha saralash
5. Top N yangiliklar chiqarish

**Texnologiyalar:**
- `net/http` — HTTP client
- `encoding/xml` — RSS XML parsing
- `sync.WaitGroup` + channels — concurrency
- `context` — timeout
- `sort` — saralash

**RSS Feed misollar:**
```go
feeds := []string{
    "https://news.ycombinator.com/rss",
    "https://www.reddit.com/r/golang/.rss",
    "https://blog.golang.org/feed.atom",
}
```

**Delivery:**
- `main.go` — asosiy kod
- `go run main.go --top=10 --workers=3`

---

## 6. Challenge

### Challenge: "Go Battle" — Real-Time Language Performance Tournament

**Maqsad:** Go dastur yozing, u turli xil algoritmik vazifalarni bajarib, natijalarni real-time ko'rsatadi. Bu dastur Go'ning kuchli tomonlarini namoyish etishi kerak.

**Vazifalar:**

1. **Round 1: CPU Benchmark**
   - Matrix multiplication (100x100)
   - Prime number sieve (1M gacha)
   - Fibonacci (recursive, n=40)

2. **Round 2: Concurrency Benchmark**
   - 10000 goroutine yaratish va tugashini kutish
   - Fan-out/fan-in pattern (100 producer, 10 consumer)
   - Worker pool (10 worker, 1000 task)

3. **Round 3: Memory Benchmark**
   - 1M element slice allocate va sort
   - Map: 100K key-value insert va lookup
   - String concat: 100K iterations (Builder vs +)

4. **Round 4: IO Benchmark**
   - 100MB fayl yozish
   - 100MB fayl o'qish
   - Concurrent fayl operations (10 goroutine)

**Constraints:**
- Barcha benchmark'lar **bitta main.go** faylda
- `testing` package ishlatmang — custom benchmark framework
- Natijalar **JSON** formatda ham, **table** formatda ham chiqsin
- Barcha kodlar **race-free** bo'lsin (`go run -race` bilan tekshiring)
- Total execution time **30 sekunddan** kam bo'lsin

**Scoring:**

| Criteria | Points |
|----------|--------|
| Barcha 4 round implementatsiya qilingan | 40 |
| To'g'ri natijalar (race-free) | 20 |
| JSON va table output | 15 |
| Kod sifati (error handling, clean code) | 15 |
| 30 sekund ichida tugashi | 10 |
| **Bonus:** Comparison table (Go estimated vs Python/Java estimated) | +10 |
| **Total** | 100 (+10 bonus) |

**Starter Structure:**

```go
package main

import (
    "encoding/json"
    "fmt"
    "math/rand"
    "os"
    "runtime"
    "sort"
    "strings"
    "sync"
    "time"
)

type RoundResult struct {
    Round    string        `json:"round"`
    Tasks    []TaskResult  `json:"tasks"`
    Duration time.Duration `json:"total_duration_ns"`
}

type TaskResult struct {
    Name     string        `json:"name"`
    Duration time.Duration `json:"duration_ns"`
    Result   string        `json:"result"`
}

type Tournament struct {
    Rounds    []RoundResult `json:"rounds"`
    Total     time.Duration `json:"total_duration_ns"`
    GoVersion string        `json:"go_version"`
    OS        string        `json:"os"`
    Arch      string        `json:"arch"`
    CPUs      int           `json:"cpus"`
}

func runCPUBenchmark() RoundResult {
    // TODO: Matrix multiplication, prime sieve, fibonacci
    return RoundResult{Round: "CPU Benchmark"}
}

func runConcurrencyBenchmark() RoundResult {
    // TODO: Goroutine creation, fan-out/fan-in, worker pool
    return RoundResult{Round: "Concurrency Benchmark"}
}

func runMemoryBenchmark() RoundResult {
    // TODO: Slice sort, map operations, string concat
    return RoundResult{Round: "Memory Benchmark"}
}

func runIOBenchmark() RoundResult {
    // TODO: File write, file read, concurrent IO
    return RoundResult{Round: "IO Benchmark"}
}

func printTable(tournament Tournament) {
    // TODO: Natijalarni table formatda chiqarish
    fmt.Println("=== Go Battle — Performance Tournament ===")
    fmt.Printf("Go: %s | OS: %s/%s | CPUs: %d\n\n",
        tournament.GoVersion, tournament.OS, tournament.Arch, tournament.CPUs)

    for _, round := range tournament.Rounds {
        fmt.Printf("--- %s (Total: %v) ---\n", round.Round, round.Duration)
        for _, task := range round.Tasks {
            fmt.Printf("  %-35s %12v  %s\n", task.Name, task.Duration, task.Result)
        }
        fmt.Println()
    }

    fmt.Printf("=== TOTAL: %v ===\n", tournament.Total)
}

func main() {
    fmt.Println("Starting Go Battle...")
    totalStart := time.Now()

    tournament := Tournament{
        GoVersion: runtime.Version(),
        OS:        runtime.GOOS,
        Arch:      runtime.GOARCH,
        CPUs:      runtime.NumCPU(),
        Rounds: []RoundResult{
            runCPUBenchmark(),
            runConcurrencyBenchmark(),
            runMemoryBenchmark(),
            runIOBenchmark(),
        },
    }
    tournament.Total = time.Since(totalStart)

    // Table output
    printTable(tournament)

    // JSON output
    data, _ := json.MarshalIndent(tournament, "", "  ")
    os.WriteFile("battle_results.json", data, 0644)
    fmt.Println("\nResults saved to battle_results.json")

    // Suppress unused imports
    _ = rand.Int
    _ = sort.Ints
    _ = strings.Builder{}
    _ = sync.WaitGroup{}
}
```

**Ishga tushirish:**
```bash
go run -race main.go
```

**Good luck!**
