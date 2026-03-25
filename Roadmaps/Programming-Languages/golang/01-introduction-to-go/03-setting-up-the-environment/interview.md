# Setting Up the Environment — Interview Questions

## Table of Contents

1. [Junior Level](#1-junior-level)
2. [Middle Level](#2-middle-level)
3. [Senior Level](#3-senior-level)
4. [Scenario-Based](#4-scenario-based)
5. [FAQ](#5-faq)

---

## 1. Junior Level

### 1.1. GOPATH va GOROOT o'rtasidagi farqni tushuntiring.

<details>
<summary>Javob</summary>

**GOROOT** — Go kompilyatori va standart kutubxonalar o'rnatilgan papka. Masalan, `/usr/local/go`. Bu ichida:
- `bin/` — `go`, `gofmt` binary lari
- `src/` — standart kutubxona manba kodlari
- `pkg/` — kompilyatsiya qilingan standart paketlar

**GOPATH** — foydalanuvchining ishchi papkasi. Default: `~/go`. Bu ichida:
- `bin/` — `go install` bilan o'rnatilgan binary fayllar
- `pkg/mod/` — yuklab olingan module cache

**Asosiy farq:** GOROOT = Go tilining o'zi (kompilyator, standart kutubxona). GOPATH = foydalanuvchining muhiti (dependency cache, o'rnatilgan tool lar).

```bash
go env GOROOT  # /usr/local/go
go env GOPATH  # /home/user/go
```
</details>

### 1.2. Go Modules nima va uni qanday boshlash mumkin?

<details>
<summary>Javob</summary>

Go Modules — Go'ning rasmiy dependency management tizimi. Go 1.11 da kiritilgan, Go 1.16 dan boshlab default.

Boshlash uchun:
```bash
mkdir myproject && cd myproject
go mod init github.com/username/myproject
```

Bu `go.mod` faylini yaratadi:
```go
module github.com/username/myproject

go 1.23.4
```

Dependency qo'shish:
```bash
go get github.com/gin-gonic/gin@v1.9.1
```

`go mod tidy` — keraksiz dependency larni o'chiradi, keraklilarini qo'shadi.
</details>

### 1.3. `go run`, `go build` va `go install` o'rtasidagi farqlarni ayting.

<details>
<summary>Javob</summary>

| Buyruq | Natija | Ishlatish holati |
|--------|--------|-----------------|
| `go run main.go` | Kompilyatsiya + ishga tushirish. Binary saqlanmaydi | Development, tez sinash |
| `go build ./...` | Binary yaratish (joriy papkada) | Production build, deploy |
| `go install pkg@ver` | Binary yaratish → `GOPATH/bin` ga joylashtirish | Tool o'rnatish |

```bash
go run main.go           # Ishga tushiradi, binary o'chiriladi
go build -o server .     # ./server binary yaratadi
go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest  # ~/go/bin/ ga o'rnatadi
```
</details>

### 1.4. `go.sum` fayli nima uchun kerak? Uni git ga commit qilish kerakmi?

<details>
<summary>Javob</summary>

`go.sum` — har bir dependency ning kriptografik hash (checksum) larini saqlaydi. Bu fayl supply chain attack lardan himoya qiladi:

```
github.com/gin-gonic/gin v1.9.1 h1:4idEAncQnU5cB7BeOkPtxjfCSye0AAm1R0RVIqFPSsg=
github.com/gin-gonic/gin v1.9.1/go.mod h1:hPrL/0KcuqOSEYJMaBK2NRtea7So7lEFo2/aBJSBJa4=
```

**Ha, doimo git ga commit qiling!** Sabablari:
1. Jamoa a'zolari bir xil dependency versiyalarini ishlatishini ta'minlaydi
2. Agar dependency o'zgartirilsa (hacker tomonidan), hash mos kelmaydi va xato beradi
3. CI/CD da reproducible build ta'minlaydi

```bash
go mod verify  # Barcha dependency hash larini tekshiradi
```
</details>

### 1.5. `go env` buyrug'i nima qiladi? Muhit o'zgaruvchisini qanday o'zgartirish va qaytarish mumkin?

<details>
<summary>Javob</summary>

`go env` — Go muhit o'zgaruvchilarini ko'rsatadi va boshqaradi.

```bash
# Ko'rish
go env              # Barcha o'zgaruvchilar
go env GOPATH       # Bitta o'zgaruvchi
go env -json        # JSON formatda

# O'zgartirish
go env -w GOPROXY=https://goproxy.io,direct

# Qaytarish (default ga)
go env -u GOPROXY

# O'zgartirilgan qiymatlar saqlanadi:
# Linux: ~/.config/go/env
# macOS: ~/Library/Application Support/go/env
```

Muhim: Shell environment variable (`export GOPROXY=...`) `go env -w` dan ustun turadi.
</details>

### 1.6. VS Code da Go development uchun qanday extension va tools kerak?

<details>
<summary>Javob</summary>

1. **Go extension** — "Go Team at Google" tomonidan (VS Code Marketplace)
2. Extension o'rnatgandan keyin: `Ctrl+Shift+P` → "Go: Install/Update Tools" → Hammasini tanlang

Asosiy tool lar:
- **gopls** — Language Server (autocomplete, go to definition, refactoring)
- **dlv** — Debugger (breakpoint, step, variable inspect)
- **golangci-lint** — Linter (kod sifati tekshirish)
- **goimports** — Auto import va format

```json
// settings.json
{
  "go.useLanguageServer": true,
  "editor.formatOnSave": true,
  "[go]": {
    "editor.defaultFormatter": "golang.go"
  }
}
```
</details>

### 1.7. GOPROXY nima? Default qiymati nima?

<details>
<summary>Javob</summary>

GOPROXY — Go paketlarni yuklab olish uchun proxy server manzili.

```bash
go env GOPROXY
# https://proxy.golang.org,direct
```

Bu degani:
1. Avval `proxy.golang.org` (Google ning proxy si) dan qidiradi
2. Agar topilmasa — `direct` (to'g'ridan-to'g'ri git clone)

Proxy afzalliklari:
- **Tezlik** — CDN orqali tez yuklash
- **Xavfsizlik** — checksum tekshirish (sum.golang.org)
- **Mavjudlik** — paket GitHub dan o'chirilsa ham proxy da saqlanadi
</details>

---

## 2. Middle Level

### 2.1. GOPATH erasi va Go Modules o'rtasidagi asosiy farqlarni tushuntiring. Nima uchun Modules kiritildi?

<details>
<summary>Javob</summary>

**GOPATH erasi (Go 1.0 — Go 1.10) muammolari:**
1. **Versiya boshqaruvi yo'q** — bitta dependency ning faqat bitta versiyasi
2. **Joylashuv majburiyligi** — barcha kod `$GOPATH/src` ichida
3. **Qayta tiklanmaydigan build** — `go get` har safar oxirgi versiyani oladi
4. **Diamond dependency** — A→B v1, A→C→B v2 = conflict

**Go Modules yechimi:**
1. **MVS algoritmi** — har bir dependency versiyalangan, minimal qoniqarli versiya
2. **Istalgan papka** — `go mod init` bilan istalgan joyda loyiha
3. **go.sum** — kriptografik hash bilan reproducible build
4. **Semantic versioning** — v1, v2 alohida import path

```
# GOPATH era:
$GOPATH/src/github.com/user/project  # Majburiy joylashuv

# Modules era:
~/projects/myapp/  # Istalgan papka
├── go.mod         # Modul ta'rifi + dependency versiyalari
└── go.sum         # Checksum xavfsizlik
```
</details>

### 2.2. GOPRIVATE, GONOPROXY, GONOSUMDB, GONOSUMCHECK o'rtasidagi farqlarni tushuntiring.

<details>
<summary>Javob</summary>

| O'zgaruvchi | Tavsif | Ta'siri |
|-------------|--------|---------|
| **GOPRIVATE** | Private modul pattern | GONOPROXY + GONOSUMDB + GONOSUMCHECK ni o'rnatadi |
| **GONOPROXY** | Proxy ishlatilmaydigan modullar | To'g'ridan-to'g'ri git clone |
| **GONOSUMDB** | sum.golang.org ga so'rov yuborilmaydigan | Lekin go.sum dagi hash TEKSHIRILADI |
| **GONOSUMCHECK** | Checksum umuman tekshirilmaydigan | Xavfli! Hash tekshiruvi yo'q |

```bash
# Eng oddiy: GOPRIVATE o'rnatish (barchasini o'rnatadi)
go env -w GOPRIVATE=github.com/company/*

# Nozik sozlash:
go env -w GONOPROXY=github.com/company/*        # Proxy yo'q
go env -w GONOSUMDB=github.com/company/*         # Sum DB so'rovi yo'q
go env -w GONOSUMCHECK=                          # Checksum TEKSHIRILSIN
```

Amalda `GOPRIVATE` yetarli. Nozik sozlash faqat maxsus hollarda kerak.
</details>

### 2.3. `go.work` nima? Qachon ishlatish kerak va qachon ishlatmaslik kerak?

<details>
<summary>Javob</summary>

`go.work` — workspace mode. Bir nechta modulni lokal development da birga ishlatish uchun.

```go
// go.work
go 1.23.4
use (
    ./api
    ./lib
    ./shared
)
```

**Qachon ishlatish kerak:**
- Ko'p modulli monorepo da lokal development
- Bir vaqtda ikki modulni tahrirlash kerak bo'lganda
- `replace` directive o'rniga

**Qachon ishlatMASLIK kerak:**
- CI/CD da (go.work `.gitignore` da bo'lishi kerak)
- Production build da
- Bitta modulli loyihada

**Muhim:** `go.work` va `go.work.sum` ni hech qachon git ga commit qilmang!

```bash
GOWORK=off go build ./...  # CI da workspace o'chirish
```
</details>

### 2.4. Docker multi-stage build da Go muhitni qanday optimizatsiya qilasiz?

<details>
<summary>Javob</summary>

```dockerfile
# Stage 1: Build
FROM golang:1.23.4-alpine AS builder
WORKDIR /app

# 1. Dependency cache layer
COPY go.mod go.sum ./
RUN go mod download && go mod verify

# 2. Source code va build
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build \
    -trimpath -ldflags="-w -s" \
    -o /app/server ./cmd/server

# Stage 2: Minimal runtime
FROM alpine:3.19
RUN apk --no-cache add ca-certificates
COPY --from=builder /app/server /server
ENTRYPOINT ["/server"]
```

**Optimizatsiya nuqtalari:**
1. **Layer cache** — go.mod/go.sum alohida COPY → dependency o'zgarmasa qayta yuklanmaydi
2. **CGO_ENABLED=0** — static binary, C library kerak emas
3. **-trimpath** — lokal path larni olib tashlash
4. **-ldflags="-w -s"** — debug info yo'q, kichikroq binary
5. **alpine base** — minimal runtime image (~5MB vs ~1GB golang image)
6. **Multi-stage** — final image da Go toolchain yo'q
</details>

### 2.5. CI/CD da Go muhitni qanday sozlaysiz? Caching strategiyasi qanday?

<details>
<summary>Javob</summary>

```yaml
# GitHub Actions
- uses: actions/setup-go@v5
  with:
    go-version: '1.23'
    cache: true                    # Module + build cache
    cache-dependency-path: '**/go.sum'

- name: Verify
  run: |
    go mod verify                  # Integrity check
    go mod tidy                    # Tidy check
    git diff --exit-code go.mod go.sum  # Uncommitted changes?

- name: Test
  run: go test -race -coverprofile=coverage.out ./...
```

**Caching strategiyasi:**
1. **Module cache** — `GOMODCACHE` (go.sum ga asoslangan)
2. **Build cache** — `GOCACHE` (kompilyatsiya natijalari)
3. `actions/setup-go` ikkalasini avtomatik cache qiladi
4. Docker da: `--mount=type=cache` (BuildKit)
5. GitLab da: `cache:` directive

**Muhim CI checks:**
- `go mod tidy && git diff --exit-code` — go.mod to'g'riligi
- `go mod verify` — dependency integrity
- `go vet ./...` — static analysis
- `golangci-lint run` — linting
</details>

### 2.6. `go get` va `go install` o'rtasidagi farqni tushuntiring. Qachon qaysi birini ishlatish kerak?

<details>
<summary>Javob</summary>

| Buyruq | Maqsad | go.mod ga ta'siri |
|--------|--------|-------------------|
| `go get pkg@ver` | Dependency boshqarish | go.mod ni O'ZGARTIRADI |
| `go install pkg@ver` | Binary o'rnatish | go.mod ni O'ZGARTIRMAYDI |

```bash
# go get — dependency qo'shish/yangilash/o'chirish
go get github.com/gin-gonic/gin@v1.9.1    # Qo'shish
go get github.com/gin-gonic/gin@v1.10.0   # Yangilash
go get github.com/gin-gonic/gin@none      # O'chirish

# go install — binary tool o'rnatish
go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest
# Binary → $GOPATH/bin/golangci-lint
```

**Go 1.17+ dan beri:**
- `go get` binary o'rnatish uchun deprecated
- Binary uchun doimo `go install pkg@version` ishlating
</details>

---

## 3. Senior Level

### 3.1. Multi-module monorepo da Go muhitni qanday boshqarasiz? Versiya chiqarish strategiyasi qanday?

<details>
<summary>Javob</summary>

**Strukturasi:**
```
monorepo/
├── go.work              # Lokal dev (.gitignore da)
├── libs/
│   ├── auth/go.mod      # github.com/company/monorepo/libs/auth
│   └── logger/go.mod    # github.com/company/monorepo/libs/logger
├── services/
│   ├── api/go.mod       # github.com/company/monorepo/services/api
│   └── worker/go.mod    # github.com/company/monorepo/services/worker
└── Makefile             # Orchestration
```

**Versiya chiqarish:**
```bash
# Har bir modul mustaqil versiyalanadi
git tag libs/auth/v1.2.0
git push origin libs/auth/v1.2.0

# Boshqa modul yangi versiyani ishlatish:
cd services/api
go get github.com/company/monorepo/libs/auth@v1.2.0
```

**CI strategiyasi:**
1. O'zgargan fayllardan affected modullarni aniqlash (`git diff`)
2. Faqat affected modullarni test/build qilish
3. `GOWORK=off` — har bir modul mustaqil build bo'lishi kerak
4. Matrix strategy — parallel build

**Muammolar va yechimlar:**
- Tag collision → unique modul nomlari
- Cross-module change → go.work (dev), per-module update (CI)
- Dependency sync → `go work sync`
</details>

### 3.2. GOTOOLCHAIN strategiyasini jamoa uchun qanday tanlaysiz?

<details>
<summary>Javob</summary>

**Variantlar:**

| Strategiya | Afzalligi | Kamchiligi | Qachon |
|-----------|-----------|-----------|--------|
| `auto` | Avtomatik versiya | Download vaqti | Development |
| `local` | Predictable | Manual update | CI/CD |
| `goX.Y.Z` | Aniq versiya | Flexibility yo'q | Strict compliance |

**Tavsiya etilgan yondashuv:**
1. **go.mod da toolchain pin:** `toolchain go1.23.4`
2. **Development:** `GOTOOLCHAIN=auto` — avtomatik yuklash
3. **CI/CD:** `GOTOOLCHAIN=local` + `setup-go` action — aniq versiya
4. **Docker:** `FROM golang:1.23.4` + `ENV GOTOOLCHAIN=local`

```bash
# Jamoa bo'yicha:
# 1. go.mod da pin
go mod edit -toolchain=go1.23.4

# 2. .go-version fayl (goenv, asdf uchun)
echo "1.23.4" > .go-version

# 3. CI da tekshirish
MOD_VER=$(grep 'toolchain' go.mod | awk '{print $2}')
DOCKER_VER=$(grep 'FROM golang:' Dockerfile | head -1 | ...)
# Agar mos kelmasa → CI fail
```
</details>

### 3.3. Air-gapped muhitda Go muhitni qanday sozlaysiz?

<details>
<summary>Javob</summary>

**3 ta yondashuv:**

**1. go mod vendor (eng oddiy):**
```bash
# Internet bor muhitda:
go mod vendor
tar czf deps.tar.gz vendor/ go.mod go.sum

# Air-gapped muhitda:
tar xzf deps.tar.gz
go build -mod=vendor ./...
```

**2. Module cache export (ko'p loyiha uchun):**
```bash
# Internet bor:
go mod download
tar czf modcache.tar.gz -C $(go env GOMODCACHE) .

# Air-gapped:
tar xzf modcache.tar.gz -C $(go env GOMODCACHE)
GOPROXY=file://${GOMODCACHE}/cache/download,off go build ./...
```

**3. DMZ da Athens proxy (enterprise):**
```bash
# DMZ da Athens o'rnatish (internet bilan)
# Internal tarmoqda Athens ni GOPROXY sifatida ishlatish
go env -w GOPROXY=http://athens.dmz:3000,off
```

**Tavsiya:** Kichik team → vendor. Katta team → Athens. Compliance → vendor + audit.
</details>

### 3.4. Custom GOPROXY (Athens yoki Artifactory) ni qachon va nima uchun o'rnatish kerak?

<details>
<summary>Javob</summary>

**Qachon kerak:**
- 50+ dasturchi — module download traffic optimallashtirish
- Private modullar — authentication bilan proxy
- Security compliance — dependency audit trail
- High availability — upstream down bo'lsa ham ishlashi kerak
- Air-gapped muhit — internet yo'q

**Athens vs Artifactory:**

| Jihat | Athens | Artifactory |
|-------|--------|-------------|
| Narx | Open source (bepul) | Enterprise litsenziya |
| Storage | Disk, S3, GCS, Azure Blob | Ichki |
| Private modules | .netrc bilan | Built-in auth |
| Scanning | Yo'q | Built-in vuln scanning |
| Audit | Basic logging | Enterprise audit trail |
| O'rnatish | Docker compose | Enterprise setup |

**Tavsiya:**
- Startup/SMB → Athens + S3 (bepul, yetarli)
- Enterprise → Artifactory (compliance, scanning)
- Google-scale → Custom solution (Bazel)
</details>

### 3.5. Reproducible builds strategiyasini tushuntiring. Qanday qilib bir xil source dan bir xil binary hosil qilasiz?

<details>
<summary>Javob</summary>

**Kerakli shartlar:**
1. **Bir xil Go versiyasi** — `toolchain go1.23.4` go.mod da
2. **Bir xil dependency** — `go.sum` commit qilingan
3. **-trimpath** — lokal path larni olib tashlash
4. **CGO_ENABLED=0** — C compiler bog'liqligini yo'q qilish
5. **Static ldflags** — dynamic data (timestamp) yo'q

```bash
# Reproducible build:
CGO_ENABLED=0 go build -trimpath -ldflags="-w -s" -o server ./cmd/server

# Tekshirish:
sha256sum server  # Ikki marta build → bir xil hash

# Nima buzadi:
# 1. -trimpath yo'q → /home/user/... binary da
# 2. CGO_ENABLED=1 → C compiler versiyasiga bog'liq
# 3. -X main.date=$(date) → har safar boshqa
# 4. Go versiyasi farqli → boshqa binary
```

**runtime/debug.ReadBuildInfo()** — binary dagi build info:
```go
info, _ := debug.ReadBuildInfo()
// GoVersion, VCS revision, VCS time, etc.
```
</details>

### 3.6. Dependency vulnerability management strategiyangiz qanday?

<details>
<summary>Javob</summary>

**Multi-layered approach:**

1. **Avtomatik scanning (CI):**
```bash
govulncheck ./...              # Go rasmiy tool
```

2. **Scheduled scanning (haftalik):**
```yaml
# GitHub Actions cron job
on:
  schedule:
    - cron: '0 6 * * 1'  # Har dushanba
```

3. **Dependabot/Renovate** — avtomatik PR yaratish
4. **License checking:**
```bash
go-licenses check ./... --allowed_licenses=MIT,Apache-2.0,BSD-3-Clause
```

5. **Private proxy scanning** — Artifactory da built-in Xray

6. **Policy:**
- Critical/High vuln → 24 soat ichida fix
- Medium → sprint ichida
- Low → keyingi release da
</details>

---

## 4. Scenario-Based

### 4.1. Yangi jamoa a'zosi keldi. Go development muhitni noldan sozlash kerak. Qanday qadamlar bo'ladi?

<details>
<summary>Javob</summary>

**Setup script (setup-dev.sh):**

```bash
#!/bin/bash
set -euo pipefail

# 1. Go o'rnatish tekshirish
go version || { echo "Go o'rnating: https://go.dev/dl/"; exit 1; }

# 2. Environment sozlash
go env -w GOPRIVATE=github.com/company/*
go env -w GOPROXY=http://athens.internal:3000,https://proxy.golang.org,direct
go env -w GOTOOLCHAIN=auto

# 3. Git sozlash (private repos)
git config --global url."git@github.com:company/".insteadOf "https://github.com/company/"

# 4. Tools o'rnatish
go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest
go install github.com/air-verse/air@latest
go install golang.org/x/vuln/cmd/govulncheck@latest
go install golang.org/x/tools/gopls@latest

# 5. VS Code extensions
code --install-extension golang.go

# 6. Repo clone va dependency download
git clone git@github.com:company/monorepo.git
cd monorepo
go work init $(find . -name 'go.mod' -exec dirname {} \;)
go mod download

# 7. Pre-commit hooks
git config core.hooksPath .githooks

echo "Setup complete!"
```

Bu script ni repo dagi `CONTRIBUTING.md` da hujjatlash kerak.
</details>

### 4.2. CI/CD da build 10 daqiqa davom etadi. Qanday qilib 2 daqiqaga tushirasiz?

<details>
<summary>Javob</summary>

**Optimizatsiya qadamlari:**

1. **Cache yoqish:**
```yaml
- uses: actions/setup-go@v5
  with:
    cache: true  # Module + build cache
```

2. **Faqat o'zgargan modullarni build:**
```bash
CHANGED=$(git diff --name-only HEAD~1 | ...)
# Faqat affected modullarni test/build
```

3. **Parallel jobs:**
```yaml
strategy:
  matrix:
    module: [services/api, services/worker, libs/auth]
```

4. **Docker BuildKit cache:**
```dockerfile
RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    go build ./...
```

5. **Test parallelism:**
```bash
go test -parallel 8 -count=1 ./...
```

6. **Lint va test parallel:**
```yaml
jobs:
  lint:    # Parallel
  test:    # Parallel
  build:
    needs: [lint, test]  # Faqat ikkalasi muvaffaqiyatli bo'lganda
```

Natija: 10 min → 2-3 min.
</details>

### 4.3. Production da `go mod verify` xato berdi: "checksum mismatch". Qanday react qilasiz?

<details>
<summary>Javob</summary>

**Bu jiddiy xavfsizlik muammosi bo'lishi mumkin!**

**Darhol qadamlar:**
1. **Panic qilmang, lekin jiddiy yondasking**
2. **Qaysi dependency ta'sirlangan — aniqlang**
```bash
go mod verify 2>&1 | grep "FAILED"
# github.com/some/pkg v1.0.0: checksum mismatch
```

3. **Sabab aniqlash:**
- Module cache corrupt bo'lgan bo'lishi mumkin
- Haqiqiy supply chain attack
- go.sum commit qilinmagan/eskirgan

4. **Tekshirish:**
```bash
# Module cache tozalash va qayta yuklash
go clean -modcache
go mod download
go mod verify  # Yana tekshirish
```

5. **Agar yana xato bersa:**
```bash
# go.sum dan problematic hash o'chirish
# go mod tidy → qayta hash olish
go mod tidy
go mod verify
```

6. **Agar sum.golang.org dan farqli hash kelsa:**
- Bu **haqiqiy supply chain attack** belgisi
- Dependency ni ishlatishni to'xtating
- Security jamoaga xabar bering
- Dependency alternativasini qidiring

7. **Post-incident:**
- `govulncheck` ishga tushiring
- Audit log tekshiring
- Dependency review jarayonini kuchaytiring
</details>

### 4.4. Jamoa 5 ta microservice da ishlaydi. Har birida turli Go versiya. Qanday standartlashtirasiz?

<details>
<summary>Javob</summary>

**Qadamlar:**

1. **Yagona Go versiya tanlash:**
```bash
# Eng yangi stable versiya
# Barcha service lar uchun: go 1.23.4
```

2. **go.mod da pin qilish:**
```go
// Har bir service/go.mod:
go 1.23.4
toolchain go1.23.4
```

3. **GOTOOLCHAIN=auto:**
```bash
# Barcha dasturchilar:
go env -w GOTOOLCHAIN=auto
# Avtomatik to'g'ri versiyani yuklaydi
```

4. **.go-version fayl:**
```
1.23.4
```

5. **CI da enforce:**
```yaml
- uses: actions/setup-go@v5
  with:
    go-version-file: '.go-version'
```

6. **Docker da:**
```dockerfile
FROM golang:1.23.4-alpine
ENV GOTOOLCHAIN=local
```

7. **Versiya yangilash policy:**
- Har 3 oyda yangi Go versiyaga o'tish
- Bitta PR da barcha service lar yangilanadi
- Automated testing pipeline orqali validate
</details>

### 4.5. Private module yaratish kerak. GOPROXY, authentication va CI ni qanday sozlaysiz?

<details>
<summary>Javob</summary>

**1. Module yaratish:**
```bash
# Private repo da:
go mod init github.com/company/private-lib
# Kod yozing, tag chiqaring:
git tag v1.0.0 && git push origin v1.0.0
```

**2. GOPRIVATE sozlash (barcha dasturchilar):**
```bash
go env -w GOPRIVATE=github.com/company/*
```

**3. Git authentication:**

SSH (dasturchilar uchun):
```bash
git config --global url."git@github.com:company/".insteadOf "https://github.com/company/"
```

Token (CI uchun):
```bash
# .netrc
echo "machine github.com login oauth2 password ${GITHUB_TOKEN}" > ~/.netrc
chmod 600 ~/.netrc
```

**4. CI/CD:**
```yaml
# GitHub Actions
env:
  GOPRIVATE: github.com/company/*
steps:
  - uses: actions/checkout@v4
    with:
      token: ${{ secrets.GO_PRIVATE_TOKEN }}
  - run: |
      git config --global url."https://oauth2:${TOKEN}@github.com/company/".insteadOf "https://github.com/company/"
    env:
      TOKEN: ${{ secrets.GO_PRIVATE_TOKEN }}
```

**5. Private proxy (ixtiyoriy):**
```bash
# Athens + .netrc bilan private module cache
go env -w GOPROXY=http://athens.internal:3000,direct
```
</details>

---

## 5. FAQ

### 5.1. Go versiyasini yangilagandan keyin loyiha build bo'lmay qoldi. Nima qilish kerak?

<details>
<summary>Javob</summary>

**Diagnostika:**
```bash
go version                    # Yangi versiya tekshirish
go build ./... 2>&1           # Xato xabarini o'qish
go mod tidy                   # Dependency yangilash
go clean -cache               # Build cache tozalash
go build -v ./...             # Verbose build
```

**Eng ko'p sabablar:**
1. **Deprecated API** — yangi Go da eski API o'chirilgan → kodni yangilang
2. **Dependency mos kelmayapti** — `go get -u ./...` bilan yangilang
3. **Build cache eskirgan** — `go clean -cache`
4. **CGO muammo** — C library versiyasi mos kelmayapti
5. **go.mod versiya** — `go mod edit -go=1.23.4`
</details>

### 5.2. `go mod tidy` ishga tushirganda dependency o'chirib yubordi. Nima uchun?

<details>
<summary>Javob</summary>

`go mod tidy` koddagi `import` larni tahlil qiladi. Agar hech qanday `.go` faylda import qilinmagan dependency bo'lsa — uni o'chiradi.

**Sabablar:**
1. Dependency faqat test faylda ishlatilgan, lekin test fayllar build tag bilan cheklangan
2. Dependency `//go:build ignore` bilan belgilangan faylda
3. Dependency haqiqatan ham ishlatilmayapti

**Yechim:**
```bash
# Tekshirish:
git diff go.mod  # Nima o'zgarganini ko'rish

# Agar kerak bo'lsa qaytarish:
go get github.com/needed/pkg@v1.0.0

# Tool dependency uchun (binary, build da ishlatilmaydi):
# tools.go pattern ishlating
```
</details>

### 5.3. GoLand va VS Code — qaysi biri yaxshiroq Go development uchun?

<details>
<summary>Javob</summary>

| Jihat | VS Code + Go ext | GoLand |
|-------|------------------|--------|
| Narx | Bepul | Pullik ($199/yil, shaxsiy) |
| Performance | Yengil (~300MB RAM) | Og'ir (~2GB RAM) |
| Refactoring | Yaxshi (gopls) | Ajoyib (built-in) |
| Debugging | dlv integration | Built-in (kuchli) |
| Database | Extension kerak | Built-in |
| Docker | Extension kerak | Built-in |
| Setup | Extension o'rnatish kerak | Ready out-of-box |
| Ecosystem | Barcha tillar uchun | Faqat Go (+ web) |

**Tavsiya:**
- **Yangi boshlovchi** → VS Code (bepul, community katta)
- **Professional Go dev** → GoLand (powerful refactoring, debugging)
- **Polyglot dev** → VS Code (barcha tillar bitta IDE da)
- **Enterprise** → GoLand (JetBrains litsenziya bilan)
</details>

### 5.4. `go env -w` bilan o'rnatilgan sozlamalar boshqa loyihalarga ham ta'sir qiladimi?

<details>
<summary>Javob</summary>

**Ha!** `go env -w` global sozlama — barcha Go loyihalarga ta'sir qiladi.

```bash
# go env -w qiymatlari saqlanadi:
# Linux:   ~/.config/go/env
# macOS:   ~/Library/Application Support/go/env
# Windows: %AppData%\go\env

# Bu fayl BARCHA go buyruqlari uchun o'qiladi
```

**Per-project sozlash uchun:**
1. Shell environment variable ishlating (loyiha Makefile da)
2. direnv ishlating (`.envrc` fayl)
3. go.mod dagi directive lar (go, toolchain)

```bash
# .envrc (direnv bilan)
export GOPROXY=http://special-proxy:3000,direct
export GOPRIVATE=github.com/special-company/*
```
</details>

### 5.5. `go clean -cache` va `go clean -modcache` ni qachon ishlatish kerak?

<details>
<summary>Javob</summary>

```bash
# go clean -cache — build cache tozalash
# Qachon: build muammolari, eskirgan cache, disk joy bo'shatish
# Xavfsiz: Ha, keyingi build shunchaki sekinroq
# Hajm: odatda 1-5 GB

go clean -cache
du -sh $(go env GOCACHE)  # 0B

# go clean -modcache — module cache tozalash
# Qachon: jiddiy dependency muammo, corrupted cache
# Xavfsiz: Ha, lekin internet kerak (qayta yuklash uchun)
# Hajm: odatda 5-20 GB

go clean -modcache
du -sh $(go env GOMODCACHE)  # 0B
```

**Tavsiya:**
- `-cache` — oyda bir marta yoki muammo bo'lganda
- `-modcache` — faqat jiddiy muammo bo'lganda
- CI da — har safar emas, cache ishlatish yaxshiroq
</details>
