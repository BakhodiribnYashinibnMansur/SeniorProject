# Hello World in Go — Practical Tasks

## Table of Contents

1. [Junior Tasks](#1-junior-tasks)
2. [Middle Tasks](#2-middle-tasks)
3. [Senior Tasks](#3-senior-tasks)
4. [Questions](#4-questions)
5. [Mini Projects](#5-mini-projects)
6. [Challenge](#6-challenge)

---

## 1. Junior Tasks

### Task 1: Vizitkarta Chiqaruvchi

**Maqsad:** `fmt.Println`, `fmt.Printf`, `fmt.Print` va format verblari bilan ishlash.

**Talablar:**

1. `main.go` fayl yarating
2. Quyidagi vizitkartani chiqaring:

```
╔══════════════════════════════╗
║  Ism:      Anvar Karimov     ║
║  Yosh:     25                ║
║  Kasb:     Go Developer      ║
║  Tajriba:  2.5 yil           ║
║  Faol:     true              ║
║  Email:    anvar@example.com  ║
╚══════════════════════════════╝

Turlar: string, int, string, float64, bool, string
```

3. Har bir ma'lumot uchun **o'zgaruvchi** e'lon qiling
4. `fmt.Println`, `fmt.Printf`, `fmt.Print` ning **har birini** kamida bir marta ishlating
5. Format verblari: `%s`, `%d`, `%.1f`, `%t`, `%v`, `%T` ning har birini ishlating
6. Turlar qatorini `%T` verbi bilan avtomatik chiqaring

**Boshlang'ich shablon:**

```go
package main

import "fmt"

func main() {
    ism := "Anvar Karimov"
    yosh := 25
    kasb := "Go Developer"
    tajriba := 2.5
    faol := true
    email := "anvar@example.com"

    // Sizning kodingiz shu yerda
    // Yuqoridagi natijani chiqaring
}
```

**Tekshirish:** `go run main.go` bilan ishga tushiring va natijani solishtiring.

---

### Task 2: Formatting Playground

**Maqsad:** Barcha asosiy format verblarini amalda sinash.

**Talablar:**

1. Quyidagi jadvalni `fmt.Printf` bilan chiqaring:

```
╔═══════════╦════════════════╦════════════════════╗
║   Verb    ║    Qiymat      ║    Natija          ║
╠═══════════╬════════════════╬════════════════════╣
║   %d      ║    42          ║    42              ║
║   %b      ║    42          ║    101010          ║
║   %o      ║    42          ║    52              ║
║   %x      ║    42          ║    2a              ║
║   %X      ║    42          ║    2A              ║
║   %e      ║    3.14        ║    3.140000e+00    ║
║   %f      ║    3.14        ║    3.140000        ║
║   %.2f    ║    3.14        ║    3.14            ║
║   %s      ║    "Go"        ║    Go              ║
║   %q      ║    "Go"        ║    "Go"            ║
║   %t      ║    true        ║    true            ║
║   %c      ║    65          ║    A               ║
║   %p      ║    &x          ║    0xc0000...      ║
║   %%      ║    -           ║    %               ║
╚═══════════╩════════════════╩════════════════════╝
```

2. Har bir qator uchun **alohida** `fmt.Printf` chaqiruvi ishlatiladi
3. Jadval chiziqlari ham chiqarilishi kerak

**Maslahat:** `fmt.Printf("║   %-7s ║    %-12v ║    %-16v ║\n", verb, qiymat, natija)` — width va alignment ishlatish mumkin.

---

### Task 3: Go run vs Go build Taqqoslash

**Maqsad:** `go run` va `go build` farqini amalda ko'rish.

**Talablar:**

1. `hello.go` fayl yarating (Hello World)
2. Quyidagi amallarni bajaring va natijalarni yozing:

```bash
# 1. go run bilan ishga tushiring
time go run hello.go

# 2. go build bilan binary yarating
time go build -o hello hello.go

# 3. Binary hajmini tekshiring
ls -lh hello

# 4. Binary ni ishga tushiring
time ./hello

# 5. Binary haqida ma'lumot
file hello

# 6. go build -ldflags "-s -w" bilan qayta build
go build -ldflags "-s -w" -o hello-small hello.go
ls -lh hello-small
```

3. Natijalarni jadval shaklida yozing:

| Buyruq | Vaqt | Binary hajmi |
|--------|------|-------------|
| `go run` | ? | vaqtinchalik |
| `go build` | ? | ? |
| `go build -ldflags "-s -w"` | ? | ? |
| binary ishga tushirish | ? | - |

---

### Task 4: Multi-File Dastur

**Maqsad:** Bir nechta fayldan iborat Go dastur yaratish.

**Talablar:**

1. 3 ta fayl yarating (barchasi `package main`):

```
project/
├── main.go       // func main() — boshqa funksiyalarni chaqiradi
├── greet.go      // func greet(name string) — salomlashish
└── math.go       // func add(a, b int) int — qo'shish
```

2. `main.go` dan `greet()` va `add()` funksiyalarini chaqiring
3. `go run .` bilan ishga tushiring

**Kutilgan natija:**
```
Salom, Anvar!
3 + 5 = 8
```

---

## 2. Middle Tasks

### Task 1: CLI Tool — Kalit So'z Qidiruvchi

**Maqsad:** `os.Args`, `fmt`, `os`, `strings` bilan amaliy CLI dastur yozish.

**Talablar:**

1. `search.go` dastur yarating
2. Dastur 2 ta argument qabul qiladi: `qidiruv_so'zi` va `matn`
3. Matn ichida qidiruv so'zini topib, natija chiqaradi

**Foydalanish:**
```bash
go run search.go hello "hello world, hello Go"
```

**Kutilgan natija:**
```
Qidiruv: "hello"
Matnda: "hello world, hello Go"
Topildi: 2 marta
Pozitsiyalar: [0, 13]
Katta-kichik harfga sezgir: Ha
```

**Qo'shimcha talablar:**
- Agar argument yetarli bo'lmasa — xato xabar + `os.Exit(1)`
- Xato xabarlari `os.Stderr` ga yozilsin
- `-i` flag bilan case-insensitive qidiruv (os.Args ni tekshirish)
- Topilmasa — mos xabar

**Boshlang'ich shablon:**

```go
package main

import (
    "fmt"
    "os"
    "strings"
)

func main() {
    if len(os.Args) < 3 {
        fmt.Fprintln(os.Stderr, "Foydalanish: search <so'z> <matn> [-i]")
        os.Exit(1)
    }

    query := os.Args[1]
    text := os.Args[2]
    caseInsensitive := false

    // -i flagni tekshirish
    for _, arg := range os.Args[3:] {
        if arg == "-i" {
            caseInsensitive = true
        }
    }

    // Sizning kodingiz shu yerda
}
```

---

### Task 2: init() Tartibi Tadqiqoti

**Maqsad:** `init()` funksiyasining bajarilish tartibini amalda tekshirish.

**Talablar:**

1. 3 ta fayl yarating (barchasi `package main`):

```
initorder/
├── a_first.go
├── b_second.go
└── main.go
```

2. Har bir faylda:
   - Package-level o'zgaruvchi (init funksiyasi bilan)
   - 2 ta `init()` funksiya
   - Har biri chiqish beradi

3. Kutilgan tartibni **oldindan taxmin qiling**, keyin ishga tushirib tekshiring

**a_first.go:**
```go
package main

import "fmt"

var varA = func() int {
    fmt.Println("a_first.go: var A init")
    return 1
}()

func init() {
    fmt.Println("a_first.go: init() #1")
}

func init() {
    fmt.Println("a_first.go: init() #2")
}
```

4. Quyidagi savolga javob bering:
   - Package-level var va init() qaysi tartibda?
   - Fayl nomlari tartibga ta'sir qiladimi?
   - Agar `b_second.go` ni `a_aa.go` ga qayta nomlasangiz nima bo'ladi?

---

### Task 3: Custom Stringer va Log Formatter

**Maqsad:** `fmt.Stringer`, `fmt.GoStringer` va `slog.LogValuer` implementatsiya qilish.

**Talablar:**

1. `User` struct yarating:
```go
type User struct {
    ID       int
    Name     string
    Email    string
    Password string
    Role     string
}
```

2. Quyidagi interface'larni implement qiling:
   - `fmt.Stringer` — `String()` — password maskalangan
   - `fmt.GoStringer` — `GoString()` — password maskalangan
   - `slog.LogValuer` — `LogValue()` — structured, password yo'q

3. Test:
```go
u := User{1, "Anvar", "anvar@mail.com", "secret123", "admin"}

fmt.Println(u)           // User{Anvar, anvar@mail.com, role:admin}
fmt.Printf("%v\n", u)   // User{Anvar, anvar@mail.com, role:admin}
fmt.Printf("%#v\n", u)  // User{ID:1, Name:"Anvar", Email:"anvar@mail.com", Password:"****", Role:"admin"}
fmt.Printf("%+v\n", u)  // User{Anvar, anvar@mail.com, role:admin}

// slog bilan
slog.Info("user created", "user", u)
// {"level":"INFO","msg":"user created","user":{"id":1,"name":"Anvar","email":"anvar@mail.com","role":"admin"}}
```

4. **Password hech qanday log/print da ko'rinmasligi kerak!**

---

## 3. Senior Tasks

### Task 1: Production-Ready CLI Scaffold

**Maqsad:** Production-grade main package pattern yaratish.

**Talablar:**

1. Quyidagi tuzilishda loyiha yarating:

```
myctl/
├── cmd/
│   └── myctl/
│       └── main.go
├── internal/
│   ├── app/
│   │   └── app.go
│   └── config/
│       └── config.go
├── go.mod
├── Makefile
└── .goreleaser.yaml
```

2. **main.go** talablari:
   - Thin main (`run()` pattern)
   - Version embedding (`-ldflags`)
   - Signal handling (SIGINT, SIGTERM)
   - Structured logging (`slog`)
   - Exit codes (0 = success, 1 = error, 2 = usage)
   - `version` subcommand

3. **Makefile** talablari:
   - `make build` — joriy platforma
   - `make build-all` — 5 platforma (linux/darwin/windows, amd64/arm64)
   - `make test`
   - `make lint`
   - `make clean`
   - Version, commit, build date avtomatik

4. **.goreleaser.yaml** — asosiy konfiguratsiya

5. **Test:**
```bash
make build
./bin/myctl version
# myctl v0.1.0 (abc1234) built 2024-01-15T14:30:45Z go1.22.0 linux/amd64

./bin/myctl
# Usage: myctl <command>
# Commands: version, serve, help

# Ctrl+C → Graceful shutdown
./bin/myctl serve
# 2024-01-15T14:30:45Z INF starting server port=8080 version=0.1.0
# ^C
# 2024-01-15T14:30:50Z INF shutdown signal received
# 2024-01-15T14:30:50Z INF server stopped gracefully
```

---

### Task 2: Build Tags bilan Feature Toggle System

**Maqsad:** Build tags va conditional compilation bilan feature toggle yaratish.

**Talablar:**

1. Loyiha tuzilishi:

```
featureapp/
├── main.go
├── features.go          // feature registry
├── feature_cache.go     // //go:build feature_cache
├── feature_metrics.go   // //go:build feature_metrics
├── feature_auth.go      // //go:build feature_auth
└── feature_default.go   // //go:build !feature_cache && !feature_metrics && !feature_auth
```

2. **Feature registry:**
```go
type Feature struct {
    Name        string
    Description string
    Version     string
}

func RegisterFeature(f Feature)
func ListFeatures() []Feature
func IsEnabled(name string) bool
```

3. **Har bir feature fayli** — `init()` da o'zini register qiladi
4. **Default fayl** — hech qanday feature yoqilmaganda default xabar

5. **Test:**
```bash
# Hech qanday feature
go run .
# Enabled features: none (default mode)

# Faqat cache
go run -tags feature_cache .
# Enabled features:
#   - cache v1.0.0: In-memory caching system

# Hammasi
go run -tags "feature_cache,feature_metrics,feature_auth" .
# Enabled features:
#   - auth v1.0.0: JWT authentication
#   - cache v1.0.0: In-memory caching system
#   - metrics v1.0.0: Prometheus metrics endpoint
```

---

### Task 3: Binary Size Analyzer

**Maqsad:** Go binary hajmini tahlil qiluvchi tool yozish.

**Talablar:**

1. Dastur argumentdan binary yo'lni qabul qiladi
2. `go tool nm` natijasini parse qiladi
3. Quyidagi hisobotni chiqaradi:

```
Binary: ./myapp
Total size: 1.8MB

Top 10 packages by size:
  1. runtime          612 KB  (34%)
  2. fmt              198 KB  (11%)
  3. reflect          156 KB  (9%)
  4. internal/poll     89 KB  (5%)
  5. syscall           78 KB  (4%)
  ...

Size breakdown:
  .text (code):     1.1 MB
  .rodata (data):   200 KB
  .gopclntab:       300 KB
  .other:           200 KB

Optimization suggestions:
  - Use -ldflags "-s -w" to save ~500KB
  - Replace fmt with os.Stdout.Write to save ~350KB
  - Current binary is NOT stripped
```

4. **Bonus:** Ikki binary ni solishtirish rejimi:
```bash
go run analyzer.go --compare app-v1 app-v2
```

---

## 4. Questions

### 4.1 `package main` bo'lmasa nima bo'ladi? Go bu faylni qanday ko'radi?

<details>
<summary>Javob</summary>

`package main` bo'lmasa Go bu faylni **kutubxona (library)** deb hisoblaydi. `go run` bilan ishga tushirib bo'lmaydi:
```
go run: cannot run non-main package
```
`go build` esa `.a` (archive) fayl yaratadi, bajariladigan binary emas.

</details>

### 4.2 `func main()` argument qabul qilsa yoki qiymat qaytarsa nima bo'ladi?

<details>
<summary>Javob</summary>

```go
func main(args []string) {}     // Kompilyatsiya xatosi
func main() error { return nil } // Kompilyatsiya xatosi
```
`func main must have no arguments and no return values`. CLI argumentlar uchun `os.Args` ishlatiladi.

</details>

### 4.3 `fmt.Println` ichida `os.Stdout.Write` chaqiriladi. Agar `os.Stdout` yopilgan bo'lsa nima bo'ladi?

<details>
<summary>Javob</summary>

```go
os.Stdout.Close()
n, err := fmt.Println("Salom")
// n = 0, err = write /dev/stdout: bad file descriptor
```
Dastur crash qilmaydi — `fmt.Println` error qaytaradi. Lekin ko'pchilik bu error'ni tekshirmaydi.

</details>

### 4.4 Bitta faylda 10 ta `init()` bo'lishi mumkinmi?

<details>
<summary>Javob</summary>

**Ha.** Go'da bitta faylda cheksiz `init()` bo'lishi mumkin. Ular yozilish tartibida chaqiriladi. Lekin bu **anti-pattern** — kodni tushunishni qiyinlashtiradi.

</details>

### 4.5 `go run .` va `go run main.go` farqi nima?

<details>
<summary>Javob</summary>

- `go run main.go` — faqat `main.go` ni kompilyatsiya qiladi
- `go run .` — joriy papkadagi **barcha** `.go` fayllarni kompilyatsiya qiladi

Agar dastur bir nechta fayldan iborat bo'lsa, `go run main.go` xato berishi mumkin (boshqa fayldagi funksiyalar topilmaydi).

</details>

### 4.6 `fmt.Printf("%v", x)` va `fmt.Printf("%+v", x)` farqi nima?

<details>
<summary>Javob</summary>

```go
type User struct {
    Name string
    Age  int
}
u := User{"Anvar", 25}

fmt.Printf("%v\n", u)   // {Anvar 25}       — faqat qiymatlar
fmt.Printf("%+v\n", u)  // {Name:Anvar Age:25} — field nomlari bilan
fmt.Printf("%#v\n", u)  // main.User{Name:"Anvar", Age:25} — Go syntax
```

</details>

### 4.7 Nima uchun Go'da ishlatilmagan o'zgaruvchi kompilyatsiya xatosi, lekin ishlatilmagan funksiya xato emas?

<details>
<summary>Javob</summary>

Go dizaynerlarining qarori: ishlatilmagan o'zgaruvchi **har doim** xato (kod toza bo'lishi kerak), lekin funksiya boshqa fayllar tomonidan chaqirilishi mumkin (eksport qilingan bo'lsa) yoki kelajakda ishlatilishi mumkin. Package-level o'zgaruvchilar ham xato bermaydi — faqat lokal o'zgaruvchilar.

</details>

### 4.8 `go build` bilan yaratilgan binary boshqa kompyuterda ishlaydi-mi?

<details>
<summary>Javob</summary>

**Ha (CGO_ENABLED=0 holda)** — Go static binary yaratadi, hech qanday tashqi kutubxona kerak emas. Lekin **bir xil OS va arxitektura** bo'lishi kerak. Boshqa OS uchun cross-compile:
```bash
GOOS=linux GOARCH=amd64 go build -o app-linux .
```

</details>

### 4.9 `log.Fatal` va `panic` farqi nima?

<details>
<summary>Javob</summary>

| | `log.Fatal` | `panic` |
|---|---|---|
| Log yozadi | Ha (stderr) | Stack trace chiqaradi |
| defer | **Ishlamaydi** | **Ishlaydi** |
| recover | Mumkin emas | **Mumkin** |
| Exit code | 1 | 2 |
| Ichida | `os.Exit(1)` | `runtime.gopanic()` |

</details>

### 4.10 `//go:build` va `// +build` farqi nima?

<details>
<summary>Javob</summary>

`//go:build` — Go 1.17+ da kiritilgan **yangi sintaksis**. `// +build` — eski sintaksis (hali ishlaydi, lekin deprecated).

```go
// Yangi (Go 1.17+):
//go:build linux && amd64

// Eski:
// +build linux,amd64
```

Yangi sintaksis `&&`, `||`, `!`, `()` operatorlarini qo'llab-quvvatlaydi — aniqroq va o'qishga osonroq.

</details>

---

## 5. Mini Projects

### Project 1: System Info Reporter

**Maqsad:** Go'ning standart kutubxonasi bilan amaliy dastur yozish.

**Tavsif:** Dastur tizim haqida ma'lumot to'playdi va chiroyli formatda chiqaradi.

**Talablar:**

```
╔══════════════════════════════════════╗
║        SYSTEM INFO REPORTER          ║
╠══════════════════════════════════════╣
║  OS:           linux                 ║
║  Arch:         amd64                 ║
║  Go Version:   go1.22.0             ║
║  NumCPU:       8                     ║
║  GOMAXPROCS:   8                     ║
║  Goroutines:   1                     ║
║  Hostname:     myserver              ║
║  PID:          12345                 ║
║  Working Dir:  /home/user/project    ║
╠══════════════════════════════════════╣
║  ENVIRONMENT VARIABLES               ║
║  HOME:         /home/user            ║
║  GOPATH:       /home/user/go         ║
║  SHELL:        /bin/zsh              ║
╠══════════════════════════════════════╣
║  ARGUMENTS                           ║
║  [0]: ./sysinfo                      ║
║  [1]: --verbose                      ║
╚══════════════════════════════════════╝
```

**Ishlatadigan paketlar:** `runtime`, `os`, `fmt`, `strings`

**Qo'shimcha (bonus):**
- `--json` flag bilan JSON formatda chiqarish
- `--no-color` flag bilan ranglarni o'chirish
- `Sprintf` bilan string yaratish va oxirida bir marta `Println`

**Boshlang'ich shablon:**

```go
package main

import (
    "fmt"
    "os"
    "runtime"
    "strings"
)

func main() {
    info := collectInfo()
    format := "table" // default

    for _, arg := range os.Args[1:] {
        if arg == "--json" {
            format = "json"
        }
    }

    switch format {
    case "json":
        printJSON(info)
    default:
        printTable(info)
    }
}

type SystemInfo struct {
    OS          string
    Arch        string
    GoVersion   string
    NumCPU      int
    GOMAXPROCS  int
    Goroutines  int
    Hostname    string
    PID         int
    WorkDir     string
    EnvVars     map[string]string
    Args        []string
}

func collectInfo() SystemInfo {
    hostname, _ := os.Hostname()
    wd, _ := os.Getwd()

    return SystemInfo{
        OS:         runtime.GOOS,
        Arch:       runtime.GOARCH,
        GoVersion:  runtime.Version(),
        NumCPU:     runtime.NumCPU(),
        GOMAXPROCS: runtime.GOMAXPROCS(0),
        Goroutines: runtime.NumGoroutine(),
        Hostname:   hostname,
        PID:        os.Getpid(),
        WorkDir:    wd,
        EnvVars: map[string]string{
            "HOME":   os.Getenv("HOME"),
            "GOPATH": os.Getenv("GOPATH"),
            "SHELL":  os.Getenv("SHELL"),
        },
        Args: os.Args,
    }
}

func printTable(info SystemInfo) {
    // Sizning kodingiz
    _ = info
    _ = strings.Repeat("=", 40)
    _ = fmt.Sprintf
}

func printJSON(info SystemInfo) {
    // JSON formatda chiqarish (encoding/json ishlatmay, fmt bilan)
    _ = info
}
```

---

### Project 2: Multi-Format Converter

**Maqsad:** `fmt.Sprintf`, `strconv`, `strings` bilan amaliy dastur.

**Tavsif:** Kiritilgan sonni turli formatlarda chiqaruvchi dastur.

**Foydalanish:**
```bash
go run converter.go 255
```

**Kutilgan natija:**
```
╔══════════════════════════════════════╗
║        NUMBER CONVERTER              ║
║        Input: 255                    ║
╠══════════════════════════════════════╣
║  Decimal:      255                   ║
║  Binary:       11111111              ║
║  Octal:        377                   ║
║  Hex (lower):  ff                    ║
║  Hex (upper):  FF                    ║
║  Scientific:   2.550000e+02          ║
║  Unicode:      ÿ (U+00FF)           ║
║  Padded:       00000255              ║
╠══════════════════════════════════════╣
║  BIT ANALYSIS                        ║
║  Bits:    8                          ║
║  Bytes:   1                          ║
║  Signed:  -1 (int8)                  ║
║  Max u8:  255 (uint8 max!)           ║
╠══════════════════════════════════════╣
║  SIZE REPRESENTATION                 ║
║  255 B                               ║
║  0.25 KB                             ║
║  0.00 MB                             ║
╚══════════════════════════════════════╝
```

**Talablar:**
1. `os.Args` dan sonni o'qish
2. `strconv.Atoi` bilan parse qilish
3. `fmt.Sprintf` bilan barcha formatlar
4. Xato holatlari: argument yo'q, son emas, manfiy son

---

## 6. Challenge

### Ultimate Challenge: Go Binary Inspector

**Murakkablik:** Yuqori

**Maqsad:** Go binary haqida to'liq hisobot beradigan CLI dastur yarating.

**Tavsif:**

`go-inspect` — Go binary'ni tahlil qiluvchi tool:

```bash
go run inspector.go ./myapp
```

**Kutilgan natija:**

```
╔═══════════════════════════════════════════════════╗
║              GO BINARY INSPECTOR                  ║
║              Target: ./myapp                      ║
╠═══════════════════════════════════════════════════╣
║                                                   ║
║  BASIC INFO                                       ║
║  File size:      1,834,567 bytes (1.8 MB)        ║
║  File type:      ELF 64-bit LSB executable       ║
║  Go version:     go1.22.0                        ║
║  Build mode:     pie                             ║
║  CGO enabled:    false                           ║
║  Stripped:       no                              ║
║                                                   ║
║  BUILD INFO (if available)                        ║
║  Module:         myapp                           ║
║  Version:        v1.2.3                          ║
║  Commit:         abc1234                         ║
║  GOOS:           linux                           ║
║  GOARCH:         amd64                           ║
║                                                   ║
║  SIZE ANALYSIS                                    ║
║  ████████████████████░░░░░░  runtime    612KB 34% ║
║  ██████░░░░░░░░░░░░░░░░░░░  fmt        198KB 11% ║
║  █████░░░░░░░░░░░░░░░░░░░░  reflect    156KB  9% ║
║  ████████░░░░░░░░░░░░░░░░░  gopclntab  300KB 17% ║
║  █████████░░░░░░░░░░░░░░░░  other      568KB 29% ║
║                                                   ║
║  OPTIMIZATION SUGGESTIONS                         ║
║  [!] Binary is NOT stripped                       ║
║      Run: go build -ldflags "-s -w" ...          ║
║      Expected savings: ~500KB                     ║
║                                                   ║
║  [i] fmt package detected (198KB)                 ║
║      If only using Println, consider              ║
║      os.Stdout.WriteString for smaller binary     ║
║                                                   ║
╚═══════════════════════════════════════════════════╝
```

**Talablar:**

1. **Binary ma'lumot:**
   - `os.Stat` bilan fayl hajmi
   - `debug/buildinfo.ReadFile` bilan Go version va build info
   - ELF/Mach-O/PE detect (birinchi baytlarni o'qish)

2. **Size analysis:**
   - `os/exec` bilan `go tool nm -size <binary>` ishga tushirish
   - Natijani parse qilish
   - Package bo'yicha guruhlash

3. **Visual bar chart:**
   - `fmt.Printf` bilan Unicode block characters (`█`, `░`)
   - Proportional width

4. **Optimization suggestions:**
   - Strip status tekshirish
   - fmt usage detect
   - CGO status

5. **Qo'shimcha (bonus):**
   - `--compare binary1 binary2` — ikki binary solishtirish
   - `--json` — JSON formatda chiqarish
   - `--watch` — binary o'zgarganda qayta tahlil

**Ishlatadigan paketlar:**
- `os`, `os/exec`, `fmt`, `strings`, `strconv`
- `debug/buildinfo` (Go 1.18+)
- `path/filepath`

**Maslahat:** Bosqichma-bosqich yozing:
1. Avval fayl hajmi va basic info
2. Keyin `go tool nm` integration
3. Keyin visual output
4. Oxirida optimization suggestions
