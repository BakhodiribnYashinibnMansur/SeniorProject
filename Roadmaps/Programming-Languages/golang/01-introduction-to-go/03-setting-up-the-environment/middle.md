# Setting Up the Environment — Middle Level

## Table of Contents

1. [Introduction](#1-introduction)
2. [Core Concepts](#2-core-concepts)
3. [Pros & Cons](#3-pros--cons)
4. [Use Cases](#4-use-cases)
5. [Code Examples](#5-code-examples)
6. [Product Use / Feature](#6-product-use--feature)
7. [Error Handling](#7-error-handling)
8. [Security Considerations](#8-security-considerations)
9. [Performance Optimization](#9-performance-optimization)
10. [Debugging Guide](#10-debugging-guide)
11. [Best Practices](#11-best-practices)
12. [Edge Cases & Pitfalls](#12-edge-cases--pitfalls)
13. [Common Mistakes](#13-common-mistakes)
14. [Tricky Points](#14-tricky-points)
15. [Comparison with Other Languages](#15-comparison-with-other-languages)
16. [Test](#16-test)
17. [Tricky Questions](#17-tricky-questions)
18. [Cheat Sheet](#18-cheat-sheet)
19. [Summary](#19-summary)
20. [What You Can Build](#20-what-you-can-build)
21. [Further Reading](#21-further-reading)
22. [Related Topics](#22-related-topics)

---

## 1. Introduction

Middle darajada Go muhitni sozlash faqat o'rnatishdan iborat emas — bu GOPATH erasidan Go Modules ga o'tishni tushunish, proxy va xavfsizlik sozlamalarini bilish, ko'p versiyali muhitda ishlash, workspace mode, Docker va CI/CD integratsiyasini o'z ichiga oladi. Bu bo'limda **nima uchun** va **qachon** savollarga javob beriladi.

---

## 2. Core Concepts

### 2.1. GOPATH erasi vs Go Modules erasi

```mermaid
timeline
    title Go Dependency Management Evolyutsiyasi
    2009-2012 : Go 1.0
               : GOPATH only
               : Barcha kod $GOPATH/src da
    2013-2015 : Go 1.5
               : vendor/ papka
               : Eksperimental dependency vendoring
    2016-2017 : Go 1.8-1.9
               : dep, glide, govendor
               : 3rd-party dependency management
    2018 : Go 1.11
         : Go Modules (eksperimental)
         : go mod init, go.mod, go.sum
    2019 : Go 1.13
         : GO111MODULE=auto default
         : GOPROXY=proxy.golang.org
    2021 : Go 1.16
         : GO111MODULE=on default
         : go install pkg@version
    2022 : Go 1.18
         : go.work (workspace mode)
    2023 : Go 1.21
         : GOTOOLCHAIN
         : Toolchain management
```

#### GOPATH erasi strukturasi

```
$GOPATH/ (masalan ~/go)
├── src/                              # Barcha source code
│   ├── github.com/
│   │   ├── user/project-a/           # Sizning loyihangiz
│   │   └── lib/dependency/           # Dependency — faqat 1 versiya!
│   └── golang.org/
│       └── x/tools/
├── pkg/
│   └── linux_amd64/                  # Kompilyatsiya qilingan .a fayllar
│       └── github.com/lib/dep.a
└── bin/
    └── project-a                     # go install binary-lar
```

**GOPATH ning muammolari:**

| Muammo | Tavsif | Modules yechimi |
|--------|--------|-----------------|
| Versiya yo'q | Bitta dependency ning faqat 1 versiyasi | `go.mod` da har bir dependency versiyalangan |
| Joylashuv | Barcha kod `$GOPATH/src` ichida | Istalgan papkada `go mod init` |
| Qayta tiklanmaydigan build | `go get` har safar oxirgi versiyani oladi | `go.sum` bilan aniq versiya va hash |
| Diamond dependency | A → B v1, A → C → B v2 — conflict | MVS (Minimal Version Selection) |

### 2.2. go.mod va go.sum batafsil

**go.mod strukturasi:**

```go
// go.mod
module github.com/company/myservice    // 1. Modul nomi

go 1.23.4                               // 2. Minimal Go versiya

toolchain go1.23.4                       // 3. Toolchain versiya (Go 1.21+)

require (                                // 4. To'g'ridan-to'g'ri dependency-lar
    github.com/gin-gonic/gin v1.9.1
    github.com/jackc/pgx/v5 v5.5.1
    go.uber.org/zap v1.26.0
)

require (                                // 5. Bilvosita (indirect) dependency-lar
    github.com/bytedance/sonic v1.10.2 // indirect
    github.com/klauspost/cpuid/v2 v2.2.6 // indirect
    golang.org/x/sys v0.15.0 // indirect
)

retract (                                // 6. Qaytarib olingan versiyalar
    v1.0.0 // Critical bug found
    [v1.1.0, v1.2.0] // Security vulnerability
)

exclude (                                // 7. Chiqarib tashlangan versiyalar
    github.com/old/pkg v1.0.0
)

replace (                                // 8. Almashtirish
    github.com/company/mylib => ../mylib              // Lokal dev
    github.com/broken/pkg v1.0.0 => github.com/fixed/pkg v1.0.1  // Fork
)
```

**go.sum strukturasi:**

```
github.com/gin-gonic/gin v1.9.1 h1:4idEAncQnU5cB7BeOkPtxjfCSye0AAm1R0RVIqFPSsg=
github.com/gin-gonic/gin v1.9.1/go.mod h1:hPrL/0KcuqOSEYJMaBK2NRtea7So7lEFo2/aBJSBJa4=
```

Har bir qator: `module version hash_type:hash`
- `h1:` — SHA-256 hash, base64 encoded
- Ikki qator: modul source hash + go.mod hash

### 2.3. GOPROXY, GOPRIVATE, GONOSUMCHECK

```mermaid
flowchart LR
    A["go get pkg"] --> B{GOPRIVATE ga mos?}
    B -->|Ha| C["To'g'ridan-to'g'ri \n git clone"]
    B -->|Yo'q| D{GOPROXY}
    D --> E["proxy.golang.org"]
    E --> F{Topildimi?}
    F -->|Ha| G["Yuklash + \n sum.golang.org \n tekshirish"]
    F -->|Yo'q| H["direct — \n git clone"]
    H --> I{GONOSUMCHECK \n ga mos?}
    I -->|Ha| J["Checksum tekshirilmaydi"]
    I -->|Yo'q| G
```

```bash
# GOPROXY — proxy zanjiri
go env -w GOPROXY=https://proxy.golang.org,https://goproxy.io,direct

# GOPRIVATE — private modullar (proxy va sum DB dan o'tkazilmaydi)
go env -w GOPRIVATE=github.com/company/*,gitlab.company.com/*

# GONOSUMCHECK — checksum tekshirilmaydigan modullar
go env -w GONOSUMCHECK=github.com/company/*

# GONOSUMDB — sum database ga so'rov yuborilmaydigan modullar
go env -w GONOSUMDB=github.com/company/*

# GONOPROXY — proxy ishlatilmaydigan modullar
go env -w GONOPROXY=github.com/company/*
```

### 2.4. Ko'p Go versiyalari bilan ishlash

```bash
# 1. Qo'shimcha versiyani o'rnatish
go install golang.org/dl/go1.22.0@latest
go1.22.0 download

# 2. Ishlatish
go1.22.0 version
# go version go1.22.0 linux/amd64

go1.22.0 build ./...
go1.22.0 test ./...

# 3. SDK joylashuvi
ls ~/sdk/go1.22.0/

# 4. GOTOOLCHAIN bilan avtomatik versiya boshqarish (Go 1.21+)
go env -w GOTOOLCHAIN=auto

# go.mod da:
# go 1.23.4
# toolchain go1.23.4
# Bu Go 1.21+ ni ishlatganingizda avtomatik to'g'ri versiyani tanlaydi
```

### 2.5. Workspace mode — go.work

```bash
# Ko'p modulli loyihada lokal development uchun
go work init ./api ./lib ./shared

# go.work fayli yaratiladi:
cat go.work
```

```go
// go.work
go 1.23.4

use (
    ./api
    ./lib
    ./shared
)

// Ixtiyoriy: boshqa modulni replace qilish
replace github.com/company/external-lib => ../external-lib
```

```bash
# go.work buyruqlari
go work use ./new-module    # Modul qo'shish
go work edit -dropuse=./old # Modul olib tashlash
go work sync               # go.mod larni sinxronlash

# MUHIM: go.work ni .gitignore ga qo'shing!
# Bu faqat lokal development uchun
echo "go.work" >> .gitignore
echo "go.work.sum" >> .gitignore
```

### 2.6. Docker bilan Go development

```dockerfile
# === Multi-stage build ===

# Stage 1: Build
FROM golang:1.23.4-alpine AS builder

WORKDIR /app

# Dependency-larni avval yuklash (cache layer)
COPY go.mod go.sum ./
RUN go mod download && go mod verify

# Source code ni copy va build
COPY . .
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -ldflags="-w -s" -o /app/server ./cmd/server

# Stage 2: Runtime
FROM alpine:3.19

RUN apk --no-cache add ca-certificates tzdata
COPY --from=builder /app/server /server

EXPOSE 8080
ENTRYPOINT ["/server"]
```

```yaml
# docker-compose.yml — development uchun
version: "3.9"
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile.dev
    volumes:
      - .:/app                    # Hot reload uchun
      - go-mod-cache:/go/pkg/mod  # Module cache persist
      - go-build-cache:/root/.cache/go-build  # Build cache
    ports:
      - "8080:8080"
    environment:
      - GOPROXY=https://proxy.golang.org,direct

volumes:
  go-mod-cache:
  go-build-cache:
```

```dockerfile
# Dockerfile.dev — development uchun (hot reload bilan)
FROM golang:1.23.4

RUN go install github.com/air-verse/air@latest

WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download

COPY . .
CMD ["air", "-c", ".air.toml"]
```

### 2.7. CI/CD muhit sozlash

```yaml
# GitHub Actions
name: Go CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        go-version: ['1.22', '1.23']

    steps:
      - uses: actions/checkout@v4

      - name: Set up Go
        uses: actions/setup-go@v5
        with:
          go-version: ${{ matrix.go-version }}
          cache: true  # go module cache avtomatik

      - name: Verify dependencies
        run: |
          go mod verify
          go mod tidy
          git diff --exit-code go.mod go.sum

      - name: Build
        run: go build -v ./...

      - name: Test
        run: go test -race -coverprofile=coverage.out ./...

      - name: Lint
        uses: golangci/golangci-lint-action@v4
        with:
          version: latest
```

```yaml
# GitLab CI
stages:
  - test
  - build

variables:
  GOPATH: ${CI_PROJECT_DIR}/.go
  GOPROXY: https://proxy.golang.org,direct

cache:
  key: ${CI_COMMIT_REF_SLUG}
  paths:
    - .go/pkg/mod/
    - .cache/go-build/

test:
  stage: test
  image: golang:1.23.4
  script:
    - go mod download
    - go test -race -v ./...
    - go vet ./...

build:
  stage: build
  image: golang:1.23.4
  script:
    - CGO_ENABLED=0 go build -ldflags="-w -s" -o app ./cmd/server
  artifacts:
    paths:
      - app
```

---

## 3. Pros & Cons

### Go Modules vs Boshqa yondashuvlar

| Jihat | Go Modules | GOPATH (eski) | Vendor (eski) |
|-------|-----------|---------------|---------------|
| Versiya boshqarish | MVS algoritmi, aniq versiyalar | Yo'q — faqat oxirgi commit | Manual vendoring |
| Joylashuv | Istalgan papka | Faqat `$GOPATH/src` | `$GOPATH/src` + `vendor/` |
| Qayta tiklanadigan build | Ha (`go.sum` bilan) | Yo'q | Qisman (commit hash ga bog'liq) |
| Security | Checksum DB | Yo'q | Yo'q |
| CI/CD integratsiya | Mukammal (cache, proxy) | Murakkab | Murakkab |
| Multi-module | go.work bilan | Imkonsiz | Imkonsiz |

### Workspace mode trade-offs

| Afzallik | Kamchilik |
|----------|-----------|
| Lokal development tezlashadi | go.work ni git ga push qilmaslik kerak |
| Ko'p modulni birga tahrirlash oson | CI/CD da go.work ishlamaydi |
| replace directive kerak emas | Jamoa a'zolari orasida farqli muhit |

---

## 4. Use Cases

- **Monorepo lokal development** — `go.work` bilan bir nechta modulni birga tahrirlash
- **Private modullar bilan ishlash** — `GOPRIVATE` va git credential sozlash
- **CI/CD pipeline** — Docker multi-stage build, GitHub Actions cache
- **Team standardizatsiyasi** — `.go-version`, `GOTOOLCHAIN`, golangci-lint config
- **Cross-compilation** — `GOOS`, `GOARCH` bilan turli platformalar uchun build

---

## 5. Code Examples

### 5.1. Private modul sozlash

```bash
# 1. GOPRIVATE sozlash
go env -w GOPRIVATE=github.com/mycompany/*,gitlab.internal.com/*

# 2. Git credential sozlash (SSH)
git config --global url."git@github.com:mycompany/".insteadOf "https://github.com/mycompany/"

# 3. HTTPS bilan (token)
git config --global url."https://oauth2:${GITHUB_TOKEN}@github.com/mycompany/".insteadOf "https://github.com/mycompany/"

# 4. .netrc bilan (CI uchun)
cat > ~/.netrc << EOF
machine github.com
login oauth2
password ${GITHUB_TOKEN}
EOF
chmod 600 ~/.netrc
```

### 5.2. go.mod bilan ishlash (ilg'or)

```bash
# Dependency versiyasini yangilash
go get github.com/gin-gonic/gin@v1.10.0

# Barcha dependency larni yangilash (minor/patch)
go get -u ./...

# Faqat patch versiyalarni yangilash
go get -u=patch ./...

# Dependency grafini ko'rish
go mod graph

# Nima uchun dependency kerakligini tushuntirish
go mod why github.com/klauspost/cpuid/v2
# github.com/company/myservice
# github.com/gin-gonic/gin
# github.com/bytedance/sonic
# github.com/klauspost/cpuid/v2

# go.mod ni tahrirlash (programmatic)
go mod edit -require github.com/pkg/errors@v0.9.1
go mod edit -exclude github.com/broken/pkg@v1.0.0
go mod edit -replace github.com/old/pkg=github.com/new/pkg@v2.0.0
go mod edit -retract v1.0.0
```

### 5.3. Makefile bilan muhit standardizatsiyasi

```makefile
# Makefile
.PHONY: all build test lint clean setup

GO := go
GOFLAGS := -v
BINARY := myservice
LDFLAGS := -ldflags="-w -s -X main.version=$(shell git describe --tags --always)"

# Minimal Go versiya tekshiruvi
REQUIRED_GO_VERSION := 1.23
CURRENT_GO_VERSION := $(shell go version | awk '{print $$3}' | sed 's/go//')

## setup: Development muhitni sozlash
setup:
	@echo "Go versiya tekshiruvi..."
	@go version
	go mod download
	go mod verify
	go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest
	go install github.com/air-verse/air@latest
	@echo "Muhit tayyor!"

## build: Binary build qilish
build:
	CGO_ENABLED=0 $(GO) build $(GOFLAGS) $(LDFLAGS) -o bin/$(BINARY) ./cmd/server

## test: Barcha testlarni ishga tushirish
test:
	$(GO) test -race -coverprofile=coverage.out ./...
	$(GO) tool cover -func=coverage.out

## lint: Kodni tekshirish
lint:
	golangci-lint run ./...
	$(GO) vet ./...

## clean: Build artefaktlarni tozalash
clean:
	rm -rf bin/
	$(GO) clean -cache -testcache

## dev: Hot reload bilan development
dev:
	air -c .air.toml

## mod-check: go.mod tozaligini tekshirish
mod-check:
	$(GO) mod tidy
	git diff --exit-code go.mod go.sum

## docker: Docker image build
docker:
	docker build -t $(BINARY):latest .
```

### 5.4. Air konfiguratsiyasi (Hot reload)

```toml
# .air.toml
root = "."
tmp_dir = "tmp"

[build]
  bin = "./tmp/main"
  cmd = "go build -o ./tmp/main ./cmd/server"
  delay = 1000
  exclude_dir = ["assets", "tmp", "vendor", "node_modules", ".git"]
  exclude_file = []
  exclude_regex = ["_test.go"]
  exclude_unchanged = true
  follow_symlink = false
  full_bin = ""
  include_dir = []
  include_ext = ["go", "tpl", "tmpl", "html", "yaml", "toml"]
  kill_delay = "0s"
  log = "build-errors.log"
  send_interrupt = false
  stop_on_error = true

[log]
  time = false

[color]
  app = ""
  build = "yellow"
  main = "magenta"
  runner = "green"
  watcher = "cyan"

[misc]
  clean_on_exit = true
```

### 5.5. golangci-lint konfiguratsiyasi

```yaml
# .golangci.yml
run:
  go: '1.23'
  timeout: 5m
  modules-download-mode: readonly

linters:
  enable:
    - errcheck
    - govet
    - staticcheck
    - unused
    - gosimple
    - ineffassign
    - typecheck
    - gocritic
    - gofmt
    - goimports
    - misspell
    - prealloc

linters-settings:
  gocritic:
    enabled-tags:
      - diagnostic
      - style
      - performance
  errcheck:
    check-type-assertions: true
    check-blank: true

issues:
  exclude-rules:
    - path: _test\.go
      linters:
        - errcheck
        - gocritic
```

---

## 6. Product Use / Feature

| Mahsulot | Scale | Go muhit sozlash xususiyatlari |
|----------|-------|-------------------------------|
| **Google** | 10,000+ Go dasturchilar | Ichki GOPROXY, custom toolchain, monorepo |
| **Uber** | 2,000+ Go microservice | Private module proxy, standardized Makefile |
| **Cloudflare** | 500+ Go service | Multi-arch build, WASM support, custom lint |
| **Docker** | Core platform Go'da | Multi-stage build, moby monorepo, go.work |
| **HashiCorp** | Terraform, Vault, Consul | Plugin system Go modules bilan, provider SDK |

---

## 7. Error Handling

### 7.1. Module resolution xatolari

```bash
# Xato: "module not found"
# Sabab: Private repo ga kirish yo'q
go get github.com/company/private-lib@v1.0.0
# fatal: could not read Username for 'https://github.com': terminal prompts disabled

# Yechim:
go env -w GOPRIVATE=github.com/company/*
git config --global url."git@github.com:company/".insteadOf "https://github.com/company/"
```

### 7.2. Checksum mismatch

```bash
# Xato: "verifying module: checksum mismatch"
# Sabab: Module content o'zgargan (xavfli!) yoki cache eskirgan

# Yechim 1: go.sum ni yangilash
go mod tidy

# Yechim 2: Module cache ni tozalash
go clean -modcache
go mod download

# Yechim 3: Agar shubhali — tekshiring!
# Bu supply chain attack belgisi bo'lishi mumkin
go mod verify
```

### 7.3. Workspace mode xatolari

```bash
# Xato: "go.work file found but not expected"
# Sabab: CI/CD da go.work fayli bor

# Yechim 1: GOWORK env
GOWORK=off go build ./...

# Yechim 2: .gitignore
echo "go.work" >> .gitignore
echo "go.work.sum" >> .gitignore
```

### 7.4. Go versiya mos kelmaslik

```bash
# Xato: "go: go.mod requires go >= 1.23"
# Yechim 1: Go ni yangilash
go install golang.org/dl/go1.23.4@latest && go1.23.4 download

# Yechim 2: GOTOOLCHAIN=auto (Go 1.21+)
go env -w GOTOOLCHAIN=auto
# Avtomatik to'g'ri versiyani yuklaydi
```

---

## 8. Security Considerations

### 8.1. GOPROXY xavfsizlik modeli

```mermaid
flowchart TD
    A["go get pkg@v1.0.0"] --> B["GOPROXY: proxy.golang.org"]
    B --> C["Modul yuklanadi"]
    C --> D["GOSUMDB: sum.golang.org"]
    D --> E{"Hash tekshiruvi"}
    E -->|"Mos"| F["OK — modul ishlatiladi"]
    E -->|"Mos emas"| G["XATO — security violation!"]
    G --> H["Modul rad etiladi"]
```

### 8.2. Supply chain xavfsizlik

```bash
# 1. go.sum ni doimo commit qiling
git add go.sum
git commit -m "Update dependency checksums"

# 2. go mod verify — integrity tekshirish
go mod verify
# all modules verified

# 3. govulncheck — zaifliklarni tekshirish
go install golang.org/x/vuln/cmd/govulncheck@latest
govulncheck ./...

# 4. Dependency audit
go list -m -json all | jq '.Path'
```

### 8.3. Private module xavfsizligi

```bash
# GOPRIVATE — proxy va sum DB dan chetlab o'tish
go env -w GOPRIVATE=github.com/company/*

# Bu quyidagilarni o'rnatadi:
# GONOPROXY=github.com/company/*
# GONOSUMCHECK=github.com/company/*
# GONOSUMDB=github.com/company/*

# CI/CD da token bilan:
# GitHub Actions secret
echo "machine github.com login oauth2 password ${TOKEN}" > ~/.netrc
```

### 8.4. GOFLAGS xavfsizligi

```bash
# GOFLAGS — global go buyruq flaglari
# EHTIYOT: Bu barcha go buyruqlariga ta'sir qiladi!

# Xavfli:
go env -w GOFLAGS="-mod=mod"  # har doim go.mod ni o'zgartiradi

# Xavfsiz:
go env -w GOFLAGS="-mod=readonly"  # go.mod o'zgartirilmaydi
# CI/CD da tavsiya etiladi
```

---

## 9. Performance Optimization

### 9.1. Build cache optimizatsiyasi

```bash
# Build cache statistikasi
go env GOCACHE
# ~/.cache/go-build

# Cache hajmi
du -sh $(go env GOCACHE)

# Build vaqtini o'lchash
time go build ./...          # Birinchi marta: ~5s
time go build ./...          # Cache bilan: ~0.1s

# Cache invalidation sabablari:
# 1. Go versiyasi o'zgardi
# 2. Build flags o'zgardi (-race, -ldflags, etc.)
# 3. Source fayl o'zgardi
# 4. Dependency o'zgardi
# 5. CGO_ENABLED o'zgardi
```

### 9.2. CI/CD da cache

```yaml
# GitHub Actions — go module cache
- uses: actions/setup-go@v5
  with:
    go-version: '1.23'
    cache: true          # Avtomatik GOMODCACHE + GOCACHE
    cache-dependency-path: '**/go.sum'
```

```bash
# Docker layer cache
# MUHIM: go.mod/go.sum ni avval COPY qiling
COPY go.mod go.sum ./
RUN go mod download         # Bu layer cache bo'ladi
COPY . .                    # Source o'zgarganda faqat bu layer qayta ishlaydi
```

### 9.3. Parallel build

```bash
# Go default: GOMAXPROCS = CPU cores
go env GOMAXPROCS

# Build parallelism
# go build avtomatik parallel kompilyatsiya qiladi
# Lekin CI da resurs cheklangan bo'lishi mumkin:
GOMAXPROCS=4 go build ./...

# Test parallelism
go test -parallel 8 ./...    # Test ichidagi parallelism
go test -count=1 ./...       # Cache o'chirish (benchmark uchun)
```

### 9.4. Module download tezlashtirish

```bash
# Proxy orqali (default) — tezroq, chunki CDN
go env GOPROXY
# https://proxy.golang.org,direct

# Ko'p proxy (fallback)
go env -w GOPROXY=https://proxy.golang.org,https://goproxy.io,direct

# Vendor mode — offline build
go mod vendor              # vendor/ papkaga dependency copy
go build -mod=vendor ./... # vendor/ dan build
# CI da internet muammolari bo'lganda foydali
```

---

## 10. Debugging Guide

### 10.1. go env muammolarni aniqlash

```bash
# Barcha muhit o'zgaruvchilarini ko'rish
go env

# JSON formatda (parsing uchun)
go env -json

# Bitta o'zgaruvchini tekshirish
go env GOPATH GOROOT GOPROXY GOPRIVATE GOTOOLCHAIN

# Qayerdan kelganini bilish
go env GOENV
# ~/.config/go/env

# go env faylini o'qish
cat $(go env GOENV)
```

### 10.2. Module resolution debugging

```bash
# Dependency grafini ko'rish
go mod graph

# Nima uchun dependency kerak
go mod why -m github.com/some/package

# Module versiyalarini ko'rish
go list -m -versions github.com/gin-gonic/gin

# Barcha dependency larni JSON da
go list -m -json all

# go.mod vs actual import tekshiruvi
go mod tidy -v  # Verbose — nima o'zgarganini ko'rsatadi
```

### 10.3. Build muammolari

```bash
# Verbose build (qaysi paketlar build bo'layotgan)
go build -v ./...

# Bajarilgan buyruqlarni ko'rish
go build -x ./...

# Build debug (nima uchun qayta build bo'layotgan)
go build -v -a ./...  # -a: barcha paketlarni qayta build (cache chetlab o'tish)

# CGO muammolari
CGO_ENABLED=1 go build -v ./...
# Agar CGO kerak bo'lsa, C compiler o'rnatilganligini tekshiring:
gcc --version || apt install build-essential
```

### 10.4. Proxy va network muammolari

```bash
# Proxy ni tekshirish
curl -v https://proxy.golang.org/github.com/gin-gonic/gin/@v/list

# Direct mode bilan tekshirish
GOPROXY=direct go get github.com/some/package@latest

# Verbose download
go get -v github.com/some/package@latest

# Git credential tekshirish
GIT_TERMINAL_PROMPT=1 git ls-remote https://github.com/company/private-repo
```

---

## 11. Best Practices

### 11.1. Loyiha standardizatsiyasi

```
myproject/
├── .github/
│   └── workflows/
│       └── ci.yml          # CI/CD
├── .golangci.yml           # Linter config
├── .air.toml               # Hot reload config
├── .gitignore              # go.work, binary, etc.
├── Makefile                # Build, test, lint buyruqlari
├── Dockerfile              # Production build
├── Dockerfile.dev          # Development build
├── go.mod
├── go.sum
├── cmd/
│   └── server/
│       └── main.go
├── internal/               # Private packages
│   ├── handler/
│   ├── service/
│   └── repository/
└── pkg/                    # Public packages
    └── util/
```

### 11.2. .gitignore

```gitignore
# Go
*.exe
*.exe~
*.dll
*.so
*.dylib
*.test
*.out
bin/
vendor/   # Agar vendor mode ishlatilmasa

# Go workspace (faqat lokal development)
go.work
go.work.sum

# IDE
.idea/
.vscode/settings.json  # Shaxsiy sozlamalar
*.swp

# OS
.DS_Store
Thumbs.db

# Environment
.env
.env.local
```

### 11.3. Dependency management qoidalari

1. **`go mod tidy`** — har safar dependency o'zgarganda ishga tushiring
2. **`go mod verify`** — CI da har doim tekshiring
3. **Versiyani aniq ko'rsating** — `go get pkg@v1.2.3`, `@latest` dan saqlaning
4. **`go.sum` ni commit qiling** — xavfsizlik uchun
5. **`replace`** faqat development uchun — production da olib tashlang

---

## 12. Edge Cases & Pitfalls

### 12.1. go.work va CI/CD

```bash
# go.work CI da muammo qiladi
# Yechim 1: GOWORK=off
GOWORK=off go build ./...

# Yechim 2: CI skriptida
if [ -f go.work ]; then
    echo "WARNING: go.work found, using GOWORK=off"
    export GOWORK=off
fi
```

### 12.2. replace directive va publish

```go
// XATO: replace bilan publish qilingan modul
// Boshqalar go get qilganda replace ishlamaydi!
module github.com/user/mylib

replace github.com/user/dep => ../dep  // Bu faqat sizda ishlaydi!
```

### 12.3. Indirect dependency conflict

```bash
# Turli versiyalar talab qilinsa:
go mod graph | grep "problematic/pkg"

# MVS eng yuqori versiyani tanlaydi
# Agar bu xato keltirsa:
go mod edit -replace github.com/problematic/pkg@v2.0.0=github.com/problematic/pkg@v1.5.0
```

---

## 13. Common Mistakes

| # | Xato | To'g'ri usul |
|---|------|-------------|
| 1 | `go.work` ni git ga push qilish | `.gitignore` ga qo'shing |
| 2 | CI da `go mod tidy` ishlatmaslik | CI pipeline ga `go mod tidy && git diff --exit-code` qo'shing |
| 3 | `replace` directive ni publish qilish | Faqat development uchun ishlating, release oldidan olib tashlang |
| 4 | `GOPRIVATE` o'rnatmaslik | Private repo lar uchun `go env -w GOPRIVATE=...` |
| 5 | Docker da cache layer ishlatmaslik | `COPY go.mod go.sum ./` va `RUN go mod download` ni alohida qiling |
| 6 | `go get -u ./...` bilan barcha ni yangilash | Ehtiyotkorlik bilan, faqat keraklisini yangilang |
| 7 | Vendor va modules ni aralashtirish | Bitta yondashuvni tanlang |

---

## 14. Tricky Points

### 14.1. MVS (Minimal Version Selection) — Go'ning o'ziga xos yondashuvi

```
A → B v1.2.0
A → C → B v1.4.0

# Boshqa tizimlar (npm, pip): B v1.4.0 (eng yangi mos versiya)
# Go MVS: B v1.4.0 (eng kichik qoniqarli versiya)

# Farq: MVS hech qachon talab qilinganidan YUQORI versiya tanlamaydi
# Bu reproducible build ga yordam beradi
```

### 14.2. go.mod dagi `go` directive semantikasi

```go
// go.mod
go 1.23    // Bu "minimum Go versiya" degan ma'no
           // Lekin Go 1.21+ da bu TILNING versiyasini ham belgilaydi
           // Masalan: go 1.22 → range over int, range over func
```

### 14.3. GOTOOLCHAIN va go directive munosabati

```bash
# Go 1.21+ da:
# go.mod: go 1.23.4
# go.mod: toolchain go1.23.4

# GOTOOLCHAIN=auto bo'lganda:
# 1. go directive — minimal til versiyasi
# 2. toolchain directive — aniq toolchain versiyasi
# 3. Agar o'rnatilgan Go < toolchain → avtomatik yuklaydi

# GOTOOLCHAIN=local bo'lganda:
# Faqat o'rnatilgan Go ishlatiladi
# Agar mos kelmasa — xato beradi
```

---

## 15. Comparison with Other Languages

### Toolchain sozlash taqqoslash

| Jihat | Go | Node.js | Python | Rust |
|-------|-----|---------|--------|------|
| **O'rnatish** | Bitta binary, dependency yo'q | Node + npm/yarn/pnpm | Python + pip + virtualenv | rustup + cargo |
| **Dependency fayl** | `go.mod` | `package.json` | `requirements.txt` / `pyproject.toml` | `Cargo.toml` |
| **Lock fayl** | `go.sum` (checksum only) | `package-lock.json` | `pip.lock` / `poetry.lock` | `Cargo.lock` |
| **Versiya boshqarish** | `go install golang.org/dl/goX.Y@latest` | `nvm` | `pyenv` | `rustup` |
| **Paket registry** | proxy.golang.org | npmjs.com | pypi.org | crates.io |
| **Private paketlar** | GOPRIVATE + git | `.npmrc` + token | pip + token | Cargo registry |
| **Build cache** | Ichki (GOCACHE) | Yo'q (webpack/turbo) | Yo'q | sccache |
| **Cross-compilation** | GOOS/GOARCH (built-in) | Yo'q (native) | Yo'q | `rustup target add` |
| **Monorepo** | go.work | npm workspaces | Yo'q (monorepo tools) | Cargo workspaces |
| **IDE** | VS Code + gopls, GoLand | VS Code, WebStorm | VS Code + Pylance, PyCharm | VS Code + rust-analyzer |

### Decision Matrix: Qachon qaysi toolni ishlatish

| Vaziyat | Yechim |
|---------|--------|
| Bitta modul, kichik loyiha | `go mod init` — boshqa hech narsa kerak emas |
| Ko'p modul, lokal dev | `go.work` — lekin git ga push qilmang |
| Private modullar | `GOPRIVATE` + git SSH/token |
| CI/CD | Docker multi-stage + cache layers |
| Monorepo | `go.work` (dev) + Makefile (CI) |
| Versiya pinning | `toolchain` directive (Go 1.21+) |
| Offline build | `go mod vendor` + `-mod=vendor` |

---

## 16. Test

**1. GOPATH erasi va Go Modules o'rtasidagi asosiy farq nima?**

- A) GOPATH tezroq ishlaydi
- B) Modules dependency versiya boshqarish va istalgan papkada ishlash imkonini beradi
- C) GOPATH ko'proq xavfsiz
- D) Modules faqat Go 1.20+ da ishlaydi

<details>
<summary>Javob</summary>
B) Go Modules har bir dependency uchun aniq versiya, checksum tekshiruvi va istalgan papkada loyiha yaratish imkonini beradi. GOPATH da bu imkoniyatlar yo'q edi.
</details>

**2. `go.work` faylini git ga push qilish kerakmi?**

- A) Ha, jamoa bilan bo'lishish kerak
- B) Yo'q, faqat lokal development uchun
- C) Faqat CI/CD uchun
- D) `go.work.sum` bilan birga push qilish kerak

<details>
<summary>Javob</summary>
B) `go.work` faqat lokal development uchun. Har bir dasturchi o'zining lokal muhitiga mos `go.work` yaratadi. `.gitignore` ga qo'shing.
</details>

**3. GOPRIVATE o'rnatganda qaysi sozlamalar avtomatik o'zgaradi?**

- A) Faqat GOPROXY
- B) GONOPROXY, GONOSUMCHECK, GONOSUMDB
- C) GOROOT va GOPATH
- D) GOCACHE va GOMODCACHE

<details>
<summary>Javob</summary>
B) `GOPRIVATE=github.com/company/*` o'rnatganda, `GONOPROXY`, `GONOSUMCHECK` va `GONOSUMDB` ham shu pattern ga o'rnatiladi (agar alohida o'rnatilmagan bo'lsa).
</details>

**4. Docker multi-stage build da `COPY go.mod go.sum ./` ni avval qilish nima uchun muhim?**

- A) Xavfsizlik uchun
- B) Docker layer cache — faqat dependency o'zgarganda qayta yuklash
- C) Go talab qiladi
- D) Farqi yo'q

<details>
<summary>Javob</summary>
B) Docker har bir `COPY` va `RUN` ni alohida layer sifatida cache qiladi. Agar source code o'zgarsa lekin go.mod o'zgarmasa, `go mod download` layer cache dan ishlatiladi — build tezlashadi.
</details>

**5. MVS (Minimal Version Selection) qanday ishlaydi?**

- A) Har doim eng yangi versiyani tanlaydi
- B) Har doim eng eski versiyani tanlaydi
- C) Barcha talablarni qoniqtiradigan eng kichik versiyani tanlaydi
- D) Random versiya tanlaydi

<details>
<summary>Javob</summary>
C) MVS barcha dependency lar talab qilgan versiyalar orasidan eng kichik qoniqarli versiyani tanlaydi. Masalan: A→B v1.2, C→B v1.4 bo'lsa, B v1.4 tanlanadi (ikkalasini qoniqtiradi). Lekin v1.5 tanlamaydi (talab qilinmagan).
</details>

**6. `GOTOOLCHAIN=auto` va `GOTOOLCHAIN=local` o'rtasidagi farq?**

- A) Farq yo'q
- B) `auto` kerakli toolchain ni yuklaydi, `local` faqat o'rnatilganini ishlatadi
- C) `local` tezroq ishlaydi
- D) `auto` faqat CI uchun

<details>
<summary>Javob</summary>
B) `auto` — go.mod dagi `toolchain` directive ga qarab, kerak bo'lsa yangi Go versiyani avtomatik yuklaydi. `local` — faqat tizimda o'rnatilgan Go ni ishlatadi, mos kelmasa xato beradi.
</details>

**7. `go mod vendor` qachon foydali?**

- A) Har doim ishlatish kerak
- B) Hech qachon ishlatmang
- C) Offline build, air-gapped muhit, va network ishonchsiz CI uchun
- D) Faqat GOPATH mode da

<details>
<summary>Javob</summary>
C) `go mod vendor` dependency larni `vendor/` papkaga saqlaydi. Bu offline build, internet yo'q muhit (air-gapped), va network ishonchsiz CI pipeline larda foydali. Lekin odatda Go module cache yetarli.
</details>

**8. CI/CD da `go mod tidy && git diff --exit-code go.mod go.sum` nima uchun kerak?**

- A) Build tezlashtirish uchun
- B) go.mod/go.sum commit qilinganligini va to'g'riligini tekshirish uchun
- C) Dependency larni yangilash uchun
- D) Cache tozalash uchun

<details>
<summary>Javob</summary>
B) Bu buyruq `go mod tidy` ishga tushiradi va go.mod/go.sum o'zgarganligini tekshiradi. Agar o'zgargan bo'lsa — demak dasturchi `go mod tidy` ni ishga tushirmagan. CI fail qiladi va dasturchi go.mod/go.sum ni to'g'irlab commit qilishi kerak.
</details>

**9. `go get -u ./...` va `go get -u=patch ./...` o'rtasidagi farq?**

- A) Farq yo'q
- B) `-u` minor+patch yangilaydi, `-u=patch` faqat patch
- C) `-u` patch, `-u=patch` major yangilaydi
- D) `-u=patch` tezroq ishlaydi

<details>
<summary>Javob</summary>
B) `go get -u ./...` — barcha dependency larni eng yangi minor yoki patch versiyaga yangilaydi (v1.2.0 → v1.5.0). `go get -u=patch ./...` — faqat patch versiyani yangilaydi (v1.2.0 → v1.2.5). Patch xavfsizroq.
</details>

**10. `replace` directive publish qilingan modulda ishlaydi mi?**

- A) Ha, barcha foydalanuvchilarda ishlaydi
- B) Yo'q, faqat root module (main module) da ishlaydi
- C) Faqat go.work bilan ishlaydi
- D) Go 1.23+ da ishlaydi

<details>
<summary>Javob</summary>
B) `replace` faqat root (main) modulda ta'sir qiladi. Agar siz `github.com/user/lib` ni publish qilsangiz va unda `replace` bo'lsa, boshqalar `go get github.com/user/lib` qilganda `replace` hisobga olinmaydi. Bu ataylab shunday — dependency lar o'z replace ni majburlay olmasligi kerak.
</details>

---

## 17. Tricky Questions

**1. `go mod download` va `go mod tidy` o'rtasidagi farq nima? Qachon qaysi birini ishlatish kerak?**

<details>
<summary>Javob</summary>

```bash
# go mod download — go.mod dagi barcha dependency larni yuklaydi
# go.mod ni O'ZGARTIRMAYDI
go mod download

# go mod tidy — import larni tahlil qilib:
# 1. Kerakli dependency larni go.mod ga qo'shadi
# 2. Keraksiz dependency larni o'chiradi
# 3. go.sum ni yangilaydi
go mod tidy
```

**Qachon:**
- `go mod download` — CI/CD da dependency cache qilish uchun (go.mod allaqachon to'g'ri)
- `go mod tidy` — development da, yangi import qo'shgandan keyin

**Muhim farq:** `tidy` source kodni tahlil qiladi, `download` faqat go.mod ni o'qiydi.
</details>

**2. Bitta loyihada ikki xil Go versiyasi kerak bo'lsa (masalan, main module Go 1.23, lekin dependency Go 1.21 talab qilsa), nima bo'ladi?**

<details>
<summary>Javob</summary>

Go MVS bu muammoni hal qiladi:
- Sizning `go.mod`: `go 1.23`
- Dependency `go.mod`: `go 1.21`
- Natija: Go 1.23 ishlatiladi (yuqori versiya)

Lekin teskari vaziyat:
- Sizning `go.mod`: `go 1.21`
- Dependency `go.mod`: `go 1.23` (til xususiyatlari)
- Natija: `go 1.23` talab qilinadi, chunki dependency 1.23 til xususiyatlarini ishlatishi mumkin

Go 1.21+ da `GOTOOLCHAIN=auto` bu muammoni avtomatik hal qiladi — kerakli versiyani yuklaydi.
</details>

**3. `GOPROXY=off` va `GOFLAGS=-mod=vendor` birga ishlatilsa nima bo'ladi?**

<details>
<summary>Javob</summary>

```bash
GOPROXY=off GOFLAGS=-mod=vendor go build ./...
```

Bu holda:
1. `GOPROXY=off` — hech qanday proxy dan yuklash taqiqlangan
2. `-mod=vendor` — faqat `vendor/` papkadan dependency olish

Natija: To'liq offline build. Agar `vendor/` da barcha dependency bor bo'lsa — muvaffaqiyatli build. Agar yo'q bo'lsa — xato.

Bu air-gapped (internetdan uzilgan) muhitlar uchun ideal sozlama.
</details>

**4. `go env -w` bilan o'rnatilgan qiymat va shell `export` bilan o'rnatilgan qiymat conflict qilsa, qaysi biri ustun?**

<details>
<summary>Javob</summary>

Ustuvorlik tartibi (yuqoridan pastga):
1. **Shell environment variable** (`export GOPROXY=...`) — eng ustun
2. **`go env -w`** (`go env -w GOPROXY=...`) — ikkinchi
3. **Default qiymat** — eng past

```bash
# go env -w bilan:
go env -w GOPROXY=https://goproxy.io

# Shell da:
export GOPROXY=direct

# Natija:
go env GOPROXY
# direct   <-- shell export ustun!
```

Bu bilish muhim, chunki CI/CD da ko'pincha shell env ishlatiladi va lokal `go env -w` sozlamalari ignore bo'ladi.
</details>

**5. go.sum da dependency o'chirilgandan keyin ham eski hash lar qolishi mumkinmi?**

<details>
<summary>Javob</summary>

Ha! `go get` bilan dependency o'chirilganda yoki versiya o'zgartirilganda, `go.sum` dagi eski qatorlar avtomatik o'chirilmaydi.

```bash
# go.sum ni tozalash:
go mod tidy  # Bu keraksiz hash larni o'chiradi

# Tekshirish:
git diff go.sum
```

Shuning uchun `go mod tidy` ni muntazam ishga tushirish va CI da `git diff --exit-code go.sum` tekshirish muhim.
</details>

**6. `go.work` mavjud bo'lganda `go mod tidy` boshqacha ishlaydi mi?**

<details>
<summary>Javob</summary>

Ha! `go.work` mavjud bo'lganda:

```bash
# go.work MAVJUD:
go mod tidy
# workspace dagi boshqa modullarni hisobga oladi
# replace directive larga ehtiyoj kamayadi

# go.work MAVJUD EMAS:
go mod tidy
# faqat joriy modul go.mod ni tahlil qiladi
```

Shuning uchun:
```bash
# Bitta modulni mustaqil tidy qilish:
GOWORK=off go mod tidy

# Barcha workspace modullarni tidy qilish:
go work sync
```

CI/CD da `GOWORK=off` ishlatish tavsiya etiladi, chunki CI da `go.work` bo'lmasligi kerak.
</details>

---

## 18. Cheat Sheet

### Muhit sozlash buyruqlari

| Buyruq | Tavsif |
|--------|--------|
| `go env` | Barcha o'zgaruvchilar |
| `go env -json` | JSON formatda |
| `go env -w KEY=VAL` | O'zgaruvchi o'rnatish |
| `go env -u KEY` | Default ga qaytarish |
| `go env GOENV` | Config fayl joylashuvi |

### Module buyruqlari

| Buyruq | Tavsif |
|--------|--------|
| `go mod init MODULE` | Yangi modul |
| `go mod tidy` | Dependency tozalash |
| `go mod download` | Dependency yuklash |
| `go mod verify` | Integrity tekshirish |
| `go mod graph` | Dependency grafi |
| `go mod why -m PKG` | Nima uchun kerak |
| `go mod vendor` | Vendor papka yaratish |
| `go mod edit -require PKG@VER` | Programmatic tahrirlash |

### Workspace buyruqlari

| Buyruq | Tavsif |
|--------|--------|
| `go work init ./a ./b` | Workspace yaratish |
| `go work use ./c` | Modul qo'shish |
| `go work sync` | Sinxronlash |
| `GOWORK=off go build` | Workspace o'chirish |

### Decision Matrix

| Vaziyat | Yechim |
|---------|--------|
| Yangi loyiha | `go mod init` |
| Private repo | `GOPRIVATE` + git SSH |
| Ko'p modul lokal dev | `go.work` |
| CI/CD | Docker multi-stage + cache |
| Offline build | `go mod vendor -mod=vendor` |
| Versiya pinning | `toolchain` directive |
| Dependency tekshirish | `go mod verify` + `govulncheck` |

---

## 19. Summary

- **GOPATH erasi** tarixiy ahamiyatga ega, lekin Go Modules uni to'liq almashtirdi
- **go.mod** — modul nomi, versiya, dependency; **go.sum** — checksum xavfsizlik
- **GOPROXY/GOPRIVATE** — proxy va private modul sozlamalari
- **go.work** — lokal multi-module development, lekin git ga push qilmang
- **Docker multi-stage build** — layer cache bilan samarali CI/CD
- **GOTOOLCHAIN=auto** — avtomatik Go versiya boshqarish (Go 1.21+)
- **CI/CD** — `go mod verify`, `go mod tidy` + `git diff`, cache, lint

---

## 20. What You Can Build

```mermaid
graph TD
    A["Go muhit ilg'or sozlash"] --> B["Private module library"]
    A --> C["Docker multi-stage pipeline"]
    A --> D["CI/CD automation"]
    B --> E["Monorepo + go.work"]
    C --> E
    D --> E
    E --> F["Team toolchain standardizatsiya"]
    F --> G["Enterprise Go platform"]
```

---

## 21. Further Reading

1. **Go Modules Reference** — https://go.dev/ref/mod
2. **Go Toolchains** — https://go.dev/doc/toolchain
3. **Go Blog: Workspace Mode** — https://go.dev/blog/get-familiar-with-workspaces
4. **GOPROXY Protocol** — https://go.dev/ref/mod#goproxy-protocol
5. **Go Supply Chain Security** — https://go.dev/blog/supply-chain

---

## 22. Related Topics

- [Go Modules chuqur — MVS, retract, deprecate](../../03-modules-and-packages/01-go-modules/middle.md)
- [Go Build System — ldflags, tags, cross-compilation](../../02-go-basics/01-go-toolchain/middle.md)
- [Docker bilan Go — production best practices](../../07-deployment/01-docker/middle.md)
- [CI/CD bilan Go — GitHub Actions, GitLab CI](../../07-deployment/02-ci-cd/middle.md)
