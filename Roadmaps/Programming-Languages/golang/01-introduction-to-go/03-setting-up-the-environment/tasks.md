# Setting Up the Environment — Practical Tasks

## Table of Contents

1. [Junior Tasks](#1-junior-tasks)
2. [Middle Tasks](#2-middle-tasks)
3. [Senior Tasks](#3-senior-tasks)
4. [Questions](#4-questions)
5. [Mini Projects](#5-mini-projects)
6. [Challenge](#6-challenge)

---

## 1. Junior Tasks

### Task 1: Go o'rnatish va muhit tekshirish

**Maqsad:** Go'ni o'rnatish va muhitni to'g'ri sozlash.

**Qadamlar:**

1. Go'ni rasmiy saytdan (https://go.dev/dl/) yuklab o'rnating
2. Quyidagi buyruqlarni bajaring va har birining natijasini yozing:

```bash
go version
go env GOROOT
go env GOPATH
go env GOPROXY
go env GOBIN
go env GOCACHE
go env GOMODCACHE
```

3. `$GOPATH/bin` ni PATH ga qo'shing:

```bash
# ~/.bashrc yoki ~/.zshrc ga qo'shing:
export PATH=$PATH:$(go env GOPATH)/bin
source ~/.bashrc  # yoki source ~/.zshrc
```

4. Tekshiring:

```bash
echo $PATH | tr ':' '\n' | grep go
```

**Kutilgan natija:**

```bash
go version
# go version go1.23.4 linux/amd64

go env GOROOT
# /usr/local/go

go env GOPATH
# /home/username/go

go env GOPROXY
# https://proxy.golang.org,direct
```

**Topshirish:** Terminal screenshot yoki natijalar matnini yuboring.

---

### Task 2: Birinchi Go loyihani yaratish va ishga tushirish

**Maqsad:** Go Modules bilan yangi loyiha yaratish, dependency qo'shish, test yozish.

**Qadamlar:**

1. Loyiha yaratish:

```bash
mkdir -p ~/projects/go-greeter && cd ~/projects/go-greeter
go mod init github.com/<your-username>/go-greeter
```

2. `main.go` faylini yarating:

```go
package main

import (
    "fmt"
    "os"

    "github.com/fatih/color"
)

func greet(name string) string {
    if name == "" {
        return "Salom, dunyo!"
    }
    return fmt.Sprintf("Salom, %s!", name)
}

func main() {
    name := ""
    if len(os.Args) > 1 {
        name = os.Args[1]
    }

    msg := greet(name)
    color.Green(msg)
}
```

3. Dependency qo'shish va ishga tushirish:

```bash
go mod tidy
go run main.go
go run main.go Sardor
```

4. `main_test.go` test faylini yarating:

```go
package main

import "testing"

func TestGreet(t *testing.T) {
    tests := []struct {
        name     string
        input    string
        expected string
    }{
        {"empty name", "", "Salom, dunyo!"},
        {"with name", "Go", "Salom, Go!"},
        {"uzbek name", "Sardor", "Salom, Sardor!"},
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            result := greet(tt.input)
            if result != tt.expected {
                t.Errorf("greet(%q) = %q, kutilgan %q", tt.input, result, tt.expected)
            }
        })
    }
}
```

5. Testni ishga tushiring:

```bash
go test -v
```

6. Build qiling:

```bash
go build -o greeter .
./greeter Dunyo
```

**Topshirish:**
- `go.mod` va `go.sum` fayllarining content ini ko'rsating
- Test natijasini ko'rsating
- Binary hajmini (`ls -lh greeter`) ko'rsating

---

### Task 3: VS Code sozlash va go env bilan ishlash

**Maqsad:** VS Code da Go development muhitini sozlash va `go env` buyruqlarini o'rganish.

**Qadamlar:**

1. VS Code da Go extension o'rnating:
   - Extensions → "Go" (Go Team at Google) → Install

2. Go tools o'rnating:
   - `Ctrl+Shift+P` → "Go: Install/Update Tools" → Hammasini tanlang → OK

3. VS Code settings sozlang. `settings.json` ga qo'shing:

```json
{
  "go.useLanguageServer": true,
  "go.lintTool": "golangci-lint",
  "editor.formatOnSave": true,
  "[go]": {
    "editor.defaultFormatter": "golang.go",
    "editor.codeActionsOnSave": {
      "source.organizeImports": "explicit"
    }
  }
}
```

4. `go env` bilan eksperiment:

```bash
# Barcha o'zgaruvchilar
go env

# Muhim o'zgaruvchilar
go env GOROOT GOPATH GOPROXY GOCACHE GOMODCACHE

# JSON formatda
go env -json

# O'zgaruvchini o'zgartirish
go env -w GONOSUMDB=example.com/*

# Tekshirish
go env GONOSUMDB

# Qaytarish
go env -u GONOSUMDB
go env GONOSUMDB  # Bo'sh — default

# go env fayl joylashuvini ko'ring
go env GOENV
cat $(go env GOENV)
```

**Topshirish:**
- VS Code da Go faylni oching, autocomplete va formatting ishlayotganini ko'rsating (screenshot)
- `go env` buyruqlari natijalarini ko'rsating

---

### Task 4: Loyiha strukturasi va go mod buyruqlari

**Maqsad:** Loyiha strukturasini tushunish va go mod buyruqlari bilan ishlash.

**Qadamlar:**

1. Quyidagi strukturani yarating:

```bash
mkdir -p ~/projects/go-calculator && cd ~/projects/go-calculator
go mod init github.com/<your-username>/go-calculator
```

2. `calc/calc.go`:

```go
package calc

import "errors"

var ErrDivisionByZero = errors.New("nolga bo'lish mumkin emas")

func Add(a, b float64) float64      { return a + b }
func Subtract(a, b float64) float64 { return a - b }
func Multiply(a, b float64) float64 { return a * b }

func Divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, ErrDivisionByZero
    }
    return a / b, nil
}
```

3. `calc/calc_test.go`:

```go
package calc

import (
    "math"
    "testing"
)

func TestAdd(t *testing.T) {
    if got := Add(2, 3); got != 5 {
        t.Errorf("Add(2, 3) = %f, kutilgan 5", got)
    }
}

func TestDivide(t *testing.T) {
    result, err := Divide(10, 2)
    if err != nil {
        t.Fatalf("kutilmagan xato: %v", err)
    }
    if math.Abs(result-5) > 0.001 {
        t.Errorf("Divide(10, 2) = %f, kutilgan 5", result)
    }
}

func TestDivideByZero(t *testing.T) {
    _, err := Divide(10, 0)
    if err == nil {
        t.Fatal("xato kutilgan edi, lekin yo'q")
    }
    if err != ErrDivisionByZero {
        t.Errorf("kutilgan ErrDivisionByZero, olindi: %v", err)
    }
}
```

4. `main.go`:

```go
package main

import (
    "fmt"

    "github.com/<your-username>/go-calculator/calc"
)

func main() {
    fmt.Println("2 + 3 =", calc.Add(2, 3))
    fmt.Println("10 - 4 =", calc.Subtract(10, 4))
    fmt.Println("3 * 7 =", calc.Multiply(3, 7))

    result, err := calc.Divide(15, 3)
    if err != nil {
        fmt.Println("Xato:", err)
    } else {
        fmt.Println("15 / 3 =", result)
    }

    _, err = calc.Divide(10, 0)
    if err != nil {
        fmt.Println("Xato:", err)
    }
}
```

5. go mod buyruqlarini sinab ko'ring:

```bash
go run main.go
go test ./...
go test -v ./calc/
go build -o calculator .
go vet ./...
```

**Topshirish:**
- `go test -v ./...` natijasi
- `go run main.go` natijasi
- `go.mod` fayli content i

---

## 2. Middle Tasks

### Task 1: Multi-module loyiha va go.work

**Maqsad:** Ko'p modulli loyiha yaratish, go.work bilan ishlash, dependency boshqarish.

**Qadamlar:**

1. Loyiha strukturasini yarating:

```bash
mkdir -p ~/projects/go-workspace && cd ~/projects/go-workspace

# Shared library
mkdir -p shared && cd shared
go mod init github.com/<user>/go-workspace/shared
cd ..

# API service
mkdir -p api && cd api
go mod init github.com/<user>/go-workspace/api
cd ..

# Worker service
mkdir -p worker && cd worker
go mod init github.com/<user>/go-workspace/worker
cd ..
```

2. `shared/greeting.go`:

```go
package shared

import (
    "fmt"
    "strings"
    "time"
)

type Greeting struct {
    Message   string
    CreatedAt time.Time
}

func NewGreeting(name string) Greeting {
    name = strings.TrimSpace(name)
    if name == "" {
        name = "dunyo"
    }
    return Greeting{
        Message:   fmt.Sprintf("Salom, %s!", name),
        CreatedAt: time.Now(),
    }
}

func (g Greeting) String() string {
    return fmt.Sprintf("[%s] %s", g.CreatedAt.Format("15:04:05"), g.Message)
}
```

3. `shared/greeting_test.go`:

```go
package shared

import "testing"

func TestNewGreeting(t *testing.T) {
    g := NewGreeting("Go")
    if g.Message != "Salom, Go!" {
        t.Errorf("kutilgan 'Salom, Go!', olindi '%s'", g.Message)
    }
}

func TestNewGreetingEmpty(t *testing.T) {
    g := NewGreeting("")
    if g.Message != "Salom, dunyo!" {
        t.Errorf("kutilgan 'Salom, dunyo!', olindi '%s'", g.Message)
    }
}
```

4. `api/main.go`:

```go
package main

import (
    "encoding/json"
    "fmt"
    "log"
    "net/http"

    "github.com/<user>/go-workspace/shared"
)

func greetHandler(w http.ResponseWriter, r *http.Request) {
    name := r.URL.Query().Get("name")
    greeting := shared.NewGreeting(name)

    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(map[string]string{
        "message":    greeting.Message,
        "created_at": greeting.CreatedAt.String(),
    })
}

func main() {
    http.HandleFunc("/greet", greetHandler)
    fmt.Println("API server: http://localhost:8080")
    log.Fatal(http.ListenAndServe(":8080", nil))
}
```

5. `worker/main.go`:

```go
package main

import (
    "fmt"
    "time"

    "github.com/<user>/go-workspace/shared"
)

func main() {
    names := []string{"Go", "Rust", "Python", ""}
    for _, name := range names {
        greeting := shared.NewGreeting(name)
        fmt.Println(greeting)
        time.Sleep(500 * time.Millisecond)
    }
}
```

6. Workspace yaratish va ishga tushirish:

```bash
# Root papkada:
cd ~/projects/go-workspace
go work init ./shared ./api ./worker

# go.work tekshirish:
cat go.work

# Shared library testlari:
go test ./shared/...

# Worker ishga tushirish:
go run ./worker

# API ishga tushirish (boshqa terminal da):
go run ./api
# curl http://localhost:8080/greet?name=Sardor
```

7. GOWORK=off bilan tekshirish:

```bash
# Har bir modul mustaqil build bo'lishi kerak (CI uchun):
cd api
GOWORK=off go build ./...
# Xato beradi! Chunki shared modul publish qilinmagan

# Yechim: replace directive (CI uchun emas, faqat lokal)
# Yoki: shared ni alohida repo/tag bilan publish qilish
```

**Topshirish:**
- `go.work` fayli content i
- `curl` natijasi
- `go test ./shared/...` natijasi
- GOWORK=off xatosini va yechimini tushuntiring

---

### Task 2: Docker bilan Go development muhiti

**Maqsad:** Multi-stage Docker build, cache optimizatsiya, development va production Dockerfile.

**Qadamlar:**

1. Oddiy Go web server yarating:

```bash
mkdir -p ~/projects/go-docker && cd ~/projects/go-docker
go mod init github.com/<user>/go-docker
```

2. `main.go`:

```go
package main

import (
    "encoding/json"
    "fmt"
    "log"
    "net/http"
    "os"
    "runtime/debug"
    "time"
)

var (
    version = "dev"
    commit  = "unknown"
)

type HealthResponse struct {
    Status    string `json:"status"`
    Version   string `json:"version"`
    Commit    string `json:"commit"`
    GoVersion string `json:"go_version"`
    Uptime    string `json:"uptime"`
}

var startTime = time.Now()

func healthHandler(w http.ResponseWriter, r *http.Request) {
    goVersion := "unknown"
    if info, ok := debug.ReadBuildInfo(); ok {
        goVersion = info.GoVersion
    }

    resp := HealthResponse{
        Status:    "ok",
        Version:   version,
        Commit:    commit,
        GoVersion: goVersion,
        Uptime:    time.Since(startTime).String(),
    }

    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(resp)
}

func main() {
    port := os.Getenv("PORT")
    if port == "" {
        port = "8080"
    }

    http.HandleFunc("/health", healthHandler)
    http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
        fmt.Fprintf(w, "Salom, Go Docker! Version: %s\n", version)
    })

    fmt.Printf("Server ishga tushdi: :%s\n", port)
    log.Fatal(http.ListenAndServe(":"+port, nil))
}
```

3. `Dockerfile` (production):

```dockerfile
# Stage 1: Build
FROM golang:1.23.4-alpine AS builder

WORKDIR /app

# Dependency cache layer
COPY go.mod go.sum ./
RUN go mod download && go mod verify

# Build
COPY . .
ARG VERSION=dev
ARG COMMIT=unknown
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -trimpath \
    -ldflags="-w -s -X main.version=${VERSION} -X main.commit=${COMMIT}" \
    -o /app/server .

# Stage 2: Runtime
FROM alpine:3.19
RUN apk --no-cache add ca-certificates tzdata
COPY --from=builder /app/server /server
EXPOSE 8080
ENTRYPOINT ["/server"]
```

4. `.dockerignore`:

```
.git
*.md
Dockerfile*
docker-compose*
.env
bin/
tmp/
```

5. Build va ishga tushirish:

```bash
# Build
docker build \
    --build-arg VERSION=$(git describe --tags --always 2>/dev/null || echo "dev") \
    --build-arg COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown") \
    -t go-docker:latest .

# Ishga tushirish
docker run -p 8080:8080 go-docker:latest

# Tekshirish
curl http://localhost:8080/health | jq .

# Image hajmini tekshirish
docker images go-docker
```

6. Image hajmlarini solishtiring:

```bash
# golang base image bilan:
# FROM golang:1.23.4
# ... go build ...
# Hajm: ~1.2 GB

# Multi-stage alpine bilan:
# Hajm: ~15 MB

# scratch bilan (eng kichik):
# FROM scratch
# Hajm: ~8 MB
```

**Topshirish:**
- `docker images` natijasi (image hajmi)
- `curl /health` natijasi (JSON)
- Multi-stage build ning afzalliklarini tushuntiring

---

### Task 3: CI/CD pipeline yaratish

**Maqsad:** GitHub Actions bilan Go loyiha uchun CI/CD pipeline yaratish.

**Qadamlar:**

1. Oldingi task dagi loyihani GitHub ga push qiling

2. `.github/workflows/ci.yml` yarating:

```yaml
name: Go CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        go-version: ['1.22', '1.23']

    steps:
      - uses: actions/checkout@v4

      - name: Set up Go ${{ matrix.go-version }}
        uses: actions/setup-go@v5
        with:
          go-version: ${{ matrix.go-version }}
          cache: true

      - name: Verify dependencies
        run: |
          go mod verify
          go mod tidy
          git diff --exit-code go.mod go.sum

      - name: Vet
        run: go vet ./...

      - name: Test
        run: go test -race -coverprofile=coverage.out -v ./...

      - name: Coverage
        run: go tool cover -func=coverage.out

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-go@v5
        with:
          go-version: '1.23'
          cache: true

      - name: Build
        run: |
          CGO_ENABLED=0 go build \
            -trimpath \
            -ldflags="-w -s -X main.version=${{ github.ref_name }} -X main.commit=${{ github.sha }}" \
            -o bin/server .

      - name: Upload artifact
        uses: actions/upload-artifact@v4
        with:
          name: server
          path: bin/server
```

3. Push qiling va GitHub Actions natijasini kuzating

**Topshirish:**
- GitHub Actions natijasi (screenshot yoki link)
- Pipeline qancha vaqt ishlaganini yozing
- Cache bilan va cache siz farqni tushuntiring

---

## 3. Senior Tasks

### Task 1: Monorepo muhit yaratish va CI bilan integratsiya

**Maqsad:** Multi-module monorepo, go.work, Makefile, affected module detection, CI pipeline.

**Qadamlar:**

1. Monorepo strukturasini yarating:

```
go-monorepo/
├── go.work              # Lokal dev
├── .gitignore           # go.work, bin/, etc.
├── Makefile             # Orchestration
├── libs/
│   ├── auth/
│   │   ├── go.mod       # github.com/<user>/go-monorepo/libs/auth
│   │   ├── auth.go
│   │   └── auth_test.go
│   └── config/
│       ├── go.mod       # github.com/<user>/go-monorepo/libs/config
│       ├── config.go
│       └── config_test.go
├── services/
│   ├── api/
│   │   ├── go.mod       # github.com/<user>/go-monorepo/services/api
│   │   ├── cmd/
│   │   │   └── server/
│   │   │       └── main.go
│   │   └── internal/
│   │       └── handler/
│   │           └── handler.go
│   └── worker/
│       ├── go.mod       # github.com/<user>/go-monorepo/services/worker
│       └── cmd/
│           └── worker/
│               └── main.go
└── tools/
    ├── go.mod
    └── tools.go
```

2. Makefile yarating:

```makefile
MODULES := $(shell find . -name 'go.mod' -not -path './tools/*' -exec dirname {} \;)

.PHONY: tidy test lint build-all changed

tidy:
	@for mod in $(MODULES); do \
		echo "==> tidy: $$mod"; \
		(cd $$mod && GOWORK=off go mod tidy); \
	done

test:
	@for mod in $(MODULES); do \
		echo "==> test: $$mod"; \
		(cd $$mod && go test -race -v ./...); \
	done

lint:
	@for mod in $(MODULES); do \
		echo "==> vet: $$mod"; \
		(cd $$mod && go vet ./...); \
	done

build-all:
	@for svc in $(shell ls services/ 2>/dev/null); do \
		echo "==> build: services/$$svc"; \
		(cd services/$$svc && CGO_ENABLED=0 go build -trimpath -ldflags="-w -s" -o ../../bin/$$svc ./cmd/...); \
	done

changed:
	@git diff --name-only HEAD~1 2>/dev/null | \
		while read f; do \
			dir=$$(dirname "$$f"); \
			while [ "$$dir" != "." ]; do \
				if [ -f "$$dir/go.mod" ]; then echo "$$dir"; break; fi; \
				dir=$$(dirname "$$dir"); \
			done; \
		done | sort -u
```

3. Barcha modullarni implement qiling (auth, config, api, worker)

4. go.work yaratish va test:

```bash
go work init $(find . -name 'go.mod' -not -path './tools/*' -exec dirname {} \;)
make test
make build-all
make changed
```

5. .gitignore yarating:

```gitignore
go.work
go.work.sum
bin/
*.exe
```

6. CI pipeline (affected modules only):

```yaml
# .github/workflows/ci.yml
name: Monorepo CI

on: [push, pull_request]

jobs:
  detect:
    runs-on: ubuntu-latest
    outputs:
      modules: ${{ steps.changes.outputs.modules }}
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - id: changes
        run: |
          if [ "${{ github.event_name }}" = "pull_request" ]; then
            BASE=${{ github.event.pull_request.base.sha }}
          else
            BASE=${{ github.event.before }}
          fi
          MODULES=$(git diff --name-only ${BASE} ${{ github.sha }} | \
            while read f; do
              dir=$(dirname "$f")
              while [ "$dir" != "." ]; do
                if [ -f "$dir/go.mod" ]; then echo "$dir"; break; fi
                dir=$(dirname "$dir")
              done
            done | sort -u | jq -R -s -c 'split("\n") | map(select(length > 0))')
          echo "modules=$MODULES" >> $GITHUB_OUTPUT

  test:
    needs: detect
    if: needs.detect.outputs.modules != '[]' && needs.detect.outputs.modules != 'null'
    runs-on: ubuntu-latest
    strategy:
      matrix:
        module: ${{ fromJson(needs.detect.outputs.modules) }}
      fail-fast: false
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-go@v5
        with:
          go-version: '1.23'
          cache: true
      - run: |
          cd ${{ matrix.module }}
          GOWORK=off go test -race -v ./...
```

**Topshirish:**
- Monorepo strukturasi (`tree` output)
- `make test` natijasi
- `make changed` natijasi (commit qilgandan keyin)
- CI pipeline natijasi (agar GitHub da bo'lsa)

---

### Task 2: Private proxy va offline build muhiti

**Maqsad:** Athens proxy o'rnatish, private module sozlash, offline build test.

**Qadamlar:**

1. Athens ni Docker da ishga tushiring:

```yaml
# docker-compose.yml
version: "3.9"
services:
  athens:
    image: gomods/athens:latest
    ports:
      - "3000:3000"
    environment:
      - ATHENS_DISK_STORAGE_ROOT=/var/lib/athens
      - ATHENS_STORAGE_TYPE=disk
    volumes:
      - athens-data:/var/lib/athens
volumes:
  athens-data:
```

```bash
docker compose up -d
```

2. Athens ni GOPROXY sifatida sozlang:

```bash
go env -w GOPROXY=http://localhost:3000,https://proxy.golang.org,direct
```

3. Loyiha yaratib, Athens orqali dependency yuklang:

```bash
mkdir -p ~/projects/go-athens-test && cd ~/projects/go-athens-test
go mod init github.com/<user>/go-athens-test
go get github.com/gin-gonic/gin@latest

# Athens loglarini tekshiring:
docker compose logs athens | grep "gin"
```

4. Offline build test:

```bash
# 1. Vendor yarating
go mod vendor

# 2. Internet o'chiring (GOPROXY=off)
GOPROXY=off go build -mod=vendor ./...

# 3. Module cache test
go clean -modcache
GOPROXY=http://localhost:3000,off go build ./...
# Athens cache dan ishlashi kerak!
```

5. Athens API ni sinab ko'ring:

```bash
# Versiyalar ro'yxati
curl http://localhost:3000/github.com/gin-gonic/gin/@v/list

# Versiya info
curl http://localhost:3000/github.com/gin-gonic/gin/@v/v1.9.1.info

# go.mod
curl http://localhost:3000/github.com/gin-gonic/gin/@v/v1.9.1.mod
```

**Topshirish:**
- Athens ishga tushgan screenshot
- `curl` natijalari
- Offline build muvaffaqiyatli ekanligini ko'rsating
- Athens afzalliklarini tushuntiring

---

### Task 3: Reproducible builds va security audit

**Maqsad:** Reproducible build yaratish, build info tekshirish, vulnerability scanning.

**Qadamlar:**

1. Loyiha yarating va build info embed qiling:

```go
// main.go
package main

import (
    "encoding/json"
    "fmt"
    "os"
    "runtime/debug"
)

var (
    version = "dev"
    commit  = "unknown"
    date    = "unknown"
)

func main() {
    if len(os.Args) > 1 && os.Args[1] == "version" {
        info := map[string]interface{}{
            "version": version,
            "commit":  commit,
            "date":    date,
        }

        if bi, ok := debug.ReadBuildInfo(); ok {
            info["go_version"] = bi.GoVersion
            settings := map[string]string{}
            for _, s := range bi.Settings {
                settings[s.Key] = s.Value
            }
            info["build_settings"] = settings
        }

        enc := json.NewEncoder(os.Stdout)
        enc.SetIndent("", "  ")
        enc.Encode(info)
        return
    }

    fmt.Printf("App v%s (commit: %s, date: %s)\n", version, commit, date)
}
```

2. Reproducible build script:

```bash
#!/bin/bash
# build.sh
set -euo pipefail

VERSION=$(git describe --tags --always --dirty 2>/dev/null || echo "dev")
COMMIT=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown")
DATE=$(date -u +%Y-%m-%dT%H:%M:%SZ)

echo "Building version: $VERSION, commit: $COMMIT"

CGO_ENABLED=0 go build -trimpath \
    -ldflags="-w -s \
        -X main.version=${VERSION} \
        -X main.commit=${COMMIT} \
        -X main.date=${DATE}" \
    -o bin/app .

echo "Build complete: bin/app"
echo "SHA256: $(sha256sum bin/app)"
```

3. Reproducibility test:

```bash
# Ikki marta build (DATE ni statik qiling!)
CGO_ENABLED=0 go build -trimpath -ldflags="-w -s -X main.date=2024-01-01" -o bin/app1 .
CGO_ENABLED=0 go build -trimpath -ldflags="-w -s -X main.date=2024-01-01" -o bin/app2 .

# Hash solishtirish
sha256sum bin/app1 bin/app2
# Bir xil bo'lishi kerak!

# Build info tekshirish
go version -m bin/app1
./bin/app1 version | jq .
```

4. Security audit:

```bash
# govulncheck o'rnatish va ishga tushirish
go install golang.org/x/vuln/cmd/govulncheck@latest
govulncheck ./...

# go mod verify
go mod verify

# Dependency ro'yxati
go list -m all

# Dependency grafi
go mod graph
```

**Topshirish:**
- Ikki build ning SHA256 hash lari bir xil ekanligini ko'rsating
- `./app version` natijasi (JSON)
- `govulncheck` natijasi
- Reproducible build uchun qanday shartlar kerakligini tushuntiring

---

## 4. Questions

### Nazariy savollar

**1.** GOPATH va GOROOT o'rtasidagi farqni misol bilan tushuntiring. GOPATH ning qaysi papkalari qanday maqsadda ishlatiladi?

**2.** Go Modules tizimida MVS (Minimal Version Selection) qanday ishlaydi? NPM ning dependency resolution dan qanday farq qiladi? Misol keltiring.

**3.** `go.sum` faylidagi hash qanday hisoblanadi? `h1:` prefiksi nimani anglatadi? Nima uchun ba'zan bitta dependency uchun ikki qator bo'ladi?

**4.** GOPROXY da `,` (vergul) va `|` (pipe) separator lar o'rtasidagi farqni tushuntiring. Qachon qaysi birini ishlatish kerak?

**5.** `go env -w` bilan o'rnatilgan qiymat va `export` bilan o'rnatilgan qiymat conflict qilsa, qaysi biri ustun? Nima uchun? Environment variable precedence tartibini to'liq tushuntiring.

**6.** `go mod tidy`, `go mod download` va `go mod verify` o'rtasidagi farqlarni tushuntiring. CI/CD da qaysi birini va qaysi tartibda ishlatish kerak?

**7.** Docker multi-stage build da `COPY go.mod go.sum ./` ni alohida layer qilish nima uchun muhim? Layer caching qanday ishlaydi?

**8.** `replace` directive publish qilingan modulda ishlaydi mi? Nima uchun? `exclude` va `retract` dan qanday farqi bor?

**9.** GOTOOLCHAIN=auto bo'lganda go.mod da `go 1.23` va `toolchain go1.23.4` o'rtasidagi farq nima? Ikkalasi ham kerakmi?

**10.** Reproducible build nima? Qanday shartlar kerak? `-trimpath` flag nimaga kerak?

---

## 5. Mini Projects

### Mini Project 1: Go Environment Inspector CLI

**Maqsad:** `go env` ma'lumotlarini chiroyli formatlangan holda ko'rsatadigan CLI tool yarating.

**Talablar:**
1. Barcha muhim Go env variable larni guruhlangan holda ko'rsatish (Paths, Modules, Build, etc.)
2. `-json` flag bilan JSON output
3. `-check` flag bilan muhitni tekshirish (PATH da go bor mi, GOPATH/bin PATH da bormi, etc.)
4. Rangli output (terminal uchun)
5. Cross-platform ishlashi kerak

**Texnik talablar:**
- `os/exec` bilan `go env -json` ishga tushirish
- `encoding/json` bilan parse qilish
- `flag` paketi bilan CLI argument lar
- Test coverage >= 70%

**Kutilgan natija:**

```bash
$ go-env-inspector
╔══════════════════════════════════════╗
║     Go Environment Inspector        ║
╚══════════════════════════════════════╝

📁 Paths:
  GOROOT:     /usr/local/go
  GOPATH:     /home/user/go
  GOBIN:      /home/user/go/bin
  GOCACHE:    /home/user/.cache/go-build
  GOMODCACHE: /home/user/go/pkg/mod

📦 Modules:
  GO111MODULE: on
  GOPROXY:     https://proxy.golang.org,direct
  GOPRIVATE:   github.com/company/*
  GONOSUMDB:
  GONOSUMCHECK:

🔧 Build:
  GOOS:       linux
  GOARCH:     amd64
  CGO_ENABLED: 1
  GOFLAGS:

📌 Version:
  Go:          go1.23.4
  GOTOOLCHAIN: auto

$ go-env-inspector -check
✅ Go installed: go1.23.4
✅ GOPATH/bin in PATH
✅ GOPROXY configured
⚠️  GOPRIVATE not set (set if using private repos)
✅ go.mod found in current directory
```

---

### Mini Project 2: Go Module Dependency Visualizer

**Maqsad:** `go mod graph` natijasini vizualizatsiya qiladigan tool yarating.

**Talablar:**
1. `go mod graph` output ni parse qilish
2. Dependency tree ni terminal da chiroyli ko'rsatish
3. Direct va indirect dependency larni ajratish
4. Dependency depth ni ko'rsatish
5. `-format=mermaid` flag bilan Mermaid diagram output

**Texnik talablar:**
- `os/exec` bilan `go mod graph` ishga tushirish
- Grafi ma'lumot tuzilmasida saqlash
- BFS/DFS bilan tree traversal
- Test coverage >= 60%

**Kutilgan natija:**

```bash
$ go-dep-viz
github.com/user/myproject
├── github.com/gin-gonic/gin@v1.9.1 (direct)
│   ├── github.com/bytedance/sonic@v1.10.2
│   ├── github.com/gin-contrib/sse@v0.1.0
│   └── github.com/go-playground/validator/v10@v10.16.0
│       └── github.com/go-playground/universal-translator@v0.18.1
├── go.uber.org/zap@v1.26.0 (direct)
│   └── go.uber.org/multierr@v1.11.0
└── github.com/jackc/pgx/v5@v5.5.1 (direct)

Total: 3 direct, 15 indirect dependencies
Max depth: 4

$ go-dep-viz -format=mermaid
graph TD
    A["myproject"] --> B["gin@v1.9.1"]
    A --> C["zap@v1.26.0"]
    B --> D["sonic@v1.10.2"]
    ...
```

---

## 6. Challenge

### Enterprise Go Environment Platform

**Maqsad:** Kichik hajmdagi "enterprise Go environment management" platformasini yarating.

**Tavsif:** Siz jamoa uchun Go muhitni standartlashtiruvchi tool yozishingiz kerak. Bu tool quyidagi vazifalarni bajaradi:

**Talablar:**

1. **Environment Checker (`env-check`):**
   - Go versiya tekshirish (go.mod dagi talab bilan solishtirish)
   - Barcha kerakli tool lar o'rnatilganligini tekshirish (gopls, dlv, golangci-lint)
   - GOPROXY, GOPRIVATE sozlamalarini tekshirish
   - PATH sozlamalari to'g'riligini tekshirish
   - Natija: JSON yoki terminal report

2. **Module Auditor (`mod-audit`):**
   - `go mod verify` ishga tushirish
   - `govulncheck` ishga tushirish (agar o'rnatilgan bo'lsa)
   - Direct vs indirect dependency statistikasi
   - go.mod dagi `replace` directive ogohlantirish
   - go.sum mavjudligini tekshirish

3. **Setup Automator (`setup`):**
   - Config fayldan kerakli tool larni o'rnatish
   - GOPROXY, GOPRIVATE sozlash
   - Git hooks o'rnatish
   - VS Code settings generate qilish

4. **Config fayl formati (`.goenv.yaml`):**

```yaml
# .goenv.yaml
go_version: "1.23.4"
gotoolchain: "auto"
goproxy: "http://athens.internal:3000,https://proxy.golang.org,direct"
goprivate: "github.com/company/*"

tools:
  - name: golangci-lint
    package: github.com/golangci/golangci-lint/cmd/golangci-lint
    version: latest
  - name: air
    package: github.com/air-verse/air
    version: latest
  - name: govulncheck
    package: golang.org/x/vuln/cmd/govulncheck
    version: latest

hooks:
  pre-commit:
    - go vet ./...
    - go test -short ./...

vscode:
  format_on_save: true
  lint_tool: golangci-lint
```

**Texnik talablar:**
- CLI: `cobra` yoki `flag` paketi
- Config: `gopkg.in/yaml.v3`
- Test coverage >= 60%
- `go vet` va `golangci-lint` dan xato yo'q
- Cross-platform (Linux + macOS)

**Ishga tushirish:**

```bash
# Muhitni tekshirish
go-env-platform env-check

# Module audit
go-env-platform mod-audit

# Muhitni sozlash
go-env-platform setup

# Barcha tekshiruvlar
go-env-platform check-all
```

**Baholash mezonlari:**
- Funksional to'g'rilik (40%)
- Kod sifati va tuzilishi (20%)
- Test coverage (15%)
- Error handling (15%)
- Documentation va UX (10%)

**Qo'shimcha ball uchun:**
- Mermaid diagram bilan dependency vizualizatsiya
- GitHub Actions integration (CI da ishga tushirish)
- Team config sharing (centralized .goenv.yaml)
