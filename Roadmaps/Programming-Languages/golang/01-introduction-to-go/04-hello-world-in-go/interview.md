# Hello World in Go — Interview Questions

## Table of Contents

1. [Junior Level](#1-junior-level)
2. [Middle Level](#2-middle-level)
3. [Senior Level](#3-senior-level)
4. [Scenario-Based Questions](#4-scenario-based-questions)
5. [FAQ](#5-faq)

---

## 1. Junior Level

### 1.1 Go dasturida `package main` va `func main()` ning roli nima?

<details>
<summary>Javob</summary>

**`package main`** — Go kompilyatoriga bu fayl bajariladigan dastur (executable) ekanligini bildiradi. `package main` bo'lmasa, Go bu kodni kutubxona (library) deb hisoblaydi.

**`func main()`** — dastur ishga tushganda birinchi chaqiriladigan funksiya (entry point). U hech qanday argument qabul qilmaydi va hech qanday qiymat qaytarmaydi.

```go
package main

import "fmt"

func main() {
    fmt.Println("Salom!")
}
```

Ikkalasi ham **majburiy** — bittasi bo'lmasa dastur kompilyatsiya bo'lmaydi.

**Qo'shimcha ball uchun:**
- `main()` tugashi = dastur tugashi (barcha goroutine'lar to'xtaydi)
- Bitta dasturda faqat bitta `main()` bo'lishi mumkin

</details>

### 1.2 `fmt.Println`, `fmt.Printf` va `fmt.Print` ning farqlarini tushuntiring.

<details>
<summary>Javob</summary>

| Funksiya | Yangi qator | Format string | Argumentlar orasida bo'sh joy |
|----------|------------|---------------|-------------------------------|
| `Println` | Ha (avtomatik) | Yo'q | Ha (har doim) |
| `Printf` | Yo'q (`\n` kerak) | Ha (`%s`, `%d`...) | Format string boshqaradi |
| `Print` | Yo'q | Yo'q | Faqat ikkala argument string bo'lmaganda |

```go
fmt.Println("A", "B")              // A B\n
fmt.Printf("%s-%s\n", "A", "B")   // A-B\n
fmt.Print("A", "B")               // AB (ikkala string — bo'sh joy yo'q)
fmt.Print(1, 2)                    // 1 2 (int'lar — bo'sh joy bor)
```

**Qo'shimcha ball uchun:**
- `Sprintf` — string qaytaradi (ekranga chiqarmaydi)
- `Fprintf` — `io.Writer` ga yozadi (faylga, network ga)

</details>

### 1.3 `go run` va `go build` ning farqi nima?

<details>
<summary>Javob</summary>

| Xususiyat | `go run` | `go build` |
|-----------|----------|------------|
| Binary fayl | Vaqtinchalik (`/tmp` da, keyin o'chiriladi) | Doimiy (joriy papkada) |
| Ishlatish | Development/test | Production/deploy |
| Tezlik | Har safar qayta kompilyatsiya | Bir marta build, ko'p marta run |

```bash
go run main.go         # kompilyatsiya + run, binary saqlanmaydi
go build -o app main.go  # binary yaratiladi
./app                  # binary ishga tushiriladi
```

**Qo'shimcha ball uchun:**
- `go run .` — joriy papkadagi barcha `.go` fayllarni kompilyatsiya qiladi
- `go build` orqali yaratilgan binary hech qanday Go o'rnatmasdan ishlaydi (static linking)

</details>

### 1.4 Go'da izohlar (comments) qanday yoziladi?

<details>
<summary>Javob</summary>

```go
// Bir qatorli izoh (line comment)

/*
   Ko'p qatorli izoh
   (block comment)
*/
```

**Muhim:**
- `//` — eng ko'p ishlatiladigan
- `/* */` — paket dokumentatsiyasi uchun ham ishlatiladi
- Go'da ichma-ich (nested) block comment yo'q: `/* /* */ */` — xato

</details>

### 1.5 Nima uchun Go'da `{` yangi qatorga qo'yib bo'lmaydi?

<details>
<summary>Javob</summary>

Go lexer'i qator oxiriga avtomatik nuqtali vergul (`;`) qo'yadi:

```go
// Bu Go nima ko'radi:
func main();   // ← lexer ";" qo'ydi
{              // ← yangi statement — XATO!
}

// TO'G'RI:
func main() {  // qavs shu qatorda
}
```

Bu Go dizaynerlarining qasddan qilgan qarori — barcha Go kodni yagona uslubda yozishni ta'minlash uchun. `gofmt` avtomatik to'g'irlaydi.

</details>

### 1.6 Ishlatilmagan import nima uchun xato beradi?

<details>
<summary>Javob</summary>

Go dizaynerlarining qasddan qabul qilgan qarori:

1. **Toza kod** — keraksiz dependency'lar to'planib qolmaydi
2. **Tez kompilyatsiya** — faqat kerakli paketlar kompilyatsiya qilinadi
3. **Aniq dependency** — dastur nimaga bog'liq ekanligi aniq

```go
import (
    "fmt"
    "os"   // Xato: imported and not used
)
```

**Yechim:** `goimports` tool avtomatik qo'shadi va olib tashlaydi. Yoki `_ "os"` (blank import).

</details>

### 1.7 Go Playground nima va uning cheklovlari?

<details>
<summary>Javob</summary>

[Go Playground](https://go.dev/play/) — brauzerda Go kodni yozish va ishga tushirish. Go o'rnatish shart emas.

**Cheklovlar:**
- Tashqi paketlar ishlamaydi (faqat standart kutubxona)
- `time.Now()` har doim 2009-11-10 qaytaradi
- Network chaqiruvlar cheklangan
- Fayl tizimi vaqtinchalik
- Dastur bajarilish vaqti cheklangan

</details>

---

## 2. Middle Level

### 2.1 `init()` funksiyasi haqida tushuntiring. Qachon ishlatiladi va qachon ishlatilmasligi kerak?

<details>
<summary>Javob</summary>

`init()` — maxsus funksiya, `main()` dan oldin avtomatik chaqiriladi.

**Xususiyatlari:**
- Argument yo'q, return qiymat yo'q
- Bir faylda bir nechta `init()` bo'lishi mumkin (yozilish tartibida)
- Bir nechta fayldagi `init()` lar fayl nomi alifbo tartibida
- Foydalanuvchi tomonidan chaqirib bo'lmaydi: `init()` — kompilyatsiya xatosi

**Qachon ishlatiladi:**
- Package-level validatsiya (env var tekshirish)
- Driver/codec/handler registration (`database/sql`, `image/*`)
- One-time setup

**Qachon ishlatilMASligi kerak:**
- Database connection
- Network chaqiruvlar
- Og'ir initialization (test qilishni qiyinlashtiradi)
- Global state yaratish (DI ga to'sqinlik qiladi)

```go
// YAXSHI
func init() {
    if os.Getenv("API_KEY") == "" {
        log.Fatal("API_KEY o'rnatilmagan")
    }
}

// YOMON
func init() {
    db, _ = sql.Open("postgres", os.Getenv("DB_URL"))
    db.Ping() // network call init() da — test qilib bo'lmaydi
}
```

**Tavsiya:** Explicit init funksiyalarni chaqirish (`newApp()`, `setup()`) — testable va aniq.

</details>

### 2.2 `os.Exit()` va `return` (main dan) farqi nima? `run()` pattern nimaga kerak?

<details>
<summary>Javob</summary>

| | `return` | `os.Exit(code)` |
|---|---|---|
| Exit code | 0 | Ixtiyoriy |
| `defer` | Ishlaydi | **Ishlamaydi** |
| Goroutine cleanup | Ha | Yo'q |

**Muammo:**
```go
func main() {
    f, _ := os.Create("data.txt")
    defer f.Close() // ISHLAMAYDI!
    // ...
    os.Exit(1) // defer skip qilinadi
}
```

**run() pattern:**
```go
func main() {
    os.Exit(run())
}

func run() int {
    f, _ := os.Create("data.txt")
    defer f.Close() // ISHLAYDI!
    // ...
    return 0
}
```

Bu pattern production Go dasturlarida standart (Kubernetes, Docker, Terraform).

</details>

### 2.3 `log` vs `fmt` — qachon nimani ishlating?

<details>
<summary>Javob</summary>

| | `fmt` | `log` | `slog` (Go 1.21+) |
|---|---|---|---|
| Output | stdout | stderr | Configurable |
| Timestamp | Yo'q | Ha | Ha |
| Structured | Yo'q | Yo'q | Ha (JSON/Text) |
| Fatal | Yo'q | Ha (`log.Fatal`) | Yo'q |
| Ishlatish | User output, debug | Server logging | Production |

```go
// Foydalanuvchiga chiqish
fmt.Println("Server :8080 da ishga tushdi")

// Server log
log.Println("Request received from 192.168.1.1")

// Production structured log
slog.Info("request", "method", "GET", "path", "/api", "status", 200)
```

**Qoida:** `fmt` = user-facing output, `log/slog` = developer/ops logging.

</details>

### 2.4 Cross-compilation qanday ishlaydi? CGO bilan muammolar nima?

<details>
<summary>Javob</summary>

```bash
# Oddiy cross-compilation
GOOS=linux GOARCH=amd64 go build -o app-linux .

# CGO bilan muammo
CGO_ENABLED=1 GOOS=linux go build .
# Xato: cross-compilation uchun C compiler kerak
```

**CGO muammolari:**
1. Target platformaning C compiler kerak (`x86_64-linux-musl-gcc`)
2. Dynamic linking (binary `libc.so` ga bog'liq)
3. `scratch` Docker image'da ishlamaydi

**Yechim:**
```bash
CGO_ENABLED=0 GOOS=linux go build -o app .  # Pure Go, static binary
```

</details>

### 2.5 `fmt.Stringer` interface nima?

<details>
<summary>Javob</summary>

```go
type Stringer interface {
    String() string
}
```

`%v` va `%s` bilan chiqarilganda custom format beradi:

```go
type User struct {
    Name string
    Age  int
}

func (u User) String() string {
    return fmt.Sprintf("%s (%d)", u.Name, u.Age)
}

u := User{"Anvar", 25}
fmt.Println(u) // Anvar (25)
```

**Security uchun muhim:** Password va token'larni maskalash:
```go
func (c Credentials) String() string {
    return fmt.Sprintf("{user:%s, pass:****}", c.Username)
}
```

</details>

### 2.6 Build constraints (build tags) qanday ishlaydi?

<details>
<summary>Javob</summary>

Ikki usul:

**1. `//go:build` directive (Go 1.17+):**
```go
//go:build linux && amd64

package mypackage
```

**2. Fayl nomlash konvensiyasi:**
- `file_linux.go` — faqat Linux
- `file_windows.go` — faqat Windows
- `file_darwin_arm64.go` — macOS ARM

**Operatorlar:**
- `&&` — VA
- `||` — YOKI
- `!` — NOT

```go
//go:build (linux || darwin) && !race
```

Ishlatish holatlari: platform-specific kod, feature flags, integration test'lar.

</details>

---

## 3. Senior Level

### 3.1 Go'ning initsializatsiya tartibini spec bo'yicha tushuntiring.

<details>
<summary>Javob</summary>

Go spec bo'yicha:

```
1. Imported packagelar dependency graph bo'yicha init qilinadi
   (har bir package faqat BIR MARTA)
2. Har bir package ichida:
   a. Package-level o'zgaruvchilar deklaratsiya tartibida
      (dependency bo'lsa — dependency birinchi)
   b. init() funksiyalari source fayl tartibida
      (bitta faylda — yozilish tartibida)
3. main package init()
4. main.main()
```

**Muhim nuanslar:**
- Fayl nomlari orasidagi tartib Go spec'da **aniq kafolatlanmagan** (kompilyator implementatsiyasiga bog'liq, lekin amalda alifbo tartibida)
- Circular import **taqiqlangan**
- `init()` ni chaqirib bo'lmaydi — `init()` reserved identifier emas, lekin maxsus holat
- Package faqat bir marta init qilinadi (qancha joyda import qilinishidan qat'i nazar)

</details>

### 3.2 `-ldflags` bilan version embedding qanday ishlaydi? Qanday cheklovlar bor?

<details>
<summary>Javob</summary>

```bash
go build -ldflags "-X main.version=1.2.3 -X main.commit=$(git rev-parse --short HEAD)" .
```

**Cheklovlar:**
- Faqat `string` turdagi package-level `var` (const emas, int emas)
- Bo'sh joy bo'lsa qoshtirnoq kerak: `-X 'main.date=Jan 15 2024'`
- Linker bosqichida — binary allaqachon kompilyatsiya qilingan

**CI/CD muammolari:**
- Shallow clone — `git describe` ishlamaydi
- Dirty working tree detection
- Tag'lar fetch qilinmagan

**Alternativa:** Go 1.18+ `//go:embed version.txt` — fayldan o'qish.

</details>

### 3.3 Graceful shutdown ni production uchun qanday implement qilasiz?

<details>
<summary>Javob</summary>

```go
func main() {
    os.Exit(run())
}

func run() int {
    ctx, stop := signal.NotifyContext(context.Background(),
        syscall.SIGINT, syscall.SIGTERM)
    defer stop()

    srv := &http.Server{Addr: ":8080", Handler: mux}

    go func() {
        if err := srv.ListenAndServe(); err != http.ErrServerClosed {
            log.Fatal(err)
        }
    }()

    <-ctx.Done()

    shutdownCtx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
    defer cancel()

    if err := srv.Shutdown(shutdownCtx); err != nil {
        return 1
    }
    return 0
}
```

**Muhim aspektlar:**
1. **SIGINT + SIGTERM** tutiladi (SIGKILL tutib bo'lmaydi)
2. **Shutdown timeout** — cheksiz kutmaslik
3. **In-flight request'lar** — `Shutdown()` ularga tugashga imkon beradi
4. **Resource cleanup** — DB connection, message queue
5. **Health check** — shutdown boshlanganida `/health` 503 qaytarishi kerak
6. **WaitGroup** — barcha goroutine'lar tugashini kutish
7. **`run()` pattern** — defer'lar ishlashini ta'minlash

</details>

### 3.4 GoReleaser va multi-platform build strategiyasi haqida gapiring.

<details>
<summary>Javob</summary>

**GoReleaser** — multi-platform binary + release pipeline:

```yaml
builds:
  - env: [CGO_ENABLED=0]
    goos: [linux, darwin, windows]
    goarch: [amd64, arm64]
    ldflags: [-s -w -X main.version={{.Version}}]
```

**Strategiya:**
1. **CI trigger** — git tag push (`v1.2.3`)
2. **GoReleaser** — 6 binary (3 OS x 2 arch)
3. **Archive** — tar.gz (Linux/macOS), zip (Windows)
4. **Checksum** — SHA256 checksums.txt
5. **GitHub Release** — avtomatik upload
6. **Docker images** — multi-arch manifest
7. **Homebrew formula** — macOS uchun

**Alternativalar:** Makefile + shell script, GitHub Actions matrix, `ko` (container-focused).

</details>

### 3.5 `init()` ni ishlatmasdan dasturni qanday tashkil qilasiz?

<details>
<summary>Javob</summary>

**Explicit initialization pattern:**

```go
// cmd/server/main.go
func main() {
    os.Exit(run())
}

func run() int {
    cfg, err := config.Load("config.yaml")
    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        return 1
    }

    db, err := database.Connect(cfg.DatabaseURL)
    if err != nil {
        fmt.Fprintln(os.Stderr, err)
        return 1
    }
    defer db.Close()

    app := app.New(cfg, db)
    return app.Run()
}
```

**Afzalliklari init() ga nisbatan:**
1. **Testable** — constructor'ga mock kiritish mumkin
2. **Error handling** — `error` qaytarish mumkin (init() da faqat `log.Fatal` yoki `panic`)
3. **Explicit dependency** — kim nimaga bog'liq aniq ko'rinadi
4. **Ordering control** — tartib to'liq qo'lingizda
5. **DI-friendly** — `wire`, `fx` bilan ishlaydi

</details>

### 3.6 Hello World binary hajmi nima uchun ~1.8MB va qanday kamaytirish mumkin?

<details>
<summary>Javob</summary>

**Hajm taqsimoti:**
- Runtime (GC, scheduler, memory): ~600KB (33%)
- fmt + reflect: ~350KB (19%)
- .gopclntab (stack traces): ~300KB (17%)
- Boshqa internal paketlar: ~550KB (31%)

**Kamaytirish usullari:**
1. `-ldflags "-s -w"` — debug info strip (~30% kamaytirish)
2. `-trimpath` — build path strip
3. UPX compression — qo'shimcha ~60%
4. `fmt` o'rniga `os.Stdout.WriteString` — ~500KB tejash
5. `tinygo` — embedded uchun (~15KB binary)

```bash
# Natija:
go build -o app .                          # 1.8MB
go build -ldflags="-s -w" -o app .         # 1.3MB
upx --best app                             # ~500KB
```

</details>

---

## 4. Scenario-Based Questions

### 4.1 Siz production serverda deploy qilmoqchisiz. main.go qanday tuziladi?

<details>
<summary>Javob</summary>

```go
// cmd/server/main.go
package main

import (
    "context"
    "fmt"
    "log/slog"
    "os"
    "os/signal"
    "runtime"
    "syscall"

    "myapp/internal/server"
)

var (
    version   = "dev"
    commit    = "unknown"
    buildDate = "unknown"
)

func main() {
    os.Exit(run())
}

func run() int {
    logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))

    logger.Info("starting",
        "version", version,
        "commit", commit,
        "go", runtime.Version(),
    )

    ctx, stop := signal.NotifyContext(context.Background(),
        syscall.SIGINT, syscall.SIGTERM)
    defer stop()

    srv, err := server.New(logger)
    if err != nil {
        logger.Error("init failed", "error", err)
        return 1
    }

    if err := srv.Run(ctx); err != nil {
        logger.Error("server error", "error", err)
        return 1
    }

    return 0
}
```

**Asosiy tamoyillar:**
- Thin main, `run()` pattern
- Structured logging (`slog`)
- Graceful shutdown (signal context)
- Version embedding (ldflags)
- Error reporting (exit codes)
- Defer safety

</details>

### 4.2 Jamoangiz `init()` ni ko'p ishlatmoqda va testlar buzilmoqda. Nima qilasiz?

<details>
<summary>Javob</summary>

**Muammo diagnosti:**
1. `init()` test paytida ham ishlaydi — production resource'larga ulanishi mumkin
2. Global state — parallel test'lar conflict qiladi
3. Tartib implicit — kutilmagan behavior

**Yechim bosqichlari:**

1. **Audit** — barcha `init()` larni toping va kategoriyalang
2. **Ajratish:**
   - Driver registration (`_ "image/png"`) — saqlab qolish mumkin
   - Config validation — explicit funktsiyaga ko'chirish
   - Resource init (DB, cache) — constructor ga ko'chirish
3. **DI kiritish:**
```go
// Oldin:
var db *sql.DB
func init() { db = connectDB() }

// Keyin:
type App struct { db *sql.DB }
func NewApp(db *sql.DB) *App { return &App{db: db} }
```
4. **TestMain** ishlatish (agar init() zarur bo'lsa):
```go
func TestMain(m *testing.M) {
    // test setup
    os.Exit(m.Run())
}
```

</details>

### 4.3 Sizdan CLI tool yozish so'ralmoqda. 5 ta platform uchun binary chiqarish kerak. Qanday yo'l tutasiz?

<details>
<summary>Javob</summary>

**1. Loyiha tuzilishi:**
```
myctl/
├── cmd/myctl/main.go
├── internal/
├── go.mod
├── Makefile
└── .goreleaser.yaml
```

**2. Main pattern:**
```go
var version = "dev"

func main() {
    app := &cli.App{
        Name:    "myctl",
        Version: version,
        Commands: []*cli.Command{...},
    }
    if err := app.Run(os.Args); err != nil {
        os.Exit(1)
    }
}
```

**3. Build strategy:**
- Local: `Makefile` bilan 5 platforma
- CI: GoReleaser + GitHub Actions
- Platforms: `linux/amd64`, `linux/arm64`, `darwin/amd64`, `darwin/arm64`, `windows/amd64`

**4. Release pipeline:**
```yaml
# .github/workflows/release.yml
on:
  push:
    tags: ['v*']
jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - uses: goreleaser/goreleaser-action@v5
        with:
          args: release --clean
```

**5. Qo'shimcha:** checksum, SBOM, Homebrew formula, Docker image.

</details>

### 4.4 `fmt.Println` production log sifatida ishlatilmoqda. Nima muammo va qanday fix qilasiz?

<details>
<summary>Javob</summary>

**Muammolar:**
1. **Vaqt belgisi yo'q** — qachon sodir bo'lganini bilish mumkin emas
2. **Structured data yo'q** — log aggregation (ELK, Datadog) da parse qilish qiyin
3. **Level yo'q** — INFO/WARN/ERROR ajratib bo'lmaydi
4. **stdout ga** — stderr ga emas
5. **Thread-safe, lekin format yo'q** — concurrent log'lar aralashadi
6. **Sensitive data leak** — `%v` struct ichidagi password ni ko'rsatadi

**Fix:**
```go
// Oldin:
fmt.Println("User logged in:", username)

// Keyin:
logger.Info("user logged in",
    "username", username,
    "ip", r.RemoteAddr,
    "method", r.Method,
)
```

**Migration plan:**
1. `slog` yoki `zerolog` tanlash
2. `fmt.Println` ni `grep` bilan topish
3. Har bir chaqiruvni structured log ga o'zgartirish
4. Sensitive data uchun `Stringer`/`LogValuer` qo'shish
5. Log level konfiguratsiya qilish (env var orqali)

</details>

### 4.5 Dastur `os.Exit(1)` bilan chiqmoqda, lekin database connection yopilmayapti. Sabab va yechim?

<details>
<summary>Javob</summary>

**Sabab:** `os.Exit()` **defer'larni o'tkazib yuboradi**.

```go
// MUAMMO:
func main() {
    db := connectDB()
    defer db.Close() // BU ISHLAMAYDI!

    if err := doWork(); err != nil {
        fmt.Fprintln(os.Stderr, err)
        os.Exit(1) // defer skip!
    }
}
```

**Yechim — `run()` pattern:**
```go
func main() {
    os.Exit(run())
}

func run() int {
    db := connectDB()
    defer db.Close() // BU ISHLAYDI!

    if err := doWork(); err != nil {
        fmt.Fprintln(os.Stderr, err)
        return 1 // defer ishlaydi
    }
    return 0
}
```

**Alternativ yechim (kamroq ideal):**
```go
func main() {
    db := connectDB()
    // os.Exit o'rniga explicit cleanup
    if err := doWork(); err != nil {
        db.Close() // qo'lda yopish
        os.Exit(1)
    }
    db.Close()
}
```

</details>

---

## 5. FAQ

### 5.1 Go dasturida `func main()` argument qabul qiladimi?

**Yo'q.** Go'da `main()` hech qanday argument qabul qilmaydi (C/Java dan farqli). CLI argumentlar `os.Args` orqali olinadi:

```go
func main() {
    args := os.Args[1:] // os.Args[0] = dastur nomi
    fmt.Println("Argumentlar:", args)
}
```

Murakkab CLI uchun `flag` paketi yoki tashqi kutubxonalar (`cobra`, `urfave/cli`) ishlatiladi.

### 5.2 Go dasturi nechta fayldan iborat bo'lishi mumkin?

Bir `package main` ichida **cheksiz** fayl bo'lishi mumkin. Barcha fayllar bitta package'ga tegishli bo'lishi kerak. `go run .` barcha fayllarni kompilyatsiya qiladi.

```go
// main.go
package main
func main() { greet() }

// greet.go
package main
import "fmt"
func greet() { fmt.Println("Salom!") }
```

### 5.3 `fmt.Println` va `log.Println` bir xil ishlaydi-mi?

**Yo'q:**
- `fmt.Println` → stdout, vaqt belgisiz
- `log.Println` → stderr, vaqt belgisi bilan
- `log.Fatal` → log + `os.Exit(1)`
- `log.Panic` → log + `panic()`

### 5.4 Go Playground da nima uchun `time.Now()` har doim bir xil?

Go Playground **deterministik** natija berish uchun vaqtni muzlatadi: `2009-11-10 23:00:00 +0000 UTC`. Bu Go'ning birinchi ochiq kodli release sanasi. Bu kodni qayta ishga tushirganda bir xil natija olish uchun qilingan.

### 5.5 Eng minimal Go dasturi qanday?

```go
package main

func main() {}
```

Bu to'liq to'g'ri Go dasturi — hech narsa qilmaydi va exit code 0 bilan tugaydi. `import` ham shart emas.
