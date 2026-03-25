# Go Command — Practical Tasks

## Mundarija (Table of Contents)

1. [Junior Tasks (4 ta)](#1-junior-tasks-4-ta)
2. [Middle Tasks (3 ta)](#2-middle-tasks-3-ta)
3. [Senior Tasks (3 ta)](#3-senior-tasks-3-ta)
4. [Questions (10 ta)](#4-questions-10-ta)
5. [Mini Projects (2 ta)](#5-mini-projects-2-ta)
6. [Challenge (1 ta)](#6-challenge-1-ta)

---

## 1. Junior Tasks (4 ta)

### Task 1: Build, Run va Install

**Maqsad:** `go run`, `go build`, `go install` buyruqlarini amalda o'rganish.

**Vazifa:**

1. Yangi loyiha yarating:

```bash
mkdir -p ~/go-tasks/task1 && cd ~/go-tasks/task1
go mod init task1
```

2. Quyidagi dasturni yarating:

```go
// main.go
package main

import (
	"fmt"
	"os"
	"runtime"
	"time"
)

func main() {
	fmt.Println("=== System Info ===")
	fmt.Printf("OS:       %s\n", runtime.GOOS)
	fmt.Printf("Arch:     %s\n", runtime.GOARCH)
	fmt.Printf("CPUs:     %d\n", runtime.NumCPU())
	fmt.Printf("Go:       %s\n", runtime.Version())
	fmt.Printf("PID:      %d\n", os.Getpid())
	fmt.Printf("Time:     %s\n", time.Now().Format("2006-01-02 15:04:05"))

	if len(os.Args) > 1 {
		fmt.Printf("Args:     %v\n", os.Args[1:])
	}
}
```

3. Quyidagi buyruqlarni bajaring va natijalarni yozing:

```bash
# 1. go run bilan ishga tushiring
$ go run main.go

# 2. Argument bilan ishga tushiring
$ go run main.go hello world

# 3. Binary yarating
$ go build -o sysinfo .

# 4. Binary hajmini tekshiring
$ ls -lh sysinfo

# 5. Binary'ni ishga tushiring
$ ./sysinfo

# 6. go install bilan o'rnating
$ go install .

# 7. Go install qayerga o'rnatganini tekshiring
$ which task1
```

**Kutilgan natija:**
- Har bir buyruq muvaffaqiyatli ishlashi
- `go install` binary'ni `$GOPATH/bin` ga o'rnatishi
- Binary hajmi taxminan 1.5-2MB bo'lishi

---

### Task 2: Module va Dependency Management

**Maqsad:** `go mod init`, `go mod tidy`, `go get` buyruqlarini o'rganish.

**Vazifa:**

1. Yangi loyiha yarating:

```bash
mkdir -p ~/go-tasks/task2 && cd ~/go-tasks/task2
go mod init github.com/username/colorapp
```

2. Quyidagi dasturni yarating:

```go
// main.go
package main

import (
	"fmt"
	"os"

	"github.com/fatih/color"
)

func main() {
	if len(os.Args) < 2 {
		color.Yellow("Usage: colorapp <message>")
		os.Exit(1)
	}

	message := os.Args[1]

	color.Green("SUCCESS: %s", message)
	color.Red("ERROR: %s", message)
	color.Blue("INFO: %s", message)
	color.Yellow("WARNING: %s", message)
	fmt.Println("Normal:", message)
}
```

3. Quyidagi vazifalarni bajaring:

```bash
# 1. go mod tidy — dependency'larni yuklab olish
$ go mod tidy

# 2. go.mod ni tekshiring
$ cat go.mod

# 3. go.sum ni tekshiring (nechta qator?)
$ wc -l go.sum

# 4. Ishga tushiring
$ go run . "Hello Go!"

# 5. Dependency grafini ko'ring
$ go mod graph

# 6. Nima uchun bu dependency kerak?
$ go mod why github.com/mattn/go-isatty

# 7. Module integrity tekshirish
$ go mod verify
```

**Savol:** `go.sum` faylida nechta qator bor va nima uchun `go.mod` dan ko'proq?

---

### Task 3: Format, Vet va Test

**Maqsad:** `go fmt`, `go vet`, `go test` buyruqlarini o'rganish.

**Vazifa:**

1. Yangi loyiha yarating:

```bash
mkdir -p ~/go-tasks/task3 && cd ~/go-tasks/task3
go mod init calculator
```

2. Quyidagi fayllarni yarating:

```go
// calculator.go
package calculator

import "errors"

func Add(a, b float64) float64 {
return a+b
}

func Subtract(a,b float64) float64{
return a - b
}

func Multiply(a, b float64) float64 {
    return a * b
}

func Divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b,nil
}
```

```go
// calculator_test.go
package calculator

import "testing"

func TestAdd(t *testing.T) {
	tests := []struct {
		name     string
		a, b     float64
		expected float64
	}{
		{"positive", 2, 3, 5},
		{"negative", -1, -2, -3},
		{"zero", 0, 0, 0},
		{"mixed", -5, 10, 5},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := Add(tt.a, tt.b)
			if result != tt.expected {
				t.Errorf("Add(%v, %v) = %v; want %v", tt.a, tt.b, result, tt.expected)
			}
		})
	}
}

func TestDivide(t *testing.T) {
	t.Run("normal", func(t *testing.T) {
		result, err := Divide(10, 2)
		if err != nil {
			t.Fatalf("unexpected error: %v", err)
		}
		if result != 5 {
			t.Errorf("Divide(10, 2) = %v; want 5", result)
		}
	})

	t.Run("divide_by_zero", func(t *testing.T) {
		_, err := Divide(10, 0)
		if err == nil {
			t.Error("expected error for division by zero, got nil")
		}
	})
}

func TestSubtract(t *testing.T) {
	result := Subtract(10, 3)
	if result != 7 {
		t.Errorf("Subtract(10, 3) = %v; want 7", result)
	}
}

func TestMultiply(t *testing.T) {
	result := Multiply(4, 5)
	if result != 20 {
		t.Errorf("Multiply(4, 5) = %v; want 20", result)
	}
}
```

3. Quyidagi vazifalarni bajaring:

```bash
# 1. go fmt — formatlash
$ go fmt ./...
# Qaysi fayllar formatlandi?

# 2. go vet — xatolarni tekshirish
$ go vet ./...

# 3. go test — testlarni ishga tushirish
$ go test ./...

# 4. Verbose test
$ go test -v ./...

# 5. Faqat TestAdd ishga tushirish
$ go test -v -run TestAdd ./...

# 6. Coverage tekshirish
$ go test -cover ./...

# 7. Coverage HTML report
$ go test -coverprofile=coverage.out ./...
$ go tool cover -html=coverage.out -o coverage.html
# coverage.html ni browser'da oching
```

**Savol:** Coverage necha foiz? Qaysi funksiya test qilinmagan?

---

### Task 4: Go Doc va Go Env

**Maqsad:** `go doc`, `go version`, `go env` buyruqlarini o'rganish.

**Vazifa:**

```bash
# 1. Go versiyasini tekshiring
$ go version

# 2. fmt paket haqida ma'lumot
$ go doc fmt

# 3. fmt.Println haqida batafsil
$ go doc fmt.Println

# 4. strings.Contains haqida
$ go doc strings.Contains

# 5. sort paketi haqida
$ go doc sort

# 6. Muhim environment o'zgaruvchilarni yozing
$ go env GOPATH
$ go env GOROOT
$ go env GOOS
$ go env GOARCH
$ go env GOCACHE
$ go env GOMODCACHE
$ go env CGO_ENABLED

# 7. Barcha environment'ni ko'ring
$ go env

# 8. Cross-compilation uchun platformalar ro'yxati
$ go tool dist list | head -20

# 9. Build cache hajmi
$ du -sh $(go env GOCACHE)
```

**Yozing:** Har bir muhit o'zgaruvchisining qiymatini va ma'nosini qisqacha tushuntiring.

---

## 2. Middle Tasks (3 ta)

### Task 5: Build Flags va ldflags

**Maqsad:** Build flags'larni amalda qo'llash — version inject, race detection, build tags.

**Vazifa:**

1. Loyiha yarating:

```bash
mkdir -p ~/go-tasks/task5 && cd ~/go-tasks/task5
go mod init github.com/username/versionapp
```

2. Quyidagi fayllarni yarating:

```go
// main.go
package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"net/http"
	"os"
)

var (
	Version   = "dev"
	Commit    = "unknown"
	BuildTime = "unknown"
)

type VersionInfo struct {
	Version   string `json:"version"`
	Commit    string `json:"commit"`
	BuildTime string `json:"build_time"`
}

func versionHandler(w http.ResponseWriter, r *http.Request) {
	info := VersionInfo{
		Version:   Version,
		Commit:    Commit,
		BuildTime: BuildTime,
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(info)
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusOK)
	w.Write([]byte(`{"status":"ok"}`))
}

func main() {
	showVersion := flag.Bool("version", false, "Show version")
	port := flag.String("port", "8080", "Server port")
	flag.Parse()

	if *showVersion {
		fmt.Printf("Version:    %s\nCommit:     %s\nBuild Time: %s\n", Version, Commit, BuildTime)
		os.Exit(0)
	}

	http.HandleFunc("/version", versionHandler)
	http.HandleFunc("/health", healthHandler)

	fmt.Printf("Server v%s starting on :%s\n", Version, *port)
	if err := http.ListenAndServe(":"+*port, nil); err != nil {
		fmt.Fprintf(os.Stderr, "Server error: %v\n", err)
		os.Exit(1)
	}
}
```

3. Quyidagi vazifalarni bajaring:

```bash
# 1. Oddiy build
$ go build -o app .
$ ./app --version
# Version: dev — chunki inject qilmadik

# 2. Version inject bilan build
$ go build -ldflags "\
  -X main.Version=1.2.3 \
  -X main.Commit=$(git rev-parse --short HEAD 2>/dev/null || echo 'none') \
  -X 'main.BuildTime=$(date -u +%Y-%m-%dT%H:%M:%SZ)'" \
  -o app .
$ ./app --version
# Endi haqiqiy versiya ko'rinishi kerak

# 3. Binary hajmlarini solishtiring
$ go build -o app_default .
$ go build -ldflags="-s -w" -o app_stripped .
$ go build -trimpath -ldflags="-s -w" -o app_production .
$ ls -lh app_default app_stripped app_production
# Hajm farqlarini yozing

# 4. Race detection bilan test
$ go build -race -o app_race .
$ ls -lh app_race
# Race binary hajmini yozing

# 5. Server'ni ishga tushiring va tekshiring
$ ./app_production &
$ curl -s http://localhost:8080/version | python3 -m json.tool
$ curl -s http://localhost:8080/health
$ kill %1

# 6. Cross-compilation
$ GOOS=linux GOARCH=arm64 go build -o app-linux-arm64 .
$ GOOS=windows GOARCH=amd64 go build -o app.exe .
$ GOOS=darwin GOARCH=arm64 go build -o app-macos .
$ ls -lh app-linux-arm64 app.exe app-macos
$ file app-linux-arm64 app.exe app-macos
```

**Savol:**
1. `-s -w` flag'lari binary hajmini necha foizga kamaytirdi?
2. `-race` flag binary hajmini qanday o'zgartirdi?

---

### Task 6: Test Flags, Coverage va Benchmark

**Maqsad:** Ilg'or test buyruqlarini o'rganish — coverage, benchmark, profiling.

**Vazifa:**

1. Loyiha yarating:

```bash
mkdir -p ~/go-tasks/task6 && cd ~/go-tasks/task6
go mod init github.com/username/searchlib
```

2. Quyidagi fayllarni yarating:

```go
// search.go
package searchlib

// LinearSearch — O(n)
func LinearSearch(data []int, target int) int {
	for i, v := range data {
		if v == target {
			return i
		}
	}
	return -1
}

// BinarySearch — O(log n), data sorted bo'lishi kerak
func BinarySearch(data []int, target int) int {
	low, high := 0, len(data)-1
	for low <= high {
		mid := low + (high-low)/2
		if data[mid] == target {
			return mid
		} else if data[mid] < target {
			low = mid + 1
		} else {
			high = mid - 1
		}
	}
	return -1
}

// Contains — element mavjudligini tekshirish
func Contains(data []int, target int) bool {
	return LinearSearch(data, target) != -1
}

// UniqueCount — nechta unique element borligini hisoblash
func UniqueCount(data []int) int {
	seen := make(map[int]bool)
	for _, v := range data {
		seen[v] = true
	}
	return len(seen)
}
```

```go
// search_test.go
package searchlib

import "testing"

func TestLinearSearch(t *testing.T) {
	tests := []struct {
		name     string
		data     []int
		target   int
		expected int
	}{
		{"found_first", []int{1, 2, 3, 4, 5}, 1, 0},
		{"found_last", []int{1, 2, 3, 4, 5}, 5, 4},
		{"found_middle", []int{1, 2, 3, 4, 5}, 3, 2},
		{"not_found", []int{1, 2, 3, 4, 5}, 6, -1},
		{"empty_slice", []int{}, 1, -1},
		{"single_element", []int{42}, 42, 0},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := LinearSearch(tt.data, tt.target)
			if result != tt.expected {
				t.Errorf("LinearSearch(%v, %d) = %d; want %d",
					tt.data, tt.target, result, tt.expected)
			}
		})
	}
}

func TestBinarySearch(t *testing.T) {
	tests := []struct {
		name     string
		data     []int
		target   int
		expected int
	}{
		{"found_first", []int{1, 2, 3, 4, 5}, 1, 0},
		{"found_last", []int{1, 2, 3, 4, 5}, 5, 4},
		{"found_middle", []int{1, 2, 3, 4, 5}, 3, 2},
		{"not_found", []int{1, 2, 3, 4, 5}, 6, -1},
		{"empty_slice", []int{}, 1, -1},
		{"single_element", []int{42}, 42, 0},
		{"large_data", generateSorted(10000), 5000, 4999},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := BinarySearch(tt.data, tt.target)
			if result != tt.expected {
				t.Errorf("BinarySearch(%v..., %d) = %d; want %d",
					truncate(tt.data, 5), tt.target, result, tt.expected)
			}
		})
	}
}

func TestContains(t *testing.T) {
	data := []int{1, 2, 3, 4, 5}
	if !Contains(data, 3) {
		t.Error("Contains should return true for 3")
	}
	if Contains(data, 6) {
		t.Error("Contains should return false for 6")
	}
}

func TestUniqueCount(t *testing.T) {
	tests := []struct {
		name     string
		data     []int
		expected int
	}{
		{"all_unique", []int{1, 2, 3}, 3},
		{"duplicates", []int{1, 1, 2, 2, 3}, 3},
		{"empty", []int{}, 0},
		{"all_same", []int{5, 5, 5}, 1},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := UniqueCount(tt.data)
			if result != tt.expected {
				t.Errorf("UniqueCount(%v) = %d; want %d", tt.data, result, tt.expected)
			}
		})
	}
}

// === Benchmarks ===

func BenchmarkLinearSearch(b *testing.B) {
	data := generateSorted(10000)
	target := 9999 // worst case — oxirgi element

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		LinearSearch(data, target)
	}
}

func BenchmarkBinarySearch(b *testing.B) {
	data := generateSorted(10000)
	target := 9999

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		BinarySearch(data, target)
	}
}

func BenchmarkContains(b *testing.B) {
	data := generateSorted(10000)
	target := 5000

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		Contains(data, target)
	}
}

func BenchmarkUniqueCount(b *testing.B) {
	data := generateSorted(10000)

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		UniqueCount(data)
	}
}

// === Helpers ===

func generateSorted(n int) []int {
	data := make([]int, n)
	for i := range data {
		data[i] = i + 1
	}
	return data
}

func truncate(data []int, n int) []int {
	if len(data) <= n {
		return data
	}
	return data[:n]
}
```

3. Quyidagi vazifalarni bajaring:

```bash
# 1. Barcha testlarni verbose ishga tushiring
$ go test -v ./...

# 2. Faqat BinarySearch testlarini ishga tushiring
$ go test -v -run TestBinarySearch ./...

# 3. Coverage tekshiring
$ go test -cover ./...

# 4. Funksiya bo'yicha coverage
$ go test -coverprofile=coverage.out ./...
$ go tool cover -func=coverage.out

# 5. HTML coverage report
$ go tool cover -html=coverage.out -o coverage.html

# 6. Benchmark ishga tushiring
$ go test -bench=. -benchmem ./...

# 7. Faqat Search benchmark'larini ishga tushiring
$ go test -bench=Search -benchmem ./...

# 8. Benchmark'ni 3 marta ishga tushiring (barqarorlik uchun)
$ go test -bench=. -benchmem -count=3 ./...

# 9. CPU profiling
$ go test -cpuprofile=cpu.prof -bench=. ./...
$ go tool pprof -text cpu.prof | head -20

# 10. Race detection bilan test
$ go test -race -count=1 ./...

# 11. Test cache'ni tozalab qayta ishga tushiring
$ go clean -testcache
$ go test -v ./...
```

**Savollar:**
1. LinearSearch va BinarySearch benchmark natijalarini solishtiring. Necha marta farq bor?
2. BenchmarkUniqueCount da nechta memory allocation bo'lyapti? Nima uchun?
3. Coverage necha foiz?

---

### Task 7: go generate va Module Management

**Maqsad:** `go generate`, `go mod replace`, `go clean` buyruqlarini o'rganish.

**Vazifa:**

1. Loyiha yarating:

```bash
mkdir -p ~/go-tasks/task7 && cd ~/go-tasks/task7
go mod init github.com/username/enumapp
```

2. Quyidagi fayllarni yarating:

```go
// status.go
package main

import "fmt"

//go:generate stringer -type=Status
type Status int

const (
	StatusPending  Status = iota // 0
	StatusActive                 // 1
	StatusInactive               // 2
	StatusDeleted                // 3
)

//go:generate stringer -type=Priority
type Priority int

const (
	PriorityLow    Priority = iota // 0
	PriorityMedium                 // 1
	PriorityHigh                   // 2
	PriorityCritical               // 3
)

func main() {
	s := StatusActive
	p := PriorityHigh

	fmt.Printf("Status:   %s (value: %d)\n", s, s)
	fmt.Printf("Priority: %s (value: %d)\n", p, p)

	// Barcha status'larni ko'rsatish
	for i := StatusPending; i <= StatusDeleted; i++ {
		fmt.Printf("  %d: %s\n", i, i)
	}
}
```

3. Vazifalar:

```bash
# 1. stringer tool'ni o'rnatish
$ go install golang.org/x/tools/cmd/stringer@latest

# 2. go generate ishga tushirish
$ go generate ./...

# 3. Yaratilgan fayllarni tekshirish
$ ls *_string.go
$ cat status_string.go

# 4. Dasturni ishga tushirish
$ go run .

# 5. go clean buyruqlari
$ go clean -cache && echo "Build cache tozalandi"
$ go clean -testcache && echo "Test cache tozalandi"

# 6. Cache hajmini tekshirish
$ du -sh $(go env GOCACHE)
$ du -sh $(go env GOMODCACHE)

# 7. go mod edit bilan ishlash
$ go mod edit -json  # JSON formatda go.mod ko'rish
```

**Savol:** `go generate` va `go build` o'rtasidagi bog'lanish nima? `go build` avtomatik `go generate` ishga tushiradimi?

---

## 3. Senior Tasks (3 ta)

### Task 8: Production Build Pipeline

**Maqsad:** Enterprise-grade build pipeline yaratish — reproducible builds, multi-platform, security.

**Vazifa:**

1. Loyiha yarating:

```bash
mkdir -p ~/go-tasks/task8/{cmd/server,internal/version} && cd ~/go-tasks/task8
go mod init github.com/username/prodapp
```

2. Quyidagi fayllarni yarating:

```go
// internal/version/version.go
package version

import (
	"fmt"
	"runtime"
	"runtime/debug"
)

var (
	Version   = "dev"
	Commit    = "unknown"
	BuildTime = "unknown"
	GoVersion = runtime.Version()
)

func Info() string {
	return fmt.Sprintf(
		"Version:    %s\nCommit:     %s\nBuild Time: %s\nGo:         %s\nOS/Arch:    %s/%s",
		Version, Commit, BuildTime, GoVersion,
		runtime.GOOS, runtime.GOARCH,
	)
}

func ModuleInfo() string {
	info, ok := debug.ReadBuildInfo()
	if !ok {
		return "no build info available"
	}
	return info.String()
}
```

```go
// cmd/server/main.go
package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/username/prodapp/internal/version"
)

func main() {
	showVersion := flag.Bool("version", false, "Show version")
	showModules := flag.Bool("modules", false, "Show module info")
	port := flag.String("port", "8080", "Server port")
	flag.Parse()

	if *showVersion {
		fmt.Println(version.Info())
		os.Exit(0)
	}

	if *showModules {
		fmt.Println(version.ModuleInfo())
		os.Exit(0)
	}

	http.HandleFunc("/version", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(map[string]string{
			"version":    version.Version,
			"commit":     version.Commit,
			"build_time": version.BuildTime,
			"go_version": version.GoVersion,
		})
	})

	http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(map[string]string{"status": "ok"})
	})

	log.Printf("Server v%s starting on :%s", version.Version, *port)
	log.Fatal(http.ListenAndServe(":"+*port, nil))
}
```

3. **Makefile yarating** quyidagi target'lar bilan:

```
make build         — Production binary yaratish
make build-all     — Linux, macOS, Windows uchun binary
make build-debug   — Debug binary
make test          — Test + coverage
make lint          — go fmt + go vet
make verify        — lint + test + vulnerability check
make clean         — Build artifact'larni tozalash
make version       — Version info ko'rsatish
make size-compare  — Turli build flag'lari bilan binary hajmlarini solishtirish
```

4. **size-compare** target'da quyidagi binary'larni yaratib hajmlarini solishtiring:
- Default build
- `-ldflags="-s -w"` bilan
- `-trimpath -ldflags="-s -w"` bilan
- `CGO_ENABLED=0 -trimpath -ldflags="-s -w"` bilan

5. **Reproducible build tekshiruvi:**
- Bir xil flag'lar bilan ikki marta build qiling
- `sha256sum` bilan hash'larni solishtiring

**Natija:** To'liq Makefile va binary hajm solishtiruvi jadvalini yozing.

---

### Task 9: Coverage Strategy va Benchmark Regression

**Maqsad:** Strategic coverage analysis va benchmark regression testing.

**Vazifa:**

1. Loyiha yarating:

```bash
mkdir -p ~/go-tasks/task9/{internal/cache,internal/service} && cd ~/go-tasks/task9
go mod init github.com/username/cacheapp
```

2. Quyidagi fayllarni yarating:

```go
// internal/cache/cache.go
package cache

import (
	"sync"
	"time"
)

type entry struct {
	value     interface{}
	expiresAt time.Time
}

type Cache struct {
	mu      sync.RWMutex
	items   map[string]entry
	maxSize int
}

func New(maxSize int) *Cache {
	return &Cache{
		items:   make(map[string]entry),
		maxSize: maxSize,
	}
}

func (c *Cache) Set(key string, value interface{}, ttl time.Duration) {
	c.mu.Lock()
	defer c.mu.Unlock()

	// Evict if full
	if len(c.items) >= c.maxSize {
		c.evictOldest()
	}

	c.items[key] = entry{
		value:     value,
		expiresAt: time.Now().Add(ttl),
	}
}

func (c *Cache) Get(key string) (interface{}, bool) {
	c.mu.RLock()
	defer c.mu.RUnlock()

	item, found := c.items[key]
	if !found {
		return nil, false
	}

	if time.Now().After(item.expiresAt) {
		return nil, false
	}

	return item.value, true
}

func (c *Cache) Delete(key string) {
	c.mu.Lock()
	defer c.mu.Unlock()
	delete(c.items, key)
}

func (c *Cache) Len() int {
	c.mu.RLock()
	defer c.mu.RUnlock()
	return len(c.items)
}

func (c *Cache) Clear() {
	c.mu.Lock()
	defer c.mu.Unlock()
	c.items = make(map[string]entry)
}

func (c *Cache) evictOldest() {
	var oldestKey string
	var oldestTime time.Time

	for k, v := range c.items {
		if oldestKey == "" || v.expiresAt.Before(oldestTime) {
			oldestKey = k
			oldestTime = v.expiresAt
		}
	}

	if oldestKey != "" {
		delete(c.items, oldestKey)
	}
}
```

3. **Vazifalar:**

```bash
# 1. Cache uchun to'liq test faylini yarating (cache_test.go):
#    - TestSet, TestGet, TestDelete, TestLen, TestClear
#    - TestExpiration (TTL tugagandan keyin Get nil qaytarishi)
#    - TestEviction (maxSize ga yetganda oldest o'chirilishi)
#    - TestConcurrency (goroutine'lar bilan parallel Set/Get)

# 2. Benchmark yarating:
#    - BenchmarkSet
#    - BenchmarkGet
#    - BenchmarkConcurrentReadWrite

# 3. Quyidagi buyruqlarni bajaring:
$ go test -v ./...
$ go test -race -count=3 ./...
$ go test -cover -coverprofile=coverage.out ./...
$ go tool cover -func=coverage.out
$ go tool cover -html=coverage.out -o coverage.html

# 4. Benchmark natijalarni saqlang
$ go test -bench=. -benchmem -count=5 ./... > bench_v1.txt

# 5. Cache'ga optimization qiling (masalan, sync.Map ishlatish)
# Va yangi benchmark oling
$ go test -bench=. -benchmem -count=5 ./... > bench_v2.txt

# 6. Benchstat bilan solishtiring
$ go install golang.org/x/perf/cmd/benchstat@latest
$ benchstat bench_v1.txt bench_v2.txt
```

---

### Task 10: Custom Build Tags va Feature Flags

**Maqsad:** Build tags bilan enterprise feature management.

**Vazifa:**

1. Loyiha yarating:

```bash
mkdir -p ~/go-tasks/task10/{features,cmd/app} && cd ~/go-tasks/task10
go mod init github.com/username/featureapp
```

2. Quyidagi tuzilmani yarating:

```
task10/
├── features/
│   ├── registry.go          # Feature registry (har doim)
│   ├── community.go         # //go:build !enterprise
│   ├── enterprise.go        # //go:build enterprise
│   └── metrics.go           # //go:build metrics
├── cmd/app/
│   └── main.go
├── go.mod
└── Makefile
```

3. `registry.go` — feature'larni registratsiya qilish va tekshirish

4. `community.go` — `!enterprise` tag bilan, basic feature'lar

5. `enterprise.go` — `enterprise` tag bilan, premium feature'lar (SSO, audit-log, RBAC)

6. `metrics.go` — `metrics` tag bilan, Prometheus va OpenTelemetry

7. `main.go` — barcha feature'larni chop etadigan dastur

8. **Makefile:**
```
make community     — Community edition build
make enterprise    — Enterprise edition build
make full          — Enterprise + metrics build
make compare       — Barcha edition'lar hajmini solishtirish
```

9. **Vazifalar:**
```bash
$ make community && ./bin/app-community
# Faqat basic feature'lar ko'rinishi kerak

$ make enterprise && ./bin/app-enterprise
# Enterprise feature'lar ham ko'rinishi kerak

$ make full && ./bin/app-full
# Hammasi ko'rinishi kerak

$ make compare
# Binary hajmlarini solishtiring
```

---

## 4. Questions (10 ta)

### 4.1 `go run .` va `go run main.go` farqi nima?

<details>
<summary>Javob</summary>

`go run .` — joriy papkadagi **barcha** Go fayllarni compile qiladi.
`go run main.go` — faqat `main.go` faylni compile qiladi. Agar boshqa fayllardagi funksiyalar ishlatilsa, `undefined` xato beradi.

</details>

### 4.2 `go mod tidy` nima uchun `go.sum` ni ham o'zgartiradi?

<details>
<summary>Javob</summary>

`go.sum` barcha dependency'larning (shu jumladan transitive) checksum'larini saqlaydi. `go mod tidy` yangi dependency qo'shsa yoki eskisini o'chirsa, `go.sum` ham yangilanadi — integrity ta'minlash uchun.

</details>

### 4.3 `go vet` nimalarni topa oladi? Misol keltiring.

<details>
<summary>Javob</summary>

- Printf format xatolari: `fmt.Printf("%d", "string")`
- Mutex copy: `var m2 = m1` (mutex copy qilinmasligi kerak)
- Unreachable code: `return` dan keyin kod
- Struct tag xatolari: `` `json:"name" xml="name"` `` (xml da `=` emas `:`)
- Loop variable capture: goroutine'da loop variable ishlatish

</details>

### 4.4 `go test -cover` statement coverage o'lchaydi. Branch coverage qanday farq qiladi?

<details>
<summary>Javob</summary>

**Statement coverage** — qaysi qatorlar execute bo'lganini o'lchaydi. **Branch coverage** — if/else ning har bir branch'i execute bo'lganini o'lchaydi. Go default'da faqat statement coverage beradi. Masalan, `if err != nil { return err }` da faqat `err == nil` case test qilinsa, statement coverage 100% bo'lishi mumkin, lekin branch coverage 50%.

</details>

### 4.5 `go build -race` production'da nima uchun ishlatilmasligi kerak?

<details>
<summary>Javob</summary>

`-race` flag:
- Binary'ni 2-10x sekinlashtiradi (har bir memory access tekshiriladi)
- 5-10x ko'proq memory ishlatadi
- Binary hajmi 2-3x oshadi
Faqat development va CI/CD testlarida ishlating.

</details>

### 4.6 `go mod replace` directive'ning CI/CD da asosiy xavfi nima?

<details>
<summary>Javob</summary>

`replace github.com/pkg => ../local-pkg` — local path CI/CD server'da mavjud emas. Build fail bo'ladi. Shuning uchun CI da `grep "^replace" go.mod && exit 1` tekshiruvi qo'shish kerak.

</details>

### 4.7 `GOPROXY` nima va nima uchun kerak?

<details>
<summary>Javob</summary>

`GOPROXY` — module'larni yuklab olish uchun proxy server. Default: `https://proxy.golang.org,direct`. Afzalliklari: 1) Tezroq download (cache), 2) Module o'chirilsa ham proxy'da mavjud, 3) Checksum tekshiruvi. Private module uchun `GOPRIVATE` sozlash kerak.

</details>

### 4.8 `go generate` buyrug'i `go build` bilan birga ishlang deyilganda nima tushuniladi?

<details>
<summary>Javob</summary>

`go build` hech qachon `go generate` ni avtomatik ishga tushmaydi. Ular alohida buyruqlar. Tartib: 1) `go generate ./...` 2) `go build ./...`. CI da: `go generate ./... && git diff --exit-code` — generate natijasi commit qilinganligini tekshirish.

</details>

### 4.9 Go binary nima uchun boshqa tillardan kattaroq?

<details>
<summary>Javob</summary>

Go **static binary** yaratadi — barcha kerakli kutubxonalar (shu jumladan Go runtime, garbage collector, goroutine scheduler) binary ichiga kiritiladi. C/C++ dynamic linking ishlatadi — runtime library'lar tizimda bo'lishi kerak. Trade-off: Go binary katta, lekin mustaqil — hech qanday dependency kerak emas.

</details>

### 4.10 `CGO_ENABLED=0` va `CGO_ENABLED=1` o'rtasidagi asosiy farq nima?

<details>
<summary>Javob</summary>

`CGO_ENABLED=0`: Pure Go — static binary, Docker scratch'da ishlaydi, cross-compile oson. `CGO_ENABLED=1`: C library'lar bilan ishlash mumkin (SQLite, etc.), dynamic linking, cross-compile qiyin. Default: `1` (agar C compiler o'rnatilgan bo'lsa).

</details>

---

## 5. Mini Projects (2 ta)

### Mini Project 1: Go Build Analyzer

**Maqsad:** Go binary haqida ma'lumot beradigan CLI tool yarating.

**Funksionallik:**
1. Binary hajmini ko'rsatish
2. Binary'dagi Go version va module ma'lumotlarini ko'rsatish
3. Build flags'larni aniqlash (stripped, trimpath, race, CGO)
4. Dependency'lar ro'yxati

**Misol ishlatish:**

```bash
$ go run . analyze ./some-binary
=== Binary Analysis ===
File:       ./some-binary
Size:       4.8 MB
Go Version: go1.23.0
Module:     github.com/user/app
Stripped:   Yes (-s -w)
Trimpath:   Yes
CGO:        No

Dependencies (5):
  github.com/gin-gonic/gin         v1.9.1
  github.com/go-playground/validator v10.16.0
  golang.org/x/crypto              v0.16.0
  golang.org/x/net                 v0.19.0
  golang.org/x/sys                 v0.15.0
```

**Hint:** `debug.ReadBuildInfo()` va `os.Stat()` ishlatish.

---

### Mini Project 2: Go Project Initializer

**Maqsad:** Yangi Go loyiha tuzilmasini avtomatik yaratadigan CLI tool.

**Funksionallik:**
1. Loyiha papkasini yaratish (`cmd/`, `internal/`, `pkg/`)
2. `go mod init` ishga tushirish
3. `main.go`, `Makefile`, `.gitignore` yaratish
4. Template tanlash (cli, web-server, library)

**Misol ishlatish:**

```bash
$ go run . init myapp --template web-server --module github.com/user/myapp
Creating project: myapp
  mkdir myapp/cmd/server
  mkdir myapp/internal/handler
  mkdir myapp/internal/service
  mkdir myapp/internal/repository
  create myapp/cmd/server/main.go
  create myapp/Makefile
  create myapp/.gitignore
  create myapp/go.mod
  run: go mod init github.com/user/myapp
Done! cd myapp && make dev
```

---

## 6. Challenge (1 ta)

### Challenge: Go Toolchain Dashboard

**Maqsad:** Loyihangiz haqida to'liq diagnostika beradigan dashboard tool yarating.

**Funksionallik:**

1. **Build Info:**
   - `go version` natijasi
   - `GOOS`, `GOARCH`, `CGO_ENABLED`
   - Build cache hajmi va holati

2. **Module Info:**
   - `go.mod` tahlili (dependency soni, Go versiya)
   - `go.sum` integrity tekshiruvi (`go mod verify`)
   - Yangilanish mavjud dependency'lar (`go list -m -u all`)
   - Vulnerability scan (`govulncheck`)

3. **Code Quality:**
   - `go fmt` tekshiruvi (formatlangan yoki yo'q)
   - `go vet` natijasi
   - Test coverage foizi

4. **Build Analysis:**
   - Default binary hajmi
   - Stripped binary hajmi
   - Hajm farqi foizi
   - Build vaqti

**Output misol:**

```
╔══════════════════════════════════════════════╗
║         Go Project Dashboard                  ║
╠══════════════════════════════════════════════╣
║ Go Version:     go1.23.0                      ║
║ OS/Arch:        linux/amd64                   ║
║ CGO:            disabled                      ║
║ Build Cache:    245 MB                        ║
╠══════════════════════════════════════════════╣
║ Module:         github.com/user/myapp         ║
║ Dependencies:   23 (direct: 8, indirect: 15)  ║
║ Vulnerabilities: 0                            ║
║ Module Verify:  PASS                          ║
║ Updates:        3 available                   ║
╠══════════════════════════════════════════════╣
║ Format:         PASS (all formatted)          ║
║ Vet:            PASS (no issues)              ║
║ Test Coverage:  82.4%                         ║
║ Tests:          42 pass, 0 fail               ║
╠══════════════════════════════════════════════╣
║ Binary (default):  7.2 MB                     ║
║ Binary (stripped): 4.8 MB (-33%)              ║
║ Build Time:        2.3s                       ║
╚══════════════════════════════════════════════╝
```

**Hint:**
- `os/exec` paketi bilan `go` buyruqlarini ishga tushiring
- Natijalarni parse qilib chiroyli formatda chiqaring
- `tabwriter` yoki oddiy `fmt.Printf` bilan formatlash

**Bonus:**
- JSON output mode (`--json` flag)
- CI mode — xatolikda exit code 1
- HTML report generation
