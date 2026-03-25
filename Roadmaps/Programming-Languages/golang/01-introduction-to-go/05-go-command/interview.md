# Go Command — Interview Questions

## Mundarija (Table of Contents)

1. [Junior Level (7 ta)](#1-junior-level-7-ta)
2. [Middle Level (6 ta)](#2-middle-level-6-ta)
3. [Senior Level (6 ta)](#3-senior-level-6-ta)
4. [Scenario-Based (5 ta)](#4-scenario-based-5-ta)
5. [FAQ (5 ta)](#5-faq-5-ta)

---

## 1. Junior Level (7 ta)

### 1.1 `go run` va `go build` o'rtasidagi farqni tushuntiring

<details>
<summary>Javob</summary>

**`go run`:**
- Kodni kompilyatsiya qiladi va **darhol ishga tushiradi**
- Binary faylni vaqtinchalik papkada yaratadi va keyin o'chiradi
- Development uchun qulay — tez sinab ko'rish
- Har safar qayta kompilyatsiya qiladi

**`go build`:**
- Kodni kompilyatsiya qiladi va **binary fayl** yaratadi
- Binary diskda saqlanadi — keyingi safar qayta compile kerak emas
- Production deployment uchun ishlatiladi
- Build cache'dan foydalanadi — tezroq

```bash
# go run — binary saqlanmaydi
$ go run main.go
Hello, World!

# go build — binary yaratiladi
$ go build -o app main.go
$ ./app
Hello, World!
```

**Muhim farq:** `go run` binary'ni temp papkada yaratadi. `go build` esa joriy papkada (yoki `-o` bilan belgilangan joyda) saqlaydi.

**Qachon nima ishlatiladi:**
- `go run` — development, prototip, tez tekshirish
- `go build` — production, deployment, release

</details>

---

### 1.2 `go mod init` va `go mod tidy` nima uchun kerak?

<details>
<summary>Javob</summary>

**`go mod init`** — yangi Go modul (loyiha) yaratadi. `go.mod` faylini hosil qiladi:

```bash
$ go mod init github.com/username/myapp
# go.mod yaratiladi:
# module github.com/username/myapp
# go 1.23.0
```

**`go mod tidy`** — dependency'larni tartibga soladi:
- Kodda import qilingan, lekin `go.mod` da yo'q bo'lgan dependency'larni **qo'shadi**
- Kodda ishlatilmagan dependency'larni **o'chiradi**
- `go.sum` faylini yangilaydi

```bash
# Yangi dependency import qilgandan keyin
$ go mod tidy
go: finding module for package github.com/gin-gonic/gin
go: downloading github.com/gin-gonic/gin v1.9.1
```

**Qoida:** Har safar dependency o'zgarganda `go mod tidy` ishga tushiring.

</details>

---

### 1.3 `go fmt` nima qiladi va nima uchun muhim?

<details>
<summary>Javob</summary>

`go fmt` Go kodini **standart formatda** formatlaydi:
- Indentation (tab vs spaces)
- Qavs joylashuvi
- Import tartiblanishi
- Bo'sh qatorlar

```go
// Formatlashdan oldin
package main
import "fmt"
func main(){
fmt.Println("hello")
    x:=5
}

// go fmt dan keyin
package main

import "fmt"

func main() {
	fmt.Println("hello")
	x := 5
}
```

**Nima uchun muhim:**
1. **Bir xillik** — barcha Go dasturchilari bir xil uslubda yozadi
2. **Code review** — format haqida bahslashmaslik
3. **Avtomatlashtirish** — CI/CD da format tekshiruvi
4. **O'qilishi oson** — tanish format tezroq tushuniladi

```bash
$ go fmt ./...  # barcha fayllarni formatlash
```

</details>

---

### 1.4 `go vet` va `go fmt` o'rtasidagi farq nima?

<details>
<summary>Javob</summary>

| | `go fmt` | `go vet` |
|---|---------|----------|
| **Maqsad** | Kodni **formatlash** | Kodni **tahlil qilish** (xatolarni topish) |
| **Nima qiladi** | Indentation, spacing, import tartibini to'g'rilaydi | Potensial bug'larni topadi |
| **O'zgartiradi** | Ha — faylni qayta yozadi | Yo'q — faqat ogohlantirish beradi |
| **Misol** | Tab vs space to'g'rilash | Printf format xatosini topish |

```go
// go fmt to'g'rilaydi:
x:=5  →  x := 5

// go vet topadi:
fmt.Printf("%d", "text")  // %d raqam uchun, lekin string berilgan!
```

**Tartib:** Avval `go fmt`, keyin `go vet`, keyin `go test`.

</details>

---

### 1.5 `go test` buyrug'ini tushuntiring. Test fayl qanday nomlanishi kerak?

<details>
<summary>Javob</summary>

`go test` loyihadagi barcha testlarni topib ishga tushiradi.

**Test fayl qoidalari:**
1. Fayl nomi `_test.go` bilan tugashi kerak (masalan, `math_test.go`)
2. Test funksiya nomi `Test` bilan boshlanishi kerak
3. `*testing.T` parametri kerak

```go
// math.go
package math

func Add(a, b int) int { return a + b }
```

```go
// math_test.go
package math

import "testing"

func TestAdd(t *testing.T) {
    result := Add(2, 3)
    if result != 5 {
        t.Errorf("Add(2, 3) = %d; want 5", result)
    }
}
```

```bash
# Barcha testlar
$ go test ./...

# Verbose
$ go test -v ./...

# Aniq test
$ go test -run TestAdd ./...
```

</details>

---

### 1.6 `go install` nimani o'rnatadi va qayerga?

<details>
<summary>Javob</summary>

`go install` ikki xil ishlatiladi:

**1. Loyiha ichidan — binary'ni `$GOPATH/bin` ga o'rnatish:**

```bash
$ go install .
$ which myapp
/home/user/go/bin/myapp
```

**2. Tashqi tool o'rnatish:**

```bash
$ go install golang.org/x/tools/cmd/goimports@latest
$ which goimports
/home/user/go/bin/goimports
```

**Binary qayerga o'rnatiladi:**
- `$GOBIN` belgilangan bo'lsa → `$GOBIN`
- Aks holda → `$GOPATH/bin`

**Muhim:** `$GOPATH/bin` `PATH` ga qo'shilgan bo'lishi kerak:

```bash
export PATH=$PATH:$(go env GOPATH)/bin
```

</details>

---

### 1.7 `go env` buyrug'i bilan qaysi muhim o'zgaruvchilarni bilish kerak?

<details>
<summary>Javob</summary>

| O'zgaruvchi | Vazifasi | Misol |
|-------------|----------|-------|
| `GOPATH` | Go workspace papkasi | `/home/user/go` |
| `GOROOT` | Go o'rnatilgan papka | `/usr/local/go` |
| `GOOS` | Target OS | `linux`, `darwin`, `windows` |
| `GOARCH` | Target architecture | `amd64`, `arm64` |
| `GOCACHE` | Build cache papkasi | `~/.cache/go-build` |
| `GOMODCACHE` | Module cache | `~/go/pkg/mod` |
| `CGO_ENABLED` | C interop | `1` yoki `0` |
| `GOPROXY` | Module proxy | `https://proxy.golang.org` |

```bash
# Bitta o'zgaruvchi
$ go env GOPATH
/home/user/go

# O'zgartirish
$ go env -w GOBIN=/custom/path

# Qaytarish
$ go env -u GOBIN
```

</details>

---

## 2. Middle Level (6 ta)

### 2.1 `go build` flag'larini tushuntiring: `-o`, `-v`, `-race`, `-ldflags`, `-tags`

<details>
<summary>Javob</summary>

| Flag | Vazifasi | Misol |
|------|----------|-------|
| `-o` | Binary nomini belgilash | `go build -o server .` |
| `-v` | Compile bo'layotgan paketlarni ko'rsatish | `go build -v ./...` |
| `-race` | Data race detector yoqish | `go build -race -o app .` |
| `-ldflags` | Linker flag'lari (version inject, strip) | `go build -ldflags="-s -w -X main.V=1.0" .` |
| `-tags` | Conditional compilation (build tags) | `go build -tags prod .` |

**ldflags details:**
- `-s` — symbol table o'chirish
- `-w` — DWARF debug info o'chirish
- `-X pkg.Var=value` — string o'zgaruvchiga qiymat berish

```bash
# Production build
$ go build -trimpath \
    -ldflags="-s -w -X main.Version=1.2.3" \
    -tags prod \
    -o server ./cmd/server
```

**`-race` haqida:**
- Binary 2-10x sekinroq, 5-10x ko'p memory ishlatadi
- Faqat development/CI da ishlatish kerak
- Production'da **hech qachon** ishlatmang

</details>

---

### 2.2 `go test` flag'larini tushuntiring: `-v`, `-run`, `-count`, `-cover`, `-bench`

<details>
<summary>Javob</summary>

| Flag | Vazifasi | Misol |
|------|----------|-------|
| `-v` | Verbose — har bir test natijasini ko'rsatish | `go test -v ./...` |
| `-run` | Regex bilan test tanlash | `go test -run TestAdd ./...` |
| `-count` | Necha marta ishga tushirish (cache bypass) | `go test -count=1 ./...` |
| `-cover` | Code coverage foizini ko'rsatish | `go test -cover ./...` |
| `-bench` | Benchmark'larni ishga tushirish | `go test -bench=. ./...` |
| `-benchmem` | Benchmark memory stats | `go test -bench=. -benchmem ./...` |
| `-coverprofile` | Coverage ma'lumotini faylga yozish | `go test -coverprofile=c.out ./...` |
| `-timeout` | Test timeout | `go test -timeout 30s ./...` |
| `-short` | Qisqa rejim (uzoq testlarni skip) | `go test -short ./...` |
| `-parallel` | Parallel test soni | `go test -parallel 8 ./...` |

**Test cache:** Go test natijalarini cache'laydi. `-count=1` har doim qayta ishga tushiradi.

```bash
# CI/CD ideal buyruq
$ go test -race -count=1 -cover -timeout 5m ./...
```

</details>

---

### 2.3 `go mod replace` directive'ni qachon va nima uchun ishlatiladi?

<details>
<summary>Javob</summary>

`replace` directive dependency'ni boshqa manba bilan almashtiradi:

**1. Local development:**
```go
// go.mod
replace github.com/company/shared-lib => ../shared-lib
```

**2. Fork ishlatish:**
```go
replace github.com/original/pkg => github.com/myfork/pkg v1.0.0
```

**3. Bugfix kutish:**
```go
replace github.com/buggy/pkg v1.2.3 => github.com/buggy/pkg v1.2.4-fix
```

**Xavfi:**
- Local path CI/CD da ishlamaydi
- Production'ga push qilib qo'yish mumkin

**Best practice:**
```bash
# CI da tekshirish
$ grep "^replace" go.mod && echo "FAIL: replace directive found" && exit 1
```

</details>

---

### 2.4 `go generate` nima qiladi va qanday ishlatiladi?

<details>
<summary>Javob</summary>

`go generate` source koddagi `//go:generate` kommentlarini topib, ulardagi buyruqlarni ishga tushiradi.

```go
//go:generate stringer -type=Status
type Status int

const (
    StatusActive Status = iota
    StatusInactive
)
```

```bash
$ go generate ./...
# status_string.go yaratiladi
```

**Keng tarqalgan ishlatish holatlari:**
1. **Stringer** — enum'lar uchun `String()` method
2. **Mockgen** — interface mock'lari
3. **Protobuf** — gRPC kod generatsiyasi
4. **Swagger** — API dokumentatsiya

**Muhim:**
- `go build` avtomatik `go generate` **ishga tushmaydi**
- Generate natijalarini git'ga commit qiling
- CI da: `go generate ./... && git diff --exit-code`

</details>

---

### 2.5 `go tool pprof` nima uchun kerak va qanday ishlatiladi?

<details>
<summary>Javob</summary>

`pprof` — Go'ning built-in **profiling** vositasi. CPU, memory, goroutine va boshqa resource'larni tahlil qiladi.

**Ishlatish:**

```go
import _ "net/http/pprof" // Avtomatik endpoint qo'shadi

func main() {
    go http.ListenAndServe(":6060", nil)
    // ...
}
```

```bash
# CPU profiling
$ go tool pprof http://localhost:6060/debug/pprof/profile?seconds=30
(pprof) top 10     # eng ko'p CPU ishlatgan funksiyalar
(pprof) web        # grafik ko'rish (browser'da)

# Memory profiling
$ go tool pprof http://localhost:6060/debug/pprof/heap
(pprof) top        # eng ko'p memory ishlatgan
(pprof) list func  # funksiya ichidagi allocation'lar
```

**Test bilan:**
```bash
$ go test -cpuprofile=cpu.prof -memprofile=mem.prof -bench=. ./...
$ go tool pprof cpu.prof
```

**flat vs cum:**
- `flat` — funksiyaning o'zi sarflagan vaqt
- `cum` (cumulative) — funksiya + uning chaqirgan funksiyalar

</details>

---

### 2.6 Go toolchain'ni boshqa tillar toolchain'lari (npm, cargo, maven) bilan solishtiring

<details>
<summary>Javob</summary>

| Feature | Go | Node.js (npm) | Rust (cargo) | Java (Maven) |
|---------|-----|---------------|--------------|---------------|
| **Config files** | **0** (zero-config) | 5+ | 1 (Cargo.toml) | 1 (pom.xml) |
| **Build** | `go build` | `npm run build` | `cargo build` | `mvn compile` |
| **Test** | `go test` | jest (3rd party) | `cargo test` | junit (3rd party) |
| **Format** | `go fmt` (built-in) | prettier (3rd party) | `cargo fmt` | 3rd party |
| **Lint** | `go vet` (built-in) | eslint (3rd party) | `cargo clippy` | 3rd party |
| **Race detect** | `go test -race` | N/A | N/A | N/A |
| **Deps** | `go mod` | `package.json` | `Cargo.toml` | `pom.xml` |
| **Binary** | Static, standalone | Needs Node.js | Static | Needs JVM |

**Go'ning asosiy ustunligi:** Zero configuration. Yangi loyiha uchun `go mod init` yetarli — boshqa hech narsa kerak emas.

</details>

---

## 3. Senior Level (6 ta)

### 3.1 Production Go binary yaratishda qaysi flag'larni ishlatish kerak va nima uchun?

<details>
<summary>Javob</summary>

```bash
CGO_ENABLED=0 go build \
    -trimpath \
    -ldflags="-s -w \
      -X main.Version=$(git describe --tags) \
      -X main.Commit=$(git rev-parse --short HEAD) \
      -X 'main.BuildTime=$(date -u +%Y-%m-%dT%H:%M:%SZ)'" \
    -o server ./cmd/server
```

| Flag | Sabab |
|------|-------|
| `CGO_ENABLED=0` | Static binary — Docker scratch'da ishlaydi, C library dependency yo'q |
| `-trimpath` | Local fayl yo'llarini yashirish — security |
| `-s` | Symbol table o'chirish — binary kichikroq |
| `-w` | DWARF debug info o'chirish — binary kichikroq |
| `-X main.Version=...` | Version inject — runtime'da versiya ko'rinadi |
| `-buildid=` (optional) | Reproducible build uchun |

**Nima ISHLATMASLIK kerak:**
- `-race` — 10x sekin, 10x ko'p memory
- `-gcflags="-N -l"` — optimization o'chirilgan
- `CGO_ENABLED=1` (Docker scratch uchun)

</details>

---

### 3.2 Reproducible build nima va Go'da qanday amalga oshiriladi?

<details>
<summary>Javob</summary>

**Reproducible build** — bir xil source koddan **bit-for-bit bir xil** binary yaratish. Supply chain security uchun muhim.

**Go'da reproducible build:**

```bash
CGO_ENABLED=0 go build \
    -trimpath \
    -ldflags="-s -w -buildid= -X main.BuildTime=2024-01-01T00:00:00Z" \
    -o app .
```

**Kerakli shartlar:**
1. `-trimpath` — local path'lar o'chirilgan
2. `-buildid=` — random build ID o'chirilgan
3. `CGO_ENABLED=0` — C compiler farqlarini yo'qotish
4. Fixed timestamps — `-X` bilan doimiy vaqt
5. Bir xil Go version — Docker bilan ta'minlash
6. `go.sum` — dependency versiyalari locked

**Tekshirish:**
```bash
$ sha256sum app1 app2
# Ikkalasi bir xil hash bo'lishi kerak
```

**Nima uchun muhim:**
- Supply chain security — binary source'ga mos kelishini tekshirish
- Audit — "bu binary qaysi koddan yaratilgan?"
- Compliance — regulatory talablar

</details>

---

### 3.3 Build tags bilan enterprise feature management qanday amalga oshiriladi?

<details>
<summary>Javob</summary>

Build tags compile-time'da kodning qaysi qismlari binary'ga kirishini boshqaradi:

```go
//go:build enterprise

// features_enterprise.go
package features

func init() {
    Register("sso", true)
    Register("audit-log", true)
    Register("multi-tenant", true)
}
```

```go
//go:build !enterprise

// features_community.go
package features

func init() {
    Register("sso", false)
    Register("audit-log", false)
    Register("multi-tenant", false)
}
```

```bash
# Community
$ go build -o app-free .

# Enterprise
$ go build -tags enterprise -o app-enterprise .
```

**Runtime if/else dan ustunligi:**
1. Binary'da faqat kerakli kod — kichikroq hajm
2. Attack surface kamayadi — premium kod free binary'da yo'q
3. Licensing enforcement — reverse engineer qilishda ham ko'rinmaydi

**CI/CD matrix:**
```yaml
strategy:
  matrix:
    tier: [free, pro, enterprise]
steps:
  - run: go build -tags ${{ matrix.tier }} -o app-${{ matrix.tier }} .
```

</details>

---

### 3.4 Go module dependency'larini audit qilish va supply chain security ta'minlashni tushuntiring

<details>
<summary>Javob</summary>

**1. Vulnerability scanning:**
```bash
$ govulncheck ./...
```

**2. Dependency integrity:**
```bash
$ go mod verify
all modules verified
```

**3. Dependency graph audit:**
```bash
$ go mod graph | wc -l         # Nechta dependency?
$ go mod why github.com/pkg    # Nima uchun kerak?
```

**4. SBOM generation:**
```bash
$ go version -m app            # Binary'dagi dependency'lar
```

**5. Private modules:**
```bash
$ go env -w GOPRIVATE=github.com/company/*
$ go env -w GONOSUMDB=github.com/company/*
```

**6. CI checks:**
```bash
# Replace directive tekshiruvi
grep "^replace" go.mod && exit 1

# Tidy tekshiruvi
go mod tidy && git diff --exit-code go.mod go.sum

# Vulnerability scan
govulncheck ./...
```

**Best practices:**
- Har hafta `go list -m -u all` bilan yangilanishlarni tekshiring
- Major version yangilash — alohida PR
- `go.sum` ni har doim commit qiling
- CI da `govulncheck` mandatory

</details>

---

### 3.5 Go'da profiling strategiyasi qanday bo'lishi kerak (development vs production)?

<details>
<summary>Javob</summary>

**Development profiling:**
```bash
# Benchmark bilan
$ go test -cpuprofile=cpu.prof -memprofile=mem.prof -bench=. ./...
$ go tool pprof -http=:8080 cpu.prof
```

**Staging profiling:**
```go
import _ "net/http/pprof"

// Staging'da pprof endpoint ochiq
go http.ListenAndServe("localhost:6060", nil)
```

**Production profiling:**
- pprof endpoint'ni **internal network** dan faqat ochish
- Continuous profiling (Datadog, Pyroscope)
- Low-overhead sampling

```go
// Production-safe pprof
if os.Getenv("ENABLE_PPROF") == "true" {
    go func() {
        mux := http.NewServeMux()
        mux.HandleFunc("/debug/pprof/", pprof.Index)
        http.ListenAndServe("localhost:6060", mux)
    }()
}
```

**Profiling strategiyasi:**
1. **Benchmark regression** — har bir PR da `benchstat` bilan solishtirish
2. **CPU profiling** — `pprof top` bilan hot function topish
3. **Memory profiling** — `pprof -alloc_space` bilan allocation topish
4. **Goroutine profiling** — goroutine leak topish
5. **Trace** — latency spike'larni aniqlash

</details>

---

### 3.6 CGO_ENABLED=0 strategik qarorining trade-off'larini tushuntiring

<details>
<summary>Javob</summary>

| Aspect | CGO_ENABLED=0 | CGO_ENABLED=1 |
|--------|---------------|---------------|
| Binary type | Fully static | Dynamic (libc bilan) |
| Docker scratch | Ishlaydi | Ishlamaydi |
| Cross-compile | Oson (faqat GOOS/GOARCH) | Qiyin (cross-compiler kerak) |
| C libraries | Ishlamaydi | Ishlaydi (SQLite, etc.) |
| DNS resolver | Pure Go | C resolver (glibc) |
| Performance | Slightly faster startup | Slightly better DNS |
| Attack surface | Minimal | libc vulnerability mumkin |
| Binary size | Kichikroq | Kattaroq |
| Reproducibility | Oson | Qiyin (C compiler farqlari) |

**Qachon CGO=0:**
- Docker scratch/distroless
- Cross-compilation
- Minimal attack surface
- Pure Go dependencies

**Qachon CGO=1:**
- SQLite (go-sqlite3)
- System libraries (OpenSSL, etc.)
- C/C++ libraries integration
- CGO-dependent packages

**Alternative strategies:**
```bash
# CGO kerak lekin static ham kerak
$ CGO_ENABLED=1 go build \
    -ldflags='-linkmode external -extldflags "-static"' \
    -tags 'netgo osusergo' -o app .
```

</details>

---

## 4. Scenario-Based (5 ta)

### 4.1 Production'da deploy qilingan Go service 500 error qaytarmoqda. Binary debug qilish mumkin emas (stripped). Qanday yondashgan bo'lardingiz?

<details>
<summary>Javob</summary>

**Bosqichma-bosqich:**

1. **Loglar tekshirish:**
```bash
$ kubectl logs deployment/myapp --tail=100
```

2. **pprof bilan goroutine dump:**
```bash
$ curl http://service:6060/debug/pprof/goroutine?debug=2
# Barcha goroutine'larning stack trace'larini ko'rish
```

3. **Heap profiling (memory leak tekshirish):**
```bash
$ go tool pprof http://service:6060/debug/pprof/heap
(pprof) top 10
```

4. **Staging'da debug binary deploy qilish:**
```bash
$ go build -gcflags="all=-N -l" -o app-debug .
# Delve bilan connect
$ dlv exec --headless --listen=:2345 --api-version=2 ./app-debug
```

5. **Core dump yoqish (agar crash bo'lsa):**
```bash
$ GOTRACEBACK=crash ./app
# Core dump yaratilganda:
$ dlv core app core.12345
```

6. **Metrics tekshirish:**
- Prometheus/Grafana'da latency, error rate, goroutine count
- Memory usage trend

**Xulosa:** Production'da debug binary bo'lmasa ham, pprof + structured logging + metrics yetarli.

</details>

---

### 4.2 Jamoada 5 ta microservice bor. Barcha service'lar uchun yagona build pipeline qanday yaratiladi?

<details>
<summary>Javob</summary>

**Monorepo + Makefile pattern:**

```
monorepo/
├── services/
│   ├── api/
│   │   ├── cmd/main.go
│   │   └── Dockerfile
│   ├── worker/
│   │   ├── cmd/main.go
│   │   └── Dockerfile
│   └── scheduler/
│       ├── cmd/main.go
│       └── Dockerfile
├── shared/
│   ├── config/
│   └── middleware/
├── go.mod
├── go.sum
├── go.work
└── Makefile
```

```makefile
SERVICES := api worker scheduler
VERSION := $(shell git describe --tags --always)

.PHONY: build-all test-all docker-all

build-all: $(addprefix build-,$(SERVICES))

build-%:
	CGO_ENABLED=0 go build -trimpath \
		-ldflags="-s -w -X main.Version=$(VERSION)" \
		-o bin/$* ./services/$*/cmd

test-all:
	go test -race -count=1 -coverprofile=coverage.out ./...

docker-all: $(addprefix docker-,$(SERVICES))

docker-%: build-%
	docker build -f services/$*/Dockerfile \
		-t company/$*:$(VERSION) .
```

**CI/CD:**
```yaml
# Faqat o'zgargan service'larni build qilish
changed=$(git diff --name-only HEAD~1 | grep "services/" | cut -d/ -f2 | sort -u)
for svc in $changed; do
    make build-$svc docker-$svc
done
```

</details>

---

### 4.3 Go loyihada dependency'larning biri critical vulnerability bor. Qanday harakat qilasiz?

<details>
<summary>Javob</summary>

**1. Vulnerability aniqlash:**
```bash
$ govulncheck ./...
Vulnerability #1: GO-2024-1234
  Package: github.com/vulnerable/pkg
  Version: v1.2.3
  Fixed in: v1.2.5
  Severity: HIGH
```

**2. Severity baholash:**
- Bu vulnerability bizning kodimizda ishlatilayaptimi?
```bash
$ go mod why github.com/vulnerable/pkg
# Qaysi paketimiz ishlatayotganini ko'rish
```

**3. Patch mavjudligini tekshirish:**
```bash
$ go list -m -versions github.com/vulnerable/pkg
v1.2.0 v1.2.1 v1.2.2 v1.2.3 v1.2.4 v1.2.5
```

**4. Yangilash:**
```bash
$ go get github.com/vulnerable/pkg@v1.2.5
$ go mod tidy
$ go test -race ./...
$ govulncheck ./...  # Qayta tekshirish
```

**5. Agar patch yo'q bo'lsa:**
- Fork qilib, patch qo'yish
- `replace` directive bilan vaqtinchalik almshtirish
- Alternative kutubxona topish
- Vulnerable funksiyani ishlatmaslik

**6. Post-incident:**
- Dependabot/Renovate sozlash
- CI da `govulncheck` mandatory qilish
- Security policy yozish

</details>

---

### 4.4 Go binary hajmi 50MB dan oshib ketdi. Qanday optimizatsiya qilgan bo'lardingiz?

<details>
<summary>Javob</summary>

**Bosqichma-bosqich:**

**1. Hajm tahlili:**
```bash
$ go build -o app .
$ ls -lh app
50M app

# Section hajmlari
$ size -A app | sort -rnk2 | head -10
```

**2. Debug info o'chirish (~30% kamaytirish):**
```bash
$ go build -ldflags="-s -w" -o app .
# 50M → 35M
```

**3. Dependency audit:**
```bash
$ go mod graph | wc -l
# Ko'p dependency = katta binary
# Keraksiz dependency'larni o'chirish
$ go mod tidy
```

**4. CGO o'chirish:**
```bash
$ CGO_ENABLED=0 go build -ldflags="-s -w" -o app .
# Ba'zan biroz kichikroq
```

**5. Import audit:**
```go
// XATO — butun paketni import qilish
import "github.com/aws/aws-sdk-go-v2"

// TO'G'RI — faqat kerakli subpackage
import "github.com/aws/aws-sdk-go-v2/service/s3"
```

**6. Build tags bilan keraksiz kodni chiqarish:**
```go
//go:build !debug
// Debug kodi production binary'ga kirmaydi
```

**7. UPX compression (ehtiyotkorlik bilan):**
```bash
$ upx --best app
# 35M → 12M (lekin antivirus warning mumkin)
```

</details>

---

### 4.5 CI/CD pipeline'da `go test` 15 daqiqa ishlayapti. Qanday tezlashtirish mumkin?

<details>
<summary>Javob</summary>

**1. Bottleneck topish:**
```bash
$ go test -v ./... 2>&1 | grep -E "^--- (PASS|FAIL)" | sort -t'(' -k2 -rn | head -10
--- PASS: TestIntegration (120.5s)
--- PASS: TestE2E (85.3s)
--- PASS: TestSlowQuery (45.2s)
```

**2. Test parallelism oshirish:**
```bash
$ go test -parallel 8 ./...
```

**3. Short mode ishlatish:**
```go
func TestSlow(t *testing.T) {
    if testing.Short() {
        t.Skip("skipping slow test")
    }
    // ...
}
```

```bash
$ go test -short ./...  # Tez testlar faqat
```

**4. Test cache ishlatish:**
```bash
# Cache-friendly: go test ./... (default cache'dan foydalanadi)
# go test -count=1 faqat kerak bo'lganda
```

**5. Test'larni ajratish:**
```yaml
# CI da parallel jobs
jobs:
  unit-test:
    run: go test -short ./...           # 2 min
  integration-test:
    run: go test -tags integration ./... # 10 min (parallel)
```

**6. Faqat o'zgargan paketlarni test qilish:**
```bash
CHANGED=$(go list ./... | xargs -I{} sh -c 'git diff --name-only HEAD~1 | grep -q "$(echo {} | sed "s|myapp/||")" && echo {}')
go test $CHANGED
```

**7. Test binary cache (CI):**
```yaml
- uses: actions/cache@v4
  with:
    path: |
      ~/go/pkg/mod
      ~/.cache/go-build
    key: go-${{ hashFiles('**/go.sum') }}
```

</details>

---

## 5. FAQ (5 ta)

### 5.1 `go get` va `go install` farqi nima?

<details>
<summary>Javob</summary>

Go 1.17+ dan beri:

| | `go get` | `go install` |
|---|---------|-------------|
| **Maqsad** | `go.mod` ga dependency qo'shish/yangilash | Binary o'rnatish |
| **go.mod** | O'zgartiradi | O'zgartirmaydi |
| **Binary** | Yaratmaydi (Go 1.17+) | `$GOPATH/bin` ga o'rnatadi |

```bash
# Dependency qo'shish
$ go get github.com/gin-gonic/gin@v1.9.1

# Tool o'rnatish
$ go install golang.org/x/tools/cmd/goimports@latest
```

**Muhim:** Go 1.17 dan oldin `go get` ham binary o'rnatgan. Endi faqat `go install` binary o'rnatadi.

</details>

---

### 5.2 `./...` pattern nima degani?

<details>
<summary>Javob</summary>

`./...` — joriy papka va barcha sub-papkalardagi Go paketlarni bildiradi.

```bash
# Barcha paketlarni test qilish
$ go test ./...

# Barcha paketlarni build qilish
$ go build ./...

# Barcha paketlarni formatlash
$ go fmt ./...

# Bitta paket
$ go test ./internal/handler

# Bitta sub-tree
$ go test ./internal/...
```

Bu Go'ning **pattern matching** sintaksisi — boshqa tildagi `**/*` ga o'xshash.

</details>

---

### 5.3 Go build cache qanday ishlaydi?

<details>
<summary>Javob</summary>

Go build natijalarini `$GOCACHE` da saqlaydi. Cache key = SHA256(inputs):
- Go version
- Build flags
- Source file content
- Dependency build IDs
- Environment variables (GOOS, GOARCH)

```bash
# Cache hajmi
$ du -sh $(go env GOCACHE)
245M

# Cache tozalash
$ go clean -cache          # build cache
$ go clean -testcache      # test cache
$ go clean -modcache       # module cache (ehtiyot!)

# Cache statistika
$ go build -x ./... 2>&1 | grep "cache" | head -5
```

Har qanday input o'zgarsa — cache miss. Shuning uchun ikkinchi build birinchisidan ancha tez.

</details>

---

### 5.4 `go.mod` va `go.sum` farqi nima? Ikkalasini ham commit qilish kerakmi?

<details>
<summary>Javob</summary>

| | `go.mod` | `go.sum` |
|---|---------|----------|
| **Vazifasi** | Module nomi, Go version, dependency'lar | Dependency'lar checksums |
| **O'lcham** | Kichik | Katta (transitive deps ham) |
| **O'zgartirish** | Qo'lda yoki `go mod tidy` | Faqat avtomatik |
| **Git** | **Albatta** commit | **Albatta** commit |

```bash
# go.mod — sizning to'g'ridan-to'g'ri dependency'laringiz
module myapp
go 1.23.0
require github.com/gin-gonic/gin v1.9.1

# go.sum — BARCHA dependency'lar (transitive) ning checksum'lari
github.com/gin-gonic/gin v1.9.1 h1:4idE...
github.com/gin-gonic/gin v1.9.1/go.mod h1:...
```

**Ikkalasini ham commit qiling!** `go.sum` — security uchun muhim. Agar kimdir dependency'ni o'zgartirsa, checksum mos kelmaydi va build fail bo'ladi.

</details>

---

### 5.5 Go'da cross-compilation qanday ishlaydi?

<details>
<summary>Javob</summary>

Go `GOOS` va `GOARCH` environment variables orqali har qanday platformaga build qilish mumkin — **boshqa tool o'rnatish shart emas**.

```bash
# Linux uchun
$ GOOS=linux GOARCH=amd64 go build -o app-linux .

# macOS (Intel)
$ GOOS=darwin GOARCH=amd64 go build -o app-mac .

# macOS (Apple Silicon)
$ GOOS=darwin GOARCH=arm64 go build -o app-mac-arm .

# Windows
$ GOOS=windows GOARCH=amd64 go build -o app.exe .

# Raspberry Pi
$ GOOS=linux GOARCH=arm GOARM=7 go build -o app-pi .
```

**Muhim:** `CGO_ENABLED=0` qo'shish tavsiya etiladi — aks holda target platformada C compiler kerak bo'ladi.

```bash
# Qo'llab-quvvatlanadigan platformalar ro'yxati
$ go tool dist list
aix/ppc64
android/amd64
darwin/amd64
darwin/arm64
linux/amd64
linux/arm64
windows/amd64
# ... 40+ platform
```

</details>
